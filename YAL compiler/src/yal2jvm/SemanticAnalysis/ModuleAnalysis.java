package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class ModuleAnalysis extends Analysis
{
    public ModuleAnalysis(SimpleNode ast)
    {
        super(ast, null, new HashMap<String, Symbol>());
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
        int numSimbols = this.mySymbols.size();
        for(int i = 0; i < numSimbols; i++)
        {
            Symbol symbol = this.mySymbols.get(i);
            if(symbol instanceof FunctionSymbol)
            {
                HashMap<String, Symbol> unifiedSymbolTable = getUnifiedSymbolTable();
                FunctionAnalysis functionAnalysis = new FunctionAnalysis((SimpleNode) ast.jjtGetChild(i), unifiedSymbolTable,
                        functionNameToFunctionSymbol);
                functionAnalysis.parse();
            }
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
                FunctionSymbol functionSymbol = parseFunctionChild(child);
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

    private FunctionSymbol parseFunctionChild(Node functionNode)
    {
        String id = ((ASTFUNCTION) functionNode).id;
        int argumentsIndex = 0; //indicates the index(child num) of the arguments. 0 if no return value, or 1 if has return value.

        //get return value if existent
        VarSymbol returnValue = null;
        SimpleNode returnValueNode = (SimpleNode) functionNode.jjtGetChild(0);
        if(returnValueNode instanceof ASTSTATEMENTS)
            return new FunctionSymbol((SimpleNode) functionNode, id, null, null);;
        if(!(returnValueNode instanceof ASTARGUMENTS))
        {
            argumentsIndex++;
            if(returnValueNode instanceof ASTSCALARELEMENT)
            {
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)returnValueNode;
                String returnValueId = astscalarelement.id;
                returnValue = new VarSymbol(returnValueId, "ASTSCALARELEMENT", true);
            }
            else
            {
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
                String returnValueId = astarrayelement.id;
                returnValue = new VarSymbol(returnValueId, "ASTARRAYELEMENT", true);
            }
        }

        //get arguments if existent
        SimpleNode arguments = (SimpleNode) functionNode.jjtGetChild(argumentsIndex);
        if(arguments == null || !(arguments instanceof ASTARGUMENTS))
            return new FunctionSymbol((SimpleNode) functionNode, id, null, returnValue);

        ArrayList<VarSymbol> argumentsVarSymbols = new ArrayList<>();
        for(int i = 0; i < arguments.jjtGetNumChildren(); i++)
        {
            SimpleNode child = (SimpleNode) arguments.jjtGetChild(i);
            if( child != null)
            {
                VarSymbol varSymbol;
                if(child instanceof ASTSCALARELEMENT)
                {
                    ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
                    varSymbol = new VarSymbol(astscalarelement.id, "SCALARELEMENT", true);
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
                    varSymbol = new VarSymbol(astarrayelement.id, "ARRAYELEMENT", true);
                }
                argumentsVarSymbols.add(varSymbol);
            }
        }

        return new FunctionSymbol((SimpleNode) functionNode, id, argumentsVarSymbols, returnValue);
    }

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
