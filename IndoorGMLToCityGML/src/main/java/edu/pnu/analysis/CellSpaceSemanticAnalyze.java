package edu.pnu.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.opengis.indoorgml.v_1_0.vo.core.CellSpace;
import net.opengis.indoorgml.v_1_0.vo.core.CellSpaceBoundary;
import net.opengis.indoorgml.v_1_0.vo.core.IndoorFeatures;
import net.opengis.indoorgml.v_1_0.vo.core.InterEdges;
import net.opengis.indoorgml.v_1_0.vo.core.InterLayerConnection;
import net.opengis.indoorgml.v_1_0.vo.core.State;
import net.opengis.indoorgml.v_1_0.vo.core.Transition;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOCurve;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOSurface;

import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.primitive.Curve;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

public class CellSpaceSemanticAnalyze {
	private IndoorFeatures indoorFeatures;

	private List<Double> floorZOrdinates;
	private Map<Double, List<CellSpace>> floorCellSpaceMap;

	private PrimitiveFactoryImpl pf = null;

	public CellSpaceSemanticAnalyze(IndoorFeatures indoorFeatures, List<Double> floorZOrdinates, Map<Double, List<CellSpace>> floorCellSpaceMap,
			PrimitiveFactoryImpl pf) {
		this.indoorFeatures = indoorFeatures;
		this.floorZOrdinates = floorZOrdinates;
		this.floorCellSpaceMap = floorCellSpaceMap;
		this.pf = pf;
	}

	public void analyzeStep() {		
		for (Double z : floorZOrdinates) {
			List<CellSpace> floorCellSpace = floorCellSpaceMap.get(z);

			for (CellSpace cellSpace : floorCellSpace) {
				findCellSpaceSemantic(cellSpace);
			}
		}
	}
	
	public boolean findCellSpaceSemantic(CellSpace cellSpace) {
		// navigable
		if (cellSpace.getNavigableType().equals("ConnectionSpaceType")) {
			cellSpace.setEstimatedType("DOOR");
		} else if (cellSpace.getNavigableType().equals("AnchorSpaceType")) {
			cellSpace.setEstimatedType("DOOR");
		} else if (cellSpace.getNavigableType().equals("GeneralSpaceType")) {
			cellSpace.setEstimatedType("ROOM");
		} else if (cellSpace.getNavigableType().equals("TransitionSpaceType")) {
			if (isStair(cellSpace)) {
				cellSpace.setEstimatedType("STAIR");
			} else {
				cellSpace.setEstimatedType("ROOM");
			}
		} else { // CellSpaceType, NavigableSpaceType
			State stateDuality = cellSpace.getDuality();
			if (stateDuality == null) return false;
			
			ArrayList<Transition> transitions = stateDuality.getConnects();
			if (transitions == null || transitions.size() == 0) {
				cellSpace.setEstimatedType("WALL");
			} else {
				if (isStair(cellSpace)) {
					cellSpace.setEstimatedType("STAIR");
					return true;
				}
				
				// door 판별				
				if (isDoor(cellSpace)) {
					cellSpace.setEstimatedType("DOOR");
				} else {
					cellSpace.setEstimatedType("ROOM");
				}
				
			}
		}
		
		return true;
	}
	
	private boolean isStair(CellSpace cellSpace) {
		State stateDuality = cellSpace.getDuality();
		if (stateDuality == null) return false;
		
		ArrayList<Transition> transitions = stateDuality.getConnects();
		boolean isStair = false;
		for (Transition transition : transitions) {
			VOCurve voCurve = transition.getGeometry();
			Curve curve = voCurve.getGeometry();
			DirectPosition startPoint = curve.getStartPoint();
			DirectPosition endPoint = curve.getEndPoint();
			
			if (startPoint.getOrdinate(2) != endPoint.getOrdinate(2)) {
				isStair = true;
				
				if (transition.getDuality() == null) {
					List<Surface> facets = cellSpace.getFacets();
					CellSpaceBoundary cellSpaceBoundary = null;
					Surface facet = null;
					if (facets.get(0).intersects(curve)) {
						facet = facets.get(0);
						cellSpaceBoundary = createCellSpaceBoundary(facet, transition);
						
					} else if (facets.get(1).intersects(curve)) {
						facet = facets.get(1);
						cellSpaceBoundary = createCellSpaceBoundary(facet, transition);
					}
					
					if (cellSpaceBoundary != null) {
						transition.setDuality(cellSpaceBoundary);
						ArrayList<CellSpaceBoundary> partialBoundedBy = cellSpace.getPartialBoundedBy();
						if (partialBoundedBy == null) {
							partialBoundedBy = new ArrayList<CellSpaceBoundary>();
							cellSpace.setPartialBoundedBy(partialBoundedBy);
						}
						partialBoundedBy.add(cellSpaceBoundary);
						
						Map<Surface, List<CellSpaceBoundary>> facetBoundaryMap = cellSpace.getFacetBoundaryMap();
						if (facetBoundaryMap == null) {
							facetBoundaryMap = new HashMap<Surface, List<CellSpaceBoundary>>();
							cellSpace.setFacetBoundaryMap(facetBoundaryMap);
						}
						List<CellSpaceBoundary> boundaryList = facetBoundaryMap.get(facet);
						if (boundaryList == null) {
							boundaryList = new ArrayList<CellSpaceBoundary>();
						}
						boundaryList.add(cellSpaceBoundary);
						facetBoundaryMap.put(facet, boundaryList);						
					}
					
				}
				break;
			}
		}
		
		return isStair;
	}
	
	private CellSpaceBoundary createCellSpaceBoundary(Surface surface, Transition duality) {
		CellSpaceBoundary cellSpaceBoundary = new CellSpaceBoundary();
		VOSurface geometry3D = new VOSurface();
		geometry3D.setGeometry(surface);
		cellSpaceBoundary.setGeometry3D(geometry3D);
		cellSpaceBoundary.setDuality(duality);
		
		return cellSpaceBoundary;
	}
	
	private boolean isDoor(CellSpace cellSpace) {
		State state = cellSpace.getDuality();
		if (state == null) return false;
		ArrayList<Transition> connects = state.getConnects();
		if (!(connects != null && connects.size() == 2)) return false;
		
		Map<Surface, List<CellSpaceBoundary>> facetBoundaryMap = cellSpace.getFacetBoundaryMap();		
		int equalsCnt = 0;
		
		for (Transition transition : connects) {
			CellSpaceBoundary boundary = transition.getDuality();
			if (boundary == null) return false;
			OrientableSurface boundaryGeometry = boundary.getGeometry3D().getGeometry();
			
			if (boundaryGeometry == null) {
				if (boundary.getGeometry3D().getPolygonGeometry() != null) {
					SurfaceBoundary polygonBoundary = boundary.getGeometry3D().getPolygonGeometry().getBoundary();
					boundaryGeometry = pf.createSurface(polygonBoundary);
				} else {
					throw new UnsupportedOperationException("Not exist geometry object of CellSpaceBoundary");
				}
			}
			
			for(Entry<Surface, List<CellSpaceBoundary>> entry : facetBoundaryMap.entrySet()) {
				Surface facet = entry.getKey();
				List<CellSpaceBoundary> boundaryList = entry.getValue();
				
				if (boundaryList.contains(boundary) && facet.equals(boundaryGeometry)) {
					equalsCnt++;
					break;
				}
			}
		}
		
		if (equalsCnt == 2 && isSubspace(cellSpace)) return false;
		
		return false;
	}
	
	private boolean isSubspace(CellSpace cellSpace) {
		return isSubspace(cellSpace.getDuality());
	}
	
	private boolean isSubspace(State state) {
		InterEdges interEdges = indoorFeatures.getMultiLayeredGraph().getInterEdges().get(0);
		Map<State, InterLayerConnection> stateILCMap = interEdges.getStateILCMap();
		
		InterLayerConnection ilc = stateILCMap.get(state);
		if (ilc == null) {
			return false;
		} else {
			String topology = ilc.getTypeOfTopoExpression();
			int index = ilc.getInterConnects().indexOf(state);
			
			if ( (index == 0 && topology.equalsIgnoreCase("WITHIN"))
					|| (index == 1 && topology.equalsIgnoreCase("CONTAINS")) ) {
				return true;
			}
		}
		
		return false;
	}
}
