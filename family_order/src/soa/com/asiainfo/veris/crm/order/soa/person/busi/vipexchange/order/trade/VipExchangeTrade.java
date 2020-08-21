
package com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VipExchangeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.GiftFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.VipTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata.VipExchangeData;
import com.asiainfo.veris.crm.order.soa.person.busi.vipexchange.order.requestdata.VipExchangeReqData;

public class VipExchangeTrade extends BaseTrade implements ITrade
{

    @Override
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        VipExchangeReqData reqData = (VipExchangeReqData) btd.getRD();

        List<VipExchangeData> vipExchangeDataList = reqData.getVipExchangeDataList();
        for (int i = 0, size = vipExchangeDataList.size(); i < size; i++)
        {
            VipExchangeData vipExchgData = vipExchangeDataList.get(i);
            String giftTypeCode = vipExchgData.getGiftTypeCode();
            String isSendSms = "0";// 是否发送二维码标识，0-不发送，1-发送

            if ("1".equals(giftTypeCode))
            {

            }
            if ("2".equals(giftTypeCode))
            {

            }
            // 兑换话费直充
            if ("3".equals(giftTypeCode))
            {
                dealTradeFeeExchange(btd, reqData, vipExchgData);
            }
            if ("4".equals(giftTypeCode))
            {

            }

            // 生成大客户兑换礼品发送二维码信息
            decodeDimensionalSmsOther(btd, reqData, vipExchgData, isSendSms);
            // 生成礼品兑换发送E拇指平台信息
            decodeEmzSmsOther(btd, reqData, vipExchgData, isSendSms);
            // 生成大客户兑换礼品台账
            genTradeExchange(btd, reqData, vipExchgData, isSendSms);
        }
    }

    /**
     * 生成兑换费用台帐
     * 
     * @param btd
     * @param reqData
     * @param vipExchgData
     * @throws Exception
     */
    private void dealTradeFeeExchange(BusiTradeData btd, VipExchangeReqData reqData, VipExchangeData vipExchgData) throws Exception
    {
        UcaData uca = reqData.getUca();
        String giftFee = vipExchgData.getGiftFee();

        GiftFeeTradeData giftFeeTD = new GiftFeeTradeData();
        giftFeeTD.setUserId(uca.getUserId());
        giftFeeTD.setFeeMode("2");
        giftFeeTD.setFeeTypeCode("1");
        giftFeeTD.setFee(giftFee);

        btd.add(uca.getSerialNumber(), giftFeeTD);
    }

    /**
     * 生成大客户兑换礼品发送二维码信息
     * 
     * @param btd
     * @param reqData
     * @param vipExchgData
     * @throws Exception
     */
    private void decodeDimensionalSmsOther(BusiTradeData btd, VipExchangeReqData reqData, VipExchangeData vipExchgData, String isSendSms) throws Exception
    {
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        String giftTypeCode = vipExchgData.getGiftTypeCode();
        String giftId = vipExchgData.getGiftId();

        IDataset commPara1 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "86", giftTypeCode, giftId, uca.getUserEparchyCode());

        if (IDataUtil.isNotEmpty(commPara1))
        {
            isSendSms = "1";
            IData para1 = commPara1.getData(0);
            String rsrvValueCode = para1.getString("PARA_CODE12");
            String serviceId = para1.getString("PARA_CODE13");

            if (StringUtils.isBlank(rsrvValueCode) || StringUtils.isBlank(serviceId))
            {
                // 从参数表获取RSRV_VALUE_CODE或者SERVICE_ID为空
                CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_8);
            }

            String endDate = SysDateMgr.getTheLastTime();

            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setUserId(uca.getUserId());
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setRsrvValueCode(rsrvValueCode);
            otherTD.setRsrvStr1(serviceId); // callpf约定使用service_id ？？？
            otherTD.setRsrvStr2(para1.getString("PARA_CODE2", ""));// 接入系统号
            otherTD.setRsrvStr3(para1.getString("PARA_CODE3", ""));// 业务商号
            otherTD.setRsrvStr4(para1.getString("PARA_CODE4", ""));// 发送类型
            otherTD.setRsrvStr5(para1.getString("PARA_CODE5", ""));// 凭证标题
            otherTD.setRsrvStr6(para1.getString("PARA_CODE6", ""));// 连续密码错误最大次数
            otherTD.setRsrvStr7(para1.getString("PARA_CODE7", ""));// 活动号
            otherTD.setRsrvStr8(para1.getString("PARA_CODE8", ""));// 条码有效期(天)
            otherTD.setRsrvStr9(para1.getString("PARA_CODE9", ""));// 活动可使用次数
            otherTD.setRsrvStr10(para1.getString("PARA_CODE10", ""));// 活动可使用金额(元)
            otherTD.setRsrvStr11(para1.getString("PARA_CODE11", ""));// 是否需要返回图片信息
            otherTD.setStartDate(sysdate);
            otherTD.setEndDate(endDate);

            // 若存在有效天数，则以当前时间+有效天等到日期当天的最后一秒;若不存在有效天数，则最大失效时间
            String validDays = para1.getString("PARA_CODE8", "");
            if (StringUtils.isNotBlank(validDays))
            {
                endDate = SysDateMgr.addDays(sysdate, Integer.parseInt(validDays)) + SysDateMgr.getEndTime235959();
                otherTD.setEndDate(endDate);
            }

            endDate = SysDateMgr.decodeTimestamp(endDate, SysDateMgr.PATTERN_CHINA_DATE);
            String printContent = para1.getString("PARA_CODE24", ""); // 打印内容
            printContent = StringUtils.replace(printContent, "%END_DATE!", endDate);
            otherTD.setRsrvStr12(printContent);

            String smsContent = para1.getString("PARA_CODE22", "") + para1.getString("PARA_CODE23", "");
            smsContent = StringUtils.replace(smsContent, "%END_DATE!", endDate);
            otherTD.setRsrvStr21(smsContent); // 短信内容

            // 判断下发的二位码是否包括彩信内容,因为彩信的内容比较长，所以由一个单独的参数配置
            IDataset commPara2 = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "87", giftTypeCode, giftId, uca.getUserEparchyCode());

            if (IDataUtil.isNotEmpty(commPara2))
            {
                IData para2 = commPara2.getData(0);
                String part1 = para2.getString("PARA_CODE20", "") + para2.getString("PARA_CODE23", "");
                otherTD.setRsrvStr25(part1.replace("%END_DATE!", endDate)); // 彩信内容part1
                String part2 = para2.getString("PARA_CODE21", "") + para2.getString("PARA_CODE24", "");
                otherTD.setRsrvStr26(part2.replace("%END_DATE!", endDate)); // 彩信内容part2
                String part3 = para2.getString("PARA_CODE22", "") + para2.getString("PARA_CODE25", "");
                otherTD.setRsrvStr27(part3.replace("%END_DATE!", endDate)); // 彩信内容part3
            }

            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setRemark("大客户兑换礼品发送二维码信息");

            btd.add(uca.getSerialNumber(), otherTD);
        }
    }

    /**
     * 处理礼品兑换发送E拇指平台信息
     * 
     * @param btd
     * @param reqData
     * @param vipExchgData
     * @param isSendSms
     * @throws Exception
     */
    private void decodeEmzSmsOther(BusiTradeData btd, VipExchangeReqData reqData, VipExchangeData vipExchgData, String isSendSms) throws Exception
    {
        UcaData uca = reqData.getUca();
        String sysdate = reqData.getAcceptTime();
        String giftTypeCode = vipExchgData.getGiftTypeCode();
        String giftId = vipExchgData.getGiftId();

        IDataset commPara = CommparaInfoQry.getEnableCommparaInfoByCode1("CSM", "286", giftTypeCode, giftId, uca.getUserEparchyCode());

        // 判断用户兑换的礼品是否需要下发E拇指信息
        if (IDataUtil.isNotEmpty(commPara))
        {
            isSendSms = "1";
            IData para1 = commPara.getData(0);
            String rsrvValueCode = para1.getString("PARA_CODE12");
            String serviceId = para1.getString("PARA_CODE13");

            if (StringUtils.isBlank(rsrvValueCode) || StringUtils.isBlank(serviceId))
            {
                // 从参数表获取RSRV_VALUE_CODE或者SERVICE_ID为空
                CSAppException.apperr(VipExchangeException.CRM_VIP_EXCHANGE_8);
            }

            OtherTradeData otherTD = new OtherTradeData();
            otherTD.setUserId(uca.getUserId());
            otherTD.setInstId(SeqMgr.getInstId());
            otherTD.setRsrvValueCode(rsrvValueCode);
            otherTD.setRsrvStr1(serviceId); // callpf约定使用service_id ？？？
            otherTD.setRsrvStr2(para1.getString("PARA_CODE2", ""));
            otherTD.setRsrvStr3(para1.getString("PARA_CODE3", ""));
            otherTD.setRsrvStr4(para1.getString("PARA_CODE4", ""));
            otherTD.setRsrvStr5(para1.getString("PARA_CODE5", ""));
            otherTD.setRsrvStr6(para1.getString("PARA_CODE6", ""));
            otherTD.setRsrvStr7(para1.getString("PARA_CODE7", ""));// 活动产品编码（与E拇指平台约定，由E拇指平台提供，调用E拇指平台接口时需要将该参数传给对方）
            otherTD.setStartDate(sysdate);
            otherTD.setEndDate(SysDateMgr.getTheLastTime());
            otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
            otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
            otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
            otherTD.setRemark("礼品兑换发送E拇指平台信息");

            btd.add(uca.getSerialNumber(), otherTD);
        }
    }

    /**
     * 生成大客户兑换礼品台账
     * 
     * @param btd
     * @param reqData
     * @param vipExchgData
     * @throws Exception
     */
    private void genTradeExchange(BusiTradeData btd, VipExchangeReqData reqData, VipExchangeData vipExchgData, String isSendSms) throws Exception
    {
        UcaData uca = reqData.getUca();
        VipTradeData vipData = uca.getVip();
        String sysdate = reqData.getAcceptTime();

        String giftTypeCode = vipExchgData.getGiftTypeCode();
        String giftId = vipExchgData.getGiftId();
        String giftName = vipExchgData.getGiftName();
        String ruleCheck = reqData.getRuleCheck();

        OtherTradeData otherTD = new OtherTradeData();
        otherTD.setUserId(uca.getUserId());
        otherTD.setInstId(SeqMgr.getInstId());
        otherTD.setRsrvValueCode("VIP_EXCHANGE_GIFT");
        otherTD.setRsrvValue(uca.getSerialNumber());
        otherTD.setRsrvStr1(vipData != null ? vipData.getVipId() : "");
        otherTD.setRsrvStr2(vipData != null ? vipData.getVipTypeCode() : "");
        otherTD.setRsrvStr3(vipData != null ? vipData.getVipClassId() : "");
        otherTD.setRsrvStr4(giftTypeCode);
        otherTD.setRsrvStr5(giftId);
        otherTD.setRsrvStr6(giftName);
        otherTD.setRsrvStr7(isSendSms);
        otherTD.setRsrvStr8("1");
        otherTD.setRsrvTag1(ruleCheck);// 1-不校验礼品是否符合大客户级别等判断，否则需要校验
        otherTD.setStartDate(sysdate);
        otherTD.setEndDate(SysDateMgr.getLastDayOfThisYear());
        otherTD.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTD.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTD.setModifyTag(BofConst.MODIFY_TAG_ADD);
        otherTD.setRemark("大客户兑换礼品日志");

        btd.add(uca.getSerialNumber(), otherTD);
    }

}
