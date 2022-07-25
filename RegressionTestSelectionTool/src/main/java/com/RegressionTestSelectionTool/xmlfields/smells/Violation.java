package com.RegressionTestSelectionTool.xmlfields.smells;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("violation")
public class Violation {
  
  @XStreamAsAttribute
  public String beginline;

  @XStreamAsAttribute
  public String endline;

  @XStreamAsAttribute
  public String begincolumn;

  @XStreamAsAttribute
  public String endcolumn;

  @XStreamAsAttribute
  public String rule;

  @XStreamAsAttribute
  public String ruleset;

  @XStreamAlias("package")
  @XStreamAsAttribute
  public String packageField;

  @XStreamAlias("class")
  @XStreamAsAttribute
  public String classField;

  @XStreamAsAttribute
  public String method;

  @XStreamAsAttribute
  public String externalInfoUrl;

  @XStreamAsAttribute
  public String priority;

  public String getClassName() {
    return packageField + "." + classField;
  }
}
