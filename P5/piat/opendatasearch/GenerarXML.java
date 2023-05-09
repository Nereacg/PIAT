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
	private static final String pConcept		= "\n\t\t\t<concept>#ID#</concept>" ;
	private static final String pDataset		= "\n\t\t\t<dataset id=\"#ID#\">";
	private static final String pTitle			= "\n\t\t\t\t<title>#valor#</title>";
	private static final String pDescription	= "\n\t\t\t\t<description>#valor#</description>";
	private static final String pTheme			= "\n\t\t\t\t<theme>#valor#</theme>";

	/* Practica 4 */
	private static final String pResource		= "\n\t\t\t<resource id=\"#ID#\">";
	private static final String pConcept2		= "\n\t\t\t<concept id=\"#ID#\"/>" ;
	
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
	public static String generar (List<String> lConcepts, Map<String, Map<String, String>> hDatasets,
					String [] args, Map<String, List<Map<String, String>>> map){
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
			salidaXML.append(pConcept.replace("#ID#", aux));
		}
		salidaXML.append("\n\t\t</concepts>");
		if(hDatasets.size() != 0){

			salidaXML.append("\n\t\t<datasets>");
			for (Map.Entry<String, Map<String, String>> entry : hDatasets.entrySet()) {
				Map<String, String> mapaValor = entry.getValue();
				if (mapaValor.containsKey("title") && mapaValor.containsKey("description") && mapaValor.containsKey("theme")) {
					salidaXML.append(pDataset.replace("#ID#", entry.getKey()));
					salidaXML.append(pTitle.replace("#valor#", mapaValor.get("title")));
					salidaXML.append(pDescription.replace("#valor#", mapaValor.get("description")));
					salidaXML.append(pTheme.replace("#valor#", mapaValor.get("theme")));
					salidaXML.append("\n\t\t\t</dataset>");
				}
			}
			salidaXML.append("\n\t\t</datasets>");
			generarResources(salidaXML, map);
		}
		salidaXML.append("\n\t</results>");
		salidaXML.append("\n</searchResults>");
		return salidaXML.toString();
	}

	/*********** Practica 4 *********************/

	public static void generarResources (StringBuilder salidaXML, Map<String, List<Map<String,String>>> mapa){
		salidaXML.append("\n\t\t<resources>");
		
		for (Map.Entry<String, List<Map<String, String>>> entry : mapa.entrySet()) {
			List<Map<String, String>> lista = entry.getValue();
			for (Map<String, String> mapaValor : lista) {
					
				salidaXML.append (pResource.replace("#ID#", entry.getKey()));
				salidaXML.append (pConcept2.replace("#ID#", mapaValor.get("@type")));
				salidaXML.append (pLink.replace("#valor#", mapaValor.get("link")));
				salidaXML.append (pTitle.replace("#valor#", mapaValor.get("title")));
				salidaXML.append("\n\t\t\t\t<location>" );
				salidaXML.append (pEventLocation.replace("#valor#", mapaValor.get("eventLocation")));
				salidaXML.append("\n\t\t\t\t\t\t<address>" );
				salidaXML.append (pArea.replace("#valor#", mapaValor.get("area")));
				salidaXML.append (pLocality.replace("#valor#", mapaValor.get("locality")));
				salidaXML.append (pStreet.replace("#valor#", mapaValor.get("street-address")));
				salidaXML.append("\n\t\t\t\t\t\t</address>" );
				salidaXML.append("\n\t\t\t\t\t\t<timetable>" );
				salidaXML.append (pStart.replace("#valor#", mapaValor.get("dtstart")));
				salidaXML.append (pEnd.replace("#valor#", mapaValor.get("dtend")));
				salidaXML.append("\n\t\t\t\t\t\t</timetable>" );
				salidaXML.append (pGeoreference.replace("#valor#", mapaValor.get("latitude") + " " + mapaValor.get("longitude")));
				salidaXML.append("\n\t\t\t\t</location>" );
				salidaXML.append (pDescription.replace("#valor#", mapaValor.get("description")));
				
				salidaXML.append("\n\t\t\t</resource>");
				
			}
			
		}
		
		salidaXML.append("\n\t\t</resources>");
	}
}