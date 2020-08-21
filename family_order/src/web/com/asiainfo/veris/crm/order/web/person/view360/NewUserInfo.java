
package com.asiainfo.veris.crm.order.web.person.view360;

import org.apache.tapestry.IRequestCycle;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ExcelConfig;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;

public abstract class NewUserInfo extends PersonBasePage
{
    /**
     * 同步导出,根据生成的文件ID生成下载的URL地址
     * 
     * @param cycle
     * @throws Exception
     */
    public void exportDataToFile(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        String userId = data.getString("USER_ID", "");
        if ("".equals(userId))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_660);
        }

        IData inparam = new DataMap();
        inparam.put("USER_ID", userId);
        inparam.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));

        // 获取要导出的数据集
        IDataset userInfos = CSViewCall.call(this, "SS.GetUser360ViewSVC.getUserInfoForExport", inparam);
        IData userInfo = userInfos.getData(0);
        userInfo.put("PSPT_TYPE", pageutil.getStaticValue("PSPT_TYPE_CODE", userInfo.getString("PSPT_TYPE_CODE")));
        userInfo.put("PAY_MODE", pageutil.getStaticValue("TD_S_PAYMODE", userInfo.getString("PAY_MODE_CODE")));
        userInfo.put("BANK", pageutil.getStaticValue("TD_B_BANK", "BANK_CODE", "BANK", userInfo.getString("BANK_CODE")));
        userInfo.put("BRAND", pageutil.getStaticValue("TD_S_BRAND", "BRAND_CODE", "BRAND", userInfo.getString("BRAND_CODE")));
        IDataset userDataset = new DatasetList();
        userDataset.add(userInfo);
        //
        // // 保存的字段列表
        // String para[] =
        // { "SERIAL_NUMBER", "CUST_NAME", "PSPT_TYPE", "PSPT_ID", "PSPT_ADDR", "HOME_ADDRESS", "POST_CODE", "PHONE",
        // "FAX_NBR", "EMAIL", "CONTACT", "USER_TYPE", "X_SVCSTATE_EXPLAIN", "BRAND", "OPEN_DATE", "DESTROY_TIME",
        // "PAY_NAME", "PAY_MODE",
        // "BANK", "BANK_ACCT_NO" };
        // String paraName[] =
        // { "电话号码：", "客户名称：", "证件类型：", "证件号码：", "证件地址：", "居住地址：", "居住邮编：", "联系电话：", "传真电话：", "email地址：", "联系人：",
        // "用户类型：", "用户状态：", "品牌类型：", "开户时间：", "消户时间：", "帐户名称：", "帐户类型：", "银行名称：", "银行帐号：" };
        //
        // inparam.put("FILE_NAME", data.getString("SERIAL_NUMBER", "15116287078") + ".txt");
        // inparam.put("USER_DATASET", userDataset);
        // inparam.put("USER_PARAM", para);
        // inparam.put("USER_PARAM_NAME", paraName);
        //
        // IDataset dataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.exportUser", inparam);
        //
        // // 返回可用于下载文件的URL
        // String url = (String) dataset.get(0);
        // setAjax("url", url);
        String fileName = "用户基本信息[" + data.getString("SERIAL_NUMBER", "") + "].xls";
        String xmlPath = "export/view360/NewUserInfo.xml";
        IData params = new DataMap();
        params.put("posX", "0");
        params.put("posY", "0");
        params.put("ftpSite", "personserv");

        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());
        // 将数据写入文件并返回文件ID
        String fileId = ImpExpUtil.beginExport(null, params, fileName, new IDataset[]
        { userDataset }, ExcelConfig.getSheets(xmlPath));
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(fileId, fileName);
        setAjax("url", url);
    }

    /**
     * 用户基本信息
     * 
     * @param cycle
     * @throws Exception
     */
    public void queryInfo(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        if (StringUtils.isNotBlank(data.getString("USER_ID", "")))
        {

            data.put("USER_ID", data.getString("USER_ID", ""));
            data.put(Route.ROUTE_EPARCHY_CODE, this.getVisit().getStaffEparchyCode());

            IDataset userInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserInfo", data);
            IDataset platInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryPlatInfo", data);
            IDataset accountInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryBaseAccountInfo", data);
            IDataset resInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserResSimInfo", data);
            IDataset mmsfuncInfo = CSViewCall.call(this, "SS.GetUser360ViewSVC.qry_tf_sm_bi_mmsfunc_InfoByUserId", data);            
            IData user = new DataMap();

            if(IDataUtil.isNotEmpty(userInfo))
            {
            	user = userInfo.getData(0);
                // 发展员工字段有错误数据 去掉
                if (user.getString("DEVELOP_STAFF_ID", "").trim().length() < 8)
                {
                	user.put("DEVELOP_STAFF_ID", "");
                }
                if("4".equals(user.getString("USER_STATE_CODESET",""))){
                	IDataset stopUsers = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserIfNotRealName", data);
                	if(stopUsers!=null && stopUsers.size()>0){
                		user.put("USER_STATE_CODESET", "HT");
                	}
                }
                /**
                 * REQ201608260010 关于非实名用户关停改造需求
                 * 20160912 chenxy3
                 * 欠费停机转“非实名制全停”
                 * */
                if("5".equals(user.getString("USER_STATE_CODESET",""))){
                	IDataset stopUsers = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryUserIfAllStop", data);
                	if(stopUsers!=null && stopUsers.size()>0){
                		user.put("USER_STATE_CODESET", "AT");
                	}
                }
            }

            if(IDataUtil.isNotEmpty(resInfo))
            {
            	user.put("RES_KIND_NAME", resInfo.getData(0).getString("RES_KIND_NAME",""));
            }
            if (IDataUtil.isNotEmpty(mmsfuncInfo))
            {
                String att_flag1_name = (String) mmsfuncInfo.getData(0).get("ATT_FLAG1_NAME");
                user.put("ATT_FLAG1_NAME", att_flag1_name);
            }
            setCond(data);
            
//          //翻译产品名称
//          String productId = user.getString("PRODUCT_ID","");
//          String productName = UpcViewCall.queryOfferNameByOfferId(this, "P", productId);
//          user.put("PRODUCT_NAME", productName);
//          //次月翻译产品名称
//          String bProductId = user.getString("B_PRODUCT_ID","");
//          String bProductName = UpcViewCall.queryOfferNameByOfferId(this, "P", bProductId);
//          user.put("B_PRODUCT_NAME", bProductName);
//          //翻译品牌名称
//          String brandCode = user.getString("BRAND_CODE","");
//          String brandName = UpcViewCall.getBrandNameByBrandCode(this, brandCode);
//          user.put("BRAND_NAME", brandName);
//          //次月翻译品牌名称
//          String bBrandCode = user.getString("B_BRAND_CODE","");
//          String bBrandName = UpcViewCall.getBrandNameByBrandCode(this, bBrandCode);
//          user.put("B_BRAND_NAME", bBrandName);
            
            //翻译产品名称 品牌名称
            IData  param=new DataMap();
            param.put("PRODUCT_ID", user.getString("PRODUCT_ID",""));
            param.put("BRAND_CODE", user.getString("BRAND_CODE",""));            
            IData result = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param);
            user.put("PRODUCT_NAME", result.getString("PRODUCT_NAME"));
            user.put("BRAND_NAME", result.getString("BRAND_NAME"));

            
          //翻译下月产品名称 品牌名称
            IData  param2=new DataMap();
            param2.put("PRODUCT_ID", user.getString("B_PRODUCT_ID",""));
            param2.put("BRAND_CODE", user.getString("B_BRAND_CODE",""));            
            IData result2 = CSViewCall.callone(this, "SS.CreateRedMemberSVC.getUserName", param2);
            user.put("B_PRODUCT_NAME", result2.getString("PRODUCT_NAME"));
            user.put("B_BRAND_NAME", result2.getString("BRAND_NAME"));

            
            

            setInfo(user);
            setPlatInfos(platInfo);
            setAccountInfos(accountInfo);

            // 增加查询客户综合资料时显示左边的业务信息
            IData inParam = userInfo.getData(0);
            inParam.put("TRADE_TYPE_CODE", "2101");
            IDataset dataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.qryPopuInfo", data);
            setPopuInfo(dataset.getData(0));

        }

    }

    /**
     * 发送客户经理号码
     * 
     * @param cycle
     * @throws Exception
     */
    public void sendCustManagerNum(IRequestCycle cycle) throws Exception
    {
        IData data = getData();
        IData result = null;
        String custmanagerid = data.getString("CUST_MANAGER_ID", "");
        String serialnum = data.getString("SERIAL_NUMBER", "");
        if ("".equals(custmanagerid) || "".equals(serialnum))
        {
            CSViewException.apperr(CrmUserException.CRM_USER_783, "用户服务号码或客户经理ID为空！");
        }
        IData param = new DataMap();
        param.put("CUST_MANAGER_ID", custmanagerid);
        param.put("SERIAL_NUMBER", serialnum);
        IDataset dataset = CSViewCall.call(this, "SS.GetUser360ViewSVC.sendCustManagerNum", param);
        result = dataset.getData(0);
        setInfo(result);
    }

    public abstract void setAccountInfos(IDataset accountInfos);

    public abstract void setCond(IData cond);

    public abstract void setInfo(IData info);

    public abstract void setPlatInfos(IDataset platInfos);

    public abstract void setPopuInfo(IData popuInfo);

}
