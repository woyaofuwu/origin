
package com.asiainfo.veris.crm.order.soa.person.busi.bank;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;

public class BankSVC extends CSBizService
{

	private static Logger logger = Logger.getLogger(BankSVC.class);

    private static final long serialVersionUID = 1L;

    /**
	 * 查询绑定关系
	 * @param pd
	 * @param param
	 * @throws Exception
	 * @author wukw3
	 */
    public IDataset getBankBindDatas(IData input) throws Exception {
    	
    	BankBean bean = (BankBean) BeanManager.createBean(BankBean.class);
    	
        IDataset ret = bean.getBankBindDatas(input);
        
        return ret;
    	
    }
    
    /**
  	 * 网厅查询绑定关系
  	 * @param pd
  	 * @param param
  	 * @throws Exception
  	 * @author wukw3
  	 */
      public IData getNetBankBindDatas(IData input) throws Exception {
      	
    	  IData inParam = new DataMap();
    	  UcaData uca = null;
          uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
    	  
      	BankBean bean = (BankBean) BeanManager.createBean(BankBean.class);
      	
      	inParam.put("USER_ID", uca.getUserId());
        IData ret = bean.getNetBankBindDatas(inParam);
          
        return ret;
      	
      }

}
