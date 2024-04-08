package com.RegressionTestSelectionTool.CodeSmellsDetector;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.RegressionTestSelectionTool.TestSelector;
import com.RegressionTestSelectionTool.utils.CLICommandExecuter;
import com.RegressionTestSelectionTool.utils.OSGetter;
import com.RegressionTestSelectionTool.xmlfields.smells.PmdFile;
import com.RegressionTestSelectionTool.xmlfields.smells.PmdReport;
import com.RegressionTestSelectionTool.xmlfields.smells.Violation;
import com.google.common.collect.HashMultimap;
import com.thoughtworks.xstream.XStream;

import jdk.jshell.spi.ExecutionControl.NotImplementedException;

public class CodeSmellsDetector implements Runnable{

	//Paths
	private String pmdRulesetPath;
	private String projectDirectoryPath;
    private List<String> tempXMLReportFilePaths;
    private final List<String> tempXMLOutputFilenames = Arrays.asList(
            "code_smells_list_tempfile.xml"
    );
    
    private ArrayList<String> selectedViolationsTypes = new ArrayList<String>();
    private HashMultimap<String, String> codeSmellMap = HashMultimap.create();
    private Boolean codeSmellIntersection=Boolean.TRUE;
    
    private Set<String> classWithViolationsList = new HashSet<>();
    

    public Set<String> getClassWithViolationsList() {
		return classWithViolationsList;
	}

	public CodeSmellsDetector(String projectDirectoryPath, ArrayList<String> selectedViolationsTypes, Boolean codeSmellIntersection) throws NotImplementedException{
        this.projectDirectoryPath = projectDirectoryPath;
        this.selectedViolationsTypes = selectedViolationsTypes;
        this.codeSmellIntersection = codeSmellIntersection;

        setTempXMLOutputPaths();

        this.pmdRulesetPath = getPmdRulesetPath();
    }

    public void getClassWithCodeViolationsList() throws NotImplementedException
    {
        ArrayList<Violation> projectViolationsList = getProjectViolationsList();

        projectViolationsList = this.removeTestFiles(projectViolationsList);

        this.deleteTempFiles();

        //return getClassWithViolationsList(projectViolationsList);
        classWithViolationsList = getClassWithViolationsList(projectViolationsList);
    }

    private ArrayList<Violation> getProjectViolationsList() throws NotImplementedException
    {
        String violationsListTempfile = tempXMLOutputFilenames.get(0);

        String pmdCliCommand = getPmdCliCommand(projectDirectoryPath, violationsListTempfile);
        
        //CLICommandExecuter commandExecuter = new CLICommandExecuter();

        CLICommandExecuter.Execute(pmdCliCommand);

        ArrayList<Violation> violations = getViolationsFromXMLReportFile(violationsListTempfile);

        return violations;
    }

    private String getPmdCliCommand(String projectVersionDirectoryPath, String reportFileDirectoryPath) throws NotImplementedException {
        if (OSGetter.isWindows()) {
            return ".\\pmd.bat -d " + projectVersionDirectoryPath + " -f xml -R "+ this.pmdRulesetPath + " --report-file " + reportFileDirectoryPath;
        } else if (OSGetter.isUnix()) {
            return "pmd check " + projectVersionDirectoryPath + " -f xml -R "+ this.pmdRulesetPath + " --report-file " + reportFileDirectoryPath;     
        } else {
            throw new NotImplementedException("Your Operational System is not supported yet");
        }
    }

    private void setTempXMLOutputPaths(){
        String projectPath = getProjectPath();

        tempXMLReportFilePaths = new ArrayList<>();
        for (String tempXMLOutputFilename: tempXMLOutputFilenames) {
            tempXMLReportFilePaths.add(Paths.get(projectPath, tempXMLOutputFilename).toAbsolutePath().toString());
            
            
            
        }
        
    }

    private String getProjectPath() {
    	File mainClassFile = null;
        try {
            mainClassFile = new File(TestSelector.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
  
        return mainClassFile.getParentFile().getPath();
    }

    private ArrayList<Violation> getViolationsFromXMLReportFile(String xmlReportFilePath) {
        var xStream = new XStream();
        xStream.ignoreUnknownElements();

        xStream.allowTypesByWildcard(new String[] {
            "com.RegressionTestSelectionTool.**",
        });

        xStream.processAnnotations(PmdReport.class);
        xStream.processAnnotations(PmdFile.class);
        xStream.processAnnotations(Violation.class);

        var XMLFile = new File(xmlReportFilePath);

        PmdReport pmdReport = (PmdReport) xStream.fromXML(XMLFile);

        return pmdReport.getViolations();
    }

    private Set<String> getClassWithViolationsList(ArrayList<Violation> projectViolationsList) {
    	Set<String> classWithViolationsList = new HashSet<String>();

    	for (Violation violation : projectViolationsList) {
			classWithViolationsList.add(violation.getClassName());
			codeSmellMap.put(violation.getClassName(), violation.rule);
		}
    	
    	if(codeSmellIntersection) {
    		
    		 int NumberOfIntersections = selectedViolationsTypes.size();
    	        Set<String> classesWithCodeSmells = getClassesWithNCodeSmells(codeSmellMap,NumberOfIntersections);
    	        
    	        return classesWithCodeSmells;
    		 		
    	}else {
    		classWithViolationsList.removeIf(className -> className.contains(".null"));
            return classWithViolationsList;
    	}
    
        //System.out.println(codeSmellMap.toString());
 
    }
    

    private Set<String> getClassesWithNCodeSmells(HashMultimap<String, String> codeSmellMap2, int n) {
		HashSet<String> classesList  = new HashSet<String>();
		
		for (String className : codeSmellMap2.keys()) {
		    Collection<String> codeSmellType = codeSmellMap2.get(className);
		    if(codeSmellType.size() == n) {
		    	classesList.add(className);
		    }
		    
		    //for (String value : codeSmellType) {
		        //System.out.println(className + " -> " + value);
		    //}
		}
		
		return classesList;
	}

	private ArrayList<Violation> removeTestFiles(ArrayList<Violation> projectViolationsList) {
    	ArrayList<Violation> testFiles = new ArrayList<Violation>();

        for (Violation violation : projectViolationsList) {
            if (violation.getClassName().toLowerCase().contains("test")) {
                testFiles.add(violation);
            }
        }

        projectViolationsList.removeAll(testFiles);
        
        return projectViolationsList;
    }

    private void deleteTempFiles() {
        //for (String tempXMLOutputFilename: tempXMLReportFilePaths) {       	
        	//System.out.println("tempXMLOutputFilename "+tempXMLOutputFilename);
            //deleteFile(tempXMLOutputFilename);
        //}

        if (!selectedViolationsTypes.isEmpty()) {
            deleteFile(this.pmdRulesetPath);
        }
    }
    
    private static void deleteFile(String filePathToDelete) {

		try{
			File f= new File(filePathToDelete); 
		
			if(!f.delete()){
				System.out.println("Temporary File Not Deleted  "+f.getName());
				System.out.println("file Path To Delete: "+filePathToDelete);
			}else{  
				//System.out.println("  Deleted  "+f.getName());  
			}  
		}catch(Exception e){
			System.out.println("Error: "+filePathToDelete);
			e.printStackTrace();
		} 
	}

    private String getPmdRulesetPath() {
        if (selectedViolationsTypes.isEmpty()) 
            return "rulesets/java/quickstart.xml";

        var rulesetsFactory = new PmdXmlRulesetFactory(this.selectedViolationsTypes);
        return rulesetsFactory.buildXmlRuleset(getProjectPath());
    }

	@Override
	public void run() {
		try {
			getClassWithCodeViolationsList();
		} catch (NotImplementedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
