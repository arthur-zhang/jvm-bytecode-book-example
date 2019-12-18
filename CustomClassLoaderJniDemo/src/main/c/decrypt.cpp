#include "decrypt.h"
extern "C"
JNIEXPORT jbyteArray JNICALL
Java_me_ya_classloader_MyCustomClassLoader_decryptJni(JNIEnv *env, jclass clazz,
                                                      jbyteArray src_bytes) {
    jbyte *src = env->GetByteArrayElements(src_bytes, 0);
    // 获取 byte 数组的长度
    jsize src_Len = env->GetArrayLength(src_bytes);
    jbyte buf[src_Len];

    // 执行 XOR 运算
    for (int i = 0; i < src_Len; ++i) {
        buf[i] = src[i] ^ 0xFF;
    }

    env->ReleaseByteArrayElements(src_bytes, src, 0);
    jbyteArray result = env->NewByteArray(src_Len);
    env->SetByteArrayRegion(result, 0, src_Len, buf);
    return result;
}