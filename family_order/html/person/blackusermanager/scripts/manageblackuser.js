function refreshPartAtferAuth(data) {
	$.beginPageLoading("正在查询数据...");
	$.ajax.submit('AuthPart', 'loadChildInfo', '' ,
			'listTablePart', function() {
				$("#bcreate").attr("disabled",false);
				$("#bdelete").attr("disabled",false);
				$.endPageLoading();
			}, function(error_code, error_info,detail) {
				$.endPageLoading();
				MessageBox.error("错误提示", error_info, $.auth.reflushPage, null, null, detail);
			});
}
function tableRowClick(){
	//获取选择行的数据
	var rowData = $.table.get("ListTable").getRowData();
	$("#BLACK_SERIAL_NUMBER").val(rowData.get("BLACK_SERIAL_NUMBER"));
	$("#BLACK_TYPE").val(rowData.get("BLACK_TYPE"));
	$("#REMARK").val(rowData.get("REMARK"));
}
// 删除
function delItem()
{	
    var rowData = $.table.get("ListTable").getRowData();
	if(rowData.length == 0)
	{
		alert("请您选择记录后再进行删除操作！");
		return;
	}
	$.table.get("ListTable").deleteRow();
	setValues();
	return;
}
// 添加
function addItem()
{	
	var black_serial_number = $("#BLACK_SERIAL_NUMBER").val();
	var black_type =  $("#BLACK_TYPE").val();
	var remark =  $("#REMARK").val(); 
	//验证输入
	if(black_type=='0'){
		if(!checkPhone(black_serial_number)){
			return false;
		}
	}
	if(remark != "" )
	{
		if(remark.strlen()>30){
			alert("备注字符长度不能超过30!");
			$("#REMARK").focus();
			return false;
		}
		if(remark.indexOf(',')>=0){
			alert('输入的[备注]中请不要带有[ , ]符!');
			$("#REMARK").focus();
			return false;
		}
	}
	//验证可否添加
	var listTable=$.table.get("ListTable").getTableData(null,true);
	for(var i=0;i<listTable.length;i++){
		var _black_serial_number = listTable.get([i],"BLACK_SERIAL_NUMBER");
		var _black_type =listTable.get([i],"BLACK_TYPE");
		var _tag =listTable.get([i],"tag");
		//全部设置，如果下面有单个的，提示先删除
		if(black_type==6){
			if(_tag=="0"||_tag==undefined){
				alert("全部黑名单已经存在，或者已经存在单个黑名单，不能添加全部黑名单！");
				$("#BLACK_SERIAL_NUMBER").focus();
				return false;
			}
		}
		//单个设置
		else{
			if(black_serial_number==$("#AUTH_SERIAL_NUMBER").val()){
				alert("不能添加自己为黑名单！");
				$("#BLACK_SERIAL_NUMBER").focus();
				return false;
			}
			if((_tag=="0"||_tag==undefined)&&(_black_serial_number=="-1"||_black_serial_number==black_serial_number)){
				alert("已经存在全部黑名单，或者本号码已经添加，不能添加此号码!！");
				$("#BLACK_SERIAL_NUMBER").focus();
				return false;
			}
		}
	}
	
	var custEdit = $.ajax.buildJsonData("EditPart");
    custEdit["BLACK_SERIAL_NUMBER"] = black_serial_number;//
    custEdit["BLACK_TYPE_NAME"] = $("#BLACK_TYPE")[0].options($("#BLACK_TYPE")[0].selectedIndex).text;//获取方式
	custEdit["BLACK_TYPE"] = $("#BLACK_TYPE")[0].options($("#BLACK_TYPE")[0].selectedIndex).value;//获取方式
	custEdit["REMARK"] = remark;//备注
	$.table.get("ListTable").addRow(custEdit);
	setValues();
}

function setValues(){
	var ListTable=$.table.get("ListTable").getTableData(null,true);
	$("#X_CODING_STR").val(ListTable);
	$("#BLACK_TYPE").val("0");
	$("#BLACK_SERIAL_NUMBER").val("");
	$("#BLACK_SERIAL_NUMBER").attr("disabled",false);
}
//黑名单设置选择动作
function black_Type_Change()
{
	var black_type =  $("#BLACK_TYPE").val();
	if(black_type=="0"){
		$("#BLACK_SERIAL_NUMBER").val("");
		$("#BLACK_SERIAL_NUMBER").attr("disabled",false);
	}else if(black_type=="6") {
		$("#BLACK_SERIAL_NUMBER").val("-1");
		$("#BLACK_SERIAL_NUMBER").attr("disabled",true);
	}
}
/** 
 * 提交保存
 */
function submitTrade(){
	var tableDatas = $.table.get("ListTable").getTableData(null, true);
	
	if(tableDatas.length < 1){
		alert("没有可以提交的数据");
		return false;
	}
	var param = "&BLACK_USER_DATAS="+tableDatas;
	$.cssubmit.addParam(param);
	
	return true;
}
function checkPhone(black_serial_number){
		if(isNull(black_serial_number))
		{	
			alert("号码不能为空!");
			$("#BLACK_SERIAL_NUMBER").focus();
			return false;
		}
		if(!checkMphone(black_serial_number))
		{
			$("#BLACK_SERIAL_NUMBER").focus();
			return false;
		}
		return true;	
	}
function checkMphone(obj){
	if (obj != ""){
	   var reg0 = /^13\d{9,9}$/;;
	   var reg1 = /^15\d{9,9}$/;
	   var reg2 = /^18\d{9,9}$/;

	   var my = false;
	   if (reg0.test(obj))my=true;
	   if (reg1.test(obj))my=true;
	   if (reg2.test(obj))my=true;
	   if (!my){
         alert('非法号码，请重新输入合法号码！');
	       return false;
	   }
	}
   return true;
}
function isNull(str){
	var flag=false;
	if(str==undefined||str==null||str==""){
		flag=true;
	}
	return flag;
}
String.prototype.strlen = function()
{ 
 var cArr = this.match(/[^x00-xff]/ig); 
 return this.length + (cArr == null ? 0 : cArr.length);
}