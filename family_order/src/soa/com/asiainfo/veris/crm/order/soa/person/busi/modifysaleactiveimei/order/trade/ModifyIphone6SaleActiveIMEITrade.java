
package com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.trade;

import java.util.List;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.modifysaleactiveimei.order.requestdata.ModifySaleActiveIMEIReqData;

public class ModifyIphone6SaleActiveIMEITrade extends BaseTrade implements ITrade{
	@Override
    @SuppressWarnings("unchecked")
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ModifySaleActiveIMEIReqData msaird = (ModifySaleActiveIMEIReqData) bd.getRD();
        UcaData uca = msaird.getUca();
        List<SaleActiveTradeData> list = uca.getUserSaleActives();
        int tag = 0;
        for (int i = 0, size = list.size(); i < size; i++)
        {
        	SaleActiveTradeData saleActiveTradeData = list.get(i);
            if (msaird.getOldIMEI().equals(saleActiveTradeData.getRsrvStr22()) && msaird.getRelationTradeId().equals(saleActiveTradeData.getRelationTradeId())){
            	SaleActiveTradeData newSaleActiveTradeData = saleActiveTradeData.clone();
            	newSaleActiveTradeData.setRsrvStr22(msaird.getNewIMEI());
            	newSaleActiveTradeData.setRemark(msaird.getRemark());
            	newSaleActiveTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
                bd.add(uca.getSerialNumber(), newSaleActiveTradeData);
                tag = 1;
                break;
            }
        }
        if (tag == 0) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户无对应的IPHONE6活动数据，更新失败！");
		}
        // 修改主台帐
        MainTradeData mtd = bd.getMainTradeData();
        mtd.setRsrvStr3(msaird.getRelationTradeId());
        mtd.setRemark("IPHONE6营销活动华为修改IMEI号");
    }
}