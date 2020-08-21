
package com.asiainfo.veris.crm.order.web.person.broadband.widenet.ftthmodemmanage;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class FTTHModemManage extends PersonBasePage
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
        if(tradeTypeCode.equals("6131")){//FTTH光猫审理
        	String deposit = data.getString("DEPOSIT");
        	if(deposit != null || deposit != ""){
        		deposit = Integer.parseInt(deposit)*100 + "";
            	data.put("DEPOSIT",deposit.trim());
        	}
        	dataset = CSViewCall.call(this, "SS.FTTHModemApplyRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("7132")){//FTTH光猫更改
            dataset = CSViewCall.call(this, "SS.FTTHModemChangeRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("7133")){//FTTH光猫退还
        	 dataset = CSViewCall.call(this, "SS.FTTHModemReturnRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("7134")){//FTTH光猫丢失
        	 dataset = CSViewCall.call(this, "SS.FTTHModemLoseRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("7135")){//FTTH光猫升级
       	     dataset = CSViewCall.call(this, "SS.FTTHModemUpLevelRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6132")){//FTTH商务光猫申领
        	 IDataset paramSet = new DatasetList(data.getString("FTTH_DATASET"));
        	 data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
        	 dataset = CSViewCall.call(this, "SS.FTTHBusiModemManageApplyRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6133")){//FTTH商务光猫更换
        	 dataset = CSViewCall.call(this, "SS.FTTHBusiModemChangeRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6134")){//FTTH商务光猫退还
        	 dataset = CSViewCall.call(this, "SS.FTTHBusiModemReturnRegSVC.tradeReg", data);
        }else if(tradeTypeCode.equals("6135")){//FTTH商务光猫丢失
        	 dataset = CSViewCall.call(this, "SS.FTTHBusiModemLoseRegSVC.tradeReg", data);
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
        IData checks=new DataMap();
        IDataset results = CSViewCall.call(this, "SS.FTTHBusiModemManageSVC.checkFTTHBusi", pagedata); 
        if(results==null || results.size()==0){
        	checks.put("RTNCODE", "9");
        	checks.put("RTNMSG", "该用户不存在7341 集团商务宽带产品！"); 
        	//判断该用户是否有未完工申领光猫
        	IDataset tradeModems = CSViewCall.call(this, "SS.FTTHModemManageSVC.queryTradeModemInfo", pagedata); 
        	if(DataSetUtils.isNotBlank(tradeModems)){//有未完工工单，提示用户
        		checks.put("CHECK_CODE", "0");
        		String applyType = tradeModems.getData(0).getString("RSRV_TAG1","");
        		String applyName = pageutil.getStaticValue("WIDE_MODEM_STYLE", applyType);
        		checks.put("CHECK_INFO", "该用户"+pagedata.getString("SERIAL_NUMBER")+"有未完工工单并且有"+applyName+"模式的光猫");
        	}
        }
        this.setAjax(checks); 
    } 
    
    /**
     * 
     * 判断用户宽带是否能办理该业务
     * lijun17 20160616
     * */
    public void checkOperType(IRequestCycle cycle) throws Exception
    { 
    	IData pagedata = getData(); 
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
        IData results = CSViewCall.call(this, "SS.FTTHModemManageSVC.checkFTTHdeposit", pagedata).first(); 
        this.setModermInfo(results);
        this.setAjax(results); 
    }  
    
    
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
    
    /**
     * 
     * 判断用户否能办理光猫升级业务
     * lizj 20200311
     * */
    public void checkModermUp(IRequestCycle cycle) throws Exception
    { 
    	IData pagedata = getData(); 
    	IData result = new DataMap();
    	result.put("UP_TAG", "0");
    	IDataset results = CSViewCall.call(this, "SS.FTTHModemManageSVC.checkModermUp", pagedata); 
    	if (IDataUtil.isNotEmpty(results))
        {
    		String commparaTag = results.first().getString("COMMPARA_TAG");
    		String discntTag = results.first().getString("DISCNT_TAG");
    		String dateTag = results.first().getString("DATE_TAG");
    		if("1".equals(discntTag)&&("1".equals(commparaTag)||"1".equals(dateTag))){
    			result.put("UP_TAG", "1");
    		}
    		
        }
    	this.setAjax(result); 
    }
    
    public abstract void setApplyTypeList(IDataset applyTypeList);
    public abstract void setModermInfo(IData modermInfo);
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    
}
