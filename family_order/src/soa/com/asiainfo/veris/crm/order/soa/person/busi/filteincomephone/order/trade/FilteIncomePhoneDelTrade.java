/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneTradeReqData;

/**
 * @CREATED by gongp@2014-5-28 修改历史 Revision 2014-5-28 下午04:42:18
 */
public class FilteIncomePhoneDelTrade extends FilteIncomePhoneBaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub
        FilteIncomePhoneTradeReqData reqData = (FilteIncomePhoneTradeReqData) bd.getRD();
        MainTradeData mainTD = bd.getMainTradeData();

        String userId = reqData.getUca().getUserId();

        if (this.getEffectRejectNoCount(bd, userId) > 0)
        {
            mainTD.setRsrvStr8("0");// 发指令开户、销户的标志
            mainTD.setRsrvStr1("");// 主台帐表中记录拒接号码
            mainTD.setRsrvStr2("1");// 主台帐表中记录操作类型
            mainTD.setRsrvStr3("");// 不需要插入来电拒接短信提醒记录的标志

            dealMainTradeTag(mainTD, reqData);

            if ("2".equals(reqData.getOperType()))// 全删
            {
                mainTD.setRsrvStr2("2");// 主台帐表中记录操作类型

                geneTradeSvc(bd, BofConst.MODIFY_TAG_DEL);
                geneTradeDistcnt(bd, BofConst.MODIFY_TAG_DEL);
                genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_DEL);
                mainTD.setRsrvStr9("2");
                mainTD.setRsrvStr8("0");// 发指令开户、销户的标志
                IDataset delSnDataset = UserOtherInfoQry.getUserOther(userId, "1301");

                for (int i = 0, size = delSnDataset.size(); i < size; i++)
                {
                    IData dealData = delSnDataset.getData(i);

                    geneTradeOther(bd, dealData.getString("RSRV_STR1", ""), "全删", BofConst.MODIFY_TAG_DEL);
                }

            }
            else
            {
                geneTradeOther(bd, reqData.getRejectSn(), reqData.getRemark(), BofConst.MODIFY_TAG_DEL);

                if (this.getEffectRejectNoCount(bd, userId) == 0)
                {
                    geneTradeSvc(bd, BofConst.MODIFY_TAG_DEL);
                    geneTradeDistcnt(bd, BofConst.MODIFY_TAG_DEL);
                    genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_DEL);
                    mainTD.setRsrvStr9("2");// 1：接收来电通知 2：不接收来电通知
                    mainTD.setRsrvStr8("0");// 发指令开户、销户的标志
                }
                else
                {
                    if ("1".equals(reqData.getIsOpenSms()))
                    {
                        if (this.getTradeUserFilterPhoneSmsData(BofConst.MODIFY_TAG_ADD) == null)
                        {
                            genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_ADD);
                            mainTD.setRsrvStr9("1");// 1：接收来电通知 2：不接收来电通知
                        }
                    }
                    else
                    {
                        if (this.getTradeUserFilterPhoneSmsData(BofConst.MODIFY_TAG_DEL) == null)
                        {
                            genSendSMSTradeOther(bd, BofConst.MODIFY_TAG_DEL);
                            mainTD.setRsrvStr9("2");// 1：接收来电通知 2：不接收来电通知
                        }
                    }
                    mainTD.setRsrvStr8("1");
                }
                mainTD.setRsrvStr1(reqData.getRejectSn());
            }
        }
        else
        {

        }

    }
}
