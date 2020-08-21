
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VipExchangeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata.VipExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata.VipExchangeReqData;

public class BuildVipExchangeReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        VipExchangeReqData reqData = (VipExchangeReqData) brd;

        String ruleCheck = param.getString("X_RULECHECK", "");// 1：校验 其它：不校验
        reqData.setRuleCheck(ruleCheck);

        IDataset vipExchangeList = new DatasetList(param.getString("VIP_EXCHANGE_LIST", "[]"));

        if (IDataUtil.isEmpty(vipExchangeList))
        {
            // 礼品兑换数据为空
            CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_7);
        }

        for (int i = 0, size = vipExchangeList.size(); i < size; i++)
        {
            IData vipExchangeData = vipExchangeList.getData(i);
            String giftTypeCode = vipExchangeData.getString("PARAM_CODE", "");
            String giftName = vipExchangeData.getString("PARAM_NAME", "");
            String giftId = vipExchangeData.getString("PARA_CODE1", "");
            String giftFee = vipExchangeData.getString("PARA_CODE2", "0");

            VipExchangeData vipExchgData = new VipExchangeData();
            vipExchgData.setGiftTypeCode(giftTypeCode);
            vipExchgData.setGiftName(giftName);
            vipExchgData.setGiftId(giftId);
            vipExchgData.setGiftFee(giftFee);

            reqData.addVipExchangeData(vipExchgData);
        }

    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        // TODO Auto-generated method stub
        return new VipExchangeReqData();
    }

}
