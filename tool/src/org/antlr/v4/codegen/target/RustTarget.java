package org.antlr.v4.codegen.target;

import org.antlr.v4.codegen.CodeGenerator;
import org.antlr.v4.codegen.Target;
import org.antlr.v4.codegen.UnicodeEscapes;
import org.antlr.v4.tool.Grammar;
import org.antlr.v4.tool.ast.GrammarAST;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RustTarget extends Target {
	protected RustTarget(CodeGenerator gen, String language) {
		super(gen, language);
	}

	@Override
	public String getVersion() {
		return "4.7.1";
	}

	@Override
	protected void appendUnicodeEscapedCodePoint(int codePoint, StringBuilder sb) {
		UnicodeEscapes.appendSwiftStyleEscapedCodePoint(codePoint, sb);
	}

	@Override
	protected boolean visibleGrammarSymbolCausesIssueInGeneratedCode(GrammarAST idNode) {
		return badWords.contains(idNode.getText());
	}

	private static final String[] keywords = new String[] {
		"as", "break", "const", "continue", "crate", "else", "enum",
		"extern", "false", "fn", "for", "if", "impl", "in", "let",
		"loop", "match", "mod", "move", "mut", "pub", "ref", "return",
		"Self", "self", "static", "struct", "super", "trait", "true",
		"type", "unsafe", "use", "where", "while",
		// Reserved keywords
		"abstract", "alignof", "become", "box", "do", "final",
		"macro", "offsetof", "override", "priv", "proc", "pure",
		"sizeof", "typeof", "unsized", "virtual", "yield",
		// Contextual keywords
		"union", "dyn",
		// Rust 2018 keywords
		"async", "await", "try", "catch",
 	};
	private static final String[] prelude = new String[] {
		"Copy", "Send", "Sized", "Sync", "Drop", "Fn", "FnMut", "FnOnce",
		"drop", "Box", "ToOwned", "Clone", "PartialEq", "PartialOrd", "Eq", "Ord",
		"AsRef", "AsMut", "Into", "From", "Default", "Iterator", "Extend",
		"IntoIterator", "DoubleEndedIterator", "ExactSizeIterator", "Option",
		"Some", "None", "Result", "Ok", "Err", "SliceConcatExt",
		"String", "ToString", "Vec"
	};
	private static final String[] primitiveTypes = new String[] {
		"bool", "char", "f32", "f64", "fn",
	};
	private static final String[] integerSizes = new String[] {
		"8", "16", "32", "64", "128", "size"
	};
	private static final Set<String> badWords = new HashSet<>();
	static {
		Collections.addAll(badWords, keywords);
		Collections.addAll(badWords, prelude);
		Collections.addAll(badWords, primitiveTypes);
		for (String size : integerSizes) {
			badWords.add("i" + size);
			badWords.add("u" + size);
		}
		badWords.add("rule");
		badWords.add("parser_rule");
		badWords.add("action");
	}


	public String getRecognizerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g!=null;
		String name;
		switch ( g.getType()) {
			case ANTLRParser.PARSER:
				name = g.name.endsWith("Parser") ? g.name.substring(0, g.name.length()-6) : g.name;
				return name.toLowerCase()+"_parser.rs";
			case ANTLRParser.LEXER:
				name = g.name.endsWith("Lexer") ? g.name.substring(0, g.name.length()-5) : g.name; // trim off "lexer"
				return name.toLowerCase()+"_lexer.rs";
			case ANTLRParser.COMBINED:
				return g.name.toLowerCase()+"_parser.rs";
			default :
				return "INVALID_FILE_NAME";
		}
	}

	/** A given grammar T, return the listener name such as
	 *  TListener.java, if we're using the Java target.
	 */
	public String getListenerFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_listener.rs";
	}

	/** A given grammar T, return the visitor name such as
	 *  TVisitor.java, if we're using the Java target.
	 */
	public String getVisitorFileName(boolean header) {
		CodeGenerator gen = getCodeGenerator();
		Grammar g = gen.g;
		assert g.name != null;
		return g.name.toLowerCase()+"_visitor.rs";
	}

}
