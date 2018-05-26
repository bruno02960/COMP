.class public static callMain
.super java/lang/Object


.field public static x I = 1


.method public static f()V
.limit locals 255
.limit stack 20
getstatic callMain/x I
ifgt true_f1
goto if_f_end2
true_f1:
getstatic callMain/x I
iconst_1
isub
putstatic callMain/x I
aconst_null
invokestatic callMain/main([Ljava/lang/String;)V
if_f_end2:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc "Call main"
invokestatic io/println(Ljava/lang/String;)V
invokestatic callMain/f()V
return
.end method
