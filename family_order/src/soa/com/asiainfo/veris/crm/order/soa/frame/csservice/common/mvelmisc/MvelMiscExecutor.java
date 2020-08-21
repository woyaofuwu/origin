/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc;

import java.util.List;

import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.RegTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.FeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;

/***
 * 
 */
public class MvelMiscExecutor
{

    public static String getContractSaleDetail(RegTradeData regTradeData) throws Exception
    {
        String strContent = "";
        int advanceFee = 0;// 预存款
        int operFee = 0;// 购机款
        int giftFee = 0;// 赠款
        double tmpFee = 0;
        double averagereturn = 0;// 月返还金额
        double lastmonthreturn = 0;// 最后一个月返还金额
        String contractStartDate = "";// 合约开始时间
        String contractEndDate = "";// 合约结束时间
        String returnstr = "";// 话费返还描述
        int consumeLevel = 0;// 客户承若最低消费
        int months = 0;// 合约期

        List<FeeTradeData> tradefeeSubList = regTradeData.get("TF_B_TRADEFEE_SUB");
        for (int i = tradefeeSubList.size() - 1; i >= 0; i--)
        {
            FeeTradeData feeTradeData = tradefeeSubList.get(i);
            if (feeTradeData.getFeeMode().equals("2"))
            {
                advanceFee += Integer.parseInt(feeTradeData.getFee());
            }
            else if (feeTradeData.getFeeMode().equals("0"))
            {
                operFee += Integer.parseInt(feeTradeData.getFee());
            }
        }

        if (advanceFee == 0)
        {
            List<GiftFeeTradeData> giftFeeList = regTradeData.get("TF_B_TRADEFEE_GIFTFEE");
            for (int i = giftFeeList.size() - 1; i >= 0; i--)
            {
                GiftFeeTradeData giftFeeData = giftFeeList.get(i);
                giftFee += Integer.parseInt(giftFeeData.getFee());
            }
            tmpFee = giftFee / 100.0;
        }
        else
        {
            tmpFee = advanceFee / 100.0;
        }

        // 获取承若合约期
        List<AttrTradeData> attrTradeDatalist = regTradeData.get("TF_B_TRADE_ATTR");
        for (AttrTradeData attrTradeData : attrTradeDatalist)
        {
            if (attrTradeData.getAttrCode().equals("ITEM_END_OFFSET"))
            {
                months = Integer.parseInt(attrTradeData.getAttrValue());
                break;
            }
        }

        // 合约开始时间和结束时间
        List<SaleActiveTradeData> saleActiveList = regTradeData.get("TF_B_TRADE_SALE_ACTIVE");
        SaleActiveTradeData saleActive = saleActiveList.get(0);
        contractStartDate = saleActive.getStartDate();
        contractEndDate = saleActive.getEndDate();

        if (tmpFee % months != 0)
        {
            // 向上取整
            averagereturn = (int) tmpFee / months + 1;
            lastmonthreturn = tmpFee - averagereturn * (months - 1);

            if (lastmonthreturn < 0)
            {
                averagereturn = averagereturn - 1;
                lastmonthreturn = tmpFee - averagereturn * (months - 1);
            }
            returnstr = "话费月均返还额(末月除外)" + averagereturn + "元，末月返还额" + String.format("%3.2f", lastmonthreturn) + "元";
        }
        else
        {
            averagereturn = tmpFee / months;
            returnstr = "话费月均返还额" + averagereturn + "元";
        }

        // 客户承诺最低消费
        for (AttrTradeData attrTradeData : attrTradeDatalist)
        {
            if (attrTradeData.getAttrCode().equals("MONEY"))
            {
                consumeLevel = Integer.parseInt(attrTradeData.getAttrValue());
                break;
            }
        }

        if (advanceFee > 0)
        {
            strContent = "合约零售价：" + (advanceFee + operFee) / 100.0 + "元，客户承诺最低消费" + consumeLevel / 100.0 + "元，客户承诺捆绑期" + months + "月，" + "客户实际购机价格" + operFee / 100.0 + "元，客户预存话费" + advanceFee / 100.0 + "元，" + returnstr;
        }
        else
        {
            strContent = "客户承诺最低消费" + consumeLevel / 100.0 + "元，客户承诺捆绑期" + months + "月，" + "赠送客户话费" + giftFee / 100.0 + "元，" + returnstr;
        }

        strContent += "，合约生效日期：" + contractStartDate + "，合约截止时期：" + contractEndDate + "。";

        return strContent;
    }
}
