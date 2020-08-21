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

function querytaskinfo() {	
	$.beginPageLoading("数据查询中..");
	$.ajax.submit('TaskInfoPart', 'queryBatTaskStart', null, 'TaskDataPart',
			function(data) {
				$.endPageLoading();

			}, function(error_code, error_info) {
				$.endPageLoading();
				alert(error_info);
			});
}

function submit() {
	//var data = $.table.get("idList").getTableData();
	var isCk = isChecked("QueryListTable", null, "idList");
	if(!isCk)
	{
		alert('未选择批量任务！');
		return false;
	}
	var data = getCheckedTableData("QueryListTable", null, "idList");
	var param = "&PARAM="+data;
	
	/**
	 * 判断批量是否已经打印
	 * @author  zhuoyingzhi
	 * @date 20180724
	 */
	var batchOperType=$("#BATCH_OPER_TYPE").val();
	var printTag=0;
	var cmpTag = "1";
	var msgInfo="";
	
	if(batchOperType == 'CREATEPREUSER_M2M'||batchOperType == 'CREATEPREUSER_PWLW'){
		//行业应用卡批量开户
		$.ajax.submit(null,'chexkBatTask',param,'',function(data){ 
		    printTag=data.get("printTag");
			if(printTag=="0"){ 
				//存在 未打印或打印信息不存在的
				msgInfo=data.get("msgInfo");
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});		
		
		/**
	     * REQ201904260020新增物联网批量开户界面权限控制需求
	     * 免人像比对权限判断、免受理单打印
	     * @author mengqx
	     * @date 20190515
	     * @param clcle
	     * @throws Exception
	     */
		$.ajax.submit(null,'isBatCmpPic','','',
				function(data){ 
			var flag=data.get("CMPTAG");
			if(flag=="0"){ 
				cmpTag = "0";
			}
			$.endPageLoading();
		},function(error_code,error_info){
			$.MessageBox.error(error_code,error_info);
			$.endPageLoading();
		},{
			"async" : false
		});
	}
	//
	if (batchOperType == 'TD_FIXED_PHONE_STOP'||batchOperType == 'TT_FIXED_PHONE_STOP'||batchOperType == 'BATREALNAME'||batchOperType == 'BATUPDATEPSW'||batchOperType == 'MODIFYTDPSPTINFO'){
        // TD二代无线固话批量停机 和 铁通固话批量停机 检查是否存在未打印的受理单
        $.ajax.submit(null,'chexkBatTask',param,'',function(data){
            printTag=data.get("printTag");
            if(printTag=="0"){
                //存在 未打印或打印信息不存在的
                msgInfo=data.get("msgInfo");
            }
            $.endPageLoading();
        },function(error_code,error_info){
            $.MessageBox.error(error_code,error_info);
            $.endPageLoading();
        },{
            "async" : false
        });

        if (printTag == '0'){// printTag 为0 存在未打印的受理单
            MessageBox.error("存在未打印的批次号",msgInfo,null, null, null, null);
            return false;
		}
    }

	if(cmpTag == '0' && printTag == '0' && (batchOperType == 'CREATEPREUSER_M2M'||batchOperType == 'CREATEPREUSER_PWLW')){
		MessageBox.error("存在未打印的批次号",msgInfo,null, null, null, null);
		return false;
	}
	
	//var data = Wade.table.get('QueryListTable');
	
	$.beginPageLoading("数据提交中..");
	$.ajax.submit(null,"batTaskNowRun",param,'TaskDataPart',function(data){
		$.endPageLoading();
		alert('立即启动成功！');
		querytaskinfo();
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
	
}

function ontimesubmit() {
	var startDate=$("#cond_BATCH_START_DATE").val();
	var startTime=$("#cond_BATCH_TASK_TIME").val();
	var isCk = isChecked("QueryListTable", null, "idList");
	if(!isCk)
	{
		alert('未选择批量任务！');
		return false;
	}	
	if(startDate == "" || startTime== "")
	{
		alert('预约启动时间和日期不能为空！');
		return false;
	}
	var data = getCheckedTableData("QueryListTable", null, "idList");
	var param = "&PARAM="+data;
	param = param + "&StartDate=" + startDate;
	param = param + "&StartTime=" + startTime;
	$.beginPageLoading("数据提交中..");
	$.ajax.submit(null,"batTaskOnTimeRun",param,'TaskDataPart',function(data){
		$.endPageLoading();
		alert('预约启动成功！');
	},
	function(error_code,error_info){
		$.endPageLoading();
		alert(error_info);
	});
}

function getCheckedTableData(tbName, g, chekboxName) {
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

function DealMark(chk){
	if (chk.attr("checked") == true) {
		chk.attr("rowIndex", $.table.get("QueryListTable").getTable().attr("selected"));
	}
}
