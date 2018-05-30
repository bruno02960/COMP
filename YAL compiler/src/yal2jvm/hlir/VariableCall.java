package yal2jvm.hlir;

public class VariableCall extends Variable {
    IRCall irCall;

    VariableCall(String var, Type type, IRCall irCall) {
        super(var, type);
        this.irCall = irCall;
    }

    IRCall getIrCall(){
        return irCall;
    }
}
