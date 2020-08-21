package com.asiainfo.veris.crm.order.soa.person.busi.saleactiveactivate;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;

public class SaleActiveExtendBean extends CSBizBean {
  
	public IDataset queryExtendSaleActive(IData params) throws Exception  
	{
		IDataset saleActives = new DatasetList();
		String userId = params.getString("USER_ID");
		IDataset allSaleActives = UserSaleActiveInfoQry.queryAllSaleActiveByUserId(userId);

        if (IDataUtil.isNotEmpty(allSaleActives))
        {
        	IDataset ids = CommparaInfoQry.getCommByParaAttr("CSM", "9977", CSBizBean.getUserEparchyCode());
        	if (IDataUtil.isNotEmpty(ids)){
        		String strStaffId = getVisit().getStaffId();
            	boolean bExtend = StaffPrivUtil.isFuncDataPriv(strStaffId, "SALEACTIVE_EXTEND");
            	if( "SUPERUSR".equalsIgnoreCase(strStaffId) ){
            		bExtend = true;
            	}
        		for (int i = 0; i < ids.size(); i++) {
					IData id = ids.getData(i);
					String strProductId = id.getString("PARAM_CODE", "");
					String strDayCount = id.getString("PARA_CODE1", "");
					for (int j = 0; j < allSaleActives.size(); j++) {
						IData allSaleActive = allSaleActives.getData(j);
						String strSaleActiveId = allSaleActive.getString("PRODUCT_ID", "");
						if( strSaleActiveId.equals(strProductId) ){
							if( bExtend ){
								allSaleActive.put("PRODT_FLAG", "1");
							}else{
								allSaleActive.put("PRODT_FLAG", "0");
							}
							allSaleActive.put("DAY_COUNT", strDayCount);
							saleActives.add(allSaleActive);
						}
					}
				}
        	}
        }
        return saleActives;
	}
	
	public IDataset tradeReg(IData params) throws Exception  
	{
		String strSn = params.getString("SERIAL_NUMBER");
		String strRelaTradeId = params.getString("RELATION_TRADE_ID");
		String strEndDate = params.getString("END_DATE");
		String strRemark = params.getString("REMARK");
		String strStaffId = getVisit().getStaffId();
		
		UcaData uca = UcaDataFactory.getNormalUca(strSn); //获取三户资料方法
		//String strUserId = uca.getUserId();
		//SaleActiveTradeData activeData = uca.getUserSaleActiveByRelaTradeId(strRelaTradeId);
		IDataset IDactive = UserSaleActiveInfoQry.queryUserRelationAllSaleActive(uca.getUserId(), strRelaTradeId);
		if( IDataUtil.isNotEmpty(IDactive) ){// activeData != null
			//SaleActiveTradeData activeData = new SaleActiveTradeData(IDactive.getData(0));
			IData data = IDactive.getData(0);
			/*data.put("USER_ID", strUserId);
	        data.put("INST_ID", IDactive.getData(0).getString("INST_ID"));
	        data.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));*/
	        data.put("END_DATE", strEndDate);
	        data.put("REMARK", strRemark);
	        data.put("TRADE_STAFF_ID", strStaffId);
	        data.put("ACCEPT_DATE", SysDateMgr.getSysTime());
	        
	        Dao.update("TF_F_USER_SALE_ACTIVE", data, new String[]
	        { "INST_ID", "PARTITION_ID" });
			
	        /*IData param = new DataMap();
			param.put("RELATION_TRADE_ID", strRelaTradeId);
			param.put("USER_ID", strUserId);
			param.put("PARTITION_ID", strUserId.substring(strUserId.length() - 4));
			param.put("INST_ID", IDactive.getData(0).getString("INST_ID"));
            param.put("END_DATE", strEndDate);
            param.put("REMARK", strRemark);
            param.put("TRADE_STAFF_ID", strStaffId);
            param.put("ACCEPT_DATE", SysDateMgr.getSysTime());
            Dao.save("TF_F_USER_SALE_ACTIVE", param);*/
		}else{
			CSAppException.apperr(CrmCommException.CRM_COMM_103, "找不到选择活动，操作失败!");
		}
        return new DatasetList();
	}
}
