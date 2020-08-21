
package com.asiainfo.veris.crm.order.web.frame.csview.group.rule;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.group.common.GroupRuleConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSBizHttpHandler;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;

public class CreateGroupUserRule extends CSBizHttpHandler
{

    /**
     * 集团用户开户BaseInfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkBaseInfoRule() throws Exception
    {

        IData conParams = getData();

        IData checkParam = new DataMap();

        String if_centretype = conParams.getString("IF_CENTRETYPE", ""); // 融合V网标识
        if (if_centretype.equals("2")) // 融合V网
        {
            checkParam.put("IF_CENTRETYPE", if_centretype);
        }
        checkParam.put("CHK_FLAG", "BaseInfo");
        checkParam.put("CUST_ID", conParams.getString("CUST_ID"));
        checkParam.put("EPARCHY_CODE", conParams.getString("EPARCHY_CODE"));
        checkParam.put("PRODUCT_ID", conParams.getString("PRODUCT_ID"));
        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 集团用户BBOSS界面规则验证
     * 
     * @author liuxx3
     * @Date 2014-06-23
     */
    public void checkBBossProInfoRule() throws Exception
    {

        IData conParams = getData();

        String operType = conParams.getString("OPER_TYPE");
        String groupId = conParams.getString("GROUP_ID");
        String productIdList = conParams.getString("PRODUCT_ID_LIST").toString();
        String currentProduct = conParams.getString("CURRENT_PRODUCT");

        IData checkParam = new DataMap();

        checkParam.put("OPER_TYPE", operType);
        checkParam.put("GROUP_ID", groupId);
        checkParam.put("PRODUCT_ID_LIST", new DataMap(productIdList));
        checkParam.put("PRODUCT_ID", currentProduct);
        checkParam.put("CHK_FLAG", GroupRuleConst.BBoss);

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

    /**
     * 集团用户开户ProductInfo界面规则验证
     * 
     * @throws Exception
     */
    public void checkProductInfoRule() throws Exception
    {

        IData conParams = getData();
        String productId = conParams.getString("PRODUCT_ID");
        String custId = conParams.getString("CUST_ID");
        String sn = conParams.getString("SERIAL_NUMBER");
        String acctId = conParams.getString("ACCT_ID");
        IData checkParam = new DataMap();

        String allselectedElements = conParams.getString("ALL_SELECTED_ELEMENTS");
        checkParam.put("ALL_SELECTED_ELEMENTS", new DatasetList(allselectedElements));
        String grpPackageList = conParams.getString("SELECTED_GRPPACKAGE_LIST");
        if (StringUtils.isNotBlank(grpPackageList) && !grpPackageList.equals("[]"))
        {
            checkParam.put("SELECTED_GRPPACKAGE_LIST", new DatasetList(grpPackageList));
        }
        checkParam.put("CHK_FLAG", "ProductInfo");
        checkParam.put("SELECTED_USER_ID", "-1");
        checkParam.put("POWER100_INFO", conParams.getString("POWER100_PRODUCT_INFO"));
        checkParam.put("EPARCHY_CODE", conParams.getString("EPARCHY_CODE"));
        checkParam.put("BUSI_TYPE", conParams.getString("BUSI_TYPE", ""));
        checkParam.put("CUST_ID", custId);
        checkParam.put("PRODUCT_ID", productId);
        checkParam.put("SERIAL_NUMBER", sn);
        checkParam.put("ACCT_ID",acctId);
        // checkParam.put("POWER100_PRODUCT_SIZE", conParams.getString("POWER100_PRODUCT_SIZE",""));

        IDataset ruleResults = CSViewCall.call(this, "CS.chkGrpUserOpen", checkParam);

        if (IDataUtil.isNotEmpty(ruleResults))
        {
            IData ruleResult = ruleResults.getData(0);
            if (IDataUtil.isNotEmpty(ruleResult))
                this.setAjax(ruleResult);
        }

    }

}
