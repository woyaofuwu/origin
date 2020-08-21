
package com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.infomanage.order.requestdata.CustomerTitleRequestData;

public class CustomerTitleTrade extends BaseTrade implements ITrade
{

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        // 插入other表动作
        CustomerTitleRequestData reqData = (CustomerTitleRequestData) btd.getRD();
        IDataset userOtherData = UserOtherInfoQry.getUserOtherUserId(btd.getRD().getUca().getUserId(), "CTHN", null);
        if (IDataUtil.isNotEmpty(userOtherData))
        {
            IData otherInfo = new DataMap();
            if (IDataUtil.isNotEmpty(userOtherData))
            {
                if (IDataUtil.isNotEmpty(userOtherData.getData(0)))
                {
                    otherInfo = userOtherData.getData(0);
                }
            }
            else
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1097);
            }
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(btd.getRD().getUca().getUserId());
            otherTradeData.setRsrvValueCode("CTHN");
            otherTradeData.setRsrvValue(reqData.getCustTitle());

            otherTradeData.setStartDate(btd.getRD().getAcceptTime());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_UPD);
            otherTradeData.setInstId(otherInfo.getString("INST_ID"));

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }
        else
        {
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(btd.getRD().getUca().getUserId());
            otherTradeData.setRsrvValueCode("CTHN");
            otherTradeData.setRsrvValue(reqData.getCustTitle());

            otherTradeData.setStartDate(btd.getRD().getAcceptTime());
            otherTradeData.setEndDate(SysDateMgr.END_DATE_FOREVER);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setInstId(SeqMgr.getInstId());

            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

    }

}
