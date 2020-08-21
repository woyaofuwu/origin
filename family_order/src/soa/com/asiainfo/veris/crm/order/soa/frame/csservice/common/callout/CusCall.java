
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout;

import org.apache.log4j.Logger;

import com.ailk.biz.BizVisit;
import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CusCall
{
    protected static Logger log = Logger.getLogger(CusCall.class);
    
    
    public static IDataset qryByParams(IData input) throws Exception{
        setPublicParam(input);
        //return callRes("CCF.group.IGroupQuerySV.qryNpUserListBakByParams",input);
        return callCus(input.getString("CALL_NAME"),input);
    }

 // 公共信息参数设置
    private static void setPublicParam(IData inData) throws Exception
    {
        inData.put(Route.USER_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 用户归属地州
        inData.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());// 用户交易地州
        inData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());// 受理业务区
        inData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());// 受理部门
        inData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());// 受理员工
        inData.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());
    }
    
    public static IDataset callCus(String svcCode,IData param)throws Exception{    	
    	ServiceRequest request = new ServiceRequest();
        request.setData(param);
        if(log.isDebugEnabled()){
        	log.debug(svcCode + " send res params: " + param);
        }
        ServiceResponse response = (ServiceResponse) BizServiceFactory.call(svcCode, request, null);
        IData head = response.getHead();
        String xResultcode = head.getString("X_RESULTCODE");
        if(!"0".equals(xResultcode)){
        	String xResultinfo = head.getString("X_RESULTINFO");
//        	CSAppException.apperr(BizException.CRM_BIZ_171, svcCode, xResultinfo);
        }
        IDataset dataset = response.getData();
//        IDataset dataset = response.getDataset(OUTDATA);
        if(log.isDebugEnabled()){
        	log.debug(svcCode + " receive res result: " + dataset);
        }
        return dataset;
    }
    
}
