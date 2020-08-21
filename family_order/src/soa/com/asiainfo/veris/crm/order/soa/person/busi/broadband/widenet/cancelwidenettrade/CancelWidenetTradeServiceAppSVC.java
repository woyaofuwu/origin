/**
 * 
 */

package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.cancelwidenettrade;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;


/**
 * REQ201609190029_优化家庭宽带装机退单分类内容
 * @author zhuoyingzhi
 * 20161019
 *
 */
public class CancelWidenetTradeServiceAppSVC extends CSBizService
{
	
	private Logger log=Logger.getLogger(CancelWidenetTradeServiceAppSVC.class);

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;
    

    /**
     * 提供给外部接口调用
     * <br/>
     * 查询  TAG为1 撤单原因      2 二级原因 
     * @param input
     * @return
     * @throws Exception
     */
	public IData qryCancelReasonInfo(IData input) throws Exception{
		
	    IData result=new DataMap();
    	try {
    		IDataset iDataset=new DatasetList();
    		String tag=input.getString("TAG");
    		if("1".equals(tag)){
    			//撤单原因 
    			 iDataset=StaticUtil.getStaticList("WIDE_CANCEL_REASON");
    		}else if("2".equals(tag)){
    			//二级原因
    		     String  pdata_id=input.getString("PDATAID");
    		     iDataset=StaticUtil.getStaticListByParent("WIDE_CANCEL_REASON_RELATION", pdata_id);
    		}
    		if(IDataUtil.isNotEmpty(iDataset)){
    			String dataName="";
    			String dataId="";
    			for(int i=0;i<iDataset.size();i++){
    				IData data=iDataset.getData(i);
    				if(i==(iDataset.size()-1)){
    					dataName=dataName+data.getString("DATA_NAME");
    					dataId=dataId+data.getString("DATA_ID");
    				}else{
    					dataName=dataName+data.getString("DATA_NAME")+"<@>";
    					dataId=dataId+data.getString("DATA_ID")+"<@>";
    				}
    			}
    			result.put("DATA_NAME", dataName);
    			result.put("DATA_ID", dataId);
    		}
    		return result;
		} catch (Exception e) {
		     //log.info("(e.getMessage());
		     throw e;
		}
    }
	

}
