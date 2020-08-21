
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateVispGroupUser extends CreateGroupUser
{

    public CreateVispGroupUser()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {

        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        regOtherInfoTrade();

    }

    protected void actTradeUser() throws Exception
    {
        UserTradeData userTradeData = reqData.getUca().getUser();
        // 用户
        if (userTradeData != null)
        {
            // 存产品产品信息到user表
            String product_id = reqData.getUca().getProductId();
            IData productParams = reqData.cd.getProductParamMap(product_id);

            if (IDataUtil.isNotEmpty(productParams))
            {
                userTradeData.setRsrvStr7(productParams.getString("NOTIN_DETMANAGER_INFO"));
                userTradeData.setRsrvStr8(productParams.getString("NOTIN_DETMANAGER_PHONE"));
                userTradeData.setRsrvStr9(productParams.getString("NOTIN_DETADDRESS"));
            }
        }
        super.actTradeUser();
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

    }

    public void regOtherInfoTrade() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();
        IData data = null;
        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        if (null != attrInternet && attrInternet.size() > 0)
        {
            for (int i = 0; i < attrInternet.size(); i++)
            {
                data = new DataMap();
                IData internet = attrInternet.getData(i);

                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("RSRV_VALUE_CODE", "N001");
                data.put("STATE", "ADD");
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());

                data.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")));
                // 专线
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                // 专线带宽
                data.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                // 专线价格
                data.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                // 专线实例号
                data.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));

                dataset.add(data);
            }

            addTradeOther(dataset);
        }
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        IData paramData = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        data.put("RSRV_STR7", paramData.getString("NOTIN_DETMANAGER_INFO", ""));
        data.put("RSRV_STR8", paramData.getString("NOTIN_DETMANAGER_PHONE", ""));
        data.put("RSRV_STR9", paramData.getString("NOTIN_DETADDRESS", ""));

    }

}
