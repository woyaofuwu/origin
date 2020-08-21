
package com.asiainfo.veris.crm.order.soa.group.destroygroupmember;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserImpuInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupImsUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.destroygroupmember.DestroyGroupMember;

public class DestroyCentrexVpnGroupMember extends DestroyGroupMember
{
    protected String power = "";

    private boolean delFlag = true;

    private String userType = "0";

    private String netTypeCode = "";

    public DestroyCentrexVpnGroupMember()
    {

    }

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理成员VPN表
        infoRegDataVpn();

        infoRegDataCentreOther();

        infoRegDataImpu();

    }

    /**
     * Centrex 登记平台other表 判读
     * 
     * @throws Exception
     */
    public void infoRegDataCentreOther() throws Exception
    {
        String userId = reqData.getUca().getUserId();
        String productId = reqData.getUca().getProductId();

        IDataset dataset = new DatasetList();

        if("05".equals(netTypeCode)) //手机号码加融合V网不发数指
        {
        	if (delFlag)
            {
                // 发CNTRX指令
                IData delData = new DataMap();
                delData.put("USER_ID", userId);
                delData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
                delData.put("RSRV_VALUE", "融合V网");
                delData.put("RSRV_STR1", productId);// 产品ID
                delData.put("RSRV_STR9", "8171"); // 服务id
                if ("2".equals(power))
                {
                    delData.put("OPER_CODE", "27"); // 操作类型:注销管理员
                }
                else
                {
                    delData.put("OPER_CODE", "02"); // 操作类型：注销成员
                }
                delData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                delData.put("START_DATE", getAcceptTime());
                delData.put("END_DATE", getAcceptTime()); // 立即截止
                delData.put("INST_ID", SeqMgr.getInstId());
                dataset.add(delData);

                IData hss = new DataMap();
                hss.put("USER_ID", userId);
                hss.put("RSRV_VALUE_CODE", "HSS");// domain域
                hss.put("RSRV_VALUE", "融合V网");
                hss.put("RSRV_STR1", productId);// 产品ID
                hss.put("RSRV_STR9", "8172"); // 服务id
                hss.put("OPER_CODE", "02");// 用于服务开通操作码
                hss.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                hss.put("START_DATE", getAcceptTime());
                hss.put("END_DATE", getAcceptTime()); // 立即截止
                hss.put("INST_ID", SeqMgr.getInstId());
                dataset.add(hss);

                /*
                IData enumData = new DataMap();
                enumData.put("USER_ID", userId);
                enumData.put("RSRV_VALUE_CODE", "ENUM");// domain域
                enumData.put("RSRV_VALUE", "融合V网");
                enumData.put("RSRV_STR1", productId);// 产品ID
                enumData.put("RSRV_STR9", "8173"); // 服务id
                enumData.put("OPER_CODE", "02"); // 操作类型 02 删除
                enumData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                enumData.put("START_DATE", getAcceptTime());
                enumData.put("END_DATE", getAcceptTime()); // 立即截止
                enumData.put("INST_ID", SeqMgr.getInstId());
                dataset.add(enumData);
                */
                
            }
            else
            {
                // 发CNTRX指令,成员信息变更ServiceType由0为1：短号可用
                IData centreData2 = new DataMap();
                centreData2.put("USER_ID", userId);
                centreData2.put("RSRV_VALUE_CODE", "CNTRX");// domain域
                centreData2.put("RSRV_VALUE", "多媒体桌面电话");
                centreData2.put("RSRV_STR1", productId);// 产品ID
                centreData2.put("RSRV_STR9", "8171"); // 服务id
                if ("2".equals(power))
                {
                    centreData2.put("OPER_CODE", "28"); // 操作类型：修改管理员
                }
                else
                {
                    centreData2.put("OPER_CODE", "08"); // 操作类型：修改成员
                }
                centreData2.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                centreData2.put("START_DATE", getAcceptTime());
                centreData2.put("END_DATE", SysDateMgr.getTheLastTime());
                centreData2.put("INST_ID", SeqMgr.getInstId());
                dataset.add(centreData2);
            }
        }
        addTradeOther(dataset);
    }

    /**
     * 更新IMPU用户信息
     * 
     * @author luoyong
     * @2012-3-26
     * @throws Exception
     */
    public void infoRegDataImpu() throws Exception
    {
        if (delFlag) // 删除成员
        {
            // 查询是否存在IMPU信息；
            String eparchyCode = reqData.getUca().getUser().getEparchyCode();
            String userId = reqData.getUca().getUserId();

            String utype = "0";
            if ("4".equals(userType))
            {
                utype = "1";
            }

            IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfoByUserType(userId, utype, eparchyCode);
            if (IDataUtil.isNotEmpty(impuInfo))
            {
                IDataset dataset = new DatasetList();
                for (int i = 0, len = impuInfo.size(); i < len; i++)
                {
                    IData impuData = (IData) impuInfo.get(i);
                    String rsrvstr1 = impuData.getString("RSRV_STR1", "");
                    if ("1".equals(rsrvstr1))
                    {
                        impuData.put("RSRV_STR5", "0"); // rsrv_str5 用于信控，标识为1是发了HSS,ENUM信息，为0则是发了后又取消
                        impuData.put("RSRV_STR4", ""); // rsrv_str4 角色|短号
                        impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        dataset.add(impuData);
                    }
                    else
                    {
                        impuData.put("RSRV_STR4", ""); // rsrv_str4 角色|短号
                        impuData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        dataset.add(impuData);
                    }
                }
                addTradeImpu(dataset);
            }
        }
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        String cust_id = reqData.getGrpUca().getCustId(); // 集团客户ID
        String user_id_b = reqData.getUca().getUserId(); // 成员用户id
        String eparchy_code = reqData.getUca().getUser().getEparchyCode(); // 成员地州

        netTypeCode = reqData.getUca().getUser().getNetTypeCode();

        delFlag = GroupImsUtil.getDelMebFlag(cust_id, user_id_b); // 判断成员是否还有订购其他ims产品，如果没有则可发删除成员报文，返回true

        // 查impu信息
        IDataset impuInfo = UserImpuInfoQry.queryUserImpuInfo(user_id_b, eparchy_code);
        if (IDataUtil.isNotEmpty(impuInfo))
        {
            IData datatmp = impuInfo.getData(0);
            userType = datatmp.getString("RSRV_STR1", ""); // 用户类型
        }
        if (!"05".equals(netTypeCode))
        {
            userType = "4";// =4 : 传统移动用户
        }
        if (delFlag)
        {
            String tmp = GroupImsUtil.getImpuStr4(user_id_b, userType, 0);
            if (StringUtils.isNotBlank(tmp))
            {
                power = tmp; // 已订购ims业务的取impu表, RSRV_STR4 中的成员角色值
            }
        }
        
        
        //融合V网成员注销，特殊处理优惠截止时间
        makReqDataElementForMeb();
        
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

        IData data = bizData.getTrade();

        data.put("RSRV_STR2", "5"); // CENTREX 参数CNTRX_MEMB_POWER取值
        // 判断当前号码是否是移动的号码
        if (!"05".equals(netTypeCode))
        {
            data.put("RSRV_STR3", "0"); // 与callpf 约定的HLR发送标志
        }

    }

    /**
     * 覆写父类方法
     */
    public void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);

        String serviceType = map.getString("ATTR_CODE"); // 短号有效标志，0-无效；1-融合V网有效
        if ("ServiceType".equals(serviceType))
        {
            map.put("ATTR_VALUE", "0");
        }
    }

    /**
     * 覆写父类方法
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR2", "5"); // CENTREX 参数CNTRX_MEMB_POWER取值
        // 判断当前号码是否是移动的号码
        if (!"05".equals(netTypeCode))
        {
            map.put("RSRV_STR3", "0"); // 与callpf 约定的HLR发送标志
        }
    }
    
    
    private void makReqDataElementForMeb() throws Exception{
        IDataset userDiscntList = reqData.cd.getDiscnt();
        
        if (IDataUtil.isNotEmpty(userDiscntList)){
            for (int i = 0, iSize = userDiscntList.size(); i < iSize; i++)
            {
                IData userDiscntData = userDiscntList.getData(i); // 取每个元素
                if ("D".equals(userDiscntData.getString("ELEMENT_TYPE_CODE")) &&
                        "33001972".equals(userDiscntData.getString("PACKAGE_ID"))
                        && TRADE_MODIFY_TAG.DEL.getValue().equals(userDiscntData.getString("MODIFY_TAG"))){ // 优惠
                    userDiscntData.put("END_DATE", SysDateMgr.getSysTime());
                }
            }
        }
    }

}
