
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade.order.filter;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterIn;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: CreateTDUserIntfFilter.java
 * @Description: 无线固话批量开户入参处理
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-11 下午3:54:41 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-11 yxd v1.0.0 修改原因
 */
public class CreateTDUserIntfFilter implements IFilterIn
{

    public void transferDataInput(IData input) throws Exception
    {
        // BATCH_ID为空[营业前台]直接返回
        String batchId = input.getString("BATCH_ID");
        if (StringUtils.isBlank(batchId))
        {
            return;
        }
        String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
        // 1.手机和SIM卡都为空
        if (StringUtils.isBlank(serialNumber) && StringUtils.isBlank(simCardNo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "手机号码和SIM卡不能同时为空！");
        }
        // 2.手机号码非空，sim卡空，取SIM卡号
        if (StringUtils.isNotBlank(serialNumber) && StringUtils.isBlank(simCardNo))
        {
            //IDataset dataset = ResCall.getMphonecodeIMSIInfoByNumber(serialNumber);
            IDataset dataset = ResCall.getMphonecodeInfo(serialNumber);
//            String imsi = dataset.first().getString("IMSI_CODE");
//            IDataset simDataset = ResCall.getSimcardInfoByImsiOrSimcardNo("", imsi, "1", "");
            simCardNo = dataset.first().getString("SIM_CARD_NO");
            input.put("SIM_CARD_NO", simCardNo);
        }
        // 3.手机号码空，sim卡非空，取手机号码
        if (StringUtils.isBlank(serialNumber) && StringUtils.isNotBlank(simCardNo))
        {
            //IDataset dataset = ResCall.getSerialNumberByCardNo(simCardNo);
            IDataset dataset = ResCall.getSimCardInfo("0", simCardNo, null, null);
            serialNumber = dataset.first().getString("SERIAL_NUMBER_CODE");
            input.put("SERIAL_NUMBER", serialNumber);
        }
        // 4.手机号码和SIM卡号选占
        //IDataset mphoneSet = ResCall.checkResourceForIOTMphone("0", "0", serialNumber, "0", SessionManager.getInstance().getVisit().get("DEPART_ID"));
        IDataset mphoneSet = ResCall.checkResourceForMphone("0", serialNumber, "0","1");
        IData mphoneData = mphoneSet.first();
        String simCardNo1 = mphoneData.getString("SIM_CARD_NO", ""); // SIM卡
        String preOpenTag = mphoneData.getString("PREOPEN_TAG", "0"); // 预开
        String preCodeTag = mphoneData.getString("PRECODE_TAG", "0"); // 预配
        // 4.1预开和密码卡
        IDataset simCardSet = new DatasetList();
        if (StringUtils.isNotBlank(simCardNo1) && (StringUtils.equals("1", preOpenTag) || StringUtils.equals("1", preCodeTag)))
        {
            //simCardSet = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo1, "1", SessionManager.getInstance().getVisit().get("DEPART_ID"), "", "0");
            simCardSet= ResCall.checkResourceForSim("0", serialNumber, simCardNo, "1");
            input.put("SIM_CARD_NO", simCardNo1);
        }
        else
        {
            //simCardSet = ResCall.checkResourceForIOTSim("0", "0", serialNumber, simCardNo, "1", SessionManager.getInstance().getVisit().get("DEPART_ID"), "", "0");
            simCardSet= ResCall.checkResourceForSim("0", serialNumber, simCardNo, "1");
        }
        IData simCardData = simCardSet.first();
        // 设置密码卡信息
        String cardPassWord = simCardData.getString("PASSWORD");// 密码卡
        String passCode = simCardData.getString("KIND");// 密码因子
        if (StringUtils.isNotBlank(cardPassWord) && StringUtils.isNotBlank(passCode))
        {
            input.put("CARD_PASSWD", cardPassWord);
            input.put("PASSCODE", passCode);
            input.put("DEFAULT_PWD_FLAG", "1");
        }
        else
        {
            input.put("CARD_PASSWD", "");
            input.put("PASSCODE", "");
            input.put("DEFAULT_PWD_FLAG", "0");
        }
        // 其他默认值设置
        input.put("CUST_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_NAME", input.getString("CUST_NAME", "无档户"));
        input.put("PAY_MODE_CODE", input.getString("PAY_MODE_CODE", "0"));
        input.put("PSPT_TYPE_CODE", input.getString("PSPT_TYPE_CODE", "Z"));
        input.put("PSPT_ID", input.getString("PSPT_ID", "11111111111111111111"));
        input.put("PHONE", input.getString("PHONE", "10086"));
        input.put("POST_CODE", input.getString("POST_CODE", "000000"));
        input.put("CONTACT", input.getString("CUST_NAME", "无档户"));
        input.put("CONTACT_PHONE", input.getString("CONTACT_PHONE", "10086"));
//        input.put("REAL_NAME", "1");
        input.put("NET_TYPE_CODE", "18");
        input.put("IMSI", simCardData.getString("IMSI", ""));
        input.put("KI", simCardData.getString("KI", ""));
        input.put("RES_KIND_CODE", simCardData.getString("RES_KIND_CODE", ""));
        input.put("RES_TYPE_CODE", simCardData.getString("RES_TYPE_CODE", ""));
        input.put("SIM_FEE_TAG", simCardData.getString("FEE_TAG", ""));
        //设置3822批量预开业务类型
        input.put("ORDER_TYPE_CODE", "3822");
        input.put("TRADE_TYPE_CODE", "3822");

    }
}
