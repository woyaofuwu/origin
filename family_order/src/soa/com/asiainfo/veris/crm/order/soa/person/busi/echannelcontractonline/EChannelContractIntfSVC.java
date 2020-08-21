package com.asiainfo.veris.crm.order.soa.person.busi.echannelcontractonline;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.dbconn.DBConnection;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SaleDepositInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo;

public class EChannelContractIntfSVC extends CSBizService {
	static Logger logger = Logger.getLogger(EChannelContractIntfSVC.class); 
	/**
	 * 能力开放平台总接口
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 1、办理资格校验接口
	 * 
	 * @param pd
	 * @param td
	 * @return
	 * @throws Exception
	 */
	public IData checkBussinessRule(IData input) throws Exception 
	{
		chkParamNoStr(input, "SERIAL_NUMBER", "2001"); // 手机号码
		chkParamNoStr(input, "BOSS_ID", "2001"); //合约档信息ID
		chkParamNoStr(input, "CHECK_TYPE", "2001"); // 校验类型
		
		String number = input.getString("SERIAL_NUMBER", ""); 
		
		EChannelContractIntfBean checkBean = BeanManager.createBean(EChannelContractIntfBean.class);
		IData retnData = checkBean.checkCustomerDoService(input);
		retnData.put("SERIAL_NUMBER", number);
		retnData.put("OPRTIME", DateFormatUtils.format(new  Date(), "yyyyMMddHHmmss"));
		return retnData;
	}

	/**
	 * 2、合约办理接口
	 * 省BOSS完成合约办理，套餐变更。返回合约办理的工单流水号
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData orderContractInfo(IData input) throws Exception 
	{

		chkParamNoStr(input, "SERIAL_NUMBER", "1001"); //手机号码
		chkParamNoStr(input, "BOSS_ID", "1001"); //合约档信息ID
		chkParamNoStr(input, "ORDER_ID", "1001"); //商城订单ID
		saveListOrderInfo(input);
	
		IData retnData = new DataMap();
		retnData.put("X_RESULTCODE", "1003");
		retnData.put("X_RESULTINFO", "尊敬的客户，您好！您的业务办理失败1");
		retnData.put("CP_RESULTCODE", "1003");
		retnData.put("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败1");
		retnData.put("CP_MESSAGE", "尊敬的客户，您好！您的产品办理失败1");
		retnData.put("SA_RESULTCODE", "1003");
		retnData.put("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败1");
		retnData.put("SA_MESSAGE", "尊敬的客户，您好！您的合约办理失败1");
		input.putAll(retnData);
		logger.error("=================开始处理订单==============");
		retnData = dealListOrderInfo(input);
		logger.error("=================结束处理订单==============");
		return retnData;
	}

	/**
	 * 保存销售订单信息
	 * 
	 * @param pd
	 * @param data
	 * @param conn
	 * @throws Exception
	 */
	public void saveListOrderInfo(IData data) throws Exception 
	{
		// 独立事务用来保存接收到的订单信息，以免在登记订单的过程异常而导致订单信息丢失
		DBConnection conn = null;
		try 
		{
			conn = SessionManager.getInstance().getAsyncConnection("cen1");
			EChannelContractIntfBean bean = BeanManager.createBean(EChannelContractIntfBean.class);

			String strSn = data.getString("SERIAL_NUMBER");
			String strOrderID = data.getString("ORDER_ID");
			String strBossID = data.getString("BOSS_ID");
			
			//chkParamNoStr(data, "ORDER_ID", "802127"); // 订单编码
			data.put("CHANNEL_ID", CSBizBean.getVisit().getInModeCode());
			data.put("BILL_DAY", SysDateMgr.getSysDateYYYYMMDD());
			data.put("PAY_MENT", "0");
			data.put("CREATE_TIME", SysDateMgr.getSysTime());
			data.put("PAY_TIME", SysDateMgr.getSysTime());
			
			data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			data.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			data.put("TID", strOrderID);
			data.put("UPDATE_TIME", SysDateMgr.getSysTime());
			data.put("REMARK", "一级自有电渠合约订单");
			bean.insertIntoCtrmTList(conn, data);
			
			String eparchyCode = getMofficeBySN(strSn);
			// 订单中子订单信息信息

			IData orderList = new DataMap();
			orderList.put("TID", strOrderID);// 订单ID
			orderList.put("OID", strOrderID);// 子订单ID
			orderList.put("OUT_OID", strOrderID);// 合作渠道子订单编码
			orderList.put("GOODS_ID", strBossID);// 商品编码
			orderList.put("TITLE", "4G自选套餐");//商品标题
			orderList.put("NUM", "1");// 购买数量
			orderList.put("PRICE", "0");// 价格
			orderList.put("PROVINCE", "898");// 商品所在地省份
			orderList.put("CITY", "898");// 商品所在地城市
			orderList.put("TOTAL", "0");// 该子订单总金额
			orderList.put("ADJEST_FEE", "0");// 手工调整金额
			orderList.put("MOBILE_NO", "1");//业务号码类型 1-手机号；2-宽带号码
			orderList.put("PHONE", strSn);// 订单状态
			orderList.put("ORDER_STATUS", "7");

			orderList.put("ACCEPT_DATE", SysDateMgr.getSysTime());
			orderList.put("UPDATE_TIME", SysDateMgr.getSysTime());
			orderList.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
			orderList.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
			orderList.put("REMARK", "一级自有电渠合约订单");
			bean.insertIntoCtrmOrder(conn, orderList);
			
			/* 查询产品对应关系表信息 */
			IData inparam = new DataMap();
			inparam.put("EPARCHY_CODE", eparchyCode);
			inparam.put("CTRM_PRODUCT_ID", strBossID);
			IDataset relaProductIds = QueryListInfo.queryListInfoForRelation(inparam);
			if (IDataUtil.isEmpty(relaProductIds)) 
			{
				String errors = "订单产品ID【"+ strBossID + "】不存在本地产品的映射关系，请管理员进行配置！";
				CSAppException.appError("808292", errors);
			} 
			
			for (int k = 0; k < relaProductIds.size(); k++) 
			{
				IData relaMap = relaProductIds.getData(k);
				String strCPT = relaMap.getString("CTRM_PRODUCT_TYPE");
				// 能力开放平台产品编码类型,1-终端产品，2-合约产品，3-套餐及增值产品
				if ("1".equals(strCPT)) 
				{
					IData productInfo = new DataMap();
					productInfo.put("TID", strOrderID);
					productInfo.put("OID", strOrderID);
					productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
					productInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					productInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					productInfo.put("PHONE", strSn);
					productInfo.put("CTRM_PRODUCT_ID", strBossID);
					productInfo.put("STATUS", "8");
					//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
					
					productInfo.put("PID", SeqMgr.getCtrmProId());
					productInfo.put("CONTRACT_ID", relaMap.getString("CONTRACT_ID", "-1"));
					productInfo.put("PRODUCT_ID", relaMap.getString("PRODUCT_ID", "-1"));
					productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
					productInfo.put("ELEMENT_ID", relaMap.getString("ELEMENT_ID", "-1"));
					productInfo.put("ELEMENT_TYPE_CODE", relaMap.getString("ELEMENT_TYPE_CODE"));
					productInfo.put("CTRM_PRODUCT_TYPE", relaMap.getString("CTRM_PRODUCT_TYPE"));
					productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
					productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
					productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
					productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
					productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
					bean.insertIntoCtrmOrderProduct(conn, productInfo);
					// 终端产品默认为已处理完													
				}
				else if("2".equals(strCPT))
				{
					String checkProId = relaMap.getString("CHECK_ELEMENT_ID"); // 需要验证的产品id
					String checkProType = relaMap.getString("CHECK_ELEMENT_TYPE"); // 需要验证的产品类型
					if (StringUtils.isNotBlank(checkProId) && StringUtils.isNotBlank(checkProType)) 
					{
						String[] strs = checkProId.split("\\|");
						for (int i = 0; i < strs.length; i++) 
						{
							if (strs[i].startsWith("wtc")) 
							{
								IDataset elements = bean.builderElements(strSn, strs[i]);
								if(IDataUtil.isNotEmpty(elements))
								{
									for (int j = 0; j < elements.size(); j++) 
									{
										IData element = elements.getData(j);
										
										IData productInfo = new DataMap();
										productInfo.put("TID", strOrderID);
										productInfo.put("OID", strOrderID);
										productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
										productInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
										productInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
										productInfo.put("PHONE", strSn);
										productInfo.put("CTRM_PRODUCT_ID", strs[i]);
										productInfo.put("STATUS", "7");
										//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
										
										productInfo.put("PID", SeqMgr.getCtrmProId());
										productInfo.put("CONTRACT_ID", "-1");
										productInfo.put("PRODUCT_ID", element.getString("PRODUCT_ID", "-1"));
										productInfo.put("PACKAGE_ID", element.getString("PACKAGE_ID", "-1"));
										productInfo.put("ELEMENT_ID", element.getString("ELEMENT_ID", "-1"));
										productInfo.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
										productInfo.put("CTRM_PRODUCT_TYPE", "3");
										productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
										productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
										productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
										productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
										productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
										bean.insertIntoCtrmOrderProduct(conn, productInfo);
									}
								}
							} 
							else 
							{
								IData productInfo = new DataMap();
								productInfo.put("TID", strOrderID);
								productInfo.put("OID", strOrderID);
								productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
								productInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
								productInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
								productInfo.put("PHONE", strSn);
								productInfo.put("CTRM_PRODUCT_ID", strBossID);
								productInfo.put("STATUS", "7");
								//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
								
								productInfo.put("PID", SeqMgr.getCtrmProId());
								productInfo.put("CONTRACT_ID", "-1");
								
								//input.put("MODIFY_TAG", "0");
								if ("P".equalsIgnoreCase(checkProType)) 
								{
									productInfo.put("PRODUCT_ID", strs[i]);
								}
								else 
								{
									productInfo.put("ELEMENT_ID", strs[i]);
								}
								productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
								productInfo.put("ELEMENT_TYPE_CODE", checkProType);
								
								productInfo.put("CTRM_PRODUCT_TYPE", "3");
								productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
								productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
								productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
								productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
								productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
								bean.insertIntoCtrmOrderProduct(conn, productInfo);

							}
						}
					}
					
					IData productInfo = new DataMap();
					productInfo.put("TID", strOrderID);
					productInfo.put("OID", strOrderID);
					productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
					productInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					productInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					productInfo.put("PHONE", strSn);
					productInfo.put("CTRM_PRODUCT_ID", strBossID);
					productInfo.put("STATUS", "7");
					//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
					
					productInfo.put("PID", SeqMgr.getCtrmProId());
					productInfo.put("CONTRACT_ID", relaMap.getString("CONTRACT_ID", "-1"));
					productInfo.put("PRODUCT_ID", relaMap.getString("PRODUCT_ID", "-1"));
					productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
					productInfo.put("ELEMENT_ID", relaMap.getString("ELEMENT_ID", "-1"));
					productInfo.put("ELEMENT_TYPE_CODE", relaMap.getString("ELEMENT_TYPE_CODE"));
					productInfo.put("CTRM_PRODUCT_TYPE", relaMap.getString("CTRM_PRODUCT_TYPE"));
					productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
					productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
					productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
					productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
					productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
					bean.insertIntoCtrmOrderProduct(conn, productInfo);
					
				}
				else if("3".equals(strCPT))
				{
					IData productInfo = new DataMap();
					productInfo.put("TID", strOrderID);
					productInfo.put("OID", strOrderID);
					productInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
					productInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
					productInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
					productInfo.put("PHONE", strSn);
					productInfo.put("CTRM_PRODUCT_ID", strBossID);
					productInfo.put("STATUS", "7");
					//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
					
					productInfo.put("PID", SeqMgr.getCtrmProId());
					productInfo.put("CONTRACT_ID", relaMap.getString("CONTRACT_ID", "-1"));
					productInfo.put("PRODUCT_ID", relaMap.getString("PRODUCT_ID", "-1"));
					productInfo.put("PACKAGE_ID", relaMap.getString("PACKAGE_ID", "-1"));
					productInfo.put("ELEMENT_ID", relaMap.getString("ELEMENT_ID", "-1"));
					productInfo.put("ELEMENT_TYPE_CODE", relaMap.getString("ELEMENT_TYPE_CODE"));
					productInfo.put("CTRM_PRODUCT_TYPE", relaMap.getString("CTRM_PRODUCT_TYPE"));
					productInfo.put("RSRV_STR1", relaMap.getString("RSRV_STR1"));
					productInfo.put("RSRV_STR2", relaMap.getString("RSRV_STR2"));
					productInfo.put("RSRV_STR3", relaMap.getString("RSRV_STR3"));
					productInfo.put("RSRV_STR4", relaMap.getString("RSRV_STR4"));
					productInfo.put("RSRV_STR5", relaMap.getString("RSRV_STR5"));
					bean.insertIntoCtrmOrderProduct(conn, productInfo);
				}
				
			}
			conn.commit();
		}
		catch (Exception e1) 
		{
			if(logger.isInfoEnabled()) logger.info(e1);
			if(conn != null)
			{
				conn.rollback();
			}			
			CSAppException.appError("1002", e1.getMessage());
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
	 * 处理订单登记
	 * 
	 * @param pd
	 * @param data
	 * @param tidList
	 * @throws Exception
	 */
	public IData dealListOrderInfo(IData data) throws Exception 
	{
		EChannelContractIntfBean bean = BeanManager.createBean(EChannelContractIntfBean.class);
		String strBossID = data.getString("BOSS_ID");
		String tid = data.getString("ORDER_ID");
		String strSn = data.getString("SERIAL_NUMBER");
		
		data.put("PRE_TYPE", "");
		
		IData param = new DataMap();
		param.put("TID", tid);
		param.put("OID", tid);
		param.put("STATUS", "7");//一级自有电渠合约订单状态，7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
		IDataset subOrderList = QueryListInfo.queryListInfoForTidTlist(param);
		if (IDataUtil.isEmpty(subOrderList)) 
		{
			String errors = "订单产品ID【"+ tid + "】 TF_B_CTRM_ORDER_PRODUCT表数据查询为空！";
			CSAppException.appError("1004", errors);
		}
		
		IDataset productParams = new DatasetList();
		IData idOutput = new DataMap();
		
		IData product = new DataMap();
    	IDataset eleIdList = new DatasetList();
    	boolean bCPflag = false;
    	boolean bSAflag = false;
    	
    	String eparchyCode = getMofficeBySN(strSn);
		/* 查询产品对应关系表信息 */
		IData inparam = new DataMap();
		inparam.put("EPARCHY_CODE", eparchyCode);
		inparam.put("CTRM_PRODUCT_ID", strBossID);
		IDataset relaProductIds = QueryListInfo.queryListInfoForRelation(inparam);
    	if(IDataUtil.isNotEmpty(relaProductIds))
    	{
    		for (int i = 0; i < relaProductIds.size(); i++) 
    		{
    			product = relaProductIds.getData(i);
    			IData eleInfo = new DataMap();
    			if("1".equals(product.getString("CTRM_PRODUCT_TYPE")))
    			{
					continue; //终端产品
				}
    			else if ("2".equals(product.getString("CTRM_PRODUCT_TYPE"))) 
				{
    				bSAflag = true;
    				idOutput = bean.executeContract(product, data, eparchyCode, strSn);
    				for (int j = 0; j < subOrderList.size(); j++)
    				{
    					IData subInfo = subOrderList.getData(j);
    					String strCPT3 = subInfo.getString("CTRM_PRODUCT_TYPE");
    					String strPID = subInfo.getString("PID");
    					if ("3".equals(strCPT3))
    					{
    						String strTI = idOutput.getString("CP_TRADE_ID", "-1");
    						String strState = "9";
    						if(!"-1".equals(strTI))
    						{
    							strState = "8";
    						}
    						String msg = idOutput.getString("CP_RESULTINFO", "尊敬的客户，您好！您的产品办理失败8");
    						msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
    						IData inputData = new DataMap();
    						inputData.put("PID", strPID);
    						inputData.put("TRADE_ID", strTI);
    						inputData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
    						inputData.put("STATUS", strState);
    						inputData.put("ACCEPT_RESULT", strState);
    						inputData.put("REMARK", msg);
    						inputData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    						inputData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    						inputData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    						productParams.add(inputData);
    					}
    					else if("2".equals(strCPT3))
    					{
    						String strTI = idOutput.getString("SA_TRADE_ID", "-1");
    						String strState = "9";
    						if(!"-1".equals(strTI))
    						{
    							strState = "8";
    						}
    						String msg = idOutput.getString("SA_RESULTINFO", "尊敬的客户，您好！您的合约办理失败8");
    						msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
    						IData inputData = new DataMap();
    						inputData.put("PID", strPID);
    						inputData.put("TRADE_ID", strTI);
    						inputData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
    						inputData.put("STATUS", strState);
    						inputData.put("ACCEPT_RESULT", strState);
    						inputData.put("REMARK", msg);
    						inputData.put("UPDATE_TIME", SysDateMgr.getSysTime());
    						inputData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
    						inputData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
    						productParams.add(inputData);
    					}
    				}
				}
				else if ("3".equals(product.getString("CTRM_PRODUCT_TYPE"))) 
				{
					eleInfo.put("ELEMENT_ID", product.getString("ELEMENT_ID"));
					eleInfo.put("MODIFY_TAG", "0");
					if ("P".equals(product.getString("ELEMENT_TYPE_CODE")))
					{
						eleInfo.put("ELEMENT_ID", product.getString("PRODUCT_ID"));	
						IData userProInfo = UcaInfoQry.qryUserMainProdInfoBySn(strSn);
						if (IDataUtil.isNotEmpty(userProInfo) && 
							product.getString("PRODUCT_ID","").equals(userProInfo.getString("PRODUCT_ID")) && 
							"10004445".equals(userProInfo.getString("PRODUCT_ID"))) 
						{
							bCPflag = true;
						}
					}
					eleInfo.put("ELEMENT_TYPE_CODE", product.getString("ELEMENT_TYPE_CODE"));
					eleIdList.add(eleInfo);
				}
			}
    		if(bCPflag && !bSAflag)
    		{
    			//重新做下元素处理
  				IDataset elements = bean.builderElements(strSn, strBossID);
  				eleIdList.clear();
  				eleIdList.addAll(elements);
    		}
    		if (IDataUtil.isNotEmpty(eleIdList)) 
    		{
    			idOutput = bean.executeProduct(eleIdList, data, eparchyCode, strSn);
    			for (int j = 0; j < subOrderList.size(); j++)
				{
					IData subInfo = subOrderList.getData(j);
					String strCPT3 = subInfo.getString("CTRM_PRODUCT_TYPE");
					String strPID = subInfo.getString("PID");
					if ("3".equals(strCPT3))
					{
						String strTI = idOutput.getString("CP_TRADE_ID", "-1");
						String strState = "9";
						if(!"-1".equals(strTI))
						{
							strState = "8";
						}
						String msg = idOutput.getString("CP_MESSAGE", "尊敬的客户，您好！您的产品办理失败88");
						msg = msg.length() > 200 ? msg.substring(0, 200) : msg;
						IData inputData = new DataMap();
						inputData.put("PID", strPID);
						inputData.put("TRADE_ID", strTI);
						inputData.put("ACCEPT_DATE", SysDateMgr.getSysTime());
						inputData.put("STATUS", strState);
						inputData.put("ACCEPT_RESULT", strState);
						inputData.put("REMARK", msg);
						inputData.put("UPDATE_TIME", SysDateMgr.getSysTime());
						inputData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
						inputData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
						productParams.add(inputData);
					}
				}
			}       		
    	}
    	else
    	{
    		idOutput.put("X_RESULTCODE", "1012");
    		idOutput.put("X_RESULTINFO", "商品或者产品的映射关系没有配置.请联系管理员!");
    	}
		if (IDataUtil.isNotEmpty(productParams))
		{
			bean.updateBatchInfo("TF_B_CTRM_TLIST","UPD_CTRM_ORDER_PRODUCT", productParams);	
		}
		
		return idOutput;
	}


	/**
	 * 退款订单同步接口
	 * 
	 * @param pd
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IData refundContractOrder(IData data) throws Exception 
	{
		chkParamNoStr(data, "ORDER_ID", "808291");
		chkParamNoStr(data, "SERIAL_NUMBER", "808291");
		
		EChannelContractIntfBean bean = BeanManager.createBean(EChannelContractIntfBean.class);
		IData retnData = bean.refundOrderSynchro(data);
		return retnData;
	}

	/** *********************************************************************************************** */
	/**
	 * 校验传入在是否为空
	 * 
	 * @param data
	 * @param keys
	 * @throws Exception
	 */
	public void chkParamNoStr(IData data, String keys, String errorCode) throws Exception 
	{
		String key = data.getString(keys, "");
		if ("".equals(key)) 
		{
			CSAppException.appError(errorCode, "传入在字段" + keys + "值不能为空！");
		}
	}

	/**
	 * 通过关键字获取Str 特别解析 [["", ""]]的字符串到 IDataset
	 * 
	 * @param key
	 * @param data
	 * @return
	 * @author hud
	 */
	public static IDataset getIDatasetSpecl(String key, String str) throws Exception 
	{
		IDataset dataset = new DatasetList();
		str = str.replace(",", "rex");
		if (str.startsWith("[") && str.endsWith("]")) 
		{
			str = str.replaceAll("\"", "");
			String[] param = str.substring(2, str.length() - 2).split("rex");
			for (int i = 0; i < param.length; i++) 
			{
				IData data = new DataMap();
				data.put(key, param[i]);
				dataset.add(data);
				data = null;
			}
		} 
		else 
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_648);
		}
		return dataset;
	}

	public String getMofficeBySN(String serialNumber) throws Exception 
	{
		IData data = new DataMap();
		data.put("SERIAL_NUMBER", serialNumber);

		IDataset tmp = UserInfoQry.getMofficeBySN(data);
		if (IDataUtil.isNotEmpty(tmp)) 
		{
			IData data2 = tmp.getData(0);
			return data2.getString("EPARCHY_CODE");
		} 
		else 
		{
			// 携转号码无moffice信息
			IDataset out = TradeNpQry.getValidTradeNpBySn(serialNumber);
			if (IDataUtil.isNotEmpty(out)) 
			{
				return out.getData(0).getString("AREA_CODE");
			} 
			else 
			{
				return null;
			}
		}
		
	}
	
	public IData Order4GDiscntActionIMEI(IData input) throws Exception 
	{
		chkParamNoStr(input, "SERIAL_NUMBER", "2001"); //手机号码
		chkParamNoStr(input, "IMEI_NO", "2001"); //终端IMEI信息
		chkParamNoStr(input, "ORDER_ID", "2001"); //商城订单ID
		chkParamNoStr(input, "MODIFY_TAG", "2001"); //新增或者修改
		
		IData idOutput = new DataMap();
		/*idOutput.put("X_RESULTCODE", "-1");
		idOutput.put("X_RESULTINFO", "更新失败");*/
		
		String strImeiNo = input.getString("IMEI_NO");
		String strTid = input.getString("ORDER_ID");
		String strSerialNumber = input.getString("SERIAL_NUMBER");
		String strModifyTag = input.getString("MODIFY_TAG");
		
		IData idUser = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
		if(IDataUtil.isEmpty(idUser))
		{
			idOutput.put("X_RESULTCODE", "2001");
			idOutput.put("X_RESULTINFO", "用户信息不存在，请检查");
			return idOutput;
		}
		
		String strUserID = idUser.getString("USER_ID", "");
		if(StringUtils.isBlank(strUserID))
		{
			String errors = "USER_ID不能为空！";
			idOutput.put("X_RESULTCODE", "2001");
			idOutput.put("X_RESULTINFO", errors);
			return idOutput;
		}
		
		IData param = new DataMap();
		param.put("TID", strTid);
		param.put("OID", strTid);
		param.put("STATUS", "8");//一级自有电渠合约订单状态，6为返销成功,7为等待执行,8为订购成功，9为订购失败，区分第三方电商能力平台
		IDataset idsSubOrderList = QueryListInfo.queryListInfoForTidTlist(param);
		if (IDataUtil.isEmpty(idsSubOrderList)) 
		{
			String errors = "订单产品ID【"+ strTid + "】 TF_B_CTRM_ORDER_PRODUCT表数据查询为空或者4G合约已经返销！";
			idOutput.put("X_RESULTCODE", "2001");
			idOutput.put("X_RESULTINFO", errors);
			return idOutput;
			//CSAppException.appError("2001", errors);
		}
		
		for (int i = 0; i < idsSubOrderList.size(); i++) 
		{
			IData idSubOrderList = idsSubOrderList.getData(i);
			String strCPT3 = idSubOrderList.getString("CTRM_PRODUCT_TYPE", "");//产品类型
			if("2".equals(strCPT3))//合约产品
			{
				String strProductID = idSubOrderList.getString("PRODUCT_ID", "");//
				String strPackageID = idSubOrderList.getString("PACKAGE_ID", "");
				
				//IDataset idsPackageElement = ProductPkgInfoQry.queryPackageElementByPackageId(strPackageID);
//				IDataset idsPackageElementDeposit = SaleDepositInfoQry.querySaleDepositByPackageIdEparchy(strPackageID, "0898");
				/*IDataset idsPackageElementDeposit = UPackageElementInfoQry.getElementsByElementTypeCode(strPackageID, "A");
				if(IDataUtil.isEmpty(idsPackageElementDeposit))
				{
					String errors = "PACKAGE_ID=【"+ strPackageID + "】 TD_B_PACKAGE_ELEMENT,TD_B_SALE_DEPOSIT表数据查询为空！";
					idOutput.put("X_RESULTCODE", "2001");
					idOutput.put("X_RESULTINFO", errors);
					return idOutput;
				}
				
				IData idPackageElementDeposit = idsPackageElementDeposit.first();
				String strDiscntGiftID = idPackageElementDeposit.getString("A_DISCNT_CODE", "");
				if(StringUtils.isBlank(strDiscntGiftID))
				{
					String errors = "PACKAGE_ID=【"+ strPackageID + "】对应的TD_B_SALE_DEPOSIT表，未配置A_DISCNT_CODE！";
					idOutput.put("X_RESULTCODE", "2001");
					idOutput.put("X_RESULTINFO", errors);
					return idOutput;
				}*/
				
				/*IData idAMparam = new DataMap();
				idAMparam.put("USER_ID", strUserID);
				idAMparam.put("ACTION_CODE", strDiscntGiftID);
				idAMparam.put("IMEI_NO", strImeiNo);
				IData id4GDiscntActionImei = CSAppCall.call("AM_CRM_Upd4GDiscntActionImei", idAMparam).first();
				idOutput.putAll(id4GDiscntActionImei);*/
				idOutput.put("USER_ID", strUserID);
				idOutput.put("SERIAL_NUMBER", strSerialNumber);
				idOutput.put("PRODUCT_ID", strProductID);
				idOutput.put("PACKAGE_ID", strPackageID);
				//idOutput.put("ACTION_CODE", strDiscntGiftID);//返还A元素
				idOutput.put("IMEI_NO", strImeiNo);
				
				IData inparam = new DataMap();
		        inparam.put("IMEI_NO", strImeiNo);
		        inparam.put("MODIFY_TAG", strModifyTag);
		        inparam.put("ORDER_ID", strTid);
				EChannelContractIntfBean bean = BeanManager.createBean(EChannelContractIntfBean.class);
				bean.updateCmrOrderForTID(inparam);
		        //Dao.executeUpdateByCodeCode("TF_B_CTRM_TLIST", "UPD_CTRM_ORDER", inparam);
			}
		}
		return idOutput;
	}
}
