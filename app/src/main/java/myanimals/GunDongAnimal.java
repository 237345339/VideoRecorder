package myanimals;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by Administrator on 2019/3/11.
 */

public class GunDongAnimal implements ValueAnimator.AnimatorUpdateListener{
    View view;
    AfterGunDongAnimal afterGunDongAnimal;
    public GunDongAnimal(View view) {
        this.view = view;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float f = ((Integer) valueAnimator.getAnimatedValue()).floatValue();
        view.setRotation(f);
        afterGunDongAnimal.gundongData(view);
    }

    public interface  AfterGunDongAnimal{
        public void gundongData(View view);

    }

    public void setAfterGunDongAnimal(AfterGunDongAnimal afterGunDongAnimal) {
        this.afterGunDongAnimal = afterGunDongAnimal;
    }
}
