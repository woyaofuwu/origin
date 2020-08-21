
package com.asiainfo.veris.crm.order.soa.person.rule.run.svc;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;

/**
 * 校验宽带用户的是否已经预约报停
 * 
 * @author chenzm
 */
public class CheckBookSvcStateRule extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID");

        // 查询用户有效主体服务状态
        IDataset dataset = UserSvcStateInfoQry.queryUserSvcStateInfo(userId);
        if (IDataUtil.isNotEmpty(dataset))
        {
            int size = dataset.size();
            for (int i = 0; i < size; i++)
            {
                IData data = dataset.getData(i);
                String mainTag = data.getString("MAIN_TAG");
                // String startDate = data.getString("START_DATE");
                String stateCode = data.getString("STATE_CODE");
                if ("1".equals(mainTag) && ("1".equals(stateCode) || "5".equals(stateCode)))
                {
                	String strTradeTypeCode = databus.getString("TRADE_TYPE_CODE", "");
                	if("603".equals(strTradeTypeCode) || "632".equals(strTradeTypeCode) || "7220".equals(strTradeTypeCode))
                	{
                		boolean bIsHave = true;
                		IDataset Commparas5666 = CommparaInfoQry.getCommparaAllColByParser("CSM", "5666", "CheckBookSvcStateRule", "0898");
                		if(IDataUtil.isNotEmpty(Commparas5666))
                		{
                			
                			for (int j = 0; j < Commparas5666.size(); j++)
                			{
                				IData Commpara5666 = Commparas5666.getData(j);
                				String strComTTC = Commpara5666.getString("PARA_CODE1", "");
                				IDataset idsTradeBs = TradeInfoQry.getMainTradeByUserIdTypeCode(userId, strComTTC);
                        		if (IDataUtil.isNotEmpty(idsTradeBs))
                                {
                        			bIsHave = false;
                        			break;
                                }
							}
                		}
                		return bIsHave;
                	}
                	else
                	{
                		return true;
                	}
                    
                }

            }

        }

        return false;
    }
}
