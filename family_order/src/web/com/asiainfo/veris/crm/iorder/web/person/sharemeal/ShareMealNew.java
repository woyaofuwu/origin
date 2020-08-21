
package com.asiainfo.veris.crm.iorder.web.person.sharemeal;



import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class ShareMealNew extends PersonBasePage
{
	private final static Logger logger = Logger.getLogger(ShareMealNew.class);
    /**
     * 添加成员时校验成员号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void checkAddMeb(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        IDataset ds = new DatasetList();
        //logger.info("前="+pageData);
        String numbList = pageData.getString("SERIAL_NUMBER_B_LIST", "");
        String mianNum = pageData.getString("SERIAL_NUMBER", "");
        String[] serialNumberBList = numbList.split(",");
        
        IData paramData = new DataMap();
        paramData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IData returnData = new DataMap();
        StringBuilder errorList = new StringBuilder();
        IDataset successList = new DatasetList();
        IData success = null;
        StringBuilder errorNumList = new StringBuilder();
        for (int i = 0; i < serialNumberBList.length; i++) {
        	success = new DataMap();
			String serialNumberB = serialNumberBList[i];
			paramData.put("SERIAL_NUMBER_B", serialNumberB);
			paramData.put("SERIAL_NUMBER", mianNum);
			ds = CSViewCall.call(this, "SS.ShareMealSVC.checkAddMeb", paramData);
			if("-1".equals(ds.getData(0).getString("WARM_TIPS"))){
				String errorInfo = ds.getData(0).getString("WARM_INFO");
				errorList.append(serialNumberB);
				errorList.append(":");
				errorList.append(errorInfo);
				errorList.append("; ");
				
				errorNumList.append(serialNumberB);
				errorNumList.append(" ");
			}else{
				success.put("SERIAL_NUMBER_B", serialNumberB);
				success.put("START_DATE", ds.getData(0).getString("START_DATE"));
				success.put("END_DATE", ds.getData(0).getString("END_DATE"));
				successList.add(success);
			}
		}
        
        returnData.put("errorList", errorList.toString());
        returnData.put("errorNumList", errorNumList.toString());
        returnData.put("successList", successList);
        setAjax(returnData);
    }
    
    public void loadInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        CSViewCall.call(this, "SS.ShareMealSVC.check", data);
        
        IDataset AddMebMaxlNumList = CSViewCall.call(this, "SS.ShareMealSVC.checkAddMebMaxNum", data);
        IData otherinfo = new DataMap();
        otherinfo.put("AddMebMaxNum", 4);//在查不出数据的情况下，默认4个。
        logger.error("ShareMealNewxxxxxxxxxxxxxxxxxx82 "+AddMebMaxlNumList);

        if(IDataUtil.isNotEmpty(AddMebMaxlNumList)){
        	otherinfo.put("AddMebMaxNum", AddMebMaxlNumList.getData(0).getString("AddMebMaxNum"));
        }
        logger.error("ShareMealNewxxxxxxxxxxxxxxxxxx85 "+otherinfo);
        setViceInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyMebList", data));
        setDiscntInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyDiscntList", data));
        setOtherInfo(otherinfo);
    }
    
    public void loadInfoForcancel(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        data.put("MEMBER_CANCEL", "1");// 用户副卡取消用
        CSViewCall.call(this, "SS.ShareMealSVC.check", data);
        setViceInfos(CSViewCall.call(this, "SS.ShareMealSVC.queryFamilyMeb", data));
    }
    
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData pageData = getData();
        pageData.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
        IDataset rtDataset = CSViewCall.call(this, "SS.ShareMealRegSVC.tradeReg", pageData);
        this.setAjax(rtDataset);
    }
     
    public abstract void setDiscntInfo(IData discntInfo);
    
    public abstract void setDiscntInfos(IDataset discntInfos);

    public abstract void setViceInfo(IData viceInfo);

    public abstract void setViceInfos(IDataset viceInfos);
    
    public abstract void setOtherInfo(IData otherInfo);
    
}
