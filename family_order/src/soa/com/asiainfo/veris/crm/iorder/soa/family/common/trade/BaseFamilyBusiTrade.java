
package com.asiainfo.veris.crm.iorder.soa.family.common.trade;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.data.requestdata.BaseFamilyBusiReqData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.PlatSvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;

import java.util.List;

/**
 * @Description 家庭业务受理公用登记类
 * @Auther: zhenggang
 * @Date: 2020/7/30 17:27
 * @version: V1.0
 */
public class BaseFamilyBusiTrade extends BaseTrade implements ITrade
{
    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        BaseFamilyBusiReqData reqData = (BaseFamilyBusiReqData) btd.getRD();

        // 公用登记处理角色的商品
        List<ProductModuleData> pmds = reqData.getFamilyRole().getRoleOfferDatas();
        if (!pmds.isEmpty())
        {
            ProductModuleCreator.createProductModuleTradeData(pmds, btd);
        }

        // 成员订购家庭子商品回填家庭用户ID
        if (StringUtils.isNotEmpty(reqData.getFamilyUserId()))
        {
            this.fillVirtualFieldsForOfferTrade(reqData, btd);
        }

    }

    private void fillVirtualFieldsForOfferTrade(BaseFamilyBusiReqData reqData, BusiTradeData btd) throws Exception
    {
        List<DiscntTradeData> dtds = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        this.setUserIdAForTradeDatas(dtds, reqData);

        List<SvcTradeData> stds = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        this.setUserIdAForTradeDatas(stds, reqData);

        List<PlatSvcTradeData> pstds = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        this.setUserIdAForTradeDatas(pstds, reqData);

    }

    public void setUserIdAForTradeDatas(List<? extends ProductModuleTradeData> pmtds, BaseFamilyBusiReqData reqData) throws Exception
    {
        if (pmtds != null && pmtds.size() > 0)
        {
            for (ProductModuleTradeData productModuleTradeData : pmtds)
            {
                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(productModuleTradeData.getElementType()) && BofConst.MODIFY_TAG_ADD.equals(productModuleTradeData.getModifyTag()))
                {
                    DiscntTradeData dtd = (DiscntTradeData) productModuleTradeData;
                    dtd.setUserIdA(reqData.getFamilyUserId());
                    dtd.setSpecTag(FamilyConstants.OFFER.FMY_OFFER_SPEC_TAG);
                    dtd.setRsrvStr1(reqData.getFamilyUserId());
                    dtd.setRsrvStr2(reqData.getFamilyRole().getRoleCode());
                    dtd.setRsrvStr3(reqData.getFamilyRole().getRoleInstId());
                    dtd.setRemark("FMY|" + reqData.getTradeType().getTradeTypeCode() + "|" + reqData.getFamilyRole().getRoleCode());
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(productModuleTradeData.getElementType()) && BofConst.MODIFY_TAG_ADD.equals(productModuleTradeData.getModifyTag()))
                {
                    SvcTradeData std = (SvcTradeData) productModuleTradeData;
                    std.setUserIdA(reqData.getFamilyUserId());
                    std.setRsrvStr1(reqData.getFamilyUserId());
                    std.setRsrvStr2(reqData.getFamilyRole().getRoleCode());
                    std.setRsrvStr3(reqData.getFamilyRole().getRoleInstId());
                    std.setRemark("FMY|" + reqData.getTradeType().getTradeTypeCode() + "|" + reqData.getFamilyRole().getRoleCode());
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(productModuleTradeData.getElementType()) && BofConst.MODIFY_TAG_ADD.equals(productModuleTradeData.getModifyTag()))
                {
                    PlatSvcTradeData pstd = (PlatSvcTradeData) productModuleTradeData;
                    pstd.setRsrvStr1(reqData.getFamilyUserId());
                    pstd.setRsrvStr2(reqData.getFamilyRole().getRoleCode());
                    pstd.setRsrvStr3(reqData.getFamilyRole().getRoleInstId());
                    pstd.setRemark("FMY|" + reqData.getTradeType().getTradeTypeCode() + "|" + reqData.getFamilyRole().getRoleCode());
                }
            }
        }
    }
}
