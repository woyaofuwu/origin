
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

/**
 * 判断该工号是否有优惠权限
 * 
 * @author lucifer
 */
public class ValidationPriceData extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        boolean result = true;
        String staffId = CSBizBean.getVisit().getStaffId();
        String productId = databus.getString("PRODUCT_ID");
        String epachyCode = databus.getString("EPARCHY_CODE");

        boolean res = StaffPrivUtil.isFuncDataPriv(staffId, "SYS555");
        if (!res)
        {
            IData priceParam = new DataMap();
            priceParam.put("SUBSYS_CODE", "CSM");
            priceParam.put("PARAM_ATTR", "555");
            priceParam.put("PARAM_CODE", productId);
            priceParam.put("EPARCHY_CODE", epachyCode);

            IDataset priceDataInfo = CSAppCall.call("SS.BookTradeSVC.getPriceDataByProductId", priceParam);

            if (null == priceDataInfo || priceDataInfo.size() == 0)
            {

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "308001", "您没有该产品的优惠权限，无法继续办理！");
                result = false;
            }
        }
        return result;
    }

}
