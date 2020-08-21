


function myTabSwitchAction(ptitle,title){

	if(title =="规则集关联规则")
	{
		var parScripId = document.getElementById("SCRIPT_ID").value;
		var childScripId =frames[2].document.getElementById("SCRIPT_ID");
		if(parScripId && parScripId!="" && childScripId)
		{
			childScripId.value=parScripId;
		}
		
		var parRuleBizId = document.getElementById("RULE_BIZ_ID").value;
		var childRuleBizId =frames[2].document.getElementById("RULE_BIZ_ID");
		if(parRuleBizId && parRuleBizId!="" && childRuleBizId)
		{
			childRuleBizId.value=parRuleBizId;
		}
	}
	return true;
}


function scriptTypeChange()
{
	var scriptTypeId =$("#SCRIPT_TYPE").val();
	
	if(scriptTypeId == 'SQL')
	{
		$("#SCRIPT_PATH").val("com.ailk.script.rule.common.SplCheckBySql");
		$("#SCRIPT_PATH").attr("disabled", true);
		
		$("#SCRIPT_ID").val("");
		$("#SCRIPT_ID").attr("disabled", false);
		
		$("#SCRIPT_DESCRIPTION").val("");
		$("#SCRIPT_DESCRIPTION").attr("disabled", false);
		
	}else if(scriptTypeId == 'COMPARA')
	{
		$("#SCRIPT_PATH").val("com.ailk.script.rule.common.SplCheckByOperation");
		$("#SCRIPT_PATH").attr("disabled", true);
		
		$("#SCRIPT_ID").val("splCheckByOperation");
		$("#SCRIPT_ID").attr("disabled", true);
		
		$("#SCRIPT_DESCRIPTION").val("业务操作符号判断");
		$("#SCRIPT_DESCRIPTION").attr("disabled", true);
		
	}else
	{
		$("#SCRIPT_PATH").val("");
		$("#SCRIPT_PATH").attr("disabled", false);
		
		$("#SCRIPT_ID").val("");
		$("#SCRIPT_ID").attr("disabled", false);
		
		$("#SCRIPT_DESCRIPTION").val("");
		$("#SCRIPT_DESCRIPTION").attr("disabled", false);
		
	}
}

/**
 * 规则
 * @return
 */
function saveRuleDifinition()
{
	if(!$.validate.verifyAll()) return false;
	$.beginPageLoading("正在保存......");
	
	parent.document.getElementById("SCRIPT_ID").value=$("#SCRIPT_ID").val();
	$.ajax.submit('EditPart', 'saveRuleDifinition','' ,'EditPart', function(){
		$.endPageLoading();
		alert("添加成功！");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	return true;
}

/**
 * 规则集
 * @return
 */
function saveRuleBusiness()
{
	if(!$.validate.verifyAll()) return false;
	$.beginPageLoading("正在保存......");
	$.ajax.submit('EditPart', 'saveRuleBusiness','' ,'EditPart', function(data){
		$.endPageLoading();
		parent.document.getElementById("RULE_BIZ_ID").value=data.get(0).get("RULE_BIZ_ID");
		alert("添加成功！新规则集编号为：  "+data.get(0).get("RULE_BIZ_ID"));
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	return true;
}


/**
 * 规则集绑定规则
 * @return
 */
function saveRuleRelation()
{
	if(!$.validate.verifyAll()) return false;
	$.beginPageLoading("正在保存......");
	
	var tableData = $.table.get("ParameterTable").getTableData();
	$.ajax.submit('RelationPart', 'saveRuleRelation','&TABLE_DATA='+tableData ,'RelationPart,ParameterPart,ParameterEditPart', function(){
		$.endPageLoading();
		alert("添加成功！");
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
	return true;
}



function addTableRow() {
	var edit = $.ajax.buildJsonData("ParameterEditPart");
	$.table.get("ParameterTable").addRow(edit);
}

function delRow()
{
	$.table.get("ParameterTable").deleteRow();
}