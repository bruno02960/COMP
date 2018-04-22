package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRStoreArith extends IRNode
{
	private String name;
	private Operation op;
	private IRNode rhs, lhs;

	public IRStoreArith(String name, Operation op)
	{
		this.name = name;
		this.op = op;
		this.nodeType = "StoreArith";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		ArrayList<String> rhsInst = rhs.getInstructions();
		ArrayList<String> lhsInst = lhs.getInstructions();
		String opInst = null;
		
		//TODO: add iinc later + add NOT
		switch(op)
		{
			case ADD: opInst = "iadd"; break;
			case SUB: opInst = "isub"; break;
			case MULT: opInst = "imul"; break;
			case DIV: opInst = "idiv"; break;
			case SHIFT_R: opInst = "ishr"; break;
			case SHIFT_L: opInst = "ishl"; break;
			case USHIFT_R: opInst = "iushl"; break;
			case AND: opInst = "iand"; break;
			case OR: opInst = "ior"; break;
		}
		//code for storing
		
		inst.addAll(rhsInst);
		inst.addAll(lhsInst);
		inst.add(opInst);
		return inst;
	}

	public IRNode getRhs()
	{
		return rhs;
	}

	public void setRhs(IRNode rhs)
	{
		this.rhs = rhs;
	}

	public IRNode getLhs()
	{
		return lhs;
	}

	public void setLhs(IRNode lhs)
	{
		this.lhs = lhs;
	}
}
