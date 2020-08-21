
package com.asiainfo.veris.crm.order.soa.person.busi.vipsimbak;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCardException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.DevicePriceQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;

public class VipSimBakQueryBean extends CSBizBean
{

    public IDataset checkBakCard(IData params) throws Exception
    {

        IData checkResInfo = new DataMap();
        // 老代码中调用资源接口进行校验
        // inParam.put("RES_TRADE_CODE", "ICheckSimCardInfo");
        // inParam.put("RES_TYPE_CODE", "1");
        // inParam.put("RES_CODE", td.getBaseCommInfo().get("BAK_CARD_NO"));
        // inParam.put("SERIAL_NUMBER", td.getSerialNumber());
        // inParam.put("X_TAG", "2");
        // inParam.put("X_CHECK_TAG", "");
        // inParam.put("OCCUPY_TYPE_CODE", "0");
        // inParam.put("PARA_VALUE1", "2");
        // inParam.put("X_CHOICE_TAG", "1");
        // IDataset dataset = TuxedoHelper.callTuxSvc(pd, "TCS_CheckResource", inParam);

        // IDataset dataset = ResCall.checkResourceForSim("3", "1", params.getString("SERIAL_NUMBER"),
        // params.getString("BAK_CARD_NO"), "1");
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("SIM_TYPE_CODE", "Z");
        data.put("CAPACITY_TYPE_CODE", "K");
        data.put("RES_TYPE_CODE", "sd");
        data.put("RES_KIND_CODE", "L");
        dataset.add(data);
        if (dataset.size() > 0)
        {
            checkResInfo = (IData) dataset.get(0);
            checkResInfo.put("BOOK_DATE", SysDateMgr.getSysDate());
            checkResInfo.put("SIMCARD_NAME", checkResInfo.getString("RSRV_STR4", "测试卡"));
            checkResInfo.put("SIM_TYPE_CODE", checkResInfo.getString("SIM_TYPE_CODE", ""));
            checkResInfo.put("CAPACITY_TYPE_CODE", checkResInfo.getString("CAPACITY_TYPE_CODE", ""));
        }

        checkResInfo.put("X_RESULTCODE", "0");

        checkResInfo.put("BAK_CARD_NO", checkResInfo.getString("SIM_CARD_NO"));

        dataset.add(checkResInfo);
        return dataset;
    }

    public IDataset getDevicePrice(IData param) throws Exception
    {

        String userTag = param.getString("USER_TAG");
        Boolean tagCommonUser = false;
        Boolean tagVipUser = false;
        IDataset feeInfos = new DatasetList();

        String feeTag = param.getString("FEE_TAG", "");
        if ("1".equals(userTag))
        {
            // 获取普通用户优惠
            IDataset commonClasses = ParamInfoQry.getCommparaByCode("CSM", "1750", "Z", CSBizBean.getTradeEparchyCode());

            IDataset userDiscnts = UserDiscntInfoQry.getAllValidDiscntByUserId(param.getString("USER_ID"));

            for (int i = 0; i < userDiscnts.size(); i++)
            {

                IData userDiscnt = (IData) userDiscnts.get(i);
                String discntCode = userDiscnt.getString("DISCNT_CODE", "");

                for (int j = 0; j < commonClasses.size(); j++)
                {

                    IData userClass = (IData) commonClasses.get(j);
                    String paraCode1 = userClass.getString("PARA_CODE1", "TEMP");
                    String paraCode2 = userClass.getString("PARA_CODE2", "TEMP");
                    if (discntCode.equals(paraCode1))
                    {
                        if (!discntCode.equals(paraCode2))
                        {
                            tagCommonUser = true;
                            break;
                        }
                    }
                }
            }
            if (tagCommonUser == true && feeTag.equals("NO"))
            {
                // 只有当用户有优惠且当年无备卡记录时不收费
            }
            else
            {

                IData feeInfo = DevicePriceQry.getDevicePrice(CSBizBean.getTradeEparchyCode(), "1", param.getString("PRODUCT_ID"), "142", param.getString("CAPACITY_TYPE_CODE"), param.getString("RES_KIND_CODE"));

                feeInfos.add(feeInfo);
            }

        }
        else
        {

            IDataset vipClasses = ParamInfoQry.getCommparaByCode("CSM", "1750", "", CSBizBean.getTradeEparchyCode());

            String vipTypeCode = param.getString("VIP_TYPE_CODE");

            for (int i = 0; i < vipClasses.size(); i++)
            {

                IData userClass = (IData) vipClasses.get(i);
                String paramCode = userClass.getString("PARAM_CODE", "TEMP");
                String paraCode1 = userClass.getString("PARA_CODE1", "TEMP");

                if (vipTypeCode.equals(paramCode))
                {
                    if (paraCode1.equals(param.getString("VIP_CLASS_ID")))
                    {
                        tagVipUser = true;
                        break;
                    }

                }
            }
            if (tagVipUser == true && feeTag.equals("NO"))
            {
                // 只有当用户有配置了相关参数且当年无备卡记录时不收费

            }
            else
            {
                IData feeInfo = DevicePriceQry.getDevicePrice(CSBizBean.getTradeEparchyCode(), "1", param.getString("PRODUCT_ID"), "142", param.getString("CAPACITY_TYPE_CODE"), param.getString("RES_KIND_CODE"));

                feeInfos.add(feeInfo);
            }
        }
        return feeInfos;
    }

    private void getSimBakKindName(IDataset dataset) throws Exception
    {

        for (int i = 0; i < dataset.size(); i++)
        {

            IData simCard = dataset.getData(i);

            String simCardName = simCard.getString("CLIENT_INFO3");
            // simCard.put("SIM_TYPE_NAME","testName");
            simCard.put("SIM_TYPE_NAME", ((simCardName == null || "".equals(simCardName))) ? this.getSimCardKindName(simCard.getString("SIM_CARD_NO")) : simCardName);

            IDataset oldResInfos = ResCall.getSimCardInfo("0", simCard.getString("SIM_CARD_NO"), "", "");
            String resTypeCode = "";
            String opc = "";
            if (oldResInfos != null && oldResInfos.size() > 0)
            {
                resTypeCode = oldResInfos.getData(0).getString("RES_TYPE_CODE", "");
                opc = oldResInfos.getData(0).getString("OPC", "");
            }
            else
            {
                resTypeCode = "";
            }
            simCard.put("RES_TYPE_CODE", resTypeCode);
            simCard.put("OPC", opc);

        }

    }

    /**
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IDataset getSimCardInfo(String simCardNo) throws Exception
    {
        IData outParam = new DataMap();

        IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "");

        if (simCardInfo != null && simCardInfo.size() > 0)
        {
            IData tempInfo = (IData) simCardInfo.get(0);

            if (tempInfo.getInt("X_RESULTCODE") == 0)
            {
                outParam.put("KI", tempInfo.getString("KI"));
                outParam.put("SIM_TYPE_CODE", tempInfo.getString("SIM_TYPE_CODE"));
                outParam.put("RSRV_STR6", tempInfo.getString("RSRV_STR6"));
                outParam.put("RSRV_STR7", tempInfo.getString("RSRV_STR7"));
                outParam.put("X_RESULTCODE", tempInfo.getInt("X_RESULTCODE"));
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "550206", outParam.getString("X_RESULTINFO"));
                // common.error("550206:" +outParam.getString("X_RESULTINFO"));
            }
        }
        else
        {// for test *********************************
            outParam.put("KI", "111111");
            outParam.put("SIM_TYPE_CODE", "222222");
            outParam.put("RSRV_STR6", "333333");
            outParam.put("RSRV_STR7", "444444");
            outParam.put("X_RESULTCODE", 0);
        }

        IDataset returnDatas = new DatasetList();
        returnDatas.add(outParam);

        return returnDatas;

    }

    private String getSimCardKindName(String simCardNo) throws Exception
    {

        IDataset simCardInfo = ResCall.getSimCardInfo("0", simCardNo, "", "");

        if (simCardInfo != null && simCardInfo.size() > 0)
        {
            IData tempInfo = (IData) simCardInfo.get(0);

            if (tempInfo.getInt("X_RESULTCODE") == 0)
            {
                return tempInfo.getString("RES_KIND_NAME", "未知卡类型");
            }
            else
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_13, "550206", tempInfo.getString("X_RESULTINFO"));
                // common.error("550206:" +outParam.getString("X_RESULTINFO"));
            }

            return "未知卡类型";
        }
        else
        {
            return "未知卡类型";
        }
    }

    public String getSimType(IData params) throws Exception
    {

        String simNo = "";
        String returnValue = "";

        IDataset userReses = UserResInfoQry.queryUserSimInfo(params.getString("USER_ID"), "1");

        if (userReses != null || userReses.size() >= 0)
        {
            for (int t = 0; t < userReses.size(); t++)
            {
                IData tempRes = (IData) userReses.get(t);
                String resTypeCode = tempRes.getString("RES_TYPE_CODE", "");
                if (resTypeCode.equals("1"))
                {
                    simNo = tempRes.getString("RES_CODE", "");
                }
            }
        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_293);
        }

        if (simNo == null || simNo.equals(""))
        {
            CSAppException.apperr(CrmUserException.CRM_USER_294);
        }
        else
        {
            // 调用资源接口，测试暂不调用
            // IDataset oldResInfos = ResCall.getSimCardInfo("0", simNo, "", "", this.getVisit());
            IDataset oldResInfos = ResCall.getSimCardInfo("0", simNo, "", "");
            // IDataset oldResInfos = new DatasetList();
            // IData data = new DataMap();
            // data.put("RES_KIND_NAME", "testResName");
            // oldResInfos.add(data);
            if (oldResInfos != null && oldResInfos.size() > 0)
            {
                returnValue = ((IData) (oldResInfos.get(0))).getString("RES_TYPE_CODE", "");
            }
            else
            {
                returnValue = "";
            }

        }
        return returnValue;
    }

    protected String getSysTagInfo(String tagCode, String key, String defaultValue) throws Exception
    {

        IData param = new DataMap();

        param.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        param.put("TAG_CODE", tagCode);
        param.put("SUBSYS_CODE", "CSM");
        param.put("USE_TAG", 0);

        return TagInfoQry.getSysTagInfo(tagCode, key, defaultValue, CSBizBean.getTradeEparchyCode());


    }

    /**
     * 获取用户有效资源信息
     * 
     * @param pd
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getUserResInfo(IData params) throws Exception
    {
        return UserResInfoQry.queryUserResByUserIdResType(params.getString("USER_ID"), params.getString("RES_TYPE_CODE"));
    }

    /**
     * 获取vip客户信息
     * 
     * @param params
     *            查询所需参数
     * @return IDataset
     * @throws Exception
     */
    public IDataset getVipInfos(IData params) throws Exception
    {
        return CustVipInfoQry.queryVipInfoBySn(params.getString("SERIAL_NUMBER"), params.getString("REMOVE_TAG"));
    }

    /**
     * 返回大客户备卡激活所需的信息 包括 IDataset 申请的备卡信息 IData 大客户信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakActInfo(IData params) throws Exception
    {

        IData vipSimBakInfo = new DataMap();
        IData infodata = new DataMap();

        String userId = params.getString("USER_ID");

        IData vipIdData = new DataMap();

        IDataset mobilePays = UserPlatSvcInfoQry.queryUserPlatByUserType(userId, "54");
        if (mobilePays.size() > 0)
        {
            // 目前大客户备卡、备卡激活、异地补卡界面对手机支付用户有拦截，根据集团的规范是不进行拦截的，请业务支撑部进行优化，取消拦截。
            CSAppException.apperr(CrmCardException.CRM_CARD_265);
        }

        vipSimBakInfo.put("VIP_ID", userId);
        vipSimBakInfo.put("ACT_TAG", "0");

        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(vipSimBakInfo);

        if (simbakInfos == null || simbakInfos.size() <= 0)
        {

            IData param = new DataMap();

            param.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
            param.put("REMOVE_TAG", "0");

            IDataset vipInfos = this.getVipInfos(param);

            // 当无大客户信息，查询备卡
            if (vipInfos == null || vipInfos.size() <= 0)
            {

                IData vipInfoTemp = new DataMap();

                IDataset vipInfosTemp = CustVipInfoQry.queryCustVipInfoBySn(params.getString("SERIAL_NUMBER"), "1");

                if (vipInfosTemp == null || vipInfosTemp.size() <= 0)
                {
                    CSAppException.apperr(CrmCardException.CRM_CARD_14);
                    // common.error("550202:该客户还没有备卡！");
                }
            }
            else
            {// 当有大客户信息，查询备卡

                vipIdData = (IData) vipInfos.get(0);

                String vipId = vipIdData.getString("VIP_ID", "");

                IData vipInfoTemp = new DataMap();
                vipInfoTemp.put("VIP_ID", vipId);//
                vipInfoTemp.put("ACT_TAG", "0");

                simbakInfos = CustVipInfoQry.getSimBakInfos(vipInfoTemp);

                if (simbakInfos == null || simbakInfos.size() <= 0)
                {
                    // common.error("550203:该用户没有办理备卡，业务无法继续！");
                    CSAppException.apperr(CrmCardException.CRM_CARD_16);
                }
                else
                {
                    infodata.put("NEW_SIM_CARD_NO", vipIdData.getString("SIM_CARD_NO"));
                    infodata.put("NEW_IMSI", vipIdData.getString("IMSI"));
                }

            }

        }
        else
        {
            IData tempInfo = (IData) simbakInfos.get(0);
            infodata.put("NEW_SIM_CARD_NO", tempInfo.getString("SIM_CARD_NO"));
            infodata.put("NEW_IMSI", tempInfo.getString("IMSI"));
            // infodata.put("RES_TYPE_CODE", this.getSimType(params));
        }

        // 用户有效SIM卡资源信息
        IData userResInfo = new DataMap();
        userResInfo.put("X_CONN_DB_CODE", CSBizBean.getTradeEparchyCode());
        userResInfo.put("USER_ID", params.getString("USER_ID"));
        userResInfo.put("RES_TYPE_CODE", "1");
        IDataset userResInfoList = this.getUserResInfo(userResInfo);

        if (userResInfoList == null || userResInfoList.size() <= 0)
        {
            // common.error("550205:获取用户有效SIM卡信息无数据!");
            CSAppException.apperr(CrmCardException.CRM_CARD_20);
        }
        IData userSimInfo = (IData) userResInfoList.get(0);
        // 获取老卡的opc值start
        IDataset oldResInfos = ResCall.getSimCardInfo("0", userSimInfo.getString("RES_CODE"), "", "");
        String opc = "";
        if (oldResInfos != null && oldResInfos.size() > 0)
        {
            opc = oldResInfos.getData(0).getString("OPC", "");
        }
        userSimInfo.put("OLD_OPC", opc);
        // 获取老卡的opc值end

        vipIdData.putAll(infodata);
        vipIdData.putAll(userSimInfo);
        vipIdData.put("RES_TYPE_CODE", this.getSimType(params));
        getSimBakKindName(simbakInfos);
        vipIdData.put("VIP_BAK_INFO", simbakInfos);

        IDataset returnDats = new DatasetList();
        returnDats.add(vipIdData);

        return returnDats;
    }

    /**
     * 返回大客户备卡申请所需的信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakAppInfo(IData params) throws Exception
    {
        String NORMALSIMBAK_LIMIT = "";
        String VIPSIMBAK_LIMIT = getSysTagInfo("CS_NUM_VIPSIMBAKNOLIMIT", "TAG_NUMBER", "1");// 参数字段
        String NORMAL_USER_SIMBAK = getSysTagInfo("CS_NORMALUSERSIMBAK", "TAG_CHAR", "0");// 参数字段
        if ("1".equals(NORMAL_USER_SIMBAK))
        {
            NORMALSIMBAK_LIMIT = getSysTagInfo("CS_NORMALUSERSIMBAK", "TAG_NUMBER", "1");// 参数字段
        }

        String user_tag = "0";
        String simBak_Limit = "0";
        String strUserIdBiz = "";
        String vipClassId = "";

        // 判断用户是否已经办过手机支付业务
        String userId = UcaInfoQry.qryUserInfoBySn(params.getString("SERIAL_NUMBER")).getString("USER_ID");
        IDataset mobilePays = UserPlatSvcInfoQry.queryUserPlatByUserType(userId, "54");
        if (mobilePays.size() > 0)
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_256);
        }

        IDataset vipInfos = CustVipInfoQry.queryVipInfoBySn(params.getString("SERIAL_NUMBER"), params.getString("REMOVE_TAG"));

        IData vipInfo = new DataMap();

        if (vipInfos == null || vipInfos.size() < 1)
        {
            if ("0".equals(NORMAL_USER_SIMBAK))
            {
                CSAppException.apperr(CrmCardException.CRM_CARD_12);
            }
            else
            {
                simBak_Limit = NORMALSIMBAK_LIMIT;
                user_tag = "1";
                strUserIdBiz = params.getString("USER_ID");
            }
        }
        else
        {
            vipInfo = (IData) vipInfos.get(0);

            // if (StringUtils.isEmpty(vipInfo.getString("VIP_CARD_END_DATE")) ||
            // SysDateMgr.getSysTime().compareTo(vipInfo.getString("VIP_CARD_END_DATE")) > 0)
            // {
            // CSAppException.apperr(CrmCommException.CRM_COMM_103, "该大客户已经失效");
            // }

            simBak_Limit = VIPSIMBAK_LIMIT;
            user_tag = "2";
            strUserIdBiz = vipInfo.getString("VIP_ID", "");
            vipClassId = vipInfo.getString("VIP_CLASS_ID", "");
        }

        params.put("VIP_ID", strUserIdBiz);

        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(params);

        // 调用资源的接口。校验sim
        // getSimBakKindName(simbakInfos);

        params.put("START_DATE", SysDateMgr.getNowYear() + "-01" + "-01 00" + ":00:00");
        params.put("END_DATE", SysDateMgr.getLastDayOfThisYear());

        vipInfo.put("FEE_TAG", CustVipInfoQry.getSimBakInfosByDate(params).size() < 1 ? "NO" : "YES");

        // 调用资源的接口。校验sim
        vipInfo.put("RES_KIND_NAME", this.getSimType(params));
        vipInfo.put("RES_KIND_NAME", "");
        vipInfo.put("VIP_BAK_INFO", simbakInfos);

        int iNowVipBakNo = 0;
        if (!simbakInfos.isEmpty())
        {
            for (int i = 0; i < simbakInfos.size(); i++)
            {
                if ("0".equals(simbakInfos.get(i, "ACT_TAG")))
                    iNowVipBakNo++;
            }
        }

        vipInfo.put("VIPSIMBAK_NUMBER", iNowVipBakNo);
        vipInfo.put("OLD_SIM_BAK", simbakInfos);
        vipInfo.put("VIP_ID", strUserIdBiz);// 隐藏参数字段
        vipInfo.put("USER_TAG", user_tag);// 隐藏参数字段
        vipInfo.put("VIP_CLASS_ID", vipClassId);// 隐藏参数字段
        vipInfo.put("SIMBAK_LIMIT", simBak_Limit);// 隐藏参数字段

        if (iNowVipBakNo >= Integer.parseInt(simBak_Limit))
        {
            CSAppException.apperr(CrmCardException.CRM_CARD_15, simBak_Limit);
        }

        IDataset returnDatas = new DatasetList();

        returnDatas.add(vipInfo);

        return returnDatas;

    }

    /**
     * 返回大客户备卡取消所需的信息 包括 IDataset 申请的备卡信息 IData 大客户信息
     * 
     * @param params
     * @return
     * @throws Exception
     */
    public IDataset getVipSimBakCancelInfo(IData params) throws Exception
    {

        IData param = new DataMap();
        IData vipInfo = new DataMap();
        String strUserIdBiz = "";

        param.put("SERIAL_NUMBER", params.getString("SERIAL_NUMBER"));
        param.put("REMOVE_TAG", "0");

        IDataset vipInfos = this.getVipInfos(param);

        if (IDataUtil.isEmpty(vipInfos))
        {
            strUserIdBiz = params.getString("USER_ID");
            vipInfo.put("VIP_EMPTY_TAG", "0");
        }
        else
        {
            vipInfo = (IData) vipInfos.get(0);

            vipInfo.put("VIP_EMPTY_TAG", "1");
            strUserIdBiz = vipInfo.getString("VIP_ID", "");
        }

        // String simTypeCode = this.getSimType(params);

        IData inParam = new DataMap();
        inParam.put("VIP_ID", strUserIdBiz);
        IDataset simbakInfos = CustVipInfoQry.getSimBakInfos(inParam);

        if (IDataUtil.isEmpty(simbakInfos))
        {
            // common.error("该用户没有备卡，不能办理取消！");
            CSAppException.apperr(CrmCardException.CRM_CARD_97);
        }

        // vipInfo.put("SIM_TYPE_CODE", simTypeCode);
        getSimBakKindName(simbakInfos);
        vipInfo.put("VIP_BAK_INFO", simbakInfos);
        vipInfo.put("RES_TYPE_CODE", this.getSimType(params));
        IDataset returnDats = new DatasetList();
        returnDats.add(vipInfo);

        return returnDats;
    }

}
