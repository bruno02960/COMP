.class public static testGlobals
.super java/lang/Object


.field public static a I = 1
.field public static b I = 1000
.field public static c I = 0


.method public static foo(I)I
.limit locals 255
.limit stack 20
ldc 1234
istore 1
iload_1
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 20
ldc 12
istore 1
ldc 44
istore 2
getstatic testGlobals/b I
invokestatic testGlobals/foo(I)I
putstatic testGlobals/a I
iconst_3
putstatic testGlobals/c I
iconst_4
putstatic testGlobals/c I
ldc "a = "
getstatic testGlobals/a I
invokestatic io/println(Ljava/lang/String;I)I

pop
ldc "b = "
getstatic testGlobals/b I
invokestatic io/println(Ljava/lang/String;I)I

pop
ldc "c = "
getstatic testGlobals/c I
invokestatic io/println(Ljava/lang/String;I)I

pop
return
.end method
