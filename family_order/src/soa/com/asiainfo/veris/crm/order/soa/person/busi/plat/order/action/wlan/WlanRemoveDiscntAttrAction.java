
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wlan;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * Wlan套餐变更 或者套餐订购，更换了套餐级别时， 将原来的级别的套餐属性移除
 * 
 * @author xiekl
 */
public class WlanRemoveDiscntAttrAction implements IProductModuleAction
{

    @Override
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        String serialNumber = uca.getSerialNumber();
        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        if (PlatConstants.OPER_ORDER_TC.equals(pstd.getOperCode()) || PlatConstants.OPER_CHANGE_TC.equals(pstd.getOperCode()))
        {
            // 如果以前是标准套餐，现在订购套餐，立即生效
            List<DiscntTradeData> discntList = uca.getUserDiscntsByDiscntCodeArray("89016,89017,89018,89019,89020");
            if (discntList == null || discntList.isEmpty())
            {
                pstd.setRsrvStr1("EFFECT_NOW");
            }

            boolean hasChangedSelType = false;
            String selType = null;
            List<AttrTradeData> attrTradeList = pstd.getAttrTradeDatas();
            for (int i = 0; i < attrTradeList.size(); i++)
            {
                AttrTradeData attrTrade = attrTradeList.get(i);
                // 如果有SEL_TYPE属性，则肯定修改了SEL_TYPE属性
                if (attrTrade != null && "SEL_TYPE".equals(attrTrade.getAttrCode()) && BofConst.MODIFY_TAG_ADD.equals(attrTrade.getModifyTag()))
                {
                    hasChangedSelType = true;
                    selType = attrTrade.getAttrValue();
                }
            }

            if (hasChangedSelType)
            {
                // 当前选中包月时长套餐 401,401_2删除
                if ("1".equals(selType))
                {
                    AttrTradeData flowDiscntAttr = uca.getUserAttrsByRelaInstIdAttrCode(pstd.getInstId(), "401_2");

                    if (flowDiscntAttr != null)
                    {
                        flowDiscntAttr.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        flowDiscntAttr.setEndDate(btd.getRD().getAcceptTime());
                        btd.add(serialNumber, flowDiscntAttr);
                    }

                }
                else
                {
                    // 当前选中包月流量套餐401_2,时长套餐删除
                    AttrTradeData timeDiscntAttr = uca.getUserAttrsByRelaInstIdAttrCode(pstd.getInstId(), "401");

                    if (timeDiscntAttr != null)
                    {
                        timeDiscntAttr.setModifyTag(BofConst.MODIFY_TAG_DEL);
                        timeDiscntAttr.setEndDate(btd.getRD().getAcceptTime());
                        btd.add(serialNumber, timeDiscntAttr);
                    }

                }
            }
        }
    }

}
