package ver3_0;
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
import java.util.concurrent.TimeUnit;

import com.codingame.gameengine.runner.SoloGameRunner;
import com.codingame.gameengine.runner.dto.GameResult;

public class SkeletonMainEvolutivo {

//	public static String[] map_names = {"test0.json","test1.json","test2.json","test3.json","test5.json","test6.json","test7.json","test8.json","test9.json"};
	public static String[] map_names = {"test3.json","test5.json","test6.json","test7.json","test8.json","test9.json"};
//	public static String[] map_names = {"test9.json"};
	
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
		
		int total_num_datos = 1+1+2+2+1+72;

    	int[] angulosA = new int[1];
    	int[] angulosB = new int[1];
    	int[] distanciasA = new int[2];
    	int[] velB = new int[2];
    	int[] distanciasC = new int[1];
    	int[] thrusts = new int[72];
		
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
			varianzas = new Double[total_num_datos];
			
			
			for (int i = 0; i < 4; i++) {
				varianzas[i] = Double.valueOf(randInt(10,100));
			}
			for (int i = 4; i < 9; i++) {
				varianzas[i] = Double.valueOf(randInt(1000,10000));
			}
			for (int i = 9; i < varianzas.length; i++) {
				varianzas[i] = Double.valueOf(randInt(10,100));
			}

			for (int i = 0; i < angulosA.length; i++) {
				angulosA[i] = (int)(getGaussian(75, 24));
			}
			for (int i = 0; i < angulosB.length; i++) {
				angulosB[i] = (int)(getGaussian(75, 24));
			}

			for (int i = 0; i < distanciasA.length; i++) {
				distanciasA[i] = (int)(getGaussian(8000, 3000));
			}
			for (int i = 0; i < velB.length; i++) {
				velB[i] = (int)(getGaussian(300, 100));
			}
			for (int i = 0; i < distanciasC.length; i++) {
				distanciasC[i] = (int)(getGaussian(8000, 3000));
			}
			
			for (int i = 0; i < thrusts.length; i++) {
				thrusts[i] = (int)(getGaussian(100, 50));
			}
		}
		
		public DataToRunGame getDataToRunGame() {
			DataToRunGame dataToRunGame = new DataToRunGame();
			
			dataToRunGame.angulosA = this.angulosA.clone();
			dataToRunGame.angulosB = this.angulosB.clone();
			dataToRunGame.distanciasA = this.distanciasA.clone();
			dataToRunGame.velB = this.velB.clone();
			dataToRunGame.distanciasC = this.distanciasC.clone();
			dataToRunGame.thrusts = this.thrusts.clone();
			
			return dataToRunGame;
		}
		
		@Override
		public String toString() {
			String outString = ";eval;";
			
			for (String map_name : map_names) {
				outString += String.format ("%.5f", this.evaluacion_mapa.get(map_name))+";";
			}
			
			outString += ";data;";
			for (int i = 0; i < angulosA.length; i++) {
				outString += String.valueOf(this.angulosA[i])+" ;";
			}
			for (int i = 0; i < angulosB.length; i++) {
				outString += String.valueOf(this.angulosB[i])+" ;";
			}
			for (int i = 0; i < distanciasA.length; i++) {
				outString += String.valueOf(this.distanciasA[i])+" ;";
			}
			for (int i = 0; i < velB.length; i++) {
				outString += String.valueOf(this.velB[i])+" ;";
			}
			for (int i = 0; i < distanciasC.length; i++) {
				outString += String.valueOf(this.distanciasC[i])+" ;";
			}
			for (int i = 0; i < thrusts.length; i++) {
				outString += String.valueOf(this.thrusts[i])+" ;";
			}
			
			outString += "var;";
			for (double varianza : this.varianzas) {
				outString += String.format ("%.5f", varianza)+"; ";
			}
			
			return outString;
		}
		
		public String getIDString() {
			String outString = "datos={";
			for (int i = 0; i < angulosA.length; i++) {
				outString += String.valueOf(this.angulosA[i])+" ;";
			}
			for (int i = 0; i < angulosB.length; i++) {
				outString += String.valueOf(this.angulosB[i])+" ;";
			}
			for (int i = 0; i < distanciasA.length; i++) {
				outString += String.valueOf(this.distanciasA[i])+" ;";
			}
			for (int i = 0; i < velB.length; i++) {
				outString += String.valueOf(this.velB[i])+" ;";
			}
			for (int i = 0; i < distanciasC.length; i++) {
				outString += String.valueOf(this.distanciasC[i])+" ;";
			}
			for (int i = 0; i < thrusts.length; i++) {
				outString += String.valueOf(this.thrusts[i])+" ;";
			}
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
			public int frecuencia_1_5 = 15;
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
			int vari_count = 0;
			for (int i = 0; i < padre.angulosA.length; i++) {
				hijo.angulosA[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.angulosA[i]), 181);
				vari_count++;
			}
			for (int i = 0; i < padre.angulosB.length; i++) {
				hijo.angulosB[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.angulosB[i]), 181);
				vari_count++;
			}
			for (int i = 0; i < padre.distanciasA.length; i++) {
				hijo.distanciasA[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.distanciasA[i]), 16001);
				vari_count++;
			}
			for (int i = 0; i < padre.velB.length; i++) {
				hijo.velB[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.velB[i]), 601);
				vari_count++;
			}
			for (int i = 0; i < padre.distanciasC.length; i++) {
				hijo.distanciasC[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.distanciasC[i]), 16001);
				vari_count++;
			}
			for (int i = 0; i < padre.thrusts.length; i++) {
				hijo.thrusts[i] = Math.floorMod(((int)getGaussian(0, padre.varianzas[vari_count])+padre.thrusts[i]), 201);
				vari_count++;
			}

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
			debug_write("");
		}
		public String get_executable() {

			String out = "";

			for (int i = 0; i < this.padre.angulosA.length; i++) {
				out += String.valueOf(this.padre.angulosA[i])+" ";
			}
			for (int i = 0; i < this.padre.angulosB.length; i++) {
				out += String.valueOf(this.padre.angulosB[i])+" ";
			}
			for (int i = 0; i < this.padre.distanciasA.length; i++) {
				out += String.valueOf(this.padre.distanciasA[i])+" ";
			}
			for (int i = 0; i < this.padre.velB.length; i++) {
				out += String.valueOf(this.padre.velB[i])+" ";
			}
			for (int i = 0; i < this.padre.distanciasC.length; i++) {
				out += String.valueOf(this.padre.distanciasC[i])+" ";
			}
			for (int i = 0; i < this.padre.thrusts.length; i++) {
				out += String.valueOf(this.padre.thrusts[i])+" ";
			}
			return out;
		}
	}
	
	public static void ejecucion_evolutivo() {

		clear_file(new String("output_v3_0.txt"));
		clear_file(new String("debug_v3_0.txt"));
		
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
    	for (int i = 0; i < 100; i++) {
        	ejecucion_evolutivo();
		}
    }
    
    
    public static class DataToRunGame{

    	int[] angulosA = new int[1];
    	int[] angulosB = new int[1];
    	int[] distanciasA = new int[2];
    	int[] velB = new int[2];
    	int[] distanciasC = new int[1];
    	int[] thrusts = new int[72];
		
		public DataToRunGame() {
		}
		
		public DataToRunGame(int[] angulosA, int[] angulosB, int[] distanciasA,int[] distanciasB,int[] distanciasC, int[] thrusts) {
			this.angulosA = angulosA;
			this.angulosB = angulosB;
			this.distanciasA = distanciasA;
			this.velB = distanciasB;
			this.distanciasC = distanciasC;
			this.thrusts = thrusts;
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
	        		"target"+separator+"test-classes\""+" ver3_0.AgentDefinitivo ";
    		for (int i = 0; i < data.angulosA.length; i++) {
    			command += String.valueOf(data.angulosA[i])+" ";
			}
			for (int i = 0; i < data.angulosB.length; i++) {
				command += String.valueOf(data.angulosB[i])+" ";
			}
			for (int i = 0; i < data.distanciasA.length; i++) {
				command += String.valueOf(data.distanciasA[i])+" ";
			}
			for (int i = 0; i < data.velB.length; i++) {
				command += String.valueOf(data.velB[i])+" ";
			}
			for (int i = 0; i < data.distanciasC.length; i++) {
				command += String.valueOf(data.distanciasC[i])+" ";
			}
			for (int i = 0; i < data.thrusts.length; i++) {
				command += String.valueOf(data.thrusts[i])+" ";
			}
	        gameRunner.setAgent(command);
//	        debug_write(command);
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
            File archivo = new File(getPathToResources()+"output_v3_0.txt");
            
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
            File archivo = new File(getPathToResources()+"debug_v3_0.txt");
            
            FileWriter escribir = new FileWriter(archivo, true);

            escribir.write(message+"\n");

            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
	}
    public static void result_write(String message) {
    	copyFile(getPathToResources()+"output_v3_0.txt", getPathToResources()+"datos_salida"+String.valueOf(randInt(1000, 9999))+".csv");
    	try {
            File archivo = new File(getPathToResources()+"result_v3_0.txt");
            
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
