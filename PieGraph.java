package com.elegantjbi.amcharts;

import static org.apache.commons.lang.StringEscapeUtils.unescapeHtml;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.codehaus.jackson.map.ObjectMapper;
import org.jsoup.parser.Parser;

import com.elegantjbi.amcharts.vo.AllLabels;
import com.elegantjbi.amcharts.vo.Balloon;
import com.elegantjbi.amcharts.vo.GraphJson;
import com.elegantjbi.amcharts.vo.GraphLegendJson;
import com.elegantjbi.amcharts.vo.Legend;
import com.elegantjbi.amcharts.vo.Responsive;
import com.elegantjbi.amcharts.vo.Titles;
import com.elegantjbi.core.olap.ICubeResultSetSupport;
import com.elegantjbi.entity.graph.GraphInfo;
import com.elegantjbi.service.graph.GraphCommandNameList;
import com.elegantjbi.service.graph.GraphConstants;
import com.elegantjbi.service.kpi.KPIConstants;
import com.elegantjbi.util.AppConstants;
import com.elegantjbi.util.GeneralFiltersUtil;
import com.elegantjbi.util.GraphsUtil;
import com.elegantjbi.util.ResourceManager;
import com.elegantjbi.util.StringUtil;
import com.elegantjbi.util.logger.ApplicationLog;
import com.elegantjbi.vo.properties.kpi.TrendDataValueProperties;

public class PieGraph {

	public static String amPieJson(GraphInfo graphInfo,String dashbordTdId,boolean isContextFilter)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		List colorWiseIndex = new ArrayList<>();
		List jsonList = new ArrayList();

		List<Integer> pieMultipleMeasureIdArr = new ArrayList<Integer>();
		List<String> pieTitle = new ArrayList<String>();
		String colLabel ="";
		
		String rowLabel =graphInfo.getGraphData().getRowLabel();
		int multipleMeasures = graphInfo.getDataColLabels3().size();
		int countmm=0;
		List colList;
		List colListForMap = new ArrayList();//Added for Column Labels Bug #12443
		List<String> originalDataColList = new ArrayList<String>();
		if(graphInfo.getGraphProperties().getDataValuePropertiesMap().size() > 1 &&
				(graphInfo.getRowColumns().size() == 0 || graphInfo.getColColumns().size() == 0))
		{
			colList  = graphInfo.getGraphData().getColLabelsName();
			//
			boolean emptyRow = null != graphInfo.getRowColumns() && (graphInfo.getRowColumns().isEmpty() || graphInfo.getRowColumns().get(0).equals(""));//
			boolean emptyCol = null != graphInfo.getColColumns() && (graphInfo.getColColumns().isEmpty() || graphInfo.getColColumns().get(0).equals(""));
			if (emptyRow && emptyCol && graphInfo.getGraphType() == GraphConstants.PIE_GRAPH) {
				colList = graphInfo.getGraphData().getColList();
				colListForMap = graphInfo.getGraphData().getColList();
			}
			if(!colList.isEmpty()) {
				Map<String, String> colLabelsMap = graphInfo.getGraphProperties().getColLabelsMap();
				for (int i = 0; i < colList.size(); i++) {
					for (Entry<String, String> e : colLabelsMap.entrySet()) {
						if(colList.get(i).equals(e.getValue()))
							originalDataColList.add(e.getKey());
					}
				}
			}
			if(originalDataColList.isEmpty())
				originalDataColList.addAll(colList);
			colListForMap.addAll(originalDataColList);
			//
		}
		else
		{
			colList = graphInfo.getGraphData().getColList();
			colListForMap = graphInfo.getGraphData().getColList();
		}
		int colListSize ;

		String drillAxis = "drillAxis";
		String drillLegend = "drillLegend";
		List drillList = graphInfo.getGraphData().getDrillLinkList();
		//List dataList = graphInfo.getGraphData().getDataList();
		String dataLabel = graphInfo.getGraphData().getDataLabel();
		List rowList = graphInfo.getGraphData().getRowList();
		int rowListSize = rowList.size();
		Map<String, String> pieDrillMap = new HashMap<>();//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
		
		List dateColList = graphInfo.getGraphData().getDatecolList();
		List dateRowList = graphInfo.getGraphData().getDaterowList();
		boolean isMultiMeasure = graphInfo.getDataColLabels3().size()>1 && (dataLabel.equalsIgnoreCase("Data") || dataLabel==null || dataLabel.equalsIgnoreCase("null"));

		boolean isLegendVisible = true;
		boolean rowFlag = true;
		List tmpList;
		int tmpListSize = 0;
		boolean emptyRow = null != graphInfo.getRowColumns() && (graphInfo.getRowColumns().isEmpty() || graphInfo.getRowColumns().get(0).equals(""));//
		boolean emptyCol = null != graphInfo.getColColumns() && (graphInfo.getColColumns().isEmpty() || graphInfo.getColColumns().get(0).equals(""));

		if(rowListSize == 0){
			rowFlag = false;
			rowListSize = 1;
			isLegendVisible =  false;
			colLabel = graphInfo.getGraphData().getColLabel();
			tmpList = colList;
			colListSize = 1;/*colList.size();*/
		}
		else
		{
			colLabel = graphInfo.getGraphData().getRowLabel();
			tmpList = rowList;
			colListSize =  colList.size();
		}
		tmpListSize = tmpList.size();
		String json = null;
		String zaxisValue = "zaxisvalue";
		String zaxisValueMouseOver = "zaxisValueMouseOver"; 
		int nullSize = colListSize*tmpListSize;
		if(drillList.isEmpty() || nullSize > graphInfo.getGraphData().getDrillLinkList().size())
		{
			for(int i=0;i<nullSize;i++)
			{
				drillList.add("null");
			}
		}
		
		/*String[] colors = new String[]{"rgb(141,170,203)","rgb(252,115,98)","rgb(187,216,84)","rgb(255,217,47)","rgb(102,194,150)",
				"rgb(255, 148, 10)","rgb(148, 247, 244)"};*/
		String[] colors =new String[]{"#67b7dc","#6794dc","#6771dc","#8067dc","#a367dc","#c767dc","#dc67ce","#dc67ab","#dc6788","#dc6967",
			    "#dc8c67","#dcaf67","#dcd267","#c3dc67","#a0dc67","#7ddc67","#67dc75","#67dc98","#67dcbb","#67dadc",
			    "#80d0f5","#80adf5","#808af5","#9980f5","#bc80f5","#e080f5","#f580e7", "#f7d584", "#b1fb83", "#50407f", 
			    "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c", "#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296",
			    "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424",
			    "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92",
			    "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"}; 

		String[] Quarter = new String[]{"Q1","Q2","Q3","Q4"};
		String[] month = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		
		boolean colLabelsName = false;
		if(graphInfo.getGraphData().getColLabelsName() != null && graphInfo.getGraphData().getColLabelsName().size()>0)
			colLabelsName = true;

		switch(graphInfo.getGraphProperties().getColorType())
		{
		case 1:
			if(graphInfo.getGraphProperties().getCustomColors() != null)
			{
				for (int i = 0; i < graphInfo.getGraphProperties().getCustomColors().size(); i++) {
					if(i > (colors.length-1))
					{
						colors = appendValue(colors, graphInfo.getGraphProperties().getCustomColors().get(i));
					}
					else
					{	
						colors[i] = graphInfo.getGraphProperties().getCustomColors().get(i);
					}
				}

			}
			break;
		case 2: 
			colors = new String[]{graphInfo.getGraphProperties().getColor()};
			break;
		}

		int noOfColors = colors.length;
		//graph fill area transparency start
		double getAreaTransparency = (double)graphInfo.getGraphProperties().getTranceperancy();
		double fillAlpha = (100 - getAreaTransparency) / 100;
		//graph fill area transparency end

		List<Integer> multipleMeasureDataValueIndexArr = new ArrayList<Integer>();
		for (int itr = 0; itr < colListSize; itr++) {
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
		
		
		int precisionLabelCounter=1;
		if(graphInfo.getDataColLabels3().size() > 1 
				&& (graphInfo.getGraphData().getRowLabel()!=null && graphInfo.getGraphData().getRowLabel().equalsIgnoreCase("Legend")))
		{
			precisionLabelCounter = graphInfo.getDataColLabels3().size();
		}
		List precisionLabelList= new ArrayList();
		for(int i=0;i<precisionLabelCounter;i++)
		{
			String precisionLabel="";
			if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)).getNumberFormat().isShowadAdjustedSuffixed())
			{	
				int prefix = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)).getNumberFormat().getAdjustedDigit();
				
				switch(prefix)
				{
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
				
			}
			precisionLabelList.add(precisionLabel);
		}
		int multipleMeasureDataValueIndex;
		
		//performance changes start
		String mapKeyStr = "";
		Double dataValue = 0.0;
		Map keyValueMap = graphInfo.getGraphData().getKeyValueMap();
		int adjustedDigit = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+0).getNumberFormat().getAdjustedDigit();
		Double d = new Double(adjustedDigit);
		int divValue = (int) (Math.pow(10, d));
		boolean isMultipleMeasure = graphInfo.getDataColLabels3().size() > 1 && rowLabel != null && rowLabel.equalsIgnoreCase("Legend");
		//performance changes end
		
		for (int i = 0; i < colListSize; i++) {
			GraphJson graphJson = new GraphLegendJson();

			//Start
			graphJson.setType("pie");
			graphJson.setObjectType("graph");//BUG: 13842
			graphJson.setTheme("none");
			graphJson.setAddClassNames(true);
			graphJson.setStartEffect("easeOutSine");
			graphJson.setSequencedAnimation(false);
			graphJson.setColors(Arrays.asList(colors));
			graphJson.setAlpha(fillAlpha);
			graphJson.setColumnSpacing(0);
			graphJson.setMouseWheelZoomEnabled(false);
			graphJson.setRadius(graphInfo.getGraphProperties().getPieGraph().getRadius() + "%");
			//graphJson.setRadiusIncrement(50);
			graphJson.setChartType("pie");
			graphJson.setBringToFront(true);

			//maxLabelWidth  is used if labels cut out of screen
			graphJson.setMaxLabelWidth(75);
			//angle of pie
			graphJson.setAngle(graphInfo.getGraphProperties().getPieGraph().getAngle());
			//start angle
			graphJson.setStartAngle(graphInfo.getGraphProperties().getPieGraph().getStartAngle());
			graphJson.setTitles(new ArrayList<Titles>());
			graphJson.setAllLabels(new ArrayList<AllLabels>());
			//------------------------------------------- Pie Title Start-----------------------------------------------
			String colTitleTruncateLabels = "";
			if(!colList.isEmpty() && colList.size() > i) {
				colTitleTruncateLabels = colList.get(i).toString();//Added for Bug #15242
			}
			if(graphInfo.getGraphProperties().getPieTitle().isVisible())
			{
				String titleTruncateLabels = "";
				int truncateCharLimit = 15;
				switch(graphInfo.getGraphProperties().getPieTitle().getFontProp().getCharacterLimit())
				{
				case "auto":
					if(colList.size()!= 0)
						titleTruncateLabels = colList.get(i).toString();
					truncateCharLimit = 15;
					if (titleTruncateLabels.length() > truncateCharLimit)
						titleTruncateLabels = titleTruncateLabels.substring(0, truncateCharLimit)+"..";
					colTitleTruncateLabels = titleTruncateLabels;
					break;
				case "custom":
					if(colList.size()!= 0)
						titleTruncateLabels = colList.get(i).toString();
					truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getPieTitle().getFontProp().getCustomCharacterLimit());
					if (titleTruncateLabels.length() > truncateCharLimit)
						titleTruncateLabels = titleTruncateLabels.substring(0, truncateCharLimit)+"..";
					colTitleTruncateLabels = titleTruncateLabels;
					break;
				default:
					if(colList.size()!= 0)
						titleTruncateLabels = colList.get(i).toString();
					break;
				}
				if(rowFlag)
				{
					if(titleTruncateLabels != null)
						titleTruncateLabels = Parser.unescapeEntities(titleTruncateLabels, false);
					pieTitle.add(titleTruncateLabels);
				}
				if(colList.size()!= 0)
					graphJson.setCategoryField(colList.get(i).toString());
			}

			//------------------------------------------- Pie Title End-----------------------------------------------
			//------------------------------------------- Graph area start-----------------------------------------------
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isVisible()) {
				graphJson.setDepth3D(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getPieDepth());
			}
			else
			{
				graphJson.setDepth3D(0);
			}
			//Margin
			float marginAll = graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().getGeneralProperties().getPanelMargin().getAll();
			//Margin start
			//graphJson.setAutoMarginOffset(marginAll);
			graphJson.setMarginTop(marginAll);
			graphJson.setMarginBottom(marginAll);
			graphJson.setMarginLeft(marginAll);
			graphJson.setMarginRight(marginAll);
			//Margin end
			//------------------------------------------- Graph area end-----------------------------------------------
			//------------------------------------------- Data Provider Start-----------------------------------------------
			

			List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();
			Map<String, String> drillMap =null;
			int drillIndex = i;
			if(isLegendVisible)
			{
				drillIndex=rowListSize+i;
				drillMap = new HashMap<String, String>();
			}

			for (int j = 0; j < tmpListSize; j++) {
				int dataIndex = 0;
				if(isLegendVisible)
				{
					dataIndex = tmpListSize*i + j;
				}
				else
				{
					dataIndex = j;
				}

				Map<String, Object> dpMap =  new HashMap<String, Object>();
				String legendTruncateLabels = tmpList.get(j).toString();
				//Added code for Bug #13406 start
				if(!dateRowList.isEmpty() && dateRowList.size() > j
						&& null != dateRowList.get(j) && !dateRowList.get(j).equals(AppConstants.NULL_DISPLAY_VALUE)) {
					String stringFormat;
					stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
					stringFormat = stringFormat.replaceAll("&#39;", "'");
					Calendar cal = Calendar.getInstance();
					Date axisDate = new Date();
					axisDate = (Date) dateRowList.get(j);
					cal.setTime(axisDate);
					stringFormat=stringFormat.trim();
					legendTruncateLabels = new SimpleDateFormat(stringFormat).format(cal.getTime());
				}//Added code for Bug #13406 end
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
				{
				case "custom":
					legendTruncateLabels = tmpList.get(j).toString();
					int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
					if (legendTruncateLabels.length() > truncateCharLimit)
						legendTruncateLabels = legendTruncateLabels.substring(0, truncateCharLimit)+"..";
					break;
				/*default:
					legendTruncateLabels = tmpList.get(j).toString();
					break;*/
				}
				dpMap.put("disableToggle", true);
				dpMap.put("index", j);				
				dpMap.put(zaxisValue, legendTruncateLabels);//instead of legendTruncateLabels kept tmpList.get(j).toString() for bug 14300
				dpMap.put(zaxisValueMouseOver, tmpList.get(j).toString());
				
				colorWiseIndex.add(graphInfo.getColorInfoList().get(j)%colors.length);
				dpMap.put("color", colors[graphInfo.getColorInfoList().get(j)%colors.length]);
				if(drillList.size() != 0)
				{
					if(graphInfo.isMultipleYAxisLabelsEnable() && (graphInfo.getGraphData().getRowLabel().equals("Legend")))
					{
						dpMap.put(drillLegend, drillList.get(j+colListSize).toString());
					}
					else
					{
						dpMap.put(drillLegend, drillList.get(j).toString());
					}
					dpMap.put(drillAxis, drillList.get(drillIndex).toString());
					/*if(!colList.isEmpty() && colList.size() > i) {
						pieDrillMap.put(colList.get(i).toString()+dashbordTdId, drillList.get(drillIndex).toString());//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
					}*/
					pieDrillMap.put(colTitleTruncateLabels+dashbordTdId, drillList.get(drillIndex).toString());//Changes for Bug #15242 (30 Sept 2019)
				}
				
				//performance changes start
				mapKeyStr = "";
				if(isMultipleMeasure)//when multiple measure
				{
					if(null != rowList && rowList.size() > 0)
						mapKeyStr += rowList.get(j).toString();
					if(null != colListForMap && colListForMap.size() > 0)//Changed from colList for Column Labels Bug #12443
						mapKeyStr += colListForMap.get(i).toString();
				}
				else if(isLegendVisible)//when both Row and Col
				{
					if(null != colList && colList.size() > 0)
						mapKeyStr += colList.get(i).toString();
					if(null != rowList && rowList.size() > 0)
						mapKeyStr += rowList.get(j).toString();
					if(!isMultipleMeasure)
						mapKeyStr += graphInfo.getDataColLabels3().get(0).toString();
				}
				else
				{
					if(null != colList && colList.size() > 0)
						mapKeyStr += colList.get(j).toString();
					/*if(null != rowList && rowList.size() > 0)
						mapKeyStr += rowList.get(j).toString();*/
					if(!isMultipleMeasure)
						mapKeyStr += graphInfo.getDataColLabels3().get(0).toString();
				}
				//performance changes end
				
				/*if(!dataList.isEmpty())
				{*/	
					if(keyValueMap.get(mapKeyStr) != null)
					{
						dataValue = Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / divValue;//performance changes
						
						//dpMap.put("Abs"+dataLabel, roundDecimalValue(((Double)dataList.get(dataIndex)),graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits()));
						Double parseValue = 0.0;
						parseValue = dataValue;
						if(dataValue < 0)//for displaying -ve values
						{
							
							dpMap.put(dataLabel, (dataValue * -1));
							//dpMap.put(dataLabel, roundDecimalValue(((Double)dataList.get(dataIndex) * -1),graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits()));
						}
						else
						{
							dpMap.put(dataLabel, dataValue);
							//parseValue = Double.valueOf(roundDecimalValue((Double)dataList.get(dataIndex),graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits()));
						}
						 String commaSeparated = commaFormats(parseValue, graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()), graphInfo);
						 dpMap.put("AbsValue"+i, commaSeparated);
	                     
	                      //for showing multiple measure value in mouseover and datavalue	                     
	                     if(isMultiMeasure)
	                     {
	                    	 
	                    	 for(int k=0;k<graphInfo.getDataColLabels3().size();k++)
	                    	 {
	                    		
	                    		 if(!(graphInfo.getDataColLabels3().get(k).equals(graphInfo.getDataColLabels3().get(i))))
	                    		 {
	                    				 mapKeyStr=tmpList.get(j)+graphInfo.getDataColLabels3().get(k).toString();
	                    				 if(keyValueMap.get(mapKeyStr)!=null)
	                    				 {
	                    					 parseValue=Double.valueOf(keyValueMap.get(mapKeyStr).toString()) / divValue;
	                    					 commaSeparated = commaFormats(parseValue, graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()), graphInfo);
	                						 //System.out.println(" \"AbsValue"+k +"commaSeparated="+commaSeparated);
	                	                     dpMap.put("AbsValue"+k, commaSeparated);
	                    				 }
	                    		 }
	                    	 }
	                    		 
	                     }
					/*	if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().isCommaSeprator())
						{
							switch(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getCommaFormat())
							{
							case 1:
								commaSeparated = parseformat("#,###.##", parseValue, graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits());
								dpMap.put("Abs"+dataLabel, commaSeparated);
								break;
							case 2:
								try{
									if(parseValue < 1000) {
										commaSeparated = parseformat("###", parseValue,graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits());
									} else {
										double hundreds = parseValue % 1000;
										int other = (int) (parseValue / 1000);
										commaSeparated = parseformat(",##", other,0) + ',' + parseformat("000", hundreds,graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits());
									}
								}catch(Exception e){
									System.out.println(e);
								}
								dpMap.put("Abs"+dataLabel, commaSeparated);	
								break;
							}
						}*/
					}
				//}
				dpMap.put("xaxistitle", StringUtil.replaceSpecialCharWithHTMLEntity(graphInfo.getGraphData().getColLabel()));
				String yAxisValue="";
				String yAxisValueOriginal="";
				if(dataLabel != null && (dataLabel.equalsIgnoreCase("null") || dataLabel.equalsIgnoreCase("Data")))
				{
					if(colListSize > 1)
					{
						yAxisValue = colList.get(i).toString();
						yAxisValueOriginal = originalDataColList.get(i).toString();
					}	
				}
				else
				{
					if(colLabelsName) {
						yAxisValue = graphInfo.getGraphData().getColLabelsName().get(0).toString();
						yAxisValueOriginal = graphInfo.getDataColLabels3().get(0).toString();
					}
					else {
						yAxisValue = dataLabel;
						yAxisValueOriginal = dataLabel;
					}
				}
				dpMap.put("yaxisTitle"+yAxisValueOriginal.replaceAll("[^\\s\\w]*",""), unescapeHtml(yAxisValue));
				
				String zAxisTitle ="";
				if(isLegendVisible)
				{
					if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
					{
						zAxisTitle = graphInfo.getGraphData().getColLabel();
					}
					else
					{
						zAxisTitle = rowLabel;
					}
				}
				else
				{
					zAxisTitle = graphInfo.getGraphData().getColLabel();
				}
				dpMap.put("zaxisTitle", StringUtil.replaceSpecialCharWithHTMLEntity(zAxisTitle));
				dpList.add(dpMap);
			}
			graphJson.setDataProvider(dpList);
			//------------------------------------------- Data Provider End--------------------------------------------------
			//------------------------------------------- Pie Start--------------------------------------------------
			String innerRadius = Integer.toString(graphInfo.getGraphProperties().getPieGraph().getDoughnutHole());	
			graphJson.setInnerRadius(innerRadius+"%");
			if(graphInfo.getGraphProperties().getPieGraph().getBorderProperties().isVisible()){
				graphJson.setOutlineAlpha(1);
				graphJson.setOutlineColor(graphInfo.getGraphProperties().getPieGraph().getBorderProperties().getAllBorderColor());
				graphJson.setOutlineThickness(graphInfo.getGraphProperties().getPieGraph().getBorderProperties().getAllBorderWidth());
			}
			//------------------------------------------- Pie End--------------------------------------------------
			double startDuration = 0.0;
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isAnimationPlayable())
				startDuration = 0.25;
			graphJson.setStartDuration(startDuration);//Play or Stop Animation
			
			graphJson.setPieAnimationEnable(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isAnimationPlayable());

			graphJson.setPullOutOnlyOne(true);
			graphJson.setPullOutRadius(0);
			graphJson.setPulledField("pullOut");
			graphJson.setColorField("color");
			graphJson.setLabelColor("#fff");
			graphJson.setLineAlpha(0);
			graphJson.setTitleField(zaxisValue);
			graphJson.setValueField(dataLabel);
			graphJson.setDescriptionField("Abs"+dataLabel);
			pieMultipleMeasureIdArr.add(i);
			// data value digits after decimal start
			int precision = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getNumberOfDigits();
			int percentagePrecision=graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+i%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getNumberFormat().getPercentageValue();
			graphJson.setPercentPrecision(percentagePrecision);//Feture request raised by Jignesh Patel
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
			//Data value start
			if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().isDataValuePointVisible())
			{
				String dataVAlues = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().getDataValuePointFormatText();
				dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsValue"+i+"]]");
				dataVAlues = dataVAlues.replace("[[AbsValue"+i+"]]", "[[AbsValue"+i+"]]"+precisionLabelList.get(i%precisionLabelList.size()));
				String X_AXIS_VALUE = "";				
							
				dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.PERCENT_VALUE,"[[percents]]"+"%");
				
				
				countmm = 0;
				if(isMultiMeasure)
				{
					for(int m=0; m<multipleMeasures;m++)	{					
						//if(!(graphInfo.getDataColLabels3().get(m).equals(originalDataColList.get(i)))) {
							++countmm;
							dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_VALUE"+countmm+"$","[[AbsValue"+m+"]]"+precisionLabelList.get(i%precisionLabelList.size()));														
						//}
					}
				}
				
				//X_AXIS_TITLE start
				if(graphInfo.getDataColLabels3().size() > 1 && graphInfo.getGraphData().getDataLabel().equals("Data"))
				{
					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					if(colListSize > 1 || isLegendVisible)
					{
						X_AXIS_VALUE = colList.get(i).toString();
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,StringUtil.replaceSpecialCharWithHTMLEntity(colList.get(i).toString()));
					}
					else
					{
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
					}
					
 					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
 					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[zaxisTitle]]");
				}
				else
				{
					if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
					{
						if(!colLabel.equalsIgnoreCase("Legend"))
							dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					}
					if(colListSize > 1 || isLegendVisible)
					{
						X_AXIS_VALUE = colList.get(i).toString();
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_VALUE,StringUtil.replaceSpecialCharWithHTMLEntity(colList.get(i).toString()));
					}
					else
					{
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
					}
					
					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
				}
				/*if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
				{
					if(!colLabel.equalsIgnoreCase("Legend"))
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					else
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"");
				}*/
				/*else
					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"");*/
				//X_AXIS_TITLE end
				
				//Z_AXIS_TITLE start
				/*if(isLegendVisible)
				{
					if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
					{
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					}
					else
					{
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					}
				}
				else
				{*/
				if(graphInfo.getDataColLabels3().size() > 1 && graphInfo.getGraphData().getDataLabel().equals("Data"))
				{
					if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
					{
						if(!colLabel.equalsIgnoreCase("Legend"))
							dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					}
				}
				else
				{
					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
				}
					
				/*}*/
				//Z_AXIS_TITLE end
				
				//Y_AXIS_TITLE start
				if(dataLabel != null && (dataLabel.equalsIgnoreCase("null") || dataLabel.equalsIgnoreCase("Data")))
				{
					if(originalDataColList.size() > 0)
						X_AXIS_VALUE = originalDataColList.get(i).toString();
					dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+X_AXIS_VALUE.replaceAll("[^\\s\\w]*","")+"]]");
					
					
					countmm = 0;
					if(isMultiMeasure)
					{
						for(int m=0; m<multipleMeasures;m++)	{					
							//if(!(graphInfo.getDataColLabels3().get(m).equals(originalDataColList.get(i)))) {
								++countmm;
								dataVAlues = StringUtil.replace(dataVAlues,"$Y-AXIS_TITLE"+countmm+"$",graphInfo.getDataColLabels3().get(m).toString());														
							//}
						}
					}
				}
				else
				{
					//dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+dataLabel.replaceAll("[^\\s\\w]*","")+"]]");
					if(colLabelsName)
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(0).toString().replaceAll("[^\\s\\w]*","")+"]]");
					else
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+dataLabel.replaceAll("[^\\s\\w]*","")+"]]");
				}
				//Y_AXIS_TITLE end
				
				//dataVAlues = Parser.unescapeEntities(dataVAlues, false);	
				int labelTickAlpha = 0;
				switch(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().getPosition())
				{
				case "Inside":
					graphJson.setLabelRadius(-25);
					break;
				case "OutSide":
					if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().getDataPointLineVisible())
					{
						labelTickAlpha = 1;
						graphJson.setLabelTickColor(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().getDataPointLineColor());
						graphJson.setLabelRadius(5);
					}

					break;
				}
				graphJson.setLabelTickAlpha(labelTickAlpha);
				if(dataVAlues!=null)
					dataVAlues = Parser.unescapeEntities(dataVAlues, false);	
				graphJson.setLabelText(dataVAlues);
				graphJson.setLabelFunction("");
				graphJson.setColor(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValuePoint().getFontProperties().getFontColor());
			}
			else
			{
				graphJson.setLabelText("");
			}
			//Data value end

			//------------------------------------------- Data value mouse over start--------------------------------------------------
			Balloon balloon = new Balloon();
			if(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValueMouseOver().isMouseOverTextEnable())
			{
				String mouseOverString = graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValueMouseOver().getDataValueMouseOverFormatText();
				if(mouseOverString!=null){
					mouseOverString=mouseOverString.replace("&lt;/br&gt", "");
					mouseOverString=mouseOverString.replace("&lt;br&gt", "");
					}
				mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_VALUE,"[[AbsValue"+i+"]]");
				mouseOverString = mouseOverString.replace("[[AbsValue"+i+"]]", "[[AbsValue"+i+"]]"+precisionLabelList.get(i%precisionLabelList.size()));
				String X_AXIS_VALUE = "";
				
				countmm = 0;
				if(isMultiMeasure)
				{
					for(int m=0; m<multipleMeasures;m++)	{					
						//if(!(graphInfo.getDataColLabels3().get(m).equals(graphInfo.getDataColLabels3().get(i)))) {
							++countmm;
							mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_VALUE"+countmm+"$","[[AbsValue"+m+"]]"+precisionLabelList.get(i%precisionLabelList.size()));														
						//}
					}
				}
				/*if(colListSize > 1)
				{
					X_AXIS_VALUE = colList.get(i).toString();
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,StringUtil.replaceSpecialCharWithHTMLEntity(colList.get(i).toString()));
				}
				else
				{
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[["+zaxisValue+"]]");
				}
				
				mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,"[["+zaxisValue+"]]");
				mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.PERCENT_VALUE,"[[percents]]"+"%");*/
				
				
				
				
				/*//X_AXIS_TITLE start
				if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
				{
					if(!colLabel.equalsIgnoreCase("Legend"))
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					else
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"");
				}*/
				//
				mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.PERCENT_VALUE,"[[percents]]"+"%");
				
				//X_AXIS_TITLE start
				if(graphInfo.getDataColLabels3().size() > 1 && graphInfo.getGraphData().getDataLabel().equals("Data"))
				{
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					if(colListSize > 1 || isLegendVisible)
					{
						X_AXIS_VALUE = colList.get(i).toString();
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,StringUtil.replaceSpecialCharWithHTMLEntity(colList.get(i).toString()));
					}
					else
					{
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
					}
					
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[zaxisTitle]]");
				}
				else
				{
					if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
					{
						if(!colLabel.equalsIgnoreCase("Legend"))
							mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					}
					if(colListSize > 1 || isLegendVisible)
					{
						X_AXIS_VALUE = colList.get(i).toString();
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,StringUtil.replaceSpecialCharWithHTMLEntity(colList.get(i).toString()));
					}
					else
					{
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
					}
					
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_VALUE,"[["+zaxisValueMouseOver+"]]");
				}
				/*if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
				{
					if(!colLabel.equalsIgnoreCase("Legend"))
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					else
						dataVAlues = StringUtil.replace(dataVAlues,GraphCommandNameList.X_AXIS_TITLE,"");
				}*/
				//
				/*else
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"");*/
				//X_AXIS_TITLE end
				
				//Z_AXIS_TITLE start
		/*		if(isLegendVisible)
				{
					if(rowLabel != null && rowLabel.equalsIgnoreCase("Legend"))
					{
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					}
					else
					{
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
					}
				}
				else
				{*/
				if(graphInfo.getDataColLabels3().size() > 1 && graphInfo.getGraphData().getDataLabel().equals("Data"))
				{
					if(colLabel != null && !colLabel.equalsIgnoreCase("null"))
					{
						if(!colLabel.equalsIgnoreCase("Legend"))
							mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.X_AXIS_TITLE,"[[xaxistitle]]");
					}
				}
				else
				{
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Z_AXIS_TITLE,"[[zaxisTitle]]");
				}
				/*}*/
				//Z_AXIS_TITLE end
				
				//Y_AXIS_TITLE start
				if(dataLabel != null && (dataLabel.equalsIgnoreCase("null") || dataLabel.equalsIgnoreCase("Data")))
				{
					if(originalDataColList.size() > 0)
						X_AXIS_VALUE = originalDataColList.get(i).toString();
					mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+X_AXIS_VALUE.replaceAll("[^\\s\\w]*","")+"]]");
					
					countmm = 0;
					if(isMultiMeasure)
					{
						for(int m=0; m<multipleMeasures;m++)	{					
							//if(!(graphInfo.getDataColLabels3().get(m).equals(graphInfo.getDataColLabels3().get(i)))) {
								++countmm;
								mouseOverString = StringUtil.replace(mouseOverString,"$Y-AXIS_TITLE"+countmm+"$",graphInfo.getDataColLabels3().get(m).toString());														
							//}
						}
					}
				}
				else
				{
					
					if(colLabelsName)
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+graphInfo.getDataColLabels3().get(0).toString().replaceAll("[^\\s\\w]*","")+"]]");
					else
						mouseOverString = StringUtil.replace(mouseOverString,GraphCommandNameList.Y_AXIS_TITLE,"[[yaxisTitle"+dataLabel.replaceAll("[^\\s\\w]*","")+"]]");
				}
				//Y_AXIS_TITLE end
				
				String customClass = "amcharts-balloon-textM"+i;
				mouseOverString = "<div class='"+customClass+"'>"+mouseOverString + "</div>";
				graphJson.setBalloonText(mouseOverString);

				balloon.setEnabled(true);
				balloon.setFontSize(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValueMouseOver().getDataValueMouseOverFont().getFontSize());
				balloon.setColor(graphInfo.getGraphProperties().getDataValuePropertiesMap().get("M"+multipleMeasureDataValueIndexArr.get(i)%graphInfo.getGraphProperties().getDataValuePropertiesMap().size()).getDataValueMouseOver().getDataValueMouseOverFont().getFontColor());
				balloon.setAdjustBorderColor(true);
				balloon.setBorderThickness(0);

			}
			else
			{
				graphJson.setBalloonText("");
				balloon.setEnabled(false);
			}
			graphJson.setBalloon(balloon);
			//------------------------------------------- Data value mouse over end--------------------------------------------------
			//------------------------------------------- Legend Start--------------------------------------------------
			Legend legend1= new Legend();
			legend1.setEnabled(false);
			legend1.setShowEntries(false);
			if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible())
			{
				if(colListSize == 1)
				{	
					legend1.setShowEntries(true);
					legend1.setEnabled(true);
					if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isDrillDown())
					{
						legend1.setSwitchable(false);
					}
					else
					{
						legend1.setSwitchable(true);
					}
					if(isContextFilter)
					{
						legend1.setSwitchable(false);
					}
					List<Map<String, Object>>  dataRulesMap =  new	ArrayList<Map<String,Object>>();
					List<Integer> updatedColorInfoList = graphInfo.getColorInfoList();
					if(null != graphInfo.getLegendColorInfoList() && !graphInfo.getLegendColorInfoList().isEmpty()) {
						updatedColorInfoList = graphInfo.getLegendColorInfoList();
						if(graphInfo.getLegendColorInfoList().size() !=updatedColorInfoList.size()) {
					    	  updatedColorInfoList = graphInfo.getColorInfoList();
					     }
					}
					
					
					 if(null!= graphInfo.getGraphProperties() && null !=graphInfo.getGraphProperties().getLegendCustomValueList() && 
								!graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()) {
						
		/*					Map<String, Integer> colorMapping = new HashMap<>();
					        for (int k = 0; k < graphInfo.getLovListForColor().size(); k++) {
					        	if(graphInfo.getColorInfoList().size()>k) 
					            colorMapping.put(graphInfo.getLovListForColor().get(k), graphInfo.getColorInfoList().get(k));
					        }
		
					        // Create a new list for updated colorInfoList
					        updatedColorInfoList = new ArrayList<>();
					        for (Object category : graphInfo.getGraphProperties().getLegendCustomValueList()) {
					        	if(null!= colorMapping.get(category))     
					            updatedColorInfoList.add(colorMapping.get(category));
					        }*/
					        if(graphInfo.getLegendColorInfoList().size() !=updatedColorInfoList.size()) {
						    	  updatedColorInfoList = graphInfo.getColorInfoList();
						     }
					 }
					 //ApplicationLog.info(updatedColorInfoList);
						
					List legendValList = new ArrayList<>();
					legendValList.addAll(tmpList);

					if (graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
							.getLegendValuesOrder().equalsIgnoreCase("option3")
							&& graphInfo.getGraphProperties().getLegendCustomValueList() != null
							&& !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()&& graphInfo.getDrilldownBreadcrumbMap() == null) {
						legendValList.clear();
						legendValList.addAll(graphInfo.getGraphProperties().getLegendCustomValueList());
					}
					Map<String, Object> dataMapOther =  new HashMap<>();
					for (int k = 0; k < tmpListSize; k++) {
						Map<String, Object> dataMap =  new HashMap<String, Object>();
						if(isLegendVisible)
						dataMap.put("drillLegend", drillList.get(k).toString());
						String tmp = legendValList.get(k).toString();
						String tmp1 = legendValList.get(k).toString();
						if(colLabelsName && null != graphInfo.getGraphData().getColLabelsName() && !graphInfo.getGraphData().getColLabelsName().isEmpty() && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
								&& graphInfo.getGraphData().getRowLabel()!=null  &&  k < graphInfo.getGraphData().getColLabelsName().size() && tmp!= graphInfo.getGraphData().getColLabelsName().get(k) && null !=rowList && !rowList.isEmpty()  &&!rowList.get(k).equals(graphInfo.getGraphData().getColLabelsName().get(k))) {
								tmp = graphInfo.getGraphData().getColLabelsName().get(i).toString();
						}
						
						//Added code for Bug #13406 start
						if(!dateRowList.isEmpty() && dateRowList.size() > k
								&& null != dateRowList.get(k) && !dateRowList.get(k).equals(AppConstants.NULL_DISPLAY_VALUE)) {
							String stringFormat;
							stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
							stringFormat = stringFormat.replaceAll("&#39;", "'");
							Calendar cal = Calendar.getInstance();
							Date axisDate = new Date();
							axisDate = (Date) dateRowList.get(k);
							cal.setTime(axisDate);
							stringFormat=stringFormat.trim();
							tmp = new SimpleDateFormat(stringFormat).format(cal.getTime());
						}//Added code for Bug #13406 end
						if (null !=graphInfo.getDataColLabels3() && graphInfo.getDataColLabels3().size() < 2 && graphInfo.getDateFrequencyMap() != null && !graphInfo.getDateFrequencyMap().isEmpty() && null != graphInfo.getRowColumns() && !graphInfo.getRowColumns().isEmpty() && null != graphInfo.getDateFrequencyMap().get(graphInfo.getRowColumns().elementAt(0).toString()) && !graphInfo.getDateFrequencyMap().get(graphInfo.getRowColumns().elementAt(0).toString()).isEmpty()) {
										tmp  = GraphsUtil.getLegendDateFormat(graphInfo,tmp);
							/* Map<String, String> dateFrequencyMap = graphInfo.getDateFrequencyMap();
							String frequency = "";
							if (null != graphInfo.getColColumns() && !graphInfo.getColColumns().isEmpty()) {
								String strCol = graphInfo.getColColumns().elementAt(0).toString();
								if (dateFrequencyMap != null && !dateFrequencyMap.isEmpty() && dateFrequencyMap.get(strCol) != null
										&& !dateFrequencyMap.get(strCol).isEmpty()) {
									frequency = dateFrequencyMap.get(strCol);
									
								}
							}
							if (null != graphInfo.getRowColumns() && !graphInfo.getRowColumns().isEmpty()) {
								String strCol = graphInfo.getRowColumns().elementAt(0).toString();
								if (dateFrequencyMap != null && !dateFrequencyMap.isEmpty() && dateFrequencyMap.get(strCol) != null
										&& !dateFrequencyMap.get(strCol).isEmpty()) {
									frequency = dateFrequencyMap.get(strCol);
									
								}
							}
							if(frequency != null && !frequency.isEmpty() ) {
								if(frequency.equalsIgnoreCase(KPIConstants.FREQUENCY_QUARTERLY) || frequency.equalsIgnoreCase(KPIConstants.FREQUENCY_MONTHLY) || frequency.equalsIgnoreCase(KPIConstants.FREQUENCY_WEEKLY)) {
								
									String stringFormat;
									stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat();
									stringFormat = stringFormat.replaceAll("&#39;", "'");
									Calendar cal = Calendar.getInstance();
									
									String strData = tmp;
									int iColumnType = 0;
												
												try {
													
												if(null != dateFrequencyMap.get(graphInfo.getColColumns().elementAt(0).toString()) && !dateFrequencyMap.get(graphInfo.getColColumns().elementAt(0).toString()).isEmpty())
													iColumnType = GeneralFiltersUtil.getCubeColumnType(graphInfo.getCubeInfo(),graphInfo.getColColumns().elementAt(0).toString());
												else if(null != dateFrequencyMap.get(graphInfo.getRowColumns().elementAt(0).toString()) && !dateFrequencyMap.get(graphInfo.getRowColumns().elementAt(0).toString()).isEmpty())
													iColumnType = GeneralFiltersUtil.getCubeColumnType(graphInfo.getCubeInfo(),graphInfo.getRowColumns().elementAt(0).toString());
												} catch (Exception e) {
													ApplicationLog.error(e);
												}
												
								if(null != dateFrequencyMap.get(graphInfo.getColColumns().elementAt(0).toString()) && !dateFrequencyMap.get(graphInfo.getColColumns().elementAt(0).toString()).isEmpty())			
									strData = StringUtil.getValuebyDateColumn(strData, iColumnType, graphInfo.getColColumns().elementAt(0).toString(), dateFrequencyMap,stringFormat);
								else if(null != dateFrequencyMap.get(graphInfo.getRowColumns().elementAt(0).toString()) && !dateFrequencyMap.get(graphInfo.getRowColumns().elementAt(0).toString()).isEmpty())
									strData = StringUtil.getValuebyDateColumn(strData, iColumnType, graphInfo.getRowColumns().elementAt(0).toString(), dateFrequencyMap,stringFormat);
									tmp = strData;
								}
								
							
							}
						*/	
						}
						switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
						{
						case "auto":
							//tmp = tmpList.get(k).toString();
							int truncateCharLimit1 = 15;
							if (tmp.length() > truncateCharLimit1)
								tmp = tmp.substring(0, truncateCharLimit1)+"..";
							break;
						case "custom":
							//tmp = tmpList.get(k).toString();
							int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
							if (tmp.length() > truncateCharLimit)
								tmp = tmp.substring(0, truncateCharLimit)+"..";
							break;
						/*default:
							tmp = tmpList.get(k).toString();
							break;
*/
						}
						if(!tmp.equalsIgnoreCase("Other")) {
							dataMap.put("title", tmp);
							dataMap.put("title1", tmp1);
							dataMap.put("color", colors[updatedColorInfoList.get(k)%colors.length]);
							dataMap.put("index", k);
							dataRulesMap.add(dataMap);
							colorWiseIndex.add(graphInfo.getColorInfoList().get(k)%colors.length);
						}else {
							dataMapOther.put("title", tmp);
							dataMapOther.put("title1", tmp1);
							dataMapOther.put("color", colors[updatedColorInfoList.get(k)%colors.length]);
							dataMapOther.put("index", k);
							colorWiseIndex.add(graphInfo.getColorInfoList().get(k)%colors.length);
						}
					}
					try {
						if(!dataRulesMap.isEmpty() && !graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
								.getLegendValuesOrder().equalsIgnoreCase("option3")){
							dataRulesMap.sort(Comparator.comparing(o -> String.valueOf(o.get("title")), String.CASE_INSENSITIVE_ORDER));
							
						}
						if(!dataMapOther.isEmpty())
							dataRulesMap.add(dataMapOther);
		
					}catch(Exception e) {
						ApplicationLog.error(e);
					}
					legend1.setData(dataRulesMap);
					//------------------------------------------- Legend Panel Start--------------------------------------------------
					String position="";
					switch(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelPosition())
					{
					case 1: position ="top";break;
					case 2: position ="left";break;
					case 3: position ="right";break;
					case 4: position="bottom";break;
					}
					legend1.setPosition(position);
					//legend Visible
					if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().isVisible()) {
						legend1.setBackgroundAlpha(1);
						legend1.setBackgroundColor(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().getBackGroundColor());

						if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().isBackgroundTransparent()) {
							legend1.setBackgroundAlpha(0);
						}
						else{
							legend1.setBackgroundAlpha(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelBackgroundProperties().getTransparency()/100.0);
						}
					}
					else {
						legend1.setBackgroundAlpha(0);
						legend1.setBackgroundColor("");
					}
					//Legend Margin start
					legend1.setAutoMargins(false);
					legend1.setMarginTop(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getTopMargin());
					legend1.setMarginBottom(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getBottomMargin());
					legend1.setMarginLeft(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getLeftMargin());
					legend1.setMarginRight(20.0f+graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelMarginProperties().getRightMargin());
					//Legend Margin end

					//this allows distance between on hover value and legend value(title)
					legend1.setEqualWidths(false);
					legend1.setAlign("center");
					//------------------------------------------- Legend Panel End--------------------------------------------------
					//------------------------------------------- Legend Title Start--------------------------------------------------
					/*if(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().isTitleVisible())
					{
						String legendTitle = null;
						if(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitle().equals(""))
						{
							if(isLegendVisible)
							{
								legendTitle = graphInfo.getGraphData().getRowLabel();	
								if(rowLabel.equalsIgnoreCase("Legend"))
									legendTitle = "";
							}
							else
							{
								legendTitle = graphInfo.getGraphData().getColLabel();
							}
						}
						else
							legendTitle = graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitle();

						legend1.setTitle(legendTitle);
					}
					else
					{	*/
					legend1.setTitle("");
					/*}*/
					legend1.setFontSize(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getFontSize());
					//legend1.setFontSize(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitleFont().getFontSize());
					//------------------------------------------- Legend Title End--------------------------------------------------
					//------------------------------------------- Legend Values Start--------------------------------------------------
					int maxColumns = 100;
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
					}
					legend1.setMaxColumns(maxColumns);
					legend1.setVerticalGap(1);

					if(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option1") || 
							graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
							.getLegendValuesOrder().equalsIgnoreCase("option3")){
						legend1.setReversedOrder(false);
					} 
					else if(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option2")){
						legend1.setReversedOrder(true);
					}
					legend1.setValueText("");
					//------------------------------------------- Legend Values End--------------------------------------------------
					//------------------------------------------- Legend Icon Start--------------------------------------------------
					String legendIconShape="";

					int markerSize = graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getWidth();
					switch(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconSelectShape())
					{
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
					legend1.setMarkerSize(markerSize);
					legend1.setMarkerType(legendIconShape);

					if(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().isVisible() &&
							graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderStyle().equalsIgnoreCase("solid")){
						legend1.setMarkerBorderAlpha(1);
						legend1.setMarkerBorderThickness(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderWidth());
						legend1.setMarkerBorderColor(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderColor());
					}
					else
					{
						legend1.setMarkerBorderAlpha(0);
						legend1.setMarkerBorderColor("");
					}
					//------------------------------------------- Legend Icon End--------------------------------------------------
					 if(colListSize > 1)
					legend1.setDivId("legenddiv");
				}
			}

			((GraphLegendJson) graphJson).setLegend(legend1);
			//------------------------------------------- Legend End--------------------------------------------------
			//Responsive start
			Responsive responsive = new Responsive();
			responsive.setEnabled(true);
			responsive.setAddDefaultRules(false);
			List<LinkedHashMap<String, Object>> rulesMapList = new ArrayList<LinkedHashMap<String, Object>>();
			LinkedHashMap<String, Object> dpRulesMap =  new LinkedHashMap<String, Object>();
			/*if(!(graphInfo.getGraphProperties().getPieGraph().isClustered()))
			{
				dpRulesMap.put("maxWidth", 420);
				LinkedHashMap<String, Object> legendMap = new LinkedHashMap<String, Object>();

				dpRulesMap.put("overrides", legendMap);
				LinkedHashMap<String, Object> ruleMap = new LinkedHashMap<String, Object>();
				legendMap.put("legend", ruleMap);
				ruleMap.put("enabled", true);
				ruleMap.put("position", "bottom");
				ruleMap.put("maxColumns", "100");
				legendMap.put("legend", ruleMap);
			}*/
			rulesMapList.add(dpRulesMap);
			responsive.setRules(rulesMapList);

			graphJson.setResponsive(responsive);
			graphJson.setPieMultipleMeasureIdArr(pieMultipleMeasureIdArr);
			//Responsive end
			jsonList.add(graphJson);
		}
		if(isLegendVisible)
		{

			GraphJson graphJson1 = new GraphLegendJson();

			graphJson1.setType("pie");
			graphJson1.setObjectType("graph");
			graphJson1.setTheme("none");
			graphJson1.setAddClassNames(true);
			graphJson1.setColors(Arrays.asList(colors));
			graphJson1.setColorField("color");
			graphJson1.setAlpha(fillAlpha);
			graphJson1.setColumnSpacing(0);
			graphJson1.setMouseWheelZoomEnabled(false);
			graphJson1.setRadius(graphInfo.getGraphProperties().getPieGraph().getRadius() + "%");
			graphJson1.setChartType("doughnut");
			graphJson1.setBringToFront(true);
			graphJson1.setPieLegendDiv(true);
			/*graphJson1.setStartEffect("easeOutSine");
			graphJson1.setSequencedAnimation(false);
			
			double startDuration = 0.0;
			if(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isAnimationPlayable())
				startDuration = 0.5;
			graphJson1.setStartDuration(startDuration);//Play or Stop Animation
			
			graphJson1.setPieAnimationEnable(graphInfo.getGraphProperties().getGraphAreaProperties().getGeneralGraphArea().isAnimationPlayable());*/
			
			List<Map<String, Object>> dpList1 =  new ArrayList<Map<String,Object>>();
			for (int j = 0; j < tmpListSize; j++) {

				Map<String, Object> dpMap =  new HashMap<String, Object>();
				String legendTruncateLabels = "";
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible())
				{
					legendTruncateLabels = tmpList.get(j).toString();
					//Added code for Bug #13406 start
					if(!dateRowList.isEmpty() && dateRowList.size() > j
							&& null != dateRowList.get(j) && !dateRowList.get(j).equals(AppConstants.NULL_DISPLAY_VALUE)) {
						String stringFormat;
						stringFormat = graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getDateFormat() + " " +graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getTimeFormat();
						stringFormat = stringFormat.replaceAll("&#39;", "'");
						Calendar cal = Calendar.getInstance();
						Date axisDate = new Date();
						axisDate = (Date) dateRowList.get(j);
						cal.setTime(axisDate);
						stringFormat=stringFormat.trim();
						legendTruncateLabels = new SimpleDateFormat(stringFormat).format(cal.getTime());
					}//Added code for Bug #13406 end
					switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
					{
					case "custom":
						//legendTruncateLabels = tmpList.get(j).toString();
						int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
						if (legendTruncateLabels.length() > truncateCharLimit)
							legendTruncateLabels = legendTruncateLabels.substring(0, truncateCharLimit)+"..";
						break;
					/*default:
						legendTruncateLabels = tmpList.get(j).toString();
						break;*/
					}	
				}
				dpMap.put(zaxisValue, legendTruncateLabels);
				dpMap.put(zaxisValueMouseOver, tmpList.get(j).toString());
				
				colorWiseIndex.add(graphInfo.getColorInfoList().get(j)%colors.length);
				dpMap.put("color", colors[graphInfo.getColorInfoList().get(j)%colors.length]);
				dpMap.put("index", j);
				dpMap.put("disableToggle", true);
				if(drillList.size() != 0)
				{
					if(graphInfo.isMultipleYAxisLabelsEnable() && (graphInfo.getGraphData().getRowLabel().equals("Legend")))
					{
						dpMap.put(drillLegend, drillList.get(j+colListSize).toString());
					}
					else
					{
						dpMap.put(drillLegend, drillList.get(j).toString());
					}
					dpMap.put(drillAxis, drillList.get(j).toString());
					//pieDrillMap.put(List.get().toString()+dashbordTdId, drillList.get(j).toString());//Added for NeGD feature request of Pie drill on Dashboard (24 July 2019)
				}
				
				dpList1.add(dpMap);
			}
			graphJson1.setDataProvider(dpList1);
			graphJson1.setTitleField(zaxisValue);
			graphJson1.setValueField(zaxisValue);//Added for Bug #12496 (Legend show/hide)
			graphJson1.setShowZeroSlices(true);//Added for Bug #12612 (Legend show/hide)
			Legend legend= new Legend();

			if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isLegendPanelVisible())
			{
				legend.setShowEntries(true);
				if(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().isDrillDown())
				{
					legend.setSwitchable(false);
				}
				else
				{
					legend.setSwitchable(true);
				}
				if(isContextFilter)
				{
					legend.setSwitchable(false);
				}
				// add legend data for sorting order 18/7/22
				List<Map<String, Object>>  dataRulesMap =  new ArrayList<Map<String,Object>>();
				
				Map<String, Object> dataMapOther =  new HashMap<>();
				List<Integer> updatedColorInfoList = graphInfo.getColorInfoList();
				if(null != graphInfo.getLegendColorInfoList() && !graphInfo.getLegendColorInfoList().isEmpty()) {
					updatedColorInfoList = graphInfo.getLegendColorInfoList();
					if(graphInfo.getLegendColorInfoList().size() !=updatedColorInfoList.size()) {
				    	  updatedColorInfoList = graphInfo.getColorInfoList();
				     }
				}
				 
				 if(null!= graphInfo.getGraphProperties() && null !=graphInfo.getGraphProperties().getLegendCustomValueList() && 
							!graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()) {
					
						/*Map<String, Integer> colorMapping = new HashMap<>();
				        for (int k = 0; k < graphInfo.getLovListForColor().size(); k++) {
				        	if(graphInfo.getColorInfoList().size()>k) 
				            colorMapping.put(graphInfo.getLovListForColor().get(k), graphInfo.getColorInfoList().get(k));
				        }
	
				        // Create a new list for updated colorInfoList
				        updatedColorInfoList = new ArrayList<>();
				        for (Object category : graphInfo.getGraphProperties().getLegendCustomValueList()) {
				        	if(null!= colorMapping.get(category))     
				            updatedColorInfoList.add(colorMapping.get(category));
				        }*/
				        if(graphInfo.getColorInfoList().size() !=updatedColorInfoList.size()) {
					    	  updatedColorInfoList = graphInfo.getColorInfoList();
					     }
				 }
				 if(graphInfo.getColorInfoList().size() !=updatedColorInfoList.size()) {
			    	  updatedColorInfoList = graphInfo.getColorInfoList();
			     }
				 List legendValList = new ArrayList<>();
					legendValList.addAll(tmpList);

					if (graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
							.getLegendValuesOrder().equalsIgnoreCase("option3")
							&& graphInfo.getGraphProperties().getLegendCustomValueList() != null
							&& !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty() && graphInfo.getDrilldownBreadcrumbMap() == null) {
						legendValList.clear();
						legendValList.addAll(graphInfo.getGraphProperties().getLegendCustomValueList());
					}
				for (int i = 0; i < tmpListSize; i++) {
					Map<String, Object> dataMap =  new HashMap<String, Object>();
					//if(isLegendVisible)
					//dataMap.put("drillLegend", drillList.get(i).toString());
					String tmp = legendValList.get(i).toString();
					String tmp1 = legendValList.get(i).toString();
					if(colLabelsName && null != graphInfo.getGraphData().getColLabelsName() && !graphInfo.getGraphData().getColLabelsName().isEmpty() && graphInfo.getGraphData().getColLabelsName().size() >= rowListSize
							&& graphInfo.getGraphData().getRowLabel()!=null  && tmp!= graphInfo.getGraphData().getColLabelsName().get(i) && !rowList.get(i).equals(graphInfo.getGraphData().getColLabelsName().get(i))) {
							tmp = graphInfo.getGraphData().getColLabelsName().get(i).toString();
					}
					if(!dateRowList.isEmpty() && dateRowList.size() > i
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
					switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
					{
					case "auto":
						//tmp = rowList.get(i).toString();
						int truncateCharLimitAuto = 15;
						if (tmp.length() > truncateCharLimitAuto)
							tmp = tmp.substring(0, truncateCharLimitAuto)+"..";
						break;
					case "custom":
						//tmp = tmpList.get(i).toString();
						int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
						if (tmp.length() > truncateCharLimit)
							tmp = tmp.substring(0, truncateCharLimit)+"..";
						break;
					}
					if(!tmp.equalsIgnoreCase("Other")) {
						dataMap.put("title", tmp);
						dataMap.put("title1", tmp1);
						dataMap.put("color", colors[updatedColorInfoList.get(i)%colors.length]);
						dataMap.put("index", i);
						dataRulesMap.add(dataMap);
						colorWiseIndex.add(graphInfo.getColorInfoList().get(i)%colors.length);
					}else {
						dataMapOther.put("title", tmp);
						dataMapOther.put("title1", tmp1);
						dataMapOther.put("color", colors[updatedColorInfoList.get(i)%colors.length]);
						dataMapOther.put("index", i);
						colorWiseIndex.add(graphInfo.getColorInfoList().get(i)%colors.length);
					}
				}
				try {
					if(!dataRulesMap.isEmpty() && !graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties()
							.getLegendValuesOrder().equalsIgnoreCase("option3")){
						dataRulesMap.sort(Comparator.comparing(o -> String.valueOf(o.get("title")), String.CASE_INSENSITIVE_ORDER));
						
					}
					if(!dataMapOther.isEmpty()) {
						dataRulesMap.add(dataMapOther);
					}
				}catch(Exception e) {
					ApplicationLog.error(e);
				}					
				
				
				legend.setData(dataRulesMap);
				//------------------------------------------- Legend Panel Start--------------------------------------------------
				String position="";
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendPanelProperties().getLegendPanelPosition())
				{
				case 1: position ="top";break;
				case 2: position ="left";break;
				case 3: position ="right";break;
				case 4: position="bottom";break;
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
							if(rowLabel.equalsIgnoreCase("Legend"))
								legendTitle = "";
						}
						else
						{
							legendTitle = graphInfo.getGraphData().getColLabel();
						}
					}
					else
						legendTitle = graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitle();

					legend.setTitle(legendTitle);
				}
				else
				{	
					legend.setTitle("");
				}
				legend.setFontSize(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getFontSize());
				//legend.setFontSize(graphInfo.getGraphProperties().getLegendProperties().getTitleProperties().getTitleFont().getFontSize());
				//------------------------------------------- Legend Title End--------------------------------------------------
				//------------------------------------------- Legend Values Start--------------------------------------------------
				int maxColumns = 100;
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
				}
				legend.setMaxColumns(maxColumns);
				legend.setVerticalGap(1);

				if(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option1") ||
						graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option3")){
					legend.setReversedOrder(false);
				} else {
					legend.setReversedOrder(true);
				}
				if(null !=graphInfo.getGraphProperties().getLegendCustomValueList() && !graphInfo.getGraphProperties().getLegendCustomValueList().isEmpty()
		                && !graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesOrder().equalsIgnoreCase("option2")) {
		                	legend.setReversedOrder(false);
				}

				legend.setValueText("");
				//------------------------------------------- Legend Values End--------------------------------------------------
				//------------------------------------------- Legend Icon Start--------------------------------------------------
				String legendIconShape="";

				int markerSize = graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getWidth();
				switch(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconSelectShape())
				{
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

				if(graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().isVisible() &&
						graphInfo.getGraphProperties().getLegendProperties().getLegendIconProperties().getLegendIconBorderProperties().getAllBorderStyle().equalsIgnoreCase("solid")){
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
				/*if(colListSize > 1)
				legend.setDivId(dashbordTdId+"legenddiv");*/
			}
			else
			{
				legend.setEnabled(false);
				legend.setShowEntries(false);
			}

			((GraphLegendJson) graphJson1).setLegend(legend);
			//------------------------------------------- Legend End--------------------------------------------------
			//Responsive start
			Responsive responsive1 = new Responsive();
			responsive1.setEnabled(true);
			responsive1.setAddDefaultRules(false);

			List<LinkedHashMap<String, Object>> rulesMapList = new ArrayList<LinkedHashMap<String, Object>>();
			LinkedHashMap<String, Object> dpRulesMap =  new LinkedHashMap<String, Object>();

			dpRulesMap.put("maxWidth", 320);
			LinkedHashMap<String, Object> legendMap = new LinkedHashMap<String, Object>();

			/*dpRulesMap.put("overrides", legendMap);
			LinkedHashMap<String, Object> ruleMap = new LinkedHashMap<String, Object>();
			legendMap.put("legend", ruleMap);
			ruleMap.put("enabled", true);
			ruleMap.put("position", "bottom");
			ruleMap.put("maxColumns", "100");
			rulesMapList.add(dpRulesMap);*/
			responsive1.setRules(rulesMapList);
			graphJson1.setResponsive(responsive1);
			jsonList.add(graphJson1);

		}
		graphInfo.setTitleData(pieTitle);
		graphInfo.setPieMultipleMeasureIdArr(pieMultipleMeasureIdArr);
		try {
			json =objectMapper.writeValueAsString(jsonList);		
		} catch (IOException e) {
			ApplicationLog.error(e);
		}
		
		if(null != rowLabel && !"Legend".equals(rowLabel))//Added for NeGD feature request 15075 of Pie drill on Dashboard (24 July 2019)
		{
			graphInfo.getGraphData().setPieDrillMap(pieDrillMap);
		}
		setDisplayColorIndex(colorWiseIndex, graphInfo);
		//ApplicationLog.info(json);
		return json;
	}
	private static final String[] appendValue(String[] s1 ,String newValue) {
		String[] erg = new String[s1.length + 1];
		erg[erg.length-1] = newValue;
		System.arraycopy(s1, 0, erg, 0, s1.length);

		return erg;
	}
	public static String roundDecimalValue(double value, int decimalPlaces) {
		BigDecimal bigDecimal = BigDecimal.valueOf(value);
		BigDecimal roundedValueWithDivideLogic = bigDecimal.divide(BigDecimal.ONE,decimalPlaces,BigDecimal.ROUND_HALF_UP);

		return roundedValueWithDivideLogic.toPlainString();
	}
	public static String parseformat(String pattern, Object value , int decimalPlaces) {
		//DecimalFormat df = new DecimalFormat(pattern);
		DecimalFormat df = new DecimalFormat(pattern,DecimalFormatSymbols.getInstance(Locale.ENGLISH));
		df.setMinimumFractionDigits(decimalPlaces);
	    return df.format(value);
	}
	public static String commaFormats(double in_objValue, TrendDataValueProperties trendDataValueProperties,GraphInfo graphInfo){

		double dValue = in_objValue;
		String strData = "";
		/*ApplicationLog.info(in_objValue+"colProp..."+colProp);
		ApplicationLog.info("colProp.getValueFormat()..."+colProp.getValueFormat());
		ApplicationLog.info("colProp.getValueFormat().getNumberFormat()..."+colProp.getValueFormat().getNumberFormat());
		ApplicationLog.info("colProp.getValueFormat().getNumberFormat().getAdjustedDigit()..."+colProp.getValueFormat().getNumberFormat().getAdjustedDigit());*/
		//int place = yaxisTrendProperties.getLabelProperties().getAdjustedDigit();//in_alsItemInfo.getColumnProperties()
		/*if (place > 0) {
			dValue /= Math.pow(10, place);
		}*/

		Object commaSepObj = trendDataValueProperties.getNumberFormat().isCommaSeprator();
		boolean commaSep = false;
		if (commaSepObj != null) {
			commaSep = ((Boolean) commaSepObj).booleanValue();
		}

		int commaPosStyleObj =  trendDataValueProperties.getNumberFormat().getCommaFormat();
		//commaPosStyleObj = false;
		boolean commaPosStyle = false;
		if (commaPosStyleObj == 2) {
			
			//commaPosStyle = ((Boolean) commaPosStyleObj).booleanValue();
			commaPosStyle = true;
		}
		
		if (commaSep && commaPosStyle
				&& (dValue <= -100000 || dValue >= 100000)) {				
			trendDataValueProperties.getNumberFormat().setCommaSeprator(false);
			String strFmt = GetDecimalFormatString(trendDataValueProperties,0);
			trendDataValueProperties.getNumberFormat().setCommaSeprator(true);				
			java.text.DecimalFormat DecFormat = new java.text.DecimalFormat(
					strFmt);
			strData = DecFormat.format(dValue);
			strData = StringUtil.modifyDataForIndianStyleCommaPos(strData,
					null, false);
		} else {
			String strFmt = GetDecimalFormatString(trendDataValueProperties,0);
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
	
	 public static String GetDecimalFormatString(TrendDataValueProperties colProp,int operationType)
	  {
	    String strFormat = "";
	    Object key_coma =  null;
	    if(colProp != null )
	    	key_coma =  colProp.getNumberFormat().isCommaSeprator();
	   
	    if(key_coma != null)
	    {
	      boolean bValue = ((Boolean)key_coma).booleanValue();
	      if(bValue)
	      {
	        strFormat += "###,##0";
	      }
	      else
	      {
	        strFormat += "#####0";
	      }
	    }
	    Object obj = null;
	    if(colProp != null)
	    	obj = colProp.getNumberFormat().getNumberOfDigits();
	   
	    if(operationType == ICubeResultSetSupport.totalTypeCount || operationType == ICubeResultSetSupport.totalTypeCount2
	    		||operationType == ICubeResultSetSupport.totalTypeCount4) {
	    	obj = 0;
	    }
	    int scale = 0;
	    if(obj != null)
	    {
	      scale = ((Integer)obj).intValue();
	    }
	    for(int i = 0; i < scale; i++ )
	    {
	      if(i == 0)
	      {
	        strFormat += '.';
	      }
	      strFormat += '0';
	    }
	    strFormat += ';';
	    int iChoice = 0;
	    if(colProp != null)
	    	iChoice = colProp.getNumberFormat().getNegativeNumberFormat();
	    
		  switch(iChoice)
		  {
		    case 0:
		      strFormat += '-';
		      break;
		    case 1:
		      strFormat += " ";
		      break;
		    case 2:
		      strFormat += '(';
		      break;
		    case 3:
		      strFormat += ResourceManager.getString("DOLLAR");
		      break;
		    case 4:
		      strFormat += ResourceManager.getString("EURO");
		      break;
		  }
	    
	    obj = key_coma;
	    if(obj != null)
	    {
	      boolean bValue = ((Boolean)obj).booleanValue();
	      if(bValue)
	      {
	        strFormat += "###,##0";
	      }
	      else
	      {
	        strFormat += "#####0";
	      }
	    }
	    for(int i = 0; i < scale; i++ )
	    {
	      if(i == 0)
	      {
	        strFormat += '.';
	      }
	      strFormat += '0';
	    }
	    
	      switch(iChoice)
	      {
	        case 2:
	          strFormat += ')';
	          break;
	      }
	    
	    return strFormat;
	  }
	 
	 private static void setDisplayColorIndex(List colorWiseIndex, GraphInfo graphInfo) {
			if(colorWiseIndex != null && !colorWiseIndex.isEmpty()) {
				HashSet<String> uniqueElements = new HashSet<>(colorWiseIndex);
		        colorWiseIndex = new ArrayList<>(uniqueElements);
			}
			graphInfo.setDisplayBarIndexList(colorWiseIndex);
	 }
}
