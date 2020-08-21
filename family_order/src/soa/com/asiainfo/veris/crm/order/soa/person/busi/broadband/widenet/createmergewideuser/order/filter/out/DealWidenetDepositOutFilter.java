package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.filter.out;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.createmergewideuser.order.requestdata.MergeWideUserCreateRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


/**
 * 进行光猫、魔百和押金转账处理
 * 之所以放在出参转换，是为了保证先把所有规则走完，最后在插台帐前调计费账务转账接口，以避免调完计费账务接口被规则拦截，无法回滚
 * @author yuyj3
 */
public class DealWidenetDepositOutFilter implements IFilterOut
{

    protected static Logger log = Logger.getLogger(DealWidenetDepositOutFilter.class);
    
    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
    	
        MergeWideUserCreateRequestData mergeWideUserCreateRd = ((MergeWideUserCreateRequestData)btd.getRD());
        
        //付费模式为立即支付才在此处转账
        if ("P".equals(mergeWideUserCreateRd.getWidenetPayMode()))
        {
        	int modemDeposit = mergeWideUserCreateRd.getModemDeposit();
            
        	
            if (modemDeposit > 0 )
            {
                List<OtherTradeData> otherTradeDatas = btd.getTradeDatas(TradeTableEnum.TRADE_OTHER);
                
                if(otherTradeDatas != null && otherTradeDatas.size() > 0)
                {
                    for(int i = 0 ; i < otherTradeDatas.size() ; i++)
                    {
                        OtherTradeData otherTradeData = otherTradeDatas.get(i);
                        
                        String rsrvValueCode = otherTradeData.getRsrvValueCode() ;
                        //光猫押金
                        if(rsrvValueCode != null && rsrvValueCode.equals("FTTH"))
                        {
                            String rsrvTag1 = otherTradeData.getRsrvTag1();
                            //只有租赁的时候有押金
                            if(rsrvTag1 != null && "0".equals(rsrvTag1))
                            {
                                IData inparams=new DataMap();
                                inparams.put("SERIAL_NUMBER", mergeWideUserCreateRd.getNormalSerialNumber());
                                inparams.put("OUTER_TRADE_ID", otherTradeData.getRsrvStr8()); //光猫扣押金的流水ID，退还时需要此流水
                                inparams.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
                                inparams.put("DEPOSIT_CODE_IN", "9002"); //9002 光猫押金转入存折
                                inparams.put("TRADE_FEE", modemDeposit);
                                inparams.put("CHANNEL_ID", "15000");
                                inparams.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                                inparams.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                                inparams.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
                                inparams.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                                inparams.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                                
                                IData inAcct = AcctCall.transFeeInADSL(inparams);
                                
                                if(log.isDebugEnabled())
                                {
                                    log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + inAcct);
                                }
                                
                                String result=inAcct.getString("RESULT_CODE","");
                                
                                if("".equals(result) || !"0".equals(result))
                                {
                                    CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + inparams + "错误:" + inAcct.getString("RESULT_INFO"));
                                }
                            }
                        }
                    }
                }
            }
            
            int topSetBoxDeposit = mergeWideUserCreateRd.getTopSetBoxDeposit();
            
            if (topSetBoxDeposit > 0)
            {
                //调用账务的接口进行押金返回
                IData params=new DataMap(); 
                
                params.put("OUTER_TRADE_ID", btd.getMainTradeData().getRsrvStr4());
                params.put("SERIAL_NUMBER", mergeWideUserCreateRd.getNormalSerialNumber());
                params.put("DEPOSIT_CODE_OUT", WideNetUtil.getOutDepositCode());
                params.put("DEPOSIT_CODE_IN", "9016");
                params.put("TRADE_FEE", topSetBoxDeposit);
                params.put("CHANNEL_ID", "15000");
                params.put("UPDATE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                params.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                params.put("TRADE_CITY_CODE",  CSBizBean.getVisit().getCityCode());
                params.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
                params.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
                
                //调用接口，将【现金类】——>【押金】
                IData resultData = AcctCall.transFeeInADSL(params);
                
                if(log.isDebugEnabled())
                {
                    log.debug("调用AM_CRM_TransFeeInADSL 接口返回结果:" + resultData);
                }
                
                String result=resultData.getString("RESULT_CODE","");
                
                if("".equals(result) || !"0".equals(result))
                {
                    CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
                }
            }
        }
        
        //如果办理IMS固话业务，则对固话号码进行预占操作
        if (StringUtils.isNotBlank(mergeWideUserCreateRd.getFixNumber()) && StringUtils.isNotBlank(mergeWideUserCreateRd.getImsProductId()))
        {
        	//IMS固话号码预占
            ResCall.resEngrossForMphone(mergeWideUserCreateRd.getFixNumber());
        }
        
        IData resultInfo = new DataMap();
        return resultInfo;
    }
}
