
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.IntfIAGWException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class BatGrpMemCancelTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());
        
        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");
        String grpUserId =  IDataUtil.getMandaData(condData, "USER_ID");
        String grpProductId =  IDataUtil.chkParam(condData, "PRODUCT_ID");
        String brandCode = UProductInfoQry.getBrandCodeByProductId(grpProductId);//daidl
        IDataset blackWhiteInfo = new DatasetList();
        IData serviceInfo = new DataMap();
        if (isOutNet&&("ADCG".equals(brandCode) || "MASG".equals(brandCode)))
        {// 网外号码
        	
            blackWhiteInfo = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, grpUserId);// 查sn在blackwhite的记录
            String mebservid =blackWhiteInfo.getData(0).getString("SERVICE_ID", "");
            String grpservid =MemParams.getServIdByGrpORmebServId(mebservid);
            serviceInfo.put("OPER_TYPE", "02");
            serviceInfo.put("MEB_USER_ID", blackWhiteInfo.getData(0).getString("USER_ID", "")); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "1"); // 删除
            serviceInfo.put("PRODUCT_ID", grpProductId);
            serviceInfo.put("SERVICE_ID", grpservid);
            
            svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));// 集团产品ID
            svcData.put("MEB_USER_ID", serviceInfo.getString("MEB_USER_ID", "-1"));// 成员USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.chkParam(batData, "SERIAL_NUMBER"));// 成员用户号码
            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团用户标识
            svcData.put("SERVICE_INFOS", new DatasetList(serviceInfo));// 服务参数信息
            svcData.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
            svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE"));
        }
        else
        {
            svcData.put("USER_ID", condData.getString("USER_ID"));
            svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));
            svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID"));
            svcData.put("REMARK", batData.getString("REMARK", "批量成员注销"));
            svcData.put("IN_MODE_CODE", batData.getString("IN_MODE_CODE"));
        }

        // 是否退出对应的集团资料， 0 不退出 1 退出
        String joinIn = condData.getString("JOIN_IN");
        if (StringUtils.isNotBlank(joinIn))
            svcData.put("JOIN_IN", joinIn);

        // 业务是否预约 true 预约到账期末执行 false 非预约工单
        String ifBooking = condData.getString("ifBooking");
        if (StringUtils.isNotBlank(ifBooking))
            svcData.put("IF_BOOKING", ifBooking);

        // modify by lixiuyu@20110613 集团v网批量业务发短信提醒和短信办理V网成员新增优化的需求
        if ("8000".equals(condData.getString("PRODUCT_ID")))
        {
            // 是否发送短信
            svcData.put("IF_SMS", "true");
        }
        else
        {
            // 是否发送短信
            svcData.put("IF_SMS", "false");
        }

    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        String productId= IDataUtil.chkParam(condData, "PRODUCT_ID");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 网外号码
            condData.put("IS_OUT_NET", true);
        }
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        if ("ADCG".equals(brandCode) || "MASG".equals(brandCode))
        {
        IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, grpUserId);// 查sn在blackwhite的记录
        boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

        if (!isBW)
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
        }
        }
    }

}
