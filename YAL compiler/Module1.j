.class public static Module1
.super java/lang/Object


.field private static a
.field private static b I 
.field private static c I = 12
.field private static d I = 12345


.method public static method1(III)V
<<<<<<< HEAD
.limit locals 255
.limit stack 0
ldc 0
istore 3
iload 3
iload 3
imul
istore 3
return
.end method


.method public static method2(III)V
.limit locals 255
iload 1
iload 2
imul
istore 0
=======
.limit locals 3
return
.end method


.method public static method2(III)V
.limit locals 4
.limit stack 20
ldc 50
istore 3
>>>>>>> branch 'master' of https://github.com/bruno02960/COMP
return
.end method


.method public static main([Ljava/lang/String;)V
<<<<<<< HEAD
.limit locals 255
=======
.limit locals 3
.limit stack 20
ldc 0
istore 0
ldc 10
istore 1
ldc 20000
istore 2
ldc 100
ldc 200
iadd
istore -1
>>>>>>> branch 'master' of https://github.com/bruno02960/COMP
return
ldc 0
istore 3
.end method
