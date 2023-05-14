package piat.opendatasearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


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

		try{

			Map<String, List<Map<String,String>>> mDatasetConcepts;	// Mapa para almacenar los concepts de los dataset

			/* Código realizado en la práctica 5:
			- Validar los argumentos recibidos en main()
			- Instanciar un objeto ManejadorXML pasando como parámetro el código de la categoría recibido en el segundo argumento de main()
			- Instanciar un objeto SAXParser e invocar a su método parse() pasando como parámetro un descriptor de fichero, cuyo nombre se recibió en el primer argumento de main(), y la instancia del objeto ManejadorXML 
			- Invocar al método getConcepts() del objeto ManejadorXML para obtener un List<String> con las uris de los elementos <concept> cuyo elemento <code> contiene el código de la categoría buscado
			- Invocar al método getLabel() del objeto ManejadorXML para obtener el nombre de la categoría buscada
			- Invocar al método getDatasets() del objeto ManejadorXML para obtener un mapa con los datasets de la categoría buscada. Guardarlo en mDatasets
			- Invocar al método getDatasetConcepts() de P4_JSON que utiliza la clase JSONDatasetParser para obtener un mapa con los concepts de los datasets
			- Crear el fichero de salida con el nombre recibido en el tercer argumento de main()
			- Volcar al fichero de salida los datos en el formato XML especificado por ResultadosBusquedaP4.xsd
			- Realizar las consultas XPATH de la práctica 5 y volcarlas sobre el fichero .json recibido en el cuarto argumento de main()
			 */			

			// Crear la lista de ficheros json a procesar
			List<String> listaIDs = new ArrayList<String>();

			// Agregar a listaIDs los valores de los elementos con clave "@id" del mapa interno de la lista de los valores de mDatasetConcepts
			for (String key: mDatasetConcepts.keySet()) {	// Recorrer el mapa mDatasetConcepts por la clave
				List<Map<String, String>> lGraphs = mDatasetConcepts.get(key);	// Crear una lista de graphs con el valor de cada entrada de  mDatasetConcepts
				for (Map<String, String> graph : lGraphs) {	// Recorrer los elementos de la lista
					if(graph.containsKey("@id"))		// Por cada elemento, mirar si existe una clave de valor "@id"
						listaIDs.add(graph.get("@id"));	// Guardar el valor en listaIDs
				}
			}	

			// Invocar al método getGraphValues() que se encargará de obtener de cada fichero JSON que hay en listaIDs los valores title y description 
			Map<String, Map<String,String>> mGraph = getGraphValues (listaIDs);

			// Generar un XML a partir de la información que hay en mGraph
			PrintStream fSalidaExamen= new PrintStream(args[4],"UTF-8");
			fSalidaExamen.println (SalidaXMLExamen.generar(mGraph));
			fSalidaExamen.close();


		}catch (IOException e){
			System.out.format("\nERROR AL GENERAR EL FICHERO XML: %s", e.getMessage());
			System.exit(1);				
		}

		System.exit(0);
	}



	/*
    *  @param: ListaIDs: ArrayList de String con el contenido de los @id de cada
	 * objetos del array @graph pertinentes
    *  @retun Mapa donde la clave es el @id de cada objeto del array @graph
	 * pertinente y el valor otro un mapa con los elementos indicados en graphs.xsd 
    */
	private static Map<String, Map<String,String>> getGraphValues ( List<String> listaIDs) throws InterruptedException {

		Map<String, Map<String,String>> mGraphValues =  new ConcurrentHashMap<String, Map<String,String>>();


		final int numDeNucleos = Runtime.getRuntime().availableProcessors();
		System.out.print ("\nLanzando hilos en los " + numDeNucleos + " núcleos disponibles de este ordenador ");

		// Crear un pool donde ejecutar los hilos. El pool tendrá un tamaño del nº de núcleos del ordenador
		// por lo que nunca podrá haber más hilos que ese número en ejecución simultánea.
		// Si se quiere hacer pruebas con un solo trabajador en ejecución, poner como argumento un 1. Irá mucho más lenta
		final ExecutorService ejecutor = Executors.newFixedThreadPool(numDeNucleos);

		final AtomicInteger numTrabajadoresTerminados = new AtomicInteger(0);
		int numTrabajadores=0;

		for (String json : listaIDs) {
			Runnable trabajador = new JSONGraphParser(json,mGraphValues, numTrabajadoresTerminados);
			ejecutor.execute(trabajador);
			System.out.print (".");
			numTrabajadores++;
		}

		System.out.println ("\nSe van a ejecutar "+numTrabajadores+" trabajadores en el pool. Esperar a que terminen");

		// Esperar a que terminen todos los trabajadores
		ejecutor.shutdown();	// Cerrar el ejecutor cuando termine el último trabajador
		// Cada 10 segundos mostrar cuantos trabajadores se han ejecutado
		while (!ejecutor.awaitTermination(10, TimeUnit.SECONDS)) {
			System.out.print("\nYa han terminado "+numTrabajadoresTerminados.get()+". Esperando a los "+(numTrabajadores-numTrabajadoresTerminados.get())+" que quedan ");
		}
		// Mostrar todos los trabajadores que se han ejecutado. Debe coincidir con los creados. Esta es una comprobación que no es imprescindible hacerla
		final int numTrabajadoresEjecutados=numTrabajadoresTerminados.get();
		if (numTrabajadores!=numTrabajadoresEjecutados) {
			System.err.println("\nAlgo ha salido mal. Se deberían haber ejecutado "+numTrabajadores+" pero se han ejecutado "+numTrabajadoresEjecutados);
			System.exit(1);
		} else
			System.out.println("\nYa han terminado los "+numTrabajadoresTerminados.get()+" trabajadores");			

		return mGraphValues;

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
				"Uso: " + thisClass.getEnclosingClass().getCanonicalName() + " <ficheroCatalogo> <códigoCategoría> <ficheroSalida> <ficheroGraphs>\n" +
						"donde:\n"+
						"\t ficheroCatalogo:\t path al fichero XML con el catálogo de datos\n" +
						"\t códigoCategoría:\t código de la categoría de la que se desea obtener datos\n" +
						"\t ficheroSalida:\t\t nombre del fichero XML de salida\n" +	
						"\t ficheroJSON:\t\t nombre del fichero JSON con las consultas xPath\n"+		/* P5 */
						"\t ficheroGraphs:\t\t nombre del fichero XML del examen\n"		/* Examen */
				);				
	}	


}
