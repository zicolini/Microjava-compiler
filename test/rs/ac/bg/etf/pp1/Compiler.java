package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import java_cup.runtime.*;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.ast.*;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class Compiler {

	static {
		DOMConfigurator.configure(Log4JUtils.instance().findLoggerConfigFile());
		Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
	}
	
	public static void main(String[] args) throws Exception {		
		Logger log = Logger.getLogger(Compiler.class);		
		Reader br = null;
		try {
			
			File sourceCode = new File("test/program.mj");
			log.info("Compiling source file: " + sourceCode.getAbsolutePath());
			
			br = new BufferedReader(new FileReader(sourceCode));
			Yylex lexer = new Yylex(br);			
			MJParser p = new MJParser(lexer);
	        Symbol s = p.parse();  //formiranje AST
	        
	        Program prog = (Program)(s.value);
	        
			// ispis AST
			log.info(prog.toString(""));
			log.info("=====================================================================");
			
			Tab.init();
			Struct boolType = new Struct(Struct.Bool);
			Obj boolObj = Tab.insert(Obj.Type, "bool", boolType);
			boolObj.setAdr(-1);
			boolObj.setLevel(-1);
		
			for(Obj fp: Tab.find("chr").getLocalSymbols())
				fp.setFpPos(1);
			for(Obj fp: Tab.find("ord").getLocalSymbols())
				fp.setFpPos(1);
			for(Obj fp: Tab.find("len").getLocalSymbols())
				fp.setFpPos(1);
			
			Struct setType = new Struct(SemAnalyzer.setStructConst); 
			setType.setElementType(Tab.intType);
			Obj setObj = Tab.insert(Obj.Type, "set", setType);
			setObj.setAdr(-1);
			setObj.setLevel(-1);
	
			
			Obj add = Tab.insert(Obj.Meth, "add", Tab.noType);
			Tab.openScope();
				Tab.insert(Obj.Var, "a", Tab.find("set").getType()).setFpPos(1);
				Tab.insert(Obj.Var, "b", Tab.intType).setFpPos(1);
				add.setLevel(2);
				Tab.chainLocalSymbols(add);
			Tab.closeScope();
			
			Obj addAll = Tab.insert(Obj.Meth, "addAll", Tab.noType);
			Tab.openScope();
				Tab.insert(Obj.Var, "a", setType).setFpPos(1);
				Tab.insert(Obj.Var, "b", new Struct(Struct.Array, Tab.intType)).setFpPos(1);
				Tab.chainLocalSymbols(addAll);
			Tab.closeScope(); 
			
			
			/*
			Obj printSet = Tab.insert(Obj.Meth, "printSet", Tab.noType);
			Tab.openScope();
				Tab.insert(Obj.Var, "a", setType).setFpPos(1);
				Tab.chainLocalSymbols(printSet);
			Tab.closeScope(); 
			
			Obj map = Tab.insert(Obj.Meth, "map", Tab.intType);
			Tab.openScope();
				Tab.insert(Obj.Var, "a", Tab.intType).setFpPos(1);
				Tab.insert(Obj.Var, "b", new Struct(Struct.Array, Tab.intType)).setFpPos(1);
				Tab.chainLocalSymbols(map);
			Tab.closeScope(); 
			*/
			
			SemAnalyzer analyzer = new SemAnalyzer();
			prog.traverseBottomUp(analyzer);
			
			//ispis tabele
			Tab.dump();
			
			if(!p.errorDetected && analyzer.passed()){
				log.info("Parsing COMPLETE");
				
				File objFile = new File("test/program.obj");
				if(objFile.exists()) objFile.delete();
				
				CodeGenerator generator = new CodeGenerator();
				prog.traverseBottomUp(generator);
				Code.dataSize = analyzer.getNVars();
				Code.mainPc = generator.getMainPc();
				Code.write(new FileOutputStream(objFile));
				
			}else{
				log.error("Parsing FAILED");
			}
			
		} 
		finally {
			if (br != null) try { br.close(); } catch (IOException e1) { log.error(e1.getMessage(), e1); }
		}

	}
	
	
}
