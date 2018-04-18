package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.VarSymbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ModuleAnalysis extends Analysis
{
    public ModuleAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
        super(ast, inheritedSymbols);
        this.inheritedSymbols = new HashMap<String, Symbol>();
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
            VarSymbol varSymbol = (VarSymbol) this.mySymbols.get(i);
            if(varSymbol.getType().equals("FUNCTION"))
            {
                FunctionAnalysis functionAnalysis = new FunctionAnalysis((SimpleNode) ast.jjtGetChild(i), mySymbols);
                functionAnalysis.parse();
            }
        }

    }

    private void addSymbolToSymbolTable(Node child)
    {
        String type = child.toString();
        String name = null;
        ArrayList<Integer> values = null;
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

                inheritedSymbols.put(functionSymbol.getId(), functionSymbol);
            case "DECLARATION":
                Node node = child.jjtGetChild(0);
                if(node instanceof ASTSCALARELEMENT)
                {
                    ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)node;
                    values = getValuesFromScalarElementDeclarationIfExists(astscalarelement);
                    name = astscalarelement.id;
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)node;
                    values = getValuesFromArrayElementDeclarationIfExists(astarrayelement);
                    name = astarrayelement.id;
                }
                //TODO DEBUG TIRAR
                System.out.println("symbol name: " + name);
                System.out.println("symbol type: " + type);
                if(values != null)
                {
                    System.out.println("values: ");
                    for(int i = 0; i < values.size(); i++)
                    {
                        System.out.println(values.get(i) + " ");
                    }
                }


                Symbol varSymbol = new VarSymbol(name, type, values);
                mySymbols.put(varSymbol.getId(), varSymbol);
                break;
            default:
                System.out.println("Unexpected node" + child.toString()); //TODO linha
                System.exit(-1);
                break;
        }
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
                returnValue = new VarSymbol(returnValueId, "ASTSCALARELEMENT");
            }
            else
            {
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
                String returnValueId = astarrayelement.id;
                returnValue = new VarSymbol(returnValueId, "ASTARRAYELEMENT");
            }
        }

        //get arguments if existent
        SimpleNode arguments = (SimpleNode) functionNode.jjtGetChild(argumentsIndex);
        if(arguments == null)
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
                    varSymbol = new VarSymbol(astscalarelement.id, "SCALARELEMENT");
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
                    varSymbol = new VarSymbol(astarrayelement.id, "ARRAYELEMENT");
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
