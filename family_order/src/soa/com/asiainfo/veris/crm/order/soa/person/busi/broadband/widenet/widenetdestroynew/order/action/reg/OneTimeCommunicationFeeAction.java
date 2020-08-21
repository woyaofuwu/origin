
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;

public class OneTimeCommunicationFeeAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(OneTimeCommunicationFeeAction.class);
    /**
     * 在使用虚拟账号操作商务宽带FTTH业务退订时，是否退光猫选项如果选择否，
     * 且该虚拟账号宽带业务使用期限不足三年，
     * 则系统自动在虚拟账号所归属的商务宽带产品编码一次性通信服务费上按200元/台收取宽带使用补偿金。
     * 通过在tf_b_trade_other表中修改一次性通信费用的方式，叠加要收取的补偿金，
     * 完工后同步给账务当月收取费用。
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	DestroyUserNowRequestData rd = (DestroyUserNowRequestData)btd.getRD();
    	String trade_type_code = btd.getRD().getTradeType().getTradeTypeCode();
    	String userId = btd.getRD().getUca().getUserId();
        String serialNumber = btd.getRD().getUca().getSerialNumber();
    	if(serialNumber.indexOf("KD_")>-1 && ("605".equals(trade_type_code) || "615".equals(trade_type_code))) {//宽带账号
        	String modermReturn = rd.getModermReturn();//是否退光猫标识
        	//是否申领光猫
        	IDataset userOtherCode = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"FTTH_GROUP");
        	if (IDataUtil.isNotEmpty(userOtherCode)){
	    		if(serialNumber.split("_")[1].length()>11 && "0".equals(modermReturn))
	    		{
	    			//宽带业务使用期限不足三年
	    			IDataset year =  WidenetInfoQry.getUserWidenetYearInfo(userId);
	    			if (IDataUtil.isNotEmpty(year)){
	    	        // 1、以当前userID作为关系表中的user_ID_B来查询所有未失效的关系数据
		    	        IDataset userBRelaInfos = RelaUUInfoQry.getUserIdB(userId, "47");
		    	        if (IDataUtil.isNotEmpty(userBRelaInfos))
		    	        {
		    	        	String userIdA = userBRelaInfos.getData(0).getString("USER_ID_B", "");
		    	        	//查询出主号的一次性费用
		    	        	IDataset userOtherFee = UserOtherInfoQry.getOtherInfoByCodeUserId(userIdA,"N002");
		    	        	if (IDataUtil.isNotEmpty(userOtherFee))
		        	        {
		    	        		IData tempData = userOtherFee.getData(0);
		    	        		OtherTradeData data = new OtherTradeData(tempData);
		                        data.setModifyTag(BofConst.MODIFY_TAG_UPD);
		                        float fee = Float.parseFloat(tempData.getString("RSRV_STR4", "0")) + 200;
		                        data.setRsrvStr4(String.valueOf(fee));
		                        btd.add(serialNumber, data);
		        	        }else
		        	        {
		        	        	IData tempData = userOtherFee.getData(0);
		    	        		OtherTradeData data = new OtherTradeData(tempData);
		                        data.setModifyTag(BofConst.MODIFY_TAG_ADD);
		                        data.setUserId(userIdA);
		                        data.setRsrvValueCode("N002");
		                        data.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
		                        data.setEndDate(SysDateMgr.getTheLastTime());
		                        data.setRsrvStr4("200");
		                        btd.add(serialNumber, data);
		        	        }
		    	        }
	    			}
	    		}
        	}
    	}
    
    
    }
 
}
