package yal2jvm.hlir;

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

    /**
     *
     * @param operator
     * @return
     */
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

    /**
     *
     * @return
     */
    String getSymbol() {
        switch(this) {
            case ADD:
                return "+";
            case SUB:
                return "-";
            case MULT:
                return "*";
            case DIV:
                return "/";
            case SHIFT_R:
                return ">>";
            case SHIFT_L:
                return "<<";
            case USHIFT_R:
                return ">>>";
            case AND:
                return "&";
            case OR:
                return "|";
            case XOR:
                return "^";
        }

        return "";
    }
}