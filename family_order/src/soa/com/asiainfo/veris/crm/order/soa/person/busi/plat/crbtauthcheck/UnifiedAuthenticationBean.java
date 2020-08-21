
package com.asiainfo.veris.crm.order.soa.person.busi.plat.crbtauthcheck;

import org.apache.log4j.Logger;

import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.ailk.org.apache.commons.lang3.StringUtils;

public class UnifiedAuthenticationBean extends CSBizBean
{

	private static final Logger log = Logger.getLogger(UnifiedAuthenticationBean.class);
    /**
     * @description 视频彩铃鉴权接口
     * @date 2018-05-30
     * @author dengyi5
     * @param param
     *            
     * @return
     * @throws Exception
     */
    public IData crbtAuth(IData param) throws Exception
    {
    	log.debug("-------------crbtAuth------param="+param);
    	IDataUtil.chkParam(param, "OPR_CODE");
		IDataUtil.chkParam(param, "SERIAL_NUMBER");    	
		IData result = new DataMap();
		
        String oprCode = param.getString("OPR_CODE");        
        String serialNumber = param.getString("SERIAL_NUMBER");
        result.put("SERIAL_NUMBER", serialNumber);
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo)) 
        {
        	IDataset userInfoList = UserInfoQry.getDestroyUserInfoBySn(serialNumber);
        	if (IDataUtil.isEmpty(userInfoList)) 
        	{
        		userInfo = userInfoList.first();
        	} 
        	else 
        	{
        		result.put("USER_STATUS", "99");
        		result.put("X_RESULTCODE", "0925");
        		result.put("X_RSPTYPE", "2");
        		result.put("X_RESULTINFO", "用户不存在");
        		return result;
        	}
        }
        String removeTag = userInfo.getString("REMOVE_TAG");
		String userId = userInfo.getString("USER_ID");
		
        
        if (!"0".equals(removeTag)) 
        {
			if (removeTag.equals("1") || removeTag.equals("3")) 
			{
				result.put("USER_STATUS", "03");
				result.put("X_RESULTCODE", "0923");
				result.put("X_RSPTYPE", "2");
				result.put("X_RESULTINFO", "用户预销户");
				return result;
			} 
			else// if (!removeTag.equals("0")) 
			{
				result.put("USER_STATUS", "04");
				result.put("X_RESULTCODE", "0924");
				result.put("X_RSPTYPE", "2");
				result.put("X_RESULTINFO", "用户销户");
				return result;
			}
		}
        
        IDataset userUiaList = UserPlatSvcInfoQry.queryUiaBySerialNumber(serialNumber);
		if (IDataUtil.isEmpty(userUiaList)) 
		{
			result.put("USER_STATUS", "99");
			result.put("X_RESULTCODE", "0925");
			result.put("X_RSPTYPE", "2");
			result.put("X_RESULTINFO", "其他资源表错误");
			return result;
		} 
		else 
		{
			userInfo.putAll(userUiaList.first());
		}		
		IData commList = getCommParam();//获取彩铃，volte服务id
        //00：密码鉴权;01：音频彩铃用户状态查询; 02：视频彩铃用户状态查询;03：用户VoLTE属性查询
        if("00".equals(oprCode))
        {
        	// 密码校验 优先于 其他 业务查询判断
        	String strPasswd = param.getString("PASSWD", "");
    		if (StringUtils.isNotBlank(strPasswd)) 
    		{
    			if (!UserInfoQry.checkUserPassWd(userId, strPasswd)) 
    			{
    				result.put("X_RESULTCODE", "0901");
    				result.put("X_RSPTYPE", "2");
    				result.put("X_RESULTINFO", "密码不正确");
    				return result;
    			}
    		}
        }
        else if("01".equals(oprCode)||"02".equals(oprCode))
        {
        	String userStateCodeset = userInfo.getString("USER_STATE_CODESET");
    		result.put("USER_STATUS", userStateCodeset);
    		
    		if("01".equals(oprCode))
    		{
    			IDataset extension = new DatasetList();
    			IData crtTag = new DataMap();
    			crtTag.put("NAME", "CRT");
    			crtTag.put("VALUE", getsvcTag(userId, commList, "CRT"));
    			extension.add(crtTag);
    			IData volteTag = new DataMap();
    			volteTag.put("NAME", "VoLTE");
    			volteTag.put("VALUE", getsvcTag(userId, commList, "VoLTE"));
    			extension.add(volteTag);
    			
    			result.put("EXTENSION", extension);
    		}
    		
        }
        else if("03".equals(oprCode))
        {
        	//00：非VoLTE用户;01：是VoLTE用户
        	String volteTag = getsvcTag(userId,commList,"VoLTE");
        	if("0".equals(volteTag))
        	{//非
        		volteTag ="00";
        	}else
        	{//是
        		volteTag ="01";
        	}
        	result.put("USER_STATUS", volteTag);//用户已单向停机
        }
        log.debug("-------------crbtAuth------result="+result);
        return result;
    }

	private String getsvcTag(String userId,IData commList,String extName) throws Exception 
	{
		String statustag  = "";
		if("CRT".equals(extName))
		{//个人彩铃业务
			//0－开通;1－未开通
			IDataset coloringSvc = UserSvcInfoQry.getSvcUserId(userId, commList.getString("PARA_CODE3"));
			if(IDataUtil.isEmpty(coloringSvc))
			{
				statustag = "1";
			}
			else
			{
				statustag = "0";
			}
		}
		else
		{//volte
			// 0-非;1-是
			IDataset volteSvc = UserSvcInfoQry.getSvcUserId(userId, commList.getString("PARA_CODE2"));
			if(IDataUtil.isEmpty(volteSvc))
			{
				statustag = "0";
			}
			else
			{
				statustag = "1";
			}
		}
		return statustag;
	}

	private IData getCommParam() throws Exception
    {
		//查询视频彩铃服务配置,PARA_CODE1 配置为视频彩铃服务、PARA_CODE2 配置为VOLTE服务、PARA_CODE3 配置为彩铃服务
        IDataset commList = CommparaInfoQry.getCommpara("CSM", "2017", "VIDEO_COLORRING_SERV", CSBizBean.getUserEparchyCode());
        if(IDataUtil.isEmpty(commList))
        {
            String errors = "视频彩铃服务静态参数【VIDEO_COLORRING_SERV】未配置，请联系管理员！";
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errors);
        }
        return commList.first();
    }
}
