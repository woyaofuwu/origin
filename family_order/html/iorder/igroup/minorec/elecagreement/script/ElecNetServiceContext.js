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
            });
		}
	}

    (function($){
        $.extend({archives:{
                events:{
                    uploadImage:function(retStr){
                        var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA').val());
                        var param = "&base64Bitmap="+encodeURIComponent(retStr.toString())+"&ARCHIVES_NAME="+archiveInfo.get("ARCHIVES_NAME")+"&ARCHIVES_ID="+archiveInfo.get("ARCHIVE_ID");
                        $.beginPageLoading("数据上传中......");
                        $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.handler.ElecAgreementHandler", "uploadImage", param,
                            function (data) {
                                $.endPageLoading();
                                var fileId = data.get("FILE_ID");
                                try{
                                    callCustomer();
                                }catch (e) {
                                    alert(e);
                                }
                                //MessageBox.alert("温馨提示", "签名上传成功！");

                            }, function(error_code,error_info,derror){
                                $.endPageLoading();
                                showDetailErrorInfo(error_code,error_info,derror);
                            }, function () {
                                $.endPageLoading();
                                MessageBox.alert("告警提示", "签名上传失败，请重新签名上传");
                            });
                    },
                    setImage:function(ret){
                        var resultJson = $.parseJSON(ret);
                        var a= resultJson.exeResult;
                        if(a !=1){
                            window.MBOP.getEleSignBase64("$.archives.events.uploadImage");
                        }
                    }
                }
            }});
    })(Wade);
	
})

function backTest(){
	window.MBOP.closeWebPlugin();
}

function callCustomer(){
	$.beginPageLoading("数据提交中......");
	 $.ajax.submit("HiddenPart", "callCustomer", "", "", function(data){
		 $.endPageLoading();
		 var groupId = data.get("GROUP_ID");
		 MessageBox.success("温馨提示", "集团新增成功！集团编码为："+groupId, function(btn){
			 window.MBOP.closeWebPlugin();
         });
	 },
	 function(error_code,error_info,derror){
         $.endPageLoading();
         showDetailErrorInfo(error_code,error_info,derror);
     });
	
}

function displaydetail(btn,a)
{
	var button = $(btn);
    var detail_a = $('#'+a);
    if (detail_a.css('display') != "none")
    {
    	detail_a.css('display', 'none');
	    button.empty();
	    button.html('<span class="e_ico-unfold"></span>显示协议详细信息'); 
    }else {
    	detail_a.css('display', '');
        button.empty();
	    button.html('<span class="e_ico-fold"></span>隐藏协议详细信息'); 
    }
}

function onSubmit(obj){

    var submitPart = "HiddenPart";
    if($.os.phone) {
        if(!$.validate.verifyAll("NAME_phone")||!$.validate.verifyAll("DATE_phone")){
            return false;
        }
        submitPart = submitPart + ",NAME_phone,DATE_phone";
    }else{
        if(!$.validate.verifyAll("NAME_pc")||!$.validate.verifyAll("DATE_pc")){
            return false;
        }
        submitPart = submitPart + ",NAME_pc,DATE_pc";
    }
    $.beginPageLoading("数据提交中......");
    $.ajax.submit(submitPart, "onSubmit", "", "", function(data){
            $.endPageLoading();

            //将已生成的协议写回页面，防止重复提交
            $("#ARCHIVES_ID").val(data.get('ARCHIVE_ID'));
            $("#SUBMITTYPE").val("U");
            
            $("#NOT_SIGN").css("display","none");
            //返回订购页面的参数
            var agreementData=new Wade.DataMap();
            agreementData.put("AGREEMENT_ID",data.get('CONTRACT_NUMBER'));
            agreementData.put("CONTRACT_WRITE_DATE",data.get('CONTRACT_WRITE_DATE'));
            agreementData.put("CONTRACT_END_DATE",data.get('CONTRACT_END_DATE'));
            agreementData.put("ARCHIVES_NAME",data.get('ARCHIVES_NAME'));
            agreementData.put("PRODUCT_ID",data.get('PRODUCT_ID'));

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

            tempArchiveData.put("MINOREC_PRODUCT_INFO",agreementData);
            $("#TEMP_ARCHIVE_DATA").val(tempArchiveData.toString());
            //ret.TEMP_ARCHIVE_DATA=tempArchiveData;
            //setPopupReturnValue(this,ret ,false);

            if($.os.phone) {
                var currentUrl = window.location.href;
                var arr = currentUrl.split("?");
                currentUrl = arr[0]+"?service=page/"+data.get('URL')+"&listener=initPage&BACK_BUTTON=false&SHOWBUTTON=false"+"&ARCHIVES_ID="+data.get('ARCHIVE_ID');
                var param = {
                    "content":currentUrl,
                    "needStamp":1,
                    "callback":"$.archives.events.setImage"
                };
                window.MBOP.eleWebSign(JSON.stringify(param));
                //window.MBOP.eleSign(currentUrl,"$.archives.events.setImage");
            }else {
                MessageBox.success("温馨提示", "协议提交成功", function(btn){
                	closeNav();
                });
            }
        },
        function(error_code,error_info,derror){
            $.endPageLoading();
            showDetailErrorInfo(error_code,error_info,derror);
        });
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

function onTest(obj){
	
	
    /*if(!$.validate.verifyAll("onsubmitPart")){
        return false;
    }
    if($.os.phone) {
        window.MBOP.eleSign("https://mp.weixin.qq.com/s/UON3Yf21rstvNAxGLa0edg","callback");
    }else {
        MessageBox.success("温馨提示", "协议提交成功", function(btn){
            hidePopup(obj);
        });
    }*/
}

