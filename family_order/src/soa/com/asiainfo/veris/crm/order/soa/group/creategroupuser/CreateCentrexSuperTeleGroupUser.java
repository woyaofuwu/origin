
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;

public class CreateCentrexSuperTeleGroupUser extends CreateGroupUser
{

    private IData centrexProductParam = new DataMap();// 产品参数信息

    private IDataset mebSvcDataList = new DatasetList();

    boolean flattag = false;

    public CreateCentrexSuperTeleGroupUser()
    {

    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        String vpn_no = centrexProductParam.getString("VPN_NO", ""); // 页面传过来的vpn编码
        if (StringUtils.isNotBlank(vpn_no))
        {
            flattag = false;
        }
        else
        {
            flattag = true;
        }
        // 没有vpn_no，则取多媒体桌面电话页面参数初始值，写入attr表
        if (flattag)
        {
            IDataset paramList = reqData.cd.getProductParamList(reqData.getUca().getProductId());
            String eparchyCode = BizRoute.getRouteId();
            // 取多媒体桌面电话默认参数值
            IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO("2222", "P", "0", eparchyCode, null);
            for (int row = 0, len = dataset.size(); row < len; row++)
            {
                IData dparam = (IData) dataset.get(row);
                String attr_code = dparam.getString("ATTR_CODE");
                String attr_init_value = dparam.getString("ATTR_INIT_VALUE");

                IData attr = new DataMap();
                attr.put("ATTR_CODE", attr_code);
                attr.put("ATTR_VALUE", attr_init_value);
                paramList.add(attr);
            }
        }
    }

    /**
     * 生成其它台帐数据
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记VPN用户信息
        infoRegDataCentrexVpn();

        // 登记总机号码Other信息
        infoRegDataSuperNumberOther();

        // 登记发报文信息
        infoRegDataCentrexOther();

        // 加入TRADE_ID
        for (int i = 0, row = mebSvcDataList.size(); i < row; i++)
        {
            mebSvcDataList.getData(i).put("TRADE_ID", getTradeId());
        }
    }

    /**
     * @description 得到融合总机产品参数
     * @return
     * @throws Exception
     */
    private IData getParamData() throws Exception
    {
        String curProductId = reqData.getUca().getProductId();
        // 融合总机产品参数
        IData paramData = reqData.cd.getProductParamMap(curProductId);
        if (IDataUtil.isEmpty(paramData))
        {
            CSAppException.apperr(ParamException.CRM_PARAM_345);
        }
        return paramData;
    }

    /**
     * 插入other表, 登记发报文信息
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();

        centreData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_VALUE", "融合总机开通");
        centreData.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
        centreData.put("RSRV_STR9", "6300"); // 服务id
        centreData.put("OPER_CODE", "03"); // 操作类型
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        centreData.put("START_DATE", getAcceptTime());// 开始时间
        centreData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);
        // 如果VPN_NO没数据则发创建集团
        if (flattag)
        {
            IData centreData1 = new DataMap();

            centreData1.put("USER_ID", reqData.getUca().getUserId());
            centreData1.put("RSRV_VALUE_CODE", "CNTRX");// domain域
            centreData1.put("RSRV_VALUE", "创建集团");
            centreData1.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID

            centreData1.put("RSRV_STR9", "817"); // 服务id
            centreData1.put("OPER_CODE", "01"); // 操作类型 01 注册

            centreData1.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            centreData1.put("START_DATE", getAcceptTime());
            centreData1.put("END_DATE", SysDateMgr.getTheLastTime());
            centreData1.put("INST_ID", SeqMgr.getInstId());
            dataset.add(centreData1);
        }
        addTradeOther(dataset);
    }

    /**
     * 登记VPN用户信息
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexVpn() throws Exception
    {
        IData vpnData = super.infoRegDataVpn();

        vpnData.put("SCP_CODE", "10"); // SCP代码-固定值  无法被叫问题
        vpnData.put("VPN_SCARE_CODE", ""); // 集团范围属性-通过数据库配置获取
        vpnData.put("CALL_NET_TYPE", "0000"); // 呼叫网络类型
        vpnData.put("CALL_AREA_TYPE", "0"); // 呼叫区域类型
        vpnData.put("VPMN_TYPE", "3"); // VPN集团类型 0本地集团 1全省集团 2全国集团 3本地化全国集团
        vpnData.put("FUNC_TLAGS", "100000000000000000000000000000100000");// 集团功能集

        String vpn_no = centrexProductParam.getString("VPN_NO", ""); // VPN集团号
        if (StringUtils.isNotBlank(vpn_no))
        {
            vpnData.put("VPN_NO", vpn_no); // 集团VPN编码
        }
        else
        {
            vpnData.put("VPN_NO", reqData.getUca().getSerialNumber());
        }

        vpnData.put("VPN_USER_CODE", "2");// 集团用户属性

        super.addTradeVpn(vpnData);
    }

    /**
     * 登记总机号码信息,插入CG库Other表
     * 
     * @throws Exception
     */
    public void infoRegDataSuperNumberOther() throws Exception
    {
        IDataset superNumberList = centrexProductParam.getDataset("SUPERNUMBER");

        if (IDataUtil.isNotEmpty(superNumberList))
        {
            IDataset otherDataList = new DatasetList();
            for (int i = 0, row = superNumberList.size(); i < row; i++)
            {
                IData superNumberData = superNumberList.getData(i);
                IData otherData = new DataMap();

                otherData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
                otherData.put("RSRV_VALUE_CODE", "MUTISUPERTEL");
                otherData.put("RSRV_VALUE", superNumberData.getString("EXCHANGETELE_SN", "")); // 手机号码
                otherData.put("RSRV_STR1", superNumberData.getString("E_CUST_NAME", "")); // 客户名称
                otherData.put("RSRV_STR2", superNumberData.getString("E_BRAND_CODE", "")); // 品牌
                otherData.put("RSRV_STR3", superNumberData.getString("E_EPARCHY_CODE", "")); // 归属地州
                otherData.put("RSRV_STR4", superNumberData.getString("E_USER_ID", "")); // 归属地州
                otherData.put("RSRV_STR5", superNumberData.getString("E_CUST_ID", "")); // 归属客户
                otherData.put("RSRV_STR6", superNumberData.getString("MAXWAITINGLENGTH", "")); // 总机对应的呼叫队列最大等待长度
                otherData.put("RSRV_STR7", superNumberData.getString("CALLCENTERTYPE", "")); // 话务台呼叫种类
                otherData.put("RSRV_STR8", superNumberData.getString("CALLCENTERSHOW", "")); // 话务员呼叫群内用户显示号码
                otherData.put("RSRV_STR9", superNumberData.getString("CORP_REGCODE", "")); // 话务员注册码 全数字
                otherData.put("RSRV_STR10", superNumberData.getString("CORP_DEREGCODE", "")); // 话务员注销码 全数字
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                otherData.put("START_DATE", getAcceptTime()); // 开始时间
                otherData.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherDataList.add(otherData);
            }
            super.addTradeOther(otherDataList);
        }
    }

    /**
     * 构建融合总机参数
     * 
     * @throws Exception
     */
    public void makCentrexProductParam() throws Exception
    {
        IData productParam = getParamData();

        centrexProductParam.put("VPN_NO", productParam.getString("VPN_NO"));

        String superNumberStr = productParam.getString("SUPERNUMBER");
        if (StringUtils.isNotBlank(superNumberStr))
        {
            centrexProductParam.put("SUPERNUMBER", new DatasetList(superNumberStr));
        }
        else
        {
            centrexProductParam.put("SUPERNUMBER", new DatasetList());
        }

        productParam.remove("SUPERNUMBER");
        productParam.remove("EXCHANGETELE_SN");
        productParam.remove("IF_CENTRETYPE");
        productParam.remove("VPN_NO_Box_Text");
        productParam.remove("VPN_NO");

        reqData.cd.putProductParamList(reqData.getUca().getProductId(), IDataUtil.iData2iDataset(productParam, "ATTR_CODE", "ATTR_VALUE"));
    }

    /**
     * 构建调用成员服务的数据
     * 
     * @param map
     * @throws Exception
     */
    public void makMebSvcData(IData map) throws Exception
    {
        IDataset superNumberList = centrexProductParam.getDataset("SUPERNUMBER");

        for (int i = 0, row = superNumberList.size(); i < row; i++)
        {
            IData superNumberData = superNumberList.getData(i);

            IData mebSvcData = new DataMap();
            mebSvcData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
            mebSvcData.put("GRP_SERIAL_NUMBER", reqData.getUca().getSerialNumber());// 集团用户服务号码
            mebSvcData.put("SERIAL_NUMBER", superNumberData.getString("EXCHANGETELE_SN"));// 总机号码
            mebSvcData.put("PRODUCT_ID", reqData.getUca().getProductId());
            mebSvcData.put("MEM_ROLE_B", superNumberData.getString("ROLE_CODE_B", "2"));
            mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

            mebSvcDataList.add(mebSvcData);
        }

        map.put("MEB_SVC_DATA", mebSvcDataList);
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 构建融合总机参数
        makCentrexProductParam();

        // 构建总机号码数据,为调用成员服务准备数据
        makMebSvcData(map);
    }
}
