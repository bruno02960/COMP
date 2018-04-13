package yal2jvm.SemanticAnalysis;

import yal2jvm.Analysis;
import yal2jvm.SymbolTables.Symbol;
import yal2jvm.ast.*;

import java.util.ArrayList;
import java.util.HashMap;

public class ModuleAnalysis extends Analysis
{

    public ModuleAnalysis(SimpleNode ast, HashMap<String, Symbol> inheritedSymbols)
    {
        super(ast, inheritedSymbols);
    }

    public void initiateGlobalSymbolTable()
    {
        int numChildren = ast.jjtGetNumChildren();

        for(int i = 0; i < numChildren; i++)
        {
            Node child = ast.jjtGetChild(i);
            Symbol symbol = createSymbol(child);
            mySymbols.put(symbol.getId(), symbol);
        }

        //TODO ver analise semantica
    }

    public void parse()
    {
        for(int i = 0; i < this.mySymbols.size(); i++)
        {
          if(this.mySymbols.get(i).getType().equals("FUNCTION"))
          {
              FunctionAnalysis functionAnalysis = new FunctionAnalysis((SimpleNode) ast.jjtGetChild(i), mySymbols);
              functionAnalysis.parse();
          }
        }



    }

    private Symbol createSymbol(Node child)
    {
        String type = child.toString();
        String name = null;
        ArrayList<Integer> values = null;
        switch (type)
        {
            case "FUNCTION":
                FunctionSymbol functionSymbol = parseFunctionChild(child);
                //TODO DEBUG TIRAR
                System.out.println("functionSymbol name: " + functionSymbol.getName());
                System.out.println("functionSymbol arguments: ");
                for(int i = 0; i < functionSymbol.getArguments().size(); i++)
                {
                    System.out.println("id: " + functionSymbol.getArguments().get(i).getId() + " type: " + functionSymbol.getArguments().get(i).getType() + " ");
                }

                System.out.println("functionSymbol returnValue type: " + functionSymbol.getReturnValue().getType());
                System.out.println("functionSymbol returnValue id: " + functionSymbol.getReturnValue().getId());
                break;
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
                System.out.println("values: ");
                for(int i = 0; i < values.size(); i++)
                {
                    System.out.println(values.get(i) + " ");
                }
                break;
            default:
                //TODO

        }



        return new Symbol(name, type, values);
    }

    private FunctionSymbol parseFunctionChild(Node functionNode)
    {
        String id = ((ASTFUNCTION) functionNode).id;

        int argumentsIndex = 0;

        //return value
        Symbol returnValue = null;
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
                returnValue = new Symbol(returnValueId, "ASTSCALARELEMENT");
            }
            else
            {
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
                String returnValueId = astarrayelement.id;
                returnValue = new Symbol(returnValueId, "ASTARRAYELEMENT");
            }
        }

        //arguments
        SimpleNode arguments = (SimpleNode) functionNode.jjtGetChild(argumentsIndex);
        if(arguments == null)
            return new FunctionSymbol((SimpleNode) functionNode, id, null, returnValue);

        ArrayList<Symbol> argumentsSymbols = new ArrayList<>();
        for(int i = 0; i < arguments.jjtGetNumChildren(); i++)
        {
            SimpleNode child = (SimpleNode) arguments.jjtGetChild(i);
            if( child != null)
            {
                if(child instanceof ASTSCALARELEMENT)
                {
                    ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
                    argumentsSymbols.add(getSymbolsFromScalarElementDeclarationIfExists(astscalarelement));
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
                    argumentsSymbols.add(getSymbolsFromArrayElementDeclarationIfExists(astarrayelement));
                }
            }
        }

        return new FunctionSymbol((SimpleNode) functionNode, id, argumentsSymbols, returnValue);
    }

    private Symbol getSymbolsFromArrayElementDeclarationIfExists(ASTARRAYELEMENT astarrayelement)
    {
        String arrayElementId = astarrayelement.id;
        return new Symbol(arrayElementId, "ARRAYELEMENT");
    }

    private Symbol getSymbolsFromScalarElementDeclarationIfExists(ASTSCALARELEMENT astscalarelement)
    {
        String scalarElementId = astscalarelement.id;
        return new Symbol(scalarElementId, "SCALARELEMENT");
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
