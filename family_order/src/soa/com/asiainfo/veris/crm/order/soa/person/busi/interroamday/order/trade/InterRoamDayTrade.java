
package com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.trade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.exception.IBossException;
import com.asiainfo.veris.crm.order.pub.exception.InterRoamDayException;
import com.asiainfo.veris.crm.order.pub.exception.ParamException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.ProductModuleData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.BusiTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.AttrTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DeferTradeFeeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherFeeTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OtherTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.ITrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.BaseTrade;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.impl.product.process.ProductModuleCreator;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.InterRoamDayBean;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata.InterRoamDayReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.interroamday.order.requestdata.InterRoamDayRequestData;

public class InterRoamDayTrade extends BaseTrade implements ITrade
{

	public void createBusiTradeData(BusiTradeData btd) throws Exception
    {


        InterRoamDayReqData reqdata = (InterRoamDayReqData) btd.getRD();
        String userType =  reqdata.getUserType() ;
        String prodId   =  reqdata.getProdId() ;
        String prodType = reqdata.getProdType() ;
        String relationStat = reqdata.getRelation_stat(); //01新增 ,02修改
        String prodState  = reqdata.getProdStat();
        
    	UcaData uca = btd.getRD().getUca();

        if("00".equals(userType)){
        	
        	if("01".equals(relationStat)){
        		
        		String  instId = SeqMgr.getInstId();
        		this.addTableTradeDiscnt(btd, reqdata, instId,"0",reqdata.getExpireDate());
        		this.addTableTradeOther(btd, reqdata ,instId, "0");
        		
        		//change by huangyq  2019-04-18  国漫产品订购激活退订已调接口AM_CRM_DoRomanAccep同步费用
        		// 注释  避免重复计费 
//        		this.addTableDeferTradeFee(btd);
        		// 新增费用子台账
        		this.addOtherFeeTrade(btd, reqdata, prodState);
        		
        	}else if("02".equals(relationStat)){
        		        		
        		IDataset discntInfo = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(uca.getUserId(),prodId);
        		
        				
        		if(IDataUtil.isNotEmpty(discntInfo)){
        			
        			for(int i= 0;i<discntInfo.size();i++){
        				
        				String inst_id = discntInfo.getData(i).getString("INST_ID");
        				
        				if(inst_id.equals(reqdata.getActiveInstId())){
        					
        					this.addTableTradeDiscnt(btd, reqdata, inst_id,"2",reqdata.getEndTime());
        				}
        						
        			}
        			
        		}else if("01".equals(prodState)&&IDataUtil.isEmpty(discntInfo)){
            		createAttrTradeData(btd,reqdata,"PROD_INST_ID",reqdata.getProdInsId(),SeqMgr.getInstId(),"2",reqdata.getEndTime());

				}
        		// 新增费用子台账
        		this.addOtherFeeTrade(btd, reqdata, prodState);
        		
        	}else if("03".equals(relationStat))//表示退订
        	{
        		String  instId = SeqMgr.getInstId();
        		this.addTableTradeDiscnt(btd, reqdata, instId,"1",SysDateMgr.getSysTime());
        		this.addTableTradeOther(btd, reqdata ,instId,"1");
        	}
        	
        }
        
    }
    
    /**
     * 
     * 
     */
    
    private void addTableTradeDiscnt(BusiTradeData bd, InterRoamDayReqData reqdata ,
	String instId ,String modifyTag,  String endDate) throws Exception
    {
    	DiscntTradeData discntData = new DiscntTradeData();
    	
    	UcaData uca = bd.getRD().getUca();
    	discntData.setElementId(reqdata.getProdId());
    	discntData.setElementType("D");
    	discntData.setInstId(instId);
    	discntData.setUserIdA("-1");
    	discntData.setUserId(bd.getRD().getUca().getUserId());
    	discntData.setStartDate(reqdata.getValidDate());
		discntData.setEndDate(endDate);
		String sProdInstID=bd.getRD().getPageRequestData().getString("SPROD_INST_ID", "");
    	if(!(null == sProdInstID || "".equals(sProdInstID))){
    		createAttrTradeData(bd,reqdata,"PRD_ID",reqdata.getProdId(),instId,modifyTag,endDate);
    		createAttrTradeData(bd,reqdata,"PROD_INST_ID",reqdata.getProdInsId(),instId,modifyTag,endDate);
    		createAttrTradeData(bd,reqdata,"SPROD_INST_ID",sProdInstID,instId,modifyTag,endDate);
		}else{
    	// 在 退订下发时 上面if 不会进入 为防止 在 TradeOtherFee中取值空指针，在此添加台账 add by huangyq
     	createAttrTradeData(bd,reqdata,"PROD_INST_ID",reqdata.getProdInsId(),instId,modifyTag,endDate);
		}
 	    if(null == endDate || "".equals(endDate)){
    		discntData.setEndDate(reqdata.getExpireDate());
		}
		
    	discntData.setModifyTag(modifyTag);
    	discntData.setProductId("-1");
    	discntData.setRemark("国漫套餐");
    	discntData.setRsrvDate1(reqdata.getFirstTime());
    	
    	if(!"6".equals(bd.getMainTradeData().getInModeCode())){
    		
    		if(null == reqdata.getValidDate() || "".equals(reqdata.getValidDate())){
    			discntData.setStartDate(SysDateMgr.getSysTime());
    		}
    		if(null == reqdata.getExpireDate() || "".equals(reqdata.getExpireDate())){
    			discntData.setEndDate(SysDateMgr.END_DATE_FOREVER);
    		}
    	}
    	
    	discntData.setPackageId("99990000");
    	
    	bd.add(reqdata.getUca().getSerialNumber(), discntData);
    }

    /**
     * 设置其他台帐子表的数据
     * 
     * @param pd
     * @param td
     * @throws Exception
     */
    private void addTableTradeOther(BusiTradeData bd, InterRoamDayReqData reqdata ,String instId ,String modifyTag) throws Exception
    {
    	OtherTradeData otherData = new OtherTradeData();
        otherData.setRsrvValueCode("ROAM");
        otherData.setRsrvValue("国漫套餐订购关系同步");
        otherData.setUserId(reqdata.getUca().getUserId());
        otherData.setStartDate(SysDateMgr.getSysTime());
        otherData.setEndDate(SysDateMgr.getTheLastTime());
        otherData.setStaffId(CSBizBean.getVisit().getStaffId());
        otherData.setDepartId(CSBizBean.getVisit().getDepartId());
        otherData.setRsrvStr1(reqdata.getProdInsId());
        otherData.setRsrvStr2(instId);
        otherData.setInstId(SeqMgr.getInstId());
        otherData.setModifyTag(modifyTag);
        bd.add(reqdata.getUca().getSerialNumber(), otherData);
    }
    
    
    /**
	 * 海南、云南newbiling同步定向套餐费用，TF_B_TRADEFEE_DEFER表
	 * @param btd
	 * @throws Exception 
	 */
	private void addTableDeferTradeFee(BusiTradeData btd) throws Exception {
		// TODO Auto-generated method stub
		DeferTradeFeeData deferTradeFee = new DeferTradeFeeData();
    	InterRoamDayBean bean = new InterRoamDayBean();
    	String discntCode = btd.getRD().getPageRequestData().getString("ELEMENT_ID", "");
//        IData newResult = bean.getPackageDiscnt("99990000", discntCode);
    	IData newResult=new DataMap();
        IDataset discntAll=UPackageElementInfoQry.getElementInfoByGroupId("99990000");        
        for(int i=0; i<discntAll.size(); i++){
        	  IData temp=discntAll.getData(i);
        	  if(temp.getString("ELEMENT_ID").equals(discntCode)){
        		  newResult.putAll(temp);
        	  }
        }
        
        String newDiscntTag = newResult.getString("RSRV_TAG1","");
        if(!"2".equals(newDiscntTag))
        {
        	return; //只定向套餐才需要继续往下执行
        }
        String fee = newResult.getString("RSRV_STR1");//费用以分计算
        /**
         * 国漫订购关系同步报文
         * <br/>
         * 修改获取费用的方式,如果集团传过来有值，则读取集团的费用值.
         * @author zhuoyingzhi
         * @date 20180605
         */
        //集团传的值，单位是元   例如：108.02
        String groupFee=((InterRoamDayReqData) btd.getRD()).getFee();
        System.out.println("----InterRoamDayTrade-----groupFee:"+groupFee);
        if(!"".equals(groupFee) && groupFee!=null){
        	//  .,则去掉
        	Double groupFeeDoble=Double.valueOf(groupFee);
        	       groupFeeDoble=groupFeeDoble*100;
        	fee=String.valueOf(groupFeeDoble);
        }
        System.out.println("----InterRoamDayTrade-----fee:"+fee);
        /**************结束******************/
        String strArea = newResult.getString("RSRV_STR3"); //产品标识
        String days = newResult.getString("RSRV_STR4");
        String strParea = strArea + days;
        String deferItemCode = "";       
        //IData feeParam = CommparaInfoQry.getCommNetInfo("CSM", "3738", days).getData(0);
        IDataset feeParams = CommparaInfoQry.getCommNetInfo("CSM", "3738", days);
        if(IDataUtil.isNotEmpty(feeParams))
        {
        	for (int i = 0; i < feeParams.size(); i++) {
        		IData feeParam = feeParams.getData(i);
        		String strComArea = feeParam.getString("PARA_CODE21", "");
        		if(strComArea.indexOf(strParea) >= 0)
        		{
        			deferItemCode = feeParam.getString("PARA_CODE1");
        			break;
        		}
        		else if(StringUtils.isBlank(strComArea))
        		{
        			deferItemCode = feeParam.getString("PARA_CODE1");
        		}
        		
			}
        	 //deferItemCode = feeParam.getString("PARA_CODE1");
        }
        
        deferTradeFee.setUserId(btd.getRD().getUca().getUserId());
        deferTradeFee.setFeeMode("0");
        deferTradeFee.setFeeTypeCode("0");
        deferTradeFee.setDeferCycleId("-1");
        deferTradeFee.setDeferItemCode(deferItemCode);
        deferTradeFee.setMoney(fee);
        deferTradeFee.setActTag("1");
        deferTradeFee.setRsrvStr1("ONCEFEE");// 标识为BBOSS一次性费用    	 
        deferTradeFee.setRemark("国漫数据流量定向套餐一次性费用");
  
        btd.add(btd.getRD().getUca().getSerialNumber(), deferTradeFee);
	}

	/**
	 * 海南 国漫同步 新增费用子台账  add by huangyq
	 * @param btd
	 * @param reqdata
	 * @param prodState
	 * @throws Exception
	 */
	private void addOtherFeeTrade(BusiTradeData btd, InterRoamDayReqData reqdata, String prodState) throws Exception{
		IDataset commparaTimeSet = CommparaInfoQry.getCommpara("CSM", "2979", "ROAM_CANCEL", CSBizBean.getTradeEparchyCode());
		
		IDataset commparaTimeSet1 = CommparaInfoQry.getCommparaByCode1("CSM", "2979", "ROAM_DISHIS_USER", btd.getRD().getUca().getSerialNumber(), null);
		
		if(IDataUtil.isNotEmpty(commparaTimeSet))
    	{
    		String timeSet = commparaTimeSet.getData(0).getString("PARA_CODE1");
    		String startDate = reqdata.getValidDate();
    		if(null == startDate || "" == startDate){
    			startDate = SysDateMgr.getSysTime();
    		}
    		if(SysDateMgr.decodeTimestamp(timeSet,"yyyy-MM-dd HH:mm:ss")
    				.compareTo(SysDateMgr.decodeTimestamp(startDate,"yyyy-MM-dd HH:mm:ss"))>0)
    		{//配置上线时间点之前订购的资费，订购操作已处理，现有流程直接不处理 add by huangyq
    			if(IDataUtil.isEmpty(commparaTimeSet1)){//如果不在特殊处理的数据里
    				return;
    			}else{//如果在特殊处理的数据里，但时间不在订购范围
    				if(!(startDate.compareTo(commparaTimeSet1.getData(0).getString("PARA_CODE2"))>0
    						&&startDate.compareTo(commparaTimeSet1.getData(0).getString("PARA_CODE3"))<0
    						)){
    					return ;
    				}
    			}
    		}
    	}
		else
		{
			CSAppException.apperr(ParamException.CRM_PARAM_505, "参数表中，国漫资费退订开始时间点未配置");
		}
		OtherFeeTradeData otherFeeTD = new OtherFeeTradeData();
		
		String groupFee=((InterRoamDayReqData) btd.getRD()).getFee();
	
        if(!"".equals(groupFee) && groupFee!=null && Double.valueOf(groupFee) >0){
        	Double groupFeeDoble=Double.valueOf(groupFee)*100;
	        groupFee=String.valueOf(groupFeeDoble);
        
	        otherFeeTD.setUserId(btd.getRD().getUca().getUserId());// 源账户user_id
			otherFeeTD.setAcctId(btd.getRD().getUca().getAcctId()); 
			otherFeeTD.setOperFee(groupFee); // 单位 分
			otherFeeTD.setPaymentId("0"); // 表中字段 不能为空
			otherFeeTD.setOperType(BofConst.OTHERFEE_ROAMFEE_TRANS); // 新增费用类型
			otherFeeTD.setStartDate(SysDateMgr.getSysDate());
	        otherFeeTD.setEndDate(SysDateMgr.getTheLastTime());
			String operType = "";
	        if("00".equals(prodState))
	        {
	        	operType = "1";
	        }
	        else if("02".equals(prodState))
	        {
	        	operType = "2";
	        }
	        else if("04".equals(prodState))
	        {
	        	operType = "3";
	        }
	        else
	        {
	        	operType = "4";
	        }
	        
        /*IDataset commparaSet = CommparaInfoQry.getCommparaInfoIPSvc1("CSM", "2787","GPRS_ROAM",reqdata.getProdId());
		String detailItemCode = "";
		if(IDataUtil.isEmpty(commparaSet)){
			detailItemCode="17762";
		}else{
			detailItemCode= commparaSet.getData(0).getString("PARA_CODE2");
		}*/
        String detailItemCode=getDetailItemCode(reqdata.getProdId());
	        otherFeeTD.setRsrvStr1(operType);
	        otherFeeTD.setRsrvStr2(detailItemCode);
	        otherFeeTD.setRemark("国漫产品订购激活退订 RsrvStr1:操作类型； RsrvStr2:账目编码；");
	        //  设置初始状态 9999-待完工
	        otherFeeTD.setRsrvStr6("9999");
	        otherFeeTD.setRsrvStr7("待完工处理");
	        
			btd.add(reqdata.getUca().getSerialNumber(), otherFeeTD);
        }
	}
	
	private void createAttrTradeData(BusiTradeData bd, InterRoamDayReqData reqdata ,String attrCode,String attrValue,
    		String instId ,String modifyTag , String endDate) throws Exception{
    	AttrTradeData attrTradeData = new AttrTradeData();
		attrTradeData.setAttrCode(attrCode);
		attrTradeData.setAttrValue(attrValue);
		attrTradeData.setModifyTag(modifyTag);
		attrTradeData.setUserId(reqdata.getUca().getUserId());							
		attrTradeData.setStartDate(reqdata.getValidDate());
		attrTradeData.setEndDate(endDate);
		attrTradeData.setRelaInstId(instId);
		attrTradeData.setInstType("D");
		attrTradeData.setInstId(SeqMgr.getInstId());
		attrTradeData.setElementId(reqdata.getProdId());
		bd.add(reqdata.getUca().getSerialNumber(), attrTradeData);
    }
	/**
	 * 获取账目编码
	 * @param prodId
	 * @return
	 */
	private String getDetailItemCode(String prodId)throws Exception{
		IData newResult=new DataMap();
		IDataset discntAll=UPackageElementInfoQry.getElementInfoByGroupId("99990000");        
        for(int i=0; i<discntAll.size(); i++){
        	  IData temp=discntAll.getData(i);
        	  if(temp.getString("ELEMENT_ID").equals(prodId)){
        		  newResult.putAll(temp);
        	  }
        }
        String strArea = newResult.getString("RSRV_STR3"); //产品标识
        String days = newResult.getString("RSRV_STR4");
        String strParea = strArea + days;
        String deferItemCode = "";       
        IDataset feeParams = CommparaInfoQry.getCommNetInfo("CSM", "3738", days);
        if(IDataUtil.isNotEmpty(feeParams))
        {
        	for (int i = 0; i < feeParams.size(); i++) {
        		IData feeParam = feeParams.getData(i);
        		String strComArea = feeParam.getString("PARA_CODE21", "");
        		if(strComArea.indexOf(strParea) >= 0)
        		{
        			deferItemCode = feeParam.getString("PARA_CODE1");
        			break;
        		}
        		else if(StringUtils.isBlank(strComArea))
        		{
        			deferItemCode = feeParam.getString("PARA_CODE1");
        		}
        		
			}
        }
        return deferItemCode;
	}
	
	public static void main(String args[]) throws Exception{
		
		
		
		System.out.println(SysDateMgr.decodeTimestamp("20190718200004", "yyyy-MM-dd HH:mm:ss")
		.compareTo(SysDateMgr.decodeTimestamp("2019-07-18 21:00:04", "yyyy-MM-dd HH:mm:ss")));
	}
	
}


