package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.changeproductnew.order.requestdata.WidenetProductRequestData;

public class BuildWidenetChangeProductRequestData extends com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.changeproduct.order.buildrequest.BuildChangeProduct implements IBuilder {

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception {
        super.buildBusiRequestData(param, brd);

        WidenetProductRequestData request = (WidenetProductRequestData) brd;

        request.setWideActivePayFee(param.getString("WIDE_ACTIVE_PAY_FEE", "0"));
        request.setYearDiscntRemainFee(param.getString("YEAR_DISCNT_REMAIN_FEE", "0"));
        request.setRemainFee(param.getString("REMAIN_FEE", "0"));
        request.setAcctReainFee(param.getString("ACCT_REMAIN_FEE", "0"));
        request.setReturnYearDiscntRemainFee(param.getString("RETURN_YEAR_DISCNT_REMAIN_FEE", "0"));

        String serialNumberGrp = param.getString("EC_SERIAL_NUMBER");

        if (StringUtils.isNotBlank(serialNumberGrp)) {
            IData productInfo = UcaInfoQry.qryUserMainProdInfoBySnForGrp(serialNumberGrp);
            if (IDataUtil.isNotEmpty(productInfo)) {
                // 如果是集团商务宽带用户，做一个标识
                if ("7341".equals(productInfo.getString("PRODUCT_ID"))) {
                    // request.setUserPasswd("123456");
                    // request.setRsrvStr10("BNBD");
                    //
                    // request.setBusinessWide(true);
                    // 中小企业快速商务宽带创建集团与成员受理的UU关系，入参
                    request.setEcSerialNumber(param.getString("SERIAL_NUMBER", ""));
                    request.setEcUserId(param.getString("EC_USER_ID", ""));
                    request.setIbsysId(param.getString("IBSYSID", ""));
                    request.setNodeId(param.getString("NODE_ID", ""));
                    request.setRecordNum(param.getString("RECORD_NUM", ""));
                    request.setBusiformId(param.getString("BUSIFORM_ID", ""));
                }
            }
        }
    }

    public BaseReqData getBlankRequestDataInstance() {
        // TODO Auto-generated method stub
        return new WidenetProductRequestData();
    }

}
