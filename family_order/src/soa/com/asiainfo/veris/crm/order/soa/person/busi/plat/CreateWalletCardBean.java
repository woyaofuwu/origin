
package com.asiainfo.veris.crm.order.soa.person.busi.plat;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;

import org.apache.log4j.Logger;

import com.ailk.common.config.GlobalCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.file.FileUtil;
import com.ailk.common.util.parser.ImpExpUtil;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.exception.CustException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.route.Route;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.SysDateMgr;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizBean;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bcf.seq.SeqMgr;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.IBossCall;

public class CreateWalletCardBean extends CSBizBean
{

    protected static Logger log = Logger.getLogger(CreateWalletCardBean.class);

    public static boolean removeDirectory(String file_path, boolean isall) throws Exception
    {
        File file = new File(file_path);
        if (!(file.exists()))
            return false;

        if (isall)
        {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; ++i)
            {
                File fileItem = fileList[i];
                if (fileItem.isDirectory())
                    removeDirectory(fileItem.getPath(), isall);
                else
                {
                    fileItem.delete();

                }
            }
        }

        return file.delete();
    }

    /**
     * @description 提交实名认证
     * @date 2014-06-11
     * @author zhuyu
     * @param param
     *            必须包含：SERIAL_NUMBER,custInfo_PSPT_ID,custInfo_CUST_NAME,custInfo_BOSS_ID
     * @return
     * @throws Exception
     */
    public IData checkRealName(IData param) throws Exception
    {
        IData custInfo = getCustInfoBySN(param).getData(0);

        IData inparam = new DataMap();
        inparam.put("AuthReq", "AuthReq");
        inparam.put("ID_TYPE", "01");
        inparam.put("SERIAL_NUMBER", param.getString("SERIAL_NUMBER", ""));
        if (custInfo.getString("PSPT_ID", "").length() == 18)
        {
            inparam.put("ID_CARD_TYPE", "01");
        }
        else if (custInfo.getString("PSPT_ID", "").length() == 15)
        {
            inparam.put("ID_CARD_TYPE", "02");
        }

        // String path="";
        String path = this.getDomainPath("WEB-INF");
        // String path="/"; //如果上面的方法不行，可以改成这样的，也可以把这个放到配置文件中。
        String fild_id = SeqMgr.getFileId();// DualMgr.getSeqId(pd, "SEQ_FILE_ID");
        fild_id = fild_id.substring(2, 9) + fild_id.substring(11, 16);// 文件序列号
        String path_fildid = path + fild_id + ".jpg";// 路径加文件名
        inparam.put("ID_CARD_NUM", custInfo.getString("PSPT_ID", ""));
        inparam.put("ID_CARD_NAME", custInfo.getString("CUST_NAME", ""));
        inparam.put("TRANS_ID", param.getString("custInfo_BOSS_ID", ""));
        inparam.put("ACTION_ID", getVisit().getDepartId());
        inparam.put("ACTION_USER_ID", getVisit().getStaffId());
        inparam.put("KIND_ID", "BIP2B082_T2040021_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编

        IDataset rets = IBossCall.callHttpIBOSS("IBOSS", inparam);

        IData data = (IDataUtil.isEmpty(rets)) ? null : rets.getData(0);

        // only for test start!

        // IData data = null; if(data == null){ data = new DataMap(); data.put("X_RSPCODE", "0000");
        // data.put("X_RESULTINFO", "success"); data.put("RSP_CODE", "00"); data.put("ID_CARD_DEPARTMENT",
        // "长沙市天心区公安分局"); }

        // only for test end!
        if (IDataUtil.isNotEmpty(data))
        {
            data.put("FILD_ID", fild_id);
            data.put("PATH_FILD_ID", path_fildid);
        }


        return data;
    }

    /**
     * @date 2014-06-11
     * @author zhuyu
     * @description
     * @param temp
     *            必须包含：AUTH_SERIAL_NUMBER,custInfo_PSPT_ID,custInfo_CUST_NAME,custInfo_BOSS_ID,custInfo_RSP_CODE
     * @return
     * @throws Exception
     */
    public IData checkRealNameFail(IData temp) throws Exception
    {
        // 由于认证组件返回的三户资料已经模糊化，这里只能再从表里面取一次，用于接口调用。
        IData custInfo = getCustInfoBySN(temp).getData(0);

        IData inparam = new DataMap();
        inparam.put("AuthReq", "AuthReq");
        inparam.put("ID_TYPE", "01");
        inparam.put("SERIAL_NUMBER", temp.getString("SERIAL_NUMBER", ""));

        if (custInfo.getString("PSPT_ID", "").length() == 18)
        {
            inparam.put("ID_CARD_TYPE", "01");
        }
        else if (custInfo.getString("PSPT_ID", "").length() == 15)
        {
            inparam.put("ID_CARD_TYPE", "02");
        }
        inparam.put("ID_CARD_NUM", custInfo.getString("PSPT_ID", ""));
        inparam.put("ID_CARD_NAME", custInfo.getString("CUST_NAME", ""));
        inparam.put("TRANS_ID", temp.getString("custInfo_BOSS_ID", ""));

        inparam.put("AUTH_FLAG", "0");
        if ("02".equals(temp.getString("custInfo_RSP_CODE", "")) || "03".equals(temp.getString("custInfo_RSP_CODE", "")))
        {
            inparam.put("AUTH_FAIL_REASON", temp.getString("custInfo_RSP_CODE", ""));
        }
        else
        {
            inparam.put("AUTH_FAIL_REASON", "04");
        }
        inparam.put("ACTION_ID", CSBizBean.getVisit().getDepartId());
        inparam.put("ACTION_USER_ID", CSBizBean.getVisit().getStaffId());
        inparam.put("KIND_ID", "BIP2B082_T2040023_0_0");// 交易唯一标识
        inparam.put("X_TRANS_CODE", "");// 交易编
        IDataset rets = IBossCall.callHttpIBOSS("IBOSS", inparam); // 正式上线需要把这句放开

        IData data = (IDataUtil.isEmpty(rets)) ? null : rets.getData(0);
        // only for test start

        // IData data = new DataMap(); data.put("X_RSPCODE", "0000");

        // only for test end;
        return data;

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
        String ftpSite = param.getString("FTP_SITE");
        String ftpPath = param.getString("FTP_PATH");
        String fullPathName = param.getString("FULL_PATH_NAME");
        String fileId = param.getString("FILE_ID");
        String downloadFileName = param.getString("DOWNLOAD_FILE_NAME");

        // 设置文件处理上下文
        ImpExpUtil.getImpExpManager().getFileAction().setVisit(getVisit());

        // 构建输入流上传文件到FTP服务器。 "personserv", "upload/attach"
        FileInputStream fileInputStream = new FileInputStream(fullPathName);
        String newFtpFileId = ImpExpUtil.getImpExpManager().getFileAction().upload(fileInputStream, ftpSite, ftpPath, fileId);

        // 获取文件下载的URL
        String url = ImpExpUtil.getDownloadPath(newFtpFileId, downloadFileName);
        fileInputStream.close();

        InputStream infile = new FileInputStream(fullPathName);
        int fileSize = infile.available();
        infile.close();

        // 删除临时文件
        removeDirectory(fullPathName, false);

        // 插上传文件序列文件
        IData filedata = new DataMap();
        filedata.put("FILE_ID", fileId);
        filedata.put("FILE_NAME", fileId + ".jpg");
        filedata.put("FILE_TYPE", "1");
        filedata.put("FILE_KIND", "2");
        filedata.put("FILE_PATH", "upload/attach");
        filedata.put("FILE_SIZE", fileSize);
        filedata.put("CREA_STAFF", getVisit().getStaffId());
        filedata.put("CREA_TIME", SysDateMgr.getSysTime());
        insertFile(filedata);

        IData rtnData = new DataMap();
        rtnData.put("URL", url);
        IDataset dataset = new DatasetList();
        dataset.add(rtnData);
        return dataset;
    }

    public IDataset getCustInfoBySN(IData data) throws Exception
    {

        IDataset dataset = null;

        if (StringUtils.isNotBlank(data.getString("SERIAL_NUMBER")))// 传手机号码根据手机号码查询
        {

            IData userInfos = UcaInfoQry.qryUserInfoBySn(data.getString("SERIAL_NUMBER"));

            if (IDataUtil.isNotEmpty(userInfos))
            {
                dataset = IDataUtil.idToIds(UcaInfoQry.qryPerInfoByCustId(userInfos.getString("CUST_ID")));
            }
        }

        if (IDataUtil.isEmpty(dataset))
        {
            CSAppException.apperr(CustException.CRM_CUST_105, data.getString("SERIAL_NUMBER"));
        }

        return dataset;
    }

    /**
     * @date 2014-06-11
     * @author zhuyu
     * @description 获取应用的根目录。
     * @param name
     * @return
     */
    public String getDomainPath(String name)
    {

        // String ss = System.getProperty("tmp.dir") + GlobalCfg.getProperty("file.temp.dir");

        URL url = CreateWalletCardBean.class.getResource("CreateWalletCardBean.class");
        File file = new File(url.getPath());
        String absolutepath = file.getParent();
        String pathname = absolutepath.substring(0, absolutepath.indexOf("WEB-INF") - 1);

        if (absolutepath.startsWith("/"))
        {
            pathname = pathname.substring(0, pathname.lastIndexOf("/"));
        }
        else
        {
            pathname = pathname.substring(0, pathname.lastIndexOf("\\"));
        }
        pathname = pathname + "/" + GlobalCfg.getProperty("file.temp.dir");

        // 如果临时目录不存在，则创建临时目录。
        FileUtil fileUtil = new FileUtil();
        fileUtil.createDirectory(pathname);
        return pathname;
    }

    public void insertFile(IData data) throws Exception
    {
        Dao.insert("TD_M_FILE", data, Route.CONN_CRM_CEN);
    }
}
