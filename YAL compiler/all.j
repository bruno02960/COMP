.class public static all
.super java/lang/Object


.field public static a I = 0
.field public static b I = 1


.method public static funcAbove(II)I
.limit locals 255
.limit stack 20
ldc ""arg1 = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""arg2 = ""
invokestatic io/println(Ljava/lang/String;)V
iload 0
iload 0
iadd
istore 3
ldc ""ret in funcAbove = ""
invokestatic io/println(Ljava/lang/String;)V
ldc 0
istore 3
iload_3
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc ""a = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""b = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""1 = ""
ldc 1
invokestatic io/println(Ljava/lang/String;I)V
getfield all/a I
getfield all/a I
iadd
istore 0
getfield all/a I
getfield all/a I
isub
istore 1
getfield all/a I
getfield all/a I
imul
istore 2
getfield all/a I
getfield all/a I
idiv
istore 3
ldc ""sum = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""dif = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""mul = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""div = ""
invokestatic io/println(Ljava/lang/String;)V
invokestatic io/println()V
getfield all/a I
getfield all/a I
iadd
istore 4
ldc ""c = ""
invokestatic io/println(Ljava/lang/String;)V
invokestatic io/println()V
invokestatic funcAbove()I
istore_-1
invokestatic funcBelow()I
istore_-1
ldc ""funcAbove of b c = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""funcBelow of b c = ""
invokestatic io/println(Ljava/lang/String;)V
ldc 0
istore 0
ldc 0
istore 1
ldc 0
istore 2
ldc 0
istore 3
ldc 0
istore 4
return
.end method


.method public static funcBelow(II)I
.limit locals 255
.limit stack 20
ldc ""arg1 = ""
invokestatic io/println(Ljava/lang/String;)V
ldc ""arg2 = ""
invokestatic io/println(Ljava/lang/String;)V
iload 0
iload 0
iadd
istore 3
ldc ""ret in funcBelow = ""
invokestatic io/println(Ljava/lang/String;)V
ldc 0
istore 3
iload_3
.end method
