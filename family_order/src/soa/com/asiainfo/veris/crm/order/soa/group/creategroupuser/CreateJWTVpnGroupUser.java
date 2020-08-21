
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.CustGroupTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

/**
 * 监务通集团产品开户
 * 
 * @author huyong
 * @version 2014-03-18
 */
public class CreateJWTVpnGroupUser extends CreateGroupUser
{
    private static final Logger logger = Logger.getLogger(CreateJWTVpnGroupUser.class);

    public CreateJWTVpnGroupUser()
    {

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVPMNVpn();
    }

    public IData getParamData() throws Exception
    {
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isEmpty(paramData))
            CSAppException.apperr(ParamException.CRM_PARAM_345);

        if (logger.isDebugEnabled())
            logger.debug("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<  执行CreateJWTVpnGroupUser类 getParamData() 得到产品页面传过来的参数<<<<<<<<<<<<<<<<<<<");

        return paramData;
    }

    /**
     * 处理台帐VPN子表的数据-用户
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        IDataset discntcodes = reqData.cd.getDiscnt();

        String rsrvStr4 = "";
        String discntCode = "";
        if (IDataUtil.isNotEmpty(discntcodes))
        {
            IData discnt = (IData) discntcodes.get(0);
            String dis_code = discnt.getString("ELEMENT_ID");
            IDataset infos = ParamInfoQry.getCommparaByCode("CSM", "6014", dis_code, CSBizBean.getUserEparchyCode());
            if (IDataUtil.isEmpty(infos))
                CSAppException.apperr(VpmnUserException.VPMN_USER_17);

            IData param = (IData) infos.get(0);
            rsrvStr4 = param.getString("PARA_CODE1");
            discntCode = param.getString("PARAM_CODE");
        }

        IData productParam = getParamData();

        IData vpnData = new DataMap();

        // 获取VPN类型 2为跨省
        String vpn_scare_code = productParam.getString("VPN_SCARE_CODE", "");

        // 3为跨省集团，VPN_NO通过集团资料获取字段VPMN_GROUP_ID RSRV_STR1 字段为集团省代码
        if ("2".equals(vpn_scare_code))
        {

            CustGroupTradeData groupInfo = reqData.getUca().getCustGroup();
            String vpmn_group_id = groupInfo.getRsrvStr5();
            String prosrc = groupInfo.getRsrvStr1();

            if (StringUtils.isBlank(vpmn_group_id) || StringUtils.isBlank(prosrc))
                CSAppException.apperr(VpmnUserException.VPMN_USER_19);

            String[] prosrcs = prosrc.split("\\|");

            if (prosrcs.length < 2)
                CSAppException.apperr(VpmnUserException.VPMN_USER_18);

            prosrc = prosrcs[0];
            String scpgt = prosrcs[1];
            vpnData.put("VPN_NO", vpmn_group_id);
            vpnData.put("RSRV_STR1", prosrc);
            vpnData.put("RSRV_STR2", scpgt);

        }
        else
        {
            vpnData.put("VPN_NO", reqData.getUca().getSerialNumber());
        }

        vpnData.put("USER_ID_A", reqData.getUca().getUserId());
        String groupArea = TagInfoQry.getSysTagInfo("CS_INF_AREACODE", "TAG_INFO", "898", CSBizBean.getUserEparchyCode());
        // String groupArea = ParamInfoQry.getSysTagInfo("CS_INF_AREACODE", "TAG_INFO",
        // "898",CSBizBean.getUserEparchyCode());
        vpnData.put("GROUP_AREA", groupArea);
        vpnData.put("SCP_CODE", productParam.getString("SCP_CODE"));
        vpnData.put("VPMN_TYPE", "0");
        vpnData.put("VPN_TYPE", ""); // VPN类型：0-IVPN,1-WVPN
        String provinceCode = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HAIN", CSBizBean.getUserEparchyCode());
        // ParamInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_INFO", "HAIN",CSBizBean.getUserEparchyCode());
        vpnData.put("PROVINCE_CODE", provinceCode);
        vpnData.put("SUB_STATE", "0");
        vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam)); // 呼叫网络类型 CALL_NET_TYPE
        vpnData.put("CALL_AREA_TYPE", productParam.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型 CALL_AREA_TYPE

        // FUNC_TLAGS
        String funcTlags = "1100000000000000000000000000000000000000";

        String vpnTagSetStr = productParam.getString("VPN_TAG_SET");

        if (!StringUtils.isBlank(vpnTagSetStr))
        {
            IDataset vpnTagSet = new DatasetList(vpnTagSetStr);
            funcTlags = VpnUnit.comFlagField(funcTlags, vpnTagSet);
        }
        String strCallNetType = vpnData.getString("CALL_NET_TYPE");
        String strCallAreaType = vpnData.getString("CALL_AREA_TYPE");
        String strFuncFlagsTem = "";
        if (!StringUtils.isBlank(strCallAreaType) && !StringUtils.isBlank(strCallNetType))
        {
            for (int i = 0; i < 4; i++)
            {
                if (strCallNetType.substring(i, i + 1).equals("1"))
                {
                    strFuncFlagsTem += strCallAreaType;
                    strFuncFlagsTem += strCallAreaType;
                }
                else
                {
                    strFuncFlagsTem += "11";
                }
            }
            funcTlags = strFuncFlagsTem + funcTlags.substring(8);
        }

        vpnData.put("FUNC_TLAGS", funcTlags);

        vpnData.put("FEEINDEX", discntCode); // 集团优惠类型
        vpnData.put("INTER_FEEINDEX", "-1");
        vpnData.put("OUT_FEEINDEX", "-1");
        vpnData.put("OUTGRP_FEEINDEX", "-1");
        vpnData.put("NOTDISCNT_FEEINDEX", "-1");
        vpnData.put("MAX_CLOSE_NUM", productParam.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数 MAX_CLOSE_NUM
        vpnData.put("MAX_NUM_CLOSE", productParam.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数 MAX_NUM_CLOSE
        vpnData.put("PERSON_MAXCLOSE", productParam.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数 PERSON_MAXCLOSE
        vpnData.put("MAX_INNER_NUM", productParam.getString("MAX_INNER_NUM", "")); // 最大网内号码总数 MAX_INNER_NUM
        vpnData.put("MAX_OUTNUM", productParam.getString("MAX_OUTNUM", "")); // 最大网外号码总数 MAX_OUTNUM
        vpnData.put("MAX_USERS", productParam.getString("MAX_USERS", "")); // 集团最大用户数-海南固定为10??
        vpnData.put("MAX_TELPHONIST_NUM", productParam.getString("MAX_TELPHONIST_NUM", "")); // 话务员最大数
        // MAX_TELPHONIST_NUM

        // 6、HAIN有特殊处理MAX_LINKMAN_NUM=rsrvStr5(但CB里没找到这个控件)
        vpnData.put("MAX_LINKMAN_NUM", productParam.getString("MAX_LINKMAN_NUM", ""));
        vpnData.put("WORK_TYPE_CODE", productParam.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
        vpnData.put("VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
        vpnData.put("OVER_FEE_TAG", productParam.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记 OVER_FEE_TAG

        vpnData.put("LIMFEE_TYPE_CODE", "1111");
        vpnData.put("SINWORD_TYPE_CODE", productParam.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        vpnData.put("CUST_MANAGER", productParam.getString("CUST_MANAGER", ""));

        vpnData.put("VPN_BUNDLE_CODE", productParam.getString("VPN_BUNDLE_CODE", ""));
        vpnData.put("MANAGER_NO", productParam.getString("MANAGER_NO", ""));
        vpnData.put("SHORT_CODE_LEN", productParam.getString("SHORT_CODE_LEN", "0"));
        vpnData.put("RSRV_STR1", discntCode);
        vpnData.put("RSRV_STR4", rsrvStr4);
        vpnData.put("RSRV_STR5", productParam.getString("CUST_MANAGER"));
        vpnData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE", ""));
        vpnData.put("RSRV_STR9", discntCode);
        vpnData.put("RSRV_STR10", productParam.getString("WORK_TYPE_CODE"));
        vpnData.put("OPEN_DATE", getAcceptTime());
        vpnData.put("STATE", "ADD");
        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        vpnData.put("REMOVE_TAG", "0");
        addTradeVpn(IDataUtil.idToIds(vpnData));
    }

    /**
     * 重写主台账表,补充预留字段
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        IData param = getParamData();
        data.put("RSRV_STR6", param.getString("WORK_TYPE_CODE"));
        data.put("RSRV_STR7", param.getString("CUST_MANAGER"));
        data.put("RSRV_STR5", param.getString("MAX_LINKMAN_NUM"));
    }

    /**
     *重写基类用户台账表,补充字段
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        IData paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
            map.put("USER_TYPE_CODE", "C"); // 行业类型-集团V网类别
        map.put("RSRV_STR6", paramData.getString("WORK_TYPE_CODE"));
        map.put("RSRV_STR7", paramData.getString("CUST_MANAGER"));
        map.put("RSRV_STR5", paramData.getString("MAX_LINKMAN_NUM"));
    }
}
