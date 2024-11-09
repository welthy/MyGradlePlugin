package cn.wx.firstplugin;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;

import org.gradle.api.Project;

import java.io.File;
import java.lang.reflect.Method;

class InjectByJavassit {
    public static void doInject(Project project, File clsFile, String originPath) throws Exception {
        System.out.println(("[Inject] DoInject: "+clsFile.getAbsolutePath()));
        String cls = clsFile.getAbsolutePath().replace('/', '.');
        cls = cls.substring(0, cls.lastIndexOf(".class"));
        System.out.println("[Inject] Cls: "+cls);

        ClassPool pool = ClassPool.getDefault();
        // 加入当前路径
        System.out.println("[Inject] originPath="+originPath);
        pool.appendClassPath(originPath);
        // project.android.bootClasspath 加入android.jar，不然找不到android相关的所有类
        //pool.appendClassPath(project.android.bootClasspath[0].toString());
        // 引入android.os.Bundle包，因为onCreate方法参数有Bundle
        pool.importPackage("android.os.Bundle");

        CtClass ctClass = pool.getCtClass("cn.wx.plugintest.Utils");
        // 解冻
        if (ctClass.isFrozen()) {
            ctClass.defrost();
        }
        // 获取方法
        CtMethod ctMethod = ctClass.getDeclaredMethod("onCreate");

        String toastStr = "android.util.Log.d(\"WANG\", \"this is inject\")";

        // 方法尾插入
        ctMethod.insertAfter(toastStr);
        ctClass.writeFile(originPath);

        // 释放
        ctClass.detach();
    }

    public static void injectLog(String fileNamePath, String filePath, Project project) throws Exception{
        ClassPool pool = ClassPool.getDefault();
        fileNamePath.replaceAll("\\\\", "/");
        System.out.println("[injectLog], fileNamePath="+fileNamePath);

//        String className =  filePath.replace(fileNamePath, "")
//                .replace("\\", ".")  .replace("/", ".");
//        String name = className.replace(".class", "").substring(1);
//        System.out.println("包名为:" + name);
        System.out.println("[injectLog], filePath="+filePath);
        //目标类的根目录加入到ClassPool中
        //F:\Android\tempProjects\MyGradlePlugin\PluginTest\app\build\intermediates\javac\debug\classes
        pool.insertClassPath(filePath);
        pool.importPackage("android.os.Bundle");

        //包名+类名获得对应CtClass
        CtClass ctClass = pool.getCtClass("cn.wx.plugintest.Utils");
        CtMethod ctMethod = ctClass.getDeclaredMethod("setApi");
        ctMethod.setBody("System.out.println(\"WANG this is inject\");");
        //写入class文件
        ctClass.writeFile(filePath);

        //类对象未被加载过，才可以调toClass
        Class<?> cc = ctClass.toClass();
        Method method = cc.getDeclaredMethod("setApi");
        Object obj = cc.newInstance();
        method.invoke(obj);
    }
}