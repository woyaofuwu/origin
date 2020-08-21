
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
 * 子账号移机提示
 * 
 * @author chenzm
 * @date 2014-07-24
 */
public class CheckSubAccount extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String userId = databus.getString("USER_ID");
        String roleCodeB = "";
        String errorInfo = "";
        // 平行账号查询
        IDataset relationInfos = RelaUUInfoQry.isMasterAccount(userId, "77");
        if (IDataUtil.isNotEmpty(relationInfos))
        {
            roleCodeB = relationInfos.getData(0).getString("ROLE_CODE_B");
            if ("1".equals(roleCodeB))
            {
                errorInfo = "该用户为平行主账号，办理该业务后成为普通账号。(如需办理子账号移机，请到GPON子账号移机页面处理。)";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604016", errorInfo);
            }
            else
            {
                errorInfo = "该用户为平行子账号，办理该业务后成为普通账号。(如需办理子账号移机，请到GPON子账号移机页面处理。)";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604017", errorInfo);
            }

        }
        else
        {
            // 家庭账号查询
            relationInfos = RelaUUInfoQry.isMasterAccount(userId, "78");
            if (IDataUtil.isNotEmpty(relationInfos))
            {
                errorInfo = "该用户为家庭子账号，办理该业务后成为普通账号。(如需办理子账号移机，请到GPON子账号移机页面处理。)";
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604018", errorInfo);
            }
        }

        return false;
    }

}
