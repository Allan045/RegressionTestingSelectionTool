package com.RegressionTestSelectionTool.CodeSmellsDetector;

import com.RegressionTestSelectionTool.TestSelector;
import com.RegressionTestSelectionTool.utils.CLICommandExecuter;
import com.RegressionTestSelectionTool.utils.OSGetter;
import com.RegressionTestSelectionTool.xmlfields.smells.PmdFile;
import com.RegressionTestSelectionTool.xmlfields.smells.PmdReport;
import com.RegressionTestSelectionTool.xmlfields.smells.Violation;
import com.thoughtworks.xstream.XStream;

import jdk.jshell.spi.ExecutionControl;
import jdk.jshell.spi.ExecutionControl.NotImplementedException;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CodeSmellsDetector {

    private String projectDirectoryPath;
    private final List<String> tempXMLOutputFilenames = Arrays.asList(
            "code_smells_list_tempfile.xml"
    );
    private List<String> tempXMLReportFilePaths;
    private ArrayList<String> selectedViolationsTypes = new ArrayList<String>();
    private String pmdRulesetPath;

    public CodeSmellsDetector(
            String projectDirectoryPath, 
            ArrayList<String> selectedViolationsTypes
    ) throws NotImplementedException
    {
        this.projectDirectoryPath = projectDirectoryPath;
        this.selectedViolationsTypes = selectedViolationsTypes;

        setTempXMLOutputPaths();

        this.pmdRulesetPath = getPmdRulesetPath();
    }

    public Set<String> getClassWithCodeViolationsList() throws NotImplementedException
    {
        ArrayList<Violation> projectViolationsList = getProjectViolationsList();

        projectViolationsList = this.removeTestFiles(projectViolationsList);

        this.deleteTempFiles();

        return getClassWithViolationsList(projectViolationsList);
    }

    private ArrayList<Violation> getProjectViolationsList() throws NotImplementedException
    {
        String violationsListTempfile = tempXMLOutputFilenames.get(0);

        String pmdCliCommand = getPmdCliCommand(projectDirectoryPath, violationsListTempfile);

        CLICommandExecuter commandExecuter = new CLICommandExecuter();

        commandExecuter.execute((pmdCliCommand));

        ArrayList<Violation> violations = getViolationsFromXMLReportFile(violationsListTempfile);

        return violations;
    }

    private String getPmdCliCommand(String projectVersionDirectoryPath, String reportFileDirectoryPath) throws NotImplementedException {
        if (OSGetter.isWindows()) {
            return ".\\pmd.bat -d " + projectVersionDirectoryPath + " -f xml -R "+ this.pmdRulesetPath + " --report-file " + reportFileDirectoryPath;
        } else if (OSGetter.isUnix()) {
            return "run.sh pmd -d " + projectVersionDirectoryPath + " -f xml -R "+ this.pmdRulesetPath + " --report-file " + reportFileDirectoryPath;
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
        var classWithViolationsList = new HashSet<String>();

        for (Violation violation : projectViolationsList) {
            classWithViolationsList.add(violation.getClassName());
        }

        return classWithViolationsList;
    }

    private ArrayList<Violation> removeTestFiles(ArrayList<Violation> projectViolationsList) {
        var testFiles = new ArrayList<String>();

        for (Violation violation : projectViolationsList) {
            if (violation.getClassName().contains("Test")) {
                testFiles.add(violation.getClassName());
            }
        }

        for (String testFile : testFiles) {
            projectViolationsList.removeIf(violation -> violation.getClassName().equals(testFile));
        }

        return projectViolationsList;
    }

    private void deleteTempFiles() {
        for (String tempXMLOutputFilename: tempXMLOutputFilenames) {
            File tempXMLOutputFile = new File(tempXMLOutputFilename);
            tempXMLOutputFile.delete();
        }

        if (!selectedViolationsTypes.isEmpty()) {
            File pmdRulesetFile = new File(this.pmdRulesetPath); 
            
            pmdRulesetFile.delete();
        }
    }

    private String getPmdRulesetPath() {
        if (selectedViolationsTypes.isEmpty()) 
            return "rulesets/java/quickstart.xml";
        
        // if(selectedViolationsTypes.contains("Comments")){
        //     selectedViolationsTypes.clear();
        //     return "/home/luccasparoni/Documents/TCC/rulesets/nocomments.xml";
        // }

        var rulesetsFactory = new PmdXmlRulesetFactory(this.selectedViolationsTypes);
        return rulesetsFactory.buildXmlRuleset(getProjectPath());
    }
}
