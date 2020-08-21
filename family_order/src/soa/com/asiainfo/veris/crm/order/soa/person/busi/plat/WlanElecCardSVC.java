
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.PlatInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.TwoCheckSms;

public class WlanElecCardSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 7579141967068296483L;

    /**
     * 检查帐务侧余额
     * 
     * @param param
     * @throws Exception
     */
    private static boolean checkOweFee(String userId, String adjustfee) throws Exception
    {
        IData oweFee = AcctCall.getOweFeeByUserId(userId);
        // 判断用户是否有足够实时结余
        if (Double.parseDouble(adjustfee) > oweFee.getDouble("ACCT_BALANCE"))
        {
            return false;
        }
        return true;
    }

    /**
     * 获取短信通知内容
     * 
     * @param cardType
     * @param strRouteEparchy
     * @return
     * @throws Exception
     */
    private static String getNoticeContent(String cardType, String strRouteEparchy) throws Exception
    {
        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "1080", cardType, strRouteEparchy);
        if (commparas.size() == 0)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_365, cardType);
        }
        IData commpara = commparas.getData(0);
        return commpara.getString("PARAM_NAME", "");
    }

    /**
     * 提供给短信营业厅调用的扣费确认接口
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData buckleFeeConfirm(IData data) throws Exception
    {
        IData result = new DataMap();
        String resultCode = "0";
        String respCode = "0000";
        String resultInfo = "OK";
        String seqId = IDataUtil.chkParam(data, "SEQ_ID"); // 后台跑二次确认时，传入

        IDataset feeCardLogList = PlatInfoQry.selWlanFeeCardByPK(seqId);
        if (IDataUtil.isEmpty(feeCardLogList))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1000);
        }

        // 参数准备
        IData feeCardLog = feeCardLogList.getData(0);
        String serialNumber = feeCardLog.getString("SERIAL_NUMBER");
        String strCardType = feeCardLog.getString("CARD_TYPE");
        String strUserId = feeCardLog.getString("RSRV_STR1");
        String strOprNum = feeCardLog.getString("OPR_NUMBER");
        String routeEparchyCode = feeCardLog.getString("RSRV_STR2");
        String state = feeCardLog.getString("STATE");
        if (!"Y0B".equals(state))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_1007);
        }

        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "1080", strCardType, routeEparchyCode);
        if (IDataUtil.isEmpty(commparas))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_365, strCardType);
        }
        // 转换卡类型为资源需要的2位的编码.
        String strValueCode = commparas.getData(0).getString("PARA_CODE2", "");
        if (StringUtils.isBlank(strValueCode))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "资源卡类型编码不能为空,请联系资源约定类型编码!");
        }

        // 用户状态校验
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String acctTag = userInfo.getString("ACCT_TAG", "");

        if (!acctTag.equals("0"))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_770, serialNumber);
        }


        IDataset feeCardList = PlatInfoQry.selWlanFeeCardByOperDate(serialNumber, "Y0A");
        if (IDataUtil.isNotEmpty(feeCardList))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "该号码[" + serialNumber + "]一小时内已经购卡成功，请一小时后再继续!");
        }

        //调资源接口
        strValueCode = "32I3" + strValueCode;
        IDataset resList = ResCall.wlanCardOccupySel(strValueCode);
        IData resInfo = null;
        if (IDataUtil.isNotEmpty(resList))
        {
            resInfo = resList.getData(0);
        }

        String cardFee = resInfo.getString("VALUE_PRICE");
        String resNo = resInfo.getString("RES_NO");
        // 然后先在账务扣费，然后调用资源接口，占用资源，如果占用资源失败，则退费
        IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(strUserId);
         String Remark_dzk =  "WLANDZK" + seqId;
        IDataset paymentResult = AcctCall.wlanCardPayment(strUserId, acctInfo.getString("ACCT_ID"), routeEparchyCode, Remark_dzk, cardFee, "0");
        if (IDataUtil.isEmpty(paymentResult))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "扣费失败!");
        }

        if (!paymentResult.getData(0).getString("X_RESULTCODE").equals("0") && !paymentResult.getData(0).getString("X_RESULTCODE").equals("00"))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "尊敬的中国移动客户，您的手机号码余额不足，请充值后重新订购!");
        }
        else
        {
            IDataset wlanCardOccupy = ResCall.wlanCardOccupy(resNo, strValueCode);
            if (IDataUtil.isEmpty(wlanCardOccupy))
            {
            	respCode = "1011";
            	resultCode = "1011";
            	resultInfo = "调用资源接口失败，实体卡资源占用失败1!";
            }

              // 资源占用成功 ，发一级boss进行财务确认， 然后调一级boss发送卡号 密码
              IDataset confirmFeeList = IBossCall.confirmWlanElecCardFee(strOprNum, serialNumber, resNo, respCode, resultInfo, String.valueOf(Integer.parseInt(cardFee) * 10));// 要转换为厘
              if (IDataUtil.isEmpty(confirmFeeList) || !"0".equals(confirmFeeList.getData(0).getString("X_RSPTYPE")))
              {
            	  respCode = "1005";
            	  resultCode = "1005";
              	  resultInfo = "调用一级BOSS通知WLAN平台失败";
              }

             IDataset sendCardResult = IBossCall.sendSmsFromEntityCardPlat(serialNumber, resNo, strOprNum);
              if (IDataUtil.isEmpty(sendCardResult) || !"0".equals(sendCardResult.getData(0).getString("X_RSPTYPE")))
              {
            	  respCode = "1006";
            	  resultCode = "1006";
                  resultInfo = "由实体卡平台下发卡密码短信失败!";
               }

                // 更新LOG表
                IData updateWlanLogParam = new DataMap();
                updateWlanLogParam.put("SEQ_ID", seqId);
                updateWlanLogParam.put("RES_NO", resNo);
                updateWlanLogParam.put("OLD_STATE", "Y0B");
                updateWlanLogParam.put("STATE", "Y0A");
                updateWlanLogParam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                updateWlanLogParam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                updateWlanLogParam.put("RSRV_STR3", "");
                updateWlanLogParam.put("PAY_LOG_ID", "");
                Dao.executeUpdateByCodeCode("TF_B_WLAN_FEE_CARD_LOG", "UPD_WLAN_FEECARD_BY_PK", updateWlanLogParam, Route.CONN_CRM_CEN);
            
        }

        result.put("X_RESULTCODE", resultCode);
        result.put("X_RESULTINFO", resultInfo);
        return result;
    }

    /**
     * 电子卡购卡扣费方法
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData elecCardBuckleFee(IData param) throws Exception
    {
        String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
        String operNum = IDataUtil.chkParam(param, "OPR_NUMB");
        String cardType = IDataUtil.chkParam(param, "CARD_TYPE");
        String orderId = SeqMgr.getOrderId();
        String tradeId = SeqMgr.getTradeId();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (userInfo == null)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        String strRouteEparchy = userInfo.getString("EPARCHY_CODE", "");
        String strUserId = userInfo.getString("USER_ID");

        param.put("EPARCHY_CODE", strRouteEparchy);
        // 根据卡类型编码获取购卡类型描述
        String strCardTypeName = StaticUtil.getStaticValue("WLAN_CARD_TYPE", cardType);
        if (StringUtils.isBlank(strCardTypeName))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_152, cardType);
        }
        
        
        IDataset commparas = CommparaInfoQry.getCommpara("CSM", "1080", cardType, strRouteEparchy);
        if (IDataUtil.isEmpty(commparas))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_365, cardType);
        }
        // 转换卡类型为资源需要的2位的编码.
        String strValueCode = commparas.getData(0).getString("PARA_CODE2", "");
        if (StringUtils.isBlank(strValueCode))
        {
            CSAppException.apperr(PlatException.CRM_PLAT_74, "资源卡类型编码不能为空,请联系资源约定类型编码!");
        }

        //调资源接口
        strValueCode = "32I3" + strValueCode;
        IDataset resList = ResCall.wlanCardOccupySel(strValueCode);
        IData resInfo = null;
        if (IDataUtil.isNotEmpty(resList))
        {
            resInfo = resList.getData(0);
        }

        String cardFee = resInfo.getString("VALUE_PRICE");
        
        if (!checkOweFee(strUserId, cardFee)) // 查询余额是否给力
        {         
        	//CSAppException.apperr(PlatException.CRM_PLAT_74, "手机号码余额不足!");
            IData res = new DataMap();
            res.put("X_RSPTYPE", "2");
            res.put("X_RSPCODE", "2998");
            res.put("X_RSPDESC", "手机号码余额不足!");
            res.put("X_RESULTCODE", "2998");
            res.put("X_RESULTINFO", "手机号码余额不足!");
            return res;
        }

        // 插入wlan购卡日志
        IData wlanLogParam = new DataMap();
        String smsNoticId = SeqMgr.getSmsSendId();
        String seq = smsNoticId.substring(4);
        wlanLogParam.put("SEQ_ID", seq);// 为了与短信营业厅传过来的12位值统一
        wlanLogParam.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        wlanLogParam.put("SERIAL_NUMBER", serialNumber);
        wlanLogParam.put("OPR_NUMBER", operNum);
        wlanLogParam.put("CARD_TYPE", cardType);
        wlanLogParam.put("OPR_TYPE", "F0A");
        wlanLogParam.put("STATE", "Y0B");
        wlanLogParam.put("OPR_DATE", SysDateMgr.getSysTime());
        wlanLogParam.put("OPR_STAFF_ID",CSBizBean.getVisit().getStaffId());
        wlanLogParam.put("OPR_DEPART_ID", CSBizBean.getVisit().getDepartId());
        wlanLogParam.put("RSRV_STR1", strUserId);
        wlanLogParam.put("RSRV_STR2", strRouteEparchy);
        // wlanLogParam.put("RES_NO",resInfo.getString("RES_NO"));
        if (!Dao.insert("TF_B_WLAN_FEE_CARD_LOG", wlanLogParam, Route.CONN_CRM_CEN))// 插入购卡日志
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_112);
        }

        // 发送二次确认短信
        // 从参数表中查询配置的短信提醒内容
        String strNoticeContent = getNoticeContent(cardType, userInfo.getString("EPARCHY_CODE", ""));
        String noticeContent = strNoticeContent.replace("%301!", "" + strCardTypeName + "");
        if (noticeContent.trim().indexOf("提醒服务", 0) < 0)
        {
            noticeContent = "提醒服务：" + noticeContent;
        }

        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", "0");
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", noticeContent);
        sendInfo.put("REMARK", "WLAN购卡提醒短信");
        sendInfo.put("FORCE_OBJECT", "10086");
        sendInfo.put("SERIAL_NUMBER", serialNumber);
        sendInfo.put("SMS_CONTENT", noticeContent);
        sendInfo.put("SMS_TYPE", "WlanPreCard");// wlan预售卡
        sendInfo.put("TRADE_ID", tradeId);// wlan预售卡
        sendInfo.put("OPR_SOURCE", "1");// wlan预售卡
        sendInfo.put("RSRV_STR3", "5");

        // 插二次短信表
        IData preOrderData = new DataMap();
        preOrderData.put("X_TRANS_CODE", "SS.WlanElecCardSVC.buckleFeeConfirm");
        preOrderData.put("SVC_NAME", "SS.WlanElecCardSVC.buckleFeeConfirm");
        preOrderData.put("ORDER_ID", orderId);
        preOrderData.put("PRE_TYPE", "WlanPreCard");
        preOrderData.put("SERIAL_NUMBER", serialNumber);
        // preOrderData.put("VALUE_PRICE", adjustFee);//作为二次确认时，传入的卡费入参
        preOrderData.put("SEQ_ID", seq); // 作为二次确认，传入的seq_id入参
        // preOrderData.put("RES_NO", resNo);//作为二次确认的,res_no入参
        IData twoCheckData = TwoCheckSms.twoCheck("-1", 48, preOrderData, sendInfo);// 插入2次短信表

        // }
        IData res = new DataMap();
        res.put("X_RESULTCODE", "0");
        res.put("X_RESULTINFO", "购卡申请扣费登记成功");
        res.put("REQUEST_ID", twoCheckData.getString("REQUEST_ID"));
        // res.put("RES_NO", resInfo.getString("RES_NO"));
        // res.put("SEQ_ID", seq);
        return res;
    }

    /**
     * 查询WLAN资费
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData getWlanDiscnt(IData data) throws Exception
    {
        IData iparam = new DataMap();

        String serialNumber = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String eparchyCode = IDataUtil.chkParam(data, "EPARCHY_CODE");

        // 查询用户资料
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);

        if (IDataUtil.isNull(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        String uesrId = userInfo.getString("USER_ID");
        data.put("USER_ID", uesrId);
        IDataset discntList = PlatInfoQry.getCurrentWlanDiscnt(uesrId, eparchyCode);
        IData userDiscnt = new DataMap();
        if (IDataUtil.isEmpty(discntList))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_79);
        }
        else
        {
            userDiscnt = (IData) discntList.get(0);
        }

        IData discntData = UDiscntInfoQry.getDiscntInfoByPk(userDiscnt.getString("DISCNT_CODE"));
        if (IDataUtil.isNull(discntData))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_84);
        }

        userDiscnt.put("DISCNT_NAME", discntData.getString("DISCNT_NAME"));
        userDiscnt.put("DISCNT_EXPLAIN", discntData.getString("DISCNT_EXPLAIN"));

        if ("1".equals(data.getString("X_TAG")))
        {
            // 发短信。现在还未实现
            sendSMS(data, userDiscnt);
        }

        return userDiscnt;
    }

    private void insertSendCardPasswdLog(IData param) throws Exception
    {
        IDataset infos = PlatInfoQry.selWlanFeeCardByPK(param.getString("SEQ_ID"));
        if (infos.size() > 0)
        {
            IData data = infos.getData(0);
            String smsNoticId = SeqMgr.getSmsSendId();
            data.put("SEQ_ID", smsNoticId.substring(4));// 为了与短信营业厅传过来的12位值统一
            data.put("OPR_TYPE", "F0B");// 密码重发状态
            data.put("OPR_DATE", SysDateMgr.getSysTime());
            data.put("OPR_STAFF_ID", "ITF00000");
            data.put("OPR_DEPART_ID", "ITF00");
            Dao.insert("TF_B_WLAN_FEE_CARD_LOG", data, Route.CONN_CRM_CEN);
        }
    }

    public IDataset queryElecCardSaleList(IData param) throws Exception
    {
        if (param.getString("SERIAL_NUMBER") == null || "".equals(param.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "入参SerialNumber不能为空");
        }
        param.put("OPR_TYPE", "F0A");
        param.put("STATE", "Y0A");
        IDataset feeCardLogList = PlatInfoQry.queryElecCardSaleList(param.getString("SERIAL_NUMBER"), param.getString("OPR_TYPE"), param.getString("STATE"), param.getString("WLAN_CARD_SEQ"));
        if (IDataUtil.isEmpty(feeCardLogList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未查到数据！");
        }
        return feeCardLogList;
    }

    /**
     * 重发WLAN电子卡密码
     * 
     * @author xiekl
     * @param param
     * @return
     * @throws Exception
     */
    public IData sendCardPassword(IData param) throws Exception
    {
        if (param.getString("SERIAL_NUMBER") == null || "".equals(param.getString("SERIAL_NUMBER")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "入参SerialNumber不能为空");
        }
        if (param.getString("WLAN_CARD_SEQ") == null || "".equals(param.getString("WLAN_CARD_SEQ")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "入参WLAN_CARD_SEQ不能为空");
        }
        param.put("OPR_TYPE", "F0A");
        param.put("STATE", "Y0A");
        IData result = new DataMap();
        IDataset userElecCardInfos = queryElecCardSaleList(param);
        if (IDataUtil.isNotEmpty(userElecCardInfos))
        {
            IData info = userElecCardInfos.getData(0);
            // 通过一级boss向实体卡平台发送
            IDataset entityData = IBossCall.sendSmsFromEntityCardPlat(info.getString("SERIAL_NUMBER"), info.getString("WLAN_CARD_SEQ"), info.getString("OPR_NUMBER"));

            result.put("X_RESULTCODE", entityData.getData(0).getString("X_RSPTYPE"));

            insertSendCardPasswdLog(info);// 插入重发密码的记录日志
        }
        else
        {
            result.put("X_RESULTCODE", "800001");
            result.put("X_RESULTINFO", "无法找到此SEQ_ID的电子卡");
        }

        return result;
    }

    public void sendSMS(IData data, IData userDiscnt) throws Exception
    {
        IData param = new DataMap();
        String discntExplain = userDiscnt.getString("DISCNT_EXPLAIN");
        String startDate = userDiscnt.getString("START_DATE");
        String endDate = userDiscnt.getString("END_DATE");
        String noticeContent = "尊敬的客户，你选择了" + discntExplain + "，生效于" + startDate.substring(0, 4) + "年" + startDate.substring(5, 7) + "月" + startDate.substring(8, 10) + "日，截至于" + endDate.substring(0, 4) + "年" + endDate.substring(5, 7) + "月"
                + endDate.substring(8, 10) + "日。";
        // 必传参数
        param.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        param.put("RECV_OBJECT", data.getString("SERIAL_NUMBER"));
        param.put("RECV_ID", "0");
        param.put("SMS_PRIORITY", "50");
        param.put("NOTICE_CONTENT", noticeContent);
        param.put("REMARK", "查询WLAN资费");
        param.put("FORCE_OBJECT", "10086");

        SmsSend.insSms(param);
    }

}
