/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.activeconsum;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;

/**
 * @REQ201608100026 新增套餐推荐界面开发需求
 */
public class ActiveConsumBean extends CSBizBean
{
    /**
     * 查询用户三个月信息平均
     * */
    public static IDataset qryConsumInfos(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", input.getString("USER_ID"));
        return Dao.qryByCode("TL_B_USER_CONSUM_LOG", "SEL_CONSUM_USER_INFOS", param);
    }

    /**
     * 查询用户原有套餐信息
     */
    public static IDataset qryUserProductOld(IData input) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", input.getString("USER_ID"));
        return Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_USER_OLD_PRODUCT", param);
    }

    /**
     * 查询用户新套餐信息
     */
    public static IDataset qryUserProductNew(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("USER_ID", input.getString("USER_ID"));
        return Dao.qryByCode("TL_B_USER_CONSUM", "SEL_USER_CONSUM_BY_USERID", data);
    }

    /**
     * 查询用户产品描述
     */
    public static String qryProdInfoDesc(String prodId) throws Exception
    {
        String productExplain = UProductInfoQry.getProductExplainByProductId(prodId);

        return productExplain;
    }

    /**
     * 查询用户优惠描述
     */
    public static String qryDiscntInfoDesc(String discntCode) throws Exception
    {
        String discntExplain = UDiscntInfoQry.getDiscntExplainByDiscntCode(discntCode);

        return discntExplain;
    }

    /**
     * 重新计算
     */
    public static IData reConsum(IData input) throws Exception
    {
        // 存储过程参数名
        String callType = input.getString("CALL_TYPE", "");
        String[] procParamName = new String[]
        { "pi_user_id", "pi_serial_number", "pi_cal_type", "pi_tel_1", "pi_tel_2", "pi_tel_3", "pi_gprs", "pi_sms", "PI_wlan", "PI_all_fee", "PO_RESULT_CODE", "PO_RESULT_INFO" };
        IData procData = new DataMap();
        procData.put("pi_user_id", input.getString("USER_ID"));
        procData.put("pi_serial_number", input.getString("SERIAL_NUMBER")); // 不含税价
        procData.put("pi_cal_type", input.getString("CALL_TYPE")); // 税额
        procData.put("pi_tel_1", input.getString("TELE_TIME1", "")); // 税率
        procData.put("pi_tel_2", input.getString("TELE_TIME2", "")); // 计税方式
        procData.put("pi_tel_3", input.getString("TELE_TIME3", "")); // 混合税率
        procData.put("pi_gprs", input.getString("GPRS_CNT", ""));
        procData.put("pi_sms", input.getString("SMS_CNT", ""));
        procData.put("PI_wlan", input.getString("WLAN", ""));
        procData.put("PI_all_fee", input.getString("ALL_FEE", ""));

        // 调用存储过程
        Dao.callProc("P_CSM_CONSUM_USER", procParamName, procData);
        // 得到输出参数
        IData outData = new DataMap();
        outData.put("RESULT_CODE", procData.getString("PO_RESULT_CODE"));
        outData.put("RESULT_INFO", procData.getString("PO_RESULT_INFO"));
        return outData;
    }

    /**
     * REQ201611250012 套餐推荐界面补充需求（增加描述、时间等） chenxy 20161216 推荐套餐下发短信
     * */
    public static void sendConsumSMS(IData input) throws Exception
    {
        /* 插短信 */
        String prodNew = input.getString("PRODUCT_NEW", "");// 推荐套餐
        String prodDesc = input.getString("PRODUCT_DESC", "");// 套餐描述
        String content = "尊敬的用户，根据您的话费情况，我们推荐您使用以下套餐：【" + prodNew + "】。(" + prodDesc + ")";
        IData smsData = new DataMap();
        smsData.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER", ""));
        smsData.put("USER_ID", input.getString("USER_ID", ""));
        String sysTime = SysDateMgr.getSysTime();
        sysTime = sysTime.substring(sysTime.indexOf(":") - 2);
        smsData.put("NOTICE_CONTENT", content);
        smsData.put("STAFF_ID", CSBizBean.getVisit().getStaffId());
        smsData.put("DEPART_ID", CSBizBean.getVisit().getDepartId());
        smsData.put("REMARK", "推荐套餐短信");
        smsData.put("BRAND_CODE", "");
        smsData.put("FORCE_START_TIME", "");
        smsData.put("FORCE_END_TIME", "");
        smsSent(smsData);
    }

    /**
     * REQ201611250012 套餐推荐界面补充需求（增加描述、时间等） chenxy 20161216 推荐套餐下发短信
     * */
    public static void smsSent(IData param) throws Exception
    {

        String serialNumber = param.getString("SERIAL_NUMBER", "");
        String userId = param.getString("USER_ID", "");
        String content = param.getString("NOTICE_CONTENT", "");
        String staffId = param.getString("STAFF_ID", getVisit().getStaffId());
        String departId = param.getString("DEPART_ID", getVisit().getDepartId());
        String remark = param.getString("REMARK", "");
        String brandCode = param.getString("BRAND_CODE", "");
        String forStartTime = param.getString("FORCE_START_TIME", "");
        String forEndTime = param.getString("FORCE_END_TIME", "");
        IData smsData = new DataMap();

        // 插入短信表
        String seq = SeqMgr.getSmsSendId();
        long seq_id = Long.parseLong(seq);
        long partition_id = seq_id % 1000;

        smsData.put("SMS_NOTICE_ID", seq_id);
        smsData.put("PARTITION_ID", partition_id);
        smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        smsData.put("BRAND_CODE", brandCode);
        smsData.put("IN_MODE_CODE", getVisit().getInModeCode());
        smsData.put("SMS_NET_TAG", "0");
        smsData.put("CHAN_ID", "GTM");
        smsData.put("SEND_OBJECT_CODE", "1");
        smsData.put("SEND_TIME_CODE", "1");
        smsData.put("SEND_COUNT_CODE", "");
        smsData.put("RECV_OBJECT_TYPE", "00");
        smsData.put("RECV_OBJECT", serialNumber);
        smsData.put("RECV_ID", userId);
        smsData.put("SMS_TYPE_CODE", "20");
        smsData.put("SMS_KIND_CODE", "08");
        smsData.put("NOTICE_CONTENT_TYPE", "0");
        smsData.put("NOTICE_CONTENT", content);
        smsData.put("REFERED_COUNT", "");
        smsData.put("FORCE_REFER_COUNT", "1");
        smsData.put("FORCE_OBJECT", "10086");
        smsData.put("FORCE_START_TIME", forStartTime);
        smsData.put("FORCE_END_TIME", forEndTime);
        smsData.put("SMS_PRIORITY", "1001");
        smsData.put("REFER_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        smsData.put("REFER_STAFF_ID", staffId);
        smsData.put("REFER_DEPART_ID", departId);
        smsData.put("DEAL_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        smsData.put("DEAL_STAFFID", staffId);
        smsData.put("DEAL_DEPARTID", departId);
        smsData.put("DEAL_STATE", "15");
        smsData.put("REMARK", remark);
        smsData.put("REVC1", "");
        smsData.put("REVC2", "");
        smsData.put("REVC3", "");
        smsData.put("REVC4", "");
        smsData.put("MONTH", SysDateMgr.getCurMonth());
        smsData.put("DAY", SysDateMgr.getCurDay());

        Dao.insert("TI_O_SMS", smsData);
    }

}
