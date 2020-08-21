package com.asiainfo.veris.crm.order.soa.person.busi.interboss.remoteservice;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.IdentcardInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class RemoteServiceSVC extends CSBizService{
	
	/**
     * 获取统一凭证
     * 
     * @param data
     * @throws Exception
     */
    private void qryUserIdentInfo(IData data) throws Exception
    {
    	IData result = IdentcardInfoQry.getUserIdentInfo(data.getString("IDENT_CODE"),data.getString("ID_VALUE"));

        if (IDataUtil.isEmpty(result))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_915);
        }

        data.put("USER_TYPE", result.getString("USER_TYPE"));
        data.put("IDENT_CODE_LEVEL", result.getString("IDENT_CODE_LEVEL"));
        data.put("IDENT_CODE_TYPE", result.getString("IDENT_CODE_TYPE"));
        data.put("MSISDN", result.getString("SERIAL_NUMBER"));
        data.put("USER_ID", result.getString("USER_ID"));
        data.put("TAG", result.getString("TAG",""));
    }
    
    /**
     * 根据IDENT_CODE延长统一凭证
     * 
     * @param data
     * @throws Exception
     */
    private void updUserIdentInfoByIdentCode(IData data) throws Exception
    {
        int result = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_BY_IDENT", data);

        if (result <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_918);
        }
    }
    
    /**
     *根据IDENT_CODE 终止统一凭证
     * 
     * @param data
     * @throws Exception
     */
    private void delUserIdentInfoByIdentCode(IData data) throws Exception
    {
        int result = Dao.executeUpdateByCodeCode("TF_B_IDENTCARD_MANAGE", "UPD_DISABLE_BY_IDENT", data);

        if (result <= 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_916);
        }
    }
	
	/**
	 * 身份凭证延时---跨区业务
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset authDelay(IData data) throws Exception{
		// 返回操作结果
        IDataset returnDataset = new DatasetList();
        IData resultData = new DataMap();
        // 参数检查
		IDataUtil.chkParam(data, "ID_VALUE");
		IDataUtil.chkParam(data, "IDENT_CODE");
		IDataUtil.chkParam(data, "EFFECTIVE_TIME");
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", data.getString("ID_VALUE"));
		resultData.put("OPR_NUMB", data.getString("OPR_NUMB"));
		IDataset commparaSet = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "IDENT_CODE_MAX_EFFECTIVE_TIME", getTradeEparchyCode());
		int limitTime;
		if(IDataUtil.isNotEmpty(commparaSet)) {
			limitTime = commparaSet.first().getInt("PARA_CODE1", 1800);
		} else {
			limitTime = 1800;
		}
		int effectiveTime = data.getInt("EFFECTIVE_TIME");
		if(effectiveTime > limitTime) {
			resultData.put("BIZ_ORDER_RESULT", "2998");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RESULTCODE", "2998");
			resultData.put("RESULT_DESC", "身份凭证申请延时时间不得超过限制：" + String.valueOf(limitTime) + "秒！");
			resultData.put("X_RSPDESC", "身份凭证申请延时时间不得超过限制：" + String.valueOf(limitTime) + "秒！");
			returnDataset.add(resultData);
			return returnDataset;
		}
        try {
			// 检查当前身份凭证是否为有效状态
			qryUserIdentInfo(data);
			
			if(data.getString("TAG","").equals("EXPIRE")){
				resultData.put("BIZ_ORDER_RESULT", "3018");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "2998");
				resultData.put("X_RESULTCODE", "3018");
				resultData.put("RESULT_DESC", "身份凭证已经失效！");
				resultData.put("X_RSPDESC", "身份凭证已经失效！");
						returnDataset.add(resultData);
				return returnDataset;
			}
		} catch (Exception e) {
			resultData.put("BIZ_ORDER_RESULT", "2998");
			resultData.put("X_RSPTYPE", "2");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RESULTCODE", "2998");
			if(e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2) {
				resultData.put("RESULT_DESC", e.getMessage().substring(e.getMessage().indexOf(":")+1));
				resultData.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":")+1));
			}

			else if(e.getMessage().indexOf("`") >= 1 && e.getMessage().length() >= 2) {
				resultData.put("RESULT_DESC", e.getMessage().substring(e.getMessage().indexOf("`")+1));
				resultData.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf("`")+1));
			}
			else {
				resultData.put("RESULT_DESC", "业务处理中出现异常");
				resultData.put("X_RSPDESC", "业务处理中出现异常");
			}

			
			returnDataset.add(resultData);
	        return returnDataset;
		}

        // 设置失效时间
        updUserIdentInfoByIdentCode(data);

        resultData.put("BIZ_ORDER_RESULT","0000");
		resultData.put("X_RESULTCODE", "0000");
		resultData.put("X_RSPCODE", "0000");
        resultData.put("RESULT_DESC", "受理成功");
        resultData.put("X_RSPDESC", "受理成功");
        returnDataset.add(resultData);
        return returnDataset;
	}
	
	/**
	 * 鉴权登出---跨区业务
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public IDataset authLogout(IData data) throws Exception{
		// 返回操作结果
        IDataset returnDataset = new DatasetList();
        IData resultData = new DataMap();
        // 参数检查
		IDataUtil.chkParam(data, "ID_VALUE");
		IDataUtil.chkParam(data, "IDENT_CODE");
		resultData.put("ID_TYPE", "01");
		resultData.put("ID_VALUE", data.getString("ID_VALUE"));
		resultData.put("OPR_NUMB", data.getString("OPR_NUMB"));
        try {
			String serialNumber = data.getString("ID_VALUE");
			IData userInfo = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber);
			if (IDataUtil.isEmpty(userInfo))
			{
				resultData.put("BIZ_ORDER_RESULT", "2009");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "2998");
				resultData.put("X_RESULTCODE", "2009");
				resultData.put("RESULT_DESC","用户资料不存在！");
				resultData.put("X_RSPDESC", "用户资料不存在！");
				returnDataset.add(resultData);
				return returnDataset;
			    
			}
			// 检查当前身份凭证是否存在
			qryUserIdentInfo(data);

			// 设置当前身份凭证的失效时间
			if(data.getString("TAG").equals("EXPIRE"))//已经注销凭证直接返回成功
			{
				resultData.put("BIZ_ORDER_RESULT", "3018");
				resultData.put("RESULT_DESC", "用户身份已过期/失效");
				resultData.put("X_RSPTYPE", "2");
				resultData.put("X_RSPCODE", "2998");
//				resultData.put("X_RSPDESC", "失败");
				resultData.put("X_RESULTCODE", "3018");
				resultData.put("X_RSPDESC", "用户身份已过期/失效");
				returnDataset.add(resultData);
		        return returnDataset;
			}else{
				
				delUserIdentInfoByIdentCode(data);
			}
		} catch (Exception e) {
			resultData.put("BIZ_ORDER_RESULT", "2998");
			resultData.put("X_RSPCODE", "2998");
			resultData.put("X_RESULTCODE", "2998");
			if(e.getMessage().indexOf(":") >= 1 && e.getMessage().length() >= 2) {
				resultData.put("RESULT_DESC", e.getMessage().substring(e.getMessage().indexOf(":")+1));
				resultData.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf(":")+1));
			}

			else if(e.getMessage().indexOf("`") >= 1 && e.getMessage().length() >= 2) {
				resultData.put("RESULT_DESC", e.getMessage().substring(e.getMessage().indexOf("`")+1));
				resultData.put("X_RSPDESC", e.getMessage().substring(e.getMessage().indexOf("`")+1));
			}
			else {
				resultData.put("RESULT_DESC", "业务处理中出现异常");
				resultData.put("X_RSPDESC", "业务处理中出现异常");
			}
			returnDataset.add(resultData);
	        return returnDataset;
		}

		resultData.put("BIZ_ORDER_RESULT","0000");
        resultData.put("X_RESULTCODE", "0000");
        resultData.put("X_RSPCODE", "0000");
		resultData.put("RESULT_DESC", "受理成功");
		resultData.put("X_RSPDESC", "受理成功");
        returnDataset.add(resultData);
        return returnDataset;
	}
}
