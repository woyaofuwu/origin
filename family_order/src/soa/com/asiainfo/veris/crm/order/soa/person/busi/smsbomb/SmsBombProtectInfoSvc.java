package com.asiainfo.veris.crm.order.soa.person.busi.smsbomb;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

@SuppressWarnings("serial")
public class SmsBombProtectInfoSvc extends CSBizService
{
	private static final Logger logger = Logger.getLogger(SmsBombProtectInfoSvc.class);
	
	/**
	 * 根据号码来查询保护名单信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombProtectInfoBySn(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
        IDataset result = bean.querySmsBombProtectInfoBySn(param);
        return result;
    }
	
	/**
	 * 根据号码来查询保护名单信息
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombInfoBySn(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
        IDataset result = bean.querySmsBombInfoBySn(param);
        return result;
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void addProtectInfos(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
		bean.addProtectInfos(param);
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void delProtectInfos(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
		bean.delProtectInfos(param);
    }
	
	/**
	 * 
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public void updateProtectInfos(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
		bean.updateProtectInfos(param);
    }
	
	/**
	 * 查询白名单
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset querySmsBombWhiteInfoByRecvId(IData param) throws Exception
    {
		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
        IDataset result = bean.querySmsBombWhiteInfoByRecvId(param);
        return result;
    }
	
	/**
	 * 短信炸弹保护名单查询接口,提供给在线客服
	 * C898HQQrySMSBombWhiteMobileNumList
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData qrySMSBombWhiteMobileNumList(IData params) throws Exception 
	{
		IData result = new DataMap();
    	IData object = new DataMap();
    	IDataset resultDataset = new DatasetList();
    	
    	try
    	{
    		IData param = new DataMap(params.toString()).getData("params");
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("短信炸弹保护名单查询接口请求参数:" + params);
    		}
    		
    		if (IDataUtil.isEmpty(param))
    		{
    			object.put("respCode", "-9999");
    			object.put("respDesc", "获取params参数为空!");
    			object.put("result", resultDataset);
    			
    			result.put("object", object);
    			result.put("rtnCode", "0");
    			result.put("rtnMsg", "成功!");
    			return result;
    		}
    		
    		//手机号码
    		String serialNumber = param.getString("userMobile");
    		if(StringUtils.isBlank(serialNumber))
    		{
    			setResult(resultDataset,object,result,"服务号码不能为空!");
    			return result;
    		}
    		
    		IData userInfoParam = new DataMap();
    		userInfoParam.put("SERIAL_NUMBER", serialNumber);
    		userInfoParam.put("REMOVE_TAG", "0");
    		
    		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
            IDataset smsBombInfos = bean.querySmsBombProtectInfoBySn(userInfoParam);
    		            
            IDataset resultSub = null;
            if(IDataUtil.isNotEmpty(smsBombInfos))
            {
            	IData data = smsBombInfos.getData(0);
            	if(IDataUtil.isNotEmpty(data))
            	{
            		String recvId = data.getString("RECV_ID","");
            		if(StringUtils.isNotBlank(recvId))
            		{
            			IData subParam = new DataMap();
            			subParam.put("RECV_ID", recvId);
            			subParam.put("REMOVE_TAG", "0");
            			resultSub = bean.querySmsBombWhiteInfoByRecvId(subParam);
            		}
            	}
            }
            
            IDataset userMobileList = new DatasetList();
            if(IDataUtil.isNotEmpty(resultSub))
            {
            	for (int i = 0; i < resultSub.size(); i++) 
    			{
            		IData userMobileMap = new DataMap();
    				IData data = resultSub.getData(i);
    				String whiteNum = data.getString("WHITE_NUM","");
    				String whiteType = data.getString("WHITE_TYPE","");
    				String createTime = data.getString("CREATE_TIME","");
    				userMobileMap.put("whiteMobileNum", whiteNum);
    				userMobileMap.put("opTime", createTime);
    				userMobileMap.put("mobileType", whiteType);
    				userMobileList.add(userMobileMap);
    			}
            }
            
	        IData userMobileMap = new DataMap();
	        userMobileMap.put("userMobileList", userMobileList);
	        resultDataset.add(userMobileMap);
	        
    		object.put("respCode", "0");
    		object.put("respDesc", "成功!");
    		object.put("result", resultDataset);
    		
    		result.put("object", object);
    		result.put("rtnCode", "0");
    		result.put("rtnMsg", "成功!");
    		
    		return result;
    	}
    	catch(Exception e)
    	{
    		String message = e.getMessage();
    		
    		logger.error("短信炸弹保护名单查询异常:" + message);
    		
    		object.put("respCode", "-9999");
    		object.put("respDesc", message);
    		object.put("result", resultDataset);
    		
    		result.put("object", object);
    		result.put("rtnCode", "-9999");
    		result.put("rtnMsg", "失败!");
    		return result;
    	}
	}
	
	/**
	 * 短信炸弹保护名单业务办理
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public IData submitSMSBombWhiteMobNumHandle(IData params) throws Exception 
	{
		IData result = new DataMap();
    	IData object = new DataMap();
    	IDataset resultDataset = new DatasetList();
    	
    	try
    	{
    		IData param = new DataMap(params.toString()).getData("params");
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("短信炸弹保护名单业务办理接口请求参数:" + params);
    		}
    		
    		if (IDataUtil.isEmpty(param))
    		{
    			object.put("respCode", "-9999");
    			object.put("respDesc", "获取params参数为空!");
    			object.put("result", resultDataset);
    			
    			result.put("object", object);
    			result.put("rtnCode", "0");
    			result.put("rtnMsg", "成功!");
    			return result;
    		}
    		
    		//渠道编码
    		String channel = param.getString("contactChannel","");
    		
    		//手机号码
    		String serialNumber = param.getString("userMobile");
    		if(StringUtils.isBlank(serialNumber))
    		{
    			setResult(resultDataset,object,result,"服务号码不能为空!");
    			return result;
    		}
    		
    		IData userInfoParam = new DataMap();
    		userInfoParam.put("SERIAL_NUMBER", serialNumber);
    		userInfoParam.put("REMOVE_TAG", "0");
    		SmsBombProtectInfoBean bean = (SmsBombProtectInfoBean) BeanManager.createBean(SmsBombProtectInfoBean.class);
    		IDataset smsBombInfos = bean.querySmsBombProtectInfoBySn(userInfoParam);
    		
    		//保护名单不为空,走修改来新增白名单、删除白名单
    		if(IDataUtil.isNotEmpty(smsBombInfos))
    		{
    			IData smsBombInfo = smsBombInfos.getData(0);
    			String recvId = "";
    	   	 	String provId = "";
    	   	 	String expireDate = "";
    	   	 	recvId = smsBombInfo.getString("RECV_ID","");
    	   	 	provId = smsBombInfo.getString("PROV_ID","");
    	   	 	expireDate = smsBombInfo.getString("EXPIRE_DATE","");

				//白名单解析
				IData transData = new DataMap();
				IDataset whiteNumList = new DatasetList();
				IDataset whiteNumListd = new DatasetList();
				IDataset whiteMobileNumList = param.getDataset("whiteMobileNumList");
				if(IDataUtil.isNotEmpty(whiteMobileNumList))
				{
					for (int i = 0; i < whiteMobileNumList.size(); i++)
					{
						IData whiteNumData = new DataMap();
						IData data = whiteMobileNumList.getData(i);
						String action = data.getString("action","");
						if("0".equals(action))
						{
							whiteNumData.put("WHITE_NUM", data.getString("whiteMobileNum",""));
							whiteNumData.put("WHITE_TYPE", data.getString("mobileType",""));
							whiteNumData.put("ACTION", action);
							whiteNumList.add(whiteNumData);
						}
						if("1".equals(action))
						{
							whiteNumData.put("WHITE_NUM", data.getString("whiteMobileNum",""));
							whiteNumData.put("WHITE_TYPE", data.getString("mobileType",""));
							whiteNumData.put("ACTION", action);
							whiteNumListd.add(whiteNumData);
						}
					}
				}
				transData.put("PROTECT_NUM", serialNumber);
				transData.put("SERIAL_NUMBER",serialNumber);
				transData.put("REMOVE_TAG","1");//保护名单设置成无效
				transData.put("RECV_ID",recvId);
				transData.put("PROV_ID",provId);
				transData.put("EXPIRE_DATE",expireDate);

				if(IDataUtil.isNotEmpty(whiteNumList))
				{
					transData.put("PROTECT_DATASUB", whiteNumList);
					//新增保护名单
					bean.addProtectInfosnew(transData);
				}

				if(IDataUtil.isNotEmpty(whiteNumListd))
				{
					transData.put("PROTECT_DATASUBNEW", whiteNumListd);
					//删除保护名单
					bean.delProtectInfosnew(transData);
				}
    		}
    		object.put("respCode", "0");
    		object.put("respDesc", "成功!");

    		result.put("object", object);
    		result.put("rtnCode", "0");
    		result.put("rtnMsg", "成功!");

    		return result;
    	}
    	catch(Exception e)
    	{
    		String message = e.getMessage();
    		
    		logger.error("短信炸弹保护名单业务办理异常:" + message);
    		
    		object.put("respCode", "-9999");
    		object.put("respDesc", message);
    		object.put("result", resultDataset);
    		
    		result.put("object", object);
    		result.put("rtnCode", "-9999");
    		result.put("rtnMsg", "失败!");
    		return result;
    	}
	}
	
	private void setResult(IDataset resultDataset,IData object,
			IData result, String message) throws Exception
	{
		object.put("respCode", "-9999");
		object.put("respDesc", message);
		object.put("result", resultDataset);
		
		result.put("object", object);
		result.put("rtnCode", "0");
		result.put("rtnMsg", "成功!");
	}
	
	
}
