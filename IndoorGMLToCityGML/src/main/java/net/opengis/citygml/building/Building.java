package net.opengis.citygml.building;

import java.util.ArrayList;
import java.util.List;

import net.opengis.citygml.core.CityObject;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;

public class Building extends CityObject {
	private Building parent;
	private List<Building> buildingParts;
	private String type; // Building, BuildingPart
	
	private List<Room> rooms;
		
	private String clazz; // CodeType
	private String function; // CodeType
	private String usage; // CodeType
	private int yearOfConstruction;
	private int yearOfDemolition;
	private String roofType;
	private String measureHeight; // LengthType
	private int storeysAboveGround;
	private int storeysBelowGround;
	private String storeyHeightsAboveGround;
	private String storeyHeightsBelowGround;
	
	private VOMultiSurface lod1MultiSurface;
	
	public Building() {
		yearOfConstruction = -1;
		yearOfDemolition = -1;
		storeysAboveGround = -1;
		storeysBelowGround = -1;		
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public int getYearOfConstruction() {
		return yearOfConstruction;
	}

	public void setYearOfConstruction(int yearOfConstruction) {
		this.yearOfConstruction = yearOfConstruction;
	}

	public int getYearOfDemolition() {
		return yearOfDemolition;
	}

	public void setYearOfDemolition(int yearOfDemolition) {
		this.yearOfDemolition = yearOfDemolition;
	}

	public String getRoofType() {
		return roofType;
	}

	public void setRoofType(String roofType) {
		this.roofType = roofType;
	}

	public String getMeasureHeight() {
		return measureHeight;
	}

	public void setMeasureHeight(String measureHeight) {
		this.measureHeight = measureHeight;
	}

	public int getStoreysAboveGround() {
		return storeysAboveGround;
	}

	public void setStoreysAboveGround(int storeysAboveGround) {
		this.storeysAboveGround = storeysAboveGround;
	}

	public int getStoreysBelowGround() {
		return storeysBelowGround;
	}

	public void setStoreysBelowGround(int storeysBelowGround) {
		this.storeysBelowGround = storeysBelowGround;
	}

	public String getStoreyHeightsAboveGround() {
		return storeyHeightsAboveGround;
	}

	public void setStoreyHeightsAboveGround(String storeyHeightsAboveGround) {
		this.storeyHeightsAboveGround = storeyHeightsAboveGround;
	}

	public String getStoreyHeightsBelowGround() {
		return storeyHeightsBelowGround;
	}

	public void setStoreyHeightsBelowGround(String storeyHeightsBelowGround) {
		this.storeyHeightsBelowGround = storeyHeightsBelowGround;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

		public Building getParent() {
		return parent;
	}

	public void setParent(Building parent) {
		this.parent = parent;
	}

	public List<Building> getBuildingParts() {
		if (buildingParts == null) {
			buildingParts = new ArrayList<Building>();
		}
		return buildingParts;
	}

	public void setBulidingParts(List<Building> bulidingParts) {
		this.buildingParts = bulidingParts;
	}

	public VOMultiSurface getLod1MultiSurface() {
		return lod1MultiSurface;
	}

	public void setLod1MultiSurface(VOMultiSurface lod1MultiSurface) {
		this.lod1MultiSurface = lod1MultiSurface;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public void setRooms(List<Room> rooms) {
		this.rooms = rooms;
	}

}
