//v14

//v13 - Alternative 

package com.RegressionTestSelectionTool;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import com.RegressionTestSelectionTool.CodeSmellsDetector.CodeSmellsDetector;
import com.RegressionTestSelectionTool.DependencyFinderTool.DependencyFinder;
import com.RegressionTestSelectionTool.DependencyFinderTool.JarJarDiff;
import com.RegressionTestSelectionTool.Technique.SelectionTechnique;
import com.RegressionTestSelectionTool.Technique.SelectionTechniqueEnum;
import com.RegressionTestSelectionTool.Technique.SelectionTechniqueFactory;
import com.RegressionTestSelectionTool.utils.CheckRequirements;
import com.RegressionTestSelectionTool.xmlfields.dependencies.ClassField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.DependenciesField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.InboundField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.OutboundField;
import com.RegressionTestSelectionTool.xmlfields.dependencies.PackageField;
import com.RegressionTestSelectionTool.xmlfields.differences.DifferencesField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassField;
import com.RegressionTestSelectionTool.xmlfields.differences.ModifiedClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewClassesField;
import com.RegressionTestSelectionTool.xmlfields.differences.NewPackagesField;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class TestSelector {
	
	//Main Paths
    private final String dependencyFinderHomePath;
    private final String initialProjectVersionDirectoryPath;
    private final String modifiedProjectVersionDirectoryPath;
    
    //Processing XML
    private  ArrayList<String> tempXMLOutputPaths = new ArrayList<>();
    private final List<String> tempXMLOutputFilenames = Arrays.asList(
            "dependency_extractor_for_modified_version_tempfile.xml",
            "c2c_for_modified_version_tempfile.xml",
            "jarjardiff_tempfile.xml",
            "dependency_extractor_for_initial_version_tempfile.xml",
            "c2c_for_initial_version_tempfile.xml"
    );
    
    //Logic XML
    private DependenciesField newestVersionClassDependencies;
    private DependenciesField oldestVersionClassDependencies;
    private DependenciesField oldestVersionTestsClassDependencies;
    private DifferencesField classDifferences;
    
    private Set<String> originalTestSet = new HashSet<>();
    //private final SelectionTechniqueEnum selectionTechnique;
    private final ArrayList<SelectionTechniqueEnum> selectedTechniquesNames;
    
    
    //Technique Logic    
    private Set<ClassField> selectedClassesDependencies = new HashSet<>();
    private Set<String> notSelectedTestClasses = new HashSet<>();
    private Set<String> selectedTestClasses = new HashSet<>();
    private Set<String> selectedClasses = new HashSet<>();

    
    //Code Smell Logic
    private CodeSmellsDetector smellsDetector;
	private Set<String> selectedViolations;
    private Set<String> classesWithViolations = new HashSet<>();

	private ArrayList<Report> reports;
	

	public ArrayList<Report> getReports() {
		return reports;
	}


	public void main(String[] args) {    	

    		if(CheckRequirements.checkRequirements(dependencyFinderHomePath)) {
	    		
    			createTemporaryFilesPath();
    			
	        	setClassesDependenciesAndSmells();
	        	
	        	setClassDifferences();

	        	reports = setTechniques();       
 
		        deleteTempFiles();
    			
			}				        
			
    }


    private ArrayList<Report> setTechniques() {
    	ArrayList<Report> techniqueReports = new ArrayList<>();
    	SelectionTechniqueFactory selectionTechniqueFactory = new SelectionTechniqueFactory();
    	
    	for(SelectionTechniqueEnum techniqueName : selectedTechniquesNames) {
        	SelectionTechnique choosedSelectionTechnique = selectionTechniqueFactory.createSelectionTechnique(
        			techniqueName,
        			oldestVersionClassDependencies,
        			oldestVersionTestsClassDependencies,
        			newestVersionClassDependencies,
        			classDifferences,
        			selectedViolations,
        			classesWithViolations);
        	
        	techniqueReports.add(choosedSelectionTechnique.getTechniqueReport());
    	}
    	
    	return techniqueReports;
	}


	private void setClassDifferences() {
    	if(classDifferences == null) {
    		JarJarDiff differences = new JarJarDiff(dependencyFinderHomePath, initialProjectVersionDirectoryPath, modifiedProjectVersionDirectoryPath, tempXMLOutputPaths.get(2));
        	classDifferences = differences.getClassDifferences();
    	} 	
	}


	private void setClassesDependenciesAndSmells() {
    	DependencyFinder oldestVersion = new DependencyFinder(dependencyFinderHomePath, initialProjectVersionDirectoryPath, tempXMLOutputPaths.get(3), tempXMLOutputPaths.get(4),Boolean.TRUE);
    	DependencyFinder newestVersion = new DependencyFinder(dependencyFinderHomePath, modifiedProjectVersionDirectoryPath, tempXMLOutputPaths.get(0), tempXMLOutputPaths.get(1),Boolean.FALSE);
    	
    	
    	Thread oldestVersionThread = new Thread(oldestVersion);
    	Thread newestVersionThread = new Thread(newestVersion);
    	Thread smellDetectorThread = new Thread(smellsDetector);
    	
    	oldestVersionThread.start();
    	newestVersionThread.start();
    	smellDetectorThread.start();
    	
    	
    	try {
			oldestVersionThread.join();
			newestVersionThread.join();
			smellDetectorThread.join();
			
			oldestVersionClassDependencies = oldestVersion.getClassDependencies();
	    	oldestVersionTestsClassDependencies = oldestVersion.getTestClassDependencies();
	    	originalTestSet = oldestVersion.getOriginalTestSet();
	    	newestVersionClassDependencies = newestVersion.getClassDependencies();	
	    	classesWithViolations = smellsDetector.getClassWithViolationsList(); 
	    	
		} catch (InterruptedException e) {
			System.out.println("Error Dependency Finder or PMD");
			e.printStackTrace();
			
		}
	
	}

	
    public Set<String> getNotSelectedTestSet() {
    	return notSelectedTestClasses;
	}


    public TestSelector(  		
            String initialProjectVersionDirectoryPath,
            String modifiedProjectVersionDirectoryPath,
            ArrayList<SelectionTechniqueEnum> selectedTechniquesNames,
            String dependencyFinderHomePath,
            ArrayList<String> selectedViolations,
            Boolean codeSmellIntersection
            
    ) throws NotImplementedException {
    	this.initialProjectVersionDirectoryPath = initialProjectVersionDirectoryPath;
    	this.modifiedProjectVersionDirectoryPath = modifiedProjectVersionDirectoryPath; 
    	this.selectedTechniquesNames = selectedTechniquesNames;
        this.dependencyFinderHomePath = dependencyFinderHomePath;
        this.selectedViolations = new HashSet<String>(selectedViolations);
        this.smellsDetector = new CodeSmellsDetector(modifiedProjectVersionDirectoryPath,selectedViolations,codeSmellIntersection);         	      
    }


    public Set<String> getSelectedTestcases() throws NotImplementedException {
        return selectedTestClasses;
    }

    
	@SuppressWarnings("unused")
	private void printDependenciesField(DependenciesField dependenciesField) {
		int countIn = 0;
		int countOut =0;
		
		if(dependenciesField.packages != null)
		for(PackageField packageField : dependenciesField.packages) {
			
			System.out.println(""+packageField.name);
			
			if(packageField.classes != null)
			for(ClassField classField : packageField.classes) {
				
				System.out.println("	"+classField.name);
				
				if(classField.inbounds != null)
				for(InboundField inboundField : classField.inbounds) {
					
					System.out.println("		->"+inboundField.text);
					//System.out.println("		"+inboundField.type);
					countIn++;
					
				}
				
				if(classField.outbounds != null)
				for(OutboundField outboundField : classField.outbounds) {
					
					System.out.println("		<-"+outboundField.text);
					//System.out.println("		"+outboundField.type);
					countOut++;
					
				}
				
			}
			
		}
    	
		
	}


    private void createTemporaryFilesPath() {
        String mainClassFolderPath = getMainClassFolderPath();

        tempXMLOutputPaths = new ArrayList<>(); 
        for (String tempXMLOutputFilename: tempXMLOutputFilenames) {
            tempXMLOutputPaths.add(Paths.get(mainClassFolderPath, tempXMLOutputFilename).toAbsolutePath().toString());
        }
    }


    private String getMainClassFolderPath() {
        File mainClassFile = null;
        try {
            mainClassFile = new File(TestSelector.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return mainClassFile.getParentFile().getPath();
    }

  
    private void deleteTempFiles() {		
    	for (String filePath : tempXMLOutputPaths) {
        	deleteFile(filePath);
        }	
    }  

    
    private static void deleteFile(String filePathToDelete) {

		try{
			File f= new File(filePathToDelete); 
		
			if(!f.delete()){
				System.out.println("Temporary File Not Deleted  "+f.getName());
			}else{  
				//System.out.println("  Deleted  "+f.getName());  
			}  
		}catch(Exception e){
			System.out.println("Error: "+filePathToDelete);
			e.printStackTrace();
		} 
	}


    public void execCmd(String cmd) {
        System.out.println("");
        System.out.println("Running CLI Command: " + cmd);
        String errorResult = null;
        try (
                InputStream inputStream = Runtime.getRuntime().exec(cmd).getErrorStream();
                Scanner s = new Scanner(inputStream).useDelimiter("\\A")
        ) {
            errorResult = s.hasNext() ? s.next() : null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (errorResult != null) {
            throw new RuntimeException(
                    "Error when executing the CLI command" + "\n"
                            + "Error: " + errorResult);
        }
    }
    

    public Set<String> getAllClassesNamesFromNewestProject(){
    	
    	Set<String> allClasses = new HashSet<String>();
    	
    	for(PackageField packages : newestVersionClassDependencies.packages) {
    		for(ClassField classes : packages.classes) {
    			
    			if(classes.confirmed.equals("yes")) {
    				String className = filterSubClasses(classes.name);   				
    				allClasses.add(className);
    			}
		   				    			
    		}
    	}
    	
    	
    	return allClasses;
    }
    

    public Set<String> getAllClassesNamesFromOldestProject(){
    	
    	Set<String> allClasses = new HashSet<String>();
    	
    	for(PackageField packages : oldestVersionClassDependencies.packages) {
    		for(ClassField classes : packages.classes) {			
    				String className = filterSubClasses(classes.name);   				
    				allClasses.add(className);		   				    			
    		}
    	}
    	
    	return allClasses;
    }

    
    public Set<String> getOriginalTestSet(){
    	return originalTestSet;
    }


    private String filterSubClasses(String originalString) {
    	String filteredString = originalString.split("\\$")[0];
    	return filteredString;
    }

    
	public DependenciesField getNewestVersionClassDependencies() {
		return this.newestVersionClassDependencies;
	}

	public DependenciesField getOldestVersionClassDependencies() {
		return this.oldestVersionClassDependencies;
	}
	
	public DependenciesField getOldestVersionTestsClassDependencies() {
		return this.oldestVersionTestsClassDependencies;
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
	
    //Show Confirmed Dependencies
    @SuppressWarnings("unused")
	private void printConsole(DependenciesField dependencies) {

    	int countP = 0;
    	int countC = 0;
    	int countT = 0;
    	
    	for(PackageField packages : dependencies.packages) {
			if(packages.confirmed.equals("yes")) {
				
				for(ClassField classes : packages.classes) {
					
					if(classes.confirmed.equals("yes")) {
						
						if((classes.name.toLowerCase().contains("test"))) {
							System.out.println(packages.name+" | "+classes.name );
							if(classes.inbounds != null)
								classes.inbounds.forEach(inbound -> {System.out.println(inbound.text);});
							//System.out.println(packages.name+";"+classes.name);
							countT++;
						}						
						countC++;
					}					
				}				
				countP++;
			}				
		}

    	System.out.println("packages "+countP);
    	System.out.println("classes "+countC);
    	System.out.println("testcases "+countT);
	}
    
    //Show Differences between Versions
    @SuppressWarnings("unused")
	private void printConsole(DifferencesField differences) {

    	int countModified = 0;
    	int countNewClasses = 0;
    	int countNewPackages = 0;
    	
    	//String differencesName = differences.name; //Not necessary
    	    	
    	ModifiedClassesField modifiedClasses = differences.modifiedClassesField;  
    	if(modifiedClasses != null) {
    		for(ModifiedClassField classes : modifiedClasses.classes) {
        		System.out.println(" "+classes.name);
        		countModified++;
        	}
    	}
    	
    	
    	NewClassesField newClasses = differences.newClassesField; 
    	if(newClasses != null) {
	    	for(String name : newClasses.names) {
	    		System.out.println(" "+name);
	    		countNewClasses++;
	    	}
    	}
    	
    	NewPackagesField newPackages = differences.newPackagesField;
    	if(newPackages != null) {
    		for(String name : newPackages.names) {
        		//System.out.println(" "+name);
        		countNewPackages++;
        	}
    	}
    	
   	    	
    	System.out.println("Modified Classes "+countModified);
    	System.out.println("New classes "+countNewClasses);
    	System.out.println("New packages "+countNewPackages);    	
	}

	public Set<String> getSelectedClasses() {		
		return selectedClasses;
	}

	public Set<String> getClassesWithViolations() {
		return classesWithViolations;
	}
   
	public Set<String> getSelectedClassesDependenciesList(){		
		Set<String> list = new HashSet<>();		
		selectedClassesDependencies.forEach(classField -> list.add(classField.name));		
		return list;
	}
	
}