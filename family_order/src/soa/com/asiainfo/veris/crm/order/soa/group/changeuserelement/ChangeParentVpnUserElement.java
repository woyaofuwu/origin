
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.groupunit.VpnUnit;

public class ChangeParentVpnUserElement extends ChangeUserElement
{
    private IData productParam;

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 修改用户信息
        actTradeUser();

        // 修改用户VPN信息
        actTradeUserVpn();
    }

    /**
     * 处理用户预留字段信息
     * 
     * @throws Exception
     */
    public void actTradeUser() throws Exception
    {
    	IDataset tradeUserDatas = bizData.getTradeUser();
    	if(IDataUtil.isNotEmpty(tradeUserDatas)){
    		IData userData = tradeUserDatas.getData(0);
            userData.put("RSRV_STR4", productParam.getString("RSRV_STR4"));
            userData.put("RSRV_STR5", productParam.getString("MAX_LINKMAN_NUM"));
            userData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE"));
            userData.put("RSRV_STR7", productParam.getString("CUST_MANAGER"));
    	}else{
    		IData userData = reqData.getUca().getUser().toData();
            userData.put("RSRV_STR4", productParam.getString("RSRV_STR4"));
            userData.put("RSRV_STR5", productParam.getString("MAX_LINKMAN_NUM"));
            userData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE"));
            userData.put("RSRV_STR7", productParam.getString("CUST_MANAGER"));
            userData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue()); // 修改
            super.addTradeUser(userData);
    	}   	
        
    }

    /**
     * 处理VPN用户信息
     * 
     * @throws Exception
     */
    public void actTradeUserVpn() throws Exception
    {
        IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(reqData.getUca().getUserId());

        if (IDataUtil.isEmpty(userVpnList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_20, reqData.getUca().getSerialNumber());
        }

        IData userVpnData = userVpnList.getData(0);

        userVpnData.put("CALL_NET_TYPE", VpnUnit.comCallNetTypeField(productParam)); // 呼叫网络类型 CALL_NET_TYPE
        userVpnData.put("CALL_AREA_TYPE", productParam.getString("CALL_AREA_TYPE", "")); // 呼叫区域类型 CALL_AREA_TYPE

        userVpnData.put("FUNC_TLAGS", "00000000000000000000");

        userVpnData.put("MAX_CLOSE_NUM", productParam.getString("MAX_CLOSE_NUM", "")); // 最大闭合用户群数 MAX_CLOSE_NUM
        userVpnData.put("MAX_NUM_CLOSE", productParam.getString("MAX_NUM_CLOSE", "")); // 单个闭合用户群能包含的最大用户数 MAX_NUM_CLOSE
        userVpnData.put("PERSON_MAXCLOSE", productParam.getString("PERSON_MAXCLOSE", "")); // 单个用户最大可加入的闭合群数
        // PERSON_MAXCLOSE
        userVpnData.put("MAX_INNER_NUM", productParam.getString("MAX_INNER_NUM", "")); // 最大网内号码总数 MAX_INNER_NUM
        userVpnData.put("MAX_OUTNUM", productParam.getString("MAX_OUTNUM", "")); // 最大网外号码总数 MAX_OUTNUM
        userVpnData.put("MAX_USERS", productParam.getString("MAX_USERS", "")); // 集团最大用户数-海南固定为10??
        userVpnData.put("MAX_TELPHONIST_NUM", productParam.getString("MAX_TELPHONIST_NUM", "")); // 话务员最大数
        // MAX_TELPHONIST_NUM

        // 6、HAIN有特殊处理MAX_LINKMAN_NUM=rsrvStr5(但CB里没找到这个控件)
        userVpnData.put("MAX_LINKMAN_NUM", productParam.getString("MAX_LINKMAN_NUM", ""));
        userVpnData.put("WORK_TYPE_CODE", productParam.getString("WORK_TYPE_CODE", "")); // VPMN集团类型
        userVpnData.put("VPN_SCARE_CODE", productParam.getString("VPN_SCARE_CODE", "")); // 集团范围属性--td_s_commpara表配置（BMS,1,0），根据配置输入或读取
        userVpnData.put("OVER_FEE_TAG", productParam.getString("OVER_FEE_TAG", "")); // 呼叫超出限额处理标记 OVER_FEE_TAG

        userVpnData.put("SINWORD_TYPE_CODE", productParam.getString("SINWORD_TYPE_CODE", "")); // 语种选择
        userVpnData.put("CUST_MANAGER", productParam.getString("CUST_MANAGER", ""));
        userVpnData.put("SHORT_CODE_LEN", productParam.getString("SHORT_CODE_LEN", ""));

        userVpnData.put("VPN_BUNDLE_CODE", productParam.getString("VPN_BUNDLE_CODE", ""));
        userVpnData.put("MANAGER_NO", productParam.getString("MANAGER_NO", ""));

        userVpnData.put("FEEINDEX", productParam.getString("DISCNT_CODE", userVpnData.getString("FEEINDEX"))); // 集团优惠类型
        userVpnData.put("RSRV_STR1", productParam.getString("DISCNT_CODE", userVpnData.getString("FEEINDEX")));
        userVpnData.put("RSRV_STR4", productParam.getString("RSRV_STR4", ""));
        userVpnData.put("RSRV_STR5", productParam.getString("CUST_MANAGER", ""));
        userVpnData.put("RSRV_STR6", productParam.getString("WORK_TYPE_CODE", ""));
        userVpnData.put("RSRV_STR9", productParam.getString("DISCNT_CODE", userVpnData.getString("FEEINDEX")));
        userVpnData.put("RSRV_STR10", productParam.getString("WORK_TYPE_CODE", ""));

        userVpnData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeVpn(userVpnData);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String productId = reqData.getUca().getProductId();

        productParam = (IData) Clone.deepClone(reqData.cd.getProductParamMap(productId));

        if (IDataUtil.isEmpty(productParam))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // VPMN一些个性化参数存放到主台帐和USER表的预留字段里, 因此将产品参数信息清空
        reqData.cd.putProductParamList(productId, new DatasetList());

        // 判断智能网优惠参数信息
        IDataset discntList = reqData.cd.getDiscnt();

        if (IDataUtil.isEmpty(discntList))
        {
            return;
        }

        for (int i = 0, row = discntList.size(); i < row; i++)
        {
            IData discntData = discntList.getData(i);

            // 判断新增资费
            if (TRADE_MODIFY_TAG.Add.getValue().equals(discntData.getString("MODIFY_TAG")))
            {
                String discntCode = discntData.getString("ELEMENT_ID");

                IDataset paramList = ParamInfoQry.getCommparaByCode("CSM", "6014", discntCode, CSBizBean.getUserEparchyCode());

                if (IDataUtil.isEmpty(paramList))
                {
                    CSAppException.apperr(VpmnUserException.VPMN_USER_17);
                }

                IData paramData = paramList.getData(0);

                productParam.put("RSRV_STR4", paramData.getString("PARA_CODE1"));
                productParam.put("DISCNT_CODE", discntCode);

                break;
            }
        }

        // 特殊处理服务信息, 修改800智能网VPMN集团, 服务开通才会发联指
        IDataset userSvcList = UserSvcInfoQry.qrySvcInfoByUserIdSvcId(reqData.getUca().getUserId(), "850");

        if (IDataUtil.isEmpty(userSvcList))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_21);
        }

        IData userSvcData = userSvcList.getData(0);
        userSvcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        IDataset svcList = reqData.cd.getSvc();
        svcList.add(userSvcData);

        reqData.cd.putSvc(svcList);
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
}
