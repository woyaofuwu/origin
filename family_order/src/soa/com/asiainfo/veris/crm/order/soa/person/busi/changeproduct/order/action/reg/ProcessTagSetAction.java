
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.requestdata.ChangeProductReqData;

public class ProcessTagSetAction implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ChangeProductReqData request = (ChangeProductReqData) btd.getRD();

        List<ProductTradeData> productList = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);

        List<SvcTradeData> svcList = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);

        List<DiscntTradeData> discntList = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);

        // 第三位，是否变更品牌
        if (btd.isBrandChange())
        {
            btd.setMainTradeProcessTagSet(3, "1");
        }

        // 第四位，是否取消大客户资格
        if (this.isCancelCustVip(request, btd))
        {
            btd.setMainTradeProcessTagSet(4, "1");
        }

        // 第五位，是否变更主产品(重算信用标志位)
        if (true)
        {
            btd.setMainTradeProcessTagSet(5, "1");
        }
        // 第六位，是否变更附加产品
        /*
         * String sTackChange = specData.getString("IF_MODIFY_TACK", "0"); if ("1".equals(sTackChange)) { processTagSet
         * = processTagSet.substring(0, 5) + "1" + processTagSet.substring(6, 20); }
         */
        // 第七位，是否变更服务
        if (svcList != null && svcList.size() > 0)
        {
            btd.setMainTradeProcessTagSet(7, "1");
        }

        // 第八位，是否变更优惠
        if (discntList != null && discntList.size() > 0)
        {
            btd.setMainTradeProcessTagSet(8, "1");
        }
        // 第九位，业务变更标志(1:仅服务变更;2:仅优惠变更;3:仅服务和优惠变更;4:主产品变更;）
        String sChangeType = "0";
        if (productList != null && productList.size() > 0)
        {
            sChangeType = "4";
        }
        else if (svcList != null && svcList.size() > 0 && (discntList == null || discntList.size() <= 0))
        {
            sChangeType = "1";
        }
        else if ((svcList == null || svcList.size() <= 0) && discntList != null && discntList.size() > 0)
        {
            sChangeType = "2";
        }
        else if (svcList != null && svcList.size() > 0 && discntList != null && discntList.size() > 0 && (productList != null || productList.size() <= 0))
        {
            sChangeType = "3";
        }
        /*
         * else if("1".equals(sTackChange)) { sChangeType = "5"; }
         */
        btd.setMainTradeProcessTagSet(9, sChangeType);

        // 第十位，是否预约产品变更后服务变更
        if (svcList != null && svcList.size() > 0 && request.isBookingTag() && productList != null && productList.size() > 0)
        {
            btd.setMainTradeProcessTagSet(10, "1");
        }

        // 第十七位，是否取消营销活动 参阅 SaleActiveCancelAction
        if (this.isCancelSaleActive(request, btd))
        {
            btd.setMainTradeProcessTagSet(17, "1");
        }

        // 第十八位，是否积分清零 参阅 ScoreCleanAction

        // 第十九位：是否预约产品变更
        if (request.isBookingTag())
        {
            btd.setMainTradeProcessTagSet(19, "1");
        }
    }

    /**
     * @Description:是否取消大客户
     * @param request
     * @param btd
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 5, 2014 5:03:07 PM
     */
    public boolean isCancelCustVip(ChangeProductReqData request, BusiTradeData btd) throws Exception
    {
        if (btd.isBrandChange())
        {
            String custId = btd.getRD().getUca().getCustId();

            String oldBrand = request.getUca().getUserMainProduct().getBrandCode();

            String newBrand = request.getNewMainProduct().getBrandCode();

            IData custVipData = new ChangeProductBean().getCustVipIsCancelTag(custId, oldBrand, newBrand);

            if (IDataUtil.isNotEmpty(custVipData))
            {
                // 取消大客户
                if ("1".equals(custVipData.getString("CUST_VIP_CANCEL_TAG")))
                {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @Description: 是否取消营销活动
     * @param request
     * @param btd
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 5, 2014 5:02:49 PM
     */
    public boolean isCancelSaleActive(ChangeProductReqData request, BusiTradeData btd) throws Exception
    {
        if (btd.isProductChange())
        {
            String userId = btd.getRD().getUca().getUserId();

            String oldProductId = btd.getMainTradeData().getRsrvStr2();

            String newProductId = request.getNewMainProduct().getProductId();

            String oldBrand = btd.getRD().getUca().getBrandCode();

            String newBrand = request.getNewMainProduct().getBrandCode();

            String bookingDate = request.getBookingDate();

            IDataset cancelSaleActiveData = new ChangeProductBean().getCancelSaleActiveList(userId, oldBrand, newBrand, oldProductId, newProductId, bookingDate);

            if (IDataUtil.isNotEmpty(cancelSaleActiveData))
            {
                return true;
            }
        }

        return false;
    }
}
