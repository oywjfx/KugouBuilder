package com.qiqi.util

//import net.wequick.gradle.pack.ProjectInfo
import org.gradle.api.Project
/**
 * Created by siganid on 2016/5/16.
 */
public interface HookProjectTaskInterface {
    /**
     * hook宿主项目宿主打包完成之后做资源混淆
     * @param project
     */
    public boolean prepareKugouProject(Project project);
    public void prepareCommonProject(Project project);
    public void prepareLibProject(Project project);
//    public boolean prepareModuleProjectForHost(Project project, ProjectInfo moduleProject);
//    public void prepareModuleProject(Project project, ProjectInfo moduleProject);
    /**
     *  编译common项目之后调用
     * @param project
     */
    public void prepareStyleProject(Project project);
    /**
     * 添加MakeJar的声明 编译common的代码并且把它放到 game的目录下。
     * @param commonProject
     */
    public void makeJarDependCommonProject(Project commonProject);


}
