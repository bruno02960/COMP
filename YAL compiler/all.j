.class public static all
.super java/lang/Object


.field public static a I = 0
.field public static b I = 1


.method public static funcAbove(II)I
.limit locals 255
.limit stack 20
ldc "arg1 = "
invokestatic io/println(Ljava/lang/String;)I
ldc "arg2 = "
invokestatic io/println(Ljava/lang/String;)I
ldc "ret in funcAbove = "
invokestatic io/println(Ljava/lang/String;)I
iload_-1
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc "a = "
invokestatic io/println(Ljava/lang/String;)I
ldc "b = "
invokestatic io/println(Ljava/lang/String;)I
ldc "1 = "
ldc 1
invokestatic io/println(Ljava/lang/String;I)I
ldc "sum = "
invokestatic io/println(Ljava/lang/String;)I
ldc "dif = "
invokestatic io/println(Ljava/lang/String;)I
ldc "mul = "
invokestatic io/println(Ljava/lang/String;)I
ldc "div = "
invokestatic io/println(Ljava/lang/String;)I
invokestatic io/println()I
ldc "c = "
invokestatic io/println(Ljava/lang/String;)I
invokestatic io/println()I
invokestatic funcAbove()I
istore_-1
invokestatic funcBelow()I
istore_-1
ldc "funcAbove of b c = "
invokestatic io/println(Ljava/lang/String;)I
ldc "funcBelow of b c = "
invokestatic io/println(Ljava/lang/String;)I
return
.end method


.method public static funcBelow(II)I
.limit locals 255
.limit stack 20
ldc "arg1 = "
invokestatic io/println(Ljava/lang/String;)I
ldc "arg2 = "
invokestatic io/println(Ljava/lang/String;)I
ldc "ret in funcBelow = "
invokestatic io/println(Ljava/lang/String;)I
iload_-1
.end method
