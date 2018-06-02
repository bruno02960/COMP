package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.TreeSet;

import yal2jvm.utils.Utils;

public abstract class Method
{
	protected ArrayList<String> lines;
	protected int index = 0;
	
	public abstract ArrayList<String> getAllVars();

	public abstract Line getNextLine();
	
	public abstract String getName();

	public abstract void buildAllLines();
}
