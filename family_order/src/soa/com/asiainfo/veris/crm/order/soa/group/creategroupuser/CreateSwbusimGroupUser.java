
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateSwbusimGroupUser extends CreateGroupUser
{
    private IData paramData;

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        data.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        data.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String productId = reqData.getUca().getProductId();

        // 处理产品参数信息
        paramData = (IData) Clone.deepClone(reqData.cd.getProductParamMap(productId));

        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // 一些个性化参数存放到主台帐和USER表的预留字段里, 因此将产品参数信息清空
        reqData.cd.putProductParamList(productId, new DatasetList());

    }

    /**
     * 处理用户预留字段信息
     * 
     * @throws Exception
     */
    public void setTradeUser(IData userData) throws Exception
    {
        super.setTradeUser(userData);

        userData.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        userData.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        userData.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));

    }

}
