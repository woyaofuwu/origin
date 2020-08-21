
package com.asiainfo.veris.crm.order.soa.person.busi.broadband.nophonewidenet.nophonewidemodem;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.ftthmodemmanage.FTTHModemManageBean;
import com.asiainfo.veris.crm.order.soa.person.common.util.NoPhoneTradeUtil;

public class NoPhoneModemManageSVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(NoPhoneModemManageSVC.class);

    private static final long serialVersionUID = 1L;

    /**
     * 查询是否存在未处理的错单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset checkIfNoPhoneUser(IData input) throws Exception
    {
    	String serialNum=input.getString("SERIAL_NUMBER","");
    	if(!serialNum.startsWith("KD_")){
    	    serialNum = "KD_"+serialNum;
    	}
        boolean isNoPhoneUser= NoPhoneTradeUtil.checkIfNoPhoneUserNew(serialNum);
        
        if(isNoPhoneUser){}else{
        	CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户非无手机用户，无法使用该业务。"); 
        }
        return new DatasetList();
    }
    
    /**
     * 查询是否存在未处理的错单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserTradeWWG(IData input) throws Exception
    {
    	NoPhoneModemManageBean bean= BeanManager.createBean(NoPhoneModemManageBean.class); 
        return bean.getUserTradeWWG(input);
    }
    
    /**
     * 查询是否存在未处理的错单
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset getUserWideInfo(IData input) throws Exception
    {
    	//宽带地址信息
        IDataset dataset = CSAppCall.call("CS.WidenetInfoQuerySVC.getUserWidenetInfo", input);
        String widetype = dataset.getData(0).getString("RSRV_STR2");
        if(!"3".equals(widetype) && !"5".equals(widetype)){
        	//需要校验FTTB，是否有未退还的光猫
        	if("1".equals(input.getString("WARN_TAG"))){
        		IDataset infos =CSAppCall.call("SS.NoPhoneWidenetMoveSVC.getModelInfo", input);
        		boolean isNeedWarn=true;
        		for(int i=0;i<infos.size();i++){
        			//rsrv_tag1--申领模式  0租赁，1购买，2赠送，3自备  rsrv_str9--移机、拆机未退光猫标志：1.移机未退光猫  2.拆机未退光猫
        			if(("0".equals(infos.getData(i).getString("RSRV_TAG1"))||"".equals(infos.getData(i).getString("RSRV_TAG1")))
        	    			&&("1".equals(infos.getData(i).getString("RSRV_STR9")))){
        				dataset.getData(0).put("H_TO_B", "1");
        				isNeedWarn=false;
        	    	}
        		}
        		if(isNeedWarn){
    				CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户非FTTH宽带，无法使用该业务。");
        		}
        	}else{
        		CSAppException.apperr(CrmCommException.CRM_COMM_103, "当前用户非FTTH宽带，无法使用该业务。");
        	}
        }
        return dataset;
    }
    
    /** 
     * 判断用户该收取的押金金额 
     * */

    public IData checkFTTHdeposit(IData inParam) throws Exception{
      	IData rtnData=new DataMap();
      	String deposit="";
      	String serialNumber=inParam.getString("SERIAL_NUMBER","");
      	String applyType = inParam.getString("APPLYTYPE","");
      	FTTHModemManageBean bean= BeanManager.createBean(FTTHModemManageBean.class);
      	if(StringUtils.isBlank(applyType)){
      		rtnData.put("APPLYTYPE","");
          	rtnData.put("DEPOSIT","");
          	rtnData.put("X_INFO","2");
          	return rtnData;
      	}else{
      		if("1".equals(applyType)){//申领模式为购买，查询购买金额
      			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", "3", "0898");
  	      		deposit=paras.getData(0).getString("PARA_CODE1");
      		}else if("0".equals(applyType)){//租赁  
          		IData param=new DataMap();
          		param.put("SERIAL_NUMBER", serialNumber);
          		String payType="1";//购买模式，因现在都是统一100，所以随便给个值即可
          		////2、判断用户存在那种产品，只开通FTTH宽带押金200；开通宽带且办理宽带1+押金100    		
          		//根据用户判断是否开通FTTH宽带和宽带1+，返回值是commpara表attr=6131的内容。
          		if("0".equals(payType)){
          			CSAppException.appError("61310", "未办理FTTH宽带业务，无法办理租赁光猫。");
          		}else{
          	      		IDataset datas=bean.getCustModermInfo(param); 
    	      	      	if(datas!=null && datas.size()>0){
    	              		rtnData.put("APPLYTYPE","");
    	                  	rtnData.put("DEPOSIT","");
    	                  	rtnData.put("X_INFO","1");
    	                  	return rtnData;
    	              	}else{
    	              		//先取押金金额commpara表param_attr=6131
    	          			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", payType, "0898");
    	          			deposit=paras.getData(0).getString("PARA_CODE1");
    	              	}
          	      	}
      		}else if("2".equals(applyType)){//赠送
      			IData param=new DataMap();
          		param.put("SERIAL_NUMBER", serialNumber);
      			IDataset datas=bean.getCustModermInfo(param); 
      	      	if(datas!=null && datas.size()>0){
              		rtnData.put("APPLYTYPE","");
                  	rtnData.put("DEPOSIT","");
                  	rtnData.put("X_INFO","1");
                  	return rtnData;
              	}else{
              		deposit = "0";
              	} 
  	      	}
      		rtnData.put("APPLYTYPE",applyType);
          	rtnData.put("DEPOSIT", Integer.parseInt(deposit)/100);
          	rtnData.put("X_INFO","0");
          	return rtnData;
      	}
      } 
}
