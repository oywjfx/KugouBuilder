package com.qiqi.my

import com.qiqi.Constant
import com.qiqi.util.AntBaseUtil
import com.qiqi.util.Log
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.taskdefs.Javac
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.optional.depend.ClassfileSet
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task

/**
 * Created by yijunwu on 2019/9/19.
 */

public class BuilderMain implements Plugin<Project>, Constant {
    Project project

    private boolean isAllCompile = true;

    @Override
    void apply(Project project) {
        this.project = project;
        Log.Stute("========================");
        Log.Stute("这是插件! ");
        Log.Stute("========================");

        project.afterEvaluate {
            project.android.applicationVariants.all { variant ->
                String variantName = variant.name.capitalize()
                if (variantName == null || variantName.equals("") || variantName.equalsIgnoreCase("release")) {
                    return
                }
                Log.Stute("环境 " + variantName);
                dexTask(variantName)
            }
        }
    }

    private void dexTask(String variantName) {
        String infoTxt = BuildUtils.getBuildMyClassPath(project) + "\\javaInfo.txt";
        if (new File(infoTxt).exists()) {
            Log.Stute("已备份class")
            isAllCompile = false;
        } else {
            isAllCompile = true;
        }

        if (isAllCompile && !project.getName().equals(BUILD_PLUG_NAME)) {

            Log.Stute("这是任务 " + project.getName() + " " + project.getTasks().size());
            project.getTasks().findByName("compile${variantName}JavaWithJavac").doLast {
                Log.Stute("全量编译，备份class")

                String src = BuildUtils.getClassPath(project);
                String dest = BuildUtils.getBuildMyClassPath(project);
                CopyDir.copy(src, dest);

                //记录文件消息
                FileScanHelper helper = new FileScanHelper();
                Log.Stute("BuildUtils.getPath():" + BuildUtils.getPath(project) + "\\src\\main\\java")
                helper.scan(new File(BuildUtils.getPath(project) + "\\src\\main\\java"));
                FileScanHelper.writeFile(helper.pathList, infoTxt)
            }
        }



        if (!isAllCompile && !project.getName().equals(BUILD_PLUG_NAME)) {
            Log.Stute("增量编译")

            for (Task task : project.getTasks()) {
                if (task != null) task.setEnabled(false);
            }

            FileScanHelper helper = new FileScanHelper();
            helper.scan(new File(BuildUtils.getPath(project) + "\\src\\main\\java"));

            Map<String, FileScanHelper.FileInfo> cacheMap = FileScanHelper.readFile(infoTxt);
            List<String> javaLit = new ArrayList<>()
            for (FileScanHelper.FileInfo info : helper.pathList) {
                FileScanHelper.FileInfo search = cacheMap.get(info.path);
                if (search == null) {
                    System.out.println("增加 " + info.path);
                    javaLit.add(info.path)
                } else if (!search.eq(info)) {
                    System.out.println("修改 " + info.path);
                    javaLit.add(info.path)
                }
            }

            compile(javaLit);
            pack2Jar();
            JarToDex();
//            compile(copyJava(javaLit))

        }

    }

    public String copyJava(List<String> javaLit) {
        //复制文件
        String javaPath = BuildUtils.getBuildMyClassPath(project) + "\\dex\\java";
        FileUtils.deleteDir(new File(javaPath))
        FileUtils.ensumeDir(new File(javaPath))


        for (String java : javaLit) {
            String name = java.substring(java.lastIndexOf("java\\") + 5, java.length());
            Log.Stute("name:" + name)
            String path = name.substring(0, name.lastIndexOf("\\"));
            Log.Stute("path:" + path)
            CopyDir.fileCopy(java, javaPath + "\\" + name)
        }

        return javaPath;
    }

    public void compile(List<String> javaLit) {
        List<String> cmdArgs = new ArrayList<>()
        cmdArgs.add(BuildUtils.getJavacCmdPath())
        cmdArgs.add("-encoding")
        cmdArgs.add("UTF-8")
        cmdArgs.add("-g")
//        cmdArgs.add("-target")
//        cmdArgs.add(javaCompile.targetCompatibility)
//        cmdArgs.add("-source")
//        cmdArgs.add(javaCompile.sourceCompatibility)

        List<String> classPath = new ArrayList<>()
        String androidJar = "${BuildUtils.getSdkDirectory(project)}${File.separator}platforms${File.separator}${project.android.getCompileSdkVersion()}${File.separator}android.jar"
        classPath.add(androidJar)
        classPath.add(BuildUtils.getBuildMyClassPath(project) + "\\debug")
        cmdArgs.add("-cp")
        cmdArgs.add(joinClasspath(classPath))

        String destPath = BuildUtils.getBuildMyClassPath(project) + "\\dex\\classes";
        FileUtils.deleteDir(new File(destPath))
        FileUtils.ensumeDir(new File(destPath))
//        cmdArgs.add("-s")
//        cmdArgs.add(destPath)
        cmdArgs.add("-d")
        cmdArgs.add(destPath)
        cmdArgs.add(joinJavapath(javaLit))
        String cmd = "";
        for (String s : cmdArgs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }

    def joinJavapath(List<String> collection) {
        StringBuilder sb = new StringBuilder()

        collection.each { file ->
            sb.append(file)
            sb.append(" ")
        }
        return sb
    }

    def joinClasspath(List<String> collection) {
        StringBuilder sb = new StringBuilder()

        boolean window = true
        collection.each { file ->
            sb.append(file)
            if (window) {
                sb.append(";")
            } else {
                sb.append(":")
            }
        }
        return sb
    }


    public void pack2Jar() {
        String destPath = BuildUtils.getBuildMyClassPath(project) + "\\dex\\patch.jar";
        Jar jar = new Jar();
        jar.setProject(new org.apache.tools.ant.Project());
        jar.destFile = new File(destPath);
        FileSet fileSet = AntBaseUtil.makeFileSet(BuildUtils.getBuildMyClassPath(project) + "\\dex\\classes", ClassesForDex, null);
        jar.addFileset(fileSet)
        jar.execute();
    }

    public static final String[] ClassesForDex = [
            "**",
    ]

    public void JarToDex() {
        String jarfile = BuildUtils.getBuildMyClassPath(project) + "\\dex\\patch.jar";
        String dexOut = BuildUtils.getBuildMyClassPath(project) + "\\dex\\patch.dex";
        File dxFile = new File(BuildUtils.getDxCmdPath(project));
        List<String> dexargs = new ArrayList<>();
        dexargs.add(dxFile.absolutePath);
        dexargs.add("--dex");
        dexargs.add("--no-locals")
        dexargs.add("--force-jumbo");
        // 添加此标志以规避dx.jar对"java/"、"javax/"的检查
        dexargs.add("--core-library")
        // disableDexMerger="${dex.disable.merger}"

        dexargs.add("--output=${dexOut}");
        dexargs.add("${jarfile}")
        String cmd = "";
        for (String s : dexargs) {
            cmd = cmd + "${s} ";
        }
        AntBaseUtil.commandExec(cmd)
    }

    private void closeTask(String name) {
        Task task = project.getTasks().findByName(name);
        if (task != null) task.setEnabled(false);
    }

}
