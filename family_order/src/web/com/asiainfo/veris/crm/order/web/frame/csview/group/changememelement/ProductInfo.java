
package com.asiainfo.veris.crm.order.web.frame.csview.group.changememelement;

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
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationbbinfo.RelationBBInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.userinfo.relationuuinfo.RelationUUInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.paraminfo.attrbiz.AttrBizInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.base.GroupBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.group.common.util.GroupProductUtilView;

public abstract class ProductInfo extends GroupBasePage
{
    /**
     * 作用：初始化页面
     * 
     * @author luojh 2009-07-29
     * @param cycle
     * @throws Exception
     */
    public void initial(IRequestCycle cycle) throws Exception
    {
    	IData inparam = this.getData();
        String grpUserId = getData().getString("GRP_USER_ID", "");
        String grpSn = getData().getString("GRP_USER_ID", "");
        String grpId = getData().getString("GROUP_ID", "");
        String mebuserId = getData().getString("MEB_USER_ID", "");
        String mebsn = getData().getString("MEB_SERIAL_NUMBER", "");
        String mebeparchycode = getData().getString("MEB_EPARCHY_CODE", "");
        productId = getData().getString("GRP_PRODUCT_ID", "");

        // 获取产品控制信息
        IData productCtrlInfo = AttrBizInfoIntfViewUtil.qryChgMbProductCtrlInfoByProductId(this, productId);
        setProductCtrlInfo(productCtrlInfo);

        // 获取成员账期信息
        IData mebUserAcctDayInfo = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, mebuserId, mebeparchycode);

        // 获取产品组件的已选区初始参数
        String tradeTypeCode = productCtrlInfo.getData("TradeTypeCode").getString("ATTR_VALUE");
        IData cond = new DataMap();
        cond.put("PRODUCT_ID", productId);
        cond.put("USER_ID", mebuserId);
        cond.put("EPARCHY_CODE", mebeparchycode);
        cond.put("TRADE_TYPE_CODE", tradeTypeCode);
        cond.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);

        String acctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_ACCT_DAY", "");
        String firstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_FIRST_DATE", "");
        String nextAcctDay = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_ACCT_DAY", "");
        String nextFirstDate = mebUserAcctDayInfo.getString("USER_ACCTDAY_NEXT_FIRST_DATE", "");
        cond.put("ACCT_DAY", acctDay);
        cond.put("FIRST_DATE", firstDate);
        cond.put("NEXT_ACCT_DAY", nextAcctDay);
        cond.put("NEXT_FIRST_DATE", nextFirstDate);

        String ifBooking = getData().getString("IF_BOOKING", "false");
        if (ifBooking.equals("true"))
        {
            String startBookingData = SysDateMgr.getFirstDayOfNextMonth4WEB();
            String endBookingData = SysDateMgr.getLastDateThisMonth();
            IData userAcctDayData = UCAInfoIntfViewUtil.qryMebUserAcctDayInfoUserId(this, mebuserId, mebeparchycode);
            if (IDataUtil.isNotEmpty(userAcctDayData))
            {
                startBookingData = userAcctDayData.getString("FIRST_DAY_NEXTACCT", startBookingData);
                endBookingData = userAcctDayData.getString("LAST_DAY_THISACCT", endBookingData);
            }
            cond.put("PRODUCT_PRE_DATE", startBookingData);
            cond.put("PRODUCT_PRE_END_DATE", endBookingData);
        }
        cond.put("MEB_PRODUCT_ID", UpcViewCall.queryMemProductIdByProductId(this, productId));
        
        //add by chenzg@20180705-begin-REQ201804280001集团合同管理界面优化需求--
        /*身份校验方式:若加成员时需要身份校验,则不需要上传凭证生成集团业务稽核工单，
         * 免身份校验则需要上传凭证*/
        String authCheckMode = inparam.getString("cond_CHECK_MODE", "");
        System.out.println("========inparam=="+inparam);
        System.out.println("========authCheckMode=="+authCheckMode);
        //免身份校验
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
            	cond.put("MEB_VOUCHER_FILE_SHOW","true");
            }
        }
        //需要身份校验则不显示凭证上传
        else{
        	cond.put("MEB_VOUCHER_FILE_SHOW","false");
        }
        //add by chenzg@20180705-end-REQ201804280001集团合同管理界面优化需求----
        
        setCond(cond);

        // 获取产品组件的包列表区初始参数
        IData pkgParam = new DataMap();
        pkgParam.put("PRODUCT_ID", productId);
        pkgParam.put("GRP_USER_ID", grpUserId);
        pkgParam.put("EPARCHY_CODE", mebeparchycode);
        pkgParam.put(Route.USER_EPARCHY_CODE, mebeparchycode);
        setPkgParam(pkgParam);

        // 获取产品参数页面的的初始参数
        IData dynParam = new DataMap();
        dynParam.put("PRODUCT_ID", productId);
        dynParam.put("GRP_USER_ID", grpUserId);
        dynParam.put("GRP_SN", grpSn);
        dynParam.put("MEB_USER_ID", mebuserId);
        dynParam.put("MEB_EPARCHY_CODE", mebeparchycode);
        dynParam.put("EPARCHY_CODE", mebeparchycode);
        dynParam.put("MEB_SERIAL_NUMBER", mebsn);
        dynParam.put("GROUP_ID", grpId);
        dynParam.put(Route.ROUTE_EPARCHY_CODE, mebeparchycode);
        
        setDynParam(dynParam);

        // 展示成员角色信息
        IData info = new DataMap();
        String roleCodeB = RelationUUInfoIntfViewUtil.qryRelaUURoleCodeBByUserIdBAndUserIdA(this, mebuserId, grpUserId, mebeparchycode);

        if (StringUtils.isBlank(roleCodeB))
        {
            roleCodeB = RelationBBInfoIntfViewUtil.qryRelaBBRoleCodeBByUserIdBAndUserIdA(this, mebuserId, grpUserId, mebeparchycode);
        }
        info.put("ROLE_CODE_B", roleCodeB);

        setInfo(info);
        setRoleList(GroupProductUtilView.qryRoleListByProductId(this, productId));
        setProductId(productId);
        setResList(GroupProductUtilView.initResList(this, getData().getString("MEB_USER_ID", ""), getData().getString("GRP_USER_ID", ""), getData().getString("MEB_EPARCHY_CODE", "")));
    }

    public abstract void setCond(IData info);

    public abstract void setDynParam(IData dynParam);

    public abstract void setInfo(IData info);

    public abstract void setPkgParam(IData pkgParam);

    public abstract void setProductCtrlInfo(IData productCtrlInfo);

    public abstract void setResList(IDataset idata);

    public abstract void setRoleList(IDataset roleList);

}
