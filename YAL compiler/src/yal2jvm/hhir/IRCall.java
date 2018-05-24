package yal2jvm.hhir;

import yal2jvm.Yal2jvm;
import yal2jvm.utils.Utils;

import java.util.ArrayList;

public class IRCall extends IRNode
{
    private String method;
    private String module;
    private String lhsVarName;
    private ArrayList<PairStringType> arguments;

    public IRCall(String method, String module, ArrayList<PairStringType> arguments, String lhsVarName)
    {
        this.method = method;
        this.module = module;
        this.arguments = arguments;
        this.lhsVarName = lhsVarName;
        this.nodeType = "Call";
    }

    @Override
    public ArrayList<String> getInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();

        inst.addAll(getArgumentsInstructions());

        String callInstructions = getCallInstruction();
		inst.add(callInstructions);

        if((getParent() instanceof IRStore) == false && Utils.isLastCharacterOfString("I", callInstructions))
            inst.add("\npop");

        return inst;
    }
    
    private ArrayList<String> getArgumentsInstructions()
    {
    	 ArrayList<String> inst = new ArrayList<>();

         for (int i = 0; i < arguments.size(); i++)
         {
         	PairStringType arg = arguments.get(i);
         	
         	switch (arg.getType())
         	{
         		case STRING:
 	        	{
 	        		IRConstant stringConst = new IRConstant(arg.getString());
 	        		inst.addAll(stringConst.getInstructions());
 	        		break;
 	        	}
         		case INTEGER:
         		{
         			IRNode var;
         			if (arg.getString().matches("-?\\d+"))
         			{
         				var = new IRConstant(arg.getString());
         			}
         			else
         			{
         				var = new IRLoad(arg.getString());
         				this.addChild(var);
         			}
         			inst.addAll(var.getInstructions());
         			break;
         		}
         		case ARRAY:
         		{
         			break;
         		}
         		default:
         			break;
         	}
         }
         return inst;
    }
    
    private String getCallInstruction()
    {
       	String callInst = "invokestatic ";
    	if (this.module != null)
    		callInst += this.module + "/";
    	callInst += this.method + "(";
    	
    	for (int i = 0; i < arguments.size(); i++)
    	{
    		switch(arguments.get(i).getType())
    		{
		    	case STRING:
		    	{
		    		callInst += "Ljava/lang/String;";
		    		break;
		    	}
				case INTEGER:
				{
					callInst += "I";
					break;
				}
				case ARRAY:
				{
					break;
				}
				default:
					break;
    		}
    	}
        callInst += ")";

        if (this.module == null || this.module.equals(Yal2jvm.moduleName))
        {
        	IRModule irModule = (IRModule) findParent("Module");
        	IRMethod irMethod = (IRMethod) irModule.getChild("Method");
			Type returnType = irMethod.getReturnType();
			switch(returnType)
			{
				case INTEGER:
					callInst += "I";
					break;
				case ARRAY:
					callInst += "A";
					break;
				case VOID:
					callInst += "V";
					break;
			}
        }
        else //return undefined
        {
			//if call from statements, keep return undefined
        	if(lhsVarName == null)
				callInst += "V";

			IRAllocate irAllocate = getVarIfExists(lhsVarName);
			if(irAllocate == null || irAllocate.getRegister() == -1)//if lhs not defined yet, we assume int
				callInst += "I";
			else if(irAllocate.getType().equals(Type.INTEGER)) //otherwise lhs defined, and type equals lhs var type
				callInst += "I";
			else
				callInst += "A";
        }
        return callInst;
    }
}
