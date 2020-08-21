package com.asiainfo.veris.crm.order.soa.person.busi.numbercard;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.realnamemgr.NationalOpenLimitBean;

public class NumberCardIntfBean extends CSBizBean
{
    /**
     * 作用：号码准入资格校验
     * 
     * @param pd
     * @param data
     * @return
     * @throws Exception
     */
    public IData chkBuyCond(IData data) throws Exception
    {
//        System.out.println("NumberCardIntfBean.javaxxxxxxxxxxxxxxxxx30 chkBuyCond " + data);
        
        IData outparams = new DataMap();
        outparams.put("status","0");
        outparams.put("message","请求成功");
        IData checkResult = new DataMap();
        //元素校验
        chkParams(data);
        /**
         * 一证五号校验接口_入参不对问题
         * <br/>
         * 由于移动商城传的证件类型：
         *  01:省份证
			02:VIP卡号
			03:护照
			04:军官证
			05:武装警察身份证
			99:其他
		   	需要转换为本地的证件类型
         * @author zhuoyingzhi
         * @date 20180418
         */
        String  psptTypeCode=data.getString("id_type","");
        IDataset psptTypeCodeTrans=CommparaInfoQry.getCommparaAllCol("CSM", "2555", psptTypeCode, "ZZZZ");
        if(IDataUtil.isNotEmpty(psptTypeCodeTrans)){
        	String idCardType = psptTypeCodeTrans.getData(0).getString("PARA_CODE1", "");
        	data.put("id_type", idCardType);
        }
        /******************一证五号校验接口_入参不对问题_end***********************/
        //System.out.println("---NumberCardIntfBean----data:"+data);
        
        //是否黑名单用户校验
        IDataset blackUserInfo = isBlackUser(data.getString("id_type"), data.getString("id_num"));
        checkResult.put("isblacklist", IDataUtil.isNotEmpty(blackUserInfo) ? "1" : "0");
        checkResult.put("opr_time", SysDateMgr.getSysDateYYYYMMDDHHMMSS());

        if (IDataUtil.isEmpty(blackUserInfo)) {//不是黑名单用户
            checkResult.put("limit_num", 0);//本接口不涉及合约机，默认合约机的允许开户数量为 0 
            checkResult.put("allow_num", 1);

            String psptType = data.getString("id_type", "").trim();
            String psptName = data.getString("id_name", "").trim();
            String psptId = data.getString("id_num", "").trim();

            //1.先判断是否超过本省限制的数量，2.在判断是否超过全国1证5号的数据
            if (psptType.equals("0") || psptType.equals("1") || psptType.equals("3") || psptType.equals("A") || psptType.equals("O") || psptType.equals("W")) {//本地外地户口护照军人/港澳/华侨
                IDataset localLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "7637", "0", "0898");// 本地实名制开户数目默认阙值
                if (IDataUtil.isNotEmpty(localLimitResult)) {
                    int local_limit_number = localLimitResult.getData(0).getInt("PARA_CODE1", 0);
                    if (local_limit_number<= 0) {
                        checkResult.put("allow_num", 0);
                        outparams.put("data", checkResult);
                        return outparams;
                    }
                    
                    IDataset ds = CustomerInfoQry.getCustInfoByPsptCustType2(psptType, psptId, psptName);
                    if (!ds.isEmpty()) {
                        if (local_limit_number - ds.size() <= 0) {
                            checkResult.put("allow_num", 0);
                            outparams.put("data", checkResult);
                            return outparams;
                        }
                    }                    
                }

                IData inparam = new DataMap();
                inparam.put("CUSTOMER_NAME", psptName);
                inparam.put("IDCARD_TYPE", psptType);
                inparam.put("IDCARD_NUM", psptId);
                IDataset globalLimitResult = CommparaInfoQry.getCommparaAllCol("CSM", "2552", psptType, "ZZZZ");//全国1证5号不同证件类型的限制数量

                if (IDataUtil.isNotEmpty(globalLimitResult)) {
                    int global_limit_number = globalLimitResult.getData(0).getInt("PARA_CODE1", 0);
                    if (global_limit_number<= 0) {
                        checkResult.put("allow_num", 0);
                        outparams.put("data", checkResult);
                        return outparams;
                    }
                    
                    NationalOpenLimitBean bean = BeanManager.createBean(NationalOpenLimitBean.class);
                    IDataset nationalOpenLimit = bean.idCheck(inparam);

                    if (IDataUtil.isNotEmpty(nationalOpenLimit)) {
                        IData responseInfo = nationalOpenLimit.getData(0);
                        int untrustresult = responseInfo.getInt("UN_TRUST_RESULT", 0);
                        int globaltotal = responseInfo.getInt("TOTAL", 0);
                        if (untrustresult > 0)
                        {
                            checkResult.put("allow_num", 0);
                            outparams.put("data", checkResult);
                            return outparams;
                        }

                        if (global_limit_number - globaltotal <= 0) {
                            checkResult.put("allow_num", 0);
                            outparams.put("data", checkResult);
                            return outparams;
                        }
                    }
                }
            }
        }
        outparams.put("data", checkResult);
//        System.out.println("NumberCardIntfBean.javaxxxxxxxxxxxxxxxxx88 chkBuyCond " + outparams);
        return outparams;
    }

    private void chkParams(IData data) throws Exception
    {
        //校验类型：
        //1：号码购买校验
        //2：新用户合约机购买校验
        //3：老用户合约机购买校验
        if (StringUtils.isBlank(data.getString("check_type"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "校验类型不能为空！");
        }
        if (!("1".equals(data.getString("check_type")) || "2".equals(data.getString("check_type")) || "3".equals(data.getString("check_type")))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_908, data.getString("check_type"));
        }
        // 证件类型
        if (StringUtils.isBlank(data.getString("id_type"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件类型不能为空！");
        }// 证件号码
        if (StringUtils.isBlank(data.getString("id_num"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件号码不能为空！");
        }// 证件姓名（新增字段）
        if (StringUtils.isBlank(data.getString("id_name"))) {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "证件姓名不能为空！");
        }
    }

    /*
        *//**
          * @Function: qryNumbers
          * @Description: 查询用户可购买的号码个数
          * @param: @param data
          * @author: zhangbo18
          */
    /*
    public IDataset qryNumbers() throws Exception
    {
     IData param = new DataMap();
     param.put("SUBSYS_CODE", "CSM");
     param.put("PARAM_ATTR", "2553");
     param.put("PARAM_CODE", "TOTALNUMBERS");
     param.put("EPARCHY_CODE", getVisit().getStaffEparchyCode());

     return Dao.qryByCodeParser("TD_S_COMMPARA", "SEL1_PK_TD_S_COMMPARA", param, Route.CONN_CRM_CEN);
    }*/

    /**
     * 根据证件类型、证件号码查询用户是否黑名单用户
     * @param data(PSPT_TYPE_CODE\PSPT_ID)
     * @throws Exception
     */
    public IDataset isBlackUser(String pspt_type, String pspt_id) throws Exception
    {
        IData data = new DataMap();
        data.put("PSPT_TYPE_CODE", pspt_type);
        data.put("PSPT_ID", pspt_id);
        return Dao.qryByCodeParser("TD_O_BLACKUSER", "SEL_BY_PK", data, Route.CONN_CRM_CEN);
    }
}
