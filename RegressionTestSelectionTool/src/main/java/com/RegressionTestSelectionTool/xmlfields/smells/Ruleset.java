package com.RegressionTestSelectionTool.xmlfields.smells;

import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("ruleset")
@XStreamConverter(value=ToAttributedValueConverter.class, strings={"rules"})
public class Ruleset {

  @XStreamAlias("name")
  private String name = "PMD Rules TEst";

  @XStreamAlias("xmlns")
  private String xmlns = "http://pmd.sourceforge.net/ruleset/2.0.0";

  @XStreamAlias("xmlns:xsi")
  private String xmlns_xsi = "http://www.w3.org/2001/XMLSchema-instance";

  @XStreamAlias("xsi:schemaLocation")
  private String xsi_schemaLocation = "http://pmd.sourceforge.net/ruleset/2.0.0 http://pmd.sourceforge.net/ruleset_2_0_0.xsd";

  @XStreamAlias("description")
  private String description = "PMD Ruleset for Regression Test Selection Tool";

  @XStreamImplicit(itemFieldName = "rule")
  public ArrayList<Rule> rules = new ArrayList<Rule>();

  public void addBestPraticeRef(String rule) {
    this.rules.add(new Rule(rule));
  }
}
