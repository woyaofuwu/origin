
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;

public class GroupTradeAfterUtil
{

    /**
     * iDataset里面遍历加key元素值为value
     * 
     * @param iDataset
     * @param modifyTag
     * @throws Exception
     */
    private static void dealElementKeyValue(IDataset iDataset, String key, String value) throws Exception
    {
        if (IDataUtil.isNotEmpty(iDataset))
        {
            for (int i = 0, size = iDataset.size(); i < size; i++)
            {
                iDataset.getData(i).put(key, value);
            }
        }
    }

    public static void getTradeAfterElementData(IData ruleParam, IData tradeAllData) throws Exception
    {
        if (tradeAllData.containsKey(TradeTableEnum.TRADE_SVC.getValue()) || tradeAllData.containsKey(TradeTableEnum.TRADE_DISCNT.getValue()) || tradeAllData.containsKey(TradeTableEnum.TRADE_PRODUCT.getValue()))
        {
            IDataset svcList = getTradeChkAfterSvcs(tradeAllData, ruleParam.getString("USER_ID"));
            ruleParam.put("TF_F_USER_SVC_AFTER", svcList);

            IDataset discntListt = getTradeChkAfterDiscnt(tradeAllData, ruleParam.getString("USER_ID"));
            ruleParam.put("TF_F_USER_DISCNT_AFTER", discntListt);

            IDataset productsList = getTradeChkAfterProduct(tradeAllData, ruleParam.getString("USER_ID"));
            ruleParam.put("TF_F_USER_PRODUCT_AFTER", productsList);

            IDataset attrList = getTradeChkAfterAttr(tradeAllData, ruleParam.getString("USER_ID"));
            ruleParam.put("TF_F_USER_ATTR_AFTER", attrList);
        }
    }

    private static IDataset getTradeChkAfterAttr(IData idata, String userId) throws Exception
    {

        IDataset userAttrs = BofQuery.queryUserAllAttr(userId, BizRoute.getRouteId());
        if (userAttrs == null)
            userAttrs = new DatasetList();

        dealElementKeyValue(userAttrs, "MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());

        IDataset tradeAttrs = idata.getDataset("TF_B_TRADE_ATTR");
        if (tradeAttrs == null)
            tradeAttrs = new DatasetList();

        IDataset future = DataBusUtils.getFutureForSpec(userAttrs, tradeAttrs, new String[]
        { "INST_ID", "ATTR_CODE" });

        return future;
    }

    public static IDataset getTradeChkAfterDiscnt(IData idata, String userId) throws Exception
    {

        IDataset tradeDiscnts = idata.getDataset("TF_B_TRADE_DISCNT");
        if (tradeDiscnts == null)
            tradeDiscnts = new DatasetList();

        IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(userId, BizRoute.getRouteId());
        if (userDiscnts == null)
            userDiscnts = new DatasetList();

        dealElementKeyValue(userDiscnts, "MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());

        IDataset future = DataBusUtils.getFuture(userDiscnts, tradeDiscnts, new String[]
        { "INST_ID" });

        return future;
    }

    private static IDataset getTradeChkAfterProduct(IData idata, String userId) throws Exception
    {

        IDataset userProducts = BofQuery.getUserAllProducts(userId, BizRoute.getRouteId());
        if (userProducts == null)
            userProducts = new DatasetList();

        dealElementKeyValue(userProducts, "MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());

        IDataset tradeProducts = idata.getDataset("TF_B_TRADE_PRODUCT");
        if (tradeProducts == null)
            tradeProducts = new DatasetList();

        IDataset future = DataBusUtils.getFuture(userProducts, tradeProducts, new String[]
        { "INST_ID" });

        return future;
    }

    public static IDataset getTradeChkAfterSvcs(IData idata, String userId) throws Exception
    {

        IDataset tradeSvcs = idata.getDataset("TF_B_TRADE_SVC");
        if (tradeSvcs == null)
            tradeSvcs = new DatasetList();

        IDataset userSvcs = BofQuery.queryUserAllSvc(userId, BizRoute.getRouteId());
        if (userSvcs == null)
            userSvcs = new DatasetList();

        dealElementKeyValue(userSvcs, "MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());

        IDataset future = DataBusUtils.getFuture(userSvcs, tradeSvcs, new String[]
        { "INST_ID" });

        return future;
    }

}
