
package com.asiainfo.veris.crm.order.pub.exception;

import com.asiainfo.veris.crm.order.pub.frame.bcf.exception.IBusiException;

public enum ProductException implements IBusiException // 产品异常
{
    CRM_PRODUCT_1("%s%s]，查询产品信息不存在! "), //
    CRM_PRODUCT_2("对应的子产品ID：[%s]未查询到产品信息！"), //
    CRM_PRODUCT_3("获取产品关系无信息"), //
    CRM_PRODUCT_4("该产品无法办理指定业务!"), //
    CRM_PRODUCT_5("该产品无法办理指定业务!"), //
    CRM_PRODUCT_6("BBOSS相关注销业务请走产品变更的取消！"), //
    CRM_PRODUCT_7("RSRV_STR15 产品属性代码格式错误！"), //
    CRM_PRODUCT_8("RSRV_STR16 产品属性值格式错误！"), //
    CRM_PRODUCT_9("RSRV_STR17 产品属性名称格式错误！"), //
    CRM_PRODUCT_10("RSRV_STR18 产品属性操作代码格式错误！"), //
    CRM_PRODUCT_11("XXXXX:根据服务获取产品包信息失败！"), //
    CRM_PRODUCT_12("XXXXX:获取个人宽带产品信息失败！"), //
    CRM_PRODUCT_13("所传的产品不允许订购多个！"), //
    CRM_PRODUCT_14("X_TRADE_PRODUCT不存在，请重新选择产品！"), //
    CRM_PRODUCT_15("[%s]在TD_B_PRODUCT_COMP参数没有配置！"), //
    CRM_PRODUCT_16("【%s】的产品为【%s|%s】，非集团成员产品，不能在该界面注销！"), //
    CRM_PRODUCT_17("本次成员变更有以下产品失败：</br>%s</br></br>以下产品成功：%s</br>"), //
    CRM_PRODUCT_18("变更产品为【%s】失败!"), //
    CRM_PRODUCT_19("查询产品%s出错：无记录！"), //
    CRM_PRODUCT_20("查询关系类型出错，请检查是否配置了产品关系表"), //
    CRM_PRODUCT_21("查询家庭产品失败：无产品资料！"), //
    CRM_PRODUCT_22("查询亲情产品表错误：无产品资料！"), //
    CRM_PRODUCT_23("获取关系类型失败!"), //
    CRM_PRODUCT_24("未查询到子产品信息！"), //
    CRM_PRODUCT_25("产品ID不能为空"), //
    CRM_PRODUCT_26("产品【%s】的模型下不可选【%s】资费!"), //
    CRM_PRODUCT_27("产品【%s】没有配置暂停、恢复、注销需要发送报文的属性"), //
    CRM_PRODUCT_28("产品编码：%s在产品表TD_B_PRODUCT无配置信息,请与管理员联系."), //
    CRM_PRODUCT_29("产品不能为空!"), //
    CRM_PRODUCT_30("产品的分省支付方式属性未填写"), //
    CRM_PRODUCT_31("产品分类不能为空!"), //
    CRM_PRODUCT_32("产品或产品属性ProPackType无效!"), //
    CRM_PRODUCT_33("产品配置出错！"), //
    CRM_PRODUCT_34("产品配置了反馈阶段，但未配置3400参数"), //
    CRM_PRODUCT_35("未查询到子产品用户信息！"), //
    CRM_PRODUCT_36("产品属性【%s】的3527参数配置不正确"), //
    CRM_PRODUCT_37("产品元素配置错误"), //
    CRM_PRODUCT_38("此业务只受理家庭产品变更，个人产品请到产品变更办理！"), //
    CRM_PRODUCT_39("从用户产品表取产品生效日期失败！"), //
    CRM_PRODUCT_40("点选产品校验：客户标识[CUST_ID]是必须的！"), //
    CRM_PRODUCT_41("点选产品校验：营销包标识[PACKAGE_ID]是必须的！"), //
    CRM_PRODUCT_42("点选产品校验：营销产品标识[PRODUCT_ID]是必须的！"), //
    CRM_PRODUCT_43("点选产品校验：用户标识[USER_ID]是必须的！"), //
    CRM_PRODUCT_44("对不起,操作员工无权操作此产品下相应优惠"), //
    CRM_PRODUCT_45("对不起，该产品不可以办理此亲情业务或者该产品暂时还未配置优惠信息"), //
    CRM_PRODUCT_46("修改操作,子产品USER_ID不能为空！"), //
    CRM_PRODUCT_47("对不起，该产品不可以办理亲情业务。（产品限制暂时还未配置）"), //
    CRM_PRODUCT_48("对不起，未获取到亲情产品信息，请核对是否选择了亲情产品！"), //
    CRM_PRODUCT_49("对不起，用户预约产品不可以办理此项亲情业务。"), //
    CRM_PRODUCT_50("多个产品的操作类型不一致，不能一同办理!"), //
    CRM_PRODUCT_51("副卡产品限制参数配置有误！"), //
    CRM_PRODUCT_52("该产品的此功能尚未提供！"), //
    CRM_PRODUCT_53("该产品功能暂未提供！"), //
    CRM_PRODUCT_54("该产品无此类型业务！"), //
    CRM_PRODUCT_55("该产品无法办理指定业务!"), //
    CRM_PRODUCT_56("获取产品ID无数据!"), //
    CRM_PRODUCT_57("该产品下没有对应的包数据!"), //
    CRM_PRODUCT_58("该号码已经订购过组合产品!"), //
    CRM_PRODUCT_59("该用户产品属于后付费，不能办理BOSS预付费用户一卡多号隐号业务！"), //
    CRM_PRODUCT_60("该用户产品属于预付费，不能办理BOSS后付费服务！"), //
    CRM_PRODUCT_61("根据用户编码【%s】查询PRODUCT_OFFER_ID出错！"), //
    CRM_PRODUCT_62("获取PRODUCT_ID异常！"), //
    CRM_PRODUCT_63("获取产品和销售包 无结果: %s"), //
    CRM_PRODUCT_64("获取产品和销售包错误!原因:%s"), //
    CRM_PRODUCT_65("获取产品信息失败"), //
    CRM_PRODUCT_66("获取组合产品参数无数据,请先配置"), //
    CRM_PRODUCT_67("不是彩铃产品,请输入正确的彩铃产品编号! "), //
    CRM_PRODUCT_68("没有查到该产品的服务元素！"), //
    CRM_PRODUCT_69("没有订购家庭产品，不能取消套餐"), //
    CRM_PRODUCT_70("没有配置家庭产品"), //
    CRM_PRODUCT_71("请填写产品的优惠及服务信息！"), //
    CRM_PRODUCT_72("请选择某种产品！"), //
    CRM_PRODUCT_73("请选择要成员开户产品！"), //
    CRM_PRODUCT_74("取产品cancel_tag出错！"), //
    CRM_PRODUCT_75("取得用户主产品信息出错！"), //
    CRM_PRODUCT_76("如需开通企业飞信语音功能，请先升级该集团下的VPMN产品为融合VPMN产品"), //
    CRM_PRODUCT_77("商品产品与子产品的TD_B_PRODUCT_COMP信息未配置！"), //
    CRM_PRODUCT_78("产品编号为[%s]的产品信息不存在!"), //
    CRM_PRODUCT_79("商品没有对应的子产品用户！"), //
    CRM_PRODUCT_80("商品下[%s]在TD_B_PRODUCT_COMP_RELA无产品配置"), //
    CRM_PRODUCT_81("未得到产品模式!"), //
    CRM_PRODUCT_82("未输入要变更的主产品或与原主产品一致，无法办理！"), //
    CRM_PRODUCT_83("未找到BBOSS成员订购产品信息,不能进行新增之外的操作！"), //
    CRM_PRODUCT_84("未找到或找到不止一条PUSHMAIL手机邮箱服务对应的产品和包的记录！"), //
    CRM_PRODUCT_85("未找到或找到不止一条gprs服务产品id包id记录！"), //
    CRM_PRODUCT_86("校验不通过：产品信息不能为空！"), //
    CRM_PRODUCT_87("已经存在BBOSS成员订购产品信息,不能进行新增操作！"), //
    CRM_PRODUCT_88("营业员没有做主产品变更的权限!"), //
    CRM_PRODUCT_89("产品订购关系PRODUCT_OFFER_ID不能为空！"), //
    CRM_PRODUCT_90("用户订购的产品%s未到结束时间，不能删除!"), //
    CRM_PRODUCT_91("用户对应的主产品不是普通个人产品，无法办理产品变更业务！"), //
    CRM_PRODUCT_92("用户亲情产品编码没有传到后台,请重新选择亲情产品!"), //
    CRM_PRODUCT_93("找不到对应的产品配置参数！"), //
    CRM_PRODUCT_94("只能办理本地州或者全省发布的产品，业务无法办理!"), //
    CRM_PRODUCT_95("主产品变更时不允许操作附加产品！"), //
    CRM_PRODUCT_96("总机开户：总机没有订购任何产品！"), //
    CRM_PRODUCT_97("产品功能暂未提供！"), //
    CRM_PRODUCT_98("%s%s]未开通此产品无法进行变更操作!"), //
    CRM_PRODUCT_99("产品信息为空"), //
    CRM_PRODUCT_100("动力100产品受理：无子产品信息"), //
    CRM_PRODUCT_102("该产品无此类型业务！"), //
    CRM_PRODUCT_103("该用户产品包里未配置个人彩铃【20】服务，无法开通，请联系系统管理员！"), //
    CRM_PRODUCT_104("根据产品关系类型[%s]查询产品ID失败！"), //
    CRM_PRODUCT_105("根据订购关系编码%s获取子产品信息失败！"), //
    CRM_PRODUCT_106("获取子产品订购关系失败！"), //
    CRM_PRODUCT_107("无法获取用户的产品信息，查询条件：%s"), //
    CRM_PRODUCT_108("从用户产品表取产品生效日期失败！"), //
    CRM_PRODUCT_109("%s%s]未开通此产品无法进行恢复操作!"), //
    CRM_PRODUCT_110("该产品无法办理特殊优惠【%s】!"), //
    CRM_PRODUCT_111("该产品无法办理指定业务【%s】!"), //
    CRM_PRODUCT_112("得到主产品生效失效时间失败！"), //
    CRM_PRODUCT_113("取产品cancel_tag出错！"), //
    CRM_PRODUCT_114("用户订购的产品%s未到结束时间，不能删除!"), //
    CRM_PRODUCT_115("该手机号【%s 】的主产品不能进行回退操作！"), //
    CRM_PRODUCT_116("该产品无此类型业务！"), //
    CRM_PRODUCT_117("查询不到用户产品对应的产品包"), //
    CRM_PRODUCT_118("业务前产品依赖互斥检查 X_TRADE_DATA 不能为空!"), //
    CRM_PRODUCT_119("产品依赖互斥判断:不允许由产品[%s]变更为产品[%s]！"), //
    CRM_PRODUCT_120("%s%s]未开通此产品无法进行暂停操作!"), //
    CRM_PRODUCT_121("产品依赖互斥判断:当前订购的产品[%s|%s]和产品[%s|%s]互斥，不能同时生效，业务不能继续办理！"), //
    CRM_PRODUCT_122("产品依赖互斥判断:新增产品[%s|%s]不能生效，因为它所依赖的产品[%s|%s]不存在。业务不能继续办理！"), //
    CRM_PRODUCT_123("产品依赖互斥判断:产品[%s|%s]不能被删除，因为它被用户的另一个产品[%s|%s]所依赖。业务不能继续办理！"), //
    CRM_PRODUCT_124("产品依赖互斥判断:当前订购的产品[%s|%s]和产品[%s|%s]互斥，不能同时生效，业务不能继续办理！"), //
    CRM_PRODUCT_125("产品依赖互斥判断:修改后产品[%s|%s]不能生效，因为它所依赖的产品[%s|%s]不存在。业务不能继续办理！"), //
    CRM_PRODUCT_126("产品依赖互斥判断:产品修改标识不准确!"), //
    CRM_PRODUCT_127("产品依赖互斥判断:当前用户标识号为空！"), //
    CRM_PRODUCT_128("产品依赖互斥判断:不正确的产品元素类型标记%s"), //
    CRM_PRODUCT_129("产品依赖互斥判断：V网成员新增特殊处理: 没有获取到用户资料"), //
    CRM_PRODUCT_130("产品依赖互斥判断:依赖[AB]记不正确！[strSqlTag]"), //
    CRM_PRODUCT_131("%s%s]未开通此产品无法进行终止操作!"), //
    CRM_PRODUCT_132("产品依赖互斥判断:服务状态操作标志无效!"), //
    CRM_PRODUCT_133("根据产品ID查不到有效的产品信息"), //
    CRM_PRODUCT_134("产品配置出错！"), //
    CRM_PRODUCT_135("该产品ID【%s】没有有效的产品信息！"), //
    CRM_PRODUCT_136("该手机号码【%s】现有产品不能进行产品变更！"), //
    CRM_PRODUCT_137("该手机号码【%s】现有产品不能变更为产品【%s】"), //
    CRM_PRODUCT_138("手机号码【%s】没有能够变更的附加产品！"), //
    CRM_PRODUCT_139("手机号码【%s】不能进行变更为附加产品【%s】的操作"), //
    CRM_PRODUCT_140("不支持该产品模式的产品变更！"), //
    CRM_PRODUCT_141("%s,根据产品订购关系编码无法查到用户订购产品信息[%s]"), //
    CRM_PRODUCT_142("只能办理本地州或者全省发布的产品，业务无法办理!"), //
    CRM_PRODUCT_143("未输入要变更的主产品或与原主产品一致，无法办理！"), //
    CRM_PRODUCT_144("该手机用户的主产品下没有附加产品标识【%s】！"), //
    CRM_PRODUCT_145("无产品信息！"), //
    CRM_PRODUCT_147("根据产品获取产品关系无数据［%s］"), //
    CRM_PRODUCT_148("查询用户下月产品信息出错：无记录！"), //
    CRM_PRODUCT_149("对不起，用户预约产品不可以办理此项亲情业务。"), //
    CRM_PRODUCT_150("对不起，当前产品限制不可以办理此项亲情业务。%s"), //
    CRM_PRODUCT_151("此用户当前不是青青校园产品,不可以办理此业务！"), //
    CRM_PRODUCT_152("调用端对端校验失败反馈接口失败[%s]：%s"), //
    CRM_PRODUCT_153("此用户当前不是快乐空间产品,不可以办理此业务！"), //
    CRM_PRODUCT_154("没找到相应亲情产品的关系及优惠信息，请验证！"), //
    CRM_PRODUCT_155("获取产品生效标志失败%s"), //
    CRM_PRODUCT_156("获取优惠生效时间出错：无产品信息TD_B_PRODUCT-%s"), //
    CRM_PRODUCT_157("查询用户下月产品信息出错：无记录！"), //
    CRM_PRODUCT_158("请输入产品编码"), //
    CRM_PRODUCT_159("该号码%s已受产品限制，不可以再加入亲情集团！"), //
    CRM_PRODUCT_160("亲情副卡产品不正确"), //
    CRM_PRODUCT_161("服务校验，获取产品对应服务无数据！"), //
    CRM_PRODUCT_162("请先选择IP直通车产品！"), //
    CRM_PRODUCT_163("根据编码[%s]，查找产品信息不存在！"), //
    CRM_PRODUCT_164("获取产品服务无数据！"), //
    CRM_PRODUCT_165("该产品【%s】在td_b_product没记录"), //
    CRM_PRODUCT_166("产品费用表没有配置国际漫游押金费用记录！"), //
    CRM_PRODUCT_167("获取兑换的移动自有产品无数据!"), //
    CRM_PRODUCT_168("输入原因与要求的产品不符！[%s|%s]"), //
    CRM_PRODUCT_169("当前产品不是组合产品,不能办理此业务!"), //
    CRM_PRODUCT_170("当前用户家庭副卡，不能进行组合产品销户!"), //
    CRM_PRODUCT_171("产品信息没有找到"), //
    CRM_PRODUCT_172("当前产品不是组合产品,不能办理此业务!"), //
    CRM_PRODUCT_173("没有查到该产品的关系信息"), //
    CRM_PRODUCT_174("根据产品编码[%s]，查找产品不存在！"), //
    CRM_PRODUCT_175("获取产品信息失败"), //
    CRM_PRODUCT_176("业务前特殊限制表判断-TD家庭产品存在亲情关系不能办理此业务！"), //
    CRM_PRODUCT_177("对不起，您不能办理此亲情产品！"), //
    CRM_PRODUCT_178("您设置的亲情产品中没有您选择的主卡优惠！"), //
    CRM_PRODUCT_179("对不起，该产品不可以办理此亲情业务或者该产品暂时还未配置优惠信息"), //
    CRM_PRODUCT_180("查询家庭产品失败：无产品资料！"), //
    CRM_PRODUCT_181("查询关系类型出错，请检查是否配置了产品关系表"), //
    CRM_PRODUCT_182("对不起，未获取到亲情产品信息，请核对是否选择了亲情产品！"), //
    CRM_PRODUCT_183("该产品暂不支持信控或信控编码未配置！"), //
    CRM_PRODUCT_184("该产品无此类型业务！"), //
    CRM_PRODUCT_185("获取套餐信息异常！"), //
    CRM_PRODUCT_186("获取PRODUCT_NAME异常！"), //
    CRM_PRODUCT_187("更新用户产品信息：没有找到产品信息！"), //
    CRM_PRODUCT_188("更新用户产品信息：没有找到用户资料信息！"), //
    CRM_PRODUCT_189("%s无该集团产品【%s】的权限，不能注销该集团用户！"), //
    CRM_PRODUCT_190("短号校验%s"), //
    CRM_PRODUCT_191("根据产品包ID【%s】获取产品信息无数据！"), //
    CRM_PRODUCT_192("根据产品标识【%s】获取必选服务无数据！"), //
    CRM_PRODUCT_193("当前产品下服务【%s】对应的参数【%s】无参数值！"), //
    CRM_PRODUCT_194("当前产品下优惠【%s】对应的参数【%s】无参数值！"), //
    CRM_PRODUCT_195("您已经有预约生效的主产品，不能再办理主产品的变更 "), //
    CRM_PRODUCT_196("产品不是亲情通产品，不能办理情通取消业务！"), //
    CRM_PRODUCT_197("根据PRODUCT_OFFER_ID=【%s】productId=【%s】获取产品跟商品用户关系失败！"), //
    CRM_PRODUCT_198("获取商品用户信息失败！"), //
    CRM_PRODUCT_199("落地容错配置【%s】没有配置！"), //
    CRM_PRODUCT_200("根据MP_GROUP_CUST_CODE没查询到集团资料，请确认已经将该集团资料同步"), //
    CRM_PRODUCT_201("其他错误，商品资费计划标识跟商品级资费操作代码长度不一致"), //
    CRM_PRODUCT_202("其他错误,产品属性名、产品属性值、产品属性代码数目不匹配"), //
    CRM_PRODUCT_203("其他错误,一次性费用名称、一次性费用不匹配"), //
    CRM_PRODUCT_204("产品级资费操作代码错误"), //
    CRM_PRODUCT_205("其他错误，产品资费计划标识跟产品级资费操作代码长度不一致"), //
    CRM_PRODUCT_206("其他错误，产品跟产品级资费操作代码长度不一致"), //
    CRM_PRODUCT_207("其他错误，产品跟产品订单号长度不一致"), //
    CRM_PRODUCT_208("根据PRODUCT_ID【%s】，批量配置信息不存在或者该产品无集团成员产品！"), //
    CRM_PRODUCT_209("根据商品订单编号没有找到对应的商品订购信息"), //
    CRM_PRODUCT_210("其他错误,当前状态负责人电话、姓名、所在单位及部门数目不匹配"), //
    CRM_PRODUCT_211("其他错误,产品客户经理类型、姓名、电话数目不匹配"), //
    CRM_PRODUCT_212("其他错误,产品受理涉及的客户类型、客户编码数目不匹配"), //
    CRM_PRODUCT_213("其他错误,产品落实侧、当前状态处理说明数目不匹配"), //
    CRM_PRODUCT_214("取消商品资费时，根据商品元素ID找不到对应的元素信息"), //
    CRM_PRODUCT_215("根据产品用户，查询不到商品、产品的用户关系！"), //
    CRM_PRODUCT_216("根据产品编码[%s]查找产品品牌信息不存在!"), //
    CRM_PRODUCT_217("产品编码[%s]暂不支持对应的产品信息查询!"), //
    CRM_PRODUCT_218("产品编码[%s]没有配置预受理操作!"), //
    CRM_PRODUCT_219("产品缺少默认的漫游级别!"), //
    CRM_PRODUCT_220("根据机型和合约ID查不到合约信息"), //
    CRM_PRODUCT_221("产品参数[VPMN编码]不能为空!"), //
    CRM_PRODUCT_222("该用户已有预约主产品，不能再办理主产品变更"), //
    CRM_PRODUCT_223("没有配置IMS自选群套餐优惠!"), //
    CRM_PRODUCT_224("携入地主产品与携出地主产品一致，无法办理！"), //
    CRM_PRODUCT_225("产品[%s]费用未配置！"), //
    CRM_PRODUCT_226("请上传合同附件！"), //
    CRM_PRODUCT_227("不允许从界面办理退订该业务！"), //
    CRM_PRODUCT_228("您以前使用过【99166969 手机电视全网套餐】平台业务，不属于本次免费体验活动范围！"), //
    CRM_PRODUCT_229("未激活用户，不能参加订购【%s】【%s】优惠活动！"), //
    CRM_PRODUCT_230("您在最近三个月内使用过【%s】【%s】业务，不属于本次免费体验活动范围!"), //
    CRM_PRODUCT_231("您是咪咕特级会员的客户,不能办理优惠套餐【%s】!"), //
    CRM_PRODUCT_232("您以前使用过【%s】,不属于本次免费体验活动范围!"), //
    CRM_PRODUCT_233("需要先订购主产品[%s]才能订购产品[%s]!产品[%s]跟产品[%s]也不可以在一笔订单中同时订购!"), //
    CRM_PRODUCT_234("需要先取消产品[%s]才能取消产品[%s]!也可以和产品[%s]同时取消!"), //
    CRM_PRODUCT_235("产品[%s]和产品[%s]是互斥关系不能同时订购!"), //
    CRM_PRODUCT_236("需要和主产品[%s]同时订购,才能订购产品[%s]!"), //
    CRM_PRODUCT_237("需要先暂停附产品[%s]才能暂停主产品[%s]!也可以和产品[%s]同时暂停!"), //
    CRM_PRODUCT_238("需要先恢复主产品[%s]才能恢复附产品[%s]!也可以和产品[%s]同时恢复!"), //
    CRM_PRODUCT_239("根据产品[%s]元素类型[%s]元素编码[%s]找不到对应的包信息"), //
    CRM_PRODUCT_240("产品编码[%s]格式错误!"), //
    CRM_PRODUCT_241("该产品ID【%s】，父产品类型【%s】没有查询有效的产品类型信息！"), //
    CRM_PRODUCT_242("您当前预约变更了服务【%s】，预约业务只能预约产品变更和优惠变更，不能预约服务变更!"), //
    CRM_PRODUCT_243("您已经有预约生效的主产品，不能再办理预约业务!"), //
    CRM_PRODUCT_244("用户不是【和4G活动】用户!"), //
    CRM_PRODUCT_245("用户不是4G用户!"), //
    CRM_PRODUCT_246("用户对应的主产品不是普通个人产品，无法办理产品变更业务!"), //
    CRM_PRODUCT_500("获取用户凭证信息失败!"), //
    CRM_PRODUCT_501("用户产品变更失败！"), //
    CRM_PRODUCT_502("产品【%s】的格式不正确,必须为数字或者单个逗号分割的数字！"), //
    CRM_PRODUCT_503("该优惠ID【%s】没有有效的优惠信息！！"), //
    CRM_PRODUCT_504("根据产品ID[%s]获取关系类型编码出错!"), //
    CRM_PRODUCT_505("TD_B_ATTR_BIZ表没有配置该业务类型,业务不能办理!"), //
    CRM_PRODUCT_506("未找到BBOSS成员订购产品信息,不能进行流量叠加包订购操作！"), //
    CRM_PRODUCT_507("BBOSS下发失败通知产品订单号为空！"),
    CRM_PRODUCT_508("赠送有效期不能从有限期改成无限期！"),
    CRM_PRODUCT_509("该用户主优惠(动感地带校园音乐套餐)生效时间【%s】小于产品(10001139)生效时间【%s】,请关闭页面或者更换浏览器重试!"),
    CRM_PRODUCT_512("您的话费实时余额需大于[%s]元以上才能开通国际长途、漫游业务，开通国漫业务的预存款费用在业务关闭后不能进行清退。"),
    CRM_PRODUCT_521("用户已经办理过优惠%s"),
    CRM_PRODUCT_522("%s"),
    CRM_PRODUCT_523("假日优惠commpara配置出现错误"),
    CRM_PRODUCT_524("您在本月未使用过【%s】【%s】业务，不属于本次免费体验活动范围!"),
    CRM_PRODUCT_525("您已预约生效的主产品不是该营销包指定的主产品，不能办理该营销活动!");
    
    private final String value;

    private ProductException(String value)
    {

        this.value = value;
    }

    public String getValue()
    {

        return value;
    }
}
