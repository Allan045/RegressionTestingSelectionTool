package com.RegressionTestSelectionTool.DependencyFinderTool;

import java.io.File;

import com.RegressionTestSelectionTool.xmlfields.dependencies.ClassField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.InboundField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.OutboundField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.PackageField;
import com.thoughtworks.xstream.XStream;

public class DependencyXMLParser {

	
	public static DependenciesField getDependenciesFromXML(String path) {
        var xStream = new XStream();
        xStream.ignoreUnknownElements();
        
        xStream.allowTypesByWildcard(new String[] {
                "com.RegressionTestSelectionTool.**",
        });
        
        xStream.processAnnotations(DependenciesField.class);
        xStream.processAnnotations(PackageField.class);
        xStream.processAnnotations(ClassField.class);
        xStream.processAnnotations(InboundField.class);
        xStream.processAnnotations(OutboundField.class);

        var XMLFile = new File(path);
        DependenciesField dependenciesField = ((DependenciesField) xStream.fromXML(XMLFile)); 
        
        return dependenciesField;
    }
}
