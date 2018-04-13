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
            mySymbols.put(symbol.getName(), symbol);
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
                    ASTARRAYELEMENT astscalarelement = (ASTARRAYELEMENT)node;
                    values = getValuesFromArrayElementDeclarationIfExists(astscalarelement);
                    name = astscalarelement.id;
                }
                break;
            default:
                //TODO

        }

        System.out.println("symbol name: " + name);
        System.out.println("symbol type: " + type);
        System.out.println("values: ");
        for(int i = 0; i < values.size(); i++)
        {
            System.out.println(values.get(i) + " ");
        }

        return new Symbol(name, type, values);
    }

    private FunctionSymbol parseFunctionChild(Node functionNode)
    {
        String name = ((ASTFUNCTION) functionNode).id;

        //arguments
        ArrayList<Symbol> argumentsSymbols = new ArrayList<>();
        SimpleNode arguments = (SimpleNode) functionNode.jjtGetChild(0);
        if(arguments != null)
        {
            if(arguments instanceof ASTSCALARELEMENT)
            {
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)arguments;
                argumentsSymbols = getSymbolsFromScalarElementDeclarationIfExists(astscalarelement);
                name = astscalarelement.id;
            }
            else
            {
                ASTARRAYELEMENT astscalarelement = (ASTARRAYELEMENT)arguments;
                argumentsSymbols = getSymbolsFromArrayElementDeclarationIfExists(astscalarelement);
                name = astscalarelement.id;
            }
        }
        //return value


        return new FunctionSymbol(child, name, argumentsSymbols, returnValue);
    }

    private ArrayList<Symbol> getSymbolsFromArrayElementDeclarationIfExists(ASTARRAYELEMENT astscalarelement)
    {
        return null;
    }

    private ArrayList<Symbol> getSymbolsFromScalarElementDeclarationIfExists(ASTSCALARELEMENT astscalarelement)
    {
        return null;
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
