package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRStoreArith extends IRNode
{

	private String name;

	public IRStoreArith(String name)
	{
		this.name = name;
		this.nodeType = "StoreArith";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
