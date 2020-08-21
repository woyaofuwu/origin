
package com.asiainfo.veris.crm.order.soa.frame.csservice.group.groupintf.transtrade;

import com.ailk.biz.BizVisit;
import com.ailk.biz.service.BizRoute;
import com.ailk.biz.util.StaticUtil;
import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.session.SessionManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.ProductException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.bizctrl.BizCtrlType;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupBaseConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.Clone;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UAttrBizInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.template.TemplateBean;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.AcctCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.ResCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.GrpInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.other.OtherInfoQrySVC;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeGrpMerchMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.trade.TradeReceptionHallMebInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.*;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.SynMebWordOrder.ActualMebWorkOrderThread;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.SynMebWordOrder.JKDTActualMeb;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz.AsynchronizationProcessor;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossPayBiz.BbossPayBiz;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commoncomponent.BbossOrderVerifyBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.GrpCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.IntfField;
import org.apache.log4j.Logger;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class GrpIntf extends CSBizBean
{
	private static final Logger log = Logger.getLogger(GrpIntf.class);

    /**
     * chenyi 成员叠加包订购同步/成员叠加包订购归档 2014-4-8
     */
    protected static IDataset bbossPayBiz (IData inparam) throws Exception
    {
        // 0- 克隆入参,以避免被底层修改
        IData data = (IData) Clone.deepClone(inparam);

        // 1-定义并且初始化返回结果
        IDataset dealResultInfoList = new DatasetList();

        // 2- 开通工单校验
        String action = data.getString("ACTION");

        if (StringUtils.contains("2", action))
        {
            // 2-1 工单重复校验
            dealResultInfoList = BbossOrderVerifyBean.verifyBbossPayBizOrder(data);

            if(IDataUtil.isEmpty(dealResultInfoList))
            {
                // 2-2 工单集团校验  
                IData map = BbossPayBiz.bbossPayBizOrderOpenChkGrp(data);

                //集团校验通过则进行成员校验并发送成员开通反馈
                if(StringUtils.equals("00", map.getString("ORDER_RESULT")))
                {

                    //文件接口直接校验成员号码并反馈开通结果给IBOSS
                    String returnFlag = data.getString("RETURN_FLAG_KT", "");
                    if (StringUtils.isNotEmpty(returnFlag))
                    {
                        data.put("SERIAL_NUMBER", data.getString("MEMBER_NUMBER",""));
                        data.put("PRODUCTID", data.getString("PRODUCT_ID",""));
                        IDataset result = CSAppCall.call("CS.BbossPayBizSVC.bbossPayBizOrderOpenChkMeb", data);
                        IData id = result.getData(0);
                        map.put("MEMBER_NUMBER", IDataUtil.getDataset("MEMBER_NUMBER",id).get(0).toString());
                        map.put("FAIL_STATUS", IDataUtil.getDataset("FAIL_STATUS",id).get(0).toString());
                        map.put("FAIL_DESC", IDataUtil.getDataset("FAIL_DESC",id).get(0).toString());

                    }
                    //实时接口异步反馈开通结果
                    else
                    {

                        AsynchronizationProcessor pro = new AsynchronizationProcessor(data, (BizVisit)CSBizBean.getVisit(),SessionManager.getInstance().peek()) {};

                        pro.start();
                    }
                }
                dealResultInfoList.add(map);
            }
            return dealResultInfoList;//校验失败直接返回
        }
        // 3- 下发归档
        else if(StringUtils.contains("3", action))
        {
            // 3-1 获取服务号码
            IDataset member_numberlist = IDataUtil.getDatasetSpecl("MEMBER_NUMBER", data);
            IDataset member_numberDataList = (IDataset) Clone.deepClone(member_numberlist);

            // 3-2 获取订购流量叠加包
            IDataset member_order_ratelist = IDataUtil.getDatasetSpecl("MEMBER_ORDER_RATE", data);
            IDataset member_order_rateDataList = (IDataset) Clone.deepClone(member_order_ratelist);

            // 3-3 如果是归档，获取bboss下发的开通结果
            IDataset openStatusList = IDataUtil.getDatasetSpecl("FAIL_STATUS", data);
            IDataset openStatusDataList = (IDataset) Clone.deepClone(openStatusList);

            // 3-4 循环拆单
            for (int i = 0, sizeI = member_numberDataList.size(); i < sizeI; i++)
            {

                // 3-4-0  开通状态
                data.put("OPEN_STATUS", openStatusDataList.get(i));

                if (!"00".contains(data.getString("OPEN_STATUS")))
                {
                    continue ;//判断该手机号是否开通工单成功，失败直接返回
                }

                String serial_number = member_numberDataList.get(i).toString();

                data.put("SERIAL_NUMBER", serial_number);
                data.put("MEMBER_NUMBER", serial_number);

                // 3-4-1 获取成员用户信息
                String memberUserId = MebCommonBean.getMemberUserId(data);

                // 判断是否外省号码
                IDataset memberUserInfoList = MebCommonBean.searchOuterMemUserInfo(serial_number);

                // 非本省号码将生成工单完工，本省号码不做处理，直接返回成功
                if(StringUtils.isBlank(memberUserId) || IDataUtil.isNotEmpty(memberUserInfoList))
                {

	                // 3-4-2 获取用户订购流量叠加包
	                data.put("MEMBER_ORDER_RATE", member_order_rateDataList.get(i));

	                // 3-4-3 产品订购关系
	                String product_id = data.getString("PRODUCT_ID", "");

	                data.put("PRODUCTID", product_id);

	                // 3-4-4 获取成员号码地州，网外号默认设置为
	                String memEparchCode = getMebEparchCodeBySn(data);

	                // 3-4-5 添加用户路由(后台生成虚拟号码用)
	                data.put(Route.USER_EPARCHY_CODE, memEparchCode);

	                // 3-4-6 设置成员路由
	                data.put(Route.ROUTE_EPARCHY_CODE, memEparchCode);

	                // 3-4-7 设置员工交易地州
	                data.put("TRADE_EPARCHY_CODE", memEparchCode);

	                // 3-4-8 设置反向标记
	                data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口

	                // 3-4-9- 流量叠加包标识
	                data.put("PayBiz", true);


	                // 3-4-10  成员反向订购流量叠加包
                    dealResultInfoList = CSAppCall.call("CS.BbossPayBizSVC.crtBbossPayBiz", data);

                }
            }

            dealResultInfoList.clear();
            IData result_data = new DataMap();

            result_data.put("ORDER_RESULT", "00");
            result_data.put("MEMBER_ORDER_NUMBER", inparam.getString("MEMBER_ORDER_NUMBER", ""));
            result_data.put("MEMBER_NUMBER", IDataUtil.getDataset("MEMBER_NUMBER",inparam));
            result_data.put("FAIL_STATUS", IDataUtil.getDataset("FAIL_STATUS",inparam));
            result_data.put("FAIL_DESC", IDataUtil.getDataset("FAIL_DESC",inparam));
            result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
            result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
            result_data.put("RSPCODE", "00");
            result_data.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
            result_data.put("TRADE_ID", "-1");
            dealResultInfoList.add(result_data);
        }
        // 4- 返回处理结果
        return dealResultInfoList;
    }

    /**
     * chenyi 成员接口查询 2015-2-9
     */
    protected static IDataset getOrderInfo(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();
        dealResult = CSAppCall.call("CS.BbossMemberBizSVC.getOrderInfo", data);
        return dealResult;
    }
    /*
     * @description 反向类型为集团业务
     * @author xunyl
     * @date 2013-07-10
     */
    protected static IDataset dealBbossGroupBiz(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 2- 获取一级BOSS传递的商品操作类型
        String merchOperType = IDataUtil.getDataset("OPERA_TYPE", data, false).get(0).toString();

        // 3- 修正一级BOSS传递的商品操作类型(典型场景:一级BOSS里面的商品操作类型为13，即业务开展省新增或者删除，对于落地省来说意味着3种情况，第一种情
        // 况为本省是新增的业务开展省，需要进行商品新增，第二种是本省是被删除的业务开展省，需要进行商品注销，第三种是本省是业务主办省，无须进行任何操作)
        modifyOperType(merchOperType, data);

        //4-校验是否为重复归档
        dealResult=BbossOrderVerifyBean.verifyGrpBBossRsp(data);
        if(IDataUtil.isNotEmpty(dealResult)){
            return dealResult;
        }

        // 5- 将修正后的商品操作类型调用不同的服务处理类
        dealResult = merchOpTypeToProOpType(merchOperType, data);

        // 6- 返回结果
        return dealResult;
    }

    /**
     *
     * 集团业务规则校验 zhangcz 2018-9-25
     * @param data
     * @return
     * @throws Exception
     */
    protected static IDataset dealEcCheckInfoBiz(IData data) throws Exception {

        String account_number = data.getString("ACCOUNT_NUMBER");// 省公司账户编码
        String ec_number = data.getString("EC_NUMBER");// 全网编码
        String host_company = data.getString("HOST_COMPANY");// 主办省编码
        String orderSourceID=data.getString("ORDER_SOURCE_ID", "0");//订单来源
        
        String hostCompany = "898"; //海南省编码
        String erroInfo; //错误信息

        //返回值
        IDataset ids = new DatasetList();
        IData result_data = new DataMap();

        result_data.put("querySequence", data.getString("QUERY_SEQUENCE", "")); //返回 --- 查询流水
        ids.add(result_data);
        if(!orderSourceID.equals("1") && !orderSourceID.equals("2") && !orderSourceID.equals("")){
        	//1-集客大厅2-政企ESP当订单来源是政企ESP时，此字段必填 update by zhuwj
        	  result_data.put("rspCode", "12");
              result_data.put("rspDesc", "订单来源错误");
              return ids;
        	
        }
        

        //01 - 集团客户编码错误（是否是空）
        if (StringUtils.isBlank(ec_number)) {//判断为空
            result_data.put("rspCode", "01");
            result_data.put("rspDesc", "集团客户编码错误");
            return ids;
        }

        //06 - 集团客户状态错误
        // 1、根据EC客户编号获取集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(ec_number, null);
        // 2- 集团客户信息不存在，抛出异常
        if (IDataUtil.isEmpty(groupInf))
        {
            result_data.put("rspCode", "06");
            result_data.put("rspDesc", "集团客户状态错误");
            return ids;
        }
        String cust_id = groupInf.getData(0).getString("CUST_ID");//客户编码


        //02 - 省公司账户编码错误
        if (StringUtils.isNotBlank(account_number) &&
                IDataUtil.isEmpty(AcctInfoQry.getAcctInfoByAcctIdCustId(account_number, cust_id))) {
            result_data.put("rspCode", "02");
            result_data.put("rspDesc", "省公司账户编码错误");
            return ids;
        }

        //03 - 主办省错误
        if (!StringUtils.equals(host_company, hostCompany)) {
            result_data.put("rspCode", "03");
            result_data.put("rspDesc", "主办省错误");
            return ids;
        } 

        //04 && 05
        IDataset po_info = data.getDataset("PO_INFO");
        String po_spec_number="";
        for (int i = 0; i < po_info.size(); i++) {
            IData data_single = po_info.getData(i);
            String order_type = data_single.getString("ORDER_TYPE"); //操作类型
            String product_offering_id = data_single.getString("PRODUCT_OFFERING_ID");  // 商品订购关系
            po_spec_number = data_single.getString("PO_SPEC_NUMBER"); //商品规格编号

            //04 - 操作类型错误
            if (!(StringUtils.equals(order_type, "1") || StringUtils.equals(order_type, "2") || StringUtils.equals(order_type, "3"))) {
                result_data.put("rspCode", "04");
                result_data.put("rspDesc", "操作类型错误");
                return ids;
            } else if (StringUtils.equals(order_type, "2") && StringUtils.isBlank(product_offering_id)) { //如果类型是2 ， 订购关系不能为空
                result_data.put("rspCode", "04");
                result_data.put("rspDesc", "操作类型错误");
                return ids;
            }

            //05 - 商品规格编号不存在
            if (StringUtils.isBlank(po_spec_number)){
                result_data.put("rspCode", "05");
                result_data.put("rspDesc", "商品规格编号不能为空");
                return ids;
            }

            //商品订购校验
            //2个入参PO_NUMBER，TYPE（0-商品 1-产品 2-资费），根据TYPE传不同的全网编码，都为必传参数
            IDataset offerInfoset = UpcCall.queryOfferMappingByBossNumber(po_spec_number,"0");
            if(IDataUtil.isEmpty(offerInfoset)){
                  result_data.put("rspCode", "05");
                  result_data.put("rspDesc", "商品规格编号不能为空");
                  return ids;
            }

            //07 - 商品订购关系ID不存在
            if (StringUtils.isBlank(po_spec_number)){
                result_data.put("rspCode", "07");
                result_data.put("rspDesc", "商品订购关系ID不能为空");
                return ids;
            }
            //如果是政企订单，则校验订购关系id是否存在 update by zhuwj
           // System.out.print("product_offering_id:"+product_offering_id);
            if(orderSourceID.equals("2") && product_offering_id !="" && product_offering_id !=null && !"1".equals(order_type))
            {
            	if (StringUtils.isBlank(product_offering_id)){
            		result_data.put("rspCode", "09");
                    result_data.put("rspDesc", "产品订购关系ID不存在");
                    return ids;
            	}
                      	
            }
            IDataset result = UserEcrecepOfferfoQry.qryJKDTMerchInfoByMerchOfferId(product_offering_id);
            //产品订购时，判断是否已经订购，已经订购则返回错误
            if ("1".equals(order_type) && IDataUtil.isNotEmpty(result)&&(!"50011".equals(po_spec_number))) {
                result_data.put("rspCode", "05");
                result_data.put("rspDesc", "商品订购关系ID:" + po_spec_number + "错误");
                return ids;
            }

            //产品注销时，判断是否存在该记录，不存在则返回错误
            if("2".equals(order_type) && IDataUtil.isEmpty(result)){
                result_data.put("rspCode", "07");
                result_data.put("rspDesc", "商品订购关系ID:" + product_offering_id + "不存在");
                return ids;
            }

        }

        //REQ202003300003 关于优化企业视频彩铃受理界面欠费办理规则的需求--开发欠费红名单界面
        for (int j = 0; j < po_info.size(); j++) {
            IData data_single = po_info.getData(j);
            po_spec_number = data_single.getString("PO_SPEC_NUMBER"); //商品规格编号
            //2个入参PO_NUMBER，TYPE（0-商品 1-产品 2-资费），根据TYPE传不同的全网编码，都为必传参数
            IDataset offerInfoset = UpcCall.queryOfferMappingByBossNumber(po_spec_number,"0");
            if(!IDataUtil.isEmpty(offerInfoset.getData(0))){
                IData paramData = new DataMap();
                paramData.put("GROUP_ID",groupInf.getData(0).getString("GROUP_ID"));
                paramData.put("OFFER_CODE",offerInfoset.getData(0).getString("OFFER_CODE"));
                paramData.put("REMOVE_TAG","0");

                IDataset redsInfos = CSAppCall.call("SS.TwoBusiArrearageRedSVC.selectRednfo", paramData);
                if (!DataUtils.isEmpty(redsInfos)) {
                    result_data.put("rspCode", "00");
                    result_data.put("rspDesc", "校验通过，允许订购");
                    return ids;
                }



            }

        }
        //REQ202003300003 关于优化企业视频彩铃受理界面欠费办理规则的需求--开发欠费红名单界面
        //99 - 02账户欠费查询
        IDataset acctInfoList = AcctInfoQry.getAcctInfoByCustId(cust_id); //集团成员下所有的账户列表

        if (IDataUtil.isNotEmpty(acctInfoList)) { //防空
            //遍历用户下所有账户
            for (int i = 0; i < acctInfoList.size(); i++) {
                //判断是否为空
                if (IDataUtil.isNotEmpty(acctInfoList.getData(i))) {
                    String acct_id = acctInfoList.getData(i).getString("ACCT_ID");

                    //单个账户是否欠费
                    IData accData = new DataMap();
                    accData.put("ACCT_ID", acct_id);
                    IDataset ds=null;
                    
                    if("010113001".equals(po_spec_number)){
                    	//算上缴费周期 update by zhuwj
                    	ds = CSAppCall.call("AM_CRM_GetGroupAccountOwefee", accData);
                    //	System.out.print("zzzewj"+ds);
                
                    	 if (DataSetUtils.isNotBlank(ds) && "010113001".equals(po_spec_number)){
                         	IData acctData = ds.getData(0);
                         	 if (IDataUtil.isNotEmpty(acctData)){
                         		Calendar start = Calendar.getInstance();
                    	        Calendar end = Calendar.getInstance();
                         		SimpleDateFormat format=new SimpleDateFormat("yyyyMM");
                         		Date starDate=format.parse(acctData.getString("MIN_OWE_MONTH",""));
                     	        Date endDate=format.parse(format.format(new Date())); 
                     	        start.setTime(starDate);
                   	            end.setTime(endDate);
                   	            int result = end.get(Calendar.MONTH) - start.get(Calendar.MONTH);
                   	            int month = (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12;
                                 if(Math.abs(month + result)>3){
                                	 erroInfo = "该服务号码有欠费记录，不能办理托收业务。";
                                     result_data.put("rspCode", "99");
                                     result_data.put("rspDesc", erroInfo);
                                     return ids;
                                 }
                         	 }
                            
                         }
                    }else{
                    	ds = CSAppCall.call("AM_CRM_GetAccountOwefee", accData);
                    }
                   
                    if (DataSetUtils.isNotBlank(ds) && !"010113001".equals(po_spec_number)) {
                        IData acctData = ds.getData(0);
                        if (IDataUtil.isNotEmpty(acctData)) {
                        	//有欠费
                            //检查当前工号是否有权限，在欠费情况下办理托收，反向工号默认值：IBOSS000
                            String privTag = "0";
                            
                            if (StaffPrivUtil.isFuncDataPriv(getVisit().getStaffId(), "ACCT_PAYMODE_CHGPRIV")) {//有权限
                                privTag = "1";
                            }
                            if ("1".equals(privTag)) {
                            	//有权限
                                erroInfo = "您已特殊办理银行托收业务，为避免停机给您造成不便，请您务必及时缴清往月欠费。";
                                result_data.put("rspCode", "00");
                                result_data.put("rspDesc", erroInfo);
                                return ids;
                            } else {//无权限
                                erroInfo = "该服务号码有欠费记录，不能办理托收业务。";
                                result_data.put("rspCode", "99");
                                result_data.put("rspDesc", erroInfo);
                                return ids;
                            }
                        }
                    }
                }
            }
        }

        result_data.put("rspCode", "00");
        result_data.put("rspDesc", "校验通过，允许订购");
        return ids;
    }

    /**
     *
     * @description 成员业务反向业务 V2.0
     * @param data
     * @return
     * @throws Exception
     */
    protected static IDataset dealReceptionHallMemberService(IData data) throws Exception {
        //0- 日志保存信息

    	//判断是否是下发成员校验，是则进行成员校验
    	String orderType = data.getString("ORDER_TYPE", "");
        data.put(IntfField.ANTI_INTF_FLAG[0], "1");
        data.put("RECEPTIONHALLMEM", "1");

        if("3".equals(orderType)){//"1"代表成员开通   ，"2"代表成员归档     "3"代表成员校验
            IDataset checkResultList = new DatasetList();
            checkResultList = dealJKDTMemberNumber(data,false);
            return checkResultList;
        }
        if("5".equals(orderType)){//校验成员号码
    		IDataset checkResultList = new DatasetList();
    		checkResultList = checkJKDTMemberNumber(data);//校验成员号码
    		return checkResultList;
    	}
        String isNotTwoCheck ="";
    	if("4".equals(orderType)){//校验,二次短信,开通
    		IDataset checkResultList = new DatasetList();
    	   checkResultList = dealJKDTMemberNumber(data,true);//校验,二次短信,开通
    	   isNotTwoCheck =  checkResultList.getData(0).getString("IS_NOT_TWOCHECK","");
   	       if("".equals(isNotTwoCheck)){
   	    	return checkResultList;
   	    }
    	}



        //1- 定义返回结果
        IDataset dealResult = new DatasetList();
        // 2- 设置方向标记

        // 3- 获取产品订单编码，非空表示签约工单同步,空则表示签约关系归档
        if (StringUtils.equals(orderType, "1")||"0".equals(isNotTwoCheck)){
                //正常工单处理  其中包含普通和文件
            IData rspResult = synJKDTMebWorkOrder(data);
            return IDataUtil.idToIds(rspResult);
        }

        // 3- 判断订单是否完工，是 --- 返回成功
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", data);
        String productOfferId = (String) productIdList.get(0);
        IDataset UserGrpmerchpInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId); //产品信息
        if(IDataUtil.isEmpty(UserGrpmerchpInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141,IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData userGrpMerchpInfo = UserGrpmerchpInfoList.getData(0);
        String productSpecCode = userGrpMerchpInfo.getString("PRODUCT_SPEC_CODE","");
        boolean isNeedDeal = !MebCommonBean.hasMebOrderOpen(productSpecCode);
        String orderSource = data.getString("SRC", "");
//        if(isNeedDeal || StringUtils.equals("0", orderSource))//校验成员工单开通签约关系阶段是否产生过用户资料信息，如果已经存在则直接反馈成功
//        {
            //多个服务号码须循环处理返回符合业务需求的数据
        IDataset singleSerialNumberList = dealserialNumberListJKDT(data);
        if(IDataUtil.isNotEmpty(singleSerialNumberList))
        {
            for (int i = 0; i < singleSerialNumberList.size(); i++)
            {
                IData mebOpenOrderMap = singleSerialNumberList.getData(i);
                String oSubTypeID = mebOpenOrderMap.getString("ACTION");
                    
                String serialNumber = mebOpenOrderMap.getString("SERIAL_NUMBER");
                boolean isOutNetSn = MebCommonBean.isOutNetSn(serialNumber);//判断是否是网外号码

                if(isNeedDeal || StringUtils.equals("0", orderSource) || (isOutNetSn && "99971000195".equals(productOfferId)))//校验成员工单开通签约关系阶段是否产生过用户资料信息，如果已经存在则直接反馈成功
                {
                    // 根据成员操作类型调用不同的服务进行处理
                    if ("1".equals(oSubTypeID))
                    {// 成员新增
                        dealResult = CSAppCall.call("CS.CreateReceptionHallMemSVC.crtOrder", mebOpenOrderMap);
                    }
                    else if ("0".equals(oSubTypeID))
                    {// 成员删除
                        dealResult = CSAppCall.call("CS.DestroyReceptionHallMemSVC.dealReceptionHallMebBiz", mebOpenOrderMap);
                    }
                    else if(isOutNetSn && "6".equals(oSubTypeID)){
                    	//网外号码的成员属性变更，直接返回成功
                    }
                    else
                    {// 成员变更
                        dealResult = CSAppCall.call("CS.ChangeReceptionHallMemSVC.crtOrder", mebOpenOrderMap);
                    }
                 }

            }
        }
 //     }

        // 4- 处理成功或者无须处理的情况皆返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE", "00");
        result_data.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        dealResult.add(result_data);

        // 5- 返回结果
        return dealResult;

    }

    /*
     * @description 反向类型为成员业务
     * @author xunyl
     * @date 2013-07-10
     */
    protected static IDataset dealBbossMemBiz(IData data) throws Exception
    {
     // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 2- 设置方向标记
        data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口

        // 3- 获取产品订单编码，非空表示签约工单同步,空则表示签约关系归档
        String offerId = data.getString("ORDER_NO", "");
        if (!"".equals(offerId))
        {
        	//正常工单处理
            IData rspResult = synMebWorkOrder(data);
            return IDataUtil.idToIds(rspResult);
        }

        // 3- 判断成员业务是否有开通工单环节，有开通环节的业务直接反馈成功
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", data);
        String productOfferId = (String) productIdList.get(0);
        IDataset UserGrpmerchpInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
        if(IDataUtil.isEmpty(UserGrpmerchpInfoList))
        {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141,IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData userGrpMerchpInfo = UserGrpmerchpInfoList.getData(0);
        String productSpecCode = userGrpMerchpInfo.getString("PRODUCT_SPEC_CODE","");
        boolean isNeedDeal = !MebCommonBean.hasMebOrderOpen(productSpecCode);
        String orderSource = data.getString("SRC", "");
        if(isNeedDeal || StringUtils.equals("0", orderSource))//校验成员工单开通签约关系阶段是否产生过用户资料信息，如果已经存在则直接反馈成功
        {
            //多个服务号码须循环处理返回符合业务需求的数据
            IDataset singleSerialNumberList = dealserialNumberList(data);
            if(IDataUtil.isNotEmpty(singleSerialNumberList))
            { 
                for (int i = 0; i < singleSerialNumberList.size(); i++)
                {
                    IData mebOpenOrderMap = singleSerialNumberList.getData(i);
                    String oSubTypeID = mebOpenOrderMap.getString("ACTION");
                    // 根据成员操作类型调用不同的服务进行处理
                    if ("1".equals(oSubTypeID))
                    {// 成员新增
                        dealResult = CSAppCall.call("CS.CreateBBossMemSVC.crtOrder", mebOpenOrderMap);
                    }
                    else if ("0".equals(oSubTypeID))
                    {// 成员删除
                        dealResult = CSAppCall.call("CS.DestroyBBossMemSVC.dealBBossMebBiz", mebOpenOrderMap);
                    }
                    else
                    {// 成员变更
                        dealResult = CSAppCall.call("CS.ChangeBBossMemSVC.crtOrder", mebOpenOrderMap);
                    }
                }
            }
        }

        // 4- 处理成功或者无须处理的情况皆返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE", "00");
        result_data.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        dealResult.add(result_data);

        // 5- 返回结果
        return dealResult;
    }

    public static IDataset dealserialNumberListJKDT(IData data) throws Exception {

        /**
         *
         * memberNumber
         * extends
         */
        IDataset singleserialNumberList= new DatasetList();
        IDataset actionInfoList = IDataUtil.getDatasetSpecl("ACTION", data);
        IDataset userTypeInfoList = IDataUtil.getDatasetSpecl("USER_TYPE", data);
        IDataset mebGroupNumberInfoList = IDataUtil.getDatasetSpecl("RSRV_STR10", data);
        IDataset effDateInfoList = IDataUtil.getDatasetSpecl("EFFDATE", data);
        IDataset characterIDInfoList = IDataUtil.getDataset("RSRV_STR11", data);
        IDataset characterNameInfoList = IDataUtil.getDataset("RSRV_STR12", data);
        IDataset characterValueInfoList = IDataUtil.getDataset("RSRV_STR13", data);
        //  获取成员列表
        IDataset serialNumberList = IDataUtil.getDatasetSpecl(IntfField.SERIAL_NUMBER[0], data);
        if (IDataUtil.isEmpty(serialNumberList)) {
            CSAppException.apperr(GrpException.CRM_GRP_30);
        }
        for (int i = 0; i < serialNumberList.size(); i++) {
            String serialNumber = serialNumberList.get(i).toString();

            IData mebOpenOrderMap = (IData) Clone.deepClone(data);

            mebOpenOrderMap.put("SERIAL_NUMBER", serialNumber);
            // 1- 设置成员路由地州
            String memEparchCode = getJKDTMebEparchCodeBySn(mebOpenOrderMap);
            mebOpenOrderMap.put(Route.ROUTE_EPARCHY_CODE, memEparchCode);

            // 2- 添加用户路由(后台生成虚拟号码用)
            mebOpenOrderMap.put(Route.USER_EPARCHY_CODE, memEparchCode);

            // 3- 更改操作类型
            mebOpenOrderMap.put("ACTION", actionInfoList.get(i));

            // 4- 更改成员类型
            mebOpenOrderMap.put("USER_TYPE", userTypeInfoList.get(i));

            // 5- 更改成员群组号
            if (IDataUtil.isNotEmpty(mebGroupNumberInfoList)) {
                mebOpenOrderMap.put("RSRV_STR10", mebGroupNumberInfoList.get(i));
            }

            // 6-  更改期望生效时间
            mebOpenOrderMap.put("EFFDATE", effDateInfoList.get(i));

            // 7-  更改属性编码
            IDataset attrCodeList = new DatasetList();
            attrCodeList.add(characterIDInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR11", attrCodeList);

            // 8-  更改属性名称
            IDataset attrNameList = new DatasetList();
            attrNameList.add(characterNameInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR12", attrNameList);

            // 9-  更改属性值
            IDataset attrValueList = new DatasetList();
            attrValueList.add(characterValueInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR13", attrValueList);
            singleserialNumberList.add(mebOpenOrderMap);

            //10-放集客大厅受理标记
            mebOpenOrderMap.put("RECEPTIONHALLMEM",data.getString("RECEPTIONHALLMEM",""));
            
            //11-文件接口标记
            mebOpenOrderMap.put("RSRV_STR3",data.getString("JKDTFILE",""));
            mebOpenOrderMap.put("RSRV_STR4",data.getString("FILENAME",""));
        }
        return singleserialNumberList;
    }


    /**
     * 循环处理多个成员服务号码
     * @param data
     * @return
     * @throws Exception
     */
    public static IDataset dealserialNumberList(IData data) throws Exception
    {
        IDataset singleserialNumberList= new DatasetList();
        IDataset actionInfoList = IDataUtil.getDatasetSpecl("ACTION", data);
        IDataset userTypeInfoList = IDataUtil.getDatasetSpecl("USER_TYPE", data);
        IDataset mebGroupNumberInfoList = IDataUtil.getDatasetSpecl("RSRV_STR10", data);
        IDataset effDateInfoList = IDataUtil.getDatasetSpecl("EFFDATE", data);
        IDataset characterIDInfoList = IDataUtil.getDataset("RSRV_STR11", data);
        IDataset characterNameInfoList = IDataUtil.getDataset("RSRV_STR12", data);
        IDataset characterValueInfoList = IDataUtil.getDataset("RSRV_STR13", data);
        //  获取成员列表
        IDataset serialNumberList = IDataUtil.getDatasetSpecl(IntfField.SERIAL_NUMBER[0], data);
        if (IDataUtil.isEmpty(serialNumberList))
        {
            CSAppException.apperr(GrpException.CRM_GRP_30);
        }
        for (int i = 0; i < serialNumberList.size(); i++)
        {
            String serialNumber = serialNumberList.get(i).toString();

            IData mebOpenOrderMap = (IData) Clone.deepClone(data);

            mebOpenOrderMap.put("SERIAL_NUMBER", serialNumber);
            // 1- 设置成员路由地州
            String memEparchCode = getMebEparchCodeBySn(mebOpenOrderMap);
            mebOpenOrderMap.put(Route.ROUTE_EPARCHY_CODE, memEparchCode);

            // 2- 添加用户路由(后台生成虚拟号码用)
            mebOpenOrderMap.put(Route.USER_EPARCHY_CODE, memEparchCode);

            // 3- 更改操作类型
            mebOpenOrderMap.put("ACTION", actionInfoList.get(i));

            // 4- 更改成员类型
            mebOpenOrderMap.put("USER_TYPE", userTypeInfoList.get(i));

            // 5- 更改成员群组号
            if (IDataUtil.isNotEmpty(mebGroupNumberInfoList))
            {
                mebOpenOrderMap.put("RSRV_STR10", mebGroupNumberInfoList.get(i));
            }

            // 6-  更改期望生效时间
            mebOpenOrderMap.put("EFFDATE", effDateInfoList.get(i));

            // 7-  更改属性编码
            IDataset attrCodeList = new DatasetList();
            attrCodeList.add(characterIDInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR11", attrCodeList);

            // 8-  更改属性名称
            IDataset attrNameList = new DatasetList();
            attrNameList.add(characterNameInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR12", attrNameList);

            // 9-  更改属性值
            IDataset attrValueList = new DatasetList();
            attrValueList.add(characterValueInfoList.get(i));
            mebOpenOrderMap.put("RSRV_STR13",attrValueList);

            singleserialNumberList.add(mebOpenOrderMap);
        }
        return singleserialNumberList;
    }

    /*
     * @description 商品订单处理失败通知业务
     * @author xunyl
     * @date 2013-07-19
     */
    protected static IDataset dealBbossOrderDealFaildBiz(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 2- 调用商品订单处理失败通知接口进行处理
        dealResult = CSAppCall.call("CS.DealBBossFaildOrderSVC.modifyTradeError", data);

        // 3- 处理成功,返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 4- 返回结果
        return dealResult;
    }

    /*
     * @description BBOSS向省BOSS下发工单开通业务
     * @author xunyl
     * @date 2013-07-25
     */
    protected static IDataset dealBbossOrderOpenBiz(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

       //2-订单重复校验
        dealResult =BbossOrderVerifyBean.verifyGrpBBossOrder(data);
        if( IDataUtil.isNotEmpty(dealResult))
            return dealResult;

        // 3设置用户地州
        String ecCustNumber = data.getString("CUSTOMER_NUMBER", "");
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));

        // 4- 掉用BBOSS向省BOSS下发工单开通业务接口进行处理
        dealResult = CSAppCall.call("CS.BBossOrderOpenSVC.dealOrderOpen", data);

        // 5- 将成功的订单号保存表TF_B_POORDER
        String orderNumber=data.getString("ORDER_NO");
        BbossOrderVerifyBean.saveOrderInfo(orderNumber, "S");

        //6- 处理成功,返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 5- 返回结果
        return dealResult;
    }
    /*
     * @description BBOSS向省BOSS下发工单开通业务
     * @author xunyl
     * @date 2018-11-13
     */
    protected static IDataset dealJKDTBbossOrderOpenBiz(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

       //2-订单重复校验
        dealResult =BbossOrderVerifyBean.verifyGrpBBossOrder(data);
        if( IDataUtil.isNotEmpty(dealResult))
            return dealResult;

        // 3设置用户地州
        String ecCustNumber = data.getString("CUSTOMER_NUMBER", "");
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));

        // 4- 掉用BBOSS向省BOSS下发工单开通业务接口进行处理
        dealResult = CSAppCall.call("CS.ReceptionHallOrderOpenSVC.dealOrderOpen", data);

        // 5- 将成功的订单号保存表TF_B_POORDER
        String orderNumber=data.getString("ORDER_NO");
        BbossOrderVerifyBean.saveOrderInfo(orderNumber, "S");

        //6- 处理成功,返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 5- 返回结果
        return dealResult;
    }

    /*
     * @description 工单流转业务
     * @author xunyl
     * @date 2013-07-19
     */
    protected static IDataset dealBbossOrderStateBiz(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 2- 设置用户地州(配合省协助受理/预受理生成省内集团虚拟号)
        String ecCustNumber = data.getString("EC_SERIAL_NUMBER");
        if (StringUtils.isEmpty(ecCustNumber))
        {
            ecCustNumber = data.getString("CUSTOMER_NUMBER", "");
        }
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));

        // 2- 掉用工单流转同步接口进行处理
        dealResult = CSAppCall.call("CS.SynBbossOrderStateSVC.insetBbossOrderState", data);

        // 3- 处理成功,返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("POORDER_NUMBER", data.getString("POORDER_NUMBER", ""));
        result_data.put("STATE_TYPE", data.getString("STATE_TYPE", ""));
        IDataset productOrderNumberInfoList = IDataUtil.getDatasetSpecl("PRODUCT_ORDER_NUMBER", data);
        if(IDataUtil.isNotEmpty(productOrderNumberInfoList)){
            result_data.put("PRODUCT_ORDER_NUMBER", productOrderNumberInfoList);
        }
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSP_CODE", "00");
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
        result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
        result_data.put("PSUBSCRIBE_ID", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
        dealResult.add(result_data);

        // 4- 返回结果
        return dealResult;
    }

    /*
     * @description 拼写工单处理结果明细
     * @author xunyl
     * @date 2014-06-24
     */
    public static void dealRspInfo(IData rspResult, IDataset dealResult, String serialNumber) throws Exception
    {
        // 1- 拼写错误号码明细
        String errSerialNumber = dealResult.getData(0).getString("SERIAL_NUMBER");
        if (StringUtils.isNotBlank(errSerialNumber))
        {

            IDataset errSerialNumberList = rspResult.getDataset("SERIAL_NUMBER", new DatasetList());
            IDataset errOprNumbList = rspResult.getDataset("OPR_NUMB", new DatasetList());
            IDataset errRspCodeList = rspResult.getDataset("RSPCODE", new DatasetList());
            IDataset errRspDescList = rspResult.getDataset("RSPDESC", new DatasetList());
            if (IDataUtil.isEmpty(errSerialNumberList))
            {
                rspResult.put("SERIAL_NUMBER", errSerialNumberList);
                rspResult.put("OPR_NUMB", errOprNumbList);
                rspResult.put("RSPCODE", errRspCodeList);
                rspResult.put("RSPDESC", errRspDescList);
            }
            String errOprNumb = dealResult.getData(0).getString("OPR_NUMB", "");
            String errRspCode = dealResult.getData(0).getString("RSPCODE", "");
            String errRspDesc = dealResult.getData(0).getString("RSPDESC", "");
            errSerialNumberList.add(errSerialNumber);
            errOprNumbList.add(errOprNumb);
            errRspCodeList.add(errRspCode);
            errRspDescList.add(errRspDesc);
            rspResult.put("SERIAL_NUMBER", errSerialNumberList);
            rspResult.put("OPR_NUMB", errOprNumbList);
            rspResult.put("RSPCODE", errRspCodeList);
            rspResult.put("RSPDESC", errRspDescList);

        }

        // 2- 拼写反馈参数列表信息
        IDataset characterIds = dealResult.getData(0).getDataset("CHARACTER_ID", new DatasetList());
        if (IDataUtil.isNotEmpty(characterIds))
        {
            IDataset memberNumberList = rspResult.getDataset("MEMBER_NUMBER", new DatasetList());
            IDataset characterIdList = rspResult.getDataset("CHARACTER_ID", new DatasetList());
            IDataset characterNameList = rspResult.getDataset("CHARACTER_NAME", new DatasetList());
            IDataset characterValueList = rspResult.getDataset("CHARACTER_VALUE", new DatasetList());
            if (IDataUtil.isEmpty(memberNumberList))
            {
                rspResult.put("MEMBER_NUMBER", memberNumberList);
                rspResult.put("CHARACTER_ID", characterIdList);
                rspResult.put("CHARACTER_NAME", characterNameList);
                rspResult.put("CHARACTER_VALUE", characterValueList);
            }
            IDataset characterNames = dealResult.getData(0).getDataset("CHARACTER_NAME", new DatasetList());
            IDataset characterValues = dealResult.getData(0).getDataset("CHARACTER_VALUE", new DatasetList());
            memberNumberList.add(serialNumber);
            characterIdList.add(characterIds);
            characterNameList.add(characterNames);
            characterValueList.add(characterValues);
            rspResult.put("MEMBER_NUMBER", memberNumberList);
            rspResult.put("CHARACTER_ID", characterIdList);
            rspResult.put("CHARACTER_NAME", characterNameList);
            rspResult.put("CHARACTER_VALUE", characterValueList);
        }
    }

    /*
     * @Description 设置成员路由，网外号吗默认设置为集团所在地州
     * @author xunyl
     * @date 2014-03-22
     */
    protected static String getMebEparchCodeBySn(IData map) throws Exception
    {
        // 1- 根据成员手机号查找对应地州
        String serialNumber = map.getString("SERIAL_NUMBER", "");
        String routeEparchCode = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);

        // 2- 如果地州编号为null，则说明是网外号，设置集团所在地州
        if (StringUtils.isBlank(routeEparchCode))
        {
            // 2-1 根据产品订购关系编号查询省BOSS侧的产品订购关系
            String productOfferId = map.getString("PRODUCTID", "");
            IDataset merchpUserInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
            if (IDataUtil.isEmpty(merchpUserInfoList))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
            }
            IData merhcpUserInfo = merchpUserInfoList.getData(0);

            // 2-2 获取产品订购关系中的地州集团用户编号
            String userId = merhcpUserInfo.getString("USER_ID");

            // 2-3 根据集团用户编号获取用户资料中的地州
            IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1);
            }

            // 2-4 设置集团所在地州为成员的默认地州
            routeEparchCode = userInfo.getString("EPARCHY_CODE");
        }

        // 3- 返回成员路由
        return routeEparchCode;
    }

    protected static String getJKDTMebEparchCodeBySn(IData map) throws Exception
    {
        // 1- 根据成员手机号查找对应地州
        String serialNumber = map.getString("SERIAL_NUMBER", "");
        String routeEparchCode = RouteInfoQry.getEparchyCodeBySnForCrm(serialNumber);

        // 2- 如果地州编号为null，则说明是网外号，设置集团所在地州
        if (StringUtils.isBlank(routeEparchCode))
        {
            // 2-1 根据产品订购关系编号查询省BOSS侧的产品订购关系
            String productOfferId = map.getString("PRODUCTID", "");
            IDataset merchpUserInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);
            if (IDataUtil.isEmpty(merchpUserInfoList))
            {
                CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[0], IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
            }
            IData merhcpUserInfo = merchpUserInfoList.getData(0);

            // 2-2 获取产品订购关系中的地州集团用户编号
            String userId = merhcpUserInfo.getString("USER_ID");

            // 2-3 根据集团用户编号获取用户资料中的地州
            IData userInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(userId, "0");
            if (IDataUtil.isEmpty(userInfo))
            {
                CSAppException.apperr(CrmUserException.CRM_USER_1);
            }

            // 2-4 设置集团所在地州为成员的默认地州
            routeEparchCode = userInfo.getString("EPARCHY_CODE");
        }

        // 3- 返回成员路由
        return routeEparchCode;
    }

    /*
     * @description 初始化成员签约工单反馈信息
     * @author xunyl
     * @date 2014-06-21
     */
    private static IData initRspWebWorkOrderResult(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData initRspInfo = new DataMap();

        // 2- 初始化返回结果
        initRspInfo.put("SUBSCRIBE_ID", data.getString("ORDER_NO", ""));
        initRspInfo.put("PKGSEQ", data.getString("PKGSEQ", ""));
        initRspInfo.put("PROVINCE_CODE", "HNAN");
        initRspInfo.put("IN_MODE_CODE", "0");
        initRspInfo.put("TRADE_EPARCHY_CODE", CSBizBean.getTradeEparchyCode());
        initRspInfo.put("TRADE_CITY_CODE", getVisit().getCityCode());
        initRspInfo.put("TRADE_DEPART_ID", getVisit().getDepartId());
        initRspInfo.put("TRADE_STAFF_ID", getVisit().getStaffId());
        initRspInfo.put("TRADE_DEPART_PASSWD", ""); // 渠道接入密码此密码由BOSS制定，接入渠道必填
        initRspInfo.put(Route.ROUTE_EPARCHY_CODE, CSBizBean.getTradeEparchyCode());
        initRspInfo.put("ROUTETYPE", "00");
        initRspInfo.put("ROUTEVALUE", "000");
        initRspInfo.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        initRspInfo.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        initRspInfo.put("RSPCODE", "00");
        initRspInfo.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);

        // 3- 返回初始化信息
        return initRspInfo;
    }

    /**
     * @description 判断集团用户是否已经开户
     * @author xunyl
     * @date 2013-06-21
     */
    public static boolean isUserExistBossGrp(String merchOfferId) throws Exception
    {
        // 1- 根据商品订购关系ID查询用户订购商品信息
        IData merchs = new DataMap();
        IDataset result = UserGrpMerchInfoQry.qryMerchInfoByMerchOfferId(merchOfferId);
        if (result == null || result.size() == 0)
        {
            return false;
        }
        else
        {
            merchs = result.getData(0);
        }

        // 2- 根据用户编号获取用户信息
        String userId = merchs.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (userInfo == null || userInfo.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    /*
     * @description 商品操作类型转换为本省操作类型
     * @author xunyl
     * @2013-06-21
     */
    protected static IDataset merchOpTypeToProOpType(String merchOperType, IData data) throws Exception
    {
        // 1- 定义返回数据
        IDataset dealResult = new DatasetList();

        // 2- 查询用户的存在状态

        String merchOfferId = data.getString("RSRV_STR2", "");// 商品订购关系ID
        boolean isUserExist = isUserExistBossGrp(merchOfferId);

        // 3- 设置反向标记
        data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口

        // 4- 添加用户路由(后台生成虚拟号码用)
        String ecCustNumber = data.getString("EC_SERIAL_NUMBER", "");
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));

        // 5- 根据商品操作编号和用户的存在状态分别调用不同的服务进行处理
        if (!isUserExist && "1".equals(merchOperType))// 集团产品受理
        {
            dealResult = CSAppCall.call("CS.CreateBBossUserSVC.crtOrder", data);
        }
        else if (isUserExist && "2".equals(merchOperType))// 集团产品注销
        {
            dealResult = CSAppCall.call("CS.DestroyBBossUserSVC.dealDelBBossBiz", data);
        }
        else if (isUserExist)// 集团产品变更
        {
            dealResult = CSAppCall.call("CS.ChangeBBossUserSVC.crtOrder", data);
        }
        else if (!isUserExist && "20".equals(merchOperType))
        {// 针对两级界面方式在预受理阶段做处理时预留

        }
        else if ((isUserExist && "1".equals(merchOperType)) || (!isUserExist && "2".equals(merchOperType)))
        {// 无须调用服务处理的情况，直接返回处理成功

        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_735);
        }

        // 6- 处理成功或者无须处理的情况皆返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
        result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 7- 返回处理结果
        return dealResult;
    }

    /*
     * @description 修正一级BOSS传递的商品操作类型
     * @author xunyl
     * @date 2013-06-20
     */
    protected static void modifyOperType(String merchOperType, IData data) throws Exception
    {

        // 操作码为"13"业务开展省新增或删除操作(目前本地企业飞信商品有该种案例)
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PROV.getValue().equals(merchOperType))
        {
            operTypeDealSpec(merchOperType, data);
        }

        // 操作码为"6"变更成员特殊处理
        if (GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_MEB.getValue().equals(merchOperType))
        {
            merchOperType = GroupBaseConst.MERCH_STATUS.MERCH_MODIFY_PARAM.getValue();
            String prodOperCodeStr = data.getString("RSRV_STR14");
            prodOperCodeStr = prodOperCodeStr.replaceAll(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_MEB.getValue(), GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_MODIFY_PARAM.getValue());
            data.put("RSRV_STR14", new DatasetList(prodOperCodeStr));
        }

        // 针对两级界面方式在预受理阶段做处理时预留
        if (GroupBaseConst.MERCH_STATUS.MERCH_CAMP_ON.getValue().equals(merchOperType))
        {
//            data.put("RSRV_STR14", new DatasetList(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CAMP_ON.getValue()));
        }

    }

    /**
     * @desctiption "13"业务开展省新增或删除操作特殊处理： 当前省为业务主办省，保持"13"操作 当前省为业务开展省新增省，商品和产品操作码都改为新增"1"
     *              当前省为业务开展省被删除省，商品和产品操作码都改为取消"2" 当前省为业务开展省原有配合省，不涉及新增或删除操作，保持"13"操作
     * @author xunyl
     * @date 2013-06-20
     */
    private static void operTypeDealSpec(String merchOperType, IData data) throws Exception
    {
        // 获取业务办理省得省代码

        String curProv = TagInfoQry.getSysTagInfo("PUB_INF_PROVINCE", "TAG_SEQUID", ProvinceUtil.getProvinceCodeGrpCorp(), CSBizBean.getTradeEparchyCode());

        // 获取报文中主办省的省代码
        String bizProv = (String) IDataUtil.getDataset("PROVINCE", data, false).get(0);

        // 当前省为业务主办省，保持"13"操作
        if (curProv.equals(bizProv))
        {
            return;
        }

        // 根据商品编号获取该商品下对应的业务开展省属性编码

        String poNumber = data.getString("RSRV_STR1", "");
        IDataset staticValue = StaticUtil.getList(null, "TD_S_STATIC", "DATA_ID", "PDATA_ID", new String[]
        { "TYPE_ID", "DATA_ID" }, new String[]
        { "BBOSS_BIZ_OPEN_PROVINCE", poNumber });
        if (staticValue == null || staticValue.size() <= 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_897, poNumber);
        }
        String openProAttrCode = staticValue.getData(0).getString("PDATA_ID");

        // 根据业务开展省状态判断本省属于新增业务开展省或者被删除的业务开展省，修改商品操作类型

        IDataset proAttrCodeList = IDataUtil.getDataset("RSRV_STR15", data);// 产品属性代码

        IDataset attrValueList = IDataUtil.getDataset("RSRV_STR16", data);// 产品属性值

        IDataset attrActionList = IDataUtil.getDataset("RSRV_STR18", data);// 产品属性操作代码

        IDataset proAttrCodeListElement = IDataUtil.modiIDataset(proAttrCodeList.get(0), "RSRV_STR15");
        IDataset attrValueListElement = IDataUtil.modiIDataset(attrValueList.get(0), "RSRV_STR16");
        IDataset attrCodeListElement = IDataUtil.modiIDataset(attrActionList.get(0), "RSRV_STR18");
        for (int i = 0; i < proAttrCodeListElement.size(); ++i)
        {
            if (openProAttrCode.equals(proAttrCodeListElement.get(i).toString()) && curProv.equals(attrValueListElement.get(i).toString()))
            {
                String attrAction = attrCodeListElement.get(i).toString();
                if ("0".equals(attrAction))// 说明本省属于业务删除省

                {
                    data.put("RSRV_STR14", new DatasetList(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_ADD.getValue()));
                    merchOperType = GroupBaseConst.MERCH_STATUS.MERCH_ADD.getValue();
                }
                else if ("1".equals(attrAction))
                {// 说明本省属于业务新增省

                    data.put("RSRV_STR14", new DatasetList(GroupBaseConst.MERCH_PRODUCT_STATUS.PRODUCT_CANCLE.getValue()));
                    merchOperType = GroupBaseConst.MERCH_STATUS.MERCH_CANCLE.getValue();
                }
                return;
            }
        }
    }

    /*
     * @description 根据集团编号查询集团所在地州(IBOSS反向数据没有USER_EPARCH_CODE)
     * @author xunyl
     * @date 2013-11-23
     */
    protected static String qryEparchCodeByEcCode(String mpGroupCustCode) throws Exception
    {
        // 1- 根据EC客户编号获取集团客户信息
        IDataset groupInf = GrpInfoQry.queryCustGroupInfoByMpCustCode(mpGroupCustCode, null);

        // 2- 集团客户信息不存在，抛出异常
        if (groupInf == null || groupInf.size() == 0)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_899, mpGroupCustCode);
        }

        // 3- 返回集团客户所在地州
        String eparchCode = groupInf.getData(0).getString("EPARCHY_CODE");
        return eparchCode;
    }

    /*
     * @description BBOSS向省BOSS下发管理节点报文
     * @author chenyi
     * @date 2013-07-25
     */
    protected static IDataset synBBossGrpMgrBiz(IData data) throws Exception
    {
        // 1- 重置工号,防止aee过来的数据没有产品权限
        getVisit().setStaffId("IBOSS000");

        // 2- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 3- BBOSS向省BOSS下发管理节点报文
        String ecCustNumber = data.getString("EC_SERIAL_NUMBER", "");
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));
        dealResult = CSAppCall.call("CS.SynBBossGrpMgrBizSVC.dealCommonData", data);

        // 4- 处理成功或者无须处理的情况皆返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
        result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
        result_data.put("PRODUCT_ORDERNUMBER", IDataUtil.getDatasetSpecl("PSUBSCRIBE_ID", data)); // 产品订单号
        IDataset temp = new DatasetList();
        temp.add("00");
        result_data.put("PRODUCT_ORDER_RSP_CODE", temp);
        IDataset temp1 = new DatasetList();
        temp1.add(IntfField.SUUCESS_CODE[1]);
        result_data.put("PRODUCT_ORDER_RSP_DESC", temp1);
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 5- 返回结果
        return dealResult;
    }

    /*
     * @description 商产品规格同步接口
     * @author xunyl
     * @date 2013-09-27
     */
    protected static IDataset synBBossPoInfo(IData data) throws Exception
    {
        // 1- 定义返回结果
        IDataset dealResult = new DatasetList();

        // 2- BBOSS向省BOSS下发管理节点报文
        dealResult = CSAppCall.call("CS.SynBBossPoSpecSVC.processPosSyncInfo", data);

        // 3- 返回结果
        return dealResult;
    }

    /*
     * @description 分解成员签约关系工单(接口层只接受单个成员处理)
     * @author xunyl
     * @date 2014-06-20
     */
    private static IData synMebWorkOrder(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData rspResult;

        // 2- 判断是否需要CRM侧做工单反馈，RETURN_FLAG_KT为非空时代表文件接口，此时工单反馈由IBOSS完成
        String returnFlag = data.getString("RETURN_FLAG_KT", "");

        //3- 分别调用文件接口和实时接口的处理方法
        if (!"".equals(returnFlag)){
            rspResult = dealFileMebWorkOrder(data);
        }else{
            rspResult = dealActualMebWorkOrder(data);
        }

        //4- 返回应答结果
        return rspResult;
    }

    private static IData synJKDTMebWorkOrder(IData data) throws Exception
    {
        // 1- 定义返回结果
        IData rspResult;

        // 2- RETURN_FLAG_KT为非空时代表文件接口
        String returnFlag = data.getString("RETURN_FLAG_KT", "");

        //3- 分别调用文件接口和实时接口的处理方法
        if (!"".equals(returnFlag)){
            rspResult = dealJKDTFileMebWorkOrder(data);
        }else{
            rspResult = dealJKDTActualMebWorkOrder(data);
        }

        //4- 返回应答结果
        return rspResult;
    }

    /**
     * @descripiton 处理文件接口的成员签约工单
     * @author xunyl
     * @date 2015-08-10
     */
    private static IData dealFileMebWorkOrder(IData data)throws Exception{
        // 1- 定义返回结果
        IData rspResult = new DataMap();

        // 2- 初始化返回结果
        rspResult = initRspWebWorkOrderResult(data);

        // 3- 循环处理多个成员服务号码
        IDataset singleSerialNumberList = dealserialNumberList(data);
        groupRealInfoSynForMosp(data);//和多号，中间号同步信息      
        if(IDataUtil.isNotEmpty(singleSerialNumberList))
        {
            for (int i = 0; i < singleSerialNumberList.size(); i++)
            {
                IData mebWordOrderMap = singleSerialNumberList.getData(i);
                IDataset dealResult = CSAppCall.call("CS.SynMebWordOrderSVC.dealMebWordOrder", mebWordOrderMap);
                String serialNumber = mebWordOrderMap.getString("SERIAL_NUMBER");
                // 拼写工单处理结果
                dealRspInfo(rspResult, dealResult, serialNumber);
            }
        }

        // 4- 拼写错误信息
        if(IDataUtil.isNotEmpty(rspResult.getDataset("RSPCODE"))){
            rspResult.put("X_RESULTCODE", rspResult.getDataset("RSPCODE").get(0).toString());
            rspResult.put("X_RESULTINFO", rspResult.getDataset("RSPDESC").get(0).toString());
        }

        // 5- 保存处理成功工单记录
        BbossOrderVerifyBean.saveOrderInfo(data.getString("ORDER_NO", ""), "S");

        // 6- 返回结果
        return rspResult;
    }

    /**
     * @description 拼写实时接口的成员签约工单
     * @author xunyl
     * @date 2015-08-10
     */
    private static IData dealActualMebWorkOrder(IData data)throws Exception{
        // 1- 定义返回结果
        IData rspResult = new DataMap();

        // 2- 多线程处理成员签约工单（实现应答与请求异步，方便处理需要等待的单子，例如叠加包开通与成员签约开通同时落地的场景）
        ActualMebWorkOrderThread pro = new ActualMebWorkOrderThread(data, (BizVisit)CSBizBean.getVisit(),SessionManager.getInstance().peek()) {};
        pro.start();

        // 3- 初始化返回结果
        rspResult = initRspWebWorkOrderResult(data);

        // 4- 返回结果
        return rspResult;
    }

    private static IData dealJKDTActualMebWorkOrder(IData data)throws Exception{
        // 1- 定义返回结果
        IData rspResult;

        // 2- 多线程处理成员签约工单（实现应答与请求异步，方便处理需要等待的单子，例如叠加包开通与成员签约开通同时落地的场景）
        JKDTActualMeb pro = new JKDTActualMeb();
        pro.mebWorkRun(data);

        // 3- 初始化返回结果
        rspResult = initRspWebWorkOrderResult(data);

        // 4- 返回结果
        return rspResult;
    }

    /**
     * @Function:
     * @Description: 对于集团下发的BIP4B257 T4101035 报文 直接返回成功
     * @param：
     * @return：
     * @throws：
     * @version:
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    public static IDataset dealBbossMebOrderOpenBiz(IData data) throws Exception
    {
        IDataset dealResult = new DatasetList();

        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE", "00");
        result_data.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        dealResult.add(result_data);

        String memSerialNumber = data.getString("SERIAL_NUMBER", "");
        if ("".equals(memSerialNumber))
        {

            return dealResult;
        }
        IData map = new DataMap();

        map.put("SERIAL_NUMBER", memSerialNumber);

        IDataset tradeset = TradeGrpMerchMebInfoQry.getMerchpMebTradeInfo(memSerialNumber, null, null);
        if (IDataUtil.isNotEmpty(tradeset))
        {
            String order_id = tradeset.getData(0).getString("ORDER_ID");

            dealErrorTrade(order_id);
        }

        return dealResult;
    }

    /**
     * @Function:
     * @Description: 对于集团下发的报文 直接返回成功
     * @date: 2018-12-4
     */
    public static IDataset dealJKDTMemberOrderOpenBiz(IData data) throws Exception
    {
        IDataset dealResult = new DatasetList();

        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("RSPCODE", "00");
        result_data.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        dealResult.add(result_data);

        String memSerialNumber = data.getString("SERIAL_NUMBER", "");
        if ("".equals(memSerialNumber))
        {

            return dealResult;
        }
        IData map = new DataMap();

        map.put("SERIAL_NUMBER", memSerialNumber);

        IDataset tradeset = TradeReceptionHallMebInfoQry.getMerchpMebTradeInfo(memSerialNumber, null, null);
        if (IDataUtil.isNotEmpty(tradeset))
        {
            String order_id = tradeset.getData(0).getString("ORDER_ID");

            dealErrorTrade(order_id);
        }

        return dealResult;
    }
    /**
     * @Function:
     * @Description:搬迁在读错单
     * @author:chenyi
     * @date: 下午3:32:50 2013-10-23
     */
    private static void dealErrorTrade(String orderId) throws Exception
    {

        // 1-处理trade表
        IDataset tradeInfoDataset = TradeInfoQry.queryTradeByOrerId(orderId, "0");
        for (int i = 0, sizeI = tradeInfoDataset.size(); i < sizeI; i++)
        {
            IData tradeInfo = tradeInfoDataset.getData(i);
            Dao.delete("TF_B_TRADE", tradeInfo,Route.getJourDb(BizRoute.getTradeEparchyCode()));

            tradeInfo.put("REMARK", "总部确认失败");
            tradeInfo.put("SUBSCRIBE_STATE", "A");
            Dao.insert("TF_BH_TRADE", tradeInfo,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        }
        // 2处理order表
        IData orderInfo = TradeInfoQry.getOrderByOrderId(orderId);
        Dao.insert("TF_BH_ORDER", orderInfo,Route.getJourDb(BizRoute.getTradeEparchyCode()));
        Dao.delete("TF_B_ORDER", orderInfo,Route.getJourDb(BizRoute.getTradeEparchyCode()));
    }
    /**
     /**
     *
     * 集团副号码信息同步（中间号，和多号发起方）
     * @param data
     * @throws Exception
     */
    public static void groupRealInfoSynForMosp(IData data) throws Exception {
        IData param = new DataMap();
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", data);
        String productOfferId = (String) productIdList.get(0);
        IDataset UserGrpmerchpInfoList = UserGrpMerchpInfoQry.qryMerchpInfoByProductOfferId(productOfferId);
        if (IDataUtil.isEmpty(UserGrpmerchpInfoList)) {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData userGrpMerchpInfo = UserGrpmerchpInfoList.getData(0);
        String productSpecCode = userGrpMerchpInfo.getString("PRODUCT_SPEC_CODE", "");
        if ("9101002".equals(productSpecCode) || "9101003".equals(productSpecCode)) {//中间号或企业和多号
            IDataset characterIDInfoList = IDataUtil.getDataset("RSRV_STR11", data);
            IDataset characterNameInfoList = IDataUtil.getDataset("RSRV_STR12", data);
            IDataset characterValueInfoList = IDataUtil.getDataset("RSRV_STR13", data);
            String groupUserId = userGrpMerchpInfo.getString("USER_ID", "");
            IData groupUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(groupUserId, "0");
            String groupCustId = groupUserInfo.getString("CUST_ID", "");
            param.put("GROUP_CUST_ID", groupCustId);
            param.put("GROUP_USER_ID", groupUserId);
            IDataset serialNumberList = IDataUtil.getDatasetSpecl(IntfField.SERIAL_NUMBER[0], data);//成员列表
            if (IDataUtil.isEmpty(serialNumberList)) {
                CSAppException.apperr(GrpException.CRM_GRP_30);
            }
            IDataset numberList = new DatasetList();
            for (int i = 0; i < serialNumberList.size(); i++) {
                IData numbers = new DataMap();
                IDataset actionInfoList = IDataUtil.getDatasetSpecl("ACTION", data);
                String action = actionInfoList.get(i).toString();
                IDataset attrIds = characterIDInfoList.getDataset(i);
                IDataset attrValues = characterValueInfoList.getDataset(i);
                String followNumProv = "";
                for (int j = 0; j < attrIds.size(); j++) {
                    if ("1002".equals(attrIds.get(j).toString()) || "1011".equals(attrIds.get(j).toString())) {
                        followNumProv = attrValues.get(j).toString();//副号码归属省份编码
                    }
                }
                if ("1".equals(action)) {//增加
                    numbers.put("SERIAL_NUMBER", serialNumberList.get(i).toString());
                    numbers.put("FOLLOW_PROV", followNumProv);
                    numbers.put("OPR_CODE", "01");
                    numberList.add(numbers);
                } else if ("0".equals(action)) {//或删除
                    numbers.put("SERIAL_NUMBER", serialNumberList.get(i).toString());
                    numbers.put("FOLLOW_PROV", followNumProv);
                    numbers.put("OPR_CODE", "02");
                    numberList.add(numbers);
                }
            }
            param.put("NUMBER_LIST", numberList);
            MiddleNumberInfoSynSVC.groupFollowMsisdnSyn(param);
        }
    }

    public static IDataset synchGrpProdGprsRoam(IData data) throws Exception{
    	IDataset returnInfos = new DatasetList();
    	data.put("RESULT_CODE", IntfField.SUUCESS_CODE[0]);
    	data.put("RESULT_INFO", IntfField.SUUCESS_CODE[1]);
    	IDataset uds = data.getDataset("UD1", new DatasetList());
    	if(IDataUtil.isEmpty(uds)){
    		returnInfos.add(data);
        	return returnInfos;
    	}
    	for(int i=0,size=uds.size();i<size;i++){
    		IData ud = uds.getData(i);
    		//00 - 业务办理成功 01 - 业务办理失败
    		String productOrderResult = ud.getString("PRODUCT_ORDER_RESULT","01");
    		if(!StringUtils.equals("00", productOrderResult)){
    			continue;
    		}
    		//用户订购关系同步信息(PRODUCT_ORDER_RESULT取值为00时需填写)
    		IDataset syncOrderRelationInfos = ud.getDataset("SYNC_ORDER_RELATION_INFO");
    		if(IDataUtil.isEmpty(syncOrderRelationInfos)){
    			continue;
    		}
    		IData syncOrderRelationInfo = syncOrderRelationInfos.getData(0);
    		//用户类型(00 - 个人用户,01 - 集团用户)
    		String userType = syncOrderRelationInfo.getString("USER_TYPE");
    		if(!"01".equals(userType)){
    			ud.put("RESULT_CODE", "1");
    			ud.put("RESULT_INFO", "只能处理用户类型USER_TYPE为01(集团用户)的用户!");
    			continue;
    		}
    		//产品类型(00 - 一次性产品 01 - 功能性产品)
    		String prodType = syncOrderRelationInfo.getString("PROD_TYPE");
        	if(!"00".equals(prodType)){
        		ud.put("RESULT_CODE", "1");
        		ud.put("RESULT_INFO", "只能处理产品类型PROD_TYPE为00(一次性产品)");
        		continue;
            }
        	//用户号码
        	String serialNumber = syncOrderRelationInfo.getString("SERIAL_NUMBER");
        	String routeId = RouteInfoQry.getEparchyCodeBySn(serialNumber);
            IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber,routeId);
            if(IDataUtil.isEmpty(userInfo)){
            	ud.put("RESULT_CODE", "1");
            	ud.put("RESULT_INFO", "用户号码["+serialNumber+"]未查到用户信息。");
            	continue;
            }
            String userId = userInfo.getString("USER_ID");
            //用户订购产品会生成一个的唯一订购流水号
        	String prodInstId = syncOrderRelationInfo.getString("PROD_INST_ID");
        	//查询用户资费
        	IDataset userNormalDiscntInfos =UserDiscntInfoQry.queryDiscntInfosByInstId(userId, prodInstId, routeId);;
            if(IDataUtil.isEmpty(userNormalDiscntInfos)){
    			ud.put("RESULT_CODE", "1");
    			ud.put("RESULT_INFO", "受理失败，叠加包资费信息未生成!");
    			continue;
    		}
        	//产品状态(00 - 未激活，未过期,01 - 未激活，已过期,02 - 已激活，正在使用,03 - 已激活，使用完毕,04 - 已退订,05 - 已销户退订)
        	String prodStat = syncOrderRelationInfo.getString("PROD_STAT");
        	//格式化日期
            DateFormat dateFormat = new SimpleDateFormat(SysDateMgr.PATTERN_STAND);
            //订购过期时间(用户必须在订购过期时间前使用)
            String expireDateStr = TimeUtil.encodeTimestamp(syncOrderRelationInfo.getString("EXPIRE_TIMSI")).toString();
            //产品失效时间
            String endDateStr = TimeUtil.encodeTimestamp(syncOrderRelationInfo.getString("END_TIMSI")).toString();
    		String endDate = dateFormat.format(dateFormat.parse(expireDateStr));
            if("02".equals(prodStat)||"03".equals(prodStat)){
            	endDate = dateFormat.format(dateFormat.parse(endDateStr));
            }
            //订购关系状态(01- 新增(为01时，省boss需要进行根据计费金额字段进行扣费处理)；02 - 修改(为02时，省boss则不需要扣费处理))
        	String orderRelationStat = syncOrderRelationInfo.getString("ORDER_RELATION_STAT");
        	if("01".equals(orderRelationStat)){
        		ud.put("RESULT_CODE", IntfField.SUUCESS_CODE[0]);
    			ud.put("RESULT_INFO", IntfField.SUUCESS_CODE[1]);
        		continue;
        	}

        	if(log.isDebugEnabled()){
        		log.debug("GrpIntf synchGrpProdGprsRoam ud"+i+"="+ud);
        		log.debug("GrpIntf synchGrpProdGprsRoam userNormalDiscntInfos"+i+"="+userNormalDiscntInfos);
        		log.debug("GrpIntf synchGrpProdGprsRoam endDate"+i+"="+endDate);
        	}

        	if("02".equals(orderRelationStat)){
        		IDataset userMebCenPayInfos = UserMebCenPayQry.queryMebCenpayInfosByInstId(userId, prodInstId, routeId);
            	if(IDataUtil.isNotEmpty(userMebCenPayInfos)){
            		//产品订购关系
            		syncOrderRelationInfo.put("PRODUCTID", userMebCenPayInfos.getData(0).getString("PRODUCT_OFFER_ID", ""));
            	}
            	IData userNormalDiscntInfo = userNormalDiscntInfos.getData(0);
            	//变更截止时间
            	userNormalDiscntInfo.put("END_DATE", endDate);
        		IDataset results = chgBbossPayBiz(syncOrderRelationInfo, userNormalDiscntInfo);

        		if(log.isDebugEnabled()){
            		log.debug("GrpIntf synchGrpProdGprsRoam results"+i+"="+results);
        		}

        		ud.put("RESULT_CODE", IntfField.SUUCESS_CODE[0]);
        		ud.put("RESULT_INFO", results.toString());
        		continue;
        	}
        	ud.put("RESULT_CODE", "1");
    		ud.put("RESULT_INFO", "不能处理的订购关系状态ORDER_RELATION_STAT["+orderRelationStat+"]");
    		continue;
    	}
    	returnInfos.add(data);
    	return returnInfos;
    }

    //反向激活国际流量统付流量叠加包
    private static IDataset chgBbossPayBiz(IData data, IData discntInfo) throws Exception{
    	if(log.isDebugEnabled()){
    		log.debug("GrpIntf chgBbossPayBiz IN data="+data);
    		log.debug("GrpIntf chgBbossPayBiz IN discntInfo="+discntInfo);
    	}

    	IData param = (IData) Clone.deepClone(data);

        //获取成员号码地州，网外号默认设置为
        String memEparchCode = getMebEparchCodeBySn(param);
        //添加用户路由(后台生成虚拟号码用)
        param.put(Route.USER_EPARCHY_CODE, memEparchCode);
        //设置成员路由
        param.put(Route.ROUTE_EPARCHY_CODE, memEparchCode);
        //设置员工交易地州
        param.put("TRADE_EPARCHY_CODE", memEparchCode);
        //设置反向标记(1-代表是反向接口)
        param.put(IntfField.ANTI_INTF_FLAG[0], "1");
        //流量叠加包标识
        param.put("PayBiz", true);
        //集团用户ID
        param.put("USER_ID", discntInfo.getString("USER_ID_A"));

        IDataset elementInfos = new DatasetList();
        discntInfo.put("ELEMENT_TYPE_CODE", "D");
        discntInfo.put("ELEMENT_ID", discntInfo.getString("DISCNT_CODE"));
        discntInfo.put("MODIFY_TAG", "2");//修改
        elementInfos.add(discntInfo);

        param.put("ELEMENT_INFO", elementInfos);

        if(log.isDebugEnabled()){
    		log.debug("GrpIntf chgBbossPayBiz IN param="+param);
    	}

        IDataset dealResultInfoList = CSAppCall.call("CS.BbossPayBizSVC.chgBbossPayBiz", param);

        if(log.isDebugEnabled()){
    		log.debug("GrpIntf chgBbossPayBiz OUT dealResultInfoList="+dealResultInfoList);
    	}
        return dealResultInfoList;
    }

    /*
     * @description 集客大厅-反向类型为集团业务
     * @author xunyl
     * @date 2013-07-10
     */
    protected static IDataset dealJKDTGroupBiz(IData data) throws Exception
    {   
    	GrpCommonBean.repalceProSpecCode(data);//daidl 替换报文中的全网产品编码
        // 1- 定义返回结果
        IDataset dealResult;

        // 2- 获取一级BOSS传递的商品操作类型
        String merchOperType = IDataUtil.getDataset("OPERA_TYPE", data, false).get(0).toString();

        // 3- 修正一级BOSS传递的商品操作类型(典型场景:一级BOSS里面的商品操作类型为13，即业务开展省新增或者删除，对于落地省来说意味着3种情况，第一种情
        // 况为本省是新增的业务开展省，需要进行商品新增，第二种是本省是被删除的业务开展省，需要进行商品注销，第三种是本省是业务主办省，无须进行任何操作)
        modifyOperType(merchOperType, data);

        //4-校验是否为重复归档
        dealResult=BbossOrderVerifyBean.verifyJKDTGrpRsp(data);
        if(IDataUtil.isNotEmpty(dealResult)){
            return dealResult;
        }

        // 5- 将修正后的商品操作类型调用不同的服务处理类
        dealResult = merchJKDTOpTypeToProOpType(merchOperType, data);

        // 6- 返回结果
        return dealResult;
    }

    /*
     * @description 集客大厅-商品操作类型转换为本省操作类型
     * @author xunyl
     * @2013-06-21
     */
    protected static IDataset merchJKDTOpTypeToProOpType(String merchOperType, IData data) throws Exception
    {
        // 1- 定义返回数据
        IDataset dealResult = new DatasetList();

        // 2- 查询用户的存在状态

        String merchOfferId = data.getString("RSRV_STR2", "");// 商品订购关系ID
        boolean isUserExist = isUserExistJKDTBossGrp(merchOfferId);
  		String poSpecNumber = data.getString("RSRV_STR1", "");// 商品规格编码
 	 	if("50011".equals(poSpecNumber)&& "1".equals(merchOperType)&&isUserExist){
 	 	     merchOperType = "7";
 	 	     data.put("OPERA_TYPE","7");
 	 	}
        // 3- 设置反向标记
        data.put(IntfField.ANTI_INTF_FLAG[0], "1");// 反向标记 1代表是反向接口

        // 4- 添加用户路由(后台生成虚拟号码用)
        String ecCustNumber = data.getString("EC_SERIAL_NUMBER", "");
        data.put(Route.USER_EPARCHY_CODE, qryEparchCodeByEcCode(ecCustNumber));

        // 5- 根据商品操作编号和用户的存在状态分别调用不同的服务进行处理
        if (!isUserExist && "1".equals(merchOperType))// 集团产品受理
        {
            dealResult = CSAppCall.call("CS.CreateJKDTUserSVC.crtOrder", data);
        }
        else if (isUserExist && "2".equals(merchOperType))// 集团产品注销
        {
            dealResult = CSAppCall.call("CS.DestroyJKDTUserSVC.dealDelBBossBiz", data);
        }
        else if (isUserExist)// 集团产品变更
        {
            dealResult = CSAppCall.call("CS.ChangeJKDTUserSVC.crtOrder", data);
        }
        else if (!isUserExist && "20".equals(merchOperType))
        {// 针对两级界面方式在预受理阶段做处理时预留

        }
        else if ((isUserExist && "1".equals(merchOperType)) || (!isUserExist && "2".equals(merchOperType)))
        {// 无须调用服务处理的情况，直接返回处理成功

        }
        else
        {
            CSAppException.apperr(CrmUserException.CRM_USER_735);
        }

        // 6- 处理成功或者无须处理的情况皆返回成功状态
        dealResult.clear();
        IData result_data = new DataMap();
        result_data.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
        result_data.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
        result_data.put("TRADE_ID", "-1");
        result_data.put("OPR_TIME", SysDateMgr.getSysDate("yyyyMMddhhmmss"));
        result_data.put("EC_SERIAL_NUMBER", data.getString("EC_SERIAL_NUMBER", ""));
        result_data.put("SUBSCRIBE_ID", data.getString("SUBSCRIBE_ID", ""));
        result_data.put("RSPCODE".toUpperCase(), "00");
        result_data.put("RSP_DESC".toUpperCase(), IntfField.SUUCESS_CODE[1]);
        dealResult.add(result_data);

        // 7- 返回处理结果
        return dealResult;
    }

    //集客大厅
    public static boolean isUserExistJKDTBossGrp(String merchOfferId) throws Exception
    {
        // 1- 根据商品订购关系ID查询用户订购商品信息
        IData merchs = new DataMap();
        IDataset result = UserEcrecepOfferfoQry.qryJKDTMerchInfoByMerchOfferId(merchOfferId);
        if (result == null || result.size() == 0)
        {
            return false;
        }
        else
        {
            merchs = result.getData(0);
        }

        // 2- 根据用户编号获取用户信息
        String userId = merchs.getString("USER_ID");
        IData userInfo = UcaInfoQry.qryUserMainProdInfoByUserIdForGrp(userId);
        if (userInfo == null || userInfo.size() == 0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    /**
     * 校验成员状态
     * @param memberInfo
     * @return
     * @throws Exception
     */
    public static IData checkMemberStatus(IData memberInfo) throws Exception{
    	IData resultMap = new DataMap();
    	// 1- 集团用户信息不存在，说明本省非用户开展，工单无法开通
		IData inparams = new DataMap();
		String offerId = memberInfo.getString("PRODUCTID", "");
		String memSerialNumber = memberInfo.getString("SERIAL_NUMBER","");
		inparams.put("PRODUCT_OFFER_ID", offerId);
//		String receptionHallMem = memberInfo.getString("RECEPTIONHALLMEM");//集客大厅受理标记 如果为集客大厅受理则查集客大厅表
        IDataset userProductInfoList;
//        if (StringUtils.isNotBlank(receptionHallMem)) {
        userProductInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(offerId);
//        } else {
//            userProductInfoList = CSAppCall.call("CS.UserGrpMerchpInfoQrySVC.qryMerchpInfoByProductOfferId",inparams);
//        }
        if (IDataUtil.isEmpty(userProductInfoList))
        {
        	resultMap.put("SERIAL_NUMBER", memSerialNumber);
        	resultMap.put("RSP_CODE", "99");
        	resultMap.put("RSP_DESC", "成员归属省不在业务开展省内，无法同步成员签约关系");                                
            resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);                
            return resultMap;
        }
        
        // 2- 成员用户信息不存在，工单无法开通
        inparams.clear();
        inparams.put("SERIAL_NUMBER", memSerialNumber);
        inparams.put("REMOVE_TAG", "0");
 //daidl      IDataset memberUserInfoList = CSAppCall.call("CS.UserInfoQrySVC.getUserInfoBySnNoProduct",inparams);
        IDataset memberUserInfoList = UserInfoQry.getUserInfoBySn(memSerialNumber, "0");
        if (IDataUtil.isEmpty(memberUserInfoList))
        {
        	resultMap.put("SERIAL_NUMBER", memSerialNumber);
        	resultMap.put("RSP_CODE", "13");
        	resultMap.put("RSP_DESC", "用户状态不正常");                                
            resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);                
            return resultMap;
        }
        
        IData memUserInfo = memberUserInfoList.getData(0);
        String user_state_codeset=memUserInfo.getString("USER_STATE_CODESET");
        if (!"0".equals(user_state_codeset))
        {
            resultMap.put("SERIAL_NUMBER", memSerialNumber);
            resultMap.put("RSP_CODE", "13");
            resultMap.put("RSP_DESC", "用户状态不正常");
            resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
            resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
            return resultMap;
        }

     // 3- 如果该当业务为集团一点支付，成员帐户为预付费帐户，工单无法开通
        //daidl
        IData userProductInfo = userProductInfoList.getData(0);
        String productSpecCode = userProductInfo.getString("PRODUCT_SPEC_CODE", "");

        if ("99902".equals(productSpecCode))
        { 
            if (!"0".equals(memUserInfo.getString("PREPAY_TAG")))
            {
                resultMap.put("SERIAL_NUMBER", memSerialNumber);
                resultMap.put("RSPCODE", "99");
                resultMap.put("RSP_DESC", "成员帐户为预付费帐户，工单无法开通");
                resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
                resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
                return resultMap;
            }
        }

        //4- 企业视频彩铃 ，与VOLTE依赖 、与一号通互斥
        //daidl
        String memUserId = memUserInfo.getString("USER_ID");
        if ("910401".equals(productSpecCode))
        {
            // 4-1- 根据成员用户编号查询UU关系表，查看该成员是否开通了VoLTE服务
            inparams.clear();
            inparams.put("USER_ID_B",memUserId);

            IDataset userSvcInfo = UserSvcInfoQry.queryUserSvcByUserId(memUserId, "22", memberInfo.getString(Route.USER_EPARCHY_CODE,"0898"));
            if(IDataUtil.isEmpty(userSvcInfo)){
                resultMap.put("SERIAL_NUMBER", memSerialNumber);
                resultMap.put("RSP_CODE", "99");
                resultMap.put("RSP_DESC", "用户未开通上网服务，不能订购企业视频彩铃业务!");
                resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
                resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
            }

            // 4-2- 根据成员用户编号查询UU关系表，查看该成员是否开通了一号通产品
            inparams.put("USER_ID_B",memUserId);
            inparams.put("RELATION_TYPE_CODE","E2");
            IDataset oneCardUserRelaList = CSAppCall.call("CS.RelaUUInfoQrySVC.getRelaUUInfoByUserRelarIdB",inparams);
            if (!IDataUtil.isEmpty(oneCardUserRelaList))
            {
                resultMap.put("SERIAL_NUMBER", memSerialNumber);
                resultMap.put("RSP_CODE", "12");
                resultMap.put("RSP_DESC", "企业视频彩铃与一号通产品互斥，不能订购该产品!");
                resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
                resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
                return resultMap;
            }
            
            String usim = "0";//0 sim 卡 1 USIM卡
            String imsi = "";
            IDataset ids = BofQuery.queryUserAllValidRes(memUserId,memberInfo.getString(Route.USER_EPARCHY_CODE,"0898"));
            if(IDataUtil.isNotEmpty(ids))
			{
            	for(int i = 0 ; i < ids.size(); i++)
				{
					if("1".equals(ids.getData(i).getString("RES_TYPE_CODE")))
					{
						imsi = ids.getData(i).getString("IMSI");
						break;
					}
				}
			}			
			
			IDataset simInfos = ResCall.getSimCardInfo("1", "", imsi, "", "00");
			if(IDataUtil.isNotEmpty(simInfos))
			{
				IDataset reSet = ResCall.qrySimCardTypeByTypeCode(simInfos.getData(0).getString("RES_TYPE_CODE"));
		        if (IDataUtil.isNotEmpty(reSet))
		        {
		        	String typeCode = reSet.getData(0).getString("NET_TYPE_CODE");
		        	if("01".equals(typeCode))
		        	{
		        		usim = "1";
		        	}
		        }
			}else {
				IData param = new DataMap();
				param.put("IMSI", imsi);
				IDataset simInfo = ResCall.qryResaleSimByImsi(param);
				if(IDataUtil.isNotEmpty(simInfo))
				{
					String reSet = simInfo.getData(0).getString("CARD_MODE_TYPE");
			        if ("2".equals(reSet))
			        {
			        	usim = "1";
			        }
				}
			}
			
			//判断是否为USIM卡
			if(!"1".equals(usim))
			{
				resultMap.put("SERIAL_NUMBER", memSerialNumber);
                resultMap.put("RSP_CODE", "14");
                resultMap.put("RSP_DESC", "用户卡类型不为USIM卡，不能订购企业视频彩铃业务!");
                resultMap.put("X_RESULTCODE".toUpperCase(), IntfField.DATABASE_ERR[0]);// 其他错误
                resultMap.put("X_RESULTINFO".toUpperCase(), IntfField.DATABASE_ERR[1]);
                return resultMap;
			}
        }
        
       // IData memUserInfo = memberUserInfoList.getData(0);
        //String memUserId = memUserInfo.getString("USER_ID");
        String userStateCodeset = memUserInfo.getString("USER_STATE_CODESET", "0");
        if(!"0".equals(userStateCodeset)){
            resultMap.put("SERIAL_NUMBER", memSerialNumber);
            resultMap.put("RSP_CODE", "13");
            resultMap.put("RSP_DESC", "用户状态不正常");
            resultMap.put("X_RESULTCODE".toUpperCase(), "13");
            resultMap.put("X_RESULTINFO".toUpperCase(), "用户状态不正常");
            return resultMap;
        }

        if (!"910401".equals(productSpecCode))
        {
            IData userOwnFee = AcctCall.getOweFeeByUserId(memUserId);
            if (userOwnFee.getInt("ACCT_BALANCE", 0) < 0){
                resultMap.put("SERIAL_NUMBER", memSerialNumber);
                resultMap.put("RSP_CODE", "99");
                resultMap.put("RSP_DESC", "该号码已欠费:"+memSerialNumber);
                resultMap.put("X_RESULTCODE".toUpperCase(), "99");
                resultMap.put("X_RESULTINFO".toUpperCase(), "该号码已欠费:"+memSerialNumber);
                return resultMap;
            }
        }


        String netTypeCode = memUserInfo.getString("NET_TYPE_CODE", "00");//NET_TYPE_CODE 05:固话号码
        //最后如果是固话号码，则不需下发二次确认短信，直接入XML_INFO表并且OPEN_RESULT_CODE字段值为00
        if("05".equals(netTypeCode) || "11".equals(netTypeCode)){
            resultMap.put("SERIAL_NUMBER", memSerialNumber);
            resultMap.put("RSP_CODE", "00");
            resultMap.put("RSP_DESC", "固话号码校验成功");
            resultMap.put("X_RESULTCODE".toUpperCase(), "00");
            resultMap.put("X_RESULTINFO".toUpperCase(), "该号码为固话号码，无需下发二次确认短信");
            return resultMap;
        }
         			
    	return resultMap;
    }


    /**
     * 发短信二次确认处理
     * param iData
     */
    private static IData dealSmsTwoCheck(IData iData,boolean ifOpen) throws Exception{
    	IData result = new DataMap();
    	String memSerialNumber = iData.getString("SERIAL_NUMBER","");
    	String productOfferId = iData.getString("PRODUCTID");
        IDataset UserGrpmerchpInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId); //产品信息
        IData userGrpMerchpInfo = UserGrpmerchpInfoList.getData(0);
        String productSpecCode = userGrpMerchpInfo.getString("PRODUCT_SPEC_CODE","");
        String productId = GrpCommonBean.merchJKDTToProduct(productSpecCode, 2, null);
		String grpUserId = userGrpMerchpInfo.getString("USER_ID","");//获取集团的user_id
		String tradeTypeCode = "2352";
        
        //获取trade_type_code
		IDataset ids = UAttrBizInfoQry.getBizAttrByIdTypeObj(productId, "P", BizCtrlType.CreateMember, null);
		if(IDataUtil.isNotEmpty(ids)){
		    for (int i = 0; i < ids.size(); i++){
		        if (StringUtils.equals("业务类型", ids.getData(i).getString("ATTR_NAME", "")))
                {
                    tradeTypeCode = ids.getData(i).getString("ATTR_VALUE", "");
                    break;
                }
            }
		}
		
		//获取产品标识 统付标识
      	String 	payFlag = GrpCommonBean.getAttrValueByAttrCode(grpUserId,"9104010008");
      	if("910401".equals(productSpecCode)&&!"02".equals(payFlag)){
      		result.put("IS_NOT_TWOCHECK","0");//企业视频彩铃统付不需要二次校验
			return result;
      	}
      	if(!"1".equals(iData.getString("ACTION",""))){
      		result.put("IS_NOT_TWOCHECK","0");//非开通不需要二次校验
			return result;
      	}
        
        //获取下发二次确认短信的配置
        IDataset prodCommparaList = CommparaInfoQry.getCommpara("CSM", "9090", productSpecCode, "ZZZZ");
        if(IDataUtil.isEmpty(prodCommparaList)){
        	result.put("SERIAL_NUMBER", memSerialNumber);
        	result.put("X_RESULTCODE", "99");
        	result.put("X_RESULTINFO", "该业务在参数表中未配置");
        	result.put("RSP_CODE", "99");
        	result.put("RSP_DESC", "该业务在参数表中未配置");
            return result;
		}
        
        String smsContent = "";
        String tradeName = "";   //daidl  
        //集团V网，获取短信内容
        if("5001301".equals(productSpecCode)){
        	smsContent = getJTVWSmsContent(grpUserId,productId,productSpecCode);
        	tradeName = "集团V网（全国版）业务";
        }
        
          // daidl 企业视频彩铃业务   个付
        if ("910401".equals(productSpecCode)) {
            smsContent = getJTCLSmsContent(grpUserId,productId,productSpecCode);
            tradeName = "企业视频彩铃业务";
        }

        if(StringUtils.isEmpty(smsContent)){
        	result.put("SERIAL_NUMBER", memSerialNumber);
        	result.put("X_RESULTCODE", "99");
        	result.put("X_RESULTINFO", "获取短信内容为空");
        	result.put("RSP_CODE", "99");
        	result.put("RSP_DESC", "获取短信内容为空");
            return result;
        }
        
        IData param = new DataMap();
		//获取serialNumber
		param.put("SERIAL_NUMBER", memSerialNumber);
		param.put("SMS_CONTENT", smsContent);
		param.put("OPR_SOURCE", "1");
		param.put("SMS_TYPE", BofConst.GRP_BUSS_SEC);
		iData.put("SERIAL_NUMBER", memSerialNumber);
		iData.put("PRE_TYPE", BofConst.GRP_BUSS_SEC);
		iData.put("TRADE_STAFF_ID", CSBizBean.getVisit().getStaffId());
		iData.put("TRADE_CITY_CODE", CSBizBean.getVisit().getCityCode());
		iData.put("TRADE_DEPART_ID", CSBizBean.getVisit().getDepartId());
	
		iData.put("TRADE_NAME", tradeName);	//daidl
		iData.put("SVC_NAME", "SS.GroupTwoCheckIntfSVC.callBoss");
		if(ifOpen){ 
			iData.put("SVC_NAME", "SS.GroupTwoCheckIntfSVC.callJKDT");//集客大厅二次短信确认后直接开通    daidl
		}
		//daidl
		//TwoCheckSms.twoCheck(tradeTypeCode, 24, iData, param);
		IData twoCheckData = new DataMap();
	    twoCheckData.put("SERIAL_NUMBER", memSerialNumber);
	    twoCheckData.put("TRADE_TYPE_CODE", tradeTypeCode);
	    twoCheckData.put("PRE_DATA", iData);
	    twoCheckData.put("SMS_DATA", param);
	    twoCheckData.put("AMOUNT", "24");
	    CSAppCall.call("CS.TwoCheckSmsSVC.twoCheckJKDT",twoCheckData);
	        
		result.put("SERIAL_NUMBER", memSerialNumber);
    	result.put("X_RESULTCODE", IntfField.SUUCESS_CODE[0]);
    	result.put("X_RESULTINFO", IntfField.SUUCESS_CODE[1]);
    	result.put("RSP_CODE", "00");
    	result.put("RSP_DESC", IntfField.SUUCESS_CODE[1]);
        return result;
    }
    
    /**
     * 获取集团V网成员二次确认短信内容
     */
    private static String getJTVWSmsContent(String grpUserId,String productId,String productSpecCode) throws Exception{
		String templateContent = "";
		String grpDiscntCode = "";

		// 获取集团用户的资费信息，取出对应的资费编码
		IDataset grpDiscntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserIdForGrp(grpUserId);
//		for (int i = 0; i < grpDiscntInfos.size(); i++) {
//			IData grpDiscntInfo = grpDiscntInfos.getData(i);
//			String grpProductId = grpDiscntInfo.getString("PRODUCT_ID", "");
//			if (grpProductId.equals(productId)) {
//				grpDiscntCode = grpDiscntInfo.getString("DISCNT_CODE", "");
//			}
//		}

//		String merchDiscnt = GrpCommonBean.productJKDTToMerch(grpDiscntCode, 1);
        String merchDiscnt = "0";
        for (int i = 0; i < grpDiscntInfos.size(); i++) {
            IData grpDiscntInfo = grpDiscntInfos.getData(i);
            grpDiscntCode = grpDiscntInfo.getString("DISCNT_CODE", "");
            merchDiscnt = GrpCommonBean.productJKDTToMerch(grpDiscntCode, 1);
            if (merchDiscnt.equals("1655") || merchDiscnt.equals("1656") || merchDiscnt.equals("1657"))
                break;
        }

        // 通过全网产品编码和全网资费编码获取对应配置的分钟数信息
        IDataset prodCommparaList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9090", productSpecCode, merchDiscnt);
		if (IDataUtil.isNotEmpty(prodCommparaList)) {
			String month = SysDateMgr.getCurMonth();
			String day = SysDateMgr.getCurDay();
			String grpSimpleName = GrpCommonBean.getAttrValueByAttrCode(grpUserId, "50013010004");// 获取集团简称或集团名
			String commParaMin = prodCommparaList.getData(0).getString("PARA_CODE2", "");// 属性值
			// 获取短信模板，拼短信内容
			templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00099");
			templateContent = templateContent.replaceAll("@\\{GROUP_CUST_NAME\\}", grpSimpleName);
			templateContent = templateContent.replaceAll("@\\{MONTH\\}", month);
			templateContent = templateContent.replaceAll("@\\{DAY\\}", day);
			templateContent = templateContent.replaceAll("@\\{MIN\\}", commParaMin);
		}

		return templateContent;
    }

    private static IData dealJKDTFileMebWorkOrder(IData data)throws Exception{
        // 1- 定义返回结果
        IData rspResult;

        // 2- 初始化返回结果
        rspResult = initRspWebWorkOrderResult(data);

        // 3- 循环处理多个成员服务号码
        IDataset singleSerialNumberList = dealserialNumberListJKDT(data);
        groupJKDTRealInfoSynForMosp(data);//和多号，中间号同步信息
        if(IDataUtil.isNotEmpty(singleSerialNumberList))
        {
            for (int i = 0; i < singleSerialNumberList.size(); i++)
            {
                IData mebWordOrderMap = singleSerialNumberList.getData(i);
                IDataset dealResult = CSAppCall.call("CS.SynMebWordOrderSVC.dealMebWordOrder", mebWordOrderMap);
                String serialNumber = mebWordOrderMap.getString("SERIAL_NUMBER");
                // 拼写工单处理结果
                dealRspInfo(rspResult, dealResult, serialNumber);
            }
        }

        // 4- 拼写错误信息
        if(IDataUtil.isNotEmpty(rspResult.getDataset("RSPCODE"))){
            rspResult.put("X_RESULTCODE", rspResult.getDataset("RSPCODE").get(0).toString());
            rspResult.put("X_RESULTINFO", rspResult.getDataset("RSPDESC").get(0).toString());
        }

        // 5- 保存处理成功工单记录
        BbossOrderVerifyBean.saveOrderInfo(data.getString("ORDER_NO", ""), "S");

        // 6- 返回结果
        return rspResult;
    }

    public static void groupJKDTRealInfoSynForMosp(IData data) throws Exception {
        IData param = new DataMap();
        IDataset productIdList = IDataUtil.getDatasetSpecl("PRODUCTID", data);
        String productOfferId = (String) productIdList.get(0);

        IDataset UserGrpmerchpInfoList = UserEcrecepProductInfoQry.getUserEcrecepProductByOfferId(productOfferId);

        if (IDataUtil.isEmpty(UserGrpmerchpInfoList)) {
            CSAppException.apperr(ProductException.CRM_PRODUCT_141, IntfField.BbossGrpMemBizRet.ERR11[1], productOfferId);
        }
        IData userGrpMerchpInfo = UserGrpmerchpInfoList.getData(0);
        String productSpecCode = userGrpMerchpInfo.getString("PRODUCT_SPEC_CODE", "");
        if ("9101002".equals(productSpecCode) || "9101003".equals(productSpecCode)) {//中间号或企业和多号
            IDataset characterIDInfoList = IDataUtil.getDataset("RSRV_STR11", data);
            IDataset characterNameInfoList = IDataUtil.getDataset("RSRV_STR12", data);
            IDataset characterValueInfoList = IDataUtil.getDataset("RSRV_STR13", data);
            String groupUserId = userGrpMerchpInfo.getString("USER_ID", "");
            IData groupUserInfo = UserInfoQry.getGrpUserInfoByUserIdForGrp(groupUserId, "0");
            String groupCustId = groupUserInfo.getString("CUST_ID", "");
            param.put("GROUP_CUST_ID", groupCustId);
            param.put("GROUP_USER_ID", groupUserId);
            IDataset serialNumberList = IDataUtil.getDatasetSpecl(IntfField.SERIAL_NUMBER[0], data);//成员列表
            if (IDataUtil.isEmpty(serialNumberList)) {
                CSAppException.apperr(GrpException.CRM_GRP_30);
            }
            IDataset numberList = new DatasetList();
            for (int i = 0; i < serialNumberList.size(); i++) {
                IData numbers = new DataMap();
                IDataset actionInfoList = IDataUtil.getDatasetSpecl("ACTION", data);
                String action = actionInfoList.get(i).toString();
                IDataset attrIds = characterIDInfoList.getDataset(i);
                IDataset attrValues = characterValueInfoList.getDataset(i);
                String followNumProv = "";
                for (int j = 0; j < attrIds.size(); j++) {
                    if ("1002".equals(attrIds.get(j).toString()) || "1011".equals(attrIds.get(j).toString())) {
                        followNumProv = attrValues.get(j).toString();//副号码归属省份编码
                    }
                }
                if ("1".equals(action)) {//增加
                    numbers.put("SERIAL_NUMBER", serialNumberList.get(i).toString());
                    numbers.put("FOLLOW_PROV", followNumProv);
                    numbers.put("OPR_CODE", "01");
                    numberList.add(numbers);
                } else if ("0".equals(action)) {//或删除
                    numbers.put("SERIAL_NUMBER", serialNumberList.get(i).toString());
                    numbers.put("FOLLOW_PROV", followNumProv);
                    numbers.put("OPR_CODE", "02");
                    numberList.add(numbers);
                }
            }
            param.put("NUMBER_LIST", numberList);
            MiddleNumberInfoSynSVC.groupFollowMsisdnSyn(param);
        }
    }

    /**
     * @description 集客大厅成员接口校验和发确认短信结果入报文主表
     * @date 2018-12-11
     */
    private static String recordMebCheckRspXMLMainInfo(IData map,String dealState)throws Exception {
        //1- 定义报文主表信息对象
        IData bbossXmlMainInfo = new DataMap();

        //2- 添加主键编号
        String seqId = TimeUtil.getSysDate("yyyyMMdd", true)+SeqMgr.getXmlInfoId();
        bbossXmlMainInfo.put("SEQ_ID", seqId);

        //3- 添加BIPCODE,	MemberService_CKRSP代表需要按4.6.4.2反馈结果
        bbossXmlMainInfo.put("BIPCODE", "MemberService_CKRSP");

        //4- 获取TRANDS_IDO
        String transIdo = map.getString("TRANSIDO");
        if(StringUtils.isNotEmpty(transIdo)){
            bbossXmlMainInfo.put("TRANDS_IDO", transIdo);
        }else{
            bbossXmlMainInfo.put("TRANDS_IDO", map.getString("IBSYSID"));//文件接口没有TRANDS_IDO 只有IBSYSID；
        }

        //5- 添加成员号码
        bbossXmlMainInfo.put("SERIAL_NUMBER", map.getString("SERIAL_NUMBER", ""));

        //6- 添加报文的落地时间
        bbossXmlMainInfo.put("LOCATE_TIME", SysDateMgr.getSysTime());

        //7- 添加报文的处理时间
        bbossXmlMainInfo.put("DEAL_TIME", SysDateMgr.getSysTime());

        //8- 添加报文处理状态
        bbossXmlMainInfo.put("DEAL_STATE",dealState);

        //9- 添加处理结果编码
        bbossXmlMainInfo.put("OPEN_RESULT_CODE",map.getString("OPEN_RESULT_CODE",""));

        //10- 添加处理结果说明
        bbossXmlMainInfo.put("OPEN_RESULT_DESC",map.getString("OPEN_RESULT_DESC",""));

        //11- 初始化IBOSS_RESULT为-1，供IBOSS使用
        bbossXmlMainInfo.put("IBOSS_RESULT", "-1");

        //12- 添加报文流水PKGSEQ
        bbossXmlMainInfo.put("PKG_SEQ", map.getString("PKG_SEQ", ""));

        //12.5- 修改某些剩余字段
        bbossXmlMainInfo.put("PRODUCT_ORDER_NUMBER", map.getString("PRODUCT_ORDER_NUMBER",""));
        bbossXmlMainInfo.put("PRODUCT_OFFER_ID", map.getString("PRODUCTID"));
        bbossXmlMainInfo.put("RSRV_STR1", map.getString("PKG_SEQ", ""));
        bbossXmlMainInfo.put("RSRV_STR2", map.getString("PKG_SEQ", ""));
        bbossXmlMainInfo.put("RSRV_STR3", map.getString("RSRV_STR3"));
        bbossXmlMainInfo.put("RSRV_STR4", map.getString("RSRV_STR4"));

        //13- 调用方法保存
        Dao.delete("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo, Route.CONN_CRM_CEN);
        Dao.insert("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo,Route.CONN_CRM_CEN);

        //1- 定义报文内容信息对象    daidl
  		IData bbossXmlContentInfo = new DataMap();

  		//2- 添加主键
  		bbossXmlContentInfo.put("SEQ_ID", seqId);

  		//3- 添加报文流水号
  		bbossXmlContentInfo.put("TRANDS_IDO", bbossXmlMainInfo.getString("TRANDS_IDO"));
  		
  		//4- 分割报文串，分别保存到对应的字段中
		map.put("RECEPTIONHALLMEM", "1");//集客大厅标记
		map.put(IntfField.ANTI_INTF_FLAG[0], "1");//反向标记
  		String xmlContent = map.toString();
  		String[] xmlContentArr = MebCommonBean.splitStringByBytes(xmlContent,4000);
  		for(int i=0;i<10;i++){
  			bbossXmlContentInfo.put("XML_CONTENT_"+(i+1), xmlContentArr[i]);
  		}

  		//5- 调用方法保存
  		//Dao.delete("TF_TP_BBOSS_XML_CONTENT", bbossXmlContentInfo, Route.CONN_CRM_CEN);
  		Dao.insert("TF_TP_BBOSS_XML_CONTENT", bbossXmlContentInfo,Route.CONN_CRM_CEN);	  

        //14- 返回主键
        return seqId;
    }

    /**
     * 集客大厅处理成员号码
     * @return
     * @throws Exception
     */
    public static IDataset dealJKDTMemberNumber(IData data,boolean openflag) throws Exception{
    	
    	String returnFlag = data.getString("RETURN_FLAG_KT", "");
        String fileflag = "";
        String filename = "";
        if (!"".equals(returnFlag)) {
     	   fileflag = "JKDTFILE";//文件接口标记
     	   filename = data.getString("FILENAME","");
        } 
        IDataset checkResultList = new DatasetList();
        // 循环处理多个成员服务号码
        IDataset singleSerialNumberList = GrpIntf.dealserialNumberListJKDT(data);
        if(IDataUtil.isNotEmpty(singleSerialNumberList))
        {
            for (int i = 0; i < singleSerialNumberList.size(); i++){
                IData mebWordOrderMap = singleSerialNumberList.getData(i);
                mebWordOrderMap.put("PRODUCTID", data.getString("PRODUCTID", ""));
                mebWordOrderMap.put("RSRV_STR3", fileflag); 
                mebWordOrderMap.put("RSRV_STR4", filename); 
                IData checkData = checkMemberStatus(mebWordOrderMap);//校验成员状态
                if(IDataUtil.isNotEmpty(checkData)){
                    mebWordOrderMap.put("OPEN_RESULT_CODE", checkData.getString("RSP_CODE", ""));
                    mebWordOrderMap.put("OPEN_RESULT_DESC", checkData.getString("RSP_DESC", ""));
                    mebWordOrderMap.put("PKG_SEQ", "".equals(data.getString("pkgSeq",""))?data.getString("PKGSEQ","") : data.getString("pkgSeq",""));
        			mebWordOrderMap.put("PRODUCT_ORDER_NUMBER", data.getString("ORDER_NO"));
                    recordMebCheckRspXMLMainInfo(mebWordOrderMap, "C");//处理结果入报文表，deal_state置为C
                    checkResultList.add(checkData);
                }else{
                    IData smsData = new DataMap();
                    smsData = dealSmsTwoCheck(mebWordOrderMap,openflag);
                    String dealFlag = smsData.getString("RSP_CODE", "");
                    String isNotTwoCheck = smsData.getString("IS_NOT_TWOCHECK","");
        			if("0".equals(isNotTwoCheck)){
        				checkResultList.add(smsData);
        				return checkResultList;
        			}
                    if("00".equals(dealFlag)){//处理二次确认短信成功，OPEN_RESULT_CODE和OPEN_RESULT_DESC字段置为空，待后续处理
                        mebWordOrderMap.put("OPEN_RESULT_CODE", "");
                        mebWordOrderMap.put("OPEN_RESULT_DESC", "");
                        mebWordOrderMap.put("PKG_SEQ", "".equals(data.getString("pkgSeq",""))?data.getString("PKGSEQ","") : data.getString("pkgSeq",""));
            			mebWordOrderMap.put("PRODUCT_ORDER_NUMBER", data.getString("ORDER_NO"));
                        recordMebCheckRspXMLMainInfo(mebWordOrderMap, "C");//处理结果入报文表，deal_state置为C
                    }else{
                        mebWordOrderMap.put("OPEN_RESULT_CODE", smsData.getString("RSP_CODE", ""));
                        mebWordOrderMap.put("OPEN_RESULT_DESC", smsData.getString("RSP_DESC", ""));
                        mebWordOrderMap.put("PKG_SEQ", "".equals(data.getString("pkgSeq",""))?data.getString("PKGSEQ","") : data.getString("pkgSeq",""));
            			mebWordOrderMap.put("PRODUCT_ORDER_NUMBER", data.getString("ORDER_NO"));
                        recordMebCheckRspXMLMainInfo(mebWordOrderMap, "C");//处理结果入报文表，deal_state置为C
                    }
                    checkResultList.add(smsData);
                }
            }
        }else{
            IData errData = new DataMap();
            errData.put("X_RESULTCODE", "99");
            errData.put("X_RESULTINFO", "数据错误，无成员服务号码");
            errData.put("RSP_CODE", "99");
            errData.put("RSP_DESC", "数据错误，无成员服务号码");
            checkResultList.add(errData);
        }
        return checkResultList;
    }

    /**
     * 集客大厅校验成员号码     daidl  2019-4-16
     * @param memberInfo
     * @return
     * @throws Exception
     */
    public static IDataset checkJKDTMemberNumber(IData data) throws Exception{
    	String returnFlag = data.getString("RETURN_FLAG_KT", "");
        String fileflag = "";
        String filename = "";
        if (!"".equals(returnFlag)) {
     	   fileflag = "JKDTFILE";//文件接口标记
     	   filename = data.getString("FILENAME","");
        } 
    	IDataset checkResultList = new DatasetList();
		// 循环处理多个成员服务号码
        IDataset singleSerialNumberList = GrpIntf.dealserialNumberListJKDT(data);
        if(IDataUtil.isNotEmpty(singleSerialNumberList))
        {
        	for (int i = 0; i < singleSerialNumberList.size(); i++){
        		IData mebWordOrderMap = singleSerialNumberList.getData(i); 
        		mebWordOrderMap.put("PRODUCTID", data.getString("PRODUCTID", ""));
        		mebWordOrderMap.put("RSRV_STR3", fileflag);   
                mebWordOrderMap.put("RSRV_STR4", filename);
        		IData checkData = checkMemberStatus(mebWordOrderMap);//校验成员状态
        		if(IDataUtil.isNotEmpty(checkData)){
        			mebWordOrderMap.put("OPEN_RESULT_CODE", checkData.getString("RSP_CODE", ""));
        			mebWordOrderMap.put("OPEN_RESULT_DESC", checkData.getString("RSP_DESC", ""));
        			mebWordOrderMap.put("PKG_SEQ", "".equals(data.getString("pkgSeq",""))?data.getString("PKGSEQ","") : data.getString("pkgSeq",""));
        			mebWordOrderMap.put("PRODUCT_ORDER_NUMBER", data.getString("ORDER_NO"));
        			recordMebCheckRspXMLMainInfo(mebWordOrderMap, "C");//处理结果入报文表，deal_state置为C
        			checkResultList.add(checkData);
        		}
        		else{
        			mebWordOrderMap.put("OPEN_RESULT_CODE", "00");
        			mebWordOrderMap.put("OPEN_RESULT_DESC", "成员号码校验成功");
        			mebWordOrderMap.put("PKG_SEQ", "".equals(data.getString("pkgSeq",""))?data.getString("PKGSEQ","") : data.getString("pkgSeq",""));
        			mebWordOrderMap.put("PRODUCT_ORDER_NUMBER", data.getString("ORDER_NO"));
        			recordMebCheckRspXMLMainInfo(mebWordOrderMap, "C");//处理结果入报文表，deal_state置为C
        	    	
        			IData resultMap = new DataMap();
        			resultMap.put("SERIAL_NUMBER", mebWordOrderMap.getString("SERIAL_NUMBER",""));
    	    		resultMap.put("RSP_CODE", "00");
    	    		resultMap.put("RSP_DESC", "成员服务号码校验成功");                                
    	    		resultMap.put("X_RESULTCODE".toUpperCase(), "00");
    	    		resultMap.put("X_RESULTINFO".toUpperCase(), "校验成功");  
        			checkResultList.add(checkData);
        		}
        	}
        }else{
        	IData errData = new DataMap();
        	errData.put("X_RESULTCODE", "99");
        	errData.put("X_RESULTINFO", "数据错误，无成员服务号码");
        	errData.put("RSP_CODE", "99");
        	errData.put("RSP_DESC", "数据错误，无成员服务号码");
        	checkResultList.add(errData);
        }
		return checkResultList;
    }
    
    
    
    //daidl  成员订购彩铃业务进行二次确认短信内容 
    private static String getJTCLSmsContent(String grpUserId, String proSpecCode, String productSpecCode) throws Exception {
        String templateContent = "";
        String grpDiscntCode = "";

        //1- 用集团用户ID查询该订购付费模式
        IDataset userAttrByUserId = UserAttrInfoQry.getUserAttrByUserId(grpUserId, "9104010008");
        if (IDataUtil.isEmpty(userAttrByUserId)) {
            CSAppException.apperr(CrmUserException.CRM_USER_783, grpUserId);
        }
        String feeType = userAttrByUserId.getData(0).getString("ATTR_VALUE");

        if (StringUtils.equals(feeType, "01")) {
            return templateContent;
        }

        String productId = GrpCommonBean.merchJKDTToProduct(productSpecCode + "_02", 2, null);
        // 获取集团用户的资费信息，取出对应的资费编码
        IDataset grpDiscntInfos = UserDiscntInfoQry.queryUserAllDiscntByUserId(grpUserId);
        for (int i = 0; i < grpDiscntInfos.size(); i++) {
            IData grpDiscntInfo = grpDiscntInfos.getData(i);
            String grpProductId = grpDiscntInfo.getString("PRODUCT_ID", "");
            //if (grpProductId.equals(productId)) {
                grpDiscntCode = grpDiscntInfo.getString("DISCNT_CODE", "");
          //  }
        }
        

        String merchDiscnt = GrpCommonBean.productJKDTToMerch(grpDiscntCode, 1);
        // 通过全网产品编码和全网资费编码获取对应配置的分钟数信息
        IDataset prodCommparaList = CommparaInfoQry.getCommparaByCodeCode1("CSM", "9090", productSpecCode, merchDiscnt);
        if (IDataUtil.isNotEmpty(prodCommparaList)) {
            String month = SysDateMgr.getCurMonth();
            String day = SysDateMgr.getCurDay();
            IDataset grp_infos = GrpInfoQry.queryUserGroupInfos(grpUserId, "0");
            String grpSimpleName = grp_infos.getData(0).getString("CUST_NAME");// 获取集团简称或集团名

            String commParaMin = prodCommparaList.getData(0).getString("PARA_CODE2", "");// 属性值
            // 获取短信模板，拼短信内容
            templateContent = TemplateBean.getTemplate("CRM_SMS_GRP_BBOSS_00099");
            templateContent = templateContent.replaceAll("@\\{GROUP_CUST_NAME\\}", grpSimpleName);
            
            templateContent = templateContent.replaceAll("@\\{MIN\\}",commParaMin);
        }

        return templateContent;
    }

}
