package yal2jvm.Utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import yal2jvm.Yal2jvm;

public class UnitTests
{
	@Test
	public void compileAndRunCompiledClass()
	{
		Yal2jvm compiler = new Yal2jvm(255, false, "examples/all.yal");
		compiler.run();

		int retVal = -1;
		try 
		{
			retVal = Runtime.getRuntime().exec("java -cp . all").waitFor();
		} catch (InterruptedException | IOException e) 
		{
			e.printStackTrace();
			fail();
		}
		assertEquals(retVal, 0);
	}
}
