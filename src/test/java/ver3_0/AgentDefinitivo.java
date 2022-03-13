package ver3_0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.commons.io.filefilter.AndFileFilter;

import com.google.gson.annotations.Since;

public class AgentDefinitivo {
    public static void main(String[] args) {
    	int inicio = 0;
    	int[] optimiza_angulos_checkpoint1_vaina_checkpoint2 = new int[1];
    	int[] optimiza_angulos_vaina_checkpoint_velocidadVaina = new int[1];
    	int[] optimiza_distancia_vaina_checkpoint = new int[2];
    	int[] optimiza_velocidad_actual = new int[2];
    	int[] optimiza_distancias_checkpoint1_checkpoint2 = new int[1];
    	int[] optimiza_thrusts = new int[72];
    	
    	for (int i = 0; i < optimiza_angulos_checkpoint1_vaina_checkpoint2.length; i++) {
    		optimiza_angulos_checkpoint1_vaina_checkpoint2[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}
    	
    	for (int i = 0; i < optimiza_angulos_vaina_checkpoint_velocidadVaina.length; i++) {
    		optimiza_angulos_vaina_checkpoint_velocidadVaina[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}

    	for (int i = 0; i < optimiza_distancia_vaina_checkpoint.length; i++) {
    		optimiza_distancia_vaina_checkpoint[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}

    	for (int i = 0; i < optimiza_velocidad_actual.length; i++) {
    		optimiza_velocidad_actual[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}
    	for (int i = 0; i < optimiza_distancias_checkpoint1_checkpoint2.length; i++) {
    		optimiza_distancias_checkpoint1_checkpoint2[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}

    	for (int i = 0; i < optimiza_thrusts.length; i++) {
    		optimiza_thrusts[i] = Integer.parseInt(args[inicio]);
    		inicio++;
		}
		

    	Arrays.sort(optimiza_angulos_checkpoint1_vaina_checkpoint2);
    	Arrays.sort(optimiza_angulos_vaina_checkpoint_velocidadVaina);
    	Arrays.sort(optimiza_distancia_vaina_checkpoint);
    	Arrays.sort(optimiza_velocidad_actual);
    	
    	for (String string : args) {
			System.err.println(string);
		}
    	
        @SuppressWarnings("resource")
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
            Point next_targ_point = null;
            try {
                next_targ_point = targets.get(target + 1);
				
			} catch (Exception e) {
                next_targ_point = targets.get(target);
			}

            Point current = new Point(x, y);
            Point current_vel = new Point(vx, vy);
            
            Double angulo_checkpoint1_vaina_checkpoint2 = Math.abs(angulo(targ_point,current , next_targ_point));
            Double angulo_vaina_checkpoint_velocidadVaina = Math.abs(angulo(current, current.add(current_vel), targ_point));
			Double distancia_vaina_checkpoint = targ_point.distance(current);
			Double velocidad_actual = current_vel.modulo();
			Double distancia_checkpoint1_checkpoint2 = targ_point.distance(next_targ_point);
			

			int key_distancia_vaina_checkpoint = 0;
			if (distancia_vaina_checkpoint < optimiza_distancia_vaina_checkpoint[0]) {
				key_distancia_vaina_checkpoint = 0;
			}else if (distancia_vaina_checkpoint < optimiza_distancia_vaina_checkpoint[1]) {
				key_distancia_vaina_checkpoint = 1;
			}else{
				key_distancia_vaina_checkpoint = 2;
			}

			int key_velocidad_actual = 0;
            if (velocidad_actual < optimiza_velocidad_actual[0]) {
				key_velocidad_actual = 0;
			}else if (velocidad_actual < optimiza_velocidad_actual[1]) {
				key_velocidad_actual = 1;
			}else{
				key_velocidad_actual = 2;
			}
			
			int key_distancia_checkpoint1_checkpoint2 = 0;
            if (distancia_checkpoint1_checkpoint2 < optimiza_distancias_checkpoint1_checkpoint2[0]) {
				key_distancia_checkpoint1_checkpoint2 = 0;
			}else{
				key_distancia_checkpoint1_checkpoint2 = 1;
			}

			int key_angulo_checkpoint1_vaina_checkpoint2 = 0;
            if (angulo_checkpoint1_vaina_checkpoint2 < optimiza_angulos_checkpoint1_vaina_checkpoint2[0]) {
				key_angulo_checkpoint1_vaina_checkpoint2 = 0;
			}else{
				key_angulo_checkpoint1_vaina_checkpoint2 = 1;
			}
            
            int key_angulo_vaina_checkpoint_velocidadVaina = 0;
            if (angulo_vaina_checkpoint_velocidadVaina < optimiza_angulos_vaina_checkpoint_velocidadVaina[0]) {
				key_angulo_vaina_checkpoint_velocidadVaina = 0;
			}else{
				key_angulo_vaina_checkpoint_velocidadVaina = 1;
			}

            int key = calc_key(key_distancia_vaina_checkpoint,key_velocidad_actual,key_distancia_checkpoint1_checkpoint2,key_angulo_checkpoint1_vaina_checkpoint2,key_angulo_vaina_checkpoint_velocidadVaina);
//            int key = calc_key(tramoA,angulo_A,angulo_B);
            int thrust = optimiza_thrusts[key];
            if (thrust == 0) {
				thrust = 20;
			}
            System.err.println("key "+key+" : "+key_distancia_vaina_checkpoint+" "+key_angulo_checkpoint1_vaina_checkpoint2+" "+key_angulo_vaina_checkpoint_velocidadVaina);
            System.err.println("angulo_A "+angulo_checkpoint1_vaina_checkpoint2);
            System.err.println("angulo_B "+angulo_vaina_checkpoint_velocidadVaina);
            System.err.println("distancia1 "+distancia_vaina_checkpoint);
//            System.err.println("distancia2 "+distancia2);
            System.err.println("current_vel "+current_vel+" modulo "+current_vel.modulo());
            System.out.println(targ_point.x+ " " + targ_point.y + " " + thrust + " Agent DEFINITIVO"); // X Y THRUST MESSAGE
  
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
	    
	    double modulo() {
	        return Math.sqrt((this.x) * (this.x) + (this.y) * (this.y));
	    }
	    
	    @Override
	    public String toString() {
	      return String.format("[x: %d; y: %d]", this.x, this.y);
	    }
	    
	    public Point add(Point point) {
	    	return new Point(this.x+point.x, this.y+point.y);
	    }
	    
	}
	
	public static int calc_key(int tramo_A,int tramo_B,int tramo_C,int angulo_A, int angulo_B) {
//		Total 3*3*2*2*2 = 72
		return tramo_A+3*tramo_B+9*tramo_C+18*angulo_A+36*angulo_B;
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
		 double result = Math.atan2(tres.y - uno.y, tres.x - uno.x) -
				Math.atan2(dos.y - uno.y, dos.x - uno.x);
		 Double newAngle = Math.toDegrees(result); 
		 while (newAngle <= -180) newAngle += 360; 
		 while (newAngle> 180) newAngle -= 360; 
		 return newAngle; 
	    //transladamos al origen de coordenadas los tres puntos
//	    Point pi=new Point(dos.x-uno.x,dos.y-uno.y);
//	    Point pj=new Point(tres.x-uno.x,tres.y-uno.y);
//	    //calculamos su angulo de coordenada polar
//	    double ang_pi=Math.atan2((double)pi.x,(double)pi.y);
//	    double ang_pj=Math.atan2((double)pj.x,(double)pj.y);
//	
//	    //hallamos la diferencia
//	    double ang = ang_pj-ang_pi;
//	    return Math.toDegrees(ang);
	    //Si el angulo es negativo le sumamos 2PI para obtener el
	    //angulo en el intervalo [0-2PI]; 
	    //siempre obtenemos �ngulos positivos (en sentido antihorario)
	//	    if (ang<0.0)
	//	    	return ang+(2.0*Math.PI);
	//	    else
	//	    	return ang;
	}//fin angulo
}