package yal2jvm.hlir.liveness_analysis;

import java.util.ArrayList;
import java.util.TreeSet;

import yal2jvm.utils.Utils;

public class MethodInstructions extends Method
{
	public ArrayList<String> getAllVars()
	{
		TreeSet<String> vars = new TreeSet<>();
		
		for (String line : lines)
		{
			if (line.contains("load"))
			{
				vars.add(line.substring(6));
			}
			else if (line.contains("store"))
			{
				vars.add(line.substring(7));
			}
		}
		return Utils.setToList(vars);
	}

	public String getName()
	{
		String s = lines.get(0);
		int beginIndex = ".method public static ".length();
		int endIndex = s.indexOf("(");
		return s.substring(beginIndex, endIndex);
	}

	@Override
	public Line getNextLine()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void buildAllLines()
	{
		// TODO Auto-generated method stub
		
	}
}
