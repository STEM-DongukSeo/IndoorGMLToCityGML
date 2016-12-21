package net.opengis.citygml.core;

import java.util.Date;

public class CityObject extends AbstractFeature {
	private String cityObjectType;
	private Date creationDate;
	private Date terminationDate;
	private String relativeToTerrain;
	private String relativeToWater;
	
	public CityObject() {
		// TODO Auto-generated constructor stub
	}

	public String getCityObjectType() {
		return cityObjectType;
	}

	public void setCityObjectType(String cityObjectType) {
		this.cityObjectType = cityObjectType;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getTerminationDate() {
		return terminationDate;
	}

	public void setTerminationDate(Date terminationDate) {
		this.terminationDate = terminationDate;
	}

	public String getRelativeToTerrain() {
		return relativeToTerrain;
	}

	public void setRelativeToTerrain(String relativeToTerrain) {
		this.relativeToTerrain = relativeToTerrain;
	}

	public String getRelativeToWater() {
		return relativeToWater;
	}

	public void setRelativeToWater(String relativeToWater) {
		this.relativeToWater = relativeToWater;
	}

}
