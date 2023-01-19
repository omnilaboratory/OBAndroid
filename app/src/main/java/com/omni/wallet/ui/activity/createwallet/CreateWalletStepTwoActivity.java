package com.omni.wallet.ui.activity.createwallet;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.omni.wallet.R;
import com.omni.wallet.base.AppBaseActivity;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.framelibrary.entity.User;
import com.omni.wallet.listItems.SelectSeedItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class CreateWalletStepTwoActivity extends AppBaseActivity {

    final String TAG = CreateWalletStepTwoActivity.class.getSimpleName();
    private ArrayList<EditText> list = new ArrayList<>();

    @BindView(R.id.seed_inputs_grid_view)
    GridView seedInputsGridView;

    @BindView(R.id.seed_select_grid_view)
    GridView seedSelectGridView;

    Context ctx = CreateWalletStepTwoActivity.this;
    List<SelectSeedItem> seedList = new ArrayList<>();
    List<String> seedsInputList = new ArrayList<>();
    String[] gotSeedArray;
    SeedsInputAdapter seedsInputAdapter;
    SeedsSelectAdapter seedsSelectAdapter;


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
        seedsInputAdapter = new SeedsInputAdapter(mContext);
        seedsSelectAdapter = new SeedsSelectAdapter(mContext);
        for (int i = 0; i < 24; i++) {
            seedsInputList.add("");
        }
        initSeedArray();
        initSeedsInputGridView();
        initSeedsSelectGridView();

    }


    @Override
    protected void initData() { }

    public void initSeedArray(){
        String seedsString = User.getInstance().getSeedString(mContext);
        gotSeedArray = seedsString.split(" ");
        List<String> gotSeedList = new ArrayList<>();
        for (int i = 0; i < gotSeedArray.length; i++) {
            gotSeedList.add(gotSeedArray[i]);
        }
        Collections.shuffle(gotSeedList);
        for (int i = 0; i < gotSeedList.size(); i++) {
            String seed = gotSeedList.get(i);
            SelectSeedItem seedItem = new SelectSeedItem(seed,false,-1);
            seedList.add(seedItem);
        }

    }

    public void initSeedsSelectGridView() {
        seedSelectGridView.setAdapter(seedsSelectAdapter);
        seedSelectGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int willSetIndex = 0;
                SelectSeedItem selectSeedItem = seedList.get(position);
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

            }
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
                String toastText = toastTextHead + Integer.toString(j+1) + "" + toastTextEnd;
                Toast checkWrongToast = Toast.makeText(CreateWalletStepTwoActivity.this,toastText,Toast.LENGTH_LONG);
                checkWrongToast.setGravity(Gravity.TOP,0,40);
                checkWrongToast.show();
                break;
            }
        }
        if(checkResult){
            User.getInstance().setSeedChecked(mContext,true);
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

        private Context mContext;

        public SeedsInputAdapter(Context context) {
            this.mContext = context;
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

        private Context mContext;

        public SeedsSelectAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return seedList != null ? seedList.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return seedList.get(position);
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
            boolean isSelected = seedList.get(position).isSelected();
            viewHolder.setText(R.id.select_seed_text, seedList.get(position).getSeed());
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

}
