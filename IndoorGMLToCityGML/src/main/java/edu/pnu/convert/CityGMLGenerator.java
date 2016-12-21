package edu.pnu.convert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.opengis.citygml.building.BoundarySurface;
import net.opengis.citygml.building.Building;
import net.opengis.citygml.building.IntBuildingInstallation;
import net.opengis.citygml.building.Opening;
import net.opengis.citygml.building.Room;
import net.opengis.indoorgml.v_1_0.vo.core.CellSpace;
import net.opengis.indoorgml.v_1_0.vo.core.CellSpaceBoundary;
import net.opengis.indoorgml.v_1_0.vo.core.IndoorFeatures;
import net.opengis.indoorgml.v_1_0.vo.core.State;
import net.opengis.indoorgml.v_1_0.vo.core.Transition;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOSolid;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOSurface;

import org.geotools.geometry.iso.aggregate.AggregateFactoryImpl;
import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;

import edu.pnu.util.GeometryUtil;
import edu.pnu.util.JTSUtil;

public class CityGMLGenerator {
	private IndoorFeatures indoorFeatures;	
	private List<Double> floorZOrdinates;
	private Map<Double, List<CellSpace>> floorCellSpaceMap;
	
	private PrimitiveFactoryImpl pf;
	private AggregateFactoryImpl af;
	
	public CityGMLGenerator(IndoorFeatures indoorFeatures, List<Double> floorZOrdinates, Map<Double, List<CellSpace>> floorCellSpaceMap,
			PrimitiveFactoryImpl pf, AggregateFactoryImpl af) {
		this.indoorFeatures = indoorFeatures;
		this.floorZOrdinates = floorZOrdinates;
		this.floorCellSpaceMap = floorCellSpaceMap;
		
		this.pf = pf;
		this.af = af;
	}

	public Building generate() {
		return createBuilding();
	}
	
	private Building createBuilding() {
		Building building = new Building();
		building.setGmlID("Lotte World Mall");
		building.setType("Building");
		
		// createBuildng MultiSurface - Union
		VOMultiSurface lod1MultiSurface = new VOMultiSurface();
		building.setLod1MultiSurface(lod1MultiSurface);		
		Set<OrientableSurface> surfaces = new HashSet<OrientableSurface>();
		
		List<com.vividsolutions.jts.geom.Polygon> floorUnions = new ArrayList<com.vividsolutions.jts.geom.Polygon>();
		double heightMax = 0;
		for (int i = 0; i < floorZOrdinates.size(); i++) {
			Double z = floorZOrdinates.get(i);
			List<CellSpace> floorCellSpaces = floorCellSpaceMap.get(z);
			double height = floorCellSpaces.get(0).getGeometry3D().getGeometry().getEnvelope().getUpperCorner().getOrdinate(2);
			if (heightMax < height) {
				heightMax = height;
			}
			
			com.vividsolutions.jts.geom.Geometry floorSurfaceUnion = getFloorSurfaceUnion(floorCellSpaces);
			if (floorSurfaceUnion.getGeometryType().equalsIgnoreCase("Polygon")) {
				floorUnions.add((com.vividsolutions.jts.geom.Polygon) floorSurfaceUnion);
			} else if (floorSurfaceUnion.getGeometryType().equalsIgnoreCase("MultiPolygon")) {
				com.vividsolutions.jts.geom.MultiPolygon multiPolygon = (MultiPolygon) floorSurfaceUnion;
				com.vividsolutions.jts.geom.Geometry tempUnion = multiPolygon.union();
				if (tempUnion.getGeometryType().equalsIgnoreCase("Polygon")) {
					floorUnions.add((com.vividsolutions.jts.geom.Polygon) tempUnion);
				}
			}
			
			
			
			///
			/*
			Object unionSurfaceISO = GeometryUtil.UnionSurfaceToISO(floorSurfaceUnion);
			
			Surface union = null;
			List<Surface> unions = null;
			boolean isUnionSurface = false;
			if (unionSurfaceISO instanceof Surface) {
				union = (Surface) unionSurfaceISO;
				isUnionSurface = true;
			} else if (unionSurfaceISO instanceof List) {
				unions = (List<Surface>) unionSurfaceISO;
				isUnionSurface = false;
			} else if (unionSurfaceISO == null){
				throw new UnsupportedOperationException("The UnionSurface is null");
			}			
			
			if (i == 0) {
				if (isUnionSurface) {
					Surface newUnion = GeometryUtil.createSurfaceByZ(union, z);
					surfaces.add(newUnion);
				} else if (!isUnionSurface) {
					throw new UnsupportedOperationException("GroundFloor cannot be multiSurface");
				}
			}
			if (i == floorZOrdinates.size() - 1) {
				if (isUnionSurface) {
					// z값 변경해서 추가한다.
					Surface newUnion = GeometryUtil.createSurfaceByZ(union, height);
					surfaces.add(newUnion);
				} else if (!isUnionSurface) {
					throw new UnsupportedOperationException("RoofFloor cannot be multiSurface");
				}
			}
			
			if (isUnionSurface) {
				Surface newUnion = GeometryUtil.createSurfaceByZ(union, z);
				List<Surface> wallSurfaces = GeometryUtil.createWallSurfaceByFloor(newUnion, height);
				surfaces.addAll(wallSurfaces);
			} else if (!isUnionSurface) {
				for (Surface surface : unions) {
					Surface newSurface = GeometryUtil.createSurfaceByZ(surface, z);
					List<Surface> wallSurfaces = GeometryUtil.createWallSurfaceByFloor(newSurface, height);
					surfaces.addAll(wallSurfaces);
				}
			}
			*/
		}
		com.vividsolutions.jts.geom.MultiPolygon baseWholeFloorMP = JTSUtil.createMultiPolygon(floorUnions);
		com.vividsolutions.jts.geom.Geometry baseWholeFloor = baseWholeFloorMP.union();
		if (!baseWholeFloor.getGeometryType().equalsIgnoreCase("Polygon")) {
			throw new UnsupportedOperationException("base floor union is not Polygon");
		}
		Surface unionSurfaceISO = (Surface) GeometryUtil.UnionSurfaceToISO(baseWholeFloor);		
		
		Surface newGround = GeometryUtil.createSurfaceByZ(unionSurfaceISO, floorZOrdinates.get(0).doubleValue());
		Surface newRoof = GeometryUtil.createSurfaceByZ(unionSurfaceISO, heightMax);
		List<Surface> newWallSurfaces = GeometryUtil.createWallSurfaceByFloor(newGround, heightMax);
		surfaces.add(newGround);
		surfaces.add(newRoof);
		surfaces.addAll(newWallSurfaces);
		
		MultiSurface multiSurface = GeometryUtil.createMultiSurface(surfaces);
		lod1MultiSurface.setGeometry(multiSurface);
				
		
		// create Room Solid, BoundarySurface MultiSurface
		List<Room> rooms = new ArrayList<Room>();
		for (Double z : floorZOrdinates) {
			List<CellSpace> floorCellSpaces = floorCellSpaceMap.get(z);

			for (CellSpace cellSpace : floorCellSpaces) {
				if (!cellSpace.getEstimatedType().equalsIgnoreCase("ROOM") &&
						!cellSpace.getEstimatedType().equalsIgnoreCase("STAIR")) continue;
				
				Room room = createRoom(cellSpace);
				List<IntBuildingInstallation> intBuildingInstallations = new ArrayList<IntBuildingInstallation>();
				room.setIntBuildingInstallations(intBuildingInstallations);
				
				String clazz = "1000";
				String func = "1000";
				if (cellSpace.getEstimatedType().equalsIgnoreCase("STAIR")) {
					// 계단을 나타내는 기하 생성
					IntBuildingInstallation stair = createStair(cellSpace);
					if (stair != null) {
						stair.setClazz("8000");
						stair.setClassCodeSpace("http://www.sig3d.org/codelists/standard/building/2.0/IntBuildingInstallation_class.xml");
						
						stair.setFunc("8020");
						stair.setFuncCodeSpace("http://www.sig3d.org/codelists/standard/building/2.0/IntBuildingInstallation_function.xml");
						intBuildingInstallations.add(stair);
					}
					func = "1060";
				}
				
				if (room != null) {
					room.setClazz(clazz); // habitation
					room.setClassCodeSpace("http://www.sig3d.org/codelists/standard/building/2.0/Room_class.xml");
					
					room.setFunc(func);
					room.setFuncCodeSpace("http://www.sig3d.org/codelists/standard/building/2.0/Room_function.xml");
					
					rooms.add(room);
				}
			}
		}
		building.setRooms(rooms);
		
		return building;
	}
	
	private Room createRoom(CellSpace cellSpace) {
		Map<Surface, List<CellSpaceBoundary>> facetBoundaryMap = cellSpace.getFacetBoundaryMap();
		if (facetBoundaryMap == null) {
			facetBoundaryMap = new HashMap<Surface, List<CellSpaceBoundary>>();
		}
		
		Room room = new Room();
		room.setGmlID(cellSpace.getGmlID());
		room.setGmlName(cellSpace.getName());
		//room.setDescription(cellSpace.getDescription());		
		String usage = cellSpace.getEstimatedType();
		//if (usage.equals("WALL") return null;		
		room.setUsage(usage);
		
		
		List<Surface> facets = cellSpace.getFacets();
		List<BoundarySurface> boundarySurfaces = new ArrayList<BoundarySurface>();
		List<OrientableSurface> newFacets = new ArrayList<OrientableSurface>();
		
		BoundarySurface interiorWallSurface = new BoundarySurface();
		interiorWallSurface.setBoundaryType("INTERIORWALLSURFACE");
		
		for (int i = 0; i < facets.size(); i++) {
			Surface facet = facets.get(i);
			try {
				if (facetBoundaryMap.containsKey(facet)) {
					List<CellSpaceBoundary> boundaryList = facetBoundaryMap.get(facet);
					if (boundaryList.isEmpty()) {
						throw new UnsupportedOperationException("boundaryList is empty");					
					} else if (boundaryList.size() == 1
							&& boundaryList.get(0).getEstimatedType().equalsIgnoreCase("VIRTUALWALL")) {
						BoundarySurface boundarySurface = createBoundarySurface(null, "CLOSURESURFACE", facet, true);					
						boundarySurfaces.add(boundarySurface);
						
						newFacets.add(facet);
					} else {
						// 일단 옆면에 문이 있는 경우에 대해서만
						// 벽면에 문을 뚫어야한다.
						// 벽면 Surface를 구성하는 점들 중에서 바닥쪽에 있는 두 점(p1, p2) 사이에
						// 문 Surface의 바닥쪽 점(q1, q2, q3, q4)들을 추가 해야한다.
						// p1, p2를 연결하는 선분과 문의 점(q1, q2)이 최단거리인 점(r1, r2)를 찾는다.
						// p1, r1, r3, r4, r2, p2순으로 되게 만든다. r3, r4는 r1, r2와 x, y가 같으면서 z는 문의 높이
						// 이렇게해도 만들어지지 않는 문은 일단 넘어간다.
						Surface refinementFacet = facet;
						List<VOSurface> doorSurfaces = new ArrayList<VOSurface>();
						for (CellSpaceBoundary boundary : boundaryList) {
							if (!boundary.getEstimatedType().equalsIgnoreCase("DOOR")) {
								System.out.println("estimated type of boundary is not door : " + boundary.getEstimatedType());
								continue;
							}						
							
							doorSurfaces.add(boundary.getGeometry3D());
						}
						List<Surface> refinementFacets = refinementSurface(refinementFacet, doorSurfaces);
						
						// 구멍뚫린 벽면으로 InteriorWallSurface 생성
						interiorWallSurface = createBoundarySurface(interiorWallSurface, "INTERIORWALLSURFACE", refinementFacets.get(0), true);
						// Opening 생성
						List<Opening> openings = interiorWallSurface.getOpenings();
						if (openings == null) {
							openings = new ArrayList<Opening>();
							interiorWallSurface.setOpenings(openings);
						}
						for (int j = 1 ; j < refinementFacets.size(); j++) {
							Surface doorSurface = refinementFacets.get(j);
							Opening opening = createOpening("Door", doorSurface);
							openings.add(opening);
						}

						if (!boundarySurfaces.contains(interiorWallSurface)) {
							boundarySurfaces.add(interiorWallSurface);
						}
						
						newFacets.add(reverseSurface(refinementFacets.get(0)));
					}
				} else {
					if (i == 0) { // ceiling
						BoundarySurface boundarySurface = createBoundarySurface(null, "CEILINGSURFACE", facet, true);
						boundarySurfaces.add(boundarySurface);
					} else if (i == 1) { // floor
						BoundarySurface boundarySurface = createBoundarySurface(null, "FLOORSURFACE", facet, true);
						boundarySurfaces.add(boundarySurface);
					} else { // interiorWall
						interiorWallSurface = createBoundarySurface(interiorWallSurface, "INTERIORWALLSURFACE", facet, true);
						if (!boundarySurfaces.contains(interiorWallSurface)) {
							boundarySurfaces.add(interiorWallSurface);
						}
					}
					newFacets.add(facet);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		room.setBoundarySurfaces(boundarySurfaces);
		
		// Solid 생성
		VOSolid originSolid =  cellSpace.getGeometry3D();		
		VOSolid lod4Solid = new VOSolid();
		Solid newSolid = GeometryUtil.createSolid(newFacets);
		
		lod4Solid.setGmlId(originSolid.getGmlId());
		lod4Solid.setGeometry(newSolid);		
		room.setLod4Solid(lod4Solid);
				
		return room;
	}
	
	private Opening createOpening(String type, Surface surface) {
		Opening opening = new Opening();
		opening.setOpeningType(type);
		
		VOMultiSurface lod4MultiSurface = new VOMultiSurface();
		Set<OrientableSurface> surfaces = new HashSet<OrientableSurface>();
		
		surfaces.add(surface);
		MultiSurface multiSurface = GeometryUtil.createMultiSurface(surfaces);
		lod4MultiSurface.setGeometry(multiSurface);
		opening.setLod4MultiSurface(lod4MultiSurface);		
		
		return opening;
	}
		
	private BoundarySurface createBoundarySurface(BoundarySurface boundarySurface, String type, Surface facet, boolean isReverse) {
		if (boundarySurface == null) {
			boundarySurface = new BoundarySurface();
			boundarySurface.setBoundaryType(type);
		} else if (!boundarySurface.getBoundaryType().equals(type)) {
			throw new UnsupportedOperationException("The type of BoundarySurface is not equal");
		}
		
		if (isReverse) {
			facet = reverseSurface(facet);
		}
		
		VOMultiSurface voMultiSurface = boundarySurface.getLod4MultiSurface();
		if (voMultiSurface == null) {
			voMultiSurface = new VOMultiSurface();
			boundarySurface.setLod4MultiSurface(voMultiSurface);
		}
		
		MultiSurface multiSurface = voMultiSurface.getGeometry();
		Set<OrientableSurface> surfaces = null;
		if (multiSurface == null) {
			surfaces = new HashSet<OrientableSurface>();
		} else {
			surfaces = multiSurface.getElements();
		}
		surfaces.add(facet);
		
		multiSurface = GeometryUtil.createMultiSurface(surfaces);
		boundarySurface.getLod4MultiSurface().setGeometry(multiSurface);
		
		return boundarySurface;
	}
	
	private IntBuildingInstallation createStair(CellSpace cellSpace) {
		IntBuildingInstallation stair = new IntBuildingInstallation();
		VOMultiSurface lod4Geometry = new VOMultiSurface();
		stair.setLod4Geometry(lod4Geometry);
		
		List<Surface> facets = cellSpace.getFacets();
		Set<OrientableSurface> stairSurfaces = new HashSet<OrientableSurface>();
		stairSurfaces.add(facets.get(1)); // sequence of facets : ceiling, floor, interiorWall
		
		Ring exterior = facets.get(1).getBoundary().getExterior();
		List<DirectPosition> exteriorPositions = ((RingImplUnsafe) exterior).asDirectPositions();
		if (exteriorPositions.size() != 5) {
			System.out.println("Only consider the case that floor Surface consist of 5 points");
			return null;
		}
		
		// 계단과 복도(방)을 연결하는 Transition을 찾는다.
		ArrayList<Transition> transitions = cellSpace.getDuality().getConnects();
		Transition baseTransition = null;
		for (Transition transition : transitions) {
			ArrayList<State> states = transition.getConnects();
			State state1 = states.get(0);
			State state2 = states.get(1);
			
			double z1 = state1.getGeometry().getGeometry().getDirectPosition().getOrdinate(2);
			double z2 = state2.getGeometry().getGeometry().getDirectPosition().getOrdinate(2);
			
			if (z1 != z2) continue;
			baseTransition = transition;
		}
		
		// Transition과 intersects하는 벽면 Surface를 찾는다.
		Surface baseSurface = null;
		if (baseTransition == null) {
			baseSurface = facets.get(2);
		} else {
			for (int i = 2; i < facets.size(); i++) {
				Surface facet = facets.get(i);
				if (facet.intersects(baseTransition.getGeometry().getGeometry())) {
					baseSurface = facet;
				}
			}
			if (baseSurface == null) {
				throw new UnsupportedOperationException("baseSurface is null");
			}
		}
		
		List<DirectPosition> floorPositions = getFloorPositions(facets.get(1), baseSurface);
		
		Envelope envelope = cellSpace.getGeometry3D().getGeometry().getEnvelope();
		double floorHeight = envelope.getLowerCorner().getOrdinate(2);
		double ceilingHeight = envelope.getUpperCorner().getOrdinate(2);
		double midHeight = (floorHeight + ceilingHeight) / 2; 
				
		double x11 = floorPositions.get(0).getOrdinate(0);
		double x12 = floorPositions.get(1).getOrdinate(0);
		double y11 = floorPositions.get(0).getOrdinate(1);
		double y12 = floorPositions.get(1).getOrdinate(1);
		double internalX1 = (x11 * 2 + x12 * 8) / 10;
		double internalY1 = (y11 * 2 + y12 * 8) / 10;
		
		double x21 = (floorPositions.get(0).getOrdinate(0) + floorPositions.get(3).getOrdinate(0)) / 2;
		double x22 = (floorPositions.get(1).getOrdinate(0) + floorPositions.get(2).getOrdinate(0)) / 2;
		double y21 = (floorPositions.get(0).getOrdinate(1) + floorPositions.get(3).getOrdinate(1)) / 2;
		double y22 = (floorPositions.get(1).getOrdinate(1) + floorPositions.get(2).getOrdinate(1)) / 2;
		double internalX2 = (x21 * 2 + x22 * 8) / 10;
		double internalY2 = (y21 * 2 + y22 * 8) / 10;
		
		double x31 = floorPositions.get(3).getOrdinate(0);
		double x32 = floorPositions.get(2).getOrdinate(0);
		double y31 = floorPositions.get(3).getOrdinate(1);
		double y32 = floorPositions.get(2).getOrdinate(1);
		double internalX3 = (x31 * 2 + x32 * 8) / 10;
		double internalY3 = (y31 * 2 + y32 * 8) / 10;
		
		double internalX4 = (x11 * 8 + x12 * 2) / 10;
		double internalY4 = (y11 * 8 + y12 * 2) / 10;
		double internalX5 = (x21 * 8 + x22 * 2) / 10;
		double internalY5 = (y21 * 8 + y22 * 2) / 10;
		double internalX6 = (x31 * 8 + x32 * 2) / 10;
		double internalY6 = (y31 * 8 + y32 * 2) / 10;

		double[] coords11 = floorPositions.get(0).getCoordinate();
		double[] coords12 = new double[3];
		coords12[0] = internalX1;
		coords12[1] = internalY1;
		coords12[2] = midHeight;
		double[] coords13 = floorPositions.get(1).getCoordinate();
		coords13[2] = midHeight;
		double[] coords14 = floorPositions.get(1).getCoordinate();		
		coords14[2] = (floorHeight * 2 + midHeight * 8) / 10;
		/*
		double[] coords15 = coords12;
		coords15[2] = coords14[2];
		double[] coords16 = new double[3];
		coords16[0] = internalX4;
		coords16[1] = internalY4;
		coords16[2] = floorHeight;
		*/
		DirectPosition position11 = GeometryUtil.createDirectPosition(coords11);
		DirectPosition position12 = GeometryUtil.createDirectPosition(coords12);
		DirectPosition position13 = GeometryUtil.createDirectPosition(coords13);
		DirectPosition position14 = GeometryUtil.createDirectPosition(coords14);
		List<DirectPosition> positions1 = new ArrayList<DirectPosition>();
		positions1.add(position11);
		positions1.add(position12);
		positions1.add(position13);
		positions1.add(position14);
		//positions1.add(position15);
		//positions1.add(position16);
		positions1.add(position11);
		Surface surface1 = GeometryUtil.createSurface(positions1);
		stairSurfaces.add(surface1);
		
		double[] coords22 = new double[3];
		coords22[0] = x21;
		coords22[1] = y21;
		coords22[2] = floorHeight;
		double[] coords23 = new double[3];
		coords23[0] = internalX2;
		coords23[1] = internalY2;
		coords23[2] = midHeight;		
		DirectPosition position22 = GeometryUtil.createDirectPosition(coords22);
		DirectPosition position23 = GeometryUtil.createDirectPosition(coords23);
		List<DirectPosition> positions2 = new ArrayList<DirectPosition>();
		positions2.add(position11);
		positions2.add(position22);
		positions2.add(position23);
		positions2.add(position12);
		positions2.add(position11);
		Surface surface2 = GeometryUtil.createSurface(positions2);
		stairSurfaces.add(surface2);
		
		double[] coords32 = new double[3];
		coords32[0] = x21;
		coords32[1] = y21;
		coords32[2] = ceilingHeight;
		DirectPosition position32 = GeometryUtil.createDirectPosition(coords32);
		List<DirectPosition> positions3 = new ArrayList<DirectPosition>();
		positions3.add(position22);
		positions3.add(position32);
		positions3.add(position23);
		positions3.add(position22);
		Surface surface3 = GeometryUtil.createSurface(positions3);
		stairSurfaces.add(surface3);
		
		double[] coords42 = floorPositions.get(3).getCoordinate();
		coords42[2] = ceilingHeight;
		double[] coords43 = new double[3];
		coords43[0] = internalX3;
		coords43[1] = internalY3;
		coords43[2] = midHeight;
		DirectPosition position42 = GeometryUtil.createDirectPosition(coords42);
		DirectPosition position43 = GeometryUtil.createDirectPosition(coords43);
		List<DirectPosition> positions4 = new ArrayList<DirectPosition>();
		positions4.add(position32);
		positions4.add(position42);
		positions4.add(position43);
		positions4.add(position23);
		positions4.add(position32);
		Surface surface4 = GeometryUtil.createSurface(positions4);
		stairSurfaces.add(surface4);
		
		double[] coords53 = floorPositions.get(2).getCoordinate();
		coords53[2] = midHeight;
		DirectPosition position53 = GeometryUtil.createDirectPosition(coords53);
		List<DirectPosition> positions5 = new ArrayList<DirectPosition>();
		positions5.add(position12);
		positions5.add(position43);
		positions5.add(position53);
		positions5.add(position13);
		positions5.add(position12);
		Surface surface5 = GeometryUtil.createSurface(positions5);
		stairSurfaces.add(surface5);
		
		List<DirectPosition> positions6 = new ArrayList<DirectPosition>();
		positions6.add(position13);
		positions6.add(position53);
		positions6.add(floorPositions.get(2));
		positions6.add(floorPositions.get(1));
		positions6.add(position13);
		Surface surface6 = GeometryUtil.createSurface(positions6);
		stairSurfaces.add(surface6);
		
		List<DirectPosition> positions7 = new ArrayList<DirectPosition>();
		positions7.add(floorPositions.get(3));
		positions7.add(floorPositions.get(2));
		positions7.add(position53);
		positions7.add(position43);
		positions7.add(position42);
		positions7.add(floorPositions.get(3));
		Surface surface7 = GeometryUtil.createSurface(positions7);
		stairSurfaces.add(surface7);
		
		List<DirectPosition> positions8 = new ArrayList<DirectPosition>();
		positions8.add(position22);
		positions8.add(floorPositions.get(3));
		positions8.add(position42);
		positions8.add(position32);
		positions8.add(position22);
		Surface surface8 = GeometryUtil.createSurface(positions8);
		stairSurfaces.add(surface8);
		
		MultiSurface multiSurface = GeometryUtil.createMultiSurface(stairSurfaces);
		lod4Geometry.setGeometry(multiSurface);
		
		return stair;
	}
	
	private Surface reverseSurface(Surface surface) {
		SurfaceBoundary boundary = null;
		
		if (surface instanceof Surface) {
			boundary = surface.getBoundary();
		} else if (surface instanceof Polygon) {
			boundary = ((Polygon) surface).getBoundary();
		} else {
			throw new UnsupportedOperationException("boundary is null");
		}
		
		Ring exterior = boundary.getExterior();
		List<Ring> interiors = boundary.getInteriors();
		
		Ring rExterior = null;
		List<Ring> rInteriors = null;
		
		List<DirectPosition> positions = ((RingImplUnsafe) exterior).asDirectPositions();
		List<DirectPosition> reverse = new ArrayList<DirectPosition>(positions);
		Collections.reverse(reverse);
		rExterior = pf.createRingByDirectPositions(reverse);
		
		if (interiors != null && interiors.size() > 0) {
			rInteriors = new ArrayList<Ring>();
			
			for (Ring interior : interiors) {
				List<DirectPosition> interiorPositions = ((RingImplUnsafe) interior).asDirectPositions();
				List<DirectPosition> reverseInteriorPositions = reverseList(interiorPositions);
				Ring rInterior = pf.createRingByDirectPositions(reverseInteriorPositions);
				
				rInteriors.add(rInterior);
			}
		}
		
		SurfaceBoundary rBoundary = pf.createSurfaceBoundary(rExterior, rInteriors);
		Surface rSurface = pf.createSurface(rBoundary);
		
		return rSurface;
	}
	
	private List<DirectPosition> reverseList(List<DirectPosition> list) {
		 if (list == null) {
			 return null;
		 } else if (list.size() == 0) {
			 return list;
		 }
		 
		 List<DirectPosition> reverse = new ArrayList<DirectPosition>();
		 for (int i = list.size() - 1; i >= 0; i--) {
			 reverse.add(list.get(i));
		 }
		 
		 return reverse;
	}
	
	private List<Surface> refinementSurface(Surface wallFacet, List<VOSurface> voSurfaces) {
		List<Surface> newSurfaces = new ArrayList<Surface>();
		
		SurfaceBoundary wallBoundary = wallFacet.getBoundary();		
		Ring wallExterior = wallBoundary.getExterior();		
		List<DirectPosition> wallPositions = ((RingImplUnsafe) wallExterior).asDirectPositions();
		
		Envelope envelope = wallFacet.getEnvelope();
		double minZ = envelope.getLowerCorner().getOrdinate(2);
		List<DirectPosition> wallFloorPositions = new ArrayList<DirectPosition>();
		for (int i = 0; i < wallPositions.size() - 1; i++) { // 사각형은 5개 position으로 구성되므로 바닥 점이 3개가 될 수 있어서 하나 빼야한다. 
			DirectPosition position = wallPositions.get(i);
			if (position.getOrdinate(2) == minZ) {
				wallFloorPositions.add(position);
			}
		}
		
		List<DirectPosition> doorFloorPositions = new ArrayList<DirectPosition>();
		double doorHeight = 0;
		for (VOSurface voSurface : voSurfaces) {
			SurfaceBoundary doorBoundary = null;
			if (voSurface.getGeometry() != null) {
				doorBoundary = voSurface.getGeometry().getBoundary();
			} else if (voSurface.getPolygonGeometry() != null) {
				doorBoundary = voSurface.getPolygonGeometry().getBoundary();
			}
			
			Ring doorExterior = doorBoundary.getExterior();
			List<DirectPosition> doorPositions = ((RingImplUnsafe) doorExterior).asDirectPositions();
			
			//rExterior = pf.createRingByDirectPositions(reverse);
			
			// envelope 으로 바닥점 2개 찾는다. 인덱스 저장.
			// 벽 바닥점 2개와 문의 바닥점 2n(문이 여러개일 경우)개 정렬한다.
			// 벽 바닥점을 잇는 선분에 문 바닥점의 snap point 찾아야함.
			// ISO Geometry -> JTS로 변환
			// LocationIndexedLine으로 snapPoint 추출 -> 정렬
			// JTS -> ISO 변환, z값 추가
			// 벽의 바닥점 2개 사이에 문에 대한 2n*2개점 추가
						
			for (int i = 0; i < doorPositions.size() - 1; i++) { // 사각형은 5개 position으로 구성되므로 바닥 점이 3개가 될 수 있어서 하나 빼야한다. 
				DirectPosition position = doorPositions.get(i);
				if (position.getOrdinate(2) == minZ) {
					doorFloorPositions.add(position);
				}
			}
			doorHeight = doorBoundary.getEnvelope().getUpperCorner().getOrdinate(2);
			if (doorFloorPositions.size() % 2 != 0) {
				System.out.println("Door Floor Point is not even");
			}
		}
				
		doorFloorPositions = getSnapPositionForDoor(wallFloorPositions, doorFloorPositions);
		// 문의 바닥점 2개에 높이 z값만 바꾼 점들을 추가해주어야 한다.
		List<DirectPosition> newDoorFloorPositions = new ArrayList<DirectPosition>();
		List<DirectPosition> newPositionForDoorInstance = new ArrayList<DirectPosition>();
		for (int i = 0; i < doorFloorPositions.size(); i += 2) {
			DirectPosition doorStartPoint = doorFloorPositions.get(i);
			DirectPosition doorEndPoint = doorFloorPositions.get(i + 1);
			
			double[] startCoords = doorStartPoint.getCoordinate();
			double[] endCoords = doorEndPoint.getCoordinate();
			startCoords[2] = doorHeight;
			endCoords[2] = doorHeight;
			
			DirectPosition doorMidPoint1 = GeometryUtil.createDirectPosition(startCoords);
			DirectPosition doorMidPoint2 = GeometryUtil.createDirectPosition(endCoords);
			
			newDoorFloorPositions.add(doorStartPoint);
			newDoorFloorPositions.add(doorMidPoint1);
			newDoorFloorPositions.add(doorMidPoint2);
			newDoorFloorPositions.add(doorEndPoint);
			
			newPositionForDoorInstance.add(doorStartPoint);
			newPositionForDoorInstance.add(doorMidPoint1);
			newPositionForDoorInstance.add(doorMidPoint2);
			newPositionForDoorInstance.add(doorEndPoint);
			newPositionForDoorInstance.add(doorStartPoint);
			
			Surface doorSurface = pf.createSurfaceByDirectPositions(newPositionForDoorInstance);
			newSurfaces.add(doorSurface);
			newPositionForDoorInstance.clear();
		}
		
		// 원래 벽의 기하에 문에 대한 점을 추가해서 구멍을 뚫는다.
		DirectPosition wallFloorStartPoint = wallFloorPositions.get(0);
		int startIdx = wallPositions.indexOf(wallFloorStartPoint);
		wallPositions.addAll(startIdx, newDoorFloorPositions);
		
		// 구멍뚫은 벽면 생성 - 면의 중앙에 뚫리는 것이 아니므로 Interior 가 필요없다.
		/*
		Ring newExterior = pf.createRingByDirectPositions(wallFloorPositions);
		Collections.reverse(doorFloorPositions);
		Ring newInterior = pf.createRingByDirectPositions(doorFloorPositions);
		List<Ring> newInteriors = new ArrayList<Ring>();
		newInteriors.add(newInterior);
		SurfaceBoundary newSurfaceBoundary = pf.createSurfaceBoundary(newExterior, newInteriors);
		Surface newSurface = pf.createSurface(newSurfaceBoundary);
		*/
		Surface newWallSurface = pf.createSurfaceByDirectPositions(wallPositions);
		newSurfaces.add(0, newWallSurface);
		
		return newSurfaces;
	}
	
	private List<DirectPosition> getSnapPositionForDoor(List<DirectPosition> wallFloorPositions, List<DirectPosition> doorFloorPositions) {
		LineString lineString = GeometryUtil.createJTSLineString(wallFloorPositions);
		List<DirectPosition> doorSnapPosition = new ArrayList<DirectPosition>();
		for (DirectPosition position : doorFloorPositions) {
			Point jtsPoint = GeometryUtil.ISODirectPositionToJTSPoint(position);
			Point snapJTSPoint = JTSUtil.snapPointToLineStringByLIL(lineString, jtsPoint);
			
			doorSnapPosition.add(GeometryUtil.JTSPointToISODirectPosition(snapJTSPoint));
		}
		
		doorSnapPosition = orderSnapPositionForDoor(wallFloorPositions, doorFloorPositions);
		
		return doorSnapPosition;
	}
	
	private List<DirectPosition> orderSnapPositionForDoor(List<DirectPosition> wallFloorPositions, List<DirectPosition> doorFloorPoisitions) {
		DirectPosition startPoint = wallFloorPositions.get(0);
		DirectPosition endPoint = wallFloorPositions.get(1);
		
		double startX = startPoint.getOrdinate(0);
		double startY = startPoint.getOrdinate(1);
		double endX = endPoint.getOrdinate(0);
		double endY = endPoint.getOrdinate(1);
		
		boolean isAscendingX = true;
		boolean isAscendingY = true;
		if (startX > endX) {
			isAscendingX = false;
		}
		if (startY > endY) {
			isAscendingY = false;
		}
		
		List<DirectPosition> orderedPosition = orderSnapPositionForDoor(doorFloorPoisitions, 0, isAscendingX);
		orderedPosition = orderSnapPositionForDoor(doorFloorPoisitions, 1, isAscendingY);
		
		return orderedPosition;
	}
	
	private List<DirectPosition> orderSnapPositionForDoor(List<DirectPosition> doorFloorPositions, int dimension, boolean isAscending) {
		DirectPosition[] positions = doorFloorPositions.toArray(new DirectPosition[doorFloorPositions.size()]);
		
		for (int i = 0; i < positions.length - 1; i++) {
			for (int j = i; j < positions.length - 1; j++) {
				if (positions[j].getOrdinate(dimension) > positions[j + 1].getOrdinate(dimension)) {
					DirectPosition temp = positions[j];
					positions[j] = positions[j + 1];
					positions[j + 1] = temp;
				}
			}
		}
		
		List<DirectPosition> ordered = new ArrayList<DirectPosition>();
		Collections.addAll(ordered, positions);
		
		if (!isAscending) {
			Collections.reverse(ordered);
		}
		
		return ordered;
	}
	
	private com.vividsolutions.jts.geom.Geometry getFloorSurfaceUnion(List<CellSpace> cellSpaces) { // Polygon or MultiPolygon (floors of other building)
		List<com.vividsolutions.jts.geom.Polygon> jtsPolygons = new ArrayList<com.vividsolutions.jts.geom.Polygon>(); 
		for (CellSpace cellSpace : cellSpaces) {
			Surface floorSurface = cellSpace.getFacets().get(1); // floor facet
			jtsPolygons.add(GeometryUtil.ISOSurfaceToJTSPolygon(floorSurface));
		}
		com.vividsolutions.jts.geom.Geometry union = JTSUtil.getUnion(jtsPolygons);
		
		return union;
	}
	
	private List<DirectPosition> getFloorPositions(Surface floor, Surface wall) {
		Ring wallExterior = wall.getBoundary().getExterior();
		List<DirectPosition> wallPositions = ((RingImplUnsafe) wallExterior).asDirectPositions();
		double z = floor.getEnvelope().getMinimum(2);
		
		List<DirectPosition> wallBottomPositions = new ArrayList<DirectPosition>();
		for (int i = 0; i < wallPositions.size() - 1; i++) {
			DirectPosition position = wallPositions.get(i);
			
			if (position.getOrdinate(2) == z) {
				wallBottomPositions.add(position);
			}
		}
		
		Ring floorExterior = floor.getBoundary().getExterior();
		List<DirectPosition> floorPositions = ((RingImplUnsafe) floorExterior).asDirectPositions();		
		List<DirectPosition> result = new ArrayList<DirectPosition>();
		int idx = -1;
		for (int i = 0; i < floorPositions.size() - 1; i++) {
			double[] floorCoords = floorPositions.get(i).getCoordinate();
			for (DirectPosition wallPosition : wallBottomPositions) {
				double[] wallCoords = wallPosition.getCoordinate();
				
				if (floorCoords[0] == wallCoords[0] && floorCoords[1] == wallCoords[1]) {
					idx = i;
					break;
				}
			}
			if (idx != -1) {
				break;
			}
		}
		
		// 0 : start(문쪽) 1 2 : 반대쪽 3:end(문쪽)
		while (result.size() < 4) {
			result.add(floorPositions.get(idx++));
			idx = idx % 4;
		}
		
		return result;
	}
	
}
