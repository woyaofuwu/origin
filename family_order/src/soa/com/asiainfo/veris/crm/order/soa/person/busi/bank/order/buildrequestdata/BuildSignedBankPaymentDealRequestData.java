
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.buildrequestdata;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata.SignedBankPaymentDealRequestData;

public class BuildSignedBankPaymentDealRequestData extends BaseBuilder implements IBuilder
{

    private static Logger logger = Logger.getLogger(BuildSignedBankPaymentDealRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception
    {

        SignedBankPaymentDealRequestData reqData = (SignedBankPaymentDealRequestData) brd;
        reqData.setPartitionId(data.getString("PARTITION_ID"));
        reqData.setOpenBank(data.getString("OPEN_BANK"));
        reqData.setBankCardNo(data.getString("BANK_CARD_NO"));
        reqData.setCardTypeCode(data.getString("CARD_TYPE_CODE"));
        reqData.setStartTime(data.getString("START_TIME"));
        reqData.setEndTime(data.getString("END_TIME"));
        reqData.setReckTag(data.getString("RECK_TAG"));
        reqData.setRemark(data.getString("REMARK"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new SignedBankPaymentDealRequestData();
    }

}
