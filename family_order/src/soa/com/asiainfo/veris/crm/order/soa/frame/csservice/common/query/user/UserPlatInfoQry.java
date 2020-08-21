
package com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.user;

import com.ailk.common.data.IData;
import com.ailk.common.data.IDataset;
import com.ailk.common.data.impl.DataMap;
import com.ailk.common.data.impl.DatasetList;
import com.ailk.database.util.SQLParser;
import com.asiainfo.veris.crm.order.pub.frame.bcf.util.IDataUtil;
import com.asiainfo.veris.crm.order.pub.util.ArrayUtil;
import com.asiainfo.veris.crm.order.soa.frame.bcf.dao.Dao;
import com.asiainfo.veris.crm.order.soa.frame.bcf.query.UcaInfoQry;
import com.asiainfo.veris.crm.order.soa.frame.bof.data.consts.BofConst;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.callout.UpcCall;
import com.asiainfo.veris.crm.order.soa.frame.csservice.common.query.product.PlatSvcInfoQry;

// 统一封装所有的平台业务数据库操作，不允许在其他BEAN中直接CODE_CODE
public class UserPlatInfoQry
{
    private static IData mailPUSHContent = null;

    private static IData mailSmsContent = null;

    public static boolean bindWlanFreeFeeExpDiscnt(IData data) throws Exception
    {
        return true;

    }

    /**
     * 错误码转义
     * 
     * @param inparams
     * @return
     */
    public static String errorCodeParser(IData inparams)
    {

        String errorCode = inparams.getString("X_RESULTCODE");
        String errorName = "";
        if (errorCode.equals("00"))
        {
            errorName = "成功！";
        }
        else if (errorCode.equals("01"))
        {
            errorName = "企业代码错误！";
        }
        else if (errorCode.equals("02"))
        {
            errorName = "操作代码错误！";
        }
        else if (errorCode.equals("03"))
        {
            errorName = "业务代码错误！";
        }
        else if (errorCode.equals("04"))
        {
            errorName = "生效时间错误！";
        }
        else if (errorCode.equals("10"))
        {
            errorName = "未订购基本业务！";
        }
        else if (errorCode.equals("11"))
        {
            errorName = "用户已订购该业务！";
        }
        else if (errorCode.equals("12"))
        {
            errorName = "用户未订购该业务或已退订！";
        }
        else if (errorCode.equals("13"))
        {
            errorName = "用户已主动恢复！";
        }
        else if (errorCode.equals("14"))
        {
            errorName = "用户已主动暂停！";
        }
        else if (errorCode.equals("20"))
        {
            errorName = "用户已单向停机！";
        }
        else if (errorCode.equals("21"))
        {
            errorName = "用户已停机！";
        }
        else if (errorCode.equals("22"))
        {
            errorName = "用户预销户！";
        }
        else if (errorCode.equals("23"))
        {
            errorName = "用户销户！";
        }
        else if (errorCode.equals("24"))
        {
            errorName = "用户不存在！";
        }
        else if (errorCode.equals("25"))
        {
            errorName = "内置号码业务受理状态不允许用户进行绑定操作";
        }
        else if (errorCode.equals("98"))
        {
            errorName = "落地方内部错误！";
        }
        else if (errorCode.equals("99"))
        {
            errorName = "其它错误！";
        }
        else if (errorCode.equals("0015"))
        {
            errorName = "用户尚未办理基础服务！";
        }
        else if (errorCode.equals("0016"))
        {
            errorName = "该用户服务开关已关闭，或存在限制服务！";
        }
        else if (errorCode.equals("0017"))
        {
            errorName = "重复操作！";
        }
        else if (errorCode.equals("0018"))
        {
            errorName = "用户品牌不正确！";
        }
        else if (errorCode.equals("0019"))
        {
            errorName = "SP企业信息不存在或SP业务状态不正确！";
        }
        else if (errorCode.equals("0021"))
        {
            errorName = "该用户是黑名单用户！";
        }
        else if (errorCode.equals("0022"))
        {
            errorName = "调用lcu发生错误！";
        }
        else if (errorCode.equals("0023"))
        {
            errorName = "该用户尚未办理此服务，不能操作!";
        }
        else if (errorCode.equals("0024"))
        {
            errorName = "该用户已经办理此服务，不能操作!";
        }
        else if (errorCode.equals("0025"))
        {
            errorName = "该服务已经暂停，请选择其他操作!";
        }
        else if (errorCode.equals("0026"))
        {
            errorName = "用户信息不存在,或者该用户为异地用户！";
        }
        else if (errorCode.equals("0027"))
        {
            errorName = "业务信息不存在！";
        }
        else if (errorCode.equals("0028"))
        {
            errorName = "该用户无任何有效订购关系！";
        }
        else if (errorCode.equals("0029"))
        {
            errorName = "该用户无任何历史订购关系！";
        }
        else if (errorCode.equals("0030"))
        {
            errorName = "该用户无订购关系！";
        }
        else if (errorCode.equals("0031"))
        {
            errorName = "该用户开关已经打开!";
        }
        else if (errorCode.equals("0032"))
        {
            errorName = "该用户开关已经关闭!";
        }
        else if (errorCode.equals("0033"))
        {
            errorName = "计费号码不存在或非法!";
        }
        else if (errorCode.equals("0034"))
        {
            errorName = "服务开关未打开，不能进行操作!";
        }
        else if (errorCode.equals("0035"))
        {
            errorName = "该用户未注册，不能进行预约!";
        }
        else if (errorCode.equals("0036"))
        {
            errorName = "该用户无预约业务!";
        }
        else if (errorCode.equals("0037"))
        {
            errorName = "该操作只允许单条!";
        }
        else if (errorCode.equals("0038"))
        {
            errorName = "该用户有未完工的工单，请稍后操作!";
        }
        else if (errorCode.equals("0039"))
        {
            errorName = "该用户存在增值邮箱服务，请先退订增值邮箱！";
        }
        else if (errorCode.equals("0040"))
        {
            errorName = "内置卡已经挂失,只能进行解挂操作";
        }
        else if (errorCode.equals("0041"))
        {
            errorName = "内置卡未挂失,不能进行解挂操作";
        }
        else if (errorCode.equals("0042"))
        {
            errorName = "该SP厂商处于处罚状态，暂停办理业务！";
        }
        else if (errorCode.equals("0043"))
        {
            errorName = "查询不到内置卡信息,操作失败";
        }
        else if (errorCode.equals("0044"))
        {
            errorName = "该用户已订购一种基础邮箱，请先退订再进行其他基础邮箱订购！";
        }
        else if (errorCode.equals("0045"))
        {
            errorName = "用户已经是该级别会员！";
        }
        else if (errorCode.equals("0046"))
        {
            errorName = "无线音乐业务办理，会员级别不能为空！";
        }
        else if (errorCode.equals("0047"))
        {
            errorName = "密码修改失败，请查询！";
        }
        else if (errorCode.equals("0048"))
        {
            errorName = "套餐变更失败，请查询！";
        }
        else if (errorCode.equals("0049"))
        {
            errorName = "套餐订购失败，请查询！";
        }
        else if (errorCode.equals("0050"))
        {
            errorName = "套餐订购/变更失败，请查询！";
        }
        else if (errorCode.equals("0051"))
        {
            errorName = "套餐退订失败，请查询！";
        }
        else if (errorCode.equals("0052"))
        {
            errorName = "业务查询失败，未订购任何WLAN套餐，请查询！";
        }
        else if (errorCode.equals("0053"))
        {
            errorName = "未注册WLAN业务，请查询！";
        }
        else if (errorCode.equals("0054"))
        {
            errorName = "已经申请套餐，请变更套餐!";
        }
        else if (errorCode.equals("0055"))
        {
            errorName = "尚未申请套餐，不能操作!";
        }
        else if (errorCode.equals("0056"))
        {
            errorName = "缺少参数或参数错误，请检查!";
        }
        else if (errorCode.equals("0057"))
        {
            errorName = "该用户已订购了该套餐，无需再次开通!";
        }
        else if (errorCode.equals("0058"))
        {
            errorName = "该用户已经暂停了WLAN业务，无需重复操作!";
        }
        else if (errorCode.equals("0059"))
        {
            errorName = "该用户的WLAN业务已处于正常状态，无需恢复操作!";
        }
        else if (errorCode.equals("0060"))
        {
            errorName = "该用户有需短信二次确认的工单！";
        }
        else if (errorCode.equals("0061"))
        {
            errorName = "该用户原有手机电视服务是全网服务，不能重复订购全网服务!";
        }
        else if (errorCode.equals("0062"))
        {
            errorName = "该用户原有手机电视服务和当前新订购的服务不在同一个月，不能订购!";
            // }else if(errorCode.equals("0063")){
            // errorName = "对不起，您的话费余额不足，不能订购手机电视";
        }
        else if (errorCode.equals("0064"))
        {
            errorName = "对不起，手机导航非GPS包月已经停止在BOSS侧订购，请订购手机导航[企业编码:300008,业务编码:20100100]";
        }
        else if (errorCode.equals("0065"))
        {
            errorName = "用户已经订购全球通音乐包，如须成功单独退订该服务，须先退订该产品包下的其他服务";
        }
        else if (errorCode.equals("0066"))
        {
            errorName = "实时结余不足，无法订购该套餐";
        }
        else if (errorCode.equals("0067"))
        {
            errorName = "用户已参与wlan免费体验活动，不得重复参与";
        }
        else if (errorCode.equals("0068"))
        {
            errorName = "此业务需开通GPRS功能后才能办理!";
        }
        return errorName;
    }

    public static String get139MailPushContent(String str)
    {

        if (mailPUSHContent == null)
        {
            mailPUSHContent = new DataMap();
            mailPUSHContent.put("+MAILBZ_06", "尊敬的中国移动客户，你已成功开通5元版139邮箱。5元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILBZ_06_+MAILMF", "5元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILBZ_06_+MAILBZ", "5元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILBZ_06_+MAILVIP", "5元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");

            mailPUSHContent.put("+MAILVIP_06", "尊敬的中国移动客户，您已成功开通20元版139邮箱。20元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILVIP_06_+MAILMF", "20元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILVIP_06_+MAILBZ", "20元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
            mailPUSHContent.put("+MAILVIP_06_+MAILVIP", "20元版139邮箱赠送PushEmail功能，随时随地手机收发邮件，推荐下载，安装成功后点击“使用139邮箱PushEmail”按钮便可正常使用。http://218.200.249.217:8080/MigPortal/wap/wap_index.jsp");
        }
        return mailPUSHContent.getString(str);
    }

    public static String get139MailSmsContent(String str)
    {

        if (mailSmsContent == null)
        {
            mailSmsContent = new DataMap();
            mailSmsContent.put("+MAILMF_06", "");
            mailSmsContent.put("+MAILMF_06_+MAILMF", "尊敬的中国移动客户，您已是免费版139邮箱用户,功能费0元/月,详询10086。");
            mailSmsContent.put("+MAILMF_06_+MAILBZ", "尊敬的中国移动客户，5元版变更为免费版，需求已受理，账号、密码不变，功能费0元/月，变更下月生效,详询10086。");
            mailSmsContent.put("+MAILMF_06_+MAILVIP", "尊敬的中国移动客户，20元版变更为免费版，需求已受理，账号、密码不变，功能费0元/月，变更下月生效,详询10086。");
            mailSmsContent.put("+MAILMF_06_ERROR", "尊敬的中国移动客户，您申请免费版139邮箱暂未成功开通，请再次发送短信KTYX到10086申请,详询10086。");

            mailSmsContent.put("+MAILBZ_06_+MAILBZ", "尊敬的中国移动客户，您已是5元版139邮箱用户，功能费5元/月，邮箱网址mail.139.com,详询10086。");
            mailSmsContent.put("+MAILBZ_06_+MAILVIP", "尊敬的中国移动客户，20元版变更到5元版，需求已受理：账号、密码不变，功能费5元/月，变更下月生效,详询10086。");
            mailSmsContent.put("+MAILBZ_06_ERROR", "尊敬的中国移动客户，您申请5元版139邮箱暂未成功开通，请再次编辑短信KTYX5发送到10086申请，详询10086。");

            mailSmsContent.put("+MAILVIP_06_+MAILVIP", "尊敬的中国移动客户，您已是20元版139邮箱用户，功能费20元/月,邮箱网址mail.139.com,详询10086。");
            mailSmsContent.put("+MAILVIP_06_ERROR", "尊敬的中国移动客户，您申请20元版139邮箱暂未成功开通，请再次发送短信KTYX20到10086,详询10086。");
            mailSmsContent.put("+MAILMF_07", "尊敬的中国移动客户，您尚未开通139邮箱，编辑短信KTYX发送到10086即可注册免费版139邮箱，邮箱网址mail.139.com,详询10086。");
            mailSmsContent.put("+MAILMF_07_+MAILBZ", "尊敬的中国移动客户，您是5元版139邮箱用户，如需取消5元版139邮箱请发送短信QXYX5到10086，详询10086。");
            mailSmsContent.put("+MAILMF_07_+MAILVIP", "尊敬的中国移动客户，您是20元版139邮箱用户，如需取消20元版139邮箱请发送短信QXYX20到10086，详询10086。");
            mailSmsContent.put("+MAILMF_07_ERROR", "尊敬的中国移动客户，您的免费版139邮箱未能成功注销，请稍后再试。");
            mailSmsContent.put("+MAILBZ_07", "尊敬的中国移动客户，您尚未开通5元版139收费邮箱，编辑短信KTYX5发送到10086即可注册5元版139收费邮箱，详询10086。");
            mailSmsContent.put("+MAILBZ_07_+MAILMF", "尊敬的中国移动客户，您是免费版139邮箱用户，如需取消免费版139邮箱请发送短信QXYX到10086，详询10086。");
            mailSmsContent.put("+MAILBZ_07_+MAILVIP", "尊敬的中国移动客户，您是20元版139邮箱用户，如需取消20元版139邮箱请发送短信QXYX20到10086，详询10086。");
            mailSmsContent.put("+MAILBZ_07_ERROR", "尊敬的中国移动客户，您的5元版139邮箱未能成功注销，请再次编辑短信QXYX5发送到10086申请，详询10086。");

            mailSmsContent.put("+MAILVIP_07", "尊敬的中国移动客户，您尚未开通20元版139收费邮箱，编辑短信KTYX20发送到10086即可注册20元版139收费邮箱,详询10086。");
            mailSmsContent.put("+MAILVIP_07_+MAILMF", "尊敬的中国移动客户，您是免费版139邮箱用户，如需取消免费版139邮箱请发送短信QXYX到10086，详询10086。");
            mailSmsContent.put("+MAILVIP_07_+MAILBZ", "尊敬的中国移动客户，您是5元版139邮箱用户，如需取消5元版139邮箱请发送短信QXYX5到10086，详询10086。");
            mailSmsContent.put("+MAILVIP_07_ERROR", "尊敬的中国移动客户，您的20元版139邮箱未能成功注销，请再次编辑短信QXYX20发送到10086再次申请，详询10086。");
        }
        return mailSmsContent.getString(str);
    }

    /**
     * 根据sp_code和bizcode查询
     * 
     * @param param
     * @return
     * @throws Exception
     */
    public static IDataset getMediaSmsContent(IData param) throws Exception
    {

        return Dao.qryByCode("TD_B_PLATSVC_SMS", "SEL_FLOWMEDIA_SMS", param);
    }

    public static IDataset getOperType(IData service, String bizProcessTag) throws Exception
    {

        IDataset opers = new DatasetList();
        if (bizProcessTag == null || bizProcessTag.length() == 0)
        {
            IData oper = new DataMap();
            oper.put("OPER_CODE", "-1");
            oper.put("OPER_TYPE", "未配置操作");// --第一位
            opers.add(oper);
            return opers;
        }
        for (int i = 0; i < bizProcessTag.length(); i++)
        {
            String j = bizProcessTag.charAt(i) + "";

            if (j.equals("0"))
            {
                continue;
            }
            IData oper = new DataMap();
            oper.putAll(service);
            oper.put("NODES", "0");

            switch (i)
            {

                case 0:
                    oper.put("OPER_CODE", "01");
                    oper.put("OPER_TYPE", "用户注册");// --第一位
                    break;
                case 1:
                    oper.put("OPER_CODE", "02");
                    oper.put("OPER_TYPE", "用户注销");// --第二位
                    break;
                case 2:
                    oper.put("OPER_CODE", "03");
                    oper.put("OPER_TYPE", "密码修改");// --第三位
                    break;
                case 3:
                    oper.put("OPER_CODE", "04");
                    oper.put("OPER_TYPE", "业务暂停");// --第四位
                    break;
                case 4:
                    oper.put("OPER_CODE", "05");
                    oper.put("OPER_TYPE", "业务恢复");// --第五位
                    break;
                case 5:
                    oper.put("OPER_CODE", "06");
                    oper.put("OPER_TYPE", "服务定购");// --第六位
                    break;
                case 6:
                    oper.put("OPER_CODE", "07");
                    oper.put("OPER_TYPE", "服务订购取消");// 第七位
                    break;
                case 7:
                    oper.put("OPER_CODE", "08");
                    oper.put("OPER_TYPE", "用户资料变更");// 第八位
                    break;
                case 8:
                    oper.put("OPER_CODE", "11");
                    oper.put("OPER_TYPE", "赠送");// 第九位
                    break;
                case 9:
                    oper.put("OPER_CODE", "14");
                    oper.put("OPER_TYPE", "用户主动暂停");// 第十位

                    break;
                case 10:
                    oper.put("OPER_CODE", "15");
                    oper.put("OPER_TYPE", "用户主动恢复");// 十一位
                    break;
                case 11:
                    oper.put("OPER_CODE", "18");
                    oper.put("OPER_TYPE", "服务开关开");// 第十二位
                    break;
                case 12:
                    oper.put("OPER_CODE", "19");
                    oper.put("OPER_TYPE", "服务开关关");// 十三位
                    break;
                case 13:
                    oper.put("OPER_CODE", "89");
                    oper.put("OPER_TYPE", "SP全退订");// 第十四位 --直接写历史表，完工处理？

                    break;
                case 14:
                    oper.put("OPER_CODE", "97");
                    oper.put("OPER_TYPE", "全业务恢复");// --第十伍位 --直接写历史表，完工处理？

                    break;
                case 15:
                    oper.put("OPER_CODE", "98");
                    oper.put("OPER_TYPE", "全业务暂停");// --第十六位
                    break;
                case 16:
                    oper.put("OPER_CODE", "99");
                    oper.put("OPER_TYPE", "全业务退订");// --第十七位
                    break;
                case 17:
                    oper.put("OPER_CODE", "22");
                    oper.put("OPER_TYPE", "点播");// --第十八位
                    break;
                case 18:
                    oper.put("OPER_CODE", "16");
                    oper.put("OPER_TYPE", "充值"); // --第十九位
                    break;
                case 19:
                    oper.put("OPER_CODE", "12");
                    oper.put("OPER_TYPE", "预约"); // --第二十位
                    break;
                case 20:
                    oper.put("OPER_CODE", "13");
                    oper.put("OPER_TYPE", "预约取消"); // --第二十一位
                    break;
                case 21:
                    oper.put("OPER_CODE", "33");
                    oper.put("OPER_TYPE", "挂失"); // --第二十二位
                    break;
                case 22:
                    oper.put("OPER_CODE", "34");
                    oper.put("OPER_TYPE", "解挂"); // --第二十三位
                    break;
                // case 23:
                // // oper.put("OPER_CODE", "10");
                // // oper.put("OPER_TYPE", "139激活(前台不能操作)"); //--第二十三位
                // break;
                case 24:
                    oper.put("OPER_CODE", "10");
                    oper.put("OPER_TYPE", "WLAN套餐申请"); // --第二十五位
                    break;
                case 25:
                    oper.put("OPER_CODE", "12");
                    oper.put("OPER_TYPE", "WLAN套餐退订"); // --第二十六位
                    break;
                case 26: // 套餐变更 add by zhouwei WLAN套餐变更
                    oper.put("OPER_CODE", "23");
                    oper.put("OPER_TYPE", "WLAN套餐变更"); // --第二十七位
                    break;
                case 29:
                    oper.put("OPER_CODE", "88");
                    oper.put("OPER_TYPE", "套餐变更"); // --第三十位
                    break;
                default:
                    oper.put("OPER_CODE", "-1");
                    oper.put("OPER_TYPE", "未知操作(" + i + ":" + j + ")"); // --第三十位
            }

            opers.add(oper);
        }
        return opers;
        /*
         * 01-用户注册--第一位 注册类 02-用户注销--第二位 注册类 03-密码修改--第三位 属性变更 04-业务暂停--第四位 状态变更 05-业务恢复--第五位 状态变更 06-服务定购--第六位 注册类
         * 07-服务订购取消--第七位 注册类 08-用户资料变更--第八位 属性变更 11-赠送--第九位 注册类 14-用户主动暂停--第十位 状态变更 15-用户主动恢复--第十一位 状态变更 18-服务开关开
         * --第十二位 状态变更 19-服务开关关--第十三位 状态变更 89-SP全业务退订--第十四位 注册类 97-全业务恢复 第十五位 状态变更 98-全业务暂停 第十六位 状态变更 99-全业务退定 第十七位 注册类
         * 22-点播 第十八位 注册类 16-充值 第十九位 12-预约 第二十位 13-取消预约 第二十一位 33-挂失 第二十二位 34-解挂 第二十三位 10-解挂 第二十四位 88-套餐变更 第三十位
         */
    }

    public static IDataset getOperTypeBySp_Biz(String sp_code, String biz_code, String biz_type_code) throws Exception
    {

        String bizProcessTag = "";
        if (sp_code == null)
            sp_code = "";
        if (biz_code == null)
            biz_code = "";
        String sql = "SELECT BIZ_PROCESS_TAG" + " FROM TD_B_PLATSVC A" + " WHERE A.BIZ_STATE_CODE = 'A' AND A.SP_CODE = '" + sp_code + "' AND A.BIZ_CODE='" + biz_code + "' AND SYSDATE BETWEEN START_DATE AND END_DATE" + " AND A.BIZ_TYPE_CODE = '"
                + biz_type_code + "'";
        IDataset result = new DatasetList();// Dao.queryListParse(sql);
        if (result != null && result.size() > 0)
        {
            bizProcessTag = ((IData) (result.get(0))).getString("BIZ_PROCESS_TAG");
        }
        return getOperType(new DataMap(), bizProcessTag);
    }

    // 获取平台产品包编码名称
    public static IData getPlatPackage(IData inparams)
    {

        IData data = new DataMap();
        data.put("PACKAGE_ID", "50000000");
        data.put("PACKAGE_NAME", "平台产品包");
        return data;
    }

    // 获取平台产品编码名称
    public static IData getPlatProduct(IData inparams)
    {

        IData data = new DataMap();
        data.put("PRODUCT_ID", "50000000");
        data.put("PRODUCT_NAME", "平台产品");
        return data;
    }

    /*
     * 根据手机号码查询用户信息（在线用户）（最后一个销户用户）
     */
    public static IDataset getUserInfoBySn(IData inparam) throws Exception
    {

        IDataset userInfo = new DatasetList();
        int userInfoTag = 0;
        userInfo.clear();

        inparam.put("REMOVE_TAG", "0");
        inparam.put("NET_TYPE_CODE", "00");

        String serial_number = inparam.getString("SERIAL_NUMBER", "");
        IDataset normalUserInfo = IDataUtil.idToIds(UcaInfoQry.qryUserMainProdInfoBySn(serial_number));

        if (normalUserInfo.size() == 1)
        {
            userInfo.addAll(normalUserInfo);
        }
        else
        {
            userInfoTag = 1;
        }
        // 获取正常用户无资料时，再获取最后销户用户
        if (userInfoTag == 1)
        {
            IDataset lastDestroyUserInfo = UserInfoQry.getDestroyUserInfoBySn(serial_number);

            if (lastDestroyUserInfo != null && lastDestroyUserInfo.size() > 0)
            {
                userInfo.add(lastDestroyUserInfo.getData(0));
            }
        }

        return userInfo;
    }

    public static IDataset getUserPlatAttrInfos(String serialNumber, String bizTypeCode, String serviceId, String infoCode) throws Exception
    {
        IData params = new DataMap();
        //params.put("BIZ_TYPE_CODE", bizTypeCode);
        params.put("SERIAL_NUMBER", serialNumber);
        params.put("SERVICE_ID", serviceId);
        params.put("INFO_CODE", infoCode);
        IDataset userPlatSvcs = Dao.qryByCode("TF_F_USER_PLATSVC_ATTR", "SEL_ALL_SVCATTR", params);
        
        if(ArrayUtil.isNotEmpty(userPlatSvcs))
        {
        	for(int i = 0 ; i <userPlatSvcs.size() ; i ++)
        	{
        		IData userPlatSvc = userPlatSvcs.getData(i);
        		IDataset upcDatas = new DatasetList();
        		try{
        			upcDatas = UpcCall.querySpServiceAndProdByCond(null, null, bizTypeCode, userPlatSvc.getString("SERVICE_ID"));
        		}catch(Exception e){
        			
        		}
        		if(ArrayUtil.isNotEmpty(upcDatas))
        		{
        			for(int k = 0 ; k < upcDatas.size() ; k++)
        			{
        				IData upcData = upcDatas.getData(k);
        				userPlatSvc.put("BIZ_TYPE_CODE", upcData.getString("BIZ_TYPE_CODE"));
        			}
        		}else{
        			userPlatSvcs.remove(i);
        			i--;
        		}
        		userPlatSvc.remove("SERVICE_ID");
        	}
        }
        return userPlatSvcs;
    }

    /**
     * 根据开始时间，结束时间查询
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatByStartEndDate(String userId, String startDate, String endDate) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);
        params.put("START_DATE", startDate);
        params.put("END_DATE", endDate);

        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_STARTDATE_ENDDATE", params);
    }

    public static IDataset getUserPlatByUserIdCodes(String userId, String bizCode, String bizStateCode) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        param.put("BIZ_CODE", bizCode);
        param.put("BIZ_STATE_CODE", bizStateCode);
        SQLParser parser = new SQLParser(param);
        parser.addSQL(" SELECT T.* FROM TF_F_USER_PLATSVC T  ");
        parser.addSQL(" WHERE T.PARTITION_ID=MOD(:USER_ID,10000) ");
        parser.addSQL(" AND T.USER_ID = :USER_ID ");
        parser.addSQL(" AND T.BIZ_STATE_CODE = :BIZ_STATE_CODE ");
        parser.addSQL(" AND T.END_DATE > SYSDATE ");
        IDataset dataset= Dao.qryByParse(parser);
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		IData temp  = dataset.getData(i);
        		String serviceId = temp.getString("SERVICE_ID");
        		IDataset upcList = UpcCall.querySpServiceAndProdByCond("", bizCode, "", serviceId);
				if(IDataUtil.isEmpty(upcList)){
					dataset.remove(i);
					i--;
				}else{
					temp.put("BIZ_CODE", upcList.getData(0).getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", upcList.getData(0).getString("BIZ_TYPE_CODE"));
				}
			}
        }
        return dataset;
    }

    public static IDataset getUserPlatElecInfoByUserId(String userId) throws Exception
    {
        IData param = new DataMap();
        param.put("USER_ID", userId);
        IDataset dataset = Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USER_ID_NEW_1", param);
        IDataset upcList = new DatasetList();
        IData temp = new DataMap();
        String serviceId = "";
        if(IDataUtil.isNotEmpty(dataset)){
        	for (int i = 0; i < dataset.size(); i++) {
        		temp = dataset.getData(i);
				serviceId = temp.getString("SERVICE_ID");
				upcList = UpcCall.querySpServiceAndProdByCond("", "", "04", serviceId);
				if(IDataUtil.isEmpty(upcList)){
					dataset.remove(i);
					i--;
				}else{
					temp.put("BIZ_CODE", upcList.getData(0).getString("BIZ_CODE"));
					temp.put("BIZ_TYPE_CODE", upcList.getData(0).getString("BIZ_TYPE_CODE"));
				}
			}
        }
        return dataset;
    }

    /**
     * 通过用户标识ID查询查询平台业务信息
     * 
     * @param userId
     * @param startDate
     * @param endDate
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatInfoByUserId(String userId) throws Exception
    {
        IData params = new DataMap();
        params.put("USER_ID", userId);

        return Dao.qryByCode("TF_F_USER_PLATSVC", "SEL_BY_USERID_NEW", params);
    }

    public static IDataset getWlanGiftUser(String serialNumber) throws Exception
    {
        IData param = new DataMap();
        param.put("SERIAL_NUMBER", serialNumber);
        return Dao.qryByCode("TF_F_USER_WLANGIFT_TARGET", "SEL_BY_SN", param);
    }

    public static String getWlanSmsContent(String str, IData inParam)
    {

        IData wlanSmsContent = null;
        wlanSmsContent = new DataMap();
        if (!"".equals(inParam.getString("SERIAL_NUMBER")) && inParam.getString("SERIAL_NUMBER").length() == 11)
        {
            wlanSmsContent.put("WLAN_01_", "欢迎使用中国移动WLAN业务，帐号：" + inParam.getString("SERIAL_NUMBER") + "，密码：" + inParam.getString("WLAN_PASSWORD") + "，如需帮助请拨打10086。");
        }
        wlanSmsContent.put("WLAN_01_WLAN", "在此之前您已申请开通了WLAN业务，如忘记密码请发送“CZWLANMM”到10086或者拨打10086咨询。");
        wlanSmsContent.put("WLAN_01_ERROR", "对不起，开户不成功，请稍后再试或拨打10086咨询。");
        wlanSmsContent.put("WLAN_01_ILLEGALUSER", "对不起，申请不成功，请稍后重试或拨打10086咨询。");

        if (!"".equals(inParam.getString("INFO_VALUE")) && inParam.getString("INFO_VALUE").length() == 6)
        {
            wlanSmsContent.put("WLAN_03_WLAN", "您已成功修改WLAN密码，新密码为：" + inParam.getString("INFO_VALUE") + "，请注意保管。");
        }
        wlanSmsContent.put("WLAN_03_ERROR", "对不起，修改密码不成功，请核实原密码是否正确，可稍后再试或拨打10086咨询");

        if (!"".equals(inParam.getString("INFO_VALUE")) && inParam.getString("INFO_VALUE").length() == 6)
        {
            wlanSmsContent.put("WLAN_09_WLAN", "您已重置WLAN密码，新密码为：" + inParam.getString("INFO_VALUE") + "，请注意保管。");
        }
        wlanSmsContent.put("WLAN_09_ERROR", "对不起，密码重置不成功，请稍后再试或拨打10086咨询。");

        if (!"".equals(inParam.getString("SMS_TEXT")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN套餐，" + inParam.getString("SMS_TEXT") + "，套餐已生效。");
        }
        if ("20001".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包单位时间资费包小时，套餐已生效。");
        }
        else if ("20002".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包单位时间资费包天，套餐已生效。");
        }
        else if ("20003".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包单位时间资费包周，套餐已生效。");
        }
        else if ("20004".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包单位时间资费包月，套餐已生效。");
        }
        else if ("30001".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包时长资费30元/15小时，套餐已生效。");
        }
        else if ("30002".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包时长资费50元/40小时，套餐已生效。");
        }
        else if ("30003".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_10_WLAN", "您已开通WLAN包时长资费100元/200小时，套餐已生效。");
        }
        wlanSmsContent.put("WLAN_10_ERROR", "对不起，套餐办理不成功，请稍后再试或拨打10086咨询。");

        if (!"".equals(inParam.getString("SMS_TEXT")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，" + inParam.getString("SMS_TEXT") + "新套餐下月生效。");
            wlanSmsContent.put("WLAN_232_WLAN", "您已变更WLAN高校优惠资费套餐，" + inParam.getString("SMS_TEXT") + "，新套餐下月生效。");
        }
        if ("20001".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包单位时间资费包小时，新套餐下月生效。");
        }
        else if ("20002".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包单位时间资费包天，新套餐下月生效。");
        }
        else if ("20003".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包单位时间资费包周，新套餐下月生效。");
        }
        else if ("20004".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包单位时间资费包月，新套餐下月生效。");
        }
        else if ("30001".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包时长资费30元/15小时，新套餐下月生效。");
        }
        else if ("30002".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包时长资费50元/40小时，新套餐下月生效。");
        }
        else if ("30003".equals(inParam.getString("INFO_VALUE")))
        {
            wlanSmsContent.put("WLAN_231_WLAN", "您已变更WLAN套餐，WLAN包时长资费100元/200小时，新套餐下月生效。");
        }

        wlanSmsContent.put("WLAN_231_ERROR", "对不起，套餐变更不成功，请稍后再试或拨打10086咨询。");
        wlanSmsContent.put("WLAN_232_ERROR", "对不起，套餐变更不成功，请稍后再试或拨打10086咨询。");

        wlanSmsContent.put("WLAN_121_WLAN", "您已成功取消WLAN套餐，下月生效。");
        wlanSmsContent.put("WLAN_122_WLAN", "您已取消WLAN高校优惠资费套餐，下月生效，同时将关闭您的高校WLAN业务功能。");
        wlanSmsContent.put("WLAN_12_ERROR", "对不起，套餐取消不成功，请稍后再试或拨打10086咨询。");

        // 套餐查询返回的短信内容
        wlanSmsContent.put("WLAN_QY_WLAN", inParam.getString("SMS_TEXT"));
        wlanSmsContent.put("WLAN_QY_ERROR", "对不起，您尚未办理任何WLAN套餐，套餐查询失败，请拨打10086咨询。");

        /*
         * if (!"".equals(inParam.getString("SUIT_INFOS"))){ mailSmsContent.put("WLAN_QY_WLAN",
         * inParam.getString("SUIT_INFOS") + "WLAN包单位时间资费包小时,有效期至2050年12月31日；" + "WLAN包单位时间资费包天,有效期至2050年12月31日；" +
         * "WLAN包单位时间资费包周,有效期至2050年12月31日；" + "WLAN包单位时间资费包月,有效期至2050年12月31日；" + "WLAN包时长资费30元/15小时,有效期至2050年12月31日；" +
         * "WLAN包时长资费50元/40小时,有效期至2050年12月31日；" + "WLAN包时长资费100元/200小时,有效期至2050年12月31日。"); }
         */

        wlanSmsContent.put("WLAN_02_WLAN", "您已成功注销WLAN业务，下月生效；如果您开通了WLAN套餐，将会同步取消。");
        wlanSmsContent.put("WLAN_02_ERROR", "对不起，业务注销不成功，请稍后再试或拨打10086咨询。");

        return wlanSmsContent.getString(str);
    }

    /**
     * 操作码转义
     */
    public static String operCodeParser(IData inparams)
    {

        String operCode = inparams.getString("OPER_CODE");
        String stateCode = "";
        if (operCode.equals("01") || operCode.equals("06") || operCode.equals("15") || operCode.equals("05") || operCode.equals("08"))
        {
            stateCode = "A";// 正常商用
        }
        else if (operCode.equals("04") || operCode.equals("14"))
        {
            stateCode = "N";// 暂停
        }
        else if (operCode.equals("02") || operCode.equals("07") || operCode.equals("89") || operCode.equals("99"))
        {
            stateCode = "E";// 终止
        }
        return stateCode;
    }

    public static IDataset queryOPType(IData inParam) throws Exception
    {

        IData param = new DataMap();
        param.put("SERVICE_ID", inParam.getString("SERVICE_ID"));
        IDataset services = PlatSvcInfoQry.queryServiceInfo(param);
        IDataset opers = new DatasetList();
        if (services == null || services.size() == 0)
        {
            return opers;
        }

        IData service = (IData) services.get(0);

        String bizProcessTag = service.getString("BIZ_PROCESS_TAG");
        return getOperType(service, bizProcessTag);
    }
    
    /**
     * modify by duhj 
     * 查询用户已开平台业务信息
     * 
     * @param user_id
     * @param user_id_a
     * @return
     * @throws Exception
     */
    public static IDataset getUserPlatInfoByUserIdNew(String user_id) throws Exception
    {

        IData iData = new DataMap();
        iData.put("USER_ID", user_id);

        SQLParser platparser = new SQLParser(iData);
        platparser.addSQL(" SELECT t1.PARTITION_ID,to_char(t1.USER_ID) USER_ID,t1.SERVICE_ID,to_char(t1.START_DATE, 'yyyy-mm-dd hh24:mi:ss') START_DATE,to_char(t1.END_DATE, 'yyyy-mm-dd hh24:mi:ss') END_DATE ");
        platparser.addSQL("   FROM TF_F_USER_PLATSVC t1 ");
        platparser.addSQL("  WHERE t1.USER_ID = TO_NUMBER(:USER_ID) ");
        platparser.addSQL("    AND t1.PARTITION_ID = MOD(:USER_ID, 10000) ");
        platparser.addSQL("    AND (t1.BIZ_STATE_CODE = 'A' OR t1.BIZ_STATE_CODE = 'N' OR ");
        platparser.addSQL("        t1.BIZ_STATE_CODE = 'E') ");
        platparser.addSQL("    AND SYSDATE BETWEEN t1.START_DATE AND trunc(last_day(t1.END_DATE)+1)- 1/24/60/60 ");
        //duhj platparser.addSQL("    AND t1.service_id = t2.service_id ");

        IDataset resultset = Dao.qryByParse(platparser);
        
        IData temp = new DataMap();
        String serviceId = "";
        if(IDataUtil.isNotEmpty(resultset)){
        	for (int i = 0; i < resultset.size(); i++) {
        		temp = resultset.getData(i);
				serviceId = temp.getString("SERVICE_ID");		
				IDataset  res = new DatasetList();
				try{
					res = UpcCall.queryPlatSvc(serviceId, BofConst.ELEMENT_TYPE_CODE_PLATSVC, null, null);
				}catch(Exception e){
					
				}

				if(IDataUtil.isEmpty(res)){
					resultset.remove(i);
					i--;
				}else{
					temp.put("BIZ_CODE", res.getData(0).getString("BIZ_CODE"));
					temp.put("INST_ID", res.getData(0).getString("BIZ_CODE"));

				}
			}
        }
        
        return resultset;
    }
    
    

}
