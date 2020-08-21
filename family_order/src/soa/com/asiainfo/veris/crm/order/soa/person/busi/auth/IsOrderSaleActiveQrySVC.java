package com.asiainfo.veris.crm.order.soa.person.busi.auth;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.component.saleactive.SaleActiveQueryBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;


public class IsOrderSaleActiveQrySVC extends CSBizService{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	static Logger logger = Logger.getLogger(IsOrderSaleActiveQrySVC.class);
	/**
	 * 根据SERIAL_NUMBER查询用户EPARCHY_CODE
	 * 根据EPARCHY_CODE查询用户可定营销活动信息
	 * @param SERIAL_NUMBER
	 * @return data
	 * @throws Exception
	 */
	 public IData queryUserSaleActiveInfoBySerialNumber(IData inparam) throws Exception{
//		 logger.error("=================进入方法==============");
		 String serial_number = inparam.getString("SERIAL_NUMBER");
		 if("".equals(serial_number)){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户电话号码不能为空！");
		 }
		 
		 IsOrderSaleActiveQryBean isOrderSaleActiveQryBean = new IsOrderSaleActiveQryBean();
		 
		 IDataset userinfodata = isOrderSaleActiveQryBean.queryUserInfoBySerialNumber(inparam);
		 if(userinfodata.isEmpty()){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"用户电话号码对应用户信息为空！");
		 }
		 
		 
		 String eparchycode = userinfodata.getData(0).getString("EPARCHY_CODE");
		 String userid = userinfodata.getData(0).getString("USER_ID");
		 String custid = userinfodata.getData(0).getString("CUST_ID");
		 
		 //对IPHONE6活动进行处理
		 String paramStr = "S1000";
		 IData paramData = new DataMap();
		 paramData.put("LABEL_ID", paramStr);
		 IDataset labeliddata = isOrderSaleActiveQryBean.isIphone6Cons(paramData);
		 String paramLabel = "";
		 for (int i = 0; i < labeliddata.size(); i++) {
			if("YX11".equals(labeliddata.getData(i).getString("LABEL_ID"))){
				IData input = new DataMap();
				input.put("EPARCHY_CODE", eparchycode);
				input.put("USER_ID", userid);
		    	IData result=new DataMap();
		    	SaleActiveQueryBean saleActiveQueryBean = BeanManager.createBean(SaleActiveQueryBean.class);
		    	result=saleActiveQueryBean.isIphone6Cons(input);
		    	if(!result.getBoolean("RESULT")){
		    		paramLabel = "YX11";
	            }
			}
		}
		 IData resultuserinfo = new DataMap();
		 resultuserinfo.put("EPARCHY_CODE", eparchycode);
		 resultuserinfo.put("LABEL_ID", paramLabel);
		 resultuserinfo.put("SERIAL_NUMBER", serial_number);
//		 logger.error("=================查询TD_B_LABEL表==============");
	     IDataset productiddata = isOrderSaleActiveQryBean.queryProductIdByLabelid(paramLabel);
	     if(productiddata.isEmpty()){
			 CSAppException.apperr(CrmCommException.CRM_COMM_103,"营销活动产品ID为空！");
		 }
	     List<IData> SAInfoList = new ArrayList<IData>();
	     for (int i = 0; i < productiddata.size(); i++) {
	    	 String productid = productiddata.getData(i).getString("PRODUCT_ID");
	         IData param = new DataMap();
	         param.put("USER_ID", userid);
	         param.put("CUST_ID", custid);
	         param.put("PRODUCT_ID", productid);
//	         String result = this.choiceProductNode(param);
//	         if(!"0".equals(result)){
//	        	 productiddata.remove(i);
//	        	 continue;
//	         }
	         resultuserinfo.put("PRODUCT_ID", productid);
//	         logger.error("=================进入querySaleActives方法查询==============");
	         IDataset saleactives = this.querySaleActives(resultuserinfo);
		     for (int j = saleactives.size() - 1; j >= 0; j--)
		        {
		            IData saleactive = saleactives.getData(j);
		            if ("true".equals(saleactive.getString("OTHER_INFO")))
		            {
		                saleactives.remove(j);
		                continue;
		            }
		            IData resultdata = new DataMap();
			   	    resultdata.put("ActionId", saleactives.getData(j).getString("PACKAGE_ID"));
			   	    resultdata.put("ActionName", saleactives.getData(j).getString("PACKAGE_NAME"));
			   	    resultdata.put("ActionDesc", saleactives.getData(j).getString("PACKAGE_DESC"));
			   	     
			   	    SAInfoList.add(resultdata);
		        }
		}
	     IData result = new DataMap();
	     result.put("ActionList", SAInfoList);
	     result.put("BIZ_CODE", "0000");
	     result.put("BIZ_DESC", "查询可订购营销活动成功！");
	     
	     return result;
	    }
	 
	 /**
		 * 选中产品节点，校验产品是否可选
		 * 
		 * @param input
		 * @return
		 * @throws Exception
		 */
		public String choiceProductNode(IData input) throws Exception {		
			if (!input.containsKey("USER_ID"))
				CSAppException.appError("123", "点选产品校验：用户标识[USER_ID]是必须的！");
			if (!input.containsKey("CUST_ID"))
				CSAppException.appError("123", "点选产品校验：客户标识[CUST_ID]是必须的！");
			if (!input.containsKey("PRODUCT_ID"))
				CSAppException.appError("123", "点选产品校验：营销产品标识[PRODUCT_ID]是必须的！");
			
			IData paramValue = new DataMap();
			
			paramValue.put("V_EVENT_TYPE", "MODE");
			paramValue.put("V_EPARCHY_CODE", this.getVisit().getStaffEparchyCode());
			paramValue.put("V_CITY_CODE", this.getVisit().getCityCode());
			paramValue.put("V_DEPART_ID", this.getVisit().getDepartId());
			paramValue.put("V_STAFF_ID", this.getVisit().getStaffId());
			
			paramValue.put("V_USER_ID", input.getString("USER_ID"));
			paramValue.put("V_DEPOSIT_GIFT_ID", input.getString("CUST_ID"));
			paramValue.put("V_PURCHASE_MODE",input.getString("PRODUCT_ID"));
			paramValue.put("V_PURCHASE_ATTR", "-1");
			paramValue.put("V_TRADE_ID", "-1");
			
			paramValue.put("V_CHECKINFO", input.getString("CHECKINFO", ""));
			paramValue.put("V_RESULTCODE", input.getString("RESULTCODE", ""));
			paramValue.put("V_RESULTINFO", input.getString("RESULTINFO", ""));
			paramValue.put("V_SALE_TYPE", input.getString("CAMPN_TYPE"));
			paramValue.put("V_VIP_TYPE_ID", input.getString("VIP_TYPE_ID", "-1"));
			
			paramValue.put("V_VIP_CLASS_ID", input.getString("VIP_CLASS_ID", "-1"));
			
			ProductInfoQry.checkSaleActiveProdByProced(paramValue);
		    String resultdata = paramValue.getString("V_RESULTCODE");
			return resultdata;
		}
		
		/**
		 * 根据product_id查询package信息
		 * @param input
		 * @return
		 * @throws Exception
		 */
		
		public IDataset querySaleActives(IData input) throws Exception
	    {
	        IData otherInfo = new DataMap();
	        otherInfo.put("OTHER_INFO", "true");
	        IsOrderSaleActiveQryBean isOrderSaleActiveQryBean = new IsOrderSaleActiveQryBean();
	        IDataset saleActives = new DatasetList();
//	        logger.error("=================查询TD_B_PACKAGE表==============");
	        saleActives = isOrderSaleActiveQryBean.queryUserSaleActivePackageInfos(input);

	        if (IDataUtil.isEmpty(saleActives))
	            return saleActives;

	        // 循环调用规则
	        //SaleActiveCheckBean.checkPackages(saleActives, input.getString("SERIAL_NUMBER"));
	        String allFail = "true";
	        for (int i = 0, size = saleActives.size(); i < size; i++)
	        {
	            IData saleActive = saleActives.getData(i);
	            if (!"1".equals(saleActive.getString("ERROR_FLAG")))
	            {
	                allFail = "false";
	                break;
	            }
	        }
	        otherInfo.put("ALL_FAIL", allFail);

//	        PackagePrivUtil.filterPackageListByPriv(getVisit().getStaffId(), saleActives);

	        DataHelper.sort(saleActives, "ERROR_FLAG", IDataset.TYPE_INTEGER);

	        saleActives.add(otherInfo);

	        return saleActives;
	    }
}
