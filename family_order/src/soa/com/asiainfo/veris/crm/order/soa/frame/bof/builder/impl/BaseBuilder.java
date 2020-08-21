
package com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl;

import com.ailk.biz.BizEnv;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.configdata.TradeTypeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.OrderDataBus;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.databus.util.DataBusManager;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.fee.FeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.exception.impl.BofException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public abstract class BaseBuilder implements IBuilder
{

    public abstract void buildBusiRequestData(IData param, BaseReqData brd) throws Exception;

    public void buildReqFee(IData param, BaseReqData brd) throws Exception
    {
        String tradeTypeCode = brd.getTradeType().getTradeTypeCode();
        if (StringUtils.isNotBlank(param.getString("X_TRADE_FEESUB")))
        {
            IDataset feeSubInfos = new DatasetList(param.getString("X_TRADE_FEESUB"));
            int feeSubSize = feeSubInfos.size();
            for (int i = 0; i < feeSubSize; i++)
            {
                IData feeSubInfo = feeSubInfos.getData(i);
                if (tradeTypeCode.equals(feeSubInfo.getString("TRADE_TYPE_CODE")))
                {
                    FeeData feeData = new FeeData();
                    String fee = feeSubInfo.getString("FEE", "0");
                    String oldFee = feeSubInfo.getString("OLDFEE", "0");
                    if(!"0".equals(fee) && fee.indexOf(".") != -1){
                    	fee = fee.substring(0,fee.indexOf("."));
                    }
                    if(!"0".equals(oldFee) && oldFee.indexOf(".") != -1){
                    	oldFee = oldFee.substring(0,oldFee.indexOf("."));
                    }
                    feeData.setFee(fee);
                    feeData.setFeeMode(feeSubInfo.getString("FEE_MODE", "0"));
                    feeData.setFeeTypeCode(feeSubInfo.getString("FEE_TYPE_CODE", "0"));
                    feeData.setOldFee(oldFee);
                    feeData.setDiscntGiftId(feeSubInfo.getString("DISCNT_GIFT_ID"));
                    feeData.setElementId(feeSubInfo.getString("ELEMENT_ID"));

                    brd.addFeeData(feeData);
                }
            }
        }
    }

    @Override
    public BaseReqData buildRequestData(IData param) throws Exception
    {
        IData cond = new DataMap();

        // 构建三户资料对象
        UcaData uca = this.buildUcaData(param);
        uca.setSubmitType(DataBusManager.getDataBus().getSubmitType());
        DataBusManager.getDataBus().setUca(uca);

        // 构建基本请求对象
        BaseReqData brd = this.getBlankRequestDataInstance();
        brd.setUca(uca);
        brd.setXTransCode(CSBizBean.getVisit().getXTransCode());
        brd.setJoinType(param.getString("JOIN_TYPE", "0"));

        // 设置业务类型参数
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");
        IData tradeType = UTradeTypeInfoQry.getTradeType(tradeTypeCode, uca.getUserEparchyCode());

        if (IDataUtil.isEmpty(tradeType))
        {
            CSAppException.apperr(BofException.CRM_BOF_001, tradeTypeCode);
        }
        brd.setTradeType(new TradeTypeData(tradeType));
        OrderDataBus orderDataBus = DataBusManager.getDataBus();
        brd.setSubmitType(orderDataBus.getSubmitType());
        brd.setSubmitSource(param.getString("SUBMIT_SOURCE",""));

        // 提交前规则校验
        //IS_LUCKY_NUMBER是在BatOpernStopDeal封装，用来区分是通过后台来回收吉祥号
        String isLuckyNumber=param.getString("IS_LUCKY_NUMBER", "");
        boolean isCheck=true;
        
        //对于批量预开户的吉祥号的回收进行特殊的处理：不进行业务前规则效验
        if(tradeTypeCode.equals("192")&&isLuckyNumber!=null&&isLuckyNumber.equals("1")){
        	IDataset numberInfo = ResCall.getMphonecodeInfo(uca.getSerialNumber());// 查询号码信息
        	if (IDataUtil.isNotEmpty(numberInfo))
            {
        		String jxNumber = numberInfo.getData(0).getString("BEAUTIFUAL_TAG","");
        		if(jxNumber!=null&&jxNumber.equals("1")){
        			isCheck=false;
        		}
            }
        }
        
        //是否跳过规则，如果传入SKIP_RULE=TRUE，则不进行提交后的规则校验
        if(StringUtils.isNotBlank(param.getString("SKIP_RULE","")) && "TRUE".equals(param.getString("SKIP_RULE","")))
        {
        	isCheck = false;
        }
        
        if(isCheck){
        	this.checkBefore(param, brd);
        }
        
        if (StringUtils.isNotBlank(param.getString("BATCH_ID")))
        {
            brd.setBatchId(param.getString("BATCH_ID"));
        }
        if (StringUtils.isNotBlank(param.getString("REMARK")))
        {
            brd.setRemark(param.getString("REMARK"));
        }
        if (StringUtils.isNotBlank(param.getString("PRE_TYPE")))
        {
            brd.setPreType(param.getString("PRE_TYPE"));
        }
        if (StringUtils.isNotBlank(param.getString("IS_CONFIRM")))
        {
            brd.setIsConfirm(param.getString("IS_CONFIRM"));
        }
        if ("1".equals(param.getString("IS_QUADRIC_NOTE")))
        {
            brd.setIsConfirm("true");
        }
        String strNeedSms = param.getString("IS_NEED_SMS", "");
        if (StringUtils.isNotBlank(strNeedSms) && "false".equals(strNeedSms))
        {
        	brd.setNeedSms(false);
        }

        String strNeedAction = param.getString("IS_NEED_ACTION", "");
        if (StringUtils.isNotBlank(strNeedAction) && "false".equals(strNeedAction))
        {
            brd.setNeedAction(false);
        }

        brd.setBatchDealType(param.getString("BATCH_DEAL_TYPE"));
        // 身份校验方式
        brd.setCheckMode(param.getString("CHECK_MODE", "Z"));// 默认为无

        // 构建业务请求对象
        this.buildBusiRequestData(param, brd);

        // 构建费用信息
        buildReqFee(param, brd);

        brd.setPageRequestData(param);

        return brd;
    }

    public UcaData buildUcaData(IData param) throws Exception
    {
        // 设置三户资料对象
        String sn = param.getString("SERIAL_NUMBER");
        UcaData uca = DataBusManager.getDataBus().getUca(sn);
        if (uca == null)
        {
            uca = UcaDataFactory.getNormalUca(sn);
        }
        
        if("1".equals(param.getString("PRE_TYPE",""))){
        	uca = UcaDataFactory.getNormalUca(sn,true);
        }
        
        return uca;
    }

    protected void checkBefore(IData input, BaseReqData reqData) throws Exception
    {
        UcaData uca = reqData.getUca();

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
        inparam.put("TRADE_TYPE_CODE", reqData.getTradeType().getTradeTypeCode());

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

        String judgeOweTag = reqData.getTradeType().getJudgeOweTag();
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

        String ruleTypeCode = this.getRuleTypeCode();
        if (StringUtils.isBlank(ruleTypeCode))
        {
            ruleTypeCode = "TRADEALL.TradeCheckBefore";// modify by xiaocl
        }

        String ruleKindCode = this.getRuleKindCode();
        if (StringUtils.isBlank(ruleKindCode))
        {
            ruleKindCode = "TradeCheckSuperLimit";
        }

        inparam.put("ACTION_TYPE", ruleTypeCode);

        IData checkData = BizRule.TradeBeforeCheck4Error(inparam);
        CSAppException.breerr(checkData);
    }

    public abstract BaseReqData getBlankRequestDataInstance();

    public String getRuleKindCode()
    {
        return null;
    }

    public String getRuleTypeCode()
    {
        return null;
    }
    
    private void checkFee(IData param) throws Exception
    {
    	boolean feeCheck = BizEnv.getEnvBoolean("bof.feeCheck");
        if (feeCheck)
        {
        	int totalFee = param.getInt("X_TOTAL_FEE");
        	if(totalFee != 0)
        	{
        		int tmpTotalFee = 0;
        		if (StringUtils.isNotBlank(param.getString("X_TRADE_FEESUB")))
                {
                    IDataset feeSubInfos = new DatasetList(param.getString("X_TRADE_FEESUB"));
                    int feeSubSize = feeSubInfos.size();
                    for (int i = 0; i < feeSubSize; i++)
                    {
                        IData feeSubInfo = feeSubInfos.getData(i);
                        tmpTotalFee += feeSubInfo.getInt("FEE");
                    }
                }
        		
        		if(totalFee != tmpTotalFee)
        		{
        			CSAppException.apperr(BofException.CRM_BOF_022, totalFee, tmpTotalFee);
        		}
        	}
        }
    }
}
