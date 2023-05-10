package piat.opendatasearch;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

/**
 * @author Alvaro Miguel Arroyo Gonzalez 51549946T
 *
 */

/**
 * Clase principal de la aplicación de extracción de información del 
 * Portal de Datos Abiertos del Ayuntamiento de Madrid
 *
 */
public class P5_XPATH {


	public static void main(String[] args) throws InterruptedException {
		
		// Verificar nº de argumentos correcto
		if (args.length!=5){
			String mensaje="ERROR: Argumentos incorrectos.";
			if (args.length>0)
				mensaje+=" He recibido estos argumentos: "+ Arrays.asList(args).toString()+"\n";
			mostrarUso(mensaje);
			System.exit(1);
		}		
		//Validar los argumentos recibidos en main()
		validArgs(args);

		try{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			factory.setNamespaceAware(true);
			//Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
			SAXParser saxParser = factory.newSAXParser();
			//Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el primer argumento de main()
			ManejadorXML manejadorXML = new ManejadorXML(args[0]);
			saxParser.parse(new File(args[1]), manejadorXML);
			//Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
			//Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada
			//Crear el fichero de salida con el nombre recibido en el cuarto argumento de main()
			Map<String, List<Map<String, String>>> mapa = getDatasetConcepts(manejadorXML.getConcepts(), manejadorXML.getDatasets());
			String contenido = GenerarXML.generar(manejadorXML.getConcepts(), manejadorXML.getDatasets(), args, mapa);
			File salida = new File(args[3]);
			salida.delete();
			//Escribir en el fichero de salida el contenido del List<String> obtenido en el paso anterior
			//FileWriter writer = new FileWriter(salida, true);
			//BufferedWriter para escribir en UTF-8 y evitar problemas con caracteres especiales
			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(salida), StandardCharsets.UTF_8));
			bufferedWriter.write(contenido);
			bufferedWriter.close();
			validXsd(args);
			//Practica 5 - Generar el fichero JSON
			GenerarJSON.generar(contenido, XPATH_Evaluador.evaluar(args[4]));
		} catch(SAXException | ParserConfigurationException | IOException
				| XPathExpressionException e){
			e.printStackTrace();
		}
		System.exit(0);
	}
	
	
	/**
	 * Muestra mensaje de los argumentos esperados por la aplicación.
	 * Deberá invocase en la fase de validación ante la detección de algún fallo
	 *
	 * @param mensaje  Mensaje adicional informativo (null si no se desea)
	 */
	private static void mostrarUso(String mensaje){
		Class<? extends Object> thisClass = new Object(){}.getClass();
		
		if (mensaje != null)
			System.err.println(mensaje+"\n");
		System.err.println(
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <códigoCategoría> <ficheroCatalogo> <ficheroXSDsalida> <ficheroXMLSalida>\n" +
				"donde:\n"+
				"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
				"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
				"\t ficheroXSDsalida:\t nombre del fichero que contiene el esquema contra el que se tiene que validar el documento XML de salida\n"	+
				"\t ficheroXMLSalida:\t nombre del fichero XML de salida\n"
				);				
	}
	/*
	 * Validar los argumentos recibidos en main()
	 * 1. El primer argumento es un código de categoría válido, es decir, 
	 * 		una cadena de caracteres alfanuméricos de 3 a 4 caracteres, seguida de un guión 
	 * 		y de una cadena de caracteres alfanuméricos de 3 a 8 caracteres, que puede repetirse 0 o más veces.
	 * 2. El segundo argumento es un path a un fichero XML válido.
	 * 3. El tercer argumento es un path a un fichero XSD válido.
	 * 4. El cuarto argumento es un path a un fichero XML válido.
	 * 
	 */
	private static void validArgs(String[] args) {
		String regex[] = {"^[0-9]{3,4}(-[0-9A-Z]{3,8})*$",
					"(.*xml)$",
					"(.*xsd)$",
					"(.*xml)$",
					"(.*json)$"};
		for (int i = 0; i < args.length; i++) {
			if (!args[i].matches(regex[i])) {
				// Código para manejar el caso en que args[i] no coincide con regex[j]
				throw new IllegalArgumentException("Argumento incorrecto: " + args[i]);
			}
		}
		try {
			validArg1_2(args[1]);
			validArg1_2(args[2]);
			validArg3(args[3]);
			validArg4(args[4]);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//Comprobar que se puede leer el fichero de entrada
	private static void validArg1_2(String arg){
		if(new File(arg).canRead())
			System.out.println("Se pude leer el fichero "+arg);
	}
	//Comprobar que se puede crear y escribir en el fichero de salida xml
	private static void validArg3(String arg) throws IOException{
		File file = new File(arg);
		file.delete();
		if(file.createNewFile() && file.canWrite())
			System.out.println("Se pude crear y escribir en el fichero "+arg);
	}
	//Comprobar que el fichero de salida es válido frente al esquema
	private static void validXsd(String [] args) throws SAXException, IOException{
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		File schemaFile = new File(args[2]);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		File xmlFile = new File(args[3]);
		validator.validate(new StreamSource(xmlFile));
	}
	
	/*************************************************************  EMPIEZA PRACTICA 4  *************************************************************************/
	
	private static Map<String, List<Map<String,String>>>getDatasetConcepts(List<String>
	lConcepts, Map<String, Map<String, String>> mDatasets) 
	throws SAXException,ParserConfigurationException,IOException, InterruptedException, InterruptedException {
		/*Devuelve un mapa donde la clave es el id del dataset y los valores son todos los graphs encontrados
		en el json que son pertinentes*/
		/* Código a completar:
		 Obtener el nº de núcleos del procesador del ordenador
		 Crear un pool donde ejecutar hilos del tamaño del nº de núcleos
		 Crear tantos hilos como entradas haya en el mapa de Dataset, pasándoles como
		parámetro la clave que contiene la url del JSON a procesar por el hilo. En el comentario se
		puede poner la instrucción:
		Runnable trabajador = new JSONDatasetParser (dataset.getKey(),
		Concepts,
		mDatasetConcepts);
		 Esperar a que terminen todos los hilos
		*/
		/****** Mapa que contendra la lista generada por los hilos ******/
		//Mapa concurrente, solo lo puede tocar un hilo a la vez, si hay un hilo modificandolo los demas esperan a que este acaba
		ConcurrentHashMap<String, List<Map<String, String>>> mapa = new ConcurrentHashMap<>();
		//Obtener el nº de núcleos del procesador del ordenador
		int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.println ("Se va a crear un pool de hilos para que como máximo haya " + numDeNucleos + " hilos en ejecución simultaneamente.");
		
		// Crear un pool donde ejecutar los hilos. El pool tendrá un tamaño del nº de núcleos del ordenador
		// por lo que nunca podrá haber más hilos que ese número en ejecución simultánea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecución, poner como argumento un 1. Irá mucho más lenta la ejecución porque los ficheros se procesarán secuencialmente
		ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);
		
		AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
		int numTrabajadores=0;
		System.out.print ("Lanzando hilos al pool ");
		for (String json: mDatasets.keySet()){
			System.out.print (".");
			ejecutor.execute(new JSONDatasetParser(json, lConcepts, mapa));
			numTrabajadores++;
			//break; // Descomentando este break, solo se ejecuta el primer trabajador
		}
		System.out.print ("\nEn total se van a ejecutar "+numTrabajadores+" JSONDatasetParser en el pool. Esperar a que terminen ");
		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el último trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado y los que quedan
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			final int terminados=numTrabajadoresTerminados.get();
			System.out.print("\nYa han terminado "+terminados+". Esperando a los "+(numTrabajadores-terminados)+" que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados
		System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" JSONDatasetParser");
		
		
		return mapa;
	}
	/*************************************************************  EMPIEZA PRACTICA 5  *************************************************************************/

	//Comprobar que se puede crear y escribir en el fichero de salida json
	private static void validArg4(String arg) throws IOException{
		File file = new File(arg);
		file.delete();
		if(file.createNewFile() && file.canWrite())
			System.out.println("Se pude crear y escribir en el fichero "+arg);
	}
}