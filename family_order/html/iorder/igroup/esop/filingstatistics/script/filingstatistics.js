//结合单独的Calendar组件使用时，设置Calendar组件的事件
var activeDateFieldBegin; // 多个DateField使用同一个弹出日期组件时，将当前DateFieldid元素保存在变量中
var activeDateFieldEnd;

// 刷新日期组件显示的日期
function setCalendar1Value(dateField){
	//获取当前操作dateField元素
	var el = dateField && dateField.nodeType == 1 ? dateField : document.getElementById(dateField);
	if(!el) return;
	calendar1.val(el.value); //设置日期组件值
	activeDateField = el;
}

function selectDate(e){
	if(activeDateField && activeDateField.nodeType){
		$(activeDateField).val( this.val() ); // 进行日期选择时候，把选择的日期值返回到DateField元素上
		// alert(this.lunarVal()); // 获取农历日期值
	}
}

function clearDate(e){
	if(activeDateField && activeDateField.nodeType){
		$(activeDateField).val( "" );
	}
}

// 绑定日期组件的日期选择事件
$("#BEGIN_DATE").select(selectDate);
$("#END_DATE").select(selectDate);

//绑定日期清除按钮事件
$("#BEGIN_DATE").select(clearDate);
$("#END_DATE").clear(clearDate);

function qryInfos(obj){
	if(!$("#BEGIN_DATE").val()){
		alert("请先选择开始日期！");
		return false;
	}
	
	if(!$("#END_DATE").val()){
		alert("请先选择结束日期！");
		return false;
	}
	
	$.beginPageLoading('正在查询...');
	$.ajax.submit('qryInfo','qryInfos',null,'queryPart',function(data){
		hidePopup(obj);
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}

function qryDetailInfos(obj){
	var qryTd = $(obj);
	var cols = qryTd.attr("cols");
	var subTypeCode = qryTd.attr("sub_type_code");
	var startDate = qryTd.attr("start_date");
	var endDate = qryTd.attr("end_date");
	
	var param = "&COLS="+cols+"&ROWS="+subTypeCode+"&START_DATE="+startDate+"&END_DATE="+endDate;
	$.beginPageLoading('正在查询...');
	$.ajax.submit(null,'qryDetailInfos',param,'detailInfo,detailCond',function(data){
		showPopup('qryPopup1','qryOrderInfos');
		$.endPageLoading();
	},function(error_code,error_info,derror){
		$.endPageLoading();
		showDetailErrorInfo(error_code,error_info,derror);
	});
}