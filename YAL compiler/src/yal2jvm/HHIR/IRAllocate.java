package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRAllocate extends IRNode
{

	private String name;
	private Type type;
	private Integer value;

	public IRAllocate(String name, Type type, Integer value)
	{
		this.nodeType = "Allocate";
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
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
		
		((IRMethod)parent).varN++;
		
		inst.add(inst1);
		inst.add(inst2);
		inst.add(inst3);
		inst.add(inst4);
		return inst;
	}

}
