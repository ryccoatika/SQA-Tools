#include <jni.h>
#include <vector>
#include <malloc.h>

using namespace std;

// Global storage for allocated memory
vector<void *> allocatedMemory;

jlong getAllocatedMemorySize(JNIEnv *, jobject) {
    long totalSize = 0;
    for (void *memory: allocatedMemory) {
        totalSize += static_cast<long>(malloc_usable_size(memory));
    }
    return totalSize;
}

#pragma clang diagnostic push
#pragma ide diagnostic ignored "MemoryLeak"

void allocateMemory(JNIEnv *, jobject, jlong size) {
    if (size <= 0) return;

    void *memory = malloc(static_cast<size_t>(size));
    if (memory != nullptr) {
        memset(memory, 0, static_cast<size_t>(size));
        allocatedMemory.push_back(memory);
    }
}

#pragma clang diagnostic pop

void deallocateAllMemory(JNIEnv *, jobject) {
    for (void *memory: allocatedMemory) {
        free(memory); // Free each allocated memory block
    }
    allocatedMemory.clear();
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *) {
    JNIEnv *env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    jclass clazz = env->FindClass("com/ryccoatika/sqatoolkit/fillmemory/core/utils/MemoryHelper");
    if (clazz == nullptr) {
        return JNI_ERR;
    }

    JNINativeMethod methods[] = {
            {"getAllocatedMemorySize", "()J",  reinterpret_cast<void *>(getAllocatedMemorySize)},
            {"allocateMemory",         "(J)V", reinterpret_cast<void *>(allocateMemory)},
            {"deallocateAllMemory",    "()V",  reinterpret_cast<void *>(deallocateAllMemory)},

    };

    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) < 0) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
