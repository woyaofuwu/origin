
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.requestdata.NoPhoneWideDestroyUserRequestData;
  
public class NoPhoneWideAdjustFeeAction implements ITradeAction
{

	private static transient Logger logger = Logger.getLogger(NoPhoneWideAdjustFeeAction.class);
    /**
	 * 无手机宽带 chenxy3 20170117
     * 调用账务接口进行调账
     * 将“宽带光猫押金存折”退费
     */
    public void executeAction(BusiTradeData btd) throws Exception
    {
    	
    	NoPhoneWideDestroyUserRequestData rd = (NoPhoneWideDestroyUserRequestData)btd.getRD();
    	String widetype = rd.getWideType();
    	if ("1".equals(widetype) || "2".equals(widetype) || "4".equals(widetype) || "6".equals(widetype))
    	{
    		//因现在的业务类型统一使用原GPON宽带的，需要把action表中业务类型为625的reg等配置改为605
    		//需要在这里重新判断下宽带类型，只允许FTTH有光猫的宽带类型执行
    		return ;
    	}
    	if(rd.getModermReturn().equals("0"))//如果选择不退光猫，则不调华为接口
    	{
    		return;
    	}
    	if (!"0".equals(rd.getModemMode()))
        {
         	return ;//非租赁光猫，不需要处理光猫退订
        }

    	String serialNumber = rd.getSerialNumberA();
    	
//    	if(serialNumber.length()>11)//集团宽带不处理
//    		return;
    	
    	String tradeFee = rd.getModemFee();
    	
    	IDataset userInfos = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
    	if(userInfos.isEmpty()){
    		logger.error("FTTH宽带拆机时，查询用户信息失败！"+serialNumber);
    		return;
    	}
    	
        String eparchyCode = userInfos.getData(0).getString("EPARCHY_CODE");
        String cityCode = userInfos.getData(0).getString("CITY_CODE");
        //查询租赁光猫信息
        IDataset userOthersInfos = UserOtherInfoQry.getModemRentByCodeUserId(userInfos.getData(0).getString("USER_ID"),"FTTH");
        if(userOthersInfos != null && userOthersInfos.size() > 0)
        {
        	tradeFee = userOthersInfos.getData(0).getString("RSRV_STR2","0");
        	String outTradeId = userOthersInfos.getData(0).getString("RSRV_STR8","");
        	String modemFeeState = userOthersInfos.getData(0).getString("RSRV_STR7","");
        	if(!"0".equals(modemFeeState))
        	{
        		return ;
        	}
        	
        	 //调测费用户没有押金不再对押金处理
//            IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUserIdAndRouteId(rd.getUca().getUserId(),"84073842",Route.CONN_CRM_CG);
            IDataset discntInfo = UserDiscntInfoQry.getAllDiscntByUser_2(rd.getUca().getUserId(), "84073842");//后面现场调测费优惠只有一个月有效期，不能根据有效的去判断了  modify_by_duhj_kd

            if(IDataUtil.isNotEmpty(discntInfo))
            {
            	return;
            }

            if (!"".equals(tradeFee)&&Integer.parseInt(tradeFee) > 0)
            {
            	 
//            		IData param = new DataMap();
//                    param.put("TRADE_FEE", tradeFee);
//                    param.put("EPARCHY_CODE", eparchyCode);
//                    param.put("CITY_CODE", cityCode);
//                    param.put("SERIAL_NUMBER", serialNumber);
//            		param.put("OUTER_TRADE_ID", outTradeId);
//            		param.put("DEPOSIT_CODE_OUT", "9002");
//            		param.put("CHANNEL_ID", "15000");
//            		param.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
//               		param.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
//            		param.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
//               		param.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
//               		param.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
               		
            	//3、获取默认账户  （acct_id)
				IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
		    	String acctId=accts.first().getString("ACCT_ID");
		    	IData params=new DataMap(); 
				params.put("ACCT_ID", acctId);
				params.put("CHANNEL_ID", "15000");
				params.put("PAYMENT_ID", "14");
				params.put("PAY_FEE_MODE_CODE", "0");
				params.put("REMARK", "无手机宽带退还光猫押金退费！");
				IData depositeInfo=new DataMap();
				depositeInfo.put("DEPOSIT_CODE", "9002");
				depositeInfo.put("TRANS_FEE", tradeFee);
				
				IDataset depositeInfos=new DatasetList();
				depositeInfos.add(depositeInfo);
				params.put("DEPOSIT_INFOS", depositeInfos);
		        CSBizBean.getVisit().setStaffEparchyCode("0898");
		   		//调用接口，将【押金】退费
				IData inAcct =AcctCall.foregiftDeposite(params);
				String callRtnType=inAcct.getString("X_RESULTCODE","");
				if(!"".equals(callRtnType)&&"0".equals(callRtnType)){ 
				}else{ 
					if (logger.isDebugEnabled())
	                {
	                    logger.error("FTTH宽带拆机时，光猫押金退费失败！【"+serialNumber+"】");
	                }
	            	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口退订光猫押金失败:" + inAcct.getString("X_RESULTINFO",""));
				} 
            	
            }
        }
    } 
}
