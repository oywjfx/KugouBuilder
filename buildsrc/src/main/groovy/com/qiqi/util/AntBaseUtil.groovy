package com.qiqi.util

import org.apache.tools.ant.taskdefs.Copy
import org.apache.tools.ant.taskdefs.Java
import org.apache.tools.ant.taskdefs.optional.ReplaceRegExp
import org.apache.tools.ant.types.FileSet
import org.gradle.api.Project;

/**
 * Created by mingzhihuang on 2016/5/24.
 */
public class AntBaseUtil {

    static org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();

    public static void LogStute(String log) {
        System.out.println(log + "【OK】");
    }

    public static void log(String log) {
        System.out.println(log);
    }

    public static void baseCopy(String from, String toPath) {
        File fromFile = new File(from);
        File toFile = new File(toPath);
        if (!fromFile.exists()) {
            return;
        }
        Copy copy = new Copy();
        copy.setProject(antProject);
        copy.setOverwrite(true);
        copy.setFile(fromFile);
        copy.setTodir(toFile);
        copy.execute();
    }

    public static File makePath(Project project, String type) {
        String tempPath = "kugou_temp/";
        String alltempPath = project.getRootProject().project("androidkugou").getBuildDir().absolutePath + "/" + tempPath + "/" + type;
        File tempFile = new File(alltempPath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        tempFile.mkdirs();
        return tempFile;
    }


    public static Java makeJavaBase(String jarPath, List<String> args) {
        Java java = new Java();
        java.failonerror = true;
        java.fork = true;
        java.jar = new File(jarPath);
        java.maxmemory = "128m";
        for (String arg : args) {
            java.createArg().setValue(arg);
        }
        return java;
    }


    public
    static ReplaceRegExp makeBaseReplaceRegExp(String pattern, String expression, String fileSetDir, String[] fileSetIncludes) {
        ReplaceRegExp regExp = new ReplaceRegExp();
        regExp.setFlags("g");
        regExp.setByLine(false);
        regExp.setEncoding("utf-8");
        regExp.createRegexp().setPattern(pattern);
        regExp.createSubstitution().setExpression(expression);
        String[] includes = { "KGCommonApplication.java" };
        FileSet fileSet = makeFileSet(new File(fileSetDir), fileSetIncludes);
        regExp.addFileset(fileSet);
    }

    public static FileSet makeFileSet(String dir, String[] includes, String[] excludes) {
        FileSet fileSet = new FileSet();
        fileSet.dir = new File(dir);
        if (includes != null) {
            for (String s : includes) {
                fileSet.appendIncludes(s)
            }
        }

        if (excludes != null) {
            for (String s : excludes) {
                fileSet.appendExcludes(s)
            }
        }
        return fileSet;
    }

    public static void commandExec(String command) {
        Log.Command(command);
        Process process = command.execute();
        new Thread(new Runnable() {
            @Override
            void run() {
                InputStream stderr = process.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    println(line);
                }
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            void run() {
                InputStream inputStream = process.getInputStream();
                InputStreamReader is = new InputStreamReader(inputStream);
                BufferedReader bris = new BufferedReader(is);
                String line = null;
                while ((line = bris.readLine()) != null) {
                    println(line);
                }
            }
        }).start();
        process.waitFor();
        process.destroy();
    }


    public static void commandExec(String cmd, File outPutFile) {
        println("命令执行：" + cmd);
        Process process = cmd.execute();

        new Thread(new Runnable() {
            @Override
            void run() {
                InputStream stderr = process.getErrorStream();
                InputStreamReader isr = new InputStreamReader(stderr);
                BufferedReader br = new BufferedReader(isr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    println(line);
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            void run() {
                FileWriter fileWriter = new FileWriter(outPutFile);
                InputStream inputStream = process.getInputStream();
                InputStreamReader is = new InputStreamReader(inputStream);
                BufferedReader bris = new BufferedReader(is);
                String line = null;
                while ((line = bris.readLine()) != null) {
                    println(line);
                    fileWriter.write(line + "\r\n");
                }
                fileWriter.flush();
                fileWriter.close();
                bris.close()
                is.close();
                inputStream.close();
            }
        }).start();
        process.waitFor();
        process.destroy();

    }

}
