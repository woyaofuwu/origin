
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive;

import java.util.ArrayList;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.SaleActiveException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.person.common.SaleActiveConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.HwTerminalCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.tib.SaleActiveInfoQry;

public class TerminalBean extends CSBizBean
{
    private IData convertTermianlInfo(IData terminalData) throws Exception
    {
        IData returnData = new DataMap();

        returnData.put("DEVICE_MODEL_CODE", terminalData.getString("DEVICE_MODEL_CODE"));
        returnData.put("DEVICE_MODEL", terminalData.getString("DEVICE_MODEL")); // 终端型号名称
        returnData.put("DEVICE_COST", terminalData.getString("DEVICE_COST")); // 终端成本价(结算价(进货价))
        returnData.put("DEVICE_BRAND", terminalData.getString("DEVICE_BRAND")); // 终端品牌名称
        returnData.put("DEVICE_BRAND_CODE", terminalData.getString("DEVICE_BRAND_CODE")); // 终端品牌编码
        returnData.put("SUPPLY_COOP_ID", terminalData.getString("SUPPLY_COOP_ID")); // 提供商编码
        returnData.put("TERMINAL_TYPE_CODE", terminalData.getString("TERMINAL_TYPE_CODE")); // 某一终端类型编码
        returnData.put("TERMINAL_STATE", terminalData.getString("TERMINAL_STATE")); // 在途状态
        returnData.put("SALE_PRICE", terminalData.getString("RSRV_STR6")); // 终端销售价--rsrv_str6
        returnData.put("DEPUTY_FEE", terminalData.getString("RSRV_STR7")); // 代办费 rsrv_str7
        returnData.put("IS_INTELL_TERMINAL", terminalData.getString("RSRV_STR1")); // 是否智能机：0:非智能机 ,1:智能机 RSRV_STR1
        returnData.put("RSRV_STR3", terminalData.getString("RSRV_STR3")); // 终端颜色描述
        returnData.put("RSRV_STR4", terminalData.getString("RSRV_STR4")); // 终端电池配置
        returnData.put("RES_SKU_ID", terminalData.getString("RES_SKU_ID")); // 终端sku编码

        return returnData;
    }

    /**
     * 根据机型编码调用资源接口获取机型相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getEnableTerminalByResNo(IData input)  throws Exception
    {
        IDataset hdhkActives = SaleActiveInfoQry.queryHdfkActivesByResNo(input.getString("RES_NO"));

        if (IDataUtil.isNotEmpty(hdhkActives))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_29);
        }

        String resNo = input.getString("RES_NO");
        String staffId = input.getString("SALE_STAFF_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");
        
        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(input.getString("PRODUCT_ID"));
        String saleTag = "";
        
        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 2016-08-26
         * "RG".equals(productInfo.getString("RSRV_TAG2") 红包专用
         * * REQ201703070008 关于调整明星终端产品全网统一营销活动渠道办理方式的需求
         * "M".equals(productInfo.getString("RSRV_TAG2")) 
         * */
        if ("YX08".equals(input.getString("CAMPN_TYPE"))||"R".equals(productInfo.getString("RSRV_TAG2",""))
                || "M".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "1"; // 买断过一次,再优惠购机

        if ("YX09".equals(input.getString("CAMPN_TYPE")))
            saleTag = "2"; // 虚拟供货的再优惠购机

        if (IDataUtil.isNotEmpty(productInfo) && "G".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "3";

        IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(resNo, staffId, serialNumber, saleTag);

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }

        IData returnData = convertTermianlInfo(terminalData);

        String terminalState = returnData.getString("TERMINAL_STATE");

        if (!"1".equals(terminalState))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_39);
        }

        /*
         * if("|5|6|7|8|C|".indexOf(terminalState)>-1) { CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_29); }
         * String buyType = input.getString("BUY_TYPE"); //页面传过来的参数，标识是销售的终端，还是赠送的终端，仅仅给下面的校验使用。
         * if(SaleActiveConst.TERMINAL_BUY_TYPE_GIFT.equals(buyType)) { if("4".equals(terminalState)) {
         * CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_30); } }
         */

        return returnData;
    }

    /**
     * 根据机型编码调用资源接口获取机型相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getTerminalByDeviceModelCode(IData input) throws Exception
    {

        String deviceModelCode = input.getString("DEVICE_MODEL_CODE", "");
        String resTypeId = input.getString("RES_TYPE_ID", "");
        IDataset terminalDataset = HwTerminalCall.getTerminalByDeviceMode(deviceModelCode, resTypeId);
        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
        	if(StringUtils.equals("1", input.getString("TM_CALL_TYPE")))
        	{
        		return terminalData;
        	}
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }

        return convertTermianlInfo(terminalData);
    }

    /**
     * 根据机型编码调用资源接口获取机型相关信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData getTerminalByResNoOnly(IData input) throws Exception
    {
        String resNo = input.getString("RES_NO");
        
//        IData productInfo = UProductInfoQry.qryProductByPK(input.getString("PRODUCT_ID"));
        IData productInfo = UpcCall.queryTempChaByCond(input.getString("PRODUCT_ID"), "TD_B_PRODUCT");
        String saleTag = "";
        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 2016-08-26
         * "RG".equals(productInfo.getString("RSRV_TAG2") 红包专用
         * * REQ201703070008 关于调整明星终端产品全网统一营销活动渠道办理方式的需求
         * "M".equals(productInfo.getString("RSRV_TAG2")) 
         * */
        if ("YX08".equals(input.getString("CAMPN_TYPE"))||"R".equals(productInfo.getString("RSRV_TAG2",""))
                || "M".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "1"; // 买断过一次,再优惠购机

        if ("YX09".equals(input.getString("CAMPN_TYPE")))
            saleTag = "2"; // 虚拟供货的再优惠购机

        if (IDataUtil.isNotEmpty(productInfo) && "G".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "3";
        
        String staffId = getVisit().getStaffId();

        IDataset terminalDataset = HwTerminalCall.getTerminalInfoByTerminalId(resNo, staffId, null, saleTag);

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
        	if(StringUtils.equals("1", input.getString("TM_CALL_TYPE")))
        	{
        		return terminalData;
        	}
        	
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }

        return convertTermianlInfo(terminalData);
    }

    /**
     * 根据终端串号实占
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void occupyTerminalByResNo(IData input) throws Exception
    {
        IDataset resultSet = HwTerminalCall.occupyTerminalByTerminalId(input);

        IData resultData = resultSet.getData(0);
        String xResultCode = resultData.getString("X_RESULTCODE");

        if (!"0".equals(xResultCode))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, resultData.getString("X_RESULTINFO"));
        }
    }

    /**
     * 根据终端串号校验
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData preOccupyTerminalByResNo(IData input) throws Exception
    {
        String resNo = input.getString("RES_NO", "");
        String netOrderId = input.getString("NET_ORDER_ID");
        String saleStaffId = input.getString("SALE_STAFF_ID");
        String serialNumber = input.getString("SERIAL_NUMBER");

        IData productInfo = UProductInfoQry.qrySaleActiveProductByPK(input.getString("PRODUCT_ID"));
        
        String saleTag = "";
        /**
         * REQ201607220020 关于2016预存话费送VOLTE手机营销活动的开发需求
         * chenxy3 2016-08-26
         * "RG".equals(productInfo.getString("RSRV_TAG2") 红包专用
         * REQ201703070008 关于调整明星终端产品全网统一营销活动渠道办理方式的需求
         * "M".equals(productInfo.getString("RSRV_TAG2")) 
         * */
        if ("YX08".equals(input.getString("CAMPN_TYPE"))||"R".equals(productInfo.getString("RSRV_TAG2","")) 
                || "M".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "1"; // 买断过一次,再优惠购机

        if ("YX09".equals(input.getString("CAMPN_TYPE")))
            saleTag = "2"; // 虚拟供货的再优惠购机

        if ("G".equals(productInfo.getString("RSRV_TAG2")))
            saleTag = "3";

        
        IDataset hdhkActives = SaleActiveInfoQry.queryHdfkActivesByResNo(resNo);

        String callType = input.getString("CALL_TYPE");
        if (IDataUtil.isNotEmpty(hdhkActives) && !"4".equals(callType))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_29);
        }

        IDataset terminalDataset = HwTerminalCall.preOccupyTerminalByTerminalId(resNo, saleStaffId, serialNumber, saleTag, netOrderId);

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }

        IData returnData = convertTermianlInfo(terminalData);

        String buyType = input.getString("BUY_TYPE");

        String terminalState = returnData.getString("TERMINAL_STATE");

        if ("|5|6|7|8|C|".indexOf(terminalState) > -1)
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_29);
        }

        if (SaleActiveConst.TERMINAL_BUY_TYPE_GIFT.equals(buyType))
        {
            if ("4".equals(terminalState))
            {
                CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_30);
            }
        }

        return returnData;
    }

    /**
     * 根据终端串号返销
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public void releaseTerminalByResNo(IData input) throws Exception
    {
        IDataset resultSet = HwTerminalCall.releaseTerminalByTerminalId(input);

        IData resultData = resultSet.getData(0);
        String xResultCode = resultData.getString("X_RESULTCODE");

        if (!"0".equals(xResultCode))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, resultData.getString("X_RESULTINFO"));
        }
    }

    /**
     * 根据终端价格和终端生产厂商查询终端
     * @param input
     * @return
     * @throws Exception
     */
	public IDataset getTerminalByPriceOrBrand(IData input) throws Exception {
		
	    
	    IDataset terminalDataset = HwTerminalCall.getTerminalByPriceOrBrand(input);

        IData terminalData = terminalDataset.getData(0);

        if (!"0".equals(terminalData.getString("X_RESULTCODE")))
        {
            CSAppException.apperr(SaleActiveException.CRM_SALEACTIVE_25, terminalData.getString("X_RESULTINFO"));
        }
		ArrayList resIDList = charSplit(terminalData.getString("RESTYPEID",""), ',');
		ArrayList  resNameList = charSplit(terminalData.getString("RESTYPENAME",""), ',');
		ArrayList  parentIdList = charSplit(terminalData.getString("RESPARENTID",""), ',');
		ArrayList  typeCode = charSplit(terminalData.getString("TERMINAL_TYPE_CODE",""), ',');
		ArrayList  brandNameList = charSplit(terminalData.getString("BRANDNAME",""), ',');
		ArrayList  costList = charSplit(terminalData.getString("DEVICE_COST",""), ',');
		ArrayList  priceList = charSplit(terminalData.getString("PRICE",""), ',');
		IDataset returnResInfo = new DatasetList();
		
		for (int i = 0; i < resIDList.size(); i++) {
			IData returnData = new DataMap();
			returnData.put("DEVICE_TYPE_CODE", parentIdList.get(i));
			
			returnData.put("DEVICE_MODEL_CODE", typeCode.get(i));
			returnData.put("DEVICE_COST", costList.get(i));
			returnData.put("SALE_PRICE", priceList.get(i));
	        
			returnData.put("GOODS_EXPLAIN", brandNameList.get(i)+"|"+resNameList.get(i)+"|"+chargeMoney(String.valueOf(priceList.get(i))));
			
			if("1".equals(input.getString("QRY_TER_PRICE")))
			{
				returnData.put("GOODS_EXPLAIN", brandNameList.get(i)+"|"+resNameList.get(i)+"|"+chargeMoney(String.valueOf(costList.get(i)))+"|"+chargeMoney(String.valueOf(priceList.get(i))));
			}
			
			returnResInfo.add(returnData);
		}
		
        return returnResInfo;
	}
	
	/**
	 * 功能描述:分隔字符串
	 * @param str   源串
	 * @param split  分隔符
	 */
	public ArrayList charSplit(String str, char split) {
		if (str.startsWith(String.valueOf(split))) {
			str = str.substring(1, str.length());
		}
		if (str.endsWith(String.valueOf(split)) == false) {
			str = str + split;
		}
		char charStr[] = str.toCharArray();
		int start = 0;
		int end = 0;
		int num = 0;
		ArrayList tmp = new ArrayList();
		for (int i = 0; i < str.length(); i++) {
			if (charStr[i] == split) {
				end = i;
				tmp.add(str.substring(start, end));
				num++;
				start = end + 1;
			}
		}
		return tmp;
	}
	
	/**
	 * 功能描述:分隔字符串
	 * @param str源串
	 * @param split分隔符
	 */
	public String chargeMoney(String str) {
		String retnStr = "0元";
		if (str == null || "".equals(str)) {
			return retnStr;
		}
		try{
			float money= Float.parseFloat(str);
			money = money/100;
			retnStr = money+"元";
		}catch (Exception e) {
			retnStr = "0元";
		}

		return retnStr;
	}
}
