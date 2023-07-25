package com.RegressionTestSelectionTool.xmlfields.differences;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("class")
public class ModifiedClassField {
    public String name;

	public void clean() {
		if(this.name.contains("$")) {
    		this.name = this.name.split("\\$")[0];
    	}
	}
}
