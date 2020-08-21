package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizservice.base.CSAppCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformAttrBean;
import com.asiainfo.veris.crm.order.soa.group.esop.query.WorkformOtherSVC;

public class ChangeDatalineContractEspSVC extends CSBizService{

	private static final long serialVersionUID = 1L;
	
	public IDataset changeDatalineContractInfo(IData map) throws Exception
    {
		IDataset result = new DatasetList();
		System.out.print("fufn-ChangeDatalineContractEspSVC map:"+map);
		String ibsysId = map.getString("IBSYSID");
		String attCode = "AUDIT_RESULT";
		if("auditPeriodRestartContract".equals(map.getString("NODE_ID"))){
			result=changeInfo(ibsysId,map);

		}else if("PointCustCenterAudit".equals(map.getString("NODE_ID"))){
			IData otherInData= new DataMap();
			otherInData.put("IBSYSID", ibsysId);
			otherInData.put("ATTR_CODE", attCode);
			WorkformOtherSVC workformOtherSVC=new WorkformOtherSVC();
			IDataset otherOutList=workformOtherSVC.qryLastInfoByIbsysidAndAttrCode(otherInData);
			System.out.print("fufn-ChangeDatalineContractEspSVC otherOutList:"+otherOutList);
			if(IDataUtil.isNotEmpty(otherOutList)&&otherOutList.size()>0&&"1".equals(otherOutList.getData(0).getString("ATTR_VALUE", ""))){
				result=changeInfo(ibsysId,map);
			}
		}
		
		return result;
		
    }
	
	private IDataset changeInfo(String ibsysId,IData map) throws Exception{
		IDataset result = new DatasetList();
		IData commonData= new DataMap(); 
		getEopAttrToList(ibsysId,null,commonData);
		
		if(IDataUtil.isNotEmpty(commonData)){
			String[] names = commonData.getNames();
	        for(int i = 0; i < names.length; ++i) {
	        	if(names[i].startsWith("C_")) {
	        		commonData.put(names[i].substring(2), commonData.get(names[i]));
	            }
	        }
            
			
		}
		IDataset directLine = new DatasetList();
		commonData.put("DIRECTLINE", directLine);
		commonData.put("PRODUCT_ID", map.getString("BUSI_CODE"));
		if(commonData.getString("CONTRACT_FILE_LIST_name")!=null&&commonData.getString("CONTRACT_FILE_LIST")!=null){
			commonData.put("CONTRACT_FILE_ID", commonData.getString("CONTRACT_FILE_LIST")+":"+commonData.getString("CONTRACT_FILE_LIST_name"));
		}
		System.out.println("fufn-ChangeDatalineContractEspSVC commonData:"+commonData);
		result = CSAppCall.call("CM.ConstractGroupSVC.updateDirectlineContract",commonData);
		System.out.println("fufn-ChangeDatalineContractEspSVC result:"+result);
		return result;
	}
	protected void  getEopAttrToList(String ibsysid,IDataset pattrList,IData commonData) throws Exception{
    	if(pattrList==null){
    		pattrList=new DatasetList();
    	}
    	if(commonData==null){
    		commonData= new DataMap();
    	}
    	if(ibsysid==null){
    		ibsysid="";
    	}
        IData inparam = new DataMap();
    	inparam.put("IBSYSID", ibsysid);
    	IDataset attrtInfo = WorkformAttrBean.getEopAttrToList(inparam);
//    	IDataset attrtInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getInfoByIbsysidAttrtype", inparam);
    	if (IDataUtil.isNotEmpty(attrtInfo)) {
        	
            for (int i = 0; i < attrtInfo.size(); i++) {
            	String recordNum=attrtInfo.getData(i).getString("RECORD_NUM","");
            	String key = attrtInfo.getData(i).getString("ATTR_CODE");
                String value = attrtInfo.getData(i).getString("ATTR_VALUE");
                if(recordNum.matches("^[0-9]*$")){
                	int recordNumInt=Integer.valueOf(recordNum);
                	if(recordNumInt<=0){
                		commonData.put(key, value);
                	}else{
                		int num =recordNumInt-1;
                		
                		if(pattrList.size()<recordNumInt){
                			for(int size=pattrList.size();size<recordNumInt;size++){
                				pattrList.add(new DataMap());
                			}
                		}
                		pattrList.getData(num).put(key, value);
                	}
                }
            	
            }
        }
    }

}
