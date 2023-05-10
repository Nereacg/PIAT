package piat.opendatasearch;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Clase para evaluar las expresiones XPath
 * Contiene un método estático, llamado evaluar() que se encarga de realizar las consultas XPath al fichero XML que se le pasa como parámetro 
 */
public class XPATH_Evaluador{

	/**
	 * Método que se encarga de evaluar las expresiones XPath sobre el fichero XML generado en la práctica 4
	 * @param	ficheroXML	Fichero XML a evaluar
	 * @return	Una lista con la propiedad resultante de evaluar cada expresión XPath
	 * @throws	IOException
	 * @throws	XPathExpressionException 
	 * @throws	ParserConfigurationException 
	 * @throws	SAXException 
	 */
	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException, ParserConfigurationException, SAXException {
		// TODO: 
		// Realiza las 4 consultas XPath al documento XML de entrada que se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."
		// Cada consulta devolverá una información que se añadirá a la colección List <Propiedad>, que es la que devuelve este método
		// Una consulta puede devolver una propiedad o varias
		List<Propiedad> list = new ArrayList<Propiedad>();
		Propiedad propiedad;
		String resultado;
		//En primer lugar se crea el arbol DOM a partir del fichero XML fuente
		final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		final DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(ficheroXML);
		//se crea el objeto XPATH que evaluará las expresiones
		XPath xPath = XPathFactory.newInstance().newXPath();
		//Se evalua la primera expresion query
		String expresionXPATHquery = "//summary/query//text()";
		String query = (String) xPath.evaluate(expresionXPATHquery, doc, XPathConstants.STRING);
		propiedad = new Propiedad("query", query);
		list.add(propiedad);
		//Se evalua la segunda expresion numeroResources
		String expresionXPATHnumeroResources = "count(//resource)";
		Double numeroResources = (Double) xPath.evaluate(expresionXPATHnumeroResources, doc, XPathConstants.NUMBER);
		resultado = numeroResources.toString();
		propiedad = new Propiedad("numeroResources", resultado);
		list.add(propiedad);
		//Se evalua la tercera expresion infDatasets
		String expresionXPATHinfDatasets = "//resource";
		NodeList infDatasets = (NodeList) xPath.evaluate(expresionXPATHinfDatasets, doc, XPathConstants.NODESET);
		List<String> listaInfDatasets = new ArrayList<String>();
		for(int i=0; i<infDatasets.getLength(); i++){
			Node nodo = infDatasets.item(i);
			String infDataset = nodo.getAttributes().getNamedItem("dataset").getNodeValue();
			boolean add = true;
			if(!listaInfDatasets.contains(infDataset)){
				listaInfDatasets.add(infDataset);
			}
			else
				add = false;
			propiedad = new Propiedad(infDataset, Integer.toString(listaInfDatasets.size()));
			if(add && !infDataset.equals(""))
				list.add(propiedad);
		}
		List<String> listaUbicaciones = new ArrayList<String>();
		for(int i=0; i<infDatasets.getLength(); i++){
			Node nodo = infDatasets.item(i);
			String ubicacion = nodo.getAttributes().getNamedItem("eventLocation").getNodeValue();
			boolean add = true;
			if(!listaUbicaciones.contains(ubicacion)){
				listaUbicaciones.add(ubicacion);
			}
			else
				add = false;
			propiedad = new Propiedad("ubicacion", ubicacion);
			if(add && !ubicacion.equals(""))
				list.add(propiedad);
		}
		System.out.println (listaInfDatasets.toString());
		System.out.println (listaUbicaciones.toString());
		return list;
	}
	/**
	 * Esta clase interna define una propiedad equivalente a "nombre":"valor" en JSON
	 */
	public static class Propiedad {
		public final String nombre;
		public final String valor;

		public Propiedad (String nombre, String valor){
			this.nombre=nombre;
			this.valor=valor;		
		}

		@Override
		public String toString() {
			return this.nombre+": "+this.valor;

		}

	} //Fin de la clase interna Propiedad

} //Fin de la clase XPATH_Evaluador
