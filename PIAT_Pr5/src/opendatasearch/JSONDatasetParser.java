package piat.opendatasearch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//Para quitar el error del .google se hace importando el jar y le das boton derecho y build path y add build path

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;


/* En esta clase se comportará como un hilo */

public class JSONDatasetParser implements Runnable {
	private String fichero;
	private List<String> lConcepts;
	private Map<String, List<Map<String,String>>> mDatasetConcepts;
	private String nombreHilo;
	
	
	public JSONDatasetParser (String fichero, List<String> lConcepts, Map<String, List<Map<String,String>>> mDatasetConcepts) { 
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
	    		JsonReader jsonReader = new JsonReader(inputStream);
	    	//  - Consumir el primer "{" del fichero
	    		jsonReader.beginObject();
			//  - Procesar los elementos del fichero JSON, hasta el final de fichero o hasta que finProcesar=true
	    		while(jsonReader.hasNext() && !finProcesar) {
	    			String name=jsonReader.nextName();
	    			//	Si se encuentra el objeto @graph, invocar a procesar_graph()
	    				if(name.equals("@graph")) {
	    					finProcesar= procesar_graph(jsonReader, graphs, lConcepts);
	    				}else {
	    					//Descartar el resto de objetos
	    					jsonReader.skipValue(); // Si no es uno de los anteriores, no lo procesamos.
	    				}
	    		}
	    
			//	- Si se ha llegado al fin del fichero, consumir el último "}" del fichero
	    		jsonReader.endObject();
				
	    		//  - Cerrar el objeto JsonReader
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
					jsonReader.beginArray();
		//  - Procesar todos los objetos del array, hasta el final de fichero o hasta que finProcesar=true
					while (jsonReader.hasNext()) {	
					//  	- Consumir el primer "{" del objeto
						jsonReader.beginObject();
		//  	- Procesar un objeto del array invocando al método procesar_un_graph()
						procesar_un_graph(jsonReader, graphs, lConcepts);
		//  	- Consumir el último "}" del objeto
						jsonReader.endObject();
		// 		- Ver si se han añadido 5 graph a la lista, para en ese caso poner la variable finProcesar a true
						if(graphs.size()==5) {
							finProcesar=true;
						}
					}
					
					
					
						//	- Si se ha llegado al fin del array, consumir el último "]" del array
					jsonReader.endArray();
					
	    return finProcesar;
		
	}


	/*	procesar_un_graph()
	 * 	Procesa un objeto del array @graph y lo añade a la lista graphs si en el objeto de nombre @type hay un valor que se corresponde con uno de la lista lConcepts
	 */
	
	private void procesar_un_graph(JsonReader jsonReader, List<Map<String, String>> graphs, List<String> lConcepts) throws IOException {
		// TODO:
		//	- Procesar todas las propiedades de un objeto del array @graph, guardándolas en variables temporales
		String id,type,title,description,dtstart,dtend,link,eventLocation,area, idd, locality, street,latitude,longitude;
		id=type=title=dtstart=dtend=link=eventLocation=area=idd=locality=street=latitude=longitude=description=""; // Es necesario inicializarlos a nada por si no existen esas propiedades en el objeto para que tengan ese valor inicial
		
		HashMap<String, String> mapaTemp = new HashMap<String, String>();

		while (jsonReader.hasNext()) {
			mapaTemp = new HashMap<String, String>();
			switch (jsonReader.nextName()) {				
				case "@id":	
					id=jsonReader.nextString();
					break;
				case "@type":	
					type=jsonReader.nextString();
					break;
				case "link":	
					link=jsonReader.nextString();
					break;
				case "title":	
					title=jsonReader.nextString();
					break;
				case "event-location":	
					eventLocation=jsonReader.nextString();
					break;
				case "address":	//dentro de esta propiedad hay que buscar el objeto area y extraer el id

					jsonReader.beginObject();
					while (jsonReader.hasNext()) {		// Se recorren todas las propiedades del objeto 
						switch (jsonReader.nextName()) {				// Se procesan las propiedades que interean
						
						case "area":
							jsonReader.beginObject();
							while (jsonReader.hasNext()) {
								switch (jsonReader.nextName()) {				// Se procesan las propiedades que interean
								
								case "@id":	
									idd=jsonReader.nextString();
									break;
								case "locality":	
									 locality=jsonReader.nextString();
										break;
								case "street-address":
									 street=jsonReader.nextString();
									break;
									default:	// Si no es uno de los anteriores, no lo procesamos
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
					dtstart=jsonReader.nextString();
					break;
				case "dtend":		
					dtend=jsonReader.nextString();
					break;
					
				case "location":	
					jsonReader.beginObject();
					while (jsonReader.hasNext()) {	
						switch (jsonReader.nextName()) {
						case "latitude":
							latitude=jsonReader.nextString();
							break;
						case "longitude":
							longitude=jsonReader.nextString();
							break;
						default:	
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
					break;
				case "description":	
					description=jsonReader.nextString();
					break;
						
				default:	
						jsonReader.skipValue();
			}
		}
		//	- Una vez procesadas todas las propiedades, ver si la clave @type tiene un valor igual a alguno de los concept de la lista lConcepts. Si es as�
		//	  guardar en un mapa Map<String,String> todos los valores de las variables temporales recogidas en el paso anterior y a�adir este mapa al mapa graphs
		if(lConcepts.contains(type) && graphs.size()<5 && !graphs.contains(mapaTemp)) {//Si se han a�adido 5 graph a la lista dejar de meterlos en el mapa
			mapaTemp.put("@id", id);
			mapaTemp.put("@type", type);
			mapaTemp.put("link", link);
			mapaTemp.put("title", title);
			mapaTemp.put("dtstart", dtstart);
			mapaTemp.put("dtend", dtend);
			mapaTemp.put("eventLocation", eventLocation);
			mapaTemp.put("area", idd);
			mapaTemp.put("locality", locality);
			mapaTemp.put("street-address", street);
			mapaTemp.put("latitude", latitude);
			mapaTemp.put("longitude", longitude);
			mapaTemp.put("description", description);

			graphs.add(mapaTemp);
			
		}
	}
	
	
}

