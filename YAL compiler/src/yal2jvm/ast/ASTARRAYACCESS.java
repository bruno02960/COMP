/* Generated By:JJTree: Do not edit this line. ASTARRAYACCESS.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public
class ASTARRAYACCESS extends SimpleNode {
	public String arrayID;
	
	public ASTARRAYACCESS(int id) {
	  super(id);
	}
	
	public ASTARRAYACCESS(YalParser p, int id) {
	  super(p, id);
	}
	
	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children == null) {
			System.out.println(prefix + "\"" + this.arrayID + "\"");
		}
		if (children != null) {
			if (this.arrayID != null)
				System.out.println(prefix + "\"" + this.arrayID + "\"");
			for (Node aChildren : children) {
				SimpleNode n = (SimpleNode) aChildren;
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
}
/* JavaCC - OriginalChecksum=6574219720363313732db25bd18012bc (do not edit this line) */
