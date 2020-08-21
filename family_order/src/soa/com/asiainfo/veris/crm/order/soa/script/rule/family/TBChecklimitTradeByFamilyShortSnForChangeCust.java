
package com.asiainfo.veris.crm.order.soa.script.rule.family;

import java.sql.Timestamp;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.diversifyacct.AcctDayDateUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 业务受理前条件判断：家庭网短号用户未退出家庭网不能办理该业务！【TradeCheckBefore】
 * @author: xiaocl
 */
public class TBChecklimitTradeByFamilyShortSnForChangeCust extends BreBase implements IBREScript
{

    private static Logger logger = Logger.getLogger(TBChecklimitTradeByFamilyShortSn.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByFamilyShortSn() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        if (StringUtils.isBlank(xChoiceTag) || "0".equals(xChoiceTag))
        {
            String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE");
            String strUserId = databus.getString("USER_ID");
            if ("100".equals(strTradeTypeCode))
            {
            	IDataset familySvc=UserSvcInfoQry.qrySvcInfoByUserIdSvcId(strUserId, "831");	// 家庭网短号服务
            	
            	/*
            	 * 用户亲亲网关系结束到账期末则可以放开办理过户业务
            	 */
                if (familySvc!=null&&familySvc.size()>0)
                {
                	//获取用户的本账期末
                	String lastDayTimeThisAcct = AcctDayDateUtil.getLastDayThisAcct(strUserId);
                	//String lastDayTimeThisAcct = SysDateMgr.getLastSecond(firstDayNextAcct);
                	
                	String svcEndDate=familySvc.getData(0).getString("END_DATE");
                	
                	Timestamp lastDayTimeThisAcctT=Timestamp.valueOf(lastDayTimeThisAcct);
                	Timestamp svcEndDateT=Timestamp.valueOf(svcEndDate);
                	
                	//如果时间不相同
                	if(lastDayTimeThisAcctT.compareTo(svcEndDateT)!=0){
                		StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("家庭网短号用户未退出家庭网不能办理该业务！");
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751031, strError.toString());
                	}       
                }
            }

            if (logger.isDebugEnabled())
                logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByFamilyShortSn() " + bResult + "<<<<<<<<<<<<<<<<<<<");
        }
        return bResult;
    }

}
