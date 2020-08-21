
package com.asiainfo.veris.crm.order.soa.person.busi.interboss.mobileoperation;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.RemoteCrossRegServiceException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interboss.util.LanuchUtil;

public class RemoteCrossRegServiceBean extends CSBizBean
{

    static transient final Logger logger = Logger.getLogger(RemoteCrossRegServiceBean.class);

    public IDataset getCrossRegserviceInfo(IData param) throws Exception
    {
        String userId = param.getString("MSISDN");
        IDataset ret = UserOtherInfoQry.getUserOtherByUserId(userId);
        return ret;
    }

    public IData getCustInfo(IData param) throws Exception
    {

        IData userData = new DataMap();
        IDataset userOtherData = new DatasetList();
        IData data = new DataMap();
        LanuchUtil logutil = new LanuchUtil();

        String serialNumber = param.getString("SERIAL_NUMBER");
        String name = param.getString("NAME");
        String idCardType = logutil.decodeIdType(param.getString("IDCARDTYPE"));
        String idCardNum = param.getString("IDCARDNUM");
        String userPasswd = param.getString("USER_PASSWD");
        String routeType = param.getString("ROUTETYPE"); // 路由类型 00-省代码，01-手机号
        String mobileNum = param.getString("MOBILENUM");

        /**
         * 如果在TF_F_USER_OTHER表中有数据 且START_DATE距系统当前时间不超过一年不能办理跨区入网业务
         */
        String userId = param.getString("SERIAL_NUMBER");

        userOtherData = UserOtherInfoQry.getUserOtherByCrossRegservice(userId);

        if (IDataUtil.isNotEmpty(userOtherData))
        {
            userData = userOtherData.getData(0);
            if (StringUtils.isNotBlank(userData.getString("START_DATE", "")))
            {
                CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_4);
            }
            else
            {
                data = IBossCall.remoteCrossRegServiceGetCustInfoIBOSS(serialNumber, name, idCardType, idCardNum, userPasswd, routeType, mobileNum);
                // data.put("X_RSPCODE", "0000");data.put("X_RSPTYPE", "0");data.put("RESULT", "00");
                // data.put("ALL_CON_SCORE", "2");data.put("CLASS_LEVEL", "1");
                // data.put("LEVEL_DATE", "2016-05-28 10:41:31");data.put("JOIN_DATE", "2011-05-28 10:41:31");
                // data.put("BRAND_AWARD_SCORE", "3");data.put("YEAR_AWARD_SCORE", "6");data.put("OTHER_SCORE", "3");
                // data.put("USE_SCORE", "1");data.put("ABLE_SCORE", "7");
                if (logger.isDebugEnabled())
                    logger.debug("-----IBOSS接口(BIP2B009_T2040001_0_0)---返回数据-------" + data);
                if ("0000".equals(data.getString("X_RSPCODE")))
                {
                    String lanuchTdType = "";
                    lanuchTdType = logutil.encodeIdType(data.getString("IDCARDTYPE"));
                    data.put("IDCARDTYPE", lanuchTdType);
                }
            }
        }

        return data;
    }

    public void insertUserOther(String serialNumber, String idCardType, String idCardNum, String name, String allConScore, String brandAwardScore, String yearAwardScore, String otherScore, String useScore, String ableScore, String classLevel,
            String levelDate, String joinDate, String InstId) throws Exception
    {
        IData userOther = new DataMap();
        String userId = serialNumber;
        userOther.put("PARTITION_ID", userId.substring(userId.length() - 4));
        userOther.put("USER_ID", userId);
        userOther.put("RSRV_VALUE_CODE", "KQFW");
        userOther.put("RSRV_VALUE", "0");
        userOther.put("RSRV_STR1", userId);
        userOther.put("RSRV_STR2", ableScore);
        String brandCode = "";
        IData userInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
        if (IDataUtil.isNotEmpty(userInfo))
        {
            brandCode = userInfo.getString("BRAND_CODE");
        }

        String scoreTypeCode = "0";
        if ("G010".endsWith(brandCode))
        {
            scoreTypeCode = "1";
        }
        else if ("G001".endsWith(brandCode))
        {
            scoreTypeCode = "0";
        }
        else
        {
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_6);
        }

        userOther.put("RSRV_STR3", scoreTypeCode);
        userOther.put("RSRV_STR4", classLevel);
        userOther.put("RSRV_STR5", idCardType);
        userOther.put("RSRV_STR6", idCardNum);
        userOther.put("RSRV_STR7", name);

        userOther.put("RSRV_STR9", allConScore);
        userOther.put("RSRV_STR10", brandAwardScore);
        userOther.put("RSRV_STR11", yearAwardScore);
        userOther.put("RSRV_STR12", otherScore);
        userOther.put("RSRV_STR13", useScore);
        userOther.put("RSRV_STR14", ableScore);
        userOther.put("RSRV_STR15", classLevel);// 客户级别 0－普通用户（动感地带用户为普通用户） 3－银卡2－金卡1－钻石卡
        userOther.put("RSRV_STR16", levelDate);
        userOther.put("RSRV_STR17", joinDate);

        userOther.put("STAFF_ID", getVisit().getStaffId());
        userOther.put("DEPART_ID", getVisit().getDepartId());
        userOther.put("START_DATE", SysDateMgr.getSysDate());
        userOther.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        userOther.put("UPDATE_TIME", SysDateMgr.getSysDate());
        userOther.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        userOther.put("UPDATE_DEPART_ID", getVisit().getDepartId());
        userOther.put("INST_ID", InstId);
        Dao.insert("TF_F_USER_OTHER", userOther);
    }

    public IData updateInfo(IData idata) throws Exception
    {

        IData inparam = new DataMap();
        LanuchUtil logutil = new LanuchUtil();
        String serialNumber = idata.getString("cond_SERIAL_NUMBER");
        String name = idata.getString("cond_NAME");
        String idCardType = idata.getString("cond_IDCARDTYPE");
        String idCardNum = idata.getString("cond_IDCARDNUM");
        String userPasswd = idata.getString("cond_USER_PASSWD");
        String routeType = idata.getString("cond_ROUTETYPE"); // 路由类型 00-省代码，01-手机号
        String mobileNum = idata.getString("cond_MOBILENUM");

        String allConScore = idata.getString("ALL_CON_SCORE");
        String brandAwardScore = idata.getString("BRAND_AWARD_SCORE");
        String yearAwardScore = idata.getString("YEAR_AWARD_SCORE");
        String otherScore = idata.getString("OTHER_SCORE");
        String useScore = idata.getString("USE_SCORE");
        String ableScore = idata.getString("ABLE_SCORE");
        String classLevel = idata.getString("CLASS_LEVEL");
        String levelDate = idata.getString("LEVEL_DATE");
        String joinDate = idata.getString("JOIN_DATE");

        String instId = SeqMgr.getInstId();
        // 将跨区入网的信息插TF_F_USER_OTHER表
        this.insertUserOther(serialNumber, idCardType, idCardNum, name, allConScore, brandAwardScore, yearAwardScore, otherScore, useScore, ableScore, classLevel, levelDate, joinDate, instId);

        // *********公共参数设置start****************//
        inparam.put("TRADE_TYPE_CODE", "414");
        inparam.put("NET_TYPE_CODE", "00");
        inparam.put("CUST_ID", "");
        inparam.put("CUST_NAME", idata.getString("cond_NAME"));
        inparam.put("USER_ID", "");
        inparam.put("ACCT_ID", "");
        inparam.put("BRAND_CODE", "");
        inparam.put("INTF_ID", instId);
        // *********公共参数设置end****************//
        String operId = logutil.writeLanuchLog(inparam);
        String trade_id = operId.split(",")[0];
        String order_id = operId.split(",")[1];

        IData data = IBossCall.remoteCrossRegServiceUpdateInfoIBOSS(serialNumber, name, idCardType, idCardNum, userPasswd, routeType, mobileNum, allConScore, brandAwardScore, yearAwardScore, otherScore, useScore, ableScore, classLevel, levelDate,
                joinDate);
        if (logger.isDebugEnabled())
            logger.debug("-----IBOSS接口(BIP2B009_T2040004_0_0)---返回数据-------" + data);

        if (!"0".equals(data.getString("X_RSPTYPE")) || !"0000".equals(data.getString("X_RSPCODE")))
        {
            if ("2998".equals(data.getString("X_RSPCODE")))
            {
                data.put("X_RSPDESC", "落地方：" + data.getString("X_RSPDESC"));
            }
            CSAppException.apperr(RemoteCrossRegServiceException.CRM_CROSSREGSERVICE_3, data.getString("X_RSPDESC"));
        }
        // IData data = new DataMap();
        // data.put("X_RSPCODE", "0000");
        // data.put("X_RSPDESC", "11111111111");

        data.put("ORDER_ID", order_id);
        logutil.updateLanuchLog(trade_id, data.getString("X_RSPCODE"), data.getString("X_RSPDESC"));

        return data;
    }
}
