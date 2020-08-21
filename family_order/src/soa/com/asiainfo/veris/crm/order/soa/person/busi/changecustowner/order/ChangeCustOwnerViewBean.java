package com.asiainfo.veris.crm.order.soa.person.busi.changecustowner.order;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.priv.StaffPrivUtil;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class ChangeCustOwnerViewBean extends CSBizBean
{
    private static final long serialVersionUID = 1L;
    
    Logger logger=Logger.getLogger(ChangeCustOwnerViewBean.class);
    /**
     * 提交前的校验规则
     * @param input
     * @return
     * @throws Exception
     */
    public IData checkBefore(IData input)throws Exception{
    	
    	UcaData uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER",""));
    	logger.debug("--------ChangeCustOwnerViewBean---------uca:"+uca+",input:"+input);
    	logger.debug("--------ChangeCustOwnerViewBean---------GrpCustId:"+uca.getGrpCustId());
        IData inparam = new DataMap();
        inparam.put("REQDATA", input);// 请求数据
        inparam.put("UCADATA", uca);
        inparam.putAll(input);// 尽量不用这里面的
        /******** 用户表所有字段 ******************/
        inparam.putAll(uca.getUser().toData());

        // 规则必传参数
        inparam.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
        inparam.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("PROVINCE_CODE", CSBizBean.getVisit().getProvinceCode());
        inparam.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());

        inparam.put("REDUSER_TAG", "0"); // 红名单标记， 0－不是；1－是
        inparam.put("ID_TYPE", "1"); // 0:cust_id, 1:user_id
        inparam.put("ID", uca.getUserId());
        inparam.put("USER_ID", uca.getUserId());
        inparam.put("PRODUCT_ID", uca.getProductId());
        inparam.put("BRAND_CODE", uca.getBrandCode());
        inparam.put("TRADE_TYPE_CODE", input.getString("TRADE_TYPE_CODE",""));

        /************ 账户、账期 ******************/
        inparam.put("ACCT_ID", uca.getAccount().getAcctId());
        inparam.put("ACCT_DAY", uca.getAcctDay());
        inparam.put("FIRST_DATE", uca.getFirstDate());
        inparam.put("NEXT_ACCT_DAY", uca.getNextAcctDay());
        inparam.put("NEXT_FIRST_DATE", uca.getNextFirstDate());

        /******** 客户资料 部分字段 *****************/
        inparam.put("CUST_ID", uca.getCustId());
        inparam.put("CUST_NAME", uca.getCustomer().getCustName());
        inparam.put("CUST_TYPE", uca.getCustomer().getCustType());
        inparam.put("IS_REAL_NAME", uca.getCustomer().getIsRealName());
        inparam.put("PSPT_TYPE_CODE", uca.getCustomer().getPsptTypeCode());
        inparam.put("PSPT_ID", uca.getCustomer().getPsptId());

        inparam.put("TIPS_TYPE", "0|4");// 受理服务里为提高效率只调报错的规则

        String judgeOweTag =null;// reqData.getTradeType().getJudgeOweTag();
        String fee = "0";
        String lastOweFee = "0";
        String realFee = "0";
        String leaveRealFee = "0";
        // 暂时先这么写，一切都为了钱，晚点考虑更好的办法
        if ("0".equals(judgeOweTag))
        {

        }
        else
        {
            IData oweFee = AcctCall.getOweFeeByUserId(uca.getUserId());
        	logger.debug("--------ChangeCustOwnerViewBean---------oweFee:"+oweFee);

            if ("6".equals(judgeOweTag) || "7".equals(judgeOweTag) || "8".equals(judgeOweTag))
            {
                fee = "-" + oweFee.getString("LAST_OWE_FEE");
            }
            else
            {
                fee = oweFee.getString("ACCT_BALANCE");
            }
            lastOweFee = oweFee.getString("LAST_OWE_FEE");
            realFee = oweFee.getString("REAL_FEE");
            leaveRealFee = oweFee.getString("ACCT_BALANCE");
            uca.setLastOweFee(lastOweFee);
            uca.setAcctBlance(leaveRealFee);
            uca.setRealFee(realFee);
        }

        inparam.put("FEE", fee);
        inparam.put("LAST_OWE_FEE", lastOweFee);
        inparam.put("REAL_FEE", realFee);
        inparam.put("LEAVE_REAL_FEE", leaveRealFee);

        inparam.put("CHECK_TAG", "-1");
        inparam.put("TIPS_TYPE", "0|4");
        inparam.put("ORDER_TYPE_CODE", DataBusManager.getDataBus().getOrderTypeCode());

        String ruleTypeCode =null;// this.getRuleTypeCode();
        if (StringUtils.isBlank(ruleTypeCode))
        {
            ruleTypeCode = "TRADEALL.TradeCheckBefore";// modify by xiaocl
        }

        String ruleKindCode =null;// this.getRuleKindCode();
        if (StringUtils.isBlank(ruleKindCode))
        {
            ruleKindCode = "TradeCheckSuperLimit";
        }

        inparam.put("ACTION_TYPE", ruleTypeCode);
    	logger.debug("--------ChangeCustOwnerViewBean---------ruleTypeCode:"+ruleTypeCode+",inparam:"+inparam);

        IData checkData = BizRule.TradeBeforeCheck4Error(inparam); 
    	logger.debug("--------ChangeCustOwnerViewBean---------checkData:"+checkData);

        return checkData;
    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData tradeCheckAfter1(IData input)throws Exception{
    	
        String  testCardBreManage=checkTestCardUser(input);
        if(!"".equals(testCardBreManage)&&testCardBreManage!=null){
        	//存在规则拦截
        	//testCardBreManage
        }
        
    	
    	return null;
    }
    
    public IData tradeCheckAfter100(IData input)throws Exception{
    	
        String  testCardBreManage=checkTestCardUser(input);
        if(!"".equals(testCardBreManage)&&testCardBreManage!=null){
        	//存在规则拦截
        	//testCardBreManage
        	
        }
    	return null;
    }    
    /**
     * 对于“限制办理渠道”的测试卡用户，只能通过有拥有该权限的客服特定工号进行办理 “不限制办理渠道的测试号码可通过任何渠道办理业务。
     * @param databus
     * @return
     * @throws Exception
     */
    public String checkTestCardUser(IData databus)throws Exception{
    	   String  result=null;

			String serialNumber = databus.getString("SERIAL_NUMBER");
			IDataset  userInfo=UserInfoQry.getUserInfoBySerailNumber("0", serialNumber);
			
			if(IDataUtil.isNotEmpty(userInfo)){
				String strUserId=userInfo.getData(0).getString("USER_ID");
			    IDataset callset=UserOtherInfoQry.getUserOther(strUserId, "TEST_CARD_USER");
	   			if(IDataUtil.isNotEmpty(callset)){
			   		   String rsrvValue=callset.getData(0).getString("RSRV_VALUE");
			   		   if("0".equals(rsrvValue)){
			   			   //限制办理渠道
			   			   //权限判断
				           boolean testcardprTradepriv = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "TESTCARDPR_TRADEPRIV");
				           if(!testcardprTradepriv){
			   				   //无权限(拦截)
				        	   result="该用户无权限办理业务";
			   			   }
			   		   }
	   			}
			}
    	return result;
    }   
}
