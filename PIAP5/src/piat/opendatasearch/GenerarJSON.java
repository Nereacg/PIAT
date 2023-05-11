package piat.opendatasearch;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import com.google.gson.stream.JsonWriter;
import piat.opendatasearch.XPATH_Evaluador.Propiedad;

/**
 * Clase usada para crear un fichero JSON
 * Contiene un método estático, llamado generar() que se encarga de generar un String en formato JSON a partir de la lista de propiedades que se le pasa como parámetro 
 */
public class GenerarJSON {

	/**
	 * Método que se encarga de generar un String en formato JSON a partir de la lista de propiedades que se le pasa como parámetro
	 * @param	ficheroJSONSalida	Nombre del fichero JSON de salida
	 * @param	listaPropiedades	Lista de propiedades
	 */
	public static void generar(String ficheroJSONSalida,List<Propiedad> listaPropiedades) {
		// TODO:
		// - Crear objeto de la clase writer usando como fichero el parámetro ficheroJSONSalida
		// - Crear los arrays y objetos que debe tener el fichero JSON de salida
		try{
			int posicion;
			JsonWriter writer = new JsonWriter(new FileWriter(ficheroJSONSalida));
			writer.setIndent("\t");	// indentación
			writer.beginObject();
			if ((posicion=buscarPropiedad("query", listaPropiedades))>=0)
				writer.name(listaPropiedades.get(posicion).nombre).value(listaPropiedades.get(posicion).valor); // ---->  "query": "018",
			if ((posicion=buscarPropiedad("numeroResources", listaPropiedades))>=0)
				writer.name(listaPropiedades.get(posicion).nombre).value(Integer.parseInt(listaPropiedades.get(posicion).valor)); // ---->  "numeroResources": "8",
			if ((posicion=buscarPropiedad("ubicacion", listaPropiedades))>=0){ // ----> "ubicaciones": ["tercero", "segundo", "cuarto"            ]
				writer.name("ubicaciones");                    // ----> "ubicaciones":    
				writer.beginArray();                                // ----> [
				writer.value(listaPropiedades.get(posicion).valor); // ----> "tercero",
				//writer.beginObject();
				//writer.name(listaPropiedades.get(posicion).nombre).value(listaPropiedades.get(posicion).valor);
				//writer.endObject();
				posicion++;              
				while (posicion<listaPropiedades.size() && listaPropiedades.get(posicion).nombre.equals("ubicacion")){
					writer.value(listaPropiedades.get(posicion).valor);  //  -----> "segundo", "cuarto"
					//writer.beginObject();
					//writer.name(listaPropiedades.get(posicion).nombre).value(listaPropiedades.get(posicion).valor);
					//writer.endObject();
					posicion++;
				}          
				writer.endArray();     // ----> ]
			} 
			writer.endObject();    // ----> }
			writer.close();

		} catch (IOException e) {
			System.out.format("\nERROR AL GENERAR EL FICHERO JSON DE SALIDA: %s", e.getMessage());
		}
		
	}	
// Método auxiliar para buscar en el ArrayList el primer elemento que contenga el String pasado como parámetro en su nombre. Devuelve -1 si no lo encuentra
private static int buscarPropiedad(String nombre, List<Propiedad> listaPropiedades){
	for (int i=0;i<listaPropiedades.size();i++)
		if(listaPropiedades.get(i).nombre.equals(nombre)) return i;
	return -1;
}
}

