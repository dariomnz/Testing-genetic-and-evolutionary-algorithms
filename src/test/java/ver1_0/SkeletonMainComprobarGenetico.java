package ver1_0;

import java.io.File;
import java.util.Scanner;

import com.codingame.gameengine.runner.SoloGameRunner;

public class SkeletonMainComprobarGenetico {
    public static void main(String[] args) {
        // Uncomment this section and comment the other one to create a Solo Game
        /* Solo Game */
        SoloGameRunner gameRunner = new SoloGameRunner();
        String separator = System.getProperty("file.separator");
        String command = "java -cp \""+System.getProperty("user.dir")+separator+
        		"target"+separator+"test-classes\" ver1_0.Agent ";
        // Sets the player
        gameRunner.setAgent(command + get_best());
        // Sets a test case
        gameRunner.setTestCase("test1.json");

        gameRunner.start();
    }

    public static String getPathToResources() {
        String separator = System.getProperty("file.separator");
    	return System.getProperty("user.dir")+separator+"src"+separator+"main"+separator+"resources"+separator;
    }
    
    public static String get_best() {
	    String out = "";
    	try {
    		File file = new File(getPathToResources()+"result_genetico.txt");
    	    Scanner sc = new Scanner(file);

    	    Double best = 1000.0d;
    	    while (sc.hasNextLine()) {
    	    	String[] next_line = sc.nextLine().split(";");
    	    	if (Double.valueOf(next_line[0]) < best) {
					best = Double.valueOf(next_line[0]);
					out = next_line[1];
				}
    	    	
    	    }
           
            sc.close();
        }
        catch (Exception e) {
            System.out.println("Error al leer");
        }
    	return out;
	}
}
