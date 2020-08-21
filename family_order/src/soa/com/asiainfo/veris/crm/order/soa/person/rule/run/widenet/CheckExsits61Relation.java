
package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

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
 * 校验是否已经加入了多人约消组网关系
 * 
 * @author zhangxing3
 * @date 2020-01-10
 */
public class CheckExsits61Relation extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 是否已经加入了多人约消组网关系判断
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String errorInfo = "";
        String userId = databus.getString("USER_ID","");
        IDataset userRelationInfos = RelaUUInfoQry.getRelationsByUserIdAndTypeAndRoleCodeB("61",userId,"2");
        if (IDataUtil.isNotEmpty(userRelationInfos))
        {// 已办理宽带
        	errorInfo = "当前号码已参加多人约消，需要解除才能再办理宽带开户！";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604007", errorInfo);
        }
        return false;
    }

}
