(function($){
	$.extend({
		CarMgr:{
			dynamicParamData:$.DataMap(), // 临时参数容器
			idStrData:$.DataMap(),   // 待编辑或者待删除的记录ID
			/** idStrData转换为字符串 */
			toIdStr:function(){
				var ids="";
				if($.CarMgr.idStrData && ($.CarMgr.idStrData).length>0){
					($.CarMgr.idStrData).eachKey(function(key,index,totalcount){
						ids += "," + key;
					});
				}
				if(ids.indexOf(",")>-1){
					ids=ids.substring(1);
				}
				return ids;
			},
			/** 自定义查询条件检验 */
			verifyBuziCond:function(){
					var beginDate=$("#COND_START_DATE").val(); 
					var endDate=$("#COND_END_DATE").val(); 
					var d1 = new Date(beginDate.replace(/\-/g, "\/")); 
					var d2 = new Date(endDate.replace(/\-/g, "\/")); 

					if(beginDate!=""&&endDate!=""&&d1 >=d2) 
					{ 
						alert("开始时间不能大于结束时间！"); 
						return false; 
					}
					return true;
			},
			/** 查询 */
			queryInfo:function(){
				if(!$.validate.verifyAll("QueryCondPart")){
					return false;
				}
				
				if(!this.verifyBuziCond()){
					return false;
				}
				
				$.beginPageLoading("数据查询中.."); 				
				$.ajax.submit('QueryCondPart,QueryListPart,NavBarPart','queryByECID',null,'QueryCondPart,QueryListPart,NavBarPart',function(data){		
				   if(!data || data.length<1){
					   MessageBox.alert("状态","没有查询到数据。");	
				   }
				   $.endPageLoading();
				},	
				function(error_code,error_info){
					alert(error_info);
					$.endPageLoading();					
				});
			},
			/** 编辑区块对象 */
			EditDiv:{
				id:'editInfoPart',
				editInfoPartTitle_id:'editInfoPartTitle',
				li_SERIAL_NUMBER_id:'li_SERIAL_NUMBER',
				SERIAL_NUMBER_id:'SERIAL_NUMBER',				
				show:function(){
					$("#RSRV_STR2").val("9");
					$('#'+this.id).css('display','');
				},
				hid:function(){
					$('#'+this.id).css('display','none');
				},
				submit:function(){
					$.CarMgr.submitApprove();
				}
			},		
			/** 提交审批 */
			submitApprove:function(){
				if(!$.validate.verifyAll($.CarMgr.EditDiv.id)) {
					return false;
				}
				if("9"==$("#RSRV_STR2").val()){
					alert("尚未修改审批状态，请修改审批状态后再提交！");
					return false;
				}
				$.beginPageLoading("审批中...");
				$.ajax.submit($.CarMgr.EditDiv.id, 'onSubmitApprove', "&OPR_SEQS="+$.CarMgr.toIdStr(), null, function(data)
			    {
					if(data.length>0 && data.get("RESULT_CODE")=="0000")
					{
						alert("审批成功！");
					} else {
						alert("审批失败！");
					}
					$.endPageLoading();
					$.CarMgr.EditDiv.hid();
					$.CarMgr.queryInfo();
				},
				function(error_code,error_info)
				{
					alert(error_info);
					$.endPageLoading();					
					$.CarMgr.EditDiv.hid();
			    });
			},		
			// 选择记录
			getSelectRecord:function(){
				$.CarMgr.idStrData.clear();
				$("input[name='chkbox']").each(function(){
					if($(this).attr("checked")==true && $(this).val() && $(this).val().length>0){
						$.CarMgr.idStrData.put($(this).val(),$(this).val());
					}
				});
				return $.CarMgr.idStrData;
			},
			checkSelectedRecord:function(){
				$.CarMgr.idStrData.clear();
				var flag = true;
				$("input[name='chkbox']").each(function(){
					if($(this).attr("checked")==true && $(this).val() && $(this).val().length>0){
						if($(this).attr("approveresl")!='9'){
							alert("选择的审批记录中不可含有通过或不通过的审批结果！");
							flag = false;
						}
						$.CarMgr.idStrData.put($(this).val(),$(this).val());
					}
				});
				if(flag && !($.CarMgr.idStrData).length>0){
					alert("请选择一条或多条待处理的记录进行审批！");
					flag = false;
				}
				return flag;
			},
			/** 显示编辑 */
			showApproveDiv:function(){
				if(!this.checkSelectedRecord()){
					return false;
				}
				$.CarMgr.EditDiv.show();
			}
			
		}
	});
})(Wade);

