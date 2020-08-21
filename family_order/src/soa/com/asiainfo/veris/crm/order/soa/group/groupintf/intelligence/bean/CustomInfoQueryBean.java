package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import org.apache.commons.collections.MapUtils;

public class CustomInfoQueryBean extends CSBizBean {

    public IData customInfoQuery(IData inParams) throws Exception{
        String custId =   IDataUtil.chkParam( inParams , "customerId" );
        IData custInfo = UcaInfoQry.qryGrpInfoByGrpId( custId );
        IData custReturnInfo = new DataMap();
        if(MapUtils.isEmpty( custInfo ) ){
            CSAppException.apperr(GrpException.CRM_GRP_713,
                    "根据客户customerId："+custId+"查询客户资料不存在！");
        }
        custReturnInfo.put("customerNumber",custInfo.getString("GROUP_ID")); //对应集团编码
        custReturnInfo.put("customerID",custInfo.getString("CUST_ID"));
        custReturnInfo.put("customerStatus",custInfo.getString("REMOVE_TAG").equals("0") ? "1":"2" );
        custReturnInfo.put("customerName",custInfo.getString("CUST_NAME"));
        custReturnInfo.put("createTime",custInfo.getString("UPDATE_TIME").replaceAll("-","")
                .replaceAll(":","").replaceAll(" ",""));
        String classId =   custInfo.getString("CLASS_ID");
        String customerLevel = StaticInfoQry.getStaticInfoByTypeIdDataId("CUSTGROUP_CLASSID",classId).
                getString("DATA_NAME");
        custReturnInfo.put("customerLevel",  customerLevel );
        custReturnInfo.put("customerAddress",custInfo.getString("GROUP_ADDR"));
        custReturnInfo.put("contactsInfo",custInfo.getString("GROUP_CONTACT_PHONE"));
        custReturnInfo.put("customerManagerStaffNO",custInfo.getString("CUST_MANAGER_ID"));
        return custReturnInfo;
    }

}
