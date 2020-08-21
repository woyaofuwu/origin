
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;

public class BatChgCustTradeChangeTrans implements ITrans
{
    private static Logger logger = Logger.getLogger(BatChgCustTradeChangeTrans.class);

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 初始化数据

        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);

        // 根据条件判断调用服务
        setSVC(batData);
    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IData svcData = batData.getData("svcData", new DataMap());
        
        String groupId = batData.getString("DATA1","");
        //String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String data2 = batData.getString("DATA2","");//CALLING_TYPE_CODE   行业类型
        String data3 = batData.getString("DATA3","");//SUB_CALLING_TYPE_CODE   子行业类型
        String data4 = batData.getString("DATA4","");//RSRV_STR1   归属本省行业科室
        String data5 = batData.getString("DATA5","");//ENTERPRISE_TYPE_CODE  企业类型
        String data6 = batData.getString("DATA6","");//ORG_TYPE_A   机构大类
        String data7 = batData.getString("DATA7","");//ORG_TYPE_B   机构中类
        String data8 = batData.getString("DATA8","");//ORG_TYPE_C   机构小类
       // String mebUserId = mebUserInfo.getString("USER_ID");
        // 拼调服务所需参数
        svcData.put("GROUP_ID",groupId);
        svcData.put("CALLING_TYPE_CODE", data2);
        svcData.put("SUB_CALLING_TYPE_CODE", data3);
        svcData.put("RSRV_STR1", data4);
        svcData.put("ENTERPRISE_TYPE_CODE", data5);
        svcData.put("ORG_TYPE_A", data6);
        svcData.put("ORG_TYPE_B", data7);
        svcData.put("ORG_TYPE_C", data8);
    }
    protected void builderSvcData(IData batData) throws Exception
    {
    }
    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData svcData = batData.getData("svcData", new DataMap());
        svcName = "CCF.outsvc.ICCOutOperateSV.modifyGroupInfoForBat";//调用客管接口
        svcData.put("REAL_SVC_NAME", svcName);
        svcData.put(Route.ROUTE_EPARCHY_CODE, "0898");
    }

}
