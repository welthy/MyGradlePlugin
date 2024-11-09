package cn.wx.firstplugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Status;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class InjectTransform extends Transform {

    Project mProject;

    InjectTransform(Project mProject) {
        this.mProject = mProject;
    }

    @Override
    public String getName() {
        return InjectTransform.class.getName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        for (TransformInput input : transformInvocation.getInputs()) {
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                String path = directoryInput.getFile().getAbsolutePath();
                System.out.println("[InjectTransform].transform() path="+path);

                if (path.endsWith("javac\\debug\\classes")) {
                    try {
                        List<File> javaFiles = new ArrayList<>();
                        traverseDirectory(new File(path), javaFiles);
                        for (File file : javaFiles) {
                            System.out.println("javaFile: " + file);
                            if (file.getAbsolutePath().endsWith("Utils.class")) {
                                InjectByJavassit.injectLog(file.getAbsolutePath(), path, mProject);
                                //InjectByJavassit.doInject(mProject, file, path);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("[transform] fail. " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
    }

    public static void traverseDirectory(File directory, List<File> javaFiles) throws Exception {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files!= null) {
                for (File file : files) {
                    if (file.isFile()) {
                        javaFiles.add(file);
                    } else if (file.isDirectory()) {
                        //System.out.println("目录: " + file.getAbsolutePath());
                        traverseDirectory(file, javaFiles);
                    }
                }
            }
        }
    }


}
