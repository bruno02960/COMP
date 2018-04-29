.class public static programa1
.super java/lang/Object


.field private static data
.field private static mx I = -1
.field private static mn I = -1


.method public static det()V
.limit locals 255
.limit stack 20
ldc 0
istore 1
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
invokestatic det()V
ldc "max: "
invokestatic io/println(Ljava/lang/String;)I
ldc "min: "
invokestatic io/println(Ljava/lang/String;)I
return
.end method
