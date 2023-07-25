package com.RegressionTestSelectionTool.Technique;

import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Report;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewClassesField;

public class ClassFirewall extends SelectionTechnique{

	protected DifferencesField classDifferences;
	
	public ClassFirewall(DependenciesField oldestVersionClassDependencies,
			DependenciesField oldestVersionTestsClassDependencies, DependenciesField newestVersionClassDependencies,
			DifferencesField classDifferences) {
		
		super(SelectionTechniqueEnum.CLASS_FIREWALL, oldestVersionClassDependencies, oldestVersionTestsClassDependencies,
				newestVersionClassDependencies);
		
		this.classDifferences = classDifferences;	
		this.selectedClasses = getSelectedClassesSet();
		
		long start = System.currentTimeMillis();
		getSelectedClassesDependenciesRecursive(this.selectedClasses,Boolean.FALSE);
        getSelectedTestCasesUsingClassesInbounds();
        
        this.notSelectedTestClasses = getNotSelectedTestCases(this.selectedTestClasses,this.originalTestSet);
        this.executionTime = System.currentTimeMillis() - start;
        
        createReport();
		
        System.out.println("Class Firewall TestSet: "+this.selectedTestClasses.toString());
        System.out.println("");

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

		report  = new Report(techniqueName,new HashSet<>(),oldestVersionClassDependencies,
				newestVersionClassDependencies,getModifiedClassesNames(),new HashSet<>(),selectedClasses,getSelectedClassesDependenciesList(),
				getOriginalTestSet(),selectedTestClasses,notSelectedTestClasses,executionTime);

	}

}