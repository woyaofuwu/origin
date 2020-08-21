
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.buildrequestdata;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfareDiscntData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfarePlatSvcData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.data.WelfareSvcData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.requestdata.WelfareOfferRequestData;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.utils.WelfareUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description 权益自有商品请求对象构建类
 * @Auther: zhenggang
 * @Date: 2020/7/3 10:56
 * @version: V1.0
 */
public class BuildWelfareOfferRequestData extends BaseBuilder implements IBuilder
{
    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        WelfareOfferRequestData reqData = (WelfareOfferRequestData) brd;
        String welfareTradeId = param.getString("WELFARE_ORDER_ID");
        String advanceOrderId = param.getString("ADVANCE_ORDER_ID");
        String advanceTradeId = param.getString("ADVANCE_TRADE_ID");
        String startDate = param.getString("START_DATE");
        String endDate = param.getString("END_DATE");
        String printContent = param.getString("PRINT_CONTENT");

        // 删除权益编码
        // 支持订购 删除同时处理 也支持删除时只传权益包编码
        // DEL_WELFARE_OFFER_CODE和ELEMENTS可以只传一个 也可以混用。
        // 混用的话最好ELEMENTS里面都是订购的商品
        String delWelfareOfferCode = param.getString("DEL_WELFARE_OFFER_CODE");

        if (StringUtils.isBlank(welfareTradeId))
        {
            CSAppException.apperr(WelfareException.CRM_WELFARE_4);
        }

        IDataset elements = new DatasetList(param.getString("ELEMENTS", "[]"));

        if (IDataUtil.isEmpty(elements) && StringUtils.isEmpty(delWelfareOfferCode))
        {
            CSAppException.apperr(WelfareException.CRM_WELFARE_1);
        }

        if (StringUtils.isNotEmpty(delWelfareOfferCode) && StringUtils.isEmpty(endDate))
        {
            // 传了退订权益包那么退订时间也要传
            CSAppException.apperr(WelfareException.CRM_WELFARE_8);
        }
        // 权益订单流水
        reqData.setWelfareTradeId(welfareTradeId);
        // 前置订单流水
        reqData.setAdvanceOrderId(advanceOrderId);
        // 前置交易流水
        reqData.setAdvanceTradeId(advanceTradeId);
        // 自定义开始时间
        reqData.setSelfDefStartDate(startDate);
        // 自定义结束时间
        reqData.setSelfDefEndDate(endDate);
        // 打印内容
        reqData.setPrintContent(printContent);
        // 删除权益包编码 这个值为了减少入参。只传这个进来则会退订掉这个权益包关联订购的所有自有商品
        reqData.setDelWelfareOfferCode(delWelfareOfferCode);

        if (StringUtils.isNotEmpty(reqData.getAdvanceTradeId()))
        {
            this.queryAdvanceTradeMainOfferInstId(reqData, advanceTradeId);
        }

        List<ProductModuleData> pmds = new ArrayList<ProductModuleData>();

        if (IDataUtil.isNotEmpty(elements))
        {
            int size = elements.size();

            for (int i = 0; i < size; i++)
            {
                IData element = elements.getData(i);

                String offerCode = element.getString("OFFER_CODE");
                String offerType = element.getString("OFFER_TYPE");
                String instId = element.getString("INST_ID");
                String modifyTag = element.getString("MODIFY_TAG");

                String welfareOfferCode = element.getString("WELFARE_OFFER_CODE");

                if (StringUtils.isEmpty(welfareOfferCode))
                {
                    // ELEMENTS里面要传权益包编码，要不然不知道退订或者退订哪个权益包下面的自有权益
                    CSAppException.apperr(WelfareException.CRM_WELFARE_7);
                }

                // ----------------------------------------------
                // 转一下KEY
                element.put("ELEMENT_ID", offerCode);
                element.put("PRODUCT_ID", WelfareConstants.offerCode.FAKE_CODE.getValue());
                element.put("PACKAGE_ID", WelfareConstants.offerCode.FAKE_CODE.getValue());
                // ----------------------------------------------

                // 删除或修改时找到正确的INST_ID
                if (!BofConst.MODIFY_TAG_ADD.equals(modifyTag) && StringUtils.isBlank(instId))
                {
                    String offerInstId = WelfareUtil.findCorrectInstId(reqData, welfareOfferCode, offerCode, offerType);
                    element.put("INST_ID", offerInstId);
                }

                if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(offerType))
                {
                    WelfareDiscntData discntData = new WelfareDiscntData(element);
                    pmds.add(discntData);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(offerType))
                {
                    WelfareSvcData svcData = new WelfareSvcData(element);
                    pmds.add(svcData);
                }
                else if (BofConst.ELEMENT_TYPE_CODE_PLATSVC.equals(offerType))
                {
                    if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                    {
                        element.put("OPER_CODE", PlatConstants.OPER_ORDER);
                    }
                    else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                    {
                        element.put("OPER_CODE", PlatConstants.OPER_CANCEL_ORDER);
                    }
                    WelfarePlatSvcData platSvcData = new WelfarePlatSvcData(element);
                    pmds.add(platSvcData);
                }
            }
        }

        for (ProductModuleData pmd : pmds)
        {
            // 如果传入绝对时间则以这个时间为准
            String modifyTag = pmd.getModifyTag();

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                if (StringUtils.isNotBlank(reqData.getSelfDefStartDate()))
                {
                    pmd.setStartDate(reqData.getSelfDefStartDate());
                }

                if (StringUtils.isNotBlank(reqData.getSelfDefEndDate()))
                {
                    pmd.setEndDate(reqData.getSelfDefEndDate());
                }

                if (StringUtils.isBlank(pmd.getStartDate()))
                {
                    CSAppException.apperr(WelfareException.CRM_WELFARE_2, pmd.getElementId());
                }
                if (StringUtils.isBlank(pmd.getEndDate()))
                {
                    CSAppException.apperr(WelfareException.CRM_WELFARE_3, pmd.getElementId());
                }
            }
            else
            {
                if (StringUtils.isNotBlank(reqData.getSelfDefEndDate()))
                {
                    pmd.setEndDate(reqData.getSelfDefEndDate());
                }
                if (StringUtils.isBlank(pmd.getEndDate()))
                {
                    CSAppException.apperr(WelfareException.CRM_WELFARE_3, pmd.getElementId());
                }
            }
        }
        reqData.setPmds(pmds);
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new WelfareOfferRequestData();
    }

    /**
     * @Description: 设置主商品实例ID
     * @Param: [reqData, tradeId]
     * @return: void
     * @Author: zhenggang
     * @Date: 2020/7/14 11:53
     */
    private void queryAdvanceTradeMainOfferInstId(WelfareOfferRequestData reqData, String tradeId) throws Exception
    {
        // 查产品台账表
        IDataset tradeProducts = TradeProductInfoQry.getTradeProductByTradeId(tradeId);

        if (IDataUtil.isEmpty(tradeProducts))
        {
            return;
        }

        for (int i = 0; i < tradeProducts.size(); i++)
        {
            IData tradeProduct = tradeProducts.getData(i);

            String instId = tradeProduct.getString("INST_ID");
            String modifyTag = tradeProduct.getString("MODIFY_TAG");

            if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
            {
                reqData.setAddMainOfferInsId(instId);
            }
            else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
            {
                reqData.setDelMainOfferInsId(instId);
            }
        }
    }
}
