package com.asiainfo.veris.crm.iorder.soa.family.viewquery;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.CreditCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.mvelmisc.MvelMiscQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.QryUserInfoDao;

public class QryFamily360InfoBean extends CSBizBean {

	//查询家庭基础信息
	public IDataset queryFamilyBaseInfo(IData param) throws Exception {

		String familyUserId = param.getString("FAMILY_USER_ID");
		IData familyInfo;

		// 家庭用户信息
		familyInfo = UcaInfoQry.qryUserInfoByUserId(familyUserId);
        if (IDataUtil.isEmpty(familyInfo)) {
        	// 如果查询TF_F_USER表返回为空，则查询TF_FH_USER表
            QryUserInfoDao userHisDao = new QryUserInfoDao();
            familyInfo = userHisDao.qryUserInfoFromHis(familyUserId).first();
        }

        // 查询家庭主产品信息
		IData userMainProdInfo = UcaInfoQry.qryUserMainProdInfoByUserId(familyUserId);
        if (IDataUtil.isNotEmpty(userMainProdInfo)) {
        	familyInfo.putAll(userMainProdInfo);
        }

        return IDataUtil.idToIds(familyInfo);
	}

	 /**
     * "家庭资料综合查询"界面外框从账管等模块获取用户数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryFamilyAcctInfo(IData param) throws Exception {
        IData acctInfo = new DataMap();
        String userId = param.getString("FAMILY_USER_ID");
        String custId = param.getString("HEAD_CUST_ID");
        String normalUserCheck = param.getString("NORMAL_USER_CHECK");

        IDataset creditInfo;
        if ("on".equals(normalUserCheck)) {
            creditInfo = CreditCall.getCreditInfo(userId, "0"); // 星级和信用度
        } else {
            creditInfo = CreditCall.getCreditInfo(custId, "1");
        }

        if (IDataUtil.isNotEmpty(creditInfo)) {
            IData creditMap = creditInfo.first();
            String creditClass = creditMap.getString("CREDIT_CLASS", "");
            if (!"-1".equals(creditClass) && !"".equals(creditClass) && creditClass != null) {
                acctInfo.put("CREDIT_CLASS", creditMap.getString("CREDIT_CLASS_NAME", ""));
            } else {
                acctInfo.put("CREDIT_CLASS", "0");
            }
        } else {
            acctInfo.put("CREDIT_CLASS", "0");
        }

        IDataset scoreInfo = AcctCall.queryUserScore(userId); // 积分
        if (IDataUtil.isNotEmpty(scoreInfo)) {
            acctInfo.putAll(scoreInfo.first());
        } else {
            acctInfo.put("SUM_SCORE", "0");
        }

        IDataset avgFee = MvelMiscQry.getUserAvgPayFee(userId, "3"); // 近三月月均
        if (IDataUtil.isNotEmpty(avgFee)) {
            acctInfo.put("AVERAGE_FEE", avgFee.first().getString("PARAM_CODE"));
        } else {
            acctInfo.put("AVERAGE_FEE", "0");
        }

        IData oweFeeInfo = AcctCall.getOweFeeByUserId(userId);
        acctInfo.putAll(oweFeeInfo);

        return IDataUtil.idToIds(acctInfo);
    }

}
