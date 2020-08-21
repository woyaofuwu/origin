/**
 * 作用：用来初始化页面的显示
 */
function init()
{   
	debugger;
	var currentGroup = $("#dynamicOfferParam").closest(".c_popupGroup");
	var currentLevel = currentGroup.attr("level");
	var item = $("#directlinkItem");
	if(item){
		var popupGroup = item.closest(".c_popupGroup");
		var popLevel = popupGroup.attr("level");
		if(popLevel>currentLevel){
			$("#directlinkItem_old").remove();
			return;
		}
	}
	item = $("#directlinkItem_old");
	var popupGroup = item.closest(".c_popupGroup");
	var nextGroup = currentGroup.next();
	if(typeof(nextGroup[0]) != "undefined" && nextGroup){
		nextGroup.append(item.outerHtml());
		item.remove();
		$("#directlinkItem_old").attr("id","directlinkItem");
	}else{
		newLevel = parseInt(currentLevel)+1;
		popupGroup.attr("level",newLevel);
		currentGroup.after(popupGroup.outerHtml());
		popupGroup.remove();
		$("#directlinkItem_old").attr("id","directlinkItem");
	}
	
	window["MoListTable"] = new Wade.Table("MoListTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#MoListTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(MoListTable.getSelectedRowData());
	});
	
	
	window["powerDivTable"] = new Wade.Table("powerDivTable", {
		fixedMode:true,
		editMode:true,
	});
	$("#powerDivTable").tap(function(e){
		if(e.target && $.nodeName(e.target, "input") && "checkbox" == $.attr(e.target, "type"))
			return;
		tableRowClick(powerDivTable.getSelectedRowData());
	});
	
}
/**
*Molist table动态表：选中后将数据放入下方的输入框中
*/
function tableRowClick(data) {
//	var rowData = $.table.get("MoListTable").getRowData();
	
	
}
function setMemParamInfo(e)
{
	var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
	var offers = offerData.get("SUBOFFERS");
	if(typeof(offers) == "undefined" || typeof(offers) != "object" || offers.length == 0){
		MessageBox.alert("请先选择子商品优惠！");
		return;
	}

//	pam_NOTIN_STU_PARAM_LIST+i 把页面上的KEY 传给子页面用，子页面通过$.getSrcWindow().$("#"+pam_NOTIN_STU_PARAM_LISTi).val()取父页面的值
	var stuParamkey = $(e).attr("paramlistname");

	var stuParamStr = $('#'+stuParamkey).val();
	var stuParamList = $.DatasetList(stuParamStr);
    var  rowindex = $(e).attr("rowindex");
	var mainSn = $("#pam_NOTIN_SERIAL_NUMBER").val();
	var outsn = $('#'+'pam_NOTIN_OUT_SN'+rowindex).val();
	var operType = $('#pam_NOTIN_OPER_TYPE'+rowindex).val();
	var groupId= $("#pam_GROUP_ID").val();
	var ecUserId=$("#pam_GRP_USER_ID").val();

	// 校验家长号码
	var isMod = g_IsMobileNumber(outsn);
	if (!isMod) {
		MessageBox.alert(outsn+"格式不正确，请输入正确的手机号码");
		return ;
	}

	var  paramset = $.DatasetList();
	
	for (var i = 0; i < offers.length; i++)
	{
		var disdata = $.DataMap();

	    if(offers.get(i, "OFFER_TYPE")=="D")
	    {
	    	disdata.put("OFFER_ID",offers.get(i, "OFFER_ID"));
	    	disdata.put("OFFER_CODE",offers.get(i, "OFFER_CODE"));
	    	disdata.put("OPER_CODE",offers.get(i, "OPER_CODE"));
	    	disdata.put("OFFER_NAME",offers.get(i, "OFFER_NAME"));
	    	disdata.put("OFFER_TYPE","D");

	    	paramset.add(disdata);
	    }

	}

	if (paramset.length==0)
	{
		MessageBox.alert("请先选择和校园学生优惠套餐！");

		return ;
	}
	var param = '&DISCNTSET='+paramset+'&stuParamkey='+stuParamkey+'&NOTIN_SERIAL_NUMBER='+mainSn+'&NOTIN_OUT_SN='+outsn+'&GRP_USER_ID='+ecUserId+'&GROUP_ID='+groupId;
	
	ajaxPost("igroup.xxt.CreateMemParamInfo", "initParamInfo", param, "MoListDetail,HiddenPart", 
		   	function(data){
				if("0"!=operType){
					initMolistTable(e);
				}
				forwardPopup(e,'directlinkItem');
		    },function(code, info) {
				MessageBox.error(code, info);
			},
		    {async:false});
	
}


//控制Checkbo选中时 字段不能为空
function validateCheckbox(e)
{
	if($(e).attr('checked'))
	{
		$('#'+'pam_NOTIN_OUT_SN'+$(e).attr('indexvalue')).attr('nullable','no');
		$('#'+'pam_NOTIN_STU_PARAM_LIST'+$(e).attr('indexvalue')).attr('nullable','no');

	}
	else
	{
		$('#'+'pam_NOTIN_OUT_SN'+$(e).attr('indexvalue')).attr('nullable','yes');
		$('#'+'pam_NOTIN_STU_PARAM_LIST'+$(e).attr('indexvalue')).attr('nullable','yes');
	}
}

/*
 * 校验是否为手机号码
 */
function g_IsMobileNumber(s) {
	if (s == null || s == '') {
		return false;
	}
	if (s.length != 11 || !g_IsDigit(s)) {
		return false;
	}

	if (!checkMobileNumber(s)) {
		return false;
	}

	return true;
}

function checkMobileNumber(data) {
	var flag = false;
	ajaxPost("igroup.xxt.CreateMemParamInfo", "checkMobileNumber", '&SERIAL_NUMBER='+data, "", 
   	function(data){
   		if(data != null){
   			var rspflag = data.get("IS_FLAG");
   			if (rspflag == "true") {
   				flag = true;
   			} else {
   				flag = false;
   			}

		}
    },function(code, info) {
		MessageBox.error(code, info);
	},
    {async:false});

    return flag;
}

/*
 * 校验是否为数字类型
 */
function g_IsDigit(s) {
	if (s == null) {
		return false;
	}
	if (s == '') {
		return true;
	}
	s = '' + s;
	if (s.substring(0, 1) == '-' && s.length > 1) {
		s = s.substring(1, s.length);
	}
	var patrn = /^[0-9]*$/;
	if (!patrn.exec(s)) {
		return false;
	}
	return true
}

//区分出学生一、学生二、学生三分别能订购的优惠。
function setStuDiscnt()
{
	var stuKey = $("#STUD_KEY").val();
	var tempElemeStr = $("#stu_DiscntAll").val();

	var  tempElements = $.DatasetList(tempElemeStr);

	var  paramset = $.DatasetList();

	for (var i = 0; i < tempElements.length; i++)
	{
	    var	disdata = $.DataMap();
	    if(tempElements.get(i, "OFFER_TYPE")=="D")
	    {
	    	disdata.put("OFFER_CODE",tempElements.get(i, "OFFER_CODE"));
	    	disdata.put("OFFER_ID",tempElements.get(i, "OFFER_ID"));
	    	disdata.put("OPER_CODE",tempElements.get(i, "OPER_CODE"));
	    	paramset.add(disdata);

	    }

	}

	if (paramset.length==0)
	{
		MessageBox.alert("请先选择和校园学生优惠套餐！");
		return false;
	}
	var param ='&DISCNTSET='+paramset+'&STUD_KEY='+stuKey;
	$.beginPageLoading();
	ajaxPost("igroup.xxt.CreateMemParamInfo", 'getStuDiscntByajax', param, null,
	   	function(data){
	   		if(data != null)
	   		{
				insertDiscntList(data);
			}
			$.endPageLoading();

	    },
	    function(error_code,error_info){
	   		
			$.endPageLoading();
			MessageBox.error(code, info);

	    });

}
function insertDiscntList(data)
{
	ELEMENT_ID.empty();
	for(var i =0;i<data.length;i++){
		var info = data.get(i);
		var elementId = info.get("OFFER_CODE");
		var elementName = info.get("OFFER_NAME");
		ELEMENT_ID.append(elementName,elementId,elementName);
	}
	
}

/**
*新增记录,并校验学生信息
*@chenygai
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

function checkXXTPlatExists(operType,stuName){
	var flag = true;
	var notin_serial_number = $('#pam_NOTIN_SERIAL_NUMBER').val();
	var notin_out_sn = $('#pam_NOTIN_OUT_SN').val();
	var grp_user_id = $('#pam_GRP_USER_ID').val();
	var group_id = $('#pam_GROUP_ID').val();

	var param = '&NOTIN_SERIAL_NUMBER='+notin_serial_number+'&NOTIN_OUT_SN='+notin_out_sn+'&STU_NAME='+stuName+'&GRP_USER_ID='+grp_user_id+'&OPER_TYPE='+operType+'&GROUP_ID='+group_id;

	ajaxPost("igroup.xxt.CreateMemParamInfo", "checkXXTPlatExists", param, null,
		   	function(data){
		   		if(data != null)
		   		{
		   			flag = afterCheckXXTPlatExists(data);
				}
		    },
		    function(error_code,error_info){
		    	MessageBox.error(error_code, "和校园平台校验失败:"+error_info);
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
  	var isSameDis=false;//同一个学生只能加一次资费

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
  	
  	//
  	var list =  MoListTable.getData(true,'STUD_KEY,ELEMENT_ID');
  	list.each(function(item,index,totalcount){
		if(studKey==list.get(index,'STUD_KEY')||elementId==list.get(index,'ELEMENT_ID')){
			isSameDis = true;
		}
	});
	if(!isSameDis){
		var data = $.DataMap(editDate);
		data.put("STUD_KEY_NAME",$("#STUD_KEY").text());
		data.put("ELEMENT_NAME",$("#ELEMENT_ID").text());
		//MoListTable.addRow(editDate,null,null,true);
		MoListTable.addRow($.parseJSON(data.toString()));
	}else{
		MessageBox.alert("","添加失败！同一学生不允许重复添加相同优惠！");
	}
}
/**
 * 添加到表格之后，删除输入框中的值
 */
function delAll(data){
	var idata = $.DataMap(data);
	idata.eachKey(function(item,index,totalcount){
		$("#"+item).val('');
	});
}	

/**
*动态表格删除一条记录
*/
function deleteMoinfo()
{
	var j=MoListTable.selected;
	var  delData=MoListTable.getRowData(j);
	
	//完全新增数据 直接删除，初始化的数据则隐藏 不删除
	var tag=delData.get("tag");
	if(tag=="0"){
		MoListTable.deleteRow(j);
	}else{
		$("#MoListTable tbody tr")[j].setAttribute("status","1");
		$("#MoListTable tbody tr")[j].style.display='none';
	}
	var editDate = $.ajax.buildJsonData("MoListDetail");
	delAll(editDate);
}

//校验同一学生姓名要相同
function checkTablekeyElement(inKey,inName)
{
	var table=MoListTable.getData();
	var flag ='false';
     for (var j=0;j<table.length;j++)
     {
        var item = table.get(j);
		var key = item.get("STUD_KEY") + item.get("ELEMENT_ID");
		if(inKey==key)
		{
			flag='true';
			break;

		}

	}
	return flag;
}


//点确定按钮返回数据到父页面隐藏字段
function setADCData(e)
{
	if (!checkMaxstudent()) {
		return false;
	}

	var pam_STU_PARAM_LISTi = $("#stuParamkey").val();//父页面保存页面参数的键pam_STU_PARAM_LIST
	var stulist = MoListTable.getData(true,'STUD_KEY,STUD_NAME,ELEMENT_ID');
	
	//$("#pam_NOTIN_STU_PARAM_LIST"+rowindex).val(stulist);
	$("#"+pam_STU_PARAM_LISTi).val(stulist);
	backPopup(e);
}
//点取消按钮返回原值数据到父页面隐藏字段
function setCancleData(e)
{
	var pam_STU_PARAM_LISTi = $("#stuParamkey").val();
	$("#"+pam_STU_PARAM_LISTi).val("");
	$("#stuParamkey").val("");
	backPopup(e);
}

function checkMaxstudent() {
	var flag = true;
	var notin_serial_number = $('#NOTIN_SERIAL_NUMBER').val();
	var notin_out_sn = $('#NOTIN_OUT_SN').val();
	var grp_user_id = $('#GRP_USER_ID').val();
	var param = '&NOTIN_SERIAL_NUMBER='+notin_serial_number+'&NOTIN_OUT_SN='+notin_out_sn+'&GRP_USER_ID='+grp_user_id;

	ajaxPost("igroup.xxt.CreateMemParamInfo", "checkMaxstudent", param, null,
	   	function(data){
	   		if(data != null)
	   		{
	   			flag = aftercheckMaxstudent(data);
			}
	    },
	    function(error_code,error_info){
	    	MessageBox.error(error_code, "操作失败:"+error_info);
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
				MessageBox.alert("计费号码的学生一已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name2" == inKey) {
				MessageBox.alert("计费号码的学生二已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name3" == inKey) {
				MessageBox.alert("计费号码的学生三已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name4" == inKey) {
				MessageBox.alert("计费号码的学生四已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name5" == inKey) {
				MessageBox.alert("计费号码的学生五已经在其它学校订购，不能重复订购！");
			}
			if ("stu_name6" == inKey) {
				MessageBox.alert("计费号码的学生六已经在其它学校订购，不能重复订购！");
			}
			return false;
		}
	}

	return true;
}

////校验同一学生姓名要相同
function checkTablekeyName(inKey,inName)
{
	var table= MoListTable.getData();
	var flag ='false';
     for (var j=0;j<table.length;j++)
     {
        var item = table.get(j);
		var key = item.get("STUD_KEY");
		if(inKey==key)
		{
			flag='true';
			break;

		}

	}
	return flag;
}


/**
 * 校验异网号码的操作
 */
function validateNetPage()
{
//	//校验异网号码的操作
	var mainSn = $("#pam_NOTIN_SERIAL_NUMBER").val();
	var checkBoxList =$("#stuParamlistPart input:checked");
	var count = 0 ;
	checkBoxList.each(function()
	{
		var checkflag = $(this).val();

		if("on"==checkflag)
		{
			var stuDisParam = $('#'+'pam_NOTIN_STU_PARAM_LIST'+$(this).attr('indexvalue')).val();
			if(stuDisParam==""||stuDisParam==null||stuDisParam=='[]'||stuDisParam=={})
			{
				MessageBox.alert("至少添加一个学生参数信息！");;
				return false;
			}

			// 校验家长号码
			var isMod = g_IsMobileNumber($('#'+'pam_NOTIN_OUT_SN'+$(this).attr('indexvalue')).val());
			if (isMod == false) {
				MessageBox.alert($('#'+'pam_NOTIN_OUT_SN'+$(this).attr('indexvalue')).attr('desc') + "格式不正确，请输入正确的手机号码");
				return false;
			}
		  count++;
		}

	});
	if(count>1)
	{
		MessageBox.alert("一次只能选择一个异网号操作！");
		return false;
	}
	if(count<1)
	{
		MessageBox.alert("请选择一条异网号操作！");
		return false;
	}
	return true;
}
/**
 * 提交前数据校验
 * @param obj
 * @returns {Boolean}
 */
function validateParamPage(obj) {
	debugger;
	if(!$.validate.verifyAll("dynamicOfferParam")){
		return false;
	}
	//选择异网号码
	if(! validateNetPage())
	{
		
		return false;
	}
	if(submitDynamicOfferChaSpec($(obj).attr('OFFER_ID'))){
		backPopup(obj);
		return true;
	}else{
		return false;
	}
	return true;
}
//提交产品特征规格
function submitDynamicOfferChaSpec(offerId)
{
	var offerId = $("#prodDiv_OFFER_ID").val();
	
	var brand= PageData.getData($(".e_SelectOfferPart")).get("BRAND");
	
	var offerChaSpecDataset = new Wade.DatasetList(); //产品规格特征
	
	var chaSpecObjs = $("#dynamicOfferParam input");
	
	

	var chaSpecId = 0;
	for(var i = 0, size = chaSpecObjs.length; i < size; i++)
	{
		var chaSpecObjId = chaSpecObjs[i].id;
		var chaSpecObjName = chaSpecObjs[i].name;
		var chaValue = "";
		if(chaSpecObjName.indexOf("pam_") == -1)
		{
			continue;
		}
		if("BOSG"==brand&&isBbossNull(chaSpecObjId)){
			if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		else if (isNull(chaSpecObjId)) {
			if(chaSpecObjs[i].type != "checkbox" &&  chaSpecObjs[i].type != "radio") // 复选框 只有name没有id
			{
				continue;
			}
		}
		if(chaSpecObjs[i].type == "checkbox" ||  chaSpecObjs[i].type == "radio")
		{
			chaValue = chaSpecObjs[i].checked ? "on" : "";
		}
		else
		{
			chaValue = $("#"+chaSpecObjId).val();
		}
		if(chaValue == null || "" == chaValue){
			continue;
		}
		var offerChaSpecData = new Wade.DataMap();
		var objSpecId = chaSpecObjs[i].specId;
		if(objSpecId != null && "" != objSpecId){
			offerChaSpecData.put("CHA_SPEC_ID", objSpecId);
		}else{
			offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
			chaSpecId = chaSpecId + 1;
		}
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		
		if(chaSpecObjName.indexOf("pam_") == 0)
		{
			var prodChaValCode = chaSpecObjName.substring(4);
			offerChaSpecData.put("ATTR_CODE", prodChaValCode);

		}
		if(chaSpecObjs[i].type == "radio" || chaSpecObjs[i].type == "checkbox"){//针对些单选框的name没有pam的情况
			if(chaSpecObjName.indexOf("pam_") != 0){
				offerChaSpecData.put("ATTR_CODE", chaSpecObjName);
			}			
		}
		
		offerChaSpecDataset.add(offerChaSpecData);
	}

	var chaSpecObjs2 = $("#dynamicOfferParam textarea");
	for(var i = 0, size = chaSpecObjs2.length; i < size; i++)
	{
		var chaSpecObjId = chaSpecObjs2[i].id;
		var chaSpecObjName = chaSpecObjs2[i].name;
		var chaValue = $("#"+chaSpecObjId).val();
		var offerChaSpecData = new Wade.DataMap();
		offerChaSpecData.put("ATTR_VALUE", chaValue);
		if(chaSpecObjName.indexOf("pam_") == 0)
		{
			var prodChaValCode = chaSpecObjName.substring(4);
			offerChaSpecData.put("ATTR_CODE", prodChaValCode);
		}
		offerChaSpecData.put("CHA_SPEC_ID", chaSpecId);
		
		offerChaSpecData.put("ATTR_NAME", chaSpecObjs[i].getAttribute("desc"));
		chaSpecId = chaSpecId + 1;
		offerChaSpecDataset.add(offerChaSpecData);
	}
	
	var grpItemEleObjs =$("#dynamicOfferParam textarea[isgrpitem='true']");
	if (grpItemEleObjs && grpItemEleObjs.length>0){
		for (var i = 0;i<grpItemEleObjs.length;i++){
			var grpMapObj = new Wade.DataMap($(grpItemEleObjs[i]).text());
			var paramData = grpMapObj.get("ELEMENT_DATAS");
			for (var j =0;j<paramData.items.length;j++){
				var offerChaSpecData = new Wade.DataMap();
				var grpItem = $(grpItemEleObjs[i]).attr("id").substring(8);
				offerChaSpecData.put("CHA_SPEC_ID", paramData.keys[j]);
				offerChaSpecData.put("CHA_VALUE", paramData.items[j]);
				offerChaSpecData.put("GROUP_ATTR", grpItem);
				offerChaSpecDataset.add(offerChaSpecData);
			}
		}
	}
	
	$("#cha_"+offerId).val(offerChaSpecDataset);
	
	//将产品特征规格放到数据对象中 
	if(offerChaSpecDataset.length != 0)
	{
		var offerData = PageData.getData($("#prodDiv_OFFER_DATA"));
		offerData.put("OFFER_CHA_SPECS", offerChaSpecDataset);
		
		var esopOfferCha = offerData.get("ESOP_OFFER_CHA");
		if(!esopOfferCha)
		{
			offerData.removeKey("ESOP_OFFER_CHA");
		}
		//保存数据
		PageData.setData($("#prodDiv_OFFER_DATA"), offerData);
	}
	
	$("ul[id=prodSpecUL] li:nth-child(1)").children().eq("1").css("display", "none");
	
	return true;
}

/**
 * 作用：用来初始化页面的显示
 */

function initMolistTable(e)
{
   var stuParamkey = $(e).attr("paramlistname");
   var stuParamStr = $('#'+stuParamkey).val();
   var molistdataset = $.DatasetList(stuParamStr);
   //清空上次留下的数据
   clearOldTablecol();

   
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
	   		 
	   		if("20190812" == modata.get("ELEMENT_ID") || "20190813" == modata.get("ELEMENT_ID") ||"20190814" == modata.get("ELEMENT_ID")){
	   			//捆绑套餐不进行遍历 
	   			continue;
	   		}
	   		 
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

}

//替换动态表格操作标识字段 并且设置其显示属性
function replaceTablecol(inKey,inId,xtag,j)
{
	var table=MoListTable.getData();
	 for (var i=0;i<table.length;i++)
     {
        var item = table.get(i);
		var key = item.get("STUD_KEY");
		var elementId = item.get("ELEMENT_ID");
		if(inKey==key && inId==elementId)
		{
			$("#MoListTable tbody tr")[j].setAttribute("status",xtag);
		   if(xtag=='1')//代表删除 那么将要删除的这行隐藏掉
		   {
		       //class = e_del
		   		$("#MoListTable tbody tr")[j].style.display='none';
		   }
		}

	}

}

//替换动态表格操作标识字段 并且设置其显示属性
function clearOldTablecol()
{
	 var table=MoListTable.getData();
	 
	 for (var i=table.length-1;i>=0;i--)
     {  
		 MoListTable.deleteRow(i);
     }
}


