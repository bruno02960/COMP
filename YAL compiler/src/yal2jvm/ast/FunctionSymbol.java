package yal2jvm.ast;

import yal2jvm.SymbolTables.VarSymbol;

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
        this.arguments = new ArrayList<VarSymbol>();
    }

    public FunctionSymbol(SimpleNode functionAST, String id, ArrayList<VarSymbol> arguments, VarSymbol returnValue)
    {
        super(id);
        this.functionAST = functionAST;
        this.arguments = arguments;
        this.returnValue = returnValue;
    }

    public SimpleNode getFunctionAST()
    {
        return functionAST;
    }

    public void setFunctionAST(SimpleNode functionAST)
    {
        this.functionAST = functionAST;
    }

    public ArrayList<VarSymbol> getArguments()
    {
        return arguments;
    }

    public void setArguments(ArrayList<VarSymbol> arguments)
    {
        this.arguments = arguments;
    }

    public VarSymbol getReturnValue()
    {
        return returnValue;
    }

    public void setReturnValue(VarSymbol returnValue)
    {
        this.returnValue = returnValue;
    }

    public int getStatementsChildNumber()
    {
        return statementsChildNumber;
    }

    public void setStatementsChildNumber(int statementsChildNumber)
    {
        this.statementsChildNumber = statementsChildNumber;
    }


    public void parseFunctionHeader()
    {
        int argumentsIndex = 0; //indicates the index(child num) of the arguments. 0 if no return value, or 1 if has return value.

        //get return value if existent
        SimpleNode returnValueNode = (SimpleNode) functionAST.jjtGetChild(0);
        if(returnValueNode instanceof ASTSTATEMENTS)
            return;

        if(!(returnValueNode instanceof ASTARGUMENTS))
        {
            argumentsIndex++;
            statementsChildNumber++;
            if(returnValueNode instanceof ASTSCALARELEMENT)
            {
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT)returnValueNode;
                String returnValueId = astscalarelement.id;
                returnValue = new VarSymbol(returnValueId, "INTEGER", false);
            }
            else
            {
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)returnValueNode;
                String returnValueId = astarrayelement.id;
                returnValue = new VarSymbol(returnValueId, "ARRAY", false);
            }
        }

        //get arguments if existent
        SimpleNode argumentsNode = (SimpleNode) functionAST.jjtGetChild(argumentsIndex);
        if(argumentsNode == null || !(argumentsNode instanceof ASTARGUMENTS))
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
                    varSymbol = new VarSymbol(astscalarelement.id, "INTEGER", true);
                }
                else
                {
                    ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT)child;
                    varSymbol = new VarSymbol(astarrayelement.id, "ARRAY", true);
                }
                arguments.add(varSymbol);
            }
        }
    }

}
