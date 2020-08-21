
package com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.buildrequest;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.exception.FamilyException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.IBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.builder.impl.BaseBuilder;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.BaseReqData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.requestdata.product.extend.DiscntData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.bof.execute.service.process.LockProcess;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.FamilyCreateBean;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.FamilyTradeHelper;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyCreateReqData;
import com.asiainfo.veris.crm.order.soa.person.busi.familytrade.order.requestdata.FamilyMemberData;

public class BuildFamilyCreateReqData extends BaseBuilder implements IBuilder
{

    @Override
    public void buildBusiRequestData(IData param, BaseReqData brd) throws Exception
    {
        FamilyCreateReqData reqData = (FamilyCreateReqData) brd;
        UcaData uca = reqData.getUca();
        String familyProductId = param.getString("PRODUCT_ID");
        String familyPackageIdA = param.getString("PACKAGE_ID_A");
        String familyPackageIdB = param.getString("PACKAGE_ID_B");
        String appPackageId = param.getString("PACKAGE_ID_P");
        String discntCode = param.getString("DISCNT_CODE");
        String appDiscntCode = param.getString("APP_DISCNT_CODE");
        String mainSn = param.getString("SERIAL_NUMBER");
        String mShortCode = param.getString("SHORT_CODE");
        String tradeTypeCode = param.getString("TRADE_TYPE_CODE");

        // 主卡的
        reqData.setHomeAddress(param.getString("HOME_ADDRESS"));
        reqData.setHomeName(param.getString("HOME_NAME"));
        reqData.setHomePhone(param.getString("HOME_PHONE"));
        reqData.setShortCode(mShortCode);
        reqData.setVerifyMode(param.getString("FMY_VERIFY_MODE"));

        DiscntData discntData = new DiscntData();
        discntData.setProductId(familyProductId);
        discntData.setPackageId("-1");
        discntData.setElementId(discntCode);
        reqData.setDiscntData(discntData);

        reqData.setInTagNew(param.getString("IN_TAG_NEW"));

        if (StringUtils.isNotBlank(appDiscntCode))
        {
            DiscntData appDiscntData = new DiscntData();
            appDiscntData.setProductId(uca.getProductId());
            appDiscntData.setPackageId("-1");
            appDiscntData.setElementId(appDiscntCode);
            reqData.setAppDiscntData(appDiscntData);
        }

        // 查询已经存在的副号码信息
        IDataset haveMebList = new DatasetList();
        IDataset result = RelaUUInfoQry.qryRelaByUserIdBRelaTypeCode(uca.getUserId(), "45", null);
        if (IDataUtil.isNotEmpty(result))
        {
            IData rela = result.getData(0);
            String userIdA = rela.getString("USER_ID_A");
            IDataset mebList = RelaUUInfoQry.getUserRelationByUserIDA(userIdA,"45");
            if (IDataUtil.isNotEmpty(mebList))
            {
                haveMebList = mebList;
            }
        }

        FamilyCreateBean fCreateBean = BeanManager.createBean(FamilyCreateBean.class);

        // 成员的
        IDataset mebList = new DatasetList(param.getString("MEB_LIST", "[]"));
        if (IDataUtil.isNotEmpty(mebList))
        {
            for (int i = 0, size = mebList.size(); i < size; i++)
            {
                IData meb = mebList.getData(i);
                String tag = meb.getString("tag");
                if ("0".equals(tag))
                {
                    // 新增
                    UcaData mebUca = null;
                    String sn = meb.getString("SERIAL_NUMBER_B");
                    String shortCode = meb.getString("SHORT_CODE_B");

                    // 给成员加锁，解决成员同时提交的问题
                    LockProcess.lock(tradeTypeCode, sn);

                    // 主号码是否与副号码一致的校验
                    if (StringUtils.equals(mainSn, sn))
                    {
                        CSAppException.apperr(FamilyException.CRM_FAMILY_58);
                    }

                    // 主号短号与副号短号是否一致的校验
                    IData user = UcaInfoQry.qryUserInfoBySn(sn);
                    if (IDataUtil.isEmpty(user))
                    {
                        CSAppException.apperr(CrmUserException.CRM_USER_117, sn);
                    }
                    if (!fCreateBean.checkIsJWTUser(uca.getUserId()) && !fCreateBean.checkIsJWTUser(user.getString("USER_ID")))
                    {
                        if (StringUtils.equals(mShortCode, shortCode))
                        {
                            CSAppException.apperr(FamilyException.CRM_FAMILY_64);
                        }
                    }

                    // 相同成员号码校验
                    for (int j = 0, length = mebList.size(); j < length; j++)
                    {
                        IData tempMeb = mebList.getData(j);
                        String tempSn = tempMeb.getString("SERIAL_NUMBER_B");
                        if (StringUtils.equals(sn, tempSn) && i != j)
                        {
                            CSAppException.apperr(FamilyException.CRM_FAMILY_202001,tempSn);
                        }
                    }

                    // 相同成员短号校验
                    for (int k = 0, length = mebList.size(); k < length; k++)
                    {
                        IData tempMeb = mebList.getData(k);
                        String tempSCode = tempMeb.getString("SHORT_CODE_B");
                        if (StringUtils.equals(shortCode, tempSCode) && i != k)
                        {
                            CSAppException.apperr(FamilyException.CRM_FAMILY_202002,tempSCode);
                        }
                    }

                    // 副号码以及短号与已经存在的副号码以及短号是否一致的校验
                    if (IDataUtil.isNotEmpty(haveMebList))
                    {
                        for (int m = 0, length = haveMebList.size(); m < length; m++)
                        {
                            IData data = haveMebList.getData(m);
                            String oldShortCode = data.getString("SHORT_CODE");
                            String oldSn = data.getString("SERIAL_NUMBER_B");
                            //如果短号为空，则查询是否是月底失效的成员
                            /*if (StringUtils.isBlank(oldShortCode)) 
                            {
                            	oldShortCode = data.getString("RSRV_STR2");
							}*/
                            //Modify yanwu 月底失效成员短号改成立即失效，关系和优惠不变
                            if( !"".equals(oldShortCode) ){
                            	if (StringUtils.equals(shortCode, oldShortCode))
                                {
                                    CSAppException.apperr(FamilyException.CRM_FAMILY_65);
                                }
                            }
                            if (StringUtils.equals(sn, oldSn))
                            {
                                CSAppException.apperr(FamilyException.CRM_FAMILY_66);
                            }
                        }
                    }

                    mebUca = UcaDataFactory.getNormalUca(sn);

                    FamilyMemberData mebData = new FamilyMemberData();
                    mebData.setModifyTag(BofConst.MODIFY_TAG_ADD);
                    mebData.setUca(mebUca);

                    DiscntData mebDiscntData = new DiscntData();
                    mebDiscntData.setProductId(familyProductId);
                    mebDiscntData.setPackageId("-1");
                    mebDiscntData.setElementId(meb.getString("DISCNT_CODE_B"));
                    mebData.setDiscntData(mebDiscntData);

                    if (StringUtils.isNotBlank(meb.getString("APP_DISCNT_CODE_B")))
                    {
                        DiscntData mebAppDiscntData = new DiscntData();
                        mebAppDiscntData.setProductId(uca.getProductId());
                        mebAppDiscntData.setPackageId(FamilyTradeHelper.getDiscntPackageId(uca.getProductId(), meb.getString("APP_DISCNT_CODE_B")));
                        mebAppDiscntData.setElementId(meb.getString("APP_DISCNT_CODE_B"));
                        mebData.setAppDiscntData(mebAppDiscntData);
                    }

                    mebData.setNickName(meb.getString("NICE_NAME_B"));
                    mebData.setMemberKind(meb.getString("MEMBER_KIND_B"));
                    mebData.setMemberRole(meb.getString("MEMBER_ROLE_B"));
                    mebData.setShortCode(meb.getString("SHORT_CODE_B"));

                    mebData.setMebVerifyMode(meb.getString("MEB_VERIFY_MODE"));

                    reqData.addMemberData(mebData);
                }
                else if ("1".equals(tag))
                {
                    // 不支持
                }
            }
        }
    }

    @Override
    public BaseReqData getBlankRequestDataInstance()
    {
        return new FamilyCreateReqData();
    }

}
