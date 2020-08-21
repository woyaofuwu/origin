
package com.asiainfo.veris.crm.order.web.frame.csview.group.creategroupmember;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.StaffPrivUtil;


public abstract class ProductInfo extends GroupBasePage
{
    public void initial(IRequestCycle cycle) throws Exception
    {

        IData inparam = getData();
        // 集团产品用户标识USER_ID
        String grpuserId = inparam.getString("GRP_USER_ID", "");
        String mebuserId = inparam.getString("MEB_USER_ID", "");
        String mebcustId = inparam.getString("MEB_CUST_ID", "");
        productId = inparam.getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryCrtMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        // 产品参数页面的条件参数值
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        String grpid = inparam.getString("GROUP_ID", "");

        IData dynparam = new DataMap();
        dynparam.put("PRODUCT_ID", productId);
        dynparam.put("GRP_USER_ID", grpuserId);
        dynparam.put("MEB_USER_ID", mebuserId);
        dynparam.put("MEB_EPARCHY_CODE", mebeparchycode);
        dynparam.put("MEB_SERIAL_NUMBER", getData().getString("MEB_SERIAL_NUMBER", ""));
        dynparam.put("CUST_ID", getData().getString("CUST_ID", ""));
        dynparam.put("GROUP_ID", grpid);
        dynparam.put("MEB_CUST_ID", mebcustId); // 成员cust_id

        setDynParam(dynparam);

        // 产品组件页面已选区参数信息
        IData cond = new DataMap();
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        cond.put("PRODUCT_ID", productId);
        cond.put("EPARCHY_CODE", mebeparchycode);
        cond.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode); // 初始服务的路由
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);// 传递业务类型后才做产品互斥规则验证
        cond.put("EFFECT_NOW", true);// 新增是默认为立即生效
        cond.put("USER_ID", mebuserId); // 规则验证时需要使用用户ID
        cond.put("MEB_PRODUCT_ID", UpcViewCall.queryMemProductIdByProductId(this, productId));

        String acctDay = getData().getString("USER_ACCTDAY_ACCT_DAY", "");
        String firstDate = getData().getString("USER_ACCTDAY_FIRST_DATE", "");
        String nextAcctDay = getData().getString("USER_ACCTDAY_NEXT_ACCT_DAY", "");
        String nextFirstDate = getData().getString("USER_ACCTDAY_NEXT_FIRST_DATE", "");
        cond.put("ACCT_DAY", acctDay);
        cond.put("FIRST_DATE", firstDate);
        cond.put("NEXT_ACCT_DAY", nextAcctDay);
        cond.put("NEXT_FIRST_DATE", nextFirstDate);

        String ifBooking = getData().getString("IF_BOOKING", "false");

        if (ifBooking.equals("true"))
        {
            String bookingData = SysDateMgr.getFirstDayOfNextMonth4WEB();
            IData userAcctDayData = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, mebuserId, mebeparchycode);
            if (IDataUtil.isNotEmpty(userAcctDayData))
            {
                bookingData = userAcctDayData.getString("FIRST_DAY_NEXTACCT", bookingData);
            }
            cond.put("PRODUCT_PRE_DATE", bookingData);
        }
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        /*身份校验方式:若加成员时需要身份校验,则不需要上传凭证生成集团业务稽核工单，
         * 免身份校验则需要上传凭证*/
        String authCheckMode = inparam.getString("cond_CHECK_MODE", "");
        System.out.println("========inparam=="+inparam);
        System.out.println("========authCheckMode=="+authCheckMode);
        //免身份校验
        boolean flag = StaffPrivUtil.isFuncDataPriv(this.getVisit().getStaffId(), "CRM_OPERENTERPRISE");//有权限免稽核
        if(StringUtils.isBlank(authCheckMode) || "F".equals(authCheckMode)){
        	IData iparam = new DataMap();
            iparam.put("SUBSYS_CODE", "CSM");
            iparam.put("PARAM_ATTR", "839");
            iparam.put("PARAM_CODE", productId);
            iparam.put("EPARCHY_CODE", Route.getCrmDefaultDb());
            IDataset resultSet = CSViewCall.call(this, "CS.ParamInfoQrySVC.getCommparaByParamattr", iparam);
            if (IDataUtil.isNotEmpty(resultSet)){
            	cond.put("MEB_VOUCHER_FILE_SHOW","false");
            }else{
            	if(flag){
            		cond.put("MEB_VOUCHER_FILE_SHOW","false");
            	}else{
            		cond.put("MEB_VOUCHER_FILE_SHOW","true");
            	}
            }
        }
        //需要身份校验则不显示凭证上传
        else{
        	cond.put("MEB_VOUCHER_FILE_SHOW","false");
        }
        
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----

        setCond(cond);

        // 产品组件页面包列表参数信息
        IData pkgParam = new DataMap();

        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode); // 初始服务的路由
        pkgParam.put("GRP_USER_ID", grpuserId); // 定制产品时需要查询集团给成员定制的元素
        setPkgParam(pkgParam);

        setRoleList(GroupProductUtilView.qryRoleListByProductId(this, productId));

        IData info = new DataMap();
        info.put("IF_ADD_MEB", inparam.getString("IF_ADD_MEB", "false"));// 是否需要新增三户资料

        setInfo(info);

    }

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynparam);

    public abstract void setInfo(IData info);

    public abstract void setPkgParam(IData pkgparam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

    public abstract void setRoleList(IDataset roleList);

}
