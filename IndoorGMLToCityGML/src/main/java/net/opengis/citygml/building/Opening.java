package net.opengis.citygml.building;

import net.opengis.citygml.core.AbstractFeature;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;

public class Opening extends AbstractFeature {
	private String openingType;
	
	private VOMultiSurface lod4MultiSurface;
	
	public Opening() {
		// TODO Auto-generated constructor stub
	}

	public String getOpeningType() {
		return openingType;
	}

	public void setOpeningType(String openingType) {
		this.openingType = openingType;
	}

	public VOMultiSurface getLod4MultiSurface() {
		return lod4MultiSurface;
	}

	public void setLod4MultiSurface(VOMultiSurface lod4MultiSurface) {
		this.lod4MultiSurface = lod4MultiSurface;
	}

	
}
