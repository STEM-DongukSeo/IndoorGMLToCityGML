package net.opengis.citygml.core;

public abstract class AbstractFeature {
	
	private String gmlID;
	private String gmlName;
	private String nameCodeSpace;
	private String description;
	
	public String getGmlID() {
		return gmlID;
	}

	public void setGmlID(String gmlID) {
		this.gmlID = gmlID;
	}

	public String getGmlName() {
		return gmlName;
	}

	public void setGmlName(String gmlName) {
		this.gmlName = gmlName;
	}

	public String getNameCodeSpace() {
		return nameCodeSpace;
	}

	public void setNameCodeSpace(String nameCodeSpace) {
		this.nameCodeSpace = nameCodeSpace;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
