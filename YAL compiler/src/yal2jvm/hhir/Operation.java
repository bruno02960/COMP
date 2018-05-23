package yal2jvm.hhir;

public enum Operation
{
    ADD,
    SUB,
    MULT,
    DIV,
    SHIFT_R,
    SHIFT_L,
    USHIFT_R,
    AND,
    OR,
    XOR;

    static Operation parseOperator(String operator) {
        switch(operator) {
            case "+":
                return ADD;
            case "-":
                return SUB;
            case "*":
                return MULT;
            case "/":
                return DIV;
            case ">>":
                return SHIFT_R;
            case "<<":
                return SHIFT_L;
            case ">>>":
                return USHIFT_R;
            case "&":
                return AND;
            case "|":
                return OR;
            case "^":
                return XOR;
        }

        return null;
    }
}