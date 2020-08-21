$(function () {

    if ($.os.phone) {
        var backFlag = getUrlParams("BACK_BUTTON");
        if (backFlag == "false") {
            $("#scroll").attr("class", "c_scroll-white c_scroll-float c_scroll-header");
            document.getElementById("ht").style.overflow = "auto";
            document.getElementById("bd").style.overflow = "visible";
            document.getElementById("bd").style.height = "auto";

            $("#bd input[type=text]").each(function () {
                $(this).removeAttr("placeholder");
                if (!$(this).val()) {
                    $(this).val("\\");
                }
            });
        }

        $("#bd input[type=radio]").each(function () {
            var id = this.id;
            if(id && this.getAttribute("checked")=="checked"){
                $("#"+id).attr("checked",true);
            }
        });
    }
})

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

function onBack(obj){
	if($.os.phone) {
		window.parent.backElecSelect();
	}else{
		hidePopup(obj);
	}
}

function onSubmit(obj){

    $("#bd input[type=text]").each(function () {
        $(this).removeAttr("datatype");
    });
    var submitPart = "HiddenPart";
    if($.os.phone) {
        if(!$.validate.verifyAll("SUBMIT_phone")){
            return false;
        }
        submitPart = submitPart + ",SUBMIT_phone";
    }else{
        if(!$.validate.verifyAll("SUBMIT_pc")){
            return false;
        }
        submitPart = submitPart + ",SUBMIT_pc";
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
            
            var agreementData=new Wade.DataMap();
        	agreementData.put("AGREEMENT_ID",data.get('CONTRACT_NUMBER'));
        	agreementData.put("CONTRACT_WRITE_DATE",data.get('CONTRACT_WRITE_DATE'));
        	agreementData.put("CONTRACT_END_DATE",data.get('CONTRACT_END_DATE'));
        	agreementData.put("ARCHIVES_NAME",data.get('ARCHIVES_NAME'));
        	agreementData.put("PRODUCT_ID",data.get('PRODUCT_ID'));
        	tempArchiveData.put("MINOREC_PRODUCT_INFO",agreementData);
            ret.TEMP_ARCHIVE_DATA=tempArchiveData;


            if($.os.phone) {
            	window.parent.afterSubmit(tempArchiveData);

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

