
package com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.rentmobile.order.requestdata.RentMobileReqData;

public class BuildRentMobileReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        String rentTag = param.getString("RENT_TAG");// 租机状态
        String rentList = param.getString("RENT_LIST", "{}");
        IData rentInfo = new DataMap(rentList);
        if (IDataUtil.isEmpty(rentInfo))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "请选择一部手机办理退租或者租机!");
        }

        RentMobileReqData reqData = (RentMobileReqData) brd;
        reqData.setRentTag(rentTag);
        reqData.setFeeSerialNumber(rentInfo.getString("PARA_CODE4"));
        reqData.setAreacode(rentInfo.getString("PARA_CODE5"));
        reqData.setRentImei(rentInfo.getString("PARA_CODE8"));
        String rentTypeCode = param.getString("RENT_TYPE_CODE");// 租机类型
        // String rentSerialNumber = param.getString("RENT_SERIAL_NUMBER");//租机号码
        String rentModeCode = IDataUtil.chkParam(param, "RENT_MODE_CODE");// 租机方式
        String invoiceNo = param.getString("INVOICE_NO");// 租机发票

        reqData.setRentModeCode(rentModeCode);
        reqData.setInvoiceNo(invoiceNo);
        if (("0").equals(rentTag))
        {
            // 退机
            reqData.setStartDate(rentInfo.getString("START_DATE"));
            reqData.setMoney(rentInfo.getString("MONEY"));
            reqData.setRentSerialNumber(rentInfo.getString("RENT_SERIAL_NUMBER"));
        }
        else if ("1".equals(rentTag))
        {
            // 租机
            reqData.setStartDate(param.getString("START_DATE", SysDateMgr.getSysDate()));
            reqData.setMoney(param.getString("MONEY"));
            reqData.setRentTypeCode(rentTypeCode);
            reqData.setRentSerialNumber(rentInfo.getString("PARA_CODE3"));
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new RentMobileReqData();
    }

}
