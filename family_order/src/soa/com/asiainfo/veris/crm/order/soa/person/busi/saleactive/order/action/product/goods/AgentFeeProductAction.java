
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.action.product.goods;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.IProductModuleAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.ProductModuleTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

/**
 * 计算回传给资源的代办费
 * 
 * @author Mr.Z
 */
public class AgentFeeProductAction implements IProductModuleAction
{
    /**
     * 一、预存话费优惠购机酬金 1、酬金=首付（月约定最低消费额*40%）+分月酬金（月约定最低消费额*10% *（约定消费月数-1））； 2、其他说明：
     * （1）酬金发放中月最低消费额最高限定为288元，如高于288元按288元计(系统不做校验，由配置人员配置时判断)。 （2）约定消费月数最高24个月，如约定消费月数大于24个月按24个月计(系统不做校验，由配置人员配置时判断)。
     * （3）当分阶酬金等于一次酬金的时候以一次酬金为准
     * 
     * @param goodsConfigRsrvStr2Arr3
     * @param goodsConfigRsrvStr2Arr4
     * @return
     */
    private IData computeAgentFeeBy2And4(String goodsConfigRsrvStr2Arr2, String goodsConfigRsrvStr2Arr3, String goodsConfigRsrvStr2Arr4, int agenFee)
    {
        int ydMoney = new Integer(goodsConfigRsrvStr2Arr3);
        int ydMonth = new Integer(goodsConfigRsrvStr2Arr4);

        int agenFeeTemp = (ydMoney * 40 / 100) + ((ydMoney * 10 / 100) * (ydMonth - 1));

        if ("4".equals(goodsConfigRsrvStr2Arr2)) // 酬金标识配置为4时，要求按1、2两种酬金标识算法算酬金，哪个高就以哪个的为准
        {
            goodsConfigRsrvStr2Arr2 = "1";
            if (agenFeeTemp > agenFee)
            {
                agenFee = agenFeeTemp;
                goodsConfigRsrvStr2Arr2 = "2";
            }
        }

        IData returnData = new DataMap();
        returnData.put("RSRV_STR2", goodsConfigRsrvStr2Arr2);
        returnData.put("AGENT_FEE", agenFee);
        return returnData;
    }

    /**
     * 二、购机赠送话费酬金 1、酬金=月约定最低消费额*10%*约定消费月数 2、其他说明： （1）酬金发放中月最低消费额最高限定为288元，如高于288元按288元计(系统不做校验，由配置人员配置时判断)。
     * （2）约定消费月数最高24个月，如约定消费月数大于24个月按24个月计(系统不做校验，由配置人员配置时判断)。 （3）当分阶酬金等于一次酬金的时候以一次酬金为准
     * 
     * @param goodsConfigRsrvStr2Arr2
     * @param goodsConfigRsrvStr2Arr3
     * @param goodsConfigRsrvStr2Arr4
     * @param agenFee
     * @return
     */
    private IData computeAgentFeeBy3And5(String goodsConfigRsrvStr2Arr2, String goodsConfigRsrvStr2Arr3, String goodsConfigRsrvStr2Arr4, int agenFee)
    {
        int ydMoney = new Integer(goodsConfigRsrvStr2Arr3);
        int ydMonth = new Integer(goodsConfigRsrvStr2Arr4);

        int agenFeeTemp = (ydMoney * 10 / 100) * ydMonth;

        if ("5".equals(goodsConfigRsrvStr2Arr2)) // 酬金标识配置为5时，要求按1、3两种酬金标识算法算酬金，哪个高就以哪个的为准
        {
            goodsConfigRsrvStr2Arr2 = "1";
            if (agenFeeTemp > agenFee)
            {
                agenFee = agenFeeTemp;
                goodsConfigRsrvStr2Arr2 = "3";
            }
        }

        IData returnData = new DataMap();
        returnData.put("RSRV_STR2", goodsConfigRsrvStr2Arr2);
        returnData.put("AGENT_FEE", agenFee);
        return returnData;
    }

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

        if ("YX04".equals(compnTypde) || "YX10".equals(compnTypde))
            return; // YX10一体化合约不处理

        SaleGoodsTradeData saleGoodsTradeData = (SaleGoodsTradeData) dealPmtd;

        String goodsId = saleGoodsTradeData.getElementId();
//        IDataset goodsInfos = SaleGoodsInfoQry.querySaleGoodsByGoodsId(goodsId);
//        IData goodsData = goodsInfos.getData(0);
        IData goodsData = UpcCall.qryOfferComChaTempChaByCond(goodsId, BofConst.ELEMENT_TYPE_CODE_SALEGOODS);//datas.getData(0);

        String resTypeCode = goodsData.getString("GOODS_TYPE_CODE", "");

        if (!"4".equals(resTypeCode))
            return;

        String goodsConfigRsrvStr2 = goodsData.getString("RSRV_STR2", ""); // CHECK_AGENTFEE_TAG|酬金标识|约定消费额(分)|月份

        if (StringUtils.isBlank(goodsConfigRsrvStr2) || !goodsConfigRsrvStr2.startsWith("CHECK_AGENTFEE_TAG"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机活动时，酬金标识位参数配置错误！");
        }

        String[] goodsConfigRsrvStr2Arr = goodsConfigRsrvStr2.split("\\|");

        if (goodsConfigRsrvStr2Arr.length < 2)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机活动时，酬金标识位参数配置错误！");
        }

        String goodsConfigRsrvStr2Arr2 = goodsConfigRsrvStr2Arr[1]; // 酬金标识位

        if ("|1|2|3|4|5|".indexOf(goodsConfigRsrvStr2Arr2) < 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机活动时，酬金标识位参数配置错误！");
        }

        String goodsConfigRsrvStr2Arr3 = "0"; // 约定消费额（单位：分）
        String goodsConfigRsrvStr2Arr4 = "0"; // 约定消费月份

        if (goodsConfigRsrvStr2Arr.length >= 4)
        {
            goodsConfigRsrvStr2Arr3 = goodsConfigRsrvStr2Arr[2];
            goodsConfigRsrvStr2Arr4 = goodsConfigRsrvStr2Arr[3];
        }
        else
        {
            if ("|2|3|4|5|".indexOf(goodsConfigRsrvStr2Arr2) > 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "购机活动时，酬金标识位参数配置错误！");
            }
        }

        String agenFee = saleGoodsTradeData.getRsrvStr7();
        if (StringUtils.isBlank(agenFee))
            agenFee = "0";

        int agentFeeInt = Integer.parseInt(agenFee);

        IData returnData = new DataMap();

        if ("2".equals(goodsConfigRsrvStr2Arr2) || "4".equals(goodsConfigRsrvStr2Arr2))
        {
            returnData = computeAgentFeeBy2And4(goodsConfigRsrvStr2Arr2, goodsConfigRsrvStr2Arr3, goodsConfigRsrvStr2Arr4, agentFeeInt);
            agentFeeInt = returnData.getInt("AGENT_FEE");
            goodsConfigRsrvStr2Arr2 = returnData.getString("RSRV_STR2");
        }
        else if ("3".equals(goodsConfigRsrvStr2Arr2) || "5".equals(goodsConfigRsrvStr2Arr2))
        {
            returnData = computeAgentFeeBy3And5(goodsConfigRsrvStr2Arr2, goodsConfigRsrvStr2Arr3, goodsConfigRsrvStr2Arr4, agentFeeInt);
            agentFeeInt = returnData.getInt("AGENT_FEE");
            goodsConfigRsrvStr2Arr2 = returnData.getString("RSRV_STR2");
        }

        saleGoodsTradeData.setRsrvNum1(goodsConfigRsrvStr2Arr2); // 酬金标识位
        saleGoodsTradeData.setRsrvNum2(goodsConfigRsrvStr2Arr3); // 约定消费额（单位：分）
        saleGoodsTradeData.setRsrvNum3(goodsConfigRsrvStr2Arr4); // 酬金标识位
        saleGoodsTradeData.setRsrvStr7(String.valueOf(agentFeeInt));
    }

}
