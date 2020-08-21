
package com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.buildrequest;

import com.ailk.common.data.IData;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.topsetbox.order.requestdata.TopSetBoxRequestData;

/**
 * Copyright: Copyright (c) 2014 Asiainfo-Linkage
 * 
 * @ClassName: BuildTopSetBoxRequestData.java
 * @Description:
 * @version: v1.0.0
 * @author: yxd
 * @date: 2014-8-4 下午9:59:09 Modification History: Date Author Version Description
 *        ------------------------------------------------------------* 2014-8-4 yxd v1.0.0 修改原因
 */
public class BuildTopSetBoxRequestData extends BaseBuilder implements IBuilder
{

    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {

        TopSetBoxRequestData tsbReqData = (TopSetBoxRequestData) brd;
        tsbReqData.setSerialNumber(param.getString("SERIAL_NUMBER"));
        tsbReqData.setUserAction(param.getString("USER_ACTION"));
        tsbReqData.setIptvCityCode(param.getString("IPTV_CITYCODE",""));
        tsbReqData.setOldResNo(param.getString("OLD_RESNO"));
        tsbReqData.setOldProductId(param.getString("OLD_PRODUCT"));
        tsbReqData.setOldBasePkgs(param.getString("OLD_BASE_PACKAGES"));
        tsbReqData.setOldOptionPkgs(param.getString("OLD_OPTION_PACKAGES"));
        tsbReqData.setResNo(param.getString("RES_NO"));
        tsbReqData.setResTypeCode(param.getString("RES_TYPE_CODE")); // 4
        tsbReqData.setResBrandCode(param.getString("RES_BRAND_CODE"));
        tsbReqData.setResBrandName(param.getString("RES_BRAND_NAME"));
        tsbReqData.setResKindCode(param.getString("RES_KIND_CODE"));
        tsbReqData.setResKindName(param.getString("RES_KIND_NAME"));
        tsbReqData.setResStateCode(param.getString("RES_STATE_CODE"));
        tsbReqData.setResStateName(param.getString("RES_STATE_NAME"));
        tsbReqData.setSupplyId(param.getString("RES_SUPPLY_COOPID"));
        tsbReqData.setResFee(Float.parseFloat(param.getString("RES_FEE","0")) * 100 + "");
        tsbReqData.setProductId(param.getString("PRODUCT_ID"));
        tsbReqData.setBasePkgs(param.getString("BASE_PACKAGES"));
        tsbReqData.setOptionPkgs(param.getString("OPTION_PACKAGES"));
        tsbReqData.setArtificalSericesTag(param.getString("Artificial_services"));
        tsbReqData.setWideAddr(param.getString("WIDE_ADDRESS"));
        tsbReqData.setRemark(param.getString("REMARK"));
        tsbReqData.setWideTradeId(param.getString("WIDE_TRADE_ID"));
        tsbReqData.setWideState(param.getString("WIDE_STATE"));
        tsbReqData.setIsHasSaleActive(param.getString("IS_HAS_SALE_ACTIVE",""));
        tsbReqData.setIsMergeWideUserCreate(param.getString("IS_MERGE_WIDE_USER_CREATE","0"));
        
        tsbReqData.setWorkStaffId(param.getString("WORK_STAFF_ID",""));//施工人员信息
        tsbReqData.setWorkDepatrId(param.getString("WORK_DEPART_ID",""));
        tsbReqData.setWorkCityCode(param.getString("WORK_CITY_CODE",""));

    }

    public BaseReqData getBlankRequestDataInstance()
    {
        return new TopSetBoxRequestData();
    }

}
