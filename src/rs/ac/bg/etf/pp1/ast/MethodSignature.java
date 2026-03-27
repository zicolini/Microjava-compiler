// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class MethodSignature implements SyntaxNode {

    private SyntaxNode parent;
    private int line;
    private MethType MethType;
    private MethName MethName;
    private MethPars MethPars;

    public MethodSignature (MethType MethType, MethName MethName, MethPars MethPars) {
        this.MethType=MethType;
        if(MethType!=null) MethType.setParent(this);
        this.MethName=MethName;
        if(MethName!=null) MethName.setParent(this);
        this.MethPars=MethPars;
        if(MethPars!=null) MethPars.setParent(this);
    }

    public MethType getMethType() {
        return MethType;
    }

    public void setMethType(MethType MethType) {
        this.MethType=MethType;
    }

    public MethName getMethName() {
        return MethName;
    }

    public void setMethName(MethName MethName) {
        this.MethName=MethName;
    }

    public MethPars getMethPars() {
        return MethPars;
    }

    public void setMethPars(MethPars MethPars) {
        this.MethPars=MethPars;
    }

    public SyntaxNode getParent() {
        return parent;
    }

    public void setParent(SyntaxNode parent) {
        this.parent=parent;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line=line;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(MethType!=null) MethType.accept(visitor);
        if(MethName!=null) MethName.accept(visitor);
        if(MethPars!=null) MethPars.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(MethType!=null) MethType.traverseTopDown(visitor);
        if(MethName!=null) MethName.traverseTopDown(visitor);
        if(MethPars!=null) MethPars.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(MethType!=null) MethType.traverseBottomUp(visitor);
        if(MethName!=null) MethName.traverseBottomUp(visitor);
        if(MethPars!=null) MethPars.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("MethodSignature(\n");

        if(MethType!=null)
            buffer.append(MethType.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethName!=null)
            buffer.append(MethName.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(MethPars!=null)
            buffer.append(MethPars.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [MethodSignature]");
        return buffer.toString();
    }
}
