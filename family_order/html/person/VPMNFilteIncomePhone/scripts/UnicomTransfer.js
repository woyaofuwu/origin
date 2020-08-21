/*
 * author dengshu
 */
//页面初始状态
var modif_tag_init = '0';
//页面新增
var modif_tag_add = '1';
//页面修改
var modif_tag_update = '2';
//页面删除
var modif_tag_delete = '3';
/** 
 * 获取3户信息以后的操作
 */
function refreshPartAtferAuth(data){
	var serialNumber = data.get('USER_INFO').get('SERIAL_NUMBER');
	$.ajax.submit('', 'loadChildInfo', "&SERIAL_NUMBER="+serialNumber, 'Phone_Table,Phone_List_Part', function(){
		var buttons = $(":button[disabled=true]");
		//将前台按钮设置为可用
		buttons.attr("disabled", false);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}

/** 
 * 点击新增按钮时新增行数据，对异网号码等进行校验
 */
function createDept(obj) {
	//校验输入
	if(!verifyInputInfo()){
		return false;
	}
	if($('tbody tr::first-child').length>0){
		 alert("该用户已有一个异网转接号码，不能再继续转接！");
	     return false;
	}
	
	var serialNumberB = $('#PHONE_CODE_B').val();
	var serialNumberA =$('#PHONE_CODE_A_BAK').val();
    var start_date=$('#START_DATE').val();
    var end_date=$('#END_DATE').val();
    
	var tbody ='<tr class="on"><td class="e_center">'+serialNumberA+'</td>'+
	'<td class="e_center" id="td_serialNumberB">'+serialNumberB+'</td>'+
	'<td class="e_center" id="td_start_date">'+start_date+'</td>'+
	'<td class="e_center" id="td_end_date">'+end_date+'</td></tr>'
	$('tbody').append(tbody);
	
	if(checkUserHaveData()){
		$('#modify_tag').val(modif_tag_update);
	}else{
		$('#modify_tag').val(modif_tag_add);
	}
	
}


/** 修改一条记录，对应表格修改按钮 */
function updateDept(obj) {
	//校验输入
	if(!verifyInputInfo()){
		return false;
	}
	if($('tbody tr::first-child').length<1){
		 alert("请先新增异网服务号");
	     return false;
	}
	var serialNumberB = $('#PHONE_CODE_B').val();
    var start_date=$('#START_DATE').val();
    var end_date=$('#END_DATE').val();
    if($('#td_serialNumberB').text()==serialNumberB &&
    		$('#td_start_date').text()==start_date &&
    		$('#td_end_date').text()==end_date) {
    	return false;
    }else{
    	//修改表格数据
    	if(checkUserHaveData()){
    		$('#modify_tag').val(modif_tag_update);
    	}else{
    		$('#modify_tag').val(modif_tag_add);
    	}
    	$('#td_serialNumberB').text(serialNumberB);
    	$('#td_start_date').text(start_date);
    	$('#td_end_date').text(end_date)
    }
}


/** 
 * 删除表格的行数据
 */
	function deleteDept(obj) {
		if($('tbody tr::first-child').length<1){
		     return false;
		}
		$('tbody tr::first-child').remove();
		if(checkUserHaveData()){
			$('#modify_tag').val(modif_tag_delete);
		}else{
			$('#modify_tag').val(modif_tag_init);
		}
		
	}
	
/*
 * 检查输入手机号与开始结束日期
 */
function verifyInputInfo(){
	
	var phoneNumB = $('#PHONE_CODE_B').val();
	
	   if(phoneNumB == ''){
	    	alert("异网转接号不能为空!");
	    	return false;
	    }
	   
		if(!checkMphone(phoneNumB)){
			 alert('请输入正确的异网手机号码!');
			return false;
		}
		if($('#START_DATE').val()==""){
			 alert('请输入开始日期!');
			return false;
		}
		if($('#END_DATE').val()==""){
			 alert('请输入结束日期!');
			return false;
		}
		if(daysBetween($('#START_DATE').val(),$('#END_DATE').val())>=0){
			alert('开始日期不能大于等于结束日期');
			return false;
		}
	return true;
}
 
/**
*校验他网手机号码
*/
function checkMphone(mobileNum){

	var flag = false;
	if (mobileNum == ""||mobileNum.length!=11)
		return flag;
	//获取异网手机号码段
	var phoneBeginStr = $("#phoneBeginList").val();
	
	if(phoneBeginStr!=null&&phoneBeginStr.length>0){
	   //异网手机号码段 用,分割
	  var phoneBeginArray = phoneBeginStr.split(",");
	  
	  for(var i=0;i<phoneBeginArray.length;i++){
		  var legalPrefix = phoneBeginArray[i]; //参数配置的异网转接号段
		  var inputPrefix = mobileNum.substr(0,3);
		  
		  if(inputPrefix==legalPrefix){
			  flag = true;
			  break;
		  }
	  }
	  
	}
	return flag;
}


/*
 * 求两个时间的天数差 日期格式为 YYYY-MM-dd   
 */
function daysBetween(DateOne,DateTwo)  
{   
	    var OneMonth = DateOne.substring(5,DateOne.lastIndexOf ('-'));  
	    var OneDay = DateOne.substring(DateOne.length,DateOne.lastIndexOf ('-')+1);  
	    var OneYear = DateOne.substring(0,DateOne.indexOf ('-'));  
	  
	    var TwoMonth = DateTwo.substring(5,DateTwo.lastIndexOf ('-'));  
	    var TwoDay = DateTwo.substring(DateTwo.length,DateTwo.lastIndexOf ('-')+1);  
	    var TwoYear = DateTwo.substring(0,DateTwo.indexOf ('-'));  
	  
	    var cha=((Date.parse(OneMonth+'/'+OneDay+'/'+OneYear)- Date.parse(TwoMonth+'/'+TwoDay+'/'+TwoYear))/86400000);   
	    return cha;  
}  
	


/** 
 * 业务提交前触发的方法
 */
function submitCheck() {
	if ($('#modify_tag').val()==0){
		alert("没有需提交的信息！");
		return false;
	}else if($('#modify_tag').val()!=modif_tag_delete){
		//判断信息是否被修改
		if($('#PHONE_CODE_B_BAK').val()==$('#td_serialNumberB').text()&&
			$('#START_DATE_BAK').val()==$('#td_start_date').text()&&
			$('#END_DATE_BAK').val()==$('#td_end_date').text() ){
			alert("没有修改任何信息！");
			return false;
		}
	}
	
	//删除
	if($('#modify_tag').val()==modif_tag_delete){
		$('#sumbmit_str').val("{'PHONE_CODE_A':'"+$('#PHONE_CODE_A_BAK').val()+"','PHONE_CODE_B': '"+$('#PHONE_CODE_B_BAK').val()+"','START_DATE': '"+$('#START_DATE_BAK').val()+"'}");
	}else{
		//新增或修改
		param =  "{";
		param +="'PHONE_CODE_A': '"+$('#PHONE_CODE_A_BAK').val()+"',";
		param +="'PHONE_CODE_B': '"+$('#td_serialNumberB').text()+"',";
		param +="'START_DATE': '"+$('#td_start_date').text()+"',";
		param +="'END_DATE': '"+$('#td_end_date').text()+"' ";
		param +="}";
		$('#sumbmit_str').val(param);
	}
	return true;
}

function checkUserHaveData(){
	if($('#PHONE_CODE_B_BAK').val()==''&&
			$('#START_DATE_BAK').val()==''&&
			$('#END_DATE_BAK').val()==''){
		return false ;
	}
	return true;
}

