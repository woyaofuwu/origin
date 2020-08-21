
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import sun.misc.BASE64Decoder;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.service.bean.BeanManager;
import com.asiainfo.veris.crm.order.pub.exception.PlatException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.param.CommparaInfoQry;

public class CreateWalletCardSVC extends CSBizService
{

    public void base64ToIo(String strBase64, String path) throws Exception
    {
        String string = strBase64;
        String fileName = path; // 生成的新文件
        try
        {
            // 解码，然后将字节转换为文件
            byte[] bytes = new BASE64Decoder().decodeBuffer(string); // 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            FileOutputStream out = new FileOutputStream(fileName);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1)
            {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }

            in.close();
            out.close();

        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }

    }

    public IDataset checkRealName(IData param) throws Exception
    {
        CreateWalletCardBean bean = (CreateWalletCardBean) BeanManager.createBean(CreateWalletCardBean.class);
        IData data = bean.checkRealName(param);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    public IDataset checkRealNameFail(IData param) throws Exception
    {
        CreateWalletCardBean bean = (CreateWalletCardBean) BeanManager.createBean(CreateWalletCardBean.class);
        IData data = bean.checkRealNameFail(param);
        IDataset dataset = new DatasetList();
        dataset.add(data);
        return dataset;
    }

    /**
     * @date 2014-06-11
     * @author zhuyu
     * @description 上传本地文件到FTP服务器，并获取下载路径,删除临时文件，插上传文件序列文件。
     * @param param
     * @return
     * @throws Exception
     */
    public IDataset dealFile(IData param) throws Exception
    {

        CreateWalletCardBean bean = (CreateWalletCardBean) BeanManager.createBean(CreateWalletCardBean.class);
        return bean.dealFile(param);
    }

    public void dealIoToBase64(IData data) throws Exception
    {
        String path_fild = data.getString("PATH_FILD");
        testIoToBase64(data.getString("BASE"), data.getString("PATH_FILD"));

        File file1 = new File(path_fild);
        if (!file1.exists())
        {
            CSAppException.apperr(PlatException.CRM_PLAT_87);
        }
    }

    public IData getTradeId(IData data) throws Exception
    {
        IData result = new DataMap();
        result.put("TRADE_ID", SeqMgr.getTradeId());
        return result;
    }

    public IDataset querySvcstatecomm(IData param) throws Exception
    {
        CreateWalletCardBean bean = (CreateWalletCardBean) BeanManager.createBean(CreateWalletCardBean.class);
        return CommparaInfoQry.getInfoParaCode3(param.getString("SUBSYS_CODE"), param.getString("PARAM_ATTR"), param.getString("PARA_CODE3"));
    }

    public void testIoToBase64(String strBase64, String path) throws Exception
    {
        try
        {

            base64ToIo(strBase64, path); // 将 base64编码转换为 io 文件流，生成一幅新图片

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
