package edu.pnu.importexport.retrieve;

import java.util.List;

import javax.xml.bind.JAXBElement;

import net.opengis.citygml.building.BoundarySurface;
import net.opengis.citygml.building.Building;
import net.opengis.citygml.building.IntBuildingInstallation;
import net.opengis.citygml.building.Opening;
import net.opengis.citygml.building.Room;
import net.opengis.citygml.building.v_2_0.AbstractBoundarySurfaceType;
import net.opengis.citygml.building.v_2_0.AbstractBuildingType;
import net.opengis.citygml.building.v_2_0.AbstractOpeningType;
import net.opengis.citygml.building.v_2_0.BoundarySurfacePropertyType;
import net.opengis.citygml.building.v_2_0.BuildingPartPropertyType;
import net.opengis.citygml.building.v_2_0.BuildingPartType;
import net.opengis.citygml.building.v_2_0.CeilingSurfaceType;
import net.opengis.citygml.building.v_2_0.ClosureSurfaceType;
import net.opengis.citygml.building.v_2_0.DoorType;
import net.opengis.citygml.building.v_2_0.FloorSurfaceType;
import net.opengis.citygml.building.v_2_0.GroundSurfaceType;
import net.opengis.citygml.building.v_2_0.IntBuildingInstallationPropertyType;
import net.opengis.citygml.building.v_2_0.IntBuildingInstallationType;
import net.opengis.citygml.building.v_2_0.InteriorRoomPropertyType;
import net.opengis.citygml.building.v_2_0.InteriorWallSurfaceType;
import net.opengis.citygml.building.v_2_0.OpeningPropertyType;
import net.opengis.citygml.building.v_2_0.OuterCeilingSurfaceType;
import net.opengis.citygml.building.v_2_0.OuterFloorSurfaceType;
import net.opengis.citygml.building.v_2_0.RoofSurfaceType;
import net.opengis.citygml.building.v_2_0.RoomType;
import net.opengis.citygml.building.v_2_0.WallSurfaceType;
import net.opengis.citygml.building.v_2_0.WindowType;
import net.opengis.gml.v_3_1_1.GeometryPropertyType;
import net.opengis.gml.v_3_1_1.MultiSurfacePropertyType;
import net.opengis.gml.v_3_1_1.SolidPropertyType;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOMultiSurface;
import net.opengis.indoorgml.v_1_0.vo.spatial.VOSolid;

import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.primitive.Solid;

public class CityGMLJAXBConvertUtil {	
	private static final net.opengis.citygml.building.v_2_0.ObjectFactory bldgOf = new net.opengis.citygml.building.v_2_0.ObjectFactory();

	public static AbstractBuildingType createBuildingType(Building vo) {		
		AbstractBuildingType target = null;
		if(vo.getParent() == null) {
			//BuildingType
			target = bldgOf.createBuildingType();
		} else {
			//BuildingPartType
			target = bldgOf.createBuildingPartType();
		}
		
		//SET GEOMETRY
		VOMultiSurface lod1MultiSurface = vo.getLod1MultiSurface();
		if(lod1MultiSurface != null) {
			MultiSurface multiSurface = lod1MultiSurface.getGeometry();
			MultiSurfacePropertyType multiSurfaceProp = VOJAXBConvertUtil.createMultiSurfacePropertyType(multiSurface);
			target.setLod1MultiSurface(multiSurfaceProp);
		}

		//Rooms
		List<InteriorRoomPropertyType> interiorRoomProps = target.getInteriorRoom();
		List<Room> rooms = vo.getRooms();
		for(Room r : rooms) {
			InteriorRoomPropertyType interiorRoomProp = bldgOf.createInteriorRoomPropertyType();
			RoomType roomType = createRoomType(r);
			interiorRoomProp.setRoom(roomType);
			interiorRoomProps.add(interiorRoomProp);
		}
		
		//BuildingParts
		List<BuildingPartPropertyType> buildingPartsPropertyList = target.getConsistsOfBuildingPart();
		List<Building> buildingParts = vo.getBuildingParts();
		for(Building bp : buildingParts) {
			BuildingPartPropertyType buildingPartsProperty = bldgOf.createBuildingPartPropertyType();
			AbstractBuildingType buildingPartType = createBuildingType(bp);
			buildingPartsProperty.setBuildingPart((BuildingPartType) buildingPartType);
			buildingPartsPropertyList.add(buildingPartsProperty);
		}
		
		return target;
	}
/*
	public static BuildingInstallationType createBuildingInstallationType(BuildingInstallation vo) {
		BuildingInstallationType target = bldgOf.createBuildingInstallationType();
		
		//set CityObjectAttributes
		//VOJAXBConvertUtil.setCityObjectAttributes(target, vo);
		
		/*
		///Attributes 
		String clazz = vo.getClazz();
		String classCodeSpace = vo.getClassCodeSpace();
		JAXBElement<CodeType> clazzType = VOJAXBConvertUtil.createCodeType(clazz, classCodeSpace);
		if(clazzType != null) target.setClazz(clazzType.getValue());
	
		//NOTE : we didn't consider multiple functions
		List<CodeType> functions = target.getFunction();
		String func = vo.getFunc();
		String funcCodeSpace = vo.getFuncCodeSpace();
		JAXBElement<CodeType> funcType = VOJAXBConvertUtil.createCodeType(func, funcCodeSpace);
		if(funcType != null) functions.add(funcType.getValue());
		
		//NOTE : we didn't consider multiple usages
		List<CodeType> usages = target.getUsage();
		String usage = vo.getUsage();
		String usageCodeSpace = vo.getUsageCodeSpace();
		JAXBElement<CodeType> usageType = VOJAXBConvertUtil.createCodeType(usage, usageCodeSpace);
		if(usageType != null) usages.add(usageType.getValue());
		
		
		//GEOMETRIES 
		VOMultiSurface lod4Geometry = vo.getLod4Geometry();
		if(lod4Geometry != null) {
			MultiSurface multiSurface = lod4Geometry.getGeometry();
			GeometryPropertyType geometryProp = VOJAXBConvertUtil.createGeometryProperty(geometry);
			target.setLod4Geometry(geometryProp);
		}
		
		return target;
	}
*/
	public static IntBuildingInstallationType createIntBuildingInstallationType(IntBuildingInstallation vo) {
		IntBuildingInstallationType target = bldgOf.createIntBuildingInstallationType();
		
		//set CityObjectAttributes
		//VOJAXBConvertUtil.setCityObjectAttributes(target, vo);
		
		/*
		//Attributes 
		String clazz = vo.getClazz();
		String classCodeSpace = vo.getClassCodeSpace();
		JAXBElement<CodeType> clazzType = VOJAXBConvertUtil.createCodeType(clazz, classCodeSpace);
		if(clazzType != null) target.setClazz(clazzType.getValue());
	
		//NOTE : we didn't consider multiple functions
		List<CodeType> functions = target.getFunction();
		String func = vo.getFunc();
		String funcCodeSpace = vo.getFuncCodeSpace();
		JAXBElement<CodeType> funcType = VOJAXBConvertUtil.createCodeType(func, funcCodeSpace);
		if(funcType != null) functions.add(funcType.getValue());
		
		//NOTE : we didn't consider multiple usages
		List<CodeType> usages = target.getUsage();
		String usage = vo.getUsage();
		String usageCodeSpace = vo.getUsageCodeSpace();
		JAXBElement<CodeType> usageType = VOJAXBConvertUtil.createCodeType(usage, usageCodeSpace);
		if(usageType != null) usages.add(usageType.getValue());
		*/
		
		//GEOMETRIES 
		VOMultiSurface lod4Geometry = vo.getLod4Geometry();
		if(lod4Geometry != null) {
			MultiSurface multiSurface = lod4Geometry.getGeometry();
			GeometryPropertyType geometryProp = VOJAXBConvertUtil.createGeometryProperty(multiSurface);
			target.setLod4Geometry(geometryProp);
		}
				
		return target;
	}

	public static AbstractBoundarySurfaceType createAbstractBoundarySurfaceType(BoundarySurface vo) {	
			AbstractBoundarySurfaceType target = null;
			
			String boundaryType = vo.getBoundaryType();
			if("RoofSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createRoofSurfaceType();
			} else if("WallSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createWallSurfaceType();
			} else if("GroundSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createGroundSurfaceType();
			} else if("ClosureSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createClosureSurfaceType();
			} else if("FloorSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createFloorSurfaceType();
			} else if("OuterFloorSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createOuterFloorSurfaceType();
			} else if("InteriorWallSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createInteriorWallSurfaceType();
			} else if("CeilingSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createCeilingSurfaceType();
			} else if("OuterCeilingSurface".equalsIgnoreCase(boundaryType)) {
				target = bldgOf.createOuterCeilingSurfaceType();
			} else {
				throw new UnsupportedOperationException("Unsupported BoundaryType :" + boundaryType);
			}
			
			//CityObject attributes
			//VOJAXBConvertUtil.setCityObjectAttributes(target, vo);
			
			//Opening
			List<OpeningPropertyType> openingProps = target.getOpening();
			List<Opening> openings = vo.getOpenings();
			if (openings != null) {
				for(Opening bo : openings) {
					OpeningPropertyType buildingOpening = bldgOf.createOpeningPropertyType();
					AbstractOpeningType openingType = createAbstractOpeningType(bo);
					JAXBElement<? extends AbstractOpeningType> jOpeningType = createJAXBOpeningElement(openingType);
					buildingOpening.setOpening(jOpeningType);
					openingProps.add(buildingOpening);
				}
			}
			
			VOMultiSurface lod4Geometry = vo.getLod4MultiSurface();
			if(lod4Geometry != null) {
				MultiSurface multiSurface = lod4Geometry.getGeometry();
				MultiSurfacePropertyType multiSurfaceProp = VOJAXBConvertUtil.createMultiSurfacePropertyType(multiSurface);
				target.setLod4MultiSurface(multiSurfaceProp);
			}
						
			return target;
	}
	
	
	public static JAXBElement<? extends AbstractBoundarySurfaceType> createJAXBBoundarySurfaceElement(AbstractBoundarySurfaceType target) {
		JAXBElement<? extends AbstractBoundarySurfaceType> jBoundary = null;
		if(target instanceof RoofSurfaceType) {
			jBoundary = bldgOf.createRoofSurface((RoofSurfaceType) target);
		} else if(target instanceof WallSurfaceType) {
			jBoundary = bldgOf.createWallSurface((WallSurfaceType) target);
		} else if(target instanceof GroundSurfaceType) {
			jBoundary = bldgOf.createGroundSurface((GroundSurfaceType) target);
		} else if(target instanceof ClosureSurfaceType) {
			jBoundary = bldgOf.createClosureSurface((ClosureSurfaceType) target);
		} else if(target instanceof FloorSurfaceType) {
			jBoundary = bldgOf.createFloorSurface((FloorSurfaceType) target);
		} else if(target instanceof OuterFloorSurfaceType) {
			jBoundary = bldgOf.createOuterFloorSurface((OuterFloorSurfaceType) target);
		} else if(target instanceof InteriorWallSurfaceType) {
			jBoundary = bldgOf.createInteriorWallSurface((InteriorWallSurfaceType) target);
		} else if(target instanceof CeilingSurfaceType) {
			jBoundary = bldgOf.createCeilingSurface((CeilingSurfaceType) target);
		} else if(target instanceof OuterCeilingSurfaceType) {
			jBoundary = bldgOf.createOuterCeilingSurface((OuterCeilingSurfaceType) target);
		} else {
			throw new UnsupportedOperationException();
		}
		return jBoundary;
	}
	
	
	public static RoomType createRoomType(Room vo) {
		RoomType target = bldgOf.createRoomType();
		
		//VOJAXBConvertUtil.setCityObjectAttributes(target, vo);
		
		/*
		String clazz = vo.getClazz();
		String classCodeSpace = vo.getClassCodeSpace();
		JAXBElement<CodeType> clazzType = VOJAXBConvertUtil.createCodeType(clazz, classCodeSpace);
		if(clazzType != null) target.setClazz(clazzType.getValue());
		
		//NOTE : we didn't consider multiple values
		List<CodeType> functions = target.getFunction();
		String func = vo.getFunc();
		String funcCodeSpace = vo.getFuncCodeSpace();
		JAXBElement<CodeType> funcType = VOJAXBConvertUtil.createCodeType(func, funcCodeSpace);
		if(funcType != null) functions.add(funcType.getValue());
		
		//NOTE : we didn't consider multiple values
		List<CodeType> usages = target.getUsage();
		String usage = vo.getUsage();
		String usageCodeSpace = vo.getUsageCodeSpace();
		JAXBElement<CodeType> usageType = VOJAXBConvertUtil.createCodeType(usage, usageCodeSpace);
		if(usageType != null) usages.add(usageType.getValue());
		*/
		
		//BuildingInstallation
		List<IntBuildingInstallationPropertyType> intBIProps = target.getRoomInstallation();
		List<IntBuildingInstallation> intBuildingInstallations = vo.getIntBuildingInstallations();
		for(IntBuildingInstallation bi : intBuildingInstallations) {
			IntBuildingInstallationPropertyType intBIProp = bldgOf.createIntBuildingInstallationPropertyType();
			IntBuildingInstallationType intBIType = createIntBuildingInstallationType(bi);
			intBIProp.setIntBuildingInstallation(intBIType);
			intBIProps.add(intBIProp);
		}
				
		//BuildingBoundary
		List<BoundarySurfacePropertyType> boundaryPropertyList = target.getCityObjectBoundedBy();
		List<BoundarySurface> boundarys = vo.getBoundarySurfaces();
		for(BoundarySurface bs : boundarys) {
			AbstractBoundarySurfaceType boundary = createAbstractBoundarySurfaceType(bs);
			JAXBElement<? extends AbstractBoundarySurfaceType> jBoundary = createJAXBBoundarySurfaceElement(boundary);
			BoundarySurfacePropertyType bsProp = bldgOf.createBoundarySurfacePropertyType();
			bsProp.setBoundarySurface(jBoundary);
			boundaryPropertyList.add(bsProp);
		}
		
		//Geomtries
		VOSolid lod4Solid = vo.getLod4Solid();
		if(lod4Solid != null) {
			Solid solid = lod4Solid.getGeometry();
			SolidPropertyType solidProp = VOJAXBConvertUtil.createSolidPropertyType(solid);
			target.setLod4Solid(solidProp);
		}
		
		/*
		byte[] lod4MultiSurface = vo.getLod4MultiSurface();
		if(lod4MultiSurface != null) {
			STMultiSurface multiSurface = (STMultiSurface) parser.parseWKB(lod4MultiSurface);
			MultiSurfacePropertyType multiSurfaceProp = VOJAXBConvertUtil.createMultiSurfacePropertyType(multiSurface);
			target.setLod4MultiSurface(multiSurfaceProp);
		}
		*/
		
		return target;
	}
	
	public static AbstractOpeningType createAbstractOpeningType(Opening vo) {
		AbstractOpeningType target;
		
		String openingType = vo.getOpeningType();
		if("DOOR".equalsIgnoreCase(openingType)) {
			target = bldgOf.createDoorType();
		} else if("WINDOW".equalsIgnoreCase(openingType)) {
			target = bldgOf.createWindowType();
		} else {
			throw new UnsupportedOperationException();
		}
		
		//CityObject attributes
		//VOJAXBConvertUtil.setCityObjectAttributes(target, vo);
		
		VOMultiSurface lod4MultiSurface = vo.getLod4MultiSurface();
		if(lod4MultiSurface != null) {
			MultiSurface multiSurface = lod4MultiSurface.getGeometry();
			MultiSurfacePropertyType multiSurfaceProp = VOJAXBConvertUtil.createMultiSurfacePropertyType(multiSurface);
			target.setLod4MultiSurface(multiSurfaceProp);
		}
		
		//add AddressType
		if(target instanceof DoorType) {
			DoorType door = (DoorType) target;
			
			/*
			Address addr = vo.getAddress();
			if(addr != null) {
				List<AddressPropertyType> addresses = door.getAddress();
				AddressPropertyType addrProp = VOJAXBConvertUtil.createAddressType(addr);
				addresses.add(addrProp);
			}
			*/
		}

		return target;
	}
	
	public static JAXBElement<? extends AbstractOpeningType> createJAXBOpeningElement(AbstractOpeningType target) {
		JAXBElement<? extends AbstractOpeningType> jOpening = null;
		if(target instanceof DoorType) {
			jOpening = bldgOf.createDoor((DoorType) target);
		} else if(target instanceof WindowType) {
			jOpening = bldgOf.createWindow((WindowType) target);
		} else {
			throw new UnsupportedOperationException();
		}
		
		return jOpening;
	}
}
