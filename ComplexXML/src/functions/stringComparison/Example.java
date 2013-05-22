package functions.stringComparison;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Example {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, InterruptedException {


            List<String> resultingTerm = new ArrayList<String>();
            resultingTerm.add("java");
            resultingTerm.add("-jar");
            resultingTerm.add("-Xms1500m");
            resultingTerm.add("-Xmx1500m");
            resultingTerm.add("Tagger.jar");
            resultingTerm.add("\"D:/conference/conf ont/cmt.owl\"");
            File directory = new File("tagger/Tagger.jar");
            ProcessBuilder p = new ProcessBuilder(resultingTerm);
            p.redirectErrorStream(true);
            p.directory(new File(directory.getParent()));
            final Process x = p.start();

            //scan whatever the matcher outputs on the command line
            //if any error occurs, this can be discovered by the output
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                public void run() {
                    Scanner scanner = new Scanner(x.getInputStream());
                    while (scanner.hasNextLine()) {
                        System.out.println(scanner.nextLine());

                    }
                    scanner.close();
                }
            });

            //process has been terminated
            x.waitFor();
            executorService.shutdown();

        
	}

}
