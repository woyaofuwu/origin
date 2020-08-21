
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateTDbusinessTeleGroupUser extends CreateGroupUser
{

    private IData paramData;

    /**
     * 构造函数
     */
    public CreateTDbusinessTeleGroupUser()
    {

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        String curProductId = reqData.getUca().getProductId();

        paramData = (IData) Clone.deepClone(reqData.cd.getProductParamMap(curProductId));
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }

        // 一些个性化参数存放到主台帐和USER表的预留字段里, 因此将产品参数信息清空
        reqData.cd.putProductParamList(curProductId, new DatasetList());

    }

    /**
     * @description 处理user表数据
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);
        // 获取参数

        map.put("RSRV_STR5", paramData.getString("CUST_MANAGER", ""));
        map.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        map.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        map.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));
    }
}
