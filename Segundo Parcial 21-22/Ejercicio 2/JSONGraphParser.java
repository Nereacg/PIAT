package piat.opendatasearch;

import java.util.Map;


/* En esta clase se comportará como un hilo */

public class JSONGraphParser implements Runnable {
	private String fichero;
	private Map<String, Map<String,String>> mGraphValues;
	private String nombreHilo;
	
	public JSONGraphParser (String fichero, Map<String, Map<String, String>> mGraphValues) {
		this.fichero=fichero;
		this.mGraphValues=mGraphValues;
	}

	
	@Override
	public void run (){
		Thread.currentThread().setName("JSON " + fichero);
		nombreHilo="["+Thread.currentThread().getName()+"] ";

		// TODO: Procesar el fichero para extraer las propiedades pertinentes y guardarlas en mGraphValues

	}
	
}