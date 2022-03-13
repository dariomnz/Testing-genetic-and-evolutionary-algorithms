package ver2_0;
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
				
		public char[] cromosoma = new char[8*3+14*2];

		public int getthrust_1() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,0,8)), 2) % 201;
		}
		public int getthrust_2() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,9,16)), 2) % 201;
		}
		public int getthrust_3() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,17,24)), 2) % 201;
		}
		public int getdistance_1() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,25,38)), 2) % 16001;
		}
		public int getdistance_2() {
			return Integer.parseInt(new String(getSliceOfArray(cromosoma,39,52)), 2) % 16001;
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
			dataToRunGame.thrust_1 = this.getthrust_1();
			dataToRunGame.thrust_2 = this.getthrust_2();
			dataToRunGame.thrust_3 = this.getthrust_3();
			dataToRunGame.distance_1 = this.getdistance_1();
			dataToRunGame.distance_2 = this.getdistance_2();
			
			return dataToRunGame;
		}
		@Override
		public String toString() {
			String outString = ";data;";
			outString += String.valueOf(this.getthrust_1())+" ;";
			outString += String.valueOf(this.getthrust_2())+" ;";
			outString += String.valueOf(this.getthrust_3())+" ;";
			outString += String.valueOf(this.getdistance_1())+" ;";
			outString += String.valueOf(this.getdistance_2())+" ;";
			outString += ";eval;";
			for (String map_name : map_names) {
				outString += String.format ("%.5f", this.evaluacion_mapa.get(map_name))+";";
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

			String out = String.valueOf(this.getBest().getthrust_1())+" "+
	        		String.valueOf(this.getBest().getthrust_2())+" "+
	        		String.valueOf(this.getBest().getthrust_3())+" "+
	        		String.valueOf(this.getBest().getdistance_1())+" "+
	        		String.valueOf(this.getBest().getdistance_2());
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
		int gen_without_best = 0;
		
		for (int i = 1; i < bucle_generaciones; i++) {
			
			poblacion = poblacion.next_poblacion();
			poblacion.evaluar();

			Individuo actual_best_individuo = poblacion.getBest();
			
			if (best_individuo.getEvaluacion() > actual_best_individuo.getEvaluacion()) {
				best_individuo = actual_best_individuo;
				gen_without_best = 0;
			}
			gen_without_best ++;
			
			write_file(poblacion, best_individuo, i);
			
			if (gen_without_best >= 20) {
				break;
			}
		}
		
		result_write(String.format ("%.5f", best_individuo.getEvaluacion())+";"+poblacion.get_executable());
				
		executor.shutdownNow();
		
	}
	
	
    public static void main(String[] args) {
    	
    	ejecucion_genetico();
    	System.err.println("Termino");
    }
    
    
    public static class DataToRunGame{
    	//	Para thrust como es de 0 a 200 se usan 8 bits
		public int thrust_1 = 0;
		public int thrust_2 = 0;
		public int thrust_3 = 0;
		//	Para distance como es de 0 a 16000 se usan 14 bits
		public int distance_1 = 0;
		public int distance_2 = 0;
		
		public DataToRunGame() {
		}

		public DataToRunGame(int _1, int _2, int _3,int _4, int _5) {
			this.thrust_1 = _1;
			this.thrust_2 = _2;
			this.thrust_3 = _3;
			this.distance_1 = _4;
			this.distance_2 = _5;
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
	        
	        String command = "java -cp \""+System.getProperty("user.dir")+separator+
	        		"target"+separator+"test-classes\""+" ver2_0.AgentPresentado "+
	        		String.valueOf(data.thrust_1)+" "+
	        		String.valueOf(data.thrust_2)+" "+
	        		String.valueOf(data.thrust_3)+" "+
	        		String.valueOf(data.distance_1)+" "+
	        		String.valueOf(data.distance_2);
	        gameRunner.setAgent(command);
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
            File archivo = new File(getPathToResources()+"output_genetico_v2_0.txt");
            FileWriter escribir = new FileWriter(archivo);
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al limpiar");
        }
	}
    public static void clear_file_debug() {
    	try {
            File archivo = new File(getPathToResources()+"debug_genetico_v2_0.txt");
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
            File archivo = new File(getPathToResources()+"output_genetico_v2_0.txt");
            
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
            File archivo = new File(getPathToResources()+"debug_genetico_v2_0.txt");
            
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
    	copyFile(getPathToResources()+"output_genetico_v2_0.txt", getPathToResources()+"datos_genetico"+String.valueOf(randInt(1000, 9999))+".csv");
    	try {
            File archivo = new File(getPathToResources()+"result_genetico_v2_0.txt");
            
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
