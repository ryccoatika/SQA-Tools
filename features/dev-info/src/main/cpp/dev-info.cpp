#include <jni.h>
#include <sys/system_properties.h>
#include <string>
#include <array>
#include <memory>
#include <cstdio>

jstring getProp(JNIEnv *env, jobject, jstring key) {
    const char *keyStr = env->GetStringUTFChars(key, nullptr);

    char value[PROP_VALUE_MAX] = {0};
    int len = __system_property_get(keyStr, value);

    env->ReleaseStringUTFChars(key, keyStr);

    if (len > 0) {
        return env->NewStringUTF(value);
    }

    return env->NewStringUTF("");
}

jstring execute(JNIEnv *env, jobject, jstring cmd) {
    const char *cmdStr = env->GetStringUTFChars(cmd, nullptr);

    FILE *pipe = popen(cmdStr, "r");
    env->ReleaseStringUTFChars(cmd, cmdStr);

    if (!pipe) {
        return env->NewStringUTF("");
    }

    std::unique_ptr<FILE, decltype(&pclose)> pipeGuard(pipe, pclose);

    std::string result;
    char buffer[128];

    while (fgets(buffer, sizeof(buffer), pipe)) {
        result.append(buffer);
    }

    return env->NewStringUTF(result.c_str());
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
            {"execute", "(Ljava/lang/String;)Ljava/lang/String;", reinterpret_cast<void *>(execute)},

    };

    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
