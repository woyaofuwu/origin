
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;

public class DevicePriceQry
{
    public static IDataset getDevicePrice(IData data) throws Exception
    {
        IDataset resDevFeeList = Dao.qryByCode("TD_B_DEVICE_PRICE", "SEL_PK_DEVICE_PRICE_NEW", data, Route.CONN_CRM_CEN);
        IDataset brandInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(resDevFeeList)){
        	for (int i = 0; i < resDevFeeList.size(); i++) {
        		String brand_code = resDevFeeList.getData(i).getString("BRAND_CODE","ZZZZ");
            	String product_id = resDevFeeList.getData(i).getString("PRODUCT_ID","-1");
            	if("ZZZZ".equals(brand_code) || "-1".equals(product_id)){

            	}else{
            		brandInfos = UpcCall.queryOfferComChaByCond("P", product_id, "BRAND_CODE");
                	if(IDataUtil.isNotEmpty(brandInfos)){
                		if(brand_code.equals(brandInfos.getData(0).getString("FIELD_NAME"))){
                			resDevFeeList.remove(i);
                			i--;
                		}
                	}
            	}
			}
        }
        return resDevFeeList;
    }

    /*
     * 获取卡费新接口 根据资源修改 sunxin
     */
    public static IData getDevicePrice(String eparchy, String pdtId, String tradeTypeCode, String resKindCode, String resTypeCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("RES_KIND_CODE", resKindCode);
        param.put("PRODUCT_ID", pdtId);
        IDataset devicePrices = Dao.qryByCode("TD_B_DEVICE_PRICE", "SEL_PK_DEVICE_PRICE_NEW1", param, Route.CONN_CRM_CEN);
        IDataset brandInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(devicePrices)){
        	for (int i = 0; i < devicePrices.size(); i++) {
        		String brand_code = devicePrices.getData(i).getString("BRAND_CODE","ZZZZ");
            	String product_id = devicePrices.getData(i).getString("PRODUCT_ID","-1");
            	if("ZZZZ".equals(brand_code) || "-1".equals(product_id)){

            	}else{
            		brandInfos = UpcCall.queryOfferComChaByCond("P", product_id, "BRAND_CODE");
                	if(IDataUtil.isNotEmpty(brandInfos)){
                		if(brand_code.equals(brandInfos.getData(0).getString("FIELD_NAME"))){
                			devicePrices.remove(i);
                			i--;
                		}
                	}
            	}
			}
        }
        return devicePrices == null || devicePrices.size() == 0 ? null : devicePrices.getData(0);
    }

    public static IData getDevicePrice(String eparchy, String resTypeCode, String pdtId, String tradeTypeCode, String capacityTypeCode, String resKindCode) throws Exception
    {
        IData param = new DataMap();
        param.put("EPARCHY_CODE", eparchy);
        param.put("CAPACITY_TYPE_CODE", capacityTypeCode);
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("RES_KIND_CODE", resKindCode);
        param.put("PRODUCT_ID", pdtId);
        IDataset devicePrices = Dao.qryByCode("TD_B_DEVICE_PRICE", "SEL_PK_DEVICE_PRICE_NEW2", param, Route.CONN_CRM_CEN);
        IDataset brandInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(devicePrices)){
        	for (int i = 0; i < devicePrices.size(); i++) {
        		String brand_code = devicePrices.getData(i).getString("BRAND_CODE","ZZZZ");
            	String product_id = devicePrices.getData(i).getString("PRODUCT_ID","-1");
            	if("ZZZZ".equals(brand_code) || "-1".equals(product_id)){

            	}else{
            		brandInfos = UpcCall.queryOfferComChaByCond("P", product_id, "BRAND_CODE");
                	if(IDataUtil.isNotEmpty(brandInfos)){
                		if(brand_code.equals(brandInfos.getData(0).getString("FIELD_NAME"))){
                			devicePrices.remove(i);
                			i--;
                		}
                	}
            	}
			}
        }
        return devicePrices == null || devicePrices.size() == 0 ? null : devicePrices.getData(0);
    }

    public static IDataset getDevicePrices(String resTypeCode, String resKindCode, String cardKindCode, String capacityTypeCode, String tradeTypeCode, String productId, String classId, String eparchyCode) throws Exception
    {
        IData param = new DataMap();
        param.put("RES_TYPE_CODE", resTypeCode);
        param.put("RES_KIND_CODE", resKindCode);
        param.put("CARD_KIND_CODE", cardKindCode);
        param.put("CAPACITY_TYPE_CODE", capacityTypeCode);
        param.put("TRADE_TYPE_CODE", tradeTypeCode);
        param.put("PRODUCT_ID", productId);
        param.put("CLASS_ID", classId);
        param.put("EPARCHY_CODE", eparchyCode);
        IDataset dataset = Dao.qryByCode("TD_B_DEVICE_PRICE", "SEL_PK_DEVICE_PRICE_NEW", param, Route.CONN_CRM_CEN);
        //TODO huanghua 24 与产商品解耦---已解决
        IDataset brandInfos = new DatasetList();
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		String brand_code = dataset.getData(i).getString("BRAND_CODE","ZZZZ");
            	String product_id = dataset.getData(i).getString("PRODUCT_ID","-1");
            	if("ZZZZ".equals(brand_code) || "-1".equals(product_id)){

            	}else{
            		brandInfos = UpcCall.queryOfferComChaByCond("P", product_id, "BRAND_CODE");
                	if(IDataUtil.isNotEmpty(brandInfos)){
                		if(brand_code.equals(brandInfos.getData(0).getString("FIELD_NAME"))){
                			dataset.remove(i);
                			i--;
                		}
                	}
            	}
			}
        }
        return dataset;
    }
}
