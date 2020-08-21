package com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.order.action.finish;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;



/**
 * 权益使用短信通知
 * @author 梁端刚
 * @version V1.0
 * @date 2020/1/6 10:10
 */
public class BenefitSmsNotictAction implements ITradeFinishAction {

    @Override
    public void executeAction(IData mainTrade) throws Exception {
        String rightId=mainTrade.getString("RSRV_STR1");
        String discntCode=mainTrade.getString("RSRV_STR2");
        if(PersonConst.BENEFIT_AIRPORT.equals(rightId)&&PersonConst.BENEFIT_AIRPORT_FREE_PARKING.equals(discntCode)){
            sendMsgForFreeParking(mainTrade);
        }
    }

    /**
     * 免费停车权益使用出入场通知短信
     * @param mainTrade
     */
    private void sendMsgForFreeParking(IData mainTrade) throws Exception {
        /**
         * （一）进场短信内容为：【全球通尊享权益提醒】尊敬的全球通客户，您好！欢迎您进入全球通尊享停车位，您可享受**次免费停车权益，
         * 每次停车24小时内扣减1次免费权益（超过后每24小时扣减1次）。【中国移动】 
         * （二）离场短信内容为：【全球通尊享权益提醒】尊敬的全球通客户，您好！您本次停车时长**时**分，使用了**次免费停车权益，
         * 剩余**次免费停车权益。您需在15分钟内离开停车场，若超出将按停车场计费规则收取停车费。【中国移动】 
         */

        String rightId=mainTrade.getString("RSRV_STR1");
        String discntCode=mainTrade.getString("RSRV_STR2");
        String relId=mainTrade.getString("RSRV_STR3");
        String userId=mainTrade.getString("USER_ID");
        String serialNumber=mainTrade.getString("SERIAL_NUMBER");
        String modifyTag=mainTrade.getString("RSRV_STR4");
        String acceptDate=mainTrade.getString("ACCEPT_DATE");
        String startDate=mainTrade.getString("RSRV_STR5");
        String endDate=mainTrade.getString("RSRV_STR6");
        String tradeId=mainTrade.getString("TRADE_ID");

        BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
        IData param = new DataMap();
        if("0".equals(modifyTag)){
            //入场通知
            //查询权益使用次数
            param.clear();
            param.put("USER_ID", userId);
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("RIGHT_ID", rightId);
            param.put("DISCNT_CODE", discntCode);
            int remainUseNum = bean.queryRemainUseNum(param);
            String notictContent="【全球通尊享权益提醒】尊敬的全球通客户，您好！欢迎您进入全球通尊享停车位，" +
                    "您可享受"+remainUseNum+"次免费停车权益，每次停车24小时内扣减1次免费权益（超过后每24小时扣减1次）。【中国移动】";
            param.clear();
            param.put("USER_ID", userId);
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("NOTICE_CONTENT", notictContent);
            param.put("REMARK", "免费停车使用提醒");
            insertSms(param);
        }else if("2".equals(modifyTag)){
            //出场通知
            long useMins = SysDateMgr.minsBetweenForBenefit(startDate, endDate);
            String useTime=useMins/60+"小时";
            if(useMins%60!=0){
                useTime+=useMins%60+"分";
            }
            IDataset tradeOthers = TradeOtherInfoQry.getTradeOtherByTradeIdAndRsrvCodeDefaultRoute(tradeId, PersonConst.BENEFIT_RIGHT_USE_RECORD);
            String useNum = tradeOthers.first().getString("RSRV_VALUE");
            param.clear();
            param.put("USER_ID", userId);
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("RIGHT_ID", rightId);
            param.put("DISCNT_CODE", discntCode);
            long remainUseNum=bean.queryRemainUseNum(param);
            String notictContent="【全球通尊享权益提醒】尊敬的全球通客户，您好！您本次停车时长"+useTime+"，" +
                    "使用了"+useNum+"次免费停车权益，剩余"+remainUseNum+"次免费停车权益。您需在15分钟内离开停车场，若超出将按停车场计费规则收取停车费。【中国移动】";
            param.clear();
            param.put("USER_ID", userId);
            param.put("SERIAL_NUMBER", serialNumber);
            param.put("NOTICE_CONTENT", notictContent);
            param.put("REMARK", "免费停车使用提醒");
            insertSms(param);
        }
    }

    /**
     * 入短信中间表
     * @param input
     * @throws Exception
     */
    private void insertSms(IData input) throws Exception {
        // 拼短信表参数
        IData param = new DataMap();

        param.put("NOTICE_CONTENT", input.getString("NOTICE_CONTENT"));
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        param.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
        param.put("RECV_ID", input.getString("USER_ID"));
        param.put("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId());
        param.put("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId());

        param.put("REMARK", input.getString("REMARK","权益使用短信提醒"));
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        param.put("SMS_NOTICE_ID", seq_id);
        param.put("PARTITION_ID", seq_id % 1000);
        param.put("SEND_COUNT_CODE", "1");
        param.put("REFERED_COUNT", "0");
        param.put("CHAN_ID", "11");
        param.put("SMS_NET_TAG", "0");
        param.put("RECV_OBJECT_TYPE", "00");
        param.put("SMS_TYPE_CODE", "20");//20用户办理业务通知
        param.put("SMS_KIND_CODE", "02");//02短信通知
        param.put("NOTICE_CONTENT_TYPE", "0");//0指定内容发送
        param.put("FORCE_REFER_COUNT", "1");
        param.put("FORCE_START_TIME", SysDateMgr.getSysTime());//指定起始时间
        param.put("FORCE_OBJECT", "10086");
        param.put("SMS_PRIORITY", "50");
        param.put("DEAL_STATE", "15");// 处理状态，0：已处理，15未处理
        param.put("SEND_TIME_CODE", "1");
        param.put("SEND_OBJECT_CODE", "6");
        param.put("REFER_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_TIME", SysDateMgr.getSysTime());
        param.put("MONTH", SysDateMgr.getCurMonth());
        param.put("DAY", SysDateMgr.getCurDay());
        Dao.insert("TI_O_SMS", param);
    }
}
