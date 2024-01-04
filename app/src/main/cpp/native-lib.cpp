
#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_banrossyn_storydownloader_api_RestClient_getBaseUrl1(JNIEnv *env, jclass clazz) {
    // TODO: implement getBaseUrl1()
    {

        //TODO Change Your URl

        std::string baseURL = "https://api.threadsphotodownloader.com/v2/";
//        std::string baseURL = "https://api.threadsphotodownloader.com/";
        return env->NewStringUTF(baseURL.c_str());
    }
}