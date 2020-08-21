
package com.asiainfo.veris.crm.order.soa.group.changememelement;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.GrpException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.base.CSBaseConst.TRADE_MODIFY_TAG;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductCompInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.changememelement.ChangeMemElement;

public class ChangePocuGroupMemElement extends ChangeMemElement
{

    public ChangePocuGroupMemElement()
    {

    }

    /**
     * VPMN一些个性化参数存放到主台帐表的预留字段里
     */
    @Override
    protected void regTrade() throws Exception
    {
        super.regTrade();

        String relationTypeCode = ProductCompInfoQry.getRelaTypeCodeByProductId(reqData.getGrpUca().getProductId());

        IData data = bizData.getTrade();

        data.put("RSRV_STR1", reqData.getGrpUca().getUser().getUserId());
        data.put("RSRV_STR2", relationTypeCode);
        data.put("RSRV_STR3", reqData.getGrpUca().getSerialNumber());

    }

    /**
     * 不选中优惠也可以提交，但是需要有如下套餐param_attr = '9091'或包中的优惠 for REQ201308090009
     * 
     * @throws Exception
     * @author wangyf6
     */
    private void checkRegBeforeData() throws Exception
    {
        String userId = reqData.getUca().getUserId(); // 用户标识
        IDataset disDatasest = reqData.cd.getDiscnt();
        IDataset userDiscntList = new DatasetList();
        IData configUserDisData = new DataMap();

        String paramAttr = "9091";
        String paramCode = "1";
        String eparchyCode = "0898";
        IDataset resultData = UserDiscntInfoQry.getM2MUserDisntCommparaUnionByUserId(userId, paramAttr, paramCode, eparchyCode);
        // 用户当前有的优惠
        if (IDataUtil.isNotEmpty(resultData))
        {
            userDiscntList = DataHelper.distinct(resultData, "DISCNT_CODE", ""); // 过滤掉资费重复的

            for (int j = 0; j < userDiscntList.size(); j++)
            {
                IData eleData = userDiscntList.getData(j);
                String discnt = eleData.getString("DISCNT_CODE");
                configUserDisData.put(discnt, discnt); // 配置中用户现有的优惠
            }
        }

        // 提交时添加删除的优惠
        boolean addFlag = false;
        if (disDatasest != null && disDatasest.size() > 0)
        {
            for (int j = 0; j < disDatasest.size(); j++)
            {
                IData eleData = disDatasest.getData(j);
                if (eleData != null && eleData.size() > 0)
                {
                    String discntCode = eleData.getString("ELEMENT_ID", "");
                    String typeCode = eleData.getString("ELEMENT_TYPE_CODE", "");
                    String state = eleData.getString("MODIFY_TAG");
                    String packageId = eleData.getString("PACKAGE_ID", "");
                    if ("D".equals(typeCode) && TRADE_MODIFY_TAG.Add.getValue().equals(state))
                    {
                        addFlag = true;
                    }
                    else if ("70500102".equals(packageId) && TRADE_MODIFY_TAG.DEL.getValue().equals(state))
                    {
                        IDataset dstmp = DataHelper.filter(userDiscntList, "DISCNT_CODE=" + discntCode); // 判断“配置中现有优惠”是否存在删除的DISCNT_CODE
                        if (IDataUtil.isNotEmpty(dstmp))// 判断“配置中现有优惠”是否存在删除的DISCNT_CODE,如有则从Data中remove，最终知道是否订购相应套餐
                        {
                            configUserDisData.remove(discntCode);
                        }
                    }
                }
            }
        }

        if (!addFlag && configUserDisData.isEmpty())
        {
            // j2ee common.error("589010", "您未订购相应的数据流量套餐，不可办理M2M产品成员优惠修改,请选择优惠!");
            CSAppException.apperr(GrpException.CRM_GRP_680);
        }

        // true选择优惠时不进行拦截判断,false为不选择优惠时则进行拦截判断用户是否有相应的优惠
        if (!addFlag)
        {
            // IDataset resultDatas = dao.queryListByCodeCode("TF_F_USER_DISCNT", "SEL_BY_USERID_M2MMEMBER", param);

            IDataset resultDatas = UserDiscntInfoQry.getM2MUserDisntCommparaByUserId(userId, paramAttr, paramCode, eparchyCode);
            if (IDataUtil.isEmpty(resultDatas))
            {
                // j2ee common.error("589011", "您未订购相应的数据流量套餐，不可办理M2M产品成员修改!");
                CSAppException.apperr(GrpException.CRM_GRP_681);
            }
        }
    }
}
