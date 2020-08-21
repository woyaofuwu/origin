
package com.asiainfo.veris.crm.order.soa.person.busi.changeservice;

import java.util.Iterator;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.util.FeeUtils;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.BizException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.USvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.busi.auth.TradeInfoBean;
import com.asiainfo.veris.crm.order.soa.person.busi.changeproduct.ChangeProductIntfSVC; 

public class ChangeServiceBean extends CSBizBean
{
	private static final Logger log = Logger.getLogger(ChangeServiceBean.class);
    public IDataset loadChildInfo(IData input) throws Exception
    {    
        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER"));
        
        IData productInfo = TradeInfoBean.getUserProductInfo(userInfo.getString("USER_ID"), userInfo.getString("REMOVE_TAG", "0"));
        if (null != productInfo && productInfo.size() > 0)
        { 
            userInfo.put("PRODUCT_ID", productInfo.getString("PRODUCT_ID"));
        } 
        
        String productId = userInfo.getString("PRODUCT_ID");
        IDataset paraInfo = CommparaInfoQry.getCommparaAllCol("CSM", "990", "0", getTradeEparchyCode());
        
        IDataset userSvcs = UserSvcInfoQry.queryUserSvcByUserIdAll(userInfo.getString("USER_ID"));
     
        for (Iterator iterator = userSvcs.iterator(); iterator.hasNext();)
        {
            IData item = (IData) iterator.next(); 
            
            IDataset configList =  DataHelper.filter(paraInfo, "PARA_CODE1="+item.getString("SERVICE_ID"));
            //移除配置中不存在服务
            if (IDataUtil.isEmpty(configList))
            {
                iterator.remove();
            }
            
        }
        for(int i=0;i<userSvcs.size();i++)
        {
            IData temp = userSvcs.getData(i);
            temp.put("SERVICE_NAME", USvcInfoQry.getSvcNameBySvcId(temp.getString("SERVICE_ID")));
        }
         
        
        IDataset pkgElems  = ProductInfoQry.getProductElements(productId,CSBizBean.getUserEparchyCode());
        
        IDataset addSvcs = new DatasetList();
        if(IDataUtil.isNotEmpty(paraInfo) && IDataUtil.isNotEmpty(pkgElems))
        {
            IDataset sumDataset = new DatasetList();
            //和指定主产品的服务做比较，过滤不在主产品中的服务
            for(int i=0; i<paraInfo.size(); i++)
            {
                int a = 0;
                IData data = paraInfo.getData(i);
                String paraCode1 = data.getString("PARA_CODE1", "");
                while  ( a < pkgElems.size()) 
                {
                    IData tempElem = pkgElems.getData(a);
                    String elementId = tempElem.getString("ELEMENT_ID");
                    if (paraCode1.equals(elementId)) {
                        break;
                    }
                     a++;
                }
                if(a != pkgElems.size())
                {//参数服务在产品服务中没有数据，删除当前参数服务
                    sumDataset.add(data);  
                }
               
            }
        
            //新增服务
            for(int i = 0; i < sumDataset.size(); i++){
                IData data = sumDataset.getData(i);
                String outServiceId = data.getString("PARA_CODE1");
                int j = 0;
                while (j < userSvcs.size()) {
                    IData temp = userSvcs.getData(j);
                    String inServiceId = temp.getString("SERVICE_ID");
                    if (outServiceId.equals(inServiceId)) {
                        break;
                    } 
                    j++;
                }
                if (j == userSvcs.size()) {
                    addSvcs.add(data);
                }
            }
        }
        //删除服务
        IDataset delSvcs = new DatasetList();
        for(int i=0,size= paraInfo.size();i<size;i++)
        {
            IData data = paraInfo.getData(i);
            String paraCode1 = data.getString("PARA_CODE1", "");
            for(int j=0,count=userSvcs.size();j<count;j++)
            {
                String serviceId = userSvcs.getData(j).getString("SERVICE_ID", "");
                if(paraCode1.equals(serviceId))
                {
                    IData temp = new DataMap();
                    temp.put("PARA_CODE11", data.getString("PARA_CODE1"));
                    temp.put("PARA_CODE2", data.getString("PARA_CODE2"));
                    delSvcs.add(temp);
                    break;
                }
            }
        }
        IDataset returnSet = new DatasetList();
        IData result = new DataMap();
        result.put("ADD_SVCS", addSvcs);
        result.put("DEL_SVCS", delSvcs);
        result.put("USER_SVCS", userSvcs);
        returnSet.add(result);
        return returnSet;
    }
    
    public IDataset submitTradeInfo(IData input) throws Exception
    { 
        IDataset svcList = new DatasetList(input.getString("X_CODING_STR","[]")); 
        
        IDataset roamList = DataHelper.filter(svcList, "SERVICE_ID=19");
        
        IDataset interCallList = DataHelper.filter(svcList, "SERVICE_ID=15");
        
        if (IDataUtil.isNotEmpty(roamList) && IDataUtil.isNotEmpty(interCallList))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "操作国际漫游会自动处理国际长途,国际漫游和国际长途不能同时操作");
        }
        
        IDataset result = new DatasetList();
        
        if(IDataUtil.isNotEmpty(interCallList))
        {
            for (Iterator iterator = interCallList.iterator(); iterator.hasNext();)
            {
                IData item = (IData) iterator.next();
                item.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                result = ChangeProductIntfSVC.changeInterCall(item);
            } 
        }
        
        if(IDataUtil.isNotEmpty(roamList))
        { 
            for (Iterator iterator = roamList.iterator(); iterator.hasNext();)
            {
                IData item = (IData) iterator.next();
                
                item.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
                
                if ("0".equals(item.getString("MODIFY_TAG")))
                {
                    IDataset feeList = loadFeeInfo(input);
                    
                    if (IDataUtil.isNotEmpty(feeList))
                    { 
                        //CSAppException.apperr(BizException.CRM_BIZ_5,String.format("用户话费余额不足，请先交纳%s元差额话费后再重新办理国漫开通功能",  FeeUtils.Fen2Yuan(feeList.getData(0).getString("FEE"))));
                    }
                    result =  ChangeProductIntfSVC.addRormByEndDate(item);
                }
                else if ("1".equals(item.getString("MODIFY_TAG")))
                {
                    result =  ChangeProductIntfSVC.delRoam(item);
                } 
            } 
        } 
        
        return result;
    }
    
    public IDataset loadFeeInfo(IData input) throws Exception
    {  
        UcaData uca = null;
        
        if (StringUtils.isNotBlank(input.getString("USER_ID")))
        {
            uca = UcaDataFactory.getUcaByUserId(input.getString("USER_ID"));
        }
        else if (StringUtils.isNotBlank(input.getString("SERIAL_NUMBER")))
        {
            uca = UcaDataFactory.getNormalUca(input.getString("SERIAL_NUMBER"));
        }  
        String strCreditClass = uca.getUserCreditClass();
        
        log.debug("***************cxy<>**********strCreditClass="+strCreditClass);
        if (StringUtils.isBlank(strCreditClass))
        {
        	String sn=uca.getSerialNumber();
        	String userId=uca.getUserId();
        	log.debug("***************cxy<>**********sn="+sn+"==========userId="+userId);
        	strCreditClass=getUserCreditClass(sn,userId);
        	if(strCreditClass==null || "".equals(strCreditClass)){
            strCreditClass = "-1";
        	}
        }
        
        int iCreditClass = Integer.parseInt(strCreditClass); 
        log.debug("***************cxy<>**********iCreditClass="+iCreditClass);
        double iAcctBlance = FeeUtils.toDouble(uca.getAcctBlance()); 
        // 是否满足星级服务流程，预存款开通条件
        
        IDataset feeList = new DatasetList();
        //取消星级和费用校验
        /*if (-1 == iCreditClass || 0 == iCreditClass)
        {
            if (iAcctBlance < 20000)
            {
                double need = 20000-iAcctBlance;
                IData item = new DataMap();
                item.put("TRADE_TYPE_CODE","110"); 
                item.put("FEE", String.valueOf(need)); 
                item.put("MODE","2"); 
                item.put("CODE","111"); 
                feeList.add(item);
            }
        }*/  
        return feeList;
    }
    
    public String getUserCreditClass(String sn,String userId)throws Exception{
    	String strCreditClass="";
    	IData condition = new DataMap(); 
        condition.put("SERIAL_NUMBER", sn);
        condition.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());
        condition.put("USER_ID", userId); 
        condition.put("IDTYPE", "0");
        IDataset userCreditClass=CSAppCall.call("SS.GetUser360ViewSVC.getCreditInfo", condition);
        log.debug("************cxy()**********userCreditClass"+userCreditClass);
        if(userCreditClass!=null && userCreditClass.size()>0){
        	strCreditClass=userCreditClass.getData(0).getString("CREDIT_CLASS","");
        }
        return strCreditClass;
    }
    
}
