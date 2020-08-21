
package com.asiainfo.veris.crm.order.soa.script.rule.checkafter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgInfoQry;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @Description: 此购机业务不可以取消!【TradeCheckAfter】
 * @author: xiaocl
 */
public class CheckCancelTheUpgredRule extends BreBase implements IBREScript
{
    private static Logger logger = Logger.getLogger(CheckCancelTheUpgredRule.class);

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 CheckCancelTheUpgredRule() >>>>>>>>>>>>>>>>>>");

        /* 自定义区域 */
        boolean bResult = false;
        String strEparchyCode = databus.getString("EPARCHY_CODE");
        String strPackageId = databus.getString("RSRV_STR2");
        IDataset listPacakgeExt = new DatasetList();
        try
        {
            listPacakgeExt = PkgInfoQry.getPackageByPackage(strPackageId, strEparchyCode);
        }
        catch (Exception e)
        {
            StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("获取营销包参数出错！");
            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201157", strError.toString());
        }
        if (IDataUtil.isNotEmpty(listPacakgeExt))
        {
            if (listPacakgeExt.getData(0).getString("CANCEL_TAG").equals("4"))
            {
                StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("此购机业务不可以取消!");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201158", strError.toString());
            }
            else if (IDataUtil.isEmpty(listPacakgeExt))
            {
                StringBuilder strError = new StringBuilder("业务登记后条件判断:").append("获取购机业务参数无记录!");
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "201159", strError.toString());
            }
        }

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 CheckCancelTheUpgredRule() " + bResult + "<<<<<<<<<<<<<<<<<<<");

        return bResult;
    }

}
