// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class FactorConst extends Factor {

    private ConstTokenF ConstTokenF;

    public FactorConst (ConstTokenF ConstTokenF) {
        this.ConstTokenF=ConstTokenF;
        if(ConstTokenF!=null) ConstTokenF.setParent(this);
    }

    public ConstTokenF getConstTokenF() {
        return ConstTokenF;
    }

    public void setConstTokenF(ConstTokenF ConstTokenF) {
        this.ConstTokenF=ConstTokenF;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstTokenF!=null) ConstTokenF.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstTokenF!=null) ConstTokenF.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstTokenF!=null) ConstTokenF.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("FactorConst(\n");

        if(ConstTokenF!=null)
            buffer.append(ConstTokenF.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [FactorConst]");
        return buffer.toString();
    }
}
