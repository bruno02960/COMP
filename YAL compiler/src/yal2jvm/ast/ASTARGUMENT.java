/* Generated By:JJTree: Do not edit this line. ASTARGUMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public class ASTARGUMENT extends SimpleNode
{

	public String stringArg;
	public Integer intArg;
	public String idArg;

	public ASTARGUMENT(int id)
	{
		super(id);
	}

	public ASTARGUMENT(YalParser p, int id)
	{
		super(p, id);
	}

	public void dump(String prefix)
	{
		System.out.println(toString(prefix));
		if (children == null)
		{
			if (stringArg != null)
				System.out.println(prefix + "\"" + this.stringArg + "\"");
			else if (intArg != null)
				System.out.println(prefix + "\"" + this.intArg + "\"");
			else
				System.out.println(prefix + "\"" + this.idArg + "\"");
		}
		if (children != null)
		{
			if (stringArg != null)
				System.out.println(prefix + "\"" + this.stringArg + "\"");
			else if (intArg != null)
				System.out.println(prefix + "\"" + this.intArg + "\"");
			else
				System.out.println(prefix + "\"" + this.idArg + "\"");
			for (Node aChildren : children)
			{
				SimpleNode n = (SimpleNode) aChildren;
				if (n != null)
				{
					n.dump(prefix + " ");
				}
			}
		}
	}

}
/*
 * JavaCC - OriginalChecksum=8487cc95adcac2e8e9f0ee712d2eb9c5 (do not edit this
 * line)
 */
