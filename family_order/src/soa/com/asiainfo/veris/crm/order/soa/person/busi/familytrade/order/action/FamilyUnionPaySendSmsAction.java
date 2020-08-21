/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.RelationTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

/**
 * @CREATED by gongp@2014-7-17 修改历史 Revision 2014-7-17 上午09:54:29
 */
public class FamilyUnionPaySendSmsAction implements ITradeAction
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        List<RelationTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_RELATION);

        for (int i = 0, size = list.size(); i < size; i++)
        {

            RelationTradeData relaTd = list.get(i);

            String mainNum = btd.getRD().getUca().getSerialNumber();
            String subNum = relaTd.getSerialNumberB();

            if ("2".equals(relaTd.getRoleCodeB()))
            {
                if (BofConst.MODIFY_TAG_ADD.equals(relaTd.getModifyTag()))
                {

                    String sms_content = "尊敬的客户：您已成为" + mainNum + "号码统一付费业务的副卡,从本月起,您的通信费用将由" + mainNum + "主卡统一支付.";

                    UcaData ucaData = UcaDataFactory.getNormalUca(subNum);

                    this.genRecipientTradeSms(btd, ucaData.getUser().toData(), sms_content, subNum);

                }
                else if (BofConst.MODIFY_TAG_DEL.equals(relaTd.getModifyTag()))
                {

                    String sms_content = "尊敬的客户：您的统一付费业务已被取消,下月起" + mainNum + "号码将停止为您支付通信费用.";

                    UcaData ucaData = UcaDataFactory.getNormalUca(subNum);

                    this.genRecipientTradeSms(btd, ucaData.getUser().toData(), sms_content, subNum);
                }
            }

        }

        String strContentNumClientAdd = "";
        String strContentNumClientDel = "";
        String mainSnFlag = "";

        String mainNum = btd.getRD().getUca().getSerialNumber();

        UcaData ucaData = UcaDataFactory.getNormalUca(mainNum);

        for (int i = 0, size = list.size(); i < size; i++)
        {

            RelationTradeData relaTd = list.get(i);

            // 主号码判断
            if ("1".equals(relaTd.getRoleCodeB()))
            {

                // 第一次办理短信
                if (BofConst.MODIFY_TAG_ADD.equals(relaTd.getModifyTag()) || (BofConst.MODIFY_TAG_UPD.equals(relaTd.getModifyTag())))
                {
                    mainSnFlag = "0";
                }
                // 取消全部家庭成员
                if ("1".equals(relaTd.getModifyTag()))
                {
                    mainSnFlag = "1";
                }
                continue;
            }
            String serialNumberB = relaTd.getSerialNumberB();

            if ("0".equals(relaTd.getModifyTag()) || "2".equals(relaTd.getModifyTag()))
            {
                strContentNumClientAdd += "".equals(strContentNumClientAdd) ? serialNumberB : "," + serialNumberB;
            }
            else
            {
                strContentNumClientDel += "".equals(strContentNumClientDel) ? serialNumberB : "," + serialNumberB;
            }
        }

        if ("".equals(mainSnFlag))
        {// 主号不是第一次办理也不是删除全部成员

            String strContent = "尊敬的客户：系统已";

            if (!"".equals(strContentNumClientAdd))
            {
                strContent += "成功增加（" + strContentNumClientAdd + "）为您的统一付费业务的副号码，从本月起，您将为他(们)支付通信费用。";
            }
            if (!"".equals(strContentNumClientDel))
            {
                strContent += "成功取消（" + strContentNumClientDel + "）号码作为您的统一付费业务的副卡号码，从下月起您不再为他(们)支付通信费用。";
            }
            if ("".equals(strContentNumClientDel) && "".equals(strContentNumClientDel))
            {

            }
            else
            {
                strContent += "若需增加或删除统一付费的副卡号码，请通过营业厅、10086增加副卡号码。";
                this.genRecipientTradeSms(btd, ucaData.getUser().toData(), strContent, mainNum);
            }

        }
        else if ("0".equals(mainSnFlag))
        {

            String strContent = "尊敬的客户，您已成功办理统一付费业务，";
            if (!"".equals(strContentNumClientAdd))
            {
                strContent += "且已成功增加（" + strContentNumClientAdd + "）为您的统一付费业务的副号码，从本月起，您将为他(们)支付通信费用。";
            }
            strContent += "若需增加或删除统一付费的副卡号码，请通过营业厅、10086增加副卡号码。";

            this.genRecipientTradeSms(btd, ucaData.getUser().toData(), strContent, mainNum);

        }
        else if ("1".equals(mainSnFlag))
        {

            String strContent = "尊敬的客户，您的所有统一付费副卡已退出，系统已成功为您自动取消统一付费业务。";

            this.genRecipientTradeSms(btd, ucaData.getUser().toData(), strContent, mainNum);
        }

    }

    private void genRecipientTradeSms(BusiTradeData<BaseTradeData> btd, IData smsData, String content, String recvObject) throws Exception
    {
        SmsTradeData std = new SmsTradeData();

        std.setSmsNoticeId(SeqMgr.getSmsSendId());
        std.setEparchyCode(CSBizBean.getVisit().getStaffEparchyCode());
        std.setBrandCode(smsData.getString("BRAND_CODE", ""));
        std.setInModeCode(CSBizBean.getVisit().getInModeCode());
        std.setSmsNetTag("0");
        std.setChanId("11");
        std.setSendObjectCode("6");
        std.setSendTimeCode("1");
        std.setSendCountCode("1");
        std.setRecvObjectType("00");

        std.setRecvId(smsData.getString("USER_ID", "0"));
        std.setSmsTypeCode("20");
        std.setSmsKindCode("02");
        std.setNoticeContentType("0");
        std.setReferedCount("0");
        std.setForceReferCount("1");
        std.setForceObject("");
        std.setForceStartTime("");
        std.setForceEndTime("");
        std.setSmsPriority("50");
        std.setReferTime(SysDateMgr.getSysTime());
        std.setReferDepartId(CSBizBean.getVisit().getDepartId());
        std.setReferStaffId(CSBizBean.getVisit().getStaffId());
        std.setDealTime(SysDateMgr.getSysTime());
        std.setDealStaffid(CSBizBean.getVisit().getStaffId());
        std.setDealDepartid(CSBizBean.getVisit().getDepartId());
        std.setDealState("0");// 处理状态，0：未处理
        std.setRemark("");
        std.setRevc1(smsData.getString("REVC1", ""));
        std.setRevc2(smsData.getString("REVC2", ""));
        std.setRevc3(smsData.getString("REVC3", ""));
        std.setRevc4(smsData.getString("REVC4", ""));
        std.setMonth(SysDateMgr.getSysTime().substring(5, 7));
        std.setDay(SysDateMgr.getSysTime().substring(8, 10));
        std.setCancelTag("0");

        // 短信截取
        std.setNoticeContent(content);

        std.setRecvObject(recvObject);// 发送号码

        btd.add(btd.getRD().getUca().getUser().getSerialNumber(), std);
    }

}
