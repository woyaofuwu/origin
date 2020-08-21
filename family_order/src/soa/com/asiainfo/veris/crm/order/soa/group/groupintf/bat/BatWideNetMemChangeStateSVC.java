
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

public class BatWideNetMemChangeStateSVC extends GroupBatService
{
    
    private static final long serialVersionUID = -6470120766947944749L;

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
        IDataUtil.chkParam(condData, "STATE_FLAG");

        //String productId = condData.getString("PRODUCT_ID");//集团产品
        //String serialNumber=condData.getString("SERIAL_NUMBER");
        String changeFlag=condData.getString("STATE_FLAG");//停开机标识
        
        if("OPEN".equals(changeFlag))//开机
        {
            setService_name("SS.OpenWidenetSvcStateRegSVC.tradeReg"); // 开机
        }
        else if("STOP".equals(changeFlag))
        {
            setService_name("SS.StopWidenetSvcStateRegSVC.tradeReg"); // 停机
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_105);
        }
       
        svcName = getService_name();

    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {

        String user_id = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serial_number = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        
        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", user_id);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serial_number);
        chkMemberUCABySerialNumber(inparam);

        // 判断服务号码状态
        //if (!"0".equals(getMebUcaData().getUser().getUserStateCodeset()))
        //{
        //    CSAppException.apperr(CrmUserException.CRM_USER_471, serial_number);
        //}

        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serial_number))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serial_number);
        }

        // 校验是否是集团成员
        IData data = new DataMap();
        String user_id_b = getMebUcaData().getUserId(); // 成员user_id
                
        data.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        data.put("USER_ID", user_id);// 集团用户编码
        data.put("MEM_USER_ID", user_id_b);// 成员用户编码
        data.put("MEM_EPARCHY_CODE", getMebUcaData().getUser().getEparchyCode());// 成员用户地州

        String routeId = data.getString("MEM_EPARCHY_CODE");// 成员用户地州
        
        
        IDataset relaList = null;
        relaList = RelaUUInfoQry.qryRelationUUOneForKDMem(user_id,user_id_b,"47");

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_51);
        }
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
        svcData.put("USER_ID", getMebUcaData().getUserId());
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        //svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        svcData.put("REMARK", batData.getString("REMARK", "集团成员批量暂停"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

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
//        // 成员信息
//        ruleData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
//        ruleData.put("USER_ID_B", getMebUcaData().getUserId());
//        ruleData.put("BRAND_CODE_B", getMebUcaData().getBrandCode());
//        ruleData.put("EPARCHY_CODE_B", getMebUcaData().getUser().getEparchyCode());
//        ruleData.put("PRODUCT_ID_B", getMebUcaData().getProductId());
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

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

    }
}
