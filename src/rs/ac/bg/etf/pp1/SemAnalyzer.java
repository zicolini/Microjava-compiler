package rs.ac.bg.etf.pp1;

import java.util.ArrayList;
//import java.util.Collection;
//import java.util.Iterator;
import java.util.Stack;

import org.apache.log4j.*;
import rs.ac.bg.etf.pp1.ast.*;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

public class SemAnalyzer extends VisitorAdaptor {
	

	private boolean errorDetected = false;
	Logger log  = Logger.getLogger(getClass());
	private Obj progObj;
	private Struct currentType;
	private Struct boolType = Tab.find("bool").getType();
	private Struct setType = Tab.find("set").getType();
	private int currConstVal;
	private Struct currConstType;	//type assigned to constant or type of factor
	private boolean voidMoreRecent; //used for method signature, if true method is of void type
	private boolean mainExists;     //assures main is defined
	private Obj currMeth;
	private String currentMethName;
	private String currDesignatorName;
	private int loopLevel = 0;
	private boolean returnExists;
	private boolean relopArrCompliant;
	private ArrayList<Struct> methodLocals;
	private Stack<ArrayList<Struct>> actParLists = new Stack<>();
	private int nVars;
	static final int setStructConst = 8;
	
	public void reportError(String message, SyntaxNode info) {
		errorDetected = true;
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append("on line ").append(line);
		log.error(msg.toString());
 	}
	
	public void reportInfo(String message, SyntaxNode info) {
		StringBuilder msg = new StringBuilder(message);
		int line = (info == null) ? 0 : info.getLine();
		if (line != 0)
			msg.append("on line ").append(line);
		log.info(msg.toString());
	}
	
	public boolean passed() {
		return !errorDetected;
	}

	
	public int getNVars() {
		return nVars;
	}
	
	@Override
	public void visit(ProgramName progName) {
		progObj = Tab.insert(Obj.Prog, progName.getI1(), Tab.noType);
		Tab.openScope();
	}
	
	@Override
	public void visit (Program prog) {
		nVars = Tab.currentScope().getnVars();
		Tab.chainLocalSymbols(progObj);
		Tab.closeScope();
		if(!mainExists)
			reportError("main not defined ", prog);
	}
	
	
	@Override
	public void visit (Type type) {
		Obj typeObj = Tab.find(type.getI1());
		if (typeObj == Tab.noObj || typeObj.getKind() != Obj.Type) {
			reportError("Cannot be resolved to a type ", type);
			currentType = Tab.noType;
		}
		else
			currentType = typeObj.getType();
	}
	
	@Override
	public void visit(ConstN constant) {
		currConstVal = constant.getN1();
		currConstType = Tab.intType;
	}
	
	@Override
	public void visit(ConstC constant) {
		currConstVal = constant.getC1();
		currConstType = Tab.charType;
	}
	
	@Override
	public void visit(ConstB constant) {
		currConstVal = constant.getB1();
		currConstType = boolType;
	}
	
	//repeated methods for use in phase 4
	public void visit(ConstFN constant) {
		currConstVal = constant.getN1();
		currConstType = Tab.intType;
	}
	
	@Override
	public void visit(ConstFC constant) {
		currConstVal = constant.getC1();
		currConstType = Tab.charType;
	}
	
	@Override
	public void visit(ConstFB constant) {
		currConstVal = constant.getB1();
		currConstType = boolType;
	}
	
	private void constHelper(SyntaxNode constantItem, String name) {
		if(currentType == setType) 
			reportError("Const set not allowed " + name + " ", constantItem);
		else {
		boolean typeCond = currConstType.assignableTo(currentType);
		if(Tab.find(name) == Tab.noObj &&  typeCond)
			Tab.insert(Obj.Con, name, currentType).setAdr(currConstVal);
		else 
			if(typeCond)
				reportError("Const " + name + " already defined ", constantItem);
			else
				reportError("Invalid type assigned to " + name + " ", constantItem);
		}
	}
	
	@Override 
	public void visit (ConstListRec cl) {
		constHelper(cl, cl.getI2());
	}
	
	@Override 
	public void visit (ConstListBase cl) {
		constHelper(cl, cl.getI1());
	}
	
	private void varHelper(String name, SyntaxNode node, boolean isFP) {
		Obj search;
		if (currMeth == null)
			search = Tab.find(name);
			else
				search = Tab.currentScope().findSymbol(name);
		
		if(search == Tab.noObj || search == null) {
			Obj inserted = Tab.insert(Obj.Var, name, currentType);
			if(isFP) {
				inserted.setFpPos(1);
				//if(currMeth != null)
				currMeth.setLevel(currMeth.getLevel() + 1);
			}
		}
		else 
			reportError("Var already defined ", node);
	}
	
	private void varHelperArr(String name, SyntaxNode node, boolean isFP) {
		Obj search;
		if (currMeth == null)
			search = Tab.find(name);
			else
				search = Tab.currentScope().findSymbol(name);
		
		if(search == Tab.noObj || search == null) {
				Obj inserted = Tab.insert(Obj.Var, name, new Struct(Struct.Array, currentType));
			if(isFP) {
				inserted.setFpPos(1);
				currMeth.setLevel(currMeth.getLevel() + 1);
			}	
		}
		else 
			reportError("Var already defined ", node);
	}
	
	@Override
	public void visit(VarRec vr) {
		varHelper(vr.getI2(), vr, false);
	}
	
	@Override
	public void visit(VarRecArr vra) {
		if(currentType == setType)
			reportError("Array of set not permitted ", vra);
		else
			varHelperArr(vra.getI2(), vra, false);
	}
	
	@Override
	public void visit(VarBase vb) {
		varHelper(vb.getI1(), vb, false);
	}
	
	@Override
	public void visit(VarBaseArr vba) {
		if(currentType == setType)
			reportError("Array of set not permitted ", vba);
		else
			varHelperArr(vba.getI1(), vba, false);
	}
	
	@Override
	public void visit(FormParsRec fpr) {
		varHelper(fpr.getI3(), fpr, true);
	}
	
	@Override
	public void visit(FormParsRecArr fpra) {
		varHelperArr(fpra.getI3(), fpra, true);
	}
	
	@Override
	public void visit(FormParsBase fpb) {
		varHelper(fpb.getI2(), fpb, true);
	}
	
	@Override
	public void visit(FormParsBaseArr fpba) {
		varHelperArr(fpba.getI2(), fpba, true);
	}
	
	@Override
	public void visit(MethTypeT mtt) {
		voidMoreRecent = false;
	}
	
	@Override
	public void visit(MethTypeV mtv) {
		voidMoreRecent = true;
	}
	
	@Override
	public void visit(MethName mName) {
		currentMethName = mName.getI1();
		if(Tab.find(currentMethName) != Tab.noObj)
			reportError("Mehtod already defined ", mName);
		else
			currMeth = Tab.insert(Obj.Meth, currentMethName, voidMoreRecent ? Tab.noType : currentType);
			mName.obj = currMeth;
			Tab.openScope();	
	}
	
	@Override 
	public void visit(MethParsForm mpf) {
		if(currentMethName.equals("main")) {
			mainExists = true;
			reportError("Main must not have parameters ", mpf);
			if(!voidMoreRecent)
				reportError("Invalid type for main ", mpf);
		}
	}
	
	@Override
	public void visit(MethParsEps mpe) {
		if(currentMethName.equals("main")) {
				mainExists = true;
			if(!voidMoreRecent)
				reportError("Invalid type for main ", mpe);
		}
	}
	
	@Override
	public void visit(MethodDecl md) {
		Tab.chainLocalSymbols(currMeth);
		Tab.closeScope();
		if(currMeth.getType() != Tab.noType && !returnExists)
			reportError("No return statement found for non-void function ", md);
		returnExists = false;
		currMeth = null;
	}
	
	@Override
	public void visit(FactorDes fd) {
		//check kind (vjv samo var i const)
		fd.struct = fd.getDesignator().obj.getType();
	}
	
	@Override
	public void visit(FactorPars fp) {
		Obj desObj = fp.getDesignator().obj;
		if(desObj.getKind() != Obj.Meth) {
			reportError("Object "+ desObj.getName() +" not of Method kind ", fp);
			fp.struct = Tab.noType;
		}
		else {
			methodLocals = new ArrayList<>();
			 for(Obj local : desObj.getLocalSymbols()) {
				 if(local.getKind() == Obj.Var && local.getFpPos()==1)
					methodLocals.add(local.getType());
			 } 
			 ArrayList<Struct> actParList = actParLists.pop();
			 if(methodLocals.size()!=actParList.size())
				 reportError("Number of parameters not matching function declaration " + desObj.getName() + " ", fp);
			 else
				 //reportInfo(Integer.toString(currConstVal), fp);
				 for (int i = 0; i < methodLocals.size(); i++) {
					 if(!actParList.get(i).assignableTo(methodLocals.get(i)))
						 reportError("Parameter type not matching ", fp);
				 }
		}
		fp.struct = fp.getDesignator().obj.getType();
	}
	
	@Override
	public void visit(FactorConst fc) {
		fc.struct = currConstType;
	}
	
	@Override
	public void visit(FactorNew fn) {	
		if(!fn.getExpr().struct.equals(Tab.intType)) {
			reportError("Size not of int type ", fn);
			fn.struct = Tab.noType;
		}
		else
			if(currentType != setType)
				fn.struct = new Struct(Struct.Array, currentType);
			else {
				fn.struct = setType;

				//fn.struct = new Struct(setStructConst);
				//fn.struct.setElementType(Tab.intType);
			}
			
	}
	
	@Override
	public void visit(FactorExpr fe) {
		fe.struct = fe.getExpr().struct;
	}
	
	@Override
	public void visit(SignedFactorNeg sfn) {
		if(sfn.getFactor().struct != Tab.intType) {
			reportError("Negation not permitted for non-int types ", sfn);
			sfn.struct = Tab.noType;
		}
		else
			sfn.struct = Tab.intType;
	}
	
	@Override
	public void visit(SignedFactorPos sfp) {
		sfp.struct = sfp.getFactor().struct;
	}
	
	@Override
	public void visit(TermBase tb) {
		tb.struct = tb.getSignedFactor().struct;
	}
	
	@Override
	public void visit(TermRec tr) {
		if(!tr.getTerm().struct.equals(Tab.intType) || !tr.getSignedFactor().struct.equals(Tab.intType)) {
			reportError("Multiplicatioin not permitted for non-int types ", tr);
			tr.struct = Tab.noType;
		}
		else
				tr.struct = Tab.intType;
	}
	
	@Override 
	public void visit(ExprBase eb) {
		eb.struct = eb.getTerm().struct;
	}
	
	@Override
	public void visit(ExprRec er) {
		if(!er.getExpr().struct.equals(Tab.intType) || !er.getTerm().struct.equals(Tab.intType)) {
			reportError("addition not permitted for non-int types ", er);
			er.struct = Tab.noType;
		}
		else
				er.struct = Tab.intType;
	}
	
	@Override 
	public void visit(ExprMap expression) {
		expression.struct = Tab.intType;
		Obj methObj = expression.getDesignator().obj;
		if(methObj.getKind() != Obj.Meth) {
			reportError("Non-method " + methObj.getName() + " used to map array ", expression);
			expression.struct = Tab.noType;
		}
		else {
			if(!methObj.getType().equals(Tab.intType)) {
				reportError("Method " + methObj.getName() + " must return int type ", expression);
				expression.struct = Tab.noType;
			}
			boolean paramCounted = false;
			for(Obj local : methObj.getLocalSymbols()) {
				if(local.getKind() == Obj.Var && local.getFpPos()==1)
					if(paramCounted) {
						reportError("Mapping method " + methObj.getName() + " must have one parameter only ", expression);
						expression.struct = Tab.noType;
						break;
					}
					if(!local.getType().equals(Tab.intType)) {
						reportError("Mapping method " + methObj.getName() + " parameter must be of int type ", expression);
						expression.struct = Tab.noType;
					}
					paramCounted = true;
			 } 
		}
		
		Obj arrObj = expression.getDesignator1().obj;
		if(arrObj.getKind() != Obj.Var || arrObj.getType().getKind() != Struct.Array || !arrObj.getType().getElemType().equals(Tab.intType)) {
			reportError(arrObj.getName() + " is not an array of int ", expression);
			expression.struct = Tab.noType;
		}	
	}
	
	@Override
	public void visit(DesignatorBase db) {
		String currDesignatorName = db.getI1();
		Obj search = Tab.find(currDesignatorName);
		if(search == Tab.noObj) {
			reportError("Undeclared data unit " + currDesignatorName + " ", db);
			db.obj = Tab.noObj;
		}
		else
			if(search.getKind() == Obj.Con || search.getKind() == Obj.Var || search.getKind() == Obj.Meth)
				db.obj = search;
			else
			{
				reportError("Invalid designator kind " + currDesignatorName + " ", db);
				db.obj = Tab.noObj;
			}
	}
	
	//used for array types, points to Var Obj node
	@Override
	public void visit(DesignatorName dn) {
		Obj search = Tab.find(dn.getI1());
		if(search == Tab.noObj) {
			reportError("Undeclared data unit " + currDesignatorName + " ", dn);
			dn.obj = Tab.noObj;
		}
		else
			if(search.getKind() == Obj.Var && search.getType().getKind() == Struct.Array) 
				dn.obj = search;
			else
			{
				reportError("Invalid designator " + currDesignatorName + " ", dn);
				dn.obj = Tab.noObj;
			}
	}
	
	
	//points to Elem Obj node
	@Override
	public void visit(DesignatorArr da) {
		Obj designatorNameObj = da.getDesignatorName().obj;
		boolean exprIsInt = da.getExpr().struct.equals(Tab.intType);
		if(designatorNameObj != Tab.noObj && exprIsInt) {
			da.obj = new Obj(Obj.Elem, designatorNameObj.getName() + " elem", designatorNameObj.getType().getElemType());
		}
		else{
			da.obj = Tab.noObj;
			if(!exprIsInt)
				reportError("Array index not of int type ", da);
		}
	}
	
	@Override
	public void visit(DesStmtAssExpr dsae) {
		Obj desObj = dsae.getDesignator().obj;
		if(desObj.getKind() != Obj.Var && desObj.getKind() != Obj.Elem)
			reportError("Cannot assign value to object ", dsae);
		if(!dsae.getExpr().struct.assignableTo(desObj.getType()))
			reportError("Expression not assignable ", dsae);
	}
	
	
	//pozivanje globalne f-je
	@Override
	public void visit(DesStmtPars dsp) {
		Obj desObj = dsp.getDesignator().obj;
		if(desObj.getKind() != Obj.Meth)
			reportError("Non-method called ", dsp);
		else {
			methodLocals = new ArrayList<>();
			 for(Obj local : desObj.getLocalSymbols()) {
				 if(local.getKind() == Obj.Var && local.getFpPos()==1)
					methodLocals.add(local.getType());
			 } 
			 ArrayList<Struct> actParList = actParLists.pop();
			 if(methodLocals.size()!=actParList.size())
				 reportError("Number of parameters not matching '" + desObj.getName() + "' function declaration " , dsp);
			 else
				 //reportInfo(Integer.toString(currConstVal), dsp);
				 for (int i = 0; i < methodLocals.size(); i++) {
					 if(!actParList.get(i).assignableTo(methodLocals.get(i)))
						 reportError("Parameter type not matching ", dsp);
				 }
		}
	}
	
	@Override
	public void visit(DesStmtInc dsi) {
		Obj desObj = dsi.getDesignator().obj;
		if(desObj.getKind() != Obj.Var && desObj.getKind() != Obj.Elem)
			reportError("Cannot increment object ", dsi);
		if(!desObj.getType().equals(Tab.intType))
			reportError("Cannot increment non-int type ", dsi);
	}
	
	@Override
	public void visit(DesStmtDec dsd) {
		Obj desObj = dsd.getDesignator().obj;
		if(desObj.getKind() != Obj.Var && desObj.getKind() != Obj.Elem)
			reportError("Cannot decrement object ", dsd);
		if(!desObj.getType().equals(Tab.intType))
			reportError("Cannot decrement non-int type ", dsd);
	}
	
	@Override
	public void visit(DesStmtUnion statement) {
		if(!statement.getDesignator().obj.getType().equals(setType))
			reportError("Cannot assign union of sets to non-set type " + statement.getDesignator().obj.getName() + " ", statement);
		if(!statement.getDesignator1().obj.getType().equals(setType))
			reportError("Cannot perform union of sets on non-set type " + statement.getDesignator1().obj.getName() + " ", statement);
		if(!statement.getDesignator2().obj.getType().equals(setType))
			reportError("Cannot perform union of sets on non-set type " + statement.getDesignator2().obj.getName() + " ", statement);
	}
	
	
	@Override
	public void visit(Do d) {
		loopLevel++;
	}
	
	@Override
	public void visit(While w) {
		loopLevel--;
		if(loopLevel < 0)
			reportError("Unpaired while ", w); //should be caught during syntax analysis
	}
	
	@Override
	public void visit(StmtCont sc) {
		if(loopLevel <= 0) 
			reportError("Continue not allowed outside loops ", sc);
	}
	
	@Override
	public void visit(StmtBreak sb) {
		if(loopLevel <= 0) 
			reportError("Break not allowed outside loops ", sb);
	}
	
	@Override
	public void visit(StmtRead sr) {
		Obj desObj = sr.getDesignator().obj;
		if(desObj.getKind() != Obj.Var && desObj.getKind() != Obj.Elem)
			reportError("Cannot read object ", sr);
		if( !desObj.getType().equals(Tab.intType) &&
			!desObj.getType().equals(Tab.charType) &&
			!desObj.getType().equals(boolType))
			reportError("Cannot read type ", sr);
	}
	
	@Override
	public void visit(StmtPrint sp) {
		Struct exprStruct = sp.getExpr().struct;
		if( 	!exprStruct.equals(Tab.intType) &&
				!exprStruct.equals(Tab.charType) &&
				!exprStruct.equals(boolType) &&
				!exprStruct.equals(setType) )
			reportError("Cannot print type ", sp);
	}
	
	@Override
	public void visit(StmtPrintW spw) {
		Struct exprStruct = spw.getExpr().struct;
		if( 	!exprStruct.equals(Tab.intType) &&
				!exprStruct.equals(Tab.charType) &&
				!exprStruct.equals(boolType) &&
				!exprStruct.equals(setType) )
			reportError("Cannot print type ", spw);
	}	
	
	@Override
	public void visit(StmtRet sr) {
		if(currMeth == null) {
			reportError("Return only allowed from function ", sr);
			return;
		}
		if(currMeth.getType() != Tab.noType)
			reportError("Must return value ", sr);
	}
	
	@Override
	public void visit(StmtRetExpr sre) {
		if(currMeth == null) {
			reportError("Return only allowed from function ", sre);
			return;
		}
		if(currMeth.getType() != sre.getExpr().struct)
			reportError("Must return method type ", sre);
		returnExists = true;
	}
	
	@Override
	public void visit(RelopEqu re) {
		relopArrCompliant = true;
	}
	
	@Override
	public void visit(RelopNeq rn) {
		relopArrCompliant = true;
	}
	
	@Override
	public void visit(CondFactNoOp cf) {
		if(!cf.getExpr().struct.equals(boolType)) {
			reportError("Condition factor not of bool type ", cf);
			cf.struct = Tab.noType;
		}
		else
			cf.struct = boolType;
	}
	
	@Override
	public void visit(CondFactOp cf) {
		if(!cf.getExpr().struct.compatibleWith(cf.getExpr1().struct)) {
			reportError("Relational operands not compatible  ", cf);
			cf.struct = Tab.noType;
		}
		else {
			if( (cf.getExpr().struct.isRefType() || cf.getExpr1().struct.isRefType()) && !relopArrCompliant){
				reportError("Only EQUAL and NOT EQUAL operators permitted for arrays  ", cf);
				cf.struct = Tab.noType;
			}
			else
				cf.struct = boolType;
		}
		relopArrCompliant = false;
	}
	
	@Override
	public void visit(CondTermBase ct) {
		if(!ct.getCondFact().struct.equals(boolType)) {
			//reportError("Term factor not of bool type ", ct);
			ct.struct = Tab.noType;
		}
		else
			ct.struct = boolType;
	}
	
	@Override
	public void visit(CondTermRec ct) {
		if(!ct.getCondTermList().struct.equals(boolType) || !ct.getCondFact().struct.equals(boolType)) {
			//reportError("Conjunction permitted for bool types only ", ct);
			ct.struct = Tab.noType;
		}
		else
			ct.struct = boolType;
	}
	
	public void visit(CondTerm ct) {
		ct.struct = ct.getCondTermList().struct;
	}
	
	@Override
	public void visit(ConditionBase cond) {
		if(!cond.getCondTerm().struct.equals(boolType)) {
			//reportError("Condition term not of bool type", cond);
			cond.struct = Tab.noType;
		}
		else
			cond.struct = boolType;
	}
	
	@Override
	public void visit(ConditionRec cond) {
		if(!cond.getConditionList().struct.equals(boolType) || !cond.getCondTerm().struct.equals(boolType)) {
			//reportError("Disjunction permitted for bool types only ", ct);
			cond.struct = Tab.noType;
		}
		else
			cond.struct = boolType;
	}
	
	public void visit(Condition ct) {
		ct.struct = ct.getConditionList().struct;
	}
	
	//function called with no parameters
	@Override 
	public void visit(ParenParsParen ppp) {
		actParLists.push(new ArrayList<>()) ;
	}
	
	
	@Override
	public void visit(ParenParsPars ppp) {
	}
	
	@Override
	public void visit(ActParsBase apb) {
		ArrayList<Struct> actParList = new ArrayList<>();
		actParList.add(apb.getExpr().struct);
		actParLists.push(actParList);
	}
	
	//should've been done using peek(), oh well
	@Override
	public void visit(ActParsRec apr) {
		ArrayList<Struct> actParList = actParLists.pop();
		actParList.add(apr.getExpr().struct);
		actParLists.push(actParList);
	}
	
}