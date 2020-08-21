
package com.asiainfo.veris.crm.order.web.group.saleactive;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;

public abstract class QrySaleGrpBizAuditInfo extends GroupBasePage
{
    public abstract void setCondition(IData condition);
    public abstract void setInfo(IData info);
    public abstract void setInfos(IDataset infos);
    public abstract void setCityList(IDataset cityList);
    public abstract void setLogCount(long logCount);

    /**
     * 页面初始化
     * @param cycle
     * @throws Exception
     * @author 
     * @date 2018-7-9
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData pgData = this.getData();
        String staffId = getVisit().getStaffId();
        boolean enableFlag = false;
        // 查询区域信息
        IData svcData = new DataMap();
        if (staffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
            enableFlag = true;
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
            enableFlag = false;
        }

        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        pgData.put("CITY_CODE", getVisit().getCityCode());
        pgData.put("ENABLE_FLAG", enableFlag);

        // 设置返回值
        setCityList(cityList);
        setCondition(pgData);
    }
    
    /**
     * 查询集团业务稽核单信息
     * @param cycle
     * @throws Exception
     * @author 
     * @date 2018-7-9
     */
    public void queryGrpAuditInfos(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String inStaffId = data.getString("CITY_CODE","");
        if(StringUtils.isNotBlank(inStaffId))
        {
        	data.put("IN_STAFF_ID", inStaffId);
        }
        IDataOutput output = CSViewCall.callPage(this, "SS.QueryGrpBizAuditInfoSVC.queryGrpSaleAuditInfos", data, getPagination("LogNav"));        
        IDataset infos = output.getData();
        if(IDataUtil.isNotEmpty(infos)){
        	for(int i=0;i<infos.size();i++){
        		IData each = infos.getData(i);
        		each.put("ADD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("ADD_DISCNTS", "")));
        		each.put("DEL_DISCNTS_DESC", this.dealDisnctDesc(each.getString("DEL_DISCNTS", "")));
        		each.put("MOD_DISCNTS_DESC", this.dealDisnctDesc(each.getString("MOD_DISCNTS", "")));
        		each.put("STATE_DESC", StaticUtil.getStaticValue("GROUP_BIZ_ORDERSTATE", each.getString("STATE", "")));
        		each.put("TRADE_TYPE_DESC", StaticUtil.getStaticValue(getVisit(),"TD_S_TRADETYPE","TRADE_TYPE_CODE","TRADE_TYPE",each.getString("TRADE_TYPE_CODE", "")));
        		each.put("AUDIT_DISABLED", this.getAuditDisabled(each.getString("STATE")));
        	}
        }
        setInfos(infos);
        setLogCount(output.getDataCount());
        
        String loginStaffId = getVisit().getStaffId(); // 系统登录工号

        // 查询区域信息
        IData svcData = new DataMap();

        if (loginStaffId.substring(0, 4).matches("HNSJ|HNYD|SUPE"))
        {
            svcData.put("AREA_FRAME", getTradeEparchyCode());
        }
        else
        {
            svcData.put("AREA_FRAME", getVisit().getCityCode());
        }
        IDataset cityList = CSViewCall.call(this, "CS.AreaInfoQrySVC.qryAeraByAreaFrame", svcData);

        data.put("CITY_CODE", data.getString("CITY_CODE"));
        data.put("ENABLE_FLAG", data.getString("ENABLE_FLAG"));
        
        setCityList(cityList);
        setCondition(data);
        
        //设置返回数据
        setAjax(data);
    }
    
    /**
     * 取优惠名称信息
     * @param discntCodes
     * @return
     * @throws Exception
     * @author chenzg
     * @date 2018-8-20
     */
    private String dealDisnctDesc(String discntCodes) throws Exception{
    	String desc = "";
    	if(StringUtils.isNotBlank(discntCodes)){
    		String[] disnctArr = discntCodes.split(",");
    		for(String discntCode : disnctArr){
    			String discntName = UpcViewCall.queryOfferNameByOfferId(this, "D", discntCode);
    			desc += StringUtils.isNotBlank(desc) ? ","+discntCode+"["+discntName+"]" : discntCode+"["+discntName+"]";
    		}
    	}
    	return desc;
    }
    /**
     * 判断是否可以审核
     * @param state
     * @return
     * @author chenzg
     * @date 2018-8-29
     */
    private String getAuditDisabled(String state) {
    	String ret = "true";
    	//待审核、已整改、已二次整改的单可以稽核
    	if("0".equals(state) || "3".equals(state) || "4".equals(state)){
    		ret = "false";
    	}
		return ret;
	}
    
}
