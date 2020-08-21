
package com.asiainfo.veris.crm.order.web.person.changecustowner;
import org.apache.log4j.Logger;
import org.apache.tapestry.IRequestCycle;

import com.ailk.bizcommon.route.Route;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * REQ201612260013_【下岛问题】过户界面无法一次性提醒限制条件优化
 * @author zhuoyingzhi
 * @date 20170413
 */
public abstract class CheckBeforeChangeCustOwner extends PersonBasePage
{
	Logger logger=Logger.getLogger(CheckBeforeChangeCustOwner.class);
	public void init(IRequestCycle cycle) throws Exception
    {


    }

	/**
	 * 
	 * @param cycle
	 * @throws Exception
	 */
    public void queryChangeCustInfo(IRequestCycle cycle) throws Exception
    {
        IData inputData = this.getData("cond", true);
        /*REQ201712040014销户业务限制查询 modify by zhanglin3 20180205*/
        String selectTradeTypeCode = inputData.getString("SELECT_TRADE_TYPE_CODE");
        inputData.put("DEPART_ID", this.getVisit().getDepartId());
        inputData.put("EPARCHY_CODE", getTradeEparchyCode());
        inputData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        inputData.put("IN_MODE_CODE", "0");
        inputData.put("TRADE_TYPE_CODE", "100");
        inputData.put("ORDER_TYPE_CODE", "100");
        //返回界面显示的集合
        IDataset result=new DatasetList();
        
        String serial_number=inputData.getString("SERIAL_NUMBER");
   
        
        //获取用户所用相关信息
        IDataset userInfoList=CSViewCall.call(this, "SS.ChangeCustOwnerViewSVC.qryAllUserInfo", inputData);
        logger.debug("------------userInfoList----------"+userInfoList);
        if(IDataUtil.isEmpty(userInfoList)){
        	CSViewException.apperr(CrmCommException.CRM_COMM_103,"获取用户所有相关信息为空");
        }
        
        IData userinfo=userInfoList.getData(0);
        
        /**
         * 查询号码规则
         */
        IData data =new DataMap(userinfo);
        data.put("ID", userinfo.getString("USER_ID"));
        data.put("USER_ID", userinfo.getString("USER_ID"));
        data.put("CUST_ID", userinfo.getString("CUST_ID"));
        data.put("PRODUCT_ID", userinfo.getString("PRODUCT_ID"));
        data.put("BRAND_CODE", userinfo.getString("BRAND_CODE"));
        data.put("X_CHOICE_TAG", "0"); // 0:输号码校验;1:提交校验;
        data.put("TRADE_TYPE_CODE", selectTradeTypeCode);
        data.put("ACTION_TYPE", "TradeCheckBefore");
        data.put("ORDER_TYPE_CODE", selectTradeTypeCode);
        data.put("SERIAL_NUMBER", serial_number);
        data.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        
        logger.debug("------------CS_CheckTradeSVC_checkBeforeTrade----------"+data);
        
        IDataset beforeTradeInfoSet = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        //IDataset infos = CSViewCall.call(this, "CS.CheckTradeSVC.checkBeforeTrade", data);
        logger.debug("------------CS_CheckTradeSVC_checkBeforeTrade---beforeTradeInfoSet-------"+beforeTradeInfoSet);
        //转换bre返回的提示信息
        transData(beforeTradeInfoSet, result);
         
        
        /**
         * 提交前的校验规则
         */
        //IDataset checkBeforeInfoSet = CSViewCall.call(this, "SS.ChangeCustOwnerViewSVC.checkBefore", data);
        
        //transData(checkBeforeInfoSet, result);
        
        
        
        
        if (IDataUtil.isEmpty(result))
        {
            this.setAjax("X_RESULTCODE", "0");
        }else
        {
        	// 设置页面返回数据 X_RESULTCODE
            setInfos(result);
            setCondition(inputData);
            setPageCount(result.size());
            
            this.setAjax("X_RESULTCODE","0001");
        }
    }


  
    
    /**
     * 
     * @param beforeTradeInfoSet
     * @param result
     * @throws Exception
     */
    public void transData(IDataset beforeTradeInfoSet,IDataset  result)throws Exception{
    	
        if (IDataUtil.isNotEmpty(beforeTradeInfoSet))
        {
        	
        	IData beforeTradeInfo=beforeTradeInfoSet.getData(0);
        	IDataset  tipsTypErrorInfo=beforeTradeInfo.getDataset("TIPS_TYPE_ERROR");
            if(IDataUtil.isNotEmpty(tipsTypErrorInfo)){
            	for(int k = 0;k < tipsTypErrorInfo.size();k++)
            	{
            		IData data=new DataMap();
	        		String resultInfo = tipsTypErrorInfo.getData(k).getString("TIPS_INFO","");
	        		String resultCode = tipsTypErrorInfo.getData(k).getString("TIPS_CODE","");
	        		
	        		data.put("FORBIDDEN_MSG", resultInfo);
	        		data.put("FORBIDDEN_CODE", resultCode);
	        		
	        		result.add(data);
        		}
            }
        	
            //REQ201907260012 优化《过户销户业务限制查询》界面
        	/*IDataset  tipsTypInfo=beforeTradeInfo.getDataset("TIPS_TYPE_TIP");
            if(IDataUtil.isNotEmpty(tipsTypInfo)){
            	for(int k = 0;k < tipsTypInfo.size();k++)
            	{
            		IData data=new DataMap();
	        		String resultInfo = tipsTypInfo.getData(k).getString("TIPS_INFO","");
	        		String resultCode = tipsTypInfo.getData(k).getString("TIPS_CODE","");
	        		
	        		data.put("FORBIDDEN_MSG", resultInfo);
	        		data.put("FORBIDDEN_CODE", resultCode);
	        		
	        		result.add(data);
        		}
            }        	
        	
            
        	IDataset  tipsTypChoiceInfo=beforeTradeInfo.getDataset("TIPS_TYPE_CHOICE");
            if(IDataUtil.isNotEmpty(tipsTypChoiceInfo)){
            	for(int k = 0;k < tipsTypChoiceInfo.size();k++)
            	{
            		IData data=new DataMap();
	        		String resultInfo = tipsTypChoiceInfo.getData(k).getString("TIPS_INFO","");
	        		String resultCode = tipsTypChoiceInfo.getData(k).getString("TIPS_CODE","");
	        		
	        		data.put("FORBIDDEN_MSG", resultInfo);
	        		data.put("FORBIDDEN_CODE", resultCode);
	        		
	        		result.add(data);
        		}
            }*/ 
        }
    }
    
    
    
    public abstract void setCondition(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setInfos(IDataset infos);

    public abstract void setPageCount(long l);

}
