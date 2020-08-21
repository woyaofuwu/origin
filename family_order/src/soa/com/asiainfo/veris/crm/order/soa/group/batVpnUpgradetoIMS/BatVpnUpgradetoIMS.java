
package com.asiainfo.veris.crm.order.soa.group.batVpnUpgradetoIMS;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.group.changeuserelement.ChangeVpnUserElement;

public class BatVpnUpgradetoIMS extends ChangeUserElement
{
    private static transient Logger logger = Logger.getLogger(ChangeVpnUserElement.class);

    private String imsProId = "8001";

    private BatVpnUpgradetoIMSReqData reqData = null;

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new BatVpnUpgradetoIMSReqData();
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (BatVpnUpgradetoIMSReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setGrpPackageList(new DatasetList(map.getString("GRP_PACKAGE", "[]")));
    }

    public BatVpnUpgradetoIMS()
    {

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author tengg 2011-1-25
     * @throws Exception
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        setRegTradeOther();
        infoRegTradeSvc();
        regGrpPackageTradeData();
    }

    /**
     * @description 处理主台账表数据
     */
    protected void regTrade() throws Exception
    {
        super.regTrade();
        IData data = bizData.getTrade();

    }

    /**
     * 处理产品信息
     * 
     * @throws Exception
     */
    @Override
    public void actTradePrd() throws Exception
    {

        String productId = "8000";

        String productMode = UProductInfoQry.getProductModeByProductId(productId);
        IDataset productInfoset = new DatasetList();
        IData inparams = new DataMap();

        inparams.put("USER_ID", reqData.getUca().getUserId());
        inparams.put("USER_ID_A", "-1");
        inparams.put("PRODUCT_ID", productId);
        inparams.put("PRODUCT_MODE", productMode);

        IDataset userProductList = UserProductInfoQry.GetUserProductInfo(reqData.getUca().getUserId(), "-1", productId, productMode, Route.CONN_CRM_CG);
        if (IDataUtil.isNotEmpty(userProductList))
        {
            // 加8001
            IData productPlus = new DataMap();
            productPlus.put("USER_ID", reqData.getUca().getUserId());
            productPlus.put("USER_ID_A", "-1");
            productPlus.put("PRODUCT_ID", imsProId); // 产品标识
            productPlus.put("PRODUCT_MODE", productMode);// 产品模式
            productPlus.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(imsProId));
            productPlus.put("INST_ID", SeqMgr.getInstId()); // 实例标识
            productPlus.put("START_DATE", getAcceptTime()); // 开始时间
            productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
            productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            productPlus.put("MAIN_TAG", productPlus.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
            productPlus.put("REMARK", "V网升级为融合V网");
            productInfoset.add(productPlus);

            // 删8000
            IData userProduct = userProductList.getData(0);
            userProduct.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            userProduct.put("END_DATE", getAcceptTime());
            userProduct.put("REMARK", "V网升级为融合V网");
            productInfoset.add(userProduct);
        }

        super.addTradeProduct(productInfoset);
    }

    /**
     * 作用：写other表，用来发报文用
     * 
     * @param serviceId
     * @param operCode
     * @param modifyTag
     * @throws Exception
     */
    public void setRegTradeOther() throws Exception
    {
        IDataset dataset = new DatasetList();
        IData CentreData = new DataMap();
        CentreData.put("USER_ID", reqData.getUca().getUserId());
        CentreData.put("RSRV_VALUE_CODE", "CNTRX"); // domain域
        CentreData.put("RSRV_VALUE", "V网升级为融合V网");
        CentreData.put("RSRV_STR1", imsProId);// 产品ID
        CentreData.put("RSRV_STR9", "817"); // 服务id
        // CentreData.put("RSRV_STR11", "I"); // 操作类型
        CentreData.put("OPER_CODE", "27"); // 操作类型 由ng的I改为j2ee的27
        CentreData.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        CentreData.put("START_DATE", this.getAcceptTime());
        CentreData.put("END_DATE", SysDateMgr.getTheLastTime());
        CentreData.put("INST_ID", SeqMgr.getInstId());
        dataset.add(CentreData);
        addTradeOther(dataset);
    }

    /**
     * 修改为8001融合VPMN集团服务，服务开通才不会发联指
     * 
     * @throws Exception
     */
    public void infoRegTradeSvc() throws Exception
    {
        String grpUserId = reqData.getUca().getUserId();
        String servIdOld = "800";// 普通V网800服务
        IDataset userSvc = UserSvcInfoQry.getSvcUserId(grpUserId, servIdOld);
        if (IDataUtil.isEmpty(userSvc))
        {
            CSAppException.apperr(VpmnUserException.VPMN_USER_214, servIdOld);
        }
        IData mainSvcInfo = new DataMap();
        mainSvcInfo.put("USER_ID", grpUserId);// 用户标识
        mainSvcInfo.put("USER_ID_A", "-1");// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。
        mainSvcInfo.put("PRODUCT_ID", imsProId);// 产品标识
        mainSvcInfo.put("PACKAGE_ID", "33001651");// 包标识
        mainSvcInfo.put("SERVICE_ID", "8001");// 服务标识
        mainSvcInfo.put("MAIN_TAG", "1");// 主体服务标志：0-否，1-是
        mainSvcInfo.put("INST_ID", SeqMgr.getInstId());// 实例标识
        mainSvcInfo.put("START_DATE", getAcceptTime());// 开始时间
        mainSvcInfo.put("END_DATE", SysDateMgr.getTheLastTime());
        mainSvcInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());// 状态属性：0-增加，1-删除，2-变更
        mainSvcInfo.put("REMARK", "V网升级为融合V网");

        IDataset svcDatas = new DatasetList();
        svcDatas.add(mainSvcInfo);
        for (int i = 0, size = userSvc.size(); i < size; i++)
        {
            IData map = new DataMap();
            map.putAll(userSvc.getData(i));
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
            map.put("END_DATE", getAcceptTime());
            map.put("REMARK", "V网升级为融合V网");
            svcDatas.add(map);
        }

        addTradeSvc(svcDatas);
    }

    /**
     * 作用：生成集团定制信息
     * 
     * @throws Exception
     */
    public void regGrpPackageTradeData() throws Exception
    {
        // 查询用户的服务信息
        IDataset infos = GrpUserPkgInfoQry.getGrpPackageByUserId(reqData.getUca().getUserId());
        IDataset inDataset = new DatasetList(); // 用户定制信息
        if (IDataUtil.isNotEmpty(infos))
        {
            for (int i = 0; i < infos.size(); i++)
            {
                IData inData = infos.getData(i);
                inData.put("END_DATE", this.getAcceptTime());
                inData.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                inDataset.add(inData);
            }
        }

        IDataset grpPackageDataset = reqData.getGrpPackageList();

        for (int i = 0, size = grpPackageDataset.size(); i < size; i++)
        {
            IData grpPackageData = grpPackageDataset.getData(i);

            String productId = grpPackageData.getString("PRODUCT_ID");
            String packageId = grpPackageData.getString("PACKAGE_ID");
            String element_type_code = grpPackageData.getString("ELEMENT_TYPE_CODE");
            String element_id = grpPackageData.getString("ELEMENT_ID");
            IData rsrv = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(productId, packageId, element_id, element_type_code, "Y");
            grpPackageData.put("INST_ID", grpPackageData.getString("INST_ID", SeqMgr.getInstId()));
            grpPackageData.put("RSRV_STR1", rsrv.getString("RSRV_STR1", "")); // 预留字段1
            grpPackageData.put("RSRV_STR2", rsrv.getString("RSRV_STR2", "")); // 预留字段2
            grpPackageData.put("RSRV_STR3", rsrv.getString("RSRV_STR3", "")); // 预留字段3
            grpPackageData.put("FORCE_TAG", rsrv.getString("FORCE_TAG", "0")); // 预留字段3
            grpPackageData.put("DEFAULT_TAG", rsrv.getString("DEFAULT_TAG", "0")); // 预留字段3
            grpPackageData.put("START_DATE", this.getAcceptTime());
            grpPackageData.put("END_DATE", SysDateMgr.getTheLastTime());
            grpPackageData.put("USER_ID", reqData.getUca().getUserId());
            grpPackageData.put("EPARCHY_CODE", reqData.getUca().getUser().getEparchyCode());
            grpPackageData.put("REMARK", "V网升级为融合V网");
            inDataset.add(grpPackageData); // 集团为成员定制信息
        }

        super.addTradeGrpPackage(inDataset);
    }

    @Override
    protected String setTradeTypeCode() throws Exception
    {
        // 设置业务类型
        return "2528";
    }
}
