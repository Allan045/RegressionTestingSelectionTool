package com.RegressionTestSelectionTool.Technique;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Report;
import com.RegressionTestSelectionTool.xmlfields.dependencies.ClassField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.OutboundField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.PackageField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewClassesField;

public abstract class SelectionTechnique {

	protected SelectionTechniqueEnum techniqueName;
	protected DependenciesField oldestVersionClassDependencies;	
	protected DependenciesField oldestVersionTestsClassDependencies;
	protected DependenciesField newestVersionClassDependencies;

	protected Set<ClassField> selectedClassesDependencies = new HashSet<>();
	protected Set<String> notSelectedTestClasses = new HashSet<>();
	protected Set<String> selectedTestClasses = new HashSet<>();
	protected Set<String> selectedClasses = new HashSet<>();
	protected Set<String> originalTestSet = new HashSet<>();

	protected long executionTime;
	protected Report report;
	protected abstract void createReport();
	

	public SelectionTechnique(SelectionTechniqueEnum techniqueName, DependenciesField oldestVersionClassDependencies,
			DependenciesField oldestVersionTestsClassDependencies, DependenciesField newestVersionClassDependencies) {
		super();
		this.techniqueName = techniqueName;
		this.oldestVersionClassDependencies = oldestVersionClassDependencies;
		this.oldestVersionTestsClassDependencies = oldestVersionTestsClassDependencies;
		this.newestVersionClassDependencies = newestVersionClassDependencies;
		this.originalTestSet = getTestcasesNames(oldestVersionTestsClassDependencies);
		
		
	}

	//Return the selected Test Cases ('.java') from Oldest Project     
    protected void getSelectedTestCasesUsingClassesInbounds() {
   	   	
    	ArrayList<String> selectedClassesList = selectedClassesDependenciesToList();

    	oldestVersionTestsClassDependencies.packages
    	.forEach(testPackage -> testPackage.classes
    			.forEach(test -> 
    			{
    				boolean selectTest = false;
    				for(OutboundField outbound : test.outbounds) {
    					if(selectedClassesList.contains(outbound.text)) {
    						selectTest = true;
    					}
    				}
    				
    				
    				if (selectTest) {
    					selectedTestClasses.add(filterSubClasses(test.name));	//Discard Intern Test Classes
        			}
    			}));
    			

    }
    
    //Filter Inner Classes Names
    protected String filterSubClasses(String originalString) {
    	String filteredString = originalString.split("\\$")[0];
    	return filteredString;
    }
    
    protected ArrayList<String> selectedClassesDependenciesToList() {
    	
    	ArrayList<String> selectedClasseslist = new ArrayList<String>();
    	selectedClassesDependencies.forEach(dependency -> selectedClasseslist.add(dependency.name));
		return selectedClasseslist;
	}
    
    protected void getSelectedClassesDependenciesRecursive(Set<String> selectedClassesToGetInbounds, boolean stopAtFirstLevel) {

 		selectedClassesToGetInbounds.forEach(selectedClass -> {
     		newestVersionClassDependencies.packages.forEach(packages -> packages.classes.forEach(classes -> {
 	       		
     			if(classes.name.equals(selectedClass) && (!selectedClassesDependencies.contains(classes))) {
     				selectedClassesDependencies.add(classes);
     				
     				if(!(stopAtFirstLevel) && (classes.inbounds != null)) {

 						classes.inbounds.forEach(inbounds -> {getSelectedClassesDependenciesRecursive(Set.of(inbounds.text),stopAtFirstLevel);});
 					}
     				
     			}
 			
     		}));
     		
     	});
 	
    }

	
    
	protected Set<String> getSelectedClassesDependenciesList(){		
		Set<String> list = new HashSet<>();		
		selectedClassesDependencies.forEach(classField -> list.add(classField.name));		
		return list;
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

	public Set<String> getOriginalTestSet(){
    	return getTestcasesNames(oldestVersionTestsClassDependencies);
    }
	
	
	protected Set<String> getNotSelectedTestCases(Set<String> selectedTestClasses, Set<String> originalTestSet) {
    	Set<String> notSelectedTestSet = new HashSet<String>(originalTestSet);
    	  	
    	notSelectedTestSet.removeAll(selectedTestClasses);
    	
    	//System.out.println(selectedTestClasses.size()+" "+selectedTestClasses.toString());
    	//System.out.println(originalTestSet.size()+" "+originalTestSet.toString());
    	//System.out.println(notSelectedTestSet.size()+" "+notSelectedTestSet.toString());
    	
    	
    	return notSelectedTestSet;
	}
	
	public Report getTechniqueReport() {
		return report;
	}
	
	
}
