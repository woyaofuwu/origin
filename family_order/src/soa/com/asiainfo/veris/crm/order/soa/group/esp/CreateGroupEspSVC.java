package com.asiainfo.veris.crm.order.soa.group.esp;

import org.apache.log4j.Logger;

import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;

public class CreateGroupEspSVC extends GroupOrderService{
    private static final long serialVersionUID = 1L;
    private static transient Logger logger = Logger.getLogger(CreateGroupEspSVC.class);

    public IDataset crtTrade(IData inparam) throws Exception
    {
    	IDataset dataset = new DatasetList();
//    	IData svcCont = new DataMap(inparam.getString("SvcCont"));
    	String orderInfoReqStr = inparam.getString("ORDER_INFO_REQ");
    	IDataset orderInfoReqS = new DatasetList(orderInfoReqStr);
    	logger.debug("++++orderInfoReqS=" + orderInfoReqS);
    	if(null != orderInfoReqS && orderInfoReqS.size() > 0){
    		IData orderInfoReq = orderInfoReqS.getData(0);
    		String IsBbossProduct = orderInfoReq.getString("IS_BBOSS_PRODUCT");//是否是集客大厅管理的产品 1是 0否；目前只有移动云归属集客大厅管理
    		String grpId = orderInfoReq.getString("CUSTOMER_PROVINCE_NUMBER");//IsBbossProduct=0为省集团客户编码； IsBbossProduct=1为全网集团客户编码（BBOSS分配）；
    		IData groupInfo=new DataMap();
    		if("1".equals(IsBbossProduct))
    			groupInfo=GrpInfoQry.queryGrpInfoByGrpCustCode(grpId).getData(0);
    		else
    			groupInfo = UcaInfoQry.qryGrpInfoByGrpId(grpId);
    		
    		logger.debug("++++groupInfo=" + groupInfo);
    		String custId = groupInfo.getString("CUST_ID");
    		setTradeEparchyCode(groupInfo.getString("EPARCHY_CODE"));
    		//setRouteId(groupInfo.getString("EPARCHY_CODE"));
    		setUserEparchyCode(groupInfo.getString("EPARCHY_CODE"));
    		String operatorId = orderInfoReq.getString("OPERATOR_CODE");
    		String cityCode = groupInfo.getString("CITY_CODE");
    		IDataset productOrderInfos = orderInfoReq.getDataset("PRODUCTER_ORDER_INFO");
    		if(null != productOrderInfos && productOrderInfos.size() > 0){
    			for(int i=0; i<productOrderInfos.size(); i++){
    				IData productOrderInfo = productOrderInfos.getData(i);
    				IDataset SubProductOrderInfos = productOrderInfo.getDataset("SUB_PRODUCT_ORDER_INFO");
    				String JKDT_OFFER_ID=productOrderInfo.getString("PRODUCT_ORDER_ID");//IsBbossProduct=1为集客大厅上移动云商品订购实例
    				if(null != SubProductOrderInfos && SubProductOrderInfos.size() > 0){
    					for(int j = 0; j < SubProductOrderInfos.size(); j++){
    						IData SubProductOrderInfo = SubProductOrderInfos.getData(j);
    						SubProductOrderInfo.put("CITY_CODE", cityCode);
    						SubProductOrderInfo.put("CUST_ID", custId);
    						SubProductOrderInfo.put("OPERATOR_ID", operatorId);
    						SubProductOrderInfo.put("OPR_TYPE", SubProductOrderInfo.getString("SUB_OPR_TYPE"));
    						SubProductOrderInfo.put("IS_BBOSS_PRODUCT", IsBbossProduct);
    						SubProductOrderInfo.put("JKDT_OFFER_ID", JKDT_OFFER_ID);
  						
    						dataset = crtTradeDetail(SubProductOrderInfo);
    					}
    				}else{
    					productOrderInfo.put("CITY_CODE", cityCode);
    					productOrderInfo.put("CUST_ID", custId);
    					productOrderInfo.put("OPERATOR_ID", operatorId);
    					dataset = crtTradeDetail(productOrderInfo);
    				}
    			}
    		}
    	}
    	return dataset;
    }
    
    public IDataset crtTradeDetail(IData productOrderInfo) throws Exception
    {
    	IDataset dataset = new DatasetList();
    	//产品开通
    	if("01".equals(productOrderInfo.getString("OPR_TYPE","-1"))){
    		if(!"-1".equals(productOrderInfo.getString("ACCOUNTID","-1")) && !"".equals(productOrderInfo.getString("ACCOUNTID","-1")))
    		{//如果平台下发用户账户数据就不需要新增账户信息了
    			productOrderInfo.put("ACCT_IS_ADD", false);
    			productOrderInfo.put("ACCT_ID", productOrderInfo.getString("ACCOUNTID"));
    		}else{
    			productOrderInfo.put("ACCT_IS_ADD", true);
    		}
        	CreateGroupEspBean bean = new CreateGroupEspBean();
        	dataset = bean.crtTrade(productOrderInfo);		
    	}
    	//产品变更
    	else if("05".equals(productOrderInfo.getString("OPR_TYPE","-1")) || "06".equals(productOrderInfo.getString("OPR_TYPE","-1"))){
    		ChangeGroupEspBean bean = new ChangeGroupEspBean();
    		dataset = bean.crtTrade(productOrderInfo);
    	}
    	//产品暂停，恢复
    	else if("03".equals(productOrderInfo.getString("OPR_TYPE","-1")) || "04".equals(productOrderInfo.getString("OPR_TYPE","-1"))){
    		PauseContinueGroupEspBean bean = new PauseContinueGroupEspBean();
    		dataset = bean.crtTrade(productOrderInfo);
    	}
    	//产品取消
    	else if("02".equals(productOrderInfo.getString("OPR_TYPE","-1"))){
    		if (logger.isDebugEnabled()) {
    			logger.debug("houxi ======== staffId:" + CSBizBean.getVisit().getStaffId());
    		}
    		DestroyGroupEspBean bean = new DestroyGroupEspBean();
    		dataset = bean.crtTrade(productOrderInfo);
    	}
    	return dataset;
    }

}
