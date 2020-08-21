package com.asiainfo.veris.crm.order.web.person.sundryquery.RecyclePhoneExtend;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class RecyclePhoneExtend extends PersonBasePage{

	public abstract void setTipInfo(String tipInfo);
	
	public abstract void setCond(IData cond);

    public abstract void setInfos(IDataset infos);
    
    public abstract void setInfo(IData result);

    public abstract void setSubmitData(IData result);

    public abstract void setPageCount(long count);
    
    public abstract void setDepartKinds(IDataset departKinds);

    
    public void onInit(IRequestCycle cycle) throws Exception
    {
    	IData cond=new DataMap();
    	cond.put("cond_IS_EXTEND_TIME", "0");
    	cond.put("cond_FORE_STAFF_DEPART_ID", getVisit().getDepartId());
    	cond.put("cond_FORE_STAFF_CITY_CODE", getVisit().getCityCode());
    	this.qryDepartKinds(cycle);
    	setCond(cond);
    }
    
    
    public void qryUserBackPhones(IRequestCycle cycle) throws Exception
    {
        IData data = this.getData("cond", true);
                
        IData checkParam=new DataMap();
        checkParam.put("START_AGENT_NO", data.getString("START_AGENT_NO",""));
        checkParam.put("END_AGENT_NO", data.getString("END_AGENT_NO",""));
        checkParam.put("STAFF_DEPART_ID", getVisit().getDepartId());
        checkParam.put("STAFF_CITY_CODE", getVisit().getCityCode());
        checkParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput checkResult = CSViewCall.callPage(this, "CS.UserInfoQrySVC.checkUserRightStartEndDepart", checkParam, this.getPagination("navt"));
        IDataset checkDatas=checkResult.getData();
        IData checkResultData=checkDatas.getData(0);
        
        String checkResultCode=checkResultData.getString("QUERY_RESULT_CODE","");
        
        if(checkResultCode.equals("0")){
        	IData param=new DataMap();
        	String isExtendTime = data.getString("IS_EXTEND_TIME","");//0否，1是
            param.put("IS_EXTEND_TIME", isExtendTime);
            param.put("START_AGENT_NO", data.getString("START_AGENT_NO",""));
            param.put("END_AGENT_NO", data.getString("END_AGENT_NO",""));
            param.put("DEPART_KIND_CODE", data.getString("DEPART_KIND_CODE",""));            
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
            param.put("START_DATE", data.getString("START_DATE"));
            param.put("END_DATE", data.getString("END_DATE"));
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            
        	IDataOutput result = CSViewCall.callPage(this, "SS.UserInfoQrySVC.queryBackUser", param, this.getPagination("navt"));
        	
        	this.setPageCount(result.getDataCount());
            this.setInfos(result.getData());
        }else{
        	this.setPageCount(0);
            this.setInfos(new DatasetList());
        }
        
        IData backResult=new DataMap();
        backResult.put("QUERY_RESULT_CODE", checkResultCode);
        backResult.put("QUERY_RESULT_RESULT", checkResultData.getString("QUERY_RESULT_RESULT",""));
        setAjax(backResult);
        
    }
    /**
     * 延期号码
     * @param clcle
     * @throws Exception
     */
    public void extendPhones(IRequestCycle clcle) throws Exception{
    	IData data = getData();
    	data.put(Route.ROUTE_EPARCHY_CODE, getVisit().getStaffEparchyCode());
    	
    	String staffId=getVisit().getStaffId();
    	String departId=getVisit().getDepartId();
    	
    	String extendTime=data.getString("EXTEND_TIME","");
    	String userIds=data.getString("USER_IDS","");
    	if(!userIds.equals("")){
    		String[] userIdsArr=userIds.split(";");  		
    		if(userIdsArr!=null&&userIdsArr.length>0){
    			IDataset params=new DatasetList();
    			for(int i=0,size=userIdsArr.length;i<size;i++){
    				if(userIdsArr[i]!=null&&!userIdsArr[i].trim().equals("")&&!userIdsArr[i].trim().equals(",")){
    					String[] userIdAndOpendDate=userIdsArr[i].split(",");
    					if(userIdAndOpendDate!=null&&userIdAndOpendDate.length==3){
    						IData param=new DataMap();
        					param.put("EXTEND_MONTH", extendTime);
        					param.put("USER_ID", userIdAndOpendDate[0]);
        					param.put("OPEN_DATE", userIdAndOpendDate[1]);
        					param.put("SERIAL_NUMBER", userIdAndOpendDate[2]);
        					
        					param.put("TRADE_STAFF_ID", staffId);
        					param.put("TRADE_DEPART_ID", departId);
        					
        					params.add(param);
    					}    					
    				}	
    			}
    			
    			if(params!=null&&params.size()>0){
    				IData extendParam=new DataMap();
        			extendParam.put("EXTEND_DATAS", params);
        			extendParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        			CSViewCall.call(this, "CS.UserInfoQrySVC.extendBackUserData", extendParam);
    			}
    		}
    	}
    	
    	
    	/*
    	 * 重新刷新前台结果
    	 */
    	IData condData = this.getData("cond", true);
        
        IData checkParam=new DataMap();
        checkParam.put("QUERY_DEPART_CODE", condData.getString("DEVELOP_DEPART_ID",""));
        checkParam.put("STAFF_DEPART_ID", getVisit().getDepartId());
        checkParam.put("STAFF_CITY_CODE", getVisit().getCityCode());
        checkParam.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataOutput checkResult = CSViewCall.callPage(this, "CS.UserInfoQrySVC.checkUserRight", checkParam, this.getPagination("navt"));
        IDataset checkDatas=checkResult.getData();
        IData checkResultData=checkDatas.getData(0);
        
        String checkResultCode=checkResultData.getString("QUERY_RESULT_CODE","");
        
        if(checkResultCode.equals("0")){
        	IData param=new DataMap();
        	param.put("IS_EXTEND_TIME", data.getString("IS_EXTEND_TIME",""));
            param.put("QUERY_DEPART_ID", checkResultData.getString("QUERY_DEPART_ID"));
            param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            param.put("TRADE_STAFF_ID", data.getString("TRADE_STAFF_ID"));
            param.put("START_DATE", data.getString("START_DATE"));
            param.put("END_DATE", data.getString("END_DATE"));
            param.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
            
        	IDataOutput result = CSViewCall.callPage(this, "SS.UserInfoQrySVC.queryBackUser", param, this.getPagination("navt"));
        	
        	this.setPageCount(result.getDataCount());
            this.setInfos(result.getData());
        }else{
        	this.setPageCount(0);
            this.setInfos(new DatasetList());
        }
    	
    	IData extendResult=new DataMap();
    	extendResult.put("EXTEND_RESULT", "0");
    	setAjax(extendResult);

    }
    
    public void importData(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData set = null;
        
        data.put("STAFF_ID", getVisit().getStaffId());
        data.put("STAFF_DEPART_ID", getVisit().getDepartId());
        data.put("STAFF_CITY_CODE", getVisit().getCityCode());
        data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        IDataset sets = CSViewCall.call(this, "SS.BackUserExtendSVC.importDataByFile", data);
        if (sets.size() > 0)
        {
            set = sets.getData(0);
            setAjax(set);
        }
    }
    
    /**
     * 查询部门类型
     * 
     * @param cycle
     * @throws Exception
     */
    public void qryDepartKinds(IRequestCycle cycle) throws Exception
    {
        IData input = new DataMap();
        input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        IDataset departKinds = CSViewCall.call(this, "SS.QueryInfoSVC.queryDepartKinds", input);
        if (IDataUtil.isNotEmpty(departKinds))
        {
            for (int i = 0; i < departKinds.size(); i++)
            {
                String departKind = departKinds.getData(i).getString("DEPART_KIND");
                String departKindCode = departKinds.getData(i).getString("DEPART_KIND_CODE");
                departKinds.getData(i).put("DEPART_KIND", "[" + departKindCode + "]" + departKind);
            }
            setDepartKinds(departKinds);
        }
    }

}
