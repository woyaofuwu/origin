var data;

$(document).ready(function(){
	(function($){
	    $.extend({archives:{
	            events:{
	                uploadImage:function(retStr){
	                    var archiveInfo = new Wade.DataMap($('#TEMP_ARCHIVE_DATA_PARENT').val());
	                    var param = "&base64Bitmap="+encodeURIComponent(retStr.toString())+"&ARCHIVES_NAME="+archiveInfo.get("ARCHIVES_NAME")+"&ARCHIVES_ID="+archiveInfo.get("ARCHIVE_ID");
	                    $.beginPageLoading("数据上传中......");
	                    $.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.minorec.elecagreement.handler.ElecAgreementHandler", "uploadImage", param,
	                        function (data) {
	                            $.endPageLoading();
	                            var fileId = data.get("FILE_ID");
	                            try{
	                            	backElecSelect();
	                            }catch (e) {
									alert(e);
								}
                                /*setTimeout(
                                    function(){
                                        MessageBox.alert("温馨提示", "签名上传成功！");
                                    },500);*/

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
		
	applyFrame.init();
});


function afterContractModi(el) {
	$("#applyFrame")[0].contentWindow.afterContractModi(el);
}

function afterElecContract(el) {
	$("#applyFrame")[0].contentWindow.afterElecContract(el);
}

//设置返回区域的值
function afterSubmit(temp){
	$("#TEMP_ARCHIVE_DATA_PARENT").val(temp);
	
	$("#applyFrame")[0].contentWindow.afterSubmit(temp);
	//$('#TEMP_ARCHIVE_DATA').val(temp);
	//afterElecContract(el);
}


//显示协议填写页面
function showElecFrame(param){
	var page = param.get("PAGE");
	var params = param.get("PARAM");
	elecFrame.redirect(page, "initPage", params, "iorder");
	
	data = $("#applyFrame")[0].contentWindow.getHtmlParams();
	$("#HTML_DATA").val(data);
	
	$("#applyFrame").parent().css("display","none");
	$("#elecFrame").parent().css("display","");
	
}

function backElecSelect(){
	//var htmlData = new Wade.DataMap($("#HTML_DATA").val());
	//alert(htmlData.toString());
	// var eleData = $("#applyFrame")[0].contentWindow.getEleData();
	// var groupId = htmlData.get("cond_GROUP_ID_INPUT");
	// var templetId = htmlData.get("cond_TEMPLET_ID");
    //applyFrame.redirect("igroup.minorec.MinorecSpeedinessApply", "", "&PhoneInit=true", "iorder");
    elecFrame.redirect("igroup.minorec.MinorecSpeedinessApply", "", "&PhoneInit=true", "iorder");
	
	/*$("#cond_GROUP_ID_INPUT").val(groupId);
	$("#applyFrame")[0].contentWindow.queryGroupInfoForPhone(groupId,templetId,htmlData);
	var param = "&PRODUCT_ID="+eleData.get("PRODUCT_ID")+"&GROUP_ID="+groupId+"&CONTRACT_ID="+eleData.get("CONTRACT_NUMBER");
	param += "&ACTION=init";
	$("#applyFrame")[0].contentWindow.showEle(param);*/
	try {
        $("#applyFrame")[0].contentWindow.initContract();
    }catch (e) {
		alert(e);
    }

	
	$("#applyFrame").parent().css("display","");
	$("#elecFrame").parent().css("display","none");
}

function getInitData() {
    if($("#HTML_DATA").val()){
        return new Wade.DataMap($("#HTML_DATA").val());
    }
}
