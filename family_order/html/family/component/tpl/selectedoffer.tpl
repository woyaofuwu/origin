<div class="l_padding-2 l_padding-u" id="boxA" style="">
	<div class="c_list c_list-col-3 c_list-s" name="SelectedOfferList">
		<ul>
			<%for(var i=0,offer;offer=offerList[i++];){ %>
			<li class="link" title={{offer.get("DESCRIPTION")}}>
				<div class="main">
					<div class="title">{{"["+offer.get("ELEMENT_ID")+"]"+offer.get("ELEMENT_NAME")}}</div>
					<div class="content">{{offer.get("START_DATE")}}~{{offer.get("END_DATE")}}</div>
				</div> 
				<%if(offer.get("ATTR_PARAM") != null && offer.get("ATTR_PARAM").length > 0) {%>
				<div class="fn">
					<span class="e_ico-unfold" elementId={{offer.get(
						"ELEMENT_ID")}} elementType={{offer.get(
						"ELEMENT_TYPE_CODE")}} itemIndex={{i-1}}></span>
				</div> 
				<%}%>
			</li> 
			<%}%>
		</ul>
	</div>
</div>