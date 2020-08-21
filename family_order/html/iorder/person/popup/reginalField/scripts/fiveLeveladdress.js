function setAddr1Name(obj){
	
	var text = obj.selectedText;
	$("#cond_gis").val(text);
	$("#cond_addr_id1").val(obj.value);
	$("#cond_gis1").val('');
	$("#cond_gis2").val('');
	$("#cond_gis3").val('');
	$("#cond_gis4").val('');
	$("#cond_gis5").val('');
} 

function setAddr2Name(obj){
	var addr_id = obj.value;
	var text = obj.selectedText;
	$("#cond_gis1").val(text);
	$("#cond_gis2").val('');
	$("#cond_gis3").val('');
	$("#cond_gis4").val('');
	$("#cond_gis5").val('');
	$("#cond_addr_id2").val(addr_id);
	
}


function confirmGisDate(){
	//取五级地址及房号信息返回给CRM
	//提交时判断，是否左边的值等于右边的值，若相等取ID，不相等或者左边为空，不取ID。只取右边数据
	//1返回CRM对应参数
//	var gis1 = $("#ADDR_ID1").find("option:selected").text();
//	var gis2 = $("#ADDR_ID2").find("option:selected").text();
	var gis1 = ADDR_ID1.selectedText;
	var gis2 = ADDR_ID2.selectedText;

	var addr_name1 = $("#cond_gis").val();
	var addr_name2 = $("#cond_gis1").val();
	var addr_name6 = $("#cond_gis5").val();
	
	var addr_id1 = $("#cond_addr_id1").val();
	var addr_id2 = $("#cond_addr_id2").val();
	var addr_id3 = $("#cond_addr_id3").val();
	var addr_id4 = $("#cond_addr_id4").val();
	var addr_id5 = $("#cond_addr_id5").val();
	
	if(gis1!=addr_name1){
		addr_id1="";
	}
	if(gis2!=addr_name2){
		addr_id2="";
	}
/*	if(gis3!=addr_name3){
		addr_id3="";
	}
	if(gis4!=addr_name4){
		addr_id4="";
	}
	if(gis5!=addr_name5){
		addr_id5="";
	} */
	var addr_name;
	
	if(addr_name1=="HKS"){
		addr_name1 = "海口市";
	}
	
	if(addr_name1=="SYS"){
		addr_name1="三亚市";
	}
	
	//网维一级地址数据
	
	if(addr_name1==addr_name2){
		addr_name = addr_name2 + addr_id3 + addr_id4 + addr_id5 + addr_name6;
	}else{
		addr_name = addr_name1 + addr_name2 + addr_id3 + addr_id4 + addr_id5 + addr_name6;
	}
	
	
	//判断五级地址不能为空
	if(addr_id1 == ""){
		$.TipBox.show(document.getElementById('cond_addr_id1'), "请确认右侧的[地市]输入框不能为空,且必须等于左侧的[地市]输入框!", "red");
//		alert("请确认右侧的[地市]输入框不能为空,且必须等于左侧的[地市]输入框!");	
		return false;	
	}
	
	if(addr_id2 == ""){
		$.TipBox.show(document.getElementById('cond_addr_id2'), "请确认右侧的[区县]输入框不能为空,且必须等于左侧的[区县]输入框!", "red");
//		alert("请确认右侧的[区县]输入框不能为空,且必须等于左侧的[区县]输入框!");	
		return false;	
	}
	
	if(addr_id3 == ""){
		$.TipBox.show(document.getElementById('cond_addr_id3'), "请确认右侧的[街道]输入框不能为空!", "red");
//		alert("请确认右侧的[街道]输入框不能为空!");	
		return false;	
	}
	
	if(addr_id4 == ""){
		$.TipBox.show(document.getElementById('cond_addr_id4'), "请确认右侧的[小区]输入框不能为空!", "red");
//		alert("请确认右侧的[小区]输入框不能为空!");	
		return false;	
	}
	
	if(addr_id5 == ""){
		$.TipBox.show(document.getElementById('cond_addr_id5'), "请确认右侧的[楼栋]输入框不能为空!", "red");
//		alert("请确认右侧的[楼栋]输入框不能为空!");	
		return false;	
	}
	
	
	//返回CRM需要的参数，五级地址名称，五级地址ID，房号
	setPopupReturnValue(this, {'ADDR_NAME':addr_name,'ADDR_NAME1':addr_name1,'ADDR_ID1':addr_id1,'ADDR_NAME2':addr_name2,'ADDR_ID2':addr_id2,
		'ADDR_NAME3':addr_id3,'ADDR_ID3':'','ADDR_NAME4':addr_id4,'ADDR_ID4':'',
		'ADDR_NAME5':addr_id5,'ADDR_ID5':'','ADDR_NAME6':addr_name6});
}


function clearData(){
	$("#cond_gis").val('');
	$("#cond_gis1").val('');
	$("#cond_addr_id3").val('');
	$("#cond_addr_id4").val('');
	$("#cond_addr_id5").val('');
	$("#cond_gis5").val('');
}