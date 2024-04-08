package com.RegressionTestSelectionTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import com.RegressionTestSelectionTool.Technique.SelectionTechniqueEnum;
import com.RegressionTestSelectionTool.xmlfields.dependencies.ClassField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.PackageField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;

public class Report{

	private SelectionTechniqueEnum selectionTechnique;
	private Set<String> selectedViolations;
	private Set<String> oldestVersionClassesSet;
	private Set<String> newestVersionClassesSet;
	private Set<String> modifiedClassesSet;
	private Set<String> codesmellClasses;
	private Set<String> selectedClasses;
	private Set<String> selectedClassesDependencies;	
	private Set<String> testSet;
	private Set<String> selectedTestSet;
	private Set<String> notSelectedTestSet;
	private long executionTime;
	
	private String projectName;
	private int oldestVersionID;
	private String oldestVersionType;
	private int newestVersionID;
	private String newestVersionType;
	private int numberSelectedTriggerTests;
	private Set<String> selectedTriggerTests;
	private Set<String> d4jTriggerTestsSet;
	private int numberD4jTriggerTestsSet;
	
	
	public Report() {
		
	}
	
	public void setExperimentReport(String projectName, int oldestVersionID, String oldestVersionType, 
			int newestVersionID, String newestVersionType, Set<String> selectedTriggerTests, Set<String> d4jTriggerTestsSet) {
		this.projectName = projectName;
		
		this.oldestVersionID = oldestVersionID;
		this.oldestVersionType = oldestVersionType;
		this.newestVersionID = newestVersionID;
		this.newestVersionType = newestVersionType;
		
		this.selectedTriggerTests = selectedTriggerTests;
		this.numberSelectedTriggerTests = selectedTriggerTests.size();
		
		this.d4jTriggerTestsSet = d4jTriggerTestsSet;
		this.numberD4jTriggerTestsSet = d4jTriggerTestsSet.size();
		
	}
	
	public void printExperimentResults(String directoryName, String fileName, Boolean header) {
		final String CSV_SEPARATOR = ";";
		
		File directory = new File(directoryName);
		if (! directory.exists()){
			directory.mkdirs();
		}
		
		System.out.println("");
		
		try {
			
			
			//BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directoryName+fileName+".csv")));
			
			File file =new File(directoryName+fileName+".csv");
			
			if(!file.exists()){
		    	   file.createNewFile();
		    }
			
			FileWriter fw = new FileWriter(file,true);
	    	BufferedWriter bw = new BufferedWriter(fw);
			
			if(header) {
				String head = new String("Project;"
						+ "Oldest Version ID;Oldest Version Type;"
						+ "Newest Version ID;Newest Version Type;"
						+ "Selection Tecnique;Code Smell;"
		        		+ "# Classes Oldest Project;Classes Oldest Project;"
		        		+ "# Classes Newest Project;Classes Newest Project;"
		        		+ "# Changed Classes;Changed Classes;"
		        		+ "# Smelly Classes;Smelly Classes;"
		        		+ "# Selected Classes;Selected Classes;"
		        		+ "# Tests;Tests;"
		        		+ "# Selected Tests;Selected Tests;"
		        		+ "# Not Selected Tests;Not Selected Tests;"
		        		+ "Selection Time (ms);"
		        		+ "# Fault Revealing Tests (Defects4J);Fault Revealing Tests (Defects4J);"
		        		+ "# Selected Fault Revealing Tests;Selected Fault Revealing Tests;");
				
				
				bw.write((String) head);
				bw.write(CSV_SEPARATOR);			
	            bw.newLine();
			}
			
			
            //-- 
            bw.write((String) getProjectName());
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) Integer.toString(getOldestVersionID()));
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) getOldestVersionType());
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) Integer.toString(getNewestVersionID()));
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) getNewestVersionType());
            bw.write(CSV_SEPARATOR); 
            //--
            
            bw.write((String) getSelectionTechnique());
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) selectedViolations.toString());
            bw.write(CSV_SEPARATOR);
	                    
	        bw.write((String) Integer.toString(oldestVersionClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) oldestVersionClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(newestVersionClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) newestVersionClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(modifiedClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) modifiedClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(codesmellClasses.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) codesmellClasses.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(selectedClassesDependencies.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) selectedClassesDependencies.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(testSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) testSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(selectedTestSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) selectedTestSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(notSelectedTestSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) notSelectedTestSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Long.toString((executionTime)));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(getNumberD4jTriggerTestsSet()));
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) getD4jTriggerTestsSet().toString());
            bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) Integer.toString(getNumberSelectedTriggerTests()));
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) getSelectedTriggerTests().toString());
            bw.write(CSV_SEPARATOR); 
            
            bw.newLine();
            
			bw.flush();
            bw.close();

			System.out.println("	Results Saved");
			System.out.println("");
			
			
		}
		catch (UnsupportedEncodingException e) {
			System.out.println("		Error To Print Results File \n");
			e.printStackTrace();
		}
        catch (FileNotFoundException e){
        	System.out.println("		Error To Print Results File \n");
        	e.printStackTrace();
        }
        catch (IOException e){
        	System.out.println("		Error To Print Results File \n");
        	e.printStackTrace();
        }
	}
	
	
	public Report(SelectionTechniqueEnum selectionTechnique, Set<String> selectedViolations,
			DependenciesField oldestVersionClasses, DependenciesField newestVersionClasses,
			Set<String> modifiedClasses, Set<String> codesmellClasses, Set<String> selectedClasses, 
			Set<String> selectedClassesDependencies, Set<String> testSet, Set<String> selectedTestSet,
			Set<String> notSelectedTestSet, long executionTime) {
		
		super();
		this.selectionTechnique = selectionTechnique;
		this.selectedViolations = selectedViolations;
		this.oldestVersionClassesSet = getAllClassesNamesFromDependenciesField(oldestVersionClasses);
		this.newestVersionClassesSet = getAllClassesNamesFromDependenciesField(newestVersionClasses);
		this.modifiedClassesSet = modifiedClasses;
		this.codesmellClasses = codesmellClasses;
		this.selectedClasses = selectedClasses;
		this.selectedClassesDependencies = selectedClassesDependencies;
		this.testSet = testSet;
		this.selectedTestSet = selectedTestSet;
		this.notSelectedTestSet = notSelectedTestSet;
		this.executionTime = executionTime;
	}

	public String getSelectionTechnique() {
		if(SelectionTechniqueEnum.CHANGE_BASED.equals(selectionTechnique)) {
			return "Change Based";
		}else if(SelectionTechniqueEnum.CLASS_FIREWALL.equals(selectionTechnique)) {
			return "Class Firewall";
		}else if(SelectionTechniqueEnum.SMELL_FIREWALL.equals(selectionTechnique)) {
			return "Smell Firewall";
		}else if(SelectionTechniqueEnum.CHANGE_AND_SMELL_FIREWALL.equals(selectionTechnique)) {
			return "Change and Smell Firewall";
		}else if(SelectionTechniqueEnum.SMELL_BASED.equals(selectionTechnique)) {
			return "Smell Based";
		}else if(SelectionTechniqueEnum.CHANGE_AND_SMELL_BASED.equals(selectionTechnique)) {
			return "Change and Smell Based";
		}else if(SelectionTechniqueEnum.CHANGE_AND_SMELL_INTERSECTION_BASED.equals(selectionTechnique)) {
			return "Change and Smell Intersection Based";
		}else if(SelectionTechniqueEnum.CHANGE_AND_SMELL_INTERSECTION_FIREWALL.equals(selectionTechnique)) {
			return "Change and Smell Intersection Firewall";
		}
		return "";
	}

	public Set<String> getOldestVersionClasses() {
		return oldestVersionClassesSet;
	}

	public Set<String> getNewestVersionClasses() {
		return newestVersionClassesSet ;
	}
	
	public Set<String> getAllClassesNamesFromDependenciesField(DependenciesField dependencies){
    	
    	Set<String> allClasses = new HashSet<String>();
    	
    	for(PackageField packages : dependencies.packages) {
    		for(ClassField classes : packages.classes) {			
    				String className = filterSubClasses(classes.name);   				
    				allClasses.add(className);		   				    			
    		}
    	}
    	
    	return allClasses;
    }

	//Filter Inner Classes Names
	private String filterSubClasses(String originalString) {
		String filteredString = originalString.split("\\$")[0];
		return filteredString;
	}
	
	public Set<String> toSet(DifferencesField modifiedClasses) {
		Set<String> toSet = new HashSet<String>();
		
		modifiedClasses.modifiedClassesField.classes.forEach(classes -> toSet.add(classes.name));
		toSet.addAll(modifiedClasses.newClassesField.names);
		
		return toSet;
	}

	public Set<String> getCodesmellClasses() {
		return codesmellClasses;
	}

	public Set<String> getSelectedClasses() {
		return selectedClasses;
	}

	public Set<String> getSelectedTestSet() {
		return selectedTestSet;
	}
	
	public Set<String> getNotSelectedTestSet() {
		return notSelectedTestSet;
	}

	public long getExecutionTime() {
		return executionTime;
	}

	public void printFile(String directoryName, String fileName) {
		final String CSV_SEPARATOR = ";";
		
		File directory = new File(directoryName);
		if (! directory.exists()){
			directory.mkdirs();
		}
		
		try {
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(directoryName+fileName+".csv")));
			
			String head = new String("Selection Technique;Code Smell;"
					+ "# Classes Oldest Project;Classes Oldest Project;"
	        		+ "# Classes Newest Project;Classes Newest Project;"
	        		+ "# Changed Classes;Changed Classes;"
	        		+ "# Smelly Classes;Smelly Classes;"
	        		+ "# Selected Classes;Selected Classes;"
	        		+ "# Tests;Tests;"
	        		+ "# Selected Tests;Selected Tests;"
	        		+ "# Not Selected Tests;Not Selected Tests;"
	        		+ "Selection Time (ms);");
			
			
			bw.write((String) head);
			bw.write(CSV_SEPARATOR);			
            bw.newLine();
			
            bw.write((String) getSelectionTechnique());
            bw.write(CSV_SEPARATOR);
            
            bw.write((String) selectedViolations.toString());
            bw.write(CSV_SEPARATOR);
	                    
	        bw.write(Integer.toString(oldestVersionClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) oldestVersionClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(newestVersionClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) newestVersionClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(modifiedClassesSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) modifiedClassesSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(codesmellClasses.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) codesmellClasses.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(selectedClassesDependencies.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) selectedClassesDependencies.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(testSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) testSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(selectedTestSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) selectedTestSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Integer.toString(notSelectedTestSet.size()));
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write((String) notSelectedTestSet.toString());
	        bw.write(CSV_SEPARATOR);
	        
	        bw.write(Long.toString((executionTime)));
	        bw.write(CSV_SEPARATOR);

            
			bw.flush();
            bw.close();
            
			System.out.println("\n -> RTST Results Saved \n");
			
			
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        catch (FileNotFoundException e){
        	e.printStackTrace();
        }
        catch (IOException e){
        	e.printStackTrace();
        }
	}

	
	
	
	public String getProjectName() {
		return projectName;
	}

	public int getOldestVersionID() {
		return oldestVersionID;
	}

	public String getOldestVersionType() {
		return oldestVersionType;
	}

	public int getNewestVersionID() {
		return newestVersionID;
	}

	public String getNewestVersionType() {
		return newestVersionType;
	}

	public int getNumberSelectedTriggerTests() {
		return numberSelectedTriggerTests;
	}
	
	public Set<String> getSelectedTriggerTests() {
		return selectedTriggerTests;
	}
	

	public Set<String> getD4jTriggerTestsSet() {
		return d4jTriggerTestsSet;
	}

	public int getNumberD4jTriggerTestsSet() {
		return numberD4jTriggerTestsSet;
	}

	public void printSummaryConsole() {
		System.out.println("");
		System.out.println(getSelectionTechnique());
		System.out.println("Classes Oldest Version: "+oldestVersionClassesSet.size());
		System.out.println("Classes Newest Version: "+newestVersionClassesSet.size());
		System.out.println("Changed Classes: "+modifiedClassesSet.size());
		System.out.println("Smelly Classes: "+codesmellClasses.size()+" "+selectedViolations.toString());
		System.out.println("Selected Classes Seeds: "+ selectedClasses.size()); 
		System.out.println("Selected Classes Dependencies: "+selectedClassesDependencies.size());
   		System.out.println("Test: "+ testSet.size());
   		System.out.println("Selected Test: "+ selectedTestSet.size());
   		System.out.println("Not Selected Tests: "+ notSelectedTestSet.size());
   		System.out.println("Selection Time: "+(executionTime)+"ms");
   		System.out.println("");
	}
	
}
