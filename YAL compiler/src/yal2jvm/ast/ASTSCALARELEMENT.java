/* Generated By:JJTree: Do not edit this line. ASTSCALARELEMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public
class ASTSCALARELEMENT extends SimpleNode {
  public String id = "";
	
  public ASTSCALARELEMENT(int id) {
    super(id);
  }

  public ASTSCALARELEMENT(YalParser p, int id) {
    super(p, id);
  }
  
	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children == null) {
			System.out.println(prefix + "\"" + this.id + "\"");
		}
		if (children != null) {
			if (!this.id.equals(""))
				System.out.println(prefix + "\"" + this.id + "\"");
			for (Node aChildren : children) {
				SimpleNode n = (SimpleNode) aChildren;
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}

}
/* JavaCC - OriginalChecksum=23516b9736804c0aa3da9178894e101f (do not edit this line) */
