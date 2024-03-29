/* Generated By:JJTree: Do not edit this line. ASTCALL.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public class ASTCALL extends SimpleNode
{

	public String module;
	public String method;

	public ASTCALL(int id)
	{
		super(id);
	}

	public ASTCALL(YalParser p, int id)
	{
		super(p, id);
	}

	public void dump(String prefix)
	{
		System.out.println(toString(prefix));
		if (children == null)
		{
			if (this.module != null)
				System.out.println(prefix + "\"" + this.method + " ON MODULE " + this.module + "\"");
			else
				System.out.println(prefix + "\"" + this.method + "\"");
		}
		if (children != null)
		{
			if (this.module != null)
				System.out.println(prefix + "\"" + this.method + " ON MODULE " + this.module + "\"");
			else
				System.out.println(prefix + "\"" + this.method + "\"");
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
 * JavaCC - OriginalChecksum=38eb0a4d10c26f9cc1cc1d16c4d72dc8 (do not edit this
 * line)
 */
