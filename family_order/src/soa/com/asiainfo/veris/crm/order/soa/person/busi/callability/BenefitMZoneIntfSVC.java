package com.asiainfo.veris.crm.order.soa.person.busi.callability;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.auth.AbilityEncrypting;
import com.asiainfo.veris.crm.order.soa.person.busi.benefitcenter.BenefitCenterBean;

/**
 * 动感地带联名号卡产品的配置需求
 *
 * @author wangsc10
 * @version V1.0
 * @date 2020/5/13 9:23
 */
public class BenefitMZoneIntfSVC extends CSBizService {
	public static Logger logger = Logger.getLogger(BenefitMZoneIntfSVC.class);
	/**
     * 权益申请接口
     * 动感地带联名号卡产品的配置需求
     * by wangsc10
     * @param input
     * @return
     * @throws Exception
     */
    public static IData benefitMZone(IData input) throws Exception {
    	logger.debug("================BenefitMZoneIntfSVC.BenefitMZoneIntfSVC  input:"+input);
        IData result = new DataMap();
        result.put("X_RESULTCODE", "0000");
        result.put("X_RESULTINFO", "成功");
        String serialNumber = IDataUtil.chkParam(input, "SERIAL_NUMBER");
        String productId =IDataUtil.chkParam(input, "PRODUCT_ID");
        String custName =input.getString("CUST_NAME","");
        String userId =IDataUtil.chkParam(input, "USER_ID");
        IDataset commparaInfos = CommparaInfoQry.getCommNetInfo("CSM", "2579", productId);//根据主产品编码查询td_s_commpara的能开编码
        if(IDataUtil.isNotEmpty(commparaInfos)){
        	IDataset userInfos = UserInfoQry.getUserInfoBySn(serialNumber, "0");
    		logger.debug("================BenefitMZoneIntfSVC.BenefitMZoneIntfSVC  userInfos:"+userInfos);
	    	if(IDataUtil.isNotEmpty(userInfos)){
	    		if(custName.equals("")){
	    			String custid = userInfos.first().getString("CUST_ID","");
		    		IData custinfo = UcaInfoQry.qryCustomerInfoByCustId(custid);
		    		if(IDataUtil.isNotEmpty(custinfo)) {
		    			custName= custinfo.getString("CUST_NAME","");
			 		}
	    		}
    			//拼接能开参数
    			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    			Date date = new Date();
    			IData abilityData = new DataMap();
    			String dateStr = sdf.format(date);
    			String tradeId = SeqMgr.getTradeIdFromDb();
    			String orderId = "QY"+dateStr+tradeId;//订单号
    			
    			if(orderId.length() > 32){
    				orderId = orderId.substring(0,32);//若订单号大于32位，则截取前32位
    			}else if(orderId.length() < 32){//若订单号小于32位，则补足32位
    				for(int i = orderId.length(); i < 32; i++){
    					if(i == 31){
    						orderId = orderId+"1";
    					}else{
    						orderId = orderId+"0";
    					}
    				}
    			}
    			
    			abilityData.put("orderId", orderId);//订单编码
    			abilityData.put("createTime", dateStr);//订单创建时间
    			abilityData.put("buyerNickname", custName);//买家名称
    			
    			IData paymentInfo =  new DataMap();//支付信息
    			paymentInfo.put("chargeType", "1");//扣费类型  0：统一支付扣费；1：话费扣费; 2：第三方扣费；3：统一支付话费充值；4：线下支付；5：积分支付6：能力平台支付
    			paymentInfo.put("payment", 0);//实际支付金额 单位：分
    			paymentInfo.put("paymentTime",dateStr);//支付时间
    			abilityData.put("paymentInfo", paymentInfo);
    			
    			abilityData.put("needDistribution", "2");//是否需配送 1-需要；2-不需要
    			abilityData.put("name",custName);//收货人姓名
    			abilityData.put("province","");//收货人所在省份
    			abilityData.put("city","");//收货人所在市
    			abilityData.put("district","");//收货人所在地区
    			abilityData.put("address","");//收货人的详细地址
    			abilityData.put("mobilephone",serialNumber);//收货人手机号码
    			abilityData.put("needInvoice","2");//是否需要开发票  1-需要；2-不需要
    			
    			IDataset subOrderList = new DatasetList();//子订单集合
    			IData subOrderData = new DataMap();
    			String subTradeId = SeqMgr.getTradeIdFromDb();
    			String subOrderId = "QY"+dateStr+subTradeId;//子订单号
    			
    			
    			if(subOrderId.length() > 32){
    				subOrderId = subOrderId.substring(0,32);//若子订单号大于32位，则截取前32位
    			}else if(subOrderId.length() < 32){//若子订单号小于32位，则补足32位
    				for(int j = subOrderId.length(); j < 32; j++){
    					if(j == 31){
    						subOrderId = subOrderId+"1";
    					}else{
    						subOrderId = subOrderId+"0";
    					}
    				}
    			}
    			subOrderData.put("subOrderId", subOrderId);//子订单编码
				
				IData subscriberInfo = new DataMap();//用户号码信息
				subscriberInfo.put("numberOprType", "12");//业务号码操作类型  12：赠送类权益订购（即权益领取）
				subscriberInfo.put("number", serialNumber);//业务号码
				subscriberInfo.put("numberType", "1");//业务号码类型  1- 手机号；2- 宽带号码
				subOrderData.put("subscriberInfo", subscriberInfo);
				
				IData commparaInfo = commparaInfos.first();
				IData goodsInfo = new DataMap();//订购商品信息
				goodsInfo.put("goodsId", commparaInfo.getString("PARA_CODE3"));//商品编码
				goodsInfo.put("goodsTitle", commparaInfo.getString("PARA_CODE1"));//商品标题
				goodsInfo.put("amount", 1);//购买数量
				goodsInfo.put("corGoodsId", commparaInfo.getString("PARA_CODE2"));//关联商品编码
				goodsInfo.put("price", 0);//商品价格  单位：分
				goodsInfo.put("goodsProvince", "海南");//商品所在省份
				goodsInfo.put("goodsCity", "海南");//商品所在城市
				subOrderData.put("goodsInfo", goodsInfo);
				
				subOrderData.put("subtotalFee", 0);//子订单总金额  单位：分
				subOrderData.put("adjustFee", 0);//手工调整金额  单位：分
				subOrderData.put("orderStatus", "00");//订单状态  00 初始状态
				
				subOrderList.add(subOrderData);
    			
    			abilityData.put("subOrderList",subOrderList);
    			logger.debug("BenefitMZoneIntfSVC.BenefitMZoneIntfSVC abilityData = " + abilityData);
    			
    			if(IDataUtil.isNotEmpty(subOrderList)){
    				
    				//调用能开接口
	    			IData retData = callAbilityCIP00080(abilityData);
	    			
	    			if (IDataUtil.isNotEmpty(retData)) {
	    	            String resCode = retData.getString("resCode");
	    	            String resMsg = retData.getString("resMsg");
	    	            IData out = retData.getData("result");
	    	            String bizCode = out.getString("bizCode");
	    	            String bizDesc = out.getString("bizDesc");
	    	            if ("00000".equals(resCode)) {
	    	                if (!"0000".equals(bizCode)) {
	    	                	logger.error("调用能开失败 ，原因："+bizDesc);
	    	                	logger.error("调用能开参数：" + abilityData.toString());
	    	                    logger.error("调用能开返回结果：" + retData.toString());
	    	                    
	    	                    result.put("X_RESULTCODE", "0007");
	                            result.put("X_RESULTINFO", "调用能开失败 ，原因："+bizDesc);
	                            return result;
	    	                } else {
	    	                	 // 调用成功 
	    	                	logger.debug("调用能开成功");
	    	                	logger.debug("调用能开参数：" + abilityData.toString());
	    	                    logger.debug("调用能开返回结果：" + retData.toString());
	    	                    BenefitCenterBean bean = BeanManager.createBean(BenefitCenterBean.class);
	    	                    //入库
	    	                    for (int i = 0; i < subOrderList.size(); i++) {
	    	                    	IData subOrderData1 = subOrderList.getData(i);
	    	                    	String subOrderId1 = subOrderData1.getString("subOrderId");
	    	                    	String discntCode = productId.equals("84019040") ? "84019051" : "84019052";
	    	                    	String goodsId = subOrderData1.getData("goodsInfo").getString("goodsId");
	    	                    	String goodsTitle = subOrderData1.getData("goodsInfo").getString("goodsTitle");
	    	                    	bean.insertRightsInterests(orderId,subOrderId1,userId,serialNumber,discntCode,goodsId,goodsTitle);
								}
	    	                   
	    	                }
	    	            } else {
	    	            	logger.error("调用能开失败 ，原因："+resMsg);
	    	                logger.error("调用能开参数：" + abilityData.toString());
	    	                logger.error("调用能开返回结果：" + retData.toString());
	    	                
	    	                result.put("X_RESULTCODE", "0007");
                            result.put("X_RESULTINFO", "调用能开失败 ，原因："+resMsg);
                            return result;
	    	            }
	    	        } 
    			}
	    	}else{
	    		result.put("X_RESULTCODE", "0003");
                result.put("X_RESULTINFO", "未找到用户信息");
                return result;
	    	}
        }
        return result;
    }
    
    /**
     * 用能力开放平台CIP00080接口
     * by chenyw7
     * @param input
     */
    private static IData callAbilityCIP00080(IData abilityData)  throws Exception {
        IData retData = new DataMap();
        String Abilityurl = "";
        IData param1 = new DataMap();
        param1.put("PARAM_NAME", "crm.ABILITY.CIP00080");
        StringBuilder getInterFaceSQL = new StringBuilder().append("select t.* FROM td_s_bizenv t where t.param_name = :PARAM_NAME and t.state= 'U' ");
        IDataset Abilityurls;
		Abilityurls = Dao.qryBySql(getInterFaceSQL, param1, "cen");
		if (Abilityurls != null && Abilityurls.size() > 0) {
            Abilityurl = Abilityurls.getData(0).getString("PARAM_VALUE", "");
        } else {
            CSAppException.appError("-1", "crm.ABILITY.CIP00080接口地址未在TD_S_BIZENV表中配置");
        }
        String apiAddress = Abilityurl;
        retData = AbilityEncrypting.callAbilityPlatCommon(apiAddress, abilityData);
    	logger.debug("BenefitCenterIntfSVC.callAbilityCIP00080 retData = " + retData);
		return retData;
        
    }
}

