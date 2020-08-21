
package com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.buildrequest.common.SaleActiveCommonBuilder;
import com.asiainfo.veris.crm.order.soa.person.busi.saleactive.order.requestdata.SaleActiveReqData;

public class SaleActiveBuilder extends SaleActiveCommonBuilder implements IBuilder
{
	private static Logger logger = Logger.getLogger(SaleActiveBuilder.class);

    protected IDataset buildSelectedElementsFee(IDataset selectedElements, IData input, String productId, String packageId, String eparchyCode) throws Exception
    {
        // TODO 稍后将这个费用的校验摞到业务规则校验中去
    	String callType = input.getString("CALL_TYPE");
    	if(SaleActiveConst.CALL_TYPE_ACTIVE_TRANS.equals(callType))
    	{
    		return null;
    	}
    	
        int dbOperFeeInt = 0, dbAdvancePayInt = 0, pageOperFeeInt = 0, pageAdvancePayInt = 0;
        /**
         * REQ201604180021 网上营业厅合约终端销售价格调整需求
         * chenxy3 20160428 
         */
        String serialNumber=input.getString("SERIAL_NUMBER");
        String orderId=input.getString("NET_ORDER_ID", "");
        
        IData param=new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("PACKAGE_ID", packageId);
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("NET_ORDER_ID", orderId);
        IDataset comms = CommparaInfoQry.getCommparaInfoByCode("CSM", "6895", productId, packageId, eparchyCode);
        IDataset comms2602 = CommparaInfoQry.getCommparaInfoByCode("CSM", "2602", productId, packageId, eparchyCode);
        IDataset terminalOrderInfos = CSAppCall.call("CS.SaleActiveQuerySVC.qryTerminalOrderInfoForCheck", param);
        if(terminalOrderInfos!=null && terminalOrderInfos.size()>0){
        	IDataset payset=new DatasetList(input.getString("X_TRADE_PAYMONEY"));
        	if(IDataUtil.isNotEmpty(payset)){
        		String orderType=terminalOrderInfos.getData(0).getString("ORDER_TYPE","") ;
        		String payCode=payset.getData(0).getString("PAY_MONEY_CODE","");
        		if( "3".equals(orderType) || "5".equals(orderType)){
	        		if(!"".equals(payCode) && !"L".equals(payCode) ){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "付款方式非货到付款，请正确选择付款方式！");
	        		}
        		}else if("1".equals(orderType) || "6".equals(orderType) || "7".equals(orderType)){
        			if(!"".equals(payCode) && !"8".equals(payCode) ){
	        			CSAppException.apperr(CrmCommException.CRM_COMM_103, "付款方式非网上商城付款，请正确选择付款方式！");
	        		}
        		}
        	}
        }
        
        for (int index = 0, size = selectedElements.size(); index < size; index++)
        {
            IData selectedElement = selectedElements.getData(index);
            String elementTypeCode = selectedElement.getString("ELEMENT_TYPE_CODE");
            String elementId = selectedElement.getString("ELEMENT_ID");
//            IDataset feeDataset = ProductFeeInfoQry.qryPayFeeByIds("240", productId, packageId, elementTypeCode, elementId, eparchyCode);
            IDataset feeDataset = ProductFeeInfoQry.getSaleActiveFee("240", "K", packageId, elementTypeCode, elementId, productId, "0");
            if (IDataUtil.isNotEmpty(feeDataset))
            {
            	for(int j=0, s=feeDataset.size(); j<s; j++)
            	{
            		IData feeData = feeDataset.getData(j);

                    String feeMode = feeData.getString("FEE_MODE");
                    String fee = feeData.getString("FEE");

                    if ("0".equals(feeMode) && StringUtils.isNotBlank(fee))
                    {
                        if ("G".equals(elementTypeCode) && !"".equals(input.getString("SALEGOODS_IMEI")))
                        {
                            String terminalId = input.getString("SALEGOODS_IMEI");
                            if(StringUtils.isNotEmpty(terminalId)){
                            	terminalId = terminalId.trim();
                            }
                            String rsrvStr1 = feeData.getString("RSRV_STR1");
                            String rsrvStr2 = feeData.getString("RSRV_STR2");
                            String rsrvStr3 = feeData.getString("RSRV_STR3");
                            String rsrvStr4 = feeData.getString("RSRV_STR4");
                            fee = getTermianlFee(productId, packageId, terminalId, fee, rsrvStr1, rsrvStr2, rsrvStr3, rsrvStr4, eparchyCode);
                        }
                        dbOperFeeInt += Integer.parseInt(fee);
                    }

                    if ("2".equals(feeMode) && StringUtils.isNotBlank(fee))
                    {
                        dbAdvancePayInt += Integer.parseInt(fee);
                    } 
            	}
            	if (StringUtils.isBlank(input.getString("X_TRADE_FEESUB")))
            	{
            		if(terminalOrderInfos!=null && terminalOrderInfos.size()>0){
                    	
                    }else if(IDataUtil.isNotEmpty(comms)||IDataUtil.isNotEmpty(comms2602)){
                    	
                    }else{
	            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务受理的费用和业务配置的费用不一致，请检查！");
                    }
            	}
            		
            }
        }

        if (StringUtils.isNotBlank(input.getString("X_TRADE_FEESUB")))
        {
            IDataset feeSubInfos = new DatasetList(input.getString("X_TRADE_FEESUB"));
            int feeSubSize = feeSubInfos.size();
            for (int i = 0; i < feeSubSize; i++)
            {
                IData feeSubInfo = feeSubInfos.getData(i);
                if ("240".equals(feeSubInfo.getString("TRADE_TYPE_CODE")) || "3814".equals(feeSubInfo.getString("TRADE_TYPE_CODE")) || "3815".equals(feeSubInfo.getString("TRADE_TYPE_CODE")))
                {
                    if ("0".equals(feeSubInfo.getString("FEE_MODE")))
                    {
                        pageOperFeeInt += Integer.parseInt(feeSubInfo.getString("FEE"));
                    }

                    if ("2".equals(feeSubInfo.getString("FEE_MODE")))
                    {
                        pageAdvancePayInt += Integer.parseInt(feeSubInfo.getString("FEE"));
                    }
                }
            }
        }

        if (dbOperFeeInt != pageOperFeeInt || dbAdvancePayInt != pageAdvancePayInt)
        {
        	
            if(terminalOrderInfos!=null && terminalOrderInfos.size()>0){
            	
            }else if(IDataUtil.isNotEmpty(comms)||IDataUtil.isNotEmpty(comms2602)){
            	
            }else{
             
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "业务受理的费用和业务配置的费用不一致，请检查！");
	           
            }
        }
        
        return null;
    }

    protected IData getActiveDates(IData param, BaseReqData brd) throws Exception
    {
        IData activeDateData = new DataMap();
        activeDateData.put("START_DATE", param.getString("START_DATE", ""));
        activeDateData.put("END_DATE", param.getString("END_DATE", ""));
        activeDateData.put("ONNET_START_DATE", param.getString("ONNET_START_DATE", ""));
        activeDateData.put("ONNET_END_DATE", param.getString("ONNET_END_DATE", ""));
        activeDateData.put("BOOK_DATE", param.getString("BOOK_DATE"));
        return activeDateData;
    }

    protected IDataset getPayMoneyList(IData input, IDataset tradeFeeSub)
    {
        return null;
    }

    protected IDataset getSelectedElems(IData param, BaseReqData brd, String bookDate) throws Exception
    {
        return new DatasetList(param.getString("SELECTED_ELEMENTS"));
    }

    private String getTermianlFee(String productId, String packageId, String terminalId, String fee, String rsrvStr1, String rsrvStr2, String rsrvStr3, String rsrvStr4, String eparchyCode) throws Exception
    {
        IData feeParam = new DataMap();
        feeParam.put("PRODUCT_ID", productId);
        feeParam.put("PACKAGE_ID", productId);
        feeParam.put("RES_NO", terminalId);
        feeParam.put("FEE", fee);
        feeParam.put("RSRV_STR1", rsrvStr1);
        feeParam.put("RSRV_STR2", rsrvStr2);
        feeParam.put("RSRV_STR3", rsrvStr3);
        feeParam.put("RSRV_STR4", rsrvStr4);
        feeParam.put(Route.ROUTE_EPARCHY_CODE, eparchyCode);
        IDataset dataset = CSAppCall.call("CS.SaleActiveElementFeeSVC.getTerminalOperFeeByResNo", feeParam);
        IData data = dataset.getData(0);
        return data.getString("FEE");
    }

    protected void setCampnInfo(IData param, BaseReqData brd) throws Exception
    {
        SaleActiveReqData saleActiveRequestData = (SaleActiveReqData) brd;

        saleActiveRequestData.setCampnType(param.getString("CAMPN_TYPE"));
        saleActiveRequestData.setCampnId("-1");
        saleActiveRequestData.setCampnCode("-1");
        saleActiveRequestData.setNetOrderId(param.getString("NET_ORDER_ID"));
        saleActiveRequestData.setCallType(param.getString("CALL_TYPE"));
    }

}
