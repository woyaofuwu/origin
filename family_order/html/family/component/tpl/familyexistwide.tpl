
	<div class="c_box c_box-border">
	<div class="c_title">
		<div class="text">{{title}}宽带</div>
		<div class="fn" name="titleFn">
					<%- template.render(btnsArt, {btnStrs:btnStrs,isLtIE9:isLtIE9}) %>
		</div>
	</div>

	<div class="l_padding-2 l_padding-u">
		<div class="c_form c_form-col-3 c_form-label-9">
			<ul class="ul">
			
				<li class="required merge-2">
						<div class="label">标准地址： </div>
							<div class="value">
							<span class="e_mix">
								<input type="text" name="STAND_ADDRESS" id="STAND_ADDRESS_{{uniqueId}}" value="" disabled="true"/>
							</span>	
									<input type="hidden" id="STAND_ADDRESS_CODE_{{uniqueId}}" name="STAND_ADDRESS_CODE" value=""  desc="标准地址CODE"/>
									<input type="hidden" id="DEVICE_ID_{{uniqueId}}" name="DEVICE_ID" value="" nullable="no" desc="设备ID"/>
									<input type="hidden" id="OPEN_TYPE_{{uniqueId}}" name="OPEN_TYPE" value="" nullable="yes" desc="宽带开户类型"/>
						</div>
				</li>
				
				<li class="required">
					<div class="label">宽带账号：</div>
						<div class="value">
						<span class="e_mix">
							<input type="text" name="WIDE_ACCT_ID" value="{{wideAcctId}}" disabled="true"/>
						</span>
					</div>
				</li>


														
				<li name="userproduct" class="required merge-2" >
					<div class="label">现有商品：</div>
						<div class="value">
						<span class="e_mix">
							<input type="text" name="OFFER_NAME" value="" disabled="true"/>
						</span>
					</div>
				</li>
				
				<li name="userproductType" class="required" >
					<div class="label">宽带类型：</div>
						<div class="value">
							<input type="text" name="USER_WIDE_PRODUCT_TYPE_NAME" value="" disabled="true"/>
					</div>
				</li>	

				<li name="chooseproduct" class="required">
					<div class="label">商品：</div>
					<div class="value">						
						<span class="e_group">
							<span class="e_groupRight">
								<span class="e_ico-browse e_blue" tip="选择" name="selectOffer"></span>
							</span> <span class="e_groupMain">
								<input type="text" name="ORDER_DISCNT_CODE" nullable="no" disabled="true"/>
							</span>
						</span>						
					</div>
				</li>

				<li class="required">
					<div class="label">服务号码：</div>
						<div class="value">
						<span class="e_mix">
							<input type="text" name="SERIAL_NUMBER" value="{{sn}}" disabled="true"/>
						</span>
					</div>
				</li>

						
				<li name="detailAddress" class="required" style="display: none;">
					<div class="label">报装地址：</div>
						<div class="value">
							<input type="text" name="DETAIL_ADDRESS" id="DETAIL_ADDRESS_{{uniqueId}}" disabled="true"   value=""/>
					</div>
				</li>
							
			</ul>
		</div>
		</div> 
		<div partId="SelectedOffers"  name ="SelectedOffers" ></div>

		<input type="hidden" name="ROLE_TYPE" value="{{roletype}}"/>
		<input type="hidden" name="USER_EPARCHY_CODE" value=""/>
		<input type="hidden" name="TRADE_EPARCHY_CODE" value=""/>
		<input type="hidden" name="USER_WIDE_PRODUCT_ID" value=""/>
		<input type="hidden" name="USER_DISCNT_CODE" value=""/>
		<input type="hidden" name="USER_WIDE_USER_ID" value=""/>
		<input type="hidden" name="USER_WIDE_PRODUCT_TYPE" value=""/>
		<input type="hidden" name="USER_WIDE_PRODUCT_MODE" value=""/>
		<input type="hidden" name="USER_IMS_NUM" value=""/>
		<input type="hidden" name="USER_IMS_USER_ID" value=""/>
		<input type="hidden" name="IS_HAS_IMS" value=""/>
		<input type="hidden" name="IS_HAS_TV" value=""/>
		<input type="hidden" name="USER_WIDE_AREA_CODE" value=""/>

	</div>
