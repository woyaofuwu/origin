
package com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.service;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ailk.biz.bcc.ConcurrentKeeper;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.AssemDynData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncAssemDynDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.EncPresetData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.LocalDecPreDataRsp;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreData;
import com.asiainfo.veris.crm.order.soa.frame.csservice.person.webservice.carddata.RoamEncPreDataRsp;

public class WebServiceClient
{

    protected static Logger log = Logger.getLogger(WebServiceClient.class);

    public LocalDecPreDataRsp decPreData(LocalDecPreData ass) throws Exception
    {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_decPreData");

        String eparchyCode = "0898";
        String wsdl = getWsdladress(eparchyCode);
        CRMServicePortTypeProxy pro = new CRMServicePortTypeProxy(wsdl);
        CRMServicePortType result = pro.getCRMServicePortType();
        LocalDecPreDataRsp en = new LocalDecPreDataRsp();
        String resp = result.localDecEncyPreData(getDecPreDataString(ass));
        en = getLocalRspBean(resp);
        return en;
    }

    /**
     * 实时写卡数据加密及报文组装 webservice接口传入参数 获取应答文件xml字符串
     * 
     * @param req
     * @throws Exception
     */
    public EncAssemDynDataRsp encAssemClient(AssemDynData ass) throws Exception
    {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_encAssemClient");

        String eparchyCode = "0898";
        String wsdl = getWsdladress(eparchyCode);
        CRMServicePortTypeProxy pro = new CRMServicePortTypeProxy(wsdl);
        CRMServicePortType result = pro.getCRMServicePortType();
        EncAssemDynDataRsp en = new EncAssemDynDataRsp();
        String resp = result.encAssemDynData(getEncAssemDynDataString(ass));
        en = getRspBean(resp);
        return en;
    }

    public RoamEncPreDataRsp encPreData(RoamEncPreData ass) throws Exception
    {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_encPreData");

        String eparchyCode = "0898";
        String wsdl = getWsdladress(eparchyCode);
        CRMServicePortTypeProxy pro = new CRMServicePortTypeProxy(wsdl);
        CRMServicePortType result = pro.getCRMServicePortType();
        RoamEncPreDataRsp en = new RoamEncPreDataRsp();
        String resp = result.roamDecEncyPreData(getEncPreDataString(ass));
        en = getRoamRspBean(resp);
        return en;
    }

    /**
     * 写卡返回接口传入应答文件xml 返回bean对象
     * 
     * @param xml
     * @return
     */
    public EncAssemDynDataRsp getCheckRspBean(String xml)
    {
        StringReader read = new StringReader(xml);

        SAXBuilder sb = new SAXBuilder();
        EncAssemDynDataRsp rsq = new EncAssemDynDataRsp();

        Document doc;
        try
        {
            doc = sb.build(read);
            Element root = doc.getRootElement();
            List content = root.getChildren("WriteCardStatusRsp");
            Element root1 = (Element) content.get(0);
            String seqNo = root1.getChild("SeqNo").getText();
            String resultCode = root1.getChild("ResultCode").getText();
            String resultMessage = root1.getChild("ResultMessage").getText();

            rsq.setSeqno(seqNo);
            rsq.setResultCode(resultCode);
            rsq.setResultMessage(resultMessage);
        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rsq;

    }

    private String getDecPreDataString(LocalDecPreData local)
    {

        String str = "<CRM2OPS><LocalDecEncyPreData><SeqNo>" + local.getSeqNo() + "</SeqNo><EncPresetDataK>" + local.getEncPresetDataK() + "</EncPresetDataK><EncPresetDataOPc>" + local.getEncPresetDataOPc() + "</EncPresetDataOPc><Signature>"
                + local.getSignature() + "</Signature><LocalProvCode>" + local.getLocalProvCode() + "</LocalProvCode></LocalDecEncyPreData></CRM2OPS>";

        return str;

    }

    // 将对象转换成xml报文格式
    private String getEncAssemDynDataString(AssemDynData ass)
    {

        String str = "<CRM2OPS><AssemDynData><SeqNo>" + ass.getSeqNo() + "</SeqNo><CardInfo>" + ass.getCardInfo() + "</CardInfo><ChannelFlag>" + ass.getChanelflg() + "</ChannelFlag><EncAssemDynData>";
        String issueData = "";
        for (int i = 0; i < ass.getEnc().size(); i++)
        {
            issueData = issueData + "<MSISDN>" + ass.getEnc().get(i).getMsisdn() + "</MSISDN>" + "<IssueData><ICCID>" + ass.getEnc().get(i).getIssueData().getIccId() + "</ICCID>" + "<IMSI>" + ass.getEnc().get(i).getIssueData().getImsi() + "</IMSI>"
                    + "<SMSP>" + ass.getEnc().get(i).getIssueData().getSmsp() + "</SMSP>" + "<PIN1>" + ass.getEnc().get(i).getIssueData().getPin1() + "</PIN1>" + "<PIN2>" + ass.getEnc().get(i).getIssueData().getPin2() + "</PIN2>" + "<PUK1>"
                    + ass.getEnc().get(i).getIssueData().getPuk1() + "</PUK1>" + "<PUK2>" + ass.getEnc().get(i).getIssueData().getPuk2() + "</PUK2></IssueData>";

        }
        str = str + issueData + "</EncAssemDynData></AssemDynData></CRM2OPS>";

        return str;

    }

    private String getEncPreDataString(RoamEncPreData roam)
    {

        String str = "<CRM2OPS><RoamDecEncyPreData><SeqNo>" + roam.getSeqNo() + "</SeqNo><EncPresetData><K>" + roam.getEncPresetData().getK() + "</K><OPc>" + roam.getEncPresetData().getOPC() + "</OPc></EncPresetData><LocalProvCode>"
                + roam.getLocalProvCode() + "</LocalProvCode></RoamDecEncyPreData></CRM2OPS>";

        return str;
    }

    public LocalDecPreDataRsp getLocalRspBean(String xml)
    {
        StringReader read = new StringReader(xml);

        SAXBuilder sb = new SAXBuilder();
        LocalDecPreDataRsp rsq = new LocalDecPreDataRsp();

        Document doc;
        try
        {
            doc = sb.build(read);
            Element root = doc.getRootElement();
            List content = root.getChildren("LocalDecEncyPreDataRsp");
            Element root1 = (Element) content.get(0);
            String seqNo = root1.getChild("SeqNo").getText();
            String resultCode = root1.getChild("ResultCode").getText();
            String resultMessage = root1.getChild("ResultMessage").getText();

            String k = root1.getChild("PresetData").getChildText("K");
            String oPc = root1.getChild("PresetData").getChildText("OPc");

            rsq.setSeqNo(seqNo);
            rsq.setResultCode(resultCode);
            rsq.setResultMessage(resultMessage);

            EncPresetData presetData = new EncPresetData();
            presetData.setK(k);
            presetData.setOPC(oPc);

            rsq.setPresetData(presetData);

        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return rsq;
    }

    /*
     * public static void main(String [] rags) throws Exception{ WebServiceClient client = new WebServiceClient();
     * AssemDynData data = new AssemDynData(); data.setCardInfo("080A986800791241097267800E0A21140045080090000024");
     * data.setChanelflg("1"); data.setCardRsp("3040731B0E"); //data.setCardRsp(cardRsp); EncAssemDynData enc = new
     * EncAssemDynData(); enc.setMsisdn("13627573496"); IssueData da = new IssueData();
     * da.setIccId("89860064211490028308"); da.setImsi("460007511448308"); da.setPin1("1234"); da.setPin2("0246");
     * da.setPuk1("07330276"); da.setPuk2("09416371"); da.setSmsp("+8613800898500"); enc.setIssueData(da);
     * List<EncAssemDynData> list = new ArrayList<EncAssemDynData>(); list.add(enc); data.setEnc(list);
     * data.setSeqNo("0651490472"); EncAssemDynDataRsp rsp = client.repCheckClient(data);
     * (rsp.getResultMessage()+rsp.getResultCode()); }
     */

    // 写卡后验证将对象转换成xml报文格式
    private String getResEncAssemDynDataString(AssemDynData ass)
    {

        String str = "<CRM2OPS><WriteCardStatus><SeqNo>" + ass.getSeqNo() + "</SeqNo><CardInfo>" + ass.getCardInfo() + "</CardInfo><CardRsp>";
        str = str + ass.getCardRsp() + "</CardRsp></WriteCardStatus></CRM2OPS>";
        return str;

    }

    public RoamEncPreDataRsp getRoamRspBean(String xml)
    {
        StringReader read = new StringReader(xml);

        SAXBuilder sb = new SAXBuilder();
        RoamEncPreDataRsp rsq = new RoamEncPreDataRsp();

        Document doc;
        try
        {
            doc = sb.build(read);
            Element root = doc.getRootElement();
            List content = root.getChildren("RoamDecEncyPreDataRsp");
            Element root1 = (Element) content.get(0);
            String seqNo = root1.getChild("SeqNo").getText();
            String resultCode = root1.getChild("ResultCode").getText();
            String resultMessage = root1.getChild("ResultMessage").getText();
            String encPresetDataK = root1.getChild("EncPresetDataK").getText();
            String encPresetDataOPc = root1.getChild("EncPresetDataOPc").getText();
            String signature = root1.getChild("Signature").getText();

            rsq.setSeqNo(seqNo);
            rsq.setResultCode(resultCode);
            rsq.setResultMessage(resultMessage);
            rsq.setEncPresetDataK(encPresetDataK);
            rsq.setEncPresetDataOPc(encPresetDataOPc);
            rsq.setSignature(signature);

        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rsq;

    }

    /**
     * 实时写卡数据加密及报文组装 传入应答文件xml 返回bean对象
     * 
     * @param xml
     * @return
     */
    public EncAssemDynDataRsp getRspBean(String xml)
    {
        StringReader read = new StringReader(xml);

        SAXBuilder sb = new SAXBuilder();
        EncAssemDynDataRsp rsq = new EncAssemDynDataRsp();

        Document doc;
        try
        {
            doc = sb.build(read);
            Element root = doc.getRootElement();
            List content = root.getChildren("EncAssemDynDataRsp");
            Element root1 = (Element) content.get(0);
            String seqNo = root1.getChild("SeqNo").getText();
            String resultCode = root1.getChild("ResultCode").getText();
            String resultMessage = root1.getChild("ResultMessage").getText();
            String issueData = root1.getChild("IssueData").getText();

            rsq.setSeqno(seqNo);
            rsq.setResultCode(resultCode);
            rsq.setResultMessage(resultMessage);
            rsq.setIssueData(issueData);

        }
        catch (JDOMException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return rsq;

    }

    public String getWsdladress(String eparchyCode) throws Exception
    {

        IDataset tempInfos = CommparaInfoQry.getCommparaAllColByParser("CSM", "311", "", "");
        if (IDataUtil.isEmpty(tempInfos))
        {
            CSAppException.apperr(CrmCommException.CRM_COMM_103, "获取远程写卡参数失败！");
        }
        IData remoteWriteCardInfo = tempInfos.getData(0);
        // 如果传了控件版本，则进行校验（数据库里的ocx版本若为空，则不校验）
        return remoteWriteCardInfo.getString("PARA_CODE20");
        // return "http://10.154.90.49/HNOpsWebService/OpsService";
    }

    /**
     * 写卡后验证报文 webservice接口传入参数 获取应答文件xml字符串
     * 
     * @param req
     * @throws Exception
     */
    public EncAssemDynDataRsp repCheckClient(AssemDynData ass) throws Exception
    {
        // 业务并发控制
        ConcurrentKeeper.protect("SVC_repCheckClient");

        String eparchyCode = "0898";
        String wsdl = getWsdladress(eparchyCode);
        CRMServicePortTypeProxy pro = new CRMServicePortTypeProxy(wsdl);
        CRMServicePortType result = pro.getCRMServicePortType();
        EncAssemDynDataRsp en = new EncAssemDynDataRsp();
        String resp = result.writeCardStatus(getResEncAssemDynDataString(ass));
        en = getCheckRspBean(resp);
        return en;
    }
}
