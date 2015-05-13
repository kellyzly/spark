#include <jni.h>
JNIEXPORT void JNICALL initIDs(JNIEnv *env, jclass clazz);

JNIEXPORT jlong JNICALL initContext(JNIEnv *env, jclass clazz, jint alg, jint padding);

JNIEXPORT jstring JNICALL getLibraryName(JNIEnv *env, jclass clazz);

JNIEXPORT jlong JNICALL init(JNIEnv *env, jobject object, jlong ctx, jint mode, jint alg, jint padding,
jbyteArray key, jbyteArray iv);

JNIEXPORT jint JNICALL update(JNIEnv *env, jobject object, jlong ctx, jobject input, jint input_offset,
jint input_len, jobject output, jint output_offset, jint max_output_len);

JNIEXPORT jint JNICALL doFinal(JNIEnv *env, jobject object, jlong ctx, jobject output, jint offset,
jint max_output_len);

JNIEXPORT void JNICALL clean(JNIEnv *env, jobject object, jlong ctx);