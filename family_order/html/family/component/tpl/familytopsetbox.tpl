<div class="c_box c_box-border" id="TOP_SET_BOX_AREA_ID_{{uniqueId}}">
	<div class="c_title">
		<div class="text">{{title}}信息</div>
		<div class="fn" name="titleFn">
			<%- template.render(btnsArt, {btnStrs:btnStrs,isLtIE9:false}) %>
		</div>
	</div>

	<div class="l_padding-2 l_padding-u new_topsetbox" name="NEW_TOP_SET_BOX">
		<div class="c_form c_form-col-3 c_form-label-9">
			<ul>
				<li class="link required">
					<div class="label">魔百和产品：</div>
					<div class="value">
						<span id="selectTvProductTypeCon_{{uniqueId}}" desc="魔百和产品类型"></span>
					</div>
				</li>
				<li class="link required" jwcid="bPackagePart@Part">
					<div class="label">必选基础包：</div>
					<div class="value">
						<span id="selectTvBasePackageCon_{{uniqueId}}" desc="魔百和必选基础包"></span>
					</div>
				</li>
				<li class="link">
					<div class="label">可选优惠包：</div>
					<div class="value">
						<span id="selectTvOptionPackageCon_{{uniqueId}}" desc="魔百和可选优惠包"></span>
					</div>
				</li>
				<li class="link required">
					<div class="label">必选优惠包：</div>
					<div class="value">
						<span id="selectTvPlatsvcPackagesCon_{{uniqueId}}" desc="魔百和必选优惠包"></span>
					</div>
				</li>
				<li class="required">
					<div class="label">优惠选择：</div>
					<div class="value">
						<span class="e_group">
							<span class="e_groupRight" >
								<span class="e_ico-browse e_blue" tip="选择"  name="selectOffer"></span>
							</span> <span class="e_groupMain">
								<input type="text" name="ORDER_DISCNT_CODE" nullable="no" disabled="true" />
							</span>
						</span>
					</div>
				</li>
			</ul>

			<ul>
				<li class="link">
					<div class="label">魔百和调测费活动：</div>
					<div class="value">
						<span id="selectTvSaleActivesCon_{{uniqueId}}" desc="魔百和调测费活动"></span>
					</div>
					<input type="Hidden" name="TOP_SET_BOX_SALE_ACTIVE_FEE2_{{uniqueId}}" id="TOP_SET_BOX_SALE_ACTIVE_FEE2_{{uniqueId}}" value="" />
				</li>
				<li class="li merge-2" >
					<div class="label">调测费活动详细描述：</div>
					<div class="value">
						<textarea name="TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_{{uniqueId}}"
							id="TOP_SET_BOX_SALE_ACTIVE_EXPLAIN2_{{uniqueId}}" jwcid="@TextArea" class=""
							readOnly="readonly" desc="魔百和调测费活动详细描述"></textarea>
					</div>
				</li>
			</ul>
		</div>

		<!-- 存放选中的魔百和调测费活动信息 -->
		<input type="Hidden" name="MO_SALEACTIVE_LIST2_{{uniqueId}}" id="MO_SALEACTIVE_LIST2_{{uniqueId}}" value="" />
		<!-- 存放选中的魔百和调测费活动产品信息 -->
		<input type="Hidden" name="MO_PRODUCT_ID2_{{uniqueId}}" id="MO_PRODUCT_ID2_{{uniqueId}}" value="" />
		<!-- 存放选中的魔百和调测费活动包信息 -->
		<input type="Hidden" name="MO_PACKAGE_ID2_{{uniqueId}}" id="MO_PACKAGE_ID2_{{uniqueId}}" value="" />
	</div>

	<div class="l_padding-2 l_padding-u old_topsetbox" name="OLD_TOP_SET_BOX">
		<div class="c_form c_form-col-3 c_form-label-9">
			<ul>
				<li class="required">
					<div class="label">魔百和产品：</div>
					<div class="value">
						<span id="selectOldTvProductTypeCon_{{uniqueId}}" desc="魔百和产品类型"></span>
					</div>
				</li>
				<li class="required merge-2">
					<div class="label">必选基础包：</div>
					<div class="value">
						<input type="hidden" id="OLD_TOP_SET_BOX_BASE_PACKAGES_{{uniqueId}}" value="" />
						<input type="text" id="OLD_TOP_SET_BOX_BASE_PACKAGES_NAME_{{uniqueId}}" value="" readonly="readonly"/>
					</div>
				</li>
				<li class="required">
					<div class="label">优惠选择：</div>
					<div class="value">
						<span class="e_group">
							<span class="e_groupRight" >
								<span class="e_ico-browse e_blue" tip="选择"  name="selectOffer"></span>
							</span> <span class="e_groupMain">
								<input type="text" name="ORDER_DISCNT_CODE" nullable="no" disabled="true" />
							</span>
						</span>
					</div>
				</li>
				<li class="required merge-2">
					<div class="label">终端编码：</div>
					<div class="value">
						<input type="text" id="OLD_TOP_SET_BOX_RES_NO_{{uniqueId}}" value="" readonly="readonly"/>
					</div>
				</li>
			</ul>

		</div>
	</div>

	<div class="c_title" style="display:none">
		<div class="text">资费信息</div>
	</div>
	<div class="l_padding-2 l_padding-u" style="display:none">
		<div class="c_list c_list-s c_list-col-3 c_list-line c_list-phone-col-1 c_list-border">
			<ul>
				<li class="link required">
					<div class="label">魔百和调测费活动费用：</div>
					<div class="value">
						<span class="e_mix">
							<input type="text" name="SALE_ACTIVE_FEE2_{{uniqueId}}" id="SALE_ACTIVE_FEE2_{{uniqueId}}" value="0.00" readonly="readonly" />
							<span class="e_label">元</span>
						</span>
					</div>
				</li>
			</ul>
		</div>
	</div>
	<div partId="SelectedOffers"  name ="SelectedOffers" ></div>
</div>
