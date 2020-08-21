
package com.asiainfo.veris.crm.order.soa.person.busi.speservice;

import org.apache.log4j.Logger;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataHelper;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.consts.PersonConst;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSAppCall;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipClassInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.param.UVipTypeInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UBrandInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.product.UProductInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.cust.CustVipInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.StaticInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserDiscntInfoQry;

public class NewSvcRecomdInfoSVC extends CSBizService
{
    private static Logger logger = Logger.getLogger(NewSvcRecomdInfoSVC.class);

    private static final long serialVersionUID = 1L;

    public static final int getCharLength(String value, int length)
    {
        char chars[] = value.toCharArray();
        int charidx = 0;
        for (int charlen = 0; charlen < length && charidx < chars.length; charidx++)
            if (chars[charidx] > '\200')
            {
                charlen += 2;
                if (charlen > length)
                {
                    charidx = charidx - 1;
                }
            }
            else
            {
                charlen++;
            }

        return charidx;
    }

    /**
     * 写短信表TI_O_SMS 原来执行TI_O_SMS-INS_SMSCO_CS，有些值是写死的，这里使用默认值
     * 
     * @param data
     * @throws Exception
     */
    public static void insSms(IData data) throws Exception
    {
        IData sendData = prepareSmsData(data);
        NewSvcRecomdInfoBean bean = BeanManager.createBean(NewSvcRecomdInfoBean.class);
        bean.insSms(sendData);
    }

    /**
     * 准备短信数据
     * 
     * @param data
     * @return
     * @throws Exception
     */
    public static IData prepareSmsData(IData data) throws Exception
    {
        IData sendData = new DataMap();

        String sysdate = SysDateMgr.getSysTime();

        /*------------------------以下是原来需要传入的值--------------------------*/
        // 判断是否为空，如果空，则新生成
        String smsNoticeId = data.getString("SMS_NOTICE_ID", "");
        if (StringUtils.isBlank(smsNoticeId))
        {
            smsNoticeId = SeqMgr.getSmsSendId();
            sendData.put("SMS_NOTICE_ID", smsNoticeId);
        }

        sendData.put("PARTITION_ID", smsNoticeId.substring(smsNoticeId.length() - 4));
        sendData.put("EPARCHY_CODE", data.getString("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode()));
        sendData.put("RECV_OBJECT", data.getString("RECV_OBJECT"));// 手机号（服务号）（集团客户经理）也可以扩展其他业务
        sendData.put("RECV_ID", data.getString("RECV_ID", "-1"));// 因为是向集团客户经理发信息所以默认-1,也可以扩展其他业务

        // 短信截取
        String content = data.getString("NOTICE_CONTENT", "");
        int charLength = getCharLength(content, 4000);
        content = content.substring(0, charLength);
        sendData.put("NOTICE_CONTENT", content);

        /*------------------------以下是原来写死的值，改用默认值--------------------------*/
        sendData.put("SEND_COUNT_CODE", data.getString("SEND_COUNT_CODE", "1"));// 发送次数编码?
        sendData.put("REFERED_COUNT", data.getString("REFERED_COUNT", "0"));// 发送次数？
        sendData.put("CHAN_ID", data.getString("CHAN_ID", "11"));
        sendData.put("RECV_OBJECT_TYPE", data.getString("RECV_OBJECT_TYPE", "00"));// 00手机号
        sendData.put("SMS_TYPE_CODE", data.getString("SMS_TYPE_CODE", "20"));// 20用户办理业务通知
        sendData.put("SMS_KIND_CODE", data.getString("SMS_KIND_CODE", "02"));// 02与SMS_TYPE_CODE配套
        sendData.put("NOTICE_CONTENT_TYPE", data.getString("NOTICE_CONTENT_TYPE", "0"));// 0指定内容发送
        sendData.put("FORCE_REFER_COUNT", data.getString("FORCE_REFER_COUNT", "1"));// 指定发送次数
        sendData.put("SMS_PRIORITY", data.getString("SMS_PRIORITY", "50"));// 短信优先级
        sendData.put("REFER_TIME", data.getString("REFER_TIME", sysdate));// 提交时间
        sendData.put("REFER_STAFF_ID", data.getString("REFER_STAFF_ID", CSBizBean.getVisit().getStaffId()));// 员工ID
        sendData.put("REFER_DEPART_ID", data.getString("REFER_DEPART_ID", CSBizBean.getVisit().getDepartId()));// 部门ID
        sendData.put("DEAL_TIME", data.getString("DEAL_TIME", sysdate));// 完成时间
        sendData.put("DEAL_STATE", "0");// 处理状态，0：已处理，15未处理
        sendData.put("SEND_OBJECT_CODE", data.getString("SEND_OBJECT_CODE", "6"));// 通知短信,见TD_B_SENDOBJECT
        sendData.put("SEND_TIME_CODE", data.getString("SEND_TIME_CODE", "1"));// 营销时间限制,见TD_B_SENDTIME
        sendData.put("REMARK", data.getString("REMARK"));// 备注

        /*------------------------以下是原来没有写入的值--------------------------*/
        sendData.put("BRAND_CODE", data.getString("BRAND_CODE"));
        sendData.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());// 接入方式编码
        sendData.put("SMS_NET_TAG", data.getString("SMS_NET_TAG", "0"));
        sendData.put("FORCE_OBJECT", data.getString("FORCE_OBJECT"));// 发送方号码
        sendData.put("FORCE_START_TIME", data.getString("FORCE_START_TIME", ""));// 指定起始时间
        sendData.put("FORCE_END_TIME", data.getString("FORCE_END_TIME", ""));// 指定终止时间
        sendData.put("DEAL_STAFFID", data.getString("DEAL_STAFFID"));// 完成员工
        sendData.put("DEAL_DEPARTID", data.getString("DEAL_DEPARTID"));// 完成部门
        sendData.put("REVC1", data.getString("REVC1"));
        sendData.put("REVC2", data.getString("REVC2"));
        sendData.put("REVC3", data.getString("REVC3"));
        sendData.put("REVC4", data.getString("REVC4"));
        sendData.put("MONTH", sysdate.substring(5, 7));// 月份
        sendData.put("DAY", sysdate.substring(8, 10)); // 日期

        return sendData;
    }

    /**
     * 获取历史推荐信息记录
     */
    public IDataset createDept(IData input) throws Exception
    {
        IDataset recommInfos = new DatasetList(input.getString("cond_ADD_SMS_INFO"));

        IDataset ajaxReturnDatas = new DatasetList();

        int j = 0;

        if (IDataUtil.isNotEmpty(recommInfos))
        {
            for (int i = 0; i < recommInfos.size(); i++)
            {
                IData recommInfo = recommInfos.getData(i);
                IData ajaxReturnData = new DataMap();

                if (!("2".equals(recommInfo.getString("REPLY"))))
                {
                    if (!recommInfo.getString("NOTICE_CONTENT", "").equals(""))
                    {

                        // 发生短信接口
                        IData sendInfo = new DataMap();
                        sendInfo.put("EPARCHY_CODE", CSBizBean.getVisit().getStaffEparchyCode());
                        sendInfo.put("IN_MODE_CODE", CSBizBean.getVisit().getInModeCode());
                        sendInfo.put("RECV_OBJECT", input.getString("SERIAL_NUMBER", ""));
                        sendInfo.put("RECV_ID", input.getString("USER_ID", "0"));
                        sendInfo.put("SMS_PRIORITY", input.getString("PRIORITY", "50"));
                        sendInfo.put("NOTICE_CONTENT", recommInfo.getString("NOTICE_CONTENT", ""));
                        sendInfo.put("FORCE_OBJECT", "10086");
                        sendInfo.put("REMARK", "新业务推荐受理下发短信");
                        insSms(sendInfo);
                        j++;
                    }
                    else
                    {
                        ajaxReturnData.put("MESSAGE", "业务编码：" + recommInfo.getString("OBJECT_ID", "") + "短信信息没有配置，请先配置短信信息！");
                        ajaxReturnDatas.add(ajaxReturnData);
                    }
                }
            }

            if (j != 0)
            {
                IData ajaxReturn = new DataMap();
                ajaxReturn.put("MESSAGE", "成功发送" + j + " 条短信！");
                ajaxReturnDatas.add(ajaxReturn);
            }

        }

        return ajaxReturnDatas;
    }

    // 获取三户资料
    public IDataset getAllInfobySn(IData data) throws Exception
    {
        IData param = new DataMap();
//        param.put(StrUtil.getNotFuzzyKey(), true);
        param.put("SERIAL_NUMBER", data.getString("SERIAL_NUMBER", ""));
        param.put("TRADE_TYPE_CODE", PersonConst.TRADE_TYPE_CODE_CREATE_PERSON_USER);
        IDataset datasetUca = CSAppCall.call("CS.GetInfosSVC.getUCAInfos", param);
        IData userInfo = datasetUca.getData(0).getData("USER_INFO");
        String brandName = UBrandInfoQry.getBrandNameByBrandCode(userInfo.getString("BRAND_CODE"));
        String productName = UProductInfoQry.getProductNameByProductId(userInfo.getString("PRODUCT_ID"));
        userInfo.put("BRAND_NAME", brandName);
        userInfo.put("PRODUCT_NAME", productName);
        return datasetUca;
    }

    /**
     * 获取历史消费信息
     */
    public IDataset getHistoryConsumeInfo(IData param) throws Exception
    {

        IData inparam = new DataMap();
        IDataset ret = new DatasetList();
        IData retData = new DataMap();

        String historyConsume = "";

        inparam.put("USER_ID", param.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER"));
        inparam.put("EPARCHY_CODE", param.getString("EPARCHY_CODE"));
        inparam.put("TRADE_TYPE_CODE_A", param.getString("TRADE_TYPE_CODE_A"));
        inparam.put("Linker", "svcrecomd");

        IDataset historyConsumeInfos = CSAppCall.call("MS.BiIntfOutterSVC.queryMmsfunc", inparam);

        // IDataset historyConsumeInfos = new DatasetList();
        // IData aa = new DataMap();
        // aa.put("AVG_FEE", "10");
        // aa.put("AVG_NEWFEE", "20");
        // aa.put("AVG_LONGFEE", "30");
        // aa.put("tag", "3");
        // aa.put("VALUE_FLAG", "1");
        // historyConsumeInfos.add(aa);

        if (IDataUtil.isNotEmpty(historyConsumeInfos))
        {
            IData historyConsumeInfo = historyConsumeInfos.getData(0);

            if (IDataUtil.isNotEmpty(historyConsumeInfo))
            {
                if (!historyConsumeInfo.getString("tag").equals("0"))
                {
                    if ("1".equals(historyConsumeInfo.getString("VALUE_FLAG")))
                    {
                        historyConsume = "该客户是高价值客户，" + "近三个月平均消费金额：" + historyConsumeInfo.getString("AVG_FEE", "0") + "元，近三个月平均新业务消费金额：" + historyConsumeInfo.getString("AVG_NEWFEE", "0") + "元，近三个月平均长途消费金额："
                                + historyConsumeInfo.getString("AVG_LONGFEE", "0") + "元。";
                    }
                    else
                    {
                        historyConsume = "该客户不是高价值客户，" + "近三个月平均消费金额：" + historyConsumeInfo.getString("AVG_FEE", "0") + "元，近三个月平均新业务消费金额：" + historyConsumeInfo.getString("AVG_NEWFEE", "0") + "元，近三个月平均长途消费金额："
                                + historyConsumeInfo.getString("AVG_LONGFEE", "0") + "元，用户近三个月平均流量：" + historyConsumeInfo.getString("ATT_DECIMAL1", "0") + "MB。";
                    }

                    retData.put("RET", historyConsume);
                    ret.add(retData);
                    return ret;
                }

            }
        }

        return ret;
    }

    /**
     * 获取历史推荐信息记录
     */
    public IDataset getHistoryRecomdInfo(IData input) throws Exception
    {
        IData inparam = new DataMap();
        IDataset dealList = new DatasetList();

        inparam.put("USER_ID", input.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inparam.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
        inparam.put("TRADE_TYPE_CODE_A", input.getString("TRADE_TYPE_CODE_A"));

        dealList = CSAppCall.call("MS.BiIntfOutterSVC.queryObjectGroup", inparam);

        // IData aa = new DataMap();
        // aa.put("ELEMENT_NAME", "aaa");
        // aa.put("RECOMM_MODE", "qqq");
        // aa.put("ELEMENT_TYPE", "0");
        // aa.put("ELEMENT_ID", "111");
        // aa.put("REPLY_CODE", "222");
        // aa.put("RECOMM_DATE", "2013-04-01");
        // aa.put("RECOMM_STAFF_ID", "0000001");
        // aa.put("RECOMM_DEPART_ID", "7201");
        // dealList.add(aa);

        return dealList;
    }

    public IDataset getParaInfo(IData data) throws Exception
    {
        String queryTag = data.getString("QUERY_TAG");
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        IDataset commparaInfos = CommparaInfoQry.getCommpara("CSM", queryTag, tradeTypeCode, "0898");

        return commparaInfos;
    }

    /**
     * 获取推荐信息
     */
    public IDataset getRecomdInfo(IData input) throws Exception
    {
        IData inparam = new DataMap();

        // 判断接入方式 分为呼服和营业
        String chnlType = "";
        String inMode = CSBizBean.getVisit().getInModeCode();
        if ("0".equals(inMode))
            chnlType = "T000";
        else
            chnlType = "C001";

        IDataset delayList = new DatasetList();
        IDataset temp = new DatasetList();

        String serialNum = input.getString("SERIAL_NUMBER","");
        String eparchyCode = input.getString("EPARCHY_CODE","");
        String tradeTypeCodeA = input.getString("TRADE_TYPE_CODE_A","");
        String userId = input.getString("USER_ID", "");
        
        if(userId.isEmpty()&&!serialNum.isEmpty()){
        	NewSvcRecomdInfoBean bean = new NewSvcRecomdInfoBean();
        	IDataset userInfo = bean.getUserInfo(serialNum);
        	userId = userInfo.getData(0).getString("USER_ID","");
        }
        
        inparam.put("USER_ID", userId);
        inparam.put("SERIAL_NUMBER", serialNum);
        inparam.put("CHNL_TYPE", chnlType);// 渠道类型
        inparam.put("TOUCH_TYPE", "2");// 触发方式
        inparam.put("EPARCHY_CODE", eparchyCode);
        inparam.put("TRADE_TYPE_CODE_A", tradeTypeCodeA);

        temp = CSAppCall.call("MS.BiIntfOutterSVC.queryCampnObjects", inparam);

        // IData aa = new DataMap();
        // aa.put("SALE_ACT_SCRIPT", "111111111111111");
        // aa.put("SMS_SCRIPT", "短信信息没有配置");
        //
        // aa.put("CAMPN_NAME", "短信信息没有配置");
        // aa.put("OBJECT_TYPE_DESC", "wwwww");
        // aa.put("recomd_ACCEPT_1", "qqqq");
        // aa.put("CAMPN_ID", "1111");
        // aa.put("OBJECT_ID", "2222");
        // aa.put("OBJECT_TYPE", "ddd");
        // aa.put("MOD_NAME", "短信信息没有配置");
        // aa.put("BUTTON_NAME", "qwqw");
        //
        // aa.put("SOURCE_ID", "111");
        // aa.put("MOD_NAME_L", "1");
        // temp.add(aa);
        // temp.add(aa);
        // temp.add(aa);

        if (IDataUtil.isNotEmpty(temp))
        {
        	boolean ascFlag = true;
        	for (int i = 0; i < temp.size(); i++)
            {
        		IData recommInfo = temp.getData(i);
        		String  priorityId = recommInfo.getString("PRIORITY_ID");
                if(StringUtils.isBlank(priorityId))
                {
                	ascFlag = false;
                }
            }
        	
        	if(ascFlag)
        	{
        		//根据优先级展示
            	DataHelper.sort(temp, "PRIORITY_ID", IDataset.TYPE_STRING, IDataset.ORDER_ASCEND);
            	
        	}
            // 加入从参数表中得到的短信信息字段
            for (int i = 0; i < temp.size(); i++)
            {
                IData recommInfo = temp.getData(i);

                String  recomdCityCode = recommInfo.getString("CITY_CODE");
                if(StringUtils.isNotBlank(recomdCityCode))
        		{
                	//异地推荐业务不能受理
    				String cityCode = getVisit().getCityCode();
    				if(cityCode.equals(recomdCityCode) || "HNHN".equals(recomdCityCode))
    				{
    					recommInfo = transRecommInfo(recommInfo);
    					delayList.add(recommInfo);
    				} 
        		}
                else
                {
                	recommInfo = transRecommInfo(recommInfo);
					delayList.add(recommInfo);
                }
            	  
            }
        }

        return delayList;
    }
    
    public IData transRecommInfo(IData recommInfo) throws Exception
    {

        if (StringUtils.isNotEmpty(recommInfo.getString("SALE_ACT_SCRIPT", "")))
        {
            // 原本使用的是TF_SM_BI_USEROBJGROUP的推荐用语，如果TF_SM_BI_BUSIMANAGE推荐用语有值，替换之。
            recommInfo.remove("TEMPLET_CONTENT");
            recommInfo.put("TEMPLET_CONTENT", recommInfo.getString("SALE_ACT_SCRIPT"));
        }

        if (StringUtils.isNotEmpty(recommInfo.getString("SMS_SCRIPT")))
        {
            recommInfo.put("NOTICE_CONTENT", recommInfo.getString("SMS_SCRIPT"));// 使用经分同步短信内容
        }
        else
        {
            recommInfo.put("NOTICE_CONTENT", "业务编码：" + recommInfo.getString("OBJECT_ID", "") + "短信信息没有配置，请先配置短信信息！");
        }
        
        //根据业务类型匹配URL跳转地址
        String tradeTypeCode = recommInfo.getString("TRADE_TYPE_CODE", "");
        
        if(StringUtils.isNotBlank(tradeTypeCode))
        {
        	String elementId = StaticUtil.getStaticValue(CSBizBean.getVisit(),
        			"TD_S_STATIC", new String[] {"TYPE_ID", "DATA_ID"},"PDATA_ID",new String[] {"NEW_RECOMD_URL", tradeTypeCode} );
    		
    		String url = StaticUtil.getStaticValue(CSBizBean.getVisit(),
        			"TD_S_MODFILE","MOD_CODE","MOD_NAME", elementId);
    		
    		String title = StaticUtil.getStaticValue(CSBizBean.getVisit(),
        			"TD_S_MODFILE","MOD_CODE","REMARK", elementId);
    		
    		recommInfo.put("MOD_NAME", url);
    		recommInfo.put("OBJECT_TYPE_DESC", title);
        }
        return recommInfo;
    }

    public IDataset getRecomdString(IData input) throws Exception
    {
        NewSvcRecomdInfoBean bean = (NewSvcRecomdInfoBean) BeanManager.createBean(NewSvcRecomdInfoBean.class);
        IDataset results = bean.getRecomdString(input);

        return results;
    }

    public IDataset getRefuseInfo(IData input) throws Exception
    {
        IDataset results = StaticInfoQry.getStaticValueByTypeId("REFUSE_REASON_CODE");

        return results;
    }

    public IData getTitleInfo(IData data) throws Exception
    {
        String tradeTypeCode = data.getString("TRADE_TYPE_CODE");
        String elementId = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_STATIC", new String[]
        { "TYPE_ID", "DATA_ID" }, "PDATA_ID", new String[]
        { "NEW_RECOMD_URL", tradeTypeCode });

        String title = StaticUtil.getStaticValue(CSBizBean.getVisit(), "TD_S_MODFILE", "MOD_CODE", "REMARK", elementId);
        IData ret = new DataMap();
        ret.put("TITLE", title);
        return ret;
    }

    /**
     * 查询流量套餐和加油包
     */
    public IDataset getUseOwnInfo(IData param) throws Exception
    {
        IDataset ret = new DatasetList();
        IData output = new DataMap();

        IDataset tmp = UserDiscntInfoQry.qryUserOwn(param.getString("USER_ID"), "5", CSBizBean.getVisit().getStaffEparchyCode());// 流量套餐
        IDataset tmp1 = UserDiscntInfoQry.qryUserOwn(param.getString("USER_ID"), "8", CSBizBean.getVisit().getStaffEparchyCode());// 加油包

        if (IDataUtil.isNotEmpty(tmp))
        {
            output.put("Datadicnt", tmp.getData(0).getString("ELEMENT_NAME"));
        }

        if (IDataUtil.isNotEmpty(tmp1))
        {
            String Adddicnt = "";

            for (int i = 0; i < tmp1.size(); i++)
            {
                Adddicnt += tmp1.getData(i).getString("ELEMENT_NAME");

                if (i != tmp1.size() - 1)
                {
                    Adddicnt += "/";
                }
            }

            output.put("Adddicnt", Adddicnt);
        }

        ret.add(output);

        return ret;

    }

    /**
     * 获取终端信息
     */
    public IDataset getZhongDuanInfo(IData input) throws Exception
    {
        IData inparam = new DataMap();
        IDataset dealList = new DatasetList();

        inparam.put("USER_ID", input.getString("USER_ID"));
        inparam.put("SERIAL_NUMBER", input.getString("SERIAL_NUMBER"));
        inparam.put("EPARCHY_CODE", input.getString("EPARCHY_CODE"));
        inparam.put("TRADE_TYPE_CODE_A", input.getString("TRADE_TYPE_CODE_A"));
        inparam.put("Linker", "svcrecomd");

        dealList = CSAppCall.call("MS.BiIntfOutterSVC.queryMmsfunc", inparam);

        // IData aa = new DataMap();
        // aa.put("PHONE_QUALITY_NAME", "aaa");
        // aa.put("PHONE_MODEL_NAME", "qqq");
        // aa.put("tag", "3");
        // dealList.add(aa);
        return dealList;
    }

    /**
     * 获取用户ＶＩＰ等级名称
     */
    public IDataset Vipquery(IData param) throws Exception
    {
        IDataset ret = new DatasetList();
        IData result = new DataMap();
        IData data = new DataMap();
        String vip_type_name = "";
        // 取大客户资料
        data.clear();
        IDataset vipids = CustVipInfoQry.getCustVipByUserId(param.getString("USER_ID"), "0", CSBizBean.getVisit().getStaffEparchyCode());
        IData vipres = new DataMap();

        if (IDataUtil.isEmpty(vipids))
        {
            return null;
        }
        else
        {
            vipres = (IData) (vipids.get(0));

            // 获取大客户等级名称
            String svc = UVipClassInfoQry.getVipClassNameByVipTypeCodeClassId(vipres.getString("VIP_TYPE_CODE"), vipres.getString("VIP_CLASS_ID"));
            if (StringUtils.isBlank(svc))
            {
                result.put("CLASS_NAME", "");
            }
            else
            {
                result.put("CLASS_NAME", svc);
            }

            String vip_type_code = vipres.getString("VIP_TYPE_CODE", "");
            vip_type_name = UVipTypeInfoQry.getVipTypeNameByVipTypeCode(vip_type_code);
        }

        data.put("RET", vip_type_name + result.getString("CLASS_NAME", ""));

        ret.add(data);

        return ret;

    }
}
