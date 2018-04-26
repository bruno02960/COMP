package yal2jvm.HHIR;

import javafx.util.Pair;

import java.util.ArrayList;

public class IRCall extends IRNode
{
	private String method;
	private String module;
	private ArrayList<PairStringType> arguments;

	public IRCall(String method, String module, ArrayList<PairStringType> arguments)
	{
		this.method = method;
		this.module = module;
		this.arguments = arguments;
		this.nodeType = "Call";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		return inst;
	}

}
