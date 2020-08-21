package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderBaseBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;

public class StopOrBackLineRegBean extends GroupOrderBaseBean {

    public void actOrderDataOther(IData map) throws Exception {
        String productNo = map.getString("PRODUCTNO");
        String userId = map.getString("USER_ID");
        String productId = map.getString("PRODUCT_ID");
        String operType = map.getString("OPER_TYPE");
        int pfWait = 0;

        IData esopData = map.getData("ESOP");
        String subscribeId = "";
        if(DataUtils.isNotEmpty(esopData)) {
            subscribeId = esopData.getString("IBSYSID", "");
        }
        String serialNo = "";
        if(subscribeId != null && !subscribeId.equals("")) {
            serialNo = "ESOP" + subscribeId + "1";
        }
        
        //判断是否开环
        map.put("PF_WAIT", pfWait);
        //不发服开
        map.put("OLCOM_TAG", 0);
        
        String changeMode = "";
        String sheetType = "";
        if("stop".equals(operType)) {
            changeMode = "人工停机";
            sheetType = "41";
        } else if("back".equals(operType)) {
            changeMode = "人工复机";
            sheetType = "42";
        } else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未知操作编码[" + operType + "]");
        }
        IData maps = new DataMap();
        maps.put("PRODUCT_NO", productNo);
        maps.put("CHANGE_MODE", changeMode);
        maps.put("SERIALNO", serialNo);
        maps.put("SUBSCRIBE_ID", subscribeId);
        maps.put("CRMNO", subscribeId);
        map.put("OLCOM_TAG", 1);
        map.put("DATALINE", maps);
        map.put("SHEETTYPE", sheetType);

        String crtTradeClass = "";
        if("7010".equals(productId)) {
            crtTradeClass = "CreateLineClass";
        } else {
            crtTradeClass = "CreateCreditClass";
        }

        GrpInvoker.ivkProduct(map, BizCtrlType.ChangeUserDis, crtTradeClass);
    }
    /**
     * 数据专线（专网专线）集团产品受理业务类型
     */
    @Override
    protected String setOrderTypeCode() throws Exception {
        // TODO Auto-generated method stub
        return "3010";
    }

}
