package ver2_0;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.codingame.gameengine.runner.SoloGameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class SkeletonMainEvolutivo {
	
	public static String[] map_names = {"test0.json","test1.json","test2.json","test3.json"};
	
	public static int randInt(int min, int max) {
	    return min + (int)(Math.random() * ((max - min) + 1));
	}
	
	public static double getGaussian(double mean, double variance){
	      return mean + new Random().nextGaussian() * variance;
	}
	
	public static char[] getSliceOfArray(char[] arr, int start, int end){
		char[] slice = new char[end - start];
		
		for (int i = 0; i < slice.length; i++) {
		slice[i] = arr[start + i];
		}
		
		return slice;
	}
	
	public static class Individuo{
		
		public int thrust_1 = 0;
		public int thrust_2 = 0;
		public int thrust_3 = 0;
		public int distance_1 = 0;
		public int distance_2 = 0;
		
		private Double[] varianzas;
		
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
			varianzas = new Double[5];

			for (int i = 0; i < 3; i++) {
				varianzas[i] = Double.valueOf(randInt(10,100));
			}
			for (int i = 3; i < varianzas.length; i++) {
				varianzas[i] = Double.valueOf(randInt(1000,10000));
			}
			
			thrust_1 = (int)(getGaussian(100, 50));
			thrust_2 = (int)(getGaussian(100, 50));
			thrust_3 = (int)(getGaussian(100, 50));
			distance_1 = (int)(getGaussian(8000, 3000));
			distance_2 = (int)(getGaussian(8000, 3000));
		}
		
		public DataToRunGame getDataToRunGame() {
			DataToRunGame dataToRunGame = new DataToRunGame();
			dataToRunGame.thrust_1 = this.thrust_1;
			dataToRunGame.thrust_2 = this.thrust_2;
			dataToRunGame.thrust_3 = this.thrust_3;
			dataToRunGame.distance_1 = this.distance_1;
			dataToRunGame.distance_2 = this.distance_2;
			
			return dataToRunGame;
		}
		
		@Override
		public String toString() {
			String outString = ";data;";
			outString += String.valueOf(this.thrust_1)+" ;";
			outString += String.valueOf(this.thrust_2)+" ;";
			outString += String.valueOf(this.thrust_3)+" ;";
			outString += String.valueOf(this.distance_1)+" ;";
			outString += String.valueOf(this.distance_2);
			outString += ";var;";
			for (double varianza : this.varianzas) {
				outString += String.format ("%.5f", varianza)+"; ";
			}
			outString += ";eval;";
			for (String map_name : map_names) {
				outString += String.format ("%.5f", this.evaluacion_mapa.get(map_name))+";";
			}
			
			return outString;
		}
		
		public String getIDString() {
			String outString = "datos={";
			outString += String.valueOf(this.thrust_1)+" ;";
			outString += String.valueOf(this.thrust_2)+" ;";
			outString += String.valueOf(this.thrust_3)+" ;";
			outString += String.valueOf(this.distance_1)+" ;";
			outString += String.valueOf(this.distance_2)+"}";
			outString += "varianzas={";
			for (double varianza : this.varianzas) {
				outString += String.format ("%.5f", varianza)+", ";
			}
			outString += "}";
			
			return outString;
		}
		
	}
	
	public static class Poblacion{
		public Individuo padre;
		public Individuo hijo;
		
		public ArrayList<Boolean> exitos = new ArrayList<Boolean>();
		
		public DataGenetico dataGenetico;

		public class DataGenetico {
			public int frecuencia_1_5 = 10;
			public Double c = 0.82d;
			public DataGenetico() {
			}
		}
		
		public Poblacion(int poblacion_size) {
			this.dataGenetico = new DataGenetico();
			this.padre = new Individuo();
			this.hijo = new Individuo();
		}
		public Poblacion(DataGenetico dataGenetico) {
			this.dataGenetico = dataGenetico;
			this.padre = new Individuo();
			this.hijo = new Individuo();
		}


		public Individuo cruce(Individuo padre) {
			Individuo hijo = new Individuo();
			
			hijo.thrust_1 = Math.floorMod(((int)getGaussian(0, padre.varianzas[0])+padre.thrust_1), 201);
			hijo.thrust_2 = Math.floorMod(((int)getGaussian(0, padre.varianzas[1])+padre.thrust_2) , 201);
			hijo.thrust_3 = Math.floorMod(((int)getGaussian(0, padre.varianzas[2])+padre.thrust_3) , 201);
			hijo.distance_1 = Math.floorMod(((int)getGaussian(0, padre.varianzas[3])+padre.distance_1) , 16001);
			hijo.distance_2 = Math.floorMod(((int)getGaussian(0, padre.varianzas[4])+padre.distance_2) , 16001);

			hijo.varianzas = padre.varianzas.clone();
			
			return hijo;
		}
		
		public void seleccion() {

			if (this.hijo.getEvaluacion() < this.padre.getEvaluacion()) {
				this.padre = this.hijo;
				this.hijo = new Individuo();
				this.exitos.add(this.exitos.size(), true);
			}else {
				this.exitos.add(this.exitos.size(), false);
			}

			if (this.exitos.size() > this.dataGenetico.frecuencia_1_5) {
				this.exitos.remove(0);
			}
		}
		
		public void mutacion(int num_generacion) {
			double psi = Collections.frequency(this.exitos, true) / this.dataGenetico.frecuencia_1_5;
			if (psi < (1/5.0d)) {
				for (int i = 0; i < this.padre.varianzas.length; i++) {
					this.padre.varianzas[i] *= this.dataGenetico.c;
				}
			}else if (psi > (1/5.0d)) {
				for (int i = 0; i < this.padre.varianzas.length; i++) {
					this.padre.varianzas[i] /= this.dataGenetico.c;
				}
				
			}
		}
		
		public void update_poblacion(int num_generacion) {			
			this.hijo = this.cruce(this.padre);
			this.evaluar(false);

			this.seleccion();
			this.mutacion(num_generacion);
		}
		
		public void evaluar(Boolean evalue_padre) {
			HashMap<String, Future<Double>> hashMap = new HashMap<String, Future<Double>>();
			for (String map_name : map_names) {
				if (evalue_padre) {
					String key = map_name + this.padre.getIDString();
					Future<Double> future = executor.submit(new WorkerThread(this.padre.getDataToRunGame(),map_name));
					hashMap.put(key, future);
				}
				
				String key2 = map_name + this.hijo.getIDString();
				Future<Double> future2 = executor.submit(new WorkerThread(this.hijo.getDataToRunGame(),map_name));
				hashMap.put(key2, future2);
			}
			
			for (String map_name : map_names) {

				if (evalue_padre) {
					try {
						String key = map_name + this.padre.getIDString();
						this.padre.evaluacion_mapa.put(map_name, hashMap.get(key).get());
						debug_write(map_name+" padre evaluacion "+String.valueOf(this.padre.evaluacion_mapa.get(map_name))+this.padre.toString());
					} catch (InterruptedException | ExecutionException e) {
						e.printStackTrace();
					}
					
				}
				try {
					String key = map_name + this.hijo.getIDString();
					this.hijo.evaluacion_mapa.put(map_name, hashMap.get(key).get());
					debug_write(map_name+" hijo evaluacion "+String.valueOf(this.hijo.evaluacion_mapa.get(map_name))+this.hijo.toString());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		}
		public String get_executable() {

			String out = String.valueOf(this.padre.thrust_1)+" "+
	        		String.valueOf(this.padre.thrust_2)+" "+
	        		String.valueOf(this.padre.thrust_3)+" "+
	        		String.valueOf(this.padre.distance_1)+" "+
	        		String.valueOf(this.padre.distance_2);
			return out;
		}
	}
	
	public static void ejecucion_evolutivo() {

		clear_file(new String("output_v2_0.txt"));
		clear_file(new String("debug_v2_0.txt"));
		
		int poblation_size = 2;
		int bucle_generaciones = 100;
		
		executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
		
		Poblacion poblacion = new Poblacion(poblation_size);
		
		poblacion.evaluar(true);

		write_file(poblacion, poblacion.padre, 0);
		
		for (int i = 1; i < bucle_generaciones; i++) {
			poblacion.update_poblacion(i);

			write_file(poblacion, poblacion.padre, i);
			
			Boolean is_exit = true;
			for (Double var : poblacion.padre.varianzas) {
				if (var > 1.0d) {
					is_exit = false;
				}
			}
			if (is_exit) {
				break;
			}
		}
		result_write(String.format ("%.5f", poblacion.padre.getEvaluacion())+";"+poblacion.get_executable());
		
		executor.shutdownNow();
		
	}
	
	
    public static void main(String[] args) {
    	ejecucion_evolutivo();
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
	        debug_write(command);
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
    
    
    public static void clear_file(String file_name) {
    	try {
            File archivo = new File(getPathToResources()+file_name);
            FileWriter escribir = new FileWriter(archivo);
            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al limpiar");
        }
	}
    		
    public static void write_file(Poblacion poblacion, Individuo best_individuo,int generacion) {
    	try {
            File archivo = new File(getPathToResources()+"output_v2_0.txt");
            
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write(String.valueOf(generacion)+"; ");
            escribir.write(String.format ("%.5f", best_individuo.getEvaluacion()));
            escribir.write(best_individuo.toString()+"\n");

            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}
    
    public static void debug_write(Object message) {
    	debug_write(String.valueOf(message));
    }
    
    public static void debug_write(String message) {
    	try {
            File archivo = new File(getPathToResources()+"debug_v2_0.txt");
            
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write(message+"\n");

            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}
    public static void result_write(String message) {
    	copyFile(getPathToResources()+"output_v2_0.txt", getPathToResources()+"datos_salida"+String.valueOf(randInt(1000, 9999))+".csv");
    	try {
            File archivo = new File(getPathToResources()+"result_v2_0.txt");
            
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
