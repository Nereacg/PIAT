import java.util.HashMap;
import java.util.Map;

public class ResultsToXML {


	private static final String datasetPattern= "\n\t\t\t<dataset>#ID#</dataset>" ;
	
	private static final String resourceDatasetPattern= 	"\n\t\t\t<resource datasetOrigen=\"#ID#\">" ;
	private static final String titleDatasetPattern= 		"\n\t\t\t\t<title>#CONTENIDO#</title>" ;
	private static final String publisherPattern= 		"\n\t\t\t\t<publisher>#CONTENIDO#</publisher>" ;
	
	public static String resultsToXML (Map <String, HashMap<String, String>> datos){
		StringBuilder salidaXML= new StringBuilder();
		salidaXML.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
				"<results xmlns=\"http://www.piat.dte.upm.es/ExamenJunio\"\n" + 
				" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
				" xsi:schemaLocation=\"http://www.piat.dte.upm.es/ExamenJunioSchema ExamenJunioSchema.xsd\">\n" );	

		
		salidaXML.append("\n\t\t<datasets>");
		for (Map.Entry<String, HashMap<String,String>> unDataset : datos.entrySet()) {
			salidaXML.append (datasetPattern.replace("#ID#", unDataset.getKey()));
		}		
		salidaXML.append("\n\t\t</datasets>");

		salidaXML.append("\n\t\t<resources>");
		
		for (Map.Entry<String, HashMap<String,String>> unDataset : datos.entrySet()) {		
			HashMap<String, String> recursos = unDataset.getValue();	// Aquí se obtienen todos los recursos asociados a un dataset
			salidaXML.append (resourceDatasetPattern.replace("#ID#", unDataset.getKey()));
			salidaXML.append (titleDatasetPattern.replace("#CONTENIDO#", recursos.get("title")));
			salidaXML.append (publisherPattern.replace("#CONTENIDO#", recursos.get("publisher")));
			salidaXML.append("\n\t\t\t</resource>");
		}
		salidaXML.append("\n\t\t</resources>");		
		
		salidaXML.append("\n\t</results>");
		return salidaXML.toString();

	}

	
}