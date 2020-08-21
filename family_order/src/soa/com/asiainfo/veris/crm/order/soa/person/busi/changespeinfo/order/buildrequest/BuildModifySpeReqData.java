
package com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.changespeinfo.order.requestdata.ModifySpeInfoReqData;

public class BuildModifySpeReqData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData inParam, BaseReqData brd) throws Exception
    {
        ModifySpeInfoReqData reqData = (ModifySpeInfoReqData) brd;
        String oldCityCode = reqData.getUca().getUser().getCityCode();
        String oldUserTypeCode = reqData.getUca().getUser().getUserTypeCode();
        String oldDevelopStaffId = reqData.getUca().getUser().getDevelopStaffId();
        String oldBrandCode = inParam.getString("OLD_BRAND_CODE");
        String oldUserState = inParam.getString("OLD_USER_STATE_CODESET");
        String oldSimCardNo = inParam.getString("OLD_SIM_CARD_NO");
        String oldImsi = inParam.getString("OLD_IMSI");

        reqData.setCityCode(StringUtils.equals(inParam.getString("CITY_CODE"), oldCityCode) ? "" : inParam.getString("CITY_CODE"));
        reqData.setUserStateCode(StringUtils.equals(inParam.getString("USER_STATE_CODESET"), oldUserState) ? "" : inParam.getString("USER_STATE_CODESET"));
        reqData.setDevelopStaffId(StringUtils.equals(inParam.getString("DEVELOP_STAFF_ID"), oldDevelopStaffId) ? "" : inParam.getString("DEVELOP_STAFF_ID"));
        reqData.setUserTypeCode(StringUtils.equals(inParam.getString("USER_TYPE_CODE"), oldUserTypeCode) ? "" : inParam.getString("USER_TYPE_CODE"));
        reqData.setBrandCode(StringUtils.equals(inParam.getString("BRAND_CODE"), oldBrandCode) ? "" : inParam.getString("BRAND_CODE"));
        reqData.setImsi(StringUtils.equals(inParam.getString("IMSI"), oldImsi) ? "" : inParam.getString("IMSI"));
        reqData.setSimCardNo(StringUtils.equals(inParam.getString("SIM_CARD_NO"), oldSimCardNo) ? "" : inParam.getString("SIM_CARD_NO"));
    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new ModifySpeInfoReqData();
    }

}
