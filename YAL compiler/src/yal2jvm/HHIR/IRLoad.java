package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRLoad extends IRNode
{

	private String name;
	private Scope scope;
	private int register;

	public IRLoad(String name, Scope scope)
	{
		this.name = name;
		this.scope = scope;
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
		
		switch(scope)
		{
			case GLOBAL:
			{
				break;
			}
			case LOCAL:
			{
				break;
			}
			case PARAMETER:
			{
				break;
			}
			default:
				break;
		}
		
		return inst;
	}

}
