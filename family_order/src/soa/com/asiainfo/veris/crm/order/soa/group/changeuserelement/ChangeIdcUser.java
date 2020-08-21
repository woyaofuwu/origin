package com.asiainfo.veris.crm.order.soa.group.changeuserelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changeuserelement.ChangeUserElementReqData;

public class ChangeIdcUser extends ChangeUserElement{
    protected ChangeUserElementReqData reqData = null;
    protected String idcUserId = null;
    protected IData idcParm = null;
    protected IDataset idcParmList = null;
    /**
     * 构造函数
     */
    public ChangeIdcUser()
    {

    }
    protected BaseReqData getReqData() throws Exception
    {
        return new ChangeUserElementReqData();
    }

//    protected void actTradeUser() throws Exception
//    {
//        super.actTradeUser();
//    }

    protected void regTrade() throws Exception
    {
        super.regTrade();

    }

    protected void setTradeBase() throws Exception
    {

        super.setTradeBase();

        IData map = bizData.getTrade();

        map.put("OLCOM_TAG", "0");
        
        map.put("PF_WAIT", "0");
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();
        reqData = (ChangeUserElementReqData) getBaseReqData();
        
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);
        
        idcParm=map.getData("IDCPARAM");
        if(map.get("IDCPARAMLIST")!=null){
            idcParmList=map.getDataset("IDCPARAMLIST");

        }
        idcUserId=map.getString("USER_ID");
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
    }
    
    
    
    
    
    private void actTradeAttr() throws Exception
    {
        IDataset dataset = new DatasetList();
    	IData attrParam = new DataMap();
    	attrParam.put("USER_ID", idcUserId);
//        discntParam.put("ATTR_CODE", attrCode);
//        discntParam.put("INST_TYPE", "D");
        IDataset userAttrsOld = UserAttrInfoQry.getUserLineInfoByUserIdAttrCode(attrParam);
        IDataset userAttrsNew = new DatasetList(userAttrsOld.toString());
        

        if (IDataUtil.isNotEmpty(userAttrsNew)) {
        	/*for(int i =0 ;i<userAttrsOld.size();i++){
        		IData data = new DataMap();
                data = userAttrsOld.getData(i);
                if("P".equals(data.getString("INST_TYPE", ""))){//只针对P类型的产品参数做处理
                	data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                 	data.put("END_DATE", SysDateMgr.getSysDate());
                 	dataset.add(data);
                }
                
        	}*/
        	for(int i =0 ;i<userAttrsNew.size();i++){
        		IData data = new DataMap();
                data = userAttrsNew.getData(i);
                if("P".equals(data.getString("INST_TYPE", ""))){

                	if(IDataUtil.isNotEmpty(idcParm)&&idcParm.getString(data.getString("ATTR_CODE", ""))!=null){
                    	data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                		data.put("ATTR_VALUE",idcParm.getString(data.getString("ATTR_CODE", "")));
//                    	data.put("START_DATE", SysDateMgr.getSysTime());
//                		data.put("INST_ID", SeqMgr.getInstId());
                     	dataset.add(data);
                		continue;
                	}
//                	boolean isFlag=true;
                    if(!"".equals(data.getString("RSRV_NUM1", ""))&&IDataUtil.isNotEmpty(idcParmList)){
                    	for (int j=0,sizej=idcParmList.size();j<sizej;j++){
                    		IData idcParmData=idcParmList.getData(j);
                    		if(IDataUtil.isNotEmpty(idcParmData)){
                    			if(!data.getString("RSRV_NUM1", "").equals(idcParmData.getString("RECORD_NUM",""))){
                    				continue;
                    			}
                    			if("1".equals(idcParmData.getString("7041_MODIFY_TAG",""))){//执行删除
                                	data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                                	data.put("END_DATE", getAcceptTime());
                                 	dataset.add(data);
//                    				isFlag=false;
                    				
                    				
                    				break;
                    			}
                    			/*if("2".equals(idcParmData.getString("7041_MODIFY_TAG",""))){//目前不支持修改相关参数
                    				if(idcParmData.getString(data.getString("ATTR_CODE", ""))!=null){
                                		data.put(data.getString("ATTR_VALUE", ""),idcParmData.getString(data.getString("ATTR_CODE", "")));
                                    	data.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                                		dataset.add(data);
                                		break;
                                	}
                    			}*/
                    		}
                    	}
                    }
                }
        	}
        }
        System.out.println("dataset:"+dataset);
        super.addTradeAttr(dataset);
    }
}
