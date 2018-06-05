.class public static constantRanges
.super java/lang/Object




.method public static f()V
.limit locals 255
.limit stack 1
iconst_5
istore 6
iconst_m1
istore 5
bipush 6
istore 4
sipush 128
istore_3
sipush -129
istore_2
ldc 32768
istore_1
ldc -32769
istore_0
iload 6
invokestatic io/println(I)V
iload 5
invokestatic io/println(I)V
iload 4
invokestatic io/println(I)V
iload_3
invokestatic io/println(I)V
iload_2
invokestatic io/println(I)V
iload_1
invokestatic io/println(I)V
iload_0
invokestatic io/println(I)V
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 0
invokestatic constantRanges/f()V
return
.end method
