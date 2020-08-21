package com.asiainfo.veris.crm.order.soa.group.imscreditmgr;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * IMS集团多媒体桌面电话成员的信控停机、信控开机
 * @author think
 *
 */
public class CreditImsMebDesktopTelBatSVC extends GroupBatService
{
    
    private Logger logger = Logger.getLogger(CreditImsMebDesktopTelBatSVC.class);
    
    private static final long serialVersionUID = -6470120766947944749L;

    private String serviceName = ""; // 服务名

    private String changeFlag = "";
    
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

        if(logger.isDebugEnabled())
        {
            logger.debug("<<<<<<服务参数：batData = " + batData);
        }
        
        checkParam(batData);
        IDataUtil.chkParam(condData, "STATE_FLAG");

        changeFlag = condData.getString("STATE_FLAG");//停开机标识
        
        if("OPEN".equals(changeFlag))//开机
        {
            setServiceName("SS.ChangeImsMebDesktopTelElementSvc.crtTrade"); // 开机
        }
        else if("STOP".equals(changeFlag))
        {
            setServiceName("SS.ChangeImsMebDesktopTelElementSvc.crtTrade"); // 停机
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_105);
        }
       
        svcName = getServiceName();

    }

    @Override
    protected void batValidateSub(IData batData) throws Exception
    {

        String userIdA = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID

        String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        
        // 校验集团三户信息
        IData inparam = new DataMap();
        inparam.put("USER_ID", userIdA);
                
        // 查询集团用户资料
//        IData grpUserInfo = UcaInfoQry.qryUserInfoByUserIdForGrp(userIdA);
//        if (IDataUtil.isEmpty(grpUserInfo))
//        {
//            CSAppException.apperr(GrpException.CRM_GRP_715);
//        }
//        String grpNumber = grpUserInfo.getString("SERIAL_NUMBER","");
//        inparam.clear();
//        inparam.put("SERIAL_NUMBER", grpNumber);//集团产品编码
//        chkGroupUCABySerialNumber(inparam);
        chkGroupUCAByUserId(inparam);

        // 校验成员三户信息
        inparam.clear();
        inparam.put("SERIAL_NUMBER", serialNumber);
        chkMemberUCABySerialNumber(inparam);

        // 判断成员号码是否为集团号码
        if (isGroupSerialNumber(serialNumber))
        {
            CSAppException.apperr(GrpException.CRM_GRP_120, serialNumber);
        }

        // 校验是否是集团成员
        IData data = new DataMap();
        String userIdb = getMebUcaData().getUserId(); // 成员user_id
                
        data.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
        data.put("USER_ID", userIdA);// 集团用户编码
        data.put("MEM_USER_ID", userIdb);// 成员用户编码
        data.put("MEM_EPARCHY_CODE", getMebUcaData().getUser().getEparchyCode());// 成员用户地州
        data.getString("MEM_EPARCHY_CODE");// 成员用户地州
        
        
        IDataset relaList = null;
        relaList = RelaUUInfoQry.getUUInfoByUserIdAB(userIdb,userIdA,"S1");

        if (IDataUtil.isEmpty(relaList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_51);
        }
        
        if(logger.isDebugEnabled())
        {
            logger.debug("<<<<<<服务参数：batData = " + batData);
        }
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
    	String serialNumber = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
    	
        //String userId = getMebUcaData().getUserId();//成员userId
    	String userIdA = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        svcData.put("USER_ID", userIdA);
        
        svcData.put("GRP_SERIAL_NUMBER", getGrpUcaData().getSerialNumber());//集团产品编码
        svcData.put("SERIAL_NUMBER", serialNumber);// 成员服务号码
        svcData.put("REMARK", batData.getString("REMARK", "集团多媒体桌面电话成员的批量信控停开机"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        String tradeTypeCode = "";
        changeFlag = condData.getString("STATE_FLAG");//停开机标识
        if("OPEN".equals(changeFlag))//信控缴费开机
        {
        	tradeTypeCode = "3916";
        	svcData.put("TRADE_TYPE_CODE", tradeTypeCode);
        	svcData.put("USER_STATE_CODESET", "0") ;
            svcData.put("STATE_CODE", "5");
        }
        else if("STOP".equals(changeFlag))//信控欠费停机
        {
        	tradeTypeCode = "3917";
        	svcData.put("TRADE_TYPE_CODE", tradeTypeCode);
        	svcData.put("USER_STATE_CODESET", "5") ;
            svcData.put("STATE_CODE", "0");
        }
        else
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "未知的集团多媒体桌面电话执行动作!");
        }
        
        if(logger.isDebugEnabled())
        {
            logger.debug("<<<<<<服务参数：batData = " + batData);
            logger.debug("<<<<<<服务参数：svcData = " + svcData);
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

        IDataUtil.chkParam(batData, "SERIAL_NUMBER");

    }
}
