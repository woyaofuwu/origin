
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util;

import java.util.Iterator;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UAttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductTypeInfoQry;

public class GroupProductUtil extends CSBizBean
{
    /**
     * 作用：判断是否端到端过来的工单，调用端到端接口
     * 
     * @param tradeId
     *            工单号
     * @param businessId
     *            端到端标识，只有业务申请时传端到端，完工及其它操作不传。
     * @param eosOperCode
     *            0-申请,1-完工, 2-管理界面
     * @param rsrvStr9
     *            主台账的RSRV_STR9字段
     * @param resultCode
     *            0-成功 其 它-失败 2-退回需修改
     * @param resultInfo
     *            可为空
     * @throws Exception
     */
    public static void callEOSIntf(String tradeId, String rsrvStr9, String eosOperCode, String businessId, String resultCode, String resultInfo) throws Exception
    {

        if (!"EOS".equals(rsrvStr9))
            return;
        IData param = new DataMap();
        param.put("OPER_CODE", eosOperCode);
        param.put("X_RESULTINFO", eosOperCode);
        param.put("X_RESULTCODE", eosOperCode);
        param.put("TRADE_ID", tradeId);
        param.put("BUSINESS_ID", businessId);
        param.put("ORIGDOMAIN", "CRM");

        IData data = CSAppCall.callHttp("ITF_EOS_CRMPRODUCT", param);

        if (!data.getString("X_RESULTCODE", "").equals("0"))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, data.getString("X_RESULTCODE", ""), "ITF_EOS_CRMPRODUCT接口调用出错:", data.getString("X_RESULTINFO", ""));
        }
    }

    /**
     * 根据产品类型过滤产品列表
     * 
     * @param productList
     *            产品列表
     * @param productTypeCode
     *            产品类型
     * @throws Exception
     */
    public static void filterProductByProductTypeCode(IDataset productList, String productTypeCode) throws Exception
    {
        if (IDataUtil.isEmpty(productList))
        {
            return;
        }

        int row = productList.size();
        for (int i = row - 1; i >= 0; i--)
        {
            IData productData = productList.getData(i);
            String productId = productData.getString("PRODUCT_ID");

            boolean exiB = ProductTypeInfoQry.checkExisProductIdAndProductTypeCode(productId, productTypeCode);
            if (!exiB)
            {
                productList.remove(i);
                continue;
            }

            String productName = UProductInfoQry.getProductNameByProductId(productId);
            productData.put("PRODUCT_NAME", productName);
        }
    }

    public static String getClassId(String groupclassid, String classtype)
    {
        String result = "";
        if (groupclassid == null || groupclassid.equals(""))
        {
            // 从集团资料中获取不到信息group的classid信息时，默认级别c
            if (classtype.equals("1"))
            {
                result = "C";
                return result;
            }
            else if (classtype.equals("2"))
            {
                result = "0";
                return result;
            }
        }

        if (classtype.equals("1"))
        {
            // RSRV_TAG1
            // '5','A','6','A','7','B','8','B','9','C','10','D',
            if (groupclassid.equals("5") || groupclassid.equals("6"))
            {
                result = "A";
            }
            else if (groupclassid.equals("7") || groupclassid.equals("8"))
            {
                result = "B";
            }
            else if (groupclassid.equals("9"))
            {
                result = "C";
            }
            else if (groupclassid.equals("10"))
            {
                result = "D";
            }
            else
            {
                result = "C";
            }

        }
        else if (classtype.equals("2"))
        {
            // CREDIT_CONTROL_ID
            // '5','2','6','2','7','1','8','1','9','0','10','0'
            if (groupclassid.equals("5") || groupclassid.equals("6"))
            {
                result = "2";
            }
            else if (groupclassid.equals("7") || groupclassid.equals("8"))
            {
                result = "1";
            }
            else if (groupclassid.equals("9"))
            {
                result = "0";
            }
            else if (groupclassid.equals("10"))
            {
                result = "0";
            }
            else
            {
                result = "0";
            }

        }
        return result;
    }

    /**
     * 作用：得到集团彩铃优惠
     * 
     * @author wenjb 2010-12-12
     */
    public static String getColorDiscntName(IDataset discntLists) throws Exception
    {

        String discntScript = "";
        for (int i = 0; i < discntLists.size(); i++)
        {
            IData discnt = discntLists.getData(i);
            String sDscntCode = discnt.getString("ELEMENT_ID", "");
            String sStartDate = discnt.getString("START_DATE", "");
            if (!"".equals(sDscntCode))
            {
                IData inparams = new DataMap();
                inparams.put("DISCNT_CODE", sDscntCode);

                IDataset ids = StaticUtil.getList(getVisit(), "TD_B_DISCNT", "DISCNT_CODE", sDscntCode);
                if (null != ids && ids.size() > 0)
                {
                    String sDcnName = ids.getData(0).getString("DISCNT_NAME", "");
                    // String NextMonthFirstTime = SysDateMgr.getNextMonthFirstTime();
                    // String effect = (0 == sStartDate.compareTo(NextMonthFirstTime)) ? " 次月生效。" : " 本月生效。";
                    // discntScript = discntScript + sDcnName + effect;
                }
            }
        }
        return discntScript;
    }

    // /**
    // * 获取成员元素状态，为modify_tag 4和5而写。
    // *
    // * @param commData
    // * @param state
    // * @param elementType
    // * @param elementCode
    // * @return
    // * @throws Exception
    // */
    // public static String getElementState(IData iData) throws Exception
    // {
    //
    // String elementType = iData.getString("ELEMENT_TYPE", "");
    // String elementCode = iData.getString("SERVICE_ID", "");
    // String state = iData.getString("MODIFY_TAG", "");
    //
    // if (elementType.equals("S"))
    // {
    // String strProvince = getVisit().getProvinceCode();
    //
    // int size = UserSvcInfoQry.getUserSvcForModify45(iData);
    // if (strProvince.equals(PROVINCE.TJIN.getValue()))
    // {
    // if (state.equals(CSBaseConst.TRADE_MODIFY_TAG.Add.getValue()))
    // {
    // if (size == 0 | elementCode.equals("601") | elementCode.equals("606") | elementCode.equals("950") |
    // elementCode.equals("960"))
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.Add.getValue();// 2009-09-10 MODIFY by hjx
    // }
    // // 集团E网动态成员，MOA第二次成员新增需要发指令
    // else
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.ONLYADD.getValue();
    // }
    // }
    // else if (state.equals(CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue()))
    // {
    // if (size == 1 | elementCode.equals("601") | elementCode.equals("606") | elementCode.equals("950") |
    // elementCode.equals("960"))
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue();// 2009-09-11 MODIFY by hjx
    // }
    // // 集团E网动态成员，MOA第二次成员退订需要发指令
    // else
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.ONLYDEL.getValue();
    // }
    // }
    // }
    // else
    // {
    // if (state.equals(CSBaseConst.TRADE_MODIFY_TAG.Add.getValue()))
    // {
    // if (size == 0)
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.Add.getValue();
    // }
    // else
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.ONLYADD.getValue();
    // }
    // }
    // else if (state.equals(CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue()))
    // {
    // if (size == 1)
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.DEL.getValue();
    // }
    // else
    // {
    // return CSBaseConst.TRADE_MODIFY_TAG.ONLYDEL.getValue();
    // }
    // }
    // }
    // }
    //
    // return state;
    // }

    /**
     * 得到优惠名称
     * 
     * @return
     * @throws Exception
     */
    public static String getDiscntName(IDataset discntLists) throws Exception
    {

        String discntName = "";
        if (null != discntLists && discntLists.size() > 0)
        {
            for (int i = 0; i < discntLists.size(); i++)
            {
                String state = discntLists.getData(i).getString("MODIFY_TAG", "");
                if (CSBaseConst.TRADE_MODIFY_TAG.Add.getValue().equals(state))
                {
                    String elementName = discntLists.getData(i).getString("ELEMENT_NAME", "");
                    discntName += elementName + ".";
                }
            }
        }
        return discntName;
    }

    /**
     * 根据产品列表获取统计产品类别信息
     * 
     * @param productList
     *            产品列表
     * @return IDataset 产品类别信息
     * @throws Exception
     */
    public static IDataset getProductTypeCodeAndName(IDataset productList) throws Exception
    {
        IDataset returnDataset = new DatasetList();

        if (IDataUtil.isEmpty(productList))
        {
            return returnDataset;
        }

        IData keyMap = new DataMap();
        for (int i = 0, row = productList.size(); i < row; i++)
        {
            String productId = productList.getData(i).getString("PRODUCT_ID", "");

            String productTypeCode = ProductInfoQry.getProductTypeCodeByProductId(productId);

            if (StringUtils.isEmpty(productTypeCode))
            {
                continue;
            }

            if (!keyMap.containsKey(productTypeCode))
            {
                String productTypeName = ProductTypeInfoQry.getProductTypeNameByProductTypeCode(productTypeCode);

                if (StringUtils.isEmpty(productTypeName))
                {
                    continue;
                }

                keyMap.put(productTypeCode, productTypeName);
                IData typeData = new DataMap();
                typeData.put("PRODUCT_TYPE_CODE", productTypeCode);
                typeData.put("PRODUCT_TYPE_NAME", productTypeName);
                returnDataset.add(typeData);
            }
        }

        return returnDataset;
    }

    /**
     * ADCMAS集团用户暂停还收取功能费的问题，根源在于ADCMAS集团用户开户时，部分用户登记的台账 tf_b_trade_svcstate 和tf_b_trade_svc 表，主体服务的MAIN_TAG错填字段为0,该方法通过
     * 配置主体的rsrv_str1和rsrv_str2同时为1，就登记main_tag=1。 2010-10-20 添加注释
     * 
     * @param serviceId
     * @return
     * @throws Exception
     */
    public static String queryMainTagServiceId(String serviceId) throws Exception
    {

        if (serviceId == null || "".equals(serviceId.trim()))
        {
            return "0";
        }

        IData param = new DataMap();
        param.put("SERVICE_ID", serviceId);

        IDataset idset = StaticUtil.getList(getVisit(), "TD_B_SERVICE", "SERVICE_ID", serviceId);
        String mainTag1 = (idset != null && idset.size() > 0) ? idset.getData(0).getString("RSRV_STR1", "") : "0";
        String mainTag2 = (idset != null && idset.size() > 0) ? idset.getData(0).getString("RSRV_STR2", "") : "0";
        String reuslt = "";

        // 集团用户欠费问题核查,主体服务存的登记在BUG，这次直接用TD_B_SERVICE表的RSRV_STR1和RSRV_STR2全为1时登记
        // main_tag=1
        if ("1".equals(mainTag1) && "1".equals(mainTag2))
        {
            reuslt = "1";
        }
        else
        {
            reuslt = "0";
        }

        return reuslt;
    }

    /**
     * 查询产品控制信息
     * 
     * @param productId
     *            产品ID
     * @param eparchyCode
     *            地州编码
     * @param ctrlType
     *            控制类型
     * @author liaoyi
     * @throws Exception
     */
    public static IDataset queryProductCtrlInfoByITOCE(IData inparam) throws Exception
    {

        IData param = new DataMap();
        // IData dataProInfo = new DataMap();
        IDataset dataList = new DatasetList();

        // 得到业务类型
        String id = inparam.getString("PRODUCT_ID");
        String idtype = "P";

        String attrobj = inparam.getString("ATTR_OBJ");
        String attrcode = inparam.getString("ATTR_CODE");
        String eparchycode = inparam.getString("EPARCHY_CODE");

        param.put("ID_TYPE", "P");
        param.put("ATTR_OBJ", inparam.getString("ATTR_OBJ"));
        param.put("ATTR_CODE", inparam.getString("ATTR_CODE"));
        param.put("EPARCHY_CODE", inparam.getString("EPARCHY_CODE"));
        dataList = AttrBizInfoQry.getBizAttrByIdTypeObjCodeEparchy(id, idtype, attrobj, attrcode, eparchycode, null);
        /*
         * Iterator<Object> iterator = dataList.iterator(); while (iterator.hasNext()) { IData data = iterator.next();
         * dataProInfo.put(data.getString("ATTR_CODE", ""), data); } return dataProInfo;
         */
        return dataList;
    }

    /**
     * 查询服务控制信息
     * 
     * @param svcId
     *            服务ID
     * @param eparchyCode
     *            地州编码
     * @param product_id
     *            对应的产品ID
     * @author xiajj
     * @throws Exception
     */
    public static IData querySvcCtrlInfo(String svcId, String productId) throws Exception
    {

        IData param = new DataMap();
        IData dataProInfo = new DataMap();
        IDataset dataList = new DatasetList();
        // 得到业务类型
        param.put("ID", svcId);
        param.put("ID_TYPE", "S");
        param.put("ATTR_OBJ", productId);

        dataList = UAttrBizInfoQry.getBizAttrByIdTypeObj(svcId, "S", productId, null);

        Iterator<Object> iterator = dataList.iterator();
        while (iterator.hasNext())
        {
            IData data = (IData) iterator.next();
            dataProInfo.put(data.getString("ATTR_CODE", ""), data);
        }
        return dataProInfo;
    }
    /**
     * 判断元素是否只能订购一次
     * @param elementTypeCode
     * @param elementId
     * @param productId
     * @return
     * @throws Exception
     */
    public static boolean getOnlyOrderOneElement(String elementTypeCode, String elementId, String productId)throws Exception{
        boolean result = false;
        if(StringUtils.equals("S", elementTypeCode) && StringUtils.equals("20", elementId))
        {
            result = true;
        }
        
        return result;
    }
}
