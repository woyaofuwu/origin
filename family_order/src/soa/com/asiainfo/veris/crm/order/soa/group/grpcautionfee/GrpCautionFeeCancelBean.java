package com.asiainfo.veris.crm.order.soa.group.grpcautionfee;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;

public class GrpCautionFeeCancelBean extends GroupBean
{
	protected GrpCautionFeeReqData reqData = null;
	
	@Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        this.regDataTradefeeSub();
        //this.regDataTradefeePaymoney();
        
        //新增
        this.createGrpCautionFee();
        this.createGrpCautionFeeLog();
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
	  * 新增集团客户保证金清退
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
	  * 新增集团客户保证金清退操作日志
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
		infoData.put("OPER_TAG", "3");
		infoData.put("INSERT_STAFF_ID", CSBizBean.getVisit().getStaffId());
		infoData.put("INSERT_DEPART_ID", CSBizBean.getVisit().getDepartId());
		infoData.put("INSERT_TIME", SysDateMgr.getSysTime());
		Dao.insert("TF_F_USER_GRP_CAUTIONFEE_LOG", infoData);
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
                
    }

    @Override
    protected final void makUca(IData map) throws Exception
    {
        makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "8905";
    }
    
}