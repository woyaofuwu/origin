
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpressnobind;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressNoBindException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.acct.AcctConsignInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;

public class IpExpressNoBindBean extends CSBizBean
{
    public IData getIpInfo(IData userInfo) throws Exception
    {
        IData returndata = new DataMap();
        String serialNumber = userInfo.getString("SERIAL_NUMBER");
        if (!serialNumber.substring(0, 4).equals(CSBizBean.getTradeEparchyCode()))
        {
            CSAppException.apperr(IpExpressNoBindException.CRM_IPEXPRESSNOBIND_1, CSBizBean.getTradeEparchyCode());
        }
        IDataset relaUUInfos = RelaUUInfoQry.getRelatsBySNB(serialNumber);
        if (IDataUtil.isNotEmpty(relaUUInfos))
        {
            IData relaUUInfo = relaUUInfos.getData(0);
            String strRelationCode = relaUUInfo.getString("RELATION_TYPE_CODE");
            String roleCodeB = relaUUInfo.getString("ROLE_CODE_B", "");
            if ((strRelationCode.equals("52") && roleCodeB.equals("2")) || strRelationCode.equals("50"))
            {
                CSAppException.apperr(IpExpressNoBindException.CRM_IPEXPRESSNOBIND_2);
            }
        }
        String userId = "";
        IData user = UcaInfoQry.qryUserInfoBySn(serialNumber);
        if (IDataUtil.isNotEmpty(user))
            userId = user.getString("USER_ID");
        // 号码类型
        IDataset snType = CommparaInfoQry.getOnlyByAttr("CSM", "4", CSBizBean.getTradeEparchyCode());

        // 产品查询
//        IDataset products = ProductInfoQry.queryMainProductByBrand("IP02", CSBizBean.getTradeEparchyCode());
           IDataset products = UProductInfoQry.getProductListByBRAND("IP02");
        returndata.put("IP_SN_TYPE", snType);
        returndata.put("IP_PRODUCT", products);

        // 查询已绑定号码
        if (StringUtils.isNotBlank(userId))
        {
            IDataset bindInfos = RelaUUInfoQry.getRelaFKByUserIdB(userId, "52", "1");
            if (IDataUtil.isEmpty(bindInfos))
            {
                CSAppException.apperr(IpExpressNoBindException.CRM_IPEXPRESSNOBIND_3);
            }
            else
            {
                if (bindInfos.size() > 0)
                {
                    IData bindInfo = bindInfos.getData(0);
                    String userIdA = bindInfo.getString("USER_ID_A");

                    // 获取固定号码信息
                    IDataset fixSnInfos = RelaUUInfoQry.getRelaUUInfoByUserIda(userIdA, "52", null);
                    IDataset tempInfos = new DatasetList();
                    for (int i = 0, size = fixSnInfos.size(); i < size; i++)
                    {
                        IData fixSnInfo = fixSnInfos.getData(i);
                        String userIdB = fixSnInfo.getString("USER_ID_B");

                        IData info = UcaInfoQry.qryUserInfoByUserId(userIdB);
                        UcaData ud = UcaDataFactory.getUcaByUserId(userIdB);
                        info.put("OLD_BRAND_CODE", ud.getBrandCode());
                        info.put("OLD_PRODUCT_ID", ud.getProductId());
                        info.put("OLD_PWD", info.getString("USER_PASSWD"));
                        info.put("TEMP_PWD", info.getString("USER_PASSWD"));
                        info.put("OLD_SERIALNUMBER", info.getString("SERIAL_NUMBER"));
                        info.put("M_DEAL_TAG", "4");
                        info.put("USER_ID_B", userIdB);
                        info.put("CONDITIONM", "未修改");
                        // 获取固定号码服务信息
                        IDataset svcInfos = UserSvcInfoQry.qryUserSvcByUserId(userIdB);
                        if (svcInfos == null || svcInfos.size() == 0)
                            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_6);
                        String serviceStr = "", serviceStrTemp = "", packageSvc = "", packageSvcTemp = "";
                        for (int j = 0, svcSize = svcInfos.size(); j < svcSize; j++)
                        {
                            IData tempService = (IData) svcInfos.get(j);
                            serviceStrTemp = tempService.getString("SERVICE_ID");
                            packageSvcTemp = tempService.getString("PACKAGE_ID");
                            serviceStr += serviceStrTemp + "@";
                            packageSvc += packageSvcTemp + "@" + serviceStrTemp + "~";
                        }
                        info.put("INSTSVC", svcInfos.getData(0).getString("INST_ID", ""));
                        info.put("PACKAGESVC", packageSvc);
                        info.put("IPServiceText", serviceStr);
                        info.put("OLD_IPServiceText", serviceStr);
                        tempInfos.add(info);
                    }
                    returndata.put("BIND_IPPHONE", tempInfos);
                }
            }
            UcaData uca = UcaDataFactory.getUcaByUserId(userId);
            IData custInfo = uca.getCustomer().toData();
            IData custPersonInfo = uca.getCustPerson().toData();
            custInfo.put("USER_TYPE_CODE", uca.getUser().getUserTypeCode());
            custInfo.putAll(custPersonInfo);
            IData acctInfo = uca.getAccount().toData();

            IDataset acctConsignInfos = AcctConsignInfoQry.getConsignInfoByAcctId(uca.getAcctId());
            if (DataSetUtils.isNotBlank(acctConsignInfos))
            {
                IData acctConsignInfo = acctConsignInfos.getData(0);
                acctInfo.putAll(acctConsignInfo);
            }
            returndata.put("custInfo", custInfo);
            returndata.put("acctInfo", acctInfo);
        }

        // 配置参数查询
        IData commInfo = new DataMap();
        // 获取绑定IP电话上限
        IDataset tempDataset = TagInfoQry.getTagInfosByTagCode(CSBizBean.getTradeEparchyCode(), "CS_NUM_IPPHONECOUNTLIMIT", "CSM", "0");
        String limitNum = (tempDataset != null && tempDataset.size() > 0) ? tempDataset.getData(0).getString("TAG_NUMBER", "5") : "5";
        commInfo.put("IPPHONECOUNTLIMIT", Integer.parseInt(limitNum));
        // 获取默认密码
        tempDataset = TagInfoQry.getTagInfosByTagCode(CSBizBean.getTradeEparchyCode(), "CS_INF_DEFALTIPPASSWD", "CSM", "0");
        String defaltPWD = (tempDataset != null && tempDataset.size() > 0) ? tempDataset.getData(0).getString("TAG_INFO", "0") : "0";
        commInfo.put("DEFALTIPPASSWD", defaltPWD);
        String setPWD = (tempDataset != null && tempDataset.size() > 0) ? tempDataset.getData(0).getString("TAG_CHAR", "") : "0";
        boolean isIPPasswd = setPWD.equals("1") ? false : true;
        commInfo.put("ISDEFALTIPPASSWD", isIPPasswd);

        String phoneLenth = "", phoneType = "";
        if (snType.size() > 0)
        {
            IData tempInfo = snType.getData(0);
            phoneLenth = tempInfo.getString("PARA_CODE2", "");
            phoneType = tempInfo.getString("PARA_CODE1", "");
        }
        commInfo.put("PHONELENTH", phoneLenth);
        commInfo.put("PHONETYPE", phoneType);
        commInfo.put("SERIAL_NUMBER_F", CSBizBean.getTradeEparchyCode());
        returndata.put("COMM_INFO", commInfo);

        return returndata;
    }

}
