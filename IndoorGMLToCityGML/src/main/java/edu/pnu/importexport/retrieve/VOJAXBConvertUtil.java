/**
 * 
 */
package edu.pnu.importexport.retrieve;

import java.math.BigInteger;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import net.opengis.citygml.building.Building;
import net.opengis.citygml.building.v_2_0.AbstractBuildingType;
import net.opengis.citygml.building.v_2_0.BuildingType;
import net.opengis.citygml.core.AbstractFeature;
import net.opengis.citygml.core.CityObject;
import net.opengis.citygml.v_2_0.AbstractCityObjectType;
import net.opengis.citygml.v_2_0.AddressPropertyType;
import net.opengis.citygml.v_2_0.AddressType;
import net.opengis.citygml.v_2_0.CityModelType;
import net.opengis.gml.v_3_1_1.AbstractGMLType;
import net.opengis.gml.v_3_1_1.AbstractGeometryType;
import net.opengis.gml.v_3_1_1.AbstractRingPropertyType;
import net.opengis.gml.v_3_1_1.AbstractRingType;
import net.opengis.gml.v_3_1_1.AbstractSolidType;
import net.opengis.gml.v_3_1_1.AbstractSurfaceType;
import net.opengis.gml.v_3_1_1.BoundingShapeType;
import net.opengis.gml.v_3_1_1.CodeType;
import net.opengis.gml.v_3_1_1.CompositeSurfaceType;
import net.opengis.gml.v_3_1_1.DirectPositionListType;
import net.opengis.gml.v_3_1_1.DirectPositionType;
import net.opengis.gml.v_3_1_1.EnvelopeType;
import net.opengis.gml.v_3_1_1.FeaturePropertyType;
import net.opengis.gml.v_3_1_1.GeometryPropertyType;
import net.opengis.gml.v_3_1_1.LengthType;
import net.opengis.gml.v_3_1_1.LinearRingType;
import net.opengis.gml.v_3_1_1.MeasureOrNullListType;
import net.opengis.gml.v_3_1_1.MultiSurfacePropertyType;
import net.opengis.gml.v_3_1_1.MultiSurfaceType;
import net.opengis.gml.v_3_1_1.PointPropertyType;
import net.opengis.gml.v_3_1_1.PointType;
import net.opengis.gml.v_3_1_1.PolygonPropertyType;
import net.opengis.gml.v_3_1_1.PolygonType;
import net.opengis.gml.v_3_1_1.SolidPropertyType;
import net.opengis.gml.v_3_1_1.SolidType;
import net.opengis.gml.v_3_1_1.StringOrRefType;
import net.opengis.gml.v_3_1_1.SurfacePropertyType;
import oasis.names.tc.ciq.xsdschema.xal.AddressDetails.Address;

import org.geotools.geometry.iso.coordinate.EnvelopeImpl;
import org.geotools.geometry.iso.primitive.RingImplUnsafe;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.geometry.Geometry;
import org.opengis.geometry.aggregate.MultiSurface;
import org.opengis.geometry.coordinate.Polygon;
import org.opengis.geometry.primitive.OrientableSurface;
import org.opengis.geometry.primitive.Primitive;
import org.opengis.geometry.primitive.Ring;
import org.opengis.geometry.primitive.Shell;
import org.opengis.geometry.primitive.Solid;
import org.opengis.geometry.primitive.SolidBoundary;
import org.opengis.geometry.primitive.Surface;
import org.opengis.geometry.primitive.SurfaceBoundary;

public class VOJAXBConvertUtil {
	private static final net.opengis.citygml.building.v_2_0.ObjectFactory bldgOf = new net.opengis.citygml.building.v_2_0.ObjectFactory();
	
	private static final net.opengis.citygml.v_2_0.ObjectFactory coreOf = new net.opengis.citygml.v_2_0.ObjectFactory();
	private static final net.opengis.gml.v_3_1_1.ObjectFactory gmlOf = new net.opengis.gml.v_3_1_1.ObjectFactory();
	private static final oasis.names.tc.ciq.xsdschema.xal.ObjectFactory xalOf = new oasis.names.tc.ciq.xsdschema.xal.ObjectFactory();
	
	public static CityModelType createCityModelType(Building building) {
		CityModelType target = coreOf.createCityModelType();
		
		//Envelope
		Envelope envelope = createEnvelope(building.getLod1MultiSurface().getGeometry());
		if(envelope != null) {
			EnvelopeType envelopeType = gmlOf.createEnvelopeType();

			String srsName = "EPSG::4326";
			if(srsName != null) {
				envelopeType.setSrsName(srsName); 
			}
			Integer srsDimension = envelope.getDimension();
			if(srsDimension != null) {
				envelopeType.setSrsDimension( new BigInteger(String.valueOf(srsDimension)));
			}

			//Geometry
			DirectPosition lowerCorner = envelope.getLowerCorner();
			DirectPositionType lowerDirectPosition = createDirectPositionType(lowerCorner);
			envelopeType.setLowerCorner(lowerDirectPosition);

			DirectPosition upperCorner = envelope.getUpperCorner();
			DirectPositionType upperDirectPosition = createDirectPositionType(upperCorner);
			envelopeType.setUpperCorner(upperDirectPosition);

			//JAXBElement
			JAXBElement<EnvelopeType> jEnvelope = gmlOf.createEnvelope(envelopeType);
			BoundingShapeType boundingShapeType = gmlOf.createBoundingShapeType();
			boundingShapeType.setEnvelope(jEnvelope);
			target.setBoundedBy(boundingShapeType);	
		}

		//CityObjects
		List<JAXBElement<FeaturePropertyType>> jFeatureMember = target.getFeatureMember();
		FeaturePropertyType fp = gmlOf.createFeaturePropertyType();
		
		JAXBElement<? extends AbstractCityObjectType> jCityObject = null;
		AbstractBuildingType bType = CityGMLJAXBConvertUtil.createBuildingType(building);
		jCityObject = bldgOf.createBuilding((BuildingType) bType);
		fp.setFeature(jCityObject);
		
		JAXBElement<FeaturePropertyType> cityObjectMember = coreOf.createCityObjectMember(fp);
		jFeatureMember.add(cityObjectMember);
		
		return target;
	}
	/*
	public static CityModelType createCityModelType(CityModel vo) {
		CityModelType target = coreOf.createCityModelType();
		
		setAbstractGML(target, vo);
		
		//Envelope
		Envelope envelope = vo.getEnvelope();
		if(envelope != null) {
			EnvelopeType envelopeType = gmlOf.createEnvelopeType();

			String srsName = envelope.getSrsName();
			if(srsName != null) {
				envelopeType.setSrsName(srsName); 
			}
			Integer srsDimension = envelope.getSrsDimension();
			if(srsDimension != null) {
				envelopeType.setSrsDimension( new BigInteger(String.valueOf(srsDimension)));
			}

			//Geometry
			byte[] lowerCorner = envelope.getLowerCorner();
			STPoint lower = (STPoint) parser.parseWKB(lowerCorner);
			DirectPositionType lowerDirectPosition = createDirectPositionType(lower);
			envelopeType.setLowerCorner(lowerDirectPosition);

			byte[] upperCorner = envelope.getUpperCorner();
			STPoint upper = (STPoint) parser.parseWKB(upperCorner);
			DirectPositionType upperDirectPosition = createDirectPositionType(upper);
			envelopeType.setUpperCorner(upperDirectPosition);

			//JAXBElement
			JAXBElement<EnvelopeType> jEnvelope = gmlOf.createEnvelope(envelopeType);
			BoundingShapeType boundingShapeType = gmlOf.createBoundingShapeType();
			boundingShapeType.setEnvelope(jEnvelope);
			target.setBoundedBy(boundingShapeType);	
		}

		//CityObjects
		List<? extends CityObject> cityObjects = vo.getCityObjects();
		if(cityObjects != null) {
			List<JAXBElement<FeaturePropertyType>> jFeatureMember = target.getFeatureMember();
			for(CityObject co : cityObjects) {
				FeaturePropertyType fp = gmlOf.createFeaturePropertyType();
				
				JAXBElement<? extends AbstractCityObjectType> jCityObject = createCityObjectType(co);
				fp.setFeature(jCityObject);
				
				JAXBElement<FeaturePropertyType> cityObjectMember = coreOf.createCityObjectMember(fp);
				jFeatureMember.add(cityObjectMember);
			}
		}
		
		return target;
	}
	
	public static JAXBElement<? extends AbstractCityObjectType> createCityObjectType(CityObject co) {
		
		JAXBElement<? extends AbstractCityObjectType> jCityObject = null;
		if(co instanceof Building) {
			Building b = (Building) co;
			AbstractBuildingType bType = CityGMLJAXBConvertUtil.createBuildingType(b);
			jCityObject = bldgOf.createBuilding((BuildingType) bType);
		}
		else if(co instanceof BoundarySurface) {
			BoundarySurface vo = (BoundarySurface) co;
			net.opengis.citygml.building.v_2_0.AbstractBoundarySurfaceType bType = CityGMLJAXBConvertUtil.createAbstractBoundarySurfaceType(vo);
			jCityObject = bldgOf.createBoundarySurface(bType);
		}
		
		
		return jCityObject;
	}
	*/
	
	private static Envelope createEnvelope(MultiSurface multiSurface) {
		Set<OrientableSurface> surfaces = multiSurface.getElements();
		EnvelopeImpl envelope = null;
		
		for (OrientableSurface surface : surfaces) {
			if (envelope == null) {
				envelope = (EnvelopeImpl) surface.getEnvelope();
			} else {
				envelope.expand(surface.getEnvelope());
			}
		}
		
		return envelope;
	}
	public static AddressPropertyType createAddressType(Address vo) {
		
		AddressType addressType = coreOf.createAddressType();
		/*
		XalAddressPropertyType xalProp = createAddressDetails(vo);
		addressType.setXalAddress(xalProp);
		
		byte[] multiPointWKB = vo.getMultiPoint();
		if(multiPointWKB != null) {
			STMultiPoint multiPoint = (STMultiPoint) parser.parseWKB(multiPointWKB);
			MultiPointPropertyType multiPointProp = VOJAXBConvertUtil.createMultiPointPropertyType(multiPoint);
			addressType.setMultiPoint(multiPointProp);
		}
		*/
		AddressPropertyType addressProp = coreOf.createAddressPropertyType();
		addressProp.setAddress(addressType);
		
		return addressProp;
	}
	/*
	public static XalAddressPropertyType createAddressDetails(Address vo) {
		
		
		//Country
		AddressDetails.Country country = xalOf.createAddressDetailsCountry();
		List<CountryNameElement> countryName = country.getCountryName();
		CountryNameElement cn = xalOf.createCountryNameElement();
		cn.setContent(vo.getCountry());
		countryName.add(cn);
		
		//LocalityElement
		LocalityElement locality = xalOf.createLocalityElement();
		List<LocalityName> localityName = locality.getLocalityName();
		LocalityName ln = xalOf.createLocalityElementLocalityName();
		ln.setContent(vo.getLocalityName());
		ln.setType(vo.getLocalityType());
		localityName.add(ln);
		
		//ThoroughfareElement
		ThoroughfareElement thoroughfare = xalOf.createThoroughfareElement();
		thoroughfare.setType(vo.getThoroughfareType());
		
		List<ThoroughfareNameType> thoroughfareName = thoroughfare.getThoroughfareName();
		ThoroughfareNameType tf = xalOf.createThoroughfareNameType();
		tf.setContent(vo.getThoroughfareName());
		thoroughfareName.add(tf);
		
		List<Object> throughfareNumber = thoroughfare.getThoroughfareNumberOrThoroughfareNumberRange();
		ThoroughfareNumberElement tfne = xalOf.createThoroughfareNumberElement();
		tfne.setContent(vo.getThoroughfareNumber());
		throughfareNumber.add(tfne);
		
		locality.setThoroughfare(thoroughfare);
		country.setLocality(locality);
		
		AddressDetails addressDetails = xalOf.createAddressDetails();
		addressDetails.setCountry(country);
		
		XalAddressPropertyType xalProps = coreOf.createXalAddressPropertyType();
		xalProps.setAddressDetails(addressDetails);
		
		return xalProps;
	}
	*/
	public static void setAbstractGML(AbstractGMLType target, AbstractFeature vo) {
		//GML ID
		String gmlId = vo.getGmlID();
		target.setId(gmlId);

		//Name
		String name = vo.getGmlName();
		String nameCodeSpace = vo.getNameCodeSpace();
		List<JAXBElement<CodeType>> names = target.getName();
		//TODO : consider multiple name 
		JAXBElement<CodeType> jName = createCodeType(name, nameCodeSpace);
		names.add(jName);

		//TODO : consider StringOrRefType
		String description = vo.getDescription();
		if(description != null) {
			StringOrRefType descriptionType = gmlOf.createStringOrRefType();
			descriptionType.setValue(description);
			target.setDescription(descriptionType);
		}
	}
	
	public static void setCityObjectAttributes(AbstractCityObjectType target, AbstractFeature vo) {
		
		setAbstractGML(target, vo);
		/*
		Date creationDate = vo.getCreationDate();
		if(creationDate != null) {
			XMLGregorianCalendar jCreationDate = createXMLGregorianCalendar(creationDate);
			target.setCreationDate(jCreationDate);
		}

		Date terminationDate = vo.getTerminationDate();
		if(terminationDate != null) {
			XMLGregorianCalendar jTerminationDate = createXMLGregorianCalendar(terminationDate);
			target.setCreationDate(jTerminationDate);
		}
		*/
	}
	

	public static GeometryPropertyType createGeometryProperty(Geometry g) {
		GeometryPropertyType target = gmlOf.createGeometryPropertyType();
		JAXBElement<? extends AbstractGeometryType> geometry = createAbstractGeometryType(g);
		if(geometry != null) {
			target.setGeometry(geometry);
		}
		return target;
	}

	private static JAXBElement<? extends AbstractGeometryType> createAbstractGeometryType(final Geometry g) {

		Geometry geom = g;
		JAXBElement<? extends AbstractGeometryType> jGeometry = null;

		Class<?> geomClass = geom.getClass();		
		if(MultiSurface.class.isAssignableFrom(geomClass)) {
			MultiSurfaceType target = createMultiSurfaceType((MultiSurface) geom);
			jGeometry = gmlOf.createMultiSurface((MultiSurfaceType) target);
		}
		else if(Solid.class.isAssignableFrom(geomClass)) {
			jGeometry = createAbstractSolidType((Solid) geom);
		}
		else if(Surface.class.isAssignableFrom(geomClass)) {
			jGeometry = createAbstractSurfaceType((Surface) geom);
		}		
		else {
			throw new UnsupportedOperationException("createAbstractGeometryType : unknown geometry (" + geom.toString() + ")");
		}

		return jGeometry;
	}

	public static DirectPositionListType createDirectPositionListType(DirectPosition[] positions) {
		DirectPositionListType target = gmlOf.createDirectPositionListType();

		List<Double> dList = target.getValue();
		for(int i = 0 ; i < positions.length; i++) {
			dList.add(positions[i].getOrdinate(0));
			dList.add(positions[i].getOrdinate(1));
			dList.add(positions[i].getOrdinate(2));
		}

		return target;
	}

	public static DirectPositionType createDirectPositionType(DirectPosition position) {
		DirectPositionType target = gmlOf.createDirectPositionType();

		List<Double> dList = target.getValue();
		dList.add(position.getOrdinate(0));
		dList.add(position.getOrdinate(1));
		dList.add(position.getOrdinate(2));

		return target;
	}

	public static SolidPropertyType createSolidPropertyType(Solid g) {
		SolidPropertyType target = gmlOf.createSolidPropertyType();
		JAXBElement<? extends AbstractSolidType> solid = createAbstractSolidType(g);
		if(solid != null) {
			target.setSolid(solid);
		}

		return target;
	}

	public static JAXBElement<? extends AbstractSolidType> createAbstractSolidType(Solid g) {
		AbstractSolidType target = null;
		//TODO : always SolidType in this time.
		target = gmlOf.createSolidType();

		SolidType solid = (SolidType) target;

		//Exterior, Interior
		SolidBoundary boundary = g.getBoundary();
		Shell exterior = boundary.getExterior();
		Shell[] interiors = boundary.getInteriors();
		
		if(exterior != null) {
			SurfacePropertyType surfaceProperty = createSurfacePropertyType(exterior);
			if(surfaceProperty != null) {
				solid.setExterior(surfaceProperty);
			}
		}

		JAXBElement<? extends AbstractSolidType> jSolid = null;
		if(target instanceof SolidType) {
			jSolid = gmlOf.createSolid((SolidType) target);
		} else {
			throw new UnsupportedOperationException();
		}

		return jSolid;
	}
/*
	public static CurvePropertyType createCurvePropertyType(LineString g) {
		CurvePropertyType target = gmlOf.createCurvePropertyType();

		JAXBElement<? extends AbstractCurveType> curve = createAbstractCurveType(g);
		if(curve != null) {
			target.setCurve(curve);
		}

		return target;
	}

	public static JAXBElement<? extends AbstractCurveType> createAbstractCurveType(LineString g) {
		AbstractCurveType target = null;

		STLineString g2 = (STLineString) g;


		STPoint[] points = new STPoint[g2.numPoints()];
		for(int i = 0; i < g2.numPoints(); i++) {
			points[i] = g2.PointN(i);
		}
		DirectPositionListType dpl = createDirectPositionListType(points);

		target = gmlOf.createLineStringType();
		LineStringType l = (LineStringType) target;
		l.setPosList(dpl);

		JAXBElement<? extends AbstractCurveType> jCurve = gmlOf.createLineString((LineStringType) target);
		return jCurve;
	}
*/
	public static SurfacePropertyType createSurfacePropertyType(Object g) {
		SurfacePropertyType target = gmlOf.createSurfacePropertyType();

		JAXBElement<? extends AbstractSurfaceType> surface = createAbstractSurfaceType(g);
		if(surface != null) {
			target.setSurface(surface);
		}

		return target;
	}

	public static PolygonPropertyType createPolygonPropertyType(Polygon g) {
		PolygonPropertyType target = gmlOf.createPolygonPropertyType();

		JAXBElement<? extends AbstractSurfaceType> surface = createAbstractSurfaceType(g);
		if(surface != null) {
			target.setPolygon((PolygonType) surface.getValue());
		}

		return target;
	}
	
	public static JAXBElement<? extends AbstractSurfaceType> createAbstractSurfaceType(Object g) {
		AbstractSurfaceType target = null;

		if (g instanceof Shell) {
			target = gmlOf.createCompositeSurfaceType();
			CompositeSurfaceType s = (CompositeSurfaceType) target;

			List<SurfacePropertyType> surfaceMember = s.getSurfaceMember();

			Shell shell = (Shell) g;
			Collection<? extends Primitive> elements = shell.getElements();
			for(Primitive element : elements) {
				Surface surface = (Surface) element;
				SurfacePropertyType surfaceProp = createSurfacePropertyType(surface);
				surfaceMember.add(surfaceProp);
			}
		} else if(g instanceof Surface) {
			target = gmlOf.createPolygonType();
			PolygonType s = (PolygonType) target;

			Surface surface = (Surface) g;
			SurfaceBoundary boundary = surface.getBoundary();
			//Exterior
			Ring exterior = boundary.getExterior();
			if(exterior != null) {
				AbstractRingPropertyType value = createAbstractRingPropertyType(exterior);
				JAXBElement<AbstractRingPropertyType> exteriorProp = gmlOf.createExterior(value);
				s.setExterior(exteriorProp);
			}

			//Interior
			List<Ring> interiors = boundary.getInteriors();
			if (interiors != null) {
				List<JAXBElement<AbstractRingPropertyType>> interiorProps = s.getInterior();
				for(Ring interior : interiors) {
					AbstractRingPropertyType value = createAbstractRingPropertyType(interior);
					JAXBElement<AbstractRingPropertyType> interiorProp = gmlOf.createInterior(value);
					interiorProps.add(interiorProp);
				}
			}
		}

		JAXBElement<? extends AbstractSurfaceType> jSurface = null;
		if(target instanceof CompositeSurfaceType) {
			jSurface = gmlOf.createCompositeSurface((CompositeSurfaceType) target);
		} else if(target instanceof PolygonType) {
			jSurface = gmlOf.createPolygon((PolygonType) target);
		}
		else {
			throw new UnsupportedOperationException();
		}

		return jSurface;
	}

	public static AbstractRingPropertyType createAbstractRingPropertyType(Ring ring) {
		AbstractRingPropertyType target = gmlOf.createAbstractRingPropertyType();
		JAXBElement<? extends AbstractRingType> aRing = createRingType(ring);
		target.setRing(aRing);
		return target;
	}

	public static JAXBElement<? extends AbstractRingType> createRingType(Ring ring) {

		AbstractRingType target = null;
		
		//NOTE : citygml only use linearRing
		target = gmlOf.createLinearRingType();
		LinearRingType lRing = (LinearRingType) target;

		DirectPositionListType directPosition = gmlOf.createDirectPositionListType();
		List<Double> dList = directPosition.getValue();
		List<DirectPosition> positions = ((RingImplUnsafe) ring).asDirectPositions();
		for(DirectPosition position : positions) {
			dList.add(position.getOrdinate(0));
			dList.add(position.getOrdinate(1));
			dList.add(position.getOrdinate(2));
		}
		lRing.setPosList(directPosition);

		JAXBElement<? extends AbstractRingType> jRing = null;
		if(target instanceof LinearRingType) {
			jRing = gmlOf.createLinearRing((LinearRingType) target);
		}

		return jRing;
	}

	public static PointPropertyType createPointPropertyType(DirectPosition p) {
		PointPropertyType target = gmlOf.createPointPropertyType();
		PointType point = createPointType(p);
		target.setPoint(point);
		return target;
	}

	public static PointType createPointType(DirectPosition p) {
		PointType target = gmlOf.createPointType();

		DirectPositionType dpType = createDirectPositionType(p);
		target.setPos(dpType);

		return target;
	}

	public static MultiSurfacePropertyType createMultiSurfacePropertyType(MultiSurface g) {
		MultiSurfacePropertyType target = gmlOf.createMultiSurfacePropertyType();

		MultiSurfaceType multiSurface = createMultiSurfaceType(g);
		if(multiSurface != null) {
			target.setMultiSurface(multiSurface);
		}
		return target;
	}

	public static MultiSurfaceType createMultiSurfaceType(MultiSurface g) {
		MultiSurfaceType target = gmlOf.createMultiSurfaceType();

		List<SurfacePropertyType> surfaceMember = target.getSurfaceMember();
		Set<OrientableSurface> surfaces = g.getElements();
		for(OrientableSurface s : surfaces) {
			SurfacePropertyType surfaceProp = createSurfacePropertyType(s);
			if(surfaceProp != null) surfaceMember.add(surfaceProp);
		}

		return target;
	}

	public static MeasureOrNullListType createMeasureOrNullListType(List<String> v, String uom) {
		if(v == null || v.size() < 1) return null;

		MeasureOrNullListType storeysHeightsAbove = gmlOf.createMeasureOrNullListType();

		List<String> values = storeysHeightsAbove.getValue();
		if(v != null) values.addAll(v);

		storeysHeightsAbove.setUom(uom);

		return storeysHeightsAbove;
	}

	public static JAXBElement<CodeType> createCodeType(String value, String codeSpace) {
		if(value == null && codeSpace == null) {
			return null;
		}

		CodeType codeType = gmlOf.createCodeType();
		codeType.setValue(value);
		codeType.setCodeSpace(codeSpace);

		JAXBElement<CodeType> jCodeType = gmlOf.createName(codeType);
		return jCodeType;
	}

	public static LengthType createLengthType(Double value, String uom) {
		if(value == null && uom == null) {
			return null;
		}

		LengthType mh = gmlOf.createLengthType();
		mh.setValue(value);
		mh.setUom(uom);

		return mh;
	}

	public static XMLGregorianCalendar createXMLGregorianCalendar(Date date) {

		GregorianCalendar gregory = new GregorianCalendar();
		gregory.setTime(date);

		XMLGregorianCalendar calendar;
		try {
			calendar = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(
							gregory);
			calendar.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			calendar.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
			calendar.setDay(DatatypeConstants.FIELD_UNDEFINED);
			calendar.setMonth(DatatypeConstants.FIELD_UNDEFINED);
			return calendar;
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
}
