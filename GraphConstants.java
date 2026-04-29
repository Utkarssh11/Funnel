/*
 * @(#)GraphConstants.java Version 4.0 <Oct 30, 2014>
 *
 * Copyright 2015 Elegant MicroWeb Technologies Pvt. Ltd. (India). All Rights Reserved. Use is subject to license terms.
 */

package com.elegantjbi.service.graph;

public class GraphConstants {

	/** Specifies default length for legend. */
	public static final int LEGEND_STR_LEN_DEFAULT = -1;
	// GraphExPanel.LABEL_DRILLDOWN_PATH_SEPARATOR
	/*public static final String LABEL_DRILLDOWN_PATH_SEPARATOR = ResourceManager
			.getString(ResourceManager.GRAPH_LABEL, "LABEL_DRILLDOWN_PATH_SEPARATOR");*/
	public static final String LABEL_DRILLDOWN_PATH_SEPARATOR = ">";
	/**
	 * Specifies command name for Legend Top Position.
	 */
	public final static int LEGEND_TOP = 1;
	/**
	 * Specifies command name for Legend Left Position.
	 */
	public final static int LEGEND_LEFT = 2;
	/**
	 * Specifies command name for Legend Rights Position.
	 */
	public final static int LEGEND_RIGHT = 3;
	/**
	 * Specifies command name for Legend Bottom Position.
	 */
	public final static int LEGEND_BOTTOM = 4;
	
	/**
	 * Specifies Default Graph width.
	 */
	public final static int DEFAULT_GRAPH_WIDTH = 780;
	
	/**
	 * Specifies Default Graph Height.
	 */
	public final static int DEFAULT_GRAPH_HEIGHT = 400;
	/**
	 * Specifies Default Graph Rank.
	 */
	public final static int DEFAULT_RANK = 10;
	
	/** Vertical Bar Graph Type */
	public static final int VBAR_GRAPH = 1;
	/** Horizontal Bar Graph Type */
	public static final int HBAR_GRAPH = 2;
	/** Line Graph Type */
	public static final int LINE_GRAPH = 3;
	/** Point Graph Type */
	public static final int POINT_GRAPH = 4;
	/** Strip Graph Type */
	public static final int STRIP_GRAPH = 5;
	/** Pie Graph Type */
	public static final int PIE_GRAPH = 6;
	/** Area Graph Type */
	public static final int AREA_GRAPH = 7;
	/** Radar Graph Type */
	public static final int RADAR_GRAPH = 8;
	/** 3D Area Graph Type */
	public static final int AREA_DEPTH_GRAPH = 9;
	/** 3D Area Graph Type */
	public static final int AREA_STACK_GRAPH = 10;
	/** Stacked Vertical Bar Graph Type */
	public static final int STACKED_VBAR_GRAPH = 11;
	/** Stacked Horizontal Bar Graph Type */
	public static final int STACKED_HBAR_GRAPH = 12;
	/** Pie Graph Type */
	public static final int PIE_SINGLE_GRAPH = 13;
	/** Drilled Radar Graph Type */
	public static final int DRILLED_RADAR_GRAPH = 14;
	/** Drilled Radar Graph Type */
	public static final int DRILLED_STACKED_RADAR_GRAPH = 15;
	public static final int COMBINED_GRAPH = 16;
	public static final int HISTOGRAM_GRAPH = 17;
	/** Scatter Line Graph Type */
	public static final int SCATTER_LINE_GRAPH = 18;
	/** Scatter Point Graph Type */
	public static final int SCATTER_POINT_GRAPH = 19;
	public static final int BUBBLE_GRAPH = 20;
	/** Stacked Line Graph Type */
	public static final int STACKED_LINE_GRAPH = 21;
	/** Stacked Strip Graph Type */
	public static final int STACKED_STRIP_GRAPH = 22;
	/** Stacked Point Graph Type */
	public static final int STACKED_POINT_GRAPH = 23;
	/** Percentage Vertical Bar Graph Type */
	public static final int PERCENTAGE_VBAR_GRAPH = 24;
	/** Percentage Horizontal Bar Graph Type */
	public static final int PERCENTAGE_HBAR_GRAPH = 25;
	/** Percentage Line Graph Type */
	public static final int PERCENTAGE_LINE_GRAPH = 26;
	/** Percentage Strip Graph Type */
	public static final int PERCENTAGE_STRIP_GRAPH = 27;
	/** Percentage Point Graph Type */
	public static final int PERCENTAGE_POINT_GRAPH = 28;

	/** Pyramid Vertical Bar Graph Type */
	public static final int PYRAMID_VBAR_GRAPH = 29;
	/** CYLINDER Vertical Bar Graph Type */
	public static final int CYLINDER_VBAR_GRAPH = 30;
	/** Pyramid HORIZONTAL Bar Graph Type */
	public static final int PYRAMID_HBAR_GRAPH = 31;
	/** CYLINDER HORIZONTAL Bar Graph Type */
	public static final int CYLINDER_HBAR_GRAPH = 32;

	/** Area Percentage Graph Type */
	public static final int AREA_PERCENTAGE_GRAPH = 33;

	/** cone Vertical Bar Graph Type */
	public static final int CONE_VBAR_GRAPH = 34;
	/** cone HORIZONTAL Bar Graph Type */
	public static final int CONE_HBAR_GRAPH = 35;
	/** Stacked Vertical CYLINDER Graph Type */
	public static final int STACKED_CYLINDER_VBAR_GRAPH = 36;
	/** Stacked Vertical CONE Graph Type */
	public static final int STACKED_CONE_VBAR_GRAPH = 37;
	/** Stacked Vertical PYRAMID Graph Type */
	public static final int STACKED_PYRAMID_VBAR_GRAPH = 38;

	/** Stacked Scatter Line Graph Type */
	public static final int STACKED_SCATTER_LINE_GRAPH = 39;
	/** Stacked Scatter Point Graph Type */
	public static final int STACKED_SCATTER_POINT_GRAPH = 40;
	/** Percentage Scatter Line Graph Type */
	public static final int PERCENTAGE_SCATTER_LINE_GRAPH = 41;
	/** Percentage Scatter Line Graph Type */
	public static final int PERCENTAGE_SCATTER_POINT_GRAPH = 42;
	//
	/** Stacked horizontal CYLINDER Graph Type */
	public static final int STACKED_CYLINDER_HBAR_GRAPH = 43;
	/** Stacked horizontal CONE Graph Type */
	public static final int STACKED_CONE_HBAR_GRAPH = 44;
	/** Stacked horizontal PYRAMID Graph Type */
	public static final int STACKED_PYRAMID_HBAR_GRAPH = 45;
	/** PERCENTAGE Vertical CYLINDER Graph Type */
	public static final int PERCENTAGE_CYLINDER_VBAR_GRAPH = 46;
	/** PERCENTAGE Vertical CONE Graph Type */
	public static final int PERCENTAGE_CONE_VBAR_GRAPH = 47;
	/** PERCENTAGE Vertical PYRAMID Graph Type */
	public static final int PERCENTAGE_PYRAMID_VBAR_GRAPH = 48;
	/** PERCENTAGE Horizontal CYLINDER Graph Type */
	public static final int PERCENTAGE_CYLINDER_HBAR_GRAPH = 49;
	/** PERCENTAGE Horizontal CONE Graph Type */
	public static final int PERCENTAGE_CONE_HBAR_GRAPH = 50;
	/** PERCENTAGE Horizontal PYRAMID Graph Type */
	public static final int PERCENTAGE_PYRAMID_HBAR_GRAPH = 51;

	public static final int CANDLE_STICK_GRAPH = 52;
	public static final int HIGH_LOW_OPEN_CLOSE_GRAPH = 53;
	//
	public static final int DOUGHNUT_GRAPH = 54;
	public static final int NUMERIC_DIAL_GAUGE = 55;
	public static final int LEVEL_GAUGE = 56;
	public static final int THERMOMETER_GAUGE = 57;

	/**
	 * Specifies HeatMap's Graph ID
	 */
	public static final int HEAT_MAP_GRAPH = 58;
	
	/**
	 * Specifies D3_TREEMAP Graph ID
	 */
	public static final int D3_TREEMAP = 61;/**
	 * Specifies D3_SUNBURST Graph ID
	 */
	public static final int D3_SUNBURST = 62;/**
	 * Specifies D3_BUBBLE Graph ID
	 */
	public static final int D3_BUBBLE = 63;
	/**
	 * Specifies D3_CHORD
	 */
	public static final int D3_CHORD = 64;
	/**
	 * Specifies D3_TREELAYOUT
	 */
	public static final int D3_TREELAYOUT = 65;
	/**
	 * Specifies SMARTENVIEW_TABULAR
	 */
	public static final int SMARTENVIEW_TABULAR = 70;
	/**
	 * Specifies SMARTENVIEW_MAP
	 */
	public static final int SMARTENVIEW_MAP = 71;
	
	/**
	 * Specifies Funnel graph Type
	 */
	public static final int FUNNEL_GRAPH = 72;
	
	/** Specifies the type of Bar */
	/** Bar Type */
	public static final int TYPE_BAR = 0;
	/** Pyramid Type */
	public static final int TYPE_PYRAMID = 1;
	/** Cylinder Type */
	public static final int TYPE_CYLINDER = 2;
	/** Cone Type */
	public static final int TYPE_CONE = 3;
	

	/**
	 * Specifies Default Nested Graph Rank.
	 */
	public final static int DEFAULT_NESTED_RANK = 15;
	/**
	 * Specifies Default PIE Graph Rank.
	 */
	public final static int DEFAULT_PIE_RANK = 15;
	/*define for tab id specification in graph property*/
	
	public final static String GPD_GRAPH_GENERAL = "graph-general";
	public final static String GPD_GRAPH_TITLE = "graph-title";
	public final static String GPD_GRAPH_AREA = "graph-area";
	public final static String GPD_GRAPH_PIE_TITLE = "graph-pie-value";
	public final static String GPD_GRAPH_DOUGHNUT_TITLE = "graph-doughnut-title";
	public final static String GPD_GRAPH_RADAR_AXIS = "radar-axis";
	public final static String GPD_GRAPH_RADAR_SCALE = "radar-scale";
	public final static String GPD_GRAPH_X_AXIS = "x-graph-axis";
	public final static String GPD_GRAPH_Y_AXIS = "y-graph-axis";
	public final static String GPD_GRAPH_Y_LINE_BAR_AXIS = "y-graph-linebaraxis";
	public final static String GPD_GRAPH_STOCK_CONFIG = "stock-config";
	public final static String GPD_GRAPH_GAUGE_TITLE_PROP = "gauge-title-prop";
	public final static String GPD_GRAPH_GAUGE_SCALE_PROP = "gauge-scale-prop";
	public final static String GPD_GRAPH_GAUGE_THERMOMETER_PROP = "gauge-thermometer-prop";
	public final static String GPD_GRAPH_GAUGE_NEEDLE_PROP = "gauge-needle-prop";
	public final static String GPD_GRAPH_GAUGE_DIAL_PROP = "gauge-dial-prop";
	public final static String GPD_GRAPH_GAUGE_LEGEND_PROP = "gauge-legend-prop";
	public final static String GPD_GRAPH_LEGEND = "graph-legend";
	public final static String GPD_GRAPH_LEVEL_PROP = "graph-level-gauge-prop";
	public final static String GPD_GRAPH_DATAVALUE_PROP = "gauge-datavalue-prop";
	public final static String GPD_GRAPH_BUBBLE = "bubble-graph";
	public final static String GPD_GRAPH_COMB_CONFIG = "combinedgraph-configuration";
	public final static String GPD_GRAPH_COMB_LEGEND = "graph-combined-legend";
	public final static String GPD_GRAPH_RADAR = "radar-graph";
	public final static String GPD_GRAPH_DOUGHNUT = "doughnut-graph";
	public final static String GPD_GRAPH_PIE = "graph-Pie";
	public final static String GPD_GRAPH_VERTICAL_BAR = "graph-vertical-bar";
	public final static String GPD_GRAPH_LINE_PROP = "graph-line-prop";
	public final static String GPD_GRAPH_AREA_PROP = "area-graph-prop";
	public final static String GPD_GRAPH_CANDAL_STICK = "candle-stick";
	public final static String GPD_GRAPH_HISTOGRAM = "histogram";
	public final static String GPD_GRAPH_HEAT_MAP = "heatmap";
	public final static String GPD_GRAPH_DATA_VALUE = "graph-data-value";
	public final static String GPD_GRAPH_DATA_BAR_LINE_VALUE = "graph-data-barlinevalue";
	public final static String GPD_GRAPH_COMBINED_REFERENCE_LINE = "combined-reference-line";
	public final static String GPD_GRAPH_COMBINED_TREND_LINE = "combined-trend-line";
	public final static String GPD_GRAPH_REFERENCE_LINE = "graph-reference-line";
	public final static String GPD_GRAPH_TREND_LINE = "graph-trend-line";
	public final static String GPD_BREADCRUM = "graph-BreadCrum";
	public final static String GPD_COLUMN_LABELS = "graph-column-labels";
	public final static String GPD_GRAPH_CHART_CURSOR = "graph-chart-cursor";
	public final static String GPD_GRAPH_CHART_SCROLLBAR = "graph-chart-scrollbar";
	public final static String GPD_GRAPH_CHART_SELECTION = "graph-value-selection";
	public final static String GPD_SMARTENVIEW_MAP = "smartenview-map-area";
	public final static String GPD_SMARTENVIEW_MAP_MARKER = "smartenview-map-marker";
	public final static String GPD_SUNBURST_RADIUS = "graph-sunburst-radius";
	
	public static final int NEEDLECAPPOSITION_FRONT = 0;
	
	/** None Computation Type */
	public static final int NONE_COMPUTATION = 0;
	/** Sum Computation Type */
	public static final int SUM_COMPUTATION = 1;
	/** Count Computation Type */
	public static final int COUNT_COMPUTATION = 2;
	/** Valid Count Computation Type */
	public static final int VALID_COUNT_COMPUTATION = 3;
	/** Average Computation Type */
	public static final int AVERAGE_COMPUTATION = 4;
	/** Valid Averrage Computation Type */
	public static final int VALID_AVERAGE_COMPUTATION = 5;
	/** Maximum Computation Type */
	public static final int MAXIMUM_COMPUTATION = 6;
	/** Minimum Computation Type */
	public static final int MINIMUM_COMPUTATION = 7;

	//
	// Macro String constants
	//
	/* Specify the Macro strings for Series Name */
	public final static String SERIES_NAME = "_%SERIES_NAME";
	/* Specify the Macro strings for Series Index */
	public final static String SERIES_INDEX = "_%SERIES_INDEX";
	/* Specify the Macro strings for Item Name */
	public final static String ITEM_NAME = "_%ITEM_NAME";
	/* Specify the Macro strings for Item index */
	public final static String ITEM_INDEX = "_%ITEM_INDEX";
	/* Specify the Macro strings for Data Value */
	public final static String DATA_VALUE = "_%DATA_VALUE";
	/* Specify the Macro strings for Size Value */
	public final static String SIZE_VALUE = "_%SIZE_VALUE";
	// Added by Arpit on 10-10-06 12:36PM
	/* Specify the Macro strings for Percentage Value */
	public final static String PERCENT_VALUE = "_%PERCENT_VALUE";
	/* Specify the Macro strings for Stacked Value */
	public final static String STACKED_VALUE = "_%STACKED_VALUE";
	//
	// Compass-direction constants used to specify a position.
	//
	/** Specify Compass-direction North (up). */
	public static final int NORTH = 1;
	/** Specify Compass-direction north-east (upper right). */
	public static final int NORTH_EAST = 2;
	/** Specify Compass-direction east (right). */
	public static final int EAST = 3;
	/** Specify Compass-direction south-east (lower right). */
	public static final int SOUTH_EAST = 4;
	/** Specify Compass-direction south (down). */
	public static final int SOUTH = 5;
	/** Specify Compass-direction south-west (lower left). */
	public static final int SOUTH_WEST = 6;
	/** Specify Compass-direction west (left). */
	public static final int WEST = 7;
	/** Specify Compass-direction north west (upper left). */
	public static final int NORTH_WEST = 8;

	//
	// constants used to specify locations in a box.
	//
	/** Specify the center of a box */
	public static final int CENTER = 0x01;
	/** Specify the left of a box */
	public static final int LEFT = 0x02;
	/** Specify the right of a box */
	public static final int RIGHT = 0x04;
	/** Specify the top of a box */
	public static final int TOP = 0x08;
	/** Specify the bottom of a box */
	public static final int BOTTOM = 0x10;

	//
	// These constants specify a horizontal or vertical orientation
	//
	/** Horizontal orientation. */
	public static final int HORIZONTAL = 0;
	/** Vertical orientation. */
	public static final int VERTICAL = 1;

	//
	// These constants specify a Position of Tick line
	//
	/** Specifies Constant Tick Position */
	public static final int OUTSIDE = 1;
	/** Specifies Constant Tick Position */
	public static final int INSIDE = 2;
	/** Specifies Constant Tick Position */
	public static final int CROSS = 3;

	//
	// These constants specify Position
	//
	/** Specifies Front postion */
	public static final int FRONT = 1;
	/** Specifies Middle postion */
	public static final int MIDDLE = 2;
	/** Specifies Back postion */
	public static final int BACK = 3;

	/* specify the type of x-label */
	public static final int TYPE_NUMBER = 21;
	public static final int TYPE_DATE = 22;
	public static final int TYPE_TIME = 23;
	public static final int TYPE_TIMESTAMP = 24;
	public static final int TYPE_OTHER = 25;

	/* Background Image Display type: Stretched */
	public static final int BACKGROUND_IMAGE_DISPLAY_STRETCH = 0;
	/* Background Image Display type: Tiled */
	public static final int BACKGROUND_IMAGE_DISPLAY_TILE = 1;
	/* Background Image Display type: Center */
	public static final int BACKGROUND_IMAGE_DISPLAY_CENTER = 2;

	/* Background Image Fliped Type : NOT FLIPED */
	public static final int BACKGROUND_IMAGE_FLIP_NONE = -1;
	/* Background Image Fliped Type FLIPED Horizontally */
	public static final int BACKGROUND_IMAGE_FLIP_HORIZONTAL = 0;
	/* Background Image Fliped Type FLIPED Vertically */
	public static final int BACKGROUND_IMAGE_FLIP_VERTICAL = 1;
	/* Background Image Fliped Type FLIPED both Horizontally and Vertically */
	public static final int BACKGROUND_IMAGE_FLIP_BOTH = 2;
	/* Templet file extension */
	public static final String GRAPH_FILE_EXT = ".grf";
	/* Templet file extension */
	public static final String GRAPH_FILE_TEMPLET = ".ejxml";

	// Added By Nikhil Patel
	/* These constants specify font variant for fonts */
	
	/* NORMAL CASE LETTERS Specify the fonts entered by user itself */
	public static final int NORMALCASELETTERS = 0;

	// Added By Nikhil Patel
	/* UPPER CASE LETTERS Specify the fonts to be capitalized */
	public static final int UPPERCASELETTERS = 1;
	
	// Added By Nikhil Patel
	/* LOWER CASE LETTERS Specify the fonts to be in small caps */
	public static final int LOWERCASELETTERS = 2;
	
	// Added By Nikhil Patel
	/* FIRST UPPER CASE LETTERS Specify the fonts to be First Letter Capitalized for each word */
	public static final int FIRSTUPPERCASELETTERS = 3;
	
	// Added By Nikhil Patel
	/** Specify the normal position of trendline equation */
	public static final int ONTHELINE = 3 ;
	
	// Added By Nikhil Patel
	/* Specifies Radar Y Axis Lines To Be Square */
	public static final int SQUARE = 0;
	
	// Added By Nikhil Patel
	/* Specifies Radar Y Axis Lines To Be Round */
	public static final int ROUND = 1;
	
}
