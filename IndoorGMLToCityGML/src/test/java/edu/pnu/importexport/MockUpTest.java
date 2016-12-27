package edu.pnu.importexport;

import java.io.File;
import java.util.Locale;

import net.opengis.citygml.building.Building;

import org.apache.ibatis.io.Resources;
import org.junit.Before;
import org.junit.Test;

public class MockUpTest {
	@Before
	public void setUp() throws Exception {
		Locale.setDefault(Locale.ENGLISH);
	}
	
	@Test
	public void CoreMockUpsTest() throws Exception {
		IndoorGMLImporter importer = new IndoorGMLImporter();
		
		//String coreResource = "example/LWM_AVENUEL_1F.gml";
		//String coreResource = "example/SAMPLE_DATA_AVENUEL1F2F_3D.gml";
		//String coreResource = "example/SAMPLE_DATA_LWM_3D.gml";
		//String coreResource = "example/SAMPLE_LWM_AVENUEL.gml";
		String coreResource = "example/LWM.gml";
				
		File coreFile = Resources.getResourceAsFile(coreResource);
		importer.importIndoorGML("Core", coreFile.getAbsolutePath());
		
		Building building = importer.getBuilding();
		CityGMLExporter.exportCityGML(building, "result_building_LWM.gml");
		
		//exporter.exportIndoorGMLCore(props, "Core", "result_core.gml");
	}
	
	//@Test
	public void NaviMockUpsTest() throws Exception {
		IndoorGMLImporter importer = new IndoorGMLImporter();
		
		//IndoorGMLKairosExporter exporter = new IndoorGMLKairosExporter();
		//IndoorGMLKairosManager manager = new IndoorGMLKairosManager();
		//manager.deleteSchema(props);
		//manager.createSchema(props);
		
		String naviResource = "test/indoorgml_navi_mockup.gml";
		
		File naviFile = Resources.getResourceAsFile(naviResource);
		importer.importIndoorGML("Navi", naviFile.getAbsolutePath());
		//exporter.exportIndoorGMLNavi(props, "Navi", "result_navi.gml");
	}
}
