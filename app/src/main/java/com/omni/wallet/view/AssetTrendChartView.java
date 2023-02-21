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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Map<String,Double> findMaxAndMin(List<AssetTrendEntity> result){
        Map<String,Double> maxAndMinMap = new HashMap<>();
        if (result.size() == 0 || result == null){
            maxAndMinMap.put("max",0.0);
            maxAndMinMap.put("min",0.0);
        }else{
            List<Double> values = new ArrayList<>();
            for (int i = 0; i < result.size(); i++) {
                double value =  Double.parseDouble(result.get(i).getAsset());
                values.add(value);
            }
            double min = Collections.min(values);
            double max = Collections.max(values);
            maxAndMinMap.put("max",max);
            maxAndMinMap.put("min",min);
        }
        return maxAndMinMap;
    }


    public void setViewShow(List<AssetTrendEntity> result) {

        if (result.size() == 0 || result == null) {
            setVisibility(GONE);
            return;
        }
        Map<String,Double> maxAndMin = findMaxAndMin(result);
        float max = Float.parseFloat(String.valueOf(maxAndMin.get("max")));
        float min = Float.parseFloat(String.valueOf(maxAndMin.get("min")));
        float minY = 0.0f;
        if (max - min > min){
            minY = (max-min)/2;
        }else if(max - min < min){
            minY = min/2;
        }else {
            minY = max/2;
        }
        float maxY = 0.0f;
        if (max - min > min){
            maxY = max + (max-min)/2;
        }else if(max - min < min){
            maxY = max + min/2;
        }else {
            maxY = max + max/2;
        }

        setVisibility(VISIBLE);
        //Configure basic information
        //配置基本信息
        lineChart.getDescription().setEnabled(false);   // setting description (设置描述)
        lineChart.setTouchEnabled(false);    //Set whether to touch（设置是否可以触摸）
        lineChart.setDragDecelerationFrictionCoef(0.9f);    //Set the speed of scrolling（设置滚动时的速度快慢）
        lineChart.setDragEnabled(false);     // Is it possible to drag and drop（是否可以拖拽）
        lineChart.setScaleXEnabled(false);   //Set whether the X axis can be zoomed in（设置X轴是否能够放大）
        lineChart.setScaleYEnabled(false);  //Set whether the Y axis can be zoomed in（设置Y轴是否能够放大）
        lineChart.setScaleEnabled(false);    // Whether the x and y axes can be scaled, the default is true （是否可以缩放 x和y轴, 默认是true）
        lineChart.setDrawGridBackground(false);//Set whether to display the grid background in the chart, the default is false（设置图表内格子背景是否显示，默认是false）
        lineChart.setHighlightPerDragEnabled(false);//Whether to drag the highlighted line (the prompt line of data points and coordinates), the default is true（能否拖拽高亮线(数据点与坐标的提示线)，默认是true）
        lineChart.setBackgroundColor(Color.WHITE);  //set background color（设置背景颜色）
        lineChart.animateX(2000);
        //配置X轴属性
        //Configure X-axis properties
        XAxis xAxis = lineChart.getXAxis();
        //xAxis.setLabelRotationAngle(25f);  //set rotation offset（设置旋转偏移量）
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);  //Set the position of the X axis（设置X轴的位置）
        //设置标签文本格式
        //Format label text
        xAxis.setTextSize(10f);
        //Set label text color
        //设置标签文本颜色
        xAxis.setTextColor(Color.parseColor("#999999"));
        //Whether to draw the axis
        //是否绘制轴线
        xAxis.setDrawAxisLine(true);
        //Whether to draw grid lines
        //是否绘制网格线
        xAxis.setDrawGridLines(false);
        //Set whether a grid displays a piece of data. If this property is not set, it will cause the X-axis data to be repeated and confused.
        //设置是否一个格子显示一条数据，如果不设置这个属性，就会导致X轴数据重复并且错乱的问题
        xAxis.setGranularity(1f);
        //Add labels to the X-axis
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
        //quote tag
        // 引用标签
        xAxis.setValueFormatter(formatter);
        //Configure Y-axis information
        //配置Y轴信息
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);   //Set the position of the Y axis（设置Y轴的位置）
        leftAxis.setDrawGridLines(true);    //Set the gridline display on the left（设置左边的网格线显示）
        leftAxis.setGranularityEnabled(false);//Enable when magnified limit y interval granularity characteristics.Default value: false(启用在放大时限制y轴间隔的粒度特性。默认值：false。)
        leftAxis.setTextColor(Color.parseColor("#999999")); //Set the Y axis text color（置Y轴文字颜色)
        leftAxis.setAxisMinimum(minY);
        leftAxis.setAxisMaximum(maxY);
        YAxis rightAxis = lineChart.getAxisRight(); //To obtain the right of the Y axis(获取右边的Y轴)
        rightAxis.setEnabled(false);   //Set the right Y axis is not displayed(设置右边的Y轴不显示)
        //设置图例（也就是曲线的标签）
        Legend legend = lineChart.getLegend();//Set scale map（设置比例图）
        legend.setEnabled(false);   //Because of the illustrations bring too ugly, and the operation is inconvenient, the building Lord choose custom, does not display scale map Settings（因为自带的图例太丑，而且操作也不方便，楼主选择自定义，设置不显示比例图）
        setManyDatas(result);
    }

    /**
     * Set up multiple line of data
     * 设置多条折线的数据
     */
    private void setManyDatas(List<AssetTrendEntity> result) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();   //collection of line data
        //Set the Y-axis data for a single discount
        ArrayList<Entry> yList = new ArrayList<Entry>();
        for (int j = 0; j < result.size(); j++) {
            float value = Float.parseFloat(result.get(j).getAsset());
            yList.add(new Entry(j, value));
        }
        LineDataSet lineDataSet = new LineDataSet(yList, "");   //set a single polyline
        //设置折线的属性
        //Set the properties of the polyline
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);   //Set the Y-axis node descriptions on the left and right sides(设置左右两边Y轴节点描述)
        lineDataSet.setValueTextColor(ColorTemplate.getHoloBlue()); //Set node text color（设置节点文字颜色)
        lineDataSet.setDrawCircles(false);  //Set whether to display the small dots of the nodes(设置是否显示节点的小圆点)
        lineDataSet.setDrawValues(false);       //Set whether to display the value of the node(设置是否显示节点的值)
        lineDataSet.setHighLightColor(Color.rgb(244, 117, 117));//When a node is clicked, two lines horizontal and vertical to the node will appear, which can be customized. This method is to set the color of the line(当点击节点时，将会出现与节点水平和垂直的两条线，可以对其进行定制.此方法为设置线条颜色)
        lineDataSet.setHighlightEnabled(false);//Set whether to display the crosshair (设置是否显示十字线)
        lineDataSet.setColor(Color.parseColor("#18A0FB"));    //set line color
        lineDataSet.setCircleColor(Color.WHITE);  //set the circle color of the node
        lineDataSet.setLineWidth(1f);   //set line width
        lineDataSet.setCircleRadius(4f);//Set the circle size for each coordinate point
        lineDataSet.setDrawCircleHole(false);//Whether to customize the color of the center of the node, if it is false, the node is a single point of the same color, if it is true, the color of the center of the node can be set
        lineDataSet.setValueTextSize(9f);   //Sets the size (in dp) of the value text of the data contained in the DataSets data object.
        //设置折线图填充
        //Set line chart fill
        lineDataSet.setDrawFilled(true);    //Fill, you can fill the lower part of the line chart with color
        lineDataSet.setFillAlpha(65);       ////Set the fill area transparency, the default value is 85
        lineDataSet.setFillColor(Color.parseColor("#00FFFFFF"));//Set fill color to transparent
        lineDataSet.setFormLineWidth(0.5f);
        lineDataSet.setFormSize(15.f);
        lineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSets.add(lineDataSet);
        LineData data = new LineData(dataSets);
        lineChart.setData(data);    //adding data
        lineChart.invalidate();
    }
}