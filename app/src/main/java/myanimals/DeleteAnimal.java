package myanimals;

import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by Administrator on 2019/3/11.
 */
public class DeleteAnimal implements ValueAnimator.AnimatorUpdateListener {
    AfterDeleteAnimal afterDeleteAnimal;
    View view;
    int mPosition=0;

    public DeleteAnimal(View view, int position) {
        this.view = view;
        this.mPosition=position;
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        float defaut = 360;
        float f = ((Integer) valueAnimator.getAnimatedValue()).floatValue();
        view.setRotation(f);
        if (f == defaut) {
//            view.setVisibility(View.GONE);
            afterDeleteAnimal.deleteData(view,mPosition);
        }
    }

    public void setAfterDeleteAnimal(AfterDeleteAnimal afterDeleteAnimal) {
        this.afterDeleteAnimal = afterDeleteAnimal;
    }

    public interface AfterDeleteAnimal {
        public void deleteData(View view, int mPosition);

    }

}
