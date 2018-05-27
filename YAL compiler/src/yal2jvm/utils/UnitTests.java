package yal2jvm.utils;

import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

//Return codes:
//-1  Unspecified error
//-2  Syntactical/Lexical error
//-3  Semantical error
//-4  File not found error
//-5  Invalid arguments error

public class UnitTests
{
	private ArrayList<String> testAllFilesInFolder(String path)
	{
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		ArrayList<String> log = new ArrayList<>();

		assert listOfFiles != null;
		for (File listOfFile : listOfFiles) {
			String file = listOfFile.toString();

			if (!file.endsWith(".yal"))
				continue;

			int retVal;
			try {
				retVal = Runtime.getRuntime().exec("java -cp ./bin yal2jvm.Yal2jvm " + file).waitFor();
			} catch (InterruptedException | IOException e) {
				log.add(file + ": error calling compiler\n");
				continue;
			}

			if (retVal == -2) {
				log.add(file + ": syntactic error(s)\n");
				continue;
			} else if (retVal == -3) {
				log.add(file + ": semantic error(s)\n");
				continue;
			} else {
				if (path.contains("semantic")) {
					log.add(file + ": semantic analysis successful\n");
					continue;
				}
			}

			try {
				String classfile = file.replace(".yal", "");
				classfile = classfile.substring(classfile.lastIndexOf('\\') + 1, classfile.length());
				System.out.println("Running class file " + classfile);
				retVal = Runtime.getRuntime().exec("java -cp . " + classfile).waitFor();

				File del = new File(classfile);
				del.delete();
			} catch (InterruptedException | IOException e) {
				log.add(file + ": error calling JVM\n");
				continue;
			}

			if (retVal != 0) {
				log.add(file + ": error during .class execution " + retVal + " \n");
				continue;
			}
			log.add(file + ": successful compilation and execution\n");
		}

		for (String aLog : log) System.out.print(aLog);
		System.out.println("");
		
		return log;
	}
	
	@Test
	public void semanticNoErrors()
	{
		ArrayList<String> log = testAllFilesInFolder("examples/semantic_no_errors");

		for (String aLog : log) assertEquals(true, aLog.contains("semantic analysis successful"));
	}
	
	@Test
	public void semanticWithErrors()
	{
		ArrayList<String> log = testAllFilesInFolder("examples/semantic_errors");

		for (String aLog : log) assertEquals(true, aLog.contains("error"));
	}
	
	@Test
	public void compileAndRunNoErrors()
	{
		ArrayList<String> log = testAllFilesInFolder("examples/code_generation");

		for (String aLog : log) assertEquals(true, aLog.contains("successful compilation and execution"));
	}
}
