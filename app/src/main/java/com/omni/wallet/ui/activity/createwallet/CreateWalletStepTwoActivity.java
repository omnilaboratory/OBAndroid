package com.omni.wallet.ui.activity.createwallet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.entity.event.CloseUselessActivityEvent;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.listItems.SelectSeedItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateWalletStepTwoActivity extends AppBaseActivity {
    private ArrayList<EditText> list = new ArrayList<>();

    @BindView(R.id.seed_select_pager)
    ViewPager pagerView;

    @BindView(R.id.seed_inputs_grid_view)
    GridView seedInputsGridView;

    GridView seedSelectGridViewPageOne;
    GridView seedSelectGridViewPageTwo;

    Context ctx = CreateWalletStepTwoActivity.this;
    List<SelectSeedItem> seedListPageOne = new ArrayList<>();
    List<SelectSeedItem> seedListPageTwo = new ArrayList<>();
    List<String> seedsInputList = new ArrayList<>();
    String[] gotSeedArray;
    SeedsInputAdapter seedsInputAdapter;
    SeedsSelectAdapter seedsSelectAdapter;
    SeedsSelectAdapterForPageTwo seedsSelectAdapterForPageTwo;
    private MyPagerAdapter myPagerAdapter;
    public ArrayList<View> pageList = new ArrayList<>();


    @Override
    protected Drawable getWindowBackground() {
        return ContextCompat.getDrawable(mContext, R.color.color_f9f9f9);
    }


    @Override
    protected int getContentView() {
        return R.layout.activity_create_wallet_step_two;
    }

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        seedsInputAdapter = new SeedsInputAdapter();
        seedsSelectAdapter = new SeedsSelectAdapter();
        seedsSelectAdapterForPageTwo = new SeedsSelectAdapterForPageTwo();
        for (int i = 0; i < 24; i++) {
            seedsInputList.add("");
        }
        initSeedArray();
        initSeedsInputGridView();

        initViewPager();

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    protected void initData() { }
    @SuppressLint("InflateParams")
    public void initViewPager(){
        LayoutInflater liOne = getLayoutInflater();
        LayoutInflater liTwo = getLayoutInflater();
         View pageOne = liOne.inflate(R.layout.layout_item_seeds_selected_page,null);
         View pageTwo = liTwo.inflate(R.layout.layout_item_seeds_selected_page,null);
        seedSelectGridViewPageOne = pageOne.findViewById(R.id.seed_select_grid_view_page);
        seedSelectGridViewPageTwo = pageTwo.findViewById(R.id.seed_select_grid_view_page);
        initSeedsSelectGridViewPageOne();
        initSeedsSelectGridViewPageTwo();
        pageList.add(pageOne);
        pageList.add(pageTwo);
        myPagerAdapter = new MyPagerAdapter( pageList);
        pagerView.setAdapter(myPagerAdapter);

    }

    public void initSeedArray(){
        String seedsString = User.getInstance().getSeedString(mContext);
        gotSeedArray = seedsString.split(" ");
        List<String> gotSeedList = new ArrayList<>();
        Collections.addAll(gotSeedList, gotSeedArray);
        Collections.shuffle(gotSeedList);
        for (int i = 0; i < gotSeedList.size(); i++) {
            String seed = gotSeedList.get(i);
            SelectSeedItem seedItem = new SelectSeedItem(seed,false,-1);
            if(i<12){
                seedListPageOne.add(seedItem);
            }else{
                seedListPageTwo.add(seedItem);
            }

        }

    }

    public void initSeedsSelectGridViewPageOne() {
        seedSelectGridViewPageOne.setAdapter(seedsSelectAdapter);
        seedSelectGridViewPageOne.setOnItemClickListener((parent, view, position, id) -> {
            int willSetIndex = 0;
            SelectSeedItem selectSeedItem = seedListPageOne.get(position);
            boolean isSelected = selectSeedItem.isSelected();
            int selectInt = selectSeedItem.getSelectIndex();
            String seedString = selectSeedItem.getSeed();
            for (int i = 0; i<seedsInputList.size();i++){
                if (seedsInputList.get(i).isEmpty()){
                    willSetIndex = i;
                    break;
                }
            }
            if(isSelected){
                selectSeedItem.setSelected(false);
                selectSeedItem.setSelectIndex(-1);
                seedsInputList.set(selectInt,"");
            }else{
                selectSeedItem.setSelected(true);
                selectSeedItem.setSelectIndex(willSetIndex);
                seedsInputList.set(willSetIndex,seedString);
            }
            runOnUiThread(()->{
                seedsSelectAdapter.notifyDataSetChanged();
                seedsInputAdapter.notifyDataSetChanged();
            });

        });
    }

    public void initSeedsSelectGridViewPageTwo() {
        seedSelectGridViewPageTwo.setAdapter(seedsSelectAdapterForPageTwo);
        seedSelectGridViewPageTwo .setOnItemClickListener((parent, view, position, id) -> {
            int willSetIndex = 0;
            SelectSeedItem selectSeedItem = seedListPageTwo.get(position);
            boolean isSelected = selectSeedItem.isSelected();
            int selectInt = selectSeedItem.getSelectIndex();
            String seedString = selectSeedItem.getSeed();
            for (int i = 0; i<seedsInputList.size();i++){
                if (seedsInputList.get(i).isEmpty()){
                    willSetIndex = i;
                    break;
                }
            }
            if(isSelected){
                selectSeedItem.setSelected(false);
                selectSeedItem.setSelectIndex(-1);
                seedsInputList.set(selectInt,"");
            }else{
                selectSeedItem.setSelected(true);
                selectSeedItem.setSelectIndex(willSetIndex);
                seedsInputList.set(willSetIndex,seedString);
            }
            runOnUiThread(()->{
                seedsSelectAdapterForPageTwo.notifyDataSetChanged();
                seedsInputAdapter.notifyDataSetChanged();
            });

        });
    }

    public void initSeedsInputGridView() {
        seedInputsGridView.setAdapter(seedsInputAdapter);
    }

    /**
     * 点击Back
     * click back button
     */
    @OnClick(R.id.btn_back)
    public void clickBack() {
        finish();
    }

    /**
     * 点击Forward
     * click forward button
     */
    @OnClick(R.id.btn_forward)
    public void clickForward() {
        boolean checkResult = true;
        for (int j = 0;j<gotSeedArray.length;j++){
            if(!gotSeedArray[j].equals(seedsInputList.get(j))){
                checkResult = false;
                String toastTextHead = getResources().getString(R.string.toast_check_seeds_wrong_head);
                String toastTextEnd = getResources().getString(R.string.toast_check_seeds_wrong_end);
                String toastText = toastTextHead + (j + 1) + "" + toastTextEnd;
                Toast checkWrongToast = Toast.makeText(CreateWalletStepTwoActivity.this,toastText,Toast.LENGTH_LONG);
                checkWrongToast.setGravity(Gravity.TOP,0,40);
                checkWrongToast.show();
                break;
            }
        }
        if(checkResult){
            User.getInstance().setInitWalletType(mContext,"createStepTwo");
            switchActivity(CreateWalletStepThreeActivity.class);
        }
    }

    @OnClick(R.id.description_text_clear)
    public void clickForClearSeeds() {
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setText("");
        }
    }

    class SeedsInputAdapter extends BaseAdapter {

        SeedsInputAdapter() {
        }

        @Override
        public int getCount() {
            return seedsInputList != null ? seedsInputList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return seedsInputList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_item_input_seed, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.setText(R.id.seed_idx, (position + 1) + ".");
            TextView textView = viewHolder.getView(R.id.seed_text);
            if (!seedsInputList.get(position).isEmpty()) {
                textView.setText(seedsInputList.get(position));
                if (seedsInputList.get(position).equals(gotSeedArray[position])){
                    textView.setTextColor(Color.WHITE);
                }else{
                    textView.setTextColor(Color.RED);
                }
            } else {
                textView.setText("------");
                textView.setTextColor(Color.WHITE);
            }

            return convertView;
        }
    }

    class SeedsSelectAdapter extends BaseAdapter {

        SeedsSelectAdapter() {
        }

        @Override
        public int getCount() {
            return seedListPageOne != null ? seedListPageOne.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return seedListPageOne.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_item_select_seed, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            boolean isSelected = seedListPageOne.get(position).isSelected();
            viewHolder.setText(R.id.select_seed_text, seedListPageOne.get(position).getSeed());
            LinearLayout contentView = viewHolder.getView(R.id.select_seed_view_content);
            TextView textView = viewHolder.getView(R.id.select_seed_text);
            if(isSelected){
                contentView.setBackground(getDrawable(R.drawable.bg_btn_round_blue_4));
                textView.setTextColor(Color.WHITE);
            }else{
                contentView.setBackground(getDrawable(R.drawable.bg_btn_round_white_4));
                textView.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    class SeedsSelectAdapterForPageTwo extends BaseAdapter {

        SeedsSelectAdapterForPageTwo() {
        }

        @Override
        public int getCount() {
            return seedListPageTwo != null ? seedListPageTwo.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return seedListPageTwo.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.layout_item_select_seed, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            boolean isSelected = seedListPageTwo.get(position).isSelected();
            viewHolder.setText(R.id.select_seed_text, seedListPageTwo.get(position).getSeed());
            LinearLayout contentView = viewHolder.getView(R.id.select_seed_view_content);
            TextView textView = viewHolder.getView(R.id.select_seed_text);
            if(isSelected){
                contentView.setBackground(getDrawable(R.drawable.bg_btn_round_blue_4));
                textView.setTextColor(Color.WHITE);
            }else{
                contentView.setBackground(getDrawable(R.drawable.bg_btn_round_white_4));
                textView.setTextColor(Color.BLACK);
            }

            return convertView;
        }
    }

    class MyPagerAdapter extends PagerAdapter{

        private ArrayList<View> viewLists;

        public MyPagerAdapter(){}

        MyPagerAdapter(ArrayList<View> viewLists) {
            super();
            this.viewLists = viewLists;
        }

        @Override
        public int getCount() {
            return viewLists.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @NonNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(viewLists.get(position));
            return viewLists.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, @NonNull Object object) {
            container.removeView(viewLists.get(position));
        }

    }
    
    @Subscribe(threadMode = ThreadMode.MAIN)
        public void onCloseUselessActivityEvent(CloseUselessActivityEvent event) {
            finish();
        }

}
