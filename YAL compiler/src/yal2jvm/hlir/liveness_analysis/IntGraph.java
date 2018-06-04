package yal2jvm.hlir.liveness_analysis;

import java.io.*;
import java.util.ArrayList;

/**
 *
 */
public class IntGraph implements Serializable
{
	private ArrayList<IntNode> nodes;

	/**
	 *
	 * @param graph
	 */
	public IntGraph(IntGraph graph)
	{
        try
        {
            FileOutputStream fos = new FileOutputStream("tempdata.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(graph);
            oos.close();
        }
        catch (Exception ex)
        {
            System.out.println("Exception thrown during IntGraph copy: " + ex.toString());
            System.exit(-1);
        }

        try
        {
            FileInputStream fis = new FileInputStream("tempdata.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            IntGraph graphRead = (IntGraph) ois.readObject();
            nodes = graphRead.getNodes();
            ois.close();

            new File("tempdata.ser").delete();
        }
        catch (Exception ex)
        {
			System.out.println("Exception thrown during IntGraph copy: " + ex.toString());
			System.exit(-1);
        }
	}

	/**
	 *
	 */
	public IntGraph()
	{
		this.nodes = new ArrayList<>();
	}

	/**
	 *
	 * @param var1
	 * @param var2
	 */
	public void addInterference(String var1, String var2)
	{
		IntNode n1 = findNode(var1);
		IntNode n2 = findNode(var2);
		
		n1.addInterference(n2);
		n2.addInterference(n1);
	}

	/**
	 *
	 * @param node
	 */
	public void addNode(IntNode node)
	{
		ArrayList<IntNode> graphNodeInterferences = node.getInterferences();
		for(IntNode graphNodeToInterference : graphNodeInterferences)
		{
			graphNodeToInterference.addInterference(node);
		}

		nodes.add(node);
	}

	/**
	 *
	 * @param node
	 */
	public void removeNode(IntNode node)
	{
		int nodeIndex = nodes.indexOf(node);
		IntNode graphNodeToRemove = nodes.remove(nodeIndex);

		ArrayList<IntNode> graphNodeToRemoveInterferences = graphNodeToRemove.getInterferences();
		for(IntNode graphNodeToRemoveInterference : graphNodeToRemoveInterferences)
		{
			graphNodeToRemoveInterference.removeInterference(graphNodeToRemove);
		}
	}

	/**
	 *
	 * @param var
	 */
	public void addVariable(String var)
	{
		for (IntNode n : this.nodes)
		{
			if (n.getName() == var)
				return;
		}
		IntNode node = new IntNode(var);
		this.nodes.add(node);
	}

	/**
	 *
	 * @param var
	 * @return
	 */
	private IntNode findNode(String var)
	{
		for (IntNode n : this.nodes)
		{
			if (n.getName() == var)
				return n;
		}
		IntNode node = new IntNode(var);
		this.nodes.add(node);
		return node;
	}

	/**
	 *
	 * @return
	 */
	public ArrayList<IntNode> getNodes()
	{
		return nodes;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String toString()
	{
		String s = "";
		for (int i = 0; i < this.nodes.size(); i++)
			s += this.nodes.get(i).toString() + "\n";
		return s;
	}
}
