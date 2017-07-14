/**
 * Copyright (c) 2016 NumberFour AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *   NumberFour AG - Initial API and implementation
 */
package org.eclipse.n4js.antlr

import com.google.common.io.Files
import com.google.inject.Inject
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.Collection
import java.util.Map
import org.antlr.runtime.CharStream
import org.antlr.runtime.RecognitionException
import org.antlr.runtime.TokenSource
import org.eclipse.n4js.antlr.n4js.NoLineTerminatorHandlingInjector
import org.eclipse.xtext.AbstractElement
import org.eclipse.xtext.Grammar
import org.eclipse.xtext.Group
import org.eclipse.xtext.util.internal.Log
import org.eclipse.xtext.xbase.lib.util.ReflectExtensions
import org.eclipse.xtext.xtext.FlattenedGrammarAccess
import org.eclipse.xtext.xtext.RuleFilter
import org.eclipse.xtext.xtext.RuleNames
import org.eclipse.xtext.xtext.generator.grammarAccess.GrammarAccessExtensions
import org.eclipse.xtext.xtext.generator.model.FileAccessFactory
import org.eclipse.xtext.xtext.generator.model.JavaFileAccess
import org.eclipse.xtext.xtext.generator.parser.antlr.ContentAssistGrammarNaming
import org.eclipse.xtext.xtext.generator.parser.antlr.GrammarNaming
import org.eclipse.xtext.xtext.generator.parser.antlr.XtextAntlrGeneratorFragment2
import org.eclipse.xtext.xtext.generator.util.SyntheticTerminalDetector

import static org.eclipse.n4js.antlr.replacements.Replacements.applyReplacement

import static extension org.eclipse.xtext.GrammarUtil.*
import static extension org.eclipse.xtext.xtext.generator.model.TypeReference.*
import static extension org.eclipse.xtext.xtext.generator.parser.antlr.AntlrGrammarGenUtil.*
import java.util.Set
import com.google.common.collect.ImmutableMap
import org.eclipse.xtend2.lib.StringConcatenationClient

/**
 * Customization of the {@link XtextAntlrGeneratorFragment2} applying some massaging
 *  of the Parser implementation generated by ANTLR.
 */
@Log
class N4JSAntlrGeneratorFragment2 extends N4AntlrGeneratorFragment2 {

	@Inject GrammarNaming productionNaming

	override protected generateProductionGrammar() {
		super.generateProductionGrammar()

		val extension naming = productionNaming
		val absoluteParserFileName = projectConfig.runtime.srcGen.path + '/' + grammar.getParserGrammar.grammarFileName

		massageGrammar(absoluteParserFileName, codeConfig.encoding)
	}

	def private void massageGrammar(String absoluteParserFileName, String encoding) {
		try {
			val javaFile = absoluteParserFileName.replaceAll("\\.g$", getParserFileNameSuffix());

			val content = Files.toString(new File(javaFile), Charset.forName(encoding));
			val normalizedContent = content.replace("\r\n", "\n");

			val newContent = fixIdentifierAsKeywordWithEOLAwareness(normalizedContent);

			if (normalizedContent.equals(newContent)) {
				LOG.warn("Replacement not found in " + javaFile);
			// throw new IllegalStateException("Replacement not found in " + javaFile);
			}

			if (!content.equals(newContent)) {
				Files.write(newContent, new File(javaFile), Charset.forName(encoding));
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	def protected String getParserFileNameSuffix() {
		return ".java";
	}

	def protected String getLexerFileNameSuffix() {
		return ".java";
	}

	/**
	 * This is part of the {@link NoLineTerminatorHandlingInjector}, fixing a problem with line-ending aware tokens like
	 * 'async'.
	 * <p>
	 * The problem likely (I do not know for sure) is as follows: In some cases, keywords are not reserved and may be
	 * used as identifiers as well. Examples are some N4JS specific keywords such as 'abstract' or 'project'. Now, these
	 * terminals can only occur as keywords in some very specific locations. This is different for 'async': Since it can
	 * be used as a modifier for function expressions or arrow functions, it can occur almost everywhere in the code. In
	 * order to distinguish keyword 'async' from its use as an identifier, no line terminator must follow in the first
	 * case. There are actually two problems: First, it is not directly possible to define "no line terminator here" in
	 * the Xtext grammar. Second, checking for the line terminator and the whole keyword vs. identifier handling seems
	 * to disable automatic semicolon insertation, at least partially. The result of the later problem is pretty bad:
	 * The parser may recognize an expression statement or identifier, but in the AST null is inserted instead. I assume
	 * this is because the ANTLR parser is not aware of the line-termintor-handling which partially disables its
	 * backtracking.
	 * <p>
	 * The solution is three fold:
	 * <ol>
	 * <li>In {@link NoLineTerminatorHandlingInjector} we rewrite the lexer rule to always reject the dummy token we
	 * need for the parser rule
	 * <li>Here, we rewrite the parser rule to fail if a line terminator was found
	 * <li>Also, we adapt the primary expression rule, to continue in case an 'async' keyword has been found.
	 * </ol>
	 * Some remarks on debugging: Note that the stack trace (and the debugger) have a limit of 64k lines. Then, the
	 * counter starts again! So breakpoints and stepping may be useless if you want to debug code after 64k lines.
	 * 
	 * TODO IDE-2406 clarify/document design decision
	 */
	def private String fixIdentifierAsKeywordWithEOLAwareness(String normalizedContent) {
		val String c1 = applyReplacement(normalizedContent, "ruleNoLineTerminator.java.replacement");
		val String c2 = applyReplacement(c1, "rulePrimaryExpression.java.replacement");
		return c2;
	}


	// below is the fix for GH-39 - Content Assist doesn't reliably work in generator functions
	// TODO will be available with Xtext 2.13 as part of the framework.
	@Inject ReflectExtensions reflector
	@Inject extension GrammarAccessExtensions grammarUtil
	@Inject extension SyntheticTerminalDetector

	private def <T> T reflectiveGet(String fieldName) {
		try {
			return reflector.get(this, fieldName);
		} catch (Exception e) {
			throw new RuntimeException(e)
		}
	}

	private def boolean hasSyntheticTerminalRule() {
		grammar.allTerminalRules.exists[isSyntheticTerminalRule]
	}

	private def ContentAssistGrammarNaming getContentAssistNaming() {
		return reflectiveGet("contentAssistNaming");
	}

	private def FileAccessFactory getFileFactory() {
		return reflectiveGet("fileFactory");
	}

	private def boolean isPartialParsing() {
		return <Boolean>reflectiveGet("partialParsing");
	}

	/**
	 * This method was copied from the super class to extract the logic for {@link #initNameMappings()}
	 * which needs access to the flattened grammar.
	 */
	override JavaFileAccess generateContentAssistParser() {
		val extension naming = contentAssistNaming
		val file = fileFactory.createGeneratedJavaFile(grammar.parserClass)
		file.content = '''
			public class «grammar.parserClass.simpleName» extends «grammar.getParserSuperClass(partialParsing)» {
			
				@«Inject»
				private «grammar.grammarAccess» grammarAccess;
			
				private «Map»<«AbstractElement», String> nameMappings;
			
				@Override
				protected «grammar.internalParserClass» createParser() {
					«grammar.internalParserClass» result = new «grammar.internalParserClass»(null);
					result.setGrammarAccess(grammarAccess);
					return result;
				}
			
				«IF hasSyntheticTerminalRule»
					@Override
					protected «TokenSource» createLexer(«CharStream» stream) {
						return new «grammar.tokenSourceClass»(super.createLexer(stream));
					}
					
				«ENDIF»
				@Override
				protected String getRuleName(«AbstractElement» element) {
					if (nameMappings == null) {
						«grammar.initNameMappings()»
					}
					return nameMappings.get(element);
				}
			
				@Override
				protected «Collection»<«"org.eclipse.xtext.ide.editor.contentassist.antlr.FollowElement".typeRef»> getFollowElements(«grammar.internalParserSuperClass» parser) {
					try {
						«grammar.internalParserClass» typedParser = («grammar.internalParserClass») parser;
						typedParser.«grammar.allParserRules.head.originalElement.entryRuleName»();
						return typedParser.getFollowElements();
					} catch(«RecognitionException» ex) {
						throw new «RuntimeException»(ex);
					}
				}
			
				@Override
				protected String[] getInitialHiddenTokens() {
					return new String[] { «FOR hidden : grammar.initialHiddenTokens SEPARATOR ", "»"«hidden»"«ENDFOR» };
				}
			
				public «grammar.grammarAccess» getGrammarAccess() {
					return this.grammarAccess;
				}
			
				public void setGrammarAccess(«grammar.grammarAccess» grammarAccess) {
					this.grammarAccess = grammarAccess;
				}
			}
		'''
		file
	}

	// TODO this will produce a compile error with 2.13 and can be removed then.
	/**
	 * Produce the initial name mappings for the grammar. Handles parameterized rule calls.
	 */
	private def StringConcatenationClient initNameMappings(Grammar it) {
		val RuleFilter filter = new RuleFilter();
		filter.discardUnreachableRules = true
		val RuleNames ruleNames = RuleNames.getRuleNames(it, true);
		val Grammar flattened = new FlattenedGrammarAccess(ruleNames, filter).getFlattenedGrammar();
		val Set<AbstractElement> seenElements = newHashSet
		return '''
			«ImmutableMap».Builder<«AbstractElement», String> nameMappingsBuilder = «ImmutableMap».builder();
			«FOR element : (flattened.allAlternatives + flattened.allGroups + flattened.allAssignments + flattened.allUnorderedGroups).filter(AbstractElement).filter[seenElements.add(originalElement)]»
				nameMappingsBuilder.put(grammarAccess.«element.originalElement.grammarElementAccess», "«element.originalElement.containingRule.contentAssistRuleName»__«element.originalElement.gaElementIdentifier»«IF element instanceof Group»__0«ENDIF»");
			«ENDFOR»
			nameMappings = nameMappingsBuilder.build();
		'''
	}
	
	// ^^^^^^^^^^
	// TODO remove until where with Xtext 2.13

}
