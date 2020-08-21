function queryWorkList()
{
	$.beginPageLoading("数据查询中...");
	$.ajax.submit("CondPart", "queryWorkList", "", "ResultPart", function(data){
		$.endPageLoading();
	}, 
	function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
    });
}

function openFlowInst(el)
{
	var busiTypeCode = $(el).attr("busiTypeCode");
	
	if(busiTypeCode == "41"){//待阅
		var todoUrl = $(el).attr("todoUrl");
		var urlArr = todoUrl.split("?");
		openNav('待阅工单', encodeURI(urlArr[1].substring(13)), '', '', urlArr[0]);
	}else if(busiTypeCode == "40" || busiTypeCode == "42" 
		|| busiTypeCode == "43" || busiTypeCode == "44" || busiTypeCode == "45")
	{//待阅
		var instId = $(el).attr("instId");
		var taskId = $(el).attr("taskId");
		openNav('待阅工单', "igroup.readwork.ReadWork", 'initPage', '&INST_ID='+instId+"&TASK_ID="+taskId, "/order/iorder");
	}
	else
	{//待办
		var todoUrl = $(el).attr("todoUrl");
		var urlArr = todoUrl.split("?");
		var instId = $(el).attr("instId");
		var param = "&INST_ID="+instId
	    ajaxSubmit("",'checkInfo',param,'',function(data){
	    	openNav('工单办理', encodeURI(urlArr[1].substring(13)), '', '', urlArr[0]);
			$.endPageLoading();
		},function(error_code,error_info,derror){
			$.endPageLoading();
			showDetailErrorInfo(error_code,error_info,derror);
		});
		
		//openNav('工单办理', urlArr[1].substring(13), '', '', urlArr[0]);
	}
	
}

function openReadWork(el)
{
	var instId = $(el).attr("instId");
	openNav('已办工单', "igroup.readwork.ReadWork", 'initPage', '&INST_ID='+instId+"&QUERY_TYPE=DONE_WORK", "/order/iorder");
}

function transferWorkListSubmit(obj){
	debugger;
	var offerChaList = new Wade.DatasetList();
	if($.validate.verifyAll("querySubmit")){
		var newStaffId=$("#pattr_newStaffId").val();
		var chks = $("#myTbody [type=checkbox]");
		{
			//var tempDataLine = new Wade.DatasetList();
			for ( var j = 0; j < chks.length; j++) 
			{
				var returnData = new Wade.DataMap();
				if (chks[j].checked)// 获取选中的列表
				{
					var offerChaSpecDataset = new Wade.DatasetList();
					var instId=chks[j].value;
					var info_auth=$(chks[j]).attr("info_auth");
					var info_ibsysid=$(chks[j]).attr("info_ibsysid");
					var info_groupid=$(chks[j]).attr("info_groupid");
					var info_nodename=$(chks[j]).attr("info_nodename");
					var info_productname=$(chks[j]).attr("info_productname");
					var info_custname=$(chks[j]).attr("info_custname");
					var info_topic=$(chks[j]).attr("info_topic");
					if(info_auth==newStaffId){
						MessageBox.alert("不能将已在该工号下的工单转派给该工号");
						return false;
					}
					returnData.put("oldStaffId",info_auth);
					returnData.put("INST_ID",instId);
					returnData.put("IBSYS_ID",info_ibsysid);
					returnData.put("GROUP_ID",info_groupid);
					returnData.put("NODE_NAME",info_nodename);
					returnData.put("PRODUCT_NAME",info_productname);
					returnData.put("CUST_NAME",info_custname);
					returnData.put("INFO_TOPIC",info_topic);
					offerChaList.add(returnData);
				}
			}
		}
		if(offerChaList.length>0){
				MessageBox.confirm("确认提示", "是否将选中的待办转移至"+$("#pattr_newStaffId").val()+"工号下，且操作不可逆转是否继续?", function(btn){
					if(btn!="cancel"){
						$.beginPageLoading("转派中，请稍后...");
						var params = "&pattr_newStaffId="+newStaffId+"&pattr_remark="+$("#pattr_remark").val()+"&pattr_oldStaffId="+$("#cond_oldStaffId").val()+"&pattr_instList="+offerChaList.toString();
						ajaxSubmit(this,'transferWorkListSubmit', params,null,
						function(data){
							$.endPageLoading();
							MessageBox.success("转派成功！", "待办工单已迁移至新工号下!",function(){
								queryWorkList();
				    		});
						},
						function(error_code,error_info,derror){
							$.endPageLoading();
							showDetailErrorInfo(error_code,error_info,derror);
						});
					}
					
				},null, null);	
			
		}else{
			MessageBox.alert("请至少选择一条待办工单进行转派");
	
		}
	}
	
	
}
function  upperCase(obj){
	var str=$(obj).val().toUpperCase();
	$(obj).val(str);
}

function openStaffPopup(fieldName)
{
	$("#staffSelFrame").contents().find("#field_name").val(fieldName);
	showPopup('staffPicker','staffPickerHome');
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
	var pram = "&BUSIFORM_ID="+busiFormId+"&IBSYSID="+ibsysId+"&GROUP_ID="+groupId+"&PRODUCT_NAME="+productName+"&DEAL_STAFF_ID="+dealStaffId+"&TEMPLET_NAME="+templetName+"&URGENCY_LEVEL="+urgencyLevel+"&TITLE="+title+"&BPM_TEMPLET_ID="+bpmtempletId+"&IS_FINISH="+isFinish;
	$.ajax.submit('','queryDatelineCodeType',pram,'',function(data){
		$.endPageLoading();
		openNav("业务详情", "igroup.myWorkForm.Summarize&listener=queryData"+pram, "","", "");
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}
