
package com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.trade;

import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ReturnActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.SaleservParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.ReturnActiveBean;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.ReturnActiveExchangeReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.returnactive.order.requestdata.sub.ReturnActiveExchangeCardData;

public class ReturnActiveExchangeTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData bd) throws Exception
    {
        ReturnActiveExchangeReqData reqData = (ReturnActiveExchangeReqData) bd.getRD();
        UcaData uca = reqData.getUca();
        IData cond = new DataMap();

        List<ReturnActiveExchangeCardData> cardList = reqData.getCardList();
        if (cardList.isEmpty())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_913, "兑换的卡列表");
        }

        int giftMoney = 0;
        IDataset userGGCardInfoList = UserOtherInfoQry.getOtherInfoByIdPTag(uca.getUserId(), "RAGGCARD", "0");
        for (int i = 0, size = userGGCardInfoList.size(); i < size; i++)
        {
            IData userGGCardInfo = userGGCardInfoList.getData(i);
            String cardNo = userGGCardInfo.getString("RSRV_STR1", "");
            for (int j = 0, cardSize = cardList.size(); j < cardSize; j++)
            {
                ReturnActiveExchangeCardData cardData = cardList.get(j);
                if (cardNo.equals(cardData.getCardCode()))
                {
                    IDataset result = ReturnActiveBean.checkExchangeCard(uca.getUserId(), cardData.getCardCode(), cardData.getCardPassWord(), uca.getProductId());
                    IData ggcardInfo = result.getData(0);

                    String giftCode = cardData.getGiftCode();
                    result = SaleservParamInfoQry.queryByParaCode("RETURNACTIVE_GIFT", giftCode, CSBizBean.getUserEparchyCode());
                    if (IDataUtil.isEmpty(result))
                    {
                        CSAppException.apperr(ReturnActiveException.CRM_RETURNACTIVE_19, giftCode);
                    }
                    IData giftInfo = result.getData(0);

                    // 修改TF_R_GGCARD
                    cond.put("CARD_CODE", cardData.getCardCode());
                    cond.put("CARD_PASS_WORD", cardData.getCardPassWord());
                    cond.put("PROCESS_TAG", "1");
                    cond.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                    cond.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                    cond.put("ITEM_NAME", ggcardInfo.getString("ITEM_NAME", ""));
                    cond.put("GIFT_CODE", cardData.getGiftCode());
                    cond.put("GIFT_NAME", giftInfo.getString("PARA_NAME", ""));
                    cond.put("TERMINAL_CODE", cardData.getResCode());
                    cond.put("RSRV_DATE1", reqData.getAcceptTime());
                    Dao.executeUpdateByCodeCode("TF_RH_VALUECARD", "UPDATE_PROCESS_TAG_GGCARD", cond);

                    OtherTradeData otherTD = new OtherTradeData(userGGCardInfo);
                    otherTD.setRsrvDate1(reqData.getAcceptTime());
                    otherTD.setRsrvStr3(CSBizBean.getVisit().getStaffId());
                    otherTD.setRsrvStr4(CSBizBean.getVisit().getDepartId());
                    otherTD.setRsrvStr5(ggcardInfo.getString("ITEM_NAME", ""));// 奖项
                    otherTD.setRsrvStr6(giftInfo.getString("PARA_NAME", ""));// 奖品
                    otherTD.setRsrvStr7(cardData.getGiftCode());// 奖品编码
                    otherTD.setRsrvStr8(cardData.getResCode());// 奖品终端串码
                    otherTD.setRsrvStr9(cardData.getCardPassWord());// 卡密码
                    otherTD.setRsrvStr10(bd.getTradeId());// 兑换流水
                    otherTD.setProcessTag("1");// 已兑换
                    otherTD.setRemark(cardData.getRemark());
                    otherTD.setModifyTag(BofConst.MODIFY_TAG_UPD);

                    bd.add(uca.getSerialNumber(), otherTD);

                    // 如果是二维码，还要插条OTHER表
                    String resType = giftInfo.getString("PARA_CODE2");
                    if ("DMS".equals(resType))
                    {
                        decodeDimensionalSmsOther(bd, cardData);
                    }
                    if (!"".equals(resType) && !"R".equals(resType) && !"G".equals(resType) && !"O".equals(resType) && !"DMS".equals(resType))
                    {
                        giftMoney = giftMoney + Integer.parseInt(resType);
                    }

                    // 调资源接口
                    ResCall.ggCardReward(cardData.getCardCode(), cardData.getCardCode(), getVisit().getDepartId(), "427");
                }
            }
        }

        if (giftMoney > 0)
        {
            AcctCall.recvFee(uca.getSerialNumber(), bd.getTradeId(), String.valueOf(giftMoney), "15000", "57", "23", "");
        }

        MainTradeData mainTD = bd.getMainTradeData();
        mainTD.setRsrvStr1(String.valueOf(cardList.size()));
        mainTD.setRsrvStr2(String.valueOf(giftMoney));
    }

    private void decodeDimensionalSmsOther(BusiTradeData btd, ReturnActiveExchangeCardData cardData) throws Exception
    {
        UcaData uca = btd.getRD().getUca();
        String sysdate = btd.getRD().getAcceptTime();

        String giftCode = cardData.getGiftCode();
        IDataset results = CommparaInfoQry.getCommParas("CSM", "86", "RAGGCARD", giftCode, CSBizBean.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(results))
        {
            IData data = results.getData(0);
            int deCount = data.getInt("PARA_CODE15", 1);
            String passWd = getFixLenthString(6); // 获取6位随机数
            String md5PassWd = DigestUtils.md5Hex(passWd); // MD5加密，送给二维码平台的是MD5加密后的密码
            for (int i = 1; i < deCount; i++)
            {
                OtherTradeData otherTD = new OtherTradeData();
                otherTD.setUserId(uca.getUserId());
                otherTD.setInstId(SeqMgr.getInstId());
                otherTD.setRsrvValueCode("5556031" + i);
                otherTD.setRsrvValue("ACTIVE_DMS_SMS");
                otherTD.setRsrvStr1("5556031" + i);
                otherTD.setRsrvStr2(data.getString("PARA_CODE2", ""));// 接入系统号
                otherTD.setRsrvStr3(data.getString("PARA_CODE3", ""));// 业务商号
                otherTD.setRsrvStr4(data.getString("PARA_CODE4", ""));// 发送类型
                otherTD.setRsrvStr5(data.getString("PARA_CODE5", ""));// 凭证标题
                otherTD.setRsrvStr6(data.getString("PARA_CODE6", ""));// 连续密码错误最大次数
                otherTD.setRsrvStr7(data.getString("PARA_CODE7", ""));// 活动号
                otherTD.setRsrvStr8(data.getString("PARA_CODE8", ""));// 条码有效期(天)
                otherTD.setRsrvStr9(data.getString("PARA_CODE9", ""));// 活动可使用次数
                otherTD.setRsrvStr10(data.getString("PARA_CODE10", ""));// 活动可使用金额(元)
                otherTD.setRsrvStr11(data.getString("PARA_CODE11", ""));// 是否需要返回图片信息

                if ("1".equals(data.getString("PARA_CODE14", "")))
                {
                    otherTD.setRsrvStr13(passWd);// 二维码密码非MD5加密，下发业务办理短信时从这取来发送
                    otherTD.setRsrvStr14(md5PassWd);// 二维码密码
                }
                otherTD.setStartDate(sysdate);
                otherTD.setEndDate(SysDateMgr.getTheLastTime());

                String strValidDays = data.getString("PARA_CODE8", ""); // 条码有效期(天)
                String chnDays = "";
                if (StringUtils.isNotBlank(strValidDays))
                {
                    int seconds = (60 * 60 * 24) * (new Integer(strValidDays));
                    String strEndDate = SysDateMgr.getOtherSecondsOfSysDate(seconds);
                    strEndDate = SysDateMgr.decodeTimestamp(strEndDate, SysDateMgr.PATTERN_STAND_YYYYMMDD);
                    strEndDate = strEndDate + SysDateMgr.END_DATE;
                    otherTD.setEndDate(strEndDate);// 条码结束使用时间

                    // 有效截止时间转换成中文日期格式
                    chnDays = strEndDate.substring(0, 4) + "年" + strEndDate.substring(5, 7) + "月" + strEndDate.substring(8, 10) + "日";
                }

                // --------替换打印和短信内容配置中的变量-------------
                String printContent = data.getString("PARA_CODE24", "");
                printContent = printContent.replace("%END_DATE!", chnDays);
                otherTD.setRsrvStr12(printContent); // 活动打印内容
                otherTD.setRsrvStr18(giftCode);

                String smsContent = data.getString("PARA_CODE22", "") + data.getString("PARA_CODE23", "");
                smsContent = smsContent.replace("%END_DATE!", chnDays);
                smsContent = smsContent.replace("%PASS_WORD!", passWd);
                otherTD.setRsrvStr21(smsContent); // 短信内容

                // ---------判断下发的二位码是否包括彩信内容----------------------
                // 因为彩信的内容比较长，所以由一个单独的参数配置
                results = CommparaInfoQry.getCommParas("CSM", "87", "RAGGCARD", giftCode, CSBizBean.getTradeEparchyCode());
                if (IDataUtil.isNotEmpty(results))
                {
                    IData tmpData = results.getData(0);
                    String part1 = tmpData.getString("PARA_CODE20", "") + tmpData.getString("PARA_CODE23", "");
                    otherTD.setRsrvStr25(part1.replace("%END_DATE!", chnDays).replace("%PASS_WORD!", passWd)); // 彩信内容part1
                    String part2 = tmpData.getString("PARA_CODE21", "") + tmpData.getString("PARA_CODE24", "");
                    otherTD.setRsrvStr26(part2.replace("%END_DATE!", chnDays).replace("%PASS_WORD!", passWd)); // 彩信内容part2
                    String part3 = tmpData.getString("PARA_CODE22", "") + tmpData.getString("PARA_CODE25", "");
                    otherTD.setRsrvStr27(part3.replace("%END_DATE!", chnDays).replace("%PASS_WORD!", passWd)); // 彩信内容part3
                }

                otherTD.setStaffId(getVisit().getStaffId());
                otherTD.setDepartId(getVisit().getDepartId());
                otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
                otherTD.setRemark("刮刮卡兑奖发送二维码信息");
                btd.add(uca.getSerialNumber(), otherTD);
            }
        }
    }

    private String getFixLenthString(int strLength)
    {
        Random rm = new Random();
        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }
}
