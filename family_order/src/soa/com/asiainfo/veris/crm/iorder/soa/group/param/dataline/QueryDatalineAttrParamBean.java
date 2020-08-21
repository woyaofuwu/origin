package com.asiainfo.veris.crm.iorder.soa.group.param.dataline;

import java.util.Set;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.iorder.soa.group.param.QueryAttrParamBean;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UItemAInfoQry;

import net.sf.json.JSONArray;

public class QueryDatalineAttrParamBean  extends QueryAttrParamBean 
{

    public static IData queryDatalineParamAttrForChgInit(IData param) throws Exception
    {
    	IData result = new DataMap();
        String offerCode = param.getString("PRODUCT_ID");

        IDataset dataset = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "0", null);
        IDataset dataChaSpec = UItemAInfoQry.queryOfferChaAndValByCond(offerCode, "P", "2", null);
        IData attrItemA = new DataMap();
        IData attrItemB = new DataMap();
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemA = IDataUtil.hTable2STable(dataset, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }
        if (IDataUtil.isNotEmpty(dataset))
        {
            attrItemB = IDataUtil.hTable2STable(dataChaSpec, "FIELD_NAME", "DEFAULT_VALUE", "ATTR_VALUE");

        }

        if (IDataUtil.isNotEmpty(attrItemA))
        {
            Set<String> propNames = attrItemA.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemA.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                if (IDataUtil.isNotEmpty(workTypeCodeInfo))
                {
                    attrItem.put("DATA_VAL", workTypeCodeInfo);
                    result.put(key, attrItem);
                }
                else
                {
                    String attrItemValue = attrCodeInfo.getString("ATTR_VALUE");
                    attrItem.put("DATA_VAL", attrItemValue);
                    result.put(key, attrItem);
                }
            }
        }
        if (IDataUtil.isNotEmpty(attrItemB))
        {
            Set<String> propNames = attrItemB.keySet();
            for (String key : propNames)
            {
                IData attrCodeInfo = attrItemB.getData(key);
                IData attrItem = new DataMap();
                IDataset workTypeCodeInfo = attrCodeInfo.getDataset("DATA_VAL");
                attrItem.put("DATA_VAL", workTypeCodeInfo);
                result.put(key, attrItem);
            }
        }
        
        result.put("DATALINE_INFO",result.get("SP_LINE"));
        
        // 从EOSP获取专线数据
        IDataset eos = param.getDataset("EOS");
        IData eosList = new DataMap();
        IData resultDataset = new DataMap();
        if (null != eos && eos.size() > 0)
        {
            eosList = eos.getData(0);
            IDataset eosDataset = new DatasetList();
            IData inputParam = new DataMap();
            inputParam.put("NODE_ID", eosList.getString("NODE_ID", ""));
            inputParam.put("IBSYSID", eosList.getString("IBSYSID", ""));
            inputParam.put("SUB_IBSYSID", eosList.getString("SUB_IBSYSID", ""));
            inputParam.put("PRODUCT_ID", eosList.getString("PRODUCT_ID"));
            eosDataset.add(inputParam);

            resultDataset = getEsopData(eosDataset);
        } else
        {
//            CSViewException.apperr(GrpException.CRM_GRP_838);
        }

        if (null != resultDataset && resultDataset.size() > 0)
        {
            IDataset commonData = resultDataset.getDataset("COMMON_DATA");
            IDataset datalineData = resultDataset.getDataset("DLINE_DATA");

            if (null != datalineData && datalineData.size() > 0)
            {
                for (int i = 0; i < datalineData.size(); i++)
                {
                    IData dataline = datalineData.getData(i);
                    int number = i + 1;
                    dataline.put("pam_NOTIN_LINE_NUMBER_CODE", String.valueOf(i));
                    dataline.put("pam_NOTIN_LINE_NUMBER", "专线" + String.valueOf(number));
                    dataline.put("pam_NOTIN_LINE_BROADBAND", dataline.get("BANDWIDTH"));
                    dataline.put("pam_NOTIN_PRODUCT_NUMBER", dataline.get("PRODUCTNO"));
                    dataline.put("pam_NOTIN_LINE_INSTANCENUMBER", dataline.get("PRODUCTNO"));

                    String cityA = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYA"));
                    String cityZ = StaticUtil.getStaticValue(null, "TD_M_AREA", "AREA_NAME", "AREA_CODE", dataline.getString("CITYZ"));

                    dataline.put("pam_NOTIN_A_CITY", cityA);
                    dataline.put("pam_NOTIN_Z_CITY", cityZ);

                    //专线价格
                    dataline.put("pam_NOTIN_LINE_PRICE", "0");
                    //安装调试费
                    dataline.put("pam_NOTIN_INSTALLATION_COST", "0");
                    //一次性通信服务费
                    dataline.put("pam_NOTIN_ONE_COST", "0");
                    
                    dataline.put("pam_LINE_TRADE_NAME", dataline.getString("TRADENAME",""));//专线名称
                    
                    //集团所在市县分成比例 pam_NOTIN_GROUP_PERCENT
                    //A端所在市县分成比例 pam_NOTIN_A_PERCENT
                    //Z端所在市县分成比例 pam_NOTIN_Z_PERCENT
                    //通过合同编码，查询合同的专线信息。
                    IData contractParam = new DataMap();
                    contractParam.put("CONTRACT_ID", param.getString("CONTRACT_ID"));
                    contractParam.put("PRODUCT_ID", offerCode);
                    contractParam.put("CUST_ID", param.getString("CUST_ID"));
                    contractParam.put("LINE_NO", dataline.get("PRODUCTNO"));
                    IDataset contratSet = CSAppCall.call("CM.ConstractGroupSVC.qryContractLineInfoByLineNo", contractParam);

                    if (IDataUtil.isNotEmpty(contratSet)){
                        IData tempLine = contratSet.getData(0);
//                        dataline.put("pam_LINE_TRADE_NAME", tempLine.getString("LINE_NAME",""));//专线名称
                        dataline.put("pam_NOTIN_LINE_BROADBAND",tempLine.getString("RSRV_STR1","0"));//专线宽带
                        dataline.put("pam_NOTIN_LINE_PRICE",tempLine.getString("RSRV_STR2","0"));//专线价格
                        dataline.put("pam_NOTIN_INSTALLATION_COST",tempLine.getString("RSRV_STR3","0"));//安装调试费
                        dataline.put("pam_NOTIN_ONE_COST",tempLine.getString("RSRV_STR4","0"));//一次性通信服务费
                        dataline.put("pam_NOTIN_GROUP_PERCENT",tempLine.getString("RSRV_STR6","20%"));//集团所在市县分成比例
                        dataline.put("pam_NOTIN_A_PERCENT",tempLine.getString("RSRV_STR7","40%"));//A端所在市县分成比例
                        dataline.put("pam_NOTIN_Z_PERCENT",tempLine.getString("RSRV_STR8","40%"));//Z端所在市县分成比例
                    }
                }
            }
            IData tempData=new DataMap();
            IData tempCommonData=new DataMap();
            tempData.put("DATA_VAL", JSONArray.fromObject(datalineData).toString().replaceAll("\"", "\'"));
            tempCommonData.put("DATA_VAL", JSONArray.fromObject(commonData).toString().replaceAll("\"", "\'"));
            
            result.put("VISP_INFO", tempData);
            result.put("NOTIN_DATALINE_DATA", tempData);
            result.put("NOTIN_COMMON_DATA", tempCommonData);
        }
        return result;
    
    }
    /**
     * 从ESOP获取专线信息
     * 
     * @author liujy
     * @param bc
     * @param param
     * @return
     * @throws Exception
     */
    public static IData getEsopData(IDataset eosDataset) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData eosData = eosDataset.getData(0);
        IData resultDataset = new DataMap();

        IData inputParam = new DataMap();
        inputParam.put("X_TRANS_CODE", "ITF_EOS_QcsGrpBusi");
        inputParam.put("X_SUBTRANS_CODE", "GetEosInfo");
        inputParam.put("NODE_ID", eosData.getString("NODE_ID", ""));
        inputParam.put("IBSYSID", eosData.getString("IBSYSID", ""));
        inputParam.put("SUB_IBSYSID", eosData.getString("SUB_IBSYSID", ""));
        inputParam.put("PRODUCT_ID", eosData.getString("PRODUCT_ID"));
        inputParam.put("OPER_CODE", "14");

        IDataset httResultSetDataset = CSAppCall.call("SS.ESOPQcsGrpBusiIntfSvc.getEosInfo", inputParam);

        if (null != httResultSetDataset && httResultSetDataset.size() > 0)
        {
            IData dataLine = httResultSetDataset.getData(0);
            IData data = dataLine.getData("DLINE_DATA");
            if (null != data && data.size() > 0)
            {
                resultDataset = mergeData(dataset, httResultSetDataset);
            }
        }

        return resultDataset;
    }
    
    /**
     * 解析专线数据
     * 
     * @param dataset
     * @param httpResult
     * @return
     * @throws Exception
     */
    protected static IData mergeData(IDataset dataset, IDataset httpResult) throws Exception
    {
        IData resultData = new DataMap();
        IDataset comData = new DatasetList();
        IDataset dataLineAttr = new DatasetList();

        if (null != httpResult && httpResult.size() > 0)
        {
            IData dataLine = httpResult.getData(0);
            if (null != dataLine && dataLine.size() > 0)
            {
                IData totalData = dataLine.getData("DLINE_DATA");
                IData commonData = totalData.getData("COMM_DATA_MAP");
                IDataset lineDataList = totalData.getDataset("LINE_DATA_LIST");

                // 公共数据
                for (int i = 0; i < commonData.size(); i++)
                {
                    IData attrValue = new DataMap();
                    String attr[] = commonData.getNames();
                    attrValue.put("ATTR_CODE", attr[i]);
                    attrValue.put("ATTR_VALUE", commonData.getString(attr[i]));
                    comData.add(attrValue);
                }

                // 专线数据
                for (int j = 0; j < lineDataList.size(); j++)
                {
                    IData data = lineDataList.getData(j);
                    IData attrValue = new DataMap();
                    for (int k = 0; k < data.size(); k++)
                    {
                        String attr[] = data.getNames();
                        attrValue.put(attr[k], data.getString(attr[k]));
                    }
                    dataLineAttr.add(attrValue);
                }
            }
        }

        resultData.put("COMMON_DATA", comData);
        resultData.put("DLINE_DATA", dataLineAttr);

        return resultData;
    }
}
