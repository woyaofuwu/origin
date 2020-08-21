
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.util.List;

import net.sf.json.JSONArray;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;

public class QueryPlatServiceBean extends CSBizBean
{

    public static IDataset qryUserPlatSvcs(IData param, Pagination pagination) throws Exception
    {
        String serialNumber = param.getString("SERIAL_NUMBER");
        if (serialNumber == null || "".equals(serialNumber))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_172);
        }

        // 第一步，查询用户信息
        IData users = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(users))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_906);
        }

        // 第二步，查询客户信息
        String userId = users.getString("USER_ID", "");
        String custId = users.getString("CUST_ID", "");

        IData custs = UcaInfoQry.qryCustomerInfoByCustId(custId);
        if (IDataUtil.isEmpty(custs))
        {
            CSAppException.apperr(CustException.CRM_CUST_201);
        }

        String bizTypeCode = param.getString("BIZ_TYPE_CODE");
        String spCode = param.getString("SP_CODE");
        String operCode = param.getString("OPER_CODE");
        String bizStateCode = param.getString("BIZ_STATE_CODE");
        String bizCode = param.getString("BIZ_CODE");

        //查询出结果集与产商品结构循环取结果集
        IDataset userPlatSvcList = UserPlatSvcInfoQry.qryUserPlatSvcs(userId, bizTypeCode, operCode, spCode, bizCode, bizStateCode);
        IDataset resultList = new DatasetList();
        for (int i = 0; i < userPlatSvcList.size(); i++)
        {
        	IData userPlatSvc = userPlatSvcList.getData(i);
        	IDataset upcDatas = new DatasetList();
        	try{
        		upcDatas = UpcCall.querySpServiceAndProdByCond(spCode, bizCode, bizTypeCode, userPlatSvc.getString("SERVICE_ID", ""));
        	}catch(Exception e){
        		
        	}
        	
            if(null != upcDatas && upcDatas.size() > 0)
            {
            	IData upcData = upcDatas.getData(0);
            	//Z.BIZ_NAME,P.SERVICE_NAME,
            	//Z.BILL_TYPE,Z.PRICE,M.SP_NAME,P.SP_CODE,P.BIZ_CODE,P.BIZ_TYPE_CODE," + " Z.SERV_MODE 
            	userPlatSvc.put("BIZ_NAME", upcData.getString("BIZ_NAME"));
            	userPlatSvc.put("SERVICE_NAME", upcData.getString("SERVICE_NAME"));
            	userPlatSvc.put("BILL_TYPE", upcData.getString("BILL_TYPE"));
            	userPlatSvc.put("PRICE", upcData.getString("PRICE"));
            	userPlatSvc.put("SP_NAME", upcData.getString("SP_NAME"));
            	userPlatSvc.put("SP_CODE", upcData.getString("SP_CODE"));
            	userPlatSvc.put("BIZ_CODE", upcData.getString("BIZ_CODE"));
            	userPlatSvc.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
            	userPlatSvc.put("SERV_MODE", upcData.getString("SERV_MODE"));
            
	            String serviceId = userPlatSvc.getString("SERVICE_ID", "");
	            String bizType = userPlatSvc.getString("BIZ_TYPE_CODE", "");
	            String relaInstId = userPlatSvc.getString("RELA_INST_ID", "");
	
	            // 处理无线音乐会员
	            if (serviceId.equals("98001901") && !bizType.equals(""))
	            {
	                IData attr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, relaInstId, "302", null);
	                if ("2".equals(attr.getString("ATTR_VALUE", "")))
	                {
	                    userPlatSvc.put("PRICE", "5000");
	                    userPlatSvc.put("BILL_TYPE", "2");
	                    userPlatSvc.put("BIZ_NAME", "无线音乐平台高级会员");
	                    userPlatSvc.put("BIZ_TYPE", "无线音乐平台高级会员");
	                }
	
	                if ("1".equals(attr.getString("ATTR_VALUE", "")))
	                {
	                    userPlatSvc.put("BIZ_NAME", "无线音乐平台普通会员");
	                    userPlatSvc.put("BIZ_TYPE", "无线音乐平台普通会员");
	                    userPlatSvc.put("PRICE", "0");
	                    userPlatSvc.put("BILL_TYPE", "0");
	                }
	            }
	
	            // 处理动感短信会员业务
	            if (bizType.equals("DX"))
	            {
	                IData attr = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, relaInstId, "8899", null);
	                if ("XSMT".equals(attr.getString("ATTR_VALUE", "")))
	                {
	                    userPlatSvc.put("PRICE", "0");
	                    userPlatSvc.put("BILL_TYPE", "0");
	                    userPlatSvc.put("BIZ_NAME", "动感短信免费鉴赏会员业务");
	                    userPlatSvc.put("BIZ_TYPE", "动感短信免费鉴赏会员业务");
	                }
	
	                if ("XSMTC3".equals(attr.getString("ATTR_VALUE", "")))
	                {
	                    userPlatSvc.put("BIZ_NAME", "动感短信普通会员业务");
	                    userPlatSvc.put("BIZ_TYPE", "动感短信普通会员业务");
	                    userPlatSvc.put("PRICE", "3000");
	                    userPlatSvc.put("BILL_TYPE", "2");
	                }
	
	                if ("XSMTC5".equals(attr.getString("ATTR_VALUE", "")))
	                {
	                    userPlatSvc.put("BIZ_NAME", "动感短信高级会员业务");
	                    userPlatSvc.put("BIZ_TYPE", "动感短信高级会员业务");
	                    userPlatSvc.put("PRICE", "5000");
	                    userPlatSvc.put("BILL_TYPE", "2");
	                }
	            }
	            resultList.add(userPlatSvc);
        	}
        }
        
        int page = pagination.getCurrent();
		int rows = pagination.getPageSize();
		int total = resultList.size();

		/*
		 * if (total > 0) { returnds.getData(0).put("TOTAL", total); }
		 */

		List<Object> list = resultList.subList(rows * (page - 1), ((rows * page) > total ? total : (rows * page)));
		JSONArray json = JSONArray.fromObject(list);
		IDataset ds = DatasetList.fromJSONArray(json);
		if (IDataUtil.isNotEmpty(ds))
		{
			ds.getData(0).put("TOTAL", total);
		}

        return ds;

    }
}
