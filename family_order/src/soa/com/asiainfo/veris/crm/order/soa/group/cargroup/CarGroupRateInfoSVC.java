
package com.asiainfo.veris.crm.order.soa.group.cargroup;


import org.apache.log4j.Logger;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;



public class CarGroupRateInfoSVC extends CSBizService
{
	private static final Logger logger = Logger.getLogger(CarGroupRateInfoSVC.class.getName());

    private static final long serialVersionUID = 1L;
    
  

    
    /**
     * 车联网--由集团客户ID查看折扣率和审批信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryByECID(IData input) throws Exception
    {
        return  CarGroupRateInfoBean.queryByECID(input,getPagination());
    }
    /**
     * 车联网--新增集团客户的折扣率
     * @param input
     * @return
     * @throws Exception
     */
    public IData addCarGroupRateInfo(IData input) throws Exception
    {
    	

		CarGroupRateInfoBean bean = BeanManager.createBean(CarGroupRateInfoBean.class);
		boolean flag = bean.addCarGroupRateInfo(input);
		
		IData result = new DataMap();
		result.put("RESULT_CODE", flag ? "0000" : "9999");	
		result.put("EC_ID", input.getString("EC_ID",""));
		return result;
    }
    
    
    /**
     * 审批反馈接口
     * @param Rslt
     * @return
     * @throws Exception
     */
    public int updateRslt(IData Rslt) throws Exception
    {
    	IData param=new DataMap();
    	logger.debug("----Rslt:----"+Rslt);
    	IDataset additionalDocList = Rslt.getDataset("DOC_NAME");
    	logger.debug("----additionalDocList:----"+additionalDocList);
    	CarGroupRateInfoBean bean = BeanManager.createBean(CarGroupRateInfoBean.class);
    	param.put("OPR_SEQ",Rslt.getString("OPR_SEQ",""));
    	param.put("APPROVAL_RSLT",Rslt.getString("APPROVAL_RSLT",""));
    	param.put("APPROVAL_NO",Rslt.getString("APPROVAL_NUM",""));
    	param.put("APPROVAL_COMM",Rslt.getString("COMMENTS",""));
    	String doc = "";
    	if(IDataUtil.isNotEmpty(additionalDocList)){
    		for (int i = 0; i < additionalDocList.size(); i++) {
            	//param.put("APPROVAL_DOC",Rslt.getString("CONVID",""));
        		doc = doc + "-" + additionalDocList.get(i).toString();
    		}
    	}//判断是否为只下发了一个附件，如果为空则说明没有下发附件
    	else if(StringUtils.isNotBlank(Rslt.getString("DOC_NAME"))){
    		doc = "-" +Rslt.getString("DOC_NAME");
    	}
 
    	param.put("APPROVAL_DOC",doc);
		int result = bean.updateCarGroupRateInfo(param);
		return result;
    }
    
}
