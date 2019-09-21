package com.qiqi


import com.android.build.api.transform.Transform
import com.android.annotations.NonNull
import com.android.build.api.transform.QualifiedContent.ContentType;
import com.android.build.api.transform.QualifiedContent.Scope
import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation;
import com.android.build.gradle.internal.pipeline.TransformManager

public class TestTrasform extends Transform {


    @Override
    String getName() {
        return "TestTrasform"
    }

    @Override
    Set<ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(
            @NonNull TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
    }
}
