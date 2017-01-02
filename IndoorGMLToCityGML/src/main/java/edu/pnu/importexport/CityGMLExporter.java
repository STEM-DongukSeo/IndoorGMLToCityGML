/**
 * 
 */
package edu.pnu.importexport;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import net.opengis.citygml.building.Building;
import net.opengis.citygml.v_2_0.CityModelType;
import net.opengis.citygml.v_2_0.ObjectFactory;
import edu.pnu.importexport.retrieve.VOJAXBConvertUtil;

/**
 * @author hgryoo
 *
 */
public class CityGMLExporter {
	
	public static void exportCityGML(Building building, String filePath) throws Exception {
		
		CityModelType cityModelType = VOJAXBConvertUtil.createCityModelType(building);
		
		marshalCityModel(filePath, cityModelType);
	}
	
	public static void marshalCityModel(String path, CityModelType cityModel) throws JAXBException, IOException {
		JAXBContext context = JAXBContext.newInstance(
				"net.opengis.citygml.v_2_0"
				+ ":net.opengis.citygml.appearance.v_2_0"
				+ ":net.opengis.citygml.bridge.v_2_0"
				+ ":net.opengis.citygml.building.v_2_0"
				+ ":net.opengis.citygml.cityfurniture.v_2_0"
				+ ":net.opengis.citygml.cityobjectgroup.v_2_0"
				+ ":net.opengis.citygml.generics.v_2_0"
				+ ":net.opengis.citygml.landUse.v_2_0"
				+ ":net.opengis.citygml.relief.v_2_0"
				+ ":net.opengis.citygml.texturedsurface.v_2_0"
				+ ":net.opengis.citygml.transportation.v_2_0"
				+ ":net.opengis.citygml.tunnel.v_2_0"
				+ ":net.opengis.citygml.vegetation.v_2_0"
				+ ":net.opengis.citygml.waterbody.v_2_0"
				+ ":oasis.names.tc.ciq.xsdschema.xal"
				+ ":net.opengis.gml.v_3_1_1"
				+ ":org.w3.smil.v_2_0"
				+ ":org.w3.smil.v_2_0.language"
				+ ":org.w3.xlink"
		);
		
		File output = new File(path);
		
		ObjectFactory objectFactory = new ObjectFactory();
		JAXBElement<CityModelType> jCityModel = objectFactory.createCityModel(cityModel);
		
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, 
				"http://www.opengis.net/citygml/profiles/base/2.0 http://schemas.opengis.net/citygml/profiles/base/2.0/CityGML.xsd");
		marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
		
		//marshaller.marshal( jCityModel, output );
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.newDocument();
			
			marshaller.marshal(jCityModel, document);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer t = tf.newTransformer();
			t.setOutputProperty(OutputKeys.INDENT,"yes");
			t.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			DOMSource source = new DOMSource(document);
			StreamResult streamResult = new StreamResult(output);
			t.transform(source, streamResult);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
