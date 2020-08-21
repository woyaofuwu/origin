
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.bat;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.trans.ITrans;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class BatChgVpmnMebsvc862Trans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 校验请求参数
        checkRequestDataSub(batData);

        // 构建服务请求数据
        builderSvcData(batData);

    }

    protected void checkRequestDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        condData.put("PRODUCT_ID", "8000"); // j2ee 测试写死

        String memSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        // 查询用户信息
        IData userinfo = UserInfoQry.getUserInfoBySN(memSn);

        if (IDataUtil.isEmpty(userinfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_715);
        }

        // 校验成员是否为VPMN成员
        String memUserId = userinfo.getString("USER_ID");
        String relaTypeCode = "20"; // j2ee 测试写死

        IDataset uuInfos = RelaUUInfoQry.getRelaUUInfoByUserIdBAndRelaTypeCode(memUserId, relaTypeCode);
        if (IDataUtil.isEmpty(uuInfos))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_194, memSn);
        }
        String grpUserId = uuInfos.getData(0).getString("USER_ID_A");

        condData.put("USER_ID", grpUserId);
        batData.put("MEM_USER_ID", memUserId);
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());

        IData condData = batData.getData("condData", new DataMap());

        String productId = condData.getString("PRODUCT_ID");
        String grpUserId = condData.getString("USER_ID");
        String memUserId = batData.getString("MEM_USER_ID");

        svcData.put("USER_ID", grpUserId);
        svcData.put("PRODUCT_ID", productId);
        svcData.put("SERIAL_NUMBER", batData.getString("SERIAL_NUMBER"));

        // 组装元素
        String callOut = IDataUtil.chkParam(condData, "CALL_OUT");// 操作类型 0--新增；1--注销
        String callIn = IDataUtil.chkParam(condData, "CALL_IN");
        String callOutTag = IDataUtil.chkParam(condData, "CALL_OUT_TAG");
        String callInTag = IDataUtil.chkParam(condData, "CALL_IN_TAG");
        String callOutRight = IDataUtil.chkParam(condData, "CALL_OUT_RIGHT");
        String callInRight = IDataUtil.chkParam(condData, "CALL_IN_RIGHT");

        IDataset productElements = dealMemberSvcAttr(grpUserId, memUserId, callOut, callIn, callOutTag, callInTag, callOutRight, callInRight);
        svcData.put("ELEMENT_INFO", productElements);

    }

    /**
     * 处理成员服务和实例属性参数
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    public IDataset dealMemberSvcAttr(String grpUserId, String memUserId, String callOut, String callIn, String callOutTag, String callInTag, String callOutRight, String callInRight) throws Exception
    {
        IDataset groupSvcInfo = GrpUserPkgInfoQry.getGrpCustomizeServByUserId(grpUserId, null);
        if (IDataUtil.isEmpty(groupSvcInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_734);
        }
        IDataset svcInfos = GrpUserPkgInfoQry.getSvcElementByGrpUserIdandPackageId("80000104", grpUserId);
        // 判断成员所依赖的VPMN成员呼叫权限控制包是否已订购
        if ("1".equals(callOut) && "2".equals(callIn))
        {
            if (IDataUtil.isEmpty(svcInfos))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_195);
            }
            if (IDataUtil.isNotEmpty(svcInfos) && svcInfos.size() < 2)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_196);
            }
        }
        else if ("1".equals(callOut) && "0".equals(callIn))
        {
            if (IDataUtil.isEmpty(svcInfos))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_195);
            }
            boolean existFlag = false;
            for (int i = 0; i < svcInfos.size(); i++)
            {
                IData svcInfo = svcInfos.getData(i);
                if (svcInfo != null && svcInfo.size() > 0)
                {
                    String elementId = svcInfo.getString("ELEMENT_ID", "");
                    if ("862".equals(elementId))
                    {
                        existFlag = true;
                        break;
                    }
                }
            }
            if (!existFlag)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_197);
            }
        }
        else if ("0".equals(callOut) && "2".equals(callIn))
        {
            if (IDataUtil.isEmpty(svcInfos))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_195);
            }
            boolean existFlag = false;
            for (int i = 0; i < svcInfos.size(); i++)
            {
                IData svcInfo = svcInfos.getData(i);
                if (svcInfo != null && svcInfo.size() > 0)
                {
                    String elementId = svcInfo.getString("ELEMENT_ID", "");
                    if ("863".equals(elementId))
                    {
                        existFlag = true;
                        break;
                    }
                }
            }
            if (!existFlag)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_198);
            }
        }

        IDataset SVC = new DatasetList();
        IDataset PARAM = new DatasetList();

        /*
         * CALL_OUT 1表示限制呼叫VPMN外电话(主叫) 0不做处理 
         * CALL_IN 2表示限制接听VPMN外电话(被叫) 0不做处理 
         * CALL_OUT_TAG 0表注销;1表新增;2表修改; 3表不处理
         * CALL_OUT_TAG 0表注销;1表新增;2表修改; 3表不处理 
         * CALL_OUT_RIGHT 主叫:0一级默认;1二级(呼出限制);2三级(呼出限制) 
         * CALL_IN_RIGHT 被叫:0一级默认;1二级(呼入限制);2三级(呼入限制)
         */

        // 申请(新增)限制呼叫VPMN外电话(主叫)
        IDataset tmpSvc = UserSvcInfoQry.qryUserSvcByUserSvcId(memUserId, "862");
        IDataset params = UserAttrInfoQry.getUserProductAttrValue(memUserId, "S", "CALLOUTRIGHT");
        if ("1".equals(callOut) && "1".equals(callOutTag))
        {
            if (IDataUtil.isEmpty(tmpSvc))
            {
                IData svc862data = new DataMap();
                svc862data.put("START_DATE", SysDateMgr.getSysTime());
                svc862data.put("END_DATE", SysDateMgr.getTheLastTime()); // j2ee 时间还要算
                svc862data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                svc862data.put("ELEMENT_ID", "862");// 限制呼叫VPMN外电话
                svc862data.put("ELEMENT_TYPE_CODE", "S");
                svc862data.put("PACKAGE_ID", "80000104");
                svc862data.put("PRODUCT_ID", "800001");
                svc862data.put("INST_ID", "");
                if (IDataUtil.isEmpty(params))
                {
                    // 新增用户服务实例属性
                    IData attr862 = new DataMap();
                    attr862.put("ATTR_CODE", "CALLOUTRIGHT");
                    attr862.put("ATTR_VALUE", callOutRight);// 主叫:0一级默认;1二级(呼出限制);2三级(呼出限制)
                    PARAM.add(attr862);
                }
                else
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_199);
                }
                svc862data.put("ATTR_PARAM", PARAM);
                SVC.add(svc862data);
            }
            else
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_200);
            }

        }
        else if ("1".equals(callOut) && "0".equals(callOutTag))
        {
            // 取消(注销)限制呼叫VPMN外电话(主叫)
            if (IDataUtil.isEmpty(tmpSvc))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_201);
            }

            IData tmpSvcData = (IData) tmpSvc.get(0);
            tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));
            tmpSvcData.put("END_DATE", SysDateMgr.getSysTime());
            tmpSvcData.put("ELEMENT_TYPE_CODE", "S");
            if (IDataUtil.isNotEmpty(params))
            {// 注销服务实例参数
                for (int i = 0; i < params.size(); i++)
                {
                    IData param = params.getData(i);
                    IData attr862 = new DataMap();
                    attr862.put("ATTR_CODE", param.getString("ATTR_CODE"));
                    attr862.put("ATTR_VALUE", "0");
                    PARAM.add(attr862);
                }
            }
            tmpSvcData.put("ATTR_PARAM", PARAM);
            SVC.add(tmpSvcData);

        }
        else if ("1".equals(callOut) && "2".equals(callOutTag))
        {
            // 修改限制呼叫VPMN外电话(主叫)
            if (IDataUtil.isEmpty(tmpSvc))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_201);
            }

            IData tmpSvcData = (IData) tmpSvc.get(0);
            tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));
            tmpSvcData.put("ELEMENT_TYPE_CODE", "S");
            if (IDataUtil.isNotEmpty(params))
            {
                for (int i = 0; i < params.size(); i++)
                {
                    IData param = params.getData(i);
                    IData attr862 = new DataMap();
                    attr862.put("ATTR_CODE", param.getString("ATTR_CODE"));
                    attr862.put("ATTR_VALUE", callOutRight);
                    PARAM.add(attr862);
                }
            }
            tmpSvcData.put("ATTR_PARAM", PARAM);
            SVC.add(tmpSvcData);

        }

        // 申请(新增)表示限制接听VPMN外电话(被叫)
        IDataset attrParam = new DatasetList();
        IDataset tmpSvc863 = UserSvcInfoQry.qryUserSvcByUserSvcId(memUserId, "863");
        IDataset params863 = UserAttrInfoQry.getUserProductAttrValue(memUserId, "S", "CALLINRIGHT");
        if ("2".equals(callIn) && "1".equals(callInTag))
        {

            if (IDataUtil.isEmpty(tmpSvc863))
            {
                IData svc863data = new DataMap();
                svc863data.put("START_DATE", SysDateMgr.getSysTime());
                svc863data.put("END_DATE", SysDateMgr.getTheLastTime());
                svc863data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                svc863data.put("ELEMENT_ID", "863");// 限制接听VPMN外电话
                svc863data.put("ELEMENT_TYPE_CODE", "S");
                svc863data.put("PACKAGE_ID", "80000104");
                svc863data.put("PRODUCT_ID", "800001");
                svc863data.put("INST_ID", "");
                if (IDataUtil.isEmpty(params863))
                {
                    // 新增用户服务实例属性
                    IData attr863 = new DataMap();
                    attr863.put("ATTR_CODE", "CALLINRIGHT");
                    attr863.put("ATTR_VALUE", callInRight);// 被叫:0一级默认;1二级(呼入限制);2三级(呼入限制)
                    attrParam.add(attr863);
                }
                else
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_202);
                }
                svc863data.put("ATTR_PARAM", attrParam);
                SVC.add(svc863data);
            }
            else
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_202);
            }

        }
        else if ("2".equals(callIn) && "0".equals(callInTag))
        {
            // 取消(注销)限制接听VPMN外电话(被叫)
            if (IDataUtil.isEmpty(tmpSvc863))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_204);
            }

            IData tmpSvcData = (IData) tmpSvc863.get(0);
            tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));
            tmpSvcData.put("END_DATE", SysDateMgr.getSysTime());
            tmpSvcData.put("ELEMENT_TYPE_CODE", "S");
            if (IDataUtil.isNotEmpty(params863))
            {
                for (int i = 0; i < params863.size(); i++)
                {
                    IData param = params863.getData(i);
                    IData attr863 = new DataMap();
                    attr863.put("ATTR_CODE", param.getString("ATTR_CODE"));
                    attr863.put("ATTR_VALUE", "0");// 被叫:0一级默认;1二级(呼入限制);2三级(呼入限制)
                    attrParam.add(attr863);
                }
            }
            tmpSvcData.put("ATTR_PARAM", attrParam);
            SVC.add(tmpSvcData);
        }
        else if ("2".equals(callIn) && "2".equals(callInTag))
        {
            // 修改限制接听VPMN外电话(被叫)
            if (IDataUtil.isEmpty(tmpSvc863))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_204);
            }

            IData tmpSvcData = (IData) tmpSvc863.get(0);
            tmpSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
            tmpSvcData.put("ELEMENT_ID", tmpSvcData.getString("SERVICE_ID", ""));
            tmpSvcData.put("ELEMENT_TYPE_CODE", "S");
            if (IDataUtil.isNotEmpty(params863))
            {
                for (int i = 0; i < params863.size(); i++)
                {
                    IData param = params863.getData(i);
                    IData attr863 = new DataMap();
                    attr863.put("ATTR_CODE", param.getString("ATTR_CODE"));
                    attr863.put("ATTR_VALUE", callInRight);// 被叫:0一级默认;1二级(呼入限制);2三级(呼入限制)
                    attrParam.add(attr863);
                }
            }
            tmpSvcData.put("ATTR_PARAM", attrParam);
            SVC.add(tmpSvcData);

        }

        // 查询成员订购的服务资费
        IDataset memberSvcOrder = UserSvcInfoQry.getUserProductSvc(memUserId, grpUserId, null);
        if (IDataUtil.isNotEmpty(memberSvcOrder))
        {
            IData svcData = new DataMap();
            for (int i = 0; i < memberSvcOrder.size(); i++)
            {
                svcData = (IData) memberSvcOrder.get(i);
                if ("860".equals(svcData.getString("SERVICE_ID")))
                {

                    IData svc860 = new DataMap();
                    svc860.put("INST_ID", svcData.getString("INST_ID"));
                    svc860.put("START_DATE", svcData.getString("START_DATE"));
                    svc860.put("END_DATE", svcData.getString("END_DATE"));
                    svc860.put("ELEMENT_TYPE_CODE", svcData.getString("ELEMENT_TYPE_CODE"));
                    svc860.put("PRODUCT_ID", svcData.getString("PRODUCT_ID"));
                    svc860.put("PACKAGE_ID", svcData.getString("PACKAGE_ID"));
                    svc860.put("ELEMENT_ID", svcData.getString("SERVICE_ID"));
                    svc860.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    SVC.add(svc860);
                }
            }
        }
        else
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_173);
        }
        if (IDataUtil.isEmpty(SVC))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_174);
        }
        return SVC;
    }

}
