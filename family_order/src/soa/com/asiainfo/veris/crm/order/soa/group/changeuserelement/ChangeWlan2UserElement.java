
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;

public class ChangeWlan2UserElement extends ChangeUserElement
{
    public void actTradeSub() throws Exception
    {
        // 1- 继承基类处理
        super.actTradeSub();

        // 2- 登记other表，服务开通侧用
        infoRegDataOther();

        infoRegDataSvc();

    }

    /**
     * 服务表
     * 
     * @throws Exception
     */
    private void infoRegDataSvc() throws Exception
    {
        // 新增一条106801 虚拟台账用于服务查参
        IData data = new DataMap();
        String userId = reqData.getUca().getUserId();
        String serviceId = "106801";

        IDataset svcDatas = SvcInfoQry.getSvcByUserIdandServiceId(userId, serviceId);
        if (IDataUtil.isNotEmpty(svcDatas))
        {
            for (int i = 0; i < svcDatas.size(); i++)
            {
                IData svcData = svcDatas.getData(i);
                if ("106801".equals(svcData.getString("SERVICE_ID")))
                {
                    data = new DataMap();
                    data.put("USER_ID", svcData.getString("USER_ID", ""));// 用户标识
                    data.put("USER_ID_A", svcData.getString("USER_ID_A", "-1"));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
                    data.put("PRODUCT_ID", svcData.getString("PRODUCT_ID"));// 产品标识
                    data.put("PACKAGE_ID", svcData.getString("PACKAGE_ID"));// 包标识
                    data.put("SERVICE_ID", "106801");// 服务标识

                    data.put("MAIN_TAG", svcData.getString("MAIN_TAG"));// 主体服务标志：0-否，1-是
                    data.put("INST_ID", svcData.getString("INST_ID"));// 实例标识
                    data.put("CAMPN_ID", "");// 活动标识
                    data.put("START_DATE", svcData.getString("START_DATE"));// 开始时间
                    data.put("END_DATE", svcData.getString("END_DATE"));// 结束时间
                    data.put("OPER_CODE", "08"); // 供服开使用

                    // 状态属性：0-增加，1-删除，2-变更
                    data.put("MODIFY_TAG", "2");
                    data.put("REMARK", "集团WLAN2虚拟服务");// 备注

                    data.put("RSRV_NUM1", "");// 预留数值1
                    data.put("RSRV_NUM2", "");// 预留数值2
                    data.put("RSRV_NUM3", "");// 预留数值3
                    data.put("RSRV_NUM4", "");// 预留数值4
                    data.put("RSRV_NUM5", "");// 预留数值5

                    data.put("RSRV_STR1", "");// 预留字段1
                    data.put("RSRV_STR2", "");// 预留字段2
                    data.put("RSRV_STR3", "");// 预留字段3
                    data.put("RSRV_STR4", "");// 预留字段4
                    data.put("RSRV_STR5", "");// 预留字段5
                    data.put("RSRV_STR6", "");// 预留字段6
                    data.put("RSRV_STR7", "");// 预留字段7
                    data.put("RSRV_STR8", "");// 预留字段8
                    data.put("RSRV_STR9", "");// 预留字段9
                    data.put("RSRV_STR10", "");// 预留字段10

                    data.put("RSRV_DATE1", "");// 预留日期1
                    data.put("RSRV_DATE2", "");// 预留日期2
                    data.put("RSRV_DATE3", "");// 预留日期3
                    data.put("RSRV_TAG1", "");// 预留标志1
                    data.put("RSRV_TAG2", "");// 预留标志2
                    data.put("RSRV_TAG3", "");// 预留标志3
                    addTradeSvc(data);
                }
            }
        }

    }

    /**
     * 其它台帐处理
     */
    public void infoRegDataOther() throws Exception
    {
        // 根据产品编号获取产品参数
        String wlanInfos = reqData.cd.getProductParamMap(reqData.getUca().getProductId()).getString("WLANINFOS");

        IDataset wlanList = new DatasetList(wlanInfos);
        if (IDataUtil.isEmpty(wlanList))
        {
            return;
        }
        IDataset otherInfoList = UserOtherInfoQry.getUserOtherByUserRsrvValueCode(reqData.getUca().getUserId(), "GRP_WLAN", null);

        IDataset otherDataset = new DatasetList();
        for (int i = 0; i < wlanList.size(); i++)
        {
            IData wlanData = wlanList.getData(i);
            String tag = wlanData.getString("tag", "");
            String instId = wlanData.getString("INSTID");

            if ("0".equals(tag))
            {// 新增
                IData otherData = new DataMap();
                otherData.put("USER_ID", reqData.getUca().getUserId());// 集团用户ID
                otherData.put("RSRV_VALUE_CODE", "GRP_WLAN");
                otherData.put("RSRV_VALUE", wlanData.getString("pam_GRP_WLAN_CODE", ""));
                otherData.put("RSRV_STR1", wlanData.getString("pam_GRP_WLAN", ""));
                otherData.put("RSRV_STR2", wlanData.getString("pam_NET_LINE", ""));
                otherData.put("RSRV_STR3", wlanData.getString("pam_PRICE", ""));
                otherData.put("RSRV_STR4", wlanData.getString("pam_DIS_DATA", ""));
                otherData.put("RSRV_STR8", wlanData.getString("pam_COMPANY_NAME_CODE", ""));
                otherData.put("RSRV_STR9", wlanData.getString("pam_COMPANY_NAME", ""));
                otherData.put("RSRV_STR10", wlanData.getString("pam_REMARK", ""));
                otherData.put("START_DATE", getAcceptTime());
                otherData.put("END_DATE", SysDateMgr.getTheLastTime());
                otherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                otherData.put("INST_ID", SeqMgr.getInstId());

                otherDataset.add(otherData);
            }
            else if ("1".equals(tag) || "2".equals(tag))
            {// 删除或修改
                if (IDataUtil.isNotEmpty(otherInfoList))
                {
                    for (int j = 0, jRow = otherInfoList.size(); j < jRow; j++)
                    {
                        IData tempOtherData = otherInfoList.getData(j);
                        if (instId.equals(tempOtherData.getString("INST_ID", "")))
                        {
                            if (tag.equals("1"))
                            {// 删除
                                tempOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                tempOtherData.put("END_DATE", getAcceptTime());
                                tempOtherData.put("OPER_CODE", "07");
                            }
                            else
                            {// 修改
                                tempOtherData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                tempOtherData.put("RSRV_VALUE", wlanData.getString("pam_GRP_WLAN_CODE", ""));
                                tempOtherData.put("RSRV_STR1", wlanData.getString("pam_GRP_WLAN", ""));
                                tempOtherData.put("RSRV_STR2", wlanData.getString("pam_NET_LINE", ""));
                                tempOtherData.put("RSRV_STR3", wlanData.getString("pam_PRICE", ""));
                                tempOtherData.put("RSRV_STR4", wlanData.getString("pam_DIS_DATA", ""));
                                tempOtherData.put("RSRV_STR8", wlanData.getString("pam_COMPANY_NAME_CODE", ""));
                                tempOtherData.put("RSRV_STR9", wlanData.getString("pam_COMPANY_NAME", ""));
                                tempOtherData.put("RSRV_STR10", wlanData.getString("pam_REMARK", ""));
                                tempOtherData.put("OPER_CODE", "08");
                            }
                            otherDataset.add(tempOtherData);
                        }
                    }
                }
            }
        }
        super.addTradeOther(otherDataset);
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
}
