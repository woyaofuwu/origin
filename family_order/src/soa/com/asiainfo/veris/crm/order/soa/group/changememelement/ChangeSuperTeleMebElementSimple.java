
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.util.Iterator;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.StrUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserResInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserVpnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;

/**
 * 处理移动总机的总机号码的新增与注销
 */
public class ChangeSuperTeleMebElementSimple extends MemberBean
{

    private String mebModifyTag = "";

    private String shortCode = "";

    private String roleCodeB = "";

    private IData productIdSet;

    private String tradeId = "";

    public ChangeSuperTeleMebElementSimple()
    {

    }

    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        // 处理UU关系信息
        infoRegDataRelation();

        // 处理产品信息
        infoRegDataProduct();

        // 处理VPNMEB信息
        infoRegDataVpnMeb();

        // 处理资源信息
        infoRegDataRes();

        // 处理工单依赖
        infoRegDataTradeLimit();
    }

    public void infoRegDataProduct() throws Exception
    {
        IDataset productDataset = new DatasetList();

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            if (IDataUtil.isNotEmpty(productIdSet))
            {
                Iterator<String> iterator = productIdSet.keySet().iterator();
                while (iterator.hasNext())
                {
                    String key = iterator.next();
                    String productMode = productIdSet.getString(key, "");
                    if (productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_MAIN_PLUS_PRODUCT.getValue()) || productMode.equals(GroupBaseConst.PRODUCT_MODE.MEM_PLUS_PRODUCT.getValue()))
                    {
                        IData productData = new DataMap();
                        productData.put("USER_ID", reqData.getUca().getUserId());
                        productData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                        productData.put("PRODUCT_ID", key);
                        productData.put("PRODUCT_MODE", productIdSet.getString(key));
                        productData.put("INST_ID", SeqMgr.getInstId());
                        productData.put("START_DATE", getAcceptTime());
                        productData.put("END_DATE", SysDateMgr.getTheLastTime());
                        productData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        productDataset.add(productData);
                    }
                }
            }

        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            IDataset userProductList = UserProductInfoQry.qryGrpMebProduct(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
            for (int i = 0; i < userProductList.size(); i++)
            {
                IData userProduct = userProductList.getData(i);
                userProduct.put("END_DATE", getAcceptTime());
                userProduct.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
            productDataset.addAll(userProductList);
        }

        super.addTradeProduct(productDataset);
    }

    /**
     * 处理UU关系信息
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {

        IData relationData = null;

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            relationData = new DataMap();

            relationData.put("USER_ID_A", reqData.getGrpUca().getUserId());
            relationData.put("SERIAL_NUMBER_A", reqData.getGrpUca().getSerialNumber());
            relationData.put("USER_ID_B", reqData.getUca().getUserId());
            relationData.put("SERIAL_NUMBER_B", reqData.getUca().getSerialNumber());
            relationData.put("RELATION_TYPE_CODE", "25"); // 关系类型-固定值
            relationData.put("ROLE_TYPE_CODE", "");
            relationData.put("ROLE_CODE_A", "0"); // A角色编码
            relationData.put("ROLE_CODE_B", roleCodeB); // B角色编码-总机2或管理员9角色
            relationData.put("SHORT_CODE", shortCode);
            relationData.put("INST_ID", SeqMgr.getInstId());

            relationData.put("START_DATE", getAcceptTime());
            relationData.put("END_DATE", SysDateMgr.getTheLastTime());

            relationData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            IDataset uuInfos = RelaUUInfoQry.qryUU(reqData.getGrpUca().getUserId(), reqData.getUca().getUserId(), "25", null);
            if (IDataUtil.isNotEmpty(uuInfos))
            {
                relationData = uuInfos.getData(0);
                relationData.put("END_DATE", getAcceptTime());
                relationData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }

        super.addTradeRelation(relationData);
    }

    /**
     * 处理资源信息
     * 
     * @throws Exception
     */
    public void infoRegDataRes() throws Exception
    {

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {

            IDataset resInfos = UserResInfoQry.getUserProductRes(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null);

            if (IDataUtil.isEmpty(resInfos))
            {
                return;
            }

            for (int i = 0; i < resInfos.size(); i++)
            {
                IData resData = resInfos.getData(i);
                resData.put("END_DATE", getAcceptTime());
                resData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }
    }

    /**
     * 处理工单依赖
     * 
     * @throws Exception
     */
    public void infoRegDataTradeLimit() throws Exception
    {
        IData tradeLimitData = new DataMap();

        // 新增, 先处理集团工单, 再处理成员工单
        if (TRADE_MODIFY_TAG.Add.getValue().equals(mebModifyTag))
        {
            tradeLimitData.put("TRADE_ID", getTradeId());
            tradeLimitData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(getTradeId()));
            tradeLimitData.put("LIMIT_TRADE_ID", tradeId);
            tradeLimitData.put("LIMIT_TYPE", "0");
            tradeLimitData.put("ROUTE_ID", BizRoute.getRouteId());
        }
        // 注销, 先处理成员工单, 再处理集团工单
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(mebModifyTag))
        {
            tradeLimitData.put("TRADE_ID", tradeId);
            tradeLimitData.put("ACCEPT_MONTH", StrUtil.getAcceptMonthById(tradeId));
            tradeLimitData.put("LIMIT_TRADE_ID", getTradeId());
            tradeLimitData.put("LIMIT_TYPE", "0");
            tradeLimitData.put("ROUTE_ID", Route.CONN_CRM_CG);
        }

        Dao.insert("TF_B_TRADE_LIMIT", tradeLimitData, Route.getJourDb(Route.CONN_CRM_CG));
    }

    public void infoRegDataVpnMeb() throws Exception
    {
        IData vpnMebData = null;

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            vpnMebData = new DataMap();
            vpnMebData.put("INST_ID", SeqMgr.getInstId());
            vpnMebData.put("USER_ID", reqData.getUca().getUserId());
            vpnMebData.put("USER_ID_A", reqData.getGrpUca().getUserId());
            vpnMebData.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());
            vpnMebData.put("SHORT_CODE", shortCode);
            vpnMebData.put("MEMBER_KIND", "1"); // 成员类型：0：集团重要用户，1：集团普通用户，2：集团发展人，3：集团客户外的用户
            vpnMebData.put("PKG_TYPE", ""); // 资费套餐类型
            vpnMebData.put("CALL_ROAM_TYPE", ""); // 资费套餐类型
            vpnMebData.put("BYCALL_ROAM_TYPE", ""); // 被叫漫游权限
            vpnMebData.put("OPEN_DATE", getAcceptTime());
            vpnMebData.put("REMOVE_TAG", "0");
            vpnMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            vpnMebData = UserVpnInfoQry.getMemberVpnByUserId(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), reqData.getUca().getUserEparchyCode());
            if (IDataUtil.isNotEmpty(vpnMebData))
            {
                vpnMebData.put("TRADE_TYPE_CODE", "2437");
                vpnMebData.put("REMOVE_TAG", "1");
                vpnMebData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            }
        }

        super.addTradeVpnMeb(vpnMebData);
    }

    @Override
    protected void makInit(IData map) throws Exception
    {
        super.makInit(map);

        mebModifyTag = map.getString("MEB_MODIFY_TAG", "");
        productIdSet = map.getData("PRODUCT_ID_SET");
        shortCode = map.getString("SHORT_CODE");
        roleCodeB = map.getString("MEM_ROLE_B");
        tradeId = map.getString("TRADE_ID");
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        makReqDataElement(map);
    }

    public void makReqDataElement(IData map) throws Exception
    {

        IDataset svcDataset = new DatasetList();
        IDataset disDataset = new DatasetList();

        if (mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()))
        {
            IData svcData = new DataMap();
            svcData.put("USER_ID", reqData.getUca().getUserId());
            svcData.put("USER_ID_A", reqData.getGrpUca().getUserId());
            svcData.put("PRODUCT_ID", "-1");
            svcData.put("PACKAGE_ID", "-1");
            svcData.put("SERVICE_ID", "611");// 服务标识
            svcData.put("MAIN_TAG", "0");
            svcData.put("CAMPN_ID", "");
            svcData.put("INST_ID", SeqMgr.getInstId());
            svcData.put("START_DATE", getAcceptTime());
            svcData.put("END_DATE", SysDateMgr.getTheLastTime());
            svcData.put("RSRV_STR8", reqData.getGrpUca().getUserId());
            svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svcData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志

            svcDataset.add(svcData);

            if (!map.getString("E_DISCNT_CODE", "").equals(""))
            {
                IData disData = new DataMap();
                disData.put("USER_ID", reqData.getUca().getUserId());
                disData.put("USER_ID_A", reqData.getGrpUca().getUserId());
                disData.put("PRODUCT_ID", "-1");
                disData.put("PACKAGE_ID", "-1");
                disData.put("DISCNT_CODE", map.getString("E_DISCNT_CODE"));

                disData.put("INST_ID", SeqMgr.getInstId());
                disData.put("START_DATE", getAcceptTime());
                disData.put("END_DATE", SysDateMgr.getTheLastTime());
                disData.put("SPEC_TAG", "0");
                disData.put("RELATION_TYPE_CODE", "25");
                disData.put("CAMPN_ID", "");
                disData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                disData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志

                disDataset.add(disData);
            }

        }
        else if (mebModifyTag.equals(TRADE_MODIFY_TAG.DEL.getValue()))
        {
            IDataset userSvcList = UserSvcInfoQry.getUserProductSvc(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId(), null);
            if (IDataUtil.isNotEmpty(userSvcList))
            {
                IData svcData = userSvcList.getData(0);
                svcData.put("END_DATE", getAcceptTime());
                svcData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                svcData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志
                svcDataset.add(svcData);
            }

            IDataset userDisList = UserDiscntInfoQry.getUserProductDis(reqData.getUca().getUserId(), reqData.getGrpUca().getUserId());
            if (IDataUtil.isNotEmpty(userDisList))
            {
                IData disData = userDisList.getData(0);
                disData.put("END_DATE", getAcceptTime());
                disData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                disData.put("DIVERSIFY_ACCT_TAG", "1");// 分散账期元素处理标志
                disDataset.add(disData);
            }
        }

        reqData.cd.putSvc(svcDataset);
        reqData.cd.putDiscnt(disDataset);
    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        super.makUca(map);
        makUcaForMebNormal(map);
    }

    /**
     * 覆盖父类方法,保存预留字段信息
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData tradeData = bizData.getTrade();

        tradeData.put("RSRV_STR9", tradeId);
        tradeData.put("RSRV_STR10", "0");
    }

    protected String setTradeTypeCode() throws Exception
    {
        return mebModifyTag.equals(TRADE_MODIFY_TAG.Add.getValue()) ? "2924" : "2927";
    }

    /**
     * 覆盖父类的方法,保存预留字段信息
     */
    public void setTradeUser(IData map) throws Exception
    {
        super.setTradeUser(map);

        map.put("RSRV_STR9", tradeId); // trade_id
        map.put("RSRV_STR10", "0");
    }

}
