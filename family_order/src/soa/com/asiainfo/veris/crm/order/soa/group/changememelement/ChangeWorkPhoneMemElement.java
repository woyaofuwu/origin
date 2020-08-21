
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import java.text.DecimalFormat;

import org.apache.log4j.Logger;

import com.ailk.biz.client.BizServiceFactory;
import com.ailk.biz.data.ServiceResponse;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class ChangeWorkPhoneMemElement extends ChangeMemElement
{
	private static final Logger logger = Logger.getLogger(ChangeWorkPhoneMemElement.class);
    protected IData paramInfo = new DataMap(); // 产品参数
    protected String effectType = "";			// 生效方式

    public ChangeWorkPhoneMemElement()
    {

    }

    /**
     * 生成登记信息
     * 
     * @throws Exception
     */
    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
        paramInfo = getParamInfo();
    }

    /**
     * 生成其它台帐数据（生成台帐后）
     * 
     * @author liaoyi
     * @throws Exception
     */
    @Override
    public void actTradeSub() throws Exception
    {
        super.actTradeSub();
     // 产品和个性化参数        
        //deleteTradeAttrParam();
        deleteTradeAttrParam1();
        actTradeDiscntParam();
        updateTradeAttrParam();
    }
    
    protected void deleteTradeAttrParam() throws Exception
    {
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        
        IDataset svcDatas = new DatasetList();
        for(int i=0;i<dataset.size();i++)
        {
            IData data = dataset.getData(i);
            String rela_inst_id = data.getString("RELA_INST_ID","");
            String modify_tag = data.getString("MODIFY_TAG","");
            String user_id = data.getString("USER_ID","");
            String element_id = data.getString("ELEMENT_ID","");
            
            if("1".equals(modify_tag))
            {
                IData attrds = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCodeTWO(user_id, rela_inst_id, "114485", element_id, "0898");
                
                if (IDataUtil.isEmpty(attrds))
                {
                    continue;
                }else
                {
                    IData map = new DataMap();
                    map = attrds;
                    map.put("END_DATE", SysDateMgr.getSysDate());
                    //map.put("INST_ID", SeqMgr.getInstId());
                    map.put("RSRV_TAG1", "0");
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    svcDatas.add(map);
                }
                
                
            }else
            {
                continue;
            }
        }
        
        if (svcDatas.size() > 0)
        {
            addTradeAttr(svcDatas);
        }
    }
    
    protected void actTradeDiscntParam() throws Exception
    {
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        
        for(int i=0;i<dataset.size();i++)
        {
            IData data = dataset.getData(i);
            String rela_inst_id = data.getString("RELA_INST_ID","");
            String modify_tag = data.getString("MODIFY_TAG","");
            for(int j=i+1;j<dataset.size();j++)
            {
                IData param = dataset.getData(j);
                String rela_inst_idN = param.getString("RELA_INST_ID","");
                String modify_tagG = param.getString("MODIFY_TAG","");
                
                if(rela_inst_id.equals(rela_inst_idN) && !modify_tag.equals(modify_tagG))
                {             
                    if("0".equals(modify_tag))
                    {
                        data.put("START_DATE", SysDateMgr.firstDayOfMonth(1));
                        data.put("END_DATE", SysDateMgr.getTheLastTime());
                        data.put("RSRV_TAG1", "1");
                        param.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                        break;
                    }else
                    {
                        param.put("START_DATE", SysDateMgr.firstDayOfMonth(1));
                        param.put("END_DATE", SysDateMgr.getTheLastTime());
                        param.put("RSRV_TAG1", "1");
                        data.put("END_DATE", SysDateMgr.getLastDateThisMonth());
                        break;
                    }
                }
            }
        }
    }
    

    /**
     * 获取参数
     * 
     * @return
     * @throws Exception
     */
    public IData getParamInfo() throws Exception
    {
        String memProductId = ProductMebInfoQry.getMemberMainProductByProductId(reqData.getGrpUca().getProductId());
        IData paraminfo = reqData.cd.getProductParamMap(memProductId);
        return paraminfo;
    }

    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();
        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IData data = bizData.getTrade();
        // 主台帐预留字段
        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId()); // A用户标识：对应关系类型参数表中的A角，通常为一集团用户或虚拟用户
        data.put("RSRV_STR2", relationTypeCode);// 关系类型
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());// corpnumber集团编号
        data.put("RSRV_STR6", reqData.getGrpUca().getCustomer().getCustName());
    }
    
    /**
     * REQ201803080006关于新增工作手机折扣的需求（NBZQ需求）
     */
    @Override
    protected void setTradeAttr(IData map) throws Exception {
		if(IDataUtil.isNotEmpty(map)){
			String modifyTag = map.getString("MODIFY_TAG", "");
			String instType = map.getString("INST_TYPE", "");
			String attrCode = map.getString("ATTR_CODE", "");
			String attrVal = map.getString("ATTR_VALUE", "0");
			if("D".equals(instType)&&"114485".equals(attrCode) && ("0".equals(modifyTag)||"2".equals(modifyTag))){
				/*判断工号是否有工作手机套餐折扣权限*/
				String tradeStaffId = CSBizBean.getVisit().getStaffId();
				int iAttrVal = Integer.parseInt(attrVal);
				if(!StaffPrivUtil.isPriv(tradeStaffId, "PRIV_WKPHONE_DISCOUNT", "1")){
					if(iAttrVal>100 || iAttrVal<70){
						CSAppException.apperr(CrmCommException.CRM_COMM_103, "您没有集团工作手机套餐折扣权限，只能填写[70-100]折扣，请确认!");
					}					
				}
			}
		}
		super.setTradeAttr(map);
	}

    @Override
    protected void makInit(IData map) throws Exception
    {
        makUcaForMebNormal(map); // 提前查三户
        
        //批量修改任务处理
        getAll(map);
        
        super.makInit(map);

    }

    @Override
    protected void makUca(IData map) throws Exception
    {
        // 因为在makInit已经查出三户资料了，所以这里不再查
    }
    
    @Override
    protected void setTradeDiscnt(IData map) throws Exception
    {
    	super.setTradeDiscnt(map);
    	String modifyTag = map.getString("MODIFY_TAG", "");
    	if("0".equals(modifyTag)||"2".equals(modifyTag)){
    		String discntCode=map.getString("DISCNT_CODE", "");
        	
        	if(discntCode.equals("10010201")||discntCode.equals("10010202")||discntCode.equals("10010203")||discntCode.equals("10010204")
        			||discntCode.equals("10010205")||discntCode.equals("10010206")||discntCode.equals("10010207")||discntCode.equals("10010208")||discntCode.equals("10010209")
        			||discntCode.equals("10010210")||discntCode.equals("10010211")||discntCode.equals("10010212")){
        		tradeDiscntSeting(map);
        	}else{
        		IDataset paramCommpara=ParamInfoQry.getCommparaByAttrCode("CSM","6013","GPWP",discntCode,"0898");
        		logger.info("CreateWorkPhoneGroupMember-setTradeDiscnt-paramCommpara:"+paramCommpara);
        		if(paramCommpara.size()>0){
                	for(int i =0;i<paramCommpara.size();i++){
                		IData result=paramCommpara.getData(i);
                		if(result.getString("PARA_CODE1", "").equals(discntCode)){
                			tradeDiscntSeting(map);
                			break;
                		}
                	}
                }
        	}
    	}
    	
    }
    
    private void tradeDiscntSeting(IData map) throws Exception{
    	String discntCode=map.getString("DISCNT_CODE", "");
    	IDataset dataset = reqData.cd.getElementParam();
    	
    	String money="";
    	String minute="";
    	String discount="";
    	String flow="";
    	
    	IDataset offer_type_code_list = new DatasetList();

        IData iData = new DataMap();
        iData.put("OFFER_CODE", discntCode);
        iData.put("OFFER_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
        offer_type_code_list.add(iData);    	
        IData input = new DataMap();
		input.put("OFFER_TYPE_CODE_LIST", offer_type_code_list);
		ServiceResponse response =BizServiceFactory.call("UPC.Out.OfferQueryFSV.qryOfferNamesByOfferTypesOfferCodes", input, null);
		logger.info("CreateWorkPhoneGroupMember-tradeDiscntSeting-response:"+response.toString());
		IData out = response.getBody();
        IDataset outList=out.getDataset("OUTDATA");
        String discntCodeName="相关优惠";
        if(!IDataUtil.isEmpty(outList)){
        	discntCodeName=outList.getData(0).getString("OFFER_NAME","");
        }
        
        if("10010203".equals(discntCode)||"10010204".equals(discntCode)||"10010208".equals(discntCode)||"10010209".equals(discntCode)){
        	for (int row = 0, size = dataset.size(); row < size; row++)
            {   
        		IData idata = dataset.getData(row);
        		if(idata.getString("RELA_INST_ID", "").equals(map.getString("INST_ID", ""))){
        			String attrVlaue=idata.getString("ATTR_VALUE", "");
            		if(idata.getString("ATTR_CODE", "").equals("154485")){//金额
            			DecimalFormat df=new DecimalFormat("#.00");
            			double moneyDouble=Double.parseDouble(attrVlaue);
            			money="月功能费"+df.format(moneyDouble/100)+"元";
            		}else if(idata.getString("ATTR_CODE", "").equals("144485")){//分钟
            			int minuteInt=Integer.parseInt(attrVlaue);
            			minute="包含"+(minuteInt/60)+"分钟";
            		}else if(idata.getString("ATTR_CODE", "").equals("114485")){ //折扣
            			discount="折扣率"+attrVlaue+"%";
            		}else if(idata.getString("ATTR_CODE", "").equals("124485")){ //流量
            			long flowLong=Long.parseLong(attrVlaue);
            			flow="包含"+(flowLong/(1024*1024*1024))+"G流量";
            		}
        		}
        		
            }	
        }else {
        	for (int row = 0, size = dataset.size(); row < size; row++)
            {
        		IData idata = dataset.getData(row);
        		if(idata.getString("RELA_INST_ID", "").equals(map.getString("INST_ID", ""))&&idata.getString("MODIFY_TAG", "").equals("0")){
        			String attrVlaue=idata.getString("ATTR_VALUE", "");
            		if(idata.getString("ATTR_CODE", "").equals("154485")){//金额
            			DecimalFormat df=new DecimalFormat("#.00");
            			double moneyDouble=Double.parseDouble(attrVlaue);
            			money="月功能费"+df.format(moneyDouble/100)+"元";
            		}else if(idata.getString("ATTR_CODE", "").equals("144485")){//分钟
            			int minuteInt=Integer.parseInt(attrVlaue);
            			minute="包含"+(minuteInt/60)+"分钟";
            		}else if(idata.getString("ATTR_CODE", "").equals("114485")){ //折扣
            			discount="折扣率"+attrVlaue+"%";
            		}else if(idata.getString("ATTR_CODE", "").equals("124485")){ //流量
            			long flowLong=Long.parseLong(attrVlaue);
            			flow="包含"+(flowLong/(1024*1024))+"M流量";
            		}
        		}
        		
            }
        }
    	
    	String startTime = "";
    	if(IDataUtil.isNotEmpty(dataset)){
    		String ELEMENTID = dataset.getData(0).getString("ELEMENT_ID","");
    		String ELEMENTID2 = map.getString("ELEMENT_ID","");
    		if(ELEMENTID.equals(ELEMENTID2)){
    			startTime = dataset.getData(0).getString("START_DATE", "");
    		}else{
    			startTime = map.getString("START_DATE");
    		}
    	}else {
    		startTime = map.getString("START_DATE");
    	}
    	if(discntCode.equals("10010201")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		if(!discount.equals("")){
    			remark+=","+discount;
    		}
    		if(!minute.equals("")){
    			remark+=","+minute;
    		}
    		map.put("REMARK",remark);
    	}else if(discntCode.equals("10010202")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		if(!discount.equals("")){
    			remark+=","+discount;
    		}
    		if(!flow.equals("")){
    			remark+=","+flow;
    		}
    		map.put("REMARK",remark);
    	}else if(discntCode.equals("10010203")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		
    		if(!minute.equals("")){
    			remark+=","+minute;
    		}
    		map.put("REMARK",remark);
    		
    	}else if(discntCode.equals("10010204")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		if(!flow.equals("")){
    			remark+=","+flow;
    		}
    		map.put("REMARK",remark);
    		
    	}else if(discntCode.equals("10010208")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		if(!discount.equals("")){
    			remark+=","+discount;
    		}
    		if(!minute.equals("")){
    			remark+=","+minute;
    		}
    		map.put("REMARK",remark);
    		
    		
    	}else if(discntCode.equals("10010209")){
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!money.equals("")){
    			remark+=","+money;
    		}
    		if(!discount.equals("")){
    			remark+=","+discount;
    		}
    		if(!flow.equals("")){
    			remark+=","+flow;
    		}
    		map.put("REMARK",remark);
    	}else if(discntCode.equals("10010205")||discntCode.equals("10010206")||discntCode.equals("10010207")){
    		String remark="于"+startTime+"生效";
    		map.put("REMARK",remark);
    	}
    	else{
    		String remark=discntCodeName+"于"+startTime+"生效";
    		if(!discount.equals("")){
    			remark+=","+discount;
    		}
    		map.put("REMARK",remark);
    	}
    	logger.info("ChangeWorkPhoneMemElement setTradeDiscnt map-REMARK:"+map.getString("REMARK", ""));

    	
    }
	
	/**
     * 批量修改任务处理
     * @param map
     * @throws Exception
     */
    private void getAll(IData map) throws Exception{
    	String batType = map.getString("BATCH_OPER_TYPE","");
    	if ("BATCHGEWPGRPMEMINFO".equals(batType)) {
    		
    		//String attrValue = "";
    		String attrCode = "";
    		String modifyTag = "";
    		String startDate = "";
    		String endDate = "";
    		String elementTypeCode="";
    		String remark ="";
    		IDataset attrList = new DatasetList();
    		//获取信息
    		IDataset elementInfo = map.getDataset("ELEMENT_INFO",new DatasetList());
    		if (elementInfo.size() > 0) {
    			IData info = (IData)elementInfo.get(0);
    			attrList = info.getDataset("ATTR_PARAM");
    			//attrValue = attrList.getData(0).getString("ATTR_VALUE", "");
    			attrCode = attrList.getData(0).getString("ATTR_CODE", "");
    			modifyTag = info.getString("MODIFY_TAG", "");
    			startDate = info.getString("START_DATE", "");
    			endDate = info.getString("END_DATE", "");
    			elementTypeCode = info.getString("ELEMENT_TYPE_CODE", "");
    			remark = info.getString("REMARK", "");
			}
    		
    		String userId = reqData.getUca().getUserId();
    		IDataset userAttrList = UserAttrInfoQry.qryBbossUserAttrForGroupNew(userId,null,attrCode);
    		IDataset configList = CommparaInfoQry.getCommparaByAttrCode1("CSM", "9335","601301", "ZZZZ", null);	//配置的套餐
    		
    		//重构报文
    		IDataset eleInfo = new DatasetList();
    		for (int i = 0; i < userAttrList.size(); i++) {
    			IData userAttr = (IData)userAttrList.get(i);
    			String elementId = userAttr.getString("ELEMENT_ID");
    			
    			boolean istrue = false;
    			for (int j = 0; j < configList.size(); j++) {
    				String paramCode = configList.getData(j).getString("PARAM_CODE","");
    				if (elementId.equals(paramCode)) istrue=true;
				}
    			if (!istrue)  continue;			//仅修改配置的套餐
    			
    			IData info = new DataMap();
    			info.put("ELEMENT_ID", elementId);
    			info.put("ATTR_PARAM", attrList);
    			info.put("MODIFY_TAG", modifyTag);
    			info.put("START_DATE", startDate);
    			info.put("END_DATE", endDate);
    			info.put("ELEMENT_TYPE_CODE", elementTypeCode);
    			info.put("REMARK", remark);
    			eleInfo.add(info);
			}
    		map.put("ELEMENT_INFO", eleInfo);
    		effectType = remark;
		}
    }
    
    
    protected void updateTradeAttrParam() throws Exception{
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        String batType = reqData.getBatchOperType();
    	if ("BATCHGEWPGRPMEMINFO".equals(batType) &&"0".equals(effectType)) {
    		String time =SysDateMgr.getSysTime();
    		for (int i = 0; i < dataset.size(); i++) {
    			IData data = dataset.getData(i);
    			String modify_tag = data.getString("MODIFY_TAG","");
                
                if("0".equals(modify_tag)){
                	data.put("START_DATE", time);
                }else if ("1".equals(modify_tag)) {
                	data.put("END_DATE", time);
				}
			}
    	}
    }
    
    protected void deleteTradeAttrParam1() throws Exception
    {
        IDataset dataset = bizData.getTradeAttr();
        if (IDataUtil.isEmpty(dataset))
        {
            return;
        }
        
        IDataset svcDatas = new DatasetList();
        for(int i=0;i<dataset.size();i++)
        {
            IData data = dataset.getData(i);
            String rela_inst_id = data.getString("RELA_INST_ID","");
            String modify_tag = data.getString("MODIFY_TAG","");
            String user_id = data.getString("USER_ID","");
            String element_id = data.getString("ELEMENT_ID","");
            String attrCode = data.getString("ATTR_CODE","");
            
            if(!"10010201".equals(element_id)){
            	//continue;
            }
            
            if("1".equals(modify_tag))
            {
                IData attrds = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCodeTWO(user_id, rela_inst_id, attrCode, element_id, "0898");
                if (IDataUtil.isEmpty(attrds))
                {
                    continue;
                }else
                {
                    IData map = new DataMap();
                    map = attrds;
                    map.put("END_DATE", SysDateMgr.getSysDate());
                    //map.put("INST_ID", SeqMgr.getInstId());
                    map.put("RSRV_TAG1", "0");
                    map.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());
                    svcDatas.add(map);
                }
            }else
            {
                continue;
            }
        }
        
        if (svcDatas.size() > 0)
        {
            addTradeAttr(svcDatas);
        }
    }
}
