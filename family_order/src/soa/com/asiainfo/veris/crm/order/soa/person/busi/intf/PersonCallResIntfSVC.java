package com.asiainfo.veris.crm.order.soa.person.busi.intf;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

public class PersonCallResIntfSVC extends CSBizService{
	protected static Logger log = Logger.getLogger(PersonCallResIntfSVC.class);
	
	/**
	 * 区域选号号段查询
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset queryNumSegAreaSel(IData param) throws Exception {
		setPublicParam(param);
		return callRes("RCF.resource.INumberIntfQuerySV.queryNumSegAreaSel",param);
	}
	/**
	 * 网厅入库前检查
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset termB2BOutInstockByCheck(IData param) throws Exception {
		setPublicParam(param);
		if(param.getString("CALL_TAG")==null||"1".equals(param.getString("CALL_TAG"))){
			return callRes("RCF.resource.ITermB2BOperateSV.termB2BOutInstockByCheck",param);
		}else{
			return callRes("RC.resource.ITermB2BOperateSV.termB2BOutInstockByCheck",param);
		}
	}
	/**
	 * 网厅入库
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public IDataset termB2BOutInstock(IData param) throws Exception {
		setPublicParam(param);
		return callRes("RC.resource.ITermB2BOperateSV.termB2BOutInstock",param);
	}
	
    public static IDataset callRes(String svcCode,IData param)throws Exception{    	
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
}
