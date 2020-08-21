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
	var param = "&EC_COMMON_INFO_DATA="+encodeURIComponent(ecCommonInfoData.toString())+"&EC_OFFER_DATA="+encodeURIComponent(ecOfferData.toString())
		+"&MEB_OFFER_DATA="+encodeURIComponent(memberOfferData.toString())+"&OFFER_MEMBER_DATA="+encodeURIComponent(offerMemberInfo.toString()) + "&EC_SERIAL_NUMBER="+ecSerialNumber;
	 ajaxSubmit("",'analyslsOfferData',param,"offermeberPrat,ecCommonPrat,memberOfferPrat,grpPackagePrat,ecOfferChaPrat",function(data){
	        showPopup("popup02", "setOfferDataPratPopup", true);
	        $.endPageLoading();
	    },function(error_code,error_info,derror){
	        $.endPageLoading();
	        showDetailErrorInfo(error_code,error_info,derror);
	    }
	 );
}


//稽核提交
function submitInfos(){
	var tariffInfo=$("#TARIFF_FLAG").val();
	if(tariffInfo == "" || tariffInfo == null) 
	{
		$.validate.alerter.one($("#TARIFF_FLAG")[0], "稽核信息必选!\n");
		return false;
	}
	var table = new Wade.DatasetList();
	table = getTables();
	if(table == null || table.length == 0){
		MessageBox.alert("错误","您未选择产品，请重新操作后再提交！");
		return false;
	}
	var ibsysId= $("#IBSYSID").val();
	var productId = $("#PRODUCT_ID").val();
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	var busiformId = $("#BUSIFORM_ID").val();
	$.beginPageLoading("提交中...");
	var param = "&ROWDATAS=" + table + "&IBSYSID=" + ibsysId + "&PRODUCT_ID=" + productId + "&BPM_TEMPLET_ID=" + bpmTempletId + "&BUSIFORM_ID=" + busiformId;
	$.ajax.submit("checkRecordPart", "submit", param, "", function(data){
		$.endPageLoading();
		var ibsysid = data.get("IBSYSID");
		if(ibsysid == "" || ibsysid == null){
			MessageBox.success("操作成功！", "", function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}else{
			MessageBox.success("操作成功！", "稽核工单号：" + ibsysid, function(btn){
				if("ok" == btn){
					closeNav();
				}
			});
		}
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}
//获取选中稽核数据
function getTables(){
	var chk = document.getElementsByName("TRADES");
	var tables = new Wade.DatasetList();
	if(chk) {
		for(var i = 0;i < chk.length; i++){
            if(chk[i].checked){
				var table = new Wade.DataMap();
				table.put("USER_ID",chk[i].getAttribute("userid"));
				table.put("IBSYSID",chk[i].getAttribute("ibsysid"));
				table.put("TRADE_ID",chk[i].getAttribute("tradeid"));
				table.put("RECORD_NUM",chk[i].getAttribute("recordNum"));
				tables.add(table);
            }
       	}
		
	}
	return tables;
}
