package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Map;
import java.util.Stack;

import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.*;

public class CodeGenerator extends VisitorAdaptor {

	private int mainPc;
	
	
	private enum relopEnum{
		RelopEqu,
		RelopNeq,
		RelopGrt,
		RelopGre,
		RelopLes,
		RelopLeq
	}
	
	private ArrayList<Integer> 	patchFacts = new ArrayList<>();
	private ArrayList<Integer> 	patchTerms = new ArrayList<>();
	private Stack<ArrayList<Integer>>  patchBreaks = new Stack<>();
	private Stack<ArrayList<Integer>>  patchContinues = new Stack<>();
	private Stack<Integer> 		patchConds = new Stack<>();
	private Stack<Integer>		patchElse	= new Stack<>();
	private Stack<Integer>		loopStartAdrs = new Stack<>();
	//private boolean inWhile;
	private int addAllSetAdr, unionMethodAdr, printSetAdr;
	
	public int getMainPc() {
		return mainPc;
	}

	CodeGenerator(){
		int offset;
		
		Obj ordMethod = Tab.find("ord");
        Obj chrMethod = Tab.find("chr");
        ordMethod.setAdr(Code.pc);
        chrMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.exit);
        Code.put(Code.return_);
 
        Obj lenMethod = Tab.find("len");
        lenMethod.setAdr(Code.pc);
        Code.put(Code.enter);					//6
        Code.put(1);
        Code.put(1);
        Code.put(Code.load_n);
        Code.put(Code.arraylength);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        //method written in microJava, compiled, then decompiled to 
        //create in-built function
        Obj addMethod = Tab.find("add");
        addMethod.setAdr(Code.pc);
        Code.put(Code.enter);					//13
        Code.put(2);
        Code.put(3);
        Code.put(Code.load_n);
        Code.put(Code.const_n);
        Code.put(Code.aload);
        Code.put(Code.load_n);
        offset = lenMethod.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.putFalseJump(Code.eq, 29);			//does nothing if buffer is full already
        Code.putJump(32);
        Code.putJump(34);						//29
        Code.put(Code.exit);					//32
        Code.put(Code.return_);					//buffer full
        Code.put(Code.const_1);					//34
        Code.put(Code.store_2);
        Code.put(Code.load_n);
        Code.put(Code.const_n);
        Code.put(Code.aload);
        Code.put(Code.const_1);
        Code.putFalseJump(Code.gt, 46);			//if adding first element check is skipped to allow correct counting when adding zero as the first element
        Code.putJump(49);
        Code.putJump(84);						//46
        //do-while
        Code.put(Code.load_n);					//49
        Code.put(Code.load_2);
        Code.put(Code.aload);
        Code.put(Code.load_1);
        //if
        Code.putFalseJump(Code.eq, 59);			//checks if element exists already
        Code.putJump(62);
        Code.putJump(64);						//59
        Code.put(Code.exit);					//62
        Code.put(Code.return_);					//element exists already
        //increment
        Code.put(Code.load_2);
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.put(Code.store_2);
        
        Code.put(Code.load_2);
        Code.put(Code.load_n);
        Code.put(Code.const_n);
        Code.put(Code.aload);
        
        Code.putFalseJump(Code.lt, 78);			//while condition
        Code.putJump(81);
        Code.putJump(84);						//78
        Code.putJump(49);
        
        Code.put(Code.load_n);
        Code.put(Code.load_n);					//consider replacing with dup
        Code.put(Code.const_n);
        Code.put(Code.aload);
        Code.put(Code.load_1);
        Code.put(Code.astore);
        
        Code.put(Code.load_n);
        Code.put(Code.const_n);
        Code.put(Code.dup2);
        Code.put(Code.aload);
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.put(Code.astore);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        //printHelper
        //Obj printSetMethod = Tab.find("printSet");
        //printSetMethod.setAdr(Code.pc);
        printSetAdr = Code.pc;
        Code.put(Code.enter);
        Code.put(1); 
        Code.put(3);
        Code.put(Code.load_n);
        Code.put(Code.const_n);
        Code.put(Code.aload);
        Code.put(Code.store_1);
        Code.put(Code.const_1);
        Code.put(Code.store_2);
        Code.put(Code.load_1);
        Code.put(Code.const_1);
        Code.put(Code.jcc+1);
        Code.put2(6);
        Code.put(Code.jmp);
        Code.put2(6);
        Code.put(Code.jmp);
        Code.put2(5);
        Code.put(Code.exit);
        Code.put(Code.return_);
        Code.put(Code.load_1);
        Code.put(Code.const_2);
        Code.put(Code.jcc+3);
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(35);
        Code.put(Code.load_n);
        Code.put(Code.load_2);
        Code.put(Code.aload);
        Code.put(Code.const_n);
        Code.put(Code.print);
        Code.put(Code.const_);
        Code.put4(32);
        Code.put(Code.const_n);
        Code.put(Code.bprint);
        Code.put(Code.load_2);
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.put(Code.store_2);
        Code.put(Code.load_2);
        Code.put(Code.load_1);
        Code.put(Code.const_1);
        Code.put(Code.sub);
        Code.put(Code.jcc+5); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(-29);
        Code.put(Code.load_n);
        Code.put(Code.load_2);
        Code.put(Code.aload);
        Code.put(Code.const_n);
        Code.put(Code.print);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        //addAll
        Obj addAllMethod = Tab.find("addAll");
        addAllMethod.setAdr(Code.pc);
        Code.put(Code.enter);
        Code.put(2);
        Code.put(4);
        Code.put(Code.load_1);
        offset = lenMethod.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.put(Code.store_2);
        Code.put(Code.const_n);
        Code.put(Code.store_3);
        Code.put(Code.load_n);
        Code.put(Code.load_1);
        Code.put(Code.load_3);
        Code.put(Code.aload);
        offset = addMethod.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.put(Code.load_3);
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.put(Code.store_3);
        Code.put(Code.load_3);
        Code.put(Code.load_2);
        Code.put(Code.jcc+5);
        Code.put2(6);
        Code.put(Code.jmp);
        Code.put2(6);
        Code.put(Code.jmp);
        Code.put2(6);
        Code.put(Code.jmp);
        Code.put2(-22);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        //addAllSet
        addAllSetAdr = Code.pc;
        Code.put(Code.enter); 
        Code.put(2); 
        Code.put(4);
        Code.put(Code.load_1);
        Code.put(Code.const_n);
        Code.put(Code.aload);
        Code.put(Code.store_2);
        Code.put(Code.load_2);
        Code.put(Code.const_1);
        Code.put(Code.jcc+1); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(6);
        Code.put(Code.jmp); 
        Code.put2(5);
        Code.put(Code.exit);
        Code.put(Code.return_);
        Code.put(Code.const_1);
        Code.put(Code.store_3);
        Code.put(Code.load_n);
        Code.put(Code.load_1);
        Code.put(Code.load_3);
        Code.put(Code.aload);
        offset = addMethod.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.put(Code.load_3);
        Code.put(Code.const_1);
        Code.put(Code.add);
        Code.put(Code.store_3);
        Code.put(Code.load_3);
        //Code.put(Code.const_1);
        //Code.put(Code.add);
        Code.put(Code.load_2);
        Code.put(Code.jcc+5); 
        Code.put2(6); 
        Code.put(Code.jmp); 
        Code.put2(6); 
        Code.put(Code.jmp); 
        Code.put2(6);  
        Code.put(Code.jmp); 
        Code.put2(-22); //bilo -24
        Code.put(Code.exit);
        Code.put(Code.return_);
        
        
        unionMethodAdr = Code.pc;
        Code.put(Code.enter); 
        Code.put(3); 
        Code.put(3);
        Code.put(Code.load_n);
        Code.put(Code.load_1);
        offset = addAllSetAdr - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.put(Code.load_n);
        Code.put(Code.load_2);
        //Code.put(Code.call);
        offset = addAllSetAdr - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
        Code.put(Code.exit);
        Code.put(Code.return_);
        
	}
	
	@Override
	public void visit (MethName mn) {
		mn.obj.setAdr(Code.pc);
		if(mn.obj.getName().equals("main"))
			mainPc = Code.pc;
		Code.put(Code.enter);
		Code.put(mn.obj.getLevel());
		Code.put(mn.obj.getLocalSymbols().size()); 
	}
	
	@Override
	public void visit (MethodDecl md) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override 
	public void visit (ConstFN constant) {
		Code.loadConst(constant.getN1());
	}
	
	@Override 
	public void visit (ConstFC constant) {
		Code.loadConst(constant.getC1());
	}
	
	@Override 
	public void visit (ConstFB constant) {
		Code.loadConst(constant.getB1());
	}
	
	@Override
	public void visit(DesignatorName dn) {
		Code.load(dn.obj);
	}
	
	@Override
	public void visit(FactorDes factor) {
		Code.load(factor.getDesignator().obj);
	}
		
	@Override
	public void visit(FactorPars factor) {
		int offset = factor.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	@Override 
	public void visit(FactorNew factor) {
		//changes may be required for set
		Struct typeStruct = Tab.find(factor.getType().getI1()).getType();
		//set type uses first element of array to store next free index (for shorter arithmetic)
		if(typeStruct.getKind() == SemAnalyzer.setStructConst) {
			//increases capacity by one 
			Code.loadConst(1);
			Code.put(Code.add);
			Code.put(Code.newarray);
			Code.put(1);
			Code.put(Code.dup);
			//sets 0th element to 1 (first free index)
			Code.loadConst(0);
			Code.loadConst(1);
			Code.put(Code.astore);
			return;
		}
		Code.put(Code.newarray);	
		if (typeStruct == Tab.charType) {
			Code.put(0);
		}
		else {
			Code.put(1);
		}
	}
	
	@Override
	public void visit(SignedFactorNeg factor) {
		Code.put(Code.neg);
	}
	
	@Override
	public void visit(TermRec tr) {
		Mulop operator = tr.getMulop();
		if(operator instanceof MulopMul)
			Code.put(Code.mul);
		if(operator instanceof MulopDiv)
			Code.put(Code.div);
		if(operator instanceof MulopRem)
			Code.put(Code.rem);
	}
	
	@Override
	public void visit(ExprRec expression) {
		Addop operator = expression.getAddop();
		if(operator instanceof AddopMinus)
			Code.put(Code.sub);
		if(operator instanceof AddopPlus)
			Code.put(Code.add);
	}
	
	@Override 
	public void visit(ExprMap expression) {
		//generates code each time map is called, because call address must be determined during compile time
		//otherwise would be implemented akin to function using function pointers
		
		Code.load(expression.getDesignator1().obj);
		Code.put(Code.enter);
		Code.put(1); 
		Code.put(4);
		Code.put(Code.load_n);
		int offset = Tab.find("len").getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset); //-214 (=6)
		Code.put(Code.store_2);
		Code.put(Code.load_1);
		Code.put(Code.load_n);
		Code.put(Code.load_3);
		Code.put(Code.aload);
		offset = expression.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);  //-20 (=208)
		Code.put(Code.add);
		Code.put(Code.store_1);
		Code.put(Code.load_3);
		Code.put(Code.const_1);
		Code.put(Code.add);
		Code.put(Code.store_3);
		Code.put(Code.load_3);
		Code.put(Code.load_2);
		Code.put(Code.jcc+5); 
		Code.put2(6);
		Code.put(Code.jmp);   
		Code.put2(6);
		Code.put(Code.jmp);   
		Code.put2(6); 
		Code.put(Code.jmp);   
		Code.put2(-24); 
		Code.put(Code.load_1);
		Code.put(Code.exit);
		/*Code.put(Code.return_);
		Code.put(Code.exit);
		Code.put(Code.return_);*/
	}
	
	@Override
	public void visit(DesStmtAssExpr assign) {
		Code.store(assign.getDesignator().obj);
	}
	
	@Override
	public void visit(DesStmtInc increment) {
		if(increment.getDesignator().obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		Code.load(increment.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.add);
		Code.store(increment.getDesignator().obj);
	}
	
	@Override
	public void visit(DesStmtDec decrement) {
		if(decrement.getDesignator().obj.getKind() == Obj.Elem)
			Code.put(Code.dup2);
		Code.load(decrement.getDesignator().obj);
		Code.loadConst(1);
		Code.put(Code.sub);
		Code.store(decrement.getDesignator().obj);
	}
	
	@Override
	public void visit(DesStmtPars invocation) {
		int offset = invocation.getDesignator().obj.getAdr() - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
		if (!invocation.getDesignator().obj.getType().equals(Tab.noType))
			Code.put(Code.pop);
	}
	
	@Override
	public void visit(DesStmtUnion union) {
		Code.load(union.getDesignator().obj);
		Code.load(union.getDesignator1().obj);
		Code.load(union.getDesignator2().obj);
		int offset = unionMethodAdr - Code.pc;
		Code.put(Code.call);
		Code.put2(offset);
	}
	
	@Override
	public void visit(StmtRet ret) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	@Override
	public void visit(StmtRetExpr ret) {
		Code.put(Code.exit);
		Code.put(Code.return_);
	}
	
	//INPUT OUTPUT
	
	@Override
	public void visit(StmtRead read) {
		Obj desObj = read.getDesignator().obj;
		if(desObj.getType().equals(Tab.charType))
			Code.put(Code.bread);
		else
			Code.put(Code.read);
		Code.store(desObj);
	}
	
	@Override
	public void visit(StmtPrint print) {
		if(print.getExpr().struct.getKind() == SemAnalyzer.setStructConst) {
			int offset = printSetAdr - Code.pc;
			Code.put(Code.call);
			Code.put2(offset);
			return;
		}
		
		Code.loadConst(0);
		if(print.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	@Override
	public void visit(StmtPrintW print) {
		Code.loadConst(print.getN2());
		if(print.getExpr().struct.equals(Tab.charType))
			Code.put(Code.bprint);
		else
			Code.put(Code.print);
	}
	
	//CONTROL STRUCTURES
	
	@Override 
	public void visit(CondFactOp condFactor) {
		switch (relopEnum.valueOf(condFactor.getRelop().getClass().getSimpleName())) {
		case RelopEqu:
			Code.putFalseJump(Code.eq, 0);	
			break;

		case RelopNeq:
			Code.putFalseJump(Code.ne, 0);
			break;
			
		case RelopGrt:
			Code.putFalseJump(Code.gt, 0);
			break;
			
		case RelopGre:
			Code.putFalseJump(Code.ge, 0);
			break;
			
		case RelopLes:
			Code.putFalseJump(Code.lt, 0);
			break;
			
		case RelopLeq:
			Code.putFalseJump(Code.le, 0);
			break;
		}
		//stavi u listu za pecovanje sledeceg Terma
		patchFacts.add(Code.pc - 2);
	}
	
	@Override 
	public void visit(CondFactNoOp condFactor) {
		Code.loadConst(1);
		Code.putFalseJump(Code.eq, 0);
		//stavi u listu za pecovanje sledeceg Terma, koji setuje term
		patchFacts.add(Code.pc - 2);
	}
	
	@Override
	public void visit(CondTerm condTerm) {
		Code.putJump(0); //setuje ga COND, stavi u listu, skace se na THEN
		patchTerms.add(Code.pc - 2);
		//patchovanje liste factova na trenutnu adr
		while(!patchFacts.isEmpty())
			Code.fixup(patchFacts.remove(0));
	}
	
	
	@Override
	public void visit(Condition condition) {
		Code.putJump(0); //patchuje ili else ili end of loop
		patchConds.push(Code.pc-2);
		
		//patchovanje liste termova na trenutnu adr(then granu/ while stmt)
		while(!patchTerms.isEmpty())
			Code.fixup(patchTerms.remove(0));
	}
	
	
	@Override 
	public void visit(StmtIf statement) {
		Code.fixup(patchConds.pop());
	}
	
	@Override 
	public void visit(StmtIfElse statement) {
		Code.fixup(patchElse.pop());
	}
	
	@Override
	public void visit(Else elseBranch) {
		Code.putJump(0); //patchuje ga stmtIfElse
		patchElse.push(Code.pc-2);
		Code.fixup(patchConds.pop());
	}
	
	@Override
	public void visit(Do doLoop) {
		loopStartAdrs.push(Code.pc);
		patchBreaks.push(new ArrayList<>());
		patchContinues.push(new ArrayList<>());
	}
	
	@Override 
	public void visit(StmtBreak statement) {
		Code.putJump(0);
		patchBreaks.peek().add(Code.pc-2);
	}
	
	@Override 
	public void visit(StmtCont statement) {
		Code.putJump(0);
		patchContinues.peek().add(Code.pc-2);
	}
	
	@Override
	public void visit(While w) {
		ArrayList<Integer> list = patchContinues.pop();
		//patching continues
		while(!list.isEmpty())
			Code.fixup(list.remove(0));
	}
	
	@Override
	public void visit (StmtDo statement) {
		Code.putJump(loopStartAdrs.pop());
		//patching breaks
		ArrayList<Integer> list = patchBreaks.pop();
		while(!list.isEmpty())
			Code.fixup(list.remove(0));
	}
	
	@Override
	public void visit (StmtDoCond statement) {
		Code.putJump(loopStartAdrs.pop());
		Code.fixup(patchConds.pop());
		
		ArrayList<Integer> list = patchBreaks.pop();
		while(!list.isEmpty())
			Code.fixup(list.remove(0));
	}
	
	@Override
	public void visit (StmtDoFull statement) {
		Code.putJump(loopStartAdrs.pop());
		Code.fixup(patchConds.pop());
		
		ArrayList<Integer> list = patchBreaks.pop();
		while(!list.isEmpty())
			Code.fixup(list.remove(0));
	}
	
	
}
