package com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.bbossOrderRegist;


import com.ailk.biz.util.TimeUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.group.bbossBiz.common.commonmethod.MebCommonBean;

/**
 * @description 外围接口数据统一处理，包括数据入表，异常日志登记
 * @author xunyl
 * @date 2015-02-02
 */
public class bbossCenterControl {

    /**
     * @description 该方法用于外围接口数据入表
     * @author xunyl
     * @date 2015-02-02
     */
    public static String rigistXmlData(IData map,String dealState)throws Exception {
      //1- 登记外围接口的主要信息，入报文主表
        String seqId = rigistBBossXMLMainInfo(map,dealState);
        
        //2- 登记外围接口的所有数据，入报文详细表
        rigistBBossXMLContentInfo(map,seqId);
        
        //3- 返回主键值
        return seqId;
    }
    
    /**
     * @description 该方法用于登记外围接口数据的主信息
     * @author xunyl
     * @date 2015-02-02
     */
    private static String rigistBBossXMLMainInfo(IData map,String dealState)throws Exception {
        //1- 定义报文主表信息对象
        IData bbossXmlMainInfo = new DataMap();
        
        //2- 添加主键编号
        String seqId = TimeUtil.getSysDate("yyyyMMdd", true)+SeqMgr.getXmlInfoId();
        bbossXmlMainInfo.put("SEQ_ID", seqId);
        
        //3- 添加BIPCODE
        String bipcode = map.getString("BIPCODE");
        if(StringUtils.isEmpty(bipcode)){
            bipcode=map.getString("KIND_ID");
        }
        bbossXmlMainInfo.put("BIPCODE", bipcode);
        
        //4- 获取TRANDS_IDO
        String transIdo = map.getString("TRANSIDO");
        if(StringUtils.isNotEmpty(transIdo)){
            bbossXmlMainInfo.put("TRANDS_IDO", transIdo);
        }else{
            bbossXmlMainInfo.put("TRANDS_IDO", map.getString("IBSYSID"));//文件接口没有TRANDS_IDO 只有IBSYSID；
        }
        
        //5- BIPCODE为商产品同步接口，则获取商品规格编码
        if(StringUtils.endsWithIgnoreCase("BIP4B253", bipcode)){
            String poSpecNumber = map.getString("RSRV_STR3");
            bbossXmlMainInfo.put("PO_SPEC_NUMBER", poSpecNumber);
            bbossXmlMainInfo.put("XML_ACTION", "01");
        }
        
        //6- BIPCODE为集团业务接口，则获取集团客户编码、商品订单号
        if(StringUtils.endsWithIgnoreCase("BIP4B255", bipcode) ||
                StringUtils.contains(bipcode, "POOrderService")){
            String ecCustomerNumber = map.getString("EC_SERIAL_NUMBER");
            String poOrderNumber = map.getString("SUBSCRIBE_ID");
            bbossXmlMainInfo.put("EC_CUSTOMER_NUMBER", ecCustomerNumber);
            bbossXmlMainInfo.put("PO_ORDER_NUMBER", poOrderNumber);
            bbossXmlMainInfo.put("XML_ACTION", "05");
        }
        
        //7- BIPCODE为成员业务接口，则获取产品订购关系编码
        if(StringUtils.endsWith("BIP4B257", bipcode)||StringUtils.contains(bipcode, "MemberService") ||
                StringUtils.contains(bipcode,"MemberRspService_BBOSS")){
            String productOfferId = map.getString("PRODUCTID");
            bbossXmlMainInfo.put("PRODUCT_OFFER_ID", productOfferId);   
            String serialNumber = map.getString("SERIAL_NUMBER");
            bbossXmlMainInfo.put("SERIAL_NUMBER", serialNumber);    
            //归档或者文件接口的成员开通，类别为15，如果实时接口成员开通，类别为16
            bbossXmlMainInfo.put("XML_ACTION", "15");
            String returnFlag = map.getString("RETURN_FLAG_KT", "");
            String offerId = map.getString("ORDER_NO", "");
            if("".equals(returnFlag) && !"".equals(offerId)){
                bbossXmlMainInfo.put("XML_ACTION", "16");
            }
            String productOrderNumber = map.getString("ORDER_NO");
            bbossXmlMainInfo.put("PRODUCT_ORDER_NUMBER", productOrderNumber);
        }
        
        //8- BIPCODE为商品订单处理失败通知业务接口，则获取商品订单号
        if(StringUtils.endsWith("BIP4B258", bipcode)){
            String poOrderNumber = map.getString("SUBSCRIBE_ID");
            bbossXmlMainInfo.put("PO_ORDER_NUMBER", poOrderNumber);
            bbossXmlMainInfo.put("XML_ACTION", "20");
        }
        
        //9- BIPCODE为管理节点接口，则获取商品订单号
        if(StringUtils.endsWith("BIP4B259", bipcode)){
            String poOrderNumber = map.getString("SUBSCRIBE_ID");
            bbossXmlMainInfo.put("PO_ORDER_NUMBER", poOrderNumber);
            bbossXmlMainInfo.put("XML_ACTION", "25");
            //管理节点虚拟一个手机号码节点，目的是GTM扫描时能够正常扫描出数据
            bbossXmlMainInfo.put("SERIAL_NUMBER", "111111111110");
        }
        
        //10- BIPCODE为工单开通业务接口,则获取产品订单号
        if(StringUtils.endsWith("BIP4B256", bipcode)){
            String productOrderNumber = map.getString("ORDER_NO");
            bbossXmlMainInfo.put("PRODUCT_ORDER_NUMBER", productOrderNumber);
            bbossXmlMainInfo.put("XML_ACTION", "10");
        }
        
        //11- BIPCODE为工单流转状态同步接口，则获取商品订单号
        if(StringUtils.endsWith("BIP4B260", bipcode)){
            String poOrderNumber = map.getString("SUBSCRIBE_ID");
            bbossXmlMainInfo.put("PO_ORDER_NUMBER", poOrderNumber);
            bbossXmlMainInfo.put("XML_ACTION", "30");
        }
        
        //12- BIPCODE为流量叠加包接口,则获取成员订购订单号
        if(StringUtils.endsWith("BIP4B262", bipcode)){
            String memberOrderNumber = map.getString("MEMBER_ORDER_NUMBER");
            bbossXmlMainInfo.put("MEMBER_ORDER_NUMBER", memberOrderNumber);
            String serialNumber = map.getString("SERIAL_NUMBER");
            bbossXmlMainInfo.put("SERIAL_NUMBER", serialNumber);
            //归档或者文件接口的叠加包开通，类别为35，如果实时接口叠加包开通，类别为36
            bbossXmlMainInfo.put("XML_ACTION", "35");
            String returnFlag = map.getString("RETURN_FLAG_KT", "");
            String offerId = map.getString("MEMBER_ORDER_NUMBER", "");
            if("".equals(returnFlag) && !"".equals(offerId)){
                bbossXmlMainInfo.put("XML_ACTION", "36");
            }
            String mermberOrderRate = map.getString("MEMBER_ORDER_RATE"); 
            bbossXmlMainInfo.put("MEMBER_ORDER_RATE", mermberOrderRate);
            
            //增加国际流量统付上线保障标识，RSRV_STR3=99910 代表国际流量统付
            bbossXmlMainInfo.put("RSRV_STR3", map.getString("PRODUCT_SPEC_NUMBER","BIPCODE为流量叠加包接口"));     

        }
        
      // BIPCODE为流量叠加包暂停，恢复接口 ADD BY wangzc7 2017-8-2
        if(StringUtils.endsWith("BIP4B268", bipcode)){
        	String orderType = map.getString("ORDER_TYPE");//操作类型 22-暂停添加成员/叠加包 23-恢复添加成员/叠加包
    		String productOfferingId = map.getString("ORDER_ID");//订购关系ID
    		String illegOrderNumber = map.getString("ILLEG_ORDER_NUMBER");//省内违规信息工单号
    		String custNumber = map.getString("CUSTOMER_NUMBER");//集团客户在省内的编码
        	
            bbossXmlMainInfo.put("XML_ACTION", orderType);//违规信息的暂停添加成员/叠加包，类别为22，如果恢复添加成员/叠加包，类别为23
            bbossXmlMainInfo.put("PRODUCT_OFFER_ID", productOfferingId);
            bbossXmlMainInfo.put("EC_CUSTOMER_NUMBER", custNumber);
            bbossXmlMainInfo.put("RSRV_STR3", illegOrderNumber);
            bbossXmlMainInfo.put("RSRV_STR4", map.getString("HOST_COMPANY"));//主办省
            bbossXmlMainInfo.put("RSRV_STR5", map.getString("TEST_DATE"));//拨测日期
            bbossXmlMainInfo.put("RSRV_STR6", map.getString("BUSI_SCOPE"));//地域范围  1：跨省业务 2：本省业务
            bbossXmlMainInfo.put("RSRV_STR7", map.getString("STOP_REASON"));//关停原因
                        
        }
        
        //13- 添加报文的落地时间
        bbossXmlMainInfo.put("LOCATE_TIME", SysDateMgr.getSysTime());
        
        //14- 添加报文的处理时间
        bbossXmlMainInfo.put("DEAL_TIME", SysDateMgr.getSysTime());
        
        //15- 添加报文处理状态（0-延迟处理，1-处理失败）
        bbossXmlMainInfo.put("DEAL_STATE",dealState);
        
        //16- 添加处理结果编码
        bbossXmlMainInfo.put("OPEN_RESULT_CODE",map.getString("OPEN_RESULT_CODE",""));
        
        //17- 添加处理结果说明
        bbossXmlMainInfo.put("OPEN_RESULT_DESC",map.getString("OPEN_RESULT_DESC",""));
        
        //18- 登记参数编号(供成员签约工单用)
        bbossXmlMainInfo.put("CHARACTER_ID", map.getDataset("CHARACTER_ID"));
        
        //19- 登记参数名称
        bbossXmlMainInfo.put("CHARACTER_NAME", map.getDataset("CHARACTER_NAME"));
        
        //20- 登记参数值
        bbossXmlMainInfo.put("CHARACTER_VALUE", map.getDataset("CHARACTER_VALUE"));
        
        //21- 登记PKGSEQ
        bbossXmlMainInfo.put("RSRV_STR1", "".equals(map.getString("pkgSeq",""))?map.getString("PKGSEQ","") : map.getString("pkgSeq",""));
        
        //22- 登记OPR_NUMB
        bbossXmlMainInfo.put("RSRV_STR2", map.getString("PKGSEQ", ""));
        
        //23- 如果有错误信息需要登记
        String errorInfo = map.getString("ERROR_DETAIL","");
        if(StringUtils.isNotBlank(errorInfo)){
            String[] detailErrArr = MebCommonBean.splitStringByBytes(errorInfo,4000);
            for(int i=0;i<10;i++){
                bbossXmlMainInfo.put("ERROR_INFO_"+(i+1), detailErrArr[i]);
            }
        }
        
        //5- 是否需要回传IBOSS
        if ("Y".equals(map.getString("NOT_RSP")))
        {
        	bbossXmlMainInfo.put("IBOSS_RESULT", "9");
        }
        
        //24- 调用方法保存
        Dao.delete("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo, Route.CONN_CRM_CEN);
        Dao.insert("TF_TP_BBOSS_XML_INFO", bbossXmlMainInfo,Route.CONN_CRM_CEN);
        
        //25- 返回主键
        return seqId;      
    }
    
    /**
     * @description 该方法用于登记外围接口数据报文内容
     * @author xuny
     * @date 2015-02-02
     */
    private static void rigistBBossXMLContentInfo(IData map,String mainKey)throws Exception {
        //1- 定义报文内容信息对象
        IData bbossXmlContentInfo = new DataMap();
        
        //2- 添加主键
        bbossXmlContentInfo.put("SEQ_ID", mainKey);
        
        //3- 添加报文流水号
        String transIdo = map.getString("TRANSIDO");
        if(StringUtils.isNotEmpty(transIdo)){
            bbossXmlContentInfo.put("TRANDS_IDO", transIdo);
        }else{
            bbossXmlContentInfo.put("TRANDS_IDO", map.getString("IBSYSID"));//文件接口没有TRANDS_IDO 只有IBSYSID；
        }
        
        //4- 分割报文串，分别保存到对应的字段中
        String xmlContent = map.toString();
        String[] xmlContentArr = MebCommonBean.splitStringByBytes(xmlContent,4000);
        for(int i=0;i<10;i++){
            bbossXmlContentInfo.put("XML_CONTENT_"+(i+1), xmlContentArr[i]);
        }

        //5- 是否需要回传IBOSS
        if ("Y".equals(map.getString("NOT_RSP")))
        {
            bbossXmlContentInfo.put("IBOSS_RESULT", "9");
        }
        //5- 调用方法保存
        Dao.delete("TF_TP_BBOSS_XML_CONTENT", bbossXmlContentInfo, Route.CONN_CRM_CEN);
        Dao.insert("TF_TP_BBOSS_XML_CONTENT", bbossXmlContentInfo,Route.CONN_CRM_CEN);
    }       
}