package com.asiainfo.veris.crm.iorder.soa.person.busi.tp.tpdatadeal;

import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.person.busi.tp.base.ReqBuildService;
import com.asiainfo.veris.crm.order.pub.exception.TpOrderException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

/**
产品变更对应的甩单构造请求报文类
 目前立即销户和携出销户有限制优惠但没有限制服务，所以暂时只处理优惠的甩单
 **/
public class ChangeProductTPBuildSVC extends ReqBuildService {
    @Override
    public IData initReqData(IData input) throws Exception {
        String tradeTypeCode = "110";
        String eparchyCode = "0898";
        String userId = input.getString("USER_ID");
        String discntCode = input.getString("DISCNT_CODE","");
        String serialNumber = input.getString("SERIAL_NUMBER","");
        IData qryParam = new DataMap();
//        input.put("EPARCHY_CODE",eparchyCode);
        input.put("ROUTE_EPARCHY_CODE",eparchyCode);
//        input.put("USER_ID",userId);
        input.put("TRADE_TYPE_CODE",tradeTypeCode);
        IDataset results = CSAppCall.call("CS.SelectedElementSVC.getUserElements", input);
        IData delElement = null;
        if(IDataUtil.isNotEmpty(results)){
            IDataset elements = results.first().getDataset("SELECTED_ELEMENTS");
            if(IDataUtil.isNotEmpty(elements)){
                int eleSize = elements.size();
                for (int i = 0; i < eleSize; i++) {
                    IData element = elements.getData(i);
                    if(discntCode != null && discntCode.equals(element.getString("ELEMENT_ID")) && "D".equals(element.getString("ELEMENT_TYPE_CODE"))){
                         if("1".equals(element.getString("PACKAGE_FORCE_TAG")) || "1".equals(element.getString("PACKAGE_FORCE_TAG"))){
                             CSAppException.apperr(TpOrderException.TP_ORDER_400015,discntCode); //throw e
                         }
                         delElement = element;
                         break;
                    }
                }
            }
        }
        if(delElement == null){
            CSAppException.apperr(TpOrderException.TP_ORDER_400016,discntCode);//throw e
        }
        delElement.put("MODIFY_TAG","1");
        delElement.put("END_DATE",TimeUtil.getSysDateYYYYMMDDHHMMSS());
        IDataset regList = new DatasetList();
        regList.add(delElement);
//        IData regMap = new DataMap();
        input.put("SELECTED_ELEMENTS",regList);
//        regMap.put("SERIAL_NUMBER",serialNumber);
//        regMap.put("EPARCHY_CODE",eparchyCode);
//        regMap.put("ROUTE_EPARCHY_CODE",eparchyCode);
//        regMap.put("TRADE_TYPE_CODE",tradeTypeCode);
        IDataset dataset = CSAppCall.call("SS.ChangeProductRegSVC.tradeReg", input);
        return dataset.first();
    }

    @Override
    public String getTradeTypeCode(IData input) throws Exception {
        return null;
    }
}
