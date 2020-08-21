
//扫描读取身份证信息
function clickScanPspt(){
	
	getMsgByEForm("custInfo_PSPT_ID","custInfo_CUST_NAME","custInfo_SEX","custInfo_FOLK_CODE","custInfo_BIRTHDAY","custInfo_PSPT_ADDR,custInfo_POST_ADDRESS",null,"custInfo_PSPT_END_DATE");
}
/**
 * 人像比对
 * @param picid
 * @param picstream
 * @returns {Boolean}
 */
function identification(picid,picstream){
	var custName,psptId,psptType,fornt;
	
	custName = $("#custInfo_CUST_NAME").val();
	psptId = $("#custInfo_PSPT_ID").val();
	psptType = $("#custInfo_PSPT_TYPE_CODE").val();
	fornt = $("#custInfo_FRONTBASE64").val();
	
	if(psptId == ""){
		alert("请输入证件号码!");
		return false;
	}

	if(psptType == ""){
		alert("请选择证件类型！");
		return false;
	}
	
	var bossOriginalXml = [];
	bossOriginalXml.push('<?xml version="1.0" encoding="utf-8"?>');
	bossOriginalXml.push('<req>');
	bossOriginalXml.push('	<billid>'+'</billid>');
	bossOriginalXml.push('	<brand_name>'+'</brand_name>');
	bossOriginalXml.push('	<brand_code>'+'</brand_code>');
	bossOriginalXml.push('	<work_name>'+'</work_name>');
	bossOriginalXml.push('	<work_no>'+'</work_no>');
	bossOriginalXml.push('	<org_info>'+'</org_info>');
	bossOriginalXml.push('	<org_name>'+'</org_name>');
	bossOriginalXml.push('	<phone>'+$("#AUTH_SERIAL_NUMBER").val()+'</phone>');
	bossOriginalXml.push('	<serv_id>'+'</serv_id>');
	bossOriginalXml.push('	<op_time>'+'</op_time>');
	
	bossOriginalXml.push('	<busi_list>');
	bossOriginalXml.push('		<busi_info>');
	bossOriginalXml.push('			<op_code>'+'</op_code>');
	bossOriginalXml.push('			<sys_accept>'+'</sys_accept>');
	bossOriginalXml.push('			<busi_detail>'+'</busi_detail>');
	bossOriginalXml.push('		</busi_info>');
	bossOriginalXml.push('	</busi_list>');

	bossOriginalXml.push('	<verify_mode>'+'</verify_mode>');
	bossOriginalXml.push('	<id_card>'+'</id_card>');
	bossOriginalXml.push('	<cust_name>'+'</cust_name>');
	bossOriginalXml.push('	<copy_flag></copy_flag>');
	bossOriginalXml.push('	<agm_flag></agm_flag>');
	bossOriginalXml.push('</req>');
	var bossOriginaStr = bossOriginalXml.join("");
	//调用拍照方法
	var resultInfo = makeActiveX.Identification(bossOriginaStr);
	//获取保存结果
	var result = makeActiveX.IdentificationInfo.result;			
	//获取保存照片ID
	var picID = makeActiveX.IdentificationInfo.pic_id;
	
	if(picID != ''){
		alert("人像摄像成功");
	}else{
		alert("人像摄像失败");
		return false;
	}	
	//获取照片流
	var picStream = makeActiveX.IdentificationInfo.pic_stream;
	picStream = escape (encodeURIComponent(picStream));
	
	var SERIAL_NUMBER=$("#AUTH_SERIAL_NUMBER").val();
	if("0" == result){
		$("#"+picid).val(picID);
		$("#"+picstream).val(picStream);
		var param = "&BLACK_TRADE_TYPE=60";			
		param += "&CERT_ID="+psptId;
		param += "&CERT_NAME="+custName;
		param += "&CERT_TYPE="+psptType;
		param += "&PIC_STREAM="+picStream+"&FRONTBASE64="+escape (encodeURIComponent(fornt));			
		param += "&SERIAL_NUMBER="+SERIAL_NUMBER;
		$.beginPageLoading("正在进行人像比对。。。");
		$.ajax.submit(null, 'cmpPicInfo', param, '', function(data){
			$.endPageLoading(); 
			if(data && data.get("X_RESULTCODE")== "0"){			
				MessageBox.success("成功提示","人像比对成功", null, null, null);
				return true;
			}else if(data && data.get("X_RESULTCODE")== "1"){
				//$("#"+picid).val("ERROR");
				//$("#"+picstream).val(data.get("X_RESULTINFO"));
				MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);
				return false;
			}
		},
		function(error_code,error_info,derror){
			//$("#"+picid).val("ERROR");
			$("#"+picstream).val("人像比对失败,请重新拍摄");
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
	    });
	}else{
		MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
	}
}