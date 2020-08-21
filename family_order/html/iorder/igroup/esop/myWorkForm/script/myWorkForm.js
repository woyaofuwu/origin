$(function(){
	debugger;
	var exceptionId =$("#EXCEPTION").val();
	if(exceptionId){
		MessageBox.error("错误信息", exceptionId, function(btn){
			$.nav.close();
		});
	}
	$("#myExport").beforeAction(function(e){
		if ($("#tableBody").children("tr").length < 1) {
            MessageBox.alert("提示信息", "没有可导出的数据！");
            return false;
        }
		return confirm('是否导出?');
	});
	showPopup('popup02', 'UI-advance');
	
	
	var  eparachyCodeType = $("#EPARCHY_CODE_TYPE").val();
	
	if("false"==eparachyCodeType){
		 $("#cond_CITY_CODE").attr('disabled','');
	}else{
		$("#cond_CITY_CODE").attr('disabled','disabled');
	}
});


function queryWorkformDetail(obj){

	$.beginPageLoading('正在查询信息...');
	$.ajax.submit('QueryCondPart,AdvanceConditionPart','queryWorkformDetail',null,'QueryResultPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
//根据业务类型，查询流程信息等
function queryDatelineCodeType(obj){
	debugger;
	var ibsysId = $(obj).attr("IBSYSID");
	var groupId = $(obj).attr("GROUP_ID");
	var productName = $(obj).attr("PRODUCT_NAME");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
	var templetName = $(obj).attr("TEMPLET_NAME");
	var urgencyLevel = $(obj).attr("URGENCY_LEVEL");
	var title = $(obj).attr("TITLE");
	var bpmtempletId = $(obj).attr("BPM_TEMPLET_ID");
	var isFinish = $(obj).attr("IS_FINISH");
	var busiFormId = $(obj).attr("BUSIFORM_ID");
	var pram = "&BUSIFORM_ID="+busiFormId+"&IBSYSID="+ibsysId+"&GROUP_ID="+groupId+"&PRODUCT_NAME="+encodeURIComponent(productName)+"&DEAL_STAFF_ID="+dealStaffId+"&TEMPLET_NAME="+encodeURIComponent(templetName)+"&URGENCY_LEVEL="+encodeURIComponent(urgencyLevel)+"&TITLE="+encodeURIComponent(title)+"&BPM_TEMPLET_ID="+bpmtempletId+"&IS_FINISH="+isFinish;
	$.ajax.submit('','queryDatelineCodeType',pram,'',function(data){
		$.endPageLoading();
		openNav("业务详情", "igroup.myWorkForm.Summarize&listener=queryData"+pram, "","", "");
		//var redirectUrl = $.redirect.buildUrl("igroup.myWorkForm.Summarize", "queryData", pram);
		  //redirectNavByUrl("业务详情", redirectUrl, "");
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
//根据订单号获取专线信息及资管EOMS流程
function queryDatelineAttr(obj){
	debugger;
	var ibsysId = $(obj).attr("IBSYSID");
	var nodeId = $(obj).attr("NODE_ID");
	var productId = $(obj).attr("PRODUCT_ID");
	var bpmTempletId = $(obj).attr("BPM_TEMPLET_ID");
	var pram ="&PRODUCT_ID="+productId+"&IBSYSID="+ibsysId+"&NODE_ID="+nodeId+"&BPM_TEMPLET_ID="+bpmTempletId;
	
	$.ajax.submit('','queryDatelineAttrQueryData',pram,'',function(data){
		$.endPageLoading();
		openNav("专线详情", "igroup.myWorkForm.QueryDatelineAttr&listener=queryData"+pram,"","", "");
		//var redirectUrl = $.redirect.buildUrl("igroup.myWorkForm.QueryDatelineAttr", "queryData", pram);
		//  redirectNavByUrl("专线详情", redirectUrl, "");
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
	
}
//查询曾经节点处理人
function queryOnceTemplet(obj){
	var ibsysId = $(obj).attr("IBSYSID");
	var dealstaffName = $(obj).attr("DEAL_STAFF_NAME");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
	var acceptTime = $(obj).attr("ACCEPT_TIME");
	var pram ="&IBSYSID="+ibsysId+"&DEAL_STAFF_NAME="+encodeURIComponent(dealstaffName)+"&DEAL_STAFF_ID="+dealStaffId+"&ACCEPT_TIME="+acceptTime;
	openNav("曾经节点处理详情", "igroup.myWorkForm.ViewHisStaff&listener=queryData"+pram, "","" , "");
	//var redirectUrl = $.redirect.buildUrl("igroup.myWorkForm.ViewHisStaff", "queryData", pram);
	//  redirectNavByUrl("曾经节点处理详情", redirectUrl, "");
}
//调用撤单接口
function queryDatelineRemoveOne(obj){
	debugger;
	var ibsysId = $(obj).attr("IBSYSID");
	var dealStaffId = $(obj).attr("DEAL_STAFF_ID");
//	if("undefined"==dealStaffId||undefined==dealStaffId||""==dealStaffId){
//		MessageBox.error("提示信息", "未获取到工单处理人，无法撤单！");
//		return false; 
//	}
	var createState = $(obj).attr("CREATE_STATE");
	var nodeId = $(obj).attr("NODE_ID");
	var productId = $(obj).attr("PRODUCT_ID");
	var bpmTempletId = $(obj).attr("BPM_TEMPLET_ID");
	var busiFromId = $(obj).attr("BUSIFORM_ID");
	if("MANUALSTOP"==bpmTempletId && "newWorkSheet"==nodeId){
		MessageBox.error("提示信息", "您发起的停机单撤单，已经通过审核，无法发起撤单！");
		return false; 
	}
	var pram ="&BUSIFORM_ID="+busiFromId+"&IBSYSID="+ibsysId+"&DEAL_STAFF_ID="+dealStaffId+"&NODE_ID="+nodeId+"&BPM_TEMPLET_ID="+bpmTempletId;
	openNav("订单撤销", "igroup.myWorkForm.DatelineRemoveOne&listener=queryData"+pram, "","" , "");
	//var redirectUrl = $.redirect.buildUrl("igroup.myWorkForm.DatelineRemoveOne", "queryData", pram);
	 // redirectNavByUrl("订单撤销", redirectUrl, "");
}


//产品类型和产品选择的联动
function refreshProduct(data){
	debugger;
	var product_type = data.value;
	if(product_type=='1'){
		$("#cond_PRODUCT_NO").attr('disabled','true');
		$("#cond_PRODUCT_NO").val("");
	}else{
		$("#cond_PRODUCT_NO").attr('disabled','');
	}
	// 提交
	$.ajax.submit(null, 'queryProduct', 'PRODUCT_TYPE=' + product_type,
			'QueryProductList', function(data) {
		
				$.endPageLoading();
			}, function(error_code, error_info) {
				$.endPageLoading();
				MessageBox.alert("",error_info);
			});
}


//查询资管eoms状态信息
function queryEomsDatelineCodeType(obj){
	debugger;
	var ibsysId = $(obj).attr("IBSYSID");
	var recodeNum = $(obj).attr("RECORD_NUM");
	var bpmTempletId =$("#BPM_TEMPLET_ID").val();
	var bizRange = $(obj).attr("BIZRANGE");
	var changeMode = $(obj).attr("CHANGEMODE");
	var productId = $("#PRODUCT_ID").val();
	if("ECHANGERESOURCECONFIRM"==bpmTempletId||"ERESOURCECONFIRMZHZG"==bpmTempletId){
		MessageBox.error("提示信息", "您好，勘察单不展示资管回复详情，请知悉！");
		return false; 
	}else{
		var pram ="&CHANGEMODE="+encodeURIComponent(changeMode)+"&BIZRANGE="+encodeURIComponent(bizRange)+"&PRODUCT_ID="+productId+"&RECORD_NUM="+recodeNum+"&IBSYSID="+ibsysId+"&BPM_TEMPLET_ID="+bpmTempletId;
		$.ajax.submit('','summarizeEomsQuery',pram,'',function(data){
			$.endPageLoading();
			openNav("业务详情", "igroup.myWorkForm.SummarizeEoms&listener=queryData"+pram, "", "", "");
			//var redirectUrl = $.redirect.buildUrl("group.myWorkForm.SummarizeEoms", "queryData", pram);
			//  redirectNavByUrl("业务详情", redirectUrl, "");
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
	}
}


function queryExpWorkfrom(obj){
	debugger;
	var ibsysid =$(obj).attr("IBSYSID");
	var flag =$(obj).attr("FLAG");
	var pram ="&BI_SN="+ibsysid+"&FLAG="+flag;
	openNav("异常信息", "igroup.expWorkForm.ExpWorkForm&listener=initPageExp"+pram, "", "", "");

	
}

function submitState(){
	debugger;
	var stateInfo = $("#STATE_INFO").val();
	var dealResult = $("#order_DEAL_RESULT").val();
	var ibsysId = $("#IBSYSID").val();
	var busiFormId = $("#BUSIFORM_ID").val();
	var bpmTempletId = $("#BPM_TEMPLET_ID").val();
	var datalineSize = $("#DATALINE_SIZE").val();
	if(dealResult==null||dealResult==""){
		$.validate.alerter.one($("#order_DEAL_RESULT")[0],"您没有选择撤单原因，请选择!");
		return false;
	}
	
	var remark = $("#order_REMARK").val();
	if(remark==null||remark==""){
		$.validate.alerter.one($("#order_REMARK")[0],"您没有输入撤单意见，请输入撤单意见!");
		return false;
	}

	var dataset = new Wade.DatasetList();
	var sheetType = "";
	var agreeSizeAll = "";
	if("false"!=stateInfo){
		var rowDatas = myTable.getCheckedRowsData("TRADES");
		if(rowDatas==null||rowDatas==""){
			$.validate.alerter.one(rowDatas,"您没有勾选撤单的专线!");
			return false;
		}
		if("MANUALBACK"==bpmTempletId){
			var size = rowDatas.length;
			if(size > 1){
				$.validate.alerter.one(rowDatas,"复机单不允许勾选多条专线撤单，请只选择一条专线撤单!");
				return false;
			}
		}
		sheetType = rowDatas.get(0).get("SHEETTYPE");
		for(var i=0;i<rowDatas.length;i++){
			var rowData = rowDatas.get(i);
			var flag = rowData.get("FLAG");
			var agreeSize = rowData.get("AGREECANCEL_SIZE");
			var productNo = rowData.get("PRODUCT_NO");
			if("true"==flag){
				MessageBox.error("提示信息", "您选择的【"+productNo+"】专线已处于待归档或归档状态，无法撤单，请取消该笔专线撤单选择！");
				return false; 
			}		
			if(agreeSize>0){
				agreeSizeAll += "您选择的【"+productNo+"】专线已发起过【"+agreeSize+"】次撤单"+",";
			}
			dataset.add(rowData);
		}
		
	}
	MessageBox.confirm("提示信息", agreeSizeAll+"是否发起撤单？", function(btn){
		if("ok" == btn)
		{
			$.beginPageLoading('正在撤单...');
			$.ajax.submit('','datelineRemoveOne',"&BPM_TEMPLET_ID="+bpmTempletId+"&SHEETTYPE="+sheetType+"&DATALINE_LIST="+dataset.toString()+"&DATALINE_SIZE="+datalineSize+"&BUSIFORM_ID="+busiFormId+"&IBSYSID="+ibsysId+"&REMARK="+remark+"&DEAL_RESULT="+dealResult,'',function(data){
				$.endPageLoading();
				MessageBox.success("提交", "提交成功！",function(){
					closeNav();
				});
			},function(error_code,error_info,derror){
				$.endPageLoading();
				showDetailErrorInfo(error_code,error_info,derror);
			});
		}
	});
}
