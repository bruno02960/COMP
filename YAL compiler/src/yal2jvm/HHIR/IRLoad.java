package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRLoad extends IRNode
{

	private String name;
	private int register;

	public IRLoad(String name)
	{
		this.name = name;
		this.nodeType = "Load";
	}

	public int getRegister()
	{
		return register;
	}

	public void setRegister(int register)
	{
		this.register = register;
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
