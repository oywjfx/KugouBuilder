package com.qiqi.my

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState


public class BuildTimeListener implements TaskExecutionListener, BuildListener {
    private times = []
    private long startMillis

    @Override
    void beforeExecute(Task task) {
        startMillis = System.currentTimeMillis()
    }

    @Override
    void afterExecute(Task task, TaskState taskState) {
        times.add([System.currentTimeMillis() - startMillis, task.path])
        println "Task spend time:"+(System.currentTimeMillis() - startMillis)
        //task.project.logger.warn "${task.path} spend ${ms}ms"
    }

    @Override
    void buildStarted(Gradle gradle) {

    }

    @Override
    void settingsEvaluated(Settings settings) {

    }

    @Override
    void projectsLoaded(Gradle gradle) {

    }

    @Override
    void projectsEvaluated(Gradle gradle) {

    }

    @Override
    void buildFinished(BuildResult result) {
        if (result.failure == null) {
            println "Task spend time:"
            for (time in times) {
                if (time[0] >= 50) {
                    printf "%7sms  %s\n", time
                }
            }
        }
    }

    //javaPreCompileRelease
    //generateReleaseSources
    //packageReleaseResources
    //incrementalReleaseJavaCompilationSafeguard
    //compileReleaseJavaWithJavac
}
