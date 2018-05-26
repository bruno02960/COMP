.class public static nestedBranch
.super java/lang/Object

.method public static sign(I)I
.limit locals 255
.limit stack 20
iload_0
iflt true_sign1
iload_0
ifeq true_sign2
iconst_1
istore_1
goto if_sign_end3
true_sign2:
iconst_0
istore_1
if_sign_end3:
goto if_sign_end4
true_sign1:
iconst_m1
istore_1
if_sign_end4:
iload_1
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
bipush -10
istore_1
bipush 10
istore_2
iload_1
iload_2
iadd
istore_3
iload_1
invokestatic sign(I)I
istore 4
iload_3
invokestatic sign(I)I
istore 5
iload_2
invokestatic sign(I)I
istore 6
iload 4
invokestatic io/println(I)V
iload 5
invokestatic io/println(I)V
iload 6
invokestatic io/println(I)V
return
.end method
