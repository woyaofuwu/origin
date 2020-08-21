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
				addDivTitle:'新增', 
				editDivTitle:'编辑',
				li_SERIAL_NUMBER_id:'li_SERIAL_NUMBER',
				SERIAL_NUMBER_id:'SERIAL_NUMBER',				
				show:function(){
					$('#'+this.id).css('display','');
				},
				hid:function(){
					$('#'+this.id).css('display','none');
				},
				setTitle:function(title){
					$('#'+this.editInfoPartTitle_id).text(title);
				},
				init:function(){
					this.setTitle(this.addDivTitle);	
					
					$('#'+this.li_SERIAL_NUMBER_id).css('display','');
					$('#'+this.SERIAL_NUMBER_id).attr('nullable','no');
					$('#'+this.SERIAL_NUMBER_id).val('');
					$('#TYPE_ID').val('');
				},
				init4Edit:function(){
					this.setTitle(this.editDivTitle);	
					
					$('#'+this.li_SERIAL_NUMBER_id).css('display','none');
					$('#'+this.SERIAL_NUMBER_id).attr('nullable','yes');
				},
				submit:function(){
					var title =$('#'+this.editInfoPartTitle_id).text();
					if(this.addDivTitle==title){
						$.CarMgr.submitAdd();
					}else if(this.editDivTitle==title){
						$.CarMgr.submitEdit();
					}
				}
			},			
			/** 显示新增 */
			showAddDiv:function(){
				$.CarMgr.EditDiv.show();
				$.CarMgr.EditDiv.init();
			},			
			/** 提交新增 */
			submitAdd:function(){
				if(!$.validate.verifyAll($.CarMgr.EditDiv.id)) {
					return false;
				}
				if(isNaN($("#DISCNT_RATE").val())){
					alert("折扣率必须是数字！");
					return false;
				}
				$.beginPageLoading("数据新增中...");
				$.ajax.submit($.CarMgr.EditDiv.id, 'onSubmitAdd', null, null, function(data)
			    {
					if(data.length>0 && data.get("RESULT_CODE")=="0000")
					{
						alert("添加成功！");
					} else {
						alert("增加失败！");
					}
					$.endPageLoading();
					$.CarMgr.EditDiv.hid();
					var grouId = $("#EC_ID").val();
					$("#COND_EC_ID").val(grouId);
					$.CarMgr.queryInfo();
				},
				function(error_code,error_info)
				{
					alert(error_info);
					$.endPageLoading();					
					$.CarMgr.EditDiv.hid();
			    });
			},
			// 机卡绑定则附件不能为空
			CardBind:function(){
				if($("#CARD_BIND").val()=='1'){
					alert("请添加证明材料");
					$("#PROV_DOC").attr("nullable", "no");
				}else 
					$("#PROV_DOC").attr("nullable", "yes");
			},			
			// 选择记录
			getSelectRecord:function(){
				$.CarMgr.idStrData.clear();
				$("input[name='chkbox']").each(function(){
					debugger;
					if($(this).attr("checked")==true && $(this).val() && $(this).val().length>0){
						$.CarMgr.idStrData.put($(this).val(),$(this).val());
					}
				});
				return $.CarMgr.idStrData;
			},
			checkSelectedRecord:function(){
				var ids=this.getSelectRecord();
				if(!ids || ids.length<1){
					alert("请选择有效记录");
					return false;
				}
				return true;
			},
			/** 显示编辑 */
			showEditDiv:function(){
				if(!this.checkSelectedRecord()){
					return false;
				}
				
				// 单个记录需从后台获取相关信息。
				this.getByPK();
				
				$.CarMgr.EditDiv.show();
				$.CarMgr.EditDiv.init4Edit();
			},
			/** 获取对象 */
			getByPK:function(){
				// 只有单个对象才获取。
				if($.CarMgr.idStrData && $.CarMgr.idStrData.length>1){
					return false;
				}
				
				$.beginPageLoading("数据获取中...");
				$.ajax.submit($.CarMgr.EditDiv.id, 'onGetByPK', '&LOG_ID='+$.CarMgr.toIdStr(), $.CarMgr.EditDiv.id, function(data)
			    {
					if(data && data.length>0)
					{
						// do nothing.
					} else {
						alert("获取数据失败！");
					}
					$.endPageLoading();
				},
				function(error_code,error_info)
				{
					alert(error_info);
					$.endPageLoading();
			    },
			    {
					"async" : false  // 必须第七个参数
				});
			},
			/** 提交编辑 */
			submitEdit:function(){
				if(!this.checkSelectedRecord()){
					return false;
				}
				
				if(!$.validate.verifyAll($.CarMgr.EditDiv.id)) {
					return false;
				}
				
				$.beginPageLoading("数据提交中...");
				$.ajax.submit($.CarMgr.EditDiv.id, 'onSubmitEdit', '&ids='+$.CarMgr.toIdStr(), null, function(data)
			    {
					if(data && data.length>0 && data.get("RESULT_CODE")=="0000")
					{
						alert("编辑成功！");
						$.CarMgr.queryInfo();
					} else {
						alert("编辑失败！");
					}
					$.endPageLoading();
					$.CarMgr.EditDiv.hid();
				},
				function(error_code,error_info)
				{
					alert(error_info);
					$.endPageLoading();					
					$.CarMgr.EditDiv.hid();
			    });
			},
			/** 删除 */
			submitDel:function(){
				if(!this.checkSelectedRecord()){
					return false;
				}
				
				if(!window.confirm("选择记录将物理删除，你确认要继续吗？")){
			    	return false;
			    }
				
				$.beginPageLoading("数据提交中...");
				$.ajax.submit('', 'onSubmitDel', '&ids='+$.CarMgr.toIdStr(), null, function(data)
			    {
					if(data && data.length>0 && data.get("RESULT_CODE")=="0000")
					{
						alert("删除成功！");
					} else {
						alert("删除失败！");
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
			}
			
		}
	});
})(Wade);

