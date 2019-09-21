package com.qiqi.util

//import net.wequick.gradle.pack.HookProjectTask
import org.gradle.api.Project

/**
 * Created by mingzhihuang on 2016/5/24.
 */
public class Log {

    static Project project;
    static File logFile;
    static File logTimeFile;
    static String logFilePath = "D:\\Develop\\Android";
    static String logTimeFilePath = "log.txt";

//    public static void init(Project project) {
//        File buildPath = project.getRootProject().project(HookProjectTask.ANDROIDKUGOU).getBuildDir();
//        if (!buildPath.exists()) {
//            buildPath.mkdirs();
//        }
//        logFile = new File(buildPath, "buildLog.txt");
//        logFilePath = logFile.absolutePath;
//        if (!logFile.exists()) {
//            logFile.createNewFile();
//            logFile.write("");
//        }
//
//        logTimeFile = new File(buildPath, "buildTimeLog.txt");
//        logTimeFilePath = logTimeFile.absolutePath;
//        if (!logTimeFile.exists()) {
//            logTimeFile.createNewFile();
//            logTimeFile.write("");
//        }
//
//        Log.project = project;
//    }

    public static void Stute(String s) {
        String logString = " [Excuse] " + s ;
        println(logString)
        synchronized (Log.class) {
            writeToFile(logString);
        }
    }

    public static void Time(String s) {
        String logString = " [Time] " + s ;
        println(logString)
        synchronized (Log.class) {
            writeToTimeFile(logString);
        }
    }

    public static void Config(String s) {
        String logString = " [Config] " + s + "  ";
        println(logString)
        synchronized (Log.class) {
            writeToFile(logString);
        }
    }

    public static void Command(String s) {
        String logString = " [Command] \r\n" + s;
        println(logString)
        synchronized (Log.class) {
            writeToFile(logString);
        }
    }

    public static void PrePare(String method) {
        String logString = " [PrePare] " + method + " [OK] ";
        println(logString);
        writeToFile(logString);
    }
    static File mFile ;
    static String path = "D:\\Develop\\log.txt";

    private static void writeToFile(String log) {
        if (mFile == null ) {
            mFile = new File(path);
            if (!mFile.exists()){
                mFile.createNewFile();
            }
        }
        mFile.append(log + "\r\n");
    }

    private static void writeToTimeFile(String log) {
        if (mFile == null ) {
            mFile = new File(path);
            if (!mFile.exists()){
                mFile.createNewFile();
            }
        }
        mFile.append(log + "\r\n");
    }


}
