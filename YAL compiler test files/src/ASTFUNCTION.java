/* Generated By:JJTree: Do not edit this line. ASTFUNCTION.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
public class ASTFUNCTION extends SimpleNode {
	
	public String id = "";
	
	public ASTFUNCTION(int id) {
		super(id);
	}

	public ASTFUNCTION(YalParser p, int id) {
		super(p, id);
	}

	public void dump(String prefix) {
		System.out.println(toString(prefix));
		if (children == null) {
			System.out.println(prefix + "\"" + this.id + "\"");
		}
		if (children != null) {
			if (this.id != "")
				System.out.println(prefix + "\"" + this.id + "\"");
			for (int i = 0; i < children.length; ++i) {
				SimpleNode n = (SimpleNode) children[i];
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	}
}
/*
 * JavaCC - OriginalChecksum=ac2212b642d22fe46a8a2fcbba7f408d (do not edit this
 * line)
 */
