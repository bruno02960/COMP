.class public static aval5
.super java/lang/Object

.method public static f(II)I
.limit locals 255
.limit stack 20
bipush 10
istore_2
iload_0
iload_1
if_icmpeq true_f1
iload_1
iload_0
iadd
istore_1
goto if_f_end2
true_f1:
while_f_init3:
iload_0
iload_2
if_icmpge while_f_end4
iinc 0 1
goto while_f_init3
while_f_end4:
iload_0
iconst_2
ishl
istore_1
if_f_end2:
iload_1
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_4
iconst_5
invokestatic f(II)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_2
iconst_2
invokestatic f(II)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
