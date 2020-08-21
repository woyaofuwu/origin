package com.asiainfo.veris.crm.order.soa.person.busi.flow.order.action.finish;

import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: ClearingPackageFlowFinishAction
 * @Description: "任我看"及"任我用”套餐流量单独结算支撑改造的通知。
 * 
 * @version: v1.0.0
 * @author: zhangbo18
 * @date: 2017-6-14 下午3:10:25
 */
public class ClearingPackageFlowFinishAction implements ITradeFinishAction
{
	Logger log = Logger.getLogger(ClearingPackageFlowFinishAction.class);

	@Override
	public void executeAction(IData mainTrade) throws Exception
	{
		try
		{
			if (!checkTradeType(mainTrade))
			{
				if (log.isDebugEnabled())
					log.debug("ClearingPackageFlowFinishAction--->该业务类型无需操作套餐流量,TRADE_TYPE_CODE:" + mainTrade.getString("TRADE_TYPE_CODE"));

				return;
			}
			IDataset tradeDiscntList = null;
			if ("110".equals(mainTrade.getString("TRADE_TYPE_CODE")) || "10".equals(mainTrade.getString("TRADE_TYPE_CODE")))
			{
				String tradeId = mainTrade.getString("TRADE_ID");
				tradeDiscntList = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId);
			} else
			{
				tradeDiscntList = getAllDiscntByUserId(mainTrade.getString("USER_ID"));
			}
			if (IDataUtil.isNotEmpty(tradeDiscntList))
			{
				for (int i = 0; i < tradeDiscntList.size(); i++)
				{
					IData discntInfo = tradeDiscntList.getData(i);
					IData ibossParam = buildCallIbossParam(mainTrade, discntInfo);

					if (IDataUtil.isNotEmpty(ibossParam))
					{


								// 调用IBOSS接口
						// 调用IBOSS接口
						IBossCall.dealInvokeUrl("BIP6B644_T6101603_0_0", "IBOSS", ibossParam);
					} else
					{
						if (log.isDebugEnabled())
							log.debug("ClearingPackageFlowFinishAction.catch--->CLEARING_PACKAGEFLOW 配置为空，调用IBOSS接口失败！");
					}
				}
			} else
			{
				if (log.isDebugEnabled())
					log.debug("ClearingPackageFlowFinishAction.catch--->服务列表为空！");
			}
		} catch (Exception e)
		{
			CSAppException.apperr(BizException.CRM_GRP_713, "调用IBOSS接口失败，失败原因：" + e.getMessage());
		}
	}
	
	/****
	 * 校验业务编码是否涉及套餐流量操作
	 * 
	 * @param mainTrade
	 * @return
	 * @throws Exception
	 */
	public boolean checkTradeType(IData mainTrade) throws Exception
	{
		boolean isCheck = false;
		if ("110".equals(mainTrade.getString("TRADE_TYPE_CODE")))
		{
			isCheck = true;
		} else
		{
			isCheck = IDataUtil.isNotEmpty(getPackageFlowTradeType(mainTrade.getString("TRADE_TYPE_CODE")));
		}
		return isCheck;
	}

	/****
	 * 获取OPER_CODE字段
	 * 
	 * @param mainTrade
	 * @return
	 * @throws Exception
	 */
	public String getOperCode(IData mainTrade, IData discntInfo) throws Exception
	{
	    String oper_code = "";
	    if ("110".equals(mainTrade.getString("TRADE_TYPE_CODE"))){
	        if ("0".equals(discntInfo.getString("MODIFY_TAG")))
	        {
	            oper_code = "06";
	        }else if ("1".equals(discntInfo.getString("MODIFY_TAG")) ||
	        		"2_U".indexOf(discntInfo.getString("MODIFY_TAG")) > -1)
	        {
                oper_code = "07";
	        }
	    }else{
	        IDataset commInfos = getPackageFlowTradeType(mainTrade.getString("TRADE_TYPE_CODE"));
	        if (IDataUtil.isNotEmpty(commInfos))
	        {
	            IData commInfo = commInfos.getData(0);
	            oper_code = commInfo.getString("PARA_CODE2");
	        }
	    }
	    return oper_code;

	}

	/***
	 * 构建调用IBOSS的参数
	 * 
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	public IData buildCallIbossParam(IData mainTrade, IData discntInfo) throws Exception
	{
		//校验是否110变更操作
		if (!isChgOper(mainTrade, discntInfo))
		{
			return null;
		}
	    IData params = new DataMap();
	    IDataset commparaInfo = getPackageFlow(mainTrade,discntInfo);
	    
        //不为空，则表示该资费为任我看、任我用流量资费
        if (IDataUtil.isNotEmpty(commparaInfo))
        {
            //PARA_CODE2 配置为相应的业务类型例如：83-数据业务任我看 84-数据业务任我用
            //PARA_CODE3 配置为PACKAGE_CODE (套餐类型) 如：任我看8元套餐填写0000800
            IData commData = commparaInfo.getData(0);
            String biz_type = commData.getString("PARA_CODE2");
            params.put("OPR_SOURCE", biz_type);
            params.put("BIZ_TYPE_CODE", biz_type);
            params.putAll(buildBizOrder(discntInfo, commData, biz_type));
        }else{
            return null;
        }
        params.put("KIND_ID", "MCAS_SYNC_0_0");
		// params.put("IBSYSID", discntInfo.getString("INST_ID"));
		// 3位省代码+14位操作时间YYYYMMDDHH24MMSS+4位流水号（定长），序号每天从0001开始，增量步长为1。
		params.put("IBSYSID", "8981120" + discntInfo.getString("INST_ID").substring(2));
        params.put("SERIAL_NUMBER", mainTrade.getString("SERIAL_NUMBER"));
        params.put("BILL_TYPE", "2");
        params.put("OPER_CODE", getOperCode(mainTrade, discntInfo));
        params.put("CHANNEL_CODE", "88");
        params.put("TRADE_ID", mainTrade.getString("TRADE_ID"));
        params.put("USER_ID", mainTrade.getString("USER_ID"));
        params.put("RSRV_STR1", "CRM_CALL");
        
        params.put("ACCEPT_DATE",buildAcceptDate(mainTrade, discntInfo));
	    return params;
	}
	
	public boolean isChgOper(IData mainTrade, IData discntInfo) throws Exception 
	{
		if ("110".equals(mainTrade.getString("TRADE_TYPE_CODE")) &&
				"2_U".indexOf(discntInfo.getString("MODIFY_TAG")) > -1)
		{
			String dateStr = discntInfo.getString("END_DATE");
			if (dateStr.indexOf("2050") > -1)
			{
				return false;
			}
		}
		return true;
	}
	/***
	 * 构造日期
	 * @param mainTrade
	 * @param discntInfo
	 * @return
	 * @throws Exception
	 */
	private String buildAcceptDate(IData mainTrade, IData discntInfo)
			throws Exception {
		//退订及变更取消时，状态生效时间为失效时间
        //变更订购的状态生效时间为下月生效时间
    	String date = "";
        if ("110".equals(mainTrade.getString("TRADE_TYPE_CODE")))
        {
            if ("1".equals(discntInfo.getString("MODIFY_TAG")) || 
            		"2_U".indexOf(discntInfo.getString("MODIFY_TAG")) > -1)
            {
            	date = discntInfo.getString("END_DATE");
            	if (StringUtils.isNotBlank(date) && date.length() <= 10)
            	{
            		date = date + SysDateMgr.END_DATE;
            	}
                return DataFormat(date);
            }else{
                //订购时状态生效时间为起始时间
            	date = discntInfo.getString("START_DATE");
            	if (StringUtils.isNotBlank(date) && date.length() <= 10)
            	{
            		date = date + SysDateMgr.START_DATE_FOREVER;
            	}
               return DataFormat(date);
            }
        }else if ("10".equals(mainTrade.getString("TRADE_TYPE_CODE")))
        {
            	//订购时状态生效时间为起始时间
            	date = discntInfo.getString("START_DATE");
            	if (StringUtils.isNotBlank(date) && date.length() <= 10)
            	{
            		date = date + SysDateMgr.START_DATE_FOREVER;
            	}
               return DataFormat(date);
        }
        return SysDateMgr.getSysDateYYYYMMDDHHMMSS();

	}

	private String DataFormat(String date) throws Exception
	{
		String new_Date = date;
		Date temp_date = SysDateMgr.string2Date(date, SysDateMgr.PATTERN_STAND);
		new_Date = SysDateMgr.date2String(temp_date, SysDateMgr.PATTERN_STAND_SHORT);
		return new_Date;
	}

	private IData buildBizOrder(IData discntInfo, IData commData, String biz_type) throws Exception
	{
		IData biz_order = new DataMap();
		String sp_id = "";
		
		if ("83".equals(biz_type) || "88".equals(biz_type))
		{
			IDataset commparas = getAppointFlow(discntInfo.getString("DISCNT_CODE"));
			sp_id = commparas.getData(0).getString("PARA_CODE20");
			}
		biz_order.put("SP_CODE", sp_id);
		
		String sp_biz_code = commData.getString("PARA_CODE3");
		biz_order.put("BIZ_CODE", sp_biz_code);

		return biz_order;
	}
	
	/****
	 * 查询资费静态参数配置
	 * 
	 * @param discnt_code
	 * @return
	 * @throws Exception
	 */
	public IDataset getAppointFlow(String discnt_code) throws Exception
	{
		IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017", discnt_code, "IS_VIDEO_PKG", null);
		if (IDataUtil.isNotEmpty(paraList))
		{
			return paraList;
		}
		return null;
	}

	/****
	 * 查询套餐流量静态参数配置
	 * 
	 * @param discnt_code
	 * @return
	 * @throws Exception
	 */
	public IDataset getPackageFlow(IData mainTrade,IData discntInfo) throws Exception
    {
		String discnt_code = discntInfo.getString("DISCNT_CODE");
		String user_id = mainTrade.getString("USER_ID");
		String trade_type_code = mainTrade.getString("TRADE_TYPE_CODE");
		String trade_id = mainTrade.getString("TRADE_ID");
        IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017","CLEARING_PACKAGEFLOW", discnt_code, null);
        if (IDataUtil.isNotEmpty(paraList))
        {
            return paraList;
        }else{
        	IDataset products = null;
        	IData prod = null;
        	if ("10".equals(trade_type_code) || 
        			("110".equals(trade_type_code) && 
        					"2_U".indexOf(discntInfo.getString("MODIFY_TAG")) > -1))
        	{
        		products = TradeProductInfoQry.getTradeProductByTradeId(trade_id);
        		for(int i = 0 ; IDataUtil.isNotEmpty(products) && i < products.size() ; i++)
            	{
        			if (IDataUtil.isNotEmpty(products.getData(i)) && 
        					"1".equals(products.getData(i).getString("MAIN_TAG")))
        			{
        				prod = products.getData(i);
        			}
            	}
        	}else{
        		products = UserProductInfoQry.queryUserMainProduct(user_id);
            	if (IDataUtil.isNotEmpty(products))
            	{
            		prod = products.getData(0);
            	}
        	}
        	if (IDataUtil.isNotEmpty(prod))
        	{
        		paraList = CommparaInfoQry.getCommparaInfoBy6("CSM", "2017", "CLEARING_PACKAGEFLOW_BY_PROD", discnt_code, prod.getString("PRODUCT_ID"), "ZZZZ", null);
        		if (IDataUtil.isNotEmpty(paraList))
                {
                    return paraList;
                }
        	}
        	//BUG20200514144712关于任我用和移动王卡套餐订购关系代码异常问题的修复 start
        	//需另外判断:用户主产品为配置产品,且有生效配置优惠, 做主产品变更为非配置优惠
			if("110".equals(trade_type_code)&&"1".equals(discntInfo.getString("MODIFY_TAG"))){
				IDataset tradeProducts = TradeProductInfoQry.getTradeProductByTradeId(trade_id);
				IData tradeMainProduct = null;
				if(IDataUtil.isEmpty(tradeProducts)){
					return null;
				}
				for(int i = 0 ; i < tradeProducts.size() ; i++){
					if (IDataUtil.isNotEmpty(tradeProducts.getData(i)) &&"1".equals(tradeProducts.getData(i).getString("MAIN_TAG"))
							&&"1".equals(tradeProducts.getData(i).getString("MODIFY_TAG"))){
						tradeMainProduct = tradeProducts.getData(i);
					}
				}
				if (IDataUtil.isNotEmpty(tradeMainProduct)){
					paraList = CommparaInfoQry.getCommparaInfoBy6("CSM", "2017", "CLEARING_PACKAGEFLOW_BY_PROD", discnt_code, tradeMainProduct.getString("PRODUCT_ID"), "ZZZZ", null);
					if (IDataUtil.isNotEmpty(paraList))
					{
						return paraList;
					}
				}
			}
			//BUG20200514144712关于任我用和移动王卡套餐订购关系代码异常问题的修复 end
        }
        return null;
    }

	/****
     * 查询套餐流量相关业务静态参数配置
     * @param discnt_code
     * @return
     * @throws Exception
     */
    public IDataset getPackageFlowTradeType(String trade_type_code) throws Exception
    {
        IDataset paraList = CommparaInfoQry.getCommparaByCode1("CSM", "2017","CLEARING_PACKAGEFLOW_TRADETYPE", trade_type_code, null);
        if (IDataUtil.isNotEmpty(paraList))
        {
            return paraList;
        }
        return null;
    }
    
	/***
	 * 根据用户ID查询用户优惠
	 */
	public IDataset getAllDiscntByUserId(String userId) throws Exception
	{
		IData param = new DataMap();
		param.put("USER_ID", userId);
		return Dao.qryByCode("TF_F_USER_DISCNT", "SEL_ALLUSERDISCNT_BY_USERID", param);
	}
}
