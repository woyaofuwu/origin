
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ParamException implements IBusiException // 参数异常
{
    CRM_PARAM_1("%s,产品操作类型不能为空!"), //
    CRM_PARAM_2("X_GETMODE不存在"), //
    CRM_PARAM_3("PRODUCT_ID不能为空"), //
    CRM_PARAM_4("REMOVE_TAG字段必须为0"), //
    CRM_PARAM_5("ROUTE_EPARCHY_CODE不能为空"), //
    CRM_PARAM_6("SERIAL_NUMBER不能为空"), //
    CRM_PARAM_7("STATE字段不能为空且只能为0或1"), //
    CRM_PARAM_8("USER_ID不能为空"), //
    CRM_PARAM_9("REMOVE_TAG字段值不对，没有【%s】该类型"), //
    CRM_PARAM_10("REMOVE_TAG字段值不对，没有该类型！"), //
    CRM_PARAM_11("X_GETMODE字段值不对，没有【%s】该类型"), //
    CRM_PARAM_12("X_GETMODE参数错误"), //
    CRM_PARAM_13("SERIAL_NUMBER不能为空!"), //
    CRM_PARAM_14("X_GETMODE不能为空!"), //
    CRM_PARAM_15("REMOVE_TAG不能为空!"), //
    CRM_PARAM_16("ROUTE_EPARCHY_CODE不能为空!"), //
    CRM_PARAM_17("EPARCHY_CODE不能为空!"), //
    CRM_PARAM_18("ELEMENT_ID输入不能为空！"), //
    CRM_PARAM_19("不支持该种查询方式；X_GETMODE=%s"), //
    CRM_PARAM_20("TRADE_STAFF_ID不能为空！"), //
    CRM_PARAM_21("ELEMENT_TYPE_CODE输入不能为空！"), //
    CRM_PARAM_22("SERIAL_NUMBER不能为空!"), //
    CRM_PARAM_23("ELEMENT_ID_A不能为空!"), //
    CRM_PARAM_24("ELEMENT_ID_B不能为空!"), //
    CRM_PARAM_25("PRODUCT_ID不能为空！"), //
    CRM_PARAM_26("选择的产品中有元素的参数配置有错!"), //
    CRM_PARAM_27("PRODUCT_MODE不能为空！"), //
    CRM_PARAM_28("PRODUCT_MODE字段值不对，没有该类型！"), //
    CRM_PARAM_30("BRAND_CODE字段值不能传空！"), //
    CRM_PARAM_31("X_ACCEPT_MODE不能为空！"), //
    CRM_PARAM_32("X_ACCEPT_MODE段值不对，没有该类型！"), //
    CRM_PARAM_33("字段【RSRV_VALUE_CODE】传值错误！"), //
    CRM_PARAM_34("MODIFY_TAG输入不能为空"), //
    CRM_PARAM_35("%s获取用户骚扰电话开机业务服务状态参数无数据!"), //
    CRM_PARAM_36("%s获取用户骚扰电话停机业务服务状态参数无数据!"), //
    CRM_PARAM_37("获取用户骚扰电话开机业务服务状态参数无数据!"), //
    CRM_PARAM_38("查询通用参数[2902]出错，请确保你登录地市正确!"), //
    CRM_PARAM_39("查询通用参数[2903]出错，请确保你登录地市正确!"), //
    CRM_PARAM_40("未传入SERIAL_NUMBER"), //
    CRM_PARAM_41("参数配置有问题,请转后台处理配置TD_S_COMMPARA表5094参数的相关数据!"), //
    CRM_PARAM_42("tradeTypeCode无效"), //
    CRM_PARAM_43("userId或serialNumber至少要传一个"), //
    CRM_PARAM_44("%s字段不能为空"), //
    CRM_PARAM_45("【%s】字段必传其中之一"), //
    CRM_PARAM_46("不支持该种查询方式，X_GETMODE = 【%s】"), //
    CRM_PARAM_47("不支持该种查询方式；X_GETMODE=%s"), //
    CRM_PARAM_48("字段【X_GETMODE】传值错误！"), //
    CRM_PARAM_49("未知查询方式；X_GETMODE=%s"), //
    CRM_PARAM_50("X_GETMODE字段值不对，没有【%s】该类型"), //
    CRM_PARAM_51("未知查询方式；X_GETMODE=%s"), //
    CRM_PARAM_52("TRADE_TYPE_CODE字段没传"), //
    CRM_PARAM_53("非连续号段，[%s]不存在！"), //
    CRM_PARAM_54("入网时间限制数据异常！PARA_CODE4[%s]"), //
    CRM_PARAM_55("参数配置错误！PARA_CODE3[%s]"), //
    CRM_PARAM_56("获取通用参数161失败,获得服务级别对应积分单位"), //
    CRM_PARAM_57("获取通用参数166失败,获得服务级别对应积分单位"), //
    CRM_PARAM_58("获取通用参数989失败,获得服务级别对应积分单位"), //
    CRM_PARAM_59("字段%s不存在！"), //
    CRM_PARAM_60("用户信息不存在！"), //
    CRM_PARAM_61("参数传递错误请检查"), //
    CRM_PARAM_62("REMOVE_TAG不能为空"), //
    CRM_PARAM_63("请确认TD_B_PLATSVC表中%s数据已经配置好"), //
    CRM_PARAM_65("获取参数无数据PRODUCT_ATTR"), //
    CRM_PARAM_66("接口参数检查，输入参数%s不存在"), //
    CRM_PARAM_67("接口参数检查，输入参数ATTR_CODE不存在！"), //
    CRM_PARAM_68("接口参数检查，输入参数ATTR_VALUE不能为空，且值为0或1！"), //
    CRM_PARAM_69("接口参数检查，输入参数DISCNT_CODE不存在或值为空"), //
    CRM_PARAM_70("接口参数检查，输入参数MODIFY_TAG不存在或值为空"), //
    CRM_PARAM_71("接口参数检查，输入参数[X_GETMODE]传值错误！"), //
    CRM_PARAM_72("接口参数检查，输入参数用户标识[SERIAL_NUMBER]不存在！"), //
    CRM_PARAM_73("PARAM_ATTR值不存在"), //
    CRM_PARAM_74("接口参数检查，输入参数用户标识[TRADE_EPARCHY_CODE]不存在！"), //
    CRM_PARAM_75("接口参数检查，输入参数用户标识[TRADE_STAFF_ID]不存在！"), //
    CRM_PARAM_76("接口参数检查，输入参数用户标识[USER_ID_A]不存在！"), //
    CRM_PARAM_77("接口参数检查，输入参数[TEAM_ID]不存在！"), //
    CRM_PARAM_78("接口参数检查，输入参数[TEAM_NAME]不存在！"), //
    CRM_PARAM_79("接口参数检查，输入参数[TEAM_TYPE]不存在！"), //
    CRM_PARAM_80("接口参数检查，输入参数[USER_ID_A]不存在！"), //
    CRM_PARAM_81("接口参数检查，输入参数用户标识[SERIAL_NUMBER]不存在！"), //
    CRM_PARAM_82("接口参数检查，输入参数用户标识[TRADE_EPARCHY_CODE]不存在！"), //
    CRM_PARAM_83("接口参数检查，输入参数用户标识[TRADE_STAFF_ID]不存在！"), //
    CRM_PARAM_84("PARAM_CODE值不存在"), //
    CRM_PARAM_85("接口参数检查，输入参数用户标识[USER_ID_A]不存在！"), //
    CRM_PARAM_86("接口参数检查，输入参数用户标识[USER_ID_B]不存在！"), //
    CRM_PARAM_87("接口参数检查，输入参数用户标识[X_GETMODE]不存在！"), //
    CRM_PARAM_88("接口参数检查，输入参数重置密码[NEW_PASSWORD]不存在！"), //
    CRM_PARAM_89("接口参数检查，输入参数重置密码[TRADE_CITY_CODE]不存在！"), //
    CRM_PARAM_90("接口参数检查，输入参数重置密码[TRADE_DEPART_ID]不存在！"), //
    CRM_PARAM_91("接口参数检查，输入参数重置密码[TRADE_EPARCHY_CODE]不存在！"), //
    CRM_PARAM_92("接口参数检查，输入参数重置密码[TRADE_STAFF_ID]不存在！"), //
    CRM_PARAM_93("获取产品信息失败"), //
    CRM_PARAM_94("输入的参数中没有用户号码"), //
    CRM_PARAM_95("EPARCHY_CODE不能为空!"), //
    CRM_PARAM_96("输入的参数中没有登记类型"), //
    CRM_PARAM_97("输入的参数中没有类型"), //
    CRM_PARAM_98("输入的参数中没有操作类型"), //
    CRM_PARAM_99("输入的参数中没有证件地址"), //
    CRM_PARAM_100("输入的参数中没有通信地址"), //
    CRM_PARAM_101("输入的参数中没有账户名称"), //
    CRM_PARAM_102("输入的参数中没有用户号码"), //
    CRM_PARAM_103("输入的参数中没有代理商营业地址"), //
    CRM_PARAM_104("输入的参数中没有客户名称"), //
    CRM_PARAM_105("输入的参数中没有家庭地址"), //
    CRM_PARAM_106("%s,未知操作类型，[%s]"), //
    CRM_PARAM_107("优惠转出号码[SERIAL_NUMBER_A]不能为空"), //
    CRM_PARAM_108("输入的参数中没有IMEI号"), //
    CRM_PARAM_109("输入的参数中没有联系电话"), //
    CRM_PARAM_110("输入的参数中没有证件类型"), //
    CRM_PARAM_111("输入的参数中没有证件号码"), //
    CRM_PARAM_112("业务类型参数不能为空"), //
    CRM_PARAM_113("密码管理：: userInfo.USER_ID 为空!"), //
    CRM_PARAM_114("密码管理：销号用户密码验证，USER_ID是必须的参数！"), //
    CRM_PARAM_115("弱密码管理:PARAM_ATTR=4451参数配置多条!"), //
    CRM_PARAM_116("TRADE_ID不能为空！"), //
    CRM_PARAM_117("TRADE_ID的长度必须为16！"), //
    CRM_PARAM_118("转出号码所属地州[EPARCHY_CODE_A]不能为空"), //
    CRM_PARAM_119("IN_MODE_CODE不能为空！"), //
    CRM_PARAM_120("TERM_IP不能为空！"), //
    CRM_PARAM_121("TRADE_STAFF_ID不能为空！"), //
    CRM_PARAM_122("TRADE_EPARCHY_CODE不能为空！"), //
    CRM_PARAM_123("TRADE_CITY_CODE不能为空！"), //
    CRM_PARAM_124("TRADE_DEPART_ID不能为空！"), //
    CRM_PARAM_125("ACCTID和USERID无对应的记录"), //
    CRM_PARAM_126("BIZ_TYPE_CODE取值不能为空且只能为37"), //
    CRM_PARAM_127("CAMPN_ID输入不能为空！"), //
    CRM_PARAM_128("CODE_CODE参数缺失"), //
    CRM_PARAM_129("优惠转入号码[SERIAL_NUMBER_B]不能为空"), //
    CRM_PARAM_130("CODE为必填参数！"), //
    CRM_PARAM_131("DISCNT_CODE不能为空"), //
    CRM_PARAM_132("EPARCHY_CODE是必填参数。"), //
    CRM_PARAM_133("MODIFY_TAG不能为空"), //
    CRM_PARAM_134("NAME为必填参数！"), //
    CRM_PARAM_135("PRODUCT_ID是必填参数。"), //
    CRM_PARAM_136("ProductSelect 中没有传递 OPER_TYPE!"), //
    CRM_PARAM_137("SERIAL_NUMBER不能为空"), //
    CRM_PARAM_139("SP_ID不能为空"), //
    CRM_PARAM_140("转入号码所属地州[EPARCHY_CODE_B]不能为空"), //
    CRM_PARAM_141("TD_O_CUSTMGR_COMMPARA无对应参数"), //
    CRM_PARAM_142("TD_STATIC表中未配置TYPE_ID='USERCARD'和DATA_ID=%s的数据，请检查"), //
    CRM_PARAM_143("TD_S_COMMPARA参数表PARA_CODE3不能为空!"), //
    CRM_PARAM_144("TD_S_COMMPARA参数表PARA_CODE4不能为空!"), //
    CRM_PARAM_145("TD_S_COMMPARA参数表配置错误!"), //
    CRM_PARAM_146("TD_S_COMMPARA配置不正确"), //
    CRM_PARAM_147("TD_S_COMMPARA配置不正确,请确认该参数是否已失效"), //
    CRM_PARAM_148("TD无线座机参数没有配置"), //
    CRM_PARAM_149("TRADE_DAY输入不能为空！"), //
    CRM_PARAM_150("TRADE_ID为空!请联系维护人员!!"), //
    CRM_PARAM_151("接入号码SERIAL_NUMBER不能为空!"), //
    CRM_PARAM_152("TRADE_STAFF_ID不能为空"), //
    CRM_PARAM_153("TRADE_TYPE_CODE不能为空"), //
    CRM_PARAM_154("USER_ID不能为空"), //
    CRM_PARAM_155("USER_ID是必填参数。"), //
    CRM_PARAM_156("XXXXX:传入的TAG标记错误！"), //
    CRM_PARAM_157("X_CHECK_TAG取值不能为空且只能为1"), //
    CRM_PARAM_158("X_EXISTFLAG输入不能为null或空字符串！"), //
    CRM_PARAM_159("X_ID输入不能为空！"), //
    CRM_PARAM_160("X_TRANS_CODE不能为空"), //
    CRM_PARAM_161("[%s]该号码已经生成了资料，请输入新号码！"), //
    CRM_PARAM_162("RSRV_STR15 产品属性代码格式错误！"), //
    CRM_PARAM_163("attr_value参数内容错误，请检查attr_vlaue内容，为分隔符分隔的数字"), //
    CRM_PARAM_164("ec_code为空!请联系维护人员!!"), //
    CRM_PARAM_168("serial_number不能为空"), //
    CRM_PARAM_169("serviceid不能为空"), //
    CRM_PARAM_170("spid不能为空"), //
    CRM_PARAM_171("strActionCode输入不能为空！"), //
    CRM_PARAM_172("strTempletCode输入不能为空！"), //
    CRM_PARAM_173("RSRV_STR16 产品属性值格式错误！"), //
    CRM_PARAM_174("td_m_res_para地州参数表配置为空！"), //
    CRM_PARAM_175("trade_eparchy_code不能为空"), //
    CRM_PARAM_176("trade_id不能为空"), //
    CRM_PARAM_177("【明细账目列表】和【账目全选】不能同时勾选"), //
    CRM_PARAM_178("参数错误!"), //
    CRM_PARAM_179("参数没有配置！"), //
    CRM_PARAM_180("参数内容错误，请检查参数内容"), //
    CRM_PARAM_181("操作标识只能是一位数字，并且只能为1（增加）2（删除）3（修改），号码%s错误"), //
    CRM_PARAM_182("操作代码OPER_CODE不能传空"), //
    CRM_PARAM_183("RSRV_STR17 产品属性名称格式错误！"), //
    CRM_PARAM_184("操作代码[OPER_CODE-01新增02注销03暂停04恢复]错误！"), //
    CRM_PARAM_185("操作类型TYPE不能为空！"), //
    CRM_PARAM_186("操作类型【%s】错误!"), //
    CRM_PARAM_187("操作失败！错误编码[%s]"), //
    CRM_PARAM_188("查询方式错误：X_GETMODE=%s"), //
    CRM_PARAM_189("查询自动发指令参数赋值设置失败！"), //
    CRM_PARAM_190("产品依赖互斥判断:获取当前省代码无有效数据！"), //
    CRM_PARAM_191("程序异常，静态参数未配置，请联系管理员！"), //
    CRM_PARAM_192("传入的数据中找不到[COUNT]！"), //
    CRM_PARAM_193("传入的数据中找不到[RULE_ID]！"), //
    CRM_PARAM_194("RSRV_STR18 产品属性操作代码格式错误！"), //
    CRM_PARAM_195("此奖品不能有多条参数配置"), //
    CRM_PARAM_196("此奖品参数配置不存在或者错误"), //
    CRM_PARAM_197("此奖品没有参数配置"), //
    CRM_PARAM_198("此业务不能进行催办！"), //
    CRM_PARAM_199("此优惠编码不可用,请先配置参数!"), //
    CRM_PARAM_200("此优惠编码不能对应多条配置参数!"), //
    CRM_PARAM_201("导入的扩展参数键值个数不配对！"), //
    CRM_PARAM_202("发起方domain不能为空"), //
    CRM_PARAM_203("服务编码ELEMENT_ID是必须填写的！"), //
    CRM_PARAM_204("服务号码SERIAL_NUMBER是必须填写的！"), //
    CRM_PARAM_205("端对端传递：缺少关键字段[GROUP_ID]!"), //
    CRM_PARAM_206("服务号码不能为空，请检查输入参数！"), //
    CRM_PARAM_207("服务号码起始号段输入错误！"), //
    CRM_PARAM_208("服务开通有必填参数未填。"), //
    CRM_PARAM_209("副号码不能为空，请检查输入参数！"), //
    CRM_PARAM_210("该机型参数配置不完整，请检查!"), //
    CRM_PARAM_211("该批量业务不需要资源信息，请选择[导入号段]，填写数量即可！"), //
    CRM_PARAM_212("该序号%s已经存在"), //
    CRM_PARAM_213("根据厂家编码factoryCode获取厂家名称, 未查到记录TD_M_RES_CORP,SEL_BY_PK,CORP_NO:%s"), //
    CRM_PARAM_214("购机类型参数出错!"), //
    CRM_PARAM_215("购机类型参数为空！"), //
    CRM_PARAM_216("%s:不能缺少手机号码或者IMEI号码其中之一参数"), //
    CRM_PARAM_217("端对端传递：缺少关键字段[IBSYSID]!"), //
    CRM_PARAM_218("购机限制类型参数配置错误!"), //
    CRM_PARAM_219("规则类型[RULE_BIZ_KIND_CODE]是inData中的必须输入参数!"), //
    CRM_PARAM_220("规则类型[RULE_BIZ_TYPE_CODE]是inData中的必须输入参数!"), //
    CRM_PARAM_221("获取FTP配置参数失败！"), //
    CRM_PARAM_222("获取MODIFY_TAG的取值不正确"), //
    CRM_PARAM_223("获取参数失败!"), //
    CRM_PARAM_224("获取登记类型参数错误!"), //
    CRM_PARAM_225("获取兑换比例参数出错！"), //
    CRM_PARAM_226("获取个人业务账期生效方式参数失败!"), //
    CRM_PARAM_227("获取个性化参数失败！"), //
    CRM_PARAM_228("端对端传递：缺少关键字段[OPER_CODE]!"), //
    CRM_PARAM_229("获取活动类型参数错误!"), //
    CRM_PARAM_230("获取积分门限参数出错！"), //
    CRM_PARAM_231("获取静态参数【BORDER_DATE】失败!"), //
    CRM_PARAM_232("获取通用参数1989失败,获得服务级别对应积分单位"), //
    CRM_PARAM_233("获取银行合同号生成配置出错!"), //
    CRM_PARAM_234("交易码REQ_NUM不能为空！"), //
    CRM_PARAM_235("接口参数检查，输入参数'主号码'不存在或主号码状态不对(如:停机等)！"), //
    CRM_PARAM_236("接口参数检查，输入参数[%s]不存在！"), //
    CRM_PARAM_237("接口参数检查，输入参数[OPER_TYPE]类型错误01代表开机，02代表停机！"), //
    CRM_PARAM_238("开户数量输入批量上限[10000]或者输入为[0]，请重新输入!"), //
    CRM_PARAM_239("端对端传递：缺少关键字段[PRODUCT_ID]!"), //
    CRM_PARAM_240("开始时间START_DATE不能传空"), //
    CRM_PARAM_241("落地方domain不能为空"), //
    CRM_PARAM_242("没传必填参数！"), //
    CRM_PARAM_243("没有CSM_ADJ_UserOtherQTCS调整服务次数权限,修改失败"), //
    CRM_PARAM_244("没有查询到%s所对应的短信模板"), //
    CRM_PARAM_245("没有传入serviceId"), //
    CRM_PARAM_246("没有传入userId"), //
    CRM_PARAM_247("没有传入正确的资费！"), //
    CRM_PARAM_248("没有获取到wlan特殊参数信息"), //
    CRM_PARAM_249("没有配置该批量类型参数"), //
    CRM_PARAM_250("端对端传递：缺少关键字段[USER_ID]!"), //
    CRM_PARAM_251("没有配置该批量类型的参数"), //
    CRM_PARAM_252("每次导入的数据不能超过%s条,请检查！"), //
    CRM_PARAM_256("企业代码SP_CODE不能传空"), //
    CRM_PARAM_257("企业代码SP_CODE不能为空"), //
    CRM_PARAM_258("前%s位必须一样"), //
    CRM_PARAM_259("请传入正确的数据格式：[{RULE_ID=[\"XX\"], COUNT=[\"XX\"]}, {RULE_ID=[\"XX\"], COUNT=[\"XX\"]}]！"), //
    CRM_PARAM_260("请检查TD_S_COMMPARA表2222参数的配置"), //
    CRM_PARAM_261("端对端传递：缺少关键字段管理属相编码！"), //
    CRM_PARAM_262("请检查TD_S_STATIC表的配置"), //
    CRM_PARAM_263("请配置TD_S_COMMPARA!"), //
    CRM_PARAM_264("请配置品牌参数！"), //
    CRM_PARAM_265("请配置正确的礼品参数!"), //
    CRM_PARAM_266("请求参数【%s】为空，请检查"), //
    CRM_PARAM_267("请输入sim卡号！"), //
    CRM_PARAM_268("请输入服务号码！"), //
    CRM_PARAM_269("请输入用户服务号码！"), //
    CRM_PARAM_270("请选择不良信息后，再继续办理业务！"), //
    CRM_PARAM_271("请选择酬金返还参与原因！"), //
    CRM_PARAM_272("取消操作,子产品USER_ID不能为空！"), //
    CRM_PARAM_273("请选择酬金返还金额！"), //
    CRM_PARAM_274("请选择积分扣减方式！"), //
    CRM_PARAM_275("请选择数据导入类型！"), //
    CRM_PARAM_276("请选择用户类型！"), //
    CRM_PARAM_277("请在[TD_S_COMMPARA]配置工号每月每天下发短信参数"), //
    CRM_PARAM_278("入参有缺失!"), //
    CRM_PARAM_279("入参有缺失, Gift_ID, Trade_ID!"), //
    CRM_PARAM_280("删除自动发指令参数赋值设置失败！"), //
    CRM_PARAM_281("上传文件格式不正确！<br>第【%s】行数据有误！<br>"), //
    CRM_PARAM_282("手机号码SERIAL_NUMBER不能为空！"), //
    CRM_PARAM_283("新增操作,子产品USER_ID应为空！"), //
    CRM_PARAM_284("手机号码必须为11位数字，号码%s错误"), //
    CRM_PARAM_285("输入参数[X_GETMODE]传值错误！"), //
    CRM_PARAM_286("输入的参数中没有ACCT_ID"), //
    CRM_PARAM_287("输入的参数中没有EPARCHY_CODE"), //
    CRM_PARAM_288("输入的参数中没有MAIN_TAG"), //
    CRM_PARAM_289("输入的参数中没有TRADE_ID"), //
    CRM_PARAM_290("数据校验失败!<br>%s"), //
    CRM_PARAM_291("未传入正确参数，请检查！"), //
    CRM_PARAM_292("未能取得服务参数！"), //
    CRM_PARAM_293("未知礼品赠送类型![%s]"), //
    CRM_PARAM_294("新旧密码不能相同"), //
    CRM_PARAM_295("无对应HSS参数配置：【TAB_NAME:TD_S_COMMPARA】【KEYS:PARAM_ATTR,PARAM_CODE,PARA_CODE1】 【VALUES:%s,%s,%s】"), //
    CRM_PARAM_296("无对应HSS参数配置：【TAB_NAME:TD_S_COMMPARA】【KEYS:PARAM_ATTR,PARA_CODE2,PARA_CODE1】 【VALUES:%s,%s,%s】"), //
    CRM_PARAM_297("无对应HSS参数配置：【TAB_NAME:TD_S_COMMPARA】【KEYS:PARAM_ATTR,PARA_CODE2】 【VALUES:%s,%s】"), //
    CRM_PARAM_298("无法得到服务参数!"), //
    CRM_PARAM_299("写卡控件入参【afterWriteCardClassName】不能为空！"), //
    CRM_PARAM_300("写卡控件入参【afterWriteCardMothed】不能为空！"), //
    CRM_PARAM_301("写卡控件入参【beforeWriteCardClassName】不能为空！"), //
    CRM_PARAM_302("写卡控件入参【beforeWriteCardMothed】不能为空！"), //
    CRM_PARAM_303("写卡控件入参【inModeCode】不能为空！"), //
    CRM_PARAM_304("写卡控件入参【mode】不能为空！"), //
    CRM_PARAM_305("参数不正确，SERIAL_NUMBER、DESTINATION、ACCEPT_DATE、SMS_CONTENT必填"), //
    CRM_PARAM_306("新增自动发指令参数赋值设置失败！"), //
    CRM_PARAM_307("修改参数失败！"), //
    CRM_PARAM_308("修改自动发指令参数赋值设置失败！"), //
    CRM_PARAM_309("选择非现金付费类别时，银行名称不能为空！"), //
    CRM_PARAM_310("选择非现金付费类别时，银行账号不能为空！"), //
    CRM_PARAM_311("选择记录进行更新操作时不能修改服务代码，请重新选择记录操作！"), //
    CRM_PARAM_312("选择记录进行更新操作时不能修改企业代码，请重新选择记录操作！"), //
    CRM_PARAM_313("选择记录进行删除操作时不能修改服务代码，请重新选择记录操作！"), //
    CRM_PARAM_314("选择记录进行删除操作时不能修改企业代码，请重新选择记录操作！"), //
    CRM_PARAM_315("业务代码BIZ_CODE不能传空"), //
    CRM_PARAM_316("套餐编码（SPBizCode-BIZ_CODE）未传"), //
    CRM_PARAM_317("业务代码BIZ_CODE不能为空"), //
    CRM_PARAM_318("业务规则校验: SuperLimit类型规则参数不能为空!"), //
    CRM_PARAM_319("业务类型编码BIZ_TYPE_CODE不能传空"), //
    CRM_PARAM_320("业务类型编码BIZ_TYPE_CODE不能为空"), //
    CRM_PARAM_321("业务类型编码BIZ_TYPE_CODE和企业代码SP_CODE不能同时为空"), //
    CRM_PARAM_322("业务类型编码BIZ_TYPE_CODE为空或者不对"), //
    CRM_PARAM_323("移动号码和铁通号码不能同时为空"), //
    CRM_PARAM_324("用户服务号码信息异常，请重新按回车查询信息！"), //
    CRM_PARAM_325("优惠编码不能为空!"), //
    CRM_PARAM_326("有效期标志只能为一位数字，号码%s错误"), //
    CRM_PARAM_327("%s获取返回数据出错"), //
    CRM_PARAM_328("IN_MODE_CODE不能为空!"), //
    CRM_PARAM_329("没有查询到%s所对应的短信模板"), //
    CRM_PARAM_330("orgdomain为空"), //
    CRM_PARAM_331("BUSI_SIGN报文类型不能为空!"), //
    CRM_PARAM_332("OPER_TYPE无法识别的类型"), //
    CRM_PARAM_333("PRODUCT_TYPE_CODE不存在,请输入PRODUCT_TYPE_CODE! "), //
    CRM_PARAM_334("PR_INFO为空"), //
    CRM_PARAM_335("部分参数为空，请返回上一步填写信息"), //
    CRM_PARAM_336("参数GROUP_ID传入错误!"), //
    CRM_PARAM_337("SUBSYS_CODE值不存在"), //
    CRM_PARAM_338("参数X_GETMODE传入错误!"), //
    CRM_PARAM_339("服务控制参数为空,请联系管理员"), //
    CRM_PARAM_340("根据批次号，查询批次信息失败"), //
    CRM_PARAM_341("获得该次导入的成员号码失败!~"), //
    CRM_PARAM_342("获取PBX个性化参数 失败 ！"), //
    CRM_PARAM_343("获取VPN参数失败！"), //
    CRM_PARAM_344("获取车务通个性化参数失败"), //
    CRM_PARAM_345("获取个性化参数失败！"), //
    CRM_PARAM_346("获取集团彩铃个性化参数失败"), //
    CRM_PARAM_347("获取批量任务的参数信息失败!"), //
    CRM_PARAM_348("通用参数查询无数据"), //
    CRM_PARAM_349("获取手机邮箱个性化参数 失败 ！"), //
    CRM_PARAM_350("获取移动总机个性化参数失败"), //
    CRM_PARAM_351("没有配置该批量类型参数"), //
    CRM_PARAM_352("没有配置该批量类型的参数"), //
    CRM_PARAM_353("没有找到查询参数[GROUP_ID]！"), //
    CRM_PARAM_354("没有找到查询参数[PRODUCT_ID]！"), //
    CRM_PARAM_355("没有找到查询参数[SERIAL_NUMBER]！"), //
    CRM_PARAM_356("没有找到查询参数[X_GETMODE]！"), //
    CRM_PARAM_357("没有找到查询参数[X_PAGE]！"), //
    CRM_PARAM_358("没有找到查询参数[X_PAGE_SIZE]！"), //
    CRM_PARAM_359("找不到对应的产品配置参数！"), //
    CRM_PARAM_360("未知的REMOVE_TAG值：%s"), //
    CRM_PARAM_361("未知校验类型！deal_type=%s"), //
    CRM_PARAM_362("找不到对应的产品配置参数！"), //
    CRM_PARAM_363("操作类型【%s】错误!"), //
    CRM_PARAM_364("SERIAL_NUMBER不能为空"), //
    CRM_PARAM_365("未在TD_S_COMMPARA表配置短信通知模版参数![1080][%s]"), //
    CRM_PARAM_366("生效方式参数错误，开通的优惠不能使用该生效方式！"), //
    CRM_PARAM_367("产品模式参数错误！"), //
    CRM_PARAM_368("ID_TYPE不能为空"), //
    CRM_PARAM_369("PACKAGE_ID不能为空"), //
    CRM_PARAM_370("X_TAG不存在"), //
    CRM_PARAM_371("SERIAL_NUMBER不能为空"), //
    CRM_PARAM_372("没有配置产品属性参数或者配置错误，filterDs 为空，请联系管理员配置！"), //
    CRM_PARAM_373("此奖品不能有多条参数配置"), //
    CRM_PARAM_374("此奖品参数配置不存在或者错误"), //
    CRM_PARAM_375("OPER_TYPE无法识别的类型"), //
    CRM_PARAM_376("接口传入的参数信息错误！参数操作编码OPER_CODE不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_377("接口传入的参数信息错误！参数组类型HUNTING_TYPE不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_378("接口传入的参数信息错误！参数组类型TEAM_TYPE不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_379("接口传入的参数信息错误！产品参数的操作方式MODIFY_TAG信息不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_380("接口传入的参数信息错误！产品参数的属性编码ATTR_CODE信息不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_381("X_TAG值不正确[%s]"), //
    CRM_PARAM_382("接口传入的参数信息错误！产品参数的属性编码ATTR_VALUE信息不在规范格式内,接口传入值【%s】"), //
    CRM_PARAM_383("SERIAL_NUMBER[号码]不能为空!"), //
    CRM_PARAM_384("NEW_SERIAL_NUMBER[新号码]不能为空!"), //
    CRM_PARAM_385("传入的OPER_CODE错误!"), //
    CRM_PARAM_386("该集团没有订购此产品！"), //
    CRM_PARAM_387("礼品赠送配置错误!"), //
    CRM_PARAM_388("根据地州[%s]找不到号段公共参数！"), //
    CRM_PARAM_389("根据地州[%s]找到的号段公共参数为空！"), //
    CRM_PARAM_390("END_TIME不能为空"), //
    CRM_PARAM_391("OPER_TYPE不能为空"), //
    CRM_PARAM_392("获取业务包赠送金额没有数据！"), //
    CRM_PARAM_393("获取用户状态无数据！"), //
    CRM_PARAM_394("获取联机指令信息无数据!"), //
    CRM_PARAM_395("没有符合查询条件的用户优惠数据！"), //
    CRM_PARAM_396("没有符合查询条件的二卡合一数据！"), //
    CRM_PARAM_397("该用户%s不是一卡双号用户！"), //
    CRM_PARAM_398("获取用户资料时，业务类型编码不能为空！"), //
    CRM_PARAM_399("该用户的语音服务状态为[%s],不能办理此业务！"), //
    CRM_PARAM_400("该用户没有办理GPRS业务，如需GPRS功能请办理！"), //
    CRM_PARAM_401("该用户的GPRS服务状态不是暂停状态！"), //
    CRM_PARAM_402("根据集团客户编码[%s]找不到集团区域特点信息！"), //
    CRM_PARAM_403("542006,参数中没有终端串号!"), //
    CRM_PARAM_404("542006,参数中没有销售价格!"), //
    CRM_PARAM_405("542007,费用不合法，费用须在【%s】-【%s】元之间!"), //
    CRM_PARAM_406("542005,串号没有做为四码合一数据入库,请核实!"), //
    CRM_PARAM_407("542006,激活码没有做为四码合一数据入库或已使用,请核实!"), //
    CRM_PARAM_408("542005,该机型没有配置档次参数,请与管理员联系!"), //
    CRM_PARAM_409("【%s】参数类型不能为空!"), //
    CRM_PARAM_410("请配置好基本接入号号段!"), //
    CRM_PARAM_411("在参数表中找不到此参数的配置"), // 100004
    CRM_PARAM_412("输入参数IN_MODE_CODE是必须的"), // 100001
    CRM_PARAM_413("输入参数KIND_ID是必须的"), // 100001
    CRM_PARAM_414("输入参数IDTYPE是必须的"), // 100001
    CRM_PARAM_415("输入参数IDITEMRANGE是必须的"), // 100001
    CRM_PARAM_416("输入参数OPRNUMB是必须的"), // 100001
    CRM_PARAM_417("输入参数BIZTYPE是必须的"), // 100001
    CRM_PARAM_418("输入参数CHANNELLD是必须的"), // 100001
    CRM_PARAM_419("输入参数SESSIONID是必须的"), // 100001
    CRM_PARAM_420("输入参数IDENTCODE是必须的"), // 100001
    CRM_PARAM_421("输入参数PACKID是必须的"), // 100001
    CRM_PARAM_422("输入参数EFFECTTYPE是必须的"), // 100001
    CRM_PARAM_423("输入参数PACK_CODE是必须的"), // 100001
    CRM_PARAM_424("输入参数OPR_CODE是必须的"), // 100001
    CRM_PARAM_425("输入参数OPR_CODE取值错误"), // 100002
    CRM_PARAM_426("输入参数PRODUNCTINFO是必须的"), // 100001
    CRM_PARAM_427("输入参数ENCODTYPE是必须的"), // 100001
    CRM_PARAM_428("输入参数SPID是必须的"), // 100001
    CRM_PARAM_429("输入参数BIZCODE是必须的"), // 100001
    CRM_PARAM_430("输入参数OPRCODE是必须的"), // 100001
    CRM_PARAM_431("输入参数SPID,BIZCODE取值错误"), //
    CRM_PARAM_432("请选择文件!"), //
    CRM_PARAM_433("操作类型[%s]未定义,请联系管理员"), // 100002
    CRM_PARAM_434("01:02:兑换的产品与套餐互斥"), //
    CRM_PARAM_435("%s:05:兑换类型与受理手机号码不匹配"), //
    CRM_PARAM_436("%s:03:月末最后一天不受理"), //
    CRM_PARAM_437("%s:02:兑换的产品与套餐互斥"), //
    CRM_PARAM_438("02:07:超过兑换次数限制"), //
    CRM_PARAM_439("只能接受标识类型为01：手机号码"), // 500081
    CRM_PARAM_440("只能接受操作代码为01--业务退订"), // 500081
    CRM_PARAM_441("参数表中未配置相应的产品编码"), // 100001
    CRM_PARAM_442("BIZ_CODE格式错误，应该是BIZ_CODE|BIZ_TYPE_CODE格式！"), // 100001
    CRM_PARAM_443("只能接受生效方式为02：立即生效或04下月生效"), // 500081
    CRM_PARAM_444("RSRV_STR15 产品属性代码格式错误！"), //
    CRM_PARAM_445("RSRV_STR16 产品属性值格式错误！"), //
    CRM_PARAM_446("RSRV_STR17 产品属性名称格式错误！"), //
    CRM_PARAM_447("RSRV_STR18 产品属性操作代码格式错误！"), CRM_PARAM_448("只能接受操作代码为01--业务开通"), // 500081
    CRM_PARAM_449("OPER_CODE参数不能为空"), //
    CRM_PARAM_450("状态转换参数表找不到数据，请检查配置"), //
    CRM_PARAM_451("用户%s服务状态不符合当前操作的状态要求"), //
    CRM_PARAM_452("所有服务状态均已符合目标状态，无需变更"), CRM_PARAM_453("02:08:重复订购叠加包的次数超过限制次数"), CRM_PARAM_454("准备删除费用目录时发现DATA_ID值为空，请确认！"), CRM_PARAM_455("操作类型X_TAG=[%s]不正确！"), CRM_PARAM_456("没有可以操作的数据！"), CRM_PARAM_500("生成用户凭证信息错误!"), CRM_PARAM_501(
            "02:08:重复订购叠加包的次数超过20次！"), //
    CRM_PARAM_502("宽带预存处理标识WIDENET_PREFEE_TAG尚未配置"), CRM_PARAM_503("未在TD_S_COMMPARA表配置服务参数![1127]:[%s]"), CRM_PARAM_504("传入了不一致的SERIAL_NUMBER_A！"), CRM_PARAM_505("传入了不一致的USER_ID_A！"), CRM_PARAM_506("%s,TD_S_COMMPARA表未配置[%s]对应参数关系!"), CRM_PARAM_507(
            "接口传入的参数信息错误,BOOKING_TAG只能为0或1!"), CRM_PARAM_508("接口传入的参数信息错误,ELEMENT_TYPE_CODE只能为P,D,S或Z!"), CRM_PARAM_509("接口传入的参数信息错误,MODIFY_TAG只能为0,1或2!"), CRM_PARAM_510("接口传入的参数信息错误,CNTRX_MEMB_POWER只能为1或2!"), CRM_PARAM_511(
            "【SHORT_CODE】和【OLD_SHORT_CODE】参数必填！"), CRM_PARAM_512("若设置为话务员，则总机号码【SUPERTELNUMBER】参数必填！"), //
    CRM_PARAM_513("[TD_B_ATTR_BIZ]配置有问题,根据集团服务ID【%s】,获取对应成员服务ID不存在，请联管理员处理!"), CRM_PARAM_514("集团产品受理时，从业务属性参数表[TD_B_ATTR_BIZ]读取[%s]产品服务号码生成规则无数据，请配置！"), CRM_PARAM_515("当前只支持主产品变更前的校验,ELEMENT_TYPE_CODE只能为P!"), CRM_PARAM_516(
            "当前只支持主产品变更前的校验,MODIFY_TAG只能为0!"), 
    CRM_PARAM_517("菜单参数OPER_TYPE不能为空"),
    CRM_PARAM_518("菜单参数OPER_TYPE配置错误【停机：STOP】【开机：OPEN】"),
    CRM_PARAM_519("该用户未设定密码，请到用户密码重置界面设定密码。")//
            ;
    private final String value;

    private ParamException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
