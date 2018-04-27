package yal2jvm.SymbolTables;

import yal2jvm.ast.*;

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
        if(returnValueNode instanceof ASTSTATEMENTS)
            return;

        if(!(returnValueNode instanceof ASTVARS))
        {
            argumentsIndex++;
            statementsChildNumber++;
            if(returnValueNode instanceof ASTSCALARELEMENT)
            {
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)returnValueNode;
                String returnValueId = astscalarelement.id;
                returnValue = new VarSymbol(returnValueId, SymbolType.INTEGER.toString(), false);
            }
            else
            {
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
                String returnValueId = astarrayelement.id;
                returnValue = new VarSymbol(returnValueId, SymbolType.ARRAY.toString(), false, false);
            }
        }

        //get arguments if existent
        SimpleNode argumentsNode = (SimpleNode) functionAST.jjtGetChild(argumentsIndex);
        if(argumentsNode == null || !(argumentsNode instanceof ASTVARS))
            return;

        statementsChildNumber++;
        for(int i = 0; i < argumentsNode.jjtGetNumChildren(); i++)
        {
            SimpleNode child = (SimpleNode) argumentsNode.jjtGetChild(i);
            if( child != null)
            {
                VarSymbol varSymbol;
                if(child instanceof ASTSCALARELEMENT)
                {
                    ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)child;
                    String astScalarElementId = astscalarelement.id;
                    String astScalarElementType = SymbolType.INTEGER.toString();
                    if(returnValue != null && returnValue.getId().equals(astScalarElementId))
                    {
                        if(!returnValue.getType().equals(astScalarElementType))
                        {
                            System.out.println("Line " + astscalarelement.getBeginLine() + ": Argument " + astscalarelement.id +
                                    " already declared as " + returnValue.getType() + ".");
                            continue;
                        }
                        else
                            returnValue.setInitialized(true);
                    }
                    varSymbol = new VarSymbol(astScalarElementId, astScalarElementType, true);
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
                    String astArrayElementId = astarrayelement.id;
                    String astArrayElementType = SymbolType.ARRAY.toString();
                    if(returnValue != null && returnValue.getId().equals(astArrayElementId))
                    {
                        if(!returnValue.getType().equals(astArrayElementType))
                        {
                            System.out.println("Line " + astarrayelement.getBeginLine() + ": Argument " + astarrayelement.id +
                                    " already declared as " + returnValue.getType() + ".");
                            continue;
                        }
                        else
                        {
                            returnValue.setSizeSet(true);
                            returnValue.setInitialized(true);
                        }
                    }
                    varSymbol = new VarSymbol(astArrayElementId, astArrayElementType, true, true);
                }
                arguments.add(varSymbol);
            }
        }
    }

}
