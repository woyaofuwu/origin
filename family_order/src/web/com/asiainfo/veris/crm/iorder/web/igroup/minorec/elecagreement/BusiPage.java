package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement;


import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceRequest;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.biz.view.BizPage;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataInput;
import com.ailk.common.data.IDataOutput;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataInput;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DataOutput;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;



public abstract class BusiPage extends BizPage
{
    public IData call(String svcName, IData input) throws Exception
    {
        ServiceResponse response = BizServiceFactory.call(svcName, input);
        return response.getBody();
    }

    public IData call(String svcName, IData input, Pagination pagin) throws Exception
    {    	
    	ServiceRequest request = new ServiceRequest();
	    request.setData(input);
    	ServiceResponse response = BizServiceFactory.call(svcName, request,pagin);
    	IData data = response.getBody();
    	data.put("X_RESULTCOUNT", data.getLong("RESULT_COUNTS", 0));
    	return data;
    }
    
    public IData call(String url, String svcName, IData input, Pagination pagin) throws Exception
    {    	
        ServiceRequest request = new ServiceRequest();
        IDataInput datainput = new DataInput(null, input);
        request.setData(input);
        IDataOutput response = BizServiceFactory.call(url, svcName, datainput,pagin);
        IDataset data = response.getData();
        return data.first();
    }
    

    public final static IDataOutput callPage(String svcName, IData data) throws Exception
    {
        IDataOutput output = svcFatCall(svcName, data);
        return output;
    }
    
    public final static IDataOutput callPage(String svcName, IData data, Pagination page) throws Exception
    {
        IDataOutput output = svcFatCall(svcName, data, page);
        return output;
    }

    private final static IDataOutput svcFatCall(String svcName, IData data, Pagination page) throws Exception {
		ServiceResponse response = BizServiceFactory.call(svcName, data, page);
		IData head = new DataMap();
		head.put("X_RESULTCOUNT", response.getBody().getLong("RESULT_COUNTS", 0));
		IDataOutput output = new DataOutput();
		
		IDataset retdataset =new DatasetList();
		retdataset=response.getBody().getDataset("OUTDATA");
		if(DataUtils.isEmpty(retdataset)){
			retdataset=response.getBody().getDataset("DATAS");
		}
		
		if (DataUtils.isEmpty(retdataset)) {
			IData datas_out =new DataMap();
			  datas_out = response.getBody().getData("OUTDATA");
			if(DataUtils.isEmpty(datas_out)){
			  datas_out = response.getBody().getData("DATAS");
			}
			IDataset dataset = new DatasetList();
			if(datas_out!=null && datas_out.size() >0){
				dataset.add(datas_out);
			}
			output = new DataOutput(head, dataset);
		} else {
			output = new DataOutput(head, retdataset);
		}
		
		return output;
	}
    
    private final static IDataOutput svcFatCall(String svcName, IData data) throws Exception {
  		ServiceResponse response = BizServiceFactory.call(svcName, data);
  		IData head = new DataMap();
  		head.put("X_RESULTCOUNT", response.getBody().getLong("RESULT_COUNTS", 0));
  		IDataOutput output = new DataOutput();
  		
  		IDataset retdataset =new DatasetList();
  		retdataset=response.getBody().getDataset("OUTDATA");
  		if(DataUtils.isEmpty(retdataset)){
  			retdataset=response.getBody().getDataset("DATAS");
  		}
  		
  		if (DataUtils.isEmpty(retdataset)) {
  			IData datas_out =new DataMap();
  			  datas_out = response.getBody().getData("OUTDATA");
  			if(DataUtils.isEmpty(datas_out)){
  			  datas_out = response.getBody().getData("DATAS");
  			}
  			IDataset dataset = new DatasetList();
  			if(!DataUtils.isEmpty(datas_out)){
				dataset.add(datas_out);
			}
  			output = new DataOutput(head, dataset);
  		} else {
  			output = new DataOutput(head, retdataset);
  		}
  		
  		return output;
  	}
    
}
