
package com.asiainfo.veris.crm.order.soa.person.rule.run.productchange;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * 是否存在预约宽带产品
 */
public class CheckWidenetBookingProduct extends BreBase implements IBREScript
{

    /**
     * @Description: 是否存在预约产品
     * @param userId
     * @return
     * @throws Exception
     * @author: chenzm
     */
    public boolean isExistsBookingChangeProduct(String userId) throws Exception
    {
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(userId);

        String sysDate = SysDateMgr.getSysTime();

        if (IDataUtil.isNotEmpty(userMainProducts))
        {
            int size = userMainProducts.size();

            for (int i = 0; i < size; i++)
            {
                IData userProduct = userMainProducts.getData(i);

                if (userProduct.getString("START_DATE").compareTo(sysDate) >= 0)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean run(IData databus, BreRuleParam param) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG");
        String userId = databus.getString("USER_ID");
        if (StringUtils.isBlank(xChoiceTag) || StringUtils.equals("0", xChoiceTag))// 查询时校验，依赖请求数据
        {

            if (this.isExistsBookingChangeProduct(userId))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "604024", "用户存在预约产品,预约产品未生效之前不能再次变更产品!");

            }
        }
        return false;
    }
}
