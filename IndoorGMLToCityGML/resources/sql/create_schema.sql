/*---------------------------------*/
/* GML */
/*---------------------------------*/

CREATE TABLE Point(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_POINTZ,
	PRIMARY KEY (ID)
);

CREATE TABLE MultiPoint(
	ID	INTEGER NOT NULL AUTO_INCREMENT,
	GMLID	VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_POINTZ,
	PRIMARY KEY (ID)	
);


CREATE TABLE PointMember(
	MULTIPOINT_ID			INTEGER NOT NULL,
	POINT_ID				INTEGER NOT NULL,

	PRIMARY KEY (MULTIPOINT_ID, POINT_ID),
	FOREIGN KEY (MULTIPOINT_ID) REFERENCES MultiPoint(ID),
	FOREIGN KEY (POINT_ID) REFERENCES Point(ID)
);


CREATE TABLE Curve(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	PARENT_ID		INTEGER,
	ROOT_ID			INTEGER,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	IS_COMPOSITE	TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_GEOMCOLLECTIONZ,
	LINESTRING_GEOM	ST_LINESTRINGZ,

	PRIMARY KEY (ID),
	FOREIGN KEY (PARENT_ID) REFERENCES Curve(ID),
	FOREIGN KEY (ROOT_ID)	REFERENCES Curve(ID)
);


CREATE TABLE MultiCurve(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_MULTICURVEZ,

	PRIMARY KEY (ID)	
);


CREATE TABLE CurveMember(
	MULTICURVE_ID			INTEGER NOT NULL,
	CURVE_ID				INTEGER NOT NULL,

	PRIMARY KEY (MULTICURVE_ID, CURVE_ID),
	FOREIGN KEY (MULTICURVE_ID) REFERENCES MultiCurve(ID),
	FOREIGN KEY (CURVE_ID) REFERENCES Curve(ID)
);


CREATE TABLE Surface(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	PARENT_ID		INTEGER,
	ROOT_ID			INTEGER,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	IS_REVERSE		TINYINT,
	IS_COMPOSITE	TINYINT,
	IS_TRIANGULATED	TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_GEOMCOLLECTIONZ,
	POLYGON_GEOM	ST_POLYGONZ,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (PARENT_ID) REFERENCES Surface(ID),
	FOREIGN KEY (ROOT_ID)	REFERENCES Surface(ID)
);

CREATE TABLE MultiSurface(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	/* IT WILL BE CHANGED TO GEOMETRY TYPE */
	GEOM			ST_MULTISURFACEZ,
	
	PRIMARY KEY (ID)	
);


CREATE TABLE SurfaceMember(
	MULTISURFACE_ID			INTEGER NOT NULL,
	SURFACE_ID				INTEGER NOT NULL,

	PRIMARY KEY (MULTISURFACE_ID, SURFACE_ID),
	FOREIGN KEY (MULTISURFACE_ID) REFERENCES MultiSurface(ID),
	FOREIGN KEY (SURFACE_ID) REFERENCES Surface(ID)
);


CREATE TABLE Solid(
	ID				INTEGER NOT NULL AUTO_INCREMENT,
	GMLID			VARCHAR(256),
	SRS_NAME		VARCHAR(100),
	SRS_DIMENSION	INTEGER,
	IS_XLINK		TINYINT,
	EXTERIOR		INTEGER,
	GEOM			ST_SOLID,

	PRIMARY KEY (ID),
	FOREIGN KEY (EXTERIOR) REFERENCES Surface(ID)
);

CREATE TABLE SolidInterior(
	SOLID_ID			INTEGER NOT NULL,
	SURFACE_ID			INTEGER NOT NULL,

	PRIMARY KEY (SOLID_ID, SURFACE_ID),
	FOREIGN KEY (SOLID_ID) REFERENCES Solid(ID),
	FOREIGN KEY (SURFACE_ID) REFERENCES Surface(ID)
);

/*---------------------------------*/
/* IndoorGML Core Module */
/*---------------------------------*/

CREATE TABLE IndoorObject(
	ID					INTEGER NOT NULL AUTO_INCREMENT,
	INDOOROBJECT_TYPE 	VARCHAR(50),
	GMLID 				VARCHAR(256), 
	NAME		 		VARCHAR(1000),
	NAME_CODESPACE 		VARCHAR(2000), 
	DESCRIPTION 		VARCHAR(2000),

	PRIMARY KEY (ID),
	UNIQUE (GMLID)
);

CREATE TABLE IndoorFeature(
	ID						INTEGER NOT NULL,
	STRING_ID				VARCHAR(256),

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID)
);

CREATE TABLE PrimalSpaceFeature(
	ID							INTEGER NOT NULL,
	INDOORFEATRE_ID 			INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (INDOORFEATRE_ID) REFERENCES IndoorFeature(ID)
);

CREATE TABLE CellSpace(
	ID							INTEGER NOT NULL,
	PRIMALSPACEFEATURE_ID 		INTEGER,
	GEOMETRY2D					INTEGER,
	GEOMETRY3D					INTEGER,
	DUALITY						INTEGER,
	NAVIGABLETYPE				VARCHAR(30),
	CLAZZ						VARCHAR(256),
	CLASS_CODESPACE				VARCHAR(1000),
	FUNC						VARCHAR(256),
	FUNC_CODESPACE				VARCHAR(1000),
	USAGE						VARCHAR(256),
	USAGE_CODESPACE				VARCHAR(1000),
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (PRIMALSPACEFEATURE_ID) REFERENCES PrimalSpaceFeature(ID),
	FOREIGN KEY (GEOMETRY2D) REFERENCES Surface(ID),
	FOREIGN KEY (GEOMETRY3D) REFERENCES Solid(ID),
	FOREIGN KEY (DUALITY) REFERENCES IndoorObject(ID)
);

CREATE TABLE PartialBoundedByMapping(
	CELLSPACE_ID				INTEGER,
	PARTIALBOUNDEDBY			INTEGER,
	
	FOREIGN KEY (CELLSPACE_ID) REFERENCES CellSpace(ID),
	FOREIGN KEY (PARTIALBOUNDEDBY) REFERENCES IndoorObject(ID)
);

CREATE TABLE CellSpaceBoundary( 
	ID							INTEGER NOT NULL,
	PRIMALSPACEFEATURE_ID 		INTEGER,
	GEOMETRY2D					INTEGER,
	GEOMETRY3D					INTEGER,
	DUALITY						INTEGER,
	NAVIGABLETYPE				VARCHAR(30),
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (PRIMALSPACEFEATURE_ID) REFERENCES PrimalSpaceFeature(ID),
	FOREIGN KEY (GEOMETRY2D) REFERENCES Curve(ID),
	FOREIGN KEY (GEOMETRY3D) REFERENCES Surface(ID),
	FOREIGN KEY (DUALITY) REFERENCES IndoorObject(ID)
);

CREATE TABLE ExternalReference(
	CELLSPACE_ID				INTEGER,
	CELLSPACEBOUNDARY_ID		INTEGER,
	INFORMATIONSYSTEM			VARCHAR(1000),
	NAME						VARCHAR(256),
	URI							VARCHAR(1000),
	
	FOREIGN KEY (CELLSPACE_ID) REFERENCES CellSpace(ID),
	FOREIGN KEY (CELLSPACEBOUNDARY_ID) REFERENCES CellSpaceBoundary(ID)
);

CREATE TABLE MultiLayeredGraph(
	ID							INTEGER NOT NULL,
	INDOORFEATRE_ID 			INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (INDOORFEATRE_ID) REFERENCES IndoorFeature(ID)
);

CREATE TABLE InterEdges(
	ID							INTEGER NOT NULL,
	MULTILAYEREDGRAPH_ID 		INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (MULTILAYEREDGRAPH_ID) REFERENCES MultiLayeredGraph(ID)
);

CREATE TABLE InterLayerConnection(
	ID							INTEGER NOT NULL,
	INTEREDGES_ID		 		INTEGER,
	TYPEOFTOPOECPRESSION		VARCHAR(50),
	COMMENT						VARCHAR(2000),
	INTERCONNECTS_A				INTEGER,
	INTERCONNECTS_B				INTEGER,
	CONNECTEDLAYERS_A			INTEGER,
	CONNECTEDLAYERS_B			INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (INTEREDGES_ID) REFERENCES InterEdges(ID),
	FOREIGN KEY (INTERCONNECTS_A) REFERENCES IndoorObject(ID),
	FOREIGN KEY (INTERCONNECTS_B) REFERENCES IndoorObject(ID),
	FOREIGN KEY (CONNECTEDLAYERS_A) REFERENCES IndoorObject(ID),
	FOREIGN KEY (CONNECTEDLAYERS_B) REFERENCES IndoorObject(ID)
);

CREATE TABLE SpaceLayers(
	ID							INTEGER NOT NULL,
	MULTILAYEREDGRAPH_ID 		INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (MULTILAYEREDGRAPH_ID) REFERENCES MultiLayeredGraph(ID)
);

CREATE TABLE SpaceLayer(
	ID							INTEGER NOT NULL,
	SPACELAYERS_ID		 		INTEGER,
	CLAZZ						VARCHAR(256),
	CLASS_CODESPACE				VARCHAR(1000),
	FUNC						VARCHAR(256),
	FUNC_CODESPACE				VARCHAR(1000),
	USAGE						VARCHAR(256),
	USAGE_CODESPACE				VARCHAR(1000),
	CREATION_DATE				DATE,
	TERMINATION_DATE			DATE,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (SPACELAYERS_ID) REFERENCES SpaceLayers(ID)
);

CREATE TABLE Nodes(
	ID							INTEGER NOT NULL,
	SPACELAYER_ID 				INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (SPACELAYER_ID) REFERENCES SpaceLayer(ID)
);

CREATE TABLE State(
	ID							INTEGER NOT NULL,
	NODES_ID 					INTEGER,
	GEOMETRY					INTEGER,
	DUALITY						INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (NODES_ID) REFERENCES Nodes(ID),
	FOREIGN KEY (GEOMETRY) REFERENCES Point(ID),
	FOREIGN KEY (DUALITY) REFERENCES IndoorObject(ID)
);

CREATE TABLE Edges(
	ID							INTEGER NOT NULL,
	SPACELAYER_ID 				INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (SPACELAYER_ID) REFERENCES SpaceLayer(ID)
);

CREATE TABLE Transition(
	ID							INTEGER NOT NULL,
	EDGES_ID 					INTEGER,
	GEOMETRY					INTEGER,
	DUALITY						INTEGER,
	WEIGHT						DOUBLE,
	CONNECT_A					INTEGER,
	CONNECT_B					INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (EDGES_ID) REFERENCES Edges(ID),
	FOREIGN KEY (GEOMETRY) REFERENCES Curve(ID),
	FOREIGN KEY (DUALITY) REFERENCES IndoorObject(ID),
	FOREIGN KEY (CONNECT_A) REFERENCES IndoorObject(ID),
	FOREIGN KEY (CONNECT_B) REFERENCES IndoorObject(ID)
);

/*---------------------------------*/
/* IndoorGML Navigation Module */
/*---------------------------------*/

CREATE TABLE NavigableSpace(
	ID							INTEGER NOT NULL,
	CLAZZ						VARCHAR(256),
	CLASS_CODESPACE				VARCHAR(1000),
	FUNC						VARCHAR(256),
	FUNC_CODESPACE				VARCHAR(1000),
	USAGE						VARCHAR(256),
	USAGE_CODESPACE				VARCHAR(1000),

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID)
);

CREATE TABLE Route(
	ID							INTEGER NOT NULL,
	STRING_ID					VARCHAR(256),
	STARTROUTENODE_ID			INTEGER,
	ENDROUTENODE_ID				INTEGER,

	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (STARTROUTENODE_ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (ENDROUTENODE_ID) REFERENCES IndoorObject(ID)
);

CREATE TABLE RouteNodes(
	ID							INTEGER NOT NULL,
	ROUTE_ID					INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (ROUTE_ID) REFERENCES Route(ID)
);

CREATE TABLE RouteNode(
	ID							INTEGER NOT NULL,
	ROUTE_ID					INTEGER,
	ROUTENODES_ID				INTEGER,
	REFERENCEDSTATE_ID			INTEGER,
	GEOMETRY					INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (ROUTE_ID) REFERENCES Route(ID),
	FOREIGN KEY (ROUTENODES_ID) REFERENCES RouteNodes(ID),
	FOREIGN KEY (REFERENCEDSTATE_ID) REFERENCES State(ID),
	FOREIGN KEY (GEOMETRY) REFERENCES Point(ID)
);

CREATE TABLE Path(
	ID							INTEGER NOT NULL,
	ROUTE_ID					INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (ROUTE_ID) REFERENCES Route(ID)
);

CREATE TABLE RouteSegment(
	ID							INTEGER NOT NULL,
	PATH_ID						INTEGER,
	REFERENCEDTRANSITION_ID		INTEGER,
	WEIGHT						DOUBLE,
	GEOMETRY					INTEGER,
	CONNECT_A					INTEGER,
	CONNECT_B					INTEGER,
	
	PRIMARY KEY (ID),
	FOREIGN KEY (ID) REFERENCES IndoorObject(ID),
	FOREIGN KEY (PATH_ID) REFERENCES Path(ID),
	FOREIGN KEY (REFERENCEDTRANSITION_ID) REFERENCES Transition(ID),
	FOREIGN KEY (GEOMETRY) REFERENCES Curve(ID),
	FOREIGN KEY (CONNECT_A) REFERENCES IndoorObject(ID),
	FOREIGN KEY (CONNECT_B) REFERENCES IndoorObject(ID)
);


