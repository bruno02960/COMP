package yal2jvm.hlir;

import yal2jvm.ast.ASTLHS;
import yal2jvm.ast.ASTRHS;

import java.util.ArrayList;

public class IRAssign {
    String operator;
    boolean isSize = false;

    Variable lhs = null;
    ASTLHS astlhs;
    ASTRHS astrhs;

    ArrayList<Variable> operands = new ArrayList<>();

    IRAssign(ASTLHS astlhs, ASTRHS astrhs) {
        this.astlhs = astlhs;
        this.astrhs = astrhs;
    }
}
