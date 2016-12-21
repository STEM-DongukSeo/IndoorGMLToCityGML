package edu.pnu.util;

import java.util.List;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.index.strtree.NewGeometryItemDistance;
import com.vividsolutions.jts.index.strtree.STRtree;
import com.vividsolutions.jts.linearref.LinearLocation;
import com.vividsolutions.jts.linearref.LocationIndexedLine;
import com.vividsolutions.jts.operation.buffer.BufferOp;
import com.vividsolutions.jts.operation.buffer.BufferParameters;
;

public class JTSUtil {
	private static final PrecisionModel pm = new PrecisionModel(PrecisionModel.maximumPreciseValue);
	private static final GeometryFactory gf = new GeometryFactory(pm);
	
	public JTSUtil() {
		// TODO Auto-generated constructor stub
	}

	public static Point snapPointToLineStringByLIL(LineString line, Point point) {
		//new PrecisionModel(
		LocationIndexedLine lil = new LocationIndexedLine(line);
		LinearLocation here = lil.project(point.getCoordinate());
		Coordinate coord = lil.extractPoint(here, 0);
		Point p = gf.createPoint(coord);
		
		/*
		System.out.println("LIL isContains : " + line.contains(p));
		System.out.println("point : " + point.toText());
		System.out.println("extracted poitn : " + p.toText());
		*/
		
		return p;
	}
	
	public static Geometry getUnion(List<Polygon> polygons) {
		Polygon[] polygonArr = new Polygon[polygons.size()];
		for (int i = 0; i < polygons.size(); i++) {
			polygonArr[i] = polygons.get(i);
		}
		
		// create STRTree
		MultiPolygon multiPolygon = gf.createMultiPolygon(polygonArr);
		STRtree index = createSTRtree(multiPolygon);
		double buffer = getBufferDistance(index, multiPolygon);
		
		Geometry union = multiPolygon.buffer(buffer, 0, BufferParameters.CAP_SQUARE);
		//Geometry union = multiPolygon.buffer(buffer);
		if (!(union instanceof Polygon) && !(union instanceof MultiPolygon)) {
			throw new UnsupportedOperationException("The type of union is not Polygon " + union.getGeometryType());
		}
		
		return union;
	}
	
	private static STRtree createSTRtree(MultiPolygon multiPolygon) {
		STRtree index = new STRtree();
		
		for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
			Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
			index.insert(polygon.getEnvelopeInternal(), polygon);
		}
		
		return index;
	}
	
	private static double getBufferDistance(STRtree index, MultiPolygon multiPolygon) {
		// nearest와의 거리 중 최대값을 buffer로 사용
		double maxDistance = 0;
		for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
			Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
			Polygon nearset = (Polygon) index.nearestNeighbour(polygon.getEnvelopeInternal(), polygon, new NewGeometryItemDistance());
			
			double distance = polygon.distance(nearset);
			if (maxDistance < distance) {
				maxDistance = distance;
			}
		}
		
		return maxDistance;
	}
	
	public static MultiPolygon createMultiPolygon(List<Polygon> polygons) {
		Polygon[] polygonArr = polygons.toArray(new Polygon[polygons.size()]);
		return gf.createMultiPolygon(polygonArr);
	}
}
