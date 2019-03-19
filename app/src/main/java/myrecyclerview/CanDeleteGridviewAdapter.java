package myrecyclerview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alanjet.videorecordertest.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import myanimals.DeleteAnimal;
import myanimals.GunDongAnimal;

/**
 * Created by Administrator on 2019/3/11.
 */

public class CanDeleteGridviewAdapter extends RecyclerView.Adapter {
    private Context mContext;
    private List<Map<String, Object>> list;
    private LayoutInflater li;
    View viewLayout;

    public CanDeleteGridviewAdapter(Context context, List<Map<String, Object>> items) {
        mContext = context;
        list = items;
        li = LayoutInflater.from(mContext);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        viewLayout = li.inflate(R.layout.item_show_videos, parent, false);
        return new InnerViewHolder(viewLayout);
    }


    List<ImageView> tempIVHs = new ArrayList<>();
    List<ImageView> tempIV_IMGs = new ArrayList<>();

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final InnerViewHolder ivh = (InnerViewHolder) holder;
        tempIVHs.add(ivh.iv);
        tempIV_IMGs.add(ivh.image_item);
        Map<String, Object> map = list.get(position);
        ivh.text_item.setText((String) map.get("textItem"));
        ivh.image_item.setImageResource((int) map.get("imageItem"));
        ivh.image_item.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (view.getTag() == null)
                    view.setTag(true);
                if ((boolean) view.getTag()) {
                    view.setTag(false);
                    beginShowCloser1(ivh.image_item);
//                    beginShowCloserMutil();
                    return false;
                } else {
                    view.setTag(true);
                    beginHideCloser();

                }
                return false;
            }

        });
        ivh.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAnimal(ivh.image_item, position, holder);

            }
        });
    }

    /**
     * 为了隐藏所有关闭view
     */
    private void beginHideCloser() {
        for (ImageView ivdelete : tempIVHs) {
            ivdelete.setBackground(null);
        }

    }
    /**
     * 为了显示所有关闭view,晃动一个
     * @param image_item
     */
    private void beginShowCloser1(ImageView image_item) {
        for (ImageView iv : tempIVHs) {

            beginGunDong1(iv,image_item);
        }

    }


    private boolean hasHuangDong=false;
    /**
     * 为了显示所有关闭view,晃动所有
     */
    private void beginShowCloserMutil() {
        for(int i=0;i<tempIV_IMGs.size();i++){
            beginGunDongMutil(tempIVHs.get(i),tempIV_IMGs.get(i));
        }
    }

    /**
     * 为了让所有item都产生动画晃动的动画
     * @param img
     * @param iv
     */
    private void beginGunDong1(final ImageView iv,ImageView img) {
        ValueAnimator va = ValueAnimator.ofInt(0, 10, 0);
        va.setDuration(50);
        va.setStartDelay(50);
        va.setRepeatCount(4);
        va.setRepeatMode(ValueAnimator.REVERSE);
        GunDongAnimal gunDongAnimal = new GunDongAnimal(img);
        gunDongAnimal.setAfterGunDongAnimal(new GunDongAnimal.AfterGunDongAnimal() {
            @Override
            public void gundongData(View view) {
                iv.setBackground(mContext.getResources().getDrawable(R.mipmap.edit_clear));
            }
        });
        va.addUpdateListener(gunDongAnimal);
        va.start();
    }
    /**
     * 为了让所有item都产生动画晃动的动画
     * @param img
     * @param iv
     */
    private void beginGunDongMutil(final ImageView iv,ImageView img) {
        ValueAnimator va = ValueAnimator.ofInt(0, 10, 0);
        va.setDuration(50);
        va.setStartDelay(50);
        va.setRepeatCount(4);
        va.setRepeatMode(ValueAnimator.REVERSE);
        GunDongAnimal gunDongAnimal = new GunDongAnimal(img);
        gunDongAnimal.setAfterGunDongAnimal(new GunDongAnimal.AfterGunDongAnimal() {
            @Override
            public void gundongData(View view) {
                iv.setBackground(mContext.getResources().getDrawable(R.mipmap.edit_clear));
                hasHuangDong=true;
            }
        });
        va.addUpdateListener(gunDongAnimal);
        va.start();
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    private class InnerViewHolder extends RecyclerView.ViewHolder {

        ImageView iv;
        TextView text_item;
        ImageView image_item;

        public InnerViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.deleteBtn);
            text_item = (TextView) itemView.findViewById(R.id.text_item);
            image_item = (ImageView) itemView.findViewById(R.id.image_item);
        }
    }

    private void deleteAnimal(View view, int position, final RecyclerView.ViewHolder holder) {
        ValueAnimator va = ValueAnimator.ofInt(0, 360);
        va.setDuration(500);
        va.setStartDelay(50);
        DeleteAnimal deleteAnimal = new DeleteAnimal(view, position);
        deleteAnimal.setAfterDeleteAnimal(new DeleteAnimal.AfterDeleteAnimal() {
            @Override
            public void deleteData(View view, int mPosition) {
                list.remove(mPosition);
                beginHideCloser();

                //                CanDeleteGridviewAdapter.this.notifyDataSetChanged();//不断的刷新如果过快会崩溃
                CanDeleteGridviewAdapter.this.notifyItemRemoved(mPosition);
                CanDeleteGridviewAdapter.this.notifyItemRangeChanged(mPosition, list.size() - mPosition);

            }
        });
        va.addUpdateListener(deleteAnimal);
        va.start();
    }

    public void setCanDeleteCallBack(CanDeleteCallBack canDeleteCallBack) {
        this.canDeleteCallBack = canDeleteCallBack;
    }

    CanDeleteCallBack canDeleteCallBack;

    public interface CanDeleteCallBack {
        void pointDeleteAnimal(View viewLayout, int position);
    }
}
