
package com.asiainfo.veris.crm.order.web.person.familytradeoptimal;

import org.apache.tapestry.IRequestCycle;

import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.EpaperException;

public abstract class DirectFamily extends CSBasePage
{

    public void initial(IRequestCycle cycle) throws Exception
    {
        IData cond = new DataMap();

        String rspfile = getData().getString("RSP_FILE");
      

        IData param = new DataMap();
        param.put("QRY_FILE_NAME", rspfile);
        IDataset dataset = CSViewCall.call(this, "SS.FamilyAllNetBusiManageSVC.queryFileByRSP", param);
		IDataset return_data =new DatasetList();
		
		if (IDataUtil.isNotEmpty(dataset)) {
			for(int i=0;i<dataset.size();i++){
				IData data =new DataMap();//定义一个返回参数
				data.put("PRODUCT_CODE",dataset.getData(i).getString("PRODUCT_CODE") );
				data.put("PRODUCT_OFFERING_ID",dataset.getData(i).getString("PRODUCT_OFFERING_ID") );
				data.put("ACTION",dataset.getData(i).getString("ACTION") );
				data.put("CUSTOMER_PHONE",dataset.getData(i).getString("CUSTOMER_PHONE") );
				if("1".equals(dataset.getData(i).getString("BUSINESS_TYPE")))
				{
					data.put("BUSINESS_TYPE","个人业务");
				}else{
					data.put("BUSINESS_TYPE","集团业务");
				}
				if("1".equals(dataset.getData(i).getString("MEM_TYPE")))
				{
					data.put("MEM_TYPE","移动号码");
				}else if("2".equals(dataset.getData(i).getString("MEM_TYPE"))){
					data.put("MEM_TYPE","固话号码");
				}else{
					data.put("MEM_TYPE","异网号码");
				}
				
				
				//data.put("BUSINESS_TYPE","1".equals(dataset.getData(i).getString("MEM_TYPE"))? "个人业务" :"集团业务"  );
				//data.put("MEM_TYPE","1".equals(dataset.getData(i).getString("MEM_TYPE"))? "移动号码" :"固话号码" );
				data.put("MEM_AREA_CODE",dataset.getData(i).getString("MEM_AREA_CODE") );
				data.put("MEM_NUMBER",dataset.getData(i).getString("MEM_NUMBER") );
				data.put("FINISH_TIME",dataset.getData(i).getString("FINISH_TIME") );
				data.put("EFF_TIME",dataset.getData(i).getString("EFF_TIME") );
				data.put("EXP_TIME",dataset.getData(i).getString("EXP_TIME") );
				return_data.add(data);
				
			}
			
		} 
		log.debug("web层java的返回"+return_data);
		setAjax(return_data);
		setMebInfos(return_data);
        
    }


	public abstract void setViceInfos(IDataset viceinfos);

	public abstract void setMebInfos(IDataset mebinfos);
	 public abstract void setUserInfo(IData userInfo);
	  public abstract void setCustInfo(IData custInfo);
	  public abstract void setInfo(IData info);


	public abstract void setCondition(IData condition);

}
