
package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
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
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.DiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangePower100UserElementReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupModuleParserBean;

public class ChangePower100UserElement extends ChangeUserElement
{

    private IDataset userInfos = new DatasetList();

    protected ChangePower100UserElementReqData reqData = null;

    /**
     * 构造函数
     * 
     * @param pd
     */
    public ChangePower100UserElement()
    {
    }

    /**
     * 生成登记信息
     * 
     * @author hud
     * @throws Exception
     */
    @Override
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        // 获取产品对应的用户信息
        getUserInfos();

        // 将注销的优惠注销 将优惠挂在子用户下
        infoRegDataPower100Element();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author xiajj
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {

        super.actTradeSub();
        // 建立子产品和动力100的uu关系
        infoRegDataRelation();
    }

    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangePower100UserElementReqData();
    }

    /**
     * 获取产品对应的用户信息
     * 
     * @throws Exception
     */
    public void getUserInfos() throws Exception
    {
        IDataset power100setList = reqData.getPower100Infos();

        if (IDataUtil.isEmpty(power100setList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_100);
        }

        IDataset relaUUset = RelaUUInfoQry.getRelaUUInfoByUserida(reqData.getUca().getUserId(), Route.CONN_CRM_CG);
        if (IDataUtil.isEmpty(relaUUset))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_100);
        }

        for (int i = 0, size = power100setList.size(); i < size; i++)
        {
            IData power100ProductInfo = power100setList.getData(i);

            IData result = UserInfoQry.getGrpUserInfoByUserIdForGrp(power100ProductInfo.getString("USER_ID"), "0"); // 查用户表

            if (IDataUtil.isEmpty(result))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_100);
            }

            result.put("immeffect", (power100ProductInfo.getString("USER_ID") == null) ? true : false);
            if ("null".equals(power100ProductInfo.getString("DISCNT_CODE", "null")))
            {
                String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()); // 关系类型
                IDataset uuInfos = RelaUUInfoQry.qryUUForGrp(reqData.getUca().getUserId(), power100ProductInfo.getString("USER_ID"), relationTypeCode, null);
                if (IDataUtil.isEmpty(uuInfos))
                {
                    result.put("IS_EXIST_UU", false);
                    result.put("STATE", "ADD");
                }
                else
                {
                    result.put("IS_EXIST_UU", true);
                    result.put("STATE", "EXISTS");
                }
            }
            else
            {
                result.put("IS_EXIST_UU", true);
                result.put("STATE", "EXISTS");
            }
            // result.put("IS_EXIST_UU", (!"null".equals(power100setList.getData(i).getString("DISCNT_CODE", "null")) ?
            // "true" : "false"));
            // result.put("STATE", (!"null".equals(power100setList.getData(i).getString("DISCNT_CODE", "null")) ?
            // "EXISTS" : "ADD"));
            userInfos.add(result);
        }

    }

    /**
     * 将动力100的成员服务、优惠、资源挂在子用户下
     * 
     * @throws Exception
     */
    public void infoRegDataPower100Element() throws Exception
    {
        IDataset paramDataset = reqData.cd.getElementParam();
        IDataset grpPkgset = reqData.cd.getGrpPackage();
        IDataset dctDataset = reqData.cd.getDiscnt();
        IDataset svcDataset = reqData.cd.getSvc();
        IDataset powerDctDataset = new DatasetList();
        IDataset grpPackage = new DatasetList();
        IData dctdata = null;

        if (IDataUtil.isEmpty(grpPkgset) && IDataUtil.isEmpty(dctDataset))
        {
            IDataset userGrpPkgSet = UserGrpPkgInfoQry.getUserGrpPackageForGrp(reqData.getUca().getUserId());
            if (IDataUtil.isNotEmpty(userGrpPkgSet))
            { // 前台没有新增、删除折扣，则将已有折扣加到新加入的子产品上
                grpPackage.addAll(userGrpPkgSet);
            }
        }
        else if (IDataUtil.isEmpty(grpPkgset) && IDataUtil.isNotEmpty(dctDataset))
        { // 取消折扣产品
            grpPackage.addAll(dctDataset);
        }
        else
        {
            grpPackage.addAll(grpPkgset);
        }
        // 获取预留字段1，表示该元素属于哪个子产品
        for (int j = 0; j < grpPackage.size(); j++)
        {
            IData packageData = grpPackage.getData(j);
            String elementType = packageData.getString("ELEMENT_TYPE_CODE", "");
            String productId = packageData.getString("PRODUCT_ID");
            
            // 获取该元素属于哪个子产品，预留字段1表示产品ID
            IData results = UProductElementInfoQry.queryElementInfoByProductIdAndPackageIdAndElementId(productId, packageData.getString("PACKAGE_ID"), packageData.getString("ELEMENT_ID"), elementType, null);
            if (results == null || results.size() == 0)
            {
                continue;
            }
            // 自己维护，不到产商品取了
            String productIdB = "ALL";
            IDataset powerParams = AttrBizInfoQry.getBizAttr(packageData.getString("PACKAGE_ID"), elementType, packageData.getString("ELEMENT_ID"), "Power100", null);
            if (IDataUtil.isNotEmpty(powerParams))
            {
                productIdB = powerParams.getData(0).getString("ATTR_VALUE");
            }
            if (productIdB == null || productIdB.equals(""))
            {
                continue;
            }
            packageData.put("PRODUCT_ID_B", productIdB);
        }
        for (int i = 0; i < userInfos.size(); i++)
        {
            IData userInfo = userInfos.getData(i);
            String userId = userInfo.getString("USER_ID");
            String state = userInfo.getString("STATE");
            String productIdA = userInfo.getString("PRODUCT_ID");

            for (int j = 0; j < grpPackage.size(); j++)
            {
                IData packageData = grpPackage.getData(j);
                String productIdB = packageData.getString("PRODUCT_ID_B", "");
                String elementType = packageData.getString("ELEMENT_TYPE_CODE", "");
                String pkState = packageData.getString("MODIFY_TAG", "EXIST");

                if (state.equals("DEL") && (productIdA.equals(productIdB) || productIdB.equals("ALL")))
                {
                    if ("S".equals(elementType))
                    { // 服务
                        processSvcElement(packageData, userId, pkState, svcDataset, paramDataset);
                    }
                    else if ("D".equals(elementType))
                    { // 优惠
                        processDiscntElement(packageData, userId, pkState, powerDctDataset, paramDataset);
                    }
                }
                else if (state.equals("ADD") && (productIdA.equals(productIdB) || productIdB.equals("ALL")) && !TRADE_MODIFY_TAG.DEL.getValue().equals(pkState))
                {
                    // 查询该用户是否本月终止过动力100业务
                    IDataset tmpSet = RelaUUInfoQry.getUserLastRelation(userId, reqData.getUca().getProductId());
                    if (!IDataUtil.isEmpty(tmpSet))
                    {
                        packageData.put("START_DATE", SysDateMgr.getEnableDate(SysDateMgr.getFirstDayOfNextMonth()));
                    }
                    else
                    {
                        packageData.put("START_DATE", SysDateMgr.getEnableDate(SysDateMgr.getSysDate()));
                    }
                    if ("S".equals(elementType))
                    { // 服务
                        processSvcElement(packageData, userId, pkState, svcDataset, paramDataset);
                    }
                    else if ("D".equals(elementType))
                    { // 优惠
                        processDiscntElement(packageData, userId, pkState, powerDctDataset, paramDataset);
                    }
                }
                else if (state.equals("EXISTS") && (TRADE_MODIFY_TAG.DEL.getValue().equals(pkState) || TRADE_MODIFY_TAG.Add.getValue().equals(pkState)) && (productIdA.equals(productIdB) || productIdB.equals("ALL")))
                {
                    if (TRADE_MODIFY_TAG.DEL.getValue().equals(pkState))
                    {
                        packageData.put("END_DATE", SysDateMgr.getEndDate(SysDateMgr.getLastDateThisMonth()));
                    }
                    else if (TRADE_MODIFY_TAG.Add.getValue().equals(pkState))
                    {
                        packageData.put("START_DATE", SysDateMgr.getEnableDate(SysDateMgr.getFirstDayOfNextMonth()));
                    }
                    if ("S".equals(elementType))
                    { // 服务
                        processSvcElement(packageData, userId, pkState, svcDataset, paramDataset);
                    }
                    else if ("D".equals(elementType))
                    { // 优惠
                        processDiscntElement(packageData, userId, pkState, powerDctDataset, paramDataset);
                    }
                }
            }
        }
        dctDataset.clear();
        dctDataset.addAll(powerDctDataset);

    }

    /**
     * 建立子产品和动力100的uu关系
     * 
     * @throws Exception
     */
    public void infoRegDataRelation() throws Exception
    {
        IDataset dataset = new DatasetList();
        for (int i = 0; i < userInfos.size(); i++)
        {
            IData userInfo = userInfos.getData(i);

            if ("ADD".equals(userInfo.getString("STATE")))
            {
                String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()); // 关系类型
                IDataset uus = RelaUUInfoQry.qryUUForGrp(reqData.getUca().getUserId(), userInfo.getString("USER_ID"), relationTypeCode, null);
                if (IDataUtil.isNotEmpty(uus))
                {
                    continue;
                }
                IData map = new DataMap();
                map.put("USER_ID_A", reqData.getUca().getUserId());
                map.put("SERIAL_NUMBER_A", reqData.getUca().getSerialNumber());
                map.put("USER_ID_B", userInfo.getString("USER_ID"));
                map.put("SERIAL_NUMBER_B", userInfo.getString("SERIAL_NUMBER"));
                map.put("RELATION_TYPE_CODE", relationTypeCode);
                map.put("ROLE_CODE_A", "0");
                map.put("ROLE_CODE_B", "0");

                map.put("INST_ID", SeqMgr.getInstId());
                map.put("START_DATE", getAcceptTime()); // 开始时间
                map.put("END_DATE", SysDateMgr.getTheLastTime()); // 结束时间
                map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());

                dataset.add(map);
            }
            else if ("DEL".equals(userInfo.getString("STATE")))
            {
                String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getUca().getProductId()); // 关系类型
                IDataset uus = RelaUUInfoQry.qryUUForGrp(reqData.getUca().getUserId(), userInfo.getString("USER_ID"), relationTypeCode, null);
                if (IDataUtil.isEmpty(uus))
                {
                    continue;
                }

                IData uuInfo = uus.getData(0);
                uuInfo.put("STATE", "DEL");
                uuInfo.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                uuInfo.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                dataset.add(uuInfo);
            }
        }

        addTradeRelation(dataset);
    }

    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (ChangePower100UserElementReqData) getBaseReqData();
    }

    @Override
    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        reqData.setPower100Infos(map.getDataset("POWER100_PRODUCT_INFO"));
    }

    @Override
    public void makReqDataElement() throws Exception
    {

        // 二维结构
        IDataset elementInfos = moduleData.getElementInfo();

        IDataset dctDataset = new DatasetList(); // 优惠信息

        IDataset paramDataset = new DatasetList(); // （服务、优惠）个性化参数信息

        IData productIdset = new DataMap();// 产品

        // 查询选择产品的所有服务的itema配置
        IData elementparma = new DataMap();
        elementparma.put("PRODUCT_ID", reqData.getUca().getProductId());
        elementparma.put("ELEMENT_TYPE_CODE", "S");// 表示为服务
        elementparma.put("ATTR_TYPE_CODE", "9");// 表示为弹出窗口 显示元素参数

        IDataset productsvcItema = AttrItemInfoQry.getelementItemaByProductId("S", "9", reqData.getUca().getProductId(), null);

        // 主产品
        productIdset.put(reqData.getUca().getProductId(), GroupBaseConst.PRODUCT_MODE.USER_MAIN_PRODUCT.getValue());

        for (int i = 0, size = elementInfos.size(); i < size; i++)
        {
            // 取每个元素
            IData packageData = elementInfos.getData(i);

            // 元素类型
            String elementType = packageData.getString("ELEMENT_TYPE_CODE");

            // 产品id
            String productId = packageData.getString("PRODUCT_ID");

            // 查询产品信息
            IData productInfo = UProductInfoQry.qryProductByPK(productId);

            // 产品类型
            String productMode = productInfo.getString("PRODUCT_MODE");

            // 实例id
            String instId = SeqMgr.getInstId();

            // 其他产品
            productIdset.put(productId, productMode);

            if ("D".equals(elementType) && packageData.getString("MODIFY_TAG").equals(TRADE_MODIFY_TAG.Add.getValue())) // 优惠
            {
                String startDate = SysDateMgr.getEnableDate(packageData.getString("START_DATE"));
                String endDate = SysDateMgr.getEndDate(packageData.getString("END_DATE"));

                packageData.put("START_DATE", startDate);
                packageData.put("END_DATE", endDate);

                // 优惠参数
                IDataset dstParam = IDataUtil.getDataset(packageData, "ATTR_PARAM");

                if (IDataUtil.isNotEmpty(dstParam))
                {
                    for (int m = 0, dsize = dstParam.size(); m < dsize; m++)
                    {
                        IData param = dstParam.getData(m);

                        param.put("INST_TYPE", "D");
                        param.put("INST_ID", instId);
                        param.put("START_DATE", packageData.getString("START_DATE"));
                        param.put("END_DATE", packageData.getString("END_DATE"));
                        param.put("MODIFY_TAG", packageData.getString("MODIFY_TAG"));
                    }

                    paramDataset.addAll(dstParam); // 优惠个性化参数
                }
            }
            dctDataset.add(packageData); // 优惠信息
        }

        reqData.cd.putDiscnt(dctDataset);
        reqData.cd.putElementParam(paramDataset);

        // 资源
        GroupModuleParserBean.grpRes(reqData, moduleData);

        // 处理产品和产品参数
        makReqDataProductParam();

        // 处理集团定制
        makReqDataGrpPackage();

    }

    /**
     * 处理优惠元素
     * 
     * @throws Exception
     */
    public void processDiscntElement(IData packageData, String userId, String pkState, IDataset powerDctDataset, IDataset paramDataset) throws Exception
    {
        IData map = new DataMap();
        map.putAll(packageData);
        String instId = "";
        if (TRADE_MODIFY_TAG.Add.getValue().equals(pkState) || TRADE_MODIFY_TAG.EXIST.getValue().equals(pkState))
        { // 折扣状态不变，将其加入到新入包的子产品上
            instId = SeqMgr.getInstId();
            map.put("INST_ID", instId);
            map.put("USER_ID", userId);
            map.put("USER_ID_A", reqData.getUca().getUserId());
            map.put("DISCNT_CODE", packageData.get("ELEMENT_ID"));
            map.put("STATE", "ADD");
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            powerDctDataset.add(map); // 优惠信息
        }
        else
        {
            // 查询原有折扣
            IDataset discnts = DiscntInfoQry.getUserSingleProductDisForGrp(userId, reqData.getUca().getUserId(), map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), map.getString("ELEMENT_ID"), null, null);
            if (!IDataUtil.isEmpty(discnts))
            {
                IData discnt = discnts.getData(0);
                instId = discnt.getString("INST_ID");
                map.put("START_DATE", SysDateMgr.getEnableDate(discnt.getString("START_DATE")));

                if (TRADE_MODIFY_TAG.MODI.getValue().equals(pkState))
                {
                    map.put("END_DATE", SysDateMgr.getEndDate(discnt.getString("END_DATE")));
                }
                else if (TRADE_MODIFY_TAG.DEL.getValue().equals(pkState))
                {
                    discnt.put("INST_ID", instId);
                    discnt.put("STATE", "DEL");
                    discnt.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    discnt.put("ELEMENT_ID", discnt.getString("DISCNT_CODE"));
                    discnt.put("END_DATE", map.getString("END_DATE"));
                    powerDctDataset.add(discnt);
                }
            }
        }
        IDataset dstParam = IDataUtil.getDataset(packageData, "DST_PARAM");
        if (!IDataUtil.isEmpty(dstParam))
        {
            for (int k = 0; k < dstParam.size(); k++)
            {
                IData param = dstParam.getData(k);
                param.put("INST_TYPE", "D");
                param.put("INST_ID", instId);
                param.put("USER_ID", userId);

                String str = packageData.getString("STATE");
                if ("EXIST".equals(str))
                {
                    continue;
                }
                param.put("STATE", str);
                param.put("START_DATE", SysDateMgr.getEnableDate(map.getString("START_DATE")));
                param.put("END_DATE", SysDateMgr.getEndDate(map.getString("END_DATE")));

                paramDataset.add(param); // 优惠个性化参数
            }
        }
    }

    /**
     * 处理服务元素
     * 
     * @throws Exception
     */
    public void processSvcElement(IData packageData, String userId, String pkState, IDataset svcDataset, IDataset paramDataset) throws Exception
    {
        String instId = "";
        IData map = new DataMap();
        map.putAll(packageData);
        if (TRADE_MODIFY_TAG.Add.getValue().equals(pkState))
        {
            instId = SeqMgr.getInstId();
            map.put("INST_ID", instId);
            map.put("USER_ID", userId);
            map.put("USER_ID_A", reqData.getUca().getUserId());
            map.put("DISCNT_CODE", packageData.get("ELEMENT_ID"));
            map.put("STATE", "ADD");
            map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
            svcDataset.add(map); // 服务信息
        }
        else
        {
            // 查询原有服务
            IDataset svcs = UserSvcInfoQry.getUserSingleProductSvc(userId, reqData.getUca().getUserId(), map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), map.getString("ELEMENT_ID"), null, null);
            if (!IDataUtil.isEmpty(svcs))
            {
                IData svc = svcs.getData(0);
                instId = svc.getString("INST_ID");
                map.put("START_DATE", SysDateMgr.getEnableDate(svc.getString("START_DATE")));
                if (TRADE_MODIFY_TAG.MODI.getValue().equals(pkState))
                { // 仅修改服务参数
                    map.put("END_DATE", SysDateMgr.getEndDate(svc.getString("END_DATE")));
                }
                else if (TRADE_MODIFY_TAG.DEL.getValue().equals(pkState))
                { // 删除服务
                    svc.put("STATE", "DEL");
                    svc.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    svc.put("END_DATE", getCancelDate(svc.getString("START_DATE"), map.getString("END_DATE")));
                    svc.put("ELEMENT_ID", svc.getString("SERVICE_ID"));
                    svcDataset.add(svc);
                }
            }
        }
        IDataset servParam = IDataUtil.getDataset(packageData, "SERV_PARAM");
        if (servParam != null && servParam.size() == 0)
        {
            for (int k = 0; k < servParam.size(); k++)
            {
                IData param = servParam.getData(k);
                param.put("INST_TYPE", "S");
                param.put("INST_ID", instId);
                param.put("USER_ID", userId);

                String str = packageData.getString("STATE");
                if (str.equals("EXIST"))
                {
                    continue;
                }

                param.put("STATE", str);
                param.put("START_DATE", SysDateMgr.getEnableDate(map.getString("START_DATE")));
                param.put("END_DATE", SysDateMgr.getEndDate(map.getString("END_DATE")));

                paramDataset.add(param); // 服务个性化参数
            }
        }
    }
}
