package net.opengis.citygml.building;

import java.util.List;

import net.opengis.citygml.core.AbstractFeature;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;


public class IntBuildingInstallation extends AbstractFeature {	
	private String clazz;
	private String classCodeSpace;
	private String func;
	private String funcCodeSpace;
	private String usage;
	private String usageCodeSpace;
	private List<BoundarySurface> boundarySurfaces;
	
	private VOMultiSurface lod4Geometry;
	
	public IntBuildingInstallation() {
		// TODO Auto-generated constructor stub
	}

	public String getClazz() {
		return clazz;
	}

	public void setClazz(String clazz) {
		this.clazz = clazz;
	}

	public String getClassCodeSpace() {
		return classCodeSpace;
	}

	public void setClassCodeSpace(String classCodeSpace) {
		this.classCodeSpace = classCodeSpace;
	}

	public String getFunc() {
		return func;
	}

	public void setFunc(String func) {
		this.func = func;
	}

	public String getFuncCodeSpace() {
		return funcCodeSpace;
	}

	public void setFuncCodeSpace(String funcCodeSpace) {
		this.funcCodeSpace = funcCodeSpace;
	}

	public String getUsage() {
		return usage;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public String getUsageCodeSpace() {
		return usageCodeSpace;
	}

	public void setUsageCodeSpace(String usageCodeSpace) {
		this.usageCodeSpace = usageCodeSpace;
	}

	public List<BoundarySurface> getBoundarySurfaces() {
		return boundarySurfaces;
	}

	public void setBoundarySurfaces(List<BoundarySurface> boundarySurfaces) {
		this.boundarySurfaces = boundarySurfaces;
	}

	public VOMultiSurface getLod4Geometry() {
		return lod4Geometry;
	}

	public void setLod4Geometry(VOMultiSurface lod4Geometry) {
		this.lod4Geometry = lod4Geometry;
	}
}
