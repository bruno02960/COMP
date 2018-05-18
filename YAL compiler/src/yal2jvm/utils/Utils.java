package yal2jvm.utils;

import java.util.HashMap;
import java.util.Map;

import yal2jvm.symbol_tables.Symbol;

public class Utils
{
    public static HashMap<String, Symbol> copyHashMap(HashMap<String, Symbol> original)
    {
        HashMap<String, Symbol> copy = new HashMap<String, Symbol>();
        for (Map.Entry<String, Symbol> entry : original.entrySet())
            copy.put(entry.getKey(), entry.getValue().getCopy());

        return copy;
    }

    public static boolean isLastCharacterOfString(String character, String string)
    {
       return string.lastIndexOf(character) == string.length() - 1;
    }

    public static int stringArrayContains(String[] array, String string)
    {
       for(int i = 0; i < array.length; i++)
       {
           if(array[i].equals(string))
               return i;
       }

       return -1;
    }

    public static int stringArrayMatches(String[] array, String regex)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(array[i].matches(regex))
                return i;
        }

        return -1;
    }

    public static int stringArrayNotMatches(String[] array, String regex)
    {
        for(int i = 0; i < array.length; i++)
        {
            if(!array[i].matches(regex))
                return i;
        }

        return -1;
    }
}
