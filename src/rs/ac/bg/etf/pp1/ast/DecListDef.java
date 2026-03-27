// generated with ast extension for cup
// version 0.8
// 23/0/2025 13:8:50


package rs.ac.bg.etf.pp1.ast;

public class DecListDef extends DeclList {

    private DeclList DeclList;
    private DecListItem DecListItem;

    public DecListDef (DeclList DeclList, DecListItem DecListItem) {
        this.DeclList=DeclList;
        if(DeclList!=null) DeclList.setParent(this);
        this.DecListItem=DecListItem;
        if(DecListItem!=null) DecListItem.setParent(this);
    }

    public DeclList getDeclList() {
        return DeclList;
    }

    public void setDeclList(DeclList DeclList) {
        this.DeclList=DeclList;
    }

    public DecListItem getDecListItem() {
        return DecListItem;
    }

    public void setDecListItem(DecListItem DecListItem) {
        this.DecListItem=DecListItem;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
        if(DeclList!=null) DeclList.accept(visitor);
        if(DecListItem!=null) DecListItem.accept(visitor);
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
        if(DeclList!=null) DeclList.traverseTopDown(visitor);
        if(DecListItem!=null) DecListItem.traverseTopDown(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        if(DeclList!=null) DeclList.traverseBottomUp(visitor);
        if(DecListItem!=null) DecListItem.traverseBottomUp(visitor);
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("DecListDef(\n");

        if(DeclList!=null)
            buffer.append(DeclList.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        if(DecListItem!=null)
            buffer.append(DecListItem.toString("  "+tab));
        else
            buffer.append(tab+"  null");
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [DecListDef]");
        return buffer.toString();
    }
}
