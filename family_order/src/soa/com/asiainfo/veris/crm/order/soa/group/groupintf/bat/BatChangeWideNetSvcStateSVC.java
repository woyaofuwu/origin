
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatChangeWideNetSvcStateSVC extends GroupBatService
{    
    private String service_name = ""; // 服务名

    private String getService_name()
    {
        return service_name;
    }

    protected void setService_name(String service_name)
    {
        this.service_name = service_name;
    }

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {

        checkParam(batData);

        String productId = condData.getString("PRODUCT_ID");

        String statecode=condData.getString("STATE_FLAG");
       
        setService_name("SS.ChangeWideNetStateSVC.crtTrade"); 
       
        svcName = getService_name();
        
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {

        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 服务号码

        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        chkGroupUCAByUserId(inparam);

      
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {        
        svcData.put("USER_ID", getGrpUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getGrpUcaData().getSerialNumber());
        svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("REMARK", batData.getString("REMARK", "集团停开机"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));
        
        String statecode=condData.getString("STATE_FLAG");
        
        if("STOP".equals(statecode))
        {
            svcData.put("USER_STATE_CODESET", "1") ;
            svcData.put("STATE_CODE", "0");
        }
        else if("OPEN".equals(statecode))
        {
            svcData.put("USER_STATE_CODESET", "0") ;
            svcData.put("STATE_CODE", "1");
        }


    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

//        ruleData.put("RULE_BIZ_TYPE_CODE", "chkBeforeForGrp");
//        ruleData.put("RULE_BIZ_KIND_CODE", "GrpMebDestory");
//        // 集团信息
//        ruleData.put("PRODUCT_ID", getGrpUcaData().getProductId());
//        ruleData.put("CUST_ID", getGrpUcaData().getCustId());
//        ruleData.put("USER_ID", getGrpUcaData().getUserId());

    }

    /**
     * 校验参数必填
     * 
     * @param batData
     * @throws Exception
     */
    public void checkParam(IData batData) throws Exception
    {

        IDataUtil.chkParam(condData, "PRODUCT_ID");

        IDataUtil.chkParam(condData, "USER_ID");

       // IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        
        IDataUtil.chkParam(condData, "STATE_FLAG");

    }
}
