
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateNetZxInputUser extends CreateGroupUser
{

    private IData paramData = new DataMap();// 参数数据

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        // 构建参数数据
        makeParam();
    }

    /**
     * 构建参数数据
     * 
     * @throws Exception
     */
    public void makeParam() throws Exception
    {
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isNotEmpty(productParam))
        {
            paramData = productParam;
            // 清空ProductParam,这样 产品参数信息 不会登记TF_B_TRADE_ATTR
            reqData.cd.putProductParamList(reqData.getUca().getProductId(), new DatasetList());
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        data.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        data.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        data.put("RSRV_STR9", paramData.getString("DETLINKPHONE", ""));
    }

    /**
     * 处理用户预留字段信息
     * 
     * @throws Exception
     */
    public void setTradeUser(IData userData) throws Exception
    {
        super.setTradeUser(userData);

        userData.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        userData.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        userData.put("RSRV_STR9", paramData.getString("DETLINKPHONE", ""));
    }
}
