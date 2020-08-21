
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class CreateParentVpnGroupUser extends CreateGroupUser
{
    private IData productParam;

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理VPN信息
        actTradeUserVpn();
    }

    /**
     * 处理VPN用户信息
     * 
     * @throws Exception
     */
    public void actTradeUserVpn() throws Exception
    {
        IData userVpnData = super.infoRegDataVpn();

        String vpnScareCode = productParam.getString("VPN_SCARE_CODE");

        // 全国集团特殊处理
        if ("2".equals(vpnScareCode))
        {
            String rsrvStr1 = reqData.getUca().getCustGroup().getRsrvStr1();
            rsrvStr1 = rsrvStr1 == null ? "" : rsrvStr1;

            String[] strArray = rsrvStr1.split("\\|");

            if (strArray.length < 2)
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_18);
            }

            String rsrvStr5 = reqData.getUca().getCustGroup().getRsrvStr5();

            if (StringUtils.isEmpty(rsrvStr5))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_19);
            }

            userVpnData.put("VPN_NO", rsrvStr5);
            userVpnData.put("RSRV_STR1", strArray[0]);
            userVpnData.put("RSRV_STR2", strArray[1]);
        }

        userVpnData.put("SCP_CODE", productParam.getString("SCP_CODE"));
        userVpnData.put("VPMN_TYPE", "0");
        userVpnData.put("VPN_TYPE", "");
        userVpnData.put("SUB_STATE", "0");
        userVpnData.put("FUNC_TLAGS", "00000000000000000000");
        userVpnData.put("FEEINDEX", productParam.getString("DISCNT_CODE"));

        userVpnData.put("MAX_CLOSE_NUM", productParam.getString("MAX_CLOSE_NUM", ""));
        userVpnData.put("MAX_NUM_CLOSE", productParam.getString("MAX_NUM_CLOSE", ""));
        userVpnData.put("PERSON_MAXCLOSE", productParam.getString("MAX_CLOSE_NUM", ""));
        userVpnData.put("MAX_INNER_NUM", productParam.getString("MAX_INNER_NUM", ""));
        userVpnData.put("MAX_OUTNUM", productParam.getString("MAX_OUTNUM"));
        userVpnData.put("MAX_USERS", productParam.getString("MAX_USERS"));
        userVpnData.put("MAX_LINKMAN_NUM", productParam.getString("MAX_LINKMAN_NUM", ""));
        userVpnData.put("MAX_TELPHONIST_NUM", productParam.getString("MAX_TELPHONIST_NUM"));

        userVpnData.put("WORK_TYPE_CODE", productParam.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
        userVpnData.put("VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取

        userVpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam));
        userVpnData.put("CALL_AREA_TYPE", productParam.getString("CALL_AREA_TYPE"));
        userVpnData.put("OVER_FEE_TAG", productParam.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记 OVER_FEE_TAG

        userVpnData.put("LIMFEE_TYPE_CODE", "1111");
        userVpnData.put("SINWORD_TYPE_CODE", productParam.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        userVpnData.put("CUST_MANAGER", productParam.getString("CUST_MANAGER", ""));

        userVpnData.put("VPN_BUNDLE_CODE", productParam.getString("VPN_BUNDLE_CODE", ""));
        userVpnData.put("MANAGER_NO", productParam.getString("MANAGER_NO", ""));
        userVpnData.put("SHORT_CODE_LEN", productParam.getString("SHORT_CODE_LEN", "0"));
        userVpnData.put("RSRV_STR1", productParam.getString("DISCNT_CODE"));
        userVpnData.put("RSRV_STR4", productParam.getString("RSRV_STR4", ""));
        userVpnData.put("RSRV_STR5", productParam.getString("CUST_MANAGER", ""));
        userVpnData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE", ""));
        userVpnData.put("RSRV_STR9", productParam.getString("DISCNT_CODE"));
        userVpnData.put("RSRV_STR10", productParam.getString("WORK_TYPE_CODE", ""));

        super.addTradeVpn(userVpnData);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String productId = reqData.getUca().getProductId();

        // 处理产品参数信息
        productParam = (IData) Clone.deepClone(reqData.cd.getProductParamMap(productId));

        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // VPMN一些个性化参数存放到主台帐和USER表的预留字段里, 因此将产品参数信息清空
        reqData.cd.putProductParamList(productId, new DatasetList());

        // 判断智能网优惠参数信息
        IDataset discntList = reqData.cd.getDiscnt();

        if (IDataUtil.isNotEmpty(discntList))
        {
            IData discntData = discntList.getData(0);

            String discntCode = discntData.getString("ELEMENT_ID");

            IDataset paramList = ParamInfoQry.getCommparaByCode("CSM", "6014", discntCode, CSBizBean.getUserEparchyCode());

            if (IDataUtil.isEmpty(paramList))
            {
                CSAppException.apperr(VpmnUserException.VPMN_USER_17);
            }

            IData paramData = paramList.getData(0);

            productParam.put("RSRV_STR4", paramData.getString("PARA_CODE1"));
            productParam.put("DISCNT_CODE", paramData.getString("PARAM_CODE"));
        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR4", productParam.getString("RSRV_STR4"));
        tradeData.put("RSRV_STR5", productParam.getString("MAX_LINKMAN_NUM"));
        tradeData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE"));
        tradeData.put("RSRV_STR7", productParam.getString("CUST_MANAGER"));
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("USER_TYPE_CODE", "C");
        map.put("RSRV_STR4", productParam.getString("RSRV_STR4"));
        map.put("RSRV_STR5", productParam.getString("MAX_LINKMAN_NUM"));
        map.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE"));
        map.put("RSRV_STR7", productParam.getString("CUST_MANAGER"));
    }
}
