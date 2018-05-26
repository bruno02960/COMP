.class public static array2
.super java/lang/Object




.method public static sum_array([I)I
.limit locals 255
.limit stack 20
iconst_0
istore_1
iconst_0
istore_2
while_sum_array_init1:
iload_1
aload_0
arraylength
if_icmpge while_sum_array_end2
iload_2
aload_0
iload_1
iaload
iadd
istore_2
iinc 1 1
goto while_sum_array_init1
while_sum_array_end2:
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush 16
istore_1
iload_1
newarray int
astore_2
iconst_0
istore_3
while_main_init3:
iload_3
iload_1
if_icmpge while_main_end4
aload_2
iload_3
iconst_1
iastore
iinc 3 1
goto while_main_init3
while_main_end4:
aload_2
invokestatic array2/sum_array([I)I
istore 4
ldc "sum of array elements = "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
