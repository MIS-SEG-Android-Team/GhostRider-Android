package org.rmj.g3appdriver.lib.FacialRecognition;

import android.content.Context;

import com.google.mediapipe.tasks.core.BaseOptions;
import com.google.mediapipe.tasks.vision.core.RunningMode;
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker;

public class FRUtility {

    private final Context loInstance;

    public FRUtility(Context foContext){
        this.loInstance = foContext;
    }

    /**
     * @param fsmodelName Task Model File Path
     * @param fnnumFaces Number of faces to detect.
     * @param foRunningMode Running Mode class objects
     * @param ffminConfidence Float Value
     * @param ffacePresence Float Value
     * @param fftrackConfidence Float Value
     * @param fbBlend Boolean Value
     * @param fbFaceTransform Boolean Value
     * **/
    public FaceLandmarker GetFaceLandMarker(String fsmodelName, int fnnumFaces, RunningMode foRunningMode, float ffminConfidence,
                                                float ffacePresence, float fftrackConfidence, boolean fbBlend, boolean fbFaceTransform){

        FaceLandmarker.FaceLandmarkerOptions loLandmark = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(BaseOptions.builder()
                        .setModelAssetPath(fsmodelName)
                        .build())
                .setNumFaces(fnnumFaces)
                .setRunningMode(foRunningMode)
                .setMinFaceDetectionConfidence(ffminConfidence)
                .setMinFacePresenceConfidence(ffacePresence)
                .setMinTrackingConfidence(fftrackConfidence)
                .setOutputFaceBlendshapes(fbBlend)
                .setOutputFacialTransformationMatrixes(fbFaceTransform)
                .build();

        return FaceLandmarker.createFromOptions(loInstance, loLandmark);
    }

}
