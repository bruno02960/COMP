package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRCall extends IRNode
{

	private String method;
	private String module;
	private String[] argIDs;

	public IRCall(String method, String module, String[] argIDs)
	{
		this.method = method;
		this.module = module;
		this.argIDs = argIDs;
		this.nodeType = "Call";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
