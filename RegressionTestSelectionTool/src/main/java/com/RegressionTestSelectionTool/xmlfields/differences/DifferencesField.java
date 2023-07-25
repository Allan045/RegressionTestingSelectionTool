package com.RegressionTestSelectionTool.xmlfields.differences;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("differences")
public class DifferencesField {
    public String name;
    @XStreamAlias("modified-classes")
    public ModifiedClassesField modifiedClassesField;

    @XStreamAlias("new-classes")
    public NewClassesField newClassesField;

    @XStreamAlias("new-packages")
    public NewPackagesField newPackagesField;
    
    public void cleanDiferrencesField() {
    	
    	if(this.newClassesField != null)
    		this.newClassesField.clean();
    	
    	if(this.modifiedClassesField != null)
    		this.modifiedClassesField.clean();
    	
    	if(this.newPackagesField != null)
    		this.newPackagesField.names.clear();
    	
    }
}
