//页面关闭时调用释放资源流程
window.onunload = function () {
    var newSimCardNo = $("#NEW_SIM_CARD_NO").val();
    $.ajax.submit(null, 'releaseSingleRes', '&SIM_CARD_NO=' + newSimCardNo, null, function (data) {
    });
}
function refreshPartAtferAuth(data)
{
	var netTypeCode = data.get("USER_INFO").get("NET_TYPE_CODE");
	//uca客户名称
	var custName = data.get("CUST_INFO").get("CUST_NAME");
	//效验方式
	var checkMode=data.get("CHECK_MODE");
	
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();

	//补换卡类型---（0=补卡 1=换卡）
	var remoteCardType=$("#REMOTECARD_TYPE").val();

    var psptTypeCode = data.get("CUST_INFO").get("PSPT_TYPE_CODE");

	$.beginPageLoading();
	$.ajax.submit('AuthPart', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString(), 'userInfoPart,NewCardInfo,hiddenInfoPart', function(data){
		$.endPageLoading();
		/**
		 * REQ201705270006_关于人像比对业务优化需求
		 * @author zhuoyingzhi
		 * @date 20170703
		 */
		$("#UCA_CUST_NAME").val(custName);
		$("#AUTH_CHECK_MODE").val(checkMode);
		$("#custInfo_PSPT_TYPE_CODE").val(psptTypeCode);
		/********************************/
		//add by yangxd3 商务电话补换卡将AUTH_SERIAL_NUMBER换成157手机号码
		if (data && data.get("SERIAL_NUMBER_A")) {
			$("#AUTH_SERIAL_NUMBER").val(data.get("SERIAL_NUMBER_A"));
		}
		$("#OLD_SIM_CARD_INFO").val(data.toString());
		$("#USER_SVC_STATE_TAG").val(data.get("USER_SVC_STATE_TAG"));
		var userSvcStateTag = data.get("USER_SVC_STATE_TAG");
		if("true" == userSvcStateTag){
			//开机操作
			MessageBox.confirm("确认信息","该用户状态为申请停机，选择【确定】将继续换卡，并给用户办理【报开】业务！选择【取消】终止该业务受理！",function(btn){
				if(btn=="ok"){
					$("#OPEN_MOBILE_TAG").val("0");
					$("#readAwrite").attr("style","display:")
					if(netTypeCode=="18"||netTypeCode=="07"){//物联网和无线固话没有写卡操作
						$("#writeCardBtn").attr("style","display:none");
					}
				}else{
					$("#OPEN_MOBILE_TAG").val("1");
				}
			});

			//	 MessageBox.alert("提示","该用户状态为申请停机，选择[是]将继续换卡，请在换卡后引导用户办理【报开】否则将不能正常使用，[否]用户先办理【报开】后再来办理换卡业务！");
		}else{
			$("#readAwrite").attr("style","display:")
			if(netTypeCode=="18"||netTypeCode=="07"){//物联网和无线固话没有写卡操作
				$("#writeCardBtn").attr("style","display:none");
			}
		}
		
		/**
		 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
		 * @author zhuoyingzhi
		 * @date 20170805
		 */
		if(tradeTypeCode == "142" && remoteCardType =="0"){
			var isAgentRight=data.get("isAgentRight");
			if(isAgentRight == "0"){
				//有权限
			   // $("#agent_right_span").attr('disabled',false);
			}else if(isAgentRight == "1"){
				//无权限
				agent_right_span.remove("2");
			}
		}
		$("#spInfo").removeClass("e_hide");
		/*************REQ201707060009关于补卡、密码重置、复机业务优化的需求******end******************/		
	},
	function(error_code,error_info){
		$.endPageLoading();
	MessageBox.alert(error_info);
	});
}

//校验新sim卡
function verifySimCard(data) {
	$("#NEW_SIM_CARD_INFO").val(data);
	//解决ie10，11bug
    var oldSimCardMap = new $.DataMap();
    var oldSimCardTemp = $("#OLD_SIM_CARD_INFO").val().replace(/"/g,"");
	var oldSimCardArr = oldSimCardTemp.split(",");
	for(var i=0;i<oldSimCardArr.length;i++) {
        var tempArr = oldSimCardArr[i].split(":");
        oldSimCardMap.put(tempArr[0],tempArr[1]);
	}
    $("#OLD_SIM_CARD_INFO").val(oldSimCardMap);
	$.feeMgr.clearFeeList();
	$.beginPageLoading();
	$.ajax.submit('AuthPart,userInfoPart,hiddenInfoPart','verifySimCard','&WRITE_TAG='+data.get("WRITE_TAG"),'NewCardInfo',function(data){
		$.endPageLoading();
		$("#IS_CHECKED").val("0");
		$("#NEW_SIM_CARD_NO").val($("#SIM_CARD_NO").val());
		var feeInfo = data.get("FEE_DATA").get("FEE_INFO");
		if(feeInfo != ""){
			$("#ALERT_INFO").text(data.get("FEE_DATA").get("FEE_INFO"));
			$("#ALERT_INFO_DIV").css("display","block");
		}
		//检查是否是4G卡
//		var is4g = data.get("IS_4G");
//		if (is4g == '1'){
//			MessageBox.alert("提示信息",data.get('NOTICE_CONTENT'));
//		}
		if(data.get("FEE_DATA").get("FEE_TAG")=='1'){
			if(data.get("FEE_DATA").get("SCORE_DO")=='0'){
				MessageBox.confirm("确认信息","您当前可用积分为"+data.get("FEE_DATA").get("USER_SCORE")+"分，使用积分办理本次业务将扣减"+data.get("FEE_DATA").get("NEED_SCORE")+"分，是否使用积分兑换办理？",function(btn){
					if(btn=="ok"){
						$("#IS_SCORE").val("0");
					}else{
						$("#IS_SCORE").val("1");
						var obj = new Wade.DataMap();
						obj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
						obj.put("MODE", "0"); 
						obj.put("CODE", "10"); 
						obj.put("FEE", data.get("FEE_DATA").get("FEE"));  
						$.feeMgr.insertFee(obj);
					}
				});
			}else{
				var obj = new Wade.DataMap();
				obj.put("TRADE_TYPE_CODE", $("#TRADE_TYPE_CODE").val());
				obj.put("MODE", "0"); 
				obj.put("CODE", "10"); 
				obj.put("FEE", data.get("FEE_DATA").get("FEE"));  
				$.feeMgr.insertFee(obj);
			}
		}

	},function(errorcode,errorinfo){
	MessageBox.alert(errorinfo);
		$.endPageLoading();
	},{async:false});
}

//如果查询到的用户主服务状态为人工停机，则弹出“是否同时办理开机业务”对话框
function specCheck(obj)
{
	var userSvcStateTag = $("#USER_SVC_STATE_TAG").val();
	if("true" == userSvcStateTag){
		//开机操作
		MessageBox.confirm("确认信息","该用户状态为申请停机，选择【确定】将继续换卡，并给用户办理【报开】业务！选择【取消】终止该业务受理！",function(btn){
			if(btn=="ok"){
				$("#OPEN_MOBILE_TAG").val("0");
			}else{
				$("#OPEN_MOBILE_TAG").val("1");
			}
		});

		//	 MessageBox.alert("提示","该用户状态为申请停机，选择[是]将继续换卡，请在换卡后引导用户办理【报开】否则将不能正常使用，[否]用户先办理【报开】后再来办理换卡业务！");
	}
}

function beforeReadCard(){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$.simcard.setSerialNumber(sn);
	return true;
}


function afterReadCard(data){
	var isWrited = data.get("IS_WRITED");//用来判断卡是否被写过
	if(isWrited == "1"){
		verifySimCard(data);
	}else{
     	var emptyCardId = data.get("EMPTY_CARD_ID");
     	var sn = $("#AUTH_SERIAL_NUMBER").val();
     	$.beginPageLoading();
    	$.ajax.submit('', 'checkEmptyCard', "&EMPTY_CARD_ID="+emptyCardId+"&SERIAL_NUMBER="+sn, '', function(data){
    		$.endPageLoading();
    	},
    	function(error_code,error_info){
    		$.endPageLoading();
                // $("#writeCardBtn").attr("disabled",true);
                MessageBox.alert("请注意：写卡之后，补换卡业务也无法正常办理："+error_info);
    	});
	}
}

function afterWriteCard(data){
	if($.os.phone) {
		alert("已经写卡成功！开始进行是否成功写入信息校验操作")
        if(data.get("RESULT_CODE")=="0"){
            $.simcardPhone.events.readOrWriteChangePhone('read');
        }
	}
	else {
        if(data.get("RESULT_CODE")=="0"){
            $.simcard.readSimCard();
        }
	}
}

function setSerialNumber(){
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	$.simcardcheck.setSerialNumber(sn);
	return true;
}

function beforeSubmit(){
	if(!$.validate.verifyAll("NewCardInfo")) {
		return false;
	}
	var isChecked = $("#IS_CHECKED").val();
	if(isChecked != "0"){
            MessageBox.alert("错误提示", "请先校验卡片信息！");
		return false;
	}
	var orderTypeCode = $("#TRADE_TYPE_CODE").val();
	/**
	 * REQ201705270006_关于人像比对业务优化需求
	 * @author zhuoyingzhi
	 * @date 20170630
	 */
	//补换卡类型---（0=补卡 1=换卡）
	var remoteCardType=$("#REMOTECARD_TYPE").val();	
	if(orderTypeCode == 142 && remoteCardType== "0"){
		//换卡 (写卡)并且补卡类型为0
		var authCheckPsptTypeCode=$("#AUTH_CHECK_PSPT_TYPE_CODE").val();
		
		var authCheckPsptId=$("#AUTH_CHECK_PSPT_ID").val();
		
		//获取验证方式
		var authCheckMode=$("#AUTH_CHECK_MODE").val();
		
		
		//携入标识   1是携入      非1不是
		var npTag=$("#NPTag").val();
		
		//固话标识   1是固话  非1 不是固话
		var wxTag=$("#WXTag").val();
		
		if(npTag == 1 || wxTag == 1){
			//携入号码或者固话号码
			//提交的时候不处理
		}else{
			if((authCheckPsptTypeCode == "0" ||authCheckPsptTypeCode == "1" || authCheckPsptTypeCode == "3")&& authCheckMode=="4") {
                //(本地身份证或外地身份证、军人身份证)和效验方式为客户证件（证件号码+服务密码：4）     换卡(写卡)
                //必须进行人像比对 并且通过后才能办理业务
                var picid = $("#custInfo_PIC_ID").val();

                //补卡业务人像比对仅作为辅助手段，不限制必须进行人像比对才能办理。
//			 	if(picid == ""){
//					MessageBox.error("告警提示","请进行客户摄像!",null, null, null, null);
//					return false;
//				}		
                //经办人摄像标志
                var agentpicid = $("#custInfo_AGENT_PIC_ID").val();

                if (picid == "" && agentpicid == "") {
                    MessageBox.error("告警提示", "请客户或经办人摄像。", null, null, null, null);
                    return false;
                }


                if (null != picid && picid == "ERROR") {
                    MessageBox.error("告警提示", "客户" + $("#custInfo_PIC_STREAM").val(), null, null, null, null);
                    return false;
                }
                /**
                 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
                 * @author zhuoyingzhi
                 * @date 20170805
                 */
                if (null != agentpicid && agentpicid == "ERROR") {
                    //经办人摄像失败
                    MessageBox.error("告警提示", "经办人" + $("#custInfo_AGENT_PIC_STREAM").val(), null, null, null, null);
                    return false;
                }
			}else {// 客户证件类型选择身份证/军人身份证之外的类型
				var agengtpsptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
				if (agengtpsptType){
					if (agengtpsptType =="3"){ // 经办人选择军人身份证类型需要进行人像比对
                        if(null != agentpicid && agentpicid == "ERROR"){
                            //经办人摄像失败
                            MessageBox.error("告警提示","经办人"+$("#custInfo_AGENT_PIC_STREAM").val(),null, null, null, null);
                            return false;
                        }
                        if (agentpicid == ""){
                            MessageBox.error("告警提示","请经办人摄像。",null, null, null, null);
                            return false;
                        }
					}
				}
			}
		}
	}
	/*********************************************************/




	var param = "&ORDER_TYPE_CODE=" + orderTypeCode;
	$.cssubmit.addParam(param);
	return true;
}

/*检查非空*/
function isNull(str) {
	if(str==undefined || str==null || str=="") {
		return true;
	}
	return false;
}

function checkSimCardInfo(){
	var simCardNo = $("#SIM_CARD_NO").val();
	if(isNull(simCardNo)){
		return;
	}
	//win7+ie11下面不能转换bug
	// var oldData=new $.DataMap($("#OLD_SIM_CARD_INFO").val());
    var oldSimInfoArr = $("#OLD_SIM_CARD_INFO").val().split(",");
    var oldTemp ="";
    var oldSimCardNo="";
	for(var i=0;i<oldSimInfoArr.length;i++) {
        if(oldSimInfoArr[i].indexOf("SIM_CARD_NO")>-1) {
        	oldTemp = oldSimInfoArr[i].replace(/"/g,"");
            oldSimCardNo = oldTemp.substr(oldTemp.indexOf(":")+1);
		}
	}
	if(oldSimCardNo == simCardNo){
		MessageBox.alert("错误提示", "新SIM卡号不得与老SIM卡号一致！");
		return;
	}
	var newSimCardNo = $("#NEW_SIM_CARD_NO").val();
	if(newSimCardNo==simCardNo){
		return ;
	}
	$("#NEW_SIM_CARD_NO").val("");
	$("#IS_CHECKED").val("1");//初始化SIM卡校验结果
	$.simcardcheck.checkSimCard('SIM_CARD_NO');
	$("#NewCardInfo").removeClass("e_hide");
}
/**
 * 人像比对(按钮)
 * @param picid
 * @param picstream
 * @returns {Boolean}
 * @author zhuoyingzhi
 * @date 20170626
 */
function identification(picid,picstream){
	
	var tradeTypeCode = $("#TRADE_TYPE_CODE").val();

	//补换卡类型---（0=补卡 1=换卡）
	var remoteCardType=$("#REMOTECARD_TYPE").val();
	if(remoteCardType != "0"){
		//非补卡类型，不处理
	MessageBox.alert("非补卡类型,不提供人像比对服务.");
		return false;
	}	
	
	var custName,psptId,psptType,fornt,ucaCustName;
	
	if(picid == "custInfo_PIC_ID"){
	    //效验  客户名称(身份证中的客户名称)
		custName = $("#AUTH_CHECK_CUSTINFO_CUST_NAME").val();
		//uca中的客户名称
		ucaCustName = $("#UCA_CUST_NAME").val();
		//效验 身份证号码
		psptId = $("#AUTH_CHECK_PSPT_ID").val();
		//效验  身份证类型
		psptType = $("#AUTH_CHECK_PSPT_TYPE_CODE").val();
		//身份证正面
		fornt = $("#FRONTBASE64").val();
		//如果是手动输入证件号码,无法获取证件上的客户姓名
		if(custName == ""){
			custName=ucaCustName;
		}
	}else{
		//经办人
		custName = $("#custInfo_AGENT_CUST_NAME").val();
		psptId = $("#custInfo_AGENT_PSPT_ID").val();
		psptType = $("#custInfo_AGENT_PSPT_TYPE_CODE").val();
		fornt = $("#custInfo_AGENT_FRONTBASE64").val();
	}


	//携入标识
	var npTag=$("#NPTag").val();
	//固话标识   1是固话  非1 不是固话
	var wxTag=$("#WXTag").val();
	
	if(npTag == 1||wxTag == 1){
		MessageBox.success("提示","携入客户及固话暂不提供人像比对服务", null, null, null);
		return false;
	}
	
	if(psptId == "" || psptType=="" || custName == ""){
		if(picid == "custInfo_PIC_ID"){
		MessageBox.alert("请查询客户资料或不需要摄像");
			return false;
		}else{
		MessageBox.alert("请扫描或选择经办人证件类型");
			return false;
		}
	}

    if ($.os.phone) { // 手机移动端人像比对
        $.MBOP.shotImage(picid + "_142"); // 142：补卡
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
	//调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	//获取保存结果
	var result = makeActiveX.IdentificationInfo.result;			
	//获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	
	if(picID != ''){
        MessageBox.alert("人像摄像成功", null, function (btn) {
            if ("ok" === btn) {
                //获取照片流
                var picStream = makeActiveX.IdentificationInfo.pic_stream;
                picStream = escape (encodeURIComponent(picStream));
                if("0" == result){
                    $("#"+picid).val(picID);
                    $("#"+picstream).val(picStream);
                    var param = "&BLACK_TRADE_TYPE=142";//换卡(写卡)
                    param += "&CERT_ID="+psptId;
                    param += "&CERT_NAME="+custName;
                    param += "&CERT_TYPE="+psptType;
                    param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));
                    param += "&SERIAL_NUMBER="+$.auth.getAuthData().get("USER_INFO").get("SERIAL_NUMBER");
                    $.beginPageLoading("正在进行人像比对。。。");
                    $.ajax.submit(null, 'cmpPicInfo', param, '', function(data){
                            $.endPageLoading();
                            if(data && data.get("X_RESULTCODE")== "0"){
                                MessageBox.success("成功提示","人像比对成功", null, null, null);
                                return true;
                            }else if(data && data.get("X_RESULTCODE")== "1"){
                                $("#"+picid).val("ERROR");
                                $("#"+picstream).val(data.get("X_RESULTINFO"));
                                MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
                                return false;
                            }
                            else if (data && data.get("X_RESULTCODE") == "3") {
                                MessageBox.confirm("提示", "该身份证在公安部人像库未留存头像，请现场进行人工核验！", function (btn) {
                                    if (btn == 'cancel') {
                                        $.cssubmit.closeMessage(true);
                                    }
                                }, {'ok': "核验通过", 'cancel': '核验不通过'});
                            }
                        },
                        function(error_code,error_info,derror){
                            $("#"+picid).val("ERROR");
                            $("#"+picstream).val("人像比对失败,请重新拍摄");
                            $.endPageLoading();
                            //showDetailErrorInfo(error_code,error_info,derror);
                            MessageBox.error("告警提示","人像比对失败,请重新拍摄",null, null, null, null);
                        });
                }else{
                    MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
                }
            }
        });
	}else{
        MessageBox.alert("人像摄像失败");
            return false;
	}	
}
/***
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 客户摄像
 * @author zhuoyigzhi
 * @date 20170805
 */
function changeMethod1(){
	$("#span_CUST").css("display","");
	//经办人隐藏
	$("#span_AGENT").css("display","none");

	//经办人名称
	$("#custInfo_AGENT_CUST_NAME").val("");
	//经办人证件号码
	$("#custInfo_AGENT_PSPT_ID").val("");
	//经办人证件类型
	$("#custInfo_AGENT_PSPT_TYPE_CODE").val("");

	//经办人照片id
	$("#custInfo_AGENT_PIC_ID").val("");

	//拍摄经办人人像照片流
	$("#custInfo_AGENT_PIC_STREAM").val("");
	//经办人身份证反面照
	$("#custInfo_AGENT_BACKBASE64").val("");
	//经办人身份证正面照
	$("#custInfo_AGENT_FRONTBASE64").val("");
}
/***
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * <br/>
 * 经办人摄像
 * @author zhuoyigzhi
 * @date 20170805
 */
function changeMethod2(){	
	//隐藏客户摄像
	$("#span_CUST").css("display","none");
	//经办人 显示
	$("#span_AGENT").css("display","");
	
	//客户摄像id
	$("#custInfo_PIC_ID").val("");
	//拍摄人像照片流
	$("#custInfo_PIC_STREAM").val("");
}
/**
 * REQ201707060009关于补卡、密码重置、复机业务优化的需求
 * @author zhuoyingzhi
 * @date 20170805
 * 扫描读取经办人身份证信息
 */
function clickScanPspt2(){
    if ($.os.phone) {
        $.MBOP.getMsgByEForm("custInfo_AGENT_PSPT_ID_142"); // 142：补卡
    } else {
        var psptTypeCode = $("#custInfo_PSPT_TYPE_CODE").val();
        var needpicinfo = null;
        var tag = (psptTypeCode == "E" || psptTypeCode == "G"
            || psptTypeCode == "D" || psptTypeCode == "M" || psptTypeCode == "L") ? true : false;
        if (tag) {
            //客户证件类型为单位证件
            needpicinfo = "PIC_INFO";
        }
        getMsgByEForm("custInfo_AGENT_PSPT_ID", "custInfo_AGENT_CUST_NAME", needpicinfo, null, null, null, null, null);
    }
}