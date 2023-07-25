package com.RegressionTestSelectionTool.xmlfields.differences;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.HashSet;

@XStreamAlias("modified-classes")
public class ModifiedClassesField {
    @XStreamImplicit
    @XStreamAlias("class")
    public HashSet<ModifiedClassField> classes = new HashSet<>();

	public void clean() {
		if(this.classes != null) {
			for(ModifiedClassField modifiedClassField : this.classes) {
				modifiedClassField.clean();
			}
			
			cleanDuplicates();
		}
		
	}

	private void cleanDuplicates() {
		HashSet<ModifiedClassField> modifiedClassFieldToRemove = new HashSet<ModifiedClassField>(); 
    	
    	if(this.classes != null) {
    		
    		for(ModifiedClassField modifiedClassField : this.classes) {
        		String name = modifiedClassField.name;
        		int count = 0;
        		
        		for(ModifiedClassField modifiedClassField2 : this.classes) {
        			String name2 = modifiedClassField2.name;
        			
        			if(name2.equals(name)) {
        				count++;
        				
        				if(count > 1) {
        					modifiedClassFieldToRemove.add(modifiedClassField2);
            				count = 1;
            			}
        			}
        		}	
        	}	
        	
    		this.classes.removeAll(modifiedClassFieldToRemove);
    	}
	}
}
