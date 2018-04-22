package yal2jvm.HHIR;

import java.util.ArrayList;

public class IROperation extends IRNode
{

	private Operation operation;

	public IROperation(Operation op)
	{
		this.operation = op;
		this.nodeType = "Operation";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}
}
