package yal2jvm.hlir;

public class VariableArray extends Variable {
    Variable at;

    VariableArray(String var, Variable at) {
        super(var, Type.ARRAY);
        this.at = at;
    }

    Variable getAt() {
        return at;
    }
}
