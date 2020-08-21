
package com.asiainfo.veris.crm.order.soa.frame.bof.builder.fee.impl;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.fee.IDealFeeInfos;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.PayMoneyData;

public class DealFeeInfosImpl implements IDealFeeInfos
{

    public void setFeeInfos(IData map, BaseReqData reqData) throws Exception
    {
        List<FeeData> feeList = new ArrayList<FeeData>();
        List<PayMoneyData> pyaMoneyList = new ArrayList<PayMoneyData>();

        // 解析X_TRADE_FEESUB
        if (map.containsKey("X_TRADE_FEESUB"))
        {
            IDataset tradeFeeSubList = new DatasetList(map.getString("X_TRADE_FEESUB", "[]"));
            for (int i = 0; i < tradeFeeSubList.size(); i++)
            {
                IData tradeFeeSub = tradeFeeSubList.getData(i);

                FeeData feeData = new FeeData();
                feeData.setFeeMode(tradeFeeSub.getString("FEE_MODE"));
                feeData.setDiscntGiftId(tradeFeeSub.getString("DISCNT_GIFT_ID"));
                feeData.setFeeTypeCode(tradeFeeSub.getString("FEE_TYPE_CODE"));
                feeData.setFee(tradeFeeSub.getString("FEE"));
                feeData.setOldFee(tradeFeeSub.getString("OLDFEE"));
                feeList.add(feeData);
            }
        }

        // 解析X_TRADE_PAYMONEY
        if (map.containsKey("X_TRADE_PAYMONEY"))
        {
            IDataset tradePayMoneyList = new DatasetList(map.getString("X_TRADE_PAYMONEY", "[]"));
            for (int j = 0; j < tradePayMoneyList.size(); j++)
            {
                IData tradePayMoney = tradePayMoneyList.getData(j);

                PayMoneyData payMoneyData = new PayMoneyData();
                payMoneyData.setPayMoneyCode(tradePayMoney.getString("PAY_MONEY_CODE"));
                payMoneyData.setMoney(tradePayMoney.getString("MONEY"));
                pyaMoneyList.add(payMoneyData);
            }
        }

        reqData.setFeeList(feeList);
        reqData.setPayMoneyList(pyaMoneyList);
    }
}
