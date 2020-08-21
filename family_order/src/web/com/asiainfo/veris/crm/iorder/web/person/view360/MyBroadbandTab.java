
package com.asiainfo.veris.crm.iorder.web.person.view360;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class MyBroadbandTab extends PersonBasePage {

    /**
     * 客户资料综合查询 - 宽带信息查询
     *
     * @param cycle
     * @throws Exception
     * @author huanghui@asiainfo.com
     * @date 2014-08-15
     */
    public void queryInfo(IRequestCycle cycle) throws Exception {
        IData data = getData();
        String userId = data.getString("USER_ID", "");
        String serialNumber = data.getString("SERIAL_NUMBER", "");

        // 查询宽带信息
        if (StringUtils.isNotBlank(userId)) {
        	String fuzzyFlag = data.getString("PARAM");
        	if("no".equals(fuzzyFlag)){//免模糊化
        		data.put("X_DATA_NOT_FUZZY", true);
        	}
        	IDataset broadbandInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.queryWideUserInfo", data);
            if (IDataUtil.isNotEmpty(broadbandInfo)) {
                IData productSetInfo = broadbandInfo.getData(0).getData("USER_INFO").getData("PRODUCT_SET");
                if (IDataUtil.isNotEmpty(productSetInfo)) {
                    String brandName = UpcViewCall.getBrandNameByBrandCode(this, productSetInfo.getString("BRAND_CODE", ""));
                    brandName = StringUtils.isNotBlank(brandName) ? brandName : "";
                    productSetInfo.put("BRAND_NAME", brandName);
                }
                setBroadbandInfo(broadbandInfo.getData(0));
            }
        }

        // 查询固话信息
        if (StringUtils.isNotBlank(serialNumber)) {
            IData IMS;
            try {
                IMS = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getIMSInfoBySerialNumber", data);
            } catch (Exception e) {
                return;
            }
            if (IDataUtil.isNotEmpty(IMS)) {
                String userIdB = IMS.getString("USER_ID_B", "");
                if (StringUtils.isNotBlank(userIdB)) {
                    IData inParam = new DataMap();
                    inParam.put("USER_ID", userIdB);
                    inParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
                    IData IMSProduct = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryUserProductInfoByUserId", inParam);
                    if (IDataUtil.isNotEmpty(IMSProduct)) {
                        IData IMSInfo = new DataMap();
                        IMSInfo.put("IMS_NUMBER", IMS.getString("SERIAL_NUMBER_B", ""));
                        IMSInfo.put("IMS_BRAND", IMSProduct.getString("BRAND_CODE", ""));
                        IMSInfo.put("IMS_PRODUCT", IMSProduct.getString("RSRV_STR5", ""));                       
                        setIMSInfo(IMSInfo);
                    }
                }
            }
            
            //根据IMS固话查询用户手机号码
            try {
                IMS = CSViewCall.callone(this, "SS.ChangeSvcStateSVC.getSerialNumberInfoByIMSInfo", data);
            } catch (Exception e) {
                return;
            }
            if (IDataUtil.isNotEmpty(IMS)) {
                String userIdB = IMS.getString("USER_ID_B", "");
                if (StringUtils.isNotBlank(userIdB)) {
                    IData inParam = new DataMap();
                    inParam.put("USER_ID", userIdB);
                    inParam.put(Route.ROUTE_EPARCHY_CODE, data.getString("EPARCHY_CODE", getTradeEparchyCode()));
                    IData IMSProduct = CSViewCall.callone(this, "SS.GetUser360ViewSVC.qryUserProductInfoByUserId", inParam);
                    if (IDataUtil.isNotEmpty(IMSProduct)) {
                        IData IMSInfo = new DataMap();
                        IMSInfo.put("IMS_NUMBER", IMS.getString("SERIAL_NUMBER_B", ""));
                        IMSInfo.put("IMS_BRAND", IMSProduct.getString("BRAND_CODE", ""));
                        IMSInfo.put("IMS_PRODUCT", IMSProduct.getString("RSRV_STR5", ""));
                        IMSInfo.put("MOBILIE_TAG", "Y");
                        setIMSInfo(IMSInfo);
                    }
                }
            }
            
            //查询智能组网的信息
            IDataset dataset = CSViewCall.call(this, "SS.QuerySmartNetworkRegSVC.querySmartNetwork", data);     
            setZNZWInfos(dataset);
        }
    }

    public abstract void setBroadbandInfo(IData broadbandInfo);

    public abstract void setIMSInfo(IData IMSInfo);
    
    public abstract void setZNZWInfos(IDataset ZNZWInfos);
}