package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.action.reg;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ResTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

/**
 * 手机欠费销号，宽带关联拆机，修改魔百和状态为为丢失
 * @author zyc
 *
 */
public class UpdateSetTopBoxStateAction implements ITradeAction {

	protected static Logger log = Logger.getLogger(UpdateSetTopBoxStateAction.class);
			
	@Override
	public void executeAction(BusiTradeData btd) throws Exception {
		String phoneDestroyType = btd.getRD().getPageRequestData().getString("PHONE_DESTROY_TYPE","");
		if(phoneDestroyType != null && "7240".equals(phoneDestroyType))
		{
			//查询是否有魔百和 
			String serialNumber = btd.getMainTradeData().getSerialNumber(); 
			serialNumber = serialNumber.replaceAll("KD_", "");
			IData userinfo = UcaInfoQry.qryUserInfoBySn(serialNumber); 
			if(IDataUtil.isNotEmpty(userinfo)){
				String userId = userinfo.getString("USER_ID");
				IDataset boxInfos = UserResInfoQry.qrySetTopBoxByUserIdAndTag1AllColumns(userId, "4", "J");
				if(IDataUtil.isNotEmpty(boxInfos))
				{
					String remark = "手机欠费销号,修改魔百盒状态为丢失";
					//魔百和押金沉淀,修改魔百和结束时间,需求没提魔百和营销活动,暂不考虑魔百和营销活动的处理

					String strForegiftFee = boxInfos.getData(0).getString("RSRV_NUM2","0");
					int foregiftFee = 0 ;
					if(strForegiftFee != null && !"".equals(strForegiftFee))
						foregiftFee = Integer.parseInt(strForegiftFee);
					
					if(foregiftFee > 0)
					{
						//押金沉淀
						
						IData acctInfo = UcaInfoQry.qryAcctInfoByUserId(userId);
						if(IDataUtil.isNotEmpty(acctInfo))
						{
						     //资金进行沉淀
						     IData depositeParam=new DataMap();
						     depositeParam.put("ACCT_ID", acctInfo.getString("ACCT_ID"));
						     depositeParam.put("CHANNEL_ID", "15000");
						     depositeParam.put("PAYMENT_ID", "100021");
						     depositeParam.put("PAY_FEE_MODE_CODE", "0");
						     depositeParam.put("REMARK", "欠费销号，押金进行沉淀！");
						     
						     IData depositeInfo = new DataMap();
						     depositeInfo.put("DEPOSIT_CODE", "9016");
						     depositeInfo.put("TRANS_FEE", String.valueOf(foregiftFee * 100));
						     
						     IDataset depositeInfos = new DatasetList();
						     depositeInfos.add(depositeInfo);
						     
						     depositeParam.put("DEPOSIT_INFOS", depositeInfos);
						     
						    try
						    {
						    	IData inAcct = AcctCall.foregiftDeposite(depositeParam);
							    
							    if(IDataUtil.isNotEmpty(inAcct))
							    {
							    	String result = inAcct.getString("RESULT_CODE","");
							    	if("0".equals(result))
							    	{
							    		//修改押金状态已沉淀
							    		remark += ",押金沉淀成功";
							    	}
							    	else
							    	{
							    		log.error("欠费销号,魔百和押金沉淀,调用账务押金沉淀接口返回错误:错误代码:" + result + ";错误信息:" + inAcct.getString("RESULT_INFO","") );
							    		remark += ",押金沉淀失败";
							    	}
							    }
						    }
						    catch(Exception e)
						    {
						    	e.printStackTrace();
						    	log.error("调用账务接口报错:" + e.getMessage());
						    	String errorMsg = getCause(e);
						    	if(errorMsg == null || "".equals(errorMsg))
						    	{
						    		errorMsg = e.getMessage();
						    	}
						    	
						    	remark += ",调用账务接口AM_CRM_BackFee失败!" + errorMsg ;
						    	if(remark != null && remark.length() > 50)
						    	{
						    		remark = remark.substring(0,49);
						    	}
						    }
						}
					}
					
					ResTradeData resTrade = new ResTradeData(boxInfos.first());
					resTrade.setModifyTag(BofConst.MODIFY_TAG_DEL);
					resTrade.setEndDate(SysDateMgr.getSysTime());
					resTrade.setRemark(remark);
					
					btd.add(serialNumber, resTrade);
				}
			}
		}
	}
	
	private String getCause(Throwable throwable) {

		if (throwable.getCause() != null) {
			return getCause(throwable.getCause());
		} else {
			return getStackTrace(throwable);
		}
	}
	
	private String getStackTrace(Throwable e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String str = sw.toString();
        return str;
    }
}
