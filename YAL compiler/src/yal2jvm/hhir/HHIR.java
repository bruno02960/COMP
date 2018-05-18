package yal2jvm.hhir;

import java.util.ArrayList;

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

    public IRModule createHardcoded()
    {
        IRModule module = new IRModule("Module1");
        module.addChild(new IRGlobal("a", Type.INTEGER, null));
        module.addChild(new IRGlobal("b", Type.INTEGER, null));
        module.addChild(new IRGlobal("c", Type.INTEGER, 12));
        module.addChild(new IRGlobal("d", Type.INTEGER, 12345));

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
        m1.addChild(new IRAllocate("test1", Type.INTEGER, "test2"));
        m1.addChild(new IRAllocate("test1", Type.INTEGER, 30));
        m1.addChild(new IRAllocate("test2", Type.INTEGER, "a"));
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
    }

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
        return root.getInstructions();
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
                    createCallHHIR((ASTCALL) child, irmethod);
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

        String labelTrue = "if_" + irmethod.getName() + "_true" + root.getAndIncrementCurrLabelNumber();
        createExprTestHHIR(astIf, irmethod, labelTrue);

        //false body
        if(astIf.jjtGetNumChildren() > 2)
        {
            ASTELSE astElse = (ASTELSE) astIf.jjtGetChild(2);
            ASTSTATEMENTS astElseStatements = (ASTSTATEMENTS) astElse.jjtGetChild(0);
            createStatementsHHIR(astElseStatements, irmethod);
        }

        //jump end
        String labelEnd = "if_" + irmethod.getName() + "_end" + root.getAndIncrementCurrLabelNumber();
        createJumpEndHHIR(irmethod, "if");

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

    private IRNode getLhsIrNode(ASTLHS astLhs)
    {
        Node child = astLhs.jjtGetChild(0);
        if(child instanceof ASTARRAYACCESS)
        {
            ASTARRAYACCESS astArrayAccess = (ASTARRAYACCESS) child;
            IRNode indexNode = getIndexIrNode((ASTINDEX) child.jjtGetChild(0));
            return new IRLoad(astArrayAccess.arrayID, indexNode);
        }
        else
        {
            ASTSCALARACCESS astScalarAccess = (ASTSCALARACCESS) child;
            String id = astScalarAccess.id;
            int indexOfSize = id.indexOf(".size");
            if(indexOfSize != -1)
            {
                id = id.substring(0, indexOfSize);
                return new IRLoad(id, true);
            }

            return new IRLoad(id);
        }
    }

    private IRNode getRhsIrNode(ASTRHS astRhs)
    {
        Node child = astRhs.jjtGetChild(0);
        if(child instanceof ASTARRAYSIZE)
        {
            //TODO: ARRAY SIZE
        }
        else
        {
            //TODO terms
        }
        return new IRNode()
        {
            @Override
            public ArrayList<String> getInstructions()
            {
                return null;
            }
        };
    }

    private IRNode getIndexIrNode(ASTINDEX astIndex)
    {
        Integer indexValue = astIndex.indexValue;
        if(indexValue != null)
            return new IRConstant(indexValue.toString());

        String indexID = astIndex.indexID;
        return new IRLoad(indexID);
    }

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
        createExprTestHHIR(astWhile, irmethod, labelEnd);

        //jump end
        createJumpEndHHIR(irmethod, labelEnd);

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

    private void createExprTestHHIR(Node astNode, IRMethod irmethod, String label)
    {
        ASTEXPRTEST astExprtest = (ASTEXPRTEST) astNode.jjtGetChild(0);
        IRComparison irComparison = new IRComparison(astExprtest.operation, label, false);

        ASTLHS astLhs = (ASTLHS) astExprtest.jjtGetChild(0);
        IRNode lhsIrNode = getLhsIrNode(astLhs);
        irComparison.setLhs(lhsIrNode);

        ASTRHS astRhs = (ASTRHS) astExprtest.jjtGetChild(1);
        IRNode rhsIrNode = getRhsIrNode(astRhs);
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
        String lhsName = null;
        String size = null;
        String operator;
        String at_name = null;
        String lhsType = null;

        ASTLHS astlhs = (ASTLHS) child.jjtGetChild(0);
        ASTRHS astrhs = (ASTRHS) child.jjtGetChild(1);

        ArrayList<IRCall> calls = new ArrayList<>();
        ArrayList<String> operands = new ArrayList<>();
        ArrayList<String> types = new ArrayList<>();
        ArrayList<String> at_op = new ArrayList<>();
        ArrayList<Boolean> isSize = new ArrayList<>();

        SimpleNode lhchild = (SimpleNode) astlhs.jjtGetChild(0);
        switch (lhchild.toString())
        {
            case "ARRAYACCESS":
                ASTARRAYACCESS astarrayaccess = (ASTARRAYACCESS) lhchild;
                ASTINDEX astindex = (ASTINDEX) astarrayaccess.jjtGetChild(0);

                lhsName = astarrayaccess.arrayID;

                if (astindex.indexID != null)
                {
                    at_name = astindex.indexID;
                } else
                {
                    at_name = astindex.indexValue.toString();
                }
                lhsType = "ARRAY";
                break;
            case "SCALARACCESS":
                ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) lhchild;
                lhsName = astscalaraccess.id;
                lhsType = "VAR";
                break;
        }

        operator = astrhs.operator; /* operator == null? IRAllocate : IRStoreArith */
        int numChildren = astrhs.jjtGetNumChildren();
        for (int i = 0; i < numChildren; i++)
        {
            SimpleNode rhchild = (SimpleNode) astrhs.jjtGetChild(i);
            switch (rhchild.toString())
            {
                case "TERM":
                    ASTTERM term = (ASTTERM) rhchild;

                    if (term.integer != null)
                    {
                        types.add("INTEGER");
                        String str_value = term.operator + term.integer;
                        operands.add(str_value);
                        at_op.add("-1");
                        calls.add(null);
                        isSize.add(false);
                    }
                    else
                    {
                        SimpleNode termChild = (SimpleNode) term.jjtGetChild(0);

                        switch (termChild.toString())
                        {
                            case "CALL":
                                ASTCALL astcall = (ASTCALL) termChild;

                                isSize.add(false);
                                types.add("CALL");
                                operands.add(null);
                                at_op.add("-1");

                                IRCall irCall = getIRCall(astcall);

                                calls.add(irCall);
                                break;

                            case "ARRAYACCESS":
                                ASTARRAYACCESS astarrayaccess = ((ASTARRAYACCESS) termChild);
                                ASTINDEX astindex = (ASTINDEX) termChild.jjtGetChild(0);
                                String arrayaccess = term.operator + astarrayaccess.arrayID;
                                at_op.add((astindex.indexID != null ? astindex.indexID : astindex.indexValue.toString()));
                                if (astindex.indexID != null && astindex.indexID.contains(".size"))
                                    isSize.add(true);
                                else
                                    isSize.add(false);
                                types.add("ARRAY");
                                operands.add(arrayaccess);
                                break;
                            case "SCALARACCESS":
                                String id = ((ASTSCALARACCESS) termChild).id;
                                at_op.add("-1");
                                calls.add(null);
                                if (id.contains(".size"))
                                {
                                    isSize.add(true);
                                    id = id.split(".size")[0];
                                } else
                                {
                                    isSize.add(false);
                                }
                                types.add("VAR");
                                operands.add(term.operator + id);
                                break;
                        }
                    }
                    break;
                case "ARRAYSIZE":
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) rhchild;

                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        size = astarraysize.integer.toString();
                        operands.add(astarraysize.integer.toString());
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        size = astscalaraccess.id;

                        if (size.contains(".size"))
                        {
                            isSize.add(true);
                            size = size.split(".size")[0];
                        }
                        operands.add(astscalaraccess.id);
                    }
                    types.add("SIZE");
                    isSize.add(false);
                    at_op.add("-1");
                    break;
            }
        }

        //TODO: Debug
        if (assignDebug)
        {
            System.out.println();
            System.out.println(lhsName != null ? "lhsName = " + lhsName : "null");
            System.out.println(at_name != null ? "at = " + at_name : "null");
            for (int i = 0; i < operands.size(); i++)
            {
                System.out.println("operand = " + operands.get(i));
                System.out.println(isSize.get(i) ? " .size" : "");
                System.out.println(!at_op.get(i).equals("-1") ? "at = " + at_op.get(i) : "null");
            }
            System.out.println(!operator.equals("") ? "operator = " + operator : "null");
            System.out.print((size != null) ? ("size = " + size) : "null");
            System.out.println();
        }

        createAssignIR(operator, types, lhsType, lhsName, calls, irmethod, size, operands, at_name, isSize, at_op);
    }

    private void createAssignImmediateIR(ArrayList<String> types, String lhsType, String lhsName, ArrayList<IRCall> calls, IRMethod irmethod, String size, ArrayList<String> operands, String at_name, ArrayList<Boolean> isSize, ArrayList<String> at_op) {
        IRStoreCall irStoreCall = null;

        String type = types.get(0);
        assert lhsType != null;
        if (type.equals("CALL")) {          // a = f1();
            if (lhsType.equals("VAR")) {
                irStoreCall = new IRStoreCall(lhsName);
                irStoreCall.addChild(calls.get(0));
                irmethod.addChild(irStoreCall);
            }
            else {
                if(at_name.contains(".size")) {
                    String index = at_name.split(".size")[0];
                    irStoreCall = new IRStoreCall(lhsName, index, true);
                } else {
                    irStoreCall = new IRStoreCall(lhsName, at_name);
                }

                irStoreCall.addChild(calls.get(0));
                irmethod.addChild(irStoreCall);
            }
        } else {
            if (type.equals("INTEGER")) {   // a = 3
                if (lhsType.equals("VAR")) {
                    irmethod.addChild(new IRAllocate(lhsName, Type.INTEGER, Integer.parseInt(operands.get(0))));
                }
                else {                      // a[X] = 3
                    irmethod.addChild(new IRAllocate(lhsName, Type.ARRAY, Integer.parseInt(operands.get(0)), at_name));
                }
            } else {
                if(type.equals("VAR")) {
                    if (!isSize.get(0)) {       // a = b
                        if (lhsType.equals("VAR")) {
                            irmethod.addChild(new IRAllocate(lhsName, Type.INTEGER, operands.get(0)));
                        }
                        else {                  // a[X] = b
                            irmethod.addChild(new IRAllocate(lhsName, Type.ARRAY, operands.get(0), at_name));
                        }
                    } else {                    // a = b.size
                        irmethod.addChild(new IRAllocate(lhsName, Type.INTEGER, operands.get(0), true));
                    }
                }
                else {                          // a = [X]
                    //TODO: IR that accepts size as rhs
                }
            }
        }
    }

    private void createAssignOperationIR(String operator, ArrayList<String> types, String lhsType, String lhsName, ArrayList<IRCall> calls, IRMethod irmethod, String size, ArrayList<String> operands, String at_name, ArrayList<Boolean> isSize, ArrayList<String> at_op) {
        IRStoreArith irStoreArith = null;

        String type1 = types.get(0);
        String type2 = types.get(1);

        if (lhsType.equals("VAR")) {
            irStoreArith = new IRStoreArith(lhsName, Operation.parseOperator(operator));
        }
        else {
            if(at_name.contains(".size")) {
                String index = at_name.split(".size")[0];
                irStoreArith = new IRStoreArith(lhsName, Operation.parseOperator(operator), index, true);
            } else {
                irStoreArith = new IRStoreArith(lhsName, Operation.parseOperator(operator), at_name);
            }
        }

        if(type1.equals("CALL")) {           // a = f1() + X
            irStoreArith.setRhs(calls.get(0));
        } else {
            if(type1.equals("INTEGER")) {    // a = 3
                irStoreArith.setRhs(new IRConstant(operands.get(0)));
            }
            else {
                if(type1.equals("VAR")) {
                    if(isSize.get(0)) {         // a = b.size
                        irStoreArith.setRhs(new IRLoad(operands.get(0), true));
                    } else {                   // a = b
                        irStoreArith.setRhs(new IRLoad(operands.get(0)));
                    }
                }
                else {
                    if(isSize.get(0)) {    // a = b[c.size]
                        irStoreArith.setRhs(new IRLoad(operands.get(0), at_op.get(0), true));
                    }
                    else {                  // a = b[X]
                        irStoreArith.setRhs(new IRLoad(operands.get(0), at_op.get(0)));
                    }
                }
            }
        }

        if(type2.equals("CALL")) {           // a = f1() + X
            irStoreArith.setRhs(calls.get(1));
        }
        else {
            if(type2.equals("INTEGER")) {    // a = 3
                irStoreArith.setRhs(new IRConstant(operands.get(1)));
            }
            else {
                if(type2.equals("VAR")) {
                    if(isSize.get(1)) {         // a = b.size
                        irStoreArith.setRhs(new IRLoad(operands.get(1), true));
                    } else {                   // a = b
                        irStoreArith.setRhs(new IRLoad(operands.get(1)));
                    }
                }
                else {
                    if(isSize.get(1)) {    // a = b[c.size]
                        irStoreArith.setRhs(new IRLoad(operands.get(1), at_op.get(1), true));
                    }
                    else {                  // a = b[X]
                        irStoreArith.setRhs(new IRLoad(operands.get(1), at_op.get(1)));
                    }
                }
            }
        }

        irmethod.addChild(irStoreArith);
    }

    private void createAssignIR(String operator, ArrayList<String> types, String lhsType, String lhsName, ArrayList<IRCall> calls, IRMethod irmethod, String size, ArrayList<String> operands, String at_name, ArrayList<Boolean> isSize, ArrayList<String> at_op) {
        if(operator.equals("")) {                   // a = IMMEDIATE
            createAssignImmediateIR(types, lhsType, lhsName, calls, irmethod, size, operands, at_name, isSize, at_op);
        }
        else {                                      // a = OPERATION
            createAssignOperationIR(operator, types, lhsType, lhsName, calls, irmethod, size, operands, at_name, isSize, at_op);
        }
    }

    private IRCall getIRCall(ASTCALL astCall)
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

        return new IRCall(methodId, moduleId, arguments);
    }

    private void createCallHHIR(ASTCALL astCall, IRNode irNode)
    {
        String moduleId = astCall.module;
        String methodId = astCall.method;
        ArrayList<PairStringType> arguments = null;

        if (astCall.jjtGetNumChildren() > 0)
        {
            ASTARGUMENTS astarguments = (ASTARGUMENTS) astCall.jjtGetChild(0);
            arguments = getFunctionCallArgumentsIds(astarguments);
        }

        IRCall irCall = getIRCall(astCall);
        irNode.addChild(irCall);

        if (callDebug)
        {
            //TODO debug
            System.out.println("from createCallHHIR");
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

        String name = null;
        Type type = null;
        String variable = null;
        boolean isSize = false;
        Integer value = null;
        int size = -1;

        switch (simpleNode.toString())
        {
            case "SCALARELEMENT":
                ASTSCALARELEMENT astscalarelement = (ASTSCALARELEMENT) simpleNode;
                name = astscalarelement.id;

                if (astdeclaration.jjtGetNumChildren() == 2)
                {
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);

                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        size = astarraysize.integer;
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        variable = astdeclaration.operator + astscalaraccess.id;

                        if (variable.contains(".size"))
                        {
                            isSize = true;
                            variable = variable.split(".size")[0];
                        }
                    }

                    type = Type.ARRAY;
                } else
                {
                    type = Type.INTEGER;
                    String str_value = astdeclaration.operator + astdeclaration.integer;
                    if (!str_value.equals("null"))
                        value = Integer.parseInt(str_value);
                }
                break;
            case "ARRAYELEMENT":
                ASTARRAYELEMENT astarrayelement = (ASTARRAYELEMENT) simpleNode;
                name = astarrayelement.id;
                type = Type.ARRAY;

                if (astdeclaration.jjtGetNumChildren() == 2)
                {
                    ASTARRAYSIZE astarraysize = (ASTARRAYSIZE) astdeclaration.jjtGetChild(1);
                    if (astarraysize.jjtGetNumChildren() == 0)
                    {
                        size = astarraysize.integer;
                    } else
                    {
                        ASTSCALARACCESS astscalaraccess = (ASTSCALARACCESS) astarraysize.jjtGetChild(0);
                        variable = astscalaraccess.id;

                        if (variable.contains(".size"))
                        {
                            isSize = true;
                            variable = variable.split(".size")[0];
                        }
                    }
                } else
                {
                    String str_value = astdeclaration.operator + astdeclaration.integer;
                    if (!str_value.equals("null"))
                        value = Integer.parseInt(str_value);
                }
                break;
        }

        //TODO:DEBUG
        if (declarationDebug)
        {
            System.out.println();
            System.out.println(name != null ? "name = " + name : "null");
            System.out.println(type != null ? "type = " + type : "null");
            System.out.print(variable != null ? "variable = " + variable : "null");
            System.out.println(isSize ? " .size" : "");
            System.out.println(value != null ? "value = " + value : "null");
            System.out.println(size != -1 ? "size = " + size : "null");
            System.out.println();
        }

        assert type != null;
        switch (type)
        {
            case INTEGER:
                root.addChild(new IRGlobal(name, type, value));
                break;
            case ARRAY:
                root.addChild(new IRGlobal(name, type, value, size));
                break;
        }
    }
}
