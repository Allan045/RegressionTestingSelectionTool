package com.RegressionTestSelectionTool.xmlfields.smells;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;

@XStreamAlias("file")
public class PmdFile {
  @XStreamAsAttribute
  public String name;

  @XStreamImplicit
  @XStreamAlias("violation")
  public ArrayList<Violation> violations = new ArrayList<>();  

  public ArrayList<Violation> getViolations() {
    return violations;
  }
}
