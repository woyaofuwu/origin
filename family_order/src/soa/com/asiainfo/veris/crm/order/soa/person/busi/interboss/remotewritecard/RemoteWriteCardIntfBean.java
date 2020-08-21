
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remotewritecard;

import com.ailk.biz.bean.BizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.SimCardCheckBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.simcard.WriteCardBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;

public class RemoteWriteCardIntfBean extends CSBizBean
{

    public IDataset changeCardReg(IData input) throws Exception
    {
        input = convertData(input);
        String newImsi = input.getString("NEW_IMSI");
        String serialNumber = input.getString("SERIAL_NUMBER");
        SimCardCheckBean checkBean = BeanManager.createBean(SimCardCheckBean.class);
        checkBean.preOccupySimCard(serialNumber, input.getString("NEW_SIM_CARD_NO"), "0", "1");
        if (StringUtils.isEmpty(newImsi) || StringUtils.isEmpty(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "换卡必要元素新IMSI与手机号码不能为空！");
        }
        IData param = new DataMap();
        param.put("IMSI", newImsi);
        param.put("SERIAL_NUMBER", serialNumber);
        IDataset reSet = CSAppCall.call("SS.ChangeCardSVC.tradeReg", param);
        return reSet;
    }

    /**
     * 一级BOSS，通过服务号码或imsi获取地州信息及品牌
     * 
     * @return IData
     * @exception
     */
    private IData convertData(IData params) throws Exception
    {
        if (params.containsKey("IMSI_NUMBER"))
        {
            params.put("NEW_SIM_CARD_NO", params.getString("IMSI_NUMBER"));
        }

        if (params.containsKey("IMSI"))
        {
            params.put("NEW_IMSI", params.getString("IMSI"));
        }
        if (params.containsKey("MobileNum"))
        {
            params.put("SERIAL_NUMBER", params.getString("MobileNum"));
        }
        if (params.containsKey("PHONENUM"))
        {
            params.put("SERIAL_NUMBER", params.getString("PHONENUM"));
        }
        if (params.containsKey("RSRV_STR19"))
        {
            params.put("NEW_SIM_CARD_NO", params.getString("RSRV_STR19"));
        }
        if (params.containsKey("RSRV_STR20"))
        {
            params.put("NEW_IMSI", params.getString("RSRV_STR20"));
        }
        if (params.containsKey("RSRV_STR1") && params.getString("KIND_ID").equals("BIP2B006_T2000002_1_0"))
        {
            params.put("NEW_IMSI", params.getString("RSRV_STR1"));
        }
        if (params.containsKey("RESULTCODE") && params.getString("KIND_ID").equals("BIP2B006_T2000002_1_0"))
        {
            params.put("EXE_RESULT", params.getString("RESULTCODE"));
        }
        return params;
    }

    public IDataset getSpeSimInfo(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        if (StringUtils.isEmpty(input.getString("OSNDUNS", "")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "接口参数检查，输入参数【OSNDUNS】不得为空");
        }
        input = convertData(input);
        // getEparchyAndBrandInterBoss(input);
        IDataset configInfos = CommparaInfoQry.getCommPkInfo("CSM", "189", "1", getTradeEparchyCode());
        if (IDataUtil.isEmpty(configInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取写卡配置信息出错！");
        }
        IData configInfo = configInfos.getData(0);

        IData userProdInfo = UcaInfoQry.qryUserMainProdInfoBySn(input.getString("SERIAL_NUMBER"));

        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        // 需要特殊处理，这里的白卡ID没有实际意义，不能通过它获取写卡数据
        IData speSimInfo = bean.getSpeSimInfo(input.getString("SERIAL_NUMBER"), "", "2", "0").getData(0);

        if (speSimInfo.getInt("X_RESULTCODE") != 0)
        {
            returnInfo.put("CHARGE_ID", input.getString("CHARGE_ID"));
            returnInfo.put("RES_TRADE_TYPE", userProdInfo.getString("BRAND_CODE"));
            returnInfo.put("ANSWER_STATE", "11");// 异地写多号卡
        }

        returnInfo.put("ANSWER_STATE", "00");
        returnInfo.put("IMSI", speSimInfo.getString("IMSI"));
        returnInfo.put("IMSI_NUMBER", speSimInfo.getString("SIM_CARD_NO"));
        returnInfo.put("SIM_CARD_NO", speSimInfo.getString("SIM_CARD_NO"));
        returnInfo.put("ID", configInfo.getString("PARA_CODE2"));
        returnInfo.put("RSRV_STR10", configInfo.getString("PARA_CODE2"));
        returnInfo.put("KI", speSimInfo.getString("KI"));
        returnInfo.put("PIN", speSimInfo.getString("PIN"));
        returnInfo.put("PUK", speSimInfo.getString("PUK"));
        returnInfo.put("PIN2", speSimInfo.getString("PIN2"));
        returnInfo.put("PUK2", speSimInfo.getString("PUK2"));
        returnInfo.put("OPC", speSimInfo.getString("OPC"));
        returnInfo.put("RES_TRADE_TYPE", this.getResTradeType(userProdInfo.getString("BRAND_CODE")));
        returnInfo.put("BRAND_CODE", userProdInfo.getString("BRAND_CODE"));
        // add 20091025 根据SWITCH_TYPE_CODE 字段的值不同解密方式是不同的,新疆如果传20，不是用西门子解密，否则用西门子解密,其他省如果传10用西门子解密，否则不是用西门子解密
        returnInfo.put("SWITCH_TYPE_CODE", speSimInfo.getString("SWITCH_TYPE_CODE"));

        // 发送短信通知
        sendWriteCardSMS(input);
        // ..............

        IDataset set = new DatasetList();
        set.add(returnInfo);
        return set;

    }
    
    /**
     * 
     * @Description: 转换RES_TRADE_TYPE
     * @param brandCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 22, 2014 12:16:31 PM
     */
    public String getResTradeType(String brandCode)throws Exception
    {
        if(StringUtils.isNotBlank(brandCode))
        {
            if("G001".equals(brandCode))
            {
                return "01";
            }
            else if("G002".equals(brandCode))
            {
                return "02";
            }
            else if("G010".equals(brandCode))
            {
                return "03";
            }
            else
            {
                return "09";
            }
        }
        else
        {
            return "09";
        }
    }
    
    /**
     * 空中写卡,获取写卡信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getSpeSimInfoForAir(IData input) throws Exception
    {
        WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
        // 需要特殊处理，这里的白卡ID没有实际意义，不能通过它获取写卡数据
        IDataset speSimInfo = bean.getSpeSimInfo(input.getString("SERIAL_NUMBER"), input.getString("EMPTY_CARD_ID"), "0", "2");
        return speSimInfo;
    }

    /**
     * 操作工单传递 trade_type_code=145
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData iboperTrans(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("RSRV_STR1", input.getString("USER_PASSWD"));
        param.put("RSRV_STR3", input.getString("IDCARDNUM"));// 证件号
        param.put("RSRV_STR4", input.getString("FORWARDTO"));// 无条件前转号码
        param.put("RSRV_STR5", input.getString("RVISITSVCDESC"));// 漫游地上门服务
        param.put("RSRV_STR6", input.getString("HVISITSVCDESC"));// 归属地上门服务
        param.put("RSRV_STR7", input.getString("POSTCODE"));// 寄送地邮政编码
        param.put("RSRV_STR8", input.getString("ADDRESS"));// 寄送地址
        param.put("RSRV_STR9", input.getString("RECIPIENTS"));// 收件人地址
        param.put("RSRV_STR10", input.getString("PHONENUM"));// 联系电话
        param.put("OPER_FEE", input.getString("OPER_FEE"));
        if ("00".equals("IDCARDTYPE"))
        {
            param.put("RSRV_STR2", "0");
        }
        else
        {
            param.put("RSRV_STR2", "Z");
        }
        param.put("TRADE_TYPE_CODE", "145");
        IData returnInfo = new DataMap();
        IDataset tradeset = TradeInfoQry.getMainTradeBySN(input.getString("SERIAL_NUMBER"), "145");
        if (IDataUtil.isNotEmpty(tradeset))
        {
        	returnInfo.put("X_RSPTYPE", "2");
            returnInfo.put("X_RESULTCODE", "711004");
            returnInfo.put("X_RESULTINFO", "用户有相同业务类型的未完工的业务，业务不能继续！");
            returnInfo.put("X_RSPCODE", "711004");
            returnInfo.put("X_RSPDESC", "用户有相同业务类型的未完工的业务，业务不能继续！");
            return returnInfo;
        }
        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(userInfo))
        {
        	returnInfo.put("X_RSPTYPE", "2");
            returnInfo.put("X_RESULTCODE", "711005");
            returnInfo.put("X_RESULTINFO", "手机号码对应的用户不存在！");
            returnInfo.put("X_RSPCODE", "711005");
            returnInfo.put("X_RSPDESC", "手机号码对应的用户不存在！");
            return returnInfo;
        }
        IData prodInfo = UcaInfoQry.qryUserMainProdInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(prodInfo))
        {
        	returnInfo.put("X_RSPTYPE", "2");
            returnInfo.put("X_RESULTCODE", "711006");
            returnInfo.put("X_RESULTINFO", "用户有相同业务类型的未完工的业务，业务不能继续！");
            returnInfo.put("X_RSPCODE", "711006");
            returnInfo.put("X_RSPDESC", "用户有相同业务类型的未完工的业务，业务不能继续！");
            return returnInfo;
        }
        if ("GS01".equals(prodInfo.getString("BRAND_CODE")))
        {
            returnInfo.put("X_RESULTCODE", "0");
            returnInfo.put("X_RESULTINFO", "该用户为神州行用户");
            returnInfo.put("X_RSPCODE", "0");
            returnInfo.put("X_RSPDESC", "该用户为神州行用户");
            return returnInfo;
        }
        String tradeId = SeqMgr.getTradeId();
        String orderId = SeqMgr.getOrderId();
        IData tradeInfo = new DataMap();
        tradeInfo.put("TRADE_ID", tradeId);
        tradeInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
        tradeInfo.put("ORDER_ID", orderId);
        tradeInfo.put("TRADE_TYPE_CODE", "145");
        tradeInfo.put("PRIORITY", "0");
        tradeInfo.put("SUBSCRIBE_TYPE", "0");
        tradeInfo.put("SUBSCRIBE_STATE", "0");
        tradeInfo.put("NEXT_DEAL_TAG", "0");
        tradeInfo.put("IN_MODE_CODE", getVisit().getInModeCode());
        tradeInfo.put("USER_ID", userInfo.getString("USER_ID"));
        tradeInfo.put("CUST_ID", userInfo.getString("CUST_ID"));
        // tradeInfo.put("ACCT_ID", value);
        tradeInfo.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        tradeInfo.put("EPARCHY_CODE", userInfo.getString("EPARCHY_CODE"));
        tradeInfo.put("NET_TYPE_CODE", userInfo.getString("NET_TYPE_CODE"));
        tradeInfo.put("CITY_CODE", userInfo.getString("CITY_CODE"));
        tradeInfo.put("PRODUCT_ID", prodInfo.getString("PRODUCT_ID"));
        tradeInfo.put("BRAND_CODE", prodInfo.getString("BRAND_CODE"));
        String sysTime = SysDateMgr.getSysTime();
        tradeInfo.put("EXEC_TIME", sysTime);
        tradeInfo.put("ACCEPT_DATE", sysTime);
        tradeInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
        tradeInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
        tradeInfo.put("TRADE_CITY_CODE", getVisit().getCityCode());
        tradeInfo.put("TRADE_EPARCHY_CODE", getTradeEparchyCode());
        tradeInfo.put("OPER_FEE", param.getString("OPER_FEE"));
        tradeInfo.put("FOREGIFT", "0");
        tradeInfo.put("ADVANCE_PAY", "0");
        tradeInfo.put("FEE_STATE", "0");
        tradeInfo.put("PROCESS_TAG_SET", "0000000000000000000000000000000000000000");
        tradeInfo.put("OLCOM_TAG", "0");
        tradeInfo.put("CANCEL_TAG", "0");
        tradeInfo.put("UPDATE_TIME", sysTime);
        tradeInfo.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        tradeInfo.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        tradeInfo.put("RSRV_STR1", param.getString("RSRV_STR1"));
        tradeInfo.put("RSRV_STR2", param.getString("RSRV_STR2"));
        tradeInfo.put("RSRV_STR3", param.getString("RSRV_STR3"));
        tradeInfo.put("RSRV_STR4", param.getString("RSRV_STR4"));
        tradeInfo.put("RSRV_STR5", param.getString("RSRV_STR5"));
        tradeInfo.put("RSRV_STR6", param.getString("RSRV_STR6"));
        tradeInfo.put("RSRV_STR7", param.getString("RSRV_STR7"));
        tradeInfo.put("RSRV_STR8", param.getString("RSRV_STR8"));
        tradeInfo.put("RSRV_STR9", param.getString("RSRV_STR9"));
        tradeInfo.put("RSRV_STR10", param.getString("RSRV_STR10"));
        String routeId = Route.getJourDb(BizBean.getVisit().getStaffEparchyCode());
        Dao.insert("TF_B_TRADE", tradeInfo, routeId);

        tradeInfo.put("ORDER_TYPE_CODE", "145");

        tradeInfo.put("ORDER_STATE", "0");
        Dao.insert("TF_B_ORDER", tradeInfo, routeId);

        returnInfo.put("TRADE_ID", tradeId);
        returnInfo.put("X_RESULTCODE", "0");
        returnInfo.put("X_RESULTINFO", "受理成功");
        returnInfo.put("X_RSPCODE", "0");
        returnInfo.put("X_RSPDESC", "受理成功");
        return returnInfo;
    }

    /**
     * 发送补卡告知短信
     * 
     * @param pd
     * @param tradeData
     * @param iData
     * @throws Exception
     */
    public void sendWriteCardSMS(IData input) throws Exception
    {
        // 取省份名称
        String proviceName = "";
        IDataset result = CommparaInfoQry.getCommparaInfoBy5("CSM", "1023", input.getString("OSNDUNS"), "0", getTradeEparchyCode(), null);
        if (IDataUtil.isNotEmpty(result))
        {
            proviceName = result.getData(0).getString("PARAM_NAME", "");
        }

        // 拼短信表参数
        IData param = new DataMap();
        String sysdate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        String sms = "尊敬的客户，您的号码" + input.getString("SERIAL_NUMBER") + "于" + sysdate.substring(4, 6) + "月" + sysdate.substring(6, 8) + "日" + sysdate.substring(8, 10) + "时" + sysdate.substring(10, 12) + "分在中国移动" + proviceName
                + "公司营业厅提出跨区补卡申请，我公司将尽快为您办理补卡业务。如有疑问请致电客服热线10086。";
        param.put("NOTICE_CONTENT", sms);
        param.put("EPARCHY_CODE", getTradeEparchyCode());
        param.put("IN_MODE_CODE", "0");
        param.put("RECV_OBJECT", input.getString("SERIAL_NUMBER"));
        param.put("RECV_ID", "99999999");
        param.put("REFER_STAFF_ID", getVisit().getStaffId());
        param.put("REFER_DEPART_ID", getVisit().getDepartId());
        param.put("REMARK", "跨区补卡告知短信");
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        param.put("SMS_NOTICE_ID", seq_id);
        param.put("PARTITION_ID", seq_id % 1000);
        param.put("SEND_COUNT_CODE", "1");
        param.put("REFERED_COUNT", "0");
        param.put("CHAN_ID", "C009");
        param.put("SMS_NET_TAG", "0");
        param.put("RECV_OBJECT_TYPE", "00");
        param.put("SMS_TYPE_CODE", "20");
        param.put("SMS_KIND_CODE", "02");
        param.put("NOTICE_CONTENT_TYPE", "0");
        param.put("FORCE_REFER_COUNT", "1");
        param.put("FORCE_OBJECT", "10086");
        param.put("SMS_PRIORITY", "3000");
        param.put("DEAL_STATE", "15");
        param.put("SEND_TIME_CODE", "1");
        param.put("SEND_OBJECT_CODE", "6");
        param.put("REFER_TIME", SysDateMgr.getSysTime());
        param.put("DEAL_TIME", SysDateMgr.getSysTime());
        param.put("MONTH", SysDateMgr.getCurMonth());
        param.put("DAY", SysDateMgr.getCurDay());
        param.put("ISSTAT", "0");
        param.put("TIMEOUT", "0");

        Dao.insert("ti_o_sms", param);
    }

    public IDataset updRmtWrtSimCard(IData input) throws Exception
    {
        input = convertData(input);
        String tag = input.getString("EXE_RESULT");
        if ("00".equals(tag) || "11".equals(tag))
        {// 成功和失败均修改SIM卡状态
            // 调用资源状态更新接口更新SIM卡信息
            String imsi = input.getString("NEW_IMSI", "");
            //String serialNumber = input.getString("ROUTEVALUE");
            // String simCardNo = input.getString("NEW_SIM_CARD_NO","");
            WriteCardBean bean = BeanManager.createBean(WriteCardBean.class);
            String xChoiceTag = "0";
            if ("11".equals(tag))
            {
                xChoiceTag = "1";
            }
            bean.remoteWriteUpdate(imsi, "", "", "0", xChoiceTag);
        }
        // 不需要返回值
        IDataset set = new DatasetList();
        set.add(input);
        return set;
    }

    public IDataset writeCardExistVerify(IData input) throws Exception
    {
        IData returnInfo = new DataMap();
        returnInfo.put("CHARGE_ID", input.getString("CHARGE_ID"));
        returnInfo.put("EXE_RESULT", "00");
        returnInfo.put("X_RSPTYPE", "0");
        returnInfo.put("X_RSPCODE", "0000");

        IDataset set = new DatasetList();
        set.add(returnInfo);
        return set;
    }

}
