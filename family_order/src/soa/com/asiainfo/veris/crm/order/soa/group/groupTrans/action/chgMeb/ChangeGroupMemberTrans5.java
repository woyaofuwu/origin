
package com.asiainfo.veris.crm.order.soa.group.groupTrans.action.chgMeb;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.exception.VpmnUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.group.groupTrans.ChangeGroupMemberTransHAIN;

public class ChangeGroupMemberTrans5 extends ChangeGroupMemberTransHAIN
{

    // 子类重载
    protected void addSubDataBefore(IData idata) throws Exception
    {
        transChangeVpmnGroupMemberRequestData(idata);
    }

    // 子类重载
    protected void addSubDataAfter(IData idata) throws Exception
    {
    }

    private void transChangeVpmnGroupMemberRequestData(IData data) throws Exception
    {
        checkRequestData5(data);

        IDataset discntList = new DatasetList();

        String discntCodeOld = IDataUtil.getMandaData(data, "OLD_DISCNT_CODE");// 原有优惠编码
        String discntCodeNew = IDataUtil.getMandaData(data, "NEW_DISCNT_CODE");// 新优惠编码
        String effectTime = IDataUtil.getMandaData(data, "EFFECT_TIME");// 生效类型：0.立即生效 1.下月生效

        String effectNow = "false";
        if ("0".equals(effectTime)) // 立即生效
        {
            effectNow = "true";
        }

        if (StringUtils.isNotBlank(discntCodeOld))
        {
            if(StringUtils.isNotBlank(discntCodeNew)){
                String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
                IData mebInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
                if(IDataUtil.isNotEmpty(mebInfo)){
                    String userId = mebInfo.getString("USER_ID","");
                   //3、5、8元套餐变更时，办理的新套餐，如果用户已经有则拦截提示
                    IDataset mebDiscntInfo = UserDiscntInfoQry.queryVpmnUserDiscntByUserId(userId,discntCodeNew);
                    if(IDataUtil.isNotEmpty(mebDiscntInfo)){
                        //拦截提示
                        CSAppException.apperr(VpmnUserException.VPMN_USER_227, discntCodeNew);
                    }
                }
            }
            
            String[] oldDiscntCode = discntCodeOld.split(",");
            for (int i = 0, size = oldDiscntCode.length; i < size; i++)
            {
                IData discntOld = new DataMap();
                discntOld.put("MODIFY_TAG", "1");
                discntOld.put("DISCNT_CODE", oldDiscntCode[i]);
                discntList.add(discntOld);
            }
        }

        IData discntNew = new DataMap();
        discntNew.put("MODIFY_TAG", "0");
        discntNew.put("DISCNT_CODE", discntCodeNew);
        discntList.add(discntNew);

        //特殊处理358未生效的资费
        dealNextAcctEffectDiscnt(data, discntList);
        
        data.put("LIST_INFOS", discntList);

        data.put("EFFECT_NOW", effectNow);
    }

    public void checkRequestData5(IData data) throws Exception
    {
        String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String serialNumberA = data.getString("SERIAL_NUMBER_A");// 集团sn
        // 如果serialNumberA不传,则根据serialNumber查询userida,只支持8000
        if (StringUtils.isEmpty(serialNumberA))
        {
            IDataset vpmnInfos = RelaUUInfoQry.queryVpmnRelaBySn(serialNumber);
            if (IDataUtil.isEmpty(vpmnInfos))
                CSAppException.apperr(VpmnUserException.VPMN_USER_144, serialNumber);
            String userIdA = vpmnInfos.getData(0).getString("USER_ID_A");
            data.put("USER_ID_A", userIdA);
        }

    }
    
    /**
     * 处理下账期生效的358资费
     * @throws Exception
     */
    public void dealNextAcctEffectDiscnt(IData data, IDataset discntList) throws Exception
    {
        String serialNumber = IDataUtil.getMandaData(data, "SERIAL_NUMBER");
        String user_id_a = data.getString("USER_ID_A");
        
        IData mebInfo = UcaInfoQry.qryUserInfoBySn(serialNumber);
        
        if (IDataUtil.isEmpty(mebInfo))
        {
            CSAppException.apperr(GrpException.CRM_GRP_713, "当前成员[" + serialNumber + "]用户信息不存在!");
        }
        
        String userId = mebInfo.getString("USER_ID");
        
        IDataset nextAcctEffectVpmn358Discnts = UserDiscntInfoQry.getUserNextEffectVpmn358Discnt(userId, user_id_a, "20");
        
        if (IDataUtil.isNotEmpty(nextAcctEffectVpmn358Discnts))
        {
            for (int i = 0, size = nextAcctEffectVpmn358Discnts.size(); i < size; i++)
            {
                IData notEffectDiscnt = new DataMap();
                notEffectDiscnt.put("MODIFY_TAG", "1");
                notEffectDiscnt.put("DISCNT_CODE", nextAcctEffectVpmn358Discnts.getData(i).getString("DISCNT_CODE"));
                discntList.add(notEffectDiscnt);
            }
        }
    }

}
