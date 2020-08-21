package com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.filter.out;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IFilterOut;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.person.busi.internettv.order.requestdata.InternetTvOpenRequestData;
import com.asiainfo.veris.crm.order.soa.person.common.util.WideNetUtil;


/**
 * 进行光猫、魔百和押金转账处理
 * 之所以放在出参转换，是为了保证先把所有规则走完，最后在插台帐前调计费账务转账接口，以避免调完计费账务接口被规则拦截，无法回滚
 * @author yuyj3
 */
public class DealWidenetDepositOutFilter implements IFilterOut
{

    @Override
    public IData transfterDataOut(IData input, BusiTradeData btd) throws Exception
    {
    	InternetTvOpenRequestData reqData = ((InternetTvOpenRequestData)btd.getRD());

        int topSetBoxDeposit = reqData.getTopSetBoxDeposit();
        
        if (topSetBoxDeposit > 0)
        {
            //调用账务的接口进行押金返回
            IData params=new DataMap(); 
            
            params.put("OUTER_TRADE_ID", btd.getMainTradeData().getRsrvStr4());
            params.put("SERIAL_NUMBER", reqData.getNormalSerialNumber());
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
            String result=resultData.getString("RESULT_CODE","");
            
            if("".equals(result) || !"0".equals(result))
            {
                CSAppException.appError("61312", "调用接口AM_CRM_TransFeeInADSL转存押金入参：" + params + "错误:" + resultData.getString("RESULT_INFO"));
            }
        }
        
    	IData resultInfo = new DataMap();
        return resultInfo;
    }

}
