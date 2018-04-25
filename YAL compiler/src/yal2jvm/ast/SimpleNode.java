/* Generated By:JJTree: Do not edit this line. SimpleNode.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public
class SimpleNode implements Node {

	  private Node parent;
	  protected Node[] children;
	  protected int id;
	  protected String value = "";
	  protected YalParser parser;
	  protected int beginLine = -1;

	  public SimpleNode(int i) {
	    id = i;
	  }

	  public SimpleNode(YalParser p, int i) {
	    this(i);
	    parser = p;
	  }

	  public void jjtOpen() {
	  }

	  public void jjtClose() {
	  }

	public void jjtSetParent(Node n) { parent = n; }
	  public Node jjtGetParent() { return parent; }

	  public void jjtAddChild(Node n, int i) {
	    if (children == null) {
	      children = new Node[i + 1];
	    } else if (i >= children.length) {
	      Node c[] = new Node[i + 1];
	      System.arraycopy(children, 0, c, 0, children.length);
	      children = c;
	    }
	    children[i] = n;
	  }

	  public Node jjtGetChild(int i) {
	    return children[i];
	  }

	  public int jjtGetNumChildren() {
	    return (children == null) ? 0 : children.length;
	  }

	public String jjtGetValue() { return value; }

	  /* You can override these two methods in subclasses of SimpleNode to
	     customize the way the node appears when the tree is dumped.  If
	     your output uses more than one line you should override
	     toString(String), otherwise overriding toString() is probably all
	     you need to do. */

	  public String toString() { return YalParserTreeConstants.jjtNodeName[id]; }
	  public String toString(String prefix) { return prefix + toString(); }

	  /* Override this method if you want to customize how the node dumps
	     out its children. */

	  public void dump(String prefix) {
	  	System.out.println(toString(prefix));
		if (children == null) {
			System.out.println(prefix + "\"" + this.value + "\"");
		}
		if (children != null) {
			if (!this.value.equals(""))
				System.out.println(prefix + "\"" + this.value + "\"");
			for (Node aChildren : children) {
				SimpleNode n = (SimpleNode) aChildren;
				if (n != null) {
					n.dump(prefix + " ");
				}
			}
		}
	  }
	}

/* JavaCC - OriginalChecksum=6b77298fe96954feb69b7e0ff03aa57b (do not edit this line) */
