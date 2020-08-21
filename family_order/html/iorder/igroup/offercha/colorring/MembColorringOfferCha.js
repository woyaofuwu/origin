function initPageParam_110000620001() {
	//$("input[name='pam_TWOCHECK_SMS_FLAG']").val("1");
	if($("#pam_CANCEL_LING").val()=="1"){
		$("input[name='CANCEL_LING']").attr("checked","true");
	}
	var methodName = $("#cond_OPER_TYPE").val();
	if(methodName!="CrtMb")
	{
		$("input[name='pam_TWOCHECK_SMS_FLAG']").parent().parent().css("display","none");
		$("#COLORRING_TIP").css("display","none");
	}

	//REQ201812200001关于优化集团产品二次确认功能的需求
	$.ajax.submit("", "queryTwoCheck", "", "", function(data){
		$.endPageLoading();
		if(data != null)
		{
			if(data.get("TWOCHECK") == '1'){
				$("input[name='pam_TWOCHECK_SMS_FLAG']").attr("disabled","");
			}
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}
function initProdChaSpec_110000620001(obj){
    if(obj){
        for(var i=0;i<obj.length;i++){
            var data=obj.get(i);
            //$("#"+data.get("ATTR_CODE")).val(data.get("ATTR_VALUE"));
			if("CANCEL_LING"==data.get("ATTR_CODE")){
				if("1"==data.get("ATTR_VALUE")){
                    $("input[name='CANCEL_LING']").attr("checked","true");
                }
                return;
			}

        }
    }

}


//提交
function checkSub(obj)
{
    var cancelBut = $("input[name='CANCEL_LING']");
    if(cancelBut[0].checked){
        $('#pam_CANCEL_LING').val("1");
    }else{
        $('#pam_CANCEL_LING').val("0");
    }

    if($("input[name='pam_TWOCHECK_SMS_FLAG']:checked").length==0){
        /*if(!confirm("当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择，重新选择请点【取消】按钮！")){
            return false;
        }*/
        debugger;
        var flag=true;
        MessageBox.confirm("提示信息", "当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择，重新选择请点【取消】按钮！", function(btn){
            if("ok"==btn){
                cancelBut.remove();
                if(!submitOfferCha())
                    return false;
                backPopup(obj);
            }
        });
    }else{
        cancelBut.remove();
        if(!submitOfferCha())
            return false;
        backPopup(obj);
    }

}