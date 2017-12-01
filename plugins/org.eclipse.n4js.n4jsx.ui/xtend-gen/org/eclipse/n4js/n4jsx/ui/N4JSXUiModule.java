/**
 * Copyright (c) 2017 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.n4jsx.ui;

import com.google.inject.Binder;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Names;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.n4js.CancelIndicatorBaseExtractor;
import org.eclipse.n4js.N4JSRuntimeModule;
import org.eclipse.n4js.binaries.BinariesPreferenceStore;
import org.eclipse.n4js.binaries.OsgiBinariesPreferenceStore;
import org.eclipse.n4js.external.ExternalLibraryWorkspace;
import org.eclipse.n4js.external.GitCloneSupplier;
import org.eclipse.n4js.external.TargetPlatformInstallLocationProvider;
import org.eclipse.n4js.external.TypeDefinitionGitLocationProvider;
import org.eclipse.n4js.generator.common.CompilerDescriptor;
import org.eclipse.n4js.generator.common.IComposedGenerator;
import org.eclipse.n4js.generator.common.IGeneratorMarkerSupport;
import org.eclipse.n4js.generator.ui.GeneratorMarkerSupport;
import org.eclipse.n4js.n4jsx.ui.AbstractN4JSXUiModule;
import org.eclipse.n4js.n4jsx.ui.contentassist.CustomN4JSXParser;
import org.eclipse.n4js.n4jsx.ui.editor.syntaxcoloring.ParserBasedDocumentTokenSource;
import org.eclipse.n4js.n4jsx.ui.organize.imports.N4JSXReferencesFilter;
import org.eclipse.n4js.n4jsx.ui.search.N4JSXReferenceQueryExecutor;
import org.eclipse.n4js.preferences.ExternalLibraryPreferenceStore;
import org.eclipse.n4js.preferences.OsgiExternalLibraryPreferenceStore;
import org.eclipse.n4js.projectModel.IN4JSCore;
import org.eclipse.n4js.scoping.utils.CanLoadFromDescriptionHelper;
import org.eclipse.n4js.ts.findReferences.TargetURIKey;
import org.eclipse.n4js.ts.ui.search.BuiltinSchemeAwareTargetURIKey;
import org.eclipse.n4js.ui.N4JSEditor;
import org.eclipse.n4js.ui.N4JSEditorErrorTickUpdater;
import org.eclipse.n4js.ui.building.FileSystemAccessWithoutTraceFileSupport;
import org.eclipse.n4js.ui.building.N4JSBuilderParticipant;
import org.eclipse.n4js.ui.building.N4JSOutputConfigurationProvider;
import org.eclipse.n4js.ui.building.instructions.ComposedGeneratorRegistry;
import org.eclipse.n4js.ui.containers.N4JSAllContainersStateProvider;
import org.eclipse.n4js.ui.contentassist.ContentAssistantFactory;
import org.eclipse.n4js.ui.contentassist.N4JSFollowElementCalculator;
import org.eclipse.n4js.ui.contentassist.PatchedFollowElementComputer;
import org.eclipse.n4js.ui.contentassist.SimpleLastSegmentFinder;
import org.eclipse.n4js.ui.editor.AlwaysAddNatureCallback;
import org.eclipse.n4js.ui.editor.ComposedMemberAwareHyperlinkHelper;
import org.eclipse.n4js.ui.editor.EditorAwareCanLoadFromDescriptionHelper;
import org.eclipse.n4js.ui.editor.N4JSDirtyStateEditorSupport;
import org.eclipse.n4js.ui.editor.N4JSDocument;
import org.eclipse.n4js.ui.editor.N4JSDoubleClickStrategyProvider;
import org.eclipse.n4js.ui.editor.N4JSHover;
import org.eclipse.n4js.ui.editor.N4JSHyperlinkDetector;
import org.eclipse.n4js.ui.editor.N4JSLocationInFileProvider;
import org.eclipse.n4js.ui.editor.N4JSReconciler;
import org.eclipse.n4js.ui.editor.NFARAwareResourceForEditorInputFactory;
import org.eclipse.n4js.ui.editor.autoedit.AutoEditStrategyProvider;
import org.eclipse.n4js.ui.editor.syntaxcoloring.HighlightingConfiguration;
import org.eclipse.n4js.ui.editor.syntaxcoloring.InvalidatingHighlightingHelper;
import org.eclipse.n4js.ui.editor.syntaxcoloring.TemplateAwarePartitionTokenScanner;
import org.eclipse.n4js.ui.editor.syntaxcoloring.TemplateAwareTokenScanner;
import org.eclipse.n4js.ui.editor.syntaxcoloring.TokenToAttributeIdMapper;
import org.eclipse.n4js.ui.editor.syntaxcoloring.TokenTypeToPartitionMapper;
import org.eclipse.n4js.ui.formatting2.FixedContentFormatter;
import org.eclipse.n4js.ui.internal.ConsoleOutputStreamProvider;
import org.eclipse.n4js.ui.internal.EclipseBasedN4JSWorkspace;
import org.eclipse.n4js.ui.labeling.N4JSContentAssistLabelProvider;
import org.eclipse.n4js.ui.labeling.N4JSHoverProvider;
import org.eclipse.n4js.ui.labeling.N4JSHyperlinkLabelProvider;
import org.eclipse.n4js.ui.organize.imports.IReferenceFilter;
import org.eclipse.n4js.ui.outline.MetaTypeAwareComparator;
import org.eclipse.n4js.ui.outline.N4JSFilterLocalTypesOutlineContribution;
import org.eclipse.n4js.ui.outline.N4JSFilterNonPublicMembersOutlineContribution;
import org.eclipse.n4js.ui.outline.N4JSFilterStaticMembersOutlineContribution;
import org.eclipse.n4js.ui.outline.N4JSOutlineModes;
import org.eclipse.n4js.ui.outline.N4JSOutlineNodeFactory;
import org.eclipse.n4js.ui.outline.N4JSShowInheritedMembersOutlineContribution;
import org.eclipse.n4js.ui.preferences.N4JSBuilderPreferenceAccess;
import org.eclipse.n4js.ui.projectModel.IN4JSEclipseCore;
import org.eclipse.n4js.ui.quickfix.N4JSIssue;
import org.eclipse.n4js.ui.quickfix.N4JSMarkerResolutionGenerator;
import org.eclipse.n4js.ui.resource.OutputFolderAwareResourceServiceProvider;
import org.eclipse.n4js.ui.search.LabellingReferenceFinder;
import org.eclipse.n4js.ui.search.N4JSEditorResourceAccess;
import org.eclipse.n4js.ui.utils.CancelIndicatorUiExtractor;
import org.eclipse.n4js.ui.validation.ManifestAwareResourceValidator;
import org.eclipse.n4js.ui.workingsets.WorkingSetManagerBroker;
import org.eclipse.n4js.ui.workingsets.WorkingSetManagerBrokerImpl;
import org.eclipse.n4js.utils.process.OutputStreamProvider;
import org.eclipse.n4js.utils.ui.editor.AvoidRefreshDocumentProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.xtend.lib.annotations.FinalFieldsConstructor;
import org.eclipse.xtext.builder.EclipseResourceFileSystemAccess2;
import org.eclipse.xtext.builder.IXtextBuilderParticipant;
import org.eclipse.xtext.builder.preferences.BuilderPreferenceAccess;
import org.eclipse.xtext.generator.IFileSystemAccess;
import org.eclipse.xtext.generator.IGenerator;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.ide.editor.contentassist.antlr.ContentAssistContextFactory;
import org.eclipse.xtext.ide.editor.contentassist.antlr.FollowElementCalculator;
import org.eclipse.xtext.ide.editor.contentassist.antlr.FollowElementComputer;
import org.eclipse.xtext.ide.editor.contentassist.antlr.IContentAssistParser;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.SynchronizedXtextResourceSet;
import org.eclipse.xtext.resource.XtextResourceSet;
import org.eclipse.xtext.resource.containers.IAllContainersState;
import org.eclipse.xtext.ui.editor.DirtyStateEditorSupport;
import org.eclipse.xtext.ui.editor.IXtextEditorCallback;
import org.eclipse.xtext.ui.editor.XtextEditor;
import org.eclipse.xtext.ui.editor.autoedit.AbstractEditStrategyProvider;
import org.eclipse.xtext.ui.editor.contentassist.ContentProposalLabelProvider;
import org.eclipse.xtext.ui.editor.contentassist.FQNPrefixMatcher;
import org.eclipse.xtext.ui.editor.contentassist.IContentAssistantFactory;
import org.eclipse.xtext.ui.editor.contentassist.PrefixMatcher;
import org.eclipse.xtext.ui.editor.doubleClicking.DoubleClickStrategyProvider;
import org.eclipse.xtext.ui.editor.findrefs.EditorResourceAccess;
import org.eclipse.xtext.ui.editor.findrefs.IReferenceFinder;
import org.eclipse.xtext.ui.editor.findrefs.ReferenceQueryExecutor;
import org.eclipse.xtext.ui.editor.formatting2.ContentFormatter;
import org.eclipse.xtext.ui.editor.hover.IEObjectHover;
import org.eclipse.xtext.ui.editor.hover.IEObjectHoverProvider;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkHelper;
import org.eclipse.xtext.ui.editor.hyperlinking.HyperlinkLabelProvider;
import org.eclipse.xtext.ui.editor.model.DocumentTokenSource;
import org.eclipse.xtext.ui.editor.model.IResourceForEditorInputFactory;
import org.eclipse.xtext.ui.editor.model.TerminalsTokenTypeToPartitionMapper;
import org.eclipse.xtext.ui.editor.model.XtextDocument;
import org.eclipse.xtext.ui.editor.model.XtextDocumentProvider;
import org.eclipse.xtext.ui.editor.outline.IOutlineTreeProvider;
import org.eclipse.xtext.ui.editor.outline.actions.IOutlineContribution;
import org.eclipse.xtext.ui.editor.outline.impl.OutlineFilterAndSorter;
import org.eclipse.xtext.ui.editor.outline.impl.OutlineNodeFactory;
import org.eclipse.xtext.ui.editor.preferences.IPreferenceStoreInitializer;
import org.eclipse.xtext.ui.editor.quickfix.MarkerResolutionGenerator;
import org.eclipse.xtext.ui.editor.reconciler.XtextReconciler;
import org.eclipse.xtext.ui.editor.syntaxcoloring.AbstractAntlrTokenToAttributeIdMapper;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingHelper;
import org.eclipse.xtext.ui.resource.DefaultResourceUIServiceProvider;
import org.eclipse.xtext.ui.shared.Access;
import org.eclipse.xtext.ui.util.IssueUtil;
import org.eclipse.xtext.validation.IResourceValidator;

/**
 * Use this class to register components to be used within the Eclipse IDE.
 */
@FinalFieldsConstructor
@SuppressWarnings("all")
public class N4JSXUiModule extends AbstractN4JSXUiModule {
  @Override
  public void configure(final Binder binder) {
    super.configure(binder);
    this.configureIGenerator(binder);
  }
  
  @Override
  public Class<? extends IXtextBuilderParticipant> bindIXtextBuilderParticipant() {
    return N4JSBuilderParticipant.class;
  }
  
  /**
   * Re-binds the {@link Singleton @Singleton} {@link ExternalLibraryWorkspace N4JS external library workspace}
   * instance declared and created in the {@link N4JSRuntimeModule}.
   */
  public Provider<ExternalLibraryWorkspace> provideN4JSExternalLibraryWorkspace() {
    return Access.<ExternalLibraryWorkspace>contributedProvider(ExternalLibraryWorkspace.class);
  }
  
  /**
   * Re-binds the {@link GitCloneSupplier} to the singleton instance declared in the contribution module.
   */
  public Provider<GitCloneSupplier> provideGitCloneSupplier() {
    return Access.<GitCloneSupplier>contributedProvider(GitCloneSupplier.class);
  }
  
  @Override
  public Provider<IAllContainersState> provideIAllContainersState() {
    return new N4JSAllContainersStateProvider();
  }
  
  /**
   * A custom {@link DirtyStateEditorSupport} that uses a custom JobFamily.
   */
  public Class<? extends DirtyStateEditorSupport> bindDirtyStateEditorSupport() {
    return N4JSDirtyStateEditorSupport.class;
  }
  
  /**
   * Bind the {@link ILocationInFileProvider} that is aware of derived elements.
   */
  public Class<? extends ILocationInFileProvider> bindILocationInFileProvider() {
    return N4JSLocationInFileProvider.class;
  }
  
  /**
   * Bind the {@link ReferenceQueryExecutor} that maps to types.
   */
  public Class<? extends ReferenceQueryExecutor> bindReferenceQueryExecutor() {
    return N4JSXReferenceQueryExecutor.class;
  }
  
  /**
   * Bind the {@link IResourceValidator} that knows how to ignore files that are not in source folders according to
   * the manifest.
   */
  public Class<? extends IResourceValidator> bindResourceValidator() {
    return ManifestAwareResourceValidator.class;
  }
  
  /**
   * De-configure the hard coded built in scheme from the runtime bundle.
   */
  public Class<? extends XtextResourceSet> bindXtextResourceSet() {
    return SynchronizedXtextResourceSet.class;
  }
  
  /**
   * Configure the IN4JSCore instance to use the implementation that is backed by the Eclipse workspace.
   */
  public Class<? extends IN4JSCore> bindIN4JSCore() {
    return IN4JSEclipseCore.class;
  }
  
  /**
   * Binds the external library preference store to use the {@link OsgiExternalLibraryPreferenceStore OSGi} one. This
   * provider binding is required to share the same singleton instance between modules, hence injectors.
   */
  public Provider<ExternalLibraryPreferenceStore> provideExternalLibraryPreferenceStore() {
    return Access.<ExternalLibraryPreferenceStore>contributedProvider(ExternalLibraryPreferenceStore.class);
  }
  
  /**
   * Binds the broker for the working set managers in a singleton scope.
   */
  public Provider<WorkingSetManagerBroker> provideWorkingSetManagerBroker() {
    return Access.<WorkingSetManagerBroker>contributedProvider(WorkingSetManagerBroker.class);
  }
  
  /**
   * Binds the broker implementation for the working set managers in a singleton scope.
   */
  public Provider<WorkingSetManagerBrokerImpl> provideWorkingSetManagerBrokerImpl() {
    return Access.<WorkingSetManagerBrokerImpl>contributedProvider(WorkingSetManagerBrokerImpl.class);
  }
  
  /**
   * Binds the binaries preference store to use the {@link OsgiBinariesPreferenceStore} one. This provider binding is
   * required to share the same singleton instance between modules, hence injectors.
   */
  public Provider<BinariesPreferenceStore> provideBinariesPreferenceStore() {
    return Access.<BinariesPreferenceStore>contributedProvider(BinariesPreferenceStore.class);
  }
  
  /**
   * Binds the target platform location provider to the Eclipse based implementation. This requires a running
   * {@link Platform platform} and an existing and available {@link IWorkspace workspace}.
   */
  public Provider<TargetPlatformInstallLocationProvider> provideTargetPlatformInstallLocationProvider() {
    return Access.<TargetPlatformInstallLocationProvider>contributedProvider(TargetPlatformInstallLocationProvider.class);
  }
  
  /**
   * Binds the type definition Git location provider.
   */
  public Provider<TypeDefinitionGitLocationProvider> provideTypeDefinitionGitLocationProvider() {
    return Access.<TypeDefinitionGitLocationProvider>contributedProvider(TypeDefinitionGitLocationProvider.class);
  }
  
  /**
   * Configure the IN4JSCore instance to use the implementation that is backed by the Eclipse workspace.
   */
  public Provider<IN4JSEclipseCore> provideIN4JSEclipseCore() {
    return Access.<IN4JSEclipseCore>contributedProvider(IN4JSEclipseCore.class);
  }
  
  /**
   * Configure the IN4JSCore instance to use the implementation that is backed by the Eclipse workspace.
   */
  public Provider<EclipseBasedN4JSWorkspace> provideEclipseBasedN4JSWorkspace() {
    return Access.<EclipseBasedN4JSWorkspace>contributedProvider(EclipseBasedN4JSWorkspace.class);
  }
  
  @Override
  public Class<? extends IResourceForEditorInputFactory> bindIResourceForEditorInputFactory() {
    return NFARAwareResourceForEditorInputFactory.class;
  }
  
  /**
   * custom builder preferences initializer that iterates over all default configurations provided by the sub
   * generators contained in the found composite generators.
   */
  @Override
  public void configureBuilderPreferenceStoreInitializer(final Binder binder) {
    binder.<IPreferenceStoreInitializer>bind(IPreferenceStoreInitializer.class).annotatedWith(Names.named("builderPreferenceInitializer")).to(N4JSBuilderPreferenceAccess.Initializer.class);
  }
  
  /**
   * @return custom builder preference access sets the activation of the builder participant to be always true so that
   *         dirty state handling is always enabled.
   */
  public Class<? extends BuilderPreferenceAccess> bindBuilderPreferenceAccess() {
    return N4JSBuilderPreferenceAccess.class;
  }
  
  /**
   * @return iterates over all registered composite generators to pick up the default output configurations of their
   *         contained sub generators
   */
  public Class<? extends IOutputConfigurationProvider> bindIOutputConfigurationProvider() {
    return N4JSOutputConfigurationProvider.class;
  }
  
  /**
   * Filter fully qualified proposals also by their last segment and not only by their fully qualified name.
   */
  public Class<? extends PrefixMatcher> bindPrefixMatcher() {
    return FQNPrefixMatcher.class;
  }
  
  /**
   * No special treatment for uppercase module name segments.
   */
  public Class<? extends FQNPrefixMatcher.LastSegmentFinder> bindLastSegmentFinder() {
    return SimpleLastSegmentFinder.class;
  }
  
  /**
   * Binds the output stream provider to the console based one in the UI.
   */
  public Class<? extends OutputStreamProvider> bindOutputStreamProvider() {
    return ConsoleOutputStreamProvider.class;
  }
  
  /**
   * Bind the customized content assist parser infrastructure.
   */
  public Class<? extends ContentAssistContextFactory> bindContentAssistContextFactory() {
    return org.eclipse.n4js.n4jsx.ui.contentassist.ContentAssistContextFactory.class;
  }
  
  /**
   * Bind the customized content assist follow element calculator that drops parser rules of "bogus" language parts.
   */
  public Class<? extends FollowElementCalculator> bindFollowElementCalculator() {
    return N4JSFollowElementCalculator.class;
  }
  
  /**
   * Remove this binding once the change of https://github.com/eclipse/xtext-core/pull/167 is available in the target
   * platform.
   */
  public Class<? extends FollowElementComputer> bindFollowElementComputer() {
    return PatchedFollowElementComputer.class;
  }
  
  @Override
  public Class<? extends IContentAssistParser> bindIContentAssistParser() {
    return CustomN4JSXParser.class;
  }
  
  /**
   * Loads all registered composed generators via the extension point if there are some the first found composite
   * generator is registered as IGenerator (this binding is required internally by the Xtext builder participant) or
   * if there are no composite generators found, a dummy IComposedGenerator implementation is bound as IGenerator.
   * 
   * 
   * @param binder
   *            the Google guice binder
   */
  private void configureIGenerator(final Binder binder) {
    IComposedGenerator composedGenerator = null;
    final List<IComposedGenerator> composedGenerators = ComposedGeneratorRegistry.getComposedGenerators();
    boolean _isEmpty = composedGenerators.isEmpty();
    boolean _not = (!_isEmpty);
    if (_not) {
      composedGenerator = composedGenerators.get(0);
    } else {
      composedGenerator = new IComposedGenerator() {
        @Override
        public void doGenerate(final Resource input, final IFileSystemAccess fsa) {
        }
        
        @Override
        public Set<CompilerDescriptor> getCompilerDescriptors() {
          return new HashSet<CompilerDescriptor>();
        }
      };
    }
    binder.<IGenerator>bind(IGenerator.class).toInstance(composedGenerator);
  }
  
  /**
   * Binds a specific label provider for the content assist use case.
   */
  @Override
  public void configureContentProposalLabelProvider(final Binder binder) {
    binder.<ILabelProvider>bind(ILabelProvider.class).annotatedWith(ContentProposalLabelProvider.class).to(N4JSContentAssistLabelProvider.class);
  }
  
  /**
   * Binds a specific label provider for the hyper linking use case.
   */
  @Override
  public void configureHyperlinkLabelProvider(final Binder binder) {
    binder.<ILabelProvider>bind(ILabelProvider.class).annotatedWith(HyperlinkLabelProvider.class).to(N4JSHyperlinkLabelProvider.class);
  }
  
  /**
   * Bind custom MarkerResolutionGenerator.
   */
  public void configureMarkerResolutionGenerator(final Binder binder) {
    boolean _isWorkbenchRunning = PlatformUI.isWorkbenchRunning();
    if (_isWorkbenchRunning) {
      binder.<MarkerResolutionGenerator>bind(MarkerResolutionGenerator.class).to(N4JSMarkerResolutionGenerator.class);
    }
  }
  
  /**
   * Bind custom XtextEditor.
   */
  public Class<? extends XtextEditor> bindXtextEditor() {
    return N4JSEditor.class;
  }
  
  /**
   * Bind custom IssueUtil.
   */
  public Class<? extends IssueUtil> bindIssueUtil() {
    return N4JSIssue.Util.class;
  }
  
  /**
   * Bind custom IEObjectHoverProvider.
   */
  public Class<? extends IEObjectHoverProvider> bindIEObjectHoverProvider() {
    return N4JSHoverProvider.class;
  }
  
  /**
   * Bind a callback that always add the nature silently.
   */
  @Override
  public Class<? extends IXtextEditorCallback> bindIXtextEditorCallback() {
    return AlwaysAddNatureCallback.class;
  }
  
  /**
   * Bind an implementation can handle find references to builtin types.
   */
  public Class<? extends TargetURIKey> bindTargetURIKey() {
    return BuiltinSchemeAwareTargetURIKey.class;
  }
  
  /**
   * Bind a proper token mapper for the special token types in N4JS
   */
  public Class<? extends AbstractAntlrTokenToAttributeIdMapper> bindAbstractAntlrTokenToAttributeIdMapper() {
    return TokenToAttributeIdMapper.class;
  }
  
  /**
   * Bind a proper highlighting configuration
   */
  public Class<? extends IHighlightingConfiguration> bindIHighlightingConfiguration() {
    return HighlightingConfiguration.class;
  }
  
  /**
   * Configure the parser based token source for the coloring.
   */
  public Class<? extends DocumentTokenSource> bindDocumentTokenSource() {
    return ParserBasedDocumentTokenSource.class;
  }
  
  /**
   * Configure the token to partition type mapper.
   */
  public Class<? extends TerminalsTokenTypeToPartitionMapper> bindTerminalTokenTypeToPartitionMapper() {
    return TokenTypeToPartitionMapper.class;
  }
  
  @Override
  public Class<? extends IHighlightingHelper> bindIHighlightingHelper() {
    return InvalidatingHighlightingHelper.class;
  }
  
  @Override
  public Class<? extends ITokenScanner> bindITokenScanner() {
    return TemplateAwareTokenScanner.class;
  }
  
  @Override
  public Class<? extends AbstractEditStrategyProvider> bindAbstractEditStrategyProvider() {
    return AutoEditStrategyProvider.class;
  }
  
  @Override
  public Class<? extends IContentAssistantFactory> bindIContentAssistantFactory() {
    return ContentAssistantFactory.class;
  }
  
  @Override
  public Class<? extends IPartitionTokenScanner> bindIPartitionTokenScanner() {
    return TemplateAwarePartitionTokenScanner.class;
  }
  
  /**
   * Bind the double click strategy provider.
   */
  public Class<? extends DoubleClickStrategyProvider> bindDoubleClickStrategyProvider() {
    return N4JSDoubleClickStrategyProvider.class;
  }
  
  /**
   * Bind the mechanism to extract a cancel indicator (a "real" cancel indicator in IDE scenario, a "NullImpl" one in
   * the headless compiler).
   */
  public Class<? extends CancelIndicatorBaseExtractor> bindCancelIndicatorExtractor() {
    return CancelIndicatorUiExtractor.class;
  }
  
  /**
   * Avoid unnecessary overhead for trace file lookup.
   */
  public Class<? extends EclipseResourceFileSystemAccess2> bindEclipseResourceFileSystemAccess2() {
    return FileSystemAccessWithoutTraceFileSupport.class;
  }
  
  /**
   * Filter out output-folders from compilation-processing.
   */
  public Class<? extends DefaultResourceUIServiceProvider> bindResourceUIServiceProvider() {
    return OutputFolderAwareResourceServiceProvider.class;
  }
  
  /**
   * Generating markers.
   */
  public Class<? extends IGeneratorMarkerSupport> bindIGeneratorMarkerSupport() {
    return GeneratorMarkerSupport.class;
  }
  
  /**
   * Performance workaround, see https://github.com/NumberFour/n4js/issues/246
   */
  public Class<? extends ContentFormatter> bindContentFormatter() {
    return FixedContentFormatter.class;
  }
  
  /**
   * Languages variation point for the organize imports
   */
  public Class<? extends IReferenceFilter> bindContentReferenceFilter() {
    return N4JSXReferencesFilter.class;
  }
  
  /**
   * Bind the {@link IReferenceFinder} that find references solely to types (and
   * TVariables, IdentifiableElement and TEnumLiterals).
   */
  public Class<? extends IReferenceFinder> bindIReferenceFinder() {
    return LabellingReferenceFinder.class;
  }
  
  /**
   * CanLoadFromDescriptionHelper specific to the interactive editor scenario.
   */
  public Class<? extends CanLoadFromDescriptionHelper> bindCanLoadFromDescriptionHelper() {
    return EditorAwareCanLoadFromDescriptionHelper.class;
  }
  
  /**
   * Provide multiple hyperlink for composed members.
   */
  public Class<? extends HyperlinkHelper> bindHyperlinkHelper() {
    return ComposedMemberAwareHyperlinkHelper.class;
  }
  
  /**
   * Binds outline factory which creates special nodes to allow for inherited members to be filtered.
   */
  public Class<? extends OutlineNodeFactory> bindOutlineNodeFactory() {
    return N4JSOutlineNodeFactory.class;
  }
  
  /**
   * Outline modes for showing inherited members or not
   */
  public Class<? extends IOutlineTreeProvider.ModeAware> bindIOutlineTreeProvider_ModeAware() {
    return N4JSOutlineModes.class;
  }
  
  /**
   * Toggle showing inherited members or not.
   */
  public void configureInheritedMembersOutlineContribution(final Binder binder) {
    binder.<IOutlineContribution>bind(IOutlineContribution.class).annotatedWith(Names.named("InheritedMembersOutlineContribution")).to(
      N4JSShowInheritedMembersOutlineContribution.class);
  }
  
  /**
   * Toggle showing static members or not.
   */
  public void configureFilterStaticMembersOutlineContribution(final Binder binder) {
    binder.<IOutlineContribution>bind(IOutlineContribution.class).annotatedWith(Names.named("FilterStaticMembersOutlineContribution")).to(
      N4JSFilterStaticMembersOutlineContribution.class);
  }
  
  /**
   * Toggle showing non-public members or not.
   */
  public void configureFilterNonPublicMembersOutlineContribution(final Binder binder) {
    binder.<IOutlineContribution>bind(IOutlineContribution.class).annotatedWith(Names.named("FilterNonPublicMembersOutlineContribution")).to(N4JSFilterNonPublicMembersOutlineContribution.class);
  }
  
  /**
   * Toggle showing local types or not.
   */
  public void configureFilterLocalTypesOutlineContribution(final Binder binder) {
    binder.<IOutlineContribution>bind(IOutlineContribution.class).annotatedWith(Names.named("FilterLocalTypesOutlineContribution")).to(N4JSFilterLocalTypesOutlineContribution.class);
  }
  
  @Override
  public Class<? extends OutlineFilterAndSorter.IComparator> bindOutlineFilterAndSorter$IComparator() {
    return MetaTypeAwareComparator.class;
  }
  
  /**
   * Custom EditorResourceAccess as a fix for GH-234
   */
  public Class<? extends EditorResourceAccess> bindEditorResourceAccess() {
    return N4JSEditorResourceAccess.class;
  }
  
  /**
   * Workaround for the problem: file is refreshed when opened
   */
  public Class<? extends XtextDocumentProvider> bindXtextDocumentProvider() {
    return AvoidRefreshDocumentProvider.class;
  }
  
  /**
   * Custom XtextDocument.
   */
  public Class<? extends XtextDocument> bindXtextDocument() {
    return N4JSDocument.class;
  }
  
  /**
   * Custom XtextReconciler.
   */
  public Class<? extends XtextReconciler> bindXtextReconciler() {
    return N4JSReconciler.class;
  }
  
  /**
   * Custom IEObjectHover.
   */
  @Override
  public Class<? extends IEObjectHover> bindIEObjectHover() {
    return N4JSHover.class;
  }
  
  /**
   * Custom IHyperlinkDetector.
   */
  @Override
  public Class<? extends IHyperlinkDetector> bindIHyperlinkDetector() {
    return N4JSHyperlinkDetector.class;
  }
  
  @Override
  public void configureXtextEditorErrorTickUpdater(final Binder binder) {
    binder.<IXtextEditorCallback>bind(IXtextEditorCallback.class).annotatedWith(Names.named("IXtextEditorCallBack")).to(
      N4JSEditorErrorTickUpdater.class);
  }
  
  public N4JSXUiModule(final AbstractUIPlugin plugin) {
    super(plugin);
  }
}