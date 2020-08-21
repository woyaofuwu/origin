package com.asiainfo.veris.crm.iorder.web.person.imslandline;

import org.apache.tapestry.IRequestCycle;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.person.imslandline.CancelIMSLandLineTrade;

public abstract class CancelIMSLandLineNew extends CancelIMSLandLineTrade {

	/**
     * 判断是否免认证，有权限无需认证
     * @param cycle
     * @throws Exception
     */
    public void isNeedAuth(IRequestCycle cycle) throws Exception
    {    	 
    	 boolean isNeedAuth = StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009");
    	 IData authData = new DataMap();
    	 authData.put("IS_NEED_AUTH", !isNeedAuth ? "YES" : "NO");
    	 setAjax(authData);
    	 
    }
    
    /**
     * 查询用户可取消订单
     * @param cycle
     * @throws Exception
     */
    public void queryCancelTradeList(IRequestCycle cycle) throws Exception
    {    	 
        
        IData pagedata = getData(); 
        IDataset results = CSViewCall.call(this, "SS.CancelIMSLandLineService.queryUserCancelTradeMerge", pagedata); 
        setCancelTradeList(results);
        setAjax(results);
    	
    }
    
	/**
     * 认证校验
     * 
     * @param data
     * @throws Exception
     */
    public void authCheck(IRequestCycle cycle) throws Exception
    {
    	IData data = getData();
        IData authData = new DataMap();
        authData.put("RESULT_CODE", "0");
        data.remove("ACTION");
        IData params = new DataMap();
        params.putAll(data);

        // 除客服，营业厅接入方做校验

        String inModeCode = getVisit().getInModeCode();
        if ("0".equals(inModeCode) || "1".equals(inModeCode) || "3".equals(inModeCode))
        {
        	if(data.getString("SERIAL_NUMBER") == null || "".equals(data.getString("SERIAL_NUMBER","")))
        	{
        		CSViewException.apperr(CrmUserException.CRM_USER_373);
        	}
        	
        	IDataset userInfo = CSViewCall.call(this, "CS.UserInfoQrySVC.getNormalUserInfoBySN", params);
        	
        	if(IDataUtil.isEmpty(userInfo))
            {
        		CSViewException.apperr(CrmUserException.CRM_USER_389, data.getString("SERIAL_NUMBER"));
            }
        	
        	params.put("USER_ID", userInfo.getData(0).getString("USER_ID"));
        	
            boolean rightFlag = false;
            
            //更新时放开
            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "SYS009") && !"347".equals(data.getString("TRADE_TYPE_CODE")) && !"343".equals(data.getString("TRADE_TYPE_CODE")))
            {
                rightFlag = true;
            }
            // 统一认证密码校验
            if (!rightFlag )
            {
            	 
            	
                String svcName = "CS.AuthCheckSVC.authCheck";
                String authType = data.getString("AUTH_TYPE", "0");
                if (authType.equals("04"))
                {
                    params.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
                    svcName = "CS.AuthCheckSVC.authBroadbandCheck";
                }
                IDataset outputData = CSViewCall.call(this, svcName, params);
                if (null != outputData && outputData.size() > 0)
                {
                    authData = outputData.getData(0);
                }
            }
        }
        authData.put("CHECK_MODE", data.getString("CHECK_MODE"));
        setAjax(authData);
    }
    
    
    public void submitCancelTrade(IRequestCycle cycle) throws Exception
    {
        IData input = this.getData();
        IDataset dataset = CSViewCall.call(this, "SS.CancelIMSLandLineService.cancelTradeReg", input);
        setAjax(dataset);
    }
    
    /**
     * REQ201609190029_优化家庭宽带装机退单分类内容
     * @author zhuoyingzhi
     * 20161017
     * <br/>
     * 查询二级原因
     * @param cycle
     * @throws Exception
     */
    public void  qrySecondLevelTypeInfo(IRequestCycle cycle)throws Exception{
          //获取 撤单原因  编码
          String  pdataId=this.getData().getString("CANCEL_REASON");
          
          IDataset iDataset=StaticUtil.getStaticListByParent("WIDE_CANCEL_REASON_RELATION", pdataId);
          
          setCancelReasonSecondLevel(iDataset);
    }
    
    public abstract void setCancelReasonSecondLevel(IDataset dataset);
    public abstract void setCancelTradeList(IDataset dataset);
}
