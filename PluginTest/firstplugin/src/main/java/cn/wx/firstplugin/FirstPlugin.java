package cn.wx.firstplugin;

import com.android.build.gradle.AppExtension;
import com.android.build.gradle.LibraryExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.JavaPluginExtension;

class FirstPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        project.getTasks().register("firstplugin", task -> {
            task.doLast(s -> {
                System.out.println("this is my first plugin");
                System.out.println("FirstPlugin buildFile ==> "+project.getBuildFile().getAbsolutePath());
            });
        });
        if (project.getPlugins().hasPlugin("com.android.library")) {
            //project.getA
            System.out.println("get android library");
        }
        project.getExtensions().findByType(AppExtension.class).registerTransform(new InjectTransform(project));


    }
}