package com.RegressionTestSelectionTool.xmlfields.smells;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("property")
public class Property {

  public Property(String name, String value) {
    this.name = name;
    this.value = value;
  }

  @XStreamAsAttribute
  @XStreamAlias("name")
  private String name;

  @XStreamAsAttribute
  @XStreamAlias("value")
  private String value;

}
