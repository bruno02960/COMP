.class public static array2
.super java/lang/Object




.method public static sum_array()I
.limit locals 255
.limit stack 20
iconst_0
astore_1
iconst_0
astore_2
while_sum_array_init2:
aload_0
arraylength
iload_1
 while_sum_array_end3
goto while_sum_array_end3
iload_2
iload_0
iadd
istore_2
iload_1
iconst_1
iadd
istore_1
goto while_sum_array_init2
while_sum_array_end3:
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc 16
astore_1
iload_1
newarray int
astore_2
iconst_0
astore_3
while_main_init4:
iload_1
iload_3
if_icmplt while_main_end5
goto while_main_end5
iconst_1
aload_2
iload_3
iastore
iload_3
iconst_1
iadd
istore_3
goto while_main_init4
while_main_end5:
aload_2
invokestatic array2/sum_array(I)I
istore 4
ldc "sum of array elements = "
aload 4
invokestatic io/println(Ljava/lang/String;I)VI

pop
return
.end method
