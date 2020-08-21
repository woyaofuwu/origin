package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQueryHelp;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;


public class CheckLimitByCPEServiceState extends BreBase implements IBREScript
{

	/**
     * Copyright: Copyright 2016 Asiainfo
     * 
     * @Description: CPE服务状态与业务受理判断 【CheckLimitByCPEServiceState】
     * @author: songlm
     * 参照TBCheckLimitByServiceState改造
     */
	
	private static final long serialVersionUID = 1L;
	private static Logger logger = Logger.getLogger(CheckLimitByCPEServiceState.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckLimitByCPEServiceState() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        
        String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String userId = databus.getString("USER_ID");
        String strEparchyCode = databus.getString("TRADE_EPARCHY_CODE");
        
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("TRADE_TYPE_CODE", strTradeTypeCode);
        param.put("EPARCHY_CODE", strEparchyCode);
        IDataset listTmp = Dao.qryByCode("TD_S_SVCSTATE_TRADE_LIMIT", "SEL_EXISTS_BY_USERID", param);
        if(IDataUtil.isNotEmpty(listTmp))
        {
//        	String strSvcstateName = BreQueryHelp.getNameByCode("SvcStateName", new String[]
//        	{ (String) listTmp.get(0, "SERVICE_ID"), (String) listTmp.get(0, "STATE_CODE") });
        	String strSvcstateName = USvcStateInfoQry.getSvcStateNameBySvcIdStateCode((String) listTmp.get(0, "SERVICE_ID"), (String) listTmp.get(0, "SERVICE_ID"));
        	
        	String errInfo = listTmp.getData(0).getString("RSRV_STR1","");//提示信息
        	BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 2016031601, "用户状态[" + strSvcstateName + "],不能办理该项业务!" + errInfo);
        	bResult = true;
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckLimitByCPEServiceState() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
