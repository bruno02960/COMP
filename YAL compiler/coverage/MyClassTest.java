import org.junit.Test;
import yal2jvm.HHIR.HHIR;
import yal2jvm.SemanticAnalysis.ModuleAnalysis;
import yal2jvm.Yal2jvm;
import yal2jvm.ast.ParseException;
import yal2jvm.ast.SimpleNode;
import yal2jvm.ast.YalParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class MyClassTest {
    static Yal2jvm instance = null;
    @Test
    public void testSomething() throws FileNotFoundException {
        File folder = new File("examples");
        YalParser parser = null;

        ArrayList<String> listFileNames = listFilesForFolder(folder);
        for (int i = 0; i < listFileNames.size(); i++) {
            if (listFileNames.get(i).contains(".yal")) {
                SimpleNode root = null;
                System.out.println(listFileNames.get(i) + "###########\u005cn\u005cn");
                if(parser == null)
                    parser = new YalParser(new FileInputStream("examples\\" + listFileNames.get(i)));
                else
                    parser.ReInit(new FileInputStream("examples\\" + listFileNames.get(i)));
                try {
                    root = parser.Module();
                    root.dump("");
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                ModuleAnalysis moduleAnalysis = new ModuleAnalysis(root);
                moduleAnalysis.parse();

                HHIR hhir = new HHIR(root);

            }
        }
    }

    public static ArrayList<String> listFilesForFolder(final File folder)
    {
        ArrayList<String> list = new ArrayList<>();
        for (final File fileEntry : Objects.requireNonNull(folder.listFiles()))
        {
            if (fileEntry.isDirectory())
                list.addAll(listFilesForFolder(fileEntry));
            else
                list.add(fileEntry.getName());
        }
        return list;
    }
}