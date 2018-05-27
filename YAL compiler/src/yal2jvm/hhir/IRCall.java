package yal2jvm.hhir;

import yal2jvm.Yal2jvm;
import yal2jvm.utils.Utils;

import java.util.ArrayList;

public class IRCall extends IRNode
{
    private String method;
    private String module;
    private String lhsVarName;
	private Type type;
    private ArrayList<PairStringType> arguments;

    IRCall(String method, String module, ArrayList<PairStringType> arguments, String lhsVarName)
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
		ArrayList<String> inst = new ArrayList<>(getArgumentsInstructions());

        String callInstructions = getCallInstruction();
		inst.add(callInstructions);

        if(getParent() instanceof IRMethod)
            if(!Utils.isLastCharacterOfString("V", callInstructions))
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
			 for (PairStringType arg : arguments) {
				 Type type = arg.getType();

				 type = getArgumentsType(arg, type);

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

	private Type getArgumentsType(PairStringType arg, Type initType)
	{
		IRMethod method = (IRMethod) findParent("Method");
		Type ret_type = initType;

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
			return initType;
		else
			return ret_type;
	}

	public Type getType()
	{
		return type;
	}

	private String getCallInstruction()
    {
       	StringBuilder callInst = new StringBuilder("invokestatic ");
    	if (this.module != null)
    		callInst.append(this.module).append("/");
    	callInst.append(this.method).append("(");

    	if(this.method.equals("main")) {
    		callInst.append("[Ljava/lang/String;");
		}
		else {
			for (PairStringType argument : arguments) {
				Type argumentType = argument.getType();

				argumentType = getArgumentsType(argument, argumentType);

				switch (argumentType) {
					case STRING: {
						callInst.append("Ljava/lang/String;");
						break;
					}
					case INTEGER: {
						callInst.append("I");
						break;
					}
					case ARRAY: {
						callInst.append("[I");
						break;
					}
					case ARRAYSIZE: {
						callInst.append("[I");
						break;
					}
					default:
						break;
				}
			}
		}
        callInst.append(")");

        if (this.module == null || this.module.equals(Yal2jvm.moduleName))
        {
        	IRModule irModule = (IRModule) findParent("Module");
        	IRMethod irMethod = irModule.getChildMethod(method);
			Type returnType = irMethod.getReturnType();
			switch(returnType)
			{
				case INTEGER:
					callInst.append("I");
					type = Type.INTEGER;
					break;
				case ARRAY:
					callInst.append("[I");
					type = Type.ARRAY;
					break;
				case VOID:
					callInst.append("V");
					type = Type.VOID;
					break;
			}
        }
        else //return undefined
        {
			//if call from statements, keep return undefined
        	if(lhsVarName == null) {
				callInst.append("V");
				type = Type.VOID;
				return callInst.toString();
			}

			IRNode node = getVarIfExists(lhsVarName);
        	if(node == null)
			{
				callInst.append("I");
				type = Type.INTEGER;
				return callInst.toString();
			}

        	if(node instanceof IRAllocate)
			{
				IRAllocate allocate = (IRAllocate)node;
				if(allocate.getRegister() == -1)//if lhs not defined yet, we assume int
				{
					callInst.append("I");
					type = Type.INTEGER;
				}

				if(allocate.getType().equals(Type.INTEGER)) //otherwise lhs defined, and type equals lhs var type
				{
					callInst.append("I");
					type = Type.INTEGER;
				}
				else
				{
					callInst.append("[I");
					type = Type.ARRAY;
				}
			}
			else if(node instanceof IRGlobal)
			{
				IRGlobal global = (IRGlobal) node;
				if(global.getType().equals(Type.INTEGER)) //otherwise lhs defined, and type equals lhs var type
				{
					callInst.append("I");
					type = Type.INTEGER;
				}
				else
				{
					callInst.append("[I");
					type = Type.ARRAY;
				}
			}
			else //argument
			{
				IRMethod method = (IRMethod) findParent("Method");
				if(method.getArgumentType(lhsVarName).equals(Type.INTEGER)) //otherwise lhs defined, and type equals lhs var type
				{
					callInst.append("I");
					type = Type.INTEGER;
				}
				else
				{
					callInst.append("[I");
					type = Type.ARRAY;
				}
			}
        }
        return callInst.toString();
    }
}
