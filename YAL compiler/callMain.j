.class public static callMain
.super java/lang/Object


.field public static x I = 1


.method public static f()V
.limit locals 255
.limit stack 2
getstatic callMain/x I
ifle if_end1
getstatic callMain/x I
iconst_1
isub
putstatic callMain/x I
aconst_null
invokestatic callMain/main([Ljava/lang/String;)V
if_end1:
return
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
ldc "Call main"
invokestatic io/println(Ljava/lang/String;)V
invokestatic callMain/f()V
return
.end method
