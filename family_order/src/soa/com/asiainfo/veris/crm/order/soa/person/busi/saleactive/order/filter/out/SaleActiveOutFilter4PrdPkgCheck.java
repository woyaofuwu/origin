
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.filter.out;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CreditTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleDepositTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveOutFilter4PrdPkgCheck implements IFilterOut
{
    @SuppressWarnings("unchecked")
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
        SaleActiveReqData saleActiveReqData = (SaleActiveReqData) btd.getRD();
        IData outData = new DataMap();

        // 返回给外围渠道的活动总费用
        String getFee = input.getString("X_GETFEE");
        List<FeeData> feeDataList = saleActiveReqData.getFeeList();
        int feeInt = 0;
        if (CollectionUtils.isNotEmpty(feeDataList) && (StringUtils.isNotBlank(getFee) && "1".equals(getFee)))
        {
            for (int index = 0, size = feeDataList.size(); index < size; index++)
            {
                String fee = feeDataList.get(index).getFee();
                feeInt += Integer.parseInt(fee);
            }
        }
        outData.put("FEE", feeInt);

        // 对于1746有参数配置的，返回特殊的值！
        String productId = saleActiveReqData.getProductId();
        String eparchyCode = saleActiveReqData.getUca().getUserEparchyCode();
        IDataset commparaDataset = CommparaInfoQry.getCommPkInfo("CSM", "1746", productId, eparchyCode);
        if (IDataUtil.isEmpty(commparaDataset))
        {
            //return outData;//20160406 by songlm 陈琼春要求不再使用1746判断，放开所有活动，都返回扩展字段内容
        }

        String packageId = saleActiveReqData.getPackageId();
        IData packageExtInfo = UPackageInfoQry.getPackageByPK(packageId);
        outData.put("EXT_DESC", packageExtInfo.getString("EXT_DESC"));

        String packageExtRsrvStr25 = packageExtInfo.getString("RSRV_STR25");
        if (StringUtils.isNotBlank(packageExtRsrvStr25))
        {
            outData.put("RSRV_STR1", packageExtRsrvStr25);
        }

        List<SaleDepositTradeData> saleDepositTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEDEPOSIT);
        for (SaleDepositTradeData saleDepositTradeData : saleDepositTradeList)
        {
            outData.put("RSRV_STR2", saleDepositTradeData.getRsrvNum5());
//            IDataset datas = SaleDepositInfoQry.querySaleDepositById(saleDepositTradeData.getElementId(), eparchyCode);
            IDataset datas = UpcCall.qryOfferGiftByExtGiftId(saleDepositTradeData.getElementId());
            if (IDataUtil.isNotEmpty(datas))
            {
                outData.put("EXT_DESC", datas.getData(0).getString("DESCRIPTION"));
            }
        }

        List<SaleGoodsTradeData> saleGoodsTradeList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEGOODS);
        for (SaleGoodsTradeData saleGoodsTradeData : saleGoodsTradeList)
        {
            outData.put("RSRV_STR4", saleGoodsTradeData.getRsrvNum5());
        }

        List<CreditTradeData> creditTradeDataList = btd.getTradeDatas(TradeTableEnum.TRADE_CREDIT);
        for (CreditTradeData creditTradeData : creditTradeDataList)
        {
            outData.put("RSRV_STR3", creditTradeData.getCreditValue());
        }

        return outData;
    }

}
