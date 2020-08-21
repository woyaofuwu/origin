
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.tradefeereg.order.buildrequest;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.tradefeereg.order.requestdata.BaseTradefeeReqData;

public class BuildTradefeeReqData extends BaseBuilder implements IBuilder
{
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        IDataUtil.chkParam(param, "PAY_MONEY_CODE");
        IDataUtil.chkParam(param, "TRADEFEELISTS");

        BaseTradefeeReqData baseTradefeeReqData = (BaseTradefeeReqData) brd;

        baseTradefeeReqData.setPayMoneyCode(param.getString("PAY_MONEY_CODE")); // 付款方式

        List<FeeData> feeDatas = new ArrayList<FeeData>();
        List<PayMoneyData> payMoneyDatas = new ArrayList<PayMoneyData>();

        IDataset tradeFeeLists = new DatasetList(param.getString("TRADEFEELISTS")); // 费用串
        for (int i = 0; i < tradeFeeLists.size(); i++)
        {
            IData tradeFeeList = tradeFeeLists.getData(i);

            FeeData feeData = new FeeData();

            feeData.setFeeMode(tradeFeeList.getString("FEE_MODE"));// 费用大类，必传
            feeData.setFeeTypeCode(tradeFeeList.getString("FEE_TYPE_CODE")); // 费用小类，必传
            feeData.setFee(tradeFeeList.getString("FEE")); // 费用，必传
            feeData.setOldFee(tradeFeeList.getString("OLDFEE", tradeFeeList.getString("FEE"))); // 原费用，费用可能会减免，如果没传，则跟费用一致
            feeData.setDiscntGiftId(tradeFeeList.getString("DISCNT_GIFT_ID", "0"));
            feeDatas.add(i, feeData);
        }

        baseTradefeeReqData.setFeeList(feeDatas);

        // 原账务接口，会传入trade_id
        if (param.containsKey("TRADE_ID"))
        {
            baseTradefeeReqData.setTradeId(param.getString("TRADE_ID"));
        }
    }

    public UcaData buildUcaData(IData param) throws Exception
    {
        UcaData uca = UcaDataFactory.getUcaByUserId(param.getString("USER_ID"));

        return uca;
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new BaseTradefeeReqData();
    }
}
