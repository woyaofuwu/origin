
package com.asiainfo.veris.crm.order.soa.person.busi.welfare.utils;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.welfare.consts.WelfareConstants;
import com.asiainfo.veris.crm.iorder.pub.welfare.exception.WelfareException;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.person.busi.welfare.order.requestdata.WelfareOfferRequestData;

import java.util.List;

/**
 * @Description 权益关联订购自有商品工具类
 * @Auther: zhenggang
 * @Date: 2020/7/6 20:36
 * @version: V1.0
 */
public class WelfareUtil
{
    /**
     * @Description: 找出正确的商品实例ID
     * @Param: [uca, relOfferCode, relOfferType]
     * @return: java.lang.String
     * @Author: zhenggang
     * @Date: 2020/7/6 20:08
     */
    public static String findCorrectInstId(WelfareOfferRequestData reqData, String welfareOfferCode, String offerCode, String offerType) throws Exception
    {
        String offerInsId = "";

        UcaData uca = reqData.getUca();

        List<OfferRelTradeData> offerRels = uca.getOfferRelsByRelUserId();

        if (ArrayUtil.isNotEmpty(offerRels))
        {
            for (OfferRelTradeData offerRel : offerRels)
            {
                String tmpOfferCode = offerRel.getOfferCode();// 优惠、服务、平台服务编码
                String tmpOfferType = offerRel.getOfferType();// 优惠、服务、平台服务类型

                if (!offerCode.equals(tmpOfferCode) || !offerType.equals(tmpOfferType))
                {
                    continue;
                }
                String relOfferCode = offerRel.getRelOfferCode();// 权益编码
                String relOfferType = offerRel.getRelOfferType();// Q
                String relOfferInsId = offerRel.getRelOfferInsId();// 主商品的实例ID
                if (!WelfareConstants.RelType.WEFFARE.getValue().equals(relOfferType) || !relOfferCode.equals(welfareOfferCode))
                {
                    // ELEMENTS里面要传权益包编码，这里要比较过滤。找到对应的权益包编码
                    continue;
                }

                // 如果为产品退订引起的，则需要找到这个产品关联的自有权益然后处理
                if (StringUtils.isNotEmpty(reqData.getDelMainOfferInsId()) && !reqData.getDelMainOfferInsId().equals(relOfferInsId))
                {
                    continue;
                }

                offerInsId = offerRel.getOfferInsId();
            }
        }

        if (StringUtils.isBlank(offerInsId))
        {
            String offerName = UpcCall.qryOfferNameByOfferTypeOfferCode(offerCode, offerType);
            CSAppException.apperr(WelfareException.CRM_WELFARE_6, "[" + offerCode + "]" + offerName);
        }
        return offerInsId;
    }
}
