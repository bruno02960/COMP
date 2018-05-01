package yal2jvm.hhir;

import yal2jvm.utils.Utils;

import java.util.ArrayList;

public class IRCall extends IRNode
{
    private String method;
    private String module;
    private ArrayList<PairStringType> arguments;

    public IRCall(String method, String module, ArrayList<PairStringType> arguments)
    {
        this.method = method;
        this.module = module;
        this.arguments = arguments;
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

        if (this.module != null)
        {
            if (this.module.equals("io"))
                callInst += "V";
            else
                callInst += "I";
        }
        else
        {
            IRModule mod;
            IRNode par = this.parent;
            while (true)
            {
                if (par.toString().equals("Module"))
                {
                    mod = (IRModule) par;
                    break;
                } else
                    par = par.getParent();
            }
            
            Type retType = null;
            for (int i = 0; i < mod.getChildren().size(); i++)
            {
            	if(mod.getChildren().get(i).toString().equals("Method"))
            	{
            		IRMethod met = ((IRMethod)mod.getChildren().get(i));
            		if (met.getName().equals(this.method))
            		{
            			retType = met.getReturnType();
            			break;
            		}
            	}
            }
            
            switch(retType)
            {
	            case VOID:
	            	callInst += "V";
	            	break;
	            case INTEGER:
	            	callInst += "I";
	            	break;
	            case ARRAY:
	            	break;
	            default:
	            	break;
            }
        }
        return callInst;
    }
}
