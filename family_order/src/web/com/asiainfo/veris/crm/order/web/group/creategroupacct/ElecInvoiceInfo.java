
package com.asiainfo.veris.crm.order.web.group.creategroupacct;
import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
public abstract class ElecInvoiceInfo extends GroupBasePage
{	
	public static final String MON_ELEC_INVOICE = "0"; // 月结电子发票

    public static final String CASH_ELEC_INVOICE = "1"; // 现金电子发票

    public static final String BUSINESS_ELEC_INVOICE = "2"; //营业业务电子发票

  //按客户设置页面初始化方法
    public void initByCustId(IRequestCycle cycle) throws Exception
    {
        IData data = getData();         
        data.put("OPRATION", "ByCustId");       
        setInfo(data);
    }
    //按账户设置页面初始化方法
    public void initByUserId(IRequestCycle cycle) throws Exception
    {
         IData data = getData(); 
         data.put("OPRATION", "ByUserId"); 
         IDataset out = CSViewCall.call(this, "CS.SetGrpElecInvoiceSVC.qryEPostInfoByUserID", data);
         
         IData busiPost = new DataMap();//存放电子发票初始化信息
            if(!out.isEmpty()){ 
                for(int i=0;i<out.size();i++){
                    String postTag = out.getData(i).getString("POST_TAG");                  
                    if(MON_ELEC_INVOICE.equals(postTag)){   //月结电子发票
                        busiPost.put("POST_TAG_0", postTag);
                        busiPost.put("POST_DATE", out.getData(i).getString("POST_DATE"));
                    }else if(CASH_ELEC_INVOICE.equals(postTag)){//现金电子发票
                        busiPost.put("POST_TAG_1",postTag);                 
                    }
                    if(BUSINESS_ELEC_INVOICE.equals(postTag)){//营业业务电子发票
                        busiPost.put("POST_TAG_2", postTag);
                    }
                    busiPost.put("POST_CHANNEL", out.getData(i).getString("POST_CHANNEL"));
                    busiPost.put("RECEIVE_NUMBER", out.getData(i).getString("RECEIVE_NUMBER"));
                    busiPost.put("POST_ADR", out.getData(i).getString("POST_ADR"));                 
                }
            }
            setBusiInfo(busiPost); 
            setInfo(data);
            
        
    }
	//提交
    public void onSubmit(IRequestCycle cycle) throws Exception
    {
     
        IData param=getData();
        IData checkData=new DataMap();
        checkData.put("SERIAL_NUMBER", param.getString("postinfo_RECEIVE_NUMBER"));
        CSViewCall.call(this, "CS.SetGrpElecInvoiceSVC.checkPostNumber", checkData); 
        param.put("CUST_ID", param.getString("CUST_ID"));
        param.put("USER_ID", param.getString("USER_ID"));
        param.put("OPRATION", param.getString("OPRATION"));
        param.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        param.put("NEW_FLAG", param.getString("NEW_FLAG"));
        IDataset result=CSViewCall.call(this, "CS.SetGrpElecInvoiceSVC.onSubmitBaseTrade", param);          
        this.setAjax(result); 
    }
    
   
    public abstract void setCondition(IData data);
    public abstract void setInfos(IDataset infos);
    public abstract void setInfo(IData info);
    public abstract void setBusiInfo(IData info);
   
}
