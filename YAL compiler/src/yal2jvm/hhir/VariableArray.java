package yal2jvm.hhir;

public class VariableArray extends Variable {
    private Variable at;

    VariableArray(String var, Variable at) {
        super(var, Type.ARRAY);
        this.at = at;
    }

    Variable getAt() {
        return at;
    }
}
