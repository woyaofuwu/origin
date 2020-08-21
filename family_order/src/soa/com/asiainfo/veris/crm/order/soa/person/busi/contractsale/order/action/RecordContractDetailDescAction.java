/***
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to You under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.action;

import java.util.List;

import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SaleActiveTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.person.busi.contractsale.order.requestdata.ContractSaleReqData;

/***
 * 
 */
public class RecordContractDetailDescAction implements ITradeAction
{

    /**
     * (non-Javadoc)
     * 
     * @see com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction#executeAction(com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        ContractSaleReqData req = (ContractSaleReqData) btd.getRD();
        UcaData uca = req.getUca();

        String contractConsumeDetailDesc = req.getContractConsumeDetailDesc();
        if (StringUtils.isNotBlank(contractConsumeDetailDesc))
        {
            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setUserId(uca.getUserId());
            otherTD.setRsrvValueCode("CONTRACT_DETAILDESC");
            otherTD.setRsrvValue("合约计划描述细节");
            otherTD.setRsrvStr30(contractConsumeDetailDesc);
            otherTD.setRsrvStr11(req.getDiscount());
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setStartDate(req.getStartDate());
            otherTD.setEndDate(req.getEndDate());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);

            // 记活动的INST_ID，方便后续关联
            List<SaleActiveTradeData> saleActiveTDList = btd.getTradeDatas(TradeTableEnum.TRADE_SALEACTIVE);
            otherTD.setRsrvStr1(saleActiveTDList.get(0).getInstId());

            btd.add(uca.getSerialNumber(), otherTD);
        }
    }

}
