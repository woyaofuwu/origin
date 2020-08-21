/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.trade;

import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.filteincomephone.order.requestdata.FilteIncomePhoneSendSMSTradeReqData;

/**
 * @CREATED by gongp@2014-4-28 修改历史 Revision 2014-4-28 上午11:20:30
 */
public class FilteIncomePhoneSendSMSTradeTrade extends BaseTrade implements ITrade
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        // TODO Auto-generated method stub

        FilteIncomePhoneSendSMSTradeReqData reqData = (FilteIncomePhoneSendSMSTradeReqData) bd.getRD();

        MainTradeData mainTD = bd.getMainTradeData();

        mainTD.setRsrvStr3("1");

        mainTD.setRsrvStr9(reqData.getOpenfuncRadio());

        String modify_tag = "0";

        if ("2".equals(reqData.getOpenfuncRadio()))
        {
            modify_tag = "1";
            this.genSendSMSTradeOther(bd, modify_tag);
        }
        else if ("1".equals(reqData.getOpenfuncRadio()))
        {
            modify_tag = "0";
            this.genSendSMSTradeOther(bd, modify_tag);
        }

    }

    public void genSendSMSTradeOther(BusiTradeData bd, String modifyTag) throws Exception
    {

        FilteIncomePhoneSendSMSTradeReqData reqData = (FilteIncomePhoneSendSMSTradeReqData) bd.getRD();

        OtherTradeData td = new OtherTradeData();

        if ("0".equals(modifyTag))
        {
            td.setStartDate(SysDateMgr.getSysTime());
            td.setEndDate(SysDateMgr.END_DATE_FOREVER);
            td.setUserId(reqData.getUca().getUserId());
            td.setRsrvValueCode("271");
            td.setRsrvValue("开通来电拒接短信提醒功能");
            td.setInstId(SeqMgr.getInstId());
            td.setStaffId(CSBizBean.getVisit().getStaffId());
            td.setDepartId(CSBizBean.getVisit().getDepartId());
        }
        else
        {
            td = getUserFilterPhoneSmsData(reqData.getUca().getUserId());
            if (td == null)
            {
                return;
            }
            td.setRsrvValue("关闭来电拒接短信提醒功能");
            td.setEndDate(SysDateMgr.getSysTime());
            td.setRsrvStr1(reqData.getTradeId());
        }

        td.setModifyTag(modifyTag);

        bd.add(reqData.getUca().getSerialNumber(), td);
    }

    /**
     * 得到用户来电拒接短信提醒信息
     * 
     * @param user_id
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-5-16
     */
    private OtherTradeData getUserFilterPhoneSmsData(String user_id) throws Exception
    {

        IDataset dataset = UserOtherInfoQry.getOtherInfoByCodeUserId(user_id, "271");
        OtherTradeData td = null;

        if (dataset.size() > 0)
        {
            return new OtherTradeData(dataset.getData(0));
        }

        return td;
    }

}
