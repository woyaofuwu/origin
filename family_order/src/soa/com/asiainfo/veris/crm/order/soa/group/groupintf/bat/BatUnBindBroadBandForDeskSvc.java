
package com.asiainfo.veris.crm.order.soa.group.groupintf.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bat.GroupBatService;
import com.asiainfo.veris.crm.order.soa.group.bindbroadbandmgr.BindBroadBandForDeskMemQry;

public class BatUnBindBroadBandForDeskSvc extends GroupBatService
{
    private static final long serialVersionUID = 1L;

    private static final String SERVICE_NAME = "SS.BindBroadBandMgrSvc.unCrtTrade";

    private String kdSerialNumber = "";
    
    @Override
    public void batInitialSub(IData batData) throws Exception
    {
        svcName = SERVICE_NAME;
    }

    @Override
    public void batValidateSub(IData batData) throws Exception
    {
    	IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); //IMS固话号码

        validateImsNumber(batData);
    }

    @Override
    public void builderSvcData(IData batData) throws Exception
    {
    	String serialNumber = IDataUtil.getMandaData(batData, "SERIAL_NUMBER"); //IMS固话号码
        svcData.put("SERIAL_NUMBER", serialNumber);
        svcData.put("KD_SERIAL_NUMBER", kdSerialNumber);
        svcData.put("BIND_TAG", "1");
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
        
        //是否绑定了宽带账号
        IDataset uuInfoDatas = RelaUUInfoQry.getRelaUUInfoByUserIdA(userId, "T9");
        if(IDataUtil.isEmpty(uuInfoDatas))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "该IMS固话" + serialNumber + "未绑定宽带账号,不可办理该业务!");
        }
        if(IDataUtil.isNotEmpty(uuInfoDatas) && uuInfoDatas.size() > 1)
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "请确认该IMS固话" + serialNumber + "绑定宽带账号的数量是否正确!");
        }
        
        IData uuInfoData = uuInfoDatas.getData(0);
        kdSerialNumber = uuInfoData.getString("SERIAL_NUMBER_B","");
        
        // 查询客户信息
        String custId = userData.getString("CUST_ID","");
        IData custData = UcaInfoQry.qryCustInfoByCustId(custId);
        if (IDataUtil.isEmpty(custData))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "未获取到该用户的客户资料信息!");
        }
        
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        param.put("TRADE_TYPE_CODE", "3756");
        IDataset checkResult = BindBroadBandForDeskMemQry.checkTradeBroadBand(param);// 检查是否有未完工工单
        if (IDataUtil.isNotEmpty(checkResult))
        {
        	CSAppException.apperr(GrpException.CRM_GRP_713, "用户有未完工的工单,正在处理请稍后!");
        }
        
    }
    
}
