.class public static Module1
.super java/lang/Object


.field public static a I = 0
.field public static b I = 0
.field public static c I = 12
.field public static d I = 12345


.method public static method1(III)V
.limit locals 255
.limit stack 20
ldc 20
istore 3
ldc 50
istore 4
iload 4
istore 3
ldc 30
istore 3
getstatic Module1/a I
istore 4
return
.end method


.method public static method2(III)V
.limit locals 255
.limit stack 20
iload 1
iload 2
imul
istore 0
ldc "var1 = "
ldc 2
invokestatic io/println(Ljava/lang/String;I)I
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
return
.end method
