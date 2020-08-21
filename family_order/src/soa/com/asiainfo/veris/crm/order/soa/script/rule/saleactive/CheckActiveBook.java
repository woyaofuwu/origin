
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.script.rule.saleactive.msg.SaleActiveBreConst;

/**
 * 仅仅只用于前台选包、输入赠送人号码的校验
 * 
 * @author Mr.Z
 */
public class CheckActiveBook extends BreBase implements IBREScript
{
    private static final long serialVersionUID = 2987559940602315670L;

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String activeCheckMode = databus.getString("ACTIVE_CHECK_MODE");

        if (!SaleActiveBreConst.ACTIVE_CHECK_MODE_SEL_PKG.equals(activeCheckMode))
            return true;

        String campnType = databus.getString("CAMPN_TYPE");
        String productId = databus.getString("PRODUCT_ID");
        String packageId = databus.getString("PACKAGE_ID");
        String eparchyCode = databus.getString("EPARCHY_CODE");
        String actionType = databus.getString("ACTION_TYPE");

//        IDataset result = PkgExtInfoQry.queryPackageExtInfo(packageId, eparchyCode);
//        IData saleactive = result.getData(0);
        
        IData saleactive = databus.getData("PM_OFFER_EXT");//UPackageExtInfoQry.qryPkgExtEnableByPackageId(packageId);
        saleactive.put("CAMPN_TYPE", campnType);
        saleactive.put("PRODUCT_ID", productId);
        saleactive.put("SERIAL_NUMBER", databus.getString("SERIAL_NUMBER"));
        saleactive.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);

        IDataset activeDates = CSAppCall.call("CS.SaleActiveDateSVC.callActiveStartEndDate", saleactive);
        IData activeDate = activeDates.getData(0);

        if (StringUtils.isNotBlank(activeDate.getString("BOOK_DATE")))
        {
            if (SaleActiveBreConst.ACTION_TYPE_CHK_ACTIVE_TRADE.equals(actionType))
            {
                StringBuilder tips = new StringBuilder("已存在有效的营销活动，此营销包只能顺延办理！活动顺延时间为:[");

                tips.append(activeDate.getString("BOOK_DATE"));

                tips.append("]；约定在网顺延时间为:[").append(activeDate.getString("ONNET_START_DATE"));

                tips.append("]。是否继续办理?");

                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_CHIOCE, 14061301, tips.toString());
            }

            if (SaleActiveBreConst.ACTION_TYPE_CHK_DEPOSIT_GIFT_USER.equals(actionType))
            {
                StringBuilder tips = new StringBuilder("被赠送人存在有效的营销活动，此赠送只能顺延开始，开始时间为：").append(activeDate.getString("BOOK_DATE"));
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_TIPS, 14061401, tips.toString());
            }
        }

        if (SaleActiveBreConst.ACTION_TYPE_CHK_DEPOSIT_GIFT_USER.equals(actionType))
        {
            databus.put("GIFT_START_DATE", activeDate.getString("START_DATE"));
            databus.put("GIFT_END_DATE", activeDate.getString("END_DATE"));
        }

        return true;
    }

}
