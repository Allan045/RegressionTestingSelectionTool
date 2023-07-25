package com.RegressionTestSelectionTool.xmlfields.differences;
import java.util.HashSet;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
@XStreamAlias("new-classes")
public class NewClassesField {
    @XStreamImplicit
    @XStreamAlias("name")
    public HashSet<String> names = new HashSet<>();

	public void clean() {
		
		HashSet<String> newNames = new HashSet<String>(); 
		
		if(this.names != null) {
			
			for(String name : this.names) {
				if(name.contains("$")) {
					newNames.add(name.split("\\$")[0]);
		    	}else {
		    		newNames.add(name);
		    	}
			}
			
			this.names = newNames;
		}	
	}
	
}
