package edu.pnu.test;

import org.junit.Test;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class jtsTest {
	private static final PrecisionModel pm = new PrecisionModel(PrecisionModel.maximumPreciseValue);
	private static final GeometryFactory gf = new GeometryFactory(pm);
	
	@Test
	public void unionTest() {
		Coordinate[] coords1 = new Coordinate[5];
		coords1[0] = new Coordinate(0, 0);
		coords1[1] = new Coordinate(5, 0);
		coords1[2] = new Coordinate(5, 5);
		coords1[3] = new Coordinate(0, 5);
		coords1[4] = coords1[0];
		
		Coordinate[] coords2 = new Coordinate[5];
		coords2[0] = new Coordinate(2, 0);
		coords2[1] = new Coordinate(7, 0);
		coords2[2] = new Coordinate(7, 5);
		coords2[3] = new Coordinate(2, 5);
		coords2[4] = coords2[0];
		
		Coordinate[] coords3 = new Coordinate[5];
		coords3[0] = new Coordinate(6, 0);
		coords3[1] = new Coordinate(10, 0);
		coords3[2] = new Coordinate(10, 5);
		coords3[3] = new Coordinate(6, 5);
		coords3[4] = coords3[0];
		
		Coordinate[] coords4 = new Coordinate[5];
		coords4[0] = new Coordinate(5, 6);
		coords4[1] = new Coordinate(10, 6);
		coords4[2] = new Coordinate(10, 11);
		coords4[3] = new Coordinate(5, 11);
		coords4[4] = coords4[0];
		
		Polygon polygon1 = gf.createPolygon(coords1);
		Polygon polygon2 = gf.createPolygon(coords2);
		Polygon polygon3 = gf.createPolygon(coords3);
		Polygon polygon4 = gf.createPolygon(coords4);
		
		Geometry[] geometries = new Geometry[3];
		geometries[0] = polygon1;
		geometries[1] = polygon3;
		geometries[2] = polygon4;
		
		GeometryCollection collection = gf.createGeometryCollection(geometries);
		Geometry buffer = collection.buffer(1.0);
		System.out.println(buffer.toText());
		System.out.println(collection.toText());
	}
}
