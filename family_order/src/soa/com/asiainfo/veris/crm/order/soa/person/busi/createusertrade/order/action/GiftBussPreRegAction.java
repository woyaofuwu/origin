
package com.asiainfo.veris.crm.order.soa.person.busi.createusertrade.order.action;

import java.util.List;

import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
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

public class GiftBussPreRegAction implements ITradeAction
{

    public void executeAction(BusiTradeData btd) throws Exception
    {
        String strInModeCode = CSBizBean.getVisit().getInModeCode();
        String strTradeTypeCode = btd.getTradeTypeCode();
        String strEparchyCode = CSBizBean.getTradeEparchyCode();
        String strProductId = btd.getRD().getUca().getProductId();
        String strPackageId = btd.getMainTradeData().getRsrvStr2();
        String strRsrvStr1 = "";
        String strRsrvStr2 = "";
        String strRsrvStr3 = "";
        String strParaCode5 = "";

        /* 空中开户不预登记 */
        if (strTradeTypeCode.equals("10") && strInModeCode.equals("N"))
        {
            return;
        }

        if (!"10".equals(strTradeTypeCode) && !"240".equals(strTradeTypeCode))
        {
            return;
        }
        if ("10".equals(strTradeTypeCode))
        {
            // 批量开户
            String strBatchCode = btd.getMainTradeData().getBatchId();
            if (strBatchCode == null)
                strBatchCode = "";

            if (!strBatchCode.equals("") && !strEparchyCode.equals("0746"))
                return;

            int count1 = 0, count2 = 0;
            List<DiscntTradeData> discntTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
            for (DiscntTradeData discntTradeData : discntTradeDatas)
            {
                if (BofConst.MODIFY_TAG_ADD.equals(discntTradeData.getModifyTag()))
                {
                    IDataset commparaInfos = new DatasetList();// CommparaInfoQry.getCommparaInfoByParacode8("CSM",
                    // "3225", strTradeTypeCode, strProductId,
                    // discntTradeData.getElementId(), strEparchyCode);
                    if (IDataUtil.isNotEmpty(commparaInfos))
                    {
                        strRsrvStr2 = commparaInfos.getData(0).getString("PARA_CODE2", ""); // 业务类型
                        strRsrvStr1 = commparaInfos.getData(0).getString("PARA_CODE3", ""); // 参与原因
                        strRsrvStr3 = commparaInfos.getData(0).getString("PARA_CODE4", ""); // 奖品档次
                        strParaCode5 = commparaInfos.getData(0).getString("PARA_CODE5", ""); // 批量方式
                        count1 = 1;
                        break;
                    }
                }
            }
            if (count1 == 0)
            {
                int nAdvPay = Integer.parseInt(btd.getMainTradeData().getAdvancePay());
                IDataset commparaInfos = new DatasetList();// CommparaInfoQry.getCommparaInfoByParacode8("CSM", "3225",
                // strTradeTypeCode, strProductId, "1", strEparchyCode);
                if (commparaInfos.size() > 0)
                {
                    for (int i = 0; i < commparaInfos.size(); i++)
                    {
                        int nMinAdvPay = 0, nMaxAdvPay = 0;
                        nMinAdvPay = commparaInfos.getData(i).getInt("PARA_CODE6");
                        nMaxAdvPay = commparaInfos.getData(i).getInt("PARA_CODE7");
                        // 判断预付话费最低金额
                        if (nMinAdvPay <= nAdvPay && nMaxAdvPay >= nAdvPay)
                        {
                            strRsrvStr2 = commparaInfos.getData(i).getString("PARA_CODE2", ""); // 业务类型
                            strRsrvStr1 = commparaInfos.getData(i).getString("PARA_CODE3", ""); // 参与原因
                            strRsrvStr3 = commparaInfos.getData(i).getString("PARA_CODE4", ""); // 奖品档次
                            strParaCode5 = commparaInfos.getData(i).getString("PARA_CODE5", ""); // 批量方式
                            count2 = 1;
                            break;
                        }

                    }
                }
            }
            if (count1 == 0 && count2 == 0)
                return;
        }

        if (strRsrvStr1.equals("") || strRsrvStr2.equals("") || strRsrvStr3.equals(""))
        {
            return;
        }
        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setUserId(btd.getRD().getUca().getUserId());
        otherTradeData.setRsrvValueCode("LPZS");
        otherTradeData.setRsrvValue(btd.getRD().getUca().getSerialNumber());
        otherTradeData.setRsrvStr1(strRsrvStr1);
        otherTradeData.setRsrvStr2(strRsrvStr2);
        otherTradeData.setRsrvStr3(strRsrvStr3);
        otherTradeData.setRsrvStr6("0"); // 表示未兑奖
        otherTradeData.setRsrvStr7(strEparchyCode);
        otherTradeData.setRsrvStr8(CSBizBean.getVisit().getStaffId());
        otherTradeData.setRsrvStr9(CSBizBean.getVisit().getDepartId());
        otherTradeData.setRsrvStr10(btd.getRD().getUca().getSerialNumber());
        otherTradeData.setRsrvStr27(strProductId); // 产品编码
        otherTradeData.setRsrvStr28(strPackageId);// 包编码
        otherTradeData.setRsrvStr29("1");// 表示预登记
        otherTradeData.setRsrvStr30(btd.getTradeId());// 预登记trade_id
        otherTradeData.setStartDate(btd.getRD().getAcceptTime());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTradeData.setInstId(SeqMgr.getInstId());
        btd.add(btd.getRD().getUca().getSerialNumber(), otherTradeData);
    }

}
