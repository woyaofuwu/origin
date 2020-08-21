
package com.asiainfo.veris.crm.order.soa.group.modifypayrelation;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSpecialPayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class BatMebPayRelaDestroyBean extends MemberBean
{
	private String queryType = null;
	
    private String actionFlag = null;

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        actTradePayRela();
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        data.put("REMARK", "批量取消集团代付");
    }
    
    /**
     * 处理付费关系
     * 
     * @throws Exception
     */
    public void actTradePayRela() throws Exception
    {
    	
    	String serialNumber = reqData.getUca().getSerialNumber();
    	if("1".equals(queryType) && StringUtils.isNotBlank(serialNumber))
    	{
    		IDataset payRelaSet = new DatasetList();
            IDataset specPayRelaSet = new DatasetList();
            
    		IDataset userPayListInfo = PayRelaInfoQry.qryGrpSpecialPayBySerialNumber(serialNumber, null);
    		if(IDataUtil.isEmpty(userPayListInfo))
    		{
    			String errMes = "未获取到该号码" + serialNumber + "的付费关系!";
    			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
    		}
    		for (int i = 0, iRow = userPayListInfo.size(); i < iRow; i++)
            {
    			IData userPayDataInfo = userPayListInfo.getData(i);
    			String userId = userPayDataInfo.getString("USER_ID","");
    			//String actTag = userPayDataInfo.getString("ACT_TAG","");
    			//String payItemCode = userPayDataInfo.getString("PAYITEM_CODE", "");
    			String acctId = userPayDataInfo.getString("ACCT_ID", "");
    			//String limit = userPayDataInfo.getString("LIMIT","");
    			//String limitType = userPayDataInfo.getString("LIMIT_TYPE","");
    			
    			IDataset userPayList = PayRelaInfoQry.qryPayRelaByUserAcctIdDefTag(userId, acctId, "0");
    			if(IDataUtil.isEmpty(userPayList))
        		{
        			String errMes = "未获取到该用户USER_ID=" + userId + ",ACCT_ID=" + acctId + "的付费关系!";
        			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        		}
    			for (int K = 0, KRow = userPayList.size(); K < KRow; K++)
                {
    				IData userPayData = userPayList.getData(K);
    				String payItemCode = userPayData.getString("PAYITEM_CODE", "");
        			String limit = userPayData.getString("LIMIT","");
        			String limitType = userPayData.getString("LIMIT_TYPE","");
        			
        			IDataset userSpecialPayList = UserSpecialPayInfoQry.qryUserSpecialPay(userId, acctId, payItemCode);
        			if (IDataUtil.isEmpty(userSpecialPayList))
                    {
                        CSAppException.apperr(GrpException.CRM_GRP_638, acctId, userId);
                    }
        			for (int j = 0, jRow = userSpecialPayList.size(); j < jRow; j++)
                    {
        				IData userSpecialPyaData = userSpecialPayList.getData(j);
            			String userIdA = userSpecialPyaData.getString("USER_ID_A");
            			String limitSpec = userSpecialPyaData.getString("LIMIT","");
            			String limitTypeSpec = userSpecialPyaData.getString("LIMIT_TYPE","");
            			
            			if (!limit.equals(limitSpec) || !limitType.equals(limitTypeSpec))
            			{
            				continue;
            			}
            			
            			// 状态属性：0-增加，1-删除，2-变更
                        userPayData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        userSpecialPyaData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        
                        // 注销(上月底)
                        if ("1".equals(actionFlag))
                        {

                            String lastCycleLastAcct = SysDateMgr.getDateForYYYYMMDD(SysDateMgr.getAddMonthsLastDay(-1));
                            userPayData.put("END_CYCLE_ID", lastCycleLastAcct);
                            userPayData.put("ACT_TAG", "0");// 设为无效(0)
                            userPayData.put("REMARK", "注销2(上月底)"); // 注销(上月底)

                            userSpecialPyaData.put("END_CYCLE_ID", lastCycleLastAcct);
                            userSpecialPyaData.put("REMARK", "注销2(上月底)"); // 注销(上月底)
                        }
                        else if ("2".equals(actionFlag))
                        {
                        	// 注销(本月底)
                            String lastCycleThisAcct = SysDateMgr.getLastCycleThisMonth();
                            userPayData.put("END_CYCLE_ID", lastCycleThisAcct);
                            // 状态属性：0-增加，1-删除，2-变更
                            userPayData.put("REMARK", "注销2(本月底)"); // 注销(本月底)

                            userSpecialPyaData.put("END_CYCLE_ID", lastCycleThisAcct);
                            userSpecialPyaData.put("REMARK", "注销2(本月底)"); // 注销(本月底)

                        }
                        payRelaSet.add(userPayData);
                        specPayRelaSet.add(userSpecialPyaData);
                    }
        			
                }
    			
            }
    		
    		if(IDataUtil.isEmpty(payRelaSet) && IDataUtil.isEmpty(specPayRelaSet))
    		{
    			String errMes = "没有满足条件的付费关系!";
    			CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
    		}
    		
    		super.addTradePayrelation(payRelaSet);
            super.addTradeUserSpecialepay(specPayRelaSet);
    	}
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        queryType = map.getString("QUERY_TYPE", "");
        actionFlag = map.getString("ACTION_FLAG", "");
        
        if(!"1".equals(queryType))
        {
        	String errMes = "查询方式不正确,请核实!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
        
        if(!"1".equals(actionFlag) && !"2".equals(actionFlag))
        {
        	String errMes = "失效方式不正确,请核实!";
            CSAppException.apperr(GrpException.CRM_GRP_713, errMes);
        }
                
        reqData.setNeedSms(false); //暂时不发短信
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);

        super.makUcaForMebNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        return "9982";
    }
}
