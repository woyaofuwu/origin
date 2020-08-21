
package com.asiainfo.veris.crm.order.soa.person.busi.wechatmicroblog;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.TradeProcess;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MSpBizQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserIdentInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;

/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductIntfSVC.java
 * @Description: 产品变更接口转接服务类【一般给外围接口使用】
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 12, 2014 4:21:56 PM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 12, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductIntfSVC extends CSBizService
{
	 /**
     * @Description: 获取产品变更数据
     * @param btd
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 6, 2014 2:44:05 PM
     */
    public List<BaseTradeData> getChangeProductData(BusiTradeData btd) throws Exception
    {
        List<BaseTradeData> productData = new ArrayList<BaseTradeData>();

        List<BaseTradeData> discntLists = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);

        List<BaseTradeData> svcLists = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);

        List<BaseTradeData> productLists = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);

        productData.addAll(discntLists);
        productData.addAll(svcLists);
        productData.addAll(productLists);

        return productData;
    }
    
    /**
     * @Description: 拼串
     * @param str
     * @param elementTypeCode
     * @param productData
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Aug 6, 2014 2:44:24 PM
     */
    public String getStringAppendData(String str, String elementTypeCode, BaseTradeData productData) throws Exception
    {
        String tempStr = "";

        if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(elementTypeCode) && productData.getTableName().equals("TF_B_TRADE_DISCNT"))
        {
            tempStr = UDiscntInfoQry.getDiscntNameByDiscntCode(productData.toData().getString("DISCNT_CODE"));
        }
        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(elementTypeCode) && productData.getTableName().equals("TF_B_TRADE_SVC"))
        {
            tempStr = USvcInfoQry.getSvcNameBySvcId(productData.toData().getString("SERVICE_ID"));
        }
        if (BofConst.ELEMENT_TYPE_CODE_PRODUCT.equals(elementTypeCode) && productData.getTableName().equals("TF_B_TRADE_PRODUCT"))
        {
            tempStr = UProductInfoQry.getProductNameByProductId(productData.toData().getString("PRODUCT_ID"));
        }

        if (StringUtils.isNotBlank(tempStr))
        {
            str += tempStr + ",";
        }
        return str;
    }

    /**
     * @Description: 产品变更校验
     * @param input
     * @return
     * @throws Exception
     * @author: pengsy 
     */
    public IData changeProductCheck(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "IDTYPE");
    	IDataUtil.chkParam(input, "OPR_NUMB");
    	IDataUtil.chkParam(input, "BIZ_TYPE_CODE");
        String serialNumber = IDataUtil.chkParam(input, "IDVALUE");//手机号码
        String bunessType = IDataUtil.chkParam(input, "BUNESS_TYPE");//产品类型
        String oprNumb = input.getString("OPR_NUMB");
        input.put("SERIAL_NUMBER", serialNumber);
        String productId = "";
        String newProductName = "";
        IData result = new DataMap();
        IData resultData = new DataMap();
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	
        if (IDataUtil.isEmpty(userInfo))
        {
        	CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
		
        String identCode = input.getString("IDENT_CODE");
        
        /************************合版本 duhj  2017/5/3 start*******************************/
        String intf_type="";
		//校验客户凭证
        String bizCodeType = input.getString("BIZ_TYPE_CODE");//渠道编码
        if("62".equals(bizCodeType) || "76".equals(bizCodeType)){//微信微博身份鉴权
		IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
		if(IDataUtil.isEmpty(dataset)){
			CSAppException.apperr(CrmUserException.CRM_USER_938);
		}
        }else if(CustServiceHelper.isCustomerServiceChannel(bizCodeType)){//一级客服升级业务能力开放平台身份鉴权
        	IData identPara =  new DataMap();
        	identPara.put("SERIAL_NUMBER", serialNumber);
        	identPara.put("IDENT_CODE", identCode);
        	CustServiceHelper.checkCertificate(identPara);
        	intf_type="01";
        }
        /************************合版本 duhj  2017/5/3 end*******************************/

		if ("01".equals(bunessType))
	    {
			productId = IDataUtil.chkParam(input, "PRODUCT_ID");// 产品ID
			OrderDataBus dataBus = DataBusManager.getDataBus();
	        dataBus.setOrderTypeCode("110");
	        dataBus.setAcceptTime(SysDateMgr.getSysDate());

	        input.put("TRADE_TYPE_CODE", "110");
	        input.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
	        input.put("X_TRANS_CODE", "SS.ChangeProductRegSVC.ChangeProduct"); 
	        input.put("ELEMENT_TYPE_CODE", "P");
	        input.put("MODIFY_TAG", "0");
	        input.put("ELEMENT_ID", productId);
	        input.put("PRODUCT_ID", productId); 
//            resultData.put("ELEMENT_TYPE_CODE", "P");
//            resultData.put("MODIFY_TAG", "0");
//            resultData.put("ELEMENT_ID", productId);
//            resultData.put("PRODUCT_ID", productId); 
            //input.put("SELECTED_ELEMENTS", resultList);
	        BusiTradeData btd = TradeProcess.acceptOrder(input);

	        List<BaseTradeData> productDatas = this.getChangeProductData(btd);

	        String addSvc = new String();
	        String delSvc = new String();
	        String addDiscnt = new String();
	        String delDiscnt = new String();
	        String addProduct = new String();
	        String delProduct = new String();

	        if (productDatas != null && productDatas.size() > 0)
	        {
	            for (BaseTradeData productData : productDatas)
	            {
	                String modifyTag = productData.toData().getString("MODIFY_TAG");

	                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
	                {
	                    addSvc = this.getStringAppendData(addSvc, BofConst.ELEMENT_TYPE_CODE_SVC, productData);
	                    addDiscnt = this.getStringAppendData(addDiscnt, BofConst.ELEMENT_TYPE_CODE_DISCNT, productData);
	                    addProduct = this.getStringAppendData(addProduct, BofConst.ELEMENT_TYPE_CODE_PRODUCT, productData);
	                }
	                if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
	                {
	                    delSvc = this.getStringAppendData(delSvc, BofConst.ELEMENT_TYPE_CODE_SVC, productData);
	                    delDiscnt = this.getStringAppendData(delDiscnt, BofConst.ELEMENT_TYPE_CODE_DISCNT, productData);
	                    delProduct = this.getStringAppendData(delProduct, BofConst.ELEMENT_TYPE_CODE_PRODUCT, productData);
	                }
	            }
	        }

	        newProductName = UProductInfoQry.getProductNameByProductId(productId);

	       
	        result.put("DEL_SVC", StringUtils.isNotBlank(delSvc) ? delSvc.substring(0, delSvc.length() - 1) : "");
	        result.put("ADD_SVC", StringUtils.isNotBlank(addSvc) ? addSvc.substring(0, addSvc.length() - 1) : "");
	        result.put("DEL_DISCNT", StringUtils.isNotBlank(delDiscnt) ? delDiscnt.substring(0, delDiscnt.length() - 1) : "");
	        result.put("ADD_DISCNT", StringUtils.isNotBlank(addDiscnt) ? addDiscnt.substring(0, addDiscnt.length() - 1) : "");
	        result.put("RSRV_STR1", StringUtils.isNotBlank(delProduct) ? delProduct.substring(0, delProduct.length() - 1) : "");
	        result.put("RSRV_STR2", StringUtils.isNotBlank(addProduct) ? addProduct.substring(0, addProduct.length() - 1) : "");
	        result.put("RSRV_STR3", StringUtils.isNotBlank(delSvc) ? delSvc.substring(0, delSvc.length() - 1) : "");
	        result.put("RSRV_STR4", StringUtils.isNotBlank(addSvc) ? addSvc.substring(0, addSvc.length() - 1) : "");
	        result.put("RSRV_STR5", StringUtils.isNotBlank(delDiscnt) ? delDiscnt.substring(0, delDiscnt.length() - 1) : "");
	        result.put("RSRV_STR6", StringUtils.isNotBlank(addDiscnt) ? addDiscnt.substring(0, addDiscnt.length() - 1) : "");
	        result.put("DEAL_TAG", "0");
	        result.put("VPMN_TAG", "0");
	        result.put("INFO_VALUE", "你所办理的新产品是【" + newProductName + "】");
	        result.put("CHECK_INFO", "你所办理的新产品是【" + newProductName + "】");
	        result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
	        result.put("OPR_NUMB", oprNumb);
	    }else if("02".equals(bunessType)){
	    	/**增加判断平台业务代码、取BIZ_TYPE_CODE(平台不会传的），拼串。chenxy3 2015-09-11*/
	    	IDataUtil.chkParam(input, "BIZ_CODE");
	    	IDataUtil.chkParam(input, "SP_CODE");
	    	String spCode=input.getString("SP_CODE","");
	    	String bizCode=input.getString("BIZ_CODE","");
	    	String bizCode4IBoss ="";
	    	String bizTypeCode="";
	    	IDataset bizList=MSpBizQry.queryBizInfoBySpcodeBizCode(spCode,bizCode);
	    	if(bizList !=null && bizList.size()>0){
	    		bizTypeCode=bizList.getData(0).getString("BIZ_TYPE_CODE","");
	    		bizCode4IBoss = bizCode+"|"+bizTypeCode;
	    	}else{
	    		CSAppException.apperr(ParamException.CRM_PARAM_441);
	    	}
	    	
        	IData param = new DataMap();
        	param.put("SERIAL_NUMBER", serialNumber);
        	
        	
        	result = this.dealPlatTrade(param,bizCode4IBoss,input,
					oprNumb,PlatConstants.OPER_ORDER);
            result.put("OPR_NUMB", input.getString("OPR_NUMB"));
        }
		/**********************合版本 duhj 2017/5/3 start********************************/
		if(!"".equals(intf_type)){
			result.put("INTF_TYPE", intf_type);
		}
		/**********************合版本 duhj 2017/5/3 end********************************/

		result.put("PROD_ORDER_CHECKRSLT", "0");//0成功，1失败
        return result;
    }
    
    private IData dealPlatTrade(IData param,String bizCode4IBoss, IData data,String oprNumb,
			 String operOrder) throws Exception {
		// TODO Auto-generated method stub
   	IData result = new DataMap();
		String spCode = data.getString("SP_CODE");
		if (bizCode4IBoss.indexOf("|") == -1)
		{
			CSAppException.apperr(ParamException.CRM_PARAM_442);
		}
		String bizCode = bizCode4IBoss.split("\\|")[0];
		String bizTypeCode = bizCode4IBoss.split("\\|")[1];
		String oprSource = data.getString("BIZ_TYPE_CODE");
		param.put("TRANS_ID", oprNumb);
		param.put("BIZ_TYPE_CODE", bizTypeCode);
		param.put("SP_CODE", spCode);
		param.put("BIZ_CODE", bizCode);
		param.put("OPER_CODE", operOrder);
		/**************************合版本 duhj 2017/5/3 start***********************************/
		String changeOprSoure = CustServiceHelper.getCustomerServiceChannel(oprSource);                
        if(changeOprSoure!=null&&changeOprSoure.trim().length()>0){
            oprSource = changeOprSoure;
        }
		/**************************合版本 duhj 2017/5/3 end***********************************/

		param.put("OPR_SOURCE", oprSource);
		param.put("PRE_TYPE", BofConst.PRE_TYPE_CHECK);// 预受理校验，不写台账
		IDataset results = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", param);
		if ("".equals(results)&& results == null){
			result.put("X_RESULTCODE", "-1");
			result.put("X_RESULTINFO", "调用平台业务出错!"); 
			result.put("PROD_ORDER_CHECKRSLT", "1");//0成功，1失败
        	return result;
		}
		result = results.getData(0);
		//增加OPR_TIME字段返回
		result.put("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		
		
		return result;
	}
   
   private void setEffectiveTime(IData result,String flag) throws Exception{
		if("0".equals(flag))
			result.put("EFFECT_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
		else
			result.put("EFFECT_TIME",SysDateMgr.getFirstDayOfNextMonth());
	}
   
    
}
