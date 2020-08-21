package com.asiainfo.veris.crm.iorder.soa.group.param.minorec.elecagreement;

import com.ailk.biz.BizEnv;
import com.ailk.biz.BizVisit;
import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.IData;
import com.ailk.common.util.DataUtils;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.ailk.service.protocol.ServiceException;
import com.asiainfo.veris.crm.order.pub.exception.CrmCommException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.itextpdf.text.Image;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import sun.misc.BASE64Decoder;

import java.io.*;

public class PaperlessCall {

    public static Image getContractSeal(BizVisit visit) throws Exception{

        //拼入参XML
        Document reqDoc = DocumentHelper.createDocument();
        Element reqRoot = reqDoc.addElement("req");

        String staffId = visit.getStaffId();
        String sealTypeStr = "2";
        String sealStr = "contract_seal";
        IDataset redStaffList = StaticUtil.getStaticList("RED_CONTRACT_STAFF");
        if(DataUtils.isNotEmpty(redStaffList)){
            for(int i =0;i<redStaffList.size();i++){
                IData redStaffData = redStaffList.getData(i);
                if(staffId != null && staffId.startsWith(redStaffData.getString("DATA_ID"))){//只有这个工号取红色章
                    sealTypeStr = "3";
                    sealStr = "red_contract_seal";
                    break;
                }
            }
        }
        /*if(staffId != null && staffId.startsWith("HNHKJK")){//只有这个工号取红色章
            sealTypeStr = "3";
            sealStr = "red_contract_seal";
        }else{
            sealTypeStr = "2";
            sealStr = "contract_seal";
        }*/

        //员工工号
        Element workNo =  reqRoot.addElement("work_no");
        workNo.addText(staffId);
        //workNo.addText("HNHKREW2");

        //员工部门ID
        Element orgInfo = reqRoot.addElement("org_info");
        orgInfo.addText(visit.getDepartId());
        //orgInfo.addText("55837");


        //0-公章，1-私章，2-合同章，3-合同章和红色合同章
        Element sealType = reqRoot.addElement("seal_type");
        sealType.addText(sealTypeStr);

        String content = reqDoc.asXML();
        content = "message="+content;

        String respStr = callPaperless(content,"UTF-8");
        if(StringUtils.isBlank(respStr)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "调用无纸化接口失败，获取报文为空！");
        }

        Document respDoc = DocumentHelper.parseText(respStr);
        Element respRoot = respDoc.getRootElement();
        Element retCode = respRoot.element("ret_code");
        String text = retCode.getText();
        //0-成功，1-失败，2-没有公章
        if("1".equals(text)){
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "调用无纸化接口失败["+respRoot.element("ret_msg").getText()+"]！");
        }else if("2".equals(text)){
            return null;
        }else if("0".equals(text)){
            String contractSealStr = respRoot.element(sealStr).getText();
            if(StringUtils.isNotBlank(contractSealStr)) {
                BASE64Decoder decoder = new BASE64Decoder();
                byte[] b = decoder.decodeBuffer(contractSealStr);
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {//调整异常数据
                        b[i] += 256;
                    }
                }
                //ByteArrayInputStream baInStream = new ByteArrayInputStream(b);
                Image contractSealImg = Image.getInstance(b);
                return contractSealImg;
            }
        }else {
            CSAppException.apperr(CrmCommException.CRM_COMM_103,  "获取合同章出错！");
        }

        return null;
    }

    public static String callPaperless(String content,String charset) throws Exception{

        String url = BizEnv.getEnvString("crm.contract.seal.url");

        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(HTTP.CONTENT_ENCODING, charset);
        HttpResponse response = null;
        HttpPost post = null;
        post = new HttpPost(url);
        post.getParams().setParameter("http.socket.timeout", new Integer(500000));
        post.setHeader("Content-type", "application/x-www-form-urlencoded; charset=" + charset);
        post.setHeader("Connection", "close");
        InputStreamEntity reqEntity = null;

        try {
            reqEntity = new InputStreamEntity(new ByteArrayInputStream(content.getBytes(charset)), -1L);
        } catch (UnsupportedEncodingException var17) {
            throw new ServiceException(var17);
        }

        reqEntity.setContentType("binary/octet-stream");
        reqEntity.setChunked(true);
        post.setEntity(reqEntity);
        try {
            response = client.execute(post);
        } catch (ClientProtocolException e) {
            throw new ServiceException(e);
        } catch (IOException e) {
            throw new ServiceException(e);
        }

        HttpEntity entity = response.getEntity();
        BufferedReader in = null;
        StringBuffer buffer = new StringBuffer();
        if (entity != null) {
            try {
                in = new BufferedReader(new InputStreamReader(entity.getContent(), charset));
                String line = null;
                while ((line = in.readLine()) != null) {
                    buffer.append(line);
                }
            } catch (UnsupportedEncodingException e) {
                throw new ServiceException(e);
            } catch (IllegalStateException e) {
                throw new ServiceException(e);
            } catch (IOException e) {
                throw new ServiceException(e);
            }finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return buffer.toString();

    }
}
