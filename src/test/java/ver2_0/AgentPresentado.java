package ver2_0;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

import org.apache.commons.io.filefilter.AndFileFilter;

import javassist.bytecode.stackmap.TypeData.UninitThis;


public class AgentPresentado {
    public static void main(String[] args) {
    	
		int thrust_1 = Integer.parseInt(args[0]);
		int thrust_2 = Integer.parseInt(args[1]);
		int thrust_3 = Integer.parseInt(args[2]);
		int distance_1 = Integer.parseInt(args[3]);
		int distance_2 = Integer.parseInt(args[4]);
		
		if (distance_1 > distance_2) {
			int aux = distance_1;
			distance_1 = distance_2;
			distance_2 = aux;
		}
    	
    	for (String string : args) {
			System.err.println(string);
		}
        Scanner scanner = new Scanner(System.in);
        int checkpoints = Integer.parseInt(scanner.nextLine());
        ArrayList<Point> targets = new ArrayList<>();
        for(int i = 0; i < checkpoints; i++){
            String[] line = scanner.nextLine().split(" ");
            System.err.println(line[0] + " " + line[1]);
            targets.add(new Point(Integer.parseInt(line[0]), Integer.parseInt(line[1])));
        }
        
        while (true) {
            String s = scanner.nextLine();
            System.err.println(s);
            String[] input = s.split(" ");
            // id x y vx vy angle
            int target = Integer.parseInt(input[0]);
            int x = Integer.parseInt(input[1]);
            int y = Integer.parseInt(input[2]);
            int vx = Integer.parseInt(input[3]);
            int vy = Integer.parseInt(input[4]);
            
            Point targ_point = targets.get(target);
            
            
            Point current = new Point(x, y);
            int thrust = 1; 
            if(targ_point.distance(current) < distance_1){
                thrust = thrust_1;
            }else if (targ_point.distance(current) < distance_2) {
                thrust = thrust_2;
			}else {
                thrust = thrust_3;
			}

            System.out.println(targ_point.x+ " " + targ_point.y + " " + thrust + " Agent 2"); // X Y THRUST MESSAGE
        }
    }

	private static class Point{
	    public int x, y;
	    public Point(int x, int y){
	        this.x = x;
	        this.y = y;
	    }
	
	    double distance(Point p) {
	        return Math.sqrt((this.x - p.x) * (this.x - p.x) + (this.y - p.y) * (this.y - p.y));
	    }
	    
	    @Override
	    public String toString() {
	      return String.format("[x: %d; y: %d]", this.x, this.y);
	    }
	}

}