import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


public class TInvariantChecker {
    private Process mProcess;
    private String outputFileLocation = "out/t_invariants_test.txt";

    public void runScript(){
        Process process;
        try{
            process = Runtime.getRuntime().exec(new String[]{"python" , "T-invariants.py"});
            mProcess = process;
        }catch(Exception e) {
            System.out.println("Exception Raised" + e.toString());
        }
        InputStream stdout = mProcess.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(stdout, StandardCharsets.UTF_8));

        try {
            String line;
            FileWriter file = new FileWriter(outputFileLocation);
            PrintWriter pw = new PrintWriter(file);
            while((line = reader.readLine()) != null){
               pw.println(line);
            }

            pw.close();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
