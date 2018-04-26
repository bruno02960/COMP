package test;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import yal2jvm.Yal2jvm;

public class UnitTests
{

	@Test
	public void compileAndRunCompiledClass()
	{
		Yal2jvm compiler = new Yal2jvm(255, false, "test/test2.yal");
		compiler.run();

		int retVal = -1;
		try {
			retVal = Runtime.getRuntime().exec("java -cp . Module1").waitFor();
		} catch (InterruptedException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(retVal, 0);
	}
}
