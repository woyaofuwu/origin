
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
								<button type="button" class="e_button-blue" name="showAddrPopup"><span class="e_ico-check"></span><span>标准地址查询</span></button>
								<button type="button" class="e_button-green" name="showTreeAddrPopup"><span class="e_ico-check"></span><span>树状地址查询</span></button>
							</span>	
									<input type="hidden" id="STAND_ADDRESS_CODE_{{uniqueId}}" name="STAND_ADDRESS_CODE" value=""  desc="标准地址CODE"/>
									<input type="hidden" id="DEVICE_ID_{{uniqueId}}" name="DEVICE_ID" value="" nullable="no" desc="设备ID"/>
									<input type="hidden" id="OPEN_TYPE_{{uniqueId}}" name="OPEN_TYPE" value="" nullable="yes" desc="宽带开户类型"/>
									<!-- 新增获取预约宽带订单号隐藏 -->
									<input type="hidden" name="BOOK_ID" id="BOOK_ID" value=""/>																				
						</div>
				</li>

				<li name="wideAreaCode" class="required">
					<div class="label">地区：</div>
						<div class="value">
							<span id="mywideAreaCode_{{uniqueId}}" name="mywideAreaCode_{{uniqueId}}" desc="地区">
							</span>
					
					</div>
				</li>

				<li name="detailAddress" class="required merge-2">
					<div class="label">报装地址：</div>
						<div class="value">
							<input type="text" name="DETAIL_ADDRESS" id="DETAIL_ADDRESS_{{uniqueId}}" value=""/>
					</div>
				</li>
				
				<li name="floorNum" class="required">
					<div class="label">楼层和房号：</div>
						<div class="value">
							<input type="text" name="FLOOR_AND_ROOM_NUM" id="FLOOR_AND_ROOM_NUM_{{uniqueId}}" value=""/>
							<!--楼层和房号   标识  -->
						    <input type="hidden" name="FLOOR_AND_ROOM_NUM_FLAG" id="FLOOR_AND_ROOM_NUM_FLAG_{{uniqueId}}" value="0"/>							
					</div>
				</li>

				<li name="wideProductType" class="required">
					<div class="label">宽带产品类型：</div>
						<div class="value">
							<span id="mySelectWideProductType_{{uniqueId}}" name="mySelectWideProductType_{{uniqueId}}" desc="宽带产品类型">
							</span>
					
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
					<div class="label">宽带账号：</div>
						<div class="value">
						<span class="e_mix">
							<input type="text" name="WIDE_ACCT_ID" value="{{wideAcctId}}" disabled="true"/>
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

				<li name="widePayMode" class="required">
					<div class="label">支付模式：</div>
						<div class="value">
							<span id="mySelectWidePayMode_{{uniqueId}}" name="mySelectWidePayMode_{{uniqueId}}" desc="支付模式">
							</span>
					
					</div>
				</li>
										
				<li name="contactphone" class="required">
					<div class="label">联系人电话：</div>
						<div class="value">
							<input type="text" name="CONTACT_PHONE" value="{{sn}}" nullable="no"  maxlength="11" datatype="numeric" desc="联系人电话（满意度评测人）"/>
					</div>
				</li>

				<li name="contact" class="required">
					<div class="label">联系人：</div>
						<div class="value">
							<input type="text" name="CONTACT" value=""/>
					</div>
				</li>
				
				<li name="phone" class="required">
					<div class="label">联系电话：</div>
						<div class="value">
							<input type="text" name="PHONE" value="" nullable="no"  maxlength="11" datatype="numeric" desc="客户现场施工联系人"/>
					</div>
				</li>
				
				<li name="wideUserStyle" class="required">
					<div class="label">开户方式：</div>
						<div class="value">
							<span id="myWideUserOpenStyle_{{uniqueId}}" name="myWideUserOpenStyle_{{uniqueId}}" desc="开户方式">
							</span>
					
					</div>
				</li>
											
				<li name="predate">
					<div class="label">预约施工时间：</div>
						<div class="value">
						<span class="e_mix e_group">
							<input type="text" name="SUGGEST_DATE" id="SUGGEST_DATE_{{uniqueId}}" disabled="true">
							<span class="e_ico-date"></span>
							<input type="hidden" id="SUGGEST_DATE_MAX" name="SUGGEST_DATE_MAX" value=""/>
							<input type="hidden" id="SUGGEST_DATE_MIN" name="SUGGEST_DATE_MIN" value=""/>
													
						</span>
					</div>
				</li>

				<li name="modemDiv"  id="modemDiv_{{uniqueId}}">
					<div class="label">调测费活动：</div>
						<div class="value">
							<span id="mySelectModelActive_{{uniqueId}}" name="mySelectModelActive_{{uniqueId}}" desc="调测费活动">
							</span>
					</div>
					<input type="hidden" name="SALE_ACTIVE_FEE2" id="SALE_ACTIVE_FEE2_{{uniqueId}}" value=""/>
					<input type="hidden" name="MODEM_STYLE" id="MODEM_STYLE_{{uniqueId}}" value=""/>
					<input type="hidden" name="MODEM_DEPOSIT" id="MODEM_DEPOSIT_{{uniqueId}}" value="0"/>
					<input type="hidden" name="IS_NEED_MODEM" id="IS_NEED_MODEM_{{uniqueId}}" value="1"/>															
				</li>
				
				<li name="modelSaleExplain"  id="modelSaleExplain_{{uniqueId}}" >
					<div class="label">调测费活动详细描述：</div>
						<div class="value">
							<input type="text" id="SALE_ACTIVE_EXPLAIN2_{{uniqueId}}" name="SALE_ACTIVE_EXPLAIN2" value="" disabled="true"/>
					</div>
				</li>
			</ul>
		</div>
		</div> 
		<div partId="SelectedOffers"  name ="SelectedOffers" ></div>


		<input type="hidden" name="NEW_PRODUCT_START_DATE" id="NEW_PRODUCT_START_DATE" value=""/>
		<input type="hidden" name="IS_BUSINESS_WIDE" id="IS_BUSINESS_WIDE" value=""/>
		<input type="hidden" name="DEVICE_ID_HIGHT" id="DEVICE_ID_HIGHT" value="0"/>

		<input type="hidden" name="ROLE_TYPE" value="{{roletype}}"/>
		<input type="hidden" name="TRADE_EPARCHY_CODE" value=""/>
		<input type="hidden" name="USER_IMS_NUM" value=""/>
		<input type="hidden" name="USER_IMS_USER_ID" value=""/>
		<input type="hidden" name="IS_HAS_IMS" value=""/>
		<input type="hidden" name="IS_HAS_TV" value=""/>

	</div>
