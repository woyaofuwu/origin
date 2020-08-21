package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.action.reg;
 
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidedestroy.order.requestdata.NoPhoneWideDestroyUserRequestData;

/**
 * 无手机宽带拆机成功后释放宽带账号。
 * @author chenxy3
 */
public class NoPhoneWideSpeDestroyBackFee implements ITradeAction {

	@Override
	 public void executeAction(BusiTradeData btd) throws Exception
	{ 
		NoPhoneWideDestroyUserRequestData rd = (NoPhoneWideDestroyUserRequestData)btd.getRD();
		//3、获取默认账户  （acct_id)	
    	String acctId = btd.getRD().getUca().getAcctId(); 
    	String backFee=rd.getBackFee();
    	if(backFee != null && !"".equals(backFee))
    	{
    		callAcctBackFee(acctId,backFee);
    	}
	}
	
	/**
     * 无手机宽带，余额沉淀
     * 清退账户9023余额
     * */
    public static void callAcctBackFee(String acctId,String backFee) throws Exception {
    	 
    	IData params=new DataMap(); 
		params.put("ACCT_ID", acctId);
		params.put("CHANNEL_ID", "15000");
		params.put("PAYMENT_ID", "100021");
		params.put("PAY_FEE_MODE_CODE", "0");
		params.put("FORCE_TAG", "1");//強制清退
		params.put("REMARK", "无手机宽带特殊拆机余额清退！");
		IData depositeInfo=new DataMap();
		depositeInfo.put("DEPOSIT_CODE", "9021");
		depositeInfo.put("TRANS_FEE", backFee);//清退金额
		
		IDataset depositeInfos=new DatasetList();
		depositeInfos.add(depositeInfo);
		params.put("DEPOSIT_INFOS", depositeInfos);
        CSBizBean.getVisit().setStaffEparchyCode("0898");
   		//调用接口，清退金额
		IData inAcct =AcctCall.foregiftDeposite(params);
		String callRtnType=inAcct.getString("X_RESULTCODE","");
		if(!"".equals(callRtnType)&&"0".equals(callRtnType)){ 
		}else{  
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "调用账务接口清退无手机宽带特殊拆机余额失败:" + inAcct.getString("X_RESULTINFO",""));
		}   
    }
}
