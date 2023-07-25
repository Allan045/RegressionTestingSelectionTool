package com.RegressionTestSelectionTool.DependencyFinderTool;

import com.RegressionTestSelectionTool.utils.CLICommandExecuter;
import com.RegressionTestSelectionTool.utils.OSGetter;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class JarJarDiff {
	
	private final String dependencyFinderHomePath;
	private final String projectOldestVersionPath;
	private final String projectNewestVersionPath;
	private final String diffXMLPath;
	
	private DifferencesField classDifferences;
	


	public JarJarDiff(String dependencyFinderHomePath, String projectOldestVersionPath, 
			String projectNewestVersionPath, String diffXMLPath) {
		super();
		this.dependencyFinderHomePath = dependencyFinderHomePath;
		this.projectOldestVersionPath = projectOldestVersionPath;
		this.projectNewestVersionPath = projectNewestVersionPath;
		this.diffXMLPath = diffXMLPath;
		
		try {
			setClassesDifferencesBetweenInitialAndModifiedVersion(dependencyFinderHomePath, 
					projectOldestVersionPath, projectNewestVersionPath, diffXMLPath);
		} catch (NotImplementedException e) {
			e.printStackTrace();
		}
	}


	public DifferencesField getClassDifferences() {
		return classDifferences;
	}


	private void setClassesDifferencesBetweenInitialAndModifiedVersion(String dependencyFinderHomePath,
    		String projectOldestVersionPath,
    		String projectNewestVersionPath,
    		String diffXMLPath) throws NotImplementedException {
        
    	runJarJarDiff(dependencyFinderHomePath,projectOldestVersionPath,projectNewestVersionPath,diffXMLPath);
        classDifferences =  DifferencesXMLParser.getDifferencesFromXML(diffXMLPath);
        
        //Clean classDifferences
        classDifferences = removeTestcasesFromDifferencesField(classDifferences);
        classDifferences.cleanDiferrencesField();
  
    }
    
    //Remove classes with "test" from a DifferencesField object
    private DifferencesField removeTestcasesFromDifferencesField(DifferencesField differences) {
    	
    	DifferencesField differencesAux = new DifferencesField();
    	
    	if(differences != null) {
    		differencesAux = differences;
    		
    		if (differencesAux.newClassesField != null) {
                if (differencesAux.newClassesField.names != null) {
                	differencesAux.newClassesField.names.removeIf(name -> name.toLowerCase().contains("test"));
                }
            }
    		
            if (differencesAux.modifiedClassesField != null) {
                if (differencesAux.modifiedClassesField.classes != null) {
                	differencesAux.modifiedClassesField.classes.removeIf(classField -> classField.name.toLowerCase().contains("test"));
                }
            }  
    	}

        return differencesAux;
    }

    private void runJarJarDiff(String dependencyFinderHomePath,String initialProjectVersionDirectoryPath,String modifiedProjectVersionDirectoryPath, String tempXMLOutputPath) throws NotImplementedException {
        String JarJarDiffCommand;
        JarJarDiffCommand = getJarJarDiffCommand(dependencyFinderHomePath,initialProjectVersionDirectoryPath,modifiedProjectVersionDirectoryPath,tempXMLOutputPath);
        CLICommandExecuter.Execute(JarJarDiffCommand);
        
    }
    
    //tempXMLOutputPaths.get(2)
    private String getJarJarDiffCommand(String dependencyFinderHomePath,String initialProjectVersionDirectoryPath,String modifiedProjectVersionDirectoryPath, String tempXMLOutputPath) throws NotImplementedException {
        String CLICommand;
        if (OSGetter.isWindows()) {
            CLICommand = "cmd.exe /c JarJarDiff -code -out " + tempXMLOutputPath
                    + " -old-label Initial -old " + initialProjectVersionDirectoryPath
                    + " -new-label Modified -new " + modifiedProjectVersionDirectoryPath;
        } else if (OSGetter.isUnix() || OSGetter.isMac()) {
            CLICommand = dependencyFinderHomePath + "/bin/JarJarDiff -code -out " + tempXMLOutputPath
                    + " -old-label Initial -old " + initialProjectVersionDirectoryPath
                    + " -new-label Modified -new " + modifiedProjectVersionDirectoryPath;
        }
        else {
            throw new NotImplementedException("Your Operational System is not supported yet");
        }
        return CLICommand;
    }
}
