package com.RegressionTestSelectionTool.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class CLICommandExecuter {
    public CLICommandExecuter() {}

    public void execute(String cmd) {
        System.out.println();
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

       // if (errorResult != null) {
       //     throw new RuntimeException(
        //            "Error when executing the CLI command" + "\n"
         //                   + "Error: " + errorResult);
        //}
    }
}
