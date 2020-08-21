/**
 * 作用：用来初始化页面的显示
 */

function initParamInfo()
{
   var pam_STU_PARAM_LISTi = $("#stuParamkey").val()//父页面保存页面参数的键pam_STU_PARAM_LIST
   var  stuParamStr = $.getSrcWindow().$("#"+pam_STU_PARAM_LISTi).val();//从父页面获取pam_STUDENT_PARAM;

   var molistdataset = $.DatasetList(stuParamStr);

	  if(molistdataset)
	  {
	  	var molistcount=molistdataset.getCount();
	  	for(j=0;j<molistcount;j++)
	 	 {
	  		 var modata=molistdataset.get(j);
	   		 modata.eachKey(
	 			 function(mokey)
	 	 		 {
	     			try
	     			{
	     				$("#"+mokey).val(modata.get(mokey,""));
	     			}
	     			catch(ee)
	     			{
	     			}
	     		}
	    	);

	   	    if(modata.get("tag","")=="" )//直接从数据库查得的记录
	   	 	{
	  			createMoinfo();
	  			replaceTablecol(modata.get("STUD_KEY"),modata.get("ELEMENT_ID"),'',j);
	  	 	}
	  	 	else if(modata.get("tag","")=="2")//修改的记录
	  	 	{
	  	 		createMoinfo();
	  			replaceTablecol(modata.get("STUD_KEY"),modata.get("ELEMENT_ID"),'2',j);
	  	 	}
	  	 	else if( modata.get("tag","")=="0")//新增的记录
	  	 	{
	  	 		createMoinfo();//往表新增这条记录
	  	 		replaceTablecol(modata.get("STUD_KEY"),modata.get("ELEMENT_ID"),'0',j)
	  	 	}
	  	 	else if( modata.get("tag","")=="1")//删除的记录
	  		{
	  	 		createMoinfo();//往表新增这条记录
	  			replaceTablecol(modata.get("STUD_KEY"),modata.get("ELEMENT_ID"),'1',j); //修改这条记录的操作标识为删除,同时隐藏这条记录
	  	 	}
	  	}
	  }

};

//点确定按钮返回数据到父页面隐藏字段
function setData()
{
	if (!checkMaxstudent()) {
		return false;
	}

	var pam_STU_PARAM_LISTi = $("#stuParamkey").val()//父页面保存页面参数的键pam_STU_PARAM_LIST
	var stulist="";
	try
	{
		stulist = $.table.get("MoListTable").getTableData("STUD_KEY,STUD_NAME,ELEMENT_ID",true);
	}
	catch(e)
	{
	  stulist="";
	}

//	alert("setData=确定=="+stulist);
	$.setReturnValue([pam_STU_PARAM_LISTi,stulist],true)
}
//点取消按钮返回原值数据到父页面隐藏字段
function setCancleData()
{
	$.setReturnValue();
}

// 区分出学生一、学生二、学生三分别能订购的优惠。
function setStuDiscnt()
{
	var stuKey = $("#STUD_KEY").val();
	var tempElemeStr = $("#stu_DiscntAll").val();

	var  tempElements = $.DatasetList(tempElemeStr);

	var  paramset = $.DatasetList();

	for (var i = 0; i < tempElements.length; i++)
	{
	    var	disdata = $.DataMap();
	    if(tempElements.get(i, "ELEMENT_TYPE_CODE")=="D")
	    {
	    	disdata.put("ELEMENT_ID",tempElements.get(i, "ELEMENT_ID"));
	    	disdata.put("MODIFY_TAG",tempElements.get(i, "MODIFY_TAG"));
	    	paramset.add(disdata);

	    }

	}

	if ("[]"==paramset||null==paramset||""==paramset)
	{
	    alert("请先选择和校园学生优惠套餐！");
		return false;
	}
	var param = '&DISCNTSET='+paramset+'&STUD_KEY='+stuKey;
	$.beginPageLoading();
	$.ajax.submit(this, 'getStuDiscntByajax', param, null,
	   	function(data){
	   		if(data != null)
	   		{
				insertDiscntList(data);
			}
			$.endPageLoading();

	    },
	    function(error_code,error_info,derror){
	   		showDetailErrorInfo(error_code,error_info,derror);
			$.endPageLoading();

	    });

}

function insertDiscntList(data)
{
	var roleObj = $('#ELEMENT_ID');
	roleObj.html('');
	roleObj.attr('disabled',false)
	var roleSelInnerHTML = [];
	roleSelInnerHTML.push('<option value="" title="--请选择--">--请选择--</option>');
	for(var i =0;i<data.length;i++){
		var info = data.get(i);
		var elementId = info.get("ELEMENT_ID");
		var elementName = info.get("ELEMENT_NAME");
		roleSelInnerHTML.push( '<option value="'+elementId+'">'+elementId+'|'+elementName+'</option>');
	}
	$.insertHtml('afterbegin',roleObj,roleSelInnerHTML.join(""));
}


/**
*table动态表
*/

function tableRowClick() {
	var rowData = $.table.get("MoListTable").getRowData();
	$("#STUD_KEY").val(rowData.get("STUD_KEY"));//学生参数
	$("#STUD_NAME").val(rowData.get("STUD_NAME"));//学生姓名
	$("#ELEMENT_ID").val(rowData.get("ELEMENT_ID"));
	setStuDiscnt();
}

function tableRowDBClick() {
   alert(savaStuTableData());
	var rowData = $.table.get("MoListTable").getRowData();
	$("#STUD_KEY").val(rowData.get("STUD_KEY"));//学生参数
	$("#STUD_NAME").val(rowData.get("STUD_NAME"));//学生姓名
	$("#ELEMENT_ID").val(rowData.get("ELEMENT_ID"));
}

function tableColumnClick() {alert(3);}
function tableAddRow(e) {$.table.get("MoListTable").addRow(e);};
function tableDeleteRow(e) {$.table.get("MoListTable").deleteRow();};
function tableCleanRow(e) {$.table.get("MoListTable").cleanRow(e);};
/**
*动态表格新增一条记录
*/
function createMoinfo()
{
	var editDate = $.ajax.buildJsonData("MoListDetail");

  	if(!$.validate.verifyAll('MoListDetail'))return false;

  	var studKey = $('#STUD_KEY').val();
  	var stuName = $('#STUD_NAME').val();
  	var elementId = $("#ELEMENT_ID").val();

  	//校验同一学生姓名要相同
  	var falg = checkTablekeyElement(studKey+elementId,stuName);
	if("true"==falg)
	{
		MessageBox.alert("","添加失败！同一学生只能加一个资费！");
		return false;
	}
	if(elementId==""||elementId==null)
	{
		MessageBox.alert("","优惠不能空！");
		return false;
	}

  	if (!$.table.get("MoListTable").isPrimary('STUD_KEY,ELEMENT_ID', editDate))
  	{
		editDate["STUD_KEY_NAME"]=$("#STUD_KEY").find("option:selected").text();
		editDate["ELEMENT_NAME"]=$("#ELEMENT_ID").find("option:selected").text();
		//往表格里添加一行并将编辑区数据绑定上
		$.table.get("MoListTable").addRow(editDate,null,null,true);
		//往表格里添加一行数据后清空编辑框
		resetArea('MoListDetail',true);
	} else {
		MessageBox.alert("","添加失败！同一学生不允许重复添加相同优惠！");
		resetArea('MoListDetail',true);
	}
	//savaStuTableData();
}

/**
*新增记录,并校验学生信息
*/
function addMoinfo()
{
	var opertype = "01"; //新增
 	var stuName = $('#STUD_NAME').val();

  	if(checkXXTPlatExists(opertype,stuName))
  	{
  		createMoinfo();
  	}

}


/**
*动态表格更新一条记录
*/
function updateMoinfo()
{
	var editDate = $.ajax.buildJsonData("MoListDetail");
	if(!$.validate.verifyAll('MoListDetail')) return false;

	var studKey = $('#STUD_KEY').val();
  	var stuName = $('#STUD_NAME').val();
  	var elementId = $("#ELEMENT_ID").val();
  	//校验同一学生姓名要相同
	var falg = checkTablekeyElement(studKey+elementId,stuName);
	if("true"==falg)
	{
		MessageBox.alert("","添加失败！同一学生不允许重复添加相同优惠！");
		return false;
	}
	if (!$.table.get("MoListTable").isPrimary('STUD_KEY,STUD_NAME,ELEMENT_ID', editDate)){
	editDate["STUD_KEY_NAME"]=$("#STUD_KEY").find("option:selected").text();
	editDate["ELEMENT_NAME"]=$("#ELEMENT_ID").find("option:selected").text();

	$.table.get("MoListTable").updateRow(editDate);
	//往表格里添加一行数据后清空编辑框
	resetArea('MoListDetail',true);
	//savaStuTableData();
	}else{
		MessageBox.alert("","修改失败！该学生参数没有修改！");
		resetArea('MoListDetail',true);
	}
}

/**
*动态表格删除一条记录
*/
function deleteMoinfo()
{
	var stuName = $('#STUD_NAME').val();
	var opertype = "02"; //退订
  	//if(checkXXTPlatExists(opertype,stuName))
  	{
  		//var tab = $.table.get("MoListTable");
  		//tab.deleteRow(tab.getTable().attr("selected"));
  		$.table.get("MoListTable").deleteRow();
  		//往表格里添加一行数据后清空编辑框
  		resetArea('MoListDetail',true);
  		//savaStuTableData();


  	}
}


//替换动态表格操作标识字段 并且设置其显示属性
function replaceTablecol(inKey,inId,xtag,j)
{
	var table=$.table.get("MoListTable").getTableData();
	 for (var i=0;i<table.length;i++)
     {
        var item = table.get(i);
		var key = item.get("STUD_KEY");
		var elementId = item.get("ELEMENT_ID");
		if(inKey==key && inId==elementId)
		{
			$("#MoListTable tbody tr")[j].setAttribute("status", xtag);
		   if(xtag=='1')//代表删除 那么将要删除的这行隐藏掉
		   {
		       //class = e_del
		   		$("#MoListTable tbody tr")[j].style.display='none';
		   }
		}

	}

}
/**********************************动态表格结束***********************************************/

//校验同一学生姓名要相同
function checkTablekeyName(inKey,inName)
{
	var table=$.table.get("MoListTable").getTableData();
	var flag ='false';
     for (var j=0;j<table.length;j++)
     {
        var item = table.get(j);
		var key = item.get("STUD_KEY");
		//var name = item.get("STUD_NAME");
		if(inKey==key)
		{
			flag='true';
			break;

		}

	}
	return flag;
}

//校验同一学生姓名要相同
function checkTablekeyElement(inKey,inName)
{
	var table=$.table.get("MoListTable").getTableData();
	var flag ='false';
     for (var j=0;j<table.length;j++)
     {
        var item = table.get(j);
		var key = item.get("STUD_KEY") + item.get("ELEMENT_ID");

		//var name = item.get("STUD_NAME");
		if(inKey==key)
		{
			flag='true';
			break;

		}

	}
	return flag;
}


/**************************************************动态表格结束*********************************************************/

//保存添加到表格的 学生参数信息
function savaStuTableData()
{
	var paramset = $.DatasetList();
	var studentParam = $.DataMap();

	var stulist="";
	try
	{
		stulist = $.table.get("MoListTable").getTableData("STUD_KEY,STUD_NAME,ELEMENT_ID",true);
	}
	catch(e)
	{
	  stulist="";
	}


}

function checkXXTPlatExists(operType,stuName){
	var flag = true;
	var notin_serial_number = $('#NOTIN_SERIAL_NUMBER').val();
	var notin_out_sn = $('#NOTIN_OUT_SN').val();
	var grp_user_id = $('#GRP_USER_ID').val();
	var group_id = $('#GROUP_ID').val();

	var param = '&NOTIN_SERIAL_NUMBER='+notin_serial_number+'&NOTIN_OUT_SN='+notin_out_sn+'&STU_NAME='+stuName+'&GRP_USER_ID='+grp_user_id+'&OPER_TYPE='+operType+'&GROUP_ID='+group_id;

	$.ajax.submit(this, "checkXXTPlatExists", param, null,
		   	function(data){
		   		if(data != null)
		   		{
		   			flag = afterCheckXXTPlatExists(data);
				}
		    },
		    function(error_code,error_info,derror){
		    	$.showErrorMessage("和校园平台校验失败:"+error_info);
		    	flag = false
		    }, {
				async : false
			});
		return flag;
}

function afterCheckXXTPlatExists(data){

	if("false"==data.get("FLAG")){
		var errinfo=data.get("X_RESULTINFO");
		alert("和校园平台校验失败:"+errinfo);
		return false;
	}
	return true;
}

function checkMaxstudent() {
	var flag = true;
	var notin_serial_number = $('#NOTIN_SERIAL_NUMBER').val();
	var notin_out_sn = $('#NOTIN_OUT_SN').val();
	var grp_user_id = $('#GRP_USER_ID').val();
//	alert($('#EXISTSTUDENT').val());
//	var data = new Wade.DatasetList($('#EXISTSTUDENT').val());
//	flag = aftercheckMaxstudent(data);

	var param = '&NOTIN_SERIAL_NUMBER='+notin_serial_number+'&NOTIN_OUT_SN='+notin_out_sn+'&GRP_USER_ID='+grp_user_id;

	$.ajax.submit(this, "checkMaxstudent", param, null,
	   	function(data){
	   		if(data != null)
	   		{
	   			flag = aftercheckMaxstudent(data);
			}
	    },
	    function(error_code,error_info,derror){
	    	$.showErrorMessage("操作失败:"+error_info);
	    	flag = false
	    }, {
			async : false
		});
	return flag;
}

function aftercheckMaxstudent(data)
{
	for(var i =0;i<data.length;i++){
		var info = data.get(i);
		var inKey = info.get("RSRV_STR1");
		var result = checkTablekeyName(inKey, "");
		if ("true" == result) {
			if ("stu_name1" == inKey) {
				alert("计费号码+家长服务号码的学生一已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name2" == inKey) {
				alert("计费号码+家长服务号码的学生二已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name3" == inKey) {
				alert("计费号码+家长服务号码的学生三已经在其它学校订购，不能重复订购！");
			}
			return false;
		}
	}

	return true;
}
