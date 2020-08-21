package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

/**
 * REQ202003180001 “共同战疫宽带助力”活动开发需求 add by liangdg3
 * 宽带拆机校验是否存在指定的营销活动
 * @author 梁端刚
 * @version V1.0
 * @date 2020/3/21 22:02
 */
public class CheckExistsSaleActive extends BreBase implements IBREScript {
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
        String serialNumber = databus.getString("SERIAL_NUMBER").replace("KD_", "");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
            String userId = userInfo.getString("USER_ID");
            IDataset commparaInfos9924 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9924","SALEACTIVE",null);
            if(IDataUtil.isNotEmpty(commparaInfos9924)){
                IData params=new DataMap();
                for (int i = 0; i < commparaInfos9924.size(); i++) {
                    String productId = commparaInfos9924.getData(i).getString("PARA_CODE1");
                    String packageId = commparaInfos9924.getData(i).getString("PARA_CODE2");
                    params.clear();
                    params.put("USER_ID", userId);
                    params.put("PRODUCT_ID", productId);
                    if(!StringUtils.equals("-1",packageId)){
                        params.put("PACKAGE_ID", packageId);
                    }
                    IDataset userSaleActive= Dao.qryByCodeParser("TF_F_USER_SALE_ACTIVE", "SEL_BY_PACKAGEID", params);
                    if(IDataUtil.isNotEmpty(userSaleActive)){
                        String startDate = userSaleActive.getData(0).getString("START_DATE");
                        String endDate = userSaleActive.getData(0).getString("END_DATE");
                        String packageName =commparaInfos9924.getData(i).getString("PARA_CODE3");
                        String errorInfo = "尊敬的用户，您参加的"+packageName+"活动尚未到期（"+startDate+"--"+endDate+"），无法进行拆机。";
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020032101", errorInfo);
                    }
                }
            }
        }
        return false;
    }
}
