
package com.asiainfo.veris.crm.order.soa.person.busi.internetofthings;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.InstancePfQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.IotQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.ailk.biz.util.StaticUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.ailk.common.data.impl.Pagination;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class IotTranSVC extends CSBizService
{

    /**
	 * 
	 */
    private static final long serialVersionUID = -8067967073827895929L;
    private static final Logger logger = Logger.getLogger(IotTranSVC.class);

    /**
     * 沉默期转停机
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData silenceTranStop(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "ok");
        result.put("X_BOOKLIST", "0");
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "ok");

        IDataset bookList = IotQuery.queryAllSilenceTranStopIotBook();
        for (int i = 0; i < bookList.size(); i++)
        {
            try
            {
                IData book = bookList.getData(i);
                String serialNumber = book.getString("SERIAL_NUMBER");
                String userId = book.getString("USER_ID");
                if (StringUtils.isBlank(serialNumber))
                {
                	result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "不存在符合查询条件的手机号码!");
                	return result;
                    //CSAppException.apperr(CrmCommException.CRM_COMM_172);
                }

                book.put("EXEC_TIME", SysDateMgr.getSysTime());
                book.put("DEAL_TAG", "1");

                // 调用停机接口参数
                IData param = new DataMap();
                param.put("SERIAL_NUMBER", serialNumber);

                try
                {
                    IDataset stopResultList = CSAppCall.call("SS.StopMobileRegSVC.tradeReg", param);
                    if (IDataUtil.isNotEmpty(stopResultList))
                    {
                        IData stopResult = stopResultList.first();
                        String strOrderID = stopResult.getString("ORDER_ID");
                        String strTradeID = stopResult.getString("TRADE_ID");
                        if (StringUtils.isNotEmpty(strOrderID))
                        {

                            book.put("RESULT_CODE", "0");
                            book.put("RESULT_INFO", "trade ok");
                            book.put("TRADE_ID", strTradeID);
                            book.put("RSRV_STR1", strOrderID);
                            // 新增一条N个帐期后由停机转换成销户状态的记录
                            IData newTaskParam = new DataMap();
                            newTaskParam.put("SERIAL_NUMBER", serialNumber);
                            newTaskParam.put("USER_ID", userId);
                            newTaskParam.put("INST_ID", SeqMgr.getInstId(Route.CONN_CRM_CG));
                            newTaskParam.put("OLD_STATE_CODE", "3");
                            newTaskParam.put("NEW_STATE_CODE", "4");
                            newTaskParam.put("TRAN_DATE", SysDateMgr.endDateOffset(SysDateMgr.getSysTime(), "3", "3"));// 根据当地规则结合分散帐期修改
                            newTaskParam.put("DEAL_TAG", "0");
                            newTaskParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                            newTaskParam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                            newTaskParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            newTaskParam.put("TRADE_ID", strTradeID);
                            newTaskParam.put("RSRV_STR1", strOrderID);
                            Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", newTaskParam, Route.CONN_CRM_CEN);
                        }
                        else
                        {
                            book.put("RESULT_CODE", "-1");
                            book.put("RESULT_INFO", stopResult.getString("X_RESULTINFO"));
                        }

                    }
                }
                catch (Exception e)
                {
                    logger.error(e);//e.printStackTrace();
                    String msg = e.getMessage();
                    msg = (msg == null) ? "调用停机接口失败" : msg;
                    String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                    book.put("RESULT_CODE", "-1");
                    book.put("RESULT_INFO", rspDesc);
                }

                Dao.update("TF_F_INTERNETOFTHINGS_BOOK", book, new String[]{ "INST_ID" }, Route.CONN_CRM_CEN);
                result.putAll(book);
            }
            catch (Exception e)
            {
            	logger.info(e);
                String msg = e.getMessage();
                msg = (msg == null) ? "调用测试期转沉默接口失败." : msg;
                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", rspDesc);
            }

        }

        return result;
    }

    /**
     * 停机转销户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData stopTranDestory(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "ok");
        result.put("X_BOOKLIST", "0");
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "ok");
        IDataset bookList = IotQuery.queryAllStopTranDestroyIotBook();
        for (int i = 0; i < bookList.size(); i++)
        {
            try
            {
                IData book = bookList.getData(i);
                String serialNumber = book.getString("SERIAL_NUMBER");
                if (StringUtils.isBlank(serialNumber))
                {
                	result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "不存在符合查询条件的手机号码!");
                	return result;
                    //CSAppException.apperr(CrmCommException.CRM_COMM_172);
                }

                book.put("EXEC_TIME", SysDateMgr.getSysTime());
                book.put("DEAL_TAG", "1");

                // 调用销户接口参数
                IData param = new DataMap();
                param.put("SERIAL_NUMBER", serialNumber);
                param.put(Route.ROUTE_EPARCHY_CODE, "0898");
                param.put("TRADE_TYPE_CODE", "192");
                param.put("ORDER_TYPE_CODE", "192");
                try
                {
                    IDataset stopResultList = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", param);
                    if (IDataUtil.isNotEmpty(stopResultList))
                    {
                        IData stopResult = stopResultList.getData(0);
                        String strOrderID = stopResult.getString("ORDER_ID");
                        String strTradeID = stopResult.getString("TRADE_ID");
                        if (StringUtils.isNotEmpty(strOrderID))
                        {

                            book.put("RESULT_CODE", "0");
                            book.put("RESULT_INFO", "trade ok");
                            book.put("TRADE_ID", strTradeID);
                            book.put("RSRV_STR1", strOrderID);
                        }
                        else
                        {
                            book.put("RESULT_CODE", "-1");
                            book.put("RESULT_INFO", stopResult.getString("X_RESULTINFO"));
                        }

                    }
                }
                catch (Exception e)
                {
                    logger.error(e);//e.printStackTrace();
                    String msg = e.getMessage();
                    msg = (msg == null) ? "调用销户接口失败" : msg;
                    String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                    book.put("RESULT_CODE", "-1");
                    book.put("RESULT_INFO", rspDesc);
                }

                Dao.update("TF_F_INTERNETOFTHINGS_BOOK", book, new String[] { "INST_ID" }, Route.CONN_CRM_CEN);
                result.putAll(book);
            }
            catch (Exception e)
            {
            	logger.info(e);
                String msg = e.getMessage();
                msg = (msg == null) ? "调用销户接口失败." : msg;
                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;

                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", rspDesc);
            }

        }

        return result;
    }
    
    /**
     * 沉默转正常
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData SilenceTransNormal(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "ok");
        result.put("X_BOOKLIST", "0");
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "ok");
        IDataset bookList = IotQuery.queryAllSilenceTransNormalIotBook();

        for (int i = 0; i < bookList.size(); i++)
        {
            try
            {
                IData book = bookList.getData(i);
                String serialNumber = book.getString("SERIAL_NUMBER");
                //String userId = book.getString("USER_ID");
                if (StringUtils.isBlank(serialNumber))
                {
                	result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "不存在符合查询条件的手机号码!");
                	return result;
                    //CSAppException.apperr(CrmCommException.CRM_COMM_172);
                }

                book.put("EXEC_TIME", SysDateMgr.getSysTime());
                book.put("DEAL_TAG", "1");

                IData param = new DataMap();
                param.putAll(input);
                param.put("SERIAL_NUMBER", serialNumber);

                try
                {
                    IDataset stopResultList = CSAppCall.call("SS.SilenceTransNormalSVC.tradeReg", param);
                    if (IDataUtil.isNotEmpty(stopResultList))
                    {
                        IData stopResult = stopResultList.first();
                        String strOrderID = stopResult.getString("ORDER_ID");
                        String strTradeID = stopResult.getString("TRADE_ID");
                        if (StringUtils.isNotEmpty(strOrderID))
                        {

                            book.put("RESULT_CODE", "0");
                            book.put("RESULT_INFO", "trade ok");
                            book.put("TRADE_ID", strTradeID);
                            book.put("RSRV_STR1", strOrderID);
                        }
                        else
                        {
                            book.put("RESULT_CODE", "-1");
                            book.put("RESULT_INFO", stopResult.getString("X_RESULTINFO"));
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.info(e);
                    String msg = e.getMessage();
                    msg = (msg == null) ? "调用测试期转沉默接口失败" : msg;
                    String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                    book.put("RESULT_CODE", "-1");
                    book.put("RESULT_INFO", rspDesc);
                }

                Dao.update("TF_F_INTERNETOFTHINGS_BOOK", book, new String[] { "INST_ID" }, Route.CONN_CRM_CEN);
                result.putAll(book);
            }
            catch (Exception e)
            {
                logger.info(e);
                String msg = e.getMessage();
                msg = (msg == null) ? "调用测试期转沉默接口失败." : msg;
                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", rspDesc);
            }
        }
        return result;
    }

    /**
     * 测试期转沉默
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData testTranSilence(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "ok");
        result.put("X_BOOKLIST", "0");
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "ok");
        IDataset bookList = IotQuery.queryAllTestTranSilenceIotBook();

        for (int i = 0; i < bookList.size(); i++)
        {
            try
            {
                IData book = bookList.getData(i);
                String serialNumber = book.getString("SERIAL_NUMBER");
                String userId = book.getString("USER_ID");
                if (StringUtils.isBlank(serialNumber))
                {
                	result.put("RESULT_CODE", "-1");
                    result.put("RESULT_INFO", "不存在符合查询条件的手机号码!");
                	return result;
                    //CSAppException.apperr(CrmCommException.CRM_COMM_172);
                }

                book.put("EXEC_TIME", SysDateMgr.getSysTime());
                book.put("DEAL_TAG", "1");

                IData param = new DataMap();
                param.put("SERIAL_NUMBER", serialNumber);

                try
                {
                    IDataset stopResultList = CSAppCall.call("SS.TestTransSilenceSVC.tradeReg", param);
                    if (IDataUtil.isNotEmpty(stopResultList))
                    {
                        IData stopResult = stopResultList.first();
                        String strOrderID = stopResult.getString("ORDER_ID");
                        String strTradeID = stopResult.getString("TRADE_ID");
                        if (StringUtils.isNotEmpty(strOrderID))
                        {

                            book.put("RESULT_CODE", "0");
                            book.put("RESULT_INFO", "trade ok");
                            book.put("TRADE_ID", strTradeID);
                            book.put("RSRV_STR1", strOrderID);
                            // 新增一条6个帐期后由沉默期转正常的记录
                            IData newTaskParam = new DataMap();
                            newTaskParam.put("SERIAL_NUMBER", serialNumber);
                            newTaskParam.put("USER_ID", userId);
                            newTaskParam.put("INST_ID", SeqMgr.getInstId(Route.CONN_CRM_CG));
                            newTaskParam.put("OLD_STATE_CODE", "1");
                            newTaskParam.put("NEW_STATE_CODE", "2");
                            newTaskParam.put("TRAN_DATE", SysDateMgr.endDateOffset(SysDateMgr.getSysTime(), "6", "3"));// 根据当地规则结合分散帐期修改
                            newTaskParam.put("DEAL_TAG", "0");
                            newTaskParam.put("UPDATE_STAFF_ID", getVisit().getStaffId());
                            newTaskParam.put("UPDATE_DEPART_ID", getVisit().getDepartId());
                            newTaskParam.put("UPDATE_TIME", SysDateMgr.getSysTime());
                            newTaskParam.put("TRADE_ID", strTradeID);
                            newTaskParam.put("RSRV_STR1", strOrderID);
                            Dao.insert("TF_F_INTERNETOFTHINGS_BOOK", newTaskParam, Route.CONN_CRM_CEN);
                        }
                        else
                        {
                            book.put("RESULT_CODE", "-1");
                            book.put("RESULT_INFO", stopResult.getString("X_RESULTINFO"));
                        }

                    }
                }
                catch (Exception e)
                {
                    logger.info(e);
                    String msg = e.getMessage();
                    msg = (msg == null) ? "调用测试期转沉默接口失败" : msg;
                    String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                    book.put("RESULT_CODE", "-1");
                    book.put("RESULT_INFO", rspDesc);
                }

                Dao.update("TF_F_INTERNETOFTHINGS_BOOK", book, new String[] { "INST_ID" }, Route.CONN_CRM_CEN);
                result.putAll(book);
            }
            catch (Exception e)
            {
                logger.info(e);
                String msg = e.getMessage();
                msg = (msg == null) ? "调用测试期转沉默接口失败." : msg;
                String rspDesc = (msg.length() > 120) ? msg.substring(0, 120) : msg;
                result.put("RESULT_CODE", "-1");
                result.put("RESULT_INFO", rspDesc);
            }

        }

        return result;
    }

	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop(IData input) throws Exception
    {
		logger.info("chenzg3=="+input);
        IData result = new DataMap();
        result.put("X_RESULTCODE", "00");
        result.put("X_RESULT_INFO", "ok");
        
        //查询当月到期用户
        for(int i = 0; i < 10; i++){
        	IDataset userList = InstancePfQuery.queryAllUserToStop(i*1000,(i+1)*1000);
        	for (int j = 0; j < userList.size(); j++)
            {
        		try{
	                IData user = userList.getData(j);
	                String serialNumber = user.getString("SERIAL_NUMBER");
	                if (serialNumber == null || "".equals(serialNumber)){
	                    CSAppException.apperr(CrmCommException.CRM_COMM_172);
	                }
	                // 调用停机接口参数
	                IData param = new DataMap();
	                param.put("SERIAL_NUMBER", serialNumber);
	                
	                //调用停机服务
	                IDataset stopResultList = CSAppCall.call("SS.StopMobileRegSVC.tradeReg", param);
	                logger.info("stopResultList="+stopResultList);
        		}catch(Exception e){
        			logger.error("[NBIOT_autostop error]", e);
        			result.put("X_RESULTCODE", "-1");
        	        result.put("X_RESULT_INFO", e.getMessage());
        		}
            }
        }
        return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData IAutoStop(IDataset userList) throws Exception
    {
		IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULT_INFO", "ok");
        if(IDataUtil.isNotEmpty(userList))
        {
        	for (int j = 0; j < userList.size(); j++)
            {
        		try
        		{
                    IData user = userList.getData(j);
                    String serialNumber = user.getString("SERIAL_NUMBER");
                    if (serialNumber == null || "".equals(serialNumber))
                    {
                        CSAppException.apperr(CrmCommException.CRM_COMM_172);
                    }
                    // 调用停机接口参数
                    IData param = new DataMap();
                    param.put("SERIAL_NUMBER", serialNumber);
                    
                    //调用停机服务
                    IDataset stopResultList = CSAppCall.call("SS.StopMobileRegSVC.tradeReg", param);
                    logger.info("stopResultList = " + stopResultList);
        		}
        		catch(Exception e)
        		{
        			logger.error("[NBIOT_autostop error]", e);
        			result.put("X_RESULTCODE", "-1");
        	        result.put("X_RESULT_INFO", e.getMessage());
        		}
            }
        }
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop0(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(0);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop1(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(1);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop2(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(2);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop3(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(3);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop4(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(4);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop5(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(5);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop6(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(6);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop7(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(7);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop8(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(8);
        IData result = IAutoStop(userList);
		return result;
    }
	
	/**
	 * 成员正式套餐已经到期且不进行续签，此时系统自动触发停机操作
     * @param input
     * @return
     * @throws Exception
	 */
	public IData autoStop9(IData input) throws Exception
    {
		logger.info("chenzg3 == " + input);
		
        IDataset userList = InstancePfQuery.queryUserProductDiscnt(9);
        IData result = IAutoStop(userList);
		return result;
    }
	
	 /**
     * 物联网PBOSS二期（暨车联网前装产品）业务支撑系统实施方案
     * 8.55 反向订购关系查询接口  20180122
     * @param input
     * @return
     * @throws Exception
     */
    public static IData queryPbossOrder(IData data) throws Exception
    {
    	logger.debug("<<<<<<<<<<<<<<<queryPbossOrder>>>>>>>>>>>调用过来");
    	
    	Map<String, IData> DISCNT_CONFIG_MAP = loadConfig("9013");

    	Map<String, IData> SVC_CONFIG_MAP = loadConfig("9014");

    	String oprSeq = IDataUtil.chkParam(data,"OPR_SEQ");
    	String queryType = IDataUtil.chkParam(data,"QUERY_TYPE");//0：集团，1：成员
    	String queryNum = IDataUtil.chkParam(data,"QUERY_NUM");
    	IData result = new DataMap();
    	result.put("OPR_SEQ", oprSeq);
    	result.put("QUERY_TYPE", queryType);
    	result.put("QUERY_NUM", queryNum);
    	IDataset prodInfos = new DatasetList();
    	IDataset userinfos = new DatasetList();
    	if("0".equals(queryType)){       //集团
    		 //一、查询集团用户信息
    		//IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(queryNum,null);
            //if (groupInf == null || groupInf.size() == 0)
            //{
            //    CSAppException.apperr(CrmUserException.CRM_USER_899, queryNum);
            //}
    		//String custId = groupInf.getData(0).getString("CUST_ID");
            //userinfos = UserInfoQry.getUserInfoByCandB(custId, "WLWG", Route.CONN_CRM_CG);
     
            String subsId = queryNum;
            String userId = "";
            IData instanPfInfo = InstancePfQuery.queryUserBySubsIdAndInstType(subsId, "U", Route.CONN_CRM_CG);
            if (IDataUtil.isNotEmpty(instanPfInfo))
            {
                userId = instanPfInfo.getString("USER_ID");
                IData users = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
                if (IDataUtil.isNotEmpty(users))
                {
                	userinfos.add(users);
                }
            }
    	 }else if("1".equals(queryType)){ //成员
    		 userinfos = UserInfoQry.getUsersBySn(queryNum);
    	 }
    	
    	 if (IDataUtil.isEmpty(userinfos)) 
         {
             CSAppException.apperr(CrmUserException.CRM_USER_760);
         }
    	 for(int a = 0; a < userinfos.size(); a++){
         	IData userinfo = userinfos.getData(a);
         	String userId = userinfo.getString("USER_ID");
         	//查询用户订购的服务
            IDataset userSvcs =  UserSvcInfoQry.queryUserServices(userId);
            if(IDataUtil.isNotEmpty(userSvcs)){
            	for(int i = 0; i < userSvcs.size(); i++){
            		IData userSvc = userSvcs.getData(i);
            		if(SVC_CONFIG_MAP.get(userSvc.getString("SERVICE_ID")) != null){
            			IData svcConfig = (IData) SVC_CONFIG_MAP.get(userSvc.getString("SERVICE_ID"));
            			IData prodInfo = new DataMap();
            			prodInfo.put("PROD_ID", svcConfig.getString("PARA_CODE1"));
            			prodInfo.put("PKG_PROD_ID", svcConfig.getString("PARA_CODE2"));
            			prodInfo.put("PROD_INST_ID", userSvc.getString("INST_ID"));
            			prodInfo.put("PROD_INST_EFF_TIME", userSvc.getString("START_DATE"));
            			prodInfo.put("PROD_INST_EXP_TIME", userSvc.getString("END_DATE"));
            			
            			IDataset prodAttrInfos = new DatasetList();
            			
            			//查询用户的属性信息
            			IDataset userAttrs = UserAttrInfoQry.getuserAttrByUserIdSvcId(userId, userSvc.getString("SERVICE_ID"));
            			if(IDataUtil.isNotEmpty(userAttrs)){
            				for(int j = 0; j < userAttrs.size(); j++){
            					IData userAttr = userAttrs.getData(j);
            					IData prodAttrInfo = new DataMap();
            					prodAttrInfo.put("SERVICE_ID", svcConfig.getString("PARA_CODE3"));
            					prodAttrInfo.put("ATTR_KEY", userAttr.getString("ATTR_CODE"));
            					prodAttrInfo.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
            					prodAttrInfos.add(prodAttrInfo);
            				}
            			}
            			
            			if(IDataUtil.isNotEmpty(prodAttrInfos)){
            				prodInfo.put("PROD_ATTR_INFO", prodAttrInfos);
            			}
            			prodInfos.add(prodInfo);
            		}
            	}
            }
            
            IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
            if(IDataUtil.isNotEmpty(userDiscnts)){
            	for(int i = 0; i < userDiscnts.size(); i++){
            		IData userDiscnt = userDiscnts.getData(i);
            		if(SVC_CONFIG_MAP.get(userDiscnt.getString("SERVICE_ID")) != null){
            			IData discntConfig = (IData) DISCNT_CONFIG_MAP.get(userDiscnt.getString("DISCNT_CODE"));
            			IData prodInfo = new DataMap();
            			prodInfo.put("PROD_ID", discntConfig.getString("PARA_CODE1"));
            			prodInfo.put("PKG_PROD_ID", discntConfig.getString("PARA_CODE2"));
            			prodInfo.put("PROD_INST_ID", userDiscnt.getString("INST_ID"));
            			prodInfo.put("PROD_INST_EFF_TIME", userDiscnt.getString("START_DATE"));
            			prodInfo.put("PROD_INST_EXP_TIME", userDiscnt.getString("END_DATE"));
            			
            			IDataset prodAttrInfos = new DatasetList();
            			
            			//查询用户的属性信息
            			IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserIdInstid(userId, "D", userDiscnt.getString("INST_ID"));
            			if(IDataUtil.isNotEmpty(userAttrs)){
            				for(int j = 0; j < userAttrs.size(); j++){
            					IData userAttr = userAttrs.getData(j);
            					IData prodAttrInfo = new DataMap();
            					prodAttrInfo.put("SERVICE_ID", userDiscnt.getString("PARA_CODE3"));
            					prodAttrInfo.put("ATTR_KEY", userAttr.getString("ATTR_CODE"));
            					prodAttrInfo.put("ATTR_VALUE", userAttr.getString("ATTR_VALUE"));
            					prodAttrInfos.add(prodAttrInfo);
            				}
            			}
            			
            			if(IDataUtil.isNotEmpty(prodAttrInfos)){
            				prodInfo.put("PROD_ATTR_INFO", prodAttrInfos);
            			}
            			prodInfos.add(prodInfo);
            		}
            	}
            }
         }
         
    	result.put("PROD_INFO", prodInfos);
    	return result;
    }
    
    public static Map<String, IData> loadConfig(String paramAttr) throws Exception
    {
        Map<String, IData> configMap = new HashMap<String, IData>();
        IDataset configList = CommparaInfoQry.getCommByParaAttr("CSM", paramAttr, "0898");
        for (int i = 0; i < configList.size(); i++)
        {
            IData config = configList.getData(i);
            configMap.put(config.getString("PARAM_CODE"), config);
        }
        return configMap;
    }
	
	
	    /**
     * 物联网PBOSS二期（暨车联网前装产品）业务支撑系统实施方案
     * 8.51 反向用户信息与个人产品订购关系信息同步申请 接口 add by lihb3 20160804
     * @param input
     * @return
     * @throws Exception
     */
    
   public IData reverIotInfoSyn(IData input) throws Exception{
	   IDataUtil.chkParam(input, "PERSON_INFO");
	   
	   IDataset personInfos  =  input.getDataset("PERSON_INFO");
	   int reqCounts = personInfos.size();
	   IData personInfo = new DataMap();
	   IData result = new DataMap();
	   
	   for(int i=0;i<reqCounts;i++)
	   {
		   personInfo = personInfos.getData(i);
		   String oprSeq = IDataUtil.chkParam(personInfo, "OPR_SEQ");                     // 本次操作的流水号
		   String serialNumber = IDataUtil.chkParam(personInfo, "SERIAL_NUMBER");         // 手机号
		   String oprCode = IDataUtil.chkParam(personInfo, "OPR_CODE");  
		   // 本业务信息的操作编码
		   if("02".equals(oprCode))
		   {
			   //销户
			   IData param = new DataMap();
	           param.put("SERIAL_NUMBER", serialNumber);
	           input.put("OPR_SEQ", oprSeq);
	           param.put("REMOVE_REASON", "物联网PBOSS反向销户");
	           param.put("TRADE_TYPE_CODE", "192");
	           result = CSAppCall.call("SS.DestroyUserNowRegSVC.tradeReg", param).getData(0); 
	    	   break;
		   }
		   
		   IDataUtil.chkParam(personInfo, "PROD_INFO");
		   IDataset prodInfos  =  personInfo.getDataset("PROD_INFO");                // 原子产品信息
		   IData prodInfo = new DataMap();
		   IDataset elements = new DatasetList();
		   String suspendServiceIds = "";
		   String resumeServiceIds = "";
		   
		   for(int k=0;k<prodInfos.size();k++)
		   {
			   prodInfo = prodInfos.getData(k);
			   String prodId =  prodInfo.getString("PROD_ID");                        // 原子产品编码
			   String prodOperCode = prodInfo.getString("PROD_OPER_TYPE");            // 产品操作类型
			   
			   String elementId = "";
			   String elementTypeCode = "S";
			   String pkgProdId =  prodInfo.getString("PKG_PROD_ID");
			   IDataset dataset = CommparaInfoQry.getInfoParaCode1_2("CSM","9014",prodId,pkgProdId);
			   
			   if(IDataUtil.isEmpty(dataset))
			   {
	 			    dataset = CommparaInfoQry.getInfoParaCode1_2("CSM","9013",prodId,pkgProdId);
	 			    if(IDataUtil.isEmpty(dataset))
	 			    {
	 			    	result.put("X_RESULTCODE", "-1");
	 					result.put("X_RESULTINFO", "数据库未配置车联网产品对应的服务或资费");
	 					return result;
	 			    }	
	 			    elementTypeCode = "D"; 
	 		   }
			   elementId = dataset.getData(0).getString("PARAM_CODE"); 	
			   
			   String instId = "";
		       String userId = "";
		       String productId = "-1";
		       String packageId = "-1";
	           UcaData ucaData = null;	
			   
			   if(!"01".equals(prodOperCode))
			   {
					//不是新增,则根据原子产品实例来获得原子产品数据		
		        	ucaData = UcaDataFactory.getNormalUca(serialNumber);
		        	userId = ucaData.getUserId();
			        String proInstd = prodInfo.getString("PRPD_INST_ID", "");
			        if(StringUtils.isNotBlank(proInstd))
			        {
			        	 IDataset instanceInfos = InstancePfQuery.queryInstancePfInfoByUserIds(userId, "S", proInstd);  
			        	 if(IDataUtil.isNotEmpty(instanceInfos))
			        	 {
			        		 instId = instanceInfos.first().getString("INST_ID");	
			        		 
			        		 String route = RouteInfoQry.getEparchyCodeBySn(serialNumber);
				        	 this.setRouteId(route);
				        	 
			        		 SvcTradeData svcTradeData = ucaData.getUserSvcByInstId(instId);
			        		 
			        		 if(svcTradeData != null)
			        		 {
			        			 elementId = svcTradeData.getElementId();
			        			 elementTypeCode = "S";
			        			 productId = svcTradeData.getProductId();
			        			 packageId = svcTradeData.getPackageId();
			        		 }
			        		 else
			        		 {
			        			 DiscntTradeData discntTradeData = ucaData.getUserDiscntByInstId(instId);
			        			 if(discntTradeData != null)
			        			 {
			        				 elementId = discntTradeData.getDiscntCode();
			        				 elementTypeCode = "D";
			        				 productId = discntTradeData.getProductId();
				        			 packageId = discntTradeData.getPackageId();
			        			 }
			        			 else
			        			 {    
				 			    	  result.put("X_RESULTCODE", "-1");
				 					  result.put("X_RESULTINFO", "用户没有订购此原子产品:"+prodId);
				 					  return result;
			        			 }
			        		 }	        		 
			        	 }
			        	 else
			        	 {
			        		 result.put("X_RESULTCODE", "-1");
			 				 result.put("X_RESULTINFO", "用户没有此原子产品实例:"+proInstd);
			 				 return result;
			        	 }	        	   
			         }
			        else
			        {    
			        	 result.put("X_RESULTCODE", "-1");
		 				 result.put("X_RESULTINFO", "ProdInfo.OperType为02/03/04/05时,PRPD_INST_ID不能为空！");
		 				 return result;
			        }
		       }
			   
			   
			   IData params = new DataMap();
	           String modifyTag = "";
	           
	           if("01".equals(prodOperCode))
	           {
	    	       modifyTag = "0";
	           }
	           else if("02".equals(prodOperCode))
	           {
	    	       modifyTag = "1";
	           }
	           else if("03".equals(prodOperCode))
	           {
		    	   modifyTag = "2";
		       }
	           else
	           {
	        	// 暂停服务
					if ("04".equals(prodOperCode)) 
					{	
						if (StringUtils.isEmpty(suspendServiceIds))
						{
							suspendServiceIds = elementId;
						}
						else
						{
							suspendServiceIds = suspendServiceIds + "," + elementId;
						}
					}
					// 恢复服务
					if ("05".equals(prodOperCode)) 
					{
						if (StringUtils.isEmpty(resumeServiceIds))
						{
							resumeServiceIds = elementId;
						}
						else
						{
							resumeServiceIds = resumeServiceIds + "," + elementId;
						}
					}
					
				   continue;
		       }

			   int index = 0;
			   IDataset prodAttrInfos  =  prodInfo.getDataset("PROD_ATTR_INFO");
			   if(IDataUtil.isNotEmpty(prodAttrInfos)){
				   index = prodAttrInfos.size();
				   for(int a = 0,j =1; a < index; a++,j+=2){
					   IData prodAttrInfo = prodAttrInfos.getData(a);
					   params.put("ATTR_STR"+(j), prodAttrInfo.getString("PROD_ATTR_KEY"));
					   params.put("ATTR_STR"+(j+1), prodAttrInfo.getString("PROD_ATTR_VALUE"));
				   }		   
			   }
			   
			   IDataset svcAttrInfos  =  prodInfo.getDataset("SVC_ATTR_INFO");
		       if(IDataUtil.isNotEmpty(svcAttrInfos)){
				   for(int a = 0,j=index*2; a < svcAttrInfos.size(); a++,j+=2){
					   IData svcAttrInfo = svcAttrInfos.getData(a);
					   params.put("ATTR_STR"+(j+1), svcAttrInfo.getString("SVC_ATTR_KEY"));
					   params.put("ATTR_STR"+(j+2), svcAttrInfo.getString("SVC_ATTR_VALUE"));
					   params.put("WLW_SVC_ATTR", "WLW_SVC_ATTR");
				   }	
			   }
		       
	           params.put("SERIAL_NUMBER", serialNumber);
	           params.put("PRODUCT_ID", productId);
	           params.put("PACKAGE_ID", packageId);
	           params.put("ELEMENT_ID", elementId);
	           params.put("ELEMENT_TYPE_CODE", elementTypeCode);
	           params.put("INST_ID", prodInfo.getString("PRPD_INST_ID",""));
	           params.put("MODIFY_TAG", modifyTag);

	           elements.add(params);	          
		   }
		   
		   IData inParam = new DataMap(); 
		   inParam.put("SERIAL_NUMBER", serialNumber);// 标识号码
		   inParam.put("OPR_SEQ", oprSeq);     //在主台账中设置反向订购标志
		   
		   if(StringUtils.isNotBlank(suspendServiceIds) || StringUtils.isNotBlank(resumeServiceIds))
		   {
			   inParam.put("SUSPEND_SERVICE", suspendServiceIds);
			   inParam.put("RESUME_SERVICE", resumeServiceIds);
			   result = CSAppCall.call("SS.SuspendResumeServiceRegSVC.tradeReg", inParam).getData(0);
		   }
		   
		   
		   if(IDataUtil.isNotEmpty(elements))
		   {
			   inParam.put("ELEMENTS", elements);
		      
		       result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", inParam).getData(0);	
		   }
	   }
	   result.put("X_RESULTCODE", "0"); 
       result.put("X_RESULTINFO", "OK");	      
	   return result;
    }
   
   /**
    * 物联网PBOSS二期（暨车联网前装产品）业务支撑系统实施方案
    * 8.54 反向订单查询接口 add by huping 20160815
    * @param input
    * @return
    * @throws Exception
    */
   public static IData queryOrder(IData input) throws Exception{

	   String query_oper_seq = IDataUtil.chkParam(input, "QUERY_OPER_SEQ");  //需要查询的操作流水
		
		IData resultData = new DataMap();
		IDataset result = new DatasetList();
		
		String[] query_operseq = query_oper_seq.split(",");
		IDataset qry_oper_seq_list = input.getDataset("QUERY_OPER_SEQ");
		if(IDataUtil.isNotEmpty(qry_oper_seq_list))
		{
			for(int i = 0;i<qry_oper_seq_list.size();i++){
				String queryseq = (String)qry_oper_seq_list.get(i);
				IData tradeinfo = IotQuery.queryTradeIdByOperseq(queryseq);
				IData orderinfo = new DataMap();
				if(IDataUtil.isNotEmpty(tradeinfo)){
					String tradeId = tradeinfo.getString("TRADE_ID");
					orderinfo = IotQuery.queryStateByTradeID(tradeId);
				}else{
					orderinfo.put("ORDER_STATUS", "99");//99:被查询的订单不存在
				}
				orderinfo.put("QUERY_OPER_SEQ", queryseq);
				result.add(orderinfo);
			}
		}
		
		resultData.put("ORDER_INFO", result);
		resultData.put("X_RESULTCODE", "0");
		resultData.put("X_RESULTINFO", "OK");
		return resultData;
	}

	
	/**
     * 
     * 反向用户状态同步申请接口
     * @author xiezhong
     * @param data
     * @return
     * @throws Exception
     */
    public IData  reverseUserStateChg(IData data) throws Exception{
    	/**
    	 * input :   
    	 * 			SERIAL_NUMBER    电话号码
    	 *          USER_STATUS      用户状态代码
    	 *          OPR_SEQ          本次操作流水
    	 *          OTHER_STATUS     其他状态代码   --反向：4
    	 *          PKG_SEQ          发起方交易包流水号
    	 * 
    	 * output ：
    	 *          X_RESULTCODE     返回结果代码
    	 *          X_RESULTINFO	  返回结果说明	
    	 */
 	  IData input = new DataMap();
 	  IDataset resultList = new DatasetList();
 	  IData output = new DataMap();
 	  IDataUtil.chkParam(data, "CHG_USER_INFO");
	  IDataset userInfos  =  data.getDataset("CHG_USER_INFO");
	  for(int i = 0; i < userInfos.size(); i++){
		  IData userinfo = userInfos.getData(i);
		  String serialNumber = userinfo.getString("SERIAL_NUMBER");
	 	  String userStatus = userinfo.getString("USER_STATUS");
	      UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
	      input.put("SERIAL_NUMBER", serialNumber);
	      input.put("OPR_SEQ", userinfo.getString("OPR_SEQ"));
	      if(StringUtils.equals(userStatus, "00")){
	    		//开机(1.停机转正常期  2.沉默期转正常期)   		
	    		//判断是停机还是沉默期
	    		if("F".equals(uca.getUser().getUserTypeCode())){ //沉默期
	    			resultList = CSAppCall.call("SS.SilenceTransNormalSVC.tradeReg", input);
	    		}else if("1".equals(uca.getUser().getUserStateCodeset())){ //停机
	    			input.put("IS_CHECK_PSPT", false);
	    			resultList = CSAppCall.call("SS.OpenMobileRegSVC.tradeReg", input);
	    		} 	    		
	    	}else if(StringUtils.equals(userStatus, "02")){
	    		//停机
	    		//正常期转停机
	    		if("1".equals(uca.getUser().getUserStateCodeset())){ 
	    			//已停机，返回成功
	    			output.put("X_RESULTCODE", "0");
	                output.put("X_RESULTINFO", "trade ok");
	    		}else if("0".equals(uca.getUser().getUserStateCodeset())){
	                input.put("IS_CHECK_PSPT", false);
	    			resultList = CSAppCall.call("SS.StopMobileRegSVC.tradeReg", input);
	    		}
	    	}else if(StringUtils.equals(userStatus, "07")){
	    		//待激活
	    		//测试期转沉默期    判断是否是 oldStateCode=0 && newStateCode=1 && dealTag=0
	    		//否则不予变更   不能跨状态变更
	    		resultList = CSAppCall.call("SS.TestTransSilenceSVC.tradeReg",input);
	    	}
	   }
    	// 拼接返回结果
    	if (resultList != null && !resultList.isEmpty())
        {
           	output.put("X_RESULTCODE", "0");
            output.put("X_RESULTINFO", "trade ok");
    	
        }else{
        	output.put("X_RESULTCODE", "-1");
        	output.put("X_RESULTINFO", "接口调用失败");
        }
    	return output;
    }
    
    /**
     * 
     * 反向码号信息同步BIP3B621
     * @author 
     * @param data
     * @return
     * @throws Exception
     */
    public IData reverseNumSync(IData data) throws Exception
    { 
        IData output = new DataMap();
        output.put("X_RESULTCODE", "0");
        output.put("X_RESULTINFO", "trade ok");
       
        try
        { 
            regOrderInfo(data);
        }
        catch (Exception e)
        {
            IData exceptionInfo = ExceptionUtils.getExceptionInfo(e); 
            output.put("X_RESULTCODE", "-1");
            output.put("X_RESULTINFO",exceptionInfo.getString("RESULT_INFO","接口调用失败"));
        }
       
       
        return output;
    }

    private void regOrderInfo(IData data) throws Exception
    {
        IDataset userInfos = IDataUtil.chkParamDataset(data, "U_REG_INFO");
        for (int i = 0; i < userInfos.size(); i++)
        {
            IData userinfo = userInfos.getData(i); 
            
            String oprCode = IDataUtil.chkParam(userinfo, "OPR_CODE");
            
            if ("03".equals(oprCode))
            {
                IData input = new DataMap();
                input.put("SERIAL_NUMBER", IDataUtil.chkParam(userinfo, "SERIAL_NUMBER"));
                input.put("OPR_SEQ", IDataUtil.chkParam(userinfo, "OPR_SEQ"));
                input.put("IMSI",  IDataUtil.chkParam(userinfo, "IMSI"));
                
                CSAppCall.call("SS.ChangeCardSVC.tradeReg", userinfo);
            }
            else if ("04".equals(oprCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "不支持的操作类型[04]异地补卡");
            }
            else if ("05".equals(oprCode))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "不支持的操作类型[05]号码变更");
            } 
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "不支持的操作类型["+oprCode+"]"); 
            }
            
        }
    }

    /**
     * 生成本省物联网用户部分信息
     *
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset buildPartInfo2Iotuser(IData input) throws Exception {    	
        IDataset resultSet = new DatasetList();
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULTINFO", "ok");
        result.put("X_BOOKLIST", "0");
        result.put("RESULT_CODE", "0");
        result.put("RESULT_INFO", "ok");
        //String systime = SysDateMgr.getSysTime();
        int partition_start = input.getInt("PARTITION_START", 0);
        int partition_end = input.getInt("PARTITION_END", 0);

        if(partition_end ==0){
            result.put("RESULT_CODE", "-1");
            result.put("RESULT_INFO", "PARTITION_END不能为空或者为0");
            resultSet.add(result);
            return resultSet;
        }

        IDataset callingTypeCodeset = StaticUtil.getStaticList("WLW_APPLY_INDUSTRY");
        IData callingTypeCodeMap = new DataMap();
        IData callingTypeCode;

        for (int i = 0; i < callingTypeCodeset.size(); i++) {
            callingTypeCode = callingTypeCodeset.getData(i);
            callingTypeCodeMap.put(callingTypeCode.getString("DATA_ID"), callingTypeCode.getString("DATA_NAME"));
        }
                
       // while(flag) {
            //userpagination.setCurrent(currentpage++);
        	long start=System.currentTimeMillis();
            IDataset userInfoList = IotQuery.queryAllUserInfo(partition_start, partition_end);
            long end=System.currentTimeMillis();
            if (logger.isDebugEnabled()){
            	logger.debug("iot总时长:"+(end-start)+"ms");
            }	
//            if(userInfoList.size()==0){
//                flag = false;
//                continue;
//            }

            IDataset insDataset = new DatasetList();
            IDataset updDataset = new DatasetList();

            IData userInfo;
            String userId_B;
            IData custInfo;

        	//循环总运行时长日志  开始
        	long forStart=System.currentTimeMillis();
            for (int i = 0; i < userInfoList.size(); i++) {
                userInfo = userInfoList.getData(i);
                userId_B = userInfo.getString("USER_ID");

                long a1=System.currentTimeMillis();
                IDataset userRels = RelaUUInfoQry.getUUInfoByUserIdB(userId_B);               
                long a2=System.currentTimeMillis();
                if (logger.isDebugEnabled()){
                	logger.debug("a时长:"+(a2-a1)+"ms");
                }	 
                
                /*queryGroupInfo(userRels, rtnData) 简化开始*/
                String custId_A = null;
                String mgrId = null;
                for (Object obj : userRels) {
                    IData userRel = (IData) obj;
                    String USER_ID_A = userRel.getString("USER_ID_A");                      
//                      IDataset grpUserset = CSAppCall.call("CS.UcaInfoQrySVC.qryUserInfoByUserId", input1);	                      
                    	long b1=System.currentTimeMillis();              
                    	IData grpUser = UcaInfoQry.qryUserInfoByUserId(USER_ID_A);
                        long b2=System.currentTimeMillis();
                        if (logger.isDebugEnabled()){
                        	logger.debug("b时长:"+(b2-b1)+"ms");
                        }	
                        if (IDataUtil.isEmpty(grpUser) || StringUtils.isBlank(grpUser.getString("CUST_ID"))) {
                            continue;
                        }
                        custId_A = grpUser.getString("CUST_ID");
//                        IDataset grpCust = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", input1);
                        long c1=System.currentTimeMillis();                     
                        IData grpCust = UcaInfoQry.qryGrpInfoByCustId(custId_A);
                        long c2=System.currentTimeMillis();
                        if (logger.isDebugEnabled()){
                        	logger.debug("c时长:"+(c2-c1)+"ms");
                        }	
                        if (IDataUtil.isNotEmpty(grpCust)) {
                        	mgrId = grpCust.getString("CUST_MANAGER_ID");
                        	userInfo.put("GROUP_NAME", grpCust.getString("CUST_NAME"));//集团名称
                            break;
                        }
                    }             
                /*queryGroupInfo(userRels, rtnData) 简化结束*/
                
                /*queryCustManagerInfo(rtnData) 简化开始*/              	                   
	                    if (StringUtils.isNotBlank(mgrId)) {	                        
	                        long d1=System.currentTimeMillis();
//	                        IDataset mgrInfo = CSAppCall.call("CS.CustManagerInfoQrySVC.qryCustManagerInfoById", input);
	                        IDataset mgrInfo = IotQuery.qryCustManagerStaffById(mgrId);//超慢
	                        long d2=System.currentTimeMillis();
	                        if (logger.isDebugEnabled()){
	                        	logger.debug("d时长:"+(d2-d1)+"ms");
	                        }	
	                        
	                        if (IDataUtil.isNotEmpty(mgrInfo)) {
	                        	userInfo.put("CUST_MANAGER_NAME", mgrInfo.getData(0).getString("CUST_MANAGER_NAME"));//客户经理名称	                        	
	                        } else {
	                        	userInfo.put("CUST_MANAGER_NAME", "已离岗");
	                        }
	                    } else {
	                    	userInfo.put("CUST_MANAGER_NAME", "未分派");
	                    }	                            
                /*queryCustManagerInfo(rtnData) 简化结束*/
                
                custInfo = UcaInfoQry.qryCustInfoByCustId(userInfo.getString("CUST_ID"));//custId_B

                if(IDataUtil.isNotEmpty(custInfo)){
                    userInfo.put("CALLING_TYPE_CODE",custInfo.getString("CALLING_TYPE_CODE"));//行业ID
                }
           
                long f1=System.currentTimeMillis();
                IDataset userAttrApnName = IotQuery.queryUserAttrInfo(userInfo);
                long f2=System.currentTimeMillis();
                if (logger.isDebugEnabled()){
                	logger.debug("f时长:"+(f2-f1)+"ms");
                }
                
                for (int j = 1; j <= userAttrApnName.size() ; j++) {
                    userInfo.put("APN"+j, userAttrApnName.getData(j-1).getString("ATTR_VALUE"));
                }
                userInfo.put("OPEN_DATE", userInfo.getString("OPEN_DATE"));//开户时间
                userInfo.put("CALLING_TYPE_CODE", callingTypeCodeMap.getString(userInfo.getString("CALLING_TYPE_CODE")));//行业NAME
                userInfo.put("PROVINCIAL_ROAM_TAG", "1");//是否开通国际漫游
                userInfo.put("OPRFLAG", userInfo.getString("USER_STATE_CODESET"));//用户停开机状态
                userInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
                userInfo.put("IN_DATE", SysDateMgr.getSysTime());

                IDataset iotUserInfo = IotQuery.queryByIotUserID(userId_B);
                if (IDataUtil.isNotEmpty(iotUserInfo)) {
                    updDataset.add(userInfo);
                } else {
                    insDataset.add(userInfo);
                }
            }
            //循环完总运行时长日志  结束
            long forEnd=System.currentTimeMillis();
            if (logger.isDebugEnabled()){
            	logger.debug("for总运行时长:"+(forEnd-forStart)+"ms");
            }

            Dao.executeBatchByCodeCode("TF_F_IOT_USER", "INS_IOT_USER", insDataset);
            Dao.executeBatchByCodeCode("TF_F_IOT_USER", "UPD_IOT_USER", updDataset);


        //}
        resultSet.add(result);    	
        return resultSet;
    }


    private void queryGroupInfo(IDataset userRels, IDataset rtnData) throws Exception {
        IData input = new DataMap();
        IData grpUserData = new DataMap(); // key:grpUserId, value:grpCustId
        for (Object obj : userRels) {
            IData userRel = (IData) obj;
            String grpUserId = userRel.getString("USER_ID_A");
            String custId = grpUserData.getString(grpUserId, "");
            if (StringUtils.isBlank(custId)) {
                input.clear();
                input.put("USER_ID", grpUserId);
                IDataset grpUserset = CSAppCall.call("CS.UcaInfoQrySVC.qryUserMainProdInfoByUserIdForGrp", input);
                if (IDataUtil.isEmpty(grpUserset) || StringUtils.isBlank(grpUserset.getData(0).getString("CUST_ID"))) {
                    continue;
                }
                custId = grpUserset.getData(0).getString("CUST_ID");
                grpUserData.put(grpUserId, custId);

                input.clear();
                input.put("CUST_ID", custId);
                IDataset grpCust = CSAppCall.call("CS.UcaInfoQrySVC.qryGrpInfoByCustId", input);
                if (IDataUtil.isNotEmpty(grpCust)) {
                    rtnData.add(grpCust.getData(0));
                    break;
                }
            }
        }
    }
    private IDataset queryUserAttrInfo(IData tabTradeInfo) throws Exception {
        IData inParam = new DataMap();
        inParam.put("USER_ID", tabTradeInfo.getString("USER_ID"));
        inParam.put("ATTR_CODE", "APNNAME");

        //查询用户物联网策略属性
        IDataset userApnNames = Dao.qryByCode("TF_F_USER_ATTR", "SEL_BY_ATTRCODE", inParam);
        return userApnNames;
    }

    private void queryCustManagerInfo(IDataset rtnData) throws Exception {
        if (IDataUtil.isEmpty(rtnData)) return;

        IData input = new DataMap();
        for (Object obj : rtnData) {
            IData group = (IData) obj;
            String mgrId = group.getString("CUST_MANAGER_ID", "");
            if (StringUtils.isNotBlank(mgrId)) {
                input.clear();
                input.put("CUST_MANAGER_ID", mgrId); 
                IDataset mgrInfo = CSAppCall.call("CS.CustManagerInfoQrySVC.qryCustManagerInfoById", input);
                if (IDataUtil.isNotEmpty(mgrInfo)) {
                    group.put("MANAGER_NAME", mgrInfo.getData(0).getString("CUST_MANAGER_NAME"));
                } else {
                    group.put("MANAGER_NAME", "已离岗");
                }
            } else {
                group.put("MANAGER_NAME", "未分派");
            }
        }
    }

    /**
	 * 反向达量限速消息接口
	 * 当用户使用的流量满足降速条件，内容计费对订购了自动降速产品的用户触发降速提醒，下发给各省BOSS
	 * @param input
	 * @throws Exception
	 */
	public IData reverSLInfoNotice(IData input) throws Exception
	{
		IData result = new DataMap();
		IDataUtil.chkParam(input, "USER_INFO");
		IDataset userInfos = input.getDataset("USER_INFO");
		
		IData userInfo = null;
		for(int i=0; i<userInfos.size(); i++)
		{
			userInfo = userInfos.getData(i);
			String oprSeq = IDataUtil.chkParam(userInfo, "OPR_SEQ");  
			String serialNumber = IDataUtil.chkParam(userInfo, "SERIAL_NUMBER"); 
			String apnName = IDataUtil.chkParam(userInfo, "APN_NAME");
			String slRate = IDataUtil.chkParam(userInfo, "SL_RATE");  //限速速率
			String slThreshold = IDataUtil.chkParam(userInfo, "SL_THRESHOLD"); //限速阈值
			String slStrategy = IDataUtil.chkParam(userInfo, "SL_STRATEGY"); //限速策略	
		
			String route = RouteInfoQry.getEparchyCodeBySn(serialNumber);
      	 	super.setRouteId(route);
			
           IData userData = UcaInfoQry.qryUserInfoBySn(serialNumber);
          
           if (IDataUtil.isEmpty(userData))
           {
           	
		    	result.put("X_RESULTCODE", "-1");
				result.put("X_RESULTINFO", "用户资料不存在"+serialNumber);
				return result;
           }
          
   	    	UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);  
   	    
   	    	//获取用户订购的哪个通信服务
   	    	List<AttrTradeData> attrApnTradeDatas = ucaData.getUserAttrs();
   	    
   	    	AttrTradeData userApnAttrTradeData = null;
   	    	for(AttrTradeData attrTradeData : attrApnTradeDatas)
   	    	{
   	    		if("APNNAME".equals(attrTradeData.getAttrCode()) && apnName.equals(attrTradeData.getAttrValue()) 
   	    				&& "S".equals(attrTradeData.getInstType()))
   	    		{
   	    			userApnAttrTradeData = attrTradeData;
   	    			break;
   	    		}
   	    	}
   	    	if(userApnAttrTradeData == null){
   	    		result.put("X_RESULTCODE", "-1");
   	    		result.put("X_RESULTINFO", "用户没有开通APNNAME为"+apnName+"的数据通信服务");	      
   	    		return result;
   	    	}
   	    
   	    	String relaInstId = userApnAttrTradeData.getRelaInstId();
   	    	String serviceId = userApnAttrTradeData.getElementId();
   	    	AttrTradeData userServiceCodeAttrDatas = ucaData.getUserAttrsByRelaInstIdAttrCode(relaInstId, "ServiceCode");
   	    	//if(userServiceCodeAttrDatas == null || !slStrategy.equals(userServiceCodeAttrDatas.getAttrValue())){
   	    	if(userServiceCodeAttrDatas == null){
   	    		result.put("X_RESULTCODE", "-1");
   	    		result.put("X_RESULTINFO", "用户没有开通自动达量降速的策略");	      
   	    		return result;
   	    	}
			
   	    	SvcTradeData svcTradeData = ucaData.getUserSvcByInstId(relaInstId);
   	    	if(svcTradeData == null){
   	    		result.put("X_RESULTCODE", "-1");
   	    		result.put("X_RESULTINFO", "用户没有订购开通自动达量降速的策略的数据通信服务");	      
   	    		return result;
   	    	}    	   
   			   
			IDataset selectedElements = new DatasetList();	
			IData element = new DataMap();
			IDataset attrDatas = new DatasetList();
			IData attr1 = new DataMap();
			attr1.put("ATTR_CODE","ServiceCode");
			attr1.put("ATTR_VALUE", slStrategy);
			attr1.put("MODIFY_TAG", "2");
			attrDatas.add(attr1);
			IData attr2 = new DataMap();
			attr2.put("ATTR_CODE","OperType");
			attr2.put("ATTR_VALUE", "03");
			attr2.put("MODIFY_TAG", "2");
			attrDatas.add(attr2);
			IData attr3 = new DataMap();
			attr3.put("ATTR_CODE","ServiceUsageState");
			attr3.put("ATTR_VALUE", slRate);
			attr3.put("MODIFY_TAG", "2");
			attrDatas.add(attr3);	
             
			element.put("ELEMENT_ID", serviceId);
			element.put("ELEMENT_TYPE_CODE", "S");
	        element.put("MODIFY_TAG", "2");		
	        element.put("INST_ID", svcTradeData.getInstId());
	        element.put("PRODUCT_ID", svcTradeData.getProductId());
	        element.put("PACKAGE_ID", svcTradeData.getPackageId());
	        element.put("ATTR_PARAM", attrDatas);  
	        selectedElements.add(element);
	        
	        IData param = new DataMap();
	        param.put("SERIAL_NUMBER", serialNumber);
			param.put("SELECTED_ELEMENTS", selectedElements);			
	        param.put("FLOW_PAYMENT_ID", oprSeq);                    //在主台账中保存操作流水号
	        param.put("REMARK", "IOT_REVERT");     //在主台账中设置反向订购标志	
	        result = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", param).getData(0);
		}
				
		result.put("X_RESULTCODE", "0");
	    result.put("X_RESULTINFO", "接受反向请求成功");	      
		return result;
	}
	
	/**
   	 * 个人用户信息同步接口
   	 * 个人客户在物联网开卡、销户时，集中化系统把个人用资料同步给省BOSS，省BOSS保存个人用户信息。wangsc
   	 * @param input
   	 * @author jianggang 2018.11.6
   	 * @throws Exception
   	 * */
   	public IData indivUserInforsyn(IData input) throws Exception{
   		
   		IData result  = new DataMap();
   		String oprSeq = "";
   		try {
   			//对入参进行判空处理，并塞入Map中
   			oprSeq = IDataUtil.chkParamNoStr(input, "OPR_SEQ");
   			IData params = new DataMap();
   			params.put("OPR_SEQ", oprSeq);
   			params.put("OPR_CODE", IDataUtil.chkParamNoStr(input, "OPR_CODE"));
   			params.put("OPR_TIME", IDataUtil.chkParamNoStr(input, "OPR_TIME"));
   			params.put("CHANNEL", IDataUtil.chkParamNoStr(input, "CHANNEL"));
   			params.put("USER_ID", IDataUtil.chkParamNoStr(input, "SUBS_ID"));
   			params.put("CUST_ID", IDataUtil.chkParamNoStr(input, "CUST_CODE"));
   			params.put("USER_TYPE", IDataUtil.chkParamNoStr(input, "USER_TYPE"));
   			params.put("USER_STATUS", IDataUtil.chkParamNoStr(input, "USER_STATUS"));
   			params.put("BIZ_TYPE", IDataUtil.chkParamNoStr(input, "BIZ_TYPE"));
   			params.put("SERIAL_NUMBER", IDataUtil.chkParamNoStr(input, "SERVICE_NUMBER"));
   			params.put("PROVINCE_ID", IDataUtil.chkParamNoStr(input, "BE_ID"));
   			params.put("REGION_ID", IDataUtil.chkParamNoStr(input, "REGION_ID"));
   			params.put("EFFECT_TIME", IDataUtil.chkParamNoStr(input, "EFFECT_TIME"));
   			
   			//调用Dao的insert方法入TF_B_CMIOT_USER表
   			boolean insertResult = Dao.insert("TF_B_CMIOT_USER", params);
   			
   			if (insertResult) {
   				result.put("OPR_SEQ", oprSeq);
   				result.put("BIZ_CODE", "0000");
   				result.put("BIZ_DESC", "个人用户信息同步成功！");
   			} else {
   				result.put("OPR_SEQ", oprSeq);
   				result.put("BIZ_CODE", "-1");
   				result.put("BIZ_DESC", "系统错误，个人用户信息同步失败！");
   			}
   		} catch (Exception e) {
   			result.put("OPR_SEQ", oprSeq);
   			result.put("BIZ_CODE", "-1");
   			result.put("BIZ_DESC", e.getMessage());
   		}
    
   		return result;
   	}
/**
	 * 获取用户状态描述
	 * 
	 * @param input
	 * @author ouym3 2019.2.15
	 */
	private String getIBossUserStateDesc(String param) {
		IData userStateData = new DataMap();
		userStateData.put("N", "信用有效时长开通");// 00
		userStateData.put("T", "骚扰电话半停机");// 01
		userStateData.put("0", "开通");// 00
		userStateData.put("1", "申请停机");// 02
		userStateData.put("2", "挂失停机");// 02
		userStateData.put("3", "并机停机");// 02
		userStateData.put("4", "局方停机");// 02
		userStateData.put("5", "欠费停机");// 02
		userStateData.put("6", "申请销号");// 04
		userStateData.put("7", "高额停机");// 02
		userStateData.put("8", "欠费预销号");// 03
		userStateData.put("9", "欠费销号");// 04
		userStateData.put("A", "欠费半停机");// 01
		userStateData.put("B", "高额半停机"); // 01
		userStateData.put("E", "转网销号停机");// 02
		userStateData.put("F", "申请预销停机");// 02
		userStateData.put("G", "申请半停机"); // 01
		userStateData.put("I", "申请停机（收月租）");// 02
		return userStateData.getString(param, "");
	}

	/**
	 * 手机用户信息查询 此方法主要是CMIOT系统查询手机号码状态信息
	 * 
	 * @param input
	 * @author ouym3 2019.2.15
	 * @throws Exception
	 */
	public IData checkPaymentUser(IData input) {
		IData resultInfo = new DataMap();// 返回结果
		String oprSeq = input.getString("OPR_SEQ", "");// 发起方操作的流水
		String serialNumber = input.getString("SERIAL_NUMBER", ""); // 服务号码
		try {
			IDataUtil.chkParam(input, "CHANNEL");
			IDataUtil.chkParam(input, "BIZ_TYPE");
			IDataUtil.chkParam(input, "OPR_SEQ");
			IDataUtil.chkParam(input, "CUST_TYPE");
			IDataUtil.chkParam(input, "SERIAL_NUMBER");
		} catch (Exception e1) {
			e1.printStackTrace();
			resultInfo.put("OPR_SEQ", oprSeq);
			resultInfo.put("BIZ_CODE", "0001");
			resultInfo.put("BIZ_DESC", e1.getMessage());
		}
		try {
			String route = RouteInfoQry.getEparchyCodeBySn(serialNumber);
			super.setRouteId(route);
			//IDataset userDataList = UserInfoQry.queryAllUserInfoBySn(serialNumber);
			//IDataset userDataList = UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
			IDataset userDataList = UserInfoQry.getLatestUserInfosBySerialNumber(serialNumber);
			// 空
			if (IDataUtil.isEmpty(userDataList)) {
				resultInfo.put("BIZ_CODE", "4005");
				resultInfo.put("BIZ_DESC", "号码不存在!");
				return resultInfo;
			}
			IData userData = userDataList.first();
			String userStateCodeSet = userData.getString("USER_STATE_CODESET");// 用户状态码
			String acctTag = userData.getString("ACCT_TAG");//待激活状态
			String remvoeTag = userData.getString("REMOVE_TAG");//待激活状态
			
			resultInfo.put("OPR_SEQ", oprSeq);
			IData userInfoMap = new DataMap(); // 用户信息
			IDataset userInfos = new DatasetList();
			userInfoMap.put("SERIAL_NUMBER", serialNumber);
			
			if(!"0".equals(remvoeTag))
			{
				resultInfo.put("BIZ_CODE", "0000");
				resultInfo.put("BIZ_DESC", "号码已经销户!");
				userInfoMap.put("IS_USE", "0");
				userInfoMap.put("UN_USER", "号码已经销户!");
				String custId = userData.getString("CUST_ID");
				IData custData = UcaInfoQry.qryCustomerInfoByCustId(custId);
				if (IDataUtil.isNotEmpty(custData)) {
					userInfoMap.put("USER_NAME", custData.get("CUST_NAME"));
				}
				userInfoMap.put("SERIAL_NUMBER", serialNumber);
				userInfos.add(userInfoMap);
				resultInfo.put("USER_INFO", userInfos);
				return resultInfo;
			}
			
			if("2".equals(acctTag))
			{
				resultInfo.put("BIZ_CODE", "0000");
				resultInfo.put("BIZ_DESC", "号码是未激活的号码!");
				userInfoMap.put("IS_USE", "0");
				userInfoMap.put("UN_USER", "号码是未激活的号码!");
				String custId = userData.getString("CUST_ID");
				IData custData = UcaInfoQry.qryCustomerInfoByCustId(custId);
				if (IDataUtil.isNotEmpty(custData)) {
					userInfoMap.put("USER_NAME", custData.get("CUST_NAME"));
				}
				userInfoMap.put("SERIAL_NUMBER", serialNumber);
				userInfos.add(userInfoMap);
				resultInfo.put("USER_INFO", userInfos);
				return resultInfo;
			}
			
			if ("0".equals(userStateCodeSet)) { // 号码正常
				resultInfo.put("BIZ_CODE", "0000");
				userInfoMap.put("IS_USE", "1");
				String custId = userData.getString("CUST_ID");
				IData custData = UcaInfoQry.qryCustomerInfoByCustId(custId);
				if (IDataUtil.isNotEmpty(custData)) {
					userInfoMap.put("USER_NAME", custData.get("CUST_NAME"));
				}
				userInfos.add(userInfoMap);
				resultInfo.put("USER_INFO", userInfos);
			} else {// 号码不正常
				resultInfo.put("BIZ_CODE", "0000");
				String bizDesc = getIBossUserStateDesc(userStateCodeSet);
				if (null == bizDesc || "".equals(bizDesc)) {
					bizDesc = "该号码异常!";
				}
				resultInfo.put("BIZ_DESC", bizDesc);
				userInfoMap.put("IS_USE", "0");
				userInfoMap.put("UN_USER", bizDesc);
				userInfoMap.put("SERIAL_NUMBER", serialNumber);
				userInfos.add(userInfoMap);
				resultInfo.put("USER_INFO", userInfos);
			}
			
				
		} catch (Exception e) {
			logger.info(e.getMessage());
			resultInfo.put("OPR_SEQ", oprSeq);
			resultInfo.put("BIZ_CODE", "0001");
			resultInfo.put("BIZ_DESC", "通讯异常！");
		}
		return resultInfo;
	}

	/**
	 * 合账代付业务请求 此方法主要用来接收CMIOT合账代付业务请求
	 * 
	 * @param input
	 * @author ouym3 2019.2.18
	 * @throws Exception
	 */
	public IData unifyPaymentReq(IData input) {
		IData resultInfo = new DataMap();// 返回结果
		IDataset paymentDBInfoList = new DatasetList();// 入表数据
		IData paymentDBInfo;// 单条记录
		String channel = input.getString("CHANNEL", ""); // 操作来源
		String bizType = input.getString("BIZ_TYPE", "");// 业务类型代码
		String oprSeq = input.getString("OPR_SEQ", "");// 发起方操作的流水
		String custType = input.getString("CUST_TYPE", "");// 客户类型
		String oprType = input.getString("OPR_TYPE", "");// 操作类型，01-新增 02-变更
															// 03-取消
															// OprType=01、02时需要调用反馈接口进行二次返回；OprType=03直接返回处理结果，不做二次返回
		String provinceId = input.getString("PROVINCE_ID");// 客户归属省代码
		String regionId = input.getString("REGION_ID");// 归属区域标识
		String custName = input.getString("CUST_NAME");// 客户名称
		String idType = input.getString("ID_TYPE");// 证件类型
		String idNum = input.getString("ID_NUM");// 证件号码
		try {
			IDataUtil.chkParam(input, "CHANNEL");
			IDataUtil.chkParam(input, "BIZ_TYPE");
			IDataUtil.chkParam(input, "OPR_SEQ");
			IDataUtil.chkParam(input, "CUST_TYPE");
			IDataUtil.chkParam(input, "OPR_TYPE");
			IDataUtil.chkParam(input, "PROVINCE_ID");
			IDataUtil.chkParam(input, "REGION_ID");
			IDataUtil.chkParam(input, "CUST_NAME");
			IDataUtil.chkParam(input, "ID_TYPE");
			IDataUtil.chkParam(input, "ID_NUM");
			List<IData> paymentInfoList = (List<IData>) input.get("PAYMENT_INFO");// 代付信息列表
			for (IData paymentInfo : paymentInfoList) {
				String payId = paymentInfo.getString("PAY_ID");// 代付标识
				String payNumber = paymentInfo.getString("PAY_NUMBER");// 代付号码
				String oldPayNumber = paymentInfo.getString("OLD_PAY_NUMBER");// 原代付号码
				String payeeNumber = paymentInfo.getString("PAYEE_NUMBER");// 被代付号码
				String effectTime = paymentInfo.getString("EFFECT_TIME");// 生效时间
				IDataUtil.chkParam(paymentInfo, "PAY_ID");
				IDataUtil.chkParam(paymentInfo, "PAY_NUMBER");
				IDataUtil.chkParam(paymentInfo, "PAYEE_NUMBER");
				IDataUtil.chkParam(paymentInfo, "EFFECT_TIME");
				paymentDBInfo = new DataMap();
				paymentDBInfo.put("PAY_ID", payId);
				paymentDBInfo.put("PAY_NUMBER", payNumber);
				paymentDBInfo.put("PAYEE_NUMBER", payeeNumber);
				paymentDBInfo.put("OLD_PAY_NUMBER", oldPayNumber);
				paymentDBInfo.put("EFFECT_TIME", effectTime);
				paymentDBInfo.put("ID_TYPE", idType);
				paymentDBInfo.put("ID_NUM", idNum);
				paymentDBInfo.put("CUST_NAME", custName);
				paymentDBInfo.put("PROVINCE_ID", provinceId);
				paymentDBInfo.put("REGION_ID", regionId);
				paymentDBInfo.put("CUST_TYPE", custType);
				paymentDBInfo.put("OPR_SEQ", oprSeq);
				paymentDBInfo.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
				paymentDBInfo.put("CHANNEL", channel);
				paymentDBInfo.put("BIZ_TYPE", bizType);
				paymentDBInfo.put("UPDATE_OPR_SEQ", oprSeq);// 更新操作流水
				paymentDBInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
				IData condParam = new DataMap();
				if ("01".equals(oprType)) { // 新增
					condParam.put("PAY_NUMBER", payNumber);
					condParam.put("PAYEE_NUMBER", payeeNumber);
					condParam.put("STATE", "0");
					if (IDataUtil.isNotEmpty(qryCmiotRecords(condParam))) { // 如果已经提交了新增的申请
						resultInfo.put("OPR_SEQ", oprSeq);
						resultInfo.put("BIZ_CODE", "0001");
						resultInfo.put("BIZ_DESC", "号码" + payNumber + "与代付号码" + payeeNumber + "已经提交代付合账请求，不能重复提交！");
						return resultInfo;
					}
					condParam.put("STATE", "1");
					if (IDataUtil.isNotEmpty(qryCmiotRecords(condParam))) { // 如果已经绑定
						resultInfo.put("OPR_SEQ", oprSeq);
						resultInfo.put("BIZ_CODE", "0001");
						resultInfo.put("BIZ_DESC", "号码" + payNumber + "已与被号码" + payeeNumber + "绑定！");
						return resultInfo;
					}
					paymentDBInfo.put("STATE", "0");// '0-初始值;1-正常;2-失效;3-暂停';
					paymentDBInfoList.add(paymentDBInfo);
				} else if ("02".equals(oprType)) { // 变更
					if(StringUtils.isEmpty(oldPayNumber)){
						resultInfo.put("OPR_SEQ", oprSeq);
						resultInfo.put("BIZ_CODE", "0001");
						resultInfo.put("BIZ_DESC", "代付关系变更, OLD_PAY_NUMBER 字段不能为空！");
						return resultInfo;
					}
					condParam.put("PAY_ID", payId);
					condParam.put("STATE", "1");
					condParam.put("PAY_NUMBER", payNumber);
					condParam.put("PAYEE_NUMBER", oldPayNumber);
					if (IDataUtil.isEmpty(qryCmiotRecords(condParam))) { // 如果该号码没有正常的绑定关系
						resultInfo.put("OPR_SEQ", oprSeq);
						resultInfo.put("BIZ_CODE", "0001");
						resultInfo.put("BIZ_DESC", "代付号码" + payNumber + "与被代付号码  "+oldPayNumber+" 无正常代付关系，无法变更！");
						return resultInfo;
					} else {
						condParam.put("STATE", "0");
						if (IDataUtil.isNotEmpty(qryCmiotRecords(condParam))) {
							resultInfo.put("OPR_SEQ", oprSeq);
							resultInfo.put("BIZ_CODE", "0001");
							resultInfo.put("BIZ_DESC", "代付号码" + payNumber + "与被代付号码" + payeeNumber + "已经提交代付合账变更请求，无法重复提交！");
							return resultInfo;
						}
						paymentDBInfo.put("STATE", "0");
						paymentDBInfoList.add(paymentDBInfo);
					}
				} else if ("03".equals(oprType)) { // 取消直接修改数据
					condParam.clear();
					condParam.put("PAY_ID", payId);
					StringBuilder sb = new StringBuilder("SELECT * FROM TF_A_CMIOT_PAYRELATION WHERE PAY_ID=:PAY_ID AND (STATE='3' OR STATE='1')");
					IDataset dataList = Dao.qryBySql(sb, condParam, Route.CONN_CRM_CEN);
					if (IDataUtil.isEmpty(dataList)) {
						resultInfo.put("OPR_SEQ", oprSeq);
						resultInfo.put("BIZ_CODE", "0001");
						resultInfo.put("BIZ_DESC", "代付号码" + payNumber + "与被代付号码" + payeeNumber + "无正常代付关系,无法取消!");
						return resultInfo;
					}
					IData paramData = dataList.getData(0);
					paramData.put("EFFECT_TIME", SysDateMgr.getSysTime());
					paramData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					paramData.put("UPDATE_OPR_SEQ", oprSeq);
					paramData.put("STATE", "2");// '0-初始值;1-正常;2-失效;3-暂停';
					Dao.update("TF_A_CMIOT_PAYRELATION", paramData, new String[] { "PAY_ID", "PAY_NUMBER", "PAYEE_NUMBER" }, Route.CONN_CRM_CEN);
				} else {
					resultInfo.put("OPR_SEQ", oprSeq);
					resultInfo.put("BIZ_CODE", "0001");
					resultInfo.put("BIZ_DESC", "请求数据无效！");
					return resultInfo;
				}
			}
			Dao.inserts("TF_A_CMIOT_PAYRELATION", paymentDBInfoList);
		} catch (Exception e) {
			logger.info(e.getMessage());
			resultInfo.put("OPR_SEQ", oprSeq);
			resultInfo.put("BIZ_CODE", "0001");
			resultInfo.put("BIZ_DESC", "通讯异常！");
			return resultInfo;
		}
		resultInfo.put("OPR_SEQ", oprSeq);
		resultInfo.put("BIZ_CODE", "0000");
		return resultInfo;
	}

	/**
	 * 根据条件查询合账代付记录
	 */
	public IDataset qryCmiotRecords(IData qryParam) throws Exception {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT * from TF_A_CMIOT_PAYRELATION where 1 = 1");
		Iterator<String> it = qryParam.keySet().iterator();
		while (it.hasNext()) {
			String keyStr = it.next();
			sb.append(" AND ");
			sb.append(keyStr + " = :" + keyStr);
		}
		return Dao.qryBySql(sb, qryParam, Route.CONN_CRM_CEN);
	}

	/**
	 * 合账代付业务开通异步处理 此方法是为了通知CMIOT代付合账业务开通情况,由AEE定时调度
	 */
	public IData unifyPaymentResponse(IData inParam) throws Exception {
		IData param = new DataMap();
		param.put("STATE", "0");
		IDataset pendTaskList = Dao.qryByCode("TF_A_CMIOT_PAYRELATION", "SEL_CMIOT_PAYRELATION_BY_CONDITION", param , Route.CONN_CRM_CEN);
		for (int i = 0; i < pendTaskList.size(); i++) {
			IData pendTaskData = pendTaskList.getData(i);
			IData resultData = new DataMap();// 反馈信息
			resultData.put("CHANNEL", pendTaskData.getString("CHANNEL", ""));
			resultData.put("BIZ_TYPE", pendTaskData.getString("BIZ_TYPE", ""));
			resultData.put("OPR_SEQ", "731" + "000" + SysDateMgr.getSysDateYYYYMMDDHHMMSS() + (int) ((Math.random() * 9 + 1) * 100000));
			resultData.put("REQ_OPR_SEQ", pendTaskData.getString("OPR_SEQ", ""));
			resultData.put("KIND_ID", "UnifyPaymentResult_bizOrder_0_0");
			String payId = pendTaskData.getString("PAY_ID");
			IData validateInfo = validateNotifyCmiotData(pendTaskData);// 校验请求数据
			String resultCode = validateInfo.getString("RESULT_CODE");
			if ("1".equals(resultCode)) {// 如果校验失败
				// 删除该数据
				IDataset resultInfoList = new DatasetList();
				IData resultInfo = new DataMap();
				resultInfo.put("PAY_ID", payId);
				resultInfo.put("PAYMENT_RESULT", "0"); // 失败
				resultInfo.put("FAIL_REASON", validateInfo.get("RESULT_DESC"));
				resultInfoList.add(resultInfo);
				resultData.put("RESULT_INFO", resultInfoList);
				Dao.delete("TF_A_CMIOT_PAYRELATION", pendTaskData, new String[] { "PAY_ID", "STATE" }, Route.CONN_CRM_CEN);
				// 通知CMIOT
				IDataset res = IBossCall.dealInvokeUrl("UnifyPaymentResult_bizOrder_0_0", "IBOSS6", resultData);
				continue;
			}
			IData qryParam = new DataMap();
			qryParam.put("PAY_ID", payId);
			qryParam.put("STATE", "1");
			qryParam.put("PAY_NUMBER", pendTaskData.getString("PAY_NUMBER"));
			qryParam.put("PAYEE_NUMBER", pendTaskData.getString("OLD_PAY_NUMBER"));
			IDataset normalRecordList = qryCmiotRecords(qryParam);
			if (IDataUtil.isNotEmpty(normalRecordList) && StringUtils.isNotEmpty(pendTaskData.getString("OLD_PAY_NUMBER", ""))) { // 变更操作
				// 通知CMIOT
				IDataset resultInfoList = new DatasetList();
				IData resultInfo = new DataMap();
				resultInfo.put("PAY_ID", payId);
				resultInfo.put("PAYMENT_RESULT", "1"); // 1：合账代付申请成功
				resultInfoList.add(resultInfo);
				resultData.put("RESULT_INFO", resultInfoList);
				IDataset res = IBossCall.dealInvokeUrl("UnifyPaymentResult_bizOrder_0_0", "IBOSS6", resultData);
				IData recvInfo = res.getData(0);
				String bizCode = recvInfo.getString("BIZ_CODE");
				if ("0000".equals(bizCode)) {// 成功，则变更信息
					IData updData = normalRecordList.getData(0);
					updData.put("OLD_PAY_NUMBER", pendTaskData.getString("OLD_PAY_NUMBER", ""));
					updData.put("PAYEE_NUMBER", pendTaskData.getString("PAYEE_NUMBER", ""));
					updData.put("ACCEPT_MONTH", pendTaskData.getString("ACCEPT_MONTH", ""));
					updData.put("EFFECT_TIME", SysDateMgr.getSysTime());
					updData.put("UPDATE_TIME", SysDateMgr.getSysTime());
					updData.put("UPDATE_OPR_SEQ", pendTaskData.getString("OPR_SEQ"));
					Dao.update("TF_A_CMIOT_PAYRELATION", updData, new String[] { "PAY_ID", "PAY_NUMBER", "STATE" }, Route.CONN_CRM_CEN);
				}
				// 删除申请记录
				Dao.delete("TF_A_CMIOT_PAYRELATION", pendTaskData, new String[] { "PAY_ID", "STATE" }, Route.CONN_CRM_CEN);
			} else {// 新增操作，校验通过，
				// 通知CMIOT
				IDataset resultInfoList = new DatasetList();
				IData resultInfo = new DataMap();
				resultInfo.put("PAY_ID", payId);
				resultInfo.put("PAYMENT_RESULT", "1"); // 合账代付申请成功
				resultInfoList.add(resultInfo);
				resultData.put("RESULT_INFO", resultInfoList);
				
				IDataset res = IBossCall.dealInvokeUrl("UnifyPaymentResult_bizOrder_0_0", "IBOSS6", resultData);
				IData recvInfo = res.getData(0);
				String bizCode = recvInfo.getString("BIZ_CODE");
				// 修改state状态
				IData updParam = new DataMap();
				updParam.put("PAY_ID", payId);
				StringBuilder updStr = new StringBuilder("UPDATE TF_A_CMIOT_PAYRELATION SET STATE= :STATE WHERE PAY_ID = :PAY_ID");
				if ("0000".equals(bizCode)) {
					updParam.put("STATE", "1");// 将状态改为正常
					Dao.executeUpdate(updStr, updParam, Route.CONN_CRM_CEN);
				} else {
					updParam.put("STATE", "2");// 失效
					Dao.executeUpdate(updStr, updParam, Route.CONN_CRM_CEN);
				}
			}
		}
		return null;
	}

	/**
	 * 校验合账代付业务开通数据的合法性
	 * 
	 * @param notifyCmiotData
	 * @return
	 */
	private IData validateNotifyCmiotData(IData notifyCmiotData) throws Exception {
		IData resultData = new DataMap();

		String serialNumber = notifyCmiotData.getString("PAY_NUMBER");// 代付手机号码
		String provinceId = notifyCmiotData.getString("PROVINCE_ID");// 被代付物联网省市编码
		// IDataset custData = FuzzyInfoQry.qryCustPersonBySn(serialNumber);
		// IDataset smzCustSet = SmzCustInfoQry.queryAllByUserId(strUserId);
		String route = RouteInfoQry.getEparchyCodeBySn(serialNumber);
		super.setRouteId(route);
		IDataset custData = UserInfoQry.getUserinfo(serialNumber);
		if (IDataUtil.isEmpty(custData)) {
			resultData.put("RESULT_CODE", "1");
			resultData.put("RESULT_DESC", "无客户信息，请先办理手机实名制登记业务！");
			return resultData;
		}
		String custName = custData.getData(0).getString("CUST_NAME", "").trim();// 客户姓名
		// 1.个人合账代付业务不支持跨省代付，代付和被代付用户需要在同一省
		if (!provinceId.equals("898")) { // 写死，地州编码跟接口定义传过来的地州编码不一样
			resultData.put("RESULT_CODE", "1");
			resultData.put("RESULT_DESC", "代付号码与被代付号码归属地州不同，不能做合账代付处理！");
			return resultData;
		}
		// 2.代付号码实名制规则限制
		if (custName.contains("大众卡") || custName.contains("轻松卡") || custName.contains("创业卡") || custName.contains("拜年卡")) {
			String erroInfo = "号码:" + serialNumber + "的客户姓名不符合规定，请先办理手机实名制登记业务！";
			resultData.put("RESULT_CODE", "1");
			resultData.put("RESULT_DESC", erroInfo);
			return resultData;
		}
		resultData.put("RESULT_CODE", "0");
		return resultData;
	}
	
	/**
	 * 号卡资源同步接口,该接口与为CMIOT系统的接口,具体接口规范参见《中国移动物联网集中化支撑系统与省级支撑系统接口规范》-7.2.4.4
	 * @param input
	 * @author yangwh3 2019.04.03
	 * @throws Exception
	 */
	public IData indivNumCardInfoSyn(IData input) throws Exception{
		IData result = new DataMap();
		IDataUtil.chkParam(input, "OPERATOR_ID");
		IDataUtil.chkParam(input, "MSISDN");
		IDataUtil.chkParam(input, "USER_ID");
		IDataUtil.chkParam(input, "ICCID");
		IDataUtil.chkParam(input, "ORDER_ID");
		IDataUtil.chkParam(input, "CREATE_TIME");
		IDataUtil.chkParam(input, "CUST_ID");
		IDataUtil.chkParam(input, "ACCT_ID");
		IDataUtil.chkParam(input, "USER_STATUS");
		input.put("ACCEPT_MONTH",SysDateMgr.getCurMonth());
		if(input.getString("USER_STATUS").equals("2")){//新增操作
			IDataset cmiotList = Dao.qryByCode("TF_B_CMIOT_NUMCARD", "SEL_BY_MSISDN", input, Route.CONN_CRM_CEN);
			if(cmiotList.isEmpty()){
				try{
					Dao.insert("TF_B_CMIOT_NUMCARD", input, Route.CONN_CRM_CEN);
					result.put("BIZ_CODE", "0000");
					result.put("BIZ_DESC", "succ.");
				}catch (Exception e) {
					result.put("BIZ_CODE", "0001");
					result.put("BIZ_DESC", "数据库操作异常,号卡资源同步失败");
				}
			}else{
				result.put("BIZ_CODE", "0001");
				result.put("BIZ_DESC", "数据库中已存在MSISDN="+input.getString("MSISDN"));
			}
		}else if(input.getString("USER_STATUS").equals("9")){//删除操作
			IDataset cmiotList = Dao.qryByCode("TF_B_CMIOT_NUMCARD", "SEL_BY_MSISDN_USERID", input, Route.CONN_CRM_CEN);
			if(cmiotList.isEmpty()){
				try {
					input.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
					if(!input.containsKey("LAST_TIME")){
						input.put("LAST_TIME", input.getString("UPDATE_TIME"));
					}
					int count = Dao.executeUpdateByCodeCode("TF_B_CMIOT_NUMCARD", "UPD_BY_MSISDN_USERID", input, Route.CONN_CRM_CEN);
					if(count == 0){
						result.put("BIZ_CODE", "0001");
						result.put("BIZ_DESC", "数据库中无USER_ID="+input.getString("USER_ID")+",MSISDN="+input.getString("MSISDN")+"信息，删除失败");
					}else{
						result.put("BIZ_CODE", "0000");
						result.put("BIZ_DESC", "succ.");
					}
				} catch (Exception e) {
					result.put("BIZ_CODE", "0001");
					result.put("BIZ_DESC", "数据库操作异常,号卡资源同步失败");
				}
			}else{
				result.put("BIZ_CODE", "0001");
				result.put("BIZ_DESC", "数据库中已存在USER_ID="+input.getString("USER_ID")+",MSISDN="+input.getString("MSISDN")+"的删除记录");
			}
		}else{
			result.put("BIZ_CODE", "0001");
			result.put("BIZ_DESC", "未知操作编码");
		}
		result.put("ORDER_ID", input.getString("ORDER_ID"));
		return result;
	}
	
}
