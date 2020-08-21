var group_name_is_exist = false;
var group_name_check = true;

function selectManagerValue(obj)
{
	$("#GROUP_MGR_SN").val(obj.attr('SERIAL_NUMBER'));
	$("#GROUP_MGR_CUST_NAME").val(obj.attr('CUST_NAME'));
	hidePopup('qryManagerPopup');
}

function clearManager()
{
	$("#GROUP_MGR_SN").val('');
	$("#GROUP_MGR_CUST_NAME").val('');
	hidePopup('qryManagerPopup');
}

function selectSocValue(obj)
{
	$("#SC_CODE").val(obj.attr('SC_CODE'));
	$("#ORG_CODE").val(obj.attr('ORG_CODE'));
	hidePopup('qrySocPopup');
}

function clearSoc()
{
	$("#SC_CODE").val('');
	$("#ORG_CODE").val('');
	hidePopup('qrySocPopup');
}

function selectParentGroupValue(obj)
{
	$("#SUPER_GROUP_ID").val(obj.attr('GROUP_ID'));
	$("#SUPER_GROUP_NAME").val(obj.attr('CUST_NAME'));
	hidePopup('qryParentGroupPopup');
}

function clearParentGroup()
{
	$("#SUPER_GROUP_ID").val('');
	$("#SUPER_GROUP_NAME").val('');
	hidePopup('qryParentGroupPopup');
}

/**
 * 客户经理查询
 */
function openCustManagerPopup(val){
	var params = '&cond_PARAMS='+val+'&cond_CLOSEPOPUP=false';
	popupDialog('选择客户经理信息', 'customer.vc.popup.cust.ManagerSelect', 
			'initCusts',params , null, '71.42em', '35.71em', true, null, null);
}

$(function(){
	changeRsrvNum3();
	var activeDateField;
	function setRegistDate1Value(dateField){
		var el = dateField && dateField.nodeType == 1 ? dateField : document.getElementById(dateField);
		if(!el) return;
		calendar1.val(el.value);
		activeDateField = el;
	}
	
	$("#qryManagerButton").tap(function(){
		var check = $.validate.verifyAll("qryPopup");
		if(check != true){
			return;
		}
		$.beginPageLoading('正在查询..');
		ajaxSubmit('qryPopup','queryManager','','QryManagerResultPart',function(data){
			hidePopup('qryPopup');
			$.endPageLoading();
		},function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误信息", error_info, function(btn){	});
		});
	});
	
	$("#qrySocButton").tap(function(){
		var check = $.validate.verifyAll("qryPopup1");
		if(check != true){
			return;
		}
		$.beginPageLoading('正在查询..');
		ajaxSubmit('qryPopup1','querySoc','','QrySocResultPart',function(data){
			hidePopup('qryPopup1');
			$.endPageLoading();
		},function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误信息", error_info, function(btn){	});
		});
	});
	
	$("#qryParentGroupButton").tap(function(){
		var check = $.validate.verifyAll("qryPopup2");
		if(check != true){
			return;
		}
		$.beginPageLoading('正在查询..');
		ajaxSubmit('qryPopup2','queryParentGroup','','QryParentGroupResultPart',function(data){
			hidePopup('qryPopup2');
			$.endPageLoading();
		},function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误信息", error_info, function(btn){	});
		});
	});
	

	//绑定下拉框change事件
	$("#CALLING_TYPE_CODE").bind("change", function(){
		var industryCode = this.value; //当前值
		if(industryCode == ''){
			return;
		}		
		ajaxSubmit('','getIndustryType','&CALLING_TYPE_CODE='+industryCode,"subIndustryPart",function(data){
		},function(error_code,error_info){
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	});
	
	$("#EXT_ISSC").bind("change", function(){
		var isLinealTag = this.value; //当前值
		if(isLinealTag == '' || isLinealTag == '0'){
			Wade($("#scCodeLi")).removeClass('required');
			Wade($("#orgCodeLi")).removeClass('required');
			$("#SC_CODE").attr('desc','');
			$("#ORG_CODE").attr('desc','');
			$("#SC_CODE").val('');
			$("#ORG_CODE").val('');
			$("#SC_CODE").attr('disabled','disabled');
			$("#ORG_CODE").attr('disabled','disabled');
			$("#sc").attr('ontap','');
		}else{
			Wade($("#scCodeLi")).addClass('required');
			Wade($("#orgCodeLi")).addClass('required');
			$("#SC_CODE").attr('desc','直管客户编码');
			$("#ORG_CODE").attr('desc','组织机构代码');
			$("#SC_CODE").attr('disabled','');
			$("#ORG_CODE").attr('disabled','');
			$("#sc").attr('ontap',"showPopup('qrySocPopup', 'querySocPopup')");
		}
	});
	
	$("#BUSINESS_LICENSE").afterAction(function(e, file){
		$("#BUSINESS_LICENSE_NAME").val(file.name); // 从 file 对象中获取文件名和上传后的文件 id
	});
	
	$("#submitButton").tap(function(){
		var check = $.validate.verifyAll("EditPart");
		if(check != true){
			return;
		}
		var check1 = $.validate.verifyAll("ExtendPart");	
		if(check1 != true){
			return;
		}
		
		var type = $('#BUSI_LICENCE_TYPE').val();
		if(type=="E"){
			if(!$.validate.verifyAll("enterprisePart")){
				return;
			}
		}else if(type=="3"){
			if(!$.validate.verifyAll("orgPart")){
				return;
			}
		}
		
		if(!validPspt()){
			return;
		}
		
		if(!validGroupInfo_hain()){
			return;
		}
		var groupName = $("#CUST_NAME").val();
		if(groupName == ''){
			$.validate.verifyAll("EditPart");
		}else{
			$.beginPageLoading("正在校验....");
			ajaxSubmit('','checkEnterName','&CUST_NAME='+groupName,null,function(data){
				$.endPageLoading();
				if(data.get("isExit") == '0'){
					MessageBox.alert("提示信息","集团名称已经存在!",function(btn){});
				}else{
					
				/*	var check2 = checlUpload();
					if(check2 != true){
						return;
					}*/
					if(check == true && check1 == true){
						if($('#RSRV_NUM3').val() =='1') {
							tip = "你确定要创建【铁通集团客户】吗？";
						} else {
							tip = "你确定要创建【移动集团客户】吗？";
						}
						MessageBox.confirm("提示信息", tip, function(btn){
							if(btn=='ok'){
								$.beginPageLoading("正在提交....");
								ajaxSubmit('EditPart,popup','addEnterprise','&LICENCE_INFO_name='+$('#LICENCE_INFO_name').val(),null,function(data){
									$.endPageLoading();
									if(data.get("message") != null){
										MessageBox.error(data.get("message"));
									}else{
										MessageBox.success("成功信息","提交成功!</br> 新增集团编码:"+data.get("GROUP_ID"),function(btn){});
										document.getElementById("submitButton").disabled = true;
										document.getElementById("submitButton").style.background = "grey";
										$("#GROUP_ID").val(data.get("GROUP_ID"));
										$("#showGroupId").css('display','');
									}
								},function(error_code,error_info){
									$.endPageLoading();
									MessageBox.error("错误信息!", error_info, function(btn){});
								});
					        }
						});
					}
				       }
			},function(error_code,error_info){
				$.endPageLoading();
				MessageBox.error("错误信息!",error_info,function(btn){});
			});
		}
	});
	
	$("#checkEnterName").tap(function(){
		
		var rsrv_num3 = $("#RSRV_NUM3").val();
		var group_name_val = $('#CUST_NAME').val();
		//var group_add = $('#ADDR_DETAIL').val();
		if(group_name_val==null || group_name_val=='')
		{
			MessageBox.alert('请输入集团名称！');
			return false;
		}else
		{
			if(group_name_val.length < 4)
			{
				MessageBox.alert("集团名称输入不少于4个中文汉字或4个字符");
		 		return false;
			}
		}
		$.beginPageLoading("正在校验....");
		ajaxSubmit('','checkEnterName','&CUST_NAME='+group_name_val+'&RSRV_NUM3='+rsrv_num3,null,function(data){
			$.endPageLoading();
			if(data.get("isExit") == '0'){
				group_name_is_exist = true;
				MessageBox.alert("提示信息","集团名称已经存在!",function(btn){});
			}else{
				group_name_is_exist = false;
				group_name_check = true;
				MessageBox.success("提示信息","集团名称可以使用!",function(btn){});
			}
		},function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误信息!",error_info,function(btn){});
		});
	});
	
	$("#addGrpPspt").tap(function(){
		Wade($("#psptTypeLi")).addClass('required');
		Wade($("#psptIdLi")).addClass('required');
		Wade($("#psptImageLi")).addClass('required');
		$("#GRP_PSPT_TYPE").attr('desc','集团客户资质证件类型');
		$("#GRP_PSPT_ID").attr('desc','资质证件号码');
		$("#GRP_PSPT_IMAGE").attr('desc','集团客户资质证件');
		var check = $.validate.verifyAll("psptPart");
		if(check == true){
			var psptType = GRP_PSPT_TYPE.selectedText;
			var psptTypeId = $("#GRP_PSPT_TYPE").val();
			var psptNo = $("#GRP_PSPT_ID").val();
			var psptImage = $("#GRP_PSPT_IMAGE_name").val();
			var psptImageId = $("#GRP_PSPT_IMAGE").val();
			psptTable.addRow({
				"GRP_PSPT_TYPE":psptType,
				"GRP_PSPT_TYPE_ID":psptTypeId,
				"GRP_PSPT_ID":psptNo,
				"GRP_PSPT_FILE":"<a href='attach?action=download&fileId="+psptImageId+"' >"+psptImage+"</a>",
				"GRP_PSPT_FILE_ID":psptImageId
			});
			$("#GRP_PSPT_TYPE").val('');
			$("#GRP_PSPT_ID").val('');
			$("#GRP_PSPT_IMAGE").val('');
			$("#GRP_PSPT_IMAGE_name").val('文件大小限制：30MB 文件类型：(.jpg,.png)');
			$("#GRP_PSPT_IMAGE_btn_close").css('display','none');
			$("#GRP_PSPT_IMAGE_btn_download").css('display','none');
			Wade($("#psptTypeLi")).removeClass('required');
			Wade($("#psptIdLi")).removeClass('required');
			Wade($("#psptImageLi")).removeClass('required');
			$("#GRP_PSPT_TYPE").attr('desc','');
			$("#GRP_PSPT_ID").attr('desc','');
			$("#GRP_PSPT_IMAGE").attr('desc','');
		}
	});
	
	$("#qryOutSynMgrButton").tap(function(){
		$.beginPageLoading('正在查询..');
		ajaxSubmit('qryOutSynPopup','queryOutGroupSynInfo','','QryOutSynGrpResultPart',function(data){
			hidePopup('qryOutSynPopup');
			$.endPageLoading();
		},function(error_code,error_info){
			$.endPageLoading();
			MessageBox.error("错误信息", error_info, function(btn){	});
		});
	});
	
	var orgTypeVal = $("#ORG_TYPE_A").val();
	if(($("#ORG_TYPE_A").val() == "3") && ($("#ORG_TYPE_A_FLAG").val() == "1")){
		$("#ORG_TYPE_A").attr("disabled","true");		
		$("#ORG_TYPE_D").attr("nullable","no");
		$("#GRID_CODE").attr("nullable","no");
		$("#ogrTypeD").css('display','');
		$("#grid").css('display','');
		$("#classDType").css('display','');
		//getElement('gridPart').style.display='';
		ajaxSubmit('','setOgrType','&OGR_TYPE=A'+'&OGR_TYPE_VALUE='+orgTypeVal,'ogrTypeB,classDTypePart',function(data){
			
		},function(error_code,error_info){
			MessageBox.error("错误信息!", error_info, function(btn){});
		});		
		//ajaxDirect(this,'getOrgTypeB','&ORG_TYPE_A=' + getElement("ORG_TYPE_A").value,'OrgPartB',null,null);
		//ajaxDirect(this,'getOrgTypeD','&ORG_TYPE_A=' + getElement("ORG_TYPE_A").value,'OrgPartD',null,null);
	}
	if($("#ACCEPT_CHANNEL").val()=="7"){		
		$("ACCEPT_CHANNEL").val('7');
		$("SERVICE_CHANNEL").val('7');
		$("#ACCEPT_CHANNEL").attr("disabled","true");
		$("#SERVICE_CHANNEL").attr("disabled","true");		
	}	
	showPopup('popup','more');
	
	//初始化隐藏part
	$("#enterprisePart").css('display','none');
	$("#orgPart").css('display','none');
});

function showChange()
{
	$("#showOptionButton").css('display','none');
	$("#hideOptionButton").css('display','');
	$("#hidePart").css('display','');
}

function hideChange()
{
	$("#showOptionButton").css('display','');
	$("#hideOptionButton").css('display','none');
	$("#hidePart").css('display','none');
}


function getCallingTypeB(obj){
	var callingTypeValue = obj.val();	
	ajaxSubmit('','getCallingTypeB','&CALLING_TYPEA='+callingTypeValue,'callingTypeBPart,callingTypeCPart',function(data){
		var callingTypeB = $("#CUSTGROUP_CALLINGTYPE_B").val();
		ajaxSubmit('','getCallingTypeC','&CALLING_TYPEB='+callingTypeB,'callingTypeCPart',function(data){
			if(data.get("hasTypeC") != '1'){
				$("#callTypeC").hide();
				$("#CUSTGROUP_CALLINGTYPE_C").attr('nullable','yes');
			}else{
				$("#callTypeC").show();
				$("#CUSTGROUP_CALLINGTYPE_C").attr('nullable','no');
			}
		},function(error_code,error_info){
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	},function(error_code,error_info){
		MessageBox.error("错误信息!", error_info, function(btn){});
	});
}

function changeRsrvNum3() {
	var rsrv_num3 = $('#RSRV_NUM3').val();
	//每一次点击变更，都将需要重新调整客户经理选择框
	if(rsrv_num3 =='1') {
		$('#custmgr_tt').css('display','');
		$('#custmgr_yd').css('display','none');
	} else {
		$('#custmgr_tt').css('display','none');
		$('#custmgr_yd').css('display','');
	}
	//每一次点击变更，都将需要重新验证集团名称
	group_name_check = false;
	
	//每一次点击变更，都将上级集团数据清除
	$('#SUPER_GROUP_ID').val('');
	$('#SUPER_GROUP_NAME').val('');
 }

function getCallingTypeC(obj){
	var callingTypeValue = obj.val();
	ajaxSubmit('','getCallingTypeC','&CALLING_TYPEB='+callingTypeValue,'callingTypeCPart',function(data){
		if(data.get("hasTypeC") != '1'){
			$("#callTypeC").hide();
			$("#CUSTGROUP_CALLINGTYPE_C").attr('nullable','yes');
		}else{
			$("#callTypeC").show();
			$("#CUSTGROUP_CALLINGTYPE_C").attr('nullable','no');
		}
	},function(error_code,error_info){
		MessageBox.error("错误信息!", error_info, function(btn){});
	});
}

function checlUpload() {
	var channel = $("#ACCEPT_CHANNEL").val();
	if(channel == '7'){
		if($("#GRID_CODE").val() == ''){
			MessageBox.alert('---------请选择网格---------');
		return false;
		}
	}
	return true;
}

function ogrTypeChange(obj,ogrType,part){
	var ogrTypeValue = obj.val();
	if(ogrTypeValue == ''){
		if(ogrType == 'B'){
			$("#ogrTypeCLi").css('display','none');
		}
		return;
	}
	
	if(ogrType == 'A'){
		$("#ogrTypeCLi").css('display','none');
		$("#ogrTypeC").val('');
		if(ogrTypeValue == '1'){
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('E');
		}else if(ogrTypeValue == '3' || ogrTypeValue == '2'){
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('F');
		}else{
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('F');		
		}
		//CLASS_D_TYPE
		if( $("#CLASS_ID").val()=='F' && $("#ORG_TYPE_A_FLAG").val() == '0' ){
			$("#classDType").css('display','');
			$("#CLASS_D_TYPE").attr('nullable','no');
		}else{
			$("#classDType").css('display','none');
			$("#CLASS_D_TYPE").attr('nullable','yes');	
		}
	}
	
	if(ogrType == 'A'){
		ajaxSubmit('','setOgrType','&OGR_TYPE='+ogrType+'&OGR_TYPE_VALUE='+ogrTypeValue,part+',classDTypePart',function(data){
			if(ogrType == 'A' && ogrTypeValue == '1'){
				$("#ogrTypeCLi").css('display','');
			}
		},function(error_code,error_info){
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	}
	if(ogrType == 'B'){ //不刷新classDTypePart	
		var orgTypeA = $("#ORG_TYPE_A").val();	
		ajaxSubmit('','setOgrType','&OGR_TYPE='+ogrType+'&OGR_TYPE_VALUE='+ogrTypeValue,part,function(data){
			if(ogrType == 'B' && orgTypeA == '1'){
				$("#ogrTypeCLi").css('display','');
			}
		},function(error_code,error_info){
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	}
}

//function updatePsptCss(){
//	Wade($("#psptTypeLi")).removeClass('required');
//	Wade($("#psptIdLi")).removeClass('required');
//	Wade($("#psptImageLi")).removeClass('required');
//	$("#GRP_PSPT_TYPE").attr('desc','');
//	$("#GRP_PSPT_ID").attr('desc','');
//	$("#GRP_PSPT_IMAGE").attr('desc','');
//}

function addressChange(obj, type, part) {
	var value = obj.val();
	if (value == '') {
		return;
	}
	ajaxSubmit('', 'refreshAddressSel', '&TYPE=' + type + '&VALUE=' + value,
			part, function(data) {

			}, function(error_code, error_info) {
				MessageBox.error("错误信息!", error_info, function(btn) {
				});
			});
}

//通过企业员工数和集团年收入来计算集团规模
//desc="集团年营业额" name="YEAR_GAIN" 
//	desc="企业员工数" name="EMPLOYEE_NUMS" 
//	企业规模  name="ENTERPRISE_SIZE_CODE" 
function calcGrpScope() {
	/*
	 * a) 员工人数>=1000，或年收入>=30000万元，为特大型； b) 100 <=员工人数<1000 ，或3000万元<=年收入<30000万元，为大型；
	 * c) 50 <=员工人数<100，或1000万元<=年收入<3000万元，为中型； d) 员工人数<50，或年收入<1000万元，为小微型。
	 */
	// var yeargain = document.getElementById('YEAR_GAIN').value;
	var entsizecode = $("#ENTERPRISE_SIZE_CODE").val();

	var yeargain = $("#YEAR_GAIN").val();
	try {
		if (yeargain == null || yeargain == '')
			yeargain = 0;
		yeargain = parseInt(yeargain);
	} catch (e) {
		MessageBox.alert('集团年营业额必须为数字！');
		return;
	}
	// var empnums = document.getElementById('EMPLOYEE_NUMS').value;
	var empnums = $("#EMPLOYEE_NUMS").val();
	try {
		if (empnums == null || empnums == '')
			empnums = 0;
		empnums = parseInt(empnums);
	} catch (e) {
		MessageBox.alert('企业员工数必须为数字！');
		return;
	}
	if (empnums >= 1000 || yeargain >= 30000) {
		// document.getElementById('ENTERPRISE_SIZE_CODE').value = '0';
		if (entsizecode != "0") {
			$("#ENTERPRISE_SIZE_CODE").val('0');
			MessageBox.alert('根据计算规则：员工人数>=1000，或年收入>=30000万元，为特大型。所以调整该集团为特大型集团！');
		}
		return;
	} else if ((empnums >= 100 && empnums < 1000)
			|| (yeargain >= 3000 && yeargain < 30000)) {
		// document.getElementById('ENTERPRISE_SIZE_CODE').value = '1';
		if (entsizecode != "1") {
			$("#ENTERPRISE_SIZE_CODE").val('1');
			MessageBox.alert('根据计算规则：100 <=员工人数<1000 ，或3000万元<=年收入<30000万元，为大型。所以调整该集团为大型集团！');
		}
		return;
	} else if ((empnums >= 50 && empnums < 100)
			|| (yeargain >= 1000 && yeargain < 3000)) {
		// document.getElementById('ENTERPRISE_SIZE_CODE').value = '2';
		if (entsizecode != "2") {
			$("#ENTERPRISE_SIZE_CODE").val('2');
			MessageBox.alert('根据计算规则：50 <=员工人数<100，或1000万元<=年收入<3000万元，为中型。所以调整该集团为中型集团！');
		}

		return;
	} else {
		// document.getElementById('ENTERPRISE_SIZE_CODE').value = '3';
		if (entsizecode != "3") {
			$("#ENTERPRISE_SIZE_CODE").val('3');
			MessageBox.alert('根据计算规则：员工人数<50，或年收入<1000万元，为小微型。所以调整该集团为小微型集团！');
		}
		return;
	}
}

/**
 * 校验证件类型和证件号码
 */
function validPspt()
{
	if($("#BUSI_LICENCE_NO").val()=='')
		return true;
	var busi_licence_type_obj = $("#BUSI_LICENCE_TYPE");
    var lic_type = busi_licence_type_obj.val();
    if(lic_type==null || lic_type=='')
    {
    	MessageBox.alert("证件类型不能为空");
         return false;  
    }
//    else if(lic_type == '0' ||lic_type == '1')
//    {
//         var lic_no = $("#BUSI_LICENCE_NO").val();
//         var lic_no_length = lic_no.length;
//         if(lic_no_length != 18 && lic_no_length != 15)
//         {
//        	 MessageBox.alert("身份证的位数需要满足15位或者18位,您输入的位数是"+lic_no_length);
//            //getElement('BUSI_LICENCE_NO').focus();
//            return false;
//         }
//    }
    else if(lic_type == 'E')
    {    
    	var lic_no =  $("#BUSI_LICENCE_NO").val();
        var lic_no_length = lic_no.replace(/\D/g,'').length;
        if(lic_no_length < 4){
        	MessageBox.alert("营业执照证件号码至少有4位数字,现只包含数字位数为" + lic_no_length);
           return false;
        }
        if(lic_no.length!=15&&lic_no.length!=18){
        	MessageBox.alert("营业执照证件号码长度需满足15或18位！当前："+lic_no.length+"位");
       	 return false;
        }
    }
//    else if(lic_type=='S' || lic_type=='W')
//	{
//		var lic_no = $("#BUSI_LICENCE_NO").val();
//		lic_no = lic_no.toUpperCase();
//		$("#BUSI_LICENCE_NO").val(lic_no);
//		var reg1 =  /^[0-9A-Z]{18}$/;
//		if(!reg1.test(lic_no)) {
//			MessageBox.alert("证件类型为三证合一时，证件号码长度只能是18位（数字和大写英文字母的组合）");
//			$("#BUSI_LICENCE_NO")[0].focus();
//			return false;
//		}
//	}    
    return true;
}

function initArea(eparchyCode){
	var params = "&VALUECODE=CITY_CODE&VALUENAME=CITY_NAME&POP_MODE=S&CHECK_BOX_TYPE=radio&ROOT_REGION="+eparchyCode;
	popupDialog('区县选择','crmbc.popup.sec.SecDistrictSlect','init',params,null,'400px','500px',true)
}

/**
 * 输入参数长度校验
 */
function checkLength(field, length, desc) 
{
	if (field) 
	{
		var value = field.val();
        var curr_length = getLength(value);
        if (curr_length > length && length!=0) 
        {
        	MessageBox.alert("【" + desc + "】" + "最大长度不能超过" + length + "字节，当前输入字符的总字节数为" + curr_length + "字节！\n【提示：汉字为两个字节，字母、数字、标点符号等其它为一个字节】");
            field[0].focus();
            return false;
        }
    }
	return true;
}

/**
 * 监听集团名称输入框文本输入改变事件
 */
 function groupNameChangeCheck(){
	group_name_check = false;
	group_name_is_exist = false;
 }
 /**
  * 
  * @returns {Boolean}
  */
function changeCorD(){
	var class_id = $('#CLASS_ID_CD').val();
	$('#CLASS_ID').val(class_id);
}
function changeClassID(){
	$('#CLASS_ID_CD').attr('nullable','yes');
}
function validGroupInfo_hain()
{
	var group_name_val = $('#CUST_NAME').val();
	//var group_add = $('#ADDR_DETAIL').val();
	var polotical_vill_id= $('#ORG_TYPE_B').val();
	if(group_name_val==null || group_name_val=='')
	{
		MessageBox.alert('请输入集团名称！');
		return false;
	}else
	{
		if(group_name_val.length < 4)
		{
			MessageBox.alert("集团名称输入不少于4个中文汉字或4个字符");
	 		return false;
		}
	}
	
	if(!group_name_check)
	{
		MessageBox.alert('您还没有进行集团名称验证，请先验证！');
		return false;
	}
	if(group_name_is_exist)
	{
		MessageBox.alert('集团名称已经存在，请重新输入！');
		return false;
	}
	
	//集团名称需要做转义处理
	group_name_val = group_name_val.replace("&", "&amp;"); 
	group_name_val = group_name_val.replace("<", "&lt;");
	group_name_val = group_name_val.replace(">", "&gt;"); 
	$('#CUST_NAME').val(group_name_val);
	
/*	if(group_add.length < 6)
	{
		MessageBox.alert("集团客户地址输入不少于6个中文汉字或6个字符");
		$('#ADDR_DETAIL')[0].focus();
	 	return false;
	}*/
	
	var postCode = $('#POST_CODE');
	if(postCode.val().length!=6)
	{
		MessageBox.alert('邮编长度必须是6位');
		postCode[0].focus();
		return false;
	}
	 
	

	//step-2：检测是否选择了父全国一级集团，默认为0
	var pnational_group_id_obj = $('#PNATIONAL_GROUP_ID');
	if(pnational_group_id_obj != null)
	{
		var pnational_group_id_val = pnational_group_id_obj.val();
		if(pnational_group_id_val==null || pnational_group_id_val=='')
		{
			pnational_group_id_obj.val('0');
		}
	}	

	//附属集团单位名称：去除特殊字符（空格、空白字符、<、>、"）
	var sExtRsrvStr40=$('#SUBSIDIARY_GROUP_NAME').val();
	if (sExtRsrvStr40!=null && sExtRsrvStr40!='' && sExtRsrvStr40.length>0)
		$('#SUBSIDIARY_GROUP_NAME').val(sExtRsrvStr40.replace(/[\s*\t\n\r\<\>\u0022]/g,''));

	//2016-11-08 wangym5 客户区域所在经纬度校验
	var iExt37Lng = parseInt($('#LONGITUDE').val());//客户区域所在经度
	var iExt38Lat = parseInt($('#LATITUDE').val());//客户区域所在纬度
	if (iExt37Lng<75 || iExt37Lng>95) {
		MessageBox.alert ("客户区域所在经度范围在75~95度之间,小数点后面保留6位!");
		return false;
	}
	if (iExt38Lat<36 || iExt38Lat>49) {
		MessageBox.alert ("客户区域所在纬度范围在36~49度之间，小数点后面保留6位!");
		return false;
	}

	return true;
}

function clearOutGrpMgr()
{
	$("#OUTSYN_GRP_CUST_NAME").val('');
	$("#cond_OUTSYN_CUST_NAME").val('');
	hidePopup('qryOutSynGrpMgrPopup');
}

function selectOutSynMgrValue(obj)
{
	if(outSynMgrTable.selected == null)
	{
		MessageBox.alert('您未选择数据,请选择行后,再点击"选择"按钮!');
		return false;
	}
	if(outSynMgrTable.selected != null) {
		var rowData = outSynMgrTable.getRowData(outSynMgrTable.selected);
		$("#CUST_NAME").val(rowData.get("OUT_CUST_NAME"));
		
		//使用默认
		//$("#PROVINCE_CODE").val(rowData.get("OUT_PROVINCE_CODE"));
		$("#RSRV_NUM1").val(rowData.get("OUT_COUNTY_CODE"));
		$("#EPARCHY_CODE").val(rowData.get("OUT_EPARCHY_CODE"));
		$("#CITY_CODE").val(rowData.get("OUT_CITY_CODE"));
		//使用默认
		//$("#GROUP_TYPE").val(rowData.get("OUT_GROUP_TYPE"));
		//使用默认
		//$("#GROUP_STATUS").val(rowData.get("OUT_GROUP_STATUS"));
		$("#GROUP_ATTR").val(rowData.get("OUT_GROUP_ATTR"));
		
		//集团机构大类型 集团机构类型中类 集团机构类型小类
		//$("#ORG_TYPE_A").val(rowData.get("OUT_ORG_TYPE_A"));
		var outOrgTypeA = rowData.get("OUT_ORG_TYPE_A");
		var outOrgTypeB = rowData.get("OUT_ORG_TYPE_B");
		if(outOrgTypeA != null && outOrgTypeA != '')
		{
			outOgrTypeChange(outOrgTypeA,'A','ogrTypeB','',outOrgTypeB);
		}
		if(outOrgTypeB != null && outOrgTypeB != '')
		{
			outOgrTypeChange(outOrgTypeB,'B','ogrTypeC',outOrgTypeA,outOrgTypeB);
		}
		//$("#ORG_TYPE_B").val(rowData.get("OUT_ORG_TYPE_B"));
		
		$("#RSRV_STR6").val(rowData.get("OUT_PRO_MANAGER"));
		$("#ENTERPRISE_SCOPE").val(rowData.get("OUT_ENTERPRISE_SCOPE"));
		
		//所属行业类别 所属子行业类别
		$("#CALLING_TYPE_CODE").val(rowData.get("OUT_CALLING_TYPE_CODE"));
		var callingTypeCode = rowData.get("OUT_CALLING_TYPE_CODE");
		var subCallingTypeCode = rowData.get("OUT_SUB_CALLING_TYPE_CODE");
		if(callingTypeCode != null && callingTypeCode != '')
		{
			callingTypeCodeChange(callingTypeCode,subCallingTypeCode);
		}
		//$("#SUB_CALLING_TYPE_CODE").val(rowData.get("OUT_SUB_CALLING_TYPE_CODE"));
		
		$("#RSRV_STR1").val(rowData.get("OUT_INDUSTRY_ATTR"));
		$("#ENTERPRISE_TYPE_CODE").val(rowData.get("OUT_ENTERPRISE_TYPE_CODE"));
		$("#BUSI_LICENCE_TYPE").val(rowData.get("OUT_BUSI_LICENCE_TYPE"));
		$("#BUSI_LICENCE_NO").val(rowData.get("OUT_BUSI_LICENCE_NO"));
		$("#LICENCE_INFO").val(rowData.get("OUT_LICENCE_INFO"));
		$("#GROUP_MGR_CUST_NAME").val(rowData.get("OUT_GROUP_MGR_CUST_NAME"));
		
		$("#GROUP_MGR_SN").val(rowData.get("OUT_GROUP_MGR_SN"));
		
		$("#CLASS_ID").val(rowData.get("OUT_CLASS_ID"));
		var outClassId = rowData.get("OUT_CLASS_ID");
		if(outClassId != null && outClassId != '')
		{
			$('#CLASS_ID_CD').attr('nullable','yes');
		}
		
		$("#GROUP_ADDR").val(rowData.get("OUT_GROUP_ADDR"));
		$("#POST_CODE").val(rowData.get("OUT_POST_CODE"));
		
		$("#GROUP_CONTACT_PHONE").val(rowData.get("OUT_GROUP_CONTACT_PHONE"));
		$("#RSRV_NUM3").val(rowData.get("OUT_MOBILE_GRP_TAG"));
		$("#RSRV_TAG4").val(rowData.get("OUT_TOP_GRP_TAG"));
		$("#CLASS_ID_NEW").val(rowData.get("OUT_CLASS_ID_NEW"));
		$("#EMPLOYEE_NUMS").val(rowData.get("OUT_EMPLOYEE_NUMS"));
		$("#YEAR_GAIN").val(rowData.get("OUT_YEAR_GAIN"));
		$("#ENTERPRISE_SIZE_CODE").val(rowData.get("OUT_ENTERPRISE_SIZE_CODE"));
		
		//是否直管客户
		$("#EXT_ISSC").val(rowData.get("OUT_EXT_ISSC"));
		var outExtIssc = rowData.get("OUT_EXT_ISSC");
		extIsscChange(outExtIssc);
		
		$("#SECRET_GRP").val(rowData.get("OUT_SECRET_GRP"));
		
		var outGrpSynTagObj = $("#OUT_GRPSYN_TAG");
		if(outGrpSynTagObj)
		{
			outGrpSynTagObj.val("TRUE");
		}

	}
	hidePopup('qryOutSynGrpMgrPopup');
}


function outOgrTypeChange(obj,ogrType,part,orgTypeA,orgTypeB){
	debugger;
	var ogrTypeValue = obj;
	if(ogrTypeValue == ''){
		if(ogrType == 'B'){
			$("#ogrTypeCLi").css('display','none');
		}
		return;
	}
	
	if(ogrType == 'A'){
		$("#ogrTypeCLi").css('display','none');
		$("#ogrTypeC").val('');
		if(ogrTypeValue == '1'){
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('E');
		}else if(ogrTypeValue == '3' || ogrTypeValue == '2'){
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('F');
		}else{
			$("#ogrTypeCLi").css('display','none');
			$("#CLASS_ID").val('F');		
		}
		//CLASS_D_TYPE
		if( $("#CLASS_ID").val()=='F' && $("#ORG_TYPE_A_FLAG").val() == '0' ){
			$("#classDType").css('display','');
			$("#CLASS_D_TYPE").attr('nullable','no');
		}else{
			$("#classDType").css('display','none');
			$("#CLASS_D_TYPE").attr('nullable','yes');	
		}
	}
	
	if(ogrType == 'A'){
		ajaxSubmit('','setOgrType','&OGR_TYPE='+ogrType+'&OGR_TYPE_VALUE='+ogrTypeValue,part+',classDTypePart',function(data){
			debugger;
			if(ogrType == 'A' && ogrTypeValue == '1'){
				$("#ogrTypeCLi").css('display','');
			}
			else 
			{
				$("#ogrTypeCLi").css('display','none');
			}
			$("#ORG_TYPE_A").val(ogrTypeValue);
			$("#ORG_TYPE_B").val(orgTypeB);
		},function(error_code,error_info){
			var outGrpSynTagObj = $("#OUT_GRPSYN_TAG");
			if(outGrpSynTagObj)
			{
				outGrpSynTagObj.val("");
			}
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	}
	if(ogrType == 'B'){ //不刷新classDTypePart	
		//var orgTypeA = $("#ORG_TYPE_A").val();	
		ajaxSubmit('','setOgrType','&OGR_TYPE='+ogrType+'&OGR_TYPE_VALUE='+ogrTypeValue,part,function(data){
			debugger;
			if(ogrType == 'B' && orgTypeA == '1'){
				$("#ogrTypeCLi").css('display','');
			}
			else 
			{
				$("#ogrTypeCLi").css('display','none');
			}
			$("#ORG_TYPE_C").val(ogrTypeValue);
		},function(error_code,error_info){
			var outGrpSynTagObj = $("#OUT_GRPSYN_TAG");
			if(outGrpSynTagObj)
			{
				outGrpSynTagObj.val("");
			}
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	}
}

function callingTypeCodeChange(objValue,subObjValue)
{
	if(objValue == '')
	{
		return;
	}
	if(objValue !=null && objValue != '')
	{
		ajaxSubmit('','getIndustryType','&CALLING_TYPE_CODE='+objValue,"subIndustryPart",function(data){
			$("#SUB_CALLING_TYPE_CODE").val(subObjValue);
		},function(error_code,error_info){
			var outGrpSynTagObj = $("#OUT_GRPSYN_TAG");
			if(outGrpSynTagObj)
			{
				outGrpSynTagObj.val("");
			}
			MessageBox.error("错误信息!", error_info, function(btn){});
		});
	}	
}

function extIsscChange(outExtIssc)
{
	if(outExtIssc != '' && outExtIssc == '1')
	{
		Wade($("#scCodeLi")).addClass('required');
		Wade($("#orgCodeLi")).addClass('required');
		$("#SC_CODE").attr('desc','直管客户编码');
		$("#ORG_CODE").attr('desc','组织机构代码');
		$("#SC_CODE").attr('disabled','');
		$("#ORG_CODE").attr('disabled','');
		$("#sc").attr('ontap',"showPopup('qrySocPopup', 'querySocPopup')");
	}
	else 
	{
		Wade($("#scCodeLi")).removeClass('required');
		Wade($("#orgCodeLi")).removeClass('required');
		$("#SC_CODE").attr('desc','');
		$("#ORG_CODE").attr('desc','');
		$("#SC_CODE").val('');
		$("#ORG_CODE").val('');
		$("#SC_CODE").attr('disabled','disabled');
		$("#ORG_CODE").attr('disabled','disabled');
		$("#sc").attr('ontap','');
	}
}

function checkPsptTypeCode(){
	var psptTypeCode = $('#BUSI_LICENCE_TYPE').val();
	setEnterpriseInfo(psptTypeCode);
	setOrgInfo(psptTypeCode);
}

function setEnterpriseInfo(psptTypeCode){	
	if(psptTypeCode=="E"){//法人、成立时间、营业开始时间、营业结束时间都要填		 
		$("#custInfo_legalperson").attr("nullable","no");
		$("#custInfo_startdate").attr("nullable","no");
		$("#custInfo_termstartdate").attr("nullable","no");
		$("#custInfo_termenddate").attr("nullable","no");
		$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").addClass("required");		
		$("#enterprisePart").css("display","");		
	}else{
		$("#custInfo_legalperson").attr("nullable","yes");
		$("#custInfo_startdate").attr("nullable","yes");
		$("#custInfo_termstartdate").attr("nullable","yes");
		$("#custInfo_termenddate").attr("nullable","yes");
		$("#span_legalperson,#span_startdate,#span_termstartdate,#span_termenddate").removeClass("required");			
		$("#enterprisePart").css("display","none");	
		$("#custInfo_legalperson").val('');
		$("#custInfo_termstartdate").val('');
		$("#custInfo_termenddate").val('');
		$("#custInfo_startdate").val('');
	}
}

function setOrgInfo(psptTypeCode){
	if(psptTypeCode=="3"){//机构类型、有效日期、失效日期
		$("#custInfo_orgtype").attr("nullable","no");
		$("#custInfo_effectiveDate").attr("nullable","no");
		$("#custInfo_expirationDate").attr("nullable","no");
		$("#span_orgtype,#span_effectiveDate,#span_expirationDate").addClass("required");		
		$("#orgPart").css("display","");
	}else{ 
		$("#custInfo_orgtype").attr("nullable","yes");
		$("#custInfo_effectiveDate").attr("nullable","yes");
		$("#custInfo_expirationDate").attr("nullable","yes");
		$("#span_orgtype,#span_effectiveDate,#span_expirationDate").removeClass("required");				
		$("#orgPart").css("display","none");	
		$("#custInfo_orgtype").val('');
		$("#custInfo_effectiveDate").val('');
		$("#custInfo_expirationDate").val('');
	} 
}