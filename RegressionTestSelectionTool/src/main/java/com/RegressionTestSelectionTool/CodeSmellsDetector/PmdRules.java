package com.RegressionTestSelectionTool.CodeSmellsDetector;

import java.util.ArrayList;

import com.RegressionTestSelectionTool.CodeSmellsDetector.PmdRulesList.BestPracticesRules;
import com.RegressionTestSelectionTool.CodeSmellsDetector.PmdRulesList.CodeStyleRules;
import com.RegressionTestSelectionTool.CodeSmellsDetector.PmdRulesList.DesignRules;
import com.RegressionTestSelectionTool.CodeSmellsDetector.PmdRulesList.DocumentationRules;



public class PmdRules {
  public static ArrayList<String> getAllRulesAsString() {
    ArrayList<String> rules = new ArrayList<String>();

    for (BestPracticesRules rule : BestPracticesRules.values()) {
      rules.add(rule.name());
    }

    for (CodeStyleRules rule : CodeStyleRules.values()) {
      rules.add(rule.name());
    }

    for (DesignRules rule : DesignRules.values()) {
      rules.add(rule.name());
    }

    for (DocumentationRules rule : DocumentationRules.values()) {
      rules.add(rule.name());
    }

    return rules;
  }

  public static boolean IsBestPracticeRule(String rule) {
    for (BestPracticesRules bestPracticeRule : BestPracticesRules.values()) {
      if (bestPracticeRule.name().equals(rule)) {
        return true;
      }
    }
    return false;
  }

  public static boolean IsCodeStyleRule(String rule) {
    for (CodeStyleRules codeStyleRule : CodeStyleRules.values()) {
      if (codeStyleRule.name().equals(rule)) {
        return true;
      }
    }
    return false;
  }

  public static boolean IsDesignRule(String rule) {
    for (DesignRules designRule : DesignRules.values()) {
      if (designRule.name().equals(rule)) {
        return true;
      }
    }
    return false;
  }

  public static boolean IsDocumentationRule(String rule) {
    for (DocumentationRules documentationRule : DocumentationRules.values()) {
      if (documentationRule.name().equals(rule)) {
        return true;
      }
    }
    return false;
  }
}


