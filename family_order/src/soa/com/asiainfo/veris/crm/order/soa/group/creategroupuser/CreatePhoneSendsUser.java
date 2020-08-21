
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreatePhoneSendsUser extends CreateGroupUser
{
    private IData sendParam = new DataMap();// 短信参数

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        // 构建短信参数数据
        makSendParam();
    }

    /**
     * 构建短信参数数据
     * 
     * @throws Exception
     */
    public void makSendParam() throws Exception
    {

        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        if (IDataUtil.isNotEmpty(productParam))
        {
            sendParam = productParam;
            reqData.cd.putProductParamList(reqData.getUca().getProductId(), new DatasetList());
        }

    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void regTrade() throws Exception
    {
        super.regTrade();

        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR7", sendParam.getString("DETMANAGERINFO", "")); // 管理员信息
        tradeData.put("RSRV_STR8", sendParam.getString("DETMANAGERPHONE", "")); // 管理员电话
        tradeData.put("RSRV_STR9", sendParam.getString("DETLINKPHONE", "")); // 联系地址
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR7", sendParam.getString("DETMANAGERINFO", "")); // 管理员信息
        map.put("RSRV_STR8", sendParam.getString("DETMANAGERPHONE", "")); // 管理员电话
        map.put("RSRV_STR9", sendParam.getString("DETLINKPHONE", "")); // 联系地址
    }
}
