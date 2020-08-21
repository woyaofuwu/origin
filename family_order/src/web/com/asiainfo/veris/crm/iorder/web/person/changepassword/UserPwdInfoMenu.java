package com.asiainfo.veris.crm.iorder.web.person.changepassword;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.web.frame.bcf.base.CSViewCall;
import com.asiainfo.veris.crm.order.web.frame.csview.person.base.PersonBasePage;
import org.apache.tapestry.IRequestCycle;

public abstract class UserPwdInfoMenu extends PersonBasePage {

    /**
     * 页面初始化加载参数
     *
     * @param cycle
     * @throws Exception
     */
    public void onInitTrade(IRequestCycle cycle) throws Exception {
        IDataset tradeTypeCodeList = new DatasetList();
        
        IData data = new DataMap();
        data.put("SUBSYS_CODE", "CSM");
        data.put("PARAM_ATTR", "0");
        data.put("PARAM_CODE", "USER_PWD_INFO_MENU");
        data.put("EPARCHY_CODE", getTradeEparchyCode());
        IDataset tradeTypes = CSViewCall.call(this, "CS.CommparaInfoQrySVC.getCommpara", data);
        
        if(DataUtils.isNotEmpty(tradeTypes)) {
        	for(int i = 0; i < tradeTypes.size(); i++) {
	            IData tradeTypeCodeItem = new DataMap();
	            tradeTypeCodeItem.put("TRADE_TYPE_CODE", tradeTypes.getData(i).getString("PARA_CODE1"));
	            tradeTypeCodeItem.put("RIGHT_CODE", tradeTypes.getData(i).getString("PARA_CODE2"));
	            tradeTypeCodeItem.put("TRADE_TYPE_NAME", tradeTypes.getData(i).getString("PARA_CODE3"));
	            tradeTypeCodeList.add(tradeTypeCodeItem);
        	}
        }
        
        // 过滤客户权限
/*        for (int i = (tradeTypeCodeList.size() - 1); i >= 0; i--) {
            String rightCode = tradeTypeCodeList.getData(i).getString("RIGHT_CODE");
            String staffId = getVisit().getStaffId();// 获取客户工号
            boolean isStaffPriv = StaffPrivUtil.isPriv(staffId, rightCode, StaffPrivUtil.PRIV_TYPE_FUNCTION);
            if (!isStaffPriv) {
                tradeTypeCodeList.remove(i);
            }
        }*/
        if (IDataUtil.isNotEmpty(tradeTypeCodeList)) {
            String tradeTypeCodeFirst = tradeTypeCodeList.first().getString("TRADE_TYPE_CODE");
            setTradeTypeCodeEmpty("full");
            setTradeTypeCodeFirst(tradeTypeCodeFirst);
        } else {
            setTradeTypeCodeEmpty("empty");
        }
    }

    public abstract void setTradeTypeCodeFirst(String TradeTypeCodeFirst);

    public abstract void setTradeTypeCodeEmpty(String TradeTypeCodeEmpty);
}
