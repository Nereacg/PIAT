
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XMLTransformer {
	private final String sXMLFile;


	/**
	 * @param sXMLFile	path a un documento XML válido
	 */
	public XMLTransformer(String sXMLFile) {
		this.sXMLFile=sXMLFile;


	}

	/** transform()
	 * @param sXSLFile	path al fichero XSL con la descripción de la transformación
	 * @param sOUTFile	path al fichero resultante de la transformación
	 */
	public void transform(String sXSLFile, String sOUTFile){
		try {
			// Documentos implicados en la transformación
			StreamSource xmlSource =new StreamSource(sXMLFile);
			StreamSource xslSource =new StreamSource(sXSLFile);
			
			StreamResult docResult;
			if (sOUTFile.equals("consola"))		// Si se invoca desde el main, se puede pasar este string para que la salida sea sobre consola
					docResult =new StreamResult(System.out);
			else 		
				docResult =new StreamResult(sOUTFile);

			TransformerFactory transFact= TransformerFactory.newInstance();			// Instancia de TransformerFactory
			Transformer transformer=transFact.newTransformer(xslSource);			// Creación del Transformer con la hoja de transformación
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");				// Configuración del Transformer
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");

			// Transformación
			transformer.transform(xmlSource, docResult);
			
		}catch (Exception e) { System.out.println(e.getMessage()); }


	}	


	
	
}
