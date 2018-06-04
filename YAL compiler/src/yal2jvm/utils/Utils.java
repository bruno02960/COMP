package yal2jvm.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import yal2jvm.symbol_tables.Symbol;

/**
 *
 */
public class Utils
{
    /**
     *
     * @param original
     * @return
     */
    public static HashMap<String, Symbol> copyHashMap(HashMap<String, Symbol> original)
    {
        HashMap<String, Symbol> copy = new HashMap<String, Symbol>();
        for (Map.Entry<String, Symbol> entry : original.entrySet())
            copy.put(entry.getKey(), entry.getValue().getCopy());

        return copy;
    }

    /**
     *
     * @param character
     * @param string
     * @return
     */
    public static boolean isLastCharacterOfString(String character, String string)
    {
       return string.lastIndexOf(character) == string.length() - 1;
    }

    /**
     *
     * @param array
     * @param string
     * @return
     */
    public static int stringArrayContains(String[] array, String string)
    {
       for(int i = 0; i < array.length; i++)
       {
           if(array[i].equals(string))
               return i;
       }

       return -1;
    }

    /**
     *
     * @param array
     * @param regex
     * @return
     */
    public static int stringArrayMatches(String[] array, String regex)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(array[i].matches(regex))
                return i;
        }

        return -1;
    }

    /**
     *
     * @param array
     * @param regex
     * @return
     */
    public static int stringArrayNotMatches(String[] array, String regex)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(!array[i].matches(regex))
                return i;
        }

        return -1;
    }

    /**
     *
     * @param var1
     * @param var2
     * @param operator
     * @return
     */
    public static int getOperationValue(String var1, String var2, String operator)
    {
        switch(operator)
        {
            case "+":
                return Integer.parseInt(var1) + Integer.parseInt(var2);
            case "-":
                return Integer.parseInt(var1) - Integer.parseInt(var2);
            case "*":
                return Integer.parseInt(var1) * Integer.parseInt(var2);
            case "/":
                return Integer.parseInt(var1) / Integer.parseInt(var2);
            case ">>":
                return Integer.parseInt(var1) >> Integer.parseInt(var2);
            case "<<":
                return Integer.parseInt(var1) << Integer.parseInt(var2);
            case ">>>":
                return Integer.parseInt(var1) >>> Integer.parseInt(var2);
            case "&":
                return Integer.parseInt(var1) & Integer.parseInt(var2);
            case "|":
                return Integer.parseInt(var1) | Integer.parseInt(var2);
            case "^":
                return Integer.parseInt(var1) ^ Integer.parseInt(var2);
        }

        return 0;
    }

    /**
     *
     * @param set
     * @param <T>
     * @return
     */
    public static <T> ArrayList<T> setToList(TreeSet<T> set)
    {
    	ArrayList<T> list = new ArrayList<T>();
    	for (T elem : set)
    		list.add(elem);
    	return list;
    }
}
