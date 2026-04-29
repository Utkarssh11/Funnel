package com.elegantjbi.amcharts;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONObject;

import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.service.kpi.KPIConstants;
import com.elegantjbi.util.AppConstants;
import com.elegantjbi.util.GeneralFiltersUtil;
import com.elegantjbi.util.GraphsUtil;
import com.elegantjbi.util.StringUtil;
import com.elegantjbi.util.logger.ApplicationLog;
import com.elegantjbi.vo.properties.kpi.TrendLineProperties;
import com.elegantjbi.vo.properties.kpi.YaxisTrendProperties;

public class BarGraphDataProvider {

	public static List<Map<String, Object>> dataProviderJson(GraphInfo graphInfo,int startIndex,int Quantity,int categoryIndex,int categoryQuantity)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		List colorWiseIndex = new ArrayList<>();
		String rowLabel = graphInfo.getGraphData().getRowLabel();
		List rowList = graphInfo.getGraphData().getRowList();
		int rowListSize = rowList.size();


		boolean isLegendVisible = true;
		if(rowListSize == 0){
			rowListSize = 1;
			isLegendVisible =  false;
		}

		String colLabel = graphInfo.getGraphData().getColLabel();
		List dateColList = graphInfo.getGraphData().getDatecolList();
		List dateRowList = graphInfo.getGraphData().getDaterowList();
		List colList = graphInfo.getGraphData().getColList();
		List colList2 = new ArrayList();
		colList2.addAll(colList);
		List dvTruncatedColList = new ArrayList();//Added for NeGD feature request 15092 [13 Aug 2019]
		dvTruncatedColList.addAll(colList);
		List truncatedColList = new ArrayList();
		truncatedColList.addAll(colList);
		String truncatedLabel = "truncatedLabel";
		int dvTruncateCharLimit = 15;
		String dvTruncatedLabel = "dvTruncatedLabel";
		boolean isBarDataValueCharacterLimitNone = graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit().equalsIgnoreCase("none");
		//Added for NeGD feature request 15092 start [8 Aug 2019]
		if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
			|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
			|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
		{
			String truncatedLabelString = "";
			for (int i = 0; i < dvTruncatedColList.size(); i++) {
				truncatedLabelString = dvTruncatedColList.get(i).toString();
				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
				{
				case "auto":
					dvTruncateCharLimit = 15;
					break;
				case "custom":
					dvTruncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
					break;
				}
				if (truncatedLabelString.length() > dvTruncateCharLimit && !isBarDataValueCharacterLimitNone) {
					truncatedLabelString = truncatedLabelString.substring(0, dvTruncateCharLimit)+"..";
				}
				dvTruncatedColList.set(i, truncatedLabelString);
			}
		}
		//Added for NeGD feature request end [8 Aug 2019]
		
		int truncateCharLimit = 15;
		boolean isCharacterLimitNone = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCharacterLimit().equalsIgnoreCase("none");
		if(colList != null && !graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCharacterLimit().equalsIgnoreCase("none"))
		{
			String array_element;
			for (int i = 0; i < colList.size(); i++) {
				array_element = colList.get(i).toString();
				array_element = array_element.replaceAll("<b>", "").replaceAll("</b>", "");
				switch(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCharacterLimit())
				{
				case "auto":
					truncateCharLimit = 15;
					break;
				case "custom":
					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getFontProperties().getCustomCharacterLimit());
					break;
				}
				if (array_element.length() > truncateCharLimit)
					array_element = array_element.substring(0, truncateCharLimit)+"..";
				
				colList2.set(i, array_element);
			}
		}
		
		int colListSize = colList.size();
		
		boolean isMultipleMeasure = false;
		int noOfMeasure = 1;
		int noOfYAxis = 1;
		int multipleMeasures = graphInfo.getDataColLabels3().size();
		List dataLabelList = new ArrayList<String>();
		if(isLegendVisible && multipleMeasures > 1
			&& (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH))
		{
			isMultipleMeasure = true;
			if(graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))
				noOfMeasure = 1;
			else	
			noOfMeasure = graphInfo.getDataColLabels3().size();//Multiple Measures
			//noOfMeasure = graphInfo.getDataColLabels3().size();//Multiple Measures
			//rowListSize*=noOfMeasure;
			for (int d=0;d < graphInfo.getDataColLabels3().size();d++)//Fetching dataLabels for Multiple Measure //change size
			{
				dataLabelList.add(d, graphInfo.getDataColLabels3().get(d).toString());
			}
			noOfYAxis = noOfMeasure;
		}
		if((graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH) && (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			noOfYAxis = rowListSize;
		}


		String drillAxis = "drillAxis";
		String drillLegend = "drillLegend";
		List drillList = graphInfo.getGraphData().getDrillLinkList();
		int nullSize = colListSize*rowListSize*noOfMeasure;
		
		if(drillList.isEmpty() || nullSize > graphInfo.getGraphData().getDrillLinkList().size())
		{
			for(int i=0;i<nullSize;i++)
			{
				drillList.add("null");
			}
		}

		//boolean flag=false;
		//double customMax = 0.0;
		boolean isMultipleValueAxis = false;
		boolean[] flag = new boolean[multipleMeasures];
		double[] customMax = new double[multipleMeasures];
		String dataLabel = graphInfo.getGraphData().getDataLabel();
		//List percentageValueList= graphInfo.getGraphData().getPercentageValueList();
		//List realPercentageValueList=graphInfo.getGraphData().getRealPercentageValueList();
		
		Map keyValueMap = graphInfo.getGraphData().getKeyValueMap();
		
		
		List totalList = graphInfo.getGraphData().getTotalValueList();
		
		Map stackedvalueMap =  graphInfo.getGraphData().getStackedTotalValues();
		
		Map  stackedDataTotalValues = graphInfo.getGraphData().getStackedDataTotalValues();
		
		//int dataListSize = dataList.size();
		
	/*	String[] barColor =new String[]{"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"
										,"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
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
		
		
		flag = new boolean[rowListSize];
			customMax = new double[rowListSize];
			
			int customMaxRowListSize = rowListSize;
			int customMaxNumberofMeasure = 1;
			
			for(int i=0;i<customMaxRowListSize;i++)
			{
			
			if(graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))
			{			
				customMaxNumberofMeasure = multipleMeasures;
				customMaxRowListSize = 1;
			}
			
				for(int j=0;j<customMaxNumberofMeasure;j++)
				{
					if(null!=graphInfo.getGraphProperties() && null!=graphInfo.getGraphProperties().getyAxisPropertiesMap()&&graphInfo.getGraphProperties().getyAxisPropertiesMap().size()>=customMaxNumberofMeasure &&  graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+j).getLabelProperties().getMaxValType() == 1)
					{
						flag[i]=true;
						customMax[i] =Double.parseDouble(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+j).getLabelProperties().getMaxCustomVal());
					}
				}
			}
			boolean colLabelsName = false;
			//This is when non clustered and multiple measure along with col labels
			if(graphInfo.getGraphData().getColLabelsName() != null && graphInfo.getGraphData().getColLabelsName().size()>0)
				colLabelsName = true;
			List dataColList = new ArrayList();
			int precisionLabelCounter=1;
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
			
			//Added code for Column Labels Special char($) (For Bug #12443)
			if(null != rowLabel && rowLabel.equalsIgnoreCase("Legend")
					 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)) {
				List<String> originalRowList = new ArrayList<String>();
				
				if(!rowList.isEmpty()) {
					Map<String, String> colLabelsMap = graphInfo.getGraphProperties().getColLabelsMap();
					for (int i = 0; i < rowList.size(); i++) {
						for (Entry<String, String> e : colLabelsMap.entrySet()) {
							if(rowList.get(i).equals(e.getKey()))	//value to key
								originalRowList.add(e.getKey());
						}
					}
					if(!originalRowList.isEmpty()) {
						rowList = new ArrayList();
						rowList.addAll(originalRowList);
					}
				}
			}
			
			List<String> originalDataList = new ArrayList<String>();
			if(null != rowLabel && !rowLabel.equalsIgnoreCase("Legend") && null != colLabel
					 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH)) {
				List<String> dataColLabelsList = new ArrayList<String>(graphInfo.getDataColLabels3());
				if(!dataColLabelsList.isEmpty()) {
					Map<String, String> colLabelsMap = graphInfo.getGraphProperties().getColLabelsMap();
					for (int i = 0; i < dataColLabelsList.size(); i++) {
						for (Entry<String, String> e : colLabelsMap.entrySet()) {
							if(dataColLabelsList.get(i).equals(e.getKey()))	// value to key
								originalDataList.add(e.getKey());
						}
					}
				}
			}
			
			//Column Labels Special char($) end
		
//		if((graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH && ((graphInfo.getGraphData().getColLabel() != null || graphInfo.getGraphData().getRowLabel() != null) && (graphInfo.getGraphData().getRowLabel()!=null &&  graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))))
//				|| (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH && ((graphInfo.getGraphData().getColLabel() != null || graphInfo.getGraphData().getRowLabel() != null) && (graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))))
//				|| (graphInfo.getGraphData().getColLabel() != null || (graphInfo.getGraphData().getRowLabel() != null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("legend"))))
//		{
//			isMultipleValueAxis = true;
//		}
//		if(dataListSize > 0){
//		if(isMultipleValueAxis)
//		{
//			for(int i=0;i<multipleMeasures;i++)
//			{
//				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxValType() == 1)
//				{
//					flag[i]=true;
//					customMax[i] =Double.parseDouble(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getMaxCustomVal());
//				}
//				else
//				{
//					flag[i]=false;
//				}
//			}
//		}
//		else
//		{
//			flag = new boolean[rowListSize];
//			customMax = new double[rowListSize];
//			for(int i=0;i<rowListSize;i++)
//			{
//				if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().getMaxValType() == 1)
//				{
//					flag[i]=true;
//					customMax[i] =Double.parseDouble(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M0").getLabelProperties().getMaxCustomVal());
//				}
//			}
//		}
//		}
		
		//int div = dataListSize/noOfMeasure;

		//String[] barColor =new String[]{"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		//String[] bulletColor =new String[]{"#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296", "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424", "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92", "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		switch(graphInfo.getGraphProperties().getColorType())
		{
		case 1:
			if(graphInfo.getGraphProperties().getCustomColors() != null)
			{
				for (int i = 0; i < graphInfo.getGraphProperties().getCustomColors().size(); i++) {
					if(i > (barColor.length-1))// || i > (bulletColor.length-1))
					{
						barColor = appendValue(barColor, graphInfo.getGraphProperties().getCustomColors().get(i));
						bulletColor = appendValue(bulletColor, graphInfo.getGraphProperties().getCustomColors().get(i));
					}
					else
					{	
						barColor[i] = graphInfo.getGraphProperties().getCustomColors().get(i);
						bulletColor[i] = graphInfo.getGraphProperties().getCustomColors().get(i);
					}
				}
			}
			break;
		case 2:
			barColor = new String[]{graphInfo.getGraphProperties().getColor()};
			bulletColor = new String[]{graphInfo.getGraphProperties().getColor()};
			break;
		}
		
		if(categoryIndex > colListSize)
			categoryIndex=colListSize;
		if(startIndex > rowListSize)
			startIndex=rowListSize;
		
		boolean legendflag=false;
		int startIndexcolne=startIndex;	
		if(Quantity== -1) {
			Quantity =100000;
		}
		int paginationIndex= startIndex+Quantity;
		int categorypaginationIndex=categoryIndex+categoryQuantity;
		if(categorypaginationIndex > colListSize)
			categorypaginationIndex=colListSize+1;
		if(paginationIndex >= rowListSize)
		{
			legendflag=true;
			paginationIndex=rowListSize;
		}	
		int k=0;
		int drillIndex=0;
		List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();

		//Trend Line Start
		Map trendMAp = graphInfo.getGraphData().getTrendMap();
		boolean isTrend =false;
		int trendCount;

		int noOfTrendLines = 0;
		if(trendMAp != null)
			noOfTrendLines = trendMAp.size(); 
		List trendColor = new ArrayList();
		List trendLineName = new ArrayList();
		List trendValues = new ArrayList();
		List trendLineColoumn = new ArrayList();
		List trendAlgoList = new ArrayList();
		if(noOfTrendLines > 0)
		{
			isTrend=true;
			trendCount = noOfTrendLines;

			Map<Integer, TrendLineProperties> testMap = graphInfo.getGraphProperties().getTrendlinePropertiesMap();
			for (Entry<Integer, TrendLineProperties> entry : testMap.entrySet()) {
				trendColor.add(entry.getValue().getTrendLineColor());
				trendLineName.add(entry.getValue().getTrendLineName());//Name of the trend Line given by the user
				trendLineColoumn.add(entry.getValue().getTrendLineColumn());
				trendAlgoList.add(entry.getValue().getTrendLineType().toString());
			}
		}
		//trend Line End
		//int index = 0,even=0;
		//int cnt = -1;
		int counts=0;
		String yAxisTitle = "yAxisTitle";
		//String percentageValues = "percentageValues";
		//String realPercentageValues = "realPercentageValues";
		
		//Performance changes start
		List adjustedDigitList=new ArrayList();
		
		if((graphInfo.getDataColLabels3().size() > 1 && (graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH) && (graphInfo.getGraphData().getRowLabel() != null &&  graphInfo.getGraphData().getColLabel() != null))
				|| (graphInfo.getGraphType() == GraphConstants.VBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH || graphInfo.getGraphType() == GraphConstants.LINE_GRAPH || graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH)
				&& ((graphInfo.getGraphData().getRowLabel() == null || (graphInfo.getGraphData().getRowLabel() != null && "Legend".equals(graphInfo.getGraphData().getRowLabel()))) ||  graphInfo.getGraphData().getColLabel() == null) && graphInfo.getDataColLabels3().size()>1)
		{
			if(graphInfo.getDataColLabels3().size() > 1)
			{
				for(int i=0;i<graphInfo.getGraphProperties().getyAxisPropertiesMap().size();i++)
				{
					if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().isVisible() /*|| graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().isDataValuePointVisible()*/)
						adjustedDigitList.add(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+i).getLabelProperties().getAdjustedDigit());
					else
						adjustedDigitList.add(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getAdjustedDigit());
				}
			}
		}
		else
		{
			int adjustedDigit = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0).getLabelProperties().getAdjustedDigit();
			//int divValue = (int)(Math.pow(10, adjustedDigit));
			adjustedDigitList.add(adjustedDigit);
		}
		int divValue;
		List multiDivValueList = new ArrayList();
		for(int z=0;z<adjustedDigitList.size();z++)
		{
			divValue = (int)(Math.pow(10, Double.valueOf(adjustedDigitList.get(z).toString())));
			multiDivValueList.add(divValue);
		}
		
		double stackTotal = 0.0;
		
		String mapKeyStr = "";
		boolean nonSatckedMultipleMeasure = graphInfo.getDataColLabels3().size() > 1 && rowLabel != null && rowLabel.equalsIgnoreCase("Legend");
		Double dataValue = 0.0;
		String label = null;
		String label1 = null;
		String label2 = "";
		String totalValueKey = "";
		String colLabelNew = "";
		String dvColLabelNew = "";
		/*int otherIndex =0;
		boolean rowOtherFlag = false;
		*/
		//9 Apr 2019
		List hideGraphsList = new ArrayList();
		Map graphsVisibleMap = graphInfo.getGraphProperties().getGraphsVisibleMap();
		for(int i=0;i<graphInfo.getDataColLabels3().size();i++)
		{
			if(graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)) != null && graphsVisibleMap.get(graphInfo.getDataColLabels3().get(i)).toString().equals("false"))
				hideGraphsList.add(i);
		}
		//9 Apr 2019
		

		/*if(rowList!=null && !rowList.isEmpty() && rowList.contains("Other")) {
			rowList.set(rowList.size()-2 , rowList.get(rowList.size()-1));
			rowList.set(rowList.size()-1,"Other");
		}*/
		for (int i = 0; i < colListSize; i++) {
			Map<String, Object> dpMap =  new HashMap<String, Object>();
			stackTotal = 0.0;
			
			if(i+1==colListSize)
				dpMap.put("colLastPage", "1");
			if(paginationIndex==rowListSize)
			{
				dpMap.put("rowLastPage", 1);
				startIndex=startIndexcolne;
			}
			Map<String, String> drillMap =null;
			if(i==categoryIndex)
			{
				if( (!graphInfo.getGraphData().getDaterowList().isEmpty() && (null == rowLabel || (null != rowLabel && "Legend".equals(rowLabel))))
                        || !graphInfo.getGraphData().getDatecolList().isEmpty() )//Added for Bug #13406 start
                { 
					String stringFormat;
					stringFormat = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getTimeFormat();
					stringFormat = stringFormat.replaceAll("&#39;", "'");
					Calendar cal = Calendar.getInstance();
					
					Date axisDate = null;
					if(!dateColList.isEmpty())
					{
						if(dateColList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
							colLabelNew = dateColList.get(i).toString();				 
						else
							axisDate = (Date) dateColList.get(i);
					}
					else
					{
						if(!dateRowList.isEmpty())
						{
						 if(dateRowList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
							colLabelNew = dateRowList.get(i).toString();				 
						else
							axisDate = (Date) dateRowList.get(i);
						}
					}
					if(axisDate != null)
					{
							cal.setTime(axisDate);
							stringFormat=stringFormat.trim();
							colLabelNew = new SimpleDateFormat(stringFormat).format(cal.getTime());
					}
					
					dpMap.put(truncatedLabel,colLabelNew);
					if(!isCharacterLimitNone && (colLabelNew.length() > truncateCharLimit))
						colLabelNew = colLabelNew.substring(0, truncateCharLimit)+"..";
					dpMap.put(colLabel, colLabelNew);
					
					//Added for NeGD feature request 15092 start [8 Aug 2019]
					dvColLabelNew = colLabelNew;
                    if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
        				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
        				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
            		{
        				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
        				{
        				case "auto":
        					truncateCharLimit = 15;
        					break;
        				case "custom":
        					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
        					break;
        				}
        				if (dvColLabelNew.length() > truncateCharLimit && !isBarDataValueCharacterLimitNone) {
        					dvColLabelNew = dvColLabelNew.substring(0, truncateCharLimit)+"..";
        				}
            		}
					dpMap.put(dvTruncatedLabel, dvColLabelNew);
					//Added for NeGD feature request end [8 Aug 2019]
				}//Added for Bug #13406 end
				else if (graphInfo.getDateFrequencyMap() != null && !graphInfo.getDateFrequencyMap().isEmpty()  ) {
					
					GraphsUtil.getDateFormat(graphInfo,dpMap,i);
				}
				else {
					dpMap.put(colLabel, colList2.get(i).toString());
					dpMap.put(truncatedLabel,truncatedColList.get(i).toString());
					dpMap.put(dvTruncatedLabel, dvTruncatedColList.get(i).toString());
				}
				if(isLegendVisible && isMultipleMeasure)
				{
					for(int measureCnt = 0; measureCnt < multipleMeasures; measureCnt++)
					{
						if(graphInfo.getDataColLabels3().get(measureCnt) != null && !graphInfo.getDataColLabels3().isEmpty())
							dpMap.put("yAxisTitle"+measureCnt, unescapeHtml(graphInfo.getDataColLabels3().get(measureCnt).toString()));
					}
				}
				else
				{
					dpMap.put("yAxisTitle", StringUtil.replaceSpecialCharWithHTMLEntity(dataLabel));
				}
				if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
					dpMap.put("xaxisTitle", StringUtil.replaceSpecialCharWithHTMLEntity(colLabel));
				drillIndex = i;
				if(isLegendVisible)
				{
					drillIndex=rowListSize+i;
					drillMap = new HashMap<String, String>();
				}
				if(graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().isVisible())
				{
					if(drillList.size() > drillIndex && (!drillList.isEmpty() && drillList.get(drillIndex)!=null) && !drillList.get(drillIndex).equals("null"))
						dpMap.put(drillAxis, drillList.get(drillIndex).toString());
				}
				//otherIndex = 0;
			
			innerloop:
			
				for (int j = 0; j < rowListSize; j++) {
					if(j==startIndex)// && !hideGraphsList.contains(j)
					{
						//if(null != rowList && rowList.get(j) != null)
							//mapKeyStr = colList2.get(i).toString()+rowList.get(j).toString();
						if(startIndex==paginationIndex && !legendflag)
						{

							if(!dpMap.containsKey(colLabel))
							{
								if( (!graphInfo.getGraphData().getDaterowList().isEmpty() && (null == rowLabel || (null != rowLabel && "Legend".equals(rowLabel))))
				                        || !graphInfo.getGraphData().getDatecolList().isEmpty() )//Added for Bug #13406 start
				                { 
									String stringFormat;
									stringFormat = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getTimeFormat();
									stringFormat = stringFormat.replaceAll("&#39;", "'");
									Calendar cal = Calendar.getInstance();
									Date axisDate = null;
									if(!dateColList.isEmpty())
									{
										if(dateColList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
											colLabelNew = dateColList.get(i).toString();				 
										else
											axisDate = (Date) dateColList.get(i);
									}
									else
									{
										if(!dateRowList.isEmpty())
										{
										 if(dateRowList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
											colLabelNew = dateRowList.get(i).toString();				 
										else
											axisDate = (Date) dateRowList.get(i);
										}
									}
									if(axisDate != null)
									{
											cal.setTime(axisDate);
											stringFormat=stringFormat.trim();
											colLabelNew = new SimpleDateFormat(stringFormat).format(cal.getTime());
									}
									dpMap.put(truncatedLabel,colLabelNew);
									if(!isCharacterLimitNone && (colLabelNew.length() > truncateCharLimit))
										colLabelNew = colLabelNew.substring(0, truncateCharLimit)+"..";
									dpMap.put(colLabel, colLabelNew);
									
									//Added for NeGD feature request 15092 start [8 Aug 2019]
									dvColLabelNew = colLabelNew;
				                    if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
				        				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
				        				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
				            		{
				        				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
				        				{
				        				case "auto":
				        					truncateCharLimit = 15;
				        					break;
				        				case "custom":
				        					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
				        					break;
				        				}
				        				if (dvColLabelNew.length() > truncateCharLimit && !isBarDataValueCharacterLimitNone) {
				        					dvColLabelNew = dvColLabelNew.substring(0, truncateCharLimit)+"..";
				        				}
				            		}
									dpMap.put(dvTruncatedLabel, dvColLabelNew);
									//Added for NeGD feature request end [8 Aug 2019]
								}//Added for Bug #13406 end
								else {
									dpMap.put(colLabel, colList2.get(i).toString());
									dpMap.put(truncatedLabel,truncatedColList.get(i).toString());
									dpMap.put(dvTruncatedLabel, dvTruncatedColList.get(i).toString());
								}
							}
							if(!dpMap.containsKey(drillAxis))
							{
								if(!drillList.isEmpty() && drillList.size() > drillIndex && drillList.get(drillIndex)!=null && !drillList.get(drillIndex).equals("null")) 
								dpMap.put(drillAxis, drillList.get(drillIndex).toString());

							}
							/*if(j==rowListSize-1)									
								dpMap.put("rowLastPage", 1);*/										
							startIndex=startIndexcolne;
							//dpList.add(dpMap);
							break innerloop;
						}
					for(int theIndex = 0; theIndex < noOfMeasure; theIndex++)
					{
					/*if(!dataList.isEmpty())
					{*/
						mapKeyStr = "";
						if(null != colList && colList.size() > 0)
							mapKeyStr += colList.get(i).toString();
						if(null != rowList && rowList.size() > 0)
							mapKeyStr += rowList.get(j).toString();
						if(!nonSatckedMultipleMeasure) {
							if(null != rowLabel && !rowLabel.equalsIgnoreCase("Legend") && !originalDataList.isEmpty())
								mapKeyStr += originalDataList.get(theIndex).toString();
							else
								mapKeyStr += graphInfo.getDataColLabels3().get(theIndex).toString();
						}
						
						
						if(keyValueMap.get(mapKeyStr)!=null){	
							
							
								
								//otherIndex = j;
								label = null;
								label1 = null;
								label2 = "";
								totalValueKey = "";
								if(stackedvalueMap != null && stackedvalueMap.size() > 0)
								{
									totalValueKey = truncatedColList.get(i).toString();
									if(null != rowLabel && !rowLabel.equalsIgnoreCase("Legend") && !originalDataList.isEmpty())
										totalValueKey += originalDataList.get(theIndex).toString();
									else
										totalValueKey += graphInfo.getDataColLabels3().get(theIndex).toString();
									
								}
								if(isLegendVisible){
									
									label = rowList.get(j).toString();
									
									
									if(isMultipleMeasure)
									{
										if(graphInfo.getDataColLabels3().size() > theIndex)
											label2 = label + graphInfo.getDataColLabels3().get(theIndex).toString();//Added for Y_axis_Stacked value when multiple measure & stacked bar Bug #12741
										/*if((j%(rowListSize/noOfMeasure))==0)
										{
											index=0;//Fetching appropriate label when Multiple Measure
											cnt++;
										}
										label = rowList.get(index).toString();*/
										if(null != rowLabel && rowLabel.equalsIgnoreCase("Legend") && !originalDataColList.isEmpty())
											label += originalDataColList.get(theIndex).toString();
										else if(null != rowLabel && !rowLabel.equalsIgnoreCase("Legend") && !originalDataList.isEmpty())
											label += originalDataList.get(theIndex).toString();
										else {
											if(!originalDataList.isEmpty() && originalDataList.size() > theIndex)
												label += originalDataList.get(theIndex).toString();//dataLabelList.get(theIndex).toString();
										}
										
									}
									//label = label;//.replaceAll("[^\\s\\w]*","");
									
									label1 = j+label.replaceAll("[^\\s\\w]*","");
									
									if(drillMap==null)
										drillMap = new HashMap<String, String>();
									if(!drillList.isEmpty() && rowList.size() > j  && drillList.size() > drillIndex &&  drillList.get(j)!=null && !drillList.get(j).equals("null"))
									{
										/*if(isMultipleMeasure)
											drillMap.put(rowList.get(index).toString(), drillList.get(j).toString());
										else*/
										drillMap.put(rowList.get(j).toString(), drillList.get(j).toString());
									}
									if(dataColList != null && !dataColList.isEmpty())
									{
										if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
										{
											if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
											{	
												dpMap.put("yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*",""), StringUtil.replaceSpecialCharWithHTMLEntity(graphInfo.getDataColLabels3().get(j).toString()));
												dpMap.put("zaxisvalue"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*",""), StringUtil.replaceSpecialCharWithHTMLEntity(graphInfo.getDataColLabels3().get(j).toString()));
											}
											else
											{
												dpMap.put("yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString()));
												dpMap.put("zaxisvalue"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString()));
											}
										}
										else{
											dpMap.put("yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString()));
										}
									}
									
									
									//index++;
									if(colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend")) && (totalList != null && totalList.get(theIndex) != null))
									{
										//dpMap.put("realTotal", totalList.get(i));
										stackTotal = stackTotal + (Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString()));//Double.parseDouble(totalList.get(i).toString());
										String t = commaFormats(stackTotal, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex), graphInfo);
										//String t = commaFormats(totalList.get(i).toString(),graphInfo,theIndex);
										dpMap.put("AbsrealTotal",t);
										//absRealTotal = absRealTotal + Double.valueOf(keyValueMap.get(mapKeyStr).toString());
									}
									/*else if(totalList != null && totalList.get(theIndex) != null)
									{*/
										/*if((graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH ||
												graphInfo.getGraphType() == GraphConstants.STACKED_VBAR_GRAPH))
										{
											dpMap.put("realTotal", totalList.get(j));//j
											String t = commaFormats(totalList.get(i).toString(),graphInfo,theIndex);
											dpMap.put("AbsrealTotal",t);
										}
										else
										{*/
										if(multiDivValueList.size() >= rowListSize && (graphInfo.getGraphType() != GraphConstants.STACKED_VBAR_GRAPH && graphInfo.getGraphType() != GraphConstants.STACKED_HBAR_GRAPH))//Added for bug no 12277
											stackTotal = stackTotal + (Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(j).toString()));
										else
											stackTotal = stackTotal + (Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString()));//Double.parseDouble(totalList.get(i).toString());
										String t = commaFormats(stackTotal, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex), graphInfo);
											//dpMap.put("realTotal", totalList.get(i));
											//String t = commaFormats(Double.parseDouble(totalList.get(i).toString()), graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex), graphInfo);
											//String t = commaFormats(totalList.get(i).toString(),graphInfo,theIndex);
											dpMap.put("AbsrealTotal",t);
										//}
									//}	
									if(stackedvalueMap != null && stackedvalueMap.size() > 0){
										dpMap.put("realTotal"+theIndex, stackedvalueMap.get(totalValueKey));
										t = commaFormats(Double.parseDouble(stackedvalueMap.get(totalValueKey).toString()), graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex), graphInfo);
										//String t = commaFormats(stackedvalueMap.get(totalValueKey).toString(),graphInfo,theIndex);
										dpMap.put("AbsrealTotal"+theIndex,t);
									}
									else{
										dpMap.put("realTotal", "");
									}
									dpMap.put(drillLegend, drillMap);
								}else{
									label = graphInfo.getGraphData().getDataLabel();
									//label = label;//.replaceAll("[^\\s\\w]*","");
									label1 = j+label.replaceAll("[^\\s\\w]*","");
									if(graphInfo.getGraphType() == GraphConstants.AREA_DEPTH_GRAPH
											||	graphInfo.getGraphType() == GraphConstants.AREA_STACK_GRAPH
											|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
									{
										if(null == rowLabel) {//Added for Bug #15407
											dpMap.put("color", barColor[0]);
											colorWiseIndex.add(0);
										} else {
											dpMap.put("color", barColor[graphInfo.getColorInfoList().get(j)%barColor.length]);
											colorWiseIndex.add(graphInfo.getColorInfoList().get(j)%barColor.length);
										}
									}
									else
									{
										dpMap.put("color", barColor[graphInfo.getColorInfoList().get(i)%barColor.length]);
										colorWiseIndex.add(graphInfo.getColorInfoList().get(i)%barColor.length);
									}
									
									//This is when one measure and dimension in stacked
									if(graphInfo.getDataColLabels3().size() == 1)
									{
										dataValue = (Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString()));
										String t = commaFormats(dataValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex), graphInfo);
										//String t = commaFormats(dataList.get((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex)).toString(), graphInfo,theIndex);
										dpMap.put("AbsrealTotal",t);
									}
								}
								//Changes bar values to customMaxValue if flag is true
	
								if(flag[j])
								{
									/*if((Double)dataList.get((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex)) > customMax[j])
									{
										dataList.set((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex), customMax[j]);
									}*/
								}
								if(multiDivValueList.size() >= rowListSize && (graphInfo.getGraphType() != GraphConstants.STACKED_VBAR_GRAPH && graphInfo.getGraphType() != GraphConstants.STACKED_HBAR_GRAPH))//Added for bug no 12277
									dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(j).toString());
								else
									dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString());
								dpMap.put(label, dataValue);
								if(dataColList != null && !dataColList.isEmpty())
								{
									if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
									{
										if(dataColList.get(j) != null && dataColList.get(j).toString().equalsIgnoreCase("data"))
										{	
											dpMap.put("yaxisTitle"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*",""), StringUtil.replaceSpecialCharWithHTMLEntity(graphInfo.getDataColLabels3().get(j).toString()));
											dpMap.put("zaxisvalue"+graphInfo.getDataColLabels3().get(j).toString().replaceAll("[^\\s\\w]*",""), StringUtil.replaceSpecialCharWithHTMLEntity(graphInfo.getDataColLabels3().get(j).toString()));
										}
										else
										{
											dpMap.put("yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString().toString()));
											dpMap.put("zaxisvalue"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString().toString()));
										}
									}
									else{
										dpMap.put("yaxisTitle"+originalDataColList.get(j).toString().replaceAll("[^\\s\\w]*",""), unescapeHtml(dataColList.get(j).toString().toString()));
									}
								}
								//commaseparator start
								int theIndexProp = theIndex;
								if(graphInfo.getGraphType() != GraphConstants.STACKED_HBAR_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.STACKED_VBAR_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.PERCENTAGE_HBAR_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.PERCENTAGE_VBAR_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.STACKED_LINE_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.PERCENTAGE_LINE_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.AREA_STACK_GRAPH &&
										graphInfo.getGraphType() != GraphConstants.AREA_PERCENTAGE_GRAPH)
								{	
									if(colLabel != null && (rowLabel != null && rowLabel.equalsIgnoreCase("Legend")))
										theIndexProp = j;
								}
								if(multiDivValueList.size() >= rowListSize && (graphInfo.getGraphType() != GraphConstants.STACKED_VBAR_GRAPH && graphInfo.getGraphType() != GraphConstants.STACKED_HBAR_GRAPH))//Added for bug no 12277
									dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(j).toString());
								else
									dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString());
                                double parseValue = dataValue;
                                
                                int precision = graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp).getLabelProperties().getNumberOfDigits();
                                String digitsAfterDecimalString="";
                                switch(precision)
                                {
                                case 0:
                                	digitsAfterDecimalString="#.";
                                    break;
                                case 1:
                                	digitsAfterDecimalString="#.0";
                                    break;
                                case 2:
                                	digitsAfterDecimalString="#.00";
                                    break;
                                case 3:
                                	digitsAfterDecimalString="#.000";
                                    break;
                                case 4:
                                	digitsAfterDecimalString="#.0000";
                                    break;
                                case 5:
                                	digitsAfterDecimalString="#.00000";
                                    break;
                                }

                            //NumberFormat formatter = new DecimalFormat(digitsAfterDecimalString);
                            //NumberFormat formatter = new DecimalFormat(digitsAfterDecimalString,DecimalFormatSymbols.getInstance(Locale.ENGLISH));    
                            //parseValue = Double.valueOf(formatter.format(parseValue));
                                
                                //String commaSeparated = "";                               
                                String commaSeparated = commaFormats(parseValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp), graphInfo);
                                String dvCommaSeparated = commaSeparated;
                                //Added for NeGD feature request 15092 start [8 Aug 2019]
                                if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
                    				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
                    				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
                        		{
	                				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
	                				{
	                				case "auto":
	                					truncateCharLimit = 15;
	                					break;
	                				case "custom":
	                					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
	                					break;
	                				}
	                				if (dvCommaSeparated.length() > truncateCharLimit && !isBarDataValueCharacterLimitNone) {
	                					dvCommaSeparated = dvCommaSeparated.substring(0, truncateCharLimit)+"..";
	                				}
                        		}
                				//Added for NeGD feature request end [8 Aug 2019]
                				
                               dpMap.put("Abs"+label1, commaSeparated);
                               // dpMap.put("Abs\\(180 d\\)ays", commaSeparated);
                                dpMap.put("AbsDv"+label1, dvCommaSeparated);
                                if(!"".equals(label2))
                                	dpMap.put("Abs"+label2, commaSeparated);
                               /* if(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp).getLabelProperties().isCommaSeprator())
                                {
                                	switch(graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp).getLabelProperties().getCommaFormat())
                                	{
                                	case 1:
                                		commaSeparated = parseformat("#,###.##", parseValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp).getLabelProperties().getNumberOfDigits());
                                		dpMap.put("Abs"+label, commaSeparated);
                                		break;
                                	case 2:
                                		try{
                        					if(parseValue<0)
                                			{
                                				if(parseValue < -999)
                                				{
                                					parseValue=Math.abs(parseValue);
                                					double hundreds = parseValue % 1000;
                                					int other = (int) (parseValue / 1000);
                                					commaSeparated = parseformat(",##", other,0) + ',' + parseformat("000", hundreds,graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex).getLabelProperties().getNumberOfDigits());
                                					commaSeparated="-"+commaSeparated;
                                				}
                                				else
                                				{
                                					//commaSeparated=String.valueOf(parseValue);
                                					commaSeparated = parseformat("#,###.##", parseValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex).getLabelProperties().getNumberOfDigits());
                                				}
                                			}
                        					else
                        					{
                        						if(parseValue == 0 || (parseValue > 0 && parseValue < 1000))
                                				{
                        							commaSeparated = parseformat("#,###.##", parseValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex).getLabelProperties().getNumberOfDigits());
                                				}
                        						else
                        						{
                        							double hundreds = parseValue % 1000;
                        							int other = (int) (parseValue / 1000);
                        							commaSeparated = parseformat(",##", other,0) + ',' + parseformat("000", hundreds,graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndex).getLabelProperties().getNumberOfDigits());
                        						}
                        					}
                        				}catch(Exception e){
                                			System.out.println(e);
                                		}
                                		dpMap.put("Abs"+label, commaSeparated);
                                		break;
                                	}
                                }*/
                               /* else
                                {
                                	commaSeparated = parseformat("####.##", parseValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp).getLabelProperties().getNumberOfDigits());
                            		dpMap.put("Abs"+label, commaSeparated);	
                                }*/
                                //commma seaparator ends
								if(graphInfo.getGraphType() == GraphConstants.PERCENTAGE_VBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH
										|| graphInfo.getGraphType() == GraphConstants.AREA_PERCENTAGE_GRAPH)
								{
									/*if(percentageValueList != null && percentageValueList.size() > 0)
									{*/
										/*	Commented while developing NeGD feature request 15081 as it was always taking 100 [1 Aug 2019]
										double percValue = 100.00;//Double.valueOf(percentageValueList.get(i).toString());
										String commaPercValue = commaFormats(percValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp), graphInfo);
										dpMap.put(percentageValues+j,commaPercValue);*/
									//}
									/*if(realPercentageValueList != null && realPercentageValueList.size() > 0)
									{
										double realPercValue = Double.valueOf(realPercentageValueList.get((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex)).toString());
										//String commaRealPercValue = commaFormats(realPercValue, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp), graphInfo);
										dpMap.put(realPercentageValues+j, realPercValue);//Removed comma separator for bug fixing (as not required in % chart)
									}*/
									dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / Double.valueOf(multiDivValueList.get(theIndex).toString());
									dpMap.put("yaxisValue"+j, dataValue);
									double value = dataValue.doubleValue();
									dpMap.put("AbsyaxisValue"+j, commaFormats(value, graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp), graphInfo));
									
									if(null != stackedDataTotalValues)
									{
										dpMap.put("yaxisStackedValue"+j, stackedDataTotalValues.get((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex)));
										value = Double.valueOf(stackedDataTotalValues.get((i*rowListSize*noOfMeasure)+(j*noOfMeasure+theIndex)).toString());
										dpMap.put("AbsyaxisStackedValue"+j, commaFormats(value,graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+theIndexProp) ,graphInfo));
									}
									
								}
									
							}else{
								//12934
								if(!drillList.isEmpty() && rowList.size() > j && drillList.size() > j &&  drillList.get(j)!=null && !drillList.get(j).equals("null"))
								{
									/*if(isMultipleMeasure)
										drillMap.put(rowList.get(index).toString(), drillList.get(j).toString());
									else*/
									drillMap.put(rowList.get(j).toString(), drillList.get(j).toString());
								}
								//12934
								//rowOtherFlag = true;
								
								if(startIndex==paginationIndex)
								{
									if(j==rowListSize-1)									
										dpMap.put("rowLastPage", 1);
									startIndex=startIndexcolne;
									break innerloop;
								}
								//startIndex++;
								counts++;
								continue;	
							}
						//}
					}
					
					counts++;
					if(startIndex==paginationIndex && legendflag)
					{

						if(!dpMap.containsKey(colLabel))
						{
							if( (!graphInfo.getGraphData().getDaterowList().isEmpty() && (null == rowLabel || (null != rowLabel && "Legend".equals(rowLabel))))
			                        || !graphInfo.getGraphData().getDatecolList().isEmpty() )//Added for Bug #13406 start
			                { 
								String stringFormat;
								stringFormat = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getTimeFormat();
								stringFormat = stringFormat.replaceAll("&#39;", "'");
								Calendar cal = Calendar.getInstance();
								
								Date axisDate = null;
								if(!dateColList.isEmpty())
								{
									if(dateColList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
										colLabelNew = dateColList.get(i).toString();				 
									else
										axisDate = (Date) dateColList.get(i);
								}
								else
								{
									if(!dateRowList.isEmpty())
									{
									 if(dateRowList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
										colLabelNew = dateRowList.get(i).toString();				 
									else
										axisDate = (Date) dateRowList.get(i);
									}
								}
								if(axisDate != null)
								{
										cal.setTime(axisDate);
										stringFormat=stringFormat.trim();
										colLabelNew = new SimpleDateFormat(stringFormat).format(cal.getTime());
								}
								
								dpMap.put(truncatedLabel,colLabelNew);
								if(!isCharacterLimitNone && (colLabelNew.length() > truncateCharLimit))
									colLabelNew = colLabelNew.substring(0, truncateCharLimit)+"..";
								dpMap.put(colLabel, colLabelNew);
								
								//Added for NeGD feature request 15092 start [8 Aug 2019]
								dvColLabelNew = colLabelNew;
			                    if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
			        				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
			        				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
			            		{
			        				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
			        				{
			        				case "auto":
			        					truncateCharLimit = 15;
			        					break;
			        				case "custom":
			        					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
			        					break;
			        				}
			        				if (dvColLabelNew.length() > truncateCharLimit && !isBarDataValueCharacterLimitNone) {
			        					dvColLabelNew = dvColLabelNew.substring(0, truncateCharLimit)+"..";
			        				}
			            		}
								dpMap.put(dvTruncatedLabel, dvColLabelNew);
								//Added for NeGD feature request end [8 Aug 2019]
							}//Added for Bug #13406 end
							else {
								dpMap.put(colLabel, colList2.get(i).toString());
								dpMap.put(truncatedLabel,truncatedColList.get(i).toString());
								dpMap.put(dvTruncatedLabel, dvTruncatedColList.get(i).toString());
							}
						}
						if(!dpMap.containsKey(drillAxis))
						{
							if(!drillList.isEmpty() && drillList.size() > drillIndex && drillList.get(drillIndex)!=null && !drillList.get(drillIndex).equals("null"))
						{
							dpMap.put(drillAxis, drillList.get(drillIndex).toString());
						}
						}
						if(j==rowListSize-1)									
							dpMap.put("rowLastPage", 1);										
						startIndex=startIndexcolne;
						//dpList.add(dpMap);
						break innerloop;
					}
					startIndex++;
					//otherIndex =rowOtherFlag?j:j+1;
				}
			}
			dpMap.put("realTotal", stackTotal);
			//dpMap.put("totalCount", String.valueOf((long)totalCount));
			if(graphInfo.getGraphData().getTotalCountDataList() != null && graphInfo.getGraphData().getTotalCountDataList() > 0) {
				try {
					DecimalFormat df = new DecimalFormat("#.00");
					dpMap.put("totalPercents", df.format((stackTotal*100)/graphInfo.getGraphData().getTotalCountDataList()));
					String t = commaFormats(graphInfo.getGraphData().getTotalCountDataList(), graphInfo.getGraphProperties().getyAxisPropertiesMap().get("M"+0), graphInfo);
					dpMap.put("totalRecords", t);
				} catch (Exception e) {
					ApplicationLog.error(e);
				}
			}
				
			//cnt = -1;
			if(isTrend)//Trend Line
			{
				label = "";

				for(int c=0;c<noOfTrendLines;c++)
				{
					String[] splitString = trendLineColoumn.get(c).toString().split(",");
					String key = (splitString[0])+(String)trendAlgoList.get(c);
					List temp=(ArrayList)(trendMAp.get(key));//fetch the coloumn given by the user
					label=trendLineName.get(c).toString();
					if(temp.get(i)!=null)
					dpMap.put(label+"trend", temp.get(i));
					if(i==colListSize-1)
						dpMap.put("TrendLabel"+c, label);
				}

			}
			if(i==categoryIndex)
			{
				if(!dpMap.containsKey(colLabel) && dpMap.size() >0)
				{
					if( (!graphInfo.getGraphData().getDaterowList().isEmpty() && (null == rowLabel || (null != rowLabel && "Legend".equals(rowLabel))))
	                        || !graphInfo.getGraphData().getDatecolList().isEmpty() )//Added for Bug #13406 start
	                { 
						String stringFormat;
						stringFormat = graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getxAxisProperties().getLabelProperties().getTimeFormat();
						stringFormat = stringFormat.replaceAll("&#39;", "'");
						Calendar cal = Calendar.getInstance();
						
						Date axisDate = null;
						if(!dateColList.isEmpty())
						{
							if(dateColList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
								colLabelNew = dateColList.get(i).toString();				 
							else
								axisDate = (Date) dateColList.get(i);
						}
						else
						{
							if(!dateRowList.isEmpty())
							{
							 if(dateRowList.get(i).equals(AppConstants.NULL_DISPLAY_VALUE))
								colLabelNew = dateRowList.get(i).toString();				 
							else
								axisDate = (Date) dateRowList.get(i);
							}
						}
						if(axisDate != null)
						{
								cal.setTime(axisDate);
								stringFormat=stringFormat.trim();
								colLabelNew = new SimpleDateFormat(stringFormat).format(cal.getTime());
						}
						
						dpMap.put(truncatedLabel,colLabelNew);
						if(!isCharacterLimitNone && (colLabelNew.length() > truncateCharLimit))
							colLabelNew = colLabelNew.substring(0, truncateCharLimit)+"..";
						dpMap.put(colLabel, colLabelNew);
						
						//Added for NeGD feature request 15092 start [8 Aug 2019]
						dvColLabelNew = colLabelNew;
	                    if(graphInfo.getGraphType() == GraphConstants.HBAR_GRAPH 
	        				|| graphInfo.getGraphType() == GraphConstants.STACKED_HBAR_GRAPH
	        				|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_HBAR_GRAPH) 
	            		{
	        				switch(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCharacterLimit())
	        				{
	        				case "auto":
	        					truncateCharLimit = 15;
	        					break;
	        				case "custom":
	        					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getDataValueProperties().getDataValuePoint().getFontProperties().getCustomCharacterLimit());
	        					break;
	        				}
	        				if (dvColLabelNew.length() > truncateCharLimit && !isBarDataValueCharacterLimitNone) {
	        					dvColLabelNew = dvColLabelNew.substring(0, truncateCharLimit)+"..";
	        				}
	            		}
						dpMap.put(dvTruncatedLabel, dvColLabelNew);
						//Added for NeGD feature request end [8 Aug 2019]
					}//Added for Bug #13406 end
					else {
						dpMap.put(colLabel, colList2.get(i).toString());
						dpMap.put(truncatedLabel,truncatedColList.get(i).toString());
						dpMap.put(dvTruncatedLabel, dvTruncatedColList.get(i).toString());
					}
				}
				if(!dpMap.containsKey(drillAxis) && dpMap.size() >0)
				{
					if(drillList.size() > drillIndex && (!drillList.isEmpty() && drillList.get(drillIndex)!=null) && !drillList.get(drillIndex).equals("null"))
						dpMap.put(drillAxis, drillList.get(drillIndex).toString());

				}
				if((categorypaginationIndex-1)==categoryIndex)
				{
					
					dpList.add(dpMap);
					return dpList;
				}
				if(dpMap.size() >0)
					dpList.add(dpMap);
				categoryIndex++;
			}
			}
			
			
		}
		if(colorWiseIndex != null && !colorWiseIndex.isEmpty()) {
			if ((graphInfo.getGraphType() == GraphConstants.LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.STACKED_LINE_GRAPH
					|| graphInfo.getGraphType() == GraphConstants.PERCENTAGE_LINE_GRAPH) && (graphInfo.getGraphData().getRowLabel() == null || graphInfo.getGraphData().getColLabel() == null)) {
				colorWiseIndex.clear();
				colorWiseIndex.add(0);
			}
			setDisplayColorIndex(colorWiseIndex, graphInfo);
		}
		return dpList;

	}
	private static final String[] appendValue(String[] s1 ,String newValue) {

		String[] erg = new String[s1.length + 1];
		erg[erg.length-1] = newValue;
		System.arraycopy(s1, 0, erg, 0, s1.length);

		return erg;

	}
	//new comma
	public static String commaFormats(double in_objValue, YaxisTrendProperties yaxisTrendProperties,GraphInfo graphInfo){

		double dValue = in_objValue;
		String strData = "";
		/*ApplicationLog.info(in_objValue+"colProp..."+colProp);
		ApplicationLog.info("colProp.getValueFormat()..."+colProp.getValueFormat());
		ApplicationLog.info("colProp.getValueFormat().getNumberFormat()..."+colProp.getValueFormat().getNumberFormat());
		ApplicationLog.info("colProp.getValueFormat().getNumberFormat().getAdjustedDigit()..."+colProp.getValueFormat().getNumberFormat().getAdjustedDigit());*/
		int place = yaxisTrendProperties.getLabelProperties().getAdjustedDigit();//in_alsItemInfo.getColumnProperties()
		/*if (place > 0) {
			dValue /= Math.pow(10, place);
		}*/

		Object commaSepObj = yaxisTrendProperties.getLabelProperties().isCommaSeprator();
		boolean commaSep = false;
		if (commaSepObj != null) {
			commaSep = ((Boolean) commaSepObj).booleanValue();
		}

		int commaPosStyleObj =  yaxisTrendProperties.getLabelProperties().getCommaFormat();
		//commaPosStyleObj = false;
		boolean commaPosStyle = false;
		if (commaPosStyleObj == 2) {
			
			//commaPosStyle = ((Boolean) commaPosStyleObj).booleanValue();
			commaPosStyle = true;
		}
		
		if (commaSep && commaPosStyle
				&& (dValue <= -100000 || dValue >= 100000)) {				
			yaxisTrendProperties.getLabelProperties().setCommaSeprator(false);
			String strFmt = graphInfo.GetDecimalFormatString(yaxisTrendProperties,0);
			yaxisTrendProperties.getLabelProperties().setCommaSeprator(true);				
			java.text.DecimalFormat DecFormat = new java.text.DecimalFormat(
					strFmt);
			strData = DecFormat.format(dValue);
			strData = StringUtil.modifyDataForIndianStyleCommaPos(strData,
					null, false);
		} else {
			String strFmt = graphInfo.GetDecimalFormatString(yaxisTrendProperties,0);
			java.text.DecimalFormat DecFormat = new java.text.DecimalFormat(
					strFmt);
			// Check The Infinate & NaN value By Piyush Ramani Bug:5949
			if (Double.isInfinite(dValue) || Double.isNaN(dValue))
				strData = "";
			else
				strData = DecFormat.format(dValue);
		}
		/*if(yaxisTrendProperties.getLabelProperties().isShowadAdjustedSuffixed())
			strData += GeneralUtil.getAdjustedDigitSuffix(place);*/
	
		return strData;
	}
	
	private static void setDisplayColorIndex(List colorWiseIndex, GraphInfo graphInfo) {
		graphInfo.setDisplayBarIndexList(colorWiseIndex);
	}

}
