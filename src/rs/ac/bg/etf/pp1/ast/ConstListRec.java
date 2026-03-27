// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class ConstListRec extends ConstList {

    private ConstList ConstList;
    private String I2;
    private ConstToken ConstToken;

    public ConstListRec (ConstList ConstList, String I2, ConstToken ConstToken) {
        this.ConstList=ConstList;
        if(ConstList!=null) ConstList.setParent(this);
        this.I2=I2;
        this.ConstToken=ConstToken;
        if(ConstToken!=null) ConstToken.setParent(this);
    }

    public ConstList getConstList() {
        return ConstList;
    }

    public void setConstList(ConstList ConstList) {
        this.ConstList=ConstList;
    }

    public String getI2() {
        return I2;
    }

    public void setI2(String I2) {
        this.I2=I2;
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
        if(ConstList!=null) ConstList.accept(visitor);
        if(ConstToken!=null) ConstToken.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(ConstList!=null) ConstList.traverseTopDown(visitor);
        if(ConstToken!=null) ConstToken.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(ConstList!=null) ConstList.traverseBottomUp(visitor);
        if(ConstToken!=null) ConstToken.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("ConstListRec(\n");

        if(ConstList!=null)
            buffer.append(ConstList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(" "+tab+I2);
        buffer.append("\n");

        if(ConstToken!=null)
            buffer.append(ConstToken.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [ConstListRec]");
        return buffer.toString();
    }
}
