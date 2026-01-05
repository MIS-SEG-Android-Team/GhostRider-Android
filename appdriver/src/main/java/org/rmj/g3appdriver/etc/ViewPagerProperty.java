package org.rmj.g3appdriver.etc;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

public class ViewPagerProperty {

    private ViewPager2 object;

    public ViewPagerProperty(ViewPager2 object){
        this.object = object;
    }

    public void initSliderPadding(Padding_Property loProperties){

        /**THIS PROPERTIES MAKE THE ITEMS VISIBLE TO THE CURRENT ITEMS VIEW.
         * IT WILL ONLY WORK IF PADDING ON BOTH SIDES OF THE VIEWPAGER IS SET**/

        //todo: set viewpager properties, it helps to reduce space between items to make it closer to current item view

        object.setPaddingRelative(loProperties.paddingLeft, loProperties.paddingTop,loProperties.paddingRight, loProperties.paddingBottom);
        object.setClipToPadding(loProperties.clipPadding());
        object.setClipChildren(loProperties.clipChildren());
        object.setOffscreenPageLimit(loProperties.offscreenPageLimit());
        object.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

    }

    public void initSliderPageTransformer(){

        //todo: add viewpager page scaling, helps to reduce the scale view of other items and float the current item
        CompositePageTransformer loCompositePageTransformer = new CompositePageTransformer();
        loCompositePageTransformer.addTransformer(new MarginPageTransformer(40));
        loCompositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                page.setElevation(0.1f);
            }
        });

        object.setPageTransformer(loCompositePageTransformer);
    }

    public record Padding_Property(int paddingLeft, int paddingRight, int paddingTop,
                                   int paddingBottom, Boolean clipPadding, Boolean clipChildren,
                                   int offscreenPageLimit) {

    }
}
