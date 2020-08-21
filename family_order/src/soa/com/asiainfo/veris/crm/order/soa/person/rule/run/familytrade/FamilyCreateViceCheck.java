
package com.asiainfo.veris.crm.order.soa.person.rule.run.familytrade;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bre.base.BreBase;
import com.asiainfo.veris.crm.order.soa.frame.bre.databus.BreRuleParam;
import com.asiainfo.veris.crm.order.soa.frame.bre.script.IBREScript;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreFactory;
import com.asiainfo.veris.crm.order.soa.frame.bre.tools.BreTipsHelp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyCreateBean;

public class FamilyCreateViceCheck extends BreBase implements IBREScript
{

    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    private IData getCommpara(String eparchyCode) throws Exception
    {
        IData rtParam = new DataMap();
        String paramCode = "FAMILYNETLIMIT";
        int subCountLim = 6;// 默认是六次
        IDataset result = CommparaInfoQry.getCommparaAllCol("CSM", "1010", paramCode, eparchyCode);

        if (IDataUtil.isNotEmpty(result))
        {
            IData commpara = result.getData(0);
            subCountLim = commpara.getInt("PARA_CODE1");
        }

        rtParam.put("FMY_SUB_COUNT_LIM", subCountLim);

        return rtParam;
    }

    @Override
    public boolean run(IData databus, BreRuleParam ruleParam) throws Exception
    {
        String xChoiceTag = databus.getString("X_CHOICE_TAG", "");
        int currViceCount = 0;// 当前副卡数量
        int subCountLim = 0;// 副卡数量限制

        if (StringUtils.isBlank(xChoiceTag) || "1".equals(xChoiceTag))
        {
            // 提交时校验
            UcaData mainUca = (UcaData) databus.get("UCADATA");
            IData reqData = databus.getData("REQDATA");
            String userId = mainUca.getUserId();

            IDataset memberList = new DatasetList(reqData.getString("MEB_LIST", "[]"));
            if (IDataUtil.isEmpty(memberList))
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 830005, "没有得到待操作的数据");
            }

            // 获取当前成员数量
            IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(userId, "45", null);
            if (IDataUtil.isNotEmpty(result))
            {
                IData rela = result.getData(0);
                String userIdA = rela.getString("USER_ID_A");
                IDataset mebList = RelaUUInfoQry.getFamilyMebByUserIdA(userIdA);
                currViceCount = mebList.size();
            }

            // 获取成员数量限制
            IData para = getCommpara(mainUca.getUserEparchyCode());
            subCountLim = para.getInt("FMY_SUB_COUNT_LIM");

            // 监务通用户不能办理短号
            IDataset tempMemberList = new DatasetList();
            String shortCode = reqData.getString("SHORT_CODE", "");
            FamilyCreateBean bean = BeanManager.createBean(FamilyCreateBean.class);
            boolean isJwt = bean.checkIsJWTUser(userId);
            if (isJwt && StringUtils.isNotBlank(shortCode))
            {
                reqData.put("SHORT_CODE", "");// 设置为空
            }

            for (int i = 0, size = memberList.size(); i < size; i++)
            {
                IData member = memberList.getData(i);
                String modifyTag = member.getString("tag");
                if (BofConst.MODIFY_TAG_ADD.equals(modifyTag))
                {
                    String sn = member.getString("SERIAL_NUMBER_B");
                    IData checkParam = new DataMap();
                    checkParam.put("SERIAL_NUMBER", mainUca.getSerialNumber());
                    checkParam.put("SERIAL_NUMBER_B", member.getString("SERIAL_NUMBER_B"));
                    bean.checkAddMeb(checkParam);

                    // 监务通用户不能办理短号
                    String mebShortCode = member.getString("SHORT_CODE_B", "");
                    IData user = UcaInfoQry.qryUserInfoBySn(sn);
                    if (IDataUtil.isEmpty(user))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
                    }
                    String mebUserId = user.getString("USER_ID");
                    boolean isJwtMeb = bean.checkIsJWTUser(mebUserId);
                    if (isJwtMeb && StringUtils.isNotBlank(mebShortCode))
                    {
                        member.put("SHORT_CODE_B", "");// 设置为空
                    }

                    tempMemberList.add(member);

                    currViceCount++;
                }
            }

            // 重新设置成员列表
            reqData.put("MEB_LIST", tempMemberList);
            // 覆盖REQDATA
            databus.put("REQDATA", reqData);

            // 副卡上限判断
            if (currViceCount > subCountLim)
            {
                BreTipsHelp.addNorTipsInfo(databus, BreFactory.TIPS_TYPE_ERROR, 830019, "当前成员个数已达上限：" + subCountLim + "个");
            }
        }

        return true;
    }

}
