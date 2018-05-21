.class public static testLocalArrays
.super java/lang/Object


.field public static a I = 1


.method public static f1()I
.limit locals 255
.limit stack 20
ldc "a = "
getstatic testLocalArrays/a I
invokestatic io/println(Ljava/lang/String;I)I

pop
ldc 100
istore 0
iload_0
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
iconst_2
putstatic testLocalArrays/a I
invokestatic testLocalArrays/f1()I

pop
return
.end method
