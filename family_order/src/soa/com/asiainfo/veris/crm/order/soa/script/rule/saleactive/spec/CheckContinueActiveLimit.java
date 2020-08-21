
package com.asiainfo.veris.crm.order.soa.script.rule.saleactive.spec;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 2.3.0 param_attr = 968 >> 可顺延的活动的期限限制 与所有话费返还/约定消费期限在三个月（含第三个月）内兼容， 与所有话费返还/约定消费期限超过三个月（不含第三个月）以上互斥。（月份支持可配置）
 * TD_BRE_PARAMETER 入参，限制月份 LIMIT_MONTH
 * 
 * @author Mr.Z
 */
public class CheckContinueActiveLimit extends BreBase implements IBREScript
{

    private static final long serialVersionUID = 3394554042220308455L;

    private static Logger logger = Logger.getLogger(CheckContinueActiveLimit.class);

    @Override
    public boolean run(IData databus, BreRuleParam ruleparam) throws Exception
    {
        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step in CheckContinueActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        List<String> exceptProductIds = new ArrayList<String>();
        List<String> exceptPackageIds = new ArrayList<String>();

        IDataset noBackConfigs = CommparaInfoQry.getCommparaByParaAttr("CSM", "155", databus.getString("PACKAGE_ID"));
        SaleActiveUtil.getCommparaProductIdAndPackageId(exceptProductIds, exceptPackageIds, noBackConfigs);

        UcaData uca = UcaDataFactory.getNormalUca(databus.getString("SERIAL_NUMBER"));
        List<SaleActiveTradeData> userBookAndBackSaleActives = uca.getUserSaleActiveExceptProductAndPackage(exceptProductIds, exceptPackageIds);
        userBookAndBackSaleActives = SaleActiveUtil.filterUserSaleActivesByQyyx(userBookAndBackSaleActives);

        if (CollectionUtils.isEmpty(userBookAndBackSaleActives))
            return true;

        String maxEndDate = SaleActiveUtil.getMaxEndDateFromUserSaleActive(userBookAndBackSaleActives);
        String limitMoth = ruleparam.getString(databus, "LIMIT_MONTH");
        int monthInterval = SysDateMgr.monthInterval(SysDateMgr.getSysTime(), maxEndDate);
        if (monthInterval > Integer.parseInt(limitMoth))
        {
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20114, "用户已办理的活动不在可顺延期限内，不能办理本活动！");
            return false;
        }

        if (logger.isDebugEnabled())
        {
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> step out CheckContinueActiveLimit() >>>>>>>>>>>>>>>>>>");
        }

        return false;
    }
}
