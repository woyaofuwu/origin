
package com.asiainfo.veris.crm.order.web.group.bindbroadbandmgr;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;

public abstract class ChBindBroadBandForDeskMem extends CSBasePage
{

    /**
     * 提交方法
     * 
     * @param cycle
     * @throws Exception
     */
    public void onSubmitBaseTrade(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        // 业务受理前校验
        onSubmitBaseTradeCheck(cycle);

        // 调用服务数据
        IData svcData = new DataMap();
        svcData.put("SERIAL_NUMBER", condData.getString("SERIAL_NUMBER"));
        svcData.put("KD_SERIAL_NUMBER", condData.getString("KD_SERIAL_NUMBER"));
        svcData.put("OLDKD_SERIAL_NUMBER", condData.getString("OLDKD_SERIAL_NUMBER"));
        svcData.put("BIND_TAG", "2");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.BindBroadBandMgrSvc.chCrtTrade", svcData);

        // 设置返回数据
        setAjax(retDataset);
    }

    /**
     * 业务受理前的校验
     * 
     * @param cycle
     * @throws Exception
     */
    private void onSubmitBaseTradeCheck(IRequestCycle cycle) throws Exception
    {
        IData condData = getData();

        String userId = condData.getString("USER_ID","");
        String serialNumber = condData.getString("SERIAL_NUMBER","");
        String kdSerialNumber = condData.getString("KD_SERIAL_NUMBER", "");
        String oldkdSerialNumber = condData.getString("OLDKD_SERIAL_NUMBER", "");
        if(StringUtils.isBlank(serialNumber))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "该IMS固话号码为空!");
        }
        
        if(StringUtils.isBlank(kdSerialNumber))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "需要绑定的宽带账号为空!");
        }
        
        //查询用户信息
        IData userData = UCAInfoIntfViewUtil.qryUserInfoBySn(this, kdSerialNumber, false);
        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该宽带账号用户" + kdSerialNumber + "信息不存在!");
        }
        
        String userStateCode = userData.getString("USER_STATE_CODESET","");
        if(!"0".equals(userStateCode))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "该宽带用户状态不正常!");
        }
        
        //是否绑定了宽带账号
        IDataset uuInfoDatas = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this,userId,"T9");
        if(IDataUtil.isNotEmpty(uuInfoDatas))
        {
        	String serialNumberB = uuInfoDatas.getData(0).getString("SERIAL_NUMBER_B","");
        	if(!(serialNumberB.trim().equals(oldkdSerialNumber.trim())))
        	{
        		String message = "该用户" + serialNumber + "提交的宽带账号" + oldkdSerialNumber + "与获取的宽带账号" + serialNumberB + "不一致!";
        		CSViewException.apperr(GrpException.CRM_GRP_713, message);
        	}
        }
        
        String eparchyCode = getTradeEparchyCode();
        IDataset kdUuInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosBySerialNumberBAndRelationTypeCode(this,kdSerialNumber,"T9",eparchyCode);
        if(IDataUtil.isNotEmpty(kdUuInfos))
        {
        	String serialNumberA = kdUuInfos.getData(0).getString("SERIAL_NUMBER_A","");
        	String serialNumberB = kdUuInfos.getData(0).getString("SERIAL_NUMBER_B","");
        	String message = "该用户" + serialNumberA + "已被宽带账号" + serialNumberB + "绑定,不可再办理该业务!!";
        	CSViewException.apperr(GrpException.CRM_GRP_713, message);
        }
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "3755");
        IDataset checkResult = CSViewCall.call(this, "SS.BindBroadBandMgrSvc.checkTradeBroadBand", param); // 检查是否有未完工工单
        if (IDataUtil.isNotEmpty(checkResult))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "用户有未完工的工单,正在处理请稍后!");
        }
    }

    /**
     * 查询IMS固话用户信息
     * @param cycle
     * @throws Exception
     */
    public void qryImsPhoneNumInfo(IRequestCycle cycle) throws Exception
    {
        IData condData = getData("cond", true);

        String serialNumber = condData.getString("SERIAL_NUMBER");

        // 查询用户信息
        IData userData = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serialNumber, false);
        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该号码用户信息不存在!");
        }

        //IMS语音
        if (!"IMSG".equals(userData.getString("BRAND_CODE")))
        {
        	 CSViewException.apperr(GrpException.CRM_GRP_713, "只有IMS用户才能办理该业务!");
        }
        
        String userId = userData.getString("USER_ID");

        //是否订购了多媒体桌面电话
        IDataset userUUInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdBAndRelationTypeCode(this,userId,"S1",false);
        if(IDataUtil.isEmpty(userUUInfos))
        {
        	 CSViewException.apperr(GrpException.CRM_GRP_713, "该用户未订购多媒体桌面电话,不可办理该业务!");
        }

        //是否绑定了宽带账号
        IDataset userKdInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this,userId,"T9");
        if(IDataUtil.isEmpty(userKdInfos))
        {
        	 CSViewException.apperr(GrpException.CRM_GRP_713, "该用户未绑定宽带账号,不可办理该业务!");
        }
        if(IDataUtil.isNotEmpty(userKdInfos) && userKdInfos.size() > 1)
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "请确认该用户绑定宽带账号的数量是否正确!");
        }
        
        // 查询客户信息
        String custId = userData.getString("CUST_ID","");
        String eparchyCode = userData.getString("EPARCHY_CODE", "");
        IData custData = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(this,custId,eparchyCode,false);
        //UcaInfoQry.qryCustInfoByCustId();
        if (IDataUtil.isEmpty(custData))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的客户资料信息!");
        }

        // 设置返回值
        IData userInfoData = new DataMap();
        
        IData userKdInfo = userKdInfos.getData(0);
        if(IDataUtil.isNotEmpty(userKdInfo))
        {
        	String serialNumberB = userKdInfo.getString("SERIAL_NUMBER_B","");
        	String userIdB = userKdInfo.getString("USER_ID_B","");
        	userInfoData.put("SERIAL_NUMBER_B", serialNumberB);
        	userInfoData.put("USER_ID_B", userIdB);
        }
        
        userInfoData.put("USER_ID", userData.getString("USER_ID"));
        userInfoData.put("CUST_ID", custId);
        userInfoData.put("SERIAL_NUMBER", serialNumber);
        userInfoData.put("CUST_NAME", custData.getString("CUST_NAME"));
        userInfoData.put("PSPT_TYPE_NAME", custData.getString("PSPT_TYPE_NAME"));
        userInfoData.put("PSPT_TYPE_CODE", custData.getString("PSPT_TYPE_CODE"));
        userInfoData.put("PSPT_ID", custData.getString("PSPT_ID"));
        userInfoData.put("EPARCHY_NAME", custData.getString("EPARCHY_NAME"));
        
        setImsUserInfo(userInfoData);

        setCondition(getData());
    }

    /**
     * 查询宽带账号信息
     * @param cycle
     * @throws Exception
     */
    public void qryKdPhoneNumInfo(IRequestCycle cycle) throws Exception
    {
    	IData condData = getData("cond", true);

        String serialNumber = condData.getString("KD_SERIAL_NUMBER");
        if(StringUtils.isNotBlank(serialNumber) && !serialNumber.startsWith("KD_"))
        {
        	serialNumber = "KD_" + serialNumber;
        }
        condData.put("KD_SERIAL_NUMBER", serialNumber);
        
        //查询用户信息
        IData userData = UCAInfoIntfViewUtil.qryUserInfoBySn(this, serialNumber, false);
        if (IDataUtil.isEmpty(userData))
        {
            CSViewException.apperr(GrpException.CRM_GRP_713, "该宽带账号用户" + serialNumber + "信息不存在!");
        }
        
        String userStateCode = userData.getString("USER_STATE_CODESET","");
        if(!"0".equals(userStateCode))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "该宽带用户状态不正常!");
        }
        
        IData data = new DataMap();
        data.put("SERIAL_NUMBER", serialNumber);
        IDataset kdUserInfos = CSViewCall.call(this, "SS.BindBroadBandMgrSvc.queryBroadBandInfoByNumber", data);
        
        if (IDataUtil.isEmpty(kdUserInfos))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的宽带资料信息!");
        }
        if(IDataUtil.isNotEmpty(kdUserInfos) && kdUserInfos.size() > 1)
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "获取该用户的宽带资料信息异常!");
        }
        
        String eparchyCode = getTradeEparchyCode();
        IDataset kdUuInfos = RelationUUInfoIntfViewUtil.qryRelaUUInfosBySerialNumberBAndRelationTypeCode(this,serialNumber,"T9",eparchyCode);
        if(IDataUtil.isNotEmpty(kdUuInfos))
        {
        	String serialNumberA = kdUuInfos.getData(0).getString("SERIAL_NUMBER_A","");
        	String serialNumberB = kdUuInfos.getData(0).getString("SERIAL_NUMBER_B","");
        	String message = "该宽带账号" + serialNumberB + "已被" + serialNumberA + "绑定,不可再办理该业务!";
        	CSViewException.apperr(GrpException.CRM_GRP_713, message);
        }
        
        IData kdUserInfo = kdUserInfos.getData(0);
        
        String widenetType = kdUserInfo.getString("RSRV_STR2","");
        if(!"3".equals(widenetType) && !"5".equals(widenetType))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "只能绑定FTTH类型宽带!");
        }
        
        if("3".equals(widenetType))
        {
        	kdUserInfo.put("PRODUCT_NAME", "移动FTTH");
        }
        else if("5".equals(widenetType))
        {
        	kdUserInfo.put("PRODUCT_NAME", "铁通FTTH");
        }
        
        setKdUserInfo(kdUserInfo);
        getData().put("cond_KD_SERIAL_NUMBER", serialNumber);
        setCondition(getData());
    }
    
    public abstract void setCondition(IData condition);

    public abstract void setKdUserInfo(IData kdUserInfo);
    
    public abstract void setImsUserInfo(IData imsUserInfo);
    

}
