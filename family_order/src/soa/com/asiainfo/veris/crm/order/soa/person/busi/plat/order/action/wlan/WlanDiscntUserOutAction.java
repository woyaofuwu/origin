
package com.asiainfo.veris.crm.order.soa.person.busi.plat.order.action.wlan;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.AttrData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.PlatSvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;

/**
 * 套餐用尽通知处理
 * 
 * @author xiekl
 */
public class WlanDiscntUserOutAction implements IProductModuleAction
{

    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {

        PlatSvcTradeData pstd = (PlatSvcTradeData) dealPmtd;
        if (pstd.getOperCode().equals(PlatConstants.OPER_TC_USEROUT_NOTICE))
        {
            PlatSvcData pmd = (PlatSvcData) pstd.getPmd();
            if (pmd != null)
            {
                List<AttrData> attrDatas = pmd.getAttrs();
                if (attrDatas != null && !attrDatas.isEmpty())
                {
                    for (AttrData attrData : attrDatas)
                    {
                        AttrTradeData userAttrTradeData = uca.getUserAttrsByRelaInstIdAttrCode(pstd.getInstId(), attrData.getAttrCode());
                        if (userAttrTradeData != null)
                        {
                            AttrTradeData updAttrTradeData = userAttrTradeData.clone();
                            updAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                            updAttrTradeData.setRsrvStr1("OVER_FLAG");
                            updAttrTradeData.setEndDate(SysDateMgr.getSysTime());
                            btd.add(uca.getSerialNumber(), updAttrTradeData);
                        }
                    }
                }
            }
        }
        else if (pstd.getOperCode().equals(PlatConstants.OPER_ORDER_TC))
        {
            List<AttrTradeData> attrList = pstd.getAttrTradeDatas();
            if (attrList != null && !attrList.isEmpty())
            {
                for (int i = 0; i < attrList.size(); i++)
                {
                    AttrTradeData attr = attrList.get(i);
                    if (BofConst.MODIFY_TAG_ADD.equals(attr.getModifyTag()) && "401_3".equals(attr.getAttrCode()))
                    {
                        attr.setEndDate(SysDateMgr.getLastDateThisMonth());
                    }
                }
            }
            else
            {
                PlatSvcData pmd = (PlatSvcData) pstd.getPmd();
                if (pmd != null)
                {
                    List<AttrData> attrDatas = pmd.getAttrs();
                    if (attrDatas != null && !attrDatas.isEmpty())
                    {
                        for (AttrData attrData : attrDatas)
                        {
                            if ("401_3".equals(attrData.getAttrCode()))
                            {
                                AttrTradeData addAttrTradeData = new AttrTradeData();
                                addAttrTradeData.setAttrCode(attrData.getAttrCode());
                                addAttrTradeData.setAttrValue(attrData.getAttrValue());
                                addAttrTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                                addAttrTradeData.setUserId(pstd.getUserId());
                                addAttrTradeData.setEndDate(SysDateMgr.getLastDateThisMonth());
                                addAttrTradeData.setStartDate(btd.getRD().getAcceptTime());
                                addAttrTradeData.setRelaInstId(pstd.getInstId());
                                addAttrTradeData.setInstType(pmd.getElementType());
                                addAttrTradeData.setInstId(SeqMgr.getInstId());
                                addAttrTradeData.setElementId(pstd.getElementId());
                                pstd.addAttrTradeDatas(addAttrTradeData);
                                btd.add(uca.getSerialNumber(), addAttrTradeData);
                            }
                        }
                    }
                }
            }
        }
    }

}
