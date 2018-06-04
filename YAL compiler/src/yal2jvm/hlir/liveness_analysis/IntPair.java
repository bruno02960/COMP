package yal2jvm.hlir.liveness_analysis;

/**
 *
 */
public class IntPair
{
	private String var1;
	private String var2;

	/**
	 *
	 * @param var1
	 * @param var2
	 */
	public IntPair(String var1, String var2)
	{
		this.var1 = var1;
		this.var2 = var2;
	}

	/**
	 *
	 * @return
	 */
	public String getVar1()
	{
		return var1;
	}

	/**
	 *
	 * @param var1
	 */
	public void setVar1(String var1)
	{
		this.var1 = var1;
	}

	/**
	 *
	 * @return
	 */
	public String getVar2()
	{
		return var2;
	}

	/**
	 *
	 * @param var2
	 */
	public void setVar2(String var2)
	{
		this.var2 = var2;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		return var1 + "-" + var2;
	}
}
