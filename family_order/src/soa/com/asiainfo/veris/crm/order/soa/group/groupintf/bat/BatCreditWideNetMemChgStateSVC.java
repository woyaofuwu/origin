package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.BroadBandException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;

/**
 * 集团商务宽带产品的成员的欠费停机、缴费开机
 * 信控
 * @author think
 *
 */
public class BatCreditWideNetMemChgStateSVC extends GroupBatService
{
    
    private Logger logger = Logger.getLogger(BatCreditWideNetMemChgStateSVC.class);
    
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
            setServiceName("SS.ChangeWidenetSvcStateRegSVC.tradeReg"); // 开机
        }
        else if("STOP".equals(changeFlag))
        {
            setServiceName("SS.ChangeWidenetSvcStateRegSVC.tradeReg"); // 停机
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
        data.getString("MEM_EPARCHY_CODE");// 成员用户地州
        
        
        IDataset relaList = null;
        relaList = RelaUUInfoQry.qryRelationUUOneForKDMem(user_id,user_id_b,"47");

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
        String userId = getMebUcaData().getUserId();
        svcData.put("USER_ID", userId);
        svcData.put("SERIAL_NUMBER", getMebUcaData().getSerialNumber());
        svcData.put("REMARK", batData.getString("REMARK", "集团商务宽带成员的批量信控停开机"));
        svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE", getVisit().getInModeCode()));

        String tradeTypeCode = "";
        changeFlag = condData.getString("STATE_FLAG");//停开机标识
        if("OPEN".equals(changeFlag))//信控缴费开机
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
            if(IDataUtil.isNotEmpty(widenetInfos))
            {
                String widenetType = widenetInfos.getData(0).getString("RSRV_STR2");
                if("1".equals(widenetType))
                {
                    tradeTypeCode = "7306";// GPON宽带缴费开机
                }
                else if ("2".equals(widenetType))
                {
                    tradeTypeCode = "7307";// ADSL宽带缴费开机
                }
                else if ("3".equals(widenetType))
                {
                    tradeTypeCode = "7308";// 光纤宽带缴费开机
                }
                else if("5".equals(widenetType))//铁通FTTH
                {
                    tradeTypeCode = "7308";
                }
                else if("6".equals(widenetType))//铁通FTTB
                {
                    tradeTypeCode = "7306";
                }
                else 
                {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "集团商务宽带是没有校园宽带，故业务不能继续!");
                }
                svcData.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_22);
            }
        }
        else if("STOP".equals(changeFlag))//信控欠费停机
        {
            IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfo(userId);
            if(IDataUtil.isNotEmpty(widenetInfos))
            {
                String widenetType = widenetInfos.getData(0).getString("RSRV_STR2");
                if("1".equals(widenetType))
                {
                    tradeTypeCode = "7221";// GPON宽带欠费停机
                }
                else if ("2".equals(widenetType))
                {
                    tradeTypeCode = "7222";// ADSL宽带欠费停机
                }
                else if ("3".equals(widenetType))
                {
                    tradeTypeCode = "7223";// 光纤宽带欠费停机
                }
                else if("5".equals(widenetType))//铁通FTTH
                {
                    tradeTypeCode = "7223";
                }
                else if("6".equals(widenetType))//铁通FTTB
                {
                    tradeTypeCode = "7221";
                }
                else 
                {
                    CSAppException.apperr(GrpException.CRM_GRP_713, "集团商务宽带是没有校园宽带，故业务不能继续!");
                }
                svcData.put("TRADE_TYPE_CODE", tradeTypeCode);
            }
            else 
            {
                CSAppException.apperr(WidenetException.CRM_WIDENET_22);
            }            
        }
        else
        {
            CSAppException.apperr(BroadBandException.CRM_BROADBAND_105);
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
