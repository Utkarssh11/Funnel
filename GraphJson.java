package com.elegantjbi.amcharts.vo;

import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.annotate.JsonSerialize;

public class GraphJson {
	
	private String type;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean sortColumns;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean bringToFront;
	private String theme;
	private String startEffect;
	private boolean sequencedAnimation;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String pathToImages;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String chartType;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String objectType;//Type can be of smartenview or graph[Bug 13842]

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List colors;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean addClassNames;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Titles> titles;
	private List<Map<String,Object>> dataProvider;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private Map<String,Object> export;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List <AllLabels> allLabels;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<ValueAxis> valueAxis;
	
	private double startDuration;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Graphs> graphs;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String categoryField;
	private double marginTop;
	private double marginBottom;
	private double marginLeft;
	private double marginRight;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private CategoryAxis categoryAxis;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<ValueAxes> valueAxes;
	private boolean mouseWheelZoomEnabled;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private Balloon balloon;
	//responsive
	private Responsive responsive;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int angle;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int depth3D;
	
	//rotate for horizontal
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean rotate;
	
	//chartScrollbar
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private ChartScrollbar chartScrollbar;
	
	//valueScrollBar
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private ValueScrollbar valueScrollbar;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String balloonText;
	
	private double columnSpacing;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String innerRadius;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int startAngle;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int lineAlpha;

	private int pullOutRadius;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String pulledField;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String colorField;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String labelColor;
	private int labelRadius;
	
	private int labelTickAlpha;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String labelText;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String titleField;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String valueField;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String dataValueMouseOverColor;
	//Guides
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Guides> guides;
		
	//mouse pointer
	private boolean showHandOnHover;
	
	private double precision;
	
	private boolean usePrefixes;

	//pie graph and Doughnut graph
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String color;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private ChartCursor chartCursor;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Axes> axes;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Arrows> arrows;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean typeTwo;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String chartname;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int radiusIncrement;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int outlineAlpha;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String outlineColor;
	private int outlineThickness;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String radius;

	//pie graph and Doughnut graph
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private double alpha;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean pullOutOnlyOne;

	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String[] colLabel;
	
	// setting scrollBar via amchartEvents
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int scrollHeight;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean scrollBar;
	
	//pie and doughnut data value maxwidth
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int maxLabelWidth;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private double autoMarginOffset;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean gridAboveGraphs;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int columnWidth;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List valueAxisName;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int heatMapDigitsAfterDecimal;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String heatMapDataValueSuffix;
	
	private String zoomOutText=" ";
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String zoomType;
	private String labelTickColor;
	
	private boolean colIsMeasure;//Bubble
	private boolean uddcInCol;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String descriptionField;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Map<String, Object>> prefixesOfBigNumbers;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int plotAreaBorderAlpha;
	//private int borderAlpha;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	//private String borderColor;
	private String plotAreaBorderColor;
	
	
	private boolean pieLegendDiv;
	
	private String thousandsSeparator;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List<Integer> pieMultipleMeasureIdArr;

	private boolean multipleMeasure;
	private boolean multipleMeasureLine;
	
	private boolean valueAxisPositionEnabled;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean splitGraphEnabled;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean autoMargins;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private List sizeValueIndex;
	private boolean gaugeTitleVisible;
	
	private boolean gaugeActualDataVisible;
	
	private boolean gaugeTargetDataVisible;
	
	private boolean pieAnimationEnable;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String labelFunction;
	
	private int percentPrecision;
	
@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean showZeroSlices;@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private int valueAxesCounter;//Added for checking Multiple Y-axis //New Rows/Cols approach
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean ascendingSortEnable;//Added for Bug #14832
	
	//to ShowAllValues
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private boolean ShowAllValues;
	
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String plotAreaFillColors;
	@JsonSerialize(include=JsonSerialize.Inclusion.NON_DEFAULT)
	private String plotAreaFillAlphas;
	
	public String getPlotAreaFillColors() {
		return plotAreaFillColors;
	}

	public void setPlotAreaFillColors(String plotAreaFillColors) {
		this.plotAreaFillColors = plotAreaFillColors;
	}

	public String getPlotAreaFillAlphas() {
		return plotAreaFillAlphas;
	}

	public void setPlotAreaFillAlphas(String plotAreaFillAlphas) {
		this.plotAreaFillAlphas = plotAreaFillAlphas;
	}

	public List getSizeValueIndex() {
		return sizeValueIndex;
	}
	
	public void setSizeValueIndex(List sizeValueIndex) {
		this.sizeValueIndex = sizeValueIndex;
	}	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isBringToFront() {
		return bringToFront;
	}

	public void setBringToFront(boolean bringToFront) {
		this.bringToFront = bringToFront;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public String getStartEffect() {
		return startEffect;
	}

	public void setStartEffect(String startEffect) {
		this.startEffect = startEffect;
	}

	public boolean isSequencedAnimation() {
		return sequencedAnimation;
	}

	public void setSequencedAnimation(boolean sequencedAnimation) {
		this.sequencedAnimation = sequencedAnimation;
	}

	public String getPathToImages() {
		return pathToImages;
	}

	public void setPathToImages(String pathToImages) {
		this.pathToImages = pathToImages;
	}

	public String getChartType() {
		return chartType;
	}

	public void setChartType(String chartType) {
		this.chartType = chartType;
	}
	
	public String getObjectType() {
		return objectType;
	}

	public void setObjectType(String objectType) {
		this.objectType = objectType;
	}

	public List getColors() {
		return colors;
	}

	public void setColors(List colors) {
		this.colors = colors;
	}

	public boolean isAddClassNames() {
		return addClassNames;
	}

	public void setAddClassNames(boolean addClassNames) {
		this.addClassNames = addClassNames;
	}

	public List<Titles> getTitles() {
		return titles;
	}

	public void setTitles(List<Titles> titles) {
		this.titles = titles;
	}

	public List<Map<String, Object>> getDataProvider() {
		return dataProvider;
	}

	public void setDataProvider(List<Map<String, Object>> dataProvider) {
		this.dataProvider = dataProvider;
	}

	public Map<String, Object> getExport() {
		return export;
	}

	public void setExport(Map<String, Object> export) {
		this.export = export;
	}

	public List<AllLabels> getAllLabels() {
		return allLabels;
	}

	public void setAllLabels(List<AllLabels> allLabels) {
		this.allLabels = allLabels;
	}

	public List<ValueAxis> getValueAxis() {
		return valueAxis;
	}

	public void setValueAxis(List<ValueAxis> valueAxis) {
		this.valueAxis = valueAxis;
	}

	public double getStartDuration() {
		return startDuration;
	}

	public void setStartDuration(double startDuration) {
		this.startDuration = startDuration;
	}

	public List<Graphs> getGraphs() {
		return graphs;
	}

	public void setGraphs(List<Graphs> graphs) {
		this.graphs = graphs;
	}

	public String getCategoryField() {
		return categoryField;
	}

	public void setCategoryField(String categoryField) {
		this.categoryField = categoryField;
	}

	public double getMarginTop() {
		return marginTop;
	}

	public void setMarginTop(double marginTop) {
		this.marginTop = marginTop;
	}

	public double getMarginBottom() {
		return marginBottom;
	}

	public void setMarginBottom(double marginBottom) {
		this.marginBottom = marginBottom;
	}

	public double getMarginLeft() {
		return marginLeft;
	}

	public void setMarginLeft(double marginLeft) {
		this.marginLeft = marginLeft;
	}

	public double getMarginRight() {
		return marginRight;
	}

	public void setMarginRight(double marginRight) {
		this.marginRight = marginRight;
	}

	public CategoryAxis getCategoryAxis() {
		return categoryAxis;
	}

	public void setCategoryAxis(CategoryAxis categoryAxis) {
		this.categoryAxis = categoryAxis;
	}

	public List<ValueAxes> getValueAxes() {
		return valueAxes;
	}

	public void setValueAxes(List<ValueAxes> valueAxes) {
		this.valueAxes = valueAxes;
	}

	public boolean isMouseWheelZoomEnabled() {
		return mouseWheelZoomEnabled;
	}

	public void setMouseWheelZoomEnabled(boolean mouseWheelZoomEnabled) {
		this.mouseWheelZoomEnabled = mouseWheelZoomEnabled;
	}

	public Balloon getBalloon() {
		return balloon;
	}

	public void setBalloon(Balloon balloon) {
		this.balloon = balloon;
	}

	public Responsive getResponsive() {
		return responsive;
	}

	public void setResponsive(Responsive responsive) {
		this.responsive = responsive;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	public int getDepth3D() {
		return depth3D;
	}

	public void setDepth3D(int depth3d) {
		depth3D = depth3d;
	}

	public boolean isRotate() {
		return rotate;
	}

	public void setRotate(boolean rotate) {
		this.rotate = rotate;
	}

	public ChartScrollbar getChartScrollbar() {
		return chartScrollbar;
	}

	public void setChartScrollbar(ChartScrollbar chartScrollbar) {
		this.chartScrollbar = chartScrollbar;
	}

	public ValueScrollbar getValueScrollbar() {
		return valueScrollbar;
	}

	public void setValueScrollbar(ValueScrollbar valueScrollbar) {
		this.valueScrollbar = valueScrollbar;
	}

	public String getBalloonText() {
		return balloonText;
	}

	public void setBalloonText(String balloonText) {
		this.balloonText = balloonText;
	}

	public double getColumnSpacing() {
		return columnSpacing;
	}

	public void setColumnSpacing(double columnSpacing) {
		this.columnSpacing = columnSpacing;
	}

	public String getInnerRadius() {
		return innerRadius;
	}

	public void setInnerRadius(String innerRadius) {
		this.innerRadius = innerRadius;
	}

	public int getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(int startAngle) {
		this.startAngle = startAngle;
	}

	public int getLineAlpha() {
		return lineAlpha;
	}

	public void setLineAlpha(int lineAlpha) {
		this.lineAlpha = lineAlpha;
	}

	public int getPullOutRadius() {
		return pullOutRadius;
	}

	public void setPullOutRadius(int pullOutRadius) {
		this.pullOutRadius = pullOutRadius;
	}

	public String getPulledField() {
		return pulledField;
	}

	public void setPulledField(String pulledField) {
		this.pulledField = pulledField;
	}

	public String getColorField() {
		return colorField;
	}

	public void setColorField(String colorField) {
		this.colorField = colorField;
	}

	public String getLabelColor() {
		return labelColor;
	}

	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
	}

	public int getLabelRadius() {
		return labelRadius;
	}

	public void setLabelRadius(int labelRadius) {
		this.labelRadius = labelRadius;
	}

	public int getLabelTickAlpha() {
		return labelTickAlpha;
	}

	public void setLabelTickAlpha(int labelTickAlpha) {
		this.labelTickAlpha = labelTickAlpha;
	}

	public String getLabelText() {
		return labelText;
	}

	public void setLabelText(String labelText) {
		this.labelText = labelText;
	}

	public String getTitleField() {
		return titleField;
	}

	public void setTitleField(String titleField) {
		this.titleField = titleField;
	}

	public String getValueField() {
		return valueField;
	}

	public void setValueField(String valueField) {
		this.valueField = valueField;
	}

	public String getDataValueMouseOverColor() {
		return dataValueMouseOverColor;
	}

	public void setDataValueMouseOverColor(String dataValueMouseOverColor) {
		this.dataValueMouseOverColor = dataValueMouseOverColor;
	}

	public List<Guides> getGuides() {
		return guides;
	}

	public void setGuides(List<Guides> guides) {
		this.guides = guides;
	}

	public boolean isShowHandOnHover() {
		return showHandOnHover;
	}

	public void setShowHandOnHover(boolean showHandOnHover) {
		this.showHandOnHover = showHandOnHover;
	}

	public double getPrecision() {
		return precision;
	}

	public void setPrecision(double precision) {
		this.precision = precision;
	}

	public boolean isUsePrefixes() {
		return usePrefixes;
	}

	public void setUsePrefixes(boolean usePrefixes) {
		this.usePrefixes = usePrefixes;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public ChartCursor getChartCursor() {
		return chartCursor;
	}

	public void setChartCursor(ChartCursor chartCursor) {
		this.chartCursor = chartCursor;
	}

	public List<Axes> getAxes() {
		return axes;
	}

	public void setAxes(List<Axes> axes) {
		this.axes = axes;
	}

	public List<Arrows> getArrows() {
		return arrows;
	}

	public void setArrows(List<Arrows> arrows) {
		this.arrows = arrows;
	}

	public boolean isTypeTwo() {
		return typeTwo;
	}

	public void setTypeTwo(boolean typeTwo) {
		this.typeTwo = typeTwo;
	}

	public String getChartname() {
		return chartname;
	}

	public void setChartname(String chartname) {
		this.chartname = chartname;
	}

	public int getRadiusIncrement() {
		return radiusIncrement;
	}

	public void setRadiusIncrement(int radiusIncrement) {
		this.radiusIncrement = radiusIncrement;
	}

	public int getOutlineAlpha() {
		return outlineAlpha;
	}

	public void setOutlineAlpha(int outlineAlpha) {
		this.outlineAlpha = outlineAlpha;
	}

	public String getOutlineColor() {
		return outlineColor;
	}

	public void setOutlineColor(String outlineColor) {
		this.outlineColor = outlineColor;
	}

	public int getOutlineThickness() {
		return outlineThickness;
	}

	public void setOutlineThickness(int outlineThickness) {
		this.outlineThickness = outlineThickness;
	}

	public String getRadius() {
		return radius;
	}

	public void setRadius(String radius) {
		this.radius = radius;
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public boolean isPullOutOnlyOne() {
		return pullOutOnlyOne;
	}

	public void setPullOutOnlyOne(boolean pullOutOnlyOne) {
		this.pullOutOnlyOne = pullOutOnlyOne;
	}
	public String[] getColLabel() {
		return colLabel;
	}

	public void setColLabel(String[] colLabel) {
		this.colLabel = colLabel;
	}

	public int getScrollHeight() {
		return scrollHeight;
	}

	public void setScrollHeight(int scrollHeight) {
		this.scrollHeight = scrollHeight;
	}

	public boolean isScrollBar() {
		return scrollBar;
	}

	public void setScrollBar(boolean scrollBar) {
		this.scrollBar = scrollBar;
	}

	public int getMaxLabelWidth() {
		return maxLabelWidth;
	}

	public void setMaxLabelWidth(int maxLabelWidth) {
		this.maxLabelWidth = maxLabelWidth;
	}

	public double getAutoMarginOffset() {
		return autoMarginOffset;
	}

	public void setAutoMarginOffset(double autoMarginOffset) {
		this.autoMarginOffset = autoMarginOffset;
	}

	public boolean isGridAboveGraphs() {
		return gridAboveGraphs;
	}

	public void setGridAboveGraphs(boolean gridAboveGraphs) {
		this.gridAboveGraphs = gridAboveGraphs;
	}

	public int getColumnWidth() {
		return columnWidth;
	}

	public void setColumnWidth(int columnWidth) {
		this.columnWidth = columnWidth;
	}

	public List getValueAxisName() {
		return valueAxisName;
	}

	public void setValueAxisName(List valueAxisName) {
		this.valueAxisName = valueAxisName;
	}
	
	public int getHeatMapDigitsAfterDecimal() {
		return heatMapDigitsAfterDecimal;
	}
	public void setHeatMapDigitsAfterDecimal(int heatMapDigitsAfterDecimal) {
		this.heatMapDigitsAfterDecimal = heatMapDigitsAfterDecimal;
	}

	public String getHeatMapDataValueSuffix() {
		return heatMapDataValueSuffix;
	}

	public void setHeatMapDataValueSuffix(String heatMapDataValueSuffix) {
		this.heatMapDataValueSuffix = heatMapDataValueSuffix;
	}

	public String getZoomOutText() {
		return zoomOutText;
	}

	public void setZoomOutText(String zoomOutText) {
		this.zoomOutText = zoomOutText;
	}

	public String getZoomType() {
		return zoomType;
	}

	public void setZoomType(String zoomType) {
		this.zoomType = zoomType;
	}

	public String getLabelTickColor() {
		return labelTickColor;
	}

	public void setLabelTickColor(String labelTickColor) {
		this.labelTickColor = labelTickColor;
	}

	public boolean isColIsMeasure() {
		return colIsMeasure;
	}

	public void setColIsMeasure(boolean colIsMeasure) {
		this.colIsMeasure = colIsMeasure;
	}

public String getDescriptionField() {
		return descriptionField;
	}

	public void setDescriptionField(String descriptionField) {
		this.descriptionField = descriptionField;
	}

	public boolean isUddcInCol() {
		return uddcInCol;
	}

	public void setUddcInCol(boolean uddcInCol) {
		this.uddcInCol = uddcInCol;
	}

	public List<Map<String, Object>> getPrefixesOfBigNumbers() {
		return prefixesOfBigNumbers;
	}

	public void setPrefixesOfBigNumbers(
			List<Map<String, Object>> prefixesOfBigNumbers) {
		this.prefixesOfBigNumbers = prefixesOfBigNumbers;
	}

	public boolean isPieLegendDiv() {
		return pieLegendDiv;
	}

	public void setPieLegendDiv(boolean pieLegendDiv) {
		this.pieLegendDiv = pieLegendDiv;
	}

	public String getThousandsSeparator() {
		return thousandsSeparator;
	}

	public void setThousandsSeparator(String thousandsSeparator) {
		this.thousandsSeparator = thousandsSeparator;
	}

	public List<Integer> getPieMultipleMeasureIdArr() {
		return pieMultipleMeasureIdArr;
	}

	public void setPieMultipleMeasureIdArr(List<Integer> pieMultipleMeasureIdArr) {
		this.pieMultipleMeasureIdArr = pieMultipleMeasureIdArr;
	}

	public boolean isMultipleMeasure() {
		return multipleMeasure;
	}

	public void setMultipleMeasure(boolean multipleMeasure) {
		this.multipleMeasure = multipleMeasure;
	}

	public boolean isMultipleMeasureLine() {
		return multipleMeasureLine;
	}

	public void setMultipleMeasureLine(boolean multipleMeasureLine) {
		this.multipleMeasureLine = multipleMeasureLine;
	}
	
	public int getPlotAreaBorderAlpha() {
		return plotAreaBorderAlpha;
	}

	public void setPlotAreaBorderAlpha(int plotAreaBorderAlpha) {
		this.plotAreaBorderAlpha = plotAreaBorderAlpha;
	}

	public String getPlotAreaBorderColor() {
		return plotAreaBorderColor;
	}

	public void setPlotAreaBorderColor(String plotAreaBorderColor) {
		this.plotAreaBorderColor = plotAreaBorderColor;
	}

	public boolean isValueAxisPositionEnabled() {
		return valueAxisPositionEnabled;
	}

	public void setValueAxisPositionEnabled(boolean valueAxisPositionEnabled) {
		this.valueAxisPositionEnabled = valueAxisPositionEnabled;
	}

	public boolean isSplitGraphEnabled() {
		return splitGraphEnabled;
	}

	public void setSplitGraphEnabled(boolean splitGraphEnabled) {
		this.splitGraphEnabled = splitGraphEnabled;
	}

	public boolean isAutoMargins() {
		return autoMargins;
	}

	public void setAutoMargins(boolean autoMargins) {
		this.autoMargins = autoMargins;
	}
	
	public boolean isGaugeTitleVisible() {
		return gaugeTitleVisible;
	}

	public void setGaugeTitleVisible(boolean gaugeTitleVisible) {
		this.gaugeTitleVisible = gaugeTitleVisible;
	}

	public boolean isGaugeActualDataVisible() {
		return gaugeActualDataVisible;
	}

	public void setGaugeActualDataVisible(boolean gaugeActualDataVisible) {
		this.gaugeActualDataVisible = gaugeActualDataVisible;
	}

	public boolean isGaugeTargetDataVisible() {
		return gaugeTargetDataVisible;
	}

	public void setGaugeTargetDataVisible(boolean gaugeTargetDataVisible) {
		this.gaugeTargetDataVisible = gaugeTargetDataVisible;
	}

	public boolean isPieAnimationEnable() {
		return pieAnimationEnable;
	}

	public void setPieAnimationEnable(boolean pieAnimationEnable) {
		this.pieAnimationEnable = pieAnimationEnable;
	}

	public String getLabelFunction() {
		return labelFunction;
	}

	public void setLabelFunction(String labelFunction) {
		this.labelFunction = labelFunction;
	}

	public int getPercentPrecision() {
		return percentPrecision;
	}

	public void setPercentPrecision(int percentPrecision) {
		this.percentPrecision = percentPrecision;
	}
public boolean isSortColumns() {
		return sortColumns;
	}

	public void setSortColumns(boolean sortColumns) {
		this.sortColumns = sortColumns;
	}public boolean isShowZeroSlices() {
		return showZeroSlices;
	}

	public void setShowZeroSlices(boolean showZeroSlices) {
		this.showZeroSlices = showZeroSlices;
	}public int getValueAxesCounter() {
		return valueAxesCounter;
	}

	public void setValueAxesCounter(int valueAxesCounter) {
		this.valueAxesCounter = valueAxesCounter;
	}

	public boolean isAscendingSortEnable() {
		return ascendingSortEnable;
	}

	public void setAscendingSortEnable(boolean ascendingSortEnable) {
		this.ascendingSortEnable = ascendingSortEnable;
	}

	public boolean isShowAllValues() {
		return ShowAllValues;
	}

	public void setShowAllValues(boolean showAllValues) {
		ShowAllValues = showAllValues;
	}

	

	
	
}