
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.widenet.widenetbook;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.rule.CRMMVELMiscCache;
import com.asiainfo.veris.crm.order.soa.frame.bcf.bof.util.mvel.script.MVELExecutor;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * 
 * 
 * @author zyz
 *
 */
public class WideNetBookSVC extends CSBizService
{
   /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static Logger log=Logger.getLogger(WideNetBookSVC.class);
   
   
   
    /**
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
   public  IData checkValidCodeNum(IData input) throws Exception{
	   IData result=new DataMap();
	   try {
		   WideNetBookBean bean= BeanManager.createBean(WideNetBookBean.class);
		   IDataset  dataset=bean.checkValidCodeNum(input);
		    //通过模版获取,最大个数
		    int maxNum=getWidenetNum();
		    result.put("msg", "您的验证码使用次数已超限");
		   if(IDataUtil.isNotEmpty(dataset)){
			    int  widenetNum=dataset.getData(0).getInt("WINDENTNUM");
			    if(widenetNum  < maxNum ){
			    	//通过
			    	result.put("stauts", "0");
			    	result.put("msg", "校验通过");
			    }else{
			    	result.put("stauts", "1");
			    }
		   }else{
			      result.put("stauts", "1");
		   }
		} catch (Exception e) {
			result.put("stauts", "1");
			result.put("msg", e.getMessage());
			//log.info("(e);
			throw e;
		}
	   return result;
   }
   
     /**
      * 
      * @param templateId
      * @return
      * @throws Exception
      */
	 public int getWidenetNum() throws Exception
	    {
		  try {
		         IDataset dsdefault = CommparaInfoQry.getCommparaAllCol("CSM", "6182", "0", "0898");
		         int Lcount = Integer.parseInt(dsdefault.getData(0).getString("PARA_CODE1", "5"));
		         return Lcount;
			} catch (Exception e) {
				// TODO: handle exception
				//log.info("(e);
				throw e;
			}
	    }
	 
	 /**
	  * 校验验证码是否存在
	  * @return
	  * @throws Exception
	  */
	public IData checkValidCodeIsExist(IData input)throws Exception{
		IData result=new DataMap();
		try {
			WideNetBookBean bean= BeanManager.createBean(WideNetBookBean.class);
			IDataset  dataset=bean.checkValidCodeIsExist(input,"IS50KDMSG");
			   if(IDataUtil.isNotEmpty(dataset)){
				   //验证码存在
			    	result.put("stauts", "0");
			    	result.put("msg", "验证码存在");
			   }else{
				  IDataset data100=bean.checkValidCodeIsExist(input,"IS100KDMSG");
				  if(IDataUtil.isNotEmpty(data100)){
				    	result.put("stauts", "0");
				    	result.put("msg", "验证码存在");
				  }else{
					   //不存在
				    	result.put("stauts", "1");
				    	result.put("msg", "验证码不存在");
				  }
			   }
		} catch (Exception e) {
	    	result.put("stauts", "1");
	    	result.put("msg", e.getMessage());
			//log.info("(e);
			throw e;
		}
		return result;
	}

}
