/**
 * Test module for yal
 * @author up201503616
 *
 */
public class test 
{
	public static void doSomething()
	{
		System.out.println("Do something");
	} 
	
	public static int sqrt(int x)
	{
		return (int)Math.sqrt(x);
	}
	
	public static int atan(int x, int y)
	{
		return (int)Math.atan2(y, x);
	}
	
	public static void printArray(int[] array)
	{
		for (int i = 0; i < array.length; i++)
			System.out.println(array[i]);
	}
}
