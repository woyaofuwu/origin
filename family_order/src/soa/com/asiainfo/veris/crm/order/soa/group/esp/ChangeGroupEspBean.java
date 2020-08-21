package com.asiainfo.veris.crm.order.soa.group.esp;

import org.apache.log4j.Logger;

import com.ailk.bizcommon.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.bizservice.seq.SeqMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.AttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.GroupBean;
import com.asiainfo.veris.crm.order.soa.group.common.query.GroupInfoQueryDAO;


public class ChangeGroupEspBean extends GroupBean{
	private static final Logger log = Logger.getLogger(ChangeGroupEspBean.class);
	private CreateGroupEspReqData reqData = null;
    protected void makInit(IData map) throws Exception
    {
    	String serialNumber = null;
    	String userID = null;
        String custId = map.getString("CUST_ID", "");
        IData inparam = new DataMap();
        inparam.put("CUST_ID", custId);
        String productId = map.getString("PRODUCT_CODE");
        //产品ID系统编码转换集团编码
        String productCode = map.getString("PRODUCT_CODE");
        IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "PRO", productCode, null);
        if (IDataUtil.isNotEmpty(attrBizInfoList)){
        	productId = attrBizInfoList.getData(0).getString("ATTR_CODE");
        }
        inparam.put("PRODUCT_ID", productId);
        IDataset userInfoList = GroupInfoQueryDAO.getUserInfo(inparam);
        IData userInfo = null;
        String rsrvValueCode = "ESPG";
        for(int k=0 ; k<userInfoList.size(); k++){
        	String userId = userInfoList.getData(k).getString("USER_ID","");
        	IDataset userOthers = UserOtherInfoQry.getUserOtherInfo(userId, rsrvValueCode);
        	if(userOthers!= null && userOthers.size() >0){
        		String rsrvStr4 = userOthers.getData(0).getString("RSRV_STR4");
        		if(rsrvStr4.equals(map.getString("PRODUCT_ORDER_ID"))){
        			userInfo = userInfoList.getData(k);
        			break;
        		}
        	}
        }
        if(null == userInfo){
        	CSAppException.apperr(CustException.CRM_CUST_56);
        }
        serialNumber = userInfo.getString("SERIAL_NUMBER");
        userID = userInfo.getString("USER_ID");
    	map.put("SERIAL_NUMBER", serialNumber);
    	map.put("USER_ID", userID);
        map.put("CUST_ID", custId);
       	map.put("PRODUCT_ID", productId);
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
    	actTradePrdAndPrdParams();
    	actTradeDiscnt();
    }
    
	 public void actTradeDiscnt() throws Exception
	    {
	        IDataset dataset = reqData.getProductOrderInfo().getDataset("PRODUCT_ORDER_RATE_PLAN");

	        if (IDataUtil.isEmpty(dataset))
	        {
	            return;
	        }

	        super.addTradeDiscnt(dataset);
	    }
    
	protected void setTradeDiscnt(IData map) throws Exception
    {
        super.setTradeDiscnt(map);
        if("1".equals(map.getString("ACTION"))){
        	String  startDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
        	if("1".equals(map.getString("EFF_TYPE"))){
        		startDate = SysDateMgr.getFirstDayOfNextMonth4WEB()+" 00:00:00";
        	}
        	map.put("USER_ID", map.getString("USER_ID", reqData.getUca().getUserId()));// 用户标识
        	log.debug("++++++++++houxi USER_ID_A" + map.getString("USER_ID_A"));
        	map.put("USER_ID_A", map.getString("USER_ID_A", "-1"));// 用户标识
        	log.debug("++++++++++houxi USER_ID_A2" + map.getString("USER_ID_A"));
        	//优惠ID系统编码转换集团编码
        	String ratePlanId = map.getString("RATE_PLAN_ID", "");
        	IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", ratePlanId, null);
            if (IDataUtil.isNotEmpty(attrBizInfoList)){
            	map.put("DISCNT_CODE", attrBizInfoList.getData(0).getString("ATTR_CODE"));// 优惠编码
            }else{
            	map.put("DISCNT_CODE", ratePlanId);
            }
            
        	map.put("SPEC_TAG", map.getString("SPEC_TAG", "0")); // 特殊优惠标记
        	map.put("RELATION_TYPE_CODE", map.getString("RELATION_TYPE_CODE")); // 关系类型
        	map.put("INST_ID", SeqMgr.getInstId());// 实例标识
        	map.put("PRODUCT_ID", reqData.getProductOrderInfo().getString("PRODUCT_ID"));
        	map.put("PACKAGE_ID", "-1");
        	map.put("MODIFY_TAG", "0");
        	map.put("START_DATE", startDate);
        	map.put("END_DATE", SysDateMgr.getTheLastTime());
        	map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
    		IDataset parameter = map.getDataset("RATE_PARAM");
    		if(null != parameter && parameter.size() > 0){
    			IDataset dataset = new DatasetList();
    			for(int i=0; i<parameter.size(); i++){
    				IData data = new DataMap();
    				data.put("USER_ID", map.getString("USER_ID"));
    				data.put("INST_TYPE", "D");
    				data.put("RELA_INST_ID", map.getString("INST_ID", "0"));
    				data.put("INST_ID", SeqMgr.getInstId());
    				data.put("ATTR_CODE", parameter.getData(i).getString("PARAMETER_NUMBER",""));
    				data.put("ATTR_VALUE", parameter.getData(i).getString("PARAM_VALUE",""));
    				data.put("START_DATE", map.getString("START_DATE"));// 起始时间
    				data.put("END_DATE", map.getString("END_DATE")); // 终止时间
    				data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
    				dataset.add(data);
    			}
    			super.addTradeAttr(dataset);
    		}
        }
        else if("0".equals(map.getString("ACTION"))){
        	String ratePlanId = map.getString("RATE_PLAN_ID", "");
        	IDataset attrBizInfoList = AttrBizInfoQry.getBizAttrByAttrValue("1", "B", "DIS", ratePlanId, null);
        	if (IDataUtil.isNotEmpty(attrBizInfoList)){
        		String discntCode = attrBizInfoList.getData(0).getString("ATTR_CODE");
        		String userId = reqData.getUca().getUserId();
        		IDataset userDisInfos=UserDiscntInfoQry.queryXQWDiscntInfoByUserIdAndDisCode(userId, discntCode);
        		IData userDisInfo = null;
        		for(int k=0 ; k<userDisInfos.size() ; k++ ){
        			userDisInfo = userDisInfos.getData(k);
        			String endDate = SysDateMgr.getSysDateYYYYMMDDHHMMSS();
    				if("1".equals(map.getString("EFF_TYPE"))){
    					endDate = SysDateMgr.getLastDateThisMonth4WEB();
    				}
    				log.debug("+++++++++++houxi++userDisInfo" + userDisInfo);
    				log.debug("+++++++++++houxi" + endDate);
    				log.debug("+++++++++++houxi" + userDisInfo.getString("END_DATE"));
    				log.debug(endDate.compareTo(userDisInfo.getString("END_DATE")));
        			if(endDate.compareTo(userDisInfo.getString("END_DATE"))<0){
        				String RELA_INST_ID = userDisInfo.getString("INST_ID");
        				userDisInfo.put("END_DATE", endDate);
        				userDisInfo.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
        				userDisInfo.put("MODIFY_TAG", "1");
        				map.put("USER_ID", userDisInfo.getString("USER_ID", reqData.getUca().getUserId()));
        				map.put("USER_ID_A", userDisInfo.getString("USER_ID_A", "-1"));// 用户标识
        				map.put("DISCNT_CODE", userDisInfo.getString("DISCNT_CODE"));
        				map.put("MODIFY_TAG", "1");
        				map.put("END_DATE", endDate);
        				map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
        				map.put("SPEC_TAG", userDisInfo.getString("SPEC_TAG", "0")); // 特殊优惠标记
        				map.put("RELATION_TYPE_CODE", userDisInfo.getString("RELATION_TYPE_CODE")); // 关系类型
        				map.put("INST_ID", userDisInfo.getString("INST_ID"));
                		map.put("PRODUCT_ID", userDisInfo.getString("PRODUCT_ID"));
                		map.put("PACKAGE_ID", userDisInfo.getString("PRODUCT_ID","-1"));
                		map.put("START_DATE", userDisInfo.getString("START_DATE"));
                		map.put("UPDATE_STAFF_ID", reqData.getProductOrderInfo().getString("OPERATOR_CODE"));
                		IDataset userAttrs = UserAttrInfoQry.getUserAttrByUserIdInstidAndEndDate(userId, "D", RELA_INST_ID);
                		for(int j=0; j<userAttrs.size(); j++){
                			userAttrs.getData(j).put("END_DATE", endDate);
                			userAttrs.getData(j).put("MODIFY_TAG", "1");
                		}
                		super.addTradeAttr(userAttrs);
        			}
        		}
        	}	        	
        }
    }
	
	
    /**
     * 产品子表
     * 
     * @throws Exception
     */
    protected void actTradePrdAndPrdParams() throws Exception
    {

    	makReqDataProductParam();
    	
    	String stateTime = reqData.getProductOrderInfo().getString("PRODUCT_EFF_TIME");
    	String endTime = reqData.getProductOrderInfo().getString("EXP_TIME");
    	
        IDataset productInfoset = new DatasetList();

        String productId = reqData.getProductOrderInfo().getString("PRODUCT_ID");
        String userId = reqData.getUca().getUserId();
        IDataset productInfoList = UserProductInfoQry.getUserProductByUserIdProductId(userId, productId);
        
        
        if (IDataUtil.isNotEmpty(productInfoList))
        {
        	IData productPlus = productInfoList.getData(0);
        	
        	// //续订时修改product子表 --- add by huangzl3
        	productPlus.put("START_DATE", stateTime);
        	productPlus.put("END_DATE", endTime);
 	        productPlus.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());

        	productInfoset.add(productPlus);
        	
        	String instId = productPlus.getString("INST_ID");
        	
        	IDataset userAttrs = UserAttrInfoQry.getUserAttrByInstID(userId, instId);

            IDataset productParam = reqData.cd.getProductParamList(productId);

            if (IDataUtil.isNotEmpty(productParam))
            {

                IDataset dataset = new DatasetList();

                for (int i = 0, iSzie = productParam.size(); i < iSzie; i++)
                {
                    IData paramData = productParam.getData(i);
                    String attrCode = paramData.getString("ATTR_CODE");
                    String attrValue = paramData.getString("ATTR_VALUE", "");
                    
                    boolean isChangeValue = false;
                    for(int k = 0; k < userAttrs.size(); k++){
                    	IData userAttr = userAttrs.getData(k);
                    	if(attrCode.equals(userAttr.getString("ATTR_CODE"))){
                    		IData map = new DataMap();
                        	map.put("INST_TYPE", "P");
                        	map.put("RELA_INST_ID", instId);
                        	map.put("INST_ID", userAttr.getString("INST_ID"));
                        	map.put("MODIFY_TAG", TRADE_MODIFY_TAG.MODI.getValue());
                        	map.put("ATTR_CODE", attrCode);
                        	map.put("ATTR_VALUE", attrValue);
                        	map.put("START_DATE", stateTime);
                        	map.put("END_DATE", endTime);
                        	map.put("USER_ID", reqData.getUca().getUser().getUserId());
                    		dataset.add(map);
                    		isChangeValue = true;
                    		break;
                    	}
                    }
                    if(!isChangeValue){
                    	IData map = new DataMap();

                    	map.put("INST_TYPE", "P");
                    	map.put("RELA_INST_ID", instId);
                    	map.put("INST_ID", SeqMgr.getInstId());
                    	map.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                    	map.put("ATTR_CODE", attrCode);
                    	map.put("ATTR_VALUE", attrValue);
                    	map.put("START_DATE", stateTime);
                    	map.put("END_DATE", endTime);
                    	map.put("USER_ID", reqData.getUca().getUser().getUserId());

                    	dataset.add(map);
                    }   
                }

                this.addTradeAttr(dataset);
            }
            reqData.cd.putProduct(productInfoset);
            //续订时修改product子表 --- add by huangzl3
            super.addTradeProduct(productInfoset);
        }
    }
    
    //产品属性
    protected void makReqDataProductParam() throws Exception
    {
        IDataset infos = reqData.getProductOrderInfo().getDataset("PARAMETER");
        
        String productId = reqData.getProductOrderInfo().getString("PRODUCT_ID");
        
        IDataset productParam = new DatasetList();
        if(null != infos && infos.size() > 0){
        	for (int i = 0, size = infos.size(); i < size; i++)
        	{
        		IData info = infos.getData(i);

        		IData param = new DataMap();
            
        		param.put("ATTR_CODE", info.getString("PARAMETER_NUMBER"));
            
        		param.put("ATTR_VALUE", info.getString("PARAMETER_VALUE"));
            
        		productParam.add(param); 
        	}
        	reqData.cd.putProductParamList(productId, productParam);
        }
    }
    
    public  String setTradeTypeCode() throws Exception{
    	return "4685";
    }
    
    @Override
    public void makUca(IData map) throws Exception
    {
    	makUcaForGrpNormal(map);
    }
	
}
