package com.vividsolutions.jts.index.strtree;

import com.vividsolutions.jts.geom.Geometry;

public class NewGeometryItemDistance extends GeometryItemDistance {

  public double distance(ItemBoundable item1, ItemBoundable item2) {
    Geometry g1 = (Geometry) item1.getItem();
    Geometry g2 = (Geometry) item2.getItem();
    
    if (g1.equals(g2) || g1.intersects(g2)) return Double.MAX_VALUE;
        
    return g1.distance(g2);    
  }
}

