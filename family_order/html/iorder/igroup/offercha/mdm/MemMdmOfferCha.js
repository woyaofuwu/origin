function initPageParam_110000840201() {  
	var number=1;
	$("input[name='pam_TWOCHECK_SMS_FLAG']").val("1");
	var methodName=$("#cond_OPER_TYPE").val();
	if(methodName!='CrtMb'){
		//$("input[name='pam_TWOCHECK_SMS_FLAG']").parent().parent().css("display","none");
		$("#productParamPart").css("display","none");
	}
	/*window["DSParams"] = new Wade.Table("DSParams", {
		fixedMode:true,
		editMode:true,
	});*/
	//获取隐藏域表格值
	/*var dsParamTable = new Wade.DatasetList($("#userParamInfos").val());
	$.each(dsParamTable,function(index,data) {
		var insertData=new Wade.DataMap();
		insertData.put("SEQ",number);
		insertData.put("DS_CODE",data.get("pam_DISCNT_CODE"));
		insertData.put("DS_NAME",data.get("pam_DISCNT_NAME"));
		insertData.put("CDS_CODE",data.get("pam_CDISCNT_CODE"));
		insertData.put("START_TIME",data.get("pam_START_DATE"));
		insertData.put("END_TIME",data.get("pam_END_DATE"));
		DSParams.addRow($.parseJSON(insertData.toString()));
		//不知道为什么成员商品里面不能new出Wade.Table，只能用这么蠢的办法往表里面塞数据
		var dsTableHtml='<tr><td>'+number+'</td>';
        dsTableHtml+='<td>'+data.get("pam_DISCNT_CODE")+'</td>';
        dsTableHtml+='<td>'+data.get("pam_DISCNT_NAME")+'</td>';
        dsTableHtml+='<td>'+data.get("pam_CDISCNT_CODE")+'</td>';
        dsTableHtml+='<td>'+data.get("pam_START_DATE")+'</td>';
        dsTableHtml+='<td>'+data.get("pam_END_DATE")+'</td></tr>';
        $('#DSParams tbody').append(dsTableHtml);
		number++;
	});
    $("#DSParams tbody tr").attr("class","");//去掉背景色
    */
    //REQ201812200001关于优化集团产品二次确认功能的需求
	$.ajax.submit("", "queryTwoCheck", "", "", function(data){
		$.endPageLoading();
		if(data != null)
		{
			if(data.get("TWOCHECK") == '1'){
				$("input[name='pam_TWOCHECK_SMS_FLAG']").attr("disabled","");
				$("input[name='pam_TWOCHECK_SMS_FLAG']").attr("checked","");
			}
		}
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}


function validateParamPage(methodName) {
	$("input[name='pam_TWOCHECK_SMS_FLAG']").val("1");
	if(methodName=='CrtMb' || methodName=='ChgMb'){
		var payPlanList = $("input[name='pam_TWOCHECK_SMS_FLAG']:checked") ;
		if(payPlanList.length==0){
			if(!confirm("当前业务办理涉及不知情订购，若需用户短信二次确认请在界面中选择，重新选择请点【取消】按钮！")){
				return false;
			}else{
				$("input[name='pam_TWOCHECK_SMS_FLAG']").val("");
			}
		}
	}
	return true;
}

//提交
function checkSub(obj)
{
	$("#userParamInfos").val("");
	var offerTpye = $("#cond_OPER_TYPE").val();
	if(!validateParamPage(offerTpye))
		return false;
	
	if(!submitOfferCha())
		return false; 
	
	backPopup(obj);
}

 
