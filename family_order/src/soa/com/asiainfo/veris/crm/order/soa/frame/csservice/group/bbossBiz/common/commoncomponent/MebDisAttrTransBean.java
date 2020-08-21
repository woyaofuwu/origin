
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.BBossAttrQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PkgElemInfoQry;

public class MebDisAttrTransBean
{

    // 将成员属性转化为资费（同步集团）
    // 资费与属性互换信息

    /**
     * chenyi 2014-03-24 将成员属性转化为资费，方便用户修改以及同步计费账务 1.传入 map 含有 ELEMENT_INFO 和PRODUCT_PARAM_INFO 2.传出参数中的
     * PRODUCT_PARAM_INFO 包含ELEMENT_INFO 里面的信息， 即参数需要入discnt表，也需要入attr表 如果成员属性 attr_value
     * 为集团下发指定值，则每个指定值在td_s_static表有对应本地资费编码 如果成员属性 attr_value
     * 为用户填写值，则该attr_code对应在td_s_static有一个本地资费编码，且attr_value为ICB参数值
     * 
     * @throws Exception
     */
    public static IData attrDataToDisData(IData map) throws Exception
    {
        // 1 先获取资费信息和产品参数信息
        IDataset productParamList = map.getDataset("PRODUCT_PARAM_INFO");
        IDataset productElementsList = map.getDataset("ELEMENT_INFO");

        // 2检查当前集合是否为空
        if (IDataUtil.isEmpty(productParamList))
        {
            return map;
        }
        if (IDataUtil.isNull(productElementsList))
        {
            productElementsList = new DatasetList();
        }

        String product_id = productParamList.getData(0).getString("PRODUCT_ID");
        // 获取集团产品规格编码 用于拼写本地attr_code
        String productspeccharacternumber = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_ATTR_BIZ", new String[]
        { "ID", "ID_TYPE", "ATTR_CODE", "ATTR_OBJ" }, "ATTR_VALUE", new String[]
        { "1", "B", product_id, "PRO" });// 获取集团产品编码
        // 获取本地成员产品编码
//        String baseMemProduct = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_B_PRODUCT_MEB", new String[]
//        { "PRODUCT_ID" }, "PRODUCT_ID_B", new String[]
//        { product_id });    
        String baseMemProduct = UProductInfoQry.queryMemProductIdByProductId(product_id);

        for (int i = 0, size = productParamList.size(); i < size; i++)
        {
            // 获取成员产品编码
            IDataset product_param = productParamList.getData(i).getDataset("PRODUCT_PARAM");
            // 循环参数 生成资费
            for (int j = 0, sizej = product_param.size(); j < sizej; j++)
            {
                IData paramData = product_param.getData(j);
                String attr_code = paramData.getString("ATTR_CODE");
                attr_code = productspeccharacternumber.concat(attr_code);// 组合配置出配置中的attr_code
                String attr_value = paramData.getString("ATTR_VALUE");
                String state = paramData.getString("STATE");

                // 校验是否是为变换为资费值的属性
                IData specialInfoData = chkSpecialParam(attr_code, attr_value);
                String elementCode = specialInfoData.getString("ELEMENT_ID");
                boolean isICB = specialInfoData.getBoolean("ISICB");
                // 4.1如果该没有配置对应资费信息，则continue继续循环
                if (StringUtils.isEmpty(elementCode))
                {
                    continue;
                }

                // 5 拼装新资费 有资费参数的
                if (isICB)
                {
                    // 构建icb参数 TD_B_ATTR_ITEMA
                    DatasetList attr_paramsetData = new DatasetList();
                    IData attr_param = new DataMap();
                    IDataset iCBDataset = AttrItemInfoQry.getAttrItemAByADC(elementCode, "D", "ZZZZ", "0");
                    if (IDataUtil.isNotEmpty(iCBDataset))
                    {
                        attr_param.put("ATTR_CODE", iCBDataset.getData(0).getString("ATTR_CODE"));
                        attr_param.put("ATTR_VALUE", attr_value);
                    }
                    attr_paramsetData.add(attr_param);
                    // 获取package_id 根据product_id，element_id 查询TD_B_PACKAGE_ELEMENT
                    IData packData = PkgElemInfoQry.getDiscntsByDiscntCode(elementCode, baseMemProduct, null);

                    IData newDisnct = newDisData(baseMemProduct, state, attr_paramsetData, packData.getString("PACKAGE_ID"), elementCode);// 新拼写的资费信息
                    productElementsList.add(newDisnct);
                }
                else
                {
                    IData packData = PkgElemInfoQry.getDiscntsByDiscntCode(elementCode, baseMemProduct, null);
                    String element_id = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
                    { "TYPE_ID", "SUBSYS_CODE", "PDATA_ID" }, "DATA_ID", new java.lang.String[]
                    { "BBOSS_ATTRTODIS", attr_value, attr_code });
                    IData newDisnct = newDisData(baseMemProduct, state, null, packData.getString("PACKAGE_ID"), element_id);// 新拼写的资费信息
                    productElementsList.add(newDisnct);
                }

            }
        }

        // 将新拼装的资费放入map
        if (IDataUtil.isNotEmpty(productElementsList))
        {
            map.put("ELEMENT_INFO", productElementsList);
        }

        return map;
    }

    /**
     * @Function:
     * @Description:判断当前属性是否是成员特殊资费属性，如果是，则判断是否有ICB
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @throws Exception
     * @date: 下午3:32:50 2013-10-23
     */
    private static IData chkSpecialParam(String attr_code, String attr_value) throws Exception
    {
        IData map = new DataMap();
        boolean isICB = true;// 属性值是否为ICB参数标记
        // 判断是不是特殊成员属性资费
        // 1.1获取此属性对应的元素编码 如果用户填写值则TD_S_STATIC 的SUBSYS_CODE配置为-1
        String elementCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
        { "TYPE_ID", "PDATA_ID", "SUBSYS_CODE" }, "DATA_ID", new java.lang.String[]
        { "BBOSS_ATTRTODIS", attr_code, "-1" });

        // 1.2如果为集团公司指定值则TD_S_STATIC 的SUBSYS_CODE配置为集团公司下发值 attr_value不作为ICB参数
        if (StringUtils.isEmpty(elementCode))
        {
            elementCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "PDATA_ID", "SUBSYS_CODE" }, "DATA_ID", new java.lang.String[]
            { "BBOSS_ATTRTODIS", attr_code, attr_value });
            isICB = false;
        }
        map.put("ELEMENT_ID", elementCode);
        map.put("ISICB", isICB);
        return map;

    };

    // 将资费转化为成员属性(同步集团)
    /**
     * chenyi 2014-03-21 由于成员资费需要同步给本地账务，且在集团公司是以成员产品参数形式存在，所以需要成员资费需要在本地以资费形式存在，在集团公司侧需要以成员属性形式存在 将前台资费转化为产品参数
     * 方便发服开生成报文 1.传入 map 含有 ELEMENT_INFO 和PRODUCT_PARAM_INFO 2.传出参数中的 PRODUCT_PARAM_INFO 包含ELEMENT_INFO 里面的信息， 即资费信息要入
     * discnt表，也需要入attr表 如果成员属性 attr_value 为集团下发指定值，则每个指定值在td_s_static表有对应本地资费编码 如果成员属性 attr_value
     * 为用户填写值，则该attr_code对应在td_s_static有一个本地资费编码，且attr_value为ICB参数值
     * 
     * @throws Exception
     */
    public static IData disDataToAttrData(IData map) throws Exception
    {

        // 1 先获取资费信息和产品参数信息
        IDataset productParamList = map.getDataset("PRODUCT_PARAM_INFO");
        IDataset productElementsList = map.getDataset("ELEMENT_INFO");
        
        
        IDataset product_param = null;
        if (IDataUtil.isNotEmpty(productParamList))
        {
            product_param = productParamList.getData(0).getDataset("PRODUCT_PARAM");
        }
        else
        {
            productParamList = new DatasetList();
            product_param = new DatasetList();
            IData data = new DataMap();
            data.put("PRODUCT_PARAM", product_param);
            productParamList.add(data);
        }

        // 2 循环资费信息 判断资费信息是不是对应的成员产品参数如果是将信息加入产品参数

        if (IDataUtil.isEmpty(productElementsList))
        {
            return map;
        }

        for (int i = 0, size = productElementsList.size(); i < size; i++)
        {
            IData productElementData = productElementsList.getData(i);
            String element_id = productElementData.getString("ELEMENT_ID");
            String modify_tag = productElementData.getString("MODIFY_TAG");

            // 获取此元素对应的属性编码
            String attrCode = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new java.lang.String[]
            { "BBOSS_ATTRTODIS", element_id });

            if (StringUtils.isEmpty(attrCode))
            {
                continue;
            }

            // 1获取本地属性配置信息
            IDataset attrDataset = BBossAttrQry.qryBBossAttrByAttrCode(attrCode);
            if (IDataUtil.isEmpty(attrDataset))
            {
                CSAppException.apperr(GrpException.CRM_GRP_837);
            }

            // 2判断是否是集团公司下发指定值

            String subsys_code = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new java.lang.String[]
            { "TYPE_ID", "DATA_ID" }, "SUBSYS_CODE", new java.lang.String[]
            { "BBOSS_ATTRTODIS", element_id });
            // 如果不是则subsys_code为-1，icb参数值为newAttrData的attrvalue，
            if ("-1".equals(subsys_code))
            {
                IData attrData = attrDataset.getData(0);
                IDataset iCBValueDataset = productElementData.getDataset("ATTR_PARAM");// 获取ICB参数的值

                // 拼写新的attrINFO
                for (int j = 0, sizej = iCBValueDataset.size(); j < sizej; j++)
                {
                    IData ICBValue = iCBValueDataset.getData(j);
                    IData newAttrData = newATTRData(ICBValue.getString("ATTR_VALUE"), attrData.getString("ATTR_NAME"), modify_tag, attrCode);
                    product_param.add(newAttrData);
                }
            }
            else
            { // 如果不是则subsys_code为newAttrData的attrvalue
                IData attrData = attrDataset.getData(0);
                // 拼写新的属性信息 subsys_code为新属性的attr_value
                IData newAttrData = newATTRData(subsys_code, attrData.getString("ATTR_NAME"), modify_tag, attrCode);
                product_param.add(newAttrData);
            }
        }

        // 3将新拼写的属性放到map
        if (IDataUtil.isNotEmpty(product_param))
        {
            map.put("PRODUCT_PARAM_INFO", productParamList);
        }

        // 4 返回新构造好的信息
        return map;
    }

    /**
     * chenyi 2014-3-24 拼写新的参数信息
     * 
     * @return
     */
    private static IData newATTRData(String attrValue, String attr_name, String modify_tag, String attr_code)
    {
        IData newAttrData = new DataMap();
        newAttrData.put("ATTR_VALUE", attrValue);
        newAttrData.put("ATTR_NAME", attr_name);
        newAttrData.put("ATTR_CODE", attr_code);
        // 根据资费状态判断新拼属性状态
        if (TRADE_MODIFY_TAG.Add.getValue().equals(modify_tag))
        {
            newAttrData.put("STATE", "ADD");
        }
        else if (TRADE_MODIFY_TAG.EXIST.getValue().equals(modify_tag))
        {
            newAttrData.put("STATE", "EXIST");
        }
        else if (TRADE_MODIFY_TAG.MODI.getValue().equals(modify_tag))
        {
            newAttrData.put("STATE", "MODI");
        }
        else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modify_tag))
        {
            newAttrData.put("STATE", "DEL");
        }
        return newAttrData;
    }

    /**
     * chenyi 2014-3-24 拼写新的资费信息
     * 
     * @return
     * @throws Exception
     */
    private static IData newDisData(String product_id, String state, IDataset attr_param, String package_id, String element_id) throws Exception
    {
        IData newDisnct = new DataMap();
        newDisnct.put("START_DATE", SysDateMgr.getSysTime());
        newDisnct.put("ELEMENT_TYPE_CODE", "D");
        newDisnct.put("PRODUCT_ID", product_id);
        newDisnct.put("END_DATE", SysDateMgr.END_TIME_FOREVER);
        newDisnct.put("INST_ID", "");
        newDisnct.put("ATTR_PARAM", attr_param);
        newDisnct.put("PACKAGE_ID", package_id);
        newDisnct.put("ELEMENT_ID", element_id);

        if ("ADD".equals(state))
        {
            newDisnct.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        }
        else if (TRADE_MODIFY_TAG.EXIST.getValue().equals(state))
        {
            newDisnct.put("MODIFY_TAG", TRADE_MODIFY_TAG.EXIST.getValue());
        }
        else if ("MODI".equals(state))
        {
            newDisnct.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
        }
        else if ("DEL".equals(state))
        {
            newDisnct.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
        }

        return newDisnct;
    }
}
