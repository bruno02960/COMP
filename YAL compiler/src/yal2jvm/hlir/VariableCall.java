package yal2jvm.hlir;

/**
 *
 */
public class VariableCall extends Variable {
    IRCall irCall;

    /**
     *
     * @param var
     * @param type
     * @param irCall
     */
    VariableCall(String var, Type type, IRCall irCall) {
        super(var, type);
        this.irCall = irCall;
    }

    /**
     *
     * @return
     */
    IRCall getIrCall(){
        return irCall;
    }
}
