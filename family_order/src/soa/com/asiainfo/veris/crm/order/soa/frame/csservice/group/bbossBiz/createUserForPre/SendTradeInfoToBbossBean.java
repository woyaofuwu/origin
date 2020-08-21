
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.createUserForPre;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.server.hessian.wade3tran.Wade3DataTran;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.trade.TradeMag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.SendDataToEsopBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;

public class SendTradeInfoToBbossBean
{

    /**
     * chenyi 2014-6-17 预受理转正式受理 更新台帐表数据
     * 
     * @param trade_id
     * @param mproduct_id
     * @param order_id
     * @param merch_trade_id
     * @throws Exception
     */
    private static void changeStatusForTradeForPre(String trade_id, String mproduct_id, boolean isMerch) throws Exception
    {
        // 1- 查询台账信息
        IDataset ProductTradeInfoList = TradeInfoQry.queryTradeSet(trade_id, null);
        if (IDataUtil.isEmpty(ProductTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData idata = ProductTradeInfoList.getData(0);
        String mainTradeOrderID = idata.getString("ORDER_ID");

        // 一单多线商品订单只生成一次
        String isFirst = idata.getString("RSRV_STR10");

        if (isMerch && StringUtils.isNotEmpty(isFirst))
        {
            // 2-1 处理商品级ORDER_ID

            // 更新商品的Merch表的merch order_id
            String merch_order_id = SeqMgr.getBBossMerchIdForGrp();
            TradeGrpMerchInfoQry.updateMerchOrderIdByTradeId(trade_id, merch_order_id);

        }
        else
        {
            // 2-2 处理产品级ORDER_ID

            // 更新产品的MerchP表product_orderid
            String product_order_id = SeqMgr.getBBossProductIdForGrp();
            TradeGrpMerchpInfoQry.updateMerchpOrderIdByTradeId(trade_id, product_order_id);
        }

        // 3- 更新台账信息
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", trade_id);
        inparam.put("RSRV_STR10", "");
        // 更改产品主台帐标记为受理状态
        TradeInfoQry.updateTradeRsrvStr10(inparam);

        // 更新order表orderstate改成0状态 HQ_tag 改成r
        TradeMag.updateStateHq(mainTradeOrderID, "r", "C", "0");

        // 改台账表商产品的状态为0
        TradeInfoQry.updateSubstate(trade_id, "0");

    }

    public static void sendTradeInfo(IData map) throws Exception
    {
        // 1- 获取处理标志，区分预受理转正式受理和管理报文发送

        // 2- 获取需要正式受理的产品台账编号
        String strTradeIds = map.getString("PRODUCT_TRADE_ID");
        List list = Wade3DataTran.strToList(strTradeIds);

        IDataset productTradeIdInfoList = Wade3DataTran.wade3To4Dataset(list);
        if (IDataUtil.isEmpty(productTradeIdInfoList))
        {
            return;
        }

        // 3- 通过产品信息获取商品信息
        IData merchTradeInfo = GrpCommonBean.getMerchTradeInfo(productTradeIdInfoList.get(0).toString());
        String merchTradeId = merchTradeInfo.getString("TRADE_ID");

        if (IDataUtil.isEmpty(TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCode(merchTradeId, "ATT_INFOS")))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_226);
        }

        // 4- 受理报文发送通知esop
        map.put("MERCH_TRADEID", merchTradeId);
        SendDataToEsopBean.sendEsopSLBefore(map);

        // 5- 发送产品台账数据至服开
        String productTradeId = "";
        for (int i = 0; i < productTradeIdInfoList.size(); i++)
        {
            productTradeId = productTradeIdInfoList.get(i).toString();
            IDataset productTradeInfoList = TradeProductInfoQry.getTradeProduct(productTradeId, null);
            if (IDataUtil.isEmpty(productTradeInfoList))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_825);
            }
            IData productTradeInfo = productTradeInfoList.getData(0);
            String productId = productTradeInfo.getString("PRODUCT_ID");
            changeStatusForTradeForPre(productTradeId, productId, false);
        }

        // 6- 发送商品台账信息至服开
        IDataset productTradeInfoList = TradeProductInfoQry.getTradeProduct(merchTradeId, null);
        if (IDataUtil.isEmpty(productTradeInfoList))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_825);
        }
        IData productTradeInfo = productTradeInfoList.getData(0);
        String merchId = productTradeInfo.getString("PRODUCT_ID");
        changeStatusForTradeForPre(merchTradeId, merchId, true);

        // 7- 清除BBOSS侧产品资费表(MERCHP_DISCNT)及产品参数表中对应的MODIFY为删除的数据(这部分数据在完工时是无意义的)
        IData inparam = new DataMap();
        for (int i = 0; i < productTradeIdInfoList.size(); i++)
        {
            inparam.clear();
            productTradeId = (String) productTradeIdInfoList.get(i);
            inparam.put("TRADE_ID", productTradeId);
            inparam.put("MODIFY_TAG", CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            // 处理产品参数表
            TradeAttrInfoQry.deleteAttrByTradeIdModifyTag(productTradeId, CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
            // 处理产品资费表
            TradeGrpMerchpDiscntInfoQry.deleteMerchpDiscntByTradeIdModifyTag(productTradeId, CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue());
        }

    }

}
