
package com.asiainfo.veris.crm.order.soa.person.busi.changephone;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.ChangePhoneException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAltsnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

public class ChangePhoneCancelBean extends CSBizBean
{
	protected static Logger logger = Logger.getLogger(ChangePhoneCancelBean.class);
    public IData changePhoneCancel(IData data) throws Exception
    {
        ChangePhonePreRegisterBean changePhonePreRegisterBean = (ChangePhonePreRegisterBean) BeanManager.createBean(ChangePhonePreRegisterBean.class);
        // 查出改号记录表中新号码关联的激活的记录
        String new_serial_number = data.getString("NEW_SN", "");
        String rela_type = "1";
        String status = "1";
        IDataset userAltsnList = UserAltsnInfoQry.queryUserAltsnBySn(new_serial_number, rela_type, status);
        if (userAltsnList.isEmpty() || userAltsnList.size() < 1)
        {
            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_21, new_serial_number);
        }
        String activateTime = userAltsnList.getData(0).getString("ACTIVATE_TIME");
        String cancelPreTime = SysDateMgr.addMonths(activateTime, 1);
        String sysdate = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD);
        if(sysdate.compareTo(cancelPreTime) < 0){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "关联期未满一个月，不能办理此业务！！");
        }

        // 更新改号资料信息,把激活状态改为已关联取消状态
        String from_status = status;
        String to_status = "3";
        updStatusTimeById(new_serial_number, from_status, to_status, rela_type);

        // 获取老号码路由 为同步取消到对应的省
        IData in = new DataMap();
        IData out = new DataMap();
        IData userAltsn = userAltsnList.getData(0);
        in.put("SERIAL_NUMBER", userAltsn.getString("RELA_SERIAL_NUMBER", ""));
        String oldEparchy = changePhonePreRegisterBean.getSnRoute(in, out);
        String oldProvince = out.getString("FLAG", "");

        // 同步改号取消到对应省
        in.put("RSRV_STR1", new_serial_number);
        in.put("SERIAL_NUMBER", userAltsn.getString("RELA_SERIAL_NUMBER", ""));
        in.put("OLD_PROVINCE", oldProvince);
        in.put("IS_CANCEL", "1");
        in.put("OLD_EPARCHY", oldEparchy);
        in.put("NEW_PROVINCE", "A"); // 本地修改
//         pd.commitDBConn(); // 锁表，必加
        IData result = changePhonePreRegisterBean.synSnActivate(data, in);

        // tuxBill ( pd , td , "") ; 改到落地方调用 调用改号平台接口 改号取消 today
        IData ibossResult = new DataMap();
        try
        {
            String kind_id = "BIP2B075_T2001075_0_0";// 交易唯一标识
            String x_trans_code = "";// 交易编码-IBOSS
            String old_id_value = userAltsn.getString("RELA_SERIAL_NUMBER", "");
            String volteType = userAltsn.getString("RSRV_STR3", "");
            if("".equals(volteType)){
            	volteType="0";
            }
            String new_id_value = new_serial_number;
            String user_id = changePhonePreRegisterBean.getLocalUserInfo(in).getString("USER_ID");
            // 查找new_imsi
            IData params = new DataMap();
            params.put("SERIAL_NUMBER", new_serial_number);
            params.put("USER_ID", user_id);
            params.put("NEW_SN", new_serial_number);

            String new_imsi = changePhonePreRegisterBean.getNewIdIMSI(params);
            String opr_code = "02";// 02-关联关系取消数据同步到改号平台
            String reserve = "";
            
            IData inData = new DataMap();
			inData.put("KIND_ID", "BIP2B075_T2001075_0_0"); // 交易唯一标识
			inData.put("X_TRANS_CODE", x_trans_code); // 交易编码-IBOSS
			inData.put("OLD_ID_VALUE", old_id_value);
			inData.put("OLD_IMSI", userAltsn.getString("RSRV_STR2", ""));// OldIMSI
			inData.put("VOLTE_TYPE", volteType);// VOLTE_TYPE
			inData.put("OLD_IMPI", userAltsn.getString("RSRV_STR1", ""));// OldIMPI
			inData.put("NEW_ID_VALUE", new_id_value);
			inData.put("NEW_IMSI", new_imsi); // NewIMSI
			inData.put("IMPI", new_imsi + "@ims.mnc0"+new_imsi.substring(3, 5)+".mcc460.3gppnetwork.org");// NewIMPI
			inData.put("OPR_CODE", opr_code); // 02-关联关系取消数据同步到改号平台
			inData.put("RESERVE", reserve);
            IDataset callBossset = IBossCall.dealInvokeUrl(kind_id,"IBOSS6",inData);
            ibossResult = callBossset.getData(0);
            if (!"0000".equals(ibossResult.getString("X_RSPCODE")))
            {
                logger.error("调用改号平台出错:" + ibossResult.getString("X_RSPCODE") + " " + ibossResult.getString("X_RSPDESC"));
//                CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_22, ibossResult.getString("X_RESULTINFO"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
//            CSAppException.apperr(ChangePhoneException.CRM_CHANGEPHONE_22, ibossResult.getString("X_RESULTINFO"));
        }
        // 最后成功返回成功信息
        result.put("X_RESULTINFO", "OK");
        result.put("X_RECORDNUM", "1");
        result.put("X_RESULTCODE", "0");
        return result;
    }

    /**
     * 页面取消改号信息查询 ，查询改号业务表
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IData queryChangePhoneInfo(IData data) throws Exception
    {
        IData result = new DataMap();
        String serial_number = data.getString("SERIAL_NUMBER", "");
        IDataset userAltsnBySnList = UserAltsnInfoQry.queryUserAltsnBySnSelSnCancel(serial_number);
        if (userAltsnBySnList.size() > 0)
        {
            IData userAltsnBySn = userAltsnBySnList.getData(0);
            result.putAll(userAltsnBySn);
            result.put("STATUS_DESC", "已激活");
            String rela_type = userAltsnBySn.getString("RELA_TYPE", "-1"); // 关联关系描述
            // 新号码关联老号码，无须转换显示
            if ("1".equals(rela_type))
            {
            }
            // 老号码关联新号码
            else
            {
                result.put("SERIAL_NUMBER", userAltsnBySn.getString("RELA_SERIAL_NUMBER", ""));
                result.put("RELA_SERIAL_NUMBER", userAltsnBySn.getString("SERIAL_NUMBER", ""));
                result.put("EPARCHY_CODE", userAltsnBySn.getString("RELA_EPARCHY_CODE", ""));
                result.put("RELA_EPARCHY_CODE", userAltsnBySn.getString("EPARCHY_CODE", ""));
            }
            return result;
        }
        return result;
    }

    // 更新改号资料信息的中的状态与更新时间
    public int updStatusTimeById(String serial_number, String from_status, String to_status, String rela_type) throws Exception
    {
        IData params = new DataMap();
        params.put("SERIAL_NUMBER", serial_number);
        params.put("FROM_STATUS", from_status);
        params.put("TO_STATUS", to_status);
        params.put("RELA_TYPE", rela_type);
        params.put("ALT_CANCEL_TIME", SysDateMgr.getSysTime());
        int dsPreInfo = Dao.executeUpdateByCodeCode("TF_F_USER_ALTSN", "UPD_CANCELDATE_BY_ID", params);
        return dsPreInfo;
    }
    public void cancelTrigger(IData param) throws Exception{
        IData data = new DataMap();
        SQLParser parser = new SQLParser(data);
        parser.addSQL(" SELECT * ");
        parser.addSQL(" FROM TF_F_USER_ALTSN T ");
        parser.addSQL(" WHERE T.STATUS = 1 ");
        parser.addSQL(" AND T.RELA_TYPE = 1 ");
        parser.addSQL(" AND TO_DATE(TO_CHAR(SYSDATE,'YYYY-MM-DD'),'YYYY-MM-DD') BETWEEN T.ACTIVATE_TIME AND T.RSRV_DATE1 ");
        IDataset altSnInfos = Dao.qryByParse(parser);
        for (int i = 0; i < altSnInfos.size(); i++)
        {
            IData altSnInfo = altSnInfos.getData(i);
            //如果当前时间等于预计取消的时间，同时不续约，调用激活取消接口，并发送短信
            String endDate = altSnInfo.getString("RSRV_DATE1").substring(0, 10);
            String sysDate = SysDateMgr.getSysDate();
            String edt = altSnInfo.getString("EXPIRE_DEAL_TAG");
            String sn = altSnInfo.getString("SERIAL_NUMBER");
            String relasn = altSnInfo.getString("RELA_SERIAL_NUMBER");
            if(sysDate.equals(endDate)&&!("1".equals(edt)))
            {
                IData input = new DataMap();
                input.put("OLD_SN", relasn);
                input.put("NEW_SN", sn);
                input.put("CHANNEL", "1");
                input.put("OPER_CODE", "2");
                input.put("ACTIVED_TIME", SysDateMgr.getSysDate());
                input.put("HAND_CHARGE", "");
                input.put("RESERVE", "");
                input.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
                // 其他附加参数
                input.put("TRADE_CITY_CODE", getVisit().getCityCode());
                input.put("TRADE_STAFF_ID", getVisit().getStaffId());
                input.put("TRADE_DEPART_ID", getVisit().getDepartId());
                input.put("IN_MODE_CODE", "1");
                CSAppCall.call("SS.ChangePhoneCancelSVC.changePhoneCancel", input); 
                sendSMS(sn,relasn);
            }
            
            
        }
    }
    public void sendSMS(String serialNumber ,String relaSerialNumber)throws Exception{
        String content = "您好，您的新号码" + serialNumber + "与原号码" + relaSerialNumber + "关联关系已取消，原号码已停机，如需重新启用原号码，请于一个月内到海南省中国移动营业厅免费补卡。中国移动";
        IData sendInfo = new DataMap();
        sendInfo.put("EPARCHY_CODE", "0898");
        sendInfo.put("RECV_OBJECT", serialNumber);
        sendInfo.put("RECV_ID", serialNumber);
        sendInfo.put("SMS_PRIORITY", "50");
        sendInfo.put("NOTICE_CONTENT", content);
        sendInfo.put("REMARK", "改号业务关联关系取消提醒");
        sendInfo.put("FORCE_OBJECT", "10086");
        SmsSend.insSms(sendInfo);
    }
}
