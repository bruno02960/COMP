package yal2jvm.hhir;

import yal2jvm.ast.ASTLHS;
import yal2jvm.ast.ASTRHS;

import java.util.ArrayList;

public class IRAssign {
    String operator;
    Variable atlhs = null;
    boolean isSize = false;

    Variable lhs = null;
    ASTLHS astlhs;
    ASTRHS astrhs;

    ArrayList<IRCall> calls = new ArrayList<>();
    ArrayList<Variable> operands = new ArrayList<>();
    ArrayList<Variable> at_op = new ArrayList<>();

    IRAssign(ASTLHS astlhs, ASTRHS astrhs) {
        this.astlhs = astlhs;
        this.astrhs = astrhs;
    }
}
