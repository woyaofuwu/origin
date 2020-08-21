
package com.asiainfo.veris.crm.order.soa.person.busi.bank.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BankPaymentManageException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.MainTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.bank.order.requestdata.SignedBankPaymentDealRequestData;

import dnapay.common.Strings;
import dnapay.common.ToolKit;
import dnapay.service.client.PosMessage;
import dnapay.service.client.TransactionClient;
import dnapay.service.client.TransactionType;

public class SignedBankPaymentDealTrade extends BaseTrade implements ITrade
{

    /**
     * 根据当前时间生成模拟系统跟踪号
     * 
     * @return
     */
    public static String getSerialNO()
    {
        return dnapay.common.DateFormatter.HHmmss(new java.util.Date());
    }

    public void cancelClient(IData input, IData bankInfo, String serialNumber) throws Exception
    {
        // 测试环境WebCA外网地址
        // 加密报文体格式：BASE64(版本号))|BASE64(RSA(报文加密密钥))| BASE64(3DES(报文原文))| BASE64(MD5(报文原文))
        String client = input.getString("PARA_CODE1");
        String cert = input.getString("PARA_CODE2");
        String mno1 = input.getString("PARA_CODE3");
        String mno2 = input.getString("PARA_CODE4");
        String pw = input.getString("PARA_CODE5");
        String tno = input.getString("PARA_CODE6");
        String ano = input.getString("PARA_CODE7");

        // 交易密钥, 随机生成, 用于加密解密报文
        String encryptKey = Strings.random(24);

        // 测试持卡人信息, 请修改为商户测试人员的信息
        String mobileNumber = serialNumber;// 用户号码
        String bankCardNo = bankInfo.getString("BANK_CARD_NO"); // 借记卡
        String accountNum = ano + mobileNumber + "|" + bankCardNo; // 借记卡
        String idCardType = ""; // 银行开户证件类型，　01:身份证，02:护照，03:军人证，04:台胞证
        String idCardNo = ""; // 银行开户证件号
        String idCardName = ""; // 银行开户姓名
        String userName = ""; // 订单商品受益人，多个受益人以","分割．
        String bankAddress = ""; // 银行开户地，信用卡可填空，省市以","号分割．
        String ipAddress = ""; // 持卡人登录IP地址．
        String idCardAddress = ""; // 身份证地址,截取至街道, 特殊风控.
        String productPhoneNumber = ""; // 受益手机号，电话充值商户需填写;
        String productAddress = "BOSS渠道标识"; // 商品销售地，省市以","号分割，团购商户需填写;
        String bankPhoneNumber = "";
        String extTransData = ""; // 额外交易数据，　Apple appID
        String memberFlag = ""; // 商户定制用户标签

        String transData = idCardName + "|" + idCardNo + "|" + bankAddress + "|" + idCardType + "|" + userName + "|" + ipAddress + "|" + idCardAddress + "|" + productPhoneNumber + "|" + productAddress + "|" + bankPhoneNumber + "|" + extTransData
                + memberFlag;

        TransactionClient tm = new TransactionClient(client, "");
        tm.setTransactionType(TransactionType.CA);
        tm.setServerCert(ToolKit.getPropertyFromFile(cert));

        // 测试商户系统,事后验证商户(502020000001), 事前验证商户(002010000014),
        tm.setMerchantNo(mno1 + mno2); // 商户类型+商户编号
        tm.setMerchantPassWD(pw); // 商户Mac密钥
        tm.setTerminalNo(tno); // 商户终端编号
        PosMessage pm = tm.delMerchantMember(getSerialNO(), accountNum, transData, encryptKey);
    }

    public void createBusiTradeData(BusiTradeData btd) throws Exception
    {
        IDataset bankInfos = UserBankMainSignInfoQry.qryBankPaymentInfoList(btd.getRD().getUca().getUserId());

        if (IDataUtil.isEmpty(bankInfos))
        {
            CSAppException.apperr(BankPaymentManageException.CRM_BANKPAYMENT_232);
        }

        // 更新主台账的费用信息
        dealMainTradeData(btd);

        IData bankInfo = bankInfos.getData(0);

        dealBankInfo(btd.getRD().getTradeId(), bankInfo.getString("USER_BAND_ID"));

        delMerchantMember(btd, bankInfo);

    }

    public void dealBankInfo(String tradeId, String userBankId) throws Exception
    {
        IData data = new DataMap();
        data.put("RSRV_STR1", tradeId);
        data.put("USER_BAND_ID", userBankId);

        Dao.save("TF_F_BANK", data);
    }

    public void dealMainTradeData(BusiTradeData btd) throws Exception
    {
        SignedBankPaymentDealRequestData reqData = (SignedBankPaymentDealRequestData) btd.getRD();
        // 更新主台账的费用信息
        MainTradeData mainTradeData = btd.getMainTradeData();
        mainTradeData.setRemark(reqData.getRemark());
        mainTradeData.setRsrvStr1(reqData.getPartitionId());
        mainTradeData.setRsrvStr2(reqData.getOpenBank());
        mainTradeData.setRsrvStr3(reqData.getBankCardNo());
        mainTradeData.setRsrvStr4(reqData.getCardTypeCode());
        mainTradeData.setRsrvStr5(reqData.getStartTime());
        mainTradeData.setRsrvStr6(reqData.getEndTime());
        mainTradeData.setRsrvStr7(reqData.getReckTag());
        mainTradeData.setExecTime(SysDateMgr.END_DATE_FOREVER);

    }

    public void delMerchantMember(BusiTradeData btd, IData bankInfo) throws Exception
    {
        IDataset results = CommparaInfoQry.getCommpara("CSM", "2", "9800", null);// 查询易联配置

        // 设置HTTPS连接参数, 配置在systemsetting.properties文件中
        System.setProperty("javax.net.ssl.trustStore", ToolKit.getPropertyFromFile("javax.net.ssl.trustStore"));
        System.setProperty("javax.net.ssl.trustStorePassword", ToolKit.getPropertyFromFile("javax.net.ssl.trustStorePassword"));

        // 解约
        cancelClient(results.getData(0), bankInfo, btd.getRD().getUca().getSerialNumber());
    }

}
