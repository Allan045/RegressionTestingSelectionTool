package com.RegressionTestSelectionTool.Technique;

import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Report;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;

public class CodeSmellFirewall extends SelectionTechnique {

	protected Set<String> selectedViolations;
	protected Set<String> classesWithViolations;
	
	public CodeSmellFirewall(DependenciesField oldestVersionClassDependencies,
			DependenciesField oldestVersionTestsClassDependencies, 
			DependenciesField newestVersionClassDependencies,
			Set<String> selectedViolations,
			Set<String> classesWithViolations) {
		
		super(SelectionTechniqueEnum.CODE_SMELL_FIREWALL, 
				oldestVersionClassDependencies, 
				oldestVersionTestsClassDependencies,
				newestVersionClassDependencies);
		
		this.selectedViolations = selectedViolations;
		this.classesWithViolations = classesWithViolations;
		
		long start = System.currentTimeMillis();
		this.selectedClasses = getSelectedClassesWithViolations();
		

        getSelectedClassesDependenciesRecursive(this.selectedClasses,Boolean.FALSE);
        getSelectedTestCasesUsingClassesInbounds();
        
        this.notSelectedTestClasses = getNotSelectedTestCases(this.selectedTestClasses,this.originalTestSet);
        this.executionTime = System.currentTimeMillis() - start;
        
        createReport();
		
		System.out.println("Code Smell Firewall TestSet: "+selectedTestClasses.toString());
		System.out.println("");
		
	}
	
	 private Set<String> getSelectedClassesWithViolations() {
	   	 Set<String> selectedClasses = new HashSet<>();
	   	
	   	if (classesWithViolations != null && !classesWithViolations.isEmpty()) {
	           classesWithViolations.forEach(classWithViolation -> {              
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
