//function initPageParam_110000006200(){
//	var serialNumber = $("#pam_GRP_PAY").val("0");
//	var serialNumber1 = $("#pam_PAY_SN_SMS").val("1");
//}

function shortNumColorring()
{
	//短号码验证
	var serialNumber = $("#pam_PAY_SN_1").val();
	var serialNumber1 = $("#pam_PAY_SN").val();
	var re = /[^\d]/g;
	if(re.test(serialNumber)){
		$.validate.alerter.one($("#pam_PAY_SN_1")[0],"主付费号码只能为数字，请重新输入！");
		return false;
	}
	if( serialNumber.length != 11 ) {  
	    $.validate.alerter.one($("#pam_PAY_SN_1")[0], "主付费号码只能为11位手机号码，请重新输入!\n");
		return false;
	}
	if(null==serialNumber|| "" == serialNumber){
		$.validate.alerter.one($("#pam_PAY_SN_1")[0], "您输入的主付费号码为空，请输入后再验证!\n");			
		return false; 
    }
	if(serialNumber==serialNumber1){
		$.validate.alerter.one($("#pam_PAY_SN_1")[0], "您输入的主付费号码已验证，不需要重复验证!\n");			
		return false;
	}
	
	$.beginPageLoading("数据加载中......");
	$.httphandler.post("com.asiainfo.veris.crm.iorder.web.igroup.colorring.ColorringHandler", "shortNumColorringAdmin",'&SERIAL_NUMBER='+ serialNumber, function(data){
		$.endPageLoading();
		sucShortNumRes(data,serialNumber);
		},    
		function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });

}
function sucShortNumRes(data,serialNumber){
	if("true"== data.get("RESULT"))
	{
		$("#pam_PAY_SN").val(serialNumber);
		MessageBox.success("验证结果", "恭喜您,主付费号码"+serialNumber+"验证成功!", function(){
		});
	}
	else
	{
		MessageBox.alert("验证提示",data.get("ERROR_MESSAGE")+"验证失败,请重新输入号码！");
	}
}
function submitColorring(obj)
{
	
	if(!submitOfferCha())
		return false; 
	var paySn = $("#pam_PAY_SN").val();
	var paySn1 = $("#pam_PAY_SN_1").val();
	
	if(null==paySn|| "" == paySn){
		$.validate.alerter.one($("#pam_PAY_SN_1")[0], "您没有验证主付费号码，请验证!\n");			
		return false; 
    }
	
	if(paySn!=paySn1){
		$.validate.alerter.one($("#pam_PAY_SN_1")[0], "您输入的主付费号码与验证号码不一致，请重新验证!\n");			
		return false; 
		}
	
	var ringNum = $("#pam_MAX_RINGNUMBER").val(); 
	var re = /[^\d]/g;
	if(re.test(ringNum)){
		$.validate.alerter.one($("#pam_MAX_RINGNUMBER")[0],"最大铃声数量只能为数字，请重新输入！");
		return false;
	}
	
	var  ringNum1 = parseInt(ringNum);
	if(ringNum1 > 10){
		$.validate.alerter.one($("#pam_MAX_RINGNUMBER")[0], "最大铃声数量不能超过10个，请重新输入!\n");			
	   	return false;
	}
	if(ringNum1< 1){
		$.validate.alerter.one($("#pam_MAX_RINGNUMBER")[0], "最大铃声数量不能少于1个，请重新输入!\n");			
	   	return false;
	}
	
	
	var sysDate = new Date();
	var year = sysDate.getFullYear(); //获取年 
	var month = sysDate.getMonth()+1;//获取月  
	var date = sysDate.getDate(); //获取当日
	var time = year+"/"+month+"/"+date; //组合
	var grpEndDate = $("#pam_GRP_END_DATE").val();
	if("" != grpEndDate){
		var startTime = new Date(Date.parse(time));
		var endTime = new Date(Date.parse(grpEndDate));
		if(endTime <= startTime ){
			$.validate.alerter.one($("#pam_GRP_END_DATE")[0], "产品参数信息中,集团彩铃使用结束日期不能小于当前日期！\n");			
		   	return false;
		}
		var date = grpEndDate.split("/");
		var year = date[0];
		var month = date[1];
		var day = new Date(year, month, 0);
		var lastDate = year + "/" + month + "/" + day.getDate();
		if (grpEndDate != lastDate) {
			$.validate.alerter.one($("#pam_GRP_END_DATE")[0], "产品参数信息中,集团彩铃使用结束日期只能为选择月份的最后一天！\n");			
		   	return false;
		}
		
	}

	backPopup(obj);
}