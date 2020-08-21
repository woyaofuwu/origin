
package com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.action.trade;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.common.util.IdcardUtils;
import com.asiainfo.veris.crm.order.soa.person.common.util.TradeUtils;

/**
 * 必须在指定的年龄区间才可以办理指定优惠
 * @author tanzheng
 *
 */
public class LimitAgeNotAllowChangePsptAction implements ITradeAction
{
	private static final Logger log = Logger.getLogger(LimitAgeNotAllowChangePsptAction.class);
    @Override
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	if(TradeUtils.getChangePsptId(btd)){
    		//有变化则查看用户使用有办理指定套餐
    		List<DiscntTradeData> lsDiscnt = btd.getRD().getUca().getUserDiscnts();
    		for(DiscntTradeData data:lsDiscnt){
    			IDataset discntds=CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "5212", "DISCNT_CODE_AGE", data.getDiscntCode());
                if(IDataUtil.isNotEmpty(discntds)){
           		 String psptId=TradeUtils.getChangeNewPsptId(btd);
           		 String psptTypeCode=TradeUtils.getChangeNewPsptType(btd);
           		 //要求年龄段
           		 IData discntd=discntds.getData(0);
           		 int fromAge=discntd.getInt("PARA_CODE3", 0);
           		 int toAge=discntd.getInt("PARA_CODE4", 0);
           		 String productName = discntd.getString("PARA_CODE2");
           		 if("0".equals(psptTypeCode)||"1".equals(psptTypeCode)||"2".equals(psptTypeCode)){
           			 //身份证  户口本
           			 if(StringUtils.isNotBlank(psptId)){
           				 int age=IdcardUtils.getAgeByIdCard(psptId);
           				 if(!(fromAge <= age && age<= toAge)){
           					 //办理客户年龄段（14-22周岁方可办理）
           					 String discntName = UDiscntInfoQry.getDiscntNameByDiscntCode(data.getDiscntCode());
           					 CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户办理了[" + data.getDiscntCode() + "]" + discntName + "，要求年龄必须在("+fromAge+"-"+toAge+"周岁)");
           				 }
           			 }else{
                            CSAppException.apperr(CrmCommException.CRM_COMM_888, "客户资料有问题"+"！");
           			 }
           		 }else{
					 CSAppException.apperr(CrmCommException.CRM_COMM_888, "【"+productName+"】只能使用证件类型为【身份证、户口本】做激活！");
				 }
                	
                	
                }
    		}
    	}
    } 
}
