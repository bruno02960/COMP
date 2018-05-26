.class public static aval2
.super java/lang/Object




.method public static f(II)I
.limit locals 255
.limit stack 20
iconst_0
istore_2
iload_0
iload_1
if_icmpeq true_f1
goto if_f_end2
true_f1:
iconst_2
istore_2
if_f_end2:
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_2
bipush 12
invokestatic aval2/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_4
iconst_2
invokestatic aval2/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
iconst_3
istore_1
iconst_4
iconst_2
invokestatic aval2/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
