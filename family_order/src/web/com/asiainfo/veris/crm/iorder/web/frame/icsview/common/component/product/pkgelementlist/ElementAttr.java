
package com.asiainfo.veris.crm.iorder.web.frame.icsview.common.component.product.pkgelementlist;

import org.apache.tapestry.IMarkupWriter;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizTempComponent;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public abstract class ElementAttr extends CSBizTempComponent 
{
    @Override
    protected void cleanupAfterRender(IRequestCycle cycle)
    {
        // TODO Auto-generated method stub
        super.cleanupAfterRender(cycle);
        this.setAttr(null);
        this.setAttrs(null);
        this.setElementId(null);
        this.setItemIndex(null);
        this.setDisplayCondition(null);
        this.setConfirmHandler(null);
        this.setElementTypeCode(null);
		this.setElementName(null);
		this.setElementStartDate(null);
		this.setElementEndDate(null);
		this.setSDateSource(null);
		this.setEDateSource(null);
		this.setSDateType(null);
		this.setEDateType(null);
		this.setEDisabled(null);
		this.setSDisabled(null);
    }

    public abstract String getConfirmHandler();

    @Override
    public void renderComponent(StringBuilder builder, IMarkupWriter writer, IRequestCycle cycle) throws Exception
    {
        boolean isAjax = isAjaxServiceReuqest(cycle);
        if (isAjax)
        {
            includeScript(writer, "scripts/iorder/icsserv/component/product/pkgelementlist/elementattr.js");
        }
        else
        {
            this.getPage().addResBeforeBodyEnd("scripts/iorder/icsserv/component/product/pkgelementlist/elementattr.js");
        }
        IData param = this.getPage().getData();
        if (param.getString("ELEMENT_ID", "").equals("") || param.getString("ELEMENT_TYPE_CODE", "").equals(""))
        {
            return;
        }
        
        if(param.containsKey("ELEMENT_START_DATE") && param.containsKey("ELEMENT_END_DATE")){
			String elementStartDate = param.getString("ELEMENT_START_DATE");
			String elementEndDate = param.getString("ELEMENT_END_DATE");
			String sDateType = param.getString("sDateType");
			String eDateType = param.getString("eDateType");
			this.setSDateType(sDateType);
			this.setEDateType(eDateType);
			if(StringUtils.equals(sDateType, "select")){
				IDataset sDateSource = new DatasetList();
				String[] elementStartDateArr  = elementStartDate.split(",");
				for(int i=0;i<elementStartDateArr.length;i++){
					String startDate = elementStartDateArr[i].substring(0,10);
					IData sDateMap = new DataMap();
					sDateMap.put("DATA_ID", startDate);
					sDateMap.put("DATA_NAME", startDate);
					sDateSource.add(sDateMap);
				}
				this.setSDateSource(sDateSource);
			}else{
				this.setElementStartDate(elementStartDate.substring(0,10));
			}
			
			if(StringUtils.equals(eDateType, "select")){
				IDataset eDateSource = new DatasetList();
				String[] elementEndDateArr  = elementEndDate.split(",");
				for(int i=0;i<elementEndDateArr.length;i++){
					String endDate = elementEndDateArr[i].substring(0,10);
					IData eDateMap = new DataMap();
					eDateMap.put("DATA_ID", endDate);
					eDateMap.put("DATA_NAME", endDate);
					eDateSource.add(eDateMap);
				}
				this.setEDateSource(eDateSource);
			}else{
				this.setElementEndDate(elementEndDate.substring(0,10));
			}
		}
        this.setSDisabled(param.getString("sDisabled"));
		this.setEDisabled(param.getString("eDisabled"));
		this.setElementTypeCode(param.getString("ELEMENT_TYPE_CODE"));
		this.setElementName(param.getString("ELEMENT_NAME"));
        
        this.setElementId(param.getString("ELEMENT_ID"));
        this.setItemIndex(param.getString("ITEM_INDEX"));
        this.setDisplayCondition(param.getString("DISPLAY_CONDITION"));
        IDataset attrs = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttrs", param);
        if (attrs != null && attrs.size() > 0)
        {
            IDataset attrsResult = new DatasetList();
            IData tempAttr = null;
            int size = attrs.size();
            String attrCode = "";
            for (int i = 0; i < size; i++)
            {
                IData attr = attrs.getData(i);
                if (!attrCode.equals(attr.getString("ATTR_CODE")))
                {
                    // 如果是视频会议，下拉列表数据从OTHER表中获取
                    if ("CONF_ID".equals(attr.getString("ATTR_CODE")) && "98003201".equals(attr.getString("ID")))
                    {
                        if (!"".equals(param.getString("USER_ID", "")))
                        {
                            IData queryParam = new DataMap();
                            queryParam.put("USER_ID", param.getString("USER_ID"));
                            queryParam.put(Route.ROUTE_EPARCHY_CODE, param.getString("EPARCHY_CODE"));
                            IDataset meetings = CSViewCall.call(this, "CS.PlatComponentSVC.getBookingViedoMeeting", queryParam);
                            tempAttr = attr;
                            tempAttr.put("PARAMS", new DatasetList());

                            for (int mcount = 0; mcount < meetings.size(); mcount++)
                            {
                                IData attrParam = new DataMap();
                                attrParam.put("ATTR_FIELD_NAME", meetings.getData(mcount).getString("RSRV_STR10"));
                                attrParam.put("ATTR_FIELD_CODE", meetings.getData(mcount).getString("RSRV_STR10"));
                                tempAttr.getDataset("PARAMS").add(attrParam);
                            }
                        }

                        continue;
                    }

                    if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9") || attr.getString("ATTR_TYPE_CODE").equals("5"))
                    {
                        if (tempAttr != null)
                        {
                            attrsResult.add(tempAttr);
                        }
                        tempAttr = attr;
                        tempAttr.put("PARAMS", new DatasetList());
                        IData attrParam = new DataMap();
                        attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
                        attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
                        if (attr.getString("ATTR_TYPE_CODE").equals("9"))
                        {
                            String attrProductId = attr.getString("ATTR_CODE","");
                            if(StringUtils.isEmpty(attrProductId))
                                attrProductId = attr.getString("RSRV_STR5");
                            attrParam.put("PRODUCT_ID", attrProductId);
                            attrParam.put("WIDTH", attr.getString("RSRV_STR3"));
                            attrParam.put("HEIGHT", attr.getString("RSRV_STR4"));
                            attrParam.put("TITLE", attr.getString("TITLE"));
                        }
                        if (!(StringUtils.isBlank(attrParam.getString("ATTR_FIELD_NAME")) && StringUtils.isBlank(attrParam.getString("ATTR_FIELD_CODE"))))
                        {
                            tempAttr.getDataset("PARAMS").add(attrParam);
                        }
                        attrCode = attr.getString("ATTR_CODE");
                        if (i == size - 1)
                        {
                            attrsResult.add(tempAttr);
                        }
                    }
                    else if (attr.getString("ATTR_TYPE_CODE").equals("8"))
                    {// 自定义弹出框提交的验证JS方法
                    	if (tempAttr != null)
                        {
                            attrsResult.add(tempAttr);
                            tempAttr = null;
                        }
                        String attrScript = attr.getString("SELFFUNC", "");
                        if (StringUtils.isNotBlank(attrScript))
                        {
                            setConfirmHandler(attrScript);
                        }
                    }
                    else
                    {
                        if (tempAttr != null)
                        {
                            attrsResult.add(tempAttr);
                            tempAttr = null;
                        }
                        //*******************处理集团自由充（全量统付）的属性，获取流量值 start**************/
                        if("0".equals(attr.getString("ATTR_TYPE_CODE"))&&"7362".equals(attr.getString("ATTR_CODE"))){//7362为流量总额的标识
                        	param.put("PARAM_CODE", param.getString("ELEMENT_ID"));
                        	param.put("PARAM_ATTR", 886);
                        	param.put("SUBSYS_CODE", "CSM");
                        	IDataset flowValues = CSViewCall.call(this, "CS.SelectedElementSVC.getElementAttr4ProFlow", param);
                        	attr.put("ATTR_SINGLE_FLOW", String.valueOf(flowValues.first().get("PARA_CODE1")));
                        }
                      //*******************处理集团自由充（全量统付）的属性，获取流量值 end**************/
                        attrsResult.add(attr);
                    }
                }
                else
                {
                    if (attr.getString("ATTR_TYPE_CODE").equals("1") || attr.getString("ATTR_TYPE_CODE").equals("3") || attr.getString("ATTR_TYPE_CODE").equals("9") || attr.getString("ATTR_TYPE_CODE").equals("5"))
                    {
                        IData attrParam = new DataMap();
                        attrParam.put("ATTR_FIELD_NAME", attr.getString("ATTR_FIELD_NAME"));
                        attrParam.put("ATTR_FIELD_CODE", attr.getString("ATTR_FIELD_CODE"));
                        if (attr.getString("ATTR_TYPE_CODE").equals("9"))
                        {
                            String attrProductId = attr.getString("ATTR_CODE","");
                            if(StringUtils.isEmpty(attrProductId))
                                attrProductId = attr.getString("RSRV_STR5");
                            attrParam.put("PRODUCT_ID", attrProductId);
                            attrParam.put("WIDTH", attr.getString("RSRV_STR3"));
                            attrParam.put("HEIGHT", attr.getString("RSRV_STR4"));
                            attrParam.put("TITLE", attr.getString("TITLE"));
                        }
                        if (!(StringUtils.isBlank(attrParam.getString("ATTR_FIELD_NAME")) && StringUtils.isBlank(attrParam.getString("ATTR_FIELD_CODE"))))
                        {
                            tempAttr.getDataset("PARAMS").add(attrParam);
                        }
                        if (i == size - 1)
                        {
                            attrsResult.add(tempAttr);
                        }
                    }
                }
                attrCode = attr.getString("ATTR_CODE");
            }
            int attrSize = attrsResult.size();
            boolean isNeedPopupSelfWindow = false;
            IData needPopupAttr = null;
            for (int i = 0; i < attrSize; i++)
            {
                IData attr = attrsResult.getData(i);
                if ("9".equals(attr.getString("ATTR_TYPE_CODE")))
                {
                    isNeedPopupSelfWindow = true;
                    needPopupAttr = attr;
                    break;
                }
            }
            if (isNeedPopupSelfWindow)
            {
                this.getPage().setAjax(needPopupAttr.getDataset("PARAMS"));
                this.setRenderContent(false);
                return;
            }
            this.setAttrs(attrsResult);
            if (StringUtils.isBlank(getConfirmHandler()))
            {
                setConfirmHandler("selectedElements.confirmAttr(this.getAttribute('itemIndex'));");
            }
        }
    }

    public abstract void setAttr(IData attr);

    public abstract void setAttrs(IDataset attrs);

    public abstract void setConfirmHandler(String confirmHandler);

    public abstract void setDisplayCondition(String displayCondition);

    public abstract void setElementId(String elementId);

    public abstract void setItemIndex(String itemIndex);
    
    public abstract void setElementTypeCode(String elementTypeCode);
	
	public abstract void setElementName(String elementName);
	
	public abstract void setElementStartDate(String elementStartDate);
	
	public abstract void setElementEndDate(String elementEndDate);
	
	public abstract void setSDateSource(IDataset sDateSource);
	
	public abstract void setEDateSource(IDataset eDateSource);
	
	public abstract void setSDateType(String sDateType);
	
	public abstract void setEDateType(String eDateType);
	
	public abstract void setEDisabled(String eDisabled);
	
	public abstract void setSDisabled(String sDisabled);


}
