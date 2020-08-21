
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.order.trade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustPersonTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustomerTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.iot.InstancePfQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.changecustinfo.order.requestdata.ModifyCustInfoReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.FTTHModermApplyBean;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply.order.requestdata.FTTHModermApplyReqData;

/**
 * REQ201505210004 FTTH光猫申领
 * 
 * @author chenxy3 2015-6-1
 */
public class FTTHModermApplyTrade extends BaseTrade implements ITrade
{
	String deposit="";//押金
    public void createBusiTradeData(BusiTradeData btd) throws Exception
    { 
    	String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();
    	String userid=btd.getRD().getUca().getUser().getUserId();
    	String custid=btd.getRD().getUca().getUser().getCustId();
    	String eparchycode=btd.getRD().getUca().getUser().getEparchyCode();
    	String citycode=btd.getRD().getUca().getUser().getCityCode();
    	
    	//1、先判断用户存在那种产品，只开通FTTH宽带押金200；开通宽带且办理宽带1+押金100    		
		FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		//2、根据用户判断是否开通FTTH宽带和宽带1+，返回值是commpara表attr=6131的内容。
		String payType=bean.getPayMoneyType(param);
		if("0".equals(payType)){
			CSAppException.appError("61310", "未办理FTTH宽带业务，无法办理申领光猫。");
		}else{
			//先取押金金额commpara表param_attr=6131
			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", payType, "0898");
			deposit=paras.getData(0).getString("PARA_CODE1");
			//已存在宽带产品，需要判断用户的现金是否足够
			//3、获取默认账户  （acct_id)
	    	IDataset accts=AcctInfoQry.qryAcctDefaultIdBySn(serialNumber);
	    	String acctId=accts.getData(0).getString("ACCT_ID");
	    	param.put("X_PAY_ACCT_ID", acctId); 
	    	//4、调接口判断用户的现金是否足够，不够则提示缴费，不登记台账；调用接口
	    	IData checkCash= AcctCall.getZDepositBalance(param);
	    	String cash=checkCash.getString("CASH_BALANCE");
	    	if(Integer.parseInt(cash)<Integer.parseInt(deposit)){
	    		CSAppException.appError("61311", "账户存折可用余额不足，请先办理缴费。账户余额："+Double.parseDouble(cash)/100+"元，押金金额："+Integer.parseInt(deposit)/100+"元");
	    	}else{
	    		//5、调账务提供的接口将现金存折的钱转到宽带光猫押金存折； 
	    		IData inparams=new DataMap();
	    		inparams.put("USER_ID", userid);
	    		inparams.put("ACCT_ID", acctId);
	    		inparams.put("CUST_ID", custid);
	    		inparams.put("TRADE_FEE", deposit);
	    		inparams.put("EPARCHY_CODE", eparchycode);
	    		inparams.put("CITY_CODE", citycode);
	    		inparams.put("SERIAL_NUMBER", serialNumber); 
	    		IData inAcct=AcctCall.transFEEInFtth(inparams);
	    		String result=inAcct.getString("RESULT_CODE","");
	    		if(!"".equals(result) && "0".equals(result)){
	    			// 成功！ 处理other表
	    	        createOtherTradeInfo(btd); 
	    		}else{
	    			CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInFTTH转存押金错误:"+inAcct.getString("RESULT_INFO"));
	    		}
	    	}
		}     	
    } 

    /**
     * 处理other台账表
     * 
     * @param btd
     * @throws Exception
     */
    private void createOtherTradeInfo(BusiTradeData btd) throws Exception
    {
    	FTTHModermApplyReqData rd = (FTTHModermApplyReqData) btd.getRD();
        String serialNumber = btd.getRD().getUca().getUser().getSerialNumber();

        OtherTradeData otherTradeData = new OtherTradeData();
        otherTradeData.setRsrvValueCode("FTTH");
        otherTradeData.setRsrvValue("FTTH光猫申领");
        otherTradeData.setUserId(rd.getUca().getUser().getUserId());
        otherTradeData.setStartDate(SysDateMgr.getSysDateYYYYMMDDHHMMSS());
        otherTradeData.setEndDate(SysDateMgr.getTheLastTime());
        otherTradeData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherTradeData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherTradeData.setInstId(SeqMgr.getInstId());// 新增 则需要新增inst_id值 
        otherTradeData.setModifyTag(BofConst.MODIFY_TAG_ADD); 
        otherTradeData.setRemark(rd.getRemark()); 
        otherTradeData.setRsrvStr2(deposit);//押金
        btd.add(serialNumber, otherTradeData);

        
    }
}
