
package com.asiainfo.veris.crm.order.soa.group.grouprule;

import com.ailk.biz.service.BizRoute;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.query.BofQuery;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAttrInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.ElementUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.common.util.GroupTradeAfterUtil;
import com.asiainfo.veris.crm.order.soa.frame.csservice.rule.BizRule;

public class CheckSelecteElementsValid extends BreBase implements IBREScript
{
    public boolean run(IData databus, BreRuleParam rule) throws Exception
    {

        String userElementsStr = databus.getString("ALL_SELECTED_ELEMENTS");
        String userId = databus.getString("SELECTED_USER_ID", "-1");
        String userIdA = databus.getString("SELECTED_USER_ID_A", "-1");
        if (StringUtils.isBlank(userElementsStr))
            return false;
        IDataset userElements = new DatasetList(userElementsStr);
        if (IDataUtil.isEmpty(userElements))
            return false;
        IDataset tradeAttrs = new DatasetList();
        IDataset tradeSvcs = new DatasetList();
        IDataset tradeDiscnts = new DatasetList();
        String tradeId = databus.getString("TRADE_ID", "");
        if (tradeId.equals(""))
            tradeId = userId;
        int size = userElements.size();
        String sysTimeString = SysDateMgr.getSysTime();
        int instidkey = 0; // 防止attr表中的INST_ID重复 ，可恶的铁通专线订购了两条资费，每条资费上都挂了相同的属性,导致instid重复了
        for (int i = 0; i < size; i++)
        {
            IData element = userElements.getData(i);

            IData ruleElement = new DataMap();
            ruleElement.put("USER_ID_A", userIdA);
            if ("0_1".equals(element.getString("MODIFY_TAG")))
            {
                continue;
            }
            if (BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                ruleElement.put("USER_ID", userId);
                ruleElement.put("DISCNT_CODE", element.getString("ELEMENT_ID"));
                ruleElement.putAll(element);
                String inst_id = ruleElement.getString("INST_ID", "");
                if (inst_id.equals(""))
                    inst_id = "" + i;
                ruleElement.put("INST_ID", inst_id);
                if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_UPD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
                {
                    ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
                    String attrparamstr = ruleElement.getString("ATTR_PARAM");
                    if (!StringUtils.isBlank(attrparamstr))
                    {
                        IDataset attrparams = new DatasetList(attrparamstr);
                        for (int k = 0; k < attrparams.size(); k++)
                        {

                            IData attrparam = attrparams.getData(k);
                            attrparam.put("USER_ID", userId);
                            attrparam.put("INST_TYPE", BofConst.ELEMENT_TYPE_CODE_DISCNT);
                            attrparam.put("RELA_INST_ID", inst_id);
                            instidkey = instidkey + 1;
                            attrparam.put("INST_ID", "" + instidkey);
                            attrparam.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
                            attrparam.put("END_DATE", element.getString("END_DATE"));
                            attrparam.put("TRADE_ID", tradeId);
                        }

                        tradeAttrs.addAll(attrparams);
                    }
                    ruleElement.put("TRADE_ID", tradeId);
                    tradeDiscnts.add(ruleElement);
                }

            }
            else if (BofConst.ELEMENT_TYPE_CODE_SVC.equals(element.getString("ELEMENT_TYPE_CODE")))
            {
                ruleElement.put("USER_ID", userId);
                ruleElement.put("SERVICE_ID", element.getString("ELEMENT_ID"));
                ruleElement.putAll(element);
                String inst_id = ruleElement.getString("INST_ID", "");
                if (inst_id.equals(""))
                    inst_id = "" + i;
                ruleElement.put("INST_ID", inst_id);

                if (BofConst.MODIFY_TAG_ADD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_UPD.equals(element.getString("MODIFY_TAG")) || BofConst.MODIFY_TAG_DEL.equals(element.getString("MODIFY_TAG")))
                {

                    ElementUtil.reCalcElementDateByNowTime(ruleElement, sysTimeString);
                    String attrparamstr = ruleElement.getString("ATTR_PARAM");
                    if (!StringUtils.isBlank(attrparamstr))
                    {
                        IDataset attrparams = new DatasetList(attrparamstr);
                        for (int k = 0; k < attrparams.size(); k++)
                        {
                            IData attrparam = attrparams.getData(k);
                            attrparam.put("USER_ID", userId);
                            attrparam.put("INST_TYPE", BofConst.ELEMENT_TYPE_CODE_SVC);
                            attrparam.put("RELA_INST_ID", inst_id);
                            instidkey = instidkey + 1;
                            attrparam.put("INST_ID", "" + instidkey);
                            attrparam.put("MODIFY_TAG", element.getString("MODIFY_TAG"));
                            attrparam.put("END_DATE", element.getString("END_DATE"));
                            attrparam.put("TRADE_ID", tradeId);
                        }

                        tradeAttrs.addAll(attrparams);
                    }
                    ruleElement.put("TRADE_ID", tradeId);
                    tradeSvcs.add(ruleElement);
                }

            }
        }

        if (IDataUtil.isNotEmpty(tradeAttrs))
        {
            int attrLen = tradeAttrs.size();
            for (int i = attrLen - 1; i >= 0; i--)
            {
                IData tradeAttr = tradeAttrs.getData(i);
                String attrCode = tradeAttr.getString("ATTR_CODE");
                if (attrCode == null)
                {
                    // 自定义弹出窗口的属性没有按照标准的ATTRCODE,ATTRVALUE格式传，不管了
                    tradeAttrs.remove(i);
                    continue;
                }
                String modifyTag = tradeAttr.getString("MODIFY_TAG", "");
                if (BofConst.MODIFY_TAG_UPD.equals(modifyTag) || BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                    String relaInstId = tradeAttr.getString("RELA_INST_ID");
                    IData userAttrInfo = UserAttrInfoQry.getUserAttrByRelaInstIdAndAttrCode(userId, relaInstId, attrCode, BizRoute.getRouteId());
                    if (IDataUtil.isNotEmpty(userAttrInfo))
                    {
                        tradeAttr.put("INST_ID", userAttrInfo.getString("INST_ID"));
                    }

                }

            }
        }
        IDataset tradeMains = new DatasetList();
        IData tradeMain = new DataMap();
        tradeMain.put("TRADE_EPARCHY_CODE", databus.getString("EPARCHY_CODE", getVisit().getStaffEparchyCode()));
        tradeMain.put("EPARCHY_CODE", databus.getString("EPARCHY_CODE", getVisit().getStaffEparchyCode()));
        tradeMain.put("TRADE_TYPE_CODE", databus.getString("TRADE_TYPE_CODE"));
        tradeMain.put("IN_MODE_CODE", "0");
        tradeMain.put("USER_ID", userId);
        tradeMains.add(tradeMain);
        IData ruleParam = new DataMap();

        ruleParam.put("TF_B_TRADE_ATTR", tradeAttrs);
        ruleParam.put("TF_B_TRADE_SVC", tradeSvcs);
        ruleParam.put("TF_B_TRADE_DISCNT", tradeDiscnts);
        ruleParam.put("TF_B_TRADE", tradeMains);
        ruleParam.put("USER_ID", userId);
        ruleParam.put("TRADE_TYPE_CODE", databus.getString("TRADE_TYPE_CODE"));

        GroupTradeAfterUtil.getTradeAfterElementData(ruleParam, ruleParam);

        //资费的属性变更时，资费开始时间的时分秒没有了，重新从资费表中取资费的开始时间填写
        boolean updateFlag = false;
        if(IDataUtil.isNotEmpty(tradeDiscnts))
        {
        	for (int m = 0, cn = tradeDiscnts.size(); m < cn; m++)
            {
        		IData discntInfos = tradeDiscnts.getData(m);
        		if(BofConst.MODIFY_TAG_UPD.equals(discntInfos.getString("MODIFY_TAG")) && 
        				BofConst.ELEMENT_TYPE_CODE_DISCNT.equals(discntInfos.getString("ELEMENT_TYPE_CODE"))){
        			updateFlag = true;
        		}
            }
        }
        
        if(updateFlag)
        {
        	IDataset userDiscntAfters = ruleParam.getDataset("TF_F_USER_DISCNT_AFTER");
        	IDataset userDiscnts = BofQuery.queryUserAllValidDiscnt(userId, BizRoute.getRouteId());
        	if(IDataUtil.isNotEmpty(userDiscntAfters))
        	{
        		for (int m = 0, cn = userDiscntAfters.size(); m < cn; m++)
                {
        			IData discntAfters = userDiscntAfters.getData(m);
        			String discntCodeAfter = discntAfters.getString("DISCNT_CODE");
        			if(IDataUtil.isNotEmpty(userDiscnts))
        			{
        				for(int j=0,sz = userDiscnts.size(); j<sz; j++)
        				{
        					IData userDiscnt = userDiscnts.getData(j);
        					String discntCode = userDiscnt.getString("DISCNT_CODE");
        					if(StringUtils.isNotBlank(discntCodeAfter) && StringUtils.isNotBlank(discntCode)
        							&& StringUtils.equals(discntCodeAfter, discntCode))
        					{
        						discntAfters.put("START_DATE", userDiscnt.getString("START_DATE"));
        						break;
        					}
        				}
        			}
                }
        	}
        }
        
        IData result = BizRule.bre4ProductLimitNeedFormat(ruleParam);
        if (IDataUtil.isNotEmpty(result))
        {
            IDataset errors = result.getDataset("TIPS_TYPE_ERROR");
            if (IDataUtil.isNotEmpty(errors))
            {
                int errorSize = errors.size();
                StringBuilder errorInfo = new StringBuilder();
                for (int i = 0; i < errorSize; i++)
                {
                    IData error = errors.getData(i);
                    errorInfo.append(error.getString("TIPS_INFO"));
                }
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 1163, errorInfo.toString());
            }
        }

        return false;
    }
}
