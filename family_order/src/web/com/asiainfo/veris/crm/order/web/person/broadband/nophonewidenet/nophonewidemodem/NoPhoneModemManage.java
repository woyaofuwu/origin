
package com.asiainfo.veris.crm.order.web.person.broadband.nophonewidenet.nophonewidemodem;

import org.apache.tapestry.IRequestCycle;
 
import com.ailk.bizview.base.CSViewCall; 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NoPhoneModemManage extends PersonBasePage
{ 
	 public void initApplyType(IRequestCycle cycle) throws Exception
	    {
	        IDataset applyTypeList = pageutil.getStaticList("WIDE_MODEM_STYLE");
	        boolean isLargess = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "FTTH_FREE_RIGHT");//赠送光猫权限查询
	        for (int i = 0; i < applyTypeList.size(); i++)
	        {
	            IData applyType = applyTypeList.getData(i);
	            if (applyType.containsValue("3"))
	            {
	            	applyTypeList.remove(i); // 过滤自备光猫模式
	            }
	            if(!isLargess && applyType.containsValue("2")){
	            	applyTypeList.remove(i);
	            }
	        }
	        this.setApplyTypeList(applyTypeList);
	    }
    /**
     * FTTH光猫管理
     * @param clcle
     * @throws Exception
     * @author chenxy3
     */ 
    public void onTradeSubmit(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String routeId = data.getString("EPARCHY_CODE");
        // 客服工号，HAIN, 则默认到0898
        if (StringUtils.isBlank(routeId) || routeId.length() != 4 || !StringUtils.isNumeric(routeId))
        {
            data.put("EPARCHY_CODE", "0898");
        }
        IDataset dataset = null;
        if(tradeTypeCode.equals("6801")){//FTTH光猫审理
        	String deposit = data.getString("DEPOSIT");
        	if(deposit != null || deposit != ""){
        		deposit = Integer.parseInt(deposit)*100 + "";
            	data.put("DEPOSIT",deposit.trim());
        	}
        	dataset = CSViewCall.call(this, "SS.NoPhoneModemApplyRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6802")){//FTTH光猫更改
            dataset = CSViewCall.call(this, "SS.NoPhoneModemChangeRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6803")){//FTTH光猫退还
        	 dataset = CSViewCall.call(this, "SS.NoPhoneModemReturnRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6804")){//FTTH光猫丢失
        	 dataset = CSViewCall.call(this, "SS.NoPhoneModemLoseRegSVC.tradeReg", data);
        } 
        setAjax(dataset);
    } 
    
    /**
     * 
     * 判断用户规则
     * chenxy3 20151208
     * */
    public void checkFTTHBusi(IRequestCycle cycle) throws Exception
    { 
        IData pagedata = getData(); 
        pagedata.put(Route.ROUTE_EPARCHY_CODE, pagedata.getString("ROUTE_EPARCHY_CODE", getTradeEparchyCode()));
        IData info = new DataMap();
        //先校验是否无手机宽带用户
        CSViewCall.call(this, "SS.NoPhoneModemManageSVC.checkIfNoPhoneUser", pagedata);
      //判断该用户是否有未完工申领光猫
    	IDataset tradeModems = CSViewCall.call(this, "SS.NoPhoneModemManageSVC.getUserTradeWWG", pagedata); 
    	if(tradeModems!=null && tradeModems.size()>0){//有未完工工单，提示用户
    		info.put("CHECK_CODE", "0");
    		String applyType = tradeModems.getData(0).getString("RSRV_TAG1","");
    		String applyName = pageutil.getStaticValue("WIDE_MODEM_STYLE", applyType);
    		info.put("CHECK_INFO", "该用户"+pagedata.getString("SERIAL_NUMBER")+"有未完工工单。");
    	}else{
    		info.put("CHECK_CODE", "1");
	      //宽带地址信息
	        IDataset dataset = CSViewCall.call(this, "SS.NoPhoneModemManageSVC.getUserWideInfo", pagedata);
	        String widetype = dataset.getData(0).getString("RSRV_STR2");
	        String hTOb=dataset.getData(0).getString("H_TO_B","0");
	        info.put("hTOb", hTOb);
	        if ("5".equals(widetype)){
	        	widetype="3";
	        }
	      
	        info.put("WIDE_TYPE_CODE", widetype);
	        if ("1".equals(widetype))
	        {
	        	info.put("WIDE_TYPE_NAME", "移动FTTB");
	        }else if ("2".equals(widetype))
	        {
	        	info.put("WIDE_TYPE_NAME", "铁通ADSL");
	        }else if ("3".equals(widetype))
	        {
	        	info.put("WIDE_TYPE_NAME", "移动FTTH");
	        }else if ("5".equals(widetype))
	        {
	        	info.put("WIDE_TYPE_NAME", "铁通FTTH");
	        }else if ("6".equals(widetype))
	        {
	        	info.put("WIDE_TYPE_NAME", "铁通FTTB");
	        }
	        //如果是FTTH，则需要检查是否租用了光猫 
	        if("3".equals(widetype) || "5".equals(widetype) 
	        		||"1".equals(hTOb))// 
	        {	String userStateDesc="";
	        	String serialNumber = pagedata.getString("SERIAL_NUMBER");
	        	IData param = new DataMap();
	        	param.put("SERIAL_NUMBER", serialNumber);
	        	 
	        	IDataset userinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.getUserInfoBySerailNumber", param);
	        	
	        	if(!userinfo.isEmpty())
	        	{
	        		if(!userinfo.first().getString("RSRV_STR10","").equals("BNBD"))
	        		{
	        			//IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
	        			IDataset userOtherinfo = CSViewCall.call(this, "SS.DestroyUserNowSVC.queryUserModemRent", userinfo.first());
	            		if(!userOtherinfo.isEmpty())
	            		{
	            			info.put("MODEM_CODE", userOtherinfo.getData(0).getString("RSRV_STR1", ""));
	            			info.put("MODEM_FEE", userOtherinfo.getData(0).getString("RSRV_STR2", "0"));
	            			info.put("MODEM_MODE", userOtherinfo.getData(0).getString("RSRV_TAG1", "0"));
	
	            			info.put("MODEM_FEE_STATE", userOtherinfo.getData(0).getString("RSRV_STR7", "0"));
	            			info.put("MODEM_START_DATE", userOtherinfo.getData(0).getString("START_DATE", "0"));
	            			info.put("MODEM_END_DATE", userOtherinfo.getData(0).getString("END_DATE", "0"));
	            			if(!"".equals(userOtherinfo.getData(0).getString("RSRV_STR1", ""))){
	            				
	            			}
	            		}else{
	    	        		userStateDesc= "用户未申领光猫。";
	    	        	}
	        		} 
	        	}
	        	String modemmode=info.getString("MODEM_MODE","");
	        	if ("0".equals(modemmode))
	        	{
	        		info.put("MODEM_MODE_NAME","租赁");
	        	}else if ("1".equals(modemmode))
	        	{
	        		info.put("MODEM_MODE_NAME","购买");
	        	}else if ("2".equals(modemmode))
	        	{
	        		info.put("MODEM_MODE_NAME","赠送");
	        	}else if ("3".equals(modemmode))
	        	{
	        		info.put("MODEM_MODE_NAME","自备");
	        	}else{
	        		info.put("MODEM_MODE_NAME","无");
	        	}
	        	
	        	//押金状态
	        	String feestate=info.getString("MODEM_FEE_STATE","");
	        	if ("0".equals(feestate))
	        	{
	        		info.put("MODEM_FEE_STATE_NAME","正常");
	        	}else if ("1".equals(feestate))
	        	{
	        		info.put("MODEM_FEE_STATE_NAME","已转移");
	        	}else if ("2".equals(feestate))
	        	{
	        		info.put("MODEM_FEE_STATE_NAME","已退还");
	        	}else if ("3".equals(feestate))
	        	{
	        		info.put("MODEM_FEE_STATE_NAME","已沉淀");
	        	}else if ("3".equals(feestate))
	        	{
	        		info.put("MODEM_FEE_STATE_NAME","无");
	        	}
	        	
	        	//光猫押金
	        	String modemDepost=info.getString("MODEM_FEE","0");
	            if(!"0".equals(modemDepost)){
	            	modemDepost=""+Integer.parseInt(modemDepost)/100;
	            	if(!"".equals( info.getString("MODEM_CODE",""))){
	            		userStateDesc= "用户已经申领光猫并已交押金。";
	            	}else{
	            		userStateDesc= "用户已经已交光猫押金，未有光猫串号。";
	            	}
	            }
	            
	            
	            info.put("USER_STATE_DESC", userStateDesc);
	            info.put("MODEM_DEPOSIT",modemDepost);
	        } 
	        this.setInfo(info);
    	}
        this.setAjax(info); 
    } 
    
    /**
     * 
     * 判断用户宽带是否能办理该业务
     * lijun17 20160616
     * */
    public void checkOperType(IRequestCycle cycle) throws Exception
    { 
    	IData pagedata = getData(); 
    	String serialNum=pagedata.getString("SERIAL_NUMBER","");
    	
    	if(serialNum.length()==18){
			//通过身份证获取KD_账号
			IData callParam=new DataMap();
			callParam.put("PSPT_ID", serialNum);
			IDataset phoneSet = CSViewCall.call(this, "SS.NoPhoneWideChangeProdSVC.noPhoneUserQryByPSPTID", callParam);//根据身份证获取无手机宽带账号
			if(phoneSet!=null && phoneSet.size()==1){
				pagedata.put("SERIAL_NUMBER", phoneSet.getData(0).getString("SERIAL_NUMBER",""));
			} 
		} 
    	/**
    	 * 先解决2个问题：
    	 * 1、FTTH才允许使用该界面。
    	 * 2、已申领过的不允许再次申领。
    	 * */
    	IDataset results = CSViewCall.call(this, "SS.FTTHModemManageSVC.checkOperType", pagedata);
    	
    	
    	this.setAjax(results); 
    }
    
    /**
     * REQ201511190036 关于开发商务宽带业务免押金0元租用光猫界面的需求
     * 查询录入的宽带号码是否满足条件
     * chenxy3 20151208
     * */
    public void checkKDNumber(IRequestCycle cycle) throws Exception
    { 
        IData pagedata = getData(); 
        IData checks=new DataMap();
        IData results = CSViewCall.call(this, "SS.FTTHBusiModemManageSVC.checkKDNumber", pagedata).first();
        this.setAjax(results); 
    }  
    
    /**
     * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
     * 判断用户该收取的押金金额
     * chenxy3 20151029
     * */
    public void checkFTTHdeposit(IRequestCycle cycle) throws Exception
    {

        IData pagedata = getData(); 
        IData results = CSViewCall.call(this, "SS.NoPhoneModemManageSVC.checkFTTHdeposit", pagedata).first(); 
        this.setModermInfo(results);
        this.setAjax(results); 
    }  
    
    /**
     * 查询光猫信息
     * */
    public void queryModermInfo(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.FTTHModemManageSVC.queryModemInfo", pagedata); 
        setInfos(results);
        setAjax(results);
    }
    
    public void queryBusiModermInfo(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.FTTHBusiModemManageSVC.queryBusiModemInfo", pagedata); 
        setInfos(results);
        setAjax(results);
    }
    
    /**
     * 校验终端，预占终端
     * @param cycle
     * @throws Exception
     */
    public void checkModermId(IRequestCycle cycle)throws Exception
    {
    	IData pagedata = getData(); 
    	String serial_number = pagedata.getString("SERIAL_NUMBER");
    	if(serial_number.length() > 11){//商务宽带去掉宽带号码KD_
    		serial_number = serial_number.substring(3, serial_number.length());
    	}
    	pagedata.put("SERIAL_NUMBER", serial_number);
    	IDataset results = CSViewCall.call(this, "SS.TopSetBoxSVC.checkModem", pagedata);
    	IData retData = results.first();
    	setInfo(retData);
    	setAjax(results);
    }
    
    public abstract void setApplyTypeList(IDataset applyTypeList);
    public abstract void setModermInfo(IData modermInfo);
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    
}
