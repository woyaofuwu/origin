
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;

public class CheckUnifyPayProductAcct extends BreBase implements IBREScript
{
    /**
     * 201611011510规则
     */
    private static final long serialVersionUID = 1L;
    
    private static Logger logger = Logger.getLogger(CheckUnifyPayProductAcct.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckUnifyPayProductAcct()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String newProductId = databus.getString("PRODUCT_ID");
        String custId = databus.getString("CUST_ID");
        String acctId = databus.getString("ACCT_ID");
        if (!"7345".equals(newProductId))
        {
            return true;
        }
        
        IDataset result = TradeAcctInfoQry.getUnifyPayTradeAcctByCustId(custId,acctId);
        if (result.getData(0).getInt("COUNT") <= 0)
        {
            err = "集团统一付费产品需要新增一个专有账户来计费!";
            BreTipsHelp.addNorTipsInfo(databus, 
                    BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
        
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckUnifyPayProductAcct() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
