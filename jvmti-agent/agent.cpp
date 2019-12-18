
#include <stdlib.h>
#include <string.h>
#include <iostream>

#include "jvmti.h"
#include "jni.h"

void JNICALL
MyClassFileLoadHookHandler(
        jvmtiEnv *jvmti_env,
        JNIEnv *jni_env,
        jclass class_being_redefined,
        jobject loader,
        const char *name,
        jobject protection_domain,
        jint class_data_len,
        const unsigned char *class_data,
        jint *new_class_data_len,
        unsigned char **new_class_data) {
    printf("loading class: %s\n", name);

    if (strncmp(name, "me/ya/", 6) != 0) {
        return;
    }
    jvmtiError result;

    result = jvmti_env->Allocate(class_data_len, new_class_data);
    if (result != JVMTI_ERROR_NONE) return;

    *new_class_data_len = class_data_len;

    unsigned char *output_class_data = *new_class_data;
    for (int i = 0; i < class_data_len; ++i) {
        output_class_data[i] = class_data[i] ^ 0xFF;
    }
}

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    jvmtiEnv *jvmti_env;
    jvmtiError error;
    jvmtiEventCallbacks callbacks;
    // 获取 JVMTI environment
    jint ret = vm->GetEnv((void **) &jvmti_env, JVMTI_VERSION);
    if (ret != JNI_OK) {
        return ret;
    }
    //设置类加载事件回调
    (void) memset(&callbacks, 0, sizeof(callbacks));
    callbacks.ClassFileLoadHook = &MyClassFileLoadHookHandler;
    error = jvmti_env->SetEventCallbacks(&callbacks, sizeof(callbacks));
    if (error != JVMTI_ERROR_NONE) {
        return error;
    }
    //设置事件通知
    error = jvmti_env->SetEventNotificationMode(JVMTI_ENABLE, JVMTI_EVENT_CLASS_FILE_LOAD_HOOK,
                                                NULL);
    if (error != JVMTI_ERROR_NONE) {
        return error;
    }
    return JNI_OK;
}


