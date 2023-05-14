package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gson.stream.JsonReader;


/* En esta clase se comportará como un hilo */

public class JSONGraphParser implements Runnable {
	private String fichero;
	private Map<String, Map<String,String>> mGraphValues;
	private String nombreHilo;
	private final AtomicInteger numTrabajadoresTerminados;
	private Map<String,String> contenido=new HashMap<String,String>();


	public JSONGraphParser (String fichero, Map<String, Map<String, String>> mGraphValues, AtomicInteger numTrabajadoresTerminados) {
		this.fichero=fichero;
		this.mGraphValues=mGraphValues;
		this.numTrabajadoresTerminados=numTrabajadoresTerminados;
	}

	@Override
	public void run (){
		String clave;

		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo="["+Thread.currentThread().getName()+"] ";
		System.out.println(nombreHilo+"Empezar a descargar de internet el JSON");
		try {
			InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8");
			JsonReader jsonReader = new JsonReader(inputStream);

			System.out.println(nombreHilo+"Procesándolo");
			jsonReader.setLenient(true);// Para poder saltarse valores con skipValue()

			jsonReader.beginObject();	// Consumir el primer "{" del fichero

			while (jsonReader.hasNext()) {	// Procesar todos los elementos del fichero
				clave=jsonReader.nextName();	// Obtener el nombre del objeto
				switch (clave) {
				case "@context":
					procesar_context (jsonReader);
					break;
				case "@graph":
					procesar_graph (jsonReader);
					break;

				default:	// Si no es uno de los anteriores, no lo procesamos
					jsonReader.skipValue();
				}
			}

			jsonReader.endObject();	// Consumir el último "}" del fichero
			jsonReader.close();	
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo+"El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo+"Hubo un problema al abrir el fichero. Ignorándolo");
		}
		mGraphValues.put(fichero, contenido); 	// Se añaden al Mapa que comparten todos los hilos el objeto contenido con la información que se ha obtenido del json 
		numTrabajadoresTerminados.incrementAndGet();
	}

	/* 	procesar_context()
	 * 	Procesa el array @context
	 */
	private void procesar_context(JsonReader jsonReader) throws IOException {
		String clave;
		jsonReader.beginObject();	// Consumir el primer "{" del fichero

		while (jsonReader.hasNext() ) {	// Procesar todos los objetos de context
			clave=jsonReader.nextName();	// Obtener el nombre de la propiedad
			switch (clave) { 
			case "postal-code":
				contenido.put("tipoPostalCode", jsonReader.nextString());
				break;
			default:	// Si no es uno de los anteriores, no lo procesamos
				jsonReader.skipValue();
			}
		}
		jsonReader.endObject();	// Consumir el último "}" del fichero

	}

	/* 	procesar_graph()
	 * 	Procesa el array @graph
	 */
	private void procesar_graph(JsonReader jsonReader) throws IOException {

		jsonReader.beginArray();	// Consumir el primer "[" del array @graph
		while (jsonReader.hasNext() ) {	// Procesar todos los objetos de este graph
			jsonReader.beginObject();	// Consumir el primer "{" del objeto
			procesar_un_graph (jsonReader);	// Procesa un objeto del array
			jsonReader.endObject();	// Consumir el último "}" del objeto
		}
		jsonReader.endArray();	// Consumir el último "]" del array @graph

	}

	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph
	 */
	private void procesar_un_graph(JsonReader jsonReader) throws IOException {
		String clave;

		while (jsonReader.hasNext()) {	// Procesar todas las propiedades de un objeto del array @graph 
			clave=jsonReader.nextName();	// Obtener el nombre de la propiedad
			switch (clave) { 
			case "address":		// Esta propiedad contiene objetos dentro, así que por simplificar este código, se obtienen en un método
				jsonReader.beginObject();	// Consumir el primer "{" del objeto
				procesar_address(jsonReader);
				jsonReader.endObject();	// Consumir el último "}" del objeto
				break;

			default:	// Si no es uno de los anteriores, no lo procesamos
				jsonReader.skipValue();
			}
		}
	}


	/*	procesar_address()
	 * 	Procesa los objetos de la propiedad address
	 */
	private void procesar_address(JsonReader jsonReader) throws IOException {
		String clave;

		while (jsonReader.hasNext()) {		// Procesar todas las propiedades de los objetos de address
			clave=jsonReader.nextName();	// Obtener el nombre de la propiedad
			switch (clave) { 
			case "postal-code":
				contenido.put("postalCode", jsonReader.nextString());
				break;

			default:	// Si no es uno de los anteriores, no lo procesamos
				jsonReader.skipValue();
			}
		}
	}	


}