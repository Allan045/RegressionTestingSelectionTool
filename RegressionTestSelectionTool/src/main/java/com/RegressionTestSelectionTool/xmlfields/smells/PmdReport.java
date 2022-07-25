package com.RegressionTestSelectionTool.xmlfields.smells;
import java.util.ArrayList;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("pmd")
public class PmdReport {

  @XStreamImplicit
  @XStreamAlias("file")
  public ArrayList<PmdFile> file; 

  public ArrayList<Violation> getViolations() {
    var violations = new ArrayList<Violation>();

    for (var file : this.file) {
      violations.addAll(file.getViolations());
    }

    return violations;
  } 
}
