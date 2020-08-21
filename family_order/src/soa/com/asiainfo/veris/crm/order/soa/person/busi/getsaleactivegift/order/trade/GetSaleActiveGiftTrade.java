
package com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.GetSaleActiveGiftException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.GetSaleActiveGiftBean;
import com.asiainfo.veris.crm.order.soa.person.busi.getsaleactivegift.order.requestdata.GetSaleActiveGiftReqData;

public class GetSaleActiveGiftTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        dealMainTrade(bd);
        createGoodsTradeData(bd);
    }

    private void createGoodsTradeData(BusiTradeData bd) throws Exception
    {
        GetSaleActiveGiftReqData gsagrd = (GetSaleActiveGiftReqData) bd.getRD();
        GetSaleActiveGiftBean bean = BeanManager.createBean(GetSaleActiveGiftBean.class);
        String resCode = gsagrd.getResCode();
        String relationTradeId = gsagrd.getRelationTradeId();
        String userId = gsagrd.getUca().getUserId();
        if ("".equals(resCode) || "".equals(relationTradeId))
        {
            CSAppException.apperr(GetSaleActiveGiftException.CRM_GETSALEACTIVEGIFT_4);
        }
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", userId);
        IDataset giftInfos = bean.getGiftInfos(userInfo);
        if (giftInfos == null || giftInfos.size() < 1)
        {
            CSAppException.apperr(GetSaleActiveGiftException.CRM_GETSALEACTIVEGIFT_1);
        }
        IData selectedGift = new DataMap();
        for (int i = 0, size = giftInfos.size(); i < size; i++)
        {
            IData giftInfo = giftInfos.getData(i);
            if (resCode.equals(giftInfo.getString("PARA_CODE2")) && relationTradeId.equals(giftInfo.getString("RELATION_TRADE_ID")))
            {
                selectedGift = giftInfo;
            }
        }
        if (selectedGift == null)
        {
            CSAppException.apperr(GetSaleActiveGiftException.CRM_GETSALEACTIVEGIFT_2);
        }

        SaleGoodsTradeData giftTradeDate = new SaleGoodsTradeData();
        giftTradeDate.setUserId(userId);
        giftTradeDate.setResCode(resCode);
        giftTradeDate.setRelationTradeId(relationTradeId);
        giftTradeDate.setProductId(selectedGift.getString("PRODUCT_ID"));
        giftTradeDate.setPackageId(selectedGift.getString("PACKAGE_ID"));
        giftTradeDate.setInstId(SeqMgr.getInstId());
        giftTradeDate.setElementId(resCode);
        giftTradeDate.setGoodsName(selectedGift.getString("PARA_CODE3"));
        giftTradeDate.setGoodsNum("1");
        giftTradeDate.setGoodsState("0");
        giftTradeDate.setRemark("预存话费送礼品领取礼品信息");
        giftTradeDate.setRsrvStr1(gsagrd.getTradeId());
        giftTradeDate.setRsrvStr2("Y");
        giftTradeDate.setRsrvDate1(SysDateMgr.getSysTime());
        giftTradeDate.setRsrvDate2(selectedGift.getString("END_DATE"));
        giftTradeDate.setModifyTag(BofConst.MODIFY_TAG_ADD);
        bd.add(gsagrd.getUca().getSerialNumber(), giftTradeDate);
    }

    private void dealMainTrade(BusiTradeData bd) throws Exception
    {
        MainTradeData mtd = bd.getMainTradeData();
        GetSaleActiveGiftReqData gsagrd = (GetSaleActiveGiftReqData) bd.getRD();
        mtd.setRsrvStr1(gsagrd.getRelationTradeId());
    }
}
