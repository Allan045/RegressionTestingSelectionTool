package com.RegressionTestSelectionTool.DependencyFinderTool;

import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.utils.CLICommandExecuter;
import com.RegressionTestSelectionTool.utils.OSGetter;
import com.RegressionTestSelectionTool.xmlfields.dependencies.ClassField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.PackageField;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class DependencyFinder implements Runnable{

	private final String projectPath;
	private final String dependencyExtractorXMLOutputFilePath;
	private final String c2cXMLOutputFilePath;
	private final Boolean processTestSet;
	
	
	private final String dependencyFinderHomePath;
	private DependenciesField classDependencies;
	private DependenciesField testClassDependencies;
	private Set<String> originalTestSet = new HashSet<>();
	
	
	public DependencyFinder(String dependencyFinderHomePath, String projectPath, String dependencyExtractorXMLOutputFilePath, String c2cXMLOutputFilePath, Boolean processTestSet) {
		super();
		this.dependencyFinderHomePath = dependencyFinderHomePath;
		this.projectPath = projectPath;
		this.dependencyExtractorXMLOutputFilePath = dependencyExtractorXMLOutputFilePath;
		this.c2cXMLOutputFilePath = c2cXMLOutputFilePath;
		this.processTestSet = processTestSet;
	}


	public DependenciesField getClassDependencies() {
		return classDependencies;
	}

	
	public DependenciesField getTestClassDependencies() {
		return testClassDependencies;
	}
	
    public Set<String> getOriginalTestSet(){
    	return originalTestSet;
    }

	
	private void setClassesDependenciesForOldestVersion(String projectPath,String dependencyExtractorXMLOutputFilePath,String c2cXMLOutputFilePath,Boolean processTestSet) throws NotImplementedException {
        runDependencyExtractorForInitialVersion(dependencyExtractorXMLOutputFilePath,projectPath);
        runClassToClassForInitialVersion(dependencyExtractorXMLOutputFilePath,c2cXMLOutputFilePath);

        classDependencies = DependencyXMLParser.getDependenciesFromXML(c2cXMLOutputFilePath);
        classDependencies = filterNotConfirmedAndTestcases(classDependencies);
        classDependencies.cleanPackages();
     
        if(processTestSet) {
        	testClassDependencies = DependencyXMLParser.getDependenciesFromXML(c2cXMLOutputFilePath);
        	testClassDependencies = removeNotConfirmedClassDependencies(testClassDependencies);
            testClassDependencies = removeClassFromOldestVersionDependencies(testClassDependencies);
            testClassDependencies.cleanPackages();
            originalTestSet = getTestcasesNames(testClassDependencies);
        }
    }
	
    //Return all Test Cases Names found in Initial Project after the selection process
    private Set<String> getTestcasesNames(DependenciesField dependenciesField){
    	
    	Set<String> testCasesNames = new HashSet<String>();
    	
    	if(dependenciesField != null) {
    		for(PackageField packages : dependenciesField.packages) {
        		for(ClassField classes : packages.classes) {
        			if(classes.confirmed.equals("yes") && classes.name.toLowerCase().contains("test")) {
        				String TestcaseName = filterSubClasses(classes.name);
        				testCasesNames.add(TestcaseName);
        			}   				    			
        		}
        	}
    	}
    	
    	
    	
    	return testCasesNames;
    }
    
    
    //Filter Inner Classes Names
    private String filterSubClasses(String originalString) {
    	String filteredString = originalString.split("\\$")[0];
    	return filteredString;
    }
    

	//Filters
    private DependenciesField filterNotConfirmedAndTestcases(DependenciesField dependencies) {
    	DependenciesField dependenciesAux = new DependenciesField();
    	
    	if(dependencies.packages != null) {
    		dependenciesAux.packages = new HashSet<PackageField>(dependencies.packages);
    		
	    	//Remove Not Confirmed Classes        
	    	dependenciesAux = removeNotConfirmedClassDependencies(dependenciesAux);       
	    	
	    	//Filter Test Cases from Modified Project to not interfere
	    	dependenciesAux = removeTestcases(dependenciesAux); 
    	}
    	
		return dependenciesAux;
	}
	
    //Remove Not Confirmed Dependencies
    private DependenciesField removeNotConfirmedClassDependencies(DependenciesField dependencies) { 
    	DependenciesField dependenciesAux = new DependenciesField();
  
			if (dependencies.packages != null) {   		
				
				dependenciesAux.packages = new HashSet<PackageField>(dependencies.packages);
	    		dependenciesAux.packages.removeIf(packageField -> packageField.confirmed.equals("no"));
	    		dependenciesAux.packages.forEach(packageField -> { 
	    			
	    			if(packageField.classes != null) {
	    				packageField.classes.removeIf(classField -> classField.confirmed.equals("no"));
	        			packageField.classes.forEach(classField -> { 
	        				
	        				if (classField.inbounds != null)
	        					classField.inbounds.removeIf(inboundField -> inboundField.confirmed.equals("no")); 
	        				
	        				if (classField.outbounds != null)
	        					classField.outbounds.removeIf(outboundField -> outboundField.confirmed.equals("no")); 
	        			}); 
	    			}
	    		}); 
	    	} 	
			
		return dependenciesAux;  	
    }

    
    //Remove Test Cases from Dependencies
    private DependenciesField removeTestcases(DependenciesField dependencies) {
    	DependenciesField dependenciesAux = new DependenciesField();
    			
			if (dependencies.packages != null) {
				dependenciesAux.packages = new HashSet<PackageField>(dependencies.packages);
				
				//Remove only classes with "test" in the name
	        	dependenciesAux.packages.forEach(packageField ->
	                    
	                    packageField.classes.removeIf(classField ->
	                            classField.name.toLowerCase().contains("test"))
	            );
	        }
	
        return dependenciesAux;
    
    }
    
    
    //Remove Classes from TestCases Dependencies
    private DependenciesField removeClassFromOldestVersionDependencies(DependenciesField unfilteredTestcasesDependencies) {
    	DependenciesField dependenciesAux = new DependenciesField();
		
			if (unfilteredTestcasesDependencies.packages != null) {
				dependenciesAux.packages = new HashSet<PackageField>(unfilteredTestcasesDependencies.packages);
				
				//Get only Testcases
	        	dependenciesAux.packages.forEach(packageField ->
	                    packageField.classes.removeIf(classField ->
	                            !classField.name.toLowerCase().contains("test"))
	            );
	        }
		     
		return dependenciesAux;
	}
    
	
	private void runDependencyExtractorForInitialVersion(String dependencyExtractorXMLOutputFilePath, String projectPath) throws NotImplementedException {
        String dependencyExtractorCommand = getDependencyExtractorCommand(dependencyExtractorXMLOutputFilePath,projectPath);
        CLICommandExecuter.Execute(dependencyExtractorCommand);
    }

	
    private String getDependencyExtractorCommand(String dependencyExtractorXMLOutputFilePath,String projectPath) throws NotImplementedException {
        String CLICommand;
        if (OSGetter.isWindows()) {
            CLICommand = "cmd.exe /c DependencyExtractor -xml -out " + dependencyExtractorXMLOutputFilePath + " " + projectPath;
        } else if (OSGetter.isUnix() || OSGetter.isMac()) {
            CLICommand = dependencyFinderHomePath + "/bin/DependencyExtractor -xml -out " + dependencyExtractorXMLOutputFilePath + " " + projectPath;
        }
        else {
            throw new NotImplementedException("Your Operational System is not supported yet");
        }
        return CLICommand;
    }
    
    
    private String getClassToClassCommand(String dependencyExtractorXMLOutputFilePath, String c2cXMLOutputFilePath) throws NotImplementedException {
        String CLICommand;
        if (OSGetter.isWindows()) {
            CLICommand = "cmd.exe /c c2c " + dependencyExtractorXMLOutputFilePath + " -xml -out " + c2cXMLOutputFilePath;
        } else if (OSGetter.isUnix() || OSGetter.isMac()) {
            CLICommand = dependencyFinderHomePath + "/bin/c2c " + dependencyExtractorXMLOutputFilePath + " -xml -out " + c2cXMLOutputFilePath;
        }
        else {
            throw new NotImplementedException("Your Operational System is not supported yet");
        }
        return CLICommand;
    }
	
    private void runClassToClassForInitialVersion(String dependencyExtractorXMLOutputFilePath, String c2cXMLOutputFilePath) throws NotImplementedException {
		String classToClassCommand = getClassToClassCommand(dependencyExtractorXMLOutputFilePath,c2cXMLOutputFilePath);
        CLICommandExecuter.Execute(classToClassCommand);
    }


	@Override
	public void run() {
		try {
			setClassesDependenciesForOldestVersion(projectPath,dependencyExtractorXMLOutputFilePath,c2cXMLOutputFilePath,processTestSet);
		} catch (NotImplementedException e) {
			e.printStackTrace();
		}
		
	}
    
}
