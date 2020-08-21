package com.asiainfo.veris.crm.order.soa.group.querygroupinfo;

import java.util.Iterator;

import com.ailk.bizcommon.util.SysDateMgr;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.consts.EcConstants;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.group.common.query.DatalineOrderDAO;

public class GrpLineInfoQryBean {

    public static IDataset qryGroupLineInfos(IData param) throws Exception {
        String groupId = param.getString("GROUP_ID");
        IDataset userInfoList = UserInfoQry.getGrpUserInfoByGrpId(groupId, "0");
        Iterator<Object> it = userInfoList.iterator();
        while (it.hasNext()) {
            IData userInfo = (IData) it.next();
            String productId = userInfo.getString("PRODUCT_ID");
            if(!"97011".equals(productId) && !"97012".equals(productId) && !"7010".equals(productId)) {
                it.remove();
                continue;
            }
            String productName = UProductInfoQry.getProductNameByProductId(productId);
            userInfo.put("PRODUCT_NAME", productName);
        }
        return userInfoList;
    }

    public static IDataset qryLineInfoByUserId(IData param/*, Pagination pagination*/) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT D.*, P.PRODUCT_ID, U.SERIAL_NUMBER ");
        sql.addSQL(" FROM TF_F_USER_DATALINE D, ");
        sql.addSQL(" TF_F_CUST_GROUP    G, ");
        sql.addSQL(" TF_F_USER          U, ");
        sql.addSQL(" TF_F_USER_PRODUCT  P  ");
        sql.addSQL(" WHERE G.CUST_ID = U.CUST_ID ");
        sql.addSQL(" AND U.USER_ID = P.USER_ID ");
        sql.addSQL(" AND D.USER_ID = U.USER_ID ");
        sql.addSQL(" AND U.REMOVE_TAG = '0' ");
        sql.addSQL(" AND G.GROUP_ID = :GROUP_ID ");
        sql.addSQL(" AND D.SHEET_TYPE = :SHEET_TYPE ");
        sql.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL(" AND D.PRODUCT_NO = :PRODUCT_NO ");
        IDataset result = Dao.qryByParse(sql, /* pagination, */Route.CONN_CRM_CG);
        if(DataUtils.isNotEmpty(result)) {
            for (int i = 0; i < result.size(); i++) {
                IData data = result.getData(i);
                String productId = data.getString("PRODUCT_ID");
                String productName = UProductInfoQry.getProductNameByProductId(productId);
                data.put("PRODUCT_NAME", productName);
            }

        }
        return result;
    }

    public static IDataset qryLineInfoAndAcctInfo(IData param) throws Exception {
        SQLParser sql1 = new SQLParser(param);
        sql1.addSQL(" SELECT D.PRODUCT_NO, D.BIZ_SECURITY_LV, D.RSRV_STR5,D.PORT_CONTACT_A, ");
        sql1.addSQL(" D.PORT_CONTACT_PHONE_A,P.PRODUCT_ID,U.SERIAL_NUMBER,U.USER_ID ");
        sql1.addSQL(" FROM TF_F_USER_DATALINE D, ");
        sql1.addSQL("      TF_F_CUST_GROUP    G, ");
        sql1.addSQL("      TF_F_USER          U, ");
        sql1.addSQL("      TF_F_USER_PRODUCT  P  ");
        sql1.addSQL(" WHERE D.USER_ID = U.USER_ID ");
        sql1.addSQL(" AND G.CUST_ID = U.CUST_ID ");
        sql1.addSQL(" AND U.USER_ID = P.USER_ID ");
        sql1.addSQL(" AND U.REMOVE_TAG = '0' ");
        sql1.addSQL(" AND P.PRODUCT_ID IN ('97011', '97012' ,'97016','970111', '970112','970121', '970122') ");
        sql1.addSQL(" AND G.GROUP_ID=:GROUP_ID ");
        IDataset lineInfos = Dao.qryByParse(sql1, Route.CONN_CRM_CG);
        if(DataUtils.isEmpty(lineInfos)) {
            return lineInfos;
        }

        String acctOperType = param.getString("ACCT_OPERTYPE");
        if(StringUtils.isBlank(acctOperType)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "未获取到操作类型！");
        }

        Iterator<Object> it = lineInfos.iterator();
        while (it.hasNext()) {
            IData lineInfo = (IData) it.next();
            String userId = lineInfo.getString("USER_ID");
            IData membPayRela = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(userId);

            IDataset ralationInfos = RelaUUInfoQry.getRelaByUserIdBForSqlff(userId, null);

            if(DataUtils.isEmpty(ralationInfos)) {
                it.remove();
                continue;
            }

            IData ralationInfo = ralationInfos.first();
            IData grpPayRela = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(ralationInfo.getString("USER_ID_A"));

            if("1".equals(acctOperType)) {
                if(DataUtils.isNotEmpty(membPayRela) && DataUtils.isNotEmpty(grpPayRela) && StringUtils.equals(membPayRela.getString("ACCT_ID"), grpPayRela.getString("ACCT_ID"))) {
                    lineInfo.put("ACCT_ID", membPayRela.getString("ACCT_ID"));
                    String productId = lineInfo.getString("PRODUCT_ID");
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    lineInfo.put("PRODUCT_NAME", productName);
                } else {
                    it.remove();
                    continue;
                }
            } else if("2".equals(acctOperType)) {
                if(DataUtils.isEmpty(membPayRela) || DataUtils.isEmpty(grpPayRela) || !StringUtils.equals(membPayRela.getString("ACCT_ID"), grpPayRela.getString("ACCT_ID"))) {
                    lineInfo.put("ACCT_ID", membPayRela.getString("ACCT_ID"));
                    String productId = lineInfo.getString("PRODUCT_ID");
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    lineInfo.put("PRODUCT_NAME", productName);
                } else {
                    it.remove();
                    continue;
                }
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未知操作类型！");
            }
        }
        return lineInfos;
    }

    public IDataset checkAcctInfo(IData param) throws Exception {
        String operType = param.getString("OPERTYPE");
        if("1".equals(operType)) {
            String acctId = param.getString("");
        } else if("2".equals(operType)) {

        }
        SQLParser sql = new SQLParser(param);
        return null;
    }

    public IDataset qryLineInfoStopOrBack(IData param, Pagination pagination) throws Exception {
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT G.GROUP_ID, D.PRODUCT_NO, D.RSRV_STR5,P.PRODUCT_ID,U.SERIAL_NUMBER,U.USER_ID ");
        sql.addSQL(" FROM TF_F_USER_DATALINE D, ");
        sql.addSQL("      TF_F_CUST_GROUP    G, ");
        sql.addSQL("      TF_F_USER          U, ");
        sql.addSQL("      TF_F_USER_PRODUCT  P ");
        sql.addSQL(" WHERE D.USER_ID = U.USER_ID ");
        sql.addSQL(" AND G.CUST_ID = U.CUST_ID ");
        sql.addSQL(" AND U.USER_ID = P.USER_ID ");
        sql.addSQL(" AND G.GROUP_ID = :GROUP_ID ");
        sql.addSQL(" AND U.SERIAL_NUMBER = :SERIAL_NUMBER ");
        sql.addSQL(" AND D.PRODUCT_NO = :PRODUCT_NO ");

        IDataset lineInfos = Dao.qryByParse(sql, /*pagination,*/ Route.CONN_CRM_CG);

        IDataset result = new DatasetList();

        if(lineInfos != null) {
            String changeMode = param.getString("CHANGEMODE");
            String qryStr = "";
            if("stop".equals(changeMode)) {
                qryStr = " AND EW.BPM_TEMPLET_ID IN ('MANUALSTOP', 'CREDITDIRECTLINEPARSE') ";
            } else if("back".equals(changeMode)) {
                qryStr = " AND EW.BPM_TEMPLET_ID IN ('MANUALBACK', 'CREDITDIRECTLINECONTINUE') ";
            } else {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "未知查询类型！");
            }
            Iterator<Object> it = lineInfos.iterator();
            while (it.hasNext()) {
                IData lineInfo = (IData) it.next();
                IDataset timeInfos = getFinshTime(qryStr, lineInfo.getString("PRODUCT_NO", ""));
                if(DataUtils.isEmpty(timeInfos)) {
                    continue;
                }
                for (int i = 0; i < timeInfos.size(); i++) {
                    lineInfo.put("FINISH_TIME", timeInfos.getData(i).getString("FINISH_DATE").substring(0, 10));
                    lineInfo.put("SUB_TYPE", timeInfos.getData(i).getString("SUB_TYPE"));
                    lineInfo.put("ACCEPT_STAFF_ID", timeInfos.getData(i).getString("ACCEPT_STAFF_ID"));
                    String productId = lineInfo.getString("PRODUCT_ID");
                    String productName = UProductInfoQry.getProductNameByProductId(productId);
                    lineInfo.put("PRODUCT_NAME", productName);
                    result.add(lineInfo);
                }

            }
        }

        return result;
    }

    private IDataset getFinshTime(String qryStr, String prooductNo) throws Exception {
        IData param = new DataMap();
        param.put("PRODUCT_NO", prooductNo);
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT DISTINCT EW.FINISH_DATE,ED.IBSYSID,DECODE(EW.BPM_TEMPLET_ID,'MANUALSTOP','简单场景变更(停机)','MANUALBACK','简单场景变更(复机)','CREDITDIRECTLINEPARSE','信控停机','CREDITDIRECTLINECONTINUE','信控复机') SUB_TYPE,EW.ACCEPT_STAFF_ID ");
        sql.addSQL(" FROM TF_BH_EOP_EOMS_STATE ED, TF_BH_EWE EW ");
        sql.addSQL(" WHERE ED.IBSYSID = EW.BI_SN ");
        sql.addSQL(" AND ED.PRODUCT_NO = :PRODUCT_NO ");
        sql.addSQL(qryStr);

        return Dao.qryByParse(sql, Route.getJourDb());

    }

    public static IData queryDataLineUserInfo(String userId) throws Exception {
        if(StringUtils.isBlank(userId)) {
            return new DataMap();
        }
        IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
        if(IDataUtil.isEmpty(userInfo)){
            return new DataMap();
        }
        IData userLineInfo = DatalineOrderDAO.queryDataline(userId);
        userInfo.putAll(userLineInfo);
        IData userPayrela = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(userId);
        userInfo.putAll(userPayrela);
        return userInfo;
    }

    public static IData queryLineGrpAcctByMebUserId(String userId) throws Exception {
        IDataset ralationInfos = RelaUUInfoQry.getRelaByUserIdBForSqlff(userId, null);
        if(IDataUtil.isEmpty(ralationInfos)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据USER_ID=" + userId + "未查询到专线集团用户！");
        }
        String grpUserId = ralationInfos.first().getString("USER_ID_A");
        IData userPayrela = UcaInfoQry.qryDefaultPayRelaByUserIdForGrp(grpUserId);
        return userPayrela;
    }

    public static IData queryAcctInfo(String acctId) throws Exception {
        IData param = new DataMap();
        param.put("ACCT_ID", acctId);
        return AcctCall.qryRoweFeeAndAllMoney(param);
    }

    public static IData queryContractInfo(String contractId, String custId) throws Exception {
        IData param = new DataMap();
        param.put("CONTRACT_ID", contractId);
        param.put("CUST_ID", custId);
        SQLParser sql = new SQLParser(param);
        sql.addSQL(" SELECT t.* FROM  TF_F_CUST_CONTRACT T ");
        sql.addSQL(" WHERE T.CUST_ID = :CUST_ID ");
        sql.addSQL(" AND T.CONTRACT_ID = :CONTRACT_ID ");

        IDataset results = Dao.qryByParse(sql, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(results)) {
            return results.first();
        }
        return new DataMap();
    }

	public static IData queryLineByProductNO(String productNO) throws Exception {
		IData param = new DataMap();
        param.put("PRODUCT_NO", productNO);
        
        IDataset results = Dao.qryByCode("TF_F_USER_DATALINE", "SEL_DATALINE_BY_PRODUCTNO", param, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(results)) {
            return results.first();
        }
        return new DataMap();
	}

	/**
	 * 查询专线付费关系
	 * @param param
	 * @param page
	 * @return
	 * @throws Exception
	 */
	 public static IDataset queryLinePayrelation(IData param, Pagination page) throws Exception {
	        SQLParser sql = new SQLParser(param);

	        sql.addSQL(" SELECT  (select r.serial_number from tf_f_user r where r.user_id = s.user_id_a and r.remove_tag = '0') SPSN, ");
			sql.addSQL(" S.ACCT_ID SPACCTID,A.SERIAL_NUMBER,D.PRODUCT_NO,P.PRODUCT_ID,C.ACCT_ID,A.USER_ID,B.CUST_NAME, ");
	        sql.addSQL("   U.SERIAL_NUMBER_A GRP_SERIAL_NUMBER,E.ACCT_ID GRP_ACCT_ID,B.GROUP_ID,DECODE(A.REMOVE_TAG,'0','有效','已注销') REMOVE_TAG ");
	        sql.addSQL("   FROM TF_F_USER        A, ");
	        sql.addSQL("      TF_F_CUST_GROUP    B, ");
	        sql.addSQL("      TF_A_PAYRELATION   C, ");
	        sql.addSQL("      TF_F_USER_DATALINE D, ");
	        sql.addSQL("      TF_F_USER_PRODUCT  P, ");
	        sql.addSQL("      TF_F_RELATION_UU   U, ");
	        sql.addSQL("      TF_A_PAYRELATION   E, ");
			sql.addSQL("      TF_F_USER_SPECIALEPAY S ");
	        sql.addSQL("   WHERE A.CUST_ID = B.CUST_ID ");
	        sql.addSQL("     AND A.USER_ID = C.USER_ID ");
	        sql.addSQL("     AND A.USER_ID = D.USER_ID ");
	        sql.addSQL("     AND A.USER_ID = P.USER_ID ");
	        sql.addSQL("     AND A.USER_ID = U.USER_ID_B ");
	        sql.addSQL("     AND U.USER_ID_A = E.USER_ID ");
	        /*sql.addSQL("     AND A.REMOVE_TAG = '0' ");*/
	        sql.addSQL("     AND C.DEFAULT_TAG = '1' ");
	        sql.addSQL("     AND E.DEFAULT_TAG = '1' ");
	        sql.addSQL("     AND P.PRODUCT_ID IN ('97011', '97012','97016','970111', '970112','970121','970122') ");
	        sql.addSQL("     AND C.ACCT_ID = :ACCT_ID ");
			sql.addSQL("     AND (A.USER_ID = S.USER_ID(+) AND S.END_CYCLE_ID(+) > TO_NUMBER(TO_CHAR(SYSDATE, 'YYYYMMDD'))) ");
	        sql.addSQL("     AND U.SERIAL_NUMBER_A = :SERIAL_NUMBER_A ");
	        sql.addSQL("     AND B.GROUP_ID = :GROUP_ID ");
	        sql.addSQL("     AND A.SERIAL_NUMBER = :SERIAL_NUMBER ");
	        sql.addSQL("     AND D.PRODUCT_NO = :PRODUCT_NO ");
	        sql.addSQL("     AND U.END_DATE>SYSDATE ");
	        sql.addSQL("     AND U.START_DATE<SYSDATE ");
	        sql.addSQL("     AND C.END_CYCLE_ID>=TO_CHAR(SYSDATE,'YYYYMMDD') ");
	        sql.addSQL("     AND C.START_CYCLE_ID<=TO_CHAR(SYSDATE,'YYYYMMDD') ");
	        sql.addSQL("     AND P.END_DATE>SYSDATE ");
	        IDataset results = Dao.qryByParse(sql, page, Route.CONN_CRM_CG);
	        if(IDataUtil.isEmpty(results)) {
	            return results;
	        }
	        for (int i = 0, size = results.size(); i < size; i++) {
	            IData result = results.getData(i);
	            IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(result.getString("PRODUCT_ID"));
	            if(IDataUtil.isNotEmpty(grpProducts)) {
	                String productId = grpProducts.first().getString("PRODUCT_ID");
	                String productName = UProductInfoQry.getProductNameByProductId(productId);
	                result.put("PRODUCT_NAME", productName);
	                String userId = result.getString("USER_ID","");
	                String bilingDate = "";
	                if(StringUtils.isNotEmpty(userId)){
	                    IDataset discntDatas = UserDiscntInfoQry.queryDiscntByUserIdAndDiscntCode(userId, EcConstants.ZERO_DISCNT_CODE);
	                    if(IDataUtil.isNotEmpty(discntDatas)){//存在零元资费
	                        String endDate =  discntDatas.first().getString("END_DATE");
	                        if(StringUtils.isNotEmpty(endDate)){
	                            bilingDate = SysDateMgr.getFirstDayOfNextMonth(endDate);
	                        }
	                    }else{//不存在零元资费
	                        discntDatas = UserDiscntInfoQry.getAllDiscntInfo(userId);
	                        if(IDataUtil.isNotEmpty(discntDatas)){
	                            bilingDate = discntDatas.first().getString("START_DATE");
	                        }
	                    }
	                    if(StringUtils.isNotBlank(bilingDate)) {
	                        bilingDate = SysDateMgr.suffixDate(bilingDate, 0);
	                        bilingDate = bilingDate.substring(0, 19);
	                        //bilingDate = bilingDate.substring(0, 10);
	                    }
	                    result.put("BILING_DATE", bilingDate);
	                }
	            }
	        }
	        return results;
	    }
    
    public static IDataset queryLineByUserId (String userid) throws Exception {
		IData param = new DataMap();
        param.put("USER_ID", userid);
        
        IDataset results = Dao.qryByCode("TF_F_USER_DATALINE", "SEL_DATALINE_BY_USERID", param, Route.CONN_CRM_CG);
        if(IDataUtil.isNotEmpty(results)) {
            return results;
        }
        return new DatasetList();
	}
    
    /**
     * 查询已有专线
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset queryLineByGroupIdSnProductNo(IData param) throws Exception{
        String productNo = param.getString("PRODUCTNO");
        IDataset results = new DatasetList();
        SQLParser sql = new SQLParser(param);
        if(StringUtils.isBlank(productNo)){
            sql.addSQL(" SELECT A.GROUP_ID, A.CUST_NAME, B.SERIAL_NUMBER, B.USER_ID, C.PRODUCT_ID ");
            sql.addSQL("    FROM TF_F_CUST_GROUP A, TF_F_USER B, TF_F_USER_PRODUCT C ");
            sql.addSQL("   WHERE A.CUST_ID = B.CUST_ID ");
            sql.addSQL("     AND B.USER_ID = C.USER_ID ");
            sql.addSQL("     AND B.REMOVE_TAG = '0' ");
            sql.addSQL("     AND C.PRODUCT_ID IN ('7010', '7011', '7012','70111','70112','70121','70122') ");
            sql.addSQL("     AND A.GROUP_ID = :GROUP_ID ");
            sql.addSQL("     AND B.SERIAL_NUMBER = :SERIAL_NUMBER ");
            
            IDataset grpUserInfos = Dao.qryByParse(sql, Route.CONN_CRM_CG);
            
            if(IDataUtil.isEmpty(grpUserInfos)){
                return results;
            }
            
            for(int i =0;i < grpUserInfos.size();i++){
                IData grpUserData = grpUserInfos.getData(i);
                String productId = grpUserData.getString("PRODUCT_ID");
                if("7010".equals(productId)){
                    IDataset lineInfos = queryLineByUserId(grpUserData.getString("USER_ID"));
                    if(IDataUtil.isNotEmpty(lineInfos)){
                        grpUserData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                        for(int j=0;j<lineInfos.size();j++){
                            IData lineInfo = lineInfos.getData(j);
                            IData grpUserDataClone = (IData) Clone.deepClone(grpUserData);
                            grpUserDataClone.put("SERIAL_NUMBER_B", grpUserDataClone.getString("SERIAL_NUMBER"));
                            grpUserDataClone.put("PRODUCT_NO", lineInfo.getString("PRODUCT_NO"));
                            results.add(grpUserDataClone);
                        }
                    }
                }else{
                    String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
                    IDataset lineInfos = RelaUUInfoQry.getAllMebByUSERIDA(grpUserData.getString("USER_ID"), relationTypeCode);
                    if(IDataUtil.isNotEmpty(lineInfos)){
                        grpUserData.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
                        for(int j=0;j<lineInfos.size();j++){
                            IData lineInfo = lineInfos.getData(j);
                            IData grpUserDataClone = (IData) Clone.deepClone(grpUserData);
                            grpUserDataClone.put("SERIAL_NUMBER_B", lineInfo.getString("SERIAL_NUMBER_B"));
                            IDataset datalines = queryLineByUserId(lineInfo.getString("USER_ID_B"));
                            if(IDataUtil.isNotEmpty(datalines)){
                                grpUserDataClone.put("PRODUCT_NO", datalines.first().getString("PRODUCT_NO"));
                                results.add(grpUserDataClone);
                            }
                        }
                    }
                }
            }
        }else{
            
            IData dataline = queryLineByProductNO(productNo);
            if(IDataUtil.isEmpty(dataline)){
                return results;
            }
            
            IData result  = new DataMap();
            result.put("PRODUCT_NO", dataline.getString("PRODUCT_NO"));
            
            String condGroupId = param.getString("GROUP_ID");
            String snA = param.getString("SERIAL_NUMBER");
            
            String userId = dataline.getString("USER_ID");
            IData groupInfo = UcaInfoQry.qryGrpInfoByUserId(userId);
            
            if(StringUtils.isNotBlank(condGroupId)&&!condGroupId.equals(groupInfo.getString("GROUP_ID"))){
                return results;
            }
            
            result.put("GROUP_ID", groupInfo.getString("GROUP_ID"));
            result.put("CUST_NAME", groupInfo.getString("CUST_NAME"));
            
            IData userInfo = UcaInfoQry.qryUserInfoByUserId(userId);
            result.put("SERIAL_NUMBER_B", userInfo.getString("SERIAL_NUMBER"));
            
            IData mainProductInfo = UcaInfoQry.qryMainProdInfoByUserId(userId);
            String productId = mainProductInfo.getString("PRODUCT_ID");
            if("7010".equals(productId)){
                result.put("SERIAL_NUMBER", userInfo.getString("SERIAL_NUMBER"));
                result.put("PRODUCT_NAME", UProductInfoQry.getProductNameByProductId(productId));
            }else{
                IDataset grpProducts = UProductMebInfoQry.queryGrpProductInfosByMebProductId(productId);
                if(IDataUtil.isNotEmpty(grpProducts)) {
                    productId = grpProducts.first().getString("PRODUCT_ID");
                    result.put("PRODUCT_NAME", grpProducts.first().getString("PRODUCT_NAME"));
                }
                String relationTypeCode = UProductCompInfoQry.getRelationTypeCodeByProductId(productId);
                IDataset relationDatas = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCodeForGrp(userId,relationTypeCode,null);
                if(IDataUtil.isNotEmpty(relationDatas)){
                    IData grpUserInfo = UcaInfoQry.qryUserInfoByUserId(relationDatas.first().getString("USER_ID_A"));
                    result.put("SERIAL_NUMBER", grpUserInfo.getString("SERIAL_NUMBER"));
                }
            }
            
            if(StringUtils.isNotBlank(snA)&&!snA.equals(result.getString("SERIAL_NUMBER"))){
                return results;
            }
            
            results.add(result);
        }
        
        return results;
    }
}
