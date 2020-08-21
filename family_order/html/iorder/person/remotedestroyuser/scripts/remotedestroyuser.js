$(document).ready(function(){

	//调用验证是否免人像比对和身份证可手动输入权限
	kqbkDataRight();
});

//扫描读取身份证信息
function clickScanPspt(){
	getMsgByEFormKQBK("IDCARDNUM","CUST_NAME",null,null,null,null,null,null);
	if('' != $('#IDCARDNUM').val()){
		$('#IDCARDNUM').attr('disabled', true);
	}
	if('' != $('#CUST_NAME').val()){
		$('#CUST_NAME').attr('disabled', true);
	}
}

function identification(picid,picstream) {
    var custName, psptId, psptType, fornt, desc;

    custName = $("#CUST_NAME").val();
    psptId = $("#IDCARDNUM").val();
    psptType = "0";//默认身份证
    fornt = $("#FRONTBASE64").val();
    desc = "请输入客户证件号码！";

    /*	if( psptType == ""){
            alert("请选择证件类型！");
            return false;
        }*/

    if (psptId == "") {
        alert(desc);
        return false;
    }
    var blackTradeType = "149";
    var sn = "";

    sn = $("#MOBILENUM").val();

    var bossOriginalXml = [];
    bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
    bossOriginalXml.push('<req>');
    bossOriginalXml.push('	<billid>' + '</billid>');
    bossOriginalXml.push('	<brand_name>' + '</brand_name>');
    bossOriginalXml.push('	<brand_code>' + '</brand_code>');
    bossOriginalXml.push('	<work_name>' + '</work_name>');
    bossOriginalXml.push('	<work_no>' + '</work_no>');
    bossOriginalXml.push('	<org_info>' + '</org_info>');
    bossOriginalXml.push('	<org_name>' + '</org_name>');
    bossOriginalXml.push('	<phone>' + sn + '</phone>');
    bossOriginalXml.push('	<serv_id>' + '</serv_id>');
    bossOriginalXml.push('	<op_time>' + '</op_time>');

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
        alert("人像摄像成功");
    } else {
        alert("人像摄像失败");
        return false;
    }
    //获取照片流
    var picStream = makeActiveX.IdentificationInfo.pic_stream;
    picStream = escape(encodeURIComponent(picStream));
    if ("0" == result) {
        //alert("picid:"+picid);
        $("#" + picid).val(picID);
        $("#" + picstream).val(picStream);
        var param = "&BLACK_TRADE_TYPE=" + blackTradeType;
        param += "&CERT_ID=" + psptId;
        param += "&CERT_NAME=" + custName;
        param += "&CERT_TYPE=" + psptType;
        param += "&SERIAL_NUMBER=" + sn;
        param += "&FRONTBASE64=" + escape(encodeURIComponent(fornt));
        param += "&PIC_STREAM=" + picStream;

        $.beginPageLoading("正在进行人像比对。。。");
        $.ajax.submit(null, "cmpPicInfo", param, '',
            function (data) {
                $.endPageLoading();
                if (data && data.get("X_RESULTCODE") == "0") {
                    MessageBox.success("成功提示", "人像比对成功", null, null, null);
                    $("#ShotFlag").val("1");//比对验证成功
                    return true;
                } else if (data && data.get("X_RESULTCODE") == "1") {
                    $("#" + picid).val("ERROR");
                    $("#" + picstream).val(data.get("X_RESULTINFO"));
                    $("#ShotFlag").val("0");//比对验证失败
                    MessageBox.error("告警提示", data.get("X_RESULTINFO"), null, null, null, null);
                    return false;
                }
                else if (data && data.get("X_RESULTCODE") == "3") {
                    MessageBox.confirm("提示", "该身份证在公安部人像库未留存头像，请现场进行人工核验！", function (btn) {
                        if (btn == 'cancel') {
                            $.cssubmit.closeMessage(true);
                        }
                    }, {'ok': "核验通过", 'cancel': '核验不通过'});
                }
            }, function (code, info, detail) {
                $.endPageLoading();
                $("#" + picid).val("ERROR");
                $("#" + picstream).val("人像比对信息错误，请重新拍摄！");
                $("#ShotFlag").val("0");//比对验证失败
                MessageBox.error("错误提示", "人像比对信息错误，请重新拍摄！", null, null, null, null);
            }, function () {
                $.endPageLoading();
                $("#" + picid).val("ERROR");
                $("#" + picstream).val("人像比对失败，请重新拍摄");
                $("#ShotFlag").val("0");//比对验证失败
                MessageBox.alert("告警提示", "人像比对失败，请重新拍摄");
            });
    } else {
        MessageBox.error("告警提示", "拍摄失败！请重新拍摄", null, null, null, null);
    }
}

function checkUserNum(){
	var destroyCheckFlag= $("#DestroyCheckFlag").val();
	var qryCardTypeTag=$("#QryCardTypeTag").val();
	if("1"!=destroyCheckFlag){//销户前校验标志
		MessageBox.alert("提示信息","请先进行销户前检验！");
		return false;
	}
	if("1"!=qryCardTypeTag){//卡查询标志
		MessageBox.alert("提示信息","请先进行卡查询操作！");
		return false;
	}
	var shotFlag = $("#ShotFlag").val();
	var scanTag = $("#SCAN_TAG").val();
	if(!$.validate.verifyAll('QueryPart')) {
		return false;
	}
	if(!$.validate.verifyAll('psptCheckPart')) {
		return false;
	}
	if(!$.validate.verifyAll('PhoneCheckPart')) {
		return false;
	}
	if(!$.validate.verifyAll('pswdCheckPart')) {
		return false;
	}
	if("1" != shotFlag){
	       alert("请进行客户人像比对！");
	       return false;
	}
	if("1" == scanTag){//扫描标记比较特殊，1为未扫描0为已扫描
		alert("请进行证件扫描！");
		return false;
	}
    var param = "&MOBILENUM=" + $("#MOBILENUM").val() + "&PSPT_ID=" + $("#IDCARDNUM").val() +"&CUST_NAME=" + $("#CUST_NAME").val() + "&PASSWORD=" + $("#PASSWORD").val();
   
	if("2"==$("#FRIENDCOUNTS").val()){
		var phone4 = $("#PHONE4").val();
		var phone5 = $("#PHONE5").val();
		param = param+"&NUMBER_CHECK="+phone4+"|"+phone5;
	}
	if("3"==$("#FRIENDCOUNTS").val()){
		var phone1 = $("#PHONE1").val();
		var phone2 = $("#PHONE2").val();
		var phone3 = $("#PHONE3").val();
		param = param+"&NUMBER_CHECK="+phone1+"|"+phone2+"|"+phone3;
	}
	$.beginPageLoading("鉴权中...");
	$.ajax.submit('PhoneCheckPart', 'openResultAuth',param , '', function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
            if("1" == data.get("BUS_STATE")){
            	MessageBox.success("提示信息","校验成功");
                $("#IDENT_CODE").val(data.get("IDENT_CODE"));
                $("#UserCheckFlag").val('1');
                $("#BRAND_CODE").val(data.get("BRAND_CODE"));
            }else{
            	MessageBox.error("错误提示", "校验失败："+data.get("REASON"));
                $("#IDENT_CODE").val(data.get("IDENT_CODE"));
                $("#UserCheckFlag").val('0');
            }
		}else{
			MessageBox.error("错误提示", "校验失败："+data.get("REASON"));
			$("#UserCheckFlag").val('0');
            $("#IDENT_CODE").val('');
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}

function qryCardType(){
	if(11!= $("#MOBILENUM").val().length){
		alert("手机号码必须为11位数字");
		return false;
	}
	var destroyCheckFlag= $("#DestroyCheckFlag").val();
	if("1"!=destroyCheckFlag){//销户前校验标志
		MessageBox.alert("提示信息","请先进行销户前检验！");
		return false;
	}
	
	var param = "&SERIAL_NUMBER="+$("#MOBILENUM").val();
	$.beginPageLoading("数据处理中...");
	$.ajax.submit('QueryPart', 'qryCardType',param , 'QueryFriend,PhoneCheckPart', function(data){
		$.endPageLoading();
		var numCount=data.get("NUM_COUNT");
		// if("0" != data.get("CARD_TYPE")){
		//     var cardTypeName;
		//     switch (data.get("CARD_TYPE")){
         //        case "1" : cardTypeName = "NFC卡"; break;
         //        case "2" : cardTypeName = "RFID卡"; break;
         //        case "3" : cardTypeName = "一卡双号"; break;
         //        case "4" : cardTypeName = "和多号副卡（含实体副卡）"; break;
         //        case "5" : cardTypeName = "一号多终端副号码"; break;
         //        case "6" : cardTypeName = "物联网用户"; break;
         //        case "7" : cardTypeName = "其他类型卡"; break;
         //        default : cardTypeName = "未知类型"; break;
         //    }
		// 	alert("提示：用户卡号不是标准实体卡，该卡类型为" + cardTypeName);
		// }
		if("00" != data.get("USER_STATUS")){
            alert("用户状态异常，无法办理！用户状态码：" + data.get("USER_STATUS"));
            return false;
        }
        $('#HOME_PROV').val(data.get('HOME_PROV'));
		$('#HOME_PROV_NAME').val(data.get('HOME_PROV_NAME'));
        if(data.get('HOME_PROV_NAME') == "海南") {
            alert("错误提示：跨区销户只面向省外号码进行！");
            return false;
        }
		if('0' == data.get('IS_JXH')){
			$('#BeautifulFlag').val('1');
			$('#ISJXH').val('是');
			$('#friendCountsId').css('display','');
			if("2"==numCount){
				$("#FRIENDCOUNTS").val(numCount);
				$('#PhoneCheckTitle').css('display','');
				$("#TWONUMBER").css("display","");
				$("#PHONE4").attr('nullable','no');
				$("#PHONE5").attr('nullable','no');
			}else if("3"==numCount){
				$("#FRIENDCOUNTS").val(numCount);
				$('#PhoneCheckTitle').css('display','');
				$("#THREENUMBER").css("display","");
				$("#PHONE1").attr('nullable','no');
				$("#PHONE2").attr('nullable','no');
				$("#PHONE3").attr('nullable','no');
			}else{
				$("#FRIENDCOUNTS").val(numCount);
			}
		}else{
			$('#BeautifulFlag').val('0');
			$('#ISJXH').val('否');
			$('#friendCountsId').css('display','none');
			$('#PhoneCheckTitle').css('display','none');
		}
		$("#QryCardTypeTag").val('1');
		$.cssubmit.disabledSubmitBtn(false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
	// if('1'==$("#BeautifulFlag").val()){
	// 	$.beginPageLoading("您的号码是吉祥号码，正进行金库认证..");
	// 	$.treasury.auth('暂时为空', $("#MOBILENUM").val(), function(ret){
	// 		$.endPageLoading();
	// 		if(true === ret){
	// 			alert("认证成功！");
	// 		}else{
	// 			alert("认证失败！");
	// 		}
	// });
	// }
}

function checkSerialNumber(phone, name)
{
    var isSuccess = false;
    var param = "&NAME=" + name+ "&SERIAL_NUMBER=" + phone;
    $.beginPageLoading();
    $.ajax.submit(null, 'checkSerialNumber', param, null, function(data){
        $.endPageLoading();
        var flag = data.get('FLAG');
        if("C" != flag) {
            alert("校验完成！该号码为本省号码！");
        } else {
            alert("校验完成！该号码为" + data.get('HOME_PROV_NAME') + "号码！");
        }
        isSuccess = true;
    }, function (errorCode,errorInfo) {
        $.endPageLoading();
        alert(errorInfo);
        isSuccess = false;
    });
    return isSuccess;
}

function varifyContact()
{
	var mobileNum = $("#MOBILENUM").val();
    var phone = $("#CONTACT_PHONE").val();
    var name = $("#CONTACT_NAME").val();
    if("" == phone || "" == name) {
        alert("请填写联系人姓名和电话！");
        return false;
    }
    if(!isSN(phone))
    {
        alert("填写号码不合法！");
        return false;
    }
    if(phone==mobileNum){
    	MessageBox.alert("重要提示", "联系人电话不能与销户号码相同");
    	return false;
    }
    var param = "&NAME=" + name+ "&SERIAL_NUMBER=" + phone;
    $.beginPageLoading();
    $.ajax.submit(null, 'checkSerialNumber', param, null, function(data){
        $.endPageLoading();
        var flag = data.get('FLAG');

        $("#VERIFY_CONTACT_TAG").val("1");
        $("#CONTACT_PHONE").attr('disabled', true);
        $("#CONTACT_NAME").attr('disabled', true);
        $("#bt_varify_contact").attr('disabled', true);
        if("C" != flag) {
            alert("校验完成！该号码为本省号码！");
        } else {
            alert("校验完成！该号码为" + data.get('HOME_PROV_NAME') + "号码！");
        }
        return true;
    }, function (errorCode,errorInfo) {
        $.endPageLoading();
        alert(errorInfo);
        return false;
    });
   
}

function varifyGiftCust()
{
	var mobileNum = $("#MOBILENUM").val();
    var phone = $("#GIFT_SERIAL_NUMBER").val();
    var name = $("#GIFT_CUST_NAME").val();
    if("" == phone || "" == name) {
        alert("请填写现金转费号码客户姓名和电话！");
        return false;
    }

    if(!isSN(phone))
    {
        alert("填写号码不合法！");
        return false;
    }
    if(phone==mobileNum){
    	MessageBox.alert("重要提示", "费用转移号码不能与销户号码相同");
    	return false;
    }
    var param = "&NAME=" + name+ "&SERIAL_NUMBER=" + phone;
    $.beginPageLoading();
    $.ajax.submit(null, 'checkSerialNumber', param, null, function(data){
        $.endPageLoading();
        var flag = data.get('FLAG');
        if("C" != flag) {
            alert("校验完成！该号码为本省号码！");
        } else {
            alert("校验完成！该号码为" + data.get('HOME_PROV_NAME') + "号码！");
        }
        $("#VERIFY_GIFT_TAG").val("1");
        $("#GIFT_SERIAL_NUMBER").attr('disabled', true);
        $("#GIFT_CUST_NAME").attr('disabled', true);
        $("#bt_varify_gift").attr('disabled', true);
        return true;
    }, function (errorCode,errorInfo) {
        $.endPageLoading();
        alert(errorInfo);
        return false;
    });
    
}

function varifyGiftCustB()
{
	var phoneGift = $("#GIFT_SERIAL_NUMBER").val();
	var mobileNum = $("#MOBILENUM").val();
    var phone = $("#GIFT_SERIAL_NUMBER_B").val();
    var name = $("#GIFT_CUST_NAME_B").val();
    if("" == phone || "" == name) {
        alert("请填写非现金转费号码客户姓名和电话！");
        return false;
    }

    if(!isSN(phone))
    {
        alert("填写号码不合法！");
        return false;
    }
    if(phone==mobileNum){
    	MessageBox.alert("重要提示", "费用转移号码不能与销户号码相同");
    	return false;
    }
    if(phone!=phoneGift){
    	MessageBox.alert("重要提示", "费用转移号码必须相同");
    	return false;
    }
    var param = "&NAME=" + name+ "&SERIAL_NUMBER=" + phone;
    $.beginPageLoading();
    $.ajax.submit(null, 'checkSerialNumber', param, null, function(data){
        $.endPageLoading();
        var flag = data.get('FLAG');
        if("C" != flag) {
            alert("校验完成！该号码为本省号码！");
            //alert("非现金转费号码须与待销户号码归属同一省份，请重新填写同省份号码！");
            //return false;
        } else {
            alert("校验完成！该号码为" + data.get('HOME_PROV_NAME') + "号码！");
        }
       /* if($("#HOME_PROV_NAME").val() != data.get('HOME_PROV_NAME')) {
            alert("非现金转费号码须与待销户号码归属同一省份，请重新填写同省份号码！");
            return false;
        }*/
        $("#VERIFY_GIFT_B_TAG").val("1");
        $("#GIFT_SERIAL_NUMBER_B").attr('disabled', true);
        $("#GIFT_CUST_NAME_B").attr('disabled', true);
        $("#bt_varify_gift_b").attr('disabled', true);
        return true;
    }, function (errorCode,errorInfo) {
        $.endPageLoading();
        alert(errorInfo);
        return false;
    });

}

function isSN(phone)
{
    var reg = /^([0-9]|[\-])+$/g;
    if (phone.length !== 11) {
        return false;
    } else {
        return reg.exec(phone);
    }
}

/**
 * 提交时校验
 * @return
 */
function checkBeforeSubmit()
{
	var userCheck = $("#UserCheckFlag").val();
    var shotFlag = $("#ShotFlag").val();
	var scanTag = $("#SCAN_TAG").val();
	var verifyContactTag = $("#VERIFY_CONTACT_TAG").val();
    var verifyGiftTag = $("#VERIFY_GIFT_TAG").val();
    var verifyGiftBTag = $("#VERIFY_GIFT_B_TAG").val();
	var requiredContentEmpty = "" == $("#CONTACT_PHONE").val() || "" == $("#CONTACT_NAME").val() ||
        "" == $("#GIFT_SERIAL_NUMBER").val() || "" == $("#GIFT_CUST_NAME").val() ||
        "" == $("#GIFT_SERIAL_NUMBER_B").val() || "" == $("#GIFT_CUST_NAME_B").val() ||
        "" == $("#CREATE_CONTACT").val() || "" == $("#CREATE_PHONE").val() ||
        "" == $("#CREATE_ORG_NAME").val() || "" == $("#REMARKS").val();
    if("1" != userCheck){
		alert("请进行鉴权！");
		return false;
    }
    if("1" != shotFlag){
       alert("请进行客户人像比对！");
       return false;
	 }
     if("1" == scanTag){//扫描标记比较特殊，1为未扫描0为已扫描
     	alert("请进行证件扫描！");
     	return false;
     }
    if("1" != verifyContactTag){
	    alert("请校验联系人号码！");
	    return false;
    }
    if("1" != verifyGiftTag){
        alert("请校验现金转费号码！");
        return false;
    }
    if("1" != verifyGiftBTag){
        alert("请校验非现金转费号码！");
        return false;
    }
	if(requiredContentEmpty){
		alert("还有未填的必填项！");
		return false;
	}
	//MessageBox.alert("重要提示","业务受理成功后请用户");
    return true;
}

//是否免人像比对和身份证可手动输入权限
function kqbkDataRight(){

	$.ajax.submit(null,'kqbkDataRight','','',
			function(data){ 
		var flag=data.get("TAG");
		
		if(flag=="1"){ 
			$("#ShotFlag").val("1");//免人像比对
			
			$('#SCAN_TAG').val("0");//免证件扫描
		}
		$.endPageLoading();
	},function(error_code,error_info){
		$.MessageBox.error(error_code,error_info);
		$.endPageLoading();
	},{
		"async" : false
	});
		
}
/**
 * 销户前校验
 * @returns {Boolean}
 */
function destroyCheck(){
	if(11!= $("#MOBILENUM").val().length){
		alert("手机号码必须为11位数字");
		return false;
	}
	var param = "&MOBILENUM="+$("#MOBILENUM").val();
	$.beginPageLoading("校验中...");
	$.ajax.submit('', 'destroyCheck',param , '', function(data){
		$.endPageLoading();
		if("1" == data.get("RESULT")){
            if("1" == data.get("BUS_STATE")){
            	MessageBox.success("校验成功","请提醒客户1、销户前请完成积分兑换，销户后剩余积分将清零；2、销户前请取消与银行、第三方等捆绑的全部业务，如支付宝、微信、银行委托扣款等 。");
                $("#DestroyCheckFlag").val('1');
            }else{
            	MessageBox.error("错误提示", "校验失败："+data.get("REASON"));
            	$("#DestroyCheckFlag").val('0');
            	return false;
            }
		}else{
			MessageBox.error("错误提示", "校验失败："+data.get("REASON"));
			$("#DestroyCheckFlag").val('0');
        	return false;
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
   });
}
