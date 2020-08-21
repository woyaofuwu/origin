
package com.asiainfo.veris.crm.order.soa.group.groupintf.transtrade.ims;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.Utility;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.group.common.query.GrpImsInfoQuery;

public class CreateIMSGrpMem
{
    /*
     * @description 融合产品集团成员新增
     * @author liuzz
     * @date 2014/05/30
     */
    public static IDataset OperAddGrpMeb(IData data) throws Exception
    {
        String serialNumberB = IDataUtil.chkParam(data, "SERIAL_NUMBER");
        String userIdA = IDataUtil.chkParam(data, "USER_ID_A");

        // 查询集团、成员用户信息
        UcaData grpUcaData = UcaDataFactory.getUcaByUserId(userIdA);
        UcaData memUcaData = UcaDataFactory.getNormalUca(serialNumberB);
        String productId = grpUcaData.getProductId();
        // 产品元素
        IDataset discntListInfos = data.getDataset("LIST_INFOS");
        if (IDataUtil.isEmpty(discntListInfos) && GrpImsInfoQuery.VPN_GRP_PRODUCTID.equals(productId))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_66, "LIST_INFOS");// 接口参数检查，输入参数%s不存在
        }
        // 个性化参数
        IDataset attrLists = data.getDataset("PRODUCT_ATTR");
        if (IDataUtil.isEmpty(attrLists))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_65);// 获取参数无数据PRODUCT_ATTR
        }

        // 元素处理
        String servIdStr = "0"; // 成员产品服务id
        String mebProductId = "";
        String svcPackageId = "";
        if (GrpImsInfoQuery.VPN_GRP_PRODUCTID.equals(productId))
        {
            servIdStr = GrpImsInfoQuery.VPN_SVC_ID + "," + GrpImsInfoQuery.VPN_SVC_SHORT_CODE; // "860";
            mebProductId = GrpImsInfoQuery.VPN_MEB_PRODUCTID; // "800010";
            svcPackageId = GrpImsInfoQuery.VPN_SVC_PAKAGE_ID; // "80001001";
        }
        else if (GrpImsInfoQuery.SUPTEL_GRP_PRODUCTID.equals(productId))
        {
            servIdStr = GrpImsInfoQuery.SUPTEL_SVC_ID; // "630";
            mebProductId = GrpImsInfoQuery.SUPTEL_MEB_PRODUCTID; // "613001";
            svcPackageId = GrpImsInfoQuery.SUPTEL_MEBSVC_PAKAGEID; // "61300101";
        }
        else if (GrpImsInfoQuery.DESKTEL_GRP_PRODUCTID.equals(productId))
        {
            servIdStr = GrpImsInfoQuery.CNTRX_BASESVC + "," + GrpImsInfoQuery.ENUM_BASESVC; // "8171,8173";
            // 8172因为有服务参数，放后面处理
            mebProductId = GrpImsInfoQuery.DESKTEL_MEB_PRODUCTID; // "222201";
            svcPackageId = GrpImsInfoQuery.DESKTEL_MEBSVC_PAKAGEID; // "22220101";
        }
        else if (GrpImsInfoQuery.YHT_GRP_PRODUCTID.equals(productId))
        {
            servIdStr = GrpImsInfoQuery.YHT_SVC_ID; // "80161111";
            mebProductId = GrpImsInfoQuery.YHT_MEB_PRODUCTID; // "801611";
            svcPackageId = GrpImsInfoQuery.YHT_MEBSVC_PAKAGEID; // "80161101";
        }
        else
        {
            CSAppException.apperr(GrpException.CRM_GRP_72);//此集团不是融合产品用户，不能添加融合产品成员！     
        }
        
        // 服务处理
        IDataset packElements = new DatasetList();
        String[] services = servIdStr.split(",");
        for (int i = 0; i < services.length; i++)
        {
            IData pkgSvcData = new DataMap();
            pkgSvcData.put("PRODUCT_ID", mebProductId);
            pkgSvcData.put("PACKAGE_ID", svcPackageId);
            pkgSvcData.put("ELEMENT_ID", services[i]);
            pkgSvcData.put("ELEMENT_TYPE_CODE", "S");
            pkgSvcData.put("MODIFY_TAG", "0");// 新增
            pkgSvcData.put("START_DATE", SysDateMgr.getSysTime());
            pkgSvcData.put("END_DATE", SysDateMgr.getTheLastTime());

            packElements.add(pkgSvcData);

        }
        // 资费处理
        IDataset elementInfo = DealIMSCommon.setElementInfo(discntListInfos, grpUcaData, memUcaData, mebProductId);
        // 将资费服务放到同一个包中
        packElements.addAll(elementInfo);

        // 处理参数，返回处理结果
        IData conmmitData = DealIMSCommon.setProductParam(attrLists, grpUcaData, memUcaData, mebProductId, "");// 参数处理结束
        IDataset servElement = conmmitData.getDataset("ELEMENT_INFO");
        if (IDataUtil.isNotEmpty(servElement))
        {
            packElements.addAll(servElement);
        }
        conmmitData.put("ELEMENT_INFO", packElements);
        conmmitData.put("USER_ID", userIdA);
        conmmitData.put("SERIAL_NUMBER", memUcaData.getSerialNumber());
        if ("".equals(conmmitData.getString("MEM_ROLE_B", "")))
        {
            conmmitData.put("MEM_ROLE_B", "1");
        }
        conmmitData.put("PRODUCT_ID", grpUcaData.getProductId());
        conmmitData.put("EFFECT_NOW", "true");
        conmmitData.put("PLAN_TYPE_CODE", "P");
        conmmitData.put("IS_NEED_TRANS", false);

        IDataset dataset = new DatasetList();
        try
        {
            dataset = CSAppCall.call("CS.CreateGroupMemberSvc.createGroupMember", conmmitData);
        }
        catch (Throwable e)
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, Utility.getBottomException(e).getMessage());
        }
        return dataset;
    }
}
