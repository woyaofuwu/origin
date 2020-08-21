
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.rule;

import org.apache.log4j.Logger;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UTradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.tradectrl.TradeCtrl;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TradeTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public final class CheckTradeBean extends CSBizBean
{
    private static final Logger log = Logger.getLogger(CheckTradeBean.class);

    public IDataset checkBeforeTrade(IData data) throws Exception
    {
        IData inparam = new DataMap();
        inparam.putAll(data);

        // 规则必传参数
        inparam.put("TRADE_STAFF_ID", getVisit().getStaffId());
        inparam.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        inparam.put("TRADE_CITY_CODE", getVisit().getCityCode());
        inparam.put("TRADE_DEPART_ID", getVisit().getDepartId());
        inparam.put("PROVINCE_CODE", getVisit().getProvinceCode());
        inparam.put("IN_MODE_CODE", getVisit().getInModeCode());

        inparam.put("REDUSER_TAG", "0"); // 红名单标记， 0－不是；1－是
        inparam.put("ID_TYPE", "1"); // 0:cust_id, 1:user_id
        inparam.put("ID", data.getString("USER_ID"));
        inparam.put("USER_ID", data.getString("USER_ID"));
        inparam.put("CUST_ID", data.getString("CUST_ID"));
        inparam.put("PRODUCT_ID", data.getString("PRODUCT_ID"));
        inparam.put("BRAND_CODE", data.getString("BRAND_CODE"));
        inparam.put("X_CHOICE_TAG", data.getString("X_CHOICE_TAG")); // 0:输号码校验;1:提交校验;
        inparam.put("TRADE_TYPE_CODE", data.getString("TRADE_TYPE_CODE"));
        inparam.put("IS_MERGE_WIDE_CANCEL", data.getString("IS_MERGE_WIDE_CANCEL"));

        String judgeOweTag = "0";
        IData tradeTypeInfo = UTradeTypeInfoQry.getTradeType(data.getString("TRADE_TYPE_CODE"), BizRoute.getRouteId());
        if (IDataUtil.isNotEmpty(tradeTypeInfo))
        {
            // TODO 这里可能报空指针异常，是否要处理？
            judgeOweTag = tradeTypeInfo.getString("JUDGE_OWE_TAG");
        }

        String fee = "0";
        IData oweFee = new DataMap();
        if (!"0".equals(judgeOweTag))
        {
            // 欠费查询
            oweFee = AcctCall.getOweFeeByUserId(data.getString("USER_ID"));
            
            if ("6".equals(judgeOweTag) || "7".equals(judgeOweTag) || "8".equals(judgeOweTag))
            {
                fee = "-" + oweFee.getString("LAST_OWE_FEE");
            }
            else
            {
                fee = oweFee.getString("ACCT_BALANCE");
            }
        }
        

        inparam.put("FEE", fee);

        // 规则里面需要
        inparam.put("LAST_OWE_FEE", oweFee.getString("LAST_OWE_FEE", "0"));
        inparam.put("REAL_FEE", oweFee.getString("REAL_FEE", "0"));
        inparam.put("LEAVE_REAL_FEE", oweFee.getString("ACCT_BALANCE", "0"));

        inparam.put("CHECK_TAG", "-1");
        if ("401".equals(inparam.getString("TRADE_TYPE_CODE")))
        {
            inparam.put("TRADE_TYPE_CODE", "402");// 携号特殊处理,因为携号规则都挂在402下,所以在携号申请的时候走402的规则.
        }

        //先进行未完工工单限制校验
        lmtTradeReg(data.getString("TRADE_TYPE_CODE"),data.getString("SERIAL_NUMBER"));
         //再执行业务配置规则
        IData checkData = BizRule.TradeBeforeCheck(inparam);

        IDataset returnDataset = new DatasetList();
        returnDataset.add(checkData);
        return returnDataset;
    }

    public void verifyunFinishTrade(IData data) throws Exception
    {
    	this.lmtTradeReg(data.getString("TRADE_TYPE_CODE"),data.getString("SERIAL_NUMBER"));
    }
    
    private void lmtTradeReg(String tradeTypeCode,String sn) throws Exception
    {
        // 业务登记限制，判断是否有未完工业务
        boolean limit = TradeCtrl.getCtrlBoolean(tradeTypeCode, TradeCtrl.CTRL_TYPE.TRADE_REG_LIMIT, true);

        if (limit == false) // 不限制
        {
            return;
        }

        if (StringUtils.isBlank(sn))
        {
            return;
        }
        String tradeTypeName1 = UTradeTypeInfoQry.getTradeTypeName(tradeTypeCode);

        IDataset ids = TradeInfoQry.getMainTradeBySn(sn, BizRoute.getTradeEparchyCode());
        if (sn.substring(0, 4).equals("KD_1"))
        {
        	sn = sn.substring(3);
        }else{
        	
        	 //家庭IMS固话未完工校验
        	IData useuca = UcaInfoQry.qryUserInfoBySn(sn);
            if(IDataUtil.isNotEmpty(useuca))
            {
            	IDataset id2 = TradeInfoQry.getExistUser("MS", useuca.getString("USER_ID"), "1");
            	
                if(id2!=null&&id2.size()>0&&ids!=null&&ids.size()>0){
                	ids.addAll(id2);
                }else if(id2==null||id2.size()==0){}
            	else if((ids==null||ids.size()==0)&&(id2!=null&&id2.size()>0)){
            		ids = id2;
            	}
            }
        	
        	sn = "KD_" + sn;
        }
        IDataset id1 = TradeInfoQry.getMainTradeBySn(sn, BizRoute.getRouteId());
        
        if(id1!=null&&id1.size()>0&&ids!=null&&ids.size()>0){
        	ids.addAll(id1);
        }else if(id1==null||id1.size()==0){}
    	else if((ids==null||ids.size()==0)&&(id1!=null&&id1.size()>0)){
    		ids = id1;
    	}
        

        if (IDataUtil.isEmpty(ids))
        {
            return;
        }

        IDataset tradeTypeLimitDataset = TradeTypeInfoQry.queryTradeTypeLimitInfos(tradeTypeCode, BizRoute.getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(tradeTypeLimitDataset))
        {
            IDataset unFinishTrades = ids;
            if (IDataUtil.isNotEmpty(unFinishTrades))
            {
                for (int i = 0, count = unFinishTrades.size(); i < count; i++)
                {
                    // 得到定单信息
                    IData unFinishTrade = unFinishTrades.getData(i);
                    String unFinishTradeTypeCode = unFinishTrade.getString("TRADE_TYPE_CODE");
                    boolean bFindLimit = false;
                    for (int j = 0, jcount = tradeTypeLimitDataset.size(); j < jcount; j++)
                    {
                        IData tradeTypeLimitData = tradeTypeLimitDataset.getData(j);
                        if (StringUtils.equals(unFinishTradeTypeCode,
                                tradeTypeLimitData.getString("LIMIT_TRADE_TYPE_CODE")))
                        {
                            bFindLimit = true;
                            break;
                        }
                    }
                    if (bFindLimit)
                    {
                        String tradeId = unFinishTrade.getString("TRADE_ID");
                        String acceptDate = unFinishTrade.getString("ACCEPT_DATE");
                        // 得到定单类型
                        String tradeTypeName = UTradeTypeInfoQry.getTradeTypeName(unFinishTrade
                                .getString("TRADE_TYPE_CODE"));
                        //判断办理校讯通时，是否该用户办理过魔百盒开户等长流程的业务有未完工的工单，如果是则不校验是否有未完工的魔百盒工单 update by zhuwj
                        String stu=tradeTypeName.substring(0, 2);
                        String stu1=tradeTypeName1.substring(0, 2);
                        if("魔百和".equals(stu) && "和校园".equals(stu1)){
                        	 return;
                        }
                        
                        CSAppException.apperr(CrmCommException.CRM_COMM_982, tradeTypeName, tradeId, acceptDate);
                    }
                }
            }
        }
    }
}
