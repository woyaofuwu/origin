
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmAccountException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

public class CheckAdvPayRelaForGrp extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String userId = databus.getString("USER_ID", "");		//集团产品用户ID
        String productId = databus.getString("PRODUCT_ID", "");
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        //查询账户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        IData acctInfo = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        if (IDataUtil.isEmpty(acctInfo))
        {
            CSAppException.apperr(CrmAccountException.CRM_ACCOUNT_114);
        }
        String acctId = acctInfo.getString("ACCT_ID");
        
        //查询是否有特殊代付关系(集团产品统一付费关系)
        IDataset payRelas = PayRelaInfoQry.getAdvPayRelaByGrpUserIdAndAcctId(userId, acctId);
        if (IDataUtil.isNotEmpty(payRelas))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "该集团产品存在代付关系，请先取消，再进行此操作！");
            return false;
        }

        return true;
    }

}
