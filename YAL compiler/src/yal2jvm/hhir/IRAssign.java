package yal2jvm.hhir;

import yal2jvm.ast.ASTLHS;
import yal2jvm.ast.ASTRHS;

import java.util.ArrayList;

public class IRAssign {
    String lhsName = null;
    String size = null;
    String operator;
    String at_name = null;
    String lhsType = null;

    ASTLHS astlhs = null;
    ASTRHS astrhs = null;

    ArrayList<IRCall> calls = new ArrayList<>();
    ArrayList<String> operands = new ArrayList<>();
    ArrayList<String> types = new ArrayList<>();
    ArrayList<String> at_op = new ArrayList<>();
    ArrayList<Boolean> isSize = new ArrayList<>();

    IRAssign(ASTLHS astlhs, ASTRHS astrhs) {
        this.astlhs = astlhs;
        this.astrhs = astrhs;
    }
}
