package com.RegressionTestSelectionTool.xmlfields.differences;
import java.util.HashSet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("new-classes")
public class NewPackagesField {
    @XStreamImplicit
    @XStreamAlias("name")
    public HashSet<String> names = new HashSet<>();
}
