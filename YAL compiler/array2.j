.class public static array2
.super java/lang/Object




.method public static sum_array([I)I
.limit locals 255
.limit stack 3
iconst_0
istore_2
iconst_0
istore_1
iconst_0
aload_0
arraylength
if_icmpge while_end1
while_init1:
iload_1
aload_0
iload_2
iaload
iadd
istore_1
iinc 2 1
iload_2
aload_0
arraylength
if_icmplt while_init1
while_end1:
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 3
bipush 16
istore_1
bipush 16
newarray int
astore_2
iconst_0
istore_0
iconst_0
bipush 16
if_icmpge while_end2
while_init2:
aload_2
iload_0
iconst_1
iastore
iinc 0 1
iload_0
iload_1
if_icmplt while_init2
while_end2:
aload_2
invokestatic array2/sum_array([I)I
istore_1
ldc "sum of array elements = "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
return
.end method
