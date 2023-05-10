package piat.opendatasearch;

import java.util.List;
import java.util.Map;

/**
 * @author Alvaro Miguel Arroyo Gonzalez 51549946T
 *
 */


/**
 * Clase estática para crear un String que contenga el documento xml a partir de la información almacenadas en las colecciones 
 *
 */	
	
	public class GenerarXML {
		private static final String pConcept		= "\n\t\t\t<concept>#valor#</concept>" ;
		private static final String pDataset		= "\n\t\t\t<dataset id=\"#valor#\">";
		private static final String pTitle			= "\n\t\t\t\t<title>#valor#</title>";
		private static final String pDescription	= "\n\t\t\t\t<description>#valor#</description>";
		private static final String pTheme			= "\n\t\t\t\t<theme>#valor#</theme>";

		/* Practica 4 */
		private static final String pResource		= "\n\t\t\t<resource id=\"#valor#\">";
		private static final String pConcept2		= "\n\t\t\t<concept id=\"#valor#\"/>" ;
		
		private static final String pLink= "\n\t\t\t\t<link> <![CDATA[#valor#]]> </link>" ;
		private static final String pEventLocation= "\n\t\t\t\t\t\t<eventLocation>#valor#</eventLocation>";
		private static final String pArea= "\n\t\t\t\t\t\t\t\t<area>#valor#</area>";
		private static final String pLocality= "\n\t\t\t\t\t\t\t\t<locality>#valor#</locality>";
		private static final String pStreet= "\n\t\t\t\t\t\t\t\t<street>#valor#</street>";
		private static final String pStart= "\n\t\t\t\t\t\t\t<start>#valor#</start>";
		private static final String pEnd= "\n\t\t\t\t\t\t\t<end>#valor#</end>";
		private static final String pGeoreference= "\n\t\t\t\t\t\t<georeference>#valor#</georeference>";
		


		

		/**  
		 * Método que deberá ser invocado desde el programa principal
		 * Le metemos la lista y mapa gordo para que nos lo ponga en formato XML , y lo convierte en un String, que habra que meter 
		 * en nuestro fichero de salida
		 * 
		 * @param Colecciones con la información obtenida del documento XML de entrada
		 * @return String con el documento XML de salida
		 */	
		public static String generar (List<String> lConcepts, Map<String, Map<String, String>> hDatasets, String [] args,  Map<String, List<Map<String, String>>> map){
			StringBuilder salidaXML= new StringBuilder();
			salidaXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			salidaXML.append("<searchResults \txmlns=\"http://piat.dte.upm.es/practica4\"");
			salidaXML.append("\n\t\t\t\txmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
			salidaXML.append("\n\t\t\t\txsi:schemaLocation=\"http://piat.dte.upm.es/practica4 ResultadosBusquedaP4.xsd\">");
			salidaXML.append("\n\t<summary>");
			salidaXML.append("\n\t\t<query>"+args[0]+"</query>");
			salidaXML.append("\n\t\t<numConcepts>"+lConcepts.size()+"</numConcepts>");
			salidaXML.append("\n\t\t<numDatasets>"+hDatasets.size()+"</numDatasets>");
			salidaXML.append("\n\t</summary>");
			salidaXML.append("\n\t<results>");

			salidaXML.append("\n\t\t<concepts>");
			for (String aux : lConcepts) {
				salidaXML.append(pConcept.replace("#valor#", aux));
			}
			salidaXML.append("\n\t\t</concepts>");
			if(hDatasets.size() != 0){

				salidaXML.append("\n\t\t<datasets>");
				for (Map.Entry<String, Map<String, String>> entry : hDatasets.entrySet()) {
					Map<String, String> mapaValor = entry.getValue();
					if (mapaValor.containsKey("title") && mapaValor.containsKey("description") && mapaValor.containsKey("theme")) {
						salidaXML.append(pDataset.replace("#valor#", entry.getKey()));
						salidaXML.append(pTitle.replace("#valor#", mapaValor.get("title")));
						salidaXML.append(pDescription.replace("#valor#", mapaValor.get("description")));
						salidaXML.append(pTheme.replace("#valor#", mapaValor.get("theme")));
						salidaXML.append("\n\t\t\t</dataset>");
					}
				}
				salidaXML.append("\n\t\t</datasets>");
				resourcesToXML(salidaXML, map);
			}
			salidaXML.append("\n\t</results>");
			salidaXML.append("\n</searchResults>");
			return salidaXML.toString();
		}
		
	
		/*********** Practica 4 *********************/

		public static void resourcesToXML (StringBuilder salidaXML, Map<String, List<Map<String,String>>> mapa){
			salidaXML.append("\n\t\t<resources>");
			
			for (Map.Entry<String, List<Map<String, String>>> entry : mapa.entrySet()) {
				List<Map<String, String>> lista = entry.getValue();
				for (Map<String, String> mapaValor : lista) {
					salidaXML.append (pResource.replace("#valor#", entry.getKey()));
					String [] string = {"@type"};
					valorValido(pConcept2, salidaXML, mapaValor, string);	
					string[0] = "link";
					valorValido(pLink, salidaXML, mapaValor, string);	
					string[0] = "title";
					valorValido(pTitle, salidaXML, mapaValor, string);	
					salidaXML.append("\n\t\t\t\t<location>" );
					string[0] = "eventLocation";
					valorValido(pEventLocation, salidaXML, mapaValor, string);	
					salidaXML.append("\n\t\t\t\t\t\t<address>" );
					string[0] = "area";
					valorValido(pArea, salidaXML, mapaValor, string);	
					string[0] = "locality";
					valorValido(pLocality, salidaXML, mapaValor, string);	
					string[0] = "street-address";
					valorValido(pStreet, salidaXML, mapaValor, string);	
					salidaXML.append("\n\t\t\t\t\t\t</address>" );
					salidaXML.append("\n\t\t\t\t\t\t<timetable>" );
					string[0] = "dtstart";
					valorValido(pStart, salidaXML, mapaValor, string);	
					string[0] = "dtend";
					valorValido(pEnd, salidaXML, mapaValor, string);
					salidaXML.append("\n\t\t\t\t\t\t</timetable>" );
					String [] string1 = {"latitude", "longitude"};
					valorValido(pGeoreference, salidaXML, mapaValor, string1);	
					salidaXML.append("\n\t\t\t\t</location>" );
					string[0] = "description";
					valorValido(pDescription, salidaXML, mapaValor, string);	
					salidaXML.append("\n\t\t\t</resource>");
				}
			}
			salidaXML.append("\n\t\t</resources>");
		}
		
		private static void valorValido(String patron, StringBuilder salidaXML, Map<String, String> map, String [] key){
			if(map.containsKey(key[0])){
				switch(key.length) {
				case 2:
					if(!map.get(key[0]).equals("") && !map.get(key[1]).equals(""))
						salidaXML.append (patron.replace("#valor#", map.get(key[0]) + " " + map.get(key[1])));
					break;
				case 1:
					if(!map.get(key[0]).equals(""))
						salidaXML.append (patron.replace("#valor#", map.get(key[0])));		
					break;
				default:
					break;
				}
			}
		}
	}

