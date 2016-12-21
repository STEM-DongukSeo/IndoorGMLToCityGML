package edu.pnu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.geotools.factory.GeoTools;
import org.geotools.factory.Hints;
import org.geotools.geometry.GeometryBuilder;
import org.geotools.geometry.iso.PositionFactoryImpl;
import org.geotools.geometry.iso.aggregate.AggregateFactoryImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.PrecisionModel;

public class GeometryUtil {
	private static final PrecisionModel pm = new PrecisionModel(PrecisionModel.maximumPreciseValue);
	private static final GeometryFactory gf = new GeometryFactory(pm);
	
	private static Hints hints = null;
	private static GeometryBuilder builder = null;
	private static PrimitiveFactoryImpl pf = null;
	private static PositionFactoryImpl positionFactory = null;
	private static AggregateFactoryImpl af = null;
	
	static {
		hints = GeoTools.getDefaultHints();
        hints.put(Hints.CRS, DefaultGeographicCRS.WGS84_3D);
        hints.put(Hints.GEOMETRY_VALIDATE, false);
        builder = new GeometryBuilder(hints);
        pf = (PrimitiveFactoryImpl) builder.getPrimitiveFactory();
        af = (AggregateFactoryImpl) builder.getAggregateFactory();
        positionFactory = (PositionFactoryImpl) builder.getPositionFactory();
	}
	
	public GeometryUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static Coordinate getJTSCoordinate(double[] coords) {		
		Coordinate coordinate = new Coordinate(coords[0], coords[1], coords[2]);
		
		return coordinate;
	}
	
	public static Point ISODirectPositionToJTSPoint(DirectPosition position) {
		Coordinate coordinate = getJTSCoordinate(position.getCoordinate());
		Point jtsPoint = gf.createPoint(coordinate);
		
		return jtsPoint;
	}
	
	public static Polygon ISOSurfaceToJTSPolygon(Surface surface) {
		SurfaceBoundary boundary = surface.getBoundary();
		Ring exterior = boundary.getExterior();
		List<DirectPosition> positions = ((RingImplUnsafe) exterior).asDirectPositions();
		
		Coordinate[] coords = new Coordinate[positions.size()];
		for (int i = 0; i < positions.size(); i++) {
			coords[i] = getJTSCoordinate(positions.get(i).getCoordinate());
		}
		
		return gf.createPolygon(coords);
	}
	
	public static DirectPosition JTSCoordinateToISODirectPosition(Coordinate coordinate) {
		double[] coords = new double[3];
		for (int i = 0; i < 3; i++) {
			coords[i] = coordinate.getOrdinate(i);
		}
		
		DirectPosition position = positionFactory.createDirectPosition(coords);
		
		return position;
	}
	
	public static DirectPosition JTSPointToISODirectPosition(Point jtsPoint) {		
		return JTSCoordinateToISODirectPosition(jtsPoint.getCoordinate());
	}
	
	public static Object UnionSurfaceToISO(com.vividsolutions.jts.geom.Geometry union) {
		if (union.getGeometryType().equalsIgnoreCase("Polygon")) {
			return JTSPolygonToISOSurface((Polygon) union);
		} else if (union.getGeometryType().equalsIgnoreCase("MultiPolygon")) {
			List<Surface> surfaces = new ArrayList<Surface>();
			MultiPolygon multiPolygon = (MultiPolygon) union;
			for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
				surfaces.add(JTSPolygonToISOSurface((Polygon) multiPolygon.getGeometryN(i)));
			}
			
			return surfaces;
		}		
		
		return null;
	}
	
	public static Surface JTSPolygonToISOSurface(Polygon jtsPolygon) {
		Coordinate[] coords = jtsPolygon.getCoordinates();
		
		List<DirectPosition> positions = new ArrayList<DirectPosition>();
		for (int i = 0; i < coords.length; i++) {
			positions.add(JTSCoordinateToISODirectPosition(coords[i]));
		}
		
		return pf.createSurfaceByDirectPositions(positions);		
	}
	
	public static LineString createJTSLineString(List<DirectPosition> positions) {
		Coordinate[] coordinates = new Coordinate[positions.size()];
		for (int i = 0; i < positions.size(); i++) {
			coordinates[i] = getJTSCoordinate(positions.get(i).getCoordinate());
		}
		
		LineString jtsLine = gf.createLineString(coordinates);
		return jtsLine;
	}
	
	public static DirectPosition createDirectPosition(double[] coords) {
		return positionFactory.createDirectPosition(coords);
	}
	
	public static MultiSurface createMultiSurface(Set<OrientableSurface> surfaces) {
		return af.createMultiSurface(surfaces);
	}
	
	public static Surface createSurface(List<DirectPosition> positions) {
		return pf.createSurfaceByDirectPositions(positions);
	}
	
	public static Surface createSurfaceByZ(Surface surface, double z) {
		SurfaceBoundary boundary = surface.getBoundary();
		Ring exterior = boundary.getExterior();
		List<DirectPosition> positions = ((RingImplUnsafe) exterior).asDirectPositions();
		
		for (DirectPosition position : positions) {
			position.setOrdinate(2, z);
		}
		
		Surface newSurface = pf.createSurfaceByDirectPositions(positions);		
		return newSurface;
	}
	
	public static List<Surface> createWallSurfaceByFloor(Surface floor, double z) {
		SurfaceBoundary boundary = floor.getBoundary();
		Ring exterior = boundary.getExterior();
		List<DirectPosition> positions = ((RingImplUnsafe) exterior).asDirectPositions();
		
		List<Surface> wallSurfaces = new ArrayList<Surface>();
		for (int i = 0; i < positions.size() - 1; i++) {
			DirectPosition startPosition = positions.get(i);
			DirectPosition endPosition = positions.get(i + 1);
			
			double[] midCoords1 = startPosition.getCoordinate();
			double[] midCoords2 = endPosition.getCoordinate();			
			midCoords1[2] = z;
			midCoords2[2] = z;
			
			DirectPosition midPosition1 = positionFactory.createDirectPosition(midCoords1);
			DirectPosition midPosition2 = positionFactory.createDirectPosition(midCoords2);
			
			List<DirectPosition> wallPositions = new ArrayList<DirectPosition>();
			wallPositions.add(startPosition);
			wallPositions.add(midPosition1);
			wallPositions.add(midPosition2);
			wallPositions.add(endPosition);
			wallPositions.add(startPosition);
			
			Surface wallSurface = pf.createSurfaceByDirectPositions(wallPositions);
			wallSurfaces.add(wallSurface);
		}
		
		return wallSurfaces;
	}
	
	public static Solid createSolid(List<OrientableSurface> surfaces) {
		Shell exterior = pf.createShell(surfaces);
		List<Shell> interiors = new ArrayList<Shell>();
		SolidBoundary boundary = pf.createSolidBoundary(exterior, interiors);
		
		return pf.createSolid(boundary);
	}
}
