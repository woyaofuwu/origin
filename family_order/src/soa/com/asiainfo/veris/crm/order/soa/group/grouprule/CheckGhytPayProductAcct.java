
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;

/**
 * 集团海洋通产品账户校验，要求新增专用的账户
 * @author chenzg
 *
 */
public class CheckGhytPayProductAcct extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;
    
    private static Logger logger = Logger.getLogger(CheckGhytPayProductAcct.class);
    
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGhytPayProductAcct()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        String newProductId = databus.getString("PRODUCT_ID");
        String custId = databus.getString("CUST_ID");
        String acctId = databus.getString("ACCT_ID");
        if (!"84011638".equals(newProductId))
        {
            return true;
        }
        
        IDataset payRelas = AcctInfoQry.getPayrelaAcctByAcctId(acctId);
        if (IDataUtil.isNotEmpty(payRelas))
        {
            err = "集团海洋通产品需要新增一个专有账户来计费!";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, err);
            return false;
        }
        
        if (logger.isDebugEnabled()){
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckGhytPayProductAcct() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
