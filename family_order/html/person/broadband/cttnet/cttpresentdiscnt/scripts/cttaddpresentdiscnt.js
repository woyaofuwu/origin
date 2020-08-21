

/**
 * 验证查询
 */
function validateQuery()
{
	var serialNumber = $("#SERIAL_NUMBER").val();
	if(trim(serialNumber)=="")
	{
		alert("请输入需要查询的服务号码!");
		return false;
	}
	return true;
}

/**
 * 查询用户返回优惠
 */
function querySendBackDiscnt()
{
	if(!validateQuery()) return;
	
	$.ajax.submit('QueryPart', 'querySendBackDiscnt', null, 'ResultDataPart', function(data){
		var tabChecks = $("input[name=tabCheck]");
		if(tabChecks.length>0)
		{
			$("#presentDiv").css("display",""); 
			$("#operDiv").css("display",""); 
			$("#btnAdd").css("display",""); 
			$("#btnUpdate").css("display",""); 
			
			//默认选择第一项
			for(var i =0; i<tabChecks.length;i++)
			{
				if(tabChecks[i].checked)
				{
					checkDiscntRadio(tabChecks[i]); 
					break;
				}
			}
		}else{
			
			$("#presentDiv").css('display','none'); 
			$("#operDiv").css('display','none'); 
			$("#btnAdd").css("display","none"); 
			$("#btnUpdate").css("display","none"); 
			$("#btnSubmit").css("display","none"); 
			
			$("#presentTable").css("display","none");
			alert("没有查询到用户需要赠送的优惠!");
		}
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
}





/**
 * 查找父亲节点TR对象
 * @param {} target
 * @return {}
 */
function findParentTR(target)
{
	
	while(target.nodeName!="TR")
	{
		target = target.parentNode;
	}
	return target;
}


/**
 * 选择返回优惠事件
 * @param {} target
 */
function checkDiscntRadio(target)
{
	var trObj = findParentTR(target);
	var condSerialNumber = $("#SERIAL_NUMBER").val();
	$("#save_ACCT_ID").val(condSerialNumber); //宽带用户账号
	$("#save_USER_ID").val(trObj.cells[1].innerText);	//宽带用户号码
	$("#save_DISCNT_INST_ID").val(trObj.cells[2].innerText);	//返还优惠实例ID
	$("#save_DISCNT_CODE").val(trObj.cells[3].innerText);	//返还优惠编码
	$("#M_MONEY").val(trObj.cells[5].innerText);	//可赠送移动号码金额
	$("#M_COUNT").val(trObj.cells[6].innerText) ;		//可赠送移动号码数量
	$("#T_MONEY").val(trObj.cells[7].innerText) ;		//可赠送固话号码金额
	$("#T_COUNT").val(trObj.cells[8].innerText);		//可赠送固话号码数量
	
	//查询已存在的登记记录
	$.ajax.submit('', 'queryUserPresentDiscnts', '&USER_ID='+$("#save_USER_ID").val()+'&DISCNT_CODE='+$("#save_DISCNT_CODE").val()+'&DISCNT_INST_ID='+
			$("#save_DISCNT_INST_ID").val(),'PresentPart', function(data){
		$("#PHONE_TYPE").val('');
		$("#PRESENT_SERIAL_NUMBER").val('');
		$("#PRESENT_MONEY").val('');
		$("#REMARK").val('');
		$("#btnSubmit").css("display","none");
	},function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


/**
 * 验证
 * @param {} obj
 * @return {Boolean}
 */
function validateAll(obj)
{
	//判断是否选择了返还优惠
	var presentUserId = $("#save_PRESENT_USER_ID").val();
	var discntCode = $("#save_DISCNT_CODE").val();
	
	if(presentUserId=="" || discntCode=="")
	{
		alert("请选择需要赠送费用的返还优惠!");
		return false;
	}
	
	//公共验证方法
	if(!$.validate.verifyAll(obj))
	{
		return false;
	}
	return true;
}


/**
 * 保存赠送优惠记录
 * @param {} obj
 * @return {Boolean}
 */
function savePresentDiscnt(obj)
{
	var userId = $("#save_USER_ID").val();
	var acctId = $("#save_ACCT_ID").val();
	var discntInstId = $("#save_DISCNT_INST_ID").val();
	var discntCode = $("#save_DISCNT_CODE").val();
	var presentTable = getAllTableData("zsTable");
	var presentDataset=new Wade.DatasetList();
	for(var i=0;i<presentTable.length;i++){
		var data = new Wade.DataMap();
		if(presentTable.get(i).get("X_TAG") != ""){
			data.put('X_TAG', presentTable.get(i).get("X_TAG"));
			data.put('PHONE_TYPE', presentTable.get(i).get("PHONE_TYPE"));
			data.put('PRESENT_SERIAL_NUMBER', presentTable.get(i).get("PRESENT_SERIAL_NUMBER"));
			data.put('PRESENT_MONEY', presentTable.get(i).get("PRESENT_MONEY"));
			data.put('INST_ID', presentTable.get(i).get("INST_ID"));
			data.put('REMARK', presentTable.get(i).get("REMARK"));
			data.put('USER_ID', userId);
			data.put('ACCT_ID',acctId);
			data.put('DISCNT_INST_ID', discntInstId);
			data.put('DISCNT_CODE', discntCode);
			presentDataset.add(data);
		}
	}
	if(presentDataset.length == 0){
		alert("没有进行新增或修改操作，不能提交！");
		return false;
	}
	$.ajax.submit('', 'savePresentDiscnt', '&presentInfos='+presentDataset, '', function(data){
		alert("提交成功！");
		$.table.get("queryTable").cleanRows();
		$("#SERIAL_NUMBER").val('');
		$("#presentDiv").css('display','none'); 
		$("#operDiv").css('display','none'); 
		$("#btnAdd").css("display","none"); 
		$("#btnUpdate").css("display","none"); 
		$("#btnSubmit").css("display","none"); 
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	
	return true;
}

/**
 * 操作PresernTable验证
 * @param {} operType
 * @return {Boolean}
 */
function validatePresentOper(operType)
{
	var presentTable =getAllTableData("zsTable");	
	var mCount = 0; 
	var tCount = 0;
	var mMoney = 0;
	var tMoney = 0;
	
	for(var i=1;i<presentTable.length;i++)
	{
		var data = new Wade.DataMap();
		var tds = presentTable.get(i);
		tds.get("PHONE_TYPE");
		if(tds.get("PHONE_TYPE")=="固话号码")
		{	
			tCount++;
			
			if(operType=="update")  continue;	//如果是修改数据行,选择的数据行金额不参与计算
				
			tMoney += parseFloat(tds.get("PRESENT_MONEY"));
		}
		else if(tds.get("PHONE_TYPE")=="移动号码")
		{
			mCount++;
			
			if(operType=="update")  continue;	//如果是修改数据行,选择的数据行金额不参与计算
			
			mMoney += parseFloat(tds.get("PRESENT_MONEY"));
		}
	}
	
	var phoneType = $("#PHONE_TYPE").val();
	
	//判断赠送移动数据
	var mTotalCount = $("#M_COUNT").val(); 	//可赠送的移动号码数量
	var tTotalCount = $("#T_COUNT").val();	//可赠送的固话号码数量
	
	if(operType == "add") //如果是新增数据行，对统计的赠送表格数据记录数增加1
	{
		if(phoneType == "移动号码")
		{
			mCount ++;
		}else if(phoneType == "固话号码")
		{
			tCount ++;
		}
	}
	if(mCount > mTotalCount) //判断是否超过了可赠送的移动号码数量
	{
		alert("可赠送的移动号码数量已达到最大数!");
		return false;
	}
	if(tCount > parseInt(tTotalCount)) //判断是否超过了可赠送的固话号码数量
	{
		alert("可赠送的固话号码数量已达到最大数!");
		return false;
	}

	//判断赠送金额
	
	if(phoneType == "移动号码")
	{
		var mTotalMoney = $("#M_MONEY").val();
		mMoney += parseFloat($("#PRESENT_MONEY").val());
		
		if(mMoney > parseFloat(mTotalMoney))
		{
			alert("可赠送的移动号码金额已超过最大数!");
			return false;
		}
	}
	else if(phoneType == "固话号码")
	{
		var tTotalMoney = $("#T_MONEY").val();
		tMoney += parseFloat($("#PRESENT_MONEY").val());
		
		if(tMoney > parseFloat(tTotalMoney))
		{
			alert("可赠送的固话号码金额已超过最大数!");
			return false;
		}
	}
	
	return true;
}

/**
 * 增加presentTable行
 * @return {Boolean}
 */
function addPresentRow(obj)
{
	if(!validatePresentOper("add")) return false;
	if(!verifyAll(obj)) return false;
	
	var table = $.table.get("zsTable");
    var custEdit = new Array();
	custEdit["PHONE_TYPE"] = $("#PHONE_TYPE").val();
	custEdit["PRESENT_SERIAL_NUMBER"] = $("#PRESENT_SERIAL_NUMBER").val();
	custEdit["PRESENT_MONEY"] = $("#PRESENT_MONEY").val();
	custEdit["REMARK"] = $("#REMARK").val();
	custEdit["X_TAG"] = "0";
	
	table.addRow(custEdit);
	$("#btnSubmit").css("display",""); 
}


function editPresentRow(){
	var table = $.table.get("zsTable");

	var json = table.getRowData();
	var _PHONE_TYPE = json.get("PHONE_TYPE");
	var _PRESENT_SERIAL_NUMBER = json.get("PRESENT_SERIAL_NUMBER");
	var _PRESENT_MONEY = json.get("PRESENT_MONEY");
	var _REMARK = json.get("REMARK");
	
	$("#PHONE_TYPE").val(_PHONE_TYPE);
	$("#PRESENT_SERIAL_NUMBER").val(_PRESENT_SERIAL_NUMBER);
	$("#PRESENT_MONEY").val(_PRESENT_MONEY);
	$("#REMARK").val(_REMARK);
	
}

/**
 * 修改presentTable行
 * @param {} obj
 * @return {Boolean}
 */
function updatePresentRow(obj)
{

	if(!validatePresentOper("update")) return false;
	
	var table = $.table.get("zsTable");
    var custEdit = new Array();
	custEdit["PHONE_TYPE"] = $("#PHONE_TYPE").val();
	custEdit["PRESENT_SERIAL_NUMBER"] = $("#PRESENT_SERIAL_NUMBER").val();
	custEdit["PRESENT_MONEY"] = $("#PRESENT_MONEY").val();
	custEdit["REMARK"] = $("#REMARK").val();
	custEdit["X_TAG"] = "2";

	table.updateRow(custEdit);
	$("#btnSubmit").css("display",""); 
}


/**
 * 删除presentTable行
 * @param {} obj
 * @return {Boolean}
 */
function deletePresentRow(obj)
{
	var table = $.table.get("zsTable");
	/* 删除表格行 */
	table.deleteRow();
}
