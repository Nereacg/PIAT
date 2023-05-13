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
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
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
	public static List<Propiedad> evaluar (String ficheroXML) throws IOException, XPathExpressionException {
		// TODO: 
		// Realiza las 4 consultas XPath al documento XML de entrada que se indican en el enunciado en el apartado "3.2 Búsqueda de información y generación del documento de resultados."

		List<Propiedad> lPropiedad = new ArrayList<Propiedad>();
		try {		
			/*Abrir fichero xml*/	
			InputSource inputSource = new InputSource(ficheroXML);					// Creación de la fuente de datos
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();	// Creación de la factoria
			//factory.setNamespaceAware(true);										// Configuración de la factoria para que trabaje con espacios de nombres
			DocumentBuilder builder = factory.newDocumentBuilder();						 	
			Document inputDoc = builder.parse (inputSource);	
			//se crea el objeto XPATH que evaluará las expresiones
			XPath xPath = XPathFactory.newInstance().newXPath();
			// Cada consulta devolverá una información que se añadirá a la colección List <Propiedad>, que es la que devuelve este método
			/*Rutas consultas*/
			//a) Contenido textual del elemento <query>.
			final String xPathQuery="//summary/query//text()";
			//b) Número de elementos <resource> hijos de <resources>.
			final String xPathCountResource="count(//resources/resource)";
			//c) Contenido de cada uno de los elementos <eventLocation>, descendientes de <resource>. Si su contenido está vacío no se tendrá en cuenta. Si una ubicación está repetida sólo se añadirá una vez al documento de salida.
			final String xPathEventLocation="//resources/resource/location/eventLocation//text()";
			//d) Por cada elemento <dataset>, hijo de <datasets>, número de elementos <resource> cuyo atributo id es igual al atributo id del elemento <dataset>
			final String xPathDatasetID="//datasets/dataset[@id=//resources/resource/@id]/@id"; //me coge todos los id de dataset que aparecen también en algún resoruce		
			Propiedad p;		
			p = new Propiedad("query", (String) xPath.evaluate(xPathQuery, inputDoc, XPathConstants.STRING)); //¿¿¿¿estará bien crear tantas instancias de propiedad?
			lPropiedad.add(p);	
			Double numResources = (Double) xPath.evaluate(xPathCountResource, inputDoc, XPathConstants.NUMBER);
			String resultado= String.valueOf(numResources.intValue());	// Convertir a int para quitar decimales, y luego a String
			p=new Propiedad ("numResources", resultado);
			lPropiedad.add(p);	
			NodeList lEventLocation= (NodeList) xPath.evaluate(xPathEventLocation, inputDoc, XPathConstants.NODESET);
			for(int i=0;i< lEventLocation.getLength(); i++) {
				p = new Propiedad("eventLocation", lEventLocation.item(i).getTextContent()); //ir creando cada propiedad título
				lPropiedad.add(p);
			}	
			NodeList lResource= (NodeList) xPath.evaluate(xPathDatasetID, inputDoc, XPathConstants.NODESET);
			for(int i=0;i< lResource.getLength(); i++) {
				String id = lResource.item(i).getTextContent(); //obtener los id de dataset que también apracen en resource
				p = new Propiedad("id", id); //Propiedad con el id del dataset
				lPropiedad.add(p);
				String sPathCountResourceId = "count(//resources/resource[@id=\""+id+"\"])";//Contar las veces que aparece ese id en resources
				p = new Propiedad("num", (String) xPath.evaluate(sPathCountResourceId, inputDoc, XPathConstants.STRING));
				lPropiedad.add(p);
			}		
		}catch (Throwable t) {  t.printStackTrace();  }
		return lPropiedad;		
	}//del (main)

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
