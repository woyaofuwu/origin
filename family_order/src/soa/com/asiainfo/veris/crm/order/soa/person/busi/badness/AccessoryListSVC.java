
package com.asiainfo.veris.crm.order.soa.person.busi.badness;

import java.util.List;

import org.apache.log4j.Logger;

import com.ailk.common.config.ModuleCfg;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.common.util.FtpUtil;
import com.asiainfo.veris.crm.order.pub.exception.DedInfoException;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.bcf.exception.CSAppException;

public class AccessoryListSVC extends CSBizService
{

    protected static Logger log = Logger.getLogger(AccessoryListSVC.class);

    // 一级BOSS主机地址
    public static String ibossIP;

    public static String ibossUser;

    public static String ibossPassword;

    // 一级BOSS彩信文件上传目录
    private static String ibossUpDir;

    // 一级BOSS彩信文件下发目录
    public static String ibossDownDir;

    static
    {
        try
        {
            ibossIP = ModuleCfg.getProperty("mmsfile/ftp/iboss_server");
            ibossUser = ModuleCfg.getProperty("mmsfile/ftp/iboss_user");
            ibossPassword = ModuleCfg.getProperty("mmsfile/ftp/iboss_password");
            ibossUpDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_updir");
            ibossDownDir = ModuleCfg.getProperty("mmsfile/ftp/iboss_downdir");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public IData checkFileExsist(IData input) throws Exception
    {
        AccessoryListBean bean = new AccessoryListBean();
        IData result = new DataMap();
        IData data = new DataMap();
        data.put("INFO_RECV_ID", input.getString("INFO_RECV_ID"));
        IDataset results = bean.queryAccessoryLists(data);
        String recvId = "", stickList = "", rsrv4 = "", recvPorvince = "";
        if (IDataUtil.isNotEmpty(results))
        {
            IData accessoryInfo = results.getData(0);
            recvId = accessoryInfo.getString("INFO_RECV_ID", "").trim();
            stickList = accessoryInfo.getString("STICK_LIST", "").trim();
            rsrv4 = accessoryInfo.getString("RSRV_STR4", "").trim();
            recvPorvince = accessoryInfo.getString("RECV_PROVINCE", "").trim();

            if (!"".equals(rsrv4))
            {
                result.put("RESULT_MESSAGE", "3");// 附件已解析
                result.put("INFO_RECV_ID", recvId);
                result.put("RSRV_STR4", rsrv4);
            }
            else
            {
                String ftpDir = ibossDownDir;
                FtpUtil ftp = new FtpUtil(ibossIP, ibossUser, ibossPassword, ftpDir);
                boolean isExist = false;
                List fileList = ftp.getFileList(ftpDir);
                for(int j=0; j<fileList.size(); j++){
                    if(fileList.get(j).equals(stickList)){
                        isExist = true;
                        break;
                    }
                }
//                if (fileList.contains(stickList) || fileList.contains(stickList + ".gz"))
//                {
//                    isExist = true;
//                }
                if (!isExist)
                {
                    result.put("RESULT_MESSAGE", "1");// 附件未下载
                }
                else
                {
                    result.put("RESULT_MESSAGE", "2");// //附件已下载
                    result.put("INFO_RECV_ID", recvId);
                    result.put("RSRV_STR4", rsrv4);
                    result.put("RECV_PROVINCE", recvPorvince);
                    result.put("STICK_LIST", stickList);
                }
                ftp.closeServer();
            }
        }
        else
        {
            result.put("RESULT_MESSAGE", "0");// 未找到工单
        }

        IData idata = new DataMap();
        idata.put("RET", result);
        return idata;
    }

    public IData queryAccessoryList(IData input) throws Exception
    {

        AccessoryListBean bean = new AccessoryListBean();
        String recvId = input.getString("INFO_RECV_ID");
        String rsrv4 = input.getString("RSRV_STR4");

        // 未解析彩信内容
        if ("".equals(rsrv4))
        {
            String recvPorvince = input.getString("RECV_PROVINCE");
            Mm7Parse parse = new Mm7Parse();
            int type = 1;
            String stickList = input.getString("STICK_LIST");
            String fileList = parse.resolvingMmsContent(stickList, recvId, type);
            if (fileList != null && !fileList.equals(""))
            {
                IData params = new DataMap();
                params.put("INFO_RECV_ID", recvId);
                params.put("RSRV_STR4", fileList);
                bean.saveBADNESSInfos("TF_F_BADNESS_INFO", params, new String[]
                { "INFO_RECV_ID" });

                rsrv4 = fileList;
            }
        }
        String url = "";
        IData urldata = new DataMap();
        urldata.put("PARAM_ATTR", "7777");
        urldata.put("PARAM_CODE", "URL");
        IDataset urlset = bean.queryUrlPara(urldata);
        if (urlset != null && urlset.size() > 0)
        {
            url = urlset.getData(0).getString("PARA_CODE1", "").trim();
        }
        else
        {
            CSAppException.apperr(DedInfoException.CRM_DedInfo_16);
        }

        IDataset temps = new DatasetList();
        String[] accessoryList = rsrv4.split(";");
        for (int i = 0; i < accessoryList.length; i++)
        {
            IData temp = new DataMap();
            temp.put("INFO_RECV_ID", recvId + "_" + accessoryList[i].trim());
            temp.put("BLOCK_NAME", accessoryList[i].trim());
            temp.put("URL", url.trim() + recvId.substring(0, 8) + "/" + recvId + "/" + accessoryList[i].trim());

            temps.add(temp);
        }

        IData idata = new DataMap();
        idata.put("RET", temps);
        return idata;
    }

}
