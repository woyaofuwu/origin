
package com.asiainfo.veris.crm.order.soa.group.creategroupuser;


import java.util.Iterator;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.sysorg.UAreaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.creategroupuser.CreateGroupUser;
import com.asiainfo.veris.crm.order.soa.group.esop.query.EweConfigQry;

public class CreateIdcUser extends CreateGroupUser
{
    protected CreateInternetGroupUserReqData reqData = null;
    protected IData idcParm = null;
    protected IDataset idcParmList = null;


    /**
     * 构造函数
     */
    public CreateIdcUser()
    {

    }
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateInternetGroupUserReqData();
    }

    protected void actTradeUser() throws Exception
    {
        super.actTradeUser();
    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

    }

    protected void setTradeBase() throws Exception
    {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("OLCOM_TAG", "0");
        
        map.put("PF_WAIT", reqData.getPfWait());
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        
        reqData = (CreateInternetGroupUserReqData) getBaseReqData();
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        reqData.setPfWait(map.getInt("PF_WAIT"));
        idcParm=map.getData("IDCPARAM");
        if(map.get("IDCPARAMLIST")!=null){
            idcParmList=map.getDataset("IDCPARAMLIST");

        }
    }
    
    @Override
    protected void setTradeAttr(IData map) throws Exception
    {
        super.setTradeAttr(map);
    }
    
    /**
     * 其它台帐处理
     */
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
        actTradeAttr();
        infoRegDataOther();
    }
    
    private void actTradeAttr() throws Exception
    {

        
        String instid="";
        String instidDiscnt="";
        String discntCode="";

        IDataset datasetDiscnt=reqData.cd.getDiscnt();
        IDataset datasetProduct=reqData.cd.getProduct();
        if(IDataUtil.isNotEmpty(datasetDiscnt)&&datasetDiscnt.size()>0){
	    	IData dataDiscnt=datasetDiscnt.first();
	    	instidDiscnt=dataDiscnt.getString("INST_ID", "");
	    	discntCode=dataDiscnt.getString("DISCNT_CODE", "");
        }
        if(IDataUtil.isNotEmpty(datasetProduct)){
            for (int i=0,size=datasetProduct.size();i<size;i++){
            	IData dataProduct=datasetProduct.getData(i);
            	if(dataProduct.getString("PRODUCT_ID", "").equals(reqData.getUca().getProductId())){
            		instid=dataProduct.getString("INST_ID", "");
            		break;
            	}
            }
        }
        IDataset idcTradeParamChange = EweConfigQry.qryByConfigName("IDC_TRADEPARAMCHANGE","0");
        IDataset dataset = new DatasetList();
        String[] names=idcParm.getNames();
        for (int i=0,size=names.length;i<size;i++){
        	String name=names[i];
        	if(name.indexOf("IDC_")>=0){
        		IData map = new DataMap();
//                map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
        		
                String nameNow=null;
                Iterator idcParamChangeIt = idcTradeParamChange.iterator();
	    		while( idcParamChangeIt.hasNext()){	//进行参数转化
	    			IData idcParamChangeData = (IData) idcParamChangeIt.next();
	    			if(name.equals(idcParamChangeData.getString("PARAMVALUE"))){
	    				//
//	    				if("".equals(idcParamChangeData.getString("RSRV_STR1",""))){
	    				nameNow=idcParamChangeData.getString("PARAMNAME");break;
//	    				}
	    			}
	    		}
	    		if(nameNow!=null){
	    			/*addTradeAttrCom(map);
	                map.put("INST_TYPE", "P");
	                map.put("RELA_INST_ID", instid);
	                map.put("ATTR_CODE", nameNow);
	                map.put("ATTR_VALUE", idcParm.getString(name, ""));
	                dataset.add(map);*/
	                addTradeAttrCom(map);
	                map.put("INST_TYPE", "D");
	                map.put("RELA_INST_ID", instidDiscnt);
	                map.put("ELEMENT_ID", discntCode);
	                map.put("ATTR_CODE", nameNow);
	                map.put("ATTR_VALUE", idcParm.getString(name, ""));
	                dataset.add(map);
	    		}else{
	    			addTradeAttrCom(map);
	                map.put("INST_TYPE", "P");
	                map.put("RELA_INST_ID", instid);
	                map.put("ATTR_CODE", name);
	                map.put("ATTR_VALUE", idcParm.getString(name, ""));
	                dataset.add(map);
	    		}
        	}
        }
//        if(IDataUtil.isNotEmpty(idcParmList)){
//        	for (int j=0,sizej=idcParmList.size();j<sizej;j++){
//        		IData idcParmData=idcParmList.getData(j);
//        		String[] jnames=idcParmData.getNames();
//                for (int i=0,size=jnames.length;i<size;i++){
//                	String jname=jnames[i];
//                	if(jname.indexOf("IDC_")>=0){
//                		IData map = new DataMap();
////                        map.put("USER_ID_A", reqData.getGrpUca().getUser().getUserId());
//                		addTradeAttrCom(map);
//                        map.put("INST_TYPE", "P");
//                        map.put("RELA_INST_ID", instid);
//                        map.put("ATTR_CODE", jname);
//                        map.put("ATTR_VALUE", idcParmData.getString(jname, ""));
//                        map.put("RSRV_NUM1", j+1);
//                        dataset.add(map);
//                	}
//                }
//        	}
//        }
        
        super.addTradeAttr(dataset);
    }
    private void addTradeAttrCom(IData map) throws Exception
    {
        map.put("USER_ID", reqData.getUca().getUser().getUserId());
        map.put("INST_ID", SeqMgr.getInstId());
        map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
        map.put("START_DATE",  getAcceptTime());
        map.put("END_DATE", SysDateMgr.getTheLastTime());
    }
    
    private void infoRegDataOther() throws Exception
    {
    	 String userId=reqData.getUca().getUser().getUserId();
    	 if(IDataUtil.isNotEmpty(idcParmList)){
    	     IDataset dataset = new DatasetList();
    		 for (int j=0,sizej=idcParmList.size();j<sizej;j++){
    			IData data = new DataMap();
    			data.put("USER_ID", userId );
    			data.put("RSRV_VALUE_CODE", "IDCLINE");
         		IData idcParmData=idcParmList.getData(j);
         		data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                data.put("START_DATE", getAcceptTime());
                data.put("END_DATE", SysDateMgr.getTheLastTime());
                data.put("INST_ID", SeqMgr.getInstId()); 
                data.put("RSRV_VALUE",j+1);
                data.put("RSRV_STR1", idcParmData.getString("IDC_7041_EquipmentName"));
                data.put("RSRV_STR2", idcParmData.getString("IDC_7041_IpAddress"));
                data.put("RSRV_STR3", idcParmData.getString("IDC_7041_PortNumber"));
                data.put("RSRV_STR4", idcParmData.getString("IDC_7041_TakeEffectType"));
                data.put("RSRV_STR5", idcParmData.getString("IDC_7041_TakeEffectTime"));
                data.put("RSRV_STR6", idcParmData.getString("IDC_7041_FreeTime1"));
                data.put("RSRV_STR7", idcParmData.getString("IDC_7041_FreeTime2"));
                data.put("RSRV_STR8", idcParmData.getString("IDC_7041_FreeTime3"));
                dataset.add(data);

    		 }
             addTradeOther(dataset);
    	 }
    	
    	

    }
    
    @Override
    protected void setTradeSvc(IData map) throws Exception {
        super.setTradeSvc(map);
        map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识

        String elementId = map.getString("ELEMENT_ID", "");

        String mainTag = SvcInfoQry.queryMainTagByPackageIdAndServiceId(map.getString("PRODUCT_ID"), map.getString("PACKAGE_ID"), elementId);
        map.put("MAIN_TAG", mainTag);// 主体服务标志：0-否，1-是
        map.put("CAMPN_ID", map.getString("CAMPN_ID"));
    }
    @Override
    protected void setTradeSvcstate(IData map) throws Exception {
        super.setTradeSvcstate(map);
    }
}
