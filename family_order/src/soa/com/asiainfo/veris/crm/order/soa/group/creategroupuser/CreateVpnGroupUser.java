
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpExtInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

/**
 * @description 智能VPMN集团产品受理Bean类
 * @author yish
 */
public class CreateVpnGroupUser extends CreateGroupUser
{
    private static transient Logger logger = Logger.getLogger(CreateVpnGroupUser.class);

    private String vpnTageSet = "[]";

    private IData paramData = new DataMap();

    public CreateVpnGroupUser()
    {

    }

    /**
     * @description 业务执行前处理
     * @author yish
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramData = getParamData();
        // add by lixiuyu@20100531 VPMN集团订购订购“来电显示服务包”时，判断集团是否订购漫游短号服务或者是跨省VPMN
        validchk864Svc(); // j2ee 规则需改造
    }

    /**
     * @description 子类执行的动作
     * @author yish
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegDataVPMNVpn();
        
        //start add by wangyf6 at 20191111
        infoRegOtherData();
        //end add by wangyf6 at 20191111
        
    }

    /**
     * @description 得到智能VPMN产品参数
     * @author yish
     * @return
     * @throws Exception
     */
    public IData getParamData() throws Exception
    {
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        return paramData;
    }

    /**
     * @description 处理台帐VPN子表的数据-用户
     * @author yish
     * @date 2013-10-15
     * @throws Exception
     */
    public void infoRegDataVPMNVpn() throws Exception
    {
        // VPMN个性化参数
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // VPN数据
        IDataset dataset = new DatasetList();
        IData vpnData = super.infoRegDataVpn();
        // 获取VPN类型 2为全国集团
        String vpn_scare_code = paramData.getString("VPN_SCARE_CODE", "");

        // 2为全国集团，VPN_NO通过集团资料获取字段 VPMN_GROUP_ID RSRV_STR1 字段为集团省代码
        if ("2".equals(vpn_scare_code))
        {
            String group_id = reqData.getUca().getCustGroup().getGroupId();
            if (StringUtils.isBlank(group_id))
            {
                CSAppException.apperr(GrpException.CRM_GRP_677);
            }
            IData para = new DataMap();
            String vpnno = paramData.getString("VPN_NO", "");
            if ("".equals(vpnno))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_170);
            }
            para.put("GROUP_ID", group_id);
            para.put("RSRV_TAG1", "0");
            para.put("RSRV_TAG2", "0");
            para.put("VPN_NO", vpnno);

            IDataset vpninfos = GrpExtInfoQry.getVPNNOByVPNNO(para);
            if (IDataUtil.isEmpty(vpninfos))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_171);
            }
            IData vpninfo = vpninfos.getData(0);

            String prosrc = vpninfo.getString("RSRV_STR2", "");
            String scpgt = vpninfo.getString("RSRV_STR3", "");
            String dial_type = paramData.getString("DIAL_TYPE_CODE", "");

            vpnData.put("RSRV_STR1", prosrc);
            vpnData.put("RSRV_STR2", scpgt);
            vpnData.put("VPN_NO", vpnno);
            vpnData.put("RSRV_TAG1", dial_type); // 拨打方式 写死的长短号拨打
            vpnData.put("RSRV_STR3", paramData.getString("SERIAL_NUMBER"));

        }
        vpnData.put("USER_ID_A", reqData.getUca().getUserId());
        IDataset daset = TagInfoQry.getTagInfosByTagCode(reqData.getUca().getUserEparchyCode(), "CS_INF_AREACODE", null, null);
        if (IDataUtil.isNotEmpty(daset))
        {
            IData data = daset.getData(0);
            vpnData.put("GROUP_AREA", data.getString("TAG_INFO", "898"));
        }
        vpnData.put("SCP_CODE", paramData.getString("SCP_CODE")); // SCP代码
        vpnData.put("VPMN_TYPE", "0");
        vpnData.put("VPN_TYPE", ""); // VPN类型：0-IVPN,1-WVPN
        daset.clear();
        daset = TagInfoQry.getTagInfosByTagCode(reqData.getUca().getUserEparchyCode(), "PUB_INF_PROVINCE", null, null);
        if (IDataUtil.isNotEmpty(daset))
        {
            IData data = daset.getData(0);
            vpnData.put("PROVINCE_CODE", data.getString("TAG_INFO", "HAIN"));
        }
        vpnData.put("SUB_STATE", "0");
        vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(paramData)); // 呼叫网络类型 CALL_NET_TYPE
        vpnData.put("CALL_AREA_TYPE", paramData.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型 CALL_AREA_TYPE

        // FUNC_TLAGS
        String funcTlags = "1100000000000000000000000000000000000000";
        String vpnTageSet = paramData.getString("NOTIN_VPN_TAG_SET", "");

        if (StringUtils.isNotEmpty(vpnTageSet))
        {
            IDataset vpnTagSet = new DatasetList(vpnTageSet);
            funcTlags = VpnUnit.comFlagField(funcTlags, vpnTagSet);
        }
        String strCallNetType = vpnData.getString("CALL_NET_TYPE");
        String strCallAreaType = vpnData.getString("CALL_AREA_TYPE");
        String strFuncFlagsTem = "";
        if (strCallAreaType != "" && strCallNetType != "")
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

        vpnData.put("FEEINDEX", ""); // 集团优惠类型(CB里pt_CEnRecord.AddField("VPN0",
        // mstGetVPMNInfo->FieldByName("RSRV_STR1")->AsString.c_str());)
        IDataset objCommparas = ParamInfoQry.getCommparaByCode("CSM", "250", reqData.getUca().getProductId(), reqData.getUca().getUserEparchyCode());
        if (objCommparas != null && objCommparas.size() == 1)
        {
            IData objCommpara = objCommparas.getData(0);
            vpnData.put("INTER_FEEINDEX", objCommpara.getString("PARA_CODE2", ""));
            vpnData.put("OUT_FEEINDEX", objCommpara.getString("PARA_CODE3", ""));
            vpnData.put("OUTGRP_FEEINDEX", objCommpara.getString("PARA_CODE4", ""));
            vpnData.put("NOTDISCNT_FEEINDEX", objCommpara.getString("PARA_CODE7", ""));
        }
        else
        {
            vpnData.put("INTER_FEEINDEX", "-1");
            vpnData.put("OUT_FEEINDEX", "-1");
            vpnData.put("OUTGRP_FEEINDEX", "-1");
            vpnData.put("NOTDISCNT_FEEINDEX", "-1");
        }
        vpnData.put("MAX_CLOSE_NUM", paramData.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数
        vpnData.put("MAX_NUM_CLOSE", paramData.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数
        vpnData.put("PERSON_MAXCLOSE", paramData.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数
        vpnData.put("MAX_INNER_NUM", paramData.getString("MAX_INNER_NUM", "")); // 网内用户最大数
        vpnData.put("MAX_OUTNUM", paramData.getString("MAX_OUTNUM", "")); // 网外用户最大数
        vpnData.put("MAX_USERS", paramData.getString("MAX_USERS", "")); // 集团最大用户数-海南固定为10??
        vpnData.put("MAX_TELPHONIST_NUM", paramData.getString("MAX_TELPHONIST_NUM", "")); // 话务员最大数

        // 6、HAIN有特殊处理MAX_LINKMAN_NUM=rsrvStr5(但CB里没找到这个控件)
        vpnData.put("MAX_LINKMAN_NUM", paramData.getString("MAX_LINKMAN_NUM", ""));
        vpnData.put("WORK_TYPE_CODE", paramData.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
        vpnData.put("VPN_SCARE_CODE", paramData.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
        vpnData.put("OVER_FEE_TAG", paramData.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记 OVER_FEE_TAG

        vpnData.put("LIMFEE_TYPE_CODE", "1111");
        vpnData.put("SINWORD_TYPE_CODE", paramData.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        vpnData.put("CUST_MANAGER", paramData.getString("CUST_MANAGER", ""));

        vpnData.put("VPN_BUNDLE_CODE", paramData.getString("VPN_BUNDLE_CODE", ""));
        vpnData.put("MANAGER_NO", paramData.getString("MANAGER_NO", ""));
        vpnData.put("SHORT_CODE_LEN", paramData.getString("SHORT_CODE_LEN", "0")); // 集团分机号码长度：

        vpnData.put("RSRV_STR5", paramData.getString("VPN_CLASS", "")); // VPMN类别
        vpnData.put("OPEN_DATE", this.getAcceptTime());
        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        vpnData.put("REMOVE_TAG", "0");
        dataset.add(vpnData);
        addTradeVpn(dataset);

        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出CreateVpnGroupUser类 infoRegDataVPMNVpn()  插入vpn表数据：" + vpnData + "<<<<<<<<<<<<<<<<<<<");
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        if (IDataUtil.isNotEmpty(paramData))
        {
            data.put("RSRV_STR6", paramData.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
            data.put("RSRV_STR7", paramData.getString("CUST_MANAGER", "")); // 分管客户经理
            data.put("RSRV_STR10", paramData.getString("VPN_GRP_ATTR", "")); // 集团属性

        }
    }

    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        if (IDataUtil.isNotEmpty(paramData))
        {
            map.put("RSRV_STR6", paramData.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
            map.put("RSRV_STR7", paramData.getString("CUST_MANAGER", "")); // 分管客户经理
            map.put("RSRV_STR10", paramData.getString("VPN_GRP_ATTR", ""));// 集团属性
        }
    }

    /**
     * VPMN集团订购订购“来电显示服务包”(80000105)时，判断集团是否订购漫游短号服务(801)或者是跨省VPMN(vpn_scare_code=2)
     * 
     * @param Datas
     * @author lixiuyu@20100531
     * @throws Exception
     */
    public void validchk864Svc() throws Exception
    {
        IDataset svcdatas = reqData.cd.getSvc();
        IDataset grpPackages = reqData.cd.getGrpPackage();

        if (IDataUtil.isEmpty(paramData))
        {
            // 这里应该要判空报错的  暂时先直接返回
            return;
        }
        // 获取VPN类型 2为跨省
        String vpn_scare_code = paramData.getString("VPN_SCARE_CODE", "");
        String svcId801 = "";
        if (IDataUtil.isNotEmpty(svcdatas))
        {
            for (int i = 0; i < svcdatas.size(); i++)
            {
                IData svcdata = svcdatas.getData(i);
                if (TRADE_MODIFY_TAG.Add.getValue().equals(svcdata.getString("MODIFY_TAG", "")) && "801".equals(svcdata.getString("ELEMENT_ID", "")))
                {
                    svcId801 = svcdata.getString("ELEMENT_ID", "");
                    break;
                }
            }
        }

        if (IDataUtil.isNotEmpty(grpPackages))
        {
            for (int i = 0; i < grpPackages.size(); i++)
            {
                IData grpPackage = grpPackages.getData(i);
                String svcId = grpPackage.getString("ELEMENT_ID", "");

                if ("864".equals(svcId)) // 864:来电显示长号
                {
                    if (!"2".equals(vpn_scare_code) && !"801".equals(svcId801))
                    {
                        // j2ee common.warn("该集团不是跨省集团或者未订购漫游短号服务包，不能为成员订购来电显示服务包！");
                        CSAppException.apperr(VpmnUserException.VPMN_USER_172);
                    }
                }
            }
        }
    }
    
    
    /**
     * 处理台帐Other子表的数据
     * for REQ201910230023
     * @throws Exception
     */
    public void infoRegOtherData() throws Exception 
    {
        IDataset lineDataset = new DatasetList();
        String grpClipType = paramData.getString("GRP_CLIP_TYPE", "");
        String grpUserClipType = paramData.getString("GRP_USER_CLIP_TYPE", "");
        String grpUserMod = paramData.getString("GRP_USER_MOD", "");
        if(StringUtils.isNotBlank(grpClipType))
        {
            IData data = new DataMap();
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("RSRV_VALUE_CODE", "VPMN_GRPCLIP");
            data.put("RSRV_VALUE", "");
            
            data.put("RSRV_STR1", grpClipType); //GRP_CLIP_TYPE 呼叫来显方式
            if("1".equals(grpClipType))
            {
                data.put("RSRV_STR2", grpUserClipType);//GRP_USER_CLIP_TYPE 选择号显方式
                data.put("RSRV_STR3", grpUserMod);//GRP_USER_MOD 成员修改号显方式
            } 
            else if("0".equals(grpClipType))
            {
            	//如果是该值是0时，需要把这两个字段置空
                data.put("RSRV_STR2", "");//GRP_USER_CLIP_TYPE 选择号显方式
                data.put("RSRV_STR3", "");//GRP_USER_MOD 成员修改号显方式
            }
            data.put("START_DATE", getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            lineDataset.add(data);
            super.addTradeOther(lineDataset);
        }
    }
    
}
