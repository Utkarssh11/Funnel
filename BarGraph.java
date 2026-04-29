package com.elegantjbi.amcharts;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.parser.Parser;

import com.elegantjbi.amcharts.vo.Balloon;
import com.elegantjbi.amcharts.vo.CategoryAxis;
import com.elegantjbi.amcharts.vo.ChartCursor;
import com.elegantjbi.amcharts.vo.GraphJson;
import com.elegantjbi.amcharts.vo.GraphLegendJson;
import com.elegantjbi.amcharts.vo.Graphs;
import com.elegantjbi.amcharts.vo.Guides;
import com.elegantjbi.amcharts.vo.Legend;
import com.elegantjbi.amcharts.vo.Responsive;
import com.elegantjbi.amcharts.vo.ValueAxes;
import com.elegantjbi.amcharts.vo.ValueAxis;
import com.elegantjbi.amcharts.vo.ValueScrollbar;
import com.elegantjbi.core.olap.CubeLabelInfo;
import com.elegantjbi.core.olap.CubeRankDataLabel;
import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.service.kpi.KPIConstants;
import com.elegantjbi.util.AppConstants;
import com.elegantjbi.util.GeneralFiltersUtil;
import com.elegantjbi.util.GraphsUtil;
import com.elegantjbi.util.StringUtil;
import com.elegantjbi.util.logger.ApplicationLog;
import com.elegantjbi.vo.properties.graph.ReferenceLine;

public class BarGraph {

	private BarGraph() {}
	public static final String LEGEND_LABEL = "Legend";
	public static String amJson(GraphInfo graphInfo,boolean isContextFilter) 
	{
		ObjectMapper objectMapper = new ObjectMapper();
		List colorWiseIndex = new ArrayList<>();
		GraphJson graphJson = null;
		
		List jsonList = new ArrayList();
		
		List<String> bulletList = new ArrayList<>();
		List<Integer> bulletSizeList = new ArrayList<>();
		List<Integer> lineStyleList = new ArrayList<>();
		List<Integer> lineThicknessList = new ArrayList<>();
		List<String> borderColorList = new ArrayList<>();
		List<Integer> borderWidthList = new ArrayList<>();
		List<Integer> bulletStyleList = new ArrayList<>();		
		
		String[] barColor =new String[]{"#67b7dc","#6794dc","#6771dc","#8067dc","#a367dc","#c767dc","#dc67ce","#dc67ab","#dc6788","#dc6967",
			    "#dc8c67","#dcaf67","#dcd267","#c3dc67","#a0dc67","#7ddc67","#67dc75","#67dc98","#67dcbb","#67dadc",
			    "#80d0f5","#80adf5","#808af5","#9980f5","#bc80f5","#e080f5","#f580e7", "#f7d584", "#b1fb83", "#50407f", 
			    "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c", "#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296",
			    "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424",
			    "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92",
			    "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		
		String[] bulletColor =new String[]{"#67b7dc","#6794dc","#6771dc","#8067dc","#a367dc","#c767dc","#dc67ce","#dc67ab","#dc6788","#dc6967",
			    "#dc8c67","#dcaf67","#dcd267","#c3dc67","#a0dc67","#7ddc67","#67dc75","#67dc98","#67dcbb","#67dadc",
			    "#80d0f5","#80adf5","#808af5","#9980f5","#bc80f5","#e080f5","#f580e7", "#f7d584", "#b1fb83", "#50407f", 
			    "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c", "#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296",
			    "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424",
			    "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92",
			    "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		
		String[] bulletTypeArray = new String[]{"square","round","triangleUp","diamond"};
		String json = null;

		String chartType = null;
		String type = null;
		double fillAlpha = 0;
		int lineAlpha = 0;
		int bulletAlpha = 0;
		int bulletBorderAlpha = 0;
		boolean isLine = false;
		boolean rotate=false;
		String stackType="none";
		String bullet = null;
		String colLabel = graphInfo.getGraphData().getColLabel();
		String rowLabel = graphInfo.getGraphData().getRowLabel();		
		int gridCountSize = graphInfo.getGraphData().getColList().size();	
		
		List rowList = graphInfo.getGraphData().getRowList();
		List dateRowList = graphInfo.getGraphData().getDaterowList();
		int rowListSize = rowList.size();
		boolean colLabelsName = false;
		boolean isPercentageChart = graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH 
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH;
		//This is when non clustered and multiple measure along with col labels
		if(graphInfo.getGraphData().getColLabelsName() != null && !graphInfo.getGraphData().getColLabelsName().isEmpty())
			colLabelsName = true;
		
		boolean isLegendVisible = true;
		if(rowListSize == 0){
			rowListSize = 1;
			isLegendVisible =  false;
		}
		
		graphJson = new GraphLegendJson();
		//rank maintain the sequence of graph in desc order
		if(graphInfo.getRankList() != null && !graphInfo.getRankList().isEmpty())
		{
			boolean rankEnable = false;
			for (int cnt = 0; cnt < graphInfo.getRankList().size(); cnt++) {
				CubeRankDataLabel rankDataLabel = graphInfo.getRankList().get(cnt);
				if(rankDataLabel.isStatus()
					&& ((null != rowLabel && rankDataLabel.getColumnName().equalsIgnoreCase(rowLabel)) || (null != colLabel && rankDataLabel.getColumnName().equalsIgnoreCase(colLabel))))
					rankEnable = false;
			}
			if(rankEnable)
				graphJson.setSortColumns(true);
		}
		//rank maintain the sequence of graph in desc order
		
		//Code to maintain the sequence of graph when Advance sort applied (Bug #14832) start	
		if(graphInfo.getSortList() != null && !graphInfo.getSortList().isEmpty())
		{
			boolean sortEnable = false;
			boolean isAscendingSortEnable = false;
			for (int cnt = 0; cnt < graphInfo.getSortList().size(); cnt++) {
				CubeLabelInfo cubeLabelInfo = graphInfo.getSortList().get(cnt);
				if(cubeLabelInfo.isStatus() && cubeLabelInfo.getSortType() == 1
					&& ((null != graphInfo.getRowLabelForLov() && cubeLabelInfo.getName().equalsIgnoreCase(graphInfo.getRowLabelForLov())) || (graphInfo.getRowLabelForLov() == null && null != colLabel && cubeLabelInfo.getName().equalsIgnoreCase(colLabel))) ) {
					sortEnable = true;
					if(!cubeLabelInfo.isDescOrder())//For checking whether descending or not
					{
						isAscendingSortEnable = true;
					}
				}
			}
			
			graphJson.setSortColumns(sortEnable);
			
			graphJson.setAscendingSortEnable(isAscendingSortEnable);
				
			ObjectMapper mapper = new ObjectMapper();
			String jsonString = null;
			try {
				jsonString = mapper.writeValueAsString(graphJson);
			} catch (IOException e) {
				ApplicationLog.error(e);
			}			
		}
		//Code to maintain the sequence of graph when Advance sort applied (Bug #14832) end
		
		if(LEGEND_LABEL.equals(rowLabel))//Added rowLabel != "Legend" check for Bug #15046
		{ graphJson.setSortColumns(false); }
		
		for (int k = 0; k < rowListSize; k++) {
			lineThicknessList.add(1);
		}
		int colorType = graphInfo.getGraphProperties().getColorType();
		List<String> customColors = graphInfo.getGraphProperties().getCustomColors();
		String sameColor = graphInfo.getGraphProperties().getColor();
		
		int pointColorType = graphInfo.getGraphProperties().getPointColorType();
		List<String> pointCustomColors = graphInfo.getGraphProperties().getPointCustomColors();
		String pointSameColor = graphInfo.getGraphProperties().getPointcolor();
		if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
		{	
			colorType = graphInfo.getGraphProperties().getLineColorType();
			customColors = graphInfo.getGraphProperties().getLineCustomColors();
			sameColor = graphInfo.getGraphProperties().getLinecolor();
		}
		switch(colorType)
		{
		case 1:
			if(customColors != null)
			{
				for (int i = 0; i < customColors.size(); i++) {
					if(i > (barColor.length-1))// || i > (bulletColor.length-1))
					{
						barColor = appendValue(barColor, customColors.get(i));
					}
					else
					{	
					barColor[i] = customColors.get(i);
					}
				}
			}
			break;
		case 2:
			barColor = new String[]{sameColor};
			break;
		}
		switch(pointColorType)
		{
		case 0:
			bulletColor = barColor;break;
		case 1:
			if(pointCustomColors != null)
			{
				for (int i = 0; i < pointCustomColors.size(); i++) {
					if(i > (bulletColor.length-1))// || i > (bulletColor.length-1))
						bulletColor = appendValue(bulletColor, pointCustomColors.get(i));
					else
						bulletColor[i] = pointCustomColors.get(i);
				}
			}
			break;
		case 2:
			bulletColor = new String[]{pointSameColor};
			break;
		}
		
		//Decides the type of graph
		if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH)
		{
			type = "column";
			chartType = "bar";
			fillAlpha = 1;
			if(graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH)
			{
				stackType="regular";
			}
			if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH)
			{
				stackType="100%";
			}
			for (int k = 0; k < rowListSize; k++) {
				lineThicknessList.set(k,0);
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
		{
			type = "line";
			chartType = "line";
			fillAlpha = 0;
			switch(graphInfo.getGraphProperties().getGraphLineProperties().getType())
			{
			case 1:lineAlpha =1;bulletAlpha = 0; bulletBorderAlpha = 0;isLine=true;break;
			case 2:lineAlpha = 0;bulletAlpha = 1;bulletBorderAlpha = 1;break;
			case 3:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 1;type = "smoothedLine";break;
			case 4:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 1;type = "step";break;
			default:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 0;break;
			}
			if(graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH)
			{
				stackType = "regular";
			}
			if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
			{
				stackType = "100%";
			}
			for (int k = 0; k < rowListSize; k++) {
				bulletList.add("round");
				bulletSizeList.add(8);
				lineStyleList.add(0);
				lineThicknessList.add(8);
				borderWidthList.add(0);
				bulletStyleList.add(7);
				borderColorList.add("none");
			}
			if(graphInfo.getGraphProperties().getLineType() == 0)
			{
				int dashLength = 0;
				for (int i = 0; i < rowListSize; i++) {

					int lineStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getAllLineStyle());
					int customLineThickness = graphInfo.getGraphProperties().getGraphLineProperties().getAllLineWidth();
					//Switch case for line style (dash/dot)
					switch (lineStyle) {
					case 0:
						dashLength = 0;
						break;
					case 1:
						dashLength = 0;
						break;
					case 2:
						dashLength = 7;
						break;
					case 3:
						dashLength = 1;
						break;
					}
					lineStyleList.set(i, dashLength);
					lineThicknessList.set(i, customLineThickness);
				}
			}
			else
			{
				if(null != graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList() && graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList().size() != 0)
				{
					int dashLength = 0;
					int temp =0;
					for (int l = 0; l < graphInfo.getDataColLabels3().size(); l++) {
						// .getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList()

						if (graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList()
								.size() > l) {
							int lineStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties()
									.getGraphlinePropertiesList().get(l).getStyle());
							int customLineThickness = Integer.parseInt(graphInfo.getGraphProperties()
									.getGraphLineProperties().getGraphlinePropertiesList().get(l).getThickness());
							// Switch case for line style (dash/dot)
							switch (lineStyle) {
							case 0:
								dashLength = 0;
								break;
							case 1:
								dashLength = 0;
								break;
							case 2:
								dashLength = 7;
								break;
							case 3:
								dashLength = 1;
								break;
							}
							lineStyleList.set(l % lineStyleList.size(), dashLength);
							lineThicknessList.set(l % lineThicknessList.size(), customLineThickness);
						}
						else {
							int lineStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties()
									.getGraphlinePropertiesList().get(temp).getStyle());
							int customLineThickness = Integer.parseInt(graphInfo.getGraphProperties()
									.getGraphLineProperties().getGraphlinePropertiesList().get(temp).getThickness());
							// Switch case for line style (dash/dot)
							switch (lineStyle) {
							case 0:
								dashLength = 0;
								break;
							case 1:
								dashLength = 0;
								break;
							case 2:
								dashLength = 7;
								break;
							case 3:
								dashLength = 1;
								break;
							}
							if (temp < lineStyleList.size()) {
								lineStyleList.set(temp % lineStyleList.size(), dashLength);
								lineThicknessList.set(temp % lineThicknessList.size(), customLineThickness);
								/*lineStyleList.set(temp, dashLength);
								lineThicknessList.set(temp, customLineThickness);*/
							}
						}
					}
				}
			}
			if(graphInfo.getGraphProperties().getPointType() == 0)
			{
				for (int i = 0; i < rowListSize; i++) {

					int bulletType = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getAllPointStyle());
					int bulletSize = graphInfo.getGraphProperties().getGraphLineProperties().getAllPointWidth();
					String bordercolor = graphInfo.getGraphProperties().getGraphLineProperties().getAllbordercolor();
					int borderwidth = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getAllborderwidth());
					int bulletStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getAllborderstyle());
					//Switch case for line style (dash/dot)
					switch (bulletType) {
					case 0:
						bullet = "square";
						break;
					case 1:
						bullet = "round";
						break;
					case 2:
						bullet = "triangleUp";
						break;
					case 3:
						bullet = "diamond";
						break;
					}
					switch(graphInfo.getGraphProperties().getGraphLineProperties().getAllborderwidth())
					{
					case "-1":borderwidth = 0;break;
					}
					if(bulletList.size()-1 >= i)
					{
						bulletList.set(i, bullet);					 
						bulletSizeList.set(i, bulletSize);
						if(i < bulletTypeArray.length)
						{
							bulletTypeArray[i] = bullet;
						}
						borderWidthList.set(i, borderwidth);
						if(!graphInfo.getGraphProperties().getGraphLineProperties().isAllbordercoloraslinecolor())
							borderColorList.set(i, bordercolor);
						else
							borderColorList.set(i, barColor[i%barColor.length]);
						bulletStyleList.set(i,bulletStyle);
					}
				
				}
			}
			else
			{
				if(null != graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList() && graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().size() != 0)
				{
					for (int l = 0; l < graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().size(); l++) {


						int bulletType = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getStyle());
						int bulletSize = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getThickness());
						String bordercolor = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getBordercolor();
						int borderwidth = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getBorderwidth());
						int bulletStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getBorderstyle());
						//Switch case for line style (dash/dot)
						switch (bulletType) {
						case 0:
							bullet = "square";
							break;
						case 1:
							bullet = "round";
							break;
						case 2:
							bullet = "triangleUp";
							break;
						case 3:
							bullet = "diamond";
							break;
						}
						switch(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(l).getBorderwidth())
						{
						case "-1":borderwidth = 0;break;
						}
						if(bulletList.size()-1 >= l)
						{
							bulletList.set(l, bullet);					 
							bulletSizeList.set(l, bulletSize);
							if(l < bulletTypeArray.length)
							{
								bulletTypeArray[l] = bullet;
							}
							borderWidthList.set(l%borderWidthList.size(), borderwidth);
							
							borderColorList.set(l%borderColorList.size(), barColor[l]);
							bulletStyleList.set(l%bulletStyleList.size(),bulletStyle);
						}
					}
				}
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
		{
			type = "column";
			chartType = "bar";
			fillAlpha = 1;
			rotate=true;
			if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
			{
				stackType="regular";
			}
			if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
			{
				stackType="100%";
			}
			for (int k = 0; k < rowListSize; k++) {
				lineThicknessList.set(k, 0);
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
		{
			type = "line";
			chartType = "area";
			double getAreaTransparency = (double)graphInfo.getGraphProperties().getTranceperancy();
			fillAlpha = (100 - getAreaTransparency) / 100;

			bulletAlpha = 0;

			if(graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH)
			{
				stackType="regular";
			}
			if(graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
			{
				stackType="100%";
			}
		}


		int colorLength = barColor.length;

		graphJson.setChartType(chartType);
		graphJson.setType("serial");
		graphJson.setRotate(rotate);
		graphJson.setTheme("none");
		graphJson.setStartEffect("easeOutSine");
		graphJson.setUsePrefixes(false);
		graphJson.setColumnSpacing(0);
		graphJson.setAddClassNames(true);
		graphJson.setColors(Arrays.asList(barColor));

		if(!rowList.isEmpty()) {
			for (int i = 0; i < rowList.size(); i++) {
				colorWiseIndex.add(graphInfo.getColorInfoList().get(i)%barColor.length);
			}
		}
		//String precisionLabel="";
		
		ChartCursor chartCursor = new ChartCursor();//As it is required by both clusteredMouseOver as well as chartCursor itself.
		//Adjusted Digit
		int precisionLabelCounter=1;
		List precisionLabelList= new ArrayList();
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
				|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL)))
		{
			precisionLabelCounter = graphInfo.getDataColLabels3().size();
		}
		String firstMeasurePrecision = "";
		for(int i=0;i<precisionLabelCounter;i++)
		{	
			String precisionLabel="";
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isShowadAdjustedSuffixed())
			{	
				int prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit();
				
				switch(prefix)
				{
				case 0:
					precisionLabel="";
					break;
				case 3:
					precisionLabel="K";
					break;
				case 5:
					precisionLabel="L";
					break;
				case 6:
					precisionLabel="M";
					break;
				case 7:
					precisionLabel="Cr";
					break;
				case 9:
					precisionLabel="Bn";
					break;
				default:
					precisionLabel="";
					break;
				}
				precisionLabelList.add(precisionLabel);
			}
			else
			{
				precisionLabel="";
				graphJson.setPrecision(-1);
				precisionLabelList.add(precisionLabel);
			} 
			if(i==0) {
				firstMeasurePrecision = precisionLabel;
			}
		}
		

		/*// yaxis labels digits after decimal start
		int digitsaftDecimal = 0;
		int yaxisPrecision = graphInfo.getGraphProperties().getyAxisProperties().getLabelProperties().getNumberOfDigits();
		switch(yaxisPrecision)
		{
		case 0:
			digitsaftDecimal = 0;
			break;
		case 1:
			digitsaftDecimal = 1;
			break;
		case 2:
			digitsaftDecimal = 2;
			break;
		case 3:
			digitsaftDecimal = 3;
			break;
		case 4:
			digitsaftDecimal = 4;
			break;
		case 5:
			digitsaftDecimal = 5;
			break;
		} 
		// yaxis labels digits after decimal end
*/
		// data value digits after decimal start
		int precision = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().getNumberOfDigits();//getDataValueProperties().getNumberFormat().getNumberOfDigits();
		switch(precision)
		{
		case 0:
			graphJson.setPrecision(0);
			break;
		case 1:
			graphJson.setPrecision(1);
			break;
		case 2:
			graphJson.setPrecision(2);
			break;
		case 3:
			graphJson.setPrecision(3);
			break;
		case 4:
			graphJson.setPrecision(4);
			break;
		case 5:
			graphJson.setPrecision(5);
			break;
		} 
		// data value digits after decimal start
		//----------------------------------------------------3D START---------------------------------------------------//
		if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible())
		{
			graphJson.setAngle(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getAngle());
			graphJson.setDepth3D(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getDepth3d());
		}
		//----------------------------------------------------3D END---------------------------------------------------//

		//--------------------------------------------Graph Area Start------------------------------------------------
		//Margin start
		/*if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll()==0.0)
		{
			graphJson.setMarginTop(20.0);
			graphJson.setAutoMarginOffset(20.0);
			graphJson.setMarginBottom(20.0);
			graphJson.setMarginLeft(20.0);
			graphJson.setMarginRight(20.0);
			
		}else{*/
			graphJson.setMarginTop(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());
			graphJson.setAutoMarginOffset(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());
			graphJson.setMarginBottom(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());
			graphJson.setMarginLeft(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());

			/*
			 *Note:- Apply graphJson.setMarginRight 
			 *		separately for getGraphHorizontalAreaProperties
			 *		because it will affect default view
			 *		of Stacked HORIZONTAL Graph 
			 */
			graphJson.setMarginRight(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());
		/*}*/
		//Margin end

		//grid for both axes
		CategoryAxis categoryAxis = new CategoryAxis();
		
		String xaxisTitle = "";
		String yaxisTitle ="";
		String valueAxesPosition="";
		boolean labelsEnabled=false;
		boolean inside;
		
		if(graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().isVisible())
		{
			if(graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().getTitle().equals(""))
			{
				xaxisTitle = colLabel;
			}
			else
			{
				xaxisTitle = graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().getTitle();
			}
			if(xaxisTitle!=null )
				xaxisTitle = Parser.unescapeEntities(xaxisTitle, false);	
			categoryAxis.setTitle(xaxisTitle);
		}
		//int categoryAxisTitleRotation = 0;
		if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
		{
			
			/*if(graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().getRotateCharacter() == 0)
				categoryAxisTitleRotation = 270;
			else
				categoryAxisTitleRotation = graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().getRotateCharacter();*/
			
			categoryAxis.setTitleRotation(270);
			
			//Added for default view
			if(graphInfo.getGraphProperties().getyAxisProperties().getyAxisTitleTrendProperties().isVisible())
			{
				graphJson.setMarginRight(graphInfo.getGraphProperties().getGraphHorizontalAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getRightMargin());
				if(graphInfo.getGraphProperties().getGraphHorizontalAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll() > 50)
					graphJson.setMarginRight(graphInfo.getGraphProperties().getGraphHorizontalAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll());
			}
		}
		//-----------------------------------------------------------CATEGOTY AXES START---------------------------------------------------------------------------------//
		//Tick Line Position 
		if(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getAlignment().equalsIgnoreCase("Left"))
		{
			categoryAxis.setTickPosition("start");
		}
		else
		{
			categoryAxis.setTickPosition("middle");
		}
		
		categoryAxis.setTitleFontSize(graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getFontSize());
		categoryAxis.setTitleColor(graphInfo.getGraphProperties().getxAxisProperties().getxAxisTitleTrendProperties().getFontProperties().getFontColor());

		categoryAxis.setAxisColor(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getColor());
		categoryAxis.setAxisAlpha(1);
		categoryAxis.setAxisThickness(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getThickness());

		//Axis position start
		if(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getPosition().equalsIgnoreCase("Top"))
		{
			categoryAxis.setPosition("top");
			graphJson.setMarginBottom(10.0f+graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getBottomMargin());
		}
		else
		{
			categoryAxis.setPosition("bottom");
		}
		//Axis position end

		//Labels Visible
		if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isVisible())
		{
			categoryAxis.setLabelsEnabled(true);
			categoryAxis.setTickLength(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getHeight());
			categoryAxis.setLabelOffset(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getDistanceFromLine());
			categoryAxis.setLabelRotation(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getRotationAngle());
			categoryAxis.setColor(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getFontColor());
			categoryAxis.setFontSize(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getFontSize());

			switch(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCharacterLimit())
			{
			case "none":
				categoryAxis.setTruncateLabels("undefined");
				break;
			case "auto":
				categoryAxis.setTruncateLabels("undefined");
				break;
			case "custom":
				categoryAxis.setTruncateLabels(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCustomCharacterLimit());
				break;
			} 
		} 
		if(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getTickPosition() == 2)
		{
			categoryAxis.setInside(true);
			categoryAxis.setLabelOffset(-40);
			graphJson.setGridAboveGraphs(true);
		}

		//Tick Line visibility
		if(!graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
		{
			categoryAxis.setTickLength(0);
		}
		else
		{
			categoryAxis.setTickLength(graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().getAxisMajorLineTickTrendProperties().getHeight());
		}

		//Axis Line visible start
		if(!graphInfo.getGraphProperties().getxAxisProperties().getLineProperties().isVisible())
		{
			categoryAxis.setAxisThickness(0);
		}
		//Axis Line visible end
		categoryAxis.setLabelFunction("");

		//Stagger Start
		if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isStaggerEnable())
		{
			categoryAxis.setStagger(true);
			if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getStartFrom().equalsIgnoreCase("startfromtop"))
				categoryAxis.setStaggertopbottom(true);
			else
				categoryAxis.setStaggertopbottom(false);
		}
		//Stagger End

		//ODD AND EVEN 
		if(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().isGridStripVisible())
		{
			categoryAxis.setFillColor(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getOddStripColor());
			double getTransparency = graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getTransparency();
			double newTransparency = ((100-getTransparency)/100);
			categoryAxis.setFillAlpha(newTransparency);
			categoryAxis.setGridPosition("start");
		}
		
		if(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().isGridLineVisible())
		{
			int dashLength = 0;
			int gridType=graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getStyle();
			//Switch case for Grid style (Dash/Dotted)
			switch (gridType) {
			case 1: //Dash
				dashLength = 9;
				break;
			case 2: //Dot
				dashLength = 3;
				break;
			default:
				dashLength = 0;
				break;
			}
//Removed			valueAxes.setDashLength(dashLength);
			categoryAxis.setDashLength(dashLength);
		}
		
		if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isAll())
		{
			categoryAxis.setAutoGridCount(false);
			categoryAxis.setGridCount(gridCountSize+1);
		}

		categoryAxis.setEqualSpacing(true);//For omitting empty values on Category Axis 
		graphJson.setCategoryAxis(categoryAxis);
		//-----------------------------------------------------------CATEGOTY AXES END---------------------------------------------------------------------------------//

		
		//-----------------------------------------------------------VALUE AXES START---------------------------------------------------------------------------------//
		List<ValueAxes> valueAxesList = new ArrayList<ValueAxes>();
		
		ValueAxes valueAxes=null;
		int offset=80;
		int valueAxesCounter = 1;
		//List measureNameList = new ArrayList(graphInfo.getDataColLabels3());
		/*if(graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))//Multiple Measures)
		{
			valueAxesCounter = graphInfo.getDataColLabels3().size();
			if(graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL))//when more then one measure and non  clust
				valueAxesCounter = 1;
		}*/
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
				|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL)))
			{
			valueAxesCounter = graphInfo.getDataColLabels3().size();
			}
		int leftValueAxis = 0;
		int rightValueAxis = 0;
		int leftValueAxesOffset = 0;
		int rightValueAxesOffset = 0;
		 if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartScrollbar().isEnable())
			 rightValueAxesOffset = 15;
		 
		 
		 
		//22 Jan 2020[when paginaiton, valueAxis pagination should be same]
		 
		 List measureWiseMax = new ArrayList();
		 List measureWiseMin = new ArrayList();
		 
		 int noOfVisibleValueAxis = 0; 
		 try {
			 if(graphInfo.getMeasureMaxValueList() != null && !graphInfo.getMeasureMaxValueList().isEmpty())
			 {
				 List maxMeasure = graphInfo.getMeasureMaxValueList();
				 List minMeasure = graphInfo.getMeasureMinValueList();
				 List visibleIndex = new ArrayList();
				 Map valueMaxMap = new HashMap();
				 Map valueMinMap = new HashMap();
				 Map yAxisMap = graphInfo.getGraphProperties().getyAxisPropertiesMap();

				 boolean isStackedWithOneM = false;
					if ((graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
							|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
							|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH)
							&& graphInfo.getDataColLabels3().size() == 1) {
						isStackedWithOneM = true;
					}
					
					  if ((graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH ||
					  graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH ||
					  graphInfo.getGraphType() == GraphConstants.LINE_GRAPH ||
								/* graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH || */
					  graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) || !isStackedWithOneM){
					 for(int I=0;I<yAxisMap.size();I++)
					 {
						 if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+I).getLineProperties().isVisible())
						 {
							 noOfVisibleValueAxis++;
							 visibleIndex.add(I);
						 }
						 else
							 visibleIndex.add(0);

					 }
					 //visibleIndex = new ArrayList(new LinkedHashSet(visibleIndex));
					 if(noOfVisibleValueAxis == 1)
					 {
						 measureWiseMax.add(Double.valueOf(Collections.max(maxMeasure).toString())/*(double) Collections.max(maxMeasure)*/);
						 if(Double.valueOf(Collections.min(minMeasure).toString())/*(double)Collections.min(minMeasure)*/ < 0)
							 measureWiseMin.add(Double.valueOf(Collections.min(minMeasure).toString())/*(double) Collections.min(minMeasure)*/);
						 else
							 measureWiseMin.add((double)0.0);
					 }
					 else
					 {
						 for(int z=0;z<visibleIndex.size();z++)
						 {
							 double maxMsr = Double.valueOf(maxMeasure.get(z).toString());//(double)maxMeasure.get(z);
							 double minMsr = Double.valueOf(minMeasure.get(z).toString());//(double)minMeasure.get(z);
							 
							 String key = "M"+visibleIndex.get(z);
							 if(valueMaxMap.containsKey(key))
							 {	
								 double prevValue = Double.valueOf(valueMaxMap.get(key).toString());//(double)valueMaxMap.get(key);
								 if(maxMsr > prevValue)
									 valueMaxMap.put(key, maxMsr);
							 }
							 else
								 valueMaxMap.put(key, (Double.valueOf(maxMeasure.get(z).toString()))/*(double)maxMeasure.get(z))*/);
							 
							 
							 
							 if(valueMinMap.containsKey(key))
							 {	
								 double prevValue = Double.valueOf(valueMinMap.get(key).toString());//(double)valueMinMap.get(key);
								 if(minMsr < prevValue)
									 valueMinMap.put(key, minMsr);
							 }
							 else
								 valueMinMap.put(key, (Double.valueOf(minMeasure.get(z).toString()))/*((double)minMeasure.get(z))*/);
							 if(valueMinMap.containsKey(key) && Double.valueOf(valueMinMap.get(key).toString())>0/*(double)valueMinMap.get(key) > 0*/)
								 valueMinMap.put(key, ((double)0.0));
							 //measureWiseMax.add(valueMaxMap.get(key));
						 }
						 for(int i=0;i<visibleIndex.size();i++)
						 {
							 String key = "M"+i;
							 if(valueMaxMap.get(key) != null)
								 measureWiseMax.add(valueMaxMap.get(key));
							else
								measureWiseMax.add(valueMaxMap.get("M0"));
							 
							 
							 if(valueMinMap.get(key) != null)
								 measureWiseMin.add(valueMinMap.get(key));
							else
								measureWiseMin.add(valueMinMap.get("M0"));
							 
							 
						 }
					 }
				 }
				 if (isStackedWithOneM) {
				 
					 List dataList = new ArrayList();
					 List row = graphInfo.getGraphData().getRowList();
					 List col = graphInfo.getGraphData().getColList();
					 Map keyValueMap = graphInfo.getGraphData().getKeyValueMap();
					 String key = "";
					 int rowSize = row.size();
					 String firstMsr = graphInfo.getDataColLabels3().get(0).toString();
					 
					 if(rowSize == 0)
						 rowSize = 1;
					 double total = 0.0;
					 String colKey = "";
					 String rowKey = "";
					 for(int i=0;i<col.size();i++)
					 {
						 colKey = col.get(i).toString();
						 total = 0.0;
						 for(int j=0;j<rowSize;j++)
						 {
							 if(!row.isEmpty())
								 rowKey = row.get(j).toString();
							 
							 key = colKey + rowKey + firstMsr;
							 if(keyValueMap.get(key) != null)
								 total = total + Double.valueOf(keyValueMap.get(key).toString());//(double)keyValueMap.get(key); 
						 }
						 dataList.add(total);
					 }
					 measureWiseMax.add(Collections.max(dataList));
					 measureWiseMin.add(Collections.min(dataList));
				 }
					
			 }
		 }
		 catch (Exception e) {
			 ApplicationLog.error(e);
		 }
		 
		//22 Jan 2020
		 
		for(int i=0;i < valueAxesCounter;i++){
			valueAxes = new ValueAxes();
			valueAxes.setLocale(Locale.getDefault().getLanguage());
			
			valueAxes.setAutoGridCount(true);
			valueAxes.setAutoOffset(false);
			valueAxes.setId("valueAxes"+i);
			/*if(i > 0)
				valueAxes.setOffset(offset);*/
			// yaxis labels digits after decimal start
			int digitsaftDecimal = 0;
			int yaxisPrecision = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getNumberOfDigits();
			switch(yaxisPrecision)
			{
			case 0:
				digitsaftDecimal = 0;
				break;
			case 1:
				digitsaftDecimal = 1;
				break;
			case 2:
				digitsaftDecimal = 2;
				break;
			case 3:
				digitsaftDecimal = 3;
				break;
			case 4:
				digitsaftDecimal = 4;
				break;
			case 5:
				digitsaftDecimal = 5;
				break;
			} 
			// yaxis labels digits after decimal end
			
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().isVisible())
			{
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getTitle().equals(""))
				{					
					if(graphInfo.getGraphData().getColLabelsName() != null && !graphInfo.getGraphData().getColLabelsName().isEmpty())
						yaxisTitle =  graphInfo.getGraphData().getColLabelsName().get(i).toString();
					else if(valueAxesCounter == 1)
						yaxisTitle =  graphInfo.getGraphData().getDataLabel2();
					else {
						yaxisTitle =  graphInfo.getDataColLabels3().get(i).toString();
						if(graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
							yaxisTitle =  graphInfo.getGraphData().getDataLabel2();						
					}
					
					if(yaxisTitle != null && yaxisTitle.equalsIgnoreCase("data"))
						yaxisTitle = "";
				}
				else
				{
					yaxisTitle = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getTitle();
				}
				if(yaxisTitle != null)
					yaxisTitle = Parser.unescapeEntities(yaxisTitle, false);
				
				valueAxes.setTitle(yaxisTitle);
				int valueAxesTitleRotation = 0;
				if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
				{
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getRotateCharacter() == 0)
						valueAxesTitleRotation = 270;
					else
						valueAxesTitleRotation = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getRotateCharacter();
				}
				else if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
						|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
				{
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getRotateCharacter() == 0)
						valueAxesTitleRotation = 0;
					else
						valueAxesTitleRotation = 180;//When Horizontal as per amcharts requirement Bug #12614
				}
				valueAxes.setTitleRotation(valueAxesTitleRotation);
			}
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().isGridLineVisible())
			{
				int dashLength = 0;
				int gridType=graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getStyle();
				//Switch case for Grid style (Dash/Dotted)
				switch (gridType) {
				case 1: //Dash
					dashLength = 9;
					break;
				case 2: //Dot
					dashLength = 3;
					break;
				default:
					dashLength = 0;
					break;
				}
				valueAxes.setDashLength(dashLength);
			}
			
			//Y-axis Line Position start
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getPosition().equalsIgnoreCase("Left"))
			{
				valueAxesPosition = "left";
			}
			else
			{
				valueAxesPosition = "right";
			}
			//Y-axis Line Position end

			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible())
			{
				labelsEnabled = true;
			}
			else
			{
				labelsEnabled = false;
			}

			//Title
			valueAxes.setTitleFontSize(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getFontProperties().getFontSize());
			valueAxes.setTitleColor(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().getFontProperties().getFontColor());

			//Axis
			valueAxes.setAxisColor(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getColor());
			valueAxes.setAxisAlpha(1);
			valueAxes.setAxisThickness(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getThickness());

			//Tick and Label
			valueAxes.setLabelsEnabled(labelsEnabled);
			valueAxes.setTickLength(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getHeight());
			valueAxes.setLabelOffset(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getDistanceFromLine());
			valueAxes.setLabelRotation(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getRotationAngle());
			valueAxes.setColor(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getFontProperties().getFontColor());
			valueAxes.setFontSize(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getFontProperties().getFontSize());
			if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isVisible()&& (graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH))
			{
				valueAxes.setLabelRotation(graphInfo.getGraphProperties().getyAxisProperties().getLabelProperties().getRotationAngle());	
			}
			//Y-axis Tick position start
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().getTickPosition() == 2)
			{
				inside = true;
				valueAxes.setLabelOffset(-50);
				graphJson.setGridAboveGraphs(true);
			}
			else
			{
				inside = false;
			}
			valueAxes.setInside(inside);
			//Y-axis Tick position end

			//Tick Line visibility
			if(!graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().isVisible())
			{
				valueAxes.setTickLength(0);
			}

			//Axis Line yAxis visible start
			if(!graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible())
			{
				valueAxes.setAxisThickness(0);
			}
			//Axis Line yAxis visible end

			
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible()
					|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible()
					|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().getAxisMajorLineTickTrendProperties().isVisible()
				    || graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().isVisible())
			{
				valueAxes.setAutoOffset(true);
			}
			
			//Minor Grid
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().isMinorGridEnable())
			{
				valueAxes.setMinorGridAlpha(1);
				valueAxes.setMinorGridEnabled(true);
				valueAxes.setMinorTickLength(0);
			}
			
			//Stack Type
			valueAxes.setStackType(stackType);//stacked bar
			valueAxes.setPosition(valueAxesPosition);
			
			String blank = "";
			graphJson.setThousandsSeparator(blank);
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isCommaSeprator())
            {
				//graphJson.setThousandsSeparator(",");
				valueAxes.setLabelFunction("");
				 switch(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getCommaFormat())
				 {
				 case 1:
					 valueAxes.setCommaSeparatorUsStyle(true);
					 break;
				 case 2:
					 
					 valueAxes.setCommaSeparatorIndianStyle(true);
					 break;
				 }
	         
            }

			//-----------------------------------------------------------VALUE AXES END---------------------------------------------------------------------------------//
			// Grid Lines
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().isGridLineVisible())
			{
				categoryAxis.setGridColor(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineColor());
				categoryAxis.setGridAlpha(1);
				categoryAxis.setGridThickness(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineThickness());
				categoryAxis.setGridPosition("start");

				int gridType=graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getStyle();
				if(gridType == 1)
				{
					valueAxes.setDashLength(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineThickness());
					categoryAxis.setDashLength(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineThickness());
				}

				//yAxis
				

				valueAxes.setGridColor(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineColor());
				valueAxes.setGridAlpha(1);
				valueAxes.setGridThickness(graphInfo.getGraphProperties().getGraphAreaProperties().getBackGroundGrid().getGridLineThickness());
				valueAxes.setGridPosition("start");
				//valueAxesList.add(valueAxes);

				//graphJson.setValueAxes(valueAxesList);
			}
			else
			{
				categoryAxis.setGridPosition("");
				categoryAxis.setGridColor("");
				categoryAxis.setGridAlpha(0);
				categoryAxis.setGridThickness(1);
				categoryAxis.setGridPosition("start");


				valueAxes.setGridColor("");
				valueAxes.setGridPosition("");
				valueAxes.setGridAlpha(0);
				valueAxes.setGridThickness(1);
				valueAxes.setPosition(valueAxesPosition);
				valueAxes.setStackType(stackType);
				//valueAxesList.add(valueAxes);
				//graphJson.setValueAxes(valueAxesList);
			}
			if(valueAxesPosition.equalsIgnoreCase("left"))
			{
				leftValueAxis++;
				if(leftValueAxis>1)
					leftValueAxesOffset=leftValueAxesOffset+80;
				//valueAxes.setOffset(leftValueAxesOffset);
				
			}
			else
			{
				rightValueAxis++;
				if(rightValueAxis>1)
					rightValueAxesOffset=rightValueAxesOffset+80;
				//valueAxes.setOffset(rightValueAxesOffset);
			}
			//--------------------------------------------Graph Area End------------------------------------------------ 

			//------------------------------------------- Data Provider Start-----------------------------------------------
			double customMax = 0.0;
			//When we provide custom max value this will set flag to true.
			int adjustedDigit = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit();
			int divValue = (int)(Math.pow(10, adjustedDigit));
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxValType() == 1)
			{
					customMax =Double.parseDouble(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxCustomVal());
					customMax= customMax/divValue;					
					valueAxes.setStrictMinMax(true);
			}
					
			//--------------------------------------------Custom/Auto start--------------------------------------------------------//
			
			//22 Jan 2020
			boolean stackAuto = false;
			boolean barAuto = false;

			if ((graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH)
					&& graphInfo.getDataColLabels3().size() == 1)
				stackAuto = true;
			 
			if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
					 || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH)
				barAuto = true;
			boolean autoMax = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxValType() == 0;
			if(null !=graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+"0") && graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+"0").getLabelProperties().getMaxValType()==2) {
				if(graphInfo.getGraphType()==GraphConstants.AREA_GRAPH || graphInfo.getGraphType()==GraphConstants.AREA_DEPTH_GRAPH ||graphInfo.getGraphType()==GraphConstants.AREA_STACK_GRAPH)
				{
					measureWiseMax.addAll(graphInfo.getMeasureMaxValueList());
					measureWiseMin.addAll(graphInfo.getMeasureMinValueList());
				}
				autoMax=true;
			}
			boolean asperDataMax = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxValType() == 2;
			boolean asperDataMin = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMinValType() == 2;
			if(asperDataMax && asperDataMin) {
			    	valueAxes.setStrictMinMax(true);
			}
			if ((asperDataMax || asperDataMin)
					&& (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
							|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH || 
							graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH ||
							graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH) && !stackAuto) {

				try {					
					double max = 0.0;
					double min = 0.0001;
					if(asperDataMax) {
						for (int j = 0; j < graphInfo.getMeasureMaxValueList().size(); j++) {
							max += ((double) graphInfo.getMeasureMaxValueList().get(j) / divValue);
						}
						max = max + (max * 0.10);
						valueAxes.setMaximum(Math.ceil(max));
					}
					if(asperDataMin) {
						for (int j = 0; j < graphInfo.getMeasureMinValueList().size(); j++) {
							min += ((double) graphInfo.getMeasureMinValueList().get(j) / divValue);
						}
	
						if (min < 0.0001)
							valueAxes.setMinimum(min);
						else
							valueAxes.setMinimum(0.0001);
					}					
				} catch (Exception e) {
					ApplicationLog.error(e);
				}
			}
			 
		    if(i>0){
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M" + i).getLabelProperties().isVisible()==true && graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M" + i).getLabelProperties().getMaxValType()!=2) {
					autoMax =false;
				}
			}
		  
			if(graphInfo.getMeasureMaxValueList() != null && graphInfo.getMeasureMaxValueList().size() > 0
					  && (barAuto || stackAuto))
			{
				try {
					//valueAxes.setStrictMinMax(true);
					double max = 0.0;
					double min = 0.0001;
					if(asperDataMax) {
						if(noOfVisibleValueAxis == 1 && measureWiseMax.size() > 0)
							max = ((double)measureWiseMax.get(0)/divValue);
						else
							max = (((double)measureWiseMax.get(i)/divValue));
						
						max = max + (max*0.10);
						valueAxes.setMaximum(Math.ceil(max));
					}
					if(asperDataMin) {						
						if(noOfVisibleValueAxis == 1 && measureWiseMin.size() > 0)
							min = ((double)measureWiseMin.get(0)/divValue);
						else
							min = (((double)measureWiseMin.get(i)/divValue));
						if(min < 0.0001)
							valueAxes.setMinimum(min);
						else
							valueAxes.setMinimum(0.0001);
					}
					
				}
				catch(Exception e)
				{
					ApplicationLog.error(e);
				}
			}
			//22 Jan 2020
			
			
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxValType() == 1)
			{
				valueAxes.setMaximum(customMax);
			}
			/*else
			{
				valueAxes.setM(0.0);
			}*/
			if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMinValType() == 1)
			{
				double customMin;
				customMin = Double.parseDouble(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMinCustomVal());
				customMin = customMin/divValue;
				if(customMin == 0.0 || customMin == 0)
					customMin=0.000001;
				
				valueAxes.setMinimum(customMin);
			}
			/*else
			{
				valueAxes.setMinimum(0.0);
			}*/
			
			valueAxes.setUnit(precisionLabelList.get(i).toString());
			if(isPercentageChart)
				valueAxes.setUnit("%");
			valueAxes.setPrecision(digitsaftDecimal);
			
			if((graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
					&& graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isShowTotalValue())
			{
				String suffix = precisionLabelList.get(i).toString();
				if(suffix=="" || firstMeasurePrecision!=suffix) {
					suffix = firstMeasurePrecision;
				}
				if(valueAxesCounter>1 && graphInfo.getGraphData().getColLabel() != null && !graphInfo.getGraphData().getColLabel().isEmpty() && (graphInfo.getGraphData().getRowLabel()!=null && !graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL))) {
					valueAxes.setTotalText("[[AbsrealTotal"+i+"]]"+suffix);
				}else {
					valueAxes.setTotalText("[[AbsrealTotal]]"+suffix);
				}
			}
			if(null !=graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i) && graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isLogarithmic()) {
				valueAxes.setLogarithmic(true);
				valueAxes.setUsePrefixes(true);
				valueAxes.setTreatZeroAs(0.01);
				
			}
			if(null != graphInfo.getGraphProperties().getSmartenProperties() && graphInfo.getGraphProperties().getSmartenProperties().isEnableLogarithmic())
			{
				valueAxes.setLogarithmic(true);
				valueAxes.setTreatZeroAs(0.01);
			}
			valueAxesList.add(valueAxes);
			/*if(i > 0)
			offset=offset+80;*/
			}
		

		graphJson.setValueAxes(valueAxesList);

		//--------------------------------------------Custom/Auto end--------------------------------------------------------//

		//------------------------------------------- Data Provider End--------------------------------------------------

		//------------------------------------------- Legend Start--------------------------------------------------
		
		if(isLegendVisible){
			if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible())
			{
				// 9 Apr [p.p]
				List hideGraphsList = new ArrayList();
				Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();
				if(graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL))
				{
					for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
					{
						if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)).toString().equals("false")) {
							hideGraphsList.add(i);
						}
					}
				}
				// 9 Apr [p.p]
				Legend legend = new Legend();

				legend.setValueWidth(0);// for purpose of removing white spaces(Legned)
				List<Map<String, Object>> dataRulesMap = new ArrayList<Map<String, Object>>();
				Map<String, Object> dataMapOther = new HashMap<>();

				List<Integer> updatedColorInfoList = graphInfo.getColorInfoList();
				if(null != graphInfo.getLegendColorInfoList() && !graphInfo.getLegendColorInfoList().isEmpty() && null != graphInfo.getGraphProperties()
						&& null != graphInfo.getGraphProperties().getLegendCustomValueList()
						&& !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()) {
					updatedColorInfoList = graphInfo.getLegendColorInfoList();
					if(graphInfo.getLegendColorInfoList().size() !=updatedColorInfoList.size()) {
				    	  updatedColorInfoList = graphInfo.getColorInfoList();
				     }
				}
				/*if (null != graphInfo.getGraphProperties()
						&& null != graphInfo.getGraphProperties().getLegendCustomValueList()
						&& !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()) {

					if (graphInfo.getColorInfoList().size() != updatedColorInfoList.size()) {

						updatedColorInfoList = graphInfo.getLegendColorInfoList();
					}
				}
*/				List legendValList = new ArrayList<>();
				legendValList.addAll(rowList);

				if (graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
						.getLegendValuesOrder().equalsIgnoreCase("option3")
						&& graphInfo.getGraphProperties().getLegendCustomValueList() != null
						&& !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()
						&& graphInfo.getDrilldownBreadcrumbMap() == null) {
					legendValList.clear();
					legendValList.addAll(graphInfo.getGraphProperties().getLegendCustomValueList());
				}
				boolean result = null != graphInfo.getRowColumns() && !graphInfo.getRowColumns().isEmpty()
						&& null != graphInfo.getColColumns() && !graphInfo.getColColumns().isEmpty()
						&& graphInfo.getDataColLabels3().size() > 1 && graphInfo.getDateFrequencyMap() != null
						&& !graphInfo.getDateFrequencyMap().isEmpty()
						&& null != graphInfo.getDateFrequencyMap()
								.get(graphInfo.getRowColumns().elementAt(0).toString())
						&& !graphInfo.getDateFrequencyMap().get(graphInfo.getRowColumns().elementAt(0).toString())
								.isEmpty();
				if (!rowList.isEmpty()) {
					for (int i = 0; i < rowList.size(); i++) {
						if(!hideGraphsList.contains(i)) { //9 Apr [p.p]
						Map<String, Object> dataMap =  new HashMap<>();
						
						String tmp = legendValList.get(i).toString();
						String tmp1=legendValList.get(i).toString();
						if(colLabelsName && null != graphInfo.getGraphData().getColLabelsName() && !graphInfo.getGraphData().getColLabelsName().isEmpty() && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
								&& graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL) && tmp!= graphInfo.getGraphData().getColLabelsName().get(i) && !rowList.get(i).equals(graphInfo.getGraphData().getColLabelsName().get(i))) {
								tmp = graphInfo.getGraphData().getColLabelsName().get(i).toString();
						}
						
						
						//Added code for Bug #13406 start
						if(null !=graphInfo.getDataColLabels3() && graphInfo.getDataColLabels3().size() < 2 &&!dateRowList.isEmpty() && dateRowList.size() > i
								&& null != dateRowList.get(i) && !dateRowList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE)) {
							String stringFormat;
							stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
							stringFormat = stringFormat.replaceAll("&#39;", "'");
							Calendar cal = Calendar.getInstance();
							Date axisDate = new Date();
							axisDate = (Date) dateRowList.get(i);
							cal.setTime(axisDate);
							stringFormat=stringFormat.trim();
							tmp = new SimpleDateFormat(stringFormat).format(cal.getTime());
						}//Added code for Bug #13406 end
						 if (null !=graphInfo.getDataColLabels3() && graphInfo.getDataColLabels3().size() < 2 && graphInfo.getDateFrequencyMap() != null && !graphInfo.getDateFrequencyMap().isEmpty() && null != graphInfo.getRowColumns() && !graphInfo.getRowColumns().isEmpty() && null != graphInfo.getDateFrequencyMap().get(graphInfo.getRowColumns().elementAt(0).toString()) && !graphInfo.getDateFrequencyMap().get(graphInfo.getRowColumns().elementAt(0).toString()).isEmpty()) {
						
							tmp  = GraphsUtil.getLegendDateFormat(graphInfo,tmp);
														
						}
						 if(result) {
							 tmp  = GraphsUtil.getLegendDateFormat(graphInfo,tmp);
						 }
						switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
						{
						case "auto":
							//tmp = rowList.get(i).toString();
							int truncateCharLimitAuto = 15;
							if (tmp.length() > truncateCharLimitAuto)
								tmp = tmp.substring(0, truncateCharLimitAuto)+"..";
							break;
						case "custom":
							//tmp = rowList.get(i).toString();
							int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
							if (tmp.length() > truncateCharLimit)
								tmp = tmp.substring(0, truncateCharLimit)+"..";
							break;
						/*default:
							tmp = rowList.get(i).toString();
							break;*/
						}
						/*if(tmp.contains("..")  && null != graphInfo.getGraphData().getColLabelsName() && null != graphInfo.getGraphData().getColLabelsName().get(i) && colLabelsName && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize ) {
							tmp = graphInfo.getGraphData().getColLabelsName().get(i).toString();
							int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
							if (tmp.length() > truncateCharLimit)
								tmp = tmp.substring(0, truncateCharLimit)+"..";	
						}
						else if(colLabelsName && null != graphInfo.getGraphData().getColLabelsName() && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
								&& graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase(LEGEND_LABEL)) {
							tmp = graphInfo.getGraphData().getColLabelsName().get(i).toString();
						}*/
						if(tmp != null)
							tmp = Parser.unescapeEntities(tmp, false);
						
						
						if(!tmp.equalsIgnoreCase("Other")) {
							dataMap.put("title", tmp);
							dataMap.put("valueField", tmp1);
							if(updatedColorInfoList != null) {
								dataMap.put("color", barColor[ updatedColorInfoList.get(i)%barColor.length]);
							}
							dataRulesMap.add(dataMap);
						}else {
							dataMapOther.put("title", tmp);
							dataMapOther.put("valueField",tmp1);
							dataMapOther.put("color", barColor[updatedColorInfoList.get(i)%barColor.length]);
						}
					}
				}
				
										
					try {
						if(!dataRulesMap.isEmpty() && !graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
								.getLegendValuesOrder().equalsIgnoreCase("option3")){
							dataRulesMap.sort(Comparator.comparing(o -> String.valueOf(o.get("title")), 
							        String.CASE_INSENSITIVE_ORDER));
							
						}
						if(!dataMapOther.isEmpty()) {
							dataRulesMap.add(dataMapOther);
						}
					}catch(Exception e) {
						ApplicationLog.error(e);
					}
					
					legend.setData(dataRulesMap);
					}
				//}
				
				
				
				//legend.setDivId("legenddivs");
				if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().isEnable())
				{
					legend.setValueText("");
				}
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isDrillDown())
				{
					legend.setSwitchable(false);
					//legend.setClickLabel("");
				}
				else
				{
					legend.setSwitchable(true);
				}
				if(isContextFilter)
				{
					legend.setSwitchable(false);
				}
				if(isContextFilter && graphInfo.getGraphData().getDataLabel() != null && graphInfo.getGraphData().getDataLabel().equalsIgnoreCase("data"))
				{
					legend.setSwitchable(true);
				}
				
				//------------------------------------------- Legend Panel Start--------------------------------------------------
				legend.setVerticalGap(5);
				String position="";
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelPosition())
				{
				case 1: position ="top";
					legend.setHorizontalGap(0);
					break;
				case 2: position ="left";
					legend.setHorizontalGap(10);
					break;
				case 3: position ="right";
				legend.setHorizontalGap(10);
					break;
				case 4: position="bottom";
					legend.setHorizontalGap(0);
					break;
				}
				legend.setPosition(position);
				//legend Visible
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().isVisible()) {
					legend.setBackgroundAlpha(1);
					legend.setBackgroundColor(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().getBackGroundColor());

					if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().isBackgroundTransparent()) {
						legend.setBackgroundAlpha(0);
					}
					else{
						legend.setBackgroundAlpha(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().getTransparency()/100.0);
					}
				}
				else {
					legend.setBackgroundAlpha(0);
					legend.setBackgroundColor("");
				}
				//Legend Margin start
				//legend.setAutoMargins(true);
				
				legend.setMarginTop(5.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getTopMargin());
				legend.setMarginBottom(5.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getBottomMargin());
				legend.setMarginLeft(10.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getLeftMargin());
				legend.setMarginRight(20.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getRightMargin());
				
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getAll() != 0.0)
				{	
					legend.setMarginTop(5.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getAll());
					legend.setMarginBottom(5.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getAll());
					legend.setMarginLeft(10.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getAll());
					legend.setMarginRight(20.0+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getAll());
				}
				
				//Legend Margin end

				//this allows distance between on hover value and legend value(title)
				legend.setEqualWidths(false);
				legend.setAlign("center");

				//------------------------------------------- Legend Panel End--------------------------------------------------

				//------------------------------------------- Legend Title Start--------------------------------------------------
				if(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().isTitleVisible())
				{
					String legendTitle = null;
					if(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitle().equals(""))
					{
						if(isLegendVisible)
						{
							legendTitle = graphInfo.getGraphData().getRowLabel();	
							if(rowLabel.equalsIgnoreCase(LEGEND_LABEL))
								legendTitle = "";
						}
						else
						{
							legendTitle = graphInfo.getGraphData().getColLabel();
						}
					}
					else
					{	
						legendTitle = graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitle();
					}	
					if(legendTitle!=null)
					legendTitle = Parser.unescapeEntities(legendTitle, false);		

					legend.setTitle(legendTitle);
				}
				else
				{	
					legend.setTitle("");
				}
				legend.setFontSize(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getFontSize());
				//------------------------------------------- Legend Title End--------------------------------------------------
				//------------------------------------------- Legend Values Start--------------------------------------------------
				int maxColumns = 0;
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesColumn())
				{
				case 1:
					maxColumns = 1;
					break;
				case 2:
					maxColumns = 2;
					break;
				case 3:
					maxColumns = 3;
					break;
				case 4:
					maxColumns =4;
					break;
				default:
					maxColumns = 100;
					break;

				}
				legend.setMaxColumns(maxColumns);
				/*legend.setVerticalGap(5);
				legend.setHorizontalGap(0);*/
				
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option1") || 
						graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
						.getLegendValuesOrder().equalsIgnoreCase("option3")){
					legend.setReversedOrder(false);
				} 
				else if(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option2")){
					legend.setReversedOrder(true);
				}

				//------------------------------------------- Legend Values End--------------------------------------------------
				//------------------------------------------- Legend Icon Start--------------------------------------------------
				String legendIconShape="";

				int markerSize = graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getWidth();
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconSelectShape())
				{
				case "None": legendIconShape="none"; break;
				case "Square": legendIconShape="square"; break;
				case "Circle": legendIconShape="circle"; break;
				case "TriangleUp": legendIconShape="triangleUp"; break;
				case "TriangleDown": legendIconShape="triangleDown"; break;
				case "TriangleLeft": legendIconShape="triangleLeft"; break;
				case "TriangleRight": legendIconShape="triangleRight"; break;
				/*case "Line": legendIconShape="line"; break;*/
				case "Diamond": legendIconShape="diamond"; break;
				case "Bubble": legendIconShape="bubble"; break;
				}
				legend.setMarkerSize(markerSize);
				legend.setMarkerType(legendIconShape);

				if(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().isVisible()
						&& graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderStyle().equalsIgnoreCase("solid")){
					legend.setMarkerBorderAlpha(1);
					legend.setMarkerBorderThickness(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderWidth());
					legend.setMarkerBorderColor(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderColor());
				}
				else
				{
					legend.setMarkerBorderAlpha(0);
					legend.setMarkerBorderColor("");
				}
				//------------------------------------------- Legend Icon End-------------------------------------------------- 
				((GraphLegendJson) graphJson).setLegend(legend);			
			}
		}
		//------------------------------------------- Legend End--------------------------------------------------
		
		//Dashboard purpose
		if(colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase(LEGEND_LABEL)))
		{
			graphJson.setMultipleMeasure(true);
		}
		else
		{
			graphJson.setMultipleMeasure(false);
		}
		//Dashboard purpose end
		
		List allLabels = new ArrayList();
		graphJson.setAllLabels(allLabels);

		List<ValueAxis> valueAxisList = new ArrayList<>();
		ValueAxis valueAxis = new ValueAxis();
		valueAxis.setAxisAlpha(0);
		valueAxis.setPosition("left");
		valueAxis.setTitle("New Ones");
		valueAxisList.add(valueAxis);
		graphJson.setValueAxis(valueAxisList);

		double startDuration = 0.0;
		if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isAnimationPlayable())
			startDuration = 0.25;
		graphJson.setStartDuration(startDuration);//Play or Stop Animation

		
		graphJson.setCategoryField(colLabel);
		
		List<Graphs> graphsList = new ArrayList<>();
		graphJson.setGraphs(graphsList);
		//-------------------------------------------Balloon Start----------------------------------------------------
		if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable())
		{
			Balloon balloon = new Balloon();
			balloon.setColor(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontColor());
			balloon.setFontSize(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFont().getFontSize());
			balloon.setAdjustBorderColor(true);
			balloon.setBorderThickness(0);
			graphJson.setBalloon(balloon);
		}
		//--------------------------------------------Balloon End--------------------------------------------------

		//Reference Line Start
		List<Guides> guideList = new ArrayList<>();
		int refLineStyle = 0;
		int refDashLength = 0;		
		int i=0;//Fetching reference line style index
		Map<Integer, ReferenceLine> testMap = graphInfo.getGraphProperties().getReferencelinePropertiesMap();
		for (Entry<Integer, ReferenceLine> entry : testMap.entrySet()) {
			i++;
			Guides guides = new Guides();
			ReferenceLine referenceLine = entry.getValue();
			refLineStyle = Integer.parseInt(referenceLine.getStyle());
			switch (refLineStyle) {
			case 0:
				refDashLength = 0;
				break;
			case 1:
				refDashLength = 0;
				break;
			case 2:
				refDashLength = 7;
				break;
			case 3:
				refDashLength = 2;
				break;
			}
			guides.setLineAlpha(referenceLine.getLineAlpha());
			guides.setLineColor(referenceLine.getColor());
			guides.setValueAxis("valueAxes0");
			guides.setLabel(referenceLine.getLabel());
			double sd=Double.parseDouble(referenceLine.getValue());
			int adjustedDigit = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().getAdjustedDigit();
			int divValue = (int)(Math.pow(10, adjustedDigit));
			if(sd>0 && sd/divValue>0)
				guides.setValue(sd/divValue);
			else
				guides.setValue(sd);
			guides.setInside(true);
			guides.setLineThickness(referenceLine.getWidth());
			guides.setDashLength(refDashLength);
			guides.setToValue(guides.getValue());
		
			//code add by akshay for fillGuids in snapshot use//
			double sd2=Double.parseDouble(referenceLine.getToValue());			
			if( sd2 != 0) {
				if(sd2>0 && sd2/divValue>0)
					guides.setToValue(sd2/divValue);
				else
					guides.setToValue(sd2);
				
				guides.setInside(true);
				guides.setAbove(false);
				guides.setFillAlpha(referenceLine.getFillAlpha());
				guides.setFillColor(referenceLine.getFillColor());
			}
			
			//end Akshay 
			guideList.add(guides);
		}
		graphJson.setGuides(guideList);
		//Reference Line End

		//cone and cylinder
		if(graphInfo.getGraphProperties().getBarProperties().getType() == 2)
		{
			graphJson.setAngle(30);
			graphJson.setDepth3D(30);
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getAngle() > 1 && graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible())
				graphJson.setAngle(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getAngle());
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getDepth3d() > 1 && graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible())
				graphJson.setDepth3D(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getDepth3d());
		}
		else if(graphInfo.getGraphProperties().getBarProperties().getType() == 3)
		{
				graphJson.setAngle(30);
				graphJson.setDepth3D(30);
				if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getAngle() > 1 && graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible())
					graphJson.setAngle(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getAngle());
				if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getDepth3d() > 1 && graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible())
					graphJson.setDepth3D(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getDepth3d());
		}
		
		
		//------------------------------------------- Graphs Scroll Start--------------------------------------------------
		if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartScrollbar().isEnable())
		{
			//mouseWheelZoomEnabled
			graphJson.setMouseWheelZoomEnabled(true);
			
			graphJson.setScrollBar(true);
			//value ScrollBar
			ValueScrollbar valueScrollbar = new ValueScrollbar();
			valueScrollbar.setEnabled(true);
			graphJson.setValueScrollbar(valueScrollbar);
		}
		//------------------------------------------- Graphs Scroll End--------------------------------------------------
		
		//------------------------------------------- Chart Cursor Start--------------------------------------------------

		if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().isEnable())//.isHorizontal() || graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().isVertical())
		{
			if(graphInfo.getGraphProperties().getZoomType() == 0)//Both
			{
				chartCursor.setCursorLineAlpha(0);
				//chartCursor.setCursorAlpha(0.3);
				chartCursor.setZoomable(true);
				chartCursor.setValueZoomable(true);
				//chartCursor.setValueLineAlpha(0.3);
				chartCursor.setValueLineEnabled(true);
				graphJson.setZoomType("both");
			}
			else if(graphInfo.getGraphProperties().getZoomType() == 1)//Horizontal
			{
				chartCursor.setCursorLineAlpha(0);
				//chartCursor.setCursorAlpha(0.3);
				chartCursor.setZoomable(true);
				chartCursor.setValueLineAlpha(0);
				//chartCursor.setValueBalloonsEnabled(true);
				//chartCursor.setCategoryBalloonEnabled(true);
				/*chartCursor.setValueZoomable(true);
				chartCursor.setValueLineEnabled(true);*/
				graphJson.setZoomType("horizontal");
			}
			else if(graphInfo.getGraphProperties().getZoomType() == 2)//Vertical
			{
				chartCursor.setValueZoomable(true);
				chartCursor.setValueLineEnabled(true);
				chartCursor.setZoomable(false);
				chartCursor.setCursorAlpha(0);
				//chartCursor.setValueLineAlpha(0.3);
				chartCursor.setCursorLineAlpha(0);
				/*chartCursor.setCategoryBalloonEnabled(false);
				chartCursor.setValueLineBalloonEnabled(true);*/
				//chartCursor.setValueBalloonsEnabled(false);
				graphJson.setZoomType("vertical");
			}

			chartCursor.setCursorColor("black");
			chartCursor.setCursorPosition("mouse");
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().isFullWidth())
			{
				chartCursor.setFullWidth(true);
				chartCursor.setCursorPosition("middle");
			}
			/*if(graphInfo.getGraphProperties().getGraphAreaProperties().getGraphChartCursor().isSelectWithoutZooming())
			{
				chartCursor.setSelectWithoutZooming(true);
			}*/
			chartCursor.setAvoidBalloonOverlapping(false);
			chartCursor.setSelectionAlpha(0.3);

			graphJson.setChartCursor(chartCursor);
		}
		//------------------------------------------- Chart Cursor End--------------------------------------------------
		
		
		//Responsive start
		Responsive responsive = new Responsive();
        responsive.setEnabled(true);
        responsive.setAddDefaultRules(false);
        boolean adaptiveBehaviour = graphInfo.getGraphProperties().getAdaptiveBehaviour();
        List<LinkedHashMap<String, Object>> rulesMapList = new ArrayList<>();
        LinkedHashMap<String, Object> dpRulesMap =  new LinkedHashMap<>();
        if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible() && isLegendVisible && adaptiveBehaviour)
		{
        dpRulesMap.put("maxWidth", 320);
        LinkedHashMap<String, Object> legendMap = new LinkedHashMap<>();
        	
        dpRulesMap.put("overrides", legendMap);
        LinkedHashMap<String, Object> ruleMap = new LinkedHashMap<>();
        ruleMap.put("enabled", true);
        ruleMap.put("position", "bottom");
        ruleMap.put("maxColumns", "undefined");
        legendMap.put("legend", ruleMap);
		}
        rulesMapList.add(dpRulesMap);
        responsive.setRules(rulesMapList);
        graphJson.setPathToImages("../themes/default/img/amcharts/");
        graphJson.setResponsive(responsive); 
		//Responsive end
        //Double d = new Double(graphJson.getPrecision());
		graphJson.setPercentPrecision(2);//Setting default 2 instead (d.intValue()) for NeGD feature request 15081 [1 Aug 2019]
		jsonList.add(graphJson);
		
		try {
			json = objectMapper.writeValueAsString(jsonList);			
		} catch (IOException e) {
			ApplicationLog.error(e);
		}
		//ApplicationLog.info("json- "+json);
		setDisplayColorIndex(colorWiseIndex, graphInfo);
		return (json);
}
	private static final String[] appendValue(String[] s1 ,String newValue) {

		  String[] erg = new String[s1.length + 1];
		  erg[erg.length-1] = newValue;
	      System.arraycopy(s1, 0, erg, 0, s1.length);

	      return erg;

	  }
	
	private static void setDisplayColorIndex(List colorWiseIndex, GraphInfo graphInfo) {
		if(graphInfo.getGraphProperties().getColorType()==2) {
			if(graphInfo.getDataColLabels3().size()>=2 && graphInfo.getGraphData().getRowLabel()!=null&& (graphInfo.getGraphData().getRowLabel().equals("Legend") || graphInfo.getGraphData().getColLabel() ==null || graphInfo.getGraphData().getColLabel().isEmpty())) {
				colorWiseIndex.clear();
				for(int i=0;i<graphInfo.getDataColLabels3().size();i++) {
					colorWiseIndex.add(i);
				}
			}
			else {
				if(graphInfo.getLovListForColor()!=null && !graphInfo.getLovListForColor().isEmpty()) {
					for(int i=0;i<graphInfo.getLovListForColor().size();i++) {
						colorWiseIndex.add(i);
					}
				}
				/*if(graphInfo.getGraphData().getRowList()!=null && !graphInfo.getGraphData().getRowList().isEmpty() ) {
					for(int i=0;i<graphInfo.getGraphData().getRowList().size();i++) {
						colorWiseIndex.add(i);
					}
				}
				else if(graphInfo.getGraphData().getColList()!=null && !graphInfo.getGraphData().getColList().isEmpty()) {
					for(int i=0;i<graphInfo.getGraphData().getColList().size();i++) {
						colorWiseIndex.add(i);
					}
				}*/
			}
		}
		graphInfo.setDisplayBarIndexList(colorWiseIndex);
	}

}
