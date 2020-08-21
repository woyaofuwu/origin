
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VipExchangeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.VipExchangeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class VipExchangeFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        // 服务号码
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        // 兑换礼品类别：1-大客户生日礼品券兑换、2-大客户消费情况兑换、3-大客户生日话费直充、4-网上兑换电影票
        IDataUtil.chkParam(input, "GIFT_TYPE_CODE");
        // 兑换礼品编码
        IDataUtil.chkParam(input, "GIFT_ID");

        String serialNumber = input.getString("SERIAL_NUMBER");
        String giftTypeCode = input.getString("GIFT_TYPE_CODE");
        String giftId = input.getString("GIFT_ID");

        IDataset exchangeList = new DatasetList();
        UcaData mebUca = UcaDataFactory.getNormalUca(serialNumber);// 获取三户资料
        String userId = mebUca.getUserId();
        VipTradeData vipInfo = mebUca.getVip();// 获取VIP信息

        // 1标识不校验礼品是否符合大客户级别等判断，否则需要校验
        String isCheckGift = input.getString("X_RULECHECK", "");
        if (!StringUtils.equals(isCheckGift, "1"))
        {
            // 判断是否VIP大客户
            if (null == vipInfo)
            {
                // 您不是VIP大客户
                CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_1);
            }

            IDataset vipExchg = VipExchangeQry.getVipExchangeGifts(userId);
            if (IDataUtil.isNotEmpty(vipExchg))
            {
                // 您已办理大客户礼品兑换业务
                CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_2);
            }
        }

        IData vipExchangeInfo = new DataMap();
        IDataset giftParam = CommparaInfoQry.getCommparaAllCol("CSM", "362", giftTypeCode, mebUca.getUserEparchyCode());
        if (IDataUtil.isEmpty(giftParam))
        {
            // 获取礼品配置信息无数据
            CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_3);
        }

        for (int i = 0, size = giftParam.size(); i < size; i++)
        {
            IData gift = giftParam.getData(i);
            String paraCode1 = gift.getString("PARA_CODE1");
            if (StringUtils.equals(paraCode1, giftId))
            {
                vipExchangeInfo = gift;
                break;
            }
        }

        if (IDataUtil.isEmpty(vipExchangeInfo))
        {
            // 根据GIFT_TYPE_CODE和GIFT_ID获取礼品配置信息无数据
            CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_4);
        }

        if (!StringUtils.equals(isCheckGift, "1"))
        {
            String vipLevel = vipInfo.getVipClassId();

            // 1-大客户生日礼品券兑换、3-大客户生日话费直充,这两类兑换需要校验大客户级别
            if (StringUtils.equals(giftTypeCode, "1") || StringUtils.equals(giftTypeCode, "3"))
            {
                String paraVipClassId = vipExchangeInfo.getString("PARA_CODE3", "");
                if (!StringUtils.equals(vipLevel, paraVipClassId))
                {
                    // 您要兑换的礼品与您目前所属的大客户级别不一致
                    CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_5);
                }
            }

            // 2-大客户消费情况兑换，这类兑换需要判断消费情况
            if (StringUtils.equals(giftTypeCode, "2"))
            {
                long retFee = 0;
                IDataset lastMonthFee = AcctCall.queryLastMonthFee(serialNumber, userId);
                if (IDataUtil.isNotEmpty(lastMonthFee))
                {
                    IData feeData = lastMonthFee.getData(0);
                    retFee = feeData.getInt("ALL_RETURN_FEE", 0); // 单位：分
                }

                long minFee = vipExchangeInfo.getLong("PARA_CODE4", 0);
                long maxFee = vipExchangeInfo.getLong("PARA_CODE5", 0);

                if (!(retFee >= minFee && retFee < maxFee))
                {
                    // 您上月的消费情况intfFee/100元与您所兑换的礼品要求消费情况不一致！");
                    CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_5, retFee / 100);
                }
            }

            // 4-网上兑换电影票
            if (StringUtils.equals(giftTypeCode, "4"))
            {

            }
        }

        exchangeList.add(vipExchangeInfo);

        input.put("VIP_EXCHANGE_LIST", exchangeList);
    }

}
