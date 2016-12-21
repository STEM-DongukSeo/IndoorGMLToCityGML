package net.opengis.citygml.building;

import java.util.List;

import net.opengis.citygml.core.AbstractFeature;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;

import org.opengis.geometry.aggregate.MultiSurface;

public class BoundarySurface extends AbstractFeature {
	private String boundaryType;	
	private List<Opening> openings;	
	private VOMultiSurface lod4MultiSurface;
	
	public BoundarySurface() {
		// TODO Auto-generated constructor stub
	}

	public String getBoundaryType() {
		return boundaryType;
	}

	public void setBoundaryType(String boundaryType) {
		this.boundaryType = boundaryType;
	}

	public List<Opening> getOpenings() {
		return openings;
	}

	public void setOpenings(List<Opening> openings) {
		this.openings = openings;
	}

	public VOMultiSurface getLod4MultiSurface() {
		return lod4MultiSurface;
	}

	public void setLod4MultiSurface(VOMultiSurface lod4MultiSurface) {
		this.lod4MultiSurface = lod4MultiSurface;
	}
}
