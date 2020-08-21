
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;

/**
 * 
 * @author zyz
 * 
 */
public class WideNetBookBean extends CSBizBean
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(WideNetBookBean.class);
   
   
   
    /**
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
   public  IDataset checkValidCodeNum(IData input) throws Exception{
	   try {
		   IData param = new DataMap();
		   //验证码
		   param.put("RSRV_STR1", input.getString("validCode"));
		   IDataset  dataset=Dao.qryByCode("TF_B_TRADE_OTHER", "SEL_WIDENET_BY_VALIDCODE", param);
		   return dataset;
		} catch (Exception e) {
			// TODO: handle exception
			//log.info("(e);
			throw e;
		}
   }
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
 	public IDataset  checkValidCodeIsExist(IData input,String  flag) throws Exception{
 		try {
 	     IDataset  dataset=new DatasetList();
		  IData param = new DataMap();
		   //验证码
		   if("IS50KDMSG".equals(flag)){
			   param.put("IS50KDMSG", input.getString("validCode"));
			   dataset=Dao.qryByCode("T_OLYMPIC_USER", "SEL_VALIDCODE_IS_EXIST_50", param);
		   }
		   if("IS100KDMSG".equals(flag)){
			   param.put("IS100KDMSG", input.getString("validCode"));
			   dataset=Dao.qryByCode("T_OLYMPIC_USER", "SEL_VALIDCODE_IS_EXIST_100", param);
		   }
		   return dataset;
		} catch (Exception e) {
			//log.info("(e);
			throw e;
		}
 	}
   

}
