package yal2jvm.hlir;

public enum Comparator
{
	NEQ,	//!=
	EQ,		//==
	GT,		//>
	GTE,	//>=
	ST,		//<
	STE		//<=
;

	/**
	 *
	 * @param comp
	 * @return
	 */
	public static Comparator invert(Comparator comp)
	{
		switch(comp)
		{
		case EQ:
			return NEQ;
		case GT:
			return STE;
		case GTE:
			return ST;
		case NEQ:
			return EQ;
		case ST:
			return GTE;
		case STE:
			return GT;
		default:
			break;
		}
		return null;
	}
}
