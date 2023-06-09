package piat.opendatasearch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;
/**
 * @author Nerea Calderon Gonzalo 50356369P
 *
 */

/**
 * Clase principal de la aplicaciÃƒÂ³n de extracciÃƒÂ³n de informaciÃƒÂ³n del 
 * Portal de Datos Abiertos del Ayuntamiento de Madrid
 *
 */
public class P5_XPATH {


	public static void main(String[] args) throws IOException, InterruptedException {
		
	try{	
			// Verificar nÃ‚Âº de argumentos correcto
		if (args.length!=5){
			String mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
		
		// TODO

		
		/*
		 * Verificar que se corresponden con el tipo de informaciÃƒÂ³n que se espera de ellos
		 */
		
		
			/*1.1 ARG0 debe ser reconocer un patrÃƒÂ³n del tipo: Ã¢â‚¬Å“018Ã¢â‚¬?, Ã¢â‚¬Å“0001-018Ã¢â‚¬?, Ã¢â‚¬Å“0001-0003FL18Ã¢â‚¬?*/
				
				Pattern patronArg0 = Pattern.compile("^[0-9]{3,4}(-[0-9A-Z]{3,8})*$"); 
				Matcher matcherArg0 = patronArg0.matcher(args[0]);
				
				if(!matcherArg0.matches()) {
					System.out.println("\n El argumento 0 no estÃƒÂ¡ bien escrito.\n");
				}
				
			/*1.2 ARG1 y ARG3 deben finalizar con los caracteres Ã¢â‚¬Å“.xmlÃ¢â‚¬?*/
		
		Pattern pattern_xml = Pattern.compile( ".*\\.xml$");
		Matcher matcherArg1 = pattern_xml.matcher(args[1]);
		Matcher matcherArg3= pattern_xml.matcher(args[3]);
		Pattern pattern_json = Pattern.compile( ".*\\.json$");
		Matcher matcherArg4 = pattern_json.matcher(args[4]);
		if(!matcherArg1.matches()) { 
			System.out.println("\n El argumento 1 no tiene la extensiÃƒÂ³n adecuada.\n");
		}else if(!matcherArg3.matches()) { 
			System.out.println("\n El argumento 3 no tiene la extensiÃƒÂ³n adecuada.\n");
		}else if(!matcherArg4.matches()) { 
			System.out.println("\n El argumento 4 no tiene la extensiÃƒÂ³n adecuada.\n");
		}
		
		
		
		
		 //2.1 Verificar que ARG1 se corresponde con el path de un fichero al que se tiene permiso de acceso de lectura

		File archivoCatalogo = new File (args[1]);
		
			// 2.1 check if the file exists
		if(archivoCatalogo.exists()) {
			if(!archivoCatalogo.canRead()) {
        		System.out.println("\n No se tiene permisos de lectura para "+archivoCatalogo+".\n");
        	}
		}else {
			System.out.println(archivoCatalogo+" not found.");
            System.exit(1);
		}

		
		//2.2 Verificar que ARG2 se corresponde con el path de un fichero al que se tiene permiso de acceso de lectura
	
		File ResultadosBusquedaP4 = new File (args[2]);
		
		// check if the file exists
	if(ResultadosBusquedaP4.exists()) {
		if(!ResultadosBusquedaP4.canRead()) {
    		System.out.println("\n No se tiene permisos de lectura para "+ResultadosBusquedaP4+".\n");
    	}
	}else {
		System.out.println(ResultadosBusquedaP4+" not found.");
        System.exit(1);
	}
		
		
        /*
		 * 3 Verificar que ARG3 se corresponde con el path de un fichero al que se tiene permiso de acceso de escritura *de la P4(
		 */
			File archivoSalidaP4  = new File(args[3]);
			archivoSalidaP4.delete();
			if(archivoSalidaP4.createNewFile() && archivoSalidaP4.canWrite()) {
				System.out.println("Se pude crear y escribir en el fichero "+args[3]);
			}
	        /*
			 * 3 Verificar que ARG4 se corresponde con el path de un fichero al que se tiene permiso de acceso de escritura *de la P5(
			 */
				File archivoSalidaP5  = new File(args[4]);
				archivoSalidaP5 .delete();
				if(archivoSalidaP5.createNewFile() && archivoSalidaP5.canWrite()) {
					System.out.println("Se pude crear y escribir en el fichero "+args[4]);
				}
			 		 
		 
		 
		 
		

			
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			//Instanciar un objeto SAXParser e invocar a su mÃƒÂ©todo parse() pasando como parÃƒÂ¡metro un descriptor de fichero, cuyo nombre se recibiÃƒÂ³ en el primer argumento de main(), y la instancia del objeto ManejadorXML 
			SAXParser saxParser = factory.newSAXParser();
			//Instanciar un objeto ManejadorXML pasando como parÃƒÂ¡metro el cÃƒÂ³digo de la categorÃƒÂ­a recibido en el primer argumento de main()
			ManejadorXML manejadorXML = new ManejadorXML(args[0]);
			
			saxParser.parse(new File(args[1]), manejadorXML);
			//Invocar al mÃƒÂ©todo getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el cÃƒÂ³digo de la categorÃƒÂ­a buscado
			//Invocar al mÃƒÂ©todo getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categorÃƒÂ­a buscada
			List<String> lConcepts = manejadorXML.getConcepts();
			Map<String, Map<String, String>> hDatasets = manejadorXML.getDatasets();
			//Crear el fichero de salida con el nombre recibido en el cuarto argumento de main()
			File salida = new File(args[3]);		
			//Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP4.xsd
			Map<String, List<Map<String, String>>> bigMap = getDatasetConcepts(manejadorXML.getConcepts(), manejadorXML.getDatasets());
			String contenido = new GenerarXML().generar(manejadorXML.getConcepts(), manejadorXML.getDatasets(), args, bigMap);
			salida.delete();
			//Escribir en el fichero de salida el contenido del List<String> obtenido en el paso anterior
			FileWriter writer = new FileWriter(salida, true);
			writer.write(contenido);
			writer.close();
			
			//Validar el fichero generado con el esquema recibido en el tercer argumento de main()
			SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			File schemaFile = new File(args[2]);
			Schema schema = schemaFactory.newSchema(schemaFile);
			Validator validator = schema.newValidator();
			File xmlFile = new File(args[3]);
			validator.validate(new StreamSource(xmlFile));
			 
			//Practica 5 - Generar el fichero JSON
			GenerarJSON.generar(contenido, XPATH_Evaluador.evaluar(args[3]));
		} catch(SAXException | ParserConfigurationException | IOException
				| XPathExpressionException e){
			e.printStackTrace();
		}

		System.exit(0);
		
		
		
		
	};
	


	
	/**
	 * Muestra mensaje de los argumentos esperados por la aplicaciÃƒÂ³n.
	 * DeberÃƒÂ¡ invocase en la fase de validaciÃƒÂ³n ante la detecciÃƒÂ³n de algÃƒÂºn fallo
	 *
	 * @param mensaje  Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje+"\n");
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <cÃƒÂ³digoCategorÃƒÂ­a> <ficheroCatalogo> <ficheroXSDsalida> <ficheroXMLSalida>\n" +
				"donde:\n"+
				"\t cÃƒÂ³digoCategorÃƒÂ­a:\t cÃƒÂ³digo de la categorÃƒÂ­a de la que se desea obtener datos\n" +
				"\t ficheroCatalogo:\t path al fichero XML con el catÃƒÂ¡logo de datos\n" +
				"\t ficheroXSDsalida:\t nombre del fichero que contiene el esquema contra el que se tiene que validar el documento XML de salida\n"	+
				"\t ficheroXMLSalida:\t nombre del fichero XML de salida\n"
				);				
	}
	
	
	
	/*************************************************************  EMPIEZA PRACTICA 4  *************************************************************************/

	private static Map<String, List<Map<String,String>>>getDatasetConcepts(List<String>
		lConcepts, Map<String, Map<String, String>> mDatasets) throws InterruptedException {
		
		Map<String, List<Map<String,String>>> bigMap = new HashMap<String, List<Map<String,String>>>();
		//Devuelve un mapa donde la clave es el id del dataset y los valores son todos los graphs encontrados
		//en el json que son pertinentes*/
		
		//Obtener el nÂº de nÃºcleos del procesador del ordenador
		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		
		// Crear un pool donde ejecutar los hilos. El pool tendrÃ¡ un tamaÃ±o del nÂº de nÃºcleos del ordenador
		// por lo que nunca podrÃ¡ haber mÃ¡s hilos que ese nÃºmero en ejecuciÃ³n simultÃ¡nea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecuciÃ³n, poner como argumento un 1. IrÃ¡ mucho mÃ¡s lenta la ejecuciÃ³n porque los ficheros se procesarÃ¡n secuencialmente
			ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);
			
			final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
			    int numTrabajadores=0;
		    //idDataset es la clave del mapa que contiene los dataset
			for (String json: mDatasets.keySet()){
				//System.out.print (".");
				ejecutor.execute(new JSONDatasetParser (json, lConcepts, bigMap));
				numTrabajadores++;
				//break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
			System.out.print ("\nEn total se van a ejecutar "+numTrabajadores+" trabajadores en el pool. Esperar a que terminen ");
			
			// Esperar a que terminen todos los trabajadores
			ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el Ãºltimo trabajador
			// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que quedan
			while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
				final int terminados=numTrabajadoresTerminados.get();
				System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numTrabajadores-terminados)+" que quedan ");
			}
			// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados
			System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" JSONDatasetParser");
			

			return bigMap;
		}

	
	
	}