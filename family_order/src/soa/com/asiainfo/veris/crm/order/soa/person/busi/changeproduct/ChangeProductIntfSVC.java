
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ailk.service.bean.BeanManager;
import com.ailk.service.session.SessionManager;
import org.apache.log4j.Logger;

import com.ailk.biz.util.TimeUtil;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateFormatUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcStateTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.TradeProcess;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustPersonInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;

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
	private static final Logger logger = Logger.getLogger(ChangeProductIntfSVC.class);
	
    /**
     * @Description: 自动订购GPRS加油包
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 24, 2014 9:18:11 AM
     */
    public IDataset autoAddRGprsDiscnt(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");
        IDataUtil.chkParam(input, "ELEMENT_COUNT");

        String serialNumber = input.getString("SERIAL_NUMBER");
        String elementId = input.getString("ELEMENT_ID");
        int elementCount = Integer.parseInt(input.getString("ELEMENT_COUNT"));

        IDataset elements = new DatasetList();

        for (int i = 0; i < elementCount; i++)
        {
            IData element = new DataMap();
            element.clear();

            element.put("ELEMENT_ID", elementId);
            element.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
            element.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);

            elements.add(element);
        }

        IData callData = new DataMap();
        callData.clear();

        callData.put("SERIAL_NUMBER", serialNumber);
        callData.put("ELEMENTS", elements);

        return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", callData);
    }

    /**
     * @Description: 产品变更校验
     * @param input
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 24, 2014 10:19:19 AM
     */
    public IData changeProductCheck(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性

        if (!"P".equals(input.getString("ELEMENT_TYPE_CODE")))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_515);
        }

        if (!"0".equals(input.getString("MODIFY_TAG")))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_516);
        }

        OrderDataBus dataBus = DataBusManager.getDataBus();
        dataBus.setOrderTypeCode("110");
        dataBus.setAcceptTime(SysDateMgr.getSysDate());

        input.put("TRADE_TYPE_CODE", "110");
        input.put("X_TRANS_CODE", "SS.ChangeProductRegSVC.ChangeProduct");

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

        String newProductId = input.getString("ELEMENT_ID");
        String newProductName = UProductInfoQry.getProductNameByProductId(newProductId);

        IData result = new DataMap();
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

        String userId = btd.getRD().getUca().getUserId();
        String eparchyCode = btd.getRD().getUca().getUserEparchyCode();
        String bookingDate = input.getString("START_DATE", "");

        IDataset userRelation = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "20", null);
        if (IDataUtil.isNotEmpty(userRelation))
        {
//        	IDataset userVpmnDiscnt = UserDiscntInfoQry.getUserVPMNDiscnt(userId, "20", "80000102");
                     
        	IDataset userVpmnDiscnt=new DatasetList();
        	         
        //查询用户所有优惠	
            IDataset userdiscntAll= UserDiscntInfoQry.getUserVPMNDiscntUpc(userId, "20", "80000102");//获取用户的优惠
            		
        //查询产品包下所有元素   
            IDataset elemtntAll=UPackageElementInfoQry.getPackageElementInfoByPackageId("80000102");
       //过滤是否有Vpmn数据
            
            for(int j=0; j<userdiscntAll.size();j++){
     	       IData temp = userdiscntAll.getData(j);
	     	      for(int k=0; k<elemtntAll.size();k++){
	     	    	     String  prarmdiscode=elemtntAll.getData(k).getString("ELEMENT_ID");
	     	    	     if(temp.getString("DISCNT_CODE").equals(prarmdiscode)){
	     	    	    	userVpmnDiscnt.add(temp);
	     	    	     }
	     	         }
              }
		
            if (IDataUtil.isNotEmpty(userVpmnDiscnt))
            {
                String oldVpmnDiscnt = userVpmnDiscnt.getData(0).getString("DISCNT_CODE");
                String vpmnUserIdA = userVpmnDiscnt.getData(0).getString("USER_ID_A");
              //vpmnProductId 在后续方法中没有使用到，就按现在的值传，因为现有userDiscnt里面的product_id & package_id都是 -1
                //如后续需要使用需要从 OFFER_REL关系表中取出product_id add by hefeng
                String vpmnProductId = userVpmnDiscnt.getData(0).getString("PRODUCT_ID");
                

                if (StringUtils.isNotBlank(oldVpmnDiscnt))
                {
                    IData newVpmnDiscntData = new ChangeProductBean().getNewVpmnDiscnt(newProductId, oldVpmnDiscnt, eparchyCode, bookingDate, vpmnUserIdA, vpmnProductId);

                    if (IDataUtil.isNotEmpty(newVpmnDiscntData))
                    {
                        IDataset newVpmnDiscntDatas = newVpmnDiscntData.getDataset("NEW_VPMN_DISCNT");

                        result.put("VPMN_TAG", "1");
                        result.put("VPMN_LIST", newVpmnDiscntDatas);

                        String elementNames = new String();
                        String elementIds = new String();

                        for (int i = 0, size = newVpmnDiscntDatas.size(); i < size; i++)
                        {
                            IData newData = newVpmnDiscntDatas.getData(i);

                            String elementId = newData.getString("ELEMENT_ID", "");
                            String elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(elementId);

                            elementIds += elementId + ",";
                            elementNames += elementName + ",";
                        }
                        if (StringUtils.isNotBlank(elementIds) && StringUtils.isNotBlank(elementNames))
                        {
                            result.put("RSRV_STR7", elementIds.substring(0, elementIds.length() - 1));
                            result.put("RSRV_STR8", elementNames.substring(0, elementNames.length() - 1));
                            result.put("DEAL_TAG", "1");
                        }
                    }
                }
            }
        }

        return result;
    }

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
     * @Description: 假日优惠效验接口
     * @param input
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jul 24, 2014 10:19:19 AM
     */
    public IData checkHolidayDiscnt(IData input) throws Exception
    {
    	IData result=new DataMap();
    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        
    	result.put("DATA_CODE", "0");
    	
        //获取用户资料失败
        IData userIfno=UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isEmpty(userIfno)){
        	result.put("DATA_CODE", "1211");
        	result.put("DATE_INFO", "获取该用户资料失败！");
        	
        	return result;
        }
        
        //获取可以办理的套餐信息
        IDataset holidayDiscnts=CommparaInfoQry.queryValidHolidayDiscnt();
        if(IDataUtil.isEmpty(holidayDiscnts)){
        	/*
        	 * 获取最近已经过期的办理信息
        	 */
        	//如果已经过期的获取失败，获取马上可以办理的套餐信息
    		IDataset unMaxExpireDiscnts=CommparaInfoQry.queryUnExpireMaxHolidayDiscnt();
    		if(IDataUtil.isNotEmpty(unMaxExpireDiscnts)){
    			IData unMaxExpireDiscnt=unMaxExpireDiscnts.getData(0);
        		
    			result.put("DATA_CODE", "180522");
            	result.put("DATE_INFO", "不在假日优惠的有效办理时间之内！");
        		result.put("DISCNT_CODE", unMaxExpireDiscnt.getString("PARAM_CODE",""));
        		result.put("DISCNT_NAME", unMaxExpireDiscnt.getString("PARA_CODE2",""));
        		result.put("START_DATE", unMaxExpireDiscnt.getString("PARA_CODE4",""));
        		result.put("END_DATE", unMaxExpireDiscnt.getString("PARA_CODE5",""));
        		
        		return result;
    		}else{
    			//如果没有临近未到的优惠
    			result.put("DATA_CODE", "180523");
            	result.put("DATE_INFO", "不在假日优惠的有效办理时间之内！");
        		
        		return result;
    		}
    		
        }
        
        String strDiscntCode = holidayDiscnts.getData(0).getString("PARAM_CODE");
        String strDiscntName = holidayDiscnts.getData(0).getString("PARA_CODE2");
        String strBeginDate = holidayDiscnts.getData(0).getString("PARA_CODE4");
        String strEndDate = holidayDiscnts.getData(0).getString("PARA_CODE5");
        for (int i = 0; i < holidayDiscnts.size(); i++) {
        	IData idholidayDiscnt = holidayDiscnts.getData(i);
        	String strPARA_CODE3 = idholidayDiscnt.getString("PARA_CODE3", "");
        	if("".equals(strPARA_CODE3)){
        		strDiscntCode = idholidayDiscnt.getString("PARAM_CODE");
        		strDiscntName = idholidayDiscnt.getString("PARA_CODE2");
        		strBeginDate = idholidayDiscnt.getString("PARA_CODE4");
        		strEndDate = idholidayDiscnt.getString("PARA_CODE5");
        	}
        	
		}
        
        
        //验证用户是否已经办理过这个套餐
        String userId=userIfno.getString("USER_ID");
        IDataset userDiscnt=UserDiscntInfoQry.getDiscntsByUserIdDiscntCode(userId, strDiscntCode, CSBizBean.getUserEparchyCode());
        if(IDataUtil.isNotEmpty(userDiscnt)){
        	result.put("DATA_CODE", "180521");
        	result.put("DATE_INFO", "用户已经办理了优惠套餐【"+strDiscntCode+"】！");
        	result.put("DISCNT_CODE", strDiscntCode);//holidayDiscnts.getData(0).getString("PARAM_CODE","")
    		result.put("DISCNT_NAME", strDiscntName);//holidayDiscnts.getData(0).getString("PARA_CODE2","")
    		result.put("START_DATE", strBeginDate);//holidayDiscnts.getData(0).getString("PARA_CODE4","")
    		result.put("END_DATE", strEndDate);//holidayDiscnts.getData(0).getString("PARA_CODE5","")
        	
        	return result;
        }
        
        /*IData dateLimitConfig=holidayDiscnts.getData(0);
		String beginDate=dateLimitConfig.getString("PARA_CODE4", "");
		String endDate=dateLimitConfig.getString("PARA_CODE5", "");*/
		
		if(StringUtils.isBlank(strBeginDate)||StringUtils.isBlank(strEndDate)){
			result.put("DATA_CODE", "180523");
        	result.put("DATE_INFO", "commpara时间配置异常！");
        	
        	return result;
		}
		
		result.put("DATE_INFO","");
		result.put("DISCNT_CODE", strDiscntCode);
		result.put("DISCNT_NAME", strDiscntName);//dateLimitConfig.getString("PARA_CODE2","")
		result.put("START_DATE", strBeginDate);
		result.put("END_DATE", strEndDate);
		
        return result;
    }
    
    /**
     * @Description: 办理来电显示服务校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkAddCallerDiscnt(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性
        
    	IData result = new DataMap();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	String strElementID = input.getString("ELEMENT_ID");
    	String strElementTypeCode = input.getString("ELEMENT_TYPE_CODE");
    	String strModifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
    	
    	if(!"23".equals(strElementID) || !"S".equals(strElementTypeCode) || !"0".equals(strModifyTag))
    	{
    		result.put("X_RESULTCODE", "2016113001");
        	result.put("X_RESULTINFO", "传参不对，请检查参数");
        	return result;
    	}

    	IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
    	//UcaData uca = UcaDataFactory.getNormalUca(strSerialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该号码【%s】没有有效的用户信息！", strSerialNumber);
    		result.put("X_RESULTCODE", "2016120701");
        	result.put("X_RESULTINFO", strError);
        	return result;
    	}
    	String strUserId = userInfo.getString("USER_ID");
    	//List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(strElementID);
    	boolean bSvc = UserSvcInfoQry.getAutoPayContractState(strUserId, strElementID);
		if(bSvc)
		{
			result.put("X_RESULTCODE", "2016113002");
        	result.put("X_RESULTINFO", "来电显示该服务已经存在");
        	return result;
		}
		
		boolean bIsHave = false;
		
		IDataset discntDatas = UserDiscntInfoQry.queryUserDiscntV(strUserId, "4200");
		if(IDataUtil.isNotEmpty(discntDatas))
		{
			bIsHave = true;
		}
		else 
		{
			//限制9970 TD_S_COMMPARA配置编码
			IDataset idsCompare9970 = CommparaInfoQry.getCommparaAllCol("CSM", "9970", "checkCallerDiscnt", "0898");
			if(IDataUtil.isNotEmpty(idsCompare9970))
			{
				for (int i = 0; i < idsCompare9970.size(); i++) 
				{
					IData idCompare9970 = idsCompare9970.getData(i);
					String strElementIDC = idCompare9970.getString("PARA_CODE1", "");
					String strElementTypeCodeC = idCompare9970.getString("PARA_CODE2", "");
					if("P".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
					{
						IDataset lsProducts = UserProductInfoQry.getUserProductByUserIdProductId(strUserId, strElementIDC);
						if(IDataUtil.isNotEmpty(lsProducts))
						{
							bIsHave = true;
							break;
						}
					}
					else if("S".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
					{
						//List<SvcTradeData> lsSvcs = uca.getUserSvcBySvcId(strElementIDC);
						boolean bSvcs = UserSvcInfoQry.getAutoPayContractState(strUserId, strElementIDC);
						if(bSvcs)
						{
							bIsHave = true;
							break;
						}
					}
					else if("D".equals(strElementTypeCodeC) && StringUtils.isNotBlank(strElementIDC))
					{
						//List<DiscntTradeData> lsDiscnts = uca.getUserDiscntByDiscntId(strElementIDC);
						IDataset lsDiscnts = UserDiscntInfoQry.queryUserDiscntV(strUserId, strElementIDC);
						if(IDataUtil.isNotEmpty(lsDiscnts))
						{
							bIsHave = true;
							break;
						}
					}
				}
			}
		}
		
		if(bIsHave)
		{
			result.put("DISCNT_CODE_A", "0");
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
		else
		{
			result.put("DISCNT_CODE_A", "1"); 
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
    	return result;
    }
    
    /**
     * @Description: 取消来电显示服务校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkDelCallerDiscnt(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性
        
    	IData result = new DataMap();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	String strElementID = input.getString("ELEMENT_ID");
    	String strElementTypeCode = input.getString("ELEMENT_TYPE_CODE");
    	String strModifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
    	
    	if(!"23".equals(strElementID) || !"S".equals(strElementTypeCode) || !"1".equals(strModifyTag))
    	{
    		result.put("X_RESULTCODE", "2016113001");
        	result.put("X_RESULTINFO", "传参不对，请检查参数");
        	return result;
    	}

    	//UcaData uca = UcaDataFactory.getNormalUca(strSerialNumber);
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该号码【%s】没有有效的用户信息！", strSerialNumber);
    		result.put("X_RESULTCODE", "2016120701");
        	result.put("X_RESULTINFO", strError);
        	return result;
    	}
    	
    	String strUserId = userInfo.getString("USER_ID");
    	//List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(strElementID);
    	boolean bSvc = UserSvcInfoQry.getAutoPayContractState(strUserId, strElementID);
		if(!bSvc)
		{
			result.put("X_RESULTCODE", "2016113003");
        	result.put("X_RESULTINFO", "来电显示该服务不存在");
        	return result;
		}
		
		boolean bIsHave = false;
		
		//List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId("4200");
		IDataset discntDatas = UserDiscntInfoQry.queryUserDiscntV(strUserId, "4200");
		if(IDataUtil.isNotEmpty(discntDatas))
		{
			bIsHave = true;
		}
		
		if(bIsHave)
		{
			result.put("DISCNT_CODE_A", "1");
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
		else
		{
			result.put("DISCNT_CODE_A", "0"); 
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
    	return result;
    }
    
    /**
     * @Description: 新增彩显优惠包校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkAddCsDiscnt(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性
        
    	IData result = new DataMap();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	String strElementID = input.getString("ELEMENT_ID");
    	String strElementTypeCode = input.getString("ELEMENT_TYPE_CODE");
    	String strModifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
    	
    	if(!"4200".equals(strElementID) || !"D".equals(strElementTypeCode) || !"0".equals(strModifyTag))
    	{
    		result.put("X_RESULTCODE", "2016113001");
        	result.put("X_RESULTINFO", "传参不对，请检查参数");
        	return result;
    	}

    	//UcaData uca = UcaDataFactory.getNormalUca(strSerialNumber);
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该号码【%s】没有有效的用户信息！", strSerialNumber);
    		result.put("X_RESULTCODE", "2016120701");
        	result.put("X_RESULTINFO", strError);
        	return result;
    	}

    	String strUserId = userInfo.getString("USER_ID");
    	IDataset discntDatas = UserDiscntInfoQry.queryUserDiscntV(strUserId, strElementID);
		if(IDataUtil.isNotEmpty(discntDatas))
		{
			result.put("X_RESULTCODE", "2016113005");
        	result.put("X_RESULTINFO", "彩显优惠包已经存在");
        	return result;
		}
		
		boolean bIsHave = false;
		//List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("23");
		boolean bSvc = UserSvcInfoQry.getAutoPayContractState(strUserId, "23");
		if(bSvc)
		{
			bIsHave = true;
		}
		
		if(bIsHave)
		{
			result.put("DISCNT_CODE_A", "0");
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
		else
		{
			result.put("DISCNT_CODE_A", "1"); 
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
    	return result;
    }
    
    /**
     * @Description: 取消彩显优惠包校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkDelCsDiscnt(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "SERIAL_NUMBER");
        IDataUtil.chkParam(input, "ELEMENT_ID");// 服务ID或优惠ID或产品ID
        IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// S-服务；D-优惠；P-产品
        IDataUtil.chkParam(input, "MODIFY_TAG");// 0-新增；1-删除；2-修改属性
        
    	IData result = new DataMap();
    	String strSerialNumber = input.getString("SERIAL_NUMBER");
    	String strElementID = input.getString("ELEMENT_ID");
    	String strElementTypeCode = input.getString("ELEMENT_TYPE_CODE");
    	String strModifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");
    	
    	if(!"4200".equals(strElementID) || !"D".equals(strElementTypeCode) || !"1".equals(strModifyTag))
    	{
    		result.put("X_RESULTCODE", "2016113001");
        	result.put("X_RESULTINFO", "传参不对，请检查参数");
        	return result;
    	}

    	//UcaData uca = UcaDataFactory.getNormalUca(strSerialNumber);
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该号码【%s】没有有效的用户信息！", strSerialNumber);
    		result.put("X_RESULTCODE", "2016120701");
        	result.put("X_RESULTINFO", strError);
        	return result;
    	}
    	
    	//List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId(strElementID);
    	String strUserId = userInfo.getString("USER_ID");
    	IDataset discntDatas = UserDiscntInfoQry.queryUserDiscntV(strUserId, strElementID);
		if(IDataUtil.isEmpty(discntDatas))
		{
			result.put("X_RESULTCODE", "2016113004");
        	result.put("X_RESULTINFO", "彩显优惠包不存在");
        	return result;
		}
		
		boolean bIsHave = false;
		//List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("23");
		boolean bSvc = UserSvcInfoQry.getAutoPayContractState(strUserId, "23");
		if(bSvc)
		{
			bIsHave = true;
		}
		
		if(bIsHave)
		{
			result.put("DISCNT_CODE_A", "1");
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
		else
		{
			result.put("DISCNT_CODE_A", "0"); 
			result.put("X_RESULTCODE", "0");
        	result.put("X_RESULTINFO", "OK");
		}
    	return result;
    }
    /**
     * 国际长途开通关闭接口
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
    public IDataset openCloseInternationalCall(IData input)throws Exception{
        
        
        String oprcode = IDataUtil.chkParam(input, "OPR_CODE"); //00:功能开通 , 01：功能关闭
       
     
        if("00".equals(oprcode))
        { 
            input.put("MODIFY_TAG", "0");
        }
        else  if("01".equals(oprcode))
        {
            input.put("MODIFY_TAG", "1");
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"不支持的操作类型["+oprcode+"]！");
        }
        
        if (StringUtils.isNotBlank(input.getString("EXPIRE_TIME")))
        {
            String expireTime=dataToData(input.getString("EXPIRE_TIME"), "yyyy-MM-dd HH:mm:ss"); 
            input.put("END_DATE", expireTime);
        }
        
        IDataset result = changeInterCall(input);
        
        if(result!=null&&result.size()>0)
        {
            result.getData(0).putAll(input);
    
            //从TF_B_TRADE_SVC表查出服务开通/关闭生效时间和终止时间
            String tradeId = result.getData(0).getString("TRADE_ID");
            IData param = new DataMap(); 
            param.put("TRADE_ID", tradeId);
            param.put("SERVICE_ID", "15");
            SQLParser parser = new SQLParser(param); 
            parser.addSQL(" select to_char(t.start_date,'yyyymmddhh24miss') as VALID_DATE ,to_char(t.end_date,'yyyymmddhh24miss') as EXPIRE_DATE ");
            parser.addSQL(" from TF_B_TRADE_SVC t ");
            parser.addSQL(" WHERE t.trade_id = :TRADE_ID ");
            parser.addSQL(" AND t.SERVICE_ID = :SERVICE_ID ");
            IDataset ids = Dao.qryByParse(parser,Route.getJourDb()); 
            result.getData(0).putAll(ids.getData(0));
        
        } 
        
        return result;
    }

    public static IDataset changeInterCall(IData input) throws Exception
    {
        IDataset selectedElements = new DatasetList();
        
        if ("0".equals(input.getString("MODIFY_TAG")))
        { 
            input.put("MODIFY_TAG", "0"); 
            input.put("END_DATE", SysDateMgr.END_DATE_FOREVER); 
            selectedElements.addAll(buildChangeProdOrder(input, "15")); 
            
            input.put("MODIFY_TAG", "1");  
            input.put("DELCONTINUE", Boolean.TRUE);
            selectedElements.addAll(buildChangeProdOrder(input, "14")); 
        }
        else if ("1".equals(input.getString("MODIFY_TAG")))
        {
            input.put("MODIFY_TAG", "1"); 
            selectedElements.addAll(buildChangeProdOrder(input, "15")); 
            
            input.put("MODIFY_TAG", "0"); 
            input.put("ADDCONTINUE", Boolean.TRUE);
            input.put("END_DATE", SysDateMgr.END_DATE_FOREVER); 
            selectedElements.addAll(buildChangeProdOrder(input, "14")); 
        } 
      
        
        IData inutParam = new DataMap();
        inutParam.put("SELECTED_ELEMENTS", selectedElements); 
        inutParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        
        IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", inutParam);
        return result;
    }

    public static IDataset buildChangeProdOrder(IData input, String elemIds) throws Exception
    {

        UcaData ud = UcaDataFactory.getNormalUca(IDataUtil.chkParam(input, "SERIAL_NUMBER"));

        IDataset selectedElements = new DatasetList();

        String modifyTag = IDataUtil.chkParam(input, "MODIFY_TAG");

        String[] serviceids = elemIds.split(",");

        DataBusManager.getDataBus().setAcceptTime(SysDateMgr.getSysTime());

        ProductTradeData nextProduct = ud.getUserNextMainProduct();

        String productId = "";

        if (nextProduct != null)
        {
            productId = nextProduct.getProductId();
          
        }
        else
        {
            productId = ud.getProductId();
        }
        
        IDataset productElements  = ProductInfoQry.getProductElements(productId, ud.getUserEparchyCode());

        for (int j = 0; j < serviceids.length; j++)
        {
            IData data = new DataMap();

            if ("0".equals(modifyTag))
            {
                data.put("END_DATE", StringUtils.isNotBlank(input.getString("END_DATE")) ? input.getString("END_DATE")
                                                                                        : SysDateMgr.END_DATE_FOREVER);
                data.put("START_DATE",
                         StringUtils.isNotBlank(input.getString("START_DATE")) ? input.getString("START_DATE")
                                                                              : SysDateMgr.getSysTime());

                IDataset pkgElement = DataHelper.filter(productElements, "ELEMENT_TYPE_CODE=S,ELEMENT_ID="
                                                                         + serviceids[j]);

                if (IDataUtil.isEmpty(pkgElement))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户主产品和元素[" + serviceids[j]
                                                                         + "]没有订购关系,不能操作此元素！");
                }
                
                if (StringUtils.isBlank(pkgElement.getData(0).getString("PACKAGE_ID")))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户可订购元素[" + serviceids[j]
                                                                         + "]没有归属产品编码不能为空，请检查配置！");
                }
                data.put("PRODUCT_ID", productId); 
                data.put("PACKAGE_ID", pkgElement.getData(0).getString("PACKAGE_ID"));

                IDataset datasetsvc = UserSvcInfoQry.queryUserSvcByUserId(ud.getUserId(), serviceids[j], null);

                Boolean isCheckAdd = input.getBoolean("ADDCONTINUE", false);

                if (IDataUtil.isNotEmpty(datasetsvc) && isCheckAdd)
                {
                    continue;
                }
                
                data.put("ELEMENT_ID", serviceids[j]);
                data.put("MODIFY_TAG", modifyTag);
                data.put("ELEMENT_TYPE_CODE", "S");
                selectedElements.add(data);
            }
            else if ("1".equals(modifyTag))
            {
                IDataset datasetsvc = UserSvcInfoQry.queryUserSvcByUserId(ud.getUserId(), serviceids[j], null);

                Boolean isCheckDel = input.getBoolean("DELCONTINUE", false);

                if (isCheckDel)
                {
                    if (IDataUtil.isEmpty(datasetsvc))
                    {
                        continue;
                    }
                }

                if (IDataUtil.isEmpty(datasetsvc))
                {
                    //String serviceName = StaticUtil.getStaticValue(BizBean.getVisit(), "TD_B_SERVICE", "SERVICE_ID", "SERVICE_NAME", serviceids[j]);
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户没有开通" + serviceids[j] + "服务，不能执行该操作！");
                }

                for (Iterator iterator = datasetsvc.iterator(); iterator.hasNext();)
                {
                    IData item = (IData) iterator.next();
                    
                    data.put("START_DATE",item.getString("START_DATE"));  
                    data.put("END_DATE",SysDateMgr.getSysTime());   
                    data.put("PRODUCT_ID", item.getString("PRODUCT_ID"));
                    data.put("PACKAGE_ID",item.getString("PACKAGE_ID"));
                    data.put("INST_ID",item.getString("INST_ID")); 
                    data.put("ELEMENT_ID", serviceids[j]);
                    data.put("MODIFY_TAG", modifyTag);
                    data.put("ELEMENT_TYPE_CODE", "S");
                    selectedElements.add(data);   
                    
                } 
            } 
        }

        return selectedElements;
    }

    /**
     * 国漫功能开通/关闭接口(SS.InterRoamingSVC.OpenClose)
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
    public IDataset changeProdGprsRoam(IData input)throws Exception{
        
        String oprcode = input.getString("OPR_CODE");
        
        String servType = input.getString("SERV_TYPE"); 
       
        IDataset result = null;
        if("00".equals(oprcode)){//开通
            
            
            if ("10".equals(servType))
            { 
                
                result =  modifyEndDateByDays(input,30);
               
            }
            else if ( "11".equals(servType))
            { 
                result =   modifyEndDateByDays(input,180);
            }
            else
            {
                result =   modifyEndDateByDays(input,-1);
            } 
            
         }
        else
        { 
 
             //业务受理后特殊业务限制：非自营厅工号不能取消4G上网服务
            
            result = delRoam(input);
        }
        if(result!=null&&result.size()>0){
            result.getData(0).putAll(input);
            
            IData param = new DataMap(); 
            if("00".equals(oprcode))
            {
                param.put("MODIFY_TAG", "0");
            } 
            //从TF_B_TRADE_SVC表查出服务开通/关闭生效时间和终止时间
            String tradeId = result.getData(0).getString("TRADE_ID");
         
            param.put("TRADE_ID", tradeId);
            param.put("SERVICE_ID", "19");
            SQLParser parser = new SQLParser(param); 
            parser.addSQL(" select to_char(t.start_date,'yyyymmddhh24miss') as VALID_DATE ,to_char(t.end_date,'yyyymmddhh24miss') as EXPIRE_DATE ");
            parser.addSQL(" from TF_B_TRADE_SVC t ");
            parser.addSQL(" WHERE t.trade_id = :TRADE_ID ");
            parser.addSQL(" AND t.SERVICE_ID = :SERVICE_ID ");
            parser.addSQL(" AND t.MODIFY_TAG = :MODIFY_TAG ");
            IDataset ids = Dao.qryByParse(parser,Route.getJourDb()); 
            result.getData(0).putAll(ids.getData(0));
        
        }
        
        
        
        return result;
    }

    public static IDataset delRoam(IData input) throws Exception
    {
        IDataset result;
        IData elementInput = new DataMap();
         
        
        elementInput.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        elementInput.put("MODIFY_TAG", "1"); 
        
        IDataset selectedElements = buildChangeProdOrder(elementInput, "19"); 

        elementInput.put("MODIFY_TAG", "0");
        selectedElements.addAll(buildChangeProdOrder(elementInput, "18,14"));

        elementInput.put("DELCONTINUE", Boolean.TRUE);
        elementInput.put("MODIFY_TAG", "1");
        selectedElements.addAll(buildChangeProdOrder(elementInput, "15,18,14"));

        input.put("SELECTED_ELEMENTS", selectedElements);
        input.put("REMARK", "OVER_CHECK");
        result = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", input);
        return result;
    }
	 
	
	  public static IDataset modifyEndDateByDays(IData input, int days) throws Exception
	    {

	    
	        String expireDate = ""; 
	        
	        if (-1 == days)
	        {
	            expireDate = SysDateMgr.END_DATE_FOREVER;
	        }
	        else
	        {
	            expireDate = SysDateMgr.addDays(SysDateMgr.getSysTime(), days) + SysDateMgr.getEndTime235959();
	        }
	        
	        input.put("END_DATE", expireDate);

	        return addRormByEndDate(input);

	    }

    public static IDataset addRormByEndDate(IData input) throws Exception
    {
        IData elementInput = new DataMap(); 

        elementInput.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));


        IDataset selectedElements = new DatasetList();
        
        String expireDate = IDataUtil.chkParam(input, "END_DATE");

        //有国漫则修改，无则开通 

        elementInput.put("MODIFY_TAG", "0");
        elementInput.put("END_DATE", expireDate);
        selectedElements.addAll(buildChangeProdOrder(elementInput, "19"));

        elementInput.put("DELCONTINUE", Boolean.TRUE);
        elementInput.put("MODIFY_TAG", "1");
        selectedElements.addAll(buildChangeProdOrder(elementInput, "19,18,14")); 
        
        if (!SysDateMgr.END_DATE_FOREVER.equals(expireDate))
        {
            elementInput.put("MODIFY_TAG", "0");  
            elementInput.put("ADDCONTINUE",Boolean.FALSE);  
            elementInput.put("START_DATE", SysDateMgr.getNextSecond(expireDate));
            elementInput.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            selectedElements.addAll(buildChangeProdOrder(elementInput, "18"));
        }
        
        elementInput.put("MODIFY_TAG", "0");
        elementInput.put("START_DATE", SysDateMgr.getSysTime());
        elementInput.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
        elementInput.put("ADDCONTINUE", Boolean.TRUE);
        selectedElements.addAll(buildChangeProdOrder(elementInput, "15")); 
        
        IData inutParam = new DataMap();
        inutParam.put("SELECTED_ELEMENTS",processStartDate(selectedElements));
        inutParam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inutParam.put("REMARK", "OVER_CHECK"); 
        
        return CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", inutParam);
    }
    
    /**
     * 处理开始时间重复的问题
     * @param selectedElements
     * @return
     * @throws Exception
     */
    private static IDataset processStartDate(IDataset selectedElements) throws Exception
    {  
        IDataset result = new DatasetList();
        IDataset delElements = DataHelper.filter(selectedElements, "MODIFY_TAG=1");
        IDataset addElements = DataHelper.filter(selectedElements, "MODIFY_TAG=0");
        
        for (Iterator iterator = delElements.iterator(); iterator.hasNext();)
        {
            IData delElementItem = (IData) iterator.next();
            
             for (Iterator iterator2 = addElements.iterator(); iterator2.hasNext();)
            {
                IData addElementItem = (IData) iterator2.next(); 
                
                if (SysDateMgr.encodeTimestamp(addElementItem.getString("START_DATE")).getTime() == SysDateMgr.encodeTimestamp(delElementItem.getString("START_DATE")).getTime())
                {
                       addElementItem.put("START_DATE", SysDateMgr.getNextSecond(addElementItem.getString("START_DATE")));
                }
                
            }
        }
        
        result.addAll(delElements);
        result.addAll(addElements);
        
       
        return result;
    }
	/**
     *数据功能暂停请求接口(SS.InterRoamingSVC.dateStop
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
	public IDataset changeProdGprsRoamStop(IData input)throws Exception{
		
		String oprcode = input.getString("OPR_CODE");
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        IDataset result = new DatasetList();
        
        if (StringUtils.isBlank(serialNumber))
        {
            String userId = IDataUtil.chkParam(input, "USER_ID");
            
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            if (userInfo != null)
            {
                input.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
            }
            
        }
        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
        
        //功能暂停
        if ("00".equals(oprcode))
        {
            input.put("OPER_CODE","04");//操作编码
            //数据已暂停返回成功
            result =  buildReturnData(uca,new String[]{"22"},"2");
            if (IDataUtil.isNotEmpty(result))
            {
                return result;
            }
        }
        //功能恢复
        else if ("01".equals(oprcode))
        {
            input.put("OPER_CODE","05");//操作编码
            
            result =  buildReturnData(uca,new String[]{"22"},"0");
            if (IDataUtil.isNotEmpty(result))
            {
                return result;
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,"不支持的操作类型["+oprcode+"]！");
        }
        input.put("SERV_TYPE","1");
        input.put("SEND_FLAG","3");//操作编码(0暂停或 1恢发送标志(暂停必传),1是国内50G封顶，2是用户主动发起暂停，3是国际漫游50m封顶，0是国内15G封顶,4特殊类封顶暂停
        // 暂停
        input.put("REMARK", "FD");//action中判断是否需要封顶恢复,只有当前接口暂停才做恢复
        String svcName = "SS.ServiceOperRegSVC.tradeReg";
        // 服务用22
        input.put("SERVICE_ID", 22);
        if (StringUtils.isEmpty(svcName))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请检查传入参数");

        }
        IDataset results = CSAppCall.call(svcName, input);
        if(results!=null&&results.size()!=0){
            String tradeId = results.getData(0).getString("TRADE_ID");
            IData param = new DataMap(); 
            param.put("TRADE_ID", tradeId);
            SQLParser parser = new SQLParser(param); 
            parser.addSQL(" select to_char(t.start_date,'yyyymmddhh24miss') as VALID_DATE ,to_char(t.end_date,'yyyymmddhh24miss') as EXPIRE_DATE ");
            parser.addSQL(" from TF_B_TRADE_SVCSTATE t ");
            parser.addSQL(" WHERE t.trade_id = :TRADE_ID ");
            IDataset ids = Dao.qryByParse(parser,Route.getJourDb()); 
            if(ids!=null&&ids.size()!=0){
                results.getData(0).putAll(ids.getData(0));
            }
        }
        return results;
	}
	
	 private IDataset buildReturnData( UcaData ud,String[] serviceIds , String statCode) throws Exception
	    {
	        IDataset result = new DatasetList();
	     
	       for (int i = 0; i < serviceIds.length; i++)
	        {
	               SvcStateTradeData userSvcState_88 = ud.getUserSvcsStateByServiceId(serviceIds[i]); 
	              
	               if (null != userSvcState_88 && statCode.equals(userSvcState_88.getStateCode()))
	               { 
	                   if (IDataUtil.isNotEmpty(result))
	                   {
	                       break;
	                   } 
	                   
	                   IData resultItem = new DataMap();
	                   resultItem.put("VALID_DATE", SysDateMgr.decodeTimestamp(userSvcState_88.getStartDate(),SysDateMgr.PATTERN_STAND_SHORT));
	                   resultItem.put("EXPIRE_DATE",SysDateMgr.decodeTimestamp(userSvcState_88.getEndDate(),SysDateMgr.PATTERN_STAND_SHORT)); 
	                   resultItem.put("X_RESULTCODE","0");
	                   resultItem.put("X_RESULTINFO","ok");
	                   result.add(resultItem);
	                  
	               }
	        }
	       
	        
	        return result;
	    }
	
	/**
     *数据功能恢复同步接口（SS.InterRoamingSVC.dateRecover
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
	public IDataset changeProdGprsRoamOpen(IData input)throws Exception{
		
		String oprcode = input.getString("OPR_CODE");
		String servtype = input.getString("SERV_TYPE");
		input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);//开通
		input.put("ELEMENT_TYPE_CODE", "S");
		input.put("ELEMENT_ID", "89");
		IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", input);
		if(result!=null&&result.size()>0){
			result.getData(0).putAll(input);
			
			//从TF_B_TRADE_SVC表查出服务开通/关闭生效时间和终止时间
			String tradeId = result.getData(0).getString("TRADE_ID");
			IData param = new DataMap(); 
			param.put("TRADE_ID", tradeId);
			SQLParser parser = new SQLParser(param); 
	        parser.addSQL(" select to_char(t.start_date,'yyyymmddhh24miss') as VALID_DATE ,to_char(t.end_date,'yyyymmddhh24miss') as EXPIRE_DATE ");
	        parser.addSQL(" from TF_B_TRADE_SVC t ");
	        parser.addSQL(" WHERE t.trade_id = :TRADE_ID ");
	        IDataset ids = Dao.qryByParse(parser,Route.getJourDb()); 
	        result.getData(0).putAll(ids.getData(0));
		}
		return result;
	}
	/**
     * 上下行短信提醒接口SS.InterRoamingSVC.smsNotice
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
	public IData sendFlowPaymentSMS(IData input) throws Exception {
        IData param = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);// 
		String userid=uca.getUserId();
        String noticeContent=input.getString("SMS_CONTENT");
        noticeContent=noticeContent.replace("newline", "\n");
        param.put("RECV_OBJECT", input.getString("SERIAL_NUMBER", ""));
        param.put("RECV_ID", userid);
        param.put("FORCE_OBJECT", "10086");
        param.put("NOTICE_CONTENT", noticeContent);
        param.put("REFER_STAFF_ID", getVisit().getStaffId());
        param.put("REFER_DEPART_ID", getVisit().getDepartId());
        param.put("REMARK", "上下行短信提醒接口");
        SmsSend.insSms(param);
        param.put("X_RESULTCODE", "0");
        param.put("X_RESULTINFO", "成功");
        
		return param;
		
	}
	
	/**
     * 上行短信提醒接口SS.InterRoamingSVC.smsNoticeUp
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
	public IDataset noticeGprsRoamSMS(IData input) throws Exception {
        IData param = new DataMap();
        String serialNumber = input.getString("SERIAL_NUMBER");
		UcaData uca = UcaDataFactory.getNormalUca(serialNumber);// 
		String userid=uca.getUserId();
        String noticeContent=input.getString("SMS_CONTENT");

        input.put("RECV_OBJECT", input.getString("SERIAL_NUMBER", ""));
        input.put("RECV_ID", userid);
        input.put("FORCE_OBJECT", "10086");
        input.put("NOTICE_CONTENT", noticeContent);
        input.put("REFER_STAFF_ID", getVisit().getStaffId());
        input.put("REFER_DEPART_ID", getVisit().getDepartId());
        input.put("REMARK", "上下行短信提醒接口");
        input.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        input.put("PROV_CODE", "898");
        input.put("KIND_ID", "BIP3A311_T3000314_0_0");
        IDataset dataset= IBossCall.dealInvokeUrl("BIP3A311_T3000314_0_0","IBOSS",input);
		return dataset;
	}
	
	/**
	 * SS.InterRoamingSVC.interRoamingDiff
	 * 国漫订购关系日增量文件比对处理
	 * 
	 */
	 public IDataset interRoamingDiff(IData input) throws Exception
	    {
		 String diffType = input.getString("DIFFTYPE","");
		 String dealFlag = "";
		 String prodStat     = "";
		 IDataset result = new DatasetList();
		 if("F104".equals(diffType)){
			 
			 dealFlag = "02" ;
			 prodStat = "00" ;
		 }else if("F105".equals(diffType)){ //国漫有boss无
			 dealFlag = "01" ;
			 prodStat = input.getString("PRODSTAT","");
		 }else{
			 result.add(input);
			 return  result;
		 }
		 String serialNumber = input.getString("MSISDN","");
		 String userType     = input.getString("USERTYPE","");
		 String groupName     = input.getString("GROUPNAME","");
		 String provCode     = input.getString("PROVCODE","");
		 String prodInstID   = input.getString("PRODINSTID","");
		 String updateTIMSI  = input.getString("UPDATETIMSI","");
		 String prodType     = input.getString("PRODTYPE","");
		 String prodId     =   input.getString("PRODID","");
		 
		 String effTIMSI     = input.getString("EFFTIMSI","");
		 String expTIMSI     = input.getString("EXPTIMSI","");
		 String firstTIMSI   = input.getString("FIRSTTIMSI","");
		 String endTIMSI     = input.getString("ENDTIMSI","");
		 String orderRelationStat = dealFlag ;
		 String feeType      = input.getString("FEETYPE","");
		 String feeCycle     = input.getString("FEECYCLE","");
		 String fee          = input.getString("FEE","");
		 String feeaccessMod  = input.getString("FEEACCESSMOD","");
		 
		 IData data =  new DataMap();
		 data.put("SERIAL_NUMBER", serialNumber);
		 data.put("USER_TYPE", userType);
		 data.put("GROUP_NAME", groupName);
		 data.put("PROV_CODE", provCode);
		 data.put("UPDATE_TIME", updateTIMSI);
		 data.put("PROD_INST_ID", prodInstID);
		 data.put("PROD_TYPE", prodType);
		 data.put("PROD_ID", prodId);
		 data.put("PROD_STAT", prodStat);
		 data.put("VALID_DATE", effTIMSI);
		 data.put("EXPIRE_TIME", expTIMSI);
		 data.put("FIRST_TIME", firstTIMSI);
		 data.put("END_TIME", endTIMSI);
		 data.put("RELATION_STAT", orderRelationStat);
		 data.put("FEE_TYPE", feeType);
		 data.put("FEE_CYCLE", feeCycle);
		 data.put("FEE", fee);
		 data.put("CHANNEL", feeaccessMod);
		 
		 result =  SynchProdGprsRoam(data);
		 return result ;
	    }
	
	 /**
     * 国漫订购关系同步接口(SS.InterRoamingSVC.SynchProd)
     * 
     * @data 2016-11-23
     * @param data
     * @throws Exception
     */
    public IDataset SynchProdGprsRoam(IData input) throws Exception
    {
        IDataset orderPre = OrderPreInfoQry.queryOrderPreInfoBySnType(input.getString("SERIAL_NUMBER"), "InterRoam");
        if (!orderPre.isEmpty())
        {
            IData idataMap = orderPre.getData(0);
            StringBuilder idataString = new StringBuilder();
            idataString.append(idataMap.getString("ACCEPT_DATA1", "")).append(idataMap.getString("ACCEPT_DATA2", "")).append(idataMap.getString("ACCEPT_DATA3", "")).append(idataMap.getString("ACCEPT_DATA4", "")).append(idataMap.getString(
                    "ACCEPT_DATA5", ""));
            String jsonObject = idataString.toString();
            IData iDataMap = new DataMap(jsonObject);
            input.put("TRADE_STAFF_ID", iDataMap.get("TRADE_STAFF_ID"));
            input.put("TRADE_DEPART_ID", iDataMap.get("TRADE_DEPART_ID"));
            input.put("IN_MODE_CODE", iDataMap.get("IN_MODE_CODE"));
        }

        String prodId = input.getString("PROD_ID");
        String prodType = input.getString("PROD_TYPE");
        String relationStat = input.getString("RELATION_STAT");
        String userType = input.getString("USER_TYPE");//00个人用户，01集团用户
        String prodState = input.getString("PROD_STAT");//00 – 未激活，未过期 01 – 未激活，已过期 02 – 已激活，正在使用 03 – 已激活，使用完毕 04 – 已退订 05 – 已销户退订
        if("01".equals(userType)){//集团用户不处理
            IDataset result = CSAppCall.call("CS.TcsGrpIntfSVC.synchGrpProdGprsRoam", input);
            if(result!=null&&result.size()>0){
                result.getData(0).putAll(input);
            }
            return result;
        }
        if("05".equals(prodState)){//已销户退订不处理,因为销户时已经处理过了
            return null;
        }
        if("03".equals(prodState)){//失效不处理
            return null;
        }
        IDataset commparaSet = CommparaInfoQry.getCommparaByCode2("CSM", "2742", "", input.getString("PROD_ID"), input.getString("TRADE_EPARCHY_CODE"));
        String elementId=prodId;
        if (commparaSet != null && commparaSet.size() > 0)
        {
            elementId= commparaSet.getData(0).getString("PARAM_CODE");
        }
        String validDate=dataToData(input.getString("VALID_DATE"), "yyyy-MM-dd HH:mm:ss");
        String expireDate=dataToData(input.getString("EXPIRE_TIME"), "yyyy-MM-dd HH:mm:ss");
//        if ("00".equals(prodType))
        {//
            input.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
            input.put("ELEMENT_TYPE_CODE", "D");// 一次性产品，对应优惠

            if ("02".equals(relationStat))//修改
            {
                String serialNumber = input.getString("SERIAL_NUMBER");
                UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
                IDataset userDiscnts=UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(uca.getUserId(), elementId);
                if (userDiscnts == null || userDiscnts.size() <= 0)
                {
                	if(!"01".equals(prodState)){ // 01-未激活已过期   正常生成工单 不登记资费台账(trade类)
                		CSAppException.apperr(ElementException.CRM_ELEMENT_38, elementId);
                	}
                    
                }
                DataHelper.sort(userDiscnts,"START_DATE", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
                
                if((!("01".equals(prodState)))
                		||("01".equals(prodState)&&userDiscnts != null&&userDiscnts.size()>0)){ // 01-未激活已过期 
                	input.put("INST_ID", userDiscnts.getData(0).getString("INST_ID"));
                }
                
                input.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                if("02".equals(prodState)||"03".equals(prodState)){
                    expireDate=dataToData(input.getString("END_TIME"), "yyyy-MM-dd HH:mm:ss");
                    validDate = dataToData(input.getString("FIRST_TIME"), "yyyy-MM-dd HH:mm:ss");
                }else if("04".equals(prodState)){
                    //expireDate="";
                	// add by  huangyq 退订时，失效时间改为当前时间
                	expireDate = dataToData(SysDateMgr.getSysDateYYYYMMDDHHMMSS(), "yyyy-MM-dd HH:mm:ss");
                }
            }
            input.put("START_DATE", validDate);
            input.put("END_DATE", expireDate);
            //add by  huangyq
            input.put("END_TIME", expireDate);
            
            input.put("ELEMENT_ID", elementId);
            input.put("IS_ROAM_DISCNT", "true");
        	String paraName = input.getString("PARA_NAME");
			String paraValue = input.getString("PARA_VALUE");
			if("SProdInstID".equals(paraName)){
				input.put("SPROD_INST_ID",paraValue);
			}else{
				if("01".equals(relationStat)){
					input.put("SPROD_INST_ID",input.getString("PROD_INST_ID",""));
				}
			}
        }
//        else
//        {
//            input.put("ELEMENT_TYPE_CODE", "S");// 功能性产品，对应服务
//            input.put("ELEMENT_ID", "19");
//            return null;
//        }
        input.put("NO_TRADE_LIMIT", "TRUE");
        input.put("SKIP_RULE", "TRUE");
        
        String resultState="1";//成功
        IDataset result =new DatasetList();
//        try
//        {
            result=CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", input);
            
//        }
//        catch (Exception e)
//        {
//            resultState="2";//失败
//        }
        /*IData param = new DataMap(); 
        param.put("STATE", resultState);
        param.put("OPR_NUM", input.getString("OPR_NUM"));
        SQLParser parser = new SQLParser(param); 
        parser.addSQL(" update TI_B_IRCNORDER t set STATE=:STATE");
        parser.addSQL(" WHERE t.PKGSEQ = :OPR_NUM ");
        Dao.executeUpdate(parser); */

        return result;
    }
	/**
	 * 日期格式化
	 * 
	 * @author shenhai
	 * @param data
	 *            输入的日期
	 * @param formatter
	 *            要转换成的格式(如yyyy-MM-dd HH:mm:ss)
	 * @return String 转换后的日期
	 * @throws Exception 
	 */
	public static String dataToData(String data, String formatter) throws Exception
	{
		DateFormat format = new SimpleDateFormat(formatter);
		data=TimeUtil.encodeTimestamp(data).toString();
		return format.format(format.parse(data));
	}
	   /**
     * 国漫日套餐上报国漫集中平台
     * 
     * @param btd
     * @param reqdata
     * @param elem
     * @throws Exception
     * @author wangww
     */
	public IData InterRoamDayforIboss( IData input) throws Exception
    {

         String elemid=input.getString("ELEMENT_ID","");
         String serialNumber=input.getString("SERIAL_NUMBER","");
         String modifytag= input.getString("MODIFY_TAG","");
         String routeType = "00";
         String routeValue = "000";
      
        
         IData result = new DataMap();
         IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2742",elemid,  getVisit().getStaffEparchyCode());
         if(commparaSet!=null&&commparaSet.size()>0){
          result =   IBossCall.InterRoamDayforIboss(commparaSet.getData(0).getString("PARA_CODE2",""),serialNumber,modifytag,routeValue,routeType);
         if (!result.getString("X_RSPCODE").equals("0000"))
         {
             CSAppException.apperr(IBossException.CRM_IBOSS_4, result.getString("X_RSPCODE"), result.getString("X_RSPDESC"));
         }
         }
         return result;
    }
	
	/**
     * @Description: 物联网产品变更-GPRS服务属性校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkPwlwApnName(IData input) throws Exception
    {
    	String strSerialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(strSerialNumber);
        if(IDataUtil.isEmpty(userInfo))
        {
        	String info = "获取用户信息失败!";
    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
    		
        }
        String strUserId = userInfo.getString("USER_ID");
        IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(strUserId);
        if (IDataUtil.isEmpty(userMainProducts))
        {
        	String info = "获取用户产品信息失败!";
    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
        }
        String strUserBrandCode = userMainProducts.first().getString("BRAND_CODE", ""); 
        if("PWLW".equals(strUserBrandCode))
        {
        	IDataset selectedElements = new DatasetList(input.getString("SELECTED_ELEMENTS"));
            if (selectedElements != null && selectedElements.size() > 0)
            {
            	for (int i = 0; i < selectedElements.size(); i++) 
            	{
            		IData element = selectedElements.getData(i);
            		String strETC = element.getString("ELEMENT_TYPE_CODE");
            		String strEID = element.getString("ELEMENT_ID");
                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(strETC))
                    {
                    	IDataset compare3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSERVICE", strEID);
                    	if(IDataUtil.isNotEmpty(compare3995))
                    	{
                    		IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                            	for (int j = 0; j < attrs.size(); j++)
                                {
                            		IData attr = attrs.getData(j);
                            		String strAC = attr.getString("ATTR_CODE");
                            		String strAV = attr.getString("ATTR_VALUE");

                            		if("APNNAME".equals(strAC))
                            		{
                            			IDataset compare3995Attr = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWAPNNAME", strAV);
                                    	if(IDataUtil.isEmpty(compare3995Attr))
                                    	{
                                    		String info = strEID + "编码服务，APNNAME属性值没有填写正确，请检查。";
                                    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                    	}
                            		}
                                }
                            }
                    	}
                    	
                    	IDataset idsCompareSvcCmiot3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMIOT", strEID);
                    	if(IDataUtil.isNotEmpty(idsCompareSvcCmiot3995))
                    	{
                    		IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                            	for (int j = 0; j < attrs.size(); j++)
                                {
                            		IData attr = attrs.getData(j);
                            		String strAC = attr.getString("ATTR_CODE");
                            		String strAV = attr.getString("ATTR_VALUE");

                            		if("APNNAME".equals(strAC))
                            		{
                            			IData idCompareSvcCmiot3995 = idsCompareSvcCmiot3995.first();
                            			String strsP24 = idCompareSvcCmiot3995.getString("PARA_CODE24", "");
                            			
                                    	if(!strsP24.equals(strAV))
                                    	{
                                    		String info = strEID + "编码服务，APNNAME属性值必须是CMIOT。";
                                    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                    	}
                            		}
                                }
                            }
                    	}
                    	//start-wangsc10-20190402-REQ201809270011+关于下发车联网业务新增23G通信服务产品支撑系统改造方案的通知(全网)
                    	IDataset idsCompareSvcCMMTM3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMMTM", strEID);
                    	if(IDataUtil.isNotEmpty(idsCompareSvcCMMTM3995))
                    	{
                    		IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                            	for (int j = 0; j < attrs.size(); j++)
                                {
                            		IData attr = attrs.getData(j);
                            		String strAC = attr.getString("ATTR_CODE");
                            		String strAV = attr.getString("ATTR_VALUE");

                            		if("APNNAME".equals(strAC))
                            		{
                            			IData idCompareSvcCMMTM3995 = idsCompareSvcCMMTM3995.first();
                            			String strsP24 = idCompareSvcCMMTM3995.getString("PARA_CODE24", "");
                            			
                                    	if(!strsP24.equals(strAV))
                                    	{
                                    		String info = strEID + "编码服务，APNNAME属性值必须是CMMTM。";
                                    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                    	}
                            		}
                                }
                            }
                    	}
                    	//end-wangsc10-20190402-REQ201809270011+关于下发车联网业务新增23G通信服务产品支撑系统改造方案的通知(全网)
                    }
                    else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(strETC))
                    {
                    	//判断套餐时间配置，根据ELEMENT_ID,PACKAGE_ID去查询 pm_enable_mode表，如果cancel_mode为7则套餐未生效可以取消，生效后不能取消begin
                    	//UPC.Out.OfferQueryFSV.queryOfferByOfferCodeAndOfferType,UPC.Out.OfferQueryFSV.queryGroupComEnableModeByGroupIdOfferId
                    	String offerCode = element.getString("ELEMENT_ID","");
                    	String offerType = element.getString("ELEMENT_TYPE_CODE","");
                    	IDataset offerList=UpcCallIntf.queryOfferIdByOfferCodeAndOfferType(offerType,offerCode);
                    	if(offerList!=null && offerList.size()>0){ 
                    		String offerId = offerList.getData(0).getString("OFFER_ID","");
                    		if(StringUtils.isNotBlank(offerId)){
                    			String groupId = element.getString("PACKAGE_ID","");
                    			IDataset pemSet = UpcCallIntf.queryGroupComEnableModeByGroupIdOfferId(groupId,offerId);
                    			if(pemSet!=null&&pemSet.size()>0){
                    				String cancelMode = pemSet.getData(0).getString("CANCEL_MODE","");
                    				if(StringUtils.isNotBlank(cancelMode)&&"7".equals(cancelMode)){
                                		String modifyTag = element.getString("MODIFY_TAG");
                                		if("1".equals(modifyTag)){
                                			String startDate = element.getString("START_DATE");
                                			long now =System.currentTimeMillis();
                                			String path = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
	                            			Pattern pt = Pattern.compile(path);
	                            			Matcher flag = pt.matcher(startDate);
	                            			SimpleDateFormat sdf = null;
	                            			if(flag.matches()){
	                            				sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	                            			}else{
	                            				sdf = new SimpleDateFormat("yyyy-MM-dd");
	                            			}
	                            			
                                			long start =sdf.parse(startDate).getTime();
                                			if(start<now){
                                				CSAppException.apperr(BizException.CRM_BIZ_5, "套餐已经生效，不能取消");
                                			}
                                		}
                                	}
                    			}
                    		}
                    	}
                    	//判断套餐时间配置，根据ELEMENT_ID,PACKAGE_ID去查询 pm_enable_mode表，如果cancel_mode为7则套餐未生效可以取消，生效后不能取消end
                    	
                    	IDataset wlwSvclist = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
                    	if (IDataUtil.isNotEmpty(wlwSvclist))
                        {
            				IDataset commdisnts = DataHelper.filter(wlwSvclist, "PARA_CODE2=I00010101001,PARAM_CODE="+strEID);
            				//IDataset commdisnts1 = DataHelper.filter(wlwSvclist, "PARA_CODE2=I00010101003,PARAM_CODE="+userdiscnt.getString("DISCNT_CODE"));
            				if(IDataUtil.isNotEmpty(commdisnts))
            				{
            					boolean bApprovalNum = true;
            					boolean bDiscount = true;
            					IDataset attrs = element.getDataset("ATTR_PARAM");
                                if (IDataUtil.isNotEmpty(attrs))
                                {
                                	for (int j = 0; j < attrs.size(); j++)
                                    {
                                		IData attr = attrs.getData(j);
                                		String strAC = attr.getString("ATTR_CODE");
                                		String strAV = attr.getString("ATTR_VALUE");

                                		if("APNNAME".equals(strAC))
                                		{
                                			IDataset idsCompareSvcCmiot3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMIOT", "99011022");
                                			if(IDataUtil.isNotEmpty(idsCompareSvcCmiot3995))
                                			{
                                				IData idCompareSvcCmiot3995 = idsCompareSvcCmiot3995.first();
                                    			String strsP24 = idCompareSvcCmiot3995.getString("PARA_CODE24", "");
                                            	if(!strsP24.equals(strAV))
                                            	{
                                            		String info = strEID + "编码优惠，APNNAME属性值必须是CMIOT。";
                                            		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                            	}
                                			}
                                			else
                                			{
                                				String info = "Commpara表PARAM_ATTR=3995,未配置。。";
                                        		CSAppException.apperr(BizException.CRM_BIZ_5, info);
											}
                                		}
                                		else if("Discount".equals(strAC))
                                		{
                                			Integer n = 100;
                                			try 
                                			{
                                				n = Integer.parseInt(strAV);
											} 
                                			catch (Exception e) 
                                			{
												String info = strEID + "编码优惠，固费折扣率值没有填写正确，请检查。。";
                                        		CSAppException.apperr(BizException.CRM_BIZ_5, info);
											}
                                			if(n < 60)
                                        	{
                                				bApprovalNum = false;
                                        	}
                                		}
                                		else if("ApprovalNum".equals(strAC))
                                		{
                                			if(StringUtils.isNotBlank(strAV))
                                			{
                                				bDiscount = false;
                                			}
                                		}
                                    }
                                	
                                	if(!bApprovalNum && bDiscount)
                					{				
                                		String info = strEID + "编码优惠，固费折扣率低于6折，审批文号不能为空。。";
                                		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                					}
                                }
            				}
            			}
                    }
				}
            }
        }
    	return null;
    }
    
    /**
     * @Description: 批量物联网开户-校验非必选服务优惠接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkPwlwSelectElements(IData input) throws Exception
    {
    	String strProductid = input.getString("PRODUCT_ID", "");
    	String strSelectedElenents = input.getString("SELECTED_ELEMENTS", "");
    	if(StringUtils.isNotBlank(strSelectedElenents))
        {
            IDataset selectedElements = new DatasetList(strSelectedElenents);
            if (IDataUtil.isNotEmpty(selectedElements))
            {
                for (int i = 0; i < selectedElements.size(); i++) 
                {
                    IData element = selectedElements.getData(i);
                    String strETC = element.getString("ELEMENT_TYPE_CODE");
                    String strEID = element.getString("ELEMENT_ID");
                    IDataset idsElements = UpcCall.qryOfferByOfferIdRelOfferId(strProductid, "P", strEID, strETC);
                    // 工号是否有“可选非必选元素权限” 有数据就是有权限办理
                    boolean isNetBatOpenUser = StaffPrivUtil.isFuncDataPriv(CSBizBean.getVisit().getStaffId(), "NET_BAT_OPEN_USER");
                    if(isNetBatOpenUser){
                    	continue;
                    }else{
                    	if (IDataUtil.isNotEmpty(idsElements))
                        {
                        	IData idElement = idsElements.first();
                        	String strForceTag = idElement.getString("FORCE_TAG", "");
                        	if(!"1".equals(strForceTag))
                        	{
                        		String strProductName = UpcCall.qryOfferNameByOfferTypeOfferCode(strProductid, "P");
                            	String strOfferName = UpcCall.qryOfferNameByOfferTypeOfferCode(strEID, strETC);
                            	String info = strProductName + "产品下的" + strOfferName + "不是必选元素，只能选择产品下的必选服务或优惠";
                                CSAppException.apperr(BizException.CRM_BIZ_5, info);
                        	}
                        }
                        else
                        {
                        	String strProductName = UpcCall.qryOfferNameByOfferTypeOfferCode(strProductid, "P");
                        	String strOfferName = UpcCall.qryOfferNameByOfferTypeOfferCode(strEID, strETC);
                        	String info = strProductName + "产品下的" + strOfferName + "不是必选元素，请选择产品下的必选服务或优惠";
                            CSAppException.apperr(BizException.CRM_BIZ_5, info);
    					}
                    }
                    
                }
            }
        }
    	return null;
    }
    
    /**
     * @Description: 批量物联网开户-GPRS服务属性校验接口
     * @param input
     * @return
     * @throws Exception
     * @author: yanwu
     * @date: 2016-11-30 10:19:19 AM
     */
    public IData checkPwlwApnNameK(IData input) throws Exception
    {
    	boolean b20171211 = StaffPrivUtil.isPriv(CSBizBean.getVisit().getStaffId(), "CHECKWLWPCRFATTR", "1");
        String strSelectedElenents = input.getString("SELECTED_ELEMENTS", "");
        if(StringUtils.isNotBlank(strSelectedElenents))
        {
            IDataset selectedElements = new DatasetList(strSelectedElenents);
            if (selectedElements != null && selectedElements.size() > 0)
            {
                for (int i = 0; i < selectedElements.size(); i++) 
                {
                    IData element = selectedElements.getData(i);
                    String strETC = element.getString("ELEMENT_TYPE_CODE");
                    String strEID = element.getString("ELEMENT_ID");
                    if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(strETC))
                    {
                        IDataset compare3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSERVICE", strEID);
                        if(IDataUtil.isNotEmpty(compare3995))
                        {
                            IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                                for (int j = 0; j < attrs.size(); j++)
                                {
                                    IData attr = attrs.getData(j);
                                    String strAC = attr.getString("ATTR_CODE");
                                    String strAV = attr.getString("ATTR_VALUE");

                                    if("APNNAME".equals(strAC))
                                    {
                                        IDataset compare3995Attr = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWAPNNAME", strAV);
                                        if(IDataUtil.isEmpty(compare3995Attr))
                                        {
                                            String info = strEID + "编码服务，APNNAME属性值没有填写正确，请检查。";
                                            CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                        }
                                    }
                                }
                            }
                        }
                        IDataset idsCompareSvcCmiot3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMIOT", strEID);
                    	if(IDataUtil.isNotEmpty(idsCompareSvcCmiot3995))
                    	{
                    		IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                            	for (int j = 0; j < attrs.size(); j++)
                                {
                            		IData attr = attrs.getData(j);
                            		String strAC = attr.getString("ATTR_CODE");
                            		String strAV = attr.getString("ATTR_VALUE");

                            		if("APNNAME".equals(strAC))
                            		{
                            			IData idCompareSvcCmiot3995 = idsCompareSvcCmiot3995.first();
                            			String strsP24 = idCompareSvcCmiot3995.getString("PARA_CODE24", "");
                            			
                                    	if(!strsP24.equals(strAV))
                                    	{
                                    		String info = strEID + "编码服务，APNNAME属性值必须是CMIOT。";
                                    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                    	}
                            		}
                                }
                            }
                    	}
                    	//start-wangsc10-20190402-REQ201809270011+关于下发车联网业务新增23G通信服务产品支撑系统改造方案的通知(全网)
                    	IDataset idsCompareSvcCMMTM3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMMTM", strEID);
                    	if(IDataUtil.isNotEmpty(idsCompareSvcCMMTM3995))
                    	{
                    		IDataset attrs = element.getDataset("ATTR_PARAM");
                            if (IDataUtil.isNotEmpty(attrs))
                            {
                            	for (int j = 0; j < attrs.size(); j++)
                                {
                            		IData attr = attrs.getData(j);
                            		String strAC = attr.getString("ATTR_CODE");
                            		String strAV = attr.getString("ATTR_VALUE");

                            		if("APNNAME".equals(strAC))
                            		{
                            			IData idCompareSvcCMMTM3995 = idsCompareSvcCMMTM3995.first();
                            			String strsP24 = idCompareSvcCMMTM3995.getString("PARA_CODE24", "");
                            			
                                    	if(!strsP24.equals(strAV))
                                    	{
                                    		String info = strEID + "编码服务，APNNAME属性值必须是CMMTM。";
                                    		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                    	}
                            		}
                                }
                            }
                    	}
                    	//end-wangsc10-20190402-REQ201809270011+关于下发车联网业务新增23G通信服务产品支撑系统改造方案的通知(全网)
                    }
                    else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(strETC))
                    {
                    	IDataset wlwSvclist = CommparaInfoQry.getCommByParaAttr("CSM", "9013", "0898");
                    	if (IDataUtil.isNotEmpty(wlwSvclist))
                        {
            				IDataset commdisnts = DataHelper.filter(wlwSvclist, "PARA_CODE2=I00010101001,PARAM_CODE="+strEID);
            				//IDataset commdisnts1 = DataHelper.filter(wlwSvclist, "PARA_CODE2=I00010101003,PARAM_CODE="+userdiscnt.getString("DISCNT_CODE"));
            				if(IDataUtil.isNotEmpty(commdisnts))
            				{
            					boolean bApprovalNum = true;
            					boolean bDiscount = true;
            					IDataset attrs = element.getDataset("ATTR_PARAM");
                                if (IDataUtil.isNotEmpty(attrs))
                                {
                                	for (int j = 0; j < attrs.size(); j++)
                                    {
                                		IData attr = attrs.getData(j);
                                		String strAC = attr.getString("ATTR_CODE");
                                		String strAV = attr.getString("ATTR_VALUE");

                                		if("APNNAME".equals(strAC))
                                		{
                                			IDataset idsCompareSvcCmiot3995 = CommparaInfoQry.getCommparaByCodeCode1("CSM", "3995", "PWLWSVCCMIOT", "99011022");
                                			if(IDataUtil.isNotEmpty(idsCompareSvcCmiot3995))
                                			{
                                				IData idCompareSvcCmiot3995 = idsCompareSvcCmiot3995.first();
                                    			String strsP24 = idCompareSvcCmiot3995.getString("PARA_CODE24", "");
                                            	if(!strsP24.equals(strAV))
                                            	{
                                            		String info = strEID + "编码优惠，APNNAME属性值必须是CMIOT。";
                                            		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                                            	}
                                			}
                                			else
                                			{
                                				String info = "Commpara表PARAM_ATTR=3995,未配置。。";
                                        		CSAppException.apperr(BizException.CRM_BIZ_5, info);
											}
                                		}
                                		else if("Discount".equals(strAC))
                                		{
                                			Integer n = 100;
                                			try 
                                			{
                                				n = Integer.parseInt(strAV);
											} 
                                			catch (Exception e) 
                                			{
												String info = strEID + "编码优惠，固费折扣率值没有填写正确，请检查。。";
                                        		CSAppException.apperr(BizException.CRM_BIZ_5, info);
											}
                                			if(n < 60)
                                        	{
                                				bApprovalNum = false;
                                        	}
                                		}
                                		else if("ApprovalNum".equals(strAC))
                                		{
                                			if(StringUtils.isNotBlank(strAV))
                                			{
                                				bDiscount = false;
                                			}
                                		}
                                    }
                                	
                                	if(!bApprovalNum && bDiscount)
                					{				
                                		String info = strEID + "编码优惠，固费折扣率低于6折，审批文号不能为空。。";
                                		CSAppException.apperr(BizException.CRM_BIZ_5, info);
                					}
                                }
            				}
            			}
                    }
                    
                    String strAttrParam = element.getString("ATTR_PARAM");
                    if(StringUtils.isNotBlank(strAttrParam))
					{
						String strAddAudiInfo0898 = "";
						String str20171211 = "";
						IData idDiscount = new DataMap();
						IDataset idsAttrParam = new DatasetList(strAttrParam);
						for(int ii = 0; ii < idsAttrParam.size(); ii++)
						{
							IData idAttrParam = idsAttrParam.getData(ii);
							String strAttrCode = idAttrParam.getString("ATTR_CODE", "");
							String strAttrValue = idAttrParam.getString("ATTR_VALUE", "");
							if("20171211".equals(strAttrCode))
							{
								str20171211 = strAttrValue;
							}
							else if("AudiInfo0898".equals(strAttrCode))
							{
								strAddAudiInfo0898 = strAttrValue;
							}
							else if("Discount".equals(strAttrCode))
							{
								idDiscount = idAttrParam;
							}
						}
						if(StringUtils.isNotBlank(str20171211))
						{
							if(!b20171211)
							{
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "该工号无权限修改本省折扣率。");
							}
							int n20171211 = 0;
							try 
							{
								n20171211 = Integer.parseInt(str20171211);
							} 
							catch (Exception e) 
							{
								logger.debug(e);
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率范围请填写0到100，请正确填写。");
							}
							if(n20171211 < 0 || n20171211 > 100)
							{
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率范围请填写0到100，请正确填写。。");
							}
							if(n20171211 < 60)
							{
								if(StringUtils.isBlank(strAddAudiInfo0898))
								{
									CSAppException.apperr(CrmCommException.CRM_COMM_103, "本省折扣率低于6折，请填写审批工单号。");
								}
							}
							else
							{
								idDiscount.put("ATTR_VALUE", "100");
							}
						}
						else
						{
							if(StringUtils.isNotBlank(strAddAudiInfo0898))
							{
								CSAppException.apperr(CrmCommException.CRM_COMM_103, "填写审批工单号，同时也要填写本省折扣率，请正确填写本省折扣率。");
							}
						}
					}
                }
            }
        }
        return null;
    }
    
	
    /**
     *信用分免预存开通关闭国漫(SS.CreditRoamingSVC.CreditOpenOrClose),为信用平台提供外部接口
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IDataset CreditOpenOrClose(IData input)throws Exception{

    	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String oprCode =  IDataUtil.chkParam(input, "OPR_CODE");    	
    	
    	IData resultData = new DataMap();
    	resultData.put("X_RESULT_CODE", "0000");
    	resultData.put("X_RESULTINFO", "成功");
    	resultData.put("ID_TYPE", "01");
    	resultData.put("ID_VALUE", serialNumber);
        resultData.put("SERIAL_NUMBER", serialNumber);
        resultData.put("OPR_CODE", oprCode);
    	IDataset resultList = new DatasetList();
    	resultList.add(resultData);   	
    	
		IData dealResult = chgIntRoamWithoutAdvPay(input);
		String dealRusultCode = dealResult.getString("X_RESULTCODE");
		String dealRusultInfo = dealResult.getString("X_RESULTINFO");
				
		if (!StringUtils.equals(dealRusultCode, "0000")) {
        	resultData.put("X_RESULTCODE", "-1");
        	resultData.put("X_RESULTINFO", dealRusultInfo);
		} 
        return resultList;
    }
    
    /**
     *中国移动信用信用停机保障服务-同步业务受理(SS.CreditSVC.synBizOrder)，为信用平台提供外部接口
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IDataset synBizOrder(IData input)throws Exception{

    	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String bizType =  IDataUtil.chkParam(input, "BIZ_TYPE");
    	String oprCode =  IDataUtil.chkParam(input, "OPR_CODE");
    	IDataUtil.chkParam(input, "OPR_NUMB");
    	//checkExtendInfo(input, "CREDIT_CLASS");
    	String creditClass = checkExtendInfo(input, "CreditClass");
    	input.put("CreditClass", creditClass);

     	IData insertData = new DataMap();
     	insertData.putAll(input);
     	
    	IData resultData = new DataMap();
    	resultData.put("X_RESULTCODE", "0000");
    	resultData.put("X_RESULTINFO", "成功");
    	resultData.put("ID_TYPE", "01");
        resultData.put("SERIAL_NUMBER", serialNumber);
        resultData.put("BIZ_TYPE", bizType);
        resultData.put("OPR_CODE", oprCode);
        String oprTime = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        resultData.put("OPR_TIME", oprTime);
        
    	IDataset resultList = new DatasetList();
    	resultList.add(resultData);    	

		IData dealResult = chgCreditSvc4MobileStop(input);
		String dealRusultCode = dealResult.getString("X_RESULTCODE");
		String dealRusultInfo = dealResult.getString("X_RESULTINFO");
		String creditLimit = dealResult.getString("CREDIT");
		
		if (!StringUtils.equals(dealRusultCode, "0000")) {
        	resultData.put("X_RESULTCODE", "-1");
        	resultData.put("X_RESULTINFO", dealRusultInfo);
		} else {
			setExtendInfoRes(resultData,"CREDIT",creditLimit);
		}
		return resultList;
	}
    

    /**
     *信用分免预存开通国漫鉴权 信用停机保障鉴权(SS.CreditRoamingSVC.CreditPreOpen),为信用分平台提供外部接口
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IDataset CreditPreOpen(IData input)throws Exception{

    	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
    	//String creditClass = this.checkExtendInfo(input, "CREDIT_CLASS");
    	String creditClass = this.checkExtendInfo(input, "CreditClass");
    
    	IData BusStatedata = new DataMap();
      	BusStatedata.put("BUSSTATE", "1");
		
		IDataset BusStateList = new DatasetList();
		BusStateList.add(BusStatedata);
		
		IData resultData = new DataMap();
		resultData.put("RESULTINFO", BusStateList);
    	resultData.put("SERIAL_NUMBER", serialNumber);
    	resultData.put("ID_TYPE", "01");
    	resultData.put("ID_VALUE", serialNumber);
		resultData.put("BIZTYPE", bizType);// '业务类型
		resultData.put("HOMEPROV", "898");// 所属省份
		
    	IDataset resultList = new DatasetList();
    	resultList.add(resultData);

        if (!this.isNormalUser(serialNumber)) {
            resultData.put("X_RESULTCODE", "2009");
            resultData.put("X_RESULTINFO", "用户不存在或用户非正常状态");
            resultData.put("REASON", "用户不存在或用户非正常状态");
            BusStatedata.put("BUSSTATE", "0");
            BusStatedata.put("CONDITION", "0");
            return resultList;
        } else {
            resultData.put("X_RESULTCODE", "0000");
            resultData.put("X_RESULTINFO", "Success");
        }
        IData dealMobileStop = new DataMap();
        try {
			if (StringUtils.equals("1001", bizType)) {//信用停开机服务保障
		    	IData param = new DataMap();
		    	param.put("SERIAL_NUMBER", serialNumber);
		    	param.put("OPR_CODE", "01");///预受理检验 信用停开机服务保障 开通
		    	param.put("CREDIT_CLASS", creditClass);
		    	dealMobileStop = this.dealMobileStop(param,true);
		    	if("NO".equals(dealMobileStop.getString("ISOPEN"))){
		    		BusStatedata.put("BUSSTATE", "0");
		            BusStatedata.put("CONDITION", "1");
		    	}
			}
		}  catch (BaseException e) {
			String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);  
            
    		if (null != errorArray && errorArray.length>1) {
    			BusStatedata.put("REASON", errorArray[1]);
    		} else {
    			BusStatedata.put("REASON", error);
    		}
            BusStatedata.put("BUSSTATE", "0");
            BusStatedata.put("CONDITION", "0"); 	
        } catch (Exception ex2) {
        	String error =  Utility.parseExceptionMessage(ex2); 
        	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
    		if (null != errorArray && errorArray.length>1) {
    			BusStatedata.put("REASON", "系统异常："+errorArray[1]);
    		} else {
    			BusStatedata.put("REASON", "系统异常："+error);
    		}
            BusStatedata.put("BUSSTATE", "0");
            BusStatedata.put("CONDITION", "0");
        } 

        return resultList;
    }

    /**
     * 获取用户状态编码
     */
    public boolean isNormalUser(String serialNumber) {
        boolean isNormal = false;
        try {
            UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
            if ("0".equals(uca.getUser().getUserStateCodeset())) {
                isNormal = true;
            }
        } catch (Exception e) {
            isNormal = false;
        }
        return isNormal;
    }


    /**
     *一级IOP批量开通信用保障停机服务(SS.CreditSVC.batIOPOpen) 批量业务，为定时任务提供调用
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IDataset batIOPOpen(IData input) throws Exception {
    	IDataset resultList = new DatasetList();
		ChangeProductBean bean = new ChangeProductBean();
		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有设置信用分相关配置！");
		}
		
		int limitCount = commparaList.getData(0).getInt("PARA_CODE6",1000000);//IOP批量开通 定时任务每次启动 最多处理多少条数据
		int sizePerPage = commparaList.getData(0).getInt("PARA_CODE7",10000);//IOP批量开通 每次最多查询多少条数据
		int dealCount = 0;//计数器，表示处理了多少条数据
		
		while (dealCount<limitCount) {
			IDataset queryResult = bean.querybatIOPOpenList(sizePerPage);
			if(IDataUtil.isEmpty(queryResult)){
				return resultList;
			}
			int size = queryResult.size();
			dealCount = dealCount + size;
	        for (int i=0;i<size;i++) {
	        	String operNum = queryResult.getData(i).getString("OPER_NUM");
	        	String userId = queryResult.getData(i).getString("USER_ID");
	        	String sn = queryResult.getData(i).getString("SERIAL_NUMBER");
	        	String creditClass = queryResult.getData(i).getString("CREDIT_CLASS");
	        	
	        	IData param = new DataMap();
	        	param.put("SERIAL_NUMBER",sn);
	         	param.put("OPR_CODE","01");
	         	param.put("CREDIT_CLASS",creditClass);//信用停机保障服务
	      	
	        	IData dealResult = this.chgCreditSvc4MobileStop(input);
	        	
	        	String resultCode = dealResult.getString("X_RESULTCODE");
	        	String resultInfo = dealResult.getString("X_RESULTINFO");
	        	String releTradeId = dealResult.getString("TRADE_ID");
	        	String status = "3";//状态，null:初始状态，0，执行，1，执行成功，2：执行失败,3:不执行';
	        	//更新trade,状态，原因，可能还有其他字段，如更新时间。
	        	if (StringUtils.equals(resultCode, "0000")) {
	        		status = "1";
	        	} else {
	        		status = "2";
	        	}
	        	
	        	String reason = resultInfo;
	        	if (resultInfo.length()>200) {
	        		reason = resultInfo.substring(0,200);
	        	}
	        	IData moveParam = new DataMap();
	        	moveParam.put("IN_OPER_NUM", operNum);
	        	moveParam.put("IN_STATUS", status);
	        	moveParam.put("IN_REASON", reason);
	        	moveParam.put("IN_RELA_TRADE_ID", releTradeId);
	        	moveParam.put("IN_OPER_NUM", operNum);
	        	bean.moveBatIOPList(moveParam);
	        }
		}
		return resultList;
    }
    
    /**
     *批量同步用户信用分(SS.CreditSVC.batSyncUserCredit) 批量业务，为定时任务提供调用
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IDataset batSyncUserCredit(IData input) throws Exception {
    	IDataset resultList = new DatasetList();
		ChangeProductBean bean = new ChangeProductBean();
		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有设置信用分相关配置！");
		}
		
		int limitCount = commparaList.getData(0).getInt("PARA_CODE8",1000000);//批量信用分变更同步 定时任务每次启动 最多处理多少条数据
		int sizePerPage = commparaList.getData(0).getInt("PARA_CODE9",10000);//批量信用分变更同步  每次最多查询多少条数据
		int dealCount = 0;//计数器，表示处理了多少条数据
		
		while (dealCount<limitCount) {
			IDataset queryResult = bean.querybatSyncUserCreditList(sizePerPage);
			if(IDataUtil.isEmpty(queryResult)){
				return resultList;
			}
			int size = queryResult.size();
			dealCount = dealCount + size;
	        for (int i=0;i<size;i++) {
	        	String operNum = queryResult.getData(i).getString("OPER_NUM");
	        	String sn = queryResult.getData(i).getString("SERIAL_NUMBER");
	        	String creditClass = queryResult.getData(i).getString("CREDIT_CLASS");
	        	
	        	 //TODO
	        	IData param = new DataMap();
	        	param.put("SERIAL_NUMBER",sn);
	         	param.put("OPR_CODE","01");
	         	param.put("CREDIT_CLASS",creditClass);//信用停机保障服务
	      	   
	        	IData dealResult = this.syncUserCredit(input);
	        	
	        	String resultCode = dealResult.getString("X_RESULTCODE");
	        	String resultInfo = dealResult.getString("X_RESULTINFO");
	        	String releTradeId = dealResult.getString("TRADE_ID");
	        	String status = "3";//状态，null:初始状态，0，执行，1，执行成功，2：执行失败,3:不执行';
	        	//更新trade,状态，原因，可能还有其他字段，如更新时间。
	        	if (StringUtils.equals(resultCode, "0000")) {
	        		status = "1";
	        	} else {
	        		status = "2";
	        	}
	        	
	        	String reason = resultInfo;
	        	if (resultInfo.length()>200) {
	        		reason = resultInfo.substring(0,200);
	        	}
	        	IData moveParam = new DataMap();
	        	moveParam.put("IN_OPER_NUM", operNum);
	        	moveParam.put("IN_STATUS", status);
	        	moveParam.put("IN_REASON", reason);
	        	moveParam.put("IN_RELA_TRADE_ID", releTradeId);
	        	moveParam.put("IN_OPER_NUM", operNum);
	        	bean.moveBatSyncUserCreditList(moveParam);
	        }
		}
		return resultList;
	}
    
    /**
     * 查询用户信用分档(SS.CreditSVC.getUserCreditLevel),为CRM内部接口
     * 
     * @param params
     * @return
     * @throws Exception
     * @author wangww
     */
    public IDataset getUserCreditLevel(IData params) throws Exception
    {
    	IData resultData = new DataMap();
    	IData inputData = new DataMap();
    	String sqid=SeqMgr.getInstId();
    	IDataset resultList = new DatasetList();
    	String serialNumber =  IDataUtil.chkParam(params, "SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "用户资料异常！");
			resultList.add(resultData);
			 return resultList;
        }
        inputData.put("ID_TYPE", "01");
        inputData.put("ID_VALUE",serialNumber);
        inputData.put("OPR_NUMB", "898"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+sqid.substring(sqid.length()-6,sqid.length()));
        inputData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        inputData.put("BIZ_VERSION", "1.0.0");
        inputData.put("KIND_ID", "creditQuery_query_0_0");
        
        inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
    	IDataset result = IBossCall.dealInvokeUrl("creditQuery_query_0_0",
				"IBOSS6", inputData);
		if (IDataUtil.isNotEmpty(result)) {
			if (!result.getData(0).getString("X_RSPCODE").equals("0000")) {
				resultData.put("X_RESULTCODE", "-1");
				resultData.put("X_RESULTINFO", "调IBOSS查询信用分档异常！");
				resultList.add(resultData);
				 return resultList;

			}else{
				resultData.put("X_RESULTCODE", "0");
				resultData.put("X_RESULTINFO", "查询成功！");
				resultData.put("ID_TYPE", "01");
				resultData.put("ID_VALUE",serialNumber);
				resultData.put("CREDIT_CLASS",result.getData(0).getString("CREDIT_CLASS", ""));
				resultList.add(resultData);
				}
			}else{
				
				resultData.put("X_RESULTCODE", "-1");
				resultData.put("X_RESULTINFO", "调IBOSS查询信用分档异常！");
				resultList.add(resultData);
			}

        return resultList;
    }
    
    /**
     *开通、取消中国移动信用信用停机保障服务(SS.CreditSVC.chgCreditSvc4MobileStop)，为CRM内部接口
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IData chgCreditSvc4MobileStop(IData input) throws Exception{
    	String creditClass = getExtendInfoValue(input, "CreditClass");
    	if (StringUtils.isEmpty(creditClass)) {
    		creditClass  = IDataUtil.chkParam(input, "CreditClass");
    	}

    	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String oprCode =  IDataUtil.chkParam(input, "OPR_CODE");
    	this.addExtendInfo(input, "CREDIT_CLASS", creditClass);
    	if (StringUtils.isEmpty(input.getString("BIZ_TYPE"))) {
    		input.put("BIZ_TYPE","1001");
    	}
    	if (StringUtils.isEmpty(input.getString("OPR_NUMB"))) {
    		
    		input.put("OPR_NUMB",getOprNumFromBoss().substring(0,32));
    	}
    	if (StringUtils.isEmpty(input.getString("CHANNEL"))) {
    		
    		input.put("CHANNEL",getChannelFromBoss());
    	}
     	IData insertData = new DataMap();
     	insertData.putAll(input);
     	
    	boolean isSuccess = false;
    	String errorMsg = "失败";
    	String tradeId = "";
    	String creditLimit = "";
     	try {
	    	IData param = new DataMap();
	    	param.put("SERIAL_NUMBER", serialNumber);
	    	param.put("OPR_CODE", oprCode);///预受理检验 信用停开机服务保障 开通
	    	param.put("CREDIT_CLASS", creditClass);
			IData dealResult = dealMobileStop(param,false);

			tradeId = dealResult.getString("TRADE_ID","");	
			IData creditInfo = this.getUserCreditInfo(serialNumber,creditClass);
			creditLimit = creditInfo.getString("CREDIT_VALUE","");
			
 			isSuccess = true;
 		} catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);  
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}  
 		} catch (Exception ex2) {
        	ex2.printStackTrace();
        	String error =  Utility.parseExceptionMessage(ex2); 
        	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}
 		}
     	insertData.put("CREDIT_CLASS", creditClass);
     	insertData.put("TRADE_ID", tradeId);
     	insertData.put("IS_SUCCESS", isSuccess);
     	insertData.put("CREDIT", creditLimit);
     	insertData.put("X_RESULTINFO", errorMsg);
        insertIrcnCreditSvc(insertData);
        
        IData resultData = new DataMap();
        if (isSuccess) {
        	resultData.put("X_RESULTCODE", "0000");
        	resultData.put("X_RESULTINFO", "成功");
        } else {
        	resultData.put("X_RESULTCODE", "-1");
        	resultData.put("X_RESULTINFO", errorMsg);
        }
    	resultData.put("TRADE_ID", tradeId);
    	resultData.put("CREDIT", creditLimit);
		
        return resultData;
    }
    
    /**
     *信用分免预存开通关闭国漫(SS.CreditSVC.chgIntRoamWithoutAdvPay)，为CRM内部接口
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IData chgIntRoamWithoutAdvPay(IData input) throws Exception{

    	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String oprCode =  IDataUtil.chkParam(input, "OPR_CODE");    	
    	
    	IData insertData = new DataMap();
    	insertData.putAll(input);
    	
    	if (StringUtils.isEmpty(input.getString("BIZ_TYPE"))) {
    		input.put("BIZ_TYPE","1002");
    	}
    	if (StringUtils.isEmpty(input.getString("OPR_NUMB"))) {
    		
    		input.put("OPR_NUMB",getOprNumFromBoss());
    	}
    	if (StringUtils.isEmpty(input.getString("CHANNEL"))) {
    		
    		input.put("CHANNEL",getChannelFromBoss());
    	} 	

    	boolean isSuccess = false;
    	String errorMsg = "失败";
    	String creditClass = "";
    	String tradeId = "";
    	//正式受理业务
     	try {
     		
     		IData paramData = new DataMap();
     		paramData.put("SERIAL_NUMBER", serialNumber);
     		IDataset creditLevelList = this.getUserCreditLevel(paramData);//获取用户信用分档
            if (IDataUtil.isNotEmpty(creditLevelList)) {
            	if (!creditLevelList.getData(0).getString("X_RESULTCODE").equals("-1")) {
            		creditClass = creditLevelList.getData(0).getString("CREDIT_CLASS");
            	}else{//没有获取到用户信用分档，报错
            		CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有获取平台信用分档："+creditLevelList.getData(0).getString("X_RESULTINFO"));
            	}
            }

			
     		if("24".equals(oprCode)) {//取消国漫业务           oprCode：24  删除  23  开通
     			IData dealResult = this.cancelIntRoamWithoutAdvPay(input, false);
     			tradeId = dealResult.getString("TRADE_ID");
     		} else {//开通国漫
    	    	IData param = new DataMap();
    	    	param.put("SERIAL_NUMBER", serialNumber);
    	    	param.put("CREDIT_CLASS", creditClass);
    	    	IData dealResult = this.OpenIntRoamWithoutAdvPay(input, false);
    	    	tradeId = dealResult.getString("TRADE_ID");
    		}
     		
     		isSuccess = true;
 		} catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);  
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}  
 		} catch (Exception ex2) {
        	ex2.printStackTrace();
        	String error =  Utility.parseExceptionMessage(ex2); 
        	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}
 		}
     	
     	insertData.put("CREDIT_CLASS", creditClass);
     	insertData.put("IS_SUCCESS", isSuccess);
     	insertData.put("TRADE_ID", tradeId);
     	insertData.put("X_RESULTINFO", errorMsg);
        insertIrcnCreditSvc(insertData);
        
        IData resultData = new DataMap();
        if (isSuccess) {
        	resultData.put("X_RESULTCODE", "0000");
        	resultData.put("X_RESULTINFO", "成功");
        } else {
        	resultData.put("X_RESULTCODE", "-1");
        	resultData.put("X_RESULTINFO", errorMsg);
        }
    	resultData.put("TRADE_ID", tradeId);
    	resultData.put("CREDIT_CLASS", creditClass);
    	
    	
     	insertData.put("IS_SUCCESS",isSuccess);
     	insertData.put("X_RESULTINFO", resultData.getString("X_RESULTINFO"));
        insertIrcnCreditSvc(insertData);
        return resultData;
    }
    
    /**
     *同步单个用户信用档位
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    public IData syncUserCredit(IData input) throws Exception{
    	IData resultData = new DataMap();
    	resultData.put("X_RESULTCODE", "0");
    	resultData.put("X_RESULTINFO", "成功");
    	boolean isSuccess = false;
    	boolean isNeedExec = true;
    	String errorMsg = "失败";
    	String tradeId = "";

    	try {
        	String serialNumber =  IDataUtil.chkParam(input, "SERIAL_NUMBER");
        	String creditClass =  IDataUtil.chkParam(input, "CREDIT_CLASS");

        	//查询用户是否已经开通了信用停开机保障服务
    		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
    		if (IDataUtil.isEmpty(commparaList))
    		{
    			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有设置信用分相关配置！");
    		}
    		
    		String creditMobileStopSvc = commparaList.getData(0).getString("PARA_CODE2");//信用停开机 保障信用服务
        	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);        
            List<SvcTradeData> svcList =uca.getUserSvcBySvcId(creditMobileStopSvc);
            boolean isOpen = (svcList !=null && svcList.size()>0); 
        	if (!isOpen) {
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未开通信用停开机保障服务");
        	}
        	String userSvcInstId = svcList.get(0).getInstId();
        	AttrTradeData attrData  = uca.getUserAttrsByRelaInstIdAttrCode(userSvcInstId, "CREDIT_CLASS");

    		if (!isCanOpen(input)) {//不满足信用开通要求
    	    	input.put("OPR_CODE", "02");//取消
    	    	IData chgResult = this.chgCreditSvc4MobileStop(input);
    	    	if (StringUtils.equals("0000", chgResult.getString("X_RESULTCODE"))) {//取消时，需要插表到同步接口中，所以调用dealMobileStop上层封装接口
    	    		isSuccess = true;
    	    		errorMsg = "取消信用停开机保障服务成功";
    	    		tradeId = chgResult.getString("TRADE_ID");
    	    	} else {
    	    		isSuccess = false;
    	    		errorMsg = "取消信用停开机保障服务失败：" + chgResult.getString("X_RESULTINFO","");
    	    	}
    		} else {
            	if (StringUtils.equals(attrData.getAttrValue(), creditClass)) {//跟用户信用分档一致，不需要同步
            		//不执行，返回一个特定值
            		isNeedExec = false;
                	resultData.put("X_RESULTINFO", "待同步信用分档值与用户信用分档一致，不需要执行业务！");
            	} else
    			input.put("OPR_CODE", "MODIFY_CREDIT_CLASS");//更新同步用户信用分档
            	IData chgResult = this.dealMobileStop(input,false);//不需要插表到同步接口中，所以只需要调用dealMobileStop接口
    	    	if (StringUtils.equals("0", chgResult.getString("X_RESULTCODE"))) {
    	    		isSuccess = true;
    	    		errorMsg = "同步用户信用分档位成功";
    	    		tradeId = chgResult.getString("TRADE_ID");
    	    	} else {
    	    		isSuccess = false;
    	    		errorMsg = "同步用户信用分档位失败:"+chgResult.getString("X_RESULTINFO","");
    	    	}
    		}

    	}  catch (BaseException e) {
        	String error =  Utility.parseExceptionMessage(e); 
            String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);  
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}  
 		} catch (Exception ex2) {
        	ex2.printStackTrace();
        	String error =  Utility.parseExceptionMessage(ex2); 
        	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
    		if (null != errorArray && errorArray.length>1) {
    			errorMsg = errorArray[1];
    		} else {
    			errorMsg = error;
    		}
 		}
        if (!isNeedExec) {
        	resultData.put("X_RESULTCODE", "1");
        } else {
            if (isSuccess) {
            	resultData.put("X_RESULTCODE", "0000");
            } else {
            	resultData.put("X_RESULTCODE", "-1");
            }
        }
        resultData.put("X_RESULTINFO", errorMsg);    
    	resultData.put("TRADE_ID", tradeId);
		
        return resultData;
    }
    
    /**
     * 查询用户信用分
     * 
     * @param params
     * @return
     * @throws Exception
     * @author wangww
     */
    public IDataset getUserCreditScore(IData params) throws Exception
    {
    	IData resultData = new DataMap();
    	IData inputData = new DataMap();
    	String sqid=SeqMgr.getInstId();
    	IDataset resultList = new DatasetList();
    	String serialNumber =  IDataUtil.chkParam(params, "SERIAL_NUMBER");
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
        	resultData.put("X_RESULTCODE", "-1");
			resultData.put("X_RESULTINFO", "用户资料异常！");
			resultList.add(resultData);
			 return resultList;
        }
        inputData.put("ID_TYPE", "01");
        inputData.put("ID_VALUE",serialNumber);
        inputData.put("OPR_NUMB", "898"+SysDateMgr.getSysDateYYYYMMDDHHMMSS()+sqid.substring(sqid.length()-6,sqid.length()));
        inputData.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        inputData.put("BIZ_VERSION", "1.0.0");
        inputData.put("KIND_ID", "creditPoQuery_query_0_0");
        
        inputData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
    	IDataset result = IBossCall.dealInvokeUrl("creditPoQuery_query_0_0",
				"IBOSS6", inputData);
		if (IDataUtil.isNotEmpty(result)) {
			if (!result.getData(0).getString("X_RSPCODE").equals("0000")) {
				resultData.put("X_RESULTCODE", "-1");
				resultData.put("X_RESULTINFO", "调IBOSS查询信用分异常！");
				resultList.add(resultData);
				 return resultList;

			}else{
				resultData.put("X_RESULTCODE", "0");
				resultData.put("X_RESULTINFO", "查询成功！");
				resultData.put("ID_TYPE", "01");
				resultData.put("ID_VALUE",serialNumber);
				resultData.put("CREDIT",result.getData(0).getString("CREDIT", ""));
				resultList.add(resultData);
				}
			}else{
				
				resultData.put("X_RESULTCODE", "-1");
				resultData.put("X_RESULTINFO", "调IBOSS查询信用分异常！");
				resultList.add(resultData);
			}

        return resultList;
    }
    
    /**
     * 查询用户信用分档及信用分信息
     * 
     * @param params
     * @return
     * @throws Exception
     * @author wangww
     */
    public IData getUserCreditInfo(String sn,String givenCreditClass) throws Exception
    {
 		String creditClass = "";
 		String creditValue = "";
 		
 		if (StringUtils.isNotEmpty(givenCreditClass)) {
 			creditClass = givenCreditClass;
 		} else {
 	 		IData paramData = new DataMap();
 	 		paramData.put("SERIAL_NUMBER", sn);
 	 		IDataset creditLevelList = this.getUserCreditLevel(paramData);//获取用户信用分档
 	        if (IDataUtil.isNotEmpty(creditLevelList)) {
 	        	if (!creditLevelList.getData(0).getString("X_RESULTCODE").equals("-1")) {
 	        		creditClass = creditLevelList.getData(0).getString("CREDIT_CLASS");
 	        	}
 	        }
 		}
 		
 		/*int creditScore = Integer.parseInt(creditClass);
 		
 		if(creditScore < 17){
 			creditScore = 600;
 		}*/

		IDataset creditLimitList = AcctCall.getBindCreditLimit(sn, creditClass);
 		//IDataset creditLimitList = AcctCall.getBindCreditLimit(sn, creditScore+"");
		creditValue =  creditLimitList.getData(0).getString("CREDIT_VALUE");
        
        IData resultData = new DataMap();
    	resultData.put("CREDIT_CLASS", creditClass);
		resultData.put("CREDIT_VALUE", creditValue);
		return resultData;
    }
    
    //开通信用国漫免预存
    public IData OpenIntRoamWithoutAdvPay(IData input,boolean isPreTrade) throws Exception {
       	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	IDataUtil.chkParam(input,"CREDIT_CLASS");
		
        //查询用户是否已经开通了国际漫游
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String intRoamSvc = "19";
        List<SvcTradeData> svcList =uca.getUserSvcBySvcId(intRoamSvc);
        
        boolean isOpen = (svcList !=null && svcList.size()>0);        	
    	if (isOpen) {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户已开通国际漫游");
    	}
    	chkCredit4IntRoam(input);

    	if (isPreTrade) {
    		input.put("PRE_TYPE", "1");
      	}	
		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有设置信用分相关配置！");
		}
		
		String creditIntRoamSvc = commparaList.getData(0).getString("PARA_CODE1");//信用免预存开国漫
		input.put("CREDIT_INTROAM_SVC", creditIntRoamSvc);//标记和信用免预存开国漫;信用开国漫，开通86服务,action负责开通
		input.put("CREDIT_FLAG", 1);//标记和信用免预存开国漫;信用开国漫，开通86服务
		IDataset resultList = modifyEndDateByDays(input, -1);
		IData resultData = resultList.getData(0);
        String tradeId = resultData.getString("TRADE_ID");
        String resultInfo = resultData.getString("X_RESULT_INFO");
		
		if (StringUtils.isEmpty(tradeId)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo);
		}

		return resultData;
    }
    
    //取消国漫功能及信用功能
    public IData cancelIntRoamWithoutAdvPay(IData input,boolean isPreTrade) throws Exception {
       	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        //查询用户是否已经开通了国际漫游
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
		String intRoamSvc = "19";
        List<SvcTradeData> svcList =uca.getUserSvcBySvcId(intRoamSvc);
        
        boolean isOpen = (svcList !=null && svcList.size()>0);
    	if (!isOpen) {
    		CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户未开通国际漫游");
    	}

    	if (isPreTrade) {
    		input.put("PRE_TYPE", "1");
      	}
    	
    	IDataset resultList = delRoam(input);//国漫停了后，86连带停。通过action处理
    	IData resultData = resultList.getData(0);
        String tradeId = resultData.getString("TRADE_ID");
        String resultInfo = resultData.getString("X_RESULT_INFO");
		
		if (StringUtils.isEmpty(tradeId)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo);
		} 
		return resultData; 

    }
    
    //开通或取消信用停机保障服务 更新用户信用分档
    public IData dealMobileStop(IData input,boolean isPreTrade) throws Exception {    	
       	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String oprCode = IDataUtil.chkParam(input, "OPR_CODE");
    	IDataUtil.chkParam(input,"CREDIT_CLASS");
    	IData idate = new DataMap();
    	UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	
    	IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_888,"没有设置信用分相关配置！");
		}
		
		String creditMobileStopSvc = commparaList.getData(0).getString("PARA_CODE2");//信用停开机 保障信用服务

        //查询用户是否已经开通了信用停开机保障服务
        List<SvcTradeData> svcList =uca.getUserSvcBySvcId(creditMobileStopSvc);
        
        boolean isOpen = (svcList !=null && svcList.size()>0);
        if (StringUtils.equals(oprCode, "01")) {
        	if (isOpen) {
        		if(isPreTrade){
        			idate.put("ISOPEN","YES");
            		return idate;
        		}else{
        			CSAppException.apperr(CrmCommException.CRM_COMM_888, "用户已开通信用停开机保障服务");
        		}       		
        	} 
        	this.chkCredit4MobileStop(input);
        } else {
        	if (!isOpen) {
        		CSAppException.apperr(CrmCommException.CRM_COMM_888, "用户未开通信用停开机保障服务");
        	}
        }

    	if (isPreTrade) {
    		input.put("PRE_TYPE", "1");
    	}	
		
		input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));		
		input.put("ELEMENT_ID", creditMobileStopSvc);
		
		if (StringUtils.equals(oprCode, "01")) {//订购
			input.put("MODIFY_TAG", "0");
		} else if (StringUtils.equals(oprCode, "02")) {//退订
			input.put("MODIFY_TAG", "1");
		} else if (StringUtils.equals(oprCode, "MODIFY_CREDIT_CLASS")) {//更新
			input.put("MODIFY_TAG", "2");
		}

		input.put("ELEMENT_TYPE_CODE", "S");
		input.put("ATTR_STR1", "CREDIT_CLASS");
		input.put("ATTR_STR2", input.getString("CREDIT_CLASS"));
		input.put("ELEMENT_NOT_IN_PROD_PACK_FLAG", "1");
		
		IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", input);
		IData resultData = resultList.getData(0);
        String tradeId = resultData.getString("TRADE_ID");
        String resultInfo = resultData.getString("X_RESULT_INFO");
        if(isOpen){
        	resultData.put("ISOPEN","YES");
        }else{
        	resultData.put("ISOPEN","NO");
        }        
        
		
		if (StringUtils.isEmpty(tradeId)) {
			CSAppException.apperr(CrmCommException.CRM_COMM_888, resultInfo);
		}		
		return resultData; 
    }
    
	private String checkExtendInfo(IData data, String infoCodeName) throws Exception {
		String infoCodeValue = "";
		boolean isFind = false;
		IDataset extendInfo = data.getDataset("EXTEND_INFO");
		
		if (IDataUtil.isNotEmpty(extendInfo)) {
			for (int i=0;i<extendInfo.size();i++) {
				String infoCode = extendInfo.getData(0).getString("INFO_CODE");
				String infoValue = extendInfo.getData(0).getString("INFO_VALUE");
				
				if (StringUtils.equals(infoCode, infoCodeName)) {
					isFind = true;
					infoCodeValue = infoValue;
					break;
				}
			}
		}
        if (!isFind) {
        	String strError = "接口参数检查: 输入参数EXTEND_INFO中没有表示[" + infoCodeName + "]键值对";
            Utility.error("-1", null, strError);
        }	
        return infoCodeValue;
	}
	
	private void addExtendInfo(IData data,String key, String value) throws Exception {
		IDataset infos = data.getDataset("EXTEND_INFO");
		if (IDataUtil.isNull(infos)) {
			infos = new DatasetList();
			data.put("EXTEND_INFO",infos);
		}
		IData info = new DataMap();
		info.put("INFO_CODE", key);
		info.put("INFO_VALUE", value);
		infos.add(info);
		
	}
	
	private String getExtendInfoValue(IData data,String key) throws Exception {
		String returnValue = null;
		IDataset infos = data.getDataset("EXTEND_INFO");
		if (IDataUtil.isNotEmpty(infos)) {
			for (int i=0;i<infos.size();i++) {		
				String infoCode = infos.getData(i).getString("INFO_CODE");
				String infoValue = infos.getData(i).getString("INFO_VALUE");
				if(StringUtils.equals(infoCode, key)) {
					returnValue = infoValue;
					break;
				}				
			}
		}
		return returnValue;
	}
	
    private void insertIrcnCreditSvc(IData data) throws Exception {
		IData ircndata = new DataMap();
		ircndata.put("OPRNUMB", data.getString("OPR_NUMB"));// 操作流水号 平台受理的填写实时交易的OprNumb；BOSS短厅等渠道受理填写省内的流水号
		ircndata.put("IDTYPE", "01"); // 用户标识类型
		ircndata.put("MSISDN", data.getString("SERIAL_NUMBER"));// '用户手机号码
		ircndata.put("PROVCODE", "898");// 省代码
		ircndata.put("TRADE_ID", data.getString("TRADE_ID",""));// 单号
		ircndata.put("EFFTIMSI", SysDateMgr.getNowCyc());// 业务生效时间
		ircndata.put("OPRCCODE",  data.getString("OPR_CODE"));// 操作代码
		ircndata.put("CREDITCLASS", data.getString("CREDIT_CLASS", ""));// 信用分档
		ircndata.put("CREDIT", data.getString("CREDIT", ""));// 信用额度
		ircndata.put("CHANNEL",data.getString("CHANNEL", ""));// 操作渠道来源
		ircndata.put("SVCTYPE",data.getString("BIZ_TYPE", ""));// 业务类型
		
		ircndata.put("P_DAY",  SysDateMgr.getNowCycle());// 订购操作发生日期
		ircndata.put("P_MONTH", SysDateMgr.getCurMonth());// 分区月份
		if(data.getBoolean("IS_SUCCESS")){
			ircndata.put("RESPTYPE", "00");// 办理结果
			ircndata.put("RESPMESS", "成功");// 办理失败原因描述	

		}else{
			ircndata.put("RESPTYPE", "01");// 办理结果
			ircndata.put("RESPMESS", data.getString("X_RESULTINFO", ""));// 办理失败原因描述	
		}
		
		Dao.insert("TI_B_IRCNCREDITSVC", ircndata, Route.CONN_CRM_CEN);
	}

    /**
     *信用免预存开通国漫业务 信用分或信用分档规则校验
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    private  void chkCredit4IntRoam(IData param) throws Exception {
    	String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	String creditClass = param.getString("CREDIT_CLASS","");
    	String creditScore = param.getString("CREDIT","");
       
		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_103,"没有设置信用分相关配置！");
		}
		
		int creditIntRoam = commparaList.getData(0).getInt("PARA_CODE3");//免预存开国漫信用分要求
		String creditRuleFlag = commparaList.getData(0).getString("PARA_CODE5");//0:根据信用分档判断，1：根据信用分判断

		if (StringUtils.equals(creditRuleFlag, "0")) {
			if (StringUtils.isEmpty(creditClass)) {
		        //获取用户的信用分档
		    	IData paramData = new DataMap();
		    	paramData.put("SERIAL_NUMBER", serialNumber);
		        IDataset creditList=getUserCreditLevel(paramData);
		        if (IDataUtil.isNotEmpty(creditList)) {
		        	if (!creditList.getData(0).getString("X_RESULTCODE").equals("-1")) {
		        		creditClass = creditList.getData(0).getString("CREDIT_CLASS", "0");
		        	}else{
		    			 CSAppException.apperr(CrmCommException.CRM_COMM_103, creditList.getData(0).getString("X_RESULTINFO"));
		        	}
		        }
			}
	        
			//判断用户信用分是否大于  则不能开通。
            if(Integer.parseInt(creditClass)>creditIntRoam){
            	String msg = "用户目前信用分档值："+creditClass+";信用分档大于"+creditIntRoam+"，不能免预存开通国漫业务！";
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, msg);
            }
		
		} else if (StringUtils.equals(creditRuleFlag, "1")) {
			if (StringUtils.isEmpty(creditScore)) {
		        //获取用户的信用分值
		    	IData paramData = new DataMap();
		    	paramData.put( "SERIAL_NUMBER", serialNumber);
		    	IDataset creditList=getUserCreditScore(paramData);
		        if (IDataUtil.isNotEmpty(creditList)) {
		        	if (!creditList.getData(0).getString("X_RESULTCODE").equals("-1")) {
		        		creditScore = creditList.getData(0).getString("CREDIT", "0");
		        	}else{
		        		CSAppException.apperr(CrmCommException.CRM_COMM_103, creditList.getData(0).getString("X_RESULTINFO"));
		        	}
		        }
			}
	        
            //判断用户信用分是否低于400 低则不能开通。
            if(Integer.parseInt(creditScore)<creditIntRoam){
            	String msg = "用户目前信用分值："+creditScore+";信用分小于"+creditIntRoam+"，不能免预存开通国漫业务！";
    			CSAppException.apperr(CrmCommException.CRM_COMM_103, msg);
            }
		}
    }
    /**
     *信用停机保障 信用分或信用分档规则校验
     * 
     * @data 2018-5-08
     * @param input
     * @throws Exception
     */
    private  void chkCredit4MobileStop(IData param) throws Exception {
    	String serialNumber = IDataUtil.chkParam(param, "SERIAL_NUMBER");
    	String creditClass = param.getString("CREDIT_CLASS","");
    	String creditScore = param.getString("CREDIT","");

		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		if (IDataUtil.isEmpty(commparaList))
		{
			CSAppException.apperr(CrmCommException.CRM_COMM_888,"没有设置信用分相关配置！");
		}
		
		int creditTj = commparaList.getData(0).getInt("PARA_CODE4");//信用停开机 保障信用分要求
		String creditRuleFlag = commparaList.getData(0).getString("PARA_CODE5");//0:根据信用分档判断，1：根据信用分判断

		if (StringUtils.equals(creditRuleFlag, "0")) {
			if (StringUtils.isEmpty(creditClass)) {
		        //获取用户的信用分档
		    	IData paramData = new DataMap();
		    	paramData.put("SERIAL_NUMBER", serialNumber);
		        IDataset creditList=getUserCreditLevel(paramData);
		        if (IDataUtil.isNotEmpty(creditList)) {
		        	if (!creditList.getData(0).getString("X_RESULTCODE").equals("-1")) {
		        		creditClass = creditList.getData(0).getString("CREDIT_CLASS", "0");
		        	}else{
		    			 CSAppException.apperr(CrmCommException.CRM_COMM_888, creditList.getData(0).getString("X_RESULTINFO"));
		        	}
		        }
			}
	        
            //判断用户信用分档是否大于  则不能开通。
            if(Integer.parseInt(creditClass)>creditTj){        	
            	String msg = "用户目前信用分档值："+creditClass+";信用分档大于"+creditTj+"，不能办理信用停机保障服务业务！";
            	CSAppException.apperr(CrmCommException.CRM_COMM_888, msg);
            }
		} else if (StringUtils.equals(creditRuleFlag, "1")) {
			if (StringUtils.isEmpty(creditScore)) {
		        //获取用户的信用分值
		    	IData paramData = new DataMap();
		    	paramData.put( "SERIAL_NUMBER", serialNumber);
		    	IDataset creditList=getUserCreditScore(paramData);
		        if (IDataUtil.isNotEmpty(creditList)) {
		        	if (!creditList.getData(0).getString("X_RESULTCODE").equals("-1")) {
		        		creditScore = creditList.getData(0).getString("CREDIT", "0");
		        	}else{
		        		CSAppException.apperr(CrmCommException.CRM_COMM_888, creditList.getData(0).getString("X_RESULTINFO"));
		        	}
		        }
			}
            //判断用户信用分是否低于580 低于580则不能开通。
            if(Integer.parseInt(creditScore)<creditTj){        	
            	String msg = "用户目前信用分值："+creditScore+";信用分小于"+creditTj+"，不能办理信用停机保障服务业务！";
            	CSAppException.apperr(CrmCommException.CRM_COMM_888, msg);
            }
		}
    }
    
	private void setExtendInfoRes(IData data,String key, String value) throws Exception {
		IData info = new DataMap();
		info.put("INFO_CODE_RES", key);
		info.put("INFO_VALUE_RES", value);
		IDataset infos = new DatasetList();
		infos.add(info);
		data.put("EXTEND_INFO_RES",infos);
	}

    private String getOprNumFromBoss() throws Exception {
        StringBuilder operSeq = new StringBuilder();
        operSeq.append("COP");//省domain
        operSeq.append("898");//省编码
        operSeq.append(SeqMgr.getPreSmsSendId()); // 取系统时间，YYYYMMDDHH24MMSS+流水号,流水号不足8位补0
        return operSeq.toString();
    }   
    
    private String getChannelFromBoss() throws Exception {
    	String channel = "01";//CRM/BOSS （由系统发起）	BOSS
    	String inModeCode =  CSBizBean.getVisit().getInModeCode();
    	if (StringUtils.equals(inModeCode,"0")) {
    		channel = "01";//11	营业前台	BOSS
    	}
    	
    	/*其他渠道代码，根据实际需要进行转化
    	 * 06	网上营业厅	BOSS
			07	掌上营业厅	BOSS
			08	短信营业厅	BOSS
			09	10086人工	BOSS
			10	10086IVR	BOSS
			11	营业前台	BOSS
    	 */
    	return channel;

    }
    
    private boolean isCanOpen(IData input) {
    	boolean isCan = true;
    	try {
    		this.chkCredit4MobileStop(input);
    	} catch(Exception e) {
    		isCan = false;
    	}
    	return isCan;
    	
    }

    /**
     * ###查询用户国漫开通和信用停开机###
     *
     * @param iData
     * @return
     * @throws Exception
     */
    public IDataset getUserSvc(IData iData) throws Exception {
        ChangeProductBean bean = BeanManager.createBean(ChangeProductBean.class);
        return bean.getUserSvc(iData);
    }

	/**
     *客户资料查询接口 add by dengyi5
     *
     * @data 2018-12-19
     * @param input
     * @throws Exception
     */
    public IData roamBirthQry(IData input) throws Exception {
    	IData commparadata = getcommparavalueroamBirthCheck();
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------1998="+commparadata);
			
		}
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
    	
    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String birthTag = input.getString("BIRTH_TAG","0");//是否返回错误编码。0：是。1：否
    	
    	//全球通标识 1、全球通银卡 2、全球通金卡  3、全球通白金卡 4、全球通钻石卡（非终身）5、全球通终身钻石卡 6、全球通客户（体验）7、其他非全球通用户
    	String gsmState = "7";    	
    	String birthday = "";//用户生日
    	//1.查询用户信息
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(DataUtils.isEmpty(userInfo))
    	{
    		String errorcode = "2994"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		return result;
    	}
    	
    	//2.查询用户全球通标识   这里各省有差异DY
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", param);
    	
    	UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));   
    	if(DataUtils.isNotEmpty(roamTag)&&uca!=null)
    	{    		 	 
    		gsmState =  uca.getBrandCode() ;
    	}
    	else
    	{
    		if("0".equals(birthTag))
    		{
    			String errorcode = "2986"; 
        		result.put("X_RESULTCODE", errorcode);
        		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    			return result;
    		}
    	}
    	
    	//3.查询用户生日    	
    	IData inparam = new DataMap();
    	inparam.put("SERIAL_NUMBER", serialNumber);
    	IDataset custInfos = Dao.qryByCode("TF_F_CUST_PERSON", "SEL_BY_SERIALNUM_AND_USER", inparam);
    	if(DataUtils.isNotEmpty(custInfos))
    	{
    		IData custInfo = custInfos.getData(0);
    		String psptType = custInfo.getString("PSPT_TYPE_CODE");
    		if("0,1,2,3".contains(psptType))
    		{//0-本地身份证,1-外地身份证,2户口簿     这里各省有差异DY
    			//birthday = custInfo.getString("PSPT_ID").substring(6, 14);
    			
    			
    			String psptId = custInfo.getString("PSPT_ID");
    			if(15 == psptId.length())
    			{
    				birthday = psptId.substring(8, 12);
    			}
    			else if(18 == psptId.length())
    			{
    				birthday = psptId.substring(6, 14);
    			}else{
        			String birth = custInfo.getString("BIRTHDAY");
        			if(StringUtils.isNotBlank(birth))
        			{
        				Timestamp birthTimestamp = SysDateMgr.encodeTimestamp(birth);
        				birthday = DateFormatUtils.format(birthTimestamp.getTime(), "yyyyMMdd");
        			}
    			}
    			
    			
    		}
    		else
    		{//没有则获取BIRTHDAY数据
    			String birth = custInfo.getString("BIRTHDAY");
    			if(StringUtils.isNotBlank(birth))
    			{
    				Timestamp birthTimestamp = SysDateMgr.encodeTimestamp(birth);
    				birthday = DateFormatUtils.format(birthTimestamp.getTime(), "yyyyMMdd");
    			}
    		}
    	}
    	
    	if("0".equals(birthTag))
		{
    		if(StringUtils.isBlank(birthday))
    		{
    			String errorcode = "2992"; 
        		result.put("X_RESULTCODE", errorcode);
        		result.put("X_RESULTINFO", commparadata.getString(errorcode));

    			return result;
    		}
		}
    	
    	result.put("GSM_STATE",gsmState);
    	result.put("BIRTHDAY",birthday);
    	return result;
    }
	
    /**
     *全球通生日免单权益包生日信息修改订购接口 add by dengyi5
     *
     * @data 2018-12-22
     * @param input:SERIAL_NUMBER,BIRTHDAY
     * BIRTHDAY:格式YYYYMMDD;BIRTH_TAG=0表示是否校验且调用订购接口。0：是。1：否;
     * @throws Exception
     */
    public IData roamBirthMod(IData input) throws Exception {
    	IData commparadata = getcommparavalueroamBirthCheck();

    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2072="+input);
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2073="+commparadata);
			
		}
    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String birthday = IDataUtil.chkParam(input, "BIRTHDAY");
    	String birthTag = input.getString("BIRTH_TAG","0");//是否校验且调用订购接口。0：是。1：否
    	
    	IData result = new DataMap();  
    	result.put("X_RESULTCODE", "0000");
    	
    	//1.校验生日字段信息
    	int len = birthday.length();
    	if(!birthday.matches("^[0-9]*$") || ( len!=4 && len!=8))
    	{//日期只能为4位MMDD或YYYYMMDD
    		
			String errorcode = "2988"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		/*result.put("X_RESULTCODE", "2988");
    		result.put("X_RESULTINFO", "设定的生日免单日期有误");*/
    		return result;
    	}
    	
       	//1.查询用户信息
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2094 ="+userInfo);
		}
    	if(DataUtils.isEmpty(userInfo))
    	{
			String errorcode = "2994"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    	/*	result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", "获取用户信息失败");*/
    		return result;
    	}    	    	
    	
		String custid = userInfo.getString("CUST_ID");		
		IDataset ds = CustPersonInfoQry.getPerInfoByCustId(custid);
		
		if (ds != null && ds.size() > 0) {
			IData custdata =  ds.first();
			String psptTypeCode = custdata.getString("PSPT_TYPE_CODE","").trim();
			if(psptTypeCode.equals("0")
			||psptTypeCode.equals("1")
			||psptTypeCode.equals("2")
			||psptTypeCode.equals("3")){//身份证件用户，不允许修改生日
				
				String errorcode = "2985"; 
	    		result.put("X_RESULTCODE", errorcode);
	    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
	    		/*result.put("X_RESULTCODE", "2988");
	    		result.put("X_RESULTINFO", "身份证件用户，不能修改生日");*/
				return result;
			}
			if(custdata.getString("BIRTHDAY", "").trim().length()>0){//身份证字段已有值，不允许修改生日
				String errorcode = "2989"; 
	    		result.put("X_RESULTCODE", errorcode);
	    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
	    		/*result.put("X_RESULTCODE", "2988");
	    		result.put("X_RESULTINFO", "生日信息已设置过，不能再次修改");*/
				return result;
			}
		}
    	
    	if("0".equals(birthTag))
    	{//校验标记
    		result = roamBirthCheck(input);
        	if(logger.isDebugEnabled())
    		{
    			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2107 ="+result);
    		}
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("----roamBirthCheck------result="+result);
    		}
    		if(!"0000".equals(result.getString("X_RESULTCODE")))
    		{
    			return result;
    		}
    	}
    	
    	//调用账务修改接口
    	IData param = new DataMap();//
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("BIRTH_DATE", birthday);
    	IDataset modifyInfo = SccCall.modifyCustInfoByBirthday(param);//   这里需要修改DY
    	IData redata = modifyInfo.getData(0);
    	IData redata1 = new DatasetList(redata.getString("OUTDATA")).first() ;
    	if(logger.isDebugEnabled())
		{   
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2160 ="+modifyInfo);			
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2161 ="+redata);			
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2162 ="+redata1);			
		}
         
    	if(DataUtils.isNotEmpty(modifyInfo))
    	{
    		if("0".equals(redata1.getString("X_RESULTCODE")))
    		{
    	    	if(logger.isDebugEnabled())
    			{
    				logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2171 =" + redata1.getString("X_RESULTCODE"));
    			}
    			input.put("BIRTH_TAG","0");//标记修改入口，不重复做校验
    			input.putAll(result);
    			return roamBirthOrder(input);
    		}
    		else
    		{
				String errorcode = "2991"; 
	    		result.put("X_RESULTCODE", errorcode);
	    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    			/*result.put("X_RESULTCODE", "2988");
        		result.put("X_RESULTINFO", "生日日期修改失败");*/
        		return result;
    		}
    	}
    	else
    	{
    		String errorcode = "2991"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		/*result.put("X_RESULTCODE", "2988");
    		result.put("X_RESULTINFO", "生日日期修改异常");*/
    		return result;
    	}
    }
    
    /**
     *全球通生日免单权益包产品订购接口 add by dengyi5
     *
     * @data 2018-12-22
     * @param input BIRTH_TAG:0：表示修改接口处理完后调用,不需要重复校验及生日获取;1：表示其他直接调用受理接口
     * @throws Exception
     */
    public IData roamBirthOrder(IData input) throws Exception {
    	IData commparadata = getcommparavalueroamBirthCheck();
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2167 =" + input);
		}
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
    	
    	String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
    	String birthTag = input.getString("BIRTH_TAG","1");//是否需要查询及校验.0：表示修改接口处理完后调用。1：表示其他直接调用受理接口
    	
    	//1.查询获取用户数据接口
    	if(StringUtils.isBlank(input.getString("BIRTHDAY")))
    	{
    		IData resultQry = roamBirthQry(input);
    		if(!"0000".equals(resultQry.getString("X_RESULTCODE")))
    		{
    			return resultQry;
    		}
    		input.put("BIRTHDAY", resultQry.getString("BIRTHDAY"));
    	}
    	
    	if(!"0".equals(birthTag))
    	{//校验用户信息，获取生日生失效时间
    		result = roamBirthCheck(input);
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("--roamBirthOrder-----result="+result);
    		}
        	
    		if(!"0000".equals(result.getString("X_RESULTCODE")))
    		{
    			return result;
    		}
    		input.putAll(result);
    	}
    	
    	String elementId = "";
    	IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2745", "ROAM_BIRTH", CSBizBean.getVisit().getStaffEparchyCode());
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2205 =" + commparaSet);
		}
    	if (DataUtils.isNotEmpty(commparaSet))
		{
			elementId = commparaSet.getData(0).getString("PARA_CODE1");
		}
    	else
    	{
    		String errorcode = "2995"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		
    		/*result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", "获取用户全球通生日权益资费编码失败");*/
    		return result;
    	}
    	
    	String startDate = input.getString("BIRTH_START_DATE");
    	String endDate = input.getString("BIRTH_END_DATE");
    	if(logger.isDebugEnabled())
		{
			logger.debug("--roamBirthOrderxxxxxxxxx-----startDate="+startDate);
			logger.debug("--roamBirthOrderxxxxxxxxx-----endDate="+endDate);
		}
//    	try
//    	{//这里先注释，等测试完成后再添加 产品变更接口 各省有差异  DY
    		//6.业务受理
    		IData inParam = new DataMap();
    		inParam.put("SERIAL_NUMBER", serialNumber);
    		inParam.put("ELEMENT_ID", elementId);
    		inParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
    		inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
    		inParam.put("START_DATE", startDate);
    		inParam.put("END_DATE", endDate);
        	if(logger.isDebugEnabled())
    		{
    			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2237 =" + inParam);
    		}
        	
    		IDataset changeResult = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", inParam);
        	
    		if(logger.isDebugEnabled())
    		{
    			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2242 =" + changeResult);
    		}
    		if(DataUtils.isEmpty(changeResult))
    		{
        		String errorcode = "2997"; 
        		result.put("X_RESULTCODE", errorcode);
        		result.put("X_RESULTINFO", commparadata.getString(errorcode));
        		
    		/*	result.put("X_RESULTCODE", "2998");
        		result.put("X_RESULTINFO", "用户订购全球通生日权益包失败");*/
        		return result;
    		}
    		result.putAll(changeResult.getData(0));
    		result.put("START_DATE", startDate);
    		result.put("BIRTHDAY", input.getString("BIRTHDAY"));
        	if(logger.isDebugEnabled())
    		{
    			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2255 =" + result);
    		}
//    	}
//    	catch(Exception e)
//    	{
//    		result.put("X_RESULTCODE", "2998");
//    		result.put("X_RESULTINFO", e.getMessage());
//    		return result;
//    	}
    		
    	return result;    	
    }
    
    /**
     * 全球通生日免单权益包校验接口 add by dengyi5
     * 校验用户资料，用户状态，是否欠费，是否全球通标签用户，是否生日已过，是否已订购过生日权益
     * @data 2018-12-22
     * @param input
     * @return X_RESULTCODE:校验结果。X_RESULTINFO:返回描述。生日包生失效时间。BIRTH_START_DATE,BIRTH_END_DATE
     * @throws Exception
     */
    public IData roamBirthCheck(IData input) throws Exception {
    	IData commparadata = getcommparavalueroamBirthCheck();

    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2277 =" + input);
		}
    	IData result = new DataMap();
    	result.put("X_RESULTCODE", "0000");
    	String serialNumber = input.getString("SERIAL_NUMBER");
    	
    	//1.查询用户信息，欠费判断
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2288 =" + userInfo);
		}
    	if(DataUtils.isEmpty(userInfo))
    	{
    		String errorcode = "2994"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
//    		result.put("X_RESULTCODE", "2998");
//    		result.put("X_RESULTINFO", "获取用户信息失败");
    		return result;
    	}
    	
    	//2.用户状态校验
    	if(!"0".equals(userInfo.getString("USER_STATE_CODESET")))
    	{
    		String errorcode = "2998"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		
    		/*result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", "用户状态异常");*/
    		return result;
    	}
    	
    	//3.实时欠费
        UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	IData ownFeeData = AcctCall.getOweFeeByUserId(uca.getUserId());
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2311 =" + uca);
		}
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2315 =" + ownFeeData);
		}
    	
    	if(logger.isDebugEnabled())
		{
			logger.debug("----roamBirthCheck------ownFeeData="+ownFeeData);
		}
    	
		if(IDataUtil.isNotEmpty(ownFeeData) && Integer.parseInt(ownFeeData.getString("ACCT_BALANCE")) < 0)
		{
			
/*    		String errorcode = "2999"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
			
			result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", "用户存在实时欠费，不能订购生日权益");
    		return result;*/
        }
    	
    	//4.查询用户全球通标识    这里各省有差异DY
    	IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	IDataset roamTag = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_GSM_BY_SN", param);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2336 =" + roamTag);
		}
    	if(DataUtils.isEmpty(roamTag))
    	{
    		String errorcode = "2986"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		/*result.put("X_RESULTCODE", "2986");
			result.put("X_RESULTINFO", "非全球通标签用户");*/
			return result;
    	}
    	
    	//系统时间设置
    	String sysdate = SysDateMgr.getSysDate();
    	int thisYear = Integer.parseInt(SysDateMgr.getNowYear());//已订购判断年份
    	int birthYear = Integer.parseInt(SysDateMgr.getNowYear());//资费生效年份
    	String startDate = thisYear+"-01-01"; 
    	String endDate =  thisYear+"-01-14"; 
    	
    	String inRegion = "1";//0:系统时间在区间0101-0114内，1：区间外
    	String birthRegion = "1";//0:生日时间在区间0101-0114内，1：区间外
    	
    	//生日日期设置
    	String birthday = input.getString("BIRTHDAY");
    	String birthMMDD = birthday.substring(birthday.length()-4, birthday.length());
    	StringBuilder sb = new StringBuilder(birthMMDD);
    	sb.insert(2, "-");//在指定的位置2，插入‘-’.格式转换为MM-DD
    	birthMMDD = sb.toString();
    	
    	StringBuilder birthStartDate = new StringBuilder();
    	birthStartDate.append(birthYear);//默认当前年判断区间
    	birthStartDate.append("-");
    	birthStartDate.append(birthMMDD);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2402 =" + birthStartDate.toString());
		}
    	StringBuilder birthEndDate = new StringBuilder();
    	
    	if(sysdate.compareTo(startDate)>=0 && sysdate.compareTo(endDate)<=0)
    	{//当前时间在1.1-1.14内，判断使用年份为前一年
    		inRegion = "0";
    	}
    	
    	if(birthStartDate.toString().compareTo(startDate)>=0 && birthStartDate.toString().compareTo(endDate)<=0)
    	{//生日在1.1-1.14内
    		birthRegion = "0";
    	}
    	
    	if(logger.isDebugEnabled())
		{
			logger.debug("--roamBirthCheck--inRegion="+inRegion+"----birthRegion="+birthRegion);
		}
    	
    	if("0".equals(inRegion))
    	{//当前日期在1.1-1.14内，判断年份为上一年的年份
    		thisYear = thisYear-1;
    	}
    	else
    	{//当前日期不在1.1-1.14内
    		if("0".equals(birthRegion))
    		{//生日在1.1-1.14区间内则年份判断下一年
    			birthYear = birthYear +1;
    		}
    	}
    	
    	//设置生日日期(带时分秒)
    	birthStartDate.replace(0, 4, String.valueOf(birthYear));//thisYear+birthMMDD;//默认00:00:00
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2402 =" + birthStartDate.toString());
		}
    	birthEndDate.append(birthYear);
    	birthEndDate.append("-");
    	birthEndDate.append(birthMMDD);
    	birthEndDate.append(SysDateMgr.END_DATE);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2446 =" + birthEndDate.toString());
		}
    	
    	if("0229".equals(birthMMDD))
    	{//特殊生日处理,2.29的取当年2月的最后一天
    		String monthLastDay = SysDateMgr.getDateLastMonthSec(thisYear+"-02-01");
    		birthStartDate.replace(0, 10, monthLastDay.substring(0, 10));
    		birthEndDate.replace(0, 10, monthLastDay.substring(0, 10));
    	}
    	
    	int dealResult = sysdate.compareTo(birthStartDate.toString());
    	//5.生日日期已过判断
    	if(dealResult > 0)
    	{//生日在1.1-1.14内
    		
    		String errorcode = "2987"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		/*result.put("X_RESULTCODE", "2986");
			result.put("X_RESULTINFO", "用户生日已过无法再次订购");*/
			return result;
    	}
    	else if(dealResult == 0)
    	{//生日当天
    		birthStartDate.replace(0, birthStartDate.length(), SysDateMgr.getSysTime());
    	}
    	
    	if(logger.isDebugEnabled())
		{
			logger.debug("--roamBirthCheck-----birthStartDate2458="+birthStartDate);
			logger.debug("--roamBirthCheck-----birthEndDate2459="+birthEndDate);
		}
    	
    	String elementId = "";
    	IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2745", "ROAM_BIRTH", CSBizBean.getVisit().getStaffEparchyCode());
    	if (DataUtils.isNotEmpty(commparaSet))
		{
			elementId = commparaSet.getData(0).getString("PARA_CODE1");
		}
    	else
    	{
    		String errorcode = "2995"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		/*result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", "获取用户全球通生日权益资费编码失败");*/
    		return result;
    	}
    	
    	startDate = thisYear+"0115000000"; //每年1月15日0:00—次年1月14日23:59:59 
    	endDate =  (thisYear+1)+"0114235959"; 
    	
    	//6.已订购权益包判断
    	param.put("USER_ID", userInfo.getString("USER_ID"));
    	param.put("DISCNT_CODE", elementId);
    	param.put("START_DATE", startDate);
    	param.put("END_DATE", endDate);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2451 =" + param);
		}
    	IDataset userDiscntInfo = Dao.qryByCode("TF_F_USER_DISCNT", "SEL_BIR_BY_USERID", param);
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2456 =" + userDiscntInfo);
		}
    	if(DataUtils.isNotEmpty(userDiscntInfo))
    	{
    		String errorcode = "2990"; 
    		result.put("X_RESULTCODE", errorcode);
    		result.put("X_RESULTINFO", commparadata.getString(errorcode));
    		
    		/*result.put("X_RESULTCODE", "2990");
    		result.put("X_RESULTINFO", "用户在当年的生日权益有效期内已领取过生日权益");*/
    		return result;
    	}
    	
    	result.put("BIRTH_START_DATE", birthStartDate.toString());
    	result.put("BIRTH_END_DATE", birthEndDate.toString());
    	
    	if(logger.isDebugEnabled())
		{
			logger.debug("----SS.ChangeProductRegSVC.changeProductxxxxxxxxxxxxx------2469 =" + result);
		}
    	return result;
    }
    
    
	public IData getcommparavalueroamBirthCheck() throws Exception {
		IData data = new DataMap();
		IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2746", "roamBirthCheck", CSBizBean.getVisit().getStaffEparchyCode());
		if (DataUtils.isNotEmpty(commparaSet)) {
			for (int i = 0; i < commparaSet.size(); i++) {
				IData comparadata = commparaSet.getData(i);
				data.put(comparadata.getString("PARA_CODE1", "").trim(), comparadata.getString("PARAM_NAME", "").trim());
			}
		}
		return data;

	}
	
	public IData checkDiscntForSMS(IData input) throws Exception
    {	
		 IData result = new DataMap();
	     result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	     result.put("RSP_CODE", "0000");
	     result.put("RSP_DESC", "成功！");
	     
    	try{
    		String serialNumber = input.getString("SERIAL_NUMBER");
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isEmpty(userInfo)){
    			result.put("RSP_CODE", "1010");
    			result.put("RSP_DESC", "查不到用户信息！");
     		    return result;
    		}
    		String userId=userInfo.getString("USER_ID");
    		
    		IData param = new DataMap();
	    	param.put("USER_ID",userId);
	        param.put("SUBSYS_CODE","CSM");
	        param.put("PARAM_ATTR", "9931");
	        
	        boolean flg =false;
    		//看是否有预约
    		IDataset commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara(param);
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			//查已生效
    			commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara2(param);
    			
    		}
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			result.put("RSP_CODE", "20190516");
    		    result.put("RSP_DESC", "未查到配置范围生效的优惠！");
    		    return result;
    		}else{
    			result.put("MONEY", commparaInfos9931.getData(0).getString("PARA_CODE4"));
    			result.put("TIME", commparaInfos9931.getData(0).getString("PARA_CODE5"));
    			
    		}
    		
    		
    		
    	}catch(Exception e){
   		 SessionManager.getInstance().rollback();
	   		 result.put("RSP_CODE", "3006");
	   		 result.put("RSP_DESC","产品变更受理报错:"+e.getMessage());
	   		 return result;
     	}
    	return result;
   	
    }
	
	public IData orderDiscntForSMS(IData input) throws Exception
    {	
		 IData result = new DataMap();
	     result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	     result.put("RSP_CODE", "0000");
	     result.put("RSP_DESC", "成功！");
	     
    	try{
    		String serialNumber = input.getString("SERIAL_NUMBER");
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isEmpty(userInfo)){
    			result.put("RSP_CODE", "2019051601");
    			result.put("RSP_DESC", "查不到用户信息！");
     		    return result;
    		}
    		String userId=userInfo.getString("USER_ID");
    		
    		IData param = new DataMap();
	    	param.put("USER_ID",userId);
	        param.put("SUBSYS_CODE","CSM");
	        param.put("PARAM_ATTR", "9931");
	        
	        boolean flg =false;
    		//看是否有预约
    		IDataset commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara(param);
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			//查已生效
    			commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara2(param);
    			
    		}else{
    			 flg =true;
    		}
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			result.put("RSP_CODE", "20190516");
    		    result.put("RSP_DESC", "未查到配置范围生效的优惠！");
    		    return result;
    		}
    			
    		//IDataset gprsDiscnts = DiscntInfoQry.getUserDiscntInfo(serialNumber);
    		String elementTypeCode = commparaInfos9931.getData(0).getString("PARA_CODE2");
    		String elementId = commparaInfos9931.getData(0).getString("PARA_CODE1");
    		String startDate = commparaInfos9931.getData(0).getString("START_DATE");

    		 //产品变更入参
    		 IData inParam = new DataMap();
	   		 inParam.put("IN_MODE_CODE", "5");//该接口为短厅开发
	   		 inParam.put("ELEMENT_TYPE_CODE", elementTypeCode);
	   		 inParam.put("ELEMENT_ID", elementId);
	   		 inParam.put("BOOKING_TAG", "0");
	   		 inParam.put("FFDISCNT_START_DATE", startDate);
	   		 inParam.put("SERIAL_NUMBER", serialNumber);
	   	     inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
	   	   
	   	     if(flg){
	   	         inParam.put("FFDISCNT_FLAG", "TRURE"); 
	   	     }
	   		 
    		 //SERIAL_NUMBER=15203630786,ELEMENT_ID=84018120,ELEMENT_TYPE_CODE=D,MODIFY_TAG=0,BOOKING_TAG=0
    		
    		 IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct",inParam);
    		 if(IDataUtil.isNotEmpty(resultList)){
        		result.putAll(resultList.getData(0));
        	 }
    		
    	}
    	catch(Exception e){
    		 SessionManager.getInstance().rollback();
	   		 result.put("RSP_CODE", "3006");
	   		 result.put("RSP_DESC","产品变更受理报错:"+e.getMessage());
	   		 return result;
    	}
    	
    	return result;
    }
	
	public IData cancelDiscntForSMS(IData input) throws Exception
    {	
		 IData result = new DataMap();
	     result.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
	     result.put("RSP_CODE", "0000");
	     result.put("RSP_DESC", "成功！");
	     
    	try{
    		String serialNumber = input.getString("SERIAL_NUMBER");
    		IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    		if(IDataUtil.isEmpty(userInfo)){
    			result.put("RSP_CODE", "2019051601");
    			result.put("RSP_DESC", "查不到用户信息！");
     		    return result;
    		}
    		String userId=userInfo.getString("USER_ID");
    		
    		IData param = new DataMap();
	    	param.put("USER_ID",userId);
	        param.put("SUBSYS_CODE","CSM");
	        param.put("PARAM_ATTR", "9931");
	        
    		//看是否有预约
    		IDataset commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara(param);
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			//查已生效
    			commparaInfos9931 = DiscntInfoQry.qryDiscntsByCompara2(param);
    			
    		}
    		
    		if(IDataUtil.isEmpty(commparaInfos9931)){
    			result.put("RSP_CODE", "20190516");
    		    result.put("RSP_DESC", "未查到配置范围生效的优惠！");
    		    return result;
    		}
    		
    		String elementTypeCode = commparaInfos9931.getData(0).getString("PARA_CODE2");
    		String elementId = commparaInfos9931.getData(0).getString("PARA_CODE1");
    		String startDate = commparaInfos9931.getData(0).getString("START_DATE");
    		
    		IData params = new DataMap();
            params.put("USER_ID", userId);
            params.put("DISCNT_CODE", elementId);
            IDataset userDiscnts = Dao.qryByCodeParser("TF_F_USER_DISCNT", "SEL_XQWDISCNTINFO_BYUSERID", params);
            if(IDataUtil.isNotEmpty(userDiscnts)){
            	String endDate = userDiscnts.getData(0).getString("END_DATE");
            	Date now =new Date();
    			String date = SysDateMgr.date2String(now,SysDateMgr.PATTERN_STAND);
    			if(SysDateMgr.monthsBetween(date,endDate)==0){
    				result.put("RSP_CODE", "2019051602");
        			result.put("RSP_DESC", elementId+"优惠已终止月底！");
        			return result;
    			}
    			
    	     //产品变更入参
    	     IData inParam = new DataMap();
   	   		 inParam.put("IN_MODE_CODE", "5");//该接口为短厅开发
   	   		 inParam.put("ELEMENT_TYPE_CODE", elementTypeCode);
   	   		 inParam.put("ELEMENT_ID", elementId);
   	   		 inParam.put("BOOKING_TAG", "0");
   	   		 //inParam.put("FFDISCNT_START_DATE", startDate);
   	   		 inParam.put("SERIAL_NUMBER", serialNumber);
   	   	     inParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
   	
   	   	    
       		 //SERIAL_NUMBER=15203630786,ELEMENT_ID=84018120,ELEMENT_TYPE_CODE=D,MODIFY_TAG=0,BOOKING_TAG=0
       		
       		 IDataset resultList = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct",inParam);
       		 if(IDataUtil.isNotEmpty(resultList)){
       			result.putAll(resultList.getData(0));
       		 }
       		 
            }else{
            	result.put("RSP_CODE", "2019051603");
    			result.put("RSP_DESC", "没有可终止优惠存在！");
            }

    		
    		
    		
    	}
    	catch(Exception e){
    		 SessionManager.getInstance().rollback();
	   		 result.put("RSP_CODE", "3006");
	   		 result.put("RSP_DESC","产品变更受理报错:"+e.getMessage());
	   		 return result;
    	}
    	
    	return result;
    }
    
   /**
     * 全球通生日免单权益包生日当天短信下发接口 add by dengyi5
     * 生日当天针对符合的用户下发短信,一天执行一次
     * @data 2019-3-21
     * @throws Exception
     */
    	
    	
    	//1.已订购权益包且今天生效的用户获取
    				//2.不存在用户正常信息，不做处理
    	
   
	/**
     * 全球通生日免单权益包生日前一天自动赠送接口 add by dengyi5
     * 生日前一天针对符合的用户自动订购并下发短信
     * @data 2019-3-13
     * @throws Exception
     */
    	
    	
    	
		//1.查询全球通标签用户
    	
    			//2.查询用户生日
    				
    				//3.明天生日的用户
    							//5.下一周期生日的用户,短信下发
	
	 /**
    * 插到期处理表 add by dengyi5
    * 生日前一天针对符合的用户自动订购
	 * @return 
    * @data 2019-3-25
    * @throws Exception
    */
	
	/**
     * 订购成功短信下发
     * 
     * @data 2019-03-14
     * @param data
     * @throws Exception
     */

        		
	
	/**
	 * 通过templateId获取到短信模板，并将其中变量替换
	 * @param templateId
	 * @param iData
	 * @return
	 */
		//根据模板ID获取短信
		// 注意：短信模板的占位名需要和iData里的key保持一致
	/**
	 * 
	 * @param input SERIAL_NUMBER、USER_ID、新增优惠编码、删除优惠编码
	 * @return  返回字符串6050为校验不通过     返回字符串0为校验通过
	 * @throws Exception huangmx5
	 */
	public IData checkChangeProduct(IData input) throws Exception{
		
		IData returnData = new DataMap();
		String existWid = "0";// 0没宽带  1有完工宽带（排除校园宽带）  2有未完工宽带
		String serialNumber = input.getString("SERIAL_NUMBER");
		IData param = new DataMap();
    	param.put("SERIAL_NUMBER", serialNumber);
    	param.put("REMOVE_TAG", "0");
		IDataset userIdData = Dao.qryByCode("TF_F_USER", "SEL_BY_SERIAL_NUMBER", param);
		IData userData = userIdData.getData(0);
    	String userId = userData.getString("USER_ID");
    	//begin 1.判断用户是否已经存在宽带（非校园宽带）
    	IData widenetInfo = UcaInfoQry.qryUserInfoBySn("KD_" + serialNumber);
    	if(IDataUtil.isNotEmpty(widenetInfo)){
    		//查询是否是校园宽带
    		IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber("KD_" + serialNumber);
    		if (IDataUtil.isNotEmpty(widenetInfos)){
    			String wideType = widenetInfos.getData(0).getString("RSRV_STR2");
    			//4是校园宽带
    			if(!"4".equals(wideType)){
    				//存在非校园宽带才拦截ban
    				existWid = "1";
    			}else{
    				//校园宽带不走规则
    				//return false;
    				returnData.put("RESULT_CODE", "0");
    		    	returnData.put("RESULT_DESCRIBE", "成功!");
    				return returnData;
    			}
    		}
    		
    	}
    	String offerType = input.getString("ELEMENT_TYPE_CODE");
    	String offerCode = input.getString("ELEMENT_ID");
    	IDataset offerCodeData = UpcCall.qryComRelOffersByOfferId(offerType,offerCode);
    	for (int i = 0, size = offerCodeData.size(); i < size; i++)
        {
            IData element = offerCodeData.getData(i);
        	//获取优惠编码
            String elementId = element.getString("OFFER_CODE");
            if(StringUtils.isNotBlank(elementId)){
            	//String modifyTag = element.getString("MODIFY_TAG");
            	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
            	//如果add的是融合套餐，则不拦截flag=true
            	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
            		//return false;
            		returnData.put("RESULT_CODE", "0");
                	returnData.put("RESULT_DESCRIBE", "成功!");
            		return returnData;
                }
            }
        }
    	//end 2.判断新增的是不是融合套餐，如果是不走规则
    	//begin查询用户现有套餐
    	String mainProductId = "";
     	IData paramUserId = new DataMap();
    	paramUserId.put("USER_ID", userId);
    	IDataset productData = Dao.qryByCode("TF_F_USER_PRODUCT", "SEL_ALL_BY_USERID", paramUserId);
    	if(IDataUtil.isNotEmpty(productData)){
    		for (int i = 0, size = productData.size(); i < size; i++){
    			IData proData = productData.getData(i);
    			String mainTag = proData.getString("MAIN_TAG");
    			//1是主产品
    			if(mainTag.equals("1")){
    				mainProductId = proData.getString("PRODUCT_ID");
    			}
    		}
    	}
    	IDataset offerCodeData1 = UpcCall.qryComRelOffersByOfferId(offerType,mainProductId);
    	//end查询用户现有套餐
    	if("1".equals(existWid)){//存在完工的非校园宽带
    		
    		//begin 3.用户存在完工的非校园宽带并且没有宽带1+也没有宽带包年活动也没有度假宽带活动
        	//判断用户是否办理宽带1+活动
        	IDataset widenewtInfo1 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"69908001");
        	if(IDataUtil.isNotEmpty(widenewtInfo1)&&widenewtInfo1.size()>0){
        		//用户有办理宽带1+活动,不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//判断用户是否办理包年活动
        	IDataset widenewtInfo2 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"67220428");
        	if(IDataUtil.isNotEmpty(widenewtInfo2)&&widenewtInfo2.size()>0){
        		//用户有办理包年活动,不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//判断用户是否有度假宽带活动,度假宽带月、季、半年套餐（海南）
        	IDataset widenewtInfo3 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66002202");
        	if(IDataUtil.isNotEmpty(widenewtInfo3)&&widenewtInfo3.size()>0){
        		//用户有度假宽带活动,不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//判断用户是否有度假宽带活动,赠送60元手机报停专项款（度假宽带保有专用）
        	IDataset widenewtInfo4 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66000279");
        	if(IDataUtil.isNotEmpty(widenewtInfo4)&&widenewtInfo4.size()>0){
        		//用户有度假宽带活动,不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//判断用户是否有度假宽带活动,度假宽带2019
        	IDataset widenewtInfo5 = SaleActiveInfoQry.getUserSaleActiveByProductId(userId,"66004809");
        	if(IDataUtil.isNotEmpty(widenewtInfo5)&&widenewtInfo5.size()>0){
        		//用户有度假宽带活动,不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//end 3.用户存在完工的非校园宽带并且没有宽带1+也没有宽带包年活动也没有度假宽带活动
        	//begin 4.判断删除的是否为融合套餐，是的话拦截
        	if (IDataUtil.isNotEmpty(offerCodeData1)) {
           		for (int i = 0, size = offerCodeData1.size(); i < size; i++){
                    IData element = offerCodeData1.getData(i);
                    //获取优惠编码
                    String elementId = element.getString("OFFER_CODE");
                    if(StringUtils.isNotBlank(elementId)){
                    	//融合套餐查询
                    	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
                    	//删除的是融合套餐
                    	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
                    		//return true;
                    		returnData.put("RESULT_CODE", "6050");
                    		returnData.put("RESULT_DESCRIBE", "尊敬的客户：您已开通宽带业务，但本次申请的新套餐不包含宽带权益。为避免产生额外的宽带费用，建议您到当地移动营业厅或拨打10086热线办理套餐变更业务。【中国移动】");
            				return returnData;
                        }
                    }
                        	 
                }
                     	
             } 
        	//end 4.判断删除的是否为融合套餐，是的话拦截
        	 
    	}else{//判断是否有新装未完工并没有撤单的宽带
    		
    		//begin 5.判断是否有未完工宽带，没有的话不走规则
        	IData data = new DataMap();
        	data.put("SERIAL_NUMBER", "KD_"+serialNumber);
        	data.put("TRADE_TYPE_CODE", "600");
        	//isNot不为空的话返回false
        	boolean isNot = AcctInfoQry.getPayrelaAdvFlag(data);
        	if(isNot == false){
        		//false说明有未完工的
        		existWid = "2";
        	}else{
        		//没有未完工的宽带，不走规则
        		returnData.put("RESULT_CODE", "0");
            	returnData.put("RESULT_DESCRIBE", "成功!");
        		return returnData;
        	}
        	//end 5.判断是否有未完工宽带，没有的话不走规则
        	if("2".equals(existWid)){//存在未完工的宽带
        		//begin 6.判断是否存在预受理的宽带1+或宽带包年活动或度假宽带活动
                IDataset data1 = SaleActiveInfoQry.getUserBookSaleActive(userId,"69908001","1");
        		if(IDataUtil.isNotEmpty(data1)&&data1.size()>0){
            		//用户有预受理的宽带1+活动
        			returnData.put("RESULT_CODE", "0");
        	    	returnData.put("RESULT_DESCRIBE", "成功!");
        			return returnData;
            	}
        		
        		IDataset data2 = SaleActiveInfoQry.getUserBookSaleActive(userId,"67220428","1");
                if(IDataUtil.isNotEmpty(data2)&&data2.size()>0){
            		//用户有预受理的宽带包年活动
                	returnData.put("RESULT_CODE", "0");
                	returnData.put("RESULT_DESCRIBE", "成功!");
            		return returnData;
            	}
                //预受理度假宽带
                IDataset data3 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66002202","1");
                if(IDataUtil.isNotEmpty(data3)&&data3.size()>0){
            		//用户有预受理的度假宽带月、季、半年套餐（海南）,不走规则
                	returnData.put("RESULT_CODE", "0");
                	returnData.put("RESULT_DESCRIBE", "成功!");
            		return returnData;
            	}
                
                IDataset data4 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66000279","1");
                if(IDataUtil.isNotEmpty(data4)&&data4.size()>0){
            		//用户有预受理的赠送60元手机报停专项款（度假宽带保有专用）,不走规则
                	returnData.put("RESULT_CODE", "0");
                	returnData.put("RESULT_DESCRIBE", "成功!");
            		return returnData;
            	}
                
                IDataset data5 = SaleActiveInfoQry.getUserBookSaleActive(userId,"66004809","1");
                if(IDataUtil.isNotEmpty(data5)&&data5.size()>0){
            		//用户有预受理的度假宽带2019,不走规则
                	returnData.put("RESULT_CODE", "0");
                	returnData.put("RESULT_DESCRIBE", "成功!");
            		return returnData;
            	}
                //end 6.判断是否存在预受理的宽带1+或宽带包年活动或度假宽带活动
              //begin 7.判断删除的是否为融合套餐，是的话拦截
            	if (IDataUtil.isNotEmpty(offerCodeData1)) {
	           		for (int i = 0, size = offerCodeData1.size(); i < size; i++){
                        IData element = offerCodeData1.getData(i);
                        //获取优惠编码
                        String elementId = element.getString("OFFER_CODE");
                        if(StringUtils.isNotBlank(elementId)){
                        	//融合套餐查询
                        	IDataset commset = RouteInfoQry.getCommparaByCode("CSM", "368", elementId, "ZZZZ");
                        	//删除的是融合套餐
                        	if(IDataUtil.isNotEmpty(commset) && commset.size()>0){
                        		//return true;
                        		returnData.put("RESULT_CODE", "6050");
                        		returnData.put("RESULT_DESCRIBE", "尊敬的客户：您已开通宽带业务，但本次申请的新套餐不包含宽带权益。为避免产生额外的宽带费用，建议您到当地移动营业厅或拨打10086热线办理套餐变更业务。【中国移动】");
                				return returnData;
                            }
                        }
                            	 
	                }
                         	
                 } 
            	//end 7.判断删除的是否为融合套餐，是的话拦截
        	}
    	}
    	returnData.put("RESULT_CODE", "0");
    	returnData.put("RESULT_DESCRIBE", "成功!");
		return returnData;
	}
    
	

    
}
