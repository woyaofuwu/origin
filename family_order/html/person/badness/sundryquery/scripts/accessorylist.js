function openStickList(infoRecvId){
	$.beginPageLoading();		
	ajaxGet("badness.AccessoryList",'checkFileExsist','&INFO_RECV_ID='+infoRecvId,null, 
    	function(data){
    		$.endPageLoading();
    		 afterDo(data);
		},
		function(error_code,error_info){
		
			$.endPageLoading();
			showErrorInfo(error_code,error_info);
	    });
}

function afterDo(ajaxDataset){
	var result = ajaxDataset.get(0, "RESULT_MESSAGE");
	if(result=="0"){
		alert("举报信息不存在！");
	}else if(result=="1"){
		alert("彩信内容附件尚未送达，请稍后再试！");
	}else{
		var inforecvId = ajaxDataset.get("INFO_RECV_ID");
		var rsrvStr4 = ajaxDataset.get("RSRV_STR4");
		var recvProvince = ajaxDataset.get("RECV_PROVINCE");
		var stickList = ajaxDataset.get("STICK_LIST");
		var param = '&INFO_RECV_ID='+inforecvId+'&RSRV_STR4='+rsrvStr4+'&RECV_PROVINCE='+recvProvince+'&STICK_LIST='+stickList;
		//alert(param);
		$.popupDialog('badness.AccessoryList', 'queryAccessoryList', param, '垃圾彩信附件列表', '800', '400',null);
	}	
}