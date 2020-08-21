<div id='{{id}}' class='c_popupItem' roleCode='{{ roleCode }}'>
	<div class='c_header'>
		<div class='back' name="backofferList" handle="back" >{{ roleName }}</div>
	</div>
	<div class='c_scroll c_list-phone-col-1 c_scroll-float c_scroll-header c_scroll-submit'>
			<% for(var item in offerTypes) { %>
			
			<%  
				var offerType = item;
				var offers = offerTypes[item];
				var title = offerType=="P"?"产品": offerType=="S"?"服务": offerType=="D" ? "优惠" : offerType=="Z" ? "平台服务" : offerType=="K" ? "营销活动" : "其他";
			 %>
			<div class="c_box">
				<div class="c_title">
					<div class="text">{{ title }}</div>
					<div class="fn">
						<ul>
							<li name="foldOffersName"  handle="foldOffers">更多</li>
						</ul>
					</div>
				</div>
	
				<div class='c_list c_list-s c_list-phone-col-1'>
					<ul offerType="{{offerType}}">
						<%for(var i=0,offer;offer=offers[i++];){ %>
						<%
						var groupId = offer.get("GROUP_ID");
						var offerCode = offer.get("OFFER_CODE");
						var offerType = offer.get("OFFER_TYPE");
						var disabled=offer.get("SELECT_FLAG") == "0" ? "disabled=true " : "" ;
						var required = offer.get("SELECT_FLAG") == "0" ? "class='required' " : "";
						var checked = offer.get("SELECT_FLAG") != "2" ? "checked " : "";
						var style = i >5 ? "display:none":"";
						var offerDesc = offer.get("DESCRIPTION");
						var inputType= (offerType == "P"|| (roleCode =="9"&&offerType == "D"))? "radio" : "checkbox";
						var orderMode = offer.get("ORDER_MODE") == "R" ? "orderMode=R " : "";
						%>
						<li {{ required }} title='{{ offerDesc }}' style={{style}} >
							<div class='fn'>
								<input type='{{inputType}}' name='{{ roleCode + offerType }}' groupId='{{ groupId }}'
									roleCode='{{ roleCode }}' offerCode='{{ offerCode }}'
									offerType='{{ offerType }}' {{ checked }} {{disabled}} {{orderMode}} />
							</div>
							<div class='main'>【{{offerCode}}】{{ offer.get("OFFER_NAME") }}</div>
						</li>
						<%}%>
					</ul>
				</div>
			</div>
			<div class="c_space"></div>
			<% } %>
	</div>
	<div class="l_bottom">
		<div class='c_submit c_submit-full'>
			<button type="button" name="cancel" class="e_button-l e_button-navy"  handle="back" >取消</button>
			<button type='button' name="ok" class='e_button-l e_button-r e_button-blue' handle="ok">确定</button>
		</div>
	</div>
</div>

