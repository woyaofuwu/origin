/**
 * 
 */

package com.asiainfo.veris.crm.iorder.web.person.changesvcstate;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeSvcState
 * @Description: 服务状态变更view类
 * @version: v1.0.0
 * @author: xiaozb
 * @date: 20140306 17:00
 */
public abstract class ChangeSvcStateNew extends PersonBasePage
{
    /**
     * @Function:
     * @Description: 该方法的描述
     * @param:
     * @return：
     * @throws：异常描述
     * @version: v1.0.0
     * @author: chengxf2
     * @date: 2014-8-5 下午07:59:29 Modification History: Date Author Version Description
     *        ---------------------------------------------------------* 2014-8-5 chengxf2 v1.0.0 修改原因
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception
    {
        IData pgData = this.getData();
        String tradeTypeCode = pgData.getString("TRADE_TYPE_CODE");
        String authType = pgData.getString("authType", "00");
        IData info = new DataMap();
        info.put("TRADE_TYPE_CODE", tradeTypeCode);
        info.put("authType", authType);
        this.setInfo(info);
    }
    
    /**
     * 用户鉴权后初始化方法
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void loadChildInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.loadChildInfo", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            setAjax(resultData);
        }
    }
    
    /**
     * 手机报停关联停宽带校验
     * @param clcle
     * @throws Exception
     * @author yuyj3
     */
    public void checkStopWide(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkStopWide", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            setAjax(resultData);
        }
    }
    
    /**
     * 手机报开关联开宽带校验
     * @param clcle
     * @throws Exception
     * @author wukw3
     */
    public void checkOpenWide(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkOpenWide", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            setAjax(resultData);
        }
    }
    

    /**
     * @param requestCycle
     * @description:服务状态变更业务提交类
     * @author: xiaozb
     */
    public void onTradeSubmit(IRequestCycle requestCycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isEmpty(data.getString("SERIAL_NUMBER", "")))
        {
        	data.put("SERIAL_NUMBER", data.getString("AUTH_SERIAL_NUMBER"));
        }
        
        /**
         * IMS固话
         * <br/>
         * IMS家庭固话报停 、报开、局方停机、局方开机
         * @author zhuoyingzhi
         * @date 20171030
         */
        String tradeTypeCode= data.getString("TRADE_TYPE_CODE","");
        if("9807".equals(tradeTypeCode)||"9808".equals(tradeTypeCode)
        		||"9822".equals(tradeTypeCode)||"9823".equals(tradeTypeCode)){
        	//IMS家庭固话，报停、报开,局方停机，局方开机
        	IData ImsInfo=CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
        	if(IDataUtil.isNotEmpty(ImsInfo)){
        		//IMS家庭固话 userid
        		String  userIdB=ImsInfo.getString("USER_ID_B", "");
        		//IMS家庭固话   手机号码
        		String serialNumberB=ImsInfo.getString("SERIAL_NUMBER_B", "");
        		
        		data.put("SERIAL_NUMBER", serialNumberB);
        		data.put("USER_ID", userIdB);
        		//为了不执行bre
        		data.put("X_CHOICE_TAG", "1");
        	}else{
        		CSViewException.apperr(CrmCommException.CRM_COMM_103,"该客户没有IMS家庭固话业务,不能办理该业务.");
        	}
        }
        
        IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.tradeReg", data);
        setAjax(dataset);
    }
    
    /**
     * @param info
     * @description:报停/报开页面，根据号码查询服务状态判断业务类型编码
     * @author: huanghua
     */
    public void getTradeTypeCode(IRequestCycle requestCycle) throws Exception{
    	IData data = getData();
    	IDataset dataset = CSViewCall.call(this, "SS.ChangeSvcStateRegSVC.confirmTradeTypeCode", data);
    	setAjax(dataset);
    }
    
    /**
     * REQ201708240014_家庭IMS固话开发需求
     * <br/>
     * 判断用户是否是手机报停状态
     * @author zhuoyingzhi
     * @date 20171219
     */
    public void checkOpenIMS(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        
        IData resultData = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.checkOpenIMS", data);
        
        if (IDataUtil.isNotEmpty(resultData))
        {
            setAjax(resultData);
        }
    }
    
    public abstract void setInfo(IData info);
}
