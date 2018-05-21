package yal2jvm.hhir;

public class Variable {
    private String var;
    private boolean sizeAccess = false;
    private Integer value = null;
    private Type type;

    Variable(String var, Type type) {
        this.var = var;
        this.type = type;

        /* VAR can be null in case of CALL type */
        //TODO: Verify if it is possible to use type to distinguish between variables and constants
        if(this.var!= null) {
            try {
                value = Integer.parseInt(this.var);
            } catch (NumberFormatException ignored) {
            }

            if(this.var.contains(".size")) {
                sizeAccess = true;
                this.var = this.var.split(".size")[0];
            }
        }
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public String getVar() {
        return var;
    }
}
