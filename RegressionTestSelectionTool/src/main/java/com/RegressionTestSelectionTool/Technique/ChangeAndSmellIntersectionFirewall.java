package com.RegressionTestSelectionTool.Technique;

import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Report;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewClassesField;

public class ChangeAndSmellIntersectionFirewall extends SelectionTechnique{

	protected DifferencesField classDifferences;
	protected Set<String> selectedViolations;
	protected Set<String> classesWithViolations;
	
	public ChangeAndSmellIntersectionFirewall(DependenciesField oldestVersionClassDependencies,
			DependenciesField oldestVersionTestsClassDependencies, DependenciesField newestVersionClassDependencies,
			DifferencesField classDifferences, Set<String> selectedViolations, Set<String> classesWithViolations) {
		
		super(SelectionTechniqueEnum.CHANGE_AND_SMELL_INTERSECTION_FIREWALL, 
				oldestVersionClassDependencies, 
				oldestVersionTestsClassDependencies,
				newestVersionClassDependencies);

		this.classDifferences = classDifferences;	
		this.selectedViolations = selectedViolations;
		this.classesWithViolations = classesWithViolations;
		
		long start = System.currentTimeMillis();
		this.selectedClasses = getIntersectionClasses(getSelectedClassesSet(),getSelectedClassesWithViolations());		
		
		
		getSelectedClassesDependenciesRecursive(this.selectedClasses,Boolean.FALSE);
        getSelectedTestCasesUsingClassesInbounds();
        
        this.notSelectedTestClasses = getNotSelectedTestCases(this.selectedTestClasses,this.originalTestSet);
        this.executionTime = System.currentTimeMillis() - start;
        
        createReport();

		System.out.println("Change and Smell Intersection Firewall TestSet: "+selectedTestClasses.toString());
		System.out.println("");
	}
	
	private Set<String> getIntersectionClasses(Set<String> selectedClassesSet,
			Set<String> selectedClassesWithViolations) {

		Set<String> intersection = new HashSet<>(selectedClassesSet);
        intersection.retainAll(selectedClassesWithViolations);
        return intersection;
	}

	private Set<String> getSelectedClassesSet() {
		Set<String> selectedClasses = new HashSet<>();
		
        if (classDifferences.modifiedClassesField != null && classDifferences.modifiedClassesField.classes != null) {
            classDifferences.modifiedClassesField.classes.forEach(modifiedClasses -> {
            	selectedClasses.add(modifiedClasses.name);
            });
        }
        
        if (classDifferences.newClassesField != null && classDifferences.newClassesField.names != null) {
        	classDifferences.newClassesField.names.forEach(newClasses -> {
        		selectedClasses.add(newClasses);
        	});
        	
        }
        return selectedClasses;
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


	//Return Modified Classes Name
		public Set<String> getModifiedClassesNames() {
			HashSet<String> modifiedClassesNames = new HashSet<>();
			
			if(classDifferences != null) {
				DifferencesField classDiff = classDifferences;
				
				if(classDiff.modifiedClassesField != null) {
					
					ModifiedClassesField modifiedClassesField = classDiff.modifiedClassesField;	
					
					if(modifiedClassesField.classes != null) {
						
						HashSet<ModifiedClassField> modifiedClassField  = modifiedClassesField.classes;
						
						for(ModifiedClassField modifiedClassFieldName : modifiedClassField) {
							modifiedClassesNames.add(modifiedClassFieldName.name);			
						}
					}
					
				}
					if(classDiff.newClassesField != null) {
						NewClassesField newClassesField = classDiff.newClassesField;
						for(var aux : newClassesField.names) {
							modifiedClassesNames.add(aux);
						}
					}
				
				
			}
							
			return modifiedClassesNames;
		}
		

		@Override
		protected void createReport() {

			report = new Report(techniqueName,selectedViolations,oldestVersionClassDependencies,
					newestVersionClassDependencies,getModifiedClassesNames(),classesWithViolations,selectedClasses,getSelectedClassesDependenciesList(),
					getOriginalTestSet(),selectedTestClasses,notSelectedTestClasses,executionTime);		
		}

}
