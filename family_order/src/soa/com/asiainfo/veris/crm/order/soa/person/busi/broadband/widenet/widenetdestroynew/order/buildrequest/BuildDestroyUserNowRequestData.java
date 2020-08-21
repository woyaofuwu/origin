package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.buildrequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetdestroynew.order.requestdata.DestroyUserNowRequestData;

public class BuildDestroyUserNowRequestData extends BaseBuilder implements IBuilder {
    private static final Logger logger = Logger.getLogger(BuildDestroyUserNowRequestData.class);

    public void buildBusiRequestData(IData data, BaseReqData brd) throws Exception {
        DestroyUserNowRequestData reqData = (DestroyUserNowRequestData) brd;
        reqData.setRemove_reason_code(data.getString("REMOVE_REASON_CODE", "")); // 销户原因
        reqData.setRemove_reason(data.getString("REMOVE_REASON", ""));
        reqData.setScore(data.getString("SCORE", "0"));
        reqData.setYHFH(data.getBoolean("IS_YHFH_USER", false));// 是否影号副号

        String serialNumber = data.getString("AUTH_SERIAL_NUMBER", "");
        if (StringUtils.isBlank(serialNumber)) { // 接口过来不传AUTH_SERIAL_NUMBER的情况，直接取SERIAL_NUMBER
            data.put("AUTH_SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
            serialNumber = data.getString("SERIAL_NUMBER", "");
        }
        if (serialNumber.indexOf("KD_") > -1) {// 宽带账号
            if (serialNumber.split("_")[1].length() > 11)
                reqData.setSerialNumberA(serialNumber);// 商务宽带
            else
                reqData.setSerialNumberA(serialNumber.split("_")[1]);// 个人账号
        } else {
            if (serialNumber.length() > 11)
                reqData.setSerialNumberA("KD_" + serialNumber);// 商务宽带
            else
                reqData.setSerialNumberA(serialNumber);
        }
        reqData.setModemFee(data.getString("MODEM_FEE", "0"));
        reqData.setModermReturn(data.getString("MODEM_RETUAN", "0")); // 默认不退
        reqData.setModemMode(data.getString("MODEM_MODE", "")); // 默认空
        reqData.setModemFeeState(data.getString("MODEM_FEE_STATE", "")); // 默认空
        reqData.setWideType(data.getString("WIDE_TYPE_CODE", "1"));
        /**
         * REQ201609280002 宽带功能优化 chenxy3 2016-11-29
         * */
        reqData.setDestoryReason(data.getString("DESTORYREASON", ""));
        reqData.setReasonElse(data.getString("REASONELSE", ""));
        reqData.setIMSTag(data.getString("IMS_TAG", "")); // IMS固话标识
        reqData.setIMSSerialNumber(data.getString("IMS_SERIAL_NUMBER", "")); // 固话号码
        reqData.setIMSBrand(data.getString("IMS_BRAND", ""));
        reqData.setIMSProduct(data.getString("IMS_PRODUCT", ""));

        String serialNumberGrp = data.getString("EC_SERIAL_NUMBER");

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
                    reqData.setEcSerialNumber(data.getString("SERIAL_NUMBER", ""));
                    reqData.setEcUserId(data.getString("EC_USER_ID", ""));
                    reqData.setIbsysId(data.getString("IBSYSID", ""));
                    reqData.setNodeId(data.getString("NODE_ID", ""));
                    reqData.setRecordNum(data.getString("RECORD_NUM", ""));
                    reqData.setBusiformId(data.getString("BUSIFORM_ID", ""));
                }
            }
        }

    }

    public BaseReqData getBlankRequestDataInstance() {
        return new DestroyUserNowRequestData();
    }

}
