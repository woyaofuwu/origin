
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 办理特殊产品 限制了携号转网，则登记特殊台账
 * 
 * @author chenzm
 */
public class LimitTransAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        IDataset commparaInfos = CommparaInfoQry.getCommparaAllCol("CSM", "3719", btd.getTradeTypeCode(), btd.getRD().getUca().getUserEparchyCode());
        if (IDataUtil.isEmpty(commparaInfos))
        {
            return;
        }

        boolean isExist = false;
        String paraCode1 = "";
        String paraCode3 = "";
        List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        for (DiscntTradeData discntTradeData : discntTradeDatas)
        {
            if (!isExist)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
                {
                    for (int i = 0; i < commparaInfos.size(); i++)
                    {
                        paraCode1 = commparaInfos.getData(i).getString("PARA_CODE1");
                        paraCode3 = commparaInfos.getData(i).getString("PARA_CODE3");
                        if (discntTradeData.getElementId().equals(paraCode1))
                        {
                            isExist = true;
                            break;
                        }
                    }
                }
            }

            if (isExist)
            {
                break;
            }
        }
        if (isExist)
        {
            UcaData uca = btd.getRD().getUca();
            String startDateChanged = btd.getRD().getAcceptTime();
            String endDateChanged = SysDateMgr.endDateOffset(startDateChanged, paraCode3, "3");
            OtherTradeData otherTradeData = new OtherTradeData();
            otherTradeData.setUserId(uca.getUserId());
            otherTradeData.setRsrvValueCode("LEAVE");
            otherTradeData.setRsrvValue("限制携号转网标志");
            otherTradeData.setRsrvStr1(btd.getRD().getUca().getProductId());// 产品
            otherTradeData.setRsrvStr2(""); // 包编码
            otherTradeData.setRsrvStr3(paraCode1);// 资费编码
            otherTradeData.setRsrvStr4("A"); // 状态标志
            otherTradeData.setRsrvStr5(btd.getTradeTypeCode()); // 业务类型
            otherTradeData.setRsrvStr7(CSBizBean.getTradeEparchyCode());
            otherTradeData.setRsrvStr8(CSBizBean.getVisit().getStaffId());
            otherTradeData.setRsrvStr9(CSBizBean.getVisit().getStaffId());
            otherTradeData.setRsrvStr10(uca.getSerialNumber());
            otherTradeData.setRsrvStr30(btd.getTradeId());
            otherTradeData.setInstId(SeqMgr.getInstId());
            otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTradeData.setStartDate(startDateChanged);
            otherTradeData.setEndDate(endDateChanged);
            otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTradeData.setRemark("携号转网");
            btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
        }

    }

}
