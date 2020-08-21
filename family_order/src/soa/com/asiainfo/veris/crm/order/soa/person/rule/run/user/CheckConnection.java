
package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.userconn.UserConnQry;

public class CheckConnection extends BreBase implements IBREScript
{
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {
        String userId = databus.getString("USER_ID");
        String type = "BK";
        String serNum = databus.getString("SERIAL_NUMBER");
        IDataset dataset = UserConnQry.getConnByUserIdAndType(userId, type); // 查询用户宽带固话共线信息

        if (dataset != null && !dataset.isEmpty())
        {
            String msg = "固话【" + serNum + "】与宽带共线，是否确认要移机？继续将解除共线关系";
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, -1, msg.toString());
        }
        return false;

    }
}
