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
			
		case SMELL_BASED:
			return new SmellBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					selectedViolations,
					classesWithViolations);	
			
		case SMELL_FIREWALL:
			return new SmellFirewall(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					selectedViolations,
					classesWithViolations);
			
		case CHANGE_AND_SMELL_BASED:
			return new ChangeAndSmellBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences,
					selectedViolations,
					classesWithViolations);
			
		case CHANGE_AND_SMELL_FIREWALL:
			return new ChangeAndSmellFirewall(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences,
					selectedViolations,
					classesWithViolations);
			
		case CHANGE_AND_SMELL_INTERSECTION_BASED:
			return new ChangeAndSmellIntersectionBased(oldestVersionClassDependencies,
					oldestVersionTestsClassDependencies,
					newestVersionClassDependencies,
					classDifferences,
					selectedViolations,
					classesWithViolations);
			
		case CHANGE_AND_SMELL_INTERSECTION_FIREWALL:
			return new ChangeAndSmellIntersectionFirewall(oldestVersionClassDependencies,
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
