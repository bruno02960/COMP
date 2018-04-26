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
		
		ArrayList<String> storeInst = getInstForStoring();
		
		inst.addAll(rhsInst);
		inst.addAll(lhsInst);
		inst.add(opInst);
		inst.addAll(storeInst);
		return inst;
	}

	private ArrayList<String> getInstForStoring()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		switch(parent.toString())
		{
			case "Method":
			{
				int storeReg = -1;
				ArrayList<IRNode> methodChildren = parent.getChildren();
				
				//check if storage variable exists, and if so get its register
				for (int i = 0; i < methodChildren.size(); i++)
				{
					if (methodChildren.get(i).toString().equals("Allocate"))
					{
						storeReg = ((IRAllocate)methodChildren.get(i)).getRegister();
						break;
					}
				}
				//if not, check if it is one of the method's arguments
				storeReg = ((IRMethod)parent).getArgumentRegister(name);
				
				//if storage variable does not exist, allocate it
				if (storeReg == -1)
				{
					IRAllocate storeVar = new IRAllocate(name, Type.INTEGER, 0);
					parent.addChild(storeVar);
					storeVar.getRegister();
				}
				
				inst.add("istore " + storeReg);
				break;
			}
			default:
				break;
		}
		
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
