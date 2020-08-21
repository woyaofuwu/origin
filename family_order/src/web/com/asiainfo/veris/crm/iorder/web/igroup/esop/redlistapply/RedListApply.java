package com.asiainfo.veris.crm.iorder.web.igroup.esop.redlistapply;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.web.igroup.esop.esopcommon.EopBasePage;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcintf.datainfo.uca.UCAInfoIntf;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.datainfo.uca.UCAInfoIntfViewUtil;
import com.asiainfo.veris.crm.order.web.frame.csview.common.svcutil.upc.UpcViewCall;

public abstract class RedListApply extends EopBasePage {

    public abstract void setPattr(IData pattr);

    public abstract void setInfo(IData info);

    public abstract void setPattrs(IDataset pattrs);

    public abstract void setProductInfo(IData productInfo);

    public void qryLineInfos(IRequestCycle cycle) throws Exception {
        IData param = getData();
        String isRed = param.getString("IS_RED");
        IData info = new DataMap();
        info.put("IS_RED", isRed);
        setInfo(info);

        //查询专线信息
        IData inparam = new DataMap();
        String groupId = param.getString("GROUP_ID");
        inparam.put("GROUP_ID", groupId);
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inparam.put("PRODUCT_NO", param.getString("PRODUCT_NO"));
        inparam.put("IS_RED", isRed);
        IDataset lineInfos = CSViewCall.call(this, "SS.GrpLineInfoQrySVC.qryLineInfosForRedList", param);
        setPattrs(lineInfos);
        //查询集团信息
        IData group = null;
        if(StringUtils.isNotBlank(groupId)) {
            group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpId(this, groupId);
        } else {
            if(DataUtils.isNotEmpty(lineInfos)) {
                String userId = lineInfos.first().getString("USER_ID");
                IData data = UCAInfoIntf.qryGrpUserInfoByUserId(this, userId);
                if(DataUtils.isNotEmpty(data)) {
                    String custId = data.getString("CUST_ID");
                    group = UCAInfoIntfViewUtil.qryGrpCustInfoByGrpCustId(this, custId);
                }
            }
        }
        setGroupInfo(group);

    }

    @Override
    public void buildOtherSvcParam(IData param) throws Exception {
        String serialNumbers = param.getString("SERIAL_NUMBERS");
        String[] allSN = serialNumbers.split(";");
        IDataset attrList = new DatasetList();
        //去除重复号码
        Set<String> attrSet = new HashSet<String>();
        Collections.addAll(attrSet, allSN);

        //添加号码进ATTR表
        Iterator<String> it = attrSet.iterator();
        int i = 0;
        while(it.hasNext()){
            IData attrData = new DataMap();
            attrData.put("ATTR_CODE", "SERIAL_NUMBER");
            attrData.put("ATTR_VALUE", it.next());
            attrData.put("ATTR_NAME", "用户号码");
            attrData.put("RECORD_NUM", ++i);
            attrList.add(attrData);
        }

        param.put("CUSTOM_ATTR_LIST", attrList);

        IData commData = param.getData("COMMON_DATA");
        String productId = commData.getString("PRODUCT_ID");
        IData offerData = new DataMap();
        offerData.put("OFFER_CODE", productId);
        offerData.put("OFFER_NAME", UpcViewCall.queryOfferNameByOfferId(this, "P", productId));
        param.put("OFFER_DATA", offerData);
    }
    
}
