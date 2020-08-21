
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

public abstract class UnBindBroadBandForDeskMem extends CSBasePage
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
        svcData.put("BIND_TAG", "1");
        svcData.put(Route.ROUTE_EPARCHY_CODE, getTradeEparchyCode());
        svcData.put(Route.USER_EPARCHY_CODE, getTradeEparchyCode());

        // 调用服务
        IDataset retDataset = CSViewCall.call(this, "SS.BindBroadBandMgrSvc.unCrtTrade", svcData);

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

        String serialNumber = condData.getString("SERIAL_NUMBER");
        String kdSerialNumber = condData.getString("KD_SERIAL_NUMBER", "");
        if(StringUtils.isBlank(serialNumber))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "该IMS固话号码为空!");
        }
        
        if(StringUtils.isBlank(kdSerialNumber))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "已绑定的宽带账号为空!");
        }
        
        String userId = condData.getString("USER_ID", "");
        if(StringUtils.isNotBlank(userId))
        {
        	//是否绑定了宽带账号
            IDataset userUUInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this,userId,"T9");
            if(IDataUtil.isEmpty(userUUInfos))
            {
            	 CSViewException.apperr(GrpException.CRM_GRP_713, "该用户未绑定宽带账号,不可办理该业务!");
            }
        }
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "3756");
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

        //是否绑定了宽带账号
        IDataset userUUInfos = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(this,userId,"T9");
        if(IDataUtil.isEmpty(userUUInfos))
        {
        	 CSViewException.apperr(GrpException.CRM_GRP_713, "该用户未绑定宽带账号,不可办理该业务!");
        }
        if(IDataUtil.isNotEmpty(userUUInfos) && userUUInfos.size() > 1)
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "请确认该用户绑定宽带账号的数量是否正确!");
        }
        
        // 设置返回值
        IData userInfoData = new DataMap();
        
        IData userUUInfo = userUUInfos.getData(0);
        if(IDataUtil.isNotEmpty(userUUInfo))
        {
        	String serialNumberB = userUUInfo.getString("SERIAL_NUMBER_B","");
        	String userIdB = userUUInfo.getString("USER_ID_B","");
        	userInfoData.put("SERIAL_NUMBER_B", serialNumberB);
        	userInfoData.put("USER_ID_B", userIdB);
        }
        
        // 查询客户信息
        String custId = userData.getString("CUST_ID","");
        String eparchyCode = userData.getString("EPARCHY_CODE", "");
        IData custData = UCAInfoIntfViewUtil.qryMebCustInfoByCustIdAndRoute(this,custId,eparchyCode,false);
        if (IDataUtil.isEmpty(custData))
        {
        	CSViewException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的客户资料信息!");
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
    
    public abstract void setCondition(IData condition);

    public abstract void setKdUserInfo(IData kdUserInfo);
    
    public abstract void setImsUserInfo(IData imsUserInfo);
    

}
