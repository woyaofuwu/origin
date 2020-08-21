
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 对于活动有终端的情况，就低原则计算终端补贴金额！ td_b_Sale_goods.rsrv_Str3配置的终端补贴金额和资源校验接口返回的销售价做比较，哪个小以哪个为准作为终端补贴金额
 * 
 * @author Mr.Z
 */
public class DeviceSubSidyFeeProductAction implements IProductModuleAction
{
    @SuppressWarnings("unchecked")
    public void executeProductModuleAction(ProductModuleTradeData dealPmtd, UcaData uca, BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();

        String preType = saleActiveReqData.getPreType();
        String isConFirm = saleActiveReqData.getIsConfirm();

        if (StringUtils.isNotBlank(preType) && !"true".equals(isConFirm))
            return;

        if (SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(saleActiveReqData.getCallType()))
            return;

        String compnTypde = saleActiveReqData.getCampnType();

        if ("YX04".equals(compnTypde))
            return;

        SaleGoodsTradeData saleGoodsTradeData = (SaleGoodsTradeData) dealPmtd;
        String goodsId = saleGoodsTradeData.getElementId();

//        IDataset goodsInfos = SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsId);
//        IData goodsData = goodsInfos.getData(0);
        IData goodsData = UpcCall.qryOfferComChaTempChaByCond(goodsId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);
        String resTypeCode = goodsData.getString("RES_TYPE_CODE", "");

        if (!"4".equals(resTypeCode))
            return;

        String rsrvStr3 = goodsData.getString("RSRV_STR3", ""); // SUBSIDY_FEE_TAG|终端补贴金额

        if (!rsrvStr3.startsWith("SUBSIDY_FEE_TAG"))
            return;

        String[] tagArr = rsrvStr3.split("\\|");

        if (tagArr.length == 2 && tagArr[1].trim().length() > 0)
        {
            int subsidyFee = new Integer(tagArr[1]);

            if (!"0".equals(saleGoodsTradeData.getRsrvTag1()))
                return;

            String deviceSaleFee = saleGoodsTradeData.getRsrvStr6();

            if (StringUtils.isBlank(deviceSaleFee))
                return;

            int deviceSaleFeeInt = Integer.parseInt(deviceSaleFee); // 终端销售价

            int tempSubsidyFee = subsidyFee;

            if (deviceSaleFeeInt < tempSubsidyFee)
            {
                tempSubsidyFee = deviceSaleFeeInt;
            }

            saleGoodsTradeData.setRsrvNum4(String.valueOf(tempSubsidyFee)); // 终端补贴金额（单位：分）
        }
    }
}
