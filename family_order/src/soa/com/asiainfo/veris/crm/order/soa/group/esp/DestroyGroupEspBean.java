package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizcommon.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.ailk.bizservice.query.UcaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUser;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupuser.DestroyGroupUserReqData;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;

public class DestroyGroupEspBean extends DestroyGroupUser{
	
	
    protected void makInit(IData map) throws Exception
    {
    	String serialNumber = null;
    	String userID = null;
        String custId = map.getString("CUST_ID", "");

        String productId = map.getString("PRODUCT_CODE");
        //产品ID系统编码转换集团编码
        String productCode = map.getString("PRODUCT_CODE");
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productCode, null);
        if (IDataUtil.isNotEmpty(attrBizInfoList)){
        	productId = attrBizInfoList.getData(0).getString("ATTR_CODE");
        }
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        IDataset userInfoList = GroupInfoQueryDAO.getUserInfo(inparam);
        IData userInfo = null;
        String rsrvValueCode = "ESPG";
        for(int k=0 ; k<userInfoList.size(); k++){
        	String userId = userInfoList.getData(k).getString("USER_ID","");
        	IDataset userOthers = UserOtherInfoQry.getUserOtherInfo(userId, rsrvValueCode);
        	if(userOthers!= null && userOthers.size() >0){
        		String rsrvStr4 = userOthers.getData(0).getString("RSRV_STR4");
        		if(rsrvStr4.equals(map.getString("PRODUCT_ORDER_ID"))){
        			userInfo = userInfoList.getData(k);
        			break;
        		}
        	}
        }
        if(null == userInfo){
        	CSAppException.apperr(CustException.CRM_CUST_56);
        }
        serialNumber = userInfo.getString("SERIAL_NUMBER");
        userID = userInfo.getString("USER_ID");
        map.put("SERIAL_NUMBER", serialNumber);
    	map.put("USER_ID", userID);
        map.put("CUST_ID", custId);
        map.put("PRODUCT_ID", productId);
        map.put("STAFF_ID", map.getString("OPERATOR_CODE"));
    	super.makInit(map);	
    }
    
    
    public  String setTradeTypeCode() throws Exception{
    	return "4686";
    }
	
}
