
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class BatGlobalADCBlackWhiteTrans implements ITrans
{

    @Override
    public void transRequestData(IData batData) throws Exception
    {
        // 初始化数据
        InitialDataSub(batData);

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
        String oprCode = IDataUtil.chkParam(condData, "OPER_CODE");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码

        IData userGrpInfo = UcaInfoQry.qryUserMainProdInfoBySn(mainSn);
        if (IDataUtil.isEmpty(userGrpInfo))
        {// 网外号码
            condData.put("IS_OUT_NET", true);
        }

        IDataset blackwhite = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuserid(mainSn, grpUserId);// 查sn在blackwhite的记录
        boolean isBW = IDataUtil.isNotEmpty(blackwhite) ? true : false;

        if ("01".equals(oprCode) && isBW)
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_08); // ("用户已在名单内")
        }
        if ("02".equals(oprCode) && !isBW)
        {
            CSAppException.apperr(IntfIAGWException.CRM_IGU_09);// ("用户不在名单内")
        }
    }

    protected void builderSvcData(IData batData) throws Exception
    {
        IData svcData = batData.getData("svcData", new DataMap());
        IData condData = batData.getData("condData", new DataMap());

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        if (isOutNet)
        {// 网外号码

            IData serviceInfo = dealServiceParamInfo(batData);
            svcData.put("PRODUCT_ID", IDataUtil.getMandaData(condData, "PRODUCT_ID"));// 集团产品ID
            svcData.put("MEB_USER_ID", "-1");// 成员USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.chkParam(batData, "SERIAL_NUMBER"));// 成员用户号码
            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团用户标识
            svcData.put("SERVICE_INFOS", new DatasetList(serviceInfo));// 服务参数信息
            svcData.put(Route.ROUTE_EPARCHY_CODE, Route.getCrmDefaultDb());
        }
        else
        {
            svcData.put("USER_ID", IDataUtil.getMandaData(condData, "USER_ID"));// 集团USER_ID
            svcData.put("SERIAL_NUMBER", IDataUtil.getMandaData(batData, "SERIAL_NUMBER"));// 成员SN
            svcData.put("MEM_ROLE_B", condData.getString("PLAN_TYPE_CODE", "1"));// 成员角色
            svcData.put("PLAN_TYPE_CODE", condData.getString("PLAN_TYPE_CODE", "P"));// 个人付款
            svcData.put("RES_INFO", condData.getDataset("RES_INFO", new DatasetList("[]")));
            svcData.put("EFFECT_NOW", condData.getString("EFFECT_NOW", "true"));
            svcData.put("PRODUCT_ID", condData.getString("PRODUCT_ID", ""));// 集团产品ID
            svcData.put("ELEMENT_INFO", condData.getDataset("SELECTED_ELEMENTS", new DatasetList("[]")));
            svcData.put("PRODUCT_PARAM_INFO", condData.getDataset("PRODUCT_PARAM_INFO", new DatasetList("[]")));// 产品参数
        }

    }

    // 根据条件判断调用服务
    protected void setSVC(IData batData) throws Exception
    {
        String svcName = "";
        IData condData = batData.getData("condData", new DataMap());
        IData svcData = batData.getData("svcData", new DataMap());

        boolean isOutNet = condData.getBoolean("IS_OUT_NET", false);
        String operType = IDataUtil.chkParam(condData, "OPER_CODE");

        if (isOutNet)
        {// 网外号码处理
            svcName = "SS.MgrBlackWhiteOutSVC.crtTrade";
        }
        else
        {
            if ("01".equals(operType))
            {// 成员新增
                svcName = "CS.CreateGroupMemberSvc.createGroupMember";
            }
            else if ("02".equals(operType))
            {// 成员删除
                svcName = "CS.DestroyGroupMemberSvc.destroyGroupMember";
            }
            else
            {// 成员变更
                svcName = "CS.ChangeMemElementSvc.changeMemElement";
            }
        }

        svcData.put("REAL_SVC_NAME", svcName);
    }

    /**
     * 2, 批量全网ADC业务成员批量定购,MEMBERADCORDER;-->AllADCGrpMemBatBiz("MEMBERADCORDER"),
     * //批量全网ADC业务成员批量定购PRODUCT_ID=9188(ADC商信通产品) 3.
     * 
     * @param batData
     */
    public void InitialDataSub(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());
        IDataset mebServIdList = new DatasetList();
        String selectedElements = condData.getString("SELECTED_ELEMENTS");
        IDataset selectedElementslist = new DatasetList(selectedElements);

        for (int i = 0; i < selectedElementslist.size(); i++)
        {
            String mebServId = selectedElementslist.getData(i).getString("ELEMENT_ID");
            String elementTypeCode = selectedElementslist.getData(i).getString("ELEMENT_TYPE_CODE");
            String attrParam = selectedElementslist.getData(i).getString("ATTR_PARAM", "");

            if ("S".equals(elementTypeCode) && StringUtils.isNotBlank(attrParam))
            {// 网外号码用
                mebServIdList.add(mebServId);
            }
        }

        condData.put("MEB_SERVICE_ID_LIST", mebServIdList);
    }

    /**
     * liaolc 2014-7-26 作用：见外号服务参数串 SERVICE_INFOS[{}]
     */
    public static IData dealServiceParamInfo(IData batData) throws Exception
    {
        IData condData = batData.getData("condData", new DataMap());

        IDataset MebServIdList = condData.getDataset("MEB_SERVICE_ID_LIST");
        String oprCode = IDataUtil.chkParam(condData, "OPER_CODE");
        String grpProductId = IDataUtil.chkParam(condData, "PRODUCT_ID");
        String grpUserId = IDataUtil.chkParam(condData, "USER_ID");// 集团用户ID
        String mainSn = IDataUtil.chkParam(batData, "SERIAL_NUMBER");// 成员服务号码
        String grpServId = "";

        IData serviceInfo = new DataMap();
        IDataset blackWhiteInfo = new DatasetList();

        // 查询该网外号码是否已经加入黑白名单
        for (int i = 0, iSize = MebServIdList.size(); i < iSize; i++)
        {
            String mebServId = (String) MebServIdList.get(i);
            grpServId = MemParams.getmebServIdByGrpServId(mebServId);

            blackWhiteInfo = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(mainSn, grpUserId, mebServId);// 查sn在blackwhite的记录
            if (IDataUtil.isNotEmpty(blackWhiteInfo))
            {
                break;
            }
        }

        if (IDataUtil.isNotEmpty(blackWhiteInfo) && !"01".equals(oprCode))
        {
            IData oldblackwhite = blackWhiteInfo.getData(0);
            serviceInfo.put("OPER_TYPE", oprCode); // 退出黑白名单
            serviceInfo.put("MEB_USER_ID", oldblackwhite.getString("USER_ID", "")); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "1"); // 删除
        }

        else
        {
            serviceInfo.put("OPER_TYPE", oprCode); // 加入黑白名单
            serviceInfo.put("MEB_USER_ID", "-1"); // 成员USER_ID
            serviceInfo.put("MODIFY_TAG", "0"); // 新增
        }
        serviceInfo.put("PRODUCT_ID", grpProductId);
        serviceInfo.put("SERVICE_ID", grpServId);

        return serviceInfo;
    }
}
