package com.alanjet.videorecordertest;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import myanimals.DeleteAnimal;
import myrecyclerview.CanDeleteGridviewAdapter;
import myrecyclerview.GridSpacingItemDecoration;
import myrecyclerview.MyGridLayoutManager;

/**
 * Created by alanjet on 2016/7/24.
 */
public class ShowVideoActivity extends AppCompatActivity {
    private RecyclerView gridView;
    private List<Map<String, Object>> items;
    private List<File> videoList;
    private ImageView deleteBtn;
    CanDeleteGridviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_video);
        gridView = (RecyclerView) findViewById(R.id.gv_show_video);
        videoList = new ArrayList<File>();

//        ArrayList<String> strs = testAnimal();



        ArrayList<String> strs =   createMVFileList();



        items = new ArrayList<Map<String, Object>>();

        String[] strss = strs.toArray(new String[strs.size()]);
        for (int a = 0; a < strss.length - 1; a++) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("imageItem", R.drawable.folder);
            maps.put("textItem", strss[a]);
            items.add(maps);
        }


        MyGridLayoutManager gridLayoutManager = new MyGridLayoutManager(this, 3,
                GridLayoutManager.VERTICAL, false);
        gridView.setLayoutManager(gridLayoutManager);
        GridSpacingItemDecoration decoration = new GridSpacingItemDecoration(3, 2, true);
        gridView.addItemDecoration(decoration);
        gridView.setHasFixedSize(true);

        adapter = new CanDeleteGridviewAdapter(this, items);
//        gridView.setHasFixedSize(true);
        gridView.setAdapter(adapter);



    }

    private ArrayList<String> createMVFileList() {
        ArrayList<String> strs=new ArrayList<>();
        try {
            File   dir = new File(Environment.getExternalStorageDirectory()//内部存储/Test
                    .getCanonicalFile() + "/Test");
            if(dir.exists()){
                for(String str :dir.list()){
                    strs.add(str);
                };
            }else{
                dir.mkdir();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return strs;
    }

    /**
     * 测试动画
     * @return
     */
    private ArrayList<String> testAnimal() {
        ArrayList<String> strs=new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strs.add("xxxx_" + i);
        }
        return strs;
    }

    private void deleteAnimal(View view, int position) {
        ValueAnimator va = ValueAnimator.ofInt(0, 360);
        va.setDuration(500);
        va.setStartDelay(50);
        DeleteAnimal deleteAnimal = new DeleteAnimal(view, position);
        deleteAnimal.setAfterDeleteAnimal(new DeleteAnimal.AfterDeleteAnimal() {
            @Override
            public void deleteData(View view, int mPosition) {
                items.remove(mPosition);
                //                CanDeleteGridviewAdapter.this.notifyDataSetChanged();//不断的刷新如果过快会崩溃
//                adapter.notifyItemChanged(mPosition);
                adapter.notifyDataSetChanged();
            }
        });
        va.addUpdateListener(deleteAnimal);
        va.start();
    }
}
