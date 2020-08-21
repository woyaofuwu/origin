package com.asiainfo.veris.crm.order.soa.person.busi.realnameqry;
 
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap; 
import com.ailk.common.data.impl.DatasetList; 
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;

/**
 * REQ201608100026 新增套餐推荐界面开发需求
 * chenxy3 2016-10-12
 */
public class RealNameReasonSVC extends CSBizService
{
	private static final Logger log = Logger.getLogger(RealNameReasonSVC.class);
    /**
     * 查询用户话费信息
     */
    public IDataset qryUserNonRealNameReason(IData input) throws Exception
    {
    	IDataset rtnSet=new DatasetList();
    	RealNameReasonBean bean = BeanManager.createBean(RealNameReasonBean.class);
        IDataset results= bean.qryUserNonRealNameReason(input);
        if(IDataUtil.isNotEmpty(results)){
        	IData result=results.getData(0);
        	IData data0=new DataMap();
        	data0.put("STAT_DATE", result.getString("STAT_DATE",""));
        	data0.put("CMCC_BRAND_CODE", result.getString("CMCC_BRAND_CODE",""));
        	rtnSet.add(data0);
        	if("1".equals(result.getString("TYPE_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","证件类型不规范");rtnData.put("YES_NO","√")           ;rtnSet.add(rtnData);}
        	if("1".equals(result.getString("OPENNAME_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","开户名称不规范");rtnData.put("YES_NO","√") ;rtnSet.add(rtnData);}
        	if("1".equals(result.getString("OPENID_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","开户证件号码不规范");rtnData.put("YES_NO","√") ;rtnSet.add(rtnData);}
        	if("1".equals(result.getString("OPENADDR_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","开户证件地址不规范");rtnData.put("YES_NO","√") ;rtnSet.add(rtnData);}
        	if("1".equals(result.getString("APPTYPE_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","经办人证件类型不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("APPNAME_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","经办人证件名称不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("APPID_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","经办人证件号码不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("APPADDR_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","经办人证件地址不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("USETYPE_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","使用人证件类型不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("USENAME_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","使用人名称不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("USEID_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","使用人证件号码不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("USEADDR_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","使用人证件地址不规范");rtnData.put("YES_NO","√");rtnSet.add(rtnData);}
        	if("1".equals(result.getString("DUTYTYPE_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人证件类型不规范");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("DUTYNAME_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人名称不规范");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("DUTYID_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人证件号码不规范");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("DUTYADDR_ERR_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人证件地址不规范");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("MULTIPLE_1_NAME",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","开户证件一证多名");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("MULTIPLE_2_NAME",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","经办人一证多名");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("MULTIPLE_3_NAME",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","使用人一证多名");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("MULTIPLE_4_NAME",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人一证多名");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        	if("1".equals(result.getString("SAMENAME_FLAG",""))){IData rtnData=new DataMap();rtnData.put("REASON_LIST","责任人是否与经办人一样");rtnData.put("YES_NO","√"); rtnSet.add(rtnData);}
        }
        
        return rtnSet;
    }
     
}
