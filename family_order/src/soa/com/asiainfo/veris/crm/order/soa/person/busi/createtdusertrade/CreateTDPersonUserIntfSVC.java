
package com.asiainfo.veris.crm.order.soa.person.busi.createtdusertrade;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductFeeInfoQry;

/**
 * Copyright: Copyright (c) 2015 Asiainfo
 * 
 * @ClassName: CreateTDPersonUserIntfSVC.java
 * @Description: 无线固话开户服务接口
 * @version: v1.0.0
 * @author: yanwu
 * @date: 2015-4-24 上午15:15:15 Modification History: Date Author Version Description
 * 
 */
public class CreateTDPersonUserIntfSVC extends CSBizService
{

    private static final long serialVersionUID = 1L;

    protected static Logger log = Logger.getLogger(CreateTDPersonUserIntfSVC.class);
    
    /**
     * 1、无线固话个人开户手机号码校验接口
     * @author yanwu
     * @param input: SERIAL_NUMBER
     * @service SS.CreateTDPersonUserIntfSVC.checkSerialNumber
     * @return IData
     * @throws Exception
     */
    public IData checkSerialNumber(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IData callResult = new DataMap();
    	if("".equals(serialNumber)){
    		//CSAppException.apperr(CrmCommException.CRM_COMM_103, "-1:请输入服务号码!");
    		callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", "输入号码有误！");
    	}
    	
        try {
            IDataset retDataset = ResCall.checkResourceForMphone("0", serialNumber, "0");
            // 处理密码卡（如果是预配或预开就取密码卡信息，调用SIM卡选择接口）
            callResult = retDataset.first();
            String simCardNo = callResult.getString("SIM_CARD_NO", ""); // SIM卡
            String preOpenTag = callResult.getString("PREOPEN_TAG", "0"); // 预开
            String preCodeTag = callResult.getString("PRECODE_TAG", "0"); // 预配
            if (StringUtils.isNotBlank(simCardNo) && (StringUtils.equals("1", preOpenTag) || StringUtils.equals("1", preCodeTag)))
            {
            	try {
            		IDataset simCardSet = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "");
            		callResult.put("simCard", simCardSet);
                    callResult.put("IsBindSimCardNo", "0");
				} catch (Exception e) {
					callResult.put("SIM_CARD_NO", "");
					callResult.put("IsBindSimCardNo", "-1");
				}
            }else{
            	callResult.put("IsBindSimCardNo", "-1");
            }
            
        	//callResult = CSAppCall.call( "SS.CreateTDPersonUserSVC.checkSerialNumber", input).getData(0);
        	callResult.put("SERIAL_NUMBER", serialNumber);
        	callResult.put("X_RESULTCODE", "0");
        	callResult.put("X_RESULTINFO", "Success");
        	/*String simCardNo = callResult.getString("SIM_CARD_NO", ""); // SIM卡
            String preOpenTag = callResult.getString("PREOPEN_TAG", "0"); // 预开
            String preCodeTag = callResult.getString("PRECODE_TAG", "0"); // 预配
            if (StringUtils.isNotBlank(simCardNo) && (StringUtils.equals("1", preOpenTag) || StringUtils.equals("1", preCodeTag)))
            {
            	callResult.put("SIM_CARD_NO", simCardNo);
            }else{
            	callResult.put("SIM_CARD_NO", null);
            }*/
		} catch (Exception e) {
			callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", e.getMessage());
		}
		
		return callResult;
    }
    
    /**
     * 2、无线固话个人开户手机号码校验是否绑定USIM卡接口
     * @author yanwu
     * @param input: SERIAL_NUMBER
     * @service SS.CreateTDPersonUserIntfSVC.checkIsBindSimCardNo
     * @return IData
     * @throws Exception
     */
    public IData checkIsBindSimCardNo(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	IData callResult = new DataMap();
    	if("".equals(serialNumber)){
    		//CSAppException.apperr(CrmCommException.CRM_COMM_103, "-1:请输入服务号码!");
    		callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", "输入号码有误！");
    	}
    	
        try {
        	IDataset retDataset = ResCall.checkResourceForMphone("0", serialNumber, "0");
            // 处理密码卡（如果是预配或预开就取密码卡信息，调用SIM卡选择接口）
            callResult = retDataset.first();
            String simCardNo = callResult.getString("SIM_CARD_NO", ""); // SIM卡
            String preOpenTag = callResult.getString("PREOPEN_TAG", "0"); // 预开
            String preCodeTag = callResult.getString("PRECODE_TAG", "0"); // 预配
            if (StringUtils.isNotBlank(simCardNo) && (StringUtils.equals("1", preOpenTag) || StringUtils.equals("1", preCodeTag)))
            {
            	try {
            		IDataset simCardSet = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "");
            		callResult.put("simCard", simCardSet);
                    callResult.put("IsBindSimCardNo", "0");
				} catch (Exception e) {
					callResult.put("IsBindSimCardNo", "-1");
				}
            }else{
            	callResult.put("IsBindSimCardNo", "-1");
            }
            callResult.put("X_RESULTCODE", "0");
        	callResult.put("X_RESULTINFO", "Success");
		} catch (Exception e) {
			callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", e.getMessage());
		}
		
		return callResult;
    	
    }

    /**
     * 3、无线固话个人开户手机USIM卡校验接口
     * @author yanwu
     * @param input: SERIAL_NUMBER,SIM_CARD_NO
     * @service SS.CreateTDPersonUserIntfSVC.checkSimCardNo
     * @return IData
     * @throws Exception
     */
    public IData checkSimCardNo(IData input) throws Exception
    {
    	String serialNumber = input.getString("SERIAL_NUMBER");
        String simCardNo = input.getString("SIM_CARD_NO");
    	IData callResult = new DataMap();
    	if("".equals(serialNumber) || "".equals(simCardNo)){
    		//CSAppException.apperr(CrmCommException.CRM_COMM_103, "-1:请输入服务号码!");
    		callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", "输入号码有误！");
    	}
    	
        try {
        	//callResult = CSAppCall.call( "SS.CreateTDPersonUserSVC.checkSimCardNo", input).getData(0);
        	callResult = ResCall.checkResourceForSim("0", serialNumber, simCardNo, "").getData(0);
        	callResult.put("SERIAL_NUMBER", serialNumber);
        	callResult.put("X_RESULTCODE", "0");
        	callResult.put("X_RESULTINFO", "Success");
		} catch (Exception e) {
			callResult.put("X_RESULTCODE", "-1");
        	callResult.put("X_RESULTINFO", e.getMessage());
		}
		
		return callResult;
    }
    
    /**
     * 4、无线固话个人开户查询品牌接口
     * @author yanwu
     * @param data: PARENT_PTYPE_CODE
     * @service SS.CreateTDPersonUserIntfSVC.queryBrands
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryBrands(IData data) throws Exception{
    	//String strParentPtypeCode = data.getString("PARENT_PTYPE_CODE"); if( !"".equals(strParentPtypeCode) )
//modify by lijun17   	IDataset productTypeList = ProductInfoQry.getProductsType("5000", null);// 商务电话产品类型;
    	return UProductInfoQry.getProductsType("5000", null);// 商务电话产品类型;
    }
    
    /**
     * 5、无线固话个人开户查询产品接口
     * @author yanwu
     * @param data: PRODUCT_TYPE_CODE
     * @service SS.CreateTDPersonUserIntfSVC.queryPruducts
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryPruducts(IData data) throws Exception{
    	
    	IData inputParam = new DataMap();
        inputParam.put("PRODUCT_TYPE_CODE", data.getString("PRODUCT_TYPE_CODE",""));
        inputParam.put("USER_PRODUCT_ID", "");
        inputParam.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        
        IDataset transProducts = CSAppCall.call("CS.ProductTreeSVC.getCanTransProduct", inputParam);
        return transProducts;
    }

    /**
     * 6、无线固话个人开户查询产品费用接口
     * @author yanwu
     * @param input: PRODUCT_ID
     * @service SS.CreateTDPersonUserIntfSVC.getProductFeeInfo
     * @return IDataset
     * @throws Exception
     */
    public IDataset getProductFeeInfo(IData input) throws Exception
    {
        String product_id = input.getString("PRODUCT_ID");
        String eparchy_code = CSBizBean.getUserEparchyCode();
//        IDataset dataset = ProductFeeInfoQry.getProductFeeInfo("3820", product_id, "-1", "-1", "P", "3", eparchy_code);
        IDataset dataset = UpcCall.qryDynamicPrice(product_id, BofConst.ELEMENT_TYPE_CODE_PRODUCT, "-1", null, "3820", null, null, null);
        if(IDataUtil.isNotEmpty(dataset))
        {
            for(Object obj : dataset)
            {
                IData feeInfo = (IData) obj;
                feeInfo.put("FEE_MODE", feeInfo.getString("FEE_TYPE"));
            }
        }
        return dataset;
    }
    
    /**
     * 7、无线固话个人开户查询默认服务接口
     * @author yanwu
     * @param data: PRODUCT_ID
     * @service SS.CreateTDPersonUserIntfSVC.queryServiceByPruductId
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryServices(IData data) throws Exception{
    	
    	String product_id = data.getString("PRODUCT_ID");
        String eparchyCode = data.getString("TRADE_EPARCHY_CODE",CSBizBean.getTradeEparchyCode());

        IData productInfo = UProductInfoQry.getProductInfo(product_id);
        if (IDataUtil.isEmpty(productInfo))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_135, product_id);
        }
        //默认服务处理
//modify by lijun17        IDataset svcElems = PkgElemInfoQry.querySvcForceElementsByProductId(product_id, eparchyCode);
        IDataset svcElems = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_SVC);
        if ( IDataUtil.isEmpty(svcElems) )
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_192, product_id);
        }
        return svcElems;
    }
    
    /**
     * 8、无线固话个人开户查询默认优惠接口
     * @author yanwu
     * @param input: PRODUCT_ID
     * @service SS.CreateTDPersonUserIntfSVC.queryDiscntByPruductId
     * @return IDataset
     * @throws Exception
     */
    public IDataset queryDiscnts(IData data) throws Exception{
    	
    	String product_id = data.getString("PRODUCT_ID","");
    	if("".equals(product_id)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"-1:请输入产品编号!");
		}
//modify by lijun17    	IDataset discntinfo = PkgElemInfoQry.queryDiscntForceElementsByProductId(pruduct_id, CSBizBean.getTradeEparchyCode());
    	IDataset discntinfo = UPackageElementInfoQry.queryOfferForceElementsByProductId(BofConst.ELEMENT_TYPE_CODE_PRODUCT, product_id, BofConst.ELEMENT_TYPE_CODE_DISCNT);
    	return discntinfo;
    }

    /**
     * 9、无线固话个人开户办理接口
     * @author yanwu
     * @param input: IData
     * @service SS.CreateTDPersonUserIntfSVC.openTDPersonUser
     * @return IData
     * @throws Exception
     */
	public IDataset openTDPersonUser(IData data) throws Exception {
		
		IDataUtil.chkParam(data, "SERIAL_NUMBER");
		IDataUtil.chkParam(data, "SIM_CARD_NO");
		IDataUtil.chkParam(data, "PSPT_TYPE_CODE");
		IDataUtil.chkParam(data, "PSPT_ID");
		IDataUtil.chkParam(data, "CUST_NAME");
		IDataUtil.chkParam(data, "PSPT_ADDR");
		IDataUtil.chkParam(data, "BIRTHDAY");
		//IDataUtil.chkParam(data, "USER_TYPE_CODE");
		IDataUtil.chkParam(data, "PHONE");
		IDataUtil.chkParam(data, "ACCT_DAY");
		IDataUtil.chkParam(data, "PAY_NAME");
		IDataUtil.chkParam(data, "PAY_MODE_CODE");
		IDataUtil.chkParam(data, "DEFAULT_PWD_FLAG");
		IDataUtil.chkParam(data, "USER_PASSWD");
		IDataUtil.chkParam(data, "PRODUCT_ID");
		//IDataUtil.chkParam(data, "BRAND_CODE");
		
		//String strSn = data.getString("SERIAL_NUMBER");
		//String strScn = data.getString("SIM_CARD_NO");
		//IData Sns = checkSerialNumber(data);
		/*Sns.putAll(SimCards);
		Sns.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
		Sns.put("SIM_CARD_NO", data.getString("SIM_CARD_NO"));
		Sns.put("DEFAULT_PWD_FLAG", data.getString("DEFAULT_PWD_FLAG"));
		Sns.put("USER_TYPE_CODE", data.getString("USER_TYPE_CODE"));
		Sns.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
		Sns.put("USER_PASSWD", data.getString("USER_PASSWD"));
		Sns.put("PSPT_TYPE_CODE", data.getString("PSPT_TYPE_CODE"));
		Sns.put("PSPT_ID", data.getString("PSPT_ID"));
		Sns.put("CUST_NAME", data.getString("CUST_NAME"));
		Sns.put("PSPT_ADDR", data.getString("PSPT_ADDR"));
		Sns.put("BIRTHDAY", data.getString("BIRTHDAY"));
		Sns.put("PHONE", data.getString("PHONE"));
		Sns.put("ACCT_DAY", data.getString("ACCT_DAY"));
		Sns.put("PAY_NAME", data.getString("PAY_NAME"));
		Sns.put("PAY_MODE_CODE", data.getString("PAY_MODE_CODE"));
		data.putAll(Sns);*/
		//data.put("SERIAL_NUMBER", strSn);
		//data.put("SIM_CARD_NO", strScn);
		IData SimCards = checkSimCardNo(data);
	    data.put("IMSI", SimCards.getString("IMSI", ""));
		data.put("KI", SimCards.getString("KI", ""));
		data.put("RES_KIND_CODE", SimCards.getString("RES_KIND_CODE", ""));
		data.put("RES_TYPE_CODE", SimCards.getString("RES_TYPE_CODE", ""));
		data.put("CARD_PASSWD", SimCards.getString("CARD_PASSWD", ""));
		data.put("PASSCODE", SimCards.getString("PASSCODE", ""));
		data.put("REAL_NAME", "0");
		data.put("TRADE_TYPE_CODE", "3820");
		data.put("ORDER_TYPE_CODE", "3820");
		//data.put("TRADE_DEPART_PASSWD", data.getString("TRADE_DEPART_PASSWD","0"));//孙鑫说没用到，但被校验不能空，所以随便赋值了
		//data.put("CITY_CODE", data.getString("TRADE_CITY_CODE"));
		data.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode());
		
		if (!"0".equals(data.getString("PAY_MODE_CODE"))) {
			IDataUtil.chkParam(data, "SUPER_BANK_CODE");
			IDataUtil.chkParam(data, "BANK_CODE");
			IDataUtil.chkParam(data, "BANK_ACCT_NO");
		}

		IDataset SE = new DatasetList();
		String strProid = data.getString("PRODUCT_ID");
		IDataset services = queryServices(data);
		if( IDataUtil.isNotEmpty(services) ){
			for (int i = 0; i < services.size(); i++) {
				IData service = services.getData(i);
				service.put("START_DATE", SysDateMgr.getSysTime());
				service.put("ELEMENT_TYPE_CODE", "S");
				service.put("PRODUCT_ID", strProid);
				service.put("PACKAGE_ID", service.getString("PACKAGE_ID"));
				service.put("ELEMENT_ID", service.getString("SERVICE_ID"));
				service.put("MODIFY_TAG", "0");
				service.put("END_DATE", SysDateMgr.getTheLastTime());
				service.put("INST_ID", "");
				SE.add(service);
			}
		}
		
		IDataset discnts = queryDiscnts(data);
		if( IDataUtil.isNotEmpty(discnts) ){
			for (int i = 0; i < discnts.size(); i++) {
				IData discnt = discnts.getData(i);
				discnt.put("START_DATE", SysDateMgr.getSysTime());
				discnt.put("ELEMENT_TYPE_CODE", "D");
				discnt.put("PRODUCT_ID", strProid);
				discnt.put("PACKAGE_ID", discnt.getString("PACKAGE_ID"));
				discnt.put("ELEMENT_ID", discnt.getString("DISCNT_CODE"));
				discnt.put("MODIFY_TAG", "0");
				discnt.put("END_DATE", SysDateMgr.getTheLastTime());
				discnt.put("INST_ID", "");
				SE.add(discnt);
			}
		}
		data.put("SELECTED_ELEMENTS", SE);

		//调用开户接口
		//data.put(Route.ROUTE_EPARCHY_CODE, this.getTradeEparchyCode());
		//String svcName = "SS.CreateTDPersonUserRegSVC.tradeReg";
		IDataset transProducts = CSAppCall.call("SS.CreateTDPersonUserRegSVC.tradeReg", data);
        return transProducts;
	}
}
