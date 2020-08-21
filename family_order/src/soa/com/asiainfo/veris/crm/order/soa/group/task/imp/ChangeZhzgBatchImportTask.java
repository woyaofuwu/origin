
package com.asiainfo.veris.crm.order.soa.group.task.imp;

import java.util.Iterator;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatchImportException;
import com.asiainfo.veris.crm.order.pub.exception.ConfCrmException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class ChangeZhzgBatchImportTask extends ImportTaskExecutor
{

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception
    {
    	//判断导入Excel必填字段是否都已填入
        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(ConfCrmException.CONF_CRM_1);
        }
        String bizRange = data.getString("pattr_BIZRANGE");
        String productId = data.getString("PRODUCT_ID");
        //获得正确的专线实例号
        IDataset lineNoInfos = new DatasetList(data.getString("CHANGEDATALINENOS"));
        for (int j = 0; j < dataset.size(); j++) {
        	boolean flag = false;
        	String inputProductNO = null;
			String cityA ="";
			String cityZ ="";
			IData inputData = dataset.getData(j);
			Iterator<String> itr2 = inputData.keySet().iterator();
			while (itr2.hasNext()) {
				String attrCode2 = itr2.next();
				if(attrCode2.equals("pattr_TRADEID")) {
					inputProductNO = inputData.getString(attrCode2);
				        for (int i = 0; i < lineNoInfos.size(); i++) {
							IData lineNoInfo = lineNoInfos.getData(i);
							Iterator<String> itr = lineNoInfo.keySet().iterator();
							while (itr.hasNext()) {
								String attrCode = itr.next();
								String productNO = lineNoInfo.getString(attrCode);
								if(attrCode.equals("TRADEID")) {
									if(inputProductNO.equals(productNO)) {
										flag = true;
										break;
									}
								}
							}
						}
				}else if(attrCode2.equals("pattr_CITYA")) {
					cityA = inputData.getString(attrCode2);
				}else if(attrCode2.equals("pattr_CITYZ")) {
					cityZ = inputData.getString(attrCode2);
				}
			}
			if(!flag) {
				CSAppException.apperr(ConfCrmException.CONF_CRM_20,inputProductNO);
			}
			if(productId.equals("7012")) {
				if(bizRange.equals("本地市") && !cityA.equals(cityZ)) {
					CSAppException.apperr(BatchImportException.ESOP_BATCH_8,bizRange);
				}else if(bizRange.equals("省内跨地市") && cityA.equals(cityZ)) {
					CSAppException.apperr(BatchImportException.ESOP_BATCH_9,bizRange);
				}
			}
		}
        IDataset newDataset = new DatasetList();
        for (int k = 0; k < dataset.size(); k++) {
        	IData newTempData = new DataMap();
			IData tempData = dataset.getData(k);
			Iterator<String> itr = tempData.keySet().iterator();
			while (itr.hasNext()) {
				String attrCode = itr.next();
				String attrValue = tempData.getString(attrCode);
				if(attrCode.equals("pattr_TRADEID")) {
					newTempData.put(attrCode, attrValue);
					newTempData.put("pattr_PRODUCTNO", attrValue);
				}else if(attrCode.equals("pattr_ROUTEMODE")) {
					attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "ROUTEMODE", attrValue });
					newTempData.put(attrCode, attrValue);
				}else if(attrCode.equals("pattr_ISPREOCCUPY")) {
					attrValue = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[] { "TYPE_ID", "DATA_NAME" }, "DATA_ID", new String[] { "IF_CHOOSE_CONFCRM", attrValue });
					newTempData.put(attrCode, attrValue);
				}else {
					newTempData.put(attrCode, attrValue);
				}
			}
			newDataset.add(newTempData);
		}
        SharedCache.set("CHANGE_DATALINE_INFOS", newDataset);
        return null;
    }

}
