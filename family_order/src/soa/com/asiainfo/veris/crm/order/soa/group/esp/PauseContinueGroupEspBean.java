package com.asiainfo.veris.crm.order.soa.group.esp;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;

public class PauseContinueGroupEspBean extends GroupBean{
	
	private CreateGroupEspReqData reqData = null;
	
	private String tradeTypeCode;
	
    protected void makInit(IData map) throws Exception
    {
    	String serialNumber = null;
    	String userID = null;
        String custId = map.getString("CUST_ID", "");
        String productId = map.getString("PRODUCT_CODE");
        //产品ID系统编码转换集团编码
        String productCode = map.getString("PRODUCT_CODE");
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productCode, null);
        if (IDataUtil.isNotEmpty(attrBizInfoList)){
        	productId = attrBizInfoList.getData(0).getString("ATTR_CODE");
        }else{
        	CSAppException.apperr(GrpException.CRM_GRP_428);
        }
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        inparam.put("PRODUCT_ID", productId);
        IDataset userInfoList = GroupInfoQueryDAO.getUserInfo(inparam);
        if(null != userInfoList && userInfoList.size() > 0){
            serialNumber = userInfoList.getData(0).getString("SERIAL_NUMBER");
            userID = userInfoList.getData(0).getString("USER_ID");
        	map.put("SERIAL_NUMBER", serialNumber);
        	map.put("USER_ID", userID);
        }
        map.put("CUST_ID", custId);
        map.put("PRODUCT_ID", productId);
        if("03".equals(map.getString("OPR_TYPE"))){
        	tradeTypeCode = "4201";
        }else if ("04".equals(map.getString("OPR_TYPE"))){
        	tradeTypeCode = "4200";
        }
    	reqData.setProductOrderInfo(map);
    	super.makInit(map);	
    }
    
    @Override
    protected BaseReqData getReqData() throws Exception
    {
        return new CreateGroupEspReqData();
    }
    
    @Override
    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (CreateGroupEspReqData) getBaseReqData();
    }
	
    @Override
    protected void actTradeBase() throws Exception
    {
    	super.actTradeBase();
    	actTradeSvcState();
    }
    
    /**
     * 处理服务状态
     * 
     * @throws Exception
     */
    protected void actTradeSvcState() throws Exception
    {
    	IData svcData = new DataMap();
    	IDataset svcStateList = new DatasetList();
    	svcData.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
    	svcData.put("PACKAGE_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
    	svcData.put("SERVICE_ID", "1124");
    	svcData.put("USER_ID", reqData.getUca().getUser().getUserId());
    	//产品恢复
    	if("04".equals(reqData.getProductOrderInfo().getString("OPR_TYPE"))){
    		svcData.put("START_DATE", SysDateMgr.getNextSecond(getAcceptTime()));
    		svcData.put("END_DATE", SysDateMgr.getTheLastTime());
    		addSvcState(svcData, svcStateList);
    	}
    	//产品暂停
    	else if("03".equals(reqData.getProductOrderInfo().getString("OPR_TYPE"))){
    		delSvcState(svcData, svcStateList);
    	}
        super.addTradeSvcstate(svcStateList);
    }
    
    public  String setTradeTypeCode() throws Exception{
    	return tradeTypeCode;
    }
    
    @Override
    public void makUca(IData map) throws Exception
    {
    	makUcaForGrpNormal(map);
    }
	
}
