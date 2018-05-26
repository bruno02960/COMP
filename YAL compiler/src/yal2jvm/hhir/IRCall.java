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

        if(getParent() instanceof IRMethod)
            if(Utils.isLastCharacterOfString("V", callInstructions) == false)
                inst.add("\npop");

        return inst;
    }
    
    private ArrayList<String> getArgumentsInstructions()
    {
    	 ArrayList<String> inst = new ArrayList<>();

    	 if(this.method.equals("main")) {
    	 	inst.add("aconst_null");
		 }
    	 else {
			 for (int i = 0; i < arguments.size(); i++) {
				 PairStringType arg = arguments.get(i);

				 Type type = arg.getType();

				 type = getType(arg, type);

				 switch (type) {
					 case STRING: {
						 IRConstant stringConst = new IRConstant(arg.getString());
						 inst.addAll(stringConst.getInstructions());
						 break;
					 }
					 case INTEGER: {
						 IRNode var;
						 if (arg.getString().matches("-?\\d+")) {
							 var = new IRConstant(arg.getString());
						 } else {
							 var = new IRLoad(arg.getString(), Type.INTEGER);
							 this.addChild(var);
						 }
						 inst.addAll(var.getInstructions());
						 break;
					 }
					 case ARRAY: {
						 IRLoad irLoad = new IRLoad(arg.getString(), Type.ARRAY);
						 this.addChild(irLoad);
						 inst.addAll(irLoad.getInstructions());
						 break;
					 }
					 case ARRAYSIZE: {
						 IRLoad irLoad = new IRLoad(arg.getString(), Type.ARRAYSIZE);
						 this.addChild(irLoad);
						 inst.addAll(irLoad.getInstructions());
						 break;
					 }
					 default:
						 break;
				 }
			 }
		 }

         return inst;
    }

	private Type getType(PairStringType arg, Type type) {
		IRMethod method = (IRMethod) findParent("Method");
		Type ret_type = type;

		if(ret_type != Type.STRING) {
			ret_type = method.getArgumentType(arg.getString());
            if (ret_type == null)
				ret_type = method.getVarType(arg.getString());
            if (ret_type == null) {
                IRModule module = (IRModule) findParent("Module");
                IRGlobal global = module.getGlobal(arg.getString());
                if(global != null)
					ret_type = global.getType();
            }
        }

        if(ret_type == null)
			return type;
		else
			return ret_type;
	}

	private String getCallInstruction()
    {
       	String callInst = "invokestatic ";
    	if (this.module != null)
    		callInst += this.module + "/";
    	callInst += this.method + "(";

    	if(this.method.equals("main")) {
    		callInst+="[Ljava/lang/String;";
		}
		else {
			for (int i = 0; i < arguments.size(); i++) {
				Type type = arguments.get(i).getType();

				type = getType(arguments.get(i), type);

				switch (type) {
					case STRING: {
						callInst += "Ljava/lang/String;";
						break;
					}
					case INTEGER: {
						callInst += "I";
						break;
					}
					case ARRAY: {
						callInst += "[I";
						break;
					}
					case ARRAYSIZE: {
						callInst += "[I";
						break;
					}
					default:
						break;
				}
			}
		}
        callInst += ")";

        if (this.module == null || this.module.equals(Yal2jvm.moduleName))
        {
        	IRModule irModule = (IRModule) findParent("Module");
        	IRMethod irMethod = irModule.getChildMethod(method);
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
        	if(lhsVarName == null) {
				callInst += "V";
				return callInst;
			}

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
