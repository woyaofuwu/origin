/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.bankpaymentmanage.order.action;

import java.util.List;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.BankSubSignTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SmsTradeData;

/**
 * @CREATED by gongp@2014-7-16 修改历史 Revision 2014-7-16 下午04:49:05
 */
public class SubNumberSendSmsAction implements ITradeAction
{

    /*
     * (non-Javadoc)
     */
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
        // TODO Auto-generated method stub
        List<BankSubSignTradeData> list = btd.getTradeDatas(TradeTableEnum.TRADE_BANK_SUBSIGN);
        String tradeId = btd.getTradeId();
        String tradeTypecode = btd.getTradeTypeCode();

        if ("1393".equals(tradeTypecode))
        {

            BankSubSignTradeData subSignTd = list.get(0);

            String mainNum = subSignTd.getMainUserValue();

            String sms_content = "您好！您已被" + mainNum + "设置为易充值服务的副号码，通过此服务，主号码可随时为您充值。如您想取消关联，请登录www.10086.cn办理。中国移动";

            String subNum = subSignTd.getSubUserValue();

            IData userInfo = UcaInfoQry.qryUserInfoBySn(subNum);
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
            }// 4005:用户资料不存在！

            this.genRecipientTradeSms(btd, userInfo, sms_content, subNum);
        }
        else if ("1394".equals(tradeTypecode))
        {

            for (int i = 0, size = list.size(); i < size; i++)
            {

                BankSubSignTradeData subSignTd = list.get(i);

                String mainNum = subSignTd.getMainUserValue();

                String sms_content = "您好！您与主号" + mainNum + "的关联副号码关系已经解除。中国移动";

                String subNum = subSignTd.getSubUserValue();

                IData userInfo = UcaInfoQry.qryUserInfoBySn(subNum);
                if (IDataUtil.isEmpty(userInfo))
                {
                    CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_223);
                }// 4005:用户资料不存在！

                this.genRecipientTradeSms(btd, userInfo, sms_content, subNum);

            }
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
        std.setRemark("银行总对总");
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
