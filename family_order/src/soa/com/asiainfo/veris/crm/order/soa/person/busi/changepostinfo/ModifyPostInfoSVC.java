
package com.asiainfo.veris.crm.order.soa.person.busi.changepostinfo;

import com.ailk.biz.util.StaticUtil;
import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.org.apache.commons.lang3.StringUtils;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.base.CSBizService;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserOtherInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user.UserPostInfoQry;

public class ModifyPostInfoSVC extends CSBizService
{
    /**
     * 获取邮寄信息
     * 
     * @param allInfo
     * @return
     * @throws Exception
     */
    public IData getPostInfo(IData allInfo) throws Exception
    {
        IData params = new DataMap();
        IData postInfo = new DataMap();
        params.put("ID", allInfo.getData("USER_INFO").getString("USER_ID"));
        params.put("ID_TYPE", "1");
        params.put("SERIAL_NUMBER", allInfo.getData("USER_INFO").getString("SERIAL_NUMBER"));
        IDataset dataset = UserPostInfoQry.qryUserPostInfo(allInfo.getData("USER_INFO").getString("USER_ID"), "1");
        StringBuilder postType = new StringBuilder();

        String postContent = "";
        String emailContent = "";
        String mmsContent = "";

        if (IDataUtil.isNotEmpty(dataset))
        {
            postInfo.put("IS_POST", "1");
            postInfo.put("POST_CYC", dataset.getData(0).getString("POST_CYC"));

            for (int i = 0, size = dataset.size(); i < size; i++)
            {
                IData data = dataset.getData(i);
                String postTypeset = data.getString("POST_TYPESET").trim();
                postType.append(postTypeset.trim());// 邮寄方式拼串
                if (StringUtils.equals("0", postTypeset))// 邮政投递的方式
                {

                    // 电子渠道接口使用此业务bean，所以需要针对不同的邮寄方式 取相应的内容
                    postContent = data.getString("POST_CONTENT").trim();
                    postInfo.put("POST_ADDRESS", data.getString("POST_ADDRESS"));
                    postInfo.put("POST_CODE", data.getString("POST_CODE"));
                    postInfo.put("POST_NAME", data.getString("POST_NAME"));
                    postInfo.put("POST_TAG", data.getString("POST_TAG"));
                    postInfo.put("FAX_NBR", data.getString("FAX_NBR"));
                    if (size == 1)// 如果数据库中值只存在一条生效的邮寄信息，且是 邮政投递的方式；
                    {
                        postInfo.put("EMAIL", data.getString("EMAIL"));
                    }

                }
                else if (StringUtils.equals("2", postTypeset)) // emali
                // 方式
                {
                    emailContent = data.getString("POST_CONTENT").trim();
                    postInfo.put("EMAIL", data.getString("EMAIL"));

                    if (size == 1)
                    {
                        postInfo.put("POST_ADDRESS", data.getString("POST_ADDRESS"));
                        postInfo.put("POST_CODE", data.getString("POST_CODE"));
                        postInfo.put("POST_NAME", data.getString("POST_NAME"));
                        postInfo.put("POST_TAG", data.getString("POST_TAG"));
                        postInfo.put("FAX_NBR", data.getString("FAX_NBR"));
                    }
                }
                else if (StringUtils.equals("3", postTypeset)) // mms
                // 方式
                {
                    mmsContent = data.getString("POST_CONTENT").trim();

                    if (size >= 1)
                    {
                        postInfo.put("EMAIL", data.getString("EMAIL"));
                        postInfo.put("POST_ADDRESS", data.getString("POST_ADDRESS"));
                        postInfo.put("POST_CODE", data.getString("POST_CODE"));
                        postInfo.put("POST_NAME", data.getString("POST_NAME"));
                        postInfo.put("POST_TAG", data.getString("POST_TAG"));
                        postInfo.put("FAX_NBR", data.getString("FAX_NBR"));
                    }
                }
                else
                {
                    // 如果存在 不是 邮政投递 和EAMIL移动E信 的投递方式,则需要判断如下的值 是否已经存在（）；
                    if (StringUtils.isBlank(postInfo.getString("POST_ADDRESS", "")))
                    {
                        postInfo.put("POST_ADDRESS", data.getString("POST_ADDRESS"));
                    }
                    if (StringUtils.isBlank(postInfo.getString("POST_CODE", "")))
                    {
                        postInfo.put("POST_CODE", data.getString("POST_CODE"));
                    }
                    if (StringUtils.isBlank(postInfo.getString("POST_NAME", "")))
                    {
                        postInfo.put("POST_NAME", data.getString("POST_NAME"));
                    }
                    if (StringUtils.isBlank(postInfo.getString("POST_TAG", "")))
                    {
                        postInfo.put("POST_TAG", data.getString("POST_TAG"));
                    }
                    if (StringUtils.isBlank(postInfo.getString("FAX_NBR", "")))
                    {
                        postInfo.put("FAX_NBR", data.getString("FAX_NBR"));
                    }
                    if (StringUtils.isBlank(postInfo.getString("EMAIL", "")))
                    {
                        postInfo.put("EMAIL", data.getString("EMAIL"));
                    }
                }
            }
        }
        else
        {
            postInfo.put("IS_POST", "0");
        }

        postInfo.put("POST_TYPESET", postType);// 邮寄方式
        postInfo.put("POSTTYPE_CONTENT", postContent);// 邮政投递内容
        postInfo.put("EMAILTYPE_CONTENT", emailContent);// email投递内容
        postInfo.put("MMSTYPE_CONTENT", mmsContent);// mms投递内容

        return postInfo;

    }

    /**
     * 获取用户历史补寄信息
     * 
     * @param pd
     * @return IDataset
     * @throws Exception
     */
    public IDataset getPostRepairInfo(IData params) throws Exception
    {
        IDataset datalist = UserOtherInfoQry.getOtherServByUserIdServMode(params.getString("USER_ID"), "PR");
        return getRepairContent(datalist);
    }

    /**
     * 根据邮政投递内容和Email投递内容的编码，将其用名称代替
     */
    public IDataset getRepairContent(IDataset datalist) throws Exception
    {
        String postContent = "";

        if (datalist != null && datalist.size() > 0)
        {
            for (int i = 0; i < datalist.size(); i++)
            {
                IData data = datalist.getData(i);
                postContent = processPostContent(data.getString("RSRV_STR2"));
                data.put("RSRV_STR2", postContent);
                postContent = processPostContent(data.getString("RSRV_STR3"));
                data.put("RSRV_STR3", postContent);
            }
        }

        return datalist;
    }

    /**
     * 编码转变成名称
     */
    private String processPostContent(String postContent) throws Exception
    {
        StringBuilder sb = new StringBuilder("");
        if (postContent != null && postContent.length() >= 1)
        {
            String str1 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(0, 1));
            sb.append(str1 != null && sb.length() == 0 ? str1 : "");
        }
        if (postContent != null && postContent.length() >= 2)
        {
            String str2 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(1, 2));
            sb.append(str2 != null && sb.length() != 0 ? "," + str2 : "");
        }
        if (postContent != null && postContent.length() >= 3)
        {
            String str3 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(2, 3));
            sb.append(str3 != null && sb.length() != 0 ? "," + str3 : "");
        }
        if (postContent != null && postContent.length() >= 4)
        {
            String str4 = StaticUtil.getStaticValue("POSTINFO_POSTCONTENT", postContent.substring(3));
            sb.append(str4 != null && sb.length() != 0 ? "," + str4 : "");
        }
        return sb.toString();
    }
}
