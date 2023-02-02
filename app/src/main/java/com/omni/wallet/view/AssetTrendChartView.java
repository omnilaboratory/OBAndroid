package com.omni.wallet.view;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.omni.wallet.R;
import com.omni.wallet.entity.AssetTrendEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 汉: 资产走势图
 * En: AssetTrendChartView
 * author: guoyalei
 * date: 2023/1/12
 */
public class AssetTrendChartView extends LinearLayout {
    private static final String TAG = AssetTrendChartView.class.getSimpleName();
    private Context mContext;
    @BindView(R.id.view_asset_trend_chart)
    public LineChart lineChart;

    public AssetTrendChartView(Context context) {
        this(context, null);
    }

    public AssetTrendChartView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AssetTrendChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        initView();
    }

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.view_asset_trend_chart, this);
        ButterKnife.bind(this, rootView);
        // 初始化隐藏
        setVisibility(GONE);
    }

    public void setViewShow(List<AssetTrendEntity> result) {
        if (result.size() == 0 || result == null) {
            setVisibility(GONE);
            return;
        }
        setVisibility(GONE);
        //
        //配置基本信息
        lineChart.getDescription().setEnabled(false);   //设置描述
        lineChart.setTouchEnabled(true);    //设置是否可以触摸
        lineChart.setDragDecelerationFrictionCoef(0.9f);    //设置滚动时的速度快慢
        lineChart.setDragEnabled(true);     // 是否可以拖拽
        lineChart.setScaleXEnabled(true);   //设置X轴是否能够放大
        lineChart.setScaleYEnabled(true);  //设置Y轴是否能够放大
        lineChart.setScaleEnabled(true);    // 是否可以缩放 x和y轴, 默认是true
        lineChart.setDrawGridBackground(false);//设置图表内格子背景是否显示，默认是false
        lineChart.setHighlightPerDragEnabled(true);//能否拖拽高亮线(数据点与坐标的提示线)，默认是true
        lineChart.setBackgroundColor(Color.WHITE);  //设置背景颜色
        lineChart.animateX(2000);
        //配置X轴属性
        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setLabelRotationAngle(25f);  //设置旋转偏移量
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //设置X轴的位置
        //设置标签文本格式
        xAxis.setTextSize(10f);
        //设置标签文本颜色
        xAxis.setTextColor(Color.parseColor("#999999"));
        //是否绘制轴线
        xAxis.setDrawAxisLine(true);
        //是否绘制网格线
        xAxis.setDrawGridLines(false);
        //设置是否一个格子显示一条数据，如果不设置这个属性，就会导致X轴数据重复并且错乱的问题
        xAxis.setGranularity(1f);
        //给X轴添加标签
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int index = (int) value;
                if (index < 0 || index >= result.size()) {
                    return "";
                }
                return result.get(index).getTime();
            }
        };
        // 引用标签
        xAxis.setValueFormatter(formatter);
        //配置Y轴信息
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);   //设置Y轴的位置
        leftAxis.setDrawGridLines(true);    //设置左边的网格线显示
        leftAxis.setGranularityEnabled(false);//启用在放大时限制y轴间隔的粒度特性。默认值：false。
        leftAxis.setTextColor(Color.parseColor("#999999")); //设置Y轴文字颜色
        leftAxis.setAxisMinimum(0f);
        YAxis rightAxis = lineChart.getAxisRight(); //获取右边的Y轴
        rightAxis.setEnabled(false);   //设置右边的Y轴不显示
        //设置图例（也就是曲线的标签）
        Legend legend = lineChart.getLegend();//设置比例图
        legend.setEnabled(false);   //因为自带的图例太丑，而且操作也不方便，楼主选择自定义，设置不显示比例图
        setManyDatas(result);
    }

    /**
     * 设置多条折线的数据
     */
    private void setManyDatas(List<AssetTrendEntity> result) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();   //线条数据集合
        //设置单条折现的Y轴数据
        ArrayList<Entry> yList = new ArrayList<Entry>();
        for (int j = 0; j < result.size(); j++) {
            float value = Float.parseFloat(result.get(j).getAsset());
            yList.add(new Entry(j, value));
        }
        LineDataSet lineDataSet = new LineDataSet(yList, "");   //设置单条折线
        //设置折线的属性
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);   //设置左右两边Y轴节点描述
        lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue()); //设置节点文字颜色
        lineDataSet.setDrawCircles(false);  //设置是否显示节点的小圆点
        lineDataSet.setDrawValues(false);       //设置是否显示节点的值
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));//当点击节点时，将会出现与节点水平和垂直的两条线，可以对其进行定制.此方法为设置线条颜色
        lineDataSet.setHighlightEnabled(false);//设置是否显示十字线
        lineDataSet.setColor(Color.parseColor("#18A0FB"));    //设置线条颜色
        lineDataSet.setCircleColor(Color.WHITE);  //设置节点的圆圈颜色
        lineDataSet.setLineWidth(1f);   //设置线条宽度
        lineDataSet.setCircleRadius(4f);//设置每个坐标点的圆大小
        lineDataSet.setDrawCircleHole(false);//是否定制节点圆心的颜色，若为false，则节点为单一的同色点，若为true则可以设置节点圆心的颜色
        lineDataSet.setValueTextSize(9f);   //设置 DataSets 数据对象包含的数据的值文本的大小（单位是dp）。
        //设置折线图填充
        lineDataSet.setDrawFilled(true);    //Fill填充，可以将折线图以下部分用颜色填满
        lineDataSet.setFillAlpha(65);       ////设置填充区域透明度，默认值为85
        lineDataSet.setFillColor(Color.parseColor("#00FFFFFF"));//设置填充颜色为透明
        lineDataSet.setFormLineWidth(1f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);    //添加数据
        lineChart.invalidate();
    }
}