#include "NativeCodeLoader.h"
#include "OpensslCipher.h"
#include "random/OpensslSecureRandom.h"
#include "random/Test1.h"
#include "Test2.h"


static JNINativeMethod NativeCodeLoaderStaticMethods[] = {
    /* name, signature, funcPtr */
    {"buildSupportsOpenssl","()Z",(void*)buildSupportsOpenssl},
};

static JNINativeMethod OpensslCipherStaticMethods[] = {
    /* name, signature, funcPtr */
    {"initIDs","()V",(void*)initIDs},
    {"initContext","(II)J",(void*)initContext},
    {"getLibraryName","()Ljava/lang/String;",(void*)getLibraryName},
};

static JNINativeMethod OpensslCipherNonStaticMethods[] = {
    /* name, signature, funcPtr */
    {"init","(JIII[B[B)J",(void*)init},
    {"update","(JLjava/nio/ByteBuffer;IILjava/nio/ByteBuffer;II)I",(void*)update},
    {"doFinal","(JLjava/nio/ByteBuffer;II)I",(void*)doFinal},
    {"clean","(J)V",(void*)clean},
};

static JNINativeMethod OpensslSecureRandomStaticMethods[] = {
    /* name, signature, funcPtr */
    {"initSR","()V",(void*)initSR},
    {"initSR1","()V",(void*)initSR1},
};

static JNINativeMethod OpensslSecureRandomNonStaticMethods[] = {
     /* name, signature, funcPtr */
    //"nextRandBytes___3B","([B)V",(void*)nextRandBytes___3B},
    {"nextRandBytes","([B)Z",(void*)nextRandBytes},
};

static JNINativeMethod Test1StaticMethods[] = {
     /* name, signature, funcPtr */
     {"initTest1","()V",(void*)initTest1},
};

static JNINativeMethod Test2StaticMethods[] = {
      /* name, signature, funcPtr */
      {"initTest2","()V",(void*)initTest2},
};


/*
 * Register several native methods for one class.
 */
static int registerNativeMethods(JNIEnv* env, const char* className,
    JNINativeMethod* gMethods, int numMethods)
{
    jclass clazz;

    clazz = (*env)->FindClass(env, className);
    if (clazz == NULL)
        return JNI_FALSE;

    if ((*env)->RegisterNatives(env, clazz, gMethods, numMethods) < 0)
        return JNI_FALSE;

    return JNI_TRUE;
}


/*
 * Register native methods for all classes we know about.
 */
static int registerNatives(JNIEnv* env,const char* className, JNINativeMethod* gMethods, int numMethods)
{
  if (!registerNativeMethods(env, className,
        gMethods, numMethods))
    return JNI_FALSE;

  return JNI_TRUE;
}


JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved)
{
  JNIEnv* env = NULL;
  jint result = -1;

  if ((*vm)->GetEnv(vm, (void **)&env, JNI_VERSION_1_2) != JNI_OK)
    return JNI_ERR;

  if (!registerNatives(env,"org/apache/spark/crypto/NativeCodeLoader$",NativeCodeLoaderStaticMethods,
  sizeof(NativeCodeLoaderStaticMethods) / sizeof(NativeCodeLoaderStaticMethods[0])))
    return JNI_ERR;

  if (!registerNatives(env,"org/apache/spark/crypto/OpensslCipher$",OpensslCipherStaticMethods,
  sizeof(OpensslCipherStaticMethods) / sizeof(OpensslCipherStaticMethods[0])))
    return JNI_ERR;

  if (!registerNatives(env,"org/apache/spark/crypto/OpensslCipher",OpensslCipherNonStaticMethods,
    sizeof(OpensslCipherNonStaticMethods) / sizeof(OpensslCipherNonStaticMethods[0])))
      return JNI_ERR;
  if (!registerNatives(env,"org/apache/spark/crypto/Test2",Test2StaticMethods,
      sizeof(Test2StaticMethods) / sizeof(Test2StaticMethods[0])))
        return JNI_ERR;

  if (!registerNatives(env,"org/apache/spark/crypto/random/OpensslSecureRandom$",
  OpensslSecureRandomStaticMethods,
   sizeof(OpensslSecureRandomStaticMethods) / sizeof(OpensslSecureRandomStaticMethods[0])))
      return JNI_ERR;

  if (!registerNatives(env,"org/apache/spark/crypto/random/OpensslSecureRandom",
  OpensslSecureRandomNonStaticMethods,
   sizeof(OpensslSecureRandomNonStaticMethods) / sizeof(OpensslSecureRandomNonStaticMethods[0])))
      return JNI_ERR;


  if (!registerNatives(env,"org/apache/spark/crypto/random/Test1",Test1StaticMethods,
        sizeof(Test1StaticMethods) / sizeof(Test1StaticMethods[0])))
          return JNI_ERR;

  /* success -- return valid version number */
  result = JNI_VERSION_1_4;
  printf("result:%d",result);
  return result;
}