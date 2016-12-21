package net.opengis.citygml.core;

import java.util.ArrayList;

import org.opengis.geometry.Envelope;

public class CityModel extends AbstractFeature {
	private Envelope envelope;	
	private ArrayList<CityObject> cityObjects;
	
	public CityModel() {
		// TODO Auto-generated constructor stub
	}

	public Envelope getEnvelope() {
		return envelope;
	}

	public void setEnvelope(Envelope envelope) {
		this.envelope = envelope;
	}

	public ArrayList<CityObject> getCityObjects() {
		return cityObjects;
	}

	public void setCityObjects(ArrayList<CityObject> cityObjects) {
		this.cityObjects = cityObjects;
	}

}
