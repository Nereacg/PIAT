import java.util.Arrays;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.xml.sax.SAXException;

/* Ejemplo de parámetros válidos para su ejecución:
 * 		catalog-0040-022.json 0040-022 salida.xml salida.html 
 */

public class ExamenJunio {

	/**
	 * Clase principal de la aplicación de extracción de información del 
	 * <i>Portal de Datos Abiertos del Ayuntamiento de Madrid </i>
	 *
	 */

	private final static String pathEsquema = "ExamenJunioSchema.xsd";
	private final static String pathPlantillaXSL = "HojaEstilos.xsl";

	public static void main(String[] args) {
		
		if (args.length!=4){
			uso("ERROR: Argumentos incorrectos - " + args.length + "\n----" + Arrays.asList(args).toString());
			System.exit(1);
		}
		
		
		/* Verificar que el fichero XML de entrada que contiene el catálogo existe y es legible*/
		File fCatalog = new File (args[0]);
		if (!fCatalog.isFile() || !fCatalog.canRead()){
			uso ("Error: Config:'" + args[0]+"' no es un fichero o no es posible leerlo!");
			System.exit(1);
		}

		final String ficheroCatalogo 	= args[0]; 
		final String codigoConcepto 	= args[1];
		final String ficheroSalidaXML 	= args[2];
		final String ficheroSalidaHTML 	= args[3];
		

		try{

			JSONParserJunio jsonParserJunio = new JSONParserJunio(ficheroCatalogo,codigoConcepto);
			Map <String, HashMap<String, String>> documentoResultante=jsonParserJunio.getResults();
			
			// Volcar la salida a un fichero XML
			PrintStream	streamOut;
			streamOut = new PrintStream(ficheroSalidaXML,"UTF-8");
			// Invocar al método resultsToXML de la clase ResultToXML que devuelve un string con el xml generado
			streamOut.println (ResultsToXML.resultsToXML(documentoResultante));
			streamOut.close();
			System.out.println ("\nGenerado fichero xml: " +ficheroSalidaXML);

			validadorXML(args[2],pathEsquema);			
			
			// Generar HMTL usando XSL
			XMLTransformer transformer = new XMLTransformer (ficheroSalidaXML);
			transformer.transform(pathPlantillaXSL, ficheroSalidaHTML);
			System.out.println ("\nAplicada la plantilla "+pathPlantillaXSL+" para generar el fichero: " +ficheroSalidaHTML);


		} catch (FileNotFoundException e) {
			System.out.format("\nERROR FICHERO NO ENCONTRADO: %s", e.getMessage());
			System.exit(1);			
		}catch (IOException e){
			System.out.format("\nERROR EN ENTRADA SALIDA: %s", e.getMessage());
			System.exit(1);				
		}		

		System.exit(0);
	}


	/* 	validadorXML()
	 * 	Método para realizar una validación del esquema generado
	 */
	private static void validadorXML(String ficheroXML, String esquema) {

		File schemaFile = new File(esquema);
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema;

		System.out.print("\nValidando el documento generado contra el esquema: " + esquema);
		try {
			schema = schemaFactory.newSchema(schemaFile);
			Validator validador = schema.newValidator();
			Source xmlFileSalida = new StreamSource(ficheroXML);
			validador.validate(xmlFileSalida);
			System.out.println(" ... correcto");
		} catch (SAXException e) {
			System.err.println("ERROR: El documento no ha sido validado utilizando el esquema: " + esquema);
			System.err.println("MOTIVO: " + e.getMessage());
		} catch (IOException e) {
			System.err.println("ERROR: No se ha encontrado el documento a validar :" + ficheroXML);
			System.err.println("MOTIVO: " + e.getMessage());
		}
	}

	

	/**
	 * Muestra mensaje de los argumentos esperados por la aplicaciónn.
	 * Deberá invocarse en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje  Mensaje adicional informativo en la línea inicial (<em>null</em> si no se desea)
	 */
	private static void uso(String mensaje){
		if (mensaje != null)
			System.err.println(mensaje);
		System.err.println( 
				"\nUso: " + ExamenJunio.class.getName() +" <ficheroCatalogo> <codigoConcepto <ficheroSalida XML> <ficheroSalida XML>\n" +
						"\tficheroCatalogo:\t path al fichero JSON con el catálogo de datos\n" +
						"\tcodigoConcepto:\t\t código del 'concept'del que se desean obtener datos\n" +
						"\tficheroSalida XML:\t nombre del fichero XML de salida\n"+
						"\tficheroSalida HTML:\t nombre del fichero HTML de salida\n"
				);
	}

}
