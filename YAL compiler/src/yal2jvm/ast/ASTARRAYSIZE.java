/* Generated By:JJTree: Do not edit this line. ASTARRAYSIZE.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public class ASTARRAYSIZE extends SimpleNode
{

	public Integer integer = null;

	public ASTARRAYSIZE(int id)
	{
		super(id);
	}

	public ASTARRAYSIZE(YalParser p, int id)
	{
		super(p, id);
	}

	public void dump(String prefix)
	{
		System.out.println(toString(prefix));
		if (children == null)
		{
			if (this.integer != null)
				System.out.println(prefix + "\"" + this.integer + "\"");
		}
		if (children != null)
		{
			if (this.integer != null)
				System.out.println(prefix + "\"" + this.integer + "\"");
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
 * JavaCC - OriginalChecksum=a238e909d76a3d4158925ca6da835f3c (do not edit this
 * line)
 */
