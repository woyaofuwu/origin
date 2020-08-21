
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.createnophonewideuser;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.common.util.NoPhoneTradeUtil;

public class NoPhoneWideUserCreateBean extends CSBizBean
{
	/**
	 *  查询无手机宽带账号资源
	 * @param inParam
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public IDataset queryWideNetAccoutInfo(IData inParam) throws Exception
    {
		IData param = new DataMap();
        param.put("ACCOUNT_ID", inParam.getString("ACCOUNT_ID"));
        param.put("STATE", inParam.getString("STATE"));
        IDataset widenetAcctInfo = Dao.qryByCode("TD_B_WIDENET_ACCOUNT", "SEL_TD_B_WIDENET_ACCOUNT", param, Route.CONN_CRM_CEN);
        return widenetAcctInfo;
    }
	
	
	/**
     * 随机获得有效可用的宽带账号
     * @param inParam
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public IDataset getValidWideNetAccount() throws Exception
    {
        IData param = new DataMap();
        param.put("STATE", "0");
        IDataset widenetAcctInfo = Dao.qryByCode("TD_B_WIDENET_ACCOUNT", "SEL_VALID_WIDENET_ACCOUNT", param, Route.CONN_CRM_CEN);
        return widenetAcctInfo;
    }
	
	/**
	 * 修改宽带账号状态
	 * @param accountId
	 * @return
	 * @throws Exception
	 * @author yuyj3
	 */
	public int updateWideNetAccoutState(String accountId,String state,String selectTime,String preemptionTime,String occupationTime,String emancipationTime) throws Exception
    {
	    StringBuilder sql = new StringBuilder(500);

        sql.append("UPDATE TD_B_WIDENET_ACCOUNT T ");
        sql.append("SET T.STATE = :STATE");
	    
	    IData param = new DataMap();
        param.put("ACCOUNT_ID", accountId);
        param.put("STATE", state);
        
        //传值为null不修改，为空字符串则修改为空
        if (null != selectTime)
        {
            param.put("SELECT_TIME", selectTime);
            
            if ("".equals(selectTime))
            {
                sql.append(",T.SELECT_TIME = :SELECT_TIME ");
            }
            else
            {
                sql.append(",T.SELECT_TIME = TO_DATE(:SELECT_TIME,'YYYY-MM-DD HH24:MI:SS') ");
            }
        }
        
        if (null != preemptionTime)
        {
            param.put("PREEMPTION_TIME", preemptionTime);
            
            if ("".equals(preemptionTime))
            {
                sql.append(",T.PREEMPTION_TIME = :PREEMPTION_TIME ");
            }
            else
            {
                sql.append(",T.PREEMPTION_TIME = TO_DATE(:PREEMPTION_TIME,'YYYY-MM-DD HH24:MI:SS') ");
            }
        }
        
        if (null != occupationTime)
        {
            param.put("OCCUPATION_TIME", occupationTime);
            
            if ("".equals(occupationTime))
            {
                sql.append(",T.OCCUPATION_TIME = :OCCUPATION_TIME ");
            }
            else
            {
                sql.append(",T.OCCUPATION_TIME = TO_DATE(:OCCUPATION_TIME,'YYYY-MM-DD HH24:MI:SS') ");
            }
        }
        
        if (null != emancipationTime)
        {
            param.put("EMANCIPATION_TIME", emancipationTime);
            
            if ("".equals(emancipationTime))
            {
                sql.append(",T.EMANCIPATION_TIME = :EMANCIPATION_TIME");
            }
            else
            {
                sql.append(",T.EMANCIPATION_TIME = TO_DATE(:EMANCIPATION_TIME,'YYYY-MM-DD HH24:MI:SS')");
            }
        }
        
        sql.append("WHERE T.ACCOUNT_ID = :ACCOUNT_ID ");

        int updResult = Dao.executeUpdate(sql, param, Route.CONN_CRM_CEN);
        
        return updResult;
    }
	
	/**
     * 修改历史订单预留字段9 （无手机宽带开户用来标记服开是否完工）
     * @param trade_id
     * @param subscribe_state
     * @throws Exception
     * @author yuyj3
     */
    public void updateBhTradeRsrvStr9(String tradeId, String rsrvStr9) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("RSRV_STR9", rsrvStr9);

        StringBuilder sql = new StringBuilder(100);

        sql.append("UPDATE TF_BH_TRADE T ");
        sql.append("SET T.RSRV_STR9 = :RSRV_STR9 ");
        sql.append("WHERE T.TRADE_ID = TO_NUMBER(:TRADE_ID) ");

        Dao.executeUpdate(sql, param, Route.getJourDbDefault());
    }
    
    
    /**
     * 查询可取消的无手机开户订单
     * @param tradeId
     * @return
     * @throws Exception
     * @author yuyj3
     */
    public void updateTradeBhInfo(String tradeId, String finishDate, String cancelStaffId, String cancelDepartId, String cancelCityCode,String cancelEparchCode) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
        param.put("SUBSCRIBE_STATE", "A");
        param.put("CANCEL_TAG", "1");
        param.put("FINISH_DATE", finishDate);
        param.put("CANCEL_DATE", finishDate);
        param.put("CANCEL_STAFF_ID", cancelStaffId);
        param.put("CANCEL_DEPART_ID", cancelDepartId);
        param.put("CANCEL_CITY_CODE", cancelCityCode);
        param.put("CANCEL_EPARCHY_CODE", cancelEparchCode);
        
        SQLParser parser = new SQLParser(param);
        parser.addSQL("UPDATE TF_BH_TRADE T ");
        parser.addSQL("SET T.CANCEL_TAG=:CANCEL_TAG, ");
        parser.addSQL("T.SUBSCRIBE_STATE=:SUBSCRIBE_STATE, ");
        parser.addSQL("T.FINISH_DATE= TO_DATE(:FINISH_DATE,'YYYY-MM-DD HH24:MI:SS'), ");
        parser.addSQL("T.CANCEL_DATE= TO_DATE(:CANCEL_DATE,'YYYY-MM-DD HH24:MI:SS'), ");
        parser.addSQL("T.CANCEL_STAFF_ID=:CANCEL_STAFF_ID, ");
        parser.addSQL("T.CANCEL_DEPART_ID=:CANCEL_DEPART_ID, ");
        parser.addSQL("T.CANCEL_CITY_CODE=:CANCEL_CITY_CODE, ");
        parser.addSQL("T.CANCEL_EPARCHY_CODE=:CANCEL_EPARCHY_CODE ");
        parser.addSQL("WHERE T.TRADE_ID= :TRADE_ID");
        
        Dao.executeUpdate(parser, Route.getJourDb());
    }

    /**
     * 校验录入的宽带服务号码是否可用
     * 1) 校验该号码是否是在规定的号段内，如果不在，则不能使用；
     * 2) 校验该号码是否存在有效的手机用户，如果存在，则不能使用；
     * 3) 校验该号码是否存在有效的宽带用户，如果存在，则不能使用；
     * 4) 如果存在已经校验通过的号码资源，则释放选占资源；
     * @param input
     * @return IData
     * @author ApeJungle
     */
    public IData checkWidenetAcctIdAvailable(IData input) throws Exception {
        String selectedWidenetAcctId = input.getString("SELECTED_WIDENET_ACCTID");
        String preOccupiedWidenetAcctId = input.getString("PRE_OCCUPIED_WIDENET_ACCTID");
        if (!NoPhoneTradeUtil.checkSerialNumberValidation(selectedWidenetAcctId)) {
            CSAppException.apperr(BizException.CRM_BIZ_5, "该宽带账号[" + selectedWidenetAcctId + "]不在业务允许的开户号段中！");
        }

        IData phoneUserInfo = UcaInfoQry.qryUserInfoBySn(selectedWidenetAcctId);
        if (IDataUtil.isNotEmpty(phoneUserInfo)) {
            CSAppException.apperr(BizException.CRM_BIZ_5, "该宽带账号[" + selectedWidenetAcctId + "]已经存在有效的手机用户信息，请录入其它宽带账号！");
        }

        IData widenetUserInfo = UcaInfoQry.qryUserInfoBySn("KD_" + selectedWidenetAcctId);
        if (IDataUtil.isNotEmpty(widenetUserInfo)) {
            CSAppException.apperr(BizException.CRM_BIZ_5, "该宽带账号[" + selectedWidenetAcctId + "]已经存在有效的宽带用户信息，请录入其它宽带账号！");
        }

        // 选占号码资源释放
        if (StringUtils.isNotEmpty(preOccupiedWidenetAcctId)) {
            ResCall.releaseRes("2", preOccupiedWidenetAcctId, "0", getVisit().getStaffId());
        }
        return new DataMap();
    }


    /**
     * 释放选占的号码资源
     * @param input
     * @return
     * @throws Exception
     */
    public IData releaseSelOccupiedSn(IData input) throws Exception {
        String selOccupiedWidenetAcctId = input.getString("SEL_OCCUPIED_WIDENET_ACCTID");

        if (StringUtils.isNotEmpty(selOccupiedWidenetAcctId)) {
            ResCall.releaseRes("2", selOccupiedWidenetAcctId, "0", getVisit().getStaffId());
        }
        
        return new DataMap();
    }
   
}
