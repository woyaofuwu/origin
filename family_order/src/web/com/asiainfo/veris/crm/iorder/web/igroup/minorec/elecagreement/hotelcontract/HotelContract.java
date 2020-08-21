package com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.hotelcontract;

import org.apache.tapestry.IRequestCycle;

import com.ailk.bizview.base.CSViewCall;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.iorder.pub.frame.bcf.util.SysDateMgr4Web;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.base.ElecLineProtocolBase;
import com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.utils.ElecLineUtil;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.web.frame.bcf.exception.CSViewException;

public abstract class HotelContract extends ElecLineProtocolBase {
    
    private final static String AGREEMENT_DEF_ID = "HotelContract";
    
    private final static String B_NAME = "中国移动通信集团海南有限公司";

    private final static int prex = 3;

    private final static int ZERO = 0;
    private final static int ONE = 1;
    private final static int TWO = 2;
    private final static int THREE = 3;
    private final static int FOUR = 4;
    private final static int FIVE = 5;
    private final static int SIX = 6;
    private final static int SEVEN = 7;
    private final static int EIGHT = 8;
    private final static int NINE = 9;
    private final static int TEN = 10;
    private final static int ELEVEN = 11;
    private final static int TWELVE = 12;

    @Override
    public void initPage(IRequestCycle cycle) throws Exception {
        
        //转换移动端与PC端key值
        
        super.initPage(cycle);
    }

    protected void initalPageValue(IData protocolInfo, IData accessoryInfo, IData pageData) throws Exception{
		if("N".equals(pageData.getString("SUBMITTYPE"))){
			IData groupInfo = protocolInfo.getData("CUST_INFO",new DataMap());
			IData managerInfo = protocolInfo.getData("CUST_MRG_INFO",new DataMap());

			protocolInfo.put("A_NAME", groupInfo.getString("CUST_NAME"));
			protocolInfo.put("B_NAME", B_NAME);
			accessoryInfo.put("A_NAME_2", groupInfo.getString("CUST_NAME"));
			accessoryInfo.put("CONTACTS_A",groupInfo.getString("JURISTIC_NAME"));
            accessoryInfo.put("CONTACT_PHONE_A",groupInfo.getString("GROUP_CONTACT_PHONE"));
            accessoryInfo.put("CUST_CONTRACT",groupInfo.getString("JURISTIC_NAME"));
            accessoryInfo.put("CUST_CONTRACT_PHONE",groupInfo.getString("GROUP_CONTACT_PHONE"));
            accessoryInfo.put("CUST_NAME",groupInfo.getString("CUST_NAME"));

            accessoryInfo.put("CUST_ADD",groupInfo.getString("GROUP_ADDR"));
            accessoryInfo.put("CONTACTS_B",managerInfo.getString("CUST_MANAGER_NAME"));
            accessoryInfo.put("CONTACT_PHONE_B",managerInfo.getString("SERIAL_NUMBER"));

            protocolInfo.put("PRODUCT_ID", pageData.getString("PRODUCT_ID"));
            accessoryInfo.put("AGREEMENT_DEF_ID",AGREEMENT_DEF_ID);

            //处理时间
            String year = SysDateMgr4Web.getSysDate("yy");
            accessoryInfo.put("YEAR_XX", transNum(year,"Y"));
            String month = SysDateMgr4Web.getCurMonth();
            accessoryInfo.put("MONTH_XX", transNum(month,"M"));

            //初始化账户信息
            initAcctInfo(accessoryInfo);

            accessoryInfo.put("BEGIN_DATE",SysDateMgr4Web.getSysDate(SysDateMgr4Web.PATTERN_STAND));
            accessoryInfo.put("END_DATE",SysDateMgr4Web.END_DATE_FOREVER);

            String sysTime = SysDateMgr4Web.getSysDate("yyyy年MM月dd日");
            accessoryInfo.put("DATE_1", sysTime);
            accessoryInfo.put("DATE_2", sysTime);
        }
        accessoryInfo.put("B_NAME_2",B_NAME);
        //protocolInfo.put("A_NAME", pageData.getString("CUST_NAME"));
		//查询开始和结束日期
		accessoryInfo.put("BEGIN_DATE", protocolInfo.getString("START_DATE"));
		accessoryInfo.put("END_DATE", protocolInfo.getString("END_DATE"));
		
    }

    /**
     * 初始化移动账户信息
     * @param accessoryInfo
     * @throws Exception
     */
    private void initAcctInfo(IData accessoryInfo)throws Exception{
        String staffId = getVisit().getStaffId();
        String cityCode = getVisit().getCityCode();

        IData input = new DataMap();
        input.put("CONFIG_NAME","MINOREC_CM_ACCT");
        input.put("PARAMNAME",cityCode);
        input.put("STATUS","0");

        IDataset acctInfos = CSViewCall.call(this,"SS.EweConfigQrySVC.qryParamValueByParamName",input);
        if(DataUtils.isNotEmpty(acctInfos)){
            accessoryInfo.put("OPEN_BANK_B",acctInfos.first().getString("RSRV_STR1"));//开户行
            accessoryInfo.put("OPEN_NAME_B",acctInfos.first().getString("RSRV_STR3"));//开户名
            accessoryInfo.put("OPEN_ACCT_B",acctInfos.first().getString("RSRV_STR2"));//开户账号
        }

    }

    protected IData buildFDFInfo(IData pageData) throws Exception{
        //转换KEY值
        changeKey(pageData);
        
        IData contextData = buildProtocolInfo(pageData);
        return contextData;
    }
    
    private void changeKey(IData pageData) throws Exception{
        String[] keys = pageData.getNames();
        for(String key:keys){
            String value = pageData.getString(key);
            if(key != null &&(key.startsWith("pc_")||key.startsWith("ph_"))){
                String newKey = key.substring(prex);
                pageData.remove(key);
                pageData.put(newKey, value);
            }
        }
        
    }

	protected IData buildProtocolInfo(IData pageData) throws Exception{
		IData archivesInfo = new DataMap();
		
		//转换单选框
		transChangeValueForPDF(pageData);
		
		IDataset archiveslist = new DatasetList();

        String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        if(StringUtils.isNotEmpty(aSignDate)){
            ElecLineUtil.setPDFDateField(aSignDate, pageData, "BEGIN_YEAR", "BEGIN_MONTH", "BEGIN_DAY");
        }
        if(StringUtils.isNotEmpty(bSignDate)){
            ElecLineUtil.setPDFDateField(bSignDate, pageData, "END_YEAR", "END_MONTH", "END_DAY");
        }

        IData input = new DataMap();
        input.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        IDataset relaList = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecTemplateRele", input);
        for(int i= 0;i<relaList.size();i++){
            IData relaData = relaList.getData(i);
            String attrCode = relaData.getString("PDF_ELE_CODE");
            String attrValue = pageData.getString(attrCode);
            if(StringUtils.isNotBlank(attrValue)){
                archivesInfo.put(attrCode, attrValue);
            }else if("0".equals(relaData.getString("PDF_ELE_TYPE"))){
                archivesInfo.put(attrCode, ElecLineUtil.DUFUALT_VALUE);
            }
        }

        archivesInfo.put("A_NAME",pageData.getString("A_NAME"));
        archivesInfo.put("B_NAME",B_NAME);
        archivesInfo.put("A_NAME2",pageData.getString("A_NAME_2"));
        archivesInfo.put("A_HEADER",pageData.getString("CONTACTS_A"));
        archivesInfo.put("B_HEADER",pageData.getString("CONTACTS_B"));
        archivesInfo.put("DATE_1", pageData.getString("SYS_DATE_A"));
        archivesInfo.put("DTAE_2", pageData.getString("SYS_DATE_B"));

		for (String key : archivesInfo.getNames()) {
			IData tempData = new DataMap();
			if(StringUtils.isNotEmpty(archivesInfo.getString(key))){
				tempData.put(key, archivesInfo.getString(key));
				archiveslist.add(tempData);
			}
		}
		IData datas = new DataMap();
		datas.put("DATAS", archiveslist);
		return datas;
	}
	
	private void transChangeValueForPDF(IData pageData) throws Exception{
	    //是否开通云wifi安审功能
	    String EPwifi = pageData.getString("EP_WIFI");
	    if("1".equals(EPwifi)){
	        pageData.put("EP_WIFI_YES", "1");
	    }else if("0".equals(EPwifi)){
	        pageData.put("EP_WIFI_NO", "1");
	    }
	    
	    //是否开通豪华定制包
	    String TVpkg = pageData.getString("TV_PKG");
	    if("1".equals(TVpkg)){
	        pageData.put("TV_PKG_YES", "1");
	    }else if("0".equals(TVpkg)){
	        pageData.put("TV_PKG_NO", "1");
	    }
	    
	    //集团类型
	    String grpClass = pageData.getString("GROUP_CLASS");
	    if("1".equals(grpClass)){
	        pageData.put("LGL_PER", "1");
	    }else if("2".equals(grpClass)){
	        pageData.put("ENT_PER", "1");
	    }else if("3".equals(grpClass)){
	        pageData.put("PUB_PER", "1");
	    }else if("4".equals(grpClass)){
	        pageData.put("SOC_PER", "1");
	    }else if("5".equals(grpClass)){
	        pageData.put("OTHER_PER", "1");
	    }else if("6".equals(grpClass)){
            pageData.put("IND_PRO", "1");
        }else if("7".equals(grpClass)){
            pageData.put("OTHER_ORG", "1");
        }
	    
	    //云酒馆开通业务版本
	    String CWOpenType = pageData.getString("CW_OPEN_TYPE");
	    if("1".equals(CWOpenType)){
            pageData.put("CW_STANDARD", "1");
        }else if("2".equals(CWOpenType)){
            pageData.put("CW_ELIT", "1");
        }else if("3".equals(CWOpenType)){
            pageData.put("CW_LUXURY", "1");
        }
	    
	    //付费方式
	    String payType = pageData.getString("PAY_TYPE");
	    if("1".equals(payType)){
	        pageData.put("CASH", "1");
	    }else if("2".equals(payType)){
	        pageData.put("COLLECTION", "1");
	    }else if("3".equals(payType)){
	        pageData.put("TRA_ACCT", "1");
	    }
	    
	}

	public IData saveProtocolInfo(IData pageData) throws Exception {
        
        IData archives = new DataMap();

        ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

		IData archivesInfo = buildSaveProtocolInfo(pageData);
		
		archives.put("ARCHIVES_INFO", archivesInfo);

		IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
		
		IDataset products = new DatasetList();
		archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData,products);
		archives.put("ARCHIVES_ATTRS", archivesAttrs);

		if(DataUtils.isEmpty(products)){
		    CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品为空，请选择产品后再提交！");
		}
		archives.put("PRODUCTS", products);
		
		//协议开始结束时间
		String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        if(StringUtils.isNotEmpty(aSignDate)){
            archives.put("START_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }
        if(StringUtils.isNotEmpty(bSignDate)){
            archives.put("END_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(bSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }
		
        //协议状态
        archives.put("ARCHIVES_STATE", "0");//待审核
        return archives;
    }
	
	private IData buildSaveProtocolInfo(IData pageData) throws Exception{
	    IData archivesInfo = new DataMap();
	    archivesInfo.put("AGREEMENT_ID", pageData.getString("AGREEMENT_ID"));
	    archivesInfo.put("A_NAME", pageData.getString("A_NAME"));
	    archivesInfo.put("B_NAME", B_NAME);
        archivesInfo.put("A_ADDRESS", pageData.getString("CUST_ADD"));
        archivesInfo.put("B_ADDRESS", pageData.getString("B_ADDRESS"));
        archivesInfo.put("A_HEADER", pageData.getString("CONTACTS_A"));
        archivesInfo.put("B_HEADER", pageData.getString("CONTACTS_B"));
        archivesInfo.put("A_CONTACT_PHONE", pageData.getString("CONTACT_PHONE_A"));
        archivesInfo.put("B_CONTACT_PHONE", pageData.getString("CONTACT_PHONE_B"));
        archivesInfo.put("A_BANK", pageData.getString("A_BANK"));
        archivesInfo.put("B_BANK", pageData.getString("B_BANK"));
        archivesInfo.put("A_SIGN_DATE", SysDateMgr4Web.getSysDate());
        archivesInfo.put("B_SIGN_DATE", SysDateMgr4Web.getSysDate());
        archivesInfo.put("A_BANK_ACCT_NO", pageData.getString("A_BANK_ACCT_NO"));
        archivesInfo.put("B_BANK_ACCT_NO", pageData.getString("B_BANK_ACCT_NO"));
        archivesInfo.put("PDF_FILE", pageData.getDataset("PDF_FILE"));
        archivesInfo.put("CONTRACT_CODE", pageData.getString("CONTRACT_CODE"));
	    return archivesInfo;
	}

	private IDataset buildSaveAccessoryInfo(IDataset archivesAttrs,IData pageData,IDataset products) throws Exception{
	    
	    //products.add(pageData.getString("PRODUCT_ID"));
	    IData input = new DataMap();
        input.put("AGREEMENT_DEF_ID", pageData.getString("AGREEMENT_DEF_ID"));
        IDataset relaList = CSViewCall.call(this, "SS.AgreementInfoSVC.queryElecTemplateRele", input);
        boolean conFlag = false;
        boolean ccFlag = false;
        for(int i= 0;i<relaList.size();i++){
            
            IData archivesInfo = new DataMap();
            IData relaData = relaList.getData(i);
            String attrCode = relaData.getString("PDF_ELE_CODE");
            String attrValue = pageData.getString(attrCode);
            if(StringUtils.isBlank(attrValue)){
                continue;
            }
            archivesInfo.put(attrCode, attrValue);
            archivesAttrs.add(archivesInfo);
            
            IData productInfo = new DataMap();
            if("CON_CNT".equals(attrCode) && "1".equals(attrValue)){//融合通信
                conFlag = true;
                ccFlag = true;
                productInfo.put("PRODUCT_ID", "2222");
                //productInfo.put("PRODUCT_ID", "VP998001");
            }else if("EPS_BBD".equals(attrCode)&&"1".equals(attrValue)){//企业宽带
                productInfo.put("PRODUCT_ID", "7341");
            }else if("BUSINESS_TV".equals(attrCode)&&"1".equals(attrValue)){//和商务TV
                productInfo.put("PRODUCT_ID", "380700");
            }else if("SD_WAN".equals(attrCode)&&"1".equals(attrValue)){//SD-WAN
                //productInfo.put("PRODUCT_ID", "");
            }else if("CLOUD_WINE".equals(attrCode)&&"1".equals(attrValue)){//云酒管
                productInfo.put("PRODUCT_ID", "921015");
            }else if("CLOUD_WIFI".equals(attrCode)&&"1".equals(attrValue)){//云WIFI安审版
                productInfo.put("PRODUCT_ID", "380300");
            }else if("CC_VNET".equals(attrCode)&&"1".equals(attrValue)){//融合V网
                ccFlag = true;
                productInfo.put("PRODUCT_ID", "8001");
            }/*else if("CC_CRBT".equals(attrCode)&&"1".equals(attrValue)){//多媒体彩铃
                ccFlag = true;
                productInfo.put("PRODUCT_ID", "2222");
            }*/
            
            if(DataUtils.isNotEmpty(productInfo)){
                archivesAttrs.add(productInfo);
                products.add(productInfo.getString("PRODUCT_ID"));
            }
            
        }
        
        //融合通信业务判断
        if(conFlag&&!ccFlag){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"您还未选择融合通信中的产品，请选择产品后再提交！");
        }

        //签署日期
        IData date1 = new DataMap();
        date1.put("DATE_1",pageData.getString("SYS_DATE_A"));
        archivesAttrs.add(date1);
        IData date2 = new DataMap();
        date2.put("DATE_2",pageData.getString("SYS_DATE_B"));
        archivesAttrs.add(date2);

        return archivesAttrs;
	}

	public IData updateProtocolInfo(IData pageData) throws Exception {
		// TODO Auto-generated method stub
		IData archives = new DataMap();
		archives.put("ARCHIVES_ID", pageData.getString("ARCHIVES_ID"));

		ElecLineUtil.setCommInfoField(archives,pageData,getVisit());

        IData archivesInfo = buildSaveProtocolInfo(pageData);
        
        archives.put("ARCHIVES_INFO", archivesInfo);

        IDataset archivesAttrs = ElecLineUtil.buildSaveAccessoryInfo(pageData);
        
        IDataset products = new DatasetList();
        archivesAttrs = buildSaveAccessoryInfo(archivesAttrs,pageData,products);
        archives.put("ARCHIVES_ATTRS", archivesAttrs);
        
        if(DataUtils.isEmpty(products)){
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品为空，请选择产品后再提交！");
        }

        archives.put("PRODUCTS", products);
        
        //协议开始结束时间
        String aSignDate = pageData.getString("BEGIN_DATE");
        String bSignDate = pageData.getString("END_DATE");

        if(StringUtils.isNotEmpty(aSignDate)){
            archives.put("START_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(aSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }
        if(StringUtils.isNotEmpty(bSignDate)){
            archives.put("END_DATE", SysDateMgr4Web.date2String(SysDateMgr4Web.string2Date(bSignDate, "yyyy年MM月dd日"),SysDateMgr4Web.PATTERN_STAND));
        }
        
        //协议状态
        archives.put("ARCHIVES_STATE", "0");//待审核
        
        return archives;
	}
	
	@Override
    public void onSubmit(IRequestCycle cycle) throws Exception {
	    checkedParam(getData());
	    
        super.onSubmit(cycle);
    }
	
	public void checkedParam(IData pageData) throws Exception{
	    if(DataUtils.isEmpty(pageData)){
	        CSViewException.apperr(CrmCommException.CRM_COMM_103,"提交参数不能为空！");
	    }
	    String[] names = pageData.getNames();
	    String productIn = pageData.getString("PRODUCT_ID");
	    StringBuilder products = new StringBuilder();
	    for(String name : names){
	        if(("pc_CON_CNT".equals(name)||"ph_CON_CNT".equals(name)) && "1".equals(pageData.get(name))){
	            products.append("VP998001,");
	        }else if(("pc_EPS_BBD".equals(name)||"ph_EPS_BBD".equals(name)) && "1".equals(pageData.get(name))){
	            products.append("7341,");
	        }else if(("pc_BUSINESS_TV".equals(name)||"ph_BUSINESS_TV".equals(name)) && "1".equals(pageData.get(name))){
	            products.append("380700,");
            }else if(("pc_CLOUD_WINE".equals(name)||"ph_CLOUD_WINE".equals(name)) && "1".equals(pageData.get(name))){
                products.append("921015,");
            }else if(("pc_CLOUD_WIFI".equals(name)||"ph_CLOUD_WIFI".equals(name)) && "1".equals(pageData.get(name))){
                products.append("380300,");
            }
	    }
	    
	    String productstr = products.toString();
	    if(StringUtils.isEmpty(productstr)){
	        CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
	    }
	    
	    String[] product = productstr.split(",");
	    if("VP99999".equals(productIn)){
	        for(String p : product){
	            if("380700".equals(p)||"921015".equals(p)||"380300".equals(p)){
	                CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
	            }
	        }
	    }else if("VP998001".equals(productIn)||"7341".equals(productIn)||"380700".equals(productIn)||"921015".equals(productIn)||"380300".equals(productIn)){
	        if(product.length !=1 || !product[0].equals(productIn)){
	            CSViewException.apperr(CrmCommException.CRM_COMM_103,"所选产品不正确，请修改后再提交！");
	        }
	    }
	}

    private String transNum(String number,String type) throws Exception{
	    StringBuilder sb = new StringBuilder();
	    if(StringUtils.isBlank(number)){
	        return "";
	    }else if("Y".equals(type)){
	        char[] chars = number.toCharArray();
	        for(int i=0;i<chars.length;i++){
	            int c = Integer.valueOf(String.valueOf(chars[i]));
	            
	            if(i==0 && c==0){
	                continue;
	            }else{
	                getChineseNum(sb,c);
	            }
	            
	        }
	    }else if("M".equals(type)){
	        int num = Integer.valueOf(number);
	        getChineseNum(sb,num);
	    }
	    
	    return sb.toString();
	    
	}
	
	private void getChineseNum(StringBuilder sb,int num) throws Exception{
	    switch (num) {
	    case ZERO:
	        sb.append("零");
	        break;
        case ONE:
            sb.append("一");
            break;
        case TWO:
            sb.append("二");
            break;
        case THREE:
            sb.append("三");
            break;
        case FOUR:
            sb.append("四");
            break;
        case FIVE:
            sb.append("五");
            break;
        case SIX:
            sb.append("六");
            break;
        case SEVEN:
            sb.append("七");
            break;
        case EIGHT:
            sb.append("八");
            break;
        case NINE:
            sb.append("九");
            break;
        case TEN:
            sb.append("十");
            break;
        case ELEVEN:
            sb.append("十一");
            break;
        case TWELVE:
            sb.append("十二");
            break;
        default:
            CSViewException.apperr(CrmCommException.CRM_COMM_103,"无效数字！");
            break;
        }
	}
}
