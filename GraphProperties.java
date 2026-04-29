/*
 * @(#)GraphProperties.java Version 4.0 <Nov 9, 2014>
 *
 * Copyright 2015 Elegant MicroWeb Technologies Pvt. Ltd. (India). All Rights Reserved. Use is subject to license terms.
 */

package com.elegantjbi.vo.properties.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Transient;

import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.entity.smarten.SmartenInfo;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.util.ResourceManager;
import com.elegantjbi.util.logger.ApplicationLog;
import com.elegantjbi.vo.properties.BackgroundProperties;
import com.elegantjbi.vo.properties.BorderProperties;
import com.elegantjbi.vo.properties.BreadCrumProperties;
import com.elegantjbi.vo.properties.FontProperties;
import com.elegantjbi.vo.properties.Sunburst;
import com.elegantjbi.vo.properties.TitleProperties;
import com.elegantjbi.vo.properties.kpi.Actual;
import com.elegantjbi.vo.properties.kpi.BackGroundGrid;
import com.elegantjbi.vo.properties.kpi.DataValuePoint;
import com.elegantjbi.vo.properties.kpi.DialProperties;
import com.elegantjbi.vo.properties.kpi.GaugeTitleProperties;
import com.elegantjbi.vo.properties.kpi.GraphAreaProperties;
import com.elegantjbi.vo.properties.kpi.GraphChartCursor;
import com.elegantjbi.vo.properties.kpi.LegendPanelProperties;
import com.elegantjbi.vo.properties.kpi.NeedleProperties;
import com.elegantjbi.vo.properties.kpi.PanelProperties;
import com.elegantjbi.vo.properties.kpi.Scale;
import com.elegantjbi.vo.properties.kpi.ScaleProperties;
import com.elegantjbi.vo.properties.kpi.ShadowProperties;
import com.elegantjbi.vo.properties.kpi.Target;
import com.elegantjbi.vo.properties.kpi.TrendDataValueProperties;
import com.elegantjbi.vo.properties.kpi.TrendLegendProperties;
import com.elegantjbi.vo.properties.kpi.TrendLineProperties;
import com.elegantjbi.vo.properties.kpi.XaxisTrendProperties;
import com.elegantjbi.vo.properties.kpi.YaxisTrendProperties;
import com.elegantjbi.vo.properties.kpi.Zone;
import com.elegantjbi.vo.properties.map.MapAreaProperties;

/**
 * 
 * This Class is apply for Graph Properties of Graph.
 *
 */
public class GraphProperties implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * This applies for general properties in graph.
	 */
	private PanelProperties generalProperties;
	/**
	 * This applies for title properties in graph.
	 */
	private TitleProperties titleProperties;
	/**
	 * This applies for graph area properties in graph.
	 */
	private GraphAreaProperties graphAreaProperties;
	/**
	 * This applies for graph area properties in graph.
	 */
	private GraphAreaProperties graphHorizontalAreaProperties;
	
	/**
	 * This applies for x axis properties in graph.
	 */
	private XaxisTrendProperties xAxisProperties;
	/**
	 * This applies for y axis properties in graph.
	 */
	/*don't refer this object at any where. this object of yAxisProeprties is only refer for old object to read this property and put it into YAxisProeprtyMap*/
	private YaxisTrendProperties yAxisProperties;
	
	/*consider yAxis property from this map which has key as a measure name and value is an object of YaxisProeprty : 16/6/2016*/
	private Map<String, YaxisTrendProperties> yAxisPropertiesMap = new LinkedHashMap<String, YaxisTrendProperties>(); 
	/**
	 * This applies for legend properties in graph.
	 */
	private TrendLegendProperties legendProperties;
	
	private ShadowProperties legendPanelDhadowProperties;  
	/**
	 * This applies for Bar properties in graph.
	 * 
	 */
	private BarProperties barProperties;
	
	private BackGroundGrid backGroundGrid;
	
	private BorderProperties borderProperties;
	/**
	 * This applies for Data Value properties in graph.
	 */
	private TrendDataValueProperties dataValueProperties;
	
	private DataValuePoint dataValuePoint;

	/*
	 * This applies for Data Value properties in graph.
	 */
	private Map<String , TrendDataValueProperties> dataValuePropertiesMap = new LinkedHashMap<String, TrendDataValueProperties>();
	/**
	 * This applies for Reference Line properties in graph.
	 */
	private ReferenceLine referenceLine;
	/**
	 * This applies for Line properties in graph.
	 */
	private TrendLineProperties lineProperties;	
	/**
	 * This applies for TrendLineProperties in graph.
	 */
	private Map<Integer, TrendLineProperties> trendlinePropertiesMap;
	/**
	 * This applies for ReferenceLine in graph.
	 */
	private Map<Integer, ReferenceLine> referencelinePropertiesMap;
	/**
	 * This applies for Graph Line properties in graph.
	 */
	private GraphLineProperties graphLineProperties;
	/**
	 * This applies for Graph Area tab in graph.
	 */
	private GraphArea graphArea;
	/**
	 * This applies for Pie title in graph.
	 */
	private PieTitle pieTitle;
	/**
	 * This applies for Pie tab in Pie graph.
	 */
	private PieGraph pieGraph;
	
	private LegendPanelProperties legendPanelProperties;
	
	private FontProperties fontProperties;
	/**
	 * This applies for Doughnut Title graph.
	 */
	private DoughnutTitleProperties doughnutTitleProperties;
	/**
	 * This applies for Doughnut graph.
	 */
	private DoughNutGraph doughNutGraph;
	/**
	 * This applies for Radar graph.
	 */
	private Radar radar;
	/**
	 * This applies for Combined graph.
	 */
	private CombinedGraph combinedGraph;
	/**
	 * This applies for Combined graph.
	 */
	private CombinedYaxisProperties combinedYaxisProperties;
	/**
	 * This applies for Combined graph.
	 */
	private CombinedDataValueProperties combinedDataValueProperties;
	/**
	 * This applies for Reference Line properties in graph.
	 */
	private ReferenceLine barReferenceLine;
	/**
	 * This applies for Reference Line properties in graph.
	 */
	private ReferenceLine lineReferenceLine;
	/**
	 * This applies for ReferenceLine in graph.
	 */	
	private Map<Integer, ReferenceLine> barReferencelinePropertiesMap;
	/**
	 * This applies for ReferenceLine in graph.
	 */
	private Map<Integer, ReferenceLine> lineReferencelinePropertiesMap;
	/**
	 * This applies for TrendLineProperties in graph.
	 */
	private Map<Integer, TrendLineProperties> bartrendlinePropertiesMap;
	/**
	 * This applies for TrendLineProperties in graph.
	 */
	private Map<Integer, TrendLineProperties> linetrendlinePropertiesMap;
	/**
	 * This applies for Stock graph.
	 */	
	private CandleStick candleStick;
	/**
	 * This applies for HistoGram graph.
	 */	
	private Histogram histogram;
	/**
	 * This applies for Heatmap graph.
	 */	
	private Heatmap heatmap;
	/**
	 * This applies for AllLabelsProperties graph.
	 */	
	private AllLabelsProperties allLabelsProperties;
	/**
	 * This applies for DialGauge graph.
	 */
	private GaugeTitleProperties gaugeTitleProperties;
	/**
	 * This applies for DialGauge graph.
	 */
	private ScaleProperties gaugeScaleProperties;
	/**
	 * This applies for DialGauge graph.
	 */
	private NeedleProperties gaugeNeedleProperties;
	/**
	 * This applies for DialGauge graph.
	 */
	private DialProperties gaugeDialProperties;
	/**
	 * This applies for DialGauge graph.
	 */
	private Scale gaugeDataValueScale;
	/**
	 * This applies for DialGauge graph.
	 */
	private Actual gaugeDataValueActual;
	/**
	 * This applies for DialGauge graph.
	 */
	private Zone gaugeDataValueZone;
	/**
	 * This applies for DialGauge graph.
	 */
	private Target gaugeDataValueTarget;
	/**
	 * This applies for LevelGauge graph.
	 */
	private GaugeLevel gaugeLevel;
	/**
	 * This applies for ThermometerGauge graph.
	 */
	private ThermometerGauge thermometerGauge;
	/**
	 * This applies for BreadCrum in graph.
	 */
	private BreadCrumProperties breadCrumProperties;
	
	/**
	 * This applies for Sunburst in graph.
	 */
	private Sunburst sunburst;
	
	/**
	 * This applies for color type in barcolor auto,custom..
	 */
	private int colorType;
	
	/**
	 * This applies for add color picker daynamically when colortype is custom
	 */
	private List<String> customColors;
	/**
	 * This applies for showing auto color in smarten
	 */
	private List<String> autoColors;
	
	/**
	 * This applies for same color.
	 */
	private boolean sameColor;	
	
	/**
	 * This applies for same color value.
	 */
	private String color="#DCDCDC";
	
	/**
	 * This applies for total bar color.
	 */
	private String totalBarColor = "#000000";
	
	/**
	 * This applies for other bar color.
	 */
	private String otherBarColor = "#000000";
	
	/**
	 * This applies for tranceperancy of bar.
	 */
	private int tranceperancy;
	
	private Boolean editByCreator = false;
	
	private int applyDataoperationwhen = 2;
	
	@Transient
	private boolean clickFromSave = false;
	
	private Map<String, String> colLabelsMap;
	
	private Map graphsVisibleMap;
	/**
	 * This applies for area selection properties in graph.
	 */
	
	//AmCharts
	private String negativeBarColor = "#ff0000";
	
	private int zoomType = 1;
	
	private GraphChartCursor graphChartCursor;
	
	private FontProperties selectionFontProp;
	
	private SmartenProperties smartenProperties;
	
	/**
	 * This applies for GeoMap area properties in Smarten.
	 */
	private MapAreaProperties mapAreaProperties;

	private int smartenChartHeight;
	
	private SmartenColorProperties smartenColorProperties;
	
	/**
	 * This applies for range step value.
	 */
	//private int stepValue=5;
	//private int rangeDivValue = 5;
	private int rangeColorDivValue = 5;
	
	private int colorRange;
	
	private List rangeColorList;
	
	private String rangeStartColor = "#c3c3c3";
	
	private String rangeEndColor = "#000000";
	
	private int customColorType;
	
	private int mapColorType = 6;
	
	private String smartenMapShape = "circle";
	
	private int	smartenMapSize = 10;
	
	private int recommendedColorType = 0; 
	
	private boolean sampling = false;
	
	private boolean paginationChange = false;
		
	private boolean snapShotSampling = false;
	
	private boolean samplingSnapShotChanged = false;
	
	private boolean snapShotChanged = true;
	
	private boolean callCreateSmartenResultSet = true;
	
	private boolean samplingApplied = false;
	
	private boolean notEnoughRecord = false;
	
	//CB=checkBox
	private boolean samplingCB = true;
	private boolean snapShotSamplingCB = true;
	private boolean paginationCB = true;
	private boolean pagination = true;
	
	private Boolean autoAdjustedYAxisRange = true;
	
	private boolean changeShapeOfBulletDiamond = false;
	/*private boolean splitGraph;*/
	
	/**
	 * This applies for add color picker daynamically when line colortype is custom
	 */
	private List<String> lineCustomColors;
	
	/**
	 * This applies for add color picker daynamically when point colortype is custom
	 */
	private List<String> pointCustomColors;
	
	/**
	 * This applies for line type in line settigs auto,custom..
	 */
	private int lineType;
	
	/**
	 * This applies for line color type in line settigs auto,custom..
	 */
	private int lineColorType;
	
	/**
	 * This applies for same line color for value.
	 */
	private String linecolor="#DCDCDC";
	
	/**
	 * This applies for same point color for value.
	 */
	private String pointcolor="#ffffff";
	
	
	/**
	 * This applies for point type in line settigs auto,custom..
	 */
	private int pointType;
	
	/**
	 * This applies for point color type in line settigs auto,custom..
	 */
	private int pointColorType =2;
	
	private boolean adaptiveBehaviour=true;
	
	/**
	 * This applies for color of SmartenView Map
	 */
	private boolean fromSaveSmartenLabelProp;
	
	/**
	 * stack graph put total text on top of bar
	 */
	private String totalText;

	@Transient
	private int noOfXAxisBlocks = 5;
	
	@Transient
	private int noOfYAxisBlocks = 5;
	
	private int combinedRotateType = 0;
	
	private List legendCustomValueList =new ArrayList<>();
	
	private List customLegendSelectedValueList = new ArrayList<>();
	
	public List getCustomLegendSelectedValueList() {
		return customLegendSelectedValueList;
	}

	public void setCustomLegendSelectedValueList(List customLegendSelectedValueList) {
		this.customLegendSelectedValueList = customLegendSelectedValueList;
	}

	public List getLegendCustomValueList() {
		return legendCustomValueList;
	}

	public void setLegendCustomValueList(List legendCustomValueList) {
		this.legendCustomValueList = legendCustomValueList;
	}

	public boolean getAdaptiveBehaviour() {
		return adaptiveBehaviour;
	}

	public void setAdaptiveBehaviour(boolean adaptiveBehaviour) {
		this.adaptiveBehaviour = adaptiveBehaviour;
	}
	
	public GraphProperties()
	{
		generalProperties = new PanelProperties(0.0f);
		titleProperties = new TitleProperties(ResourceManager.getString("LBL_GRAPH_TITLE"), "center", "bold");
		graphAreaProperties = new GraphAreaProperties();
		/*this.tranceperancy =50;
		graphAreaProperties.setTranceperancy();*/
		graphHorizontalAreaProperties = new GraphAreaProperties("Horizontal");
		xAxisProperties = new XaxisTrendProperties(20);//X-axis label rotation
		yAxisProperties = new YaxisTrendProperties();
		legendProperties = new TrendLegendProperties(true);
		legendProperties.getLegendPanelProperties().setLegendPanelHeight(graphAreaProperties.getGeneralGraphArea().getHeight());
		legendProperties.getLegendPanelProperties().setLegendPanelWidth(15);
		legendProperties.getLegendPanelProperties().getLegendPanelBackgroundProperties().setVisible(false);
		legendProperties.getLegendPanelProperties().getLegendPanelShadowProperties().setFade(0);
		legendProperties.getTitleProperties().getTitlePadding().setBottomPadding(1.2f);
		barProperties = new BarProperties();
		dataValueProperties = new TrendDataValueProperties(false);
		referenceLine = new ReferenceLine();
		lineProperties = new TrendLineProperties();
		trendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		graphLineProperties = new GraphLineProperties();
		graphArea = new GraphArea();
		pieTitle = new PieTitle();
		pieGraph = new PieGraph();
		doughNutGraph = new DoughNutGraph();
		doughnutTitleProperties = new DoughnutTitleProperties();
		radar = new Radar();
		combinedGraph = new CombinedGraph();
		combinedYaxisProperties = new CombinedYaxisProperties();
		combinedDataValueProperties = new CombinedDataValueProperties(false);
		barReferenceLine = new ReferenceLine();
		lineReferenceLine = new ReferenceLine();
		barReferencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		lineReferencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		bartrendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		linetrendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		candleStick = new CandleStick();
		histogram = new Histogram();
		heatmap = new Heatmap();
		allLabelsProperties = new AllLabelsProperties();
		gaugeTitleProperties = new GaugeTitleProperties();
		gaugeScaleProperties = new ScaleProperties();
		gaugeNeedleProperties = new NeedleProperties();
		gaugeDialProperties = new DialProperties();
		gaugeDataValueScale = new Scale();
		gaugeDataValueActual = new Actual();
		gaugeDataValueZone = new Zone();
		gaugeDataValueTarget = new Target();
		gaugeLevel = new GaugeLevel();
		thermometerGauge = new ThermometerGauge();
		breadCrumProperties = new BreadCrumProperties();
		colLabelsMap=new HashMap<String, String>();
		graphsVisibleMap = new HashMap();
		graphChartCursor = new GraphChartCursor();
		selectionFontProp = new FontProperties();
		yAxisPropertiesMap = new LinkedHashMap<String, YaxisTrendProperties>(); 
		smartenProperties = new SmartenProperties();
		mapAreaProperties = new MapAreaProperties();
		smartenColorProperties = new SmartenColorProperties();
		dataValuePropertiesMap = new LinkedHashMap<String, TrendDataValueProperties>();
		rangeColorList = new ArrayList();
		
	}
	
	public GraphProperties(String title)
	{
		generalProperties = new PanelProperties(0.0f);
		titleProperties = new TitleProperties(title, "center", "bold");
		graphAreaProperties = new GraphAreaProperties();
		graphHorizontalAreaProperties = new GraphAreaProperties("Horizontal");
		xAxisProperties = new XaxisTrendProperties(20);//X-axis label rotation
		yAxisProperties = new YaxisTrendProperties();
		legendProperties = new TrendLegendProperties(true);
		legendProperties.getLegendPanelProperties().setLegendPanelHeight(graphAreaProperties.getGeneralGraphArea().getHeight());
		legendProperties.getLegendPanelProperties().setLegendPanelWidth(15);
		legendProperties.getLegendPanelProperties().getLegendPanelBackgroundProperties().setVisible(false);
		legendProperties.getLegendPanelProperties().getLegendPanelShadowProperties().setFade(0);
		legendProperties.getTitleProperties().getTitlePadding().setBottomPadding(1.2f);
		barProperties = new BarProperties();
		dataValueProperties = new TrendDataValueProperties(false);
		referenceLine = new ReferenceLine();
		lineProperties = new TrendLineProperties();
		trendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		referencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		graphLineProperties = new GraphLineProperties();
		graphArea = new GraphArea();
		pieTitle = new PieTitle();
		pieGraph = new PieGraph();
		doughNutGraph = new DoughNutGraph();
		doughnutTitleProperties = new DoughnutTitleProperties();
		radar = new Radar();
		combinedGraph = new CombinedGraph();
		combinedYaxisProperties = new CombinedYaxisProperties();
		combinedDataValueProperties = new CombinedDataValueProperties(false);
		barReferenceLine = new ReferenceLine();
		lineReferenceLine = new ReferenceLine();
		barReferencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		lineReferencelinePropertiesMap = new HashMap<Integer, ReferenceLine>();
		bartrendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		linetrendlinePropertiesMap = new HashMap<Integer, TrendLineProperties>();
		candleStick = new CandleStick();
		histogram = new Histogram();
		heatmap = new Heatmap();
		allLabelsProperties = new AllLabelsProperties();
		gaugeTitleProperties = new GaugeTitleProperties();
		gaugeScaleProperties = new ScaleProperties();
		gaugeNeedleProperties = new NeedleProperties();
		gaugeDialProperties = new DialProperties();
		gaugeDataValueScale = new Scale();
		gaugeDataValueActual = new Actual();
		gaugeDataValueZone = new Zone();
		gaugeDataValueTarget = new Target();
		gaugeLevel = new GaugeLevel();
		thermometerGauge = new ThermometerGauge();
		breadCrumProperties = new BreadCrumProperties();
		colLabelsMap=new HashMap<String, String>();
		graphsVisibleMap = new HashMap();
		graphChartCursor = new GraphChartCursor();
		selectionFontProp = new FontProperties();
		yAxisPropertiesMap = new LinkedHashMap<String, YaxisTrendProperties>(); 
		smartenProperties = new SmartenProperties();
		mapAreaProperties = new MapAreaProperties();
		smartenColorProperties = new SmartenColorProperties();
		dataValuePropertiesMap = new LinkedHashMap<String, TrendDataValueProperties>();
		rangeColorList = new ArrayList();
	}
	
	
	
	/**
	 * @return the generalProperties get general Properties.
	 */
	public PanelProperties getGeneralProperties() {
		return generalProperties;
	}
	/**
	 * @param generalProperties the generalProperties to set general Properties.
	 */
	public void setGeneralProperties(PanelProperties generalProperties) {
		this.generalProperties = generalProperties;
	}
	/**
	 * @return the titleProperties get title Properties.
	 */
	public TitleProperties getTitleProperties() {
		return titleProperties;
	}
	/**
	 * @param generalProperties the TitleProperties to set Title Properties.
	 */
	public void setTitleProperties(TitleProperties titleProperties) {
		this.titleProperties = titleProperties;
	}
	
	/**
	 * @return the graphAreaProperties get graphArea Properties.
	 */
	public GraphAreaProperties getGraphAreaProperties() {
		return graphAreaProperties;
	}
	/**
	 * @param graphAreaProperties the graphAreaProperties to set graphArea Properties.
	 */
	public void setGraphAreaProperties(GraphAreaProperties graphAreaProperties) {
		this.graphAreaProperties = graphAreaProperties;
	}
	/**
	 * @return the graphHorizontalAreaProperties
	 */
	public GraphAreaProperties getGraphHorizontalAreaProperties() {
		return graphHorizontalAreaProperties;
	}

	/**
	 * @param graphHorizontalAreaProperties the graphHorizontalAreaProperties to set
	 */
	public void setGraphHorizontalAreaProperties(GraphAreaProperties graphHorizontalAreaProperties) {
		this.graphHorizontalAreaProperties = graphHorizontalAreaProperties;
	}

	/**
	 * @return the xAxisProperties get xAxisProperties.
	 */
	public XaxisTrendProperties getxAxisProperties() {
		return xAxisProperties;
	}
	/**
	 * @param xAxisProperties the xAxisProperties to set xAxisProperties.
	 */
	public void setxAxisProperties(XaxisTrendProperties xAxisProperties) {
		this.xAxisProperties = xAxisProperties;
	}
	/**
	 * @return the yAxisProperties get yAxisProperties.
	 */
	public YaxisTrendProperties getyAxisProperties() {
		return yAxisProperties;
	}
	/**
	 * @param yAxisProperties the yAxisProperties to set yAxisProperties.
	 */
	public void setyAxisProperties(YaxisTrendProperties yAxisProperties) {
		this.yAxisProperties = yAxisProperties;
	}	
	/**
	 * @return the barProperties get barProperties.
	 */
	public BarProperties getBarProperties() {
		return barProperties;
	}
	/**
	 * @param barProperties the barProperties to set barProperties.
	 */
	public void setBarProperties(BarProperties barProperties) {
		this.barProperties = barProperties;
	}
	/**
	 * @return the dataValueProperties get dataValueProperties.
	 */
	public TrendDataValueProperties getDataValueProperties() {
		return dataValueProperties;
	}
	/**
	 * @param dataValueProperties the dataValueProperties to set dataValueProperties.
	 */
	public void setDataValueProperties(TrendDataValueProperties dataValueProperties) {
		this.dataValueProperties = dataValueProperties;
	}
	/**
	 * @return the referenceLine get referenceLine.
	 */
	public ReferenceLine getReferenceLine() {
		return referenceLine;
	}
	/**
	 * @param referenceLine the referenceLine to set referenceLine.
	 */
	public void setReferenceLine(ReferenceLine referenceLine) {
		this.referenceLine = referenceLine;
	}
	/**
	 * @return the lineProperties get lineProperties.
	 */
	public TrendLineProperties getLineProperties() {
		return lineProperties;
	}
	/**
	 * @param lineProperties the lineProperties to set lineProperties.
	 */
	public void setLineProperties(TrendLineProperties lineProperties) {
		this.lineProperties = lineProperties;
	}

	/**
	 * @return the legendProperties get legendProperties.
	 */
	public TrendLegendProperties getLegendProperties() {
		return legendProperties;
	}
	/**
	 * @param legendProperties the legendProperties to set legendProperties.
	 */
	public void setLegendProperties(TrendLegendProperties legendProperties) {
		this.legendProperties = legendProperties;
	}	
	
	/**
	 * @return the trendlinePropertiesMap get trendlinePropertiesMap.
	 */
	public Map<Integer, TrendLineProperties> getTrendlinePropertiesMap() {
		return trendlinePropertiesMap;
	}
	/**
	 * @param trendlinePropertiesMap the trendlinePropertiesMap.
	 */
	public void setTrendlinePropertiesMap(
			Map<Integer, TrendLineProperties> trendlinePropertiesMap) {
		this.trendlinePropertiesMap = trendlinePropertiesMap;
	}
	/**
	 * @return the referencelinePropertiesMap get referencelinePropertiesMap.
	 */
	public Map<Integer, ReferenceLine> getReferencelinePropertiesMap() {
		return referencelinePropertiesMap;
	}
	/**
	 * @param referencelinePropertiesMap the referencelinePropertiesMap.
	 */
	public void setReferencelinePropertiesMap(
			Map<Integer, ReferenceLine> referencelinePropertiesMap) {
		this.referencelinePropertiesMap = referencelinePropertiesMap;
	}
	/**
	 * @param graphLineProperties the graphLineProperties.
	 */
	public GraphLineProperties getGraphLineProperties() {
		return graphLineProperties;
	}
	/**
	 * @return the graphLineProperties get graphLineProperties.
	 */
	public void setGraphLineProperties(GraphLineProperties graphLineProperties) {
		this.graphLineProperties = graphLineProperties;
	}
	
	/**
	 * @return the graphArea get graphArea.
	 */
	public GraphArea getGraphArea() {
		return graphArea;
	}
	/**
	 * @param graphArea the graphArea.
	 */
	public void setGraphArea(GraphArea graphArea) {
		this.graphArea = graphArea;
	}
	/**
	 * @return the pieTitle get pieTitle.
	 */
	public PieTitle getPieTitle() {
		return pieTitle;
	}
	/**
	 * @param pieTitle the pieTitle.
	 */
	public void setPieTitle(PieTitle pieTitle) {
		this.pieTitle = pieTitle;
	}
	/**
	 * @return the pieGraph get pieGraph.
	 */
	public PieGraph getPieGraph() {
		return pieGraph;
	}
	/**
	 * @param pieGraph the pieGraph.
	 */
	public void setPieGraph(PieGraph pieGraph) {
		this.pieGraph = pieGraph;
	}		
	/**
	 * @return the doughnutTitleProperties get doughnutTitleProperties.
	 */
	public DoughnutTitleProperties getDoughnutTitleProperties() {
		return doughnutTitleProperties;
	}
	/**
	 * @param doughnutTitleProperties the doughnutTitleProperties.
	 */
	public void setDoughnutTitleProperties(
			DoughnutTitleProperties doughnutTitleProperties) {
		this.doughnutTitleProperties = doughnutTitleProperties;
	}
	/**
	 * @return the doughNutGraph get doughNutGraph.
	 */
	public DoughNutGraph getDoughNutGraph() {
		return doughNutGraph;
	}
	/**
	 * @param doughNutGraph the doughNutGraph.
	 */	
	public void setDoughNutGraph(DoughNutGraph doughNutGraph) {
		this.doughNutGraph = doughNutGraph;
	}
	/**
	 * @return the radar get radar.
	 */
	public Radar getRadar() {
		return radar;
	}
	/**
	 * @param radar the radar.
	 */
	public void setRadar(Radar radar) {
		this.radar = radar;
	}
	/**
	 * @return the combinedGraph get combinedGraph.
	 */
	public CombinedGraph getCombinedGraph() {
		return combinedGraph;
	}
	/**
	 * @param combinedGraph the combinedGraph.
	 */
	public void setCombinedGraph(CombinedGraph combinedGraph) {
		this.combinedGraph = combinedGraph;
	}
	/**
	 * @return the combinedYaxisProperties get combinedYaxisProperties.
	 */
	public CombinedYaxisProperties getCombinedYaxisProperties() {
		return combinedYaxisProperties;
	}
	/**
	 * @param combinedYaxisProperties the combinedYaxisProperties.
	 */
	public void setCombinedYaxisProperties(
			CombinedYaxisProperties combinedYaxisProperties) {
		this.combinedYaxisProperties = combinedYaxisProperties;
	}
	/**
	 * @return the combinedDataValueProperties get combinedDataValueProperties.
	 */
	public CombinedDataValueProperties getCombinedDataValueProperties() {
		return combinedDataValueProperties;
	}
	/**
	 * @param combinedDataValueProperties the combinedDataValueProperties.
	 */
	public void setCombinedDataValueProperties(
			CombinedDataValueProperties combinedDataValueProperties) {
		this.combinedDataValueProperties = combinedDataValueProperties;
	}
	/**
	 * @return the barReferenceLine get barReferenceLine.
	 */
	public ReferenceLine getBarReferenceLine() {
		return barReferenceLine;
	}
	/**
	 * @param barReferenceLine the barReferenceLine.
	 */
	public void setBarReferenceLine(ReferenceLine barReferenceLine) {
		this.barReferenceLine = barReferenceLine;
	}
	/**
	 * @return the lineReferenceLine get lineReferenceLine.
	 */
	public ReferenceLine getLineReferenceLine() {
		return lineReferenceLine;
	}
	/**
	 * @param lineReferenceLine the lineReferenceLine.
	 */
	public void setLineReferenceLine(ReferenceLine lineReferenceLine) {
		this.lineReferenceLine = lineReferenceLine;
	}
	/**
	 * @return the barReferencelinePropertiesMap get barReferencelinePropertiesMap.
	 */
	public Map<Integer, ReferenceLine> getBarReferencelinePropertiesMap() {
		return barReferencelinePropertiesMap;
	}
	/**
	 * @param barReferencelinePropertiesMap the barReferencelinePropertiesMap.
	 */
	public void setBarReferencelinePropertiesMap(
			Map<Integer, ReferenceLine> barReferencelinePropertiesMap) {
		this.barReferencelinePropertiesMap = barReferencelinePropertiesMap;
	}
	/**
	 * @return the barReferencelinePropertiesMap get barReferencelinePropertiesMap.
	 */
	public Map<Integer, ReferenceLine> getLineReferencelinePropertiesMap() {
		return lineReferencelinePropertiesMap;
	}
	/**
	 * @param lineReferencelinePropertiesMap the lineReferencelinePropertiesMap.
	 */
	public void setLineReferencelinePropertiesMap(
			Map<Integer, ReferenceLine> lineReferencelinePropertiesMap) {
		this.lineReferencelinePropertiesMap = lineReferencelinePropertiesMap;
	}	
	/**
	 * @return the bartrendlinePropertiesMap get bartrendlinePropertiesMap.
	 */
	public Map<Integer, TrendLineProperties> getBartrendlinePropertiesMap() {
		return bartrendlinePropertiesMap;
	}
	/**
	 * @param bartrendlinePropertiesMap the bartrendlinePropertiesMap.
	 */
	public void setBartrendlinePropertiesMap(
			Map<Integer, TrendLineProperties> bartrendlinePropertiesMap) {
		this.bartrendlinePropertiesMap = bartrendlinePropertiesMap;
	}
	/**
	 * @return the linetrendlinePropertiesMap get linetrendlinePropertiesMap.
	 */
	public Map<Integer, TrendLineProperties> getLinetrendlinePropertiesMap() {
		return linetrendlinePropertiesMap;
	}
	/**
	 * @param linetrendlinePropertiesMap the linetrendlinePropertiesMap.
	 */
	public void setLinetrendlinePropertiesMap(
			Map<Integer, TrendLineProperties> linetrendlinePropertiesMap) {
		this.linetrendlinePropertiesMap = linetrendlinePropertiesMap;
	}
	/**
	 * @return the candleStick get candleStick.
	 */
	public CandleStick getCandleStick() {
		return candleStick;
	}
	/**
	 * @param candleStick the candleStick.
	 */
	public void setCandleStick(CandleStick candleStick) {
		this.candleStick = candleStick;
	}	
	/**
	 * @return the histogram get histogram.
	 */
	public Histogram getHistogram() {
		return histogram;
	}
	/**
	 * @param histogram the histogram.
	 */
	public void setHistogram(Histogram histogram) {
		this.histogram = histogram;
	}	
	/**
	 * @return the heatmap
	 */
	public Heatmap getHeatmap() {
		return heatmap;
	}
	/**
	 * @param heatmap the heatmap to set
	 */
	public void setHeatmap(Heatmap heatmap) {
		this.heatmap = heatmap;
	}
	
	/**
	 * @return the allLabelsProperties get allLabelsProperties.
	 */
	public AllLabelsProperties getAllLabelsProperties() {
		return allLabelsProperties;
	}
	/**
	 * @param allLabelsProperties the allLabelsProperties.
	 */
	public void setAllLabelsProperties(AllLabelsProperties allLabelsProperties) {
		this.allLabelsProperties = allLabelsProperties;
	}

	/**
	 * @return the gaugeTitleProperties get gaugeTitleProperties.
	 */
	public GaugeTitleProperties getGaugeTitleProperties() {
		return gaugeTitleProperties;
	}
	/**
	 * @param gaugeTitleProperties the gaugeTitleProperties.
	 */
	public void setGaugeTitleProperties(GaugeTitleProperties gaugeTitleProperties) {
		this.gaugeTitleProperties = gaugeTitleProperties;
	}
	/**
	 * @return the gaugeScaleProperties get gaugeScaleProperties.
	 */
	public ScaleProperties getGaugeScaleProperties() {
		return gaugeScaleProperties;
	}
	/**
	 * @param gaugeScaleProperties the gaugeScaleProperties.
	 */
	public void setGaugeScaleProperties(ScaleProperties gaugeScaleProperties) {
		this.gaugeScaleProperties = gaugeScaleProperties;
	}
	/**
	 * @return the gaugeNeedleProperties get gaugeNeedleProperties.
	 */
	public NeedleProperties getGaugeNeedleProperties() {
		return gaugeNeedleProperties;
	}
	/**
	 * @param gaugeNeedleProperties the gaugeNeedleProperties.
	 */
	public void setGaugeNeedleProperties(NeedleProperties gaugeNeedleProperties) {
		this.gaugeNeedleProperties = gaugeNeedleProperties;
	}
	/**
	 * @return the gaugeDialProperties get gaugeDialProperties.
	 */
	public DialProperties getGaugeDialProperties() {
		return gaugeDialProperties;
	}
	/**
	 * @param gaugeDialProperties the gaugeDialProperties.
	 */
	public void setGaugeDialProperties(DialProperties gaugeDialProperties) {
		this.gaugeDialProperties = gaugeDialProperties;
	}
	/**
	 * @return the gaugeDataValueScale get gaugeDataValueScale.
	 */
	public Scale getGaugeDataValueScale() {
		return gaugeDataValueScale;
	}
	/**
	 * @param gaugeDataValueScale the gaugeDataValueScale.
	 */
	public void setGaugeDataValueScale(Scale gaugeDataValueScale) {
		this.gaugeDataValueScale = gaugeDataValueScale;
	}
	/**
	 * @return the gaugeDataValueActual get gaugeDataValueActual.
	 */
	public Actual getGaugeDataValueActual() {
		return gaugeDataValueActual;
	}
	/**
	 * @param gaugeDataValueActual the gaugeDataValueActual.
	 */
	public void setGaugeDataValueActual(Actual gaugeDataValueActual) {
		this.gaugeDataValueActual = gaugeDataValueActual;
	}
	/**
	 * @return the gaugeDataValueZone get gaugeDataValueZone.
	 */
	public Zone getGaugeDataValueZone() {
		return gaugeDataValueZone;
	}
	/**
	 * @param gaugeDataValueZone the gaugeDataValueZone.
	 */
	public void setGaugeDataValueZone(Zone gaugeDataValueZone) {
		this.gaugeDataValueZone = gaugeDataValueZone;
	}
	/**
	 * @return the gaugeDataValueTarget get gaugeDataValueTarget.
	 */
	public Target getGaugeDataValueTarget() {
		return gaugeDataValueTarget;
	}
	/**
	 * @param gaugeDataValueTarget the gaugeDataValueTarget.
	 */
	public void setGaugeDataValueTarget(Target gaugeDataValueTarget) {
		this.gaugeDataValueTarget = gaugeDataValueTarget;
	}

	/**
	 * @return the gaugeLevel get gaugeLevel.
	 */
	public GaugeLevel getGaugeLevel() {
		return gaugeLevel;
	}
	/**
	 * @param gaugeLevel the gaugeLevel.
	 */
	public void setGaugeLevel(GaugeLevel gaugeLevel) {
		this.gaugeLevel = gaugeLevel;
	}
	/**
	 * @return the thermometerGauge get thermometerGauge.
	 */
	public ThermometerGauge getThermometerGauge() {
		return thermometerGauge;
	}
	/**
	 * @param thermometerGauge the thermometerGauge.
	 */
	public void setThermometerGauge(ThermometerGauge thermometerGauge) {
		this.thermometerGauge = thermometerGauge;
	}	
	/**
	 * @return the breadCrumProperties
	 */
	public BreadCrumProperties getBreadCrumProperties() {
		return breadCrumProperties;
	}
	/**
	 * @param breadCrumProperties the breadCrumProperties to set
	 */
	public void setBreadCrumProperties(BreadCrumProperties breadCrumProperties) {
		this.breadCrumProperties = breadCrumProperties;
	}

	public int getCombinedRotateType() {
		return combinedRotateType;
	}

	public void setCombinedRotateType(int combinedRotateType) {
		this.combinedRotateType = combinedRotateType;
	}

	private StringBuilder getTitlePropertiesString(StringBuilder sbCss,String strClassName,String cssClassName) {
		
		sbCss.append("."+cssClassName+"object-area-title-main{width:100%; clear:both; position:relative; overflow:hidden;text-align: "+this.getTitleProperties().getTitleFont().getTextAlignment()+";}");
		if(this.getTitleProperties() != null) {
			sbCss.append(strClassName+"{"+this.getTitleProperties().toString()+"}");//padding-bottom:10px;				
		} else {
			sbCss.append(strClassName+"{font-size:14px; font-weight:bold;}");
		}
		
		return sbCss;
	}		
	private String[] generateDashArray(int needleBorderThickness,String needleStyle, double getTransparency)
    {
        String[] generatedArray = new String[3];
        String needleDasharrayString = "";
        switch(needleStyle)
        {
        case "none":
            needleBorderThickness = 0;
            break;
        case "dashed":
            needleDasharrayString = "5";
            break;
        case "dotted":
            needleDasharrayString = "2";
            break;
        case "3":
            needleDasharrayString = "5 2";
            break;
        case "4":
            needleDasharrayString = "5 2 2";
            break;
        }
        double alpha = (100 - getTransparency) / 100;

        generatedArray[0] = String.valueOf(needleBorderThickness);
        generatedArray[1] = needleDasharrayString;
        generatedArray[2] = String.valueOf(alpha);
        return generatedArray;
    } 

	public String generateSmartenCss(SmartenInfo graphInfo, boolean isXAxisVisible, boolean isYAxisVisible, String cssClassName)
	{
		boolean noOfMeasureInYaxis = false;
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
			|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			noOfMeasureInYaxis = true;
			for(int i=0;i<graphInfo.getGraphProperties().getyAxisPropertiesMap().size();i++)
			{
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
				{
					isYAxisVisible = true;
					break;
				}
			}
		}
		
		StringBuilder sbCss = new StringBuilder();
		GraphProperties graphProperties = graphInfo.getGraphProperties();
		List measureNameList= new ArrayList(graphInfo.getDataColLabels3());
		try {
			String s = cssClassName;
			String graphAreaBoxCss = cssClassName;
			String dashboardCss = cssClassName;
			String dashTdId = cssClassName;
			/*if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
			{*/	
			
			/*if(graphInfo.getGraphData().isSmartenRowsEnable())
			{
				sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getSmartenProperties().getSmartenTitleProperties().getFontProperties()+"}");
				sbCss.append("div[id^=rowsTitle] {"+ graphProperties.getSmartenProperties().getSmartenTitleProperties().getFontProperties()+"}");
			}
			
			if(graphInfo.getGraphData().isSmartenColoumnsEnable())
			{
				sbCss.append("."+cssClassName+"amcharts-title {"+ graphProperties.getSmartenProperties().getSmartenLabelProperties().getFontProperties()+"}");
				sbCss.append("div[id^=colsTitle] {"+ graphProperties.getSmartenProperties().getSmartenLabelProperties().getFontProperties()+"}");
			}*/
			
			
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==2 || this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==3))
			{
				/*if(measureNameList!=null && measureNameList.size() > 1)			*/
				if( (null != graphInfo.getGraphData().getRowLabel() && graphInfo.getGraphData().getRowList().size() > 15 && graphInfo.getGraphType() != GraphConstants.HEAT_MAP_GRAPH)
					|| (graphInfo.getGraphType() == GraphConstants.HEAT_MAP_GRAPH && graphInfo.getGraphData().getRangeList().size() > 15)
				 )//Added for Legend Scroll KP's point [30 Mar 2020]
				{
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 400px;overflow-y:auto !important;}");
				}
				else
				{
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 400px;}");
				}
/*				else 
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 400px;overflow-y:auto !important;}");
*/				
			}
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==1 || this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==4))
			{
				//if(measureNameList!=null && measureNameList.size() > 1)
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 100px;}");
				/*else
				sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 50px;overflow-y:auto !important;}");*/
			}
			
			//Added for Bug #15222 start
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible() && graphInfo.getGraphProperties().getTranceperancy() > 0
				 && (graphInfo.getGraphType() == GraphConstants.HISTOGRAM_GRAPH || graphInfo.getGraphType() == GraphConstants.HEAT_MAP_GRAPH))
			{
				double transperency = graphInfo.getGraphProperties().getTranceperancy();
				sbCss.append("."+"amcharts-legend-marker{opacity: "+ ((100-transperency)/100) +";}");//Added to apply opacity on legend marker for HeatMap chart (26 Sept 2019)
			}//Added for Bug #15222 end
			
				if(s.contains("dashboard-chart-dim"))
				{
					String parts[] = s.split("dashboard-chart-dim");
					graphAreaBoxCss = parts[0];
					dashTdId = parts[0];
				}
				if(cssClassName.length() > 0)
				{
					dashboardCss = graphAreaBoxCss + " .";
				}
				
			/*}*/
			//Title
			/*sbCss = graphProperties.getTitlePropertiesString(sbCss, "."+cssClassName+"section-graph-box-1 ."+cssClassName+"object-area-title",cssClassName);*/
			sbCss = graphProperties.getTitlePropertiesString(sbCss, "."+cssClassName+"object-area-title",cssClassName);
			
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{	
				if(graphProperties.getPieTitle().isVisible())
				{
					//.graph-3graph-gauge-area .graph-3amcharts-label
					String pieTitleCss = "";
					if(graphProperties.getPieGraph().isClustered())
					{
						pieTitleCss = "."+graphAreaBoxCss+"nested-graph-parent ."+graphAreaBoxCss;
					}
					else
					{
						pieTitleCss = "."+graphAreaBoxCss+"graph-gauge-area ."+graphAreaBoxCss;
					}
					sbCss.append(pieTitleCss+"amcharts-label {"+ graphProperties.getPieTitle().getFontProp().toString()+"}");
					sbCss.append(pieTitleCss+"amcharts-label {fill:"+ graphProperties.getPieTitle().getFontProp().getFontColor().toString()+"}");
				}
					
				//Data value
				sbCss.append("."+cssClassName+"amcharts-pie-label {"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" }");
				if(graphProperties.getPieGraph().getBorderProperties().isVisible())
				{
					int pieBorderThickness = graphProperties.getPieGraph().getBorderProperties().getAllBorderWidth();
					String pieBorderStyle = graphProperties.getPieGraph().getBorderProperties().getAllBorderStyle();

					String[] pieBorderArray = generateDashArray(pieBorderThickness,pieBorderStyle,0);
					String pieBorderColor = graphProperties.getPieGraph().getBorderProperties().getAllBorderColor();
					
					sbCss.append("."+cssClassName+"amcharts-pie-slice {stroke:"+ pieBorderColor+";stroke-width:"+pieBorderArray[0]+";stroke-opacity:1 !important;stroke-dasharray:"+pieBorderArray[1]+";}");
				}
				
			}
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				if(graphProperties.getPieGraph().isClustered())
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:absolute;}");
				}
				else
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:relative;}");
				}
				
				sbCss.append("."+graphAreaBoxCss+"nested-graph-parent{position:relative; }");
			}
			if(graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
			{	
				if(graphProperties.getDoughnutTitleProperties().isVisible())
				{
					String doughnutTitleCss = "";
					if(graphProperties.getDoughNutGraph().isClustered())
					{
						doughnutTitleCss = "."+graphAreaBoxCss+"nested-graph-parent ."+graphAreaBoxCss;
					}
					else
					{
						doughnutTitleCss = "."+graphAreaBoxCss+"graph-gauge-area ."+graphAreaBoxCss;
					}
					sbCss.append(doughnutTitleCss+"amcharts-label {"+ graphProperties.getDoughnutTitleProperties().getFontProp().toString()+"}");
					sbCss.append(doughnutTitleCss+"amcharts-label {fill:"+ graphProperties.getDoughnutTitleProperties().getFontProp().getFontColor().toString()+"}");
				}
					
				
				//Data value
				sbCss.append("."+cssClassName+"amcharts-pie-label {"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" }");
				
				if(graphProperties.getDoughNutGraph().getBorderProperties().isVisible())
				{
					int pieBorderThickness = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderWidth();
					String pieBorderStyle = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderStyle();

					String[] pieBorderArray = generateDashArray(pieBorderThickness,pieBorderStyle,0);
					String pieBorderColor = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderColor();
					
					sbCss.append("."+cssClassName+"amcharts-pie-slice {stroke:"+ pieBorderColor+";stroke-width:"+pieBorderArray[0]+";stroke-opacity:1 !important;stroke-dasharray:"+pieBorderArray[1]+";}");
				}
				if(graphProperties.getDoughNutGraph().isClustered())
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:absolute;}");
				}
				else
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:relative;}");
				}
				sbCss.append("."+graphAreaBoxCss+"nested-graph-parent{position:relative; }");
				
			}
			//gauge start
			if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
			{
				sbCss = graphProperties.generateDialGaugeCss(graphProperties,sbCss,cssClassName);
			}
			//gauge end
			
			//CSS for Tabular Legend start
			if(graphInfo.isSmartenTabular())//if(graphInfo.getSmartenType()==SmartenConstants.AUTO_GRAPH)// && getObjectMode() == AppConstants.NEW_MODE)
			{
				sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss, cssClassName, "tabularLegned");
			}//CSS for Tabular Legend end
			
			//Legend
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				sbCss = graphProperties.getCombinedGraph().getBarLegendProperties().generateLegendProperties(sbCss, cssClassName);
				//sbCss = graphProperties.getCombinedGraph().getLineLegendProperties().generateLegendProperties(sbCss, cssClassName+"combinedLine");
			} else {
				if (graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE) {
					sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss, cssClassName, "gaugeLegned");
				} else {
					/*if(cssClassName.contains("dashboard-chart-dim"))
					{
					    if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH
								|| graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
					    {
					    	graphAreaBoxCss += "parentlegend .";
					    }
					    else
					    {
					    	graphAreaBoxCss += " .";
					    }
					}*/
					if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
					{
						sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss,cssClassName,"smartenPieLegend");
						//code commented for 13972[pie legend DB]
						/*if(graphProperties.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() != 1 && graphProperties.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() != 4)//13975[1 and 4 are top and bottom respectively]
							sbCss.append("."+ cssClassName +"amcharts-legend-div { float : right; top:15% !important;}"); */
					}
					else
					{	
						sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss,cssClassName,graphInfo.getGraphType().toString());
					}
				}
			}
			
			
			int graphType = graphInfo.getGraphType();
			if (graphType == GraphConstants.PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.CONE_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_CONE_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_CONE_HBAR_GRAPH) {
				//X-Axis
				if(isXAxisVisible) {
					//sbCss.append(graphProperties.getyAxisProperties().generateYAxisCss(cssClassName));
					
					String strFontAlignAndRotateCss = "";
					String strFontAlignment = graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().getTextAlignment();
					int iRotateCharacter = graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getRotateCharacter();
					
					if (strFontAlignment.equalsIgnoreCase("left")) {
						strFontAlignAndRotateCss = "bottom:125px;";
						if (iRotateCharacter != 0) {
							strFontAlignment = "right";
						}
					} else if (strFontAlignment.equalsIgnoreCase("center")) {
						strFontAlignAndRotateCss = "top:50%;";
					} else if (strFontAlignment.equalsIgnoreCase("right")) {
						strFontAlignAndRotateCss = "top:125px;";
						if (iRotateCharacter != 0) {
							strFontAlignment = "left";
						}
					}
					if (iRotateCharacter == 0) {
						strFontAlignAndRotateCss += "-webkit-transform: rotate(270deg); -moz-transform: rotate(270deg); -o-transform: rotate(270deg);-ms-transform: rotate(-90deg); -sand-transform: rotate(-90deg);";
					} else {
						strFontAlignAndRotateCss += "-webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -o-transform: rotate(90deg);-ms-transform: rotate(-270deg); -sand-transform: rotate(-270deg);";
					}
					sbCss.append("."+cssClassName+"graph-title-y {" +strFontAlignAndRotateCss +" position:absolute; left:-110px;  width:250px; white-space:nowrap;}");
					//sbCss.append("."+cssClassName+"graph-title-y-main {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().toString()+"text-align:"+strFontAlignment+"; line-height:1;position:absolute; height:80%; width:30px; float:left;}");



					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main { overflow:hidden;}");
				}
			}
			else{
				//X-Axis
				sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				if(isXAxisVisible) {
					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main ."+cssClassName+"graph-title-x {");
					//sbCss.append(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().toString());
					//sbCss.append(" min-height: 30px;line-height: 30px; }");
					
					if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH && graphType != GraphConstants.PIE_GRAPH && graphType != GraphConstants.NUMERIC_DIAL_GAUGE)
					{
					
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1  ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
					if(graphProperties.getyAxisPropertiesMap().size() > 0)
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2  ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
					}

					//xaxis line start
					if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
					{
						String dasharrayString = null;
						int xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
						switch(graphProperties.getxAxisProperties().getLineProperties().getStyle())
						{
						case 0:
							xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
							break;
						case 1:
							dasharrayString = "5";
							break;
						case 2:
							dasharrayString = "2";
							break;
						case 3:
							dasharrayString = "5 2";
							break;
						case 4:
							dasharrayString = "5 2 2";
							break;
						}
						String strokeColor = graphProperties.getxAxisProperties().getLineProperties().getColor();
						sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+"}");
					}
					//xaxis line end

					sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
					if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isVisible()) {
						if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isWidthToText()) {
							sbCss.append("."+cssClassName+"section-graph-box-1 ."/*+cssClassName*/+"graph-title-x-main { text-align : "+graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getTextAlignment()+"; width:100%; float:left;}");
						}
					}
					/*sbCss.append(graphProperties.getxAxisProperties().toString());*/
					//xaxis tick Line start 
					String tickStrokeColor = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					int strokeLineThickness = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					//xaxis tick Line end
				}
			}
			//Bubble Properties
			if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
			{
				//xaxis line end
				if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getxAxisProperties().getLineProperties().getStyle())
					{
					case 0:
						xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getxAxisProperties().getLineProperties().getColor();
					//sbCss.append("."+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
					sbCss.append("."+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+"}");
				}
				//xaxis line end
				
				if(graphProperties.getxAxisProperties().getLineProperties().getAxisMinorLineTickTrendProperties().isVisible())
				{
					//xaxis tick Line start 
					String tickStrokeColor = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					int strokeLineThickness = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					//xaxis tick Line end
				}
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
				
				// yaxis 
				if(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness =graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getThickness();
					switch(graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getStyle())
					{
					case 0:
						yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getColor();
					sbCss.append("."+"value-axis-ValueAxis-2 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				if(graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMinorLineTickTrendProperties().isVisible())
				{
					//yaxis tick Line start 
					String tickStrokeColor = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					int strokeLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					//yaxis tick Line end
				}
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getLabelProperties().getFontProperties().toString()+"}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
					
				//Bullet
				if(graphProperties.getBarProperties().getBorderProperties().isVisible())
				{
					String dasharrayString = null;
					int borderThickness = graphProperties.getBarProperties().getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					switch(graphProperties.getBarProperties().getBorderProperties().getAllBorderStyle())
					{

					case "solid":
						borderStyle =0;
						break;
					case "dashed":
						borderStyle = 5;
						break;
					case "dotted":
						borderStyle = 2;
						break;
					default:
						borderThickness = 0;
						break;
					}
					String strokeColor = graphProperties.getBarProperties().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-bullet {stroke:"+strokeColor+";stroke-width:"+borderThickness+";stroke-opacity:1;stroke-dasharray:"+borderStyle+";} !important");
				}
			}
			//Y-axis properties start
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				
				//configuration bar border properties start
				if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible())
				{
					int borderWidth = graphProperties.barProperties.getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.barProperties.getBorderProperties().isVisible())
					{
						switch(graphProperties.barProperties.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "solid":
							borderStyle = -1;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.barProperties.getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}
				
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				
				
				//Title
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");

				if(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getStyle())
					{
					case 0:
						yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getColor();
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				if(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getStyle())
					{
					case 0:
						yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getColor();
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				//Y-axis-BAR tick line.
				String tickBarStrokeColor = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
				int strokeLineBarThickness = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-tick{stroke:"+tickBarStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineBarThickness+";}");
				
				//Y-axis-LINE tick line.
				String tickStrokeColor = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
				int strokeLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
				
				
				String legendMarginCss = "";
				if (graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
					sbCss.append(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().generateYAxisCss(cssClassName+"combineBarAxis-"));
					legendMarginCss = "margin-left:30px;";
				}
				if (graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
					sbCss.append(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().generateYAxisCss(cssClassName+"combineLineAxis-"));
					sbCss.append("."+cssClassName+"combineLineAxis-graph-title-y-main {right: 0;top: 0;}");
					legendMarginCss += "margin-right:30px;";
				}
				//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {"+legendMarginCss+" overflow:hidden;}");
			} else {
				
				if (graphType == GraphConstants.PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.CONE_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_CONE_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_CONE_HBAR_GRAPH) {

					if(isYAxisVisible) {
						sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main ."+cssClassName+"graph-title-x {");
						sbCss.append(graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().toString());
						sbCss.append(" min-height: 30px;line-height: 30px; }");
						if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isVisible()) {
							if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isWidthToText()) {
								sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main { text-align : "+graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().getTextAlignment()+"; width:100%; float:left;}");
							}
						}
						/*sbCss.append(graphProperties.getxAxisProperties().toString());*/
					}
					
				}
				else{
					if (isYAxisVisible && noOfMeasureInYaxis && graphType != GraphConstants.PIE_GRAPH && graphType != GraphConstants.NUMERIC_DIAL_GAUGE) {
						for(int i=0;i<measureNameList.size();i++)
						{
							if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
							{	
								sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getLabelProperties().getFontProperties().toString()+"}");
								sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
							}

							//Y-axis Line properties start
							if(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
							{
								String dasharrayString = null;
								int yaxisLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getThickness();
								switch(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getStyle())
								{
								case 0:
									yaxisLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getThickness();
									break;
								case 1:
									dasharrayString = "5";
									break;
								case 2:
									dasharrayString = "2";
									break;
								case 3:
									dasharrayString = "5 2";
									break;
								case 4:
									dasharrayString = "5 2 2";
									break;
								}
								String strokeColor = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getColor();
								//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");


								if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
								{
									if(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
									{
										sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+/*cssClassName+*/"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
									}
								}
								else
								{
									//Y-axis Line properties start
									sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
									//Y-axis Line properties end
								}
							}
							//Y-axis Line properties end

							//Y-axis tick line.

							//Y-axis tick line.
							if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
							{
								String tickStrokeColor = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
								int strokeLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
								//.value-axis-valueAxes"+i+" ."+cssClassName+"
								sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
							}
							sbCss.append(graphProperties.getyAxisPropertiesMap().get("M"+0).generateYAxisCss(cssClassName));
						}
					}
					//sbCss.append(graphProperties.getyAxisPropertiesMap().get("M"+0).generateYAxisCss(cssClassName));
					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {margin-left:30px; overflow:hidden;}");
					else if(!noOfMeasureInYaxis)
					{
						if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
						{	
							sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
							sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						}

						//Y-axis Line properties start
						if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
						{
							String dasharrayString = null;
							int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
							switch(graphProperties.getyAxisProperties().getLineProperties().getStyle())
							{
							case 0:
								yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
								break;
							case 1:
								dasharrayString = "5";
								break;
							case 2:
								dasharrayString = "2";
								break;
							case 3:
								dasharrayString = "5 2";
								break;
							case 4:
								dasharrayString = "5 2 2";
								break;
							}
							String strokeColor = graphProperties.getyAxisProperties().getLineProperties().getColor();
							//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

							if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
							{
								if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
								{
									sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
								}
							}
							else
							{
								//Y-axis Line properties start
								sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
								//Y-axis Line properties end
							}
						}
						//Y-axis Line properties end

						//Y-axis tick line.

						//Y-axis tick line.
						if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH )
						{
							String tickStrokeColor = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
							int strokeLineThickness = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
							sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
						}
						sbCss.append(graphProperties.getyAxisProperties().generateYAxisCss(cssClassName));
						//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {margin-left:30px; overflow:hidden;}");	
					}
				}
				
				
				//Radar Properties
				if(graphType == GraphConstants.DRILLED_RADAR_GRAPH ||
						graphType == GraphConstants.DRILLED_STACKED_RADAR_GRAPH)
				{
					//X-axis
					if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isVisible())
					{
						sbCss.append("."+cssClassName+"amcharts-axis-title {"+graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
						String fillColor = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getFontColor();
						sbCss.append("."+cssClassName+"amcharts-axis-title {fill:"+fillColor+"}");
						//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-title {text-decoration: none;}");
					}
					//sbCss.append("."+cssClassName+"amcharts-axis-title {"+graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
					
					//Y-axis 
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().isVisible())
					{	
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M0").getLabelProperties().getFontProperties().toString()+"}");
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-label {fill:"+ graphProperties.getyAxisPropertiesMap().get("M0").getLabelProperties().getFontProperties().getFontColor().toString()+"}");
					}
					if(graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().isVisible())
					{	
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M0").getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-title {fill:"+ graphProperties.getyAxisPropertiesMap().get("M0").getyAxisTitleTrendProperties().getFontProperties().getFontColor().toString()+"}");
					}
					if(graphInfo.getGraphProperties().getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
					{
						String tickStrokeColor = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
						int strokeLineThickness = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
						sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					}
					
					//x-axis title amcharts-label
					sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");

					//radar border properties start
					int radarBorderWidth = radar.getBorderProperties().getAllBorderWidth();
					int radarBorderStyle = 0;
					if(radar.getBorderProperties().isVisible())
					{
						switch(radar.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							radarBorderWidth = 0;
							break;
						case "dashed":
							radarBorderStyle = 5;
							break;
						case "dotted":
							radarBorderStyle = 2;
							break;
						}
						String radarStrokeColor = radar.getBorderProperties().getAllBorderColor();
						sbCss.append("."+cssClassName+"amcharts-graph-stroke{stroke:"+radarStrokeColor+";stroke-opacity:1;stroke-width:"+radarBorderWidth+"; stroke-dasharray:"+radarBorderStyle+"}");
					}
					
					//Radar axis start
					String radarAxisStyle = null;
					if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
					{
						
						switch (graphProperties.getxAxisProperties().getLineProperties().getStyle()) {
						case 1:
							radarAxisStyle = "5";
							break;
						case 2:
							radarAxisStyle = "2";
							break;
						}
					}
					sbCss.append("."+cssClassName+"amcharts-axis-line{stroke-dasharray:"+radarAxisStyle+"}");
					//Radar axis end
					
					//radar border properties end
				} 
				
				// bar border properties start
				if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible() && graphInfo.getGraphType() != com.elegantjbi.service.graph.GraphConstants.HEAT_MAP_GRAPH)
				{
					int borderWidth = graphProperties.barProperties.getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.barProperties.getBorderProperties().isVisible())
					{
						switch(graphProperties.barProperties.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.barProperties.getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}
				// bar border properties end
				
				//Histogram border properties start
				if(graphInfo.getGraphProperties().getHistogram().getBarProperties().getBorderProperties().isVisible() && graphInfo.getGraphType() != com.elegantjbi.service.graph.GraphConstants.HEAT_MAP_GRAPH)
				{
					int borderWidth = graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.getHistogram().getBarProperties().getBorderProperties().isVisible())
					{
						switch(graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}
				//Histogram border properties end
				
				//area border properties start
				int areaBorderWidth = graphProperties.getGraphArea().getBorderProperties().getAllBorderWidth();
				int areaBorderStyle = 0;
				if(graphProperties.getGraphArea().getBorderProperties().isVisible())
				{
					switch(graphProperties.getGraphArea().getBorderProperties().getAllBorderStyle())
					{
					case "none":
						areaBorderWidth =0;
						break;
					case "dashed":
						areaBorderStyle = 5;
						break;
					case "dotted":
						areaBorderStyle = 2;
						break;
					}

					String areaStrokeColor = graphProperties.getGraphArea().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-fill{stroke:"+areaStrokeColor+";stroke-opacity:1;stroke-width:"+areaBorderWidth+"; stroke-dasharray:"+areaBorderStyle+"}");
				}
				//area border properties end
			}
			
			sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-axis-main{float:none; display:inline-block; text-align:left;}");
			//dashboard
			if(!cssClassName.isEmpty())
			{	
			sbCss.append("."+cssClassName+"item show { display:block}");
			sbCss.append("."+cssClassName+"item { display:none}");
			sbCss.append("."+cssClassName+"left span { background-position:right top;}");
			sbCss.append("."+cssClassName+"right span { background-position:left top;}");
			sbCss.append("."+cssClassName+"left{top:50%;height:15%}");
			sbCss.append("."+cssClassName+"right{top:50%;left:90%;height:15%}");
			}
			//generating media query css for Iphone size devices.
			sbCss.append("@media (max-width:768px){ ."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-axis-main{display:block;}");
			sbCss.append("}");
			
			//setting Default UI when Rows/Cols/Split graph enabled start
			boolean legendMeasure = graphInfo.getGraphData().isRowMeasure() || graphInfo.getGraphData().isSizeMeasure() || graphInfo.getGraphData().isShapeMeasure();
			if(graphInfo.getGraphData().isSmartenRowsEnable() || graphInfo.getGraphData().isSmartenColoumnsEnable()
					|| (graphInfo.getGraphType() == GraphConstants.PIE_GRAPH && graphInfo.getMeasureTitleList().size() > 1))//for bug 13976
			{
				//For Graph Area border
					graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderColor("#DCDCDC");
					graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setVisible(true);
					graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderWidth(1);
					graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderStyle("solid");
			}
			else
			{
				//For Graph Area border
				graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderColor("#000000");
				graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setVisible(false);
				graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderWidth(0);
				graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().setAllBorderStyle("none");
			}//setting Default UI when Rows/Cols/Split graph enabled end
			
			//Graph-Area
			if (graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE) {
				sbCss.append("."+cssClassName+"gauge-img-box{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().toString()+" }");
			} else {
				//sbCss.append("."+cssClassName+"graph-img-box{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().toString()+" }");
				if(graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PIE_GRAPH	) {
					String tmpCss = graphAreaBoxCss+"nested-graph-child";
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().isVisible())
					{
						sbCss.append("."+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");
					}
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().isVisible())
					{
						sbCss.append("."+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
					}
					sbCss.append("."+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
					sbCss.append("."+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");
					/*if(graphProperties.getDoughNutGraph().isClustered())
					{
						sbCss.append("."+dashboardCss+"nested-graph-child{position:absolute;}");
					}
					else
					{
						sbCss.append("."+dashboardCss+"nested-graph-child{position:relative;}");
					}
					sbCss.append("."+dashboardCss+"nested-graph-parent{position:relative; }");*/
					sbCss.append("."+tmpCss+"{position:relative; }");
				}
				else
				{
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().isVisible())
					{
						sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");	
					}
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");
				}
			}

			//General Properties
			sbCss.append("."+graphAreaBoxCss+"section-graph-box-1 ."+graphAreaBoxCss+"object-area-main{"+ graphProperties.getGeneralProperties().toString()+" width:100%; clear:both; position:relative; overflow:hidden;}");
			sbCss.append("."+graphAreaBoxCss+"section-graph-box-1 ."+graphAreaBoxCss+"object-area-box{width:100%;  margin:0; text-align:center;}");
			
			//Data value row object-container
			if(graphInfo.getGraphType() == GraphConstants.D3_BUBBLE ||graphInfo.getGraphType() == GraphConstants.D3_CHORD ||graphInfo.getGraphType() == GraphConstants.D3_TREELAYOUT ||graphInfo.getGraphType() == GraphConstants.D3_TREEMAP ||graphInfo.getGraphType() == GraphConstants.D3_SUNBURST) {
				sbCss.append("."+graphAreaBoxCss+"row ."+graphAreaBoxCss+"object-container{"+ graphProperties.getGeneralProperties().toString()+" width:100%; clear:both; position:relative; overflow:hidden;}");
				sbCss.append("."+graphAreaBoxCss+"row ."+graphAreaBoxCss+"object-container{width:100%;  margin:0; text-align:center;}");
			}
		
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			sbCss.append("."+cssClassName+"amcharts-graph-column .amcharts-graph-label {"+ graphProperties.getCombinedDataValueProperties().getBardataValuePoint().getFontProperties().toString()+" }");
			String lineFontCss = graphProperties.getCombinedDataValueProperties().getLinedataValuePoint().getFontProperties().toString();
			sbCss.append(".")
		     .append(cssClassName)
		     .append("amcharts-graph-line .amcharts-graph-label, .")
		     .append(cssClassName)
		     .append("amcharts-graph-smoothedLine .amcharts-graph-label, .")
		     .append(cssClassName)
		     .append("amcharts-graph-step .amcharts-graph-label {")
		     .append(lineFontCss)
		     .append("}");}else{
				sbCss.append("."+cssClassName+"amcharts-graph-label {"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" fill:"+graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor()+"; width:20px;white-space: -moz-pre-wrap;}");
			}
			
			//Data Value Mouse Over
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{" + getCombinedDataValueProperties().getBardataValueMouseOver().toString() + "}");
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{font-family:"+ getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
				if(graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().isVisible())
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
				else
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:0;}");
				//sbCss.append("#line"+ cssClassName +" {padding:2px; position : absolute; z-index: 3000;" + getCombinedDataValueProperties().getLinedataValueMouseOver().toString() + "}");
				if(graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().isVisible())
				{
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
					
					String balloonBorderDashArray = null;
					
					switch (graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
					case "none":
						balloonBorderDashArray ="0";
						break;
					case "dashed":
						balloonBorderDashArray = "5";
						break;
					case "dotted":
						balloonBorderDashArray = "2";
						break;
					}
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
				}
			}
			else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				int totalRows = graphInfo.getGraphData().getTotalRows();
				int totalCols = graphInfo.getGraphData().getTotalCols();
				int totalCats = 1;
				if(graphInfo.getGraphData().getRowList().size() > 0)
					totalCats = graphInfo.getGraphData().getColList().size();
				if(totalRows == 0)
					totalRows = 1;
				if(totalCols == 0)
					totalCols = 1;
				
				int colListSize  = totalRows * totalCols * totalCats;
				
				/*if(graphInfo.getGraphData().isSmartenRowsEnable() && graphInfo.getGraphData().isSmartenColoumnsEnable())
				{
					if(graphInfo.getGraphData().getRowList().size()>0)
						colListSize = graphInfo.getGraphData().getRowsList().size() * graphInfo.getGraphData().getColsList().size() * graphInfo.getGraphData().getColList().size();
					else	
						colListSize = graphInfo.getGraphData().getTotalRows() * graphInfo.getGraphData().getTotalCols();
				}
				else if(graphInfo.getGraphData().isSmartenRowsEnable())
					colListSize = graphInfo.getGraphData().getRowsList().size();
				else if(graphInfo.getGraphData().isSmartenColoumnsEnable())
				{
					if(graphInfo.getGraphData().getRowList().size()>0)
						colListSize =  graphInfo.getGraphData().getColList().size() * graphInfo.getGraphData().getTotalCols();
					else
						colListSize = graphInfo.getGraphData().getColsList().size();
				}
					
				else 
					colListSize = graphInfo.getGraphData().getColList().size();
				*/
				List<Integer> multipleMeasureDataValueIndexArr = new ArrayList<Integer>();
				for (int itr = 0; itr < colListSize; itr++) {
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().size() > 1)
					{
						multipleMeasureDataValueIndexArr.add(itr);
					}
					else
					{	
						multipleMeasureDataValueIndexArr.add(0);
					}	
				}
				
				
				for (int itr = 0; itr < colListSize; itr++) {
					int index = multipleMeasureDataValueIndexArr.get(itr)%colListSize;
					String chartId = "";
					//Data value start
					if(!cssClassName.isEmpty())
					{
						chartId ="#"+graphAreaBoxCss+"_"+itr; /* SDEVAPR20-3294 change . to #*/
					}
					else
					{
						chartId = "#chart"+itr;
					}
					sbCss.append(chartId+" .amcharts-pie-label"+"{"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValuePoint().getFontProperties().toString()+" }");
					//Data value end
					
					//Mouse over value start
					sbCss.append("."+cssClassName+"amcharts-balloon-div div .amcharts-balloon-textM"+itr+"{"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-balloon-div div .amcharts-balloon-textM"+itr+"{font-family:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
					
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
					{	
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{fill:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
					}
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().isVisible()
							&& !graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle().equalsIgnoreCase("none"))
					{
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
						
						int balloonBorderDashArray = 0;
						switch (graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
						case "dashed":
							balloonBorderDashArray = 5;
							break;
						case "dotted":
							balloonBorderDashArray = 2;
							break;
						}
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
					}
					//Mouse over value end
				}
			
			}
			else{
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{"+ graphProperties.getDataValueProperties().getDataValueMouseOver().toString()+" }");
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{font-family:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
				if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
				{	
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
				}
				if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().isVisible())
				{
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
					
					String balloonBorderDashArray = null;
					switch (graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
					case "none":
						balloonBorderDashArray ="0";
						break;
					case "dashed":
						balloonBorderDashArray = "5";
						break;
					case "dotted":
						balloonBorderDashArray = "2";
						break;
					}
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
				}
				//sbCss.append("#tooltip {padding:2px; position : absolute; z-index: 3000;" + getDataValueProperties().getDataValueMouseOver().toString() + "}");	
				//sbCss.append("#tooltip"+cssClassName+" {padding:2px; position : absolute; z-index: 3000;" + getDataValueProperties().getDataValueMouseOver().toString() + "}");	
			}
			
			//for BredCrum start
			FontProperties breadCrumFont = graphProperties.getBreadCrumProperties().getBreadCrumFont();
			BackgroundProperties bredCrumBackground = graphProperties.getBreadCrumProperties().getBreadCrumBackGround();
			String strBreadcrumBorder = "";
			if(cssClassName.isEmpty()) {
				strBreadcrumBorder = "border-bottom: 1px";
			sbCss.append("."+cssClassName+"breadcrumb-section-graph{"+strBreadcrumBorder+" solid #ebebeb; border-radius:0; padding:0 0 0 10px;"/*+"text-decoration:"+breadCrumFont.getTextDecoration()+";"*/);
			}
			if(breadCrumFont != null)
				sbCss.append("color:"+breadCrumFont.getFontColor()+";");						
			if(!cssClassName.isEmpty()){
				sbCss.append("float:left;width:100%;");	
			}
			if(bredCrumBackground!= null && !bredCrumBackground.isBackgroundTransparent()) {
				if(bredCrumBackground.isVisible()){
					sbCss.append("background:"+bredCrumBackground.getBackGroundColor()+";");
				}
			}
			sbCss.append("}");
			
			if(!cssClassName.isEmpty()) {
				if(selectionFontProp != null) {
				sbCss.append(" ."+dashTdId+"-selection{"+selectionFontProp.toString()+" fill:"+selectionFontProp.getFontColor()+";} ");
				}
			}
			
			if(breadCrumFont != null)
				sbCss.append(".breadcrumb-section-graph .breadcrumb-left-section > li.active a{color:"+breadCrumFont.getFontColor()+";");
			sbCss.append("}");		
			//for BredCrum end	
			
			
			
			if(graphInfo.getGraphType() != GraphConstants.NUMERIC_DIAL_GAUGE && graphInfo.getGraphType() != GraphConstants.PIE_GRAPH)
			{	
				if(graphInfo.getGraphData().isSmartenRowsEnable())
				{
					sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getSmartenProperties().getSmartenTitleProperties().getFontProperties()+"}");
					for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
					{
						sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getLabelProperties().getFontProperties().toString()+"}");
					}
					sbCss.append("div[id^=rowsTitle] {"+ graphProperties.getSmartenProperties().getSmartenTitleProperties().getFontProperties()+"}");
				}
				if(graphInfo.getGraphData().isSmartenColoumnsEnable())
				{
					sbCss.append("."+cssClassName+"amcharts-title {"+ graphProperties.getSmartenProperties().getSmartenLabelProperties().getFontProperties()+"}");
					for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
					{
						if(graphInfo.getDataColLabels3().size()==graphProperties.getyAxisPropertiesMap().size())
							sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						else
							sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M0").getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
					}
					sbCss.append("div[id^=colsTitle] {"+ graphProperties.getSmartenProperties().getSmartenLabelProperties().getFontProperties()+"}");
				}
			}
			if(graphInfo.isSmartenQuickSetting() && graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getSmartenProperties().getSmartenLabelProperties().getFontProperties()+"}");
			}
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH && graphInfo.getDataColLabels3().size() > 1)//Added for Pie Height (When more than 1M pie were cutting from bottom)
				sbCss.append(".amcharts-chart-div{height: 100% !important;}");
		} catch(Exception ex) {
			ApplicationLog.error(ex);
		}
		return sbCss.toString();
	}
	
	/**
	 * Below function will generate CSS for SmartenView Map
	 * @param graphInfo
	 * @param cssClassName
	 * @return
	 */
	public String generateSmartenMapCss(SmartenInfo graphInfo, String cssClassName) {
		StringBuilder sbCss = new StringBuilder();
		GraphProperties graphProperties = graphInfo.getGraphProperties();

		sbCss = graphProperties.getTitlePropertiesString(sbCss, "."+cssClassName+"object-area-title",cssClassName);
		//General Properties
		sbCss.append("."+cssClassName+"section-graph-box-1 .generalMapDiv{"+ graphProperties.getGeneralProperties().toString()+" width:100%; clear:both; position:relative; overflow:hidden;}");
		sbCss.append("."+cssClassName+"section-graph-box-1 .object-area-box{width:100%;  margin:0; text-align:center;}");
		sbCss.append("."+cssClassName+"section-graph-box-1 .generalMapDiv{"+ graphProperties.getGeneralProperties().getPanelPadding().toString()+"}");

		// Title Properties	
		if(graphProperties.getMapAreaProperties() != null && graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getBackGround().isVisible())
		{
			sbCss.append("."+cssClassName+"generalMapDiv{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getBackGround().toString()+" }");
		}
		sbCss.append("."+cssClassName+"ammap-chart-div{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getPanelBorder().toString()+" }");
		sbCss.append("."+cssClassName+"ammap-chart-div{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getPanelShadow().toString()+" }");
		sbCss.append("."+cssClassName+"ammap-chart-div{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getPanelRounded().toString()+" }");
		sbCss.append("."+cssClassName+"ammap-chart-div{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().toString()+" }");
		// Data Value Point
		sbCss.append("."+cssClassName+"ammap-map-image-label {"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" }");

		sbCss.append("."+cssClassName+"mapDivDimension{"+ graphProperties.getMapAreaProperties().getGeneralMapArea().getGeneralProperties().getPanelMargin().toString()+" }");

		//for BredCrum start
		FontProperties breadCrumFont = graphProperties.getBreadCrumProperties().getBreadCrumFont();
		BackgroundProperties bredCrumBackground = graphProperties.getBreadCrumProperties().getBreadCrumBackGround();
		String strBreadcrumBorder = "";
//		if(cssClassName.isEmpty()) {
//			strBreadcrumBorder = "border-bottom: 1px";
//		sbCss.append("."+cssClassName+"breadcrumb-section-graph{"+strBreadcrumBorder+" solid #ebebeb; border-radius:0; padding:0 0 0 10px;"/*+"text-decoration:"+breadCrumFont.getTextDecoration()+";"*/);
//		}
		sbCss.append("color:"+breadCrumFont.getFontColor()+";");						
		if(!cssClassName.isEmpty()){
			sbCss.append("float:left;width:100%;");	
		}
		if(!bredCrumBackground.isBackgroundTransparent()) {
			if(bredCrumBackground.isVisible()){
				sbCss.append("background:"+bredCrumBackground.getBackGroundColor()+";");
			}
		}
		sbCss.append("}");
		if(breadCrumFont != null)
			sbCss.append("."+cssClassName+"breadcrumb-section-graph .breadcrumb-left-section > li.active a{color:"+breadCrumFont.getFontColor()+";");
		
		sbCss.append("}");
		if(!cssClassName.isEmpty())
			sbCss.append(" ."+cssClassName+"-selection{"+graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" fill:"+graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor()+";}");

		sbCss.append("."+cssClassName+"ammap-balloon-div div{"+ graphProperties.getDataValueProperties().getDataValueMouseOver().toString()+"  }");

		sbCss.append("."+cssClassName+"ammap-balloon-div div{font-family:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
		if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
			sbCss.append("."+cssClassName+"ammap-balloon-bg{fill:"+ graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");

		int borderThickness = graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth();
		String borderStyle = graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle();
		String borderColor = graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor();
		String[] borderArray = generateDashArray(borderThickness,borderStyle,0);
		sbCss.append("."+cssClassName+"ammap-balloon-bg{stroke:"+borderColor+" ;}");
		sbCss.append("."+cssClassName+"ammap-balloon-bg{stroke-width:"+ borderArray[0]+" ;}");
		sbCss.append("."+cssClassName+"ammap-balloon-bg {stroke-opacity:1;stroke-dasharray:"+borderArray[1]+";}");
		// for tabel box 
		sbCss.append("."+cssClassName+"ammap-tabel-div #info {"+ graphProperties.getDataValueProperties().getDataValueMouseOver().toString()+" }");

		sbCss.append("."+cssClassName+"ammap-tabel-div #info > #tableDID{text-decoration:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getTextDecoration().toString() +" !important }");

		sbCss.append("."+cssClassName+"ammap-tabel-div #info{font-family:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");

		if(!graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable()) {
			sbCss.append("."+cssClassName+"ammap-tabel-div #info{display:none !important;}");
		}

		if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
			sbCss.append("."+cssClassName+"ammap-tabel-text-div{background:"+ graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");

		if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().isVisible())
		{
			sbCss.append("."+cssClassName+"ammap-tabel-text-div{"+graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder()+";}");
		} 
		sbCss.append("."+cssClassName+"ammap-tabel-text-div{"+graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverShadow()+";}");

		return sbCss.toString();
	}

	public String generateSmartenBreadcrumCss(SmartenInfo graphInfo, boolean isXAxisVisible, boolean isYAxisVisible, String cssClassName) {
		GraphProperties graphProperties = graphInfo.getGraphProperties();
		StringBuilder sbCss = new StringBuilder();
		String dashTdId = cssClassName;
		//for BredCrum start
		FontProperties breadCrumFont = graphProperties.getBreadCrumProperties().getBreadCrumFont();
		BackgroundProperties bredCrumBackground = graphProperties.getBreadCrumProperties().getBreadCrumBackGround();
		String strBreadcrumBorder = "";
//		if(cssClassName.isEmpty()) {
//			strBreadcrumBorder = "border-bottom: 1px";
//		sbCss.append("."+cssClassName+"breadcrumb-section-graph{"+strBreadcrumBorder+" solid #ebebeb; border-radius:0; padding:0 0 0 10px;"/*+"text-decoration:"+breadCrumFont.getTextDecoration()+";"*/);
//		}
		if(breadCrumFont != null)
			sbCss.append("color:"+breadCrumFont.getFontColor()+";");						
		if(!cssClassName.isEmpty()){
			sbCss.append("float:left;width:100%;");	
		}
		if(bredCrumBackground!= null && !bredCrumBackground.isBackgroundTransparent()) {
			if(bredCrumBackground.isVisible()){
				sbCss.append("background:"+bredCrumBackground.getBackGroundColor()+";");
			}
		}
		sbCss.append("}");
		
		if(!cssClassName.isEmpty()) {
			if(selectionFontProp != null) {
			sbCss.append(" ."+dashTdId+"-selection{"+selectionFontProp.toString()+" fill:"+selectionFontProp.getFontColor()+";} ");
			}
		}
		
		if(breadCrumFont != null)
			sbCss.append(".breadcrumb-section-graph .breadcrumb-left-section > li.active a{color:"+breadCrumFont.getFontColor()+";");
		sbCss.append("}");	
		return sbCss.toString();
		//for BredCrum end	
	}
	
	
	public String generateCss(GraphInfo graphInfo, boolean isXAxisVisible, boolean isYAxisVisible, String cssClassName)
	{		
		
		
		boolean noOfMeasureInYaxis = false;
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
			|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			noOfMeasureInYaxis = true;
			for(int i=0;i<graphInfo.getGraphProperties().getyAxisPropertiesMap().size();i++)
			{
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
				{
					isYAxisVisible = true;
					break;
				}
			}
		}
		
		StringBuilder sbCss = new StringBuilder();
		GraphProperties graphProperties = graphInfo.getGraphProperties();
		List measureNameList= new ArrayList(graphInfo.getDataColLabels3());
		try {
			
			
			
			
			
			String s = cssClassName;
			String graphAreaBoxCss = cssClassName;
			String dashboardCss = cssClassName;
			String dashTdId = cssClassName;
			/*if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
			{*/	
			if(s.contains("dashboard-chart-dim"))
			{
				String parts[] = s.split("dashboard-chart-dim");
				graphAreaBoxCss = parts[0];
				dashTdId = parts[0];
			}
			if(cssClassName.length() > 0)
			{
				dashboardCss = graphAreaBoxCss + " .";
			}
			
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==2 || this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==3))
			{
				sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 400px;overflow-y:auto !important;}");
				sbCss.append("."+graphAreaBoxCss+"d3-legend-div{max-height: 350px;overflow-y:auto !important;}");
			}	
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==1 || this.getLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==4))
			{
				if(graphInfo.getGraphType() != GraphConstants.PIE_GRAPH)
				{	
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 100px;overflow-y:auto !important;}");
					sbCss.append("."+graphAreaBoxCss+"d3-legend-div{max-height: 50px;overflow-y:auto !important;}");
				}
				else
				{
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 475px;overflow-y:auto !important;}");
					sbCss.append("."+graphAreaBoxCss+"d3-legend-div{max-height: 475px;overflow-y:auto !important;}");
				}
			}
			
			//Added for Bug #15222 start
			if(this.getLegendProperties().getLegendPanelProperties().isLegendPanelVisible() && graphInfo.getGraphProperties().getTranceperancy() > 0
				 && (graphInfo.getGraphType() == GraphConstants.HISTOGRAM_GRAPH || graphInfo.getGraphType() == GraphConstants.HEAT_MAP_GRAPH))
			{
				double transperency = graphInfo.getGraphProperties().getTranceperancy();
				sbCss.append("."+"amcharts-legend-marker{opacity: "+ ((100-transperency)/100) +";}");//Added to apply opacity on legend marker for HeatMap chart (26 Sept 2019)
			}//Added for Bug #15222 end
			
			if(graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH)
			{
				if(this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==2 || this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==3))
				{
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 400px;overflow-y:auto !important;}");
				}
				if(this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().isLegendPanelVisible()  && (this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().getLegendPanelPosition() ==1 || this.getCombinedGraph().getBarLegendProperties().getLegendPanelProperties().getLegendPanelPosition()==4))
				{
					sbCss.append("."+cssClassName+"amcharts-legend-div{max-height: 475px;overflow-y:auto !important;}");
				}
			}
			
			
			
				
			/*}*/
			//Title
			/*sbCss = graphProperties.getTitlePropertiesString(sbCss, "."+cssClassName+"section-graph-box-1 ."+cssClassName+"object-area-title",cssClassName);*/
			sbCss = graphProperties.getTitlePropertiesString(sbCss, "."+cssClassName+"object-area-title",cssClassName);
			
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{	
				if(graphProperties.getPieTitle().isVisible())
				{
					//.graph-3graph-gauge-area .graph-3amcharts-label
					String pieTitleCss = "";
					if(graphProperties.getPieGraph().isClustered())
					{
						pieTitleCss = "."+graphAreaBoxCss+"nested-graph-parent ."+graphAreaBoxCss;
					}
					else
					{
						pieTitleCss = "."+graphAreaBoxCss+"graph-gauge-area ."+graphAreaBoxCss;
					}
					sbCss.append(pieTitleCss+"amcharts-label {"+ graphProperties.getPieTitle().getFontProp().toString()+"}");
					sbCss.append(pieTitleCss+"amcharts-label {fill:"+ graphProperties.getPieTitle().getFontProp().getFontColor().toString()+"}");
				}
					
			
				if(graphProperties.getPieGraph().getBorderProperties().isVisible())
				{
					int pieBorderThickness = graphProperties.getPieGraph().getBorderProperties().getAllBorderWidth();
					String pieBorderStyle = graphProperties.getPieGraph().getBorderProperties().getAllBorderStyle();

					String[] pieBorderArray = generateDashArray(pieBorderThickness,pieBorderStyle,0);
					String pieBorderColor = graphProperties.getPieGraph().getBorderProperties().getAllBorderColor();
					
					sbCss.append("."+cssClassName+"amcharts-pie-slice {stroke:"+ pieBorderColor+";stroke-width:"+pieBorderArray[0]+";stroke-opacity:1 !important;stroke-dasharray:"+pieBorderArray[1]+";}");
				}
				
			}
			if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				if(graphProperties.getPieGraph().isClustered())
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:absolute;}");
				}
				else
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:relative;}");
				}
				
				sbCss.append("."+graphAreaBoxCss+"nested-graph-parent{position:relative; }");
			}
			if(graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
			{	
				if(graphProperties.getDoughnutTitleProperties().isVisible())
				{
					String doughnutTitleCss = "";
					if(graphProperties.getDoughNutGraph().isClustered())
					{
						doughnutTitleCss = "."+graphAreaBoxCss+"nested-graph-parent ."+graphAreaBoxCss;
					}
					else
					{
						doughnutTitleCss = "."+graphAreaBoxCss+"graph-gauge-area ."+graphAreaBoxCss;
					}
					sbCss.append(doughnutTitleCss+"amcharts-label {"+ graphProperties.getDoughnutTitleProperties().getFontProp().toString()+"}");
					sbCss.append(doughnutTitleCss+"amcharts-label {fill:"+ graphProperties.getDoughnutTitleProperties().getFontProp().getFontColor().toString()+"}");
				}
					
				
				//Data value
				for (int i = 0; i < graphProperties.getDataValuePropertiesMap().size(); i++) {
					sbCss.append("."+cssClassName+"amcharts-pie-label {"+ graphProperties.getDataValuePropertiesMap().get("M"+i).getDataValuePoint().getFontProperties().toString()+" }");	
				}
				
				if(graphProperties.getDoughNutGraph().getBorderProperties().isVisible())
				{
					int pieBorderThickness = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderWidth();
					String pieBorderStyle = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderStyle();

					String[] pieBorderArray = generateDashArray(pieBorderThickness,pieBorderStyle,0);
					String pieBorderColor = graphProperties.getDoughNutGraph().getBorderProperties().getAllBorderColor();
					
					sbCss.append("."+cssClassName+"amcharts-pie-slice {stroke:"+ pieBorderColor+";stroke-width:"+pieBorderArray[0]+";stroke-opacity:1 !important;stroke-dasharray:"+pieBorderArray[1]+";}");
				}
				if(graphProperties.getDoughNutGraph().isClustered())
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:absolute;}");
				}
				else
				{
					sbCss.append("."+graphAreaBoxCss+"nested-graph-child{position:relative;}");
				}
				sbCss.append("."+graphAreaBoxCss+"nested-graph-parent{position:relative; }");
				
			}
			//gauge start
			if(graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE)
			{
				sbCss = graphProperties.generateDialGaugeCss(graphProperties,sbCss,cssClassName);
			}
			//gauge end
			
			//Legend
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				sbCss = graphProperties.getCombinedGraph().getBarLegendProperties().generateLegendProperties(sbCss, cssClassName);
				//sbCss = graphProperties.getCombinedGraph().getLineLegendProperties().generateLegendProperties(sbCss, cssClassName+"combinedLine");
			} else {
				if (graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE) {
					sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss, cssClassName, "gaugeLegned");
				} else {
					/*if(cssClassName.contains("dashboard-chart-dim"))
					{
					    if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH
								|| graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH)
					    {
					    	graphAreaBoxCss += "parentlegend .";
					    }
					    else
					    {
					    	graphAreaBoxCss += " .";
					    }
					}*/
					if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
					{
						sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss,cssClassName);
					}
					else
					{	
						sbCss = graphProperties.getLegendProperties().generateLegendProperties(sbCss,cssClassName,graphInfo.getGraphType().toString());
					}
				}
			}
			
			
			int graphType = graphInfo.getGraphType();
			if (graphType == GraphConstants.PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_PYRAMID_HBAR_GRAPH ||
					graphType == GraphConstants.CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_CYLINDER_HBAR_GRAPH ||
					graphType == GraphConstants.CONE_HBAR_GRAPH ||
					graphType == GraphConstants.STACKED_CONE_HBAR_GRAPH ||
					graphType == GraphConstants.PERCENTAGE_CONE_HBAR_GRAPH) {
				//X-Axis
				if(isXAxisVisible) {
					//sbCss.append(graphProperties.getyAxisProperties().generateYAxisCss(cssClassName));
					
					String strFontAlignAndRotateCss = "";
					String strFontAlignment = graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().getTextAlignment();
					int iRotateCharacter = graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getRotateCharacter();
					
					if (strFontAlignment.equalsIgnoreCase("left")) {
						strFontAlignAndRotateCss = "bottom:125px;";
						if (iRotateCharacter != 0) {
							strFontAlignment = "right";
						}
					} else if (strFontAlignment.equalsIgnoreCase("center")) {
						strFontAlignAndRotateCss = "top:50%;";
					} else if (strFontAlignment.equalsIgnoreCase("right")) {
						strFontAlignAndRotateCss = "top:125px;";
						if (iRotateCharacter != 0) {
							strFontAlignment = "left";
						}
					}
					if (iRotateCharacter == 0) {
						strFontAlignAndRotateCss += "-webkit-transform: rotate(270deg); -moz-transform: rotate(270deg); -o-transform: rotate(270deg);-ms-transform: rotate(-90deg); -sand-transform: rotate(-90deg);";
					} else {
						strFontAlignAndRotateCss += "-webkit-transform: rotate(90deg); -moz-transform: rotate(90deg); -o-transform: rotate(90deg);-ms-transform: rotate(-270deg); -sand-transform: rotate(-270deg);";
					}
					sbCss.append("."+cssClassName+"graph-title-y {" +strFontAlignAndRotateCss +" position:absolute; left:-110px;  width:250px; white-space:nowrap;}");
					//sbCss.append("."+cssClassName+"graph-title-y-main {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().toString()+"text-align:"+strFontAlignment+"; line-height:1;position:absolute; height:80%; width:30px; float:left;}");



					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main { overflow:hidden;}");
				}
			}
			else{
				//X-Axis
				/*if(isXAxisVisible) {*/
					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main ."+cssClassName+"graph-title-x {");
					//sbCss.append(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().toString());
					//sbCss.append(" min-height: 30px;line-height: 30px; }");
					sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
					if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
					{
						if(isXAxisVisible) {
						sbCss.append("."+cssClassName+"value-axis-ValueAxis-1  ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
						sbCss.append("."+cssClassName+"value-axis-ValueAxis-2  ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						}
					}

					//xaxis line start
					if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
					{
						String dasharrayString = null;
						int xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
						switch(graphProperties.getxAxisProperties().getLineProperties().getStyle())
						{
						case 0:
							xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
							break;
						case 1:
							dasharrayString = "5";
							break;
						case 2:
							dasharrayString = "2";
							break;
						case 3:
							dasharrayString = "5 2";
							break;
						case 4:
							dasharrayString = "5 2 2";
							break;
						}
						String strokeColor = graphProperties.getxAxisProperties().getLineProperties().getColor();
						sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+"}");
					}
					//xaxis line end
					if(isXAxisVisible) 
						sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
					if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isVisible()) {
						if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isWidthToText()) {
							sbCss.append("."+cssClassName+"section-graph-box-1 ."/*+cssClassName*/+"graph-title-x-main { text-align : "+graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getTextAlignment()+"; width:100%; float:left;}");
						}
					}
					/*sbCss.append(graphProperties.getxAxisProperties().toString());*/
					//xaxis tick Line start 
					String tickStrokeColor = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					int strokeLineThickness = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					//xaxis tick Line end
				}
			//}
			//Bubble Properties
			if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
			{
				//xaxis line end
				if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getxAxisProperties().getLineProperties().getStyle())
					{
					case 0:
						xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getxAxisProperties().getLineProperties().getColor();
					//sbCss.append("."+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
					sbCss.append("."+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+"}");
					sbCss.append(".d3-xaxis"+graphAreaBoxCss+" path{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+";stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
				}
				else
				{	
					sbCss.append(".d3-xaxis"+graphAreaBoxCss+" path{stroke-opacity:0;}");
				}
				//xaxis line end
				int strokeLineThickness = 0;
				String tickStrokeColor = "";
				int xStrokeOpacity = 0;
				if(graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
				{
					//xaxis tick Line start 
					tickStrokeColor = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					strokeLineThickness = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					xStrokeOpacity = 1;
					//xaxis tick Line end
				}
				
				sbCss.append(".d3-xaxis"+graphAreaBoxCss+" g.tick line{stroke:"+tickStrokeColor+";stroke-opacity:"+xStrokeOpacity+";stroke-width:"+strokeLineThickness+";}");
				
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				sbCss.append(".d3-xaxis"+graphAreaBoxCss+" text {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+";}");
				sbCss.append(".d3-xaxis"+graphAreaBoxCss+" text {fill:"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().getFontColor().toString()+";}");
				if(isXAxisVisible)
				{
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
					sbCss.append(".d3-xaxis-title"+graphAreaBoxCss+" {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+";}");
					sbCss.append(".d3-xaxis-title"+graphAreaBoxCss+" {fill:"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getFontColor().toString()+";}");
				}
				// yaxis 
				if(graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness =graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getThickness();
					switch(graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getStyle())
					{
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getColor();
					sbCss.append("."+"value-axis-ValueAxis-2 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
					sbCss.append(".d3-yaxis"+graphAreaBoxCss+" path{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+";stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				else
				{	
					sbCss.append(".d3-yaxis"+graphAreaBoxCss+" path{stroke-opacity:0;}");
				}
				int strokeOpacity = 0;
				if(graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
				{
					//yaxis tick Line start 
					tickStrokeColor = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
					strokeLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+0).getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					strokeOpacity = 1;
					//yaxis tick Line end
				}
				
				sbCss.append(".d3-yaxis"+graphAreaBoxCss+" g.tick line{stroke:"+tickStrokeColor+";stroke-opacity:"+strokeOpacity+";stroke-width:"+strokeLineThickness+";}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getLabelProperties().getFontProperties().toString()+"}");
				sbCss.append(".d3-yaxis"+graphAreaBoxCss+" text{"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getLabelProperties().getFontProperties().toString()+";}");
				sbCss.append(".d3-yaxis"+graphAreaBoxCss+" text{fill:"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getLabelProperties().getFontProperties().getFontColor().toString()+";}");
				if (isYAxisVisible)
				{	
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
					sbCss.append(".d3-yaxis-title"+graphAreaBoxCss+" {"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
					sbCss.append(".d3-yaxis-title"+graphAreaBoxCss+" {fill:"+ graphProperties.getyAxisPropertiesMap().get("M"+0).getyAxisTitleTrendProperties().getFontProperties().getFontColor().toString()+";}");
				}
					
				//Bullet
				if(graphProperties.getBarProperties().getBorderProperties().isVisible())
				{
					int borderThickness = graphProperties.getBarProperties().getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					switch(graphProperties.getBarProperties().getBorderProperties().getAllBorderStyle())
					{

					case "solid":
						borderStyle =0;
						break;
					case "dashed":
						borderStyle = 5;
						break;
					case "dotted":
						borderStyle = 2;
						break;
					default:
						borderThickness = 0;
						break;
					}
					String strokeColor = graphProperties.getBarProperties().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-bullet {stroke:"+strokeColor+";stroke-width:"+borderThickness+";stroke-opacity:1;stroke-dasharray:"+borderStyle+";} !important;");
					//sbCss.append("."+cssClassName+"dot {stroke:"+strokeColor+";stroke-width:"+borderThickness+";stroke-opacity:1;stroke-dasharray:"+borderStyle+";} !important;");
				}
				
				
				if(graphProperties.getGraphAreaProperties().getBackGroundGrid().isGridLineVisible())
				{
					int borderThickness = graphProperties.getGraphAreaProperties().getBackGroundGrid().getGridLineThickness();
					int borderStyle = 0;
					switch(graphProperties.getGraphAreaProperties().getBackGroundGrid().getStyle())
					{
					case 1:
						borderStyle = 5;
						break;
					case 2:
						borderStyle = 2;
						break;
					}
					String strokeColor = graphProperties.getGraphAreaProperties().getBackGroundGrid().getGridLineColor();
					sbCss.append(".gridline"+graphAreaBoxCss+" g.tick line{stroke:"+strokeColor+";stroke-width:"+borderThickness+";stroke-opacity:1;stroke-dasharray:"+borderStyle+";} !important;");
					sbCss.append(".gridline"+graphAreaBoxCss+" path.domain{stroke-opacity:0 !important;}");
				}
			}
			
			//if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH) {
				
				
				
					//borderProperties = new BorderProperties(true, 1, "solid", "#ffffff", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000");
					
				   // borderProperties = new BorderProperties();
				    
				    /*System.out.println("======================>>>>>>>>>>>>"+borderProperties.isVisible());
				    System.out.println("======================>>>>>>>>>>>>"+graphProperties.getBarProperties().getBorderProperties().isVisible());
					//borderProperties.setVisible(false);
					borderProperties.setAllBorderWidth(1);
					borderProperties.setAllBorderStyle("solid");
					borderProperties.setAllBorderColor("#ffffff");
				
					barProperties.setBorderProperties(borderProperties);*/
				  //borderProperties = new BorderProperties(true,  1, "solid", "#ffffff");
				    //barProperties.setBorderProperties(borderProperties);
				    
				
				
			//}else if(graphType == GraphConstants.DRILLED_RADAR_GRAPH || graphType == GraphConstants.DRILLED_STACKED_RADAR_GRAPH) {
				
				//borderProperties = new BorderProperties(true, 1, "solid", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000");
				
				
				//setTranceperancy(50);
				//radar.setBorderProperties(borderProperties);
			//}/*else {
				
				//setTranceperancy(0);
				
			//	borderProperties = new BorderProperties(false, 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000");
				
				
				//barProperties.setBorderProperties(borderProperties);
				
				//borderProperties = new BorderProperties();
			//}*/
			
			/*if(graphType == GraphConstants.PIE_GRAPH){
				
				legendPanelProperties = new LegendPanelProperties();
				legendPanelProperties.setLegendPanelVisible(true);
				
				legendPanelProperties.setLegendPanelPosition(GraphConstants.LEGEND_RIGHT);
				
							
				borderProperties = new BorderProperties(true, 2, "solid", "#ffffff", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000");
				
				
				pieGraph.setBorderProperties(borderProperties);
				
							
				legendProperties.setLegendPanelProperties(legendPanelProperties);
				
				dataValuePoint = new DataValuePoint();
				
				dataValuePoint.setDataValuePointVisible(true);
				dataValuePoint.setPosition("OutSide");
				dataValuePoint.setDataPointLineVisible(true);
				dataValuePoint.setDataValuePointFormatText("$PERCENT_VALUE%$");
				
				fontProperties = new FontProperties();
				
				fontProperties.setFontName("Verdana");
				dataValuePoint.setFontProperties(fontProperties);
				
				dataValueProperties.setDataValuePoint(dataValuePoint);
				
				
				
			}else {
				legendPanelProperties = new LegendPanelProperties();
				legendPanelProperties.setLegendPanelVisible(true);
				
				legendPanelProperties.setLegendPanelPosition(GraphConstants.LEGEND_BOTTOM);
				borderProperties = new BorderProperties(false, 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000", 0, "none", "#000000");
				
				
				pieGraph.setBorderProperties(borderProperties);
				
							
				legendProperties.setLegendPanelProperties(legendPanelProperties);
				
				dataValuePoint = new DataValuePoint();
				
				dataValuePoint.setDataValuePointVisible(false);
				dataValuePoint.setPosition("Inside");
				dataValuePoint.setDataPointLineVisible(false);
				
				fontProperties = new FontProperties();
				
				fontProperties.setFontName("Arial");
				dataValuePoint.setFontProperties(fontProperties);
				
				dataValueProperties.setDataValuePoint(dataValuePoint);
			}
			
			if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH 
					||graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH 
					||graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH
					||graphInfo.getGraphType() == GraphConstants.DRILLED_RADAR_GRAPH 
					||graphInfo.getGraphType() == GraphConstants.DRILLED_STACKED_RADAR_GRAPH) {
				setTranceperancy(50);
				
			}else {
				setTranceperancy(0);
				
			}
			
			if(graphType == GraphConstants.COMBINED_GRAPH) {
				
				backGroundGrid = new BackGroundGrid();
				backGroundGrid.setGridLineVisible(false);
				
				graphAreaProperties.setBackGroundGrid(backGroundGrid);
				
				
			}else {
				backGroundGrid = new BackGroundGrid();
				backGroundGrid.setGridLineVisible(true);
				
				graphAreaProperties.setBackGroundGrid(backGroundGrid);
				
			}
			*/
			
			
			
			
			
			//Y-axis properties start
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				
				//configuration bar border properties start
				if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible())
				{
					int borderWidth = graphProperties.barProperties.getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.barProperties.getBorderProperties().isVisible())
					{
						switch(graphProperties.barProperties.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "solid":
							borderStyle = -1;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.barProperties.getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}
				
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				
				
				//Title
				if (isYAxisVisible)
				{
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
				
				
				}

				if(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getStyle())
					{
					case 0:
						yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getColor();
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				if(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getStyle())
					{
					case 0:
						yaxisLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getColor();
					sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

				}
				//Y-axis-BAR tick line.
				String tickBarStrokeColor = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
				int strokeLineBarThickness = graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-1 ."+"amcharts-axis-tick{stroke:"+tickBarStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineBarThickness+";}");
				
				//Y-axis-LINE tick line.
				String tickStrokeColor = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
				int strokeLineThickness = graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
				sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
				
				
				String legendMarginCss = "";
				if (graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
					sbCss.append(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().generateYAxisCss(cssClassName+"combineBarAxis-"));
					legendMarginCss = "margin-left:30px;";
				}
				if (graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
					sbCss.append(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().generateYAxisCss(cssClassName+"combineLineAxis-"));
					sbCss.append("."+cssClassName+"combineLineAxis-graph-title-y-main {right: 0;top: 0;}");
					legendMarginCss += "margin-right:30px;";
				}
				//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {"+legendMarginCss+" overflow:hidden;}");
			} else {
				
				if (graphType == GraphConstants.PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_PYRAMID_HBAR_GRAPH ||
						graphType == GraphConstants.CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_CYLINDER_HBAR_GRAPH ||
						graphType == GraphConstants.CONE_HBAR_GRAPH ||
						graphType == GraphConstants.STACKED_CONE_HBAR_GRAPH ||
						graphType == GraphConstants.PERCENTAGE_CONE_HBAR_GRAPH) {

					if(isYAxisVisible) {
						sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main ."+cssClassName+"graph-title-x {");
						sbCss.append(graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().toString());
						sbCss.append(" min-height: 30px;line-height: 30px; }");
						if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isVisible()) {
							if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isWidthToText()) {
								sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main { text-align : "+graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().getTextAlignment()+"; width:100%; float:left;}");
							}
						}
						/*sbCss.append(graphProperties.getxAxisProperties().toString());*/
					}
					
				}
				else{
					if (noOfMeasureInYaxis) {
						for(int i=0;i<measureNameList.size();i++)
						{
							if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
							{	
								sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getLabelProperties().getFontProperties().toString()+"}");
								if (isYAxisVisible)
									sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
							}

							//Y-axis Line properties start
							if(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
							{
								String dasharrayString = null;
								int yaxisLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getThickness();
								switch(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getStyle())
								{
								case 0:
									yaxisLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getThickness();
									break;
								case 1:
									dasharrayString = "5";
									break;
								case 2:
									dasharrayString = "2";
									break;
								case 3:
									dasharrayString = "5 2";
									break;
								case 4:
									dasharrayString = "5 2 2";
									break;
								}
								String strokeColor = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getColor();
								//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");


								if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
								{
									if(graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
									{
										sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."+/*cssClassName+*/"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
									}
								}
								else
								{
									//Y-axis Line properties start
									sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
									//Y-axis Line properties end
								}
							}
							//Y-axis Line properties end

							//Y-axis tick line.

							//Y-axis tick line.
							if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
							{
								String tickStrokeColor = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
								int strokeLineThickness = graphProperties.getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
								//.value-axis-valueAxes"+i+" ."+cssClassName+"
								sbCss.append("."+cssClassName+"amcharts-value-axis.value-axis-valueAxes"+i+" ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
							}
							sbCss.append(graphProperties.getyAxisPropertiesMap().get("M"+0).generateYAxisCss(cssClassName));
						}
					}
					//sbCss.append(graphProperties.getyAxisPropertiesMap().get("M"+0).generateYAxisCss(cssClassName));
					//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {margin-left:30px; overflow:hidden;}");
					else if(!noOfMeasureInYaxis)
					{
						if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH)
						{	
							sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getyAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
							if (isYAxisVisible)
								sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getyAxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						}

						//Y-axis Line properties start
						if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
						{
							String dasharrayString = null;
							int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
							switch(graphProperties.getyAxisProperties().getLineProperties().getStyle())
							{
							case 0:
								yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
								break;
							case 1:
								dasharrayString = "5";
								break;
							case 2:
								dasharrayString = "2";
								break;
							case 3:
								dasharrayString = "5 2";
								break;
							case 4:
								dasharrayString = "5 2 2";
								break;
							}
							String strokeColor = graphProperties.getyAxisProperties().getLineProperties().getColor();
							//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

							if(graphType == GraphConstants.BUBBLE_GRAPH || graphType == GraphConstants.SCATTER_LINE_GRAPH)
							{
								if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
								{
									sbCss.append("."+cssClassName+"value-axis-ValueAxis-2 ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
								}
							}
							else
							{
								//Y-axis Line properties start
								sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");
								//Y-axis Line properties end
							}
						}
						//Y-axis Line properties end

						//Y-axis tick line.

						//Y-axis tick line.
						if(graphType != GraphConstants.BUBBLE_GRAPH && graphType != GraphConstants.SCATTER_LINE_GRAPH && graphType !=  GraphConstants.COMBINED_GRAPH )
						{
							String tickStrokeColor = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
							int strokeLineThickness = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
							sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
						}
						sbCss.append(graphProperties.getyAxisProperties().generateYAxisCss(cssClassName));
						//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-main {margin-left:30px; overflow:hidden;}");	
					}
				}
				//Radar Properties
				if(graphType == GraphConstants.DRILLED_RADAR_GRAPH ||
						graphType == GraphConstants.DRILLED_STACKED_RADAR_GRAPH)
				{
					//X-axis
					if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isVisible())
					{
						sbCss.append("."+cssClassName+"amcharts-axis-title {"+graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
						String fillColor = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getFontColor();
						sbCss.append("."+cssClassName+"amcharts-axis-title {fill:"+fillColor+"}");
						//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-title {text-decoration: none;}");
					}
					//sbCss.append("."+cssClassName+"amcharts-axis-title {"+graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
					
					//Y-axis 
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().isVisible())
					{	
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-label {"+ graphProperties.getyAxisPropertiesMap().get("M0").getLabelProperties().getFontProperties().toString()+"}");
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-label {fill:"+ graphProperties.getyAxisPropertiesMap().get("M0").getLabelProperties().getFontProperties().getFontColor().toString()+"}");
					}
					if(graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().isVisible())
					{	
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-title {"+ graphProperties.getyAxisPropertiesMap().get("M0").getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
						sbCss.append("."+cssClassName+"amcharts-value-axis ."+"amcharts-axis-title {fill:"+ graphProperties.getyAxisPropertiesMap().get("M0").getyAxisTitleTrendProperties().getFontProperties().getFontColor().toString()+"}");
					}
					if(graphInfo.getGraphProperties().getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
					{
						String tickStrokeColor = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
						int strokeLineThickness = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
						sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
					}
					
					//x-axis title amcharts-label
					if(isXAxisVisible) 
						sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");

					//radar border properties start
					/*int radarBorderWidth = radar.getBorderProperties().getAllBorderWidth();
					int radarBorderStyle = 0;
					if(radar.getBorderProperties().isVisible())
					{
						switch(radar.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							radarBorderWidth = 0;
							break;
						case "dashed":
							radarBorderStyle = 5;
							break;
						case "dotted":
							radarBorderStyle = 2;
							break;
						}
						String radarStrokeColor = radar.getBorderProperties().getAllBorderColor();
						sbCss.append("."+cssClassName+"amcharts-graph-stroke{stroke:"+radarStrokeColor+";stroke-opacity:1;stroke-width:"+radarBorderWidth+"; stroke-dasharray:"+radarBorderStyle+"}");
					}*/
					
					//Radar axis start
					String radarAxisStyle = null;
					if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
					{
						
						switch (graphProperties.getxAxisProperties().getLineProperties().getStyle()) {
						case 1:
							radarAxisStyle = "5";
							break;
						case 2:
							radarAxisStyle = "2";
							break;
						}
					}
					sbCss.append("."+cssClassName+"amcharts-axis-line{stroke-dasharray:"+radarAxisStyle+"}");
					//Radar axis end
					
					//radar border properties end
				} 
				
				// bar border properties start
				/*if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible() && graphInfo.getGraphType() != com.elegantjbi.service.graph.GraphConstants.HEAT_MAP_GRAPH)
				{
					int borderWidth = graphProperties.barProperties.getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.barProperties.getBorderProperties().isVisible())
					{
						switch(graphProperties.barProperties.getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.barProperties.getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}*/
				// bar border properties end
				
				//Histogram border properties start
				if(graphInfo.getGraphProperties().getHistogram().getBarProperties().getBorderProperties().isVisible() && graphInfo.getGraphType() == com.elegantjbi.service.graph.GraphConstants.HISTOGRAM_GRAPH)
				{
					int borderWidth = graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderWidth();
					int borderStyle = 0;
					if(graphProperties.getHistogram().getBarProperties().getBorderProperties().isVisible())
					{
						switch(graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderStyle())
						{
						case "none":
							borderWidth =0;
							break;
						case "dashed":
							borderStyle = 5;
							break;
						case "dotted":
							borderStyle = 2;
							break;
						}
					}
					String strokeColor = graphProperties.getHistogram().getBarProperties().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
				}
				//Histogram border properties end
				
				//area border properties start
				/*int areaBorderWidth = graphProperties.getGraphArea().getBorderProperties().getAllBorderWidth();
				int areaBorderStyle = 0;
				if(graphProperties.getGraphArea().getBorderProperties().isVisible())
				{
					switch(graphProperties.getGraphArea().getBorderProperties().getAllBorderStyle())
					{
					case "none":
						areaBorderWidth =0;
						break;
					case "dashed":
						areaBorderStyle = 5;
						break;
					case "dotted":
						areaBorderStyle = 2;
						break;
					}

					String areaStrokeColor = graphProperties.getGraphArea().getBorderProperties().getAllBorderColor();
					sbCss.append("."+cssClassName+"amcharts-graph-fill{stroke:"+areaStrokeColor+";stroke-opacity:1;stroke-width:"+areaBorderWidth+"; stroke-dasharray:"+areaBorderStyle+"}");
				}*/
				//area border properties end
			}
			
			sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-axis-main{float:none; display:inline-block; text-align:left;}");
			//dashboard
			if(!cssClassName.isEmpty())
			{	
			sbCss.append("."+cssClassName+"item show { display:block}");
			sbCss.append("."+cssClassName+"item { display:none}");
			sbCss.append("."+cssClassName+"left span { background-position:right top;}");
			sbCss.append("."+cssClassName+"right span { background-position:left top;}");
			sbCss.append("."+cssClassName+"left{top:50%;height:15%}");
			sbCss.append("."+cssClassName+"right{top:50%;left:90%;height:15%}");
			}
			//generating media query css for Iphone size devices.
			sbCss.append("@media (max-width:768px){ ."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-image-axis-main{display:block;}");
			sbCss.append("}");
			
			
			
			//Graph-Area
			if (graphInfo.getGraphType() == GraphConstants.NUMERIC_DIAL_GAUGE) {
				sbCss.append("."+cssClassName+"gauge-img-box{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().toString()+" }");
			} else {
				//sbCss.append("."+cssClassName+"graph-img-box{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().toString()+" }");
				if(graphInfo.getGraphType() == GraphConstants.DOUGHNUT_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PIE_GRAPH	) {
					String tmpCss = graphAreaBoxCss+"innermonitor";
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().isVisible())
					{
						sbCss.append("#"+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");
					}
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().isVisible())
					{
						sbCss.append("#"+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
					}
					sbCss.append("#"+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
					sbCss.append("#"+tmpCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");
					/*if(graphProperties.getDoughNutGraph().isClustered())
					{
						sbCss.append("."+dashboardCss+"nested-graph-child{position:absolute;}");
					}
					else
					{
						sbCss.append("."+dashboardCss+"nested-graph-child{position:relative;}");
					}
					sbCss.append("."+dashboardCss+"nested-graph-parent{position:relative; }");*/
				}
				else
				{
					if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().isVisible())
					{
						sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");
						sbCss.append(".d3-chart-div"+graphAreaBoxCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");
					}
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");
					
					sbCss.append(".d3-chart-div"+graphAreaBoxCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
					sbCss.append(".d3-chart-div"+graphAreaBoxCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
					sbCss.append(".d3-chart-div"+graphAreaBoxCss+"{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");
				}
			}

			//General Properties
			sbCss.append("."+graphAreaBoxCss+"section-graph-box-1 ."+graphAreaBoxCss+"object-area-main{"+ graphProperties.getGeneralProperties().toString()+" width:100%; clear:both; position:relative; overflow:hidden;}");
			sbCss.append("."+graphAreaBoxCss+"section-graph-box-1 ."+graphAreaBoxCss+"object-area-box{margin:0; text-align:center;}");
			
			//Data value
			
		
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
			sbCss.append("."+cssClassName+"amcharts-graph-column .amcharts-graph-label {"+ graphProperties.getCombinedDataValueProperties().getBardataValuePoint().getFontProperties().toString()+" }");
			String lineFontCss = graphProperties.getCombinedDataValueProperties().getLinedataValuePoint().getFontProperties().toString();
			sbCss.append(".")
		     .append(cssClassName)
		     .append("amcharts-graph-line .amcharts-graph-label, .")
		     .append(cssClassName)
		     .append("amcharts-graph-smoothedLine .amcharts-graph-label, .")
		     .append(cssClassName)
		     .append("amcharts-graph-step .amcharts-graph-label {")
		     .append(lineFontCss)
		     .append("}");
			}else{
				sbCss.append("."+cssClassName+"amcharts-graph-label {"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" fill:"+graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor()+"; width:20px;white-space: -moz-pre-wrap;}");
				sbCss.append(".d3-data-value"+graphAreaBoxCss+" tspan{"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().toString()+" width:20px;white-space: -moz-pre-wrap;}");
				sbCss.append(".d3-data-value"+graphAreaBoxCss+" tspan{fill:"+ graphProperties.getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor().toString()+";}");
				
			}
			
			//Data Value Mouse Over
			if (graphInfo.getGraphType() == GraphConstants.COMBINED_GRAPH) {
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{" + getCombinedDataValueProperties().getBardataValueMouseOver().toString() + "}");
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{font-family:"+ getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
				if(graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().isVisible())
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
				else
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:0;}");
				//sbCss.append("#line"+ cssClassName +" {padding:2px; position : absolute; z-index: 3000;" + getCombinedDataValueProperties().getLinedataValueMouseOver().toString() + "}");
				if(graphInfo.getGraphProperties().getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().isVisible())
				{
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
					
					String balloonBorderDashArray = null;
					
					switch (graphProperties.getCombinedDataValueProperties().getBardataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
					case "none":
						balloonBorderDashArray ="0";
						break;
					case "dashed":
						balloonBorderDashArray = "5";
						break;
					case "dotted":
						balloonBorderDashArray = "2";
						break;
					}
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
				}
			}
			else if(graphInfo.getGraphType() == GraphConstants.PIE_GRAPH)
			{
				List<Integer> multipleMeasureDataValueIndexArr = new ArrayList<Integer>();
				for (int itr = 0; itr < graphInfo.getGraphData().getColList().size(); itr++) {
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().size() > 1 &&
							(graphInfo.getRowColumns().size() == 0 || graphInfo.getColColumns().size() == 0))
					{
						multipleMeasureDataValueIndexArr.add(itr);
					}
					else
					{	
						multipleMeasureDataValueIndexArr.add(0);
					}	
				}
				
				for (int itr = 0; itr < graphInfo.getGraphData().getColList().size(); itr++) {
					int index = multipleMeasureDataValueIndexArr.get(itr)%graphInfo.getGraphData().getColList().size();
					String chartId = "";
					//Data value start
					if(!cssClassName.isEmpty())
					{
						chartId ="."+graphAreaBoxCss+"_"+(itr-1);
					}
					else
					{
						chartId = "#chart"+itr;
					}
					sbCss.append(chartId+" .amcharts-pie-label"+"{"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValuePoint().getFontProperties().toString()+" }");
					//Data value end
					
					//Mouse over value start
					sbCss.append("."+cssClassName+"amcharts-balloon-div div .amcharts-balloon-textM"+itr+"{"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().toString()+" }");
					sbCss.append("."+cssClassName+"amcharts-balloon-div div .amcharts-balloon-textM"+itr+"{font-family:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
					
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
					{	
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{fill:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
					}
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().isVisible()
							&& !graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle().equalsIgnoreCase("none"))
					{
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
						
						int balloonBorderDashArray = 0;
						switch (graphProperties.getDataValuePropertiesMap().get("M"+index).getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
						case "dashed":
							balloonBorderDashArray = 5;
							break;
						case "dotted":
							balloonBorderDashArray = 2;
							break;
						}
						sbCss.append(chartId+" ."+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
					}
					//Mouse over value end
				}
			}
			else{
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{"+ graphProperties.getDataValueProperties().getDataValueMouseOver().toString()+" }");
				sbCss.append("."+cssClassName+"amcharts-balloon-div div{font-family:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
				
				sbCss.append(".tooltip"+graphAreaBoxCss+"{position:fixed}");
				sbCss.append(".tooltip"+graphAreaBoxCss+"{"+ graphProperties.getDataValueProperties().getDataValueMouseOver().toString()+" }");
				sbCss.append(".tooltip"+graphAreaBoxCss+"{font-family:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontName().toString()+" !important;}");
				
				if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().isVisible())
				{	
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{fill:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
					sbCss.append(".tooltip"+graphAreaBoxCss+"{background-color:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBackground().getBackGroundColor()+" ;fill-opacity:1;}");
				}
				if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().isVisible()
						&& !graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle().equalsIgnoreCase("none"))
				{
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-width:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+" ;}");
					
					sbCss.append(".tooltip"+graphAreaBoxCss+"{border-color:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderColor()+" ;}");
					sbCss.append(".tooltip"+graphAreaBoxCss+"{border-width:"+ graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderWidth()+"px ;}");
					
					String balloonBorderDashArray = null;
					switch (graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle()) {
					case "none":
						balloonBorderDashArray ="0";
						break;
					case "dashed":
						balloonBorderDashArray = "5";
						break;
					case "dotted":
						balloonBorderDashArray = "2";
						break;
					}
					sbCss.append("."+cssClassName+"amcharts-balloon-bg{stroke-dasharray:"+balloonBorderDashArray +" ;}");
					sbCss.append(".tooltip"+graphAreaBoxCss+"{border-style:"+graphProperties.getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverBorder().getAllBorderStyle() +" ;}");
				}
				//sbCss.append("#tooltip {padding:2px; position : absolute; z-index: 3000;" + getDataValueProperties().getDataValueMouseOver().toString() + "}");	
				//sbCss.append("#tooltip"+cssClassName+" {padding:2px; position : absolute; z-index: 3000;" + getDataValueProperties().getDataValueMouseOver().toString() + "}");	
			}
			
			//for BredCrum start
			FontProperties breadCrumFont = graphProperties.getBreadCrumProperties().getBreadCrumFont();
			BackgroundProperties bredCrumBackground = graphProperties.getBreadCrumProperties().getBreadCrumBackGround();
			String strBreadcrumBorder = "";
			if(cssClassName.isEmpty()) 
				strBreadcrumBorder = "border-bottom: 1px";
			sbCss.append("."+cssClassName+"breadcrumb-section-graph{"+strBreadcrumBorder+" solid #ebebeb; border-radius:0; padding:0 0 0 10px;"/*+"text-decoration:"+breadCrumFont.getTextDecoration()+";"*/);
			
			if(breadCrumFont != null)
				sbCss.append("color:"+breadCrumFont.getFontColor()+";");						
			if(!cssClassName.isEmpty()){
				sbCss.append("float:left;width:100%;");	
			}
			if(bredCrumBackground!= null && !bredCrumBackground.isBackgroundTransparent()) {
				if(bredCrumBackground.isVisible()){
					sbCss.append("background:"+bredCrumBackground.getBackGroundColor()+";");
				}
			}
			sbCss.append("}");
			
			if(!cssClassName.isEmpty()) {
				if(selectionFontProp != null) {
				sbCss.append(" ."+dashTdId+"-selection{"+selectionFontProp.toString()+" fill:"+selectionFontProp.getFontColor()+";} ");
				}
			}
			
			if(breadCrumFont != null)
				sbCss.append(".breadcrumb-section-graph .breadcrumb-left-section > li.active a{color:"+breadCrumFont.getFontColor()+";");
			sbCss.append("}");		
			//for BredCrum end			
		} catch(Exception ex) {
			ApplicationLog.error(ex);
		}
		//System.out.println(sbCss.toString());
		return sbCss.toString();
	}
	public Sunburst getSunburst() {
		return sunburst;
	}

	public void setSunburst(Sunburst sunburst) {
		this.sunburst = sunburst;
	}

	public StringBuilder generateDialGaugeCss(GraphProperties graphProperties ,StringBuilder sbCss, String cssClassName)
	{
		if(graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().isVisible())
		{
			sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getBackGround().toString()+" }");	
		}
		sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelBorder().toString()+" }");
		sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelShadow().toString()+" }");
		sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelRounded().toString()+" }");

//		sbCss.append("."+cssClassName+"amcharts-chart-div{"+ graphProperties.getGraphAreaProperties().getGeneralGraphArea().toString()+" }");

		sbCss.append("."+cssClassName+"amcharts-label {"+ graphProperties.getGaugeTitleProperties().getGaugeFont().toString()+";}");
		sbCss.append("."+cssClassName+"amcharts-label {fill:"+ graphProperties.getGaugeTitleProperties().getGaugeFont().getFontColor().toString()+";}");

	/*	int gaugeTitleTransformX = 50;
		int gaugeTitleTransformY = 50 + graphProperties.getGaugeTitleProperties().getDistanceFromCenter();
		sbCss.append("."+cssClassName+"amcharts-label {transform:translate("+gaugeTitleTransformX+"%,"+gaugeTitleTransformY+"%);}");
	*/	
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-top-label {"+ graphProperties.getGaugeDataValueTarget().getFontText().toString()+";}");
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-top-label {fill:"+ graphProperties.getGaugeDataValueTarget().getFontText().getFontColor().toString()+";}");

	/*	int topTextTransformX = 50;
		int topTextTransformY = 50 - Integer.parseInt(graphProperties.getGaugeDataValueTarget().getDataValueConfiguration().getDistanceFromCenter());
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-top-label {transform:translate("+topTextTransformX+"%,"+topTextTransformY+"%);}");
	*/	
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-bottom-label {"+ graphProperties.getGaugeDataValueActual().getFontText().toString()+";}");
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-bottom-label {fill:"+ graphProperties.getGaugeDataValueActual().getFontText().getFontColor().toString()+";}");

	/*	int bottomTextTransformX = 50;
		int bottomTextTransformY = 50 + Integer.parseInt(graphProperties.getGaugeDataValueActual().getDataValueConfiguration().getDistanceFromCenter());
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-bottom-label {transform:translate("+bottomTextTransformX+"%,"+bottomTextTransformY+"%);}");
	*/	
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-label {"+ graphProperties.getGaugeDataValueZone().getFontText().toString()+";}");
		sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-label {fill:"+ graphProperties.getGaugeDataValueZone().getFontText().getFontColor().toString()+";}");

		if(graphProperties.getGaugeScaleProperties().getMinorTickPropertis().isVisible())
		{
			String minorTickColor = graphProperties.getGaugeScaleProperties().getMinorTickPropertis().getColor();
			int minorTickThickness = graphProperties.getGaugeScaleProperties().getMinorTickPropertis().getThickness();
			sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-tick-minor {stroke:"+ minorTickColor+";stroke-width:"+minorTickThickness+";}");
		}
		double getbandsTransparency = (double)graphProperties.getGaugeDialProperties().getZoneProperties().getDialColorProperties().getTransparency();
		int bandsBorderThickness = graphProperties.getGaugeDialProperties().getZoneProperties().getBorder().getWidth();
		String bandsBorderStyle = graphProperties.getGaugeDialProperties().getZoneProperties().getBorder().getStyle();
		String[] bandsBorderArray = generateDashArray(bandsBorderThickness,bandsBorderStyle,getbandsTransparency);
		String bandsBorderColor = graphProperties.getGaugeDialProperties().getZoneProperties().getBorder().getColor();
		//sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+cssClassName+"amcharts-axis-band {stroke:"+ bandsBorderColor+";stroke-width:"+bandsBorderArray[0]+";stroke-opacity:1;stroke-dasharray:"+bandsBorderArray[1]+";opacity:"+ bandsBorderArray[2]+";}");

		int needleBorderThickness = graphProperties.getGaugeNeedleProperties().getNeedle().getBorder().getWidth();
		String needleBorderStyle = graphProperties.getGaugeNeedleProperties().getNeedle().getBorder().getStyle();
		double getNeedleTransparency = (double)graphProperties.getGaugeNeedleProperties().getNeedle().getTransparency();
		String[] needleBorderArray = generateDashArray(needleBorderThickness,needleBorderStyle,getNeedleTransparency);
		String needleBorderColor = graphProperties.getGaugeNeedleProperties().getNeedle().getBorder().getColor();
		sbCss.append("."+cssClassName+"amcharts-gauge-arrow "+"."+"amcharts-gauge-arrow {stroke:"+ needleBorderColor+";stroke-width:"+needleBorderArray[0]+";stroke-opacity:1;stroke-dasharray:"+needleBorderArray[1]+";opacity:"+ needleBorderArray[2]+";}");

		double getNeedleCapTransparency = (double)graphProperties.getGaugeNeedleProperties().getNeedleCap().getTransparency();
		int needleCapBorderThickness = graphProperties.getGaugeNeedleProperties().getNeedleCap().getBorder().getWidth();
		String needleCapBorderStyle = graphProperties.getGaugeNeedleProperties().getNeedleCap().getBorder().getStyle();
		String[] needleCapBorderArray = generateDashArray(needleCapBorderThickness,needleCapBorderStyle,getNeedleCapTransparency);
		String needleCapBorderColor = graphProperties.getGaugeNeedleProperties().getNeedleCap().getBorder().getColor();
		sbCss.append("."+cssClassName+"amcharts-gauge-arrow "+"."+"amcharts-gauge-arrow-nail {stroke:"+ needleCapBorderColor+";stroke-width:"+needleCapBorderArray[0]+";stroke-opacity:1;stroke-dasharray:"+needleCapBorderArray[1]+";opacity:"+ needleBorderArray[2]+";}");
		sbCss.append("."+cssClassName+"amcharts-gauge-arrow "+"."+"amcharts-gauge-arrow-nail {fill:"+ graphProperties.getGaugeNeedleProperties().getNeedleCap().getColor()+"; }");
		if(graphProperties.getGaugeScaleProperties().getBaseLineTickProperties().isVisible())
		{
			double getBaselineTransparency = (double)graphProperties.getGaugeScaleProperties().getBaseLineTickProperties().getTransparency();
			int BaselineBorderThickness = graphProperties.getGaugeScaleProperties().getBaseLineTickProperties().getWidth();
			String BaselineBorderStyle = String.valueOf(graphProperties.getGaugeScaleProperties().getBaseLineTickProperties().getStyle());
		
			String[] BaselineBorderArray = generateDashArray(BaselineBorderThickness,BaselineBorderStyle,getBaselineTransparency);
			String BaselineBorderColor = graphProperties.getGaugeScaleProperties().getBaseLineTickProperties().getColor();
		
			sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+cssClassName+"amcharts-axis-line {stroke:"+ BaselineBorderColor+";stroke-width:"+BaselineBorderArray[0]+";stroke-opacity:1;stroke-dasharray:"+BaselineBorderArray[1]+";opacity:"+ BaselineBorderArray[2]+";}");
		}

		//sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-line {stroke-opacity:1;fill-opacity:1;}");

		if(graphProperties.getGaugeDataValueZone().getDataValueConfiguration().isVisible())
		{
			sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-label {"+graphProperties.getGaugeDataValueZone().getFontText().toString()+";}");
			sbCss.append("."+cssClassName+"amcharts-gauge-axis "+"."+"amcharts-axis-label {fill:"+graphProperties.getGaugeDataValueZone().getFontText().getFontColor().toString()+";}");
		}
		return sbCss;
	}
	public String generateCombineCss(GraphInfo graphInfo, boolean isXAxisVisible, boolean isYAxisVisible, String cssClassName){
		StringBuilder sbCss = new StringBuilder();
		GraphProperties graphProperties = graphInfo.getGraphProperties();
		
		//Legend		
			sbCss = graphProperties.getCombinedGraph().getBarLegendProperties().generateLegendProperties(sbCss, cssClassName+"combinedBar");
			//sbCss = graphProperties.getCombinedGraph().getLineLegendProperties().generateLegendProperties(sbCss, cssClassName+"combinedLine");
		
			//X Axis	
			if(isXAxisVisible) {
				//sbCss.append("."+cssClassName+"section-graph-box-1 ."+cssClassName+"graph-title-x-main ."+cssClassName+"graph-title-x {");
				//sbCss.append(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().toString());
				//sbCss.append(" min-height: 30px;line-height: 30px; }");
				sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-label {"+ graphProperties.getxAxisProperties().getLabelProperties().getFontProperties().toString()+"}");
				//xaxis line start
				if(graphProperties.getxAxisProperties().getLineProperties().isVisible())
				{
					String dasharrayString = null;
					int xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
					switch(graphProperties.getxAxisProperties().getLineProperties().getStyle())
					{
					case 0:
						xaxisLineThickness = graphProperties.getxAxisProperties().getLineProperties().getThickness();
						break;
					case 1:
						dasharrayString = "5";
						break;
					case 2:
						dasharrayString = "2";
						break;
					case 3:
						dasharrayString = "5 2";
						break;
					case 4:
						dasharrayString = "5 2 2";
						break;
					}
					String strokeColor = graphProperties.getxAxisProperties().getLineProperties().getColor();
					sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+xaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+"}");
				}
				//xaxis line end

				sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-title {"+ graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().toString()+"}");
				if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isVisible()) {
					if(graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().isWidthToText()) {
						sbCss.append("."+cssClassName+"section-graph-box-1 ."/*+cssClassName*/+"graph-title-x-main { text-align : "+graphProperties.getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getTextAlignment()+"; width:100%; float:left;}");
					}
				}
				/*sbCss.append(graphProperties.getxAxisProperties().toString());*/
				//xaxis tick Line start 
				String tickStrokeColor = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
				int strokeLineThickness = graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();//xAxisProperties.lineProperties.axisMajorLineTickTrendProperties.width
				sbCss.append("."+cssClassName+"amcharts-category-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
				//xaxis tick Line end

			}
			
			//Y Axis Label Properties 
			
			sbCss.append(".amcharts-value-axis "+".value-axis-ValueAxis-2 "+".amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
			sbCss.append(".amcharts-value-axis "+".value-axis-ValueAxis-1 "+".amcharts-axis-label {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getLabelProperties().getFontProperties().toString()+"}");
			
			//Y Axis Title Properties 
			if (isYAxisVisible)
			{
			sbCss.append(".amcharts-value-axis "+".value-axis-ValueAxis-2"+".amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
			sbCss.append(".amcharts-value-axis "+".value-axis-ValueAxis-1"+".amcharts-axis-title {"+ graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().getFontProperties().toString()+"}");
			}
			
			
			//Y-axis Line properties start
			if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
			{
				String dasharrayString = null;
				int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
				switch(graphProperties.getyAxisProperties().getLineProperties().getStyle())
				{
				case 0:
					yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
					break;
				case 1:
					dasharrayString = "5";
					break;
				case 2:
					dasharrayString = "2";
					break;
				case 3:
					dasharrayString = "5 2";
					break;
				case 4:
					dasharrayString = "5 2 2";
					break;
				}
				String strokeColor = graphProperties.getyAxisProperties().getLineProperties().getColor();
				//sbCss.append("."+cssClassName+"amcharts-value-axis ."+cssClassName+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

			}
			//Y-axis Line properties end
			
			//Y-axisBAR  Line properties start
			if(graphProperties.getyAxisProperties().getLineProperties().isVisible())
			{
				String dasharrayString = null;
				int yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
				switch(graphProperties.getyAxisProperties().getLineProperties().getStyle())
				{
				case 0:
					yaxisLineThickness = graphProperties.getyAxisProperties().getLineProperties().getThickness();
					break;
				case 1:
					dasharrayString = "5";
					break;
				case 2:
					dasharrayString = "2";
					break;
				case 3:
					dasharrayString = "5 2";
					break;
				case 4:
					dasharrayString = "5 2 2";
					break;
				}
				String strokeColor = graphProperties.getyAxisProperties().getLineProperties().getColor();
				sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-line{stroke:"+strokeColor+";stroke-width:"+yaxisLineThickness+"stroke-opacity:1;stroke-dasharray:"+dasharrayString+";}");

			}

			//Y-axis tick line.
			String tickStrokeColor = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();//graphProperties.getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getColor();
			int strokeLineThickness = graphProperties.getyAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getWidth();
			if(graphInfo.getGraphType() != GraphConstants.BUBBLE_GRAPH || graphInfo.getGraphType() != GraphConstants.SCATTER_LINE_GRAPH)
			{
				sbCss.append("."+cssClassName+"amcharts-value-axis ."/*+cssClassName*/+"amcharts-axis-tick{stroke:"+tickStrokeColor+";stroke-opacity:1;stroke-width:"+strokeLineThickness+";}");
			}
			
		
			
			
			
			String legendMarginCss = "";
			if (graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
				//sbCss.append(graphProperties.getCombinedYaxisProperties().getBarYaxisProperties().generateCombineYAxisCss(cssClassName+"value-axis-ValueAxis-1"));
				legendMarginCss = "margin-left:30px;";
			}
			if (graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().getyAxisTitleTrendProperties().isVisible()) {
				//sbCss.append(graphProperties.getCombinedYaxisProperties().getLineYaxisProperties().generateCombineYAxisCss(cssClassName+"value-axis-ValueAxis-2"));				
				legendMarginCss += "margin-right:30px;";
			}
			sbCss.append("."+cssClassName+"section-graph-box-1 ."/*+cssClassName*/+"graph-image-main {"+legendMarginCss+" overflow:hidden;}");
			// bar border properties start
			if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible())
			{
				int borderWidth = graphProperties.barProperties.getBorderProperties().getAllBorderWidth();
				int borderStyle = 0;
				if(graphProperties.barProperties.getBorderProperties().isVisible())
				{
					switch(graphProperties.barProperties.getBorderProperties().getAllBorderStyle())
					{
					case "none":
						borderWidth =0;
						break;
					case "dashed":
						borderStyle = 5;
						break;
					case "dotted":
						borderStyle = 2;
						break;
					case "solid":
						borderStyle = -1;
						break;
					}
				}
				String strokeColor = graphProperties.barProperties.getBorderProperties().getAllBorderColor();
				sbCss.append("."+cssClassName+"amcharts-graph-column-element{stroke:"+strokeColor+";stroke-opacity:1;stroke-width:"+borderWidth+"; stroke-dasharray:"+borderStyle+"}");
			}
			
		
			
		return sbCss.toString();
	}
	public boolean isClickFromSave() {
		return clickFromSave;
	}

	public void setClickFromSave(boolean clickFromSave) {
		this.clickFromSave = clickFromSave;
	}
	
	/**
	 * @return the tranceperancy
	 */
	public int getTranceperancy() {
		return tranceperancy;
	}
	/**
	 * @param tranceperancy
	 *            the tranceperancy to set
	 */
	public void setTranceperancy(int tranceperancy) {
		this.tranceperancy = tranceperancy;
	}
	
	/**
	 * @return the totalBarColor
	 */
	public String getOtherBarColor() {
		return otherBarColor;
	}
	/**
	 * @param otherBarColor
	 *            the otherBarColor to set
	 */
	public void setOtherBarColor(String otherBarColor) {
		this.otherBarColor = otherBarColor;
	}
	
	/**
	 * @return the totalBarColor
	 */
	public String getTotalBarColor() {
		return totalBarColor;
	}
	/**
	 * @param totalBarColor
	 *            the totalBarColor to set
	 */
	public void setTotalBarColor(String totalBarColor) {
		this.totalBarColor = totalBarColor;
	}
	
	/**
	 * @return the colorType
	 */
	public int getColorType() {
		return colorType;
	}
	/**
	 * @param colorType
	 *            the colorType to set
	 */
	public void setColorType(int colorType) {
		this.colorType = colorType;
	}
	/**
	 * @return the sameColor
	 */
	public boolean isSameColor() {
		return getColorType() == 2;
	}
	/**
	 * @param sameColor
	 *            the sameColor to set
	 */
	public void setSameColor(boolean sameColor) {
		this.sameColor = sameColor;
	}
	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color
	 *            the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}
	/**
	 * @return the customColors
	 */
	public List<String> getCustomColors() {
		return customColors;
	}
	/**
	 * @param customColors
	 *            the customColors to set
	 */
	public void setCustomColors(List<String> customColors) {
		this.customColors = customColors;
	}
	
	/**
	 * Get map containing columns and there label values
	 * @return Map<String, String> Column-name,label
	 */
	public Map<String, String> getColLabelsMap() {
		return colLabelsMap;
	}

	/**
	 * Set map containing columns and there label values 
	 * @param colLabelsMap
	 */
	public void setColLabelsMap(Map<String, String> colLabelsMap) {
		this.colLabelsMap = colLabelsMap;
	}
	
	public Map getGraphsVisibleMap() {
		return graphsVisibleMap;
	}

	public void setGraphsVisibleMap(Map graphsVisibleMap) {
		this.graphsVisibleMap = graphsVisibleMap;
	}

	/**
	 * @return the editByCreator
	 */
	public Boolean getEditByCreator() {
		return editByCreator;
	}

	/**
	 * @param editByCreator the editByCreator to set
	 */
	public void setEditByCreator(Boolean editByCreator) {
		this.editByCreator = editByCreator;
	}

	public String getNegativeBarColor() {
		return negativeBarColor;
	}

	public void setNegativeBarColor(String negativeBarColor) {
		this.negativeBarColor = negativeBarColor;
	}

	public int getZoomType() {
		return zoomType;
	}

	public void setZoomType(int zoomType) {
		this.zoomType = zoomType;
	}

	public GraphChartCursor getGraphChartCursor() {
		return graphChartCursor;
	}

	public void setGraphChartCursor(GraphChartCursor graphChartCursor) {
		this.graphChartCursor = graphChartCursor;
	}
	
	public FontProperties getSelectionFontProp() {
		return selectionFontProp;
	}

	public void setSelectionFontProp(FontProperties selectionFontProp) {
		this.selectionFontProp = selectionFontProp;
	}

	public Map<String, YaxisTrendProperties> getyAxisPropertiesMap() {
		return yAxisPropertiesMap;
	}

	public void setyAxisPropertiesMap(
			Map<String, YaxisTrendProperties> yAxisPropertiesMap) {
		this.yAxisPropertiesMap = yAxisPropertiesMap;
	}

	/*public SmartenProperties getSmartenLabelProperties() {
		return smartenLabelProperties;
	}

	public void setSmartenLabelProperties(SmartenProperties smartenLabelProperties) {
		this.smartenLabelProperties = smartenLabelProperties;
	}*/
	
	public SmartenProperties getSmartenProperties() {
		return smartenProperties;
	}

	public void setSmartenProperties(SmartenProperties smartenProperties) {
		this.smartenProperties = smartenProperties;
	}
	
	public MapAreaProperties getMapAreaProperties() {
		return mapAreaProperties;
	}

	public void setMapAreaProperties(MapAreaProperties mapAreaProperties) {
		this.mapAreaProperties = mapAreaProperties;
	}
	
	public SmartenColorProperties getSmartenColorProperties() {
		return smartenColorProperties;
	}

	public void setSmartenColorProperties(SmartenColorProperties smartenColorProperties) {
		this.smartenColorProperties = smartenColorProperties;
	}

	public Map<String, TrendDataValueProperties> getDataValuePropertiesMap() {
		return dataValuePropertiesMap;
	}

	public void setDataValuePropertiesMap(
			Map<String, TrendDataValueProperties> dataValuePropertiesMap) {
		this.dataValuePropertiesMap = dataValuePropertiesMap;
	}

	public int getSmartenChartHeight() {
		return smartenChartHeight;
	}

	public void setSmartenChartHeight(int smartenChartHeight) {
		this.smartenChartHeight = smartenChartHeight;
	}
	

	public int getRangeColorDivValue() {
		return rangeColorDivValue;
	}

	public void setRangeColorDivValue(int rangeColorDivValue) {
		this.rangeColorDivValue = rangeColorDivValue;
	}

	public List getRangeColorList() {
		return rangeColorList;
	}

	public void setRangeColorList(List rangeColorList) {
		this.rangeColorList = rangeColorList;
	}

	public int getColorRange() {
		return colorRange;
	}

	public void setColorRange(int colorRange) {
		this.colorRange = colorRange;
	}

	public String getRangeStartColor() {
		return rangeStartColor;
	}

	public void setRangeStartColor(String rangeStartColor) {
		this.rangeStartColor = rangeStartColor;
	}

	public String getRangeEndColor() {
		return rangeEndColor;
	}

	public void setRangeEndColor(String rangeEndColor) {
		this.rangeEndColor = rangeEndColor;
	}

	public int getCustomColorType() {
		return customColorType;
	}

	public void setCustomColorType(int customColorType) {
		this.customColorType = customColorType;
	}

	public int getMapColorType() {
		return mapColorType;
	}

	public void setMapColorType(int mapColorType) {
		this.mapColorType = mapColorType;
	}

	public String getSmartenMapShape() {
		return smartenMapShape;
	}

	public void setSmartenMapShape(String smartenMapShape) {
		this.smartenMapShape = smartenMapShape;
	}

	public int getSmartenMapSize() {
		return smartenMapSize;
	}

	public void setSmartenMapSize(int smartenMapSize) {
		this.smartenMapSize = smartenMapSize;
	}

	public int getRecommendedColorType() {
		return recommendedColorType;
	}

	public void setRecommendedColorType(int recommendedColorType) {
		this.recommendedColorType = recommendedColorType;
	}

	public boolean isSampling() {
		return sampling;
	}

	public void setSampling(boolean sampling) {
		this.sampling = sampling;
	}

	public boolean isSnapShotSampling() {
		return snapShotSampling;
	}

	public void setSnapShotSampling(boolean snapShotSampling) {
		this.snapShotSampling = snapShotSampling;
	}

	public boolean isSamplingSnapShotChanged() {
		return samplingSnapShotChanged;
	}

	public void setSamplingSnapShotChanged(boolean samplingSnapShotChanged) {
		this.samplingSnapShotChanged = samplingSnapShotChanged;
	}

	public boolean isSnapShotChanged() {
		return snapShotChanged;
	}

	public void setSnapShotChanged(boolean snapShotChanged) {
		this.snapShotChanged = snapShotChanged;
	}

	public boolean isCallCreateSmartenResultSet() {
		return callCreateSmartenResultSet;
	}

	public void setCallCreateSmartenResultSet(boolean callCreateSmartenResultSet) {
		this.callCreateSmartenResultSet = callCreateSmartenResultSet;
	}

	public List<String> getAutoColors() {
		return autoColors;
	}

	public void setAutoColors(List<String> autoColors) {
		this.autoColors = autoColors;
	}
	/**
	 * @return the line customColors
	 */
	public List<String> getLineCustomColors() {
		return lineCustomColors;
	}
	/**
	 * @param lineCustomColors
	 *            the lineCustomColors to set
	 */
	public void setLineCustomColors(List<String> lineCustomColors) {
		this.lineCustomColors = lineCustomColors;
	}
	/**
	 * @return the point customColors
	 */
	public List<String> getPointCustomColors() {
		return pointCustomColors;
	}
	/**
	 * @param pointCustomColors
	 *            the pointCustomColors to set
	 */
	public void setPointCustomColors(List<String> pointCustomColors) {
		this.pointCustomColors = pointCustomColors;
	}
	/**
	 * @return the linetype
	 */
	public int getLineType() {
		return lineType;
	}
	/**
	 * @param lineType the lineType to set
	 */
	public void setLineType(int lineType) {
		this.lineType = lineType;
	}
	/**
	 * @return the line color type
	 */
	public int getLineColorType() {
		return lineColorType;
	}
	/**
	 * @param lineColorType the lineColorType to set
	 */
	public void setLineColorType(int lineColorType) {
		this.lineColorType = lineColorType;
	}
	/**
	 * @return the linecolor
	 */
	public String getLinecolor() {
		return linecolor;
	}
	/**
	 * @param linecolor
	 *            the linecolor to set
	 */
	public void setLinecolor(String linecolor) {
		this.linecolor = linecolor;
	}
	/**
	 * @return the pointcolor
	 */
	public String getPointcolor() {
		return pointcolor;
	}
	/**
	 * @param pointcolor
	 *            the pointcolor to set
	 */
	public void setPointcolor(String pointcolor) {
		this.pointcolor = pointcolor;
	}
	/**
	 * @return the pointType
	 */
	public int getPointType() {
		return pointType;
	}
	/**
	 * @param pointType the lineColorType to set
	 */
	public void setPointType(int pointType) {
		this.pointType = pointType;
	}
	/**
	 * @return the pointColorType
	 */
	public int getPointColorType() {
		return pointColorType;
	}
	/**
	 * @param pointColorType the pointColorType to set
	 */
	public void setPointColorType(int pointColorType) {
		this.pointColorType = pointColorType;
	}
	/*public boolean isSplitGraph() {
		return splitGraph;
	}

	public void setSplitGraph(boolean splitGraph) {
		this.splitGraph = splitGraph;
	}
	*/

	public boolean isFromSaveSmartenLabelProp() {
		return fromSaveSmartenLabelProp;
	}

	public void setFromSaveSmartenLabelProp(boolean fromSaveSmartenLabelProp) {
		this.fromSaveSmartenLabelProp = fromSaveSmartenLabelProp;
	}

	public boolean isSamplingCB() {
		return samplingCB;
	}

	public void setSamplingCB(boolean samplingCB) {
		this.samplingCB = samplingCB;
	}

	public boolean isSnapShotSamplingCB() {
		return snapShotSamplingCB;
	}

	public void setSnapShotSamplingCB(boolean snapShotSamplingCB) {
		this.snapShotSamplingCB = snapShotSamplingCB;
	}

	public boolean isPaginationCB() {
		return paginationCB;
	}

	public void setPaginationCB(boolean paginationCB) {
		this.paginationCB = paginationCB;
	}

	public boolean isPagination() {
		return pagination;
	}

	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}

	/**
	 * @return the samplingApplied
	 */
	public boolean isSamplingApplied() {
		return samplingApplied;
	}

	/**
	 * @param samplingApplied the samplingApplied to set
	 */
	public void setSamplingApplied(boolean samplingApplied) {
		this.samplingApplied = samplingApplied;
	}

	/**
	 * @return the notEnoughRecord
	 */
	public boolean isNotEnoughRecord() {
		return notEnoughRecord;
	}

	/**
	 * @param notEnoughRecord the notEnoughRecord to set
	 */
	public void setNotEnoughRecord(boolean notEnoughRecord) {
		this.notEnoughRecord = notEnoughRecord;
	}

	/**
	 * @return the autoAdjustedYAxisRange
	 */
	public boolean isAutoAdjustedYAxisRange() {
		if(autoAdjustedYAxisRange == null) this.autoAdjustedYAxisRange = Boolean.TRUE;
		return autoAdjustedYAxisRange;
	}

	/**
	 * @param autoAdjustedYAxisRange the autoAdjustedYAxisRange to set
	 */
	public void setAutoAdjustedYAxisRange(Boolean autoAdjustedYAxisRange) {
		this.autoAdjustedYAxisRange = autoAdjustedYAxisRange;
	}

	/**
	 * @return the noOfXAxisBlocks
	 */
	public int getNoOfXAxisBlocks() {
		return noOfXAxisBlocks;
	}

	/**
	 * @param noOfXAxisBlocks the noOfXAxisBlocks to set
	 */
	public void setNoOfXAxisBlocks(int noOfXAxisBlocks) {
		this.noOfXAxisBlocks = noOfXAxisBlocks;
	}

	/**
	 * @return the noOfYAxisBlocks
	 */
	public int getNoOfYAxisBlocks() {
		return noOfYAxisBlocks;
	}

	/**
	 * @param noOfYAxisBlocks the noOfYAxisBlocks to set
	 */
	public void setNoOfYAxisBlocks(int noOfYAxisBlocks) {
		this.noOfYAxisBlocks = noOfYAxisBlocks;
	}
	
	public boolean isPaginationChange() {

		return paginationChange;
	}

	public void setPaginationChange(boolean paginationChange) {
		this.paginationChange = paginationChange;
	}

	public String getTotalText() {
		return totalText;
	}
	public void setTotalText(String totalText) {
		this.totalText = totalText;
	}
	
	@Override
	public String toString() {
		return "GraphProperties [generalProperties=" + generalProperties + ", titleProperties=" + titleProperties
				+ ", graphAreaProperties=" + graphAreaProperties + ", graphHorizontalAreaProperties="
				+ graphHorizontalAreaProperties + ", xAxisProperties=" + xAxisProperties + ", yAxisProperties="
				+ yAxisProperties + ", yAxisPropertiesMap=" + yAxisPropertiesMap + ", legendProperties="
				+ legendProperties + ", legendPanelDhadowProperties=" + legendPanelDhadowProperties + ", barProperties="
				+ barProperties + ", backGroundGrid=" + backGroundGrid + ", borderProperties=" + borderProperties
				+ ", dataValueProperties=" + dataValueProperties + ", dataValuePoint=" + dataValuePoint
				+ ", dataValuePropertiesMap=" + dataValuePropertiesMap + ", referenceLine=" + referenceLine
				+ ", lineProperties=" + lineProperties + ", trendlinePropertiesMap=" + trendlinePropertiesMap
				+ ", referencelinePropertiesMap=" + referencelinePropertiesMap + ", graphLineProperties="
				+ graphLineProperties + ", graphArea=" + graphArea + ", pieTitle=" + pieTitle + ", pieGraph=" + pieGraph
				+ ", legendPanelProperties=" + legendPanelProperties + ", fontProperties=" + fontProperties
				+ ", doughnutTitleProperties=" + doughnutTitleProperties + ", doughNutGraph=" + doughNutGraph
				+ ", radar=" + radar + ", combinedGraph=" + combinedGraph + ", combinedYaxisProperties="
				+ combinedYaxisProperties + ", combinedDataValueProperties=" + combinedDataValueProperties
				+ ", barReferenceLine=" + barReferenceLine + ", lineReferenceLine=" + lineReferenceLine
				+ ", barReferencelinePropertiesMap=" + barReferencelinePropertiesMap
				+ ", lineReferencelinePropertiesMap=" + lineReferencelinePropertiesMap + ", bartrendlinePropertiesMap="
				+ bartrendlinePropertiesMap + ", linetrendlinePropertiesMap=" + linetrendlinePropertiesMap
				+ ", candleStick=" + candleStick + ", histogram=" + histogram + ", heatmap=" + heatmap
				+ ", allLabelsProperties=" + allLabelsProperties + ", gaugeTitleProperties=" + gaugeTitleProperties
				+ ", gaugeScaleProperties=" + gaugeScaleProperties + ", gaugeNeedleProperties=" + gaugeNeedleProperties
				+ ", gaugeDialProperties=" + gaugeDialProperties + ", gaugeDataValueScale=" + gaugeDataValueScale
				+ ", gaugeDataValueActual=" + gaugeDataValueActual + ", gaugeDataValueZone=" + gaugeDataValueZone
				+ ", gaugeDataValueTarget=" + gaugeDataValueTarget + ", gaugeLevel=" + gaugeLevel
				+ ", thermometerGauge=" + thermometerGauge + ", breadCrumProperties=" + breadCrumProperties
				+ ", colorType=" + colorType + ", customColors=" + customColors + ", autoColors=" + autoColors
				+ ", sameColor=" + sameColor + ", color=" + color + ", totalBarColor=" + totalBarColor
				+ ", otherBarColor=" + otherBarColor + ", tranceperancy=" + tranceperancy + ", editByCreator="
				+ editByCreator + ", clickFromSave=" + clickFromSave + ", colLabelsMap=" + colLabelsMap
				+ ", graphsVisibleMap=" + graphsVisibleMap + ", negativeBarColor=" + negativeBarColor + ", zoomType="
				+ zoomType + ", graphChartCursor=" + graphChartCursor + ", selectionFontProp=" + selectionFontProp
				+ ", smartenProperties=" + smartenProperties + ", mapAreaProperties=" + mapAreaProperties
				+ ", smartenChartHeight=" + smartenChartHeight + ", smartenColorProperties=" + smartenColorProperties
				+ ", rangeColorDivValue=" + rangeColorDivValue + ", colorRange=" + colorRange + ", rangeColorList="
				+ rangeColorList + ", rangeStartColor=" + rangeStartColor + ", rangeEndColor=" + rangeEndColor
				+ ", customColorType=" + customColorType + ", mapColorType=" + mapColorType + ", smartenMapShape="
				+ smartenMapShape + ", smartenMapSize=" + smartenMapSize + ", recommendedColorType="
				+ recommendedColorType + ", sampling=" + sampling + ", paginationChange=" + paginationChange
				+ ", snapShotSampling=" + snapShotSampling + ", samplingSnapShotChanged=" + samplingSnapShotChanged
				+ ", snapShotChanged=" + snapShotChanged + ", callCreateSmartenResultSet=" + callCreateSmartenResultSet
				+ ", samplingApplied=" + samplingApplied + ", notEnoughRecord=" + notEnoughRecord + ", samplingCB="
				+ samplingCB + ", snapShotSamplingCB=" + snapShotSamplingCB + ", paginationCB=" + paginationCB
				+ ", pagination=" + pagination + ", autoAdjustedYAxisRange=" + autoAdjustedYAxisRange
				+ ", lineCustomColors=" + lineCustomColors + ", pointCustomColors=" + pointCustomColors + ", lineType="
				+ lineType + ", lineColorType=" + lineColorType + ", linecolor=" + linecolor + ", pointcolor="
				+ pointcolor + ", pointType=" + pointType + ", pointColorType=" + pointColorType
				+ ", adaptiveBehaviour=" + adaptiveBehaviour + ", fromSaveSmartenLabelProp=" + fromSaveSmartenLabelProp
				+ ", noOfXAxisBlocks=" + noOfXAxisBlocks + ", noOfYAxisBlocks=" + noOfYAxisBlocks
				+ ", combinedRotateType=" + combinedRotateType + "]";
	}
	
	


	public int getApplyDataoperationwhen() {
		return applyDataoperationwhen;
	}

	public void setApplyDataoperationwhen(int applyDataoperationwhen) {
		this.applyDataoperationwhen = applyDataoperationwhen;
	}

	public boolean isChangeShapeOfBulletDiamond() {
		return changeShapeOfBulletDiamond;
	}

	public void setChangeShapeOfBulletDiamond(boolean changeShapeOfBulletDiamond) {
		this.changeShapeOfBulletDiamond = changeShapeOfBulletDiamond;
	}

}
