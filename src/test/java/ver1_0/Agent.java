package ver1_0;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.text.html.HTMLDocument.HTMLReader.ParagraphAction;

import org.apache.commons.io.filefilter.AndFileFilter;

import javassist.bytecode.stackmap.TypeData.UninitThis;


public class Agent {
//	public class Data {
////		Para thrust como es de 0 a 200 se usan 8 bits
//		private int normal_thrust = 0;
//		private int near_target_thrust = 0;
//		private int power_thrust = 0;
////		Para distance como es de 0 a 16000 se usan 14 bits
//		private int near_distance = 0;
//		private int min_power_distance = 0;
//		private int max_power_distance = 0;
//	}
//	
//	public void setUp(int normal_thrust, int near_target_thrust, int power_thrust,
//			int near_distance, int min_power_distance, int max_power_distance) {
//		Data datos = new Data();
//		datos.normal_thrust = normal_thrust;
//		datos.near_target_thrust = near_target_thrust;
//		datos.power_thrust = power_thrust;
//		datos.near_distance = near_distance;
//		datos.min_power_distance = min_power_distance;
//		datos.max_power_distance = max_power_distance;
//		
//	}
//	private static Data agent_data;
//	public Agent2() {
//		// TODO Auto-generated constructor stub
//	}

    public static void main(String[] args) {
    	
		int normal_thrust = Integer.parseInt(args[0]);
		int near_target_thrust = Integer.parseInt(args[1]);
		int power_thrust = Integer.parseInt(args[2]);
		int near_distance = Integer.parseInt(args[3]);
		int min_power_distance = Integer.parseInt(args[4]);
		int max_power_distance = Integer.parseInt(args[5]);
    	
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
//            int thrust = 150;
            int thrust = normal_thrust;
//            if(targ_point.distance(current) < 4000){
            if(targ_point.distance(current) < near_distance){
//                thrust = 60;
                thrust = near_target_thrust;
            }

            Point final_targ_point = new Point(0, 0);
            if(target != targets.size()-1) {
            	Point next_targ_point = targets.get(target + 1);
                System.err.println("Angulo ");
                System.err.println(angulo(targ_point, current, next_targ_point));
                double distance = targ_point.distance(current);
                System.err.println("Distancia ");
                System.err.println(distance);
                
//                if(distance > 4000 && distance < 7000){
                if(distance > min_power_distance && distance < max_power_distance){
//                    thrust = 200;
                    thrust = power_thrust;
                    System.err.println("Gira ");
                	Point aux_point = new Point(0, 0);

                	aux_point.x = targ_point.x - current.x;
                    aux_point.y = targ_point.y - current.y;
                	final_targ_point=aux_point.girar(angulo(current, targ_point, next_targ_point)/2);

                	final_targ_point.x = final_targ_point.x + current.x;
                	final_targ_point.y = final_targ_point.y + current.y;
                	
                }
            }
            if(final_targ_point.x == 0 && final_targ_point.y == 0) {
            	final_targ_point = targ_point;
            }
            System.err.println("final_targ_point ");
            System.err.println(final_targ_point);
            
            System.out.println(final_targ_point.x+ " " + final_targ_point.y + " " + thrust + " Agent 2"); // X Y THRUST MESSAGE
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
	    
	    Point girar(double angulo) {
	    	x = (int) ((this.x)*Math.cos(angulo) - (this.y)*Math.sin(angulo));
	    	y = (int) ((this.x)*Math.sin(angulo) + (this.y)*Math.cos(angulo));
	    	return new Point(x, y);
	    }
	    
	    @Override
	    public String toString() {
	      return String.format("[x: %d; y: %d]", this.x, this.y);
	    }
	}
    /***
    Funcion que calcula la diferencia de angulos entre dos rectas formadas
    por tres puntos a->b y a->c , es decir el angulo formado por las
    dos rectas (ang(a->c)-ang(a->b)), pero siempre positivo.
    Por lo tanto, obtenemos el angulo en sentido antihorario.
    @param uno : Primer punto.
    @param dos : Segundo punto.
    @param tres : Tercer punto.
    @return : Angulo en radianes con la diferencia entre las
    rectas uno-tres y uno-dos.
    ***/
	public static double angulo(Point uno,Point dos,Point tres){
	
	    //transladamos al origen de coordenadas los tres puntos
	    Point pi=new Point(dos.x-uno.x,dos.y-uno.y);
	    Point pj=new Point(tres.x-uno.x,tres.y-uno.y);
	    //calculamos su angulo de coordenada polar
	    double ang_pi=Math.atan2((double)pi.x,(double)pi.y);
	    double ang_pj=Math.atan2((double)pj.x,(double)pj.y);
	
	    //hallamos la diferencia
	    double ang = ang_pj-ang_pi;
	    return ang;
	    //Si el angulo es negativo le sumamos 2PI para obtener el
	    //angulo en el intervalo [0-2PI]; 
	    //siempre obtenemos �ngulos positivos (en sentido antihorario)
	//	    if (ang<0.0)
	//	    	return ang+(2.0*Math.PI);
	//	    else
	//	    	return ang;
	}//fin angulo

}