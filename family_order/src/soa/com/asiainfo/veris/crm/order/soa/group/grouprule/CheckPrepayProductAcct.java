
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;

public class CheckPrepayProductAcct extends BreBase implements IBREScript
{
    /**
     * 201907100001 规则
     */
    private static final long serialVersionUID = 1L;
    
    private static Logger logger = Logger.getLogger(CheckPrepayProductAcct.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckPrepayProductAcct()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String newProductId = databus.getString("PRODUCT_ID");
        String custId = databus.getString("CUST_ID");
        String acctId = databus.getString("ACCT_ID");
        
        if(StringUtils.isNotBlank(custId) && StringUtils.isNotBlank(acctId))
        {
        	if ("7349".equals(newProductId))
            {
        		IDataset result = TradeAcctInfoQry.getPrepayTradeAcctByCustId(custId,acctId);
                if (result.getData(0).getInt("COUNT") <= 0)
                {
                    err = "集团客户预缴款(虚拟)产品需要新增一个专有账户来计费!";
                    BreTipsHelp.addNorTipsInfo(databus, 
                            BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
            }
        	else 
        	{
        		IDataset result = TradeAcctInfoQry.getPrepayTradeAcctByCustId(custId,acctId);
                if (result.getData(0).getInt("COUNT") > 0)
                {
                    err = "该账户[" + acctId + "]是集团客户预缴款(虚拟)产品的专有账户,不可作为其他集团产品的账户,请重新选择!";
                    BreTipsHelp.addNorTipsInfo(databus, 
                            BreFactory.TIPS_TYPE_ERROR, errCode, err);
                    return false;
                }
        	}
        }
        
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckPrepayProductAcct() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
