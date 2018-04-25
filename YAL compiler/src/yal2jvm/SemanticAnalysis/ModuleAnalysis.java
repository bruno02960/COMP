package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.FunctionSymbol;
import yal2jvm.ast.*;

import java.util.*;

public class ModuleAnalysis extends Analysis
{
    public static String moduleName;

    public ModuleAnalysis(SimpleNode ast)
    {
        super(ast, null, new HashMap<String, Symbol>());
        moduleName = ((ASTMODULE) ast).name;
    }

    private void initiateGlobalSymbolTable()
    {
        int numChildren = ast.jjtGetNumChildren();
        for(int i = 0; i < numChildren; i++)
        {
            Node child = ast.jjtGetChild(i);
            addSymbolToSymbolTable(child);
        }

        //TODO ver analise semantica
    }

    public void parse()
    {
        initiateGlobalSymbolTable();

        HashMap<String, Symbol> unifiedSymbolTable = getUnifiedSymbolTable();
        Iterator it = functionNameToFunctionSymbol.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry pair = (Map.Entry) it.next();
            FunctionSymbol functionSymbol = (FunctionSymbol) pair.getValue();
            SimpleNode functionAST = functionSymbol.getFunctionAST();
            FunctionAnalysis functionAnalysis = new FunctionAnalysis(functionAST, unifiedSymbolTable,
                    functionNameToFunctionSymbol);
            functionAnalysis.parse();
        }
    }

    private void addSymbolToSymbolTable(Node child)
    {
        String type = child.toString();
        String name;
        Symbol varSymbol;
        switch (type)
        {
            case "FUNCTION":
                ASTFUNCTION astfunctionNode = (ASTFUNCTION) child;
                String functionId = astfunctionNode.id;
                FunctionSymbol functionSymbol = new FunctionSymbol(astfunctionNode, functionId);
                functionSymbol.parseFunctionHeader();

                //TODO DEBUG TIRAR
                System.out.println("functionSymbol name: " + functionSymbol.getId());
                if(functionSymbol.getArguments() != null)
                {
                    System.out.println("functionSymbol arguments: ");
                    for(int i = 0; i < functionSymbol.getArguments().size(); i++)
                    {
                        System.out.println("id: " + functionSymbol.getArguments().get(i).getId() + " type: " + functionSymbol.getArguments().get(i).getType() + " ");
                    }
                }

                if(functionSymbol.getReturnValue() != null)
                {
                    System.out.println("functionSymbol returnValue type: " + functionSymbol.getReturnValue().getType());
                    System.out.println("functionSymbol returnValue id: " + functionSymbol.getReturnValue().getId());
                }

                functionNameToFunctionSymbol.put(functionSymbol.getId(), functionSymbol);
                break;
            case "DECLARATION":
                parseDeclaration((ASTDECLARATION) child);
                break;
            default:
                System.out.println("Unexpected node" + child.toString()); //TODO linha
                System.exit(-1);
                break;
        }
    }

    //TODO: remove maybe
    private boolean IsAssignRHSFromArrayElementInitialized(Node declarationNode)
    {
        Node node = declarationNode.jjtGetChild(0);
       /* if(node == null) //it means that is a declaration of type a[]
        {
            ASTDECLARATION astdeclaration = ((ASTDECLARATION) declarationNode);
            Integer value = astdeclaration.integer;
            if(value != null)
                return true;

            return false;
        }*/

       if(node == null) //it means that is a declaration of type a[]
        {
            ASTDECLARATION astdeclaration = ((ASTDECLARATION) declarationNode);
            Integer value = astdeclaration.integer;
            if(value != null)
            {
                System.out.println("Cannot declare an array as a[] = 1. This will set all values of array to 1, but no values set yet.");//TODO linha
                System.exit(-1);
                return false;
            }

            return false;
        }

        node = declarationNode.jjtGetChild(1);
        if(!(node instanceof ASTARRAYSIZE))
        {
            System.out.println("Unexpected node" + node.toString()); //TODO linha
            System.exit(-1);
        }

        return true;
    }

    //TODO: remove maybe
    private boolean IsAssignRHSFromScalarElementInitialized(Node declarationNode)
    {
        Node node = declarationNode.jjtGetChild(0);
        if(node == null) //it means that is a declaration of type a[]=1
        {
            ASTDECLARATION astdeclaration = ((ASTDECLARATION) declarationNode);
            Integer value = astdeclaration.integer;
            if(value != null)
                return true;

            return false;
        }

        node = declarationNode.jjtGetChild(1);
        if(!(node instanceof ASTARRAYSIZE))
        {
            System.out.println("Unexpected node" + node.toString()); //TODO linha
            System.exit(-1);
        }

        return true;
    }

    //TODO: remove maybe
    private ArrayList<Integer> getValuesFromScalarElementDeclarationIfExists(ASTSCALARELEMENT astscalarelement)
    {
        ArrayList<Integer> values = new ArrayList<>();
        ASTDECLARATION astdeclaration = (ASTDECLARATION)astscalarelement.jjtGetParent();
        Integer integer = astdeclaration.integer;
        if(integer == null)
            return values;
        if(astdeclaration.operator.equals("-"))
            integer *= -1;
        values.add(integer);
        return values;
    }

    //TODO: remove maybe
    private ArrayList<Integer> getValuesFromArrayElementDeclarationIfExists(ASTARRAYELEMENT astarrayelement)
    {
        String value = astarrayelement.jjtGetValue();
        if(value != "")
        {
            Integer arraySize = Integer.parseInt(value);
            return new ArrayList<>(arraySize);
        }

        return null;
    }


}
