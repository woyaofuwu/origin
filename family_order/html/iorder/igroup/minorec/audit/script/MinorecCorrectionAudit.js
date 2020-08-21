$(function(){
	var flag = $("#FLAG").val();
	var expInfo = $("#EXP_INFO").val();
	if(expInfo != "" && expInfo != null){
		MessageBox.error("错误信息", expInfo, function(btn){
			closeNav();
		});
	}
});

//展示电子协议
function showElec(el){
	var params = "&SHOWBUTTON=false";
	var linkAddr = $(el).attr("url");
	var archiveId = $(el).attr("archiveId");
	params += "&ARCHIVES_ID="+archiveId;
	popupPage('返回', linkAddr, 'initPage', params, null, 'full', null);
}

//点击查看产品详情信息
function openOfferPopupItem(obj)
{
	var offerId = $(obj).attr("OFFER_ID");
	var offerCode = $(obj).attr("OFFER_CODE");
	var offerMemberInfo = new Wade.DatasetList($("#OFFER_MEMBER_DATA_"+offerId).text());
	var memberOfferData = new Wade.DataMap($("#MEB_OFFER_DATA_"+offerId).text());
	var ecOfferData = new Wade.DataMap($("#EC_OFFER_DATA_"+offerId).text());
	var ecCommonInfoData = new Wade.DataMap($("#EC_COMMON_INFO_DATA_"+offerId).text());
	var ecSerialNumber = $("#EC_SERIAL_NUMBER").val();
	var pram = "&EC_COMMON_INFO_DATA="+encodeURIComponent(ecCommonInfoData.toString())+"&EC_OFFER_DATA="+encodeURIComponent(ecOfferData.toString())
	+"&MEB_OFFER_DATA="+encodeURIComponent(memberOfferData.toString())+"&OFFER_MEMBER_DATA="+encodeURIComponent(offerMemberInfo.toString())+ "&EC_SERIAL_NUMBER="+ecSerialNumber;
	 ajaxSubmit("",'analyslsOfferData',pram,"offermeberPrat,ecCommonPrat,memberOfferPrat,grpPackagePrat,ecOfferChaPrat",function(data){
	        showPopup("popup02", "setOfferDataPratPopup", true);
	        $.endPageLoading();
	    },function(error_code,error_info,derror){
	        $.endPageLoading();
	        showDetailErrorInfo(error_code,error_info,derror);
	    }
	 );
}

//整改稽核提交
function submitHAuditInfo(obj){
	if(!$.validate.verifyAll("checkRecordPart")){
		return false;
	}
	var IBSYSID  = $("#IBSYSID").val();
	var NODE_ID  = $("#NODE_ID").val();
	var BUSIFORM_NODE_ID  = $("#BUSIFORM_NODE_ID").val();
	var BPM_TEMPLET_ID  = $("#BPM_TEMPLET_ID").val();
	var BUSI_CODE  = $("#BUSI_CODE").val();
	var BUSI_TYPE  = $("#BUSI_TYPE").val();
	var MAIN_IBSYSID  = $("#MAIN_IBSYSID").val();
	var RECORD_NUM  = $("#RECORD_NUM").val();
	$.beginPageLoading("数据提交中......");
	var param = '&IBSYSID=' + IBSYSID+'&NODE_ID=' + NODE_ID+'&BUSIFORM_NODE_ID=' + BUSIFORM_NODE_ID+'&BPM_TEMPLET_ID=' + BPM_TEMPLET_ID
	+'&BUSI_CODE=' + BUSI_CODE+'&BUSI_TYPE=' + BUSI_TYPE+'&MAIN_IBSYSID=' + MAIN_IBSYSID + '&RECORD_NUM' + RECORD_NUM;
	$.ajax.submit("checkRecordPart", "submits", param,"checkRecordPart", function(data){
			$.endPageLoading();
			MessageBox.success("操作成功！", "稽核工单号："+data.get("IBSYSID"),function(){
				closeNav();
			});
		},
		function(error_code,error_info, derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
    	}
    );
}

