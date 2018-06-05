.class public static aval7
.super java/lang/Object




.method public static Count(I)I
.limit locals 255
.limit stack 2
iconst_0
istore_3
iconst_m1
istore_2
iload_2
bipush 32
if_icmpge while_end1
while_init1:
iload_0
iconst_1
iand
istore_1
iload_1
iconst_1
if_icmpne if_end2
iinc 3 1
if_end2:
iload_0
iconst_1
ishr
istore_0
iinc 2 1
iload_2
bipush 32
if_icmplt while_init1
while_end1:
iload_3
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
iconst_3
invokestatic aval7/Count(I)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
