package com.RegressionTestSelectionTool.CodeSmellsDetector;

import java.io.File;
import java.io.FileOutputStream;
import java.io.WriteAbortedException;
import java.nio.channels.WritePendingException;
import java.util.ArrayList;

import com.RegressionTestSelectionTool.xmlfields.smells.Ruleset;
import com.thoughtworks.xstream.XStream;

public class PmdXmlRulesetFactory {

  private ArrayList<String> rules = new ArrayList<String>();
  public PmdXmlRulesetFactory(ArrayList<String> rules) {
    this.validateRules(rules);
    this.rules = rules;    
  }

  private void validateRules(ArrayList<String> rules) {
    var possibleRules = PmdRules.getAllRulesAsString();

    for (var rule : rules) {
      if (isValidRule(rule, possibleRules)) {
        continue;
      }
      else
        throw new IllegalArgumentException(
          "Invalid rule: " + rule + ". You can check the available rules list" +
          "at: https://pmd.github.io/latest/pmd_rules_java.html");
    }
  }

  private boolean isValidRule(String rule, ArrayList<String> possibleRules) {
    for (var possibleRule : possibleRules) {
      if (possibleRule.equals(rule)) {
        return true;
      }
    }
    return false;
  }

  public String buildXmlRuleset(String projectPath) throws WritePendingException {
    var ruleSet = createXStreamRulesetFile();
    
    XStream xstream = new XStream();
    xstream.allowTypesByWildcard(new String[] {
      "com.RegressionTestSelectionTool.**",
    });

    xstream.processAnnotations(Ruleset.class);
    var xmlRuleSet = xstream.toXML(ruleSet);
    var rulesetFilePath = projectPath + "/" + "pmd_ruleset_tempfile.xml";

    saveRulesetAsTempFile(xmlRuleSet, rulesetFilePath);

    return rulesetFilePath;
  }

  private Ruleset createXStreamRulesetFile(){
    var ruleset = new Ruleset();

    for (var rule : rules) {
      ruleset.addBestPraticeRef(rule);
    }

    return ruleset;    
  }

  private void saveRulesetAsTempFile(String xmlRuleSet, String rulesetFilePath) throws WritePendingException {
    FileOutputStream rulesetFile = null;
    try {
      rulesetFile = new FileOutputStream(rulesetFilePath);
  
      rulesetFile.write("<?xml version=\"1.0\"?>".getBytes("UTF-8"));
      byte[] bytes = xmlRuleSet.getBytes("UTF-8");
      rulesetFile.write(bytes);
    }
    catch (Exception e) {
      throw new WritePendingException();
    } finally {
      if(rulesetFile != null) {
        try {
          rulesetFile.close();
        } catch (Exception e) {
          throw new WritePendingException();
        }
      }
    }
  }
}
