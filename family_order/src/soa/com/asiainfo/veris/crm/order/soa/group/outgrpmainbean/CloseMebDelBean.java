
package com.asiainfo.veris.crm.order.soa.group.outgrpmainbean;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.ElementException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

public class CloseMebDelBean extends MemberBean
{
    boolean ifCentrex = false; // 是否融合网

    protected CloseMebDelBeanReqData reqData = null;

    /**
     * 生成台帐表其它数据（拼台帐前）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();

        // 判断是否融合V网
        checkIfCentrexVPMN(reqData.getUca().getSerialNumber()); // 成员sn
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        infoRegDataRelation(); // 湖南网外都走UU

        infoRegDiscnt(); // 闭合群 优惠

        // centrex 查other表发集团配置业务指令
        infoRegDataMemberCentreOther();
    }

    /**
     * 判断是否融合V网
     * 
     * @author:tengg
     * @param userida
     * @param useridmeb
     * @throws Exception
     */
    public void checkIfCentrexVPMN(String useridmeb) throws Exception
    {
        if (StringUtils.isNotBlank(useridmeb))
        {
            IDataset uulist = RelaUUInfoQry.getRelaInfoByUserIdbAndProId(useridmeb, "8000");
            if (IDataUtil.isNotEmpty(uulist))
            {
                IData uudata = uulist.getData(0);
                String userIdA = uudata.getString("USER_ID_A", "");
                IDataset userVpnList = UserVpnInfoQry.qryUserVpnByUserId(userIdA);
                if (IDataUtil.isNotEmpty(userVpnList))
                {
                    String vpn_user_code = userVpnList.getData(0).getString("VPN_USER_CODE", "");
                    if (vpn_user_code.equals("2"))
                    {
                        ifCentrex = true;
                    }
                }
            }
        }
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new CloseMebDelBeanReqData();
    }

    /**
     * 融合V网 登记平台other表
     * 
     * @throws Exception
     */
    public void infoRegDataMemberCentreOther() throws Exception
    {
        if (!ifCentrex)
        {
            return;
        }

        IDataset dataset = new DatasetList();
        IData centreData = new DataMap();

        centreData.put("USER_ID", reqData.getUca().getUserId()); // 成员user_id
        centreData.put("RSRV_VALUE_CODE", "CNTRX");// domain域
        centreData.put("RSRV_VALUE", "融合V网");

        centreData.put("RSRV_STR9", "860"); // 服务id
        centreData.put("OPER_CODE", "30"); // 操作类型
        centreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        centreData.put("START_DATE", getAcceptTime());
        centreData.put("END_DATE", getAcceptTime()); // 立即截止
        centreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(centreData);

        this.addTradeOther(dataset);
    }

    public void infoRegDataRelation() throws Exception
    {
        String user_id_a = reqData.getUSER_ID_A(); // 闭合群user_id
        String user_id_b = reqData.getUca().getUserId();

        IDataset relationUUs = RelaUUInfoQry.qryUU(user_id_a, user_id_b, "MB", null);
        IData relationUU = new DataMap();
        if (IDataUtil.isEmpty(relationUUs))
        {
            CSAppException.apperr(GrpException.CRM_GRP_482);
        }

        for (int i = 0; i < relationUUs.size(); i++)
        {
            relationUU = relationUUs.getData(i);

            relationUU.put("END_DATE", getAcceptTime());
            relationUU.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        this.addTradeRelation(relationUU);
    }

    /**
     * 添加优惠
     * 
     * @throws Exception
     */
    public void infoRegDiscnt() throws Exception
    {
        IData userDistData = new DataMap();

        String user_id = reqData.getUca().getUserId(); // 用成员的USERID查询
        String user_id_a = reqData.getGrpUca().getUserId(); // 得到VPMN集团用户的USER_ID
        String discnt_code = reqData.getDISCNT_CODE(); // 优惠编码

        IDataset userdiscntlist = UserDiscntInfoQry.getUserDiscntInfoByAB(user_id, discnt_code, user_id_a);
        // 执行删除
        if (IDataUtil.isEmpty(userdiscntlist))
        {
            CSAppException.apperr(ElementException.CRM_ELEMENT_155);
        }

        for (int i = 0; i < userdiscntlist.size(); i++)
        {
            userDistData = userdiscntlist.getData(i);

            userDistData.put("END_DATE", getAcceptTime());
            userDistData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userDistData.put("DIVERSIFY_ACCT_TAG", "1"); // 多账期修改:已经处理过元素的时间
        }

        this.addTradeDiscnt(userDistData);
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CloseMebDelBeanReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setUSER_ID_A(map.getString("USER_ID_A")); // 闭合群user_id
        reqData.setSERIAL_NUMBER_A(map.getString("SERIAL_NUMBER_A")); // 闭合群sn
        reqData.setDISCNT_CODE(map.getString("DISCNT_CODE")); // 闭合群优惠编码
        reqData.setVPN_NO(map.getString("VPN_NO")); // 集团vpn_no
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        makUcaForMebNormal(map);
    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", ""); // 客户名称
        data.put("CUST_ID", reqData.getUca().getCustId());
        data.put("ACCT_ID", reqData.getUca().getAcctId()); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", CSBizBean.getTradeEparchyCode()); // 归属地州
        data.put("CITY_CODE", CSBizBean.getVisit().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", "-1"); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", "-1"); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", reqData.getUca().getBrandCode());// 品牌编码

        data.put("USER_ID", reqData.getUca().getUserId()); // 成员的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        data.put("USER_ID_B", reqData.getUSER_ID_A()); // 闭合群的
        data.put("SERIAL_NUMBER_B", reqData.getSERIAL_NUMBER_A());

        data.put("RSRV_STR1", ""); // 预留字段1
        data.put("RSRV_STR2", ""); // 预留字段2
        data.put("RSRV_STR3", reqData.getSERIAL_NUMBER_A()); // 闭合群Sn
        data.put("RSRV_STR4", "2"); // 组类型- 0：寻呼组1：代答组 2:闭合用户群组

        IData discntInfo = UDiscntInfoQry.getDiscntInfoByPk(reqData.getDISCNT_CODE());
        String rsrv_str3 = discntInfo.getString("RSRV_STR3", "");
        data.put("RSRV_STR5", rsrv_str3); // 用于发指令 取TD_B_DISCNT表rsrv_str3

        data.put("RSRV_STR6", ""); // 预留字段6
        data.put("RSRV_STR7", ""); // 预留字段7
        data.put("RSRV_STR8", ""); // 预留字段8
        data.put("RSRV_STR9", ""); // 预留字段9
        data.put("RSRV_STR10", reqData.getVPN_NO()); // 集团vpn_no 送服务开通
    }

    protected String setTradeTypeCode() throws Exception
    {
        return "1081"; // 闭合群成员新增业务
    }
}
