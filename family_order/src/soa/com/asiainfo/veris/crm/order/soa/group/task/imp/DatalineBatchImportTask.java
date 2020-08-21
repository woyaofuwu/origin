package com.asiainfo.veris.crm.order.soa.group.task.imp;

import com.ailk.biz.impexp.ImportTaskExecutor;
import com.ailk.biz.util.StaticUtil;
import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.BatchImportException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class DatalineBatchImportTask extends ImportTaskExecutor {

    @Override
    public IDataset executeImport(IData data, IDataset dataset) throws Exception {
        if (IDataUtil.isEmpty(dataset)) {
        	CSAppException.apperr(BatchImportException.ESOP_BATCH_1);
        }
        IDataset importDatas = new DatasetList();
        String templetId = data.getString("TEMPLET_ID");
        String bizRange = data.getString("pattr_BIZRANGE");
        String productId = data.getString("PRODUCT_ID");
		for (int i = 0; i < dataset.size(); i++) {
			IData importData = dataset.getData(i);
			if(productId.equals("7011")||productId.equals("70111")||productId.equals("70112")) {
				String widthStr = importData.getString("pattr_BANDWIDTH");
				int width=Integer.parseInt(widthStr);
				if(width<10){
		        	CSAppException.apperr(BatchImportException.ESOP_BATCH_0,"带宽数值需大于或等于10(M)");

				}
			}
			//快开批量判断
			if(templetId.equals("EDIRECTLINEOPEN")) {
				if(productId.equals("7011")||productId.equals("70111")||productId.equals("70112")) {
					if(productId.equals("7011")||productId.equals("70111")||productId.equals("70112")) {
						//互联网判断IP地址类型和对应的数量是否符合选择项
						String ipType = importData.getString("pattr_IPTYPE");
						String cusAppServIpAddNum = importData.getString("pattr_CUSAPPSERVIPADDNUM");
						String cusAppServIpV4 = importData.getString("pattr_CUSAPPSERVIPV4ADDNUM");
						String cusAppServIpV6 = importData.getString("pattr_CUSAPPSERVIPV6ADDNUM");
						if(ipType.equals("IPV4和IPV6")) {
							if(!cusAppServIpAddNum.equals("0")) {
								CSAppException.apperr(BatchImportException.ESOP_BATCH_2,ipType);
							}
						}else{
							if(!cusAppServIpV4.equals("0")||!cusAppServIpV6.equals("0")) {
								CSAppException.apperr(BatchImportException.ESOP_BATCH_3,ipType);
							}
						}
					}
				}
				importData.put("pattr_LINEOPENTAG", "1");//快开标记
				importData.put("pattr_ISCOVER", "Y");//是否覆盖标记 Y:快开 N:选择了勘察
				importData.put("IS_MODIFY_TAG", 0);//未修改标记
				importData.put("NOTIN_MARKETING_TAG", "0");//不是营销活动标记
				importDatas.add(importData);
			}else if(templetId.equals("ERESOURCECONFIRMZHZG")){
				String isPreOccupy = importData.getString("pattr_ISPREOCCUPY");
				IDataset result = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "IF_CHOOSE_CONFCRM", isPreOccupy});
				importData.put("pattr_ISPREOCCUPY", result.getData(0).getString("DATA_ID"));
				String routeMode = importData.getString("pattr_ROUTEMODE");
				IDataset result2 = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", null, new String[]{ "TYPE_ID", "DATA_NAME" }, new String[]{ "ROUTEMODE", routeMode});
				if(!result2.isEmpty()) {
					importData.put("pattr_ROUTEMODE", result2.getData(0).getString("DATA_ID"));
				}
				if(productId.equals("7012")||productId.equals("70121")||productId.equals("70122")) {
					String cityA = importData.getString("pattr_CITYA");
					String cityZ = importData.getString("pattr_CITYZ");
					if(bizRange.equals("本地市") && !cityA.equals(cityZ)) {
						CSAppException.apperr(BatchImportException.ESOP_BATCH_8,bizRange);
					}else if(bizRange.equals("省内跨地市") && cityA.equals(cityZ)) {
						CSAppException.apperr(BatchImportException.ESOP_BATCH_9,bizRange);
					}
				}
				importData.put("pattr_ENROUTE", "否");//是否在途标记
				importDatas.add(importData);
			}
		}
        SharedCache.set("DATALINE_INFOS", importDatas);
        return null;
    }

}
