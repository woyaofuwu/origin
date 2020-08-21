
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

public class IotQuery
{

    public static IDataset queryAllSilenceTranStopIotBook() throws Exception
    {
        IData param = new DataMap();
        param.put("OLD_STATE_CODE", "1");
        param.put("NEW_STATE_CODE", "3");
        param.put("DEAL_TAG", "0");
        param.put("SYS_DATE", SysDateMgr.getSysTime());

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        return list;
    }

    public static IDataset queryAllStopTranDestroyIotBook() throws Exception
    {
        IData param = new DataMap();
        param.put("OLD_STATE_CODE", "3");
        param.put("NEW_STATE_CODE", "4");
        param.put("DEAL_TAG", "0");
        param.put("SYS_DATE", SysDateMgr.getSysTime());

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        return list;
    }

    /**
     * 查询所有的测试期转沉默记录
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryAllTestTranSilenceIotBook() throws Exception
    {
        IData param = new DataMap();
        param.put("OLD_STATE_CODE", "0");
        param.put("NEW_STATE_CODE", "1");
        param.put("DEAL_TAG", "0");
        param.put("SYS_DATE", SysDateMgr.getSysTime());

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        return list;
    }
    
    /**
     * 查询所有的沉默转正常记录
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryAllSilenceTransNormalIotBook() throws Exception
    {
        IData param = new DataMap();
        param.put("OLD_STATE_CODE", "1");
        param.put("NEW_STATE_CODE", "2");
        param.put("DEAL_TAG", "0");
        param.put("SYS_DATE", SysDateMgr.getSysTime());

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        return list;
    }
    
    /**
     * 查询所有的测试期转正常期记录 NB用户
     * 
     * @return
     * @throws Exception
     */
    public static IDataset queryAllTestTransNormalIotBook() throws Exception
    {
        IData param = new DataMap();
        param.put("OLD_STATE_CODE", "0");
        param.put("NEW_STATE_CODE", "2");
        param.put("DEAL_TAG", "0");
        param.put("SYS_DATE", SysDateMgr.getSysTime());

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        return list;
    }

    public static IDataset queryIntenetBookByOldDealUserId(String OLD_STATE_CODE, String DEAL_TAG, String USER_ID) throws Exception
    {
        IData cond = new DataMap();
        cond.put("OLD_STATE_CODE", OLD_STATE_CODE);
        cond.put("DEAL_TAG", DEAL_TAG);
        cond.put("USER_ID", USER_ID);
        IDataset dataset = Dao.qryByCode("TF_F_INTERNETOFTHINGS_BOOK", "SEL_INTERNET_BY_USERID_DEALTAG_OLDSTATECODE", cond, Route.CONN_CRM_CEN);
        return dataset;
    }

    /**
     * 查询物联网 用户的转换记录表记录
     * 
     * @param userId
     * @param oldState
     * @param newState
     * @param dealTag
     * @return
     * @throws Exception
     */
    public static IData queryUserIotBook(String userId, String oldState, String newState, String dealTag, String sysDate) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("OLD_STATE_CODE", oldState);
        param.put("NEW_STATE_CODE", newState);
        param.put("DEAL_TAG", dealTag);
        param.put("SYS_DATE", sysDate);

        IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_COND", param, Route.CONN_CRM_CEN);
        if (list != null && !list.isEmpty())
        {
            return list.getData(0);
        }

        return null;
    }

    /**
     * 根据流水号查询订单信息  by huping pboss二期
     */
    public static IData queryOrderByOperseq(String oper_seq)throws Exception{
    	IData param = new DataMap();
        param.put("OPER_SEQ", oper_seq);
        String accept_month = oper_seq.substring(16, 18);
        param.put("ACCEPT_MONTH", accept_month);
        IDataset list = Dao.qryByCodeParser("TF_B_TRADE", "SEL_ORDER_BY_OPERSEQ", param);
        if (list != null && !list.isEmpty())
        {
            return list.getData(0);
        }
        return null;
    }




    /**
     * 根据流水号查询订单信息  by huping pboss二期
     */
    public static IData queryTradeIdByOperseq(String oper_seq)throws Exception{
        IData param = new DataMap();
        param.put("OPER_SEQ", oper_seq);
        IDataset list = Dao.qryByCodeParser("TF_B_TRADE_WLWREVERSE", "SEL_TRADEID_BY_OPERSEQ", param, Route.getJourDb());
        if (list != null && !list.isEmpty())
        {
            return list.getData(0);
        }
        return null;
    }
    
    /**
     * 根据TRADE_D查询订单状态  by huping pboss二期
     */
    public static IData queryStateByTradeID(String trade_id)throws Exception{
        IData param = new DataMap();
        param.put("TRADE_ID", trade_id);
        IDataset list = Dao.qryByCodeParser("TF_B_TRADE", "SEL_STATE_BY_TRADEID", param, Route.getJourDb());
        if (list != null && !list.isEmpty())
        {
            return list.getData(0);
        }
        return null;
    }

    
    /**
     * 查询记录  pboss二期
     * @param serialNumber
     * @return
     * @throws Exception
     */
    public static IData queryAllBySN(String serialNumber,String oldStateCode,String newStateCode,String dealTag)throws Exception{
    	IData param=new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("OLD_STATE_CODE", oldStateCode);
    	param.put("NEW_STATE_CODE", newStateCode);
    	param.put("DEAL_TAG", dealTag);
    	IDataset list = Dao.qryByCodeParser("TF_F_INTERNETOFTHINGS_BOOK", "SEL_IOTBOOK_BY_SN_ORDER", param,Route.CONN_CRM_CEN);
        if (list != null && !list.isEmpty())
        {
            return list.getData(0);
        }
    	return null;
    }
    
    public static void recordReverseOprSeq(String tradeID,String oprSeq)throws Exception{
        IData inparam = new DataMap();
        inparam.put("TRADE_ID", tradeID);
        inparam.put("OPER_SEQ", oprSeq);
    	Dao.insert("TF_B_TRADE_WLWREVERSE", inparam, Route.getJourDb());
    }

  //根据PARTITION_ID分区查询物联网用户信息
    public static IDataset queryAllUserInfo(int partition_start, int partition_end) throws Exception{
        IData param = new DataMap();
        param.put("PARTITION_START", partition_start);
        param.put("PARTITION_END", partition_end);
        param.put("BRAND_CODE", "PWLW");

        IDataset list = Dao.qryByCodeParser("TF_F_USER_PRODUCT", "SEL_IOTUSER_BY_BRAND_CODE", param);
        return list;
    }

    //根据USER_ID查询物联网用户
    public static IDataset queryByIotUserID(String userId) throws Exception{
        IData param = new DataMap();
        param.put("USER_ID", userId);       
        StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT U.USER_ID,U.SERIAL_NUMBER ");
		sql.append("FROM TF_F_IOT_USER U WHERE U.USER_ID = :USER_ID ");

		return Dao.qryBySql(sql, param);
    }
    //查询用户物联网策略属性
	public static IDataset queryUserAttrInfo(IData userInfo) throws Exception {
		IData inParam = new DataMap();
        inParam.put("USER_ID", userInfo.getString("USER_ID"));
        inParam.put("ATTR_CODE", "APNNAME");       
        StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT ATTR_VALUE,USER_ID FROM TF_F_USER_ATTR ");
		sql.append("WHERE USER_ID=TO_NUMBER(:USER_ID) AND PARTITION_ID=MOD(TO_NUMBER(:USER_ID),10000) AND ATTR_CODE = :ATTR_CODE ");
		sql.append("AND TO_CHAR(END_DATE,'YYYY-MM-DD HH24:MM:SS') <= TO_CHAR(SYSDATE,'YYYY-MM-DD HH24:MI:SS') ");

		return Dao.qryBySql(sql, inParam);
	}
    //查询用户物联网CustManager
	public static IDataset qryCustManagerStaffById(String mgrId) throws Exception {		       
		IData inParam = new DataMap();
        inParam.put("CUST_MANAGER_ID", mgrId);
		StringBuilder sql = new StringBuilder(1000);

		sql.append("SELECT CUST_MANAGER_NAME FROM TF_F_CUST_MANAGER_STAFF WHERE CUST_MANAGER_ID = :CUST_MANAGER_ID ");

		return Dao.qryBySql(sql, inParam);
	}
	
}
