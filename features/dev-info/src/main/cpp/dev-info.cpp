#include <jni.h>
#include <sys/system_properties.h>

jstring getProp(JNIEnv *env, jobject, jstring key) {
    const char *keyStr = env->GetStringUTFChars(key, nullptr);
    env->ReleaseStringUTFChars(key, keyStr);

    char value[PROP_VALUE_MAX] = {0};

    if (__system_property_get(keyStr, value) > 0) {
        return env->NewStringUTF(value);
    }

    return env->NewStringUTF("");
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass clazz = env->FindClass("com/ryccoatika/sqatoolkit/devinfo/core/utils/NativeHelper");
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    JNINativeMethod methods[] = {
            {"getProp", "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void *>(getProp)},

    };

    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
