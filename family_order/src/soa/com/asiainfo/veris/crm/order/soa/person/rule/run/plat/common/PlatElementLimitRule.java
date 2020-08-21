
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.common;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.plat.PlatOfficeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;

public class PlatElementLimitRule extends BreBase implements IBREScript
{

    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcTradeData pstd = (PlatSvcTradeData) databus.get(PlatConstants.RULE_PLATSVC_TRADE);

        PlatOfficeData officeData = ((PlatSvcData) pstd.getPmd()).getOfficeData();

        IDataset limitServices = PlatInfoQry.getPlatsvcLimit(officeData.getBizTypeCode(), pstd.getElementId(), pstd.getOperCode());
        int size = limitServices.size();
        for (int i = 0; i < size; i++)
        {
            IData limit = limitServices.getData(i);
            ProductModuleTradeData pmtd = null;
            String limitType = limit.getString("LIMIT_TYPE");
            String limitServiceId = limit.getString("SERVICE_ID_L");
            String serviceName = "";
            PlatOfficeData limitPlatOfficeData = null; // 依赖限制元素的局数据

            // 无依赖，互斥关系，继续执行
            if (!("0".equals(limitType) || "1".equals(limitType)))
            {
                continue;
            }

            if ("S".equals(limit.getString("SVC_TYPE")))
            {
                serviceName = USvcInfoQry.getSvcNameBySvcId(limitServiceId);
                List<SvcTradeData> pmtds = uca.getUserSvcBySvcId(limitServiceId);
                if (pmtds != null && pmtds.size() > 0)
                {
                    pmtd = pmtds.get(0);
                }
            }
            else if ("Z".equals(limit.get("SVC_TYPE")))
            {
                limitPlatOfficeData = PlatOfficeData.getInstance(limitServiceId);
                serviceName = limitPlatOfficeData.getServiceName();
                List<PlatSvcTradeData> pmtds = uca.getUserPlatSvcByServiceId(limitServiceId);
                if (pmtds != null && pmtds.size() > 0)
                {
                    pmtd = pmtds.get(0);
                }
            }

            if ("0".equals(limitType))
            {
                // 依赖
                if (pmtd == null || BofConst.MODIFY_TAG_DEL.equals(pmtd.getModifyTag()))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0914.toString(), PlatException.CRM_PLAT_0914.getValue() + "[" + limitServiceId + "]" + serviceName);
                    return false;
                }
            }
            else if ("1".equals(limitType))
            {
                if (pmtd != null && !BofConst.MODIFY_TAG_DEL.equals(pmtd.getModifyTag()) && !BofConst.MODIFY_TAG_DEL.equals(pstd.getModifyTag()))
                {
                    if ("3".equals(limitPlatOfficeData.getServType()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0983.toString(), "用户的" + serviceName + "已关闭，请先打开" + serviceName + "，本次操作失败");

                    }
                    else
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0984.toString(), PlatException.CRM_PLAT_0984.getValue() + "[" + limitServiceId + "]" + serviceName + "，本次操作失败");

                    }

                    return false;
                }
            }
        }
        return true;
    }

}
