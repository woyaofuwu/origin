
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateQqzxGroupUser extends CreateGroupUser
{

    private IDataset dataset = null;

    public CreateQqzxGroupUser()
    {
        dataset = new DatasetList();
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
        setRegWideData();
        setRegFixedData();
        setRegSerialData();
        setRegPathData();

        addTradeOther(dataset);

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
                // 铁通专线网别13
                String netTypeCode = UProductInfoQry.getNetTypeCodeByProductId(product_id);
                if (StringUtils.isBlank(netTypeCode))
                {
                    netTypeCode = "00";
                }
                userTradeData.setNetTypeCode(netTypeCode);
                userTradeData.setRsrvStr1(productParams.getString("NOTIN_USER_STATE"));
                userTradeData.setRsrvStr2(reqData.getUca().getCustGroup().getGroupId());

                //
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

    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();
        String product_id = reqData.getUca().getProductId();
        String netTypeCode = UProductInfoQry.getNetTypeCodeByProductId(product_id);
        if (StringUtils.isBlank(netTypeCode))
        {
            netTypeCode = "00";
        }
        data.put("NET_TYPE_CODE", netTypeCode);
        data.put("RSRV_STR1", reqData.getUca().getUser().getUserStateCodeset());
        data.put("RSRV_STR2", reqData.getUca().getCustGroup().getGroupId());

    }

    // 捆绑的固话处理
    private void setRegFixedData() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IData data = null;
        IDataset fixedData = new DatasetList(param.getString("NOTIN_FixedData", "[]"));

        if (null != fixedData && fixedData.size() > 0)
        {
            for (int i = 0; i < fixedData.size(); i++)
            {
                data = new DataMap();
                IData internet = fixedData.getData(i);

                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("RSRV_VALUE_CODE", "BINDFIXED");
                // 固定话号
                data.put("RSRV_VALUE", internet.getString("pam_NOTIN_FIXED_PHONE"));
                // 金额
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_FIXED_MONEY"));

                dataset.add(data);
            }
        }

    }

    /**
     * 专线路径信息
     * 
     * @throws Exception
     */
    private void setRegPathData() throws Exception
    {
        IData data = null;
        String product_id = reqData.getUca().getProductId();
        IData productParams = reqData.cd.getProductParamMap(product_id);

        String path = productParams.getString("NOTIN_PATH");
        String address = productParams.getString("NOTIN_INSTALL_ADDRESS");

        if (StringUtils.isNotBlank(path))
        {
            data = new DataMap();
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("START_DATE", getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("RSRV_VALUE_CODE", "PATH");
            data.put("RSRV_VALUE", "0");
            data.put("RSRV_STR10", path);
            dataset.add(data);
        }

        if (StringUtils.isNotBlank(address))
        {
            data = new DataMap();
            data.put("USER_ID", reqData.getUca().getUserId());
            data.put("START_DATE", getAcceptTime());
            data.put("END_DATE", SysDateMgr.getTheLastTime());
            data.put("RSRV_VALUE_CODE", "ADDRESS");
            data.put("RSRV_VALUE", "0");
            data.put("RSRV_STR10", address);
            dataset.add(data);
        }

    }

    // 捆绑的手机信息
    private void setRegSerialData() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IData data = null;
        IDataset serialData = new DatasetList(param.getString("NOTIN_SerialData", "[]"));

        if (null != serialData && serialData.size() > 0)
        {
            for (int i = 0; i < serialData.size(); i++)
            {
                data = new DataMap();

                IData internet = serialData.getData(i);
                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("RSRV_VALUE_CODE", "BINDSERIAL");
                // 宽带账号
                data.put("RSRV_VALUE", internet.getString("pam_NOTIN_SERIAL_PHONE"));
                // 宽带时长
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_SERIAL_MONEY"));
                dataset.add(data);
            }
        }

    }

    // 捆绑宽带处理
    private void setRegWideData() throws Exception
    {
        IData param = reqData.cd.getProductParamMap(reqData.getUca().getProductId());
        IData data = null;
        IDataset wideData = new DatasetList(param.getString("NOTIN_WideData", "[]"));

        if (null != wideData && wideData.size() > 0)
        {
            for (int i = 0; i < wideData.size(); i++)
            {
                data = new DataMap();
                IData internet = wideData.getData(i);

                data.put("USER_ID", reqData.getUca().getUserId());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("RSRV_VALUE_CODE", "BINDWIDE");
                // 宽带账号
                data.put("RSRV_VALUE", internet.getString("pam_NOTIN_WIDE_ACCT_ID"));
                // 宽带时长
                data.put("RSRV_STR1", internet.getString("pam_NOTIN_WIDE_MONTH"));
                // 带宽
                data.put("RSRV_STR2", internet.getString("pam_NOTIN_WIDE_NET_LINE"));

                dataset.add(data);
            }
        }

    }

}
