package net.sf.sveditor.core.tests.parser;

import junit.framework.TestCase;
import net.sf.sveditor.core.StringInputStream;
import net.sf.sveditor.core.db.SVDBItem;
import net.sf.sveditor.core.db.SVDBLocation;
import net.sf.sveditor.core.db.SVDBTaskFuncParam;
import net.sf.sveditor.core.db.SVDBTaskFuncScope;
import net.sf.sveditor.core.parser.ParserSVDBFileFactory;
import net.sf.sveditor.core.parser.SVParseException;

public class TestParseFunction extends TestCase {
	
	public void testBasicFunction() throws SVParseException {
		String content =
			"function void foobar();\n" +
			"    a = 5;\n" +
			"endfunction\n";
		
		ParserSVDBFileFactory parser = new ParserSVDBFileFactory(null);
		parser.init(new StringInputStream(content), "test");
		
		SVDBLocation start = new SVDBLocation(1, 0);
		parser.parsers().functionParser().parse(start, 0);
	}

	public void testStaticFunction() throws SVParseException {
		String content =
			"function static void foobar();\n" +
			"    int a;\n" +
			// "    a = 5;\n" +
			"endfunction\n";
		
		ParserSVDBFileFactory parser = new ParserSVDBFileFactory(null);
		parser.init(new StringInputStream(content), "test");
		
		SVDBLocation start = new SVDBLocation(1, 0);
		SVDBTaskFuncScope func = 
			parser.parsers().functionParser().parse(start, 0);
		
		for (SVDBItem it : func.getItems()) {
			System.out.println("it " + it.getType() + " " + it.getName());
		}
	}

	public void testAutomaticFunction() throws SVParseException {
		String content =
			"function automatic void foobar();\n" +
			"    a = 5;\n" +
			"endfunction\n";
		
		ParserSVDBFileFactory parser = new ParserSVDBFileFactory(null);
		parser.init(new StringInputStream(content), "test");
		
		SVDBLocation start = new SVDBLocation(1, 0);
		parser.parsers().functionParser().parse(start, 0);
	}

	public void testParamListFunction() throws SVParseException {
		String content =
			"function automatic void foobar(\n" +
			"        input int foobar,\n" +
			"        ref object bar,\n" +
			"        int foo);\n" +
			"    a_type foo, bar;\n" +
			"    b_type foo_q[$];\n" +
			"    b_cls #(foobar, bar) elem;\n" +
			"    int i, j, k;\n" +
			"    for (int i=0; i<5; i++) begin\n" +
			"        a = 5;\n" +
			"    end\n" +
			"endfunction\n";
		
		ParserSVDBFileFactory parser = new ParserSVDBFileFactory(null);
		parser.init(new StringInputStream(content), "test");
		
		SVDBLocation start = new SVDBLocation(1, 0);
		SVDBTaskFuncScope func = parser.parsers().functionParser().parse(start, 0);
		
		assertEquals("bar", func.getParams().get(1).getName());
		assertEquals(SVDBTaskFuncParam.Direction_Ref,
				func.getParams().get(1).getDir());
	}

}