.class public static stackSize
.super java/lang/Object




.method public static f(I)V
.limit locals 255
.limit stack 5
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
if_icmple if_false1
ldc "Greater"
invokestatic io/println(Ljava/lang/String;)V
goto if_end1
if_false1:
ldc "Not greater"
invokestatic io/println(Ljava/lang/String;)V
if_end1:
return
.end method


.method public static g(I)I
.limit locals 255
.limit stack 5
iload_0
iload_0
iload_0
iload_0
iload_0
invokestatic stackSize/h(IIII)I
imul
istore_1
iload_1
ireturn
.end method


.method public static h(IIII)I
.limit locals 255
.limit stack 2
iload_0
iload_1
iadd
istore 4
iload 4
iload_2
iadd
istore 4
iload 4
iload_3
iadd
istore 4
iload 4
ireturn
.end method


.method public static main([Ljava/lang/String;)V
.limit locals 255
.limit stack 1
iconst_m1
istore_0
iload_0
invokestatic stackSize/f(I)V
return
.end method
