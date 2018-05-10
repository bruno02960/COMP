package yal2jvm.hhir;

import java.util.ArrayList;

public class IRComparison extends IRNode
{
	private Comparator comp;
	private IRNode rhs;
	private IRNode lhs;
	private String label;

	public IRComparison(Comparator comp, String label)
	{
		this.comp = comp;
		this.label = label;
		this.nodeType = "Comparison";
	}
	
	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		inst.addAll(rhs.getInstructions());
		inst.addAll(lhs.getInstructions());
		
		String branchInst = "";
		
		if (useArrayOperations())
		{
			switch(comp)
			{
			case EQ:
				branchInst = "if_acmpeq";
				break;
			case NEQ:
				branchInst = "if_acmpne";
				break;
			default:
				break;
			}
		}
		else
		{
			switch(comp)
			{
			case EQ:
				branchInst += "if_icmpeq";
				break;
			case GT:
				branchInst += "if_icmpgt";
				break;
			case GTE:
				branchInst += "if_icmpge";
				break;
			case NEQ:
				branchInst += "if_icmpne";
				break;
			case ST:
				branchInst += "if_icmplt";
				break;
			case STE:
				branchInst += "if_icmple";
				break;
			default:
				break;
			}
		}
		branchInst += " " + label;
		
		inst.add(branchInst);
		return inst;
	}

	private boolean useArrayOperations()
	{
		if (rhs.nodeType.equals("Constant") || rhs.nodeType.equals("Constant"))
		{
			return false;
		}
		if (rhs.nodeType.equals("Load"))
		{
			IRLoad load = (IRLoad)rhs;
			if (load.getType() == Type.ARRAY)
				return true;
		}
		if (lhs.nodeType.equals("Load"))
		{
			IRLoad load = (IRLoad)lhs;
			if (load.getType() == Type.ARRAY)
				return true;
		}
		return false;
	}

	public IRNode getRhs()
	{
		return rhs;
	}

	public void setRhs(IRNode rhs)
	{
		this.rhs = rhs;
		this.rhs.setParent(this);
	}

	public IRNode getLhs()
	{
		return lhs;
	}

	public void setLhs(IRNode lhs)
	{
		this.lhs = lhs;
		this.lhs.setParent(this);
	}
}
