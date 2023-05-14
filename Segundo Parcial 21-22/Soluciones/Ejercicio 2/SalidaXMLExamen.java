package piat.opendatasearch;

/* Clase estática para crear un String con el xml a partir de la información que se pasa como parámetro en mGraph*/


import java.util.Map;

public class SalidaXMLExamen {


	private static final String graphPattern= "\n\t<pc:graph id=\"#ID#\">" ;

	public static String generar (Map<String, Map<String, String>> mGraph ){
		StringBuilder salidaXML= new StringBuilder();
		salidaXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<pc:graphs xmlns:pc=\"http//www.piat.dte.upm.es/examenJunio\"\n" + 
				" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				" xsi:schemaLocation=\"http//www.piat.dte.upm.es/examenJunio graphs.xsd\">\n" );

		for (Map.Entry<String, Map<String,String>> unGraph : mGraph.entrySet()) {
			salidaXML.append (graphPattern.replace("#ID#", unGraph.getKey()));
			Map<String, String> contenido = unGraph.getValue();
			salidaXML.append ("\n\t\t<pc:tipoPostalCode>"+contenido.get("tipoPostalCode")+"</pc:tipoPostalCode>");
			salidaXML.append ("\n\t\t<pc:postalCode>"+contenido.get("postalCode")+"</pc:postalCode>");
			salidaXML.append("\n\t</pc:graph>");
		}		

		salidaXML.append("\n</pc:graphs>");
		return salidaXML.toString();

	}

}

