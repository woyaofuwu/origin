
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.querynpmessage;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserNpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeNpQry;

public class QueryNpMessageSVC extends CSBizService
{
    public IData queryNpMessage(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        return bean.queryNpMessage(param, getPagination());
    }
    
    public IDataset queryNpOutSets(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        return bean.queryNpOutSets(param, getPagination());
    }
    public IDataset queryNpOutRules(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        param.put("PARAM_ATTR", "0");
        param.put("PARAM_CODE", "NPOUTRULE");
        return bean.queryParams(param, null);
    }
    
    public void setNpOutSets(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        String str = param.getString("COPRULES");
        
        if (StringUtils.isNotBlank(str)) {
            IData copData = new DataMap();
            copData.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            copData.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
            IDataset cops = new DatasetList(str);
            for (int i = 0, len = cops.size(); i < len; i++) {
            	copData.put("PARA_CODE1", cops.getData(i).getString("COP_ID"));
            	copData.put("PARA_CODE3", cops.getData(i).getString("RULE_ID"));
            	bean.setNpOutSets(copData);
            }
        }
        
    }
    
    public IData queryNpMessageforIntf(IData param) throws Exception
    {
    	IData result = new DataMap();
    	result.put("NP_OUT_TAG", "0");
    	String custName = param.getString("CUST_NAME", "");
    	String psptId = param.getString("PSPT_ID", "");
    	if ("".equals(custName) || "".equals(psptId)) {
    		CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "参数中没有客户姓名或者证件号码！");
    	}
    	IData npInfo = queryNpMessage(param);
    	IData custData = npInfo.getData("custinfo");
    	if (IDataUtil.isEmpty(custData)) {
    		CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "没有获取到客户信息！");
    	}
    	if (!custName.equals(custData.getString("CUST_NAME")) || !psptId.equals(custData.getString("PSPT_ID"))) {
    		result.put("NP_OUT_TAG", "1");
    	}
    	IDataset npInfoList = npInfo.getDataset("infos");
    	if (IDataUtil.isNotEmpty(npInfoList)) {
    		result.put("NP_OUT_TAG", "1");
    	}
    	
    	return result;
    }
    
    /**
     * SS.QueryNpMessageSVC.queryNpOutTrade
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryNpOutTrade(IData param) throws Exception
    {
    	String acceptDate = param.getString("ACCEPT_DATE", "");
    	String serialNumber = param.getString("SERIAL_NUMBER", "");
    	if(StringUtils.isEmpty(acceptDate)&&StringUtils.isEmpty(serialNumber)) {
    		CSAppException.apperr(CrmUserNpException.CRM_USER_NP_9527, "服务号码和携转受理时间不能同时为空！");
    	}
    	String trafficArea = param.getString("TRAFFIC_AREA", "");
    	
    	String endDate = null;
    	if(StringUtils.isNotEmpty(acceptDate))
    			endDate = acceptDate+SysDateMgr.END_DATE;
    	
    	IDataset result = new DatasetList();
    	if (StringUtils.isNotEmpty(trafficArea)) {
    		// 查询携出工单
        	result = TradeNpQry.getNpOutTrade(acceptDate, endDate, serialNumber, null);//不分页
		}else {
			// 查询携出工单
        	result = TradeNpQry.getNpOutTrade(acceptDate, endDate, serialNumber, getPagination());//分页
		}
    	
    	if(IDataUtil.isNotEmpty(result)){
    		// 查询客户业务区
    		IDataset custAreaList = TradeNpQry.getCustAreaByNpTrade(result,trafficArea);
    		IData custAreaMap = new DataMap();
    		if(IDataUtil.isNotEmpty(custAreaList)){
	    		for(int i=0; i<custAreaList.size(); i++){
	    			custAreaMap.put(custAreaList.getData(i).getString("USER_ID"), custAreaList.getData(i).getString("CUSTMER_AREA"));
	    		}
    		}
    		if (StringUtils.isNotEmpty(trafficArea)) {
    			IDataset result1 = new DatasetList();
    			for(int i=0; i<result.size(); i++){
	    			String custmerArea = custAreaMap.getString(result.getData(i).getString("USER_ID"));
	    			if (StringUtils.isNotEmpty(custmerArea)) {
	        			result.getData(i).put("CUSTMER_AREA", custmerArea);
	        			result1.add(result.getData(i));
					}
    			}
    			result = result1;
    		}else {
    			for(int i=0; i<result.size(); i++){
        			result.getData(i).put("CUSTMER_AREA", custAreaMap.getString(result.getData(i).getString("USER_ID")));
        		}
    		}
    	}

    	return result;
    }
    
    
    public IDataset queryNpOutMessage(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        return bean.queryNpOutMessage(param);
    }
    
    public IDataset AuthCodeApply(IData param) throws Exception
    {
        QueryNpMessageBean bean = (QueryNpMessageBean) BeanManager.createBean(QueryNpMessageBean.class);
        return bean.AuthCodeApply(param);
    }
}
