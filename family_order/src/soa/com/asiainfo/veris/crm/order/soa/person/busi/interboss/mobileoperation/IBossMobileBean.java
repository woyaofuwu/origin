
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.RemoteMobileException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.badness.BadnessInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.print.ReceiptNotePrintMgr;

public class IBossMobileBean extends CSBizBean
{

    private void checkSerialNumber(String serialNumber) throws Exception
    {
        IDataset dataset = BadnessInfoQry.qryProvCodeBySn(serialNumber);
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_340);
        }
        else
        {
            String provCode = dataset.getData(0).getString("PROV_CODE");
            if (StringUtils.equals("898", provCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "您的手机为本省用户,无法办理一级BOSS业务" + serialNumber);
            }
        }
    }

    private String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }

    public String decodeIdType(String IdType)
    {
        String iBossTdType = null;

        if ("0".equals(IdType))
        {
            iBossTdType = "00";
        }
        else if ("1".equals(IdType))
        {
            iBossTdType = "01";
        }
        else if ("A".equals(IdType))
        {
            iBossTdType = "02";
        }
        else if ("C".equals(IdType))
        {
            iBossTdType = "04";
        }
        else if ("K".equals(IdType))
        {
            iBossTdType = "05";
        }
        else
        {
            iBossTdType = "99";
        }

        return iBossTdType;
    }

    public IDataset getCustInfo(IData data) throws Exception
    {
        String idType = data.getString("IDTYPE");
        String idValue = data.getString("IDVALUE");
        String userPasswd = data.getString("USER_PASSWD");
        String idCardType = decodeIdType(data.getString("IDCARDTYPE"));
        String idCardNum = data.getString("IDCARDNUM");
        String startDate = data.getString("START_DATE");
        String endDate = data.getString("END_DATE");
        String routeType = data.getString("ROUTETYPE");
        String mobileNum = data.getString("MOBILENUM");
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");

        checkSerialNumber(mobileNum);
        IData result = IBossCall.queryRemoteOpenMobileCustInfoIBOSS(idType, idValue, userPasswd, idCardType, idCardNum, startDate, endDate, routeType, mobileNum);
        if ("0000".equals(result.getString("X_RSPCODE")))
        {
            result.put("IDCARDTYPE", encodeIdType(result.getString("IDCARDTYPE")));
        }

        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {
            if ("131".equals(tradeTypeCode) && !"00".equals(result.getString("USER_STATE_CODESET")))
            {
                if ("02".equals(result.getString("USER_STATE_CODESET")))
                {
                    CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_1);// 该用户处于停机状态，不能办理此业务
                }
                else
                {
                    CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_2);// 只有正常在网才能办理此业务
                }
            }
            else if ("133".equals(tradeTypeCode) && !"02".equals(result.getString("USER_STATE_CODESET"))) 
            {
                if ("00".equals(result.getString("USER_STATE_CODESET")))
                {
                    CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_7);// 该用户处于开通状态，不能办理此业务
                }
                else
                {
                    CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_2);// 只有正常在网才能办理此业务
                }
            }
        }
        else
        {
            if ("2998".equals(result.getString("X_RSPCODE")))
            {
                CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_3, "落地方：" + result.getString("X_RSPDESC"));
            }
        }

        String crmBalance = result.getString("CRM_BALANCE", "");
        if (StringUtils.isNotBlank(result.getString("CRM_BALANCE")))
        {
            double balance = Double.parseDouble(result.getString("CRM_BALANCE", ""));
            balance = balance / 1000.0;
            crmBalance = Double.toString(balance);
        }

        String debtBalance = result.getString("DEBT_BALANCE", "");
        if (StringUtils.isNotBlank(result.getString("DEBT_BALANCE")))
        {
            double dbalance = Double.parseDouble(result.getString("DEBT_BALANCE", ""));
            dbalance = dbalance / 1000.0;
            debtBalance = Double.toString(dbalance);
        }

        result.put("DEBT_BALANCE", debtBalance);
        result.put("BALANCE", crmBalance);
        result.put("CUST_NAME_BAK", result.getString("CUST_NAME"));
        result.put("IDCARDNUM_BAK", result.getString("IDCARDNUM"));

        return IDataUtil.idToIds(result);

        // return null;
    }

    private IData insBHTrade(IData input) throws Exception
    {
        String tradeId = SeqMgr.getTradeId();
        String orderId = SeqMgr.getOrderId();
        String sysTime = SysDateMgr.getSysTime();
        String tradeTypeCode = input.getString("TRADE_TYPE_CODE");

        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeId);// 业务流水号
        inparam.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));// 受理月份：受理时间的月份，可作为分区字段。正常情况下可从trade_id的第5、6位获得。
        inparam.put("BATCH_ID", "0");
        inparam.put("ORDER_ID", orderId);
        inparam.put("PROD_ORDER_ID", "");
        inparam.put("BPM_ID", "");
        inparam.put("CAMPN_ID", "0");
        inparam.put("TRADE_TYPE_CODE", tradeTypeCode);// 业务类型编码：见参数表TD_S_TRADETYPE
        inparam.put("PRIORITY", "0");// 优先级：值越大越优先（同一用户间以受理时间先后为准）
        inparam.put("SUBSCRIBE_TYPE", "0");// 定单类型：0-普通立即执行，1-普通预约执行，100-批量立即执行，101-批量预约执行，200-信控执行
        inparam.put("SUBSCRIBE_STATE", "0");
        inparam.put("NEXT_DEAL_TAG", "0");
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());
        inparam.put("CUST_ID", input.getString("CUST_ID", ""));
        inparam.put("CUST_NAME", input.getString("CUST_NAME_BAK", ""));
        inparam.put("USER_ID", input.getString("USER_ID", ""));
        inparam.put("ACCT_ID", input.getString("ACCT_ID", ""));
        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("CITY_CODE", "");
        inparam.put("PRODUCT_ID", "");
        inparam.put("BRAND_CODE", input.getString("BRAND_CODE_BAK", ""));
        inparam.put("ACCEPT_DATE", sysTime);
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("OPER_FEE", input.getString("OPER_FEE") == null ? "0" : input.getDouble("OPER_FEE") * 100);
        inparam.put("FOREGIFT", "0");
        inparam.put("ADVANCE_PAY", "0");
        inparam.put("PROCESS_TAG_SET", "                    ");
        inparam.put("OLCOM_TAG", "0");
        inparam.put("UPDATE_TIME", sysTime);
        inparam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        inparam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        inparam.put("INTF_ID", "TF_B_TRADE");
        String operFee = input.getString("OPER_FEE");
        if (null != operFee && !operFee.equals("0"))
        {
            inparam.put("FEE_STATE", "1");
        }
        else
        {
            inparam.put("FEE_STATE", "0");
        }
        inparam.put("FINISH_DATE", sysTime);
        inparam.put("EXEC_TIME", sysTime);
        inparam.put("CANCEL_TAG", "0");
        inparam.put("REMARK", "未处理请求！");

        if (input.containsKey("SCORE_VALUE") && input.containsKey("SCORE_TYPE_CODE") && input.containsKey("CLASS_LEVEL"))
        {
            inparam.put("USER_ID", "99999999");// 跨区入网标志
            inparam.put("RSRV_STR1", input.getString("SCORE_VALUE"));// 积分额
            inparam.put("RSRV_STR2", input.getString("SCORE_TYPE_CODE"));// 积分类型 0－全球通积分；1－动感地带
            inparam.put("RSRV_STR3", input.getString("CLASS_LEVEL"));// 客户级别 0－普通用户（动感地带用户为普通用户）1－银卡2－金卡3－钻石卡
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (input.containsKey("RSRV_STR20") && input.containsKey("RSRV_STR2"))
        {
            inparam.put("USER_ID", "88888888");// 机场VIP标志
            inparam.put("RSRV_STR1", input.getString("RSRV_STR20"));// 机场VIP应扣免费次数
            int score;
            try
            {
                score = Integer.parseInt(input.getString("RSRV_STR2"));
            }
            catch (NumberFormatException e)
            {
                score = 0;
            }
            inparam.put("RSRV_STR3", score / 20);// 积分600分，30元/人次;积分1000分，50元/人次;积分1500分，75元/人次;积分2500分，125元/人次
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        if (input.containsKey("NEWSIMNUM"))
        {
            inparam.put("RSRV_STR1", input.getString("NEWSIMNUM"));// 备卡卡号
            inparam.put("RSRV_STR4", "0");// 0 :发起方
        }
        Dao.insert("TF_BH_TRADE", inparam, Route.getJourDbDefault());
        return inparam;
    }

    private IData insOrder(IData input) throws Exception
    {
        return null;
    }

    public IDataset loadPrintData(IData data) throws Exception
    {
        IDataset printTemp = new DatasetList();
        data.put("TRADE_DEPT_NAME", getVisit().getDepartName());// 受理部门
        data.put("TRADE_STAFF_NAME", getVisit().getStaffName());// 受理员工
        data.put("TRADE_STAFF_ID", getVisit().getStaffId());// 受理员工
        data.put("TRADE_TYPE", UTradeTypeInfoQry.getTradeTypeName(data.getString("TRADE_TYPE_CODE")));// 业务类型
        data.put("ID_CARD_TYPE", StaticUtil.getStaticValue("IBOSS_PSPT_TYPE_CODE", data.getString("ID_CARD_TYPE")));// 证件类型
        data.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        data.put("VERIFY_TYPE", StaticUtil.getStaticValue("EBOSS_VERIFY_TYPE", data.getString("VERIFY_TYPE")));
        data.put("TRADE_DATE", data.getString("ACCEPT_DATE", SysDateMgr.getSysTime()));
        String tradeType = "";
        if ("131".equals(data.getString("TRADE_TYPE_CODE")))
        {
            tradeType = StaticUtil.getStaticValue("STOPMOBILE_REASON", data.getString("REASON"));
        }
        else
        {
            tradeType = StaticUtil.getStaticValue("OPENMOBILE_REASON", data.getString("REASON"));
        }
        data.put("TRADE_TYPE", tradeType);
        ReceiptNotePrintMgr printMgr = new ReceiptNotePrintMgr();
        printTemp = printMgr.printInterBossReceipt(data);

        return printTemp;
    }

    public IDataset openMobile(IData data) throws Exception
    {
        String idType = data.getString("IDTYPE");
        String idValue = data.getString("IDVALUE");// 手机号码
        String userPasswd = data.getString("USER_PASSWD");// 客服密码
        String idCardType = decodeIdType(data.getString("IDCARDTYPE"));// 证件类型编码
        String idCardNum = data.getString("IDCARDNUM");// 证件号码
        String routeType = data.getString("ROUTETYPE");
        String mobileNum = data.getString("MOBILENUM");
        String operFee = String.valueOf(data.getInt("OPER_FEE", 0) * 1000);// 手续费
        String tradeTypeCode = data.getString("REASON");// 停机原因
        data.put("TRADE_TYPE_CODE", "133");
        if ("01".equals(idType))
        {
            data.put("SERIAL_NUMBER", idValue);// 手机号码
        }
        // String tradeType = "异地复机业务";
        // if(StringUtils.isNotBlank(tradeTypeCode)) {
        // tradeType = StaticUtil.getStaticValue("STOPMOBILE_REASON", tradeTypeCode);
        // }
        IData temp = insBHTrade(data);
        IData result = IBossCall.remoteOpenMobileOpenMobileIBOSS(idType, idValue, userPasswd, idCardType, idCardNum, tradeTypeCode, operFee, routeType, mobileNum);
        updBHTrade(result, temp);

        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {

        }
        else
        {
            if ("2998".equals(result.getString("X_RSPCODE")) || "2999".equals(result.getString("X_RSPCODE")))
            {
                CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_5, "落地方：" + result.getString("X_RSPDESC"));
            }
        }
        return IDataUtil.idToIds(temp);
    }

    public IDataset stopMobile(IData data) throws Exception
    {
        String idType = data.getString("IDTYPE");
        String idValue = data.getString("IDVALUE");// 手机号码
        String userPasswd = data.getString("USER_PASSWD");// 客服密码
        String idCardType = decodeIdType(data.getString("IDCARDTYPE"));// 证件类型编码
        String idCardNum = data.getString("IDCARDNUM");// 证件号码
        String routeType = data.getString("ROUTETYPE");
        String mobileNum = data.getString("MOBILENUM");
        String operFee = String.valueOf(data.getInt("OPER_FEE", 0) * 1000);// 手续费
        String tradeTypeCode = data.getString("REASON");// 停机原因
        data.put("TRADE_TYPE_CODE", "131");
        if ("01".equals(idType))
        {
            data.put("SERIAL_NUMBER", idValue);// 手机号码
        }
        // String tradeType = "异地停机业务";
        // if(StringUtils.isNotBlank(tradeTypeCode)) {
        // tradeType = StaticUtil.getStaticValue("STOPMOBILE_REASON", tradeTypeCode);
        // }
        IData temp = insBHTrade(data);
        IData result = IBossCall.remoteStopMobileStopMobileIBOSS(idType, idValue, userPasswd, idCardType, idCardNum, tradeTypeCode, operFee, routeType, mobileNum);
        updBHTrade(result, temp);

        if ("0".equals(result.getString("X_RSPTYPE")) && "0000".equals(result.getString("X_RSPCODE")))
        {

        }
        else
        {
            if ("2998".equals(result.getString("X_RSPCODE")) || "2999".equals(result.getString("X_RSPCODE")))
            {
                CSAppException.apperr(RemoteMobileException.CRM_REMOTEMOBILE_5, "落地方：" + result.getString("X_RSPDESC"));
            }
        }
        return IDataUtil.idToIds(temp);
    }

    private int updBHTrade(IData input, IData tradeData) throws Exception
    {
        String desc = input.getString("X_RSPDESC", "");
        desc = StringUtils.replace(desc, "'", "");
        desc = StringUtils.replace(desc, "’", "");
        ;
        if (desc != null && desc.length() > 48)
        {
            desc = desc.substring(0, 48);
        }

        IData param = new DataMap();
        param.put("REMARK", input.getString("X_RSPCODE", "") + ":" + desc);
        param.put("TRADE_ID", tradeData.getString("TRADE_ID"));
        param.put("ACCEPT_MONTH", tradeData.getString("ACCEPT_MONTH"));
        return Dao.executeUpdateByCodeCode("TF_BH_TRADE", "UPDATE_TRADE_REMARK", param, Route.getJourDbDefault());
    }
}
