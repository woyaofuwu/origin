INSERT INTO tf_a_subadjustblog(adjust_id,detail_item_code,adjust_per,adjust_fee,adjust_time)
 VALUES(TO_NUMBER(:ADJUST_ID),:DETAIL_ITEM_CODE,TO_NUMBER(:ADJUST_PER),TO_NUMBER(:ADJUST_FEE),sysdate)