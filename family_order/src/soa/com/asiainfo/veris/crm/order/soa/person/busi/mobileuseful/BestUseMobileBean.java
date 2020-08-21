package com.asiainfo.veris.crm.order.soa.person.busi.mobileuseful;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.ailk.common.util.Utility;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.ailk.biz.message.MessageFactory;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.abilityopenplatform.AbilityPlatCheckRelativeQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.OrderPreInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ExceptionUtils;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.simcardmgr.SimCardBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;

public class BestUseMobileBean extends CSBizBean{

    protected static Logger log = Logger.getLogger(BestUseMobileBean.class);
    
    private static StringBuilder getInterFaceSQL;
    
    private static String PRETYPE = "BestUseMobile";
    
	private static final String OneNumOneDevice_ = "OneNumOneDevice_";

    private static String CHGESIM_PRETYPE = "OneCardOneDevice_CHG";

    private static final String eSIMDeviceCheck_ = "eSIMDeviceCheck_";
	
    static
    {
        getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' "); 
    }
    
     /*a.入网资格校验
      1)该用户的身份资料是否真实准确；
      2)该客户同一个证件号码下目前全国已开户的号码数量是否已达到上限；
      3)是否黑名单用户；
      4)该客户资料下面的用户是否有欠费
      5)开户结果通知接口
      b.调用资源预占接口，进行资源预占*/
    public IData createPersonSubscriberMobile(IData input) throws Exception
    {
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULT_INFO", "ok");
        IDataset OrderPreinfos = OrderPreInfoQry.queryOrderPreInfoByPreType(PRETYPE,"0");
        if(!OrderPreinfos.isEmpty())
        {
            for (int i = 0; i < OrderPreinfos.size(); i++)
            {
                IData OrderPreinfo = OrderPreinfos.getData(i);
//                String acceptData = OrderPreinfo.getString("ACCEPT_DATA1");
                StringBuilder acceptData = new StringBuilder();
                for (int j = 1; j < 4; j++) {
                    if(StringUtils.isNotBlank(OrderPreinfo.getString("ACCEPT_DATA"+j))){
                        acceptData = acceptData.append(OrderPreinfo.getString("ACCEPT_DATA"+j));
                    }
                }
                
                String tradeStaffId = OrderPreinfo.getString("RSRV_STR1");
                String tradeDepartIda = OrderPreinfo.getString("RSRV_STR2");
                String tradeCityCode = OrderPreinfo.getString("RSRV_STR3");
                String tradeEparchyCode = OrderPreinfo.getString("RSRV_STR4");
                CSBizBean.getVisit().setInModeCode("6");
                CSBizBean.getVisit().setStaffEparchyCode(tradeEparchyCode);
                CSBizBean.getVisit().setStaffId(tradeStaffId);
                CSBizBean.getVisit().setDepartCode(tradeDepartIda);
                CSBizBean.getVisit().setDepartId(tradeDepartIda);
                CSBizBean.getVisit().setCityCode(tradeCityCode);

                IData inparam = new DataMap(acceptData.toString());
    			inparam.put("OPEN_TYPE", "BESTUSE_OPEN");
    			inparam.put("DEVELOP_STAFF_ID", tradeStaffId);
    			inparam.put("CREATE_STAFF_ID", tradeStaffId);

                result = createPersonUserTrade(inparam);
            }
        }
        return result;
    }
    
    private IData getParam(IData input ,String paramName){
    	Object o = input.get(paramName);
    	IData newParam = null; 
    	if(o instanceof Map) {
    		newParam = new DataMap((Map) o);
		}else if(o instanceof IData) {
			newParam =  (IData) o;
		}else if (o instanceof String) {
			newParam =  new DataMap(String.valueOf(o));
		}else if (o instanceof IDataset) {
            newParam =  new DataMap();
            newParam.put("SET", (IDataset)o);
		}
    	return newParam;
    }
    
    private IDataset getDatasets(IData param,String ListName) throws Exception {
        Object o = null;
        if("PRODUCT_LIST".equals(ListName))
        {
            o = param.get("PRODUCT_LIST");
        }
        
        if(o instanceof List) {
            IDataset ids = new DatasetList();
            for (int i = 0; i < ((List) o).size();i++)
            {
                IData tmpData = new DataMap();
                if(((List)o).get(i) instanceof Map) {
                    tmpData= new DataMap((Map) (((List)o).get(i)));
                }else if(((List)o).get(i) instanceof IData) {
                    tmpData=(IData) ((List)o).get(i);
                }else if (((List)o).get(i) instanceof String) {
                    tmpData= new DataMap(String.valueOf(((List)o).get(i)));
                }
                ids.add(tmpData);
            }
            return ids  ;
        }else if(o instanceof IDataset) {
            return (IDataset) o;
        }else if (o instanceof String) {
            return new DatasetList(String.valueOf(o));
        }
        throw new Exception("未识别的params参数..."+ListName+"=" + o +",type=" + o.getClass());
    }
    
    /**
     * 请求响应
     */
    public IData responseFeedBack(IData input) throws Exception {
        IDataset resultList = new DatasetList();
        IData resultData = new DataMap();
        try{
            checkParam(input);
        }
        catch(Exception e){
            resultData.put("BIZ_CODE", "2998");
            resultData.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            return resultData;
        }
        
        String serialNumber = input.getString("SERIAL_NUMBER");
        String channelid = input.getString("CHANNEL_ID");// 合作渠道编码
        String orderid = input.getString("ORDER_ID");// 合作渠道订单编码
        String cardType = input.getString("CARD_TYPE");//号卡类型
        String ospOrderId = input.getString("OSP_ORDER_ID","");//能开订单编码
		String busiType = input.getString("BUSI_TYPE","001");//业务类型 001：最易用手机 002：一号一终端
		String remark = "";
		String preOrderRemark = "";
        remark = "@"+orderid+"@"+busiType+"@"+channelid+"@"+ospOrderId+"@mobileuseful";
        if("001".equals(busiType)){
	        preOrderRemark = "最好用手机开户请求";
	    }else if("002".equals(busiType)){
	        preOrderRemark = "一号一终端开户请求";
	        IDataUtil.chkParamNoStr(input, "OSP_ORDER_ID");// 能开订单号
	        IDataUtil.chkParamNoStr(input, "BUSI_TYPE");// 业务类型 001：最易用手机  002：一号一终端

	     }
        IDataset OrderPreinfos = OrderPreInfoQry.queryPreOrderInfosBySn(orderid,"0",null);
        if(!OrderPreinfos.isEmpty())
        {
            resultData.put("BIZ_CODE", "4008");
            resultData.put("BIZ_DESC", "重复操作");
        }
        else
        {	
        	IDataset orderPreinfos = OrderPreInfoQry.queryPreOrderInfosBySn(orderid,"9",null);
        	IDataset orderPreinfos2 = OrderPreInfoQry.queryPreOrderInfosBySn(orderid,"-1",null);
            if(!orderPreinfos.isEmpty()||IDataUtil.isNotEmpty(orderPreinfos2))
            {
                resultData.put("BIZ_CODE", "4008");
                resultData.put("BIZ_DESC", "重复操作");
                return resultData;
            }else{
            	resultData.put("BIZ_CODE", "0000");
            	resultData.put("BIZ_DESC", "接收成功");
            }
            String serial_number = input.getString("SERIAL_NUMBER");
            String sim_card_no = "";
            if("001".equals(busiType)){
                sim_card_no = input.getString("ICCID");//号卡类型=1时必传
            }
            //客户资料
            IData psptinfo = getParam(input,"PSPT_INFO");
            input.put("PSPT_ID", psptinfo.getString("PSPT_ID"));
            input.put("PSPT_TYPE_CODE", encodeIdType(psptinfo.getString("PSPT_TYPE_CODE")));
            input.put("CUST_NAME", psptinfo.getString("CUST_NAME"));
            input.put("PSPT_ADDR", psptinfo.getString("PSPT_ADDR"));
            input.put("CERT_EXPDATE", psptinfo.getString("CERT_EXPDATE"));
            /*
             * 对参数做解密
             */
            log.debug(">>>>>>>>>>responseFeedBack>>>>>>>>解密前>>>input="+input);
            IData decodeResult = decodeByIboss(input);
            input.put("CUST_NAME", decodeResult.getString("CUST_NAME"));
            input.put("PSPT_ID", decodeResult.getString("PSPT_ID"));
            log.debug(">>>>>>>>>>responseFeedBack>>>>>>>>解密后>>>input="+input);
            
            String sex = psptinfo.getString("SEX");
            if("0".equals(sex)){
                input.put("SEX", "F");
            }else{
                input.put("SEX", "M"); 
            }
            input.put("FOLK_CODE", psptinfo.getString("NATION"));
            input.put("BIRTHDAY", psptinfo.getString("BIRTHDAY"));
            input.put("X_PSPT_ORGANS", psptinfo.getString("ISSUING_AUTHORITY"));
            input.put("X_PSPT_START_DATE", psptinfo.getString("CERT_VALIDDATE"));
            input.put("X_PSPT_END_DATE", psptinfo.getString("CERT_EXPDATE"));
            input.put("PSPT_ORGANS", psptinfo.getString("ISSUING_AUTHORITY"));
            input.put("PSPT_START_DATE", psptinfo.getString("CERT_VALIDDATE"));
            input.put("PSPT_END_DATE", psptinfo.getString("CERT_EXPDATE"));
            input.put("REAL_NAME", "1");


            
            input.put("SERIAL_NUMBER", serial_number);
            
            IDataset selectelements = new DatasetList();
            IDataset productlist = getDatasets(input,"PRODUCT_LIST");//商品包含的产品列表
            log.debug("tz_input============"+input.toString());
            log.debug("tz_productlist============"+productlist.toString());
            if(IDataUtil.isNotEmpty(productlist)){
                for(int i = 0;i < productlist.size();i++){
                    IData productinfo = productlist.getData(i);
                    String ctrmproductid = productinfo.getString("PRODUCT_ID");
                    IDataset products = AbilityPlatCheckRelativeQry.getCtrmProductByBossId(ctrmproductid,CSBizBean.getTradeEparchyCode());
                    if(IDataUtil.isNotEmpty(products)){
                        
                        for(int n = 0; n < products.size();n++){
                            IData product = products.getData(n);
                            if("P".equals(product.getString("ELEMENT_TYPE_CODE",""))){
                                input.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
                                input.put("NEW_PRODUCT_ID", product.getString("PRODUCT_ID"));
                                IDataset discntInfos = PkgElemInfoQry.queryDiscntOfForcePackage(product.getString("PRODUCT_ID"));
                                if (IDataUtil.isNotEmpty(discntInfos))
                                {
                                    for (int j = 0; j < discntInfos.size(); j++)
                                    {
                                        IData element = new DataMap();
                                        IData elementInfo = discntInfos.getData(j);
                                        element.put("ELEMENT_ID", elementInfo.getString("ELEMENT_ID"));
                                        element.put("ELEMENT_TYPE_CODE", elementInfo.getString("ELEMENT_TYPE_CODE"));
                                        element.put("MODIFY_TAG", "0");
                                        element.put("PRODUCT_ID", elementInfo.getString("PRODUCT_ID"));
                                        element.put("PACKAGE_ID", elementInfo.getString("PACKAGE_ID"));
                                        element.put("END_DATE", elementInfo.getString("END_DATE"));
                                        element.put("START_DATE", elementInfo.getString("START_DATE"));
                                        selectelements.add(element);
                                    }
                                }
                                IDataset svcInfos = PkgElemInfoQry.queryServiceOfForcePackage(product.getString("PRODUCT_ID"));
                                if (IDataUtil.isNotEmpty(svcInfos))
                                {
                                    for (int j = 0; j < svcInfos.size(); j++)
                                    {
                                        IData element = new DataMap();
                                        IData elementInfo = svcInfos.getData(j);
                                        element.put("ELEMENT_ID", elementInfo.getString("ELEMENT_ID"));
                                        element.put("ELEMENT_TYPE_CODE", elementInfo.getString("ELEMENT_TYPE_CODE"));
                                        element.put("MODIFY_TAG", "0");
                                        element.put("PRODUCT_ID", elementInfo.getString("PRODUCT_ID"));
                                        element.put("PACKAGE_ID", elementInfo.getString("PACKAGE_ID"));
                                        element.put("END_DATE", elementInfo.getString("END_DATE"));
                                        element.put("START_DATE", elementInfo.getString("START_DATE"));
                                        selectelements.add(element);
                                    }
                                }
                            }

                            if("D".equals(product.getString("ELEMENT_TYPE_CODE",""))){
                                IData element = new DataMap();
                                IData elementInfo = product;
                                element.put("ELEMENT_ID", elementInfo.getString("ELEMENT_ID"));
                                element.put("ELEMENT_TYPE_CODE", elementInfo.getString("ELEMENT_TYPE_CODE"));
                                element.put("MODIFY_TAG", "0");
                                element.put("PRODUCT_ID", elementInfo.getString("PRODUCT_ID"));
                                element.put("PACKAGE_ID", elementInfo.getString("PACKAGE_ID"));
                                element.put("END_DATE", elementInfo.getString("END_DATE"));
                                element.put("START_DATE", elementInfo.getString("START_DATE"));
                                log.error("Delement!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+element);
                                selectelements.add(element);
                            }

                            if("S".equals(product.getString("ELEMENT_TYPE_CODE",""))){
                                IData element = new DataMap();
                                IData elementInfo = product;
                                element.put("ELEMENT_ID", elementInfo.getString("ELEMENT_ID"));
                                element.put("ELEMENT_TYPE_CODE", elementInfo.getString("ELEMENT_TYPE_CODE"));
                                element.put("MODIFY_TAG", "0");
                                element.put("PRODUCT_ID", elementInfo.getString("PRODUCT_ID"));
                                element.put("PACKAGE_ID", elementInfo.getString("PACKAGE_ID"));
                                element.put("END_DATE", elementInfo.getString("END_DATE"));
                                element.put("START_DATE", elementInfo.getString("START_DATE"));
                                log.error("Selement!!!!!!!!!!!!!!!!!!!!!!!!!!!!"+element);
                                selectelements.add(element);
                            }
                        }
                    }
                }
            }
 			 log.debug("selectelements============"+selectelements);            if(null!=selectelements&&selectelements.size()!=0){
            	String productId = selectelements.first().getString("PRODUCT_ID");
            	input.put("PRODUCT_ID", productId);
            }
            String brandCode = "";
            if(!StringUtils.isBlank(input.getString("PRODUCT_ID"))){
                brandCode = UProductInfoQry.getBrandCodeByProductId(input.getString("PRODUCT_ID"));
            }
            input.put("BRAND_CODE", brandCode);
            input.put("SELECTED_ELEMENTS", selectelements);
            input.put("USER_TYPE_CODE", "0");
            input.put("PSPT_ADDE", "海南");
            input.put("POST_CODE", "570000");
            input.put("TRADE_TYPE_CODE", "10");

            //账户信息
            input.put("PAY_NAME", psptinfo.getString("CUST_NAME"));
            input.put("PAY_MODE_CODE", "0");
            input.put("PAY_MONEY_CODE", "0");
            input.put("FOREGIFT", "0");
            input.put("BASIC_CREDIT_VALUE", "0");
            input.put("CREDIT_VALUE", "0");
            input.put("SCORE_VALUE", "0");
            input.put("USER_OTHER_NAME", psptinfo.getString("CUST_NAME"));
            input.put("ORDRENO", "1");
            input.put("OPER_TAG", "ADD");//新增
            
            IData paymentinfo = getParam(input,"PAYMENT_INFO");//支付信息
            IDataset tradeFeeSub = dealPayFee(paymentinfo);
            input.put("X_TRADE_FEESUB", tradeFeeSub);
            input.put("ADVANCE_PAY", StringUtils.isNotBlank(paymentinfo.getString("ADVANCE_PAY"))?paymentinfo.getString("ADVANCE_PAY"):"0");
            input.put("OPER_FEE", StringUtils.isNotBlank(paymentinfo.getString("OPER_FEE"))?paymentinfo.getString("OPER_FEE"):"0");
            
            input.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            input.put("AUTH_SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
            input.put("TRADE_TYPE_CODE", "10");
//            input.put("PSPT_TYPE_CODE", encodeIdType(psptinfo.getString("PSPT_TYPE_CODE")));
            input.put("REMARK", remark);
            input.put("TRADE_DEPART_PASSWD", input.getString("TRADE_DEPART_PASSWD","0"));//没有用到，但需要校验
            input.put("OSP_ORDER_ID", ospOrderId);
        	input.put("BUSI_TYPE", busiType);
        	
            IData preOrderData = new DataMap();
            String preId = SeqMgr.getOrderId();
            preOrderData.put("PRE_ID", preId);
            preOrderData.put("PRE_TYPE", PRETYPE);
            preOrderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(preId));
            preOrderData.put("ACCEPT_STATE", "0");
            preOrderData.put("REQUEST_ID", input.getString("ORDER_ID"));
            preOrderData.put("START_DATE", SysDateMgr.getSysTime());
            preOrderData.put("END_DATE", SysDateMgr.addSecond(SysDateMgr.getSysTime(),300));
            preOrderData.put("TRADE_TYPE_CODE", "10");
            preOrderData.put("SERIAL_NUMBER", serialNumber);
            preOrderData.put("REPLY_STATE", "0");//0是初始化状态
//            preOrderData.put("ACCEPT_DATA1", input.toString());
            IDataset dataset = getSpiltSet(input.toString(), 3800);
            for (int i = 0; i < dataset.size(); i++) {
                preOrderData.put("ACCEPT_DATA" + (i + 1), dataset.get(i));
            }
            preOrderData.put("REMARK", preOrderRemark);
            preOrderData.put("RSRV_STR1", input.getString("TRADE_STAFF_ID"));
            preOrderData.put("RSRV_STR2", input.getString("TRADE_DEPART_ID"));
            preOrderData.put("RSRV_STR3", input.getString("TRADE_CITY_CODE"));
            preOrderData.put("RSRV_STR4", input.getString("TRADE_EPARCHY_CODE"));
            Dao.insert("TF_B_ORDER_PRE", preOrderData, Route.getJourDb(Route.CONN_CRM_CG));
            
        }
        
        return resultData;
    }
    
    /**
     * 参数校验
     */
    protected void checkParam(IData input) throws Exception {
        IDataUtil.chkParamNoStr(input, "CHANNEL_ID");// 合作渠道编码
        IDataUtil.chkParamNoStr(input, "ORDER_ID");// 合作渠道订单编码
        IDataUtil.chkParamNoStr(input, "SERIAL_NUMBER");// 已选号码
        IDataUtil.chkParamNoStr(input, "CARD_TYPE");// 号卡类型
        IDataUtil.chkParamNoStr(input, "IMEI");// IMEI
//        IDataUtil.chkParamNoStr(input, "ICCID");// ICCID

        String cardtype = input.getString("CARD_TYPE");
        if("1".equals(cardtype)){
            IDataUtil.chkParamNoStr(input, "EID");// 号卡类型=1时必传
        }else if("2".equals(cardtype)){
            IDataUtil.chkParamNoStr(input, "SIM_CARD_NO");// 号卡类型=2时必传
        }
        String busiType = input.getString("BUSI_TYPE","");
        if(StringUtils.equals(busiType,"001")||StringUtils.equals(busiType,"")){
            IDataUtil.chkParamNoStr(input, "ICCID");
        }

        IDataset productlist = input.getDataset("PRODUCT_LIST");//商品包含的产品列表
        if(IDataUtil.isNotEmpty(productlist)){
            for(int i = 0;i < productlist.size();i++){
                IData productinfo = productlist.getData(i);
                IDataUtil.chkParamNoStr(productinfo, "PRODUCT_ID");// 产品编码
                IDataUtil.chkParamNoStr(productinfo, "PRODUCT_TYPE");// 产品类型
            }
        }
              
        IData paymentinfo = getParam(input,"PAYMENT_INFO");//支付信息
        if(IDataUtil.isNotEmpty(paymentinfo)){
//            IDataUtil.chkParamNoStr(paymentinfo, "RES_FEE");// 号码金额
//            IDataUtil.chkParamNoStr(paymentinfo, "OPER_FEE");// SIM卡金额
//            IDataUtil.chkParamNoStr(paymentinfo, "ADVANCE_PAY");// 预存金额
//            IDataUtil.chkParamNoStr(paymentinfo, "OTHER_FEE");// 其他金额
            IDataUtil.chkParamNoStr(paymentinfo, "TOTAL_FEE");// 订单金额
            IDataUtil.chkParamNoStr(paymentinfo, "X_FPAY_FEE");// 实际支付金额
            IDataUtil.chkParamNoStr(paymentinfo, "PAYMENT_ORDER_ID");// 支付单编码
        }else{
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[PAYMENT_INFO]不能为空！");
        }
        
        IData psptinfo = getParam(input,"PSPT_INFO");//证件信息
        if(IDataUtil.isNotEmpty(psptinfo)){
            IDataUtil.chkParamNoStr(psptinfo, "PSPT_TYPE_CODE");// 证件类型
            IDataUtil.chkParamNoStr(psptinfo, "PSPT_ID");// 证件号码
            IDataUtil.chkParamNoStr(psptinfo, "SEX");// 性别
           // IDataUtil.chkParamNoStr(psptinfo, "NATION");// 民族
            IDataUtil.chkParamNoStr(psptinfo, "BIRTHDAY");// 生日
            IDataUtil.chkParamNoStr(psptinfo, "ISSUING_AUTHORITY");// 签证机关
            IDataUtil.chkParamNoStr(psptinfo, "CERT_VALIDDATE");// 证件有效期开始日期
            IDataUtil.chkParamNoStr(psptinfo, "CERT_EXPDATE");// 证件有效期截至日期
            IDataUtil.chkParamNoStr(psptinfo, "PSPT_ADDR");// 证件地址
            IDataUtil.chkParamNoStr(psptinfo, "IDENTITY_SN");// 实名认证流水
        }else{
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[PSPT_INFO]不能为空！");
        }
    }

    /**
     * 查询欠费信息
     *
     * @return
     * @throws Exception
     */

    public IData getOweFeeUserById(IDataset custList) throws Exception {
        IData oweFeeData = new DataMap();
        IData custData = null;
        IData userData = null;
        IDataset userList = null;
        IData owefeeData = null;
        double dFee = 0;// 往月欠费
        int iOnlineNum = 0;// 当前证件下在网用户数
        boolean isExistsOweFeeFlag = false;// 存在欠费用户标记
        String oweFeeSerialNumber = "";// 欠费号码
        for (int i = 0; i < custList.size(); i++) {
            custData = custList.getData(i);
            String cust_id = custData.getString("CUST_ID");
            IData oweCustData = new DataMap();
            oweCustData.put("CUST_ID", cust_id);
            userList = com.asiainfo.veris.crm.order.soa.person.common.query.queryInfo.QueryListInfo.getPhoneUserInfoByCustId(cust_id);
            if (userList != null && userList.size() > 0) {
                iOnlineNum += userList.size();// 统计在网用户数
                // 未找到欠费用户时，才查欠费信息，找到一条则不查询，提示第一条欠费信息即可
                if (!isExistsOweFeeFlag) {
                    // 根据用户标识查询欠费信息
                    for (int j = 0; j < userList.size(); j++) {
                        userData = userList.getData(j);
                        String userId = userData.getString("USER_ID");

                        IData iparam = new DataMap();

                        iparam.put("EPARCHY_CODE", userData.getString(
                                "EPARCHY_CODE", ""));
                        iparam.put("USER_ID", userId);
                        iparam.put("ID", userId);
                        iparam.put("ID_TYPE", "1");
                        iparam.put(Route.ROUTE_EPARCHY_CODE, userData.getString(
                                Route.ROUTE_EPARCHY_CODE, ""));

                        // 调用账户流程查询欠费信息
                        // IData doweFee = UserInfoQry.getOweFeeCTT(iparam);
                        log.debug("调用账务查询费用："+userId);
                        IData doweFee = com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall.getOweFeeByUserId(userId);
                        log.debug("调用账务查询费用结果："+doweFee.toString());
                        dFee = Double.parseDouble(doweFee
                                .getString("LAST_OWE_FEE"));
                        if (dFee > 0) {// 找到有往月欠费用户则退出循环，提示欠费信息
                            isExistsOweFeeFlag = true;
                            oweFeeSerialNumber = userData
                                    .getString("SERIAL_NUMBER");
                            break;
                        }
                    }
                }
            }
        }
        // 存在欠费用户时，返回欠费号码，欠费金额，在网用户数
        if (isExistsOweFeeFlag) {
            String strFee = String.valueOf(((float) dFee) / 100);
            oweFeeData.put("OWE_FEE_SERIAL_NUMBER", oweFeeSerialNumber);
            oweFeeData.put("OWE_FEE", strFee);
            oweFeeData.put("ONLINE_NUM", iOnlineNum);
            oweFeeData.put("IS_EXISTS_OWE_FEE_FLAG", true);
        }
        return oweFeeData;

    }
    
    public IData createPersonUserTrade(IData input) throws Exception {
        IData resultData = new DataMap();
        String sim_card_no = "";
        if("001".equals(input.getString("BUSI_TYPE"))){
            sim_card_no = input.getString("ICCID");//号卡类型=1时必传
        }
        String orderid = input.getString("ORDER_ID");
        String channelid = input.getString("CHANNEL_ID");
        String serial_number = input.getString("SERIAL_NUMBER");
        String marketingAction = input.getString("BUSI_TYPE","");
		String ospOrderId = input.getString("OSP_ORDER_ID");
		String remark = input.getString("REMARK");
		String psptId = input.getString("PSPT_ID");
		String iccid = input.getString("SIM_CARD_NO");
        IDataset onePhones = ResCall.selOnePhoneNum("1", serial_number, serial_number);
        if((null==onePhones||onePhones.size()==0) &&"002".equals(marketingAction)){
            IData output = new DataMap();
            IData callData = new DataMap();
            output.put("MARKETING_ACTION", marketingAction);
            output.put("OSP_ORDER_ID", ospOrderId);
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "4005");
            output.put("RESULT_DESC", "号码非法（不存在）");
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            //调用结果反馈接口
            callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
            resultData.put("BIZ_CODE", "4005");
            resultData.put("BIZ_DESC","号码非法（不存在）");
            return resultData;
        }


        Boolean flag = IdcardUtils.validateCard(psptId);
		log.debug("tz_flag============"+flag);
		if(!flag &&"002".equals(marketingAction)){
			IData output = new DataMap();
            IData callData = new DataMap();
        	output.put("MARKETING_ACTION", marketingAction);
			output.put("OSP_ORDER_ID", ospOrderId);
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "2043");
            output.put("RESULT_DESC", "用户身份信息错误");
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			//调用结果反馈接口
			callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");

			resultData.put("BIZ_CODE", "2043");
			resultData.put("BIZ_DESC","用户身份信息错误");
			
			return resultData;
		}
		
		IDataset orderPreinfos = OrderPreInfoQry.queryPreOrderInfosBySn(orderid,"9",null);
        if(!orderPreinfos.isEmpty()&&"002".equals(marketingAction))
        {
        	IData output = new DataMap();
            IData callData = new DataMap();
            output.put("MARKETING_ACTION", marketingAction);
			output.put("OSP_ORDER_ID", ospOrderId);
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "4008");
            output.put("RESULT_DESC", "重复操作");
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			//调用结果反馈接口
			callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
	
			resultData.put("BIZ_CODE", "4008");
			resultData.put("BIZ_DESC","重复操作");
			
			return resultData;
        }
		if("002".equals(marketingAction) && IDataUtil.isEmpty(ResCall.getMphonecodeInfo(serial_number))){
			 IData output = new DataMap();
             IData callData = new DataMap();
             output.put("ORDER_ID", orderid);
             output.put("CHANNEL_ID", channelid);
             output.put("MARKETING_ACTION", marketingAction);
 			 output.put("OSP_ORDER_ID", ospOrderId);
             output.put("MSISDN", serial_number);
             output.put("ICCID", iccid);
             output.put("RESULT_CODE", "4025");
             output.put("RESULT_DESC", "号码归属省不对");
             output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
             callData = callAbilityPlatCommon(output);
             //终止TF_B_ORDER_PRE表的数据
             OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
             resultData.put("BIZ_CODE", "4025");
             resultData.put("BIZ_DESC", "号码归属省不对");
             return resultData;
		}
        boolean checkBlackCust = com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry.isBlackCust(input.getString("PSPT_TYPE_CODE"),psptId);
        if (checkBlackCust) {
            IData output = new DataMap();
            IData callData = new DataMap();
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MARKETING_ACTION", marketingAction);
            output.put("OSP_ORDER_ID", ospOrderId);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "3000");
            output.put("RESULT_DESC", "该用户有黑名单信息！");
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
            resultData.put("BIZ_CODE", "3000");
            resultData.put("BIZ_DESC", "该用户有黑名单信息！");
            return resultData;
        }
        //证件类型转换
        String custPsptTypeCode="00";
        IDataset commList= com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry.getCommNetInfo("CSM", "3838", "IDCARD");
        for(int i=0;i<commList.size();i++){
            if(input.getString("PSPT_TYPE_CODE").equals(commList.getData(i).getString("PARA_CODE3"))){
                custPsptTypeCode=commList.getData(i).getString("PARA_CODE2","0");                break;
            }
        }
        //调用全网证件号码查验接口
        com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean bean = BeanManager.createBean(com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean.class);
        //查询预占凭证
        IData param = new DataMap();
        param.put("ID_CARD_TYPE", custPsptTypeCode);
        param.put("ID_CARD_NUM", input.getString("PSPT_ID"));
        IDataset pSeqInfos = bean.selCampOn(param);
        log.debug("===========>>20111121-pSeqInfos="+pSeqInfos);
        //如果找到记录说明，已被预占
        if(IDataUtil.isNotEmpty(pSeqInfos)){
            IData output = new DataMap();
            IData callData = new DataMap();
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MARKETING_ACTION", marketingAction);
            output.put("OSP_ORDER_ID", ospOrderId);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "4040");
            output.put("RESULT_DESC", "证件号码被预占！");
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
            resultData.put("BIZ_CODE", "4040");
            resultData.put("BIZ_DESC", "证件号码被预占！");
            return resultData;
        }
        IData inParam = new DataMap();
        inParam.put("CUSTOMER_NAME", input.getString("CUST_NAME"));
        inParam.put("IDCARD_TYPE",input.getString("PSPT_TYPE_CODE"));
        inParam.put("IDCARD_NUM", input.getString("PSPT_ID"));
        IDataset callResult = bean.idCheck(inParam);
        if (IDataUtil.isNotEmpty(callResult))
        {
            int openNum = callResult.getData(0).getInt("TOTAL",0);
            int untrustresult = callResult.getData(0).getInt("UN_TRUST_RESULT", 0);
            //一证五号默认  开户限制为5
            int openLimitNum = 5;

            if (openNum > 0)
            {
                if (untrustresult > 0)
                {
                    IData output = new DataMap();
                    IData callData = new DataMap();
                    output.put("ORDER_ID", orderid);
                    output.put("CHANNEL_ID", channelid);
                    output.put("MARKETING_ACTION", marketingAction);
                    output.put("OSP_ORDER_ID", ospOrderId);
                    output.put("MSISDN", serial_number);
                    output.put("ICCID", iccid);
                    output.put("RESULT_CODE", "23043");
                    output.put("RESULT_DESC", "开户人有不良信息，不满足开户条件，禁止开户");
                    output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    callData = callAbilityPlatCommon(output);
                    //终止TF_B_ORDER_PRE表的数据
                    OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString(),"1","0");
                    resultData.put("BIZ_CODE", "2999");
                    resultData.put("BIZ_DESC", "失败");
                    return resultData;

                }


                // 根据证件类型查找全网开户限制数
                IDataset openLimitResult = com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry.getCommparaAllCol("CSM", "2552","0", "ZZZZ");

                if (IDataUtil.isNotEmpty(openLimitResult))
                {
                    openLimitNum = openLimitResult.getData(0).getInt("PARA_CODE1", 5);
                }

                if (openNum >= openLimitNum)
                {
                    IData output = new DataMap();
                    IData callData = new DataMap();
                    output.put("ORDER_ID", orderid);
                    output.put("CHANNEL_ID", channelid);
                    output.put("MARKETING_ACTION", marketingAction);
                    output.put("OSP_ORDER_ID", ospOrderId);
                    output.put("MSISDN", serial_number);
                    output.put("ICCID", iccid);
                    output.put("RESULT_CODE", "3042");
                    output.put("RESULT_DESC", "超出一证五号，不允许办理此业务");
                    output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    callData = callAbilityPlatCommon(output);
                    //终止TF_B_ORDER_PRE表的数据
                    OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
                    resultData.put("BIZ_CODE", "2999");
                    resultData.put("BIZ_DESC", "失败");
                    return resultData;

                }
            }
            else if(openNum == -1)
            {
                IData output = new DataMap();
                IData callData = new DataMap();
                output.put("ORDER_ID", orderid);
                output.put("CHANNEL_ID", channelid);
                output.put("MARKETING_ACTION", marketingAction);
                output.put("OSP_ORDER_ID", ospOrderId);
                output.put("MSISDN", serial_number);
                output.put("ICCID", iccid);
                output.put("RESULT_CODE", "3042");
                output.put("RESULT_DESC", "超出一证五号，不允许办理此业务");
                output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                callData = callAbilityPlatCommon(output);
                //终止TF_B_ORDER_PRE表的数据
                OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
                resultData.put("BIZ_CODE", "2999");
                resultData.put("BIZ_DESC", "失败");
                return resultData;
            }
        }
        String endPspt = input.getString("PSPT_END_DATE");
        if( endPspt.compareTo(SysDateMgr.getSysDateYYYYMMDD())<0){
            IData output = new DataMap();
            IData callData = new DataMap();
            output.put("ORDER_ID", orderid);
            output.put("CHANNEL_ID", channelid);
            output.put("MARKETING_ACTION", marketingAction);
            output.put("OSP_ORDER_ID", ospOrderId);
            output.put("MSISDN", serial_number);
            output.put("ICCID", iccid);
            output.put("RESULT_CODE", "3018");
            output.put("RESULT_DESC", "【用户证件失效】："+endPspt);
            output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            callData = callAbilityPlatCommon(output);
            //终止TF_B_ORDER_PRE表的数据
            OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
            resultData.put("BIZ_CODE", "2999");
            resultData.put("BIZ_DESC", "失败");
            return resultData;
        }
        //特殊时期
        IDataset config = com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry.getCommparaAllCol("CSM", "2018", "ONE_SPECIAL_TIME", "ZZZZ");


        if(IDataUtil.isNotEmpty(config)){
            String sysdate = SysDateMgr.getSysDate();//yyyy-MM-dd
            for(int i = 0 ; i<config.size();i++){
                String type = config.getData(i).getString("PARA_CODE1");//配置的特殊时期的类型，1：为单天 0为连续的
                if("1".equals(type)){
                    String specialTime =  config.getData(i).getString("PARA_CODE2").trim();
                    if(StringUtils.equals(sysdate,specialTime)){
                        IData output = new DataMap();
                        IData callData = new DataMap();
                        output.put("ORDER_ID", orderid);
                        output.put("CHANNEL_ID", channelid);
                        output.put("MARKETING_ACTION", marketingAction);
                        output.put("OSP_ORDER_ID", ospOrderId);
                        output.put("MSISDN", serial_number);
                        output.put("ICCID", iccid);
                        output.put("RESULT_CODE", "3019");
                        output.put("RESULT_DESC", "【特殊时期】："+specialTime);
                        output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                        callData = callAbilityPlatCommon(output);
                        //终止TF_B_ORDER_PRE表的数据
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
                        resultData.put("BIZ_CODE", "2999");
                        resultData.put("BIZ_DESC", "失败");
                        return resultData;

                    }
                    continue;
                }else if("0".equals(type)){
                    String specialStart =  config.getData(i).getString("PARA_CODE2").trim();
                    String specialend =  config.getData(i).getString("PARA_CODE3").trim();
                    if(sysdate.compareTo(specialStart)>0 && sysdate.compareTo(specialend)<0){
                        IData output = new DataMap();
                        IData callData = new DataMap();
                        output.put("ORDER_ID", orderid);
                        output.put("CHANNEL_ID", channelid);
                        output.put("MARKETING_ACTION", marketingAction);
                        output.put("OSP_ORDER_ID", ospOrderId);
                        output.put("MSISDN", serial_number);
                        output.put("ICCID", iccid);
                        output.put("RESULT_CODE", "3019");
                        output.put("RESULT_DESC", "【特殊时期】："+specialStart+"-"+specialend);
                        output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                        callData = callAbilityPlatCommon(output);
                        //终止TF_B_ORDER_PRE表的数据
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
                        resultData.put("BIZ_CODE", "2999");
                        resultData.put("BIZ_DESC", "失败");
                        return resultData;
                    }
                    continue;
                }
            }
        }
        //获取证件类型
        IDataset custList =new DatasetList();
        IDataset custInfoPspt = CustomerInfoQry.getCustIdByPspt(psptId);
        for(Object temp : custInfoPspt){

            IData custData=new DataMap();
            custData.put("PSPT_TYPE_CODE", custPsptTypeCode);
            custList.add(custData);
        }

        if(IDataUtil.isNotEmpty(custList)) {
            // 根据客户标记获取用户是否有欠费判断
            if (custList != null && custList.size() > 0) {
                IData oweFeeData = getOweFeeUserById(custInfoPspt);
                if (!oweFeeData.isEmpty()) {
                    IData output = new DataMap();
                    IData callData = new DataMap();
                    output.put("ORDER_ID", orderid);
                    output.put("CHANNEL_ID", channelid);
                    output.put("MARKETING_ACTION", marketingAction);
                    output.put("OSP_ORDER_ID", ospOrderId);
                    output.put("MSISDN", serial_number);
                    output.put("ICCID", iccid);
                    output.put("RESULT_CODE", "3003");
                    output.put("RESULT_DESC", "该客户有号码【" + oweFeeData.getString("OWE_FEE_SERIAL_NUMBER") + "】有往月欠费【" + oweFeeData.getString("OWE_FEE") + "】元，不能再次使用该证件办理开户业务！");
                    output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    callData = callAbilityPlatCommon(output);
                    //终止TF_B_ORDER_PRE表的数据
                    OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
                    resultData.put("BIZ_CODE", "2999");
                    resultData.put("BIZ_DESC", "失败");
                    return resultData;
                }
            }
        }


        try 
        {
            //校验sim卡信息
            if(StringUtils.isNotBlank(sim_card_no)){
                IDataset SimCardInfos = ResCall.getSimCardInfo("0",sim_card_no,"","");
                input.put("SIM_CARD_NO", sim_card_no);
                IData simcardinfo = SimCardInfos.getData(0);
                input.put("IMSI", simcardinfo.getString("IMSI",""));
                input.put("KI", simcardinfo.getString("KI",""));
                input.put("RES_KIND_CODE", simcardinfo.getString("RES_KIND_CODE",""));
                input.put("RES_KIND_NAME", simcardinfo.getString("RES_KIND_NAME",""));
                input.put("RES_TYPE_CODE", simcardinfo.getString("RES_TYPE_CODE",""));
                input.put("OPC_VALUE", simcardinfo.getString("OPC",""));
            }else{
                if(StringUtils.equals(input.getString("BUSI_TYPE"),"002")){
                    IDataset  simSetList = ResCall.selOneOccupyESim(serial_number,getVisit(),"10");
                    log.debug("simSetList======="+simSetList);
                    try {
                        input.put("SIM_CARD_NO", simSetList.getData(0).getString("ICC_ID"));
                        input.put("IMSI", simSetList.getData(0).getString("IMSI"));
                        input.put("KI", simSetList.getData(0).getString("KI",""));
                    }catch(Exception e){
                        throw new Exception(e.getMessage());
                    }
                }
            }

            input.put("FLAG_4G", "1");
            input.put("USER_PASSWD", genNewPasswd());

            if(log.isDebugEnabled()){
                log.debug("==BestUseMobileBean====SS.CreatePersonUserIntfSVC.tradeReg===input="+input);
            }
            IDataset result = CSAppCall.call("SS.CreatePersonUserIntfSVC.tradeReg", input);
            if(log.isDebugEnabled()){
                log.debug("==BestUseMobileBean====SS.CreatePersonUserIntfSVC.tradeReg===result="+result);
            }

            //REQ202004030003 一号一终端开户信息同步东软
            SynPictureInfo(input, result);

            resultData.put("BIZ_CODE", "0000");
            resultData.put("BIZ_DESC", "开户成功");
        }
        catch (Exception e) 
        {	
            String bizDesc = ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO");
            log.error("开户失败!!!!!!!!!!!!!!!!!!!!!!!!!!!!   "+bizDesc);
//            System.out.println("开户失败!!!!!!!!!!!!!!!!!!!!!!!!!!!!   "+bizDesc);
            if(bizDesc.length()>500){
                bizDesc = bizDesc.substring(0,500);
            }
            if(bizDesc.startsWith("CRM_COMM_983")){
                resultData.put("BIZ_CODE", "2998");
                resultData.put("BIZ_DESC", "工单处理中...");
            } else {
            	
                IData output = new DataMap();
                IData callData = new DataMap();
                output.put("ORDER_ID", orderid);
                output.put("CHANNEL_ID", channelid);
                output.put("MSISDN", serial_number);
                output.put("ICCID", iccid);
                output.put("RESULT_CODE", "2998");
                output.put("RESULT_DESC", "开户失败"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
                output.put("OPR_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                if(remark.endsWith("@mobileuseful") && "002".equals(marketingAction)){
                	output.put("MARKETING_ACTION", marketingAction);
					output.put("OSP_ORDER_ID", ospOrderId);
				}
                
                //该号码XXX已经被预占
                if(bizDesc.contains("RES_NUMBER_INTF_00006")||bizDesc.contains("资料表中不存在")){
                    output.put("RESULT_CODE", "2000");
                    output.put("RESULT_DESC", "开户失败"+"该号码"+serial_number+"已经被预占");
                    callData =callAbilityPlatCommon(output);
                	resultData.put("BIZ_CODE", "2000");
					resultData.put("BIZ_DESC","该号码"+serial_number+"已经被预占");
                }else if(bizDesc.contains("监护人信息")||bizDesc.contains("身份")){
                	output.put("RESULT_CODE", "2043");
                	output.put("RESULT_DESC", "用户身份信息错误");
					//调用结果反馈接口
					callData = callAbilityPlatCommon(output);
					System.out.println("结果反馈:"+callData);
					resultData.put("BIZ_CODE", "2043");
					resultData.put("BIZ_DESC","用户身份信息错误");
					
				}else{
                    output.put("RESULT_CODE", "2099");
                    output.put("RESULT_DESC", "其他原因");
                	callData = callAbilityPlatCommon(output);
                    resultData.put("BIZ_CODE", "2998");
                    resultData.put("BIZ_DESC", "开户处理结果失败,能开返回信息: "+callData);
                }
                //调用结果反馈接口
                
                System.out.println("----------------output--------------:"+output.toString());
                System.out.println("----------------resultData--------------:"+resultData.toString());

                //终止TF_B_ORDER_PRE表的数据
                OrderPreInfoQry.updateOrderPreInfoBySnPreType(serial_number,PRETYPE,"-1","开户执行异常: "+callData.toString()+";output:"+output.toString(),"1","0");
            }
        }
        return resultData;
    }

    /**
     * REQ202004030003 一号一终端开户信息同步东软
     * 开户信息同步东软，入接口表TD_B_PICTURE_INFO
     * @param input
     * @param result
     * @throws Exception
     */
    private void SynPictureInfo(IData input, IDataset result) throws Exception {
        if(log.isDebugEnabled()){
            log.debug("==BestUseMobileBean====SynPictureInfo===result="+result+";input="+input);
        }

        IData psptinfo = getParam(input,"PSPT_INFO");//证件信息
        if(IDataUtil.isEmpty(psptinfo)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "711000:接口参数检查，输入参数[PSPT_INFO]不能为空！");
        }
        String psptId = IDataUtil.chkParamNoStr(input, "PSPT_ID");// 证件号码
        String identitySn = IDataUtil.chkParamNoStr(psptinfo, "IDENTITY_SN");// 实名认证流水

        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.first()) && StringUtils.isNotEmpty(result.first().getString("TRADE_ID"))) {
            String tradeId = result.first().getString("TRADE_ID");
            String userId = result.first().getString("USER_ID");

            IData pictureInfoData = new DataMap();
            pictureInfoData.put("TRADE_ID",tradeId);
            pictureInfoData.put("ACCEPT_MONTH",StrUtil.getAcceptMonthById(tradeId));
            pictureInfoData.put("USER_ID",userId);
            pictureInfoData.put("SERIAL_NUMBER",input.getString("SERIAL_NUMBER"));
            pictureInfoData.put("CARD_ID",psptId);
            pictureInfoData.put("BUSINESS_TYPE","10");//业务类型开户
            pictureInfoData.put("TRANSACTION_ID",identitySn);
            pictureInfoData.put("CHANNEL_TYPE","6");//办理渠道，6表示一号一终端开户
            pictureInfoData.put("PIC_NNAME_Z","BOSS898"+identitySn+"_Z.jpg");//身份证正面照片文件名
            pictureInfoData.put("PIC_NNAME_F","BOSS898"+identitySn+"_F.jpg");//身份证背面照片文件名
            pictureInfoData.put("PIC_NNAME_R","BOSS898"+identitySn+"_R.jpg");//活体验证视频帧文件名
            pictureInfoData.put("DEAL_TAG","0");

            if(log.isDebugEnabled()){
                log.debug("==BestUseMobileBean====SynPictureInfo===pictureInfoData="+pictureInfoData);
            }
            Dao.insert("TD_B_PICTURE_INFO", pictureInfoData, Route.CONN_CRM_CEN);
        }
    }

    // 产生随机密码不加密
    public static String genNewPasswd()
    {

        String password = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++)
        {
            int k = random.nextInt(10);
            password += k;
        }
        return password;
    }

    /**
	 * 接收能开传入的Profile准备结果通知，提醒营业员提示用户可以
	 * 登录SM-DP+平台下载profile
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData profilePrepareResult(IData input) throws Exception {
		IData res = new DataMap();
		IData result = new DataMap();
    	result.put("bizCode", "0000");
    	result.put("bizDesc", "success");
    	
    	System.out.print("-----------mqx--profilePrepareResult-input--------:"+input);
		String bizCode = IDataUtil.chkParam(input, "BIZ_CODE");
		String bizDesc = input.getString("BIZ_DESC","");
		IDataUtil.chkParam(input, "CARD_DATA");
		System.out.println("=====CIP74入参=="+input.getString("CARD_DATA"));
		IData cardData = new DataMap(input.getString("CARD_DATA"));
		String ospOrderId = IDataUtil.chkParam(cardData, "ORDER_ID");
		String serialNumber = IDataUtil.chkParam(cardData, "SERIAL_NUMBER");
		String eid = IDataUtil.chkParam(cardData, "EID");
		String iccid = IDataUtil.chkParam(cardData, "ICCID");
		
		IDataset qryInfoset = queryReCardResult(ospOrderId, null,RouteInfoQry.getEparchyCodeBySn(cardData.getString("SERIAL_NUMBER")));
		if(IDataUtil.isNotEmpty(qryInfoset)){
			result.put("bizCode", "4008");
        	result.put("bizDesc", "重复操作！");
        	res.put("result", result);
        	res.put("respCode", "20002");
        	res.put("respDesc", "Repeat call");
        	return res;
		}
		
		//给营业员发公告消息
		StringBuilder noticeMsg = new StringBuilder(100);
		noticeMsg.append("号码：").append(serialNumber).append("在客户端申请的订单号：").append(ospOrderId)
		.append("的订单,一号一终端业务受理平台返回的profile准备情况为：");
		if("1".equals(bizCode)){
			noticeMsg.append("成功！请通过客户端登录SM-DP+平台下载profile.");
		}else{
			noticeMsg.append("失败! 失败原因："+bizDesc);
		}
		IData content = new DataMap();
        content.put("TYPE", "002");
        content.put("TOPIC", noticeMsg.toString());
        IData message = new DataMap();
        IData staffInfo = getOneNumOneDeviceCache(ospOrderId);
        if(IDataUtil.isEmpty(staffInfo)){
        	result.put("bizCode", "2998");
        	result.put("bizDesc", "未查询到该订单号的营业厅受理信息!");
        	res.put("result", result);
        	res.put("respCode", "2998");
        	res.put("respDesc", "未查询到该订单号的营业厅受理信息!");
        	return res;
        }
        
        message.put("STAFF_ID", input.getString("STAFF_ID",""));
        message.put("CONTENT", content);
        log.debug(">>>>>>>>>>>>>>>>推送卡数据准备 "+message);
        try{
            MessageFactory.sendMessage(message);
            delOneNumOneDeviceCache(ospOrderId);
        }catch(Exception ex1){
            log.debug(">>>>>>>>>>>>>>>>推送卡数据准备完成消息失败！ "+ex1.getMessage());
        }
           
        //记录日志
        IData tableData = new DataMap();
		tableData.put("TRANSACTION_ID", ospOrderId);
		tableData.put("ACCEPT_MONTH", SysDateMgr.getCurMonth());
		tableData.put("STAFF_ID", getVisit().getStaffId());
		tableData.put("SERIAL_NUMBER", serialNumber);
		tableData.put("ICCID", iccid);
		tableData.put("EMPTY_CARD_ID", eid);
		tableData.put("ORD_CODE", "一号一终端Profile准备结果通知");
		tableData.put("TRADE_ID", ospOrderId);
		//tableData.put("ORDER_ID", ospOrderId);		
		tableData.put("UPDATE_TIME", SysDateMgr.getSysTime());	
		try{
			Dao.insert("TF_F_RECARD_INFO", tableData,RouteInfoQry.getEparchyCodeBySn(cardData.getString("SERIAL_NUMBER")));
		}catch(Exception e){
			log.debug(">>>>>>>>>>>>>>>>Profile准备结果通知保存日志失败！ "+e.getMessage());
		}
		res.put("result", result);
		res.put("respCode", "00000");
		res.put("respDesc", "成功");
		return res;
	}
    /**一号一终端
	 * 查询是否申请补换eSIM设备
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData qryApplyReplaceESIM(IData input) throws Exception{
		IData result = new DataMap();
		
		IDataUtil.chkParamNoStr(input, "EID");
		IDataUtil.chkParamNoStr(input, "IMEI");
		IDataUtil.chkParamNoStr(input, "SERIAL_NUMBER");
		
		
		
		String userid = input.getString("USER_ID");
		
		String oldEid = "";
		String oldImei="";
		//查询设备原EID 和IMEI
		IData in = new DataMap();
		in.put("USER_ID", userid);
		in.put("USER_ID_A", "-1");
		in.put("RES_TYPE_CODE","E" );
		IDataset resDataset = getUserResInfoByUserIdRestype(in);
		if(IDataUtil.isNotEmpty(resDataset) && "OneNoOneTerminal".equals(resDataset.getData(0).getString("RSRV_STR1",""))){
			//开户时RSRV_STR2  ：eid@Imei
			String eidImeis = resDataset.getData(0).getString("RSRV_STR2","");
			String [] eidImei = eidImeis.split("@");
			if(eidImei.length>1){
				oldEid = eidImei[0];
				oldImei = eidImei[1];
			}
		}
		IData param = new DataMap();
		param.put("eid",oldEid);
		param.put("imei",oldImei);
		param.put("MSISDN",input.getString("SERIAL_NUMBER"));
		param.put("orderTime",SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		
		result.put("NEW_EID", input.getString("EID"));
		result.put("IMEI", input.getString("IMEI"));
		result.put("OLD_EID", oldEid);
		//result.put("IMEI", input.getString("IMEI"));
		//调能力开放平台
		IData param1 = new DataMap();
		param1.put("PARAM_NAME", "crm.ABILITY.CIP72");
		IDataset Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		String Abilityurl = "";
		if (Abilityurls != null && Abilityurls.size() > 0)
		{
			Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
		}
		else
		{
			CSAppException.appError("-1", "crm.ABILITY.UP接口地址未在TD_S_BIZENV表中配置");
		}
		
		
		IData callData = new DataMap();
		try {
			callData = callAbilityPlat(param);
		} catch (Exception e) {
			result.put("X_RESULTCODE", "-1");
	        result.put("X_RESULT_INFO", "查询补换eSIM设备申请记录失败！ "+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
	        return result;
		}
		System.out.println(">>>>>>>>>>>>qryApplyReplaceESIM>>>>>>>>>callData>>>"+callData);
		String orderId = "";
		if(IDataUtil.isNotEmpty(callData)){
			IData spdata = callData.getData("result");
			if(IDataUtil.isNotEmpty(spdata)&&"0000".equals(spdata.getString("bizCode"))){
				orderId = spdata.getString("orderId");
				if(StringUtils.isEmpty(orderId)){
					result.put("X_RESULTCODE", "-1");
					result.put("X_RESULT_INFO","一级能开返回补换eSIM设备申请订单号为空,业务无法继续！");
					return result;
				}				
			}else{
				result.put("X_RESULTCODE", "-1");
				result.put("X_RESULT_INFO","查询补换eSIM设备申请记录一级能开返回失败！ "+spdata.getString("bizDesc"));
				return result;
			}	
			setOneNumOneDeviceCache(orderId,getVisit().getStaffId());
		}
		System.out.println("==setOneNumOneDeviceCache=="+getVisit().getStaffId());
		System.out.println("==setOneNumOneDeviceCache=="+input.getString("STAFF_ID"));
		
		//获取新卡数据、卡费、VIP等信息
		IData simSet = new DataMap();
		try {
				SimCardBean simCardBean = new SimCardBean();
				simSet = simCardBean.verifyIMEI(input);
			} catch (Exception e) {
				result.put("X_RESULTCODE", "-1");
				result.put("X_RESULT_INFO", "从资源获取新ESIM卡数据信息失败！ "+e.getMessage());
				return result;
		}
				
		result.putAll(simSet);
		result.put("OSP_ORDER_ID", orderId);
		result.put("X_RESULTCODE", "0");
		result.put("X_RESULT_INFO", "ok");
		return result;
	}
    
    private IData callAbilityPlat(IData data) throws Exception {
    	IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.CIP72");
        IDataset urls = Dao.qryBySql(getInterFaceSQL, paramurl, "cen");
        String url = "";
        if (urls != null && urls.size() > 0)
        {
            url = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.CIP72接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = url+"?";
        
        //类型
        String formatType="json";
        //time
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
        String timestamp = fmt.format(new Date()); // 时间戳，自动生成
        IData ret=new DataMap();
        String content= data.toString();
        try{
        // 唯一流水32位以内
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");// 请求流水号，自动生成
        String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
        String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
        String staticToken=StaticUtil.getStaticValue("ABILITY_STATIC_TOKEN", "1");// 应用的静态tocken
        String access_token=AbilityEncrypting.getTockenStr(appId,staticToken);
     // 以下5项为非必填项,如果不填请给""或者null
        String userAuthorizationCode = ""; // 用户授权码，申请用户授权时，或者需要用户授权时填写
        String userPhoneNumber = ""; // 用户手机号
        String bIPCode = ""; // 业务流程编码
        String version = ""; // 业务流程版本号，若填写bIPCode，该字段必填
        String nodeId = ""; // 本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
        String sign = AbilityEncrypting.getSign(appId, timestamp, messageId, access_token, sessionId, content,
                userAuthorizationCode, userPhoneNumber, bIPCode, version, nodeId);
        //组合参数
//        String publicParam="&appId="+appId+"&access_token="+access_token+"&sign="+sign+"&timestamp="+timestamp+
//                           "&messageId="+messageId+"&sessionId="+sessionId;
//        String requestUrl = apiAddress+publicParam;
        IData input=new DataMap();
        input.put("appId", appId);
        input.put("access_token", access_token);
        input.put("sign", sign);
        input.put("timestamp", timestamp);
        input.put("messageId", messageId);
        input.put("sessionId", sessionId);
        input.put("content", data);  
        System.out.println("-----input:----- "+input);
        String inputStr =input.toString();
        System.out.println("inputStr: "+inputStr);
        String result = httpRequest(apiAddress, inputStr);
        System.out.println("-----result:----- "+result);
        ret=new DataMap(result); 
        }catch(Exception ex){
             throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
	}

	public final static IData callAbilityPlatCommon(IData data) throws Exception{
        IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.BUMB");
        IDataset urls = Dao.qryBySql(getInterFaceSQL, paramurl, "cen");
        String url = "";
        if (urls != null && urls.size() > 0)
        {
            url = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.BUMB接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = url+"?";
        
        //类型
        String formatType="json";
        //time
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
        String timestamp = fmt.format(new Date()); // 时间戳，自动生成
        IData ret=new DataMap();
        String content= data.toString();
        try{
        // 唯一流水32位以内
        String sessionId = UUID.randomUUID().toString().replaceAll("-", "");// 请求流水号，自动生成
        String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
        String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
        String staticToken=StaticUtil.getStaticValue("ABILITY_STATIC_TOKEN", "1");// 应用的静态tocken
        String access_token=AbilityEncrypting.getTockenStr(appId,staticToken);
     // 以下5项为非必填项,如果不填请给""或者null
        String userAuthorizationCode = ""; // 用户授权码，申请用户授权时，或者需要用户授权时填写
        String userPhoneNumber = ""; // 用户手机号
        String bIPCode = ""; // 业务流程编码
        String version = ""; // 业务流程版本号，若填写bIPCode，该字段必填
        String nodeId = ""; // 本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
        String sign = AbilityEncrypting.getSign(appId, timestamp, messageId, access_token, sessionId, content,
                userAuthorizationCode, userPhoneNumber, bIPCode, version, nodeId);
        //组合参数
//        String publicParam="&appId="+appId+"&access_token="+access_token+"&sign="+sign+"&timestamp="+timestamp+
//                           "&messageId="+messageId+"&sessionId="+sessionId;
//        String requestUrl = apiAddress+publicParam;
        IData input=new DataMap();
        input.put("appId", appId);
        input.put("access_token", access_token);
        input.put("sign", sign);
        input.put("timestamp", timestamp);
        input.put("messageId", messageId);
        input.put("sessionId", sessionId);
        input.put("content", data);  
        System.out.println("-----input:----- "+input);
        String inputStr =input.toString();
        System.out.println("inputStr: "+inputStr);
        String result = httpRequest(apiAddress, inputStr);
        System.out.println("-----result:----- "+result);
        ret=new DataMap(result); 
        }catch(Exception ex){
             throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
    }

    public final static IData callAbilityPlatCommon(IData data,String abilityCode) throws Exception{
        IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", abilityCode);
        IDataset urls = Dao.qryBySql(getInterFaceSQL, paramurl, "cen");
        String url = "";
        if (urls != null && urls.size() > 0)
        {
            url = urls.getData(0).getString("PARAM_VALUE", "");
        }
        else
        {
            CSAppException.appError("-1", "crm.ABILITY.BUMB接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = url+"?";

        //类型
        String formatType="json";
        //time
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss"); //精确到毫秒
        String timestamp = fmt.format(new Date()); // 时间戳，自动生成
        IData ret=new DataMap();
        String content= data.toString();
        try{
            // 唯一流水32位以内
            String sessionId = UUID.randomUUID().toString().replaceAll("-", "");// 请求流水号，自动生成
            String appId=StaticUtil.getStaticValue("ABILITY_APP_ID", "1");// 应用ID
            String messageId=SysDateMgr.getSysDateYYYYMMDDHHMMSS()+timestamp+SeqMgr.getLogId().substring(12);// 业务流水号（32位）
            String staticToken=StaticUtil.getStaticValue("ABILITY_STATIC_TOKEN", "1");// 应用的静态tocken
            String access_token=AbilityEncrypting.getTockenStr(appId,staticToken);
            // 以下5项为非必填项,如果不填请给""或者null
            String userAuthorizationCode = ""; // 用户授权码，申请用户授权时，或者需要用户授权时填写
            String userPhoneNumber = ""; // 用户手机号
            String bIPCode = ""; // 业务流程编码
            String version = ""; // 业务流程版本号，若填写bIPCode，该字段必填
            String nodeId = ""; // 本次能力在流程中所处的节点编码，若填bIPCode，该字段必填
            String sign = AbilityEncrypting.getSign(appId, timestamp, messageId, access_token, sessionId, content,
                    userAuthorizationCode, userPhoneNumber, bIPCode, version, nodeId);
            //组合参数
//        String publicParam="&appId="+appId+"&access_token="+access_token+"&sign="+sign+"&timestamp="+timestamp+
//                           "&messageId="+messageId+"&sessionId="+sessionId;
//        String requestUrl = apiAddress+publicParam;
            IData input=new DataMap();
            input.put("appId", appId);
            input.put("access_token", access_token);
            input.put("sign", sign);
            input.put("timestamp", timestamp);
            input.put("messageId", messageId);
            input.put("sessionId", sessionId);
            input.put("content", data);
            log.debug("-----input:----- "+input);
            String inputStr =input.toString();
            log.debug("inputStr: "+inputStr);
            String result = httpRequest(apiAddress, inputStr);
            log.debug("-----result:----- "+result);
            ret=new DataMap(result);
        }catch(Exception ex){
            throw new Exception("API:get CityCode failed:" + ex.getMessage());
        }
        return ret;
    }
    /**
     * 方法体说明：向远程接口发起请求，返回字符串类型结果
     * @param url 接口地址
     * @param requestMethod 请求类型
     * @param params 传递参数
     * @return String 返回结果
     */
    public static String httpRequest(String url, String requestBody) {
        // 接口返回结果
        String methodResult = null;
        try {
            HttpClient client = new HttpClient();
            PostMethod post = new PostMethod(url);
            post.addRequestHeader("Content-Type","application/json;charset=utf-8");
            post.setRequestBody(requestBody);
            client.executeMethod(post);
            methodResult = post.getResponseBodyAsString();
        } catch (UnsupportedEncodingException e) {
            log.error("不支持的编码格式", e);
        } catch (IOException e) {
            log.error("IO异常", e);
        }
        return methodResult;
    }
    
    public String encodeIdType(String IdType)
    {
        String lanuchTdType = null;

        if ("00".equals(IdType)||"0".equals(IdType))
        {
            lanuchTdType = "0";
        }
        else if ("01".equals(IdType)||"1".equals(IdType))
        {
            lanuchTdType = "1";
        }
        else if ("02".equals(IdType)||"A".equals(IdType))
        {
            lanuchTdType = "A";
        }
        else if ("04".equals(IdType)||"C".equals(IdType))
        {
            lanuchTdType = "C";
        }
        else if ("05".equals(IdType)||"K".equals(IdType))
        {
            lanuchTdType = "K";
        }
        else
        {
            lanuchTdType = "Z";
        }

        return lanuchTdType;
    }
    
    private IDataset dealPayFee(IData input) {
        IDataset tradeFeeSub = new DatasetList();
        int xPayFee = Integer.parseInt(StringUtils.isNotBlank(input.getString("X_FPAY_FEE","0"))?input.getString("X_FPAY_FEE","0"):"0");
        if(xPayFee>0){          
            int resFee = Integer.parseInt(StringUtils.isNotBlank(input.getString("RES_FEE","0"))?input.getString("RES_FEE","0"):"0");
            if(resFee>0){//号码费
                IData feeData = new DataMap();
                feeData.put("FEE_MODE", "2");
                feeData.put("FEE_TYPE_CODE","30");
                feeData.put("FEE",  String.valueOf((resFee)/100));
                feeData.put("PAY",  String.valueOf((resFee)/100));      
                feeData.put("TRADE_TYPE_CODE","10");
                tradeFeeSub.add(feeData);
            }
            int operFee = Integer.parseInt(StringUtils.isNotBlank(input.getString("OPER_FEE","0"))?input.getString("OPER_FEE","0"):"0");
            if((operFee)>0){//卡费
                IData feeData = new DataMap();
                feeData.put("FEE_MODE", "2");
                feeData.put("FEE_TYPE_CODE","10");
                feeData.put("FEE", String.valueOf((operFee)/100));
                feeData.put("PAY", String.valueOf((operFee)/100));      
                feeData.put("TRADE_TYPE_CODE","10");
                tradeFeeSub.add(feeData);
            }
            int otherFee = Integer.parseInt(StringUtils.isNotBlank(input.getString("OTHER_FEE","0"))?input.getString("OTHER_FEE","0"):"0");
            if(otherFee>0){//其它费
                IData feeData = new DataMap();
                feeData.put("FEE_MODE", "2");
                feeData.put("FEE_TYPE_CODE","150");
                feeData.put("FEE",  String.valueOf((otherFee)/100));
                feeData.put("PAY",  String.valueOf((otherFee)/100));        
                feeData.put("TRADE_TYPE_CODE","10");
                tradeFeeSub.add(feeData);
            }
            int advancePay = Integer.parseInt(StringUtils.isNotBlank(input.getString("ADVANCE_PAY","0"))?input.getString("ADVANCE_PAY","0"):"0");
            if(advancePay>0){//预存
                IData feeData = new DataMap();
                feeData.put("FEE_MODE", "2");
                feeData.put("FEE_TYPE_CODE", input.getString("PAY_FEE_TYPE_CODE", "0"));
                feeData.put("FEE",  String.valueOf((advancePay)/100));
                feeData.put("PAY",  String.valueOf((advancePay)/100));      
                feeData.put("TRADE_TYPE_CODE","10");
                tradeFeeSub.add(feeData);
            }           
        }
       
        return tradeFeeSub;
    }
    
    private IDataset getSpiltSet(String source, int byteLength){
        byte[] sByte = source.getBytes();
        char[] sChar = source.toCharArray();

        IDataset dataset = new DatasetList();

        if (sByte.length <= byteLength)  {
            dataset.add(source);
        } else  {
            int byleCount = 0;
            int first = 0;
            for (int i = 0; i < sChar.length; i++) {
                if ((int) sChar[i] > 0x80) {
                    byleCount += 2;
                } else {
                    byleCount += 1;
                }
                if (byleCount == byteLength){
                    if (first == 0) {
                        dataset.add(new String(sChar, first, i + 1));
                    } else {
                        dataset.add(new String(sChar, first, i - first + 1));
                    }
                    first = i + 1;
                    byleCount = 0;
                }
                if (byleCount == byteLength + 1)  {
                    if (first == 0)  {
                        dataset.add(new String(sChar, first, i));
                        first = i;
                    } else {
                        dataset.add(new String(sChar, first, i - first));
                        first = i;
                    }
                    i -= 1;
                    byleCount = 0;
                }
            }
            if (byleCount != 0){
                dataset.add(new String(sChar, first, sChar.length - first));
            }
        }
        return dataset;
    }
    private void delOneNumOneDeviceCache(String ospOrderId) throws Exception	{
		SharedCache.delete(OneNumOneDevice_ + ospOrderId); 
	}
    
    private IData getOneNumOneDeviceCache(String ospOrderId) throws Exception	{
		 Object shareCache = SharedCache.get(OneNumOneDevice_ + ospOrderId);
		 return (null == shareCache ? null : ((IData) shareCache));
	}
    private void setOneNumOneDeviceCache(String ospOrderId, String StaffId) throws Exception	{
		IData param = new DataMap();
		param.put("STAFF_ID", StaffId); //保存补换esim设备，营业员工号
		SharedCache.set(OneNumOneDevice_ + ospOrderId, param, 1800); // 秒
	}
    
    /**
     * TF_F_USER_RES  预留字段2 保存了OLD_EID
     * @param user_id
     * @param user_id_a
     * @param res_type_code
     * @return
     * @throws Exception
     */
    public  IDataset getUserResInfoByUserIdRestype(IData in) throws Exception
    {

        IData param = new DataMap();
        param.put("USER_ID", in.getString("USER_ID"));
        param.put("USER_ID_A", in.getString("USER_ID_A"));
        param.put("RES_TYPE_CODE", in.getString("RES_TYPE_CODE"));
        return Dao.qryByCode("TF_F_USER_RES", "SEL_BY_USERID_IMS", param);
    }
    
    
    /**一号一终端
	 *  向一级能力开放平台发起补换eSIM成功通知或者向能力开放平台同步销号通知
	 * @param input
	 * @return
	 * @throws Exception
	 */
	public IData noticeChangeCardOrDestroy(IData input) throws Exception{
		IData result = new DataMap();
		
		IDataUtil.chkParamNoStr(input, "MSISDN");
		IDataUtil.chkParamNoStr(input, "bizType");
		IDataUtil.chkParamNoStr(input, "ICCID1");
		IDataUtil.chkParamNoStr(input, "eid1");
		String bizType = input.getString("bizType");
		
		IData paramurl = new DataMap();
        paramurl.put("PARAM_NAME", "crm.ABILITY.CIP73");
        IDataset urls = Dao.qryBySql(AbilityEncrypting.getInterFaceSQL, paramurl, "cen");
        String url = "";
        
        if (urls != null && urls.size() > 0)
        {
           url = urls.getData(0).getString("PARAM_VALUE", "");
         }
         else
         {
             CSAppException.appError("-1", "crm.feedback接口地址未在TD_S_BIZENV表中配置");
         }
        
        String apiAddress = url;
        IData callData = new DataMap();
		//补换卡
		if("002".equals(bizType)){
			
            IData param = new DataMap();
    		param.put("orderId", input.getString("orderId"));
    		param.put("MSISDN", input.getString("MSISDN"));
    		param.put("eid1", input.getString("eid1"));
    		param.put("ICCID1", input.getString("ICCID1"));
    		param.put("bizType", "002");//补换卡
    		param.put("eid2", input.getString("eid2"));
    		param.put("ICCID2", input.getString("ICCID2"));
    		param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
            
            callData = AbilityEncrypting .callAbilityPlatCommon(apiAddress,param);
            
		}else if("003".equals(bizType)){//销户
			
	        IData param = new DataMap();
			param.put("orderId", input.getString("orderId",""));
			param.put("MSISDN",input.getString("MSISDN"));
			param.put("eid1", input.getString("eid1"));
			param.put("ICCID1", input.getString("ICCID1"));
			param.put("bizType", "003");//销户
			param.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
			
			callData = AbilityEncrypting .callAbilityPlatCommon(apiAddress,param);
		}
		System.out.println("====能开返回===callData"+callData);
		result.put("bizCode", "0000");
		result.put("bizDesc", "success");
		return result;
		
	}
	
	
    /**
     * 查询异地补卡结果反馈信息
     * 
     * @param tradeId
     * @return
     * @throws Exception
     */
    public static IDataset queryReCardResult(String tradeId, Pagination pagination,String routeId) throws Exception
    {
        IData param = new DataMap();
        param.put("TRADE_ID", tradeId);
 
        return Dao.qryByCodeParser("TF_F_RECARD_INFO", "SEL_BY_TRADE", param, pagination, routeId);
    }




    private void setUserInfoCheckReslutCache(String serialNumber,String bizCodeResult, String bizDescResult) throws Exception{
        IData param = new DataMap();
        param.put("msisdn", serialNumber);
        param.put("bizCode", bizCodeResult);
        param.put("bizDesc", bizDescResult);
        IData data = new DataMap();
        data.put(serialNumber, param);
        Object shareCache = SharedCache.get(eSIMDeviceCheck_);
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>setUserInfoCheckReslutCache>>>>>start>>>>>>>>>>>"+shareCache+"|"+bizCodeResult+"|"+bizDescResult);
        }
        IDataset dataset = new DatasetList();
        if(null == shareCache){
            dataset.add(data);
            SharedCache.set(eSIMDeviceCheck_, dataset, 300); // 秒
        }else{
            dataset = (IDataset) shareCache;
            dataset.add(data);
            SharedCache.set(eSIMDeviceCheck_, dataset, 300); // 秒
        }
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>setUserInfoCheckReslutCache>>>>>end>>>>>>>>>>>"+SharedCache.get(eSIMDeviceCheck_));
        }
    }

    private IDataset getUserInfoCheckCache() throws Exception{
        Object shareCache = SharedCache.get(eSIMDeviceCheck_);
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>getUserInfoCheckCache>>>>>start>>>>>>>>>>>"+shareCache);
        }
        if(null == shareCache){
            return null;
        }else{
            return (IDataset) shareCache;
        }
    }

    private int deleteUserInfoCheckCache(String serialNumber) throws Exception{
        Object shareCache = SharedCache.get(eSIMDeviceCheck_);
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>deleteUserInfoCheckCache>>>>>start>>>>>>>>>>>"+shareCache);
        }
        if(null == shareCache){
            return 0;
        }else{
            IDataset checkRequestcaches = (IDataset) shareCache;
            for(Iterator<Object> iterator = checkRequestcaches.iterator(); iterator.hasNext();){
                IData checkRequestcache = (IData) iterator.next();
                if(StringUtils.isNotEmpty(checkRequestcache.getString(serialNumber))){
                    iterator.remove();
                }
            }
            SharedCache.set(eSIMDeviceCheck_, checkRequestcaches, 300); // 秒
            if(log.isDebugEnabled()){
                log.debug(">>>>>>>>>>deleteUserInfoCheckCache>>>>>start>>>>>>>>>>>"+SharedCache.get(eSIMDeviceCheck_));
            }
            return 1;
        }
    }

    private void setESIMDeviceCheckResultCache(String eid, String bizCode, String bizdesc) throws Exception	{
        IData cache = getOneNumOneDeviceCache(eid);
        if(IDataUtil.isNotEmpty(cache)){
            cache.put("BIZ_CODE", bizCode);
            cache.put("BIZ_DESC", bizdesc);
            SharedCache.set(OneNumOneDevice_ + eid, cache, 1800); // 秒
        }
    }

    private IDataset qryESIMQRCodeInfo(IData input) throws Exception {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        param.put("EID", input.getString("EID"));
        param.put("BIZ_TYPE", input.getString("BIZ_TYPE"));
        return Dao.qryByCodeParser("TF_B_ESIM_QRCODE", "SEL_BY_UNANSWERED", param);
    }

    private void updateESIMQRCodeInfo(IData qrCodeInfo,IData input) throws Exception	{
        IData param = new DataMap();
        param.put("QR_CODE_ID", qrCodeInfo.getString("QR_CODE_ID"));
        param.put("BIZ_CODE", input.getString("BIZ_CODE"));
        param.put("BIZ_DESC", input.getString("BIZ_DESC"));
        param.put("DOWNLOAD_URL", input.getString("DOWNLOAD_URL"));
        param.put("ACTIVATION_CODE", input.getString("ACTIVATION_CODE"));
        param.put("BIZ_TYPE_TIME", input.getString("BIZTYPE_TIME"));
        Dao.executeUpdateByCodeCode("TF_B_ESIM_QRCODE", "UPD_BY_QR_CODE_ID", param);
    }

    /**
     * 校验eSIM设备合法性结果通知（CIP00126）
     * @param input
     * @return
     * @throws Exception
     */
    public IData eSIMDeviceCheckResult(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        String bizCode = "";
        String bizdesc = "";
        String eid = "";
        try{
            bizCode = IDataUtil.chkParam(input, "BIZ_CODE");
            bizdesc = input.getString("BIZ_DESC","");
            eid = IDataUtil.chkParam(input, "EID");
        }catch(Exception e){
            result.put("BIZ_CODE", "4999");
            result.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            return result;
        }

        IData cache = getOneNumOneDeviceCache(eid);
        if(IDataUtil.isEmpty(cache)){
            result.put("BIZ_CODE", "4999");
            result.put("BIZ_DESC", "未找到此设备的校验请求信息.EID:"+eid);
            return result;
        }

        try{
            setESIMDeviceCheckResultCache(eid,bizCode,bizdesc);
        }catch(Exception e){
            result.put("BIZ_CODE", "4999");
            result.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            return result;
        }

        return result;
    }

    /**
     * 准备profile结果通知（CIP00128）
     * @param input
     * @return
     * @throws Exception
     */
    public IData prepareProfileResult(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        String serialNumber ="";
        String primarymsisdn = "";
        String eid = "";
        String bizType = "";
        String bizCode = "";
        String bizDesc = "";
        String bizTypeTime = "";
        String downloadURL = "";
        String activationCode = "";
        try{
            serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            primarymsisdn = input.getString("PRIMARY_SERIAL_NUMBER");
            eid = IDataUtil.chkParam(input, "EID");
            bizType = IDataUtil.chkParam(input, "BIZ_TYPE");
            bizCode = IDataUtil.chkParam(input, "BIZ_CODE");
            bizDesc = input.getString("BIZ_DESC");
            bizTypeTime = input.getString("BIZTYPE_TIME");
            if("1".equals(bizCode)){
                downloadURL = IDataUtil.chkParam(input, "DOWNLOAD_URL");
                activationCode = IDataUtil.chkParam(input, "ACTIVATION_CODE");
            }
        }catch(Exception e){
            result.put("BIZ_CODE", "4999");
            result.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            return result;
        }

        IDataset staffInfo = qryESIMQRCodeInfo(input);
        if(IDataUtil.isEmpty(staffInfo)){
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", "未查询到该设备的受理信息!");
            return result;
        }
        if(IDataUtil.isNotEmpty(staffInfo.getData(0))) {
            updateESIMQRCodeInfo(staffInfo.getData(0), input);
        }

        return result;
    }

    /**
     * 用户登记信息校验请求(CIP00129)
     * @param input
     * @return
     * @throws Exception
     */
    public IData userInfoCheckRequest(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        String serialNumber ="";
        String custName = "";
        String idNum = "";
        String servicePwd = "";

        //校验手机号码、服务密码、身份证信息，并将校验结果保存至缓存
        String bizCodeResult = "";
        String bizDescResult  = "";
        try{
            serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            custName = IDataUtil.chkParam(input, "CUSTOMER_NAME");
            idNum = IDataUtil.chkParam(input, "ID_NUM"); //身份证号码
            servicePwd = IDataUtil.chkParam(input, "SERVICE_PWD");//服务密码

            IData decodeResult = decodeByIboss(input);
            custName = decodeResult.getString("CUSTOMER_NAME");
            idNum = decodeResult.getString("ID_NUM");
            servicePwd = decodeResult.getString("SERVICE_PWD");
        }catch(Exception e){
            result.put("BIZ_CODE", "4999");
            result.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            bizCodeResult = "2";
            bizDescResult="处理异常："+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO");
            setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
            return result;
        }


        IDataset checkRequestcaches = getUserInfoCheckCache();
        if(IDataUtil.isNotEmpty(checkRequestcaches)){
            if(checkRequestcaches.size()>30){
                result.put("BIZ_CODE", "5000");
                result.put("BIZ_DESC", "系统繁忙,请稍后再试");
                bizCodeResult = "2";
                bizDescResult="系统繁忙,请稍后再试";
                setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                return result;
            }

            for(Iterator<Object> iterator = checkRequestcaches.iterator(); iterator.hasNext();){
                IData checkRequestcache = (IData) iterator.next();
                if(StringUtils.isNotEmpty(checkRequestcache.getString(serialNumber))){
                    result.put("BIZ_CODE", "4008");
                    result.put("BIZ_DESC", "重复的操作！");
                    bizCodeResult = "2";
                    bizDescResult="重复的操作！";
                    setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                    return result;
                }
            }
        }

        try{
            IData userData = com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry.qryUserInfoBySn(serialNumber);
            if (IDataUtil.isEmpty(userData)) {
                bizCodeResult = "2";
                bizDescResult = "手机号码信息不存在";
                setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                return result;
            }

            String userPassword = userData.getString("USER_PASSWD");//加密的
            if(log.isDebugEnabled()){
                log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>pwd>>>>>>>>>>>userPassword："+userPassword+",原servicePwd:"+servicePwd);
            }
            servicePwd = com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.passwdmgr.PasswdMgr.encryptPassWD(servicePwd, userData.getString("USER_ID"));
            if(log.isDebugEnabled()){
                log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>pwd>>>>>>>>>>>userPassword："+userPassword+",servicePwd:"+servicePwd);
            }
            if(!servicePwd.equals(userPassword)){
                bizCodeResult = "2";
                bizDescResult = "客服密码不正确";
                setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                return result;
            }

            com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData ucaData = com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory.getNormalUca(serialNumber);
            com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData custPersonDate = ucaData.getCustPerson();
            String userName = custPersonDate.getCustName();
            String userPsptId = custPersonDate.getPsptId();
            if(!custName.equals(userName)){
                bizCodeResult = "2";
                bizDescResult = "用户姓名与系统信息不一致";
                setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                return result;
            }

            if(!idNum.equals(userPsptId)){
                bizCodeResult = "2";
                bizDescResult = "用户证件号码与系统信息不一致";
                setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
                return result;
            }
        }catch(Exception e){
            bizCodeResult = "2";
            bizDescResult="处理异常："+ExceptionUtils.getExceptionInfo(e);
            setUserInfoCheckReslutCache(serialNumber,bizCodeResult,bizDescResult);
            return result;
        }
        setUserInfoCheckReslutCache(serialNumber,"1","ok");
        return result;
    }

    private IData decodeByIboss(IData input) throws Exception {
    	//custName\idNum\servicePwd,调IBOSS解密
    
	    IData inParam = new DataMap();
	    IData decodeResults = new DataMap();
	      
	    IDataset reqParams = new DatasetList();
	    IData reqParam = new DataMap();
	    if(StringUtils.isNotEmpty(input.getString("CUSTOMER_NAME"))){
	    	reqParam.put("PARAM_NAME", "CUSTOMER_NAME");
	    	reqParam.put("PARAM_VALUE", input.getString("CUSTOMER_NAME"));
	    	reqParams.add(reqParam);
	    }
	    if(StringUtils.isNotEmpty(input.getString("CUST_NAME"))){
	    	reqParam.put("PARAM_NAME", "CUST_NAME");
	    	reqParam.put("PARAM_VALUE", input.getString("CUST_NAME"));
	    	reqParams.add(reqParam);
	    }
	    if(StringUtils.isNotEmpty(input.getString("ID_NUM"))){
	    	reqParam = new DataMap();
	    	reqParam.put("PARAM_NAME", "ID_NUM");
	    	reqParam.put("PARAM_VALUE", input.getString("ID_NUM"));
	    	reqParams.add(reqParam);
	    }
	    if(StringUtils.isNotEmpty(input.getString("PSPT_ID"))){
	    	reqParam = new DataMap();
	    	reqParam.put("PARAM_NAME", "PSPT_ID");
	    	reqParam.put("PARAM_VALUE", input.getString("PSPT_ID"));
	    	reqParams.add(reqParam);
	    }
	    if(StringUtils.isNotEmpty(input.getString("SERVICE_PWD"))){
	    	reqParam = new DataMap();
	    	reqParam.put("PARAM_NAME", "SERVICE_PWD");
	    	reqParam.put("PARAM_VALUE", input.getString("SERVICE_PWD"));
	    	reqParams.add(reqParam);
	    }
	    inParam.put("REQ_PARAM", reqParams);
	  	
	    //      inParam = {
		//      	    "KIND_ID": "BIP9A011_T9001012_0_0",
		//      	    "REQ_PARAM": [{
		//      	        "PARAM_NAME": "CUSTOMER_NAME",
		//      	        "PARAM_VALUE": "b42887a38882ce1e5b122292f91476cf704e131796c264bb"
		//      	    },
		//      	    {
		//      	        "PARAM_NAME": "ID_NUM",
		//      	        "PARAM_VALUE": "2bcbcdfcdbd5f850fdad587924d38146c144ab8f30adbfccea3341671d74a2e68de81b4d6eccf1f1"
		//      	    },
		//      	    {
		//      	        "PARAM_NAME": "SERVICE_PWD",
		//      	        "PARAM_VALUE": "b1152ffcd3da1e8ff64af9f1476d17b5752cdeeb766ceb52"
		//      	    }]
		//      	}
	  	inParam.put("KIND_ID", "BIP9A011_T9001012_0_0");// 接口标识
	  	log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>>>>解密前>>>inParam="+inParam);
		IDataset results = IBossCall.callHttpIBOSS9("IBOSS", inParam);
		if(IDataUtil.isEmpty(results))
	    {
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口报错.KIND_ID=BIP9A011_T9001012_0_0！");
	    }
	    IData tmpData = results.getData(0);
	  	log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>>>>解密后>>>tmpData="+tmpData);
	  	if(IDataUtil.isEmpty(tmpData)){
	  		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口解密失败.KIND_ID=BIP9A011_T9001012_0_0！");
	  	}
	  	if(!"0".equals(tmpData.getString("X_RESULTCODE"))){
	  		CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用IBOSS接口解密失败.KIND_ID=BIP9A011_T9001012_0_0！");
	  	}
	  	IDataset rspParams = tmpData.getDataset("RSP_PARAM");
	    log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>>>>解密后>>>rspParams="+rspParams);
	
	
	  	if(IDataUtil.isNotEmpty(rspParams)){
	  		for(Iterator<Object> iterator = rspParams.iterator(); iterator.hasNext();){
	            IData rspParam = (IData) iterator.next();
	            log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>>>>解密后>>>rspParam="+rspParam);
	            if(StringUtils.isNotEmpty(rspParam.getString("PARAM_NAME")) && StringUtils.isNotEmpty(rspParam.getString("PARAM_VALUE"))){
	                if("CUSTOMER_NAME".equals(rspParam.getString("PARAM_NAME"))){
	                	decodeResults.put("CUSTOMER_NAME", rspParam.getString("PARAM_VALUE"));
	                }else if("ID_NUM".equals(rspParam.getString("PARAM_NAME"))){
	                	decodeResults.put("ID_NUM", rspParam.getString("PARAM_VALUE"));
	                }else if("SERVICE_PWD".equals(rspParam.getString("PARAM_NAME"))){
	                	decodeResults.put("SERVICE_PWD", rspParam.getString("PARAM_VALUE"));
	                }else if("CUST_NAME".equals(rspParam.getString("PARAM_NAME"))){
	                	decodeResults.put("CUST_NAME", rspParam.getString("PARAM_VALUE"));
	                }else if("PSPT_ID".equals(rspParam.getString("PARAM_NAME"))){
	                	decodeResults.put("PSPT_ID", rspParam.getString("PARAM_VALUE"));
	                }
	            }
	        }
	  	}
	  	
	    log.debug(">>>>>>>>>>userInfoCheckRequest>>>>>>>>最后>>>decodeResults="+decodeResults);
	    
	    return decodeResults;
	}

	/**
     * 用户登记信息一致性校验结果通知（CIP00130）
     * @param input
     * @return
     * @throws Exception
     */
    public IData userInfoCheckResult(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        IDataset checkRequestcaches = getUserInfoCheckCache();
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>userInfoCheckResult>>>>>start>>>>>>>>>>>"+checkRequestcaches);
        }
        if(IDataUtil.isNotEmpty(checkRequestcaches)){
            for(Iterator<Object> iterator = checkRequestcaches.iterator(); iterator.hasNext();){
                IData checkRequestcache = (IData) iterator.next();
                List<Map<String, String>> checkRequest = new ArrayList(checkRequestcache.values());
                IData param = new DataMap((Map)checkRequest.get(0));
                String abilityCode = "HN_UNHQ_CIP00130";
                callAbilityPlatCommon( param,abilityCode);
                deleteUserInfoCheckCache(param.getString("msisdn"));
            }
        }
        return result;
    }

    /**
     * 用户更换eSIM设备请求（CIP00131）
     * 只保存请求数据。
     * @param input
     * @return
     * @throws Exception
     */
    public IData changeESIMDeviceRequest(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        String serialNumber ="";
        String orderTime = "";
        try{
            serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
            IDataUtil.chkParam(input, "CUSTOMER_NAME");
            IDataUtil.chkParam(input, "ID_NUM");
            IDataUtil.chkParam(input, "EID");
            IDataUtil.chkParam(input, "IMEI");
            orderTime = IDataUtil.chkParam(input, "BIZTYPE_TIME");
            
            /*
             * 对参数做解密
             */
            log.debug(">>>>>>>>>>responseFeedBack>>>>>>>>解密前>>>input="+input);

            IData decodeResult = decodeByIboss(input);
            input.put("CUSTOMER_NAME", decodeResult.getString("CUSTOMER_NAME"));
            input.put("ID_NUM", decodeResult.getString("ID_NUM"));
            log.debug(">>>>>>>>>>responseFeedBack>>>>>>>>解密后>>>input="+input);

        }catch(Exception e){
            result.put("BIZ_CODE", "2998");
            result.put("BIZ_DESC", "接收失败!"+ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO"));
            return result;
        }

        IDataset OrderPreinfos = OrderPreInfoQry.queryOrderPreInfoBySnPreType(serialNumber,CHGESIM_PRETYPE,"0","0");
        if(IDataUtil.isNotEmpty(OrderPreinfos)){
            result.put("BIZ_CODE", "4008");
            result.put("BIZ_DESC", "业务正在处理,重复的操作");
            return result;
        }

        IData preOrderData = new DataMap();
        String preId = SeqMgr.getOrderId();
        preOrderData.put("PRE_ID", preId);
        preOrderData.put("PRE_TYPE", CHGESIM_PRETYPE);
        preOrderData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(preId));
        preOrderData.put("ACCEPT_STATE", "0");
        preOrderData.put("REQUEST_ID", orderTime);
        preOrderData.put("START_DATE", SysDateMgr.getSysTime());
        preOrderData.put("END_DATE", SysDateMgr.addSecond(SysDateMgr.getSysTime(),300));
        preOrderData.put("TRADE_TYPE_CODE", "142");
        preOrderData.put("SERIAL_NUMBER", serialNumber);
        preOrderData.put("REPLY_STATE", "0");//0是初始化状态
        IDataset dataset = getSpiltSet(input.toString(), 3800);
        for (int i = 0; i < dataset.size(); i++) {
            preOrderData.put("ACCEPT_DATA" + (i + 1), dataset.get(i));
        }
        preOrderData.put("REMARK", "一号一终端用户更换eSIM设备请求");
        preOrderData.put("RSRV_STR1", input.getString("TRADE_STAFF_ID","IBOSS001"));
        preOrderData.put("RSRV_STR2", input.getString("TRADE_DEPART_ID","00001"));
        preOrderData.put("RSRV_STR3", input.getString("TRADE_CITY_CODE","0022"));
        preOrderData.put("RSRV_STR4", input.getString("TRADE_EPARCHY_CODE","0022"));
        Dao.insert("TF_B_ORDER_PRE", preOrderData, Route.getJourDb(Route.CONN_CRM_CG));

        return result;
    }

    /**
     * 用户更换eSIM设备结果通知（CIP00132）
     * 生成补换卡工单并返回处理结果
     * @param input
     * @return
     * @throws Exception
     */
    public IData changeESIMDeviceResult(IData input) throws Exception {
        IData result = new DataMap();
        result.put("BIZ_CODE", "0000");
        result.put("BIZ_DESC", "success");

        IDataset OrderPreinfos = OrderPreInfoQry.queryOrderPreInfoByPreType(CHGESIM_PRETYPE,"0");
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>changeESIMDeviceResult>>>>>start>>>>>>>>>>>"+OrderPreinfos);
        }
        if(IDataUtil.isNotEmpty(OrderPreinfos)){
            String abilityCode = "HN_UNHQ_CIP00132";
            for (int i = 0; i < OrderPreinfos.size(); i++){
                IData OrderPreinfo = OrderPreinfos.getData(i);
                StringBuilder acceptData = new StringBuilder();
                for (int j = 1; j < 4; j++) {
                    if(StringUtils.isNotBlank(OrderPreinfo.getString("ACCEPT_DATA"+j))){
                        acceptData = acceptData.append(OrderPreinfo.getString("ACCEPT_DATA"+j));
                    }
                }
                input.put("SERIAL_NUMBER",OrderPreinfo.getString("SERIAL_NUMBER"));

                String tradeStaffId = OrderPreinfo.getString("RSRV_STR1");
                String tradeDepartIda = OrderPreinfo.getString("RSRV_STR2");
                String tradeCityCode = OrderPreinfo.getString("RSRV_STR3");
                String tradeEparchyCode = OrderPreinfo.getString("RSRV_STR4");
                CSBizBean.getVisit().setInModeCode("6");
                CSBizBean.getVisit().setStaffEparchyCode(tradeEparchyCode);
                CSBizBean.getVisit().setStaffId(tradeStaffId);
                CSBizBean.getVisit().setDepartCode(tradeDepartIda);
                CSBizBean.getVisit().setDepartId(tradeDepartIda);
                CSBizBean.getVisit().setCityCode(tradeCityCode);

                IData noticeInfo = new DataMap();
                try{
                    IData inparam = new DataMap(acceptData.toString());
                    inparam.put("TRADE_CITY_CODE", tradeCityCode);
                    inparam.put("TRADE_TYPE_CODE", "142");
                    inparam.put("REMARK", CHGESIM_PRETYPE);

                    //结果通知信息
                    noticeInfo.put("msisdn", inparam.getString("SERIAL_NUMBER"));
                    noticeInfo.put("primarymsisdn", inparam.getString("PRIMARY_SERIAL_NUMBER",""));
                    noticeInfo.put("bizCode", "1");
                    noticeInfo.put("bizDesc", "success");
                    noticeInfo.put("eid", inparam.getString("EID"));
                    noticeInfo.put("biztypeTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                    noticeInfo.put("iccid", "898600MFSSYYGX1X2X3X4X5X6X7");//必填字段，给个默认值

                    //获取新卡数据信息
                    IData simSet = new DataMap();
                    try {
                        SimCardBean simCardBean = new SimCardBean();
                        simSet = simCardBean.verifyIMEI(inparam);
                        if(log.isDebugEnabled()){
                            log.debug(">>>>>>>>>>changeESIMDeviceResult>>>>>simSet>>>>>>>>>>>"+simSet);
                        }
                        noticeInfo.put("iccid", simSet.getString("ICC_ID"));
                    } catch (Exception e) {
                        result.put("BIZ_CODE", "2998");
                        result.put("BIZ_DESC", "获取新卡数据失败");
                        IData callData = new DataMap();
                        noticeInfo.put("bizCode", "2");
                        noticeInfo.put("bizDesc", "获取新卡数据失败");
                        try {
                            callData = callAbilityPlatCommon(noticeInfo,abilityCode);
                        }catch (Exception ex) {
                            log.error(ex);
//                            ex.printStackTrace();
                        }

                        String bizDesc = ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO");
                        if(bizDesc.length()>500){
                            bizDesc = bizDesc.substring(0,500);
                        }
                        callData.put("BIZ_CODE", "2998");
                        //终止TF_B_ORDER_PRE表的数据，修改accept_state为-1失败
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(inparam.getString("SERIAL_NUMBER"),CHGESIM_PRETYPE,"-1","补换esim设备处理结果失败,失败原因:"+bizDesc+",能开返回信息："+callData.getString("bizDesc"),"1","0");
                        continue;
                    }

                    inparam.put("IMSI", simSet.getString("IMSI"));
                    inparam.put("SIM_CARD_NO", simSet.getString("SIM_CARD_NO"));
                    inparam.put("KI", simSet.getString("KI") );
                    inparam.put("NEW_EID", inparam.getString("EID") );
                    inparam.put("NEW_IMEI", inparam.getString("IMEI") );
                    IDataset results = CSAppCall.call("SS.SimCardTrade.tradeReg", inparam);
                    IData callData = new DataMap();
                    if (IDataUtil.isNotEmpty(results) && StringUtils.isNotEmpty(results.first().getString("TRADE_ID"))){
                        try {
                            noticeInfo.put("bizCode", "1");
                            noticeInfo.put("bizDesc", "生成订单成功");
                            callData = callAbilityPlatCommon(noticeInfo,abilityCode);
                        }catch (Exception e3) {
                            log.error(e3);
//                            e3.printStackTrace();
                        }
                        callData.put("BIZ_CODE", "2998");
                        //终止TF_B_ORDER_PRE表的数据，修改accept_state为1成功
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(inparam.getString("SERIAL_NUMBER"),CHGESIM_PRETYPE,"1","更换ESIM设备成功,工单号:"+results.first().getString("TRADE_ID")+",能开返回信息："+callData.getString("bizDesc"),"1","0");
                    }else{
                        result.put("BIZ_CODE", "2998");
                        result.put("BIZ_DESC", "补换esim设备处理失败");
                        try {
                            noticeInfo.put("bizCode", "2");
                            noticeInfo.put("bizDesc", "生成订单失败");
                            callData = callAbilityPlatCommon(noticeInfo,abilityCode);
                        }catch (Exception e3) {
                            log.error(e3);
//                            e3.printStackTrace();
                        }

                        callData.put("BIZ_CODE", "2998");
                        //终止TF_B_ORDER_PRE表的数据，修改accept_state为-1失败
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(inparam.getString("SERIAL_NUMBER"),CHGESIM_PRETYPE,"-1","补换esim设备处理结果失败,失败原因未知,能开返回信息："+callData.getString("bizDesc"),"1","0");
                        result.put("BIZ_CODE", "9999");
                        result.put("BIZ_DESC", "补换esim设备处理结果失败,能开返回信息："+callData.getString("bizDesc"));
                        continue;
                    }
                }catch (Exception e) {
                    IData callData = new DataMap();
                    String bizDesc = ExceptionUtils.getExceptionInfo(e).getString("RESULT_INFO");
                    if(bizDesc.length()>500){
                        bizDesc = bizDesc.substring(0,500);
                    }
                    if(StringUtils.isNotBlank(bizDesc) && bizDesc.contains("业务受理中")){
                        result.put("BIZ_CODE", "9999");
                        result.put("BIZ_DESC", "补换esim设备处理结果失败,业务受理中");
                        return result;
                    }

                    try {
                        noticeInfo.put("bizCode", "2");
                        noticeInfo.put("bizDesc", "生成订单失败");
                        callData = callAbilityPlatCommon(noticeInfo,abilityCode);
                    } catch (Exception e2) {
                        log.error(e2);
//                        e2.printStackTrace();
                    }

                    callData.put("BIZ_CODE", "2998");
                    //终止TF_B_ORDER_PRE表的数据，修改accept_state为-1失败
                    try {
                        OrderPreInfoQry.updateOrderPreInfoBySnPreType(input.getString("SERIAL_NUMBER"),CHGESIM_PRETYPE,"-1","补换esim设备处理结果失败,失败原因:"+bizDesc+",能开返回信息："+callData.getString("bizDesc"),"1","0");
                    } catch (Exception e3) {
                        log.error(e3);
//                        e3.printStackTrace();
                    }
                    result.put("BIZ_CODE", "9999");
                    result.put("BIZ_DESC", "补换esim设备失败:"+bizDesc+";能开返回信息："+callData.getString("bizDesc"));
                    return result;
                }
            }
        }
        return result;
    }

    /**
     * 校验eSIM设备合法性请求（CIP00125）
     * @param input
     * @return
     * @throws Exception
     */
    public IData verifyESIM(IData input) throws Exception {
        IData result = new DataMap();
        String eid ="";
        String imei = "";
        if(StringUtils.isNotEmpty(input.getString("EID"))){
            eid = input.getString("EID");
        }else if(StringUtils.isNotEmpty(input.getString("NEW_EID"))){
            eid = input.getString("NEW_EID");
        }
        if(StringUtils.isNotEmpty(input.getString("IMEI"))){
            imei = input.getString("IMEI");
        }else if(StringUtils.isNotEmpty(input.getString("NEW_IMEI"))){
            imei = input.getString("NEW_IMEI");
        }

        IData param = new DataMap();
        param.put("eid", eid);
        param.put("imei", imei);
        param.put("orderTime", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        String abilityCode = "crm.ABILITY.CIP125";
        IData verifyESIMRequst = new DataMap();
        try {
            if(log.isDebugEnabled()){
                log.debug(">>>>>>>>>>verifyESIM>>>>>start>>>>>>>>>>>"+param);
            }
            verifyESIMRequst = callAbilityPlatCommon(param,abilityCode);
        } catch (Exception e) {
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULT_INFO", "校验eSIM设备合法性请求失败！ "+e.getMessage());
            return result;
        }
        log.debug(">>>>>>>>>>>>verifyIMEI>>>>>>>>>verifyESIMRequst>>>"+verifyESIMRequst);
        if(IDataUtil.isNotEmpty(verifyESIMRequst)){
            if(!"0000".equals(verifyESIMRequst.getData("result").getString("bizCode"))){
                result.put("X_RESULTCODE", "-1");
                result.put("X_RESULT_INFO","校验eSIM设备合法性请求,一级能开返回失败！ "+verifyESIMRequst.getData("result").getString("bizDesc"));
                return result;
            }
            setOneNumOneDeviceCache(eid,getVisit().getStaffId());
        }else{
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULT_INFO","校验eSIM设备合法性请求,一级能开返回信息为空！ "+verifyESIMRequst);
            return result;
        }

        result.put("X_RESULTCODE", "0");
        result.put("X_RESULT_INFO", "ok");
        return result;
    }

    /**
     * 前台检索eSIM设备合法性校验结果,如果校验通过，调资源接口获取新sim卡数据；
     * @param input
     * @return
     * @throws Exception
     */
    public IData queryCheckResult(IData input) throws Exception {
        IData result = new DataMap();
        if(log.isDebugEnabled()){
            log.debug(">>>>>>>>>>queryCheckResult>>>>>start>>>>>>>>>>>"+input);
        }
//        String eid = input.getString("NEW_EID");
        String eid ="";
        if(StringUtils.isNotEmpty(input.getString("EID"))){
            eid = input.getString("EID");
        }else if(StringUtils.isNotEmpty(input.getString("NEW_EID"))){
            eid = input.getString("NEW_EID");
        }

        IData cache = getOneNumOneDeviceCache(eid);
        if(IDataUtil.isEmpty(cache)){
            result.put("X_RESULTCODE", "1");
            result.put("X_RESULT_INFO", "未找到此设备的校验请求信息,请重新校验");
            return result;
        }

        String bizCode = cache.getString("BIZ_CODE");
        if(StringUtils.isEmpty(bizCode)){
            result.put("X_RESULTCODE", "1");
            result.put("X_RESULT_INFO", "未找到此设备的校验结果信息,请稍后再试");
            return result;
        }

        if(!"1".equals(bizCode)){
            result.put("X_RESULTCODE", "-1");
            result.put("X_RESULT_INFO", "设备校验未通过,失败原因："+cache.getString("BIZ_DESC"));
            return result;
        }

        if(!"eSIM".equals(input.getString("IN_TYPE",""))){
            //获取新卡数据、卡费、VIP等信息
            IData simSet = new DataMap();
            try {
                SimCardBean simCardBean = new SimCardBean();
                simSet = simCardBean.verifyIMEI(input);
                if(log.isDebugEnabled()){
                    log.debug(">>>>>>>>>>queryCheckResult>>>>>simSet>>>>>>>>>>>"+simSet);
                }
            } catch (Exception e) {
                result.put("X_RESULTCODE", "-1");
                result.put("X_RESULT_INFO", "从资源获取新ESIM卡数据信息失败！ "+e.getMessage());
                return result;
            }

            result = simSet;
        }
        result.put("X_RESULTCODE", "0");
        result.put("X_RESULT_INFO", "ok");
        return result;
    }

    /**
     * 准备profile请求（CIP00127）
     * @param input
     * @return
     * @throws Exception
     */
    public IData prepareProfileRequest(IData input) throws Exception {
        IDataUtil.chkParam(input, "MSISDN");
        IDataUtil.chkParam(input, "DEVICE_TYPE");
        IDataUtil.chkParam(input, "EID");
        IDataUtil.chkParam(input, "IMEI");
        IDataUtil.chkParam(input, "ICCID1");
        IDataUtil.chkParam(input, "BIZ_TYPE");
        IDataUtil.chkParam(input, "BIZ_TYPE_TIME");
        IData result = new DataMap();
        try {
            callAbilityPlatCommon(input,"HN_UNHT_CIP00127");
        }catch (Exception e){
            result.put("BIZ_CODE","9999");
            result.put("BIZ_DESC",e.getMessage());
            return result;
        }
        result.put("BIZ_CODE","0000");
        result.put("BIZ_DESC","success");
        return result;
    }
}
