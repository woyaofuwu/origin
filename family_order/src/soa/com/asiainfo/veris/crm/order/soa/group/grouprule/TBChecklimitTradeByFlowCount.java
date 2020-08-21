package com.asiainfo.veris.crm.order.soa.group.grouprule;

import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.ProvinceUtil;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;

public class TBChecklimitTradeByFlowCount extends BreBase implements IBREScript {

	private static Logger logger = Logger.getLogger(TBChecklimitTradeByFlowCount.class);
	public boolean run(IData databus, BreRuleParam ruleParam) throws Exception {    
        if (logger.isDebugEnabled())
            logger.debug(" >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> 进入 TBChecklimitTradeByFlowCount() >>>>>>>>>>>>>>>>>>");

        boolean bResult = false;  
        boolean isNewFlowDistinc = false;
        if (ProvinceUtil.isProvince(ProvinceUtil.HAIN)){
            long folwCount = 0L;
            IDataset allSelectedElement = databus.getDataset("ALL_SELECTED_ELEMENTS");
            if(null!=allSelectedElement&&allSelectedElement.size()>0){
                for(int i=0;i<allSelectedElement.size();i++){
                    IDataset attrParam = allSelectedElement.getData(i).getDataset("ATTR_PARAM");
                    String modifyTag = allSelectedElement.getData(i).getString("MODIFY_TAG");
                    if(null!=attrParam&&attrParam.size()>0){
                        for(int t=0;t<attrParam.size();t++){
                            String attrCode = attrParam.getData(t).getString("ATTR_CODE");
                            if(("7361".equals(attrCode))&&("0".equals(modifyTag)||"2".equals(modifyTag))){//即将订购的新套餐,或者修改后的套餐
                                isNewFlowDistinc = true;//比如：[73615]本省流量卡单卡1000元
                                folwCount = Long.parseLong(attrParam.getData(t).getString("ATTR_VALUE"));//获取订购的流量份数
                                break;
                            }
                        }
                    }
                }
                if(isNewFlowDistinc){
                    String grpUserId = databus.getString("USER_ID");
                    IDataset gfspRela = RelaUUInfoQry.getRelaCoutByPK(grpUserId, "T5");//T5:流量自由充(全量统付)
                    int gfspAllNum = gfspRela.getData(0).getInt("RECORDCOUNT");
                    if(folwCount<gfspAllNum){
                        StringBuilder strError = new StringBuilder("业务受理前条件判断:").append("订购流量池单卡的份数必须大于成员的数量！"+"当前成员数量为"+gfspAllNum);
                        BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 20150817, strError.toString());
                    }
                }
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(" <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< 退出 TBChecklimitTradeByFlowCount() " + bResult + "<<<<<<<<<<<<<<<<<<<");
    
        return bResult;   
    }

}
