$(function(){
	debugger;
	selectUnitType();
});
//加载合同组件
function initPageParam(){
	
}
function initContractPopupItem()
{
//	if(!isSwitchOff())
//	{//如果上一次打开商品设置区域没有提交，则本次不让打开
//		return ;
//	}
//	showPopup('popup08','staffPopupItem',true);
	updateShowToPop('popup12');
	queryContractInfo();
//	queryContractInfoShowToPop('popup12');
//	showPopup('popup15', 'QryContractPopupItem', true);
}
/**
 * 合同组件回调
 */
function contractPopupItemCallback(data) {
	var node = new Wade.DataMap($("#NODE_TEMPLETE").text());
	var bpmTempletId = node.get("BPM_TEMPLET_ID");
//	var bpmTempId = $("#BPM_TEMPLET_ID").val();
	if(bpmTempletId=="EDIRECTLINEOPENIDC"){
		var contractInfo = data.get("CONTRACT_INFO");
		var offerCode = $("#cond_OFFER_CODE").val();
		if(contractInfo.get("OFFER_IDS").indexOf(offerCode)>=0){
			var param="&CONTRACT_ID="+contractInfo.get("CONTRACT_ID");
			$.beginPageLoading();
			$.ajax.submit('','getContractFileByContractId',param,'IDC_CONTRACT_Info',function(data){
				debugger;
				var data1 = new Wade.DataMap();
				data1.put("FILE_ID", data.get("IDC_C_FILE_LIST"));
				data1.put("FILE_NAME", data.get("IDC_C_FILE_LIST_NAME"));
				data1.put("ATTACH_TYPE", "C");
				$("#IDC_C_FILE_LIST").text(data1.toString());
				$("#IDC_C_FILE_LIST").val(data.get("IDC_C_FILE_LIST")+':'+data.get("IDC_C_FILE_LIST_NAME"));
				
				$("#IDC_C_FILE_LIST_NAME").val(data.get("IDC_C_FILE_LIST_NAME"));
				$.endPageLoading();
				$("#IDC_ContractId").val(contractInfo.get("CONTRACT_ID"));
			},
			function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
		    });
		}else{
			MessageBox.alert("业务错误", "所选该合同"+contractInfo.get("CONTRACT_ID")+"无当前所选择的产品! "+offerCode);
			return false;
		}
	}
}

function deleteIdcParam()
{
	var checkedNum = getCheckedBoxNum("IDCDATALIST_TAG");
	if(checkedNum < 1)
	{
		MessageBox.alert("提示信息", "请选择需要删除的信息！");
		return false;
	}
	$("input:checkbox[name=IDCDATALIST_TAG]:checked").each(function(){
		var checkBoxId = $(this).attr("id");
//		$("#"+checkBoxId+"_LI").remove();
		$("#"+checkBoxId+"_LI").css("display", "none");
		$(this).attr("checked","");
	});
}
function checkAllIdcParam(el){
	
	$("input:checkbox[name=IDCDATALIST_TAG]").each(function(){
		var checkBoxId = $(this).attr("id");
//		$("#"+checkBoxId+"_LI").remove();
		if($("#"+checkBoxId+"_LI").css("display")!="none"){
			$(this).attr("checked",$("#"+el.id).attr("checked"));
		}
	});

}


function selectRelIbsysidForIdc(el)
{
	debugger;
	
	if(el.value==''){
		changeTempletId();
		return false;
	}
	var relIbsysidId=el.id;
	var relIbsysidValue=el.value;
	var offerCode = $("#cond_OFFER_CODE").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one(document.getElementById("cond_OFFER_CODE"), "请选择产品！");
		return false;
	}
	
	var userId = $("#apply_USER_ID").val();
	var opertype = $("#cond_OPER_TYPE").val();
	var templetId = $("#cond_TEMPLET_ID").val();
	if(!templetId){
		return false;
	}
	var pattrTitle = $("#OrderPart input[name=pattr_TITLE]").val();
	var pattrLevel = $("#OrderPart input[name=pattr_URGENCY_LEVEL]").val();
	
	var param = "&TEMPLET_ID="+templetId+"&EC_USER_ID="+userId+"&OFFER_CODE="+offerCode+"&OPER_TYPE="+$("#cond_OPER_TYPE").val()+"&CUST_ID="+$("#cond_CUST_ID").val()+"&GROUP_ID="+$("#cond_GROUP_ID").val();
	
	
	param += "&rel_ibsysid="+relIbsysidValue;
	$.beginPageLoading();
	$.ajax.submit('','selectRelIbsysidForIdc',param,'IdcDataApplyParamPart',function(data){
		$("#"+relIbsysidId).val(relIbsysidValue);
		selectUnitType();
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function selectOrderNumFlag(el)
{
	debugger;
	
	if(el.value==''){
		return false;
	}
	var id=el.id;
	var value=el.value;
	if(value=='1'){//是
		$("#ORDER_ChooseOpenType_Li").css("display", "");
		$("#ORDER_ChooseOpenType").attr("nullable", "no");
		
		$("#ORDER_ChooseFirstIbsysid_Li").css("display", "none");
		$("#ORDER_ChooseFirstIbsysid").attr("nullable", "yes");
		$("#ORDER_ChooseFirstIbsysid").val("");
	}else{
		$("#ORDER_ChooseOpenType_Li").css("display", "none");
		$("#ORDER_ChooseOpenType").attr("nullable", "yes");
		$("#ORDER_ChooseOpenType").val("");
		
		$("#ORDER_ChooseFirstIbsysid_Li").css("display", "");
		$("#ORDER_ChooseFirstIbsysid").attr("nullable", "no");
		
	}
	
}

function queryIdcOperType(){
	debugger;
	var offerCode = $("#cond_OFFER_CODE").val();
	ajaxSubmit(this,'queryIdcOperType',null,"operTypePart",function (){
		var operTypeIDCList= $("#operTypeTable input[name=operTypeIDC]");
		for(var i = 0, size = operTypeIDCList.length; i < size; i++)
		{
			var operTypeIDCValue = operTypeIDCList[i].value;
			if(offerCode==operTypeIDCValue){
				operTypeIDCList[i].checked="true";
				operTypeIDCList[i].disabled="true";
				break;
			}

		}
		
	},null);
}
function operTypeReturn(){
	debugger;
	var operTypeIDCStr="";
	var checkboxs = $("#operTypeTable input[name=operTypeIDC]:checked");
	for(var i=0;i<checkboxs.length;i++){
		if(checkboxs[i].checked){
			if(i==checkboxs.length-1){
				operTypeIDCStr += $(checkboxs[i]).val();
			}else{
				operTypeIDCStr += $(checkboxs[i]).val()+",";
			}
		}
		
	}
	if(operTypeIDCStr!=""){
		$("#ORDER_ChooseOpenType").val(operTypeIDCStr);
		checkOrderNumFlagForIdc();
	}
	hidePopup("popup13");
}

function checkOrderNumFlagForIdc()
{
	debugger;
	var orderNumFlagVal=$("#ORDER_OrderNumFlag").val();
	if(orderNumFlagVal==''){
		MessageBox.alert("提醒", "请先选择【填写该笔是否为总订单第一笔订单】后再选择该字段！");
		$("#ORDER_ChooseFirstIbsysid").val("");
		return false;
	}
	var chooseOpenType=$("#ORDER_ChooseOpenType").val();
	var chooseFirstIbsysid=$("#ORDER_ChooseFirstIbsysid").val();
	if(orderNumFlagVal=='1'){//是第一笔
		if(orderNumFlagVal==''){
			MessageBox.alert("提醒", "请先选择【选择总订单关联的所有产品类型】后再选择该字段！");
			return false;
		}
	}else{
		if(chooseFirstIbsysid==''){
//			MessageBox.alert("提醒", "请先选择【关联总订单第一笔订单】后再选择该字段！");
			return false;

		}
	}
	
	var offerCode = $("#cond_OFFER_CODE").val();
	if(offerCode == null || offerCode == "")
	{
		$.validate.alerter.one(document.getElementById("cond_OFFER_CODE"), "请选择产品！");
		return false;
	}
	
	var param = "&ORDER_OrderNumFlag="+orderNumFlagVal+"&ORDER_ChooseOpenType="+chooseOpenType
	+"&ORDER_ChooseFirstIbsysid="+chooseFirstIbsysid+"&OFFER_CODE="+offerCode;
	$.beginPageLoading();
	$.ajax.submit('','checkOrderNumFlagForIdc',param,null,function(data){
		var lastIbsysidFlag=data.get("ORDER_LastIbsysidFlag");
		if(lastIbsysidFlag!=null){
			$("#ORDER_LastIbsysidFlag").val(data.get("ORDER_LastIbsysidFlag"));
		}else{
			MessageBox.alert("提醒", "查询后返回【lastIbsysidFlag】值不存在！");
			$("#ORDER_ChooseFirstIbsysid").val("");

		}
		$.endPageLoading();
	},
	function(error_code,error_info,derror){
		$.endPageLoading();
		$("#ORDER_ChooseFirstIbsysid").val("");
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function selectUnitType()
{
	debugger;
	
	if($("#CUST_unitType").val()==''){
		return false;
	}
	if($("#CUST_unitType").val()=='1'){//电信业务经营者
		$("#CUST_manageLicenceNum_Li").css("display", "");
		$("#CUST_manageLicenceNum").attr("nullable", "no");
	}else{
		$("#CUST_manageLicenceNum_Li").css("display", "none");
		$("#CUST_manageLicenceNum").attr("nullable", "yes");
		$("#CUST_manageLicenceNum").val("");
	}
	
}