package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{
	private String name;
	private Type type;
	private int value;
	private int register = -1;
	int size = -1;

	public IRAllocate(String name, Type type, Integer value)
	{
		this.nodeType = "Allocate";
		this.name = name;
		assert type==Type.INTEGER;
		this.type = type;
		this.value = value == null ? 0 : value;
	}

	public IRAllocate(String name, Type type, Integer value, Integer size)
	{
		this.nodeType = "Allocate";
		this.name = name;
		assert type==Type.ARRAY;
		this.type = type;
		this.value = value == null ? 0 : value;
		this.size = size;
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		/*
		String label1 = "Label" + ((IRMethod)parent).labelN;
		((IRMethod)parent).labelN++;
		String label2 = "Label" + ((IRMethod)parent).labelN;
		((IRMethod)parent).labelN++;
		
		String inst1 = ".var " + ((IRMethod)parent).varN + " is " + name;
		switch(type)
		{
			case INTEGER:
			{
				inst1 += " I ";
				break;
			}
			case ARRAY: break;

			default:
				break;
		}
		inst1 += " from " + label1 + " to " + label2;
		String inst2 = label1 + ":";
		String inst3 = "ldc " + (value == null? 0 : value);
		String inst4 = label2 + ":";
		
		((IRMethod)parent).incrementRegN();
		
		inst.add(inst1);
		inst.add(inst2);
		inst.add(inst3);
		inst.add(inst4);*/
		
		initRegister();
		
		switch(type)
		{
			case INTEGER:
			{
				inst.add("ldc " + this.value);
				inst.add("istore " + this.register);
				break;
			}
			case ARRAY: break;

			default:
				break;
		}
		
		return inst;
	}

	private void initRegister()
	{
		if (register == -1)
		{
			this.register = ((IRMethod)parent).getRegN();
			((IRMethod)parent).incrementRegN();
		}
	}
	
	public int getRegister() 
	{
		initRegister();
		return register;
	}

	public void setRegister(int register) 
	{
		this.register = register;
	}

	public String getName() 
	{
		return name;
	}

	public void setName(String name) 
	{
		this.name = name;
	}

}
