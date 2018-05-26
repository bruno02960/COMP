.class public static aval7
.super java/lang/Object




.method public static Count(I)I
.limit locals 255
.limit stack 20
iconst_0
istore_1
bipush -1
istore_2
while_Count_init1:
iload_2
bipush 32
if_icmpge while_Count_end2
iload_0
iconst_1
iand
istore_3
iload_3
iconst_1
if_icmpeq true_Count3
goto if_Count_end4
true_Count3:
iinc 1 1
if_Count_end4:
iload_0
iconst_1
ishr
istore_0
iinc 2 1
goto while_Count_init1
while_Count_end2:
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_3
invokestatic Count(I)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
