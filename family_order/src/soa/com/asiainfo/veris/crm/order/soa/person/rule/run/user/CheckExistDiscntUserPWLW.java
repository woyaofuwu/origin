package com.asiainfo.veris.crm.order.soa.person.rule.run.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UPackageElementInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * BUG20200508152354拦截PBOSS长周期套餐可取消问题
 * @author 梁端刚
 * @version V1.0
 * @date 2020/5/9 16:36
 */
public class CheckExistDiscntUserPWLW extends BreBase implements IBREScript {
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
        String brandCode = databus.getString("BRAND_CODE");
        if(!"PWLW".equals(brandCode)){
            return  false;
        }
        String userId = databus.getString("USER_ID");
        IDataset userDincnts= UserDiscntInfoQry.getAllValidDiscntByUserId(userId);
        if(IDataUtil.isEmpty(userDincnts)){
            return false;
        }
        for (int i = 0; i < userDincnts.size(); i++) {
            IData userDincnt = userDincnts.getData(i);
            if(StringUtils.isBlank(userDincnt.getString("DISCNT_CODE"))){
                continue;
            }
            IDataset qryBycompara = CommparaInfoQry.getCommparaAllColByParser("CSM", "9013", userDincnt.getString("DISCNT_CODE"), "0898");
            if(IDataUtil.isEmpty(qryBycompara)){
                continue;
            }
            IData enableData = UPackageElementInfoQry.queryElementEnableMode(userDincnt.getString("PRODUCT_ID"), userDincnt.getString("PACKAGE_ID")
                    , userDincnt.getString("DISCNT_CODE"), "D");
            if("7".equals(enableData.getString("CANCEL_TAG"))){
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "202000511", userDincnt.getString("DISCNT_NAME")+"未到元素结束日期不能销户!");
                break;
            }
        }
        return false;
    }
}
