/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.person.busi.exchangegoods.order.trade;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleGoodsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.person.busi.exchangegoods.order.requestdata.ExchangeGoodsReqData;

/***
 * 
 */
public class ExchangeGoodsTrade extends BaseTrade implements ITrade
{

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade#createBusiTradeData(com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        if (StringUtils.isBlank(bd.getRD().getPreType()) || "true".equals(bd.getRD().getIsConfirm()))
        {
            ExchangeGoodsReqData reqData = (ExchangeGoodsReqData) bd.getRD();
            UcaData uca = reqData.getUca();
            SaleActiveTradeData exchangeSaleActive = null;

            List<SaleActiveTradeData> saleactiveTDList = uca.getUserSaleActives();

            for (int i = saleactiveTDList.size() - 1; i >= 0; i--)
            {
                SaleActiveTradeData saleActiveTD = saleactiveTDList.get(i);
                String resCode = saleActiveTD.getRsrvStr23();
                if (reqData.getOldImei().equals(resCode))
                {
                    exchangeSaleActive = saleActiveTD.clone();
                    break;
                }
            }

            if (exchangeSaleActive == null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据老手机串号没有找到活动记录");
            }
            exchangeSaleActive.setModifyTag(BofConst.MODIFY_TAG_UPD);
            exchangeSaleActive.setRsrvStr3(exchangeSaleActive.getRsrvStr23());
            exchangeSaleActive.setRsrvStr23(reqData.getNewImei());

            bd.add(uca.getSerialNumber(), exchangeSaleActive);

            SaleGoodsTradeData exchangeSaleGoods = null;

            List<SaleGoodsTradeData> saleGoodsTDList = uca.getUserSaleGoods();
            for (int i = saleGoodsTDList.size() - 1; i >= 0; i--)
            {
                SaleGoodsTradeData saleGoodsTD = saleGoodsTDList.get(i);
                if (reqData.getOldImei().equals(saleGoodsTD.getResCode()))
                {
                    exchangeSaleGoods = saleGoodsTD.clone();
                    break;
                }
            }
            if (exchangeSaleGoods == null)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据老手机串号没有找到用户实物记录");
            }

            // 把老串号记录到主台账里
            bd.getMainTradeData().setRsrvStr1(exchangeSaleGoods.getResCode());

            exchangeSaleGoods.setResCode(reqData.getNewImei());
            exchangeSaleGoods.setModifyTag(BofConst.MODIFY_TAG_UPD);

            bd.add(uca.getSerialNumber(), exchangeSaleGoods);
        }
    }
}
