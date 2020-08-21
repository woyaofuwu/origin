
package com.asiainfo.veris.crm.order.soa.group.blackwhiteuser;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.UserTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.MsisdnInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserBlackWhiteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserGrpPlatSvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.base.MemberBean;
import com.asiainfo.veris.crm.order.soa.group.common.query.LimitBlackwhiteBean;
import com.asiainfo.veris.crm.order.soa.group.param.adc.MemParams;

public class MgrBlackWhiteOutBean extends MemberBean
{
    protected MgrBlackWhiteOutBeanReqData reqData = null;

    protected String modifyTag = "0";

    public void actTradeBefore() throws Exception
    {
        super.actTradeBefore();
    }

    public void actTradeSub() throws Exception
    {
        super.actTradeSub();

        regBlackWhite();
        regBlackWhiteOutSyn();
    }

	public void checkGrpMebCount(String dataId, String service_id, int icount)
			throws Exception {

		if (!"4".equals(dataId))// VIP级成员数量不受限制
		{
			// 获取该集团允许的最大成员数量
			IDataset staticInfo = StaticUtil.getList(null, "TD_S_STATIC",
					"DATA_ID", "PDATA_ID",
					new String[] { "TYPE_ID", "DATA_ID" }, new String[] {
							"GRP_PLAT_RSRV_TAG2", dataId });
			if (IDataUtil.isNotEmpty(staticInfo)) {
				int grpMebCount = staticInfo.getData(0).getInt("PDATA_ID");

				if (grpMebCount <= icount) {
					CSAppException.apperr(GrpException.CRM_GRP_641,
							grpMebCount, icount);
				}
			}
		}
	}

    public void dealLimitBlackWhiteForAdd(String bizattr, String biz_in_code) throws Exception
    {
        String custName = "";// 网外号码无成员客户名
        // G代表从网关过来的
        if ("G".equals(CSBizBean.getVisit().getInModeCode()))
        {
            if ("2".equals(bizattr))
            // 黑名单
            {
                LimitBlackwhiteBean.insertLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code, custName);
            }
            if ("1".equals(bizattr))
            // 白名单
            {
                LimitBlackwhiteBean.deleteLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code, custName);
            }
        }
        // 非G代表从网关过来
        else
        {
            if ("1".equals(bizattr))
            // 白名单
            {
                IDataset limitDataset = LimitBlackwhiteBean.queryLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code);
                if (IDataUtil.isNotEmpty(limitDataset))
                {
                    CSAppException.apperr(CrmUserException.CRM_USER_1027, reqData.getUca().getSerialNumber());
                }
            }
        }
    }

    public void dealLimitBlackWhiteForDel(String biz_in_code) throws Exception
    {
        String custName = "";// 网外号码无成员客户名

        IDataset grpPlatsvcList = UserGrpPlatSvcInfoQry.getUserGrpPlatSvcByUserId(reqData.getGrpUca().getUserId());
        for (int j = 0, psize = grpPlatsvcList.size(); j < psize; j++)
        {
            String bizAttr = grpPlatsvcList.getData(j).getString("BIZ_ATTR");
            // G代表从网关过来的
            if ("G".equals(CSBizBean.getVisit().getInModeCode()))
            {
                if ("1".equals(bizAttr))
                // 白名单
                {
                    LimitBlackwhiteBean.insertLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code, custName);
                }
                if ("2".equals(bizAttr))
                // 黑名单
                {
                    LimitBlackwhiteBean.deleteLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code, custName);
                }
            }
            // 非G代表从网关过来
            else
            {
                if ("2".equals(bizAttr))
                // 白名单
                {
                    IDataset limitDataset = LimitBlackwhiteBean.queryLimitBlackWhite(reqData.getUca().getSerialNumber(), biz_in_code);
                    if (IDataUtil.isNotEmpty(limitDataset))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_1027, reqData.getUca().getSerialNumber());
                    }
                }
            }
        }
    }

    protected BaseReqData getReqData() throws Exception
    {
        return new MgrBlackWhiteOutBeanReqData();
    }

    /**
     * @description 根据user_id_b,service_id查询用户订购服务的 platsvc
     * @param user_id_b
     * @param service_id
     * @return
     * @throws Exception
     */
    public IData getUserPlatSvc(String user_id, String service_id) throws Exception
    {
        // 取集团平台参数
        IData platsvc = UserGrpPlatSvcInfoQry.getuserPlatsvcbyserverid(user_id, service_id); // 取平集团用户参数

        return platsvc;
    }

    protected void initReqData() throws Exception
    {
        super.initReqData();

        reqData = (MgrBlackWhiteOutBeanReqData) getBaseReqData();
    }

    protected void makInit(IData map) throws Exception
    {
        IDataset productElements = map.getDataset("SERVICE_INFOS");
        if (IDataUtil.isEmpty(productElements))
        {
            return;
        }
        modifyTag = productElements.getData(0).getString("MODIFY_TAG");
    }

    protected void makReqData(IData map) throws Exception
    {
        super.makReqData(map);

        reqData.setSERVICE_INFOS(map.getDataset("SERVICE_INFOS"));
    }

    protected void makUca(IData map) throws Exception
    {
        makUcaForMebOutNum(map);

    }

    /**
     * 手动构建网外号码uca
     *
     * @param map
     * @throws Exception
     */
    protected void makUcaForMebOutNum(IData map) throws Exception
    {
        String meb_user_id = map.getString("MEB_USER_ID", ""); // 成员用户ID（网外成员）

        if ("-1".equals(meb_user_id))
        {
            meb_user_id = SeqMgr.getUserId();
        }
        String out_grp_num = map.getString("SERIAL_NUMBER"); // 网外号码 sn

        UcaData ucaData = new UcaData();
        IData userInfo = new DataMap();
        userInfo.put("USER_ID", meb_user_id); // 网外号码 userId
        userInfo.put("SERIAL_NUMBER", out_grp_num); // 网外号码 sn

        ucaData.setUser(new UserTradeData(userInfo));

        reqData.setUca(ucaData);

        UcaData grpUCA = UcaDataFactory.getNormalUcaByUserIdForGrp(map);
        reqData.setGrpUca(grpUCA);
    }

    public void regBlackWhite() throws Exception
    {
        IDataset productElements = reqData.getSERVICE_INFOS(); // 集团平台服务
        if (IDataUtil.isEmpty(productElements))
        {
            return;
        }

        for (int i = 0, size = productElements.size(); i < size; ++i)
        {
            IData productParam = productElements.getData(i);
            String service_id = productParam.getString("SERVICE_ID"); // 服务id
            String user_id = reqData.getGrpUca().getUserId(); // 集团用户id

            IData platsvc = getUserPlatSvc(user_id, service_id);
            if (IDataUtil.isEmpty(platsvc))
            {
                CSAppException.apperr(GrpException.CRM_GRP_25);
            }
            String biz_status = platsvc.getString("BIZ_STATUS");
            String bizattr = platsvc.getString("BIZ_ATTR", "");

            // 20090601 业务状态非正常商用时，强制校验
            if (!biz_status.equals("A"))
            {
                CSAppException.apperr(PlatException.CRM_PLAT_85, service_id);
            }

            String modify_tag = productParam.getString("MODIFY_TAG");

            IData data = new DataMap();
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modify_tag))
            {
                data = setRegBlackWhite(productParam, platsvc);
                data.put("OPER_STATE", "01");// 加入
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.Add.getValue());
                if ("1".equals(bizattr)) // 加入黑白名单，进行该集团的成员总数校验
                {
                    String dataId = platsvc.getString("RSRV_TAG2");
                    int  icount = platsvc.getInt("RSRV_NUM1");// 成员数量
                    checkGrpMebCount(dataId, service_id,icount);
                }

                data.put("END_DATE", SysDateMgr.getTheLastTime());
            }
            else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modify_tag))
            {
                String outSn = reqData.getUca().getSerialNumber();
                String grpUserId = reqData.getGrpUca().getUserId();
                String serviceId = productParam.getString("SERVICE_ID");

                // 根据集团服务ID,获取集团服务对应成员服务。
                String mebServId = MemParams.getmebServIdByGrpServId(serviceId);

                IDataset datas = UserBlackWhiteInfoQry.getBlackWhiteInfoBySnEcuseridSerid(outSn, grpUserId, mebServId);
                if (IDataUtil.isEmpty(datas))
                {
                    CSAppException.apperr(CustException.CRM_CUST_193);
                }
                data = datas.getData(0);
                data.put("OPER_STATE", "02");// 退出
                data.put("MODIFY_TAG", TRADE_MODIFY_TAG.DEL.getValue());

                data.put("END_DATE", getAcceptTime());
            }

            data.put("IS_NEED_PF", "1");// // 1或者是空 走服务开通发指令,0：不走服务开通不发指令
            data.put("RSRV_TAG3", "0");// 标识是否走服务开通 0 正常走服务开通模式 1 ADC平台 2 行业网关

            this.addTradeBlackwhite(data);

            String biz_in_code = platsvc.getString("BIZ_IN_CODE");

            if (TRADE_MODIFY_TAG.Add.getValue().equals(modify_tag))
            {
                dealLimitBlackWhiteForAdd(bizattr, biz_in_code);
            }
            if (TRADE_MODIFY_TAG.DEL.getValue().equals(modify_tag))
            {
                dealLimitBlackWhiteForDel(biz_in_code);
            }
        }
    }

    /**
     * 登记黑白名单同步信息
     * @author
     * @date 2014-11-7
     * @throws Exception
     */
    public void regBlackWhiteOutSyn()throws Exception{

        IDataset productElements = reqData.getSERVICE_INFOS(); // 集团平台服务
        if (IDataUtil.isEmpty(productElements))
        {
            return;
        }

        IData data = new DataMap();
        data.put("TRADE_ID", getTradeId());
        data.put("ACCEPT_MONTH", getTradeId().substring(4, 6));
        data.put("USER_ID", reqData.getUca().getUser().getUserId());
        data.put("SERIAL_NUMBER", reqData.getUca().getUser().getSerialNumber());  //用户手机号码

        data.put("GROUP_ID",  reqData.getGrpUca().getCustGroup().getGroupId());
        data.put("CUST_NAME",  reqData.getGrpUca().getCustomer().getCustName());  //集团客户名称

        for (int i = 0, size = productElements.size(); i < size; ++i)
        {
            IData productParam = productElements.getData(i);
            String service_id = productParam.getString("SERVICE_ID"); // 服务id
            String user_id = reqData.getGrpUca().getUserId(); // 集团用户id

            IData platsvc = getUserPlatSvc(user_id, service_id);
            if (IDataUtil.isEmpty(platsvc))
            {
                CSAppException.apperr(GrpException.CRM_GRP_25);
            }

            String  bizattr=platsvc.getString("BIZ_ATTR","");
            String usertypecode = null;
            if("1".equals(bizattr))//白名单
            {
                usertypecode="1";
            }
            else if("2".equals(bizattr))//黑名单
            {
                usertypecode="0";
            }
            if(usertypecode == null){
                return;
            }

            data.put("SERV_CODE",   platsvc.getString("BIZ_IN_CODE"));
            data.put("BIZ_CODE",   platsvc.getString("BIZ_CODE"));
            data.put("USER_TYPE_CODE",  usertypecode);

            String modify_tag = productParam.getString("MODIFY_TAG");
            if (TRADE_MODIFY_TAG.Add.getValue().equals(modify_tag))
            {
                data.put("OPER_STATE", "01"); // 加入
                data.put("END_DATE", SysDateMgr.getTheLastTime());
            } else if (TRADE_MODIFY_TAG.DEL.getValue().equals(modify_tag))
            {
                data.put("OPER_STATE", "02"); // 退出
                data.put("END_DATE", getAcceptTime());
            }

            data.put("START_DATE",  reqData.getAcceptTime());

            data.put("PROVINCE_CODE",   CSBizBean.getVisit().getProvinceCode()); //集团客户归属省代码
            IData  msisdnInfo = MsisdnInfoQry.getMsisonBySerialnumber(reqData.getUca().getUser().getSerialNumber(), null);
            data.put("PROVINCE",  msisdnInfo.getString("PROV_CODE"));       //成员号码所属省份

            data.put("SYNC_TYPE",  "01");
            data.put("STATUS",  "");
            data.put("BK_RESULT",  "");
            data.put("STATUS_CODE",  "01");
            data.put("UPDATE_TIME",SysDateMgr.getSysTime());
            data.put("UPDATE_STAFF_ID", CSBizBean.getVisit().getStaffId());
            data.put("UPDATE_DEPART_ID",CSBizBean.getVisit().getDepartId());
            data.put("RSRV_NUM1",   "");
            data.put("RSRV_NUM2",   "");
            data.put("RSRV_NUM3",   "");
            data.put("RSRV_STR1",   "");
            data.put("RSRV_STR2",   "");
            data.put("RSRV_STR3",   "");
            data.put("RSRV_DATE1",   "");
            data.put("RSRV_DATE2",   "");
            data.put("RSRV_DATE3",   "");
            data.put("RSRV_TAG1",   "");
            data.put("RSRV_TAG2",   "");
            data.put("RSRV_TAG3",   "");
            Dao.insert("TF_F_BLACKWHITE_OUT", data,Route.CONN_CRM_CEN);
        }
    }

    public String replaceBillingType(String isfree, String billingmode)
    {
        String membillingtype = "";
        if (isfree.equals("00"))// 整个业务免费时
        {
            membillingtype = "2";// 免费
        }
        else if (billingmode.equals("4"))// 上行按集团 下行按集团时为免费
        {
            membillingtype = "0";// 集团付费
        }
        else if (billingmode.equals("2"))// 上行按用户 下行按用户时为个人付费
        {
            membillingtype = "1";// 个人付费
        }
        else
        // 按集团付费
        {
            membillingtype = "0";// 集团付费
        }
        return membillingtype;
    }

    public String replaceBizAtrr(String bizattr)
    {
        String usertypecode = null;
        if ("0".equals(bizattr))// 订购关系
        {
            usertypecode = "S";
        }
        else if ("1".equals(bizattr))// 白名单
        {
            usertypecode = "W";
        }
        else if ("2".equals(bizattr))// 黑名单
        {
            usertypecode = "B";
        }

        return usertypecode;
    }

    public IData setRegBlackWhite(IData productParam, IData platsvc) throws Exception
    {
        String bizattr = platsvc.getString("BIZ_ATTR", "");
        String usertypecode = replaceBizAtrr(bizattr);

        String isfree = platsvc.getString("BILLING_TYPE", "");
        String billingmode = platsvc.getString("BILLING_MODE", "");
        String membillingtype = replaceBillingType(isfree, billingmode);

        String service_id = productParam.getString("SERVICE_ID"); // 服务id
        // 根据集团服务ID,获取集团服务对应成员服务。
        String mebServId = MemParams.getmebServIdByGrpServId(service_id);

        String startdate = productParam.getString("START_DATE", "");
        if (startdate.length() > 19)
        {
            startdate = startdate.substring(0, 19);
        }
        if (StringUtils.isBlank(startdate))
        {
            startdate = getAcceptTime();
        }
        String instId = SeqMgr.getInstId();
        IData data = new DataMap();
        data.put("INST_ID", instId);
        data.put("USER_ID", reqData.getUca().getUserId());
        data.put("USER_TYPE_CODE", usertypecode);
        data.put("EC_USER_ID", reqData.getGrpUca().getUserId());
        data.put("SERV_CODE", platsvc.getString("BIZ_IN_CODE"));
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber()); //
        data.put("GROUP_ID", platsvc.getString("GROUP_ID"));
        data.put("BIZ_CODE", platsvc.getString("BIZ_CODE"));
        data.put("BIZ_NAME", platsvc.getString("BIZ_NAME"));
        data.put("BIZ_DESC", platsvc.getString("BIZ_DESC"));
        data.put("REMARK", reqData.getRemark());
        data.put("RSRV_NUM1", "0");
        data.put("RSRV_NUM2", "0");
        data.put("RSRV_NUM3", "0");
        data.put("RSRV_NUM4", "0");
        data.put("RSRV_NUM5", "0");
        data.put("RSRV_STR1", "");
        data.put("RSRV_STR2", "");
        data.put("RSRV_STR3", "");
        data.put("RSRV_STR4", "");
        data.put("RSRV_STR5", "");
        data.put("RSRV_DATE1", "");
        data.put("RSRV_DATE2", "");
        data.put("RSRV_DATE3", "");
        data.put("RSRV_TAG1", "");
        data.put("RSRV_TAG2", "");
        data.put("RSRV_TAG3", productParam.getString("RSRV_TAG3", "0"));
        data.put("BIZ_IN_CODE", platsvc.getString("BIZ_IN_CODE"));
        //adc类业务BIZ_IN_CODE_A的是 SI_BASE_IN_CODE_A,mas类业务取的是 EC_BASE_IN_CODE_A
        if("ADCG".equals(reqData.getGrpUca().getBrandCode()))
        {
            data.put("BIZ_IN_CODE_A", platsvc.getString("SI_BASE_IN_CODE_A"));
            
        }
        else
        {
            data.put("BIZ_IN_CODE_A", platsvc.getString("EC_BASE_IN_CODE_A"));
            
        }
        data.put("SERVICE_ID", mebServId);
        data.put("BILLING_TYPE", membillingtype);

        data.put("PLAT_SYNC_STATE", "1");// 服务状态
        data.put("EC_SERIAL_NUMBER", platsvc.getString("SERIAL_NUMBER"));

        data.put("START_DATE", startdate);

        String expect_time = productParam.getString("EXPECT_TIME", "");
        if (StringUtils.isBlank(expect_time))
        {
            expect_time = getAcceptTime();
        }
        data.put("EXPECT_TIME", expect_time);
        data.put("OPR_EFF_TIME", data.getString("START_DATE"));

        return data;
    }

    protected void chkTradeBefore(IData map) throws Exception
    {

    }

    /**
     * 处理台帐主表的数据
     */
    protected void regTrade() throws Exception
    {
        IData data = bizData.getTrade();

        data.put("CUST_NAME", ""); // 客户名称
        data.put("CUST_ID", "-1");
        data.put("ACCT_ID", "-1"); // 帐户标识
        data.put("NET_TYPE_CODE", "G"); // 网别编码
        data.put("EPARCHY_CODE", reqData.getGrpUca().getUser().getEparchyCode()); // 归属地州
        data.put("CITY_CODE", reqData.getGrpUca().getUser().getCityCode()); // 归属业务区
        data.put("PRODUCT_ID", "-1"); // 产品标识
        data.put("CUST_ID_B", reqData.getGrpUca().getCustId()); // 客户标识B：关联业务中的B客户标识，通常为一集团客户或虚拟客户。对于非关联业务填-1
        data.put("ACCT_ID_B", reqData.getGrpUca().getAcctId()); // 帐户标识B：关联业务中的B帐户标识，通常为一集团帐户或虚拟帐户。对于非关联业务填-1
        data.put("BRAND_CODE", reqData.getGrpUca().getBrandCode());// 品牌编码

        data.put("USER_ID", reqData.getUca().getUserId()); // 网外的
        data.put("SERIAL_NUMBER", reqData.getUca().getSerialNumber());

        data.put("USER_ID_B", reqData.getGrpUca().getUserId()); // 集团的
        data.put("SERIAL_NUMBER_B", reqData.getGrpUca().getSerialNumber());

    }

    protected void initProductCtrlInfo() throws Exception
    {
        String bizType = BizCtrlType.CreateMember;
        if (TRADE_MODIFY_TAG.DEL.getValue().equals(modifyTag))
        {
            bizType = BizCtrlType.DestoryMember;
        }
        String productId = reqData.getGrpUca().getProductId();

        getProductCtrlInfo(productId, bizType);
    }
}
