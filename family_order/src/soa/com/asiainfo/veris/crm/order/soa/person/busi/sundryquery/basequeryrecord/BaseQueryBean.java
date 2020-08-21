
package com.asiainfo.veris.crm.order.soa.person.busi.sundryquery.basequeryrecord;

import com.ailk.org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.exception.TimeException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.ParamInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserInfoQry;

public class BaseQueryBean extends CSBizBean
{
    static transient final Logger logger = Logger.getLogger(BaseQueryBean.class);

    public IData busiCallIBOSS(IData input) throws Exception
    {
        String kindId = input.getString("KIND_ID");
        String indictSeq = input.getString("INDICTSEQ");
        String svcTypeId = input.getString("SVCTYPEID");
        String provinceID = input.getString("HOMEPROV");
        String svcCity = input.getString("SVCCITY");
        String originTime = input.getString("ORIGINTIME");
        String contactChannel = input.getString("CONTACTCHANNEL");
        String operateConditions = input.getString("OPERATECONDITIONS");
        String serNumber = input.getString("CALLERNO");
        String serviceTypeId = input.getString("SERVICETYPEID");
        String operateTypeId = input.getString("OPERATETYPEID");
        String currPage = input.getString("QUERYPAGENUM");
        String subsLevel = input.getString("SUBSLEVEL");

        IData interfaceData = IBossCall.queryBussQureySeriveIBOSS(kindId, indictSeq, svcTypeId, provinceID, svcCity, originTime, contactChannel, operateConditions, serNumber, serviceTypeId, operateTypeId, currPage,subsLevel);

        return interfaceData;
    }

    /**
     * 业务查询服务请求派发接口获取数据
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset BussQureySerive(IData input) throws Exception
    {
		if(checkAgentSn(input.getString("CALLERNO"))){
	    	String  useLevel = getSubsLevel(input.getString("CALLERNO"));
	    	input.put("SUBSLEVEL", useLevel);//新增用户级别
		}
    	
        IData interfaceData = this.busiCallIBOSS(input);

        this.checkDataError(interfaceData);

        IData RsltTotalCnt = new DataMap();
        IData data = new DataMap();
        IDataset infos = new DatasetList();

        IDataset pdts = this.resolveDataMessage(interfaceData);

        if (pdts == null || pdts.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有符合查询条件的数据！");
        }

        RsltTotalCnt.put("RSLTTOTALCNT", interfaceData.getString("RSLTTOTALCNT"));
        RsltTotalCnt.put("RSLTPAGECURRCNT", interfaceData.getString("RSLTPAGECURRCNT"));

        data.put("RSLT_CNT", RsltTotalCnt);
        data.put("PDTS_INFO", pdts);

        infos.add(data);

        return infos;
    }

    /**
     * 业务办理结果获取接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IData BussTransact(IData input) throws Exception
    {
        IData interfaceData = this.busiCallIBOSS(input);

        this.checkDataError(interfaceData);

        return interfaceData;
    }

    /**
     * 报错信息处理
     * 
     * @param interfaceData
     * @throws Exception
     */
    public void checkDataError(IData interfaceData) throws Exception
    {
        if ("10".equals(interfaceData.getString("OPERRTNSTAID")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_496);
        }
        if ("11".equals(interfaceData.getString("OPERRTNSTAID")))
        {
            CSAppException.apperr(TimeException.CRM_TIME_55);
        }
        if ("12".equals(interfaceData.getString("OPERRTNSTAID")))
        {
            CSAppException.apperr(TimeException.CRM_TIME_42);
        }
        if ("21".equals(interfaceData.getString("OPERRTNSTAID")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_340);
        }
        if ("31".equals(interfaceData.getString("OPERRTNSTAID")))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_575);
        }
    }

    /**
     * //服务请求标识 数据构造： YYYYMMDD＋CSVC＋3位省代码＋7位流水号
     * 
     * @return
     * @throws Exception
     */
    public String getIndictSequence(String provinceID) throws Exception
    {
        String tempSeq = SeqMgr.getCustContact();

        String finalSeq = "";
        String indictSeq = "";

        if (tempSeq.length() == 7)
        {
            finalSeq = tempSeq;
        }
        else
        {
            finalSeq = tempSeq.substring(tempSeq.length() - 7, tempSeq.length());
        }

        indictSeq = SysDateMgr.getNowCycle() + "CSVC" + provinceID + finalSeq;

        return indictSeq;
    }

    /**
     * 获取省代码
     * 
     * @param cycle
     * @return
     * @throws Exception
     */
    public String getProvinceID() throws Exception
    {
        String provinceID = "";

        if ("XINJ".equals(getVisit().getProvinceCode()))
        {
            provinceID = "991";
        }
        if ("HAIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "898";
        }
        if ("HNAN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "731";
        }
        if ("QHAI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "971";
        }
        if ("SHXI".equals(getVisit().getProvinceCode()))
        {
            provinceID = "290";
        }
        if ("TJIN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "220";
        }
        if ("YUNN".equals(getVisit().getProvinceCode()))
        {
            provinceID = "871";
        }
        return provinceID;
    }

    /**
     * 插入日志表
     * 
     * @param input
     * @throws Exception
     */
    public void InsertPlatREQ(IData input) throws Exception
    {
        Dao.insert("TF_B_PLATREQ_LOG", input);
    }

    /**
     * IBOSS返回数据解析
     * 
     * @param interfaceData
     * @return
     * @throws Exception
     */
    public IDataset resolveDataMessage(IData interfaceData) throws Exception
    {
        // 得到IBOSS返回的数据IData类型，在就是用interfaceDate来替代IBOSS输出
    	IDataset pdts = new DatasetList();
    	IDataset ids = new DatasetList();
        Object obj = interfaceData.get("QRYRSLTLIST");
        if(obj instanceof IDataset)
        {
        	ids = (IDataset) obj;
        	if (null!=ids)
        	{
                for (int i = 0; i < ids.size(); i++)
                {
	                IData resultData = new DataMap();
                	String[] resultMsg = ids.get(i).toString().split("\\|");	
                	for (int j = 0; j < resultMsg.length; j++)
                	{
                		resultData.put("RSRV_STR" + j, resultMsg[j]);
                	}
                	pdts.add(resultData);
                }
            }
        }
        else
        {
        	String resultString = interfaceData.getString("QRYRSLTLIST");
        	if(null!=resultString){
	        	IData resultData = new DataMap();
	        	String[] resultMsg = resultString.split("\\|");	
	        	for (int j = 0; j < resultMsg.length; j++)
	        	{
	        		resultData.put("RSRV_STR" + j, resultMsg[j]);
	        	}
	        	pdts.add(resultData);
        	}
        }

        return pdts;
    }

    /**
     * 未到达业务查询结果获取接口
     * 
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset UnfinishBussQureySerive(IData input) throws Exception
    {
        IData interfaceData = this.busiCallIBOSS(input);

        this.checkDataError(interfaceData);

        IDataset infos = this.resolveDataMessage(interfaceData);

        if (infos == null || infos.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有符合查询条件的数据！");
        }

        return infos;
    }

    /**
     * 更新日志表
     * 
     * @param input
     * @throws Exception
     */
    public void UpdatePlatREQ(IData input) throws Exception
    {
        Dao.executeUpdateByCodeCode("TF_B_PLATREQ_LOG", "UPD_STATE_FINISH_TIME", input);
    }
    
    /**
   	 * 获取vip客户信息
   	 * 
   	 * @param params
   	 *            查询所需参数
   	 * @param pd
   	 * @return IDataset
   	 * @throws Exception
   	 */
   	public static IDataset getVipInfos(IData params) throws Exception {
   		params.put("REMOVE_TAG", "0");
   		return Dao.qryByCode("TF_F_CUST_VIP", "SEL_BY_SN_VIPCLASS", params);
   		
   	}
   	/**
   	 * 获取用户星级资料信息
   	 * 
   	 * @param params
   	 *            查询所需参数
   	 * @param pd
   	 * @return IDataset
   	 * @throws Exception
   	 */
   	public static IDataset getStarInfos(IData params) throws Exception {
   		params.put("ACT_TAG", "1");
   		return Dao.qryByCode("MV_TF_O_CREDITINFO", "SEL_BY_SN_VIPCLASS", params);
   		
   	}
	
	/**
	 * 根据手机号码获取用户级别，先出现tf_f_cust_vip表，如tf_f_cust_vip无记录再查TF_F_CUST_STAR表
	 * 在原用户级别（钻石卡客户、金卡客户、银卡客户、普通客户）取值范围的基础上，
	 * 新增五星（钻）、五星（金）、五星、四星、三星、二星、一星、准星、未评级等9个客户等级。
	 * 即兼容新旧不同的用户等级体系，又保证系统最新修改
	 * 编码	说明
	 *01钻石卡客户  02金卡客户  03银卡客户  04普通客户  05五星（钻） 06	五星（金） 07	五星  08	四星 09三星 10二星 11一星 12准星 13未评级
	 * @param params
	 *            查询所需参数
	 * @param pd
	 * @return IDataset
	 * @throws Exception
	 */
	public String getSubsLevel(String  sn) throws Exception{
		IData inparams = new DataMap();
		if(sn.startsWith("86")){
		    sn = sn.substring(2);
        }


		inparams.put("SERIAL_NUMBER", sn);
		IDataset vipInfos = getVipInfos(inparams);//先查vip表
		String useLevel = "";
		if(!vipInfos.isEmpty()){
			IData vip = (IData) vipInfos.get(0);
			String vipClass = vip.getString("VIP_CLASS_ID");
			IDataset paramset = ParamInfoQry.getCommparaByCode1("CSM", "8866","VIPJBBM", vipClass, getVisit().getLoginEparchyCode());
			if(paramset!=null && paramset.size()>0){
				useLevel = paramset.getData(0).getString("PARA_CODE2");//根据一级boss接口规范转换
			}				
		}else{



			IDataset userInfos = UserInfoQry.getUserInfoBySn(sn,"0");
			inparams.put("USER_ID", userInfos.getData(0).getString("USER_ID"));
			
			IDataset starInfos = getStarInfos(inparams);//vip表查不到数据时查星级表
			if(!starInfos.isEmpty()){
				IData star = (IData) starInfos.get(0);
				String starClass = star.getString("CREDIT_CLASS");
				IDataset paramset = ParamInfoQry.getCommparaByCode1("CSM", "8866", "XJJBBM", starClass, getVisit().getLoginEparchyCode());
				if(paramset!=null && paramset.size()>0){
					useLevel = paramset.getData(0).getString("PARA_CODE2");//根据一级boss接口规范转换
				}	
			}
			
		}
		if(useLevel.isEmpty()){//如果无数据则为未评级
			useLevel = "13";
		}
		return useLevel;
	}
	
	 /**
     * 校验是否手机号码 add by ouyang
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
/*	
    public static boolean isMobileNO(String mobiles) throws Exception{
    	Pattern p = Pattern.compile("^((13)|(18)|(15))\\d{9}$");
    	Matcher m = p.matcher(mobiles);
    	return m.matches();
    }
*/    
	 /**
     * 校验是否存在用户号码 add by ouyang
     * 
     * @param serialNumber
     * @return
     * @throws Exception
     */
    private boolean checkAgentSn(String serialNumber) throws Exception
    {
		 IDataset res = UserInfoQry.getUserInfoBySerailNumber("0",serialNumber);
		 if (res == null || res.size() < 1 || res.isEmpty())
        {
		    return false;
        }
	     return true;
    }
    
    /**
     * 用户轨迹核查
     * 业务查询服务请求派发接口获取数据
     * @param input
     * @return
     * @throws Exception
     */
    public IDataset BussQureySerive1(IData input) throws Exception
    {
        IData interfaceData = this.busiCallIBOSS1(input);

        this.checkDataError(interfaceData);

        IData RsltTotalCnt = new DataMap();
        IData data = new DataMap();
        IDataset infos = new DatasetList();

        IDataset pdts = this.resolveDataMessage(interfaceData);

        if (pdts == null || pdts.size() == 0)
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有符合查询条件的数据！");
        }

        RsltTotalCnt.put("RSLTTOTALCNT", interfaceData.getString("RSLTTOTALCNT"));
        RsltTotalCnt.put("RSLTPAGECURRCNT", interfaceData.getString("RSLTPAGECURRCNT"));
       /* RsltTotalCnt.put("RSLTTOTALCNT", 3);
        RsltTotalCnt.put("RSLTPAGECURRCNT", 1);*/

        data.put("RSLT_CNT", RsltTotalCnt);
        data.put("PDTS_INFO", pdts);

        infos.add(data);

        return infos;
    }
   
    /**
     * 用户轨迹核查
     */
        public IData busiCallIBOSS1(IData input) throws Exception
        {
            String kindId = input.getString("KIND_ID");
            String indictSeq = input.getString("INDICTSEQ");
            String svcTypeId = input.getString("SVCTYPEID");
            String provinceID = input.getString("HOMEPROV");
            String svcCity = input.getString("SVCCITY");
            String originTime = input.getString("ORIGINTIME");
            String contactChannel = input.getString("CONTACTCHANNEL");
            String operateConditions = input.getString("TRADE_ID");//订单号作为操作条件
            //String serNumber = input.getString("CALLERNO");
            String serviceTypeId = input.getString("SERVICETYPEID");
            String operateTypeId = input.getString("OPERATETYPEID");
            String currPage = input.getString("QUERYPAGENUM");
            String trade_id = input.getString("TRADE_ID");
            
         /* String subsLevel = "";
            
    		if(checkAgentSn(input.getString("CALLERNO"))){
    			subsLevel = getSubsLevel(input.getString("CALLERNO"));//新增用户级别 add by chenff5 2015.2.3
    	    	input.put("SUBSLEVEL", subsLevel);//新增用户级别
    		}*/

            IData interfaceData = IBossCall.queryBussQureySeriveIBOSS1(kindId, indictSeq, svcTypeId, provinceID, svcCity, originTime, contactChannel, operateConditions, serviceTypeId, operateTypeId, currPage);

            return interfaceData;
        }
        
        /**
         * 用户轨迹查询
         * 未到达业务查询结果获取接口
         */
        public IDataset UnfinishBussQureySerive1(IData input) throws Exception
        {
            IData interfaceData = this.busiCallIBOSS1(input);

            this.checkDataError(interfaceData);

            IDataset infos = this.resolveDataMessage(interfaceData);

            if (infos == null || infos.size() == 0)
            {
                CSAppException.apperr(CrmCommException.CRM_COMM_103, "没有符合查询条件的数据！");
            }

            return infos;
        }


    /*
     *功能描述 自有业务对iboss返回的数据进行解析
     * @author heyy3
     * @date 2019/8/19
     * @param
     * @return
     */
    public IDataset resolveReturnStringForZiyouBusi(String resultString){
        IDataset pdts = new DatasetList();
        if(StringUtils.isNotBlank(resultString)){
            IDataset resDataset = null;
            Object[] lines = null;

            if (resultString.startsWith("[")&&resultString.endsWith("]"))
            {
                resDataset = new DatasetList(resultString);
                lines = resDataset.toArray();
            }
            else
            {
                lines = new Object[1];
                lines[0] = resultString;
            }
            for (int a = 0; a < lines.length; a++)
            {
                IData resultData = new DataMap();
                String[] resultMsg = lines[a].toString().split("\\|");
                for (int b = 0; b < resultMsg.length; b++)
                {
                    resultData.put("RSRV_STR" + b, resultMsg[b]);
                }
                pdts.add(resultData);
            }
        }
        return pdts;
    }
    public IDataset resolveReturnStringForAbility(String resultString){
        IDataset pdts = new DatasetList();
        if(StringUtils.isNotBlank(resultString)){
            String[] aStrings = resultString.split("\\[REC_SPLIT\\]");
            for (int a = 0; a < aStrings.length; a++)
            {
                IData resultData = new DataMap();
                String[] resultMsg = aStrings[a].toString().split("\\|");
                for (int b = 0; b < resultMsg.length; b++)
                {
                    resultData.put("RSRV_STR" + b, resultMsg[b]);
                }
                pdts.add(resultData);
            }
        }
        return pdts;
    }
}
