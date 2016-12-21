package net.opengis.indoorgml.v_1_0.vo.core;

import java.util.ArrayList;
import java.util.Map;

public class InterEdges extends IndoorObject {
	public ArrayList<IndoorObject> indoorObject = new ArrayList<IndoorObject>();
	public MultiLayeredGraph parents;

	private ArrayList<InterLayerConnection> interLayerConnectionMember;
	private Map<State, InterLayerConnection> stateILCMap;

	public ArrayList<InterLayerConnection> getInterLayerConnectionMember() {
		return interLayerConnectionMember;
	}

	public void setInterLayerConnectionMember(ArrayList<InterLayerConnection> interLayerConnectionMember) {
		this.interLayerConnectionMember = interLayerConnectionMember;
	}
	
	public Map<State, InterLayerConnection> getStateILCMap() {
		return stateILCMap;
	}
	
	public void setStateILCMap(Map<State, InterLayerConnection> stateILCMap) {
		this.stateILCMap = stateILCMap;
	}
}
