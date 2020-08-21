SELECT COUNT(1) recordcount
FROM tf_r_pwdsimcard WHERE serial_number=:SERIAL_NUMBER AND product_id_a>0
AND product_id_a!=TO_NUMBER(:PRODUCT_ID)