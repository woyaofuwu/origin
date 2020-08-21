
package com.asiainfo.veris.crm.order.soa.person.busi.changeproduct;

import java.util.ArrayList;
import java.util.List;

import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.collections.CollectionUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.DiscntPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnv;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.AcctTimeEnvManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.cust.UCustBlackInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.cfg.ProductElementsCache;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.OfferRelTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.ProductTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.SvcTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.flow.FlowInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.GrpUserPkgInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserNpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSaleActiveInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcStateInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.utils.ProductUtils;

public class ChangeProductBean extends CSBizBean
{
	protected static Logger log = Logger.getLogger(ChangeProductBean.class);
	
	/**
	 * @Description: 获取取消营销活动时间
	 * @param userId
	 * @param bookingDate
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Aug 29, 2014 10:46:31 AM
	 */
	public String getCancelSaleActiveDate(String userId, String bookingDate) throws Exception
	{
		// 获取用户账期
		IDataset userAcctDays = UcaInfoQry.qryUserAcctDaysByUserId(userId);

		String acctDay = "";
		String firstDay = "";
		String nextAcctDay = "";
		String nextFirstDay = "";
		String startDate = "";
		String nextStartDate = "";
		if (IDataUtil.isNotEmpty(userAcctDays))
		{
			int size = userAcctDays.size();
			IData userAcctDay = userAcctDays.getData(0);
			acctDay = userAcctDay.getString("ACCT_DAY");
			firstDay = userAcctDay.getString("FIRST_DATE");
			startDate = userAcctDay.getString("START_DATE");
			if (size > 1)
			{
				IData userNextAcctDay = userAcctDays.getData(1);
				nextAcctDay = userNextAcctDay.getString("ACCT_DAY");
				nextFirstDay = userNextAcctDay.getString("FIRST_DATE");
				nextStartDate = userNextAcctDay.getString("START_DATE");
			}
			AcctTimeEnv env = new AcctTimeEnv(acctDay, firstDay, nextAcctDay, nextFirstDay, startDate, nextStartDate);
			AcctTimeEnvManager.setAcctTimeEnv(env);
		}

		String cancelDate = "";

		if (ProductUtils.isBookingChange(bookingDate))
		{
			int bookingDateDay = SysDateMgr.getIntDayByDate(bookingDate);
			int userAcctDay = 1;

			AcctTimeEnv env = AcctTimeEnvManager.getAcctTimeEnv();
			if (env != null)
			{
				userAcctDay = Integer.parseInt(env.getAcctDay());
			}

			if (bookingDateDay != userAcctDay)
			{
				cancelDate = SysDateMgr.getAddMonthsLastDay(1, bookingDate);
			}
			else
			{
				cancelDate = SysDateMgr.getAddMonthsLastDay(-1, bookingDate);
			}
		}
		else
		{
			cancelDate = SysDateMgr.getLastDateThisMonth();
		}

		return cancelDate;
	}

	/**
	 * @Description: 获取可取消营销活动数据集
	 * @param userId
	 * @param oldBrand
	 * @param newBrand
	 * @param oldProductId
	 * @param newProductId
	 * @param bookingDate
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 26, 2014 5:57:32 PM
	 */
	public IDataset getCancelSaleActiveList(String userId, String oldBrand, String newBrand, String oldProductId, String newProductId, String bookingDate) throws Exception
	{
		String cancelDate = this.getCancelSaleActiveDate(userId, bookingDate);

		// TF_F_USER_SALE_NEW 此表已经废止 数据已合并至TF_F_USER_SALE_ACTIVE 只查TF_F_USER_SALE_ACTIVE即可
		IDataset cancelSaleActives = UserSaleActiveInfoQry.queryCancelSaleActives(userId, oldBrand, newBrand, oldProductId, newProductId, cancelDate);

		return cancelSaleActives;
	}

	/**
	 * @Description: 产品变更取消营销活动提示
	 * @param userId
	 * @param oldBrand
	 * @param newBrand
	 * @param oldProductId
	 * @param newProductId
	 * @param bookingDate
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 26, 2014 6:00:50 PM
	 */
	public IData getCancelSaleActiveTag(String userId, String oldBrand, String newBrand, String oldProductId, String newProductId, String bookingDate) throws Exception
	{
		IData returnData = new DataMap();
		returnData.clear();

		String saleInfo = "";

		IDataset cancelSaleActiveList = this.getCancelSaleActiveList(userId, oldBrand, newBrand, oldProductId, newProductId, bookingDate);

		if (IDataUtil.isNotEmpty(cancelSaleActiveList))
		{
			saleInfo += getSaleActiveMessage(cancelSaleActiveList);

			saleInfo = "您已办理活动:\r\n" + saleInfo + "，\r\n产品变更后活动将失效，是否继续办理?";

			returnData.put("SALEACTIVE_CANCEL_TAG", "1");
			// returnData.put("SALEACTIVE_CANCEL_DATE", cancelDate);
			returnData.put("MESSAGE", saleInfo);
		}

		return returnData;
	}

	/**
	 * 获取大客户是否需要取消数据
	 * 
	 * @param custId
	 * @param oldBrand
	 * @param newBrand
	 * @return
	 * @throws Exception
	 */
	public IData getCustVipIsCancelTag(String custId, String oldBrand, String newBrand) throws Exception
	{
		IData returnData = new DataMap();
		returnData.clear();

		IDataset custVipInfo = CustVipInfoQry.qryVipInfoByCustId(custId);

		if (IDataUtil.isNotEmpty(custVipInfo))
		{
			// 用户大客户资格处理
			String vipTypeCode = custVipInfo.getData(0).getString("VIP_TYPE_CODE", "");
			String vipClassId = custVipInfo.getData(0).getString("VIP_CLASS_ID", "");

			if (!"G001".equals(newBrand) && !"G002".equals(newBrand))
			{
				if ("0".equals(vipTypeCode) || "2".equals(vipTypeCode))
				{
					String className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);

					returnData.put("CUST_VIP_CANCEL_TAG", "1");
					returnData.put("CLASS_NAME", className);
				}
			}
			else
			{
				returnData.put("CUST_VIP_CANCEL_TAG", "0");
			}

			// 全球通换到神州行时，取消个人大客户
			if ("G001".equals(oldBrand) && "G002".equals(newBrand))
			{
				if ("0".equals(vipTypeCode) || "2".equals(vipTypeCode))
				{
					String className = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipTypeCode, vipClassId);

					returnData.put("CUST_VIP_CANCEL_TAG", "1");
					returnData.put("CLASS_NAME", className);
				}
			}
		}
		return returnData;
	}

	/**
	 * @Description: 获取新VPMN优惠
	 * @param newProductId
	 * @param oldVpmnDiscnt
	 * @param eparchyCode
	 * @param bookingDate
	 * @param vpmnUserIdA
	 * @param vpmnProductId
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 26, 2014 2:19:34 PM
	 */
	public IData getNewVpmnDiscnt(String newProductId, String oldVpmnDiscnt, String eparchyCode, String bookingDate, String vpmnUserIdA, String vpmnProductId) throws Exception
	{
		IData result = new DataMap();

		String tradeStaffId = CSBizBean.getVisit().getStaffId();

		IDataset prdForDis = ProductElementsCache.getForceDiscntElements(newProductId);

		String discntAllId = "";
		String finalDiscnt = "";

		if (prdForDis != null && prdForDis.size() > 0)
		{
			for (int i = 0; i < prdForDis.size(); i++)
			{
				String discntId = prdForDis.get(i, "ELEMENT_ID").toString();

				discntAllId += discntId + ",";
			}
		}

		if (discntAllId.trim().length() > 0)
		{
			finalDiscnt = discntAllId.substring(0, discntAllId.length() - 1);
		}
		else
		{
			//finalDiscnt = "''";   
			finalDiscnt = "";   //modify  by duhj  2017/4/26  写在代码里面的sql,表已经废弃,改为产商品接口了

		}

		if (ProductUtils.isBookingChange(bookingDate) && !"655".equals(oldVpmnDiscnt))
		{
			IDataset commpara1139 = CommparaInfoQry.getCommParas("CSM", "1139", "VPMN", newProductId, eparchyCode);

			if (IDataUtil.isNotEmpty(commpara1139))
			{
				IDataset vpmnDiscnt = GrpUserPkgInfoQry.getVpmnDiscntByRightLimit(vpmnUserIdA, vpmnProductId, finalDiscnt, tradeStaffId);

				DiscntPrivUtil.filterDiscntListByPriv(tradeStaffId, vpmnDiscnt);

				if (IDataUtil.isNotEmpty(vpmnDiscnt))
				{
					result.put("NEW_VPMN_DISCNT_TAG", "TRUE");
					result.put("NEW_VPMN_DISCNT", vpmnDiscnt);
				}
			}
		}
		else
		{
			if(StringUtils.isNotBlank(oldVpmnDiscnt)){//modify by duhj 加了这个if判断,防止调用产商品接口报错   2017/4/26
//				IDataset vpmnDiscntList = UpcCall.queryOfferRelInfoByTwoOfferOrInversionIfNecessary(BofConst.ELEMENT_TYPE_CODE_DISCNT, oldVpmnDiscnt, BofConst.ELEMENT_TYPE_CODE_DISCNT, finalDiscnt, "0");//ElemLimitInfoQry.getVpmnDiscntByProductIdElementId(oldVpmnDiscnt, finalDiscnt, eparchyCode);
				IDataset vpmnDiscntList = UpcCall.qryOfferRelWithInverse(oldVpmnDiscnt, BofConst.ELEMENT_TYPE_CODE_DISCNT, "0");
				Boolean isVpnmDiscnt = false;
				if(IDataUtil.isNotEmpty(vpmnDiscntList)){
					for(Object obj : vpmnDiscntList){
						IData vpmnDiscnt = (IData) obj;
						for(int i = 0 ; i < prdForDis.size() ; i++){
							if(StringUtils.equals(vpmnDiscnt.getString("OFFER_CODE",""), prdForDis.getData(i).getString("ELEMENT_ID",""))){
								isVpnmDiscnt = true;
								break;
							}
							if(StringUtils.equals(vpmnDiscnt.getString("REL_OFFER_CODE",""), prdForDis.getData(i).getString("ELEMENT_ID",""))){
								isVpnmDiscnt = true;
								break;
							}
						}
					}
				}
				if (isVpnmDiscnt)
				{
					if (StringUtils.isNotBlank(vpmnUserIdA) && StringUtils.isNotBlank(vpmnProductId))
					{
						IDataset vpmnDiscnt = GrpUserPkgInfoQry.getVpmnDiscntByRightLimit(vpmnUserIdA, vpmnProductId, finalDiscnt, tradeStaffId);

						DiscntPrivUtil.filterDiscntListByPriv(tradeStaffId, vpmnDiscnt);

						if (IDataUtil.isNotEmpty(vpmnDiscnt))
						{
							result.put("NEW_VPMN_DISCNT_TAG", "TRUE");
							result.put("NEW_VPMN_DISCNT", vpmnDiscnt);
						}
					}
				}
			}
			

		}

		return result;
	}

	/**
	 * 提示信息拼接
	 * 
	 * @param saleActives
	 * @return
	 * @throws Exception
	 */
	public String getSaleActiveMessage(IDataset saleActives) throws Exception
	{
		String msgInfo = "";

		if (IDataUtil.isNotEmpty(saleActives))
		{
			for (int i = 0; i < saleActives.size(); i++)
			{
				IData each = saleActives.getData(i);

				String productName = each.getString("PRODUCT_NAME", "");
				String packageName = each.getString("PACKAGE_NAME", "");

				productName = StringUtils.isBlank(productName) ? "营销活动" : productName;
				packageName = StringUtils.isBlank(packageName) ? "营销活动" : packageName;

				msgInfo += "".equals(msgInfo) ? productName + "【" + packageName + "】" : "，" + productName + "【" + packageName + "】";
			}
		}

		return msgInfo;
	}

	/**
	 * @Description: 获取用户VPMN数据
	 * @param userId
	 * @return
	 * @throws Exception
	 * @author: maoke
	 * @date: Jul 26, 2014 2:27:29 PM
	 */
	public IData getUserVpmnData(UcaData uca) throws Exception
	{
		IData result = new DataMap();

		IDataset userRelation = RelaUUInfoQry.getRelationUUInfoByDeputySn(uca.getUserId(), "20", null);
		if (IDataUtil.isNotEmpty(userRelation))
		{
			List<DiscntTradeData> userVpmnDiscnt = this.findOldVpmnDiscnts(uca, "20", "80000102");//UserDiscntInfoQry.getUserVPMNDiscnt(userId, "20", "80000102");

			if (ArrayUtil.isNotEmpty(userVpmnDiscnt))
			{
				result.put("OLD_VPMN_DISCNT_TAG", "TRUE");
				
				DiscntTradeData userVpmn = userVpmnDiscnt.get(0);
				OfferCfg offerCfg = OfferCfg.getInstance(userVpmn.getDiscntCode(), BofConst.ELEMENT_TYPE_CODE_DISCNT);
				result.put("OLD_VPMN_DISCNT", userVpmn.getDiscntCode());
				result.put("OLD_VPMN_DISCNT_NAME", offerCfg.getOfferName());
				result.put("OLD_VPMN_START_DATE", userVpmn.getStartDate());
				result.put("VPMN_USER_ID_A", userVpmn.getUserIdA());
				
				List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndRelOfferInsId(userVpmn.getInstId());
				String productId = "";
				if(ArrayUtil.isNotEmpty(offerRels)){
					for(OfferRelTradeData offerRel : offerRels){
						String groupId = offerRel.getGroupId();
						if("80000102".equals(groupId)){
							productId = offerRel.getOfferCode();
							break;
						}
					}
				}
				result.put("VPMN_PRODUCT_ID",productId);
			}
		}

		return result;
	}
	
	private List<DiscntTradeData> findOldVpmnDiscnts(UcaData uca, String relationType, String groupId) throws Exception{
		List<DiscntTradeData> rst = new ArrayList<DiscntTradeData>();
		List<DiscntTradeData> userDiscnts = uca.getUserDiscnts();
		for(DiscntTradeData discnt : userDiscnts){
			if(!"2".equals(discnt.getSpecTag())){
				continue;
			}
			if(!relationType.equals(discnt.getRelationTypeCode())){
				continue;
			}
			List<OfferRelTradeData> offerRels = uca.getOfferRelByRelUserIdAndRelOfferInsId(discnt.getInstId());
			boolean findGroup = false;
			for(OfferRelTradeData offerRel : offerRels){
				if(groupId.equals(offerRel.getGroupId())){
					findGroup = true;
					break;
				}
			}
			if(!findGroup){
				continue;
			}
			if(SysDateMgr.compareTo(discnt.getEndDate(), SysDateMgr.getLastDateThisMonth()) >= 0 && BofConst.MODIFY_TAG_USER.equals(discnt.getModifyTag())){
				rst.add(discnt);
			}
		}
		return rst;
	}
	
	/**
     * 判断是否是两城一家，或者非常假期的优惠
     * @param elementId
     * @param elementType
     * @return 0不是，1是两城一家，2是非常假期
     * @throws Exception
     */
    public int judgeIsCityVacationData(String elementId, String elementType)throws Exception{
    	int result=0;
    	
    	//验证是否两城一家的内容	10000881
    	IDataset cityPackageElement = UpcCall.queryGroupComRel("10000881", elementType, elementId);//PkgElemInfoQry.getServElementByPk("10000881", elementType, elementId);
    	if(IDataUtil.isNotEmpty(cityPackageElement)){
    		result=1;
    	}
    	
    	
    	//验证是否是非常假期的内容	10000880
    	if(result==0){
    		IDataset vocationPackageElement = UpcCall.queryGroupComRel("10000880", elementType, elementId);//PkgElemInfoQry.getServElementByPk("10000880", elementType, elementId);
        	if(IDataUtil.isNotEmpty(vocationPackageElement)){
        		result=2;
        	}
    	}
    	
    	return result;
    }
    
    public String getWarnInfo(int type, String discntName, String inModeCode)throws Exception{
    	StringBuilder result=new StringBuilder();
    	
    	if(type==1){	//两城一家
//    		if(inModeCode!=null&&!inModeCode.equals("5")){
//    			result.append("尊敬的神州行客户：");
//    		}
    		result.append("您已开通神州行");
    		result.append(discntName);
    		result.append("，无需重复办理。了解该服务请回复JSLCYJ。中国移动海南公司");
    	}else if(type==2){		//非常假期
//    		if(inModeCode!=null&&!inModeCode.equals("5")){
//    			result.append("尊敬的动感地带客户：");
//    		}
    		result.append("您已开通");
    		result.append(discntName);
    		result.append("，无需重复办理。了解该服务请回复JSFCJQ。中国移动海南公司");
    	}
    	
    	return result.toString();
    }
   
    
    //流量库存扣减
  	public static void decreaseFlowStock(IData param) throws Exception
  	{
  		param.put("UPDATE_TIME", SysDateMgr.getSysTime());
  		param.put("UPDATE_STAFF_ID", getVisit().getStaffId());
  		param.put("UPDATE_DEPART_ID", getVisit().getDepartId());
  		
  		FlowInfoQry.updateCustDatapckStock(param);
  		
  	}
  	
	public String queryFlowPaymentResult(IData input) throws Exception {
		IData data = new DataMap();
        data.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        data.put("RSRV_STR3", input.getString("ORI_TRANSACTION_ID"));
        
        IDataset dataset = Dao.qryByCode("TF_B_TRADE", "SEL_BY_SN_RSRV", data, Route.getJourDb());
        String statusCode = "";
        
        if(dataset.isEmpty()){
        	dataset = Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_RSRV", data, Route.getJourDb() );
        	if(!dataset.isEmpty()){
        		 statusCode  = "OK"; //订单已完工
        	}
        }else{
        	 statusCode  = dataset.getData(0).getString("SUBSCRIBE_STATE");
        	 if("3_6_D_E_I_M".indexOf(statusCode) < 0 ){
        		 statusCode  = "OK"; //订单正常执行
        	 }
        }

		return statusCode;
	}
	
	public IDataset queryFlowPayment(IData input) throws Exception {
		IData data = new DataMap();
        data.put("SERIAL_NUMBER", input.getString("IDVALUE"));
        data.put("RSRV_STR3", input.getString("TRANS_ID"));
        
        return Dao.qryByCode("TF_BH_TRADE", "SEL_BY_SN_RSRV", data, Route.getJourDb());
        
	}
	
	/**
     * @Description: 一级BOSS移动商城接口1.8-流量直充CRM完工后回写一级BOSS流水表状态
     * @version: v1.0.0
     * @throws Exception
     * @author songxw 20160825
     */
    public void updateIbossCrmResult(IData input) throws Exception
    {
        Dao.executeUpdateByCodeCode("TO_O_FLOWDIRECT_MOBILEMALL", "UPD_CRM_RESULT", input, Route.CONN_CRM_CEN);
    }
    
    /**
     * 自动开通VOLTE服务，4G特殊用户过滤，接口调用
     * @param 
     * @return
     * @throws Exception
     */
    public IData checkVoLTELimitA(String serialNumber) throws Exception
    {
    	IData idResult = new DataMap();
    	IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
    	//UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	if(IDataUtil.isEmpty(userInfo))
    	{
    		String strError = String.format("该服务号码[%s]用户信息不存在！", serialNumber);
    		idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", strError);
        	return idResult;
    	}
    	
    	String strUserID = userInfo.getString("USER_ID");
    	
    	//限制9981 TD_S_COMMPARA配置编码
		IDataset CommparaParas = CommparaInfoQry.getCommByParaAttr("CSM", "9981", "0898");
		if (IDataUtil.isNotEmpty(CommparaParas)) 
		{
			for (int i = 0; i < CommparaParas.size(); i++) 
			{
				IData CommparaPara = CommparaParas.getData(i);
				String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
				String strParamName = CommparaPara.getString("PARAM_NAME", "");
				String strParamCode = CommparaPara.getString("PARAM_CODE", "");
				if ("P".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1)) 
				{

					IDataset productDatas = UserProductInfoQry.getUserProductByUserIdProductId(strUserID, strParaCode1);//uca.getUserProduct(strParaCode1);
					if (IDataUtil.isNotEmpty(productDatas))
					{
						//String strError = String.format("【%s】", serialNumber);
			    		idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				} 
				else if ("S".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{
					boolean bSvc = UserSvcInfoQry.getAutoPayContractState(strUserID, strParaCode1);//uca.getUserSvcBySvcId(strParaCode1);
					if (bSvc) 
					{
						idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						////log.info("("关于下发VoLTE业务自动开通功能支撑系统配套改造工作要求的通知  S" + strParaCode1);
						//return true;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				} 
				else if ("D".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{
					IDataset discntDatas = UserDiscntInfoQry.getAllDiscntByUser(strUserID, strParaCode1); //uca.getUserDiscntByDiscntId(strParaCode1);
					if (IDataUtil.isNotEmpty(discntDatas))
					{
						idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						////log.info("("关于下发VoLTE业务自动开通功能支撑系统配套改造工作要求的通知  D" + strParaCode1);
						//return true;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				}
			}
		}
		
		//如果没有上网服务，则限制
		boolean bSvc22 = UserSvcInfoQry.getAutoPayContractState(strUserID, "22");
		if (!bSvc22) 
		{
			idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户没有上网服务，不能办理VOLTE自动开通服务！");
        	return idResult;
			//return true;
		}
		else
		{
			IDataset idsSvcState22 = UserSvcStateInfoQry.getUserLastStateByUserSvc(strUserID, "22");
			/*if(IDataUtil.isEmpty(idsSvcState22))
			{
				idResult.put("X_RESULTCODE", "2998");
	    		idResult.put("X_RESULTINFO", "用户上网服务状态不正常，不能办理VOLTE自动开通服务。");
	        	return idResult;
			}
			else*/
			if(IDataUtil.isNotEmpty(idsSvcState22))
			{
				IData idSvcState22 = idsSvcState22.first();
				String strSvc22 = idSvcState22.getString("SERVICE_ID", "");
				String strSC = idSvcState22.getString("STATE_CODE", "");
				if(!"0".equals(strSC) && "22".equals(strSvc22))
				{
					idResult.put("X_RESULTCODE", "2998");
		    		idResult.put("X_RESULTINFO", "用户上网服务状态不正常，不能办理VOLTE自动开通服务");
		        	return idResult;
				}
			}
		}
		
		//如果是黑名单，则限制
		/*IData idParamCust = new DataMap();
		idParamCust.put("SERIAL_NUMBER", serialNumber);*/
		IDataset idsCustInfos = CustomerInfoQry.queryCustInfoBySN(serialNumber);
		if (IDataUtil.isEmpty(idsCustInfos))
        {
			String strError = String.format("该服务号码[%s]用户信息不存在！", serialNumber);
    		idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", strError);
    		return idResult;
            // common.error 没有客户资料
			////log.info("("关于下发VoLTE业务自动开通功能支撑系统配套改造工作要求的通知  custInfo 没有客户资料");
			//return true;
        }
		
		IData idcustInfo = idsCustInfos.first();
		IData param = new DataMap();
		String strPsptTypeCode = idcustInfo.getString("PSPT_TYPE_CODE", "");
		String strPsptId = idcustInfo.getString("PSPT_ID", "");
		/*param.clear();
        param.put("PSPT_TYPE_CODE", strPsptTypeCode);
        param.put("PSPT_ID", strPsptId);*/
        IDataset idsBlackUser = UCustBlackInfoQry.qryBlackCustInfo(strPsptTypeCode, strPsptId);
        if (IDataUtil.isNotEmpty(idsBlackUser))//Dao.qryByRecordCount("TD_O_BLACKUSER", "SEL_BY_PK", param)
        {
        	idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是黑名单，不能办理VOLTE自动开通服务！");
        	return idResult;
        }
        
        //如果是测试卡，则限制 
        String strUserTypeCode = userInfo.getString("USER_TYPE_CODE");//uca.getUser().getUserTypeCode();
        if("A".equals(strUserTypeCode))
        {
        	idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是测试卡[NET_TYPE_CODE=A]，不能办理VOLTE自动开通服务！");
        	return idResult;
        	//return true;
        }
		
		//携转用户校验
		//String strUserID = uca.getUserId();
		IDataset idsNpUser = UserNpInfoQry.qryUserNpInfosByUserId(strUserID);
		if(IDataUtil.isNotEmpty(idsNpUser))
		{
			IData idNpUser = idsNpUser.first();
			String strNpTag = idNpUser.getString("NP_TAG", "");
			if("1".equals(strNpTag))
			{		
				//如果是携入用户
				idResult.put("X_RESULTCODE", "2998");
	    		idResult.put("X_RESULTINFO", "用户是携入用户，不能办理VOLTE自动开通服务！");
	        	return idResult;
				//return true;
				
				/*List<SvcTradeData> lsSvcs = uca.getUserSvcs();
				if(CollectionUtils.isNotEmpty(lsSvcs))
				{
					for (int i = 0; i < lsSvcs.size(); i++) 
					{
						SvcTradeData dSvc = lsSvcs.get(i);
						String strModifyTag = dSvc.getModifyTag();
						String strSvcId = dSvc.getElementId();
						if("190".equals(strSvcId) && BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
						{
							//CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入用户不能办理【服务】[190|VOLTE服务]！");
							return true;
						}
					}
				}*/
			}
		}
		
		//如果是红名单，则限制
		/*IData idParamRedUser = new DataMap();
		idParamRedUser.put("SERIAL_NUMBER", serialNumber);
		IDataset idsRedUser = CSAppCall.call("QCC_ITF_GetRedUser", idParamRedUser);
    	if(IDataUtil.isNotEmpty(idsRedUser) && idsRedUser.size() > 0)
    	{
    		//如果是携入用户
			idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是红名单用户，不能办理VOLTE自动开通服务！");
        	return idResult;
    	}*/
		
		idResult.put("X_RESULTCODE", "0");
		idResult.put("X_RESULTINFO", "OK");
		return idResult;
    }
    
	/**
     * 自动开通VOLTE服务，4G特殊用户过滤，界面调用
     * @param 
     * @return
     * @throws Exception
     */
    public IData checkVoLTELimit(UcaData uca) throws Exception
    {
    	IData idResult = new DataMap();
    	//UcaData uca = UcaDataFactory.getNormalUca(serialNumber);
    	/*if(uca == null)
    	{
    		return true;
    	}*/
    	
    	String strUserID = uca.getUserId();
    	
    	//限制9981 TD_S_COMMPARA配置编码
		IDataset CommparaParas = CommparaInfoQry.getCommByParaAttr("CSM", "9981", "0898");
		if (IDataUtil.isNotEmpty(CommparaParas)) 
		{
			for (int i = 0; i < CommparaParas.size(); i++) 
			{
				IData CommparaPara = CommparaParas.getData(i);
				String strParaCode1 = CommparaPara.getString("PARA_CODE1", "");
				String strParamName = CommparaPara.getString("PARAM_NAME", "");
				String strParamCode = CommparaPara.getString("PARAM_CODE", "");
				if ("P".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1)) 
				{

					List<ProductTradeData> productDatas = uca.getUserProduct(strParaCode1);
					if (CollectionUtils.isNotEmpty(productDatas))
					{
		        		  if (log.isDebugEnabled())
		        	        {
		        			  //log.info("("VoLTE业务自动开通 " + strParamName + "用户不能办理VOLTE服务！");
		        	        }
						
						idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						//return true;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				} 
				else if ("S".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{

					List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId(strParaCode1);
					if (CollectionUtils.isNotEmpty(svcDatas)) 
					{
						 if (log.isDebugEnabled())
		        	        {
		        			  //log.info("("VoLTE业务自动开通 " + strParamName + "用户不能办理VOLTE服务！");
		        	        }
						idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						//return true;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				} 
				else if ("D".equals(strParamCode) && StringUtils.isNotBlank(strParaCode1))
				{

					List<DiscntTradeData> discntDatas = uca.getUserDiscntByDiscntId(strParaCode1);
					if (CollectionUtils.isNotEmpty(discntDatas))
					{
						 if (log.isDebugEnabled())
		        	        {
							 //log.info("("VoLTE业务自动开通 " + strParamName + "用户不能办理VOLTE服务！");
		        	        }
						idResult.put("X_RESULTCODE", "2998");
			    		idResult.put("X_RESULTINFO", strParamName + "用户不能办理VOLTE自动开通服务！");
			        	return idResult;
						//return true;
						//CSAppException.apperr(CrmUserException.CRM_USER_783, strParamName + "用户不能办理VOLTE服务！");
					}

				}
			}
		}
		
		//如果没有上网服务，则限制
		List<SvcTradeData> svcDatas = uca.getUserSvcBySvcId("22");
		if (CollectionUtils.isEmpty(svcDatas)) 
		{
			
			 if (log.isDebugEnabled())
 	        {
				 //log.info("("VoLTE业务自动开通 " + "用户没有上网服务，不能办理VOLTE自动开通服务！");
 	        }
			idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户没有上网服务，不能办理VOLTE自动开通服务！");
        	return idResult;
			//return true;
		}
		else
		{
			IDataset idsSvcState22 = UserSvcStateInfoQry.getUserLastStateByUserSvc(strUserID, "22");
			/*if(IDataUtil.isEmpty(idsSvcState22))
			{
				idResult.put("X_RESULTCODE", "2998");
	    		idResult.put("X_RESULTINFO", "用户上网服务状态不正常，不能办理VOLTE自动开通服务。");
	        	return idResult;
			}
			else*/
			if(IDataUtil.isNotEmpty(idsSvcState22))
			{
				IData idSvcState22 = idsSvcState22.first();
				String strSvc22 = idSvcState22.getString("SERVICE_ID", "");
				String strSC = idSvcState22.getString("STATE_CODE", "");
				if(!"0".equals(strSC) && "22".equals(strSvc22))
				{
					idResult.put("X_RESULTCODE", "2998");
		    		idResult.put("X_RESULTINFO", "用户上网服务状态不正常，不能办理VOLTE自动开通服务");
		        	return idResult;
				}
			}
		}
		
		//IData param = new DataMap();
		String strPsptTypeCode = uca.getCustomer().getPsptTypeCode();
		String strPsptId = uca.getCustomer().getPsptId();
		//param.clear();
        //param.put("PSPT_TYPE_CODE", strPsptTypeCode);
        //param.put("PSPT_ID", strPsptId);
        //如果是黑名单，则限制
        IDataset idsBlackUser = UCustBlackInfoQry.qryBlackCustInfo(strPsptTypeCode, strPsptId);
        if (IDataUtil.isNotEmpty(idsBlackUser))//Dao.qryByRecordCount("TD_O_BLACKUSER", "SEL_BY_PK", param)
        {
        	
        	 if (log.isDebugEnabled())
  	        {
 				 //log.info("("VoLTE业务自动开通 " + "用户是黑名单，不能办理VOLTE自动开通服务！");
  	        }
        	idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是黑名单，不能办理VOLTE自动开通服务！");
        	return idResult;
        }
        
        //如果是测试卡，则限制
        String strUserTypeCode = uca.getUser().getUserTypeCode();
        if("A".equals(strUserTypeCode))
        {
        	
       	 if (log.isDebugEnabled())
	        {
       		   //log.info("("VoLTE业务自动开通 " + "用户是测试卡[NET_TYPE_CODE=A]，不能办理VOLTE自动开通服务！");
	        }
        	idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是测试卡[NET_TYPE_CODE=A]，不能办理VOLTE自动开通服务！");
        	return idResult;
        	//return true;
        }
		
		//携转用户校验
		IDataset idsNpUser = UserNpInfoQry.qryUserNpInfosByUserId(strUserID);
		if(IDataUtil.isNotEmpty(idsNpUser))
		{
			IData idNpUser = idsNpUser.first();
			String strNpTag = idNpUser.getString("NP_TAG", "");
			if("1".equals(strNpTag))
			{		
				//如果是携入用户
				idResult.put("X_RESULTCODE", "2998");
	    		idResult.put("X_RESULTINFO", "用户是携入用户，不能办理VOLTE自动开通服务！");
	        	return idResult;
				//return true;
				
				/*List<SvcTradeData> lsSvcs = uca.getUserSvcs();
				if(CollectionUtils.isNotEmpty(lsSvcs))
				{
					for (int i = 0; i < lsSvcs.size(); i++) 
					{
						SvcTradeData dSvc = lsSvcs.get(i);
						String strModifyTag = dSvc.getModifyTag();
						String strSvcId = dSvc.getElementId();
						if("190".equals(strSvcId) && BofConst.MODIFY_TAG_ADD.equals(strModifyTag))
						{
							//CSAppException.apperr(CrmCommException.CRM_COMM_103,"携入用户不能办理【服务】[190|VOLTE服务]！");
							return true;
						}
					}
				}*/
			}
		}
		
		//如果是红名单，则限制
		/*IData idParamRedUser = new DataMap();
		idParamRedUser.put("SERIAL_NUMBER", uca.getSerialNumber());
		IDataset idsRedUser = CSAppCall.call("QCC_ITF_GetRedUser", idParamRedUser);
    	if(IDataUtil.isNotEmpty(idsRedUser) && idsRedUser.size() > 0)
    	{
    		//如果是携入用户
			idResult.put("X_RESULTCODE", "2998");
    		idResult.put("X_RESULTINFO", "用户是红名单用户，不能办理VOLTE自动开通服务！");
        	return idResult;
    	}*/
		
		idResult.put("X_RESULTCODE", "0");
		idResult.put("X_RESULTINFO", "OK");
		return idResult;
    }
    
	public IDataset querybatIOPOpenList(int rowNum) throws Exception {
		IData data = new DataMap();
        data.put("IN_ROW_NUM", rowNum);
        //data.put("IN_STATUS", null);
        
        return Dao.qryByCode("TI_B_IOP_91800", "SEL_BY_STATUSROWNUM", data, Route.CONN_CRM_CEN);
	}
	
	public IDataset querybatSyncUserCreditList(int rowNum) throws Exception {
		IData data = new DataMap();
        data.put("IN_ROW_NUM", rowNum);
        //data.put("IN_STATUS", null);
        
        return Dao.qryByCode("TI_B_SYNC_USER_CREDIT", "SEL_BY_STATUSROWNUM", data, Route.CONN_CRM_CEN);
	}
	
	public void moveBatIOPList(IData param) throws Exception {
        //搬迁TI_B_IOP_91800表
        Dao.executeUpdateByCodeCode("TI_B_IOP_91800", "MOVE_UPDATE_BH_IOP_91800", param, Route.CONN_CRM_CEN);
        //删除TI_B_IOP_91800表
        Dao.executeUpdateByCodeCode("TI_B_IOP_91800", "MOVE_DEL_B_IOP_91800", param, Route.CONN_CRM_CEN);
	}
	
	public void moveBatSyncUserCreditList(IData param) throws Exception {
        //搬迁TI_B_IOP_91800表
        Dao.executeUpdateByCodeCode("TI_B_SYNC_USER_CREDIT", "MOVE_UPDATE_BH_SYNC_USER_CREDIT", param, Route.CONN_CRM_CEN);
        //删除TI_B_IOP_91800表
        Dao.executeUpdateByCodeCode("TI_B_SYNC_USER_CREDIT", "MOVE_DEL_B_SYNC_USER_CREDIT", param, Route.CONN_CRM_CEN);
	}

	/**
	 * 查询用户国漫开通和信用停开机
	 *
	 * @param iData
	 * @return
	 * @throws Exception
	 */
	public IDataset getUserSvc(IData iData) throws Exception {
		String sn = iData.getString("SERIAL_NUMBER");
		IDataset result = new DatasetList();
		//查询用户是否已经开通了国际漫游
		UcaData uca = UcaDataFactory.getNormalUca(sn);
		String intRoamSvc = "19";
		List<SvcTradeData> svcList_IntRoam = uca.getUserSvcBySvcId(intRoamSvc);
		if (!(svcList_IntRoam.isEmpty())) {
			SvcTradeData std_IntRoam = svcList_IntRoam.get(0);
			String serviceId_IntRoam = std_IntRoam.getElementId();
			String startData_IntRoam = std_IntRoam.getStartDate();
			IData iData_IntRoam = new DataMap();
			iData_IntRoam.put("BIZ_TYPE", "1002");
			iData_IntRoam.put("SERVICE_ID", serviceId_IntRoam);
			iData_IntRoam.put("START_DATE", startData_IntRoam);
			iData_IntRoam.put("SERVICE_NAME", "信用免预存开国漫");
			result.add(iData_IntRoam);
		}

		//查询用户信用停开机保障服务
		IDataset commparaList = CommparaInfoQry.getCommparaAllCol("CSM", "2001", "XINYF_SVC", CSBizBean.getUserEparchyCode());
		String creditMobileStopSvc = commparaList.getData(0).getString("PARA_CODE2");//信用停开机 保障信用服务
		List<SvcTradeData> svcList_Stop = uca.getUserSvcBySvcId(creditMobileStopSvc);
		if (!(svcList_Stop.isEmpty())) {
			SvcTradeData std_Stop = svcList_Stop.get(0);
			String serviceId_Stop = std_Stop.getElementId();
			String startData_Stop = std_Stop.getStartDate();
			IData iData_Stop = new DataMap();
			iData_Stop.put("BIZ_TYPE", "1001");
			iData_Stop.put("SERVICE_ID", serviceId_Stop);
			iData_Stop.put("START_DATE", startData_Stop);
			iData_Stop.put("SERVICE_NAME", "信用停机保障");
			result.add(iData_Stop);
		}

		return result;
	}

	/**
	 * @description 根据服务或平台服务截至相应的营销活动 配置为 param_attr=6868
	 * @param @param changeSvcs 服务id或平台服务id列表
	 * @param @param string 类型
	 * @return void
	 * @author tanzheng
	 * @date 2019年5月10日
	 * @param changeSvcs
	 * @param string
	 * @throws Exception 
	 */
	public void dealActiveBySvcEnd(String userId,List<String> changeSvcs, String offerType,String checkMode) throws Exception {
		IData param = new DataMap();
		param.put("SUBSYS_CODE", "CSM");
		param.put("PARAM_ATTR", "6868");
		param.put("PARA_CODE3", offerType);
		for(String elementId:changeSvcs){
			param.put("PARAM_CODE", elementId);
			IDataset dataset = CommparaInfoQry.getCommparaInfoByPara(param);
			//如果有配置相应的营销活动
			if(IDataUtil.isNotEmpty(dataset)){
				for(Object data : dataset){
					//活动id
					String productId = ((IData)data).getString("PARA_CODE2");
					//包id
					String packageId = ((IData)data).getString("PARA_CODE1");
					IDataset ids = UserSaleActiveInfoQry.queryValidSaleActiveByUserIdAndProductId(userId, productId,packageId);
					if(IDataUtil.isNotEmpty(ids)){
						
						IData endActiveParam = new DataMap();
				        endActiveParam.put("SERIAL_NUMBER", ids.getData(0).getString("SERIAL_NUMBER",""));
				        endActiveParam.put("PRODUCT_ID", ids.getData(0).getString("PRODUCT_ID",""));
				        endActiveParam.put("PACKAGE_ID", ids.getData(0).getString("PACKAGE_ID",""));
				        endActiveParam.put("RELATION_TRADE_ID", ids.getData(0).getString("RELATION_TRADE_ID",""));
				        endActiveParam.put("IS_RETURN", "0");
				        endActiveParam.put("FORCE_END_DATE", SysDateMgr.getLastDateThisMonth());
				        endActiveParam.put("END_DATE_VALUE", "7"); //强制终止
				        endActiveParam.put("EPARCHY_CODE",CSBizBean.getTradeEparchyCode());
				        endActiveParam.put("SKIP_RULE","TRUE");
				        endActiveParam.put("NO_TRADE_LIMIT","TRUE");
				        //认证方式
				        endActiveParam.put("CHECK_MODE", (checkMode != null && !"".equals(checkMode)) ? checkMode : "Z" );

				        CSAppCall.call("SS.SaleActiveEndRegSVC.tradeReg4Intf", endActiveParam);
					}
					
					else{
						log.info(userId+"用户不存在活动"+productId+"，包"+packageId);
					}
				}
			}
		}
		
	}
	

}
