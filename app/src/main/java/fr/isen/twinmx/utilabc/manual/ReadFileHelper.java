package fr.isen.twinmx.utilabc.manual;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


/**
 * Created by pierredfc.
 */
public class ReadFileHelper {

    public static String readFile(Context context){
        String line;
        String content="";
        String current = Locale.getDefault().getLanguage();

        // English manual by default
        String fileName = "Manual_EN.txt";

        if (Locale.FRENCH.toString().equals(current))
        {
            fileName = "Manual_FR.txt";
        }

        InputStream input = null;
        try {

            input = context.getResources().getAssets().open(fileName);
            BufferedReader bfr = new BufferedReader(new InputStreamReader(input));
            while ((line=bfr.readLine())!=null){
                content+=line;
            }
            bfr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    public static List<ManualPage> getManualFromFile(Context context, String categorie) {
        String fileContent =readFile(context);
        List<ManualPage> listOfPage = new ArrayList<>();

        String arrayOfPages[] = fileContent.split("Title;");

        for (int i=0;i<arrayOfPages.length;i++){
            String pageContent[] = arrayOfPages[i].split(";");
            for(int u=0;u<pageContent.length;u++) {
                String tempo[]=pageContent[u].split("//");
                if(tempo[0].equals(categorie)) {
                    ManualPage page=new ManualPage(tempo[0],tempo[1],tempo[2]);
                    listOfPage.add(page);
                }
            }
        }
        return listOfPage ;
    }
}

