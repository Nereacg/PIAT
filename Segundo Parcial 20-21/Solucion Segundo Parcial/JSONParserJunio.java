import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.stream.JsonReader;


public class JSONParserJunio {
	private String fichero;
	private String codigoConcepto;
	

	public JSONParserJunio (String fichero, String codigoConcepto) {
		this.fichero=fichero;
		this.codigoConcepto=codigoConcepto;
	}


	public Map <String, HashMap<String, String>> getResults (){
		Map <String, HashMap<String, String>> documentoResultante=null;
		String clave;
		String conceptID="";
		System.out.println("Empezar a analizar el JSON");
		try {
            JsonReader jsonReader = new JsonReader(new FileReader(fichero));

			jsonReader.setLenient(true);// Para poder saltarse valores con skipValue()

			jsonReader.beginObject();	// Consumir el primer "{" del fichero

			while (jsonReader.hasNext()) {	// Procesar todos los elementos del fichero
				clave=jsonReader.nextName();	// Obtener el nombre del objeto
				switch (clave) {
				case "concepts":
					conceptID=procesar_concepts (jsonReader);
					break;
				case "datasets":
					documentoResultante=procesar_datasets (jsonReader,conceptID);
					break;
				default:	// Si no es uno de los anteriores, no lo procesamos. 
					jsonReader.skipValue();
				}
			}


			jsonReader.close();	
		} catch (FileNotFoundException e) {
			System.err.println("El fichero no existe. Ignorándolo");
		} catch (IOException e) {
			System.err.println("Hubo un problema al abrir el fichero. Ignorándolo");
		}
		return documentoResultante;

	}

	private String procesar_concepts(JsonReader jsonReader) throws IOException {
		String conceptID,clave,id,code;
		conceptID=code=id="";
		
		jsonReader.beginArray();	// Consumir el primer "[" del array concepts
		while (jsonReader.hasNext() ) {	// Procesar todos los objetos del concepts
			jsonReader.beginObject();	// Consumir el primer "{" del objeto

			// Procesar cada concept
			while (jsonReader.hasNext()) {	// Procesar todas las propiedades de un objeto concept 
				clave=jsonReader.nextName();	// Obtener el nombre de la propiedad
				switch (clave) {	// Mediante este switch se van almacenando en las variables correspondientes los valores de cada propiedad que interesa procesar 
				case "id":
					id=jsonReader.nextString();
					break;
				case "properties":
					jsonReader.beginObject();	// Consumir el primer "{" del objeto
					while (jsonReader.hasNext()) {
						clave=jsonReader.nextName();
						switch (clave) {
						case "code":
							code=jsonReader.nextString();
							break;
						default:	// Si no es uno de los anteriores, no lo procesamos
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();	// Consumir el último "}" del objeto
					break;
				default:	// Si no es uno de los anteriores, no lo procesamos
					jsonReader.skipValue();
				}			
			}
			if (code.equals(codigoConcepto))
				conceptID=id;
			
			jsonReader.endObject();	// Consumir el último "}" del objeto
		}

		jsonReader.endArray();	// Consumir el último "]" del array @graph
		
		return conceptID;
	}
	

	private Map <String, HashMap<String, String>> procesar_datasets(JsonReader jsonReader, String conceptID ) throws IOException {
		
		Map <String, HashMap<String, String>> documentoResultante = new HashMap <String, HashMap<String, String>>();

		jsonReader.beginArray();	// Consumir el primer "[" del array datasets
		while (jsonReader.hasNext() ) {	// Procesar todos los objetos de este array
			jsonReader.beginObject();	// Consumir el primer "{" del objeto
			procesar_un_dataset (jsonReader,conceptID,documentoResultante);	// Procesa un objeto del array
			jsonReader.endObject();	// Consumir el último "}" del objeto
		}
		jsonReader.endArray();	// Consumir el último "]" del array 
		
		return documentoResultante;
	}

	private void procesar_un_dataset (JsonReader jsonReader, String conceptID, Map <String, HashMap<String, String>>documentoResultante) throws IOException {
		String clave;
		String id,title,publisher;		// En estas variables se almacenarán los valores buscados
		id=title=publisher="";			// Es necesario inicializarlos a nada por si no existen esas propiedades en el objeto para que tengan ese valor inicial
		boolean pertinente=false;

		while (jsonReader.hasNext()) {	// Procesar todas las propiedades de un objeto del array de un dataset 
			clave=jsonReader.nextName();	// Obtener el nombre de la propiedad
			switch (clave) {	// Mediante este switch se van almacenando en las variables correspondientes los valores de cada propiedad que interesa procesar 
			case "id":
				id=jsonReader.nextString();
				break;
			case "title":
				title=jsonReader.nextString();
				break;				
			case "properties":		// Esta propiedad contiene un objeto dentro
					jsonReader.beginObject();
					while (jsonReader.hasNext()) {	
						clave=jsonReader.nextName();
						switch (clave) {
						case "concepts":
							pertinente=procesar_concepts(jsonReader,conceptID);
							break;
						case "publisher":
							publisher=jsonReader.nextString();
							break;								
						default:	// Si no es uno de los anteriores, no lo procesamos
							jsonReader.skipValue();
						}
					}
					jsonReader.endObject();
				break;    			
			default:	// Si no es uno de los anteriores, no lo procesamos
				jsonReader.skipValue();
			}
		}


	if (pertinente) { 
		HashMap<String, String> datos = new HashMap<String, String>();
		datos.put("title", title);
		datos.put("publisher", publisher);
		documentoResultante.put(id,datos);
		System.out.println("\tEncontrado un dataset pertinente con id "+id);
		}
	}


	private boolean procesar_concepts(JsonReader jsonReader, String conceptID) throws IOException {
		boolean encontrado,datasetencontrado=false;
		jsonReader.beginArray();	// Consumir el primer "[" del array concepts
		while (jsonReader.hasNext() ) {	// Procesar todos los objetos de este array
			jsonReader.beginObject();	// Consumir el primer "{" del objeto
			encontrado=procesar_un_concept_de_dataset (jsonReader,conceptID);	// Procesa un objeto del array
			if (encontrado)
				datasetencontrado=true;
			jsonReader.endObject();	// Consumir el último "}" del objeto
		}
		jsonReader.endArray();	// Consumir el último "]" del array @graph
		return datasetencontrado;
	}
	
	private boolean procesar_un_concept_de_dataset(JsonReader jsonReader,String conceptID) throws IOException {
		String clave,valor;
		boolean encontrado=false;

		while (jsonReader.hasNext()) {		// Se recorren todas las propiedades del objeto 
			clave=jsonReader.nextName();
			switch (clave) {				// Se procesan las propiedades que interean
				case "concept":	
						valor=jsonReader.nextString();
						if (valor.equals(conceptID))
							encontrado=true;
						break;
				default:	// Si no es uno de los anteriores, no lo procesamos
						jsonReader.skipValue();
			}
	
		}
		return encontrado;
	}


}