
package com.asiainfo.veris.crm.order.soa.person.rule.run.discnt;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 集团商务宽带优惠判断
 * 
 * @author 
 * @date 2014-05-23
 */

public class CheckGrpBroadbandUserDiscnt extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckGrpBroadbandUserDiscnt.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGrpBroadbandUserDiscnt() >>>>>>>>>>>>>>>>>>");

        String strBrandCode = "";
        IDataset listTradeUser = databus.getDataset("TF_B_TRADE_USER");
        String eparchyCode = databus.getString("EPARCHY_CODE");
        if(IDataUtil.isNotEmpty(listTradeUser)){
        	strBrandCode = listTradeUser.getData(0).getString("RSRV_STR10","");
        }
        /* 自定义区域 */
        boolean bResult = false;
        //集团商务宽带开户校验
        if("BNBD".equals(strBrandCode)){
        	
        	/* 获取业务台账，用户资料信息 */
            IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
            
            boolean isSelected = false;
            for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
            {
                IData tradeDiscnt = (IData) iter.next();
                String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
                IDataset limiteTrade = CommparaInfoQry.getCommpara("CSM", "7344", discntCode, eparchyCode);
                if (IDataUtil.isNotEmpty(limiteTrade))
                {
                	isSelected = true;
                	break;
                }
            }
            /**
            if(isSelected){
            	String errorString = "集团商务宽带开户只能选择集团商务宽带的套餐[20001088]!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20150407, errorString);
                return true;
            }*/
            if(!isSelected){
            	String errorString = "集团商务宽带开户必需要选择集团商务宽带的套餐[20001088]!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20150407, errorString);
                return true;
            }
            
        } else {
        	//个人宽带校验,不可以选择集团商务宽带的套餐
        	/* 获取业务台账，用户资料信息 */
            IDataset listTradeDiscnt = databus.getDataset("TF_B_TRADE_DISCNT");
            
            boolean isSelected = false;
            for (Iterator iter = listTradeDiscnt.iterator(); iter.hasNext();)
            {
                IData tradeDiscnt = (IData) iter.next();
                String discntCode = tradeDiscnt.getString("DISCNT_CODE","");
                if ("20001088".equals(discntCode))
                {
                	isSelected = true;
                	break;
                }
            }
            if(isSelected){
            	String errorString = "不是集团商务宽带开户不能选择集团商务宽带的套餐[20001088]!";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20150407, errorString);
                return true;
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckGrpBroadbandUserDiscnt() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return false;
    }

}
