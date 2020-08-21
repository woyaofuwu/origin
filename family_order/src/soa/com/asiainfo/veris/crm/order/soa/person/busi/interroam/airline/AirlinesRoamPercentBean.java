
package com.asiainfo.veris.crm.order.soa.person.busi.interroam.airline;

import com.ailk.bizservice.dao.Dao;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.AirlinesInterRoamUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class AirlinesRoamPercentBean extends CSBizBean {

    /**
     * 根据输入条件模糊查询表
     */
    public IDataset loadDiscntInfo(IData userInfo) throws Exception {

        IDataset useDiscntList = new DatasetList();
        String user_id = userInfo.getString("USER_ID");
        // 查询用户是否存在6个月最低消费的优惠
        IDataset userVaildDiscnt = UserDiscntInfoQry.getNowVaildByUserIdDiscnt(user_id, AirlinesInterRoamUtil.getGuaranteeMoney6MonthDiscnt());
        if (DataUtils.isNotEmpty(userVaildDiscnt)) {
            IData useDiscnt = new DataMap();
            useDiscnt.put("DISCNT_CODE", AirlinesInterRoamUtil.getGuaranteeMoney6MonthDiscnt());
            useDiscnt.put("DISCNT_NAME", "航空公司128元专属叠加月包的6个月的最低消费");
            useDiscntList.add(useDiscnt);
        }

        if (DataUtils.isEmpty(useDiscntList)) {//不存在折扣资费信息
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "用户不存在专属折扣信息");
        }
        return useDiscntList;
    }

    public IDataset submit(IData userInfo) throws Exception {
        IDataset result = new DatasetList();
        String serial_number = userInfo.getString("SERIAL_NUMBER");
        // 根据手机号获取USER_ID
        IDataset userInfoSet = UserInfoQry.getEffUserInfoBySn(serial_number, "0", CSBizBean.getVisit().getStaffEparchyCode());
        // 假如没有，说明用户已经销户了，直接跳过
        if (IDataUtil.isEmpty(userInfoSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "根据手机号未找到有效的用户信息");
        }

        // 根据USER_ID查找用户的优惠
        String user_id = userInfoSet.getData(0).getString("USER_ID");

        // 先判断用户是否已经连续订购了6个月的68元最低消费优惠
        IData input = new DataMap();
        input.put("USER_ID", user_id);
        input.put("DISCNT_CODE", AirlinesInterRoamUtil.getGuaranteeMoney6MonthDiscnt());
        IDataset full6DiscntSet = queryFull6Discnts(input);
        // 假如没有则直接提示必须连续订购6个月才能取消
        if (IDataUtil.isEmpty(full6DiscntSet)) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "您的68元保底消费套餐必须满6个月才能取消");
        }
        // 假如已经订购满6个月了则直接调用产品变更接口取消
        IData changeParam = new DataMap();
        changeParam.put("SERIAL_NUMBER", serial_number);
        changeParam.put("ELEMENT_ID", AirlinesInterRoamUtil.getGuaranteeMoney6MonthDiscnt());
        changeParam.put("ELEMENT_TYPE_CODE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
        changeParam.put("MODIFY_TAG", BofConst.MODIFY_TAG_DEL);
        changeParam.put("BOOKING_TAG", "0");
        changeParam.put("END_DATE", SysDateMgr.getLastDateThisMonth());
        result = CSAppCall.call("SS.ChangeProductRegSVC.ChangeProduct", changeParam);
        return result;
    }

    // 查询用户是否存在满6个月的优惠
    private static IDataset queryFull6Discnts(IData input) throws Exception {
        // 假如用户的优惠已经过期，则判断(END_DATE - START_DATE) 是否大于180
        // 假如用户的优惠还未过期，则判断(SYSDATE - START_DATE) 是否大于180
        SQLParser parser = new SQLParser(input);
        parser.addSQL(" SELECT USER_ID, DISCNT_CODE, START_DATE, END_DATE ");
        parser.addSQL(" FROM TF_F_USER_DISCNT A ");
        parser.addSQL(" WHERE 1=1 ");
        parser.addSQL(" AND A.USER_ID = :USER_ID ");
        parser.addSQL(" AND A.DISCNT_CODE = :DISCNT_CODE ");
        parser.addSQL(" AND ( ");
        parser.addSQL(" ( (SYSDATE - START_DATE) >= 180 AND SYSDATE < END_DATE ) ");
        parser.addSQL(" OR  ");
        parser.addSQL(" ( (END_DATE - START_DATE) >= 180 AND (SYSDATE > END_DATE) ) ");
        parser.addSQL(" ) ");
        return Dao.qryByParse(parser);
    }
}
