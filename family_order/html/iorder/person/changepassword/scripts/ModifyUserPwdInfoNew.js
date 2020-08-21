window.onload = function () {
    $("#PasswordSetPart").addClass("e_dis");
    $("#LittleKeyBtn").addClass("e_dis");
    $("#PasswordSetPart").attr("disabled",true);
    $("#LittleKeyBtn").attr("disabled",true);
}
function refreshPartAtferAuth(data) {
	$("#chPWDInfoPart").css("display", "");
    $("#PasswordSetPart").removeClass("e_dis");
    $("#LittleKeyBtn").removeClass("e_dis");
    $("#PasswordSetPart").attr("disabled",false);
    $("#LittleKeyBtn").attr("disabled",false);
	$("#REMARK").attr("disabled", false);
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	
	if (tradeTypeCode == '71' || tradeTypeCode == '3810') { // 71用户密码变更，3810无线固话用户密码变更
		initModifyUserPswInfo();
	} else if (tradeTypeCode == '73') { // 密码重置（随机密码）
		initResetUserPwdInfo();
	    /**
	     * REQ201705270006_关于人像比对业务优化需求
	     * @author zhuoyingzhi
	     * @date 20170630
	     */
		// 效验方式
		var checkMode = data.get("CHECK_MODE");
		$("#AUTH_CHECK_MODE").val(checkMode);
		var custName = data.get("CUST_INFO").get("CUST_NAME");
		$("#UCA_CUST_NAME").val(custName);
		//客户证件号码
		$("#UCA_PSPT_ID").val(data.get("CUST_INFO").get("PSPT_ID"));
		//效验用户是否为携入号码
		var param = "&SERIAL_NUMBER=" + $("#AUTH_SERIAL_NUMBER").val()
				+ "&EPARCHY_CODE=0898" + "&TRADE_TYPE_CODE=73";
		ajaxSubmit(null, 'checkNpQryOrWX', param, '',
 				function(data) {
					//把携入标识  传给界面
					$("#NPTag").val(data.get("NPTag"));
					//无线
					$("#WXTag").val(data.get("WXTag"));
					/**
					 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
					 * @author zhuoyingzhi
					 * @date 20170805
					 */
					var isAgentRight = data.get("isAgentRight");
					if (isAgentRight == "0") {
						//有权限
						$("#IDENTITY_AUTHENTICATION").attr("disabled", false);
					} else if (isAgentRight == "1") {
						// 无权限
						$("#IDENTITY_AUTHENTICATION").attr("disabled", true);
					}
			        /**
			         * 手动输入身份证号码权限判断
			         * @author 卓英智
			         * @date 20171020
			         */
					$("#highprivRight").val(data.get("highprivRight"));
					$("#LOAD_CUST_NAME").val(data.get("LOAD_CUST_NAME"));
					$("#LOAD_PSPT_ID").val(data.get("LOAD_PSPT_ID"));
					$("#LOAD_PSPT_TYPE_CODE").val(data.get("LOAD_PSPT_TYPE_CODE"));
					/*************REQ201707060009关于补卡、密码重置、复机业务优化的需求******end******************/
					if (data.get("BRAND_CODE") == 'PWLW') {
						$("#is_PWLW").val('1');
					} else {
						$("#is_PWLW").val('0');
					}
				  	$.endPageLoading();
				}, function(code, info, detail) {
					$.endPageLoading();
					MessageBox.error("错误提示", info);
				}, function() {
					$.endPageLoading();
					MessageBox.alert("告警提示", "查询超时!", function(btn) {
					});
				});
	}
	$.cssubmit.disabledSubmitBtn(true);
}

function initResetUserPwdInfo() {
	$.cssubmit.disabledSubmitBtn(true);
	$("#pwdInfo_PASSWD_TYPE").attr("disabled", null);
}

function initModifyUserPswInfo() {// 密码修改页面初始化方法
	$("#pwdInfo_PASSWD_TYPE").attr("disabled", null);
	var oldPasswd = $.auth.getAuthData().get('USER_INFO').get('USER_PASSWD');// 原密码
	if (oldPasswd != "") { // 如果有原密码 则默认选择 修改密码
		showMe("1");
		$("#pwdInfo_PASSWD_TYPE").val('1');
	} else { // 如果没有原密码 则默认选择 新增密码
		showMe("2");
		$("#pwdInfo_PASSWD_TYPE").val('2');
	}
}

// page值说明 :1=修改密码 2=新增密码 3=随机密码 4=取消密码 (5=重置密码 HXYD-YZ-REQ)
function showMe(page) {
	$("#RandomPWD").css("display", "none");
	var ispwlw = $("#is_PWLW").val();
	if (page == 3) {
		if (ispwlw == '1') {
			$.MessageBox.error('0', '物联网号码禁用随机密码方式');
			$("#pwdInfo_PASSWD_TYPE").val('1');
			return;
		}
	}
	$.password._clearPassword();
	$("#NEW_PASSWD").val('');// 清空新密码
	if (page == 3 || page == 4) // 选择取消和随机密码 则不能点击设置密码按钮
	{
	    $.cssubmit.disabledSubmitBtn(false);
		$("#chPWDInfoPart").css("display", "none");
        $("#PasswordSetPart").addClass("e_dis");
        $("#LittleKeyBtn").addClass("e_dis");
        $("#PasswordSetPart").attr("disabled",true);
        $("#LittleKeyBtn").attr("disabled",true);
		
		if (page == 3) { // 随机密码，界面互联网新样式-在密码设置处新增发送随机密码功能
			$("#RandomPWD").css("display", "");
		}
	} else {// 选择修改和新增密码 则能点击设置密码按钮
		$("#chPWDInfoPart").css("display", "");
        $("#PasswordSetPart").removeClass("e_dis");
        $("#LittleKeyBtn").removeClass("e_dis");
        $("#PasswordSetPart").attr("disabled",false);
        $("#LittleKeyBtn").attr("disabled",false);
	}
    
	if ($.auth.getAuthData() != null) {
	    var oldPasswd = $.auth.getAuthData().get('USER_INFO').get('USER_PASSWD');
		if (oldPasswd != "") {// 说明有原密码
			if (page == 2) { // 原密码存在 不能选择新增
				MessageBox.alert("告警提示", "用户密码已存在,请选择修改密码或取消密码！");
				$("#pwdInfo_PASSWD_TYPE").val('1');
				return;
			}
		} else {
			if (page == 1 || page == 4 || page == 5) { // 原密码不存在 只能选择新增 2和随机 3 modify by songzy@20110121
				MessageBox.alert("告警提示", "用户密码不存在,请选择新增密码或随机密码！");
				$("#pwdInfo_PASSWD_TYPE").val('2');
				$("#setPWDIV").removeClass("e_dis");
				$("#chPWDInfoPart").css("display", "");
                $("#PasswordSetPart").removeClass("e_dis");
                $("#LittleKeyBtn").removeClass("e_dis");
                $("#PasswordSetPart").attr("disabled",false);
                $("#LittleKeyBtn").attr("disabled",false);
				return;
			}
		}
	}
}

// 点击“密码设置”按钮组件时执行
function beforeEvent() {
	$("#NEW_PASSWD").val('');// 清空新密码
	var t_psptId = $.auth.getAuthData().get('CUST_INFO').get('ORIGIN_PSPT_ID');
	var t_serialNumber = $.auth.getAuthData().get('USER_INFO').get('SERIAL_NUMBER');
	var t_userId = $.auth.getAuthData().get('USER_INFO').get('USER_ID');
	
	$.password.setPasswordAttr({
		psptId : t_psptId,
		serialNumber : t_serialNumber,
		userId : t_userId
	});
	return true;
}

// 密码设置完成后的回调方法
function afterEvent(data) {
	$("#NEW_PASSWD").val(data.get("NEW_PASSWORD"));
}

// 提交校验
function checkBeforeSubmit() {
	/**
	 * REQ201705270006_关于人像比对业务优化需求
	 * @author zhuoyingzhi
	 * @date 20170630
	 */
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	if (tradeTypeCode == "73") {
		//用户密码重置
//		var authCheckPsptTypeCode=$("#AUTH_CHECK_PSPT_TYPE_CODE").val();
//
//		var authCheckPsptId=$("#AUTH_CHECK_PSPT_ID").val();

		//获取验证方式
		var authCheckMode = $("#AUTH_CHECK_MODE").val();
		/**
		 * 客户摄像添加 扫描
		 * <br/>
		 * 修改获取客户信息方式,通过扫描方式
		 * @author zhuoyingzhi
		 * @date 20170906
		 */
		var authCheckPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();

		var authCheckPsptId = $("#custInfo_PSPT_ID").val();


		// 携入标识 1是携入 非1不是
		var npTag = $("#NPTag").val();

		// 固话标识 1是固话 非1 不是固话
		var wxTag = $("#WXTag").val();

		if (npTag == 1 || wxTag == 1) {
			// 携入号码或者固话号码
			// 提交的时候不处理
		} else {
			var cmpTag = "1";

            $.ajax.submit(null, "isCmpPic", null, null,
                function (ajaxData) {
                    var flag = ajaxData.get("CMPTAG");
                    if (flag === "0") {
                        cmpTag = "0";
                    }
                    $.endPageLoading();
                },
                function (error_code, error_info) {
                    $.MessageBox.error(error_code, error_info);
                    $.endPageLoading();
                }, {
                    "async": false
                });

			if (cmpTag == "0") {
				// 经办人证件类型
				var agentPsptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();

				if (authCheckPsptTypeCode == "" && agentPsptTypeCode == "") {
					MessageBox.error("告警提示", "证件类型为必选择", null, null, null, null);
					return false;
				}
				if (authCheckPsptTypeCode == "0" || authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "2" || authCheckPsptTypeCode == "3") {
					var ucaCustName = $("#UCA_CUST_NAME").val();
					var ucaPsptId = $("#UCA_PSPT_ID").val();

					// 输入的客户信息
					var custInfoCustName = $("#custInfo_CUST_NAME").val();

					var loadCustName = $("#LOAD_CUST_NAME").val();
					var loadPsptId = $("#LOAD_PSPT_ID").val();
					var loadPsptTypeCode = $("#LOAD_PSPT_TYPE_CODE").val();
					if (custInfoCustName != loadCustName || authCheckPsptId != loadPsptId || authCheckPsptTypeCode != loadPsptTypeCode) {
						MessageBox.error("告警提示", "输入的信息与原客户信息不一致", null, null, null, null);
						return false;
					}
				}
				if ((authCheckPsptTypeCode == "0" || authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "2" || authCheckPsptTypeCode == "3" || agentPsptTypeCode == "0"
					|| agentPsptTypeCode == "1" || agentPsptTypeCode == "2" || agentPsptTypeCode == "3")&& authCheckMode == "0" ) {
					//(本地身份证或外地身份证)和效验方式为客户证件（客户证件：0）     用户密码重置
					//必须进行人像比对 并且通过后才能办理业务
					var picid = $("#custInfo_PIC_ID").val();

					//经办人摄像标志
					var agentpicid = $("#custInfo_AGENT_PIC_ID").val();

					if (null != picid && picid == "ERROR"){
						// 客户摄像失败
                        MessageBox.error("告警提示", "客户" + $("#custInfo_PIC_STREAM").val(), null, null, null, null);
                        return false;
					}else if ("" == picid){// 客户未摄像
                        // 客户或经办人人像比对成功即可
						if (null != agentpicid && agentpicid == "ERROR"){// 经办人摄像失败
                            MessageBox.error("告警提示", "经办人" + $("#custInfo_PIC_STREAM").val(), null, null, null, null);
                            return false;
						}else if("" == agentpicid){// 经办人未摄像
                            // 客户或经办人未进行摄像
                            MessageBox.error("告警提示", "请进行客户或经办人摄像!", null, null, null, null);
                            return false;
						}
					}

					/**
					 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
					 * @author zhuoyingzhi
					 * @date 20170805
					 */
					if (null != agentpicid && agentpicid == "ERROR") {
						// 经办人摄像失败
						MessageBox.error("告警提示", "经办人" + $("#custInfo_AGENT_PIC_STREAM").val(), null, null, null, null);
						return false;
					}
				}
			}
		}
	}

	var pwdType = $("#pwdInfo_PASSWD_TYPE").val();// 1=修改密码 2=新增密码 3=随机密码 4=取消密码
        // $.beginPageLoading("业务受理中。。。");
    // setTimeout(function() {
        $.endPageLoading();
        $("#pass_CONFIRM_PASSWD").trigger("blur");
		if (pwdType == 1 || pwdType == 2) {
			var newPwd = $("#NEW_PASSWD").val();
			if (newPwd == "") {
				// MessageBox.alert("告警提示", "请设置并确认新服务密码！");
				return false;
			} else {
				$.getScript("scripts/csserv/common/des/des.js", function() {
					var data = newPwd;
					var firstKey = "c";
					var secondKey = "x"
					var thirdKey = "y"
					$("#NEW_PASSWD").val(strEnc(data, firstKey, secondKey, thirdKey) + "xxyy");
				});
			}
		}
		var param = "&PASSWD_TYPE=" + pwdType + "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
		$.cssubmit.addParam(param);
		// $.cssubmit.submitTrade();
    return true;
	// }, 500);
}

//提交校验
function checkBeforeSubmit2() {
	/**
	 * REQ201705270006_关于人像比对业务优化需求
	 * @author zhuoyingzhi
	 * @date 20170630
	 */
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
	if (tradeTypeCode == "73") {
		//用户密码重置
//		var authCheckPsptTypeCode=$("#AUTH_CHECK_PSPT_TYPE_CODE").val();
//
//		var authCheckPsptId=$("#AUTH_CHECK_PSPT_ID").val();

		//获取验证方式
		var authCheckMode = $("#AUTH_CHECK_MODE").val();
		/**
		 * 客户摄像添加 扫描
		 * <br/>
		 * 修改获取客户信息方式,通过扫描方式
		 * @author zhuoyingzhi
		 * @date 20170906
		 */
		var authCheckPsptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();

		var authCheckPsptId = $("#custInfo_PSPT_ID").val();


		// 携入标识 1是携入 非1不是
		var npTag = $("#NPTag").val();

		// 固话标识 1是固话 非1 不是固话
		var wxTag = $("#WXTag").val();

		if (npTag == 1 || wxTag == 1) {
			// 携入号码或者固话号码
			// 提交的时候不处理
		} else {
			var cmpTag = "1";
			ajaxSubmit(null, 'isCmpPic', '', '', function(data) {
				var flag = data.get("CMPTAG");
				if (flag == "0") {
					cmpTag = "0";
				}
				$.endPageLoading();
			}, function(code, info, detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", info);
			}, function() {
				$.endPageLoading();
				MessageBox.alert("告警提示", "人像比对超时!", function(btn) {
				});
			}, {
				"async" : false
			});

			if (cmpTag == "0") {
				// 经办人证件类型
				var agentPsptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();

				if (authCheckPsptTypeCode == "" && agentPsptTypeCode == "") {
					MessageBox.error("告警提示", "证件类型为必选择", null, null, null, null);
					return false;
				}
				if (authCheckPsptTypeCode == "0" || authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "2") {
					var ucaCustName = $("#UCA_CUST_NAME").val();
					var ucaPsptId = $("#UCA_PSPT_ID").val();

					// 输入的客户信息
					var custInfoCustName = $("#custInfo_CUST_NAME").val();

					var loadCustName = $("#LOAD_CUST_NAME").val();
					var loadPsptId = $("#LOAD_PSPT_ID").val();
					var loadPsptTypeCode = $("#LOAD_PSPT_TYPE_CODE").val();
					if (custInfoCustName != loadCustName || authCheckPsptId != loadPsptId || authCheckPsptTypeCode != loadPsptTypeCode) {
						MessageBox.error("告警提示", "输入的信息与原客户信息不一致", null, null, null, null);
						return false;
					}
				}
				if ((authCheckPsptTypeCode == "0" || authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "2" || agentPsptTypeCode == "0"
					|| agentPsptTypeCode == "1" || agentPsptTypeCode == "2")&& authCheckMode == "0" ) {
					//(本地身份证或外地身份证)和效验方式为客户证件（客户证件：0）     用户密码重置
					//必须进行人像比对 并且通过后才能办理业务
					var picid = $("#custInfo_PIC_ID").val();

					//经办人摄像标志
					var agentpicid = $("#custInfo_AGENT_PIC_ID").val();


					if (picid == "" && agentpicid == "") {
						// 客户或经办人未进行摄像
						MessageBox.error("告警提示", "请进行客户或经办人摄像!", null, null, null, null);
						return false;
					}


					if (null != picid && picid == "ERROR") {
						// 客户摄像失败
						MessageBox.error("告警提示", "客户" + $("#custInfo_PIC_STREAM").val(), null, null, null, null);
						return false;
					}

					/**
					 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
					 * @author zhuoyingzhi
					 * @date 20170805
					 */
					if (null != agentpicid && agentpicid == "ERROR") {
						// 经办人摄像失败
						MessageBox.error("告警提示", "经办人" + $("#custInfo_AGENT_PIC_STREAM").val(), null, null, null, null);
						return false;
					}
				}
			}
		}
	}

	var pwdType = $("#pwdInfo_PASSWD_TYPE").val();// 1=修改密码 2=新增密码 3=随机密码 4=取消密码
        // $.beginPageLoading("业务受理中。。。");
    // setTimeout(function() {
        $.endPageLoading();
        $("#pass_CONFIRM_PASSWD").trigger("blur");
		if (pwdType == 1 || pwdType == 2) {
			var newPwd = $("#NEW_PASSWD").val();
			if (newPwd == "") {
				//MessageBox.alert("告警提示", "请设置并确认新服务密码！");
				return false;
			} else {
				$.getScript("scripts/csserv/common/des/des.js", function() {
					var data = newPwd;
					var firstKey = "c";
					var secondKey = "x"
					var thirdKey = "y"
					$("#NEW_PASSWD").val(strEnc(data, firstKey, secondKey, thirdKey) + "xxyy");
				});
			}
		}
		var param = "&PASSWD_TYPE=" + pwdType + "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
		$.cssubmit.addParam(param);
		// $.cssubmit.submitTrade();
    return true;
	// }, 500);
}

/**
 * 人像比对(按钮)
 * @param picid
 * @param picstream
 * @returns {Boolean}
 * @author zhuoyingzhi
 * @date 20170626
 */
function identification(picid, picstream) {

	var custName, psptId, psptType, fornt, ucaCustName;

	if (picid == "custInfo_PIC_ID") {
		//客户
/*		//效验  客户名称(身份证中的客户名称)
		custName = $("#AUTH_CHECK_CUSTINFO_CUST_NAME").val();
		
		//uca中的客户名称
		ucaCustName = $("#UCA_CUST_NAME").val();
		
		//效验 身份证号码
		psptId = $("#AUTH_CHECK_PSPT_ID").val();
					
		//效验  身份证类型
		psptType = $("#AUTH_CHECK_PSPT_TYPE_CODE").val();
		
		//如果是手动输入证件号码,无法获取证件上的客户姓名
		if(custName == ""){
			custName=ucaCustName;
		}*/
		//身份证正面
		fornt = $("#custInfo_FRONTBASE64").val();
		/**
		 * 修改获取客户信息方式，通过扫描方式
		 * @author zhuoyingzhi
		 * @date 20170906
		 */
		psptType = $("#custInfo_PSPT_TYPE_CODE").val();

		psptId = $("#custInfo_PSPT_ID").val();

		custName = $("#custInfo_CUST_NAME").val();
	} else {
		// 经办人
		custName = $("#custInfo_AGENT_CUST_NAME").val();
		psptId = $("#custInfo_AGENT_PSPT_ID").val();
		psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		fornt = $("#custInfo_AGENT_FRONTBASE64").val();
	}	

//	psptId="460006198902173112";
//	psptType="0";
//	custName="卓英智";

	//携入标识
	var npTag = $("#NPTag").val();

	// 固话标识 1是固话 非1 不是固话
	var wxTag = $("#WXTag").val();
	
	if (npTag == 1 || wxTag == 1) {
		MessageBox.success("提示", "携入客户及固话暂不提供人像比对服务", null, null, null);
		return false;
	}
	

	if (psptId == "" || psptType == "" || custName == "") {
		if (picid == "custInfo_PIC_ID") {
			MessageBox.alert("告警提示", "请扫描或输入客户名称、证件类型和证件号码！");
			return false;
		} else {
			MessageBox.alert("告警提示", "请扫描或输入经办人名称、经办人证件类型和经办人证件号码！");
			return false;
		}
	}

    if ($.os.phone) { // 手机移动端人像比对
        $.MBOP.shotImage(picid + "_73"); // 73：用户密码重置
        return true;
    }
	
	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>'+'</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+$("#AUTH_SERIAL_NUMBER").val()+'</phone>');
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>'+'</op_time>');
	
	bossOriginalXml.push('	<busi_list>');
	bossOriginalXml.push('		<busi_info>');
	bossOriginalXml.push('			<op_code>'+'</op_code>');
	bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
	bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
	bossOriginalXml.push('		</busi_info>');
	bossOriginalXml.push('	</busi_list>');

	bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
	bossOriginalXml.push('	<id_card>'+'</id_card>');
	bossOriginalXml.push('	<cust_name>'+'</cust_name>');
	bossOriginalXml.push('	<copy_flag></copy_flag>');
	bossOriginalXml.push('	<agm_flag></agm_flag>');
	bossOriginalXml.push('</req>');
	
	var bossOriginaStr = bossOriginalXml.join("");
	// 调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	// 获取保存结果
	var result = makeActiveX.IdentificationInfo.result;
	// 获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	
	if (picID != '') {
		MessageBox.success("成功提示", "人像摄像成功", function (btn) {
            if ("ok" === btn) {
                //获取照片流
                var picStream = makeActiveX.IdentificationInfo.pic_stream;
                picStream = escape(encodeURIComponent(picStream));
                if ("0" == result) {
                    $("#" + picid).val(picID);
                    $("#" + picstream).val(picStream);
                    var param = "&BLACK_TRADE_TYPE=73";
                    param += "&CERT_ID=" + psptId;
                    param += "&CERT_NAME=" + custName;
                    param += "&CERT_TYPE=" + psptType;
                    param += "&PIC_STREAM=" + picStream + "&FRONTBASE64=" + escape(encodeURIComponent(fornt));
                    param += "&SERIAL_NUMBER=" + $.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");


                    $.beginPageLoading("正在进行人像比对。。。");
                    ajaxSubmit(null, 'cmpPicInfo', param, '', function(data) {
                        $.endPageLoading();
                        if (data && data.get("X_RESULTCODE") == "0") {
                            MessageBox.success("成功提示", "人像比对成功", null, null, null);
                            return true;
                        } else if (data && data.get("X_RESULTCODE") == "1") {
                            $("#" + picid).val("ERROR");
                            $("#" + picstream).val(data.get("X_RESULTINFO"));
                            MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                            return false;
                        }
                    }, function(code, info, detail) {
                        $("#" + picid).val("ERROR");
                        $("#" + picstream).val("人像比对失败,请重新拍摄");
                        $.endPageLoading();
                        // showDetailErrorInfo(error_code,error_info,derror);
                        MessageBox.error("告警提示", "人像比对失败,请重新拍摄", null, null, null, null);
                    });
                }else{
                    MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
                }
            }
        });
	} else {
		MessageBox.error("错误提示", "人像摄像失败");
		return false;
	}	

}

/**
 * 选择客户摄像/经办人摄像方式
 * @param obj
 */
function changeMethod(authentication) {
	if (authentication == "1") {
		changeMethod1();
	} else if (authentication == "2") {
		changeMethod2();
	}
}

/**
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 客户摄像
 * @author zhuoyigzhi
 * @date 20170801
 */
function changeMethod1() {
	$(".span_CUST").css("display", "");
	// 经办人隐藏
	$(".span_AGENT").css("display", "none");

	// 经办人名称
	$("#custInfo_AGENT_CUST_NAME").val("");
	// 经办人证件号码
	$("#custInfo_AGENT_PSPT_ID").val("");
	// 经办人证件类型
	$("#custInfo_AGENT_PSPT_TYPE_CODE").val("");

	// 经办人照片id
	$("#custInfo_AGENT_PIC_ID").val("");

	// 拍摄经办人人像照片流
	$("#custInfo_AGENT_PIC_STREAM").val("");
	// 经办人身份证反面照
	$("#custInfo_AGENT_BACKBASE64").val("");
	// 经办人身份证正面照
	$("#custInfo_AGENT_FRONTBASE64").val("");
}

/**
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 经办人摄像
 * @author zhuoyigzhi
 * @date 20170802
 */
function changeMethod2() {
	// 隐藏客户摄像
	$(".span_CUST").css("display", "none");
	// 经办人 显示
	$(".span_AGENT").css("display", "");

	// 客户摄像id
	$("#custInfo_PIC_ID").val("");
	// 拍摄人像照片流
	$("#custInfo_PIC_STREAM").val("");

	// 客户证件类型
	$("#custInfo_PSPT_TYPE_CODE").val("");
	// 客户证件号码
	$("#custInfo_PSPT_ID").val("");
	// 客户名称
	$("#custInfo_CUST_NAME").val("");
}

/**
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * @author zhuoyingzhi
 * @date 20170802
 * 扫描读取经办人身份证信息
 */
function clickScanPspt2() {
	var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
	var needpicinfo = null;
	var tag = (psptTypeCode == "E" || psptTypeCode == "G"
			|| psptTypeCode == "D" || psptTypeCode == "M" || psptTypeCode == "L") ? true
			: false;
	if (tag) {
		// 客户证件类型为单位证件
		needpicinfo = "PIC_INFO";
	}
	getMsgByEForm("custInfo_AGENT_PSPT_ID", "custInfo_AGENT_CUST_NAME", needpicinfo, null, null, null, null, null);
}

/**
 * 客户扫描读取身份证信息
 * @author zhuoyingzhi
 * @date 20170906
 */
function clickScanPspt() {
	if ($.os.phone) {
		 $.MBOP.getMsgByEForm("custInfo_PSPT_ID"); 
		} else{
			getMsgByEForm("custInfo_PSPT_ID", "custInfo_CUST_NAME", null, null, null,
					"custInfo_PSPT_ADDR,custInfo_POST_ADDRESS", null,
					"custInfo_PSPT_END_DATE");
		}
	
	this.verifyIdCardName(fieldName);
}

/**
 * 添加手动输入身份证号码权限判断
 * @author zhuoyingzhi
 * @date 20171020
 * @param tag
 */
function changePsptTypeCode(tag) {
	// 获取权限
	var highprivRight = $("#highprivRight").val();

	if (tag == "PSPT_TYPE_CODE") {
		// 客户
		var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
		if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" ||  psptTypeCode == "3") {
			// 显示扫描和摄像按钮
			$("#ShotImgButton").css("display", "block");
			$("#ReadCardButton").css("display", "block");
			if (highprivRight == 1) {
				// 有权限,并且证件类型为：本地身份证、外地身份证或户口本 ,则证件信息可以手动输入
				$("#custInfo_CUST_NAME").attr("disabled", false);
				$("#custInfo_PSPT_ID").attr("disabled", false);
			} else {
				// 无限,不能手动输入证件信息
				$("#custInfo_CUST_NAME").attr("disabled", true);
				$("#custInfo_PSPT_ID").attr("disabled", true);
			}
		} else {
			// 隐藏扫描和摄像按钮
			$("#ShotImgButton").css("display", "none");
			$("#ReadCardButton").css("display", "none");

			// 能手动输入证件信息
			$("#custInfo_CUST_NAME").attr("disabled", false);
			$("#custInfo_PSPT_ID").attr("disabled", false);
		}
		// 客户证件号码
		$("#custInfo_PSPT_ID").val("");
		// 客户名称
		$("#custInfo_CUST_NAME").val("");
	} else if (tag == "AGENT_PSPT_TYPE_CODE") {
		// 经办人
		var agentPsptTypeCode = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		if (agentPsptTypeCode == "0" || agentPsptTypeCode == "1" || agentPsptTypeCode == "2" || agentPsptTypeCode == "3") {
			// 显示扫描和摄像按钮
			$("#AGENT_SHOT_IMG").css("display", "block");
			$("#ReadCardButton").css("display", "block");

			if (highprivRight == 1) {
				// 有权限,并且证件类型为：本地身份证、外地身份证或户口本 ,则证件信息可以手动输入
				$("#custInfo_AGENT_CUST_NAME").attr("disabled", false);
				$("#custInfo_AGENT_PSPT_ID").attr("disabled", false);
			} else {
				// 不能手动输入证件信息
				$("#custInfo_AGENT_CUST_NAME").attr("disabled", true);
				$("#custInfo_AGENT_PSPT_ID").attr("disabled", true);
			}
		} else {
			// 隐藏扫描和摄像按钮
			$("#AGENT_SHOT_IMG").css("display", "none");
			$("#ReadCardButton").css("display", "none");

			// 能手动输入证件信息
			$("#custInfo_AGENT_CUST_NAME").attr("disabled", false);
			$("#custInfo_AGENT_PSPT_ID").attr("disabled", false);
		}
		// 经办人名称
		$("#custInfo_AGENT_CUST_NAME").val("");
		//经办人证件号码
		$("#custInfo_AGENT_PSPT_ID").val("");
	}
}