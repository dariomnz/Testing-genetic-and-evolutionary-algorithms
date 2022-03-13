package tools;


import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

public class csv_tool {

	public static void main(String[] args) {

		tool("datos_salida", "datos_final.csv");
		tool("datos_genetico", "datos_final_genetico.csv");
		
		
	}
	public static void tool(String files_name, String result_name) {
		
//		Recogemos todos los archivos .csv
		ArrayList<String> file_names = new ArrayList<String>();

		File[] files = new File(getPathToResources()).listFiles();

		for (File file : files) {
		    if (file.isFile() && file.getName().contains(files_name)) {
		    	file_names.add(file.getName());
		    }
		}
		
		
		
//		Recogemos todos datos de los ficheros
		ArrayList<ArrayList<String>> data = new ArrayList<ArrayList<String>>();
		for (String file_name : file_names) {
			ArrayList<String> aux_data = new ArrayList<String>();
			try {
	    		File file = new File(getPathToResources()+file_name);
	    	    Scanner sc = new Scanner(file);

	    	    while (sc.hasNextLine()) {
	    	    	String[] next_line = sc.nextLine().split(";");
	    	    	aux_data.add(next_line[1]);
	    	    }
	           
	            sc.close();
	        }
	        catch (Exception e) {
	            System.out.println("Error al leer");
	        }

			data.add(aux_data);
		}
		
//		Escribimos todos los datos en el fichero destino
		try {
            File archivo = new File(getPathToResources()+result_name);
            
            FileWriter escribir = new FileWriter(archivo);
            
            for (int i = 0; i < 100; i++) {
            	for (ArrayList<String> arrayList : data) {
            		try {
                        escribir.write(arrayList.get(i)+";");
					} catch (Exception e) {
                        escribir.write(";");
					}
    			}
                escribir.write("\n");
			}

            escribir.close();
        }
        catch (Exception e) {
            System.out.println("Error al escribir");
        }
		
    	
	}

    public static String getPathToResources() {
        String separator = System.getProperty("file.separator");
    	return System.getProperty("user.dir")+separator+"src"+separator+"main"+separator+"resources"+separator;
    }
    
}
