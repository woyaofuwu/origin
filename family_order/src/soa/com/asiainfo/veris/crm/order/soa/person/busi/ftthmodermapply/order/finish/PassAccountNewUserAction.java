package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.order.finish;  

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.action.ITradeFinishAction;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeAcctInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.FTTHModermApplyBean;

/**
 * REQ201505210004 FTTH光猫申领
 * a)调账务提供的接口将原来用户的宽带光猫存折的钱转移到新的用户（注意过户业务可能生成新的账户）；
 * b)a点成功后登记台账，需要等级TF_B_TRADE_OTHER，修改START_DATE为当前时间；
 * @CREATED by chenxy3@2015-6-11
 * 修改历史 
 */
public class PassAccountNewUserAction implements ITradeFinishAction
{
	private static Logger logger = Logger.getLogger(PassAccountNewUserAction.class);
    @Override
    public void executeAction(IData mainTrade) throws Exception
    {
        // TODO Auto-generated method stub
        IData param = new DataMap();
        String tradeId = mainTrade.getString("TRADE_ID");
        String tradeTypeCode=mainTrade.getString("TRADE_TYPE_CODE");
        String serialNumber = mainTrade.getString("SERIAL_NUMBER");
        String userId = mainTrade.getString("USER_ID");
        String newCustId = mainTrade.getString("CUST_ID");
        String newEparchyCode = mainTrade.getString("EPARCHY_CODE");
        String newCityCode = mainTrade.getString("CITY_CODE"); 
        
        String execTime = mainTrade.getString("ACCEPT_DATE");
        
        if("100".equals(tradeTypeCode)){
	        FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
	        param.put("SERIAL_NUMBER", serialNumber);
	        IDataset users=bean.getUserModermInfo(param);
	        //考虑到可能用户操作了申领光猫，但是还没有串号，但是已经扣钱，因此只要有押金，就允许办理 
	        if(users!=null && users.size()>0){
	        	
	        	
//	        	Calendar ca=Calendar.getInstance();
//	            ca.add(Calendar.MINUTE, 5);//延后5分钟执行
//	            DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	            String execTime=df.format(ca.getTime());
	        	IData insparam = new DataMap();
	        	String paramValue = "";
	        	int addTime = 300;
	        	paramValue = StaticUtil.getStaticValue("DEAL_CHANGECUSTOWNER_EXPIRE", "EXPIRE_TIME");
	            if (StringUtils.isNotBlank(paramValue))
	            {
	            	addTime = Integer.parseInt(paramValue);
	            }
	        	
	        	insparam.put("DEAL_ID", SeqMgr.getTradeId());
	        	insparam.put("USER_ID", userId);
	            insparam.put("PARTITION_ID", userId.substring(userId.length() - 4));
	            insparam.put("SERIAL_NUMBER", serialNumber);
	            insparam.put("EPARCHY_CODE", newEparchyCode);
	            insparam.put("IN_TIME", SysDateMgr.getSysTime());
	            insparam.put("DEAL_STATE", "0");
	            insparam.put("DEAL_TYPE", BofConst.EXPIRE_TYPE_CHANGE_CUSTOWNER);
	            insparam.put("EXEC_TIME", SysDateMgr.addSecond(execTime,addTime));
	            insparam.put("EXEC_MONTH", SysDateMgr.getMonthForDate(execTime));
	            insparam.put("TRADE_ID", tradeId);

	            Dao.insert("TF_F_EXPIRE_DEAL", insparam); 
	        } 
        }
    } 
}
