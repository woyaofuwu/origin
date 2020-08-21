package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.notstopmigrantuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;

public class NotStopMigrantUserBean extends CSBizBean
{

	private static final long serialVersionUID = 1L;

	/**
     * 查询是否登记
     */
    public IData queryInfo(IData params) throws Exception
    {
    	String serialNumber = params.getString("SERIAL_NUMBER","0");
    	//1、检查号码是否存在，即是否有用户资料
    	IData userList = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userList) || userList == null || userList.size() < 1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户资料不存在");
		}
		
		//2、检查宽带号码是否存在
		IData kdUserList = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
		if (IDataUtil.isEmpty(kdUserList) || kdUserList == null || kdUserList.size() < 1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带用户资料不存在");
		}
		
		String kdUserId = kdUserList.getString("USER_ID","0");//获取宽带号码的USER_ID

		//3、判断是否是候鸟套餐用户
		IDataset isMigrant = null;
		IData querySqlParams = new DataMap();
		querySqlParams.put("KD_USER_ID", kdUserId);
        StringBuilder querySql = new StringBuilder(" SELECT T.*  ");
		querySql.append(" FROM TF_F_USER_DISCNT T ");
		querySql.append(" WHERE 1 = 1 ");
		querySql.append(" AND T.DISCNT_CODE IN (36120018, 36120019) ");
		querySql.append(" AND T.END_DATE > SYSDATE ");
		querySql.append(" AND T.USER_ID = :KD_USER_ID ");
        isMigrant = Dao.qryBySql(querySql, querySqlParams);
        if(IDataUtil.isEmpty(isMigrant))
        {
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "非候鸟套餐用户");
        }
		
    	//4、查看号码是否已在登记表中
		IDataset result = null;
		IData sqlParams = new DataMap();
		sqlParams.put("SERIAL_NUMBER", serialNumber);
		sqlParams.put("KD_USER_ID", kdUserId);
        StringBuilder sql = new StringBuilder(" SELECT T.SERIAL_NUMBER, ");
        sql.append(" T.USER_ID ");
        sql.append(" FROM TF_F_USER_MIGRANT_NOTSTOP T ");
        sql.append(" WHERE 1 = 1 ");
        sql.append(" AND T.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.append(" AND T.KD_USER_ID = :KD_USER_ID ");
        result = Dao.qryBySql(sql, sqlParams);
    	
    	IData returnInfo = new DataMap();
    	if(IDataUtil.isEmpty(result))
    	{
    		returnInfo.put("MIGRANT_STATE_VALUE", "未登记");
    		returnInfo.put("REG_BUTTON_STATE", "0");
    	}
    	else
    	{
    		returnInfo.put("MIGRANT_STATE_VALUE", "已登记候鸟短期套餐到期不停机");
    		returnInfo.put("REG_BUTTON_STATE", "1");
    	}
    	
    	return returnInfo;
    }
    
    /**
     * 提交登记
     */
    public IData onTradeSubmit(IData params) throws Exception
    {
    	String serialNumber = params.getString("SERIAL_NUMBER","0");
    	//1、检查号码是否存在，即是否有用户资料
    	IData userList = UcaInfoQry.qryUserInfoBySn(serialNumber);
		if (IDataUtil.isEmpty(userList) || userList == null || userList.size() < 1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户资料不存在");
		}
		
		//2、检查宽带号码是否存在
		IData kdUserList = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
		if (IDataUtil.isEmpty(kdUserList) || kdUserList == null || kdUserList.size() < 1) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "宽带用户资料不存在");
		}
		
		String userId = userList.getString("USER_ID");
		String kdUserId = kdUserList.getString("USER_ID");//获取宽带号码的USER_ID
		
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);
		data.put("USER_ID", userId);
		data.put("KD_USER_ID", kdUserId);
		data.put("UPDATE_TIME", SysDateMgr.getSysTime());
		data.put("UPDATE_STAFF_ID", getVisit().getStaffId());
		
		boolean countInsert = Dao.insert("TF_F_USER_MIGRANT_NOTSTOP", data);

    	IData returnInfo = new DataMap();
    	returnInfo.put("DATA_INSERT_RESULT", countInsert);

    	return returnInfo;
    }


}
