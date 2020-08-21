package com.asiainfo.veris.crm.order.soa.person.busi.imslandline;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.SaleTerminalLimitInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeWideNetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleGoodsInfoQry;


public class IMSLandLineIntfSVC extends CSBizService
{
	
	private static final long serialVersionUID = 1L;


    public IData checkAuthSerialNum(IData input) throws Exception
    {
    	IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String serialNumber = input.getString("SERIAL_NUMBER");
        String wSerialNumber = "KD_" + serialNumber;
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        String userId = userInfo.getString("USER_ID");
        

        // 是否有宽带在途工单
        IDataset wideInfos = TradeInfoQry.queryExistWideTrade(wSerialNumber);
        String wideState = "0"; // 0-系统异常
        if (IDataUtil.isEmpty(wideInfos))
        {
            // .1是否办理过宽带
            IData wUserInfo = UcaInfoQry.qryUserInfoBySn(wSerialNumber);
            if (IDataUtil.isEmpty(wUserInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_162); // 用户没有开通宽带, 不能办理该业务
            }
            
            // .2用户宽带FTTH类型宽带
            IData wideNetInfo = WidenetInfoQry.getUserWidenetInfoBySerialNumber(wSerialNumber).first();
            
            if (IDataUtil.isEmpty(wideNetInfo))
            {
                CSAppException.appError("-1", "该用户宽带资料信息不存在！");
            }
            
            if(!(StringUtils.equals("3", wideNetInfo.getString("RSRV_STR2", "")) || StringUtils.equals("5", wideNetInfo.getString("RSRV_STR2", ""))))
            {
            	CSAppException.apperr(CrmUserException.CRM_USER_783,"用户宽带非FTTH类型宽带");
            }
            
            // .3用户是否已经办理家庭IMS固话
            IDataset uuInfo = RelaUUInfoQry.getRelationUUInfoByDeputySn(userId, "MS",null);
            
            if (IDataUtil.isNotEmpty(uuInfo))
            {
                CSAppException.appError("-1", "该用户已经办理过家庭IMS固话业务！");
            }
            
            IDataset userMainProducts = UserProductInfoQry.queryUserMainProduct(wideNetInfo.getString("USER_ID"));
            if(IDataUtil.isNotEmpty(userMainProducts))
            {
            	IData userProduct = userMainProducts.getData(0);
            	String userProductId = userProduct.getString("PRODUCT_ID");
            	String userProductName = UProductInfoQry.getProductNameByProductId(userProductId);
            	userInfo.put("WIDE_PRODUCT_NAME", userProductName);
            }
            
            
            wideState = "2"; // 2-正常
            userInfo.put("WIDE_START_DATE", wideNetInfo.getString("START_DATE"));
            userInfo.put("WIDE_END_DATE", wideNetInfo.getString("END_DATE"));
            userInfo.put("WIDE_USER_ID", wUserInfo.getString("USER_ID"));
            userInfo.put("WIDE_ADDRESS", wideNetInfo.getString("DETAIL_ADDRESS")); // 宽带安装地址
            userInfo.put("RSRV_STR4", wideNetInfo.getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
        }
        else
        {
            wideState = "1"; // 1-未完工
            IData wideTD = wideInfos.getData(0);
            userInfo.put("WIDE_TRADE_ID", wideTD.getString("TRADE_ID"));
            userInfo.put("WIDE_USER_ID", wideTD.getString("USER_ID"));
            userInfo.put("WIDE_START_DATE", "--");
            userInfo.put("WIDE_END_DATE", "--");
            IDataset addrTD = TradeWideNetInfoQry.queryTradeWideNet(wideTD.getString("TRADE_ID"));
            
            if (IDataUtil.isNotEmpty(addrTD))
            {
                userInfo.put("WIDE_ADDRESS", addrTD.first().getString("DETAIL_ADDRESS"));
                userInfo.put("RSRV_STR4", addrTD.first().getString("RSRV_STR4")); //给PBOSS自动预约派单与回单用
            }
          
        }
        
        // 设置宽带状态
        userInfo.put("WIDE_STATE", wideState);
        userInfo.put("WIDE_STATE_NAME", "2".equals(wideState) ? "正常" : "1".equals(wideState) ? "未完工" : "异常");
        
        if("2".equals(wideState))
        {
        	userInfo.put("RESULT_CODE", "1");
        }else{
        	userInfo.put("RESULT_CODE", "0");
        	userInfo.put("RESULT_INFO","宽带未完工！");
        	CSAppException.apperr(CrmUserException.CRM_USER_163,"宽带未完工");
        }
  	  
      	return userInfo;
    }
    
    public IDataset searchResNum(IData param)throws Exception{
    	IDataUtil.chkParam(param, "RSRV_STR4");
    	String city_code = param.getString("RSRV_STR4");
    	IDataset callset=ResCall.getTenImsiPhoneByCityCode("0", "0G", "0", city_code);
    	return callset;
    }
    
    
    public IData checkTopSetBoxTerminal(IData input) throws Exception
    {
    	//
    	setVisitTradeStaff(input);

        IData retData = new DataMap();
        String resNo = input.getString("RES_ID");
        
        String tradeId = input.getString("TRADE_ID");
        
        if (StringUtils.isBlank(resNo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "家庭IMS固话终端编码为空!");
        }
        
        if (StringUtils.isBlank(tradeId))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "TRADE_ID参数为空!");
        }
        
        IDataset topSetBoxInfos = TradeInfoQry.getTradeAndBHTradeByTradeId(tradeId);
        
        if (IDataUtil.isNotEmpty(topSetBoxInfos))
        {
            IData topSetBoxInfo = topSetBoxInfos.first();
            
            //扩展字段1存放的手机号码
            String serialNumber = topSetBoxInfo.getString("RSRV_STR1");
            String productId = topSetBoxInfo.getString("RSRV_STR9","");
            String packageId = topSetBoxInfo.getString("RSRV_STR10","");
            
            IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(resNo, "", serialNumber, "");
            
            //调用华为接口进行终端查询校验
            IDataset retDataset = HwTerminalCall.querySetTopBox(serialNumber, resNo);
            
            if (DataSetUtils.isNotBlank(retDataset) && StringUtils.equals(retDataset.first().getString("X_RESULTCODE"), "0"))
            {

                IData res = retDataset.first();
                IData terminalLimit = SaleTerminalLimitInfoQry.queryByPK(productId, packageId, "0", res.getString("DEVICE_MODEL_CODE", ""), "0898");
                if (IDataUtil.isEmpty(terminalLimit))
                {
                    CSAppException.apperr(CrmCommException.CRM_COMM_103, "输入的终端串码无法匹配活动!");
                }
                
                IData userdata =  UcaInfoQry.qryUserInfoBySn(serialNumber);
                if(IDataUtil.isNotEmpty(userdata))
                {
                	IData param = new DataMap();
                	param.put("USER_ID", userdata.getString("USER_ID"));
                	param.put("SERIAL_NUMBER_B", "");
                	param.put("PRODUCT_ID", productId);
                	param.put("PACKAGE_ID", packageId);
                	param.put("INST_ID", SeqMgr.getInstId());
                	param.put("CAMPN_ID", "");
                	
                	String goodsID = "99032303";
                	IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", "1887", packageId, "0898");
                	if(IDataUtil.isNotEmpty(commparaInfos))
                	{
                		goodsID = commparaInfos.getData(0).getString("PARA_CODE1", "");
                	}
                	
                	param.put("GOODS_ID", goodsID);
            		IDataset returnResult = UpcCall.qryOffersByOfferTypeLikeOfferName("G", goodsID, "");
            		if(IDataUtil.isNotEmpty(returnResult))
            		{
            			param.put("GOODS_NAME", returnResult.getData(0).getString("OFFER_NAME"));
            		}else
            		{
            			param.put("GOODS_NAME", "家庭IMS固话预存送机");
            		}               	
                	param.put("GOODS_NUM", "1");
                	param.put("GOODS_VALUE", "0");
                	param.put("GOODS_STATE", "0");
                	param.put("RES_TAG", "1");
                	param.put("RES_TYPE_CODE", "4");
                	param.put("RES_ID", "");
                	param.put("RES_CODE", resNo);
                	param.put("DEVICE_MODEL_CODE", res.getString("DEVICE_MODEL_CODE", ""));
                	param.put("DEVICE_MODEL", res.getString("DEVICE_MODEL", ""));
                	param.put("DEVICE_COST", res.getString("DEVICE_COST","0"));
                	param.put("DEVICE_BRAND_CODE", res.getString("DEVICE_BRAND_CODE"));
                	param.put("DEVICE_BRAND", res.getString("DEVICE_BRAND"));
                	param.put("DESTROY_FLAG", "0");
                	param.put("GIFT_MODE", "0");
                	param.put("POST_NAME", "");
                	param.put("POST_ADDRESS", "");
                	param.put("POST_CODE", "");
                	
                	//获取预受理的230工单
                    IDataset tradeH = TradeInfoQry.getHisMainTradeByOrderId(topSetBoxInfo.getString("ORDER_ID"), "0", "0898");
                    if(IDataUtil.isNotEmpty(tradeH))
                    {
                    	param.put("RELATION_TRADE_ID", tradeH.getData(0).getString("TRADE_ID"));
                    }else{
                    	param.put("RELATION_TRADE_ID", tradeId);
                    }
                	
                	param.put("ACCEPT_DATE", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                	param.put("CANCEL_DATE", "2050-12-31 23:59:59");
                	param.put("UPDATE_TIME", SysDateMgr.getSysDateYYYYMMDDHHMMSS());
                	param.put("UPDATE_STAFF_ID", input.getString("TRADE_STAFF_ID", "SUPERUSR"));
                	param.put("UPDATE_DEPART_ID", input.getString("TRADE_DEPART_ID", "36601"));
                	param.put("REMARK", "家庭IMS固话出库");
                	param.put("RSRV_NUM1", "1");
                	param.put("RSRV_NUM2", "0");
                	param.put("RSRV_NUM3", "0");
                	param.put("RSRV_NUM4", "");
                	param.put("RSRV_NUM5", "");
                	param.put("RSRV_STR1", res.getString("SUPPLY_COOP_ID", ""));
                	param.put("RSRV_STR2", "");
                	param.put("RSRV_STR3", "");
                	param.put("RSRV_STR4", "");
                	param.put("RSRV_STR5", "");
                	param.put("RSRV_STR6", res.getString("RSRV_STR6", "0"));
                	param.put("RSRV_STR7", "0");
                	param.put("RSRV_STR8", "");
                	param.put("RSRV_STR9", res.getString("TERMINAL_TYPE_CODE", ""));
                	param.put("RSRV_STR10", "YX02");
                	param.put("RSRV_DATE1", "");
                	param.put("RSRV_DATE2", "");
                	param.put("RSRV_DATE3", "");
                	param.put("RSRV_TAG1", "");
                	param.put("RSRV_TAG2", "");
                	param.put("RSRV_TAG3", "");
                	
                	UserSaleGoodsInfoQry.insertIMSTopsetboxOnline(param);
                }
                
                retData.put("X_RESULTCODE", "0");
                retData.put("X_RESULTINFO", res.getString("X_RESULTINFO", ""));
            }
            else
            {
                String resultInfo = retDataset.first().getString("X_RESULTINFO", "华为接口调用异常！");
                CSAppException.apperr(CrmCommException.CRM_COMM_103, resultInfo); // 接口返回异常
            }
        }
        else
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据TRADE_ID查询不到家庭IMS固话业务受理信息!");
        }
        
        return retData;
    }
    
    public void setVisitTradeStaff(IData input) throws Exception
    {
    	//获取接口传入的交易信息
    	String tradeStaffId = input.getString("TRADE_STAFF_ID", "");
    	String tradeDepartId = input.getString("TRADE_DEPART_ID", "");
    	String tradeCityCode = input.getString("TRADE_CITY_CODE", "");
    	
    	if(tradeStaffId != null && !"".equals(tradeStaffId))
    		CSBizBean.getVisit().setStaffId(tradeStaffId);
    	if(tradeDepartId != null && !"".equals(tradeDepartId))
    		CSBizBean.getVisit().setDepartId(tradeDepartId);
    	if(tradeCityCode != null && !"".equals(tradeCityCode))
    		CSBizBean.getVisit().setCityCode(tradeCityCode);
    }
    
    
    /**
     * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
     * mengqx 20190916
     * 给新大陆、电渠提供校验家庭IMS固话证件号码和姓名是否与对应的手机号码的证件号码、姓名一致的接口
     * 接口入参：手机号码、证件号码、姓名
     * 接口出参：0 表示证件号码和姓名与手机的一致 ，-1 表证件号码和姓名与手机的不一致
     */
    public IData checkIMSPhoneCustInfo(IData input) throws Exception
    {
    	IMSLandLineBean bean = BeanManager.createBean(IMSLandLineBean.class);
    	return bean.checkIMSPhoneCustInfo(input);
    }
    
}
