package com.asiainfo.veris.crm.order.soa.group.modifydataline;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.querygroupinfo.GrpLineInfoQryBean;

public class ModifyDataLineBean extends GroupBean{

    private IData lineData;
    
    @Override
    public void actTradeBefore() throws Exception {

        super.actTradeBefore();
    }

    @Override
    public void actTradeSub() throws Exception {

        super.actTradeSub();

        actTradeDataLine();

    }
    
    public void actTradeDataLine() throws Exception{
        if(IDataUtil.isEmpty(lineData)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到修改的专线数据！");
        }
        String productNo = lineData.getString("PRODUCT_NO");
        
        IData dataline =  GrpLineInfoQryBean.queryLineByProductNO(productNo);
        if(IDataUtil.isEmpty(dataline)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据专线实例号【"+productNo+"】为获取到专线信息！"); 
        }
        
        dataline.putAll(lineData);
        dataline.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        
        super.addTradeDataLine(dataline);
    }
    
    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);
        lineData = map.getData("LINE_DATE");
    }

    @Override
    protected void makUca(IData map) throws Exception {
        makUcaForGrpNormal(map);
    }

    @Override
    protected String setTradeTypeCode() throws Exception {
        return "1603";
    }
}
