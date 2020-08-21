package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * IMS集团多媒体桌面电话的信控停机、信控开机
 * 
 * @author think
 *
 */

public class CreditImsGrpDesktopTelBatSVC extends GroupBatService
{    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String serviceName = ""; // 服务名

    private String getServiceName()
    {
        return serviceName;
    }

    protected void setServiceName(String serviceName)
    {
        this.serviceName = serviceName;
    }

    @Override
    protected void batInitialSub(IData batData) throws Exception
    {
        checkParam(batData);
        setServiceName("SS.ChangeImsGrpDesktopTelStateSVC.crtTrade"); 
        svcName = getServiceName();
    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {
        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 服务号码

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
        svcData.put("REMARK", batData.getString("REMARK", "集团多媒体桌面电话停开机"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));
        
        String statecode = condData.getString("STATE_FLAG");
        
        if("STOP".equals(statecode))
        {
            svcData.put("USER_STATE_CODESET", "5") ;
            svcData.put("STATE_CODE", "0");
        }
        else if("OPEN".equals(statecode))
        {
            svcData.put("USER_STATE_CODESET", "0") ;
            svcData.put("STATE_CODE", "5");
        }

    }

    /**
     * 构造规则数据
     */
    @Override
    protected void builderRuleData(IData batData) throws Exception
    {
        super.builderRuleData(batData);

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
        IDataUtil.chkParam(condData, "STATE_FLAG");
    }
    
}
