package org.rmj.g3appdriver.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.mlkit.vision.common.InputImage;
import com.squareup.picasso.Picasso;

import org.rmj.g3appdriver.R;

public class ImageFileManager {

    private static final String TAG = ImageFileManager.class.getSimpleName();

    public static void LoadImageToView(String link, ShapeableImageView view){
        Picasso.get().load(link).placeholder(R.drawable.img_imageview_place_holder)
                .error(R.drawable.img_imageview_place_holder).into(view);
    }

    public InputImage ConvertBitmapToInputImage(Bitmap foBitmap, int fnRotation){
        return InputImage.fromBitmap(foBitmap, fnRotation);
    }

    public InputImage ConvertFilePathToInputImage(Context foContext, Uri foUri){
        try {
            return InputImage.fromFilePath(foContext, foUri);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public InputImage ConvertBitmapToInputImage(Image foImage, int fnRotation){
        return InputImage.fromMediaImage(foImage, fnRotation);
    }

}
