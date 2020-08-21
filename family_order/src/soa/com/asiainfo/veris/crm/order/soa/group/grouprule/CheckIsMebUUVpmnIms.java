
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class CheckIsMebUUVpmnIms extends BreBase implements IBREScript
{

    /**
     * 该客户已是VPMN成员，一个成员只能加入一个V网，不能再次加入！
     */
    private static final long serialVersionUID = 1L;

    private static Logger logger = Logger.getLogger(CheckIsMebUUVpmnIms.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBCheckBlackList() >>>>>>>>>>>>>>>>>>");

        // 当前用户标识
        String strUserIdB = databus.getString("USER_ID_B", "");
        String errCode = databus.getString("RULE_BIZ_ID");
        IData param = new DataMap();

        // 查用户关系表，判断是否有uu关系
        param.put("USER_ID_B", strUserIdB);

        IDataset idsUU = RelaUUInfoQry.getUserRelationVpmnByUserIdB(strUserIdB);

        if (IDataUtil.isEmpty(idsUU)) // 不是vpmn成员
        {
            return true;
        }

        // 查询已加入过的VPMN集团客户
        String strUserIda = idsUU.getData(0).getString("USER_ID_A", "");
        IDataset idsCustGroup = GrpInfoQry.queryUserGroupInfos(strUserIda, "0");

        if (IDataUtil.isEmpty(idsCustGroup))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "判断VPMN成员时，根据集团用户标识查询集团客户信息不存在！USER_ID=" + strUserIda);
            return false;
        }

        IData idGroup = idsCustGroup.getData(0);
        // 得到集团客户编码、名称
        String strGroupId = idGroup.getString("GROUP_ID");
        String strGroupName = idGroup.getString("CUST_NAME");

        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, errCode, "业务受限提示：该客户已是【" + strGroupId + strGroupName + "】的VPMN成员。一个成员只能加入一个V网，不能再次加入！");

        return false;
    }

}
