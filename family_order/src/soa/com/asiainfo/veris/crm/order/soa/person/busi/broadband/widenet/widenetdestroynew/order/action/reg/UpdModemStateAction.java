package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
/**
 * REQ201612080012_优化手机销户关联宽带销号的相关规则
 * <br/>
 * 手机欠费销号，宽带关联销号时，光猫押金沉淀，状态修改为丢弃
 * <br/>
 * 由于 手机欠费销号  宽带修改为报停，则不对光猫押金进行处理。
 * 只有当资源调SS.DestroyUserNowSVC.checkWideInfoAndDestroy做
 * 号码回收时，宽带做特殊拆机处理时才对光猫处理。
 * @author zhuoyingzhi
 * @date 20180126
 *
 */
public class UpdModemStateAction implements ITradeAction {

	protected static Logger log = Logger.getLogger(UpdModemStateAction.class);
	
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String phoneDestroyType = btd.getRD().getPageRequestData().getString("PHONE_DESTROY_TYPE","");
		String tradeTypeCode = btd.getRD().getPageRequestData().getString("TRADE_TYPE_CODE","");
		System.out.println("---UpdModemStateAction-----phoneDestroyType:"+phoneDestroyType+",tradeTypeCode:"+tradeTypeCode);
		log.debug("---UpdModemStateAction-----phoneDestroyType:"+phoneDestroyType+",tradeTypeCode:"+tradeTypeCode);
		
		if(phoneDestroyType != null && "7240".equals(phoneDestroyType) && "615".equals(tradeTypeCode))
		{
			String phoneSerialNumber = "";
			//传入的是KD_宽带号码
			String kd_serialNumber = btd.getRD().getUca().getSerialNumber();
			String userId="";
	        if(kd_serialNumber.indexOf("KD_")>-1) {//宽带账号
	    		if(kd_serialNumber.split("_")[1].length()>11)
	    			phoneSerialNumber = kd_serialNumber ;
	    		else
	    			phoneSerialNumber = kd_serialNumber.split("_")[1];//个人账号
	    	} 
			
			//查询号码是否正常
	        IData param = new DataMap();
	    	param.put("SERIAL_NUMBER", phoneSerialNumber);
	    	IDataset userInfos = qryDestoryUser(param);
	    	if(userInfos!=null && userInfos.size()>0){
	    		userId=userInfos.getData(0).getString("USER_ID","");
	    	}
	    	

    		IDataset userOtherinfo = null;

			//userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserOtherInfo", userinfo.first());
			
    		IData cond = new DataMap();
    		cond.put("SERIAL_NUMBER", phoneSerialNumber);
            cond.put("USER_ID", userId);
            cond.put("RSRV_VALUE_CODE", "FTTH");
    		userOtherinfo = CSAppCall.call("SS.DestroyUserNowSVC.queryUserModemRent", cond);
			if(IDataUtil.isNotEmpty(userOtherinfo))
    		{
				String rsrvTag1 = userOtherinfo.getData(0).getString("RSRV_TAG1","");
				String rsrvTag2 = userOtherinfo.getData(0).getString("RSRV_TAG2","");
				if(rsrvTag1 != null && "0".equals(rsrvTag1))
				{
					if(rsrvTag2 != null && !"3".equals(rsrvTag2))
					{
						String remark = "欠费销号记录光猫丢失,押金沉淀";
						//只有租赁时才记录光猫的状态为丢失
    					OtherTradeData data = new OtherTradeData(userOtherinfo.first());
						data.setModifyTag(BofConst.MODIFY_TAG_DEL);
						data.setEndDate(SysDateMgr.getSysDate(SysDateMgr.PATTERN_STAND_YYYYMMDD) + SysDateMgr.getFirstTime00000());

						data.setRsrvTag2("4");//记录光猫丢失状态

						//押金沉淀
						String rsrvStr7 = userOtherinfo.getData(0).getString("RSRV_STR7","");
						String rsrvStr2 = userOtherinfo.getData(0).getString("RSRV_STR2","");
						if(rsrvStr7 != null && rsrvStr7.equals("0"))
						{
							IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
							if(IDataUtil.isNotEmpty(acctInfo))
							{

								/**
								 * 判断宽带光猫押金存折是否有钱
								 * @author zhuoyingzhi
								 * @data 20180709
								 */
								String acctId=acctInfo.getString("ACCT_ID","");
						    	int balance9002=0;
								IDataset allUserMoney = AcctCall.queryAccountDepositByAcctId(acctId);
								if(IDataUtil.isNotEmpty(allUserMoney)){
							    	for(int i=0;i<allUserMoney.size();i++){
							    		if("9002".equals(allUserMoney.getData(i).getString("DEPOSIT_CODE"))){
							    			String balance1 = allUserMoney.getData(i).getString("DEPOSIT_BALANCE","0");
							                int balance2 = Integer.parseInt(balance1);
							                balance9002 = balance9002 + balance2;
							    		}
							    	}									
								}
								System.out.println("---UpdModemStateAction---allUserMoney:"+allUserMoney+",balance9002:"+balance9002);
								log.debug("---UpdModemStateAction---allUserMoney:"+allUserMoney+",balance9002:"+balance9002);
								if(balance9002 <= 0){
									//宽带光猫押金存折  没有钱
									remark +=",根据acctid:"+acctId+"获取账本以及对应余额,为0";
								}else{
									IData params=new DataMap(); 
									params.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
									params.put("CHANNEL_ID", "15000");
									params.put("PAYMENT_ID", "100021");
									params.put("PAY_FEE_MODE_CODE", "0");
									params.put("FORCE_TAG", "1");//強制清退
									params.put("REMARK", "押金清退！");
									IData depositeInfo=new DataMap();
									depositeInfo.put("DEPOSIT_CODE", "9002");
									depositeInfo.put("TRANS_FEE", rsrvStr2);//清退金额
									
									IDataset depositeInfos=new DatasetList();
									depositeInfos.add(depositeInfo);
									params.put("DEPOSIT_INFOS", depositeInfos);
							        CSBizBean.getVisit().setStaffEparchyCode("0898");
							   		//调用接口，清退金额
									IData inAcct =AcctCall.foregiftDeposite(params);
								    
								    if(IDataUtil.isNotEmpty(inAcct))
								    {
								    	String result = inAcct.getString("RESULT_CODE","");
								    	if("0".equals(result))
								    	{
								    		//修改押金状态已沉淀
								    		data.setRsrvStr7("3");//押金已沉淀
								    		remark += "成功";
								    	}
								    	else
								    	{
								    		log.error("欠费销号,光猫押沉淀,调用账务押金沉淀接口返回错误:错误代码:" + result + ";错误信息:" + inAcct.getString("RESULT_INFO","") );
								    		
								    		remark += "失败";
								    	}
								     }									
								  }
							}
						}
						data.setRemark(remark);
						btd.add(phoneSerialNumber, data);
					}
				}
    		}	  
		}
	}
	
	/**
     * 查询用户家庭固话信息
     * */
    public IDataset qryDestoryUser(IData params) throws Exception{
    	 
    	SQLParser parser = new SQLParser(params); 
        parser.addSQL("  select t.*  from Tf_f_User t ");
        parser.addSQL(" where T.SERIAL_NUMBER=:SERIAL_NUMBER ");
        parser.addSQL(" AND t.REMOVE_TAG='4' "); 
        IDataset infos=  Dao.qryByParse(parser); 
        
    	return infos;
    }
}
