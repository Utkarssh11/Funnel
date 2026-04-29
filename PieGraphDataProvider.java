package com.elegantjbi.amcharts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import com.elegantjbi.entity.graph.GraphInfo;

public class PieGraphDataProvider {


	public static List<Map<String, Object>> dataProviderJson(GraphInfo graphInfo,int startIndex,int Quantity)
	{
		ObjectMapper objectMapper = new ObjectMapper();
		
		String rowLabel = graphInfo.getGraphData().getRowLabel();
		List rowList = graphInfo.getGraphData().getRowList();
		int rowListSize = rowList.size();
		String colLabel ="";
		List colList = graphInfo.getGraphData().getColList();
		int colListSize ;
		/*String[] colors = new String[]{"rgb(141,170,203)","rgb(252,115,98)","rgb(187,216,84)","rgb(255,217,47)","rgb(102,194,150)",
				"rgb(255, 148, 10)","rgb(148, 247, 244)"};*/
		String[] colors =new String[]{"#67b7dc","#6794dc","#6771dc","#8067dc","#a367dc","#c767dc","#dc67ce","#dc67ab","#dc6788","#dc6967",
			    "#dc8c67","#dcaf67","#dcd267","#c3dc67","#a0dc67","#7ddc67","#67dc75","#67dc98","#67dcbb","#67dadc",
			    "#80d0f5","#80adf5","#808af5","#9980f5","#bc80f5","#e080f5","#f580e7", "#f7d584", "#b1fb83", "#50407f", 
			    "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c", "#8daacb", "#fc7362", "#bbd854", "#ffd92f", "#66c296",
			    "#e5b694", "#e78ad2", "#b3b3b3", "#a6d8e3", "#abe9bc", "#1b7d9c", "#ffbfc9", "#4da741", "#c4b2d6", "#b22424",
			    "#00acac", "#be6c2c", "#695496", "#349152", "#c9a16c", "#2d6396", "#fb2600", "#1596ff", "#fc9400", "#36fa92",
			    "#ec8b8b", "#93c2ff", "#f7d584", "#b1fb83", "#50407f", "#64c7cd", "#02adf2", "#828813", "#3ab54a", "#ed008c"};
		boolean isLegendVisible = true;
		List tmpList;
		int tmpListSize = 0;
		if(rowListSize == 0){
			rowListSize = 1;
			isLegendVisible =  false;
			colLabel = graphInfo.getGraphData().getColLabel();
			tmpList = colList;
			colListSize = 1;
		}
		else
		{
			colLabel = graphInfo.getGraphData().getRowLabel();
			tmpList = rowList;
			colListSize =  colList.size();
		}
		tmpListSize = tmpList.size();
		
		String dataLabel = graphInfo.getGraphData().getDataLabel();
		
		String drillAxis = "drillAxis";
		String drillLegend = "drillLegend";
		List drillList = graphInfo.getGraphData().getDrillLinkList();
		int nullSize = colListSize*rowListSize;
		
		if(drillList.isEmpty() || nullSize > graphInfo.getGraphData().getDrillLinkList().size())
		{
			for(int i=0;i<nullSize;i++)
			{
				drillList.add("null");
			}
		}
		
		boolean flag=false;
		double customMax = 0.0;
		if(graphInfo.getGraphProperties().getyAxisProperties().getLabelProperties().getMaxValType() == 1)
		{
			flag=true;
			customMax =Double.parseDouble(graphInfo.getGraphProperties().getyAxisProperties().getLabelProperties().getMaxCustomVal());
		}
		
		List dataList = graphInfo.getGraphData().getDataList();

		String[] barColor =new String[]{"rgb(141,170,203)","rgb(252,115,98)","rgb(187,216,84)","rgb(255,217,47)","rgb(102,194,150)","rgb(255, 148, 10)","rgb(148, 247, 244)"};
		switch(graphInfo.getGraphProperties().getColorType())
		{
		case 1:
			if(graphInfo.getGraphProperties().getCustomColors() != null)
			{
				for (int i = 0; i < graphInfo.getGraphProperties().getCustomColors().size(); i++) {
					if(i > (barColor.length-1))
					{
						barColor = appendValue(barColor, graphInfo.getGraphProperties().getCustomColors().get(i));
					}
					else
					{	
					barColor[i] = graphInfo.getGraphProperties().getCustomColors().get(i);
					}
				}
			}
			break;
		case 2:
			barColor = new String[]{graphInfo.getGraphProperties().getColor()};
			break;
		}
		
		
		
		
		int paginationIndex=startIndex+Quantity;
		int k=0;
		int drillIndex=0;
		List<Map<String, Object>> dpList =  new ArrayList<Map<String,Object>>();

		for (int i = 0; i < colListSize; i++) {
			Map<String, String> drillMap =null;
			if(k==startIndex)
			{
				drillIndex = i;
				if(isLegendVisible)
				{
					drillIndex=rowListSize+i;
					drillMap = new HashMap<String, String>();
				}
			}
			for (int j = 0; j < tmpListSize; j++) {
				Map<String, Object> dpMap =  new HashMap<String, Object>();
				if(dataList.get(i*tmpListSize+j)!=null){	
					if(k==startIndex)
					{	
						if(paginationIndex==startIndex)
						{

							int dataIndex = 0;
							if(isLegendVisible)
							{
								dataIndex = tmpListSize*i + j;
							}
							else
							{
								dataIndex = j;
							}

							String legendTruncateLabels = "";
							switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
							{
							case "custom":
								legendTruncateLabels = tmpList.get(j).toString();
								int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
								if (legendTruncateLabels.length() > truncateCharLimit)
									legendTruncateLabels = legendTruncateLabels.substring(0, truncateCharLimit)+"..";
								break;
							default:
								legendTruncateLabels = tmpList.get(j).toString();
								break;
							}
							
							dpMap.put(colLabel, legendTruncateLabels);
							dpMap.put("color", colors[j%colors.length]);
							if(drillList.size() > j && (!drillList.isEmpty() && drillList.get(j)!=null) && !drillList.get(j).equals("null")) {
								dpMap.put(drillLegend, drillList.get(j).toString());
							}
							dpMap.put(dataLabel, dataList.get(dataIndex).toString());
							if(drillList.size() > drillIndex && (!drillList.isEmpty() && drillList.get(drillIndex)!=null) && !drillList.get(drillIndex).equals("null")) {
								dpMap.put(drillAxis, drillList.get(drillIndex).toString());
							}
							dpList.add(dpMap);
							return dpList;

						}

						int dataIndex = 0;
						if(isLegendVisible)
						{
							dataIndex = tmpListSize*i + j;
						}
						else
						{
							dataIndex = j;
						}

						String legendTruncateLabels = "";
						switch(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCharacterLimit())
						{
						case "custom":
							legendTruncateLabels = tmpList.get(j).toString();
							int truncateCharLimit = Integer.parseInt(graphInfo.getGraphProperties().getLegendProperties().getLegendValuesProperties().getLegendValuesFontProperties().getCustomCharacterLimit());
							if (legendTruncateLabels.length() > truncateCharLimit)
								legendTruncateLabels = legendTruncateLabels.substring(0, truncateCharLimit)+"..";
							break;
						default:
							legendTruncateLabels = tmpList.get(j).toString();
							break;
						}
						
						dpMap.put(colLabel, legendTruncateLabels);
						dpMap.put("color", colors[j%colors.length]);
						if(drillList.size() > j && (!drillList.isEmpty() && drillList.get(j)!=null) && !drillList.get(j).equals("null")) {
							dpMap.put(drillLegend, drillList.get(j).toString());
						}
						dpMap.put(dataLabel, dataList.get(dataIndex).toString());
						if(drillList.size() > drillIndex && (!drillList.isEmpty() && drillList.get(drillIndex)!=null) && !drillList.get(drillIndex).equals("null")) {
							dpMap.put(drillAxis, drillList.get(drillIndex).toString());
						}

						//Changes bar values to customMaxValue if flag is true

						startIndex++;
					}
				}else{					
					continue;
				}

				k++;
				dpList.add(dpMap);
			}
		}
		
		
		return dpList;
		
	}
	private static final String[] appendValue(String[] s1 ,String newValue) {

		  String[] erg = new String[s1.length + 1];
		  erg[erg.length-1] = newValue;
	      System.arraycopy(s1, 0, erg, 0, s1.length);

	      return erg;

	  }

}
