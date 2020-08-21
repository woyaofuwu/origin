
package com.asiainfo.veris.crm.order.soa.group.cenpaygfffesop;

import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cenpaygfffesop.GrpCenpayGfffEsopMgrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;



public class GrpCenpayGfffEsopSvc extends GroupOrderService
{
	private static final Logger logger = Logger.getLogger(GrpCenpayGfffEsopSvc.class);
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作流量自由充产品的主台账工单
     * 流量自由充ESOP审核接口
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset updateAllGrpCenpayGfff(IData data) throws Exception
    {
        IDataset resultSet = new DatasetList();
        IData resultData = new DataMap();
        
        logger.error("集团流量自由充(定额统付)产品审核的入参:---" + data);
        
        //esop传过来的order_id
        if(IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(GrpException.CRM_GRP_914);
        }
        
        String tradeId = data.getString("TRADE_ID","");
        if(StringUtils.isBlank(tradeId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_911);
        }
        
        String checkPass = data.getString("CHECK_PASS","");
        
        //true 审核通过,修改工单的执行时间为当前时间
        if(StringUtils.isNotBlank(checkPass) && 
        		StringUtils.equals("true", checkPass))
        {
        	IDataset resultInfos = GrpCenpayGfffEsopMgrQry.queryGrpCenpayGfffByTradeId(data);
        	if(IDataUtil.isEmpty(resultInfos))
        	{
        		CSAppException.apperr(GrpException.CRM_GRP_913);
        	}
        	
        	String orderId = resultInfos.getData(0).getString("ORDER_ID","");
        	String tradeTypeCode = resultInfos.getData(0).getString("TRADE_TYPE_CODE","");
        	if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3890", tradeTypeCode))//定额产品订购
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeGrpCenpayNowByTradeId(tradeId);
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3891", tradeTypeCode))//定额产品修改
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNextDateByTradeId(tradeId,"00099002");
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowNextByTradeId(tradeId,"00099002");
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3892", tradeTypeCode))//定额产品的注销
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowNextByTradeId(tradeId,"00099002");
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowNextByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowNextByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeCenpayNewNextByTradeId(tradeId);
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3626", tradeTypeCode))//全量统付产品新增
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeGrpCenpayNowByTradeId(tradeId);
        		
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3627", tradeTypeCode))//全量统付产品修改
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNextDateByTradeId(tradeId,"7361");
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNextDateByTradeId(tradeId,"7360");
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNextDateByTradeId(tradeId,"7362");
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowNextByTradeId(tradeId,"7361");
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowNextByTradeId(tradeId,"7360");
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowNextByTradeId(tradeId,"7362");
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowNextByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNextDateByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNextDateByTradeId(tradeId);
        		
        	} 
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3628", tradeTypeCode))//全量统付产品注销
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateAllTradeAttrDateByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowNextByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeCenpayNewNextByTradeId(tradeId);
        		
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3896", tradeTypeCode))//限量统付产品新增
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNowByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeGrpCenpayNowByTradeId(tradeId);
        		
        	}
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3897", tradeTypeCode))//限量统付产品资料修改
        	{
        		
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowNextByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNextDateByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateAllTradeAttrDateByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeAttrNextDateByTradeId(tradeId);
        		
        	} 
        	else if(StringUtils.isNotBlank(tradeTypeCode) &&
        			StringUtils.equals("3898", tradeTypeCode))//限量统付产品注销
        	{
        		
        		GrpCenpayGfffEsopMgrQry.updateAllTradeAttrDateByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeDiscntNowNextByTradeId(tradeId);
        		
        		GrpCenpayGfffEsopMgrQry.updateTradeCenpayNewNextByTradeId(tradeId);
        		GrpCenpayGfffEsopMgrQry.updateTradeSvcNowNextByTradeId(tradeId);
        		
        	}
        	
        	
        	
        	
        	GrpCenpayGfffEsopMgrQry.updateGrpGfffTradeByTradeId(tradeId);
        	GrpCenpayGfffEsopMgrQry.updateGrpGfffOrderByOrderId(orderId);
        	
        	resultData.put("X_RESULTCODE", "0");
        	resultData.put("X_RESULTINFO", "审核通过成功!");
        	resultSet.add(resultData);
        }
        //false 审核不通过,把主台账工单搬历史表,并删除现表主台账的工单
        else if(StringUtils.isNotBlank(checkPass) && 
        		StringUtils.equals("false", checkPass))
        {
        	String orderId = "";
        	IDataset resultInfos = GrpCenpayGfffEsopMgrQry.queryGrpCenpayGfffByTradeId(data);
        	if(IDataUtil.isNotEmpty(resultInfos))
        	{
        		orderId = resultInfos.getData(0).getString("ORDER_ID","");
        		
        	}
        	
        	
        	if(StringUtils.isNotBlank(orderId))
    		{
        		GrpCenpayGfffEsopMgrQry.insertGrpGfffBhOrderByOrderId(orderId);
    		}
        	GrpCenpayGfffEsopMgrQry.insertGrpGfffBhTradeByTradeId(tradeId);
        	
        	
        	if(StringUtils.isNotBlank(orderId))
    		{
        		GrpCenpayGfffEsopMgrQry.deleteGrpGfffBOrderByOrderId(orderId);
    		}
        	GrpCenpayGfffEsopMgrQry.deleteGrpGfffBTradeByTradeId(tradeId);
        	
        	resultData.put("X_RESULTCODE", "0");
        	resultData.put("X_RESULTINFO", "审核不通过成功!");
        	resultSet.add(resultData);
        } 
        else 
        {
//        	CSAppException.apperr(GrpException.CRM_GRP_912);
        }
  
        return resultSet;
    }
    
    
    /**
     * 流量自由充信息获取接口，提供给ESOP展示
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getGrpGfffInfo(IData data) throws Exception
    {
    	IDataset resultSet = new DatasetList();
    	IData resultData = new DataMap();
        
        if(IDataUtil.isEmpty(data))
        {
            CSAppException.apperr(GrpException.CRM_GRP_914);
        }
        
        String tradeId = data.getString("TRADE_ID","");
        if(StringUtils.isBlank(tradeId))
        {
            CSAppException.apperr(GrpException.CRM_GRP_911);
        }
        
        String getType = data.getString("GET_TYPE","");
        if(StringUtils.isBlank(getType))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713,"缺少GET_TYPE");
        }
        
        //开通
        if("0".equals(getType))
        {
        	//1、合同信息 CONTRACT_INFO
            IDataset contractInfos = GrpCenpayGfffEsopMgrQry.queryContractAndSerialNumberInfos(data);
            String contractInfo = "";
            if(IDataUtil.isNotEmpty(contractInfos))
            {
            	contractInfo = contractInfos.getData(0).getString("CONTRACT_INFO","");
            }
            resultData.put("CONTRACT_INFO", contractInfo);
            
            //2、产品信息PRODUCT_INFO和 元素信息 ELEMENT_INFO
            //产品信息
            IDataset productInfos = GrpCenpayGfffEsopMgrQry.queryProductInfos(data);
            String productInfo = "";
            if(IDataUtil.isNotEmpty(productInfos))
            {
            	productInfo = productInfos.getData(0).getString("PRODUCT_INFO","");
            }
            resultData.put("PRODUCT_INFO", productInfo);

            //元素信息
            IDataset elementInfos = GrpCenpayGfffEsopMgrQry.queryElementInfos(data);
            String elementInfo = "";
        	if(IDataUtil.isNotEmpty(elementInfos))
        	{
        		for (int i = 0, psize = elementInfos.size(); i < psize; i++)
                {
            		IData temp1 = elementInfos.getData(i);
            		elementInfo = elementInfo + temp1.getString("ELEMENT_INFO","")+"\n";
                }
        	}
            resultData.put("ELEMENT_INFO", elementInfo);
            
            //3、成员附加产品信息
            String grpInfo = "";
            IDataset grpInfos = GrpCenpayGfffEsopMgrQry.queryGrpProductInfos(data);
            if(IDataUtil.isNotEmpty(grpInfos))
        	{
            	for (int i = 0, gsize = grpInfos.size(); i < gsize; i++)
                {
            		IData temp2 = grpInfos.getData(i);
            		grpInfo = grpInfo + temp2.getString("GRP_INFO","")+"\n";
                }
        	}
            resultData.put("GRP_INFO", grpInfo);
            
            //4、服务号码信息
            if(IDataUtil.isNotEmpty(contractInfos))
        	{
            	 String serialNumber = contractInfos.getData(0).getString("SERIAL_NUMBER","");
            	 resultData.put("SERIAL_NUMBER", serialNumber);
        	}
            
            //5、账户信息
            IDataset accountInfos = GrpCenpayGfffEsopMgrQry.queryAccountInfos(data);
            String accountInfo = "";
        	if(IDataUtil.isNotEmpty(accountInfos))
        	{
        		accountInfo = accountInfos.getData(0).getString("ACCOUNT_INFO","");
        	}
            resultData.put("ACCOUNT_INFO", accountInfo);
        }
        
        //变更
        if("1".equals(getType))
        {
        	//1、合同信息 CONTRACT_INFO  先从台帐取，如果取不到表示没变合同，则从资料取
        	String contractInfo = "";
            IDataset contractInfos = GrpCenpayGfffEsopMgrQry.queryContractAndSerialNumberInfos(data);
            if(IDataUtil.isEmpty(contractInfos))
            {
            	contractInfos = GrpCenpayGfffEsopMgrQry.queryContractAndSerialNumberInfos2(data);
            }
            
            if(IDataUtil.isNotEmpty(contractInfos))
            {
            	contractInfo = contractInfos.getData(0).getString("CONTRACT_INFO","");
            }
            resultData.put("CONTRACT_INFO", contractInfo);
            
            //2、产品信息PRODUCT_INFO和 元素信息 ELEMENT_INFO   
            //产品信息    产品无法变更直接从资料取
            IDataset productInfos = GrpCenpayGfffEsopMgrQry.queryProductInfos2(data);
            String productInfo = "";
            if(IDataUtil.isNotEmpty(productInfos))
            {
            	productInfo = productInfos.getData(0).getString("PRODUCT_INFO","");
            }
            resultData.put("PRODUCT_INFO", productInfo);

            //元素信息   先从台帐取，如果取不到表示没变元素，则从资料取
            String elementInfo = "";
            IDataset elementInfos = GrpCenpayGfffEsopMgrQry.queryElementInfos(data);
            if(IDataUtil.isEmpty(elementInfos))
        	{
            	elementInfos = GrpCenpayGfffEsopMgrQry.queryElementInfos2(data);
        	}

        	if(IDataUtil.isNotEmpty(elementInfos))
        	{
        		for (int i = 0, psize = elementInfos.size(); i < psize; i++)
                {
            		IData temp1 = elementInfos.getData(i);
            		elementInfo = elementInfo + temp1.getString("ELEMENT_INFO","")+"\n";
                }
        	}
            resultData.put("ELEMENT_INFO", elementInfo);
            
            //3、成员附加产品信息   先从台帐取，如果取不到表示没变成员产品信息，则从资料取
            String grpInfo = "";
            IDataset grpInfos = GrpCenpayGfffEsopMgrQry.queryGrpProductInfos(data);
            if(IDataUtil.isEmpty(grpInfos))
        	{
            	grpInfos = GrpCenpayGfffEsopMgrQry.queryGrpProductInfos2(data);
        	}
            
            if(IDataUtil.isNotEmpty(grpInfos))
        	{
            	for (int i = 0, gsize = grpInfos.size(); i < gsize; i++)
                {
            		IData temp2 = grpInfos.getData(i);
            		grpInfo = grpInfo + temp2.getString("GRP_INFO","")+"\n";
                }
        	}
            resultData.put("GRP_INFO", grpInfo);
            
            //4、服务号码信息
            if(IDataUtil.isNotEmpty(contractInfos))
        	{
            	 String serialNumber = contractInfos.getData(0).getString("SERIAL_NUMBER","");
            	 resultData.put("SERIAL_NUMBER", serialNumber);
        	}

        }
        
        //拆除
        if("2".equals(getType))
        {
        	//1、合同信息 CONTRACT_INFO
            IDataset contractInfos = GrpCenpayGfffEsopMgrQry.queryContractAndSerialNumberInfos3(data);
            String contractInfo = "";
            if(IDataUtil.isNotEmpty(contractInfos))
            {
            	contractInfo = contractInfos.getData(0).getString("CONTRACT_INFO","");
            }
            resultData.put("CONTRACT_INFO", contractInfo);
            
            //2、产品信息PRODUCT_INFO和 元素信息 ELEMENT_INFO
            //产品信息
            IDataset productInfos = GrpCenpayGfffEsopMgrQry.queryProductInfos(data);
            String productInfo = "";
            if(IDataUtil.isNotEmpty(productInfos))
            {
            	productInfo = productInfos.getData(0).getString("PRODUCT_INFO","");
            }
            resultData.put("PRODUCT_INFO", productInfo);

            //元素信息
            IDataset elementInfos = GrpCenpayGfffEsopMgrQry.queryElementInfos(data);
            String elementInfo = "";
        	if(IDataUtil.isNotEmpty(elementInfos))
        	{
        		for (int i = 0, psize = elementInfos.size(); i < psize; i++)
                {
            		IData temp1 = elementInfos.getData(i);
            		elementInfo = elementInfo + temp1.getString("ELEMENT_INFO","")+"\n";
                }
        	}
            resultData.put("ELEMENT_INFO", elementInfo);
            
            //3、成员附加产品信息
            String grpInfo = "";
            IDataset grpInfos = GrpCenpayGfffEsopMgrQry.queryGrpProductInfos(data);
            if(IDataUtil.isNotEmpty(grpInfos))
        	{
            	for (int i = 0, gsize = grpInfos.size(); i < gsize; i++)
                {
            		IData temp2 = grpInfos.getData(i);
            		grpInfo = grpInfo + temp2.getString("GRP_INFO","")+"\n";
                }
        	}
            resultData.put("GRP_INFO", grpInfo);
            
            //4、服务号码信息
            if(IDataUtil.isNotEmpty(contractInfos))
        	{
            	 String serialNumber = contractInfos.getData(0).getString("SERIAL_NUMBER","");
            	 resultData.put("SERIAL_NUMBER", serialNumber);
        	}
            
            //5、账户信息
            IDataset accountInfos = GrpCenpayGfffEsopMgrQry.queryAccountInfos(data);
            String accountInfo = "";
        	if(IDataUtil.isNotEmpty(accountInfos))
        	{
        		accountInfo = accountInfos.getData(0).getString("ACCOUNT_INFO","");
        	}
            resultData.put("ACCOUNT_INFO", accountInfo);
            
            //6、拆除原因和备注
            if(IDataUtil.isNotEmpty(contractInfos))
        	{
            	 String remark = contractInfos.getData(0).getString("REMARK","");
            	 resultData.put("REMARK", remark);
        	}
        }
        
        
        resultSet.add(resultData);
    	return resultSet;
    }
}                      