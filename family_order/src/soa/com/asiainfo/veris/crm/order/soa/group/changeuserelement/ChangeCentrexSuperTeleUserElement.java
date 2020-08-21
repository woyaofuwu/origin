
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeCentrexSuperTeleUserElement extends ChangeUserElement
{

    private IData centrexProductParam = new DataMap();// 产品参数信息

    private IDataset mebSvcDataList = new DatasetList();

    /**
     * 生成其它台帐数据
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 登记总机号码Other信息
        infoRegDataSuperNumberOther();

        // 登记发报文信息
        infoRegDataCentrexOther();

        // 加入TRADE_ID
        if (IDataUtil.isNotEmpty(mebSvcDataList))
        {
            for (int i = 0, row = mebSvcDataList.size(); i < row; i++)
            {
                mebSvcDataList.getData(i).put("TRADE_ID", getTradeId());
            }
        }
    }

    /**
     * 写Other表,发送报文信息
     * 
     * @throws Exception
     */
    public void infoRegDataCentrexOther() throws Exception
    {
        IData centrexData = new DataMap();

        centrexData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
        centrexData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centrexData.put("RSRV_VALUE", "融合总机修改");
        centrexData.put("RSRV_STR1", reqData.getUca().getProductId());// 集团产品ID
        centrexData.put("RSRV_STR9", "6300"); // 服务id
        centrexData.put("OPER_CODE", "03"); // 操作类型
        centrexData.put("START_DATE", getAcceptTime());
        centrexData.put("END_DATE", getAcceptTime());
        centrexData.put("INST_ID", SeqMgr.getInstId());
        centrexData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        super.addTradeOther(centrexData);
    }

    /**
     * 登记总机号码Other信息
     * 
     * @throws Exception
     */
    public void infoRegDataSuperNumberOther() throws Exception
    {
        IDataset superNumberList = centrexProductParam.getDataset("SUPERNUMBER");

        if (IDataUtil.isEmpty(superNumberList))
        {
            return;
        }

        IDataset otherInfoList = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "MUTISUPERTEL", null);

        IDataset otherDataList = new DatasetList();

        for (int i = 0, row = superNumberList.size(); i < row; i++)
        {
            IData superNumberData = superNumberList.getData(i);
            String tag = superNumberData.getString("tag", "");
            String serialNumber = superNumberData.getString("EXCHANGETELE_SN", "");

            if ("0".equals(tag))
            {// 新增
                IData otherData = new DataMap();
                otherData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
                otherData.put("RSRV_VALUE_CODE", "MUTISUPERTEL");
                otherData.put("RSRV_VALUE", superNumberData.getString("EXCHANGETELE_SN", ""));// 手机号码
                otherData.put("RSRV_STR1", superNumberData.getString("E_CUST_NAME", ""));// 客户名称
                otherData.put("RSRV_STR2", superNumberData.getString("E_BRAND_CODE", ""));// 品牌
                otherData.put("RSRV_STR3", superNumberData.getString("E_EPARCHY_CODE", ""));// 归属地州
                otherData.put("RSRV_STR4", superNumberData.getString("E_USER_ID", ""));// 总机用户ID
                otherData.put("RSRV_STR5", superNumberData.getString("E_CUST_ID", ""));// 总机客户ID
                otherData.put("RSRV_STR6", superNumberData.getString("MAXWAITINGLENGTH", ""));// 总机对应的呼叫队列最大等待长度
                otherData.put("RSRV_STR7", superNumberData.getString("CALLCENTERTYPE", ""));// 话务台呼叫种类
                otherData.put("RSRV_STR8", superNumberData.getString("CALLCENTERSHOW", ""));// 话务员呼叫群内用户显示号码
                otherData.put("RSRV_STR9", superNumberData.getString("CORP_REGCODE", ""));// 话务员注册码 全数字
                otherData.put("RSRV_STR10", superNumberData.getString("CORP_DEREGCODE", ""));// 话务员注销码[全数字]
                otherData.put("INST_ID", SeqMgr.getInstId());
                otherData.put("START_DATE", getAcceptTime());// 开始时间
                otherData.put("END_DATE", SysDateMgr.getTheLastTime());// 结束时间
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                otherDataList.add(otherData);
            }
            else if ("1".equals(tag) || "2".equals(tag))
            {// 删除或修改
                if (IDataUtil.isNotEmpty(otherInfoList))
                {
                    for (int j = 0, jRow = otherInfoList.size(); j < jRow; j++)
                    {
                        IData tempOtherData = otherInfoList.getData(j);
                        if (serialNumber.equals(tempOtherData.getString("RSRV_VALUE", "")))
                        {
                            if (tag.equals("1"))
                            {// 删除
                                tempOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                tempOtherData.put("END_DATE", getAcceptTime());
                            }
                            else
                            {// 修改
                                tempOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                tempOtherData.put("RSRV_STR6", superNumberData.getString("MAXWAITINGLENGTH", ""));// 总机对应的呼叫队列最大等待长度
                                tempOtherData.put("RSRV_STR7", superNumberData.getString("CALLCENTERTYPE", ""));// 话务台呼叫种类
                                tempOtherData.put("RSRV_STR8", superNumberData.getString("CALLCENTERSHOW", ""));// 话务员呼叫群内用户显示号码
                                tempOtherData.put("RSRV_STR9", superNumberData.getString("CORP_REGCODE", ""));// 话务员注册码
                                // 全数字
                                tempOtherData.put("RSRV_STR10", superNumberData.getString("CORP_DEREGCODE", ""));// 话务员注销码
                                // 全数字
                            }
                            otherDataList.add(tempOtherData);
                        }
                    }
                }
            }
        }

        super.addTradeOther(otherDataList);
    }

    /**
     * 构建融合总机产品参数
     * 
     * @throws Exception
     */
    public void makCentrexProductParam() throws Exception
    {
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        String superNumberStr = productParam.getString("SUPERNUMBER", "[]");

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
        productParam.remove("VPN_NO");

        reqData.cd.putProductParamList(reqData.getUca().getProductId(), IDataUtil.iData2iDataset(productParam, "ATTR_CODE", "ATTR_VALUE"));
    }

    /**
     * 构建成员服务数据
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
            String tag = superNumberData.getString("tag", "");

            if (tag.equals(TRADE_MODIFY_TAG.Add.getValue()))
            {
                IData mebSvcData = new DataMap();
                mebSvcData.put("SERIAL_NUMBER", superNumberData.getString("EXCHANGETELE_SN"));// 总机号码
                mebSvcData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
                mebSvcData.put("PRODUCT_ID", reqData.getUca().getProductId());
                mebSvcData.put("MEM_ROLE_B", superNumberData.getString("ROLE_CODE_B", "2"));
                mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                mebSvcDataList.add(mebSvcData);
            }
            else if (tag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
            {
                IData mebSvcData = new DataMap();
                mebSvcData.put("USER_ID", reqData.getUca().getUserId());
                mebSvcData.put("SERIAL_NUMBER", superNumberData.getString("EXCHANGETELE_SN"));
                mebSvcData.put("MEB_MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                mebSvcDataList.add(mebSvcData);
            }
        }

        map.put("MEB_SVC_DATA", mebSvcDataList);
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        // 构建融合总机产品参数
        makCentrexProductParam();

        // 构建成员服务数据
        makMebSvcData(map);
    }
}
