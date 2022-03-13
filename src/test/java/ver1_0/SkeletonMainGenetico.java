package ver1_0;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.codingame.gameengine.runner.SoloGameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class SkeletonMainGenetico {
	

	public static String[] map_names = {"test0.json","test1.json","test2.json","test3.json"};

	public static int randInt(int min, int max) {
	    return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public static char[] getSliceOfArray(char[] arr, int start, int end){
		char[] slice = new char[end - start];
		
		for (int i = 0; i < slice.length; i++) {
		slice[i] = arr[start + i];
		}
		
		return slice;
	}
	
	public static class Individuo{
				
		public char[] cromosoma = new char[8*3+14*3];

		public int getNormal_trust() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,0,8)), 2) % 201;
		}
		public int getNear_target_thrust() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,9,16)), 2) % 201;
		}
		public int getPower_thrust() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,17,24)), 2) % 201;
		}
		public int getNear_distance() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,25,38)), 2) % 16001;
		}
		public int getMin_power_distance() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,39,52)), 2) % 16001;
		}
		public int getMax_power_distance() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,53,66)), 2) % 16001;
		}
		
		
		public HashMap<String, Double> evaluacion_mapa = new HashMap<String, Double>();
		public Double getEvaluacion() {
			Double sum = 0.0d;
			for (String name : map_names) {
				sum += evaluacion_mapa.get(name);
			}
			
			return sum/map_names.length;
		}
		
		public Individuo() {
			for (String name : map_names) {
				evaluacion_mapa.put(name, 1000.0d);
			}
		}

		public void randomize() {
			for (int i = 0; i < cromosoma.length; i++) {
				cromosoma[i] = Math.random() > 0.5 ? '0' : '1';
			}
		}
		
		public void mutacion(Double factor_mutacion) {
			for (int i = 0; i < cromosoma.length; i++) {
				if (Math.random() < factor_mutacion) {
					cromosoma[i] = cromosoma[i] == '0' ? '1' : '0';
				}
			}
		}
		
		public DataToRunGame getDataToRunGame() {
			DataToRunGame dataToRunGame = new DataToRunGame();
			dataToRunGame.normal_thrust = this.getNormal_trust();
			dataToRunGame.near_target_thrust = this.getNear_target_thrust();
			dataToRunGame.power_thrust = this.getPower_thrust();
			dataToRunGame.near_distance = this.getNear_distance();
			dataToRunGame.min_power_distance = this.getMin_power_distance();
			dataToRunGame.max_power_distance = this.getMax_power_distance();
			
			return dataToRunGame;
		}
		
		@Override
		public String toString() {
			String outString = "";
			outString += "normal_thrust: "+ String.valueOf(this.getNormal_trust())+" ;";
			outString += "near_target_thrust: "+ String.valueOf(this.getNear_target_thrust())+" ;";
			outString += "power_thrust: "+ String.valueOf(this.getPower_thrust())+" ;";
			outString += "near_distance: "+ String.valueOf(this.getNear_distance())+" ;";
			outString += "min_power_distance: "+ String.valueOf(this.getMin_power_distance())+" ;";
			outString += "max_power_distance: "+ String.valueOf(this.getMax_power_distance())+" ;";
			for (String name : map_names) {
				outString += name + " : "+ String.valueOf(this.evaluacion_mapa.get(name))+" ;";
				
			}
			return outString;
		}
	}
	
	public static class Poblacion{
		public Individuo[] individuos;
		public DataGenetico dataGenetico;

		public class DataGenetico {
			public int torneo_size;

			/***
			 * Siempre tiene que ser numero par. (divisible entre 2)
			 */
			public int poblacion_size;

			/***
			 * Porcentaje, 0.05 = 5%
			 */
			public Double factor_de_mutacion;
			public String map_name;

			public DataGenetico(int poblacion_size,int torneo_size, Double factor_de_mutacion) {
				this.poblacion_size = poblacion_size;
				this.torneo_size = torneo_size;
				this.factor_de_mutacion = factor_de_mutacion;
			}
		}
		
		public Poblacion(int poblacion_size,int torneo_size, Double factor_de_mutacion) {
			this.dataGenetico = new DataGenetico(poblacion_size, torneo_size, factor_de_mutacion);
			this.individuos = new Individuo[this.dataGenetico.poblacion_size];
			for (int i = 0; i < this.individuos.length; i++) {
				individuos[i] = new Individuo();
			}
		}
		public Poblacion(DataGenetico dataGenetico) {
			this.dataGenetico = dataGenetico;
			this.individuos = new Individuo[this.dataGenetico.poblacion_size];
			for (int i = 0; i < this.individuos.length; i++) {
				individuos[i] = new Individuo();
			}
		}
		
		public void randomize() {
			for (Individuo individuo : individuos) {
				individuo.randomize();
			}
		}
		
		public void mutacion() {
			for (int i = 0; i < this.individuos.length; i++) {
				individuos[i].mutacion(this.dataGenetico.factor_de_mutacion);
			}
		}
		
		public Poblacion torneo() {
			Poblacion new_poblacion = new Poblacion(this.dataGenetico);
			for (int i = 0; i < this.individuos.length; i++) {
				Individuo seleccionado = null;
				for (int j = 0; j < this.dataGenetico.torneo_size; j++) {
					Individuo aux_individuo = this.individuos[(int)(Math.random()*this.individuos.length)];
					if (seleccionado == null || aux_individuo.getEvaluacion() > seleccionado.getEvaluacion()) {
						seleccionado = aux_individuo;
					} 
				}
				new_poblacion.individuos[i] = seleccionado;
			}
			return new_poblacion;
		}

		/***
		 * @return Dos nuevos individuos
		 */
		public Individuo[] cruce_uniforme(Individuo padre, Individuo madre) {
			Individuo[] output = new Individuo[2];
			Individuo individuo1 = new Individuo();
			Individuo individuo2 = new Individuo();
			
			for (int i = 0; i < individuo1.cromosoma.length; i++) {
				if (Math.random() > 0.5) {
					individuo1.cromosoma[i] = padre.cromosoma[i];
					individuo2.cromosoma[i] = madre.cromosoma[i];
				}else {
					individuo1.cromosoma[i] = madre.cromosoma[i];
					individuo2.cromosoma[i] = padre.cromosoma[i];
				}
			}
			output[0] = individuo1;
			output[1] = individuo2;
			
			return output;
		}
		
		public Poblacion next_poblacion() {
			Poblacion pre_poblacion = this.torneo();
			Poblacion next_poblacion = new Poblacion(this.dataGenetico);
			
			for (int i = 0; i < pre_poblacion.individuos.length; i+=2) {
				Individuo[] aux_listIndividuos = cruce_uniforme(pre_poblacion.individuos[i], pre_poblacion.individuos[i+1]);
				next_poblacion.individuos[i] = aux_listIndividuos[0];
				next_poblacion.individuos[i+1] = aux_listIndividuos[1];
				
			}
			next_poblacion.mutacion();
			
			return next_poblacion;
		}
		
		public void evaluar() {
			
			HashMap<String, Future<Double>> hashMap = new HashMap<String, Future<Double>>();

			for (int i = 0; i < this.individuos.length; i++) {
				for (String map_name : map_names) {
					String key = map_name + (new String(this.individuos[i].cromosoma));
					Future<Double> future = executor.submit(new WorkerThread(this.individuos[i].getDataToRunGame(),map_name));
					hashMap.put(key, future);
				}
			}
			
			for (int i = 0; i < this.individuos.length; i++) {
				for (String map_name : map_names) {
					try {
						String key = map_name + (new String(this.individuos[i].cromosoma));
						this.individuos[i].evaluacion_mapa.put(map_name, hashMap.get(key).get());
//						debug_write(String.valueOf(i)+" evaluacion "+String.valueOf(this.individuos[i].getEvaluacion())+" cromosoma "+new String(this.individuos[i].cromosoma));
						debug_write(String.valueOf(i)+map_name+" evaluacion "+String.valueOf(this.individuos[i].evaluacion_mapa.get(map_name))+this.individuos[i].toString()+" cromosoma "+new String(this.individuos[i].cromosoma));
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		public Individuo getBest() {
			Individuo output = this.individuos[0];
			for (Individuo individuo : this.individuos) {
				if (individuo.getEvaluacion() < output.getEvaluacion()) {
					output = individuo;
				}
			}
			return output;
		}

		public String get_executable() {
			Individuo best = getBest();

	        String out = String.valueOf(best.getNormal_trust())+" "+
	        		String.valueOf(best.getNear_target_thrust())+" "+
	        		String.valueOf(best.getPower_thrust())+" "+
	        		String.valueOf(best.getNear_distance())+" "+
	        		String.valueOf(best.getMin_power_distance())+" "+
	        		String.valueOf(best.getMax_power_distance());
	        		
			return out;
		}
	}
	
	public static void ejecucion_genetico() {
		//La poblacion tiene que ser par para el cruce
		int poblation_size = 26;
		int torneo_size = 5;
		Double factor_mutacion = 0.1d;
		int bucle_generaciones = 100;
		 		
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		Poblacion poblacion = new Poblacion(poblation_size,torneo_size,factor_mutacion);
		poblacion.randomize();
		poblacion.evaluar();
		
		Individuo best_individuo = poblacion.getBest();
		

		clear_file();
		clear_file_debug();
		write_file(poblacion, best_individuo, 0);
		
		for (int i = 1; i < bucle_generaciones; i++) {
			
			poblacion = poblacion.next_poblacion();
			poblacion.evaluar();

			Individuo actual_best_individuo = poblacion.getBest();
			
			if (best_individuo.getEvaluacion() > actual_best_individuo.getEvaluacion()) {
				best_individuo = actual_best_individuo;
			}

			write_file(poblacion, best_individuo, i);
			
		}

		result_write(String.valueOf(best_individuo.getEvaluacion())+";"+poblacion.get_executable());
		
		executor.shutdownNow();
	}
	
	
    public static void main(String[] args) {
    	
    	ejecucion_genetico();
    	System.err.println("Termino");
    }
    
    
    public static class DataToRunGame{
    	//	Para thrust como es de 0 a 200 se usan 8 bits
		public int normal_thrust = 0;
		public int near_target_thrust = 0;
		public int power_thrust = 0;
		//	Para distance como es de 0 a 16000 se usan 14 bits
		public int near_distance = 0;
		public int min_power_distance = 0;
		public int max_power_distance = 0;
		
		public String map_name = "test0.json";
		
		public DataToRunGame() {
		}
		
		public DataToRunGame(int normal_thrust, int near_target_thrust, int power_thrust,
				int near_distance, int min_power_distance, int max_power_distance) {
			this.normal_thrust = normal_thrust;
			this.near_target_thrust = near_target_thrust;
			this.power_thrust = power_thrust;
			this.near_distance = near_distance;
			this.min_power_distance = min_power_distance;
			this.max_power_distance = max_power_distance;
		}
    }

    public static ExecutorService executor;
    
    public static class WorkerThread implements Callable<Double>{

    	private DataToRunGame data;
    	private String map_name;
    	public WorkerThread(DataToRunGame data, String map_name){  
            this.data=data;  
            this.map_name=map_name;  
        }  
    	
    	@Override
    	public Double call() {
    		return evaluate();
    	}
    	
	    /***
	     * Evalua un individuo
	     * @param data del individuo
	     * @return evaluacion del individuo
	     */
	    public Double evaluate() {
	    	SoloGameRunner gameRunner = new SoloGameRunner();
	    	
	        String separator = System.getProperty("file.separator");
	        
	        gameRunner.setAgent("java -cp \""+System.getProperty("user.dir")+separator+
	        		"target"+separator+"test-classes\" ver1_0.Agent "+
	        		String.valueOf(data.normal_thrust)+" "+
	        		String.valueOf(data.near_target_thrust)+" "+
	        		String.valueOf(data.power_thrust)+" "+
	        		String.valueOf(data.near_distance)+" "+
	        		String.valueOf(data.min_power_distance)+" "+
	        		String.valueOf(data.max_power_distance));
	        
	        //  Sets a test case
	        gameRunner.setTestCase(this.map_name);
	        
	        try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	        GameResult gameResult = gameRunner.simulate();
	        Double valorfinal = Double.parseDouble(gameResult.metadata.split("\"")[3]);
	        if (valorfinal < 5.0d) {
				return 1000.0d;
			}
	        return valorfinal;
		}
	}
    
    public static String getPathToResources() {
        String separator = System.getProperty("file.separator");
    	return System.getProperty("user.dir")+separator+"src"+separator+"main"+separator+"resources"+separator;
    }
    
    
    public static void clear_file() {
    	try {
            File archivo = new File(getPathToResources()+"output_genetico.txt");
            FileWriter escribir = new FileWriter(archivo);
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al limpiar");
        }
	}
    public static void clear_file_debug() {
    	try {
            File archivo = new File(getPathToResources()+"debug_genetico.txt");
            FileWriter escribir = new FileWriter(archivo);
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al limpiar");
        }
	}
    
    		
    public static void write_file(Poblacion poblacion, Individuo best_individuo,int generacion) {
    	try {
            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(getPathToResources()+"output_genetico.txt");
            
            //Crear objeto FileWriter que sera el que nos ayude a escribir sobre archivo
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write("gen: "+String.valueOf(generacion)+"; ");
            escribir.write("best: "+String.valueOf(best_individuo.getEvaluacion())+"; ");
            escribir.write("best_data: "+best_individuo.toString()+"; ");
            escribir.write("poblacion_size: "+String.valueOf(poblacion.dataGenetico.poblacion_size)+"; ");
            escribir.write("torneo_size: "+String.valueOf(poblacion.dataGenetico.torneo_size)+"; ");
            escribir.write("factor_de_mutacion: "+String.valueOf(poblacion.dataGenetico.factor_de_mutacion)+"; ");
            escribir.write("cromosoma: "+new String(best_individuo.cromosoma)+"\n");

            //Cerramos la conexion
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}
    
    public static void debug_write(String message) {
    	try {
            //Crear un objeto File se encarga de crear o abrir acceso a un archivo que se especifica en su constructor
            File archivo = new File(getPathToResources()+"debug_genetico.txt");
            
            //Crear objeto FileWriter que sera el que nos ayude a escribir sobre archivo
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write(message+"\n");

            //Cerramos la conexion
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}

    public static void result_write(String message) {
    	copyFile(getPathToResources()+"output_genetico.txt", getPathToResources()+"datos_genetico"+String.valueOf(randInt(1000, 9999))+".csv");
    	try {
            File archivo = new File(getPathToResources()+"result_genetico.txt");
            
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write(message.replace(",", ".")+"\n");
            
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}
    
    public static boolean copyFile(String fromFile, String toFile) {
        File origin = new File(fromFile);
        File destination = new File(toFile);
        if (origin.exists()) {
            try {
                InputStream in = new FileInputStream(origin);
                OutputStream out = new FileOutputStream(destination);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }
}
