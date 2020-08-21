
package com.asiainfo.veris.crm.order.soa.person.rule.run.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

/**
 * 校验宽带用户是否已经办理了营销活动(宽带userId是否对应有营销活动记录？规则先不配)
 * 
 * @author chenzm
 * @date 2014-05-29
 */
public class CheckExsitsSaleActiveInfo extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 1L;

    /**
     * 校验宽带用户是否已经办理了营销活动
     */
    public boolean run(IData databus, BreRuleParam paramData) throws Exception
    {

        String userId = databus.getString("USER_ID");
        IDataset saleActiveInfos = SaleActiveInfoQry.getUserSaleActiveInfo(userId);
        if (IDataUtil.isNotEmpty(saleActiveInfos))
        {
            return true;
        }
        return false;
    }

}
