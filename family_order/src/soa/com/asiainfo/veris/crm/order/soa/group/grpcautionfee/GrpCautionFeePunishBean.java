package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;

import com.ailk.common.BaseException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GrpCautionFeePunishBean extends GroupBean
{
	protected GrpCautionFeeReqData reqData = null;
	
	private static final Logger logger = Logger.getLogger(GrpCautionFeePunishBean.class);
	
	@Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.regDataTradefeeSub();
        this.regDataTradefeePaymoney();
        this.regDataTradefeeDefer();
        
        //新增
        this.createGrpCautionFee();
        this.createGrpCautionFeeLog();
        
        this.punishPayFee();
    }

	/**
	 * 记录业务办理时，营业费用、押金、预存款的详细缴纳情况
	 * @throws Exception
	 */
	private void regDataTradefeeSub() throws Exception
    {
		String userId = reqData.getUca().getUserId();
		
        IData tradeInfo = new DataMap();
        
        tradeInfo.put("USER_ID", userId);
        tradeInfo.put("FEE_MODE", "1");
        tradeInfo.put("FEE_TYPE_CODE", "9888");
        tradeInfo.put("OLDFEE", reqData.getDepositFee());
        tradeInfo.put("FEE", reqData.getDepositFee());
        
        tradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());

        super.addTradefeeSub(tradeInfo);
    }
	
	/**
	 * 记录办理业务时用户费用缴纳的付款情况
	 * @throws Exception
	 */
	private void regDataTradefeePaymoney() throws Exception
    {
        IData tradeInfo = new DataMap();
        tradeInfo.put("PAY_MONEY_CODE", "0");
        tradeInfo.put("MONEY", reqData.getDepositFee());
        
        tradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());

        super.addTradefeePayMoney(tradeInfo);

    }
	
	/**
	 * 记录办理业务时业务台帐费用挂帐子表
	 * @throws Exception
	*/
	private void regDataTradefeeDefer() throws Exception
    {
		String userId = reqData.getUca().getUserId();
		String dataId = reqData.getDepositType();
		IData tradeInfo = new DataMap();
		tradeInfo.put("USER_ID", userId);
        tradeInfo.put("FEE_MODE", "1");
        tradeInfo.put("FEE_TYPE_CODE", "9888");
        tradeInfo.put("DEFER_CYCLE_ID", "-1");
        //tradeInfo.put("DEFER_ITEM_CODE", "10480");
        tradeInfo.put("MONEY", reqData.getAcctDepositFee());
        tradeInfo.put("ACT_TAG", "1");
        
        IData paramSQL = new DataMap();
        IDataset queryResult = new DatasetList();
        paramSQL.put("TYPE_ID", "DEPOSIT_TYPE");
        paramSQL.put("DATA_ID", dataId);
        paramSQL.put("SUBSYS_CODE", "CSM");
        try{
		 	
	        SQLParser parser = new SQLParser(paramSQL);
	        // 查询
	        parser.addSQL("SELECT PDATA_ID ");
	        parser.addSQL("FROM td_s_static C ");
	        parser.addSQL("WHERE C.TYPE_ID =:TYPE_ID ");
	        parser.addSQL("AND C.DATA_ID =:DATA_ID ");
	        parser.addSQL("AND C.SUBSYS_CODE =:SUBSYS_CODE ");

	        queryResult = Dao.qryByParse(parser, Route.CONN_CRM_CEN);
	        if ( !CollectionUtils.isEmpty(queryResult) ) {
	        	if ( queryResult.size() > 0 ){
	        		String pdata_Id = queryResult.getData(0).getString("PDATA_ID");
	        		tradeInfo.put("DEFER_ITEM_CODE", pdata_Id);
	        	}
			}else {
				//默认赋值后向流量账目
				tradeInfo.put("DEFER_ITEM_CODE", "10480");
 			}
			
		}catch(Exception e){
	    	String error =  Utility.parseExceptionMessage(e);
	    	String[] errorArray = error.split(BaseException.INFO_SPLITE_CHAR);
			if(errorArray.length >= 2)
			{
				String strExceptionMessage = errorArray[1];
				logger.info("GrpCautionFeePunishBean-数据查询异常！"+";TYPE_ID:DEPOSIT_TYPE;DATA_ID:"+dataId+";strExceptionMessage:"+strExceptionMessage);
			}
			else
			{
				logger.info("GrpCautionFeePunishBean-数据查询异常！"+";TYPE_ID:DEPOSIT_TYPE;DATA_ID:"+dataId+";error:"+error);
			}  
         }
		
        tradeInfo.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        tradeInfo.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        tradeInfo.put("UPDATE_TIME", SysDateMgr.getSysTime());
        tradeInfo.put("REMARK", "集团客户保证金扣罚");

        super.addTradefeeDefer(tradeInfo);

    }
	
	 /**
	  * 新增集团客户保证金扣罚
	  * @throws Exception
	 */
	private void createGrpCautionFee() throws Exception
	{
		String userId = reqData.getUca().getUserId();
		
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DEPOSIT_TYPE", reqData.getDepositType());
		IDataset feeInfos = GrpCautionFeeMgrQry.queryCautionFeeByUserId(param);
		
		if(IDataUtil.isNotEmpty(feeInfos))//原来该用户有保证金记录
		{
			String auditOrder = reqData.getAuditOrder();
			String depositFee = reqData.getDepositFee();
			String depositType = reqData.getDepositType();
			
			GrpCautionFeeMgrQry.updateCautionFeeByUserId(userId,auditOrder,
					depositFee,depositType);
		}
		else 
		{
			CSAppException.apperr(GrpException.CRM_GRP_713,"未获取到该用户的保证金金额!");
		}
		
   }
   
	/**
	 * 新增集团客户保证金扣罚操作日志
	 * @throws Exception
	 */
    private void createGrpCautionFeeLog() throws Exception
    {
    	String userId = reqData.getUca().getUserId();
		String serialNumberA = reqData.getUca().getSerialNumber();
		
		IData infoData = new DataMap();
		IData param = new DataMap();
		param.put("USER_ID", userId);
		param.put("DEPOSIT_TYPE", reqData.getDepositType());
		IDataset feeInfos = GrpCautionFeeMgrQry.queryCautionFeeByUserId(param);		
		if(IDataUtil.isNotEmpty(feeInfos) && feeInfos.size()> 0){
			//原来该用户有保证金记录
			infoData.put("REL_ID", feeInfos.getData(0).getString("REL_ID"));
		}
			
		infoData.put("USER_ID", userId);
		infoData.put("PARTITION_ID", StrUtil.getPartition4ById(userId));
		infoData.put("SERIAL_NUMBER", serialNumberA);
		infoData.put("AUDIT_ORDER", reqData.getAuditOrder());
		infoData.put("DEPOSIT_FEE", reqData.getDepositFee());
		infoData.put("DEPOSIT_TYPE", reqData.getDepositType());
		infoData.put("OPER_TAG", "2");
		infoData.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
		infoData.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
		infoData.put("INSERT_TIME", SysDateMgr.getSysTime());
		Dao.insert("TF_F_USER_GRP_CAUTIONFEE_LOG", infoData);
    }
    
    /**
	 * 调用账务接口做扣罚
	 * @throws Exception
	 */
    private void punishPayFee() throws Exception
    {
		String serialNumberA = reqData.getUca().getSerialNumber();
		String tradeId = getTradeId();
        String tradeFee = reqData.getAcctDepositFee();
        
        String channelId = "150004";
        String paymentId = "9149";
        String payFeeModeCode = "0";
        String remark = "集团保证金扣罚";

        if(logger.isDebugEnabled())
        {
        	logger.debug("<<<<<<<SerialNumberA=>>>>>>>" + serialNumberA);
        	logger.debug("<<<<<<<tradeId=>>>>>>>" + tradeId);
        	logger.debug("<<<<<<<tradeFee=>>>>>>>" + tradeFee);
        }
        
        IDataset ids = AcctCall.recvFee(serialNumberA, tradeId, 
        		tradeFee, channelId, 
        		paymentId, payFeeModeCode, remark);

        if(logger.isDebugEnabled())
        {
        	logger.debug("<<<<<<<调用账务缴费接口扣罚的返回结果=>>>>>>>" + ids);
        }
        
        if (ids.size() < 0)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "集团保证金扣罚异常!");
        }
    }
    
	@Override
    protected BaseReqData getReqData() throws Exception
    {
        return new GrpCautionFeeReqData();
    }
	
	@Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (GrpCautionFeeReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setAuditOrder(map.getString("AUDIT_ORDER"));
        reqData.setDepositFee(map.getString("DEPOSIT_FEE"));
        reqData.setDepositType(map.getString("DEPOSIT_TYPE"));
        reqData.setAcctDepositFee(map.getString("ACCT_DEPOSIT_FEE"));        
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "8904";
    }
    
}