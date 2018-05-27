package yal2jvm.symbol_tables;

import yal2jvm.ast.*;
import yal2jvm.semantic_analysis.ModuleAnalysis;

import java.util.ArrayList;

public class FunctionSymbol extends Symbol
{

    private SimpleNode functionAST;
    private ArrayList<VarSymbol> arguments;
    private VarSymbol returnValue;
    private int statementsChildNumber = 0;

    public FunctionSymbol(SimpleNode functionAST, String id)
    {
        super(id);
        this.functionAST = functionAST;
        this.arguments = new ArrayList<>();
    }

    public SimpleNode getFunctionAST()
    {
        return functionAST;
    }

    public ArrayList<VarSymbol> getArguments()
    {
        return arguments;
    }

    public VarSymbol getReturnValue()
    {
        return returnValue;
    }

    public int getStatementsChildNumber()
    {
        return statementsChildNumber;
    }

    public void parseFunctionHeader()
    {
        //indicates the index(child num) of the arguments. 0 if no return value, or 1 if has return value
        int argumentsIndex = 0;

        //get return value if existent
        SimpleNode returnValueNode = (SimpleNode) functionAST.jjtGetChild(0);
        if (returnValueNode instanceof ASTSTATEMENTS)
            return;

        if (!(returnValueNode instanceof ASTVARS))
        {
            argumentsIndex++;
            statementsChildNumber++;
            parseFunctionReturnValue(returnValueNode);
        }

        //get arguments if existent
        SimpleNode argumentsNode = (SimpleNode) functionAST.jjtGetChild(argumentsIndex);
        if (!(argumentsNode instanceof ASTVARS))
            return;

        statementsChildNumber++;
        parseArguments(argumentsNode);
    }

    private void parseArguments(SimpleNode argumentsNode)
    {
        for (int i = 0; i < argumentsNode.jjtGetNumChildren(); i++)
        {
            SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);
            if (child != null)
            {
                VarSymbol varSymbol;
                if (child instanceof ASTSCALARELEMENT)
                    varSymbol = parseScalarElementArgument((ASTSCALARELEMENT) child);
                else
                    varSymbol = parseArrayElementArgument((ASTARRAYELEMENT) child);

                if (varSymbol == null)
                    continue;

                checkArgumentAlreadyExists(child, varSymbol);
                arguments.add(varSymbol);
            }
        }
    }

    private void checkArgumentAlreadyExists(SimpleNode child, VarSymbol varSymbol)
    {
        for (VarSymbol argument: arguments)
        {
            if(argument.getId().equals(varSymbol.getId()))
            {
                System.out.println("Line " + child.getBeginLine() + ": Argument " +
                        varSymbol.getId() + " already declared.");
                ModuleAnalysis.hasErrors = true;
            }
        }
    }

    private VarSymbol parseArrayElementArgument(ASTARRAYELEMENT child)
    {
        String astArrayElementId = child.id;
        String astArrayElementType = SymbolType.ARRAY.toString();
        if (returnValue != null && returnValue.getId().equals(astArrayElementId))
        {
            if (!returnValue.getType().equals(astArrayElementType))
            {
                System.out.println("Line " + child.getBeginLine() + ": Argument " + child.id
                        + " already declared as " + returnValue.getType() + ".");
                return null;
            }
            else
            {
                returnValue.setInitialized(true);
            }
        }

        return new VarSymbol(astArrayElementId, astArrayElementType, true);
    }

    private VarSymbol parseScalarElementArgument(ASTSCALARELEMENT child)
    {
        String astScalarElementId = child.id;
        String astScalarElementType = SymbolType.INTEGER.toString();
        if (returnValue != null && returnValue.getId().equals(astScalarElementId))
        {
            if (!returnValue.getType().equals(astScalarElementType))
            {
                System.out.println("Line " + child.getBeginLine() + ": Argument " + child.id
                        + " already declared as " + returnValue.getType() + ".");
                return null;
            }
            else
                returnValue.setInitialized(true);
        }

        return new VarSymbol(astScalarElementId, astScalarElementType, true);
    }

    private void parseFunctionReturnValue(SimpleNode returnValueNode)
    {
        if (returnValueNode instanceof ASTSCALARELEMENT)
        {
            ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) returnValueNode;
            String returnValueId = astscalarelement.id;
            returnValue = new VarSymbol(returnValueId, SymbolType.INTEGER.toString(), false);
        } else
        {
            ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) returnValueNode;
            String returnValueId = astarrayelement.id;
            returnValue = new VarSymbol(returnValueId, SymbolType.ARRAY.toString(), false);
        }
    }
}
