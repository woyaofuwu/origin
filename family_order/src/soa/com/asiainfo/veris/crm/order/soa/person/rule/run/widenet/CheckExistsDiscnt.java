package com.asiainfo.veris.crm.order.soa.person.rule.run.widenet;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

/**
 * 宽带拆机校验是否存在指定的优惠
 * @author 梁端刚
 * @version V1.0
 * @date 2020/4/18 16:52
 */
public class CheckExistsDiscnt extends BreBase implements IBREScript {
    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {
        String serialNumber = databus.getString("SERIAL_NUMBER").replace("KD_", "");
        IData userInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if(IDataUtil.isNotEmpty(userInfo)){
            String userId = userInfo.getString("USER_ID");
            IDataset userDincnts= UserDiscntInfoQry.getAllDiscntInfo1(userId);
            if(IDataUtil.isEmpty(userDincnts)){
                return false;
            }
            IDataset commparaInfos9924 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM","9924","DISCNT",null);
            if(IDataUtil.isEmpty(commparaInfos9924)){
                return false;
            }
            IData params=new DataMap();
            for (int i = 0; i < commparaInfos9924.size(); i++) {
                String discntCode = commparaInfos9924.getData(i).getString("PARA_CODE1");
                String configTag = commparaInfos9924.getData(i).getString("PARA_CODE2");
                String errorInfo = commparaInfos9924.getData(i).getString("PARA_CODE24");
                if("0".equals(configTag)){
                    for (int j = 0; j < userDincnts.size(); j++) {
                        String userDiscntCode=userDincnts.getData(j).getString("DISCNT_CODE");
                        if(discntCode.equals(userDiscntCode)){
                            BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020041801", errorInfo);
                        }
                    }
                }else if("1".equals(configTag)){
                    if("ZNZW".equals(discntCode)){
                        checkExistsZNZW( userDincnts, databus);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 智能组网校验
     * @param userDincnts
     * @param databus
     * @throws Exception
     */
    private void checkExistsZNZW(IDataset userDincnts,IData databus) throws Exception{
        if(IDataUtil.isEmpty(userDincnts)){
            return;
        }
        IDataset commparaInfos9211 = CommparaInfoQry.queryComparaByAttrAndCode1("CSM", "9211", null, null);
        if(IDataUtil.isEmpty(commparaInfos9211)){
            return;
        }
        for (int j = 0; j < userDincnts.size(); j++) {
            String userDiscntCode=userDincnts.getData(j).getString("DISCNT_CODE");
            for (int z = 0; z < commparaInfos9211.size(); z++) {
                if(commparaInfos9211.getData(z).getString("PARAM_CODE").equals(userDiscntCode)){
                    if(StringUtils.isNotBlank(commparaInfos9211.getData(z).getString("PARA_CODE24"))){
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, "2020041801", commparaInfos9211.getData(z).getString("PARA_CODE24"));
                    }
                }
            }
        }
    }


}
