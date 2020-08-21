
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.common.util.Utility;
import com.ailk.database.dbconn.ConnectionManagerFactory;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.org.apache.commons.lang3.time.DateUtils;
import com.ailk.org.apache.commons.lang3.time.FastDateFormat;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.*;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.PlatConstants;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.*;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.SvcData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BaseTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductModuleCalDate;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.util.ProductTimeEnv;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.TradeProcess;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.SccCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCallIntf;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityRuleCheck;
import com.asiainfo.veris.crm.order.soa.person.busi.custservicecapabilityopen.CustServiceHelper;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Date;
import java.util.List;


/**
 * Copyright: Copyright 2014 Asiainfo-Linkage
 * 
 * @ClassName: ChangeProductIBossSVC.java
 * @Description: IBOSS落地产品变更相关接口
 * @version: v1.0.0
 * @author: maoke
 * @date: Jun 11, 2014 10:45:50 AM Modification History: Date Author Version Description
 *        -------------------------------------------------------* Jun 11, 2014 maoke v1.0.0 修改原因
 */
public class ChangeProductIBossSVC extends CSBizService
{
	
	protected static Logger log = Logger.getLogger(ChangeProductIBossSVC.class);
	
    /**
     * @Description: IBOSS订购、退订彩铃服务
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 10, 2014 9:07:38 PM
     */
    public IDataset changeColorRing(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "KIND_ID");//
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 手机号

        String kindId = data.getString("KIND_ID");

        IData param = new DataMap();

        if ("BIP4B301_T4001001_1_0".equals(kindId))// 彩铃开通KIND_ID
        {
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        }
        else if ("BIP4B303_T4001003_1_0".equals(kindId))// 彩铃关闭KIND_ID
        {
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        }
        else
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_41);
        }

        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("ELEMENT_ID", "20");// 彩铃服务编码
        param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
        param.put("BOOKING_TAG", "0");// 非预约

        IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
        
        return result;
    }
    
    /**
     * 视频彩铃受理
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset changeVoLTEColorRing(IData data) throws Exception
    {
    	IDataset result = new DatasetList();
    	
    	IDataUtil.chkParam(data, "MODIFY_TAG");//
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 手机号
        
        IDataset commList=CommparaInfoQry.getCommpara("CSM","2017","VIDEO_COLORRING_SERV","ZZZZ");
        if(IDataUtil.isEmpty(commList))
        {
            String errors = "视频彩铃服务静态参数【VIDEO_COLORRING_SERV】未配置，请联系管理员！";
            CSAppException.apperr(CrmCommException.CRM_COMM_103, errors);
        }

        IData param = new DataMap();

    	IDataset userInfos = UserInfoQry.getUserInfoBySn(data.getString("SERIAL_NUMBER"), "0");
    	
    	if(userInfos.isEmpty())
    	{
    		CSAppException.appError("1020","获取用户资料无数据!");
    	}

    	String userState = userInfos.getData(0).getString("USER_STATE_CODESET");
    	
    	if (!"0".equals(userState))
    	{
    		CSAppException.appError("1020","用户状态非法!");
    	}

    	String userId = userInfos.getData(0).getString("USER_ID");
    	
		param.put("MODIFY_TAG", data.getString("MODIFY_TAG"));
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("ELEMENT_ID", commList.getData(0).getString("PARA_CODE1"));// 视频彩铃服务编码
        param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
        param.put("BOOKING_TAG", "0");// 非预约

        result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
        
        //是否开通volte服务
        IDataset colorRingSvc = UserSvcInfoQry.getSvcUserId(userId,commList.getData(0).getString("PARA_CODE2"));
		if (IDataUtil.isNotEmpty(colorRingSvc))
		{
			result.getData(0).put("VolTE", "1");
		}
		else
		{
			result.getData(0).put("VolTE", "0");
		}
        
		result.getData(0).put("Rslt", "0");

        return result;
    }

    /**
     * @Description: WAP二期全球通套餐受理
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 11, 2014 3:33:58 PM
     */
    public IDataset changeGoTone4Wap(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "KIND_ID");
        IDataUtil.chkParam(input, "IDTYPE");
        IDataUtil.chkParam(input, "IDITEMRANGE");
        IDataUtil.chkParam(input, "OPRNUMB");
        IDataUtil.chkParam(input, "BIZTYPE");
        IDataUtil.chkParam(input, "CHANNELLD");
        IDataUtil.chkParam(input, "SESSIONID");
        IDataUtil.chkParam(input, "IDENTCODE");
        IDataUtil.chkParam(input, "PACKID");
        IDataUtil.chkParam(input, "EFFECTTYPE");

        // 构建UCA,校验号码
        input.put("SERIAL_NUMBER", input.getString("IDITEMRANGE", ""));
        UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));

        // 检查用户凭证号
        IDataset userSessions = UserInfoQry.getUserWapSession(input.getString("SERIAL_NUMBER"), input.getString("SESSIONID"));

        if (IDataUtil.isEmpty(userSessions))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_911);
        }
        else
        {
            String credenceNo = userSessions.getData(0).getString("CREDENCE_NO");
            if (StringUtils.isBlank(credenceNo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_938);
            }
            else
            {
                if (!credenceNo.equals(input.getString("IDENTCODE")))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_912);
                }
            }
        }

        String packId = input.getString("PACKID");

        IDataset packTransProducts = CommparaInfoQry.getCommpara("CSM", "2688", packId, BizRoute.getRouteId());

        if (IDataUtil.isEmpty(packTransProducts))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_411);
        }
        else
        {
            IData productData = new DataMap();

            productData.put("SERIAL_NUMBER", input.getString("IDITEMRANGE"));// 标识号码
            productData.put("ELEMENT_ID", packTransProducts.getData(0).getString("PARA_CODE1"));// 新订购套餐编码
            productData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);// 产品
            productData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 修改
            productData.put("BOOKING_TAG", "0");// 0-非预约 1-预约
            if ("1".equals(input.getString("PRE_TYPE", "")))
            {
                productData.put("PRE_TYPE", "1");
            }

            return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", productData);
        }
        return null;
    }

    /**
     * @Description: WAP二期全球通套餐校验
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 11, 2014 3:34:03 PM
     */
    public IDataset changeGoTone4WapCheck(IData input) throws Exception
    {
        input.put("PRE_TYPE", "1");

        return changeGoTone4Wap(input);
    }

    /**
     * @Description: 统一门户WWW网站全球通统一套餐办理接口
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 11, 2014 3:33:53 PM
     */
    public IDataset changeGoTone4WWW(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "IN_MODE_CODE");
        IDataUtil.chkParam(input, "KIND_ID");
        IDataUtil.chkParam(input, "IDTYPE");
        IDataUtil.chkParam(input, "IDITEMRANGE");
        IDataUtil.chkParam(input, "OPR_NUMB");
        IDataUtil.chkParam(input, "PACK_CODE");
        IDataUtil.chkParam(input, "OPR_CODE");

        // 本业务参数
        if (!"01".equals(input.getString("OPR_CODE", "")))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_425);
        }

        String packId = input.getString("PACK_CODE");

        IDataset packTransProducts = CommparaInfoQry.getCommpara("CSM", "2691", packId, BizRoute.getRouteId());

        if (IDataUtil.isEmpty(packTransProducts))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_506, "错误", "2691");
        }
        else
        {
            IData productData = new DataMap();

            productData.put("SERIAL_NUMBER", input.getString("IDITEMRANGE"));// 标识号码
            productData.put("ELEMENT_ID", packTransProducts.getData(0).getString("PARA_CODE1"));// 新订购套餐编码
            productData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);// 产品
            productData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 修改
            productData.put("BOOKING_TAG", "0");// 0-非预约 1-预约

            return CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", productData);
        }
        return null;
    }

    /**
     * @Description: IBOSS主产品变更
     * @param input
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 11, 2014 11:03:21 AM
     */
    public IData changeMainProduct(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "IDVALUE");
        IDataUtil.chkParam(input, "NEW_PRODUCT_ID");
    	// 先对身份凭证进行鉴权
    	String serialNumber = input.getString("IDVALUE");
    	String identCode = input.getString("IDENT_CODE", "");

    	String bizCodeType = input.getString("BIZ_TYPE_CODE","");//渠道编码
    	
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(userInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_112);
        }
        
        String channelId= input.getString("CHANNEL_ID","");//微信微博渠道编码
        String intf_type="";
        // 身份鉴权
        //if ("".equals(bizCodeType) || bizCodeType == null){
		if ("62".equals(channelId) || "76".equals(channelId)||"62".equals(bizCodeType) || "76".equals(bizCodeType)){
				//校验客户凭证
				IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode,serialNumber);
				if(IDataUtil.isEmpty(dataset)){
					CSAppException.apperr(CrmUserException.CRM_USER_938);
				}
		//	}
		}else if(CustServiceHelper.isCustomerServiceChannel(bizCodeType)){//一级客服升级业务能力开放平台身份鉴权
            IData identPara =  new DataMap();
            identPara.put("SERIAL_NUMBER", serialNumber);
            identPara.put("IDENT_CODE", identCode);
            CustServiceHelper.checkCertificate(identPara);
            intf_type="01";
        }
		else { //if ("07".equals(bizCodeType)){// 移动商城
	    	String businessCode = input.getString("BUSINESS_CODE", "");
	    	String identCodeType = input.getString("IDENT_CODE_TYPE", "");
	    	String identCodeLevel = input.getString("IDENT_CODE_LEVEL", "");
	    	String userType = input.getString("USER_TYPE", "");
			IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
	    	if (IDataUtil.isEmpty(idents))
	    	{
	    		CSAppException.apperr(CrmCommException.CRM_COMM_915);
	    	}
	    	
	    	if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
	    	{
	    		CSAppException.apperr(CrmCommException.CRM_COMM_1103);
	    	}

	    	SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
			
		}
        
    	String newProductId=input.getString("NEW_PRODUCT_ID");
    	IData normalUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isEmpty(normalUserInfo))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_1);
        }
        
    	//验证用户的星级是否有权限办理产品
        IDataset checkProducts=CommparaInfoQry.getCommparaInfoByCode2
        		("CSM", "1414", newProductId, "P", "0898");
        
        if(IDataUtil.isNotEmpty(checkProducts)){
        	//获取用户星级信息
        	String userId=normalUserInfo.getString("USER_ID");
        	UcaData uca=UcaDataFactory.getUcaByUserId(userId);
        	String userCredit=uca.getUserCreditClass(); 
        	
    		int checkProductsI=Integer.parseInt(userCredit);
    		
    		boolean isValid=false;
    		
        	for(int i=0,size=checkProducts.size();i<size;i++){
        		String minClass=checkProducts.getData(i).getString("PARA_CODE3","");
        		String maxClass=checkProducts.getData(i).getString("PARA_CODE4","");
        		String realProductId=checkProducts.getData(i).getString("PARA_CODE1","");
        		
        		if(!minClass.equals("")&&!maxClass.equals("")){
        			int minClassI=Integer.parseInt(minClass);
        			int maxClassI=Integer.parseInt(maxClass);
        			
        			if(checkProductsI>=minClassI&&checkProductsI<=maxClassI){
        				isValid=true;
        			}
        		}else if(!minClass.equals("")){
        			int minClassI=Integer.parseInt(minClass);
        			
        			if(checkProductsI>=minClassI){
        				isValid=true;
        			}
        		}else if(!maxClass.equals("")){
        			int maxClassI=Integer.parseInt(maxClass);
        			
        			if(checkProductsI<=maxClassI){
        				isValid=true;
        			}
        		}
        		
        		//如果匹配到星级的产品
        		if(isValid){
        			newProductId=realProductId;
        			input.put("NEW_PRODUCT_ID",realProductId);
        			break;
        		}
        		
        	}
        	
        	if(!isValid){
        		String strError = "用户星级不具备办理此产品！";
                Utility.error("2001", null, strError);
        	}
        }
    	
    	
		//如果是飞享套餐，调用新接口；因为移动商城接口已经调用了此接口，而又新增了飞享套餐业务，目前接口不支持此类业务受理
        if(input.getString("NEW_PRODUCT_ID", "").startsWith("qwc"))
        	return changeProduct4MM(input);
    	
        IData productData = new DataMap();

        //移动商城2.8 主套餐变更 新增互斥列表入参  add by huangyq        
        productData.put("OPPOSE_PROD_INFO", input.getDataset("OPPOSE_PROD_INFO",new DatasetList()));
        productData.put("UNI_CHANNEL", input.getString("UNI_CHANNEL","")); // 保存渠道编码
        productData.put("NET_EXPENSES_CODE", input.getString("NET_EXPENSES_CODE","")); // 保存全网资费编码
        productData.put("OPRNUMB", input.getString("OPRNUMB","")); //流水号
        
       
        
        //end
        productData.put("SERIAL_NUMBER", input.getString("IDVALUE"));// 标识号码
        productData.put("ELEMENT_ID", input.getString("NEW_PRODUCT_ID"));// 新订购套餐编码
        productData.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);// 产品
        productData.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 产品修改标识
        productData.put("BOOKING_TAG", "0");// 0-非预约 1-预约
        
        IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", productData);

        IData result = results.getData(0);
        result.put("OPR_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
        result.put("UPDATE_PLAN_RSLT", "0");//0：成功、1：失败
        if(!"".equals(intf_type)){
        	result.put("INTF_TYPE", intf_type);
		}
        return result;
    }

    /**
     * @Description: 退订
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 12, 2014 3:14:05 PM
     */
    public IDataset closeSvc(IData data) throws Exception
    {
        String idType = data.getString("IDTYPE");
        String serialNumber = data.getString("IDVALUE");
        String productType = data.getString("PRODUCT_TYPE");
        String oprNumb = data.getString("OPRNUMB");
        String operCode = data.getString("OPR_CODE");
        String ibossProductId = data.getString("PRODUCT_ID");
        String netExpensesCode = data.getString("NET_EXPENSES_CODE");
        String  busiSign=data.getString("BUSI_SIGN","");
        
        String [] productInfos=ibossProductId.split("_");	
		 String appId="";
		 String isVideoPck="N";
		if(productInfos.length>0&&productInfos.length==2){//视频流量包
			ibossProductId=productInfos[0];
		    appId=productInfos[1];
		    isVideoPck="Y";			    
		}
        IData param = new DataMap();
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		
		if(IDataUtil.isEmpty(userInfo))
		{
			CSAppException.apperr(CrmUserException.CRM_USER_117,serialNumber);
		}
        param.put("SERIAL_NUMBER", serialNumber);
      //移动商城2.8 渠道编码保存 20190906 huangyq
        param.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
        if (!"01".equals(idType))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_439);
        }
        if (!"01".equals(operCode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_440);
        }
        if ("01".equals(productType))
        {
            String productId = "";
            String elementTypeCode = "";
            String platDiscnt = "";
            String bizCode = "";
            String spCode = "";

            IDataset configs = CommparaInfoQry.getCommpara("CSM", "2788", ibossProductId, BizRoute.getRouteId());
            if (IDataUtil.isNotEmpty(configs))
            {
                IData config = configs.getData(0);
                productId = config.getString("PARA_CODE1");
                elementTypeCode = config.getString("PARA_CODE2");

                platDiscnt = config.getString("PARA_CODE5", "");
                bizCode = config.getString("PARA_CODE6", "");
                spCode = config.getString("PARA_CODE7", "");
            }
            else
            {
                char head = ibossProductId.charAt(0);
                if (('P' == head) || ('D' == head) || ('S' == head))
                {
                    productId = ibossProductId.substring(1);
                    elementTypeCode = String.valueOf(head);
                }
                else
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_240, ibossProductId);
                }
            }
            if (StringUtils.isNotBlank(platDiscnt))
            {
                String oprSource = data.getString("BIZ_TYPE_CODE");

                return dealPlatBusiTrade(serialNumber, oprNumb, bizCode, spCode, PlatConstants.OPER_CANCEL_ORDER, oprSource,busiSign,netExpensesCode);
            }
            else
            {
                param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                param.put("ELEMENT_TYPE_CODE", elementTypeCode);
                param.put("ELEMENT_ID", productId);
                param.put("BOOKING_TAG", "0");// 非预约
                
                //lisw3 addby
                //定向流量视频
                param.put("NEED_CHANNEL_TAG", "UMMP");
                if("Y".equals(isVideoPck)){//如果是视频流量包则做特殊处理                                          
                    IData retData=AbilityRuleCheck.checkAppState(userInfo.getString("USER_ID"),productId,appId,userInfo.getString("EPARCHY_CODE"));
                    if(IDataUtil.isNotEmpty(retData)){
                    	param.put("IS_VIDEOPCK", isVideoPck);
                    	IData info=new DataMap(); 
                    	IDataset attrInfos=new DatasetList();
                    	info.put("ATTR_CODE", retData.getString("ATTR_CODE"));
                    	info.put("ATTR_VALUE", retData.getString("ATTR_VALUE"));//给要退订的app赋特殊值                   	
                    if("N".equals(retData.getString("IS_LAST_APP"))){//不是删除最后一个app,则走变更属性,修改
                        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_UPD); 
                        info.put("ATTR_CODE", retData.getString("ATTR_CODE"));
                    	info.put("ATTR_VALUE", "-1");//给要退订的app赋特殊值                      
                    }
                    attrInfos.add(info);
                    param.put("ATTR_PARAM", attrInfos);
                    }
                }
        		/**
        		 * REQ201709210005_关于新增一级电渠套餐办理记录入库的需求
        		 * @author zhuoyingzhi
        		 * @date 20171013
        		 */
                //业务类型标识
                param.put("RSRV_STR9", busiSign);
                //渠道标识
                param.put("RSRV_STR8", data.getString("BIZ_TYPE_CODE",""));
                
                //流水号
                param.put("OPRNUMB", data.getString("OPRNUMB",""));
                //全网统一渠道编码
                param.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
                //全网资费编码
                param.put("NET_EXPENSES_CODE", data.getString("NET_EXPENSES_CODE",""));
                /************end******************/
                // 调用产品变更总接口
                IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);

                // 设置失效时间
                String userProductId = UcaInfoQry.qryUserMainProdInfoBySn(serialNumber).getString("PRODUCT_ID", "");

                IData elementInfo = ProductInfoQry.getElementByProductIdElemId(userProductId, productId).getData(0);

                if (null != elementInfo)
                {
                    IData returnData = result.getData(0);
                    String cancelTag = elementInfo.getString("CANCEL_TAG");
                    if ("0".equals(cancelTag))
                    { // 立即取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }
                    else if ("1".equals(cancelTag))
                    { // 昨天取消
                        String curDate = SysDateMgr.addDays(-1);
                        returnData.put("EFFECT_TIME", curDate + SysDateMgr.END_DATE);
                    }
                    else if ("2".equals(cancelTag))
                    {// 今天取消
                        String curDate = SysDateMgr.addDays(0);
                        returnData.put("EFFECT_TIME", curDate + SysDateMgr.END_DATE);
                    }
                    else if ("3".equals(cancelTag))
                    { // 本账期末取消，月底取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getLastDateThisMonth());
                    }
                    else if ("4".equals(cancelTag))
                    { // 未到元素结束日期不能取消
                        returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }
                    else if ("5".equals(cancelTag))
                    {
                    	returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }
                    else if ("6".equals(cancelTag))
                    {
                    	returnData.put("EFFECT_TIME", SysDateMgr.getAddMonthsLastDay(-1,SysDateMgr.getSysDate()));
                    }else if ("7".equals(cancelTag))
                    {
                    	returnData.put("EFFECT_TIME", SysDateMgr.getSysDate());
                    }
                }

                return result;
            }
        }
        else if ("02".equals(productType))
        {
            String spCode = data.getString("SP_ID");
            String bizCode4IBoss = data.getString("BIZ_CODE");
            String oprSource = data.getString("BIZ_TYPE_CODE");

            return this.dealPlatBusiTrade(serialNumber, oprNumb, bizCode4IBoss, spCode, PlatConstants.OPER_CANCEL_ORDER, oprSource,busiSign,netExpensesCode);
        }
        else
        {
            return null;
        }
    }

    /**
     * @Description: 将2013-12-03 15:21:41转换成20131203152141
     * @param orginDateStr
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 9, 2014 8:42:54 PM
     */
    private String convertDateStr(String orginDateStr) throws Exception
    {
        return SysDateMgr.decodeTimestamp(orginDateStr, SysDateMgr.PATTERN_STAND_SHORT);
    }

    /**
     * 移动商城生效编码： 01 立即生效 02 次日生效 03 下周期生效 转换成 手机营业厅生效编码： 02立即生效 04下月生效
     * 
     * @param inparams
     * @return
     */
    private String convertEfftWay(String inEfftWay)
    {
        String result;
        if (inEfftWay.equals("01"))
            result = "02";
        else if (inEfftWay.equals("02"))
            result = "03";
        else if (inEfftWay.equals("03"))
            result = "04";
        else
            result = "";

        return result;
    }

    private String convertElementType2Mall(String eleType) throws Exception
    {
        String eleTypeMall = "";
        if (eleType.equals("D"))
            eleTypeMall = "01";// 套餐类
        else if (eleType.equals("Z"))
            eleTypeMall = "02";// 增值业务类
        else if (eleType.equals("S"))
            eleTypeMall = "03";// 服务功能类

        return eleTypeMall;
    }

    public void dealElementInfo(IDataset newProductLst, IDataset delProductLst, List<BaseTradeData> elementInfos, String elementTypeCode) throws Exception
    {

        for (BaseTradeData elementInfo : elementInfos)
        {
            IData itemInfo = elementInfo.toData();
            String elementId = getElementId(elementTypeCode, itemInfo);
            String productId = itemInfo.getString("PRODUCT_ID");
            String userId = itemInfo.getString("USER_ID");
            String instId = itemInfo.getString("INST_ID");
            if (itemInfo.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_ADD) || itemInfo.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_INHERIT))
            {
                IData newProductInfo = new DataMap();
                newProductInfo.put("ELEMENT_TYPE_CODE", convertElementType2Mall(elementTypeCode));
                newProductInfo.put("ELEMENT_ID", elementId);
                newProductInfo.put("ELEMENT_NAME", getElementName(elementTypeCode, elementId));
                newProductInfo.put("START_DATE", SysDateMgr.decodeTimestamp(itemInfo.getString("START_DATE", ""), SysDateMgr.PATTERN_STAND_SHORT));
                newProductInfo.put("END_DATE", SysDateMgr.decodeTimestamp(itemInfo.getString("END_DATE", ""), SysDateMgr.PATTERN_STAND_SHORT));
                newProductInfo.put("ELEMENT_ATTR", getElementAttr(elementTypeCode, elementId, instId, userId));
                
                /*
        		 * REQ201904160026线上渠道已订业务失效时间屏蔽需求
        		 * 对于线上渠道，当失效时间比当前时间大于12个月时，屏蔽失效时间（或显示失效时间为当年12月31日，与报纸公告一致）。
        		 */
        		String SYS_TIME = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);//当前系统时间
        		String END_TIME = itemInfo.getString("END_DATE");
        		int month = SysDateMgr.monthInterval(SYS_TIME,END_TIME);//获取两个时间的月差值
        		if(month > 12){
        			END_TIME = SYS_TIME.substring(0, 4) + "-12-31 23:59:59";
        			END_TIME = transDate(END_TIME, "yyyy-MM-dd HH:mm:SS", "yyyyMMddHHmmSS");
        			newProductInfo.put("END_DATE",END_TIME);
        		}
                
                //如果是后台绑定优惠会存在productId是-1的情况，代码会报服务【null】入参OFFER_TYPE【P】OFFER_CODE【-1】找不到对应OFFER_ID
                try {
                	newProductInfo.put("FORCE_TAG", getForceTag(productId, elementId));
                	newProductInfo.put("ORDER_TYPE", getForceTag(productId, elementId));//一二级能开对接
				} catch (Exception e) {
					newProductInfo.put("FORCE_TAG", "2");
					newProductInfo.put("ORDER_TYPE", "2");//一二级能开对接
				}
                
                newProductLst.add(newProductInfo);
            }
            else if (itemInfo.getString("MODIFY_TAG").equals(BofConst.MODIFY_TAG_DEL))
            {

                IData delProductInfo = new DataMap();
                delProductInfo.put("ELEMENT_TYPE_CODE", convertElementType2Mall(elementTypeCode));
                delProductInfo.put("ELEMENT_ID", elementId);
                delProductInfo.put("ELEMENT_NAME", getElementName(elementTypeCode, elementId));
                delProductInfo.put("ELEMENT_ATTR", getElementAttr(elementTypeCode, elementId, instId, userId));
                delProductLst.add(delProductInfo);
            }
        }
    }

    /**
     * @Description: 处理IBOSS过来平台业务
     * @param oprNumb
     * @param spCode
     * @param bizCode4IBoss
     * @param operCode
     * @param oprSource
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 2, 2014 7:32:34 PM
     */
    public IDataset dealPlatBusiTrade(String serialNumber, String oprNumb, String bizCode4IBoss, String spCode, String operCode, String oprSource,String busiSign,String netExpensesCode) throws Exception
    {
        if (bizCode4IBoss.indexOf("|") == -1)
        {
            CSAppException.apperr(ParamException.CRM_PARAM_442);
        }

        String bizCode = bizCode4IBoss.split("\\|")[0];
        String bizTypeCode = bizCode4IBoss.split("\\|")[1];

        IData platParam = new DataMap();

        platParam.put("SERIAL_NUMBER", serialNumber);
        platParam.put("TRANS_ID", oprNumb);
        platParam.put("BIZ_CODE", bizCode);
        platParam.put("BIZ_TYPE_CODE", bizTypeCode);
        platParam.put("SP_CODE", spCode);
        platParam.put("OPER_CODE", operCode);
        String changeOprSoure = CustServiceHelper.getCustomerServiceChannel(oprSource);                
        if(changeOprSoure!=null&&changeOprSoure.trim().length()>0){
            oprSource = changeOprSoure;
        }
        platParam.put("OPR_SOURCE", oprSource);
        platParam.put("IS_NEED_PF", "1");//移动商城业务默认走服开

		/**
		 * REQ201709210005_关于新增一级电渠套餐办理记录入库的需求
		 * @author zhuoyingzhi
		 * @date 20171013
		 */
        //业务类型标识
        platParam.put("RSRV_STR9", busiSign);
        //渠道标识
        platParam.put("RSRV_STR8", oprSource);
        /********************end*****************************/
        //全网资费编码
        platParam.put("RSRV_STR10", netExpensesCode);
        //流水号
        platParam.put("RSRV_STR7", oprNumb);
        IDataset result = CSAppCall.call("SS.PlatRegSVC.tradeRegIntf", platParam);

        // 增加EFFECT_TIME字段返回
        setEffectiveTime(result.getData(0), "0");
        return result;
    }

    public void dealplusElecmentInfo(IData input, IDataset newProductLst, BusiTradeData btd) throws Exception
    {

        // 查询出新产品下所有服务&优惠
        IDataset plusElement = new DatasetList();
        String newProductId = input.getString("NEW_PRODUCT_ID");

        IDataset newProductElements = ProductInfoQry.getProductOperElements(newProductId, btd.getRD().getUca().getUserEparchyCode(), CSBizBean.getVisit().getStaffId());
        // 比较获取所有可选元素:resultdata之外的元素为可选元素
        for (int i = 0; i < newProductElements.size(); i++)
        {
            int iFlag = 0;
            IData element = newProductElements.getData(i);
            String elementid = element.getString("ELEMENT_ID");
            for (int j = 0; j < newProductLst.size(); j++)
            {
                String elementidTag = newProductLst.getData(j).getString("ELEMENT_ID");
                if (elementid.equals(elementidTag))
                {
                    iFlag = 1;
                    break;
                }
            }
            if (iFlag == 0)
            {
                plusElement.add(element);
            }
        }

        for (int i = 0; i < plusElement.size(); ++i)
        {
            IData itemInfo = plusElement.getData(i);
            if (itemInfo.getString("ELEMENT_TYPE_CODE").equals(BofConst.ELEMENT_TYPE_CODE_SVC) || itemInfo.getString("ELEMENT_TYPE_CODE").equals(BofConst.ELEMENT_TYPE_CODE_DISCNT))
            {
                IData newProductInfo = new DataMap();
                setOptionalElement(itemInfo, btd.getRD().getUca().getProductId(), newProductId);

                newProductInfo.put("ELEMENT_TYPE_CODE", convertElementType2Mall(itemInfo.getString("ELEMENT_TYPE_CODE", "")));
                newProductInfo.put("ELEMENT_ID", itemInfo.getString("ELEMENT_ID", ""));
                newProductInfo.put("ELEMENT_NAME", itemInfo.getString("ELEMENT_NAME", ""));
                newProductInfo.put("FORCE_TAG", "0");// 都为可选元素
                newProductInfo.put("ORDER_TYPE", "0");// 一二级能开对接
                newProductInfo.put("START_DATE", SysDateMgr.decodeTimestamp(itemInfo.getString("START_DATE", ""), SysDateMgr.PATTERN_STAND_SHORT));
                newProductInfo.put("END_DATE", SysDateMgr.decodeTimestamp(itemInfo.getString("END_DATE", ""), SysDateMgr.PATTERN_STAND_SHORT));
                newProductInfo.put("ELEMENT_ATTR", itemInfo.getDataset("ATTR_PARAM"));
                
                /*
        		 * REQ201904160026线上渠道已订业务失效时间屏蔽需求
        		 * 对于线上渠道，当失效时间比当前时间大于12个月时，屏蔽失效时间（或显示失效时间为当年12月31日，与报纸公告一致）。
        		 */
        		String SYS_TIME = SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND);//当前系统时间
        		String END_TIME = itemInfo.getString("END_DATE");
        		int month = SysDateMgr.monthInterval(SYS_TIME,END_TIME);//获取两个时间的月差值
        		if(month > 12){
        			END_TIME = SYS_TIME.substring(0, 4) + "-12-31 23:59:59";
        			END_TIME = transDate(END_TIME, "yyyy-MM-dd HH:mm:SS", "yyyyMMddHHmmSS");
        			newProductInfo.put("END_DATE",END_TIME);
        		}
                
                newProductLst.add(newProductInfo);
            }
        }

    }

    /**
     * @Description: 获取产品变更数据
     * @param btd
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Aug 6, 2014 2:44:05 PM
     */
    public void getChangeProductData(IData input, BusiTradeData btd, IDataset newProductLst, IDataset delProductLst) throws Exception
    {
        // 获取变更产品后的新产品（新增和顺延）和删除的产品
        List<BaseTradeData> discntLists = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);
        dealElementInfo(newProductLst, delProductLst, discntLists, "D");

        List<BaseTradeData> svcLists = btd.getTradeDatas(TradeTableEnum.TRADE_SVC);
        dealElementInfo(newProductLst, delProductLst, svcLists, "S");

        List<BaseTradeData> platLists = btd.getTradeDatas(TradeTableEnum.TRADE_PLATSVC);
        dealElementInfo(newProductLst, delProductLst, platLists, "Z");

        // List<BaseTradeData> productLists = btd.getTradeDatas(TradeTableEnum.TRADE_PRODUCT);
        // dealElementInfo(newProductLst, delProductLst, productLists, "P");

        // 获取新产品下的可选产品
        dealplusElecmentInfo(input, newProductLst, btd);

    }

    private IDataset getElementAttr(String elementType, String elementId, String instId, String userId) throws Exception
    {
        IDataset AttrItemInfo = AttrItemInfoQry.getElementItemA(elementType, elementId, CSBizBean.getVisit().getStaffEparchyCode());
        if (IDataUtil.isNotEmpty(AttrItemInfo))
        {
            IDataset userAttrs = UserAttrInfoQry.queryUserAllAttrs(userId);

            if (IDataUtil.isNotEmpty(userAttrs))
            {

                IDataset returnAttrs = new DatasetList();
                for (int i = 0, size = userAttrs.size(); i < size; i++)
                {
                    IData itemA = userAttrs.getData(i);

                    if (instId.equals(itemA.getString("RELA_INST_ID")))
                    {
                        IData attr = new DataMap();
                        attr.put("ATTR_CODE", itemA.getString("ATTR_CODE"));
                        attr.put("ATTR_VALUE", itemA.getString("ATTR_VALUE"));
                        //移动商城1.5添加：增加属性名称
                        IData attrItem = null;
                        for(int x = 0; x < AttrItemInfo.size(); x++){
                        	attrItem = AttrItemInfo.getData(0);
                        	if(itemA.getString("ATTR_CODE").equals(attrItem.getString("ATTR_CODE")))
                        		break;
                        }
                        attr.put("ATTR_LABLE", attrItem == null ? "" : attrItem.getString("ATTR_LABLE"));
                        //新增结束
                        returnAttrs.add(attr);
                    }

                }
                return returnAttrs;
            }

        }

        return null;
    }

    private String getElementId(String elementType, IData itemInfo) throws Exception
    {
        String elementId = "";
        if ("Z".equals(elementType))
        {
            elementId = itemInfo.getString("SERVICE_ID");
        }
        else if ("S".equals(elementType))
        {
            elementId = itemInfo.getString("SERVICE_ID");
        }
        else if ("D".equals(elementType))
        {
            elementId = itemInfo.getString("DISCNT_CODE");
        }
        else if ("P".equals(elementType))
        {
            elementId = itemInfo.getString("PRODUCT_ID");
        }

        return elementId;
    }

    private String getElementName(String elementType, String itemInfo) throws Exception
    {
        String elementName = "";
        if ("Z".equals(elementType))
        {
   		    elementName = UPlatSvcInfoQry.getSvcNameBySvcId(itemInfo);
         //   elementName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PLATSVC", "SERVICE_ID", "SERVICE_NAME", itemInfo);
        }
        else if ("S".equals(elementType))
        {
            elementName = USvcInfoQry.getSvcNameBySvcId(itemInfo);
        }
        else if ("D".equals(elementType))
        {
            elementName = UDiscntInfoQry.getDiscntNameByDiscntCode(itemInfo);
        }
        else if ("P".equals(elementType))
        {
            elementName = UProductInfoQry.getProductNameByProductId(itemInfo);
        }

        return elementName;
    }

    private String getForceTag(String productId, String elementId) throws Exception
    {
        String forceTag = "";
        IDataset elementInfos = ProductInfoQry.getElementByProductIdElemId(productId, elementId);
        if (IDataUtil.isNotEmpty(elementInfos))
        {
            forceTag = elementInfos.getData(0).getString("FORCE_TAG", "");
        }
        return forceTag;
    }

    /**
     * @Description: 获取国际漫游套餐
     * @param code
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 10, 2014 8:31:55 PM
     */
    public String getGlobeRoamingElement(int code) throws Exception
    {
        switch (code)
        {
            case 1:
                return "99990004";// 国际漫游数据流量日套餐优惠ID
            case 2:
                return "99990005";// 国际漫游数据流量3日套餐优惠ID
            case 3:
                return "99990006";// 国际漫游数据流量7日套餐优惠ID
            case 4:
                return "99990007";// 国际漫游数据流量月套餐优惠ID
            default:
                return "";
        }
    }

    /**
     * @Description: IBOSS国际漫游订购、退订入口
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 10, 2014 7:48:57 PM
     */
    public IData globeRomingBusiness(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "IDTYPE");// 01:手机
        IDataUtil.chkParam(data, "IDITEMRANGE");// 手机号
        IDataUtil.chkParam(data, "SERVTYPE");// 服务编号:1.国漫流量日套餐 2:3天套餐 3:7天套餐 4:月套餐 5:移动数据流量功能 6:流量提醒(5,6暂时不作要求)
        IDataUtil.chkParam(data, "OPRCODE");// 01:订购 02:退订

        IData result = new DataMap();

        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("IDITEMRANGE", ""));
        param.put("BOOKING_TAG", "0");

        
		String servCode = data.getString("SERVTYPE");
		int intServCode = Integer.valueOf(servCode);

		// 定向包多天套餐 lihb
		if (8 == intServCode) {
//			IData cmccInfo=data.getData("CMCCInfo");
//			if(IDataUtil.isEmpty(cmccInfo)){
//				result.put("X_RESULTCODE", "2998");
//				result.put("X_RESULTINFO", "CMCCInfo参数不存在！");
//				return result;
//			}
			
			IDataUtil.chkParam(data, "N_DAY");// 包多天套餐参数如 3:包3天套餐
			IDataUtil.chkParam(data, "SEC_COUNTRY");// 国家代码
			IDataUtil.chkParam(data, "CMCC_IN_OUT");// 业务办理方向 ，本期只实现出访

			String dicntCode = "";
			String discntDays = data.getString("N_DAY");
			String discntArea = data.getString("SEC_COUNTRY");
			IDataset results = PkgElemInfoQry
					.getPackageElementByPackageId("99990000");
			for (int i = 0; i < results.size(); i++) {// 根据Nday、SecCountry查出优惠编码
				if (discntArea
						.equals(results.getData(i).getString("RSRV_STR3",""))
						&& discntDays.equals(results.getData(i).getString(
								"RSRV_STR4",""))) {
					dicntCode = results.getData(i).getString("ELEMENT_ID");
					break;
				}
			}
			if (StringUtils.isBlank(dicntCode)) {
				
				String errorCode="2996";
				String errorReason="尊敬的客户，您好！您订购的套餐指令错误，本次业务办理未成功。中国移动";
				
				IDataset smsContents=CommparaInfoQry.getCommNetInfo("CSM", "1031", errorCode);
				if(IDataUtil.isNotEmpty(smsContents)){
					IData smsContentData=smsContents.getData(0);
					
					StringBuilder smsContent=new StringBuilder();
					smsContent.append(smsContentData.getString("PARA_CODE1",""));
					smsContent.append(smsContentData.getString("PARA_CODE2",""));
					smsContent.append(smsContentData.getString("PARA_CODE3",""));
					smsContent.append(smsContentData.getString("PARA_CODE4",""));
					smsContent.append(smsContentData.getString("PARA_CODE5",""));
					smsContent.append(smsContentData.getString("PARA_CODE6",""));
					smsContent.append(smsContentData.getString("PARA_CODE7",""));
					smsContent.append(smsContentData.getString("PARA_CODE8",""));
					smsContent.append(smsContentData.getString("PARA_CODE9",""));
					smsContent.append(smsContentData.getString("PARA_CODE10",""));
					
					errorReason=smsContent.toString();
				}
				
				UcaData ud = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER"));
				//发送错误短信
				AsyncSendSms(param.getString("SERIAL_NUMBER"), errorReason, 
						ud.getUserId(), "国漫定向业务一级BOSS办理");
				
				CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_2996);
				
//				result.put("X_RESULTCODE", "2998");
//				result.put("X_RESULTINFO", "对应的优惠编码不存在！");
//				return result;
			}
			param.put("DISCNT_CODE", dicntCode);

		}
		param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
		param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
        
        
        if (data.getString("OPRCODE").equals("01"))
        {
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 订购
        }
        else
        {
            param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);// 退订
        }
        

        switch (Integer.valueOf(servCode))
        {
            case 1:
            case 2:
            case 3:
            case 4:
                param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                param.put("ELEMENT_ID", getGlobeRoamingElement(Integer.valueOf(servCode)));
                // 调用国漫接口 new InterRoamDayTradeIntfBean().interRoamDealIBoss(pd,param);//调用国漫接口(已经发送了短信)
                result = CSAppCall.call("SS.InterRoamDayRegSVC.otherDeal", param).getData(0);
                break;
            case 5:
                param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_SVC);
                param.put("ELEMENT_ID", "22");

                result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param).getData(0);
                break;
            case 6:
            	param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);  //添加一天国漫yangsh6
                String discntCode6=StaticUtil.getStaticValue("INTER_ROAM_DAY_TRADE_6", "6");	//获取默认的优惠
            	param.put("ELEMENT_ID",discntCode6);
            	param.put("IN_MODE_CODE_C", data.getString("IN_MODE_CODE",""));
                result = CSAppCall.call("SS.InterRoamDayRegSVC.otherDeal", param).getData(0);
                break;
            case 8:
				param.put("ELEMENT_TYPE_CODE", "D");
				try {
					CSBizBean.getVisit().setInModeCode("0");
					result =CSAppCall.call(
							"SS.InterRoamDayRegSVC.tradeReg", param).getData(0);
					CSBizBean.getVisit().setInModeCode("6");
				} catch (Exception e) {// 发送失败短信
					CSBizBean.getVisit().setInModeCode("6");
					String error=e.getMessage();
					
					String errorReason="";
					String errorCode="-1";
					
					if(error!=null&&error.indexOf("300001")!=-1){
						errorCode="300001";
						errorReason="尊敬的客户，您好！您未开通国际漫游服务，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300002")!=-1){
						errorCode="300002";
						errorReason="尊敬的客户，您好！您已订购定向套餐，不能再订购其它定向套餐，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300003")!=-1){
						errorCode="300003";
						errorReason="尊敬的客户，您好！您已订购该套餐，同一时段内不能重复订购，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300004")!=-1){
						errorCode="300004";
						errorReason="尊敬的客户，您好！您已订购定向套餐，不能再订购其它定向套餐，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("300005")!=-1){
						errorCode="300005";
						errorReason="尊敬的客户，您好！您的余额不足，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("2996")!=-1){
						errorCode="2996";
						errorReason="尊敬的客户，您好！您订购的套餐指令错误，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("2997")!=-1){
						errorCode="2997";
						errorReason="尊敬的客户，您好！您已订购该套餐，同一时段内不能重复订购，本次业务办理未成功。中国移动";
					}else if(error!=null&&error.indexOf("2998")!=-1){
						errorCode="2998";
						errorReason="尊敬的客户，您好！本次业务办理未成功，请您稍后再试 。中国移动";
					}else{
						errorCode="-1";
						errorReason="尊敬的客户，您好！对不起，本次业务办理未成功，推荐您稍后再试。中国移动";
					}
					
					IDataset smsContents=CommparaInfoQry.getCommNetInfo("CSM", "1031", errorCode);
					if(IDataUtil.isNotEmpty(smsContents)){
						IData smsContentData=smsContents.getData(0);
						
						StringBuilder smsContent=new StringBuilder();
						smsContent.append(smsContentData.getString("PARA_CODE1",""));
						smsContent.append(smsContentData.getString("PARA_CODE2",""));
						smsContent.append(smsContentData.getString("PARA_CODE3",""));
						smsContent.append(smsContentData.getString("PARA_CODE4",""));
						smsContent.append(smsContentData.getString("PARA_CODE5",""));
						smsContent.append(smsContentData.getString("PARA_CODE6",""));
						smsContent.append(smsContentData.getString("PARA_CODE7",""));
						smsContent.append(smsContentData.getString("PARA_CODE8",""));
						smsContent.append(smsContentData.getString("PARA_CODE9",""));
						smsContent.append(smsContentData.getString("PARA_CODE10",""));
						
						errorReason=smsContent.toString();
					}
					
					UcaData ud = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER"));
					
					//发送错误短信
					AsyncSendSms(param.getString("SERIAL_NUMBER"), errorReason, 
							ud.getUserId(), "国漫定向业务一级BOSS办理");
					
					throw e;
				}
				break;
            default:
                String noticeContent = "您好!本次服务操作失败，请稍候再试，谢谢您的理解。如需办理其他业务请直接发送业务名称到10086。中国移动";
                // sendSMS(pd,param,noticeContent);
                result.put("X_RESULTCODE", "2998");
                result.put("X_RESULTINFO", "本次服务开通失败!");
                result.put("X_RSPTYPE", "2");// add by ouyk
                result.put("X_RSPCODE", "2998");// add by ouyk
                return result;
        }
        return result;
    }

    /**
     * @Description: 单个及批量订购、退订
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 12, 2014 3:14:17 PM
     */
    public IData openAndCloseSvc(IData data) throws Exception
    {
    	IData resultData = new DataMap();
        IDataset orderRec = new DatasetList();
        IData paramData = new DataMap();
     // 移动商城 2.8  入参报文新增全网统一渠道编码字段，省份需要保存，用于全网渠道统计分析 add by huangyq
        paramData.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
        
    	String serialNumber = data.getString("IDVALUE");
    	// 先对身份凭证进行鉴权
    	String identCode = data.getString("IDENT_CODE", "");
    	String businessCode = data.getString("BUSINESS_CODE", "");
    	String identCodeType = data.getString("IDENT_CODE_TYPE", "");
    	String identCodeLevel = data.getString("IDENT_CODE_LEVEL", "");
    	String userType = data.getString("USER_TYPE", "");
    	String idType = data.getString("IDTYPE");
    	String oprCode = data.getString("OPR_CODE");
    	String bizCodeType = data.getString("BIZ_TYPE_CODE");//渠道编码
    	String channelId= data.getString("CHANNEL_ID","");//微信微博渠道编码
    	String oprnumb= data.getString("OPRNUMB","");//流水OPRNUMB
    	
    	log.debug("===OPRNUMB===:"+oprnumb);
        // 身份鉴权
		if ("62".equals(channelId) || "76".equals(channelId)||"62".equals(bizCodeType) || "76".equals(bizCodeType)|| CustServiceHelper.isCustomerServiceChannel(bizCodeType)) {// 微信微博和一级客服
			IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
		    	
		    if (IDataUtil.isEmpty(userInfo))
		    {
		        CSAppException.apperr(CrmUserException.CRM_USER_112);
		    }
				
			//校验客户凭证
		    if("62".equals(channelId) || "76".equals(channelId)||"62".equals(bizCodeType) || "76".equals(bizCodeType)){//微信微博身份鉴权
			IDataset dataset = UserIdentInfoQry.searchIdentCode(identCode, serialNumber);
			if(IDataUtil.isEmpty(dataset)){
				CSAppException.apperr(CrmUserException.CRM_USER_938);
			}
		    }else if(CustServiceHelper.isCustomerServiceChannel(bizCodeType)){//一级客服升级业务能力开放平台身份鉴权
	        	IData identPara =  new DataMap();
	        	identPara.put("SERIAL_NUMBER", serialNumber);
	        	identPara.put("IDENT_CODE", identCode);
	        	CustServiceHelper.checkCertificate(identPara);
	        }
			
			if (oprCode == null || "".equals(oprCode)){
				IDataset proList = new DatasetList();
				proList = data.getDataset("PRODUNCT_INFO");
					
				for (int i = 0; i < proList.size(); ++i)
		        {
					IData proData = proList.getData(i);
		            paramData.put("PRODUCT_TYPE", proData.getString("PRODUCT_TYPE"));
		            if(proData.getString("PRODUCT_ID")!=null){
		                paramData.put("PRODUCT_ID", proData.getString("PRODUCT_ID"));
		            }
		            if (proData.get("SP_ID") != null)
		            {
		                paramData.put("SP_ID", proData.getString("SP_ID"));
		            }
		            if (proData.get("BIZ_CODE") != null)
		            {
		                paramData.put("BIZ_CODE", proData.getString("BIZ_CODE"));
		            }

		            paramData.put("OPR_CODE", proData.getString("OPR_CODE"));
		            paramData.put("EFFT_WAY", proData.getString("EFFT_WAY"));
		            paramData.put("IDTYPE", idType);
		            paramData.put("IDVALUE", serialNumber);
		            paramData.put("BIZ_TYPE_CODE", bizCodeType);
                    paramData.put("NET_EXPENSES_CODE", proData.getString("NET_EXPENSES_CODE"));
                    paramData.put("OPRNUMB", oprnumb);
		            String strOprCode = paramData.getString("OPR_CODE", "");
		            if (StringUtils.isBlank(strOprCode))
		            {
		                CSAppException.apperr(ParamException.CRM_PARAM_424);
		            }

		            IDataset result;
	                if (strOprCode.equals("01"))
	                {
	                    paramData.put("EFFT_WAY", convertEfftWay(paramData.getString("EFFT_WAY", "")));
	                    result = CSAppCall.call("SS.ChangeProductIBossSVC.openSvc", paramData);
	                }
	                else
	                {
	                    paramData.put("OPR_CODE", "01");// 业务退订要求操作码为01,此处重新赋值,大接口中"01-业务开通 02-退订"
	                    result = CSAppCall.call("SS.ChangeProductIBossSVC.closeSvc", paramData);
	                }

		            if (paramData.getString("PRODUCT_TYPE", "").equals("01"))
		            {
		                result.getData(0).put("PRODUCT_ID", paramData.getString("PRODUCT_ID", ""));
		            }
		            else if (paramData.getString("PRODUCT_TYPE", "").equals("02"))
		            {
		                result.getData(0).put("SP_ID", paramData.getString("SP_ID", ""));
		                result.getData(0).put("BIZ_CODE", paramData.getString("BIZ_CODE", ""));
		            }

		            result.getData(0).put("EFFECT_TIME", convertDateStr(result.getData(0).getString("EFFECT_TIME",SysDateMgr.getSysDate())));
		            orderRec.add(result.getData(0));
		        }
					
				resultData.put("ORDER_REC", orderRec);
				resultData.put("PRODORDERREC", orderRec);
				resultData.put("UNICANCEL_RSLT", "0");//0成功，1失败
			}else{
				data.put("EFFT_WAY",
						convertEfftWay(data.getString("EFFT_WAY", "")));
				IDataset result = CSAppCall.call("SS.ChangeProductIBossSVC.openSvc",data);
				
				if (data.getString("PRODUCT_TYPE", "").equals("01")) {
					result.getData(0).put("PRODUCT_ID",data.getString("PRODUCT_ID", ""));
				} else if (data.getString("PRODUCT_TYPE", "").equals("02")) {
					result.getData(0).put("SP_ID", data.getString("SP_ID", ""));
					result.getData(0).put("BIZ_CODE",data.getString("BIZ_CODE", ""));
				}

				result.getData(0).put("EFFECT_TIME",convertDateStr(result.getData(0).getString("EFFECT_TIME",SysDateMgr.getSysDate())));
				result.getData(0).put("OPR_TIME",SysDateMgr.getSysDate("yyyyMMddHHmmss"));
				result.getData(0).put("PROD_ORDER_RSLT", "0");//0：成功、1：失败
				orderRec.add(result.getData(0));
				
				resultData.put("RESPONSEINFO", orderRec);
				resultData.putAll(result.getData(0));
			}
		}else{
			if(!("1".equals(data.getString("NO_AUTH","")))){
				IDataset idents = UserIdentInfoQry.checkIdent(identCode, businessCode, identCodeType, identCodeLevel, serialNumber);
		    	if (IDataUtil.isEmpty(idents))
		    	{
		    		CSAppException.apperr(CrmCommException.CRM_COMM_915);
		    	}
		    	
		    	if (StringUtils.equals(identCodeType, "02") && StringUtils.isBlank(businessCode))
		    	{
		    		CSAppException.apperr(CrmCommException.CRM_COMM_1103);
		    	}
	
		    	SccCall.getUSPRequestInfo(serialNumber, userType, identCode, identCodeType, identCodeLevel, "identAuth");
			}
	        paramData.putAll(data);

	        Object productTypeInfo = data.get("PRODUCT_TYPE");
	
	        if (productTypeInfo instanceof String)// 表示单个业务
	        {
	            String strOprCode = paramData.getString("OPR_CODE", "");
	
	            if (StringUtils.isBlank(strOprCode))
	            {
	                CSAppException.apperr(ParamException.CRM_PARAM_424);
	            }
	
	            IDataset result = new DatasetList();
	
	            if (strOprCode.equals("01"))
	            {
	                data.put("EFFT_WAY", convertEfftWay(data.getString("EFFT_WAY", "")));
	                result = CSAppCall.call("SS.ChangeProductIBossSVC.openSvc", data);
	            }
	            else
	            {
	                data.put("OPR_CODE", "01");// 业务退订要求操作码为01,此处重新赋值,大接口中"01-业务开通 02-退订"
	                result = CSAppCall.call("SS.ChangeProductIBossSVC.closeSvc", data);
	            }
	
	            if (data.getString("PRODUCT_TYPE", "").equals("01"))
	            {
	                result.getData(0).put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));
	            }
	            else if (data.getString("PRODUCT_TYPE", "").equals("02"))
	            {
	                result.getData(0).put("SP_ID", data.getString("SP_ID", ""));
	                result.getData(0).put("BIZ_CODE", data.getString("BIZ_CODE", ""));
	            }
	
	            result.getData(0).put("EFFECT_TIME", convertDateStr(result.getData(0).getString("EFFECT_TIME",SysDateMgr.getSysDate())));
	            result.getData(0).put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
	
	            orderRec.add(result.getData(0));
	        }
	        else
	        {// 批量业务
	            for (int i = 0; i < ((IDataset) productTypeInfo).size(); ++i)
	            {
	                paramData.put("PRODUCT_TYPE", data.getDataset("PRODUCT_TYPE").get(i).toString());
	                paramData.put("PRODUCT_ID", data.getDataset("PRODUCT_ID").get(i).toString());
	
	                if (data.get("SP_ID") != null)
	                {
	                    paramData.put("SP_ID", data.getDataset("SP_ID").get(i).toString());
	                }
	                if (data.get("BIZ_CODE") != null)
	                {
	                    paramData.put("BIZ_CODE", data.getDataset("BIZ_CODE").get(i).toString());
	                }
	
	                paramData.put("OPR_CODE", data.getDataset("OPR_CODE").get(i).toString());
	                paramData.put("EFFT_WAY", data.getDataset("EFFT_WAY").get(i).toString());
                    paramData.put("NET_EXPENSES_CODE", data.getDataset("NET_EXPENSES_CODE").get(i).toString());
	                String strOprCode = paramData.getString("OPR_CODE", "");
	                if (StringUtils.isBlank(strOprCode))
	                {
	                    CSAppException.apperr(ParamException.CRM_PARAM_424);
	                }
	
	                IDataset result;
	                if (strOprCode.equals("01"))
	                {
	                    paramData.put("EFFT_WAY", convertEfftWay(paramData.getString("EFFT_WAY", "")));
	                    result = CSAppCall.call("SS.ChangeProductIBossSVC.openSvc", paramData);
	                }
	                else
	                {
	                    paramData.put("OPR_CODE", "01");// 业务退订要求操作码为01,此处重新赋值,大接口中"01-业务开通 02-退订"
	                    result = CSAppCall.call("SS.ChangeProductIBossSVC.closeSvc", paramData);
	                }
	
	                if (paramData.getString("PRODUCT_TYPE", "").equals("01"))
	                {
	                    result.getData(0).put("PRODUCT_ID", paramData.getString("PRODUCT_ID", ""));
	                }
	                else if (data.getString("PRODUCT_TYPE", "").equals("02"))
	                {
	                    result.getData(0).put("SP_ID", paramData.getString("SP_ID", ""));
	                    result.getData(0).put("BIZ_CODE", paramData.getString("BIZ_CODE", ""));
	                }
	
	                result.getData(0).put("EFFECT_TIME", convertDateStr(result.getData(0).getString("EFFECT_TIME")));
	                orderRec.add(result);
	            }
	        }
	        
	        //按照李贤敏的定义，如果有多条返回结果，其中只要有失败的结果，则整体返回失败。如果未包含TRADE_ID则代表是失败的。
	        for (int i = 0; i < orderRec.size(); i++)
	        {
	        	IData temp = orderRec.getData(i);
	        	if (StringUtils.isBlank(temp.getString("TRADE_ID","")))//如果未包含TRADE_ID，则代表失败了
	        	{
	        		//如果失败，则按照与宋小威协商传给一级BOSS 
	        		//X_RSPTYPE值为2，默认成功是0；  X_RSPCODE为99；  X_RESULTCODE为99，代表失败。并且将X_RESULTINFO传出到X_RSPDESC和X_RESULTINFO
	        		String strXrespCode = temp.getString("X_RSPCODE", "99");
	        		String strXresultInfo = temp.getString("X_RESULTCODE", "99");
	        		
	        		resultData.put("X_RSPTYPE", "2");
	        		resultData.put("X_RSPCODE", strXrespCode);
	        		resultData.put("X_RSPDESC", temp.getString("X_RESULTINFO",""));
	            	resultData.put("X_RESULTCODE", strXresultInfo);
	            	resultData.put("X_RESULTINFO", temp.getString("X_RESULTINFO",""));
	        	}
	        }

	        resultData.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
			//移动接口规范：移动商城1.5.1修改，按操作时间倒序排列
			DataHelper.sort(orderRec, "EFFECT_TIME", IDataset.TYPE_STRING, IDataset.ORDER_DESCEND);
	        resultData.put("ORDER_REC", orderRec);
		}

        return resultData;
    }

    /**
     * @Description: 开通
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 12, 2014 3:13:53 PM
     */
    public IDataset openSvc(IData data) throws Exception
    {
        String idType = data.getString("IDTYPE");
        String serialNumber = data.getString("IDVALUE");
        String oprNumb = data.getString("OPRNUMB");
        String operCode = data.getString("OPR_CODE");
        String productType = data.getString("PRODUCT_TYPE");
        String efftWay = data.getString("EFFT_WAY");
        String netExpensesCode = data.getString("NET_EXPENSES_CODE");
        String ibossProductId = data.getString("PRODUCT_ID");

       String  busiSign=data.getString("BUSI_SIGN","");
       
       log.debug("===openSvc===oprNumb:"+oprNumb);
       
        if (!"01".equals(idType))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_439);
        }
        if (efftWay != null && !"".equals(efftWay) && !"02".equals(efftWay) && !"04".equals(efftWay) && !"03".equals(efftWay))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_443);
        }
        if (!"01".equals(operCode))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_448);
        }

        IData param = new DataMap();
        param.put("UNI_CHANNEL", data.getString("UNI_CHANNEL",""));
        param.put("NET_EXPENSES_CODE", data.getString("NET_EXPENSES_CODE",""));
        param.put("OPRNUMB", oprNumb);
        param.put("SERIAL_NUMBER", serialNumber);
		if(StringUtils.isNotBlank(data.getString("ELEC_TAG",""))){
			param.put("TRANSACTION_ID", data.getString("TRANSACTION_ID",""));
			param.put("ELEC_TAG", data.getString("ELEC_TAG",""));
			param.put("PAY_MONEY", data.getInt("PAY_MONEY"));//支付现金
			param.put("POINT_CHANGE_MONEY", data.getString("POINT_CHANGE_MONEY"));//积分抵扣金额
		}

        if ("01".equals(productType))
        {
            String effectType = "";
            if ("02".equals(efftWay))
            {
                effectType = "0";
            }
            else if ("04".equals(efftWay))
            {
                effectType = "1";
            }
            else
            {
                effectType = "1";
            }

            String productId = "";
            String elementTypeCode = "";
            String platDiscnt = "";
            String bizCode = "";
            String spCode = "";

            IDataset configs = CommparaInfoQry.getCommpara("CSM", "2788", ibossProductId, BizRoute.getRouteId());
            if (IDataUtil.isNotEmpty(configs))
            {
                IData config = configs.getData(0);
                productId = config.getString("PARA_CODE1");
                elementTypeCode = config.getString("PARA_CODE2");

                platDiscnt = config.getString("PARA_CODE5", "");
                bizCode = config.getString("PARA_CODE6", "");
                spCode = config.getString("PARA_CODE7", "");
            }
            else
            {
                char head = ibossProductId.charAt(0);
                if (('P' == head) || ('D' == head) || ('S' == head))
                {
                    productId = ibossProductId.substring(1);
                    elementTypeCode = String.valueOf(head);
                }
                else
                {
                    CSAppException.apperr(ProductException.CRM_PRODUCT_240, ibossProductId);
                }
            }

            if (!"".equals(platDiscnt))
            {
                String oprSource = data.getString("BIZ_TYPE_CODE");

                return dealPlatBusiTrade(serialNumber, oprNumb, bizCode, spCode, PlatConstants.OPER_ORDER, oprSource,busiSign,netExpensesCode);
            }
            else
            {
                if ("02".equals(efftWay))
                {
                    param.put("START_DATE", SysDateMgr.getSysDate());// 立即
                    param.put("BOOKING_TAG", "0");// 非预约
                }
                else if ("04".equals(efftWay))
                {
                    param.put("START_DATE", SysDateMgr.getFirstDayOfNextMonth()); // 下月
                    param.put("BOOKING_TAG", "1");// 非预约
                }
                else if ("03".equals(efftWay))
                {
                    param.put("START_DATE", SysDateMgr.getTomorrowDate());// 次日
                    param.put("BOOKING_TAG", "1");// 非预约
                }
                else
                {
                    param.put("START_DATE", SysDateMgr.getSysDate());// 默认立即生效
                    param.put("BOOKING_TAG", "0");// 非预约
                }

                param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
                param.put("ELEMENT_TYPE_CODE", elementTypeCode);
                param.put("ELEMENT_ID", productId);
                
                //00
                param.put("NEED_CHANNEL_TAG", "UMMP");

        		/**
        		 * REQ201709210005_关于新增一级电渠套餐办理记录入库的需求
        		 * @author zhuoyingzhi
        		 * @date 20171013
        		 */
                //业务类型标识
                param.put("RSRV_STR9", busiSign);
                //渠道标识
                param.put("RSRV_STR8", data.getString("BIZ_TYPE_CODE",""));
                /************end******************/
                checkBirthInput(param);//移动商城V2.5.4 add by dengyi5
                if(!"0000".equals(param.getString("X_RESULTCODE","0000")))
                {
                	return new DatasetList(param);
                }
                // 调用产品变更总接口
                IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);

                // 增加EFFECT_TIME字段返回
                setEffectiveTime(result.getData(0), effectType);
                return result;
            }
        }
        else if ("02".equals(productType))
        {
            String spCode = data.getString("SP_ID");
            String bizCode4IBoss = data.getString("BIZ_CODE");
            String oprSource = data.getString("BIZ_TYPE_CODE");
            
            return this.dealPlatBusiTrade(serialNumber, oprNumb, bizCode4IBoss, spCode, PlatConstants.OPER_ORDER, oprSource,busiSign,netExpensesCode);
        }
        else
        {
            return new DatasetList();
        }
    }
    
    //移动商城V2.5.4 add by dengyi5 
  	public void checkBirthInput(IData param) throws Exception
  	{
  		String elementList = param.getString("ELEMENT_ID");
  		//校验是否存在生日权益包资费编码，存在则获取订购时间
  		String elementId = "";
      	IDataset commparaSet = CommparaInfoQry.getCommpara("CSM", "2745", "ROAM_BIRTH", CSBizBean.getVisit().getStaffEparchyCode());
      	if (DataUtils.isNotEmpty(commparaSet))
  		{
  			elementId = commparaSet.getData(0).getString("PARA_CODE1");
  		}
      	else
      	{
      		CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取用户全球通生日权益资费编码失败");
      	}
      	if(!elementList.equals(elementId))
      	{
      		return;
      	}
      	
  		IData result = CSAppCall.call("SS.InterRoamingSVC.roamBirthQry", param).getData(0);
  		String qryResultCode = result.getString("X_RESULTCODE");
		if(!"0000".equals(qryResultCode))
		{
			String resultCode = qryResultCode;
			if("2986".equals(qryResultCode))
			{//非全球通标签用户
				resultCode = "2013";
			}
			param.put("X_RESULTCODE", resultCode);
			param.put("X_RESULTINFO", result.getString("X_RESULTINFO"));
			return;
		}
  		param.put("BIRTHDAY", result.getString("BIRTHDAY"));
  		
  		IData resultCheck = CSAppCall.call("SS.InterRoamingSVC.roamBirthCheck", param).getData(0);
  		if(!"0000".equals(resultCheck.getString("X_RESULTCODE")))
  		{
  			CSAppException.apperr(CrmCommException.CRM_COMM_103, resultCheck.getString("X_RESULTINFO"));
  		}
  		param.put("START_DATE", resultCheck.getString("BIRTH_START_DATE"));
  		param.put("END_DATE", resultCheck.getString("BIRTH_END_DATE"));
  	}
    
    /**
     * 
     * @Description: 和包流量充值订购
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 22, 2014 4:25:50 PM
     */
    public IDataset mobilePayGprsRecharge(IData data)throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 手机
        IDataUtil.chkParam(data, "PACK_CODE");// 套餐资费编码
        IDataUtil.chkParam(data, "SEQ_ID");// 操作流水号
        IDataUtil.chkParam(data, "PAYED");// 金额必须为非负整数
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
        param.put("ELEMENT_ID", this.getDiscntCode(data.getString("PACK_CODE")));// 编码
        param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
        param.put("BOOKING_TAG", "0");// 非预约
        
        IDataset result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
        
        String tradeId = "";
        String userId = "";
        if(IDataUtil.isNotEmpty(result))
        {
            String seqId = data.getString("SEQ_ID");
            tradeId = result.getData(0).getString("TRADE_ID");
            userId =  result.getData(0).getString("USER_ID");
            
            IData insertData = new DataMap();
            insertData.put("USER_ID", userId);
            insertData.put("PARTITION_ID", userId.substring(userId.length()-4));
            insertData.put("SERVICE_MODE", "SC");//此处定义SC 代表是和4G进来的
            insertData.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
            insertData.put("PROCESS_INFO", "和包流量充值记录");
            insertData.put("RSRV_STR1", tradeId);
            insertData.put("RSRV_STR2", seqId);
            insertData.put("RSRV_NUM1", "0");
            insertData.put("PROCESS_TAG", "0");
            insertData.put("STAFF_ID", this.getVisit().getStaffId());
            insertData.put("DEPART_ID", this.getVisit().getDepartId());
            insertData.put("START_DATE", SysDateMgr.getSysTime());
            insertData.put("END_DATE", SysDateMgr.END_DATE_FOREVER);
            insertData.put("INST_ID", SeqMgr.getInstId());
            
            Dao.insert("TF_F_USER_OTHERSERV", insertData);
        }
        
        return result;
    }
    
    /**
     * 
     * @Description: 和包流量充值冲正
     * @param data
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 22, 2014 7:14:29 PM
     */
    public IDataset mobilePayGprsRechargeBack(IData data)throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 手机
        IDataUtil.chkParam(data, "SEQ_ID");// 操作流水号
        IDataUtil.chkParam(data, "PAYED");// 金额必须为非负整数
        
        String serialNumber = data.getString("SERIAL_NUMBER");
        String seqId = data.getString("SEQ_ID");
        
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        IDataset result = new DatasetList();
        
        if(IDataUtil.isNotEmpty(userInfo))
        {
            String userId = userInfo.getString("USER_ID");
            IDataset scInfo = UserOtherInfoQry.getUserOtherservByPK(userId, "SC", "0", null);
            
            if(IDataUtil.isNotEmpty(scInfo))
            {
               String tradeId =  scInfo.getData(0).getString("RSRV_STR1",""); 
               String userSeqId =  scInfo.getData(0).getString("RSRV_STR2","");
               
               if(userSeqId.equals(seqId))
               {
                   IDataset tradeDiscnt = TradeDiscntInfoQry.qryTradeDiscntInfos(tradeId, BofConst.MODIFY_TAG_ADD, null);
                   if(IDataUtil.isNotEmpty(tradeDiscnt))
                   {
                       String instId = tradeDiscnt.getData(0).getString("INST_ID");
                       String discntCode = tradeDiscnt.getData(0).getString("DISCNT_CODE");
                       
                       IData param = new DataMap();
                       param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER"));
                       param.put("ELEMENT_ID", discntCode);
                       param.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                       param.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
                       param.put("INST_ID", instId);
                       param.put("BOOKING_TAG", "0");// 非预约o
                       
                       result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
                       
                       IData updData = new DataMap();
                       updData.put("USER_ID", userId);
                       updData.put("SERVICE_MODE", "SC");
                       updData.put("SERIAL_NUMBER", serialNumber);
                       updData.put("REMARK", "和包流量充值冲正");
                       updData.put("RSRV_NUM1", "0");
                       updData.put("RSRV_STR3", result.getData(0).getString("TRADE_ID"));
                       updData.put("PROCESS_TAG", "1");
                       updData.put("STAFF_ID", this.getVisit().getStaffId());
                       updData.put("DEPART_ID", this.getVisit().getDepartId());

                       Dao.executeUpdateByCodeCode("TF_F_USER_OTHERSERV", "UPDATE_BY_USERID_SERIALNUMBER", updData);                      
                   }
                   else
                   {
                       CSAppException.apperr(IBossException.CRM_IBOSS_49, tradeId);
                   }
               }
               else
               {
                   CSAppException.apperr(IBossException.CRM_IBOSS_48, seqId);
               }
            }
            else
            {
                CSAppException.apperr(IBossException.CRM_IBOSS_48, seqId);//
            }
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_126, serialNumber);
        }
        
        return result;
    }
    
    /**
     * 
     * @Description: 根据全网编码获取DISCNT_CODE
     * @param packCode
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Sep 23, 2014 3:52:32 PM
     */
    public String getDiscntCode(String packCode)throws Exception
    {
        //String discntCode = StaticUtil.getStaticValue(this.getVisit(), "TD_B_DISCNT", "ALIAS_ID", "DISCNT_CODE", packCode);
		//待定接口(根据数据转换 原接口不正确  by fangwz)
    	IDataset discntCodeDataset = UpcCallIntf.queryChaCodeValByfiledName("ALIAS_ID",packCode);
    	//UpcCall.queryTempChaByOfferTableField("D", packCode, "DISCNT_CODE", "DISCNT_CODE");
		String discntCode = "";
		if(IDataUtil.isNotEmpty(discntCodeDataset))
		{
			discntCode = discntCodeDataset.getData(0).getString("OFFER_CODE","");
		}
        
        if(StringUtils.isBlank(discntCode))
        {
            CSAppException.apperr(IBossException.CRM_IBOSS_50, packCode);
        }
        
        return discntCode;
    }
    
    /**
     * @Description: 产品变更查询新老产品数据4电渠
     * @param input
     * @return
     * @throws Exception
     * @author: zhuyu
     * @date: Jul 24, 2014 10:19:19 AM
     */
    public IData queryChangeProductInfoIBoss(IData input) throws Exception
    {
        OrderDataBus dataBus = DataBusManager.getDataBus();
        dataBus.setOrderTypeCode("110");
        dataBus.setAcceptTime(SysDateMgr.getSysDate());

        input.put("TRADE_TYPE_CODE", "110");
        input.put("X_TRANS_CODE", "SS.ChangeProductRegSVC.ChangeProduct");
        
        
      //如果是飞享套餐，调用新接口；因为移动商城接口已经调用了此接口，而又新增了飞享套餐业务，目前接口不支持此类业务受理
        if(input.getString("NEW_PRODUCT_ID", "").startsWith("qwc")){
        	//2.获得ELEMENTS元素
            IDataset elements =	builderElements(input.getString("IDVALUE", ""), input.getString("NEW_PRODUCT_ID", ""));
            
            input.put("SERIAL_NUMBER", input.getString("IDVALUE"));// 标识号码
            input.put("ELEMENTS", elements);
            input.put("X_TRANS_CODE", "SS.ChangeProductRegSVC.ChangeProductMulti");
            //清除主产品变更参数
            input.remove("ELEMENT_TYPE_CODE");
            input.remove("MODIFY_TAG");
            input.remove("ELEMENT_ID");
            input.remove("BOOKING_TAG");
        	input.remove("NEW_PRODUCT_ID");
        	input.remove("ELEMENT_ID");
        }

        BusiTradeData btd = TradeProcess.acceptOrder(input);

        IDataset newProductLst = new DatasetList();
        IDataset delProductLst = new DatasetList();

        this.getChangeProductData(input, btd, newProductLst, delProductLst);

        IData result = new DataMap();

        result.put("OPR_TIME", SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_SHORT));
        result.put("NEW_PRODUCT_INFO", newProductLst);
        result.put("DEL_PRODUCT_INFO", delProductLst);
        result.put("RSP_CODE", "0000");
	    result.put("RSP_DESC", "ok");
        
        // 移动商城2.8 新增互斥列表返回 add by  huangyq
 		IDataset OpposeProductList = getOpposeProductList(newProductLst,btd.getRD().getUca().getUserId());
 		IDataset opposeProdInfoList = new DatasetList();
 		IDataset resultdatas = new DatasetList();
 		if(IDataUtil.isNotEmpty(OpposeProductList)){
 			for(int i=0;i<OpposeProductList.size();i++){
 				IData opposeProduct = OpposeProductList.getData(i);
 				IData opposeInfo = new DataMap();
 				opposeInfo.put("ELEMENT_TYPE_CODE", convertElementType2Mall(opposeProduct.getString("ELEMENT_TYPE_CODE","")));
 				opposeInfo.put("ELEMENT_ID", opposeProduct.getString("ELEMENT_ID",""));
 				opposeInfo.put("ELEMENT_NAME", opposeProduct.getString("ELEMENT_NAME",""));
 				opposeInfo.put("INST_ID", opposeProduct.getString("INST_ID",""));
 				opposeInfo.put("START_DATE", convertDateStr(opposeProduct.getString("START_DATE","")));
 				opposeInfo.put("END_DATE", convertDateStr(SysDateMgr.getLastDateThisMonth()));
 				opposeInfo.put("MODIFY_TAG", "1");// 将互斥列表拼装成删除
 				// 查元素属性
 				IDataset attrList = AttrItemInfoQry.getElementItemA(opposeProduct.getString("ELEMENT_TYPE_CODE",""),opposeProduct.getString("ELEMENT_ID",""),getTradeEparchyCode());
 				IDataset eleAttrList = new DatasetList();
 				if(IDataUtil.isNotEmpty(attrList)){
 					for(int j=0;j<attrList.size();j++){
 	 					IData attrMap = new DataMap();
 	 					attrMap.put("ATTR_CODE", attrList.getData(j).getString("ATTR_CODE",""));
 	 					attrMap.put("ATTR_VALUE", attrList.getData(j).getString("ATTR_VALUE",""));
 	 					attrMap.put("ATTR_LABLE", attrList.getData(j).getString("ATTR_LABLE",""));
 	 					eleAttrList.add(attrMap);
 	 				}
 				}
 				opposeInfo.put("ELEMENT_ATTR",eleAttrList);
 				opposeInfo.put("ATTR_PARAM", eleAttrList);
 				opposeProdInfoList.add(opposeInfo);
 			}
 			
 			if(IDataUtil.isNotEmpty(opposeProdInfoList)){
  				result.put("OPPOSE_PROD_INFO", opposeProdInfoList);
  				result.put("OPPOSE_FLAG", "0");//0：支持一键办理
  				result.put("RSP_CODE", "3751");
  				result.put("RSP_DESC", "存在互斥列表，请先退订再办理");
  				result.put("X_RESULTCODE", "3751");
  				result.put("X_RESULTINFO", "存在互斥列表，请先退订再办理");
  				result.put("X_RSPCODE", "3751");
  			}
 			
 		}

        return result;
    }
    
    private IDataset getOpposeProductList(IDataset newProductLst,String userId) throws Exception{
    	IDataset limitList = new DatasetList();
    	IDataset userSvcElements = UserSvcInfoQry.queryUserSvcsInSelectedElements(userId);
   	 
    	//1 .查询互斥列表 
    	for(int i=0;i<newProductLst.size();i++){
    		IData newProduct = newProductLst.getData(i);
    		if("1".equals(newProduct.getString("FORCE_TAG",""))){//只查询必选的互斥
    			String elementTypeCode = newProduct.getString("ELEMENT_TYPE_CODE","");
    			if("01".equals(elementTypeCode)){
    				elementTypeCode = "D";
    			}else if("02".equals(elementTypeCode)){
    				elementTypeCode = "Z";
    			}else if("03".equals(elementTypeCode)){
    				elementTypeCode = "S";
    			}
	    		String elementId = newProduct.getString("ELEMENT_ID","");
	    		// 查询D类型互斥列表   从 PM_OFFER_REL 表
	     		IDataset limitElement = ElemLimitInfoQry.queryElementLimitByElementIdA(elementTypeCode, elementId, "D", getTradeEparchyCode());
	     		//IDataset limitElement = ElemLimitInfoQry.queryElementLimitByElementIdA(elementTypeCode, elementId, "0", getTradeEparchyCode());
	     		if (IDataUtil.isNotEmpty(limitElement)){
	     			limitList.add(limitElement);
	     		}
    		}
    		
    	}
			
    	IDataset OpposeProductList = new DatasetList();
    	//2. 过滤删除列表中的 资费、服务等
    	IDataset userDiscntElement = UserDiscntInfoQry.queryUserDiscntsInSelectedElements(userId);
    	IDataset userElements = new DatasetList();
    	if (userSvcElements.size() > 0)
		{
			userElements.addAll(userSvcElements);
		}
		if (userDiscntElement.size() > 0)
		{
			userElements.addAll(userDiscntElement);
		}
    	if(IDataUtil.isNotEmpty(limitList)){
	    	for(int k=0;k<limitList.size();k++){
	    		IDataset limitDataList = limitList.getDataset(k);
	    		if(IDataUtil.isNotEmpty(limitDataList)){
	    			for(int l=0;l<limitDataList.size();l++){
	    				IData limitData = limitDataList.getData(l);
	    				String elementId = limitData.getString("ELEMENT_ID_B","");
	    				String limitElementTypeCode = limitData.getString("ELEMENT_TYPE_CODE_B","");
	    				for(int j = 0; j < userElements.size(); j++){
	    	    			IData usrInfo = userElements.getData(j);
	    					String usrElementId = usrInfo.getString("ELEMENT_ID","");
	    					String usrElementTypeCode = usrInfo.getString("ELEMENT_TYPE_CODE","");
	    					if(elementId.equals(usrElementId) && limitElementTypeCode.equals(usrElementTypeCode)){
	    						usrInfo.put("ELEMENT_NAME", limitData.getString("REL_OFFER_NAME",""));
	    						OpposeProductList.add(usrInfo);
	    					}
	    	    		}
	    			}
	    		}
	    	}
    	}
    	
    	return OpposeProductList;
    }

    /**
     * @Description: 获取返回时间
     * @param result
     * @param flag
     * @throws Exception
     * @author: maoke
     * @date: Jun 9, 2014 8:31:50 PM
     */
    private void setEffectiveTime(IData result, String flag) throws Exception
    {
        if ("0".equals(flag))
        {
            result.put("EFFECT_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss"));
        }
        else
        {
            result.put("EFFECT_TIME", SysDateMgr.getFirstDayOfNextMonth());
        }
    }

    private void setOptionalElement(IData item, String oldProductId, String newProductId) throws Exception
    {
        String productChangeDate = null;
        //IDataset productTrans = ProductInfoQry.getProductTransInfo(oldProductId, newProductId);
        IDataset productTrans = ProductInfoQry.getProductTransInfoNew(oldProductId, newProductId);//2018/09/10-wangsc10

        if (IDataUtil.isNotEmpty(productTrans))
        {
            IData productTran = productTrans.getData(0);
            //String enableTag = productTran.getString("ENABLE_TAG");
            String enableTag = productTran.getString("ENABLE_MODE");//2018/09/10-wangsc10

            if (enableTag.equals("0"))
            {// 立即生效
                productChangeDate = SysDateMgr.getSysTime();
            }
            else if ((enableTag.equals("1")) || (enableTag.equals("2")))
            {// 下帐期生效

                productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
            }
            else if (enableTag.equals("3"))
            {
                // 按原产品的生效方效

                IData productInfo = UProductInfoQry.qryProductByPK(oldProductId);
                String enableTagOld = productInfo.getString("ENABLE_TAG");

                if ((enableTagOld.equals("0")) || (enableTagOld.equals("2")))
                {// 立即生效
                    productChangeDate = SysDateMgr.getSysTime();
                }
                else if (enableTagOld.equals("1"))
                {// 下帐期生效

                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
            else if (enableTag.equals("4"))
            {
                // 按新产品的生效方式

                IData productInfo = UProductInfoQry.qryProductByPK(newProductId);
                String enableTagNew = productInfo.getString("NEW_PRODUCT_ID");

                if ((enableTagNew.equals("0")) || (enableTagNew.equals("2")))
                {// 立即生效
                    productChangeDate = SysDateMgr.getSysTime();
                }
                else if (enableTagNew.equals("1"))
                {// 下帐期生效

                    productChangeDate = SysDateMgr.getFirstDayOfNextMonth();
                }
            }
        }

        ProductTimeEnv env = new ProductTimeEnv();
        env.setBasicAbsoluteStartDate(productChangeDate);
        ProductModuleData forceElement = null;
        if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(item.getString("ELEMENT_TYPE_CODE")))
            forceElement = new SvcData(item);
        else if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(item.getString("ELEMENT_TYPE_CODE")))
            forceElement = new DiscntData(item);

        String startDate = ProductModuleCalDate.calStartDate(forceElement, env);
        item.put("START_DATE", startDate);
        String endDate = ProductModuleCalDate.calEndDate(forceElement, startDate);
        item.put("END_DATE", endDate);
    }
    /*******************************************************************
     * @Description: 针对飞享套餐产品变更<BR/>
     * @param input
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 11, 2014 11:03:21 AM
     */
    public IData changeProduct4MM(IData input) throws Exception
    {
        IDataUtil.chkParam(input, "IDVALUE");
        IDataUtil.chkParam(input, "NEW_PRODUCT_ID");
        
        //1.如果不是飞享套餐，返回错误
        if(! input.getString("NEW_PRODUCT_ID", "").startsWith("qwc"))
        	CSAppException.apperr(ProductException.CRM_PRODUCT_19);;
        
        //2.获得ELEMENTS元素
        IDataset elements =	builderElements(input.getString("IDVALUE", ""), input.getString("NEW_PRODUCT_ID", ""));
        
        //3.调用产品变更接口
        IData productData = new DataMap();
        productData.put("SERIAL_NUMBER", input.getString("IDVALUE"));// 标识号码
        productData.put("ELEMENTS", elements);
        productData.put("UNI_CHANNEL", input.getString("UNI_CHANNEL","")); // 保存渠道编码		add by BUS202004280002
        IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProductMulti", productData);
        //4.处理结果并返回
        IData result = results.getData(0);
        result.put("OPR_TIME", SysDateMgr.decodeTimestamp(SysDateMgr.getSysTime(), SysDateMgr.PATTERN_STAND_SHORT));
        
        return result;
    }
    /***********************************************************************************
     * 针对飞享套餐特殊处理<BR/>
     * 飞享套餐传入的产品ID为"qwc + 数字"，需要转换为省内产品编码<BR/>
     * 1.如果用户主产品和转换后的产品不一致，那么需要进行主产品变更<BR/>
     * 2.如果用户主产品和转换后的产品一致，只需要进行产品内元素变更<BR/>
     * 
     * @param serialNumber	用户号码
     * @param newProductID	变更产品
     * @return
     * @throws Exception
     */
    private IDataset builderElements(String serialNumber, String newProductID)throws Exception{
		//1.根据用户号码查询用户信息
        IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if(IDataUtil.isEmpty(userInfos))
        	CSAppException.apperr(CrmUserException.CRM_USER_1);
        //2.查询用户主产品信息
        String userID = userInfos.getData(0).getString("USER_ID");
        IDataset userMainProduct = UserProductInfoQry.queryUserMainProduct(userID);
        if(IDataUtil.isEmpty(userMainProduct))
        	CSAppException.apperr(CrmUserException.CRM_USER_45, userID);
        //3.对传入的产品进行转换
		IDataset configElementList = qryConfigElements(newProductID, userInfos.getData(0).getString("EPARCHY_CODE"));
		String productID = configElementList.getData(0).getString("PRODUCT_ID");
		
		//4.1.如果是主产品变更，需要设置参数进行主产品变更
		if(! productID.equals(userMainProduct.getData(0).getString("PRODUCT_ID"))){
			IData item = new DataMap();
			item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_PRODUCT);
			item.put("ELEMENT_ID", productID);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			configElementList.add(item);
			
		//4.2.如果是元素变更，需要删除原产品下指定优惠
		}else{
			//4.2.1.查询用户已订购的飞享套餐优惠
			IDataset orderDiscnt = UserDiscntInfoQry.getVirUserDiscnts(userID, productID);
			if(IDataUtil.isEmpty(orderDiscnt))
				CSAppException.apperr(CrmUserException.CRM_USER_914, productID);
			//4.2.2.处理相同的元素：如果用户已订购了变更后套餐的元素，不用给予删除、添加操作
			dealDuplicateElements(configElementList, orderDiscnt);
			//4.2.2.退订这些优惠
			for(int i = 0; i < orderDiscnt.size(); i++){
				//如果是GPRS优惠，不能退订
				if(IDataUtil.isNotEmpty(DiscntInfoQry.getDiscntIsValid("5", orderDiscnt.getData(i).getString("DISCNT_CODE"))))
					continue;
				
				if(PersonConst.GPRS_DEFAULT_DISCNT_CODE.equals(orderDiscnt.getData(i).getString("DISCNT_CODE")))
					continue;
				
				IData item = new DataMap();
				item.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
				item.put("ELEMENT_ID", orderDiscnt.getData(i).getString("DISCNT_CODE"));
				item.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
				item.put("INST_ID", orderDiscnt.getData(i).getString("INST_ID"));
				configElementList.add(item);
			}
		}
		return configElementList;
	}
	/************************************************************************************
	 * 将传入的产品编码转换为省内的产品编码<BR/>
	 * 使用TD_S_COMMPARA表PARAM_ATTR为2801配置进行产品转换<BR/>
	 * 
	 * @param newProductID	转换前的产品编码
	 * @param eparchyCode	地州
	 * @return
	 * @throws Exception
	 */
	private IDataset qryConfigElements(String newProductID, String eparchyCode)throws Exception{
		//1.查询产品转换关系
		IDataset configElementList = CommparaInfoQry.getCommPkInfo("CSM", "2801", newProductID, eparchyCode);
		//2.没有查询到转换关系，抛出异常
		if(configElementList.isEmpty())
			CSAppException.apperr(ParamException.CRM_PARAM_359);
		
		IData configElement = configElementList.getData(0);
		String countStr = configElement.getString("PARA_CODE2");//必选元素个数
		//3.如果【必选元素个数】，不在5个内，配置已经有问题，抛出异常
		if(! countStr.matches("[1-5]"))
			CSAppException.apperr(ParamException.CRM_PARAM_145);
		String productID = configElement.getString("PARA_CODE1"), elementItem = null;
		//4.解析配置元素信息：ELEMENT_ID + '_' + ELEMENT_TYPE_CODE
		IDataset result = new DatasetList();
		IData item = null;
		for(int i = 0, len = Integer.parseInt(countStr); i < len; i++){
			elementItem = configElement.getString("PARA_CODE" + (i + 3));//取 PARA_CODE3（包括在内）后的PARA_CODE2个元素
			String[] elementInfo = elementItem.split("_");
			if(elementInfo.length != 2)
				CSAppException.apperr(ParamException.CRM_PARAM_146);
				
			item = new DataMap();
			item.put("PRODUCT_ID", productID);
			item.put("ELEMENT_TYPE_CODE", elementInfo[1]);
			item.put("ELEMENT_ID", elementInfo[0]);
			item.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);
			result.add(item);
		}
		return result;
	}
	/*****************************************************************************
	 * 去重：新增列表和删除列表中相同的元素<BR/>
	 * @param addList	新增列表
	 * @param delList	删除列表
	 */
	private void dealDuplicateElements(IDataset addList, IDataset delList){
		if(IDataUtil.isEmpty(addList) || IDataUtil.isEmpty(delList))
			return;
		
		for(int i = 0; i < addList.size(); i++){
			String addTypeCode = addList.getData(i).getString("ELEMENT_TYPE_CODE", ""), addID = addList.getData(i).getString("ELEMENT_ID", "");
			
			for(int j = 0; j < delList.size(); j++){
				if(addTypeCode.equals(BofConst.ELEMENT_TYPE_CODE_DISCNT) && addID.equals(delList.getData(j).getString("DISCNT_CODE"))){
					addList.remove(i--);
					delList.remove(j--);
				}
			}
		}
	}
	
	public final void setTrans(IData input){
		if ("6".equals(this.getVisit().getInModeCode())){  //渠道 ：热线
			if(!"".equals(input.getString("IDVALUE", ""))){
	    		String serial_number =  input.getString("IDVALUE", "");
	    		input.put("SERIAL_NUMBER",serial_number);
	    	}
			if(!"".equals(input.getString("PRODUCT_ID", ""))){
	    		String productId =  input.getString("PRODUCT_ID", "");
	    		input.put("ELEMENT_ID",productId);
	    	}
			if(!"".equals(input.getString("BUNESS_TYPE", ""))){
	    		String bunessType =  input.getString("BUNESS_TYPE", "");
	    		input.put("ELEMENT_TYPE_CODE",bunessType);
	    	}
		}
	}
	
    /**
     * @Description: 国际漫游定向套餐电渠订购入口
     * @return
     * @throws Exception
     * @author: maoke
     * @date: Jun 10, 2014 7:48:57 PM
     */
    public IDataset globeRomingBusiness4EC(IData data) throws Exception
    {
        IDataUtil.chkParam(data, "SERIAL_NUMBER");// 手机号
        IDataUtil.chkParam(data, "NDAY");// 包多天套餐参数如 3:包3天套餐
        IDataUtil.chkParam(data, "SECCOUNTRY");// 国家代码
        
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        param.put("BOOKING_TAG", "0");
        param.put("CMCCINOUT", "0");// 业务办理方向 ，本期只实现出访
        
        //加入二次确认校验
        String strPreType = data.getString("PRE_TYPE", "");
        if(StringUtils.isNotBlank(strPreType))
        {
        	param.put("PRE_TYPE", strPreType);
        }
        
        String dicntCode = "";
        String discntDays = data.getString("NDAY");
        String discntArea = data.getString("SECCOUNTRY");
//        IDataset results = PkgElemInfoQry.getPackageElementByPackageId("99990000"); 
                                    
        IDataset results=UPackageElementInfoQry.getElementInfoByGroupId("99990000");//add by hefeng 
        for (int i = 0; i < results.size(); i++){//根据Nday、SecCountry查出优惠编码
    		if(discntArea.equals(results.getData(i).getString("RSRV_STR3")) && discntDays.equals(results.getData(i).getString("RSRV_STR4"))){
    			dicntCode = results.getData(i).getString("ELEMENT_ID");
    			break;
    		}
        }
        if(StringUtils.isBlank(dicntCode)){
        	CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_2996);
        }
        
        //核对用户是不是已经有了此套餐
        UcaData ud = UcaDataFactory.getNormalUca(param.getString("SERIAL_NUMBER"));
        String userId=ud.getUserId();
        IDataset userDiscnts=UserDiscntInfoQry.getAllDiscntByUser(userId, dicntCode);
        if(IDataUtil.isNotEmpty(userDiscnts)){
        	CSAppException.apperr(InterRoamDayException.CRM_INTERROAMDAY_2997);
        }
        
        
        param.put("DISCNT_CODE", dicntCode);         
        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());     
        param.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 订购
        param.put("ELEMENT_TYPE_CODE", "D");
        
        return CSAppCall.call("SS.InterRoamDayRegSVC.tradeReg", param);
        
    }
    
    
    private void AsyncSendSms(String serialNumber, String noticeContent,String userId, String StrRemark)throws Exception
    {
    	 Connection conn = null;
    	 Statement st =null;
         try
         {
             conn = ConnectionManagerFactory.getConnectionManager().getConnection("crm1");//SessionManager.getInstance().getAsyncConnection("crm1");
             IData smsData = new DataMap();
             smsData.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
             smsData.put("RECV_OBJECT", serialNumber);
             smsData.put("RECV_ID", userId);
             smsData.put("NOTICE_CONTENT", noticeContent);
             smsData.put("REMARK", StrRemark);
             IData SmsData = SmsSend.prepareSmsData(smsData);
             String InSql = " INSERT INTO TI_O_SMS(SMS_NOTICE_ID,PARTITION_ID, " +
			          " EPARCHY_CODE,BRAND_CODE,IN_MODE_CODE,SMS_NET_TAG, " +
			          " CHAN_ID,SEND_OBJECT_CODE,SEND_TIME_CODE,SEND_COUNT_CODE, " +
			          " RECV_OBJECT_TYPE,RECV_OBJECT,RECV_ID,SMS_TYPE_CODE, " +
			          " SMS_KIND_CODE,NOTICE_CONTENT_TYPE,NOTICE_CONTENT,REFERED_COUNT, " +
			          " FORCE_REFER_COUNT,FORCE_OBJECT, " +
			          " FORCE_START_TIME,FORCE_END_TIME, " +
			          " SMS_PRIORITY, " +
			          " REFER_TIME, " +
			          " REFER_STAFF_ID,REFER_DEPART_ID," +
			          " DEAL_TIME," +
			          " DEAL_STAFFID,DEAL_DEPARTID," +
			          " DEAL_STATE, " +
			          " REMARK,REVC1,REVC2,REVC3,REVC4,MONTH,DAY) ";
             InSql += "VALUES('"  + SmsData.getString("SMS_NOTICE_ID") + "','" + SmsData.getString("PARTITION_ID");
             InSql +=   "','" + SmsData.getString("EPARCHY_CODE") + "','" + SmsData.getString("BRAND_CODE", "") + "','"  + SmsData.getString("IN_MODE_CODE") + "','" + SmsData.getString("SMS_NET_TAG");
             InSql +=   "','" + SmsData.getString("CHAN_ID") + "','" + SmsData.getString("SEND_OBJECT_CODE") + "','"  + SmsData.getString("SEND_TIME_CODE") + "','" + SmsData.getString("SEND_COUNT_CODE");
             InSql +=   "','" + SmsData.getString("RECV_OBJECT_TYPE") + "','" + SmsData.getString("RECV_OBJECT") + "','"  + SmsData.getString("RECV_ID") + "','" + SmsData.getString("SMS_TYPE_CODE");
             InSql +=   "','" + SmsData.getString("SMS_KIND_CODE") + "','" + SmsData.getString("NOTICE_CONTENT_TYPE") + "','"  + SmsData.getString("NOTICE_CONTENT") + "','" + SmsData.getString("REFERED_COUNT");
             InSql +=   "','" + SmsData.getString("FORCE_REFER_COUNT") + "','" + SmsData.getString("FORCE_OBJECT") + "','"  + SmsData.getString("FORCE_START_TIME", "") + "','" + SmsData.getString("FORCE_END_TIME", "");
             InSql +=   "','" + SmsData.getString("SMS_PRIORITY") + "'," + "to_date('" + SmsData.getString("REFER_TIME") + "', 'yyyy-mm-dd hh24:mi:ss')" + ",'"  + SmsData.getString("REFER_STAFF_ID") + "','" + SmsData.getString("REFER_DEPART_ID");
             InSql +=   "'," + "to_date('"+ SmsData.getString("DEAL_TIME") + "', 'yyyy-mm-dd hh24:mi:ss')" + ",'" + SmsData.getString("DEAL_STAFFID", "") + "','"  + SmsData.getString("DEAL_DEPARTID", "") + "','" + SmsData.getString("DEAL_STATE");
             InSql +=   "','" + SmsData.getString("REMARK", "") + "','" + SmsData.getString("REVC1", "") + "','"  + SmsData.getString("REVC2", "") + "','" + SmsData.getString("REVC3", "");
             InSql +=   "','" + SmsData.getString("REVC4", "") + "','" + SmsData.getString("MONTH") + "','"  + SmsData.getString("DAY") + "')" ;
             st = conn.createStatement();
             st.execute(InSql);
             conn.commit();
             st.close();
         }
         catch (Exception ex)
         {
             try
             {
                 if(conn!=null){
                     conn.rollback();
                 }
             }
             catch (Exception ex1)
             {
            	 
            	 if (log.isDebugEnabled())
     	        {
            		 //log.info("(ex1);
     	        }    
                 throw ex1;
             }
                          
             if (log.isDebugEnabled())
  	        {
            	 //log.info("(ex);
  	        }  
             throw ex;
         }
         finally
         {
        	 try
             {
                 if(st!=null&&!st.isClosed()){
                	 st.close();
                 }
             }
             catch (Exception ex)
             {
            	
            	  if (log.isDebugEnabled())
        	        {
            		  //log.info("(ex);
        	        }  
            	 //throw ex;
             }
        	 
             try
             {
                 if(conn!=null){
                     conn.close();
                 }
             }
             catch (Exception ex)
             {
            	
            	  if (log.isDebugEnabled())
      	        {
          		  //log.info("(ex);
      	        }  
            	 //throw ex;
             }
         }
    }
    
    /**
     * @Description: 一级BOSS移动商城接口1.8-流量直充接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
	public IData flowPayment(IData input) throws Exception{
		IDataUtil.chkParam(input, "IDVALUE");         // 手机号
		IDataUtil.chkParam(input, "PAYED");           // 充值流量，流量单位：M
		IDataUtil.chkParam(input, "PRODUCT_NO");      // 流量直充产品编码
		IDataUtil.chkParam(input, "ORDER_CNT");       // 用户购买的产品数量
		IDataUtil.chkParam(input, "TRANS_ID");        // 操作流水号
		IDataUtil.chkParam(input, "UIPBUSIID");       // 业务流水号
		IDataUtil.chkParam(input, "PAYMENT");         // 订单总金额
		
		IData result = new DataMap();  
		IData paramer = new DataMap();  
		int counts = Integer.parseInt(input.getString("ORDER_CNT"));
		String discntId = input.getString("PRODUCT_NO");
		String elementTypeCode = "D";
		String modifyTag = "0";
		String serialNumber = input.getString("IDVALUE");
		
		ChangeProductBean bean =  new ChangeProductBean();
		IDataset queryResult = bean.queryFlowPayment(input);
		if(IDataUtil.isNotEmpty(queryResult)){
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("X_RSPDESC", "重复交易，该交易已处理成功");
			
			result.put("X_RESULTINFO", "重复交易，该交易已处理成功");
	      	result.put("X_RESULTCODE", "3A34");
	      	result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
	      	//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
	      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
	      	paramer.put("CRM_RESULT","2998");
			bean.updateIbossCrmResult(paramer);
			return result;
		}
		
		//订购数量限制,各档位流量包每月累计限购/叠加/兑换次数为10次
		IDataset dataset = CommparaInfoQry.getCommparaInfos("CSM","2788",discntId);
		String flowType =null;
		if(IDataUtil.isNotEmpty(dataset)){
			flowType = dataset.getData(0).getString("PARA_CODE4"); 
		}
		if(StringUtils.isNotEmpty(flowType) && "PAY_GIFT_SCORE".contains(flowType)){
			int discntCounts = 0;
			UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
			List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
			IDataset commparaResults = CommparaInfoQry.getCommparaInfoByParacode4AndAttr("CSM","2788", flowType, null);
			for (DiscntTradeData userDiscnt : userDiscnts ){
	            String discntCode  = userDiscnt.getElementId();
	            for(int i = 0,j = commparaResults.size(); i < j; i++){
	            	if(discntCode.equals(discntId)&&discntCode.equals(commparaResults.getData(i).getString("PARA_CODE1"))){
	            		discntCounts++;
	            		break;
	            	}
	            }	            
			}
			if(discntCounts>=10){
				CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户本月拥有的相同档次流量包产品已达到上限10次！");
			}
		}
		
		if(counts > 1){
			//订购数量大于1，则进行参数转换
			discntId = paramConvert(discntId,counts);
			elementTypeCode = paramConvert(elementTypeCode,counts);
			modifyTag = paramConvert(modifyTag,counts);
		}
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		param.put("ELEMENT_ID", discntId);
		param.put("ELEMENT_TYPE_CODE", elementTypeCode);
		param.put("MODIFY_TAG", modifyTag);
		param.put("FLOW_PAYMENT_ID", input.getString("TRANS_ID"));
		param.put("NUM", input.getString("ORDER_CNT"));
        param.put("UNI_CHANNEL", input.getString("RESERVE2",""));//全网渠道19位编码
        param.put("NET_EXPENSES_CODE", input.getString("RESERVE3",""));//全网资费编码
        param.put("OPRNUMB", input.getString("TRANS_ID"));//流水
        
        
		if("PAY".equals(flowType)){//流量直充才有发票,赠送和积分兑换没有发票
			int payMent = input.getInt("PAYMENT");        //实付金额
			int discnt = input.getInt("PROD_DISCOUNT",0); //产品折减金额
			int relPayMent = payMent+discnt;              //应付金额
			param.put("PRINT_TICKET", "1");
			param.put("PAYMENT", payMent);
			param.put("REL_PAYMENT", relPayMent);
		}
		
		try{                          
			IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param);
			result = results.getData(0);
		} catch (Exception e){
			  if (log.isDebugEnabled())
  	        {
      		  //log.info("(e);
  	        }  
			//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
			
			result.put("X_RSPTYPE", "2");
			result.put("X_RSPCODE", "2998");
			result.put("X_RSPDESC", e.getMessage());
			
	      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
	      	paramer.put("CRM_RESULT","2998");
			bean.updateIbossCrmResult(paramer);
	      	if(null != e.getMessage()){
				if(e.getMessage().contains("用户已订购该优惠")){
					result.put("X_RESULTCODE", "3A34");
			      	result.put("X_RESULTINFO",  "用户已订购该优惠");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("用户有未完工的订单")){
					result.put("X_RESULTCODE", "998");
			      	result.put("X_RESULTINFO",  "用户有未完工的订单");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("用户不存在")){
					result.put("X_RESULTCODE", "810");
			      	result.put("X_RESULTINFO",  "该用户不存在或已经销号");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else if(e.getMessage().contains("停机")){
					result.put("X_RESULTCODE", "2005");
			      	result.put("X_RESULTINFO",  "用户已停机");
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}else{
					result.put("X_RESULTCODE", "2997");
			      	result.put("X_RESULTINFO",  e.getMessage());
					result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
					return result;
				}
	      	}else{
	      		result.put("X_RESULTCODE", "4024");
		      	result.put("X_RESULTINFO",  "产品编码非法");
				result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
				return result;
	      	}
		}
		//将CRM处理结果回写给一级BOSS对账用UOP_CEN1.TO_O_FLOWDIRECT_MOBILEMALL
      	paramer.put("BUSYID",input.getString("UIPBUSIID"));
      	paramer.put("CRM_RESULT","0000");
		bean.updateIbossCrmResult(paramer);
		
		result.put("X_RESULTCODE", "0");
      	result.put("X_RESULTINFO", "OK");
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
		return result;
	}
	
	/**
     * @Description: 一级BOSS移动商城接口1.8-流量直充结果查询接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160523
     */
	public IData queryFlowPaymentResult(IData input) throws Exception{
		IDataUtil.chkParam(input, "IDVALUE");              // 手机号
		IDataUtil.chkParam(input, "ORI_TRANSACTION_ID");   // 原交易发起方操作流水号
		IDataUtil.chkParam(input, "ORI_ACTION_DATE");      // 原交易发起方操作请求日期 YYYYMMDD
		
		ChangeProductBean bean =  new ChangeProductBean();
		String statusCode = bean.queryFlowPaymentResult(input);
		
		IData result = new DataMap();
		
		if(StringUtils.isEmpty(statusCode)){			
	      	result.put("X_RESULTINFO", "该交易不存在");
	      	result.put("X_RESULTCODE", "4A05");
		}else{
			if("OK".equals(statusCode)){
				result.put("X_RESULTINFO", "流量充值交易已完成或正在处理中");
				result.put("X_RESULTCODE", "0");
			}else{
				result.put("X_RESULTINFO", "流量充值交易失败,订单状态为："+statusCode);
				result.put("X_RESULTCODE", "2998");
			}
		}
		
		result.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddHHmmss")); 
		return result;
	}
	
	public String paramConvert(String param,int counts){
		StringBuilder sb=new StringBuilder();
		for(int i=0;i<counts;i++){
			if(i == counts-1){
				sb.append(param);
			}else{
				sb.append(param).append(",");
			}			
		}		
		return sb.toString();
	}
	/**
     * @Description: 一级BOSS移动商城接口1.8-流量直充冲正接口
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160808
     */
	public IData reverseFlowPayment(IData input) throws Exception{
		IDataUtil.chkParam(input, "IDVALUE");         // 手机号
		IDataUtil.chkParam(input, "TRANS_ID");        // 原交易发起方操作流水号
		IDataUtil.chkParam(input, "PRODUCT_NO");      // 流量直充产品编码
		
		IData result = new DataMap();
		String discntId = input.getString("PRODUCT_NO");
		
		ChangeProductBean bean = (ChangeProductBean) BeanManager.createBean(ChangeProductBean.class);
		IDataset queryResult = bean.queryFlowPayment(input);
		if(IDataUtil.isEmpty(queryResult)){
			result.put("X_RESULTINFO", "没有找到对应的流量直充记录");
	      	result.put("X_RESULTCODE", "2998");
			return result;
		}
		
		//获取此次充值的INST_ID，以便区分多个相同资费
		String tradeId = queryResult.getData(0).getString("TRADE_ID");
		String instId = TradeDiscntInfoQry.getTradeDiscntByTradeId(tradeId).getData(0).getString("INST_ID");
		
		IData param = new DataMap();
		param.put("SERIAL_NUMBER", input.getString("IDVALUE"));
		param.put("ELEMENT_ID", discntId);
		param.put("ELEMENT_TYPE_CODE", "D");
		param.put("MODIFY_TAG", "1");
		param.put("INST_ID", instId);
		
	    result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", param).getData(0);
		result.put("X_RESULTCODE", "0");
      	result.put("X_RESULTINFO", "OK");
		return result;
	}

	/**
     * @Description: VOLTE网络侧自动开通
     * @version: v1.0.0
     * @throws Exception
     * @author lihb3 20160612
     */
	public IData autoOpenVoLTE(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "SERIAL_NUMBER"); // 手机号
        //IDataUtil.chkParam(input, "ELEMENT_ID"); // 传固定值 190
        //IDataUtil.chkParam(input, "ELEMENT_TYPE_CODE");// 传固定值 S
        //IDataUtil.chkParam(input, "MODIFY_TAG");// 传固定值 0
        
		//String strSerialNumber = input.getString("SERIAL_NUMBER");
		
        IData result = new DataMap();
        
        //检查在VOLTE自动开通特殊用户表中是否有有效值，有则不做自动开通
        ChangeProductBean bean = (ChangeProductBean) BeanManager.createBean(ChangeProductBean.class);
        String serialNumber = input.getString("SERIAL_NUMBER");
        IData isLimit = bean.checkVoLTELimitA(serialNumber);
        String strResultCode = isLimit.getString("X_RESULTCODE", "2998");
        String strResultInfo = isLimit.getString("X_RESULTINFO", "4G特殊用户过滤");
        if(!"0".equals(strResultCode))
        {
        	result.put("X_RSPCODE", strResultCode);
        	result.put("X_RSPDESC", strResultInfo);
        	result.put("X_RESULTCODE", strResultCode);
        	result.put("X_RESULTINFO", strResultInfo);     	
        }
        else
        {
        	try 
        	{
        		IData idParam = new DataMap();
        		//idParam.put("TRADE_TYPE_CODE", "110");
            	//idParam.put("ORDER_TYPE_CODE", "110");
            	idParam.put("SERIAL_NUMBER", serialNumber);
            	idParam.put("ELEMENT_ID", "190");
            	idParam.put("ELEMENT_TYPE_CODE", "S");
            	idParam.put("MODIFY_TAG", "0");
            	idParam.put("BOOKING_TAG", "0");
            	idParam.put("IS_NEED_SMS", "false");
            	idParam.put("REMARK", "VoLTE业务自动开通");
                //调用产品变更接口开通VOLTE服务
            	IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", idParam);
            	result = results.first();
            	String strOrderId = result.getString("ORDER_ID");
            	String strTradeid = result.getString("TRADE_ID");
            	result.put("TRADE_ID", strTradeid);
            	result.put("ORDER_ID", strOrderId);
        		result.put("X_RESULTCODE", "0");
              	result.put("X_RESULTINFO", "OK");
              	result.put("X_RSPCODE", "0000");
            	result.put("X_RSPDESC", "ok");
			}
        	catch (Exception e) 
			{
        		String error =  Utility.parseExceptionMessage(e); 
				result.put("X_RESULTCODE", "2998");
              	result.put("X_RESULTINFO", error);
              	result.put("X_RSPCODE", "2998");
            	result.put("X_RSPDESC", error);
			}
        }  
    	return result;
	}	
	/*******************************************************************************************
	 * 将日期date从格式srcPattern转换为格式desPattern <BR/>
	 * 
	 * @param dateStr
	 *            源日期
	 * @param srcPattern
	 *            源日期格式
	 * @param desPattern
	 *            转换后的日期格式
	 * @return
	 * @throws Exception
	 */
	private static String transDate(String dateStr, String srcPattern, String desPattern) throws Exception
	{
		// 日期格式化模板
		Date date = DateUtils.parseDate(dateStr, new String[]
		{ srcPattern });

		FastDateFormat format = FastDateFormat.getInstance(desPattern);
		return format.format(date);
	}
	
	/**
     * @Description: 用户通过5G消息终端向5G消息中心发送5G消息开通申请
     * @version: v1.0.0
     * @throws Exception
     * @author wangsc10 20200422
     */
	public IData NativeOpenorStop5G(IData input) throws Exception
	{
		IDataUtil.chkParam(input, "IDVALUE"); // 手机号
        IDataUtil.chkParam(input, "OPRCODE"); // 01业务开通
        
        IData result = new DataMap();
        
        String serialNumber = input.getString("IDVALUE");
        String oprCode = input.getString("OPRCODE");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该服务号码[%s]用户信息不存在！", serialNumber);
    		result.put("X_RESULTCODE", "2998");
    		result.put("X_RESULTINFO", strError);
    		result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", "该服务号码["+serialNumber+"]用户信息不存在！");
        	result.put("RESERVE", "该服务号码["+serialNumber+"]用户信息不存在！");
        	result.put("X_RSPTYPE", "2");
        	return result;
    	}
    	
    	String strUserID = userInfo.getString("USER_ID");
    	
    	//判断是否办理了22上网服务
        IDataset svcDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(strUserID, "22");
        if(IDataUtil.isEmpty(svcDataset))
        {
        	result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "手机上网服务业务未开通。");
        	result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", "手机上网服务业务未开通。");
        	result.put("RESERVE", "手机上网服务业务未开通。");
        	result.put("X_RSPTYPE", "2");
            return result;
        }
        
        boolean is4G = false;
        IDataset userInfoQry = UserResInfoQry.getUserResInfoByUserId(strUserID);
        if (userInfoQry != null && userInfoQry.size() > 0) {
        	for (int i = 0; i < userInfoQry.size(); i++) {
        		String simCardNo = userInfoQry.getData(i).getString("RES_CODE");
        		String simCardType = userInfoQry.getData(i).getString("RES_TYPE_CODE");
        		if (null != simCardType && !"".equals(simCardType))
                {
        			if("1".equals(simCardType)){
        				if (null != simCardNo && !"".equals(simCardNo))
                        {
                			//是否4G卡用户
            				is4G = is4GUser(simCardNo);
                        }
        			}
                }
			}
        }
    	if (!is4G)
        {
    		result.put("X_RESULTCODE", "3065");
        	result.put("X_RESULTINFO", "非4/5G用户，不能办理5G消息服务!");
        	result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", "非4/5G用户，不能办理5G消息服务!");
        	result.put("RESERVE", "非4/5G用户，不能办理5G消息服务!");
        	result.put("X_RSPTYPE", "2");
            return result;
        }
    	
        //判断用户是否开通5G消息业务
        IDataset svc5GDataset = UserSvcInfoQry.getUserSvcByUserIdAndSvcId(strUserID, "84076654");
        if(IDataUtil.isNotEmpty(svc5GDataset) && oprCode.equals("01"))
        {
        	result.put("X_RESULTCODE", "2000");
        	result.put("RESERVE", "用户5G消息业务已开通。");
        	result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", "用户5G消息业务已开通。");
        	result.put("RESERVE", "用户5G消息业务已开通。");
        	result.put("X_RSPTYPE", "2");
            return result;
        }
        if(IDataUtil.isEmpty(svc5GDataset) && oprCode.equals("02"))
        {
        	result.put("X_RESULTCODE", "2998");
        	result.put("X_RESULTINFO", "用户5G消息业务未开通。");
        	result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", "用户5G消息业务未开通。");
        	result.put("RESERVE", "用户5G消息业务未开通。");
        	result.put("X_RSPTYPE", "2");
            return result;
        }
        
    	try 
    	{
    		IData idParam = new DataMap();
        	idParam.put("SERIAL_NUMBER", serialNumber);
        	idParam.put("ELEMENT_ID", "84076654");
        	idParam.put("ELEMENT_TYPE_CODE", "S");
        	if (oprCode.equals("01"))
            {
        		idParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_ADD);// 订购
        		idParam.put("REMARK", "5G消息业务自动开通");
            }
            else
            {
            	idParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);// 退订
            	idParam.put("REMARK", "5G消息业务自动关闭");
            }
        	idParam.put("NO_TRADE_LIMIT", "TRUE");
        	idParam.put("SKIP_RULE", "TRUE");
        	idParam.put("BOOKING_TAG", "0");
        	idParam.put("IS_NEED_SMS", "false");//不发送短信
            //调用产品变更接口开通5G消息服务
        	IDataset results = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", idParam);
        	result = results.first();
        	String strOrderId = result.getString("ORDER_ID");
        	String strTradeid = result.getString("TRADE_ID");
        	result.put("TRADE_ID", strTradeid);
        	result.put("ORDER_ID", strOrderId);
        	result.put("IDTYPE", "01");
        	result.put("IDVALUE", serialNumber);
        	IDataset output = UserResInfoQry.getUserResBySelbySerialnremove(serialNumber, "1");
        	result.put("IMSI", output.getData(0).getString("IMSI"));
    		result.put("X_RESULTCODE", "0000");
          	result.put("X_RESULTINFO", "OK");
          	result.put("X_RSPCODE", "0000");
        	result.put("X_RSPDESC", "ok");
        	result.put("RESERVE", "ok");
		}
    	catch (Exception e) 
		{
    		String error =  Utility.parseExceptionMessage(e); 
			result.put("X_RESULTCODE", "2998");
          	result.put("X_RESULTINFO", error);
          	result.put("X_RSPCODE", "2998");
        	result.put("X_RSPDESC", error);
        	result.put("RESERVE", error);
        	result.put("X_RSPTYPE", "2");
		}
    	return result;
	}
	
	/**
     * 5G消息中心查询某个手机号对应的IMSI或者某个IMSI对应的手机号信息
     * 
     * @data 2020-04-20
     * @param input
     * @return
     * @throws Exception
     */
    public IData getSerialNumberIMSI(IData input) throws Exception
    {
        IData result = new DataMap();
        
        String serialNumber = input.getString("IDVALUE");
        String IMSI = input.getString("IMSI");
        
        if(StringUtils.isNotBlank(serialNumber)){
        	IDataset output = UserResInfoQry.getUserResBySelbySerialnremove(serialNumber, "1");
        	if(IDataUtil.isNotEmpty(output)){
        		result.put("IDVALUE", serialNumber);
            	result.put("IMSI", output.getData(0).getString("IMSI"));
            	result.put("X_RESULTCODE", "0000");
              	result.put("X_RESULTINFO", "OK");
              	result.put("X_RSPCODE", "0000");
            	result.put("X_RSPDESC", "ok");
            	result.put("RESERVE", "ok");
                return result;
        	}
        }else if(StringUtils.isNotBlank(IMSI)){
        	IDataset resinfos = UserResInfoQry.queryUserIMEI(IMSI);
        	if(IDataUtil.isNotEmpty(resinfos)){
                IDataset output = UserInfoQry.getUserInfoByUserIdTag(resinfos.getData(0).getString("USER_ID"), "0");
                if(IDataUtil.isNotEmpty(output)){
	                result.put("IDVALUE", output.getData(0).getString("SERIAL_NUMBER"));
	            	result.put("IMSI", IMSI);
	                result.put("X_RESULTCODE", "0000");
	              	result.put("X_RESULTINFO", "OK");
	              	result.put("X_RSPCODE", "0000");
	            	result.put("X_RSPDESC", "ok");
	            	result.put("RESERVE", "ok");
	            	return result;
                }
			}
        }
        result.put("X_RESULTCODE", "2998");
      	result.put("X_RESULTINFO", "查询不到用户信息。");
      	result.put("X_RSPCODE", "2998");
    	result.put("X_RSPDESC", "查询不到用户信息。");
    	result.put("RESERVE", "查询不到用户信息。");
    	result.put("X_RSPTYPE", "2");
		return result;
    }
    
    /**
     * @Description: 是否4G卡用户
     * @param simCardNo
     * @return
     * @throws Exception
     * @author: zhangxing3
     */
    public boolean is4GUser(String simCardNo) throws Exception
    {


        // 调用资源接口
        IDataset simCardDatas = ResCall.getSimCardInfo("0", simCardNo, "", "");

        if (IDataUtil.isNotEmpty(simCardDatas))
        {
            IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simCardDatas.getData(0).getString("RES_KIND_ID"));
            if (IDataUtil.isNotEmpty(reSet))
            {
            	String netTypeCode = reSet.getData(0).getString("NET_TYPE_CODE", "");
    			if ("01".equals(netTypeCode))
    			{
    				return true;// 4G卡
    			}
            }
        }
        else
        {
            // CSAppException.apperr(ResException.CRM_RES_86, simCardNo);
            return false;// 因测试资料不全 暂时返回false
        }
       
        return false;
    }
	
}
