function changeCodeAreaCode(){
	 clearNumber();//每换一次号段先清空
    var codeAreaCode = $("#CODE_AREA_CODE").val();
    if(codeAreaCode && codeAreaCode.length > 0){
			for (var i = 0; i < codeAreaCode.length; i++) 
			{
				if(i<3){
					$("#RES_NO_"+i).val(codeAreaCode.substring(i, i+1) );
					$("#RES_NO_"+i).attr("disabled", true);
				}else{
			        $("#RES_NO_3").val("");
			        $("#RES_NO_4").val("");
					$("#RES_NO_"+(i+2)).val(codeAreaCode.substring(i, i+1) );
					$("#RES_NO_"+(i+2)).attr("disabled", true);
				}
								
			}
				    	    	
//        $("#RES_NO_0").val(codeAreaCode.substring(0, 1));
//        $("#RES_NO_1").val(codeAreaCode.substring(1, 2));
//        $("#RES_NO_2").val(codeAreaCode.substring(2, 3));      
        $("#ACCESS_NUMBER_S").val($("#CODE_AREA_CODE_float").find("li[idx=" + CODE_AREA_CODE.selectedIndex + "]").attr("para_code1"));
        $("#ACCESS_NUMBER_E").val($("#CODE_AREA_CODE_float").find("li[idx=" + CODE_AREA_CODE.selectedIndex + "]").attr("para_code2"));
    } else {
    	clearNumber();
    }
}

function clearNumber(){
    $("#RES_NO_0").val("");
    $("#RES_NO_1").val("");
    $("#RES_NO_2").val("");
    $("#RES_NO_3").val("");
    $("#RES_NO_4").val("");
    $("#RES_NO_5").val("");
    $("#RES_NO_6").val("");
    $("#RES_NO_7").val("");
    $("#RES_NO_8").val("");
    $("#RES_NO_9").val("");
    $("#RES_NO_10").val("");
    $("#RES_NO_11").val("");
    $("#RES_NO_12").val("");    
    $("#ACCESS_NUMBER_S").val("");
    $("#ACCESS_NUMBER_E").val("");
        
    $("#RES_NO_0").attr("disabled", false);
    $("#RES_NO_1").attr("disabled", false);
    $("#RES_NO_2").attr("disabled", false);
    $("#RES_NO_3").attr("disabled", false);
    $("#RES_NO_4").attr("disabled", false);
    $("#RES_NO_5").attr("disabled", false);
    $("#RES_NO_6").attr("disabled", false);
    $("#RES_NO_7").attr("disabled", false);
    $("#RES_NO_8").attr("disabled", false);

}

function resNoKeyDown(num){
    $("#RES_NO_"+num).val("");
}

function resNoKeyUp(num){
    var code = window.event.keyCode;
    var name = $("#RES_NO_"+num);
    if(code >= 48 && code <= 57){
        var value = String.fromCharCode(code);
        name.val(value);
        if(num != 12){
            $("#RES_NO_"+(parseInt(num)+1)).focus();
            return;
        }
    }else if(code >= 96 && code <= 105){
        if(num != 12){
            $("#RES_NO_"+(parseInt(num)+1)).focus();
            return;
        }
    }    
    else {    
            return;        
    }
}

function qrySerialNumberList() {
    if(!$.validate.verifyAll("QueryPhonePart")) {
        return false;
    }

    $.beginPageLoading("号码信息查询。。。");
    $.ajax.submit('QueryPhonePart', 'qrySerialNumberList', null, 'QueryResultPart',
        function(data)
        {
            $.endPageLoading();
            if (data != null && data.length > 0) {
//                $("#QueryMsgPart").css("display", "none");
                $("#QueryResultPart").css("display", "block");

            } else {
//                $("#QueryMsgPart").css("display", "block");
                $("#QueryResultPart").css("display", "none");
			       MessageBox.error("错误提示", "没有可用的开户号码");
            }
        }, function(error_code, error_info)
        {
            $.endPageLoading();
            $.MessageBox.error(error_code, error_info);
//            $("#QueryMsgPart").css("display", "");
		       MessageBox.error("错误提示", "没有可用的开户号码");

        });
}

// 将选择的手机号码传到父页面上
function setSelectedSerialNumber(liObj) {
    var serialNumber = $(liObj).attr("resNo");
    var preCodeTag = $(liObj).attr("preCodeTag");
    setPopupReturnValue(liObj, {"SERIAL_NUMBER": serialNumber,"PRECODE_TAG":preCodeTag});
}