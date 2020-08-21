package com.asiainfo.veris.crm.order.soa.person.busi.suppliercharge;




import com.ailk.bizservice.base.CSBizBean;
import com.ailk.bizservice.dao.Dao;
import com.ailk.bizservice.query.sysorg.UAreaInfoQry;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.query.BreQryForCommparaOrTag;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;

public class MobileShopManagerBean extends CSBizBean{
	
	public IDataset querySuppInfo(IData param)throws Exception{
		IDataset termSupp = ResCall.querySupplierTypeRel("MC", "");//手机卖场供应商
		IDataset sellSupp = ResCall.querySupplierTypeRel("M", "");//手机卖场
		IDataset cityList = UAreaInfoQry.qryAreaByAreaLevel("30");
		IDataset feeItemList = BreQryForCommparaOrTag.getCommpara("CSM", 3503, "ZZZZ");
		IData data = new DataMap();
		data.put("termSupp", termSupp);
		data.put("sellSupp", sellSupp);
		data.put("cityList", cityList);
		data.put("feeItemList", feeItemList);
		IDataset retList = new DatasetList();
		retList.add(data);
		return retList;
	}

	public IDataset queryMobileShopInfo(IData param, Pagination pagination) throws Exception{
		//这里对用户地州查询范围进行限制
		IDataset dataset = null ;
		boolean isSuperAreaUser = CSBizBean.getVisit().getCityCode().startsWith("HNSJ")||CSBizBean.getVisit().getCityCode().startsWith("HNHN")||CSBizBean.getVisit().getCityCode().startsWith("HNYD");
		if(isSuperAreaUser||StringUtils.equals(CSBizBean.getVisit().getCityCode(), param.getString("AREA_CODE"))){
			 dataset = Dao.qryByCodeParser("TF_R_FEE_MARKET", "SEL_MOBILE_SHOP", param, pagination);
		}
		if(dataset != null){
			for (Object object : dataset) {
				IData data = (IData)object;
				String corpNo = data.getString("CORP_NO");
				String shopNo = data.getString("SHOP_NO");
				if(StringUtils.isNotBlank(corpNo)){
					data.put("CORP_NAME", getCorpName("MC",corpNo));//手机卖场供应商
				}
				if(StringUtils.isNotBlank(shopNo)){
					data.put("SHOP_NAME", getCorpName("M",shopNo));//手机卖场营业厅
				}				
			}
		}
		return dataset;//ResCropDao.mobileShopCheck(param,pagination);
	}
	public String getCorpName(String resTypeId,String corpNo)throws Exception{
		IDataset dataset = ResCall.querySupplierTypeRel(resTypeId, corpNo);
		if(dataset != null && dataset.size() > 0){
			return dataset.getData(0).getString("CORP_NAME");
		}
		return "";
	}

	public IData deleteMobileInShop(IData param) throws Exception{
		
		    IData retData = new DataMap();
	        String numbers = param.getString("numbers", "");
	        if (StringUtils.isNotBlank(numbers))
	        {
//	            log.debug("-----------numbers-----------" + numbers);
	            IData data = new DataMap();
	            data.put("VALID_TAG", "1");//1无效
	            String[] Numbers = null;
	            if (numbers.contains(","))
	            {
	            	Numbers = numbers.replaceAll("\\s+", "").split(",");
	            }
	            else
	            {
	            	Numbers = numbers.split(",");
	            }
	            int succTag = 0;
	            IData inData = new DataMap();
	            for(int j = 0;j< Numbers.length;j++) {
	            	inData.put("ROWID", Numbers[j]);
//	            	succTag += ResCropDao.updMobileInShop(inData);
	            	Dao.deleteByRowId("TF_R_FEE_MARKET", inData);
	            }

	            if (succTag > 0)
	            {
	                retData.put("SUCC_TAG", "Y");
	            }
	            else
	            {
	                retData.put("SUCC_TAG", "N");
	            }
	        }
	        return retData;
	}

	public IData mobileShopInsert(IData param) throws Exception {
		IData retData = new DataMap();
		String year = param.getString("OPER_DATE").toString().substring(0,4);
		int accept_month = Integer.parseInt(param.getString("OPER_DATE").substring(5));
		String areaCode = CSBizBean.getVisit().getCityCode();
		param.put("SHOP_NAME", param.getString("SHOP_NAME",""));
		param.put("CORP_NAME", param.getString("CORP_NAME"));
		param.put("YEAR", year);
		param.put("ACCEPT_MONTH", accept_month);
		param.put("FEE", param.getString("FEE",""));
		param.put("UPDATE_STAFF", getVisit().getStaffId());
		param.put("AREA_CODE", areaCode);
		param.put("REMARK", param.getString("REMARK",""));
		
		String feeItem = param.getString("FEE_ITEM","");
		if(StringUtils.isNotBlank(feeItem)){
			IDataset feeItemList = BreQryForCommparaOrTag.getCommpara("CSM", 3503, feeItem,"ZZZZ");
			if(IDataUtil.isNotEmpty(feeItemList)){
				feeItem = feeItemList.getData(0).getString("PARAM_NAME");
			}
		}
		param.put("FEE_ITEM", feeItem);
		
		
		StringBuilder submitSql = new StringBuilder();
		submitSql.append("INSERT INTO TF_R_FEE_MARKET");
		submitSql.append(" (CHNL_ID,FACTORY_CODE,BALANCE,YEAR,ACCEPT_MONTH,UPDATE_STAFF,UPDATE_TIME,STATUS,RSRV_STR1,RSRV_STR3,REMARK)");
		submitSql.append("VALUES");
		submitSql.append("  (:SHOP_NAME, :CORP_NAME, TO_NUMBER(:FEE) * 100, :YEAR, TO_NUMBER(:ACCEPT_MONTH), :UPDATE_STAFF,");   
		submitSql.append(" SYSDATE, '0',:AREA_CODE,:FEE_ITEM,:REMARK)");
		
		int succTag = Dao.executeUpdate(submitSql, param);//Dao.insert("TF_R_FEE_MARKET", param);
		 if (succTag > 0)
         {
             retData.put("SUCC_TAG", "Y");
         }
         else
         {
             retData.put("SUCC_TAG", "N");
         }
		return retData;
	}

}
