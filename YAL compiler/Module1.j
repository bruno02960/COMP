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
.var 0 is var1 I  from Label0 to Label1
Label0:
ldc 0
Label1:
.var 1 is var2 I  from Label2 to Label3
Label2:
ldc 10
Label3:
.var 2 is var3 I  from Label4 to Label5
Label4:
ldc 20000
Label5:
ldc 100
ldc 200
iadd
return
.end method
