
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.util;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.trade.UTradeHisInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;

public final class SaleActiveActionUtil
{
    @SuppressWarnings("unchecked")
    public static IData buildOccupyActionParams(SaleGoodsTradeData saleGoodsTradeData, MainTradeData mainTradeData, IData mainData, IData tradeSaleActive) throws Exception
    {

        IData intfCommonParam = new DataMap();

        String goodsSaleType = saleGoodsTradeData.getRsrvTag1();

        intfCommonParam.put("TRADE_ID", mainData.getString("TRADE_ID"));
        intfCommonParam.put("USER_ID", mainTradeData.getUserId());
        intfCommonParam.put("RES_TAG", "1");

        intfCommonParam.put("CONTRACT_ID", mainData.getString("TRADE_ID"));
        intfCommonParam.put("PRODUCT_ID", saleGoodsTradeData.getProductId());
        intfCommonParam.put("PACKAGE_ID", saleGoodsTradeData.getPackageId());
        intfCommonParam.put("CAMPN_ID", "-1");
        intfCommonParam.put("PRODUCT_MODE", "0"); // 全网统一操盘合约机销售标志
        intfCommonParam.put("PARA_CODE1", "1"); // 0-常态销售 1-捆绑销售
        intfCommonParam.put("INFO_TAG", "1");
        intfCommonParam.put("PARA_CODE7", goodsSaleType); // 赠送还是销售
        intfCommonParam.put("RES_ID", saleGoodsTradeData.getResId());
        intfCommonParam.put("RES_TYPE_CODE", saleGoodsTradeData.getResTypeCode());
        intfCommonParam.put("OPER_NUM", saleGoodsTradeData.getGoodsNum());
        intfCommonParam.put("RES_NO", saleGoodsTradeData.getResCode());
        intfCommonParam.put("SALE_FEE", "1".equals(goodsSaleType) ? "0" : mainTradeData.getOperFee());
        intfCommonParam.put("DEVICE_COST", saleGoodsTradeData.getDeviceCost());
        intfCommonParam.put("STAFF_ID", mainData.getString("TRADE_STAFF_ID")); // 受理员工
        intfCommonParam.put("X_RES_NO_S", saleGoodsTradeData.getResCode());
        intfCommonParam.put("USER_NAME", mainTradeData.getCustName());

        intfCommonParam.put("PARA1", saleGoodsTradeData.getPackageId());
        intfCommonParam.put("PARA2", "2"); // 绑定营销案类型
        intfCommonParam.put("PARA3", tradeSaleActive.getString("RSRV_NUM1")); // 客户承诺最低消费

        intfCommonParam.put("PREVALUE1", saleGoodsTradeData.getRsrvStr9());

        // 买断过一次,再优惠购机
        if ("YX08".equals(saleGoodsTradeData.getRsrvStr10()))
            intfCommonParam.put("SALE_TAG", "1");
        // 虚拟供货的再优惠购机
        if ("YX09".equals(saleGoodsTradeData.getRsrvStr10()))
            intfCommonParam.put("SALE_TAG", "2");

        String productId = saleGoodsTradeData.getProductId();
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(productId);

        if ("G".equals(productInfo.getString("RSRV_TAG2")))
        {
            intfCommonParam.put("SALE_TAG", "3");
        }
        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 2016-08-26
         * 查询product_id信息。
         * */ 
        if ("R".equals(productInfo.getString("RSRV_TAG2")) || "M".equals(productInfo.getString("RSRV_TAG2")))
        {
            intfCommonParam.put("SALE_TAG", "1");
        }

        String operFee = mainTradeData.getOperFee();
        long operFeeLong = StringUtils.isBlank(operFee) ? 0 : Long.parseLong(operFee);
        String advanceFee = mainTradeData.getAdvancePay();
        long advanceFeeLong = StringUtils.isBlank(advanceFee) ? 0 : Long.parseLong(advanceFee);
        String foreGiftFee = mainTradeData.getForegift();
        long foreGiftFeeLong = StringUtils.isBlank(foreGiftFee) ? 0 : Long.parseLong(foreGiftFee);

        intfCommonParam.put("PARA_VALUE1", mainTradeData.getSerialNumber());
        intfCommonParam.put("PARA_VALUE3", saleGoodsTradeData.getCampnId());
        intfCommonParam.put("PARA_VALUE6", mainTradeData.getRsrvStr8());
        intfCommonParam.put("PARA_VALUE7", saleGoodsTradeData.getRsrvStr7());
        intfCommonParam.put("PARA_VALUE8", tradeSaleActive.getString("RSRV_NUM1")); //
        intfCommonParam.put("PARA_VALUE9", "03"); // 客户捆绑合约类型
        intfCommonParam.put("PARA_VALUE10", "");
        intfCommonParam.put("PARA_VALUE11", mainData.getString("ACCEPT_DATE"));
        intfCommonParam.put("PARA_VALUE12", "");
        intfCommonParam.put("PARA_VALUE13", "0"); // 是否有销售酬金 0-没有 1-有
        intfCommonParam.put("PARA_VALUE14", operFeeLong + advanceFeeLong); // 裸机价格
        intfCommonParam.put("PARA_VALUE15", advanceFeeLong); // 客户购机折让价格
        intfCommonParam.put("PARA_VALUE16", advanceFeeLong); // 客户预存话费
        intfCommonParam.put("PARA_VALUE17", operFeeLong); // 客户实际购机款
        intfCommonParam.put("PARA_VALUE18", foreGiftFeeLong + operFeeLong + advanceFeeLong); // 客户实缴费用总额

        intfCommonParam.put(Route.ROUTE_EPARCHY_CODE, mainTradeData.getEparchyCode());

        return intfCommonParam;
    }

    @SuppressWarnings("unchecked")
    public static IData buildReleaseActionParams(SaleGoodsTradeData saleGoodsTradeData, IData tradeMain, IData tradeSaleActive) throws Exception
    {
        IData intfCommonParam = new DataMap();

        String goodsSaleType = saleGoodsTradeData.getRsrvTag1();

        intfCommonParam.put("TRADE_ID", tradeMain.getString("TRADE_ID"));
        intfCommonParam.put("USER_ID", tradeMain.getString("USER_ID"));
        intfCommonParam.put("RES_TAG", "1");

        intfCommonParam.put("CONTRACT_ID", tradeMain.getString("TRADE_ID"));
        intfCommonParam.put("PRODUCT_ID", saleGoodsTradeData.getProductId());
        intfCommonParam.put("PACKAGE_ID", saleGoodsTradeData.getPackageId());
        intfCommonParam.put("CAMPN_ID", "-1");
        intfCommonParam.put("PRODUCT_MODE", "0"); // 全网统一操盘合约机销售标志
        intfCommonParam.put("PARA_CODE1", "1"); // 0-常态销售 1-捆绑销售
        intfCommonParam.put("INFO_TAG", "1");
        intfCommonParam.put("PARA_CODE7", goodsSaleType); // 赠送还是销售
        intfCommonParam.put("RES_ID", saleGoodsTradeData.getResId());
        intfCommonParam.put("RES_TYPE_CODE", saleGoodsTradeData.getResTypeCode());
        intfCommonParam.put("OPER_NUM", saleGoodsTradeData.getGoodsNum());
        intfCommonParam.put("RES_NO", saleGoodsTradeData.getResCode());
        intfCommonParam.put("SALE_FEE", "1".equals(goodsSaleType) ? "0" : tradeMain.getString("OPER_FEE"));
        intfCommonParam.put("DEVICE_COST", saleGoodsTradeData.getDeviceCost());
        intfCommonParam.put("STAFF_ID", tradeMain.getString("TRADE_STAFF_ID")); // 销售员工
        String tradeId = tradeMain.getString("TRADE_ID");
        IData tradeHistoryInfo = UTradeHisInfoQry.qryTradeHisByPk(tradeId, "1", BizRoute.getRouteId());
        if(IDataUtil.isNotEmpty(tradeHistoryInfo))
        {
        	 intfCommonParam.put("STAFF_ID", tradeHistoryInfo.getString("TRADE_STAFF_ID")); // 销售员工
        }
        intfCommonParam.put("X_RES_NO_E", saleGoodsTradeData.getResCode());
        intfCommonParam.put("USER_NAME", tradeMain.getString("CUST_NAME"));

        intfCommonParam.put("PARA1", saleGoodsTradeData.getPackageId());
        intfCommonParam.put("PARA2", "2"); // 绑定营销案类型
        intfCommonParam.put("PARA3", tradeSaleActive.getString("RSRV_NUM1")); // 客户承诺最低消费

        intfCommonParam.put("PREVALUE1", saleGoodsTradeData.getRsrvStr9());

        if ("YX08".equals(tradeMain.getString("RSRV_STR7"))) // 买断过一次,再优惠购机
        {
            intfCommonParam.put("SALE_TAG", "1");
        }

        if ("YX09".equals(tradeMain.getString("RSRV_STR7"))) // 虚拟供货的再优惠购机
        {
            intfCommonParam.put("SALE_TAG", "2");
        }

        String productId = saleGoodsTradeData.getProductId();
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(productId);

        if ("G".equals(productInfo.getString("RSRV_TAG2")))
        {
            intfCommonParam.put("SALE_TAG", "3");
        }
        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 2016-08-26
         * 查询product_id信息。
         * */ 
        if ("R".equals(productInfo.getString("RSRV_TAG2")) || "M".equals(productInfo.getString("RSRV_TAG2")))
        {
            intfCommonParam.put("SALE_TAG", "1");
        }

        String operFee = tradeMain.getString("OPER_FEE");
        long operFeeLong = StringUtils.isBlank(operFee) ? 0 : Long.parseLong(operFee);
        String advanceFee = tradeMain.getString("ADVANCE_PAY");
        long advanceFeeLong = StringUtils.isBlank(advanceFee) ? 0 : Long.parseLong(advanceFee);
        String foreGiftFee = tradeMain.getString("FOREGIFT");
        long foreGiftFeeLong = StringUtils.isBlank(foreGiftFee) ? 0 : Long.parseLong(foreGiftFee);

        intfCommonParam.put("PARA_VALUE1", tradeMain.getString("SERIAL_NUMBER"));
        intfCommonParam.put("PARA_VALUE3", "-1");
        intfCommonParam.put("PARA_VALUE6", tradeMain.getString("RSRV_STR8"));
        intfCommonParam.put("PARA_VALUE7", saleGoodsTradeData.getRsrvStr7());
        intfCommonParam.put("PARA_VALUE8", tradeSaleActive.getString("RSRV_NUM1")); //客户承诺最低消费,取的是sale_active表的RSRV_NUM1，目前未发现记录
        intfCommonParam.put("PARA_VALUE9", "03"); // 客户捆绑合约类型
        intfCommonParam.put("PARA_VALUE10", "");
        intfCommonParam.put("PARA_VALUE11", tradeMain.getString("ACCEPT_DATE"));
        intfCommonParam.put("PARA_VALUE12", "");
        intfCommonParam.put("PARA_VALUE13", "0"); // 是否有销售酬金 0-没有 1-有
        intfCommonParam.put("PARA_VALUE14", operFeeLong + advanceFeeLong); // 裸机价格
        intfCommonParam.put("PARA_VALUE15", advanceFeeLong); // 客户购机折让价格
        intfCommonParam.put("PARA_VALUE16", advanceFeeLong); // 客户预存话费
        intfCommonParam.put("PARA_VALUE17", operFeeLong); // 客户实际购机款
        intfCommonParam.put("PARA_VALUE18", foreGiftFeeLong + operFeeLong + advanceFeeLong); // 客户实缴费用总额

        intfCommonParam.put(Route.ROUTE_EPARCHY_CODE, tradeMain.getString("EPARCHY_CODE"));

        return intfCommonParam;
    }
}
