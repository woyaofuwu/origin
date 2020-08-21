
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.ModifySaleActiveIMEIException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.requestdata.ModifySaleActiveIMEIReqData;

public class ModifySaleActiveIMEITrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        ModifySaleActiveIMEIReqData msaird = (ModifySaleActiveIMEIReqData) bd.getRD();
        UcaData uca = msaird.getUca();
        SaleGoodsTradeData newGoodsTd = new SaleGoodsTradeData();
        List<SaleGoodsTradeData> list = uca.getUserSaleGoods();
        int tag = 0;
        for (int i = 0, size = list.size(); i < size; i++)
        {
            SaleGoodsTradeData goodsTd = list.get(i);
            if (msaird.getOldIMEI().equals(goodsTd.getResCode()) && msaird.getRelationTradeId().equals(goodsTd.getRelationTradeId()))
            {
                SaleGoodsTradeData newGoods = goodsTd.clone();
                newGoods.setResCode(msaird.getNewIMEI());
                newGoods.setRsrvStr5(msaird.getCheckTradeId()); /*REQ201712040014销户业务限制查询新增*/
                newGoods.setRemark(msaird.getRemark());
                newGoods.setModifyTag(BofConst.MODIFY_TAG_UPD);
                bd.add(uca.getSerialNumber(), newGoods);
                tag = 1;
            }
        }
        if (tag == 0)
        {
            CSAppException.apperr(ModifySaleActiveIMEIException.CRM_MODIFYSALEACTIVEIMEI_2);
        }

        // 修改主台帐
        MainTradeData mtd = bd.getMainTradeData();
        mtd.setRsrvStr1(msaird.getNewIMEI());
        mtd.setRsrvStr2(msaird.getOldIMEI());
        mtd.setRsrvStr3(msaird.getRelationTradeId());
        mtd.setRemark("购机活动修改IMEI号");

    }

}
