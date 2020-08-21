
package com.asiainfo.veris.crm.order.soa.person.rule.run.relationuu;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

/**
 * 家庭账户如果子账号没有拆机则主账号也不能做拆机业务.家庭账号只有一个子账号
 * 
 * @author chenzm
 * @date 2014-05-29
 */
public class CheckMasterAccount extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String userId = databus.getString("USER_ID");
        String tradeTypeCode = databus.getString("TRADE_TYPE_CODE");
        String roleCodeB = "";
        String relationTypeCode = "";
        String errorInfo = "";
        // 平行账号查询
        IDataset relationInfos = RelaUUInfoQry.isMasterAccount(userId, "77");
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            roleCodeB = relationInfos.getData(0).getString("ROLE_CODE_B");
            relationTypeCode = relationInfos.getData(0).getString("RELATION_TYPE_CODE");
        }
        else
        {
            // 家庭账号查询
            relationInfos = RelaUUInfoQry.isMasterAccount(userId, "78");
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                roleCodeB = relationInfos.getData(0).getString("ROLE_CODE_B");
                relationTypeCode = relationInfos.getData(0).getString("RELATION_TYPE_CODE");
            }
            else
            {
                return false;
            }
        }

        if ("1".equals(roleCodeB) && "78".equals(relationTypeCode))
        {
            relationInfos = RelaUUInfoQry.getUserUU(userId, "2", relationTypeCode);
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                String serialNumberB = relationInfos.getData(0).getString("SERIAL_NUMBER_B");
                if ("625".equals(tradeTypeCode) || "624".equals(tradeTypeCode) || "635".equals(tradeTypeCode) || "605".equals(tradeTypeCode)|| "1605".equals(tradeTypeCode))
                {
                    errorInfo = "该用户的子账号[" + serialNumberB + "]没有拆机,不能办理拆机业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604014", errorInfo);
                }
                else if ("622".equals(tradeTypeCode) || "623".equals(tradeTypeCode) || "606".equals(tradeTypeCode) || "636".equals(tradeTypeCode))
                {
                    errorInfo = "该用户的子账号[" + serialNumberB + "]没有移机,不能办理移机业务!";
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604015", errorInfo);
                }

            }

        }
        return false;
    }

}
