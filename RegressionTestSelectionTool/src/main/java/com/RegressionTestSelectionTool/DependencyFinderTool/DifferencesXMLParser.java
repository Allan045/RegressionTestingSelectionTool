package com.RegressionTestSelectionTool.DependencyFinderTool;

import java.io.File;

import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewClassesField;
import com.thoughtworks.xstream.XStream;

public class DifferencesXMLParser {
	
	public static DifferencesField getDifferencesFromXML(String tempXMLOutputPath) {
    	var xStream = new XStream();
        xStream.ignoreUnknownElements();
        xStream.allowTypesByWildcard(new String[] {
                "com.RegressionTestSelectionTool.**",
        });
        xStream.processAnnotations(DifferencesField.class);
        xStream.processAnnotations(ModifiedClassesField.class);
        xStream.processAnnotations(ModifiedClassField.class);
        xStream.processAnnotations(NewClassesField.class);

        var XMLFile = new File(tempXMLOutputPath);
        DifferencesField differencesField = (DifferencesField) xStream.fromXML(XMLFile);
        
        return differencesField;
    }

}
