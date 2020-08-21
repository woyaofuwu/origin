
package com.asiainfo.veris.crm.order.soa.group.groupTrans.parse;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrItemInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.util.GroupElementParamsUtil;

public class ElementParamsParse
{
    public static void parseElmentParams2Meb(IData idata) throws Exception
    {

        IData params = new DataMap();

        String sn = idata.getString("SERIAL_NUMBER");

        IData memInfo = UcaInfoQry.qryUserInfoBySn(sn);

        String memUserId = memInfo.getString("USER_ID");
        params.put("USER_ID", memUserId);
        params.put("USER_ID_A", idata.getString("USER_ID"));

        params.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getUserEparchyCode());// 服务根据成员EPARCHY_CODE路由

        IDataset elementInfos = idata.getDataset("ELEMENT_INFO");

        for (int i = 0, iSize = elementInfos.size(); i < iSize; i++)
        {
            IData element = elementInfos.getData(i);
            String elementId = element.getString("ELEMENT_ID", "");
            String elementType = element.getString("ELEMENT_TYPE_CODE", "");

            if (!"S".equals(elementType))
                continue;

            params.put("SERVICE_ID", elementId);
            IDataset plantSvcParams = CSAppCall.call("SS.AdcMebParamsSvc.getServiceParam", params);

            element.put("ATTR_PARAM", plantSvcParams);
        }
    }

    public static void parseElmentParams(IData idata) throws Exception
    {

        // 接口传入的产品元素的个性化参数
        IDataset servIntfParamset = idata.getDataset("SERV_PARAM", new DatasetList());
        IDataset disIntfParamset = idata.getDataset("DISCNT_PARAM", new DatasetList());

        IDataset elementInfos = idata.getDataset("ELEMENT_INFO");

        IDataset allServParams = new DatasetList();
        IDataset allDiscntParams = new DatasetList();

        // 1,获得元素对应的参数
        for (int i = 0, iSize = elementInfos.size(); i < iSize; i++)
        {
            IData element = elementInfos.getData(i);
            String elementId = element.getString("ELEMENT_ID", "");
            String elementType = element.getString("ELEMENT_TYPE_CODE", "");

            if (!"S".equals(elementType) && !"D".equals(elementType))
                break;

            // filter 参数
            if ("S".equals(elementType))
                allServParams.addAll(DataHelper.filter(servIntfParamset, "ID=" + elementId));

            if ("D".equals(elementType))
                allDiscntParams.addAll(DataHelper.filter(disIntfParamset, "ID=" + elementId));

            // adc/mas可以不传任何服务参数,特殊处理,拼参数
            if (IDataUtil.isEmpty(allServParams))
            {
                boolean isAdcMasProduct = isAdcMasProduct(idata.getString("PRODUCT_ID"), elementId);
                if (isAdcMasProduct)
                {
                    // 补充处理 servIntfParamset 数据
                    IData adcMasDataId = new DataMap();
                    adcMasDataId.put("ID", elementId);
                    adcMasDataId.put("PARAM_VALUE", new DataMap());
                    allServParams.add(adcMasDataId);

                }
            }
        }

        // 2 处理参数
        for (int i = 0, iSize = allServParams.size(); i < iSize; i++)
        {
            IData servParam = allServParams.getData(i);
            String elementId = servParam.getString("ID");
            IData commonServParam = servParam.getData("PARAM_VALUE");

            // 海南other没有,molist为null
            // IData otherServParam = servParam.getData("OTHERLIST");
            IDataset elmentParams = DataHelper.filter(elementInfos, "ELEMENT_ID=" + elementId);

            IDataset svcResult = new DatasetList();
            IData planSvc = null;
            // adc-mas
            boolean isAdcMasProduct = isAdcMasProduct(idata.getString("PRODUCT_ID"), elementId);

            if (isAdcMasProduct)
            {

                planSvc = GroupElementParamsUtil.qryElementParamByElementIdADCMAS(commonServParam, elementId, "S", idata.getString("OPER_TYPE"), idata.getString("PRODUCT_ID"), idata);

                IData servParamsADCfirst = new DataMap();
                servParamsADCfirst.put("PARAM_VERIFY_SUCC", "true");

                IData servParamsADCsecond = new DataMap();
                servParamsADCsecond.put("CANCLE_FLAG", "false");
                servParamsADCsecond.put("ID", elementId);
                servParamsADCsecond.put("PLATSVC", planSvc);
                svcResult.add(servParamsADCfirst);

                servParamsADCsecond.put("MOLIST", new DatasetList());

                svcResult.add(servParamsADCsecond);

            }
            else
            {

                svcResult = GroupElementParamsUtil.qryElementParamByElementId(commonServParam, elementId, "S", idata.getString("OPER_TYPE"));
            }

            elmentParams.getData(0).put("ATTR_PARAM", svcResult);
        }

        for (int i = 0, iSize = allDiscntParams.size(); i < iSize; i++)
        {
            IData discntParam = allDiscntParams.getData(i);
            String elementId = discntParam.getString("ID");
            IData commoDiscntParam = discntParam.getData("PARAM_VALUE");

            IDataset discntResult = GroupElementParamsUtil.qryElementParamByElementId(commoDiscntParam, elementId, "D", idata.getString("OPER_TYPE"));

            IDataset elmentParams = DataHelper.filter(elementInfos, "ELEMENT_ID=" + elementId);

            elmentParams.getData(0).put("ATTR_PARAM", discntResult);
        }
    }

    public static boolean isAdcMasProduct(String productId, String elmentId) throws Exception
    {

        boolean isAdcMasFlag = false;
        // adc-mas
        //IDataset platsvcParams = AttrItemInfoQry.getelementItemaByProductId("S", "9", productId, null);
        IDataset adcset = AttrBizInfoQry.getBizAttr(elmentId, "S", "ServPage", productId, null);
        String brandCode = UProductInfoQry.getBrandCodeByProductId(productId);
        // 表示这个是弹出式的服务,现在只有ADCMAS类的配置的是弹出类服务
        if (IDataUtil.isNotEmpty(adcset) && ("ADCG".equals(brandCode) || "MASG".equals(brandCode)))
            isAdcMasFlag = true;

        return isAdcMasFlag;
    }

}
