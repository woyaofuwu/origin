package com.asiainfo.veris.crm.order.soa.group.groupintf.intelligence.bean;

import org.apache.commons.collections.CollectionUtils;

import com.ailk.biz.util.StaticUtil;
import com.ailk.bizservice.base.CSBizBean;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class AcceptanceInfoQueryBean extends CSBizBean {
	
	private static final int ORIGIN_NUM = 1;
	public IData acceptanceInfoQuery(IData input) throws Exception{
		int pageingIdxStart =Integer.parseInt( IDataUtil.chkParam(input, "pageingIdxStart") ); //相当于当前页
		int pageingIdxEnd = Integer.parseInt(  IDataUtil.chkParam(input, "pageingIdxEnd") );//相当于当前页数
		Pagination pagination = new Pagination();//需要分页
        pagination.setCurrent(pageingIdxStart );
        pagination.setPageSize( pageingIdxEnd );
        pagination.setOriginPageSize( pageingIdxEnd  );
        pagination.setNeedCount(true);
        
		IData dts = new DataMap();
		try {
			//TODO 参考可以不能直接用
			IDataset dats =  queryAcceptanceInfos( this.transferIn( input ), pagination ) ;
			IData ds = transferOut( dats );
			ds.put("tradePageNum", ( dats.size()/pageingIdxEnd ) + ORIGIN_NUM ); //受理单总页数
			ds.put("tradeIdxStart", pageingIdxStart );//当前受理单列表起始索引
			ds.put("tradeIdxEnd", pageingIdxEnd );//当前受理单列表结束索引
			return ds;
		}catch (Exception e) {
			dts.put("bizCode", ContractRevokeBean.RES_FAILED);
			dts.put("bizDesc", e.getMessage() );
		}
		return dts;
	}
	/**
	 * 接口入参转换成调用内部接口需要的参数
	 * @param input
	 * @return
	 * @throws Exception
	 */
	private IData transferIn(IData input) throws Exception{
		String operId = IDataUtil.chkParam(input, "operId"); //操作员编号 需要通过操作员编号查询出客户经理名称
//		String tradeId = IDataUtil.chkParam(input, "tradeId");
        String staffName = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_M_STAFF", "STAFF_ID", "STAFF_NAME", operId);
        if (StringUtils.isBlank( staffName )) {
            CSAppException.apperr(GrpException.CRM_GRP_713, "根据操作员编号【" + operId + "】未获取到客户经理任何信息，请确认该客户经理是否存在！");
        }
		IData outParams = new DataMap();
		outParams.put("IBSYSID", input.getString("tradeId") );
		outParams.put("EPARCHY_CODE", input.getString("mgmtDistrict"));
		outParams.put("GROUP_ID", input.getString("groupId"));
		outParams.put("CUST_NAME", input.getString("groupName"));
		outParams.put("ACCEPT_STAFF_ID", operId);
		return outParams;
	}
	
	/**
	 * 接口出参转换成提供给外部需要的参数
	 * @param set
	 * @return
	 * @throws Exception
	 */
	private IData transferOut(IDataset set) throws Exception{
		IData retDs  = new DataMap();
		if( CollectionUtils.isNotEmpty( set ) ) {
			for (Object object : set) {
				IData temp = (IData) object;
				temp.put("tradeId", temp.getString("BI_SN"));
				temp.put("mgmtDistrict", temp.getString("EPARCHY_CODE"));
				temp.put("groupId", temp.getString("GROUP_ID"));
				temp.put("groupName", temp.getString("CUST_NAME"));
				String productId = temp.getString("PRODUCT_ID") ; 
				if( !StringUtils.isNumeric( productId ) ) {
					// 产品编码非数据类型 需要特殊处理
					String trueProductId ="";
					if(productId.startsWith("CL")) {
						trueProductId = CommparaInfoQry.getCommNetInfo("CSM","2020",productId ).
				                getData(0).getString("PARA_CODE4"); //云网虚拟产品包 对应的真实主产品
					}else if(productId.startsWith("VP")) {
						
					}
					temp.put("offerId", trueProductId ); 
				}else {
					temp.put("offerId", productId ); 
				}
				temp.put("offerName", temp.getString("PRODUCT_NAME"));
				temp.put("status", temp.getString("DEAL_STATE"));
				temp.put("createTime", temp.getString("CREATE_DATE"));
			}
			retDs.put("orderInfo", set);
			retDs.put("tradeNum", set.size() );
		}
		return retDs;
	}
	/**
	 * 接口出参转换成提供给外部需要的参数
	 * @return
	 * @throws Exception
	 */
	private IDataset queryAcceptanceInfos(IData input, Pagination pagination) throws Exception{
		
//		SQLParser parser = new SQLParser(input);
//        parser.addSQL(" SELECT B.BI_SN,T.EPARCHY_CODE,T.GROUP_ID, " );
//        parser.addSQL(" T.CUST_NAME,C.PRODUCT_NAME,C.PRODUCT_ID, " );
//		parser.addSQL(" T.DEAL_STATE,TO_CHAR(B.CREATE_DATE,'YYYYMMDDHH24MISS') " );
//		parser.addSQL(" FROM TF_B_EOP_SUBSCRIBE T ,TF_B_EOP_PRODUCT C ,TF_B_EWE B WHERE ");
//		parser.addSQL(" 1=1 AND C.RECORD_NUM =0 AND  ");
//		parser.addSQL(" B.BI_SN=T.IBSYSID AND B.BI_SN=C.IBSYSID ") ;
//		parser.addSQL(" AND C.IBSYSID = :IBSYSID ");
//		parser.addSQL(" AND (:EPARCHY_CODE is null or t.EPARCHY_CODE= :EPARCHY_CODE) " );
//		parser.addSQL("AND B.ACCEPT_STAFF_ID = :ACCEPT_STAFF_ID ");
//		parser.addSQL(" AND (:GROUP_ID is null or C.GROUP_ID=:GROUP_ID) " );
//        parser.addSQL("  AND ( :CUST_NAME is null or T.CUST_NAME like '%'|| :CUST_NAME ||'%') ");

        return Dao.qryByCode("TF_B_EWE_NODE", "SEL_BY_ACCEPT_STAFF", input ,pagination, Route.getJourDb(Route.CONN_CRM_CG));
	}
	
	
	
}
