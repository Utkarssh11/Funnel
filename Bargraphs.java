package com.elegantjbi.amcharts;


import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.velocity.app.event.implement.EscapeJavaScriptReference;
import org.json.simple.JSONObject;
import org.jsoup.parser.Parser;

import com.elegantjbi.amcharts.vo.Graphs;
import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.service.graph.GraphCommandNameList;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.util.AppConstants;
import com.elegantjbi.util.StringUtil;
import com.elegantjbi.vo.properties.kpi.TrendLineProperties;


public class Bargraphs {
	private Bargraphs() {}
	public static List<Graphs> graphsJson(GraphInfo graphInfo,int startIndex,int quantity){
		long startTime = System.currentTimeMillis();

		String rowLabel = graphInfo.getGraphData().getRowLabel();		
		List rowList = graphInfo.getGraphData().getRowList();		
		List dateRowList = graphInfo.getGraphData().getDaterowList();
		int rowListSize = rowList.size();
		boolean colLabelsName = false;
		//This is when non clustered and multiple measure along with col labels
		if(graphInfo.getGraphData().getColLabelsName() != null && !graphInfo.getGraphData().getColLabelsName().isEmpty())
			colLabelsName = true;	

		List precisionLabelList= new ArrayList();
		int dvTruncateCharLimit = 15;
		String type = null;
		double fillAlpha = 0;
		int colr=0;
		int lineAlpha = 0;
		int bulletAlpha = 0;
		int bulletBorderAlpha = 0;
		boolean isLine = false;
		int countmm=0;

		String bullet = null;
		boolean isLegendVisible = true;
		if(rowListSize == 0){
			rowListSize = 1;
			isLegendVisible =  false;
		}
		boolean isBarChart = false;//for barWidth
		if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
		{
			isBarChart = true;
		}
		boolean isMultipleMeasure = false;
		int multipleMeasures = graphInfo.getDataColLabels3().size();		
		int noOfMeasure = 1;
		int noOfYAxis = 1;
		String dataLabel = graphInfo.getGraphData().getDataLabel();
		List dataLabelList = new ArrayList<String>();
		List<String> originalDataList = new ArrayList<String>();
		List<String> originalDataLabelList = new ArrayList<String>();
		List<String> originalRowList = new ArrayList<String>();
		boolean isMultiMeasure = graphInfo.getDataColLabels3().size()>1 && (dataLabel.equalsIgnoreCase("Data") || dataLabel==null || dataLabel.equalsIgnoreCase("null"));
		
		if(isLegendVisible && multipleMeasures > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
		{
			isMultipleMeasure = true;
			if(graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))
				noOfMeasure = 1;
			else	
			noOfMeasure = graphInfo.getDataColLabels3().size();//Multiple Measures
			rowListSize*=noOfMeasure;
			
			for (int d=0;d < graphInfo.getDataColLabels3().size();d++) 
			{
				dataLabelList.add(d, graphInfo.getDataColLabels3().get(d).toString());
			}
			noOfYAxis = noOfMeasure;
			
			//Added code for Column Labels Special char($) (For Bug #12443)
			Map<String, String> colLabelsMapData = graphInfo.getGraphProperties().getColLabelsMap();			
			for (int i = 0; i < dataLabelList.size(); i++) {
				for (Entry<String, String> e : colLabelsMapData.entrySet()) {
					if(dataLabelList.get(i).equals(e.getKey())) {	// value to key
						originalDataList.add(e.getKey());	
						originalDataLabelList.add(e.getValue());
					}
				}
			}			
			if(originalDataList.isEmpty())
				originalDataList.addAll(dataLabelList);
			/*dataLabelList = new ArrayList();
			dataLabelList.addAll(originalDataList);*/
			
			if(null != rowLabel && rowLabel.equalsIgnoreCase("Legend")) {//For valueField when Stacked Vbar/HBar
				
				if(!rowList.isEmpty()) {
					Map<String, String> colLabelsMap = graphInfo.getGraphProperties().getColLabelsMap();
					for (int i = 0; i < rowList.size(); i++) {
						for (Entry<String, String> e : colLabelsMap.entrySet()) {
							if(rowList.get(i).equals(e.getKey()))	//bhavika
								originalRowList.add(e.getKey());
						}
					}
					if(!originalRowList.isEmpty()) {
						rowList = new ArrayList();
						rowList.addAll(originalRowList);
					}
				}
			}
			//Column Labels Special char($) end
		}
		if((graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			noOfYAxis = rowListSize;
		}
		

		List<String> bulletList = new ArrayList<String>();
		List<Integer> bulletSizeList = new ArrayList<Integer>();
		List<Integer> lineStyleList = new ArrayList<Integer>();
		List<Integer> lineThicknessList = new ArrayList<Integer>();
		List<String> borderColorList = new ArrayList<String>();
		List<Integer> borderWidthList = new ArrayList<Integer>();
		List<Integer> bulletStyleList = new ArrayList<Integer>();
		
		/*String[] barColor =new String[]{"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"
										,"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		//String[] barColor =new String[]{"rgb(141,170,203)","rgb(252,115,98)","rgb(187,216,84)","rgb(255,217,47)","rgb(102,194,150)","rgb(255, 148, 10)","rgb(148, 247, 244)"};
		String[] bulletColor =new String[]{"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"
										,"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		*/
		
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
		
		
		/*if(graphInfo.getGraphProperties().getDataValueProperties().getNumberFormat().isShowadAdjustedSuffixed())
		{	
			int prefix = graphInfo.getGraphProperties().getDataValueProperties().getNumberFormat().getAdjustedDigit();
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
			case 8:
				precisionLabel="Bn";
				break;
			}
		}*/
		
		//Precision label list
		List digitsAfterDecimal = new ArrayList();
		List yAxisTitleList = new ArrayList();
		int precisionLabelCounter=1;
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH) && (graphInfo.getGraphData().getRowLabel() != null &&  graphInfo.getGraphData().getColLabel() != null))
				|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend"))
				|| (graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			precisionLabelCounter = graphInfo.getDataColLabels3().size();
			if(graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
			{	
				for(int i=0;i<precisionLabelCounter;i++)
				{
					for(int j=0;j<rowList.size();j++)
					{	
						if(null!=graphInfo.getGraphProperties() && null!=graphInfo.getGraphProperties().getyAxisPropertiesMap()&&graphInfo.getGraphProperties().getyAxisPropertiesMap().size()>=precisionLabelCounter &&graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isShowadAdjustedSuffixed())
						{	
							int prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit();
							if(!graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible()  )
								prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getAdjustedDigit();
							String precisionLabel=null;
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
							}
										
							precisionLabelList.add(precisionLabel);
							
						}
						else
						{
							precisionLabelList.add("");
						}

						if(null!=graphInfo.getGraphProperties() && null!=graphInfo.getGraphProperties().getyAxisPropertiesMap()&&graphInfo.getGraphProperties().getyAxisPropertiesMap().size()>=precisionLabelCounter &&graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isShowadAdjustedSuffixed())
						{
						digitsAfterDecimal.add(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getNumberOfDigits());

						}
					}
					//Code for Data value Y Axis Title start
					yAxisTitleList.add(graphInfo.getDataColLabels3().get(i).toString());
					//Code for Data value Y Axis Title end
				}
			}
			else
			{
				for(int i=0;i<precisionLabelCounter;i++)
				{	
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isShowadAdjustedSuffixed())
					{	
						int prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit();
						if(!graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible())
							prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getAdjustedDigit();
						String precisionLabel=null;
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
						}
						precisionLabelList.add(precisionLabel);
					}
					else
					{
						precisionLabelList.add("");
					}
					digitsAfterDecimal.add(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getNumberOfDigits());
					
					//Code for Data value Y Axis Title start
					if(colLabelsName && graphInfo.getGraphData().getColLabelsName().size() > i && graphInfo.getGraphData().getColLabelsName().get(i) != null)
						yAxisTitleList.add(graphInfo.getGraphData().getColLabelsName().get(i).toString());
					else
						yAxisTitleList.add(graphInfo.getDataColLabels3().get(i).toString());
					//Code for Data value Y Axis Title end
				}
			}
		}
		else
		{
			for(int i=0;i<rowListSize;i++)
			{	
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().isShowadAdjustedSuffixed())
				{	
					int prefix = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getAdjustedDigit();
					String precisionLabel=null;
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
					}
					precisionLabelList.add(precisionLabel);
				}
				else
				{
					precisionLabelList.add("");
				}
				digitsAfterDecimal.add(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getNumberOfDigits());
				
				//Code for Data value Y Axis Title start
				if(colLabelsName)
				{
					if(graphInfo.getGraphData().getColLabelsName().size() < rowListSize)
						yAxisTitleList.add(graphInfo.getGraphData().getColLabelsName().get(0).toString());
					else
						yAxisTitleList.add(graphInfo.getGraphData().getColLabelsName().get(i).toString());
				}
					
				else
				{
					if(dataLabel != null)
						yAxisTitleList.add(dataLabel);
				}
				//Code for Data value Y Axis Title end
			}
		}
		List dataColList = new ArrayList();
		if(graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			precisionLabelCounter = graphInfo.getDataColLabels3().size();
			for(int i=0;i<precisionLabelCounter;i++)
			{	
				if(colLabelsName && graphInfo.getGraphData().getColLabelsName().size() > i && graphInfo.getGraphData().getColLabelsName().get(i) != null)
					dataColList.add(graphInfo.getGraphData().getColLabelsName().get(i).toString());
				else
					dataColList.add(graphInfo.getDataColLabels3().get(i).toString());
			}
		}
		else
		{
			for (int i = 0; i < rowListSize; i++) {
				if(colLabelsName)
				{
					if(graphInfo.getGraphData().getColLabelsName().size() < rowListSize)
						dataColList.add(graphInfo.getGraphData().getColLabelsName().get(0).toString());
					else
						dataColList.add(graphInfo.getGraphData().getColLabelsName().get(i).toString());
				}
					
				else
				{
					if(dataLabel != null)
						dataColList.add(dataLabel);
				}
			}
		}
		
		List<String> originalDataColList = new ArrayList<String>();
		if(!dataColList.isEmpty()) {
			Map<String, String> colLabelsMap = graphInfo.getGraphProperties().getColLabelsMap();
			for (int i = 0; i < dataColList.size(); i++) {
				for (Entry<String, String> e : colLabelsMap.entrySet()) {
					if(dataColList.get(i).equals(e.getValue()))
						originalDataColList.add(e.getKey());
				}
			}
		}
		if(originalDataColList.isEmpty())
			originalDataColList.addAll(dataColList);
		
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
			fillAlpha = 1;

			for (int k = 0; k < rowListSize; k++) {
				lineThicknessList.set(k,0);
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
		{
			type = "line";			
			fillAlpha = 0;
			switch(graphInfo.getGraphProperties().getGraphLineProperties().getType())
			{
			case 1:lineAlpha =1;bulletAlpha = 0; bulletBorderAlpha = 0;isLine=true;break;
			case 2:lineAlpha = 0;bulletAlpha = 1;bulletBorderAlpha = 1;break;
			case 3:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 0;type = "smoothedLine";break;
			case 4:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 0;type = "step";break;
			default:lineAlpha =1;bulletAlpha = 1;bulletBorderAlpha = 0;break;
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
						//.getGraphProperties().getGraphLineProperties().getGraphlinePropertiesList()
						
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
							if (l < lineStyleList.size()) {
								lineStyleList.set(l, dashLength);
								lineThicknessList.set(l, customLineThickness);
							}
							temp =l;
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
							if (l < lineStyleList.size()) {
								lineStyleList.set(l, dashLength);
								lineThicknessList.set(l, customLineThickness);
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
							borderColorList.set(i, barColor[graphInfo.getColorInfoList().get(i)%barColor.length]);//Changed for Bug #15447 barColor[i%barColor.length]);
						bulletStyleList.set(i,bulletStyle);
					}
				
				}
			}
			else
			{
				if(null != graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList() && graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().size() != 0)
				{
					
					int temp =0;
					
					for (int l = 0; l < graphInfo.getDataColLabels3().size(); l++) {

						if (graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList()
								.size() > l) {

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
							if(l < bulletList.size())
								bulletList.set(l%bulletList.size(), bullet);
							if(l < bulletSizeList.size())
								bulletSizeList.set(l%bulletSizeList.size(), bulletSize);
							if(l < bulletTypeArray.length)
							{
								bulletTypeArray[l] = bullet;
							}
							
							if(l < borderWidthList.size())
								borderWidthList.set(l%borderWidthList.size(), borderwidth);
							
							if(l < borderColorList.size())
								borderColorList.set(l%borderColorList.size(), bordercolor);
							
							if(l < bulletStyleList.size())
								bulletStyleList.set(l%bulletStyleList.size(),bulletStyle);
						}temp =l;
						}
						else {
							int bulletType = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getStyle());
							int bulletSize = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getThickness());
							String bordercolor = graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getBordercolor();
							int borderwidth = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getBorderwidth());
							int bulletStyle = Integer.parseInt(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getBorderstyle());
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
							switch(graphInfo.getGraphProperties().getGraphLineProperties().getGraphlinepointPropertiesList().get(temp).getBorderwidth())
							{
							case "-1":borderwidth = 0;break;
							}
							if(bulletList.size()-1 >= temp)
							{
								if(l < bulletList.size())
									bulletList.set(l%bulletList.size(), bullet);
								if(l < bulletSizeList.size())
									bulletSizeList.set(l%bulletSizeList.size(), bulletSize);
								if(l < bulletTypeArray.length)
								{
									bulletTypeArray[l] = bullet;
								}
								
								if(l < borderWidthList.size())
									borderWidthList.set(l%borderWidthList.size(), borderwidth);
								
								if(l < borderColorList.size())
									borderColorList.set(l%borderColorList.size(), bordercolor);
								
								if(l < bulletStyleList.size())
									bulletStyleList.set(l%bulletStyleList.size(),bulletStyle);
							}
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
			fillAlpha = 1;			

			for (int k = 0; k < rowListSize; k++) {
				lineThicknessList.set(k, 0);
			}
		}
		else if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
				|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
		{
			type = "line";			
			double getAreaTransparency = (double)graphInfo.getGraphProperties().getTranceperancy();
			fillAlpha = (100 - getAreaTransparency) / 100;

			bulletAlpha = 0;

		}

		int colorLength = barColor.length;

		int paginationIndex=startIndex+quantity;
		if(paginationIndex >= rowListSize)
			paginationIndex=rowListSize;
		if(rowListSize < quantity)
			paginationIndex=rowListSize;
		
		int k=0;
		//		if(startIndex+quantity > rowListSize)
		//		{
		//			paginationIndex=rowListSize;
		//			//quantity =rowListSize;
		//		}

		String colLabel = graphInfo.getGraphData().getColLabel();
		List colList = graphInfo.getGraphData().getColList();

		int colListSize = colList.size();
		
		if(colList.size() == 0 && graphInfo.getGraphData().getRowList().size() == 0)
			return new ArrayList<Graphs>();
		

		if(colListSize >1 && rowListSize == 1) //Row Is blank , Only Column and Measure 
			colListSize=1;

		//		if(colListSize >1 && rowListSize > 1) //Row Column and Measure
		//			colListSize=1;

		List dataList = graphInfo.getGraphData().getDataList();

		//Trend Line Start
		Map trendMAp = graphInfo.getGraphData().getTrendMap();
		boolean isTrend =false;
		int trendCount;

		int noOfTrendLines = 0;
		if(trendMAp != null)
			noOfTrendLines = trendMAp.size(); 
		List trendValue = new ArrayList();
		List trendColor = new ArrayList();
		List trendLineName = new ArrayList();
		List trendLineColoumn = new ArrayList();
		List trendLineThickness = new ArrayList();
		List trendLineStyle = new ArrayList();
		if(noOfTrendLines > 0)
		{
			isTrend=true;
			trendCount = noOfTrendLines;

			Map<Integer, TrendLineProperties> testMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
			for (Entry<Integer, TrendLineProperties> entry : testMap.entrySet()) {
				String[] splitString = ((String) entry.getValue().getTrendLineColumn()).split(",");
				trendValue.add(splitString[0]);
				trendColor.add(entry.getValue().getTrendLineColor());
				trendLineName.add(entry.getValue().getTrendLineName());//Name of the trend Line given by the user
				trendLineColoumn.add(entry.getValue().getTrendLineColumn());
				trendLineThickness.add(entry.getValue().getTrendLineThickness());
				trendLineStyle.add(entry.getValue().getTrendLineStyle());
			}
		}
		//trend Line End
		
		int inull=0;
		
		List valueList = new ArrayList<>();
		List<Graphs> graphsList = new ArrayList<Graphs>();
		
		String unEscapeHtml = "";
		
		//9 Apr 2019[for graph]
		List hideGraphsList = new ArrayList();
		Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();

		for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
		{
			if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)).toString().equals("false")) {
				hideGraphsList.add(i);
			}
		}
		
		//9 Apr 2019
		
		
			
		if(isLegendVisible && multipleMeasures > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
		{
			int[] val = new int[rowListSize];
			int valueAxisNumber = -1;
			
			int sizes=graphInfo.getGraphProperties().getyAxisPropertiesMap().size();
			int counter=0;
			List valueAxisVisiblityList= new ArrayList();
			
			// 9 Apr[p.p]
			List FinalGraphsList = new ArrayList();
			
			int rowSize = rowList.size();
			if(!graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend"))
			{
			for(int i=0;i<hideGraphsList.size();i++)
			{
				
				int startInd = (int) hideGraphsList.get(i)*rowSize;
				int endInd = (((int) hideGraphsList.get(i)+1)*rowSize);
					for(int j =startInd ;j<endInd;j++)
					{
						FinalGraphsList.add(j);//cahnges [p.p]
					}
					
			}
			hideGraphsList = new ArrayList();
			hideGraphsList.addAll(FinalGraphsList);
			
			}
		// 9 Apr [p.p]
			for(int i=0;i<sizes;i++)
			{
				
				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible()
						/*|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().isVisible()*/
						|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible())
				{
					for(int j=0;j<rowList.size();j++)
					{
						valueAxisVisiblityList.add("valueAxes"+counter);
					}
					counter++;
				}
				else
				{
										
					boolean sizeflag=false;
					for(int l=0;l<sizes;l++)
					{
						if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+l).getLineProperties().isVisible())
						{
							for(int j=0;j<rowList.size();j++)
							{
							valueAxisVisiblityList.add("valueAxes"+l);
							
							}
							sizeflag=true;
							break;
						}else{
							continue;
						}
						
					}
					if(!sizeflag)
					{
						for(int j=0;j<rowList.size();j++)
						{
						valueAxisVisiblityList.add("valueAxes0");
						}
					}
					counter++;
				
					
					
					
//					for(int j=0;j<rowList.size();j++)
//					{
//						valueAxisVisiblityList.add("valueAxes0");
//					}
//					counter++;
				}
			}
			
			
			
			/*for(int i=0;i<rowListSize;i++)
			{
				if((i%(rowListSize/multipleMeasures))==0)
				{
					valueAxisNumber++;
				}
				val[i] = valueAxisNumber;
			}*/
			if(graphInfo.getGraphData().getRowLabel()!= null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend"))//when more then one measure and non  clust
			{
				val = new int[1];
				val[0] = 0;
			}
			
			int index = 0; 
			int titleIndex = 0;
			int cnt = -1;
			for (int j = 0; j < rowListSize; j++) {
				
					/*if(dataList.get(i*rowListSize+j)!=null || rowListSize < quantity)
					{*/			
						/*if(k==startIndex || rowListSize < quantity)
						{

							if(paginationIndex==startIndex)
							
								return graphsList;*/
				
				if((j%(rowListSize/noOfMeasure))==0)
				{
					index=0;//Fetching appropriate ValueField when Multiple Measure 9 Apr [p.p]
					cnt++;
				}
				
				Graphs graphs = new Graphs();
				if((j%(rowListSize/noOfMeasure))==0 && j != 0)
				{
					graphs.setNewStack(true);// changes for removving measure functionality [p.p]
					//For $Y_AXIS_STACKED_TITLE$
					titleIndex++;
				}				
						if(!hideGraphsList.contains(j)) // 9 Apr add [p.p]
						{
							if(rowListSize==graphInfo.getDataColLabels3().size()) // added to get proper data when hide
								index=j;
							
							graphs.setShowHandOnHover(true);////feature req 13494
							if(null !=digitsAfterDecimal && !digitsAfterDecimal.isEmpty() && digitsAfterDecimal.size()>=j && digitsAfterDecimal.get(j) != null)
								graphs.setPrecision(Double.valueOf(digitsAfterDecimal.get(j).toString()));
							else
								graphs.setPrecision(0.0);
							if(graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend"))
							{
								graphs.setValueAxis("valueAxes"+val[0]);
							}
							else
							{
							graphs.setValueAxis(valueAxisVisiblityList.get(j).toString());//0
								if((graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
									&& isLegendVisible && multipleMeasures > 1 && graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isShowTotalValue())
								{
									graphs.setValueAxis("valueAxes"+titleIndex);//Added for feature request of Stack total
								}
							}
							
							/*if((j%(rowListSize/noOfMeasure))==0 && j != 0)
							{
								graphs.setNewStack(true);
								//For $Y_AXIS_STACKED_TITLE$
								titleIndex++;
							}*/

							//negative bar color
							if(!graphInfo.getGraphProperties().getNegativeBarColor().equalsIgnoreCase("#ff0000"))
							{
								graphs.setNegativeFillColors(graphInfo.getGraphProperties().getNegativeBarColor());
							}

							/*if((j%(rowListSize/noOfMeasure))==0)
							{
								index=0;//Fetching appropriate ValueField when Multiple Measure
								cnt++;
							}*/
							
							String formattedRowListValue = rowList.get(index).toString();
							//Added code for Bug #13406 start
							if(!dateRowList.isEmpty() && dateRowList.size() > index
									&& null != dateRowList.get(index) && !dateRowList.get(index).equals(AppConstants.NULL_DISPLAY_VALUE)) {
								//String tmp2 = dateRowList.get(index).toString();
								String stringFormat;
								stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
								stringFormat = stringFormat.replaceAll("&#39;", "'");
								Calendar cal = Calendar.getInstance();
								Date axisDate = new Date();
								axisDate = (Date) dateRowList.get(index);
								cal.setTime(axisDate);
								stringFormat=stringFormat.trim();
								formattedRowListValue = new SimpleDateFormat(stringFormat).format(cal.getTime());
							}//Added code for Bug #13406 end
							
							//Data value start
							if(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible())
							{
								String labelPosition = graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getPosition();
								if(labelPosition.equalsIgnoreCase("Top"))
								{
									labelPosition = "top";
								}
								if(labelPosition.equalsIgnoreCase("Bottom"))
								{
									labelPosition = "inside";
								}
								if(labelPosition.equalsIgnoreCase("Top") &&
										 (graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH ))
								{
										labelPosition = "bottom";
								}
								
								if(labelPosition.equalsIgnoreCase("Center"))
								{
									labelPosition = "middle";
								}
								
								//for bug 12193
								if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
								{
									if(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getPosition().equalsIgnoreCase("Top"))
									{
										labelPosition = "right";
										if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
											labelPosition = "left";
									}
								}
								///for bug 12193
								
								//Vertical 
								graphs.setLabelRotation(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getRotationAngle());
								graphs.setLabelOffset(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getLabelOffset());
								
								unEscapeHtml = graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getDataValuePointFormatText();
								String dataVAlues = unescapeHtml(unEscapeHtml);

								dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_VALUE,"[[dvTruncatedLabel]]");//Changed from truncatedLabel to dvTruncatedLabel for feature request 15092
								
								//Added for NeGD feature request 15092 start [8 Aug 2019]
								if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
									|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
									|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
								{
									switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
									{
									case "auto":
										dvTruncateCharLimit = 15;
										break;
									case "custom":
										dvTruncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
										break;
									}
									if (formattedRowListValue.length() > dvTruncateCharLimit) {
										formattedRowListValue = formattedRowListValue.substring(0, dvTruncateCharLimit)+"..";
									}
								}
								//Added for NeGD feature request end [8 Aug 2019]
								
								dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,formattedRowListValue);
								if(dataColList != null && !dataColList.isEmpty())
								{								
									if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
									{
									if(dataColList.get(index) != null && dataColList.get(index).toString().equalsIgnoreCase("data"))
										dataVAlues = dataVAlues.replace(formattedRowListValue,"[[zaxisvalue"+graphInfo.getDataColLabels3().get(index).toString().replaceAll("[^\\s\\w]*","")+"]]");
									else
										dataVAlues = dataVAlues.replace(formattedRowListValue,"[[zaxisvalue"+originalDataColList.get(index).toString().replaceAll("[^\\s\\w]*","")+"]]");
									}
								}
								//X_AXIS_TITLE start
								if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxisTitle]]");
								else
									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"");
								//X_AXIS_TITLE end
								
								//Z_AXIS_TITLE start
								
								if(isLegendVisible)
								{
									if(!(rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
									{/*
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"");
									}
									else
									{*/
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,StringUtil.replaceSpecialCharWithHTMLEntity(rowLabel));
									}
								}
								/*else
								{
									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"");
								}*/
								//Z_AXIS_TITLE end
								
								if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH || 
										graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
								{
									
									String yaxisStackedValue = index+(rowList.get(index).toString()+originalDataList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal" +titleIndex+ "]]");
									dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[Abs"+yaxisStackedValue+"]]");
									dataVAlues = dataVAlues.replace("[[Abs"+yaxisStackedValue+"]]", "[[Abs"+yaxisStackedValue+"]]"+precisionLabelList.get(j));
									//dataVAlues = dataVAlues.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
									
									if(colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
									{
										dataVAlues = dataVAlues.replace("[[AbsrealTotal" +titleIndex+ "]]", "[[AbsrealTotal]]"); 
												//StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal]]");
										dataVAlues = dataVAlues.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
										if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
										{
											if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
												dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
											else
												dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,originalDataLabelList.get(j).toString());//"[[yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
										}
										//showing multiple measure value on mouseover & datavalue
										countmm = 0;
										if(isMultiMeasure)
										{
											for(int m=0; m<multipleMeasures;m++)
											{												
													++countmm;
													yaxisStackedValue = (graphInfo.getDataColLabels3().get(m).toString()+originalDataList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_STACKED_VALUE"+countmm+"$", "[[Abs"+yaxisStackedValue+"]]"+precisionLabelList.get(j));
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_TITLE"+countmm+"$",originalDataLabelList.get(m).toString());//"[[yaxisTitle"+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","")+"]]");
												
											}
										}else {
											if(multipleMeasures>1 && (dataLabel!=null || dataLabel!="Data" || dataLabel!="data" || dataLabel!="")) {
												
												for (int m = 0; m < multipleMeasures; m++) {
													++countmm;
													yaxisStackedValue = index+(rowList.get(index).toString()
															+ graphInfo.getDataColLabels3().get(m).toString()).replaceAll("[^\\s\\w]*", "");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_STACKED_VALUE" + countmm + "$",
															"[[Abs" + yaxisStackedValue + "]]" + precisionLabelList.get(j));
													/*dataVAlues = StringUtil.replace(dataVAlues, "$Y-AXIS_TITLE" + countmm + "$",
															"[[yaxisTitle" + graphInfo.getDataColLabels3().get(m).toString()
																	.replaceAll("[^\\s\\w]*", "") + "]]");*/
													dataVAlues = StringUtil.replace(dataVAlues, "$Y-AXIS_TITLE" + countmm + "$",
															originalDataLabelList.get(m).toString());


												}
											}
										}
										
										
										/*if(yAxisTitleList.get(j) != null && yAxisTitleList.get(j).toString().equalsIgnoreCase("data"))
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,graphInfo.getDataColLabels3().get(j).toString());
										else
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,yAxisTitleList.get(j).toString());*/
									}
									else
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal" +titleIndex+ "]]");
										dataVAlues = dataVAlues.replace("[[AbsrealTotal" +titleIndex+ "]]", "[[AbsrealTotal" +titleIndex+ "]]"+precisionLabelList.get(j));
										dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_TITLE,originalDataLabelList.get(titleIndex).toString());// "[[yAxisTitle" + titleIndex + "]]");
									}
								}
								else
								{
									String yaxisValue = (rowList.get(index).toString()+dataLabelList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[[Abs"+yaxisValue+"]]");
									dataVAlues = dataVAlues.replace("[[Abs"+yaxisValue+"]]", "[[Abs"+yaxisValue+"]]"+precisionLabelList.get(j));
									//dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsrealTotal]]");
									if(dataLabel != null && !dataLabel.equalsIgnoreCase("null"))
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,dataLabel);
										
										countmm = 0;
										if(isMultiMeasure)
										{
											for(int m=0; m<multipleMeasures;m++)
											{											
													++countmm;
													yaxisValue = (graphInfo.getDataColLabels3().get(m).toString()+dataLabelList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_VALUE"+countmm+"$", "[[Abs"+dataLabelList+"]]"+precisionLabelList.get(j));
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_TITLE"+countmm+"$",originalDataLabelList.get(m).toString());//graphInfo.getDataColLabels3().get(m).toString());
													
												
											}
										}
										
									}
									else
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"");
								}
								
								if(dataVAlues!=null)
								dataVAlues = Parser.unescapeEntities(dataVAlues, false);		
								graphs.setLabelPosition(labelPosition);
								graphs.setLabelText(dataVAlues);//values on bar
								graphs.setColor(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor());//values on bar color
								graphs.setFontSize(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontSize());
								graphs.setShowAllValueLabels(true);

							}
							if((graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)&& graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isShowTotalValue()) {
								graphs.setColor(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor());//values on bar color
								graphs.setFontSize(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontSize());
							}
							//Data value end

							//Mouse over value start
							if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable())
							{
								String mouseOverString = graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFormatText();
								if(mouseOverString!=null)
								mouseOverString=mouseOverString.replace("&lt;/br&gt", "");
								mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[[truncatedLabel]]");
							
								mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,formattedRowListValue);
							
								if(dataColList != null && !dataColList.isEmpty())
								{
									if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
									{
									if(dataColList.get(index) != null && dataColList.get(index).toString().equalsIgnoreCase("data"))
										mouseOverString = mouseOverString.replace(formattedRowListValue,"[[zaxisvalue"+graphInfo.getDataColLabels3().get(index).toString().replaceAll("[^\\s\\w]*","")+"]]");
									else
										if(null!=originalDataLabelList && !originalDataLabelList.isEmpty())
										mouseOverString = mouseOverString.replace(formattedRowListValue,originalDataLabelList.get(index).toString());//"[[zaxisvalue"+originalDataColList.get(index).toString().replaceAll("[^\\s\\w]*","")+"]]");
									}
								}
								//X_AXIS_TITLE start
								if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
									mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[xaxisTitle]]");
								else
									mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"");
								//X_AXIS_TITLE end
								
								//Z_AXIS_TITLE start
								if(isLegendVisible)
								{
									if(!(rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
									{/*
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"");
									}
									else
									{*/
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,StringUtil.replaceSpecialCharWithHTMLEntity(rowLabel));
									}
								}
							/*	else
								{
									mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"");
								}*/
								//Z_AXIS_TITLE end

								if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH || 
										graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH ||
												graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH ||
										graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)//non clus multi value
								{
									
									String yaxisStackedValue = index+(rowList.get(index).toString()+originalDataList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
									mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal"+ titleIndex + "]]");
									mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[Abs"+yaxisStackedValue+"]]");
									mouseOverString = mouseOverString.replace("[[Abs"+yaxisStackedValue+"]]", "[[Abs"+yaxisStackedValue+"]]"+precisionLabelList.get(j));
									mouseOverString = mouseOverString.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
									
									
									countmm = 0;
									if(isMultiMeasure){										
										for(int m=0; m<multipleMeasures;m++) {
												++countmm;
												yaxisStackedValue = (graphInfo.getDataColLabels3().get(m).toString()+originalDataList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
												mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_STACKED_VALUE"+countmm+"$", "[[Abs"+yaxisStackedValue+"]]"+precisionLabelList.get(j));																				
										}
									}else {
										if(multipleMeasures>1 && (dataLabel!=null || dataLabel!="Data" || dataLabel!="data" || dataLabel!="")) {
											for(int m=0; m<multipleMeasures;m++) {
												++countmm;
												yaxisStackedValue = index+(rowList.get(index).toString() + graphInfo.getDataColLabels3().get(m).toString()).replaceAll("[^\\s\\w]*", "");
												mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_STACKED_VALUE"+countmm+"$", "[[Abs"+yaxisStackedValue+"]]"+precisionLabelList.get(j));																				
										}
										}
									}
									
									if(colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
									{
										mouseOverString = mouseOverString.replace("[[AbsrealTotal" +titleIndex+ "]]", "[[AbsrealTotal]]"); 
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal]]");
										mouseOverString = mouseOverString.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
										if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
										{
											if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
												mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
											else		
												if(null!=originalDataLabelList && !originalDataLabelList.isEmpty())													
												mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,originalDataLabelList.get(j).toString());//"[[yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
												
											countmm = 0;
											if(isMultiMeasure)
											{
												for(int m=0; m<multipleMeasures;m++)
												{
														++countmm;
														mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE"+countmm+"$",originalDataLabelList.get(m).toString());//"[[yaxisTitle"+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","")+"]]");
													
												}
											}else {
												if(multipleMeasures>1 && (dataLabel!=null || dataLabel!="Data" || dataLabel!="data" || dataLabel!="")) {
													
													for (int m = 0; m < multipleMeasures; m++) {
														++countmm;
														if(null!=originalDataLabelList && !originalDataLabelList.isEmpty())
														mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE" + countmm + "$",
																 originalDataLabelList.get(m).toString() );

													}
												}
											}
											
										}
										
									/*	if(yAxisTitleList.get(j) != null && yAxisTitleList.get(j).toString().equalsIgnoreCase("data"))
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,graphInfo.getDataColLabels3().get(j).toString());
										else
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,yAxisTitleList.get(j).toString());*/
									}
									else
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsrealTotal"+ titleIndex + "]]");
										mouseOverString = mouseOverString.replace("[[AbsrealTotal"+ titleIndex + "]]", "[[AbsrealTotal"+ titleIndex + "]]"+precisionLabelList.get(j));
										mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_TITLE,originalDataLabelList.get(titleIndex).toString());// "[[yAxisTitle" + titleIndex + "]]");
										countmm = 0;
										if(isMultiMeasure)
										{
											for(int m=0; m<multipleMeasures;m++)
											{
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE"+countmm+"$",originalDataLabelList.get(m).toString());//"[[yaxisTitle"+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","")+"]]");
												
											}
										}else {
											if(multipleMeasures>1 && (dataLabel!=null || dataLabel!="Data" || dataLabel!="data" || dataLabel!="")) {
												
												for (int m = 0; m < multipleMeasures; m++) {
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE" + countmm + "$",
															 originalDataLabelList.get(m).toString() );

												}
											}
										}
									}
								}
								else
								{
									String yaxisValue = (rowList.get(index).toString()+dataLabelList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
									mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[[Abs"+yaxisValue+"]]");
									mouseOverString = mouseOverString.replace("[[Abs"+yaxisValue+"]]", "[[Abs"+yaxisValue+"]]"+precisionLabelList.get(j));
									//mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsrealTotal]]");
									if(dataLabel != null && !dataLabel.equalsIgnoreCase("null"))
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,dataLabel);
										countmm = 0;
										if(multipleMeasures>1)
										{
											for(int m=0; m<multipleMeasures;m++)
											{
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE"+countmm+"$",originalDataLabelList.get(m).toString());//graphInfo.getDataColLabels3().get(m).toString());
													yaxisValue = (graphInfo.getDataColLabels3().get(m).toString()+dataLabelList.get(cnt).toString()).replaceAll("[^\\s\\w]*","");
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_VALUE"+countmm+"$","[[Abs"+yaxisValue+"]]"+precisionLabelList.get(j));
												
											}
										}
										
									}
									else
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"");
								}
								graphs.setBalloonText(mouseOverString);
							}
							else
							{
								graphs.setBalloonText("");
							}
							if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isClusteredMouseOverTextEnable())
							{
								graphs.setBalloonText("[[Abs"+rowList.get(index).toString()+dataLabelList.get(cnt)+"]]");
							}
							if(!graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isClusteredMouseOverTextEnable() && !graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable())
							{
								graphs.setBalloonText("");
							}

							//Mouse over value end

							graphs.setFillColorsField("color");
							graphs.setType(type);
							graphs.setFillAlphas(fillAlpha);
							graphs.setLineAlpha(lineAlpha);
							graphs.setLineThickness(lineThicknessList.get(j));
							graphs.setBulletAlpha(bulletAlpha);
							//Bar Graph Transparency start 
							if(graphInfo.getGraphProperties().getTranceperancy()>0)
							{	 
								double transperency =graphInfo.getGraphProperties().getTranceperancy();
								double barTransparency = ((100-transperency)/100); 
								graphs.setFillAlphas(barTransparency);
							}
							//Bar Graph Transparency end

							List<String> fillColorsList = new ArrayList<String>();
							
							if(colr== rowListSize/noOfMeasure)
							{
								colr=0;
							}
							if(graphInfo.getColorInfoList() != null && j < graphInfo.getColorInfoList().size())
								fillColorsList.add(barColor[graphInfo.getColorInfoList().get(j)%colorLength]);
							else
								fillColorsList.add(barColor[colr%colorLength]);

							String tmp = rowList.get(index).toString();
							//if(isLegendVisible){
								
								//Added code for Bug #13406 start
								if(!dateRowList.isEmpty() && dateRowList.size() > index
										&& null != dateRowList.get(index) && !dateRowList.get(index).equals(AppConstants.NULL_DISPLAY_VALUE)) {
									//String tmp2 = dateRowList.get(index).toString();
									String stringFormat;
									stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
									stringFormat = stringFormat.replaceAll("&#39;", "'");
									Calendar cal = Calendar.getInstance();
									Date axisDate = new Date();
									axisDate = (Date) dateRowList.get(index);
									cal.setTime(axisDate);
									stringFormat=stringFormat.trim();
									tmp = new SimpleDateFormat(stringFormat).format(cal.getTime());
								}//Added code for Bug #13406 end
								
								switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
								{
								case "auto":
									//tmp = rowList.get(index).toString();
									int truncateCharLimitAuto = 15;
									if (tmp.length() > truncateCharLimitAuto)
										tmp = tmp.substring(0, truncateCharLimitAuto)+"..";
									break;
								case "custom":
									//tmp = rowList.get(index).toString();
									int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
									if (tmp.length() > truncateCharLimit)
										tmp = tmp.substring(0, truncateCharLimit)+"..";
									break;
								/*default:
									tmp = rowList.get(index).toString();
									break;*/

								}
								
								if(colLabelsName && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
									&& graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))
										tmp = graphInfo.getGraphData().getColLabelsName().get(index).toString();
								if(tmp != null)
									tmp = Parser.unescapeEntities(tmp, false);
								graphs.setTitle(tmp);
							
								if(null != rowLabel && rowLabel.equalsIgnoreCase("Legend") && null != originalRowList && originalRowList.size() > index && null != originalDataList && originalDataList.size() > cnt)
									graphs.setValueField((originalRowList.get(index).toString()+originalDataList.get(cnt).toString()));//.replaceAll("[^\\s\\w]*",""));
								else
								{
									if(null != originalDataList && originalDataList.size() > cnt)//Added for show preview
										graphs.setValueField((rowList.get(index).toString()+originalDataList.get(cnt).toString()));//.replaceAll("[^\\s\\w]*",""));
									else
										graphs.setValueField((rowList.get(index).toString()+dataLabelList.get(cnt).toString()));//.replaceAll("[^\\s\\w]*",""));
								}
								graphs.setDescriptionField("Abs"+rowList.get(index).toString()+dataLabelList.get(cnt));
								index++;
							/*}else{
								graphs.setValueField(graphInfo.getGraphData().getDataLabel());
							}*/

														
							/*if(graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
								|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
							{*/	 
								if(graphInfo.getGraphProperties().getBarProperties().getType() == 2)
								{
									//graphJson.setAngle(30);
									//graphJson.setDepth3D(30);
									graphs.setTopRadius("1");
								}
								else if(graphInfo.getGraphProperties().getBarProperties().getType() == 3)
								{
									//	graphJson.setAngle(30);
									//	graphJson.setDepth3D(30);
									graphs.setTopRadius("0");
								}

								//Bar Gradient Start.
								if (graphInfo.getGraphProperties().getBarProperties().getGradient().isVisible())
								{
									fillColorsList.add(graphInfo.getGraphProperties().getBarProperties().getGradient().getColor());
									graphs.setFillColorsField("");
									if(graphInfo.getGraphProperties().getBarProperties().getGradient().isTransparent()){
										graphs.setFillAlphas(0.50);
									}
								}
								//Bar Gradient End
								//Corner radius
								if(graphInfo.getGraphProperties().getBarProperties().getCornerRadius() > 0)
								{
									graphs.setCornerRadiusTop(graphInfo.getGraphProperties().getBarProperties().getCornerRadius());
									//graphJson.setAngle(0);
									//graphJson.setDepth3D(0);
								}
								
								//Bar width
								if(graphInfo.getGraphProperties().getBarProperties().getBarWidth() != 100 && isBarChart)
								{
									double barWidth = (double)graphInfo.getGraphProperties().getBarProperties().getBarWidth()/100;
									graphs.setColumnWidth(barWidth);//between 0-1
								}
								//Bar width
								
								//Bar border start
								if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible()
										&& !graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderStyle().equalsIgnoreCase("none"))
								{
									graphs.setLineAlpha(1);
									graphs.setLineThickness(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderWidth());
									graphs.setLineColor(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderColor());
									int barBorderStyle = 0;
									switch (graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderStyle()) {
									case "none":
										graphs.setLineAlpha(0);
										graphs.setLineThickness(0);
										break;
									case "solid":
										barBorderStyle = 0;
										break;
									case "dashed":
										barBorderStyle = 5;
										break;
									case "dotted":
										barBorderStyle = 2;
										break;
									}
									graphs.setDashLength(barBorderStyle);
								}
								else
								{
									graphs.setLineAlpha(0);
									graphs.setLineThickness(0);
									//graphs.setLineColor();
									//graphs.setDashLength();
								}
								//Bar border end
							//}
							String legendColor=barColor[graphInfo.getColorInfoList().get(j)%barColor.length];
							switch(graphInfo.getGraphProperties().getGraphLineProperties().getType())
							{
							case 2:legendColor = bulletColor[graphInfo.getColorInfoList().get(j)%bulletColor.length];break;
							}
							graphs.setLegendColor(legendColor);//Legend Icon(Marker) color
							//graphs.setLegendColor(barColor[graphInfo.getColorInfoList().get(j)%barColor.length]);//Legend Icon(Marker) color
							graphs.setFillColors(fillColorsList);
							graphs.setLabelFunction("");
							graphsList.add(graphs);
							startIndex++;

						//}
					/*}else{
						continue;
					}*/
				
				colr++;
				k++;			
						}
						}
			
			//Trend Start for( bug 11562)
			if(isTrend)//trend
			{
				isTrend=false;
				String label;
				for(int c=0;c<noOfTrendLines;c++)
				{
					Graphs graphs = new Graphs();
					graphs.setShowHandOnHover(true);//feature req 13494
					graphs.setLineThickness((int)trendLineThickness.get(c));
					graphs.setLineAlpha(1);
					graphs.setType("line");
					graphs.setLabelText("[[TrendLabel" + c+ "]]" );
					graphs.setFillAlphas(0);
					graphs.setValueField(trendLineName.get(c).toString()+"trend");
					graphs.setDescriptionField("Abs"+trendLineName.get(c).toString()+"trend");
					graphs.setLineColor(trendColor.get(c).toString());
					graphs.setVisibleInLegend(false);
					graphs.setValueAxis("valueAxes1");

					if(graphInfo.getDataColLabels3().size() > 1)
					{
						String[] splitString = trendLineColoumn.get(c).toString().split(",");
						List measure = new ArrayList(graphInfo.getDataColLabels3());
						if(measure.contains(splitString[0]))
						{
							int occurenceIndex= measure.indexOf(splitString[0]);
							graphs.setValueAxis("valueAxes"+occurenceIndex);
						}
					}


					//line style
					int lineStyle = Integer.valueOf(trendLineStyle.get(c).toString());
					int trendDashLength = 0;
					switch (lineStyle) {
					case 0:
						trendDashLength = 0;
						break;
					case 1:
						trendDashLength = 7;
						break;
					case 2:
						trendDashLength = 3;
						break;
						/*case 3:
						trendDashLength = 1;
						break;*/
					}
					graphs.setDashLength(trendDashLength);//year quater
					graphs.setFontSize(10);
					if(!graphInfo.getGraphData().getColLabel().equalsIgnoreCase(trendValue.get(c).toString()) && isLegendVisible==false)
					{
						graphs.setLineAlpha(0);
						graphs.setLineThickness(0);
						graphs.setLabelText("");
					}
					graphsList.add(graphs);
				}

			}
			
			
			
			return graphsList;
		}
		else
		{
			
			
			int[] val = new int[rowListSize];
			int valueAxisNumber = -1;
			
			int sizes=graphInfo.getGraphProperties().getyAxisPropertiesMap().size();
			int counter=0;
			List valueAxisVisiblityList= new ArrayList();
			int measurerowlistsize=rowListSize;
			
			if(multipleMeasures > 1 && graphInfo.getGraphData().getRowLabel()!=null && (graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend") || graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Data")))
				measurerowlistsize=1;

			for(int i=0;i<sizes;i++)
			{
				
				if(null !=graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i) && graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLineProperties().isVisible()
						/*|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getyAxisTitleTrendProperties().isVisible()*/
						|| graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible() /*|| 
						graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible()*/)
				{
					for(int j=0;j<measurerowlistsize;j++)
					{
					valueAxisVisiblityList.add("valueAxes"+counter);
					counter++;
					}
				}
				else
				{
					
					boolean sizeflag=false;
					for(int l=0;l<sizes;l++)
					{
						if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+l).getLineProperties().isVisible())
						{
							for(int j=0;j<measurerowlistsize;j++)
							{
							valueAxisVisiblityList.add("valueAxes"+l);
							
							}
							sizeflag=true;
							break;
						}else{
							continue;
						}
						
					}
					if(!sizeflag)
						for(int j=0;j<measurerowlistsize;j++)
						{
							valueAxisVisiblityList.add("valueAxes0");						
						}
						
					counter++;
				}
			}

			for (int j = 0; j < rowListSize; j++) {				
							if((j==startIndex || rowListSize < quantity) && !hideGraphsList.contains(j))
							{

								if(paginationIndex==startIndex
										&& graphInfo.getGraphType() != GraphConstants.STACKED_VBAR_GRAPH
										&& graphInfo.getGraphType() != GraphConstants.STACKED_HBAR_GRAPH)
									return graphsList;

								Graphs graphs = new Graphs();
								graphs.setShowHandOnHover(true);
								
								String desLabel = "";
								String dvDesLabel = "";//Added for NeGD feature request 15092 [13 Aug 2019]
								
								if(isLegendVisible){
									
									desLabel = "Abs"+j+rowList.get(j).toString().replaceAll("[^\\s\\w]*","");									
									dvDesLabel = "AbsDv"+j+rowList.get(j).toString().replaceAll("[^\\s\\w]*","");									
								}else{
									desLabel = "Abs"+j+graphInfo.getGraphData().getDataLabel().replaceAll("[^\\s\\w]*","");
									dvDesLabel = "AbsDv"+j+graphInfo.getGraphData().getDataLabel().replaceAll("[^\\s\\w]*","");
								}
								if(digitsAfterDecimal.get(j) != null)
									graphs.setPrecision(Double.valueOf(digitsAfterDecimal.get(j).toString()));
								else
									graphs.setPrecision(0.0);
								if(colr== rowListSize/noOfMeasure)
								{
								colr=0;
								}
								//graphs.setValueAxis("valueAxes"+colr);
								graphs.setValueAxis(valueAxisVisiblityList.get(j).toString());
								//negative bar color
								if(!graphInfo.getGraphProperties().getNegativeBarColor().equalsIgnoreCase("#ff0000"))
								{
									graphs.setNegativeFillColors(graphInfo.getGraphProperties().getNegativeBarColor());
								}
								
								//Data value start
								if(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible())
								{
									String labelPosition = graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getPosition();
									if(labelPosition.equalsIgnoreCase("Top"))
									{
										labelPosition = "top";
									}
									if(labelPosition.equalsIgnoreCase("Bottom"))
									{
										labelPosition = "inside";
									}
									if(labelPosition.equalsIgnoreCase("Center"))
									{
										labelPosition = "middle";
									}
									if(labelPosition.equalsIgnoreCase("Top") &&
											 (graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH ||
												graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
												graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH ))
									{
											labelPosition = "bottom";
									}
									
									//for bug 12193
									if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)
									{
										if(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getPosition().equalsIgnoreCase("Top"))
										{
											labelPosition = "right";
											if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
												labelPosition = "left";
										}
									}
									///for bug 12193
									
									//Vertical 
									graphs.setLabelRotation(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getRotationAngle());
									graphs.setLabelOffset(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getLabelOffset());
									
									//String source = "The less than sign (<) and ampersand (&) must be escaped before using them in HTML";
									unEscapeHtml = graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getDataValuePointFormatText();
									String dataVAlues = unescapeHtml(unEscapeHtml);

									dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_VALUE,"[[dvTruncatedLabel]]");//Changed from truncatedLabel to dvTruncatedLabel for feature request 15092
									if(isLegendVisible)
									{
										if(rowList != null && !rowList.isEmpty())
										{
											String formattedRowListValue = rowList.get(j).toString();
											//Added code for Bug #13406 start
											if(!dateRowList.isEmpty() && dateRowList.size() > j
													&& null != dateRowList.get(j) && !dateRowList.get(j).equals(AppConstants.NULL_DISPLAY_VALUE)) {
												//String tmp2 = dateRowList.get(index).toString();
												String stringFormat;
												stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
												stringFormat = stringFormat.replaceAll("&#39;", "'");
												Calendar cal = Calendar.getInstance();
												Date axisDate = new Date();
												axisDate = (Date) dateRowList.get(j);
												cal.setTime(axisDate);
												stringFormat=stringFormat.trim();
												formattedRowListValue = new SimpleDateFormat(stringFormat).format(cal.getTime());
											}//Added code for Bug #13406 end
											
											//Added for NeGD feature request 15092 start [8 Aug 2019]
											if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
												|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
												|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
											{
												switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
												{
												case "auto":
													dvTruncateCharLimit = 15;
													break;
												case "custom":
													dvTruncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
													break;
												}
												if (formattedRowListValue.length() > dvTruncateCharLimit) {
													formattedRowListValue = formattedRowListValue.substring(0, dvTruncateCharLimit)+"..";
												}
											}
											//Added for NeGD feature request end [8 Aug 2019]
											
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,formattedRowListValue);
											if(dataColList != null && !dataColList.isEmpty())
											{
												if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
												{
													if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
														dataVAlues = dataVAlues.replace(formattedRowListValue,"[[zaxisvalue"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
													else
														dataVAlues = dataVAlues.replace(formattedRowListValue,"[[zaxisvalue"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
												}
											}
											/*if(isLegendVisible && multipleMeasures > 1 && colLabelsName && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize)
											dataVAlues = dataVAlues.replace(rowList.get(j).toString(),graphInfo.getGraphData().getColLabelsName().get(j).toString());*/
										}
										else
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,"[[title]]");
									}
									//X_AXIS_TITLE start
									if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxisTitle]]");
									else
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"");
									//X_AXIS_TITLE end
									
									//Z_AXIS_TITLE start
									if(isLegendVisible)
									{
										if(!(rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
										{/*
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"");
										}
										else
										{*/
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,StringUtil.replaceSpecialCharWithHTMLEntity(rowLabel));
										}
									}
									/*else
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"");
									}*/
									//Z_AXIS_TITLE end
									
									//Y_AXIS_TITLE start
									if(dataColList != null && !dataColList.isEmpty())
									{
										if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
										else {
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
											
											countmm = 0;
											if(isMultiMeasure) {
												for(int m=0; m<multipleMeasures;m++) {
														++countmm;
														dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_TITLE"+countmm+"$",graphInfo.getDataColLabels3().get(m).toString());
												}
											}
											
										}
									}
									else
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"");
									}
									/*if(yAxisTitleList != null && !yAxisTitleList.isEmpty())
									{
										if(yAxisTitleList.get(j) != null && yAxisTitleList.get(j).toString().equalsIgnoreCase("data"))
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,graphInfo.getDataColLabels3().get(j).toString());
										else
											dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,yAxisTitleList.get(j).toString());
									}
									else
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"");
									}*/
									//Y_AXIS_TITLE end
	
									if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH)
											//&& (colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))))
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_STACKED_VALUE,"[["+dvDesLabel+"]]");
										dataVAlues = dataVAlues.replace("[["+dvDesLabel+"]]", "[["+dvDesLabel+"]]"+precisionLabelList.get(j));
										dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_VALUE, "[[AbsrealTotal]]");	
										dataVAlues = dataVAlues.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
										
										countmm = 0;
										if(isMultiMeasure)
										{										
												for(int m=0; m<multipleMeasures;m++) {
														++countmm;
													String dvs = "Abs"+m+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_STACKED_VALUE"+countmm+"$","[["+dvs+"]]");
													dataVAlues = dataVAlues.replace("[["+dvs+"]]", "[["+dvs+"]]"+precisionLabelList.get(j));														
												}
										}
										
									}
									else if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
									{
										//dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_PERCENTAGE_VALUE,"[[percentageValues" + j + "]]");
										dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_PERCENTAGE_VALUE, "[[percents]]"+"%");
										//dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_PERCENTAGE_VALUE, "[[realPercentageValues" + j + "]]"+"%");
										//dataVAlues = dataVAlues.replace("[[percentageValues" + j + "]]", "[[percentageValues" + j + "]]"+"%");
										/**
										 * 12/16/2d016 12:00 PM added by krishna for showing actual value and stacked value for percentage graph
										 */
										dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsyaxisValue"+j+"]]"+precisionLabelList.get(j));
										dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_VALUE, "[[AbsrealTotal]]"+precisionLabelList.get(j));
										
										countmm = 0;									
										if(isMultiMeasure) {
											for(int m=0; m<multipleMeasures;m++) {
													++countmm;
													dataVAlues = StringUtil.replace(dataVAlues, "$Y-AXIS_STACKED_PERCENTAGE_VALUE"+countmm+"$", "[[percents]]"+"%");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_STACKED_VALUE"+countmm+"$","[[AbsyaxisValue"+m+"]]"+precisionLabelList.get(j));
													
											}
										}
										
									}
									else
									{
										dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[["+dvDesLabel+"]]");
										dataVAlues = dataVAlues.replace("[["+dvDesLabel+"]]", "[["+dvDesLabel+"]]"+precisionLabelList.get(j));
										//dataVAlues = StringUtil.replace(dataVAlues, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsrealTotal]]");
										
										countmm = 0;
										if(isMultiMeasure)
										{
											for(int m=0; m<multipleMeasures;m++)	{					
													++countmm;
													String dvs = "AbsDv"+m+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","");
													dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_VALUE"+countmm+"$","[["+dvs+"]]"+precisionLabelList.get(j));																											
												
											}
										}
									}


									graphs.setLabelPosition(labelPosition);
									graphs.setLabelText(dataVAlues);//values on bar
									graphs.setColor(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontColor());//values on bar color
									graphs.setFontSize(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getFontSize());
									graphs.setShowAllValueLabels(true);

								}
								//Data value end

								//Mouse over value start
								if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable())
								{
									String mouseOverString = graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().getDataValueMouseOverFormatText();
									if(mouseOverString!=null){
									mouseOverString=mouseOverString.replace("&lt;/br&gt", "");
									mouseOverString=mouseOverString.replace("&lt;br&gt", "");
									}
									if(graphInfo.getDateFrequencyMap() != null && !graphInfo.getDateFrequencyMap().isEmpty()  ) {
										String dateLabel = graphInfo.getGraphData().getColLabel();
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[["+dateLabel+"]]");	
									}
									else {
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[[truncatedLabel]]");
									}
									
									if(isLegendVisible)
									{
										if(rowList != null && !rowList.isEmpty())
										{
											String formattedRowListValue = rowList.get(j).toString();
											//Added code for Bug #13406 start
											if(!dateRowList.isEmpty() && dateRowList.size() > j
													&& null != dateRowList.get(j) && !dateRowList.get(j).equals(AppConstants.NULL_DISPLAY_VALUE)) {
												//String tmp2 = dateRowList.get(index).toString();
												String stringFormat;
												stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
												stringFormat = stringFormat.replaceAll("&#39;", "'");
												Calendar cal = Calendar.getInstance();
												Date axisDate = new Date();
												axisDate = (Date) dateRowList.get(j);
												cal.setTime(axisDate);
												stringFormat=stringFormat.trim();
												formattedRowListValue = new SimpleDateFormat(stringFormat).format(cal.getTime());
											}//Added code for Bug #13406 end
											
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,formattedRowListValue);
											/*if(isLegendVisible && multipleMeasures > 1 && colLabelsName && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize)
												mouseOverString = mouseOverString.replaceAll(rowList.get(j).toString(),"[[zaxisvalue"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");*/
											
											if(dataColList != null && !dataColList.isEmpty())
											{
												if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
												{
												if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
													mouseOverString = mouseOverString.replace(formattedRowListValue,"[[zaxisvalue"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
												else
													mouseOverString = mouseOverString.replace(formattedRowListValue,"[[zaxisvalue"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
												}
											}
										}
										else
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,"[[title]]");
									}
									
									//Y_AXIS_TITLE start
									if(dataColList != null && !dataColList.isEmpty())
									{
										if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
										else
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*","")+"]]");
										
										
									}
									else
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"");
									}

									countmm = 0;
									if(isMultiMeasure)
									{	
										for(int m=0; m<multipleMeasures;m++)
										{
												++countmm;
												mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE"+countmm+"$", "[[yaxisTitle"+originalDataColList.get(m).toString().replaceAll("[^\\s\\w]*","")+"]]");
										}
									}
									//Y_AXIS_TITLE end
									
									//X_AXIS_TITLE start
									if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[xaxisTitle]]");
									else
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"");
									//X_AXIS_TITLE end
									
									//Z_AXIS_TITLE start
									if(isLegendVisible)
									{
										if(!(rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
										{/*
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"");
										}
										else
										{*/
											mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,StringUtil.replaceSpecialCharWithHTMLEntity(rowLabel));
										}
									}
								/*	else
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"");
									}*/
									//Z_AXIS_TITLE end

									if(graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH ||
											graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH)
										//&& (colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))))
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_STACKED_VALUE,"[["+desLabel+"]]");
										mouseOverString = mouseOverString.replace("[["+desLabel+"]]", "[["+desLabel+"]]"+precisionLabelList.get(j));
										mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_VALUE, "[[AbsrealTotal]]");	
										mouseOverString = mouseOverString.replace("[[AbsrealTotal]]", "[[AbsrealTotal]]"+precisionLabelList.get(j));
										
										
										 countmm =0;										
										if(isMultiMeasure) {
											for(int m=0; m<multipleMeasures;m++)
											{
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_STACKED_VALUE"+countmm+"$","[[Abs"+m+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","")+"]]"+precisionLabelList.get(j));
												
											}
										}
										
									}
									else if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
									{
										//mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_PERCENTAGE_VALUE,"[[percentageValues" + j + "]]");
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_STACKED_PERCENTAGE_VALUE, "[[percents]]"+"%");
										//mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_STACKED_PERCENTAGE_VALUE, "[[realPercentageValues" + j + "]]"+"%");
										//mouseOverString = mouseOverString.replace("[[percentageValues" + j + "]]", "[[percentageValues" + j + "]]"+"%");
										/**
										 * 12/16/2d016 12:00 PM added by krishna for showing actual value and stacked value for percentage graph
										 */
										mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsyaxisValue"+j+"]]"+precisionLabelList.get(j));
										mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_VALUE, "[[AbsrealTotal]]"+precisionLabelList.get(j));
										
										
										countmm =0;										
										if(isMultiMeasure) {
										for(int m=0; m<multipleMeasures;m++)
										{
												//if(!(graphInfo.getDataColLabels3().get(m).equals(originalDataColList.get(j))))	
												//{
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString, "$Y-AXIS_STACKED_PERCENTAGE_VALUE"+countmm+"$", "[[percents]]"+"%");
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_STACKED_VALUE"+countmm+"$","[[AbsyaxisValue"+m+"]]"+precisionLabelList.get(j));
												//}
											}
										}
									}
									else
									{
										mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[["+desLabel+"]]");
										mouseOverString = mouseOverString.replace("[["+desLabel+"]]", "[["+desLabel+"]]"+precisionLabelList.get(j));
										//mouseOverString = StringUtil.replace(mouseOverString, GraphCommandNameList.Y_AXIS_STACKED_VALUE, "[[AbsrealTotal]]"+precisionLabelList.get(j));
										
										
										countmm =0;										
										if(isMultiMeasure) {
											
										for(int m=0; m<multipleMeasures;m++)
										{
												//if(!(graphInfo.getDataColLabels3().get(m).equals(originalDataColList.get(j))))	
												//{
													++countmm;
													mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_VALUE"+countmm+"$","[[Abs"+m+graphInfo.getDataColLabels3().get(m).toString().replaceAll("[^\\s\\w]*","")+"]]"+precisionLabelList.get(j));													
												//}
											}
										}
									}
									graphs.setBalloonText(mouseOverString);
								}
								else
								{
									graphs.setBalloonText("");
								}
								if(graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isClusteredMouseOverTextEnable())
								{

									graphs.setBalloonText("[["+desLabel+"]]");


								}
								if(!graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isClusteredMouseOverTextEnable() && !graphInfo.getGraphProperties().getDataValueProperties().getDataValueMouseOver().isMouseOverTextEnable())
								{
									graphs.setBalloonText("");
								}

								//Mouse over value end

								//Area graph bullet on mouse over start
								if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
										||	graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
								{	 
									graphs.setBullet("round");
								}
								//Area graph bullet on mouse over end

								if(graphInfo.getGraphProperties().getGraphLineProperties().getType() != 3)//Added for Bug #12669									
								graphs.setFillColorsField("color");
								
								graphs.setType(type);
								graphs.setFillAlphas(fillAlpha);
								graphs.setLineAlpha(lineAlpha);
								graphs.setLineThickness(lineThicknessList.get(j));
								graphs.setBulletAlpha(bulletAlpha);
								//Bar Graph Transparency start 
								if(graphInfo.getGraphProperties().getTranceperancy()>0)
								{	 
									double transperency =graphInfo.getGraphProperties().getTranceperancy();
									double barTransparency = ((100-transperency)/100); 
									graphs.setFillAlphas(barTransparency);
								}
								//Bar Graph Transparency end

								List<String> fillColorsList = new ArrayList<String>();
								fillColorsList.add(barColor[graphInfo.getColorInfoList().get(j)%colorLength]);

								//Bug: 14488[Adv sort applied hence colorInfoList was comming something like 1,0,3....]
								//but as custom color is applied and no dim in ROW,first custom color is expected
								if(/*colorType == 1 && Commented for Bug #15407 */rowLabel == null && graphInfo.getDataColLabels3().size() == 1 && rowListSize == 1)
								{
									fillColorsList.clear();
									fillColorsList.add(barColor[0]);
								}
								
								if(isLegendVisible){
									
									String tmp = rowList.get(j).toString();
									//Added code for Bug #13406 start
									if(!dateRowList.isEmpty() && dateRowList.size() > j
											&& null != dateRowList.get(j) && !dateRowList.get(j).equals(AppConstants.NULL_DISPLAY_VALUE)) {
										//String tmp2 = dateRowList.get(j).toString(); 
										String stringFormat;
										stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
										stringFormat = stringFormat.replaceAll("&#39;", "'");
										Calendar cal = Calendar.getInstance();
										Date axisDate = new Date();
										axisDate = (Date) dateRowList.get(j);
										cal.setTime(axisDate);
										stringFormat=stringFormat.trim();
										tmp = new SimpleDateFormat(stringFormat).format(cal.getTime());
									}//Added code for Bug #13406 end
									
									switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
									{
									case "auto":
										//tmp = rowList.get(j).toString();
										int truncateCharLimitAuto = 15;
										if (tmp.length() > truncateCharLimitAuto)
											tmp = tmp.substring(0, truncateCharLimitAuto)+"..";
										break;
									case "custom":
										//tmp = rowList.get(j).toString();
										int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
										if (tmp.length() > truncateCharLimit)
											tmp = tmp.substring(0, truncateCharLimit)+"..";
										break;
									/*default:
										tmp = rowList.get(j).toString();
										break;*/

									}
									if(colLabelsName && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
										&& graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))
											tmp = graphInfo.getGraphData().getColLabelsName().get(j).toString();
									if(tmp != null)
										tmp = Parser.unescapeEntities(tmp, false);
									graphs.setTitle(tmp);
									
									//Bug- SDEVAPR20-599 remove replaceAll to allow special character

									graphs.setValueField(rowList.get(j).toString());//.replaceAll("[^\\s\\w]*",""));
									graphs.setDescriptionField("Abs"+rowList.get(j).toString());

								}else{
									graphs.setValueField(graphInfo.getGraphData().getDataLabel());//.replaceAll("[^\\s\\w]*",""));
									graphs.setDescriptionField("Abs"+graphInfo.getGraphData().getDataLabel());
								}

								if(graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH)
								{	 
									graphs.setBullet(bulletTypeArray[j%bulletTypeArray.length]);
									if(null == rowLabel)//Added IF to set proper color when 1D and Sort is applied (Ritu bug)
										graphs.setBulletColor(bulletColor[0]);
									else
										graphs.setBulletColor(bulletColor[graphInfo.getColorInfoList().get(j)%bulletColor.length]);
									graphs.setBulletSize(bulletSizeList.get(j));
									graphs.setDashLength(lineStyleList.get(j));
									graphs.setLineThickness(lineThicknessList.get(j));
									//graphs.setBulletBorderAlpha(bulletBorderAlpha);
									if(bulletStyleList.get(j) == 0)
									{
										graphs.setBulletBorderColor(borderColorList.get(j));
										graphs.setBulletBorderThickness(borderWidthList.get(j));
										if(borderWidthList.get(j) > 0)
										{
											if(isLine)
											{
												bulletBorderAlpha = 0;
											}
											else
											{	
												bulletBorderAlpha = 1;
											}

										}
										graphs.setBulletBorderAlpha(bulletBorderAlpha);//(1);
									}
									if(null == rowLabel)//Added IF to set proper color when 1D and Sort is applied (Ritu bug)
										graphs.setLineColor(barColor[0]);//Added for only Line Colors
									else
										graphs.setLineColor(barColor[graphInfo.getColorInfoList().get(j)%barColor.length]);//Added for only Line Colors
								}
								if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
										||	graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
								{	 
									int bulletType =  Integer
											.parseInt(graphInfo.getGraphProperties().getGraphArea().getAllPointStyle());
									// Switch case for line style (dash/dot)
									switch (bulletType ) {
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
									if (!graphInfo.getGraphProperties().getGraphArea().getAllborderstyle().equals("-1")) {
			
										graphs.setBullet(bullet);
										graphs.setBulletSize(graphInfo.getGraphProperties().getGraphArea().getAllPointWidth());
			
										graphs.setBulletBorderAlpha(1);
										graphs.setBulletBorderColor(
												graphInfo.getGraphProperties().getGraphArea().getAllbordercolor());
										graphs.setBulletColor(bulletColor[j % bulletColor.length]);
									} else {
										graphs.setBulletAlpha(0);
									}
									if(null == rowLabel)//Added for Bug #15407
									{
										graphs.setLineColor(barColor[0]);//Added for only Area Colors
									}

									// area gradient start
									if (graphInfo.getGraphProperties().getGraphArea().getGradient().isVisible())
									{
										fillColorsList.add(graphInfo.getGraphProperties().getGraphArea().getGradient().getColor());
										graphs.setFillColorsField("");
										if(graphInfo.getGraphProperties().getGraphArea().getGradient().isTransparent()){
											graphs.setFillAlphas(0.50);
										}
									}
									// area gradient end
									
									//Area border start
									if(graphInfo.getGraphProperties().getGraphArea().getBorderProperties().isVisible()
											&& !graphInfo.getGraphProperties().getGraphArea().getBorderProperties().getAllBorderStyle().equalsIgnoreCase("none"))
									{
										graphs.setLineAlpha(1);
										graphs.setLineThickness(graphInfo.getGraphProperties().getGraphArea().getBorderProperties().getAllBorderWidth());
										graphs.setLineColor(graphInfo.getGraphProperties().getGraphArea().getBorderProperties().getAllBorderColor());
										int areaBorderStyle = 0;
										switch (graphInfo.getGraphProperties().getGraphArea().getBorderProperties().getAllBorderStyle()) {
										case "none":
											graphs.setLineAlpha(0);
											graphs.setLineThickness(0);
											break;
										case "solid":
											areaBorderStyle = 0;
											break;
										case "dashed":
											areaBorderStyle = 5;
											break;
										case "dotted":
											areaBorderStyle = 2;
											break;
										}
										graphs.setDashLength(areaBorderStyle);
									}
									else
									{
										graphs.setLineAlpha(0);
										graphs.setLineThickness(0);
										//graphs.setLineColor();
										//graphs.setDashLength();
									}
									//Area border end
								}
								if(graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH)
								{	 
									if(graphInfo.getGraphProperties().getBarProperties().getType() == 2)
									{
										//graphJson.setAngle(30);
										//graphJson.setDepth3D(30);
										graphs.setTopRadius("1");
									}
									else if(graphInfo.getGraphProperties().getBarProperties().getType() == 3)
									{
										//	graphJson.setAngle(30);
										//	graphJson.setDepth3D(30);
										graphs.setTopRadius("0");
									}

									//Bar Gradient Start.
									if (graphInfo.getGraphProperties().getBarProperties().getGradient().isVisible())
									{
										fillColorsList.add(graphInfo.getGraphProperties().getBarProperties().getGradient().getColor());
										graphs.setFillColorsField("");
										if(graphInfo.getGraphProperties().getBarProperties().getGradient().isTransparent()){
											graphs.setFillAlphas(0.50);
										}
									}
									//Bar Gradient End
									//Corner radius
									if(graphInfo.getGraphProperties().getBarProperties().getCornerRadius() > 0)
									{
										graphs.setCornerRadiusTop(graphInfo.getGraphProperties().getBarProperties().getCornerRadius());
										//graphJson.setAngle(0);
										//graphJson.setDepth3D(0);
									}
									
									//Bar width
									if(graphInfo.getGraphProperties().getBarProperties().getBarWidth() != 100 && isBarChart)
									{
										double barWidth = (double)graphInfo.getGraphProperties().getBarProperties().getBarWidth()/100;
										graphs.setColumnWidth(barWidth);//between 0-1
									}
									//Bar width
									
									//Bar border start
									if(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().isVisible()
											&& !graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderStyle().equalsIgnoreCase("none"))
									{
										graphs.setLineAlpha(1);
										graphs.setLineThickness(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderWidth());
										graphs.setLineColor(graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderColor());
										int barBorderStyle = 0;
										switch (graphInfo.getGraphProperties().getBarProperties().getBorderProperties().getAllBorderStyle()) {
										case "none":
											graphs.setLineAlpha(0);
											graphs.setLineThickness(0);
											break;
										case "solid":
											barBorderStyle = 0;
											break;
										case "dashed":
											barBorderStyle = 5;
											break;
										case "dotted":
											barBorderStyle = 2;
											break;
										}
										graphs.setDashLength(barBorderStyle);
									}
									else
									{
										graphs.setLineAlpha(0);
										graphs.setLineThickness(0);
										//graphs.setLineColor();
										//graphs.setDashLength();
									}
									//Bar border end
								}
								String legendColor=barColor[graphInfo.getColorInfoList().get(j)%barColor.length];
								switch(graphInfo.getGraphProperties().getGraphLineProperties().getType())
								{
								case 2:legendColor = bulletColor[graphInfo.getColorInfoList().get(j)%bulletColor.length];break;
								}
								graphs.setLegendColor(legendColor);//Legend Icon(Marker) color
								graphs.setFillColors(fillColorsList);
								graphs.setLabelFunction("");
								graphsList.add(graphs);
								startIndex++;
								colr++;
							}
						}
				
				if(rowListSize < quantity)
					if(isTrend)//trend
					{
						isTrend=false;
						String label;
						for(int c=0;c<noOfTrendLines;c++)
						{
							Graphs graphs = new Graphs();
							graphs.setShowHandOnHover(true);
							graphs.setLineThickness((int)trendLineThickness.get(c));
							graphs.setLineAlpha(1);
							graphs.setType("line");
							graphs.setLabelText("[[TrendLabel" + c+ "]]" );
							graphs.setFillAlphas(0);
							graphs.setValueField(trendLineName.get(c).toString()+"trend");
							graphs.setDescriptionField("Abs"+trendLineName.get(c).toString()+"trend");
							graphs.setLineColor(trendColor.get(c).toString());
							graphs.setVisibleInLegend(false);
							graphs.setValueAxis("valueAxes1");
							
							//for Bug 11562
							if(graphInfo.getDataColLabels3().size() > 1)
							{
								String[] splitString = trendLineColoumn.get(c).toString().split(",");
								List measure = new ArrayList(graphInfo.getDataColLabels3());
								if(measure.contains(splitString[0]))
								{
									int occurenceIndex= measure.indexOf(splitString[0]);
									graphs.setValueAxis("valueAxes"+occurenceIndex);
								}
							}
								
							
							//line style
							int lineStyle = Integer.valueOf(trendLineStyle.get(c).toString());
							int trendDashLength = 0;
							switch (lineStyle) {
							case 0:
								trendDashLength = 0;
								break;
							case 1:
								trendDashLength = 7;
								break;
							case 2:
								trendDashLength = 3;
								break;
							/*case 3:
								trendDashLength = 1;
								break;*/
							}
							graphs.setDashLength(trendDashLength);//year quater
							graphs.setFontSize(10);
							if(!graphInfo.getGraphData().getColLabel().equalsIgnoreCase(trendValue.get(c).toString()) && isLegendVisible==false)
							{
								graphs.setLineAlpha(0);
								graphs.setLineThickness(0);
								graphs.setLabelText("");
							}
							graphsList.add(graphs);
						}
				
				//return graphsList;
			}
		}


		long endTime = System.currentTimeMillis();

		return graphsList;

	}
	private static final String[] appendValue(String[] s1 ,String newValue) {

		String[] erg = new String[s1.length + 1];
		erg[erg.length-1] = newValue;
		System.arraycopy(s1, 0, erg, 0, s1.length);

		return erg;

	}

}