
package com.asiainfo.veris.crm.order.web.frame.csview.group.common.base;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.FeeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.product.productmebinfo.ProductMebInfoIntfViewUtil;

public abstract class GroupBasePage extends CSBasePage
{
    protected String productId;

    /**
     * 作用：必选优惠检查用(成员批量前台校验)
     * 
     * @author luojh 2009-09-10 17:25
     * @param cycle
     * @throws Exception
     */
    public void checkMustDiscnt(IRequestCycle cycle) throws Exception
    {

        String memBaseProductId = "";
        String checkresult = "";
        String selectedelements = getData().getString("SELECTED_ELEMENTS");
        String grpproductid = getParameter("GRPPRODUCTID");
        IDataset selectedelementDataset = new DatasetList(selectedelements);

        IDataset memProductLists = ProductMebInfoIntfViewUtil.qryProductMebInfosByProductId(this, grpproductid);
        for (int row = 0; row < memProductLists.size(); row++)
        {
            IData memProduct = (IData) memProductLists.get(row);

            memBaseProductId = memProduct.getString("PRODUCT_ID_B");

            IData data = new DataMap();
            data.put("PRODUCT_ID", memBaseProductId);
            data.put("EPARCHY_CODE", getTradeEparchyCode());

            // 获取产品下的必选包
            IDataset packageSet = CSViewCall.call(this, "CS.ProductPkgInfoQrySVC.getPackageByProId", data);
            if (packageSet != null && packageSet.size() != 0)
            {

                for (int i = 0; i < packageSet.size(); i++)
                {
                    boolean isfound = false;
                    IData pack = packageSet.getData(i);
                    for (int j = 0; j < selectedelementDataset.size(); j++)
                    {
                        IData source = selectedelementDataset.getData(j);
                        // 判断必选包是否已选择
                        if (pack.getString("PACKAGE_ID").equals(source.getString("PACKAGE_ID")))
                        {
                            isfound = true;
                            break;
                        }
                    }
                    if (isfound == false)
                    {
                        String packageId = pack.getString("PACKAGE_ID");
                        IData params = new DataMap();
                        params.put("PACKAGE_ID", packageId);
                        IDataset dataSet = CSViewCall.call(this, "CS.PkgInfoQrySVC.getPackageNameByPackageId", params);
                        String packageName = dataSet.getData(0).getString("PACKAGE_NAME");
                        checkresult = "请选择必选包元素：" + packageName + " [" + packageId + "]";
                    }
                }
            }

            if (!"".equals(checkresult))
            {
                break;
            }
        }

        setAjax("checkresult", checkresult);
    }

    /**
     * 从元素列表selectedElementList获取新增的服务和资费信息
     * 
     * @param selectedElementList
     * @return
     * @throws Exception
     */
    public IDataset getAddServiceDiscntList(IDataset selectedElementList) throws Exception
    {
        IDataset addElementList = new DatasetList();

        if (IDataUtil.isEmpty(selectedElementList))
        {
            return addElementList;
        }

        for (int i = 0, size = selectedElementList.size(); i < size; i++)
        {
            IData elementData = selectedElementList.getData(i);
            if (("S".equals(elementData.getString("ELEMENT_TYPE_CODE", "")) || "D".equals(elementData.getString("ELEMENT_TYPE_CODE", ""))) && "0".equals(elementData.getString("MODIFY_TAG", "")))
            {
                addElementList.add(elementData);
            }
        }

        return addElementList;
    }

    /**
     * 费用总计
     * 
     * @description
     * @author 廖翊
     * @date 2010-12-03
     * @version 1.0.0
     * @param ctx
     * @throws Exception
     */
    public String getFeeTotal() throws Exception
    {

        double dFeeTotal = 0.0;
        return String.valueOf(dFeeTotal);
    }

    /*
     * 获取页面上的集团业务发展人信息 @description @author 廖翊 @date 2012-03-28
     * @version 1.0.0 @param staffID @throws Exception
     */
    public IDataset getPageDevelopInfo(String staffID) throws Exception
    {

        IDataset developLists = new DatasetList();

        if ((null == staffID) || ("".equals(staffID)))
            return developLists;

        if ("NO".equals(staffID))
            return developLists;

        IData param = new DataMap();
        param.put("STAFF_ID", staffID);
        IDataset ids = null;// hy j2ee callDaoQryByCode("TD_M_STAFF", "SEL_ALL_BY_PK", param, ConnMgr.CONN_CRM_CEN);
        if ((ids != null) || (ids.size() > 0))
        {
            IData developInfo = new DataMap();

            developInfo.put("DEVELOP_STAFF_ID", ids.getData(0).getString("STAFF_ID", ""));
            developInfo.put("DEVELOP_STAFF_NAME", ids.getData(0).getString("STAFF_NAME", ""));

            developInfo.put("DEVELOP_DEPART_ID", ids.getData(0).getString("DEPART_ID", ""));
            developInfo.put("DEVELOP_CITY_CODE", ids.getData(0).getString("CITY_CODE", ""));
            developInfo.put("DEVELOP_EPARCHY_CODE", ids.getData(0).getString("EPARCHY_CODE", ""));

            developLists.add(developInfo);
            return developLists;

        }

        return developLists;
    }

    /**
     * @return productId
     */
    public String getProductId()
    {

        return productId;
    }

    public IDataset getTradeOperFee4Hi(IRequestCycle cycle, String productId, String tradeTypeCode, String eparchy_code) throws Exception
    {

        IData param = new DataMap();

        param.putAll(getData());

        param.put("PRODUCT_ID", productId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("EPARCHY_CODE", eparchy_code);

        IDataset tradeFeeList = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.qryTradeOperFee4Hi", param);

        return tradeFeeList;
    }

    /**
     * 初始化默认费用
     * 
     * @param tradeTypeCode
     *            业务类型编码
     * @param selectedElementList
     *            元素列表
     * @return
     * @throws Exception
     */
    public IDataset initDefaultFee(String productId, String tradeTypeCode, String tradeFeeType, IDataset selectedElementList) throws Exception
    {
        IDataset returnFeeList = new DatasetList();

        IData param = new DataMap();
        param.put("PRODUCT_ID", productId);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("TRADE_FEE_TYPE", tradeFeeType);
        param.put("EPARCHY_CODE", getTradeEparchyCode());

        if ("0".equals(tradeFeeType)) // 处理产品收费
        {
            param.put("ELEMENT_TYPE_CODE", "P");
            param.put("PACKAGE_ID", "-1");
            param.put("ELEMENT_ID", "-1");
        }
        else if ("4".equals(tradeFeeType)) // 处理元素收费
        {
            // 获取新增的服务或资费
            IDataset addServiceDiscntList = getAddServiceDiscntList(selectedElementList);
            param.put("SELECTED_ELEMENTS", addServiceDiscntList);
        }

        IDataset tradeFeeList = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.qryTradeTypeFeeForGrp", param);

        if (IDataUtil.isEmpty(tradeFeeList))
            return returnFeeList;

        // 遍历费用
        for (int j = 0, jRow = tradeFeeList.size(); j < jRow; j++)
        {
            IData tradeFeeData = tradeFeeList.getData(j);

            if (IDataUtil.isEmpty(tradeFeeData))
            {
                continue;
            }
            String ruleBizTypeCode = tradeFeeData.getString("RULE_BIZ_KIND_CODE", "-1");

            if ("-1".equals(ruleBizTypeCode))
            {
                IData feeData = new DataMap();
                feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
                feeData.put("ELEMENT_ID", tradeFeeData.getString("ELEMENT_ID"));
                feeData.put("FEE", tradeFeeData.getString("FEE", "0"));
                feeData.put("FEE_MODE", tradeFeeData.getString("FEE_MODE", ""));
                feeData.put("FEE_TYPE_CODE", tradeFeeData.getString("FEE_TYPE_CODE", ""));
                feeData.put("PAY_MODE", tradeFeeData.getString("PAY_MODE", ""));
                feeData.put("IN_DEPOSIT_CODE", tradeFeeData.getString("IN_DEPOSIT_CODE", ""));
                feeData.put("OUT_DEPOSIT_CODE", tradeFeeData.getString("OUT_DEPOSIT_CODE", ""));

                // 处理费用信息
                setGroupFeeList(feeData, returnFeeList);
            }
        }

        return returnFeeList;
    }

    /**
     * 初始化元素费用信息
     * 
     * @param productId
     * @param tradeTypeCode
     * @param elementList
     * @return
     * @throws Exception
     */
    public IDataset initElementDefaultFee(String productId, String tradeTypeCode, IDataset elementList) throws Exception
    {
        IDataset feeList = initDefaultFee(productId, tradeTypeCode, "4", elementList);

        return feeList;
    }

    /**
     * 初始化产品费用信息
     * 
     * @param productId
     * @param tradeTypeCode
     * @param tradeFeeType
     * @return
     * @throws Exception
     */
    public IDataset initProductDefaultFee(String productId, String tradeTypeCode) throws Exception
    {
        IDataset feeList = initDefaultFee(productId, tradeTypeCode, "0", null);

        return feeList;
    }

    /**
     * 初始化费用信息
     * 
     * @param tradeTypeCode
     *            业务类型编码
     * @param selectedElementList
     *            元素列表
     * @param userId
     *            用户ID
     * @return
     * @throws Exception
     */
    public IDataset initPyaFee(String productId, String tradeTypeCode, IDataset selectedElementList, String userId) throws Exception
    {
        IDataset retFeeList = new DatasetList();

        // 获取新增的服务或资费
        IDataset addServiceDiscntList = getAddServiceDiscntList(selectedElementList);

        IData param = new DataMap();
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("SELECTED_ELEMENTS", addServiceDiscntList);
        param.put("PRODUCT_ID", productId);

        IDataset tradeFeeList = CSViewCall.call(this, "CS.ProductFeeInfoQrySVC.qryTradeTypeFeeForGrp", param);

        if (IDataUtil.isEmpty(tradeFeeList))
            return retFeeList;

        // 遍历费用
        for (int i = 0, row = tradeFeeList.size(); i < row; i++)
        {
            IData tradeFeeData = tradeFeeList.getData(i);

            if (IDataUtil.isEmpty(tradeFeeData))
            {
                continue;
            }

            // 检查是否欠费, 默认为0
            String checkOweFeeTag = tradeFeeData.getString("RSRV_STR1", "0");

            if (!"0".equals(checkOweFeeTag))
            {
                IData oweParam = new DataMap();
                oweParam.put("USER_ID", userId);
                oweParam.put(Route.ROUTE_EPARCHY_CODE, Route.CONN_CRM_CG);

                // @return 欠费信息，LAST_OWE_FEE:往月欠费 REAL_FEE:实时欠费 ACCT_BALANCE:实时结余
                IData oweFeeData = CSViewCall.callone(this, "CS.UserOwenInfoQrySVC.getOweFeeByUserId", oweParam);

                double lastOweFee = Double.valueOf(oweFeeData.getString("LAST_OWE_FEE"));
                double relaFee = Double.valueOf(oweFeeData.getString("REAL_FEE"));
                double acctBalance = Double.valueOf(oweFeeData.getString("ACCT_BALANCE"));

                if (lastOweFee < 0)
                {
                    CSViewException.apperr(FeeException.CRM_FEE_46, String.format("%1$3.2f", lastOweFee / 100.0));
                }

                if (relaFee < 0)
                {
                    CSViewException.apperr(FeeException.CRM_FEE_24, String.format("%1$3.2f", relaFee / 100.0));
                }

                if (acctBalance < 0)
                {
                    CSViewException.apperr(FeeException.CRM_FEE_129, String.format("%1$3.2f", acctBalance / 100.0));
                }
            }

            IData feeData = new DataMap();
            feeData.put("TRADE_TYPE_CODE", tradeTypeCode);
            feeData.put("FEE", tradeFeeData.getString("FEE", "0"));
            feeData.put("FEE_MODE", tradeFeeData.getString("FEE_MODE", ""));
            feeData.put("FEE_TYPE_CODE", tradeFeeData.getString("FEE_TYPE_CODE", ""));
            feeData.put("PAY_MODE", tradeFeeData.getString("PAY_MODE", ""));
            feeData.put("IN_DEPOSIT_CODE", tradeFeeData.getString("IN_DEPOSIT_CODE", ""));
            feeData.put("OUT_DEPOSIT_CODE", tradeFeeData.getString("OUT_DEPOSIT_CODE", ""));

            String ruleBizTypeCode = tradeFeeData.getString("RULE_BIZ_KIND_CODE", "-1");

            if ("-1".equals(ruleBizTypeCode))
            {
                // 处理费用信息
                setGroupFeeList(feeData, retFeeList);
            }
            else
            {
                int payFee = 0;

                param.clear();
                param.put("USER_ID", userId);
                IData payFeeData = CSViewCall.callone(this, "CS.UserOtherInfoQrySVC.qrySumRsrvByUserId", param);
                if (IDataUtil.isNotEmpty(payFeeData))
                {
                    payFee = payFeeData.getInt("FEE", 0);
                }

                int fee = tradeFeeData.getInt("FEE", 0);

                // 如果预存的小于设置值
                if (fee > payFee)
                {
                    feeData.put("FEE", String.valueOf(fee - payFee));
                    CSViewException.apperr(FeeException.CRM_FEE_48, String.format("%1$3.2f", payFee / 100.0));
                }
                else
                {
                    feeData.put("FEE", "0");
                }
                setGroupFeeList(feeData, retFeeList);
            }
        }

        return retFeeList;
    }

    /**
     * 根据集团编码查询集团客户相关信息
     * 
     * @author zhujm 2009-03-06
     * @throws Throwable
     */
    public IData queryGroupCustInfo(IRequestCycle cycle) throws Throwable
    {
        IData conParams = getData("cond", true);

        String groupId = conParams.getString("GROUP_ID");
        String custId = conParams.getString("CUST_ID");
        IData custInfo = null;
        if (StringUtils.isNotEmpty(custId))
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
        else
            custInfo = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);

        return custInfo;
    }

    /**
     * 将费用信息inFeeData添加到费用列表feeList中
     * 
     * @param inFeeData
     * @param feeList
     * @throws Exception
     */
    private void setGroupFeeList(IData inFeeData, IDataset feeList) throws Exception
    {
        String inTradeTypeCode = inFeeData.getString("TRADE_TYPE_CODE"); // 业务类型
        String inElementId = inFeeData.getString("ELEMENT_ID"); // 元素ID
        String inFeeMode = inFeeData.getString("FEE_MODE");
        String inFeeTypeCode = inFeeData.getString("FEE_TYPE_CODE");// 157ADCMAS彩短信预存TD_B_PAYMENT表
        String inFactPayFee = inFeeData.getString("FEE", "0");// 应缴金额
        String inFee = inFeeData.getString("FEE", "0");// 实缴金额

        boolean isExist = false;// 费用项是否存在

        for (int i = 0, row = feeList.size(); i < row; i++)
        {
            IData feeData = feeList.getData(i);

            if (inFeeMode.equals(feeData.getString("FEE_MODE")) && inFeeTypeCode.equals(feeData.getString("FEE_TYPE_CODE")))
            {
                int fee = Integer.parseInt(inFee) + Integer.parseInt(feeData.getString("FEE", "0"));
                int factPayFee = Integer.parseInt(inFactPayFee) + Integer.parseInt(feeData.getString("FACT_PAY_FEE", "0"));

                feeData.put("FEE", String.valueOf(fee));
                feeData.put("FACT_PAY_FEE", String.valueOf(factPayFee));

                isExist = true;
            }
        }

        if (!isExist)
        {
            IData feeData = new DataMap();
            feeData.put("TRADE_TYPE_CODE", inTradeTypeCode);
            feeData.put("ELEMENT_ID", inElementId);
            feeData.put("FEE_MODE", inFeeMode);
            feeData.put("FEE_TYPE_CODE", inFeeTypeCode);
            feeData.put("FACT_PAY_FEE", inFactPayFee); // 应缴金额
            feeData.put("FEE", inFee); // 实缴金额
            feeList.add(feeData);
        }
    }

    /**
     * @param productId
     *            要设置的 productId
     */
    public void setProductId(String productId)
    {

        this.productId = productId;
    }

    public final IData setRetInfo(int code, String title, String info)
    {
        IData retInfo = new DataMap();

        retInfo.put("X_RESULTCODE", code);
        retInfo.put("X_RESULTTITLE", title);
        retInfo.put("X_RESULTINFO", info);

        return retInfo;
    }
}
