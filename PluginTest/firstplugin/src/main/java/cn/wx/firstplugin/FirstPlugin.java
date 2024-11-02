package cn.wx.firstplugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

class FirstPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("firstplugin", task -> {
            task.doLast(s -> {
                System.out.println("this is my first plugin");
                System.out.println("FirstPlugin buildFile ==> "+project.getBuildFile().getAbsolutePath());
            });
        });
    }
}