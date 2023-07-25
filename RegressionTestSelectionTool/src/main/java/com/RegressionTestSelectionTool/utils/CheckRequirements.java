package com.RegressionTestSelectionTool.utils;

public class CheckRequirements {

	public static Boolean checkRequirements(String dependencyFinderHomePath) {
		Boolean requirements = Boolean.FALSE;
		
		if(checkDependencyFinderHomePathRequirement(dependencyFinderHomePath)) {
			requirements = Boolean.TRUE;
		}
		
		return requirements;
	}

	private static Boolean checkDependencyFinderHomePathRequirement(String dependencyFinderHomePath) {
        if (OSGetter.isUnix() || OSGetter.isMac()){
            if (dependencyFinderHomePath == null) {
                throw new NullPointerException("You need to instantiate TestSelector() " +
                        "with DependencyFinder's home absolute path");
            }else {
            	return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }
	
}
