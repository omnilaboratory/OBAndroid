package com.omni.wallet.view.popupwindow;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.omni.wallet.R;
import com.omni.wallet.baselibrary.view.BasePopWindow;
import com.omni.wallet.baselibrary.view.recyclerView.adapter.CommonRecyclerAdapter;
import com.omni.wallet.baselibrary.view.recyclerView.holder.ViewHolder;
import com.omni.wallet.listItems.Node;
import com.omni.wallet.listItems.NodeGroup;
import com.omni.wallet.utils.GetResourceUtil;

import java.util.ArrayList;
import java.util.List;

public class SelectNodePopupWindow {
    private static final String TAG = SelectNodePopupWindow.class.getSimpleName();

    public Context selectNodeContext;
    public BasePopWindow selectNodeBasePopWindow;
    public List<NodeGroup> nodeGroupList=new ArrayList<>();
    public NodeGroupAdapter nodeGroupAdapter;

    public SelectNodePopupWindow(Context context){
        this.selectNodeContext = context;
    }

    public void initNodeGroupData(){
        List<String> tokenListA = new ArrayList<>();
        tokenListA.add("Doallar");
        tokenListA.add("BTC");
        List<String> tokenListB = new ArrayList<>();
        tokenListB.add("Doallar");
        List<String> tokenListC = new ArrayList<>();
        tokenListC.add("BTC");

        List<Node> nodeListA = new ArrayList<>();
        Node nodeA1 = new Node("Alice",tokenListA,150000);
        Node nodeA2 = new Node("Allen",tokenListB,20000);
        nodeListA.add(nodeA1);
        nodeListA.add(nodeA2);
        NodeGroup groupA = new NodeGroup(nodeListA,"A");

        List<Node> nodeListB = new ArrayList<>();
        Node nodeB1 = new Node("Bob",tokenListA,150000);
        Node nodeB2 = new Node("Bobby",tokenListB,20000);
        Node nodeB3 = new Node("Bush",tokenListB,80000);
        nodeListB.add(nodeB1);
        nodeListB.add(nodeB2);
        nodeListB.add(nodeB3);
        NodeGroup groupB = new NodeGroup(nodeListB,"B");

        List<Node> nodeListC = new ArrayList<>();
        Node nodeC1 = new Node("Cathy",tokenListA,150000);
        Node nodeC2 = new Node("Cinda",tokenListB,20000);
        Node nodeC3 = new Node("Carol",tokenListC,40000);
        nodeListC.add(nodeC1);
        nodeListC.add(nodeC2);
        nodeListC.add(nodeC3);
        NodeGroup groupC = new NodeGroup(nodeListC,"C");

        nodeGroupList.add(groupA);
        nodeGroupList.add(groupB);
        nodeGroupList.add(groupC);
    }

    public void show(final View view){
        if (selectNodeBasePopWindow == null){
            selectNodeBasePopWindow = new BasePopWindow(selectNodeContext);
            View rootView = selectNodeBasePopWindow.setContentView(R.layout.layout_popupwindow_select_node);
            selectNodeBasePopWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            selectNodeBasePopWindow.setHeight(WindowManager.LayoutParams.MATCH_PARENT);
            selectNodeBasePopWindow.setAnimationStyle(R.style.popup_anim_style);

            rootView.findViewById(R.id.iv_close_btn).setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    selectNodeBasePopWindow.dismiss();
                }
            });
            if (selectNodeBasePopWindow.isShowing()){
                return;
            }
//            initNodeGroupData();
            RecyclerView nodeGroupRecyclerView = rootView.findViewById(R.id.rv_node_group_list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(selectNodeContext);
            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
            nodeGroupRecyclerView.setLayoutManager(layoutManager);
            nodeGroupAdapter = new NodeGroupAdapter(selectNodeContext,nodeGroupList,R.layout.layout_item_node_group_list);
            nodeGroupRecyclerView.setAdapter(nodeGroupAdapter);
            selectNodeBasePopWindow.showAtLocation(view, Gravity.CENTER,0,0);
        }
    }

    private class NodeGroupAdapter extends CommonRecyclerAdapter<NodeGroup>{

        public NodeGroupAdapter(Context context,List<NodeGroup> data,int layoutId) {
            super(context,data,layoutId);
        }

        @Override
        public void convert(ViewHolder holder, int position, NodeGroup item) {
            holder.setText(R.id.tv_group_name,item.getGroupName());

            LinearLayout listContentView = holder.getView(R.id.lv_group_content);

            List<Node> nodeListInGroup = item.getNodeList();

            for (int i = 0; i < nodeListInGroup.size(); i++){
                Node singleNodeItem =  nodeListInGroup.get(i);
                // node content
                // node内容容器
                LinearLayout nodeItemView = new LinearLayout(selectNodeContext);
                LinearLayout.LayoutParams nodeItemParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                nodeItemParams.setMargins(0,20,0,0);
                nodeItemView.setOrientation(LinearLayout.VERTICAL);
                nodeItemView.setLayoutParams(nodeItemParams);
                // set node name
                // 设定node名称
                String nodeName = singleNodeItem.getNodeName();
                TextView nodeNameView = new TextView(selectNodeContext);
                RelativeLayout.LayoutParams nodeNameParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                nodeNameView.setText(nodeName);
                nodeNameView.setTextSize(16);
                nodeNameView.setTextColor(GetResourceUtil.getColorId(selectNodeContext,R.color.color_99000000));
                nodeNameView.setLayoutParams(nodeNameParams);

                nodeItemView.addView(nodeNameView);
                // set node information content
                // 设定node信息容器
                LinearLayout infoContentView = new LinearLayout(selectNodeContext);
                LinearLayout.LayoutParams infoContentParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                infoContentView.setOrientation(LinearLayout.HORIZONTAL);
                infoContentView.setGravity(Gravity.CENTER);
                infoContentView.setLayoutParams(infoContentParams);
                // set token picture content
                // 设定Token图片容器
                RelativeLayout tokenContentView = new RelativeLayout(selectNodeContext);
                RelativeLayout.LayoutParams tokenContentParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                tokenContentView.setLayoutParams(tokenContentParams);
                // create picture
                // 生成图片内容
                List<String> tokenList = singleNodeItem.getTokenList();
                for (int j =0;j<tokenList.size();j++){
                    String token = tokenList.get(j);
                    int margin = (tokenList.size() - 1 - j)*40;
                    ImageView tokenImageView = new ImageView(selectNodeContext);
                    LinearLayout.LayoutParams tokenImageParams = new LinearLayout.LayoutParams(60,60);
                    tokenImageView.setImageResource(GetResourceUtil.getTokenImageId(selectNodeContext,token));
                    tokenImageParams.setMarginStart(margin);
                    tokenImageView.setLayoutParams(tokenImageParams);
                    tokenContentView.addView(tokenImageView);
                }
                infoContentView.addView(tokenContentView);
                // Sum of values content
                // valueSum 容器
                LinearLayout valueSumContentView = new LinearLayout(selectNodeContext);
                LinearLayout.LayoutParams valueSumContentParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);
                valueSumContentView.setLayoutParams(valueSumContentParams);
                valueSumContentView.setOrientation(LinearLayout.HORIZONTAL);

                TextView valueSumView = new TextView(selectNodeContext);
                LinearLayout.LayoutParams valueSumParams = new LinearLayout.LayoutParams(0,LinearLayout.LayoutParams.WRAP_CONTENT,1);
                valueSumContentParams.setMarginEnd(5);
                valueSumView.setTextColor(GetResourceUtil.getColorId(selectNodeContext,R.color.color_99000000));
                valueSumView.setTextSize(16);
                valueSumView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
                valueSumView.setText(Double.toString(singleNodeItem.getValueSum()));
                valueSumView.setLayoutParams(valueSumParams);
                valueSumContentView.addView(valueSumView);

                TextView valueUnitView = new TextView(selectNodeContext);
                LinearLayout.LayoutParams valueUnitParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                valueUnitView.setTextColor(GetResourceUtil.getColorId(selectNodeContext,R.color.color_99000000));
                valueUnitView.setTextSize(16);
                valueUnitView.setText("USD");
                valueUnitView.setLayoutParams(valueUnitParams);
                valueSumContentView.addView(valueUnitView);


                infoContentView.addView(valueSumContentView);

                nodeItemView.addView(infoContentView);

                listContentView.addView(nodeItemView);
            }

        }
    }

    public void release()  {
        if (selectNodeBasePopWindow != null){
            selectNodeBasePopWindow.dismiss();
            selectNodeBasePopWindow = null;
        }
    }
}

