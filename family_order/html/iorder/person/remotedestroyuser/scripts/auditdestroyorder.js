function qryCustInfo()
{
	var sn = $("#SERIAL_NUMBER").val();
	var orderId = $("#ORDER_ID").val();
	// if(sn == "") {
	// 	alert("手机号不能为空");
	// 	return false;
	// }
    if(orderId == null || orderId == "") {
        alert("流水号不能为空！");
        return false;
    }
	$.beginPageLoading();
	$.ajax.submit('QueryPart', 'qryCustInfo', null, 'DealUserInfoPart,psptCheckPart,userInfoPart,transferPart,HidePart',
		function (data) {
			$.cssubmit.disabledSubmitBtn(false);
            $.endPageLoading();
            // if(""==sn){
            // 	$("#SERIAL_NUMBER").val(data.get("SERIAL_NUMBER"));
            // }
            if("1"==data.get("DEAL_TAG")){
            	$.cssubmit.disabledSubmitBtn(true);
            	if($("input[name='AUDIT_RESULT']").get(0).value==1){
            		$("input[name='AUDIT_RESULT']").get(0).checked = "checked";
            		$("input[name='AUDIT_RESULT']").get(1).disabled = true;
            		$("#AUDIT_REASON").attr('disabled', true);
            	}
            }else if("3"==data.get("DEAL_TAG")||"2"==data.get("DEAL_TAG")){
            	$.cssubmit.disabledSubmitBtn(true);
            	if($("input[name='AUDIT_RESULT']").get(1).value==0){
            		$("input[name='AUDIT_RESULT']").get(1).checked = "checked";
            		$("input[name='AUDIT_RESULT']").get(0).disabled = true;
            		$("#AUDIT_REASON").attr('disabled', true);
            	}
            	if(data.get("RSRV_STR5")!=null&&data.get("RSRV_STR5")!=""){
            		$("#CHECKRESULT").val(data.get("RSRV_STR5"));
            	}
            }else if("9"==data.get("DEAL_TAG")){
            	$.cssubmit.disabledSubmitBtn(true);
            	if($("input[name='AUDIT_RESULT']").get(0).value==1){
            		$("input[name='AUDIT_RESULT']").get(0).checked = "checked";
            		$("input[name='AUDIT_RESULT']").get(1).disabled = true;
            		$("#AUDIT_REASON").attr('disabled', true);
            	}
            }
            
            if ("0" == data.get("PIC_CODE")) {
                $("#IDEN_HEAD").attr("src", "data:image/jpeg;base64," + data.get("IDEN_HEAD_BASE"));
                $("#IDEN_BACK").attr("src", "data:image/jpeg;base64," + data.get("IDEN_BACK_BASE"));
                $("#PIC_NAME_R").attr("src", "data:image/jpeg;base64," + data.get("PIC_NAME_R_BASE"));
            } else {
                MessageBox.alert("告警提示", "图片加载异常!");
            }
            // $("#SERIAL_NUMBER").attr('disabled', true);
            $("#ORDER_ID").attr('disabled', true);
            $("#bt_lookCustPic").attr('disabled', false);
            $("#bt_lookIdPicOne").attr('disabled', false);
            $("#bt_lookIdPicTwo").attr('disabled', false);
            return true;
        },
        function (error_code,error_info) {
            $.endPageLoading();
            alert(error_info);
        });

}

// function lookPic(id){
//     $("#IDEN_HEAD").hide();
//     $("#IDEN_BACK").hide();
//     $("#PIC_NAME_R").hide();
//     $("#"+id).show();
//     showPopup('myDialog');
// }

function downloadEOrder() {
    var fileName = $("#PIC_CNOTE").val();
    var fileBase64 = $("#PIC_CNOTE_BASE").val().replace(/[\r\n]/g, "");//从FtpUtils获取的base64编码带有换行符，要去掉换行符交给js处理
    if(fileName == null || fileName == "" || fileBase64 == null || fileBase64 == "") {
        return false;
    }
    var blob = getBlob(fileBase64);

    if (navigator.msSaveBlob) {
        navigator.msSaveBlob(blob, fileName);
    }
    else {
        var downloadUrl = document.getElementById("downloadUrl");
        downloadUrl.download = fileName;
        downloadUrl.href = URL.createObjectURL(blob);
        downloadUrl.click();
    }
}

function getBlob(fileBase64) {

    var sliceSize = 512;

    var byteCharacters = atob(fileBase64);
    var byteArrays = [];

    for (var offset = 0; offset < byteCharacters.length; offset += sliceSize) {
        var slice = byteCharacters.slice(offset, offset + sliceSize);

        var byteNumbers = new Array(slice.length);
        for (var i = 0; i < slice.length; i++) {
            byteNumbers[i] = slice.charCodeAt(i);
        }

        var byteArray = new Uint8Array(byteNumbers);

        byteArrays.push(byteArray);
    }

    return new Blob(byteArrays, {type: 'application/octet-stream'});
}

/**
 * 提交时校验
 * @return
 */
function checkBeforeSubmit()
{
	var auditResult = $("input[name='AUDIT_RESULT']:checked").val();
	var auditReason = $("#AUDIT_REASON").val();
	var checkResult = $("#CHECKRESULT").val();
	if(!auditResult){
		alert("请选择审核结果！");
		return;
	}
    if(auditResult == "0")
    {
        if(auditReason == "")
        {
            alert("不可销户时必须填写原因！");
            return false;
        }
        if(checkResult ==null || checkResult == "")
        {
        	alert("请选择不可销户的原因！");
        	return false;
        }
    } else {
        auditReason = "销户成功";
    }

    var transMoney = $("#GIFT_AMOUNT").val();
    var transPay = $("#GIFT_AMOUNT_B").val();
    var dealUser = $("#DESTROY_DEAL_USER").val();
    var dealUserTel = $("#DESTROY_DEALUSER_TEL").val();

    $.cssubmit.addParam('&AUDIT_RESULT=' + auditResult + "&AUDIT_REASON=" + auditReason +
    	"&TRANS_MONEY=" + transMoney + "&TRANS_PAY=" + transPay+"&CHECK_RESULT="+checkResult+"&XH_PERSON="+dealUser+"&XH_TEL="+dealUserTel);
	return true;
}