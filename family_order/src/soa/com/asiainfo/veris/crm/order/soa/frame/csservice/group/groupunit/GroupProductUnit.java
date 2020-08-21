
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupunit;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupProductUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GrpInvoker;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ProductUtil;

public class GroupProductUnit extends CSBizBean
{

    /**
     * 动力100 二期 资费转换
     */
    public static void DealDisChange(IData discnt, IData userInfo) throws Exception
    {

        IDataset datas = AttrBizInfoQry.getBizAttrByAttrValue(userInfo.getString("PRODUCT_ID"), "D", "DIS", discnt.getString("ELEMENT_ID"), null);
        if (datas != null && datas.size() > 0)
        {
            discnt.put("ELEMENT_ID", datas.getData(0).getString("ATTR_CODE"));
        }
    }

    /**
     * @param itemaDataset
     * @param element
     * @param data
     * @param tmpElement
     * @param flag
     * @throws Exception
     */
    public static void dealspecialServerParam(IDataset itemaDataset, IData element, IData data, IData tmpElement, boolean flag) throws Exception
    {

        String productId = (data != null) ? data.getString("PRODUCT_ID") : element.getString("PRODUCT_ID");
        IData svcCtrlInfo = GroupProductUtil.querySvcCtrlInfo(element.getString("ELEMENT_ID"), productId);// 取服务控制参数
        if (svcCtrlInfo == null || svcCtrlInfo.size() == 0)
        {
            if (flag)
            {
                tmpElement.put("PARAM_VERIFY_SUCC", "true");// 存放服务参数是否需要校验
            }
            else
            {
                tmpElement.put("PARAM_VERIFY_SUCC", "false");// 存放服务参数是否需要校验
            }

            IDataset specialservParam = new DatasetList();
            IData paramVerifySuccData = new DataMap();
            if (flag)
            {
                paramVerifySuccData.put("PARAM_VERIFY_SUCC", "true");
            }
            else
            {
                paramVerifySuccData.put("PARAM_VERIFY_SUCC", "false");
            }
            specialservParam.add(0, paramVerifySuccData);
            tmpElement.put("SERV_PARAM", specialservParam);// 存放自己查询出来的服务参数
        }
        else
        {
            IData serverinParam = new DataMap();
            serverinParam.put("SERVICE_ID", element.getString("ELEMENT_ID"));
            serverinParam.put("USER_ID", data.getString("USER_ID", ""));
            serverinParam.put("PRODUCT_ID", productId);
            serverinParam.put("PACKAGE_ID", element.getString("PACKAGE_ID"));

            String scvctrlclass = svcCtrlInfo.getData("PlatSvcInfoSrc").getString("ATTR_VALUE");
            IDataset specialservParam = (IDataset) GrpInvoker.invoker(scvctrlclass, "getServiceParam", new Object[]
            { serverinParam }, new Class[]
            { IData.class });

            IData paramVerifySucc = (IData) specialservParam.get(0);
            String paramVerifySuccflag = paramVerifySucc.getString("PARAM_VERIFY_SUCC", "");
            if ("true".equals(paramVerifySuccflag))
            {
                tmpElement.put("PARAM_VERIFY_SUCC", "true");// 存放服务参数是否需要校验
            }
            else
            {
                tmpElement.put("PARAM_VERIFY_SUCC", "false");// 存放服务参数是否需要校验
            }
            tmpElement.put("SERV_PARAM", specialservParam);// 存放自己查询出来的服务参数
        }
    }

    /**
     * 获取注销时失效时间
     * 
     * @param Date
     * @param Mode
     * @return
     */
    public static String getCancelDate(IData commData, String startDate, String endDate) throws Exception
    {

        if (endDate == null || endDate.length() < 10)
        {
            return null;
        }

        String strCancelDate = endDate.substring(0, 10);
        String sysDate = commData.getString("SYS_DATE").substring(0, 10);

        // 未生效时注销
        if (commData.getString("SYS_DATE").compareTo(startDate) < 0)
        {
            // 当前时间减一秒
            // getVisit().setRouteEparchyCode(commData.getString("EPARCHY_CODE"));
            return SysDateMgr.getLastSecond(commData.getString("SYS_DATE"));
        }
        // 当前时间
        if (strCancelDate.compareTo(sysDate) == 0)
        {
            return commData.getString("SYS_DATE");
        }
        // 非立即生效
        else
        {
            return strCancelDate + SysDateMgr.getEndTime235959();
        }
    }

    /**
     * 获取注销时失效时间
     * 
     * @param Date
     * @param Mode
     * @return
     */
    public static String getCancelDate_ChangePower100UserElement(String endDate) throws Exception
    {

        String strCancelDate = endDate.substring(0, 10);
        String sysDate = SysDateMgr.getSysTime().substring(0, 10);

        // 当前时间
        if (strCancelDate.compareTo(sysDate) == 0)
        {
            return SysDateMgr.getSysTime();
        }
        // 非立即生效
        else
        {
            return strCancelDate + SysDateMgr.getEndTime235959();
        }
    }

    /**
     * 用户订购时必选元素
     * 
     * @param baseProductId
     *            基本产品ID
     * @return 必须选择的元素
     * @throws Exception
     * @author xiajj
     */
    public static IDataset getCreateUserBaseElement(String baseProductId) throws Exception
    {

        IDataset result = new DatasetList();
        String START_DATE = SysDateMgr.getSysDate();
        // 查询选择产品的所有服务的itema配置
        IData elementparma = new DataMap();
        elementparma.put("PRODUCT_ID", baseProductId);
        elementparma.put("ELEMENT_TYPE_CODE", "S");// 表示为服务
        elementparma.put("ATTR_TYPE_CODE", "9");// 表示为弹出窗口 显示元素参数
        IDataset productsvcItema = AttrItemInfoQry.getelementItemaByProductId("S", "9", baseProductId, null);

        IDataset packages = getPackageByProduct(baseProductId, CSBizBean.getUserEparchyCode());
        for (int i = 0; i < packages.size(); i++)
        {
            IData tmp = (IData) packages.get(i);
            if (tmp.getString("FORCE_TAG").equals("1"))
            {
                IDataset servElement = PkgElemInfoQry.getServElementByPackageNoPriv(tmp);
                IDataset disElement = PkgElemInfoQry.getDiscntElementByPackage(tmp.getString("PACKAGE_ID"), tmp.getString("USER_ID"));
                IData data = new DataMap();
                data.put("PRODUCT_ID", baseProductId);
                IData product = UProductInfoQry.qryProductByPK(baseProductId);
                data.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                data.put("SERVICE_ITEMAS", productsvcItema);
                data.put("NEED_SERV_PARAM", "true");
                data.put("ELEMENT_INDEX", "1");
                disElement.addAll(servElement);
                for (int count = 0; count < disElement.size(); count++)
                {
                    IData element = disElement.getData(count);
                    setDefaultStartEndDate(element);
                    element.put("OLD_START_DATE", element.getString("START_DATE"));
                    element.put("START_DATE", START_DATE);
                    element.put("END_DATE", element.getString("END_DATE"));
                    element.put("OLD_END_DATE", element.get("END_DATE"));
                    element.put("ELEMENT_INDEX", "1");
                    if ("1".equals(element.getString("FORCE_TAG")) || "1".equals(element.getString("DEFAULT_TAG")))
                    {
                        IDataset elementParam = null;
                        boolean mustWriteParam = GroupProductUnit.getElementParam(element.getString("ELEMENT_ID"), element.getString("ELEMENT_TYPE_CODE"), "0", elementParam);
                        element.put("SERV_PARAM", elementParam);
                        element.put("PARAM_VERIFY_SUCC", mustWriteParam);
                        if (elementParam != null && elementParam.size() > 0)
                            element.put("HAS_SERV_PARAM", "true");
                    }
                }
                GroupProductUnit.operelementsdata(disElement, result, data, false);
            }
        }
        return result;
    }

    /**
     * 获取元素个性化参数 element 要处理的元素
     * 
     * @param id
     * @param idType
     * @param attrObj
     * @param attrItemSet
     * @author xiajj
     * @throws Exception
     */
    public static boolean getElementParam(String id, String idType, String attrObj, IDataset attrItemSet) throws Exception
    {

        IData data = new DataMap();
        data.put("ID", id);
        data.put("ID_TYPE", idType);
        data.put("ATTR_OBJ", attrObj);
        String eparchyCode = getVisit().getStaffEparchyCode();
        IDataset dataset = AttrItemInfoQry.getAttrItemAByIDTO(id, idType, attrObj, null, null);

        if (attrItemSet == null)
            attrItemSet = new DatasetList();
        attrItemSet.addAll(dataset);
        for (int row = 0; row < attrItemSet.size(); row++)
        {
            IData attrMap = attrItemSet.getData(row);
            String attrCanNull = attrMap.getString("ATTR_CAN_NULL", "");
            String attrInitValue = attrMap.getString("ATTR_INIT_VALUE", "");
            // String attrInitValue = attrMap.getString("", "ATTR_INIT_VALUE");

            if (attrCanNull.equals("0") && attrInitValue.equals(""))
            {
                return false;
            }
            else if (attrCanNull.equals("9") && attrInitValue.equals(""))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取用户订购时成员必选元素
     * 
     * @param memProductId
     * @param flag
     *            是否只展现基本产品的元素(true:只展现基本产品 false:所有产品) 成员资费定制 服务非定制
     * @author hudie
     * @return
     * @throws Exception
     */
    public static IDataset getMemberBaseElement(IDataset memProductList, boolean flag, IData result) throws Exception
    {

        IDataset memResult = new DatasetList();
        IDataset memPlusProduct = new DatasetList();// 成员附加产品

        for (int row = 0; row < memProductList.size(); row++)
        {
            IData memProduct = (IData) memProductList.get(row);
            IData product = UProductInfoQry.qryProductByPK(memProduct.getString("PRODUCT_ID_B"));
            if (memProduct.getString("FORCE_TAG").equals("1") && "".equals(result.getString("setMemBasePlusProduct", "")))
            {

                result.put("setMemBasePlusProduct", product.getString("PRODUCT_ID"));

                IDataset packages = getPackageByProduct(product.getString("PRODUCT_ID"), CSBizBean.getUserEparchyCode());

                for (int i = 0; i < packages.size(); i++)
                {
                    IData tmp = (IData) packages.get(i);
                    if (tmp.getString("FORCE_TAG").equals("1"))
                    {
                        IDataset servElement = PkgElemInfoQry.getServElementByPackageNoPriv(tmp);
                        IDataset disElement = PkgElemInfoQry.getDiscntElementByPackage(tmp.getString("PACKAGE_ID"), tmp.getString("USER_ID"));
                        IDataset spElement = ProductUtil.getPlatSvcElementByPackage(tmp.getString("PACKAGE_ID"), Route.CONN_CRM_CG);
                        product = UProductInfoQry.qryProductByPK(product.getString("PRODUCT_ID"));
                        IData data = new DataMap();
                        data.put("PRODUCT_MODE", product.getString("PRODUCT_ID"));
                        data.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        data.put("NEED_SERV_PARAM", "false");
                        data.put("ELEMENT_INDEX", "1");
                        disElement.addAll(servElement);
                        disElement.addAll(spElement);
                        for (int disCount = 0; disCount < disElement.size(); disCount++)
                        {
                            IData disData = disElement.getData(disCount);

                            IDataset elementParam = new DatasetList();
                            boolean mustWriteParam = GroupProductUnit.getElementParam(disData.getString("ELEMENT_ID"), disData.getString("ELEMENT_TYPE_CODE"), "1", elementParam);
                            disData.put("PARAM_VERIFY_SUCC", mustWriteParam);
                            setDefaultStartEndDate(disData);
                        }
                        GroupProductUnit.operelementsdata(disElement, memResult, data, false);
                    }
                }
            }
            else
            {
                if (!false)
                {
                    IData tmpData = new DataMap();
                    tmpData.put("PRODUCT_ID", memProduct.getString("PRODUCT_ID_B"));
                    memPlusProduct.add(tmpData);
                }
            }
        }
        result.put("setSelectedMemPlusProduct", memPlusProduct);
        result.put("setSelectedMemElements", memResult);
        return memResult;
    }

    /**
     * 获取用户订购时成员必选元素
     * 
     * @param memProductId
     * @param flag
     *            是否只展现基本产品的元素(true:只展现基本产品 false:所有产品) 成员资费定制 服务非定制
     * @author hudie
     * @return
     * @throws Exception
     */
    public static IDataset getMemberBaseElementForGrp(IDataset memProductList, boolean flag, IData result) throws Exception
    {

        IDataset memResult = new DatasetList();
        IDataset memPlusProduct = new DatasetList();// 成员附加产品

        for (int row = 0; row < memProductList.size(); row++)
        {
            IData memProduct = (IData) memProductList.get(row);
            IData data = new DataMap();
            IData product = UProductInfoQry.qryProductByPK(memProduct.getString("PRODUCT_ID_B"));

            if (memProduct.getString("FORCE_TAG").equals("1") && "".equals(result.getString("setMemBasePlusProduct", "")))
            {
                result.put("setMemBasePlusProduct", product.getString("PRODUCT_ID"));

                IDataset packages = getPackageByProduct(product.getString("PRODUCT_ID"), CSBizBean.getUserEparchyCode());

                for (int i = 0; i < packages.size(); i++)
                {
                    IData tmp = (IData) packages.get(i);
                    if (tmp.getString("FORCE_TAG").equals("1") && !"1".equals(tmp.getString("PACKAGE_TYPE_CODE")))
                    {
                        IDataset disElement = PkgElemInfoQry.getDiscntElementByPackage(tmp.getString("PACKAGE_ID"), tmp.getString("USER_ID"));
                        data = new DataMap();
                        data.put("PRODUCT_ID", product.getString("PRODUCT_ID"));
                        product = UProductInfoQry.qryProductByPK(product.getString("PRODUCT_ID"));
                        data.put("PRODUCT_MODE", product.getString("PRODUCT_MODE"));
                        data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                        data.put("NEED_SERV_PARAM", "false");
                        data.put("ELEMENT_INDEX", "1");
                        for (int disCount = 0; disCount < disElement.size(); disCount++)
                        {
                            IData disData = disElement.getData(disCount);

                            IDataset elementParam = new DatasetList();
                            boolean mustWriteParam = GroupProductUnit.getElementParam(disData.getString("ELEMENT_ID"), disData.getString("ELEMENT_TYPE_CODE"), "1", elementParam);
                            disData.put("PARAM_VERIFY_SUCC", mustWriteParam);
                            setDefaultStartEndDate(disData);
                        }
                        GroupProductUnit.operelementsdata(disElement, memResult, data, false);
                    }
                }
            }
            else
            {
                if (!false)
                {
                    IData tmpData = new DataMap();
                    tmpData.put("PRODUCT_ID", memProduct.getString("PRODUCT_ID_B"));
                    memPlusProduct.add(tmpData);
                }
            }
        }
        result.put("setSelectedMemPlusProduct", memPlusProduct);
        result.put("setSelectedMemElements", memResult);
        return memResult;
    }

    /**
     * 作用：在ProductUtil类里也有一个这样的类，但为了和海南分开，移到天津类面
     * 
     * @param productId
     * @param eparchyCode
     * @return
     * @throws Exception
     */
    public static IDataset getPackageByProduct(String productId, String eparchyCode) throws Exception
    {

        return ProductUtil.getPackageByProduct(productId, null, eparchyCode, false);
    }

    /**
     * 动力100 二期 反资费转换，由子产品的资费转换为DLBG的资费
     */
    public static String getPower100DisChange(String productId, String elementid) throws Exception
    {

        IDataset datas = AttrBizInfoQry.getBizAttrByDynamic(productId, "D", "DIS", elementid, null);
        if (datas != null && datas.size() > 0)
        {
            return datas.getData(0).getString("ATTR_VALUE");
        }
        else
        {
            return elementid;
        }
    }

    /**
     * 操作元素数据
     * 
     * @param elements
     * @param result
     * @param data
     * @param flag
     * @return
     * @throws Exception
     */
    public static IDataset operelementsdata(IDataset elements, IDataset result, IData data, boolean flag) throws Exception
    {

        IDataset servParam = null;
        String packageId = "";
        IDataset theElements = new DatasetList();
        IData thePackage = new DataMap();

        for (int i = 0; i < elements.size(); i++)
        {
            IData element = elements.getData(i);

            String productId = ("" != element.getString("PRODUCT_ID", "")) ? element.getString("PRODUCT_ID", "") : (data != null ? data.getString("PRODUCT_ID", "") : "");
            String productMode = ("" != element.getString("PRODUCT_MODE", "")) ? element.getString("PRODUCT_MODE", "") : (data != null ? data.getString("PRODUCT_MODE", "") : "");
            String state = (data != null) ? data.getString("MODIFY_TAG") : element.getString("MODIFY_TAG");
            IDataset serviceParam = (data != null) ? data.getDataset("SERVICE_ITEMAS") : element.getDataset("SERVICE_ITEMAS");
            String needServParam = (data != null) ? data.getString("NEED_SERV_PARAM") : element.getString("NEED_SERV_PARAM");
            String grpCustomize = (data != null) ? data.getString("GRP_CUSTOMIZE") : element.getString("GRP_CUSTOMIZE");
            String elementIndex = (data != null) ? data.getString("ELEMENT_INDEX") : "1";

            if (flag || (!flag && ("1".equals(element.getString("FORCE_TAG")) || "1".equals(element.getString("DEFAULT_TAG")))))
            {

                if (!packageId.equals(element.getString("PACKAGE_ID")))
                {
                    // 一个新的包的开始
                    thePackage = new DataMap();
                    theElements = new DatasetList();
                    packageId = element.getString("PACKAGE_ID");

                    result.add(thePackage);

                    thePackage.put("PRODUCT_ID", productId);
                    thePackage.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                    thePackage.put("PRODUCT_MODE", productMode);
                    thePackage.put("ELEMENTS", theElements);

                    // 包中的第一个元素
                    IData tmpElement = new DataMap();

                    tmpElement.put("PRODUCT_ID", productId);
                    tmpElement.put("ENABLE_TAG", element.getString("ENABLE_TAG"));
                    tmpElement.put("CANCEL_TAG", element.getString("CANCEL_TAG"));
                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                    tmpElement.put("INST_ID", element.getString("INST_ID", ""));
                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("OLD_START_DATE", element.getString("START_DATE"));
                    tmpElement.put("OLD_END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", productMode);
                    tmpElement.put("NEED_SERV_PARAM", needServParam);
                    tmpElement.put("GRP_CUSTOMIZE", grpCustomize);
                    tmpElement.put("ELEMENT_INDEX", elementIndex);
                    tmpElement.put("PARAM_VERIFY_SUCC", element.getString("PARAM_VERIFY_SUCC", ""));
                    if (flag)
                    {
                        tmpElement.put("MODIFY_TAG", "EXIST");
                    }
                    else
                    {
                        tmpElement.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    }

                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        // 为服务元素 需要加入SERV_PARAM节点
                        IDataset itemaDataset = serviceParam;// 取需要用弹出窗口显示数据源的服务元素
                        if (itemaDataset == null)
                            itemaDataset = new DatasetList();
                        if (IDataUtil.itemadatasetContainsId(itemaDataset, element.getString("ELEMENT_ID")))// 如果在弹出窗口显示的服务元素列表
                        // 则采用弹出窗口赋值的形式
                        {
                            dealspecialServerParam(itemaDataset, element, data, tmpElement, flag);

                        }
                        else if (!"".equals(element.getString("SERV_PARAM", "")))
                        {
                            tmpElement.put("SERV_PARAM", element.get("SERV_PARAM"));
                            // 转换单位 注释已删除
                        }
                        else
                        {
                            servParam = new DatasetList();
                            tmpElement.put("SERV_PARAM", servParam);
                        }
                    }
                    if ("D".equals(element.getString("ELEMENT_TYPE_CODE")) && !"".equals(element.getString("DISCNT_PARAM", "")))
                    {
                        tmpElement.put("DISCNT_PARAM", element.get("DISCNT_PARAM"));
                    }
                    theElements.add(tmpElement);
                }
                else
                {
                    // 在一个包内
                    // 不同的元素
                    IData tmpElement = new DataMap();
                    servParam = new DatasetList();
                    tmpElement.put("PRODUCT_ID", productId);
                    tmpElement.put("ENABLE_TAG", element.getString("ENABLE_TAG"));
                    tmpElement.put("CANCEL_TAG", element.getString("CANCEL_TAG"));
                    tmpElement.put("PACKAGE_ID", element.getString("PACKAGE_ID"));
                    tmpElement.put("INST_ID", element.getString("INST_ID", ""));
                    tmpElement.put("ELEMENT_ID", element.getString("ELEMENT_ID"));
                    tmpElement.put("ELEMENT_NAME", element.getString("ELEMENT_NAME"));
                    tmpElement.put("ELEMENT_TYPE_CODE", element.getString("ELEMENT_TYPE_CODE"));
                    tmpElement.put("START_DATE", element.getString("START_DATE"));
                    tmpElement.put("END_DATE", element.getString("END_DATE"));
                    tmpElement.put("OLD_START_DATE", element.getString("END_DATE"));
                    tmpElement.put("OLD_END_DATE", element.getString("END_DATE"));
                    tmpElement.put("PRODUCT_MODE", productMode);
                    tmpElement.put("MODIFY_TAG", state);
                    tmpElement.put("NEED_SERV_PARAM", needServParam);
                    tmpElement.put("GRP_CUSTOMIZE", grpCustomize);
                    tmpElement.put("ELEMENT_INDEX", elementIndex);
                    tmpElement.put("PARAM_VERIFY_SUCC", element.getString("PARAM_VERIFY_SUCC", ""));
                    if ("S".equals(element.getString("ELEMENT_TYPE_CODE")))
                    {
                        IDataset itemaDataset = serviceParam;
                        if (itemaDataset == null)
                            itemaDataset = new DatasetList();
                        if (IDataUtil.itemadatasetContainsId(itemaDataset, element.getString("ELEMENT_ID")))// 如果在弹出窗口显示的服务元素列表
                        // 则采用弹出窗口赋值的形式
                        {
                            dealspecialServerParam(itemaDataset, element, data, tmpElement, flag);

                        }
                        else if (!"".equals(element.getString("SERV_PARAM", "")))
                        {
                            tmpElement.put("SERV_PARAM", element.get("SERV_PARAM"));
                        }
                        else
                        {
                            servParam = new DatasetList();
                            tmpElement.put("SERV_PARAM", servParam);
                        }
                    }
                    if ("D".equals(element.getString("ELEMENT_TYPE_CODE")) && !"".equals(element.getString("DISCNT_PARAM", "")))
                    {
                        tmpElement.put("DISCNT_PARAM", element.get("DISCNT_PARAM"));
                    }
                    theElements.add(tmpElement);
                }
            }
        }

        return result;
    }

    /**
     * 配置默认的开始结束时间 默认元素未被选择 适用与个人业务
     * 
     * @param data
     * @throws Exception
     */
    public static void setDefaultStartEndDate(IData data) throws Exception
    {

        String start = null;// PackageModuleBean.getStartDate(data);
        data.put("START_DATE", start);
        data.put("END_DATE", SysDateMgr.getEndDate(start));
    }
}
