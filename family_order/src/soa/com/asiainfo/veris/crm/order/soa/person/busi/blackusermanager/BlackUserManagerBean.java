/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.blackusermanager;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.database.dao.DAOManager;
import com.ailk.database.dao.impl.BaseDAO;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.database.util.SQLParser;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.CrmDAO;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBankMainSignInfoQry;

/**
 * @CREATED by gongp@2014-6-19 修改历史 Revision 2014-6-19 下午07:12:24
 */
public class BlackUserManagerBean extends CSBizBean
{
    /**
     * 黑名单管理
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IData blackUserManagement(IData input) throws Exception
    {

        IData result = new DataMap();
        // 1、获取号码的黑名单列表,
        // 接口传进来的是DatasetList
        IDataset blackUserList = new DatasetList();
        if (!"0".equals(this.getVisit().getInModeCode()))
        {
            blackUserList = (DatasetList) input.get("BLACK_USER");
        }
        else
        {
            blackUserList = new DatasetList(input.getString("BLACK_USER_DATAS", "[]"));
        }

        // 2、进行数据库更新记录
        String serial_number = input.getString("SERIAL_NUMBER");
        IData blackUser = new DataMap();
        Boolean daoOperResult = false; // dao操作结果
        StringBuilder failNumber = new StringBuilder(); // 保存没有用户而设置失败的的号码
        // 公用参数设置
        String dataTime = SysDateMgr.getSysTime();

        IData userInfo = UcaInfoQry.qryUserInfoBySn(serial_number);
        blackUser.put("USER_ID", userInfo.getString("USER_ID", ""));
        blackUser.put("SERIAL_NUMBER", serial_number);
        blackUser.put("END_DATE", SysDateMgr.getTheLastTime());
        blackUser.put("UPDATE_DATE", dataTime);
        blackUser.put("UPDATE_STAFF_ID", getVisit().getStaffId());
        blackUser.put("UPDATE_DEPART_ID", getVisit().getDepartId());

        for (int i = 0, size = blackUserList.size(); i < size; i++)
        {

            IData inputBlackUser = blackUserList.getData(i);
            // blackSerialNumber -1 全部设置或者取消； 非-1，单个设置
            String black_serial_number = inputBlackUser.getString("BLACK_SERIAL_NUMBER");// 取黑名单号码
            blackUser.put("BLACK_SERIAL_NUMBER", black_serial_number);

            // 取操作标记 0表示新增 1表示删除 2表示修改；
            String tag = inputBlackUser.getString("tag");

            if ("0".equals(tag))
            {
                // 全部黑名单设置
                if ("-1".equals(black_serial_number))
                {
                    blackUser.put("BLACK_TYPE", "6");
                    blackUser.put("BLACK_USER_ID", "-1");
                }
                else
                {// 单个黑名单设置
                    blackUser.put("BLACK_TYPE", "0");
                    // 查找黑名单用户资料
                    IData userList = UcaInfoQry.qryUserInfoBySn(black_serial_number);
                    if (!userList.isEmpty())
                    {
                        String blackUserId = userList.getString("USER_ID");// 通过blackSerialNumber查找该用户ID
                        blackUser.put("BLACK_USER_ID", blackUserId);
                    }
                    // 错误的添加到 操作失败的failBlackSerialNumber 中返回
                    else
                    {
                        failNumber.append(black_serial_number).append(",");
                        continue;
                    }
                }
                blackUser.put("LOG_ID", SeqMgr.getLogId());
                blackUser.put("BLACK_VALUE_CODE", "bank");
                blackUser.put("BLACK_STATUS", "0");
                blackUser.put("START_DATE", dataTime);
                blackUser.put("REMARK", "黑名单管理设置");
                blackUser.put("RSRV_STR1", inputBlackUser.getString("RSRV_STR1", ""));
                blackUser.put("RSRV_STR2", inputBlackUser.getString("RSRV_STR2", ""));
                blackUser.put("RSRV_STR3", inputBlackUser.getString("RSRV_STR3", ""));
                blackUser.put("RSRV_STR4", inputBlackUser.getString("RSRV_STR4", ""));
                blackUser.put("RSRV_STR5", inputBlackUser.getString("RSRV_STR5", ""));
                daoOperResult = Dao.insert("TF_F_BLACKUSER_MANAGER", blackUser);
                if (!daoOperResult)
                {
                    result.put("RESULT", "-1");
                    return result;
                }
            }
            else if ("1".equals(tag))
            {
                String black_status = "0";
                IData blackUserInDB = UserBankMainSignInfoQry.getBlackUser(serial_number, black_serial_number, black_status);
                if (!blackUserInDB.isEmpty())
                {
                    blackUser.put("LOG_ID", blackUserInDB.get("LOG_ID"));
                    blackUser.put("BLACK_STATUS", "1");
                    blackUser.put("END_DATE", dataTime);
                    blackUser.put("REMARK", "黑名单管理取消");
                    daoOperResult = Dao.save("TF_F_BLACKUSER_MANAGER", blackUser);
                    if (!daoOperResult)
                    {
                        result.put("RESULT", "-1");
                        return result;
                    }
                }
            }
        }
        result.put("RESULT", "0");
        if (failNumber != null && !"".equals(failNumber))
        {
            result.put("FAILD_NUMBER", failNumber.toString());
        }
        return result;
    }

    /**
     * 黑名单管理（接口名: ITF_CRM_BLACK_MANAGER）
     * 入参格式：{"SERIAL_NUMBER":"主号码","BLACK_SERIAL_NUMBER":["号码1","号码2","号码3"],"X_TAG":["1","0","1"]}
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-20
     */
    public IData blackUserManagementIntf(IData input) throws Exception
    {

        IData result = new DataMap();
        String serial_number = this.getCheckInputData(input, "SERIAL_NUMBER");
        String black_serial_number = this.getCheckInputData(input, "BLACK_SERIAL_NUMBER");
        String tag = this.getCheckInputData(input, "X_TAG");

        // 三户资料校验
        UcaData ucaData = UcaDataFactory.getNormalUca(serial_number);

        result = this.setIntfBlackUserList(input);
        // 最后成功返回成功信息
        result.put("X_RESULTINFO", "OK");
        result.put("X_RECORDNUM", "1");
        result.put("X_RESULTCODE", "0");
        return result;

    }

    /**
     * 校验入参
     * 
     * @param data
     * @param name
     * @return
     * @throws Exception
     */
    public String getCheckInputData(IData inputData, String dataName) throws Exception
    {
        String dataValue = inputData.getString(dataName, "");
        if (0 == dataValue.length())
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[" + dataName + "]不能为空！");
        }
        return dataValue;
    }

    /**
     * 黑名单查询
     * 
     * @param input
     * @return
     * @throws Exception
     * @CREATE BY GONGP@2014-6-19
     */
    public IDataset queryBlackUserList(IData input) throws Exception
    {
        String serial_number = input.getString("SERIAL_NUMBER");
        String black_status = "0";
        return UserBankMainSignInfoQry.qryBlackUserList(serial_number, black_status);
    }

    /**
     * 黑名单管理接口参数处理
     * 
     * @param data
     *            获取入参格式：{"SERIAL_NUMBER":"122323","BLACK_SERIAL_NUMBER":["","",""],"X_TAG":["","",""]}
     * @return
     * @throws Exception
     */
    public IData setIntfBlackUserList(IData input) throws Exception
    {

        IData result = new DataMap();
        // 如果校验成功，加入参数最后处理的参数，进行设置或者取消
        IDataset lastBlackUserList = new DatasetList();
        // 获取入参
        String serial_number = input.getString("SERIAL_NUMBER");
        IDataset blackSerialNumbers = (IDataset) input.get("BLACK_SERIAL_NUMBER");
        IDataset tags = (IDataset) input.get("X_TAG");

        // 中间参数作为比较
        String outBlackSerialNumber = null;
        String outTag = null;
        String innerBlackSerialNumber = null;
        String innerTag = null;

        for (int out = 0; out < tags.size(); out++)
        {
            outBlackSerialNumber = (String) blackSerialNumbers.get(out);
            outTag = (String) tags.get(out);
            // 校验步骤1：入参黑名单入参与入参之间的校验检查
            for (int inner = out + 1; inner < tags.size(); inner++)
            {
                innerBlackSerialNumber = (String) blackSerialNumbers.get(inner);
                innerTag = (String) tags.get(inner);
                // 1、参数黑名单号码不能与主号码相同；
                if (outBlackSerialNumber.equals(serial_number))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的黑名单设置号码" + outBlackSerialNumber + "不能是主号码！");
                }
                // 2、参数黑名单号码不能相同；
                if (outBlackSerialNumber.equals(innerBlackSerialNumber))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的黑名单设置号码" + outBlackSerialNumber + "重复！");
                }
                // 3、不能同时设置单个与全部
                if ("0".equals(outTag) && "0".equals(innerTag) && ("-1".equals(outBlackSerialNumber) || "-1".equals(innerBlackSerialNumber)))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "不能设置全部黑名单又设置单个黑名单！");
                }
            }
            // 校验步骤2：入参黑名单与数据库中校验检查
            // 1、设置校验检查
            if ("0".equals(outTag))
            {
                IDataset blackUserList = UserBankMainSignInfoQry.qryBlackUserList(serial_number, outTag);
                if (!blackUserList.isEmpty())
                {
                    for (int inner = 0; inner < blackUserList.size(); inner++)
                    {
                        innerBlackSerialNumber = (String) blackUserList.getData(inner).get("BLACK_SERIAL_NUMBER");
                        // 1、不能与数据库中设置相同的号码
                        if (outBlackSerialNumber.equals(innerBlackSerialNumber))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的黑名单设置号码" + outBlackSerialNumber + "已经设置过，无须再次设置！");
                        }
                        // 2、不能已经设置全部了，还设置其他的
                        else if ("-1".equals(innerBlackSerialNumber))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "已经设置过全部黑名单，所以不能设置其他黑名单！");
                        }
                        // 3、不能已经设置单个了，再设置全部
                        else if (!"-1".equals(innerBlackSerialNumber) && ("-1".equals(outBlackSerialNumber)))
                        {
                            CSAppException.apperr(CrmCommException.CRM_COMM_103, "已经设置过单个黑名单，所以不能设置全部黑名单！");
                        }
                    }
                }
            }
            // 2、取消校验检查
            else
            {
                // 没有可以取消的,则无须取消
                IData blackUser = UserBankMainSignInfoQry.getBlackUser(serial_number, outBlackSerialNumber, "0");
                if (blackUser.isEmpty())
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "传入的黑名单设置号码" + outBlackSerialNumber + "不是黑名单，无须取消！");
                }
            }
            // 如果2步都成功，加入参数最后处理的参数
            IData lastBlackUser = new DataMap();
            lastBlackUser.put("BLACK_SERIAL_NUMBER", outBlackSerialNumber);
            lastBlackUser.put("tag", outTag);
            lastBlackUserList.add(lastBlackUser);
        }
        // 设置值传入数据库设置黑名单
        input.put("BLACK_USER", lastBlackUserList);
        input.put("IN_MODE_CODE", input.getString("IN_MODE_CODE", "1"));
        result = this.blackUserManagement(input);
        return result;

    }
    
    
    /**
     * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 插黑名单日志表
     * */
    public static void insertBlackUserCheckLogInfo(IData param) throws Exception{
    	DBConnection conn = null;
    	try 
		{
    		conn = SessionManager.getInstance().getAsyncConnection("crm1");
			String acceptMon=SysDateMgr.getSysDateYYYYMMDD().substring(4,6);
    		IData insData=new DataMap();
	    	insData.put("ACCEPT_MONTH",     acceptMon); 
        	insData.put("USER_ID",          param.getString("USER_ID","")); 
        	insData.put("SERIAL_NUMBER",    param.getString("SERIAL_NUMBER",""));   
        	insData.put("PSPT_ID",          param.getString("PSPT_ID",""));         
        	insData.put("IN_MODE_CODE",     param.getString("IN_MODE_CODE",""));     
        	insData.put("UPDATE_STAFF_ID",  param.getString("UPDATE_STAFF_ID",""));  
        	insData.put("UPDATE_DEPART_ID",  param.getString("UPDATE_DEPART_ID",""));  
	    	insData.put("UPDATE_TIME",      SysDateMgr.getSysTime());      
        	insData.put("TRADE_TYPE_CODE",  param.getString("TRADE_TYPE_CODE",""));  
        	insData.put("FEE",              param.getString("FEE","")); 
        	String otherFee = param.getString("OTHER_FEE","");
        	if(otherFee!=null && otherFee.length()>1999){ //防止字段太长
        		otherFee = otherFee.substring(0, 1998);
        	}
        	insData.put("OTHER_FEE",        otherFee);        
        	insData.put("RSRV_STR1",        param.getString("RSRV_STR1",""));        
        	insData.put("RSRV_STR2",        param.getString("RSRV_STR2",""));        
        	insData.put("RSRV_STR3",        param.getString("RSRV_STR3",""));        
        	insData.put("RSRV_STR4",        param.getString("RSRV_STR4",""));       
        	insData.put("RSRV_STR5",        param.getString("RSRV_STR5",""));   
        	BaseDAO dao = DAOManager.createDAO(BaseDAO.class);
			dao.insert(conn, "TL_B_BLACK_LOG", insData);
	    	conn.commit();
		}
    	catch (Exception e1) 
		{ 
			if(conn != null)
			{
				conn.rollback();
			}			
			CSAppException.appError("2001", e1.getMessage());
		} 
		finally 
		{
			if(conn != null)
			{
				conn.close();
			}
		}
    	
    }
    

    
    /**
	 * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 黑名单用户日志查询
	 * */
	public static IDataset qryBlackUserLog(IData inParam,Pagination pagen) throws Exception
    {  
        SQLParser parser = new SQLParser(inParam); 
        
        parser.addSQL(" select t.* from  TL_B_BLACK_LOG t ");
        parser.addSQL(" where 1=1 ");  
        parser.addSQL(" and t.SERIAL_NUMBER =:SERIAL_NUMBER ");  
        parser.addSQL(" and t.PSPT_ID =:PSPT_ID ");  
        parser.addSQL(" and t.TRADE_TYPE_CODE =:TRADE_TYPE_CODE	"); 
        parser.addSQL(" and t.UPDATE_TIME >=to_date(:STARTDATE,'yyyy-mm-dd') ");  
        parser.addSQL(" and t.UPDATE_TIME <to_date(:ENDDATE,'yyyy-mm-dd')+1 ");  
    	return Dao.qryByParse(parser,pagen); 
    }
	
	/**
     * REQ201606300007 关于增加系统黑名单后台查询日志的需求
     * chenxy 20160706 
     * 插黑名单日志表
     * 作废
     * */
    public static void insertBlackUserCheckLogInfoSelf(IData param) throws Exception{
    	DBConnection conn = null;
		try 
		{
			conn = SessionManager.getInstance().getAsyncConnection("crm1");
	    	IData insData=new DataMap();
	    	insData.put("ACCEPT_MONTH",     Integer.toString(Integer.parseInt(SysDateMgr.getSysDateYYYYMMDD().substring(4,6)))); 
	    	insData.put("USER_ID",          param.getString("USER_ID","")); 
	    	insData.put("SERIAL_NUMBER",    param.getString("SERIAL_NUMBER",""));   
	    	insData.put("PSPT_ID",          param.getString("PSPT_ID",""));         
	    	insData.put("IN_MODE_CODE",     param.getString("IN_MODE_CODE",""));     
	    	insData.put("UPDATE_STAFF_ID",  param.getString("UPDATE_STAFF_ID",""));  
	    	insData.put("UPDATE_DEPART_ID",  param.getString("UPDATE_DEPART_ID",""));  
	    	insData.put("UPDATE_TIME",      SysDateMgr.getSysDateYYYYMMDDHHMMSS());      
	    	insData.put("TRADE_TYPE_CODE",  param.getString("TRADE_TYPE_CODE",""));  
	    	insData.put("FEE",              param.getString("FEE",""));              
	    	String otherFee = param.getString("OTHER_FEE","");
	    	if(otherFee!=null && otherFee.length()>999){ //防止字段太长
	    		otherFee = otherFee.substring(0, 998);
	    	}
	    	insData.put("OTHER_FEE",        otherFee);        
	    	insData.put("RSRV_STR1",        param.getString("RSRV_STR1",""));        
	    	insData.put("RSRV_STR2",        param.getString("RSRV_STR2",""));        
	    	insData.put("RSRV_STR3",        param.getString("RSRV_STR3",""));        
	    	insData.put("RSRV_STR4",        param.getString("RSRV_STR4",""));       
	    	insData.put("RSRV_STR5",        param.getString("RSRV_STR5",""));    
	    	CrmDAO dao = DAOManager.createDAO(CrmDAO.class);
			dao.insert(conn,"TL_B_BLACK_LOG", insData);
			conn.commit();
		}	
		catch (Exception e1) 
		{ 
			if(conn != null)
			{
				conn.rollback();
			}			
			CSAppException.appError("2001", e1.getMessage());
		} 
		finally 
		{
			if(conn != null)
			{
				conn.close();
			}
		}
    } 
}
