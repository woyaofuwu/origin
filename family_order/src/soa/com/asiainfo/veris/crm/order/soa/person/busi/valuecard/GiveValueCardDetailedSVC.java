package com.asiainfo.veris.crm.order.soa.person.busi.valuecard;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeFeeDeviceQry;

/**
 * 
 * 有价卡赠送清单
 * @author zyz
 * 
 *
 */
public class GiveValueCardDetailedSVC extends CSBizService{

	private static final long serialVersionUID = 1L;

	
	static Logger logger=Logger.getLogger(GiveValueCardDetailedSVC.class);
    
	/**
	 * 
	 * @param inparam
	 * @return
	 * @throws Exception
	 */
	public IDataset queryValueCardDetailedInfo(IData inparam) throws Exception{
    	
    	try {
        	String cityCode = inparam.getString("CITY_CODE","");
        	//审批工单号
        	String staffId = inparam.getString("STAFF_ID","");
        	//部门编码
        	String updateDepartId=inparam.getString("UPDATE_DEPART_ID");
        	
        	String startSaleTime = inparam.getString("START_DATE");
            if(StringUtils.isNotEmpty(startSaleTime)){
            	startSaleTime = startSaleTime+SysDateMgr.START_DATE_FOREVER;
            }	
            
        	String endSaleTime = inparam.getString("END_DATE");
            if(StringUtils.isNotEmpty(endSaleTime)){
            	 endSaleTime = endSaleTime+SysDateMgr.END_DATE;
            }
        
            //录入时间
        	String startTime5 = inparam.getString("START_RSRV_STR5_DATE");
            if(StringUtils.isNotEmpty(startTime5)){
            	startTime5 = startTime5+SysDateMgr.START_DATE_FOREVER;
            }	
        	String endTime5 = inparam.getString("END_RSRV_STR5_DATE");
            if(StringUtils.isNotEmpty(endTime5)){
            	endTime5 = endTime5+SysDateMgr.END_DATE;
            }
            
        	
        	//客户号码
        	String rsrvStr7=inparam.getString("RSRV_STR7");
        	
        	IDataset result = TradeFeeDeviceQry.qryValueCardDetailedByCondition(cityCode, staffId, startSaleTime, 
        			endSaleTime, updateDepartId, rsrvStr7,startTime5,endTime5,getPagination());
        	
/*        	if(IDataUtil.isNotEmpty(result)){
            	for (int i = 0,size=result.size(); i < size; i++) {
    				IData data = result.getData(i);
    				String deviceTypeCode = data.getString("DEVICE_TYPE_CODE","");
    				if(StringUtils.isNotBlank(deviceTypeCode)){					
    					String kindName = StaticUtil.getStaticValueDataSource(this.getVisit(),"res","TD_S_RESKIND",new java.lang.String [] {"EPARCHY_CODE","RES_KIND_CODE"},
    							"RES_KIND_NAME",new java.lang.String [] {"ZZZZ",deviceTypeCode});
    					data.put("KIND_NAME", kindName);
    				}
    				//有限日期
    				data.put("END_DATE", "20501230");
    				//销售标志
    				data.put("SALE_TAG", "已销售");
    			}
            }*/
        	return result;
		} catch (Exception e) {
			// TODO: handle exception
			if(logger.isInfoEnabled()) logger.info(e);
			throw e;
		}

    }

  
   
}
