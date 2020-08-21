package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.order.action.reg;

import java.util.List;

import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;



/**
 * REQ201805240014_考生关爱流量不限量18元可选套餐的开发需求
 * @author zhuoyingzi
 * @date 20180525
 *
 */
public class CheckDiscn18Action implements ITradeAction
{

    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
//        //log.info("("CheckDiscn2Actionxxxxxxxxxxxxxxxxxx29 " + btd);

        List<DiscntTradeData> discntTrades = btd.getTradeDatas(TradeTableEnum.TRADE_DISCNT);//获取优惠子台帐

        if (discntTrades != null && discntTrades.size() > 0) {
            	check(discntTrades,btd);
        }
    }

    private void check(List<DiscntTradeData> discntTrades,BusiTradeData btd) throws Exception
    {
        for (DiscntTradeData discntTrade : discntTrades) {
            if (BofConst.MODIFY_TAG_ADD.equals(discntTrade.getModifyTag())) {
                String discntCode = discntTrade.getDiscntCode();
                if (!"".equals(discntCode)&&discntCode!=null) {
                    
                    IDataset discntds=CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "5212", "DISCNT_CODE_AGE", discntCode);
                    if(IDataUtil.isNotEmpty(discntds)){
                    	
                    	String tradeTypeCode = btd.getTradeTypeCode();
                    	//System.out.println("-------------CheckDiscn18Action-------------------"+tradeTypeCode);
                    	if("10".equals(tradeTypeCode)||"40".equals(tradeTypeCode))
                    	{
                    		List<CustomerTradeData> customerInfos = btd.get("TF_B_TRADE_CUSTOMER");
                    		if (customerInfos != null && customerInfos.size() > 0) {
                    		String psptId = customerInfos.get(0).getPsptId();
                    		String psptTypeCode=customerInfos.get(0).getPsptTypeCode();
                        	//System.out.println("-------------CheckDiscn18Action-------------------psptId:"+psptId+",psptTypeCode:"+psptTypeCode);

	                   		 //要求年龄段
	                   		 IData discntd=discntds.getData(0);
	                   		 int fromAge=discntd.getInt("PARA_CODE3", 0);
	                   		 int toAge=discntd.getInt("PARA_CODE4", 0);
	                        	//System.out.println("-------------CheckDiscn18Action-------------------fromAge:"+fromAge+",toAge:"+toAge);

	                   		 if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
	                   			 //身份证  户口本
	                   			 if(!"".equals(psptId)&& psptId!=null){
	                   				 int age=IdcardUtils.getAgeByIdCard(psptId);
	                   				 if(fromAge <= age && age<= toAge){
	                   					 //客户满足办理条件,可以办理
	                   				 }
	                   				 else{
	                   					 //办理客户年龄段（14-22周岁方可办理）
	                                        String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
	                                        CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户年龄必须在("+fromAge+"-"+toAge+"周岁)才能办理[" + discntCode + "]" + discntName + "！");
	                   				 }
	                   			 }else{
	                                    CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户资料有问题"+"！");
	                   			 }
	                   		 }else{
	                                CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户不满足,"+discntd.getString("PARA_CODE2")+",业务办理"+"！");
	                   		 }
                    		}
                    	}
                    	else{
	                    	 //存在办理    流量不限量18元可选套餐  优惠
	                    	IData customerInfo=UcaInfoQry.qryCustomerInfoByCustId(btd.getRD().getUca().getCustId(),btd.getRoute());
	                    	if(IDataUtil.isNotEmpty(customerInfo)){
	                    		 String psptId=customerInfo.getString("PSPT_ID", "");
	                    		 String psptTypeCode=customerInfo.getString("PSPT_TYPE_CODE", "");
	                    		 //要求年龄段
	                    		 IData discntd=discntds.getData(0);
	                    		 int fromAge=discntd.getInt("PARA_CODE3", 0);
	                    		 int toAge=discntd.getInt("PARA_CODE4", 0);
	                    		 
	                    		 if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
	                    			 //身份证  户口本
	                    			 if(!"".equals(psptId)&& psptId!=null){
	                    				 int age=IdcardUtils.getAgeByIdCard(psptId);
	                    				 if(fromAge <= age && age<= toAge){
	                    					 //客户满足办理条件,可以办理
	                    				 }else{
	                    					 //办理客户年龄段（14-22周岁方可办理）
	                                         String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(discntCode);
	                                         CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户年龄必须在("+fromAge+"-"+toAge+"周岁)才能办理[" + discntCode + "]" + discntName + "！");
	                    				 }
	                    			 }else{
	                                     CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户资料有问题"+"！");
	                    			 }
	                    		 }else
								 {
	                                 CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户不满足,"+discntd.getString("PARA_CODE2")+",业务办理"+"！");
	                    		 }
	                    	}
                    	}
                    }
                }
            }
        }
    }
}
