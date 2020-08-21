(function($){
	$.extend({custInfo:{
		infoPart: "CustInfoPart",
        popupPart: "CustInfoMorePart",
		realNameSVCName:null,		//实名制数量限制服务名字，如果有需要可以指向自己实现的服务
		relatePayName:true,			//该参数表示在设置客户姓名时候，如果检测到页面使用了账户编辑里面的PAY_NAME,则两边数据关联起来
		widgets:[],
		isRealName:false,			//是否启用实名制校验
		serialNumber:null,			//服务号码，证件号码校验需要配置
		init:function(){
			this.initWidget();		//初始化自定义更新的到组件内部的表单控件
			this.bindEvents();		//绑定组件内部默认DOM接点事件
			this.initFieldAttr();		//初始化默认必填字段及隐藏显示字段
			//设置实名制标识
            this.isRealName = $("#CustInfoCmp").attr("isRealName") === "true";

            if ($("#BLACK_TRADE_TYPE").val() === "100") { // 过户则显示单方特殊过户勾选框
                $("#SPECIAL_INFO").css("display", "");
            }

            var psptTypeCode = $("#PSPT_TYPE_CODE").val();
            if (psptTypeCode) {
                this.renderSpecialField(psptTypeCode);
            }

            if (psptTypeCode == 0 || psptTypeCode == 1 || psptTypeCode == 3) {
                var inputPermission = $("#INPUT_PERMISSION").val();
                if (inputPermission == 0 ) {
                    $("#PSPT_ID").val("").attr("disabled", true);
                    $("#CUST_NAME").attr("disabled", true);
                    $("#PSPT_ADDR").attr("disabled", true);
                    $("#BIRTHDAY").attr("disabled", true);
                    $("#PSPT_END_DATE").attr("disabled", true);
                } else {
                    $("#PSPT_ID").val("").attr("disabled", false);
                    $("#CUST_NAME").attr("disabled", false);
                    $("#PSPT_ADDR").attr("disabled", false);
                    $("#BIRTHDAY").attr("disabled", false);
                    $("#PSPT_END_DATE").attr("disabled", false);
                }
            }

			/**
			 * 以上界面户主使用身份证、户口本等证件办理入网业务时，营业员可通过手工调整生日信息，导致一经上传客户资料被拦截被判非实名，
			 * 请进行屏蔽，生日信息全部通过身份证号码进行截取，且不可更改，涉及证件类型为本地身份证、外地身份证、军人身份证、户口本，
			 * 其余个人证件不限制，需人工选择。
			 * */
            if (psptTypeCode == 0 || psptTypeCode == 1 || psptTypeCode == 2 || psptTypeCode == 3) {
                $("#BIRTHDAY").attr("disabled", true);
            } else {
                $("#BIRTHDAY").attr("disabled", false);
            }

            /**
             * BUG20180316152525_实名制相关业务控制优化bug
             * 需要填写时,对应的证件类型才默认选择 本地身份证
             * @author zhuoyingzhi
             * @date 20180420
             */
			// 设置value=0选项值被选中
            /*$("#AGENT_PSPT_TYPE_CODE").val("0");
            $("#USE_PSPT_TYPE_CODE").val("0");*/

			//军人身份证
            if ($("#NP_PAGE").val() != "NP_PAGE") {//非携转界面
                $.httphandler.get(this.clazz, "psptTypeCodePriv", null,
                    function (data) {
                        $.endPageLoading();
                        var opentype = $("#OPEN_TYPE").val();
                        //物联网开户显示军人身份证
                        if (opentype == "IOT_OPEN") {
                            //证件类型添加军人身份证
                            PSPT_TYPE_CODE.append("军人身份证", "3");
                            if (data && data.get("X_RESULTCODE") == "0") {
                                var psptTypeName = data.get("PSPT_TYPE_NAME"),
                                    psptTypeCode = data.get("PSPT_TYPE_CODE");
                                AGENT_PSPT_TYPE_CODE.append(psptTypeName, psptTypeCode);
                                USE_PSPT_TYPE_CODE.append(psptTypeName, psptTypeCode);
                                RSRV_STR3.append(psptTypeName, psptTypeCode);
                            }
                            return;
                        } else {
                            if (data && data.get("X_RESULTCODE") == "0") {
                                var psptTypeName = data.get("PSPT_TYPE_NAME"),
                                    psptTypeCode = data.get("PSPT_TYPE_CODE");
                                PSPT_TYPE_CODE.append(psptTypeName, psptTypeCode);
                                AGENT_PSPT_TYPE_CODE.append(psptTypeName, psptTypeCode);
                                USE_PSPT_TYPE_CODE.append(psptTypeName, psptTypeCode);
                                return;
                            }
                        }
                    }, function (code, info, detail) {
                        $.endPageLoading();
                    }, function () {
                        $.endPageLoading();
                    });
            }

            /**
             * REQ201911290007_【携号转网】关于发布CSMS和SOA间接口协议上身份证件传递要求的通知
             * 修改携转界面使用的客户组件，过滤掉不符合规范证件
             * @author mengqx
             * @date 20200323
             */
            if ($("#NP_PAGE").val() === "NP_PAGE") { //携转界面，过滤掉不符合规范证件
                $.httphandler.get(this.clazz,'queryNpPsptTypeList', null,
                    function(data)
                    {
                        var psptTypeList = data.get("PSPT_TYPE_LIST");

                        if (null != psptTypeList)
                        {
                            PSPT_TYPE_CODE.empty();

                            for (var i = 0; i < psptTypeList.length; i++)
                            {
                                PSPT_TYPE_CODE.append(psptTypeList.get(i, "DATA_NAME"),psptTypeList.get(i, "DATA_ID"));
                            }
                            $("#PSPT_TYPE_CODE").val("0");
                        }

                        $.endPageLoading();
                    }, function(error_code, error_info, detail) {
                        $.endPageLoading();
                        MessageBox.error("错误提示", "携转界面获取规范证件后台数据错误！", null, null, error_info, detail);
                    }, function () {
                        $.endPageLoading();
                        MessageBox.alert("告警提示", "携转界面获取规范证件后台数据超时");
                    });
            }

		},
        setSerialNumber: function (sn) {
            this.serialNumber = sn;
        },
        setRelatePayName: function (flag) {
            this.relatePayName = flag;
        },
        setRealName: function (isRealName) {
            this.isRealName = isRealName;
        },
        setRealNameSVCName: function (svcName) {
            this.realNameSVCName = svcName;
        },
		//绑定事件
        bindEvents: function () {
			var oThis = this;
			//身份证类型
            $("#PSPT_TYPE_CODE,#AGENT_PSPT_TYPE_CODE,#USE_PSPT_TYPE_CODE,#RSRV_STR3").bind("change", function () {
                oThis.chkPsptTypeCode($(this).attr("id"));
            });
            //校验营业执照
            $("#legalperson,#termstartdate,#termenddate,#startdate").bind("change", function () {
                oThis.verifyEnterpriseCard();
            });
            //校验组织机构代码证
            $("#orgtype,#effectiveDate,#expirationDate").bind("change", function () {
                oThis.verifyOrgCard();
            });
            $("#AGENT_PSPT_TYPE_CODE").bind("blur", function () {
                oThis.afterChkAgentPsptTypeCode();
            });
            //校验身份证
            $("#PSPT_ID,#AGENT_PSPT_ID,#USE_PSPT_ID,#RSRV_STR4").bind("change", function () {
                oThis.chkPsptId($(this).attr("name"));
            });
            $("#AGENT_PSPT_ID").bind("blur", function () {
                oThis.afterChkAgentPsptId();
            });
            $("#PSPT_ID,#USE_PSPT_ID").bind("blur", function () {
                oThis.afterChkPsptId();
            });
			//拍摄按钮信息
            $("#FORM_SHOT_IMG").unbind("click").bind("click", function () {
                oThis.identification("FORM_PIC_ID", "FORM_PIC_STREAM");
            });
            $("#SHOT_IMG").bind("click", function () {
                oThis.identification("PIC_ID", "PIC_STREAM");
            });
            $("#AGENT_SHOT_IMG").bind("click", function () {
                oThis.identification("AGENT_PIC_ID", "AGENT_PIC_STREAM");
            });
			//显示手写区域
            $("#SHOW_SCAN_AREA").bind("click", function () {
                $("#EditInfoArea").css("display", "none").prev().css("display", "");
                $(this).css("display", "none").prev().css("display", "");
            });
			//显示扫描区域
            $("#SHOW_EDIT_AREA").bind("click", function () {
            	//REQ201809300006
            	var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            	var scanPsptTag = $("#SCAN_PSPT_TAG").val();
            	if(blackTradeType == '680'){
            		if('1' == scanPsptTag){
            			var inputPermission = $("#INPUT_PERMISSION").val();
                		if(inputPermission == 0){
                			MessageBox.alert("仅限自办营业厅工号能进行手工输入！");
                			return false;
                		}
                	}
            	}
                $("#ScanPsptArea").css("display", "none").next().css("display", "");
                $(this).css("display", "none").next().css("display", "");
                $("#SCAN_PSPT_TAG").val("1");
            });
			//邮寄信息选择
            $("#postCheckbox").bind("click", function () {
                oThis.setMailValue();
            });
			//显示更多信息区域
            $("#SHOW_MORE_INFO").bind("click", function () {
                showPopup($("#CUSTINFO_MORE_POPUP_ID").val(), $("#CUSTINFO_MORE_COMPONENT_ID").val(), true);
            });
			//扫描身份证
            $("#SCAN_PSPT").bind("click", function () {
                oThis.clickScanPspt();
            });
            //扫描经办人身份证
            $("#SCAN_PSPT2").bind("click", function () {
                oThis.clickScanPspt2();
            });
            //扫描使用人身份证
            $("#SCAN_PSPT3").bind("click", function () {
                oThis.clickScanPspt3();
            });
            //扫描责任人身份证
            $("#SCAN_PSPT4").bind("click", function () {
                oThis.clickScanPspt4();
            });
            //扫描原客户身份证，"过户"
            $("#FORM_SCAN_PSPT").unbind("click").bind("click", function () {
                oThis.clickScanPspt5();
            });
            //客户姓名
            $("#CUST_NAME,#AGENT_CUST_NAME,#USE,#RSRV_STR2").bind("change", function () {
                oThis.chkCustName($(this).attr("name"));
            });
            $("#CUST_NAME").bind("blur", function () {
                oThis.afterChkCustName();
            });
            $("#AGENT_CUST_NAME").bind("blur", function () {
                oThis.afterChkAgentCustName();
            });
			//地址
            $("#PSPT_ADDR,#AGENT_PSPT_ADDR,#USE_PSPT_ADDR,#RSRV_STR5").bind("change", function () {
                oThis.chkPsptAddr($(this).attr("name"));
            });
            $("#AGENT_PSPT_ADDR").bind("blur", function () {
                oThis.afterChkAgentPsptAddr();
            });
            //显示/隐藏非必填项（仅控制经办人和使用人信息区域）
            $("#CUST_INPUT_CONTROL").tap(function () {
                var $el = $("#CUST_INPUT_CONTROL > span:eq(0)");
                if ($el.hasClass("e_ico-show")) {
                    $("#AgentFieldPart").css("display", "");
                    $("#UseFieldPart").css("display", "");
                    $el.removeClass("e_ico-show").addClass("e_ico-hide").next().html("隐藏非必填项");
                } else if ($el.hasClass("e_ico-hide")) {
                    if (!$("#span_AGENT_CUST_NAME").hasClass("required")) { // 如果经办人信息为非必填项，隐藏经办人信息区域
                        $("#AgentFieldPart").css("display", "none");
                    }
                    if (!$("#span_USE").hasClass("required")) {             // 如果使用人信息为非必填项，隐藏使用人信息区域
                        $("#UseFieldPart").css("display", "none");
                    }
                    $el.removeClass("e_ico-hide").addClass("e_ico-show").next().html("显示非必填项");
                }
            });
		},
		//初始化字段属性及样式
        initFieldAttr: function () {
            //摄像按钮页面初始化显示
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if ('10' == blackTradeType || '100' == blackTradeType) {
                if ('10' == blackTradeType) {
                    var opentype = $("#OPEN_TYPE").val();
                    //物联网开户不显示摄像按钮
//					if( typeof(opentype)  == 'undefined' || opentype != "IOT_OPEN")
//					{
//						$("#SHOT_IMG").css("display", "");
//						$("#AGENT_SHOT_IMG").css("display", "");
//					}
                    /**
                     * REQ201707170020_新增物联卡开户人像采集功能
                     * <br/>
                     * 放开物联网摄像按钮
                     * @author zhuoyingzhi
                     * @date 20170824
                     */
                    $("#SHOT_IMG").css("display", "");
                    $("#AGENT_SHOT_IMG").css("display", "");
                } else {
                    $("#SHOT_IMG").css("display", "");
                    $("#AGENT_SHOT_IMG").css("display", "");
                    $("#FORM_SHOT_IMG").css("display", "");
                    $("#FORM_SCAN_PSPT").css("display", "");
                }
            }
			
			/**
             *REQ201809270030_无手机号码宽带开户界面增加人像对比功
             */
            if('680' == blackTradeType){
            	$("#SHOT_IMG").css("display", "");
            }
            
            //REQ201710310012关于BOSS侧进行和多号业务受理支撑系统改造的通知 start
            if('3700' == blackTradeType){
            	$("#SHOT_IMG").css("display", "");
            	$("#OtherInfoPart").css("display", "none");
            }
            //REQ201710310012关于BOSS侧进行和多号业务受理支撑系统改造的通知 end
	        //mqx start
            if('6800' == blackTradeType){
            	$("#SHOT_IMG").css("display", "");
            	$("#AGENT_SHOT_IMG").css("display", "");
            	$("#OtherInfoPart").css("display", "none");
            	
            	//IMS固话开户，不会有责任人选项，避免和IMSLandLineNew.html中RSRV_STR4重复
            	$("#RsrvFieldPart #RSRV_STR4").attr("id","RSRV_STR4_IMS");
            	$("#RsrvFieldPart #RSRV_STR4").attr("name","RSRV_STR4_IMS");

            }
            //mqx end
            
            if('600' == blackTradeType){
            	$("#SHOT_IMG").css("display", "");
            	$("#AGENT_SHOT_IMG").css("display", "");
            	
            	// $("#CustRequiredInput").css("display", "none");
            	$("#CustInfoVisiblPart").css("display", "none");
            	
            	$("#CUSTINFO_PHONE input").attr("id","CUST_PHONE");
            	$("#CUSTINFO_CONTACT input").attr("id","CUST_CONTACT");
            	$("#CUSTINFO_CONTACT_PHONE input").attr("id","CUST_CONTACT_PHONE");
            	
            	$("#CUSTINFO_PHONE input").attr("name","CUST_PHONE");
            	$("#CUSTINFO_CONTACT input").attr("name","CUST_CONTACT");
            	$("#CUSTINFO_CONTACT_PHONE input").attr("name","CUST_CONTACT_PHONE");
            	$("#OtherInfoPart").css("display", "none");
            	
            }
            
			var requiredField = $("#CustInfoCmp").attr("requiredField");
			this.initRequiredField(requiredField);
		},

		/**
		 * 选择“集团客户”证件类型（营业执照、事业单位法人证书、单位证明、组织机构代码证、社会团体法人登记证书）开户时，
		 * 需支持录入经办人名称、经办人证件类型、经办人证件号码、经办人证件地址，这些信息为必填项目。经办人的证件校验规则同个人大众客户的规则一致。
		 * E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
		 */
        renderSpecialField: function (psptTypeCode) {

            var fields = ["AGENT_CUST_NAME", "AGENT_PSPT_TYPE_CODE", "AGENT_PSPT_ID", "AGENT_PSPT_ADDR"];
            var tag = (psptTypeCode == "E" || psptTypeCode == "G"
                || psptTypeCode == "D" || psptTypeCode == "M" || psptTypeCode == "L") ? true : false;

            var custpsptTypeCode = $("#PSPT_TYPE_CODE").val();
            if (custpsptTypeCode == "E" || custpsptTypeCode == "G" || custpsptTypeCode == "D"
                || custpsptTypeCode == "M" || custpsptTypeCode == "L") {//非个人证件生日非必填
                this.setRequiredField("BIRTHDAY", false);
            } else {//个人证件生日必填
                this.setRequiredField("BIRTHDAY", true);
            }

            var enterpriseflag = false;
            if (custpsptTypeCode == "E") {//法人、成立时间、营业开始时间、营业结束时间都要填
                enterpriseflag = true;
                $("#EnterprisePart").css("display", "");
            } else {
                enterpriseflag = false;
                $("#EnterprisePart").css("display", "none");
            }

			this.setRequiredField("legalperson", enterpriseflag);
			this.setRequiredField("startdate", enterpriseflag);
			this.setRequiredField("termenddate", enterpriseflag);
			this.setRequiredField("termstartdate", enterpriseflag);

            var orgflag = false;
            if (custpsptTypeCode == "M") {//机构类型、有效日期、失效日期
                orgflag = true;
                $("#OrgPart").css("display", "");
            } else {
                orgflag = false;
                $("#OrgPart").css("display", "none");
            }
            this.setRequiredField("orgtype", orgflag);
            this.setRequiredField("effectiveDate", orgflag);
            this.setRequiredField("expirationDate", orgflag);

            var i;
            for (i = 0; i < fields.length; i++) {
                this.setRequiredField(fields[i], tag);
            }
			//生日取消限制
			//this.setRequiredField("BIRTHDAY", !tag);

            if (tag) {
                $("#AgentFieldPart").css("display", "");
            } else {
                $("#AgentFieldPart").css("display", "none");
            }

            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") { //物联网开户
                //经办人、责任人为必填，使用人非必填
                var Rsrvfields = ["RSRV_STR2", "RSRV_STR3", "RSRV_STR4", "RSRV_STR5"];
                for (i = 0; i < Rsrvfields.length; i++) {
                    this.setRequiredField(Rsrvfields[i], tag);
                }
                if (tag) {
                    $("#RsrvFieldPart").css("display", "");
                    $("#UseFieldPart").css("display", "");
                } else {
                    $("#RsrvFieldPart").css("display", "none");
                    $("#UseFieldPart").css("display", "none");
                }
            } else {//个人开户
                //经办人、使用人必填，无责任人
                var usefields = ["USE", "USE_PSPT_TYPE_CODE", "USE_PSPT_ID", "USE_PSPT_ADDR"];
                for (i = 0; i < usefields.length; i++) {
                    this.setRequiredField(usefields[i], tag);
                }
                if (tag) {
                    $("#UseFieldPart").css("display", "");
                } else {
                    $("#UseFieldPart").css("display", "none");
                }
            }
		},

        renderSpecialFieldByPsptId: function (psptId) {//检查年龄是否在16-120之间，显示隐藏经办人栏
            var i,
                fields = ["AGENT_CUST_NAME", "AGENT_PSPT_TYPE_CODE", "AGENT_PSPT_ID", "AGENT_PSPT_ADDR"];
            if (trim(psptId) != "" && !this.checkAge(psptId)) {

	    	//REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
		//加权限判断
		var param = "";
		var temp = this;//保留全局对象，调用setRequiredField方法
		$.httphandler.post(this.clazz, "verifyAgentPriv", param,
				function(data){
					$.endPageLoading();
					var permission = data.get("hasAgentPriv");
					if(data && permission!="true"){
						MessageBox.error("告警提示","该工号没有代办入网的权限！（仅限自办营业厅工号能申请该权限）",null, null, null, null);
						psptObj = $("#PSPT_ID");
						psptObj.val("");


                for (i = 0; i < fields.length; i++) {
			temp.setRequiredField(fields[i], false);
		}
		$("#AgentFieldPart").css("display", "none");
//		return false;
		}
		else{
//			verifyEnterpriseCard();
//			verifyOrgCard();
			//return true;
			for(var i=0; i<fields.length; i++){
			temp.setRequiredField(fields[i],true);

                	}
                	$("#AgentFieldPart").css("display", "");
		}
						},function(code, info, detail){
							$.endPageLoading();
							psptObj = $("#PSPT_ID");
							psptObj.val("");
							MessageBox.error("错误提示","工号权限校验失败！",null, null, null, null);
						},function(){
							$.endPageLoading();
							psptObj = $("#PSPT_ID");
							psptObj.val("");
							MessageBox.alert("错误提示","工号权限校验失败！");
					});
            } else {
                for (i = 0; i < fields.length; i++) {
                    this.setRequiredField(fields[i], false);
                }
                $("#AgentFieldPart").css("display", "none");
            }
        },

		// 根据身份证号码获取生日日期
        getBirthdayByPsptId: function (psptId) {
            var tmpStr = "";
            if ((psptId.length != 15) && (psptId.length != 18)) {
                $.validate.alerter.one($("PSPT_ID")[0], "输入的证件号码位数错误！");
                return tmpStr;
            }
            if (psptId.length == 15) {
                tmpStr = "19" + psptId.substring(6, 12);
            } else {
                tmpStr = psptId.substring(6, 14);
            }
            return tmpStr.substring(0, 4) + "-" + tmpStr.substring(4, 6) + "-" + tmpStr.substring(6);
        },

        chkPsptTypeCode: function (fieldName) {
            var obj = $("#" + fieldName);
            var custNameObj, psptObj;

            this.togglePsptScanAndInputArea(fieldName);

            if (fieldName == "PSPT_TYPE_CODE") {
                /*
                 * REQ201810190032+和家固话开户界面增加实名制校验—BOSS侧 by mqx
                 * 有该权限的工号可以在家庭产品开户界面、家庭IMS固话开户界面进行单位证件开户。
                 * 没有权限的工号进行单位证件开户时提示：该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请）
                 */
                var blackTradeType = $("#BLACK_TRADE_TYPE").val();
                if(blackTradeType == '6800'){
                    var psptTypeCode = obj.val();
                    var param = "&PSPT_TYPE_CODE="+psptTypeCode;
                    $.beginPageLoading("正在进行权限校验。。。");
                    $.httphandler.get(this.clazz, "verifyOrganizationPriv", param,
                        function (data) {
                            $.endPageLoading();
                            if (data && data.get("CODE") != "0") {
                                $.validate.alerter.one($("#PSPT_TYPE_CODE")[0], "该工号不具有和家固话单位证件开户权限（仅限自办厅工号申请）");
                                $.custInfo.selectDefaultValue(fieldName);
                                $.custInfo.renderSpecialField("0");
                                return;
                            }
                        }, function (code, info, detail) {
                            $.endPageLoading();
                            MessageBox.error("错误提示", "证件开户权限校验获取后台数据错误！", null, null, info, detail);
                        }, function () {
                            $.endPageLoading();
                            MessageBox.alert("告警提示", "证件开户权限校验超时");
                        });
                }

                var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
                if (strCheckUserPsptId == "CREATEUSERSW") {
                    var strPsptTypeCode = obj.val();
                    if (strPsptTypeCode == 0 || strPsptTypeCode == 1 || strPsptTypeCode == 3) {
                        var inputPermission = $("#INPUT_PERMISSION").val();
                        if (inputPermission == 0) {
                            $("#PSPT_ID").val("");
                            $("#PSPT_ID").attr("disabled", true);
                            $("#CUST_NAME").attr("disabled", true);
                            $("#PSPT_ADDR").attr("disabled", true);
                            $("#BIRTHDAY").attr("disabled", true);
                            $("#PSPT_END_DATE").attr("disabled", true);
                        }
                    } else {
                        $("#PSPT_ID").attr("disabled", false);
                        $("#CUST_NAME").attr("disabled", false);
                        $("#PSPT_ADDR").attr("disabled", false);
                        $("#BIRTHDAY").attr("disabled", false);
                        $("#PSPT_END_DATE").attr("disabled", false);
                    }
                }
                custNameObj = $("#CUST_NAME");
                psptObj = $("#PSPT_ID");
                this.chkCustName("CUST_NAME");
                var pspttype = obj.val();

                if (pspttype == 0 || pspttype == 1 || pspttype == 2 || pspttype == 3) {
                    $("#BIRTHDAY").attr("disabled", true);
                } else {
                    $("#BIRTHDAY").attr("disabled", false);
                }
                /**
                 * BUG20180316152525_实名制相关业务控制优化bug
                 * @author zhuoyingzhi
                 * @date 20180420
                 */
                this.isSelectedUseAndAgent(pspttype, fieldName);
            } else if (fieldName == "AGENT_PSPT_TYPE_CODE") {
                custNameObj = $("#AGENT_CUST_NAME");
                psptObj = $("#AGENT_PSPT_ID");
                this.chkCustName("AGENT_CUST_NAME");
            } else if (fieldName == "USE_PSPT_TYPE_CODE") {
                custNameObj = $("#USE");
                psptObj = $("#USE_PSPT_ID");
                this.chkCustName("USE");
            } else if (fieldName == "RSRV_STR3"){//责任人信息
                custNameObj = $("#RSRV_STR2");
                psptObj = $("#RSRV_STR4");
                this.chkCustName("RSRV_STR2");
            }
			var psptTypeCode = obj.val();
			var custName = $.trim(custNameObj.val());

			//0-本地身份证 1-外地身份证
            psptObj.attr("datatype", (psptTypeCode == 0 || psptTypeCode == 1 || psptTypeCode == 2 || psptTypeCode == 3) ? "pspt" : "");

            if (psptTypeCode == "3") {
                MessageBox.alert("请提醒客户同时出示军人身份证明！并进行留存");
            }

			var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") { //物联网开户
                if (fieldName == "PSPT_TYPE_CODE") {
                    if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "A") {
                        $("#USER_TYPE_CODE").val('0');
                    } else {
                        $("#USER_TYPE_CODE").val('8');
                    }
                }
            }

            if (custName != "" && psptTypeCode != "A") {
                if (this.includeChineseCount(custName) < 2) {
                    $.validate.alerter.one($("#" + fieldName)[0], obj.attr("desc") + "不是护照，" + custNameObj.attr("desc") + "不能少于2个中文字符！");
                    custNameObj.val("");
                    return;
                }
            }

            if (fieldName != "PSPT_TYPE_CODE") {
                if (fieldName == "AGENT_PSPT_TYPE_CODE") {
                    $("#AGENT_PSPT_ID").trigger("change");
                }
                if (fieldName == "USE_PSPT_TYPE_CODE") {
                    $("#USE_PSPT_ID").trigger("change");
                }
                if (fieldName == "RSRV_STR3") {
                    $("#RSRV_STR4").trigger("change");
                }

				//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
				//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
				//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
                if (fieldName == "USE_PSPT_TYPE_CODE") {
                    var objValue = $("#PSPT_TYPE_CODE").val();
                    if (objValue == "D" || objValue == "E" || objValue == "G" || objValue == "L" || objValue == "M") {
                        var useObjValue = $("#USE_PSPT_TYPE_CODE").val();
                        if (useObjValue == "D" || useObjValue == "E" || useObjValue == "G" || useObjValue == "L" || useObjValue == "M") {
                            $.validate.alerter.one($("#" + fieldName)[0], "证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
                            //下拉框index=0的值被选中（即默认选中第一个）
                            $.custInfo.selectDefaultValue(fieldName);
                            $("#USE_PSPT_ID").attr("datatype", "");
                            return;
                        }
                    }
                    $("#USE_PSPT_ID").trigger("change");
                }

                if (fieldName == "AGENT_PSPT_TYPE_CODE") {
                    var agentValue = $("#AGENT_PSPT_TYPE_CODE").val();
                    if (agentValue == "D" || agentValue == "E" || agentValue == "G" || agentValue == "L" || agentValue == "M") {
                        $.validate.alerter.one($("#" + fieldName)[0], "证件类型选择集团证件，经办人的证件类型只能选择个人证件，请重新选择！");
                        $.custInfo.selectDefaultValue(fieldName);
                        $("#AGENT_PSPT_ID").attr("datatype", "");
                        return;
                    }
                    $("#AGENT_PSPT_ID").trigger("change");
                }
                return;
            }

			//实名制开户限制
            if (psptTypeCode == "Z" && this.isRealName) {
                $.validate.alerter.one($("#PSPT_TYPE_CODE")[0], "实名制开户，证件类型不能为其他，请重新选择！");
                $.custInfo.selectDefaultValue(fieldName);
                psptObj.attr("datatype", "");
                return;
            }

			$("#PSPT_ID").trigger("change");
			this.renderSpecialField(psptTypeCode);

			//证件类型选择集团证件（单位证件、营业执照、事业单位法人证书、社会团体法人登记证书、组织机构代码证）的，
			//必须录入使用人名称、使用人证件类型、使用人证件号码、使用人证件地址，这些信息为必填项目，
			//并且使用人的证件类型只能选择个人证件（身份证、户口本、护照、军官证、港澳台回乡证）证件校验规则同个人大众客户的规则一致。
            if (fieldName == "PSPT_TYPE_CODE" || fieldName == "USE_PSPT_TYPE_CODE") {
                var objValue = $("#PSPT_TYPE_CODE").val();
                if (objValue == "D" || objValue == "E" || objValue == "G" || objValue == "L" || objValue == "M") {
                    var useObjValue = $("#USE_PSPT_TYPE_CODE").val();
                    if (useObjValue == "D" || useObjValue == "E" || useObjValue == "G" || useObjValue == "L" || useObjValue == "M") {
                        $.validate.alerter.one($("#" + fieldName)[0], "证件类型选择集团证件，使用人的证件类型只能选择个人证件，请重新选择！");
                        $.custInfo.selectDefaultValue(fieldName);
                        $("#USE_PSPT_ID").attr("datatype", "");
                        return;
                    }
                }
            }

            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
                this.renderSpecialFieldByPsptId($("#PSPT_ID").val());
            }
            if (psptTypeCode != "E") {//清除营业执照的附加属性
                $("#legalperson").val('');
                $("#termstartdate").val('');
                $("#termenddate").val('');
                $("#startdate").val('');
            }
            if (psptTypeCode != "M") {//清除组织机构代码证的附加属性
                $("#orgtype").val('');
                $("#effectiveDate").val('');
                $("#expirationDate").val('');
            }

			this.verifyIdCardName(fieldName);
			this.checkGlobalMorePsptId(fieldName);
            // 针对无线固话开户业务进行省侧一证五号校验
            this.checkProvinceMorePsptId(fieldName);
		},
        chkPsptId: function (fieldName) {
            var obj = $("#" + fieldName);
            var psptTypeObj;
            if (fieldName == "PSPT_ID") {
                psptTypeObj = $("#PSPT_TYPE_CODE");
                //人像比对
                if ($("#SCAN_TAG").val() == "0") {
                    $("#SCAN_TAG").val("1");
                } else {
                    $("#BACKBASE64").val("");
                    $("#FRONTBASE64").val("");
                }
            } else if (fieldName == "AGENT_PSPT_ID") {
                psptTypeObj = $("#AGENT_PSPT_TYPE_CODE");

                var blackTradeType = $("#BLACK_TRADE_TYPE").val();
                if(blackTradeType == '6800'){
                    $.beginPageLoading("正在进行工号权限校验。。。");
                    $.httphandler.get(this.clazz, "verifyIMSOpAgentPriv", null,
                        function (data) {
                            $.endPageLoading();
                            if (data && data.get("CODE") != "0") {//无权限
                                $("#AGENT_PSPT_ID").val("");
                                MessageBox.error("告警提示","该工号不具有和家固话开户代办权限（仅限自办厅工号申请）",null, null, null, null);
                                return;
                            }else {//有权限
                                //人像比对
                                if ($("#AGENT_SCAN_TAG").val() == "0") {
                                    $("#AGENT_SCAN_TAG").val("1");
                                } else {
                                    $("#AGENT_BACKBASE64").val("");
                                    $("#AGENT_FRONTBASE64").val("");
                                }
                            }
                        }, function (code, info, detail) {
                            $.endPageLoading();
                            MessageBox.error("错误提示", "工号权限校验获取后台数据错误！", null, null, info, detail);
                        }, function () {
                            $.endPageLoading();
                            MessageBox.alert("告警提示", "工号权限校验超时");
                        });
                }else {
                    //人像比对
                    if ($("#AGENT_SCAN_TAG").val() == "0") {
                        $("#AGENT_SCAN_TAG").val("1");
                    } else {
                        $("#AGENT_BACKBASE64").val("");
                        $("#AGENT_FRONTBASE64").val("");
                    }
                }
            } else if (fieldName == "USE_PSPT_ID") {
                psptTypeObj = $("#USE_PSPT_TYPE_CODE");
            } else if (fieldName == "RSRV_STR4"){//责任人信息
                psptTypeObj = $("#RSRV_STR3");
            }
            var psptId = $.trim(obj.val());
            var desc = $.trim(obj.attr("desc"));
            var psptTypeCode = psptTypeObj.val();

            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") { //物联网开户
                //经办人、责任人证件号码不能相同
                var agentid = $.trim($("#AGENT_PSPT_ID").val());
                var rsrv4id = $.trim($("#RSRV_STR4").val());
                if (agentid.length > 0 && rsrv4id.length > 0 && agentid == rsrv4id) {
                    MessageBox.alert("经办人、责任人证件号码不能相同！");
                    $("#AGENT_PSPT_ID").val("");
                    $("#RSRV_STR4").val("");
                    return false;
                }
            }
            if (psptId == "") return;
            //提示开发人员在使用该组件校验时候，必须设置手机服务号码
            //add by zhangxing3 for REQ201710310012关于BOSS侧进行和多号业务受理支撑系统改造的通知 start
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if ("3700" == blackTradeType)
            {
            	this.serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            }
            //add by zhangxing3 for REQ201710310012关于BOSS侧进行和多号业务受理支撑系统改造的通知 end
            //add by mqx
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if ("6800" == blackTradeType)
            {
            	this.serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            }
            //add by mqx
            if ("600" == blackTradeType)
            {
            	this.serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            }
            if (!this.serialNumber) {
                $.validate.alerter.one($("#SERIAL_NUMBER")[0], "请先填写服务号码!");
                return;
            }
            if ($.toollib.isRepeatCode(psptId)) {
                $.validate.alerter.one(obj[0], desc + "不能全为同一个数字，请重新输入!");
                obj.val("");
                obj.focus();
                return;
            }
            if ($.toollib.isSerialCode(psptId)) {
                $.validate.alerter.one(obj[0], "连续数字不能作为" + desc + "，请重新输入!");
                obj.val("");
                obj.focus();
                return;
            }
            if (psptId.length >= 4 && this.serialNumber.length >= psptId.length && (this.serialNumber.indexOf(psptId) == 0
                || this.serialNumber.lastIndexOf(psptId) == (this.serialNumber.length - psptId.length))) {
                $.validate.alerter.one(obj[0], "电话号码的前面四位（或以上）与电话号码后面四位（或以上）不能作为" + desc + "!");
                obj.val("");
                obj.focus();
                return;
            }

			//港澳居民回乡证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
            if (psptTypeCode == "H") {
                if (psptId.length != 9 && psptId.length != 11) {
                    $.validate.alerter.one(obj[0], "港澳居民回乡证校验：" + desc + "必须为9位或11位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (!(psptId.charAt(0) == "H" || psptId.charAt(0) == "M") || !$.toollib.isNumber(psptId.substr(1))) {
                    $.validate.alerter.one(obj[0], "港澳居民回乡证校验：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "O") {
                var psptIdtemp = psptId.replace(/\s/g, '');
                if (psptIdtemp != psptId) {
                    $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：证件号码中间不能有空格。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                //港澳居民来往内地通行证：证件号码为9位或11位。首位为英文字母"H"或"M"；其余位均为阿拉伯数字
                if (psptId.length != 9 && psptId.length != 11) {
                    $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：" + desc + "必须为9位或11位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (!(psptId.charAt(0) == "H" || psptId.charAt(0) == "M") || !$.toollib.isNumber(psptId.substr(1))) {
                    $.validate.alerter.one(obj[0], "港澳居民来往内地通行证校验：" + desc + "首位必须为英文字母\"H\"或\"M\",其余位均为阿拉伯数字。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "I") {
                //台湾居民回乡:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
                if (psptId.length != 8 && psptId.length != 11) {
                    $.validate.alerter.one(obj[0], "台湾居民回乡校验：" + desc + "必须为8位或11位！");
                    obj.val("");
                    obj.focus();
                    return false;
                }
                if (psptId.length == 8) {
                    if (!$.toollib.isNumber(psptId)) {
                        $.validate.alerter.one(obj[0], "台湾居民回乡校验：" + desc + "为8位时，必须均为阿拉伯数字！");
                        obj.val("");
                        obj.focus();
                        return false;
                    }
                }
                if (psptId.length == 11) {
                    if (!$.toollib.isNumber(psptId.substring(0, 10))) {
                        $.validate.alerter.one(obj[0], "台湾居民回乡校验：：" + desc + "为11位时，前10位必须均为阿拉伯数字。");
                        obj.val("");
                        obj.focus();
                        return false;
                    }
                }
            } else if (psptTypeCode == "N") {
                //台湾居民来往大陆通行证:证件号码为11位时，前10位为阿拉伯数字，最后一位为校验码，括号内为英文字母或阿拉伯数字；证件号码为8位时，均为阿拉伯数字。
                var psptIdtemp = psptId.replace(/\s/g, '');
                if (psptIdtemp != psptId) {
                    $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：证件号码中间不能有空格。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (psptId.substr(0, 2) != "TW" && psptId.substr(0, 4) != "LXZH") {
                    if (psptId.length == 11 || psptId.length == 12) {
                        if (!$.toollib.isNumber(psptId.substring(0, 10))) {
                            $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为11或12位时，前10位必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else if (psptId.length == 8) {
                        if (!$.toollib.isNumber(psptId)) {
                            $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为8位时，必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else if (psptId.length == 7) {
                        if (!$.toollib.isNumber(psptId)) {
                            $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "为7位时，必须均为阿拉伯数字。");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    } else {
                        $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "格式错误");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                } else {
                    var psptIdsub;
                    if (psptId.substr(0, 2) == "TW") {
                        psptIdsub = psptId.substr(2);
                    } else if (psptId.substr(0, 4) == "LXZH") {
                        psptIdsub = psptId.substr(4);
                    }

                    var re = /^[()A-Z0-9]+$/;
                    var re1 = /^[•··.．·\d\u4e00-\u9fa5]+$/;
                    var pattern1 = /[A-Z]/;
                    var pattern2 = /[0-9]/;
                    var pattern3 = /[(]/;
                    var pattern4 = /[)]/;

                    if (re1.test(psptIdsub) || !re.test(psptIdsub) || !pattern1.test(psptIdsub) || !pattern2.test(psptIdsub) || !pattern3.test(psptIdsub) || !pattern4.test(psptIdsub)) {
                        $.validate.alerter.one(obj[0], "台湾居民来往大陆通行证校验：" + desc + "前2位“TW”或 “LXZH”字符时，后面是阿拉伯数字、英文大写字母与半角“()”的组合");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                }
            } else if (psptTypeCode == "C" || psptTypeCode == "A") {
                var psptIdtemp = psptId.replace(/\s/g, '');
                if (psptIdtemp != psptId) {
                    $.validate.alerter.one(obj[0], "证件号码中间不能有空格。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                //军官证、警官证、护照：证件号码须大于等于6位字符
                if (psptId.length < 6) {
                    var tmpName = psptTypeCode == "A" ? "护照校验：" : "军官证类型校验：";
                    $.validate.alerter.one(obj[0], tmpName + desc + "须大于等于6位字符!");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "E") {
                //营业执照：证件号码长度需满足15位 20151022 REQ201510140003 营业执照证件规则调整 CHENXY3
                if (psptId.length != 13 && psptId.length != 15 && psptId.length != 18 && psptId.length != 20 && psptId.length != 22 && psptId.length != 24) {
                    $.validate.alerter.one(obj[0], "营业执照类型校验：" + desc + "长度需满足13位、15位、18位、20位、22位或24位！当前：" + psptId.length + "位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "M") {
                //组织机构代码校验
                if (psptId.length != 10 && psptId.length != 18 && psptId.length != 20) {
                    $.validate.alerter.one(obj[0], "组织机构代码证类型校验：" + desc + "长度需满足10位或18位或20位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (psptId.length == 10 && psptId.charAt(8) != "-") {
                    $.validate.alerter.one(obj[0], "组织机构代码证类型校验：" + desc + "规则为\"XXXXXXXX-X\"，倒数第2位是\"-\"。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "G") {
                //事业单位法人登记证书：证件号码长度需满足12位
                if (psptId.length != 12 && psptId.length != 18) {
                    $.validate.alerter.one(obj[0], "事业单位法人登记证书类型校验：" + desc + "长度需满足12位或者18位。");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
                if (psptId.substr(psptId.length - 1, psptId.length) == "x") {
                    obj.val(psptId.substr(0, psptId.length - 1) + "X");
                }            
                
				//身份证相关检查
                obj.attr("datatype", "pspt");
                if (!$.validate.verifyField(obj[0])) {
                    obj.val("");
                    obj.focus();
                    return;
                } else {
                    obj.attr("datatype", "");
                }
                
				var area = {11:"\u5317\u4eac", 12:"\u5929\u6d25", 13:"\u6cb3\u5317", 14:"\u5c71\u897f", 15:"\u5185\u8499\u53e4", 21:"\u8fbd\u5b81", 22:"\u5409\u6797", 23:"\u9ed1\u9f99\u6c5f", 31:"\u4e0a\u6d77", 32:"\u6c5f\u82cf", 33:"\u6d59\u6c5f", 34:"\u5b89\u5fbd", 35:"\u798f\u5efa", 36:"\u6c5f\u897f", 37:"\u5c71\u4e1c", 41:"\u6cb3\u5357", 42:"\u6e56\u5317", 43:"\u6e56\u5357", 44:"\u5e7f\u4e1c", 45:"\u5e7f\u897f", 46:"\u6d77\u5357", 50:"\u91cd\u5e86", 51:"\u56db\u5ddd", 52:"\u8d35\u5dde", 53:"\u4e91\u5357", 54:"\u897f\u85cf", 61:"\u9655\u897f", 62:"\u7518\u8083", 63:"\u9752\u6d77", 64:"\u5b81\u590f", 65:"\u65b0\u7586", 71:"\u53f0\u6e7e", 81:"\u9999\u6e2f", 82:"\u6fb3\u95e8", 91:"\u56fd\u5916"};
                psptId = psptId.toUpperCase();
                if (area[parseInt(psptId.substr(0, 2))] == null) {
                    $.validate.alerter.one(obj[0], "\u8eab\u4efd\u8bc1\u53f7\u7801\u4e0d\u6b63\u786e\u0028\u5730\u533a\u975e\u6cd5\u0029\uff01");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (!(/(^\d{15}$)|(^\d{17}([0-9]|X)$)/.test(psptId))) {
                    $.validate.alerter.one(obj[0], "\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u957f\u5ea6\u4e0d\u5bf9\uff0c\u6216\u8005\u53f7\u7801\u4e0d\u7b26\u5408\u89c4\u5b9a\uff01\n15\u4f4d\u53f7\u7801\u5e94\u5168\u4e3a\u6570\u5b57\uff0c18\u4f4d\u53f7\u7801\u672b\u4f4d\u53ef\u4ee5\u4e3a\u6570\u5b57\u6216X\u3002 ");
                    obj.val("");
                    obj.focus();
                    return;
                }
                
                // 下面分别分析出生日期和校验位
                var len, re;
                len = psptId.length;
                var arrSplit = "";
                var dtmBirth = "";
                if (len == 15) {
                    re = new RegExp(/^(\d{6})(\d{2})(\d{2})(\d{2})(\d{3})$/);
                    arrSplit = psptId.match(re);  // 检查生日日期是否正确
                    dtmBirth = new Date("19" + arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                }
                if (len == 18) {
                    re = new RegExp(/^(\d{6})(\d{4})(\d{2})(\d{2})(\d{3})([0-9]|X)$/);
                    arrSplit = psptId.match(re);  // 检查生日日期是否正确
                    dtmBirth = new Date(arrSplit[2] + "/" + arrSplit[3] + "/" + arrSplit[4]);
                }
                
                var compareyear = "" ;
                if (len == 15) {
                	compareyear = "19"+arrSplit[2];
                }else if (len == 18) {
                	compareyear = arrSplit[2];
                }               	
                //var fullyeart = dtmBirth.getFullYear();
                //var fullmonth = dtmBirth.getMonth();
                //var fullday = dtmBirth.getDate();
                
                var bGoodDay = (dtmBirth.getFullYear() == Number (compareyear) ) && ((dtmBirth.getMonth() + 1) == Number(arrSplit[3])) && (dtmBirth.getDate() == Number(arrSplit[4]));
                if (!bGoodDay) {
                    $.validate.alerter.one(obj[0], "\u8f93\u5165\u7684\u8eab\u4efd\u8bc1\u53f7\u91cc\u51fa\u751f\u65e5\u671f\u4e0d\u5bf9\uff01");
                    obj.val("");
                    obj.focus();
                    return;
                }
                
                if (len == 18) {
                /*每位加权因子*/
                var powers = new Array("7", "9", "10", "5", "8", "4", "2", "1", "6", "3", "7", "9", "10", "5", "8", "4", "2");
                /*第18位校检码*/
                var parityBit = new Array("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2");
                var psptBit = psptId.charAt(17).toUpperCase();
                var id17 = psptId.substring(0, 17);
                /*加权 */
                var power = 0;
                for (var i = 0; i < 17; i++) {
                    power += parseInt(id17.charAt(i), 10) * parseInt(powers[i]);
                }
                /*取模*/
                var mod = power % 11;
                var checkBit = parityBit[mod];
                if (psptBit != checkBit) {
                    $.validate.alerter.one(obj[0], "身份证号码不合法");
                    obj.val("");
                    obj.focus();
                    return;
                }

                var bit11 = psptId.substring(10, 11);
                var bit13 = psptId.substring(12, 13);
                if (bit11 != "0" && bit11 != "1") {
                    $.validate.alerter.one(obj[0], "18位身份证号码11位必须为0或者1");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (parseInt(bit13) > 3) {
                    $.validate.alerter.one(obj[0], "18位身份证号码13位必须小于等于3");
                    obj.val("");
                    obj.focus();
                    return;
                }
                }
                
                
                if (fieldName == "AGENT_PSPT_ID" || fieldName == "USE_PSPT_ID" || fieldName == "RSRV_STR4") {
                    if (trim(psptId) != "") {
                        var cust_age = this.jsGetAgeNew($.trim(psptId));
                        //经办人、使用人、年龄范围必须在11岁（含）至120岁（不含）之间
			/**
			* REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
		    	* 办理人16岁以下，经办人必须满16岁
			* @param idCard
			* @return
			*/
			if(!this.checkAge($("#PSPT_ID").val())){
				if(cust_age < 16){
					//MessageBox.error("告警提示","经办人年龄必须满16岁",null, null, null, null);	
                    $.validate.alerter.one(obj[0], "年龄范围必须在16-120岁之间");

					obj.val("");
					obj.focus();
					return false;
				}
			}else if ( 120 <= cust_age) {
                            $.validate.alerter.one(obj[0], "年龄范围必须在16-120岁之间");
                            obj.val("");
                            obj.focus();
                            return;
                        }
                    }
                }                
                
                this.verifyIdCard(fieldName);
                //设置生日日期
                if (fieldName == "PSPT_ID") {
                    this.renderSpecialFieldByPsptId(psptId);
                    $("#BIRTHDAY").val(this.getBirthdayByPsptId(psptId));
                }
            }
            
            this.verifyIdCardName(fieldName);
            this.checkGlobalMorePsptId(fieldName);
            // 针对无线固话开户业务进行省侧一证五号校验
            this.checkProvinceMorePsptId(fieldName);

            if (!this.isRealName || fieldName != "PSPT_ID") {
                if (fieldName == "USE_PSPT_ID") {
                    if (!this.checkUsePsptIdForReal()) {
                        obj.val("");
                        obj.focus();
                        return;
                    }
                    this.checkRealNameLimitByUsePspt();
                }
                return;
            }
            if (!this.checkPsptIdForReal()) {
                obj.val("");
                obj.focus();
                return;
            }
            this.checkRealNameLimitByPspt();
            this.checkPsptidBlackListInfo();
            this.checkTradeBookInfo();
            /**
             * BUG20180316152525_实名制相关业务控制优化bug
             * @author zhuoyingzhi
             * @date 20180420
             */
            if (fieldName == "PSPT_ID") { // 客户证件号码
                this.isSelectedUseAndAgent(psptTypeCode, fieldName);
            }

            /*
             * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
             * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
             * mengqx 20190912
             */
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if(blackTradeType == '6800') {
                if (fieldName == "PSPT_ID") {
                    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
                    var custName = $("#CUST_NAME").val();
                    var psptId = $("#PSPT_ID").val();
                    if ("" != trim(serialNumber) && "" != trim(custName) && "" != trim(psptId))
                        this.checkIMSPhoneCustInfo();
                }
            }
        },
        identification: function (picid, picstream) {
        	var toDay=new Date();
    		var toMonth=(toDay.getMonth()+1).toString();
    		if(toMonth.length==1){
    			toMonth="0"+toMonth;
    		}
    		var thisDay=toDay.getDate().toString();
    		if(thisDay.length==1){
    			thisDay="0"+thisDay;
    		} 
    		var op_time=toDay.getFullYear().toString()+"-"+toMonth+"-"+thisDay
    					+" "+toDay.getHours().toString()+":"+toDay.getMinutes().toString()+":"+toDay.getSeconds().toString();
        	var work_no = $("input[name='STAFF_ID']").val();
        	var op_code = $("input[name='BLACK_TRADE_TYPE']").val();
        	var op_code_name = "";
        	if(op_code == "10"){
        		op_code_name = "个人用户开户";
        	}else if(op_code == "100"){
        		op_code_name = "过户";
        	}
            var custName, psptId, psptType, fornt, desc;
            var head = picid.split("_")[0];
            var tag = head == "PIC";
            if (tag) {
                custName = $("#CUST_NAME").val();
                psptId = $("#PSPT_ID").val();
                psptType = $("#PSPT_TYPE_CODE").val();
                fornt = $("#FRONTBASE64").val();
                desc = "请输入客户证件号码！";
            } else if (head == "FORM") {
                custName = $("#FORM_CUST_NAME").val();
                psptId = $("#FORM_PSPT_ID").val();
                psptType = $("#FORM_PSPT_TYPE_CODE").val();
                fornt = $("#FORM_FRONTBASE64").val();
                desc = "请先进行原客户证件扫描！";
            } else {
                custName = $("#AGENT_CUST_NAME").val();
                psptId = $("#AGENT_PSPT_ID").val();
                psptType = $("#AGENT_PSPT_TYPE_CODE").val();
                fornt = $("#AGENT_FRONTBASE64").val();
                desc = "请输入经办人证件号码！";
            }
            if (psptId == "") {
                MessageBox.alert(desc);
                return false;
            }
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
			
			 /**
             * REQ201809270030_无手机号码宽带开户界面增加人像对比功能
             * 确认人像比对标记
             */
            if("680" == blackTradeType){
            	$("#IS_PORTRAIT_DISCRIMINATION").val('true');
            }

			/**
			 * REQ201707060020_关于年龄外经办人限制变更的优化
			 * <br/>
			 * 客户证件类型为  户口本 年龄小于11大于等于120 不需要进行 客户摄像
			 * @author zhuoyingzhi
			 * @date 20170804
			 */
            if ("10" == blackTradeType || "100" == blackTradeType) {
                //开户或过户
                if (tag && "2" == psptType) {
                    //客户证件类型为 户口本
                    var cust_age = this.jsGetAge($.trim(psptId));

                    if (cust_age < 11 || 120 <= cust_age) {
                        MessageBox.error("告警提示", "客户证件类型为 户口本, 年龄小于11大于等于120,不需要进行 客户摄像", null, null, null, null);
                        return false;
                    }
                }

            }
			/*********************************************/

            var sn = "";
            if ("100" == blackTradeType) {
                sn = $("#AUTH_SERIAL_NUMBER").val();
            } else {
                sn = $("#SERIAL_NUMBER").val();
            }
            
            if ($.os.phone) { // 手机移动端人像比对
                $.MBOP.shotImage(head);
                return;
            }

            var bossOriginalXml = [];
            bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
            bossOriginalXml.push('<req>');
            bossOriginalXml.push('	<billid>' + '</billid>');
            bossOriginalXml.push('	<brand_name>' + '</brand_name>');
            bossOriginalXml.push('	<brand_code>' + '</brand_code>');
            bossOriginalXml.push('	<work_name>' + '</work_name>');
            bossOriginalXml.push('	<work_no>' + work_no + '</work_no>');
            bossOriginalXml.push('	<org_info>' + '</org_info>');
            bossOriginalXml.push('	<org_name>' + '</org_name>');
            bossOriginalXml.push('	<phone>' + sn + '</phone>');
            bossOriginalXml.push('	<serv_id>' + '</serv_id>');
            bossOriginalXml.push('	<op_time>' + op_time + '</op_time>');
            bossOriginalXml.push('	<op_code>' + op_code + '</op_code>');
            bossOriginalXml.push('	<op_code_name>' + op_code_name + '</op_code_name>');
           

            bossOriginalXml.push('	<busi_list>');
            bossOriginalXml.push('		<busi_info>');
            bossOriginalXml.push('			<op_code>' + '</op_code>');
            bossOriginalXml.push('			<sys_accept>' + '</sys_accept>');
            bossOriginalXml.push('			<busi_detail>' + '</busi_detail>');
            bossOriginalXml.push('		</busi_info>');
            bossOriginalXml.push('	</busi_list>');

            bossOriginalXml.push('	<verify_mode>' + '</verify_mode>');
            bossOriginalXml.push('	<id_card>' + '</id_card>');
            bossOriginalXml.push('	<cust_name>' + '</cust_name>');
            bossOriginalXml.push('	<copy_flag></copy_flag>');
            bossOriginalXml.push('	<agm_flag></agm_flag>');
            bossOriginalXml.push('</req>');
            var bossOriginaStr = bossOriginalXml.join("");
            //调用拍照方法
            var resultInfo = makeActiveX.Identification(bossOriginaStr);
            //获取保存结果
            var result = makeActiveX.IdentificationInfo.result;
            //获取保存照片ID
            var picID = makeActiveX.IdentificationInfo.pic_id;
            if (picID != '') {
                MessageBox.success("人像摄像成功", null, function (btn) {
                    if ("ok" === btn) {
                        //获取照片流
                        var picStream = makeActiveX.IdentificationInfo.pic_stream;
                        picStream = escape(encodeURIComponent(picStream));
                        if ("0" == result) {
                            $("#" + picid).val(picID);
                            $("#" + picstream).val(picStream);
                            var param = "&BLACK_TRADE_TYPE=" + blackTradeType;
                            var t = "";
                            if (!tag) {//原客户或经办人摄像
                                t = head + "_";
                            }
                            param += "&CERT_ID=" + psptId;
                            param += "&CERT_NAME=" + custName;
                            param += "&CERT_TYPE=" + psptType;
                            param += "&PIC_STREAM=" + picStream + "&FRONTBASE64=" + escape(encodeURIComponent(fornt));
                            param += "&SERIAL_NUMBER=" + sn;
                            $.beginPageLoading("正在进行人像比对。。。");
                            //是否进行人像比对 0:否1:是
                    		$("#custInfo_COMPARISON_IS").val(1);
                    		$("#custInfo_COMPARISON_RUSLT").val(0);//人像比对是否通过1：是，0：否
                            $.httphandler.post("com.asiainfo.veris.crm.order.web.frame.csview.common.component.person.BaseInfoFieldHandler", "cmpPicInfo", param,
                                function (data) {
                                    $.endPageLoading();
                                    if (data && data.get("X_RESULTCODE") == "0") {
                                    	$("#custInfo_COMPARISON_RUSLT").val(1);//人像比对是否通过1：是，0：否
                        				$("#custInfo_COMPARISON_SEQ").val(data.get("SEQ"));//人像比对流水
                                        MessageBox.success("成功提示", "人像比对成功", null, null, null);
                                        return true;
                                    } else if (data && data.get("X_RESULTCODE") == "1") {
                                        $("#" + picid).val("ERROR");
                                        $("#" + picstream).val(data.get("X_RESULTINFO"));
                                        MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                                        return false;
                                    }
                                }, function (code, info, detail) {
                                    $.endPageLoading();
                                    $("#" + picid).val("ERROR");
                                    $("#" + picstream).val("人像比对信息错误，请重新拍摄！");
                                    MessageBox.error("错误提示", "人像比对信息错误，请重新拍摄！", null, null, null, null);
                                }, function () {
                                    $.endPageLoading();
                                    $("#" + picid).val("ERROR");
                                    $("#" + picstream).val("人像比对失败，请重新拍摄");
                                    MessageBox.alert("告警提示", "人像比对失败，请重新拍摄");
                                });
                        } else {
                            MessageBox.error("告警提示", "拍摄失败！请重新拍摄", null, null, null, null);
                        }
                    }
                });
            } else {
                MessageBox.error("人像摄像失败");
                return false;
            }
		},
        afterChkPsptId: function () {
            var obj = $(this);
            if (obj.val() == "") return;
        },
        clickScanPspt: function () {
            var $editArea = $("#SHOW_EDIT_AREA");
            if ($.os.phone) { // 手机移动端二代身份证扫描
                $.MBOP.getMsgByEForm("PSPT_ID");
                return;
            }
            //REQ201911080010 关于实名入网办理日志留存的改造通知 - 是否设备自动读取 0:否1:是
        	$("#custInfo_DEV_READ").val(1);
        	
            getMsgByEForm("PSPT_ID", "CUST_NAME,PAY_NAME", "SEX", "FOLK_CODE", "BIRTHDAY", "PSPT_ADDR,POST_ADDRESS", null, "PSPT_END_DATE","custInfo_READ_RUSLT");

	    this.renderSpecialFieldByPsptId($("#PSPT_ID").val());

	    this.chkPsptId("PSPT_ID");
	    
            this.verifyIdCard("PSPT_ID");
            this.verifyIdCardName("PSPT_ID");
            $("#SCAN_PSPT_TAG").val("0");

            if (!this.isRealName) {
                $editArea.trigger("click"); // 显示手工输入区域
                return;
            }
            var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
            if (strCheckUserPsptId == "CREATEUSERSW") {
                var strPsptAddr = $("#PSPT_ADDR").val();
                $("#CONTACT_ADDRESS").val(strPsptAddr);
            }
            this.checkRealNameLimitByPspt();
            this.checkTradeBookInfo();
            $editArea.trigger("click"); // 显示手工输入区域

            /*
                 * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
                 * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
                 * mengqx 20190912
                 */
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if(blackTradeType == '6800') {
                this.checkIMSPhoneCustInfo();
            }
        },
        clickScanPspt2: function () {
            //人像比对
            var psptTypeCode = $("#PSPT_TYPE_CODE").val();
            var needpicinfo = null;
            var tag = (psptTypeCode == "E" || psptTypeCode == "G"
                || psptTypeCode == "D" || psptTypeCode == "M" || psptTypeCode == "L") ? true : false;
            if (tag) {
                needpicinfo = "PIC_INFO";
            }
            if ($.os.phone) { // 手机移动端二代身份证扫描
                $.MBOP.getMsgByEForm("AGENT_PSPT_ID");
                return;
            }
            getMsgByEForm("AGENT_PSPT_ID", "AGENT_CUST_NAME", needpicinfo, null, null, "AGENT_PSPT_ADDR", null, null);

	    this.chkPsptId("AGENT_PSPT_ID");

            this.verifyIdCard("AGENT_PSPT_ID");
            this.verifyIdCardName("AGENT_PSPT_ID");
            if (!this.isRealName) {
                return;
            }
        },
        clickScanPspt3: function () {
            if ($.os.phone) { // 手机移动端二代身份证扫描
                $.MBOP.getMsgByEForm("USE_PSPT_ID");
                return;
            }
            getMsgByEForm("USE_PSPT_ID", "USE", null, null, null, "USE_PSPT_ADDR", null, null);
            
            //start-wangsc10--使用人在扫描时也加上一证五号校验-20190529
            this.chkPsptId("USE_PSPT_ID");
            //end
            
            this.verifyIdCard("USE_PSPT_ID");
            this.verifyIdCardName("USE_PSPT_ID");
            if (!this.isRealName) {
                return;
            }
        },
        clickScanPspt4: function () {
            if ($.os.phone) { // 手机移动端二代身份证扫描
                $.MBOP.getMsgByEForm("RSRV_STR4");
                return;
            }
            getMsgByEForm("RSRV_STR4", "RSRV_STR2", null, null, null, "RSRV_STR5", null, null);
            this.verifyIdCard("RSRV_STR4");
            this.verifyIdCardName("RSRV_STR4");
            if (!this.isRealName) {
                return;
            }
        },
        clickScanPspt5: function () {
            if ($("#OLD_PSPT_ID_NUM").val() == "") {
                MessageBox.alert("请先进行原客户资料查询！");
                return;
            }
            if ($.os.phone) { // 手机移动端二代身份证扫描
                $.MBOP.getMsgByEForm("FORM_PSPT_ID");
                return;
            }
            getMsgByEForm("FORM_PSPT_ID", "FORM_CUST_NAME", "PIC_INFO", null, null, null, null, null);
            if ($("#FORM_PSPT_ID").val() != $("#OLD_PSPT_ID_NUM").val()) {
                MessageBox.alert("原客户扫描信息与原客户真实信息不一致");
                $("#FORM_PIC_ID").val("ERROR");
                $("#FORM_PIC_STREAM").val("扫描信息与原客户真实信息不一致");
                return;
            }
            if ($("#FORM_PSPT_ID").val() != "") {
                MessageBox.success("原客户扫描成功，原客户姓名：" + $("#FORM_CUST_NAME").val() + ",证件号码：" + $("#FORM_PSPT_ID").val());
            }
        },
        chkCustName: function (fieldName) {
            var obj = $("#" + fieldName);
            /**
             * REQ201609280015 关于开户用户名优化的需求
             * 前后去空格 chenxy3 20161104
             * */
            var custNameTrim = obj.val().replace(/\s+/g, "");
            obj.val(custNameTrim);
            var psptTypeCode, psptTypeDesc;
            //判断到底是客户姓名还是经办人姓名
            if (fieldName == "CUST_NAME") {
                psptTypeCode = $("#PSPT_TYPE_CODE").val();
                psptTypeDesc = $("#PSPT_TYPE_CODE").attr("desc");
            } else if (fieldName == "AGENT_CUST_NAME") {
                psptTypeCode = $("#AGENT_PSPT_TYPE_CODE").val();
                psptTypeDesc = $("#AGENT_PSPT_TYPE_CODE").attr("desc");
            } else if (fieldName == "USE") {
                psptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
                psptTypeDesc = $("#USE_PSPT_TYPE_CODE").attr("desc");
            } else if (fieldName == "RSRV_STR2"){//责任人信息
                psptTypeCode = $("#RSRV_STR3").val();
                psptTypeDesc = $("#RSRV_STR3").attr("desc");
            }
            var custName = $.trim(obj.val());
            var desc = obj.attr("desc");

            if (!custName) return;

            if (psptTypeCode == "E" || psptTypeCode == "G" || psptTypeCode == "M") {//	营业执照、组织机构代码证、事业单位法人登记证书
                if (this.includeChineseCount(custName) < 4) {
                    $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照," + desc + "不能少于4个中文字符！");
                    obj.val("");
                    obj.focus();
                    return;
                }
            }

            var mainPsptTypeCode = $("#PSPT_TYPE_CODE").val();
            if (mainPsptTypeCode == "E" || mainPsptTypeCode == "G" || mainPsptTypeCode == "D" || mainPsptTypeCode == "M" || mainPsptTypeCode == "L") {
                //	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
                var custnametemp = trim($("#CUST_NAME").val());
                var agentnametemp = trim($("#AGENT_CUST_NAME").val());
                var usenametemp = trim($("#USE").val());
                if (custnametemp != "" && agentnametemp != "" && custnametemp == agentnametemp) {
                    $.validate.alerter.one(obj[0], "单位名称和经办人名称不能相同！");
                    obj.val("");
                    obj.focus();
                    return;
                }
                if (custnametemp != "" && usenametemp != "" && custnametemp == usenametemp) {
                    $.validate.alerter.one(obj[0], "单位名称和使用人名称不能相同！");
                    obj.val("");
                    obj.focus();
                    return;
                }
            }

            if (psptTypeCode != "A" && psptTypeCode != "D") {
                if (psptTypeCode != "E" && psptTypeCode != "G" && psptTypeCode != "D" && psptTypeCode != "M" && psptTypeCode != "L") {//	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书
                    var pattern = /[a-zA-Z0-9]/;
                    if (pattern.test(custName)) {
                        $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照, " + desc + "不能包含数字和字母！");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                }
                if (this.includeChineseCount(custName) < 2) {
                    $.validate.alerter.one(obj[0], psptTypeDesc + "不是护照," + desc + "不能少于2个中文字符！");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "A") {
                /*护照：客户名称须大于三个字符，不能全为阿拉伯数字*/
                if (custName.length < 3 || $.toollib.isNumber(custName)) {
                    $.validate.alerter.one(obj[0], psptTypeDesc + "是护照," + desc + "须大于三个字符，且不能全为阿拉伯数字！");
                    obj.val("");
                    obj.focus();
                    return;
                }
                var specialStr = "“”‘’，《》~！@#￥%……&*（）【】｛｝；：‘’“”，。、《》？+——-=";
                for (var i = 0; i < specialStr.length; i++) {
                    if (custName.indexOf(specialStr.charAt(i)) > -1) {
                        $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                }
            }

            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3" || psptTypeCode == "C" || psptTypeCode == "H" || psptTypeCode == "I" || psptTypeCode == "N" || psptTypeCode == "O") {
                var re = /^[•··.．·\d\u4e00-\u9fa5]+$/;
                if (!re.test(custName)) {
                    $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
                    obj.val("");
                    obj.focus();
                    return;
                }
            } else if (psptTypeCode == "H" || psptTypeCode == "I" || psptTypeCode == "J") {
                // 港澳证、台胞证
                if (custName.length < 2) {
                    $.validate.alerter.one(obj[0], psptTypeDesc + "是护照," + desc + "须两个字符及以上");
                    obj.val("");
                    obj.focus();
                    return;
                }
                var re = /^[•··.．·\d\u4e00-\u9fa5]+$/;
                if (!re.test(custName)) {
                    $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
                    obj.val("");
                    obj.focus();
                    return;
                }
            }

            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2"|| psptTypeCode == "3"
                || psptTypeCode == "O" || psptTypeCode == "N" || psptTypeCode == "P"
                || psptTypeCode == "Q" || psptTypeCode == "W"	) {
                this.verifyIdCard(fieldName);
            }
            this.checkGlobalMorePsptId(fieldName);
            this.verifyIdCardName(fieldName);
            // 针对无线固话开户业务进行省侧一证五号校验
            this.checkProvinceMorePsptId(fieldName);

            //如果不需要进行实名制，或者是非客户姓名，则不需要进行实名制校验限制
            if (!this.isRealName || fieldName != "CUST_NAME") {
                if (fieldName == "USE") {
                    if (this.checkUsePsptIdForReal()) {
                        this.checkRealNameLimitByUsePspt();
                    }
                }
                return;
            }
            if (this.checkPsptIdForReal()) {
                this.checkRealNameLimitByPspt();
            }

            /*
             * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
             * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
             * mengqx 20190912
             */
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if(blackTradeType == '6800'||blackTradeType == '600') {
                if (fieldName == "CUST_NAME") {
                    var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
                    var custName = $("#CUST_NAME").val();
                    var psptId = $("#PSPT_ID").val();
                    if ("" != trim(serialNumber) && "" != trim(custName) && "" != trim(psptId))
                        this.checkIMSPhoneCustInfo();
                }
            }
		},
        includeChinese: function (custName) {
            // 是否包含中文字符
            for (var i = 0; i < custName.length; i++) {
                if ($.toollib.isChinese(custName.charAt(i))) {
                    return true;
                }
            }
            return false;
        },
        includeChineseCount: function (custName) {
            // 是否包含中文字符个数
            var count = 0;
            for (var i = 0; i < custName.length; i++) {
                if ($.toollib.isChinese(custName.charAt(i))) {
                    count++;
                }
            }
            return count;
        },
        afterChkCustName: function () {
            var obj = $("#CUST_NAME");
            var custName = $.trim(obj.val());

            if (custName == "") {
                return;
            }
            var mainPsptTypeCode = $("#PSPT_TYPE_CODE").val();
            if (mainPsptTypeCode == "E" || mainPsptTypeCode == "G" || mainPsptTypeCode == "D" || mainPsptTypeCode == "M" || mainPsptTypeCode == "L") {
                //	E:营业执照,G:事业单位法人证书,D:单位证明,M:组织机构代码证,L:社会团体法人登记证书  不进行特殊字符校验
            } else {
                var specialStr = "`￥#$~!@%^&*(),;'\"?><[]{}\\|:/=+―“”‘’，《》";
                for (i = 0; i < specialStr.length; i++) {
                    if (custName.indexOf(specialStr.charAt(i)) > -1) {
                        $.validate.alerter.one(obj[0], obj.attr("desc") + "包含特殊字符，请检查！");
                        obj.val("");
                        obj.focus();
                        return;
                    }
                }
            }
            //判断如果存在账户名字，则设置账户名
            if (this.relatePayName && $("#PAY_NAME") && $("#PAY_NAME").length) {
                $("#PAY_NAME").val(custName);
            }
        },
        chkPsptAddr: function (fieldName) {
            var obj = $("#" + fieldName);
            var psptAddr = this.trimAll(obj.val());
            var desc = obj.attr("desc");
            var zjNum = psptAddr.replace(/[^\x00-\xff]/g, "**").length; //字节数

            if (psptAddr == "" || zjNum * 1 < 12) {
                $.validate.alerter.one(obj[0], desc + "栏录入文字需不少于6个汉字或12个字节！");
                obj.val("");
                obj.focus();
                return;
            }
            if ($.toollib.isNumber(psptAddr)) {
                $.validate.alerter.one(obj[0], desc + "栏不能全部为数字！");
                obj.val("");
                obj.focus();
                return;
            }

            var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
            if (strCheckUserPsptId == "CREATEUSERSW") {
                var strPsptAddr = $("#PSPT_ADDR").val();
                $("#CONTACT_ADDRESS").val(strPsptAddr);
            }
		},
        trimAll: function (str) {
            return str.replace(/(^\s+)|(\s+$)/g, "").replace(/\s/g, "");
        },
        afterChkAgentCustName: function () {
            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") {
                var agentval = $("#AGENT_CUST_NAME").val();
                //$("#RSRV_STR2").val(agentval);
            }
        },
        afterChkAgentPsptTypeCode: function () {
            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") {
                var agentval = $("#AGENT_PSPT_TYPE_CODE").val();
                //$("#RSRV_STR3").val(agentval);
            }
        },
        afterChkAgentPsptId: function () {
            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") {
                var agentval = $("#AGENT_PSPT_ID").val();
                //$("#RSRV_STR4").val(agentval);
            }
        },
        afterChkAgentPsptAddr: function () {
            var opentype = $("#OPEN_TYPE").val();
            if (opentype == "IOT_OPEN") {
                var agentval = $("#AGENT_PSPT_ADDR").val();
                //$("#RSRV_STR5").val(agentval);
            }
        },
		/*****************************以下为客户校验辅助校验部分********************************************/
		//实名制基本数据校验
        checkPsptIdForReal: function () {
            if ($("#PSPT_TYPE_CODE").val() == "Z") {
                $.validate.alerter.one($("#PSPT_TYPE_CODE")[0], "实名制开户，证件类型不能为其他，请重新选择！");
                return false;
            }
            var psptId = $("#PSPT_ID").val();
            if ($.toollib.isRepeatCode(psptId)) {
                $.validate.alerter.one($("#PSPT_ID")[0], "实名制开户，证件号码过于简单，请重新输入！");
                return false;
            }
            var psptTypeCode = $("#PSPT_TYPE_CODE").val();

            if (psptTypeCode == "0" || psptTypeCode == "1" || psptTypeCode == "2" || psptTypeCode == "3") {
                if (!$.validate.verifyField("PSPT_ID")) {
                    return false;
                }
            }
            return true;
        },
		//使用人基本数据校验
        checkUsePsptIdForReal: function () {
            if ($("#USE_PSPT_TYPE_CODE").val() == "Z") {
                $.validate.alerter.one($("#USE_PSPT_TYPE_CODE")[0], "实名制开户，使用人证件类型不能为其他，请重新选择！");
                return false;
            }
            var psptId = $("#USE_PSPT_ID").val();
            if ($.toollib.isRepeatCode(psptId)) {
                $.validate.alerter.one($("#USE_PSPT_ID")[0], "实名制开户，使用人证件号码过于简单，请重新输入！");
                return false;
            }
            if (!$.validate.verifyField("USE_PSPT_ID")) {
                return false;
            }
            return true;
        },
		//在线实名制校验
        verifyIdCard: function (fieldName) {
            var custNameObj, psptObj, psptTypeObj;
            var checkAgent='0';//检验经办人标记
            if (fieldName == "PSPT_ID" || fieldName == "CUST_NAME") {
                custNameObj = $("#CUST_NAME");
                psptObj = $("#PSPT_ID");
                psptTypeObj = $("#PSPT_TYPE_CODE");
            } else if (fieldName == "AGENT_PSPT_ID" || fieldName == "AGENT_CUST_NAME") {
                custNameObj = $("#AGENT_CUST_NAME");
                psptObj = $("#AGENT_PSPT_ID");
                psptTypeObj = $("#AGENT_PSPT_TYPE_CODE");
                checkAgent="1";
            } else if (fieldName == "USE_PSPT_ID" || fieldName == "USE") {
                custNameObj = $("#USE");
                psptObj = $("#USE_PSPT_ID");
                psptTypeObj = $("#USE_PSPT_TYPE_CODE");
            } else if (fieldName == "FORM_PSPT_ID") {
                //人像比对
                custNameObj = $("#FORM_CUST_NAME");
                psptObj = $("#FORM_PSPT_ID");
                psptTypeObj = $("#FORM_PSPT_TYPE_CODE");
            } else if (fieldName === "NETCHOOSE_PSPT_ID") { // 选号页面扫描证件后校验
                custNameObj = $("#SCAN_CUST_NAME");
                psptObj = $("#NETCHOOSE_PSPT_ID");
                psptTypeObj = $("#NETCHOOSE_PSPT_TYPE_CODE");
            } else if (fieldName == "RSRV_STR4" || fieldName == "RSRV_STR2"){//责任人信息
                custNameObj = $("#RSRV_STR2");
                psptObj = $("#RSRV_STR4");
                psptTypeObj = $("#RSRV_STR3");
            }
            var psptId = $.trim(psptObj.val());
            var psptName = $.trim(custNameObj.val());
            if (psptId == "" || psptName == "") {
                return false;
            }

            /**
             * REQ201706130001_关于录入联网核验情况的需求
             * @author zhuoyingzhi
             * @date 20170921
             */
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if ((typeof tradeTypeCode == "undefined" || tradeTypeCode == "undefined")
                || (typeof serialNumber == "undefined" || serialNumber == "undefined")) {
                var selectedTradeTypeCode = $("#SELECTED_TRADE_TYPE_CODE").val(); // 获取开户业务类型
                if (selectedTradeTypeCode == "10" || selectedTradeTypeCode == "40") { // 开户和携入开户
                    tradeTypeCode = selectedTradeTypeCode;
                    serialNumber = $("#SERIAL_NUMBER").val();
                }
            }
            
            //关于限制代办入网的需求
            if ((tradeTypeCode=='100' || tradeTypeCode=='10' || tradeTypeCode=='40')
            		&&(fieldName == "PSPT_ID"|| fieldName == "CUST_NAME")) {
            	var cust_age = this.jsGetAge($.trim(psptId));
                if (cust_age < 16) {
                    MessageBox.error("告警提示", "该户主为16周岁以下，请代办人出示其为法定监护人的相关凭证！", null, null, null, null);
                }
            }
            var cust_age = this.jsGetAge($("#PSPT_ID").val());
            var param = "&CERT_ID=" + psptId + "&CERT_TYPE=" + $.trim(psptTypeObj.val())
                + "&CERT_NAME=" + encodeURIComponent(psptName) + "&SERIAL_NUMBER=" + serialNumber
                + "&TRADE_TYPE_CODE=" + tradeTypeCode+"&checkAgent="+checkAgent+"&CUST_AGE="+cust_age;
            $.beginPageLoading("在线实名制校验。。。");
          //是否联网验证真实性 0:否1:是
			$("#custInfo_AUTHENTICITY_IS").val(1);
			$("#custInfo_AUTHENTICITY_RUSLT").val(0);//验证结果是否通过1：是，0：否
            $.httphandler.get(this.clazz, "verifyIdCard", param,
                function (data) {
                    $.endPageLoading();
                    //获取经办人一个自然月和一年内经办人证件号的数量
                    var cPsptTypeCode = $("#PSPT_TYPE_CODE").val();
                    if(!(cPsptTypeCode =="D" || cPsptTypeCode =="E" || cPsptTypeCode =="G"
           			 || cPsptTypeCode =="M" || cPsptTypeCode =="L")){
	                    if((tradeTypeCode=='100' ||tradeTypeCode=='10' ||tradeTypeCode=='40')
	                    		&&checkAgent=='1'){
	            			if(data && data.get("CODE2")=='-1'){
	            				MessageBox.error("告警提示",data.get("RESULT_INFO"),null, null, null, null);
	            				$("#AGENT_PSPT_ID").val("");
	            				return false;
	            			}
	                    }
                    }
                    if (data && data.get("X_RESULTCODE") == "1") {
                        MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                        psptObj.val("");
                        return false;
                    } else if (data && data.get("X_RESULTCODE") == "2") {
                    	//验证结果是否通过1：是，0：否
            			$("#custInfo_AUTHENTICITY_RUSLT").val(1);
            			$("#custInfo_AUTHENTICITY_SEQ").val(data.get("SEQ"));//验证流水
                        MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                        return true;
                    } else {
                    	//验证结果是否通过1：是，0：否
            			$("#custInfo_AUTHENTICITY_RUSLT").val(1);
            			$("#custInfo_AUTHENTICITY_SEQ").val(data.get("SEQ"));//验证流水
                        return true;
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "在线实名制校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "在线实名制校验失败");
                });

		},
		// 营业执照、组织机构代码证、事业单位法人登记证书不能一个证件号码对应多个不同的单位名称。
        verifyIdCardName: function (fieldName) {
            var obj = $("#" + fieldName);
            var checkAgent="0";//检验经办人标记
            var custNameObj, psptObj, psptcodeObj;
            if (fieldName == "PSPT_ID" || fieldName == "CUST_NAME") {
                custNameObj = $("#CUST_NAME");
                psptObj = $("#PSPT_ID");
                psptcodeObj = $("#PSPT_TYPE_CODE");
            } else if (fieldName == "AGENT_PSPT_ID" || fieldName == "AGENT_CUST_NAME") {
                custNameObj = $("#AGENT_CUST_NAME");
                psptObj = $("#AGENT_PSPT_ID");
                psptcodeObj = $("#AGENT_PSPT_TYPE_CODE");
                checkAgent="1";
            } else if (fieldName == "USE_PSPT_ID" || fieldName == "USE") {
                psptcodeObj = $("#USE_PSPT_TYPE_CODE");
                custNameObj = $("#USE");
                psptObj = $("#USE_PSPT_ID");
            } else if (fieldName === "NETCHOOSE_PSPT_ID") { // 选号页面扫描证件后校验
                custNameObj = $("#SCAN_CUST_NAME");
                psptObj = $("#NETCHOOSE_PSPT_ID");
                psptcodeObj = $("#NETCHOOSE_PSPT_TYPE_CODE");
            } else  if (fieldName == "RSRV_STR4" || fieldName == "RSRV_STR2") {//责任人信息
                psptcodeObj = $("#RSRV_STR3");
                custNameObj = $("#RSRV_STR2");
                psptObj = $("#RSRV_STR4");
            }
            var psptId = $.trim(psptObj.val());
            var psptName = $.trim(custNameObj.val());
            var psptcode = $.trim(psptcodeObj.val());
            if (psptId == "" || psptName == "" || psptcode == "") {
                return false;
            }
            
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if ((typeof tradeTypeCode == "undefined" || tradeTypeCode == "undefined")
                || (typeof serialNumber == "undefined" || serialNumber == "undefined")) {
                var selectedTradeTypeCode = $("#SELECTED_TRADE_TYPE_CODE").val(); // 获取开户业务类型
                if (selectedTradeTypeCode == "10" || selectedTradeTypeCode == "40") { // 开户和携入开户
                    tradeTypeCode = selectedTradeTypeCode;
                    serialNumber = $("#SERIAL_NUMBER").val();
                }
            }
            
            var cust_age = this.jsGetAge($("#PSPT_ID").val());
            
		    //REQ201701160002关于集团客户界面优化需求
            var param = "&CERT_TYPE=" + psptcode + "&CERT_ID=" + psptId + "&CERT_NAME=" + encodeURIComponent(psptName)+"&SERIAL_NUMBER=" + serialNumber+"&checkAgent=" + checkAgent + "&CUST_AGE=" + cust_age; 		    
            
		    if(psptcode=="0"||psptcode=="1"||psptcode=="2"||psptcode=="3"){//《一证多名需求》，除了本地外地身份证和户口军人身份证，其他证件都不允许一证多名
                var opentype = $("#OPEN_TYPE").val();
                if (opentype == "IOT_OPEN") { //物联网开户
                    /**
                     * REQ201804090009_新增物联网开户、变更界面单位证件开户一证多名限制需求
                     * 取消物联网号码本地一证五号的判断
                     * 为了保存判断逻辑，所以不删除if判断语句
                     * @author zhuoyingzhi
                     * @date 20180507
                     */
                    return true;
                } else {
                    return true;
                }
            } else {
                $.beginPageLoading("单位名称校验。。。");
                $.httphandler.get(this.clazz, "verifyIdCardName", param,
                    function (data) {
                        $.endPageLoading();
                        
                      //获取经办人一个自然月和一年内经办人证件号的数量(校验护照)
                        var cPsptTypeCode = $("#PSPT_TYPE_CODE").val();
                        if(!(cPsptTypeCode =="D" || cPsptTypeCode =="E" || cPsptTypeCode =="G"
               			 || cPsptTypeCode =="M" || cPsptTypeCode =="L")){
    	                    if((tradeTypeCode=='100' ||tradeTypeCode=='10' ||tradeTypeCode=='40')
    	                    		&&checkAgent=='1'){
    	            			if(data && data.get("CODE2")=='-1'){
    	            				MessageBox.error("告警提示",data.get("RESULT_INFO"),null, null, null, null);
    	            				$("#AGENT_PSPT_ID").val("");
    	            				return false;
    	            			}
    	                    }
                        }
                        
                        if (data && data.get("X_RESULTCODE") != "0") {
                            MessageBox.error("告警提示", "同一个证件号码不能对应不同的名称", null, null, null, null);
                            psptObj.val("");
                            return false;
                        } else {
                            verifyEnterpriseCard();
                            //REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
                            //verifyOrgCard();
                        }
                    }, function (code, info, detail) {
                        $.endPageLoading();
                        MessageBox.error("错误提示", "【同一个证件号码不能对应不同的单位名称】校验获取后台数据错误！", null, null, info, detail);
                    }, function () {
                        $.endPageLoading();
                        MessageBox.alert("告警提示", "【同一个证件号码不能对应不同的单位名称】校验失败");
                    });
            }
		  //REQ201701160002关于集团客户界面优化需求
		},
		// 营业执照在线校验
        verifyEnterpriseCard: function () {
            var psptcode = $.trim($("#PSPT_TYPE_CODE").val());
            var psptId = $.trim($("#PSPT_ID").val());
            var psptName = $.trim($("#CUST_NAME").val());
            var legalperson = $.trim($("#legalperson").val());
            var termstartdate = $.trim($("#termstartdate").val());
            var termenddate = $.trim($("#termenddate").val());
            var startdate = $.trim($("#startdate").val());

            //营业执照
            if (psptcode != "E") {
                return false;
            }
            if (psptId == "" || psptName == "" || psptcode == "" || legalperson == "" || termstartdate == "" || termenddate == "" || startdate == "") {
                return false;
            }
            
            /**
             * REQ201706130001_关于录入联网核验情况的需求
             * @author zhuoyingzhi
             * @date 20170921
             */
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if ((typeof tradeTypeCode == "undefined" || tradeTypeCode == "undefined")
                || (typeof serialNumber == "undefined" || serialNumber == "undefined")) {
                var selectedTradeTypeCode = $("#SELECTED_TRADE_TYPE_CODE").val(); // 获取开户业务类型
                if (selectedTradeTypeCode == "10" || selectedTradeTypeCode == "40") { // 开户和携入开户
                    tradeTypeCode = selectedTradeTypeCode;
                    serialNumber = $("#SERIAL_NUMBER").val();
                }
            }

            var param = "&regitNo=" + psptId + "&enterpriseName=" + encodeURIComponent(psptName) + "&legalperson=" + encodeURIComponent(legalperson)
                + "&termstartdate=" + encodeURIComponent(termstartdate) + "&termenddate=" + encodeURIComponent(termenddate) + "&startdate=" + encodeURIComponent(startdate)
                + "&SERIAL_NUMBER=" + serialNumber + "&TRADE_TYPE_CODE=" + tradeTypeCode;

            $.beginPageLoading("营业执照校验。。。");
            $.httphandler.get(this.clazz, "verifyEnterpriseCard", param,
                function (data) {
                    $.endPageLoading();
                    if (data && data.get("X_RESULTCODE") != "0") {
                        MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                        $("#PSPT_ID").val('');
                        return false;
                    } else {
                        return true;
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "校验失败");
                });
        },

		// 营业执照在线校验
        verifyOrgCard: function () {
        	//REQ201908150006-申请优化单位证件开户及校验的需求-guonj-去掉验证
            /*var psptcode = $.trim($("#PSPT_TYPE_CODE").val());
            var psptId = $.trim($("#PSPT_ID").val());
            var psptName = $.trim($("#CUST_NAME").val());
            var orgtype = $.trim($("#orgtype").val());
            var effectiveDate = $.trim($("#effectiveDate").val());
            var expirationDate = $.trim($("#expirationDate").val());

            //组织机构代码证
            if (psptcode != "M") {
                return false;
            }

            if (psptId == "" || psptName == "" || psptcode == "" || orgtype == "" || effectiveDate == "" || expirationDate == "") {
                return false;
            }

            var param = "&orgCode=" + psptId + "&orgName=" + encodeURIComponent(psptName) + "&orgtype=" + encodeURIComponent(orgtype)
                + "&effectiveDate=" + encodeURIComponent(effectiveDate) + "&expirationDate=" + encodeURIComponent(expirationDate);

            $.beginPageLoading("组织机构代码证校验。。。");
            $.httphandler.get(this.clazz, "verifyOrgCard", param,
                function (data) {
                    $.endPageLoading();
                    if (data && data.get("X_RESULTCODE") != "0") {
                        MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                        $("#PSPT_ID").val('');
                        return false;
                    } else {
                        return true;
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "校验失败");
                });*/
        },
        checkAge: function (idCard) {
            if (!idCard) {
                return false;
            }
            var _age = this.jsGetAgeNew(idCard);
            return _age>=16 && _age<=120;
        },
	/**
		 * REQ201808100006	关于调整实名制相关规则的需求 by mqx 20180823
		 * 要求精确到天
		 * @param idCard
		 * @return
		 */
		jsGetAgeNew:function(idCard){				 
		    var returnAge;
			var bstr = idCard.substring(6,14)			 
		    var birthYear = bstr.substring(0,4);
		    var birthMonth = bstr.substring(4,6);
		    var birthDay = bstr.substring(6,8);
		    
		    var d = new Date();
		    var nowYear = d.getFullYear();
		    var nowMonth = d.getMonth() + 1;
		    var nowDay = d.getDate();
		    
		    if(nowYear == birthYear)
		    {
		        returnAge = 0;//同年 则为0岁
		    }
		    else
		    {
		        var ageDiff = nowYear - birthYear ; //年之差
		        if(ageDiff > 0)
		        {
		            if(nowMonth == birthMonth)
		            {
		                var dayDiff = nowDay - birthDay;//日之差
		                if(dayDiff < 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		            }
		            else
		            { 
		                var monthDiff = nowMonth - birthMonth;//月之差
		                if(monthDiff <= 0)
		                {
		                    returnAge = ageDiff - 1;
		                }
		                else
		                {
		                    returnAge = ageDiff ;
		                }
		            }
		        }
		        else
		        {
		            returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
		        }
		    }
		    return returnAge;//返回周岁年龄		    
		},		
        jsGetAge: function (idCard) {
            var returnAge;
            var bstr = idCard.substring(6, 14)
            var birthYear = bstr.substring(0, 4);
            var birthMonth = bstr.substring(4, 6);

            var d = new Date();
            var nowYear = d.getFullYear();
            var nowMonth = d.getMonth() + 1;

            if (nowYear == birthYear) {
                returnAge = 0;//同年 则为0岁
            } else {
                var ageDiff = nowYear - birthYear; //年之差
                if (ageDiff > 0) {
                    var monthDiff = nowMonth - birthMonth;//月之差
                    if (monthDiff <= 0) {
                        returnAge = ageDiff - 1;
                    } else {
                        returnAge = ageDiff;
                    }
                } else {
                    returnAge = -1;//返回-1 表示出生日期输入错误 晚于今天
                }
            }
            return returnAge;//返回周岁年龄
        },
        setMailValue: function () {
            var $serialNumber = $("#SERIAL_NUMBER"),
                $postCheckbox = $("#postCheckbox"),
                $email = $("#POST_CYC").prev();
            if ($postCheckbox.attr("checked")) {
                if (this.serialNumber != null) {
                    $postCheckbox.val("1");
                    $email.val(this.serialNumber + "@139.com");
                } else {
                    $.validate.alerter.one($serialNumber[0], "请先输入服务号码！");
                    $serialNumber.focus();
                    $postCheckbox.val("0").attr("checked", false);
                    $email.val("");
                }
            }
        },
		//检查同一证件号已开实名制用户的数量是否已超出预定值
        checkRealNameLimitByPspt: function () {
            var custName = $("#CUST_NAME").val();
            var psptId = $("#PSPT_ID").val();
            /**
             * REQ201611180016 关于特殊调整我公司营业执照开户使用人不限制5户的需求
             * chenxy3 新增传送证件类型
             * */
            var psptTypeCode = $("#PSPT_TYPE_CODE").val();
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            var param = "";
            if ('680' == blackTradeType) {
                if (psptId == "") {
                    return false;
                }
                param = "&PSPT_ID=" + psptId + "&EPARCHY_CODE=" + $("#CustInfoCmp").attr("eparchyCode");
            } else {
                if (custName == "" || psptId == "") {
                    return false;
                }
                param = "&CUST_NAME=" + encodeURIComponent(custName) + "&PSPT_TYPE_CODE=" + psptTypeCode + "&PSPT_ID=" + psptId + "&EPARCHY_CODE=" + $("#CustInfoCmp").attr("eparchyCode");
            }
		    //如果没有设置则取默认服务名
            var strCheckUserPsptId = $("#CHECK_USER_PSPTID").val();
            if (strCheckUserPsptId == "CREATEUSERSW") {
                param += "&NET_TYPE_CODE=12";
                this.realNameSVCName = "SS.CreatePersonUserSVC.checkRealNameLimitByPsptCtt";
            }
            
		    //无手机宽带开户实名制开户限制
            if ('680' == blackTradeType) {
                this.realNameSVCName = "SS.NoPhoneWideUserCreateSVC.checkRealNameLimitByPspt";
            }
            
            /*
             * 优化单位证件开户阀值权限设置需求
             * OPEN_TYPE=IOT_OPEN，物联网
             * mengqx
             */
            var opentype = $("#OPEN_TYPE").val();
            //单位证件
            if(psptTypeCode == "D"||psptTypeCode == "E"
    	    	||psptTypeCode == "G"||psptTypeCode == "L"||psptTypeCode == "M"){
            	
            	if (blackTradeType === "10" || blackTradeType === "40") {
            		if (opentype != "IOT_OPEN") {
            			//非物联网开户
            			param += "&USER_TYPE=0";
            		}else{
            			//物联网开户
            			param += "&USER_TYPE=1";
            		}
            	}
            }
            
            
            if (this.realNameSVCName) {
                param += "&SVC_NAME=" + this.realNameSVCName;
            }
            
            var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            if ((typeof tradeTypeCode == "undefined" || tradeTypeCode == "undefined")
                || (typeof serialNumber == "undefined" || serialNumber == "undefined")) {
                var selectedTradeTypeCode = $("#SELECTED_TRADE_TYPE_CODE").val(); // 获取开户业务类型
                if (selectedTradeTypeCode == "10" || selectedTradeTypeCode == "40") { // 开户和携入开户
                    tradeTypeCode = selectedTradeTypeCode;
                    serialNumber = $("#SERIAL_NUMBER").val();
                }
            }
            param +="&SERIAL_NUMBER=" + serialNumber ;//用于判断是否物联网号码
            
            $.beginPageLoading("实名制用户开户数量校验。。。");
            $.httphandler.get(this.clazz, "checkRealNameLimitByPspt", param,
                function (data) {
                    $.endPageLoading();
                    if (data && data.get("CODE") != "0") {
                        $("#REALNAME_LIMIT_CHECK_RESULT").val("false");
                        $("#CUST_NAME").val("");
                        $("#PSPT_ID").val("");
                        MessageBox.alert(data.get("MSG"));
                        if ('680' == blackTradeType) {
                            $("#PSPT_ID").val("");
                        }
                        return;
                    } else {
                        $("#REALNAME_LIMIT_CHECK_RESULT").val("true");
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "实名制用户开户数量校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "实名制用户开户数量校验超时");
                });
		},
		//开户-身份证判定是否黑名单用户
        checkPsptidBlackListInfo: function () {
            var psptId = $("#PSPT_ID").val();
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if (psptId == "") {
                return false;
            }
            var param = "&PSPT_ID=" + psptId + "&BLACK_TRADE_TYPE=" + blackTradeType;
            $.beginPageLoading("判定是否黑名单用户。。。");
            $.httphandler.get(this.clazz, "checkPsptidBlackListInfo", param,
                function (data) {
                    $.endPageLoading();
                    if (data.get("BLACKCODE") != "0") {
                        MessageBox.error("告警提示", data.get("BLACKMSG"), null, null, null, null);
                        $("#PSPT_ID").val("");
                        return false;
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "黑名单用户校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "黑名单用户校验超时");
                });
        },
        //无手机宽带开户-判断是否有线上预约工单
        checkTradeBookInfo: function () {
            var psptId = $("#PSPT_ID").val();
            var blackTradeType = $("#BLACK_TRADE_TYPE").val();
            if ("680" != blackTradeType) {
                return ;
            }
            if (psptId == "") {
                return ;
            }
            var param = "&PSPT_ID=" + psptId + "&BLACK_TRADE_TYPE=" + blackTradeType;
            $.beginPageLoading("判定是否有线上无手机宽带预约开户业务。。。");
            $.httphandler.get(this.clazz, "checkTradeBookInfo", param,
                function (data) {
                    $.endPageLoading();
                    if (data.get("BOOKTAG") == "1") {
                    	$("#BOOKTAG").val("1");
                        MessageBox.alert("告警提示", "线上无手机宽带预约开户，系统将对其免人像比对及先装后付！");
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "线上无手机宽带预约开户获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "线上无手机宽带预约开户校验超时");
                });
        },
		//检查同一证件号已开实名制用户的数量是否已超出预定值
        checkRealNameLimitByUsePspt: function () {
            var custName = $("#USE").val();
            var psptTypeCode = $("#PSPT_TYPE_CODE").val();
            var psptId = $("#USE_PSPT_ID").val();
            if (custName == "" || psptId == "") {
                return false;
            }
            //start--特殊调整我公司营业执照开户使用人不限制5户--2019-07-12--wangsc
            var khcustName = $("#CUST_NAME").val();
            var khpsptId = $("#PSPT_ID").val();
            if (khcustName == "中国移动通信集团海南有限公司" && khpsptId == "91460000710920952X" && psptTypeCode == "E") {
                return false;
            }
            //end
            
            var param = "&CUST_NAME=" + encodeURIComponent(custName) + "&PSPT_ID=" + psptId + "&EPARCHY_CODE=" + $("#CustInfoCmp").attr("eparchyCode") + "&PSPT_TYPE_CODE=" + psptTypeCode;
            //如果没有设置则取默认服务名
            /*if(this.realNameSVCName){
                param += "&SVC_NAME="+this.realNameSVCName;
            }*/
            param += "&SVC_NAME=SS.CreatePersonUserSVC.checkRealNameLimitByUsePspt";
            $.beginPageLoading("使用人证件信息数量校验。。。");
            $.httphandler.get(this.clazz, "checkRealNameLimitByPspt", param,
                function (data) {
                    $.endPageLoading();
                    if (data && data.get("CODE") != "0") {
                        $("#REALNAME_LIMIT_CHECK_RESULT").val("false");
                        MessageBox.alert(data.get("MSG"));
                        return;
                    } else {
                        $("#REALNAME_LIMIT_CHECK_RESULT").val("true");
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    MessageBox.error("错误提示", "使用人证件信息数量校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "使用人证件信息数量校验超时");
                });
        },

		//全网一证五号检查
        checkGlobalMorePsptId: function (objId) {
            //不同的入口调用，判断请求入库是否需进行全国1证5号校验。在请求页面加入 <input jwcid="@Hidden" name="checkGlobalMorePsptIdFlag" id="checkGlobalMorePsptIdFlag" value="true" desc="是否进行全国1证5号校验"/>
            var globalTradeTypeCode = $("#checkGlobalMorePsptIdFlag").val();
            if (typeof(globalTradeTypeCode) == "undefined" || globalTradeTypeCode != "true") {
                return;
            }
            var opentype = $("#OPEN_TYPE").val();
            //非物联网开户才处理
            if (typeof(opentype) == 'undefined' || opentype == "undefined" || opentype != "IOT_OPEN") {
                //如是测试机用户,A， 开户人、使用人、经办人证件都不进行全国一证多号校验
                var userTypeCode = $("#USER_TYPE_CODE").val();
                if (typeof(userTypeCode) == "undefined" || userTypeCode == "undefined") {
                } else {
                    if (userTypeCode == 'A') {//测试机用户
                        return true;
                    }
                }
            }

            //全网1证5号需求， 开户证件、使用人证件校验
            var custName = "";
            var psptId = "";
            var psptTypeCode = "";
            var clearPsptId = "";
            if (objId == "USE_PSPT_ID" || objId == "USE" || objId == "USE_PSPT_TYPE_CODE") {
                custName = $("#USE").val();
                psptId = $("#USE_PSPT_ID").val();
                psptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
                clearPsptId = "#USE_PSPT_ID";
            } else if (objId == "PSPT_ID" || objId == "CUST_NAME" || objId == "PSPT_TYPE_CODE") {
                custName = $("#CUST_NAME").val();
                psptId = $("#PSPT_ID").val();
                psptTypeCode = $("#PSPT_TYPE_CODE").val();
                clearPsptId = "#PSPT_ID";
            }
            if (custName == "" || psptId == "" || psptTypeCode == "") {
                return false;
            }

            var param = "&CUST_NAME=" + encodeURIComponent(custName) + "&PSPT_ID=" + psptId + "&PSPT_TYPE_CODE=" + psptTypeCode;
            var tradeTypeCode = $("#TRADE_TYPE_CODE").val();
            var authSerialNumber = $("#AUTH_SERIAL_NUMBER").val();
            //过户100业务类型需传递 TRADE_TYPE_CODE， AUTH_SERIAL_NUMBER参数， 以便后台判断是否号码cityCode为HNHN或HNSJ,不进行全国一证多号校验
            if ((typeof(tradeTypeCode) == "undefined" || tradeTypeCode == "undefined") || (typeof(authSerialNumber) == "undefined" || authSerialNumber == "undefined")) {

            } else {
                if (tradeTypeCode == "100") {//过户，需传递
                    param += "&TRADE_TYPE_CODE=" + tradeTypeCode;
                    param += "&SERIAL_NUMBER=" + authSerialNumber;
                }
            }

            $.beginPageLoading("全网证件信息数量校验。。。");
            //是否进行一证五号查验 0:否1:是
            $("#custInfo_PROVENUM_IS").val(1);
            $("#custInfo_PROVENUM_RUSLT").val(0);
            $.httphandler.get(this.clazz, "checkGlobalMorePsptId", param,
                function (data) {
                    $.endPageLoading();
                    if (data && data.get("CODE") != "0") {
                        /**
                         * REQ201709250007_全网一证多名返回优化
                         * @author zhuoyingzhi
                         * @date 20171017
                         */
                        if (data.get("CODE") != "3") {
                            $(clearPsptId).val("");
                        }
                        /**********end**************/
                        MessageBox.alert(data.get("MSG"));
                    }else{
                    	//是否进行一证五号查验结果 1：是，0：否
        				$("#custInfo_PROVENUM_RUSLT").val(1);
        				$("#custInfo_PROVENUM_SEQ").val(data.get("SEQ"));//一证五号查验结果流水
                    }
                    
                  //单个证件入网当月一证2户数量限制功能
                    var tradeTypeCode1 = $("#BLACK_TRADE_TYPE").val();
                    if(data && (tradeTypeCode=="100" || tradeTypeCode1=="10")){
                    	if(data.get("CODE2")=="0"){
            				$.MessageBox.confirm("确认提示",data.get("MSG2"),function(re){
            					if(re=="ok"){
            					}else{
            						$(clearPsptId).val("");
            						return false;
            					}
            				});
            			}
                    }
                }, function (code, info, detail) {
                    $.endPageLoading();
                    $(clearPsptId).val("");
                    MessageBox.error("错误提示", "全网证件信息数量校验获取后台数据错误！", null, null, info, detail);
                }, function () {
                    $.endPageLoading();
                    MessageBox.alert("告警提示", "全网证件信息数量校验超时");
                });
        },

        // 省内无线固话一证五号检查  REQ202001210005_关于优化TD无线固话业务一证五号验证的需求
        checkProvinceMorePsptId: function(objId){
            // 业务类型为：无线固话开户、过户业务 进行省内固话一证五号检查
            var flag = false;

            var tradeTypeCode = $("#SELECTED_TRADE_TYPE_CODE").val();
            if ((tradeTypeCode && tradeTypeCode== "3820")){// 无线固话开户,需要校验
                flag = true;
            }
            var trade_type_code = $("#TRADE_TYPE_CODE").val();
            if (trade_type_code == "100"){//过户
                var obj = $.auth.getAuthData();
                var brandCode = "";
                if (obj){
                    brandCode =obj.get("USER_INFO").get("BRAND_CODE");
                }
                if(brandCode =="TDYD"){// 无线固话过户
                    flag = true;
                }
            }

            if(flag){// 无线固话开户、无线固话过户会调用到这个方法
                var custName = "";
                var psptId = "";
                var psptTypeCode = "";
                var clearPsptId = "";

                // 针对开户证件、使用人证件进行省内一证五号的校验
                if (objId == "USE_PSPT_ID" || objId == "USE" || objId == "USE_PSPT_TYPE_CODE") {
                    custName = $("#USE").val();
                    psptId = $("#USE_PSPT_ID").val();
                    psptTypeCode = $("#USE_PSPT_TYPE_CODE").val();
                    clearPsptId = "#USE_PSPT_ID";
                } else if (objId == "PSPT_ID" || objId == "CUST_NAME" || objId == "PSPT_TYPE_CODE") {
                    custName = $("#CUST_NAME").val();
                    psptId = $("#PSPT_ID").val();
                    psptTypeCode = $("#PSPT_TYPE_CODE").val();
                    clearPsptId = "#PSPT_ID";
                }
                // 当用户名、证件号、证件类型 都填写完成才能传到后台进行校验。
                if (custName == "" || psptId == "" || psptTypeCode == "") {
                    return false;
                }
                // 参数准备
                var param = "&CUST_NAME=" + encodeURIComponent(custName) + "&PSPT_ID=" + psptId + "&PSPT_TYPE_CODE=" + psptTypeCode;

                $.beginPageLoading("省内证件信息数量校验。。。");
                $.httphandler.get(this.clazz, "checkProvinceMorePsptId", param,
                    function (data) {
                        $.endPageLoading();
                        if (data && data.get("CODE") != "0") {// 校验不通过，提示对应的内容
                            if(data && data.get("CODE") == "2"){// 正常状态，仅提示不限制
                                ;
                            }else {
                                // 清空证件号,限制
                                $(clearPsptId).val("");
                            }
                            MessageBox.alert(data.get("MSG"));
                        }

                        //单个证件入网当月一证2户数量限制功能
                        if ((tradeTypeCode && tradeTypeCode== "3820") || (trade_type_code && trade_type_code==100)){
                            if(data.get("CODE2")=="0"){
                                $.MessageBox.confirm("确认提示",data.get("MSG2"),function(re){
                                    if(re=="ok"){
                                    }else{
                                        $(clearPsptId).val("");
                                        return false;
                                    }
                                });
                            }
                        }

                    }, function (code, info, detail) {
                        $.endPageLoading();
                        $(clearPsptId).val("");
                        MessageBox.error("错误提示", "省内证件信息数量校验获取后台数据错误！", null, null, info, detail);
                    }, function () {
                        $.endPageLoading();
                        MessageBox.alert("告警提示", "省内证件信息数量校验超时");
                    });
            }
        },

        // 下拉框选择index=0的选项
        selectDefaultValue: function (id) {
            var val = $("#" + id + "_float li:eq(0)").addClass("on").attr("val");
            $("#" + id).val(val);
        },

        // 校验手机号码后自动填充联系号码、账户类型
        setDefaultValueAfterSerialNumberCheck: function () {
            var tradeTypeCode = $("#BLACK_TRADE_TYPE").val(),
                openType = $("#OPEN_TYPE").val();
            if (tradeTypeCode === "10" || tradeTypeCode === "40") {
                if (openType !== "IOT_OPEN") {
                    $("#PHONE").val(this.serialNumber);
                }
                $("#PAY_MODE_CODE").val("0");
            }
        },

        // 根据选择的证件类型切换证件扫描区域和手工输入区域
        togglePsptScanAndInputArea: function (fieldName) {
            if (fieldName === "PSPT_TYPE_CODE") {
                var psptTypeCode = $("#PSPT_TYPE_CODE").val();
                // 仅本地身份证、外地身份证和军人身份证显示身份证扫描区域
                if (psptTypeCode === "0" || psptTypeCode === "1" || psptTypeCode === "3") {
                    $("#SHOW_SCAN_AREA").trigger("click");
                } else {
                    $("#SHOW_EDIT_AREA").trigger("click");
                }
            }
        },
        /**
         * BUG20180316152525_实名制相关业务控制优化bug
         * 需要填写时,对应的证件类型才默认选择 本地身份证
         * @author zhuoyingzhi
         * @date 20180420
         */
        isSelectedUseAndAgent: function (psptTypeCode, fieldName) {
            var openType = $("#OPEN_TYPE").val(); // 判断是否为物联网标识

            if ((psptTypeCode == 'D' || psptTypeCode == 'E' || psptTypeCode == 'G' || psptTypeCode == 'L' || psptTypeCode == 'M')
                && fieldName == 'PSPT_TYPE_CODE') { // 客户证件类型变化时
                // 默认选择，单位证件：单位证明-D 营业执照-E 事业单位法人证书-G 社会团体法人登记证书-L 组织机构代码证-M
                $("#AGENT_PSPT_TYPE_CODE").val("0");
                if (openType === "IOT_OPEN") {
                    // 物联网开户，责任人必填
                    $("#RSRV_STR3").val("0");
                } else {
                    $("#USE_PSPT_TYPE_CODE").val("0");
                }
            }
            if ((psptTypeCode == 0 || psptTypeCode == 1 || psptTypeCode == 2 || psptTypeCode == 3)
                && fieldName == 'PSPT_ID') { // 客户证件号码变化时
                var psptId = $("#PSPT_ID").val(); // 个人证件
                if ($.trim(psptId) !== "") {
                    if (!this.checkAge(psptId)) {
                        // 如果用户年龄不在11<age<=120，则经办人的证件类型默认选择本地身份证
                        $("#AGENT_PSPT_TYPE_CODE").val("0");
                    }
                }
            }
        },
        /**
         * REQ201909040010在和家固话实名认证环节增加校验客户的固话开户实名信息与手机号码实名信息—BOSS侧
         * 家庭IMS固话开户(新),在界面进行人像对比后，所获取的证件号码和姓名，要与界面输入的手机号码对应的证件号码和姓名进行对比，信息一致才能提交办理
         * mengqx 20190912
         */
        checkIMSPhoneCustInfo: function (){
        	var blackTradeType = $("#BLACK_TRADE_TYPE").val();
        	if(blackTradeType == '6800'||blackTradeType == '600'){
        		var serialNumber = $("#AUTH_SERIAL_NUMBER").val();
        		var custName = $("#CUST_NAME").val();
        		var psptId = $("#PSPT_ID").val();
        		var param = "&CUST_NAME=" + encodeURIComponent(custName) + "&PSPT_ID=" + psptId + "&SERIAL_NUMBER=" + serialNumber;
        		$.beginPageLoading("开户信息与代付手机号码身份信息校验。。。");
                $.httphandler.get(this.clazz, "checkIMSPhoneCustInfo", param,
                    function (data) {
                        $.endPageLoading();
                        if (data && data.get("CODE") != "0") {
                        	$("#PSPT_ID").val('');
                        	$("#CUST_NAME").val('');
                            MessageBox.alert(data.get("MSG"));
                            return false;
                        }
                    }, function (code, info, detail) {
                        $.endPageLoading();
                        $("#PSPT_ID").val('');
                        $("#CUST_NAME").val('');
                        MessageBox.error("错误提示", "代付手机号码身份信息获取后台数据错误！", null, null, info, detail);
                    }, function () {
                        $.endPageLoading();
                        MessageBox.alert("告警提示", "身份信息校验超时");
                    });
        	}
        }
	}});
	$.extend($.custInfo, BaseInfoLib);
})(Wade);