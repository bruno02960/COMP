.class public static all
.super java/lang/Object


.field public static a I = 0
.field public static b I = 1


.method public static funcAbove(II)I
.limit locals 255
.limit stack 20
ldc "arg1 = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
ldc "arg2 = "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
iload_0
iload_1
iadd
istore_2
ldc "ret in funcAbove = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
iload_2
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc "a = "
getstatic all/a I
invokestatic io/println(Ljava/lang/String;I)V
ldc "b = "
getstatic all/b I
invokestatic io/println(Ljava/lang/String;I)V
ldc "1 = "
iconst_1
invokestatic io/println(Ljava/lang/String;I)V
getstatic all/a I
getstatic all/b I
iadd
istore_0
getstatic all/a I
getstatic all/b I
isub
istore_1
getstatic all/a I
getstatic all/b I
imul
istore_2
getstatic all/a I
getstatic all/b I
idiv
istore_3
ldc "sum = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
ldc "dif = "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
ldc "mul = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
ldc "div = "
iload_3
invokestatic io/println(Ljava/lang/String;I)V
invokestatic io/println()V
getstatic all/a I
getstatic all/b I
iadd
istore 4
ldc "c = "
iload 4
invokestatic io/println(Ljava/lang/String;I)V
invokestatic io/println()V
getstatic all/b I
iload 4
invokestatic all/funcAbove(II)I
istore 5
getstatic all/b I
iload 4
invokestatic all/funcBelow(II)I
istore 6
ldc "funcAbove of b c = "
iload 5
invokestatic io/println(Ljava/lang/String;I)V
ldc "funcBelow of b c = "
iload 6
invokestatic io/println(Ljava/lang/String;I)V
return
.end method


.method public static funcBelow(II)I
.limit locals 255
.limit stack 20
ldc "arg1 = "
iload_0
invokestatic io/println(Ljava/lang/String;I)V
ldc "arg2 = "
iload_1
invokestatic io/println(Ljava/lang/String;I)V
iload_0
iload_1
iadd
istore_2
ldc "ret in funcBelow = "
iload_2
invokestatic io/println(Ljava/lang/String;I)V
iload_2
ireturn
.end method
