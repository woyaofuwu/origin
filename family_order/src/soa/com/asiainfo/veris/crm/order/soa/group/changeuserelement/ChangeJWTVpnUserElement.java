
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.SvcException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChangeJWTVpnUserElement extends ChangeUserElement
{
    private static final Logger logger = Logger.getLogger(ChangeJWTVpnUserElement.class);

    private String rsrvStr4 = "";// 需要插user表的预留字段

    private String dis_code = "";

    public ChangeJWTVpnUserElement()
    {

    }

    /**
     * ��ɵǼ���Ϣ
     * 
     * @author sht
     * @throws Exception
     */
    /*
     * public void setRegBeforeData() throws Exception { super.setRegBeforeData(); IDataset params =
     * GroupUtil.getDataset(productParam, "OLD_PRODUCT_PARAM"); //���������ʹ��� String callNetTypeNew =
     * VpnUnit.comCallNetTypeField(productParam); productParam.put("CALL_NET_TYPE", callNetTypeNew); Iterator<String>
     * iterator = productParam.keySet().iterator(); while (iterator.hasNext()) { String key = iterator.next(); boolean
     * isExist = false; for (int i = 0; i < params.size(); i++) { IData param = params.getData(i); //�������� if
     * (key.equals("VPN_TAG_SET") && param.getString("ATTR_CODE").equals("FUNC_TLAGS")) { String funcTlags =
     * "1100000000000000000000000000000000000000"; IDataset vpnTagSet = GroupUtil.getDataset(productParam,
     * "VPN_TAG_SET"); if(vpnTagSet != null && vpnTagSet.size()>0) { funcTlags = VpnUnit.comFlagField(funcTlags,
     * vpnTagSet); } if (!funcTlags.equals(param.getString("ATTR_VALUE", ""))) { modifyParamMap.put("FUNC_TLAGS",
     * funcTlags); isChange = true; } isExist = true; break; } else if (param.getString("ATTR_CODE").equals(key)) { if
     * (!param.getString("ATTR_VALUE", "").equals(productParam.getString(key))) { modifyParamMap.put(key,
     * productParam.getString(key, "")); isChange = true; } isExist = true; break; } } //����ֶδ��� if (!isExist &&
     * !key.equals("OLD_PRODUCT_PARAM") && !key.equals("VPN_GRP_ATTR") && !key.equals("CUST_MANAGER_Box_Text")) {
     * modifyParamMap.put(key, productParam.getString(key, "")); isChange = true; } } //ͳһ�����Ż� if
     * (!modifyParamMap.getString("GRP_PAY_DISCNT", "").equals("")) { modifyUser = true; commData.put("RSRV_STR5",
     * GroupUtil.getAndDelColValueFormIData(modifyParamMap, "GRP_PAY_DISCNT")); commData.put("RSRV_STR10",
     * commData.getString("RSRV_STR5")); } IDataset discntcode = (IDataset) commData.get("DISCNT"); if(null !=
     * discntcode && discntcode.size()>0) { for (int i=0; i<discntcode.size(); i++){ IData discnt = (IData)
     * discntcode.get(i); if("ADD".equals(discnt.getString("STATE"))) { String dis_code =
     * discnt.getString("ELEMENT_ID"); IDataset infos = ParamQry.getCommpara(pd, "CSM", "6014", dis_code,
     * pd.getContext().getEpachyId()); if (null == infos || infos.size() == 0) {
     * common.warn("�����������Żݲ�����ڣ�����������ã�"); } IData param = (IData) infos.get(0);
     * commData.put("DISCNT_CODE", dis_code); commData.put("RSRV_STR4", param.getString("PARA_CODE1")); modifyUser =
     * true; break; } } } }
     */

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        IDataset discntcodes = reqData.cd.getDiscnt();
        // USER_VPN
        if (IDataUtil.isNotEmpty(getParamData()) || IDataUtil.isNotEmpty(discntcodes))
        {
            infoRegDataVPMNVpn();
        }
        if (IDataUtil.isNotEmpty(discntcodes))
        {
            infoRegDataSvc();
        }
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
     * 修改800智能网VPMN集团，服务开通才会发联指
     * 
     * @throws Exception
     */
    private void infoRegDataSvc() throws Exception
    {

        IDataset svcInfos = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUserId(), "850");

        if (IDataUtil.isEmpty(svcInfos))
            CSAppException.apperr(SvcException.CRM_SVC_2);

        IData svcInfo = svcInfos.getData(0);

        svcInfo.put("ELEMENT_ID", "850");
        svcInfo.put("STATE", "MODI");
        svcInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        addTradeSvc(IDataUtil.idToIds(svcInfo));
    }

    public void infoRegDataVPMNVpn() throws Exception
    {
        IDataset discntcodes = reqData.cd.getDiscnt();

        for (int i = 0, iSize = discntcodes.size(); i < iSize; i++)
        {
            IData discnt = discntcodes.getData(0);
            if ("ADD".equals(discnt.getString("STATE")))
            {
                dis_code = discnt.getString("ELEMENT_ID");
                IDataset infos = ParamInfoQry.getCommparaByCode("CSM", "6014", dis_code, CSBizBean.getUserEparchyCode());
                if (IDataUtil.isEmpty(infos))
                    CSAppException.apperr(VpmnUserException.VPMN_USER_17);
                IData param = (IData) infos.get(0);
                rsrvStr4 = param.getString("PARA_CODE1");
                break;
            }
        }

        String userId = reqData.getUca().getUserId();
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userId);

        if (IDataUtil.isEmpty(userVpnList))
            CSAppException.apperr(ParamException.CRM_PARAM_343);

        IData vpnData = userVpnList.getData(0);

        IData productParam = getParamData();
        vpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam)); // 呼叫网络类型 CALL_NET_TYPE
        vpnData.put("CALL_AREA_TYPE", productParam.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型 CALL_AREA_TYPE

        vpnData.put("FUNC_TLAGS", "00000000000000000000");

        vpnData.put("MAX_CLOSE_NUM", productParam.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数 MAX_CLOSE_NUM
        vpnData.put("MAX_NUM_CLOSE", productParam.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数 MAX_NUM_CLOSE
        vpnData.put("PERSON_MAXCLOSE", productParam.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数 PERSON_MAXCLOSE
        vpnData.put("MAX_INNER_NUM", productParam.getString("MAX_INNER_NUM", "")); // 最大网内号码总数 MAX_INNER_NUM
        vpnData.put("MAX_OUTNUM", productParam.getString("MAX_OUTNUM", "")); // 最大网外号码总数 MAX_OUTNUM
        vpnData.put("MAX_USERS", productParam.getString("MAX_USERS", "")); // 集团最大用户数-海南固定为10??
        vpnData.put("MAX_TELPHONIST_NUM", productParam.getString("MAX_TELPHONIST_NUM", ""));// 话务员最大数 MAX_TELPHONIST_NUM

        // 6、HAIN有特殊处理MAX_LINKMAN_NUM=rsrvStr5(但CB里没找到这个控件)
        vpnData.put("MAX_LINKMAN_NUM", productParam.getString("MAX_LINKMAN_NUM", ""));
        vpnData.put("WORK_TYPE_CODE", productParam.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
        vpnData.put("VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
        vpnData.put("OVER_FEE_TAG", productParam.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记 OVER_FEE_TAG

        vpnData.put("SINWORD_TYPE_CODE", productParam.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        vpnData.put("CUST_MANAGER", productParam.getString("CUST_MANAGER", ""));

        vpnData.put("VPN_BUNDLE_CODE", productParam.getString("VPN_BUNDLE_CODE", ""));
        vpnData.put("MANAGER_NO", productParam.getString("MANAGER_NO", ""));

        vpnData.put("FEEINDEX", dis_code); // 集团优惠类型
        vpnData.put("RSRV_STR1", dis_code);
        vpnData.put("RSRV_STR4", rsrvStr4);
        vpnData.put("RSRV_STR5", productParam.getString("CUST_MANAGER"));
        vpnData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE", ""));
        vpnData.put("RSRV_STR9", dis_code);
        vpnData.put("RSRV_STR10", productParam.getString("WORK_TYPE_CODE"));

        vpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        vpnData.put("STATE", "MODI");

        addTradeVpn(IDataUtil.idToIds(vpnData));
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

        IData param = getParamData();
        data.put("RSRV_STR6", param.getString("WORK_TYPE_CODE"));
        data.put("RSRV_STR7", param.getString("CUST_MANAGER"));
        data.put("RSRV_STR5", param.getString("MAX_LINKMAN_NUM"));
    }

    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        map.put("RSRV_STR4", dis_code);

        IData paramData = getParamData();
        if (IDataUtil.isNotEmpty(paramData))
        {
            map.put("RSRV_STR6", paramData.getString("WORK_TYPE_CODE"));
            map.put("RSRV_STR7", paramData.getString("CUST_MANAGER"));
        }
    }
}
