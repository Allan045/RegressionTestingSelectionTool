
package com.RegressionTestSelectionTool.xmlfields.smells;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("properties")
public class Properties {
  @XStreamAlias("properties")
  public ArrayList<Property> list = new ArrayList<>();
}
