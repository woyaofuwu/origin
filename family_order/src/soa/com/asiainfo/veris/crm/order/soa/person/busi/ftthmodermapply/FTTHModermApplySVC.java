
package com.asiainfo.veris.crm.order.soa.person.busi.ftthmodermapply;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.WidenetException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.broadband.WidenetInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ201505210004关于新增FTTH光猫办理流程的需求
 * chenxy3
 * 2015-6-8
 * 入参：手机号码SERIAL_NUMBER、光猫串号MODERM_NUMBER
 * 返回：0成功，其他失败。
 * */
public class FTTHModermApplySVC extends CSBizService
{
    protected static Logger log = Logger.getLogger(FTTHModermApplySVC.class);

    private static final long serialVersionUID = 1L;

    public IData updModermNumber(IData userInfo) throws Exception
    {
    	FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
    	IData returnInfo = new DataMap();
    	IDataset userInfos=bean.getUserModermInfo(userInfo);
        if(userInfos!=null && userInfos.size()>0){ 
        	String moderm=userInfos.getData(0).getString("RSRV_STR1");
        	if(moderm!=null && !"".equals(moderm)){
        		returnInfo.put("X_RESULTCODE", "6131");
                returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】已存在光猫信息！");
        	}else{
        		bean.updModermNumber(userInfo);
            	returnInfo.put("X_RESULTCODE", "0");
                returnInfo.put("X_RESULTINFO", "办理成功!"); 
        	}
        }else{
        	returnInfo.put("X_RESULTCODE", "6130");
            returnInfo.put("X_RESULTINFO", "该用户【"+userInfo.getString("SERIAL_NUMBER")+"】还没有办理光猫申领业务!"); 
        }    	
        return returnInfo;
    }
    
    /**
     * AEE定时任务
     * 开户失败 
     * */
    public void checkWilenFailUser(IData userInfo) throws Exception{
    	FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
    	bean.checkWilenFailUser(userInfo);
    }
    
    /**
     * AEE定时任务
     * 开户失败 --返还光猫单独处理接口
     * SS.FTTHModermApplySVC.returnFtthModem
     * 用法：先检查TF_BH_TRADE_FTTH表是否存在需要退的TRADE_ID，存在就改标记成5，不存在就插入一条标记5的TRADE_ID记录。
     * */
    public void returnFtthModem(IData userInfo) throws Exception{
    	IData param=new DataMap(); 
    	FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
    	bean.getTempFtthUserReturnModem(param);
    }
    /**
     * AEE定时任务
     * 满3年用户
     * （未完工）
     * */
    public void checkThreeYearsUser(IData userInfo) throws Exception{
    	FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
    	bean.checkThreeYearsUser(userInfo);
    }
    /**
     * REQ201510260003 光猫申领提示优化【2015下岛优化】
     * 判断是否FTTH宽带用户。
     * chenxy3 20151027
     * */
    public IData checkIfFtthNewUser(IData inParam) throws Exception{
    	IData rtnData=new DataMap();
    	 
    	boolean isExist=false; 
    	
    	
//		IDataset wilens=FTTHModermApplyBean.getUserFTTHProd(inParam);
        
    	String serialNumber = inParam.getString("SERIAL_NUMBER");
        
        if (!serialNumber.startsWith("KD_"))
        {
            serialNumber = "KD_" + serialNumber;
        }
        
        IDataset widenetInfos = WidenetInfoQry.getUserWidenetInfoBySerialNumber(serialNumber);
        
        if (IDataUtil.isNotEmpty(widenetInfos))
        {
        	//3,5分别为移动FTTH,铁通FTTH宽带
        	if("3".equals(widenetInfos.getData(0).getString("RSRV_STR2"))
                    || "5".equals(widenetInfos.getData(0).getString("RSRV_STR2")))
            {
    			isExist=true;
    		}
        }else{
			IDataset dataset=FTTHModermApplyBean.getUserFTTHProdWWG(inParam);
			if(dataset!=null && dataset.size()>0){
				isExist=true;
			}
		}
		rtnData.put("IS_FTTH_USER", isExist);
		return rtnData;
    }
    
    /**
     * REQ201510260003 光猫申领提示优化【2015下岛优化】
     * 判断是否宽带1+产品
     * chenxy3 20151027
     * */
    public IData checkProdInCommpara(IData inParam) throws Exception{
    	IData rtnData=new DataMap();
    	String inTag="0";
    	String productId=inParam.getString("PRODUCT_ID","");  
    	IDataset commparas=CommparaInfoQry.getCommparaAllColByParser("CSM","1025", productId, "0898");
    	if(commparas!=null && commparas.size()>0){
    		inTag="1";
    	}
    	rtnData.put("IN_TAG", inTag);
    	return rtnData;
    }
    
    /**
     * REQ201510270009 FTTH光猫申领押金金额显示优化【2015业务挑刺】
     * 判断用户该收取的押金金额
     * chenxy3 20151029
     * */
    public IData checkFTTHdeposit(IData inParam) throws Exception{
    	IData rtnData=new DataMap();
    	String deposit="";
    	String serialNumber=inParam.getString("SERIAL_NUMBER","");
    	//1、先判断用户存在那种产品，只开通FTTH宽带押金200；开通宽带且办理宽带1+押金100    		
		FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
		IData param=new DataMap();
		param.put("SERIAL_NUMBER", serialNumber);
		//2、根据用户判断是否开通FTTH宽带和宽带1+，返回值是commpara表attr=6131的内容。
		String payType=bean.getPayMoneyType(param);
		if("0".equals(payType)){
			CSAppException.appError("61310", "未办理FTTH宽带业务，无法办理申领光猫。");
		}else{
			//先取押金金额commpara表param_attr=6131
			IDataset paras=CommparaInfoQry.getCommparaAllCol("CSM", "6131", payType, "0898");
			deposit=paras.getData(0).getString("PARA_CODE1");
			 
		}    
    	rtnData.put("DEPOSIT", Integer.parseInt(deposit)/100);
    	return rtnData;
    }
    
    /**
     * BUG20151109155214 光猫串号必须输入bug 
     * 判断是否用户已经申领光猫
     * 是的话返回相应信息
     * chenxy3 20151116 SS.FTTHModermApplySVC.getUserFtthResNO
     * */
    public IData getUserFtthResNO(IData inParam) throws Exception{
    	FTTHModermApplyBean bean= BeanManager.createBean(FTTHModermApplyBean.class);
    	IData data=new DataMap();
    	data.put("SERIAL_NUMBER", inParam.getString("SERIAL_NUMBER",""));
    	IDataset datas=bean.getUserModermInfo(data); 
    	if(datas!=null && datas.size()>0){
    		String resNO=datas.getData(0).getString("RSRV_STR1","");
    		if(resNO !=null && !"".equals(resNO)){
    			data.put("FTTH_TAG", "2");//已经办理光猫申领，且已经存在光猫串号
    			data.put("RES_NO",resNO);
    		}else{
    			data.put("FTTH_TAG", "1");//已经办理光猫申领，还未存在光猫串号
    			data.put("RES_NO","");
    		}
    	}else{
    		data.put("FTTH_TAG", "0");//还未办理光猫申领
			data.put("RES_NO","");
    	}
    	return data;
    }
}
