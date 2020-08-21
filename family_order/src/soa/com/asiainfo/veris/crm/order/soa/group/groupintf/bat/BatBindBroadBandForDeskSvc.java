
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.bat.BatTradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr.BindBroadBandForDeskMemQry;

public class BatBindBroadBandForDeskSvc extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.BindBroadBandMgrSvc.crtTrade";

    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
    	
        String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); //IMS固话号码
        String kdSerialNumber = IDataUtil.getMandaData(batData, "DATA1");//绑定的宽带账号
        String batId = IDataUtil.getMandaData(batData, "BATCH_ID");//批量号
        
        //校验IMS固话号码有重复的,拦截抛错
        int count = BatTradeInfoQry.queryBatDealCntBySerialNumber(batId,serialNumber);
        if(count > 1)
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "此次批量业务中存在重复服务号码" + serialNumber + "!");
        }
        //校验绑定宽带账号有重复的,拦截抛错
        int cnt = BatTradeInfoQry.queryBatDealCntByData1(batId,kdSerialNumber);
        if(cnt > 1)
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "此次批量业务中存在重复的绑定宽带账号" + kdSerialNumber + "!");
        }
        
        validateImsNumber(batData);
        
        validateKdNumber(batData);
        
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
    	String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); //IMS固话号码
        String kdSerialNumber = IDataUtil.getMandaData(batData, "DATA1");//绑定的宽带账号
        if(StringUtils.isNotBlank(kdSerialNumber) && !kdSerialNumber.startsWith("KD_"))
        {
        	kdSerialNumber = "KD_" + kdSerialNumber;
        }
        
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("KD_SERIAL_NUMBER", kdSerialNumber);
        svcData.put("BIND_TAG", "0");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
    }

    
    /**
     * IMS固话的号码信息校验
     * @param batData
     * @throws Exception
     */
    public void validateImsNumber(IData batData) throws Exception
    {
    	String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); //IMS固话号码
        
    	// 查询用户信息
        IDataset userDatas = UserInfoQry.getUserInfoBySn(serialNumber, "0");
        if (IDataUtil.isEmpty(userDatas))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "该号码用户信息不存在!");
        }
        
        IData userData = userDatas.getData(0);
        String userId = userData.getString("USER_ID");
        
        IDataset productInfos = UserProductInfoQry.queryUserMainProduct(userId);
        if(IDataUtil.isEmpty(productInfos))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该号码用户产品信息不存在!");
        }
        IData productData = productInfos.getData(0);
        
        //IMS语音
        if (!"IMSG".equals(productData.getString("BRAND_CODE")))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "只有IMS用户才能办理该业务!");
        }
        
        //是否订购了多媒体桌面电话
        IDataset userUUInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(userId, "S1");
        if(IDataUtil.isEmpty(userUUInfos))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该用户未订购多媒体桌面电话,不可办理该业务!");
        }
        
        //是否绑定了宽带账号
        IDataset uuInfoDatas = RelaUUInfoQry.getRelaUUInfoByUserIdA(userId, "T9");
        if(IDataUtil.isNotEmpty(uuInfoDatas))
        {
        	String serialNumberB = uuInfoDatas.getData(0).getString("SERIAL_NUMBER_B","");
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该用户已经绑定宽带账号" + serialNumberB + ",不可再办理该业务!");
        }
        
        // 查询客户信息
        String custId = userData.getString("CUST_ID","");
        IData custData = UcaInfoQry.qryCustInfoByCustId(custId);
        if (IDataUtil.isEmpty(custData))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的客户资料信息!");
        }
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "3754");
        IDataset checkResult = BindBroadBandForDeskMemQry.checkTradeBroadBand(param);// 检查是否有未完工工单
        if (IDataUtil.isNotEmpty(checkResult))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "用户有未完工的工单,正在处理请稍后!");
        }
    }
    
    /**
     * 宽带账号的信息校验
     * @param batData
     * @throws Exception
     */
    public void validateKdNumber(IData batData) throws Exception
    {
    	String kdSerialNumber = IDataUtil.getMandaData(batData, "DATA1");//绑定的宽带账号
    	
        if(StringUtils.isNotBlank(kdSerialNumber) && !kdSerialNumber.startsWith("KD_"))
        {
        	kdSerialNumber = "KD_" + kdSerialNumber;
        }
        
        //查询用户信息
        IDataset userDataset = UserInfoQry.getUserInfoBySn(kdSerialNumber, "0");
        if (IDataUtil.isEmpty(userDataset))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该宽带账号用户" + kdSerialNumber + "信息不存在!");
        }
        
        String userStateCode = userDataset.getData(0).getString("USER_STATE_CODESET","");
        if(!"0".equals(userStateCode))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该宽带用户状态不正常!");
        }
        
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", kdSerialNumber);
        IDataset kdUserInfos = CSAppCall.call("SS.BindBroadBandMgrSvc.queryBroadBandInfoByNumber", data);
        
        if (IDataUtil.isEmpty(kdUserInfos))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的宽带资料信息!");
        }
        if(IDataUtil.isNotEmpty(kdUserInfos) && kdUserInfos.size() > 1)
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "获取该用户的宽带资料信息异常!");
        }
        
        IDataset kdUuInfos = RelaUUInfoQry.queryRelaUUBySnb(kdSerialNumber, "T9");
        if(IDataUtil.isNotEmpty(kdUuInfos))
        {
        	String serialNumberA = kdUuInfos.getData(0).getString("SERIAL_NUMBER_A","");
        	String serialNumberB = kdUuInfos.getData(0).getString("SERIAL_NUMBER_B","");
        	String message = "该宽带账号" + serialNumberB + "已被" + serialNumberA + "绑定,不可再办理该业务!";
        	CSAppException.apperr(GrpException.CRM_GRP_713, message);
        }
        
        IData kdUserInfo = kdUserInfos.getData(0);
        String widenetType = kdUserInfo.getString("RSRV_STR2","");
        if(!"3".equals(widenetType) && !"5".equals(widenetType))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "只能绑定FTTH类型宽带!");
        }
        
    }
 
}
