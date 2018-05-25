package yal2jvm.hhir;

import yal2jvm.Yal2jvm;

import java.util.ArrayList;

public class IRGlobal extends IRNode
{
    private String name;
    private Type type;
    private Variable value = null;
    private boolean arraySize = false;
    private ArrayList<String> staticArraysInstructions = new ArrayList<>();

    public IRGlobal(Variable variable) // a[]; ou a;
    {
        this.name = variable.getVar();
        this.type = variable.getType();
        this.nodeType = "Global";
    }

    public IRGlobal(Variable variable, Variable value)
    {
        this(variable);
        this.value = value;
    }

    public IRGlobal(Variable variable, Variable value, Type arraySize) // a[] = [50]; ou a = [50];
    {
        this(variable, value);
        assert arraySize == Type.ARRAYSIZE;
        this.arraySize = true;
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        if(value == null)
        {
            if (type == Type.ARRAY) // a[];
            return createGlobalArrayWithSize0();
        else // a;
            return createGlobalInteger();
        }
        else
        {
            if (type == Type.ARRAY) // a[] = ...
            {
                if(arraySize) // a[] = [50];
                    return createGlobalArrayWithSize(value);
                else // a[] = 50;
                {
                    assignAllArrayElements(value);
                    return new ArrayList<String>();
                }
            }
            else // a = ...
            {
                if(arraySize) // a = [50];
                    return createGlobalArrayWithSize(value);
                else // a = 50;
                    return createGlobalInteger();
            }
        }
    }

    private ArrayList<String> createGlobalInteger()
    {
        ArrayList<String> insts = new ArrayList<>();
        String inst = ".field public static " + name;
        inst += " I = " + (value != null ? value.getVar() : 0);
        insts.add(inst);

        return insts;
    }

    private ArrayList<String> createGlobalArray(ArrayList<String> sizeInstructions)
    {
        //declare array as global
        ArrayList<String> insts = new ArrayList<>();
        String inst = ".field public static " + name + " A";
        insts.add(inst);

        // instructions to static init method
        staticArraysInstructions.addAll(sizeInstructions);
        staticArraysInstructions.add("newarray int");
        staticArraysInstructions.add("putstatic " + Yal2jvm.moduleName + "/" + name + " A");

        return insts;
    }

    private ArrayList<String> createGlobalArrayWithSize0()
    {
        ArrayList<String> sizeInstructions = new ArrayList<>();
        sizeInstructions.add("iconst_0");

        return createGlobalArray(sizeInstructions);
    }

    private ArrayList<String> createGlobalArrayWithSize(Variable value)
    {
        IRNode valueNode = getValueIRNode(value);
        return createGlobalArray(valueNode.getInstructions());
    }

    private IRNode getValueIRNode(Variable value)
    {
        IRNode valueNode;
        if(value.getType() == Type.INTEGER)
            valueNode = new IRConstant(value.getVar());
        else
            valueNode = new IRLoad(value);
        this.addChild(valueNode);
        return valueNode;
    }

    private void assignAllArrayElements(Variable value)
    {
        IRNode valueNode = getValueIRNode(value);
        IRMethod method = (IRMethod) findParent("Method");
        ArrayList<String> globalVariableJVMCode = getGlobalVariable(name, method);

        staticArraysInstructions.addAll(globalVariableJVMCode);
        staticArraysInstructions.add("arraylength");
        staticArraysInstructions.add("init:");
        staticArraysInstructions.add("iconst_1");
        staticArraysInstructions.add("isub");
        staticArraysInstructions.add("dup");
        staticArraysInstructions.add("iflt end");
        staticArraysInstructions.addAll(globalVariableJVMCode);
        staticArraysInstructions.add("swap");
        staticArraysInstructions.addAll(valueNode.getInstructions());
        staticArraysInstructions.add("iastore");
        staticArraysInstructions.add("goto init");
        staticArraysInstructions.add("end:");
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public Type getType()
    {
        return this.type;
    }

    public ArrayList<String> getStaticArraysInstructions()
    {
        return staticArraysInstructions;
    }
    
    public ArrayList<String>getInitializationInstructions()
    {
		ArrayList<String> inst = new ArrayList<>();
		
		//if size
		
		//else if init 
		
		return inst;
    }
}

