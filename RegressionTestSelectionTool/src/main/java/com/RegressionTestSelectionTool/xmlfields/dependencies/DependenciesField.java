package com.RegressionTestSelectionTool.xmlfields.dependencies;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.HashSet;

@XStreamAlias("dependencies")
public class DependenciesField{
    @XStreamImplicit
    @XStreamAlias("package")
    public HashSet<PackageField> packages = new HashSet<>();
    
    public void cleanPackages() {
    	for(PackageField packageField : this.packages) {
    		packageField.cleanClasses();
    	}
    }
    
}
