
package com.asiainfo.veris.crm.order.soa.person.busi.view360;

import java.io.File;
import java.io.FileInputStream;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.CrmUserException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.RouteInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.cfg.OfferCfg;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.UcaData;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.ucadata.factory.UcaDataFactory;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustomerInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.hint.HintInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.BankInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.rela.RelaUUInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.view360.Qry360InfoDAO;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.sms.SmsSend;
import com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.queryinfo.QueryInfoBean;

public class GetUser360ViewSVC extends CSBizService
{
    private static final long serialVersionUID = 1L;

    /**
     * 同步导出
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public String exportUser(IData data) throws Exception
    {
        IDataset userResults = new DatasetList();

        // 获取下载后的文件名
        String fileName = data.getString("FILE_NAME", "");

        // 设置文件处理上下文
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        // 构造要到出的数据
        userResults = data.getDataset("USER_DATASET");

        // 构造要显示的字段英文名和中文名
        String para[] = (String[]) data.get("USER_PARAM");
        String paraName[] = (String[]) data.get("USER_PARAM_NAME");

        // 生成一个临时文件，在该函数最后记得要删除本地文件。
        String localFileId = this.getFileId(new DataMap()).getData(0).getString("FILE_ID"); // 生成本地文件的文件名，同时也是上传到远程主机后的文件名。
        String fullFileName = null;// this.exportToLocalTxt(userResults, localFileId, para, paraName);

        // 构建输入流上传文件到FTP服务器。
        FileInputStream fileInputStream = new FileInputStream(fullFileName);
        String newFtpFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(fileInputStream, "personserv", "upload/attach", localFileId);
        // (InputStream input, String ftpSite, String filePath, String fileName)
        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(newFtpFileId, fileName);

        // 删除本地的临时文件
        File file = new File(fullFileName);
        if (file.exists())
        {
            file.delete();
        }
        return url;
    }

    /**
     * @author luoz
     * @date 2013-07-30
     * @description 360用户查询，查询非正常用户，如果有多个用户的时候的查询方法。
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getCheckUserInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.getCheckUserInfo(param, this.getPagination());
    }

    public IDataset getCreditInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.getCreditInfo(param);
    }

    public IDataset getFileId(IData input) throws Exception
    {
        IData data = new DataMap();
        data.put("FILE_ID", SeqMgr.getFileId());
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    /**
     * @author luoz
     * @date 2013-07-25
     * @description 获取用户的优惠和品牌产品信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset getUserDiscntAndProduct(IData param) throws Exception
    {
        IDataset dataset = new DatasetList();
        IData rtnData = new DataMap();
        String sendInfo = "";
        String isShow = "0";
        String sn = param.getString("SN");

        // 获取用户的品牌、产品和优惠信息
        IDataset userDataset = UserDiscntInfoQry.getUserInfoBySN(sn);
        if (!userDataset.isEmpty())
        {
            for (Object obj : userDataset)
            {
                IData userData = (IData) obj;
                // 根据用户的品牌、产品和优惠信息检查是否在TD_S_CCSMS中进行了配置。
                IDataset sendDataset = UserDiscntInfoQry.getCCSmsSendInfo(userData.getString("BRAND_CODE", ""), userData.getString("PRODUCT_ID", ""), userData.getString("DISCNT_CODE", ""));
                if (!sendDataset.isEmpty())
                {
                    sendInfo = sendDataset.getData(0).getString("CCSMS_CONTENT", "");
                    isShow = "1";
                    break;
                }
            }
        }
        rtnData.put("MESSAGE_CONTENT", sendInfo);
        rtnData.put("IS_SHOW", isShow);
        dataset.add(rtnData);
        return dataset;
    }

    /**
     * @author luoz
     * @date 2013-07-22
     * @description 获取要导出的结果集
     * @param param
     *            必须包含USER_ID
     * @param pagination
     * @return
     * @throws Exception
     */
    public IDataset getUserInfoForExport(IData param) throws Exception
    {
        SuperUserInfoBean bean = BeanManager.createBean(SuperUserInfoBean.class);
        IData data = bean.exportUserInfo(param, this.getPagination());
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    public IDataset initCustComplaint(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initCustComplaint(param);
    }

    public IDataset initCustContactMgr(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initCustContactMgr(param);
    }

    public IDataset initTradeHistoryInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initTradeHistoryInfo(param);
    }
    
    
    public IDataset initTradeHistoryInfoHis(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initTradeHistoryInfoHis(param);
    }
    

    /**
     * 查询账户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryAccountInfo(IData input) throws Exception
    {
        String userId = input.getString("USER_ID", "");

        IData map = UcaInfoQry.qryAcctInfoByUserId(userId);

        if (IDataUtil.isNotEmpty(map))
        {
            String accId = map.getString("ACCT_ID", "");

            // 根据ACCT_ID查询账户有效账期信息（当前以及预约的）
            IDataset data = Qry360InfoBean.qryAcctAcctDayInfo(accId);

            if (IDataUtil.isNotEmpty(data))
            {
                map.put("CURRENT_ACCT_DAY", data.getData(0).getString("CURRENT_ACCT_DAY", "1"));
                map.put("NEXT_ACCT_DAY", data.getData(0).getString("NEXT_ACCT_DAY", "1"));
            }
        }

        IDataset ids = IDataUtil.idToIds(map);

        return ids;
    }

    /**
     * 查询用户基本信息--个人代扣账户
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryBaseAccountInfo(IData input) throws Exception
    {
        // 查询个人代扣账户信息
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        input.put("DESTROY_TAG", "0");
        IDataset baseAccountInfos = bean.getDeductInfos(input, getPagination());
        return baseAccountInfos;
    }

    /**
     * 查询接触信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryContactMgr(IData input) throws Exception
    {
        if (!"".equals(input.getString("SERIAL_NUMBER", "")))
        {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            // IDataset contactMgr = bean.qryContactMgr(input, getPagination());
            IDataset contactMgr = bean.qryNewContactMgr(input, getPagination());
            return contactMgr;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询投诉信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryCustComplaintInfo(IData input) throws Exception
    {
        if (!"".equals(input.getString("USER_ID", "")))
        {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            IDataset custComplaintInfo = bean.qryCustComplaintInfo(input, getPagination());

            return custComplaintInfo;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询客户信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryCustInfo(IData input) throws Exception
    {
        String cust_id = input.getString("CUST_ID", "");
        IData custInfo = CustomerInfoQry.qryCustInfo(cust_id);
        
        String serialNumber = input.getString("SERIAL_NUMBER", "");
        UcaData ucaData = UcaDataFactory.getNormalUca(serialNumber);
        String userId = ucaData.getUserId();
        IDataset m2mInfos = UserOtherInfoQry.getOtherInfoByCodeUserId(userId,"HYYYKBATCHOPEN");
        if(IDataUtil.isNotEmpty(m2mInfos))
        {
        	custInfo.put("IS_M2M", "是");//是行业应用卡
        }
        else
        {
        	custInfo.put("IS_M2M", "否");//非行业应用卡
        }
        
        IDataset ids = IDataUtil.idToIds(custInfo);
        return ids;
    }

    /**
     * 根据用户ID(USER_ID)查询tf_f_user_foregift表中Money>0的保证金数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryForegiftInfo(IData input) throws Exception
    {
        // 查询保证金信息
        if (!"".equals(input.getString("USER_ID", "")))
        {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            IDataset foregiftInfo = bean.getForegiftInfo(input, getPagination());
            return foregiftInfo;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * 查询360弹出框 SerialNumberEparchyBean
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryMofficeInfoBySn(IData input) throws Exception
    {
        IData mofficeData = RouteInfoQry.getMofficeInfoBySn(input.getString("SERIAL_NUMBER"));
        if (IDataUtil.isEmpty(mofficeData))
        {
            return null;
        }
        IDataset resultSet = new DatasetList();
        resultSet.add(mofficeData);
        return resultSet;
    }

    /**
     * 查询接触信息详细
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryNewContactTrac(IData input) throws Exception
    {
        if (!"".equals(input.getString("SERIAL_NUMBER", "")))
        {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            IDataset custComplaintInfo = bean.qryNewContactTrac(input, getPagination());

            return custComplaintInfo;
        }
        else
        {
            return new DatasetList();
        }
    }

    public IDataset qryNpUserInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.qryNpUserInfo(param);
    }

    /**
     * 查询360弹出框 qryPersonInfoByCustId
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryPersonInfoByCustId(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        CustomerInfoBean bean = BeanManager.createBean(CustomerInfoBean.class);
        outDataset = bean.qryPersonInfoByCustId(input, getPagination());
        return outDataset;
    }
    public IDataset qry_tf_sm_bi_mmsfunc_InfoByUserId(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
        outDataset = bean.qry_tf_sm_bi_mmsfunc_InfoByUserId(input, getPagination());
        return outDataset;
    }

    /**
     * 查询用户基本信息--支付平台
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryPlatInfo(IData input) throws Exception
    {
        // 查询支付平台业务信息
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        IDataset platInfos = bean.getBankbandInfos(input, getPagination());
        return platInfos;
    }

    /**
     * @author luoz
     * @date 2013-07-25
     * @description 查询提示信息
     * @param userInfo
     * @return
     * @throws Exception
     */
    public IDataset qryPopuInfo(IData userInfo) throws Exception
    {
        QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
        String popuInfo = bean.qryPopuInfo(userInfo, userInfo.getString("TRADE_TYPE_CODE", "2101"));
        IData data = new DataMap();
        data.put("POPU_INFO", popuInfo);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    /**
     * 查询邮寄信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryPostInfo(IData input) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        IDataset postInfos = bean.qryPostInfo(input, getPagination());
        return postInfos;
    }

    /**
     * 查询产品信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryProductInfo(IData input) throws Exception
    {
        String user_id = input.getString("USER_ID", "");
        if (StringUtils.isBlank(user_id))
        {
            return new DatasetList();
        }

        IDataset outDataset = null;

        if ("1".equals(input.getString("SelectTag", "0")))
        {
            outDataset = Qry360InfoBean.qryProductInfoAll(input, getPagination());
            //调产商品接口
            if(IDataUtil.isNotEmpty(outDataset)){
            	for(int i = 0 ; i < outDataset.size() ; i++){
            		IData outData = outDataset.getData(i);
            		OfferCfg offerCfg = OfferCfg.getInstance(outData.getString("PRODUCT_ID",""), "P");
            		if(offerCfg != null){
            			outData.put("PRODUCT_NAME", offerCfg.getOfferName());
            			outData.put("BRAND_CODE", offerCfg.getBrandCode());
            			outData.put("PRODUCT_EXPLAIN", offerCfg.getDescription());
            		}
            	}
            }
        }
        else
        {
            outDataset = Qry360InfoBean.qryProductInfo(input, getPagination());
          //调产商品接口
            if(IDataUtil.isNotEmpty(outDataset)){
            	for(int i = 0 ; i < outDataset.size() ; i++){
            		IData outData = outDataset.getData(i);
            		OfferCfg offerCfg = OfferCfg.getInstance(outData.getString("PRODUCT_ID",""), "P");
            		if(offerCfg != null){
            			outData.put("PRODUCT_NAME", offerCfg.getOfferName());
            			outData.put("BRAND_CODE", offerCfg.getBrandCode());
            			outData.put("PRODUCT_EXPLAIN", offerCfg.getDescription());
            		}
            	}
            }
        }

        return outDataset;
    }

    /**
     * 查询用户关系信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryRelationInfo(IData input) throws Exception
    {
        if (!"".equals(input.getString("USER_ID", "")))
        {
            IDataset outDataset = new DatasetList();
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            outDataset = bean.qryRelationInfo(input, getPagination());
            return outDataset;
        }
        else
        {
            return new DatasetList();
        }
    }
    
    /**
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryIsCmccStaff(IData input) throws Exception
    {
    	IDataset dataset = new DatasetList();
    	IData data = new DataMap();
    	data.put("IS_STAFF_USER", "0");
        if (!"".equals(input.getString("USER_ID", "")))
        {
        	 boolean flag = RelaUUInfoQry.isCMCCstaffUserNew(input.getString("USER_ID", ""));
        	 if(flag){
        		 data.put("IS_STAFF_USER", "1");
        	 }
        	 dataset.add(data);
            return dataset;
        }
        else
        {
        	dataset.add(data);
            return dataset;
        }
    }

    /**
     * 查询用户资源信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryResourceInfo(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("USER_ID", "")))
        {
            return new DatasetList();
        }

        IDataset outDataset = null;
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);

        // 查询所有服务
        if ("1".equals(input.getString("SelectTag", "0")))
        {
            outDataset = bean.qryResourceInfoAll(input, getPagination());
        }
        else
        {
            outDataset = bean.qryResourceInfo(input, getPagination());
        }
        /**
         * REQ201512080015 关于客户资料综合查询界面屏蔽卡序列号
         * 2015-12-24
         * chenxy3
         * */
        IDataset resinfosNEW=new DatasetList();
        for(int i=0;i<outDataset.size();i++){
        	IData resdata=outDataset.getData(i);
        	String resTypeCode=resdata.getString("RES_TYPE_CODE","");
        	if("1".equals(resTypeCode)){
        		String simCard=resdata.getString("RES_CODE","");
        		String writeCard=resdata.getString("RSRV_STR5","");
        		String simCard2="";
        		String writeCard2="";
        		if(!"".equals(simCard)){
        			simCard2=simCard.substring(0,simCard.length()-5)+"*****";
        		}
        		if(!"".equals(writeCard)){
        			writeCard2=writeCard.substring(0,writeCard.length()-5)+"*****";
        		}
        		resdata.put("RES_CODE", simCard2);
        		resdata.put("RSRV_STR5", writeCard2);
        	}
        	resinfosNEW.add(resdata);
        }
        
        return resinfosNEW;
    }

    /**
     * 查询积分信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryScoreInfo(IData input) throws Exception
    {
        if (!"".equals(input.getString("USER_ID", "")))
        {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            IDataset scoreInfo = bean.qryScoreInfo(input, getPagination());
            return scoreInfo;
        }
        else
        {
            return new DatasetList();
        }
    }

    /**
     * @author fanfb
     * @description 查询接入号码信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qrySerialNumberBInfo(IData param) throws Exception
    {
        QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
        IDataset dataset = bean.qrySerialNumberBInfo(param);
        return dataset;
    }

    /**
     * 查询360弹出框 QryUserInfoBean
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qrySerialNumberBySim(IData input) throws Exception
    {
        String res_code = input.getString("SIM_NUMBER", "");
        if (!"".equals(res_code))
        {

            IDataset outDataset = new DatasetList();
            QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
            outDataset = bean.qrySerialNumberBySim(input, getPagination());
            return outDataset;
        }
        else
        {
            return new DatasetList();
        }
    }

    public IDataset qryTHCustomerContactInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.qryTHCustomerContactInfo(param, getPagination());
    }

    public IDataset qryTHRelaTradeInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.qryTHRelaTradeInfo(param, getPagination());
    }

    
    public IDataset queryTradeHistoryInfo(IData input) throws Exception
    {
        Qry360InfoBean bean = (Qry360InfoBean) BeanManager.createBean(Qry360InfoBean.class);

        return bean.queryTradeHistoryInfo(input, getPagination());
    }
    public IDataset queryWelfareHistoryInfo(IData input) throws Exception
    {
        Qry360InfoBean bean = (Qry360InfoBean) BeanManager.createBean(Qry360InfoBean.class);

        return bean.queryWelfareHistoryInfo(input);
    }
    public IDataset queryWelfareHistoryDetailInfo(IData input) throws Exception
    {
        Qry360InfoBean bean = (Qry360InfoBean) BeanManager.createBean(Qry360InfoBean.class);

        return bean.queryWelfareHistoryDetailInfo(input);
    }
    /**
     * 查询业务历史信息
     * 
     * @param input
     * @return
     * @throws Exception
     * @author hui
     */
    public IDataset qryTradeHistoryInfo(IData input) throws Exception
    {

        String time_check = input.getString("TIME_CHECK", "");
        if (!"on".equals(time_check))
        {
            input.remove("START_DATE");
            input.remove("END_DATE");
        }
        String book_check = input.getString("BOOK_CHECK", "");
        if (book_check != null && "on".equals(book_check))
        {
            input.remove("TIME_CHECK");
            input.remove("TRADE_TYPE_CODE");
            input.remove("START_DATE");
            input.remove("END_DATE");
            input.put("TRADE_FLAG", "0");
        }
        else
        {
            input.put("TRADE_FLAG", "1");
        }
        input.put("OP_CODE", "00100009"); // 业务编码:001xxxxx(查询类);002xxxxx(受理类);003xxxxx(IBOOS类);004xxxxx(短信发送类)，参看附件提供的操作类型编码，没有的请补上
        input.put("OP_TYPE", "0"); // 操作类型:0-查询,1-受理

        Qry360InfoBean bean = (Qry360InfoBean) BeanManager.createBean(Qry360InfoBean.class);
        IDataset results = bean.qryTradeHistoryInfo(input, getPagination());
        if(IDataUtil.isNotEmpty(results))
        {
        	for(int i=0; i<results.size();i++)
        	{
        		IData tmp = results.getData(i);
        		String brandName = UBrandInfoQry.getBrandNameByBrandCode(tmp.getString("BRAND_CODE"));
        		String productName = UProductInfoQry.getProductNameByProductId(tmp.getString("PRODUCT_ID"));
        		tmp.put("BRAND_NAME", brandName);
        		tmp.put("PRODUCT_NAME", productName);
        		
				if(tmp.getString("TRADE_TYPE_CODE").equals("138") || tmp.getString("TRADE_TYPE_CODE").equals("760"))
				{
					String tremark = "用户被加入黑名单"+(tmp.getString("REMARK")!=null ? "."+tmp.getString("REMARK"):"");
					tmp.remove("REMARK");
					tmp.put("REMARK",tremark);
					results.remove(i);
					results.add(i, tmp);
				}
        	}
        }
        
        return results;
        // return tradeHistoryInfo;
    }

    /**
     * 查询业务历史信息--页面业务类型展示
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryTradeTypeCode(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        outDataset = bean.qryTradeTypeCode(input);
        return outDataset;
    }

    /**
     * 历史用户查询业务历史信息--页面业务类型展示
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryTradeTypeCodeCg(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        outDataset = bean.queryTradeTypeCodeCg(input);
        return outDataset;
    }

    /**
     * 查询用户优惠信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserDiscntInfo(IData input) throws Exception
    {

        if (StringUtils.isBlank(input.getString("USER_ID", "")))
        {
            return new DatasetList();
        }

        IDataset outDataset = null;

        // 查询所有服务
        if ("1".equals(input.getString("SelectTag", "0")))
        {
            outDataset = Qry360InfoDAO.qryUserDiscntInfoAll(input, getPagination());
        }
        else
        {
            outDataset = Qry360InfoDAO.qryUserDiscntInfo(input, getPagination());
        }

        return outDataset;
    }
    /**
     * 查询用户权益信息by huangmx5
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserWelfareInfo(IData input) throws Exception
    {

        if (StringUtils.isBlank(input.getString("USER_ID", "")))
        {
            return new DatasetList();
        }

        IDataset outDataset = null;

        // 查询所有权益
        if ("1".equals(input.getString("SelectTag", "0")))
        {
            outDataset = Qry360InfoDAO.qryUserWelfareInfoAll(input, getPagination());
        }
        else
        {
            outDataset = Qry360InfoDAO.qryUserWelfareInfo(input, getPagination());
        }

        return outDataset;
    }
    
    /**
     * 查询用户优惠信息
     * liquan
     * @param input
     * @return 根据手机号码
     * @throws Exception
     */
    public IDataset qryUserDiscntInfoBySerialNumber(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER", ""))) {
            return new DatasetList();
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER", ""));
        if (userInfo.isEmpty()) {
            return new DatasetList();
        }
        IData userdata = new DataMap();
        userdata.put("USER_ID", userInfo.getString("USER_ID", ""));
        IDataset outDataset = null;
        outDataset = Qry360InfoDAO.qryUserDiscntInfo(userdata);

        return outDataset;
    }

    /**
     * 查询用户基本信息--基本信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfo(IData input) throws Exception
    {
        if (!"".equals(input.getString("USER_ID", "")))
        {

            IDataset userInfos = new DatasetList();

            input.put("X_GETMODE", "1");

            // 查询用户基本信息
            userInfos = Qry360InfoDAO.qryUserInfo(input, getPagination());
            
            //查询基本信息当前业务区
            IDataset userCityInfo = Qry360InfoDAO.qryUserCityInfo(input);
            if(IDataUtil.isNotEmpty(userInfos) && IDataUtil.isNotEmpty(userCityInfo))
            {
            	userInfos.getData(0).put("USER_CITY_CODE", userCityInfo.getData(0).getString("CITY_CODE", ""));
            }
            if(IDataUtil.isNotEmpty(userInfos) && IDataUtil.isEmpty(userCityInfo))
            {
            	userInfos.getData(0).put("USER_CITY_CODE", userInfos.getData(0).getString("CITY_CODE", ""));
            }
            
            //查询基本信息电子经理信息
            IDataset vipCustInfo = Qry360InfoDAO.qryTelManager(input);
            if(IDataUtil.isNotEmpty(vipCustInfo))
            {
            	userInfos.getData(0).put("CUST_MANAGER_ID", vipCustInfo.getData(0).getString("CUST_MANAGER_ID", ""));
            }
            return userInfos;
        }

        return null;
    }

    /**
     * 查询360弹出框 QryUserInfoBean
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserInfoBySerialNumber(IData input) throws Exception
    {
        String serialNumber = input.getString("SERIAL_NUMBER", "");
        String custId = input.getString("CUST_ID", "");
        if (StringUtils.isBlank(serialNumber) && StringUtils.isBlank(custId))
        {
            return new DatasetList();
        }
        else
        {
            IDataset outDataset = new DatasetList();
            QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
            outDataset = bean.qryUserInfoBySerialNumber(input, getPagination());
            return outDataset;
        }
    }

    /**
     * 查询360弹出框 密码验证
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserPass(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        IData result = new DataMap();
        Boolean passWdResult = UserInfoQry.checkUserPassWd(input.getString("USER_ID"), input.getString("PASSWORD"));
        if (passWdResult == true)
        {
            result.put("RESULT_CODE", "0");
            result.put("RESULT_INFO", "OK");
            outDataset.add(result);
            return outDataset;
        }
        else if (passWdResult == false)
        {
            CSAppException.apperr(CrmUserException.CRM_USER_91);
        }
        result.put("RESULT_CODE", "-1");
        outDataset.add(result);
        return outDataset;
    }

    public IDataset qryUserSaleActiveInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return filter566Product(bean.qryUserSaleActiveInfo(param));//REQ201501040001 关于优化营销活动资料查询规则及营销活动办理时提示信息 by songlm 20150116
    }

    public IDataset qryUserSaleActiveInfoAll(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return filter566Product(bean.qryUserSaleActiveInfoAll(param));//REQ201501040001 关于优化营销活动资料查询规则及营销活动办理时提示信息 by songlm 20150116
    }
    
    public IDataset filter566Product(IDataset param) throws Exception
    {
    	//REQ201501040001 关于优化营销活动资料查询规则及营销活动办理时提示信息 by songlm 20150116
        IDataset is566Configs = new DatasetList();
        for (int i = param.size() - 1; i >= 0; i--)
        {
        	is566Configs.clear();
            IData saleactive = param.getData(i);
            String productId = saleactive.getString("PRODUCT_ID");
            String startDate = saleactive.getString("START_DATE");
            is566Configs = CommparaInfoQry.getCommparaCode1("CSM", "566", productId, CSBizBean.getUserEparchyCode());
            if(IDataUtil.isNotEmpty(is566Configs))//commpara表的566配置中存在该产品
            {
            	String sysDate = SysDateMgr.getSysTime();
            	int intervalMonths = Integer.parseInt(is566Configs.first().getString("PARA_CODE1"));//偏移月份
            	String newStartDate = SysDateMgr.getAddMonthsNowday(intervalMonths, startDate);//活动偏移后的开始时间
                if (sysDate.compareTo(newStartDate) > 0)
                {
                	param.remove(i);
                }
            }
        }
        //end
    	return param;
    }

    /**
     * 查询用户服务信息
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryUserSvcInfo(IData input) throws Exception
    {

        if (!"".equals(input.getString("USER_ID", "")))
        {

            IDataset outDataset = new DatasetList();
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);

            // 查询所有服务
            if ("1".equals(input.getString("SelectTag", "0")))
            {
                outDataset = bean.qryUserServiceInfoAll(input, getPagination());
            }
            else
            {
                outDataset = bean.qryUserServiceInfo(input, getPagination());
            }

            // 客服接入日志 实际不用做异常处理 因为只要异常了，前台就会抛出error
            /*
             * if("1".equals(params.getString("IN_MODE_CODE",""))){//客服接入 IData data1 = new DataMap(); try {
             * data1.put("OP_TYPE", "0"); // 0查询类 data1.put("OP_CODE", "00100002"); //业务编码 data1.put("OP_RESULTCODE",
             * "0"); //记录成功标识 } catch (Exception e) { data1.put("OP_RESULTCODE", "1"); //记录失败标识
             * data1.put("OP_RESULTINFO", common.getStackTrace(e.getCause())); //记录失败日志 } finally {
             * Utility.createOperLog(pd, data1); //插入日志 } }
             */
            // 记录客户经理操作日志
            /*
             * String oper_mod = "用户360度->服务信息查询"; String oper_type = "QRY"; String oper_desc = "输入参数为:"+pd.getData();
             * OperLogBean operLog = new OperLogBean(); operLog.insertOperLog(pd, oper_mod, oper_type, oper_desc);
             * return new DatasetList();
             */

            return outDataset;
        }
        else
        {
            return new DatasetList();
        }
    }
    
    /**
     * 查询用户服务信息
     * liquan
     * @param input 手机号码
     * @return 
     * @throws Exception
     */
    public IDataset qryUserSvcInfoBySerialNumber(IData input) throws Exception
    {
        if (StringUtils.isBlank(input.getString("SERIAL_NUMBER", ""))) {
            return new DatasetList();
        }

        IData userInfo = UcaInfoQry.qryUserInfoBySn(input.getString("SERIAL_NUMBER", ""));
        if(userInfo.isEmpty()){
            return new DatasetList();
        }
        IData  userdata = new DataMap();
        userdata.put("USER_ID", userInfo.getString("USER_ID",""));
        IDataset outDataset = new DatasetList();
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        outDataset = bean.qryUserServiceInfo(userdata);
        return outDataset;              
    }

    /**
     * 查询360弹出框 queryGroupName
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset queryGroupName(IData input) throws Exception
    {
        IDataset outDataset = new DatasetList();
        GroupMemberBean bean = BeanManager.createBean(GroupMemberBean.class);
        outDataset = bean.queryGroupName(input, null);
        return outDataset;
    }

    public IDataset queryMemberAll(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.queryMemberAll(param);
    }

    public IDataset queryShareDiscnt(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
       // return bean.queryShareDiscnt(param);
        //modify by duhj  2017/5/20修改客户资料综合查询--共享页面  报错 
        IDataset  results=bean.queryShareDiscnt(param);
       if( IDataUtil.isNotEmpty(results)){
        	for(int i=0;i<results.size();i++){
        		IData temp=results.getData(i);
        		String discntName=UDiscntInfoQry.getDiscntNameByDiscntCode(temp.getString("DISCNT_CODE"));
        		temp.put("DISCNT_NAME", discntName);
        	}
        }
       return results;
    }

    public IDataset queryThServerInfos(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.queryThServerInfos(param, getPagination());
    }

    public IDataset queryWideUserInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.queryWideUserInfo(param);
    }

    /**
     * @author luoz
     * @date 2013-07-26
     * @description 给客户发送客户经理短信。
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset sendCustManagerNum(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);

        IData inParam = new DataMap();
        inParam.put("CUST_MANAGER_ID", param.getString("CUST_MANAGER_ID"));
        inParam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));

        IData data = bean.sendCustManagerNum(inParam);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    /**
     * @author luoz
     * @date 2013-07-29
     * @description 发送短信
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset sendMessage(IData param) throws Exception
    {
        param.put("NOTICE_CONTENT", param.getString("MESSAGE_CONTENT"));
        param.put("RECV_OBJECT", param.getString("SEND_NUMBER"));
        SmsSend.insSms(param);
        IDataset dataset = new DatasetList();
        IData data = new DataMap();
        data.put("IS_SUCCESS", "true");
        dataset.add(data);
        return dataset;
    }

    /**
     * @author luoz
     * @date 2013-07-24
     * @description 写台账日志，直接插台账历史表。
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset writeTradeQueryLog(IData param) throws Exception
    {
        QryUserInfoBean bean = BeanManager.createBean(QryUserInfoBean.class);
        boolean isSuccess = bean.writeTradeQueryLog(param.getString("USER_ID", ""), param.getString("NET_TYPE_CODE", ""), param.getString("SERIAL_NUMBER", ""), param.getString("EPARCHY_CODE", ""), param.getString("CITY_CODE", ""), param.getString(
                "PRODUCT_ID", ""), param.getString("BRAND_CODE", ""), param.getString("CUST_ID", ""));
        IData data = new DataMap();
        data.put("IS_SUCCESS", isSuccess);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;

    }
    
    public IDataset qryUserResSimInfo(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.qryUserResSimInfo(param);
    }
    
    public IDataset initTradeHistoryInfoHisYear(IData param) throws Exception
    {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initTradeHistoryInfoHisYear(param);
    }
    
    
    public IData queryAcctInfoCustManager(IData param)throws Exception{
    	IData result=new DataMap();
    	
    	String serialNumber=param.getString("SERIAL_NUMBER","");
    	if(serialNumber.trim().equals("")){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"参数错误：手机号不能为空！");
    	}
    	
    	IData userInfo=UcaInfoQry.qryUserInfoBySn(serialNumber);
    	if(IDataUtil.isEmpty(userInfo)){
    		CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户信息：用户已经失效或者不存在此用户！");
    	}
    	
    	String userId=userInfo.getString("USER_ID");
        IData map = UcaInfoQry.qryDefaultPayRelaByUserId(userId);
        
        if (IDataUtil.isEmpty(map)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户的默认账户信息！");
        }
//        String accId = map.getString("ACCT_ID", "");
//
//        //根据ACCT_ID查询账户有效账期信息（当前以及预约的）
//        IDataset data = Qry360InfoBean.qryAcctAcctDayInfo(accId);
//
//        if (IDataUtil.isNotEmpty(data))
//        {
//            map.put("CURRENT_ACCT_DAY", data.getData(0).getString("CURRENT_ACCT_DAY", "1"));
//            map.put("NEXT_ACCT_DAY", data.getData(0).getString("NEXT_ACCT_DAY", "1"));
//        }
        String acctId=map.getString("ACCT_ID");
        IData acctInfo=UcaInfoQry.qryAcctInfoByAcctId(acctId);
        if (IDataUtil.isEmpty(acctInfo)){
        	CSAppException.apperr(CrmCommException.CRM_COMM_103,"未查询到用户的默认账户对应的账户信息！");
        }
        
        result.put("ACCT_ID", acctId);
        result.put("PAY_NAME", acctInfo.getString("PAY_NAME", ""));
        result.put("PAY_MODE_NAME", StaticUtil.getStaticValue("TD_S_PAYMODE", acctInfo.getString("PAY_MODE_CODE")));
        
        String bankCode=acctInfo.getString("BANK_CODE", "");
        if(!bankCode.equals("")){
        	IDataset bankInfo=BankInfoQry.getBrandByBank(acctInfo.getString("EPARCHY_CODE","0898"), bankCode);
        	if(IDataUtil.isNotEmpty(bankInfo)){
        		result.put("BANK_NAME", bankInfo.getData(0).getString("BANK",""));
        	}else{
        		result.put("BANK_NAME", "");
        	}
        }else{
        	result.put("BANK_NAME", "");
        }
        
        result.put("SCORE_VALUE", acctInfo.getString("SCORE_VALUE", ""));
        result.put("OPEN_DATE", acctInfo.getString("OPEN_DATE", ""));
        
    	return result;
    }
		
	/**
      * 已开业务查询  --已开业务查询
      * @param param
      * @return
      * @throws Exception
      */
     public IDataset getOpenInfo(IData param) throws Exception
     {        
     	Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
     	QueryInfoBean queryInfoBean = (QueryInfoBean) BeanManager.createBean(QueryInfoBean.class);
     	IDataset acctOpenInfos = bean.queryOpenInfo(param);
     	IDataset crmOpenInfos = queryInfoBean.QueryUserUsingBizOpenInfo(param);
     	
     	IDataset openInfo = new DatasetList();
     	//账管接口SUM数据放在第一条数据中，如果调用失败，则将第一条清除
     	if("1".equals(acctOpenInfos.getData(0).getString("RESULT_CODE"))){
     		openInfo.add(acctOpenInfos.getData(0));
     	}else{
     		acctOpenInfos.remove(0);
     	}
     	IData acctOpenInfo = new DataMap();
     	IData crmOpenInfo = new DataMap();
     	String itemContent = null;
     	if(IDataUtil.isNotEmpty(crmOpenInfos)){
     		//先遍历crm已开业务结果集，与账管的数据一一比对，当INST_ID相同时，将账管数据替换掉CRM数据
     		for(int i=0;i<crmOpenInfos.size();i++){
     			crmOpenInfo = crmOpenInfos.getData(i);
     			crmOpenInfo.put("MONTH_RENT", "0");
				crmOpenInfo.put("RECVE_TYPE", "--");
				crmOpenInfo.put("DAY_RECVE_FEE", "--");
				crmOpenInfo.put("IS_RECV_FEE", "--");
				crmOpenInfo.put("RECVE_DAYS", "--");
				crmOpenInfo.put("RECVE_FEE_SUM", "--");
				crmOpenInfo.put("ITEM_CODE", "0");
				crmOpenInfo.put("ITEM_NAME", "--");
				crmOpenInfo.put("FEEPOLICY_ID", "--");
				crmOpenInfo.put("FEEPOLICY_INS_ID", "--");
				crmOpenInfo.put("FEE_FLAG", "0");
				crmOpenInfo.put("ITEM_CONTENT", "--");
				if(IDataUtil.isNotEmpty(acctOpenInfos)){
					for(int j=0;j<acctOpenInfos.size();j++){
	     				acctOpenInfo = acctOpenInfos.getData(j);
	     				acctOpenInfo.put("ITEM_NAME", StringUtils.isBlank(acctOpenInfo.getString("ITEM_NAME"))?"--":acctOpenInfo.getString("ITEM_NAME"));
	     				acctOpenInfo.put("ITEM_CODE", StringUtils.isBlank(acctOpenInfo.getString("ITEM_CODE"))?"0":acctOpenInfo.getString("ITEM_CODE"));
	     				//确认账目明细内容，流量安心包的FEE_FLAG为1但ITEM_NAME为空
	     				if(!"".equals(acctOpenInfo.getString("ITEM_NAME"))&&StringUtils.isNotBlank(acctOpenInfo.getString("ITEM_NAME"))){
	     					itemContent = acctOpenInfo.getString("FEE_FLAG")=="1"?"收费账目："+acctOpenInfo.getString("ITEM_NAME"):"减免账目："+acctOpenInfo.getString("ITEM_NAME");
	     					acctOpenInfo.put("ITEM_CONTENT", itemContent);
	     					itemContent = "";
	     					if("0".equals(acctOpenInfo.getString("FEE_FLAG"))){//减免数据BUSINESS_NAME为ITEM_NAME
	     						acctOpenInfo.put("BUSINESS_NAME", acctOpenInfo.get("ITEM_NAME")+"-减免");
	     						acctOpenInfo.put("START_DATE", " ");
	     						acctOpenInfo.put("END_DATE", " ");
	     					}
	     				}
	     				//匹配CRM与账管的数据
     					if(crmOpenInfo.getString("INST_ID","").equals(acctOpenInfo.getString("FEEPOLICY_INS_ID"))){
         					crmOpenInfo.put("MONTH_RENT",acctOpenInfo.getString("MONTH_RENT"));
         					crmOpenInfo.put("RECVE_TYPE",acctOpenInfo.getString("RECVE_TYPE"));
         					crmOpenInfo.put("DAY_RECVE_FEE",acctOpenInfo.getString("DAY_RECVE_FEE"));
         					crmOpenInfo.put("IS_RECV_FEE",acctOpenInfo.getString("IS_RECV_FEE"));
         					crmOpenInfo.put("RECVE_DAYS",acctOpenInfo.getString("RECVE_DAYS"));
         					crmOpenInfo.put("RECVE_FEE_SUM",acctOpenInfo.getString("RECVE_FEE_SUM"));
         					crmOpenInfo.put("ITEM_CODE", acctOpenInfo.getString("ITEM_CODE"));
         					crmOpenInfo.put("ITEM_NAME", acctOpenInfo.getString("ITEM_NAME"));
         					crmOpenInfo.put("FEEPOLICY_ID", acctOpenInfo.getString("FEEPOLICY_ID"));
         					crmOpenInfo.put("FEEPOLICY_INS_ID", acctOpenInfo.getString("FEEPOLICY_INS_ID"));
         					crmOpenInfo.put("FEE_FLAG", acctOpenInfo.getString("FEE_FLAG"));
         					crmOpenInfo.put("BUSINESS_TYPE",acctOpenInfo.getString("BUSINESS_TYPE"));
         					crmOpenInfo.put("ITEM_CONTENT", acctOpenInfo.getString("ITEM_CONTENT"));
         					acctOpenInfos.remove(j);
         					break;
         				}
	     			}
				}
				if(("2".equals(crmOpenInfo.getString("BUSINESS_TYPE",""))||"3".equals(crmOpenInfo.getString("BUSINESS_TYPE","")))&&"--".equals(crmOpenInfo.getString("IS_RECV_FEE",""))){
					crmOpenInfos.remove(i);
					i=i-1;
				}
     		}
     	}
     	openInfo.addAll(acctOpenInfos);
     	openInfo.addAll(crmOpenInfos);
     	return openInfo; 
     }
     
     
     /**
      * 已开业务查询  --营销活动明细
      * @param param
      * @return
      * @throws Exception
      */
     public IDataset getSaleInfo(IData param) throws Exception
     {        
    	 IData result = UcaInfoQry.qryUserInfoBySn(param.getString("SERIAL_NUMBER"));
     	param.put("USER_ID", result.getString("USER_ID"));
     	
     	Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
     	IDataset saleInfo = bean.qryUserSaleActiveInfo(param);
     	IDataset dataset =bean.querySaleInfo(param);
         if(IDataUtil.isNotEmpty(saleInfo) && IDataUtil.isNotEmpty(dataset)){
             for(int i=0;i<saleInfo.size();i++){
             	String relationTradeId = saleInfo.getData(i).getString("RELATION_TRADE_ID");
             	for(int j=0;j<dataset.size();j++){
             		IData data = dataset.getData(j);
             		if(relationTradeId.equals(data.getString("OUTER_TRADE_ID"))){
             			saleInfo.getData(i).put("RETURN_COUNT", data.getString("RETURN_COUNT","--"));
             			saleInfo.getData(i).put("MONTHS", data.getString("MONTHS","--"));
             		}else{
             			saleInfo.getData(i).put("MONTHS", "--");
             			saleInfo.getData(i).put("RETURN_COUNT", "--");
             		}
             	}
             }
         }
         return filter566Product(saleInfo);
     }
	 /**
     * REQ201607140023 关于非实名用户关停改造需求
     * 根据user_id判断是否存在TL_B_STOPUSER及TF_BH_TRADE 1361的信息
     * 存在则说明办理过实名制停机。
     * */
	public IDataset qryUserIfNotRealName(IData inData) throws Exception
	{
		Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
		inData.put("TRADE_TYPE_CODE", "1361");
		IDataset userBhDatas = bean.qryUserHistoryInfoByUserIdAndTradeType(inData);
		if(IDataUtil.isNotEmpty(userBhDatas)){
			inData.put("SERIAL_NUMBER", userBhDatas.getData(0).getString("SERIAL_NUMBER",""));
		}
		return bean.qryUserIfNotRealName(inData);
	}

	/**
     * REQ201608260010 关于非实名用户关停改造需求
     * 20160912 chenxy3
     * 欠费停机转“非实名制全停”
     * */
	public IDataset qryUserIfAllStop(IData inData) throws Exception
	{
		Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
		return bean.qryUserIfAllStop(inData);
	}
	public IDataset qryUserIfNotRealNameForOpen(IData inData) throws Exception
	{
		Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
		return bean.qryUserIfNotRealNameForOpen(inData);
	}
	
    /**
     * 查询品牌名称和产品名称,调产商品接口
     * @param input
     * @return
     * @throws Exception
     * add by duhj 
     * 2017/03/06
     */
    public IData getUserName(IData input) throws Exception
    {
    	IData  resuData=new DataMap();
        String productName = UProductInfoQry.getProductNameByProductId(input.getString("PRODUCT_ID"));
        String productName2 = UProductInfoQry.getProductNameByProductId(input.getString("B_PRODUCT_ID"));

        String brandName=UBrandInfoQry.getBrandNameByBrandCode(input.getString("BRAND_CODE"));
        resuData.put("PRODUCT_NAME", productName);
        resuData.put("B_PRODUCT_NAME", productName2);
        resuData.put("BRAND_NAME", brandName);


        return resuData;
    }
    
    /**
     * add by duhj
     * 查询电渠信息
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset qryChannelOrderInfo(IData input) throws Exception
    {


        Qry360InfoBean bean = (Qry360InfoBean) BeanManager.createBean(Qry360InfoBean.class);
        IDataset results = new DatasetList();

        return results;

    }
    
    /**
	 * 
     * REQ201701040013_客户资料综合查询界面增加可以根据白卡号查询
	 * <br/>
	 * 通过白卡获取sim卡号
	 * @author zhuoyingzhi
	 * @date 20170227
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public IDataset qrySIMByWhiteSIM(IData param) throws Exception{
    	Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
    	return bean.qrySIMByWhiteSIM(param);
    }

    /**
	 * 通过通过身份证号码获取用户信息
	 * @author zhengkai5
	 * @date 20170227
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public IDataset qryCustInfoByPsptId(IData param)throws Exception{
    	Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
    	return  bean.qryCustInfoByPsptId(param);
    }

    /**
	 * 通过userId获取用户主产品信息
	 * @author zhengkai5
	 * @date 20170227
	 * @param param
	 * @return
	 * @throws Exception
	 */
    public IData qryUserProductInfoByUserId(IData param)throws Exception
    {
    	String userId = param.getString("USER_ID");
    	IDataset userProduct = UserProductInfoQry.queryMainProduct(userId);
    	if(IDataUtil.isNotEmpty(userProduct))
    	{
    		return userProduct.getData(0);
    	}
    	return null;
    }

    /**
     * "客户资料综合查询"界面外框从CRM获取用户数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPersonBaseInfo(IData param) throws Exception {
        IDataset baseInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            baseInfo = bean.queryPersonBaseInfo(param);
        }
        return baseInfo;
    }

    /**
     * "客户资料综合查询"界面外框从账管等模块获取用户数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryPersonAcctInfo(IData param) throws Exception {
        IDataset acctInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            acctInfo = bean.queryPersonAcctInfo(param);
        }
        return acctInfo;
    }

    /**
     * "客户资料综合查询"子页面获取导航按钮配置信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset loadNavButtonConfig(IData param) throws Exception {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.loadNavButtonConfig(param);
    }

    /**
     * "客户资料综合查询"首页标签查询用户近三月账单明细
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUserBillInfo(IData param) throws Exception {
        IDataset billInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            billInfo = bean.queryUserBillInfo(param);
        }
        return billInfo;
    }

    /**
     * "客户资料综合查询"首页标签查询当月用户套餐使用情况
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryUserConsumptionInfo(IData param) throws Exception {
        IDataset consumptionInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            consumptionInfo = bean.queryUserConsumptionInfo(param);
        }
        return consumptionInfo;
    }

    /**
     * "客户资料综合查询"我的活动标签查询营销活动预存款和赠送款信息
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySaleActiveDepositAndGiftInfo(IData param) throws Exception {
        IDataset depositGiftInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("USER_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            depositGiftInfo = bean.querySaleActiveDepositAndGiftInfo(param);
        }
        return depositGiftInfo;
    }

    /**
     * "客户资料综合查询"我的业务历史"最近"标签初始化
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset initTradeHistoryTab(IData param) throws Exception {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initTradeHistoryTab(param);
    }

    /**
     * "客户资料综合查询"我的业务历史"历史（一年以前）"标签初始化
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset initTradeHistoryBeforeTab(IData param) throws Exception {
        Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
        return bean.initTradeHistoryBeforeTab(param);
    }

    /**
     * "客户资料综合查询"我的业务历史查询订单主台账表数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset queryMainTradeTable(IData param) throws Exception {
        IDataset mainTradeInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("TRADE_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            mainTradeInfo = bean.queryMainTradeTable(param);
        }
        return mainTradeInfo;
    }

    /**
     * "客户资料综合查询"我的业务历史查询订单关联子台账表数据
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset querySubTradeTable(IData param) throws Exception {
        IDataset subTradeInfo = new DatasetList();
        if (StringUtils.isNotBlank(param.getString("TRADE_ID", ""))) {
            Qry360InfoBean bean = BeanManager.createBean(Qry360InfoBean.class);
            subTradeInfo = bean.querySubTradeTable(param);
        }
        return subTradeInfo;
    }

    /**
     * 客户宽带信息查询与三个月手机平均消费接口
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset qryUserWideNetInfo(IData param) throws Exception{
    	IData returnData = new DataMap();
    	IDataset wideUserInfo = queryWideUserInfo(param);
    	param.put("TRADE_TYPE_CODE","2101");
    	IDataset hintInfo =HintInfoQry.getHintInfo(param);
    	if(IDataUtil.isNotEmpty(hintInfo)){
    		String hint = hintInfo.getData(0).getString("HINT_INFO2");
    		int indexOf = hint.indexOf("~",1);
    		String hint2 = hint.substring(0, indexOf);
    		returnData.put("HINT_INFO2", hint2);
    	}
    	if(IDataUtil.isNotEmpty(wideUserInfo)){
    		IData data = wideUserInfo.getData(0);
    		IData productSetInfo = data.getData("USER_INFO").getData("PRODUCT_SET");
             returnData.put("CUST_NAME", data.getData("CUST_INFO").getString("CUST_NAME"));
             returnData.put("SEX",StaticUtil.getStaticValue("SEX",data.getData("CUST_INFO").getString("SEX")));//getStaticValue('SEX',widenetInfo.CUST_INFO.SEX)
             returnData.put("SERIAL_NUMBER",data.getData("USER_INFO").getString("SERIAL_NUMBER"));
             returnData.put("PSPT_ID",data.getData("CUST_INFO").getString("PSPT_ID"));
             returnData.put("CONTACT",data.getData("CUST_INFO").getString("CONTACT"));
             returnData.put("CONTACT_PHONE",data.getData("CUST_INFO").getString("CONTACT_PHONE"));
             returnData.put("HOME_ADDRESS",data.getData("CUST_INFO").getString("HOME_ADDRESS"));
             returnData.put("PRODUCT_NAME",data.getData("USER_INFO").getData("PRODUCT_SET").getString("PRODUCT_NAME"));
             returnData.put("BRAND_NAME",  UpcCall.queryBrandNameByChaVal(productSetInfo.getString("BRAND_CODE","")));
             returnData.put("OPEN_DATE", data.getData("USER_INFO").getString("OPEN_DATE"));
             returnData.put("IN_DATE",data.getData("USER_INFO").getString("IN_DATE"));
             returnData.put("OPEN_MODE",StaticUtil.getStaticValue("OPEN_MODE",data.getData("USER_INFO").getString("OPEN_MODE")));
             returnData.put("STAND_ADDRESS",data.getData("BASE_INFO").getString("STAND_ADDRESS"));
             returnData.put("DETAIL_ADDRESS",data.getData("BASE_INFO").getString("DETAIL_ADDRESS"));
             returnData.put("AREA_NAME",StaticUtil.getStaticValue(CSBizBean.getVisit(),"TD_M_AREA","AREA_CODE", "AREA_NAME",data.getData("USER_INFO").getString("CITY_CODE")));
             returnData.put("CONSTRUCTION_ADDR",data.getData("BASE_INFO").getString("CONSTRUCTION_ADDR"));
             returnData.put("RES_ID",data.getData("BASE_INFO").getString("RES_ID",""));
             IDataset dataset = data.getData("USER_INFO").getDataset("DISCNT_SET");
             if(IDataUtil.isNotEmpty(dataset)){
            	 for(int i=0;i<dataset.size();i++){
            		 dataset.getData(i).put("DISCNT_TAG",StaticUtil.getStaticValue("USER_SPECTAG",dataset.getData(i).getString("SPEC_TAG")));
            	 }
             }
             returnData.put("WHIDE_DISCNT",dataset);
             IDataset results = IDataUtil.idToIds(returnData);
             return results;
    	}
    	return null;
    }
}