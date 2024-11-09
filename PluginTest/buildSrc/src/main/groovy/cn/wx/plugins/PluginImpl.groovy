package cn.wx.plugins

import org.gradle.api.Plugin
import org.gradle.api.Project

class PluginImpl implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.task("PluginImpl") {
            doLast {
                println("this is cn.wx.plugins.PluginImpl")
            }
        }
    }
}