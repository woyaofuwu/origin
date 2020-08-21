package com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon;

import com.ailk.cache.memcache.util.SharedCache;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.util.WorkfromViewCall;
import com.asiainfo.veris.crm.order.pub.frame.bcf.cache.CacheKey;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBasePage;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import org.apache.tapestry.IRequestCycle;

public abstract class EopBasePage extends CSBasePage
{
    public void initPage(IRequestCycle cycle) throws Exception
    {
        String ibsysid = getData().getString("IBSYSID");
        String groupId = getData().getString("GROUP_ID");
        IData workformData = new DataMap();
        if (StringUtils.isNotEmpty(ibsysid)) 
        {
        	 workformData = WorkfromViewCall.qryWorkformSubscribeByIbsysid(this, ibsysid);
             if (IDataUtil.isNotEmpty(workformData)) 
             {
            	 groupId = workformData.getString("GROUP_ID");
     		 }
		}
        if (StringUtils.isNotBlank(groupId))
        {
        	queryCustGroupByGroupId(groupId);
		}
       
    }
    
    public void submit(IRequestCycle cycle) throws Exception
    {
        IData submitData = new DataMap(getData().getString("SUBMIT_PARAM"));
        buildOtherSvcParam(submitData);
        IData param = ScrDataTrans.buildWorkformSvcParam(submitData);

        IDataset result = CSViewCall.call(this, "SS.WorkformRegisterSVC.register", param);
        setAjax(result.first());
    }
    
   
    private void checkDiscntParam(IData submitData) throws Exception
    {
    	String busiformOperType = submitData.getData("BUSI_SPEC_RELE").getString("BUSIFORM_OPER_TYPE","");
    	String productId  =  submitData.getData("OFFER_DATA").getString("OFFER_CODE");
    	if("20".equals(busiformOperType) || "21".equals(busiformOperType)){ //如果是开通则校验开通专线的带宽和价格
    		IDataset subOffers  = submitData.getData("OFFER_DATA").getDataset("SUBOFFERS");//专线信息
    		/*for(int i = 0; i< subOffers.size(); i++){
    			IDataset offerCha  = subOffers.getData(i).getDataset("OFFER_CHA");*/
    			
    			IData checkParam = new DataMap();
    	        checkParam.put("CHK_FLAG", "EsopInfo");
    	        checkParam.put("EPARCHY_CODE", "0898");
    	        checkParam.put("PRODUCT_ID", productId);
    	        checkParam.put("SUBOFFERS", subOffers);
    	        CSViewCall.call(this,"CS.chkEsopGrpUserOpen", checkParam);
    		/*}*/
    		
    		
    	}
    }
    
    public void buildOtherSvcParam(IData param) throws Exception
    {
        
    }
    
    private void queryCustGroupByGroupId(String groupId) throws Exception
    {
        IData group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        setGroupInfo(group);
        
        String custMgrId = group.getString("CUST_MANAGER_ID");
        if (StringUtils.isNotEmpty(custMgrId))
        {
            IData managerInfo = UCAInfoIntfViewUtil.qryCustManagerByCustManagerId(this, custMgrId);
            setCustMgrInfo(managerInfo);
        }
    }
    
    public IDataset queryWorkformDisList() throws Exception
    {
        String offerCode = getData().getString("BUSI_CODE");
//        String mebOfferCode = IUpcViewCall.queryMemOfferCodeByOfferCode(offerCode,this.getTradeEparchyCode());
        IData input = new DataMap();
        input.put("IBSYSID", getData().getString("IBSYSID"));
//        input.put("MEB_OFFER_CODE", mebOfferCode);
        IDataset wfDisList = CSViewCall.call(this, "SS.WorkformDisSVC.queryWorkformDisStrList", input);
        return wfDisList;
    }
    
    public void saveCache(IRequestCycle cycle) throws Exception 
	{
    	IData data = this.getData();
    	String ibsysid = data.getString("IBSYSID", "");
    	String recordNum = data.getString("RECORD_NUM", "");
    	IData busiData = new DataMap(getData().getString("LineData"));
    	// 得到缓存key
        String cacheKey = CacheKey.getUcaKeyDataLine(ibsysid, recordNum);
    	SharedCache.set(cacheKey, busiData, 600);
	}
    
    /**
     *  对相关参数格式化获取
     * @param ibsysid  TF_B_EOP_ATTR流水号
     * @param pattrList 回调数据参数 [{ATTR_CODE:ATTR_VALUE}] 根据RECORD_NUM（非0）分组
     * @param commonData 回调公共参数 根据RECORD_NUM（为0）分组
     * @throws Exception
     */
    protected void  getEopAttrToList(String ibsysid,IDataset pattrList,IData commonData) throws Exception{
        getEopAttrToList(ibsysid,pattrList,commonData,null) ;
    }
    protected void  getEopAttrToList(String ibsysid,IDataset pattrList,IData commonData,String flag) throws Exception{
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
    	IDataset attrtInfo = new DatasetList();
    	if(flag!=null&&flag.equals("His")){
        	 attrtInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getEopAttrToListForHis", inparam);

    	}else{
    		attrtInfo = CSViewCall.call(this, "SS.WorkformAttrSVC.getEopAttrToList", inparam);
    	}
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
    public abstract void setGroupInfo(IData groupInfo) throws Exception;
    public abstract void setCustMgrInfo(IData custMgrInfo) throws Exception;
}
