select t.product_order_id,t.product_offer_id
  FROM tf_f_user_grp_merchp t
    WHERE 1=1
    AND t.GROUP_ID = :GROUP_ID
    AND t.MERCH_SPEC_CODE=:MERCH_SPEC_CODE
    AND t.PRODUCT_SPEC_CODE=:PRODUCT_SPEC_CODE
    AND t.USER_ID=:USER_ID
    AND SYSDATE > t.START_DATE AND SYSDATE<t.END_DATE
