package net.opengis.citygml.building;

import java.util.List;

import net.opengis.citygml.core.AbstractFeature;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOSolid;

public class Room extends AbstractFeature {
	private Building building;
	private String clazz;
	private String classCodeSpace;
	private String func;
	private String funcCodeSpace;
	private String usage;
	private String usageCodeSpace;

	private List<IntBuildingInstallation> intBuildingInstallations;
	private List<BoundarySurface> boundarySurfaces;
	
	private VOSolid lod4Solid;
	private VOMultiSurface lod4MultiSurface;

	
	public Room() {
		// TODO Auto-generated constructor stub
	}

	public Building getBuilding() {
		return building;
	}

	public void setBuilding(Building building) {
		this.building = building;
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

	public List<IntBuildingInstallation> getIntBuildingInstallations() {
		return intBuildingInstallations;
	}

	public void setIntBuildingInstallations(
			List<IntBuildingInstallation> intBuildingInstallations) {
		this.intBuildingInstallations = intBuildingInstallations;
	}

	public List<BoundarySurface> getBoundarySurfaces() {
		return boundarySurfaces;
	}

	public void setBoundarySurfaces(List<BoundarySurface> boundarySurfaces) {
		this.boundarySurfaces = boundarySurfaces;
	}

	public VOSolid getLod4Solid() {
		return lod4Solid;
	}

	public void setLod4Solid(VOSolid lod4Solid) {
		this.lod4Solid = lod4Solid;
	}

	public VOMultiSurface getLod4MultiSurface() {
		return lod4MultiSurface;
	}

	public void setLod4MultiSurface(VOMultiSurface lod4MultiSurface) {
		this.lod4MultiSurface = lod4MultiSurface;
	}

}
