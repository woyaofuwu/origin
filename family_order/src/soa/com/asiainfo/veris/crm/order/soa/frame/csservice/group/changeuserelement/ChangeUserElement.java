package com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement;

import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.data.TradeTableEnum;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.group.common.base.GrpModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpAttrUtil;

public class ChangeUserElement extends GroupBean {
    protected GrpModuleData moduleData = new GrpModuleData();

    protected ChangeUserElementReqData reqData = null;

    /**
     * 处理产品信息
     * 
     * @throws Exception
     */
    public void actTradePrd() throws Exception {
        IData productIdset = reqData.cd.getProductIdSet();

        if (IDataUtil.isEmpty(productIdset)) {
            return;
        }

        IDataset productInfoset = new DatasetList();

        Iterator<String> iterator = productIdset.keySet().iterator();
        while (iterator.hasNext()) {
            String productId = iterator.next();

            String productMode = UProductInfoQry.getProductModeByProductId(productId);

            String modifyTag = productIdset.getString("MODIFY_TAG");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modifyTag)) // 新增
            {

                IDataset userProductList = UserProductInfoQry.GetUserProductInfo(reqData.getUca().getUserId(), null, productId, productMode, Route.CONN_CRM_CG);// 集团订购-1可不传，兼容老系统BBOSS
                // user_id_a放的商品的user_id
                if (IDataUtil.isNotEmpty(userProductList))
                    continue;

                IData productPlus = new DataMap();

                productPlus.put("PRODUCT_ID", productId); // 产品标识
                productPlus.put("PRODUCT_MODE", productMode);// 产品模式
                productPlus.put("BRAND_CODE", UProductInfoQry.getBrandCodeByProductId(productId));
                productPlus.put("INST_ID", SeqMgr.getInstId()); // 实例标识
                productPlus.put("START_DATE", getAcceptTime()); // 开始时间
                productPlus.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                productPlus.put("MAIN_TAG", productPlus.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是

                productInfoset.add(productPlus);
            } else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag)) // 删除
            {
                IDataset userProductList = UserProductInfoQry.GetUserProductInfo(reqData.getUca().getUserId(), null, productId, productMode, Route.CONN_CRM_CG);// 集团订购-1可不传，兼容老系统BBOSS
                // user_id_a放的商品的user_id

                if (IDataUtil.isEmpty(userProductList))
                    continue;

                IData userProduct = userProductList.getData(0);
                userProduct.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                userProduct.put("END_DATE", getAcceptTime());

                productInfoset.add(userProduct);
            }
        }

        super.addTradeProduct(productInfoset);
    }

    public void actTradePrdParams() throws Exception {
        IData productParam = reqData.cd.getProductParamMap(reqData.getUca().getProductId());

        if (IDataUtil.isEmpty(productParam)) {
            return;
        }

        IData productInfo = UserProductInfoQry.getUserProductBykey(reqData.getUca().getUserId(), reqData.getUca().getProductId(), null, null);// 集团订购-1可不传，兼容老系统BBOSS
        // user_id_a放的商品的user_id

        String instId = productInfo.getString("INST_ID");

        IData param = new DataMap();
        param.put("START_DATE", getAcceptTime());
        param.put("END_DATE", SysDateMgr.getTheLastTime());

        // 处理产品参数信息
        IDataset paramSet = GrpAttrUtil.dealAttrParam(productParam, reqData.getUca().getUserId(), "P", instId, param);

        super.filterParamAttr("NOTIN_", paramSet);
        if (IDataUtil.isNotEmpty(paramSet)) {
            this.addTradeAttr(paramSet);
        }

    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception {
        super.actTradeSub();

        // 处理产品表
        actTradePrd();

        // 处理产品个性化参数
        actTradePrdParams();

        // 服务状态表
        actTradeSvcState();

        // 用户资料，最后执行，ADC/MAS可能需要在actTradeSvcState()中设置TF_F_USER.USER_STATE_CODESET字段
        actTradeUserExtend();
    }

    /**
     * 判断是否修改了用户表中的合同信息
     * 
     * @throws Exception
     */

    protected void actTradeUserExtend() throws Exception {
        IData userData = reqData.getUca().getUser().toData();
        // 用户
        if (IDataUtil.isNotEmpty(userData)) {
            IData userInfoExtend = getTradeUserExtendData();
            if (IDataUtil.isNotEmpty(userInfoExtend)) {
                userData.putAll(userInfoExtend);
                this.addTradeUser(userData);
            }
        }
    }

    /**
     * 获取操作费用
     * 
     * @return
     * @throws Exception
     */
    protected String getOperFee() throws Exception {
        // 得到对象
        IDataset idsReg = bizData.getTradefeeSub();

        int iOperFee = 0;

        if (IDataUtil.isNotEmpty(idsReg)) {
            // 初始化
            IData map = null;

            for (int size = idsReg.size(), i = 0; i < size; i++) {
                map = idsReg.getData(i);

                int fee = Integer.parseInt(map.getString("FEE", "0"));

                String feeMode = map.getString("FEE_MODE");

                if ("0".equals(feeMode)) {
                    iOperFee += fee;
                }
            }
        }

        return String.valueOf(iOperFee);
    }

    @Override
    protected BaseReqData getReqData() throws Exception {
        return new ChangeUserElementReqData();
    }

    /**
     * 修改合同信息号
     * 
     * @return
     * @throws Exception
     */
    public IData getTradeUserExtendData() throws Exception {
        IData userData = reqData.getUca().getUser().toData();
        IData userDataExtend = new DataMap();
        if (IDataUtil.isNotEmpty(userData)) {
            String newContractId = reqData.getContractId();
            String contractId = reqData.getUca().getUser().getContractId();
            if (StringUtils.isNotBlank(newContractId) && !newContractId.equals(contractId)) {
                userDataExtend.put("CONTRACT_ID", newContractId);
                userDataExtend.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

            }
        }
        return userDataExtend;
    }

    @Override
    protected final void initProductCtrlInfo() throws Exception {

        String productId = reqData.getUca().getProductId();
        if ("97011".equals(productId) || "97012".equals(productId)|| "97016".equals(productId)
        		|| "970111".equals(productId)|| "970112".equals(productId)|| "970121".equals(productId)||"970122".equals(productId)) {
            getProductCtrlInfo(productId, BizCtrlType.ChangeMemberDis);
        } else {
            getProductCtrlInfo(productId, BizCtrlType.ChangeUserDis);
        }
    }

    @Override
    protected void initReqData() throws Exception {
        super.initReqData();

        reqData = (ChangeUserElementReqData) getBaseReqData();
    }

    @Override
    protected void makInit(IData map) throws Exception {
        super.makInit(map);

        moduleData.getMoudleInfo(map);
    }

    @Override
    protected void makReqData(IData map) throws Exception {
        super.makReqData(map);

        makReqDataElement();
    }

    /**
     * 元素处理
     * 
     * @throws Exception
     */
    protected void makReqDataElement() throws Exception {
        // 处理元素信息
        GroupModuleParserBean.grpElement(reqData, moduleData);

        // 处理资源
        GroupModuleParserBean.grpRes(reqData, moduleData);

        // 修改优惠
        modifyDiscnt();

        // 处理产品和产品参数
        makReqDataProductParam();

        // 处理集团定制
        makReqDataGrpPackage();
    }

    protected void makReqDataGrpPackage() throws Exception {
        IDataset grpPackage = new DatasetList(); // 用户定制信息
        IDataset grpPackageDataset = moduleData.getGrpPackageInfo();

        for (int i = 0, size = grpPackageDataset.size(); i < size; i++) {
            IData grpPackageData = grpPackageDataset.getData(i);

            if (grpPackageData.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.EXIST.getValue())) {
                continue;
            }

            if (grpPackageData.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.Add.getValue())) {

                // IData grpPackageData = grpPackageDataset.getData(i);

                IData rsrv = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(grpPackageData.getString("PRODUCT_ID"), grpPackageData.getString("PACKAGE_ID"), grpPackageData.getString("ELEMENT_ID"), grpPackageData
                        .getString("ELEMENT_TYPE_CODE"), "Y");
                // 将TD_B_PACKAGE_ELEMENT的预留字段复制到GRP_PACKAGE里
                // IData rsrv = ProductInfoQry.getRsrvByPk(grpPackageData);

                grpPackageData.put("RSRV_STR1", rsrv.getString("RSRV_STR1", "")); // 预留字段1
                grpPackageData.put("RSRV_STR2", rsrv.getString("RSRV_STR2", "")); // 预留字段2
                grpPackageData.put("RSRV_STR3", rsrv.getString("RSRV_STR3", "")); // 预留字段3
                grpPackageData.put("USER_ID", reqData.getUca().getUserId());
                grpPackageData.put("DEFAULT_TAG", rsrv.getString("DEFAULT_TAG", "0"));
                grpPackageData.put("FORCE_TAG", rsrv.getString("FORCE_TAG", "0"));
                grpPackageData.put("EPARCHY_CODE", "ZZZZ");
                grpPackageData.put("INST_ID", SeqMgr.getInstId());
                grpPackageData.put("START_DATE", getAcceptTime());
                grpPackageData.put("END_DATE", SysDateMgr.getTheLastTime());
            }
            if (grpPackageData.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.DEL.getValue())) {
                grpPackageData.put("END_DATE", getAcceptTime());

                IDataset grpPackages = UserGrpPkgInfoQry.getUserSingleGrpPackage(reqData.getUca().getUserId(), grpPackageData.getString("PRODUCT_ID"), grpPackageData.getString("PACKAGE_ID"), grpPackageData.getString("ELEMENT_ID"), grpPackageData
                        .getString("ELEMENT_TYPE_CODE"));

                if (IDataUtil.isEmpty(grpPackages)) {
                    continue; // 容错处理
                }

                (grpPackages.getData(0)).remove("END_DATE");

                grpPackageData.putAll(grpPackages.getData(0));
            }
            grpPackage.add(grpPackageData); // 集团为成员定制信息
        }

        reqData.cd.putGrpPackage(grpPackage);
    }

    protected void makReqDataProductParam() throws Exception {
        IDataset infos = moduleData.getProductParamInfo();

        for (int i = 0, size = infos.size(); i < size; i++) {
            IData info = infos.getData(i);

            String productId = info.getString("PRODUCT_ID");

            IDataset productParam = info.getDataset("PRODUCT_PARAM");

            reqData.cd.putProductParamList(productId, productParam);
        }
    }

    @Override
    protected void makUca(IData map) throws Exception {
        super.makUcaForGrpNormal(map);

    }

    /**
     * 修改优惠
     * 
     * @throws Exception
     */
    protected void modifyDiscnt() throws Exception {
        IDataset disDataset = reqData.cd.getDiscnt();

        if (IDataUtil.isEmpty(disDataset)) {
            return;
        }
        for (int i = disDataset.size() - 1; i >= 0; i--) {
            IData disData = disDataset.getData(i);

            if (!StringUtils.equals(TRADE_MODIFY_TAG.Add.getValue(), disData.getString("MODIFY_TAG"))) {
                continue;
            }
            String userId = disData.getString("USER_ID", reqData.getUca().getUserId());// 用户标识
            String productId = disData.getString("PRODUCT_ID", "");
            String packageId = disData.getString("PACKAGE_ID", "");
            String elementId = disData.getString("ELEMENT_ID", "");

            IDataset userDiscntList = UserDiscntInfoQry.getExistUserSingleProductDis(userId, null, productId, packageId, elementId);

            if (IDataUtil.isEmpty(userDiscntList)) {
                continue;
            }
            for (int j = 0, size = userDiscntList.size(); j < size; j++) {
                IData userDiscntData = userDiscntList.getData(j);

                if (userDiscntData.getString("END_DATE").compareTo(disData.getString("END_DATE")) < 0) {
                    userDiscntData.put("END_DATE", disData.getString("END_DATE"));
                    userDiscntData.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

                    disDataset.remove(i);// 把新增那条删了
                    disDataset.add(userDiscntData);// 修改那条加上
                }
            }
        }

    }

    protected void modTradeData() throws Exception {
        super.modTradeData();

        if (reqData.getIsChange()) {
            return;
        }

        String allTables = bizData.getTrade().getString("INTF_ID");

        if (allTables.contains(TradeTableEnum.TRADE_SVC.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_DISCNT.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_RES.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_ATTR.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_OTHER.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_POST.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_PRODUCT.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_PLATSVC.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_GRP_PACKAGE.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_GRP_PLATSVC.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_GRP_MOLIST.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_VPN.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_USER.getValue())) {
            return;
        }
        if (allTables.contains(TradeTableEnum.TRADE_SVCSTATE.getValue())) {
            return;
        }
        CSAppException.apperr(GrpException.CRM_GRP_4);
    }

    @Override
    protected void setTradeAttr(IData map) throws Exception {
        super.setTradeAttr(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));
    }

    @Override
    protected void setTradeDiscnt(IData map) throws Exception {
        super.setTradeDiscnt(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));// 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1。

        map.put("SPEC_TAG", map.getString("SPEC_TAG", "2")); // 特殊优惠标记：0-正常产品优惠，1-特殊优惠，2-关联优惠。
        map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE", ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()))); // 关系类型
    }

    @Override
    protected void setTradefeeSub(IData map) throws Exception {
        super.setTradefeeSub(map);

        map.put("USER_ID", reqData.getUca().getUserId());// 用户标识
    }

    @Override
    protected void setTradeProduct(IData map) throws Exception {
        super.setTradeProduct(map);

        if (!StringUtils.equals(TRADE_MODIFY_TAG.Add.getValue(), map.getString("MODIFY_TAG"))) {
            return;
        }

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识

        String productId = map.getString("PRODUCT_ID");

        map.put("PRODUCT_ID", map.getString("PRODUCT_ID", reqData.getUca().getProductId())); // 产品标识

        map.put("PRODUCT_MODE", map.getString("PRODUCT_MODE", "10")); // 产品的模式：00:基本产品，01:附加产品

        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        map.put("BRAND_CODE", brandCode); // 品牌编码

        map.put("INST_ID", map.getString("INST_ID", "0")); // 实例标识

        map.put("START_DATE", map.getString("START_DATE", SysDateMgr.getSysTime())); // 开始时间
        map.put("END_DATE", map.getString("END_DATE", SysDateMgr.getTheLastTime())); // 结束时间

        map.put("MAIN_TAG", map.getString("PRODUCT_MODE").equals(GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.toString()) ? "1" : "0");// 主产品标记：0-否，1-是
    }

    @Override
    protected void setTradeRes(IData map) throws Exception {
        super.setTradeRes(map);

        map.put("USER_ID", reqData.getUca().getUserId());
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));
    }

    @Override
    protected void setTradeSvc(IData map) throws Exception {
        super.setTradeSvc(map);

        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        map.put("USER_ID_A", map.getString("USER_ID_A", "-1")); // 用户标识A：关联服务中的A用户标识，通常为一集团用户或虚拟用户。对于非关联服务填-1

        String elementId = map.getString("ELEMENT_ID", "");

        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), elementId);
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是
        map.put("CAMPN_ID", map.getString("CAMPN_ID"));

        if (elementId.matches("910|911")) {
            map.put("RSRV_STR1", "boss"); // 预留字段1
            map.put("RSRV_STR2", "boss"); // 预留字段2
            map.put("RSRV_STR3", reqData.getUca().getCustGroup().getGroupId());// 预留字段3
            map.put("RSRV_STR4", "123456"); // 预留字段4
        } else {
            map.put("RSRV_STR1", map.getString("RSRV_STR1", ""));// 预留字段1
            map.put("RSRV_STR2", map.getString("RSRV_STR2", ""));// 预留字段2
            map.put("RSRV_STR3", map.getString("RSRV_STR3", ""));// 预留字段3
            map.put("RSRV_STR4", map.getString("RSRV_STR4", ""));// 预留字段4
        }
    }

    @Override
    protected void updTradeProcessTagSet() throws Exception {
        super.updTradeProcessTagSet();

        setProcessTag(2, "1");
    }

}
