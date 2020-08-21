package com.asiainfo.veris.crm.order.soa.script.rule.discnt;


import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class ExistsAllUserDiscntMultiSum extends BreBase implements IBREScript {

    private static Logger logger = Logger.getLogger(ExistsAllUserDiscntMultiSum.class);
    /**
     * 判断用户该笔业务是否办理某一类优惠 ExistsAllUserDiscntMultiSum.sql
     * t.rule_biz_id='2014121818180001'
     */
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 ExistsAllUserDiscntMultiSum() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        int recordcount = 0;
        int recordnum = 0;

        /* 获取规则配置信息 */
        String num = ruleParam.getString(databus, "NUM");
        String strParamCode = ruleParam.getString(databus, "PARAM_CODE");
        String strTradeId= ruleParam.getString(databus, "TRADE_ID");
        String strUserId = ruleParam.getString(databus, "USER_ID");
        String eparchyCode =CSBizBean.getTradeEparchyCode();
        /* 获取业务台账，用户资料信息 */
        IDataset listTradeDiscnt = TradeDiscntInfoQry.queryDiscntTradeByTradeIdAndModifyTag(strTradeId, "1", Route.getJourDb());
        IDataset listUserDiscnt = UserDiscntInfoQry.getUserDiscntByUseridAndTag(strUserId,strParamCode,eparchyCode);//获取用户的优惠
        IDataset listTradeDiscnt1 = TradeDiscntInfoQry.queryDiscntByTradeIdAndUseridAndTag(strTradeId,"0",strUserId,strParamCode,eparchyCode,Route.getJourDb());

        /* 开始逻辑规则校验 */
        for (int i=0;i<listUserDiscnt.size();i++)
        {     
        	IData   userdiscnt=listUserDiscnt.getData(i);
            String elementId = userdiscnt.getString("DISCNT_CODE","");
            for (int k=0;k<listTradeDiscnt.size();k++)
            {     
            	IData   tradediscnt=listTradeDiscnt.getData(k);
                 if(elementId.equals(tradediscnt.getString("DISCNT_CODE",""))){
                	 listUserDiscnt.remove(i);
            	     i--;
            	     break;
               }
            }
        }
        if(IDataUtil.isEmpty(listUserDiscnt)&&IDataUtil.isEmpty(listTradeDiscnt1)){
        	recordcount=listUserDiscnt.size()+listTradeDiscnt1.size();
        }
        if(StringUtils.isNotEmpty(num)){
        	recordnum=Integer.parseInt(num);
        	if(recordcount>recordnum){
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 751016, "业务受理后特殊业务限制：用户订购该流量叠加包的次数超过规定次数!");
        		bResult = true;
        	}
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 ExistsAllUserDiscntMultiSum() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }



}
