
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createwideuser.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.person.busi.credittrade.CreditTradeRegSVC;

/**
 * 根据手机状态发起宽带信控流程
 * 
 * @author chenzm
 */
public class DealCreditAction implements ITradeFinishAction
{	 
    public void executeAction(IData mainTrade) throws Exception
    {
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        IData param = new DataMap();
        if ("KD_".equals(serialNumber.substring(0, 3)))
        {
            serialNumber = serialNumber.substring(3);
        }
        param.put("SERIAL_NUMBER", serialNumber);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	// 商务宽带号码需要特殊判断
        	serialNumber = serialNumber.substring(0,serialNumber.length() - 4);
        	userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        	if (IDataUtil.isEmpty(userInfo))
            {
        		param.put("TRADE_TYPE_CODE", "7240");
        		
            } else {
        		
                String userStateCodeSet = userInfo.getString("USER_STATE_CODESET");
                if ("5".equals(userStateCodeSet))
                {
                    param.put("TRADE_TYPE_CODE", "7220");
                }
                else if ("7".equals(userStateCodeSet))
                {
                    param.put("TRADE_TYPE_CODE", "7110");
                }
                else if ("B".equals(userStateCodeSet))
                {
                    param.put("TRADE_TYPE_CODE", "7101");
                }
                else
                {
                    return;
                }
        	}
        }
        else
        {
            String userStateCodeSet = userInfo.getString("USER_STATE_CODESET");
            if ("5".equals(userStateCodeSet))
            {
                param.put("TRADE_TYPE_CODE", "7220");
            }
            else if ("7".equals(userStateCodeSet))
            {
                param.put("TRADE_TYPE_CODE", "7110");
            }
            else if ("B".equals(userStateCodeSet))
            {
                param.put("TRADE_TYPE_CODE", "7101");
            }
            else
            {
                return;
            }
        }
        CreditTradeRegSVC.widenetTradeReg(param);
    }

}
