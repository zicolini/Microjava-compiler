// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class DesStmtPars extends DesignatorStatement {

    private Designator Designator;
    private ParenPars ParenPars;

    public DesStmtPars (Designator Designator, ParenPars ParenPars) {
        this.Designator=Designator;
        if(Designator!=null) Designator.setParent(this);
        this.ParenPars=ParenPars;
        if(ParenPars!=null) ParenPars.setParent(this);
    }

    public Designator getDesignator() {
        return Designator;
    }

    public void setDesignator(Designator Designator) {
        this.Designator=Designator;
    }

    public ParenPars getParenPars() {
        return ParenPars;
    }

    public void setParenPars(ParenPars ParenPars) {
        this.ParenPars=ParenPars;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(Designator!=null) Designator.accept(visitor);
        if(ParenPars!=null) ParenPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(Designator!=null) Designator.traverseTopDown(visitor);
        if(ParenPars!=null) ParenPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(Designator!=null) Designator.traverseBottomUp(visitor);
        if(ParenPars!=null) ParenPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DesStmtPars(\n");

        if(Designator!=null)
            buffer.append(Designator.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(ParenPars!=null)
            buffer.append(ParenPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DesStmtPars]");
        return buffer.toString();
    }
}
