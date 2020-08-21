
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyYhtGroupMember extends DestroyGroupMember
{
    private boolean flag = true;

    private String zTag = "0"; // 被叫一号通振动方式

    private boolean delFlag = true;
    
    public DestroyYhtGroupMember()
    {

    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        flag = RouteInfoQry.isChinaMobile(reqData.getUca().getSerialNumber());
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        infoRegYhtData();
        infoRegDataImpu(); // 不用删除impu信息
        imsRegDataOther();
        infoRegDataAttr();
    }

    /**
     * 处理台帐Other子表的数据,用于发指令
     * 
     * @param Datas
     * @author liaoyi
     * @throws Exception
     */
    public void imsRegDataOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData dataOtherCNTRXMD = new DataMap();
        IData dataOtherCNTRX = new DataMap();
        if (flag)
        {
            dataOtherCNTRXMD.put("USER_ID", reqData.getUca().getUserId());
            dataOtherCNTRXMD.put("RSRV_VALUE_CODE", "CNTRX");
            dataOtherCNTRXMD.put("RSRV_VALUE", "融合一号通成员锚定服务");
            dataOtherCNTRXMD.put("RSRV_STR9", "8611"); // 锚定，service_id
            dataOtherCNTRXMD.put("OPER_CODE", "1");
            dataOtherCNTRXMD.put("RSRV_STR11", "1");
            dataOtherCNTRXMD.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            dataOtherCNTRXMD.put("START_DATE", getAcceptTime());
            dataOtherCNTRXMD.put("END_DATE", getAcceptTime());
            dataOtherCNTRXMD.put("INST_ID", SeqMgr.getInstId());
            dataset.add(dataOtherCNTRXMD);
        }
        if (delFlag)
        {
            IData hss = new DataMap();
            hss.put("USER_ID", reqData.getUca().getUserId());
            hss.put("RSRV_VALUE_CODE", "HSS");// domain域
            hss.put("RSRV_VALUE", "融合一号通");
            hss.put("RSRV_STR1", reqData.getUca().getProductId());// 产品ID
            hss.put("RSRV_STR9", "8172"); // 服务id
            hss.put("OPER_CODE", "02");// 用于服务开通操作码
            hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            hss.put("START_DATE", getAcceptTime());
            hss.put("END_DATE", getAcceptTime()); // 立即截止
            hss.put("INST_ID", SeqMgr.getInstId());
            dataset.add(hss);

        }
        String userType = "0";// =0 (SIP终端): IMS SIP-UE 用户

        if (flag)
        {
            userType = "4";// =4 : 传统移动用户
        }
        dataOtherCNTRX.put("INST_ID", SeqMgr.getInstId());
        dataOtherCNTRX.put("RSRV_STR1", userType);// 配置主号码用户类型
        dataOtherCNTRX.put("USER_ID", reqData.getUca().getUserId());
        dataOtherCNTRX.put("RSRV_VALUE_CODE", "CNTRX");
        dataOtherCNTRX.put("RSRV_VALUE", "融合一号通成员CNTRX服务");
        dataOtherCNTRX.put("RSRV_STR2", zTag);// 被叫一号通振铃方式
        dataOtherCNTRX.put("RSRV_STR9", "8174");// 用于服务开通，service_id
        dataOtherCNTRX.put("OPER_CODE", "32");
        dataOtherCNTRX.put("RSRV_STR11", "32");// 用于服务开通，注销用
        dataOtherCNTRX.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        dataOtherCNTRX.put("START_DATE", getAcceptTime());
        dataOtherCNTRX.put("END_DATE", getAcceptTime());
        dataset.add(dataOtherCNTRX);

        IData dataOtherCNTRX1 = new DataMap();
        dataOtherCNTRX1.putAll(dataOtherCNTRX);
        dataOtherCNTRX1.put("OPER_CODE", "03");
        dataOtherCNTRX1.put("RSRV_STR11", "03"); // 操作类型
        dataOtherCNTRX1.put("INST_ID", SeqMgr.getInstId());
        dataset.add(dataOtherCNTRX1);

        addTradeOther(dataset);
    }


    /**
     * 将手机用户登记在IMPU用户信息
     * 
     * @author tengg
     * @2010-8-16
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception
    {
        // 为手机用户时要发删除hss,enum，固定用户则不用
        if (delFlag)//如果是删除标志说明是定了普通v网的手机号码
        {
            // 查询是否存在IMPU信息；
            String eparchyCode = reqData.getUca().getUser().getEparchyCode();
            String rsrv_str1 = "1";

            IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(reqData.getUca().getUserId(), rsrv_str1, eparchyCode);
            if (IDataUtil.isNotEmpty(impuInfo))
            {
                IDataset dataset = new DatasetList();
                for (int i = 0; i < impuInfo.size(); i++)
                {
                    IData impuData = (IData) impuInfo.get(i);
                    impuData.put("RSRV_STR5", "0");// 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
                    impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                    dataset.add(impuData);
                }
                addTradeImpu(dataset);
            }
        }
    }
    
    /*
     * 把振动方式保存在ATTR表
     */
    public void infoRegDataAttr() throws Exception
    {
        IDataset userattrset = UserAttrInfoQry.getUserAttrByUserInstType(reqData.getUca().getUserId(), "CNTRX_MEMB_ONE_RTYPE");
        if (IDataUtil.isNotEmpty(userattrset))
        {
            IDataset dataset = new DatasetList();
            IData data = new DataMap();
            data = userattrset.getData(0);
            data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            data.put("END_DATE", getAcceptTime());
            dataset.add(data);
            addTradeAttr(dataset);
        }
    }

    /**
     * @Description:特殊服务处理
     */
    public void infoRegDataSvc() throws Exception
    {

        // 当为固定电话时，多媒体已增加这两服务，也不用发HSS,ENUM,可能会捞出这两个服务，不删除这两服务
        if (!flag)
        {
            IDataset svcs = reqData.cd.getSvc();
            for (int i = 0; i < svcs.size(); i++)
            {
                IData svc = svcs.getData(i);
                if ("8172".equals(svc.getString("ELEMENT_ID")) || "8173".equals(svc.getString("ELEMENT_ID")))
                {
                    svcs.remove(i);
                }
            }
        }
    }

    private void infoRegYhtData() throws Exception
    {
        IDataset dsUu = RelaUUInfoQry.getAllRelaUUInfoByUserIda(reqData.getUca().getUserId(), "S6");
        if (IDataUtil.isNotEmpty(dsUu))
        {
            for (int i = 0; i < dsUu.size(); i++)
            {
                IData rela = dsUu.getData(i);

                rela.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                rela.put("END_DATE", getAcceptTime());

                this.addTradeRelation(rela);
            }
        }

        // 被叫一号通
        IDataset dsUu2 = RelaUUInfoQry.getAllRelaUUInfoByUserIda(reqData.getUca().getUserId(), "S7");
        if (IDataUtil.isNotEmpty(dsUu2))
        {
            for (int i = 0; i < dsUu2.size(); i++)
            {
                IData data = dsUu2.getData(i);

                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                data.put("END_DATE", getAcceptTime());

                this.addTradeRelation(data);
            }
        }
    }

    /**
     * @description 处理主台账表数据
     * @author yish
     * @date 2013-10-14
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();
        String sType = getServType();
        data.put("RSRV_STR10", sType);
    }

    private String getServType() throws Exception
    {
        String stype = "0";
        int num = 0;

        String userIdA = reqData.getUca().getUserId(); // 成员用户id
        IDataset dsUu = RelaUUInfoQry.getAllRelaUUInfoByUserIda(userIdA, "S6");
        if (IDataUtil.isNotEmpty(dsUu))
        {
            stype = "1";
            num++;
        }

        // 被叫一号通
        IDataset dsUu2 = RelaUUInfoQry.getAllRelaUUInfoByUserIda(userIdA, "S7");
        if (IDataUtil.isNotEmpty(dsUu2))
        {
            stype = "0";
            num++;
            // 获取振铃方式
            zTag = dsUu2.getData(0).getString("RSRV_TAG1", "");
        }
        if (num == 2)
        {
            stype = "2";
        }
        return stype;
    }
    
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户ID
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id

        delFlag = GroupImsUtil.getDelMebFlag(cust_id, user_id_b); // 判断成员是否还有订购其他ims产品，如果没有则可发删除成员报文，返回true

    }
}
