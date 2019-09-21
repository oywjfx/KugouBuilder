package com.qiqi

import com.qiqi.my.BuildTimeListener
import com.qiqi.util.Log
import org.apache.tools.ant.types.FileSet
import org.gradle.api.Plugin
import org.gradle.api.Project

public class TestPlugin implements Plugin<Project> {
    Project project;

    void apply(Project project) {
        this.project = project;

        Log.Stute("========================");
        Log.Stute("这是第二个插件!");
        Log.Stute("========================");

        BuildTimeListener listener = new BuildTimeListener()
        project.gradle.addListener(listener)

        project.afterEvaluate {

            Log.Stute("androidPlugin afterEvaluate:" + project.getName())
            try {
                if (!project.android.hasProperty('applicationVariants')) return
            } catch (Exception e) {
                e.printStackTrace()
                return
            }

            project.android.applicationVariants.all { variant ->
                Log.Stute(" " + project.getName() + " " + variant.buildType.name)
                if (variant.buildType.name != 'release') return

                // While release variant created, everything of `Android Plugin' should be ready
                // and then we can do some extensions with it
                configureReleaseVariant(variant)

                Project commonProject = project.getRootProject().project(ANDROIDCOMMONPROJECT);
//                commonProject.getTasks().findByName("mergeReleaseResources").setEnabled(false);
//                commonProject.getTasks().findByName("compileReleaseJavaWithJavac").setEnabled(false);

                Project kgProject = project.getRootProject().project(ANDROIDKUGOU);
//                kgProject.getTasks().findByName("mergeReleaseResources").setEnabled(false);
//                kgProject.getTasks().findByName("compileReleaseJavaWithJavac").setEnabled(false);
            }

        }
    }
    /** File of release variant output */
    File outputFile;
    /** Tasks of aar exploder */
    Set<File> explodeAarDirs

    final public static String ANDROIDCOMMONPROJECT = "androidkugou:androidcommon";
    final public static String ANDROIDKUGOU = "androidkugou";

    protected void configureReleaseVariant(variant) {
        Log.Stute("androidPlugin configureReleaseVariant:" + project.getName())
        // Init default output file (*.apk)
        outputFile = variant.outputs[0].outputFile

        explodeAarDirs = project.tasks.findAll {
            it.hasProperty('explodedDir')
        }.collect { it.explodedDir }

        // Hook variant tasks
//        variant.assemble.doLast {
//            tidyUp()
//        }
        //这里只做一次
        //因为处理common的时候需要处理其他的task 放在其他地方读不到这些task
        if (project.getName().equals(ANDROIDKUGOU)) {
            Project kugouProject = project.getRootProject().project(ANDROIDKUGOU);
            boolean result = prepareKugouProject(kugouProject);
            if (!result) {
                Log.Stute("ANDROIDKUGOU result:" + result)
                return;
            }
            // hookAllTaskTime(kugouProject);
            // hookAllTaskTime(kugouProject.getRootProject().project(HookProjectTask.STYLE))
            Project commonProject = project.getRootProject().project(ANDROIDCOMMONPROJECT);
            prepareCommonProject(commonProject);
            //  hookAllTaskTime(commonProject);
//            for (ProjectInfo projectInfo : ProjectInfo.lists) {
//                Project project = project.getRootProject().project(projectInfo.name);
//                HookProjectTask.init().prepareModuleProjectForHost(project, projectInfo);
//                //    hookAllTaskTime(project);
//            }
//            isMianHook = true;

        }
//        try {
//            def styleProject = project.rootProject.project(':lib.style');
//            styleProject.getTasks().findByName("lintVitalRelease").setEnabled(false);
//            Log.Stute("忽略lint成功--------------------")
//        } catch (Exception e) {
//            //  e.printStackTrace()
//        }
//        try {
//            def styleProject = project.rootProject.project(':lib.ktvstyle');
//            styleProject.getTasks().findByName("lintVitalRelease").setEnabled(false);
//            Log.Stute("忽略lint成功--------------------")
//        } catch (Exception e) {
//            //  e.printStackTrace()
//        }

    }

    public void prepareCommonProject(Project project) {
        project.getTasks().findByName("generateReleaseResources").doLast {
            // prepareStyleProject(project);
        }
        makeJarDependCommonProject(project);
    }

    /**
     * hook宿主项目宿主打包完成之后做资源混淆
     * @param project
     */
    public boolean prepareKugouProject(Project project) {
        Log.Stute("hook prepareKugouProject")
        Log.Stute("hook compileReleaseJavaWithJavac")

//        if (project.getTasks().findByName("prePackageMarkerForRelease") == null && project.getTasks().findByName("prePackageMarkerForDebug") == null) {
//            Log.Stute("hook compileReleaseJavaWithJavac")
//            return false;
//        }

//        fixToChannelPackage(project);
//        prepareCleanProject(project);
//        project.getTasks().findByName("assembleRelease").doLast {
//            new HostPackImpl().doProguardKugouRes(project);
//        }

//        project.getTasks().findByName("buildLib").doLast {
//            Log.Stute("buildLib will be end  wait the background runnable");
//            ThreadUtil.checkBackRunnableAndWait();
//        }

//            isBuildDebug = true;
        project.getTasks().findByName("compileReleaseJavaWithJavac").doLast {

            Log.Stute("prePackageMarkerForRelease执行结束")
            Log.Stute("打包开始执行")

            HostPackImpl hostPack = new HostPackImpl();

//            if (hostPack.fastBuild) {
            hostPack.initMainJar(project);
//            }
            hostPack.transFormJarToDex(project);
            hostPack.copyDexToPackageFolder();
        }
//
//        project.getTasks().findByName("prePackageMarkerForRelease").doLast {
//            Log.Stute("prePackageMarkerForRelease执行结束")
//            Log.Stute("打包开始执行")
//
//
//
//            HostPackImpl hostPack = new HostPackImpl();
//
//            if (hostPack.fastBuild){
//                hostPack.initMainJar(project);
//            }
//            hostPack.transFormJarToDex(project);
//            hostPack.copyDexToPackageFolder();
//        }


        project.getTasks().findByName("transformClassesWithDexForDebug").setEnabled(false);
        project.getTasks().findByName("transformClassesWithDexForRelease").setEnabled(false);
        project.getTasks().findByName("transformClassesWithMultidexlistForRelease").setEnabled(false);

        project.getTasks().findByName("lintVitalRelease").setEnabled(false);

        return true;

    }

    public void makeJarDependCommonProject(Project commonProject) {
//        if (commonProject.getTasks().findByName("taskJar") != null) {
//            return;
//        }
//        commonProject.task("taskJar", type: Jar, dependsOn: ":androidkugou:androidcommon:generateReleaseResources") {
//            from new File(commonProject.getProjectDir().absolutePath + '/build/intermediates/classes/release')
//            destinationDir = new File(commonProject.getRootDir().absolutePath + '/libs_game')
//            Log.PrePare("编译复制commonjar到game目录成功");
//        }

        commonProject.getTasks().findByName("compileReleaseJavaWithJavac").doLast {
            org.apache.tools.ant.taskdefs.Jar jar = new org.apache.tools.ant.taskdefs.Jar();
            org.apache.tools.ant.Project antProject = new org.apache.tools.ant.Project();
            jar.setProject(antProject);
            jar.destFile = new File(commonProject.getRootDir().absolutePath + '/libs_game/androidcommon.jar');
            FileSet fileSet = new FileSet();
            fileSet.dir = new File(commonProject.getProjectDir().absolutePath + '/build/intermediates/classes/release');
            jar.addFileset(fileSet);
            jar.execute();
        }

    }

}
