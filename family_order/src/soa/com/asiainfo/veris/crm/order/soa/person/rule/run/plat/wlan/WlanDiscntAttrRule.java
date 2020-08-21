
package com.asiainfo.veris.crm.order.soa.person.rule.run.plat.wlan;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;

public class WlanDiscntAttrRule extends BreBase implements IBREScript
{

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        // TODO Auto-generated method stub
        UcaData uca = (UcaData) databus.get(PlatConstants.RULE_UCA);
        PlatSvcData psd = (PlatSvcData) databus.get(PlatConstants.RULE_PLATSVC);

        List<AttrData> attrDatas = psd.getAttrs();
        String instId = "";
        List<PlatSvcTradeData> pstds = uca.getUserPlatSvcByServiceId(psd.getElementId());
        PlatSvcTradeData pstd = null;
        if (pstds != null && pstds.size() > 0)
        {
            pstd = pstds.get(0);
            instId = pstd.getInstId();
        }
        if (PlatConstants.OPER_CANCEL_TC.equals(psd.getOperCode()))
        {
            int size = attrDatas.size();
            for (int i = 0; i < size; i++)
            {
                AttrData attr = attrDatas.get(i);
                if ("401_3".equals(attr.getAttrCode()))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_1.toString(), PlatException.CRM_PLAT_0975_1.getValue());
                    return false;
                }
                else if (attr.getAttrCode().indexOf("401") >= 0)
                {
                    AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(instId, attr.getAttrCode());
                    if (attrTradeData == null)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0913_2.toString(), PlatException.CRM_PLAT_0913_2.getValue());
                        return false;
                    }
                    else if (attrTradeData.getAttrValue() != null && !attrTradeData.getAttrValue().equals(attr.getAttrValue()) && !"00000".equals(attr.getAttrValue()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_3.toString(), PlatException.CRM_PLAT_0975_3.getValue());
                        return false;
                    }
                }
            }
        }
        else if (PlatConstants.OPER_ORDER_TC.equals(psd.getOperCode()))
        {
            int size = attrDatas.size();
            for (int i = 0; i < size; i++)
            {
                AttrData attr = attrDatas.get(i);
                if (attr.getAttrCode().indexOf("401") >= 0)
                {
                    AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(instId, attr.getAttrCode());

//                    if ("401_3".equals(attr.getAttrCode()) && "00014".equals(attr.getAttrValue()))
//                    {
//                        if (attrTradeData == null)
//                        {
//                            AttrTradeData attrTradeDataMonth = uca.getUserAttrsByRelaInstIdAttrCode(instId, "401_2");
//                            if (attrTradeDataMonth == null)
//                            {
//                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_4.toString(), PlatException.CRM_PLAT_0975_4.getValue());
//                                return false;
//                            }
//                            else if (!"OVER_FLAG".equals(attrTradeDataMonth.getRsrvStr1()))
//                            {
//                                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_5.toString(), PlatException.CRM_PLAT_0975_5.getValue());
//                                return false;
//                            }
//                        }
//                        else if (!"OVER_FLAG".equals(attrTradeData.getRsrvStr1()))
//                        {
//                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_6.toString(), PlatException.CRM_PLAT_0975_6.getValue());
//                            return false;
//                        }
//                    }else
                     if (attrTradeData != null && attrTradeData.getAttrValue() != null && attrTradeData.getAttrValue().equals(attr.getAttrValue()))
                    {// 需要判断attrTradeData.getAttrValue()是否为null，老数据存在这种情况
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_7.toString(), PlatException.CRM_PLAT_0975_7.getValue());
                        return false;
                    }
                }
            }

            // 当前状态为暂停，不能办理套餐变更操作
            if (pstd == null || PlatConstants.STATE_PAUSE.equals(pstd.getBizStateCode()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0986.toString(), PlatException.CRM_PLAT_0986.getValue());
                return false;

            }
        }
        else if (PlatConstants.OPER_CHANGE_TC.equals(psd.getOperCode()))
        {
            int size = attrDatas.size();
            for (int i = 0; i < size; i++)
            {
                AttrData attr = attrDatas.get(i);
                if ("401_3".equals(attr.getAttrCode()))
                {
                    BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_8.toString(), PlatException.CRM_PLAT_0975_8.getValue());
                    return false;
                }
                else if (attr.getAttrCode().indexOf("401") >= 0)
                {
                    AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(instId, attr.getAttrCode());
                    if (attrTradeData == null || "00000".equals(attrTradeData.getAttrValue()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0913_2.toString(), PlatException.CRM_PLAT_0913_2.getValue());
                        return false;
                    }
                    else if (attrTradeData.getAttrValue() != null && attrTradeData.getAttrValue().equals(attr.getAttrValue()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_7.toString(), PlatException.CRM_PLAT_0975_7.getValue());
                        return false;
                    }
                }
            }

            // 当前状态为暂停，不能办理套餐变更操作
            if (pstd == null || PlatConstants.STATE_PAUSE.equals(pstd.getBizStateCode()))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0986.toString(), PlatException.CRM_PLAT_0986.getValue());
                return false;

            }
        }
        else if (PlatConstants.OPER_TC_USEROUT_NOTICE.equals(psd.getOperCode()))
        {

            int size = attrDatas.size();
            for (int i = 0; i < size; i++)
            {
                AttrData attr = attrDatas.get(i);
                if (attr.getAttrCode().indexOf("401") >= 0)
                {
                    AttrTradeData attrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(instId, attr.getAttrCode());
                    if (attrTradeData == null)
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0913_2.toString(), PlatException.CRM_PLAT_0913_2.getValue());
                        return false;
                    }
                    else if (!attr.getAttrValue().equals(attrTradeData.getAttrValue()))
                    {
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, PlatException.CRM_PLAT_0975_3.toString(), PlatException.CRM_PLAT_0975_3.getValue());
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
