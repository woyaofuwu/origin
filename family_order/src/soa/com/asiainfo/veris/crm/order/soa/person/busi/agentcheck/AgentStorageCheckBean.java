package com.asiainfo.veris.crm.order.soa.person.busi.agentcheck;
 
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/** 
 * REQ201602140004下岛问题优化--资源管理相关优化
 * @chenxy3@2016-2-26 修改 
 */
public class AgentStorageCheckBean extends CSBizBean
{

	/**
     * 删除项目 
     */
    public static IDataset checkCardStorage(IData inparams,Pagination pagen) throws Exception
    {
    	String startTime=inparams.getString("START_TIME","");
    	String endTime=inparams.getString("END_TIME","");
    	IDataset results=new DatasetList();
    	if("".equals(startTime) || "".equals(endTime)){
    		results=Dao.qryByCode("TF_F_USER_RES", "SEL_USER_RES_STORAGE", inparams,pagen);
    	}else{
    		results=Dao.qryByCode("TF_F_USER_RES", "SEL_USER_RES_STORAGE2", inparams,pagen);
    	}
    	return results;
    }
    
    /**
     * REQ201802260011_关于优化渠道空中充值缴费上限功能的需求
     * <br/>
     * 通过手机号码判断是否为代理商号码
     * @param inparam
     * @return
     * @throws Exception
     * @author zhuoyingzhi
     * @date 20180307
     */
    public static IData checkIsAgentSerialNumber(IData inparam) throws Exception
    {
    	IData  result=new DataMap();
    	String serialNumber=inparam.getString("SERIAL_NUMBER","");
    	if(!"".equals(serialNumber)&&serialNumber!=null){
    		IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isNotEmpty(userInfo)){
    			IData  param=new DataMap();
    			String userId=userInfo.getString("USER_ID", "");
    			
    				   param.put("USER_ID", userId);
    				   //代理商套餐(VPMN JPA)  655
    				   //param.put("DISCNT_CODE", "655");
    				   
    		     //code_code  在生产上已经存在
    			IDataset  agenyInfo=Dao.qryByCode("TF_F_USER_DISCNT", "SEL_TF_CHL_ACCT_BY_USERID", param); 
    			if(IDataUtil.isNotEmpty(agenyInfo)){
    				//是代理商手机号码
    				result.put("RESULT_CODE", "0");
    				result.put("RESULT_MSG", "是代理商手机号码");
    			}else{
    				//非代理商手机号码
    				result.put("RESULT_CODE", "1");
    				result.put("RESULT_MSG", "非代理商手机号码");
    			}
    		}else{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户信息不存在");
    		}
    	}else{
    		//手机号码为空
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"手机号码不能为空");
    	}
    	
    	result.put("RESULT_INFO", "调接口成功");
    	
    	return result;
    }
    
}