package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


/* En esta clase se comportará como un hilo */

public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String,String>>> mDatasetConcepts;
	private String nombreHilo;
	
	
	public JSONDatasetParser (String fichero, List<String> lConcepts, 
	ConcurrentHashMap<String, List<Map<String,String>>> mDatasetConcepts) { 
		this.fichero=fichero;
		this.lConcepts=lConcepts;
		this.mDatasetConcepts=mDatasetConcepts;
	}

	
	@Override
	public void run (){
		List<Map<String,String>> graphs=new ArrayList<Map<String,String>>();	// Aquí se almacenarán todos los graphs de un dataset cuyo objeto de nombre @type se corresponda con uno de los valores pasados en el la lista lConcepts
		boolean finProcesar=false;	// Para detener el parser si se han agregado a la lista graphs 5 graph
	
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo="["+Thread.currentThread().getName()+"] ";
	    System.out.println(nombreHilo+"Empezar a descargar de internet el JSON");
	    try {
	    	InputStreamReader inputStream = new InputStreamReader(new URL(fichero).openStream(), "UTF-8"); 
			//TODO:
			//	- Crear objeto JsonReader a partir de inputStream
			//  - Consumir el primer "{" del fichero
			//  - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta que finProcesar=true
			//		Si se encuentra el objeto @graph, invocar a procesar_graph()
			//		Descartar el resto de objetos
			//	- Si se ha llegado al fin del fichero, consumir el último "}" del fichero
			//  - Cerrar el objeto JsonReader
			JsonReader jsonReader = new JsonReader(inputStream);
			jsonReader.setLenient(true);  // Para poder saltarse valores con skipValue()

			// inicio del consumo de los evantos del fichero json

			jsonReader.beginObject(); // Consumo el "{" de apertura del objeto
			while (jsonReader.hasNext()){
				//jsonReader.beginObject(); // Consumo el "{" de apertura del objeto
					String name = jsonReader.nextName();
					if (name.equals("@graph")){
						finProcesar=procesar_graph(jsonReader, graphs, lConcepts);
						if (finProcesar) break;
					}
					else jsonReader.skipValue();
				}
				//jsonReader.endObject(); // Consumo el "}" de cierre del objeto
				jsonReader.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			System.out.println(nombreHilo+"El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.out.println(nombreHilo+"Hubo un problema al abrir el fichero. Ignorándolo" + e);
		}
	    mDatasetConcepts.put(fichero, graphs); 	// Se añaden al Mapa de concepts de los Datasets
	    
	}

	/* 	procesar_graph()
	 * 	Procesa el array @graph
	 *  Devuelve true si ya se han añadido 5 objetos a la lista graphs
	 */
	private boolean procesar_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		boolean finProcesar=false;
		// TODO:
		//	- Consumir el primer "[" del array @graph
		//  - Procesar todos los objetos del array, hasta el final de fichero o hasta que finProcesar=true
		//  	- Consumir el primer "{" del objeto
		//  	- Procesar un objeto del array invocando al método procesar_un_graph()
		//  	- Consumir el último "}" del objeto
		// 		- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
		//	- Si se ha llegado al fin del array, consumir el último "]" del array
		jsonReader.beginArray();
		while (jsonReader.hasNext()){
						jsonReader.beginObject();
						procesar_un_graph(jsonReader, graphs, lConcepts);
						jsonReader.endObject();
						if (graphs.size() == 5){
							finProcesar = true;
							break;
						}
					}
					//jsonReader.endArray();
					return finProcesar;
	}

	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph y lo añade a la lista graphs si en el objeto de nombre @type hay un valor que se corresponde con uno de la lista lConcepts
	 */
	
	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		// TODO:
		//	- Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		//	- Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es así
		//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y añadir este mapa al mapa graphs
		Map<String, String> map = new HashMap<String, String>();
		while (jsonReader.hasNext()){
			switch(jsonReader.nextName()){
				case "@type":
					String type = jsonReader.nextString();
					if (lConcepts.contains(type)){
						map.put("@type", type);
					}
					break;
				case "@id":
					map.put("@id", jsonReader.nextString());
					break;
				case "link":
					map.put("link", jsonReader.nextString());
					break;
				case "title":
					map.put("title", jsonReader.nextString());
					break;
				case "event-location":
					map.put("eventLocation", jsonReader.nextString());
					break;
				case "address":
					jsonReader.beginObject();
					while(jsonReader.hasNext()){
						switch(jsonReader.nextName()){
							case "area":
							jsonReader.beginObject();
							while (jsonReader.hasNext()) {
								switch (jsonReader.nextName()) {				// Se procesan las propiedades que interean
								
								case "@id":	
									map.put("area", jsonReader.nextString());
									break;
								case "locality":	
									 map.put("locality",jsonReader.nextString());
										break;
								case "street-address":
									 map.put("street-address",jsonReader.nextString());
									break;
								default:	
									jsonReader.skipValue();
								}
							}
							jsonReader.endObject();
							break;		
						default:
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
					break;
				case "dtstart":
					map.put("dtstart", jsonReader.nextString());
					break;
				case "dtend":
					map.put("dtend", jsonReader.nextString());
					break;
				case "location":
					jsonReader.beginObject();
					while (jsonReader.hasNext()) {	
						switch (jsonReader.nextName()) {
						case "latitude":
						map.put("latitude",jsonReader.nextString());
							break;
						case "longitude":
							map.put("longitude",jsonReader.nextString());
							break;
						default:	
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
					break;
				case "description":
					map.put("description", jsonReader.nextString());
					break;
				default:
					jsonReader.skipValue();
				}
			}
		if (lConcepts.contains(map.get("@type")) && !graphs.contains(map)){
			graphs.add(map);
		}
	}
}
