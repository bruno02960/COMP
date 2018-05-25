package yal2jvm.hhir;

import java.util.ArrayList;
import java.util.Collection;

import yal2jvm.ast.*;

public class HHIR
{
    private IRModule root;
    private SimpleNode ast;

    //TODO: Debug
    boolean declarationDebug = true;
    boolean functionDebug = false;
    boolean assignDebug = false;
    boolean callDebug = false;

    public HHIR(SimpleNode ast)
    {
        this.ast = ast;
        this.root = createHHIR();
    }

    private IRModule createHHIR()
    {
        //create HHIR from AST
        ASTMODULE astModule = (ASTMODULE) ast;
        createModuleHHIR(astModule);

        return root;
    }

    /*public IRModule createHardcoded()
    {
        IRModule module = new IRModule("Module1");
        module.addChild(new IRGlobal("a", Type.INTEGER, null, false));
        module.addChild(new IRGlobal("b", Type.INTEGER, null, false));
        module.addChild(new IRGlobal("c", Type.INTEGER, 12, false));
        module.addChild(new IRGlobal("d", Type.INTEGER, 12345, false));

        //newVar1;
        //newVar2 = newVar1 * var3;
        IRMethod m1 = new IRMethod("method1", Type.VOID, null, new Type[]
        {
            Type.INTEGER, Type.INTEGER, Type.INTEGER
        }, new String[]
        {
            "var1", "var2", "var3"
        });
        //test1 = 20;
        //test2 = 50;
        //test1 = test2;
        //test1 = 30;
        //test2 = a;
        m1.addChild(new IRAllocate("test1", Type.INTEGER, 20));
        m1.addChild(new IRAllocate("test2", Type.INTEGER, 50));
        m1.addChild(new IRAllocate("test1", Type.INTEGER, "test2", false));
        m1.addChild(new IRAllocate("test1", Type.INTEGER, 30));
        m1.addChild(new IRAllocate("test2", Type.INTEGER, "a", false));
        module.addChild(m1);

        //var1 = var2 * var3;
        IRMethod m2 = new IRMethod("method2", Type.VOID, null, new Type[]
        {
            Type.INTEGER, Type.INTEGER, Type.INTEGER
        }, new String[]
        {
            "var1", "var2", "var3"
        });
        IRStoreArith arith2 = new IRStoreArith("var1", Operation.MULT);
        arith2.setRhs(new IRLoad("var2"));
        arith2.setLhs(new IRLoad("var3"));
        m2.addChild(arith2);
        ArrayList<PairStringType> lis = new ArrayList<>();
        lis.add(new PairStringType("var1 = ", Type.STRING));
        lis.add(new PairStringType("2", Type.INTEGER));
        m2.addChild(new IRCall("println", "io", lis));
        module.addChild(m2);


        IRMethod main = new IRMethod("main", Type.VOID, "ret", null, null);
        module.addChild(main);

        return module;
    }*/

    public void optimize()
    {
        //use this only as a wrapper for (possibly static) methods of a separate class
        //in order to avoid putting too much logic in this single class

    }

    public void dataflowAnalysis()
    {
        //use this only as a wrapper for (possibly static) methods of a separate class
        //in order to avoid putting too much logic in this single class
    }

    public void allocateRegisters(int maxLocals)
    {
        //use this only as a wrapper for (possibly static) methods of a separate class
        //in order to avoid putting too much logic in this single class
    }

    public ArrayList<String> selectInstructions()
    {
        ArrayList<String> inst = new ArrayList<>();
        inst.addAll(root.getInstructions());
        inst.addAll(getMethodClInit());

        return inst;
    }

    private ArrayList<String> getMethodClInit()
    {
        ArrayList<String> inst = new ArrayList<>();

        ArrayList<String> globalStaticInstructions = getAllIRGlobalStaticInstructions();
        if(globalStaticInstructions.size() != 0)
        {
            inst.add(".method static public <clinit>()V \n");
            inst.add(".limit stack 255\n");  //TODO VER ESTES LIMIT
            inst.add(".limit locals 255\n");  //TODO VER ESTES LIMIT

            inst.addAll(globalStaticInstructions);

            inst.add("return \n");
            inst.add("end method\n");
        }

        return inst;
    }

    private ArrayList<String> getAllIRGlobalStaticInstructions()
    {
        ArrayList<String> irGlobalsWithStaticInstructions = new ArrayList<>();

       for(IRNode child : root.children)
       {
           if(child.nodeType.equals("Global"))
           {
               ArrayList<String> instructions = ((IRGlobal) child).getStaticArraysInstructions();
               if(instructions.size() != 0)
                   irGlobalsWithStaticInstructions.addAll(instructions);
           }
       }

       return irGlobalsWithStaticInstructions;
    }

    public String getModuleName()
    {
        return this.root.getName();
    }

    private void createModuleHHIR(ASTMODULE astModule)
    {
        String moduleName = astModule.name;
        root = new IRModule(moduleName);

        int moduleNumberChilds = astModule.jjtGetNumChildren();
        for (int i = 0; i < moduleNumberChilds; i++)
        {
            Node child = astModule.jjtGetChild(i);
            if (child instanceof ASTDECLARATION)
                createDeclarationHHIR((ASTDECLARATION) child);
            else
                createFunctionHHIR((ASTFUNCTION) child);
        }
    }

    private void createFunctionHHIR(ASTFUNCTION astFunction)
    {
        String functionId = astFunction.id;
        Type returnType = Type.VOID;
        String returnName = null;
        Type[] argumentsTypes = null;
        String[] argumentsNames = null;

        //indicates the index(child num) of the arguments. 0 if no return value, or 1 if has return value
        int argumentsIndex = 0;

        //get return value if existent
        SimpleNode currNode = (SimpleNode) astFunction.jjtGetChild(0);
        if (!(currNode instanceof ASTVARS) && !(currNode instanceof ASTSTATEMENTS)) //indicated that is the return variable
        {
            argumentsIndex++;
            if (currNode instanceof ASTSCALARELEMENT)
            {
                returnType = Type.INTEGER;
                returnName = ((ASTSCALARELEMENT) currNode).id;
            }
            else if (currNode instanceof ASTARRAYELEMENT)
            {
                returnType = Type.ARRAY;
                returnName = ((ASTARRAYELEMENT) currNode).id;
            }
            else
            {
                returnType = Type.VOID;
            }
        }

        //get arguments if existent
        currNode = (SimpleNode) astFunction.jjtGetChild(argumentsIndex);
        if (currNode instanceof ASTVARS)
        {
            int numArguments = currNode.jjtGetNumChildren();
            argumentsNames = new String[numArguments];
            argumentsTypes = new Type[numArguments];

            for (int i = 0; i < numArguments; i++)
            {
                SimpleNode child = (SimpleNode) currNode.jjtGetChild(i);
                if (child != null)
                {
                    if (child instanceof ASTSCALARELEMENT)
                    {
                        argumentsNames[i] = ((ASTSCALARELEMENT) child).id;
                        argumentsTypes[i] = Type.INTEGER;
                    } else
                    {
                        argumentsNames[i] = ((ASTARRAYELEMENT) child).id;
                        argumentsTypes[i] = Type.ARRAY;
                    }
                }
            }
        }
        IRMethod function = new IRMethod(functionId, returnType, returnName, argumentsTypes, argumentsNames);

        //TODO: debug
        if (functionDebug)
        {
            System.out.println("name= " + functionId);
            System.out.println("return type= " + returnType.toString());
            if (returnName != null)
                System.out.println("return name= " + returnName);

            if (argumentsTypes != null)
            {
                System.out.println("argumentsTypes= ");
                for (Type argumentsType : argumentsTypes)
                    System.out.println(argumentsType);
            }

            if (argumentsNames != null)
            {
                System.out.println("argumentsNames= ");
                for (String argumentsName : argumentsNames)
                    System.out.println(argumentsName);
            }
        }

        root.addChild(function);

        //parse statements
        if (!(currNode instanceof ASTSTATEMENTS))
            currNode = (SimpleNode) astFunction.jjtGetChild(++argumentsIndex);
        createStatementsHHIR((ASTSTATEMENTS) currNode, function);
    }

    private void createStatementsHHIR(ASTSTATEMENTS aststatements, IRMethod irmethod)
    {
        for (int i = 0; i < aststatements.jjtGetNumChildren(); i++)
        {
            SimpleNode child = (SimpleNode) aststatements.jjtGetChild(i);

            switch (child.toString())
            {
                case "ASSIGN":
                    createAssignHHIR(child, irmethod);
                    break;

                case "CALL":
                    irmethod.addChild(getCallHHIR((ASTCALL) child));
                    break;

                case "IF":
                    createIfHHIR((ASTIF) child, irmethod);
                    break;

                case "WHILE":
                    createWhileHHIR((ASTWHILE) child, irmethod);
                    break;

                default:
                    System.out.println("Undefined statement. The compiler will terminate.");
                    System.exit(-1);
            }
        }
    }

    private void createIfHHIR(ASTIF astIf, IRMethod irmethod)
    {
        /* using template:

                    <do the test>
                    boper …, lab_true
                    <false_body>
                    jump lab_end
            lab_true:
                    <true_body>
            lab_end:

         */

        String labelTrue = "true_" + irmethod.getName() + root.getAndIncrementCurrLabelNumber();
        createExprTestHHIR(astIf, irmethod, labelTrue, false);

        //false body
        if(astIf.jjtGetNumChildren() > 2)
        {
            ASTELSE astElse = (ASTELSE) astIf.jjtGetChild(2);
            ASTSTATEMENTS astElseStatements = (ASTSTATEMENTS) astElse.jjtGetChild(0);
            createStatementsHHIR(astElseStatements, irmethod);
        }

        //jump end
        String labelEnd = "if_" + irmethod.getName() + "_end" + root.getAndIncrementCurrLabelNumber();
        createJumpEndHHIR(irmethod, labelEnd);

        //label true
        IRLabel irLabelTrue = new IRLabel(labelTrue);
        irmethod.addChild(irLabelTrue);

        //true body
        ASTSTATEMENTS astIfStatements = (ASTSTATEMENTS) astIf.jjtGetChild(1);
        createStatementsHHIR(astIfStatements, irmethod);

        //label true
        IRLabel irLabelEnd = new IRLabel(labelEnd);
        irmethod.addChild(irLabelEnd);
    }

    private IRNode getLhsIRNode(ASTLHS astLhs)
    {
        Node child = astLhs.jjtGetChild(0);
        if(child instanceof ASTARRAYACCESS)
        {
            ASTARRAYACCESS astArrayAccess = (ASTARRAYACCESS) child;
            Variable variable = getArrayAccessIRNode(astArrayAccess);
            return new IRLoad(new VariableArray(astArrayAccess.arrayID, variable));
        }
        else
        {
            ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) child;
            Variable variable = new Variable(astScalarAccess.id, Type.INTEGER);
            return new IRLoad(variable);
        }
    }

    private IRNode getRhsIRNodeOfExprtest(ASTRHS astRhs)
    {
        ArrayList<IRNode> termNodes =  new ArrayList<>();
        int numTerms = astRhs.jjtGetNumChildren();
        for(int i = 0; i < numTerms; i++)
        {
            ASTTERM astTerm = (ASTTERM) astRhs.jjtGetChild(i);
            termNodes.add(getTermIRNode(astTerm));
        }

        //case just one term, return the respective IRNode, an IRoad
        if(numTerms == 1)
            return termNodes.get(0);

        //for multiples terms (2) return IRLoadArith
        IRLoadArith irLoadArith = new IRLoadArith(Operation.parseOperator(astRhs.operator));
        irLoadArith.addChild(termNodes.get(0));
        irLoadArith.addChild(termNodes.get(1));
        return irLoadArith;
    }

    private IRNode getTermIRNode(ASTTERM astTerm)
    {
        String operator = astTerm.operator;

        Integer integer = astTerm.integer;
        if(integer != null)
        {
            if(operator.equals("-"))
                integer *= -1;
            return new IRConstant(integer.toString());
        }

        Node astTermChild = astTerm.jjtGetChild(0);
        if(astTermChild instanceof ASTCALL)
            return getCallHHIR((ASTCALL) astTermChild);
        else if(astTermChild instanceof ASTARRAYACCESS)
        {
            Variable variable = getArrayAccessIRNode((ASTARRAYACCESS) astTermChild);
            return new IRLoad(variable);
        }
        else
            return getScalarAccessIRNode((ASTSCALARACCESS) astTermChild);
    }

    private IRNode getScalarAccessIRNode(ASTSCALARACCESS astScalarAccess)
    {
        String id = astScalarAccess.id;
        return new IRLoad(new Variable(id, Type.INTEGER));
    }

    /*private IRNode getIndexIRNode(ASTINDEX astIndex)
    {
        Integer indexValue = astIndex.indexValue;
        if(indexValue != null)
            return new IRConstant(indexValue.toString());

        String indexID = astIndex.indexID;
        return new IRLoad(indexID);
    }*/

    private void createWhileHHIR(ASTWHILE astWhile, IRMethod irmethod)
    {
        //TODO ver ainda que template usar
        /* using template:

            lab_init: <test>
                      boper …, lab_end
                      <body>
                      jump lab_init
            lab_end:
         */

        //label init
        String labelInit = "while_" + irmethod.getName() + "_init" + root.getAndIncrementCurrLabelNumber();
        IRLabel irLabelInit = new IRLabel(labelInit);
        irmethod.addChild(irLabelInit);

        //test
        String labelEnd = "while_" + irmethod.getName() + "_end" + root.getAndIncrementCurrLabelNumber();
        createExprTestHHIR(astWhile, irmethod, labelEnd, true);

        //body
        ASTSTATEMENTS astStatements = (ASTSTATEMENTS) astWhile.jjtGetChild(1);
        createStatementsHHIR(astStatements, irmethod);

        //jump init
        createJumpEndHHIR(irmethod, labelInit);

        IRLabel irLabelEnd = new IRLabel(labelEnd);
        irmethod.addChild(irLabelEnd);
    }

    private void createJumpEndHHIR(IRMethod irmethod, String labelEnd)
    {
        IRJump irJump = new IRJump(labelEnd);
        irmethod.addChild(irJump);
    }

    private void createExprTestHHIR(Node astNode, IRMethod irmethod, String label, boolean invert)
    {
        ASTEXPRTEST astExprtest = (ASTEXPRTEST) astNode.jjtGetChild(0);
        IRComparison irComparison = new IRComparison(astExprtest.operation, label, invert);

        ASTLHS astLhs = (ASTLHS) astExprtest.jjtGetChild(0);
        IRNode lhsIrNode = getLhsIRNode(astLhs);
        irComparison.setLhs(lhsIrNode);

        ASTRHS astRhs = (ASTRHS) astExprtest.jjtGetChild(1);
        IRNode rhsIrNode = getRhsIRNodeOfExprtest(astRhs);
        irComparison.setRhs(rhsIrNode);

        irmethod.addChild(irComparison);
    }

    /**
     * Retrieves the name and may retrieve also type, value and operation for
     * some variable
     *
     * @param child simplenode
     * @param irmethod irmethod
     */
    private void createAssignHHIR(SimpleNode child, IRMethod irmethod)
    {
        IRAssign irAssign = new IRAssign((ASTLHS) child.jjtGetChild(0), (ASTRHS) child.jjtGetChild(1));

        SimpleNode lhchild = (SimpleNode) irAssign.astlhs.jjtGetChild(0);
        switch (lhchild.toString())
        {
            case "ARRAYACCESS":
                irAssign.lhs =  getArrayAccessIRNode((ASTARRAYACCESS) lhchild);
                break;

            case "SCALARACCESS":
                ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) lhchild;

                irAssign.lhs = new Variable(astscalaraccess.id, Type.VARIABLE);
                break;
        }

        irAssign.operator = irAssign.astrhs.operator; /* operator == null? IRAllocate : IRStoreArith */
        int numChildren = irAssign.astrhs.jjtGetNumChildren();
        for (int i = 0; i < numChildren; i++)
        {
            SimpleNode rhchild = (SimpleNode) irAssign.astrhs.jjtGetChild(i);
            switch (rhchild.toString())
            {
                case "TERM":
                    ASTTERM term = (ASTTERM) rhchild;

                    if (term.integer != null)
                    {
                        String str_value = term.operator + term.integer;
                        irAssign.operands.add(new Variable(str_value, Type.INTEGER));
                    }
                    else
                    {
                        SimpleNode termChild = (SimpleNode) term.jjtGetChild(0);

                        switch (termChild.toString())
                        {
                            case "CALL":

                                ASTCALL astcall = (ASTCALL) termChild;
                                IRCall irCall = getIRCall(astcall, irAssign.lhs.getVar());

                                irAssign.operands.add(new VariableCall(null, Type.CALL, irCall));
                                break;

                            case "ARRAYACCESS":
                                ASTARRAYACCESS astarrayaccess = ((ASTARRAYACCESS) termChild);
                                ASTINDEX astindex = (ASTINDEX) termChild.jjtGetChild(0);
                                String arrayaccess = term.operator + astarrayaccess.arrayID;

                                if (astindex.indexID != null)
                                    irAssign.operands.add(new VariableArray(arrayaccess, new Variable(astindex.indexID, Type.VARIABLE)));
                                else
                                    irAssign.operands.add(new VariableArray(arrayaccess, new Variable(astindex.indexValue.toString(), Type.INTEGER)));
                                break;
                            case "SCALARACCESS":
                                String id = ((ASTSCALARACCESS) termChild).id;
                                irAssign.operands.add(new Variable(id, Type.VARIABLE));
                                break;
                        }
                    }
                    break;
                case "ARRAYSIZE":
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) rhchild;

                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        irAssign.operands.add(new Variable(astarraysize.integer.toString(), Type.INTEGER));
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        irAssign.operands.add(new Variable(astscalaraccess.id, Type.VARIABLE));
                    }
                    irAssign.isSize = true;
                    break;
            }
        }

        //TODO: Debug
        if (assignDebug)
        {
            System.out.println();
            System.out.println(irAssign.lhs.getVar() != null ? "lhsName = " + irAssign.lhs.getVar() : "null");
            //System.out.println(irAssign.lhs.getAt() != null ? "at = " + irAssign.atlhs.getVar() : "null");
            for (int i = 0; i < irAssign.operands.size(); i++)
            {
                System.out.println("operand = " + irAssign.operands.get(i));
                System.out.println(irAssign.isSize ? " .size" : "");
            }
            System.out.println(!irAssign.operator.equals("") ? "operator = " + irAssign.operator : "null");
            System.out.println();
        }

        createAssignIR(irAssign, irmethod);
    }

    private Variable getArrayAccessIRNode(ASTARRAYACCESS child)
    {
        ASTARRAYACCESS astarrayaccess = child;
        ASTINDEX astindex = (ASTINDEX) astarrayaccess.jjtGetChild(0);

        if (astindex.indexID != null)
            return new VariableArray(astarrayaccess.arrayID, new Variable(astindex.indexID, Type.VARIABLE));
        else
            return new VariableArray(astarrayaccess.arrayID, new Variable(astindex.indexValue.toString(), Type.INTEGER));
    }

    private void createAssignImmediateIR(IRAssign irAssign, IRMethod irmethod) {
        IRStoreCall irStoreCall;
        Variable variable = irAssign.operands.get(0);

        if (variable.getType().equals(Type.CALL)) {          // a = f1();
            if (irAssign.lhs.getType().equals(Type.VARIABLE)) {
                irStoreCall = new IRStoreCall(irAssign.lhs.getVar());
                irStoreCall.addChild(((VariableCall) variable).getIrCall());
                irmethod.addChild(irStoreCall);
            }
            else {
                irStoreCall = new IRStoreCall((VariableArray) irAssign.lhs);
                irStoreCall.addChild(((VariableCall) variable).getIrCall());
                irmethod.addChild(irStoreCall);
            }
        } else {
            if (variable.getType().equals(Type.ARRAY)) {
                if(irAssign.lhs.getType().equals(Type.ARRAY)) {
                    irmethod.addChild(new IRAllocate((VariableArray) irAssign.lhs,(VariableArray) variable));
                }
                else {
                    irmethod.addChild(new IRAllocate(irAssign.lhs,(VariableArray) variable));
                }
            } else {
                if (!irAssign.isSize) {
                    if (irAssign.lhs.getType().equals(Type.VARIABLE)) {
                        if(variable.getType().equals(Type.ARRAY)) {    // a = b[5]   // a = b[c]     // a = b[c.size]
                            irmethod.addChild(new IRAllocate(irAssign.lhs,(VariableArray) variable));
                        }
                        else {     // a = 3   // a = b     // a = b.size
                            irmethod.addChild(new IRAllocate(irAssign.lhs.getVar(), variable));
                        }
                    } else {                      // a[X] = 3     // a[X] = b
                        irmethod.addChild(new IRAllocate((VariableArray) irAssign.lhs, variable));
                    }
                } else {                          // a = [X]
                    irmethod.addChild(new IRAllocate(irAssign.lhs.getVar(), variable, Type.ARRAYSIZE));
                }
            }
        }
    }

    private void createAssignOperationIR(IRAssign irAssign, IRMethod irmethod) {
        IRStoreArith irStoreArith;

        Variable var1 = irAssign.operands.get(0);
        Variable var2 = irAssign.operands.get(1);

        if (irAssign.lhs.getType().equals(Type.VARIABLE)) {
            irStoreArith = new IRStoreArith(irAssign.lhs.getVar(), Operation.parseOperator(irAssign.operator));
        }
        else {
            irStoreArith = new IRStoreArith((VariableArray) irAssign.lhs, Operation.parseOperator(irAssign.operator));
        }

        if(var1.getType().equals(Type.CALL)) {           // a = f1() + X
            irStoreArith.setLhs(((VariableCall) var1).getIrCall());
        } else {
            if(var1.getType().equals(Type.INTEGER)) {    // a = 3 + X
                irStoreArith.setLhs(new IRConstant(var1.getVar()));
            }
            else {
                if(var1.getType().equals(Type.VARIABLE)) {   // a = b.size // a = b
                    irStoreArith.setLhs(new IRLoad(var1));
                } else {                    // a = b[c.size] // a = b[c]
                    irStoreArith.setLhs(new IRLoad((VariableArray) var1));
                }
            }
        }

        if(var2.getType().equals(Type.CALL)) {           // a = f1() + X
            irStoreArith.setRhs(((VariableCall) var2).getIrCall());
        } else {
            if(var2.getType().equals(Type.INTEGER)) {    // a = 3
                irStoreArith.setRhs(new IRConstant(var2.getVar()));
            }
            else {
                if(var2.getType().equals(Type.VARIABLE)) {   // a = b.size // a = b
                    irStoreArith.setRhs(new IRLoad(var2));
                } else {                    // a = b[c.size] // a = b[c]
                    irStoreArith.setRhs(new IRLoad(var2));
                }
            }
        }

        irmethod.addChild(irStoreArith);
    }

    private void createAssignIR(IRAssign irAssign, IRMethod irmethod) {
        if(irAssign.operator.equals("")) {          // a = IMMEDIATE
            createAssignImmediateIR(irAssign, irmethod);
        }
        else {                                      // a = OPERATION
            createAssignOperationIR(irAssign, irmethod);
        }
    }

    private IRCall getIRCall(ASTCALL astCall, String lhsVarName)
    {
        String moduleId = astCall.module;
        String methodId = astCall.method;
        if(moduleId == null)
            moduleId = root.getName();
        ArrayList<PairStringType> arguments = new ArrayList<>();

        if (astCall.jjtGetNumChildren() > 0)
        {
            ASTARGUMENTS astarguments = (ASTARGUMENTS) astCall.jjtGetChild(0);
            arguments = getFunctionCallArgumentsIds(astarguments);
        }

        return new IRCall(methodId, moduleId, arguments, lhsVarName);
    }

    private IRCall getCallHHIR(ASTCALL astCall)
    {
        String moduleId = astCall.module;
        String methodId = astCall.method;
        ArrayList<PairStringType> arguments = null;

        if (astCall.jjtGetNumChildren() > 0)
        {
            ASTARGUMENTS astarguments = (ASTARGUMENTS) astCall.jjtGetChild(0);
            arguments = getFunctionCallArgumentsIds(astarguments);
        }

        if (callDebug)
        {
            //TODO debug
            System.out.println("from getCallHHIR");
            System.out.println("moduleId= " + moduleId);
            System.out.println("methodId= " + methodId);

            if (arguments != null)
            {
                System.out.println("arguments");
                for (PairStringType argument : arguments)
                    System.out.println("value: " + argument.getString()
                            + "   type: " + argument.getType().toString());
            }
        }

        return getIRCall(astCall, null);
    }

    private ArrayList<PairStringType> getFunctionCallArgumentsIds(ASTARGUMENTS astArguments)
    {
        ArrayList<PairStringType> arguments = new ArrayList<>();
        int numArguments = astArguments.jjtGetNumChildren();
        for (int i = 0; i < numArguments; i++)
        {
            ASTARGUMENT astArgument = (ASTARGUMENT) astArguments.jjtGetChild(i);
            if (astArgument.intArg != null)
            {
                PairStringType pair = new PairStringType(astArgument.intArg.toString(), Type.INTEGER);
                arguments.add(pair);
                continue;
            }
            if (astArgument.stringArg != null)
            {
                PairStringType pair = new PairStringType(astArgument.stringArg, Type.STRING);
                arguments.add(pair);
                continue;
            }
            if (astArgument.idArg != null)
            {
                PairStringType pair = new PairStringType(astArgument.idArg, Type.INTEGER);
                arguments.add(pair);
            }
        }

        return arguments;
    }

    /**
     * Retrieves the name, type and value of the variable
     *
     * @param astdeclaration declaration to analyse
     */
    private void createDeclarationHHIR(ASTDECLARATION astdeclaration)
    {
        SimpleNode simpleNode = (SimpleNode) astdeclaration.jjtGetChild(0);
        Variable variable = null;
        Variable value = null;
        boolean arraySize = false;
        boolean initialized = true;

        switch (simpleNode.toString())
        {
            case "SCALARELEMENT":
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) simpleNode;
                variable = new Variable(astscalarelement.id, Type.VARIABLE);

                if (astdeclaration.jjtGetNumChildren() == 2)
                {
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);

                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        value = new Variable(astarraysize.integer.toString(), Type.INTEGER);
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        value = new Variable((astdeclaration.operator + astscalaraccess.id), Type.VARIABLE);
                    }

                    arraySize = true;

                    variable.setType(Type.ARRAY);
                } else
                {
                    String str_value = astdeclaration.operator + astdeclaration.integer;

                    if(astdeclaration.integer == null) {
                        initialized = false;
                    }
                    else {
                        value = new Variable(str_value, Type.INTEGER);
                    }

                }
                break;
            case "ARRAYELEMENT":
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) simpleNode;
                variable = new Variable(astarrayelement.id, Type.ARRAY);

                if (astdeclaration.jjtGetNumChildren() == 2)
                {
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);
                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        value = new Variable(astarraysize.integer.toString(), Type.INTEGER);
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        value = new Variable(astscalaraccess.id, Type.VARIABLE);
                    }

                    arraySize = true;
                } else
                {
                    String str_value;

                    if(astdeclaration.integer == null) {
                        initialized = false;
                    }
                    else {
                        str_value = astdeclaration.operator + astdeclaration.integer;
                        value = new Variable(str_value, Type.INTEGER);
                    }
                }
                break;
        }

        //TODO:DEBUG
        if (declarationDebug)
        {
            System.out.println();
            assert variable != null;
            System.out.println(variable.getVar() != null ? "name = " + variable.getVar() : "null");
            System.out.println(variable.getType() != null ? "type = " + variable.getType() : "null");
            if (value != null)
            System.out.println(value.getVar() != null ? "value = " + value.getVar() : "null");
            System.out.println();
        }

        if(!initialized) {
            root.addChild(new IRGlobal(variable));
        }

        assert variable != null;
        switch (variable.getType())
        {
            case INTEGER:
                root.addChild(new IRGlobal(variable, value));
                break;
            case ARRAY:
                if(arraySize) {
                    root.addChild(new IRGlobal(variable, value, Type.ARRAYSIZE));
                }
                else {
                    root.addChild(new IRGlobal(variable, value));
                }
                break;
        }
    }
}
