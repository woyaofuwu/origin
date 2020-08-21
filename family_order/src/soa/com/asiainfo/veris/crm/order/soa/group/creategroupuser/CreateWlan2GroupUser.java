
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateWlan2GroupUser extends CreateGroupUser
{
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记other表，服务开通侧用
        infoRegDataOther();
    }

    /**
     * 其它台帐处理
     */
    public void infoRegDataOther() throws Exception
    {
        String wlanInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId()).getString("WLANINFOS");

        IDataset wlanList = new DatasetList(wlanInfos);
        if (IDataUtil.isEmpty(wlanList))
        {
            return;
        }
        IDataset otherDataset = new DatasetList();
        if (wlanList != null && wlanList.size() > 0)
        {
            for (int i = 0; i < wlanList.size(); i++)
            {
                IData data = new DataMap();
                IData productData = wlanList.getData(i);
                data.put("RSRV_VALUE_CODE", "GRP_WLAN");
                data.put("RSRV_VALUE", productData.getString("pam_GRP_WLAN_CODE"));
                data.put("RSRV_STR1", productData.getString("pam_GRP_WLAN", ""));
                data.put("RSRV_STR2", productData.getString("pam_NET_LINE", ""));
                data.put("RSRV_STR3", productData.getString("pam_PRICE", ""));
                data.put("RSRV_STR4", productData.getString("pam_DIS_DATA", ""));
                data.put("RSRV_STR8", productData.getString("pam_COMPANY_NAME_CODE", ""));
                data.put("RSRV_STR9", productData.getString("pam_COMPANY_NAME", ""));
                data.put("RSRV_STR10", productData.getString("pam_REMARK", ""));
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                data.put("INST_ID", SeqMgr.getInstId());
                data.put("OPER_CODE", "06");

                otherDataset.add(data);
            }
            super.addTradeOther(otherDataset);
        }
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        tradeData.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        tradeData.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        tradeData.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));
    }

    @Override
    protected void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        String curProductId = reqData.getUca().getProductId();
        // 获取参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);

        map.put("RSRV_STR8", paramData.getString("DETMANAGERPHONE", ""));
        map.put("RSRV_STR7", paramData.getString("DETMANAGERINFO", ""));
        map.put("RSRV_STR9", paramData.getString("DETADDRESS", ""));
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception
    {
        super.setTradeSvc(map);
        map.put("OPER_CODE", "06"); // 供服开使用
    }
}
