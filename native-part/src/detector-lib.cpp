/**
 * @date 2017/8/4
 * @author Mitscherlich
 */

#include "detector-lib.h"
#include "face_detection.h"

#include <string>

#ifdef null
#undef null
#endif

#define null 0

void MatrixRotate(unsigned char *matrix, int cols, int rows) {
    unsigned char *buffer = new unsigned char[cols * rows];
    memcpy(buffer, matrix, (size_t) (cols * rows));

    for (int i = 0; i < rows; ++i)
        for (int j = cols - 1; j >= 0; --j)
            matrix[i * cols + j] = buffer[i + j * rows];

    delete[] buffer;
}

JNIEXPORT jstring JNICALL Java_work_petrichor_seeta_Detector_detect
        (JNIEnv *env, jobject /* this */, jlong address, jbyteArray yv12frame, jint width, jint height) {


    seeta::FaceDetection *detector = (seeta::FaceDetection *) address;

    jbyte *data = env->GetByteArrayElements(yv12frame, 0);
    unsigned char *frame = new unsigned char[width * height];
    memcpy(frame, data, (size_t) (width * height));

    seeta::ImageData grey(height, width);
//    Transpose(frame, height, width);
//    HorizontalFlip(frame, height, width);
    MatrixRotate(frame, height, width);
    grey.data = frame;

    std::vector<seeta::FaceInfo> faces = detector->Detect(grey);
    int32_t num = static_cast<int32_t>(faces.size());

    std::string faceStr = "";

    for (int i = 0; i < num; ++i) {
        char tmpStr[200] = { 0 };
        sprintf(tmpStr, "%d,%d,%d,%d", faces[i].bbox.x, faces[i].bbox.y, faces[i].bbox.width, faces[i].bbox.height);
        faceStr += tmpStr;
        faceStr += ";";
    }

    return env->NewStringUTF(faceStr.c_str());
}

JNIEXPORT jlong JNICALL Java_work_petrichor_seeta_Detector_getAddress
        (JNIEnv *env, jobject /* this */, jstring model) {

    const char *cStr = env->GetStringUTFChars(model, 0);
    if (cStr == nullptr) return null;

    std::string modelPath = cStr;

    jlong result = (jlong) new seeta::FaceDetection(modelPath.c_str());
    env->ReleaseStringUTFChars(model, cStr);
    return result;
}
