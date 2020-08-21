
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpBizQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.plat.SpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserChargeRemindInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class ChargeRemindDealBean extends CSBizBean
{

    public int addTwoCheckSms(String FLOW_ID, String TRADE_ID, String SERIAL_NUMBER, String DEAL_STATE, String EXTEND_TAG, String SMS_CONTENT, String SP_CODE, String SP_NAME, String BIZ_CODE, String BIZ_NAME, String ANSWER_CONTENT, String TIMEOUT,
            String UPDATE_TIME, String INSERT_TIME, String EPARCHY_CODE, String RSRV_STR1, String RSRV_STR2, String RSRV_STR3, String RSRV_STR4, String RSRV_STR5, String OPR_SOURCE, String UPDATE_STAFF_ID, String UPDATE_DEPART_ID, String SMS_ID,
            String SMS_TYPE) throws Exception
    {
        IData cond = new DataMap();
        cond.put("FLOW_ID", FLOW_ID);
        cond.put("TRADE_ID", TRADE_ID);
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("DEAL_STATE", DEAL_STATE);
        cond.put("EXTEND_TAG", EXTEND_TAG);
        cond.put("SMS_CONTENT", SMS_CONTENT);
        cond.put("SP_CODE", SP_CODE);
        cond.put("SP_NAME", SP_NAME);
        cond.put("BIZ_CODE", BIZ_CODE);
        cond.put("BIZ_NAME", BIZ_NAME);
        cond.put("ANSWER_CONTENT", ANSWER_CONTENT);
        cond.put("TIMEOUT", TIMEOUT);
        cond.put("UPDATE_TIME", UPDATE_TIME);
        cond.put("INSERT_TIME", INSERT_TIME);
        cond.put("EPARCHY_CODE", EPARCHY_CODE);
        cond.put("RSRV_STR1", RSRV_STR1);
        cond.put("RSRV_STR2", RSRV_STR2);
        cond.put("RSRV_STR3", RSRV_STR3);
        cond.put("RSRV_STR4", RSRV_STR4);
        cond.put("RSRV_STR5", RSRV_STR5);
        cond.put("OPR_SOURCE", OPR_SOURCE);
        cond.put("UPDATE_STAFF_ID", UPDATE_STAFF_ID);
        cond.put("UPDATE_DEPART_ID", UPDATE_DEPART_ID);
        cond.put("SMS_ID", SMS_ID);
        cond.put("SMS_TYPE", SMS_TYPE);
        return Dao.executeUpdateByCodeCode("TI_O_TWOCHECK_SMS", "INS_SMS_TWOCHECK", cond, Route.CONN_CRM_CEN);
    }

    public IDataset checkChargeRemindDeal(String SERIAL_NUMBER, String TRADE_ID) throws Exception
    {
        return UserChargeRemindInfoQry.querrykChargeRemindBySeriaTrade(SERIAL_NUMBER, TRADE_ID);
    }

    public IDataset getUserDiscntByPk(String USER_ID_A) throws Exception
    {
        return UserDiscntInfoQry.getUserDiscntByPk(USER_ID_A);
    }

    public IDataset getUserProductAttrValue(String USER_ID, String INST_TYPE, String ATTR_CODE) throws Exception
    {
        return UserAttrInfoQry.getUserProductAttrValue(USER_ID, INST_TYPE, ATTR_CODE);
    }

    public IDataset queryBindDiscnts(String USER_ID, String SP_CODE, String SERVICE_CODE, String EPARCHY_CODE, String DATA_SP_CODE, boolean equalsFlag) throws Exception
    {
        if (equalsFlag)
        {
            return UserDiscntInfoQry.queryBindDiscntEquals(USER_ID, SP_CODE, SERVICE_CODE, EPARCHY_CODE, DATA_SP_CODE);
        }
        else
        {
            return UserDiscntInfoQry.queryBindDiscntUnequals(USER_ID, SP_CODE, SERVICE_CODE, EPARCHY_CODE, DATA_SP_CODE);
        }

    }

    public IDataset queryBizServiceInfos(String SERVICE_CODE, String SP_CODE, boolean flag60) throws Exception
    {
        if (flag60)
        {
            return SpInfoQry.queryBizServiceInfosByBillTypeSpBizCode(SERVICE_CODE, SP_CODE);
        }
        else
        {
            return SpInfoQry.queryBizServiceInfosBySpBizCode(SERVICE_CODE, SP_CODE);
        }
    }

    public IDataset queryCommparaDateInfos(String USER_ID, String SERVICE_ID, String EPARCHY_CODE, String SP_CODE, String SERVICE_CODE) throws Exception
    {
        return null;
    }

    public IDataset queryCommparaInfos(String PARAM_ATTR, String SERVICE_ID, String EPARCHY_CODE) throws Exception
    {
        return null;
    }

    public IDataset queryDiscntInfosByInstId(String USER_ID, String INST_ID) throws Exception
    {
        return UserDiscntInfoQry.queryDiscntInfosByInstId(USER_ID, INST_ID);
    }

    public IDataset queryDiscntsByCommpara(String USER_ID, String EPARCHY_CODE, String SERVICE_ID) throws Exception
    {
        return UserDiscntInfoQry.queryDiscntsByCommpara(USER_ID, EPARCHY_CODE, SERVICE_ID);
    }

    public IDataset queryDiscntsUnionCommpara(String USER_ID, String EPARCHY_CODE, String SERVICE_ID, String SERVICE_CODE, String SP_CODE) throws Exception
    {
        return UserDiscntInfoQry.queryDiscntsUnionCommpara(USER_ID, EPARCHY_CODE, SERVICE_ID, SERVICE_CODE, SP_CODE);
    }

    public IDataset queryPlatSvcCounts(String USER_ID, String SP_CODE, String SERVICE_CODE) throws Exception
    {
        return UserPlatSvcInfoQry.queryPlatSvcCounts(USER_ID, SP_CODE, SERVICE_CODE);
    }

    public IDataset queryPlatSvcInfos(String SERVICE_ID) throws Exception
    {
        return PlatSvcInfoQry.queryPlatSvcInfos(SERVICE_ID);
    }

    public IDataset queryPlatSvcInfosBySpBizCode(String BIZ_CODE, String SP_CODE) throws Exception
    {
        return PlatSvcInfoQry.queryPlatSvcInfosBySpBizCode(BIZ_CODE, SP_CODE);
    }

    public IDataset querySpBySpCodeAndBizCode(String SPCODE, String BIZCODE) throws Exception
    {
        return MSpBizQry.querySpBySpCodeAndBizCode(SPCODE, BIZCODE);
    }

    public IDataset querySpInfosBySpcodeSpstatus(String SP_CODE) throws Exception
    {
        return SpInfoQry.querySpInfosBySpcodeSpstatus(SP_CODE);
    }

    public IDataset queryStaticInfos(String TYPE_ID, String PDATA_ID, String DATA_ID) throws Exception
    {
        return StaticInfoQry.queryStaticInfos(TYPE_ID, PDATA_ID, DATA_ID);
    }

    public IData queryStaticSpCode(String PDATA_ID, String TYPE_ID) throws Exception
    {
        return StaticInfoQry.queryStaticValueByPdataId(PDATA_ID, TYPE_ID);
    }

    public IData queryStaticValueByPdataId(String PDATA_ID, String TYPE_ID) throws Exception
    {
        return StaticInfoQry.queryStaticValueByPdataId(PDATA_ID, TYPE_ID);
    }

    public IDataset querySvcInfosByInstId(String USER_ID, String INST_ID, String routeId) throws Exception
    {
        return UserSvcInfoQry.querySvcInfosByInstId(USER_ID, INST_ID, routeId);
    }

    public IDataset queryUserGroupInfos(String USER_ID, String REMOVE_TAG) throws Exception
    {
        return GrpInfoQry.queryUserGroupInfos(USER_ID, REMOVE_TAG);
    }

    public IDataset queryUserOtherInfos(String USER_ID, String RSRV_VALUE_CODE, String RSRV_VALUE) throws Exception
    {
        return UserOtherInfoQry.queryUserOtherInfos(USER_ID, RSRV_VALUE_CODE, RSRV_VALUE);
    }

    public IDataset queryUserPlatSvcInfos(String USER_ID, String SERVICE_ID) throws Exception
    {
        return UserPlatSvcInfoQry.queryUserPlatSvcInfos(USER_ID, SERVICE_ID);
    }

    public IDataset queryUserSvcInfos(String USER_ID, String SERVICE_ID) throws Exception
    {
        return UserSvcInfoQry.qrySvcInfoByUserIdSvcId(USER_ID, SERVICE_ID);
    }

    public int recordChargeRemindDealed(String RSRV_STR4, String RSRV_STR5, String SERIAL_NUMBER, String TRADE_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RSRV_STR4", RSRV_STR4);
        cond.put("RSRV_STR5", RSRV_STR5);
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("TRADE_ID", TRADE_ID);
        return Dao.executeUpdateByCodeCode("TP_F_USER_CHARGEREMIND", "UPD_CHARGEREMIND_BY_SERIALNUM_TRADEID", cond);
    }

    public int recordChargeRemindDealResult(String RSRV_STR4, String RSRV_STR5, String SERIAL_NUMBER, String TRADE_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("RSRV_STR4", RSRV_STR4);
        cond.put("RSRV_STR5", RSRV_STR5);
        cond.put("SERIAL_NUMBER", SERIAL_NUMBER);
        cond.put("TRADE_ID", TRADE_ID);
        return Dao.executeUpdateByCodeCode("TP_F_USER_CHARGEREMIND", "UPD_CHARGEREMIND_NONEED_SENDSMS", cond);
    }

}
