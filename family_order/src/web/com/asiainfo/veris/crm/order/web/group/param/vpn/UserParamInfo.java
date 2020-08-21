
package com.asiainfo.veris.crm.order.web.group.param.vpn;

import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.view.IBizCommon;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.component.group.dynamichtml.IProductParamDynamic;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attriteminfo.AttrItemInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.commparainfo.CommParaInfoIntfViewUtil;

public class UserParamInfo extends IProductParamDynamic
{

    private String scp = "";

    /**
     * 生产集团服务号，,JS调用
     * 
     * @param bp
     * @param data
     * @return
     * @throws Exception
     */
    public IData createSerialNumber(IBizCommon bp, IData data) throws Exception
    {
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;
        // String work_yype_code = data.getString("WORK_TYPE_CODE");
        data.put(Route.USER_EPARCHY_CODE, ((CSBizHttpHandler) bp).getTradeEparchyCode());
        IData data1 = new DataMap();
        for (int i = 0; i < 1000; i++)
        {
            String vpn_no = "";// j2ee VpnUnit.vpmnNoCrt(pd, work_yype_code); // VPMN产品编码生成规则

            IDataset vpnnoData = CSViewCall.call(bp, "SS.VpnUnitSVC.vpmnNoCrt", data);
            if (IDataUtil.isNotEmpty(vpnnoData))
            {
                vpn_no = vpnnoData.getData(0).getString("VPN_NO");
            }

            IData info = new DataMap();
            info.put("SERIAL_NUMBER", vpn_no);
            IData userinfo = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(bp, vpn_no, false);
            if (IDataUtil.isEmpty(userinfo))
            {
                result = true;
                paramresult.put("VPN_NO", vpn_no);
                break;
            }
            if (i == 999)
            {
                result = false;
                paramresult.put("ERROR_MESSAGE", "该VPMN集团类型的VPN_NO已经全部被使用，请重新选择VPMN集团类型！");
            }
        }

        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);
        return results;
    }

    public String getScp()
    {
        return scp;
    }

    // 原个性化参数保存在TF_F_USER_VPN里，因此还是从TF_F_USER_VPN表读取个性化
    @Override
    public IData initChgUs(IBizCommon bp, IData data) throws Throwable
    {
        String grpUserId = data.getString("USER_ID", "");
        IData result = super.initChgUs(bp, data);
        
        IDataset dataset = AttrItemInfoIntfViewUtil.qryGrpProductItemAInfosByGrpProductIdAndEparchyCode(bp, data.getString("PRODUCT_ID", ""), data.getString("USER_EPARCHY_CODE"));

        IData attrItem = new DataMap();
        if(IDataUtil.isNotEmpty(dataset))
        {
            attrItem = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
        }
        
        IData parainfo = new DataMap();
        parainfo.put("PRODUCT_ID", data.getString("PRODUCT_ID", ""));

        // 获取VPN表数据
        IData userVpnData = UCAInfoIntfViewUtil.qryUserVpnInfoByUserId(bp, grpUserId, false);

        if (IDataUtil.isEmpty(userVpnData))
        {
            userVpnData = new DataMap();
        }
        // add by xuecd 20100317 判读本集团能否订购跨省V网优惠，如果配置为空，则成员定制不能订购99720501-99720505的5个跨省资费
        String subsysCode = "CSM";
        String paramAttr = "119";
        String paramCode = "0";
        String paraCode1 = data.getString("GRP_SN");
        String eparchyCode = data.getString("EPARCHY_CODE");
        IDataset commParams = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeParamCode1EparchyCode(bp, subsysCode, paramAttr, paramCode, paraCode1, eparchyCode);
        boolean canOrderDis = true;
        if (IDataUtil.isEmpty(commParams))
        {
            canOrderDis = false;
        }
        // 特殊字段数据处理 vpn.FUNC_TLAGS vpn.CALL_NET_TYPE vpn.RSRV_TAG1 vpn.RSRV_TAG5

        // 分解拼串数据 动态表格值 start
        String defaultFunctlags = "1100000000000000000000000000000000000000";
        String functlags = userVpnData.getString("FUNC_TLAGS", "1100000000000000000000000000000000000000");
        if (functlags.length() < 40)
        {
            functlags = IDataUtil.replacStrByint(defaultFunctlags, functlags, 1, functlags.length());
        }

        IDataset datas = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bp, data.getString("PRODUCT_ID", ""), "P", "2", ((CSBasePage) bp).getTradeEparchyCode());
        if (IDataUtil.isNotEmpty(datas))
        {
            for (int i = 0; i < datas.size(); i++)
            {
                IData data1 = datas.getData(i);
                String attrCode = data1.getString("ATTR_CODE");
                try
                {
                    int codeNumber = Integer.valueOf(attrCode.substring(attrCode.length() - 2));
                    if (codeNumber <= 40)
                    {
                        data1.put("ATTR_INIT_VALUE", functlags.substring(codeNumber - 1, codeNumber));
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
                data1.put("DEFAULT_VALUE", data1.getString("ATTR_INIT_VALUE"));
                datas.set(i, data1);
            }
            
            IData transData = IDataUtil.hTable2STable(datas, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            getAttrItem().putAll(transData);
        }
        // 分解拼串数据 动态表格值 end
        // VPMN表格参数
        // 分解拼串数据 呼叫网络类型 start
        String callnettype = userVpnData.getString("CALL_NET_TYPE", "");// 呼叫网络类型

        if (callnettype.length() >= 1)
        { // 内网
            String CALL_NET_TYPE1 = callnettype.substring(0, 1);
            userVpnData.put("CALL_NET_TYPE1", CALL_NET_TYPE1);
        }
        if (callnettype.length() >= 2)
        { // 网间
            String CALL_NET_TYPE2 = callnettype.substring(1, 2);
            userVpnData.put("CALL_NET_TYPE2", CALL_NET_TYPE2);
        }
        if (callnettype.length() >= 3)
        { // 网外
            String CALL_NET_TYPE3 = callnettype.substring(2, 3);
            userVpnData.put("CALL_NET_TYPE3", CALL_NET_TYPE3);
        }
        if (callnettype.length() >= 4)
        { // 网外号码组
            String CALL_NET_TYPE4 = callnettype.substring(3, 4);
            userVpnData.put("CALL_NET_TYPE4", CALL_NET_TYPE4);
        }
        // 分解拼串数据 呼叫网络类型 end

        userVpnData.put("VPN_CLASS", userVpnData.getString("RSRV_STR5", "")); // VPMN类别

        String vap_scare = userVpnData.getString("VPN_SCARE_CODE", ""); // 集团范围属性 2:跨省
        // IDataset vpnInfos = new DatasetList();
        IData vpnparam = new DataMap();
        String custinfo = data.getString("CUST_INFO", "{}");
        IData custinfodata = new DataMap(custinfo);
        String groupId = data.getString("GROUP_ID");
        vpnparam.put("GROUP_ID", groupId);
        vpnparam.put("RSRV_TAG1", "0");
        vpnparam.put("RSRV_TAG2", "0");
        IDataset vpninfos = new DatasetList();
        if (vap_scare.equals("2")) // 全国集团
        {
            vpnparam.put("VPN_NO", userVpnData.getString("VPN_NO", ""));
            vpninfos = CSViewCall.call(bp, "CS.GrpExtInfoQrySVC.getVPNNOByVPNNO", vpnparam);
        }
        else
        {
            userVpnData.remove("VPN_NO");
            vpninfos = CSViewCall.call(bp, "CS.GrpExtInfoQrySVC.getVPNNOByGROUPID", vpnparam);
        }

        if (IDataUtil.isNotEmpty(vpninfos))
        {
            int len = vpninfos.size();
            for (int i = 0; i < len; i++)
            {
                IData vpnInfoData = vpninfos.getData(i);
                String rsrvStr1 = vpnInfoData.getString("RSRV_STR1", "");
                String rsrvStr2 = vpnInfoData.getString("RSRV_STR2", "");
                String rsrvStr3 = vpnInfoData.getString("RSRV_STR3", "");
                vpnInfoData.put("DISPLAY_NAME", rsrvStr1 + "|" + rsrvStr2 + "|" + rsrvStr3);

            }
        }

        // 成员升级资费 start
        IDataset defualtDiscntset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(bp, "CGM", "80", ((CSBasePage) bp).getTradeEparchyCode());
        if (IDataUtil.isEmpty(defualtDiscntset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_177);

        }
        IDataset defDiscntset = new DatasetList();
        String defaultDiscnt = "";
        String PROVICE_VPN_DISCNT = "";
        for (int i = 0; i < defualtDiscntset.size(); i++)
        {

            String Discnttag = defualtDiscntset.getData(i).getString("PARAM_CODE", "");
            String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
            if (PROVICE_VPN_DISCNT.equals(""))
                PROVICE_VPN_DISCNT = discntinfo;
            else
                PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
            if (Discnttag.equals("0"))
            {
                defaultDiscnt = discntinfo;
            }
            IData defDisData = new DataMap();
            defDisData.put("PARA_CODE1", discntinfo);
            defDisData.put("PARAM_NAME", defualtDiscntset.getData(i).getString("PARAM_NAME", ""));
            defDiscntset.add(defDisData);
        }
        userVpnData.put("DEFAULT_DISCNTCODE", defaultDiscnt); // 成员升级资费
        // j2ee: 界面隐藏区域，用于下一步判断“非跨省集团，不能对跨省集团资费做操作”“跨省集团需要跨省资费，请选择跨省资费!”
        userVpnData.put("PROVICE_VPN_DISCNT", PROVICE_VPN_DISCNT);
        // 成员升级资费 end
        // 获取统一付费优惠 j2ee:没找到写入的地方 start
        IData inparam = new DataMap();
        inparam.put("USER_ID", grpUserId);
        inparam.put("REMOVE_TAG", "0");
        inparam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", "0898"));
        IDataset userInfos = CSViewCall.call(bp, "CS.UserInfoQrySVC.getGrpUserInfoByUserId", inparam);
        IData userInfo = new DataMap();
        String isModify = "";
        if (IDataUtil.isNotEmpty(userInfos))
        {
            userInfo = userInfos.getData(0);
            String grpPayDiscnt = userInfo.getString("RSRV_STR5", "");
            if (StringUtils.isNotBlank(grpPayDiscnt))
            {
                isModify = "YES";// 统一付费优惠已有资料（YES），则前台不让操作（disabled=true）
            }
        }
        userVpnData.put("GRP_PAY_DISCNT", userInfo.getString("RSRV_STR5", ""));
        String str7 = userInfo.getString("RSRV_STR7", "");
        if (StringUtils.isBlank(userVpnData.getString("CUST_MANAGER", "")) && StringUtils.isNotBlank(str7))
        {
            userVpnData.put("CUST_MANAGER", str7); // 分管客户经理
        }
        userVpnData.put("VPN_GRP_ATTR", userInfo.getString("RSRV_STR10", "")); // 集团属性

        // 获取统一付费优惠 ,变更才能选择 end

        // modify by lixiuyu@20101009 用户要求升级为跨省VPMN时默认选上"长、短号拨打"属性值为2
        userVpnData.put("DIAL_TYPE_CODE", userVpnData.getString("RSRV_TAG1", "2")); // 拨打方式 ,跨省VPMN才有插值

        IData userattritem = IDataUtil.iDataA2iDataB(userVpnData, "ATTR_VALUE"); // 转格式为可ognl:getAttrItemValue('CALL_AREA_TYPE','ATTR_VALUE')
        
        // 方便前台取下拉框选项值
        transComboBoxValue(userattritem, attrItem);
        
        super.getAttrItem().putAll(userattritem);
        super.setAttrItemSet(IDataUtil.iData2iDataset(userVpnData, "ATTR_CODE", "ATTR_VALUE"));

        // add by lixiuyu@20101103 从TF_SM_MANAGER_JOBTYPE获取客户经理编码
        IData map = new DataMap();
        IDataset managerInfos = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryManagerIdJobType", map);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);

            }
        }
        // SCPCODE start
        String scpCode = userVpnData.getString("SCP_CODE", ""); // SCP_CODE
        IDataset idatas = StaticUtil.getStaticList("TD_B_SCP", scpCode);
        IData data1 = new DataMap();
        IData scpdata = new DataMap();
        IDataset SCPInfos = new DatasetList();
        if (IDataUtil.isNotEmpty(idatas))
        {
            data1 = idatas.getData(0);
            scpdata.put("DATA_NAME", data1.getString("DATA_NAME", ""));
            scpdata.put("DATA_ID", data1.getString("DATA_ID", ""));
        }
        SCPInfos.add(scpdata);
        // SCPCODE end
        // String str = queryStaffPriv(bp, data);
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();

        parainfo.put("RIGHT_CODE", StaffPrivUtil.isFuncDataPriv(staffId, "SYS126"));
        parainfo.put("managerInfos", managerInfos);
        parainfo.put("vpnInfos", vpninfos);
        parainfo.put("userParamInfos", datas); // 动态表格数据
        parainfo.put("SCPInfos", SCPInfos); // SCP代码
        parainfo.put("GRP_SN", data.getString("GRP_SN"));
        // parainfo.put("GRP_USER_ID", grpUserId);
        parainfo.put("defualtDiscntset", defDiscntset); // 跨省升级时，成员初始资费列表
        parainfo.put("canOrderDis", canOrderDis); // 判断该集团能否定制跨省5个资费
        parainfo.put("IS_MODIFY", isModify); // 统一付费优惠已有资料（YES），则前台不让操作（disabled=true）

        result.put("PARAM_INFO", parainfo);
        result.put("ATTRITEM", this.getAttrItem());
        result.put("ATTRITEMSET", this.getAttrItemSet());
        return result;
    }

    // 用户参数页面
    @Override
    public IData initCrtUs(IBizCommon bp, IData data) throws Throwable
    {
        IData result = super.initCrtUs(bp, data);
        IData parainfo = new DataMap();
        if (IDataUtil.isNotEmpty(result) && IDataUtil.isNotEmpty(result.getData("PARAM_INFO")))
        {
            parainfo = result.getData("PARAM_INFO");
        }

        IDataset datas = AttrItemInfoIntfViewUtil.qryAttrItemAInfosByIdAndIdTypeAttrObjEparchyCode(bp, data.getString("PRODUCT_ID", ""), "P", "2", ((CSBasePage) bp).getTradeEparchyCode());

        if (IDataUtil.isNotEmpty(datas))
        {
            IData transData = IDataUtil.hTable2STable(datas, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");
            getAttrItem().putAll(transData);
        }
        
        // add by lixiuyu@20101103 从TF_SM_MANAGER_JOBTYPE获取客户经理编码
        IData map = new DataMap();
        IDataset managerInfos = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryManagerIdJobType", map);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);

            }
        }

        map.clear();
        map.put("GROUP_ID", data.getString("GROUP_ID", ""));
        map.put("RSRV_TAG1", "0");
        map.put("RSRV_TAG2", "0");
        IDataset vpninfos = CSViewCall.call(bp, "CS.GrpExtInfoQrySVC.getVPNNOByGROUPID", map);
        if (IDataUtil.isNotEmpty(vpninfos))
        {
            int len = vpninfos.size();
            for (int i = 0; i < len; i++)
            {
                IData vpnInfoData = vpninfos.getData(i);
                String rsrvStr1 = vpnInfoData.getString("RSRV_STR1");
                String rsrvStr2 = vpnInfoData.getString("RSRV_STR2");
                String rsrvStr3 = vpnInfoData.getString("RSRV_STR3");
                vpnInfoData.put("DISPLAY_NAME", rsrvStr1 + "|" + rsrvStr2 + "|" + rsrvStr3);

            }
        }
        IDataset defualtDiscntset = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndEparchyCode(bp, "CGM", "80", ((CSBasePage) bp).getTradeEparchyCode());
        if (IDataUtil.isEmpty(defualtDiscntset))
        {
            CSViewException.apperr(VpmnUserException.VPMN_USER_177);
        }
        String PROVICE_VPN_DISCNT = "";
        for (int i = 0; i < defualtDiscntset.size(); i++)
        {
            String discntinfo = defualtDiscntset.getData(i).getString("PARA_CODE1", "");
            if (StringUtils.isBlank(PROVICE_VPN_DISCNT))
                PROVICE_VPN_DISCNT = discntinfo;
            else
                PROVICE_VPN_DISCNT = PROVICE_VPN_DISCNT + "," + discntinfo;
        }
        IData prodis = new DataMap();
        prodis.put("ATTR_VALUE", PROVICE_VPN_DISCNT);
        this.getAttrItem().put("PROVICE_VPN_DISCNT", prodis);
        result.put("ATTRITEM", this.getAttrItem());

        parainfo.put("managerInfos", managerInfos);
        parainfo.put("vpnInfos", vpninfos);
        parainfo.put("userParamInfos", datas);
        parainfo.put("METHOD_NAME", "CrtUs");
        parainfo.put("defualtDiscntset", defualtDiscntset); // 跨省升级时，成员初始资费下拉列表
        result.put("PARAM_INFO", parainfo);
        return result;
    }

    /**
     * 根据集团属性查询SCP,JS调用
     * 
     * @param cycle
     * @throws Exception
     */
    public IData querySCPInfos(IBizCommon bp, IData data) throws Exception
    {
        IData results = new DataMap();
        IData paramresult = new DataMap();
        boolean result = false;
        String vpnType = data.getString("VPN_GRP_ATTR", "");

        String scpCode = "10";
        if (vpnType.equals("A"))
        {

            IDataset datas = CommParaInfoIntfViewUtil.qryCommParasByParamAttrAndParamCodeEparchyCode(bp, "CGM", "246", ((CSBizHttpHandler) bp).getVisit().getCityCode(), ((CSBizHttpHandler) bp).getTradeEparchyCode());
            if (IDataUtil.isNotEmpty(datas))
            {
                scpCode = datas.getData(0).getString("PARA_CODE1");
            }
        }// add by lixiuyu@20100824 新增智能网系统（SCP6)
        else if (vpnType.equals("9"))
        {
            scpCode = "12";
        }
        // end by lixiuyu@20100824
        // add by wangyf6@20120112 新增智能网系统(SCP7)
        else if (vpnType.equals("8"))
        {
            scpCode = "13";
        }
        // end by wangyf6@20120112

        IData inparam = new DataMap();
        inparam.put("TYPE_ID", "TD_B_SCP");
        inparam.put("DATA_ID", scpCode);
        IDataset datas = StaticUtil.getStaticList("TD_B_SCP", scpCode);
        IData data1 = new DataMap();
        if (IDataUtil.isNotEmpty(datas))
        {
            data1 = datas.getData(0);
            paramresult.put("DATA_NAME", data1.getString("DATA_NAME", ""));
            paramresult.put("DATA_ID", data1.getString("DATA_ID", ""));
        }
        result = true;
        paramresult.put("RESULT", result);
        results.put("AJAX_DATA", paramresult);
        return results;
    }

    public String queryStaffPriv(IBizCommon bp, IData data) throws Exception
    {
        String str = "false";
        IData data1 = new DataMap();
        IData condition = new DataMap();
        String staffId = ((CSBasePage) bp).getVisit().getStaffId();
        data1.put("STAFF_ID", staffId);
        data1.put("DATA_CODE", "SYS126");
        data1.put("DATA_TYPE", "1");
        // IDataset rightinfo = ParamQry.getStaffRight(pd, data1); // TF_M_STAFFDATARIGHT", "SEL_BY_STAFFID_DATACODE

        IDataset rightinfo = CSViewCall.call(bp, "CS.StaffInfoQrySVC.queryDataRightByIdNumCode", data1);
        if ("SUPERUSR".equals(staffId) || rightinfo.size() > 0)
        {
            // condition.put("RIGHT_CODE", "true");
            str = "true";
        }

        // setStaffright(condition);
        return str;
    }

    public void setCondition(IData condition)
    {

    }

    public void setDefualtDiscntSet(IDataset infos)
    {

    }

    public void setScp(String scp)
    {
        this.scp = scp;
    }

    /**
     * 变成全国集团的时候，要先校验集团V网下面的短号是否合格
     * 
     * @param cycle
     * @throws Exception
     */
    public IData checkShortNumber(IBizCommon bp, IData data) throws Exception
    {
        String user_id = data.getString("GRP_USER_ID");
        // 根据serial_number取出TF_F_USER表的user_id SS.VpnUnitSVC.validchk801Svc
        // IData sourceData = UCAInfoIntfViewUtil.qryGrpUserInfoByGrpSn(bp, number);
        //
        // String user_id = sourceData.getString("USER_ID");
        // 根据user_id取出tf_f_relation_uu表中的sho短号
        IDataset relationUU = RelationUUInfoIntfViewUtil.qryGrpRelaUUInfosByUserIdAAndRelationTypeCode(bp, user_id, "20");
        IData shortCodeData1;
        String shortCodeStr;
        IData data3 = new DataMap();
        for (int i = 0; i < relationUU.size(); i++)
        {
            shortCodeData1 = (IData) relationUU.get(i);
            shortCodeStr = shortCodeData1.getString("SHORT_CODE");
            if (shortCodeStr == null)
            {
                data3.put("FLAG", "0");
                data3.put("ERR_MESSAGE", "集团下存在没有短号的成员，不能办理本业务!");
                break;
            }
            if (shortCodeStr.startsWith("6") && !(shortCodeStr.startsWith("60")) && (shortCodeStr.length() >= 3 && shortCodeStr.length() <= 6)) // j2ee去掉!(shortCodeStr.startsWith("69"))

            {
                continue;
            }
            else
            {
                data3.put("FLAG", "0");
                if (!(shortCodeStr.startsWith("6")))
                {
                    data3.put("ERR_MESSAGE", "集团下存在不以6打头的短号，不能办理本业务!");
                }
                if (shortCodeStr.startsWith("60"))
                {
                    data3.put("ERR_MESSAGE", "集团下存在以60打头的短号，不能办理本业务!");
                }
                if (shortCodeStr.length() < 3 || shortCodeStr.length() > 6)
                {
                    data3.put("ERR_MESSAGE", "集团下存在长度不符合的短号，不能办理本业务 !");
                }
                break;
            }
        }
        if (!(data3.containsKey("FLAG")))
        {
            data3.put("FLAG", "1");
        }

        IData results = new DataMap();
        results.put("AJAX_DATA", data3);
        return results;
    }
    
    /**
     * 过滤分管客户经理
     * 
     * @param cycle
     * @throws Exception
     */
    public IData filterCustManagers(IBizCommon bp, IData data) throws Exception
    {
        IData outData = new DataMap();
        IData resultData = new DataMap();
        IData paramData = new DataMap();        
        paramData.put("CUST_MANAGER_NAME", data.getString("FILTER_CUST_MANAGER",""));
        
        IDataset managerInfos = CSViewCall.call(bp, "CS.StaffInfoQrySVC.qryFilterManagerIdJobType", paramData);
        if (IDataUtil.isNotEmpty(managerInfos))
        {
            int len = managerInfos.size();
            for (int i = 0; i < len; i++)
            {
                IData infoData = managerInfos.getData(i);
                String rsrvStr1 = infoData.getString("CUST_MANAGER_NAME");
                String rsrvStr2 = infoData.getString("CUST_MANAGER_ID");
                infoData.put("CUST_MANAGER_NAME", rsrvStr1 + "|" + rsrvStr2);
            }
        }
        
        resultData.put("managerInfoList", managerInfos);
        outData.put("AJAX_DATA", resultData);
        return outData;
    }

}
