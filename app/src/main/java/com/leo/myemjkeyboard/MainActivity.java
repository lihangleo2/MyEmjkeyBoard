package com.leo.myemjkeyboard;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.leo.myemjkeyboard.Utils.Lemoji;
import com.leo.myemjkeyboard.Utils.LeoOnItemClickManagerUtils;
import com.leo.myemjkeyboard.customview.EmotionKeyboard;
import com.leo.myemjkeyboard.model.ImageModel;
import com.leo.myemjkeyboard.adapter.HorizontalRecyclerviewAdapter;
import com.leo.myemjkeyboard.adapter.NoHorizontalScrollerVPAdapter;
import com.leo.myemjkeyboard.fragment.EmojiFragment;
import com.leo.myemjkeyboard.fragment.TestFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * leo
 * 2019.1.22
 */
public class MainActivity extends AppCompatActivity {
    //表情面板
    private EmotionKeyboard mEmotionKeyboard;

    //软键盘布局
    private EditText edit_content;
    private ImageView emotion_button;
    private TextView text_send;


    //这是底部tab 和 viewPager+fragment的表情布局
    private RecyclerView recyclerview_horizontal;
    private HorizontalRecyclerviewAdapter horizontalRecyclerviewAdapter;

    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ViewPager viewPager;

    ArrayList<ImageModel> sourceList = new ArrayList<>();
    private String beforeStr = "";//还没打字前


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edit_content = findViewById(R.id.bar_edit_text);
        emotion_button = findViewById(R.id.emotion_button);
        viewPager = findViewById(R.id.viewPager);
        text_send = findViewById(R.id.text_send);


        recyclerview_horizontal = findViewById(R.id.recyclerview_horizontal);
        mEmotionKeyboard = EmotionKeyboard.with(MainActivity.this)
                .setEmotionView(findViewById(R.id.ll_emotion_layout))//绑定表情面板
                .bindToContent(findViewById(R.id.listview))//绑定内容view
                .bindToEditText(edit_content)//判断绑定那种EditView
                .bindToEmotionButton(emotion_button)//绑定表情按钮
                .build();


        findViewById(R.id.img_voice).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "此功能尚在开发", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.image_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "此功能尚在开发", Toast.LENGTH_SHORT).show();
            }
        });

        initData();
        /*
         * 注意这里如果只用到系统表情可用GlobalOnItemClickManagerUtils
         *
         * 既支持系统表情  又支持 自定义表情 用LeoOnItemClickManagerUtils 具体我会讲解。
         * */
        LeoOnItemClickManagerUtils.getInstance(MainActivity.this).attachToEditText(edit_content);

        edit_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                beforeStr = charSequence.toString();
                Log.e("是否有数据呢", beforeStr + "======");
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (TextUtils.isEmpty(editable.toString())) {
                    text_send.setVisibility(View.GONE);
                } else {
                    //这层判断的意思是 只有之前是空数据，到有数据的那一次才会运行这个方法
                    if (TextUtils.isEmpty(beforeStr)) {
                        text_send.setVisibility(View.VISIBLE);
                        Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.set_send_show);
                        text_send.startAnimation(animation);
                    }
                }
            }
        });

        text_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "此为发送", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private int oldPosition = 0;

    private void initData() {
        ImageModel model1 = new ImageModel();
        model1.icon = getResources().getDrawable(R.mipmap.emj_xiao);
        model1.flag = "经典笑脸";
        model1.isSelected = true;
        sourceList.add(model1);

        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                ImageModel model2 = new ImageModel();
                model2.icon = getResources().getDrawable(R.mipmap.gole);
                model2.flag = "其他";
                model2.isSelected = false;
                sourceList.add(model2);
            } else if (i == 1) {
                ImageModel model2 = new ImageModel();
                model2.icon = getResources().getDrawable(R.drawable.dding1);
                model2.flag = "逗比";
                model2.isSelected = false;
                sourceList.add(model2);
            } else {
                ImageModel model2 = new ImageModel();
                model2.icon = getResources().getDrawable(R.mipmap.emj_add);
                model2.flag = "其他";
                model2.isSelected = false;
                sourceList.add(model2);
            }

        }

        //底部tab
        horizontalRecyclerviewAdapter = new HorizontalRecyclerviewAdapter(MainActivity.this, sourceList);
        recyclerview_horizontal.setHasFixedSize(true);//使RecyclerView保持固定的大小,这样会提高RecyclerView的性能
        recyclerview_horizontal.setAdapter(horizontalRecyclerviewAdapter);
        recyclerview_horizontal.setLayoutManager(new GridLayoutManager(MainActivity.this, 1, GridLayoutManager.HORIZONTAL, false));
        //初始化recyclerview_horizontal监听器
        horizontalRecyclerviewAdapter.setOnClickItemListener(new HorizontalRecyclerviewAdapter.OnClickItemListener() {
            @Override
            public void onItemClick(View view, int position, List<ImageModel> datas) {
                //修改背景颜色的标记
                datas.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                datas.get(position).isSelected = true;
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(position);

                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
                oldPosition = position;
            }

            @Override
            public void onItemLongClick(View view, int position, List<ImageModel> datas) {
            }
        });

        fragments.add(EmojiFragment.newInstance(Lemoji.DATA));
        fragments.add(EmojiFragment.newInstance(Lemoji.SUNDATA));
        fragments.add(EmojiFragment.newInstance(Lemoji.MYFACE));
        fragments.add(TestFragment.newInstance(4));
        fragments.add(TestFragment.newInstance(5));

        NoHorizontalScrollerVPAdapter adapter = new NoHorizontalScrollerVPAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(adapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {

                //修改背景颜色的标记
                sourceList.get(oldPosition).isSelected = false;
                //记录当前被选中tab下标
                sourceList.get(position).isSelected = true;
                //通知更新，这里我们选择性更新就行了
                horizontalRecyclerviewAdapter.notifyItemChanged(oldPosition);
                horizontalRecyclerviewAdapter.notifyItemChanged(position);

                //viewpager界面切换
                viewPager.setCurrentItem(position, false);
                oldPosition = position;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Log.e("isClick", "11");
        if (ev != null && ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                mEmotionKeyboard.closeAndEmj();
            }
        }
        return super.dispatchTouchEvent(ev);
    }


    /**
     * 判断当前点击屏幕的地方是否是软键盘：
     * 根据点击Y坐标和当前edittext的Y轴坐标进行比对，高于的话 那么就是点击在软键盘外
     * 收起软键盘
     */
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] leftTop = {0, 0};
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            /*
             * 重点 : 这里坐了个判断 只要是点击键盘上方。收起软件
             * */
            if (event.getY() > top) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }
}
