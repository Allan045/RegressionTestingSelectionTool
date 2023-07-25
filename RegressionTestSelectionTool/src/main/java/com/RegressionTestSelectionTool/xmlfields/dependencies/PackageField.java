package com.RegressionTestSelectionTool.xmlfields.dependencies;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.HashSet;

@XStreamAlias("package")
public class PackageField {
    @XStreamAsAttribute
    public String confirmed;

    public String name;

    @XStreamImplicit
    @XStreamAlias("class")
    public HashSet<ClassField> classes = new HashSet<>();
    
    public void cleanClasses() {
    	

    		for(ClassField classField : this.classes) {
        		classField.cleanBounds();
        	}
    		
    		cleanDuplicateClasses();
    		
    		for(ClassField classField : this.classes) {
        		classField.cleanBounds();
        	}
    }
    	
    
    
    public Boolean isConfirmed() {
		if(this.confirmed.equals("yes")) {
			return Boolean.TRUE;
		}else {
			return Boolean.FALSE;
		}
    }
    
    
    private void cleanDuplicateClasses() {
    	//TO-DO
    	//Falta unificar inbounds e outbounds
    	
    	HashSet<ClassField> classFieldsToRemove = new HashSet<ClassField>(); 
    	
    	if(this.classes != null) {
    		
    		for(ClassField classField : this.classes) {
        		String name = classField.name;
        		int count = 0;
        		
        		for(ClassField classField2 : this.classes) {
        			String name2 = classField2.name;
        			
        			if(name2.equals(name)) {
        				count++;
        				
        				if(count > 1) {
        					
        					if(classField2.inbounds != null) {
        						if(classField.inbounds != null) {
        							classField.inbounds.addAll(classField2.inbounds);
        						}else {
        							classField.inbounds = new HashSet<InboundField>();
        							classField.inbounds.addAll(classField2.inbounds);
        						}
        					}
        					
        					
        					if(classField2.outbounds != null)
        						if(classField.outbounds != null) {
        							classField.outbounds.addAll(classField2.outbounds);
        						}else {
        							classField.outbounds = new HashSet<OutboundField>();
        							classField.outbounds.addAll(classField2.outbounds);
        						}
        					
        					classFieldsToRemove.add(classField2);
            				count = 1;
            			}
        			}
        		}	
        	}	
        	
    		this.classes.removeAll(classFieldsToRemove);
    	}
    }
}
