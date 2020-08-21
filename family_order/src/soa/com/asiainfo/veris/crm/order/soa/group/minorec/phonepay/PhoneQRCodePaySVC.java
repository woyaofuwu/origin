package com.asiainfo.veris.crm.order.soa.group.minorec.phonepay;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupOrderService;
import com.asiainfo.veris.crm.order.soa.group.querygroupinfo.GrpLineInfoQryBean;

import java.text.DecimalFormat;

public class PhoneQRCodePaySVC extends GroupOrderService {

    private static final long serialVersionUID = 1L;

    public IDataset queryInfos(IData data) throws Exception{
        IDataset resultList =  PhoneQRCodePayBean.queryInfos(data,getPagination());
        if(DataUtils.isEmpty(resultList)){
            return resultList;
        }

        for(Object obj : resultList){
            IData resultData = (IData) obj;
            resultData.put("PRODUCT_NAME",UProductInfoQry.getProductNameByProductId(resultData.getString("PRODUCT_ID")));
            IData acctInfo = GrpLineInfoQryBean.queryAcctInfo(resultData.getString("ACCT_ID"));
            int a = acctInfo.getInt("ALL_MONEY");
            int b = 100;
            DecimalFormat df = new DecimalFormat("0.00");
            resultData.put("ALL_MONEY",df.format((float)a/b));
        }
        return resultList;

    }

    public IDataset tradeReg(IData input) throws Exception{
        setUserEparchyCode(getVisit().getLoginEparchyCode());
        PhoneQRCodePayRegBean bean = new PhoneQRCodePayRegBean();
        return bean.crtTrade(input);
    }

    public IData payTrade(IData input) throws Exception{
        input.put("UPDATE_STAFF_ID",getVisit().getStaffId());
        input.put("UPDATE_DEPART_ID",getVisit().getDepartId());
        return PhoneQRCodePayBean.payTrade(input);
    }

    public IDataset queryOrderInfos(IData param) throws Exception{
        IData groupInfo = UcaInfoQry.qryGrpInfoByGrpId(param.getString("GROUP_ID"));
        param.put("CUST_ID",groupInfo.getString("CUST_ID"));

        IDataset orderInfos = PhoneQRCodePayBean.queryOrderInfos(param);

        if(DataUtils.isNotEmpty(orderInfos)){
            for(int i=0;i<orderInfos.size();i++){
                IData orderDate = orderInfos.getData(i);
                orderDate.put("PRODUCT_NAME",UProductInfoQry.getProductNameByProductId(orderDate.getString("PRODUCT_ID")));
                orderDate.put("GROUP_ID",param.getString("GROUP_ID"));
                String tradeId = orderDate.getString("TRADE_ID");
                IData feeData = PhoneQRCodePayBean.queryTradeFee(tradeId);
                orderDate.put("FEE_TYPE",feeData.getString("FEE_TYPE"));
                orderDate.put("FEE",feeData.getString("OLDFEE"));
            }
        }

        return orderInfos;
    }

    public IData cancelTrade(IData param) throws Exception{
        return PhoneQRCodePayBean.cancelTrade(param);
    }

    public IDataset queryCustInfoByName(IData param) throws Exception{
        return PhoneQRCodePayBean.queryCustInfoByName(param);
    }
}
