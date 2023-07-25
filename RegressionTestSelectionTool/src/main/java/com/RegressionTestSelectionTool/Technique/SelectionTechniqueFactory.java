package com.RegressionTestSelectionTool.Technique;

import java.util.Set;

import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;

public class SelectionTechniqueFactory {

	public SelectionTechnique createSelectionTechnique(SelectionTechniqueEnum techniqueName, 
			DependenciesField oldestVersionClassDependencies, 
			DependenciesField oldestVersionTestsClassDependencies, 
			DependenciesField newestVersionClassDependencies, 
			DifferencesField classDifferences,
			Set<String> selectedViolations,
			Set<String> classesWithViolations) {
		
		switch (techniqueName) {
		case CHANGE_BASED:
			return new ChangeBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences);
			
		case CLASS_FIREWALL:
			return new ClassFirewall(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences);	
			
		case CODE_SMELL_BASED:
			return new CodeSmellBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					selectedViolations,
					classesWithViolations);	
			
		case CODE_SMELL_FIREWALL:
			return new CodeSmellFirewall(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					selectedViolations,
					classesWithViolations);
			
		case CHANGE_AND_CODE_SMELL_BASED:
			return new ChangeAndCodeSmellBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences,
					selectedViolations,
					classesWithViolations);
			
		case CLASS_FIREWALL_WITH_SMELLS:
			return new ClassAndSmellsFirewall(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences,
					selectedViolations,
					classesWithViolations);
			
		default:
            throw new IllegalArgumentException("Unknown Technique"+techniqueName);
        }
		
	}
}
