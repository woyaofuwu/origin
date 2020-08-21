package com.asiainfo.veris.crm.iorder.soa.family.viewquery;

import com.ailk.bizcommon.util.IDataUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.iorder.pub.family.consts.FamilyConstants;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyCustInfoQry;
import com.asiainfo.veris.crm.iorder.soa.family.common.query.FamilyUserMemberQuery;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.PayRelaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;

/**
 *
 * @author zhangxi
 *
 */
public class GetFamily360ViewSVC extends CSBizService {

	private static final long serialVersionUID = 1L;

	public IData qryFamilyInfoBySerialNumber(IData input) throws Exception{

		IData familyInfo = new DataMap();
		String serialNumber = input.getString("SERIAL_NUMBER");
		String removeTag = input.getString("REMOVE_TAG");

		IData mainUserInfo = new DataMap();

		if (StringUtils.isBlank(serialNumber)){

			return new DataMap();

		}else{

			IDataset  familyCustInfoSet = new DatasetList();

			String memberUserId = "";
			String headCustId = "";

			if("0".equals(removeTag)){
				mainUserInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
			}else{
				mainUserInfo =  UcaInfoQry.qryAllUserInfoBySn(serialNumber).first();
			}

			if(DataUtils.isNotEmpty(mainUserInfo)){
				memberUserId = mainUserInfo.getString("USER_ID");
				headCustId = mainUserInfo.getString("CUST_ID");
			}

			familyCustInfoSet = FamilyCustInfoQry.qryFamilyCustInfoBySnHeadCustIdRemoveTag(serialNumber, headCustId, removeTag);

			if(DataUtils.isEmpty(familyCustInfoSet)){
				return new DataMap() ;
			}

			IDataset familyMemberSet = FamilyUserMemberQuery.queryMembersByMemberUserId(memberUserId);

			if(DataUtils.isNotEmpty(familyMemberSet)){
				familyInfo.put("FAMILY_USER_ID", familyMemberSet.first().get("FAMILY_USER_ID"));
			}

			familyInfo.put("FAMILY_CUST_INFO_SET",familyCustInfoSet);

			return familyInfo;

		}
	}

	/**
	 *
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public IDataset queryFamilyBaseInfo(IData param) throws Exception {
        IDataset familyBaseInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("FAMILY_USER_ID", ""))) {
        	QryFamily360InfoBean bean = BeanManager.createBean(QryFamily360InfoBean.class);
        	familyBaseInfo = bean.queryFamilyBaseInfo(param);
        }
        return familyBaseInfo;
    }

    /**
     * 家庭成员信息查询
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryMemberInfo (IData param) throws Exception {

    	IDataset memberInfos = new DatasetList();

    	String familyUserId = param.getString("FAMILY_USER_ID");
    	String queryFamilyAll = param.getString("QUERY_FAMILY_ALL");
    	String familyAcctId = "";

    	IData familyUserInfo = UcaInfoQry.qryAcctInfoByUserId(familyUserId);

    	if(IDataUtil.isNotEmpty(familyUserInfo)){

    		familyAcctId = familyUserInfo.getString("ACCT_ID");

    	}

    	if("true".equals(queryFamilyAll)){
    		memberInfos = FamilyUserMemberQuery.queryAllMembersByFamilyUserId(familyUserId);
    	}else{
    		memberInfos = FamilyUserMemberQuery.queryMembersByUserFamilyUserId(familyUserId);
    	}

    	if(IDataUtil.isEmpty(memberInfos)){

    		return new DatasetList();

    	}

    	//代付关系集合
		IDataset familyPayRelaInfos = PayRelaInfoQry.getPayRelaBySelbyAcctDa1(familyAcctId, FamilyConstants.FAMILY_PAY_DEFAULT_TAG,"1",null);
		//共享关系集合
		IDataset familyShareRelaInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(familyUserId,FamilyConstants.FAMILY_SHARE_RELATION_TYPE_CODE,null);

		for(Object objMem : memberInfos ){

    		IData memberInfo = (DataMap) objMem;
    		String memberUserId = memberInfo.getString("MEMBER_USER_ID");
    		String memberRoleCode = memberInfo.getString("MEMBER_ROLE_CODE");

    		if("4".equals(memberRoleCode)){
    			continue;
    		}

    		if(IDataUtil.isNotEmpty(familyPayRelaInfos)){
        		for(Object objPay : familyPayRelaInfos){
        			IData familyPayRelaInfo = (DataMap)objPay;
        			if(memberUserId.equals(familyPayRelaInfo.get("USER_ID"))){
        				if("1".equals(familyPayRelaInfo.get("ACT_TAG"))){
        					memberInfo.put("PAY_RELATION", "家庭代付");
        				}
        				memberInfo.put("ACT_TAG", familyPayRelaInfo.get("ACT_TAG"));
        			}
        		}
    		}

    		if(IDataUtil.isNotEmpty(familyShareRelaInfos)){
    			for(Object objShare : familyShareRelaInfos){
    				IData familyShareRelaInfo = (DataMap) objShare;
    				if(memberUserId.equals(familyShareRelaInfo.get("USER_ID_B"))){
    					memberInfo.put("SHARE_RELATION", "家庭共享");
    				}
    			}
    		}

    	}

    	return memberInfos;
    }

    /**
     * "家庭资料综合查询"家庭业务历"最近"标签初始化
     * @param inParam
     * @return
     * @throws Exception
     */
    public IDataset initTradeHistoryTab(IData inParam) throws Exception {
        IData outParam = new DataMap();
        outParam.putAll(inParam);

        String startDate = SysDateMgr.firstDayOfDate(SysDateMgr.getSysTime(), -2);
        outParam.put("START_DATE", startDate);
        outParam.put("END_DATE", SysDateMgr.getSysDate());

        Qry360InfoDAO dao = new Qry360InfoDAO();
        inParam.put("EPARCHY_CODE", CSBizBean.getUserEparchyCode()); // 设置路由
        IDataset tradeTypeCode = dao.queryTradeTypeCode(inParam);
        outParam.put("TRADE_TYPE_CODE", tradeTypeCode);

        return IDataUtil.idToIds(outParam);
    }

    /**
     * "家庭资料综合查询"界面外框从账管等模块获取家庭数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryFamilyAcctInfo(IData param) throws Exception {
        IDataset acctInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("FAMILY_USER_ID", ""))) {
        	QryFamily360InfoBean bean = BeanManager.createBean(QryFamily360InfoBean.class);
            acctInfo = bean.queryFamilyAcctInfo(param);
        }
        return acctInfo;
    }
}
