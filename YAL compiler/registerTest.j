.class public static registerTest
.super java/lang/Object

.method public static f(I)I
.limit locals 255
.limit stack 20
iload_0
iconst_1
iadd
istore_1
iload_1
iconst_2
iadd
istore_2
iload_2
iconst_3
iadd
istore_3
iload_3
iconst_1
iadd
istore 4
iload 4
ireturn
.end method

.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_1
invokestatic f(I)I
istore_1
iload_1
invokestatic io/println(I)V
return
.end method
