// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class ConstListBase extends ConstList {

    private String I1;
    private ConstToken ConstToken;

    public ConstListBase (String I1, ConstToken ConstToken) {
        this.I1=I1;
        this.ConstToken=ConstToken;
        if(ConstToken!=null) ConstToken.setParent(this);
    }

    public String getI1() {
        return I1;
    }

    public void setI1(String I1) {
        this.I1=I1;
    }

    public ConstToken getConstToken() {
        return ConstToken;
    }

    public void setConstToken(ConstToken ConstToken) {
        this.ConstToken=ConstToken;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(ConstToken!=null) ConstToken.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstToken!=null) ConstToken.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstToken!=null) ConstToken.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstListBase(\n");

        buffer.append(" "+tab+I1);
        buffer.append("\n");

        if(ConstToken!=null)
            buffer.append(ConstToken.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstListBase]");
        return buffer.toString();
    }
}
