package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class CancelMergeWidenetTradeDealSVC extends ReqBuildService {


    @Override
    public IData initReqData(IData input) throws Exception {
        String serialNumber = input.getString("SERIAL_NUMBER","");
        if(StringUtils.isEmpty(serialNumber)){
            CSAppException.apperr(TpOrderException.TP_ORDER_40004);
        }

        IData inparam = new DataMap();
        inparam.put("SERIAL_NUMBER",serialNumber);
        inparam.put("TRADE_TYPE_CODE","600"); //宽带开户
        IDataset results = new DatasetList();
        //查询可取消的订单
        results = CSAppCall.call("SS.CancelWidenetTradeService.queryUserCancelTradeMerge", inparam);
        if(IDataUtil.isEmpty(results)){
            IData inparam1 = new DataMap();
            inparam1.put("SERIAL_NUMBER",serialNumber);
            inparam1.put("TRADE_TYPE_CODE","606");  //宽带移机
            results = CSAppCall.call("SS.CancelWidenetTradeService.queryUserCancelTradeMerge", inparam1);
        }
        if(IDataUtil.isEmpty(results)){
            CSAppException.apperr(TpOrderException.TP_ORDER_400025);
        }

        IData result = results.getData(0);
        IData param = new DataMap();
        param.put("ACCEPT_MONTH",result.getString("ACCEPT_MONTH"));
        param.put("TRADE_DEPART_ID",result.getString("TRADE_DEPART_ID"));
        param.put("OPER_FEE",result.getString("OPER_FEE"));
        param.put("FEE_STAFF_ID",result.getString("FEE_STAFF_ID"));
        param.put("TRADE_STAFF_ID",result.getString("TRADE_STAFF_ID"));
        param.put("CANCEL_REASON","101909");
        param.put("CANCEL_REASON_ONE","101909");
        param.put("CANCEL_REASON_TWO","1019092");
        param.put("SERIAL_NUMBER",serialNumber);
        param.put("FEE_STATE",result.getString("FEE_STATE"));
        param.put("FOREGIFT",result.getString("FOREGIFT"));
        param.put("ADVANCE_PAY",result.getString("ADVANCE_PAY"));
        param.put("TRADE_EPARCHY_CODE",result.getString("TRADE_EPARCHY_CODE"));
        param.put("TRADE_TYPE_CODE",result.getString("TRADE_TYPE_CODE"));
        param.put("TRADE_ID",result.getString("TRADE_ID"));
        param.put("REMARK",result.getString("REMARK"));

        return param;
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }
}
