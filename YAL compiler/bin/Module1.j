.class public static Module1
.super java/lang/Object


.field private static a I 
.field private static b I 
.field private static c I = 12
.field private static d I = 12345


.method public static method1()V
.limit locals 0
return
.end method


.method public static method2(I)V
.limit locals 1
return
.end method


.method public static method3(III)V
.limit locals 3
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 3
.limit stack 20
bipush 0
istore 0
bipush 10
istore 1
bipush 20000
istore 2
ldc 100
ldc 200
iadd
return
.end method
