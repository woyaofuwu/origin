function initBatCancelPage(){
    var typeId = getElement("cond_TYPEID").value;
    if(typeId == '01') {
   		 $("#bbatch").attr("disabled", true);
    } else  if (typeId == '02'){
  	     $("#bSN").attr("disabled", true);
    } else {
    
    }
}


function changeQueryType() {
    var typeId=$('#cond_TYPEID').val();
    if (typeId=="01") 
    {
	    $('#bSerialId').css("display","block");
	    $('#bBatchId').css("display","none");
    }
    else if (typeId=="02") 
    {
        $('#bSerialId').css("display","none");
	    $('#bBatchId').css("display","block");
    } else{
    	$('#bSerialId').css("display","none");
	    $('#bBatchId').css("display","none");
    } 
 }


function queryInfo(){
	var typeId=$('#cond_TYPEID').val();
	if(typeId == null || typeId == ""){
		alert("请先选择查询类型");
		return false;
	}
	var cond_NUMBER=$('#cond_NUMBER').val();
	if(cond_NUMBER == null || cond_NUMBER == ""){
		if(typeId == '01'){
			alert("请输入服务号码后查询");
		}else{
			alert("请输入批次号码后查询")
		}
		return false;
	}
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('QueryCondPart', 'queryInfo', null, 'TaskDataPartInfo,QueryListPart', function(data){
		$.endPageLoading();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
}


function isChecked(tbName, g, chekboxName)
{
	var isok = false;
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						isok = true;
						return isok;
					}
					j = null;
				});
	}else{
		c = null;
		d = null;
		isok = false;
	}
	return isok;
}


function getCheckedTableData(tbName, g, chekboxName)
{
	var b = new Wade.DatasetList();
	var d = Wade.table.get(tbName);
	var checkboxname = chekboxName;
	var c = Wade("tbody", d.getTable()[0]);
	var e = d.tabHeadSize;
	if (c) {
		Wade("tr", c[0]).each(
				function(h, i) {
					var isChecked = $('input[name=' + checkboxname + ']', this)
							.attr("checked");
					if (isChecked) {
						var j = d.getRowData(g, h + e);
						if (j) {
							b.add(j);
						}
					}
					j = null;
				});
	}
	c = null;
	d = null;
	return b;
}

function checkChoose(){
	var rowData = $.table.get("QueryListTable2").getRowData(); 
	if (rowData.length == 0) {
		alert("未选择任何记录！");
		return false;
	}
	
	var phoneTable = $.table.get("QueryListTable2").getTableData(null, true);
	
	var hasnum = false;
	phoneTable.each(function(item,index,totalCount){
	var onecancelnum = item.get("CANCEL_NUM");
	if(onecancelnum != "0"){
		hasnum = true ;
	}
	});
	if(!hasnum){
		alert('该业务批次下没有可返销数目');
		return false;
	}
	return true;
}

function DealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("QueryListTable").getTable().attr("selected"));
	}
}

function submitCancel()
{
	var isCk = isChecked('QueryListTable', null, "idList1");
	if(!isCk)
	{
		alert('未选择任何记录！');
		return false;
	}
	var data = getCheckedTableData('QueryListTable', null, "idList1");
	var param = "&PARAM=" + data;
	$.beginPageLoading("返销处理中...");
	$.ajax.submit(null, 'cancelBySerialNum', param, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
		var title= "返销登记结果!";
		var serialNumber=data.get("SERIAL_NUMBER");
		var content=  "返销登记成功，请去批量结果查询页面查看结果,<br/> 点击[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回本页面，点击[<a href=\"#nogo\" onclick=\"javascript:openNav('批量业务结果查询','bat.battaskquery.BatDealQuery', 'initQuery','&cond_SERIAL_NUMBER="+serialNumber+"');window.closeNavByTitle('批量返销业务')\">批量结果查询</a>]" ;
		$.showSucMessage(title,content);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}

function submitCancel2()
{
	var checkResult = checkChoose();
	if(!checkResult){
		return checkResult;
	}
	var data = getCheckedTableData('QueryListTable2', null, "idList");
	var param = "&PARAM=" + data;
	$.beginPageLoading("返销处理中...");
	$.ajax.submit(null, 'cancelByBatchid', param, 'TaskDataPartInfo', function(data){
		$.endPageLoading();
		var title= "返销登记结果!";
		var batchId=data.get("BATCH_ID");
		var content=  "返销登记成功，请去批量结果查询页面查看结果,<br/> 点击[<a jwcid='@Any' href=\"javascript:$.closeMessage(this)\">返回</a>]返回本页面，点击[<a href=\"#nogo\" onclick=\"javascript:openNav('批量业务结果查询','bat.battaskquery.BatDealQuery', 'initQuery','&cond_BATCH_ID="+batchId+"');window.closeNavByTitle('批量返销业务')\">批量结果查询</a>]" ;
		$.showSucMessage(title,content);
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
    });
    
}
