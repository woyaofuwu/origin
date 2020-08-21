
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.cmmb;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.PlatReload;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CmmbOperRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);

        List<PlatSvcTradeData> userTempCmmbs = new ArrayList<PlatSvcTradeData>();
        IDataset cmmbConfigs = CommparaInfoQry.queryCmmbConfig();
        int size = cmmbConfigs.size();
        IData timeConfig = new DataMap();
        for (int i = 0; i < size; i++)
        {
            IData config = cmmbConfigs.getData(i);
            String serviceId = config.getString("PARA_CODE1");
            userTempCmmbs.addAll(uca.getUserPlatSvcByServiceId(serviceId));
            timeConfig.put(serviceId, config);
        }
        userTempCmmbs.addAll(uca.getUserPlatSvcByServiceId(PlatReload.cmmbStandard));
        size = userTempCmmbs.size();
        boolean isFind = false;
        String thisMonthEnd = SysDateMgr.getLastDateThisMonth();
        for (int i = 0; i < size; i++)
        {
            PlatSvcTradeData userPlatSvc = userTempCmmbs.get(i);
            if (psd.getElementId().equals(userPlatSvc.getElementId()))
            {
                isFind = true;
                if (BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag()))
                {
                    if (userPlatSvc.getEndDate().compareTo(thisMonthEnd) > 0)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_6.toString(), PlatException.CRM_PLAT_0912_6.getValue());
                        return false;
                    }
                }
                else if (BofConst.MODIFY_TAG_DEL.equals(psd.getModifyTag()))
                {

                    if (userPlatSvc.getEndDate().compareTo(thisMonthEnd) > 0 && !psd.getElementId().equals(PlatReload.cmmbStandard))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_8.toString(), PlatException.CRM_PLAT_0912_8.getValue());
                        return false;
                    }
                }
                if (PlatConstants.STATE_PAUSE.equals(userPlatSvc.getBizStateCode()) && !(PlatConstants.OPER_RESTORE.equals(psd.getOperCode()) || PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode())))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_5.toString(), PlatException.CRM_PLAT_0912_5.getValue());
                    return false;
                }
                else if (PlatConstants.STATE_OK.equals(userPlatSvc.getBizStateCode()) && PlatConstants.OPER_RESTORE.equals(psd.getOperCode()))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_6.toString(), PlatException.CRM_PLAT_0912_6.getValue());
                    return false;
                }
            }
            else
            {
                if (userPlatSvc.getEndDate().compareTo(thisMonthEnd) > 0 && userPlatSvc.getStartDate().compareTo(thisMonthEnd) < 0 && !userPlatSvc.getElementId().equals(PlatReload.cmmbStandard))
                {
                    if (PlatConstants.OPER_ORDER.equals(psd.getOperCode()) || PlatConstants.OPER_CANCEL_ORDER.equals(psd.getOperCode()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0912_8.toString(), PlatException.CRM_PLAT_0912_8.getValue());
                        return false;
                    }
                }
            }
        }
        if (!isFind)
        {
            if (!BofConst.MODIFY_TAG_ADD.equals(psd.getModifyTag()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0913.toString(), PlatException.CRM_PLAT_0913.getValue());
                return false;
            }
        }
        return true;
    }

}
