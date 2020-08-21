
package com.asiainfo.veris.crm.order.soa.person.rule.run.familytrade;

import java.util.List;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.tradedata.extend.DiscntTradeData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyTradeBean;

public class FamilyTradeViceCheck extends BreBase implements IBREScript
{

    private IData getCommpara(String brandCode, String productId, String eparchyCode) throws Exception
    {
        IData rtParam = new DataMap();
        String paramCode = "FAMILYCNLIMIT_" + brandCode;
        int subCountLim = 0;
        int modiTimesLim = 0;
        IDataset result = CommparaInfoQry.getCommparaInfoBy5("CSM", "1010", paramCode, productId, eparchyCode, null);
        if (IDataUtil.isEmpty(result))
        {
            result = CommparaInfoQry.getCommparaAllCol("CSM", "1010", paramCode, eparchyCode);
        }

        if (IDataUtil.isNotEmpty(result))
        {
            IData commpara = result.getData(0);
            subCountLim = commpara.getInt("PARA_CODE2");
            modiTimesLim = commpara.getInt("PARA_CODE3");
        }

        rtParam.put("FMY_SUB_COUNT_LIM", subCountLim);
        rtParam.put("FMY_MODIFY_TIMES_LIM", modiTimesLim);

        return rtParam;
    }

    private int getCurMonthModifyTimes(IData databus) throws Exception
    {
        int curMonthModifyTimes = 0;
        IDataset userOtherList = databus.getDataset("TF_F_USER_OTHER");
        for (int i = 0, size = userOtherList.size(); i < size; i++)
        {
            IData userOther = userOtherList.getData(i);

        }

        return curMonthModifyTimes;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        boolean havaDel = false;
        int currViceCount = 0;
        int subCountLim = 0;// 副卡数量限制
        int modiTimes = 0;// 当前修改次数
        int modiTimesLim = 0;// 修改次数限制

        if (StringUtils.isBlank(xChoiceTag) || "1".equals(xChoiceTag))
        {
            // 提交时校验
            UcaData mainUca = (UcaData) databus.get("UCADATA");
            IData reqData = databus.getData("REQDATA");

            IDataset memberList = new DatasetList(reqData.getString("MEB_LIST", "[]"));
            if (IDataUtil.isEmpty(memberList))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 830005, "没有得到待操作的数据");
            }

            // 已修改次数
            String productId = mainUca.getProductId();
            IDataset userOther = databus.getDataset("TF_F_USER_OTHER");
            if (null != userOther && userOther.size() > 0)
            {
                for (int i = 0, size = userOther.size(); i < size; i++)
                {
                    IData uo = userOther.getData(i);
                    String rsrvValue = uo.getString("RSRV_VALUE", "");
                    String rsrvValueCode = uo.getString("RSRV_VALUE_CODE", "");
                    String rsrvStr1 = uo.getString("RSRV_STR1", "");
                    if (productId.equals(rsrvValue) && "1399".equals(rsrvValueCode) && "HAIN".equals(rsrvStr1))
                    {
                        modiTimes = uo.getInt("RSRV_STR2", 0);
                        break;
                    }
                }
            }

            IData para = getCommpara(mainUca.getBrandCode(), mainUca.getProductId(), mainUca.getUserEparchyCode());
            subCountLim = para.getInt("FMY_SUB_COUNT_LIM");
            modiTimesLim = para.getInt("FMY_MODIFY_TIMES_LIM");

            String discntCode = FamilyTradeBean.getDiscntCodeByBrandCode(mainUca.getBrandCode(), mainUca.getProductId());
            List<DiscntTradeData> userDiscnts = mainUca.getUserDiscntByDiscntId(discntCode);
            String userIdA = "";
            if (null != userDiscnts && userDiscnts.size() > 0)
            {
                DiscntTradeData userDiscnt = userDiscnts.get(0);

                if (StringUtils.isNotBlank(userDiscnt.getUserIdA()) && !"-1".equals(userDiscnt.getUserIdA()))
                {
                    userIdA = userDiscnt.getUserIdA();
                }
            }

            if (StringUtils.isNotBlank(userIdA))
            {
                IDataset mebList = RelaUUInfoQry.getUserRelationAll(userIdA, "75");
                if (mebList.size() > 0)
                {
                    currViceCount = mebList.size() - 1;// -1是因为这个SQL的结果包含了主卡的，这个主要用于判断是否亲情下副卡全部删除了
                }
            }

            for (int i = 0, size = memberList.size(); i < size; i++)
            {
                IData member = memberList.getData(i);
                String modifyTag = member.getString("tag");
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    FamilyTradeBean bean = BeanManager.createBean(FamilyTradeBean.class);
                    IData checkParam = new DataMap();
                    checkParam.put("SERIAL_NUMBER", mainUca.getSerialNumber());
                    checkParam.put("SERIAL_NUMBER_B", member.getString("SERIAL_NUMBER_B"));
                    // 将页面成员tab数据剔除当前副号的其他成员传给checkAddMeb进行校验，防止成员自己与自己比较
                    IDataset tempMebList = new DatasetList();
                    for (int j = 0, length = memberList.size(); j < length; j++)
                    {
                        if (i == j)
                            continue;
                        IData tempMeb = memberList.getData(j);
                        tempMebList.add(tempMeb);
                    }
                    checkParam.put("MEB_LIST", tempMebList);
                    bean.checkAddMeb(checkParam);
                    currViceCount++;
                }
                else if (BofConst.MODIFY_TAG_DEL.equals(modifyTag))
                {
                    currViceCount--;
                    havaDel = true;
                }
            }

            // 副卡上限判断
            if (currViceCount > subCountLim)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 830019, "您的亲情号码个数已达上限" + subCountLim + ",不能再新增亲情号码。");
            }

            // 修改次数上限的判断
            if (havaDel && modiTimes + 1 > modiTimesLim)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 830020, "您本月的亲情号码业务修改次数已达上限(" + modiTimesLim + "次),请下月再修改！");
            }
        }

        return true;
    }
}
