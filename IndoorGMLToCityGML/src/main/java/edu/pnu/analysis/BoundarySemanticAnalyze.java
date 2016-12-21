package edu.pnu.analysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.opengis.indoorgml.v_1_0.vo.core.CellSpace;
import net.opengis.indoorgml.v_1_0.vo.core.CellSpaceBoundary;
import net.opengis.indoorgml.v_1_0.vo.core.IndoorFeatures;
import net.opengis.indoorgml.v_1_0.vo.core.InterEdges;
import net.opengis.indoorgml.v_1_0.vo.core.InterLayerConnection;
import net.opengis.indoorgml.v_1_0.vo.core.PrimalSpaceFeatures;
import net.opengis.indoorgml.v_1_0.vo.core.State;
import net.opengis.indoorgml.v_1_0.vo.core.Transition;

import org.geotools.geometry.iso.primitive.PrimitiveFactoryImpl;
import org.geotools.geometry.iso.primitive.RingImpl;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.primitive.Point;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Surface;

public class BoundarySemanticAnalyze {
	private IndoorFeatures indoorFeatures;

	private List<Double> floorZOrdinates;
	private Map<Double, List<CellSpace>> floorCellSpaceMap;

	private PrimitiveFactoryImpl pf = null;

	public BoundarySemanticAnalyze(IndoorFeatures indoorFeatures, List<Double> floorZOrdinates, Map<Double, List<CellSpace>> floorCellSpaceMap,
			PrimitiveFactoryImpl pf) {
		this.indoorFeatures = indoorFeatures;
		this.floorZOrdinates = floorZOrdinates;
		this.floorCellSpaceMap = floorCellSpaceMap;
		this.pf = pf;
	}

	// Cell의 CellBoundary의 Semantic(Door, Wall, Virtual Wall)을 알아낸다.
	
	public void analyzeStep() {
		// CellSpaceBoundary 분석
		PrimalSpaceFeatures psf = indoorFeatures.getPrimalSpaceFeatures();
		ArrayList<CellSpace> cellSpaceMember = psf.getCellSpace();
		
		for (Double z : floorZOrdinates) {
			List<CellSpace> floorCellSpace = floorCellSpaceMap.get(z);

			for (CellSpace cellSpace : floorCellSpace) {
				findBoundarySemantic(cellSpace);
			}
		}
	}
	
	private void findBoundarySemantic(CellSpace cellSpace) {
		State state = cellSpace.getDuality();
		if (state == null) return;
		
		ArrayList<Transition> connects = state.getConnects();
		if (connects == null) return;
		
		ArrayList<CellSpaceBoundary> boundedBy = cellSpace.getPartialBoundedBy();
		if (boundedBy == null || boundedBy.isEmpty()) return;
		
		for (CellSpaceBoundary boundary : boundedBy) {
			Transition transition = boundary.getDuality();
			if (transition == null) {
				boundary.setEstimatedType("WALL");
			} else {
				if (isVirtualWall(boundary, transition)) {
					boundary.setEstimatedType("VIRTUALWALL");
				} else {
					boundary.setEstimatedType("DOOR");
				}
			}
		}
	}
	
	private boolean isVirtualWall(CellSpaceBoundary boundary, Transition transition) {
		ArrayList<State> connects = transition.getConnects();
		State state1 = connects.get(0);
		State state2 = connects.get(1);
		Point point1 = state1.getGeometry().getGeometry();
		Point point2 = state2.getGeometry().getGeometry();
		
		CellSpace c1 = state1.getDuality();
		CellSpace c2 = state2.getDuality();
		if (c1 == null || c2 == null) return false;
		
		// Stair - Stair
		if (c1.getEstimatedType().equals("STAIR") && c2.getEstimatedType().equals("STAIR") && 
				point1.getDirectPosition().getOrdinate(2) != point2.getDirectPosition().getOrdinate(2)) {
			return true;
		}
		
		// Subspacing
		if (isSubspace(state1, state2)) {
			return true;
		}
		
		return false;
	}
	
	private boolean isSubspace(State state1, State state2) {
		List<InterEdges> interEdgesList = indoorFeatures.getMultiLayeredGraph().getInterEdges();
		if (interEdgesList == null || interEdgesList.isEmpty()) return false;
		
		InterEdges interEdges = indoorFeatures.getMultiLayeredGraph().getInterEdges().get(0);
		Map<State, InterLayerConnection> stateILCMap = interEdges.getStateILCMap();
		
		InterLayerConnection ilc1 = stateILCMap.get(state1);
		InterLayerConnection ilc2 = stateILCMap.get(state2);
		if (ilc1 == null || ilc2 == null) {
			return false;
		} else {
			String topology1 = ilc1.getTypeOfTopoExpression();
			String topology2 = ilc1.getTypeOfTopoExpression();
			
			int idx1 = ilc1.getInterConnects().indexOf(state1); // 0 or 1
			int idx2 = ilc2.getInterConnects().indexOf(state2);
			
			State otherState1 = ilc1.getInterConnects().get(1 - idx1);
			State otherState2 = ilc2.getInterConnects().get(1 - idx2);
			
			if (!otherState1.equals(otherState2)) {
				return false;
			} else {
				boolean isSubspace1 = false;
				boolean isSubspace2 = false;
				if ( (idx1 == 0 && topology1.equalsIgnoreCase("WITHIN"))
						|| (idx1 == 1 && topology1.equalsIgnoreCase("CONTAINS")) ) {
					isSubspace1 = true;
				}
				
				if ( (idx2 == 0 && topology2.equalsIgnoreCase("WITHIN"))
						|| (idx2 == 1 && topology2.equalsIgnoreCase("CONTAINS")) ) {
					isSubspace2 = true;
				}
				
				if (isSubspace1 && isSubspace2) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	private void findBoundarySemantic2(CellSpace cellSpace) {
		int transitionSize = cellSpace.getDuality().getConnects().size();
		int connectionCnt = 0;
		
		Map<Surface, List<CellSpaceBoundary>> facetBoundaryMap = cellSpace.getFacetBoundaryMap();
		for (Entry<Surface, List<CellSpaceBoundary>> entry : facetBoundaryMap.entrySet()) {
			Surface facet = entry.getKey();
			List<CellSpaceBoundary> boundaryList = entry.getValue();
			
			// duality로 transition이 있는 CellBoundary부터 찾는다.
			for (CellSpaceBoundary boundary : boundaryList) {
				if (boundary.getDuality() != null) {
					// virtual wall, door
					distinguishDoorVirtualWall(facet, boundary);
					connectionCnt++;
				}
			}
		}
		
		// cellspace semantic 찾아낸 후 해야한다.
		// duality가 있는 cellboundary의 개수가 transition 개수와 같다면 나머지 boundary는 모두 벽으로 처리한다.
		// 아니라면 문에 해당하는 boundary를 찾는다.
		for (Entry<Surface, List<CellSpaceBoundary>> entry : facetBoundaryMap.entrySet()) {
			Surface facet = entry.getKey();
			List<CellSpaceBoundary> boundaryList = entry.getValue();
			
			for (CellSpaceBoundary boundary : boundaryList) {
				if (boundary.getDuality() != null) continue;
				if (connectionCnt == transitionSize) {
					boundary.setEstimatedType("WALL");
				} else {
					// duality가 없는 boundary중에서 door가 될 수 것을 찾는다.
					if (isDoor(facet, boundary)) {
						boundary.setEstimatedType("DOOR");
					} else {
						boundary.setEstimatedType("WALL");
					}
				}
			}
		}
		
	}
	
	private boolean distinguishDoorVirtualWall(Surface facet, CellSpaceBoundary boundary) {
		if (isWall(facet, boundary)) { // boundary의 기하가 벽과 같으면 가상벽으로 본다.
			boundary.setEstimatedType("VIRTUALWALL");
			return true;
		} else if (isDoor(facet, boundary)) { // 다르면 문
			boundary.setEstimatedType("DOOR");
			return true;
		}
		
		return false;
	}
	
	private boolean isWall(Surface facet, CellSpaceBoundary boundary) {
		Surface boundaryGeometry = (Surface) boundary.getGeometry3D().getGeometry();
		
		if ( Math.abs(facet.getArea() - boundaryGeometry.getArea()) <= 0.001 ) {
			return true;
		}
		return false;
	}
	
	private boolean isDoor(Surface facet, CellSpaceBoundary boundary) {
		Surface boundaryGeometry = (Surface) boundary.getGeometry3D().getGeometry();
				
		// 문인지 검사
		// 1. 바닥면과 붙어있어야 한다.
		// 2. 보통 천장과는 붙어있지 않다.
		// 3. 넓이가 벽면보다 작다.
		// 바닥면과 붙어 있는지, 형태 넓이?
		Envelope facetEnv = facet.getEnvelope();
		Envelope boundaryEnv = boundaryGeometry.getEnvelope();
		
		Ring exteriorRing = boundaryGeometry.getBoundary().getExterior();
		List<DirectPosition> positions = ((RingImpl) exteriorRing).asDirectPositions();
		if (positions.size() == 5) {
			int lowerCnt = 0;
			int upperCnt = 0;
			
			for (int i = 0; i < positions.size() - 1; i++) {
				DirectPosition position = positions.get(i);
				double z = position.getOrdinate(2);
				
				if (z == boundaryEnv.getMinimum(2)) {
					lowerCnt++;
				}
				if (z == boundaryEnv.getMaximum(2)) {
					upperCnt++;
				}
			}
			
			if (lowerCnt == 2 && upperCnt == 2) {
				return true;
			}
		}
		
		return false;
	}
}
