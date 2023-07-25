package com.RegressionTestSelectionTool.Technique;

import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Report;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;

public class CodeSmellBased extends SelectionTechnique {

	protected Set<String> selectedViolations;
	protected Set<String> classesWithViolations;	
	
	public CodeSmellBased(DependenciesField oldestVersionClassDependencies,
			DependenciesField oldestVersionTestsClassDependencies, 
			DependenciesField newestVersionClassDependencies,
			Set<String> selectedViolations,
			Set<String> classesWithViolations) {
		
		super(SelectionTechniqueEnum.CODE_SMELL_BASED, 
				oldestVersionClassDependencies, 
				oldestVersionTestsClassDependencies,
				newestVersionClassDependencies);
		
		this.selectedViolations = selectedViolations;
		this.classesWithViolations = classesWithViolations;
		
		long start = System.currentTimeMillis();
		this.selectedClasses = getSelectedClassesWithViolations();
		
        getSelectedClassesDependenciesRecursive(this.selectedClasses,Boolean.TRUE);
        getSelectedTestCasesUsingClassesInbounds();
        
        this.notSelectedTestClasses = getNotSelectedTestCases(this.selectedTestClasses,this.originalTestSet);
        this.executionTime = System.currentTimeMillis() - start;
        
        createReport();
        
		System.out.println("Code Smell Based TestSet: "+this.selectedTestClasses.toString());
		System.out.println("");
		
	}
	
    private Set<String> getSelectedClassesWithViolations() {
    	Set<String> selectedClasses = new HashSet<>();
   	
	   	if (classesWithViolations != null && !classesWithViolations.isEmpty()) {
	           this.classesWithViolations.forEach(classWithViolation -> {              
	               selectedClasses.add(classWithViolation);    
	           });
	       }

	    return selectedClasses;
   }

	@Override
	protected void createReport() {
		report = new Report(techniqueName,selectedViolations,oldestVersionClassDependencies,
				newestVersionClassDependencies,new HashSet<>(),classesWithViolations,selectedClasses,getSelectedClassesDependenciesList(),
				getOriginalTestSet(),selectedTestClasses,notSelectedTestClasses,executionTime);		

		
	}

}
