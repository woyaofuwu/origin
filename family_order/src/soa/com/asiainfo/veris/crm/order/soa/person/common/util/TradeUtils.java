
package com.asiainfo.veris.crm.order.soa.person.common.util;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;

public class TradeUtils extends CSBizService
{

    private static final long serialVersionUID = 1L;
    /**
     * 
     * @Description：根据数据判断用户是否做了证件的变更
     * @param:@param btd
     * @param:@return
     * @return boolean
     * @throws Exception 
     * @throws
     * @Author :tanzheng
     * @date :2018-8-9下午04:27:31
     */
    public static boolean getChangePsptId(BusiTradeData btd) throws Exception {
    	boolean result = false ;
		String  tradePsptid=getChangeNewPsptId(btd);
	    //客户资料中的证件号码(原来的客户证件号码)
	    String psptId = CustomerInfoQry.getCustInfoPsptBySn(btd.getRD().getUca().getSerialNumber()).first().getString("PSPT_ID");
	    System.out.println("TradeUtils--tradePsptid"+tradePsptid+"--psptId"+psptId);
	    //判断证件号码是否有修改过
	    if(StringUtils.isNotBlank(tradePsptid)&&StringUtils.isNotBlank(psptId))
	    {
	    	if(!psptId.equals(tradePsptid)){
	    		result = true ;
	    	}
	    }
		
		return result;
		
	}
    /**
     * 
     * @Description：获取变更的证件号码
     * @param:@param btd
     * @param:@return
     * @param:@throws Exception
     * @return String
     * @throws
     * @Author :tanzheng
     * @date :2018-8-9下午04:52:21
     */
    public static String getChangeNewPsptId(BusiTradeData btd) throws Exception {
    	List<CustomerTradeData> lsCustomerTrade = btd.get("TF_B_TRADE_CUSTOMER");
    	
    	System.out.println("TradeUtils--lsCustomerTrade"+lsCustomerTrade.toString());
		String  tradePsptid="";
		if(CollectionUtils.isNotEmpty(lsCustomerTrade)){
			//台帐中的证件号码(新的证件号码)
			for(int i=0,size=lsCustomerTrade.size();i<size;i++){
				if(lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_UPD)||lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					tradePsptid=lsCustomerTrade.get(i).getPsptId();
					break;
				}
			}
		}
		return tradePsptid;
	}
    /**
     * 
     * @Description：获取变更的证件类型
     * @param:@param btd
     * @param:@return
     * @param:@throws Exception
     * @return String
     * @throws
     * @Author :tanzheng
     * @date :2018-8-9下午04:52:21
     */
    public static String getChangeNewPsptType(BusiTradeData btd) throws Exception {
    	List<CustomerTradeData> lsCustomerTrade = btd.get("TF_B_TRADE_CUSTOMER");
		String  tradePsptType="";
		if(CollectionUtils.isNotEmpty(lsCustomerTrade)){
			//台帐中的证件类型
			for(int i=0,size=lsCustomerTrade.size();i<size;i++){
				if(lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_UPD)||lsCustomerTrade.get(i).getModifyTag().equals(BofConst.MODIFY_TAG_ADD)){
					tradePsptType=lsCustomerTrade.get(i).getPsptTypeCode();
					break;
				}
			}
		}
		return tradePsptType;
	}
    
}
