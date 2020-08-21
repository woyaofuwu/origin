
package com.asiainfo.veris.crm.order.web.group.param.yht;

import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.FuncrightException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.userattrinfo.UserAttrInfoIntfViewUtil;

public class MemParamInfo extends IProductParamDynamic
{
    private boolean ctFlag = false;

    private String memsn = ""; // 集团成员手机号
    // 原个性化参数保存在TF_F_USER_PVPN（NG为Tf_F_USER_VPN_MEB）里，
    // 在NG中数据不好倒换到USER_ATTR表，因此还是从TF_F_USER_VPN_MEB表读取个性化
    public IData initChgMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initChgMb(bp, data);
        IData paramInfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            paramInfo = result.getData("PARAM_INFO");
        }

        String meb_user_id = data.getString("MEB_USER_ID", "");
        String memEparchy = data.getString("MEB_EPARCHY_CODE", "");

        // 此处传meb_user_id为何不是集团user_id?
        IDataset dsUu = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCode(bp, meb_user_id, "S6", memEparchy);
        if (IDataUtil.isNotEmpty(dsUu))
        {
            IDataset dataset = new DatasetList();
            for (int i = 0; i < dsUu.size(); i++)
            {
                IData oldUuData = dsUu.getData(i);

                String mainFlag = "";
                if ("1".equals(oldUuData.getString("RSRV_TAG1", "")))
                {
                    mainFlag = "是";
                }
                else
                {
                    mainFlag = "否";
                }
                oldUuData.put("X_TAG", "0");
                oldUuData.put("MAIN_FLAG_CODE", oldUuData.getString("RSRV_TAG1", ""));
                oldUuData.put("SERIAL_NUMBER", oldUuData.getString("SERIAL_NUMBER_B", ""));
                oldUuData.put("MAIN_FLAG", mainFlag);
                oldUuData.put("ZUSERID", oldUuData.getString("USER_ID_B", ""));
                oldUuData.put("START_DATE", oldUuData.getString("START_DATE", ""));

                dataset.add(oldUuData);
            }
            // 绑定信息
            paramInfo.put("INFOS", dataset);
            // 绑定变更前数据
            IData yhtpamData = new DataMap();
            yhtpamData.put("pam_zyht", dataset);
            yhtpamData.put("pam_oldzyht", dataset);// 保存上次修改数据

            IDataset zpauseList = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(bp, meb_user_id, "P", "CallingActivated");
            if (IDataUtil.isNotEmpty(zpauseList))
            {
                yhtpamData.put("ZPAUSE", zpauseList.getData(0).getString("ATTR_VALUE"));
            }

            paramInfo.put("Yhtpam", yhtpamData);
        }

        // 被叫一号通
        IDataset dsUu2 = RelationUUInfoIntfViewUtil.qryRelaUUInfosByUserIdAAndRelationTypeCode(bp, meb_user_id, "S7", memEparchy);

        if (IDataUtil.isNotEmpty(dsUu2))
        {
            IDataset dataset = new DatasetList();
            String pam_Z_TAG_VAL = "";
            for (int i = 0; i < dsUu2.size(); i++)
            {
                IData oldUuData = dsUu2.getData(i);

                // String str = "";
                // if("0".equals(oldUuData.getString("RSRV_TAG1",""))){
                // str = "同振";
                // }else{
                // str = "顺振";
                // }
                oldUuData.put("X_TAG", "0");
                pam_Z_TAG_VAL = oldUuData.getString("RSRV_TAG1", "");
                oldUuData.put("BSERIAL_NUMBER", oldUuData.getString("SERIAL_NUMBER_B", ""));
                // oldUuData.put("Z_TAG_NAME", str);
                oldUuData.put("BUSERID", oldUuData.getString("USER_ID_B", ""));
                oldUuData.put("START_DATE", oldUuData.getString("START_DATE", ""));

                dataset.add(oldUuData);
            }
            // 绑定被叫一号通信息
            paramInfo.put("BINFOS", dataset);

            // 绑定变更前专网数据
            IData pamData = new DataMap();
            pamData.put("pam_byht", dataset);
            pamData.put("pam_oldbyht", dataset);// 保存上次修改数据
            pamData.put("pam_Z_TAG_VAL", pam_Z_TAG_VAL); // 保存振动方式

            IDataset bpauseList = UserAttrInfoIntfViewUtil.qryGrpProductAttrInfosByUserIdAndInstTypeAndAttrCode(bp, meb_user_id, "P", "CalledActivated");
            if (IDataUtil.isNotEmpty(bpauseList))
            {
                pamData.put("BPAUSE", bpauseList.getData(0).getString("ATTR_VALUE"));
            }
            paramInfo.put("BYHTPAM", pamData);
        }

        // 获取成员手机号
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, meb_user_id, memEparchy);
        memsn = mebUserInfoData.getString("SERIAL_NUMBER");// 为什么不从参数里取服务号码信息 lim

        // 判断是否为用户变更
        IData method = new DataMap();
        method.put("METHOD_NAME", "ChgUs");
        method.put("MEMSN", memsn);
        paramInfo.put("METHOD", method);

        // 判断是否可以拨打国内长途 14
        IData param2 = new DataMap();
        param2.put("SERIAL_NUMBER", memsn);

        IDataset outDataset = CSViewCall.call(bp, "SS.GroupImsUtilBeanSVC.ifMofficeTelePhoneUser", param2);
        if (Boolean.valueOf(outDataset.getData(0).getString("RESULT")))
        {
            IData idata2 = new DataMap();
            idata2.put("USER_ID", meb_user_id);
            idata2.put("SERVICE_ID", "14");
            idata2.put(Route.ROUTE_EPARCHY_CODE, memEparchy);

            IDataset ds = CSViewCall.call(bp, "CS.UserSvcInfoQrySVC.querySvcByUserIDandSVC", idata2);
            if (IDataUtil.isNotEmpty(ds))
            {
                ctFlag = true;
            }
        }
        else
        { // 为固定电话时只产生了虚拟三户资料，是没有14服务，暂时不判断
            ctFlag = true;
        }
        result.put("PARAM_INFO", paramInfo);

        return result;
    }

    public IData initCrtMb(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtMb(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        // 短号有效
        parainfo.put("IF_SHORT_CODE", "yes");

        // 获取成员手机号
        String memEparchy = data.getString("MEB_EPARCHY_CODE", "");
        String meb_user_id = data.getString("MEB_USER_ID", "");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoByUserIdAndRoute(bp, meb_user_id, memEparchy);
        memsn = mebUserInfoData.getString("SERIAL_NUMBER");
        IData method = new DataMap();
        method.put("METHOD_NAME", "CrtUs");
        method.put("MEMSN", memsn);
        parainfo.put("METHOD", method);

        // 判断是否可以拨打国内长途 14
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", memsn);

        IDataset outDataset = CSViewCall.call(bp, "SS.GroupImsUtilBeanSVC.ifMofficeTelePhoneUser", param);
        if (Boolean.valueOf(outDataset.getData(0).getString("RESULT")))
        {
            IData idata = new DataMap();
            idata.put("USER_ID", meb_user_id);
            idata.put("SERVICE_ID", "14");
            idata.put(Route.ROUTE_EPARCHY_CODE, memEparchy);

            IDataset ds = CSViewCall.call(bp, "CS.UserSvcInfoQrySVC.querySvcByUserIDandSVC", idata);

            if (IDataUtil.isNotEmpty(ds))
            {
                ctFlag = true;
            }
        }
        else
        { // 为固定电话时只产生了虚拟三户资料，是没有14服务，暂时不判断
            ctFlag = true;
        }

        result.put("PARAM_INFO", parainfo);
        return result;
    }

    /**
     * 查询被叫一号通号码个人用户信息
     * 
     * @author luoyong
     * @throws Throwable
     */

    public IData queryBSerialnumberInfo(IBizCommon bp, IData data) throws Throwable
    {
        IData outResult = new DataMap();
        IData paramInfo = new DataMap();

        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码
        String memSn = data.getString("MEB_SERIAL_NUMBER", "");

        // 查询成员用户信息
        String strMebSn = data.getString("BSERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(bp, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");
        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }
        paramInfo.put("BUSER_ID", user_id_b);

        outResult.put("AJAX_DATA", paramInfo);
        return outResult;
    }

    /**
     * 查询主叫一号通号码个人用户信息
     * 
     * @author luoyong
     * @throws Throwable
     */
    public IData querySerialnumberInfo(IBizCommon bp, IData data) throws Throwable
    {
        IData outResult = new DataMap();
        IData paramInfo = new DataMap();

        String memEparchCode = data.getString("MEB_EPARCHY_CODE"); // 成员所属地市编码        String memSn = data.getString("MEB_SERIAL_NUMBER", "");
        String user_id = data.getString("MEB_USER_ID", "");
        IData yhtMem = UCAInfoIntfViewUtil.qryMebUserInfoBySn(bp, memSn);
        if (!"05".equals(yhtMem.getString("NET_TYPE_CODE")))
        {
            CSViewException.apperr(GrpException.CRM_GRP_802);// 手机号码不能开通主叫一号通业务！
        }

        // 查询成员用户信息
        String strMebSn = data.getString("SERIAL_NUMBER");
        IData mebUserInfoData = UCAInfoIntfViewUtil.qryMebUserInfoBySn(bp, strMebSn);
        String strEparchCode = mebUserInfoData.getString("EPARCHY_CODE");
        String user_id_b = mebUserInfoData.getString("USER_ID", "");

        // 没有长途拨打权限的不能加外地号码作为副号码
        if (!ctFlag)
        {
            if (!memEparchCode.equals(strEparchCode))
            {
                CSViewException.apperr(FuncrightException.CRM_FUNCRIGHT_9, memSn, strMebSn);

            }
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID_B", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "S6");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset relainfos = CSViewCall.call(bp, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(relainfos))
        {
            String s6UserIdA = relainfos.getData(0).getString("USER_ID_A");
            if (!s6UserIdA.equals(user_id))
            {
                CSViewException.apperr(GrpException.CRM_GRP_803, strMebSn);// 该服务号码[" + strMebSn + "]已订购为其他成员的主叫一号通副号！
            }
        }

        inparam.clear();
        inparam.put("USER_ID_A", user_id_b);
        inparam.put("RELATION_TYPE_CODE", "S6");
        inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);

        IDataset zrelainfos = CSViewCall.call(bp, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

        if (IDataUtil.isNotEmpty(zrelainfos))
        {
            CSViewException.apperr(GrpException.CRM_GRP_804, strMebSn);// 该服务号码[" + strMebSn + "]已订购主叫一号通业务，不能作为其他号码的主叫一号通副号！
        }

        // 如果是固话需要订购IMS业务
        if ("05".equals(mebUserInfoData.getString("NET_TYPE_CODE")))
        {
            inparam.clear();
            // 成员加入其他IMS产品前，必须加入多媒体桌面电话，所以只校验是够加入多媒体桌面电话即可
            inparam.put("USER_ID_B", user_id_b);
            inparam.put("RELATION_TYPE_CODE", "S1");
            inparam.put(Route.ROUTE_EPARCHY_CODE, strEparchCode);
            IDataset imsRelat = CSViewCall.call(bp, "CS.RelaUUInfoQrySVC.getUUByUserIdAB", inparam);

            if (IDataUtil.isEmpty(imsRelat))
            {
                CSViewException.apperr(GrpException.CRM_GRP_805, strMebSn);// 该号码非本集团IMS产品成员，不能添加为主叫副号！
            }
        }

        paramInfo.put("ZUSER_ID", user_id_b);

        outResult.put("AJAX_DATA", paramInfo);
        return outResult;
    }
}
