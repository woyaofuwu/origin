function refreshPartAtferAuth(data){
	$.ajax.submit('', 'loadChildInfo', "&USER_INFO="+data.get("USER_INFO").toString()+"&CUST_INFO="+data.get("CUST_INFO").toString()+"&ACCTDAY_INFO="+data.get("ACCTDAY_INFO").toString(), 'userInfoPart', 
		function()
		{
			$("#SubmitPart").removeClass("e_dis");
//			kqbkDataRight();
		},
		function(error_code,error_info){
			$.endPageLoading();
			$("#CSSUBMIT_BUTTON").attr("disabled", true);
			$("#SubmitPart").attr("class", "e_dis");
			alert(error_info);
		});
}


function checkBeforeSubmit(){

//	if(!$.validate.confirmAll()) return false;
//	var PhoneFlag = $("#custinfo_PhoneFlag").val();
//	if(PhoneFlag!="1"){//不是验证通过
//		alert("身份证人像比对未通过，请进行人像比对！");
//		return false;
//	}
	
//	var custinfo = $.auth.getAuthData().get("CUST_INFO");
//	var psptid = custinfo.get("PSPT_ID");
//	var custpsptid = $("#custInfo_PSPT_ID").val();
//	if(psptid != custpsptid){
//		alert("扫描证件信息与认证信息不匹配！");
//		return false;
//	}
	
	var sn = $("#AUTH_SERIAL_NUMBER").val();
	var oldSn = $("#OLD_SN").val();
	var newSn = $("#NEW_SN").val();
	
	if(sn != newSn && sn != oldSn){
		alert("服务号码必须为原号码或者新号码");
		return false;
	}
	
 	return true; 
}

function clickScanPspt()
{  	 
	getMsgByEForm("custInfo_PSPT_ID","HIDDEN_CUST_NAME","custInfo_SEX",null,null,null,null,null);
}
	
function identification(picid,picstream){
	var custName,psptId,psptType,fornt,desc;
 
	custName = $("#HIDDEN_CUST_NAME").val();
	psptId = $("#custInfo_PSPT_ID").val();
	psptType = "0";//默认身份证
	fornt = $("#custInfo_FRONTBASE64").val();
	desc = "请输入客户证件号码！";

/*	if( psptType == ""){
		alert("请选择证件类型！");
		return false;
	}*/
	if( psptId == ""){
		alert(desc);
		return false;
	}
	var blackTradeType="799";
	var sn = "";

	sn = $("#SERIAL_NUMBER").val();
	
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
	bossOriginalXml.push('	<phone>'+sn+'</phone>');				
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
	if("0" == result){	
//		alert("picid:"+picid);
		$("#"+picid).val(picID);	
		$("#"+picstream).val(picStream);
		var param = "&BLACK_TRADE_TYPE="+blackTradeType;
		param+="&CERT_ID="+psptId;
		param+="&CERT_NAME="+custName;
		param+="&CERT_TYPE="+psptType;
		param+="&SERIAL_NUMBER="+sn;
		param+="&FRONTBASE64="+escape (encodeURIComponent(fornt));
		param+="&PIC_STREAM="+picStream;
		
		$.beginPageLoading("正在进行人像比对。。。");	
		$.ajax.submit(null, "cmpPicInfo", param, '',
				function(data){
					$.endPageLoading();
					if(data && data.get("X_RESULTCODE")== "0"){
						MessageBox.success("成功提示","人像比对成功", null, null, null);		
						$("#custinfo_PhoneFlag").val("1");//比对验证成功
						return true;
					}else if(data && data.get("X_RESULTCODE")== "1"){
						$("#"+picid).val("ERROR");
						$("#"+picstream).val(data.get("X_RESULTINFO"));
						$("#custinfo_PhoneFlag").val("0");//比对验证失败
						MessageBox.error("告警提示",data.get("X_RESULTINFO"),null, null, null, null);		
						return false;
					}
				},function(code, info, detail){
					$.endPageLoading();
					$("#"+picid).val("ERROR");
					$("#"+picstream).val("人像比对信息错误，请重新拍摄！");
					$("#custinfo_PhoneFlag").val("0");//比对验证失败
					MessageBox.error("错误提示","人像比对信息错误，请重新拍摄！",null, null, null, null);
				},function(){
					$.endPageLoading();
					$("#"+picid).val("ERROR");
					$("#"+picstream).val("人像比对失败，请重新拍摄");
					$("#custinfo_PhoneFlag").val("0");//比对验证失败
					MessageBox.alert("告警提示", "人像比对失败，请重新拍摄");
			});
	}else{
		MessageBox.error("告警提示","拍摄失败！请重新拍摄",null, null, null, null);
	}
}
//是否免人像比对和身份证可手动输入权限
function kqbkDataRight(){

	$.ajax.submit(null,'kqbkDataRight','','',
			function(data){ 
		var flag=data.get("TAG");
		
		if(flag=="1"){ 
			$("#custinfo_PhoneFlag").val("1");//有免人像比对和身份证可手动输入权限
			$('#custInfo_PSPT_ID').attr("disabled",false);
			$("#PIC_ID").val("111111111111111111111111");
		}
		$.endPageLoading();
	},function(error_code,error_info){
		$.MessageBox.error(error_code,error_info);
		$.endPageLoading();
	},{
		"async" : false
	});

}
