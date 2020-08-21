
package com.asiainfo.veris.crm.order.soa.person.busi.ipexpress;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.data.impl.Pagination;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.IpExpressException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.priv.SvcPrivUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.set.util.DataSetUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.TagInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.ProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.SvcInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserAcctDayInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserSvcInfoQry;
import com.wade.container.util.StringUtil;

public class IpExpressBean extends CSBizBean
{
    /**
     * 校验固定电话，获取新虚拟USER_ID,拼必选服务，包串
     * 
     * @data 2013-9-12
     * @return
     * @throws Exception
     */
    public IDataset checkPhone(IData input) throws Exception
    {
        IData param = new DataMap();
        IData phone = new DataMap();
        IData indata = new DataMap();
        IDataset phones = new DatasetList();

        String ipPhone = input.getString("SERIAL_NUMBER_G", "");

        String ipService = input.getString("IPSERVICE", "");
        String productHidden = input.getString("PRODUCT_ID_HIDDEN", "");

        String serviceStr = "", serviceStrTemp = "", packageSvc = "", packageSvcTemp = "";
        // 取系统时间
        String phoneTime = SysDateMgr.getSysDate();
        phone.put("phoneTime", phoneTime);
        // 查找产品改造服务
        if (ipService.equals(""))
        {
//            IDataset serverInfos = SvcInfoQry.getSvcByForcetag(productHidden, getVisit().getStaffId());
               IDataset serverInfos = UProductInfoQry.queryAllProductElements(productHidden);
               IDataset  discnts=new DatasetList();
               IDataset  services=new DatasetList();
               if(IDataUtil.isNotEmpty(serverInfos)){
               	for(int i=0;i<serverInfos.size();i++){
               		IData ss= serverInfos.getData(i);
               		String offerType=ss.getString("OFFER_TYPE");
               		
               		if(StringUtils.equals(offerType, "S")){
               			services.add(ss);
               		}else if(StringUtils.equals(offerType, "D")){
               			discnts.add(ss);
               		}
               	
               	}
               }
            SvcPrivUtil.filterSvcListByPriv(getVisit().getStaffId(), services);

            int serverInt = services.size();
            if (serverInt > 0)
            {
                for (int t = 0; t < serverInt; t++)
                {
                    IData tempSvc = (IData) services.get(t);
                    serviceStrTemp = tempSvc.getString("SERVICE_ID", "");
                    packageSvcTemp = tempSvc.getString("PACKAGE_ID", "");
                    serviceStr += serviceStrTemp + "@";
                    packageSvc += packageSvcTemp + "@" + serviceStrTemp + "~";
                }
            }
        }

        IData tempPhones = UcaInfoQry.qryUserInfoBySn(ipPhone);

        if (tempPhones != null)
        {
            String serialNumber = "";
            // IData tempphone = (IData) tempPhones.get(0);
            String serialNumB = tempPhones.getString("SERIAL_NUMBER");

            IDataset tempUUs = RelaUUInfoQry.getRelatsBySNB(serialNumB);
            if (tempUUs.size() == 1)
            {
                IData tempUU = (IData) tempUUs.get(0);
                String strRelationCode = tempUU.getString("RELATION_TYPE_CODE");
                String userIdA = tempUU.getString("USER_ID_A");
                IDataset lists = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "1", strRelationCode);
                if (lists.size() >= 1)
                {
                    IData list = (IData) lists.get(0);
                    serialNumber = list.getString("SERIAL_NUMBER_B");
                    if (serialNumber.equals(""))
                    {
                        CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_9);
                    }
                    else
                    {
                        CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_10, serialNumber);
                    }
                }
            }
            CSAppException.apperr(IpExpressException.CRM_IPEXPRESS_9);
        }
        else
        {
            String userid = SeqMgr.getUserId();// 用户序列
            phone.put("USER_ID_B", userid);
        }
        phone.put("serviceStr", serviceStr);
        phone.put("packageSvc", packageSvc);
        phones.add(phone);
        return phones;
    }

    public IData getIpExpressInfo(IData userInfo) throws Exception
    {
        IData returndata = new DataMap();
        String userId = userInfo.getString("USER_ID");

        // 号码类型
        IDataset snType = CommparaInfoQry.getOnlyByAttr("CSM", "4", CSBizBean.getTradeEparchyCode());

        // 产品查询
        IDataset products = UpcCall.queryProductInfosByMode("BRAND_CODE", "IP01");
        //IDataset products = ProductInfoQry.queryMainProductByBrand("IP01", CSBizBean.getTradeEparchyCode());
        returndata.put("IP_SN_TYPE", snType);
        returndata.put("IP_PRODUCT", products);

        // --------------判断是否存在预约账期 start--/
        /*
         * UcaData uca = UcaDataFactory.getUcaByUserId(userId); String changeAcctDay = uca.getChangeAcctDay(); String
         * acctDay = uca.getAcctDay();
         */
        IData bookUserAcctDays = UserAcctDayInfoQry.getUserAcctDayAndFirstDateInfo(userId);//
        if (bookUserAcctDays != null && bookUserAcctDays.getString("NEXT_ACCT_DAY") != null)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户存在预约账期,请在预约账期生效后来办理该业务！");
        }
        if (!"1".equals(bookUserAcctDays.getString("ACCT_DAY")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "该用户为分散用户，不能办理IP直通车业务，请先办理主动账期变更，等账期变更生效后办理该业务！");
        }

        // --------------判断是否存在预约账期 end--/

        // 查询已绑定号码
        IDataset bindInfos = RelaUUInfoQry.getUserRelationByUR(userId, "50");
        if (bindInfos.size() > 0)
        {
            IData bindInfo = bindInfos.getData(0);
            String userIdA = bindInfo.getString("USER_ID_A");

            // 获取固定号码信息
            IDataset fixSnInfos = RelaUUInfoQry.getSEL_USER_ROLEA(userIdA, "2", "50");
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

    public IDataset getSvcByProductId(IData productInfo) throws Exception
    {
        String productId = productInfo.getString("PRODUCT_ID");
        //IDataset results = SvcInfoQry.getSvcByIPNameCode(productId, CSBizBean.getVisit().getStaffId());
        
        IDataset results = UpcCall.qryComRelOffersByOfferId("P", productId);
        
        if(IDataUtil.isNotEmpty(results))
        {
        	for(int i=0;i<results.size();i++)
        	{
        		IData temp = results.getData(i);
        		if(StringUtils.equals("S", temp.getString("OFFER_TYPE")))
        		{
        			temp.put("PACKAGE_ID", "-1");
        			temp.put("SERVICE_ID", temp.getString("OFFER_CODE"));
        			temp.put("SERVICE_NAME", temp.getString("OFFER_NAME"));
        			temp.put("FORCE_TAG", "1");
        		}
        		else
        		{
        			results.remove(i);i--;
        		}
        	}
        }
        	

        SvcPrivUtil.filterSvcListByPriv(getVisit().getStaffId(), results);
        return results;
    }

    /**
     * 提供给接口
     * 
     * @param productInfo
     * @return
     * @throws Exception
     */
    public IDataset getUURelationInfo(IData inparams, Pagination page) throws Exception
    {

        IData params = new DataMap();
        params.put("SERIAL_NUMBER", inparams.get("SERIAL_NUMBER"));
        params.put("REMOVE_TAG", "0");
        params.put("NET_TYPE_CODE", "00");
        IDataset infos = UserInfoQry.getUserInfoBySn(inparams.getString("SERIAL_NUMBER"), "0", "00");
        if (DataSetUtils.isBlank(infos))
        {
            return null;
        }

        IData userinfo = infos.getData(0);
        // 根据user_id_b查询用户关系信息
        IDataset uurelainfosB = RelaUUInfoQry.getRelaUUInfoByUserRelarIdB(userinfo.getString("USER_ID"), inparams.getString("RELATION_TYPE_CODE"), page);
        if (DataSetUtils.isBlank(uurelainfosB))
        {
            return null;
        }

        // 根据user_id_a查询用户关系信息
        IData infoB = uurelainfosB.getData(0);
        IDataset uurelainfosA = RelaUUInfoQry.getUserRelByRolecode(infoB.getString("USER_ID_A"), infoB.getString("RELATION_TYPE_CODE"), page);
        if (DataSetUtils.isBlank(uurelainfosA))
        {
            return null;
        }

        IDataset retdst = new DatasetList();// 结果集
        IData userInfo = new DataMap();
        for (Object anUurelainfosA : uurelainfosA)
        {
            // 关系信息
            IData relainfo = (IData) anUurelainfosA;
            String update_staff_id = relainfo.getString("UPDATE_STAFF_ID");
            String update_depart_id = relainfo.getString("UPDATE_DEPART_ID");

            // 用户信息
            IData infosData = UcaInfoQry.qryUserInfoByUserId(relainfo.getString("USER_ID_B"));
            if (IDataUtil.isEmpty(infosData))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1122);
            }
            relainfo.putAll(infosData);
            userInfo.put("IN_DEPART_ID", relainfo.getString("IN_DEPART_ID"));
            userInfo.put("IN_STAFF_ID", relainfo.getString("IN_STAFF_ID"));
            userInfo.put("IN_DATE", relainfo.getString("IN_DATE"));

            // 客户信息
            String custId = infos.get(0, "CUST_ID", "").toString();
            infos = IDataUtil.idToIds(UcaInfoQry.qryCustomerInfoByCustId(custId));
            if (DataSetUtils.isBlank(infos))
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_1134);
            }
            relainfo.putAll(infos.getData(0));
            relainfo.putAll(userInfo);
            retdst.add(relainfo);
        }

        return retdst;
    }
}
