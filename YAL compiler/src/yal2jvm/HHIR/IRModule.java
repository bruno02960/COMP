package yal2jvm.HHIR;

import java.util.ArrayList;

public class IRModule extends IRNode
{

	private String name;

	public IRModule(String name)
	{
		super();
		this.setName(name);
		this.nodeType = "Module";
	}

	@Override
	public ArrayList<String> getInstructions()
	{
		ArrayList<String> inst = new ArrayList<>();
		
		String inst1 = ".class public static " + name;
		String inst2 = ".super java/lang/Object";
		
		inst.add(inst1);
		inst.add(inst2);/*
		inst.add(".method public <init>()V");
		inst.add("aload_0");
		inst.add("invokevirtual java/lang/Object/<init>()V");
		inst.add("return");
		inst.add(".end method");*/
		
		return inst;
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
