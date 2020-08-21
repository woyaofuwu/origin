
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeVispUserElement extends ChangeUserElement
{

    public ChangeVispUserElement()
    {

    }

    protected void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

    }

    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegVispDataOther();
    }

    public IData getTradeUserExtendData() throws Exception
    {
        IData userExtenData = super.getTradeUserExtendData();

        // 存产品产品信息到user表
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);

        if (IDataUtil.isNotEmpty(productParams))
        {
            userExtenData.put("RSRV_STR7", productParams.getString("NOTIN_DETMANAGER_INFO"));
            userExtenData.put("RSRV_STR8", productParams.getString("NOTIN_DETMANAGER_PHONE"));
            userExtenData.put("RSRV_STR9", productParams.getString("NOTIN_DETADDRESS"));
            userExtenData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }

        return userExtenData;
    }

    public void infoRegVispDataOther() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IDataset dataset = new DatasetList();

        IDataset attrInternet = new DatasetList(param.getString("NOTIN_AttrInternet", "[]"));

        IData inparam = new DataMap();
        inparam.put("USER_ID", reqData.getUca().getUser().getUserId());
        inparam.put("RSRV_VALUE_CODE", "N001");
        IDataset userOther = TradeOtherInfoQry.queryUserOtherInfoByUserId(inparam);

        for (int j = 0; j < attrInternet.size(); j++)
        {
            IData internet = attrInternet.getData(j);
            String numberCode = internet.getString("pam_NOTIN_LINE_NUMBER_CODE");
            String flag = internet.getString("tag", "");
            for (int i = 0; i < userOther.size(); i++)
            {
                IData vispUser = userOther.getData(i);
                String lineNumberCode = vispUser.getString("RSRV_VALUE");
                if (numberCode.equals(lineNumberCode))
                {
                    if ("1".equals(flag))
                    {
                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                        vispUser.put("UPDATE_TIME", getAcceptTime());
                        vispUser.put("END_DATE", getAcceptTime());

                        dataset.add(vispUser);
                    }
                    else if ("2".equals(flag))
                    {
                        vispUser.put("UPDATE_TIME", getAcceptTime());

                        vispUser.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")));
                        // 专线
                        vispUser.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                        // 专线带宽
                        vispUser.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                        // 专线价格
                        vispUser.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                        // 专线实例号
                        vispUser.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));

                        vispUser.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                        dataset.add(vispUser);

                    }
                    else if ("".equals(flag))
                    {

                    }
                }
            }

            if ("0".equals(flag))
            {
                IData vispUsers = new DataMap();

                vispUsers.put("USER_ID", reqData.getUca().getUserId());
                vispUsers.put("RSRV_VALUE_CODE", "N001");

                vispUsers.put("RSRV_VALUE", Integer.valueOf(internet.getString("pam_NOTIN_LINE_NUMBER_CODE")));
                // 专线
                vispUsers.put("RSRV_STR1", internet.getString("pam_NOTIN_LINE_NUMBER"));
                // 专线带宽
                vispUsers.put("RSRV_STR2", internet.getString("pam_NOTIN_LINE_BROADBAND"));
                // 专线价格
                vispUsers.put("RSRV_STR3", internet.getString("pam_NOTIN_LINE_PRICE"));
                // 专线实例号
                vispUsers.put("RSRV_STR9", internet.getString("pam_NOTIN_LINE_INSTANCENUMBER"));

                vispUsers.put("START_DATE", getAcceptTime());
                vispUsers.put("END_DATE", SysDateMgr.getTheLastTime());

                vispUsers.put("PROCESS_TAG", ""); // 处理标志map.getString("TRADE_TAG", "")
                vispUsers.put("INST_ID", SeqMgr.getInstId());
                vispUsers.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(vispUsers);
            }
        }

        addTradeOther(dataset);
    }

    @Override
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
