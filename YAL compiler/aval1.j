.class public static aval1
.super java/lang/Object




.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_2
iconst_3
invokestatic aval1/f(II)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method


.method public static f(II)I
.limit locals 255
.limit stack 20
iload_0
iload_1
imul
istore_2
iload_2
ireturn
.end method
