
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;

public class CheckGrpUnifyPayRelation extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 1L;

    /**
     * 201611011512规则
     * 集团统一付费产品代付关系判断
     */
    private static Logger logger = Logger.getLogger(CheckGrpUnifyPayRelation.class);
    
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckGrpUnifyPayRelation()  >>>>>>>>>>>>>>>>>>");
        }
        
        String err = "";
        String userId = databus.getString("USER_ID", "");//集团产品的user_id
        String errCode = ErrorMgrUtil.getErrorCode(databus);
        
        
        if(StringUtils.isNotEmpty(userId))
        {
            IDataset accountInfos = PayRelaInfoQry.qryUnifyAccountByUserId(userId);
            if(IDataUtil.isNotEmpty(accountInfos))
            {
                String acctId = accountInfos.getData(0).getString("ACCT_ID");
                if(StringUtils.isNotEmpty(acctId))
                {
                    IDataset countInfos = AcctInfoQry.getUnifyCountByUserId(acctId);
                    int gfspTradeNum  = countInfos.getData(0).getInt("RECORDCOUNT");
                    if(gfspTradeNum > 0)
                    {
                        err = "该集团用户还有代付关系,不允许注销!";
                        BreTipsHelp.addNorTipsInfo(databus, 
                                BreFactory.TIPS_TYPE_ERROR,
                                errCode, err.toString());
                        return false;
                    }
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckGrpUnifyPayRelation() <<<<<<<<<<<<<<<<<<<");
        }
        
        return true;
    }

}
