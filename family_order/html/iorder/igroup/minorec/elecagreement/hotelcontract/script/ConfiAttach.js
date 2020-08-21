$(function () {

	//手机端签名兼容处理
	if($.os.phone) {
		var backFlag = getUrlParams("BACK_BUTTON");
		if(backFlag == "false"){
			$("#scroll").removeAttr("class");
			document.getElementById("ht").style.overflow="auto";
			document.getElementById("bd").style.overflow="visible";
			document.getElementById("bd").style.height="auto";

            $("#bd input[type=text]").each(function () {
                $(this).removeAttr("placeholder");
                if(!$(this).val()){
                    $(this).val("\\");
                }
            });
		}
	}

})

function onSubmit(obj){

    var submitPart = "HiddenPart";
    if($.os.phone) {
        /*if(!$.validate.verifyAll("NAME_phone")||!$.validate.verifyAll("DATE_phone")){
            return false;
        }*/
        submitPart = submitPart + ",phSubmitPart";
    }else{
        /*if(!$.validate.verifyAll("NAME_pc")||!$.validate.verifyAll("DATE_pc")){
            return false;
        }*/
        submitPart = submitPart + ",pcSubmitPart";
    }
    $.beginPageLoading("数据加载中......");
    $.ajax.submit(submitPart, "onSubmit", "", "", function(data){
            $.endPageLoading();

            //将已生成的协议写回页面，防止重复提交
            $("#ARCHIVES_ID").val(data.get('ARCHIVE_ID'));
            $("#SUBMITTYPE").val("U");

            //返回合同录入的参数
            var ret={};
            var tempArchiveData=new Wade.DataMap();
            tempArchiveData.put("CONTRACT_NUMBER",data.get('CONTRACT_NUMBER'));
            tempArchiveData.put("CONTRACT_WRITE_DATE",data.get('CONTRACT_WRITE_DATE'));
            tempArchiveData.put("CONTRACT_END_DATE",data.get('CONTRACT_END_DATE'));
            tempArchiveData.put("ARCHIVE_ID",data.get('ARCHIVE_ID'));
            tempArchiveData.put("ARCHIVES_NAME",data.get('ARCHIVES_NAME'));
            tempArchiveData.put("AGREEMENT_DEF_ID",data.get('AGREEMENT_DEF_ID'));
            tempArchiveData.put("URL",data.get('URL'));
            tempArchiveData.put("AFTER_FUNCTION",$("#AFTER_FUNCTION").val());

            ret.TEMP_ARCHIVE_DATA=tempArchiveData;

            if($.os.phone) {
            	window.parent.afterSubmit(tempArchiveData);
                var currentUrl = window.location.href;
                var arr = currentUrl.split("?");
                currentUrl = arr[0]+"?service=page/"+data.get('URL')+"&listener=initPage&BACK_BUTTON=false&SHOWBUTTON=false"+"&ARCHIVES_ID="+data.get('ARCHIVE_ID');
                var param = {
                    "content":currentUrl,
                    "needStamp":0,
                    "callback":"$.archives.events.setImage"
                };
                window.MBOP.eleWebSign(JSON.stringify(param));
            }else {
            	setPopupReturnValue(this,ret ,false);
                MessageBox.success("温馨提示", "协议提交成功", function(btn){
                    hidePopup(obj);
                });
            }
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}

function onBack(obj){
	if($.os.phone) {
		window.parent.backElecSelect();
	}else{
		hidePopup(obj);
	}
}

function onPrint(obj){
    $.beginPageLoading("数据加载中......");
    $.ajax.submit("onsubmitPart,HiddenPart", "onPrint", "", "", function(data){
            $.endPageLoading();
            var fileId = data.get("FILE_ID");
            popupPage('合同预览', 'igroup.elecagreement.ElecContractPDFview', 'init', '&FILE_ID='+fileId, null, 'full', '', '');
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
}