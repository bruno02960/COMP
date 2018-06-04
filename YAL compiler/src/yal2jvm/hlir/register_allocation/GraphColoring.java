package yal2jvm.hlir.register_allocation;

import yal2jvm.hlir.liveness_analysis.IntGraph;
import yal2jvm.hlir.liveness_analysis.IntNode;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 */
public class  GraphColoring
{
    private IntGraph graph;
    private int numRegisters;
    private List<Integer> registers;
    private Stack<IntNode> nodesToColorStack = new Stack<>();
    private HashMap<String, Integer> varNameToRegisterNumber = new HashMap<>();

    /**
     *
     * @param graph
     * @param numRegisters
     */
    public GraphColoring(IntGraph graph, int numRegisters)
    {
        this.graph = graph;
        this.numRegisters = numRegisters;
        this.registers = IntStream.rangeClosed(0, numRegisters - 1).boxed().collect(Collectors.toList());
    }

    /**
     *
     * @param numRegisters
     */
    public void setNumRegisters(int numRegisters)
    {
        this.numRegisters = numRegisters;
        this.registers = IntStream.rangeClosed(0, numRegisters - 1).boxed().collect(Collectors.toList());
    }

    /**
     *
     * @return
     */
    public HashMap<String, Integer> getVarNameToRegisterNumber()
    {
        return varNameToRegisterNumber;
    }

    /**
     *
     * @return
     */
    private boolean buildStackOfNodesToColor()
    {
        ArrayList<IntNode> listNodesOriginalGraph = graph.getNodes();
        IntGraph graphCopy = new IntGraph(graph);
        ArrayList<IntNode> listNodesGraphCopy = graphCopy.getNodes();
        int lastRegisterNumber = 0;
        for(int i = 0; i < listNodesGraphCopy.size(); i++)
        {
            IntNode node = listNodesGraphCopy.get(i);
            int nodeRequiredRegister = node.getRequiredRegister();
            if(nodeRequiredRegister > lastRegisterNumber)
                lastRegisterNumber = nodeRequiredRegister;
            if(node.indegree() < numRegisters && nodeRequiredRegister == -1) //indegree less than numRegisters and not parameter
            {
                nodesToColorStack.push(listNodesOriginalGraph.get(listNodesOriginalGraph.indexOf(node)));
                graphCopy.removeNode(node);
                i = -1;
            }
        }

        if(listNodesGraphCopy.size() > lastRegisterNumber + 1)  // lastRegisterNumber + 1 = number of registers
            return false;

        int expectedValue = lastRegisterNumber;
        for(int i = 0; i < listNodesGraphCopy.size(); i++)
        {
            IntNode node = listNodesGraphCopy.get(i);
            if(node.indegree() < numRegisters || node.getRequiredRegister() == expectedValue) //indegree less than numRegisters
            {
                expectedValue--;
                nodesToColorStack.push(listNodesOriginalGraph.get(listNodesOriginalGraph.indexOf(node)));
                graphCopy.removeNode(node);
                i = -1;
            }
        }

        return listNodesGraphCopy.size() == 0;
    }

    /**
     *
     * @return
     */
    public boolean colorGraph()
    {
        if(buildStackOfNodesToColor() == false)
            return false;

        while (nodesToColorStack.empty() == false)
        {
            IntNode node = nodesToColorStack.pop();

            ArrayList<Integer> usedRegisters = new ArrayList<>();
            ArrayList<IntNode> nodeInterferences = node.getInterferences();
            for(IntNode interference : nodeInterferences)
            {
                Integer registerNumber = varNameToRegisterNumber.get(interference.getName());
                if(registerNumber != null)
                    usedRegisters.add(registerNumber);
            }

            Integer register = findFirstUnusedRegisterThatMatchesRequired(usedRegisters, node.getRequiredRegister());
            if(register == null)
            {
                System.out.println("Internal error coloring graph - colorGraph of class GraphColoring.");
                System.exit(-1);
            }

            varNameToRegisterNumber.put(node.getName(), register);
        }

        return true;
    }

    /**
     *
     * @param usedRegisters
     * @return
     */
    private Integer findFirstUnusedRegisterThatMatchesRequired(ArrayList<Integer> usedRegisters, int requiredRegister)
    {
        for(Integer register : registers)
        {
            if(usedRegisters.contains(register) == false && (requiredRegister == -1 || register.intValue() == requiredRegister))
                return register;
        }

        return null;
    }
/*
    public static void main(String args[])
    {
        IntNode node1 = new IntNode("1");
        IntNode node2 = new IntNode("2");
        IntNode node3 = new IntNode("3");
        IntNode node4 = new IntNode("4");

        node1.addInterference(node2);
        node1.addInterference(node3);
        node1.addInterference(node4);

        node3.addInterference(node4);

        node2.addInterference(node4);

        IntGraph graph = new IntGraph();
        graph.addNode(node1);
        graph.addNode(node2);
        graph.addNode(node3);
        graph.addNode(node4);

        GraphColoring graphColoring = new GraphColoring(graph, 1);

        graphColoring.colorGraph();
        printHashMap(graphColoring.varNameToRegisterNumber);

        graphColoring.setNumRegisters(3);
        graphColoring.colorGraph();
        printHashMap(graphColoring.varNameToRegisterNumber);

        graphColoring.setNumRegisters(4);
        graphColoring.colorGraph();
        printHashMap(graphColoring.varNameToRegisterNumber);

    }

    private static void printHashMap(HashMap<String, Integer> hashMap)
    {
        Iterator it = hashMap.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry)it.next();
            System.out.println(pair.getKey() + " = " + pair.getValue());
        }
    }*/
}