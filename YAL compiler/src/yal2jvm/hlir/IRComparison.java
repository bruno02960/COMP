package yal2jvm.hlir;

import java.util.ArrayList;

public class IRComparison extends IRNode
{
	private Comparator comp;
	private IRNode rhs;
	private IRNode lhs;
	private String label;

	public IRComparison(Comparator comp, String label, boolean invert)
	{
		this.comp = invert ? Comparator.invert(comp) : comp;
		this.label = label;
		this.setNodeType("Comparison");
	}

	public IRComparison(String operator, String label, boolean invert)
	{
		Comparator comp = getComparatorGivenOperator(operator);
		this.comp = invert ? Comparator.invert(comp) : comp;
		this.label = label;
		this.setNodeType("Comparison");
	}

	private Comparator getComparatorGivenOperator(String operator)
	{
		switch (operator)
		{
			case ">":
				return Comparator.GT;

			case "<":
				return Comparator.ST;

			case "<=":
				return Comparator.STE;

			case ">=":
				return Comparator.GTE;

			case "==":
				return Comparator.EQ;

			case "!=":
				return Comparator.NEQ;

			default:
				System.out.println("Unrecognized relational operator " + operator + ". Compile program will terminate.");
				System.exit(-1);
		}

		return null; //unreachable
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		String branchInst = "";
		
		if (isConstantZero(rhs))
		{
			inst.addAll(lhs.getInstructions());
			
			branchInst = getZeroComparison();
		}
		else if (useArrayOperations())
		{
			inst.addAll(lhs.getInstructions());
			inst.addAll(rhs.getInstructions());

			branchInst = getArrayComparison();
		}
		else
		{
			inst.addAll(lhs.getInstructions());
			inst.addAll(rhs.getInstructions());
			
			branchInst = getIntegerComparison();
		}
		branchInst += " " + label;
		
		inst.add(branchInst);
		return inst;
	}
	
	boolean isConstantZero(IRNode node)
	{
		if (node.getNodeType().equals("Constant"))
		{
			IRConstant constant = (IRConstant)node;
			if (constant.getValue().equals("0"))
				return true;
		}
		return false;
	}

	private boolean useArrayOperations()
	{
		if (rhs.getNodeType().equals("Constant") || rhs.getNodeType().equals("Constant"))
			return false;

		if (rhs.getNodeType().equals("Load"))
		{
			IRLoad load = (IRLoad)rhs;
			if (load.getType() != Type.ARRAY || load.isArraySizeAccess())
				return false;

			if (lhs.getNodeType().equals("Load"))
			{
				load = (IRLoad)lhs;
				if (load.getType() == Type.ARRAY && load.isArraySizeAccess() == false)
					return true;
			}
		}

		return false;
	}
	
	public String getZeroComparison()
	{
		String branchInst = "";
		switch(comp)
		{
		case EQ:
			branchInst = "ifeq";
			break;
		case GT:
			branchInst = "ifgt";
			break;
		case GTE:
			branchInst = "ifge";
			break;
		case NEQ:
			branchInst = "ifne";
			break;
		case ST:
			branchInst = "iflt";
			break;
		case STE:
			branchInst = "ifle";
			break;
		default:
			break;
		}
		return branchInst;
	}
	
	public String getArrayComparison()
	{
		String branchInst = "";
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
		return branchInst;
	}
	
	public String getIntegerComparison()
	{
		String branchInst = "";
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
		return branchInst;
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

	public String getLabel()
	{
		return label;
	}

	public void setLabel(String label)
	{
		this.label = label;
	}
}
