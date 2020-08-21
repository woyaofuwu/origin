SELECT COUNT(1) recordcount
FROM tf_r_valuecard@DBLNK_BOSSCEN
WHERE value_card_no = :VALUE_CARD_NO
AND sale_tag='0'