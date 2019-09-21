package com.qiqi.my;

import com.qiqi.util.AntBaseUtil
import com.qiqi.util.Log
import org.apache.tools.ant.BuildException
import org.apache.tools.ant.taskdefs.Jar
import org.apache.tools.ant.taskdefs.Javac
import org.apache.tools.ant.types.FileSet
import org.apache.tools.ant.types.Path
import org.apache.tools.ant.types.selectors.DependSelector
import org.gradle.api.Project


/**
 * Created by yijunwu on 2019/9/19.
 */
public class BuilderDex {
    private org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
    private String jarName = "classes%s.jar";
    private String dexName = "classes%s.dex";

    private Project project;

    public void start(Project project) {
        this.project = project;
        String jarPath = BuildUtils.getBuildMyPath(project) + "\\" + String.format(jarName, "");
        String dexPath = BuildUtils.getBuildMyPath(project) + "\\" + String.format(dexName, "");
        Log.Stute("jarPath:" + jarPath)
        Log.Stute("dexPath:" + dexPath)

        pack2Jar(jarPath)
        JarToDex(jarPath, dexPath)
        copyDexToPackageFolder(dexPath)
    }


    public void compile(String destPath,List<String> javaLsit) {
        Javac compiler = new Javac();
        compiler.setProject( new org.apache.tools.ant.Project());
        File destFile = new File(destPath);

        compiler.setFork(true);
        compiler.setDestdir(destFile);

        Path srcPath = new Path(compiler.getProject(), srcFilePath);
        compiler.setSrcdir(srcPath);

        compiler.add()
        try {
            compiler.execute();
        } catch (BuildException e) {
            e.printStackTrace();
        }
    }

    public void pack2Jar(String destFile) {
        Jar jar = new Jar();
        jar.setProject(antProject);
        jar.destFile = new File(destFile);
        FileSet fileSet = AntBaseUtil.makeFileSet(BuildUtils.getDebugPath(project), ClassesForDex, null);
        jar.addFileset(fileSet)
        jar.execute();
    }

    public static final String[] ClassesForDex = [
            "**",
    ]

    public void JarToDex(String jarfile, String dexOut) {


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

    public void copyDexToPackageFolder(String dexOut) {
        String outPath = project.getBuildDir().absolutePath + "\\intermediates\\transforms\\dex\\debug\\folders\\1000\\1f\\main";

        AntBaseUtil.baseCopy(dexOut, outPath);
        Log.Stute("複製包到主package成功");
    }

}
