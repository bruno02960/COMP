/* Generated By:JJTree: Do not edit this line. ASTARRAYELEMENT.java Version 4.3 */
/* JavaCCOptions:MULTI=true,NODE_USES_PARSER=false,VISITOR=false,TRACK_TOKENS=false,NODE_PREFIX=AST,NODE_EXTENDS=,NODE_FACTORY=,SUPPORT_CLASS_VISIBILITY_PUBLIC=true */
package yal2jvm.ast;

public class ASTARRAYELEMENT extends SimpleNode
{

	public String id = "";

	public ASTARRAYELEMENT(int id)
	{
		super(id);
	}

	public ASTARRAYELEMENT(YalParser p, int id)
	{
		super(p, id);
	}

	public void dump(String prefix)
	{
		System.out.println(toString(prefix));
		if (children == null)
		{
			System.out.println(prefix + "\"" + this.id + "\"");
		}
		if (children != null)
		{
			if (!this.id.equals(""))
				System.out.println(prefix + "\"" + this.id + "\"");
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
 * JavaCC - OriginalChecksum=c3842a7217ed272ca58ce1ed104de87f (do not edit this
 * line)
 */
